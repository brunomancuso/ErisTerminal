package org.threewaves.eris.engine;

import java.util.List;

public interface ICommand {
	String description();
	String name();
	void exec(ICommandConsole console, List<String> options);
	String usage();
	
	static boolean isBackgroundOption(List<String> options) {
		return options.stream().filter(o -> o.equals("&") || o.equals("-bg")).findAny().isPresent();
	}

	static boolean isJUnitOption(List<String> options) {
		return options.stream().filter(o -> o.equals("junit")).findAny().isPresent();
	}
	static boolean isThrowExceptionOption(List<String> options) {
		return options.stream().filter(o -> o.equals("throw-exception")).findAny().isPresent();
	}

}
