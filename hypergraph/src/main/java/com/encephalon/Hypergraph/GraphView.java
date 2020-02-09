/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encephalon.Hypergraph;

import com.encephalon.Hypergraph.math.Point;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.LoggerFactory;

/**
 *
 * @author gusev_a
 */
public class GraphView<T> {
    private final static org.slf4j.Logger logger = 
            LoggerFactory.getLogger(GraphView.class);
    
    private Vertex<T>[][] position_map;
    private double distance;
    
    public Double sx = 1.;
    public Double sy = 1.;
    
    public final Graph<T> graph;
    
    public GraphView(Map<T,Vertex> vertices, Graph<T> graph){
        this.position_map = new Vertex[vertices.size()*2][vertices.size()*2];
        this.sx = new Double(vertices.size());
        this.sy = new Double(vertices.size());
        this.graph = graph;
        Iterator<Vertex> iter = vertices.values().iterator();
        GraphView.placeObjectsToMatrix(this.position_map, iter, vertices.size());
        this.distance = calculateDist();
        System.out.println("this.distance: " + this.distance);
        System.out.println(graph.getClass().getName());
        if (graph.getClass().getName().contains("GraphDAG") ||
                graph.getClass().getName().contains("BayesianNet") )
            optimizeViewDAG();
        else
            optimizeView();
        System.out.println(this.toString());
    }
    
    private static <T> void placeObjectsToMatrix(T[][] position_map, 
                                                    Iterator<T> iter,
                                                    int size){
        int x,y,dx,dy;
        x = y = 0;
        dx = 0;
        dy = -1;
        int t = size;
        int S = size;
        int Sd2 = size/2;
//        int Sd2 = 0;
        int maxI = t*t;
        for(int i=0; i < maxI; ){
            if ( (0 <= (x+Sd2)) && ((x+Sd2) < S) 
                    && (0 <= (y+Sd2)) && ((y+Sd2) < S) ){
                if (iter.hasNext()){
                    position_map[x+Sd2][y+Sd2] = iter.next();
                    i++;
                } else {
                    break;
                }
            }
            if ( (x == y) 
                    || ((x < 0) && (x == -y)) 
                    || ((x > 0) && (x == 1-y)) ){
                t = dx;
                dx = -dy;
                dy = t;
            }
            x += dx;
            y += dy;
        }        
    }
    
    public Point findPositionInMatrix(Vertex<T> obj){
        for (int i=0;i < this.position_map.length;i++)
            for (int j=0;j < this.position_map[i].length;j++)
                if (this.position_map[i][j] != null) {
                    if (this.position_map[i][j].equals(obj))
                        return new Point(i,j);
                }
        return null;
    }
    
    private static <T> Point findPositionInMatrix(T[][] position_map, T obj){
        for (int i=0;i < position_map.length;i++)
            for (int j=0;j < position_map[i].length;j++)
                if (position_map[i][j] != null)
                    if (position_map[i][j].equals(obj))
                        return new Point(i,j);
        return null;
    }
    
    private double calculateDist(){
        double sum = 0;
        for (int i=0;i < this.position_map.length;i++)
            for (int j=0;j < this.position_map[i].length;j++)
                if (this.position_map[i][j] != null){
                    double len = 0;
                    Point p = new Point(i,j);
                    List<Vertex> linked = this.graph.getNodesConnected(
                            this.position_map[i][j].getData()
                    );
                    len = linked.stream().map((n) -> GraphView.getMinkovHex(p, 
                            GraphView.findPositionInMatrix(
                                    this.position_map, n
                            )
                    )).reduce(len, (accumulator, _item) -> accumulator + _item);
                    sum += len;
                }
        return sum;
    } 
    
    private double calculateDistDAG(){
        double sum = 0;
        sum = this.graph.edge.stream().map((eg) -> GraphView.getDirectDist( 
                GraphView.findPositionInMatrix(
                        this.position_map, eg.getFrom()
                ),
                GraphView.findPositionInMatrix(
                        this.position_map, eg.getTo()
                )
        )).map((len) -> len).reduce(sum, (accumulator, _item) -> accumulator + _item);
        return sum;
    } 
    
    private double calculateVertexDist(Point p){
        double len = 0;
        Vertex<T> item = this.position_map[p.x][p.y];
        if (item != null) {
            List<Vertex> linked = this.graph.getNodesConnected(item.getData());
            len = linked.stream().map((n) -> GraphView.getMinkovHex(p, 
                    GraphView.findPositionInMatrix(
                            this.position_map, n
                    )
            )).reduce(len, (accumulator, _item) -> accumulator + _item);
        }
        return len;
    } 
    
    private double calculateVertexDistFromPoint(Point p,Point other){
        double len = 0;
        Vertex<T> item = this.position_map[p.x][p.y];
        if (item != null) {
            List<Vertex> linked = this.graph.getNodesConnected(item.getData());
            len = linked.stream().map((n) -> GraphView.getMinkovHex(other, 
                    GraphView.findPositionInMatrix(
                            this.position_map, n
                    )
            )).reduce(len, (accumulator, _item) -> accumulator + _item);
        }
        return len;
    } 
    
    private void optimizeView(){
        double len;
        do {
            System.out.println(this.toString());
            System.out.println("this.distance: " + this.distance);
            System.out.println();
            len = this.distance;
            Vertex<T> t;
            double diff;
            for (int i=0;i < this.position_map.length;i++)
                for (int j=0;j < this.position_map[i].length;j++)
                    for (int l=0;l < this.position_map.length;l++)
                        for (int m=0;m < this.position_map[l].length;m++){
//                                diff = calculateDist();
//                                System.out.println("----------------------------------------");
                            t = this.position_map[i][j];
                            this.position_map[i][j] = this.position_map[l][m];
                            this.position_map[l][m] = t;
                            diff = calculateDist();
//                                System.out.println(String.format("[%d][%d] <-> [%d][%d] now(%f)<>(%f)"
//                                ,i,j,l,m,(float)diff,(float)this.distance));
//                                System.out.println(this.toStringExcept(i,j,l,m));
                            if (diff < this.distance) {
                                this.distance = diff;
                                System.out.println(this.toString());
                                System.out.println("diff: " + diff);
                                System.out.println();
                            } else {
                                t = this.position_map[i][j];
                                this.position_map[i][j] = this.position_map[l][m];
                                this.position_map[l][m] = t;
                            }
                        }
        } while (len > this.distance);
        this.trimView();
    } 
    
    private void optimizeViewDAG(){
        double len;
        do {
            System.out.println(this.toString());
            System.out.println("this.distance: " + this.distance);
            System.out.println();
            len = this.distance;
            Vertex<T> t;
            double diff;
            for (int i=0;i < this.position_map.length;i++)
                for (int j=0;j < this.position_map[i].length;j++)
                    for (int l=0;l < this.position_map.length;l++)
                        for (int m=0;m < this.position_map[l].length;m++){
                            t = this.position_map[i][j];
                            this.position_map[i][j] = this.position_map[l][m];
                            this.position_map[l][m] = t;
                            diff = calculateDistDAG();
                            if (diff < this.distance) {
                                this.distance = diff;
                                System.out.println(this.toString());
                                System.out.println("diff: " + diff);
                                System.out.println();
                            } else {
                                t = this.position_map[i][j];
                                this.position_map[i][j] = this.position_map[l][m];
                                this.position_map[l][m] = t;
                            }
                        }
        } while (len > this.distance);
        this.trimView();
    } 
    
    private void trimView(){
        int top = -1, bot = -1, left = -1, right = -1;
        for (int i=0;i < this.position_map.length;i++)
            if (top == -1)
                for (Vertex<T> item : this.position_map[i]) {
                    if (item != null) {
                        top = i;
                    }
                }
        for (int i=this.position_map.length-1;i >= 0;i--)
            if (bot == -1)
                for (Vertex<T> item : this.position_map[i]) {
                    if (item != null) {
                        bot = i;
                    }
                }
        for (int j=0;j < this.position_map[0].length;j++)
            if (left == -1)
                for (int i=0;i < this.position_map.length;i++) {
                    if (this.position_map[i][j] != null) {
                        left = j;
                    }
                }
        for (int j=this.position_map[0].length-1;j >= 0;j--)
            if (right == -1)
                for (int i=0;i < this.position_map.length;i++) {
                    if (this.position_map[i][j] != null) {
                        right = j;
                    }
                }
        if ( ((top < 0) || (this.position_map.length <= top))
            || ((bot < 0) || (this.position_map.length <= bot))
            || ((left < 0) || (this.position_map[0].length <= left))
            || ((right < 0) || (this.position_map[0].length <= right))
            || (top > bot)
            || (left > right) )
            return;
        Vertex[][] position_map2 = new Vertex[bot-top+1][right-left+1];
        for (int i=top;i <= bot;i++)
            for (int j=left;j <= right;j++)
                position_map2[i-top][j-left] = this.position_map[i][j];
        this.position_map = position_map2;
        this.sx = new Double(bot-top+1);
        this.sy = new Double(right-left+1);
    }
    
    private static double getMinkov(Point p1, Point p2){
        if ( (p1 == null) || (p2 == null) )
            return 0;
        double dist = Math.sqrt( Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2) );
        return dist;
    }
    
    private static double getMinkovHex(Point p1, Point p2){
        if ( (p1 == null) || (p2 == null) )
            return 0;
        int p1x = p1.x*2;
        int p2x = p2.x*2;
        int p1y = p1.y*2 + p1.x % 2;
        int p2y = p2.y*2 + p2.x % 2;
        double dist = Math.sqrt( 
                Math.pow(p1x - p2x, 2) + Math.pow(p1y - p2y, 2) 
        );
        return dist;
    }
    
    private static double getDirectDist(Point p1, Point p2){
        if ( (p1 == null) || (p2 == null) )
            return 0;
        int p1x = p1.x*2;
        int p2x = p2.x*2;
        int p1y = p1.y*2 + p1.x % 2;
        int p2y = p2.y*2 + p2.x % 2;
        double mult = 1;
        if (p1y >= p2y) 
            mult = 4;
//        if (p1x == p2x) 
//            mult = 2;
        if ( (p1x > p2x) && (p1y > p2y) ) 
            mult = 6;
        double dist = mult*Math.sqrt( 
                Math.pow(p1x - p2x, 2) + Math.pow(p1y - p2y, 2) 
        );
        return dist;
    }
    
    @Override
    public String toString(){
        String str = new String();
        for (Vertex<T>[] position_map1 : this.position_map) {
            for (Vertex<T> position_map11 : position_map1) {
                if (position_map11 != null) {
                    str = str.concat(position_map11.getData().toString());
                } else {
                    str = str.concat("0");
                }
                str = str.concat(" ");
            }
            str = str.concat("\r\n");
        }
        return str;
    } 
    
    public String toStringExcept(int x1,int y1,int x2,int y2){
        String str = new String();
        for (int i=0;i < this.position_map.length;i++) {
            Vertex<T>[] position_map1 = this.position_map[i];
            for (int j=0;j < position_map1.length;j++) {
                if ( ((i == x1) && (j == y1)) || ((i == x2) && (j == y2)) ) {
                    str = str.concat("X");
                    str = str.concat(" ");
                    continue;
                }
                Vertex<T> position_map11 = position_map1[j];
                if (position_map11 != null) {
                    str = str.concat(position_map11.getData().toString());
                } else {
                    str = str.concat("0");
                }
                str = str.concat(" ");
            }
            str = str.concat("\r\n");
        }
        return str;
    } 
}
