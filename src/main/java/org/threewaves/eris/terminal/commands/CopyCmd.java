package org.threewaves.eris.terminal.commands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.swing.JOptionPane;

import org.threewaves.eris.engine.Engine;
import org.threewaves.eris.engine.ICommand;
import org.threewaves.eris.engine.ICommandConsole;
import org.threewaves.eris.engine.test_case.TestCase;

class CopyCmd implements ICommand {
	private final Engine eris;
	private final TestCaseArgumentParser parser;

	public CopyCmd(Engine eris, TestCaseArgumentParser parser) {
		this.eris = eris;
		this.parser = parser;
	}

	@Override
	public String description() {
		return "copy a test case";
	}

	@Override
	public String usage() {
		return name() + " <Test case>";
	}

	@Override
	public String name() {
		return "copy";
	}

	@Override
	public void exec(ICommandConsole console, List<String> arguments) {
		parser.process(arguments);
		List<TestCase> testCases = parser.testCases();
		if (testCases.size() != 1) {
			console.errorln(usage());
			return;
		}
		Path path = testCases.get(0).getPath();
		if (path == null || !Files.exists(path)) {
			console.errorln("Cannot find tets case: " + path);
			return;
		}
		Path parent = path.getParent();
		if (parent == null) {
			console.errorln("Cannot create test case");
			return;
		}
		String newTc = JOptionPane.showInputDialog(null, "Test case filename?",
				testCases.get(0).getFileName().toString());
		if (newTc != null) {
			try {
				Files.copy(path, Paths.get(parent.toString(), newTc));
			} catch (IOException e) {
				console.errorln(e.getMessage());
			}
		}
	}

}
