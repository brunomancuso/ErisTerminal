package org.threewaves.eris.engine.test_case;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.threewaves.eris.util.Pad;

public class TestCaseInsertCode {
	private final TestRun testRun;

	public TestCaseInsertCode() {
		this.testRun = new TestRun();
	}

	public void insert(TestCase testCase, List<String> modules) throws IOException {
		List<String> ls = new ArrayList<>();
		for (String m : modules) {
			if (insert(testCase.getPath(), m)) {
				ls.add(m);
			}
		}
		if (ls.size() > 0) {
			System.out.println(Pad.right(testCase.getName(), 40) + ": " + ls);
		}
	}

	private boolean isEqualByContent(Path fileActual, Path fileExpected) {
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

	private boolean insert(Path p, String name) throws IOException {
		if (p == null) {
			return false;
		}
		String tabs = "\t\t\t";
		String tabs1 = "\t";
		Path fileName = p.getFileName();
		boolean isJUnit = fileName != null && fileName.toString().endsWith(".java");
		if (!isJUnit) {
			tabs = "\t";
			tabs1 = "";
		}
		boolean insert = true;
		if (fileName != null && fileName.toString() != null && fileName.toString().lastIndexOf('.') > 0) {
			String tcName = fileName.toString().substring(0, fileName.toString().lastIndexOf('.'));
			Path pathActual = testRun.toActualModule(tcName, name);
			Path pathExpected = testRun.toExpectedModule(tcName, name);
			if (isEqualByContent(pathActual, pathExpected)) {
				insert = false;
			}
			List<String> content = Files.readAllLines(p);
			boolean found = false;
			for (int i = 0; i < content.size(); i++) {
				if (isModuleHeader(isJUnit, name, content.get(i))) {
					if (!insert) {
						return false;
					}
					found = true;
					int j = -1;
					if (isJUnit) {
						j = getEqualNext(content, i + 1, "};");
					} else {
						j = getEqualNext(content, i + 1, "];");
					}
					if (!Files.exists(testRun.toInsertedModule(tcName, name))) {
						Files.createFile(testRun.toInsertedModule(tcName, name));
					}
					if (j > 0) {
						remove(content, i + 1, j - 1);
						List<String> actual = Files.exists(pathActual) ? Files.readAllLines(pathActual)
								: new ArrayList<>();
						for (int k = 0; k < actual.size(); k++) {
							content.add(i + 1, tabs + "\"" + actual.get(actual.size() - k - 1) + "\",");
						}
					} else {
						// log.error("Cannot find end token");
					}
					break;
				}
			}
			if (!found) {
				for (int i = 0; i < content.size(); i++) {
					if (i > 0) {
						if (isTestHeader(isJUnit, content.get(i))) {
							List<String> actual = Files.exists(pathActual) ? Files.readAllLines(pathActual)
									: new ArrayList<>();
							if (actual != null) {
								if (isJUnit) {
									content.add(i - 1, "");
									content.add(i,
											tabs1 + "public final String[] module_" + name + " = new String[] {");
								} else {
									content.add(i - 1, "");
									content.add(i, tabs1 + "var module_" + name + " = [");
								}
								for (int k = 0; k < actual.size(); k++) {
									content.add(i + 1, tabs + "\"" + actual.get(actual.size() - k - 1) + "\",");
								}
								if (isJUnit) {
									content.add(i + 1 + actual.size() + 1, tabs1 + "};");
									content.add(i + 2 + actual.size() + 1, "");
								} else {
									content.add(i + 1 + actual.size() + 1, tabs1 + "];");
									content.add(i + 2 + actual.size() + 1, "");
								}
							}
							break;
						}
					}
				}

			}
			String s = content.stream().collect(Collectors.joining("\r\n"));
			if (s.length() > 0) {
				Files.write(p, s.getBytes(Charset.defaultCharset()));
				return true;
			}
		}
		return false;
	}

	private boolean isModuleHeader(boolean isJUnit, String name, String content) {
		if (isJUnit) {
			if (content.trim().startsWith("private final String[] module_" + name)
					|| content.trim().startsWith("public final String[] module_" + name)) {
				return true;
			}
		} else {
			if (content.trim().startsWith("var module_" + name)) {
				return true;
			}
		}

		return false;
	}

	private boolean isTestHeader(boolean isJUnit, String content) {
		if (!isJUnit) {
			if (content.trim().startsWith("function test() {")) {
				return true;
			}
		} else {
			if (content.trim().equals("@Test")) {
				return true;
			}
		}

		return false;
	}

	private static void remove(List<String> content, int start, int end) {
		for (int i = 0; i < end - start + 1; i++) {
			content.remove(start);
		}
	}

	private static int getEqualNext(List<String> content, int i, String str) {
		for (int j = i; j < content.size(); j++) {
			if (content.get(j).trim().equals(str)) {
				return j;
			}
		}
		return -1;
	}
}
