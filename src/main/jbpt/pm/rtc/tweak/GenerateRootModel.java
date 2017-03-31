package main.jbpt.pm.rtc.tweak;

import main.jbpt.pm.rtc.structure.PM;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.bpmn.EndEvent;
import org.jbpt.pm.bpmn.StartEvent;

public class GenerateRootModel {
  private NameAssigner nameAssigner = null;

  public GenerateRootModel() {
    nameAssigner = new NameAssigner();
  }

  private boolean[][] initializeReachabilityMatrix() {
    boolean[][] reachabilityMatrix = new boolean[2][2];
    reachabilityMatrix[0][0] = true;
    reachabilityMatrix[0][1] = true;
    reachabilityMatrix[1][0] = false;
    reachabilityMatrix[1][1] = true;
    return reachabilityMatrix;
  }

  private boolean[][] initializeAdjacencyMatrix() {
    boolean[][] adjacencyMatrix = new boolean[2][2];
    adjacencyMatrix[0][0] = false;
    adjacencyMatrix[0][1] = true;
    adjacencyMatrix[1][0] = false;
    adjacencyMatrix[1][1] = false;
    return adjacencyMatrix;
  }

  private FlowNode[] initializeVertices() {
    FlowNode[] vertices = new FlowNode[2];
    /* Generate start event. */
    StartEvent startEvent = new StartEvent();
    nameAssigner.assign(startEvent);
    /* Generate end event. */
    EndEvent endEvent = new EndEvent();
    nameAssigner.assign(endEvent);
    /* Add both events to the list of vertices. */
    vertices[0] = startEvent;
    vertices[1] = endEvent;
    return vertices;
  }

  public PM generate() {
    FlowNode[] vertices = initializeVertices();
    boolean[][] adjacencyMatrix = initializeAdjacencyMatrix();
    boolean[][] reachabilityMatrix = initializeReachabilityMatrix();
    PM root = new PM(vertices, adjacencyMatrix, reachabilityMatrix);
    return root;
  }
}
