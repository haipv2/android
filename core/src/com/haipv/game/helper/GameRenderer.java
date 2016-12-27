package com.haipv.game.helper;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.haipv.game.objects.Bird;
import com.haipv.game.objects.Grass;
import com.haipv.game.objects.Pipe;
import com.haipv.game.objects.ScrollHandler;
import com.haipv.game.ui.SimpleButton;
import com.haipv.game.ui.Value;
import com.haipv.game.ui.ValueAccessor;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;

public class GameRenderer {
	private GameWorld myWorld;
	private OrthographicCamera cam;
	private ShapeRenderer shapeRenderer;

	private SpriteBatch batcher;

	private int midPointY;
	private int gameHeight;
	private float runTime = 0;

	// Game Objects
	private Bird bird;
	private ScrollHandler scroller;
	private Grass frontGrass, backGrass;
	private Pipe pipe1, pipe2, pipe3;

	// Game Assets
	private TextureRegion bg, grass;
	private Animation birdAnimation;
	private TextureRegion birdMid, birdDown, birdUp;
	private TextureRegion skullUp, skullDown, bar;

	// Tween stuff
	private TweenManager manager;
	private Value alpha = new Value();

	// Buttons
	private List<SimpleButton> menuButtons;

	public GameRenderer(GameWorld world, int gameHeight, int midPointY) {
		myWorld = world;

		// The word "this" refers to this instance.
		// We are setting the instance variables' values to be that of the
		// parameters passed in from GameScreen.
		this.gameHeight = gameHeight;
		this.midPointY = midPointY;

		cam = new OrthographicCamera();
		cam.setToOrtho(true, 137, 204);

		batcher = new SpriteBatch();
		// Attach batcher to camera
		batcher.setProjectionMatrix(cam.combined);

		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setProjectionMatrix(cam.combined);
		// Call helper methods to initialize instance variables
		initGameObjects();
		initAssets();
		setupTweens();
	}

	private void setupTweens() {
		Tween.registerAccessor(Value.class, new ValueAccessor());
		manager = new TweenManager();
		Tween.to(alpha, -1, .5f).target(0).ease(TweenEquations.easeOutQuad).start(manager);
	}

	public void render(float delta) {

		// Fill the entire screen with black, to prevent potential flickering.
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Begin ShapeRenderer
		shapeRenderer.begin(ShapeType.Filled);

		// Draw Background color
		shapeRenderer.setColor(55 / 255.0f, 80 / 255.0f, 100 / 255.0f, 1);
		shapeRenderer.rect(0, 0, 136, midPointY + 66);

		// Draw Grass
		shapeRenderer.setColor(111 / 255.0f, 186 / 255.0f, 45 / 255.0f, 1);
		shapeRenderer.rect(0, midPointY + 66, 136, 11);

		// Draw Dirt
		shapeRenderer.setColor(147 / 255.0f, 80 / 255.0f, 27 / 255.0f, 1);
		shapeRenderer.rect(0, midPointY + 77, 136, 52);

		// End ShapeRenderer
		shapeRenderer.end();

		// Begin SpriteBatch
		batcher.begin();
		// Disable transparency
		// This is good for performance when drawing images that do not require
		// transparency.
		batcher.disableBlending();
		batcher.draw(AssetLoader.bg, 0, midPointY + 23, 136, 43);
		// 1. Draw Grass
		drawGrass();

		// 2. Draw Pipes
		drawPipes();
		batcher.enableBlending();

		// 3. Draw Skulls (requires transparency)
		drawSkulls();

		// The bird needs transparency, so we enable that again.
		batcher.enableBlending();

		if (bird.shouldntFlap()) {
			batcher.draw(birdMid, bird.getX(), bird.getY(), bird.getWidth() / 2.0f, bird.getHeight() / 2.0f,
					bird.getWidth(), bird.getHeight(), 1, 1, bird.getRotation());

		} else {
			batcher.draw(birdAnimation.getKeyFrame(runTime), bird.getX(), bird.getY(), bird.getWidth() / 2.0f,
					bird.getHeight() / 2.0f, bird.getWidth(), bird.getHeight(), 1, 1, bird.getRotation());
		}

		// TEMPORARY CODE! We will fix this section later:
		if (myWorld.isReady()) {
			// Draw shadow first
			AssetLoader.shadow.draw(batcher, "Touch me", (136 / 2) - (42), 76);
			// Draw text
			AssetLoader.font.draw(batcher, "Touch me", (136 / 2) - (42 - 1), 75);
		} else {

			if (myWorld.isGameOver()) {
				AssetLoader.shadow.draw(batcher, "Game Over", 25, 56);
				AssetLoader.font.draw(batcher, "Game Over", 24, 55);

				AssetLoader.shadow.draw(batcher, "Try again?", 23, 76);
				AssetLoader.font.draw(batcher, "Try again?", 24, 75);

			}

			// Convert integer into String
			String score = myWorld.getScore() + "";

			// Draw shadow first
			AssetLoader.shadow.draw(batcher, "" + myWorld.getScore(), (136 / 2) - (3 * score.length()), 12);
			// Draw text
			AssetLoader.font.draw(batcher, "" + myWorld.getScore(), (136 / 2) - (3 * score.length() - 1), 11);
		}

		batcher.end();
		drawTransition(delta);

		/*
		 * shapeRenderer.begin(ShapeType.Filled);
		 * shapeRenderer.setColor(Color.RED);
		 * shapeRenderer.circle(bird.getBoundingCircle().x,
		 * bird.getBoundingCircle().y, bird.getBoundingCircle().radius);
		 * 
		 * 
		 * Excuse the mess below. Temporary code for testing bounding
		 * rectangles.
		 * 
		 * // Bar up for pipes 1 2 and 3 shapeRenderer.rect(pipe1.getBarUp().x,
		 * pipe1.getBarUp().y, pipe1.getBarUp().width, pipe1.getBarUp().height);
		 * shapeRenderer.rect(pipe2.getBarUp().x, pipe2.getBarUp().y,
		 * pipe2.getBarUp().width, pipe2.getBarUp().height);
		 * shapeRenderer.rect(pipe3.getBarUp().x, pipe3.getBarUp().y,
		 * pipe3.getBarUp().width, pipe3.getBarUp().height);
		 * 
		 * // Bar down for pipes 1 2 and 3
		 * shapeRenderer.rect(pipe1.getBarDown().x, pipe1.getBarDown().y,
		 * pipe1.getBarDown().width, pipe1.getBarDown().height);
		 * shapeRenderer.rect(pipe2.getBarDown().x, pipe2.getBarDown().y,
		 * pipe2.getBarDown().width, pipe2.getBarDown().height);
		 * shapeRenderer.rect(pipe3.getBarDown().x, pipe3.getBarDown().y,
		 * pipe3.getBarDown().width, pipe3.getBarDown().height);
		 * 
		 * // Skull up for Pipes 1 2 and 3
		 * shapeRenderer.rect(pipe1.getSkullUp().x, pipe1.getSkullUp().y,
		 * pipe1.getSkullUp().width, pipe1.getSkullUp().height);
		 * shapeRenderer.rect(pipe2.getSkullUp().x, pipe2.getSkullUp().y,
		 * pipe2.getSkullUp().width, pipe2.getSkullUp().height);
		 * shapeRenderer.rect(pipe3.getSkullUp().x, pipe3.getSkullUp().y,
		 * pipe3.getSkullUp().width, pipe3.getSkullUp().height);
		 * 
		 * // Skull down for Pipes 1 2 and 3
		 * shapeRenderer.rect(pipe1.getSkullDown().x, pipe1.getSkullDown().y,
		 * pipe1.getSkullDown().width, pipe1.getSkullDown().height);
		 * shapeRenderer.rect(pipe2.getSkullDown().x, pipe2.getSkullDown().y,
		 * pipe2.getSkullDown().width, pipe2.getSkullDown().height);
		 * shapeRenderer.rect(pipe3.getSkullDown().x, pipe3.getSkullDown().y,
		 * pipe3.getSkullDown().width, pipe3.getSkullDown().height);
		 * 
		 * shapeRenderer.end();
		 */
	}

	private void drawTransition(float delta) {
		if (alpha.getValue() > 0) {
			manager.update(delta);
			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(1, 1, 1, alpha.getValue());
			shapeRenderer.rect(0, 0, 136, 300);
			shapeRenderer.end();
			Gdx.gl.glDisable(GL20.GL_BLEND);

		}
	}

	private void initGameObjects() {
		bird = myWorld.getBird();
		scroller = myWorld.getScroller();
		frontGrass = scroller.getFrontGrass();
		backGrass = scroller.getBackGrass();
		pipe1 = scroller.getPipe1();
		pipe2 = scroller.getPipe2();
		pipe3 = scroller.getPipe3();
	}

	private void initAssets() {
		bg = AssetLoader.bg;
		grass = AssetLoader.grass;
		birdAnimation = AssetLoader.birdAnimation;
		birdMid = AssetLoader.bird;
		birdDown = AssetLoader.birdDown;
		birdUp = AssetLoader.birdUp;
		skullUp = AssetLoader.skullUp;
		skullDown = AssetLoader.skullDown;
		bar = AssetLoader.bar;
	}

	private void drawGrass() {
		// Draw the grass
		batcher.draw(grass, frontGrass.getX(), frontGrass.getY(), frontGrass.getWidth(), frontGrass.getHeight());
		batcher.draw(grass, backGrass.getX(), backGrass.getY(), backGrass.getWidth(), backGrass.getHeight());
	}

	private void drawSkulls() {
		// Temporary code! Sorry about the mess :)
		// We will fix this when we finish the Pipe class.

		batcher.draw(skullUp, pipe1.getX() - 1, pipe1.getY() + pipe1.getHeight() - 14, 24, 14);
		batcher.draw(skullDown, pipe1.getX() - 1, pipe1.getY() + pipe1.getHeight() + 45, 24, 14);

		batcher.draw(skullUp, pipe2.getX() - 1, pipe2.getY() + pipe2.getHeight() - 14, 24, 14);
		batcher.draw(skullDown, pipe2.getX() - 1, pipe2.getY() + pipe2.getHeight() + 45, 24, 14);

		batcher.draw(skullUp, pipe3.getX() - 1, pipe3.getY() + pipe3.getHeight() - 14, 24, 14);
		batcher.draw(skullDown, pipe3.getX() - 1, pipe3.getY() + pipe3.getHeight() + 45, 24, 14);
	}

	private void drawPipes() {
		// Temporary code! Sorry about the mess :)
		// We will fix this when we finish the Pipe class.
		batcher.draw(bar, pipe1.getX(), pipe1.getY(), pipe1.getWidth(), pipe1.getHeight());
		batcher.draw(bar, pipe1.getX(), pipe1.getY() + pipe1.getHeight() + 45, pipe1.getWidth(),
				midPointY + 66 - (pipe1.getHeight() + 45));

		batcher.draw(bar, pipe2.getX(), pipe2.getY(), pipe2.getWidth(), pipe2.getHeight());
		batcher.draw(bar, pipe2.getX(), pipe2.getY() + pipe2.getHeight() + 45, pipe2.getWidth(),
				midPointY + 66 - (pipe2.getHeight() + 45));

		batcher.draw(bar, pipe3.getX(), pipe3.getY(), pipe3.getWidth(), pipe3.getHeight());
		batcher.draw(bar, pipe3.getX(), pipe3.getY() + pipe3.getHeight() + 45, pipe3.getWidth(),
				midPointY + 66 - (pipe3.getHeight() + 45));
	}

	private void drawMenuUI() {
		batcher.draw(AssetLoader.zbLogo, 136 / 2 - 56, midPointY - 50, AssetLoader.zbLogo.getRegionWidth() / 1.2f,
				AssetLoader.zbLogo.getRegionHeight() / 1.2f);

		for (SimpleButton button : menuButtons) {
			button.draw(batcher);
		}

	}

	private void drawScore() {
		int length = ("" + myWorld.getScore()).length();
		AssetLoader.shadow.draw(batcher, "" + myWorld.getScore(), 68 - (3 * length), midPointY - 82);
		AssetLoader.font.draw(batcher, "" + myWorld.getScore(), 68 - (3 * length), midPointY - 83);
	}
}
