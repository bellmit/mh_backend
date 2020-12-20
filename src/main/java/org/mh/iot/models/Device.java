package org.mh.iot.models;

import org.mh.iot.models.commands.Command;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by evolshan on 09.12.2020.
 */
@Entity
@Table(name="mh.USED_DEVICES")
public class Device {

    @Id
    @Column(name="NAME", columnDefinition="varchar(64)", unique=true, nullable = false)
    private String name;

    @Column(name="TYPE", nullable = false)
    private String type;

    @Column(name="FIRMWARE")
    private String firmware;

    @Column(name="WAKE_UP_INTERVAL")
    private int wakeUpInterval = 0;

    @Column(name="IP")
    private String ip;

    @OneToOne
    @JoinColumn(name="CONTROL_INTERFACE_ID", nullable = false)
    private Interface controlInterface;

    @OneToOne
    @JoinColumn(name="STATUS_INTERFACE_ID", nullable = false)
    private Interface statusInterface;

    @OneToMany(cascade = CascadeType.ALL,
            mappedBy = "device")
    private List<Parameter> parameters = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL,
            mappedBy = "device")
    private List<Command> commands = new ArrayList<>();

    public String getName() {
        return name;
    }

    public Device name(String name) {
        this.name = name;
        return this;
    }

    public String getType() {
        return type;
    }

    public Device type(String type) {
        this.type = type;
        return this;
    }

    public String getFirmware() {
        return firmware;
    }

    public Device firmware(String firmware) {
        this.firmware = firmware;
        return this;
    }

    public int getWakeUpInterval() {
        return wakeUpInterval;
    }

    public Device wakeUpInterval(int wakeUpInterval) {
        this.wakeUpInterval = wakeUpInterval;
        return this;
    }

    public Interface getControlInterface() {
        return controlInterface;
    }

    public Device controlInterface(Interface controlInterface) {
        this.controlInterface = controlInterface;
        return this;
    }

    public Interface getStatusInterface() {
        return statusInterface;
    }

    public Device statusInterface(Interface statusInterface) {
        this.statusInterface = statusInterface;
        return this;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public Device parameters(List<Parameter> parameters) {
        this.parameters = parameters;
        return this;
    }

    public List<Command> getCommands() {
        return commands;
    }

    public Device commands(List<Command> commands) {
        this.commands = commands;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public Device ip(String ip) {
        this.ip = ip;
        return this;
    }
}
