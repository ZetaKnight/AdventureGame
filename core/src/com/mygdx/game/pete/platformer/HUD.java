package com.mygdx.game.pete.platformer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.pete.platformer.Screens.GameScreen;

/**
 * Created by Amar on 24/09/2017.
 */
public class HUD {

    private static final int SCORE_INCREMENT= 10;
    private int score = 0;
    private BitmapFont bitmapFont;
    private GameScreen gameScreen;
    private OrthographicCamera cam;
    private Viewport viewPort;
    private Stage stage;
    boolean upPressed;
    boolean leftPressed;
    boolean rightPressed;
    boolean attackPressed;


    public HUD(GameScreen gameScreen){
        this.gameScreen = gameScreen;
        bitmapFont= new BitmapFont();
    }

    public void draw(Batch batch){
        bitmapFont.draw(
                batch, "SCORE: " + score,
                gameScreen.getViewport().getCamera()
                        .position.x - (GameScreen.WIDTH/2),
                gameScreen.getViewport().getCamera()
                .position.y + (GameScreen.HEIGHT/2)
        );
    }

    public void incrementScore() {
        score += SCORE_INCREMENT;
    }
}
