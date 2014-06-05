package com.cwt.penrose;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

/**
 * Created by Max on 5/29/2014.
 */
public class Piece {
    // Constants
    private static final float ROT_INTERVAL = 60.0f;
    private static final float sqrt3 = 1.732050807568877293527f;
    private static final float radius = 331 / 2f;

    private static final int[][] offsetTable = new int[][] {
            new int[]{+1, -1, +0},
            new int[]{+1, +0, -1},
            new int[]{+0, +1, -1},
            new int[]{-1, +1, +0},
            new int[]{-1, +0, +1},
            new int[]{+0, -1, +1}
    };

    public PieceArchetype type;
    public int rotationIndex, x, y, r, g, b;

    public Piece(PieceArchetype type, int x, int y) {
        setPos(x, y);
        this.type = type;

        rotationIndex = 0;
    }

    public Piece(Piece that) {
        this.type = that.type;
        this.rotationIndex = that.rotationIndex;
        this.x = that.x;
        this.y = that.y;
        this.r = that.r;
        this.g = that.g;
        this.b = that.b;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(type.getTexture(), x - type.centerX, y - type.centerY, type.centerX, type.centerY, type.width, type.height, 1.0f, 1.0f, ROT_INTERVAL * (rotationIndex % 6));
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
        x = nx;
        y = ny;

        b = MathUtils.round((2f * y) / (3f * radius));
        r = MathUtils.round((sqrt3  * x - y) / (3f * radius));
        g = MathUtils.round((-sqrt3 * x - y) / (3f * radius));
    }

    public void setX(int nx) {
        x = nx;
        r = MathUtils.floor((sqrt3  * x - y) / (3 * radius));
        g = MathUtils.floor((-sqrt3 * x - y) / (3 * radius));
        b = -(r + g);
    }

    public void setY(int ny) {
        y = ny;
        r = MathUtils.floor((sqrt3  * x - y) / (3 * radius));
        g = MathUtils.floor((-sqrt3 * x - y) / (3 * radius));
        b = -(r + g);
    }

    public void snapToHex() {
        x = MathUtils.floor(sqrt3 * radius * (b / 2f + r));
        y = MathUtils.floor(3f / 2f * radius * b);
    }

    /**
     * Given an edge e (numbered starting from 0 at the right edge going counterclockwise to 5),
     * returns whether there is a valid connection on that edge.
     *
     * @param e the edge number to test against
     * @return
     */
    public boolean isEdgePassable(int e) {
        int i = adjustForIndex(e); // Our rotation-adjusted index
        return type.edges[i] == EdgeState.ANY;
    }

    public boolean isPieceAdjacent(Piece other) {
        for(int e = 0; e < 6; ++e) { // Loop through all edges
            int k = adjustForIndex(e); // Our rotation-adjusted index
            if(type.edges[k] == EdgeState.ANY) {
                int u = r + offsetTable[k][0], v = g + offsetTable[k][1], w = b + offsetTable[k][2];
                System.out.println("Edge " + e + "(" + k + ")  at (" + r + ", " + g + ", " + b + ") is passable. Checking its offset, (" + u + ", " + v + ", " + w + "), for match.");
                if(other.r == u && other.g == v && other.b == w) {
                    System.out.println("Match found!");
                    return true;
                }
            }
        }

        return false;
    }

    private int adjustForIndex(int k) {
        if(rotationIndex == 0) return k;
        else return ((k + rotationIndex) % rotationIndex);
    }
}
