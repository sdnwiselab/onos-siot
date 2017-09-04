
package com.github.sdnwiselab.siot.cli;

import com.github.sdnwiselab.siot.CreateChannelService;
import org.apache.karaf.shell.commands.Command;
import org.onosproject.cli.AbstractShellCommand;

/**
 * The command deletes all Devices and Hosts in the Siot.
 */
@Command(scope = "onos", name = "deleteAllChannels",
        description = "Delete all Devices and Hosts in the Siot")
public class DeleteAllChannelsCommand extends AbstractShellCommand {

    private CreateChannelService service;

    @Override
    protected void execute() {
        try {

            service = get(CreateChannelService.class);
            String[] cookies = service.getCookie();
            service.deleteAllChannels(cookies);
            System.out.println("All channels successfully delete");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}






