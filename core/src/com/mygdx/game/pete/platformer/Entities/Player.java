package com.mygdx.game.pete.platformer.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.pete.platformer.CollisionCell;
import com.mygdx.game.pete.platformer.Screens.GameScreen;

/**
 * Created by Amar on 22/08/2017.
 */
public class Player {
    private static final float MAX_X_SPEED = 4;
    private static final float MAX_Y_SPEED = 4;
    public static final int WIDTH = 32;
    public static final int HEIGHT = 32;
    public enum DOOR_ENTRY {FROZEN, UNFROZEN}
    public DOOR_ENTRY door_entry;
    public enum Dir{WALKING_LEFT,WALKING_RIGHT}
    public Dir direction;
    private final Rectangle collisionRect = new Rectangle(0,0,WIDTH,HEIGHT);
    private float x = 48;
    private float y = 48;
    private float xSpeed = 0;
    private float ySpeed = 0;
    private boolean blockJump = false;
    private float jumpYDistance = 0;
    private static final float MAX_JUMP_DISTANCE = 3 * HEIGHT;
    private float animationTimer = 0;
    private Animation walking;
    private Animation standing;
    private Animation climbing;
    private TextureRegion climbedOn;
    private TextureRegion jumpUp;
    private TextureRegion jumpDown;
    private boolean onLand;
    private Sound jumpSound;
    private boolean openDoor;
    private boolean onClimbable, isClimbing;
    private TextureRegion toDraw;
    public static final String CHARACTER = "player-sprites.png";
    private Batch batch;
    private boolean throwPaperBallRight = false, throwPaperBallLeft = false;
    private int health;

    private GameScreen gameScreen;


    public Player(Texture texture, Sound jumpSound, Batch batch, GameScreen gameScreen){
        this.gameScreen = gameScreen;
        this.batch = batch;
        this.jumpSound = jumpSound;
        TextureRegion[] regions = TextureRegion.split(texture, WIDTH,HEIGHT)[0];
        standing = new Animation(.2f, regions[4],regions[5]);
        standing.setPlayMode(Animation.PlayMode.LOOP);
        walking =  new Animation(.1f, regions[6], regions[7], regions[8]);
        walking.setPlayMode(Animation.PlayMode.LOOP);
        climbing =  new Animation(.25f, regions[0], regions[1]);
        climbing.setPlayMode(Animation.PlayMode.LOOP);
        climbedOn = regions[0];
        jumpUp = regions[3];
        jumpDown = regions[2];
        onLand = false;
        onClimbable = false;
        isClimbing = false;
        openDoor = false;
        door_entry = DOOR_ENTRY.UNFROZEN;
        health = 3;
    }

    public void update(float delta){
        animationTimer +=delta;
        Input input = Gdx.input;
        if (health > 0) {
            controlInput(input);
        }
        x += xSpeed;
        y += ySpeed;
        updateCollisionRectangle();
    }

    private void controlInput(Input input){
        if (input.isKeyPressed(Input.Keys.RIGHT) ||
                (input.isTouched() && input.getX() > (Gdx.graphics.getWidth() / 2)) &&
                        input.getY() > 100) {
            moveRight();
        } else if (input.isKeyPressed(Input.Keys.LEFT) ||
                (input.isTouched() && input.getX() < (Gdx.graphics.getWidth() / 2)) &&
                        input.getY() > 100) {
            moveLeft();
        } else {
            xSpeed = 0;
        }
        if (input.isKeyPressed(Input.Keys.UP)){
            onClimbable = handlePlayerOnClimbable();
            isClimbing = onClimbable;
        }
        if (onClimbable && isClimbing) {
            onClimbable = handlePlayerOnClimbable();
            isClimbing = onClimbable;
            if (input.isKeyPressed(Input.Keys.RIGHT))
                xSpeed = MAX_X_SPEED;

            else if (input.isKeyPressed(Input.Keys.LEFT))
                xSpeed = -MAX_X_SPEED;

            if (input.isKeyPressed(Input.Keys.DOWN))
                ySpeed = -MAX_Y_SPEED;

            else if (input.isKeyPressed(Input.Keys.UP))
                ySpeed = MAX_Y_SPEED;

            else ySpeed = 0;

            if (input.isKeyPressed(Input.Keys.A)) isClimbing = false;
        } else if ((input.isKeyPressed(Input.Keys.A) ||
                (input.isTouched() && input.getY() < 150))
                && !blockJump) {
            if (ySpeed != MAX_Y_SPEED) jumpSound.play();
            //Build climbing function --------------------------------
            ySpeed = MAX_Y_SPEED;
            jumpYDistance += ySpeed;
            blockJump = jumpYDistance > MAX_JUMP_DISTANCE;
        } else {
            // if not falling
            if (!onLand) {
                ySpeed = -MAX_Y_SPEED;
                blockJump = jumpYDistance > 0;
            } else {
                // if in the air
                ySpeed = -2;
                blockJump = jumpYDistance > 0;
            }
        }
        //If player is positioned at door, flag open door
        if (input.isKeyPressed(Input.Keys.SPACE) && door_entry == DOOR_ENTRY.UNFROZEN) {
            setOpenDoor(true);
        }
        if (input.isKeyJustPressed(Input.Keys.S)) {
            //shoot
            if (toDraw.isFlipX()) {
                setThrowPaperBallLeft(true);
            } else if (!toDraw.isFlipX()) {
                setThrowPaperBallRight(true);
            }
        }

    }

    private void moveRight(){
        direction = Dir.WALKING_RIGHT;
        xSpeed = MAX_X_SPEED;
    }

    private void moveLeft(){
        direction = Dir.WALKING_LEFT;
        xSpeed = -MAX_X_SPEED;
    }

    public boolean handlePlayerOnClimbable(){
        float x = this.getX();
        float y = this.getY();

        float cellX = x/ GameScreen.CELL_SIZE;
        float cellY = y/GameScreen.CELL_SIZE;

        int bottomLCellX = MathUtils.floor(cellX);
        int bottomLCellY = MathUtils.floor(cellY);
        Array<CollisionCell> cellsCovered = new Array<CollisionCell>();
        TiledMapTileLayer tiledMapTileLayer = (TiledMapTileLayer) gameScreen.getTiledMap().getLayers().get(GameScreen.CLIMBABLE);
        cellsCovered.add(new CollisionCell(tiledMapTileLayer.getCell(bottomLCellX, bottomLCellY), bottomLCellX, bottomLCellY));
        cellsCovered = gameScreen.filterOutNonTiledCells(cellsCovered);

        return (cellsCovered.size>0) ? true : false;
    }

    public void draw(){
        toDraw = standing.getKeyFrame(animationTimer);
        if(isClimbing && (xSpeed != 0 || ySpeed != 0)) {
            toDraw = climbing.getKeyFrame(animationTimer);
        }else if(isClimbing){
            toDraw = climbedOn;
        }else {

            if (xSpeed != 0) {
                toDraw = walking.getKeyFrame(animationTimer);
            }
            if (ySpeed > 0) {
                toDraw = jumpUp;
            } else if (ySpeed < 0) {
                toDraw = jumpDown;
            }

            //  if facing left
            if (xSpeed < 0 || direction == Dir.WALKING_LEFT) {
                if (!toDraw.isFlipX()) toDraw.flip(true, false);
                // if facing right
            } else if (xSpeed > 0 || direction == Dir.WALKING_RIGHT) {
                if (toDraw.isFlipX()) toDraw.flip(true, false);
            }

        }

        batch.draw(toDraw, x, y);

    }

    public void drawDebug(ShapeRenderer shapeRenderer){
        shapeRenderer.rect(collisionRect.x, collisionRect.y, collisionRect.width, collisionRect.height);
    }

    private void updateCollisionRectangle(){
        collisionRect.setPosition(x, y);
    }

    public void setPosition(float x, float y){
        this.x = x;
        this.y = y;
        updateCollisionRectangle();
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }

    public void landed(){
        blockJump = false;
        jumpYDistance = 0;
        ySpeed = 0;
    }

    public void setBlockJump(boolean blockJump) {
        this.blockJump = blockJump;
    }

    public void setOnLand(boolean onLand){
        this.onLand = onLand;
    }

    public boolean getOnLand(){
        return onLand;
    }

    public Rectangle getCollisionRect(){
        return collisionRect;
    }

    public boolean isOpenDoor() {
        return openDoor;
    }

    public void setOpenDoor(boolean openDoor) {
        this.openDoor = openDoor;
    }

    public boolean isThrowPaperBallRight() {
        return throwPaperBallRight;
    }

    public void setThrowPaperBallRight(boolean throwPaperBallRight) {
        this.throwPaperBallRight = throwPaperBallRight;
    }

    public boolean isThrowPaperBallLeft() {
        return throwPaperBallLeft;
    }

    public void setThrowPaperBallLeft(boolean throwPaperBallLeft) {
        this.throwPaperBallLeft = throwPaperBallLeft;
    }

    public void setOnClimbable(boolean onClimbable){
        this.onClimbable = onClimbable;
    }

    public void setIsClimbing(boolean isClimbing){
        this.isClimbing = isClimbing;
    }

    public int getHealth() {
        return health;
    }

    public void deductHealth(){
        this.health--;
    }
}
