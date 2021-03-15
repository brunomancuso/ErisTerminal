package org.threewaves.eris.engine;

/**
 * 
 * @author Bruno Mancuso
 *
 */
public class FailedTestCaseException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public FailedTestCaseException(String cause) {
		super(cause);
	}
}
