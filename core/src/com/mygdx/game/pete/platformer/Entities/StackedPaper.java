package com.mygdx.game.pete.platformer.Entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by Amar on 04/09/2017.
 */
public class StackedPaper {
    public static final int WIDTH = 16;
    public static final int HEIGHT = 16;
    private Rectangle collision;
    private Texture texture;
    private float x;
    private float y;

    public StackedPaper(Texture texture, float x, float y){
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.collision = new Rectangle(x, y, WIDTH, HEIGHT);
    }

    public void draw(Batch batch){
        batch.draw(texture, x, y);
    }

    public Rectangle getCollision(){
        return collision;
    }
}
