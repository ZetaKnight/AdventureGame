package com.mygdx.game.pete.platformer;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

/**
 * Created by Amar on 04/09/2017.
 */
public class CollisionCell{
    private TiledMapTileLayer.Cell cell;
    public int cellX;
    public int cellY;

    public CollisionCell(TiledMapTileLayer.Cell cell, int cellX, int cellY){
        this.cell = cell;
        this.cellX = cellX;
        this.cellY = cellY;
    }

    public boolean isEmpty(){
        return cell == null;
    }
}
