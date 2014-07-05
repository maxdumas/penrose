package com.cwt.penrose.commands;

/**
 * Created by Max on 6/29/2014.
 */
public interface Command {
    public boolean execute();

    public boolean undo();

    public int getAPCost();
}
