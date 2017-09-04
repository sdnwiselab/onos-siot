
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
        name = "getRelationsFromId",
        description = "Return the relations of the channel"
)
public class GetRelationsByIdCommand extends AbstractShellCommand {

    @Argument(
            index = 0,
            name = "channelId",
            description = "channel ID of source",
            required = false,
            multiValued = false
    )
    private String channelId = null;
    private CreateChannelService service;
    private ChannelIdCompleter channelIdCompleter;
    private Map<String, List> relations = new HashMap();


    protected void execute() {
        try {
            this.service = get(CreateChannelService.class);
            String[] cookies = this.service.getCookie();
            String id = this.channelId;
            relations=this.service.getRelationsById(cookies, id);
            System.out.println("Ownership: "+relations.get("Owner"));
            System.out.println("Parental: "+relations.get("Parental"));
            System.out.println("Location: "+relations.get("Location"));
            System.out.println("Cowork: "+relations.get("Cowork"));

            this.service.getRelationGraph(id, relations);
        } catch (Exception var) {
            var.printStackTrace();
        }




    }
}
