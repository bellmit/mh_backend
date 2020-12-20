package org.mh.iot.models.commands;

/**
 * Created by evolshan on 10.12.2020.
 */
public class MHDiscoverCommand extends Command {

    public MHDiscoverCommand(){
        command("DISCOVER").id("DISCOVER_ID");
    }

    public String getCommand() {
        return "DISCOVER";
    }

    public String getId() {
        return "DISCOVER_ID";
    }
}
