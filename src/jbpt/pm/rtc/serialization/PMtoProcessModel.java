package jbpt.pm.rtc.serialization;

import jbpt.pm.rtc.structure.PM;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.ProcessModel;

public class PMtoProcessModel {
  public ProcessModel serialize(PM pm) {
    ProcessModel processModel = new ProcessModel();
    /* Reading the flow nodes. */
    FlowNode[] vertices = pm.getVertices();
    for (FlowNode vertex: vertices) {
      processModel.addFlowNode(vertex);
    }
    /* Reading the control flows. */
    boolean[][] adjacencyMatrix = pm.getAdjacencyMatrix();
    int length = adjacencyMatrix.length;
    for (int i=0; i<length; i++) {
      for (int j=0; j<length; j++) {
        if (adjacencyMatrix[i][j]) {
          processModel.addControlFlow(vertices[i], vertices[j]);
        }
      }
    }
    return processModel;
  }
}
