package com.mygdx.game.pete.platformer.Handlers;

import com.badlogic.gdx.Gdx;
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

    private final GameScreen gameScreen;
    private Player player;
    private PetePlatformer petePlatformer;
    private long currentTime = millis();
    private long unfreezePlayerControlsAndAnimationTime;
    private FadeInTransition fadeInTransition;

    public StageHandler(GameScreen gameScreen, Player player, PetePlatformer petePlatformer) {
        this.gameScreen = gameScreen;
        this.player = player;
        this.petePlatformer = petePlatformer;
        fadeInTransition = null;
    }

    public void update(float deltaTime){
        currentTime = millis();
        if(player.door_entry == Player.DOOR_ENTRY.FROZEN) {
            if(unfreezePlayerControlsAndAnimationTime <= currentTime)
                player.door_entry = Player.DOOR_ENTRY.UNFROZEN;
        }else{
            handleDoorOpening();
        }
    }

    private void handleDoorOpening(){
        if(player.isOpenDoor()){
            fadeInTransition = new FadeInTransition();
            MapLayer mapLayer = gameScreen.getTiledMap().getLayers().get(DOOR);
            for(MapObject object: mapLayer.getObjects()){
                float newLocX = Float.valueOf(String.valueOf(object.getProperties().get("locationX")));
                float newLocY = Float.valueOf(String.valueOf(object.getProperties().get("locationY")));
                float x = (object.getProperties().get("x", Float.class));
                float y = (object.getProperties().get("y", Float.class));
                String stage = String.valueOf(object.getProperties().get("open"));
                if(player.getCollisionRect().overlaps(new Rectangle(x, y, GameScreen.CELL_SIZE*2, GameScreen.CELL_SIZE*4)))
                    handleNextLevel(stage, newLocX, newLocY);
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
        Gdx.gl.glClearColor(Color.LIGHT_GRAY.r, Color.LIGHT_GRAY.g,
                Color.LIGHT_GRAY.b, Color.LIGHT_GRAY.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void handleTransition(float deltaTime){
        if(fadeInTransition != null)
            if(!fadeInTransition.isTransistionDone()){
                fadeInTransition.update(deltaTime);
            }else{
                fadeInTransition = null;
            }
    }

    public void draw(float deltaTime){
        handleTransition(deltaTime);
    }

}
