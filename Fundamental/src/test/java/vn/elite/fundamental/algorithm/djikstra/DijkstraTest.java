package vn.elite.fundamental.algorithm.djikstra;

import org.junit.Test;
import vn.elite.fundamental.algorithm.dijkstra.Dijkstra;
import vn.elite.fundamental.algorithm.dijkstra.Graph;
import vn.elite.fundamental.algorithm.dijkstra.Node;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DijkstraTest {

    @Test
    public void whenSPPSolved_thenCorrect() {

        Node nodeA = new Node("A");
        Node nodeB = new Node("B");
        Node nodeC = new Node("C");
        Node nodeD = new Node("D");
        Node nodeE = new Node("E");
        Node nodeF = new Node("F");

        nodeA.addDestination(nodeB, 10);
        nodeA.addDestination(nodeC, 15);

        nodeB.addDestination(nodeD, 12);
        nodeB.addDestination(nodeF, 15);

        nodeC.addDestination(nodeE, 10);

        nodeD.addDestination(nodeE, 2);
        nodeD.addDestination(nodeF, 1);

        nodeF.addDestination(nodeE, 5);

        Graph graph = new Graph();

        graph.addNode(nodeA);
        graph.addNode(nodeB);
        graph.addNode(nodeC);
        graph.addNode(nodeD);
        graph.addNode(nodeE);
        graph.addNode(nodeF);

        Dijkstra.calculateShortestPathFromSource(nodeA);

        List<Node> shortestPathForNodeB = Collections.singletonList(nodeA);
        List<Node> shortestPathForNodeC = Collections.singletonList(nodeA);
        List<Node> shortestPathForNodeD = Arrays.asList(nodeA, nodeB);
        List<Node> shortestPathForNodeE = Arrays.asList(nodeA, nodeB, nodeD);
        List<Node> shortestPathForNodeF = Arrays.asList(nodeA, nodeB, nodeD);

        for (Node node : graph.getNodes()) {
            switch (node.getName()) {
                case "B":
                    assertEquals(node.getShortestPath(), shortestPathForNodeB);
                    break;
                case "C":
                    assertEquals(node.getShortestPath(), shortestPathForNodeC);
                    break;
                case "D":
                    assertEquals(node.getShortestPath(), shortestPathForNodeD);
                    break;
                case "E":
                    assertEquals(node.getShortestPath(), shortestPathForNodeE);
                    break;
                case "F":
                    assertEquals(node.getShortestPath(), shortestPathForNodeF);
                    break;
            }
        }
    }
}
