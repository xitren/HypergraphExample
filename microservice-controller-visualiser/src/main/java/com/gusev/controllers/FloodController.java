package com.gusev.controllers;

import com.gusev.fx.AgentScanner;

public class FloodController implements AgentController {
    @Override
    public int getMove(AgentScanner sc) {
        int l;
        if (sc.getUpType() == 0)
            return 1;
        if (sc.getRightType() == 0)
            return 4;
        if (sc.getDownType() == 0)
            return 2;
        if (sc.getLeftType() == 0)
            return 3;
        return 0;
    }
}
