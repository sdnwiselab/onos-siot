
package com.github.sdnwiselab.siot.cli;

import com.github.sdnwiselab.siot.CreateChannelService;
import com.github.sdnwiselab.siot.SiotChannel;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.onosproject.cli.AbstractShellCommand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * The command to delete a Device or a Host by its Id.
 */
@Command(scope = "onos", name = "editById",
        description = "Edit a Channel by its Id")
public class EditChannelByIdCommand extends AbstractShellCommand {

    @Argument(index = 0, name = "channelId", description = "channel ID of source",
            required = false, multiValued = false)
    private String channelId = null;
    private CreateChannelService service;
    private ChannelIdCompleter channelIdCompleter;
    private String[] channelInfo = new String[2];
    public SiotChannel channel;
    public String channelParameters;


    String[] param = new String[4];
    String[] parame = new String[]{"description", "model", "brand", "location name"};

    @Override
    protected void execute() {
        try {

            service = get(CreateChannelService.class);
            String[] cookies = service.getCookie();

            String id = channelId;
            channelInfo = service.NameAndMacById(id, cookies);
            String name = channelInfo[0];
            String wf_mac_address = channelInfo[1];
            String ipAddr= channelInfo[2];
            channel = new SiotChannel(name, wf_mac_address,ipAddr);
            int i = 0;
            for (String parameter : parame) {
                System.out.println("Type the " + parameter);
                InputStreamReader reader = new InputStreamReader(System.in);
                BufferedReader myInput = new BufferedReader(reader);
                try {
                    param[i] = myInput.readLine();
                    System.out.println(param[i]);
                    i++;
                } catch (IOException e) {
                    System.out.println("ERROR: " + e);
                    System.exit(-1);
                }
            }

            channel.description = param[0];
            channel.model = param[1];
            channel.brand = param[2];
            channel.location_name = param[3];

            channelParameters = channel.getUrlParameters();
            service.editChannels(cookies, id, channelParameters);
            System.out.println(id + " successfully edited");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}






