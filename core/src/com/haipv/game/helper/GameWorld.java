package com.haipv.game.helper;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.haipv.game.objects.Bird;
import com.haipv.game.objects.ScrollHandler;

public class GameWorld {
	private Bird bird;
	private Rectangle rect = new Rectangle(0, 0, 17, 12);
	private ScrollHandler scroller;
	private boolean isAlive = true;
	private int score = 0;
	private GameState currentState;
	private Rectangle ground;
	private int midPointY;
	private float runTime = 0;

	public enum GameState {
		MENU, READY, RUNNING, GAMEOVER, HIGHSCORE
	}

	public GameWorld(int midPointY) {
		super();
		bird = new Bird(33, midPointY - 5, 17, 12);
		// The grass should start 66 pixels below the midPointY
		// scroller = new ScrollHandler(midPointY + 66);
		scroller = new ScrollHandler(this, midPointY + 66);
		currentState = GameState.READY;
		ground = new Rectangle(0, midPointY + 66, 137, 11);
	}

	public void update(float delta) {
		runTime += delta;
		switch (currentState) {
		case READY:
		case MENU:
			updateReady(delta);
			break;

		case RUNNING:
			updateRunning(delta);
			break;
		default:
			break;
		}
	}

	public boolean isHighScore() {
		return currentState == GameState.HIGHSCORE;
	}

	private void updateReady(float delta) {
		bird.updateReady(runTime);
		scroller.updateReady(delta);
	}

	public void updateRunning(float delta) {
		if (delta > .15f) {
			delta = .15f;
		}

		bird.update(delta);
		scroller.update(delta);

		if (scroller.collides(bird) && bird.isAlive()) {
			scroller.stop();
			bird.die();
			AssetLoader.dead.play();
		}

		if (Intersector.overlaps(bird.getBoundingCircle(), ground)) {
			scroller.stop();
			bird.die();
			bird.decelerate();
			currentState = GameState.GAMEOVER;
			if (score > AssetLoader.getHighScore()) {
				AssetLoader.setHighScore(score);
				currentState = GameState.HIGHSCORE;
			}
		}
	}

	public Bird getBird() {
		return bird;
	}

	public ScrollHandler getScroller() {
		return scroller;
	}

	public void addScore(int increment) {
		score += increment;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}

	public boolean isReady() {
		return currentState == GameState.READY;
	}

	public void start() {
		currentState = GameState.RUNNING;
	}

	public boolean isGameOver() {
		return currentState == GameState.GAMEOVER;
	}

	public void restart() {
		currentState = GameState.READY;
		score = 0;
		bird.onRestart(midPointY - 5);
		scroller.onRestart();
		currentState = GameState.READY;
	}

	public int getMidPointY() {
		return midPointY;
	}

	public void setMidPointY(int midPointY) {
		this.midPointY = midPointY;
	}

	public boolean isMenu() {
		return currentState == GameState.MENU;
	}

	public boolean isRunning() {
		return currentState == GameState.RUNNING;
	}
	
    public void ready() {
        currentState = GameState.READY;
    }
}
