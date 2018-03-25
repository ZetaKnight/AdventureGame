package com.mygdx.game.pete.platformer.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.pete.platformer.PetePlatformer;

/**
 * Created by Amar on 24/10/2017.
 */
public class MenuScreen extends ScreenAdapter {

    private static final float WORLD_HEIGHT = 640;
    private static final float WORLD_WIDTH = 480;

    private SpriteBatch batch;
    private Viewport viewport;
    private OrthographicCamera camera;
    private PetePlatformer petePlatformer;

    private Stage stage;
    private Table contents;
    private Label play;

    private Texture background;

    public MenuScreen(PetePlatformer petePlatformer){
        this.petePlatformer = petePlatformer;
    }

    @Override
    public void show(){
        camera = new OrthographicCamera();
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        camera.update();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_WIDTH, camera);
        batch = new SpriteBatch();
        stage = new Stage(viewport,batch);

        //Handle android directory for Skin
        FileHandle fileHandle = Gdx.files.internal("arcade/skin/arcade-ui.json");
        Skin skin = new Skin(fileHandle);

        play = new Label("PLAY", skin, "default");
        contents = new Table();
        //Set table to fill stage
        contents.setFillParent(true);
        contents.center();
        contents.pad(0);
        contents.add(play);
        stage.addActor(contents);

        background = petePlatformer.getAssetManager().get("door.png", Texture.class);
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

    public void update(){
        //Relative positions of pointer input to screen size
        Vector2 pointer = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        pointer = viewport.unproject(pointer);
        float x = pointer.x;
        float y = pointer.y;

        if(x > play.getX() && x < play.getX()+play.getWidth()
                && y > play.getY() && y < play.getY()+play.getHeight()
                && Gdx.input.isTouched())
            petePlatformer.setScreen(new GameScreen(petePlatformer));
    }

    public void clearScreen(){
        Gdx.gl.glClearColor(Color.LIGHT_GRAY.r, Color.LIGHT_GRAY.g,
                Color.LIGHT_GRAY.b, Color.LIGHT_GRAY.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public void draw(){
        batch.begin();
        batch.draw(background, (camera.position.x)-(background.getWidth()/2), camera.position.y-background.getHeight()/2);
        batch.end();
        stage.act();
        stage.draw();

    }


}
