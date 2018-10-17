package com.myprojects.expense.reporter.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myprojects.expense.reporter.model.ReportData;
import org.postgresql.util.PGobject;

import javax.persistence.AttributeConverter;
import java.io.IOException;
import java.sql.SQLException;

public class ReportDataJpaConverter implements AttributeConverter<ReportData, Object> {

    @Override
    public Object convertToDatabaseColumn(ReportData reportData) {
        try {

            PGobject x = new PGobject();
            x.setType("jsonb");
            x.setValue(new ObjectMapper().writeValueAsString(reportData));
            return x;
        } catch (JsonProcessingException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ReportData convertToEntityAttribute(Object dbData) {
//        try {
//            return new ObjectMapper().readValue(dbData, ReportData.class);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        return new ReportData();
    }

}
