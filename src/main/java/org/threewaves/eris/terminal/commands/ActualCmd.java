package org.threewaves.eris.terminal.commands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.threewaves.eris.engine.Engine;
import org.threewaves.eris.engine.ICommand;
import org.threewaves.eris.engine.ICommandConsole;
import org.threewaves.eris.engine.footprint.Module;
import org.threewaves.eris.engine.test_case.TestCase;
import org.threewaves.eris.engine.test_case.TestRun;

class ActualCmd implements ICommand {
	private final Engine eris;

	public ActualCmd(Engine eris) {
		this.eris = eris;
	}

	@Override
	public String description() {
		return "view actual module content";
	}

	@Override
	public String usage() {
		return "actual <test case> <module name>";
	}

	@Override
	public String name() {
		return "actual";
	}

	@Override
	public void exec(ICommandConsole console, List<String> arguments) {
		TestCaseArgumentParser parser = new TestCaseArgumentParser(eris.getTestSuit(), eris.getModules());
		parser.process(arguments);
		List<TestCase> testCases = parser.testCases();
		List<Module> modules = parser.modules();
		testCases.forEach(tc -> {
			try {
				console.notification(tc.getName() + ICommandConsole.NEWLINE);
				for (Module module : modules) {
					console.notification(module.getName() + ICommandConsole.NEWLINE);
					TestRun testRun = new TestRun();
					Path p = testRun.toRawModule(tc.getName(), module.getName());
					if (!Files.exists(p)) {
						console.errorln("Module output does not exists: " + module.getName());
					} else {
						Files.readAllLines(p).forEach(
								text -> console.notification(ICommandConsole.TAB + text + ICommandConsole.NEWLINE));
					}
				}
			} catch (RuntimeException | IOException e) {
				console.errorln(e.getMessage());
			}
		});

	}

}
