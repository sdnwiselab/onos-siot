
package com.github.sdnwiselab.siot;
import java.util.List;


public interface SIoTCastService {

    public List<String> getIdChannels() throws Exception;
    public List <String>getIpsFromId(String relation, String srcId, int areas);
    public Integer getAreasFromId(String id);

}




