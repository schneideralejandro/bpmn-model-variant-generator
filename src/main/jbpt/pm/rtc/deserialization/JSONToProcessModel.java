package main.jbpt.pm.rtc.deserialization;

import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.io.JSON2Process;
import org.jbpt.throwable.SerializationException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JSONToProcessModel {
  public ProcessModel deserialize(String file)
    throws InvalidPathException, IOException, SerializationException {
    Path path = Paths.get(file);
    byte[] encoded = Files.readAllBytes(path);
    String contents = new String(encoded, StandardCharsets.UTF_8);
    ProcessModel model = JSON2Process.convert(contents);
    return model;
  }
}
