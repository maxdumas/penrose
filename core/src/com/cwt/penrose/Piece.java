package com.cwt.penrose;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

/**
 * Created by Max on 5/29/2014.
 */
public class Piece {
    // Constants
    private static final float ROT_INTERVAL = 60.0f;
    private static final float cos30 = 0.866025403784438646763723f;
    private static final float sin30 = 0.5f;
    private static final float radius = 331 / 2f;
    private static final float xOff = cos30 * radius * 2f;
    private static final float yOff = sin30 * radius * 3f;

    private static final int[][] offsetTable = new int[][] {
            new int[]{+1, +0},
            new int[]{+1, -1},
            new int[]{+0, -1},
            new int[]{-1, +0},
            new int[]{+0, +1},
            new int[]{+1, +1}
    };

    public PieceArchetype type;
    public int rotationIndex, x, y, i, j;

    public Piece(PieceArchetype type, int x, int y) {
        this.x = x;
        this.y = y;
        this.type = type;

        rotationIndex = 0;
    }

    public Piece(Piece that) {
        this.type = that.type;
        this.rotationIndex = that.rotationIndex;
        this.x = that.x;
        this.y = that.y;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(type.getTexture(), x, y, type.centerX, type.centerY, type.width, type.height, 1.0f, 1.0f, ROT_INTERVAL * (rotationIndex % 6));
    }

    public void rotate(boolean ccw) {
        if(ccw)
            rotationIndex = (rotationIndex + 1) % 6;
        else rotationIndex = (rotationIndex - 1) % 6;
    }

    public void translate(int dx, int dy) {
        setPos(x + dx, y + dy);
    }

    public void setPos(int nx, int ny) {
        setX(nx);
        setY(ny);
    }

    public void setX(int nx) {
        x = nx;
        i = MathUtils.floor(x / xOff);
    }

    public void setY(int ny) {
        y = ny;
        j = MathUtils.floor(y / yOff);
    }

    public void snapToHex() {
        x = (int) ((i + j % 2 / 2f)* xOff);
        y = (int) (j * yOff);
    }

    /**
     * Given an edge e (numbered starting from 0 at the right edge going counterclockwise to 5),
     * returns whether there is a valid connection on that edge.
     *
     * @param e the edge number to test against
     * @return
     */
//    public boolean isEdgePassable(int e) {
//        return ((type.edges - rotationIndex) % rotationIndex) >> e != 0;
//    }
}
