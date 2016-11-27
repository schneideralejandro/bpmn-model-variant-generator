package jbpt.pm.rtc;

import org.jbpt.pm.FlowNode;

import java.util.Arrays;

public class Model {
  private FlowNode[] vertices = null;
  private boolean[][] adjacency = null;
  private boolean[][] reachability = null;

  public Model(FlowNode[] v, boolean[][] a, boolean[][] r) {
    vertices = v;
    adjacency = a;
    reachability = r;
  }

  public FlowNode[] getVertices() {
    return vertices;
  }

  public boolean[][] getAdjacency() {
    return adjacency;
  }

  public boolean[][] getReachability() {
    return reachability;
  }

  /*
   * Used by the add-task tweak.
   */
  public Model cloneToBe() {
    /*
     * The length of the array of vertices is increased in 1
     * allowing the addition of the new flow node.
     * Initially, vertices[length-1] == null
     */
    int length = vertices.length + 1;
    FlowNode[] v = Arrays.copyOf(vertices, length);
    /*
     * A row and a column are added to both matrices,
     * to store the information pertinent to the new flow node.
     */
    boolean[][] a = new boolean[length][];
    for (int i=0; i<length-1; i++) {
      a[i] = Arrays.copyOf(adjacency[i], length);
    }
    a[length-1] = new boolean[length];
    boolean[][] r = new boolean[length][];
    for (int i=0; i<length-1; i++) {
      r[i] = Arrays.copyOf(reachability[i], length);
    }
    r[length-1] = new boolean[length];
    /* Finally, we create a new Model instance */
    Model clone = new Model(v, a, r);
    return clone;
  }

  /*
   * Used by the add-control-flow tweak.
   */
  public Model cloneAsIs() {
    /* Cloning the array of vertices. */
    int length = vertices.length;
    FlowNode[] v = Arrays.copyOf(vertices, length);
    /* Cloning the adjacency matrix. */
    boolean[][] a = new boolean[length][];
    for (int i=0; i<length; i++) {
      a[i] = Arrays.copyOf(adjacency[i], length);
    }
    /* Cloning the reachability matrix. */
    boolean[][] r = new boolean[length][];
    for (int i=0; i<length; i++) {
      r[i] = Arrays.copyOf(reachability[i], length);
    }
    /* Creating a new Model instance. */
    Model clone = new Model(v, a, r);
    return clone;
  }
}
