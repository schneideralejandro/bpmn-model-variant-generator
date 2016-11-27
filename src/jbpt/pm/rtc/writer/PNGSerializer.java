package jbpt.pm.rtc.writer;

import org.jbpt.pm.ProcessModel;
import org.jbpt.utils.IOUtils;

import java.io.IOException;
import java.nio.file.Path;

public class PNGSerializer implements Serializer {
  /*
   * Takes a model and a directory and produces a PNG file in the specified
   * directory.
   */
  public boolean serialize(ProcessModel model, Path directory) {
    String dot = model.toDOT();
    try {
      IOUtils.invokeDOT(directory.toString(), model.getId() + ".png", dot);
      return true;
    } catch (IOException e) {
      String error = "";
      error += "IOException: " + e + "\n";
      error += "Cause: IOUtils#invokeDOT(String, String, String)";
      System.err.print(error);
      return false;
    }
  }
}
