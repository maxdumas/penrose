package com.cwt.penrose;

import com.cwt.penrose.commands.Command;

import java.util.Deque;
import java.util.LinkedList;

/**
 * Created by max on 7/5/14.
 */
public class Phase implements Command {
    private int totalAP = 0;
    private final Deque<Command> commands = new LinkedList<Command>();

    public Phase(Command firstCommand) {
        pushCommand(firstCommand);
    }

    public void pushCommand(Command c) {
        commands.push(c);
        totalAP += c.getAPCost();
    }

    public Command popCommand() {
        Command c = commands.pop();
        totalAP -= c.getAPCost();
        return c;
    }

    public Command peekCommand() {
        return commands.peek();
    }

    public boolean isBegun() {
        return !commands.isEmpty();
    }

    @Override
    public boolean execute() {
        for(Command c : commands)
            if(!c.execute())
                return false;

        return true;
    }

    @Override
    public boolean undo() {
        while(!commands.isEmpty())
            commands.pop().undo();
        totalAP = 0;

        return true;
    }

    @Override
    public int getAPCost() {
        return totalAP;
    }
}
