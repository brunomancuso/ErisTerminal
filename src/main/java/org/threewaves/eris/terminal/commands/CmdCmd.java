package org.threewaves.eris.terminal.commands;

import java.io.IOException;
import java.util.List;

import org.threewaves.eris.engine.ICommand;
import org.threewaves.eris.engine.ICommandConsole;
import org.threewaves.eris.util.ShellExec;

class CmdCmd implements ICommand {

	public CmdCmd() {
	}

	@Override
	public String description() {
		return "Open batch console";
	}

	@Override
	public String usage() {
		return name() + ": [<directory>]";
	}

	@Override
	public String name() {
		return "cmd";
	}

	@Override
	public void exec(ICommandConsole console, List<String> arguments) {
		try {
			// Runtime.getRuntime().exec("cmd /c start cmd");
			String dir = ".";
			if (arguments.size() > 0) {
				dir = arguments.get(0);
			}
			new ShellExec(true, true, true).execute("cmd", dir, false, "/c", "start", "cmd");
		} catch (IOException e) {
			console.errorln(e.toString());
		}
	}

}
