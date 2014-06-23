package com.cwt.penrose;

/**
 * Created by Max on 6/22/2014.
 */
public class PlayerManager {
    final PlayerHand[] playerHands;

    public PlayerManager(int numPlayers) {
        playerHands = new PlayerHand[numPlayers];
    }
}
