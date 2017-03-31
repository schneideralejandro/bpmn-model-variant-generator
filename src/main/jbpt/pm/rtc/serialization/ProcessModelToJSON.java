package main.jbpt.pm.rtc.serialization;

import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.io.Process2JSON;
import org.jbpt.throwable.SerializationException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ProcessModelToJSON implements Serializer {
  public void serialize(ProcessModel processModel, Path directory) {
    String id = processModel.getId();
    String path = directory + File.separator + id + ".json";
    Path file = Paths.get(path);
    try (BufferedWriter writer = Files.newBufferedWriter(file)) {
      String json = Process2JSON.convert(processModel);
      writer.write(json, 0, json.length());
    } catch (IOException e) {
      String error = "";
      error += e + "\n";
      error += "Possible causes: \n";
      error += "a. Files#newBufferedReader(Path) failed to open: " + file + "\n";
      error += "b. An IO error occurred during Writer#write(char[], int, int)\n\n";
      System.err.print(error);
    } catch (SerializationException e) {
      String error = "";
      error += e + "\n";
      error += "Cause: Process2JSON#convert(ProcessModel)\n\n";
      System.err.print(error);
    }
  }
}
