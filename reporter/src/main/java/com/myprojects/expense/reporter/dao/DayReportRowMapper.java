package com.myprojects.expense.reporter.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myprojects.expense.reporter.model.DayReport;
import com.myprojects.expense.reporter.model.ReportData;
import org.springframework.jdbc.core.RowMapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DayReportRowMapper implements RowMapper<DayReport>{

    private ObjectMapper objectMapper;

    public DayReportRowMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public DayReport mapRow(ResultSet rs, int rowNum) throws SQLException {
        try {
            ReportData data = objectMapper.readValue(rs.getString(3), ReportData.class);

            DayReport report = new DayReport();
            report.setId(UUID.fromString(rs.getString(1)));
            report.setDate(rs.getDate(2).toLocalDate());
            report.setData(data);
            return report;
        } catch (IOException e) {
            throw new RuntimeException("An error occurred while reading record", e);
        }
    }

}
