package com.mygdx.game.pete.platformer.Transitions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

import static com.badlogic.gdx.utils.TimeUtils.millis;

/**
 * Created by Amar on 25/03/2018.
 */

public class FadeInTransition {

    private float alpha;

    public FadeInTransition(){
        alpha = 1;
    }

    public void update(float deltaTime){
        alpha -= (60 * deltaTime);
        draw();
        System.out.println("Current deltaTime: " + deltaTime + ", Current alpha value: " + alpha);
    }

    private void draw(){
        // clear screen w/ black
        Gdx.gl.glClearColor(0, 0, 0, alpha);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public boolean isTransistionDone(){
        return alpha<0;
    }
}
