

//package com.github.sdnwiselab.siot;
//
//import com.esotericsoftware.minlog.Log;
//import org.apache.felix.scr.annotations.Component;
//import org.apache.felix.scr.annotations.Reference;
//import org.apache.felix.scr.annotations.ReferenceCardinality;
//import org.apache.felix.scr.annotations.Service;
//import org.onlab.packet.IpAddress;
//import org.onlab.packet.MacAddress;
//import org.onlab.packet.VlanId;
//import org.onosproject.core.ApplicationId;
//import org.onosproject.core.CoreService;
//import org.onosproject.incubator.net.virtual.NetworkId;
//import org.onosproject.incubator.net.virtual.VirtualNetworkListener;
//import org.onosproject.incubator.net.virtual.VirtualNetworkService;
//import org.onosproject.net.*;
//import org.onosproject.net.driver.Behaviour;
//import org.onosproject.net.host.*;
//import org.onosproject.net.mcast.McastRoute;
//import org.onosproject.net.mcast.MulticastRouteService;
//import org.onosproject.net.provider.ProviderId;
//import org.slf4j.Logger;
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.*;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.net.MalformedURLException;
//import org.apache.felix.scr.annotations.*;
//import org.onosproject.event.*;
//import org.onosproject.net.device.DeviceService;
//import org.onosproject.incubator.net.virtual.VirtualHost;
//import org.onosproject.net.HostLocation;
//import static org.slf4j.LoggerFactory.getLogger;
//import org.onosproject.net.Port;
//import org.onosproject.net.PortNumber;
//import org.onosproject.net.provider.AbstractProvider;
//import java.util.EventListener;
//import java.util.concurrent.ThreadLocalRandom;
//
////creare classe che extend abstract provider implements hostprovideservice, con providerregistry la registro tra i provider e poi chiamo il detecthost.
//
//@Component(immediate = true)
//@Service
//public class SIoTCast implements SIoTCastService{
//
//    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
//    protected CoreService coreService;
//
//    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
//    protected DeviceService deviceService;
//
//    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
//    protected HostService hostService;
//
//    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
//    protected ListenerService eventService;
//
//    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
//    protected HostProviderRegistry hostProviderRegistry;
//
//    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
//    protected SiotProvider SiotProviderRegistry;
//
//    private final Logger log = getLogger(getClass());
//    protected InnerHostListener hostListener;
//    protected InnerDeviceListener deviceListener;
//    protected InnerVirtualHostListener eventListener;
//    public Map<String, Integer> IdAreas;
//
//
//   // @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
//   // protected VirtualNetworkService vnetService;
//
//    @Activate
//    protected void activate() {
//        log.info("//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////");
//
//        this.hostListener = new InnerHostListener();
//        this.deviceListener = new InnerDeviceListener();
//        this.eventListener= new InnerVirtualHostListener();
//
//        Set<ProviderId> providerIds = hostProviderRegistry.getProviders();
//
//        log.info("KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK"+providerIds.toString());
//
//        deviceService.addListener(deviceListener);
//        hostService.addListener(hostListener);
//        log.info("----------------------->>>>>>>> qui ci arrivo");
//        eventService.addListener(eventListener);
//        log.info("----------------------->>>>>>>> Started network reader and Listeners Activated.");
//        List<String> hostData= new LinkedList<String>();
//        IdAreas = new HashMap<>();
//        int n=0;
//       // NetworkId netid= NetworkId.networkId(1);
//
//
//
//        try {
//            log.info("//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////");
//
//            log.info(deviceService.getAvailableDevices().toString());
//
//            log.info("//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////");
//            for (Device dev : deviceService.getAvailableDevices()) {
//                log.info("//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////");
//                n++;
//                IpAddress prova =IpAddress.valueOf("10.0.1."+n);
//                Set<IpAddress> s  = new HashSet<>();
//                s.add(prova);
//
//                String ids= "virtualDevice"+n;
//                log.info("//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////");
//
//                boolean c= true;
//                byte[] b= new byte[6];
//                new Random().nextBytes(b);
//                log.info(dev.toString());
//                HostId hId= HostId.hostId(ids);
//                HostDescription hDes= new HostDescription() {
//                    @Override
//                    public MacAddress hwAddress() {
//                        MacAddress mAdd = new MacAddress(b);
//                        return  mAdd;
//                    }
//
//                    @Override
//                    public VlanId vlan() {
//                        return null;
//                    }
//
//                    @Override
//                    public HostLocation location() {
//                        HostLocation hloc= new HostLocation(dev.id(), PortNumber.portNumber(81), System.currentTimeMillis());
//                        return hloc;
//                    }
//
//                    @Override
//                    public Set<IpAddress> ipAddress() {
//                            return s;
//                    }
//
//                    @Override
//                    public SparseAnnotations annotations() {
//                        return null;
//                    }
//                };
//                log.info(hId.toString()+"$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//                SiotProviderRegistry.hostDetected(hId, hDes,c );
//                log.info(hId.toString()+"$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//
//
///*
//                VirtualHost virtualHost = new VirtualHost() {
//                    @Override
//                    public NetworkId networkId() {
//                        return netid;
//                    }
//
//                    @Override
//                    public HostId id() {
//                        HostId hId= HostId.hostId(ids);
//                        log.info(hId.toString()+"$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//                        return hId;
//                    }
//
//                    @Override
//                    public MacAddress mac() {
//                        MacAddress mAdd = new MacAddress(b);
//                        return  mAdd;
//                    }
//
//
//                    @Override
//                    public VlanId vlan() {
//                        return null;
//                    }
//
//                    @Override
//                    public Set<IpAddress> ipAddresses() {
//                        return s;
//
//                    }
//
//                    @Override
//                    public HostLocation location() {
//                        HostLocation hloc= new HostLocation(dev.id(), PortNumber.portNumber(81), System.currentTimeMillis()), s;
//                        return hloc;
//                    }
//
//                    @Override
//                    public Annotations annotations() {
//                        return null;
//                    }
//
//                    @Override
//                    public ProviderId providerId() {
//                        return null;
//                    }
//
//                    @Override
//                    public <B extends Behaviour> B as(Class<B> aClass) {
//                        return null;
//                    }
//
//                    @Override
//                    public <B extends Behaviour> boolean is(Class<B> aClass) {
//                        return false;
//                    }
//                };
//                       /* NetworkId.networkId(1),
//                        HostId.hostId("unacosa"),
//                        new MacAddress(new byte[]{1, 2, 3, 4, 5, 6}),
//                        VlanId.vlanId(),
//                        new HostLocation(dev.id(), PortNumber.portNumber(8181), System.currentTimeMillis()), s) */
//
//
//          /*  log.info(virtualHost.networkId().toString()+"<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
//            log.info(virtualHost.ipAddresses().toString()+"<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
//            log.info(virtualHost.mac().toString()+"<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
//           // log.info(vnetService.getVirtualHosts(netid)+"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");*/
//            }
//
//
//            for (Host ho : hostService.getHosts()) {
//
//               // int randomNum = ThreadLocalRandom.current().nextInt(0, 5);
//                IdAreas.put(ho.id().toString(),0 );
//                String ipAddr= ho.ipAddresses().toString();
//                String ipAddress= ipAddr.substring(1, ipAddr.length()-1);
//
//                hostData.add("{\"UID\":\""+ho.id().toString()+"\",\"areas\":[0,0,0],\"meta\":\"Sensor\",\"ip\":\""+ipAddress+"\"}");
//
//            }
//
//
//            log.info(hostData.toString());
//            postSIoTCast(hostData);
//
//
//           // for (VirtualHost vh : vnetService.getVirtualHosts(netid)){
//                log.info( "QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ");
//
//          //  }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    @Deactivate
//    protected void deactivate() {
//        log.info("Stopped network reader");
//        hostService.removeListener(hostListener);
//        log.info("Listener stopped");
//
//    }
//
//
//    public static void postSIoTCast(List<String> hostData) {
//
//        try {
//
//            URL url = new URL("http://151.97.13.151:8080/Sim/SIoT/Server/List");
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setDoOutput(true);
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("Content-Type", "application/json");
//
//            String input ="{\"entities\":\n"+hostData.toString()+"}";
//            OutputStream os = conn.getOutputStream();
//            os.write(input.getBytes());
//            os.flush();
//
//            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
//                throw new RuntimeException("Failed : HTTP error code : "
//                        + conn.getResponseCode());
//            }
//
//            BufferedReader br = new BufferedReader(new InputStreamReader(
//                    (conn.getInputStream())));
//
//            String output;
//            System.out.println("Output from Server .... \n");
//            while ((output = br.readLine()) != null) {
//                System.out.println(output);
//            }
//
//            conn.disconnect();
//
//        } catch (MalformedURLException e) {
//
//            e.printStackTrace();
//
//        } catch (IOException e) {
//
//            e.printStackTrace();
//
//        }
//
//    }
//
//    public List <String>getIpsFromId(String relation, String srcId, int areas){
//
//        List <String> finalOutput = new ArrayList<>();
//
//        try {
//
//            URL url = new URL("http://151.97.13.151:8080/Sim/SIoT/Server/IpList");
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setDoOutput(true);
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("Content-Type", "application/json");
//            //String[] input = new String[] {relation,srcIp, Integer.toString(areas)};
//            String finalInput= "{\"rel\":\""+relation+"\",\"id\":\""+srcId+"\",\"area\":"+Integer.toString(areas)+"}";
//            OutputStream os = conn.getOutputStream();
//            os.write(finalInput.getBytes());
//            os.flush();
//
//
//
//            BufferedReader br = new BufferedReader(new InputStreamReader(
//                    (conn.getInputStream())));
//
//
//            String output;
//            System.out.println("Output from Server .... \n");
//            while ((output = br.readLine()) != null) {
//                System.out.println(output);
//            }
//            finalOutput= Arrays.asList(output.split(","));
//            conn.disconnect();
//
//
//        }
//        catch (MalformedURLException e) { e.printStackTrace(); }
//        catch (IOException e) { e.printStackTrace();}
//        return finalOutput;
//    }
//
//    public List<String> getIdChannels() throws Exception {
//        List<String> channelsID = new ArrayList<String>();
//        for ( String key : IdAreas.keySet() ) {
//            channelsID.add(key);
//        }
//        return channelsID;
//    }
//
//    public Integer getAreasFromId(String id)  {
//        int areas =IdAreas.get(id);
//        return areas;
//    }
//
//   /* public void createHost(HostId hostId, HostLocation location, IpAddress hostIp) {
//        DefaultHostDescription description =new DefaultHostDescription(hostId.mac(), hostId.vlanId(), location, hostIp);
//        //hostProviderService.hostDetected(hostId, description, false);
//        log.info("VIRTUAL HOST CREATED WITH ID--------------------->"+hostId.toString());
//    }*/
//
//
//
//
//}


package com.github.sdnwiselab.siot;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.onlab.packet.*;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.net.*;
import org.onosproject.net.config.NetworkConfigEvent;
import org.onosproject.net.config.NetworkConfigListener;
import org.onosproject.net.config.NetworkConfigRegistry;
import org.onosproject.net.config.basics.BasicHostConfig;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.flow.*;
import org.onosproject.net.host.DefaultHostDescription;
import org.onosproject.net.host.HostDescription;
import org.onosproject.net.host.HostProvider;
import org.onosproject.net.host.HostProviderRegistry;
import org.onosproject.net.host.HostProviderService;
import org.onosproject.net.packet.PacketPriority;
import org.onosproject.net.provider.AbstractProvider;
import org.onosproject.net.provider.ProviderId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Host provider that uses network config service to discover hosts.
 */
@Component(immediate = true)
public class SIoTCast extends AbstractProvider implements HostProvider {
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected CoreService coreService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected HostProviderRegistry providerRegistry;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected DeviceService deviceService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    FlowRuleService flowRuleService;

    private ApplicationId appId;
    private static final String APP_NAME = "com.github.sdnwiselab.siot";
    private static final ProviderId PROVIDER_ID = new ProviderId("host", APP_NAME);
    protected HostProviderService providerService;

    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Creates an network config host location provider.
     */
    public SIoTCast() {
        super(PROVIDER_ID);
    }

    @Activate
    protected void activate() {
        appId = coreService.registerApplication(APP_NAME);
        providerService = providerRegistry.register(this);
        readInitialConfig();
        log.info("Started");

    }

    @Deactivate
    protected void deactivate() {
        flowRuleService.removeFlowRulesById(appId);
        providerRegistry.unregister(this);
        providerService = null;
        log.info("Stopped");
    }

    @Override
    public void triggerProbe(Host host) {
        /*
         * Note: All hosts are configured in network config host provider.
         * Therefore no probe is required.
         */
    }

    /**
     * Adds host information.
     * IP information will be appended if host exists.
     *
     * @param mac MAC address of the host
     * @param vlan VLAN ID of the host
     * @param location Location of the host
     * @param ips Set of IP addresses of the host
     */
    protected void addHost(MacAddress mac, VlanId vlan, HostLocation location, Set<IpAddress> ips) {
        HostId hid = HostId.hostId(mac, vlan);
        HostDescription desc = (ips != null) ?
                new DefaultHostDescription(mac, vlan, location, ips, true) :
                new DefaultHostDescription(mac, vlan, location, Collections.emptySet(), true);
        providerService.hostDetected(hid, desc, true);
    }



    /**
     * Updates host information.
     * IP information will be replaced if host exists.
     *
     * @param mac MAC address of the host
     * @param vlan VLAN ID of the host
     * @param location Location of the host
     * @param ips Set of IP addresses of the host
     */
    protected void updateHost(MacAddress mac, VlanId vlan, HostLocation location, Set<IpAddress> ips) {
        HostId hid = HostId.hostId(mac, vlan);
        HostDescription desc = new DefaultHostDescription(mac, vlan, location, ips, true);
        providerService.hostDetected(hid, desc, true);
    }


    /**
     * Removes host information.
     *
     * @param mac MAC address of the host
     * @param vlan VLAN ID of the host
     */
    protected void removeHost(MacAddress mac, VlanId vlan) {
        HostId hid = HostId.hostId(mac, vlan);
        providerService.hostVanished(hid);
    }

    private void readInitialConfig() {
        /*

        networkConfigRegistry.getSubjects(HostId.class).forEach(hostId -> {
            MacAddress mac = hostId.mac();
            VlanId vlan = hostId.vlanId();
            BasicHostConfig hostConfig =
                    networkConfigRegistry.getConfig(hostId, BasicHostConfig.class);
            Set<IpAddress> ipAddresses = hostConfig.ipAddresses();
            Set<HostLocation> locs = hostConfig.locations();
            if (locs != null) {
                Set<HostLocation> locations = locs.stream()
                        .map(hostLocation -> new HostLocation(hostLocation, System.currentTimeMillis()))
                        .collect(Collectors.toSet());
                VlanId innerVlan = hostConfig.innerVlan();
                EthType outerTpid = hostConfig.outerTpid();
                addHost(mac, vlan, locations, ipAddresses, innerVlan, outerTpid);
            } else {
                log.warn("Host {} configuration {} is missing locations", hostId, hostConfig);
            }
        });

        */

        IpAddress prova =IpAddress.valueOf("10.0.1.115");
        Set<IpAddress> s  = new HashSet<>();
        s.add(prova);
        int i = 0;
        for (Device d : deviceService.getAvailableDevices() ) {
            i++;
            addHost(new MacAddress(new byte[]{1, 2, 3, 4, 5, (byte)i}), VlanId.vlanId("None"),
                    new HostLocation(d.id(), PortNumber.portNumber(81), System.currentTimeMillis()),
                    s);


            TrafficSelector selector = DefaultTrafficSelector.builder()
                    .matchEthType(Ethernet.TYPE_IPV4)
                    //.matchIPDst(IpPrefix.valueOf("10.0.1.115/24"))
                    .build();
            TrafficTreatment treatment = DefaultTrafficTreatment.builder()
                    .setIpDst(IpAddress.valueOf("10.0.0.1"))
                    .setOutput(PortNumber.portNumber(1))
                    .build();

            FlowRule fr = DefaultFlowRule.builder()
                    .forDevice(d.id())
                    .withSelector(selector)
                    .withTreatment(treatment)
                    .withPriority(PacketPriority.REACTIVE.priorityValue())
                    .makePermanent()
                    .fromApp(appId).build();

            flowRuleService.applyFlowRules(fr);
        }
    }
}
