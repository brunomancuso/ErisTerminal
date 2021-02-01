package org.threewaves.eris.terminal.commands;

import java.util.List;

import org.threewaves.eris.engine.ICommand;
import org.threewaves.eris.engine.ICommandConsole;
import org.threewaves.eris.engine.test_case.TestCase;
import org.threewaves.eris.engine.test_case.TestSuit;

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
			arguments.stream().map(arg -> TestCaseArgumentParser.parse(suit, arg)).flatMap(List::stream).filter(testCase -> filter(testCase, arguments)).forEach(testCase -> {
				console.notification(testCase.getFileName() + ICommandConsole.NEWLINE);
			});		
		}
	}

	private boolean filter(TestCase testCase, List<String> arguments) {
		if (arguments.size() == 0) {
			return true;
		}
		String name = testCase.getName();		
		for (String f : arguments) {
			if (name.contains(f.toLowerCase())) {
				return true;
			}
		}
		return false;
	}
}
