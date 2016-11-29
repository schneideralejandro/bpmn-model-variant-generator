package jbpt.pm.rtc.tweak;

import jbpt.pm.rtc.structure.PM;

import java.util.Collection;
import java.util.HashSet;

public class AddControlFlow {
  private PM root = null;

  public AddControlFlow(PM pm) {
    root = pm;
  }

  public PM apply(int source, int target) {
    PM variant = root.cloneAsIs();
    /* Adjacency matrix update. */
    boolean[][] adjacencyMatrix = variant.getAdjacencyMatrix();
    adjacencyMatrix[source][target] = true;
    /* Reachability matrix update. */
    boolean[][] reachabilityMatrix = variant.getReachabilityMatrix();
    int length = reachabilityMatrix.length;
    Collection<Integer> reachedByTarget = new HashSet<>();
    for (int j=0; j<length; j++) {
      if (reachabilityMatrix[target][j]) {
        reachedByTarget.add(j);
      }
    }
    for (int i=0; i<length; i++) {
      if (reachabilityMatrix[i][source]) {
        for (int j: reachedByTarget) {
          reachabilityMatrix[i][j] = true;
        }
      }
    }
    return variant;
  }
}
