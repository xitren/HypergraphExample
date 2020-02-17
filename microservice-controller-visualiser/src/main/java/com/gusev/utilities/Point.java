/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gusev.utilities;

/**
 * @author gusev_a
 */
public class Point {
    public int x, y;

    public Point(int _x, int _y) {
        x = _x;
        y = _y;
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }
}
