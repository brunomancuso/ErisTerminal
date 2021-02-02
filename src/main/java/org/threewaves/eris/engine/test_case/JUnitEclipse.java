package org.threewaves.eris.engine.test_case;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.threewaves.eris.engine.Config;
import org.threewaves.eris.engine.ConfigException;
import org.threewaves.eris.engine.Engine;
import org.threewaves.eris.engine.IBuilder;

public class JUnitEclipse implements IJUnit {
	private final TestCaseRunner runner;
	private final Engine engine;

	public JUnitEclipse() {
		try {
			validateDirectory();
			Config config = Config.create();
			engine = new Engine(config.createFactory(), config.createTestSuit());
			engine.initialize();
			runner = new TestCaseRunner(engine, config.scriptEngine);
			runner.beforeRunner();
		} catch (ConfigException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void validateDirectory() {
		if (!Files.exists(Paths.get("eris.yaml"))) {
			throw new RuntimeException("Unit test need to run in runtime directory");
		}
	}

	@Override
	public void afterAll(Consumer<String> assertModule) throws IOException {
		runner.afterAll(assertModule);
	}

	@Override
	public void beforeAll() {
		Exception e = new Exception();
		e.fillInStackTrace();
		StackTraceElement st = e.getStackTrace()[1];
		Optional<TestCase> testCase = engine.getTestSuit().findByFileName(st.getFileName());
		if (!testCase.isPresent()) {
			throw new RuntimeException("Test case not found: " + st.getFileName());
		}
		runner.beforeAll(testCase.get(), null);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends IBuilder> T getBuilder(String name, Class<T> clazz) {
		List<IBuilder> bs = new ArrayList<>();
		bs.addAll(engine.getEngineBuilders());
		bs.addAll(runner.getSuitBuilders());
		if (runner.getTestCaseBuilders() != null) {
			bs.addAll(runner.getTestCaseBuilders());
		}
		for (IBuilder b : bs) {
			if (b.name().equals(name)) {
				return (T) b;
			}
		}
		return null;
	}

	@Override
	public Engine getEngine() {
		return engine;
	}
}
