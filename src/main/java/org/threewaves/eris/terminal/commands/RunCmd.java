package org.threewaves.eris.terminal.commands;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import junit.framework.Test;
import org.threewaves.eris.engine.ConfigException;
import org.threewaves.eris.engine.Engine;
import org.threewaves.eris.engine.FailedTestCaseException;
import org.threewaves.eris.engine.ICommand;
import org.threewaves.eris.engine.ICommandConsole;
import org.threewaves.eris.engine.footprint.Module;
import org.threewaves.eris.engine.test_case.JUnitRuntime;
import org.threewaves.eris.engine.test_case.TestCase;
import org.threewaves.eris.engine.test_case.TestCaseRunner;
import org.threewaves.eris.util.Pad;

public class RunCmd implements ICommand {
	public static final String NAME = "run";
	private final TestCaseRunner runner;
	private final Engine eris;
	private final TestCaseArgumentParser parser;

	public RunCmd(Engine eris, String scriptEngine, TestCaseArgumentParser parser) {
		this.eris = eris;
		this.runner = new TestCaseRunner(eris, scriptEngine);
		this.parser = parser;
	}

	@Override
	public String description() {
		return "Execute test case";
	}

	@Override
	public String name() {
		return NAME;
	}

	public List<TestCase> testCases(List<String> options) {
		//TestCaseArgumentParser parser = new TestCaseArgumentParser(eris.getTestSuit(),
		//		runner.getAvailableModules());
		parser.process(options, true);
		return parser.testCases();
	}

	@Override
	public void exec(ICommandConsole console, List<String> options) {
		boolean background = CommandFactory.isBackgroundOption(options);
		boolean throwException = CommandFactory.isThrowExceptionOption(options);
		try {
			eris.getModules().refresh();
			parser.process(options, true);
			if (!parser.isAllIncluded()) {
				parser.save();
			}
			List<TestCase> testCases = parser.testCases();
			List<Module> modules = parser.modules();
			if (background) {
				console.state(false);
			}
			runner.beforeRunner(modules);
			JUnitRuntime.update(eris, runner);

			testCases.forEach(tc -> {
				try {
					runner.exec(tc, !background ? null : (testCase, duration, failedModules) -> {
						if (background) {
							console.state(true);
						}
						String sep = "";
						boolean failed = failedModules.size() > 0;
						String failedString = failedModules.stream().collect(Collectors.joining(","));
						console.notification(sep + Pad.right(testCase.getName(), 40) + " | "
								+ (failed ? "Fail" : " Ok ") + " | " + Pad.center(duration + " ms", 9) + " | "
								+ failedString + ICommandConsole.NEWLINE);
						if (background) {
							console.state(false);
						}
					});
					if (throwException && runner.testReport().currentReport().isFailed()) {
						String failedString = runner.testReport().currentReport().failedModules().stream()
								.collect(Collectors.joining(","));
						throw new FailedTestCaseException("Failed modules: " + failedString);
					}
				} catch (IOException e) {
					console.error(e);
				}
			});
		} catch (FailedTestCaseException e) {
			throw e;
		} catch (RuntimeException | ConfigException e) {
			console.error(e);
		} finally {
			runner.afterRunner();
			if (background) {
				console.state(true);
			}
		}
	}

	@Override
	public String usage() {
		return "run [<test case list>] [<include module list>] [&|-bg]";
	}
}
