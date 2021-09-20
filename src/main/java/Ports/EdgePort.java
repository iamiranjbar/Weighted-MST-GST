package Ports;

import Events.*;
import se.sics.kompics.PortType;

public class EdgePort extends PortType {{
    positive(AcceptMessage.class);
    negative(AcceptMessage.class);
    positive(ChangeRootMessage.class);
    negative(ChangeRootMessage.class);
    positive(ConnectMessage.class);
    negative(ConnectMessage.class);
    positive(InitiateMessage.class);
    negative(InitiateMessage.class);
    positive(RejectMessage.class);
    negative(RejectMessage.class);
    positive(ReportMessage.class);
    negative(ReportMessage.class);
    positive(TestMessage.class);
    negative(TestMessage.class);
}}
