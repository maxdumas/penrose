package com.cwt.penrose.commands;

import com.cwt.penrose.Piece;

/**
 * Created by max on 7/3/14.
 */
public class PositionCommand implements Command {
    final int x, y, oldX, oldY;
    final Piece p;

    public PositionCommand(Piece p, int x, int y) {
        this.p = p;
        this.x = x;
        this.y = y;
        oldX = p.x;
        oldY = p.y;
    }

    @Override
    public boolean execute() {
        p.setPos(x, y);
        p.snapToHex();

        return true;
    }

    @Override
    public boolean undo() {
        p.setPos(oldX, oldY);

        return true;
    }

    @Override
    public int getAPCost() {
        return 0;
    }

}
