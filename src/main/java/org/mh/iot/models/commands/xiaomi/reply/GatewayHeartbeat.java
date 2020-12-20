package org.mh.iot.models.commands.xiaomi.reply;

public class GatewayHeartbeat extends XiaomiCommandReply {
    public String cmd;
    public String sid;
    public String model;
    public String short_id; // NB: sometimes it is a string and sometimes a number
    public String token;
    public String data;

}
