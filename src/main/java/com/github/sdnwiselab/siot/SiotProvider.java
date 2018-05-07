package com.github.sdnwiselab.siot;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.onosproject.net.HostId;
import org.onosproject.net.host.*;
import org.onosproject.net.provider.*;
import org.onlab.packet.IpAddress;


@Component(immediate = true)
public class SiotProvider extends AbstractProvider implements HostProviderService {


    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected HostService hostService;


    public SiotProvider() {
        super(new ProviderId("",""));
    }



    @Override
    public void hostDetected(HostId hostId, HostDescription hostDescription, boolean b) {

    }

    @Override
    public void hostVanished(HostId hostId) {

    }

    @Override
    public void removeIpFromHost(HostId hostId, IpAddress ipAddress) {

    }

    @Override
    public HostProvider provider() {
        return null;
    }
}



