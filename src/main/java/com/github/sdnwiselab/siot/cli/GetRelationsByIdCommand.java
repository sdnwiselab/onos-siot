

package com.github.sdnwiselab.siot.cli;

import com.github.sdnwiselab.siot.SIoTCastService;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.onlab.packet.IpAddress;
import org.onosproject.cli.AbstractShellCommand;
import org.onosproject.net.ConnectPoint;
import org.onosproject.net.mcast.McastRoute;
import org.onosproject.net.mcast.MulticastRouteService;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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
    private SIoTCastService service;
    private ChannelIdCompleter channelIdCompleter;
    private List<String> ips;
    private static Map<String, String> mGroupMap = new HashMap<>();

    protected void execute() {
        try {
            this.service = get(SIoTCastService.class);
            int areas=this.service.getAreasFromId(this.channelId);
            ips=this.service.getIpsFromId(this.relation,this.channelId,areas );
            System.out.println(ips);
            String ipGroup = MGroupIpsHandler(ips.get(0)+this.relation);
            createMulticastGroup(ips.get(0),ipGroup);

        } catch (Exception var) {
            var.printStackTrace();
        }




    }

    protected String  MGroupIpsHandler(String ipSourceRelation){
        String ipGroup ="224.1.1.1";

        if (mGroupMap.get(ipSourceRelation)!=null){
            return mGroupMap.get(ipSourceRelation);
        }
        else {
            mGroupMap.put(ipSourceRelation, ipGroup);
            return mGroupMap.get(ipSourceRelation);
        }
    }


    protected void createMulticastGroup(String ipAddr, String ipGroup) {
        String multicastGroupIpAddr =ipGroup;
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
