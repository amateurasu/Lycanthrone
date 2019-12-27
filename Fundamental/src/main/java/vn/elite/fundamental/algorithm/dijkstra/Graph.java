package vn.elite.fundamental.algorithm.dijkstra;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
public class Graph {

    private Set<Node> nodes = new HashSet<>();

    public void addNode(Node nodeA) {
        nodes.add(nodeA);
    }
}
