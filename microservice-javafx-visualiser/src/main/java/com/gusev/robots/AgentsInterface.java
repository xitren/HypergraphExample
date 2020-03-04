package com.gusev.robots;

import com.gusev.utilities.Point;
import com.gusev.world.WorldMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@RestController
public class AgentsInterface {
    private static WorldMap world;
    private static final Set<Agent> set = new HashSet();

    public static void setWorld(WorldMap _world){
        world = _world;
    }

    @GetMapping("/agents/add")
    public String addAgent(@RequestParam(value = "x", required = true) String x,
                           @RequestParam(value = "y", required = true) String y) {
        try {
            int xp = Integer.parseInt(x);
            int yp = Integer.parseInt(y);
            if (world.isTileAccessible(xp, yp)) {
                Point pnt = world.getPoint(xp, yp);
                if (pnt != null) {
                    Agent nn = new Agent(world, pnt);
                    if (world.addAgent(nn, xp, yp)) {
                        set.add(nn);
                        return "" + nn.getId();
                    } else {
                        return "error";
                    }
                } else {
                    return "error";
                }
            } else {
                return "error";
            }
        } catch (NumberFormatException ex) {
            return "error";
        }
    }

    @GetMapping("/agents/delete")
    public String deleteAgent(@RequestParam(value = "id", required = true) String id) {
        Agent nn = set.stream().filter(e->e.getId().equals(id)).findFirst().get();
        if (nn != null) {
            if (world.removeAgent(nn)) {
                set.remove(nn);
                return "Agent deleted, ID: " + nn.getId();
            } else {
                return "error";
            }
        } else {
            return "error";
        }
    }

    @GetMapping("/agents/clear")
    public String clearAgents() {
        world.clearAgents();
        set.clear();
        return "ok";
    }

    @GetMapping("/agents/{id}/move/up")
    public String moveupAgent(@PathVariable String id) {
        Agent nn = set.stream().filter(e->e.getId().equals(id)).findFirst().get();
        if (nn != null) {
            nn.moveUp();
            return "Agent , ID: " + id + ", x: " + nn.getPoint().x + ", y: " + nn.getPoint().y;
        } else {
            return "error";
        }
    }

    @GetMapping("/agents/{id}/move/down")
    public String movedownAgent(@PathVariable String id) {
        Agent nn = set.stream().filter(e->e.getId().equals(id)).findFirst().get();
        if (nn != null) {
            nn.moveDown();
            return "Agent , ID: " + id + ", x: " + nn.getPoint().x + ", y: " + nn.getPoint().y;
        } else {
            return "error";
        }
    }

    @GetMapping("/agents/{id}/move/left")
    public String moveleftAgent(@PathVariable String id) {
        Agent nn = set.stream().filter(e->e.getId().equals(id)).findFirst().get();
        if (nn != null) {
            nn.moveLeft();
            return "Agent , ID: " + id + ", x: " + nn.getPoint().x + ", y: " + nn.getPoint().y;
        } else {
            return "error";
        }
    }

    @GetMapping("/agents/{id}/move/right")
    public String moverightAgent(@PathVariable String id) {
        Agent nn = set.stream().filter(e->e.getId().equals(id)).findFirst().get();
        if (nn != null) {
            nn.moveRight();
            return "Agent , ID: " + id + ", x: " + nn.getPoint().x + ", y: " + nn.getPoint().y;
        } else {
            return "error";
        }
    }

    @GetMapping("/agents/{id}/scan")
    public String scanAgent(@PathVariable String id) {
        try {
            Agent nn = set.stream().filter(e -> e.getId().equals(id)).findFirst().get();
            if (nn != null) {
                return "<UP>" + nn.getUpScan() + "</UP>\n"
                        + "<DOWN>" + nn.getDownScan() + "</DOWN>\n"
                        + "<LEFT>" + nn.getleftScan() + "</LEFT>\n"
                        + "<RIGHT>" + nn.getRightScan() + "</RIGHT>\n";
            } else {
                return "error";
            }
        } catch (java.util.NoSuchElementException ex) {
            return "error";
        }
    }

    @GetMapping("/agents/{id}/scan/up")
    public String scanupAgent(@PathVariable String id) {
        Agent nn = set.stream().filter(e->e.getId().equals(id)).findFirst().get();
        if (nn != null) {
            return "" + nn.getUpScan();
        } else {
            return "error";
        }
    }

    @GetMapping("/agents/{id}/scan/down")
    public String scandownAgent(@PathVariable String id) {
        Agent nn = set.stream().filter(e->e.getId().equals(id)).findFirst().get();
        if (nn != null) {
            return "" + nn.getDownScan();
        } else {
            return "error";
        }
    }

    @GetMapping("/agents/{id}/scan/left")
    public String scanleftAgent(@PathVariable String id) {
        Agent nn = set.stream().filter(e->e.getId().equals(id)).findFirst().get();
        if (nn != null) {
            return "" + nn.getleftScan();
        } else {
            return "error";
        }
    }

    @GetMapping("/agents/{id}/scan/right")
    public String scanrightAgent(@PathVariable String id) {
        Agent nn = set.stream().filter(e->e.getId().equals(id)).findFirst().get();
        if (nn != null) {
            return "" + nn.getRightScan();
        } else {
            return "error";
        }
    }

    @GetMapping("/agents/list")
    public String listAgent() {
        String ret = "";
        int i = 0;
        for (Agent ag : set) {
            ret = ret.concat(ag.toString(i++) + "\n");
        }
        return ret;
    }

    @GetMapping("/world/mark")
    public String markCalc() {
        return "" + world.getMark();
    }
}
