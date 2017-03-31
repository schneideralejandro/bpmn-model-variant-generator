package main.jbpt.pm.rtc.tweak;

import main.jbpt.pm.rtc.structure.PM;
import org.jbpt.pm.AndGateway;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.Gateway;
import org.jbpt.pm.XorGateway;

import java.util.Collection;
import java.util.HashSet;

public class AddDivergingGateway {
  private PM root = null;
  private NameAssigner nameAssigner = null;

  public AddDivergingGateway(PM pm, NameAssigner na) {
    root = pm;
    nameAssigner = na;
  }

  public void updateReachabilityMatrix(PM variant,
    int source, int targetA, int targetB) {
    boolean[][] reachabilityMatrix = variant.getReachabilityMatrix();
    int length = reachabilityMatrix.length;
    int gateway = length - 1;
    Collection<Integer> reachedByGateway = new HashSet<>();
    for (int j=0; j<length; j++) {
      if (reachabilityMatrix[targetA][j] || reachabilityMatrix[targetB][j]) {
        reachabilityMatrix[gateway][j] = true;
        reachedByGateway.add(j);
      }
    }
    reachabilityMatrix[gateway][gateway] = true;
    reachedByGateway.add(gateway);
    for (int i=0; i<length; i++) {
      if (reachabilityMatrix[i][source]) {
        for (int j: reachedByGateway) {
          reachabilityMatrix[i][j] = true;
        }
      }
    }
  }

  public void updateAdjacencyMatrix(PM variant,
    int source, int targetA, int targetB) {
    boolean[][] adjacencyMatrix = variant.getAdjacencyMatrix();
    int gateway = adjacencyMatrix.length - 1;
    adjacencyMatrix[source][gateway] = true;
    adjacencyMatrix[gateway][targetA] = true;
    adjacencyMatrix[gateway][targetB] = true;
  }

  public void updateVertices(PM variant, Gateway gateway) {
    FlowNode[] vertices = variant.getVertices();
    vertices[vertices.length-1] = gateway;
  }

  public Collection<PM> apply(int source, int targetA, int targetB) {
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
    updateAdjacencyMatrix(andVariant, source, targetA, targetB);
    updateAdjacencyMatrix(xorVariant, source, targetA, targetB);
    /* Update the reachability matrices. */
    updateReachabilityMatrix(andVariant, source, targetA, targetB);
    updateReachabilityMatrix(xorVariant, source, targetA, targetB);
    variants.add(andVariant);
    variants.add(xorVariant);
    return variants;
  }
}
