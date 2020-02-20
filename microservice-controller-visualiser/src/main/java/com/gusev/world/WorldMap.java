/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gusev.world;

import com.gusev.utilities.Point;
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

    public WorldMap(int size) {
        SIZE = size;
        tiles.clear();
        for (int i = 0;i < SIZE;i++) {
            for (int j = 0;j < SIZE;j++) {
                tiles.add(new WorldTile(WorldTile.UNDEFINED_TILE, new Point(i, j)));
            }
        }
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
            return tile.getTypeStat() == 0;
        } else
            return false;
    }

    public int getMark() {
        return tiles.stream().mapToInt((e)-> e.getKnown()).sum();
    }

    public void step() {
        tiles.stream().forEach((e)->e.step());
    }
}