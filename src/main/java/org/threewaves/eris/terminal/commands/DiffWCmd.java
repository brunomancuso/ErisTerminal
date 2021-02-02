package org.threewaves.eris.terminal.commands;

import java.io.IOException;
import java.util.List;

import org.threewaves.eris.engine.Config;
import org.threewaves.eris.engine.Engine;
import org.threewaves.eris.engine.ICommand;
import org.threewaves.eris.engine.ICommandConsole;
import org.threewaves.eris.engine.footprint.Module;
import org.threewaves.eris.engine.test_case.TestCase;
import org.threewaves.eris.engine.test_case.TestRun;
import org.threewaves.eris.util.ShellExec;

class DiffWCmd implements ICommand {
	private final Engine eris;
	private final Config config;

	public DiffWCmd(Config config, Engine eris) {
		this.eris = eris;
		this.config = config;
	}

	@Override
	public String description() {
		return "Show external diff tool";
	}

	@Override
	public String name() {
		return "diffw";
	}

	@Override
	public void exec(ICommandConsole console, List<String> options) {
		TestCaseArgumentParser parser = new TestCaseArgumentParser(eris.getTestSuit(), eris.getModules());
		parser.process(options);
		List<TestCase> testCases = parser.testCases();
		if (testCases.size() > 1) {
			console.errorln("Can only diff 1 test cases at the time");
			return;
		}
		List<Module> modules = parser.modules();
		TestRun testRun = new TestRun();
		for (TestCase testCase : testCases) {
			for (Module m : modules) {
				if (testRun.isFailed(testCase, m.getName())) {
					try {
						new ShellExec().execute(config.diffw, ".", false,
								testRun.toExpectedModule(testCase.getName(), m.getName()).toString(),
								testRun.toActualModule(testCase.getName(), m.getName()).toString());

					} catch (IOException e) {
						console.error(e);
					}
				}
			}
		}
	}

	@Override
	public String usage() {
		return "diffw <test case> [<include module list>]";
	}

}
