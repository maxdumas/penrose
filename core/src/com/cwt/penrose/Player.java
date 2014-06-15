package com.cwt.penrose;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by max on 6/14/14.
 */
public class Player {
    private static final int PATH_HAND_SIZE = 4;
    private static final int ROOM_HAND_SIZE = 3;

    int playerId;
    final List<Piece> pathHand = new ArrayList<Piece>(PATH_HAND_SIZE);
    final List<Piece> roomHand = new ArrayList<Piece>(ROOM_HAND_SIZE);
    OrthographicCamera uiCamera;

    public Player(int playerId) {
        this.playerId = playerId;
        uiCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        uiCamera.zoom = 6f;
        uiCamera.translate(Gdx.graphics.getWidth() * 6f / 2f, Gdx.graphics.getHeight() * 6f / 2f);
    }

    public void setupPathHand(boolean refill) {
        Object[] archetypes = PieceArchetype.PATHS.toArray();
        for (int i = 0; i < pathHand.size(); ++i) {
            Piece p = pathHand.get(i);
            p.setPos(5 + p.type.centerX, (int) (1.1f * i * p.type.height + p.type.centerY));
        }
        if(!refill) return;
        for (int i = pathHand.size(); i < PATH_HAND_SIZE; ++i) {
            PieceArchetype p = (PieceArchetype) archetypes[MathUtils.random(archetypes.length - 1)];
            pathHand.add(new Piece(p, 5 + p.centerX, (int) (1.1f * i * p.height + p.centerY)));
        }
    }

    public void setupRoomHand(boolean refill) {
        Object[] archetypes = PieceArchetype.ROOMS.toArray();
        for (int i = 0; i < roomHand.size(); ++i) {
            Piece p = roomHand.get(i);
            p.setPos((int) (Gdx.graphics.getWidth() * 6f - 5 - p.type.centerX), (int) (1.1f * i * p.type.height + p.type.centerY));
        }
        if(!refill) return;
        for (int i = roomHand.size(); i < ROOM_HAND_SIZE; ++i) {
            PieceArchetype p = (PieceArchetype) archetypes[MathUtils.random(archetypes.length - 1)];
            roomHand.add(new Piece(p, (int) (Gdx.graphics.getWidth() * 6f - 5 - p.centerX), (int) (1.1f * i * p.height + p.centerY)));
        }
    }

    public Piece selectPath(float screenX, float screenY) {
        Vector3 uiCoords = uiCamera.unproject(new Vector3(screenX, screenY, 0));
        int x = (int) uiCoords.x, y = (int) uiCoords.y;

        for(Piece p : pathHand)
            if(p.x < x && x < p.x + p.type.width && p.y < y && y < p.y + p.type.height)
                return p;

        return null;
    }

    public Piece selectRoom(float screenX, float screenY) {
        Vector3 uiCoords = uiCamera.unproject(new Vector3(screenX, screenY, 0));
        int x = (int) uiCoords.x, y = (int) uiCoords.y;

        for(Piece p : roomHand)
            if(p.x < x && x < p.x + p.type.width && p.y < y && y < p.y + p.type.height)
                return p;

        return null;
    }

    public void addRoom(Piece p) {
        if(roomHand.size() >= ROOM_HAND_SIZE) return;
        roomHand.add(p);
        setupRoomHand(false);
    }

    public void addPath(Piece p) {
        if(pathHand.size() >= PATH_HAND_SIZE) return;
        pathHand.add(p);
        setupPathHand(false);
    }

    public void draw(SpriteBatch batch) {
        uiCamera.update();

        batch.setProjectionMatrix(uiCamera.combined);
        for(Piece p : pathHand) p.draw(batch);
        for(Piece p : roomHand) p.draw(batch);
    }
}