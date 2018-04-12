package com.github.sdnwiselab.siot;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.onosproject.cli.AbstractShellCommand;
import org.onosproject.incubator.net.virtual.*;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.onosproject.net.Host;
import org.onosproject.net.host.HostEvent;
import org.onosproject.net.host.HostListener;
import org.onosproject.net.host.HostService;
import org.slf4j.Logger;
import org.onlab.packet.IpAddress;
import static org.slf4j.LoggerFactory.getLogger;
import java.util.ArrayList;
import java.util.List;
import org.onosproject.incubator.net.virtual.*;
import org.onosproject.incubator.net.virtual.event.AbstractVirtualListenerManager;
import org.onosproject.incubator.net.virtual.event.VirtualEvent;
import org.onosproject.incubator.net.virtual.event.VirtualListenerRegistryManager;
import org.onosproject.event.*;

@Component(immediate = true)
public class InnerVirtualHostListener implements EventListener{

    private final Logger log = getLogger(getClass());
    protected VirtualHost virtualHost;


    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected VirtualNetworkService vnetService;

    @Override
    public void event (Event event){
        log.info("New virtual host event."+ event.subject().toString());

/*
        switch (event.type()) {
            case VIRTUALHOST_ADDED:
                virtualHost = event.subject();
                try {

                    log.info("New host: " + host.id().toString() + " added and channel created.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            case HOST_REMOVED:
                break;
        }*/
    }
}
 /*
@Command(scope = "onos", name = "vnet-hosts",
        description = "Lists all virtual hosts in a virtual network.")
public class InnerVirtualHostListener extends AbstractShellCommand {

    private static final String FMT_VIRTUAL_HOST =
            "id=%s, mac=%s, vlan=%s, location=%s, ips=%s";

    @Argument(index = 0, name = "networkId", description = "Network ID",
            required = true, multiValued = false)
    Long networkId = null;

    @Override
    protected void execute() {
        getSortedVirtualHosts().forEach(this::printVirtualHost);
    }

    /**
     * Returns the list of virtual hosts sorted using the device identifier.
     *
     * @return virtual host list
     *
    private List<VirtualHost> getSortedVirtualHosts() {
        VirtualNetworkService service = get(VirtualNetworkService.class);

        List<VirtualHost> virtualHosts = new ArrayList<>();
        virtualHosts.addAll(service.getVirtualHosts(NetworkId.networkId(networkId)));
        return virtualHosts;
    }

    /**
     * Prints out each virtual host.
     *
     * @param virtualHost virtual host
     *
    private void printVirtualHost(VirtualHost virtualHost) {
        print(FMT_VIRTUAL_HOST, virtualHost.id().toString(), virtualHost.mac().toString(),
                virtualHost.vlan().toString(), virtualHost.location().toString(),
                virtualHost.ipAddresses().toString());
    }
}*/
