package com.github.sdnwiselab.siot;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;
import org.onlab.packet.Ip4Address;
import org.onosproject.core.CoreService;
import org.slf4j.Logger;
import org.onlab.packet.IpAddress;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.slf4j.LoggerFactory.getLogger;
@Component(immediate = true)
@Service
public class CreateChannel implements CreateChannelService{

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected CoreService coreService;
    //protected CreateChannelService channelService;


    private final Logger log = getLogger(getClass());
    private final String USER_AGENT = "Mozilla/5.0";
    private final String userName = "MilottaGiuseppe";
    private final String psw = "cnitcnit";
    protected static Set<String> hostNames = new HashSet<String>();

    //Getting the cookie
    @Override
    public String[] getCookie() throws Exception {


        String url = "http://platform.social-iot.org/user_session";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        String csrfToken = new String();
        Pattern p = Pattern.compile("/><meta content=\"(.*?)\" name=\"csrf-token\" />");
        Matcher m = p.matcher(response.toString());
        while (m.find()) {
            csrfToken = m.group(1);
        }

        String cookies = con.getHeaderField("Set-Cookie");
        String output[] = {cookies, csrfToken};
        log.info("Cookie and csrf Token obtained");
        return output;
    }

    //Login method
    protected void sendPost(String[] cookies) throws Exception {

        String url = "http://platform.social-iot.org/user_session";
        String urlParameters = "utf8=%E2%9C%93&authenticity_token=" + URLEncoder.encode(cookies[1], "utf-8") +
                "&userlogin=&user_session[remember_me]=false&user_session[login]=" + userName + "&user_session[password]=" +
                psw + "&user_session[remember_me]=false&user_session[remember_id]=0&user_session[remember_id]=1&commit=Sign In";
        HttpURLConnection con = postConnectionWithSiot(url, urlParameters, cookies);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();
        int responseCode = con.getResponseCode();
        log.info("Connection with SIOT Created.");
    }

    //Create the SIOT Channel with the host's Mac
    protected void createChannel(String[] cookies, String name, String macAddress, Set<IpAddress> ipAddress) throws Exception {
        SiotChannel canale = null;

        if (hostNames.contains(name)){
            log.info("Channel not created device or host already in the list.");
        }
        else {
            if (ipAddress==null){
                String ipAddr=" ";
                String url = "http://platform.social-iot.org/channels";
                String urlParameters = "utf8=%E2%9C%93&authenticity_token=" + URLEncoder.encode(cookies[1], "utf-8") + "&userlogin=&commit=Create New Channel";
                HttpURLConnection con = postConnectionWithSiot(url, urlParameters, cookies);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();
                int responseCode = con.getResponseCode();

                //edit the new channel MAC and Name
                List<String> ids = getIdChannels(cookies);
                int dimension = ids.size() - 1;
                String device = ids.get(dimension);
                canale = new SiotChannel(name,macAddress,ipAddr);
                String channelParameters =canale.getUrlParameters();
                editChannels(cookies, device, channelParameters );
                log.info("Channel with id: " + device + ", mac: " + macAddress +" and ip: "+ ipAddr+" Created");
                hostNames.add(name);
            }
            else {
                String ipAddr= ipAddress.iterator().next().toString();
                String url = "http://platform.social-iot.org/channels";
                String urlParameters = "utf8=%E2%9C%93&authenticity_token=" + URLEncoder.encode(cookies[1], "utf-8") + "&userlogin=&commit=Create New Channel";
                HttpURLConnection con = postConnectionWithSiot(url, urlParameters, cookies);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();
                int responseCode = con.getResponseCode();

                //edit the new channel MAC and Name
                List<String> ids = getIdChannels(cookies);
                int dimension = ids.size() - 1;
                String device = ids.get(dimension);
                canale = new SiotChannel(name,macAddress,ipAddr);
                String channelParameters =canale.getUrlParameters();
                editChannels(cookies, device, channelParameters );
                log.info("Channel with id: " + device + ", mac: " + macAddress +" and ip: "+ ipAddr+" Created");
                hostNames.add(name);
            }
        }

    }

    //Edit the SIOT channel
    @Override
    public void editChannels(String[] cookies, String id, String urlParameters ) throws Exception {

        String url = "http://platform.social-iot.org/channels/" + id;
        String channelParameters="utf8=%E2%9C%93&_method=put&authenticity_token=" + URLEncoder.encode(cookies[1], "utf-8") + urlParameters + "&commit=Save";
        HttpURLConnection con = postConnectionWithSiot(url, channelParameters, cookies);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(channelParameters);
        wr.flush();
        wr.close();
        int responseCodec = con.getResponseCode();
        log.info("Channel (id: " + id + ") edited");
    }

    //Return the list of all the Ids of the SIOT channels
    @Override
    public List<String> getIdChannels(String[] cookies) throws Exception {
        String url = "http://platform.social-iot.org/channels";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Cookie", cookies[0]);
        int responseCode = con.getResponseCode();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        List<String> prova = new ArrayList<String>();
        List<String> channelsID = new ArrayList<String>();
        HashMap<String, String> idMAC = new HashMap<>();
        Pattern p = Pattern.compile("<td><a href=\"/channels/(.*?)\">");

        while ((inputLine = in.readLine()) != null) {
            prova.add(inputLine);
        }
        in.close();
        for (String line : prova) {
            Matcher m = p.matcher(line);
            while (m.find()) {
                channelsID.add(m.group(1));

            }
        }

        return channelsID;
    }

    //Links the host's MAC to the channel's id
    protected HashMap getListChannels(String[] cookies) throws Exception {
        String url = "http://platform.social-iot.org/channels";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Cookie", cookies[0]);
        int responseCode = con.getResponseCode();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        List<String> prova = new ArrayList<String>();
        List<String> channelsID = new ArrayList<String>();
        HashMap<String, String> idMAC = new HashMap<>();
        Pattern p = Pattern.compile("<td><a href=\"/channels/(.*?)\">");

        while ((inputLine = in.readLine()) != null) {
            prova.add(inputLine);
        }
        in.close();
        for (String line : prova) {
            Matcher m = p.matcher(line);
            while (m.find()) {
                channelsID.add(m.group(1));
            }
        }

        for (int i = 0; i < channelsID.size(); i++) {
            String summ = "<td><a href=\"/channels/" + channelsID.get(i) + "\">";
            Pattern h = Pattern.compile(summ + "(.*?)</a></td>");
            for (String line : prova) {
                Matcher b = h.matcher(line);
                while (b.find()) {
                    idMAC.put(channelsID.get(i), b.group(1));
                    hostNames.add(b.group(1));
                }
            }
        }

        return idMAC;
    }

    //Delete the channel with the given id
    @Override
    public void deleteChannelById(String id, String[] cookies) throws Exception {

        String url = "http://platform.social-iot.org/channels/" + id;
        String urlParameters = "_method=delete&authenticity_token=" + URLEncoder.encode(cookies[1], "utf-8");
        HttpURLConnection con = postConnectionWithSiot(url, urlParameters, cookies);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();
        int responseCode = con.getResponseCode();
        log.info("Channel (id: " + id + ") deleted");
    }

    //Delete all the channels
    @Override
    public void deleteAllChannels(String[] cookies) {
        try {
            List<String> idsChannels = getIdChannels(cookies);
            for (String id : idsChannels) {
                deleteChannelById(id, cookies);
            }
            log.info("All channels have been deleted");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Create the connection with SIOT web site.
    protected HttpURLConnection postConnectionWithSiot(String url, String urlParameter, String[] cookies) {
        HttpURLConnection con = null;
        try {
            URL obje = new URL(url);
            con = (HttpURLConnection) obje.openConnection();
            con.setUseCaches(false);
            con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            con.addRequestProperty("Cookie", cookies[0].split(";", 1)[0]);
            con.setRequestProperty("Connection", "keep-alive");
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Cookie", cookies[0]);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            String urlParameters = urlParameter;
            con.setRequestProperty("Content-Length", Integer.toString(urlParameters.length()));
            con.setDoOutput(true);
            con.setDoInput(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;

    }

    //Return the Name and the MAC address of the channel by the id.
    @Override
    public String[] NameAndMacById( String id, String[] cookies) throws Exception{

        String url = "http://platform.social-iot.org/channels/"+id;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        String name = new String();
        Pattern p = Pattern.compile("<td class=\"left\">Name:</td>    <td>(.*?)</td>");
        Matcher m = p.matcher(response.toString());
        while (m.find()) {
            name = m.group(1);

        }

        String macaddress = new String();
        Pattern h = Pattern.compile("<td class=\"left\">WiFi Mac Address</td>    <td>(.*?)</td>");
        Matcher d = h.matcher(response.toString());


        while (d.find()) {
            macaddress = d.group(1).toString();
        }

        String ipAddr = new String();
        Pattern o = Pattern.compile("<td class=\"left\">Custom Field 13</td>    <td>(.*?)</td>");
        Matcher k = o.matcher(response.toString());
        while (k.find()) {
               ipAddr = k.group(1).toString();

         }
        String output[] = {name, macaddress, ipAddr};
        return output;

    }

    @Override
    public SiotChannel getChannel(String[] cookies, String id) throws Exception{
        SiotChannel canale;
        String url = "http://platform.social-iot.org/channels/"+id;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        String name = new String();
        Pattern p = Pattern.compile("<td class=\"left\">Name:</td>    <td>(.*?)</td>");
        Matcher m = p.matcher(response.toString());
        while (m.find()) {
            name = m.group(1);

        }

        String macaddress = new String();
        Pattern h = Pattern.compile("<td class=\"left\">WiFi Mac Address</td>    <td>(.*?)</td>");
        Matcher d = h.matcher(response.toString());


        while (d.find()) {
            macaddress = d.group(1).toString();

        }

        String ipAddr = new String();
        Pattern o = Pattern.compile("<td class=\"left\">Custom Field 13</td>    <td>(.*?)</td>");
        Matcher k = o.matcher(response.toString());


        while (k.find()) {
            macaddress = k.group(1).toString();

        }
        canale= new SiotChannel(name, macaddress, ipAddr);
        return canale;

    }

    @Override
    public Map<String, List> getRelationsById (String[] cookies, String id) throws Exception {
        Map<String, List> relations = new HashMap();

        List ownership = new ArrayList();
        List location = new ArrayList();
        List cowork = new ArrayList();
        List parental = new ArrayList();

        String url = "http://platform.social-iot.org/channels/"+id;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();

        String OORtable = new String();
        Pattern p = Pattern.compile("<td>OOR</td>(.*?)</table>");
        Matcher m = p.matcher(response.toString());
        while (m.find()) {
           OORtable=m.group(1);
        }
        Pattern q =Pattern.compile("<td><a href=\"/channels/(.*?)\">");
        Matcher w= q.matcher(OORtable);
        while (w.find()) {
                ownership.add(w.group(1));
            }


        String PORtable = new String();
        Pattern e = Pattern.compile("<td>POR</td>(.*?)</table>");
        Matcher r = e.matcher(response.toString());
        while (r.find()) {
            PORtable=r.group(1);
        }
        Pattern t =Pattern.compile("<td><a href=\"/channels/(.*?)\">");
        Matcher y= t.matcher(PORtable);
        while (y.find()) {
            parental.add(y.group(1));
        }

        String CLORtable = new String();
        Pattern z = Pattern.compile("<td>CLOR</td>(.*?)</table>");
        Matcher x = z.matcher(response.toString());
        while (x.find()) {
            CLORtable=x.group(1);
        }
        Pattern c =Pattern.compile("<td><a href=\"/channels/(.*?)\">");
        Matcher vv= c.matcher(CLORtable);
        while (vv.find()) {
            location.add(vv.group(1));
        }

        String CWORtable = new String();
        Pattern pp = Pattern.compile("<td>CWOR</td>(.*?)</table>");
        Matcher mm = pp.matcher(response.toString());
        while (mm.find()) {
            CWORtable=mm.group(1);
        }
        Pattern qq =Pattern.compile("<td><a href=\"/channels/(.*?)\">");
        Matcher ww= qq.matcher(CWORtable);
        while (ww.find()) {
            cowork.add(ww.group(1));
        }

        relations.put("Owner", ownership);
        relations.put("Parental", parental);
        relations.put("Location", location);
        relations.put("Cowork", cowork);
        return relations;
    }

    @Override
    public void getRelationGraph (String id, Map<String, List> relation){
        Graph graf= new Graph();
        for (Object x:relation.get("Owner")){
            String y=x.toString();
            graf.addEdge(id,y);

        }
        System.out.println("\nOwnership Graph for "+id+"\n");
        System.out.println(graf);
    }

}





