package jbpt.pm.rtc;

import org.jbpt.pm.AndGateway;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.Gateway;
import org.jbpt.pm.XorGateway;
import org.jbpt.pm.bpmn.Task;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Tweaker {
  NameGenerator nameGenerator = null;

  public Tweaker(NameGenerator n) {
    nameGenerator = n;
  }

  public void modifyReachabilityDivergingGateway
    (Model model, int source, int targetA, int targetB) {
    FlowNode[] vertices = model.getVertices();
    boolean[][] reachability = model.getReachability();
    int length = reachability.length;
    int gateway = length - 1;
    Set<Integer> gatewayReaches = new HashSet<>();
    for (int j=0; j<length; j++) {
      if (reachability[targetA][j] || reachability[targetB][j]) {
        reachability[gateway][j] = true;
        gatewayReaches.add(j);
      }
    }
    reachability[gateway][gateway] = true;
    gatewayReaches.add(gateway);
    for (int i=0; i<length; i++) {
      if (reachability[i][source]) {
        for (Integer j: gatewayReaches) {
          reachability[i][j] = true;
        }
      }
    }
  }

  public void modifyAdjacencyDivergingGateway
    (Model model, int source, int targetA, int targetB) {
    boolean[][] adjacency = model.getAdjacency();
    int gateway = adjacency.length - 1;
    /* Incoming control flow. */
    adjacency[source][gateway] = true;
    /* Outgoing control flows. */
    adjacency[gateway][targetA] = true;
    adjacency[gateway][targetB] = true;
  }

  public void modifyReachabilityMatrix(Model model,
    int sourceA, int sourceB, int target) {
    boolean[][] reachability = model.getReachability();
    int length = reachability.length;
    int gateway = length - 1;
    Set<Integer> gatewayReaches = new HashSet<>();
    for (int j=0; j<length; j++) {
      reachability[gateway][j] = true;
      gatewayReaches.add(j);
    }
    reachability[gateway][gateway] = true;
    gatewayReaches.add(gateway);
    for (int i=0; i<length; i++) {
      if (reachability[i][sourceA] || reachability[i][sourceB]) {
        for (int j: gatewayReaches) {
          reachability[sourceA][j] = true;
          reachability[sourceB][j] = true;
        }
      }
    }
  }

  public void modifyAdjacencyMatrix(Model model,
    int sourceA, int sourceB, int target) {
    boolean[][] adjacency = model.getAdjacency();
    int gateway = adjacency.length - 1;
    /* Incoming control flows. */
    adjacency[sourceA][gateway] = true;
    adjacency[sourceB][gateway] = true;
    /* Outgoing control flow. */
    adjacency[gateway][target] = true;
  }

  public void addConvergingGateway(Model andClone, Model xorClone,
    int sourceA, int sourceB, int target) {
    /* add-converging-and-gateway tweak. */
    FlowNode[] andVertices = andClone.getVertices();
    int length = andVertices.length;
    int gateway = length - 1;
    Gateway andGateway = new AndGateway();
    nameGenerator.generate(andGateway);
    andVertices[gateway] = andGateway;
    modifyAdjacencyMatrix(andClone, sourceA, sourceB, target);
    modifyReachabilityMatrix(andClone, sourceA, sourceB, target);
    /* add-converging-xor-gateway tweak. */
    FlowNode[] xorVertices = xorClone.getVertices();
    Gateway xorGateway = new XorGateway();
    nameGenerator.generate(xorGateway);
    xorVertices[gateway] = xorGateway;
    modifyAdjacencyMatrix(xorClone, sourceA, sourceB, target);
    modifyReachabilityMatrix(xorClone, sourceA, sourceB, target);
  }

  public void addXorDivergingGateway
    (Model model, int source, int targetA, int targetB) {
    FlowNode[] vertices = model.getVertices();
    Gateway gateway = new XorGateway();
    nameGenerator.generate(gateway);
    vertices[vertices.length-1] = gateway;
    modifyAdjacencyDivergingGateway(model, source, targetA, targetB);
    modifyReachabilityDivergingGateway(model, source, targetA, targetB);
  }

  public void addAndDivergingGateway
    (Model model, int source, int targetA, int targetB) {
    FlowNode[] vertices = model.getVertices();
    Gateway gateway = new AndGateway();
    nameGenerator.generate(gateway);
    vertices[vertices.length-1] = gateway;
    modifyAdjacencyDivergingGateway(model, source, targetA, targetB);
    modifyReachabilityDivergingGateway(model, source, targetA, targetB);
  }

  /*
   * Indexes used: source, task, target.
   */
  public void modifyReachability(Model model, int source, int target) {
    boolean[][] reachability = model.getReachability();
    int length = reachability.length;
    int task = length - 1;
    /*
     * Task reaches every flow node reached by target.
     */
    List<Integer> taskReaches = new ArrayList<>();
    for (int j=0; j<length; j++) {
      if (reachability[target][j]) {
        reachability[task][j] = true;
        taskReaches.add(j);
      }
    }
    /*
     * Task also reaches itself because
     * this is a -reflexive- transitive closure.
     */
    reachability[task][task] = true;
    taskReaches.add(task);
    /*
     * Every flow node that reaches source -which includes source-,
     * now reaches every flow node reached by task -which includes task-.
     */
    for (int i=0; i<length; i++) {
      if (reachability[i][source]) {
        for (int j: taskReaches) {
          reachability[i][j] = true;
        }
      }
    }
  }

  /*
   * Indexes used: source, task and target.
   */
  public void modifyAdjacencyReplaceControlFlow(Model model, int source, int target) {
    boolean[][] adjacency = model.getAdjacency();
    int task = adjacency.length - 1;
    /* Add control flow: source -> task. */
    adjacency[source][task] = true;
    /* Add control flow: task -> target. */
    adjacency[task][target] = true;
    /* Remove control flow: source -> target. */
    adjacency[source][target] = false;
  }

  /*
   * Indexes used: source, task and target.
   */
  public void modifyAdjacencyAddTask(Model model, int source, int target) {
    boolean[][] adjacency = model.getAdjacency();
    int task = adjacency.length - 1;
    /* Add control flow: source -> task. */
    adjacency[source][task] = true;
    /* Add control flow: task -> target. */
    adjacency[task][target] = true;
  }

  public void replaceControlFlow(Model model, int source, int target) {
    FlowNode[] vertices = model.getVertices();
    Task task = new Task();
    nameGenerator.generate(task);
    vertices[vertices.length-1] = task;
    modifyAdjacencyReplaceControlFlow(model, source, target);
    modifyReachability(model, source, target);
  }

  public void addTask(Model model, int source, int target) {
    FlowNode[] vertices = model.getVertices();
    Task task = new Task();
    nameGenerator.generate(task);
    vertices[vertices.length-1] = task;
    modifyAdjacencyAddTask(model, source, target);
    modifyReachability(model, source, target);
  }

  public void addControlFlow(Model model, int source, int target) {
    /* 1. On the adjacency matrix */
    boolean[][] adjacency = model.getAdjacency();
    adjacency[source][target] = true;
    /* 2. On the reachability matrix. */
    boolean[][] reachability = model.getReachability();
    int length = reachability.length;
    /*
     * I determine which flow node are reached by target because...
     */
    List<Integer> targetReaches = new ArrayList<>();
    for (int j=0; j<length; j++) {
      if (reachability[target][j]) {
        targetReaches.add(j);
      }
    }
    /*
     * ...every flow node that reaches source -including source-
     * now reaches every flow node reached by target -including target-.
     */
    for (int i=0; i<length; i++) {
      if (reachability[i][source]) {
        for (int j: targetReaches) {
          reachability[i][j] = true;
        }
      }
    }
  }
}
