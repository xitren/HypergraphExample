package com.gusev.robots;

import com.gusev.utilities.Point;
import com.gusev.world.WorldMap;
import org.springframework.web.bind.annotation.GetMapping;
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
    public String addAgent(@RequestParam(value = "x", defaultValue = "0") String x,
                           @RequestParam(value = "y", defaultValue = "0") String y) {
        try {
            int xp = Integer.parseInt(x);
            int yp = Integer.parseInt(y);
            Point pnt = world.getPoint(xp, yp);
            if (pnt != null) {
                Agent nn = new Agent(world, pnt);
                if (world.addAgent(nn, xp, yp)) {
                    set.add(nn);
                    return "Agent created, <ID>" + nn.getId() + "</ID>, x: " + x + ", y: " + y;
                } else {
                    return "Exception: Area is full, x: " + x + ", y: " + y;
                }
            } else {
                return "Exception: Point is null, x: " + x + ", y: " + y;
            }
        } catch (NumberFormatException ex) {
            return "Exception: " + ex + ", ID: , x: " + x + ", y: " + y;
        }
    }

    @GetMapping("/agents/delete")
    public String deleteAgent(@RequestParam(value = "id", defaultValue = "none") String id) {
        Agent nn = set.stream().filter(e->e.getId().equals(id)).findFirst().get();
        if (nn != null) {
            if (world.removeAgent(nn)) {
                set.remove(nn);
                return "Agent deleted, ID: " + nn.getId();
            } else {
                return "Agent not found, internal error, ID: " + id;
            }
        } else {
            return "Agent not found, ID: " + id;
        }
    }
}
