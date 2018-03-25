package com.mygdx.game.pete.platformer.Entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by Amar on 29/09/2017.
 */
public class PaperBall {
    public static final String STACKED_PAPER = "stacked_paper.png";
    public static final String PAPER_BALL = "paper.png";
    public static final String PAPER_BALL_BANG = "paper_bang.png";
    public static final int ANIMATION_TIMER = 250;
    private float x ,y;
    private float xSpeed;
    private float ySpeed;
    private Rectangle collisionRect;
    private Batch batch;
    private Texture texture;
    private static final int SIZE = 8;
    private boolean hit = false;
    private long startTimer;
    public boolean die = false;

    public PaperBall(Batch batch, Texture texture, float x,float y, float xSpeed, float ySpeed){
        this.x = x;
        this.y = y;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.batch = batch;
        this.texture = texture;
        collisionRect = new Rectangle(x,y,SIZE,SIZE);
    }

    public void update(float delta){
        if(hit){
            if(System.currentTimeMillis()> startTimer+ANIMATION_TIMER)die=true;
        }else {
            x += xSpeed;
            y += ySpeed;
            updateCollisionRectangle();
        }
    }

    public void draw(){
        batch.draw(texture, x, y);
    }

    private void updateCollisionRectangle(){
        collisionRect.setPosition(x, y);
    }

    public void setHit(boolean hit){
        this.hit = hit;
        startTimer = System.currentTimeMillis();
    }

    public boolean getHit(){
        return hit;
    }

    public void setTexture(Texture texture){
        this.texture = texture;
    }

    public float getySpeed() {
        return ySpeed;
    }

    public void setySpeed(float ySpeed) {
        this.ySpeed = ySpeed;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getxSpeed() {
        return xSpeed;
    }

    public void setxSpeed(float xSpeed) {
        this.xSpeed = xSpeed;
    }

    public Rectangle getCollisionRect() {
        return collisionRect;
    }
}
