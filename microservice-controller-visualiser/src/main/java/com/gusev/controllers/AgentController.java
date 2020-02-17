package com.gusev.controllers;

import com.gusev.fx.Agent;
import com.gusev.fx.AgentScanner;

public interface AgentController {
    int getMove(Agent ag, AgentScanner sc);
}
