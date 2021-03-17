package org.threewaves.eris.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.threewaves.eris.engine.IBuilder.Scope;
import org.threewaves.eris.engine.footprint.Modules;
import org.threewaves.eris.engine.test_case.TestSuit;

/**
 * Eris engine runtime, contains the modules that are configurated, the eris factory, 
 * the engine builders and the location of the current test suit.
 * @author Bruno Mancuso 
 */
public class Engine {
	private final Modules modules = new Modules();
	private final IErisFactory factory;
	private final List<IBuilder> engineBuilders = new ArrayList<>();
	private final TestSuit suit;

	/**
	 * Create an engine instance
	 * @param factory of the engin e
	 * @param suit the test case suit of the engine
	 */
	public Engine(IErisFactory factory, TestSuit suit) {
		this.suit = suit;
		this.factory = factory;
	}

	/**
	 * Initialized the eris engine. The engine scope builders are created, and the modules are loaded. 
	 * @throws ConfigException
	 */
	public void initialize() throws ConfigException {
		List<IBuilder> list = factory.createBuilders(Scope.ENGINE);
		if (list != null) {
			engineBuilders.addAll(list);
		}
		validateBuilders(engineBuilders);
		modules.refresh();
	}

	/**
	 * Create the suit scope builders.
	 * @return suit builder list
	 */
	public List<IBuilder> createSuitBuilders() {
		List<IBuilder> list = factory.createBuilders(Scope.TEST_SUIT);
		if (list == null) {
			list = Collections.emptyList();
		}
		validateBuilders(list);
		return list;
	}

	/**
	 * Validate builders.
	 * @param list builders to validate
	 */
	private void validateBuilders(List<IBuilder> list) {
		list.stream().forEach(b -> {
			if (b.name().isEmpty()) {
				throw new IllegalArgumentException("Builder name cannot be empty");
			}
			;
		});
	}

	/**
	 * Sleep method to be invoked from test cases. 
	 * @param time to sleep
	 * @throws InterruptedException
	 */
	public void sleep(long time) throws InterruptedException {
		Thread.sleep(1000);
	}

	/**
	 * Create test case builders.
	 * @return list of builders with test case scope
	 */
	public List<IBuilder> createTestCaseBuilders() {
		List<IBuilder> list = factory.createBuilders(Scope.TEST_CASE);
		if (list == null) {
			list = Collections.emptyList();
		}
		validateBuilders(list);
		return list;

	}

	/**
	 * Engine builders
	 * @return Engine builders
	 */
	public List<IBuilder> getEngineBuilders() {
		return engineBuilders;
	}

	/**
	 * Destroy eris runtime engine.
	 */
	public void destroy() {
		engineBuilders.stream().forEach(b -> destroy());
	}

	/**
	 * 
	 * @return eris factory
	 */
	public IErisFactory getFactory() {
		return factory;
	}

	/**
	 * 
	 * @return test suit
	 */
	public TestSuit getTestSuit() {
		return suit;
	}

	/**
	 * 
	 * @return modules
	 */
	public Modules getModules() {
		return modules;
	}

}
