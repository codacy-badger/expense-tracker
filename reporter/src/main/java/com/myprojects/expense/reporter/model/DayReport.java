package com.myprojects.expense.reporter.model;

import com.myprojects.expense.reporter.dao.LocalDateJpaConverter;
import com.myprojects.expense.reporter.dao.ReportDataJpaConverter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "day_report", schema = "reporter_v1")
public class DayReport {

    @Id
    @GeneratedValue(generator = "uuid-generator")
    @GenericGenerator(name = "uuid-generator", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "report_date")
    @Convert(converter = LocalDateJpaConverter.class)
    private LocalDate date;

    @Column
    @Convert(converter = ReportDataJpaConverter.class)
    private ReportData data;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public ReportData getData() {
        return data;
    }

    public void setData(ReportData data) {
        this.data = data;
    }
}
