/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encephalon.Hypergraph;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.slf4j.LoggerFactory;

/**
 *
 * @author gusev_a
 */
public class Hyperedge {
    private final static org.slf4j.Logger logger = 
            LoggerFactory.getLogger(Hyperedge.class);
    
    private final Set<Vertex> input = new HashSet();
    private final Set<Vertex> output = new HashSet();
    
    public Hyperedge() {
        this(null);
    }
    
//    new Hyperedge(ImmutableMap.<String, Object>of(
//            "in", Node,
//            "out", Node, 
//            "in", Node
//    )); 
    public Hyperedge(Map<String, Object> parameters) {
    }
}
