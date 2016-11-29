package jbpt.pm.rtc.serialization;

import org.jbpt.pm.ProcessModel;

import java.nio.file.Path;

public interface Serializer {
  void serialize(ProcessModel model, Path directory);
}
