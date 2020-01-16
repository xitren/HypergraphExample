/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encephalon.Hypergraph;

import org.slf4j.LoggerFactory;

/**
 *
 * @author xitre
 * @param <T>
 */
public class Vertex<T> {
    private final static org.slf4j.Logger logger = 
            LoggerFactory.getLogger(Vertex.class);
    
    public boolean leaf = false;
    private int type;
    protected T data;
    protected String name;

    public Vertex(T data) {
        this(data,0,data.toString());
    }

    public Vertex(Vertex<T> v) {
        this.data = v.data;
        this.type = v.type;
        this.name = v.name;
    }

    public Vertex(T data, int type, String name) {
        this.data = data;
        this.type = type;
        this.name = name;
    }
    
    private static void print_array(double[] status){
        System.out.print("arr");
        for (int i=0;i < status.length;i++){
            System.out.print(" "+status[i]);
        }
        System.out.println("");
    }
    private static double get_array_max(double[] vals){
        double max = vals[0];
        for (int i=1;i < vals.length;i++){
            if (max < vals[i])
                max = vals[i];
        }
        return max;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String toString(){
        String str = "";
        str = str.concat(this.data.toString());
        return str;
    }
}
