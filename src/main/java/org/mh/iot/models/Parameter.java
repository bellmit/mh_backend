package org.mh.iot.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * Created by evolshan on 09.12.2020.
 */
@Entity
@Table(name="mh.PARAMETERS")
public class Parameter {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="ID")
    private Long id;

    @Column(name="NAME", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name="TYPE", nullable = false)
    private ParameterType type;

    @ManyToOne
    @JoinColumn(name = "DEVICE_ID")
    @JsonIgnore
    private Device device;

    public String getName() {
        return name;
    }

    public Parameter name(String name) {
        this.name = name;
        return this;
    }

    public ParameterType getType() {
        return type;
    }

    public Parameter type(ParameterType type) {
        this.type = type;
        return this;
    }

    public Device getDevice() {
        return device;
    }

    public Parameter device(Device device) {
        this.device = device;
        return this;
    }
}
