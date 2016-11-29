package jbpt.pm.rtc.execution;

import jbpt.pm.rtc.deserialization.JSONtoPM;
import jbpt.pm.rtc.scan.Scanner;
import jbpt.pm.rtc.serialization.ProcessModelToJSON;
import jbpt.pm.rtc.serialization.ProcessModelToPNG;
import jbpt.pm.rtc.serialization.Serializer;
import jbpt.pm.rtc.serialization.Writer;
import jbpt.pm.rtc.structure.PM;
import jbpt.pm.rtc.tweak.GenerateRootModel;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;

public class Launcher {
  public static boolean validateExecutionArguments(String[] args) {
    if (args.length > 1) {
      String error = "";
      error += "Incorrect number of execution parameters: \n";
      error += "a. OPTIONAL! An absolute path.\n";
      return false;
    } else {
      return true;
    }
  }

  public static void main(String[] args) {
    if (validateExecutionArguments(args)) {
      if (args.length == 0) {
        /* Root model generation. */
        GenerateRootModel generator = new GenerateRootModel();
        PM root = generator.generate();
        /* Serialization. */
        Collection<Serializer> serializers = new HashSet<>();
        serializers.add(new ProcessModelToJSON());
        serializers.add(new ProcessModelToPNG());
        Writer writer = new Writer(serializers);
        writer.write(root);
      } else {
        /* Deserialization */
        Path path = Paths.get(args[0]);
        JSONtoPM deserializer = new JSONtoPM(path);
        PM root = deserializer.deserialize();
        /* Tweaking */
        Scanner scanner = new Scanner(root);
        Collection<PM> variants = scanner.scan();
        /* Serialization. */
        Collection<Serializer> serializers = new HashSet<>();
        serializers.add(new ProcessModelToJSON());
        serializers.add(new ProcessModelToPNG());
        Writer writer = new Writer(serializers);
        writer.write(variants);
      }
    }
  }
}
