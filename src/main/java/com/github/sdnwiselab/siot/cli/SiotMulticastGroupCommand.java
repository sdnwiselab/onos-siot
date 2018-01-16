
package com.github.sdnwiselab.siot.cli;

import com.github.sdnwiselab.siot.CreateChannelService;
import com.github.sdnwiselab.siot.SiotChannel;
import com.github.sdnwiselab.siot.CreateChannel;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.onosproject.cli.AbstractShellCommand;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.onlab.packet.IpAddress;
import org.onosproject.cli.AbstractShellCommand;
import org.onosproject.net.ConnectPoint;
import org.onosproject.net.mcast.McastRoute;
import org.onosproject.net.mcast.MulticastRouteService;


@Command(
        scope = "onos",
        name = "createMulticastGroup",
        description = "Create the Multicast Group for the given channel Id and relation"
)
public class SiotMulticastGroupCommand extends AbstractShellCommand {

    @Argument(
            index = 0,
            name = "channelId",
            description = "channel ID of source",
            required = false,
            multiValued = false)
    private String channelId = null;
    @Argument(
            index = 1,
            name = "relation",
            description = "desired relation",
            required = false,
            multiValued = false)

    private String relation = null;

    private CreateChannelService service;
    private ChannelIdCompleter channelIdCompleter;
    private Map<String, List> relations = new HashMap();
    private SiotChannel canales= null;


    protected void execute() {
        try {
            this.service = get(CreateChannelService.class);
            String[] cookies = this.service.getCookie();
            String id = this.channelId;
            String ipSource= this.service.NameAndMacById(id,cookies)[2];
            createMulticastGroup(ipSource);
            relations=this.service.getRelationsById(cookies, id);

            if (relation.equals("Ownership")){
                List mrelation = relations.get("Owner");
                if( mrelation!=null) {
                   for(int i=0; i<mrelation.size(); i++){
                       String channel= mrelation.get(i).toString();
                       String ipAddr= this.service.NameAndMacById(channel,cookies)[2];
                       createMulticastGroup(ipAddr);

                    }
                }

                else{
                    System.out.println("error no relations found");
                }
            }

            if (relation.equals("Parental"))  {
            System.out.println("Parental: "+relations.get("Parental"));
            }
            if (relation.equals("Location")) {
            System.out.println("Location: "+relations.get("Location"));
            }
            if (relation.equals("Cowork")) {
            System.out.println("Cowork: "+relations.get("Cowork"));
            }

        }
        catch (Exception var) {
            var.printStackTrace();
        }
    }
    protected void createMulticastGroup(String ipAddr) {
        String multicastGroupIpAddr ="224.1.1.1";
        String ingressPort= null;
        String[] ports = null;
        MulticastRouteService mcastRouteManager = get(MulticastRouteService.class);


        McastRoute mRoute = new McastRoute(IpAddress.valueOf(ipAddr),
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

    
}
