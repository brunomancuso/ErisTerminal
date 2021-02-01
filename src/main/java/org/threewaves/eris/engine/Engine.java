package org.threewaves.eris.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.threewaves.eris.engine.IBuilder.Scope;
import org.threewaves.eris.engine.footprint.Modules;
import org.threewaves.eris.engine.test_case.TestSuit;

public class Engine {
	private final Modules modules = new Modules();
	private final IErisFactory factory;
	private final List<IBuilder> engineBuilders = new ArrayList<>();
	private final TestSuit suit;
	
	public Engine(IErisFactory factory, TestSuit suit) {
		this.suit = suit;
		this.factory = factory;
	}
	
	public void initialize() throws ConfigException {
		List<IBuilder> list = factory.createBuilders(Scope.ENGINE);
		if (list != null) {
			engineBuilders.addAll(list);
		}
		validateBuilders(engineBuilders);
		modules.refresh();
	}
	
	public List<IBuilder> createSuitBuilders() {
		List<IBuilder> list = factory.createBuilders(Scope.TEST_SUIT);
		if (list == null) {
			list = Collections.emptyList();
		}
		validateBuilders(list);
		return list;
	}

	private void validateBuilders(List<IBuilder> list) {
		list.stream().forEach(b -> {
			if (b.name().isEmpty()) {
				throw new IllegalArgumentException("Builder name cannot be empty");
			};
		});		
	}

	public void sleep(long time) throws InterruptedException {
		Thread.sleep(1000);
	}
	
	public List<IBuilder> createTestCaseBuilders() {
		List<IBuilder> list = factory.createBuilders(Scope.TEST_CASE);
		if (list == null) {
			list = Collections.emptyList();
		}
		validateBuilders(list);
		return list;

	}

	public List<IBuilder> getEngineBuilders() {
		return engineBuilders;
	}

	public void destroy() {
		engineBuilders.stream().forEach(b -> destroy());
	}

	public IErisFactory getFactory() {
		return factory;
	}

	public TestSuit getTestSuit() {
		return suit;
	}

	public Modules getModules() {
		return modules;
	}

}
