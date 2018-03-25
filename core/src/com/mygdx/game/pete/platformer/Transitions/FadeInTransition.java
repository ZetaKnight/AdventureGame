package com.mygdx.game.pete.platformer.Transitions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.pete.platformer.Screens.GameScreen;

import static com.badlogic.gdx.utils.TimeUtils.millis;

/**
 * Created by Amar on 25/03/2018.
 */

public class FadeInTransition {

    private float alpha;
    private ShapeRenderer shapeRenderer;
    private Camera camera;

    public FadeInTransition(Camera camera){
        alpha = 1;
        shapeRenderer = new ShapeRenderer();
        this.camera = camera;
    }

    public void update(float deltaTime){
        alpha -= deltaTime *1;
        draw();
        System.out.println("Current deltaTime: " + deltaTime + ", Current alpha value: " + alpha);
    }

    private void draw(){
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0, 0, 0, alpha));
        shapeRenderer.rect(
                camera.position.x - (GameScreen.WIDTH/2),
                camera.position.y - (GameScreen.HEIGHT/2),
                GameScreen.WIDTH,
                GameScreen.HEIGHT);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public boolean isTransitionDone(){
        return alpha<0;
    }
}
