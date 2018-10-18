package com.myprojects.expense.reporter.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myprojects.expense.reporter.model.DayReport;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.UUID;

@Repository
public class DayReportDao {

    private static final String JSONB_DATA_TYPE = "jsonb";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final DayReportRowMapper ROW_MAPPER = new DayReportRowMapper(OBJECT_MAPPER);
    private static final String SELECT_SQL =
            "SELECT id, report_date, data FROM reporter_v1.day_report where id = ?";
    private static final String SELECT_BY_DATE_SQL =
            "SELECT id, report_date, data FROM reporter_v1.day_report where report_date = ?";
    private static final String INSERT_SQL =
            "INSERT INTO reporter_v1.day_report (id, report_date, data) VALUES (?, ?, ?)";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public DayReport save(DayReport report) {
        try {
            String reportDataJson = OBJECT_MAPPER.writeValueAsString(report.getData());

            PGobject reportData = new PGobject();
            reportData.setType(JSONB_DATA_TYPE);
            reportData.setValue(reportDataJson);

            UUID id = report.getId() == null ? UUID.randomUUID() : report.getId();

            jdbcTemplate.update(INSERT_SQL, id, report.getDate(), reportData);

            report.setId(id);
            return report;
        } catch (JsonProcessingException | SQLException e) {
            throw new RuntimeException("An error occurred while saving the report", e);
        }
    }

    public DayReport get(UUID id) {
        return jdbcTemplate.queryForObject(SELECT_SQL, ROW_MAPPER, id);
    }

    public DayReport get(LocalDate date) {
        return jdbcTemplate.queryForObject(SELECT_BY_DATE_SQL, ROW_MAPPER, date);
    }

}
