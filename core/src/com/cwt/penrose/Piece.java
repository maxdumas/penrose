package com.cwt.penrose;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.cwt.penrose.misc.HexPoint;

/**
 * Created by Max on 5/29/2014.
 */
public class Piece {
    // Constants
    private static final float ROT_INTERVAL = 60.0f;
    private static final float SQRT_3 = 1.732050807568877293527f;
    private static final float RADIUS = 331 / 2f;
    // Table containing coordinate offsets for pieces adjacent to each of a given piece's 6 edges
    private static final int[][] OFFSET_TABLE = new int[][] { // We count edges from 0 to five, starting at rightmost edge
            new int[]{+1, -1, +0}, // Rightmost edge
            new int[]{+1, +0, -1}, // Bottom-right edge
            new int[]{+0, +1, -1}, // Bottom-left edge
            new int[]{-1, +1, +0}, // Left edge
            new int[]{-1, +0, +1}, // Top-left edge
            new int[]{+0, -1, +1}  // Top-right edge
    };

    public PieceType type;
    // It should be noted that any two of r, g, b are all that we require, but we keep
    // them all for simplicity
    private int rotationIndex;
    public int x;
    public int y;
    private int r;
    private int g;
    private int b;

    public Piece(PieceType type, int x, int y) {
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

    public boolean rotate(boolean ccw) {
        return rotate(ccw, 1);
    }

    public boolean rotate(boolean ccw, int amount) {
        if(PieceType.isRoom(this)) return false;

        if(ccw)
            rotationIndex = (rotationIndex + amount) % 6;
        else rotationIndex = (rotationIndex - amount) % 6;

        return true;
    }

    public void translate(int dx, int dy) {
        setPos(x + dx, y + dy);
    }

    public void set(PieceType type, int x, int y, int rotationIndex) {
        this.type = type;
        setPos(x, y);
        this.rotationIndex = rotationIndex;
    }

    public void set(Piece that) {
        set(that.type, that.x, that.y, that.rotationIndex);
    }

    public void setPos(int nx, int ny) {
        x = nx;
        y = ny;
        calcRGB();
    }

    public void setX(int nx) {
        x = nx;
        calcRGB();
    }

    public void setY(int ny) {
        y = ny;
        calcRGB();
    }

    public void snapToHex() {
        x = MathUtils.round(SQRT_3 * RADIUS * (b / 2f + r));
        y = MathUtils.round(3f / 2f * RADIUS * b);
    }

    private void calcRGB() {
        HexPoint c = toHexPoint(x, y);
        r = c.r;
        g = c.g;
        b = c.b;
    }

    /**
     * Given an edge e (numbered starting from 0 at the right edge going counterclockwise to 5),
     * returns whether there is a valid connection on that edge.
     *
     * @param e the edge number to test against
     * @return
     */
    public boolean isEdgePassable(int e) {
        return edgeType(e) != EdgeType.NONE;
    }

    public EdgeType edgeType(int e) {
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
        if(PieceType.isRoom(other) && PieceType.isRoom(this))
            return -2;
        for(int e = 0; e < 6; ++e) { // Loop through all edges
            if(isEdgePassable(e)) {
                // We use the offset table to find the location of the space adjacent to our open edge
                int u = r + OFFSET_TABLE[e][0], v = g + OFFSET_TABLE[e][1], w = b + OFFSET_TABLE[e][2];
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

    public boolean isRoom() {
        return PieceType.isRoom(this);
    }

    public boolean isPath() {
        return PieceType.isPath(this);
    }

    public HexPoint getHexCoords() {
        return new HexPoint(r, g, b);
    }

    public static HexPoint toHexPoint(int x, int y) {
        int r = MathUtils.round((SQRT_3 * x - y) / (3f * RADIUS));
        int g = MathUtils.round((-SQRT_3 * x - y) / (3f * RADIUS));
        int b = -(r + g);

        return new HexPoint(r, g, b);
    }
}
