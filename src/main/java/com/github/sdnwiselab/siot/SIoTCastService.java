
package com.github.sdnwiselab.siot;
import org.onlab.packet.Ethernet;
import org.onosproject.net.*;
import java.util.List;
import java.util.Map;


public interface SIoTCastService {
    public Device deviceConnectedToHost(String id);
    public List<String> getIdChannels() throws Exception;
    public List<String> getIpsFromId( String srcId, int areas, String relation, String hop);
    public Integer getAreasFromId(String id);
    public void postSIoTCast(List<String> hostData);
    public List<Host> allHostsConnectedToDevice(Device dev);
    public Host getVirtualHostFromDevice(DeviceId devid);
    public Host getHostById (String id);
    public void creazioneIntent(DeviceId devId,String ipGroup,Map<HostId,PortNumber> mapHostPort,List<Device> dropDevice);
    public void sendMultiPacketToHost (Ethernet packet, Host sourceHost, String groupIp, List<String> ids);
    public void sendPacketForFlowRule(Ethernet packet, Host sourceHost);
    public List<String> getPublishers();
}




