package jbpt.pm.rtc.reader;

import org.jbpt.pm.ProcessModel;

import java.nio.file.Path;
import java.util.Collection;

public interface Deserializer {
  Collection<ProcessModel> deserialize(Path path);
}
