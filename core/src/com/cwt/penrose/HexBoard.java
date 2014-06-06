package com.cwt.penrose;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Max on 6/5/2014.
 */
public class HexBoard {
    final List<Piece> pieces = new ArrayList<Piece>();

    public void draw(SpriteBatch batch) {
        for(Piece piece : pieces)
            piece.draw(batch);
    }

    // returns false if placement failed
    public boolean placePiece(Piece newPiece) {
        if (pieces.isEmpty()) {
            pieces.add(new Piece(newPiece));
            return true;
        }
        else
            for (Piece p : pieces)
                if (p.isPieceAdjacent(newPiece) && newPiece.isPieceAdjacent(p)) {
                    pieces.add(new Piece(newPiece));
                    return true;
                }

        return false;
    }
}
