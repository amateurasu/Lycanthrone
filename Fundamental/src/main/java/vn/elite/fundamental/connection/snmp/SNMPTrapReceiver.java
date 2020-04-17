package vn.elite.fundamental.connection.snmp;

import org.snmp4j.*;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.MPv3;
import org.snmp4j.security.*;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.MultiThreadedMessageDispatcher;
import org.snmp4j.util.ThreadPool;

import java.io.IOException;
import java.util.List;

public class SNMPTrapReceiver implements CommandResponder {

    private MultiThreadedMessageDispatcher dispatcher;
    private Snmp snmp = null;
    private Address listenAddress;
    private ThreadPool threadPool;
    private final int n = 0;
    private final long start = -1;

    public SNMPTrapReceiver() {
    }

    public static void main(String[] args) {
        new SNMPTrapReceiver().run();
    }

    private void run() {
        try {
            init();
            snmp.addCommandResponder(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void init() throws IOException {
        threadPool = ThreadPool.create("Trap", 10);
        dispatcher = new MultiThreadedMessageDispatcher(threadPool, new MessageDispatcherImpl());

        //TRANSPORT
        listenAddress = GenericAddress.parse(System.getProperty("snmp4j.listenAddress", "udp:127.0.0.1/162"));
        TransportMapping<?> transport = listenAddress instanceof UdpAddress
            ? new DefaultUdpTransportMapping((UdpAddress) listenAddress)
            : new DefaultTcpTransportMapping((TcpAddress) listenAddress);

        //V3 SECURITY
        USM usm = new USM(SecurityProtocols.getInstance().addDefaultProtocols(),
            new OctetString(MPv3.createLocalEngineID()), 0);

        SecurityProtocols.getInstance().addPrivacyProtocol(new PrivAES192());
        SecurityProtocols.getInstance().addPrivacyProtocol(new PrivAES256());
        SecurityProtocols.getInstance().addPrivacyProtocol(new Priv3DES());

        usm.setEngineDiscoveryEnabled(true);

        SecurityModels.getInstance().addSecurityModel(usm);

        snmp = new Snmp(dispatcher, transport);
        snmp.getMessageDispatcher().addMessageProcessingModel(new MPv1());
        snmp.getMessageDispatcher().addMessageProcessingModel(new MPv2c());
        snmp.getMessageDispatcher().addMessageProcessingModel(new MPv3(usm));

        String username = "username";         // SET THIS
        String authpassphrase = "authpassphrase";   // SET THIS
        String privacypassphrase = "privacypassphrase";   // SET THIS

        snmp.getUSM().addUser(    // SET THE SECURITY PROTOCOLS HERE
            new OctetString(username),
            new UsmUser(new OctetString(username), AuthMD5.ID, new OctetString(authpassphrase), PrivAES128.ID,
                new OctetString(privacypassphrase)));

        snmp.listen();
    }

    public void processPdu(CommandResponderEvent crEvent) {
        PDU pdu = crEvent.getPDU();

        switch (pdu.getType()) {
            case PDU.V1TRAP:
                PDUv1 pduV1 = (PDUv1) pdu;
                System.out.println();
                System.out.println("===== NEW SNMP 1 TRAP RECEIVED ====");
                System.out.println("agentAddr " + pduV1.getAgentAddress());
                System.out.println("enterprise " + pduV1.getEnterprise());
                System.out.println("timeStamp " + pduV1.getTimestamp());
                System.out.println("genericTrap " + pduV1.getGenericTrap());
                System.out.println("specificTrap " + pduV1.getSpecificTrap());
                System.out.println("snmpVersion " + PDU.V1TRAP);
                System.out.println("communityString " + new String(crEvent.getSecurityName()));
                break;

            case PDU.TRAP:
                System.out.println();
                System.out.println("===== NEW SNMP 2/3 TRAP RECEIVED ====");
                System.out.println("errorStatus " + pdu.getErrorStatus());
                System.out.println("errorIndex " + pdu.getErrorIndex());
                System.out.println("requestID " + pdu.getRequestID());
                System.out.println("snmpVersion " + PDU.TRAP);
                System.out.println("communityString " + new String(crEvent.getSecurityName()));
                break;
        }

        List<? extends VariableBinding> varBinds = pdu.getVariableBindings();
        if (varBinds != null && !varBinds.isEmpty()) {

            for (VariableBinding vb : varBinds) {
                String syntaxString = vb.getVariable().getSyntaxString();
                int syntax = vb.getVariable().getSyntax();
                System.out.println("OID: " + vb.getOid());
                System.out.println("Value: " + vb.getVariable());
                System.out.println("syntaxstring: " + syntaxString);
                System.out.println("syntax: " + syntax);
                System.out.println("------");
            }
        }
        System.out.println("==== TRAP END ===");
    }
}
