/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gusev.robots;

import com.gusev.utilities.ValueComparator;
import com.gusev.utilities.Point;
import com.gusev.utilities.StepWorkingInteractive;
import java.util.HashSet;
import java.util.Set;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import com.gusev.world.WorldMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 * @author gusev_a
 */
public class Base implements StepWorkingInteractive {
    private static  final    int         MAX_UNSEEN        = 500;
    private         final    int         X;
    private         final    int         Y;
    private         final    int         W;
    private         final    int         H;
    
    private         final    int[][]     mapper_types_scanned;
    private         final    int[][]     mapper_timout;
    private         final    Pane[][]    mapper;
    private         final    Pane        model             = new Pane();
    private         final    Set<Agent>  robots            = new HashSet(); 
    
    public          final    WorldMap    enviroment;
    public          final    SortedMap<Point, Double> points_order;
    public                   SortedMap<Point, Double> points_to_discover;
    public          final    HashMap<Point, Double> maperp;
    private         final    int[][]     visited;
    
    public          final    HashMap<Point, LinkedList<Point>> from_paths = 
                                                new HashMap();
    public          final    HashMap<Point, LinkedList<Point>> to_paths = 
                                                new HashMap();
    public          final    HashMap<Agent, Point> starters = new HashMap();
    
    public Base(WorldMap map, int x, int y, int num_robots){
        mapper_types_scanned = new int[map.SIZE][map.SIZE];
        mapper_timout = new int[map.SIZE][map.SIZE];
        mapper = new Pane[map.SIZE][map.SIZE];
        model.setBackground(
                new Background(
                        new BackgroundFill(
                                Color.BISQUE, 
                                CornerRadii.EMPTY, 
                                Insets.EMPTY
                        )
                )
        );
        model.setPrefWidth(WorldMap.TILE_SIZE * num_robots);
        model.setPrefHeight(WorldMap.TILE_SIZE * 3);
        model.setLayoutX(WorldMap.TILE_SIZE * x);
        model.setLayoutY(WorldMap.TILE_SIZE * y);
        map.enviroment.getChildren().add(model);
        HashMap<Point, Double> maper = new HashMap<>();
        maperp = new HashMap<>();
        points_order = new TreeMap<>(new ValueComparator(maper));
        points_to_discover = new TreeMap<>(new ValueComparator(maperp));
        visited = new int[map.SIZE][map.SIZE];
        X = x;
        Y = y;
        W = num_robots;
        H = 3;
        for (int i=0;i < map.SIZE;i++){
            for (int j=0;j < map.SIZE;j++){
                maper.put( 
                        map.getPoint(i, j),
                        Math.sqrt(Math.pow((double)(i - (X + W/2)), 2) 
                                + Math.pow((double)(j - (Y + H/2)), 2))
                );
                mapper_types_scanned[i][j] = WorldMap.WALL_TILE;
                mapper_timout[i][j] = MAX_UNSEEN - 2;
                visited[i][j] = 0;
                mapper[i][j] = new Pane();
                mapper[i][j].setPrefWidth(4);
                mapper[i][j].setPrefHeight(4);
                mapper[i][j].setLayoutX(
                        WorldMap.TILE_SIZE * i - 2 + WorldMap.TILE_SIZE / 2
                );
                mapper[i][j].setLayoutY(
                        WorldMap.TILE_SIZE * j - 2 + WorldMap.TILE_SIZE / 2
                );
                map.enviroment.getChildren().add(mapper[i][j]);
            }
        }
        points_order.putAll(maper);
        enviroment = map;
        for (int i = x;i < (x + num_robots);i++){
            for (int j = y;j < (y + 3);j++){
                map.setTile(i, j, WorldMap.ROBOT_BASE_ON_TILE);
            }
        }
        for (int i=0;i < num_robots;i++) {
	    Agent t1 = new Agent(map, x + i, y);
	    starters.put(t1, map.getPoint(x + i, y));
            robots.add(t1);
	    t1 = new Agent(map, x + i, y + 1);
	    starters.put(t1, map.getPoint(x + i, y + 1));
            robots.add(t1);
	    t1 = new Agent(map, x + i, y + 2);
	    starters.put(t1, map.getPoint(x + i, y + 2));
            robots.add(t1);
        }
        robots.forEach((a) -> {
            enviroment.moving_objects.add(a);
        });
        update_view();
    }
    
    public final void update_view() {
        System.out.println("update_view");
        for (int i=0;i < enviroment.SIZE;i++){
            for (int j=0;j < enviroment.SIZE;j++){
                if (((X - 1) <= i) && (i <= (X + W)) && 
                        ((Y - 1) <= j) && (j <= (Y + H))) {
                    mapper_types_scanned[i][j] = enviroment.getTile(i, j);
                    mapper_timout[i][j] = 0;
                } else {
                    if (mapper_timout[i][j] < (MAX_UNSEEN*MAX_UNSEEN))
                        mapper_timout[i][j]++;
                }
                int c = (mapper_timout[i][j] * 255) / MAX_UNSEEN;
                if (c > 255) {
                    c = 255;
                }
                mapper[i][j].setBackground(
                    new Background(
                        new BackgroundFill(
                            Color.rgb(c, c, c, 0.5), 
                            CornerRadii.EMPTY, 
                            Insets.EMPTY
                        )
                    )
                );
            }
        }
    }
    
    public boolean isInBase(Agent ex) {
        return (((X) <= ex.getX()) && (ex.getX() <= (X + W - 1)) && 
                        ((Y) <= ex.getY()) && (ex.getY() <= (Y + H - 1)));
    }

    @Override
    public void step() {
        update_view();
        for (SortedMap.Entry<Point, Double> pair : points_order.entrySet()) {
            if (mapper_timout[pair.getKey().x][pair.getKey().y] > MAX_UNSEEN)
                if (isPointAvailable(pair.getKey().x, pair.getKey().y)) {
                    mapper_types_scanned[pair.getKey().x][pair.getKey().y] 
                                                    = WorldMap.WALL_TILE;
                    maperp.put(enviroment.getPoint(
                            pair.getKey().x, pair.getKey().y
                    ), pair.getValue());
                    points_to_discover = new TreeMap<>(new ValueComparator(maperp));
                    points_to_discover.putAll(maperp);
                }
        }
        robots.forEach((Agent i) -> {
            i.step();
            System.out.println("Agent " + i + " " + i.getStatusDescription()
                    + " in base " + isInBase(i) + " loaded " + i.isLoaded() 
                    + " x = " + i.getX() + " y = " + i.getY() );
            if (isInBase(i)) {
                HashMap<Point, Integer> t = i.getScannedPoints();
                for (Map.Entry<Point, Integer> pair : t.entrySet()) {
                    mapper_types_scanned[pair.getKey().x][pair.getKey().y] = 
                                                                pair.getValue();
                    mapper_timout[pair.getKey().x][pair.getKey().y] = 0;
                    while (maperp.containsKey(pair.getKey())) {
                        maperp.remove(pair.getKey());
                    }
                }
                points_to_discover = 
                        new TreeMap<>(new ValueComparator(maperp));
                points_to_discover.putAll(maperp);
                t.clear();
                if (i.isLoaded()) {
                    i.unloadSomething();
                }
                if (i.getStatus() == Agent.BROKEN) {
                    i.repair();
		    i.setManualPosition(starters.get(i));
		}
                if ((i.getStatus() == Agent.READY) && 
                        !points_to_discover.isEmpty()) {
                    Point t2 = points_to_discover.firstKey();
                    maperp.remove(t2);
                    enviroment.getPointsLocal(t2.x,t2.y)
                                .stream().forEach((e) -> {maperp.remove(e);});
                    points_to_discover = 
                            new TreeMap<>(new ValueComparator(maperp));
                    points_to_discover.putAll(maperp);
                    Thread thread = new Thread(){
                        public void run(){
                            i.startCalculation();
                            Point p1 = enviroment.getPoint(i.getX(), i.getY());
                            LinkedList<Point> lp1;
                            LinkedList<Point> lp2;
                            if (from_paths.containsKey(p1)) {
                                lp1 = from_paths.get(p1);
                            } else {
                                lp1 = getPath(p1.x, p1.y, X, Y);
                                from_paths.put(p1, lp1);
                            }
                            if (lp1 != null) {
                                if (to_paths.containsKey(t2)) {
                                    lp2 = to_paths.get(t2);
                                } else {
                                    lp2 = getPath(X, Y, t2.x, t2.y);
                                    to_paths.put(t2, lp2);
                                }
                                if (lp2 != null) {
                                    LinkedList<Point> lp_n = new LinkedList<>();
                                    lp_n.addAll(lp1);
                                    lp_n.addAll(lp2);
                                    i.setNextTask(lp_n);
                                }
                            }
                            i.finishCalculation();
                        }
                    };
                    thread.start();
                }
            }
        });
    }
    
    public boolean isPointAvailable(int x, int y) {
        return isTileAccessible(x - 1, y) 
                || isTileAccessible(x, y - 1)
                || isTileAccessible(x + 1, y)
                || isTileAccessible(x, y + 1);
    }

    private boolean isTileAccessible(int x, int y) {
        if ( 0 <= x && x < enviroment.SIZE 
                && 0 <= y && y < enviroment.SIZE )
            return mapper_types_scanned[x][y] <= 0;
        else
            return false;
    }
    
    public void PathFind(List<Point> options, List<Point> path, Point p1, Point p2) {
        Point current = path.get(path.size() - 1);
        boolean done = false;
        if (current.x == p2.x && current.y == p2.y) {
            if (options.isEmpty())
                options.addAll(path);
            else
                if (options.size() > path.size()){
                    options.clear();
                    options.addAll(path);
                }
            return;
        }
        if (path.size() > 30) 
            done = true;
        if (path.size() > options.size() && !options.isEmpty()) 
                done = true;
        if (!done) {
            try {
                if (isTileAccessible(current.x - 1, current.y)) {
                    Point pp = enviroment.getPoint(current.x - 1, current.y);
                    if (!path.contains(pp)) {
                        List<Point> temp = new ArrayList<>(path);
                        temp.add(pp);
                        PathFind(options, temp, pp, p2);
                    }
                } else if ((current.x - 1) == p2.x && current.y == p2.y) {
                    if (options.isEmpty())
                        options.addAll(path);
                    else
                        if (options.size() > path.size()){
                            options.clear();
                            options.addAll(path);
                        }
                    return;
                }
            } catch (Exception e) {
            }
            try {
                if (isTileAccessible(current.x + 1, current.y)) {
                    Point pp = enviroment.getPoint(current.x + 1, current.y);
                    if (!path.contains(pp)) {
                        List<Point> temp = new ArrayList<>(path);
                        temp.add(pp);
                        PathFind(options, temp, pp, p2);
                    }
                } else if ((current.x + 1) == p2.x && current.y == p2.y) {
                    if (options.isEmpty())
                        options.addAll(path);
                    else
                        if (options.size() > path.size()){
                            options.clear();
                            options.addAll(path);
                        }
                    return;
                }
            } catch (Exception e) {
            }
            try {
                if (isTileAccessible(current.x, current.y - 1)) {
                    Point pp = enviroment.getPoint(current.x, current.y - 1);
                    if (!path.contains(pp)) {
                        List<Point> temp = new ArrayList<>(path);
                        temp.add(pp);
                        PathFind(options, temp, pp, p2);
                    }
                } else if ((current.x) == p2.x && (current.y - 1) == p2.y) {
                    if (options.isEmpty())
                        options.addAll(path);
                    else
                        if (options.size() > path.size()){
                            options.clear();
                            options.addAll(path);
                        }
                    return;
                }
            } catch (Exception e) {
            }
            try {
                if (isTileAccessible(current.x, current.y + 1)) {
                    Point pp = enviroment.getPoint(current.x, current.y + 1);
                    if (!path.contains(pp)) {
                        List<Point> temp = new ArrayList<>(path);
                        temp.add(pp);
                        PathFind(options, temp, pp, p2);
                    }
                } else if ((current.x) == p2.x && (current.y + 1) == p2.y) {
                    if (options.isEmpty())
                        options.addAll(path);
                    else
                        if (options.size() > path.size()){
                            options.clear();
                            options.addAll(path);
                        }
                    return;
                }
            } catch (Exception e) {
            }
        }
    }

    
    public LinkedList<Point> getPath(int x1, int y1, int x2, int y2) {
        LinkedList<Point> pre = new LinkedList<>();
        LinkedList<Point> ret = new LinkedList<>();
        System.out.println("["+x1+", "+y1+"] *->* ["+x2+", "+y2+"]");
        pre.add(enviroment.getPoint(x1, y1));
        List<Point> options = new LinkedList<>();
        PathFind(
                options, pre, 
                enviroment.getPoint(x1, y1), 
                enviroment.getPoint(x2, y2)
        );
        if (options.isEmpty())
            return null;
        ret.addAll(options);
        for (int i = options.size() - 2;i >= 0;i--) {
            ret.add(options.get(i));
        }
        System.out.println("Generating path:");
        for (Point p : ret)
            System.out.println("["+p.x+", "+p.y+"] ->");
        return ret;
    }
}
