package com.gusev.controllers;

import com.gusev.fx.Agent;
import com.gusev.fx.AgentScanner;
import com.gusev.utilities.Point;
import com.gusev.world.WorldMap;

import java.util.HashMap;
import java.util.Map;

public class FireflyController implements AgentController {
    private final WorldMap map = new WorldMap(25);
    private final Map<String, Point> collegues = new HashMap();

    @Override
    public int getMove(Agent ag, AgentScanner sc) {
        Point pt_aut = new Point(ag.getX(), ag.getY());
        collegues.put(ag.getId(), pt_aut);
        return 0;
    }
}
