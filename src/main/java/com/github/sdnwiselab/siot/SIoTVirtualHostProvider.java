
package com.github.sdnwiselab.siot;

import org.apache.felix.scr.annotations.Component;
import org.onlab.packet.*;
import org.onosproject.net.*;
import org.onosproject.net.host.DefaultHostDescription;
import org.onosproject.net.host.HostDescription;
import org.onosproject.net.host.HostProvider;
import org.onosproject.net.host.HostProviderService;
import org.onosproject.net.provider.AbstractProvider;
import org.onosproject.net.provider.ProviderId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collections;
import java.util.Set;

import static org.slf4j.LoggerFactory.getLogger;


@Component(immediate = true)
public class SIoTVirtualHostProvider extends AbstractProvider implements HostProvider {


    private final Logger log = getLogger(getClass());
    private static final String APP_NAME = "com.github.sdnwiselab.siot";
    private static final ProviderId PROVIDER_ID = new ProviderId("host", APP_NAME);
    protected HostProviderService providerService;


    /**
     * Creates an network config host location provider.
     */
    public SIoTVirtualHostProvider() {

        super(PROVIDER_ID);
    }

    @Override
    public void triggerProbe(Host host) {
        /*
         * Note: All hosts are configured in network config host provider.
         * Therefore no probe is required.
         */
    }


   /* protected void addHost(MacAddress mac, VlanId vlan, HostLocation location, Set<IpAddress> ips) {
        HostId hid = HostId.hostId(mac, vlan);
        log.info(hid.toString());
        HostDescription desc = (ips != null) ?
                new DefaultHostDescription(mac, vlan, location, ips, true) :
                new DefaultHostDescription(mac, vlan, location, Collections.emptySet(), true);
        log.info(desc.toString());
        //providerService.hostDetected(hid, desc, true);
    }*/



    protected void updateHost(MacAddress mac, VlanId vlan, HostLocation location, Set<IpAddress> ips) {
        HostId hid = HostId.hostId(mac, vlan);
        HostDescription desc = new DefaultHostDescription(mac, vlan, location, ips, true);
        providerService.hostDetected(hid, desc, true);
    }



    protected void removeHost(MacAddress mac, VlanId vlan) {
        HostId hid = HostId.hostId(mac, vlan);
        providerService.hostVanished(hid);
    }

}
