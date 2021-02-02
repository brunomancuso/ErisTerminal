package org.threewaves.eris.terminal.commands;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.threewaves.eris.engine.footprint.Module;
import org.threewaves.eris.engine.footprint.Modules;
import org.threewaves.eris.engine.test_case.TestCase;
import org.threewaves.eris.engine.test_case.TestSuit;
import org.threewaves.eris.util.Format;

class TestCaseArgumentParser {
	private final TestSuit suit;
	private final Modules avaibleModules;
	private final List<Module> modules = new ArrayList<>();
	private final List<TestCase> testCases = new ArrayList<>();

	public TestCaseArgumentParser(TestSuit suit, Modules modules) {
		this.suit = suit;
		this.avaibleModules = modules;
	}

	public List<TestCase> testCases() {
		return testCases;
	}

	public List<Module> modules() {
		return modules;
	}

	public void process(List<String> options) {
		testCases.clear();
		modules.clear();
		options.forEach(o -> {
			List<TestCase> ls = parse(suit, o);
			testCases.addAll(ls);
			Optional<Module> m = avaibleModules.find(o);
			if (m.isPresent()) {
				modules.add(m.get());
			}
		});
		if (modules.size() == 0) {
			modules.addAll(avaibleModules.getList());
		}
		if (testCases.size() == 0) {
			testCases.addAll(suit.list());
		}
	}

	public static List<TestCase> parse(TestSuit suit, String f) {
		List<TestCase> testCases = suit.list();
		Path p = Paths.get(f);
		if (p == null) {
			return Collections.emptyList();
		}
		Path parent = p.getParent();
		Path fileName = p.getFileName();
		if (fileName == null) {
			return Collections.emptyList();
		}
		if (parent == null) {
			parent = Paths.get("..");
		}
		if (fileName.toString().contains(TestCase.PREFIX_NAME)) {
			return Collections.singletonList(TestCase.of(p));
		} else if (fileName.toString().contains(TestCase.PREFIX_NAME_SHORT)) {
			return Collections.singletonList(TestCase.of(p));
		} else if (fileName.toString().contains("-") && !fileName.toString().contains(TestCase.PREFIX_NAME)
				&& !fileName.toString().contains(TestCase.PREFIX_NAME_SHORT)) {
			int index = fileName.toString().indexOf('-');
			String s = fileName.toString().substring(0, index);
			String e = fileName.toString().substring(index + 1);
			if (Format.parseInt(s) > 0 && Format.parseInt(e) > 0) {
				return IntStream.rangeClosed(Format.parseInt(s), Format.parseInt(e)).boxed()
						.map(n -> findByNumber(testCases, n)).filter(tc -> tc.isPresent()).map(o -> o.get())
						.collect(Collectors.toList());
			} else {
				// System.out.println("Test case(s) not found: " + f);
			}
		} else if (parent != null && Format.parseInt(fileName.toString(), 0) > 0) {
			int testCase = Format.parseInt(fileName.toString(), 0);
			Optional<TestCase> tc = findByNumber(testCases, testCase);
			if (tc.isPresent()) {
				return Collections.singletonList(tc.get());
			}
		}
		return Collections.emptyList();
	}

	private static Optional<TestCase> findByNumber(List<TestCase> testCases, int testCase) {
		return testCases.stream().filter(t -> t.getNumber() == testCase).findAny();
	}

}
