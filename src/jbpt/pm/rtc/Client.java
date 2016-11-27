package jbpt.pm.rtc;

import jbpt.pm.rtc.reader.Deserializer;
import jbpt.pm.rtc.reader.JSONDeserializer;
import jbpt.pm.rtc.reader.Reader;
import jbpt.pm.rtc.writer.JSONSerializer;
import jbpt.pm.rtc.writer.PNGSerializer;
import jbpt.pm.rtc.writer.Serializer;
import jbpt.pm.rtc.writer.Writer;
import org.jbpt.pm.ProcessModel;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Client {
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
    Generator generator = new Generator();
    Set<Model> generated = new HashSet<>();
    if (args.length == 0) {
      Model root = generator.generate();
      generated.add(root);
    } else {
      Path path = Paths.get(args[0]);
      Deserializer deserializer = new JSONDeserializer();
      Reader reader = new Reader(deserializer);
      RTCSerializer serializer = new RTCSerializer();
      /* I will only work with files, NOT with directories. */
      Collection<ProcessModel> read = reader.read(path);
      for (ProcessModel model: read) {
        Model root = serializer.serialize(model);
        generated.addAll(generator.generate(root));
      }
    }
    /* To JBPT. */
    JBPTSerializer toJBPT = new JBPTSerializer();
    Collection<ProcessModel> jbpt = toJBPT.serialize(generated);
    /* To PNG. */
    Serializer serializer = new PNGSerializer();
    Writer writer = new Writer(serializer);
    writer.write(jbpt);
    /* To JSON. */
    serializer = new JSONSerializer();
    writer.setSerializer(serializer);
    writer.write(jbpt);
  }

  /*
   * public static void main(String[] args) {
   *   Generator generator = new Generator();
   *   JBPTSerializer serializer = new JBPTSerializer();
   *   Writer writer = new Writer(new PNGSerializer());
   *   Queue<Model> queue = new LinkedList<>();
   *   queue.add(generator.generate());
   *   int i = 0;
   *   do {
   *     Model root = queue.poll();
   *     Set<Model> generated = generator.generate(root);
   *     for (Model model: generated) {
   *       queue.add(model);
   *       writer.write(serializer.serialize(model));
   *     }
   *     i++;
   *   } while (!queue.isEmpty() || i<5);
   * }
   */
}
