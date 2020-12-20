package org.mh.iot.models;

/**
 * Created by evolshan on 09.12.2020.
 */
public class DataItem {
    private String elementName;
    private String type;

    public String getElementName() {
        return elementName;
    }

    public DataItem elementName(String elementName) {
        this.elementName = elementName;
        return this;
    }

    public String getType() {
        return type;
    }

    public DataItem type(String type) {
        this.type = type;
        return this;
    }
}
