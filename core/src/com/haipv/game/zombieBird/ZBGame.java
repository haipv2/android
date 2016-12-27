package com.haipv.game.zombieBird;

import com.badlogic.gdx.Game;
import com.haipv.game.helper.AssetLoader;
import com.haipv.game.ui.SplashScreen;

public class ZBGame extends Game {

	@Override
	public void create() {
		System.out.println("ZBGame Created!");
		AssetLoader.load();
        setScreen(new SplashScreen(this));
	}
	
	 @Override
	    public void dispose() {
	        super.dispose();
	        AssetLoader.dispose();
	    }

}
