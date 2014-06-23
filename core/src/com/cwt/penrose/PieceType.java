package com.cwt.penrose;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by Max on 5/29/2014.
 */
public enum PieceType {
    PATH_LONG(new EdgeType[] {EdgeType.NONE, EdgeType.ANY, EdgeType.NONE, EdgeType.NONE, EdgeType.ANY, EdgeType.NONE}), // Long connector has edges 1 and 4 passable
    PATH_MED(new EdgeType[] {EdgeType.NONE, EdgeType.NONE, EdgeType.ANY, EdgeType.NONE, EdgeType.ANY, EdgeType.NONE}), // Medium connector has edges 2 and 4 passable
    PATH_SHORT(new EdgeType[] {EdgeType.NONE, EdgeType.NONE, EdgeType.NONE, EdgeType.NONE, EdgeType.ANY, EdgeType.ANY}), // Short connector has edges 4 and 5 passable

    // All ROOM_IN_X have one in-edge which is located at edge X. They are all rotations of one another.
    ROOM_IN_0(new EdgeType[] {EdgeType.IN, EdgeType.NONE, EdgeType.OUT, EdgeType.NONE, EdgeType.OUT, EdgeType.NONE}), // Corresponds to sprite node_in_C
    ROOM_IN_1(ROOM_IN_0.edges, 1), // Corresponds to sprite node_in_D
    ROOM_IN_2(ROOM_IN_0.edges, 2), // Corresponds to sprite node_in_E
    ROOM_IN_3(ROOM_IN_0.edges, 3), // Corresponds to sprite node_in_F
    ROOM_IN_4(ROOM_IN_0.edges, 4), // Corresponds to sprite node_in_A
    ROOM_IN_5(ROOM_IN_0.edges, 5), // Corresponds to sprite node_in_B

    // All ROOM_OUT_X have one out-edge which is located at edge X. They are all rotations of one another.
    ROOM_OUT_0(new EdgeType[] {EdgeType.OUT, EdgeType.NONE, EdgeType.IN, EdgeType.NONE, EdgeType.IN, EdgeType.NONE}), // Corresponds to sprite node_out_C
    ROOM_OUT_1(ROOM_IN_0.edges, 1), // Corresponds to sprite node_out_D
    ROOM_OUT_2(ROOM_IN_0.edges, 2), // Corresponds to sprite node_out_E
    ROOM_OUT_3(ROOM_IN_0.edges, 3), // Corresponds to sprite node_out_F
    ROOM_OUT_4(ROOM_IN_0.edges, 4), // Corresponds to sprite node_out_A
    ROOM_OUT_5(ROOM_IN_0.edges, 5); // Corresponds to sprite node_out_B

    public static final HashSet<PieceType> PATHS = new HashSet<PieceType>(Arrays.asList(new PieceType[] {PATH_LONG, PATH_MED, PATH_SHORT}));
    public static final HashSet<PieceType> IN_ROOMS = new HashSet<PieceType>(
            Arrays.asList(new PieceType[] {ROOM_IN_0, ROOM_IN_1, ROOM_IN_2, ROOM_IN_3, ROOM_IN_4, ROOM_IN_5}));
    public static final HashSet<PieceType> OUT_ROOMS = new HashSet<PieceType>(
            Arrays.asList(new PieceType[] {ROOM_OUT_0, ROOM_OUT_1, ROOM_OUT_2, ROOM_OUT_3, ROOM_OUT_4, ROOM_OUT_5}));
    public static final HashSet<PieceType> ROOMS = new HashSet<PieceType>(
            Arrays.asList(new PieceType[] {ROOM_IN_0, ROOM_IN_1, ROOM_IN_2, ROOM_IN_3, ROOM_IN_4, ROOM_IN_5, ROOM_OUT_0, ROOM_OUT_1, ROOM_OUT_2, ROOM_OUT_3, ROOM_OUT_4, ROOM_OUT_5}));

    private TextureRegion texture;
    public int width, height, centerX, centerY;
    protected final EdgeType[] edges;

    private PieceType(EdgeType[] edges) {
        this.edges = edges;
    }

    private PieceType(EdgeType[] edges, int rot) {
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

    public static boolean isRoom(Piece p) { return ROOMS.contains(p.type); }

    public static boolean isPath(Piece p) { return PATHS.contains(p.type); }

    public static boolean init(TextureAtlas a) {
        boolean success = true;
        for(PieceType p : values()) {
            String fileName = p.name().toLowerCase();
            TextureRegion t = a.findRegion(fileName);
            if(t == null) {
                System.out.println("Sprite named \"" + fileName + "\" not found in provided TextureAtlas. PieceArchetype initialization failed.");
                success = false;
            }
            p.setTexture(t);
        }

        return success;
    }
}