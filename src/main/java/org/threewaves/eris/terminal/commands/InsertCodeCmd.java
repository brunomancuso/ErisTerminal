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
	private final TestCaseArgumentParser parser;

	public InsertCodeCmd(Engine eris, TestCaseArgumentParser parser) {
		this.eris = eris;
		this.parser = parser;
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
