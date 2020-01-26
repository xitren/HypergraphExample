/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gusev.utilities;

/**
 * @author gusev_a
 */
public interface MovableObject {

    Point getPoint();

    void move(Point _point);

    void moveUp();
    void moveDown();
    void moveLeft();
    void moveRight();

}
