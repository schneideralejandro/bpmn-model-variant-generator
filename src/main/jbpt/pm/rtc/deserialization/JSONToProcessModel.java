package main.jbpt.pm.rtc.deserialization;

import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.io.JSON2Process;
import org.jbpt.throwable.SerializationException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;

public class JSONToProcessModel {
  public ProcessModel deserialize(String fileName)
    throws InvalidPathException, IOException, SerializationException {
    File file = new File(fileName);
    byte[] encoded = Files.readAllBytes(file.toPath());
    String contents = new String(encoded, StandardCharsets.UTF_8);
    ProcessModel model = JSON2Process.convert(contents);
    return model;
  }
}
