package jbpt.pm.rtc.reader;

import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.io.JSON2Process;
import org.jbpt.throwable.SerializationException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;

public class JSONDeserializer implements Deserializer {
  /*
   * Generates the model.
   */
  public ProcessModel readModel(Path file) {
    ProcessModel model = null;
    try {
      String contents = readFile(file);
      model = JSON2Process.convert(contents);
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

  /*
   * Returns the whole content of the JSON file in one String.
   * Process2JSON#convert(String) returns a JSON file of just one line anyway.
   */
  public String readFile(Path file) throws IOException {
    byte[] encoded = Files.readAllBytes(file);
    String contents = new String(encoded, StandardCharsets.UTF_8);
    return contents;
  }

  /*
   * Returns the collection of JSON files inside the directory.
   */
  public Collection<Path> readDirectory(Path directory) {
    Collection<Path> files = new HashSet<>();
    try (DirectoryStream<Path> stream =
      Files.newDirectoryStream(directory, "*.json")) {
      for (Path file: stream) {
        files.add(file);
      }
    } catch (IOException e) {
      String error = "";
      error += "IOException: " + e + "\n";
      error += "Cause: Files#newDirectoryStream(Path. String)\n\n";
      System.err.print(error);
    }
    return files;
  }

  /*
   * A path could either be a JSON file or a directory.
   * JSON files are analyzed one by one. One JSON file contains one model.
   */
  public Collection<ProcessModel> deserialize(Path path) {
    Collection<ProcessModel> models = new HashSet<>();
    if (Files.isDirectory(path)) {
      Collection<Path> files = readDirectory(path);
      for (Path file: files) {
        models.add(readModel(file));
      }
    } else {
      models.add(readModel(path));
    }
    return models;
  }
}
