package org.threewaves.eris.terminal.commands;

import java.io.IOException;
import java.util.List;

import org.threewaves.eris.engine.ICommand;
import org.threewaves.eris.engine.ICommandConsole;
import org.threewaves.eris.util.ShellExec;

class RestartCmd implements ICommand {

	private final Runnable exit;

	public RestartCmd(Runnable exit) {
		this.exit = exit;
	}

	@Override
	public String description() {
		return "restart terminal";
	}

	@Override
	public String usage() {
		return "restart";
	}

	@Override
	public String name() {
		return "restart";
	}

	@Override
	public void exec(ICommandConsole console, List<String> arguments) {
		try {
			new ShellExec(true, true, true).execute("terminal.bat", ".", false);
			exit.run();
		} catch (IOException e) {
			console.error(e);
		}

	}

}
