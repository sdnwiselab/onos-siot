package com.github.sdnwiselab.siot;

import com.esotericsoftware.minlog.Log;
import com.google.common.collect.Maps;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.onlab.packet.*;
import org.onosproject.cluster.ControllerNode;
import org.onosproject.cluster.DefaultControllerNode;
import org.onosproject.cluster.NodeId;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.core.GroupId;
import org.onosproject.event.EventListener;
import org.onosproject.incubator.net.intf.InterfaceService;
import org.onosproject.incubator.net.routing.RouteService;
import org.onosproject.net.*;
import org.onosproject.net.behaviour.ControllerConfig;
import org.onosproject.net.behaviour.ControllerInfo;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.flow.*;
import org.onosproject.net.flow.criteria.Criterion;
import org.onosproject.net.flow.instructions.Instruction;
import org.onosproject.net.flow.instructions.Instructions;
import org.onosproject.net.host.DefaultHostDescription;
import org.onosproject.net.host.HostDescription;
import org.onosproject.net.host.HostProvider;
import org.onosproject.net.host.HostProviderRegistry;
import org.onosproject.net.host.HostProviderService;
import org.onosproject.net.intent.IntentService;
import org.onosproject.net.intent.Key;
import org.onosproject.net.intent.SinglePointToMultiPointIntent;
import org.onosproject.net.packet.*;
import org.onosproject.net.provider.AbstractProvider;
import org.onosproject.net.provider.ProviderId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.felix.scr.annotations.Service;
import org.onlab.packet.IpAddress;
import org.onlab.packet.MacAddress;
import org.onlab.packet.VlanId;
import org.onosproject.net.host.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

import org.onosproject.event.*;
import org.onosproject.net.HostLocation;
import static org.slf4j.LoggerFactory.getLogger;
import org.onosproject.net.PortNumber;

import javax.sound.midi.ControllerEventListener;
import javax.sound.midi.ShortMessage;


@Component(immediate = true)
@Service
public class SIoTCast implements SIoTCastService {

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected CoreService coreService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected HostProviderRegistry providerRegistry;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    FlowRuleService flowRuleService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    IntentService intetnService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected DeviceService deviceService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected HostService hostService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected PacketService packetService;


    private final Logger log = getLogger(getClass());
    protected SIoTVirtualHostProvider siotVirutalHostProvider;
    protected InnerHostListener hostListener;
    protected InnerDeviceListener deviceListener;
    private PacketProcessor packetProcessor;
    private ApplicationId appId;
    private static final String APP_NAME = "com.github.sdnwiselab.siot";
    protected HostProviderService providerService;

    public Map<HostId, DeviceId> virtualHostMap = new HashMap<HostId, DeviceId>();
    public Map<HostId, String> hostIdentificationMap = new HashMap<HostId, String>();
    private List<HostId> virtualHostList = new LinkedList<HostId>();
    List<Host> hostsListToDevice = new ArrayList<>();
    Host virtualHost;
    protected Map<DeviceId, Map<MacAddress, PortNumber>> macTables = Maps.newConcurrentMap();
    public Map<String, Integer> IdAreas;
    public String relationship;

    @Activate
    protected void activate() {
        log.info(ControllerNode.State.values().toString());
        appId = coreService.registerApplication(APP_NAME);
        this.siotVirutalHostProvider = new SIoTVirtualHostProvider();
        this.hostListener = new InnerHostListener();
        this.deviceListener = new InnerDeviceListener();
        deviceService.addListener(deviceListener);
        hostService.addListener(hostListener);
        providerService = providerRegistry.register(siotVirutalHostProvider);
        packetProcessor = new SwitchPacketProcesser();
        packetService.addProcessor(packetProcessor, PacketProcessor.director(3));
        packetService.requestPackets(DefaultTrafficSelector.builder()
                .matchEthType(Ethernet.TYPE_IPV4).build(), PacketPriority.REACTIVE, appId);

        List<String> hostData = new LinkedList<String>();
        IdAreas = new HashMap<>();
        int n = 0;

        log.info("----------------------->>>>>>>> Started network reader and Listeners Activated.");
        try {
            log.info("////////////DEVICES////////////////DEVICES//////////DEVICES/////////////DEVICES//////////////DEVICES//////////////////////////////");
            log.info(deviceService.getAvailableDevices().toString());
            log.info("//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////");

            for (Device dev : deviceService.getAvailableDevices()) {
                System.out.println(dev.id().toString());
                n++;
                createVirtualHost(dev,n);
                siotFlowRuleHandler(dev);
            }

            for (Host ho : hostService.getHosts()) {
                if (virtualHostList.contains(ho.id())) {
                    log.info(ho.id().toString() + "  is a virtual host");
                } else {
                    //int randomNum = ThreadLocalRandom.current().nextInt(0, 5);
                    IdAreas.put(ho.id().toString(), 0);
                    String ipAddr = ho.ipAddresses().toString();
                    String ipAddress = ipAddr.substring(1, ipAddr.length() - 1);

                    hostData.add("{\"UID\":\"" + ho.id().toString() + "\",\"areas\":[0,0,0],\"meta\":\"Sensor\",\"ip\":\"" + ipAddress + "\"}");
                    hostIdentificationMap.put(ho.id(), ipAddress);
                }
            }


            log.info("//////HOSTS////////////////HOSTS//////////////////HOSTS///////////HOSTS///////////HOSTS////////////HOSTS//////////////HOSTS///////");
            log.info(hostData.toString());
            log.info("//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////");
            postSIoTCast(hostData);

            for (Device dev : deviceService.getAvailableDevices()) {
                getVirtualHostFromDevice(dev.id());
            }


        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.toString());
        }

    }

    @Deactivate
    protected void deactivate() {

        hostService.removeListener(hostListener);
        deviceService.removeListener(deviceListener);
        log.info("Listeners stopped");
        for (Map.Entry<HostId, DeviceId> entry : virtualHostMap.entrySet()) {
            HostId hid = entry.getKey();
            providerService.hostVanished(hid);

        }
        log.info("Virtual Hosts deleted");
        flowRuleService.removeFlowRulesById(appId);
        providerRegistry.unregister(siotVirutalHostProvider);
        providerService = null;
        packetService.removeProcessor(packetProcessor);
        log.info("Stopped network reader");

    }


    //MI DA GLI HOST A PARTIRE DAL LORO ID
    public Host getHostById(String id) {
        Host ho = hostService.getHost(HostId.hostId(id));
        return ho;

    }

    //CONNESSIONE CON IL SERVER SIOT E FORNISCE LE INFORMAZIONI SULLA RETE
    public void postSIoTCast(List<String> hostData) {

        try {

            URL url = new URL("http://151.97.13.230:8080/Sim/SIoT/Server/List");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            String input = "{\"entities\":\n" + hostData.toString() + "}";
            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }

            conn.disconnect();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    //MI RITORNA IL DEVICE CONNESSO ALL'HOST
    public Device deviceConnectedToHost(String id) {
        Host ho = hostService.getHost(HostId.hostId(id));
        Device dev = deviceService.getDevice(ho.location().deviceId());
        return dev;

    }

    //MI RITORNA TUTTI GLI HOST CONNESSI AL DEVICE
    public List<Host> allHostsConnectedToDevice(Device dev) {

        Set<Host> hostConnectedToDevice = hostService.getConnectedHosts(dev.id());
        hostsListToDevice.addAll(hostConnectedToDevice);

        for (HostId hoid : virtualHostList) {
            Host virtualHost = hostService.getHost(hoid);
            hostsListToDevice.remove(virtualHost);
        }
        return hostsListToDevice;
    }

    //CHIEDE AL SERVER SIOT LA LISTA DI HOST CHE VERIFICANO AL RELAZIONE SOCIALE
    public List<String> getIpsFromId( String srcId, int areas) {

        List<String> finalOutput = new ArrayList<>();

        try {

            URL url = new URL("http://151.97.13.230:8080/Sim/SIoT/Server/IpList");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            //String[] input = new String[] {relation,srcIp, Integer.toString(areas)};
            String finalInput = "{\"rel\":\"" + relationship + "\",\"id\":\"" + srcId + "\",\"area\":" + Integer.toString(areas) + "}";
            OutputStream os = conn.getOutputStream();
            os.write(finalInput.getBytes());
            os.flush();


            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));


            String output;
            System.out.println("Output from Server .... ");

            while ((output = br.readLine()) != null) {
                output = output.substring(1, output.length() - 1);
                output = output.replace(" ", "");
                finalOutput = Arrays.asList(output.split(","));
                System.out.println(finalOutput.toString() + "\n");

            }
            conn.disconnect();


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(finalOutput);
        return finalOutput;
    }

    //MAPPA GLI HOST CON LE AREE
    public List<String> getIdChannels() throws Exception {
        List<String> channelsID = new ArrayList<String>();
        for (String key : IdAreas.keySet()) {
            channelsID.add(key);
        }
        return channelsID;
    }

    //RITORNA L'AREA A PARTIRE DALL'HOST
    public Integer getAreasFromId(String id) {
        int areas = IdAreas.get(id);
        return areas;
    }

    //CREA GLI HOST VIRTUALI
    public Map<HostId, DeviceId> createVirtualHost(Device dev, int n) {
        IpAddress prova = IpAddress.valueOf("10.0.1." + n);
        Set<IpAddress> s = new HashSet<>();
        s.add(prova);
        byte[] b = new byte[6];
        new Random().nextBytes(b);
        MacAddress mAdd = new MacAddress(b);
        VlanId vlanid = VlanId.vlanId("None");
        HostLocation hloc = new HostLocation(dev.id(), PortNumber.portNumber(81), System.currentTimeMillis());


        HostId hid = HostId.hostId(mAdd, vlanid);
        HostDescription desc = (s != null) ?
                new DefaultHostDescription(mAdd, vlanid, hloc, s, true) :
                new DefaultHostDescription(mAdd, vlanid, hloc, Collections.emptySet(), true);

        providerService.hostDetected(hid, desc, true);
        virtualHostMap.put(hid, dev.id());
        virtualHostList.add(hid);


        return virtualHostMap;

    }

    //MI DA IL VIRTUAL HOST OLLEGATO AL DEVICE
    public Host getVirtualHostFromDevice(DeviceId devid) {

        for (Map.Entry entry : virtualHostMap.entrySet()) {
            if (devid.equals(entry.getValue())) {
                virtualHost = hostService.getHost(HostId.hostId(entry.getKey().toString()));
                break; //breaking because its one to one map
            }
        }
        return virtualHost;
    }

    //CREA GLI INTENT PER IL MULTICAST
    public void creazioneIntent(DeviceId devId, String ipGroup, Map<HostId, PortNumber> mapHostPort, List<Device> dropDevice) {
        List<Port> ports = new ArrayList<>();
        IpPrefix ipAddressGroup = IpPrefix.valueOf(ipGroup + "/32");
        ports.addAll(deviceService.getPorts(devId));
        Set<ConnectPoint> scp = new HashSet<ConnectPoint>();
        PortNumber portNumber = null;

        for (Host ho : hostService.getConnectedHosts(devId)) {
            try {
                portNumber = mapHostPort.get(ho.id());
                ports.remove(deviceService.getPort(devId, portNumber));
                ConnectPoint cp = new ConnectPoint(devId, portNumber);
                scp.add(cp);
            } catch (Exception e) {
                ports.remove(deviceService.getPort(ho.location().deviceId(), ho.location().port()));
            }
        }
        if (ports.size() == 2) {
            ConnectPoint incp = new ConnectPoint(devId, ports.get(1).number());
            TrafficSelector selector = DefaultTrafficSelector.builder()
                    .matchEthType(Ethernet.TYPE_IPV4)
                    .matchIPDst(ipAddressGroup)
                    .build();
            TrafficTreatment treatment = DefaultTrafficTreatment.builder()
                    .setIpDst(IpAddress.valueOf("255.255.255.255"))
                    .drop()
                    .build();
            SinglePointToMultiPointIntent siotIntetn = SinglePointToMultiPointIntent.builder()
                    .appId(appId)
                    .priority(50000)
                    .selector(selector)
                    .treatment(treatment)
                    .egressPoints(scp)
                    .ingressPoint(incp)
                    .build();
            intetnService.submit(siotIntetn);
        }
        for (Device dev : dropDevice) {
            TrafficSelector selector = DefaultTrafficSelector.builder()
                    .matchEthType(Ethernet.TYPE_IPV4)
                    .matchIPDst(ipAddressGroup)
                    .build();
            TrafficTreatment treatment = DefaultTrafficTreatment.builder()
                    .drop()
                    .build();
            FlowRule fr = DefaultFlowRule.builder()
                    .forDevice(dev.id())
                    .withSelector(selector)
                    .withTreatment(treatment)
                    .withPriority(30000)
                    .makePermanent()
                    .fromApp(appId)
                    .build();
            flowRuleService.applyFlowRules(fr);
        }
    }

    //MANDA I PACCHETTI NEL CASO UNICAST
    public void sendUniPacketToHost(Ethernet packet, Host sourceHost, Host destinationHost) {

        DeviceId devIdSend = sourceHost.location().deviceId();
        List<Port> ports = deviceService.getPorts(devIdSend);
        List<Port>ports2=new ArrayList<>();
        ports2.addAll(ports);
        Set<Host> hostsDev= hostService.getConnectedHosts(devIdSend);

        String sourceIpAddr = sourceHost.ipAddresses().toString();
        String sourceIpAddress = sourceIpAddr.substring(1, sourceIpAddr.length() - 1);
        String destIpAddr = destinationHost.ipAddresses().toString();
        String destIpAddress = destIpAddr.substring(1, destIpAddr.length() - 1);

        for (Iterator<Port> portIter = ports2.iterator(); portIter.hasNext(); ) {
            Port port = portIter.next();

            for (Host host : hostsDev) {

                if (port.number().toString().equals(host.location().port().toString())) {

                    portIter.remove();
                }
            }
        }

        if(hostsDev.contains(destinationHost)){
            TrafficTreatment treatment = DefaultTrafficTreatment.builder()
                    .setIpSrc(IpAddress.valueOf(sourceIpAddress))
                    .setIpDst(IpAddress.valueOf(destIpAddress))
                    .setOutput(destinationHost.location().port()).build();

            OutboundPacket outboundPacket = new DefaultOutboundPacket(devIdSend,
                    treatment, ByteBuffer.wrap(packet.serialize()));

            packetService.emit(outboundPacket);

        }

        else {
            TrafficTreatment treatment = DefaultTrafficTreatment.builder()
                    .setIpSrc(IpAddress.valueOf(sourceIpAddress))
                    .setIpDst(IpAddress.valueOf(destIpAddress))
                    .setOutput(ports2.get(1).number()).build();

            OutboundPacket outboundPacket = new DefaultOutboundPacket(devIdSend,
                    treatment, ByteBuffer.wrap(packet.serialize()));

            packetService.emit(outboundPacket);
        }

    }

    //MANDA I PACCHETTI NEL CASO SIOTCAST
    public void sendMultiPacketToHost (Ethernet packet, Host sourceHost, String groupIp) {

        Device sendDev= deviceService.getDevice(DeviceId.deviceId("of:0000000000000001"));
        List<Port> ports = deviceService.getPorts(sendDev.id());
        String sourceIpAddr = sourceHost.ipAddresses().toString();
        String sourceIpAddress = sourceIpAddr.substring(1, sourceIpAddr.length() - 1);
        String destIpAddress = groupIp;

        for (Port poor : ports) {
            PortNumber portNumb = poor.number();
            TrafficTreatment treatment = DefaultTrafficTreatment.builder()
                    .setIpSrc(IpAddress.valueOf(sourceIpAddress))
                    .setIpDst(IpAddress.valueOf(destIpAddress))
                    .setOutput(portNumb).build();

            OutboundPacket outboundPacket = new DefaultOutboundPacket(sendDev.id(),
                    treatment, ByteBuffer.wrap(packet.serialize()));

            packetService.emit(outboundPacket);
        }


    }

    //CREA LA FLOW RULE PER LA GESTIONE DEI PACCHETTI DI CREAZIONE DEL GRUPPO SIOT
    public void siotFlowRuleHandler(Device dev){

        TrafficSelector selector = DefaultTrafficSelector.builder()
                .matchEthType(Ethernet.TYPE_IPV4)
                .matchIPDst(IpPrefix.valueOf("151.97.13.77/32"))
                .build();

        TrafficTreatment treatment = DefaultTrafficTreatment.builder()
                .setIpDst(IpAddress.valueOf("10.0.2.1"))
                .setEthDst(MacAddress.ONOS)
                .setOutput(PortNumber.CONTROLLER)
                .build();

        FlowRule fru = DefaultFlowRule.builder()
                .forDevice(dev.id())
                .withSelector(selector)
                .withTreatment(treatment)
                .withPriority(44000)
                .makePermanent()
                .fromApp(appId)
                .build();

        flowRuleService.applyFlowRules(fru);

        TrafficSelector selector2 = DefaultTrafficSelector.builder()
                .matchEthType(Ethernet.TYPE_IPV4)
                .matchIPDst(IpPrefix.valueOf("10.0.2.1/32"))
                .build();

        TrafficTreatment treatment2 = DefaultTrafficTreatment.builder()
                .drop()
                .build();

        FlowRule fru2 = DefaultFlowRule.builder()
                .forDevice(dev.id())
                .withSelector(selector2)
                .withTreatment(treatment2)
                .withPriority(44000)
                .makePermanent()
                .fromApp(appId)
                .build();
        flowRuleService.applyFlowRules(fru2);
    }

    //MANDA I PACCHETTI PER IL TEST DELLA FLOWRULE
    public void sendPacketForFlowRule(Ethernet packet, Host sourceHost) {

        List<Port> ports = deviceService.getPorts(sourceHost.location().deviceId());
        String sourceIpAddr = sourceHost.ipAddresses().toString();
        String sourceIpAddress = sourceIpAddr.substring(1, sourceIpAddr.length() - 1);
        String destIpAddress =("151.97.13.77");

        TrafficTreatment treatment = DefaultTrafficTreatment.builder()
                .setIpSrc(IpAddress.valueOf(sourceIpAddress))
                .setIpDst(IpAddress.valueOf(destIpAddress))
                .setOutput(ports.get(ports.size()-1).number()).build();

        OutboundPacket outboundPacket = new DefaultOutboundPacket(sourceHost.location().deviceId(),
                treatment, ByteBuffer.wrap(packet.serialize()));

        packetService.emit(outboundPacket);

    }

    //TESTING\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

   private class SwitchPacketProcesser implements PacketProcessor
    {

        @Override
        public void process(PacketContext pc) {

            String prova= "10.0.2.1";

            try {
                InboundPacket pkt = pc.inPacket();
                String[] parts = pkt.toString().split("\n");
                String[] ipSorceField = parts[6].split(" ");
                String[] ipDestinationField =parts[7].split(" ");
                String[] tcpPortDestField =parts[11].split(" ");
                String sourceIp= ipSorceField[1];
                String destIp = ipDestinationField[1];
                String destPort = tcpPortDestField[1].substring(0, tcpPortDestField[1].length() - 1);
                int port = Integer.parseInt(destPort);

               if (destIp.equals(prova)) {
                   switch (port) {
                       case 5001: relationship="OOR";
                       break;
                       case 5002: relationship="SOR";
                       break;
                       case 5003: relationship="CWOR";
                       break;
                       case 5004: relationship="CLOR";
                       break;
                   }
                   log.info("RICEZIONE DEL PACCHETTO DI CREAZIONE DELLA RELAZIONE "+relationship+" PER L'HOST "+sourceIp );
                }
                else{ return; }
            }
            catch(Exception e) {return;}}

            /*InboundPacket pkt = pc.inPacket();
           log.info(pkt.toString());
           log.info("changed.......................................................2");
           log.info("Received packet context: "+pkt.toString());
           log.info("Received cookie: "+pkt.cookie().toString());
           log.info("Received parsed packet: "+pkt.parsed().toString());
           log.info("Received unparsed packet: "+pkt.unparsed().toString());

            Ethernet ethPkt = pkt.parsed();



            if (ethPkt == null) {
                return;
            }


            initMacTable(pc.inPacket().receivedFrom());
           // actLikeSwitch(pc);

        }

       /* public void actLikeSwitch(PacketContext pc)
        {
            Short type = pc.inPacket().parsed().getEtherType();
            log.info("changed.......................................................11");
           log.info("Received packet context: "+pc.toString());
           log.info("Received cookie: "+pc.inPacket().cookie().toString());
           log.info("Received parsed packet: "+pc.inPacket().parsed().toString());
           log.info("Received unparsed packet: "+pc.inPacket().unparsed().toString());

            if (type != Ethernet.TYPE_IPV4)
            {
                log.info(pc.inPacket().parsed().toString());
                return;
            }

            ConnectPoint cp = pc.inPacket().receivedFrom();

            Map<MacAddress, PortNumber> macTable = macTables.get(cp.deviceId());
            MacAddress srcMac = pc.inPacket().parsed().getSourceMAC();
            MacAddress dstMac = pc.inPacket().parsed().getDestinationMAC();
            macTable.put(srcMac, cp.port());
            PortNumber outPort = macTable.get(dstMac);

            if (outPort != null)
            {
                pc.treatmentBuilder().setOutput(outPort);

                FlowRule fr = DefaultFlowRule.builder()
                        .withSelector(DefaultTrafficSelector.builder().matchEthDst(dstMac)
                                .matchEthSrc(srcMac)
                                .matchInPort(cp.port())
                                .build())
                        .withTreatment(DefaultTrafficTreatment.builder().setOutput(outPort).build())//drop().build())
                        .forDevice(cp.deviceId()).withPriority(10)
                        .makeTemporary(60)
                        .fromApp(appId).build();

                flowRuleService.applyFlowRules(fr);
                pc.send();

            }
            else
            {
                pc.treatmentBuilder().setOutput(PortNumber.FLOOD);
                pc.send();
            }
        }*/


        private void initMacTable(ConnectPoint cp) {
            macTables.putIfAbsent(cp.deviceId(), Maps.newConcurrentMap());

        }
    }




}










