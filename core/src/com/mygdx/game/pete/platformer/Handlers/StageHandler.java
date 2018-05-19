package com.mygdx.game.pete.platformer.Handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.pete.platformer.Entities.Player;
import com.mygdx.game.pete.platformer.PetePlatformer;
import com.mygdx.game.pete.platformer.Screens.GameScreen;
import com.mygdx.game.pete.platformer.Transitions.FadeInTransition;

import static com.badlogic.gdx.utils.TimeUtils.millis;

/**
 * Created by Amar on 11/03/2018.
 */
public class StageHandler {

    public static final String DOOR = "Door";
    public static final String PASSAGE = "Passage";
    public static final String LOCATION_X = "locationX";
    public static final String LOCATION_Y = "locationY";
    public static final String X = "x";
    public static final String Y = "y";
    public static final String OPEN = "open";
    public static final String DIRECTION_THROUGH = "directionThrough";

    private final GameScreen gameScreen;
    private Player player;
    private PetePlatformer petePlatformer;
    private long currentTime = millis();
    private long unfreezePlayerControlsAndAnimationTime;
    private FadeInTransition fadeInTransition;
    private Sound doorOpen;

    public StageHandler(GameScreen gameScreen, Player player, PetePlatformer petePlatformer) {
        this.gameScreen = gameScreen;
        this.player = player;
        this.petePlatformer = petePlatformer;
        fadeInTransition = null;
        doorOpen = petePlatformer.getAssetManager()
                   .get("open_interior_wood_door.mp3",Sound.class);
    }

    public void update(float deltaTime){
        currentTime = millis();
        handleDoor();
        handlePassage();
    }

    private void handleDoor(){
        if(player.door_entry == Player.DOOR_ENTRY.FROZEN) {
            if(unfreezePlayerControlsAndAnimationTime <= currentTime)
                player.door_entry = Player.DOOR_ENTRY.UNFROZEN;
        }else{
            handleDoorOpening();
        }
    }

    private void handleDoorOpening(){
        if(player.isOpenDoor()){
            MapLayer mapLayer = gameScreen.getTiledMap().getLayers().get(DOOR);
            for(MapObject object: mapLayer.getObjects()){
                float newLocX = Float.valueOf(String.valueOf(object.getProperties().get(LOCATION_X)));
                float newLocY = Float.valueOf(String.valueOf(object.getProperties().get(LOCATION_Y)));
                float x = (object.getProperties().get(X, Float.class));
                float y = (object.getProperties().get(Y, Float.class));
                String stage = String.valueOf(object.getProperties().get(OPEN));
                if(player.getCollisionRect()
                        .overlaps(new Rectangle(x, y,GameScreen.CELL_SIZE*2,
                                        GameScreen.CELL_SIZE*4))){
                    handleNextLevel(stage, newLocX, newLocY);
                }
                player.setOpenDoor(false);
            }
        }
    }

    private void handleNextLevel(String stage, float x, float y){
        player.door_entry = Player.DOOR_ENTRY.FROZEN;
        unfreezePlayerControlsAndAnimationTime = currentTime + (1000);
        gameScreen.getNpcs().clear();
        gameScreen.getStackedPapers().clear();
        gameScreen.getPaperBalls().clear();
        gameScreen.setTiledMap((TiledMap) petePlatformer.getAssetManager().get(stage));
        gameScreen.GetOrthogonalTiledMapRenderer().setMap(gameScreen.getTiledMap());
        player.setPosition(16 * x, 16 * y);
        gameScreen.populateNPCs();
        gameScreen.populateStackedPaper();
        doorOpen.play();
    }

    private void handlePassage(){
        MapLayer mapLayer = gameScreen.getTiledMap().getLayers().get(PASSAGE);
        if(mapLayer != null){
            for(MapObject object: mapLayer.getObjects()){
                float newLocX = Float.valueOf(String.valueOf(object.getProperties().get(LOCATION_X)));
                float newLocY = Float.valueOf(String.valueOf(object.getProperties().get(LOCATION_Y)));
                float xPosOfObject = (object.getProperties().get(X, Float.class));
                float yPosOfObject = (object.getProperties().get(Y, Float.class));
                String stage = String.valueOf(object.getProperties().get(OPEN));
                String directionToMoveThrough = String.valueOf(object.getProperties().get(DIRECTION_THROUGH));
                if(directionToMoveThrough.equals("right")){
                    if(player.isMovingRight() && player.getCollisionRect().overlaps(
                            new Rectangle(xPosOfObject, yPosOfObject, 1,
                            GameScreen.CELL_SIZE*4))){
                        handleNextLevel(stage, newLocX, newLocY);
                        fadeInTransition = new FadeInTransition(gameScreen.getCamera());
                    }
                }else if(directionToMoveThrough.equals("left")){
                    if(player.isMovingLeft() && player.getCollisionRect().overlaps(
                    new Rectangle(xPosOfObject, yPosOfObject, 1,
                    GameScreen.CELL_SIZE*4))){
                        handleNextLevel(stage, newLocX, newLocY);
                        fadeInTransition = new FadeInTransition(gameScreen.getCamera());
                    }
                }
            }
        }
    }

    private void handleTransition(float deltaTime){
        if(fadeInTransition != null)
            if(!fadeInTransition.isTransitionDone()){
                fadeInTransition.update(deltaTime);
            }else{
                fadeInTransition = null;
            }
    }

    public void draw(float deltaTime){
        handleTransition(deltaTime);
    }

}
