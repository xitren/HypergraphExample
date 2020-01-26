/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gusev.world;

import com.gusev.utilities.MovableObject;
import com.gusev.utilities.Point;
import com.gusev.utilities.StepWorkingInteractive;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
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
public class WorldMap implements StepWorkingInteractive {
    
    private final           Pane[][]    mapper;
    private final           int[][]     mapper_types;
    private final           Point[][]   mapper_coords;
    
    public          final   int         SIZE;
    public  static  final   int         OVER = 2;
    public  static  final   int         TILE_SIZE = 20;
    
    public  static  final   int         FREE_TILE = 0;
    public  static  final   int         ROBOT_ON_TILE = -2;
    public  static  final   int         ROBOT_BROKEN_ON_TILE = -3;
    public  static  final   int         WALL_TILE = 2;
    public  static  final   int         ROBOT_BASE_ON_TILE = -1;
    
    public          final   Set<MovableObject> moving_objects = new HashSet();
    
    public  static  final   Background  FREE_TILE_BACK = new Background(
                                new BackgroundFill(
                                        Color.LIGHTBLUE, 
                                        CornerRadii.EMPTY, 
                                        Insets.EMPTY
                                )
                        );
    public  static  final   Background  ROBOT_ON_TILE_BACK = new Background(
                                new BackgroundFill(
                                        Color.CHOCOLATE, 
                                        CornerRadii.EMPTY, 
                                        Insets.EMPTY
                                )
                        );
    public  static  final   Background  ROBOT_BASE_ON_TILE_BACK = new Background(
                                new BackgroundFill(
                                        Color.CORAL, 
                                        CornerRadii.EMPTY, 
                                        Insets.EMPTY
                                )
                        );
    public  static  final   Background  WALL_TILE_BACK = new Background(
                                new BackgroundFill(
                                        Color.BLACK, 
                                        CornerRadii.EMPTY, 
                                        Insets.EMPTY
                                )
                        );
    public  static  final   Background  ROBOT_BROKEN_ON_TILE_BACK = new Background(
                                new BackgroundFill(
                                        Color.RED, 
                                        CornerRadii.EMPTY, 
                                        Insets.EMPTY
                                )
                        );
    
    public          final   Pane        enviroment;
    
    public WorldMap(Pane map, File file) throws IOException {
        enviroment = map;
        BufferedReader READER;
        READER = new BufferedReader(new FileReader(file));
        String line;
        if ((line = READER.readLine()) != null) {
            SIZE = line.length();
            mapper_types = new int[SIZE][SIZE];
        } else
            throw new IOException("File format error!");
        int k = 0;
        do {
            for (int i=0;i < line.length();i++) {
                if (line.charAt(i) == '-')
                    mapper_types[i][k] = FREE_TILE;
                else
                    mapper_types[i][k] = WALL_TILE;
            }
            k++;
        } while ((line = READER.readLine()) != null);
        READER.close();
        map.getChildren().clear();
        mapper = new Pane[SIZE][SIZE];
        mapper_coords = new Point[SIZE][SIZE];
        for (int i=0;i < SIZE;i++){
            for (int j=0;j < SIZE;j++){
                mapper_coords[i][j] = new Point(i, j);
//                mapper_types[i][j] = 0;
                mapper[i][j] = new Pane();
                mapper[i][j].setBackground(FREE_TILE_BACK);
                if (mapper_types[i][j] == FREE_TILE)
                    mapper[i][j].setBackground(FREE_TILE_BACK);
                if (mapper_types[i][j] == WALL_TILE)
                    mapper[i][j].setBackground(WALL_TILE_BACK);
                mapper[i][j].setPrefWidth(15);
                mapper[i][j].setPrefHeight(15);
                mapper[i][j].setLayoutX(TILE_SIZE * i + OVER);
                mapper[i][j].setLayoutY(TILE_SIZE * j + OVER);
                map.getChildren().add(mapper[i][j]);
            }
        }
    }
    
    public WorldMap(Pane map, int size){
        enviroment = map;
        SIZE = size;
        map.getChildren().clear();
        mapper = new Pane[SIZE][SIZE];
        mapper_types = new int[SIZE][SIZE];
        mapper_coords = new Point[SIZE][SIZE];
        for (int i=0;i < SIZE;i++){
            for (int j=0;j < SIZE;j++){
                mapper_coords[i][j] = new Point(i, j);
                mapper_types[i][j] = 0;
                mapper[i][j] = new Pane();
                mapper[i][j].setBackground(FREE_TILE_BACK);
                mapper[i][j].setPrefWidth(15);
                mapper[i][j].setPrefHeight(15);
                mapper[i][j].setLayoutX(TILE_SIZE * i + OVER);
                mapper[i][j].setLayoutY(TILE_SIZE * j + OVER);
                map.getChildren().add(mapper[i][j]);
            }
        }
        for (int i=5;i < 20;i++){
            mapper_types[i][9] = WALL_TILE;
            mapper[i][9].setBackground(WALL_TILE_BACK);
        }
    }
    
    public void setTile(int x, int y, int type) {
        if ( 0 <= x && x < SIZE 
                && 0 <= y && y < SIZE ){}
        else
            return;
        mapper_types[x][y] = type;
        switch (type) {
            default:
            case FREE_TILE:
                mapper[x][y].setBackground(FREE_TILE_BACK);
                break;
            case ROBOT_BROKEN_ON_TILE:
                mapper[x][y].setBackground(ROBOT_BROKEN_ON_TILE_BACK);
                break;
            case ROBOT_ON_TILE:
                mapper[x][y].setBackground(ROBOT_ON_TILE_BACK);
                break;
            case ROBOT_BASE_ON_TILE:
                mapper[x][y].setBackground(ROBOT_BASE_ON_TILE_BACK);
                break;
            case WALL_TILE:
                mapper[x][y].setBackground(WALL_TILE_BACK);
                break;
        }
    }
    
    public LinkedList<Point> getPath(int x1, int y1, int x2, int y2) {
        LinkedList<Point> ret = new LinkedList<>();
        return ret;
    }
    
    public List<Point> getPointsLocal(int x, int y) {
        List<Point> ret = new LinkedList();
	for (int i = x - 2;i <= x + 2;i++)
	    for (int j = y - 2;j <= y + 2;j++)
		if (isTileAccessible(i, j))
		    ret.add(getPoint(i, j));
        return ret;
    }
    
    public Point getPoint(int x, int y) {
        if ( 0 <= x && x < SIZE 
                && 0 <= y && y < SIZE )
            return mapper_coords[x][y];
        else
            return null;
    }
    
    public int getTile(int x, int y) {
        if ( 0 <= x && x < SIZE 
                && 0 <= y && y < SIZE )
            return mapper_types[x][y];
        else
            return WALL_TILE;
    }
    
    public boolean isTileBlocked(int x, int y) {
        if ( 0 <= x && x < SIZE 
                && 0 <= y && y < SIZE )
            return mapper_types[x][y] > 0;
        else
            return true;
    }
    
    public boolean isTileAccessible(int x, int y) {
        if ( 0 <= x && x < SIZE 
                && 0 <= y && y < SIZE )
            return mapper_types[x][y] <= 0;
        else
            return false;
    }

    @Override
    public void step() {
    }
}








