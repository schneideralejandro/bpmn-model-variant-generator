package jbpt.pm.rtc.writer;

import org.jbpt.pm.ProcessModel;

import java.nio.file.Path;

public interface Serializer {
  boolean serialize(ProcessModel model, Path directory);
}
