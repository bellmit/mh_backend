package org.mh.iot.models.commands;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mh.iot.models.DataItem;
import org.mh.iot.models.Device;
import org.mh.iot.models.converter.CommandDataConverter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by evolshan on 12.12.2020.
 */
@Entity
@Table(name="mh.COMMANDS")
public class Command {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="ID")
    private Long key;

    @Column(name="COMMAND", nullable = false)
    private String command;

    @ManyToOne
    @JoinColumn(name = "DEVICE_ID")
    @JsonIgnore
    private Device device;

    @Convert(converter = CommandDataConverter.class)
    @Column(name = "DATA")
    private List<DataItem> data = new ArrayList<>();


    public String getCommand() {
        return command;
    }

    public Command command(String command) {
        this.command = command;
        return this;
    }

    public List<DataItem> getData() {
        return data;
    }

    public Command data(List<DataItem> data) {
        this.data = data;
        return this;
    }

    @JsonIgnore
    @Transient
    private ObjectMapper mapper = new ObjectMapper();

    @Transient
    private String id;

    public String getId() {
        return id;
    }

    public Command id(String id) {
        this.id = id;
        return this;
    }

    @JsonIgnore
    public String getJson(){
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    public Device getDevice() {
        return device;
    }

    public Command device(Device device) {
        this.device = device;
        return this;
    }
}
