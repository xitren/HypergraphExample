/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encephalon.Hypergraph;

/**
 *
 * @author gusev_a
 * @param <T>
 */
public class GraphDAG<T> extends Graph<T> {
    
    public GraphDAG(){
        super();
    }
    
    public GraphDAG(Graph<T> other){
        super(other);
    }
    
    public void addClassifier(T[] names_inputs, T name_root) throws Exception{
        logger.debug("Build classifier.");
        this.addNode(name_root);
        for (T item : names_inputs){
            this.addNode(item);
            this.addConnection(item, name_root);
        }
        logger.debug("Classifier finished.");
    }
}
