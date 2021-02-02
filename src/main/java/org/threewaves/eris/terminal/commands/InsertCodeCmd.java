package org.threewaves.eris.terminal.commands;

import java.io.IOException;
import java.util.List;

import org.threewaves.eris.engine.Engine;
import org.threewaves.eris.engine.ICommand;
import org.threewaves.eris.engine.ICommandConsole;
import org.threewaves.eris.engine.footprint.Module;
import org.threewaves.eris.engine.test_case.TestCase;
import org.threewaves.eris.engine.test_case.TestCaseInsertCode;

class InsertCodeCmd implements ICommand {
	private final Engine eris;

	public InsertCodeCmd(Engine eris) {
		this.eris = eris;
	}

	@Override
	public String description() {
		return "insert code";
	}

	@Override
	public String name() {
		return "insert";
	}

	@Override
	public void exec(ICommandConsole console, List<String> options) {
		TestCaseArgumentParser parser = new TestCaseArgumentParser(eris.getTestSuit(), eris.getModules());
		parser.process(options);
		List<TestCase> testCases = parser.testCases();
		List<Module> modules = parser.modules();
		testCases.forEach(tc -> {
			try {
				new TestCaseInsertCode().insert(tc, Module.toStringList(modules));
			} catch (RuntimeException | IOException e) {
				console.errorln(e.getMessage());
			}
		});
	}

	@Override
	public String usage() {
		return "insert [<test case list>] [<include module list>]";
	}

}
