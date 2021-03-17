package org.threewaves.eris.engine.footprint;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.threewaves.eris.engine.test_case.TestCase;
import org.threewaves.eris.engine.test_case.TestRun;
import org.threewaves.eris.util.Pair;

public class Module {
	private static final transient Pattern PATTERN_END = Pattern.compile("^\\s*\\};\\s*$");
	private static final transient Pattern PATTERN_END_JS = Pattern.compile("^\\s*\\];\\s*$");

	private final String path;
	private final String name;
	private final List<String> groups = new ArrayList<>();
	private final List<String> patterns = new ArrayList<>();

	private transient List<Pattern> oneLine = new ArrayList<>();
	private transient List<Pair<Pattern, Pattern>> twoLine = new ArrayList<>();
	private transient int mark;

	public Module(String name, String path, String[] regular) {
		this.path = path;
		this.name = name;
		this.mark = 0;
		for (String s : regular) {
			patterns.add(s);
		}
		compile();
	}

	public void mark() {
		Path live = path != null ? Paths.get(path) : null;
		if (live != null && Files.exists(live)) {
			mark = (int) live.toFile().length();
		} else {
			mark = 0;
		}
	}

	public void compile() {
		if (oneLine == null) {
			oneLine = new ArrayList<>();
		}
		if (twoLine == null) {
			twoLine = new ArrayList<>();
		}
		try {
			for (String r : patterns) {
				if (r != null && !r.isEmpty()) {
					if (r.contains("$$")) {
						String[] tmp = r.split("\\$\\$");
						if (tmp != null && tmp.length == 2) {
							twoLine.add(new Pair<>(Pattern.compile(tmp[0]), Pattern.compile(tmp[1])));
						}
					} else {
						oneLine.add(Pattern.compile(r));
					}
				}
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	public boolean belongsTo(String group) {
		return groups != null && groups.contains(group);
	}

	public void copyTo(Path p) throws IOException {
		if (p == null) {
			return;
		}
		Path parent = p.getParent();
		if (parent == null) {
			return;
		}
		if (!Files.exists(parent)) {
			Files.createDirectories(parent);
		}
		if (mark > toPath().toFile().length()) {
			Files.write(p, "Error saving log, Log4j switched file".getBytes(Charset.defaultCharset()));
			return;
		}
		List<String> cs = FileWatch.readContent(toPath(), mark);
		writeContent(p, cs.stream().collect(Collectors.joining(System.lineSeparator())) + System.lineSeparator());
	}

	public List<String> readContent() throws IOException {
		return FileWatch.readContent(toPath(), mark);
	}

	public List<NormalDiff> assertExpected(TestRun testRun, String name, TestCase testCase) throws IOException {
		Path live = Paths.get(path);
		List<String> actual = FileWatch.readContent(live, this.mark);
		return assertExpected(testRun, name, testCase, actual);
	}

	public List<NormalDiff> assertExpected(TestRun testRun, String name, TestCase testCase, List<String> actual)
			throws IOException {
		if (testCase == null) {
			System.err.println("Test case is null");
			return Collections.emptyList();
		}
		if (!Files.exists(testCase.getPath())) {
			System.err.println("Cannot find test case: " + testCase.getPath());
		}
		List<String> expectedLines = Files.readAllLines(testCase.getPath());
		List<String> expectedContent = new ArrayList<>();
		boolean start = false;
		for (String s : expectedLines) {
			if (PATTERN_END.matcher(s).find()) {
				start = false;
			}
			if (PATTERN_END_JS.matcher(s).find()) {
				start = false;
			}

			if (start) {
				s = s.trim();
				if (s.startsWith("\"") && s.endsWith("\",")) {
					s = s.substring(1, s.length() - 2);
					expectedContent.add(s);
				}
				if (s.startsWith("\"") && s.endsWith("\"")) {
					s = s.substring(1, s.length() - 2);
					expectedContent.add(s);
				}

			}
			if (s.contains("module_" + name + " = new String[] {")) {
				start = true;
			}
			if (s.contains("module_" + name + " = [")) {
				start = true;
			}

		}
		return assertExpected(testRun, name, testCase.getName(), expectedContent, actual);
	}

	private List<NormalDiff> assertExpected(TestRun testRun, String name, String testCaseName,
			List<String> expectedContent, List<String> actual) throws IOException {
		List<String> raw = new ArrayList<>();
		raw.addAll(actual);
		applyEscapePatternTo(actual);
		actual = actual.stream().map(s -> s.replace("\"", "\\\"")).collect(Collectors.toList());
		applyEscapePatternTo(expectedContent);

		Path fileRaw = testRun.toRawModule(testCaseName, name);
		Path fileActual = testRun.toActualModule(testCaseName, name);
		Path fileExpected = testRun.toExpectedModule(testCaseName, name);

		String sraw = join(raw);
		String sa = join(actual);
		String se = join(expectedContent);

		writeContent(fileRaw, sraw);
		writeContent(fileActual, sa);
		writeContent(fileExpected, se);
		if (sa.equals(se)) {
			return Collections.emptyList();
		}
		return new Diff().ndiff(fileExpected, fileActual);
	}

	private String join(List<String> list) {
		StringBuffer buffer = new StringBuffer();
		for (String line : list) {
			buffer.append(line + Footprint.NEWLINE);
		}
		return buffer.toString();
	}

	public void applyEscapePatternTo(List<String> actual) {
		Pattern secondLine = null;
		for (int i = 0; i < actual.size(); i++) {
			if (secondLine != null) {
				actual.set(i, match(secondLine, actual.get(i)));
				secondLine = null;
			}
			actual.set(i, filter(actual.get(i)));
			secondLine = match(actual.get(i));
		}
	}

	private static void writeContent(Path file, String ss) throws IOException {
		try (OutputStreamWriter ws = new OutputStreamWriter(new FileOutputStream(file.toFile()),
				Charset.defaultCharset())) {
			ws.write(ss);
		}
	}

	public Pattern match(String line) {
		if (twoLine == null || oneLine == null) {
			compile();
		}
		if (twoLine != null) {
			for (Pair<Pattern, Pattern> p : twoLine) {
				if (p != null) {
					Matcher matcher = p.first.matcher(line);
					if (matcher.find()) {
						return p.second;
					}
				}
			}
		}
		return null;
	}

	public String filter(String line) {
		if (twoLine == null || oneLine == null) {
			compile();
		}
		for (Pattern pattern : oneLine) {
			line = match(pattern, line);
		}
		return line;
	}

	
	private String match(Pattern pattern, String line) {
		Matcher matcher = pattern.matcher(line);
		if (matcher.find()) {
			if (matcher.groupCount() == 0) {
				// int matchedLength = matcher.end() - matcher.start();
				String replaceStr = ".";// padLeft("", matchedLength, ".");
				line = line.substring(0, matcher.start()) + replaceStr
						+ line.substring(matcher.end());
			} else {
				int groupCount = matcher.groupCount();
				for (int j = 0; j < groupCount; j++) {
					int i = groupCount - j - 1;
					// String group = matcher.group(i + 1);
					String replaceStr = ".";// padLeft("", group.length(), ".");
					int start = matcher.start(i + 1);
					int end = matcher.end(i + 1);
					line = line.substring(0, start) + replaceStr + line.substring(end);
				}
			}
		}
		return line;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	public static Module find(List<Module> modules, String module) {
		for (Module m : modules) {
			if (m.name.equals(module)) {
				return m;
			}
		}
		return null;
	}

	public Path toPath() {
		if (path != null) {
			return Paths.get(path);
		}
		return null;
	}

	public static List<String> toStringList(List<Module> modules) {
		return modules.stream().map(Module::getName).collect(Collectors.toList());
	}
}
