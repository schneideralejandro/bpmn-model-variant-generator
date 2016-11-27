package jbpt.pm.rtc;

import org.jbpt.pm.AndGateway;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.XorGateway;
import org.jbpt.pm.bpmn.EndEvent;
import org.jbpt.pm.bpmn.StartEvent;
import org.jbpt.pm.bpmn.Task;

public class NameGenerator {
  /* Counters, used for name generation. */
  private int countStartEvents = 0;
  private int countEndEvents = 0;
  private int countTasks = 0;
  private int countAndGateways = 0;
  private int countXorGateways = 0;

  public <FN extends FlowNode> FN generate (FN flowNode) {
    String name = null;
    if (flowNode instanceof StartEvent) {
      name = "s" + String.valueOf(++countStartEvents);
    } else if (flowNode instanceof EndEvent) {
      name = "e" + String.valueOf(++countEndEvents);
    } else if (flowNode instanceof Task) {
      name = "t" + String.valueOf(++countTasks);
    } else if (flowNode instanceof AndGateway) {
      name = "and" + String.valueOf(++countAndGateways);
    } else if (flowNode instanceof XorGateway) {
      name = "xor" + String.valueOf(++countXorGateways);
    }
    flowNode.setName(name);
    return flowNode;
  }
}
