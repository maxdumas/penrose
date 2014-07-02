package com.cwt.penrose;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;

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
                Piece selection = cpm.getHand().getPiece(Gdx.input.getX(), Gdx.input.getY());
                if(selection == null) {
                    selection = cpm.getArea().getPiece(x, y);
                    if(selection == null) return null;
                    else cpm.getArea().removePiece(selection);
                }
                else cpm.getHand().removePiece(selection);

                game.ghost.set(selection.type, x, y, selection.rotationIndex);
                cpm.setState(PLACING);

            } else if (Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {
                // Discard a piece or rotate an existing piece.
                Piece selection = cpm.getHand().getPiece(Gdx.input.getX(), Gdx.input.getY());
                if(selection != null && !cpm.isPieceDiscarded()) { // Hand was selected
                    cpm.getHand().pathHand.remove(selection); // Discard piece
                    cpm.setPieceDiscarded(true);
                } else {
                    selection = cpm.getArea().getPiece(x, y);
                    if(selection != null) { // Rotate
                        selection.rotate(true);
                        cpm.setLastPieceRotated(true);
                    }
                }
            }

            return null;
        }

    },
    PLACING {
        public Command handleInput(PenroseGame game, PlayerManager cpm) {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                // Attempt to place piece. If piece cannot be placed go into correcting mode
                game.ghost.setPos(Gdx.input.getX(), Gdx.input.getY());
                game.ghost.snapToHex();
                boolean placed = cpm.getArea().addPieceIfValid(new Piece(game.ghost), cpm.getActivePlayer());
                if(!placed && cpm.getAP() == 1) {
                    for (int i = 0; i < cpm.numPlayers; ++i)
                        if(i != cpm.getActivePlayer() && cpm.getArea().addPieceIfValid(new Piece(game.ghost), cpm.getActivePlayer())) {
                            placed = true;
                            break;
                        }
                }
                if(placed) {
                    cpm.changeAP(-1);
                } else { // Piece placed incorrectly, not good
                    cpm.setState(CORRECTING);
                }
            }

            return null;
        }

    },
    CORRECTING {
        public Command handleInput(PenroseGame game, PlayerManager cpm) {
            if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                // Only allow selection of invalid piece

            } else if(Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {
                // Only allow rotation of invalid piece
            }

            return null;
        }

    }
}
