package com.mygdx.game.pete.platformer.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.pete.platformer.Controls;
import com.mygdx.game.pete.platformer.Entities.NPC_1;
import com.mygdx.game.pete.platformer.Entities.Player;
import com.mygdx.game.pete.platformer.PetePlatformer;

/**
 * Created by Amar on 06/08/2017.
 */
public class LoadingScreen extends ScreenAdapter {

    private static final float WORLD_HEIGHT = 640;
    private static final float WORLD_WIDTH = 480;
    private static final float PROGRESS_BAR_WIDTH = 100;
    private static final float PROGRESS_BAR_HEIGHT = 25;
    private ShapeRenderer shapeRenderer;
    private Viewport viewport;
    private OrthographicCamera camera;
    private float progress = 0;
    private PetePlatformer petePlatformer;

    public LoadingScreen(PetePlatformer petePlatformer){
        this.petePlatformer = petePlatformer;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        camera.update();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_WIDTH, camera);
        shapeRenderer = new ShapeRenderer();
        petePlatformer.getAssetManager().load("level1.tmx", TiledMap.class);
        petePlatformer.getAssetManager().load("level2.tmx", TiledMap.class);
        petePlatformer.getAssetManager().load("stage1.tmx", TiledMap.class);
//        petePlatformer.getAssetManager().load("pete.png", Texture.class);
        petePlatformer.getAssetManager().load(Player.CHARACTER, Texture.class);
        petePlatformer.getAssetManager().load(NPC_1.CHARACTER, Texture.class);
        petePlatformer.getAssetManager().load(Controls.CONTROLS, Texture.class);
        petePlatformer.getAssetManager().load("door.png", Texture.class);
        petePlatformer.getAssetManager().load(com.mygdx.game.pete.platformer.Entities.PaperBall.STACKED_PAPER, Texture.class);
        petePlatformer.getAssetManager().load(com.mygdx.game.pete.platformer.Entities.PaperBall.PAPER_BALL, Texture.class);
        petePlatformer.getAssetManager().load(com.mygdx.game.pete.platformer.Entities.PaperBall.PAPER_BALL_BANG, Texture.class);
        petePlatformer.getAssetManager().load("jump2.wav", Sound.class);
        petePlatformer.getAssetManager().load("sacorn.wav", Sound.class);
        petePlatformer.getAssetManager().load("open_interior_wood_door.mp3", Sound.class);
        petePlatformer.getAssetManager().load("peteTheme.mp3", Music.class);

        petePlatformer.getAssetManager().finishLoading();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void render(float delta) {
        update();
        clearScreen();
        draw();
    }

    private void update(){
        if(petePlatformer.getAssetManager().update()){
//            petePlatformer.setScreen(new GameScreen(petePlatformer));
            petePlatformer.setScreen(new MenuScreen(petePlatformer));
        }else{
            progress = petePlatformer.getAssetManager().getProgress();
        }
    }

    private void clearScreen(){
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void draw(){
        shapeRenderer.setProjectionMatrix(camera.projection);
        shapeRenderer.setTransformMatrix(camera.view);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(
                (WORLD_WIDTH - PROGRESS_BAR_WIDTH) / 2, (WORLD_HEIGHT - PROGRESS_BAR_HEIGHT) / 2,
                progress * PROGRESS_BAR_WIDTH, PROGRESS_BAR_HEIGHT);
        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
