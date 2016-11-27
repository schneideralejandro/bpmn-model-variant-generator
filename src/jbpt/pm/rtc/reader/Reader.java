package jbpt.pm.rtc.reader;

import org.jbpt.pm.ProcessModel;

import java.nio.file.Path;
import java.util.Collection;

/* Builder pattern */
public class Reader {
  Deserializer deserializer = null;

  public Reader(Deserializer aDeserializer) {
    deserializer = aDeserializer;
  }

  /*
   * Returns a collection instead of just one model because a path could
   * either be a JSON file or a directory of JSON files.
   */
  public Collection<ProcessModel> read(Path path) {
    Collection<ProcessModel> models = deserializer.deserialize(path);
    return models;
  }
}
