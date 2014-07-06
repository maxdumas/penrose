package com.cwt.penrose.commands;

import com.cwt.penrose.PenroseGame;
import com.cwt.penrose.Piece;

/**
 * Created by max on 7/3/14.
 */
public class RotateCommand implements Command {
    private final int rotationAmount;
    private final PenroseGame game;
    private final Piece p;

    public RotateCommand(PenroseGame game, Piece p, int rotationAmount) {
        this.game = game;
        this.p = p;
        this.rotationAmount = rotationAmount;
    }

    @Override
    public boolean execute() {
        p.rotate(true, rotationAmount);

        game.ghostInvalid = false;
        game.ghostVisible = true;

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
