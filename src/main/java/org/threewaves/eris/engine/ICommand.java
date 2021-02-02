package org.threewaves.eris.engine;

import java.util.List;

public interface ICommand {
	String description();

	String name();

	void exec(ICommandConsole console, List<String> options);

	String usage();
}
