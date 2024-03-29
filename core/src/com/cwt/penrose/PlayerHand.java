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
public class PlayerHand {
    private static final int PATH_HAND_SIZE = 4;
    private static final int ROOM_HAND_SIZE = 3;

    private final int playerId;
    private final List<Piece> pathHand = new ArrayList<Piece>(PATH_HAND_SIZE);
    private final List<Piece> roomHand = new ArrayList<Piece>(ROOM_HAND_SIZE);
    private final OrthographicCamera uiCamera;

    public PlayerHand(int playerId) {
        this.playerId = playerId;
        uiCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        uiCamera.zoom = 6f;
        uiCamera.translate(Gdx.graphics.getWidth() * 6f / 2f, Gdx.graphics.getHeight() * 6f / 2f);
    }

    public void setupPathHand(boolean refill) {
        for (int i = 0; i < pathHand.size(); ++i) {
            Piece p = pathHand.get(i);
            p.setPos(5 + p.type.centerX, (int) (1.1f * i * p.type.height + p.type.centerY));
        }
        if(!refill) return;
        Object[] archetypes = PieceType.PATHS.toArray();
        for (int i = pathHand.size(); i < PATH_HAND_SIZE; ++i) {
            PieceType p = (PieceType) archetypes[MathUtils.random(archetypes.length - 1)];
            pathHand.add(new Piece(p, 5 + p.centerX, (int) (1.1f * i * p.height + p.centerY)));
        }
    }

    public void setupRoomHand(boolean refill) {
        for (int i = 0; i < roomHand.size(); ++i) {
            Piece p = roomHand.get(i);
            p.setPos((int) (Gdx.graphics.getWidth() * 6f - 5 - p.type.centerX), (int) (1.1f * i * p.type.height + p.type.centerY));
        }
        if(!refill) return;
        Object[] archetypes = PieceType.ROOMS.toArray();
        for (int i = roomHand.size(); i < ROOM_HAND_SIZE; ++i) {
            PieceType p = (PieceType) archetypes[MathUtils.random(archetypes.length - 1)];
            roomHand.add(new Piece(p, (int) (Gdx.graphics.getWidth() * 6f - 5 - p.centerX), (int) (1.1f * i * p.height + p.centerY)));
        }
    }

    public Piece getPiece(float screenX, float screenY) {
        Piece selection = getPath(screenX, screenY);
        if(selection == null) selection = getRoom(screenX, screenY);
        return selection;
    }

    public Piece getPath(float screenX, float screenY) {
        Vector3 uiCoords = uiCamera.unproject(new Vector3(screenX, screenY, 0));
        int x = (int) uiCoords.x, y = (int) uiCoords.y;
        Piece selection = null;
        for(Piece p : pathHand)
            if(p.x - p.type.centerX <= x && x <= p.x + p.type.centerX && p.y - p.type.centerY< y && y < p.y + p.type.centerY) {
                selection = p;
                break;
            }
        return selection;
    }

    public Piece getRoom(float screenX, float screenY) {
        Vector3 uiCoords = uiCamera.unproject(new Vector3(screenX, screenY, 0));
        int x = (int) uiCoords.x, y = (int) uiCoords.y;
        Piece selection = null;
        for (Piece p : roomHand)
            if (p.x - p.type.centerX <= x && x <= p.x + p.type.centerX && p.y - p.type.centerY < y && y < p.y + p.type.centerY) {
                selection = p;
                break;
            }
        return selection;
    }

    public boolean removePiece(Piece p) {
        return pathHand.remove(p) || roomHand.remove(p);
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

    public void addPiece(Piece p) {
        if(p.isPath()) addPath(p);
        else if(p.isRoom()) addRoom(p);
    }

    public void draw(SpriteBatch batch) {
        uiCamera.update();

        batch.setProjectionMatrix(uiCamera.combined);
        for(Piece p : pathHand) p.draw(batch);
        for(Piece p : roomHand) p.draw(batch);
    }
}