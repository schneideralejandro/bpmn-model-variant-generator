package main.jbpt.pm.rtc.structure;

import org.jbpt.pm.FlowNode;
import org.jbpt.pm.ProcessModel;

import java.util.Collection;
import java.util.HashSet;

public class PMToProcessModel {
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

  public Collection<ProcessModel> serialize(Collection<PM> pms) {
    Collection<ProcessModel> processModels = new HashSet<>();
    for (PM pm: pms) {
      processModels.add(serialize(pm));
    }
    return processModels;
  }
}
