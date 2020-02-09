/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encephalon.Hypergraph.Bayesian;

import com.encephalon.Hypergraph.Edge;
import com.encephalon.Hypergraph.Graph;
import com.encephalon.Hypergraph.GraphDAG;
import com.encephalon.Hypergraph.GraphFactor;
import com.encephalon.Hypergraph.Vertex;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author xitre
 * @param <T>
 */
public class BayesianNet<T> extends GraphDAG<T> {
    
    public BayesianNet(){
        super();
    }
    
    public BayesianNet(BayesianNet<T> other){
        super();
        this.cycles.addAll(other.cycles);
        this.edge.addAll(other.edge);
        this.vertices.putAll(other.vertices);
    }
    
    public boolean isGraphFinished(){
        for (Map.Entry<T,Vertex<T>> pair : vertices.entrySet()) {
            if (this.isVertexLeaf(pair.getValue()) 
            && (((BayesianNode)pair.getValue()).getInstantiatedData() == null) ) {
                return false;
            }
        }
        return true;
    }
    
    public List<Potential> getPotentials() throws Exception {
        List<Potential> potentials = new LinkedList();
        if (!isGraphFinished())
            throw new Exception("GraphBayesianNet is not finished");
        for (Map.Entry<T,Vertex<T>> pair : vertices.entrySet()) {
            BayesianNode<T> v = (BayesianNode)pair.getValue();
            if (isVertexLeaf(v) || v.getInstantiated()) {
                Potential p = new Potential(
			v.getInstantiatedData(),  v.getName()
		);
		potentials.add(p);
            } else {
		potentials.add(v.getPosibilities());
	    }
        }
        return potentials;
    }
    
    public void addNode(T name, double[] p_table){
        logger.debug("Adding node: " + name.toString());
        BayesianNode<T> n = new BayesianNode(name,p_table);
        vertices.put(name, n);
        logger.debug("Added.");
    }
    
    public void addConnection(T name_to, Potential p_table, 
			    T ... name_from) 
                                                            throws Exception{
        if (p_table == null)
            throw new Exception("p_table == null");
	Set<Edge> ss = new HashSet();
	for (T item : name_from) {
	    logger.debug("Adding connection: from " + item.toString()
		    + " to " + name_to.toString());
	    Edge e = new Edge(  vertices.get(item), 
				vertices.get(name_to)  );
	    ss.add(e);
	    logger.debug(e.toString());
	}
	edge.addAll(ss);
	if (this.checkCyclic()){
	    edge.removeAll(ss);
	    ((BayesianNode<T>)vertices.get(name_to)).clearConnection();
	    logger.debug("Failed DAG check.");
	} else {
	    Vertex<T>[] sv = new Vertex[name_from.length];
	    for (int i=0;i < name_from.length;i++) {
		sv[i] = vertices.get(name_from[i]);
	    }
	    ((BayesianNode<T>)vertices.get(name_to)).setConnection(p_table, sv);
	    logger.debug("Added.");
	}
    }
    
    @Override
    public String toString(){
        String str = "";
        str = str.concat("=====BAYESIAN=NET=======================\n");
        str = str.concat("Verticals:\n");
        for (Map.Entry<T,Vertex<T>> pair1 : vertices.entrySet()) {
            str = str.concat(pair1.getKey()+"\n");
        }
        str = str.concat("----------------------------------------\n");
        str = str.concat("Edges:\n");
        for (Edge e1 : edge) {
            str = str.concat(e1.toString()+"\n");
        }
        str = str.concat("========================================\n");
        return str;
    }
    
    public void calculateProbabilities(){
        GraphFactor gb = new GraphFactor(new BayesianNet(this));
        System.out.println(gb.toString());
        Graph c = gb.buildUndirectedGraphFactory();
        System.out.println(c.toString());
        c.reduceCyclic();
        System.out.println(c.toString());
        BayesianJunctionTree<String> cg;
        try {
            cg = new BayesianJunctionTree(c,this);
            System.out.println(cg.toString());
            cg.buildTree();
            System.out.println(cg.toString());
            cg.globalCollect();
//            cg.globalDistribute();
            for (Map.Entry<T,Vertex<T>> pair1 : vertices.entrySet()) {
                ((BayesianNode)pair1.getValue()).setInstantiatedData(
                        cg.calculatePotentialToVertex(
                                pair1.getKey().toString()
                ));
            }
        } catch (Exception ex) {
            Logger.getLogger(
                    BayesianNet.class.getName()
            ).log(Level.SEVERE, null, ex);
        }
    }
    
}
