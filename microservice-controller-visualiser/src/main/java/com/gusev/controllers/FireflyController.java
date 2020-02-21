package com.gusev.controllers;

import com.gusev.fx.Agent;
import com.gusev.fx.AgentScanner;
import com.gusev.utilities.Point;
import com.gusev.utilities.Vector;
import com.gusev.world.WorldMap;
import com.gusev.world.WorldTile;

import java.util.HashMap;
import java.util.Map;

public class FireflyController implements AgentController {
    private final WorldMap map = new WorldMap(25);
    private final Map<String, Point> collegues = new HashMap();

    @Override
    public int getMove(Agent ag, AgentScanner sc) {
        Point pt_aut = new Point(ag.getX(), ag.getY());
        collegues.put(ag.getId(), pt_aut);
        map.locate(ag, sc);
        Vector vect = map.getVector(ag.getX(), ag.getY());
        collegues.entrySet().stream().forEach((s) -> {
            if (ag.getId().equals(s.getKey()))
                return;
            double nn_x = s.getValue().x;
            double nn_y = s.getValue().y;
            double x_n = nn_x - ag.getX();
            double y_n = nn_y - ag.getY();
            double k = ((double)WorldTile.FULL_KNOWN_IN_STEPS)
                    / Math.sqrt(Math.pow(ag.getX() - nn_x, 2) + Math.pow(ag.getY() - nn_y, 2));
            vect.sum(-x_n * k, -y_n * k);
        });
        if (vect.getX() > 0 && sc.getRightType() > 0)
            vect.sum(-1.5 * vect.getX(), 0);
        if (vect.getX() <= 0 && sc.getLeftType() > 0)
            vect.sum(-1.5 * vect.getX(), 0);
        if (vect.getY() > 0 && sc.getDownType() > 0)
            vect.sum(0, -1.5 * vect.getY());
        if (vect.getY() > 0 && sc.getUpType() > 0)
            vect.sum(0, -1.5 * vect.getY());
        if (Math.abs(vect.getX()) > Math.abs(vect.getY())) {
            if (vect.getX() < 0)
                return 3;
            else
                return 4;
        } else {
            if (vect.getY() < 0)
                return 1;
            else
                return 2;
        }
    }
}
