package com.cwt.penrose;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by Max on 5/29/2014.
 */
public enum PieceArchetype {
    CONNECTOR_LONG(new EdgeState[] {EdgeState.NONE, EdgeState.ANY, EdgeState.NONE, EdgeState.NONE, EdgeState.ANY, EdgeState.NONE}), // Long connector has edges 1 and 4 passable
    CONNECTOR_MED(new EdgeState[] {EdgeState.NONE, EdgeState.NONE, EdgeState.ANY, EdgeState.NONE, EdgeState.ANY, EdgeState.NONE}), // Short connector has edges 2 and 4 passable
    CONNECTOR_SHORT(new EdgeState[] {EdgeState.NONE, EdgeState.NONE, EdgeState.NONE, EdgeState.NONE, EdgeState.ANY, EdgeState.ANY}); // Medium connector has edges 4 and 5 passable

    private TextureRegion texture;
    public int width, height, centerX, centerY;
    protected final EdgeState[] edges;

    private PieceArchetype(EdgeState[] edges) {
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