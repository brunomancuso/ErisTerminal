package org.threewaves.eris.engine.test_case;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.threewaves.eris.engine.Engine;

public class JUnitRuntime {
	private static IJUnit connector = new JUnitEclipse();
	
	public static IJUnit get() {
		return connector;
	}
	
	public static void update(Engine engine, TestCaseRunner runner) {
		connector = new JUnitNonEclipse(engine, runner);
	}
	
	public static void insertCode() throws IOException {
		Exception e = new Exception();
		e.fillInStackTrace();
		StackTraceElement st = e.getStackTrace()[1];
		Optional<TestCase> testCase = connector.getEngine().getTestSuit().findByFileName(st.getFileName());
		if (!testCase.isPresent()) {
			throw new RuntimeException("Test case not found: " + st.getFileName());
		}
		new TestCaseInsertCode().insert(testCase.get(), connector.getEngine().getModules().getList().stream().map(m -> m.getName()).collect(Collectors.toList()));
	}

}
