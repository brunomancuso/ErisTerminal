package org.threewaves.eris.terminal.commands;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.threewaves.eris.engine.ICommand;
import org.threewaves.eris.engine.ICommandConsole;
import org.threewaves.eris.engine.test_case.TestCase;
import org.threewaves.eris.engine.test_case.TestSuit;
import org.threewaves.eris.util.Pair;

class ListCmd implements ICommand {
	private final TestSuit suit;

	public ListCmd(TestSuit suit) {
		this.suit = suit;
	}

	@Override
	public String description() {
		return "List all test cases";
	}

	@Override
	public String usage() {
		return name() + " <Testcase pattern>";
	}

	@Override
	public String name() {
		return "list";
	}

	@Override
	public void exec(ICommandConsole console, List<String> arguments) {
		if (arguments.size() == 0) {
			suit.list().forEach(testCase -> {
				console.notification(testCase.getFileName() + ICommandConsole.NEWLINE);
			});
		} else {
			List<TestCase> list = listTestCases(suit.list(), arguments);
			list.forEach(testCase -> {
				console.notification(testCase.getFileName() + ICommandConsole.NEWLINE);
			});
		}
	}

	public static List<TestCase> listTestCases(List<TestCase> ls, List<String> filter) {
		List<Pair<Integer, TestCase>> matches = new ArrayList<>();
		for (TestCase p : ls) {
			matches.add(new Pair<>(0, p));
		}
		for (int i = 0; i < matches.size(); i++) {
			int count = 0;
			TestCase p = matches.get(i).second;
			for (String f : filter) {
				if (p.getName().toLowerCase().contains(f.toLowerCase())) {
					count++;
				}
			}
			matches.set(i, new Pair<>(count, p));
		}
		matches.sort(new Comparator<Pair<Integer, TestCase>>() {
			@Override
			public int compare(Pair<Integer, TestCase> o1, Pair<Integer, TestCase> o2) {
				return -Integer.compare(o1.first, o2.first);
			}
		});
		return matches.stream().filter(p -> p.first >= filter.size()).map(p -> p.second).collect(Collectors.toList());
	}
}
