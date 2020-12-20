package org.mh.iot.models.commands.xiaomi;

public class ReadCommand extends XiaomiCommand {
    private String sid;

    public ReadCommand(String sid) {
        this.sid = sid;
    }


    @Override
    public String getJson() {
        return "{\"cmd\":\"read\", \"sid\":\""+ sid +"\"}";
    }
}
