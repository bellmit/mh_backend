package org.mh.iot.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by evolshan on 12.12.2020.
 */
public class Condition {
    private String name;
    private String description;
    private List<ConditionParameter> parameters = new ArrayList<>();



    public String getDescription() {
        return description;
    }

    public Condition description(String description) {
        this.description = description;
        return this;
    }

    public String getName() {
        return name;
    }

    public Condition name(String name) {
        this.name = name;
        return this;
    }

    public List<ConditionParameter> getParameters() {
        return parameters;
    }

    public Condition parameters(List<ConditionParameter> parameters) {
        this.parameters = parameters;
        return this;
    }
}
