package jbpt.pm.rtc;

import org.jbpt.pm.FlowNode;
import org.jbpt.pm.ProcessModel;

import java.util.Collection;
import java.util.HashSet;

public class JBPTSerializer {
  public Collection<ProcessModel> serialize(Collection<Model> originals) {
    Collection<ProcessModel> serialized = new HashSet<>();
    for (Model original: originals) {
      serialized.add(serialize(original));
    }
    return serialized;
  }

  public ProcessModel serialize(Model original) {
    ProcessModel model = new ProcessModel();
    /* Flow nodes. */
    FlowNode[] vertices = original.getVertices();
    for (FlowNode vertex: vertices) {
      model.addFlowNode(vertex);
    }
    /* Control flows. */
    boolean[][] adjacency = original.getAdjacency();
    int length = adjacency.length;
    for (int i=0; i<length; i++) {
      for (int j=0; j<length; j++) {
        if (adjacency[i][j]) {
          model.addControlFlow(vertices[i], vertices[j]);
        }
      }
    }
    return model;
  }
}
