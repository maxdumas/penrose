package com.cwt.penrose;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by Max on 5/29/2014.
 */
public enum PieceArchetype {
    CONNECTOR_OUTER(1 << 0| 1 << 5), // Outer connector has edges 0 and 5 passable
    CONNECTOR_INNER(1 << 1 | 1 << 2), // Inner connector has edges 1 and 2 passable
    CONNECTOR_HOOK(1 << 2 | 1 << 4); // Hook connector has edges 2 and 4 passable

    private TextureRegion texture;
    public int width, height, centerX, centerY;
    public final int edges;

    private PieceArchetype(int edges) {
        this.edges = edges;
    }

    public void setTexture(TextureRegion texture) {
        this.texture = texture;

        width = texture.getRegionWidth();
        height = texture.getRegionHeight();
        centerX = width / 2;
        centerY = height / 2;
    }

    public TextureRegion getTexture() { return this.texture; }
}