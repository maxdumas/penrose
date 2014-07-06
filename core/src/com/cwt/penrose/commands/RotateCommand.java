package com.cwt.penrose.commands;

import com.cwt.penrose.Piece;

/**
 * Created by max on 7/3/14.
 */
public class RotateCommand implements Command {
    private final int rotationAmount;
    private final Piece p;

    public RotateCommand(Piece p, int rotationAmount) {
        this.p = p;
        this.rotationAmount = rotationAmount;
    }

    @Override
    public boolean execute() {
        p.rotate(true, rotationAmount);

        return true;
    }

    @Override
    public boolean undo() {
        p.rotate(true, -rotationAmount);

        return true;
    }

    @Override
    public int getAPCost() {
        return 0;
    }

}
