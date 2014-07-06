package com.cwt.penrose.commands;

import com.cwt.penrose.PenroseGame;
import com.cwt.penrose.Piece;
import com.cwt.penrose.PlayerManager;
import com.cwt.penrose.PlayerState;

/**
 * Created by max on 7/3/14.
 */
public class SelectNewCommand implements Command {
    private final boolean fromHand;
    private final PenroseGame game;
    private final PlayerManager cpm;
    private final Piece selection;

    public SelectNewCommand(PenroseGame game, PlayerManager cpm, Piece selection, boolean fromHand) {
        this.fromHand = fromHand;
        this.game = game;
        this.cpm = cpm;
        this.selection = selection;
    }

    @Override
    public boolean execute() {
        if(fromHand && !cpm.getHand().removePiece(selection)) return false;
        else if (!fromHand && !cpm.getArea().removePiece(selection)) return false;

        game.ghost.set(selection);
        game.ghostVisible = true;

        cpm.setState(PlayerState.POSITIONING);
        return true;
    }

    @Override
    public boolean undo() {
        if(fromHand) cpm.getHand().addPiece(selection);
        else cpm.getArea().addPieceIfValid(selection, cpm.getActivePlayer());

        game.ghostVisible = false;
        cpm.setState(PlayerState.SELECTING);

        return true;
    }

    @Override
    public int getAPCost() {
        return (fromHand) ? 1 : 2;
    }

}
