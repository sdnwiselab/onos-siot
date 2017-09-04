
package com.github.sdnwiselab.siot.cli;

import com.github.sdnwiselab.siot.CreateChannelService;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.onosproject.cli.AbstractShellCommand;

/**
 * The command to delete a Device or a Host by its Id.
 */
@Command(scope = "onos", name = "deleteById",
        description = "Delete a Device or a Host by its Id")
public class DeleteByIdCommand extends AbstractShellCommand {

    @Argument(index = 0, name = "channelId", description = "channel ID of source",
            required = false, multiValued = false)
    private String channelId = null;
    private CreateChannelService service;
    private ChannelIdCompleter channelIdCompleter;

    @Override
    protected void execute() {
        try {

            service = get(CreateChannelService.class);
            String[] cookies = service.getCookie();

            String id = channelId;
            service.deleteChannelById(id, cookies);
            System.out.println(id + " successfully delete");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}






