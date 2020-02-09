/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encephalon.Hypergraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author gusev_a
 * @param <T>
 */
public class Graph<T> {
    protected final static org.slf4j.Logger logger = 
            LoggerFactory.getLogger(Graph.class);
    
    public final Map<T,Vertex<T>> vertices = new ConcurrentHashMap();
    public final Set<Edge> edge = new HashSet();
    
    public Graph(){
    }
    
    public Graph(Graph<T> other){
        vertices.putAll(other.vertices);
        edge.addAll(other.edge);
    }
    
    public Graph(GraphFactor<T> other){
        this.vertices.putAll(other.vertices);
        other.edge.stream().forEach((f) -> {
            f.t.stream().forEach((v1) -> {
                f.t.stream()
                        .filter((v2) -> {
                            try {
                                return !this.getConnectedWay( 
                                        ((Vertex<T>)v1).getData(),
                                        ((Vertex<T>)v2).getData()
                                );
                            } catch (Exception ex) {
                                return false;
                            }
                        })
                        .forEach((v2) -> {
                            try {
                                this.addConnection(
                                        ((Vertex<T>)v1).getData(), 
                                        ((Vertex<T>)v2).getData()
                                );
                            } catch (Exception ex) {
                            }
                        });
            });
        });
        this.edge.forEach((e) -> {
            System.out.println(
                    ""+e.getFrom().getName()+" => "+e.getTo().getName()
            );
        });
    }
    
    public void addNode(T name){
        logger.debug("Adding node: " + name.toString());
        Vertex<T> n = new Vertex(name);
        vertices.put(name, n);
        logger.debug("Added.");
    }
    
    public List<Vertex> getNodesConnected(T name){
        List<Vertex> t = new LinkedList();
        Vertex v = vertices.get(name);
        edge.stream().filter((e) -> (e.isConnectedToNode(v))).forEachOrdered((e) -> {
            t.add(e.getOtherNode(v));
        });
        return t;
    }
    
    public List<Edge> getEdgesConnectedTo(T name){
        List<Edge> t = new LinkedList();
        Vertex v = vertices.get(name);
        edge.stream().filter((e) -> (e.isConnectedInNode(v))).forEachOrdered((e) -> {
            t.add(e);
        });
        return t;
    }
    
    public List<Edge> getEdgesConnectedFrom(T name){
        List<Edge> t = new LinkedList();
        Vertex v = vertices.get(name);
        edge.stream().filter((e) -> (e.isConnectedOutNode(v))).forEachOrdered((e) -> {
            t.add(e);
        });
        return t;
    }
    
    public Vertex<T> deleteNode(T name){
        logger.debug("Deleting node: " + name.toString());
        Vertex<T> n = vertices.remove(name);
        logger.debug("Deleting connections to/from node: " + n.toString());
        for (Iterator<Edge> i = edge.iterator(); i.hasNext();) {
            Edge element = i.next();
            if (element.isConnectedToNode(n)){
                logger.debug("Deleting link: " + element.toString());
                i.remove();
            }
        }
        logger.debug("Deleted.");
        return n;
    }
    
    public final void addConnection(T name_from, T name_to) throws Exception{
        logger.debug("Adding connection: from " + name_from.toString()
                    + " to " + name_to.toString());
        Edge e = new Edge(  vertices.get(name_from), 
                            vertices.get(name_to)  );
        
        logger.debug(e.toString());
        edge.forEach((i) -> {
            System.out.println("link "+i.getFrom().getName()+" ; "+i.getTo().getName());
        });
        if (!this.isConnected(vertices.get(name_from),vertices.get(name_to)))
            edge.add(e);
        edge.forEach((i) -> {
            System.out.println("link "+i.getFrom().getName()+" ; "+i.getTo().getName());
        });
        logger.debug("Added.");
    }
    
    public void addConnection(T name_from, 
            T name_to1, T name_to2) throws Exception{
        logger.debug("Adding connection: from " + name_from.toString()
                    + " to " + name_to1.toString());
        System.out.println("Adding connection: from " + name_from.toString()
                    + " to " + name_to1.toString());
        Edge e = new Edge(  vertices.get(name_from), vertices.get(name_to1)  );
        logger.debug(e.toString());
        edge.forEach((i) -> {
            System.out.println(
                    "link "+i.getFrom().getName()+" ; "+i.getTo().getName()
            );
        });
        if (!this.isConnected(vertices.get(name_from),vertices.get(name_to1)))
            edge.add(e);
        edge.forEach((i) -> {
            System.out.println(
                    "link "+i.getFrom().getName()+" ; "+i.getTo().getName()
            );
        });
        logger.debug("Adding connection: from " + name_from.toString()
                    + " to " + name_to2.toString());
        System.out.println("Adding connection: from " + name_from.toString()
                    + " to " + name_to2.toString());
        e = new Edge(  vertices.get(name_from), vertices.get(name_to2)  );
        logger.debug(e.toString());
        edge.forEach((i) -> {
            System.out.println(
                    "link "+i.getFrom().getName()+" ; "+i.getTo().getName()
            );
        });
        if (!this.isConnected(vertices.get(name_from),vertices.get(name_to2)))
            edge.add(e);
        edge.forEach((i) -> {
            System.out.println(
                    "link "+i.getFrom().getName()+" ; "+i.getTo().getName()
            );
        });
        logger.debug("Added.");
    }
    
    public Vertex<T> deleteConnectionTo(T name){
        logger.debug("Deleting connections to node: " + name.toString());
        Vertex<T> n = vertices.get(name);
        if ( (n == null) )
            return null;
        for (Iterator<Edge> i = edge.iterator(); i.hasNext();) {
            Edge element = i.next();
            if (element.isConnectedInNode(n)){
//                element.getTo().deleteConnection(element.getFrom());
                logger.debug("Deleting link: " + element.toString());
                i.remove();
            }
        }
        logger.debug("Deleted.");
        return n;
    }
    
    public Edge deleteConnection(T name1, T name2){
        logger.debug("Deleting connections to nodes: " + name1.toString() +
                " <-> " + name2.toString());
        Vertex<T> n1 = vertices.get(name1);
        Vertex<T> n2 = vertices.get(name2);
        if ( (n1 == null) || (n2 == null) )
            return null;
        Edge element = null;
        for (Iterator<Edge> i = edge.iterator(); i.hasNext();) {
            element = i.next();
            if (    element.isConnectedInNode(n1) 
                 && element.isConnectedOutNode(n2) ){
//                element.getTo().deleteConnection(element.getFrom());
                logger.debug("Deleting link: " + element.toString());
                i.remove();
            }
        }
        logger.debug("Deleted.");
        return element;
    }
    
    public Vertex<T> deleteConnectionFrom(T name){
        logger.debug("Deleting connections from node: " + name.toString());
        Vertex<T> n = vertices.get(name);
        if ( (n == null) )
            return null;
        for (Iterator<Edge> i = edge.iterator(); i.hasNext();) {
            Edge element = i.next();
            if (element.isConnectedOutNode(n)){
                logger.debug("Deleting link: " + element.toString());
                i.remove();
            }
        }
        logger.debug("Deleted.");
        return n;
    }
    
    public GraphView<T> buildGraphViewFactory(){
        return new GraphView(vertices,this);
    }
    
    public boolean vertexNotInAnyCycle(T name) throws Exception {
        if (!this.vertices.containsKey(name))
            throw new Exception("No vertex");
        Vertex<T> l = vertices.get(name);
        for (Vertex<T>[] v1 : cycles) {
            for (Vertex<T> vv : v1) {
                if (vv.equals(l))
                    return false;
            }
        }
        return true;
    }
    
    public boolean vertexNotInAnyCycle(Vertex<T> v) throws Exception {
        if (!this.vertices.containsValue(v))
            throw new Exception("No vertex");
        for (Vertex<T>[] v1 : cycles) {
            for (Vertex<T> vv : v1) {
                if (vv.equals(v))
                    return false;
            }
        }
        return true;
    }
    
    public boolean isFullyconnected(Vertex<T>... V){
//        System.out.println("=================================");
        for (Vertex<T> v1 : V){
            for (Vertex<T> v2 : V){
                if (!v1.equals(v2)){
//                    System.out.println("V1: "+v1.name+" V2: "+v2.name+" is "+this.isConnected(v1, v2));
                    if (!this.isConnected(v1, v2)){
//                        System.out.println("<<<<<<<<<<<");
                        return false;
                    }
                }
            }
        }
//        System.out.println("=================================");
        return true;
    }
    
    public boolean isVertexLeaf(Vertex<T> v){
        boolean b = edge.stream().noneMatch((e) -> (e.isConnectedInNode(v)));
        v.leaf = b;
        return b;
    }
    
    public int getLeafsCount(){
        int ret = 0;
        ret = vertices.entrySet().stream()
                .filter((pair) -> (isVertexLeaf(pair.getValue())))
                .map((_item) -> 1).reduce(ret, Integer::sum);
        return ret;
    }
    
    public boolean checkCyclic(){
        Map<Vertex<T>,Integer> map = new HashMap();
        List<Vertex<T>> cycle = new LinkedList();
        boolean set = false;
        for (Map.Entry<T,Vertex<T>> pair : vertices.entrySet()) {
            map.put(pair.getValue(), 0);
        }
        for (Map.Entry<T,Vertex<T>> pair : vertices.entrySet()) {
            if (map.get(pair.getValue()) == 2)
                continue;
            if (checkCyclic(pair.getKey(),map,cycle)){
                set = true;
                break;
            }
        }        
        return set;
    }
    
    public List<Vertex[]> cycles = new LinkedList<>();
    
    public boolean truangulateCycles(){
        for (int ic=0;ic < cycles.size();ic++) {
            Vertex<T>[] v1 = cycles.get(ic);
//        for (Vertex<T>[] v1 : cycles) {
            if (v1.length <= 3)
                continue;
            List<Vertex<T>> other = new LinkedList();
            for (Vertex<T> vv : v1) 
                other.add(vv);
            //check cycles
            System.out.println("/////////////////////////////");
            for (Vertex<T> vv : other) {
                System.out.print(" "+vv.name);
            }
            System.out.println("");
            for (Vertex<T>[] v2 : cycles) {
                for (Vertex<T> vv : v2) {
                    System.out.print(" - "+vv.name);
                }
                System.out.println("");
                if ( Graph.includedCycle(v1,v2) ) {
                    for (Vertex<T> vv : v2) {
                        other.remove(vv);
                    }
                }
                if (other.isEmpty())
                    break;
                for (Vertex<T> vv : other) {
                    System.out.print(" "+vv.name);
                }
                System.out.println("");
            }
            System.out.println("============================");
            System.out.print("Vertices ");
            for (Vertex<T> vv : other) {
                System.out.print(" "+vv.name);
            }
            System.out.println("");
            System.out.println("============================");
            if (other.size() > 0){
                if (other.size() == v1.length) {
                    other.remove(other.size()-1);
                    other.remove(0);
                    other.remove(0);
                }
                for (Vertex<T> vv : other) {
                    try {
                        this.addConnection((T)v1[0].getData(), (T)vv.getData());
//                        return true;
                    } catch (Exception ex) {
                        Logger.getLogger(
                                Graph.class.getName()
                        ).log(Level.SEVERE, null, ex);
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    public static boolean includedCycle(Vertex[] outer, Vertex[] inner){
        if (inner.length >= outer.length)
            return false;
        for (Vertex v1 : inner) {
            boolean same = false;
            for (Vertex v2 : outer) {
                if (v1.equals(v2)) {
                    same = true;
                    break;
                }
            }
            if (!same)
                return same;
        }
        return true;
    }
    
    public static boolean sameCycles(Vertex[] a, Vertex[] b){
        if (a.length != b.length)
            return false;
        for (Vertex v1 : a) {
            boolean same = false;
            for (Vertex v2 : b) {
                if (v1.equals(v2)) {
                    same = true;
                    break;
                }
            }
            if (!same)
                return same;
        }
        return true;
    }
    
    public void reduceCyclic(){
        Map<Vertex<T>,Integer> map = new HashMap();
        List<Vertex<T>> cycle = new LinkedList();
        boolean set = true;
        while (set){
            cycles.clear();
            for (Map.Entry<T,Vertex<T>> pair : vertices.entrySet()) {
                map.put(pair.getValue(), 0);
            }
            for (Map.Entry<T,Vertex<T>> pair : vertices.entrySet()) {
                if (map.get(pair.getValue()) == 2)
                    continue;
                checkCyclic4(pair.getKey(),map,cycle);
            } 
            set = truangulateCycles();
        }
        System.out.println();
        for (Vertex[] v : cycles) {
            for (Vertex vv : v) 
                System.out.print(" -> "+vv.getData().toString());
            System.out.println();
        }
    }
    
    public void buildTree(){
        Map<Vertex<T>,Integer> map = new HashMap();
        List<Vertex<T>> cycle = new LinkedList();
        boolean set = true;
        while (set){
            cycles.clear();
            for (Map.Entry<T,Vertex<T>> pair : vertices.entrySet()) {
                map.put(pair.getValue(), 0);
            }
            for (Map.Entry<T,Vertex<T>> pair : vertices.entrySet()) {
                if (map.get(pair.getValue()) == 2)
                    continue;
                checkCyclic4(pair.getKey(),map,cycle);
            } 
            set = false;
            try {
                set = removeLighter();
            } catch (Exception ex) {
                Logger.getLogger(
                        Graph.class.getName()
                ).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println();
        for (Vertex[] v : cycles) {
            for (Vertex vv : v) 
                System.out.print(" -> "+vv.getData().toString());
            System.out.println();
        }
    }
    
    public boolean isInCycle(Edge e) {
        System.out.println(e.toString());
        for (Vertex[] V : cycles) {
            for (Vertex v : V) 
                System.out.print(" -> "+v.getData().toString());
            System.out.println();
        }
        for (Vertex[] v1 : cycles) {
            if (v1.length <= 2){
                continue;
            }
            for (int i=1;i < v1.length;i++) {
                if ( ((e.getTo().equals(v1[i-1])) 
                        && (e.getFrom().equals(v1[i])))
                    || ((e.getFrom().equals(v1[i-1])) 
                        && (e.getTo().equals(v1[i]))) ) {
                    return true;
                }
            }
            if ( ((e.getTo().equals(v1[v1.length-1])) 
                    && (e.getFrom().equals(v1[0])))
                || ((e.getFrom().equals(v1[v1.length-1])) 
                    && (e.getTo().equals(v1[0]))) ) {
                return true;
            }
        }
        return false;
    }
    
    public boolean removeLighter(){
        double min = Integer.MAX_VALUE;
        Edge e_to_del = null;
        System.out.println("///////remove lighter");
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
        System.out.println("///////remove lighter");
        if (e_to_del != null){
            System.out.println(e_to_del.toString());
            System.out.println("//////////////////////////");
            edge.remove(e_to_del);
            return true;
        }
        System.out.println("//////////////////////////");
        return false;
    }
    
    private boolean checkCyclic4(T data, Map<Vertex<T>,Integer> map, List<Vertex<T>> cycle){
        boolean set = false;
        Vertex<T> gr = vertices.get(data);
        map.put(gr, 1);
        cycle.add(gr);
//        for (Vertex v : cycle)
//            System.out.print(" -> "+v.getData().toString());
//        System.out.println();
        int ir = cycle.size() - 1;
        for (Vertex e : getNodesConnected(data)){
            if (e.getData().equals(data))
                continue;
            if (cycle.size() > 1)
                if (e.equals(cycle.get(cycle.size()-2)))
                    continue;
            if (map.get(e) == 1){
                set = true;
                List<Vertex> cycle_copy = new LinkedList(cycle);
                int count_del = -1;
                for (int i=cycle_copy.size()-1;i >= 0;i--){
                    if (cycle_copy.get(i).equals(e)){
                        count_del = i;
                        break;
                    }
                }
                for (int i=0;i < count_del;i++){
                    cycle_copy.remove(0);
                }
                Vertex[] new_cycle = new Vertex[cycle_copy.size()];
                for (int i=0;i < cycle_copy.size();i++){
                    new_cycle[i] = cycle_copy.get(i);
                }
                boolean exists = false;
                for (Vertex[] v : cycles) {
                    if (sameCycles(v,new_cycle)){
                        exists = true;
                        break;
                    }
                }
                if (!exists)
                    cycles.add(new_cycle);
//                System.out.println(" -X ");
                break;
            }
            if (map.get(e) == 2){
                continue;
            }
            checkCyclic4((T)e.getData(),map,cycle);
        }
        cycle.remove(ir);
        map.put(gr, 0);
        return set;
    }
    
    private boolean checkCyclic(T data, Map<Vertex<T>,Integer> map, List<Vertex<T>> cycle){
        boolean set = false;
        Vertex<T> gr = vertices.get(data);
        map.put(gr, 1);
        cycle.add(gr);
        int ir = cycle.size() - 1;
        for (Edge e : getEdgesConnectedFrom(data)){
            if (map.get(e.getTo()) == 1){
                set = true;
                break;
            }
            if (map.get(e.getTo()) == 2){
                continue;
            }
            if (checkCyclic((T)e.getTo().getData(),map,cycle)){
                if (cycle.size() == 3) {
                    cycle.remove(ir);
                }
                set = true;
                break;
            }
        }
        cycle.remove(ir);
        map.put(gr, 2);
        return set;
    }
    
    public final boolean getConnectedWay(T name_from, T name_to) throws Exception {
        if (name_from.equals(name_to))
            return true;
        Vertex v1 = vertices.get(name_from);
        Vertex v2 = vertices.get(name_to);
        if ( (v1 == null) || (v2 == null) )
            throw new Exception("No verticals");
        for (Edge i : edge){
            if (i.isConnected(v1, v2) || i.isConnected(v2, v1)){
                return true;
            }
        }
        return false;
    }
    
    public Edge getConnection(T name_from, T name_to) throws Exception {
        Vertex v1 = vertices.get(name_from);
        Vertex v2 = vertices.get(name_to);
        if ( (v1 == null) || (v2 == null) )
            throw new Exception("No verticals");
        for (Edge i : edge){
            if (i.isConnected(v1, v2)){
                return i;
            }
        }
        throw new Exception("No connection");
    }
    
    public Edge getConnectionUndir(T name_from, T name_to) throws Exception {
        Vertex v1 = vertices.get(name_from);
        Vertex v2 = vertices.get(name_to);
        if ( (v1 == null) || (v2 == null) )
            throw new Exception("No verticals");
        for (Edge i : edge){
            if (i.isConnectedUndir(v1, v2)){
                return i;
            }
        }
        throw new Exception("No connection");
    }
    
    public Edge getConnection(Vertex<T> name_from, Vertex<T> name_to) throws Exception {
        for (Edge i : edge){
            if (i.isConnected(name_from, name_to)){
                return i;
            }
        }
        throw new Exception("No connection");
    }
    
    public Edge getConnectionUndir(Vertex<T> name_from, Vertex<T> name_to) throws Exception {
        for (Edge i : edge){
            if (i.isConnectedUndir(name_from, name_to)){
                return i;
            }
        }
        throw new Exception("No connection");
    }
    
    public boolean isConnected(Vertex<T> name_from, Vertex<T> name_to) {
        for (Edge i : edge){
            if (i.isConnectedUndir(name_from, name_to))
                return true;
        }
        return false;
    }
    
    public boolean isConnectedToAny(Vertex<T> name_from, Vertex<T>[] V) {
        if ((name_from == null) || (V != null))
            return false;
        if (V.length < 2)
            return false;
        for (Edge i : edge){
            for (Vertex v_other : V)
                if (i.isConnectedUndir(name_from, v_other))
                    return true;
        }
        return false;
    }
    
    public Edge getConnectionToAny(Vertex<T> name_from, Vertex<T>[] V) {
        if ((name_from == null) || (V == null))
            return null;
        if (V.length < 2)
            return null;
        for (Edge i : edge){
            for (Vertex<T> v_other : V)
                if (i.isConnectedUndir(name_from, v_other))
                    return i;
        }
        return null;
    }
    
    @Override
    public String toString(){
        String str = "";
        str = str.concat("=====GRAPH==============================\n");
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
    
}
