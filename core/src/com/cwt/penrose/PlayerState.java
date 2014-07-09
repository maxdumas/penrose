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
        public Command handleInput(final PenroseGame game, final PlayerManager cpm) {
            Vector3 worldCoords = game.sceneCamera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0f));
            int x = (int) worldCoords.x, y = (int) worldCoords.y;

            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                HexPoint p = Piece.toHexPoint(x, y);
                if(game.ghost.getHexCoords().equals(p))
                    return new ReselectCommand(game, cpm);

                boolean fromHand = true;
                Piece selection = cpm.getHand().getPiece(Gdx.input.getX(), Gdx.input.getY());
                if(selection == null) {
                    selection = cpm.getArea().getPiece(x, y);
                    if(selection == null) return null;
                    else fromHand = false;
                }

                return new SelectNewCommand(game, cpm, selection, fromHand);

            } else if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                // Discarding piece from hand
                Piece selection = cpm.getHand().getPiece(Gdx.input.getX(), Gdx.input.getY());
                if(selection != null && !cpm.isPieceDiscarded()) // Hand was selected
                    return new DiscardCommand(cpm, selection);

                // Rotating the active piece
                HexPoint p = Piece.toHexPoint(x, y);
                if(game.ghost.getHexCoords().equals(p))
                    return new RotateCommand(game, game.ghost, 1);

                // Rotating an existing piece (originally placed in a previous phase)
                selection = cpm.getArea().getPiece(x, y);
                if(selection != null)  // Rotate
                // TODO: This should be allowed iff last piece is new or piece has not been moved this phase
                // Maybe check if phase already contains a movement action, and disallow if true?
                // Or check if the selection is the ghost, and deal with configuration case in some other way...?
                    return new Phase(
                            new SelectNewCommand(game, cpm, selection, false),
                            new PositionCommand(cpm, game.ghost, selection.x, selection.y),
                            new RotateCommand(game, selection, 1));
            }

            return null;
        }

    },
    POSITIONING {
        public Command handleInput(final PenroseGame game, final PlayerManager cpm) {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                Vector3 worldCoords = game.sceneCamera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0f));
                int x = (int) worldCoords.x, y = (int) worldCoords.y;

                return new PositionCommand(cpm, game.ghost, x, y);
            }

            return null;
        }
    }
}
