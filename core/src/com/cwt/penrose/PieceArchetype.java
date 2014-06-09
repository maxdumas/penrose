package com.cwt.penrose;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by Max on 5/29/2014.
 */
public enum PieceArchetype {
    CONNECTOR_LONG(new EdgeType[] {EdgeType.NONE, EdgeType.ANY, EdgeType.NONE, EdgeType.NONE, EdgeType.ANY, EdgeType.NONE}), // Long connector has edges 1 and 4 passable
    CONNECTOR_MED(new EdgeType[] {EdgeType.NONE, EdgeType.NONE, EdgeType.ANY, EdgeType.NONE, EdgeType.ANY, EdgeType.NONE}), // Medium connector has edges 2 and 4 passable
    CONNECTOR_SHORT(new EdgeType[] {EdgeType.NONE, EdgeType.NONE, EdgeType.NONE, EdgeType.NONE, EdgeType.ANY, EdgeType.ANY}), // Short connector has edges 4 and 5 passable

    // All NODE_IN_X have one in-edge which is located at edge X. They are all rotations of one another.
    NODE_IN_0(new EdgeType[] {EdgeType.IN, EdgeType.NONE, EdgeType.OUT, EdgeType.NONE, EdgeType.OUT, EdgeType.NONE}), // Corresponds to sprite node_in_C
    NODE_IN_1(NODE_IN_0.edges, 1), // Corresponds to sprite node_in_D
    NODE_IN_2(NODE_IN_0.edges, 2), // Corresponds to sprite node_in_E
    NODE_IN_3(NODE_IN_0.edges, 3), // Corresponds to sprite node_in_F
    NODE_IN_4(NODE_IN_0.edges, 4), // Corresponds to sprite node_in_A
    NODE_IN_5(NODE_IN_0.edges, 5), // Corresponds to sprite node_in_B

    // All NODE_OUT_X have one out-edge which is located at edge X. They are all rotations of one another.
    NODE_OUT_0(new EdgeType[] {EdgeType.OUT, EdgeType.NONE, EdgeType.IN, EdgeType.NONE, EdgeType.IN, EdgeType.NONE}), // Corresponds to sprite node_out_C
    NODE_OUT_1(NODE_IN_0.edges, 1), // Corresponds to sprite node_out_D
    NODE_OUT_2(NODE_IN_0.edges, 2), // Corresponds to sprite node_out_E
    NODE_OUT_3(NODE_IN_0.edges, 3), // Corresponds to sprite node_out_F
    NODE_OUT_4(NODE_IN_0.edges, 4), // Corresponds to sprite node_out_A
    NODE_OUT_5(NODE_IN_0.edges, 5); // Corresponds to sprite node_out_B

    public static final HashSet<PieceArchetype> CONNECTORS = new HashSet<PieceArchetype>(Arrays.asList(new PieceArchetype[] {CONNECTOR_LONG, CONNECTOR_MED, CONNECTOR_SHORT}));
    public static final HashSet<PieceArchetype> IN_NODES = new HashSet<PieceArchetype>(
            Arrays.asList(new PieceArchetype[] {NODE_IN_0, NODE_IN_1, NODE_IN_2, NODE_IN_3, NODE_IN_4, NODE_IN_5}));
    public static final HashSet<PieceArchetype> OUT_NODES = new HashSet<PieceArchetype>(
            Arrays.asList(new PieceArchetype[] {NODE_OUT_0, NODE_OUT_1, NODE_OUT_2, NODE_OUT_3, NODE_OUT_4, NODE_OUT_5}));
    public static final HashSet<PieceArchetype> NODES = new HashSet<PieceArchetype>(
            Arrays.asList(new PieceArchetype[] {NODE_IN_0, NODE_IN_1, NODE_IN_2, NODE_IN_3, NODE_IN_4, NODE_IN_5, NODE_OUT_0, NODE_OUT_1, NODE_OUT_2, NODE_OUT_3, NODE_OUT_4, NODE_OUT_5}));

    private TextureRegion texture;
    public int width, height, centerX, centerY;
    protected final EdgeType[] edges;

    private PieceArchetype(EdgeType[] edges) {
        this.edges = edges;
    }

    private PieceArchetype(EdgeType[] edges, int rot) {
        this.edges = new EdgeType[6];
        for(int i = 0; i < 6; ++i) {
            int j = (i + rot) % 6;
            this.edges[j] = edges[i];
        }
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