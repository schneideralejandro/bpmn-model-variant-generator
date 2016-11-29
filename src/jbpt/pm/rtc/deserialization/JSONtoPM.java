package jbpt.pm.rtc.deserialization;

import jbpt.pm.rtc.structure.PM;
import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.io.JSON2Process;
import org.jbpt.throwable.SerializationException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class JSONtoPM {
  private Path file = null;

  public JSONtoPM(Path path) {
    file = path;
  }

  /* McGiver's duct tape design pattern. */
  public PM processModelToPM(ProcessModel processModel) {
    /* Reading the array of vertices. */
    Collection<FlowNode> v = processModel.getVertices();
    int length = v.size();
    FlowNode[] vertices = v.toArray(new FlowNode[v.size()]);
    /* Reading the adjacency matrix. */
    Collection<ControlFlow<FlowNode>> controlFlows =
      processModel.getControlFlow();
    boolean[][] adjacencyMatrix =
            new boolean[length][length];
    List<FlowNode> verticesAsList = Arrays.asList(vertices);
    for (ControlFlow<FlowNode> controlFlow : controlFlows) {
      int source = verticesAsList.indexOf(controlFlow.getSource());
      int target = verticesAsList.indexOf(controlFlow.getTarget());
      adjacencyMatrix[source][target] = true;
    }
    /* Generating the reachability matrix. */
    boolean[][] reachabilityMatrix = new boolean[length][length];
    for (int i=0; i<length; i++) {
      reachabilityMatrix[i] = Arrays.copyOf(adjacencyMatrix[i], length);
    }
    for (int i=0; i<length; i++) {
      for (int j=0; j<length; j++) {
        if (reachabilityMatrix[j][i]) {
          for (int k=0; k<length; k++) {
            reachabilityMatrix[j][k] = reachabilityMatrix[j][k] |
                    reachabilityMatrix[i][k];
          }
        }
      }
    }
    for (int i=0; i<length; i++) {
      reachabilityMatrix[i][i] = true;
    }
    /* Generating the PM instance */
    PM pm = new PM(vertices, adjacencyMatrix, reachabilityMatrix);
    return pm;
  }

  public String readFile() throws IOException {
    byte[] encoded = Files.readAllBytes(file);
    String contents = new String(encoded, StandardCharsets.UTF_8);
    return contents;
  }

  public PM deserialize() {
    PM model = null;
    try {
      String contents = readFile();
      ProcessModel aux = JSON2Process.convert(contents);
      model = processModelToPM(aux);
    } catch (IOException e) {
      String error = "";
      error += "IOException: " + e + "\n";
      error += "Cause: Files#readAllBytes(Path)\n\n";
      System.err.print(error);
    } catch (SerializationException e) {
      String error = "";
      error += "SerializationException: " + e + "\n";
      error += "Cause: JSON2Process#convert(String)\n\n";
      System.err.print(error);
    }
    return model;
  }
}
