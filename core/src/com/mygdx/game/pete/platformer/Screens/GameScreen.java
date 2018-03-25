package com.mygdx.game.pete.platformer.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.pete.platformer.CollisionCell;
import com.mygdx.game.pete.platformer.Entities.*;
import com.mygdx.game.pete.platformer.HUD;
import com.mygdx.game.pete.platformer.Handlers.StageHandler;
import com.mygdx.game.pete.platformer.PetePlatformer;

import java.util.Iterator;

/**
 * Created by Amar on 06/08/2017.
 */
public class GameScreen extends ScreenAdapter {
    public static final int  HEIGHT = 240, WIDTH = 480;// HEIGHT = 480, WIDTH = 640;
    public static final int CELL_SIZE = 16;
    public static final String PLATFORM = "Platform";
    public static final String FURNITURE = "Furniture";
    public static final String STACKED_PAPER = "Stacked Paper";
    public static final String HIDDEN = "Hidden";
    public static final String BACKGROUND = "Background";
    public static final String DOOR = "Door";
    public static final String CLIMBABLE = "Climbable";
    public static final String NPC = "NPC";

    private ShapeRenderer shapeRenderer;
    private Viewport viewport;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private PetePlatformer petePlatformer;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;
    private Player player;
    private Array<StackedPaper> stackedPapers = new Array<StackedPaper>();
    private HUD hud;
    private boolean hidden = false;
    private TiledMapTileLayer hiddenLayer;
    private Array<PaperBall> paperBalls = new Array<PaperBall>();
    private Array<NPC_1> npcs = new Array<NPC_1>();
    private NPC_1 npc_1;//useless, get rid of!!

    /*
    * component handlers
    *
    */
    private StageHandler stageHandler;


    public GameScreen(PetePlatformer petePlatformer){
        this.petePlatformer = petePlatformer;
    }

    @Override
    public void render(float deltaTime) {
        update(deltaTime);
        clearScreen();
        draw(deltaTime);
    }

    private void update(float delta){
        player.update(delta);
//        handlePlayerOnClimbable();
        for(NPC_1 npc: npcs){
            npc.update(delta);
        }
//        npc_1.update(delta);
        updateCamera();
        stopPeteLeaveScreen();
        handlePeteCollision();
        for(NPC_1 npc: npcs){
            handleCharCollision(npc);
        }

        handlePeteCollisionWithStackedPaper();
        handlePaperBallThrow();
        handlePaperBall(delta);
        stageHandler.update(delta);
//        handleDoorOpening();
    }

    private void updateCamera(){
        TiledMapTileLayer tiledMapTileLayer = (TiledMapTileLayer) tiledMap.getLayers().get(PLATFORM);
        float levelWidth = tiledMapTileLayer.getWidth() * tiledMapTileLayer.getTileWidth();
        float levelHeight = tiledMapTileLayer.getHeight() * tiledMapTileLayer.getTileHeight();
        if((player.getX() > WIDTH / 2) && (player.getX() < levelWidth - WIDTH/2)){
            camera.position.set(player.getX(), camera.position.y, camera.position.z);
            camera.update();
            orthogonalTiledMapRenderer.setView(camera);
        }else if(player.getX()< WIDTH / 2){
            camera.position.set(WIDTH / 2, camera.position.y, camera.position.z);
            camera.update();
            orthogonalTiledMapRenderer.setView(camera);
        }else if(player.getX() > levelWidth - WIDTH/2){
            camera.position.set(levelWidth - WIDTH/2, camera.position.y, camera.position.z);
            camera.update();
            orthogonalTiledMapRenderer.setView(camera);
        }

        if((player.getY() > HEIGHT / 2) && (player.getY() < levelHeight - HEIGHT/2)){
            camera.position.set(camera.position.x ,player.getY(), camera.position.z);
        }else if(player.getY()< HEIGHT / 2){
            camera.position.set(camera.position.x , HEIGHT / 2, camera.position.z);
        }else if(player.getY() > levelHeight - HEIGHT/2){
            camera.position.set(camera.position.x, levelHeight - HEIGHT / 2, camera.position.z);

        }

        camera.update();
        orthogonalTiledMapRenderer.setView(camera);
    }

    private void stopPeteLeaveScreen(){
        if(player.getY() < 0) {
            player.setPosition(player.getX(), 0);
            player.landed();
        }
        if(player.getX() < 0)
            player.setPosition(0, player.getY());

        TiledMapTileLayer tiledMapTileLayer = (TiledMapTileLayer) tiledMap.getLayers().get(PLATFORM);
        float widthOfLevel = tiledMapTileLayer.getWidth() * tiledMapTileLayer.getTileWidth();
        if(player.getX() + Player.WIDTH > widthOfLevel){
            player.setPosition(widthOfLevel - player.WIDTH, player.getY());
        }
    }

    private void clearScreen(){
        Gdx.gl.glClearColor(Color.LIGHT_GRAY.r, Color.LIGHT_GRAY.g,
                Color.LIGHT_GRAY.b, Color.LIGHT_GRAY.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void draw(float deltaTime){
        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);
        batch.begin();
        orthogonalTiledMapRenderer.renderTileLayer((TiledMapTileLayer) tiledMap.getLayers().get(BACKGROUND));
        orthogonalTiledMapRenderer.renderTileLayer((TiledMapTileLayer) tiledMap.getLayers().get(PLATFORM));
        orthogonalTiledMapRenderer.renderTileLayer((TiledMapTileLayer) tiledMap.getLayers().get(FURNITURE));
        orthogonalTiledMapRenderer.renderTileLayer((TiledMapTileLayer) tiledMap.getLayers().get(CLIMBABLE));
        hiddenLayer = (TiledMapTileLayer) tiledMap.getLayers().get(HIDDEN);
        if(hidden == false) hiddenLayer.setOpacity(1);
        else hiddenLayer.setOpacity(.5f);
        for(NPC_1 npc : npcs){
            npc.draw();
        }
        player.draw();
        drawPaperBallsThrown();
        drawStackPaper();
        orthogonalTiledMapRenderer.renderTileLayer(hiddenLayer);
        hud.draw(batch);
        stageHandler.draw(deltaTime);
        batch.end();
    }

    private void drawPaperBallsThrown(){
        for(PaperBall paperBall : paperBalls){
            paperBall.draw();
        }
    }

    private void drawStackPaper(){
        for(StackedPaper paper : stackedPapers){
            paper.draw(batch);
        }
    }
    /*
        Testing Debug outlines
    */
    private void drawDebug(){
        shapeRenderer.setProjectionMatrix(camera.projection);
        shapeRenderer.setTransformMatrix(camera.view);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        player.drawDebug(shapeRenderer);
        shapeRenderer.rect(intersection.x, intersection.y, intersection.width, intersection.height);
        for(CollisionCell cell : peteCells){
            shapeRenderer.rect(cell.cellX * CELL_SIZE, cell.cellY * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }
        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(WIDTH,HEIGHT,camera);
        viewport.apply(true);
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        tiledMap = petePlatformer.getAssetManager().get("level1.tmx");
        orthogonalTiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, batch);
        orthogonalTiledMapRenderer.setView(camera);
        player = new Player(petePlatformer.getAssetManager().get(Player.CHARACTER, Texture.class),
                petePlatformer.getAssetManager().get("jump2.wav", Sound.class),batch, this);
        populateNPCs();
        petePlatformer.getAssetManager().get("peteTheme.mp3", Music.class).setLooping(true);
//        petePlatformer.getAssetManager().get("peteTheme.mp3", Music.class).play();
        populateStackedPaper();
        hud = new HUD(this);

        /*
        * Component handlers, handling components
         */
        stageHandler = new StageHandler(this, player, petePlatformer);
    }

    private Array<CollisionCell> whichCellsCharCovers(float x, float y, TiledMapTileLayer tmtl){
        Array<CollisionCell> cellsCovered = new Array<CollisionCell>();
        float cellX = x/CELL_SIZE;
        float cellY = y/CELL_SIZE;

        int bottomLCellX = MathUtils.floor(cellX);
        int bottomLCellY = MathUtils.floor(cellY);

        TiledMapTileLayer tiledMapTileLayer = tmtl;

        cellsCovered.add(new CollisionCell(tiledMapTileLayer.getCell(bottomLCellX, bottomLCellY), bottomLCellX, bottomLCellY));
        //bottom center x & Y
        int bcx = bottomLCellX+1;
        int bcy = bottomLCellY;
        cellsCovered.add(new CollisionCell(tiledMapTileLayer.getCell(bcx, bcy), bcx, bcy));
        //top center x & y
        int tcx = bottomLCellX;
        int tcy = bottomLCellY+1;
        cellsCovered.add(new CollisionCell(tiledMapTileLayer.getCell(tcx, tcy), tcx, tcy));
        //top right center x & y
        int trcx = bottomLCellX+1;
        int trcy = bottomLCellY+1;
        cellsCovered.add(new CollisionCell(tiledMapTileLayer.getCell(trcx, trcy), trcx, trcy));

        if(cellX % 1 != 0 && cellY % 1 != 0){
            int topRightCellX = bottomLCellX + 2;
            int topRightCellY = bottomLCellY + 2;
            cellsCovered.add(new CollisionCell(tiledMapTileLayer.getCell(topRightCellX,topRightCellY),topRightCellX, topRightCellY));
        }

        if(cellX % 1 !=0){
            int bottomRightCellX = bottomLCellX + 2;
            int bottomRightCellY = bottomLCellY;
            cellsCovered.add(new CollisionCell(tiledMapTileLayer.getCell(bottomRightCellX,bottomRightCellY),bottomRightCellX, bottomRightCellY));
            int rightCentreCellX = bottomLCellX + 2;
            int rightCentreCellY = bottomLCellY + 1;
            cellsCovered.add(new CollisionCell(tiledMapTileLayer.getCell(rightCentreCellX,rightCentreCellY),rightCentreCellX, rightCentreCellY));
        }

        if(cellY % 1 != 0){
            int topLeftCellX = bottomLCellX;
            int topLeftCellY = bottomLCellY + 2;
            cellsCovered.add(new CollisionCell(tiledMapTileLayer.getCell(topLeftCellX,topLeftCellY),topLeftCellX, topLeftCellY));
        }

        return cellsCovered;
    }

    private Array<CollisionCell> whichCellsPeteCoverAbove(){
        Array<CollisionCell> cellsCovered = new Array<CollisionCell>();

        float x = player.getX();
        float y = player.getY();

        float cellX = x/CELL_SIZE;
        float cellY = y/CELL_SIZE;

        int bottomLCellX = MathUtils.floor(cellX);
        int bottomLCellY = MathUtils.floor(cellY);

        TiledMapTileLayer tiledMapTileLayer = (TiledMapTileLayer) tiledMap.getLayers().get(PLATFORM);

        cellsCovered.add(new CollisionCell(tiledMapTileLayer.getCell(bottomLCellX, bottomLCellY+2), bottomLCellX, bottomLCellY));
        cellsCovered.add(new CollisionCell(tiledMapTileLayer.getCell(bottomLCellX+1, bottomLCellY+2), bottomLCellX, bottomLCellY));

        return cellsCovered;
    }

    public Array<CollisionCell> filterOutNonTiledCells(Array<CollisionCell> cells){
        for(Iterator<CollisionCell> iter = cells.iterator(); iter.hasNext();){
            CollisionCell collisionCell = iter.next();
            if(collisionCell.isEmpty())
                iter.remove();
        }
        return cells;
    }

    Rectangle intersection = new Rectangle();
    Array<CollisionCell> peteCells = new Array<CollisionCell>();
    private void handlePeteCollision(){

        peteCells = whichCellsPeteCoverAbove();
        peteCells = filterOutNonTiledCells(peteCells);
        for(CollisionCell cell : peteCells){
            float cellLevelX = cell.cellX * CELL_SIZE;
            float cellLevelY = cell.cellY * CELL_SIZE;
            Intersector.intersectRectangles(player.getCollisionRect(), new Rectangle(cellLevelX, cellLevelY, CELL_SIZE, CELL_SIZE), intersection);
            player.setPosition(player.getX(), intersection.getY() - intersection.getHeight());
            player.setBlockJump(true);
        }

        peteCells = whichCellsCharCovers(player.getX(), player.getY(), (TiledMapTileLayer) tiledMap.getLayers().get(PLATFORM));
        peteCells = filterOutNonTiledCells(peteCells);
        for(CollisionCell cell : peteCells){
            float cellLevelX = cell.cellX * CELL_SIZE;
            float cellLevelY = cell.cellY * CELL_SIZE;
            Intersector.intersectRectangles(player.getCollisionRect(), new Rectangle(cellLevelX, cellLevelY, CELL_SIZE, CELL_SIZE), intersection);
            if(intersection.getHeight() < intersection.getWidth()){
                player.setPosition(player.getX(), intersection.getY() + intersection.getHeight());
                player.landed();
                player.setOnLand(true);
            }else if(intersection.getHeight() >= intersection.getWidth()){
                player.setOnLand(false);
                if(intersection.getX() == player.getX()){
                    player.setPosition(intersection.getX() + intersection.getWidth(), player.getY());
                }
                if(intersection.getX() > player.getX()){
                    player.setPosition(intersection.getX() - Player.WIDTH, player.getY());
                }
            }
        }

        if(peteCells.size < 1)
            player.setOnLand(false);


        peteCells = whichCellsCharCovers(player.getX(), player.getY(), (TiledMapTileLayer) tiledMap.getLayers().get(HIDDEN));
        peteCells = filterOutNonTiledCells(peteCells);
        if(peteCells.size == 0) hidden = false;
        for(CollisionCell cell : peteCells){
            float cellLevelX = cell.cellX * CELL_SIZE;
            float cellLevelY = cell.cellY * CELL_SIZE;
            Intersector.intersectRectangles(player.getCollisionRect(), new Rectangle(cellLevelX, cellLevelY, CELL_SIZE, CELL_SIZE), intersection);
            if(intersection.getHeight() > 0 || intersection.getWidth() > 0){
                hidden = true;
            }
        }
    }

    Rectangle intersectionNpc = new Rectangle();
    Array<CollisionCell> npcCells = new Array<CollisionCell>();
    private void handleCharCollision(NPC_1 npc){
        npcCells = whichCellsCharCovers(npc.getX(), npc.getY(), (TiledMapTileLayer) tiledMap.getLayers().get(PLATFORM));
        npcCells = filterOutNonTiledCells(npcCells);
        for(CollisionCell cell : npcCells){
            float cellLevelX = cell.cellX * CELL_SIZE;
            float cellLevelY = cell.cellY * CELL_SIZE;
            Intersector.intersectRectangles(npc.getCollisionRect(), new Rectangle(cellLevelX, cellLevelY, CELL_SIZE, CELL_SIZE), intersectionNpc);
            if(intersectionNpc.getHeight() < intersectionNpc.getWidth()){
                npc.setY(intersectionNpc.getY() + intersectionNpc.getHeight());
            }else if(intersectionNpc.getHeight() >= intersectionNpc.getWidth()){
                if(intersectionNpc.getX() == npc.getX()){
                    npc.setX(intersectionNpc.getX() + intersectionNpc.getWidth());
                }
                if(intersectionNpc.getX() > npc.getX()){
                    npc.setX(intersectionNpc.getX() - NPC_1.WIDTH);
                }
            }
        }
    }

    public void populateNPCs(){
        MapLayer mapLayer = tiledMap.getLayers().get(NPC);
        for(MapObject object : mapLayer.getObjects()){
            float x = (object.getProperties().get("x", Float.class));
            float y = (object.getProperties().get("y", Float.class));
            String type = object.getProperties().get("type", String.class);
            npcs.add(new NPC_1(petePlatformer.getAssetManager().get(NPC_1.CHARACTER, Texture.class),
                    petePlatformer.getAssetManager().get("jump2.wav", Sound.class),
                    batch,
                    x, y, player, type));
        }
    }

    public void populateStackedPaper(){
        MapLayer mapLayer = tiledMap.getLayers().get(STACKED_PAPER);
        for(MapObject mapObject : mapLayer.getObjects()){
            stackedPapers.add(new StackedPaper(petePlatformer.getAssetManager().get(
                    PaperBall.STACKED_PAPER, Texture.class),
                    mapObject.getProperties().get("x", Float.class),
                    mapObject.getProperties().get("y", Float.class)));
        }
    }

    private void handlePeteCollisionWithStackedPaper(){
        for(Iterator<StackedPaper> iter = stackedPapers.iterator(); iter.hasNext();){
            StackedPaper stackedPaper = iter.next();
            if (player.getCollisionRect().overlaps(stackedPaper.getCollision())) {
                petePlatformer.getAssetManager().get("sacorn.wav", Sound.class).play();
                iter.remove();
                hud.incrementScore();
            }
        }
    }

    // handles which way a ball is thrown
    public void handlePaperBallThrow(){
        if(player.isThrowPaperBallLeft()){
            paperBalls.add( new PaperBall(batch,
                    petePlatformer.getAssetManager().get(PaperBall.PAPER_BALL, Texture.class),
                    player.getX(),
                    player.getY()+(Player.HEIGHT*.75f), -7,0));
            player.setThrowPaperBallLeft(false);
        }else if(player.isThrowPaperBallRight()){
            paperBalls.add( new PaperBall(batch,
                            petePlatformer.getAssetManager().get(PaperBall.PAPER_BALL, Texture.class),
                            player.getX()+ Player.WIDTH,
                            player.getY()+(Player.HEIGHT*.75f), 7, 0));
            player.setThrowPaperBallRight(false);
        }

    }

    //handles paper balls positions and collisions
    private void handlePaperBall(float delta){
        for(Iterator<PaperBall> iter = paperBalls.iterator(); iter.hasNext();){
            PaperBall paperBall= iter.next();
            paperBall.update(delta);
            if(paperBall.die ){
                iter.remove();
            }else if(!paperBall.getHit()){
                Array<CollisionCell> collisionCells = whichCellsPaperBallCovers(
                        paperBall, (TiledMapTileLayer) tiledMap.getLayers().get(PLATFORM));
                collisionCells = filterOutNonTiledCells(collisionCells);
                boolean paperBallHit = handlePaperBallHitEnemy(paperBall);
                if(collisionCells.size>0 || paperBallHit){
                    paperBall.setTexture(petePlatformer.getAssetManager().get(PaperBall.PAPER_BALL_BANG, Texture.class));
                    paperBall.setHit(true);
//                    npc_1.setHit(true);
                }
            }
        }
    }

    private boolean handlePaperBallHitEnemy(PaperBall paperBall) {
        for(NPC_1 npc : npcs){
            if(paperBall.getCollisionRect().overlaps(npc.getCollisionRect())){
                npc.setHit(true);
                return true;
            }
        }
        return false;
    }

    private Array<CollisionCell> whichCellsPaperBallCovers(PaperBall paperBall, TiledMapTileLayer tiledMapTileLayer){
        float x = paperBall.getX();
        float y = paperBall.getY();
        Array<CollisionCell> cellsCovered = new Array<CollisionCell>();
        float cellX = x/CELL_SIZE;
        float cellY = y/CELL_SIZE;

        int bottomLCellX = MathUtils.floor(cellX);
        int bottomLCellY = MathUtils.floor(cellY);

        //  top right pos
        if(cellX % 1 != 0 && cellY % 1 != 0){
            int topRightCellX = bottomLCellX + 1;
            int topRightCellY = bottomLCellY + 1;
            cellsCovered.add(new CollisionCell(tiledMapTileLayer.getCell(topRightCellX,topRightCellY),topRightCellX, topRightCellY));
        }
        //  bottom right pos
        if(cellX % 1 !=0){
            int bottomRightCellX = bottomLCellX + 1;
            int bottomRightCellY = bottomLCellY;
            cellsCovered.add(new CollisionCell(tiledMapTileLayer.getCell(bottomRightCellX,bottomRightCellY),bottomRightCellX, bottomRightCellY));
        }

        //  top left pos
        if(cellY % 1 != 0){
            int topLeftCellX = bottomLCellX;
            int topLeftCellY = bottomLCellY + 1;
            cellsCovered.add(new CollisionCell(tiledMapTileLayer.getCell(topLeftCellX, topLeftCellY), topLeftCellX, topLeftCellY));
        }

        return cellsCovered;
    }

    /**
     * @deprecated - This method should not be used. See Player.java handlePlayerOnClimbable()
     */
    @Deprecated
    public void handlePlayerOnClimbable(){
        float x = player.getX();
        float y = player.getY();

        float cellX = x/CELL_SIZE;
        float cellY = y/CELL_SIZE;

        int bottomLCellX = MathUtils.floor(cellX);
        int bottomLCellY = MathUtils.floor(cellY);
        Array<CollisionCell> cellsCovered = new Array<CollisionCell>();
        TiledMapTileLayer tiledMapTileLayer = (TiledMapTileLayer) tiledMap.getLayers().get(CLIMBABLE);
        cellsCovered.add(new CollisionCell(tiledMapTileLayer.getCell(bottomLCellX, bottomLCellY), bottomLCellX, bottomLCellY));
        cellsCovered = filterOutNonTiledCells(cellsCovered);
        if(cellsCovered.size>0) player.setOnClimbable(true);
        else{
            player.setOnClimbable(false);
            player.setIsClimbing(false);
        }
    }

    public Viewport getViewport() {
        return viewport;
    }

    public Array<PaperBall> getPaperBalls() {
        return paperBalls;
    }

    public Array<NPC_1> getNpcs() {
        return npcs;
    }

    public Array<StackedPaper> getStackedPapers() {
        return stackedPapers;
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }
    public void setTiledMap(TiledMap tiledMap){
        this.tiledMap = tiledMap;
    }

    public OrthogonalTiledMapRenderer GetOrthogonalTiledMapRenderer(){
        return orthogonalTiledMapRenderer;
    }
}
