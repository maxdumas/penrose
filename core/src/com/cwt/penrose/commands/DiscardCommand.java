package com.cwt.penrose.commands;

import com.cwt.penrose.Piece;
import com.cwt.penrose.PlayerManager;

/**
 * Created by max on 7/3/14.
 */
public class DiscardCommand implements Command {
    private final PlayerManager cpm;
    private final Piece selection;

    public DiscardCommand(PlayerManager cpm, Piece selection) {
        this.cpm = cpm;
        this.selection = selection;
    }

    @Override
    public boolean execute() {
        if(!cpm.getHand().removePiece(selection)) return false; // Discard piece
        cpm.setPieceDiscarded(true);

        return true;
    }

    @Override
    public boolean undo() {
        cpm.getHand().addPiece(selection);
        cpm.setPieceDiscarded(false);

        return true;
    }

    @Override
    public int getAPCost() {
        return 0;
    }

}
