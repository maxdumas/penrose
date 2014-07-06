package com.cwt.penrose;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import com.cwt.penrose.commands.*;
import com.cwt.penrose.misc.HexPoint;

/**
 * Created by Max on 6/22/2014.
 */
public enum PlayerState implements State<PlayerManager> {
    SELECTING {
        @Override
        public Command handleInput(PenroseGame game, PlayerManager cpm) {
            Vector3 worldCoords = game.sceneCamera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0f));
            int x = (int) worldCoords.x, y = (int) worldCoords.y;

            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                HexPoint p = Piece.toHexPoint(x, y);
                if(game.ghost.getHexCoords().equals(p))
                    return new ReSelectionCommand(game, cpm);

                boolean fromHand = true;
                Piece selection = cpm.getHand().getPiece(Gdx.input.getX(), Gdx.input.getY());
                if(selection == null) {
                    selection = cpm.getArea().getPiece(x, y);
                    if(selection == null) return null;
                    else fromHand = false;
                }

                return new NewSelectionCommand(game, cpm, selection, fromHand);

            } else if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                // Discard a piece or rotate an existing piece.
                Piece selection = cpm.getHand().getPiece(Gdx.input.getX(), Gdx.input.getY());
                if(selection != null && !cpm.isPieceDiscarded()) // Hand was selected
                    return new DiscardCommand(cpm, selection);
                else {
                    selection = cpm.getArea().getPiece(x, y);
                    if(selection != null && selection == game.ghost)  // Rotate
                    // TODO: This should be allowed iff last piece is new or piece has not been moved this phase
                    // Maybe check if phase already contains a movement action, and disallow if true?
                    // Or check if the selection is the ghost, and deal with configuration case in some other way...?
                        return new RotationCommand(selection, 1);
                }
            }

            return null;
        }

    },
    POSITIONING {
        public Command handleInput(PenroseGame game, PlayerManager cpm) {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                Vector3 worldCoords = game.sceneCamera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0f));
                int x = (int) worldCoords.x, y = (int) worldCoords.y;

                return new PositionCommand(cpm, game.ghost, x, y);
            }

            return null;
        }
    }
}
