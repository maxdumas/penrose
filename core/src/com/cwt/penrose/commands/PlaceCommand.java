package com.cwt.penrose.commands;

import com.cwt.penrose.Piece;
import com.cwt.penrose.PlayerManager;

/**
 * Created by max on 7/6/14.
 */
public class PlaceCommand implements Command {
    private final PlayerManager cpm;
    private final Piece selection;
    private int successIndex = -1; // Stores player index of area in which selection was placed

    public PlaceCommand(PlayerManager cpm, Piece selection) {
        this.cpm = cpm;
        this.selection = selection;
    }

    @Override
    public boolean execute() {
        boolean placed = cpm.getArea().addPieceIfValid(selection, cpm.getActivePlayer());
        if (!placed && cpm.getAP() == 1) {
            for (int i = 0; i < cpm.NUM_PLAYERS; ++i)
                if (i != cpm.getActivePlayer() && cpm.getArea().addPieceIfValid(selection, cpm.getActivePlayer())) {
                    placed = true;
                    successIndex = i;
                    break;
                }
        } else if(placed) successIndex = cpm.getActivePlayer();

        return placed;
    }

    @Override
    public boolean undo() {
        return cpm.getAreas()[successIndex].removePiece(selection);
    }

    @Override
    public int getAPCost() {
        return 0;
    }
}
