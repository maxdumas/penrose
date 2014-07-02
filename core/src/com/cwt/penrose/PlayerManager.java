package com.cwt.penrose;

/**
 * Created by Max on 6/22/2014.
 */
public class PlayerManager extends DefaultInputProcessor {
    public final int numPlayers;

    private final PlayerHand[] playerHands;
    private final Area[] areas;

    private int activePlayer = 0;
    private int currentAP = 2;
    private boolean pieceDiscarded = false, lastPieceRotated = false;
    private PlayerState currentState = PlayerState.SELECTING;
    private final PenroseGame game;

    public PlayerManager(PenroseGame game, int numPlayers) {
        this.game = game;
        playerHands = new PlayerHand[numPlayers];
        this.numPlayers = numPlayers;
        areas = new Area[numPlayers];

        for(int i = 0; i < numPlayers; ++i) {
            playerHands[i] = new PlayerHand(i);
            playerHands[i].setupPathHand(true);
            playerHands[i].setupRoomHand(true);
            areas[i] = new Area(i);
        }
    }

    public void nextPlayer() {
        activePlayer = (activePlayer + 1) % numPlayers;
        currentAP = 2;
        pieceDiscarded = false;
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

    public void changeAP(int da) {
        currentAP += da;
    }

    public int getAP() {
        return currentAP;
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
        return getState().handleInput(game, this) != null;
    }

    public boolean wasLastPieceRotated() {
        return lastPieceRotated;
    }

    public void setLastPieceRotated(boolean lastPieceRotated) {
        this.lastPieceRotated = lastPieceRotated;
    }
}