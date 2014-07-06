package com.cwt.penrose;

import com.badlogic.gdx.Input;
import com.cwt.penrose.commands.Command;
import com.cwt.penrose.commands.NewSelectionCommand;
import com.cwt.penrose.misc.DefaultInputProcessor;

import java.util.Deque;
import java.util.LinkedList;

/**
 * Created by Max on 6/22/2014.
 */
public class PlayerManager extends DefaultInputProcessor {
    private static final int MAX_TURN_AP = 2;
    public static final int NUM_PLAYERS = 2;

    private final PlayerHand[] playerHands;
    private final Area[] areas;

    private int activePlayer = 0;
    private int usedAP = 0;
    private boolean pieceDiscarded = false;
    private PlayerState currentState = PlayerState.SELECTING;
    private final Deque<Phase> phases = new LinkedList<Phase>();
    private final PenroseGame game;

    public PlayerManager(PenroseGame game) {
        this.game = game;
        playerHands = new PlayerHand[NUM_PLAYERS];
        areas = new Area[NUM_PLAYERS];

        for(int i = 0; i < NUM_PLAYERS; ++i) {
            playerHands[i] = new PlayerHand(i);
            playerHands[i].setupPathHand(true);
            playerHands[i].setupRoomHand(true);
            areas[i] = new Area(i);
        }
    }

    public void nextPlayer() {
        activePlayer = (activePlayer + 1) % NUM_PLAYERS;
        usedAP = 0;
        pieceDiscarded = false;
        phases.clear();
        getHand().setupPathHand(true);
        System.out.println("It is now player " + (activePlayer + 1) + "'s turn");
    }

    public PlayerHand getHand() {
        return playerHands[activePlayer];
    }

    public Area getArea() {
        return areas[activePlayer];
    }

    public Area[] getAreas() {
        return areas;
    }

    public int getAP() {
        return MAX_TURN_AP - usedAP;
    }

    public boolean isPieceDiscarded() {
        return pieceDiscarded;
    }

    public void setPieceDiscarded(boolean pieceDiscarded) {
        this.pieceDiscarded = pieceDiscarded;
    }

    public PlayerState getState() {
        return currentState;
    }

    public void setState(PlayerState currentState) {
        this.currentState = currentState;
    }

    public int getActivePlayer() {
        return activePlayer;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Command c = getState().handleInput(game, this);
        if(c == null) return false;

        if(c instanceof NewSelectionCommand) { // Start new phase whenever we issue a new selection
            if(!phases.isEmpty()) {
                if(getAP() <= 0) {
                    System.out.println("You cannot use any more pieces this turn. Undo previous actions or end your turn.");
                    return false;
                }
                // A new piece is being selected and so we are trying to start a new phase.
                boolean placed = getArea().addPieceIfValid(new Piece(game.ghost), getActivePlayer());
                if (!placed && getAP() == 1) {
                    for (int i = 0; i < NUM_PLAYERS; ++i)
                        if (i != getActivePlayer() && getArea().addPieceIfValid(new Piece(game.ghost), getActivePlayer())) {
                            placed = true;
                            break;
                        }
                }
                if(!placed) { // Current phase is invalid, alert user and don't allow a new phase to be created
                    // TODO: Add button to allow moves to be undone in this case
                    System.out.println("Piece could not be placed! Reposition your piece or undo.");
                    game.ghostInvalid = true;
                    return false;
                }
            }
            // This is either the first phase of the turn, or it is otherwise valid.
            phases.add(new Phase(c));
            c.execute();
            usedAP += c.getAPCost();
        } else if(phases.peek().isBegun()) {
            // Not a selection command, and not the first command of this phase, so we'll use it
            usedAP += c.getAPCost();
            if(usedAP <= MAX_TURN_AP) { // Only execute if it doesn't put us over our maximum allowable AP.
                phases.peek().pushCommand(c);
                c.execute();
            } else System.out.println("This move is not allowed as it would cause you to go over your AP for this turn.");
        }

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Input.Keys.Z) {
            Phase p = phases.pop();
            usedAP -= p.getAPCost();
            p.undo();
            game.ghostInvalid = false;
        } else if(keycode == Input.Keys.SPACE && getAP() == 0) {
            nextPlayer();
            game.sceneCamera.position.set(getArea().getCenterX(), getArea().getCenterY(), 0f);
        }

        return true;
    }
}