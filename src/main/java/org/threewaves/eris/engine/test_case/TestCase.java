package org.threewaves.eris.engine.test_case;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
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

	private String getPackage() throws IOException {
		if (isJS()) {
			return "";
		}
		List<String> lines = Files.readAllLines(this.path);
		for (String line : lines) {
			line = line.trim();
			if (line.startsWith("package")) {
				line = line.substring(line.indexOf(' '), line.indexOf(';'));
				return line.trim();
			} else if (line.startsWith("import") || line.startsWith("public") || line.startsWith("class")) {
				return "";
			}
		}
		return "";
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
			String p = getPackage();
			if (!p.isEmpty()) {
				p = p + ".";
			}
			String name = p + getName();
			return Optional.of(Class.forName(name));
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}
}
