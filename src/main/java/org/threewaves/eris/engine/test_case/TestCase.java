package org.threewaves.eris.engine.test_case;

import java.nio.file.Path;
import java.util.Optional;

import org.threewaves.eris.util.Format;

public class TestCase {
	public static final String PREFIX_NAME = "Test_";
	public static final String PREFIX_NAME_SHORT = "tc_";
	private final Path path;
	
	private TestCase(Path p) {
		this.path = p;
	}

	public static TestCase of(Path p) {
		return new TestCase(p);
	}

	public String getName() {
		Path path = this.path;
		if (path != null) {
			path = path.getFileName();
			if (path != null) {
				String name = path.toString();
				return name.substring(0, name.lastIndexOf('.'));
			}
		}
		return null;
	}

	public int getNumber() {
		return number(path);
	}
	
	public static int number(Path path) {
		if (path != null) {
			path = path.getFileName();
			if (path != null) {
				String name = path.toString();
				if (name != null) {
					if (name.startsWith(PREFIX_NAME)) {
						name = name.substring(PREFIX_NAME.length(), PREFIX_NAME.length() + 4);
						return Format.parseInt(name, 0);
					}
					if (name.startsWith(PREFIX_NAME_SHORT)) {
						name = name.substring(PREFIX_NAME.length(), PREFIX_NAME_SHORT.length() + 4);
						return Format.parseInt(name, 0);
					}
				}

			}
		}
		return 0;
	}

	public Path getPath() {
		return path;
	}

	public Path getFileName() {
		return path.getFileName();
	}
	
	public String toString() {
		return path.toString();
	}

	public boolean isJS() {
		return getFileName().toString().toLowerCase().endsWith(".js");
	}

	public Optional<Class<?>> getTestCaseClass() {
		try {
			return Optional.of(Class.forName(getName()));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}
}
