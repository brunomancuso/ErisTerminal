package org.threewaves.eris.engine.test_case;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.threewaves.eris.engine.Engine;
import org.threewaves.eris.engine.IBuilder;


public class JUnitNonEclipse implements IJUnit {
	private final Engine engine;
	private final TestCaseRunner runner;
	
	public JUnitNonEclipse(Engine engine, TestCaseRunner runner) {
		super();
		this.engine = engine;
		this.runner = runner;
	}

	@Override
	public void afterAll(Consumer<String> assertModule) throws IOException {
		
	}

	@Override
	public void beforeAll() {
		
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
