/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encephalon.Hypergraph.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 *
 * @author gusev_a
 */
public class Permutations {
    
    private Permutations() {
    }
    
    public static <T> Map<T, Integer> combine(List<T> el, long[] ii) {
        if (el.size() != ii.length)
            return null;
        Map<T, Integer> map = new HashMap();
        for (int i=0;i < el.size();i++){
            map.put(el.get(i), (int)ii[i]);
        }
        return map;
    }
    
    public static List<long[]> combinations(int n) {
        if (n < 3) return null;
        List<long[]> comb = new LinkedList();
        List<Integer> st = new LinkedList();
        for (int i=0;i < n;i++)
            st.add(1);
        long[] g = new long[n];
        for (int j=0,k=0,m=0;(m < n) && (k < st.size());j++){
            int val = st.get(k);
            if (j < val)
                g[m++] = k;
            else {
                k++;
                j = -1;
            }
        }
        comb.add(g);
        for (;!((st.get(1) == 1) && (st.get(2) == 0));){
            for (int i=n-1;i > 0;i--){
                if (st.get(i) > 0){
                    st.set(i, st.get(i)-1);
                    st.set(i-1, st.get(i-1)+1);
                    g = new long[n];
                    for (int j=0,k=0,m=0;(m < n) && (k < st.size());j++){
                        int val = st.get(k);
                        if (j < val)
                            g[m++] = k;
                        else {
                            k++;
                            j = -1;
                        }
                    }
                    comb.add(g);
                }
            }
        }
        return comb;
    }
    
    public static long factorial(int n) {
        if (n > 20 || n < 0) throw new IllegalArgumentException(n + " is out of range");
        return LongStream.rangeClosed(2, n).reduce(1, (a, b) -> a * b);
    }
    
    public static <T> List<T> permutation(long no, List<T> items) {
        return permutationHelper(no,
              new LinkedList<>(Objects.requireNonNull(items)),
              new ArrayList<>());
    }
    
    private static <T> List<T> permutationHelper(long no, LinkedList<T> in, List<T> out) {
        if (in.isEmpty()) return out;
        long subFactorial = factorial(in.size() - 1);
        out.add(in.remove((int) (no / subFactorial)));
        return permutationHelper((int) (no % subFactorial), in, out);
    }
    
    @SafeVarargs
    @SuppressWarnings("varargs") // Creating a List from an array is safe
    public static <T> Stream<Stream<T>> of(T... items) {
        List<T> itemList = Arrays.asList(items);
        return LongStream.range(0, factorial(items.length))
                .mapToObj(no -> permutation(no, itemList).stream());
    }

}
