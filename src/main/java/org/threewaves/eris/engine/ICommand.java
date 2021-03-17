package org.threewaves.eris.engine;

import java.util.List;

/**
 * Interface for creating command integrated in the terminal
 * @author Bruno Mancuso
 *
 */
public interface ICommand {
	
	/**
	 * 
	 * @return long description of the command
	 */
	String description();

	/**
	 * 
	 * @return name of the command
	 */
	String name();

	/**
	 * Get called when the comma
	 * @param console callback for output
	 * @param options of the command invocation
	 */
	void exec(ICommandConsole console, List<String> options);

	/**
	 * 
	 * @return usage of the command
	 */
	String usage();
}
