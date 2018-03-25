package com.mygdx.game.pete.platformer;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.mygdx.game.pete.platformer.Screens.GameScreen;

/**
 * Created by Amar on 24/09/2017.
 */
public class HUD {

    private static final int SCORE_INCREMENT= 10;
    private int score = 0;
    private BitmapFont bitmapFont;
    private GameScreen gameScreen;

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
