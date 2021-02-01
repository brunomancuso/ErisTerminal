package org.threewaves.eris.terminal.commands;

import java.util.List;

import org.threewaves.eris.engine.ICommand;
import org.threewaves.eris.engine.ICommandConsole;

//mutable and public command 
public class ClsCmd implements ICommand {
	public static final String NAME = "cls";
	private final Runnable run;

	public ClsCmd(Runnable run) {
		this.run = run;
	}
	
	@Override
	public String description() {
		return "Clear console";
	}

	@Override
	public String usage() {
		return NAME;
	}

	@Override
	public String name() {
		return NAME;
	}

	@Override
	public void exec(ICommandConsole console, List<String> arguments) {
		run.run();
	}

}
