package com.github.sdnwiselab.siot;

import com.esotericsoftware.minlog.Log;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;
import org.onlab.packet.IpAddress;
import org.onlab.packet.MacAddress;
import org.onlab.packet.VlanId;
import org.onosproject.incubator.net.virtual.NetworkId;
import org.onosproject.incubator.net.virtual.VirtualNetworkListener;
import org.onosproject.incubator.net.virtual.VirtualNetworkService;
import org.onosproject.net.*;
import org.onosproject.net.driver.Behaviour;
import org.onosproject.net.mcast.McastRoute;
import org.onosproject.net.mcast.MulticastRouteService;
import org.onosproject.net.provider.ProviderId;
import org.slf4j.Logger;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import org.apache.felix.scr.annotations.*;
import org.onosproject.event.*;
import org.onosproject.net.host.HostService;
import org.onosproject.net.device.DeviceService;
import org.onosproject.incubator.net.virtual.VirtualHost;
import org.onosproject.net.HostLocation;
import static org.slf4j.LoggerFactory.getLogger;
import org.onosproject.net.Port;
import org.onosproject.net.PortNumber;

import java.util.EventListener;
import java.util.concurrent.ThreadLocalRandom;



@Component(immediate = true)
@Service
public class SIoTCast implements SIoTCastService{
    private final Logger log = getLogger(getClass());
    protected InnerHostListener hostListener;
    protected InnerDeviceListener deviceListener;
    protected InnerVirtualHostListener eventListener;
    public Map<String, Integer> IdAreas;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected DeviceService deviceService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected HostService hostService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected ListenerService eventService;

   // @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
   // protected VirtualNetworkService vnetService;

    @Activate
    protected void activate() {

        this.hostListener = new InnerHostListener();
        this.deviceListener = new InnerDeviceListener();
        this.eventListener= new InnerVirtualHostListener();

        deviceService.addListener(deviceListener);
        hostService.addListener(hostListener);
        log.info("----------------------->>>>>>>> qui ci arrivo");
        eventService.addListener(eventListener);
        log.info("----------------------->>>>>>>> Started network reader and Listeners Activated.");
        List<String> hostData= new LinkedList<String>();
        IdAreas = new HashMap<>();
        int n=0;
        NetworkId netid= NetworkId.networkId(1);



        try {

            log.info(deviceService.getAvailableDevices().toString());


            for (Device dev : deviceService.getAvailableDevices()) {
                n++;
                IpAddress prova =IpAddress.valueOf("10.0.1."+n);
                Set<IpAddress> s  = new HashSet<>();
                s.add(prova);

                String ids= "virtualDevice"+n;
                byte[] b= new byte[6];
                new Random().nextBytes(b);
                log.info(dev.toString());




                VirtualHost virtualHost = new VirtualHost() {
                    @Override
                    public NetworkId networkId() {
                        return netid;
                    }

                    @Override
                    public HostId id() {
                        HostId hId= HostId.hostId(ids);
                        log.info(hId.toString()+"$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                        return hId;
                    }

                    @Override
                    public MacAddress mac() {
                        MacAddress mAdd = new MacAddress(b);
                        return  mAdd;
                    }


                    @Override
                    public VlanId vlan() {
                        return null;
                    }

                    @Override
                    public Set<IpAddress> ipAddresses() {
                        return s;

                    }

                    @Override
                    public HostLocation location() {
                        HostLocation hloc= new HostLocation(dev.id(), PortNumber.portNumber(81), System.currentTimeMillis()), s;
                        return hloc;
                    }

                    @Override
                    public Annotations annotations() {
                        return null;
                    }

                    @Override
                    public ProviderId providerId() {
                        return null;
                    }

                    @Override
                    public <B extends Behaviour> B as(Class<B> aClass) {
                        return null;
                    }

                    @Override
                    public <B extends Behaviour> boolean is(Class<B> aClass) {
                        return false;
                    }
                };
                       /* NetworkId.networkId(1),
                        HostId.hostId("unacosa"),
                        new MacAddress(new byte[]{1, 2, 3, 4, 5, 6}),
                        VlanId.vlanId(),
                        new HostLocation(dev.id(), PortNumber.portNumber(8181), System.currentTimeMillis()), s) */


            log.info(virtualHost.networkId().toString()+"<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
            log.info(virtualHost.ipAddresses().toString()+"<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
            log.info(virtualHost.mac().toString()+"<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
           // log.info(vnetService.getVirtualHosts(netid)+"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }


            for (Host ho : hostService.getHosts()) {

               // int randomNum = ThreadLocalRandom.current().nextInt(0, 5);
                IdAreas.put(ho.id().toString(),0 );
                String ipAddr= ho.ipAddresses().toString();
                String ipAddress= ipAddr.substring(1, ipAddr.length()-1);

                hostData.add("{\"UID\":\""+ho.id().toString()+"\",\"areas\":[0,0,0],\"meta\":\"Sensor\",\"ip\":\""+ipAddress+"\"}");

            }


            log.info(hostData.toString());
            postSIoTCast(hostData);


           // for (VirtualHost vh : vnetService.getVirtualHosts(netid)){
                log.info( "QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ");

          //  }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Deactivate
    protected void deactivate() {
        log.info("Stopped network reader");
        hostService.removeListener(hostListener);
        log.info("Listener stopped");

    }


    public static void postSIoTCast(List<String> hostData) {

        try {

            URL url = new URL("http://151.97.13.151:8080/Sim/SIoT/Server/List");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            String input ="{\"entities\":\n"+hostData.toString()+"}";
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

    public List <String>getIpsFromId(String relation, String srcId, int areas){

        List <String> finalOutput = new ArrayList<>();

        try {

            URL url = new URL("http://151.97.13.151:8080/Sim/SIoT/Server/IpList");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            //String[] input = new String[] {relation,srcIp, Integer.toString(areas)};
            String finalInput= "{\"rel\":\""+relation+"\",\"id\":\""+srcId+"\",\"area\":"+Integer.toString(areas)+"}";
            OutputStream os = conn.getOutputStream();
            os.write(finalInput.getBytes());
            os.flush();



            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));


            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }
            finalOutput= Arrays.asList(output.split(","));
            conn.disconnect();


        }
        catch (MalformedURLException e) { e.printStackTrace(); }
        catch (IOException e) { e.printStackTrace();}
        return finalOutput;
    }

    public List<String> getIdChannels() throws Exception {
        List<String> channelsID = new ArrayList<String>();
        for ( String key : IdAreas.keySet() ) {
            channelsID.add(key);
        }
        return channelsID;
    }

    public Integer getAreasFromId(String id)  {
        int areas =IdAreas.get(id);
        return areas;
    }

}
