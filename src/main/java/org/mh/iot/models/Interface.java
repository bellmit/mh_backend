package org.mh.iot.models;

import javax.persistence.*;

/**
 * Created by evolshan on 09.12.2020.
 */
@Entity
@Table(name="mh.INTERFACES")
public class Interface {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="ID")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name="TYPE", nullable = false)
    private InterfaceType type;

    @Column(name="CONNECTION_STRING", nullable = false)
    private String connectionString;

    public InterfaceType getType() {
        return type;
    }

    public Interface type(InterfaceType type) {
        this.type = type;
        return this;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public Interface connectionString(String connectionString) {
        this.connectionString = connectionString;
        return this;
    }

}
