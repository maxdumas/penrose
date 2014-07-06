package com.cwt.penrose.commands;

import com.cwt.penrose.Piece;
import com.cwt.penrose.PlayerManager;
import com.cwt.penrose.PlayerState;

/**
 * Created by max on 7/3/14.
 */
public class PositionCommand implements Command {
    private final int x;
    private final int y;
    private final int oldX;
    private final int oldY;
    private final Piece p;
    private final PlayerManager cpm;

    public PositionCommand(PlayerManager cpm, Piece p, int x, int y) {
        this.cpm = cpm;
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

        cpm.setState(PlayerState.SELECTING);

        return true;
    }

    @Override
    public boolean undo() {
        p.setPos(oldX, oldY);

        cpm.setState(PlayerState.POSITIONING);
        return true;
    }

    @Override
    public int getAPCost() {
        return 0;
    }

}
