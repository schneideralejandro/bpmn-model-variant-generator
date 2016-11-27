package jbpt.pm.rtc.writer;

import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.io.Process2JSON;
import org.jbpt.throwable.SerializationException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JSONSerializer implements Serializer {
  /*
   * Takes a model and a directory and produces a JSON file in the specified
   * directory.Path file = Paths.get(name);
   */
  public boolean serialize(ProcessModel model, Path directory) {
    String name = directory + File.separator + model.getId() + ".json";
    Path file = Paths.get(name);
    try (BufferedWriter writer = Files.newBufferedWriter(file)) {
      String json = Process2JSON.convert(model);
      writer.write(json, 0, json.length());
      return true;
    } catch (IOException e) {
      String error = "";
      error += "IOException: " + e + "\n";
      error += "Possible causes: \n";
      error += "a. Files#newBufferedReader(Path) failed to open: " + file + "\n";
      error += "b. An IO error occurred during Writer#write(char[], int, int)\n\n";
      System.err.print(error);
      return false;
    } catch (SerializationException e) {
      String error = "";
      error += "SerializationException: " + e + "\n";
      error += "Cause: Process2JSON#convert(ProcessModel)\n\n";
      System.err.print(error);
      return false;
    }
  }
}
