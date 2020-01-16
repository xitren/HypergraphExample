/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gusev.utilities;

import java.util.Comparator;
import java.util.Map;

/**
 *
 * @author gusev_a
 */
public class ValueComparator implements Comparator<Point> {
    Map<Point, Double> base;

    public ValueComparator(Map<Point, Double> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with
    // equals.
    @Override
    public int compare(Point a, Point b) {
        if (base.get(a) >= base.get(b)) {
            return 1;
        } else {
            return -1;
        } // returning 0 would merge keys
    }
}