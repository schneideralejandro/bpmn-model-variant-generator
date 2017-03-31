package main.jbpt.pm.rtc.tweak;

import main.jbpt.pm.rtc.structure.PM;
import org.jbpt.pm.AndGateway;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.Gateway;
import org.jbpt.pm.XorGateway;

import java.util.Collection;
import java.util.HashSet;

public class AddConvergingGateway {
  private PM root = null;
  private NameAssigner nameAssigner = null;

  public AddConvergingGateway(PM pm, NameAssigner na) {
    root = pm;
    nameAssigner = na;
  }

  public void updateReachabilityMatrix(PM variant,
    int sourceA, int sourceB, int target) {
    boolean[][] reachabilityMatrix = variant.getReachabilityMatrix();
    int length = reachabilityMatrix.length;
    int gateway = length - 1;
    Collection<Integer> reachedByGateway = new HashSet<>();
    for (int j=0; j<length; j++) {
      if (reachabilityMatrix[target][j]) {
        reachabilityMatrix[gateway][j] = true;
        reachedByGateway.add(j);
      }
    }
    reachabilityMatrix[gateway][gateway] = true;
    reachedByGateway.add(gateway);
    for (int i=0; i<length; i++) {
      if (reachabilityMatrix[i][sourceA] || reachabilityMatrix[i][sourceB]) {
        for (int j: reachedByGateway) {
          reachabilityMatrix[sourceA][j] = true;
          reachabilityMatrix[sourceB][j] = true;
        }
      }
    }
  }

  public void updateAdjacencyMatrix(PM variant,
    int sourceA, int sourceB, int target) {
    boolean[][] adjacencyMatrix = variant.getAdjacencyMatrix();
    int gateway = adjacencyMatrix.length - 1;
    adjacencyMatrix[sourceA][gateway] = true;
    adjacencyMatrix[sourceB][gateway] = true;
    adjacencyMatrix[gateway][target] = true;
  }

  public void updateVertices(PM variant, Gateway gateway) {
    FlowNode[] vertices = variant.getVertices();
    vertices[vertices.length-1] = gateway;
  }

  public Collection<PM> apply(int sourceA, int sourceB, int target) {
    Collection<PM> variants = new HashSet<>();
    PM andVariant = root.cloneToBe();
    PM xorVariant = root.cloneToBe();
    /* Update the array of vertices */
    AndGateway andGateway = new AndGateway();
    XorGateway xorGateway = new XorGateway();
    nameAssigner.assign(andGateway);
    nameAssigner.assign(xorGateway);
    updateVertices(andVariant, andGateway);
    updateVertices(xorVariant, xorGateway);
    /* Update the adjacency matrices. */
    updateAdjacencyMatrix(andVariant, sourceA, sourceB, target);
    updateAdjacencyMatrix(xorVariant, sourceA, sourceB, target);
    /* Update the reachability matrices. */
    updateReachabilityMatrix(andVariant, sourceA, sourceB, target);
    updateReachabilityMatrix(xorVariant, sourceA, sourceB, target);
    variants.add(andVariant);
    variants.add(xorVariant);
    return variants;
  }
}
