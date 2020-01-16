/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encephalon.Hypergraph.Bayesian;

import com.encephalon.Hypergraph.Vertex;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author xitre
 * @param <T>
 */
public class BayesianNode<T> extends Vertex<T> {
    private Potential possibilities = null;
    private Set<Vertex<T>> connections = null;
    private boolean instantiated_status = false;
    private int instantiated;
    private double[] instantiated_data = null;
    private boolean was_changed = true;
    
    public BayesianNode(T data, double[] p_table) {
        this(data,0,data.toString(),p_table);
    }
    
    public BayesianNode(T data, Potential _possibilities, 
		Vertex<T>[] _connections, double[] p_table) {
        this(data,0,data.toString(),_possibilities,_connections,p_table);
    }
    
    public BayesianNode(T data, Potential _possibilities, 
		Set<Vertex<T>> _connections, double[] p_table) {
        this(data,0,data.toString(),_possibilities,_connections,p_table);
    }

    public BayesianNode(T data, int type, String name, double[] p_table) {
        super(data,type,name);
        this.instantiated_status = false;
//        this.possibilities = new Potential(p_table, name);
        if (p_table != null) {
            this.instantiated_data = p_table.clone();
        }
    }

    public BayesianNode(T data, int type, String name, Potential _possibilities, 
		Vertex<T>[] _connections, double[] p_table) {
        super(data,type,name);
        this.instantiated_status = false;
        this.possibilities = _possibilities;
        this.connections = new HashSet();
	for (Vertex<T> item : _connections) {
	    connections.add(item);
	}
        if (p_table != null) {
            this.instantiated_data = p_table.clone();
        }
    }

    public BayesianNode(T data, int type, String name, Potential _possibilities, 
		Set<Vertex<T>> _connections, double[] p_table) {
        super(data,type,name);
        this.instantiated_status = false;
        this.possibilities = _possibilities;
        this.connections = new HashSet(_connections);
        if (p_table != null) {
            this.instantiated_data = p_table.clone();
        }
    }
    
    public int getPosibilitiesLenght() {
	if (possibilities == null)
	    return 0;
	else
	    return possibilities.getDimension();
    }
    
    public Potential getPosibilities() {
	return possibilities;
    }

    public int setConnection(Potential p_table, Vertex<T> ... from) throws Exception {
        if ((from != null) && (p_table != null))
            if ((from.length + 1) != p_table.getDimension())
                throw new Exception("Different probability tables size");
        this.possibilities = p_table.clonePotential();
        this.connections = new HashSet();
	for (Vertex<T> item : from) {
	    connections.add(item);
	}
        return 0;
    }

    public int clearConnection(){
        this.possibilities = null;
	connections.clear();
        return 0;
    }
    
    public void setInstantiated(int p_id) throws Exception {
        if (p_id >= instantiated_data.length)
            throw new Exception("Different probability table size");
        instantiated = p_id;
        this.instantiated_status = true;
    }
    
    public boolean getInstantiated(){
        return instantiated_status;
    }
    
    public double[] getInstantiatedData(){
        if (instantiated_status){
            double[] data = new double[instantiated_data.length];
            was_changed = true;
            data[instantiated] = 1.;
            return data;
        } else {
            return this.instantiated_data;
        }
    }
    
    public void setInstantiatedData(double[] p){
        if (!this.leaf) {
            was_changed = true;
            instantiated_data = p.clone();
        }
        System.out.print(String.format("|%s|", this.data.toString()));
        for (double pp : instantiated_data)
            System.out.print(String.format(" %f", pp));
        System.out.println("");
    }
    
    protected boolean isChanged(){
        if (was_changed) {
            was_changed = false;
            return true;
        }
        return false;
    }
    
    public void clearInstantiatedData(){
        this.instantiated_status = false;
    }

    public Set<Vertex<T>> getConnections() {
        return this.connections;
    }
    
}
