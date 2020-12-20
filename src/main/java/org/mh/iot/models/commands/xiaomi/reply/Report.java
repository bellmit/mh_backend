package org.mh.iot.models.commands.xiaomi.reply;

public class Report extends XiaomiCommandReply {
    public String cmd;
    public String sid;
    public String model;
    public short short_id; // NB: sometimes it is a string and sometimes a number
    public String data;

}
