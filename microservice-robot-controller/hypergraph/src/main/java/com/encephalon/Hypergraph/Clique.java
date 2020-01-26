/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encephalon.Hypergraph;

/**
 *
 * @author xitre
 * @param <T>
 */
public class Clique<T> extends Vertex<String> {
    public final Vertex[] tri_vect;
    
    public Clique(Clique<T> graph) {
        super(graph);
        tri_vect = graph.tri_vect;
    }
    
    private String getDataString(Vertex... V){
        String data_l = "";
        for (Vertex<T> v : V)
            data_l = data_l.concat(v.getData().toString());
        return data_l;
    }
    
    public Clique(Vertex... V) {
        super(null,0,"");
        this.name = getDataString(V);
        this.data = getDataString(V);
        if (V == null){
            tri_vect = null;
            return;
        }
        if (V.length < 1){
            tri_vect = null;
            return;
        }
        int counter = 0;
        tri_vect = new Vertex[V.length];
        for (Vertex<T> v : V)
            tri_vect[counter++] = v;
    }
    
    public static boolean isOneIncluded(Clique a, Clique b){
        Clique t;
        if (a.tri_vect.length < b.tri_vect.length){
            t = a;
            a = b;
            b = t;
        }
        return a.getInterconnection(b).length == b.tri_vect.length;
    }
    
    public boolean isVertexOneOfClique(Vertex<T> _tri_vect){
        for (Vertex<T> v : tri_vect) {
            if (v.equals(_tri_vect)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isVertexOneOfClique(T _tri_vect){
        for (Vertex<T> v : tri_vect) {
            if (v.getData().equals(_tri_vect)) {
                return true;
            }
        }
        return false;
    }
    
    public Vertex[] getOtherVerticals(Vertex<T> _tri_vect){
        Vertex<T>[] ret;
        Vertex<T>[] sample = new Vertex[tri_vect.length];
        int counter = 0;
        for (Vertex<T> v : tri_vect) {
            if (v.equals(_tri_vect)) {
                continue;
            }
            sample[counter++] = v;
        }
        ret = new Vertex[counter];
        System.arraycopy(sample, 0, ret, 0, counter);
        return ret;
    }
    
    public Vertex[] getOtherVerticals(T _tri_vect){
        Vertex<T>[] ret;
        Vertex<T>[] sample = new Vertex[tri_vect.length];
        int counter = 0;
        for (Vertex<T> v : tri_vect) {
            if (v.getData().equals(_tri_vect)) {
                continue;
            }
            sample[counter++] = v;
        }
        ret = new Vertex[counter];
        System.arraycopy(sample, 0, ret, 0, counter);
        return ret;
    }
    
    public Vertex[] getInterconnection(Clique<T> other){
        Vertex<T>[] ret;
        Vertex<T>[] sample = new Vertex[tri_vect.length];
        int counter = 0;
        for (Vertex<T> v : tri_vect) {
            for (Vertex v_other : other.tri_vect) {
                if (v.equals(v_other)) {
                    sample[counter++] = v;
                    break;
                }
            }
        }
        ret = new Vertex[counter];
        System.arraycopy(sample, 0, ret, 0, counter);
        return ret;
    }
    
    public static Vertex[] getInterconnection(Clique a, Clique b){
        if ( (a == null) || (b == null) )
            return new Vertex[0];
        Vertex[] ret;
        Vertex[] sample;
        if (a.tri_vect.length > b.tri_vect.length)
            sample = new Vertex[a.tri_vect.length];
        else
            sample = new Vertex[b.tri_vect.length];
        int counter = 0;
        for (Vertex v : a.tri_vect) {
            for (Vertex v_other : b.tri_vect) {
                if (v.equals(v_other)) {
                    sample[counter++] = v;
                    break;
                }
            }
        }
        ret = new Vertex[counter];
        System.arraycopy(sample, 0, ret, 0, counter);
        return ret;
    }
    
    public static Vertex[] getNotInterconnection(Clique a, Clique b){
        if ( (a == null) || (b == null) )
            return new Vertex[0];
        Vertex[] ret;
        Vertex[] sample;
        if (a.tri_vect.length > b.tri_vect.length)
            sample = new Vertex[a.tri_vect.length];
        else
            sample = new Vertex[b.tri_vect.length];
        int counter = 0;
        for (Vertex v : a.tri_vect) {
            boolean af = true;
            for (Vertex v_other : b.tri_vect) {
                if (v.equals(v_other)) {
                    af = false;
                    break;
                }
            }
            if (af)
                sample[counter++] = v;
        }
        ret = new Vertex[counter];
        System.arraycopy(sample, 0, ret, 0, counter);
        return ret;
    }
}
