/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gusev.robots;

import com.gusev.utilities.MovableObject;
import com.gusev.utilities.Point;
import com.gusev.utilities.PointsScanner;
import com.gusev.utilities.TransferableUnit;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.gusev.world.WorldMap;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * @author gusev_a
 */
public class Agent extends Pane implements PointsScanner,
        MovableObject, TransferableUnit {
    public static final int READY = 0;
    public static final int MOVING = 1;
    public static final int BROKEN = 2;
    public static final int CALCULATING = 3;
    public static final double FAULT_CHANCE = 0.001;
    private static final int ROBOT_SIZE = 20;

    public static int id_counter = 0;

    private static final Background[] ROBOT_BACK = {
            new Background(
                    new BackgroundFill(
                            Color.GREEN,
                            CornerRadii.EMPTY,
                            Insets.EMPTY
                    )
            ),
            new Background(
                    new BackgroundFill(
                            Color.ORANGE,
                            CornerRadii.EMPTY,
                            Insets.EMPTY
                    )
            ),
            new Background(
                    new BackgroundFill(
                            Color.RED,
                            CornerRadii.EMPTY,
                            Insets.EMPTY
                    )
            ),
            new Background(
                    new BackgroundFill(
                            Color.PURPLE,
                            CornerRadii.EMPTY,
                            Insets.EMPTY
                    )
            ),
    };

    private Point point;
    private int status;
    private MovableObject load = null;
    private List<Point> path = null;
    private final HashMap<Point, Integer> collected_data = new HashMap();
    private WorldMap world;

    public Agent(WorldMap _world, Point _point) {
        world = _world;
        this.setId("agent" + id_counter++);
        point = _point;
        setStatus(READY);
        setPrefWidth(15);
        setPrefHeight(15);
        setLayout();
    }

    private void setLayout() {
        Platform.runLater(()->{
            setLayoutX(20 * point.x + 2);
            setLayoutY(20 * point.y + 2);
        });
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

    public void setStatus(int _status) {
        status = _status;
        this.setBackground(ROBOT_BACK[status]);
    }

    private int getStatus() {
        return status;
    }

    public void startCalculation() {
        setStatus(CALCULATING);
    }

    public void finishCalculation() {
        setStatus(READY);
    }

    public void repair() {
        setStatus(READY);
    }

    public void breaker() {
        setStatus(BROKEN);
    }

    @Override
    public boolean isPoint(int x, int y){
        return (x == point.x) && (y == point.y);
    }

    public Point getPoint(){
        return point;
    }

    @Override
    public void move(Point _point) {
////        System.out.println("Agent" + this + " MOVING");
//        enviroment.setTile(X.get(), Y.get(), WorldMap.FREE_TILE);
//        X.setValue(_x);
//        Y.setValue(_y);
//        enviroment.setTile(X.get(), Y.get(), WorldMap.ROBOT_ON_TILE);
////        model.setLayoutX(WorldMap.TILE_SIZE * X.get());
////        model.setLayoutY(WorldMap.TILE_SIZE * Y.get());
        setLayout();
    }

    @Override
    public void moveUp() {
        if (world.isTileAccessible(point.x, point.y - 1)) {
            point = world.getPoint(point.x, point.y - 1);
            setLayout();
        }
    }

    @Override
    public void moveDown() {
        if (world.isTileAccessible(point.x, point.y + 1)) {
            point = world.getPoint(point.x, point.y + 1);
            setLayout();
        }
    }

    @Override
    public void moveLeft() {
        if (world.isTileAccessible(point.x - 1, point.y)) {
            point = world.getPoint(point.x - 1, point.y);
            setLayout();
        }
    }

    @Override
    public void moveRight() {
        if (world.isTileAccessible(point.x + 1, point.y)) {
            point = world.getPoint(point.x + 1, point.y);
            setLayout();
        }
    }

    @Override
    public void scanNearPoints() {
        world.getTileType(point.x, point.y - 1);
        world.getTileType(point.x, point.y + 1);
        world.getTileType(point.x - 1, point.y);
        world.getTileType(point.x + 1, point.y);
    }

    public String getStatusDescription() {
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
        scanNearPoints();
//        if (Math.random() < FAULT_CHANCE) {
//            status = BROKEN;
//            if (load != null) {
//                if (enviroment.isTileAccessible(X.get() - 1, Y.get())) {
//                    unloadSomething().move(X.get() - 1, Y.get());
//                } else if (enviroment.isTileAccessible(X.get(), Y.get() - 1)) {
//                    unloadSomething().move(X.get(), Y.get() - 1);
//                } else if (enviroment.isTileAccessible(X.get() + 1, Y.get())) {
//                    unloadSomething().move(X.get() + 1, Y.get());
//                } else if (enviroment.isTileAccessible(X.get(), Y.get() + 1)) {
//                    unloadSomething().move(X.get(), Y.get() + 1);
//                }
//            }
//            enviroment.setTile(X.get(), Y.get(), WorldMap.ROBOT_BROKEN_ON_TILE);
//        }
//        if (status == CALCULATING) {
//            return;
//        }
//        if (status == BROKEN) {
////            System.out.println("Agent" + this + " BROKEN");
//            enviroment.setTile(X.get(), Y.get(), WorldMap.ROBOT_BROKEN_ON_TILE);
//            return;
//        }
//        if ((X.get() - 1) >= 0 && load == null) {
//            if (enviroment.getTile(X.get() - 1, Y.get()) == WorldMap.ROBOT_BROKEN_ON_TILE) {
//                for (MovableObject pair : enviroment.moving_objects) {
//                    if ((pair.getX() == (X.get() - 1))
//                            && ((pair.getY() == (Y.get())))) {
//                        loadSomething(pair);
//                        pair.move(X.get(), Y.get());
//                        break;
//                    }
//                }
//            }
//        }
//        if ((Y.get() - 1) >= 0 && load == null) {
//            if (enviroment.getTile(X.get(), Y.get() - 1) == WorldMap.ROBOT_BROKEN_ON_TILE) {
//                for (MovableObject pair : enviroment.moving_objects) {
//                    if ((pair.getX() == (X.get()))
//                            && ((pair.getY() == (Y.get() - 1)))) {
//                        loadSomething(pair);
//                        pair.move(X.get(), Y.get());
//                        break;
//                    }
//                }
//            }
//        }
//        if ((X.get() + 1) < enviroment.SIZE && load == null) {
//            if (enviroment.getTile(X.get() + 1, Y.get()) == WorldMap.ROBOT_BROKEN_ON_TILE) {
//                for (MovableObject pair : enviroment.moving_objects) {
//                    if ((pair.getX() == (X.get() + 1))
//                            && ((pair.getY() == (Y.get())))) {
//                        loadSomething(pair);
//                        pair.move(X.get(), Y.get());
//                        break;
//                    }
//                }
//            }
//        }
//        if ((Y.get() + 1) < enviroment.SIZE && load == null) {
//            if (enviroment.getTile(X.get(), Y.get() + 1) == WorldMap.ROBOT_BROKEN_ON_TILE) {
//                for (MovableObject pair : enviroment.moving_objects) {
//                    if ((pair.getX() == (X.get()))
//                            && ((pair.getY() == (Y.get() + 1)))) {
//                        loadSomething(pair);
//                        pair.move(X.get(), Y.get());
//                        break;
//                    }
//                }
//            }
//        }
//        if (path != null) {
//            if (path.size() > 0) {
//                Point cur = path.remove(0);
//                if (((Math.abs(cur.x - X.get()) + Math.abs(cur.y - Y.get())) == 1)
//                        || ((Math.abs(cur.x - X.get()) + Math.abs(cur.y - Y.get())) == 0)) {
//                    status = MOVING;
//                    move(cur.x, cur.y);
//                    scanNearPoints();
//                } else {
//                    status = BROKEN;
////                    System.out.println("Agent" + this + " BROKEN");
//                }
//            } else {
//                status = READY;
////                System.out.println("Agent" + this + " READY");
//            }
//        } else {
//            status = READY;
////            System.out.println("Agent" + this + " READY");
//        }
//        if (load != null) {
//            load.move(X.get(), Y.get());
//        }
    }

    @Override
    public HashMap<Point, Integer> getScannedPoints() {
        return collected_data;
    }

    @Override
    public int getUpScan() {
        return world.getTileType(point.x, point.y - 1);
    }

    @Override
    public int getDownScan() {
        return world.getTileType(point.x, point.y + 1);
    }

    @Override
    public int getleftScan() {
        return world.getTileType(point.x - 1, point.y);
    }

    @Override
    public int getRightScan() {
        return world.getTileType(point.x + 1, point.y);
    }

    @Override
    public boolean isLoaded() {
        return load != null;
    }

    @Override
    public String toString() {
        return "Id = " + getId() + ", x = " + point.x + ", x = " + point.y;
    }
}
