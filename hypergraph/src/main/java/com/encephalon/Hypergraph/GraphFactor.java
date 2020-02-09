/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encephalon.Hypergraph;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author xitre
 * @param <T>
 */
public class GraphFactor<T> extends Graph<T> {
    public final Set<Factor> edge = new HashSet();
    
    public GraphFactor(GraphDAG<T> other){
        super(other);
        other = new GraphDAG(other);
        for (Map.Entry<T,Vertex<T>> pair : other.vertices.entrySet()) {
            boolean ad = false;
            Factor f = new Factor();
            f.add(pair.getValue());
            for (Edge e : other.getEdgesConnectedTo(pair.getValue().getData())) {
                f.add(e.getFrom());
                ad = true;
            }
            if (ad)
                edge.add(f);
        }
    }
    
    public Graph<T> buildUndirectedGraphFactory(){
        Graph a = new Graph();
        a.vertices.putAll(vertices);
        for (Factor f : edge) {
            for (Vertex v1 : f.t) {
                for (Vertex v2 : f.t) {
                    try {
                        if (!a.getConnectedWay(v1.getData(), v2.getData()))
                            a.addConnection(v1.getData(), v2.getData());
                    } catch (Exception ex) {    
                        Logger.getLogger(
                                GraphFactor.class.getName()
                        ).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        for (Edge e : (Set<Edge>)a.edge) {
            System.out.println(""+e.getFrom().getName()+" => "+e.getTo().getName());
        }
        return a;
    }
    
    @Override
    public String toString(){
        String str = "";
        str = str.concat("=====GRAPH=FACTOR=======================\n");
        str = str.concat("Verticals:\n");
        for (Map.Entry<T,Vertex<T>> pair1 : vertices.entrySet()) {
            str = str.concat(pair1.getKey()+"\n");
        }
        str = str.concat("----------------------------------------\n");
        str = str.concat("Edges:\n");
        for (Factor f1 : edge) {
            str = str.concat(f1.toString()+"\n");
        }
        str = str.concat("========================================\n");
        return str;
    }
    

    
    
    
    
}
