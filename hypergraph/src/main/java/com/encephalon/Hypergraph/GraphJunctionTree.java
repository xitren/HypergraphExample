/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encephalon.Hypergraph;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gusev_a
 * @param <T>
 */
public class GraphJunctionTree<T> extends Graph<T> {
    
    public GraphJunctionTree(Graph<T> base) throws Exception{
        super();
        base.cycles
                .stream()
                .filter((v1) -> ((v1.length >= 4) && base.isFullyconnected(v1)))
                .map((v1) -> new Clique((Vertex[])v1))
                .filter((nw) -> !(this.checkAlreadyIncluded((Clique)nw)))
                .forEachOrdered((nw) -> {
                    vertices.put((T)((Clique)nw).getData(), (Clique)nw);
                });
        base.cycles
                .stream()
                .filter((v1) -> (v1.length == 3))
                .map((v1) -> new Clique(v1))
                .filter((nw) -> !(this.checkAlreadyIncluded(nw)))
                .forEachOrdered((nw) -> {
                    vertices.put((T)((Clique)nw).getData(), (Clique)nw);
                });
        base.vertices.entrySet().forEach((pair) -> {
            try {
                Vertex l = pair.getValue();
                if (base.vertexNotInAnyCycle(l)) {
                    vertices.entrySet()
                            .stream()
                            .map((pair1) -> (Clique)pair1.getValue())
                            .map((a) -> (base.getConnectionToAny(l,a.tri_vect)))
                            .filter((e) -> (e != null))
                            .forEachOrdered((e) -> {
                                Clique b = new Clique(
                                        ((Edge)e).getFrom(), ((Edge)e).getTo()
                                );
                                vertices.put((T)b.getData(), b);
                            });
                }
            } catch (Exception ex) {
                Logger.getLogger(GraphJunctionTree.class.getName()
                ).log(Level.SEVERE, null, ex);
            }
        });
        edge.clear();
        vertices.entrySet()
                .stream().forEach((pair1) -> {
                    Clique a = (Clique)pair1.getValue();
                    vertices.entrySet()
                            .stream()
                            .filter((pair2) -> !(a.equals(pair2.getValue())))
                            .map((pair2) -> {
                                try {
                                    return new Separator(a,(Clique)pair2.getValue());
                                } catch (Exception ex) {
                                    return null;
                                }
                            }).filter((e) -> (e != null))
                            .filter((e) -> !(checkExistedConnections(e)))
                            .forEach((e) -> {edge.add(e);});
                });
    }
    
    private boolean checkAlreadyIncluded(Clique<T> c){
        return vertices.entrySet().stream().anyMatch(
                (pair) -> (Clique.isOneIncluded(
                        (Clique)pair.getValue(), c
                ))
        );
    }
    
    private boolean checkExistedConnections(Separator e){
        if (e.conn_vect.length < 1)
            return true;
        return edge.stream().anyMatch((e1) -> (e.equals((Separator)e1)));
    }
    
    @Override
    public String toString(){
        String str = "";
        str = str.concat("=====GRAPH=JUNCTION=TREE================\n");
        str = str.concat("Cliques:\n");
        for (Map.Entry<T,Vertex<T>> pair1 : vertices.entrySet()) {
            str = str.concat(pair1.getKey()+"\n");
        }
        str = str.concat("----------------------------------------\n");
        str = str.concat("Separators:\n");
        for (Edge e1 : edge) {
            str = str.concat(e1.toString()+"\n");
        }
        str = str.concat("========================================\n");
        
        return str;
    }
}
