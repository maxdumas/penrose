package com.cwt.penrose;

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

    private static final int[][] offsetTable = new int[][] { // We count edges from 0 to five, starting at rightmost edge
            new int[]{+1, -1, +0}, // Rightmost edge
            new int[]{+1, +0, -1}, // Bottom-right edge
            new int[]{+0, +1, -1}, // Bottom-left edge
            new int[]{-1, +1, +0}, // Left edge
            new int[]{-1, +0, +1}, // Top-left edge
            new int[]{+0, -1, +1}  // Top-right edge
    };

    public PieceArchetype type;
    // It should be noted that any two of r, g, b are all that we require, but we keep
    // them all for simplicity
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
        if(PieceArchetype.NODES.contains(this.type)) return;

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

        r = MathUtils.round((sqrt3 * x - y) / (3f * radius));
        g = MathUtils.round((-sqrt3 * x - y) / (3f * radius));
        b = -(r + g);
    }

    public void setX(int nx) {
        x = nx;
        r = MathUtils.round((sqrt3 * x - y) / (3 * radius));
        g = MathUtils.floor((-sqrt3 * x - y) / (3 * radius));
        b = -(r + g);
    }

    public void setY(int ny) {
        y = ny;
        r = MathUtils.round((sqrt3 * x - y) / (3 * radius));
        g = MathUtils.round((-sqrt3 * x - y) / (3 * radius));
        b = -(r + g);
    }

    public void snapToHex() {
        x = MathUtils.round(sqrt3 * radius * (b / 2f + r));
        y = MathUtils.round(3f / 2f * radius * b);
    }

    /**
     * Given an edge e (numbered starting from 0 at the right edge going counterclockwise to 5),
     * returns whether there is a valid connection on that edge.
     *
     * @param e the edge number to test against
     * @return
     */
    public boolean isEdgePassable(int e) {
        return edgeState(e) != EdgeType.NONE;
    }

    public EdgeType edgeState(int e) {
        int i = adjustForRotation(e); // Our rotation-adjusted index
        return type.edges[i];
    }

    /**
     * Returns the open edge of this Piece that is adjacent the other Piece.
     * Returns -1 if no such edge exists, -2 if the two pieces are rooms and thus cannot be adjacent.
     * @param other
     * @return
     */
    public int adjacentEdge(Piece other) {
        if(PieceArchetype.NODES.contains(other.type) && PieceArchetype.NODES.contains(this.type))
            return -2;
        for(int e = 0; e < 6; ++e) { // Loop through all edges
            if(isEdgePassable(e)) {
                // We use the offset table to find the location of the space adjacent to our open edge
                int u = r + offsetTable[e][0], v = g + offsetTable[e][1], w = b + offsetTable[e][2];
                if(other.r == u && other.g == v && other.b == w) {
                    return e;
                }
            }
        }

        return -1;
    }

    protected int adjustForRotation(int k) {
        return ((k + rotationIndex) % 6);
    }
}
