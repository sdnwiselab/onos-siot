
package com.github.sdnwiselab.siot.cli;

import com.github.sdnwiselab.siot.CreateChannelService;
import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.onosproject.cli.AbstractShellCommand;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Command(
        scope = "onos",
        name = "createMulticastGroup",
        description = "Create the Multicast Group for the given channel Id and relation"
)
public class SiotMulticastGroupCommand extends AbstractShellCommand {

    @Argument(
            index = 0,
            name = "channelId",
            description = "channel ID of source",
            required = false,
            multiValued = false)
    private String channelId = null;
    @Argument(
            index = 1,
            name = "relation",
            description = "desired relation",
            required = false,
            multiValued = false)

    private String relation = null;

    private CreateChannelService service;
    private ChannelIdCompleter channelIdCompleter;
    private Map<String, List> relations = new HashMap();


    protected void execute() {
        try {
            this.service = get(CreateChannelService.class);
            String[] cookies = this.service.getCookie();
            String id = this.channelId;
            relations=this.service.getRelationsById(cookies, id);

            if (relation.equals("Ownership")) {
            System.out.println("Ownership: "+relations.get("Owner"));
            }
            if (relation.equals("Parental"))  {
            System.out.println("Parental: "+relations.get("Parental"));
            }
            if (relation.equals("Location")) {
            System.out.println("Location: "+relations.get("Location"));
            }
            if (relation.equals("Cowork")) {
            System.out.println("Cowork: "+relations.get("Cowork"));
            }

        } catch (Exception var) {
            var.printStackTrace();
        }




    }
}
