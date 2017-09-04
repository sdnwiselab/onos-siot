
package com.github.sdnwiselab.siot;


import java.util.List;
import java.util.Map;


public interface CreateChannelService {


    public String[] getCookie() throws Exception;

    public void deleteChannelById(String id, String[] cookies) throws Exception;

    public List<String> getIdChannels(String[] cookies) throws Exception;

    public void deleteAllChannels(String[] cookies);

    public String[] NameAndMacById(String id, String[] cookies) throws Exception;

    public void editChannels(String[] cookies, String id, String urlParameters) throws Exception;

    public Object getChannel(String[] cookies, String id) throws Exception;

    public Map<String, List> getRelationsById (String[] cookies, String id) throws Exception;

    public void getRelationGraph (String id, Map<String, List> relation);
}




