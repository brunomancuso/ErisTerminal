package org.threewaves.eris.engine.test_case;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class TestRun {
	public static class Data {
		public final String path;
		public final Date start;
		public final Date end;
		public final long duration;

		public Data(Path testCase, Date start, Date end) {
			this.path = testCase.toString();
			this.start = start;
			this.end = end;
			this.duration = end != null && start != null ? ((end.getTime() - start.getTime())) : 0;
		}

		public Data end() {
			return new Data(Paths.get(path), start, new Date());
		}

		public static Data start(Path testCase) {
			return new Data(testCase, new Date(), null);
		}

		private Data save() throws IOException {
			String c = gson().toJson(this);
			Files.write(Paths.get(path, "data.json"), c.getBytes(Charset.defaultCharset()));
			return this;
		}

		private static Data load(Path testCase) throws JsonSyntaxException, IOException {
			Path path = Paths.get(testCase.toString(), "data.json");
			if (Files.exists(path)) {
				return gson().fromJson(new String(Files.readAllBytes(path), Charset.defaultCharset()), Data.class);
			}
			return new Data(testCase, null, null);
		}
	}

	private final Path testRunPath;

	public TestRun() {
		this.testRunPath = Paths.get("out", "test-run");
	}

	public static Gson gson() {
		GsonBuilder b = new GsonBuilder();
		b.setPrettyPrinting();
		return b.create();
	}

	public TestRun.Data end(TestCase testCase) {
		Path dir = toTestRunPath(testCase.getName());
		try {
			return TestRun.Data.load(dir).end().save();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error ending test run: " + dir.toFile() + ", " + e);
		}
		return null;
	}

	public void start(TestCase testCase) {
		Path dir = toTestRunPath(testCase.getName());
		try {
			if (Files.exists(dir)) {
				FileUtils.deleteDirectory(dir.toFile());
			}
		} catch (IOException e) {
			System.out.println("Error deleting dir: " + dir.toFile() + ", " + e);
		}
		try {
			if (!Files.exists(dir)) {
				Files.createDirectories(dir);
			}
			TestRun.Data.start(dir).save();
		} catch (IOException e) {
			System.out.println("Error creating dir: " + dir.toFile() + ", " + e);
		}
	}

	public boolean existTestCase(String testCaseName) {
		if (!Files.exists(Paths.get(testRunPath.toString(), testCaseName))) {
			return false;
		}
		return false;
	}

	public Path toTestRunPath(String testCaseName) {
		return Paths.get(testRunPath.toString(), testCaseName);
	}

	public Path toActualModule(String tcName, String name) {
		return Paths.get(testRunPath.toString(), tcName, name + ".actual");
	}

	public Path toRawModule(String tcName, String name) {
		return Paths.get(testRunPath.toString(), tcName, name + ".raw");
	}

	public Path toExpectedModule(String tcName, String name) {
		return Paths.get(testRunPath.toString(), tcName, name + ".expected");
	}

	public Path toInsertedModule(String tcName, String name) {
		return Paths.get(testRunPath.toString(), tcName, name + ".inserted");
	}

	public boolean isFailed(TestCase testCase) {
		try {
			List<String> modules = listModules(testCase);
			for (String module : modules) {
				if (isFailed(testCase, module)) {
					return true;
				}
			}
		} catch (IOException e) {
			// log.error(e);
		}
		return false;
	}

	private List<String> listModules(TestCase testCase) throws IOException {
		try (Stream<Path> s = Files.list(toTestRunPath(testCase.getName()))) {
			return s.filter(f -> f.toString().endsWith(".actual"))
					.map(f -> f.getFileName().toString().substring(0, f.getFileName().toString().lastIndexOf('.')))
					.collect(Collectors.toList());
		}
	}

	public boolean isFailed(TestCase testCase, String module) {
		return !isEqualByContent(toActualModule(testCase.getName(), module),
				toExpectedModule(testCase.getName(), module));
	}

	public boolean isNull(TestCase testCase, String name) {
		return !Files.exists(toActualModule(testCase.getName(), name));
	}

	private static boolean isEqualByContent(Path fileActual, Path fileExpected) {
		if (fileActual.toFile().length() != fileExpected.toFile().length()) {
			return false;
		}
		try {
			return new String(Files.readAllBytes(fileActual), Charset.defaultCharset())
					.equals(new String(Files.readAllBytes(fileExpected), Charset.defaultCharset()));
		} catch (IOException e) {
			// nada
		}
		return false;
	}

	public Data loadTestData(TestCase testCase) throws JsonSyntaxException, IOException {
		Path dir = toTestRunPath(testCase.getName());
		return TestRun.Data.load(dir);
	}

	public List<String> testRunActualContent(String testCaseName, String name) throws IOException {
		return Files.readAllLines(toActualModule(testCaseName, name), Charset.defaultCharset());
	}

	public List<String> testRunRawContent(String testCaseName, String name) throws IOException {
		return Files.readAllLines(toRawModule(testCaseName, name), Charset.defaultCharset());
	}

}
