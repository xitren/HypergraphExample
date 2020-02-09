/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encephalon.Hypergraph;

/**
 *
 * @author gusev_a
 */
public class Separator extends Edge {
    
    public final Vertex[] conn_vect;
    
    public Separator(Clique a, Clique b) throws Exception {
        super(a,b,0);
        if (a.equals(b))
            throw new Exception("Cliques are the same!"); 
        conn_vect = a.getInterconnection(b);
    }
    
    @Override
    public double getWeight(){
        return (double) Clique.getInterconnection(
                (Clique)this.input, (Clique)this.output
        ).length;
    }
    
    public Clique getOtherClique(Clique a){
        if (a.equals(this.input))
            return (Clique)this.output;
        if (a.equals(this.output))
            return (Clique)this.input;
        return null;
    }
    
    public boolean isConnectedToClique(Clique a){
        return ( (a.equals(input)) || (a.equals(output)) );
    }
    
    @Override
    public String toString(){
        String str = "";
        str = str.concat(this.output.toString()+" - ");
        for (Vertex v : conn_vect) {
            str = str.concat(v.toString());
        }
        str = str.concat(" - "+this.input.toString());
        return str;
    }
}
