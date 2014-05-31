package com.cwt.penrose;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import java.util.Map;

public class PenroseGame extends ApplicationAdapter implements InputProcessor {
    public static final int BOARD_WIDTH = 128;
    public static final int BOARD_HEIGHT = 128;
    static final float cos30 = 0.866025403784438646763723f;
    static final float sin30 = 0.5f;
    static final float radius = 331 / 2f;
    static float xOff = cos30 * radius;
    static float yOff = sin30 * radius;

    boolean rightDown = false;


    SpriteBatch batch;
	TextureAtlas spritesheet;
    Piece[][] board = new Piece[BOARD_WIDTH][BOARD_HEIGHT];
    OrthographicCamera camera;
	
	@Override
	public void create () {
        Gdx.input.setInputProcessor(this);

        batch = new SpriteBatch();
        spritesheet = new TextureAtlas("sprite_sheet.txt");
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 6f;

        PieceArchetype.CONNECTOR_LONG.setTexture(spritesheet.findRegion("0_long_path"));
        PieceArchetype.CONNECTOR_MED.setTexture(spritesheet.findRegion("0_med_path"));
        PieceArchetype.CONNECTOR_SHORT.setTexture(spritesheet.findRegion("0_short_path"));

        for (int i = 0; i < BOARD_WIDTH; ++i)
            for (int j = 0; j < BOARD_HEIGHT; ++j) {
                PieceArchetype a = PieceArchetype.values()[MathUtils.random(2)];
                Piece piece = new Piece(a, (int)((j * 2 + i % 2) * xOff), (int) (i * yOff * 3f));
                piece.rotationIndex = MathUtils.random(5);
                board[i][j] = piece;
            }
    }

	@Override
	public void render () {
        camera.update();
		Gdx.gl.glClearColor(0.8f, 0.8f, 0.9f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
		batch.begin();
		for(int i = 0; i < BOARD_WIDTH; ++i)
            for(int j = 0; j < BOARD_HEIGHT; ++j) {
                board[i][j].draw(batch);
            }
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

        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        switch(button) {
            case Input.Buttons.RIGHT:
                rightDown = true;
                break;
            case Input.Buttons.LEFT:
                Vector3 worldCoords = camera.unproject(new Vector3(screenX, screenY, 0f));
                float x = worldCoords.x, y = worldCoords.y;



//                int i = (int) (y / (3f * yOff)), j;
//                if (i % 2 != 0)
//                    j = (int) (x / (2f * xOff) - 0.5f);
//                else j = (int) (x / (2f * xOff));
//                if (i >= 0 && j >= 0)
//                    board[i][j].rotationIndex += 1;
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
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
