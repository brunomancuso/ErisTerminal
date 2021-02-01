package org.threewaves.eris.engine;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.threewaves.eris.terminal.commands.CommandFactory;

public class BatchConsole {
	
	private final Map<String, ICommand> commands;
	private final ICommandConsole console;
			
	public BatchConsole(Config config, ICommandConsole console) throws ConfigException {
		Engine eris = new Engine(config.createFactory(), config.createTestSuit());
		eris.initialize();
		this.commands = CommandFactory.create(config, eris, null, null);
		this.console = console;
	}

	private static void validateDirectory() {
		if (!Files.exists(Paths.get("eris.yaml"))) {
			throw new RuntimeException("Unit test need to run in runtime directory");
		}
	}

	public static void main(String[] args) throws ConfigException {		
		validateDirectory();
		Config config = Config.create();
		BatchConsole console = new BatchConsole(config, new CommandConsole(System.out, System.err, null, null));
		console.run(args);
	}
	
	public void run(String[] args) {
		if (args.length == 0) {
			usage();
			return;
		}
		List<String> arguments = Arrays.asList(args).subList(1, args.length);
		commands.values().stream().filter(c -> c.name().equals(args[0])).forEach(c -> exec(c, console, Collections.unmodifiableList(arguments)));		
	}

	private void exec(ICommand c, ICommandConsole console, List<String> options) {
		c.exec(console, options);
	}

	private void usage() {		
		console.notification("Options:");
		commands.values().forEach(c -> console.notification(ICommandConsole.TAB + c.name() + ": " + c.description() + ICommandConsole.NEWLINE));
	}

	public static void run(int testCase) throws ConfigException {
		main(new String[] {"run", "throw-exception", Integer.toString(testCase) } );
	}
	
}
