package org.threewaves.eris.terminal.commands;

import org.threewaves.eris.engine.ICommand;
import org.threewaves.eris.engine.ICommandConsole;

import java.util.List;

//mutable and public command
public class CommandNotFoundCmd implements ICommand {
    public static final String NAME = "cnf";

    public CommandNotFoundCmd() {
    }

    @Override
    public String description() {
        return null;
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public void exec(ICommandConsole console, List<String> arguments) {
        console.errorln("Command not found");
    }

}
