package org.threewaves.eris.engine;

/**
 * 
 * @author Bruno Mancuso
 *
 */
public interface ICommandConsole {
	/**
	 * Tab caracter
	 */
	public static String TAB = "\t";
	/**
	 * new line caracters
	 */
	public static String NEWLINE = "\r\n";

	/**
	 * @param str string to display
	 */
	void print(String str);

	/**
	 * @param str string to display
	 */
	void error(String str);

	/**
	 * @param str string to display
	 */
	void println(String str);

	/**
	 * @param str string to display
	 */
	void errorln(String str);

	/**
	 * @param exception to diplay
	 */

	void error(Throwable exception);

	/**
	 * @param str string to display
	 */

	void notification(String str);

	/**
	 * Disable or enable output
	 * @param enable or disable
	 */
	void state(boolean enable);

}