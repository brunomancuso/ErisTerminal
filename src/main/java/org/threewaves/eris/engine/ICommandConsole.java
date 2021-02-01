package org.threewaves.eris.engine;

public interface ICommandConsole {
	public static String TAB = "\t";
	public static String NEWLINE = "\r\n";

	void print(String str);
	void error(String str);
	void println(String str);
	void errorln(String str);
	void error(Throwable e);
	void notification(String str);
	void state(boolean enable);

}