package main.jbpt.pm.rtc.serialization;

import org.jbpt.pm.ProcessModel;
import org.jbpt.utils.IOUtils;

import java.io.IOException;
import java.nio.file.Path;

public class ProcessModelToPNG implements Serializer {
  public void serialize(ProcessModel processModel, Path directory) {
    String dot = processModel.toDOT();
    try {
      IOUtils.invokeDOT(directory.toString(), processModel.getId() + ".png", dot);
    } catch (IOException e) {
      String error = "";
      error += e + "\n";
      error += "Cause: IOUtils#invokeDOT(String, String, String)";
      System.err.print(error);
    }
  }
}
