package org.mh.iot.models.commands;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.mh.iot.models.StatusMessage;

/**
 * Created by evolshan on 12.12.2020.
 */
public class CommandReply {

    public enum Code{
        OK,
        ERROR
    }

    private StatusMessage reply = null;

    public StatusMessage getReply() {
        return reply;
    }

    public CommandReply reply(StatusMessage reply) {
        this.reply = reply;
        return this;
    }

    private Code code;
    private String message;

    public String getMessage() {
        return message;
    }

    public CommandReply message(String message) {
        this.message = message;
        return this;
    }

    public Code getCode() {
        return code;
    }

    public CommandReply code(Code code) {
        this.code = code;
        return this;
    }

}
