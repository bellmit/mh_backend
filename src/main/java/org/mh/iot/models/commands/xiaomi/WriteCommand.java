package org.mh.iot.models.commands.xiaomi;
import org.apache.camel.json.simple.JsonObject;

public class WriteCommand extends XiaomiCommand {
    private String sid;
    private JsonObject data;

    public WriteCommand(String sid, JsonObject data) {
        this.sid = sid;
        this.data = data;
        data.put("key", "${KEY}");
    }

    @Override
    public String getJson() {
        return "{\"cmd\":\"write\", \"sid\":\""+ sid +"\", \"data\":" + data.toJson() + "}";
    }
}
