package com.gusev.world;

import com.gusev.utilities.Point;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import lombok.Setter;

public class WorldTile {
    private static final int TILE_SIZE = 20;
    private static final int TILE_OVER = 2;

    public static final int FULL_KNOWN_IN_STEPS = 100;
    public static final int FREE_TILE = 0;
    public static final int WALL_TILE = 2;
    public static final int UNDEFINED_TILE = -1;
    public static final int BROKEN_TILE = -2;

    @Setter
    private int type;

    @Setter
    private Point point;

    private int known;

    public int getKnown() {
        if (type == UNDEFINED_TILE)
            return 0;
        return known;
    }

    public WorldTile(int _type, Point _point){
        super();
        type = _type;
        known = FULL_KNOWN_IN_STEPS;
        point = _point;
    }

    public boolean isKnown(){
        if (known > (FULL_KNOWN_IN_STEPS / 2)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isPoint(int x, int y){
        return (x == point.x) && (y == point.y);
    }

    public Point getPoint(){
        return point;
    }

    public int getType(){
        if (known > (FULL_KNOWN_IN_STEPS / 2)) {
            return type;
        } else {
            return UNDEFINED_TILE;
        }
    }

    public int getTypeStat(){
        return type;
    }

    public void located(int _type){
        type = _type;
        known = (int) FULL_KNOWN_IN_STEPS;
    }

    public void scanned(){
        known = (int) FULL_KNOWN_IN_STEPS;
    }

    public void step(){
        known--;
        if (known < 0)
            known = 0;
    }
}
