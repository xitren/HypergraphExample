/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encephalon.Hypergraph.Bayesian;

import com.encephalon.Hypergraph.Edge;
import com.encephalon.Hypergraph.Graph;
import com.encephalon.Hypergraph.GraphJunctionTree;
import com.encephalon.Hypergraph.Separator;
import com.encephalon.Hypergraph.Vertex;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author xitre
 */
public class BayesianJunctionTree<T> extends Graph<T> {
    private List<Potential> graph_potentials = new LinkedList();
    private Map<T,Potential> calculated_vertex;
    
    public BayesianJunctionTree(Graph<T> base, BayesianNet<T> ref_net) 
                                                        throws Exception {
        super();
        graph_potentials.clear();
        graph_potentials.addAll(ref_net.getPotentials());
        base.cycles
                .stream()
                .filter((v1) -> ((v1.length >= 4) && base.isFullyconnected(v1)))
                .map((v1) -> new BayesianClique(graph_potentials,(Vertex[])v1))
                .filter((nw) -> {
                        boolean rr = this.checkAlreadyIncluded((BayesianClique)nw);
//                        if (rr) {
//                            graph_potentials.addAll(((BayesianClique)nw).potentials);
//                        }
                        return !rr;
                })
                .forEachOrdered((nw) -> {
                    vertices.put(
                            (T)((BayesianClique)nw).getData(), 
                            (BayesianClique)nw
                    );
                });
        base.cycles
                .stream()
                .filter((v1) -> (v1.length == 3))
                .map((v1) -> new BayesianClique(graph_potentials,v1))
                .filter((BayesianClique nw) -> {
                        boolean rr = this.checkAlreadyIncluded(nw);
//                        if (rr) {
//                            graph_potentials.addAll(nw.potentials);
//                        }
                        return !rr;
                })
                .forEachOrdered((nw) -> {
                    vertices.put(
                            (T)((BayesianClique)nw).getData(), 
                            (BayesianClique)nw
                    );
                });
        base.vertices.entrySet().forEach((pair) -> {
            try {
                Vertex l = pair.getValue();
                if (base.vertexNotInAnyCycle(l)) {
                    vertices.entrySet()
                            .stream()
                            .map((pair1) -> (BayesianClique)pair1.getValue())
                            .map((a) -> (base.getConnectionToAny(l,a.tri_vect)))
                            .filter((e) -> (e != null))
                            .forEachOrdered((e) -> {
                                BayesianClique b = new BayesianClique(
                                        graph_potentials,
                                        ((Edge)e).getFrom(), 
                                        ((Edge)e).getTo()
                                );
                                if (!this.checkAlreadyIncluded(b))
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
                    BayesianClique a = (BayesianClique)pair1.getValue();
                    vertices.entrySet()
                            .stream()
                            .filter((pair2) -> !(a.equals(pair2.getValue())))
                            .map((pair2) -> {
                                try {
                                    return new Separator(
                                            a,(BayesianClique)pair2.getValue()
                                    );
                                } catch (Exception ex) {
                                    return null;
                                }
                            }).filter((e) -> (e != null))
                            .filter((e) -> !(checkExistedConnections(e)))
                            .forEach((e) -> {
                                edge.add(e);
                                ((BayesianClique)e.getTo()).addConnection(
                                        ((BayesianClique)e.getFrom())
                                );
                                ((BayesianClique)e.getFrom()).addConnection(
                                        ((BayesianClique)e.getTo())
                                );
                            });
                });
    }
    
    private boolean checkAlreadyIncluded(BayesianClique<T> c){
        return vertices.entrySet().stream().anyMatch(
                (pair) -> (BayesianClique.isOneIncluded(
                        (BayesianClique)pair.getValue(), c
                ))
        );
    }
    
    private boolean checkExistedConnections(Separator e){
        if (e.conn_vect.length < 1)
            return true;
        return edge.stream().anyMatch((e1) -> (e.equals((Separator)e1)));
    }
    
    @Override
    public boolean removeLighter(){
        double min = Integer.MAX_VALUE;
        Edge e_to_del = null;
        System.out.println("///////remove lighter3");
        for (Edge e : edge) {
            System.out.println(e.toString());
            if (!this.isInCycle(e))
                continue;
            double w = e.getWeight();
            System.out.println(""+w);
            if ( (min > w) ) {
                min = w;
                e_to_del = e;
            }
        }
        System.out.println("///////remove lighter3");
        if (e_to_del != null){
            System.out.println(e_to_del.toString());
            System.out.println("//////////////////////////");
            edge.remove(e_to_del);
            ((BayesianClique)e_to_del.getTo()).deleteConnection(
                    ((BayesianClique)e_to_del.getFrom())
            );
            ((BayesianClique)e_to_del.getFrom()).deleteConnection(
                    ((BayesianClique)e_to_del.getTo())
            );
            return true;
        }
        System.out.println("//////////////////////////");
        return false;
    }
    
    @Override
    public String toString(){
        String str = "";
        str = str.concat("=====BAYESIAN=JUNCTION=TREE=============\n");
        str = str.concat("Cliques:\n");
        for (Map.Entry<T,Vertex<T>> pair1 : vertices.entrySet()) {
            str = str.concat(pair1.getKey()+"\n");
            BayesianClique<T> bc = (BayesianClique)pair1.getValue();
            if (bc.potentials.size() > 0) {
                str = str.concat("    Included potentials:\n");
                for (Potential p : bc.potentials) {
                    str = str.concat(""+p.toString());
                }
                str = str.concat("    Included connection:\n");
                for (Map.Entry<BayesianClique<T>,Potential[]> pair : 
                        bc.messages.entrySet()) {
                    str = str.concat("        "+pair.getKey().getName()+"\n");
                }
            }
        }
        str = str.concat("----------------------------------------\n");
        str = str.concat("Separators:\n");
        for (Edge e1 : edge) {
            str = str.concat(e1.toString()+"\n");
        }
        str = str.concat("----------------------------------------\n");
        str = str.concat("Potentials:\n");
        for (Potential p : graph_potentials) {
            str = str.concat(p.toString()+"\n");
        }
        str = str.concat("========================================\n");
        
        return str;
    }
    
    public double[] calculatePotentialToVertex(T v){
        Potential p = null;
        double[] ret = null;
        for (Map.Entry<T,Vertex<T>> pair1 : vertices.entrySet()) {
            BayesianClique<T> bc = (BayesianClique)pair1.getValue();
            if (bc.isVertexOneOfClique(v)) {
                for (Potential pot : bc.potentials) {
                    p = Potential.multiply(p, pot);
                }
                for (Potential[] pot : bc.messages.values()) {
                    p = Potential.multiply(p, pot[0]);
                    p = Potential.multiply(p, pot[1]);
                }
                try {
                    ret = p.getAlongDimension(v.toString());
                    break;
                } catch (Exception ex) {
//                    Logger.getLogger(
//                            BayesianJunctionTree.class.getName()
//                    ).log(Level.SEVERE, null, ex);
                    ret = new double[1];
                    ret[0] = 1;
                }
            }
        }
        return ret;
    }
    
    public Map globalCollect(){
        if (vertices.isEmpty())
            return null;
        for (Map.Entry<T,Vertex<T>> pair1 : vertices.entrySet()) {
            Potential p = ((BayesianClique)pair1.getValue()).collectEvidence(null);
            ((BayesianClique)pair1.getValue()).distributeEvidence(p,null);
            break;
        }
	calculated_vertex = new HashMap();
        for (Map.Entry<T,Vertex<T>> pair1 : vertices.entrySet()) {
            ((BayesianClique)pair1.getValue()).calculateSelf(calculated_vertex);
        }
	System.out.println("Resulted");
        for (Map.Entry<T,Potential> pair1 : calculated_vertex.entrySet()) {
            System.out.println(pair1.getKey().toString());
            System.out.println(((Potential)pair1.getValue()).toString());
        }
        return calculated_vertex;
    }
    
}
