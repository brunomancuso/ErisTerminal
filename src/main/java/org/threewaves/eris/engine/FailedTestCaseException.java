package org.threewaves.eris.engine;

/**
 * 
 * @author Bruno Mancuso
 *
 */
public class FailedTestCaseException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Create a failed test case exception
	 * @param message of the exception
	 */
	public FailedTestCaseException(String message) {
		super(message);
	}
}
