package jbpt.pm.rtc.writer;

import org.jbpt.pm.ProcessModel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Iterator;

/* Builder pattern. */
public class Writer {
  private Serializer serializer = null;
  private Path directory = null;

  public Writer(Serializer aSerializer) {
    serializer = aSerializer;
    directory = setDirectory();
  }

  public void setSerializer(Serializer aSerializer) {
    serializer = aSerializer;
  }

  public Path setDirectory() {
    return createDirectory();
  }

  /*
   * A family of directories is created to store the files.
   * a. JSON files are stored in:
   * current-working-directory/generated-models/json-YYYY-MM-DDThh:mm:ss/model-id
   * b. PNG files are stored in:
   * current-working-directory/generated-models/png-YYYY-MM-DDThh:mm:ss/model-id
   */
  public Path createDirectory() {
    Path directory = null;
    String currentWorkingDirectory = System.getProperty("user.dir");
    String date = LocalDateTime.now().toString();
    String path = currentWorkingDirectory;
    path += File.separator + "generated-models";
    if (serializer instanceof JSONSerializer) {
      path += File.separator + "json-" + date;
    } else if (serializer instanceof PNGSerializer) {
      path += File.separator + "png-" + date;
    }
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

  public boolean write(ProcessModel model) {
    boolean success = serializer.serialize(model, directory);
    return success;
  }

  public boolean write(Collection<ProcessModel> models) {
    boolean success;
    Iterator<ProcessModel> iterator = models.iterator();
    do {
      ProcessModel model = iterator.next();
      success = serializer.serialize(model, directory);
    } while (iterator.hasNext() && success);
    return success;
  }
}
