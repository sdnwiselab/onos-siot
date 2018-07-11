
package com.github.sdnwiselab.siot;
import org.onlab.packet.Ethernet;
import org.onosproject.core.ApplicationId;
import org.onosproject.net.*;

import java.util.List;
import java.util.Map;
import java.util.Set;


public interface SIoTCastService {
    public Device deviceConnectedToHost(String id);
    public List<String> getIdChannels() throws Exception;
    public List<String> getIpsFromId( String srcId, int areas);
    public Integer getAreasFromId(String id);
    public Map<HostId,DeviceId> createVirtualHost(Device dev, int n);
    public void postSIoTCast(List<String> hostData);
    public List<Host> allHostsConnectedToDevice(Device dev);
    public Host getVirtualHostFromDevice(DeviceId devid);
    //public void setSiotFlowRule (Device dev, String ipGroup, Host host);
    public Host getHostById (String id);
    public void creazioneIntent(DeviceId devId,String ipGroup,Map<HostId,PortNumber> mapHostPort,List<Device> dropDevice);
    public void sendUniPacketToHost (Ethernet packet, Host sourceHost, Host destinationHost);
    public void sendMultiPacketToHost (Ethernet packet, Host sourceHost, String groupIp);
    public void sendPacketForFlowRule(Ethernet packet, Host sourceHost);
}




