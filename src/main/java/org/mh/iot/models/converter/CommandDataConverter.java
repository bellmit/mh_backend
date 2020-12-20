package org.mh.iot.models.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mh.iot.models.DataItem;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by evolshan on 18.12.2020.
 */
@Converter(autoApply = true)
public class CommandDataConverter implements AttributeConverter<List<DataItem>, String> {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<DataItem> dataItems) {
        try {
            return objectMapper.writeValueAsString(dataItems);
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    @Override
    public List<DataItem> convertToEntityAttribute(String s) {
        TypeReference<ArrayList<DataItem>> typeRef = new TypeReference<ArrayList<DataItem>>(){};
        try {
            return objectMapper.readValue(s, typeRef);
        } catch (IOException e) {
            return null;
        }
    }
}
