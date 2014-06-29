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
    private boolean invalidPiece;

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
        if(invalidPiece) batch.setColor(1.0f, 0f, 0f, 1f);
        if(state != PlayerState.SELECTING || invalidPiece) ghost.draw(batch);
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
                    if (selection != null && !invalidPiece) { // SELECTION FROM HAND
                        state = PlayerState.PLACING;
                        ghost.set(selection.type, x, y, 0);
                    }
                    else if (ap >= 2) { // SELECTION OF EXISTING PIECE
                        selection = areas[activePlayer].getPiece(x, y);
                        if (selection != null && selection.isPath()) {
                            state = PlayerState.REPLACING;
                            ghost.set(selection.type, selection.x, selection.y, selection.rotationIndex);
                            areas[activePlayer].removePiece(selection);
                        }
                    }
                } else if (button == Input.Buttons.MIDDLE) {
                    // We want to rotate the piece under the mouse.
                    Piece selection = playerHands[activePlayer].selectPath(screenX, screenY);
                    if (selection != null && !pieceDiscarded) { // DISCARDING FROM HAND
                        playerHands[activePlayer].pathHand.remove(selection);
                        pieceDiscarded = true;
                    } else if (selection == null) { // ROTATION OF EXISTING PIECE
                        selection = areas[activePlayer].getPiece(x, y);
                        if (selection != null) selection.rotate(true);
                        // Need to figure out how AP will work with this as well
                    }
                }
                break;
            default:
                if (button == Input.Buttons.LEFT) {
                    // Snap the ghost piece to the hex grid before we place it
                    ghost.setPos(x, y);
                    ghost.snapToHex();

                    // Attempt to place the desired piece on our area
                    boolean placed = areas[activePlayer].addPieceIfValid(new Piece(ghost), activePlayer); // Verify that the placement is correct
                    if (!placed && ap == 1) { // Couldn't place piece on our area, try other players if we have already placed a first piece
                        for (int i = 0; i < NUM_PLAYERS; ++i) {
                            if (i != activePlayer && areas[i].addPieceIfValid(new Piece(ghost), activePlayer)) {
                                placed = true;
                                break;
                            }
                        }
                    }
                    if (placed) { // Successfully placed the piece
                        if (state == PlayerState.REPLACING) ap -= 2;
                        else --ap;
                        playerHands[activePlayer].setupPathHand(false);
                        state = PlayerState.SELECTING;
                    } else { // Did not successfully place piece, leave it as a dummy and don't allow anything else to
                        // happen until piece is placed correctly.
                        invalidPiece = true;
                        state = PlayerState.SELECTING;
                    }

                    // TODO: Need to create system where a move can be made, THEN the move is checked for validity.
                    // Player needs to be able to put pieces down at will. Their last move needs to be able to be undone
                    // until their next move is performed or until they end their turn. Rotating and placing need
                    // to be entirely separated, and need to not cost AP when being performed on a piece that has just
                    // been placed.
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
        int x = (int)worldCoords.x, y = (int)worldCoords.y;
        if(state != PlayerState.SELECTING) {
            ghost.x = x;
            ghost.y = y;
        }
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
