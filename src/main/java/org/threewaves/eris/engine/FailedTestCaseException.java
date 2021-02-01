package org.threewaves.eris.engine;

public class FailedTestCaseException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public FailedTestCaseException(String cause) {
		super(cause);
	}
}
