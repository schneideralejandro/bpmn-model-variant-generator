package main.jbpt.pm.rtc.structure;

import org.jbpt.pm.FlowNode;

import java.util.Arrays;

public class PM {
  private FlowNode[] vertices = null;
  private boolean[][] adjacencyMatrix = null;
  private boolean[][] reachabilityMatrix = null;

  public PM(FlowNode[] v, boolean[][] a, boolean[][] r) {
    vertices = v;
    adjacencyMatrix = a;
    reachabilityMatrix = r;
  }

  public FlowNode[] getVertices() {
    return vertices;
  }

  public boolean[][] getAdjacencyMatrix() {
    return adjacencyMatrix;
  }

  public boolean[][] getReachabilityMatrix() {
    return reachabilityMatrix;
  }

  /* Used by tweaks that add a flow node. */
  public PM cloneToBe() {
    /* Cloning the array of vertices. */
    int length = vertices.length + 1;
    FlowNode[] v = Arrays.copyOf(vertices, length);
    /* Cloning the adjacency matrix. */
    boolean[][] a = new boolean[length][length];
    for (int i=0; i<length-1; i++) {
      a[i] = Arrays.copyOf(adjacencyMatrix[i], length);
    }
    a[length-1] = new boolean[length];
    /* Cloning the reachability matrix. */
    boolean[][] r = new boolean[length][];
    for (int i=0; i<length-1; i++) {
      r[i] = Arrays.copyOf(reachabilityMatrix[i], length);
    }
    r[length-1] = new boolean[length];
    /* Cloning the PM instance */
    PM clone = new PM(v, a, r);
    return clone;
  }

  /* Used by the add-control-flow tweak. */
  public PM cloneAsIs() {
    /* Cloning the array of vertices. */
    int length = vertices.length;
    FlowNode[] v = Arrays.copyOf(vertices, length);
    /* Cloning the adjacency matrix. */
    boolean[][] a = new boolean[length][length];
    for (int i=0; i<length; i++) {
      a[i] = Arrays.copyOf(adjacencyMatrix[i], length);
    }
    /* Cloning the reachability matrix. */
    boolean[][] r = new boolean[length][length];
    for (int i=0; i<length; i++) {
      r[i] = Arrays.copyOf(reachabilityMatrix[i], length);
    }
    /* Cloning the PM instance. */
    PM clone = new PM(v, a, r);
    return clone;
  }
}
