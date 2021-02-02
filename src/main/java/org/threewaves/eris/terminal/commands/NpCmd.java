package org.threewaves.eris.terminal.commands;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.threewaves.eris.engine.Config;
import org.threewaves.eris.engine.ICommand;
import org.threewaves.eris.engine.ICommandConsole;
import org.threewaves.eris.engine.test_case.TestCase;
import org.threewaves.eris.engine.test_case.TestSuit;
import org.threewaves.eris.util.ShellExec;

class NpCmd implements ICommand {

	private final Config config;
	private final TestSuit suit;

	public NpCmd(Config config, TestSuit suit) {
		this.config = config;
		this.suit = suit;
	}

	@Override
	public String description() {
		return "view file in notepad";
	}

	@Override
	public String usage() {
		return name() + " <Filename> | <Testcase>";
	}

	@Override
	public String name() {
		return "np";
	}

	@Override
	public void exec(ICommandConsole console, List<String> arguments) {
		if (arguments.size() == 0) {
			throw new IllegalArgumentException(usage());
		}
		arguments.forEach(a -> np(console, a));
	}

	private void np(ICommandConsole console, String arg) {

		List<TestCase> list = TestCaseArgumentParser.parse(suit, arg);
		if (list.size() == 0) {
			np(console, Paths.get(arg));
		} else {
			list.forEach(tc -> {
				np(console, tc.getPath());
			});
		}
	}

	private void np(ICommandConsole console, Path p) {
		try {
			new ShellExec().execute(config.notepad, ".", false, p.toAbsolutePath().toString());
		} catch (IOException e) {
			console.error(e);
		}
	}

}
