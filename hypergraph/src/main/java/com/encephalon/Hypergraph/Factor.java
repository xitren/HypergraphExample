/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encephalon.Hypergraph;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author xitre
 */
public class Factor {
    public List<Vertex> t = new LinkedList();
    
    public Factor(Vertex... arguments){
        for (Vertex node : arguments) {
            t.add(node);
        }
    }
    
    public void clear(){
        t.clear();
    }
    
    public void add(Vertex node){
        t.add(node);
    }
    
    @Override
    public String toString(){
        String str = "";
        for (Vertex f1 : t) {
            str = str.concat(f1.toString()+" ");
        }
        return str;
    }
}
