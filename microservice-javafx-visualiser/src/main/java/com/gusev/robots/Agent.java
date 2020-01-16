/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gusev.robots;

import com.gusev.utilities.MovableObject;
import com.gusev.utilities.Point;
import com.gusev.utilities.PointsScanner;
import com.gusev.utilities.StepWorkingInteractive;
import com.gusev.utilities.TransferableUnit;
import com.gusev.world.WorldMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 *
 * @author gusev_a
 */
public class Agent implements StepWorkingInteractive, PointsScanner, 
                                    MovableObject, TransferableUnit {
    private        final    Pane        model                   = new Pane();
    
    public         final    WorldMap    enviroment;
    public         final    HashMap<Point,Integer> collected_data = new HashMap();
    public                  List<Point> path                    = null;
    
    private        final    IntegerProperty X = new SimpleIntegerProperty();
    private        final    IntegerProperty Y = new SimpleIntegerProperty();
    private                 int         status;
    
    public  static final    int         READY                   = 0;
    public  static final    int         MOVING                  = 1;
    public  static final    int         BROKEN                  = 2;
    public  static final    int         CALCULATING             = 3;
    
    public  static final    double      FAULT_CHANCE            = 0.001;
    
    private                 MovableObject load                  = null;
    
    public Agent(WorldMap map, int x, int y){
        model.setBackground(
                new Background(
                        new BackgroundFill(
                                Color.BLUE, 
                                CornerRadii.EMPTY, 
                                Insets.EMPTY
                        )
                )
        );
        model.setPrefWidth(WorldMap.TILE_SIZE - 5);
        model.setPrefHeight(WorldMap.TILE_SIZE - 5);
//	model.layoutXProperty().bind(X.multiply(WorldMap.TILE_SIZE));
//	model.layoutYProperty().bind(Y.multiply(WorldMap.TILE_SIZE));
//        map.enviroment.getChildren().add(model);
        map.setTile(x, y, WorldMap.ROBOT_ON_TILE);
        enviroment = map;
        X.setValue(x);
        Y.setValue(y);
        status = READY;
    }
    
    @Override
    public void loadSomething(MovableObject _load) {
        load = _load;
    }
    
    @Override
    public MovableObject unloadSomething() {
        MovableObject ex = load;
        load = null;
        return ex;
    }
    
    public void setNextTask(LinkedList<Point> _path) {
        path = _path;
        collected_data.clear();
    }
    
    public int getStatus() {
        return status;
    }
    
    public void startCalculation() {
        status = CALCULATING;
    }
    
    public void finishCalculation() {
        status = READY;
    }
    
    public void repair() {
        status = READY;
    }
    
    public void setManualPosition(Point p) {
        X.setValue(p.x);
        Y.setValue(p.y);
    }
    
    public void breaker() {
        status = BROKEN;
    }
    
    @Override
    public int getX() {
        return X.get();
    }
    
    @Override
    public int getY() {
        return Y.get();
    }
    
    @Override
    public void scanNearPoints() {
        if ((X.get() - 1) >= 0) {
            collected_data.put(
                    enviroment.getPoint(X.get() - 1, Y.get()), 
                    enviroment.getTile(X.get() - 1, Y.get())
            );
        }
        if ((Y.get() - 1) >= 0) {
            collected_data.put(
                    enviroment.getPoint(X.get(), Y.get() - 1), 
                    enviroment.getTile(X.get(), Y.get() - 1)
            );
        }
        if ((X.get() + 1) < enviroment.SIZE) {
            collected_data.put(
                    enviroment.getPoint(X.get() + 1, Y.get()), 
                    enviroment.getTile(X.get() + 1, Y.get())
            );
        }
        if ((Y.get() + 1) < enviroment.SIZE) {
            collected_data.put(
                    enviroment.getPoint(X.get(), Y.get() + 1), 
                    enviroment.getTile(X.get(), Y.get() + 1)
            );
        }
    }
    
    public String getStatusDescription(){
        switch (status) {
            default:
                return "Undefined";
            case CALCULATING:
                return "CALCULATING";
            case READY:
                return "READY";
            case BROKEN:
                return "BROKEN";
            case MOVING:
                return "MOVING";
        }
    }

    @Override
    public void step() {
        if (Math.random() < FAULT_CHANCE) {
            status = BROKEN;
            if (load != null) {
                if (enviroment.isTileAccessible(X.get() - 1, Y.get())) {
                        unloadSomething().move(X.get() - 1, Y.get());
                } else if (enviroment.isTileAccessible(X.get(), Y.get() - 1)) {
                        unloadSomething().move(X.get(), Y.get() - 1);
                } else if (enviroment.isTileAccessible(X.get() + 1, Y.get())) {
                        unloadSomething().move(X.get() + 1, Y.get());
                } else if (enviroment.isTileAccessible(X.get(), Y.get() + 1)) {
                        unloadSomething().move(X.get(), Y.get() + 1);
                }
            }
            enviroment.setTile(X.get(), Y.get(), WorldMap.ROBOT_BROKEN_ON_TILE);
        }
        if (status == CALCULATING) {
            return;
        }
        if (status == BROKEN) {
//            System.out.println("Agent" + this + " BROKEN");
            enviroment.setTile(X.get(), Y.get(), WorldMap.ROBOT_BROKEN_ON_TILE);
            return;
        }
        if ((X.get() - 1) >= 0 && load == null) {
            if (enviroment.getTile(X.get() - 1, Y.get()) == WorldMap.ROBOT_BROKEN_ON_TILE) {
                for (MovableObject pair : enviroment.moving_objects) {
                    if ((pair.getX() == (X.get() - 1)) 
                            && ((pair.getY() == (Y.get())))) {
                        loadSomething(pair);
                        pair.move(X.get(), Y.get());
                        break;
                    }
                }
            }
        } 
        if ((Y.get() - 1) >= 0 && load == null) {
            if (enviroment.getTile(X.get(), Y.get() - 1) == WorldMap.ROBOT_BROKEN_ON_TILE) {
                for (MovableObject pair : enviroment.moving_objects) {
                    if ((pair.getX() == (X.get())) 
                            && ((pair.getY() == (Y.get() - 1)))) {
                        loadSomething(pair);
                        pair.move(X.get(), Y.get());
                        break;
                    }
                }
            }
        } 
        if ((X.get() + 1) < enviroment.SIZE && load == null) {
            if (enviroment.getTile(X.get() + 1, Y.get()) == WorldMap.ROBOT_BROKEN_ON_TILE) {
                for (MovableObject pair : enviroment.moving_objects) {
                    if ((pair.getX() == (X.get() + 1)) 
                            && ((pair.getY() == (Y.get())))) {
                        loadSomething(pair);
                        pair.move(X.get(), Y.get());
                        break;
                    }
                }
            }
        } 
        if ((Y.get() + 1) < enviroment.SIZE && load == null) {
            if (enviroment.getTile(X.get(), Y.get() + 1) == WorldMap.ROBOT_BROKEN_ON_TILE) {
                for (MovableObject pair : enviroment.moving_objects) {
                    if ((pair.getX() == (X.get())) 
                            && ((pair.getY() == (Y.get() + 1)))) {
                        loadSomething(pair);
                        pair.move(X.get(), Y.get());
                        break;
                    }
                }
            }
        }
        if (path != null) {
            if (path.size() > 0) {
                Point cur = path.remove(0);
                if (    ((Math.abs(cur.x - X.get()) + Math.abs(cur.y - Y.get())) == 1) 
                        || ((Math.abs(cur.x - X.get()) + Math.abs(cur.y - Y.get())) == 0)) {
                    status = MOVING;
                    move(cur.x, cur.y);
                    scanNearPoints();
                } else {
                    status = BROKEN;
//                    System.out.println("Agent" + this + " BROKEN");
                }
            } else {
                status = READY;
//                System.out.println("Agent" + this + " READY");
            }
        } else {
            status = READY;
//            System.out.println("Agent" + this + " READY");
        }
        if (load != null) {
            load.move(X.get(), Y.get());
        }
    }
    
    @Override
    public void move(int _x, int _y) {
//        System.out.println("Agent" + this + " MOVING");
        enviroment.setTile(X.get(), Y.get(), WorldMap.FREE_TILE);
        X.setValue(_x);
        Y.setValue(_y);
        enviroment.setTile(X.get(), Y.get(), WorldMap.ROBOT_ON_TILE);
//        model.setLayoutX(WorldMap.TILE_SIZE * X.get());
//        model.setLayoutY(WorldMap.TILE_SIZE * Y.get());
    }

    @Override
    public HashMap<Point, Integer> getScannedPoints() {
        return collected_data;
    }

    @Override
    public boolean isLoaded() {
        return load != null;
    }
}
