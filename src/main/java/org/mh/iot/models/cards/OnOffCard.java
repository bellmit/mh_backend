package org.mh.iot.models.cards;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by evolshan on 16.12.2020.
 */
@Entity
@Table(name="mh.ON_OFF_CARDS")
public class OnOffCard {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name="TITLE", nullable = false)
    private String title;

    @Column(name="ICON_CLASS", nullable = false)
    private String iconClass;

    @Column(name="TYPE", nullable = false)
    private String type;

    @Column(name="DEVICE_NAME", nullable = false)
    private String device;

    @Column(name="PARAMETER", nullable = false)
    private String parameter;

    @Column(name="COMAND", nullable = false)
    private String command;

    @Column(name="ROOM_ID", nullable = false)
    private Long roomId;

    public Long getId() {
        return id;
    }

    public OnOffCard id(Long id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public OnOffCard title(String title) {
        this.title = title;
        return this;
    }

    public String getIconClass() {
        return iconClass;
    }

    public OnOffCard iconClass(String iconClass) {
        this.iconClass = iconClass;
        return this;
    }

    public String getType() {
        return type;
    }

    public OnOffCard type(String type) {
        this.type = type;
        return this;
    }

    public String getDeviceName() {
        return device;
    }

    public OnOffCard deviceName(String deviceName) {
        this.device = deviceName;
        return this;
    }

    public String getParameter() {
        return parameter;
    }

    public OnOffCard parameter(String parameter) {
        this.parameter = parameter;
        return this;
    }

    public String getCommand() {
        return command;
    }

    public OnOffCard command(String command) {
        this.command = command;
        return this;
    }

    public Long getRoomId() {
        return roomId;
    }

    public OnOffCard roomId(Long roomId) {
        this.roomId = roomId;
        return this;
    }

    public String getDevice() {
        return device;
    }

    public OnOffCard device(String device) {
        this.device = device;
        return this;
    }
}
