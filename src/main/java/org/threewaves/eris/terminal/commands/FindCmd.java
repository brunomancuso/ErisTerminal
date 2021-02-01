package org.threewaves.eris.terminal.commands;

import java.io.IOException;
import java.util.List;

import org.threewaves.eris.engine.ICommand;
import org.threewaves.eris.engine.ICommandConsole;
import org.threewaves.eris.engine.test_case.TestCase;
import org.threewaves.eris.engine.test_case.TestSuit;
import org.threewaves.eris.util.ShellExec;

class FindCmd implements ICommand {

	private final TestSuit suit;

	public FindCmd(TestSuit suit) {
		this.suit = suit;
	}

	@Override
	public String description() {
		return "Find keyword in test cases";
	}

	@Override
	public String usage() {
		return "find <pattern>";
	}

	@Override
	public String name() {
		return "find";
	}

	@Override
	public void exec(ICommandConsole console, List<String> arguments) {
		if (arguments.size() == 0) {
			console.errorln(usage());
			return;
		}
		try {
			ShellExec shell = new ShellExec(false, true, true);
			shell.execute(".\\bin\\grep.exe", ".", true, suit.directory() +  "/" + TestCase.PREFIX_NAME + "*.*",
					"--text", "-e", arguments.get(0));
			console.print(shell.getOutput());
			if (!shell.getError().isEmpty()) {
				console.errorln(shell.getError());
			}
		} catch (IOException e) {
			console.errorln(e.toString());
		}

	}
}
