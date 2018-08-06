package com.github.sdnwiselab.siot.cli;


import com.github.sdnwiselab.siot.SIoTCastService;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.onlab.packet.*;
import org.onosproject.cli.AbstractShellCommand;
import org.onosproject.net.Host;
import org.onosproject.net.host.HostService;

import java.util.ArrayList;
import java.util.List;

@Command(
        scope = "onos",
        name = "getRelationsFromId",
        description = "Return the relations of the channel"
)
public class GetRelationsByIdCommand extends AbstractShellCommand {

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
            name = "hops",
            description = "desired social distance",
            required = false,
            multiValued = false
    )

    private String hop = null;

    private HostService hostService;
    private SIoTCastService service;

    protected void execute() {
        try {
            this.service = get(SIoTCastService.class);
            this.hostService= get(HostService.class);
           /* for(Host host: hostService.getHosts()){
                ids.add(host.id().toString());
            }*/
           // int areas=this.service.getAreasFromId(this.channelId);
           // ids=this.service.getIpsFromId(this.channelId,areas,relation );
            Host sourceHost= service.getHostById(this.channelId);
            Ethernet uniPacket= packetForTheSIOTFlowRule(sourceHost); //CASO DI CREAZIONE DEL GRUPPO
            this.service.sendPacketForFlowRule(uniPacket,sourceHost);

        //caso unicast
          /*for (String id: ids){
                Host ho= this.service.getHostById(id);

                if (! this.channelId.equals( ho.id().toString())){
                    Ethernet uniPacket= packetCreation(sourceHost,ho);   //CASO UNOICAST
                    this.service.sendUniPacketToHost(uniPacket,sourceHost,ho);
                }
            }*/

        }
        catch (Exception var) {
            var.printStackTrace();
        }
    }

    //RELATIONSHIP SELECTOR
    public String getTypeOfRelationship(String relation) {
        String portA;
        switch (relation) {
            case "OOR": portA="51";
                break;
            case "SOR": portA="52";
                break;
            case "CWOR": portA="53";
                break;
            case "CLOR": portA="54";
                break;
            default:
                throw new IllegalArgumentException("Invalid relationship");
        }
        return portA;
    }

    //HOPS SELECTOR
    public String getNumberOfHop(String hop) {
       String portB;
        switch (hop) {
            case "1": portB="01";
                break;
            case "2": portB="02";
                break;
            case "3": portB="03";
                break;
            case "4": portB="04";
                break;
            default:
                throw new IllegalArgumentException("Invalid number of hops");
        }
        return portB;
    }

    //CREA IL PACCHETTO DA INVIARE PER LA GENERAZIONE DEL GRUPPO MULTICAST SIOT
    public Ethernet packetForTheSIOTFlowRule (Host sourceHost){
       String portA=getTypeOfRelationship(relation);
       String portB=getNumberOfHop(hop);
       int port= Integer.parseInt(portA+portB);
        //PAYLOAD
        String sendString = "SIOT PACKET";

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
        packet.setDestinationMACAddress(MacAddress.valueOf("A5:23:05:00:00:01"));
        packet.setEtherType(Ethernet.TYPE_IPV4);
        packet.setPriorityCode(prior.byteValue());

        //IPv4

        IPv4 ip = new IPv4();
        ip.setTtl(ttl.byteValue());
        ip.setIdentification(iden.shortValue());
        ip.setProtocol(IPv4.PROTOCOL_UDP);
        String sourceIpAddr = sourceHost.ipAddresses().toString();
        String sourceIpAddress = sourceIpAddr.substring(1, sourceIpAddr.length() - 1);
        String destIpAddress =("151.97.13.77");
        ip.setDestinationAddress(destIpAddress);
        ip.setSourceAddress(sourceIpAddress);
        ip.setPayload(udp);

        //PACCHETTO
        packet.setPayload(ip);
        return packet;

    }


    /* *************************************UNUSED************************************************************************
    //CREA IL PACCHETTO DA INVIARE NEL CASO UNICAST
    public Ethernet packetCreation (Host sourceHost, Host destinationHost){

        //PAYLOAD
        String sendString = "SIOT PACKET";

        //PRIORITA' E TTL
        Integer prior = 1000;
        Integer iden = 777;
        Integer ttl = 110;

        //UDP
        UDP udp = new UDP();
        udp.setDestinationPort(5001);
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
        String destIpAddr = destinationHost.ipAddresses().toString();
        String destIpAddress = destIpAddr.substring(1, destIpAddr.length() - 1);
        ip.setDestinationAddress(destIpAddress);
        ip.setSourceAddress(sourceIpAddress);
        ip.setPayload(udp);

        //PACCHETTO
        packet.setPayload(ip);
        return packet;
    }
*/

}









