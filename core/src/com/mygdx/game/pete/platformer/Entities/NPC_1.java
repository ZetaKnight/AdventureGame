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
    public enum State{STANDING, WALKING, SITTING, ATTACKING}
    public enum Type{ENEMY, FOLLOWING, WAITING};
    public static final String CHARACTER = "npc-sprites.png";
    public static final int WIDTH = 32;
    public static final int HEIGHT = 32;
    private static final float MAX_SPEED_X = 3;
    private static final float MAX_SPEED_Y = 2;
    private static final float RADIUS = WIDTH * 2;
    private static final float ENEMY_RADIUS = WIDTH;
    private static final long ATTACK_TIMER = 60;
    private float x ,y;
    private float xSpeed;
    private float ySpeed;
    private Rectangle collisionRect;
    private Batch batch;
    private Animation walking;
    private Animation fallingBack;
    private Animation standing;
    private Animation pushing;
    private TextureRegion toDraw;
    private int health = 2;
    private boolean hit = false;
    private Sound sound;
    private Player player;
    private float animationTimer = 0;
    private Type npcType;
    private State currentState;
    private State previousState;
    private float timer = 1;

    public NPC_1(Texture texture, Sound sound, Batch batch, float x,float y, Player player, String type){
        this.player = player;
        this.x = x;
        this.y = y;
        this.sound = sound;
        this.batch = batch;
        TextureRegion[] regions = TextureRegion.split(texture, WIDTH,HEIGHT)[0];
        walking =  new Animation(.15f, regions[9], regions[10], regions[11], regions[12]);
        walking.setPlayMode(Animation.PlayMode.LOOP);
        fallingBack =  new Animation(.2f, regions[0], regions[1], regions[2], regions[3]);
        fallingBack.setPlayMode(Animation.PlayMode.NORMAL);
        standing = new Animation(.25f, regions[7], regions[8]);
        standing.setPlayMode(Animation.PlayMode.LOOP);
        pushing = new Animation(.25f, regions[4], regions[5], regions[6]);
        pushing.setPlayMode(Animation.PlayMode.LOOP);
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

//        if(npcType == Type.FOLLOWING && currentState != State.SITTING)
//            if((this.x < player.getX() && this.x > player.getX()-RADIUS) || (this.x > player.getX() && this.x < player.getX()+RADIUS)) {
//                xSpeed = 0;
//            }else{
//                if (player.getX() < this.x) xSpeed = -MAX_SPEED_X;
//                else if (player.getX() > this.x) xSpeed = MAX_SPEED_X;
//
//                ySpeed = -MAX_SPEED_Y;
//            }
//
//        if(currentState == State.SITTING){
//            xSpeed = 0;
//            ySpeed = -MAX_SPEED_Y;
//        }

        if(npcType == Type.ENEMY && currentState != State.SITTING){
            if ((isInPlayersRadiusFromLeftSide() || isInPlayersRadiusFromRightSide()) &&
                y > player.getY() && y < player.getY()+16){
                xSpeed = 0;
                previousState = previousState != currentState ? currentState : previousState;
                currentState = State.ATTACKING;
                attackPlayer(delta);
            }else{
                if(isPlayerOutOfRange()){
                    previousState = previousState != currentState ? currentState : previousState;
                    currentState = State.STANDING;
                }else{
                    moveTowardsPlayer();
                }
            }

        }

        if(currentState == State.STANDING || currentState == State.SITTING) xSpeed = 0;

        x += xSpeed;
        y += ySpeed;
        updateCollisionRectangle();
    }

    private boolean isInPlayersRadiusFromLeftSide(){
        return x + (WIDTH/2) <= player.getX() + (Player.WIDTH/2) &&
               x + (WIDTH/2) > player.getX();
    }

    private boolean isInPlayersRadiusFromRightSide(){
        return x + (WIDTH/2) >= player.getX() + (Player.WIDTH/2) &&
               x + (WIDTH/2) < player.getX() + Player.WIDTH;
    }

    private void moveTowardsPlayer(){
        currentState = State.WALKING;
        if (x + (WIDTH / 2) < (player.getX() + (Player.WIDTH / 2)))
            xSpeed = MAX_SPEED_X;
        else if (x + (WIDTH / 2) > (player.getX() + (Player.WIDTH / 2)))
            xSpeed = -MAX_SPEED_X;
        ySpeed = -MAX_SPEED_Y;
    }

    private boolean isPlayerOutOfRange(){
        return isPlayerAbove();
    }

    private boolean isPlayerAbove(){
        return y < player.getY();
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
        toDraw = standing.getKeyFrame(animationTimer);

        if(hit){
            initiateAnimationTimerToZeroOnce();
            toDraw = fallingBack.getKeyFrame(animationTimer);
        }

        if(xSpeed !=0 && currentState != State.SITTING){
            toDraw = walking.getKeyFrame(animationTimer);
        }

        if(previousState == State.WALKING && currentState == State.SITTING){
            toDraw = fallingBack.getKeyFrame(animationTimer);
        }

        if(currentState == State.ATTACKING){
            toDraw = pushing.getKeyFrame(animationTimer);
        }

        //  if facing left
        if(x + (WIDTH / 2) > (player.getX() + (Player.WIDTH / 2))){
            if(!toDraw.isFlipX()) toDraw.flip(true, false);
        }
        // if facing right
        else if(x + (WIDTH / 2) < (player.getX() + (Player.WIDTH / 2))){
            if(toDraw.isFlipX()) toDraw.flip(true, false);
        }

        batch.draw(toDraw, x, y);
    }

    private boolean animationTimerSetZero = false;
    private void initiateAnimationTimerToZeroOnce(){
        if(!animationTimerSetZero){
            animationTimerSetZero = true;
            animationTimer = 0;
        }
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
