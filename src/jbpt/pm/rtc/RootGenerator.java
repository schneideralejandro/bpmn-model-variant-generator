package jbpt.pm.rtc;

import org.jbpt.pm.FlowNode;
import org.jbpt.pm.bpmn.EndEvent;
import org.jbpt.pm.bpmn.StartEvent;

public class RootGenerator {
  private NameGenerator nameGenerator = null;

  public RootGenerator(NameGenerator n) {
    nameGenerator = n;
  }

  /*
   * Yes, this is hard-coded.
   * Yes, I could have done it properly.
   * No, I don't want to do it.
   * It's fugly.
   */
  public boolean[][] initReachability() {
    boolean[][] reachability = new boolean[2][2];
    /*
     * Reachability: start event -> start event.
     * Because this is a -reflexive- transitive closure.
     */
    reachability[0][0] = true;
    /*
     * Reachability: start event -> end event.
     * There's a control flow between'em.
     */
    reachability[0][1] = true;
    /*
     * Note that there is no reachability
     * between the end event and the start event.
     */
    reachability[1][0] = false;
    /*
     * Reachability: start event -> end event.
     * Because this is a -reflexive- transitive closure.
     */
    reachability[1][1] = true;
    return reachability;
  }

  /*
   * Yes, this is hard-coded.
   * Yes, I could have done it properly.
   * No, I don't want to do it.
   * It's fugly.
   */
  public boolean[][] initAdjacency() {
    boolean[][] adjacency = new boolean[2][2];
    adjacency[0][0] = false;
    /* Control flow: start event -> end event. */
    adjacency[0][1] = true;
    adjacency[1][0] = false;
    adjacency[1][1] = false;
    return adjacency;
  }

  public FlowNode[] initVertices() {
    FlowNode[] vertices = new FlowNode[2];
    /* Generate start event. */
    StartEvent startEvent = new StartEvent();
    nameGenerator.generate(startEvent);
    /* Generate end event. */
    EndEvent endEvent = new EndEvent();
    nameGenerator.generate(endEvent);
    /* Add both events to the list of vertices. */
    vertices[0] = startEvent;
    vertices[1] = endEvent;
    return vertices;
  }

  public Model generate() {
    FlowNode[] vertices = initVertices();
    boolean[][] adjacency = initAdjacency();
    boolean[][] reachability = initReachability();
    Model root = new Model(vertices, adjacency, reachability);
    return root;
  }
}
