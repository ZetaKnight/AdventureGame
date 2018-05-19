package com.mygdx.game.pete.platformer.Controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.pete.platformer.Screens.GameScreen;

public class OnScreenController {
    public static final String RIGHT_PNG = "right.png";
    public static final String LEFT_PNG = "left.png";
    public static final String ATTACK_PNG = "attack.png";
    public static final String UP_PNG = "up.png";
    private OrthographicCamera cam;
    private Viewport viewPort;
    private Stage stage;
    private boolean upPressed;
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean attackPressed;

    Image rightImg;
    Image leftImg;
    Image attackImg;
    Image upImg;

    public OnScreenController(GameScreen gameScreen, Batch batch){
        cam = new OrthographicCamera();
        viewPort = new FitViewport(GameScreen.WIDTH, GameScreen.HEIGHT, cam);
        stage = new Stage(viewPort, batch);
        Gdx.input.setInputProcessor(stage);

        rightImg = new Image(new Texture(RIGHT_PNG));
        rightImg.setSize(50, 50);
        rightImg.addListener(getRightButtonListener());

        leftImg = new Image(new Texture(LEFT_PNG));
        leftImg.setSize(50, 50);
        leftImg.addListener(getLeftButtonListener());

        Table table = new Table();
        table.left().bottom();

        table.row().pad(1, 1, 1, 1);
        table.add(leftImg).size(leftImg.getWidth(), leftImg.getHeight());
        table.add(rightImg).size(rightImg.getWidth(), rightImg.getHeight());

        attackImg = new Image(new Texture(ATTACK_PNG));
        attackImg.setSize(50, 50);
        attackImg.addListener(getAttackButtonListener());

        upImg = new Image(new Texture(UP_PNG));
        upImg.setSize(50, 50);
        upImg.addListener(getUpButtonListener());

        Table actionButtons = new Table();
        actionButtons.bottom().right();

        actionButtons.setFillParent(true);
        actionButtons.row().pad(1, 1, 1, 1);
        actionButtons.add(attackImg).size(attackImg.getWidth(), attackImg.getHeight());
        actionButtons.add(upImg).size(upImg.getWidth(), upImg.getHeight());

        stage.addActor(table);
        stage.addActor(actionButtons);

        leftPressed = false;
        rightPressed = false;

        attackPressed = false;
    }

    private InputListener getRightButtonListener(){
        return new InputListener(){

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                rightPressed = true;
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                rightPressed = false;
            }

        };
    }

    private InputListener getLeftButtonListener(){
        return new InputListener(){

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                leftPressed = true;
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                leftPressed = false;
            }

        };
    }

    private InputListener getAttackButtonListener(){
        return new InputListener(){

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                attackPressed = true;
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                attackPressed = false;
            }

        };
    }

    private InputListener getUpButtonListener(){
        return new InputListener(){

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                upPressed = true;
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                upPressed = false;
            }

        };
    }

    public void draw(){
        stage.getBatch().end();
        stage.act();
        stage.draw();
        stage.getBatch().begin();
    }

    public boolean getLeftPressed(){
        return leftPressed;
    }

    public boolean getRightPressed(){
        return rightPressed;
    }

    public boolean getAttackPressed(){
        return attackPressed;
    }

    public boolean getUpPressed(){
        return upPressed;
    }
}