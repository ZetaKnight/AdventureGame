package com.mygdx.game.pete.platformer.Entities;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by Amar on 04/10/2017.
 */
public class NPC_1 {
    public enum State{STANDING, WALKING, SITTING}
    public enum Type{ENEMY, FOLLOWING, WAITING};
    public static final String CHARACTER = "npc-1_animation.png";
    public static final int WIDTH = 32;
    public static final int HEIGHT = 32;
    private static final float MAX_SPEED_X = 3;
    private static final float MAX_SPEED_Y = 2;
    private static final float RADIUS = WIDTH * 2;
    private static final long ATTACK_TIMER = 60;
    private float x ,y;
    private float xSpeed;
    private float ySpeed;
    private Rectangle collisionRect;
    private Batch batch;
    private Animation walking;
    private Animation fallingBack;
    private TextureRegion standing;
    private TextureRegion toDraw;
    private boolean hit = false;
    private Sound sound;
    private Player player;
    private float animationTimer = 0;
    private Type npcType;
    private State currentState;
    private State previousState;
    public boolean die = false;
    private float timer = 1;

    public NPC_1(Texture texture, Sound sound, Batch batch, float x,float y, Player player, String type){
        this.player = player;
        this.x = x;
        this.y = y;
        this.sound = sound;
        this.batch = batch;
        TextureRegion[] regions = TextureRegion.split(texture, WIDTH,HEIGHT)[0];
        walking =  new Animation(.25f, regions[0], regions[1]);
        walking.setPlayMode(Animation.PlayMode.LOOP);
        fallingBack =  new Animation(.45f, regions[2], regions[3]);
        fallingBack.setPlayMode(Animation.PlayMode.NORMAL);
        standing = regions[0];
        collisionRect = new Rectangle(x,y,WIDTH,HEIGHT);

        if(type.equals("following")){
            npcType = Type.FOLLOWING;
            previousState = State.WALKING;
            currentState = State.WALKING;
        }
        if(type.equals("waiting")){
            npcType = Type.WAITING;
            previousState = State.STANDING;
            currentState = State.STANDING;
        }
        if(type.equals("enemy")){
            npcType = Type.ENEMY;
            previousState = State.STANDING;
            currentState = State.WALKING;
        }

    }

    public void update(float delta){

        animationTimer +=delta;

        if(hit) currentState = State.SITTING;

        if(npcType == Type.FOLLOWING && currentState != State.SITTING)
            if((this.x < player.getX() && this.x > player.getX()-RADIUS) || (this.x > player.getX() && this.x < player.getX()+RADIUS)) {
                xSpeed = 0;
            }else{
                if (player.getX() < this.x) xSpeed = -MAX_SPEED_X;
                else if (player.getX() > this.x) xSpeed = MAX_SPEED_X;

                ySpeed = -MAX_SPEED_Y;
            }

        if(currentState == State.SITTING){
            xSpeed = 0;
            ySpeed = -MAX_SPEED_Y;
        }

        if(npcType == Type.ENEMY && currentState != State.SITTING){
            if((this.x < player.getX() && this.x > player.getX()-RADIUS+48) ||
                (this.x > player.getX() && this.x < player.getX()+(RADIUS-48))) {
                xSpeed = 0;
                attackPlayer(delta);
            }else{
                if (player.getX() < this.x) xSpeed = -MAX_SPEED_X;
                else if (player.getX() > this.x) xSpeed = MAX_SPEED_X;

                ySpeed = -MAX_SPEED_Y;
            }
        }

        x += xSpeed;
        y += ySpeed;
        updateCollisionRectangle();
    }

    private void updateCollisionRectangle(){
        collisionRect.setPosition(x, y);
    }

    private void attackPlayer(float deltaTime){
        timer -=deltaTime;
        if(timer <= 0) {
            player.deductHealth();
            timer = 1;
        }
    }

    public void draw(){
        toDraw = standing;

        if(xSpeed !=0 && currentState != State.SITTING){
            toDraw = walking.getKeyFrame(animationTimer);
        }

        if(previousState == State.WALKING && currentState == State.SITTING){
            toDraw = fallingBack.getKeyFrame(animationTimer);
        }

        //  if facing left
        if(xSpeed < 0){
            if(!toDraw.isFlipX()) toDraw.flip(true, false);
        }
        // if facing right
        else if(xSpeed > 0){
            if(toDraw.isFlipX()) toDraw.flip(true, false);
        }

        batch.draw(toDraw, x, y);
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public Rectangle getCollisionRect() {
        return collisionRect;
    }

    public void setCollisionRect(Rectangle collisionRect) {
        this.collisionRect = collisionRect;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }
}
