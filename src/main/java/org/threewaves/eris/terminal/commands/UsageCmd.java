package org.threewaves.eris.terminal.commands;

import java.util.List;

import org.threewaves.eris.engine.ICommand;
import org.threewaves.eris.engine.ICommandConsole;

class UsageCmd implements ICommand {
	
	private final String usage;

	public UsageCmd(String usage) {
		this.usage = usage;
	}

	@Override
	public String description() {
		return "usage";
	}

	@Override
	public String usage() {
		return name();
	}

	@Override
	public String name() {
		return "usage";
	}

	@Override
	public void exec(ICommandConsole console, List<String> arguments) {
		console.notification(usage + ICommandConsole.NEWLINE);
	}
}
