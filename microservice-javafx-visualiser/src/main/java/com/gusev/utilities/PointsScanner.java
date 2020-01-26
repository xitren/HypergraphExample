/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gusev.utilities;

import java.util.HashMap;

/**
 * @author gusev_a
 */
public interface PointsScanner {
    void scanNearPoints();

    HashMap<Point, Integer> getScannedPoints();
    int getUpScan();
    int getDownScan();
    int getleftScan();
    int getRightScan();
}
