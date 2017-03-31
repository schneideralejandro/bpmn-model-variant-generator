package main.jbpt.pm.rtc.execution;

import main.jbpt.pm.rtc.deserialization.JSONToProcessModel;
import main.jbpt.pm.rtc.scan.Scanner;
import main.jbpt.pm.rtc.serialization.ProcessModelToJSON;
import main.jbpt.pm.rtc.serialization.ProcessModelToPNG;
import main.jbpt.pm.rtc.serialization.Serializer;
import main.jbpt.pm.rtc.serialization.Writer;
import main.jbpt.pm.rtc.structure.PM;
import main.jbpt.pm.rtc.structure.PMToProcessModel;
import main.jbpt.pm.rtc.structure.ProcessModelToPM;
import main.jbpt.pm.rtc.throwable.InvalidAmountExecutionArgumentsException;
import main.jbpt.pm.rtc.tweak.GenerateRootModel;
import org.jbpt.pm.ProcessModel;
import org.jbpt.throwable.SerializationException;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.Collection;
import java.util.HashSet;

public class Launcher {
  /* PM -> ProcessModel -> JSON */
  private void serialize(Collection<PM> pms) {
    PMToProcessModel pmToProcessModel = new PMToProcessModel();
    Collection<ProcessModel> processModels = pmToProcessModel.serialize(pms);
    Collection<Serializer> serializers = new HashSet<>();
    serializers.add(new ProcessModelToJSON());
    serializers.add(new ProcessModelToPNG());
    Writer writer = new Writer(serializers);
    writer.write(processModels);
  }

  /* PM -> ProcessModel -> JSON */
  private void serialize(PM pm) {
    PMToProcessModel pmToProcessModel = new PMToProcessModel();
    ProcessModel processModel = pmToProcessModel.serialize(pm);
    Collection<Serializer> serializers = new HashSet<>();
    serializers.add(new ProcessModelToJSON());
    serializers.add(new ProcessModelToPNG());
    Writer writer = new Writer(serializers);
    writer.write(processModel);
  }

  /* JSON -> ProcessModel -> PM */
  private PM deserialize(String json)
    throws IOException, SerializationException {
    JSONToProcessModel jsonToProcessModel = new JSONToProcessModel();
    ProcessModelToPM processModelToPM = new ProcessModelToPM();
    ProcessModel processModel = jsonToProcessModel.deserialize(json);
    PM root = processModelToPM.deserialize(processModel);
    return root;
  }

  private void generateVariants(String json) {
    try {
      PM root = deserialize(json);
      Scanner scanner = new Scanner(root);
      Collection<PM> variants = scanner.scan();
      serialize(variants);
    } catch (InvalidPathException e) {
      String error = "";
      error = e + "\n";
      error += "CAUSE: Paths#get(String) ";
      error += "in JSONToProcessModel#deserialize(String)\n";
      System.err.print(error);
    } catch (IOException e) {
      String error = "";
      error += e + "\n";
      error += "CAUSE: Files#readAllBytes(Path) ";
      error += "in JSONToProcessModel#readFile(Path)\n";
      System.err.print(error);
    } catch (SerializationException e) {
      String error = "";
      error += e + "\n";
      error += "CAUSE: JSON2Process#convert(String) ";
      error += "in JSONToProcessModel#deserialize(String)\n";
      System.err.print(error);
    }
  }

  private void generateRootModel() {
    GenerateRootModel generator = new GenerateRootModel();
    PM root = generator.generate();
    serialize(root);
  }

  public static void main(String[] args) {
    try {
      Launcher launcher = new Launcher();
      switch (args.length) {
        case 0:
          launcher.generateRootModel();
          break;
        case 1:
          launcher.generateVariants(args[0]);
          break;
        default:
          String message = "";
          message += "\nTo generate a root model, ";
          message += "no arguments must be passed.\n";
          message += "To generate variants of an existing model, ";
          message += "a relative or absolute path must be passed.";
          throw new InvalidAmountExecutionArgumentsException(message);
      }
    } catch (InvalidAmountExecutionArgumentsException e) {
      String error = e + "\n";
      error += "CAUSE: Launcher#main(String[])\n";
      System.err.print(error);
    }
  }
}
