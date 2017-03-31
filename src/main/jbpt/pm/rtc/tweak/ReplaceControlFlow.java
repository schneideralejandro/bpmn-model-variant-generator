package main.jbpt.pm.rtc.tweak;

import main.jbpt.pm.rtc.structure.PM;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.bpmn.Task;

import java.util.Collection;
import java.util.HashSet;

public class ReplaceControlFlow {
  private PM root = null;
  private NameAssigner nameAssigner = null;

  public ReplaceControlFlow(PM pm, NameAssigner na) {
    root = pm;
    nameAssigner = na;
  }

  public void updateReachabilityMatrix(PM variant, int source, int target) {
    boolean[][] reachabilityMatrix = variant.getReachabilityMatrix();
    int length = reachabilityMatrix.length;
    int task = length - 1;
    Collection<Integer> reachedByTask = new HashSet<>();
    for (int j=0; j<length; j++) {
      if (reachabilityMatrix[j][target]) {
        reachabilityMatrix[task][j] = true;
        reachedByTask.add(j);
      }
    }
    reachabilityMatrix[task][task] = true;
    reachedByTask.add(task);
    for (int i=0; i<length; i++) {
      if (reachabilityMatrix[i][source]) {
        for (int j: reachedByTask) {
          reachabilityMatrix[i][j] = true;
        }
      }
    }
  }

  public void updateAdjacencyMatrix(PM variant, int source, int target) {
    boolean[][] adjacencyMatrix = variant.getAdjacencyMatrix();
    int task = adjacencyMatrix.length - 1;
    adjacencyMatrix[source][task] = true;
    adjacencyMatrix[task][target] = true;
    adjacencyMatrix[source][target] = false;
  }

  public void updateVertices(PM variant) {
    FlowNode[] vertices = variant.getVertices();
    Task task = new Task();
    nameAssigner.assign(task);
    vertices[vertices.length-1] = task;
  }

  public PM apply(int source, int target) {
    PM variant = root.cloneToBe();
    /* Update the array of vertices. */
    updateVertices(variant);
    /* Update the adjacency matrix. */
    updateAdjacencyMatrix(variant, source, target);
    /* Update the reachability matrix. */
    updateReachabilityMatrix(variant, source, target);
    return variant;
  }
}
