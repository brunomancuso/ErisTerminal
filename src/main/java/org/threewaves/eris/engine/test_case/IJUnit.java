package org.threewaves.eris.engine.test_case;

import java.io.IOException;
import java.util.function.Consumer;

import org.threewaves.eris.engine.Engine;
import org.threewaves.eris.engine.IBuilder;

public interface IJUnit {

	void afterAll(Consumer<String> assertModule) throws IOException;

	void beforeAll() throws ClassNotFoundException;

	<T extends IBuilder> T getBuilder(String name, Class<T> clazz);

	Engine getEngine();

}