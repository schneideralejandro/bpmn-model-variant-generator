package jbpt.pm.rtc;

import org.jbpt.pm.FlowNode;
import org.jbpt.pm.bpmn.EndEvent;
import org.jbpt.pm.bpmn.StartEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Generator {
  RootGenerator rootGenerator = null;
  Tweaker tweaker = null;

  public Generator() {
    NameGenerator nameGenerator = new NameGenerator();
    rootGenerator = new RootGenerator(nameGenerator);
    tweaker = new Tweaker(nameGenerator);
  }

  public Set<Model> scanForControlFlow(Model model,
    boolean[][] adjacency, int i, int j) {
    Set<Model> generated = new HashSet<>();
    if (adjacency[i][j]) {
      /* Apply replace-control-flow tweak. */
      Model clone = model.cloneToBe();
      tweaker.replaceControlFlow(clone, i, j);
      generated.add(clone);
      /* Apply add-task tweak. */
      clone = model.cloneToBe();
      tweaker.addTask(clone, i, j);
      generated.add(clone);
    }
    return generated;
  }

  public Set<Model> scanForReachability(Model model, FlowNode[] vertices,
    boolean[][] reachability, int i, int j,
    List<Integer> possibleGatewayTargets) {
    Set<Model> generated = new HashSet<>();
    if (!(vertices[i] instanceof EndEvent) &&
      !(vertices[j] instanceof StartEvent) &&
      !reachability[i][j] && !reachability[j][i]) {
      /* Apply add-control-flow tweak. */
      Model clone = model.cloneAsIs();
      tweaker.addControlFlow(clone, i, j);
      generated.add(clone);
      /* Apply add-task tweak.
       * clone = model.cloneToBe();
       * tweaker.addTask(clone, i, j);
       * generated.add(clone);
       */
    }
    if (!reachability[j][i]) {
      possibleGatewayTargets.add(j);
    }
    return generated;
  }

  public Set<Model> convergingGatewayScan(Model model) {
    Set<Model> generated = new HashSet<>();
    FlowNode[] vertices = model.getVertices();
    boolean[][] reachability = model.getReachability();
    int length = vertices.length;
    int gateway = length - 1;
    for (int j = 0; j < length; j++) {
      List<Integer> sources = new ArrayList<>();
      for (int i = 0; i < length; i++) {
        if (!(vertices[i] instanceof EndEvent) && !reachability[j][i]) {
          sources.add(i);
        }
      }
      int size = sources.size();
      if (size >= 2) {
        for (int sourceA = 0; sourceA < size - 1; sourceA++) {
          for (int sourceB = 1; sourceB < size; sourceB++) {
            Model andClone = model.cloneToBe();
            Model xorClone = model.cloneToBe();
            tweaker.addConvergingGateway(andClone, xorClone,
              sources.get(sourceA), sources.get(sourceB), j);
            generated.add(andClone);
            generated.add(xorClone);
          }
        }
      }
    }
    return generated;
  }

  public Set<Model> scan(Model model) {
    Set<Model> generated = new HashSet<>();
    FlowNode[] vertices = model.getVertices();
    boolean[][] adjacency = model.getAdjacency();
    boolean[][] reachability = model.getReachability();
    int length = vertices.length;
    /* We traverse the matrix just once. */
    for (int i=0; i<length; i++) {
      List<Integer> possibleGatewayTargets = new ArrayList<>();
      for (int j = 0; j < length; j++) {
        generated.addAll(scanForReachability
          (model, vertices, reachability, i, j, possibleGatewayTargets));
        generated.addAll(scanForControlFlow
          (model, adjacency, i, j));
      }
      if (possibleGatewayTargets.size() >= 2) {
        for (int targetA = 0; targetA < possibleGatewayTargets.size() - 1; targetA++) {
          for (int targetB = targetA + 1; targetB < possibleGatewayTargets.size(); targetB++) {
            Model cloneA = model.cloneToBe();
            tweaker.addAndDivergingGateway
              (cloneA, i, possibleGatewayTargets.get(targetA), possibleGatewayTargets.get(targetB));
            generated.add(cloneA);
            Model cloneB = model.cloneToBe();
            tweaker.addXorDivergingGateway
              (cloneB, i, possibleGatewayTargets.get(targetA), possibleGatewayTargets.get(targetB));
            generated.add(cloneB);
          }
        }
      }
    }
    generated.addAll(convergingGatewayScan(model));
    return generated;
  }

  public Model generate() {
    Model root = rootGenerator.generate();
    return root;
  }

  public Set<Model> generate(Model root) {
    Set<Model> generated = scan(root);
    return generated;
  }
}
