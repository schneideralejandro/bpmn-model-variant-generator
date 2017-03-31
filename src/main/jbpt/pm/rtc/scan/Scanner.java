package main.jbpt.pm.rtc.scan;

import main.jbpt.pm.rtc.structure.PM;
import main.jbpt.pm.rtc.tweak.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class Scanner {
  private PM root = null;
  /* Used for dependency injection, instead of a singleton. */
  private NameAssigner nameAssigner = null;

  public Scanner(PM pm) {
    root = pm;
    nameAssigner = new NameAssigner();
  }

  public Collection<PM> addDivergingGateway() {
    AddDivergingGateway tweaker = new AddDivergingGateway(root, nameAssigner);
    Collection<PM> variants = new HashSet<>();
    boolean[][] reachabilityMatrix = root.getReachabilityMatrix();
    int length = reachabilityMatrix.length;
    for (int i=0; i<length; i++) {
      List<Integer> targets = new ArrayList<>();
      for (int j=0; j<length; j++) {
        if (!reachabilityMatrix[j][i]) {
          targets.add(j);
        }
      }
      int size = targets.size();
      if (size >= 2) {
        for (int a=0; a<size-1; a++) {
          for (int b=a+1; b<size; b++) {
            int targetA = targets.get(a);
            int targetB = targets.get(b);
            variants.addAll(tweaker.apply(i, targetA, targetB));
          }
        }
      }
    }
    return variants;
  }

  public Collection<PM> addConvergingGateway() {
    AddConvergingGateway tweaker = new AddConvergingGateway(root, nameAssigner);
    Collection<PM> variants = new HashSet<>();
    boolean[][] reachabilityMatrix = root.getReachabilityMatrix();
    int length = reachabilityMatrix.length;
    for (int j=0; j<length; j++) {
      List<Integer> sources = new ArrayList<>();
      for (int i=0; i<length; i++) {
        if (!reachabilityMatrix[j][i]) {
          sources.add(i);
        }
      }
      length = sources.size();
      if (length >= 2) {
        for (int a=0; a<length-1; a++) {
          for (int b=a+1; b<length; b++) {
            int sourceA = sources.get(a);
            int sourceB = sources.get(b);
            variants.addAll(tweaker.apply(sourceA, sourceB, j));
          }
        }
      }
    }
    return variants;
  }

  public Collection<PM> replaceControlFlow() {
    ReplaceControlFlow tweaker = new ReplaceControlFlow(root, nameAssigner);
    Collection<PM> variants = new HashSet<>();
    boolean[][] adjacencyMatrix = root.getAdjacencyMatrix();
    int length = adjacencyMatrix.length;
    for (int i=0; i<length; i++) {
      for (int j=0; j<length; j++) {
        if (adjacencyMatrix[i][j]) {
          PM variant = tweaker.apply(i, j);
          variants.add(variant);
        }
      }
    }
    return variants;
  }

  public Collection<PM> addTask() {
    AddTask tweaker = new AddTask(root, nameAssigner);
    Collection<PM> variants = new HashSet<>();
    boolean[][] reachabilityMatrix = root.getReachabilityMatrix();
    int length = reachabilityMatrix.length;
    for (int i=0; i<length; i++) {
      for (int j=0; j<length; j++) {
        if (!reachabilityMatrix[j][i]) {
          PM variant = tweaker.apply(i, j);
          variants.add(variant);
        }
      }
    }
    return variants;
  }

  public Collection<PM> addControlFlow() {
    AddControlFlow tweaker = new AddControlFlow(root);
    Collection<PM> variants = new HashSet<>();
    boolean[][] reachabilityMatrix = root.getReachabilityMatrix();
    int length = reachabilityMatrix.length;
    for (int i=0; i<length; i++) {
      for (int j=0; j<length; j++) {
        if (!reachabilityMatrix[i][j] && !reachabilityMatrix[j][i]) {
          PM variant = tweaker.apply(i, j);
          variants.add(variant);
        }
      }
    }
    return variants;
  }

  public Collection<PM> scan() {
    Collection<PM> variants = new HashSet<>();
    variants.addAll(addControlFlow());
    variants.addAll(addTask());
    variants.addAll(replaceControlFlow());
    variants.addAll(addConvergingGateway());
    variants.addAll(addDivergingGateway());
    return variants;
  }
}
