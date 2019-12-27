package vn.elite.fundamental.algorithm.dijkstra;

import lombok.Data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@Data
public class Node {

    private String name;

    private LinkedList<Node> shortestPath = new LinkedList<>();

    private Integer distance = Integer.MAX_VALUE;

    private Map<Node, Integer> adjacentNodes = new HashMap<>();

    public Node(String name) {
        this.name = name;
    }

    public void addDestination(Node destination, int distance) {
        adjacentNodes.put(destination, distance);
    }
}
