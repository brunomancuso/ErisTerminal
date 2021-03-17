package org.threewaves.eris.engine;

/**
 *  
 * @author Bruno Mancuso
 *
 */
public interface IBuilder {
	/**
	 * Each builder can be of 3 types, the type indicates the life cycle of the builder. 
	 * @author Bruno Mancuso
	 *
	 */
	public enum Scope {
		/**
		 * Test case scope, indicates that the builder will be created before each test case.
		 */
		TEST_CASE,
		/**
		 * Test suit scope, indicates that the builder will be created before each run
		 */
		TEST_SUIT, 
		/**
		 * Engine scope, indicates that the builder will be when the engine is created
		 */
		ENGINE;
	}

	/**
	 * 
	 * @return the scope of the builder
	 */
	Scope scope();

	/**
	 * 
	 * @return the name of the builder.
	 */
	String name();

	/**
	 * Will be invoked, when the builder is dereferenced.
	 */
	void destroy();

}
