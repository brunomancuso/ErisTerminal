package org.threewaves.eris.terminal.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.threewaves.eris.engine.Config;
import org.threewaves.eris.engine.ConfigException;
import org.threewaves.eris.engine.Engine;
import org.threewaves.eris.engine.ICommand;
import org.threewaves.eris.engine.ICommandConsole;
import org.threewaves.eris.util.ShellExec;

public class CommandFactory {

	public static Map<String, ICommand> create(Config config, Engine eris, Runnable exit, Runnable cls)
			throws ConfigException {
		List<ICommand> cmds = new ArrayList<>();
		cmds.add(new ListCmd(eris.getTestSuit()));
		cmds.add(new FindCmd(eris.getTestSuit()));
		cmds.add(new NpCmd(config, eris.getTestSuit()));
		cmds.add(new InsertCodeCmd(eris));
		cmds.add(new DiffCmd(eris));
		cmds.add(new DiffWCmd(config, eris));
		cmds.add(new CmdCmd());
		cmds.add(new Cmd32Cmd());
		cmds.add(new RunCmd(eris, config.scriptEngine));
		cmds.add(new JUnitCmd(eris));
		cmds.add(new CopyCmd(eris));
		cmds.add(new ClsCmd(cls));
		cmds.add(new ActualCmd(eris));
		cmds.add(new ExitCmd(exit));
		cmds.add(new RestartCmd(exit));

		genericCommand(cmds, "ls", "ls.exe");
		genericCommand(cmds, "ll", "ls.exe", true, false, "-l");
		genericCommand(cmds, "cat", "cat.exe", false, true);
		genericCommand(cmds, "tail", "tail.exe");
		genericCommand(cmds, "touch", "touch.exe");

		cmds.add(new UsageCmd(usage(cmds)));
		eris.getFactory().createExternalCommands().forEach(c -> cmds.add(c));

		return cmds.stream().collect(Collectors.toMap(ICommand::name, c -> c));
	}

	private static String usage(List<ICommand> cmds) {
		return "Terminal commands:\r\n" + cmds.stream().map(c -> "\t" + c.usage()).collect(Collectors.joining("\r\n"));
	}

	private static void genericCommand(List<ICommand> cmds, String name, String exe, String... additionalArgs) {
		genericCommand(cmds, name, exe, true, true, additionalArgs);
	}

	private static void genericCommand(List<ICommand> cmds, String name, String exe, boolean asNotification,
			boolean auto, String... additionalArgs) {
		cmds.add(new ICommand() {
			@Override
			public void exec(ICommandConsole console, List<String> arguments) {
				try {
					ShellExec shell = new ShellExec(true, true, true);
					List<String> ls = new ArrayList<>();
					for (String s : additionalArgs) {
						if (!s.trim().isEmpty()) {
							ls.add(s.trim());
						}
					}
					for (int i = 0; i < arguments.size(); i++) {
						String a = arguments.get(i);
						if (a.contains("\\_")) {
							a = "\"" + a.replaceAll("\\\\_", " ") + "\"";
						}
						ls.add(a);
					}
					shell.execute("bin\\" + exe, ".", true, ls);
					if (!shell.getError().isEmpty()) {
						console.errorln(shell.getError());
					}
				} catch (IOException e) {
					console.errorln(e.toString());
				}
			}

			@Override
			public String description() {
				return "generic linux command";
			}

			@Override
			public String name() {
				return name;
			}

			@Override
			public String usage() {
				return name();
			}
		});
	}

	static boolean isBackgroundOption(List<String> options) {
		return options.stream().filter(o -> o.equals("&") || o.equals("-bg")).findAny().isPresent();
	}

	static boolean isJUnitOption(List<String> options) {
		return options.stream().filter(o -> o.equals("junit")).findAny().isPresent();
	}

	static boolean isThrowExceptionOption(List<String> options) {
		return options.stream().filter(o -> o.equals("throw-exception")).findAny().isPresent();
	}

}
