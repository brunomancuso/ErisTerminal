package org.threewaves.eris.engine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.threewaves.eris.engine.test_case.TestSuit;
import org.threewaves.eris.util.Reflection;
import org.threewaves.eris.util.YamlConfig;

public class Config {
	public final String notepad;
	public final String diffw;
	private final String factory;
	private final String testCaseDirectory;
	public final String scriptEngine;
	public final String fontFamily;
	public final int fontSize;

	public Config() {
		notepad = "notepad2.exe";
		diffw = "C:\\Program Files (x86)\\WinMerge\\WinMergeU.exe";			
		factory = DefaultErisFactory.class.getName();
		testCaseDirectory = "../src/main/test-case";
		scriptEngine = "ECMAScript";
		fontFamily = "Consolas";
		fontSize = 18;
	}

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

	public IErisFactory createFactory() {
		return Reflection.newInstance(factory, IErisFactory.class);
	}

	public TestSuit createTestSuit() {
		return new TestSuit(Paths.get(testCaseDirectory));
	}
}