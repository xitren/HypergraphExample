package com.gusev.controllers;

import com.gusev.fx.AgentScanner;

public class RandomController implements AgentController {
    private final static int MAX_CYCLES = 20;

    @Override
    public int getMove(AgentScanner sc) {
        int l, i = 0;
        boolean free;
        do {
            free = false;
            l = (int) ((Math.random() * 1000) % 5);
            switch (l) {
                case 1:
                    free = sc.getUpType() == 0;
                    break;
                case 2:
                    free = sc.getDownType() == 0;
                    break;
                case 3:
                    free = sc.getLeftType() == 0;
                    break;
                case 4:
                    free = sc.getRightType() == 0;
                    break;
                default:
                    free = false;
                    break;
            }
        } while ((!free) && (i++ < MAX_CYCLES));
        if (i >= MAX_CYCLES)
            return 0;
        else
            return l;
    }
}
