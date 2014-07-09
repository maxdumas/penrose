package com.cwt.penrose;

import com.cwt.penrose.commands.Command;

import java.util.*;

/**
 * Created by max on 7/5/14.
 */
public class Phase implements Command {
    private int totalAP = 0;
    private final List<Command> commands = new ArrayList<Command>();

    public Phase(Command firstCommand) {
        pushCommand(firstCommand);
    }

    public Phase(Command... commands) {
        for(Command c : commands)
            pushCommand(c);
    }

    public void pushCommand(Command c) {
        commands.add(c);
        totalAP += c.getAPCost();
    }

    public Command popCommand() {
        Command c = commands.remove(commands.size() - 1);
        totalAP -= c.getAPCost();
        return c;
    }

    public Command peekCommand() {
        return commands.get(commands.size() - 1);
    }

    public boolean isBegun() {
        return !commands.isEmpty();
    }

    @Override
    public boolean execute() {
        boolean failed = false;
        int i = 0;
        for(; i < commands.size(); ++i) {
            Command c = commands.get(i);
            if (!c.execute())
                failed = true;
        }
        if(failed)
            while(--i >= 0)
                commands.get(i).undo();

        return !failed;
    }

    @Override
    public boolean undo() {
        while(!commands.isEmpty())
            popCommand().undo();

        return true;
    }

    @Override
    public int getAPCost() {
        return totalAP;
    }
}
