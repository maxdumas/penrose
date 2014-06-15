package com.cwt.penrose;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector3;

public class PenroseGame extends ApplicationAdapter implements InputProcessor {
    public static final int NUM_PLAYERS = 2;
    boolean rightDown = false, placing = false, reconfiguring = false, pieceDiscarded = false;

    SpriteBatch batch;
	TextureAtlas spritesheet;
    OrthographicCamera sceneCamera;
    final Piece ghost = new Piece(PieceArchetype.NONE, 0, 0);
    final Area[] areas = new Area[NUM_PLAYERS];
    final Player[] players = new Player[2];

    int activePlayer = 0, ap = 2;
    static final float zoomFactor = 6f;
	
	@Override
	public void create () {
        Gdx.input.setInputProcessor(this);

        batch = new SpriteBatch();
        spritesheet = new TextureAtlas("sprite_sheet.txt");
        sceneCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        sceneCamera.zoom = zoomFactor;


        PieceArchetype.PATH_LONG.setTexture(spritesheet.findRegion("0_long_path"));
        PieceArchetype.PATH_MED.setTexture(spritesheet.findRegion("0_med_path"));
        PieceArchetype.PATH_SHORT.setTexture(spritesheet.findRegion("0_short_path"));

        PieceArchetype.ROOM_IN_0.setTexture(spritesheet.findRegion("node_in_C"));
        PieceArchetype.ROOM_IN_1.setTexture(spritesheet.findRegion("node_in_D"));
        PieceArchetype.ROOM_IN_2.setTexture(spritesheet.findRegion("node_in_E"));
        PieceArchetype.ROOM_IN_3.setTexture(spritesheet.findRegion("node_in_F"));
        PieceArchetype.ROOM_IN_4.setTexture(spritesheet.findRegion("node_in_A"));
        PieceArchetype.ROOM_IN_5.setTexture(spritesheet.findRegion("node_in_B"));

        PieceArchetype.ROOM_OUT_0.setTexture(spritesheet.findRegion("node_out_C"));
        PieceArchetype.ROOM_OUT_1.setTexture(spritesheet.findRegion("node_out_D"));
        PieceArchetype.ROOM_OUT_2.setTexture(spritesheet.findRegion("node_out_E"));
        PieceArchetype.ROOM_OUT_3.setTexture(spritesheet.findRegion("node_out_F"));
        PieceArchetype.ROOM_OUT_4.setTexture(spritesheet.findRegion("node_out_A"));
        PieceArchetype.ROOM_OUT_5.setTexture(spritesheet.findRegion("node_out_B"));

        for(int i = 0; i < NUM_PLAYERS; ++i) {
            areas[i] = new Area(i);
            players[i] = new Player(i);
            players[i].setupPathHand(true);
            players[i].setupRoomHand(true);
        }
    }

	@Override
	public void render () {
        if(ap <= 0) {
            players[activePlayer].setupPathHand(true);
            activePlayer = (activePlayer + 1) % NUM_PLAYERS;
            sceneCamera.position.set(areas[activePlayer].getCenterX(), areas[activePlayer].getCenterY(), 0f);
            ap = 2;
            pieceDiscarded = false;
        }
        sceneCamera.update();
		Gdx.gl.glClearColor(0.8f, 0.8f, 0.9f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
        // Display UI here
        Player currentPlayer = players[activePlayer];
        currentPlayer.draw(batch);
        batch.setProjectionMatrix(sceneCamera.combined);
        // Display things here
        for(Area a : areas)
            a.draw(batch);
        if(placing || reconfiguring)
            ghost.draw(batch);
		batch.end();
	}

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.ESCAPE) Gdx.app.exit();

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 worldCoords = sceneCamera.unproject(new Vector3(screenX, screenY, 0f));
        int x = (int)worldCoords.x, y = (int)worldCoords.y;

        switch(button) {
            case Input.Buttons.RIGHT:
                rightDown = true;
                break;
            case Input.Buttons.LEFT:
                if(placing || reconfiguring) {
                    // We now want to snap the ghost piece to the hex grid before we place it
                    ghost.setPos(x, y);
                    ghost.snapToHex();

                    boolean placed = areas[activePlayer].placePiece(ghost, activePlayer);
                    if(!placed && ap == 1) { // AP of 1 signifies that first piece has already been placed so we are allowed to place on other areas
                        for(int i = 0; i < NUM_PLAYERS; ++i)
                            if(i != activePlayer && areas[i].placePiece(ghost, activePlayer)) {
                                placed = true;
                                break;
                            }
                    }
                    if(placed) {
                        if(reconfiguring) ap -= 2;
                        else --ap;
                        players[activePlayer].setupPathHand(false);
                    } else {
                        ghost.rotationIndex = 0;
                        if(PieceArchetype.isRoom(ghost))
                            players[activePlayer].addRoom(new Piece(ghost));
                        else if (PieceArchetype.isPath(ghost))
                            players[activePlayer].addPath(new Piece(ghost));
                    }
                    placing = reconfiguring = false;
                    ghost.type = PieceArchetype.NONE;
                } else {
                    Piece roomSelection = players[activePlayer].selectRoom(screenX, screenY);
                    Piece pathSelection = players[activePlayer].selectPath(screenX, screenY);

                    if(roomSelection != null) {
                        // calculate which piece was chosen, set ghost to that, remove that piece from roomHand
                        ghost.type = roomSelection.type;
                        ghost.setPos(x, y);
                        ghost.rotationIndex = 0;
                        players[activePlayer].roomHand.remove(roomSelection);
                        placing = true;
                    } else if(pathSelection != null) {
                        ghost.type = pathSelection.type;
                        ghost.setPos(x, y);
                        ghost.rotationIndex = 0;
                        players[activePlayer].pathHand.remove(pathSelection);
                        placing = true;
                    }
                    // We want to select a piece and move it, disallowing rotation.
                    else if (ap >= 2) { // We only allow reconfiguring if it is the only move occurring this turn
                        Piece selection = areas[activePlayer].getPiece(x, y);
                        if (selection != null && !PieceArchetype.isRoom(selection)) {
                            reconfiguring = true;
                            ghost.type = selection.type;
                            ghost.setPos(selection.x, selection.y);
                            ghost.rotationIndex = selection.rotationIndex;
                            areas[activePlayer].removePiece(selection);
                        }
                    }
                }
                break;
            case Input.Buttons.MIDDLE:
                Piece pathSelection = players[activePlayer].selectPath(screenX, screenY);
                if(pathSelection != null && !pieceDiscarded) {
                    players[activePlayer].pathHand.remove(pathSelection);
                    pieceDiscarded = true;
                }
                else if(placing && !reconfiguring) {
                    ghost.rotate(true);
                } else if (!placing && !reconfiguring) {
                    // We want to rotate the piece under the mouse.
                    Piece selection = areas[activePlayer].getPiece(x, y);
                    if(selection != null) {
                        selection.rotate(true);
                        // Need to figure out how AP will work with this as well
                    }
                }
        }

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        switch (button) {
            case Input.Buttons.RIGHT: rightDown = false;
        }

        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if(rightDown)
            sceneCamera.translate(-Gdx.input.getDeltaX() * sceneCamera.zoom, Gdx.input.getDeltaY() * sceneCamera.zoom);

        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        Vector3 worldCoords = sceneCamera.unproject(new Vector3(screenX, screenY, 0f));
        float x = worldCoords.x, y = worldCoords.y;
        if(placing || reconfiguring) {
            ghost.x = (int)x;
            ghost.y = (int)y;
        }
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
