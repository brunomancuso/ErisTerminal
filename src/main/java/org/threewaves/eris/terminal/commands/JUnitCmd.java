package org.threewaves.eris.terminal.commands;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.threewaves.eris.engine.CommandConsole;
import org.threewaves.eris.engine.Engine;
import org.threewaves.eris.engine.ICommand;
import org.threewaves.eris.engine.ICommandConsole;
import org.threewaves.eris.engine.test_case.TestCase;

public class JUnitCmd implements ICommand {
	public static final String NAME = "junit";
	private final Engine eris;

	public JUnitCmd(Engine eris) {
		this.eris = eris;
	}
	
	@Override
	public String description() {
		return "Generate JUnit Test case";
	}

	@Override
	public String usage() {
		return NAME + "[<test case list>]";
	}

	@Override
	public String name() {
		return NAME;
	}

	@Override
	public void exec(ICommandConsole console, List<String> arguments) {
		Path output = Paths.get("out", "jenkins");
		if (!Files.exists(output)) {
			try {
				Files.createDirectories(output);
			} catch (IOException e) {
				console.error(e);
			}
		}
		TestCaseArgumentParser parser = new TestCaseArgumentParser(eris.getTestSuit(), eris.getModules());
		parser.process(arguments);
		List<TestCase> testCases = parser.testCases();
		testCases.forEach(tc -> {
			boolean js = tc.getFileName().toString().endsWith(".js");
			try {
				if (js) {
					String testCase = tc.getName();
					String number = Integer.toString(tc.getNumber());
					console.notification("+ " + testCase + CommandConsole.NEWLINE);
					Path newFile = Paths.get(output.toString(), testCase + ".java");
					Files.deleteIfExists(newFile);
					String template = new String(Files.readAllBytes(Paths.get("templates/junit.template")),
							Charset.defaultCharset());
					String newTestCase = template.replaceAll("__TEST_CASE__", testCase).replaceAll("__TEST_CASE_NUMBER__", number).replace("__TEST_CASE_EXTENSION__",
							js ? "js" : "java");
					Files.write(newFile, newTestCase.getBytes(Charset.defaultCharset()));
				} else if (!js) {
					String testCase = tc.getName();
					Path newFile = Paths.get(output.toString(), testCase + ".java");
					Files.deleteIfExists(newFile);
					Files.copy(tc.getPath(), newFile);
				}
			} catch (IOException e) {
				console.error(e);
			}

		});					
	}

}
