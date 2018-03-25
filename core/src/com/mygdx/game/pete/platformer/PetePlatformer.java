package com.mygdx.game.pete.platformer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class PetePlatformer extends Game {
	private final AssetManager assetManager = new AssetManager();

	
	@Override
	public void create () {
		assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
		setScreen(new com.mygdx.game.pete.platformer.Screens.LoadingScreen(this));
	}

	public AssetManager getAssetManager(){
		return assetManager;
	}




}
