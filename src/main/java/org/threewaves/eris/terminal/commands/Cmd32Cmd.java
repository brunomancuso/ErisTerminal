package org.threewaves.eris.terminal.commands;

import java.io.IOException;
import java.util.List;

import org.threewaves.eris.engine.ICommand;
import org.threewaves.eris.engine.ICommandConsole;
import org.threewaves.eris.util.ShellExec;


class Cmd32Cmd implements ICommand {
	
	public Cmd32Cmd() {
	}

	@Override
	public String description() {
		return "Open 32 bits batch console";
	}

	@Override
	public String usage() {
		return name() + " [<directory>]";
	}

	@Override
	public String name() {
		return "cmd32";
	}

	@Override
	public void exec(ICommandConsole console, List<String> arguments) {
		try {
			// Runtime.getRuntime().exec("cmd /c start cmd");
			String dir = ".";
			if (arguments.size() > 0) {
				dir = arguments.get(0);
			}
			new ShellExec(true, true, true).execute("cmd", dir, false, "/c", "start", "C:\\Windows\\SysWOW64\\cmd.exe");
		} catch (IOException e) {
			console.errorln(e.toString());
		}	
	}

}
