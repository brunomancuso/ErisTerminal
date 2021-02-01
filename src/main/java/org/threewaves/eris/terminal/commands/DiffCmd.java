package org.threewaves.eris.terminal.commands;

import java.io.IOException;
import java.util.List;

import org.threewaves.eris.engine.Engine;
import org.threewaves.eris.engine.ICommand;
import org.threewaves.eris.engine.ICommandConsole;
import org.threewaves.eris.engine.footprint.Diff;
import org.threewaves.eris.engine.footprint.Module;
import org.threewaves.eris.engine.footprint.NormalDiff;
import org.threewaves.eris.engine.test_case.TestCase;
import org.threewaves.eris.engine.test_case.TestRun;

class DiffCmd implements ICommand {
	private final Engine eris;
	
	public DiffCmd(Engine eris) {
		this.eris = eris;
	}

	@Override
	public String description() {
		return "Show diff inline";
	}

	@Override
	public String name() {
		return "diff";
	}

	@Override
	public void exec(ICommandConsole console, List<String> options) {
		TestCaseArgumentParser parser = new TestCaseArgumentParser(eris.getTestSuit(), eris.getModules());
		parser.process(options);
		List<TestCase> testCases = parser.testCases();
		List<Module> modules = parser.modules();		
		TestRun testRun = new TestRun();
		for (TestCase testCase : testCases) {
			for (Module m : modules) {
				if (testRun.isFailed(testCase, m.getName())) {
					List<NormalDiff> diff;
					try {
						diff = new Diff().ndiff(testRun.toExpectedModule(testCase.getName(), m.getName()),
								testRun.toActualModule(testCase.getName(), m.getName()));
						if (diff.size() > 0) {
							System.out.println("Test case: " + testCase);
							System.out.println("Module: " + m);
							System.out.println(NormalDiff.toString(diff));
						}
					} catch (IOException e) {
						console.error(e);
					}
				}
			}
		}
	}

	@Override
	public String usage() {
		return "diff [<test case list>] [<include module list>]";
	}

}
