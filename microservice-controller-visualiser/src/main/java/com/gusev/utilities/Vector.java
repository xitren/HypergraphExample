package com.gusev.utilities;

public class Vector {
    private double x, y;

    public Vector(double _x, double _y) {
        x = _x;
        y = _y;
    }

    public void sum(double _x, double _y) {
        x += _x;
        y += _y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return String.format("[%.6f, %.6f]", x, y);
    }
}
