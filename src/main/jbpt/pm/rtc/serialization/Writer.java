package main.jbpt.pm.rtc.serialization;

import org.jbpt.pm.ProcessModel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

public class Writer {
  private Collection<Serializer> serializers = null;
  private Path directory = null;

  public Writer(Collection<Serializer> collectionSerializers) {
    serializers = collectionSerializers;
    directory = createDirectory();
  }

  private Path createDirectory() {
    Path directory = null;
    String currentWorkingDirectory = System.getProperty("user.dir");
    LocalDateTime dateTime = LocalDateTime.now();
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH.mm.ss.SSS");
    String date = dateTime.format(dateFormatter);
    String time = dateTime.format(timeFormatter);
    String path = currentWorkingDirectory;
    path += File.separator + "generated-models";
    path += File.separator + "date-" + date + "T" + time;
    try {
      directory = Files.createDirectories(Paths.get(path));
    } catch (IOException e) {
      String error = "";
      error += "IOException: " + e + "\n";
      error += "serializer.dir will remain as null.\n";
      error += "JSON files will be stored at: ";
      error += currentWorkingDirectory + "\n\n";
      System.err.print(error);
    }
    return directory;
  }

  public void write(ProcessModel processModel) {
    for (Serializer serializer: serializers) {
      serializer.serialize(processModel, directory);
    }
  }

  public void write(Collection<ProcessModel> processModels) {
    for (ProcessModel processModel: processModels) {
      write(processModel);
    }
  }
}
