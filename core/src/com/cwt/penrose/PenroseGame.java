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
    boolean rightDown = false, placing = false, reconfiguring = false;

    SpriteBatch batch;
	TextureAtlas spritesheet;
    OrthographicCamera camera;
    final Piece ghost = new Piece(PieceArchetype.NONE, 0, 0);
    final Area[] areas = new Area[NUM_PLAYERS];

    int activePlayer = 0, ap = 2;
	
	@Override
	public void create () {
        Gdx.input.setInputProcessor(this);

        for(int i = 0; i < NUM_PLAYERS; ++i) areas[i] = new Area(i);

        batch = new SpriteBatch();
        spritesheet = new TextureAtlas("sprite_sheet.txt");
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 6f;

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
    }

	@Override
	public void render () {
        camera.update();
		Gdx.gl.glClearColor(0.8f, 0.8f, 0.9f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
		batch.begin();
        // Display things here
        for(Area a : areas)
            a.draw(batch);
        if(placing || reconfiguring)
            ghost.draw(batch);
		batch.end();
	}

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch(keycode) {
            case Input.Keys.ESCAPE:
                Gdx.app.exit();
                break;
            case Input.Keys.NUM_1:
                placing = !placing;
                ghost.type = PieceArchetype.PATH_LONG;
                break;
            case Input.Keys.NUM_2:
                placing = !placing;
                ghost.type = PieceArchetype.PATH_MED;
                break;
            case Input.Keys.NUM_3:
                placing = !placing;
                ghost.type = PieceArchetype.PATH_SHORT;
                break;
            case Input.Keys.NUM_4:
                placing = !placing;
                ghost.type = PieceArchetype.ROOM_IN_4;
                break;
            case Input.Keys.NUM_5:
                placing = !placing;
                ghost.type = PieceArchetype.ROOM_IN_5;
                break;
        }

        if(placing) {
            ghost.rotationIndex = 0;
            mouseMoved(Gdx.input.getX(), Gdx.input.getY());
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 worldCoords = camera.unproject(new Vector3(screenX, screenY, 0f));
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

                    boolean placed = areas[activePlayer].placePiece(ghost);
                    if(!placed && ap == 1) { // AP of 1 signifies that first piece has already been placed so we are allowed to place on other areas
                        for(int i = 0; i < NUM_PLAYERS; ++i)
                            if(i != activePlayer && areas[i].placePiece(ghost)) {
                                --ap;
                                break;
                            }
                    } else if(placed) {
                        if(reconfiguring) ap -= 2;
                        else --ap;
                    }
                    placing = reconfiguring = false;
                    ghost.type = PieceArchetype.NONE;
                } else if(!reconfiguring) {
                    // We want to select a piece and move it, disallowing rotation.
                    if (true) { // We only allow reconfiguring if it is the only move occurring this turn
                        Piece selection = areas[activePlayer].getPiece(x, y);
                        if (selection != null) {
                            reconfiguring = true;
                            ghost.type = selection.type;
                            ghost.rotationIndex = selection.rotationIndex;
                            areas[activePlayer].removePiece(selection);
                        }
                    }
                }
                break;
            case Input.Buttons.MIDDLE:
                if(placing && !reconfiguring) {
                    ghost.rotate(true);
                } else if (!placing && !reconfiguring) {
                    // We want to rotate the piece under the mouse.
                    Piece selection = areas[activePlayer].getPiece(x, y);
                    if(selection != null) {
                        selection.rotate(true);

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
            camera.translate(-Gdx.input.getDeltaX() * camera.zoom, Gdx.input.getDeltaY() * camera.zoom);

        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        Vector3 worldCoords = camera.unproject(new Vector3(screenX, screenY, 0f));
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
