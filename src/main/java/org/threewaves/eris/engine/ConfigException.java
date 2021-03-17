package org.threewaves.eris.engine;

/**
 * @author Bruno Mancuso
 *
 */
public class ConfigException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * Create configuration exceptions
	 * @param content if the configuration that generates the exception. 
	 * @param cause
	 */
	public ConfigException(String content, Throwable cause) {
		super("Error in configuration: '" + content + "'", cause);
	}

	/**
	 * Create configuration exceptions
	 * @param message of the exception
	 */
	public ConfigException(String message) {
		super(message);
	}

	/**
	 * Create configuration exceptions
	 * @param exception that causes the exception
	 */
	public ConfigException(Throwable exception) {
		super(exception);
	}

}
