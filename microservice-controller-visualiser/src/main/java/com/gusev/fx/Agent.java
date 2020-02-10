package com.gusev.fx;

public class Agent {
    private String id;
    private int x;
    private int y;

    public Agent(String _id, int _x, int _y){
        id = _id;
        x = _x;
        y = _y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}
