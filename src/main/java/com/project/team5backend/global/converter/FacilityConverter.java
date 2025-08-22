package com.project.team5backend.global.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.team5backend.global.entity.Facility;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.List;

@Converter
public class FacilityConverter implements AttributeConverter<List<Facility>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Facility> facilities) {
        if (facilities == null || facilities.isEmpty()) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(facilities);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert facilities to JSON", e);
        }
    }

    @Override
    public List<Facility> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<Facility>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert JSON to facilities", e);
        }
    }
}