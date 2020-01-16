/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encephalon.Hypergraph;

import org.slf4j.LoggerFactory;

/**
 *
 * @author gusev_a
 */
public class Edge {
    private final static org.slf4j.Logger logger = 
            LoggerFactory.getLogger(Edge.class);
    
    protected Vertex input,output;
    private int type;
    private double weight = 1;
    
    public Edge(Vertex a, Vertex b) {
        this(a,b,0);
    }
    
    public Edge(Vertex a, Vertex b, int type) {
        this.input = a;
        this.output = b;
        this.type = type;
    }
    
    public double getWeight(){
        return ( this.weight );
    }
    
    public void setWeight(double _weight){
        this.weight = _weight;
    }
    
    public Vertex getFrom(){
        return ( this.input );
    }
    
    public Vertex getTo(){
        return ( this.output );
    }
    
    public Vertex getOtherNode(Vertex a){
        if (a.equals(this.input))
            return this.output;
        if (a.equals(this.output))
            return this.input;
        return null;
    }
    
    public boolean isConnected(Vertex a, Vertex b){
        return ( (a.equals(input)) && (b.equals(output)) );
    }
    
    public boolean isConnectedUndir(Vertex a, Vertex b){
        return ( (a.equals(input)) && (b.equals(output)) ) 
                || ( (a.equals(output)) && (b.equals(input)) );
    }
    
    public boolean isConnectedToNode(Vertex a){
        return ( (a.equals(input)) || (a.equals(output)) );
    }
    
    public boolean isConnectedOutNode(Vertex a){
        return ( a.equals(input) );
    }
    
    public boolean isConnectedInNode(Vertex a){
        return ( a.equals(output) );
    }
    
    public boolean equals(Edge a){
        return ( (this.input.equals(a.input) && this.output.equals(a.output))
            || (this.input.equals(a.output) && this.output.equals(a.input)) );
    }
    
    @Override
    public String toString(){
        String str = "";
        str = str.concat(this.output.toString()+" - ");
        str = str.concat(""+this.input.toString());
        return str;
    }
}
