package org.threewaves.eris.engine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.threewaves.eris.engine.test_case.TestSuit;
import org.threewaves.eris.util.Reflection;
import org.threewaves.eris.util.YamlConfig;

/**
 * Configuration properties 
 * @author Bruno Mancuso
 *
 */
public class Config {
	/**
	 * Notepad path 
	 */
	public final String notepad;
	/**
	 * Merge tool
	 */
	public final String diffw;
	/**
	 * Factory class name implementation
	 */
	private final String factory;
	/**
	 * Test case directory path
	 */
	private final String testCaseDirectory;
	/**
	 * JS script engine
	 */
	public final String scriptEngine;
	/**
	 * Console font family
	 */
	public final String fontFamily;
	/**
	 * Console font size
	 */
	public final int fontSize;

	/**
	 * Constructor for default configuration
	 */
	public Config() {
		notepad = "notepad2.exe";
		diffw = "C:\\Program Files (x86)\\WinMerge\\WinMergeU.exe";
		factory = DefaultErisFactory.class.getName();
		testCaseDirectory = "../src/main/test-case";
		scriptEngine = "ECMAScript";
		fontFamily = "Consolas";
		fontSize = 12;
	}

	/**
	 * Creates or loads the configuration properties of the eris runtime engine.
	 * @return the current configuration properties
	 * @throws ConfigException
	 */
	public static Config create() throws ConfigException {
		try {
			if (!Files.exists(Paths.get("out"))) {
				Files.createDirectories(Paths.get("out"));
			}
			if (!Files.exists(Paths.get("logs"))) {
				Files.createDirectories(Paths.get("logs"));
			}
			if (Files.exists(Paths.get("eris.yaml"))) {
				return YamlConfig.create(Files.readAllBytes(Paths.get("eris.yaml")), new Config());
			}
			return new Config();
		} catch (IOException e) {
			throw new ConfigException(e);
		}
	}

	/**
	 * Creates a Eris factory from configuration.
	 * @return Factory instance
	 */
	public IErisFactory createFactory() {
		return Reflection.newInstance(factory, IErisFactory.class);
	}

	/**
	 * Creates a test suit from configuration
	 * @return a test suit
	 */
	public TestSuit createTestSuit() {
		return new TestSuit(Paths.get(testCaseDirectory));
	}
}
