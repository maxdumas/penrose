package com.cwt.penrose;

/**
 * Created by Max on 6/29/2014.
 */
public class PlaceCommand implements Command {
    private PenroseGame game;
    private PlayerManager cpm;
    private Piece newPiece;

    public PlaceCommand(PenroseGame game, PlayerManager cpm, Piece newPiece) {
        this.game = game;
        this.cpm = cpm;
        this.newPiece = newPiece;
    }

    @Override
    public boolean execute() {
        newPiece.snapToHex();
        boolean placed = cpm.getArea().addPieceIfValid(newPiece, cpm.getActivePlayer());
        if(!placed && cpm.getAP() == 1) {
            for (int i = 0; i < cpm.numPlayers; ++i)
                if(i != cpm.getActivePlayer() && cpm.getArea().addPieceIfValid(newPiece, cpm.getActivePlayer())) {
                    placed = true;
                    break;
                }
        }
        if(placed) {

        }

        return false;
    }
}
