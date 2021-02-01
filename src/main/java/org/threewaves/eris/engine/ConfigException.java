package org.threewaves.eris.engine;


public class ConfigException extends Exception {
	private static final long serialVersionUID = 1L;

	public ConfigException(String content, Throwable cause) {
		super("Error in configuration: '" + content + "'", cause);
	}

	public ConfigException(String message) {
		super(message);
	}

	public ConfigException(Throwable e) {
		super(e);
	}

}
