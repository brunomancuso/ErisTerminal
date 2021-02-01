package org.threewaves.eris.engine;

public interface IBuilder {
	public enum Scope {
		TEST_CASE, TEST_SUIT, ENGINE;
	}
	Scope scope();
	String name();
	void destroy();
	

}
