package com.github.sdnwiselab.siot.cli;

import com.github.sdnwiselab.siot.SIoTCastService;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.onlab.packet.*;
import org.onosproject.cli.AbstractShellCommand;
import org.onosproject.net.Host;
import org.onosproject.net.host.HostService;

@Command(
        scope = "onos",
        name = "joinGroup",
        description = "Join a Publisher"
)
public class SubscribeGroupCommand extends AbstractShellCommand {

    @Argument(
            index = 0,
            name = "channelId",
            description = "channel ID of source",
            required = false,
            multiValued = false
    )
    private String channelId = null;
    @Argument(
            index = 1,
            name = "relation",
            description = "desired relation",
            required = false,
            multiValued = false
    )
    private String relation = null;
    @Argument(
            index = 2,
            name = "publisher",
            description = "desired publisher",
            required = false,
            multiValued = false
    )
    private String publisher = null;

    private HostService hostService;
    private SIoTCastService service;

    protected void execute() {

            this.service = get(SIoTCastService.class);
            this.hostService= get(HostService.class);
            Host sourceHost= service.getHostById(this.channelId);
            Host destinationHost = service.getHostById(this.publisher);
            Ethernet uniPacket= packetForTheSIOTJoin(sourceHost,destinationHost); //CASO DI JOIN AL GRUPPO
            this.service.sendUniPacketToHost(uniPacket,sourceHost,destinationHost);

    }

    //RELATIONSHIP SELECTOR
    public int getTypeOfRelationship(String relation) {
        int port;
        switch (relation) {
            case "OOR": port=5001;
                break;
            case "SOR": port=5002;
                break;
            case "CWOR": port=5003;
                break;
            case "CLOR": port=5004;
                break;
            default:
                throw new IllegalArgumentException("Invalid relationship");
        }
        return port;
    }

    //CREA IL PACCHETTO DA INVIARE PER LA GENERAZIONE DEL GRUPPO MULTICAST SIOT
    public Ethernet packetForTheSIOTJoin (Host sourceHost, Host destinationHost){
        int port=getTypeOfRelationship(relation);
        //PAYLOAD
        String sendString = "SIOT JOIN PACKET";

        //PRIORITA' E TTL
        Integer prior = 1000;
        Integer iden = 777;
        Integer ttl = 110;

        //UDP
        UDP udp = new UDP();
        udp.setDestinationPort(port);
        udp.setSourcePort(33515);
        udp.setPayload(new Data(sendString.getBytes()));

        //ETHERNET
        Ethernet packet = new Ethernet();
        packet.setSourceMACAddress(sourceHost.mac());
        packet.setDestinationMACAddress(destinationHost.mac());
        packet.setEtherType(Ethernet.TYPE_IPV4);
        packet.setPriorityCode(prior.byteValue());

        //IPv4

        IPv4 ip = new IPv4();
        ip.setTtl(ttl.byteValue());
        ip.setIdentification(iden.shortValue());
        ip.setProtocol(IPv4.PROTOCOL_UDP);
        String sourceIpAddr = sourceHost.ipAddresses().toString();
        String sourceIpAddress = sourceIpAddr.substring(1, sourceIpAddr.length() - 1);
        String destinationIpAddr = destinationHost.ipAddresses().toString();
        String destinationIpAddress = destinationIpAddr.substring(1, sourceIpAddr.length() - 1);
        ip.setDestinationAddress(destinationIpAddress);
        ip.setSourceAddress(sourceIpAddress);
        ip.setPayload(udp);

        //PACCHETTO
        packet.setPayload(ip);
        return packet;
    }
}









