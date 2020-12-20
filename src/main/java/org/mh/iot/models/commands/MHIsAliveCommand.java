package org.mh.iot.models.commands;

import org.mh.iot.models.commands.Command;

/**
 * Created by evolshan on 10.12.2020.
 */
public class MHIsAliveCommand extends Command {

    public MHIsAliveCommand(){
        command("IS_ALIVE").id("IS_ALIVE_ID");
    }

    public String getCommand() {
        return "IS_ALIVE";
    }

    public String getId() {
        return "IS_ALIVE_ID";
    }
}
