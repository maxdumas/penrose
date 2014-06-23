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
    boolean canPan = false, pieceDiscarded = false;

    SpriteBatch batch;
	TextureAtlas spritesheet;
    OrthographicCamera sceneCamera;
    final Piece ghost = new Piece(PieceType.PATH_LONG, 0, 0);
    final Area[] areas = new Area[NUM_PLAYERS];
    final PlayerHand[] playerHands = new PlayerHand[2];

    int activePlayer = 0, ap = 2;
    PlayerState state = PlayerState.SELECTING;
    static final float zoomFactor = 6f;
	
	@Override
	public void create () {
        Gdx.input.setInputProcessor(this);

        batch = new SpriteBatch();
        spritesheet = new TextureAtlas("sprite_sheet.txt");
        sceneCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        sceneCamera.zoom = zoomFactor;

        PieceType.init(spritesheet);
        
        for(int i = 0; i < NUM_PLAYERS; ++i) {
            areas[i] = new Area(i);
            playerHands[i] = new PlayerHand(i);
            playerHands[i].setupPathHand(true);
            playerHands[i].setupRoomHand(true);
        }
    }

	@Override
	public void render () {
        sceneCamera.update();
		Gdx.gl.glClearColor(0.8f, 0.8f, 0.9f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(sceneCamera.combined);
		batch.begin();
        // Display UI here
        PlayerHand currentPlayerHand = playerHands[activePlayer];
        currentPlayerHand.draw(batch);
        batch.setProjectionMatrix(sceneCamera.combined);
        // Display things here
        for(Area a : areas) a.draw(batch);
        if(state != PlayerState.SELECTING)
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
        if(keycode == Input.Keys.SPACE && ap == 0) { // end turn
            playerHands[activePlayer].setupPathHand(true);
            activePlayer = (activePlayer + 1) % NUM_PLAYERS;
            sceneCamera.position.set(areas[activePlayer].getCenterX(), areas[activePlayer].getCenterY(), 0f);
            ap = 2;
            pieceDiscarded = false;
            state = PlayerState.SELECTING;
        }

        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 worldCoords = sceneCamera.unproject(new Vector3(screenX, screenY, 0f));
        int x = (int) worldCoords.x, y = (int) worldCoords.y;

        switch (state) {
            case SELECTING:
                if (button == Input.Buttons.LEFT) {
                    Piece selection = playerHands[activePlayer].select(screenX, screenY);
                    if (selection != null) { // If the player selected a piece in their hand
                        // calculate which piece was chosen, set ghost to that, remove that piece from roomHand
                        state = PlayerState.PLACING;
                        ghost.set(selection.type, x, y, 0);
                    }
                    // We want to select a piece and move it, disallowing rotation.
                    else if (ap >= 2) { // If player AP is 2 or higher (reconfiguring costs 2 AP)
                        selection = areas[activePlayer].getPiece(x, y);
                        if (selection != null && selection.isPath()) {
                            state = PlayerState.REPLACING;
                            ghost.set(selection.type, selection.x, selection.y, selection.rotationIndex);
                            areas[activePlayer].removePiece(selection);
                        }
                    }
                } else if (button == Input.Buttons.MIDDLE) {
                    // We want to rotate the piece under the mouse.
                    Piece selection = areas[activePlayer].getPiece(x, y);
                    if (selection != null) {
                        selection.rotate(true);
                        // Need to figure out how AP will work with this as well
                    } else {
                        Piece pathSelection = playerHands[activePlayer].selectPath(screenX, screenY);
                        if (pathSelection != null && !pieceDiscarded) {
                            playerHands[activePlayer].pathHand.remove(pathSelection);
                            pieceDiscarded = true;
                        }
                    }
                }
                break;
            default:
                if (button == Input.Buttons.LEFT) {
                    // We now want to snap the ghost piece to the hex grid before we place it
                    ghost.setPos(x, y);
                    ghost.snapToHex();

                    boolean placed = areas[activePlayer].placePiece(ghost, activePlayer);
                    if (!placed && ap == 1) { // AP of 1 signifies that first piece has already been placed so we are allowed to place on other areas
                        for (int i = 0; i < NUM_PLAYERS; ++i)
                            if (i != activePlayer && areas[i].placePiece(ghost, activePlayer)) {
                                placed = true;
                                break;
                            }
                    }
                    if (placed) {
                        if (state == PlayerState.REPLACING) ap -= 2;
                        else --ap;
                        playerHands[activePlayer].setupPathHand(false);
                    } else {
                        ghost.rotationIndex = 0;
                        if (ghost.isRoom())
                            playerHands[activePlayer].addRoom(new Piece(ghost));
                        else if (ghost.isPath())
                            playerHands[activePlayer].addPath(new Piece(ghost));
                    }
                    state = PlayerState.SELECTING;
                }
        }

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        switch (button) {
            case Input.Buttons.RIGHT: canPan = false;
        }

        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if(canPan)
            sceneCamera.translate(-Gdx.input.getDeltaX() * sceneCamera.zoom, Gdx.input.getDeltaY() * sceneCamera.zoom);

        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        Vector3 worldCoords = sceneCamera.unproject(new Vector3(screenX, screenY, 0f));
        float x = worldCoords.x, y = worldCoords.y;
        if(state != PlayerState.SELECTING) {
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
