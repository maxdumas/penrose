package com.cwt.penrose;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector3;

public class PenroseGame extends ApplicationAdapter implements InputProcessor {
    public static final int NUM_PLAYERS = 2;
    boolean canPan = false;

    private SpriteBatch batch;
    public OrthographicCamera sceneCamera;
    public boolean ghostVisible = false, ghostInvalid = false;
    public final Piece ghost = new Piece(PieceType.PATH_LONG, 0, 0);
    private PlayerManager cpm;

    static final float zoomFactor = 6f;

    @Override
	public void create () {
        // Filter input through here first, then try the player manager.
        cpm = new PlayerManager(this, NUM_PLAYERS);
        Gdx.input.setInputProcessor(new InputMultiplexer(this, cpm));
        batch = new SpriteBatch();
        TextureAtlas spritesheet = new TextureAtlas("sprite_sheet.txt");
        sceneCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        sceneCamera.zoom = zoomFactor;

        PieceType.init(spritesheet);
    }

	@Override
	public void render () {
        sceneCamera.update();
		Gdx.gl.glClearColor(0.8f, 0.8f, 0.9f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(sceneCamera.combined);
		batch.begin();
        // Display UI here
        cpm.getHand().draw(batch);
        batch.setProjectionMatrix(sceneCamera.combined);
        // Display areas here
        for(Area a : cpm.getAreas()) a.draw(batch);
        if(ghostVisible) {
            if(ghostInvalid) batch.setColor(1.0f, 0f, 0f, 1f);
            ghost.draw(batch);
        }
		batch.end();
	}

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.ESCAPE) Gdx.app.exit();

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Input.Keys.SPACE && cpm.getAP() == 0) { // end turn
            cpm.getHand().setupPathHand(true);
            sceneCamera.position.set(cpm.getArea().getCenterX(), cpm.getArea().getCenterY(), 0f);
            cpm.nextPlayer();
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        switch (button) {
            case Input.Buttons.RIGHT: canPan = false;
        }

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if(canPan)
            sceneCamera.translate(-Gdx.input.getDeltaX() * sceneCamera.zoom, Gdx.input.getDeltaY() * sceneCamera.zoom);

        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        Vector3 worldCoords = sceneCamera.unproject(new Vector3(screenX, screenY, 0f));
        int x = (int)worldCoords.x, y = (int)worldCoords.y;
        if(cpm.getState() == PlayerState.POSITIONING) {
            ghost.x = x;
            ghost.y = y;
        }
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
