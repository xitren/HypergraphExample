package com.gusev.controllers;

import com.gusev.fx.Agent;
import com.gusev.fx.AgentScanner;
import com.gusev.utilities.Point;
import java.util.HashMap;
import java.util.Map;

public class FloodController implements AgentController {
    private final Map<String, Point> map = new HashMap();

    static private boolean isPreviousUp(Point p1, Point p2){
        if (p1 != null && p2 != null){
            if ((p1.x == (p2.x)) && (p1.y == (p2.y - 1)))
                return true;
            return false;
        } else
            return false;
    }

    static private boolean isPreviousDown(Point p1, Point p2){
        if (p1 != null && p2 != null){
            if ((p1.x == (p2.x)) && (p1.y == (p2.y + 1)))
                return true;
            return false;
        } else
            return false;
    }

    static private boolean isPreviousLeft(Point p1, Point p2){
        if (p1 != null && p2 != null){
            if ((p1.x == (p2.x - 1)) && (p1.y == p2.y))
                return true;
            return false;
        } else
            return false;
    }

    static private boolean isPreviousRight(Point p1, Point p2){
        if (p1 != null && p2 != null){
            if ((p1.x == (p2.x + 1)) && (p1.y == p2.y))
                return true;
            return false;
        } else
            return false;
    }

    static private boolean isPreviousPnt(Point p1, Point p2){
        if (p1 != null && p2 != null){
            if ((p1.x == (p2.x - 1)) && (p1.y == p2.y))
                return true;
            if ((p1.x == (p2.x + 1)) && (p1.y == p2.y))
                return true;
            if ((p1.x == (p2.x)) && (p1.y == (p2.y - 1)))
                return true;
            if ((p1.x == (p2.x)) && (p1.y == (p2.y + 1)))
                return true;
            return false;
        } else
            return false;
    }

    @Override
    public int getMove(Agent ag, AgentScanner sc) {
        int l;
        Point pt_pre = map.get(ag.getId());
        Point pt_aut = new Point(ag.getX(), ag.getY());
        map.put(ag.getId(), pt_aut);
        if (sc.getUpType() == 0 && !isPreviousUp(pt_pre, pt_aut))
            return 1;
        if (sc.getRightType() == 0 && !isPreviousRight(pt_pre, pt_aut))
            return 4;
        if (sc.getDownType() == 0 && !isPreviousDown(pt_pre, pt_aut))
            return 2;
        if (sc.getLeftType() == 0 && !isPreviousLeft(pt_pre, pt_aut))
            return 3;
        return 0;
    }
}
