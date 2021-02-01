package org.threewaves.eris.util;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Sql implements Closeable {
	public static final String RELATION_ASIGN = "=";
	public static final String RELATION_EQUAL = "=";
	public static final String RELATION_LESS = "<";
	public static final String RELATION_GREATER = ">";
	public static final String RELATION_LESS_EQUAL = "<=";
	public static final String RELATION_GREATER_EQUAL = ">=";

	private final boolean debug;

	private Connection db;
	private String validatingDbConnectionQuery = null;
	private boolean connected = false;
	private boolean autoCommit = false;

	public Sql(Connection db, boolean debug) {
		this.debug = debug;
		this.db = db;
		connected = db != null;
		this.validatingDbConnectionQuery = null;
	}

	public Sql(Connection db, String validatingDbConnectionQuery) {
		this(db);
		this.validatingDbConnectionQuery = validatingDbConnectionQuery;
	}
	
	public Sql(Connection db) {
		this.debug = false;
		this.db = db;
		connected = db != null;
	}
	
	public static Connection connectMandatory(String driver, String url
			, String user, String password) throws SQLException {
		return connectMandatory(driver, url, user, password, true);
	}
	
	public static Connection connectMandatory(String driver, String url
			, String user, String password, boolean debug) throws SQLException {
		try {
			return connectDB(driver, url, user, password, false, debug);
		} catch (SQLException e) {
			System.err.println("Connection refused: " + e);
			throw e;
		}
	}

	public static Connection connect(String driver, String url, String user, String password) {
		return connect(driver, url, user, password, 0);
	}

	public static Connection connect(String driver, String url, String user, String password, int retryConnection ) {
		return connect(driver, url, user, password, retryConnection, null);
	}

	public static Connection connect(String driver, String url, String user, String password, int retryConnection, String validatingDbConnectionQuery) {
		Connection conn = null;
		//Usamos do ya que si retryConnection es false debe intentar una vez realizar la conexion
		int retryCount = 0;
		do {
			try {
				retryCount++;
				conn = connectDB(driver, url, user, password, false, false);
			} catch (SQLException e) {
				System.err.println("Attempt: " + retryCount + ". Connection refused: " + e);
			}
			if (conn != null)
				break;
		} while (retryConnection > retryCount);
		return conn;
	}
	
	private static Connection connectDB(String driver, String url, String user
			, String password, boolean mandatory, boolean debug) throws SQLException {		
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			System.err.println("Driver not found:" + e);
			return null;
		}		
		Connection tmp = null;
		if (debug) {
			System.out.println("Trying to connect to " + url);
		}
		tmp = DriverManager.getConnection(url, user, password);
		if (debug) {
			System.out.println("Connected");
		}
		return tmp;
	}
	
	public boolean isConnected() {
		try {
			return connected && !db.isClosed();
		} catch (SQLException e) {
		}
		return false;
	}

	public boolean isValid() {
		if (!isConnected()) {
			return false;
		}
		if (validatingDbConnectionQuery == null) {
			return true;
		}
		try (Statement st = db.createStatement()) {
			String result = null;
			st.setMaxRows(1);
			try (ResultSet rs = st.executeQuery(validatingDbConnectionQuery)) {
				if (rs.next()) {
					result = rs.getString(1);
					if (debug) {
						System.out.println("The db connection has been validated. | " + validatingDbConnectionQuery + " result: " + result);
					}
				}			
				if (result == null) {
					if (debug) {
						System.err.println("Invalid db connection");
					}
					return false;
				}
				return true;
			}
		} catch (SQLException e) {
			if (debug) {
				e.printStackTrace();
			}
			return false;
		}
	}

	public boolean update(String update)  {
		try (Statement st = db.createStatement()) {
			if (debug) {
				System.out.println("UPDATE::" + update);
			}			
			st.executeUpdate(update);
			return true;
		} catch (SQLException e) {
			System.err.println("UPDATE::" + e);
			if (debug) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public Long insert(String insert) {
		return insert(insert, 0L);
	}
	
	public Long insert(String insert, Long newId) {
		try (Statement st = db.createStatement()) {
			if (debug) {
				System.out.println("INSERT::" + insert);
			}
			if (newId == null) {
				st.executeUpdate(insert, Statement.RETURN_GENERATED_KEYS);
			} else {
				st.executeUpdate(insert);
			}
			if (newId == null) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					newId = rs.getLong(1);
				}
			}
		} catch (SQLException e) {
			System.err.println("INSERT::" + e);
			if (debug) {
				e.printStackTrace();
			}
		}
		return newId;
	}
	
	public List<Object[]> select(int size, String select)  {
		return select(size, select, 0);
	}
	
	public List<Object[]> select(int size, String select, int maxSize)  {
		List<Object[]> tmp = new ArrayList < Object[] >();
		if (!isConnected()) {
			return tmp;
		}
		try (Statement st = db.createStatement()) {
			if (maxSize > 0) {
				st.setMaxRows(maxSize);
			}
			try (ResultSet rs = st.executeQuery(select)) {
				while (rs.next()) {
					if (size > 0) {
						Object[] row = new Object[size];
						for (int i = 0; i < row.length; i++) {					
							row[i] = rs.getObject(i + 1);
						}
						tmp.add(row);
					} else {
						if (debug) {
							System.err.println("SELECT::Row size is 0");
						}					
					}
				}			
				if (debug) {
					System.out.println("SELECT::" + select + " == " + tmp.size());
				}
			}
		} catch (SQLException e) {
			System.err.println("SELECT::" + select + " == " + e);
			if (debug) {
				e.printStackTrace();
			}
		}
		return tmp;
	}

	public void close() {
		if (db != null) {
			try {
				db.close();
			} catch (SQLException e) {
				System.err.println("Error closing: " + e);
			}
		}
		connected = false;
	}

	public PreparedStatement createPrepareStatement(String insert) throws SQLException {
		return db.prepareStatement(insert);
	}
	
	Connection getConnection() {
		return db;
	}

	public void startTransaction() throws SQLException {
		if (connected) {
			autoCommit = db.getAutoCommit();
			db.setAutoCommit(false);
		}
	}
	
	public void commit() throws SQLException {
		if (connected) {
			db.commit();
			db.setAutoCommit(autoCommit);
		}		
	}

	public void rollback() {
		try {
			if (connected) {
				db.rollback();
				db.setAutoCommit(autoCommit);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
