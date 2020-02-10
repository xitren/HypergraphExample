/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gusev.world;

import com.gusev.robots.Agent;
import com.gusev.robots.AgentsInterface;
import com.gusev.utilities.MovableObject;
import com.gusev.utilities.Point;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author gusev_a
 */
public class WorldMap {

    private final Set<WorldTile> tiles = new HashSet();

    public final int SIZE;
    public final Set<MovableObject> step_objects = new HashSet();
    public final Pane enviroment;

    public WorldMap(Pane map, File file) throws IOException {
        enviroment = map;
        BufferedReader READER = null;
        try {
            READER = new BufferedReader(new FileReader(file));
            String line;
            if ((line = READER.readLine()) != null) {
                SIZE = line.length();
            } else
                throw new IOException("File format error!");
            int k = 0;
            do {
                for (int i = 0; i < line.length(); i++) {
                    int type;
                    if (line.charAt(i) == '-')
                        type = WorldTile.FREE_TILE;
                    else
                        type = WorldTile.WALL_TILE;
                    tiles.add(new WorldTile(type, new Point(i, k)));
                }
                k++;
            } while ((line = READER.readLine()) != null);
        } finally {
            if (READER != null)
                READER.close();
        }
        map.getChildren().clear();
        tiles.stream().forEach((e)->map.getChildren().add(e));
        AgentsInterface.setWorld(this);
    }

    public LinkedList<Point> getPath(int x1, int y1, int x2, int y2) {
        LinkedList<Point> ret = new LinkedList<>();
        return ret;
    }

    public List<Point> getPointsLocal(int x, int y) {
        List<Point> ret = new LinkedList();
        for (int i = x - 2; i <= x + 2; i++)
            for (int j = y - 2; j <= y + 2; j++)
                if (isTileAccessible(i, j))
                    ret.add(getPoint(i, j));
        return ret;
    }

    public Point getPoint(int x, int y) {
        if (0 <= x && x < SIZE && 0 <= y && y < SIZE) {
            WorldTile tile =
                    tiles.stream().filter((e) -> (e.isPoint(x, y))).findFirst().get();
            return tile.getPoint();
        } else
            return null;
    }

    public boolean addAgent(Agent ag, int x, int y) {
        if (!isTileBlocked(x, y) && ag != null) {
            step_objects.add(ag);
            Platform.runLater(()->{enviroment.getChildren().add(ag);});
            return true;
        } else
            return false;
    }

    public boolean removeAgent(Agent ag) {
        if (ag != null) {
            step_objects.remove(ag);
            Platform.runLater(()->{enviroment.getChildren().remove(ag);});
            return true;
        } else
            return false;
    }

    public int getTileType(int x, int y) {
        if (0 <= x && x < SIZE && 0 <= y && y < SIZE) {
            WorldTile tile =
                    tiles.stream().filter((e) -> (e.isPoint(x, y))).findFirst().get();
            tile.scanned();
            return tile.getType();
        } else
            return 0;
    }

    public boolean isTileBlocked(int x, int y) {
        if (0 <= x && x < SIZE && 0 <= y && y < SIZE) {
            WorldTile tile =
                    tiles.stream().filter((e) -> (e.isPoint(x, y))).findFirst().get();
            return tile.getType() > 0;
        } else
            return true;
    }

    public boolean isTileAccessible(int x, int y) {
        if (0 <= x && x < SIZE && 0 <= y && y < SIZE) {
            WorldTile tile =
                    tiles.stream().filter((e) -> (e.isPoint(x, y))).findFirst().get();
            boolean none = step_objects.stream().noneMatch((e) -> (e.isPoint(x, y)));
            return tile.getTypeStat() == 0 && none;
        } else
            return false;
    }

    public void step() {
        tiles.stream().forEach((e)->e.step());
        step_objects.stream().forEach((e)->e.step());
    }
}