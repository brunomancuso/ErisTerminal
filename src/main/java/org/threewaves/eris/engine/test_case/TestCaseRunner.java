package org.threewaves.eris.engine.test_case;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.threewaves.eris.engine.Engine;
import org.threewaves.eris.engine.IBuilder;
import org.threewaves.eris.engine.footprint.Module;
import org.threewaves.eris.engine.footprint.Modules;
import org.threewaves.eris.engine.footprint.NormalDiff;

//ST
public class TestCaseRunner {
	public interface Progress {
		void summary(TestCase testCase, long duration, List<String> failedModules);
	}

	private final List<Module> modules = new ArrayList<>();
	private final Engine eris;

	private boolean junit = false;
	private List<IBuilder> testCaseBuilders;
	private List<IBuilder> suitBuilders;
	private TestReport testReport;
	private Progress progress;
	private final String scriptEngine;

	public TestCaseRunner(Engine eris, String scriptEngine) {
		this.eris = eris;
		this.scriptEngine = scriptEngine;
	}

	public static class Stacktrace {
		private Path path;

		public Stacktrace(Path path) {
			this.path = path;
		}

		public void error(Object o) {
			if (o instanceof Throwable) {
				System.err.println("Error in script: " + path.getFileName());
				((Throwable) o).printStackTrace();
			} else if (o != null) {
				System.err.println(o.toString());
			}
		}
	}

	public void destroy() {
		eris.destroy();
	}

	// @BeforeAll
	void beforeRunner() throws IOException {
		junit = true;
		beforeRunner(eris.getModules().getList());
	}

	public void beforeRunner(List<Module> actualModuleList) {
		this.modules.clear();
		this.modules.addAll(actualModuleList);
		testReport = TestReport.create();
		suitBuilders = eris.createSuitBuilders();
		modules.forEach(m -> m.mark());
	}

	public void afterRunner() {
		testReport.summary();
		suitBuilders.stream().forEach(b -> b.destroy());
		if (junit) {
			destroy();
		}
	}

	void beforeAll(TestCase testCase, Progress progress) {
		System.out.println("----- Start " + testCase.getFileName());
		this.progress = progress;
		testReport.start(testCase);
		testCaseBuilders = eris.createTestCaseBuilders();
		eris.getModules().mark();
	}

	public void afterAll(Consumer<String> assertModule) throws IOException {
		System.out.println("----- End " + testReport.current().getFileName());
		testCaseBuilders.stream().forEach(b -> b.destroy());
		System.out.println("----- Summary");
		for (Module module : modules) {
			List<NormalDiff> diff = module.assertExpected(testReport.getTestRun(), module.getName(),
					testReport.current());
			if (diff.size() > 0) {
				System.err.println("Difference " + module + ": ");
				System.err.println(NormalDiff.toString(diff));
			}
			testReport.diff(module.getName(), diff);
		}
		testReport.end();
		if (progress != null) {
			progress.summary(testReport.current(), testReport.currentReport().getDuration(),
					testReport.currentReport().failedModules());
		}
		if (junit && assertModule != null && testReport.currentReport().failedModules().size() > 0) {
			assertModule.accept(testReport.currentReport().failedModules().stream().collect(Collectors.joining(", ")));
		}
	}

	public void exec(TestCase testCase, Progress progress) throws IOException {
		if (testCase.isJS()) {
			execJS(testCase, progress);
		} else {
			execJUnit4(testCase, progress);
		}
	}

	private void execJUnit4(TestCase testCase, Progress progress) throws IOException {
		try {
			beforeAll(testCase, progress);
			JUnitCore junit = new JUnitCore();
			if (testCase.getTestCaseClass().isPresent()) {
				Result result = junit.run(testCase.getTestCaseClass().get());
				if (result.getFailureCount() > 0) {
					result.getFailures().stream().forEach(f -> {
						f.getException().printStackTrace();
					});
					System.err.println("Test case failed");
				}
			}
		} finally {
			afterAll(null);
		}
	}

	private void execJS(TestCase testCase, Progress progress) throws IOException {
		beforeAll(testCase, progress);
		if (!Files.exists(testCase.getPath())) {
			System.err.println("File not found: " + testCase.getPath());
			return;
		}
		try (InputStreamReader reader = new InputStreamReader(new FileInputStream(testCase.getPath().toFile()),
				Charset.defaultCharset())) {
			ScriptEngineManager factory = new ScriptEngineManager();
			ScriptEngine engine = factory.getEngineByName(scriptEngine);
			ScriptContext newContext = new SimpleScriptContext();
			Bindings engineScope = newContext.getBindings(ScriptContext.ENGINE_SCOPE);
			testCaseBuilders.stream().forEach(b -> engineScope.put(b.name(), b));
			suitBuilders.stream().forEach(b -> engineScope.put(b.name(), b));
			eris.getEngineBuilders().stream().forEach(b -> engineScope.put(b.name(), b));
			engineScope.put("out", System.out);
			engineScope.put("eris", engine);
			engineScope.put("stacktrace", new Stacktrace(testCase.getPath()));
			engine.eval(reader, newContext);
			engine.eval("try {\n" + "   test();\n" + "} catch(e) {\n" + "   stacktrace.error(e);" + "}\n", newContext);
		} catch (RuntimeException | ScriptException | IOException e) {
			e.printStackTrace();
		} finally {
			afterAll(null);
		}
	}

	public Modules getAvailableModules() {
		return eris.getModules();
	}

	public Engine getEngine() {
		return eris;
	}

	public List<IBuilder> getSuitBuilders() {
		return suitBuilders;
	}

	public List<IBuilder> getTestCaseBuilders() {
		return testCaseBuilders;
	}

	public TestReport testReport() {
		return testReport;
	}
}
