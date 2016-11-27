package jbpt.pm.rtc;

import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.ProcessModel;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class RTCSerializer {
  /*
   * Supper inefficient, but I'll do for now.
   */
  public Model serialize(ProcessModel original) {
    /* Vertices. */
    Collection<FlowNode> aux = original.getVertices();
    FlowNode[] vertices = aux.toArray(new FlowNode[aux.size()]);
    int length = vertices.length;
    /* Adjacency matrix. */
    boolean[][] adjacency = new boolean[length][length];
    List<FlowNode> verticesAsList = Arrays.asList(vertices);
    Collection<ControlFlow<FlowNode>> controlFlows = original.getControlFlow();
    for (ControlFlow<FlowNode> controlFlow: controlFlows) {
      int source = verticesAsList.indexOf(controlFlow.getSource());
      int target = verticesAsList.indexOf(controlFlow.getTarget());
      adjacency[source][target] = true;
    }
    /* Reachability matrix. */
    boolean[][] reachability = new boolean[length][length];
    for (int i=0; i<length; i++) {
      reachability[i] = Arrays.copyOf(adjacency[i], length);
    }
    for (int i=0; i<length; i++)
      for (int j=0; j<length; j++)
        if (reachability[j][i])
          for (int k=0; k<length; k++)
            reachability[j][k] = reachability[j][k] | reachability[i][k];
    for (int i=0; i<length; i++) {
      reachability[i][i] = true;
    }
    /* Model initiation. */
    Model model = new Model(vertices, adjacency, reachability);
    return model;
  }
}
