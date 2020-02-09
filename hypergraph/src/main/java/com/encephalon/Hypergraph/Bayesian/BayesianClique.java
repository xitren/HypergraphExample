/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encephalon.Hypergraph.Bayesian;

import com.encephalon.Hypergraph.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author xitre
 * @param <T>
 */
public class BayesianClique<T> extends Clique<T> {
    public List<Potential> potentials = new LinkedList();
    public Map<BayesianClique<T>,Potential[]> messages = new HashMap();
    
    public BayesianClique(Clique<T> graph, List<Potential> bn) throws Exception {
        super(graph);
        bn.stream().filter((item) -> {return potentialInsideClique(item);})
                    .forEach((item) -> {
                        bn.remove(item);
                        potentials.add(item);
                    });
    }
    
    public BayesianClique(List<Potential> bn, Vertex... V) {
        super(V);
        List<Potential> rem = new LinkedList();
        bn.stream().filter((item) -> {
            return potentialInsideClique(item);
        })
        .forEach((item) -> {
            potentials.add(item);
            rem.add(item);
        });
        rem.stream().forEach((item) -> {
            bn.remove(item);
        });
    }
    
    public final boolean potentialInsideClique(Potential p) {
        boolean t = false;
        for (String s : p.names) {
            for (Vertex<T> v : this.tri_vect) {
                if (t = s.equals(v.getData().toString()))
                    break;
            }
            if (!t)
                return t;
        }
        return true;
    }
    
    public void clearEvidence() {
        messages.entrySet().stream().forEach((item) -> {
            item.getValue()[0] = null;
            item.getValue()[1] = null;
        });
    }
    
    public void calculateSelf(Map verts) {
        System.out.println(getPrep()+"calculateSelf "+this.getName()+":");
        prep++;
        // =====================================================================
        // -1- Multiplying self containing potentials --------------------------
        Potential ret_p = new Potential();
        System.out.println(getPrep()+"potentials:");
        for (Potential sp : potentials) {
            try {
                ret_p = Potential.multiply(ret_p,sp);
            } catch (Exception ex) {
                Logger.getLogger(
                        BayesianClique.class.getName()
                ).log(Level.SEVERE, null, ex);
            }
        }
        // =====================================================================
        // -2- Multiplying messages ----------------------------------
        for (Map.Entry<BayesianClique<T>,Potential[]> m_c : messages.entrySet()) {
            for (Potential p_m : m_c.getValue()) {
		ret_p = Potential.multiply(ret_p,p_m);
            }
        }
        System.out.println(ret_p.toString());
        for (int i=0;i < ret_p.names.length;i++){
	    Potential my = ret_p.getPAlongDimension(ret_p.names[i]);
	    my.normalize();
	    if (verts.containsKey(ret_p.names[i])) {
		Potential p = ((Potential)verts.get(ret_p.names[i]));
		p = Potential.multiply(p, my);
		p.normalize();
		verts.put(ret_p.names[i], p);
	    } else {
		verts.put(ret_p.names[i], my);
	    }
        }
        System.out.println(ret_p.eachToString());
        // =====================================================================
        prep--;
    }
    
    public void distributeEvidence(Potential p,BayesianClique<T> from) {
        System.out.println(getPrep()+"distributeEvidence "+this.getName()+":");
        prep++;
        // -1- Adding input message --------------------------------------------
        if ((p == null) && (from == null)) {
            messages.get(from)[0] = p;
        }
        // =====================================================================
        // -2- Multiplying self containing potentials --------------------------
        Potential ret_p = new Potential();
        System.out.println(getPrep()+"potentials:");
        for (Potential sp : potentials) {
            try {
                ret_p = Potential.multiply(ret_p,sp);
            } catch (Exception ex) {
                Logger.getLogger(
                        BayesianClique.class.getName()
                ).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println(ret_p.toString());
        System.out.println(ret_p.eachToString());
        // =====================================================================
        // -3- Multiplying outcoming messages ----------------------------------
        for (Map.Entry<BayesianClique<T>,Potential[]> m_c : messages.entrySet()) {
            BayesianClique<T> c = m_c.getKey();
            Potential ret_m = new Potential();
            for (Map.Entry<BayesianClique<T>,Potential[]> m : messages.entrySet()) {
                if (m.getKey().equals(c))
                    continue;
                try {
                    ret_m = Potential.multiply(ret_m,m.getValue()[0]);
                } catch (Exception ex) {
                    Logger.getLogger(
                            BayesianClique.class.getName()
                    ).log(Level.SEVERE, null, ex);
                }
            }
            ret_m = Potential.multiply(ret_m,ret_p);
            // -4- Reduce to dimensions that used in separator -----------------
            Vertex<T>[] inter = Clique.getNotInterconnection(this, c);
            for (Vertex<T> i : inter){
                try {
                    ret_m = ret_m.sumAlongDimension(i.getData().toString());
                } catch (Exception ex) {
                    Logger.getLogger(
                            BayesianClique.class.getName()
                    ).log(Level.SEVERE, null, ex);
                }
            }
            // =================================================================
            m_c.getValue()[1] = ret_m;
            ret_m.normalize();
            if (!c.equals(from))
                c.distributeEvidence(ret_m,this);
        }
        // =====================================================================
        prep--;
    }
    
    static int prep = 0;
    public String getPrep(){
        String ret = "";
        for (int i=0;i < prep;i++)
            ret = ret.concat("    ");
        return ret;
    }
    
    public Potential collectEvidence(BayesianClique<T> c) {
        System.out.println(getPrep()+"collectEvidence "+this.getName()+":");
        prep++;
        // -1- Recurrent calls -------------------------------------------------
        if (c != null){
            messages.entrySet().stream().filter(
                    (item) -> (!item.getKey().equals(c))
            ).forEach((item) -> {
                System.out.println(getPrep()+item.getKey().getName());
                item.getValue()[0] = 
                        item.getKey().collectEvidence(this);
            });
        } else {
            messages.entrySet().stream().forEach((item) -> {
                System.out.println(getPrep()+item.getKey());
                item.getValue()[0] = 
                        item.getKey().collectEvidence(this);
            });
        }
        // =====================================================================
        // -2- Multiplying incoming messages -----------------------------------
        Potential ret_m = new Potential();
        for (Map.Entry<BayesianClique<T>,Potential[]> m : messages.entrySet()) {
            try {
                ret_m = Potential.multiply(ret_m,m.getValue()[0]);
            } catch (Exception ex) {
                Logger.getLogger(
                        BayesianClique.class.getName()
                ).log(Level.SEVERE, null, ex);
            }
        }
        // =====================================================================
        // -3- Multiplying self containing potentials --------------------------
        Potential ret_p = new Potential();
        System.out.println(getPrep()+"potentials:");
        for (Potential p : potentials) {
            try {
                ret_p = Potential.multiply(ret_p,p);
            } catch (Exception ex) {
                Logger.getLogger(
                        BayesianClique.class.getName()
                ).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println(getPrep()+"=============Pre==============");
        System.out.println(ret_p.toString());
        System.out.println(ret_m.toString());
        ret_p = Potential.multiply(ret_p,ret_m);
        // =====================================================================
        // -4- Reduce to dimensions that used in separator ---------------------
        System.out.println(getPrep()+"=============After============");
        if (c != null) {
            Vertex<T>[] inter = Clique.getNotInterconnection(this, c);
            for (Vertex<T> i : inter){
                try {
                    ret_p = ret_p.sumAlongDimension(i.getData().toString());
                } catch (Exception ex) {
                    Logger.getLogger(
                            BayesianClique.class.getName()
                    ).log(Level.SEVERE, null, ex);
                }
            }
        }
        System.out.println(ret_p.toString());
        System.out.println(getPrep()+"=============");
        // =====================================================================
        ret_p.normalize();
        prep--;
        return ret_p;
    }
    
    public int addConnection(BayesianClique<T> data) {
        this.messages.put((BayesianClique)data, new Potential[2]);
        return 0;
    }
    
    public void deleteConnection(BayesianClique<T> data) {
        this.messages.remove((BayesianClique)data);
    }
}
