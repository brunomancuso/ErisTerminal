package org.threewaves.eris.engine;

import java.io.PrintStream;
import java.util.function.Consumer;

/**
 * 
 * @author Bruno Mancuso
 *
 */
public class CommandConsole implements ICommandConsole {
	private final PrintStream stdout;
	private final PrintStream stderr;
	private final PrintStream notificationStream;
	private Consumer<Boolean> onChanged;

	public CommandConsole(PrintStream newOut, PrintStream newErr, PrintStream notificationStream,
			Consumer<Boolean> onChanged) {
		stdout = System.out;
		stderr = System.err;
		this.onChanged = onChanged;
		System.setOut(newOut);
		System.setErr(newErr);
		this.notificationStream = notificationStream;
	}

	public CommandConsole install() {
		return this;
	}

	@Override
	public void print(String str) {
		System.out.print(str);
	}

	@Override
	public void println(String str) {
		System.out.println(str);
	}

	@Override
	public void error(String str) {
		System.err.print(str);
	}

	@Override
	public void errorln(String str) {
		System.err.println(str);
	}

	@Override
	public void error(Throwable e) {
		e.printStackTrace();
	}

	@Override
	public void notification(String str) {
		notificationStream.print(str);
	}

	@Override
	public void state(boolean enable) {
		if (onChanged != null) {
			onChanged.accept(enable);
		}
	}

	public void stdout(Object... args) {
		for (Object object : args) {
			stdout.print(object);
		}
		stdout.println();
	}

	public void stderr(Object... args) {
		for (Object object : args) {
			stderr.print(object);
		}
		stderr.println();
	}

}
