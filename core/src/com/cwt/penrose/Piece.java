package com.cwt.penrose;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Max on 5/29/2014.
 */
public class Piece {
    private static final float ROT_INTERVAL = 60.0f;

    public final PieceArchetype type;
    public int rotationIndex, x, y;

    public Piece(PieceArchetype type, int x, int y) {
        this.x = x;
        this.y = y;
        this.type = type;

        rotationIndex = 0;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(type.getTexture(), x, y, type.centerX, type.centerY, type.width, type.height, 1.0f, 1.0f, ROT_INTERVAL * (rotationIndex % 6));
    }

    /**
     * Given an edge e (numbered starting from 0 at the right edge going counterclockwise to 5),
     * returns whether there is a valid connection on that edge.
     * @param e the edge number to test against
     * @return
     */
    public boolean isEdgePassable(int e) {
        return ((type.edges - rotationIndex) % rotationIndex) >> e != 0;
    }
}
