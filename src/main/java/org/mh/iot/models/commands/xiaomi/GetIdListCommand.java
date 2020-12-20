package org.mh.iot.models.commands.xiaomi;

public class GetIdListCommand extends XiaomiCommand {
    public GetIdListCommand() {}

    @Override
    public String getJson() {
        return "{\"cmd\":\"get_id_list\"}";
    }
}
