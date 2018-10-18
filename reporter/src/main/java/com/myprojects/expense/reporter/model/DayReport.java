package com.myprojects.expense.reporter.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

public class DayReport {

    private UUID id;
    private LocalDate date;
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

    public static DayReport createEmptyReport(LocalDate date) {
        ReportData data = new ReportData();
        data.setTotal(BigDecimal.ZERO);
        data.setTotalExpenses(BigDecimal.ZERO);
        data.setTotalIncomes(BigDecimal.ZERO);
        data.setExpenses(new ArrayList<>());
        data.setIncomes(new ArrayList<>());

        DayReport emptyReport = new DayReport();
        emptyReport.setDate(date);
        emptyReport.setData(data);

        return emptyReport;
    }
}
