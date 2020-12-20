package org.mh.iot.models;

/**
 * Created by evolshan on 09.12.2020.
 */
public class ConditionParameter {
    private String name;
    private String typeName;

    public String getName() {
        return name;
    }

    public ConditionParameter name(String name) {
        this.name = name;
        return this;
    }

    public String getTypeName() {
        return typeName;
    }

    public ConditionParameter typeName(String type) {
        this.typeName = type;
        return this;
    }
}
