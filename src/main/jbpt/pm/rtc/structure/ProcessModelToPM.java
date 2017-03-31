package main.jbpt.pm.rtc.structure;

import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.ProcessModel;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ProcessModelToPM {
   public boolean[][] getReachabilityMatrix
     (int length, boolean[][] adjacencyMatrix) {
     boolean[][] reachabilityMatrix = new boolean[length][length];
     for (int i = 0; i < length; i++) {
       reachabilityMatrix[i] = Arrays.copyOf(adjacencyMatrix[i], length);
     }
     for (int i = 0; i < length; i++) {
       for (int j = 0; j < length; j++) {
         if (reachabilityMatrix[j][i]) {
           for (int k = 0; k < length; k++) {
             reachabilityMatrix[j][k] = reachabilityMatrix[j][k] |
               reachabilityMatrix[i][k];
           }
         }
       }
     }
     for (int i=0; i<length; i++) {
       reachabilityMatrix[i][i] = true;
     }
     return reachabilityMatrix;
   }

  public boolean[][] getAdjacencyMatrix
    (ProcessModel processModel, FlowNode[] vertices) {
    List<FlowNode> verticesAsList = Arrays.asList(vertices);
    Collection<ControlFlow<FlowNode>> controlFlows = processModel.getControlFlow();
    boolean[][] adjacencyMatrix = new boolean[vertices.length][vertices.length];
    for (ControlFlow<FlowNode> controlFlow: controlFlows) {
      int source = verticesAsList.indexOf(controlFlow.getSource());
      int target = verticesAsList.indexOf(controlFlow.getTarget());
      adjacencyMatrix[source][target] = true;
    }
    return adjacencyMatrix;
  }

  public FlowNode[] getVertices(ProcessModel processModel) {
    Collection<FlowNode> vertices = processModel.getVertices();
    return vertices.toArray(new FlowNode[vertices.size()]);
  }

  public PM deserialize(ProcessModel processModel) {
    FlowNode[] vertices = getVertices(processModel);
    boolean[][] adjacencyMatrix = getAdjacencyMatrix(processModel, vertices);
    boolean[][] reachabilityMatrix = getReachabilityMatrix
      (vertices.length, adjacencyMatrix);
    return new PM(vertices, adjacencyMatrix, reachabilityMatrix);
  }
}
