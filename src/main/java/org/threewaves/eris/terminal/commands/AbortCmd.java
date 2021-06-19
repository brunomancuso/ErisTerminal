package org.threewaves.eris.terminal.commands;

import org.threewaves.eris.engine.ICommand;
import org.threewaves.eris.engine.ICommandConsole;

import java.util.List;

public class AbortCmd implements ICommand {

    public AbortCmd() {
    }

    @Override
    public String description() {
        return "Abort current command";
    }

    @Override
    public String name() {
        return "CTRL+C";
    }

    @Override
    public void exec(ICommandConsole console, List<String> options) {
    }

    @Override
    public String usage() {
        return "CTRL+C";
    }
}
