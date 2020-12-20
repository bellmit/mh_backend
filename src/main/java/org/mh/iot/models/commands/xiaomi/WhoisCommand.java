package org.mh.iot.models.commands.xiaomi;

public class WhoisCommand extends XiaomiCommand {

    @Override
    public String getJson() {
        return "{\"cmd\":\"whois\"}";
    }
}
