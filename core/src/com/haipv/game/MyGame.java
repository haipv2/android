package com.haipv.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MyGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();
		/*
		// Sets a Color to Fill the Screen with (RGB = 10, 15, 230), Opacity of
				// 1 (100%)
				Gdx.gl.glClearColor(10 / 255.0f, 15 / 255.0f, 230 / 255.0f, 1f);
				// Fills the screen with the selected color
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				// Covert Frame rate to String, print it
				Gdx.app.log("GameScreen FPS", (1 / delta) + "");*/
	}
}
