/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encephalon.Hypergraph.Bayesian;

/**
 *
 * @author xitre
 */
public class SpecialCounter {
    private Integer[] max_vals;
    private Integer[] vals;
    private Integer[] tmp;
    private boolean started = false;
    
    public SpecialCounter(Integer... _max_vals){
        max_vals = _max_vals.clone();
        vals = new Integer[_max_vals.length];
        tmp = new Integer[_max_vals.length];
        for (int i=0;i < _max_vals.length;i++){
            tmp[i]=0;
            vals[i]=0;
        }
    }
    
    public boolean hasNext(){
        boolean t = true;
        if (!started)
            return true;
        if (tmp.length <= 0)
            return false;
        System.arraycopy(vals, 0, tmp, 0, vals.length);
        tmp[0]++;
        switcher(tmp);
        for (int i=0;i < tmp.length;i++){
            if (tmp[i] >= max_vals[i]){
                t = false;
                break;
            }
        }
        return t;
    }
    
    private void switcher(Integer[] _vals){
        for (int i=0;i < (_vals.length-1);i++){
            if (_vals[i] >= max_vals[i]){
                _vals[i] = 0;
                _vals[i+1]++;
            }
        }
    }
    
    public Integer[] next(){
        if (!started) {
            started = true;
            return vals;
        }
        vals[0]++;
        switcher(vals);
        for (int i=0;i < vals.length;i++){
            if (vals[i] >= max_vals[i]){
                vals[i] = 0;
                vals[i+1]++;
            }
//            System.out.print(" "+vals[i]);
        }
//        System.out.println();
        return vals;
    }
}
