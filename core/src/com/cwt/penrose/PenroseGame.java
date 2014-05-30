package com.cwt.penrose;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

import java.util.Map;

public class PenroseGame extends ApplicationAdapter {
    public static final int BOARD_WIDTH = 128;
    public static final int BOARD_HEIGHT = 128;
	SpriteBatch batch;
	Texture spritesheet;
    Piece[][] board = new Piece[BOARD_WIDTH][BOARD_HEIGHT];
    OrthographicCamera camera;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		spritesheet = new Texture("sprite_sheet.png");
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 4f;

        final int sWidth = 300, sHeight = 330;
        for(int i = 0; (i + 1) * sWidth < spritesheet.getWidth(); ++i)
            PieceArchetype.values()[i].setTexture(new TextureRegion(spritesheet, i * sWidth, 0, sWidth, sHeight));

        // TODO: Automatically load all room pieces from the spritesheet

        for(int i = 0; i < BOARD_WIDTH; ++i)
            for(int j = 0; j < BOARD_HEIGHT; ++j) {
                Piece piece = new Piece(PieceArchetype.values()[MathUtils.random(2)], (int)((i % 2 * 0.5f + j) * sWidth), (int)(i * Math.sqrt(3) / 2.0 * sWidth));
                piece.rotationIndex = MathUtils.random(5);
                board[i][j] = piece;
            }
	}

	@Override
	public void render () {
        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) Gdx.app.exit();
        if(Gdx.input.isButtonPressed(Input.Buttons.RIGHT))
            camera.translate(-Gdx.input.getDeltaX() * camera.zoom, Gdx.input.getDeltaY() * camera.zoom);
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
}
