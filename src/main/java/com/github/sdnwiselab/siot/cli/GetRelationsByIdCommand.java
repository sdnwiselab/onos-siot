package com.github.sdnwiselab.siot.cli;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.flow.*;
import org.onosproject.net.intent.HostToHostIntent;
import org.onosproject.net.intent.IntentService;
import org.onosproject.net.intent.PathIntent;
import org.onosproject.net.packet.*;
import org.onlab.packet.*;
import org.onosproject.net.*;
import org.onosproject.net.Device;
import org.onosproject.net.host.HostService;
import com.github.sdnwiselab.siot.SIoTCastService;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.onosproject.cli.AbstractShellCommand;
import org.onosproject.net.mcast.McastRoute;
import org.onosproject.net.mcast.MulticastRouteService;
import org.slf4j.Logger;
import java.nio.ByteBuffer;
import java.util.*;

import static org.onlab.packet.Ethernet.TYPE_ARP;
import static org.onlab.packet.Ethernet.TYPE_IPV4;
import static org.slf4j.LoggerFactory.getLogger;

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

    private HostService hostService;
    private CoreService coreService;
    private FlowRuleService flowRuleService;
    private SIoTCastService service;
    private DeviceService devService;
    private ChannelIdCompleter channelIdCompleter;
    private RelationCompleter relationCompleter;
    private List<String> ids= new ArrayList<>();
    private static Map<String, String> mGroupMap = new HashMap<>();
    private final Logger log = getLogger(getClass());
    private ApplicationId appId;
    Set<DeviceId> collectionOfDevices= new HashSet<DeviceId>();
    Map<HostId,PortNumber> mapHostPort=new HashMap<HostId,PortNumber>();
    Host virtualHost;

    protected void execute() {
        try {
            this.service = get(SIoTCastService.class);
            this.hostService= get(HostService.class);
            this.devService=get(DeviceService.class);
            for(Host host: hostService.getHosts()){
              ids.add(host.id().toString());
            }


            int areas=this.service.getAreasFromId(this.channelId);
            //ids=this.service.getIpsFromId(this.channelId,areas );
            System.out.println(ids.toString());
            Host sourceHost= service.getHostById(this.channelId);
            Ethernet uniPacket= packetForTheSIOTFlowRule(sourceHost); //CASO DI CREAZIONE DEL GRUPPO
            this.service.sendPacketForFlowRule(uniPacket,sourceHost);
        //caso unicast
        /*  for (String id: ids){

                Host ho= this.service.getHostById(id);

                if (! this.channelId.equals( ho.id().toString())){
                    Ethernet uniPacket= packetCreation(sourceHost,ho);   //CASO UNOICAST
                    this.service.sendUniPacketToHost(uniPacket,sourceHost,ho);

                }
            }*/

          //CASO SIOTCAST
            System.out.println("Now Creating the SIot Multicast Group.....");
            String ipGroup = MGroupIpsHandler(ids.get(0)+this.relation);
            List<Device> allDevice = new ArrayList<>();

            for( Device dev :devService.getAvailableDevices()) {
                allDevice.add(dev);
            }

            for (String id: ids){
                Host host = this.service.getHostById(id);
                Device dev = this.service.deviceConnectedToHost(id);
                allDevice.remove(dev);
                collectionOfDevices.add(dev.id());
                PortNumber portNumber=host.location().port();
                mapHostPort.put(host.id(), portNumber);
                virtualHost =this.service.getVirtualHostFromDevice(dev.id());
               // this.service.setSiotFlowRule(dev,ipGroup,host);
                createMulticastGroup(virtualHost,ipGroup); }

             for (DeviceId devId: collectionOfDevices) {
                 service.creazioneIntent(devId, ipGroup,mapHostPort,allDevice);
             }


            System.out.println("....SIot Multicast Group created!");



            Thread.sleep(5000);
            Ethernet multiPacket=multiPacketCreation(sourceHost, ipGroup );
            this.service.sendMultiPacketToHost(multiPacket,sourceHost,ipGroup);

        }
        catch (Exception var) {
            var.printStackTrace();
        }
    }

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

    //CREA IL PACCHETTO DA INVIARE NEL CASO SIOTCAST
    public Ethernet multiPacketCreation (Host sourceHost, String groupIp){


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

        //Ethernet
        String destMAC="01:00:5e:0b:01:02";
        Ethernet packet = new Ethernet();
        packet.setSourceMACAddress(sourceHost.mac());
        packet.setDestinationMACAddress(destMAC);
        packet.setEtherType(Ethernet.TYPE_IPV4);
        packet.setPriorityCode(prior.byteValue());

        //IPv4
        IPv4 ip = new IPv4();
        ip.setTtl(ttl.byteValue());
        ip.setIdentification(iden.shortValue());
        ip.setProtocol(IPv4.PROTOCOL_UDP);
        String sourceIpAddr = sourceHost.ipAddresses().toString();
        String sourceIpAddress = sourceIpAddr.substring(1, sourceIpAddr.length() - 1);
        ip.setDestinationAddress(groupIp);
        ip.setSourceAddress(sourceIpAddress);
        ip.setPayload(udp);

        //PACCHETTO
        packet.setPayload(ip);
        return packet;
    }

    //GESTISCE L'INDIRIZZO IP MULTICAST
    protected String  MGroupIpsHandler(String ipSourceRelation){
        String ipGroup ="224.11.1.2";

        if (mGroupMap.get(ipSourceRelation)!=null){
            return mGroupMap.get(ipSourceRelation);
        }
        else {
            mGroupMap.put(ipSourceRelation, ipGroup);
            return mGroupMap.get(ipSourceRelation);
        }
    }

    //CREA IL GRUPPO MULTICAST
    protected void createMulticastGroup(Host virtualHost, String ipGroup) {
        String multicastGroupIpAddr =ipGroup;
        String ingressPort= null;
        String[] ports = null;
        MulticastRouteService mcastRouteManager = get(MulticastRouteService.class);


        IpAddress virtualHostIp = virtualHost.ipAddresses().iterator().next();
        McastRoute mRoute = new McastRoute(virtualHostIp,
                IpAddress.valueOf( multicastGroupIpAddr), McastRoute.Type.STATIC);
        mcastRouteManager.add(mRoute);

        if (ingressPort != null) {
            ConnectPoint ingress = ConnectPoint.deviceConnectPoint(ingressPort);
            mcastRouteManager.addSource(mRoute, ingress);
        }

        if (ports != null) {
            for (String egCP : ports) {
                log.debug("Egress port provided: " + egCP);
                ConnectPoint egress = ConnectPoint.deviceConnectPoint(egCP);
                mcastRouteManager.addSink(mRoute, egress);

            }
        }
        print("Added the mcast route: %s", mRoute);
    }

    //CREA IL PACCHETTO DA INVIARE PER LA GENERAZIONE DEL GRUPPO MULTICAST SIOT
    public Ethernet packetForTheSIOTFlowRule (Host sourceHost){
        int port=getTypeOfRelationship(relation);
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
}









