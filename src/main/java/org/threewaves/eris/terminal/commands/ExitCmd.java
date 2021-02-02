package org.threewaves.eris.terminal.commands;

import java.util.List;

import org.threewaves.eris.engine.ICommand;
import org.threewaves.eris.engine.ICommandConsole;

class ExitCmd implements ICommand {

	private final Runnable exit;

	public ExitCmd(Runnable exit) {
		this.exit = exit;
	}

	@Override
	public String description() {
		return "exit terminal";
	}

	@Override
	public String usage() {
		return "exit";
	}

	@Override
	public String name() {
		return "exit";
	}

	@Override
	public void exec(ICommandConsole console, List<String> arguments) {
		exit.run();
	}
}
