package com.cwt.penrose.commands;

import com.cwt.penrose.PenroseGame;
import com.cwt.penrose.Piece;
import com.cwt.penrose.PlayerManager;
import com.cwt.penrose.PlayerState;

/**
 * Created by max on 7/3/14.
 */
public class NewSelectionCommand implements Command {
    final boolean fromHand;
    final PenroseGame game;
    final PlayerManager cpm;
    final Piece selection;

    public NewSelectionCommand(PenroseGame game, PlayerManager cpm, Piece selection, boolean fromHand) {
        this.fromHand = fromHand;
        this.game = game;
        this.cpm = cpm;
        this.selection = selection;
    }

    @Override
    public boolean execute() {
        if(fromHand && !cpm.getHand().removePiece(selection)) return false;
        else if (!cpm.getArea().removePiece(selection)) return false;

        game.ghost.set(selection.type, selection.x, selection.y, selection.rotationIndex);
        cpm.setState(PlayerState.POSITIONING);
        game.ghostVisible = true;
        return true;
    }

    @Override
    public boolean undo() {
        if(fromHand) cpm.getHand().addPiece(selection);
        else cpm.getArea().addPieceIfValid(selection, cpm.getActivePlayer());

        game.ghostVisible = false;


        return true;
    }

    @Override
    public int getAPCost() {
        return (fromHand) ? 1 : 2;
    }

}
