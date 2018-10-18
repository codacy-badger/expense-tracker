package com.myprojects.expense.reporter.service;

import com.myprojects.expense.reporter.dao.DayReportDao;
import com.myprojects.expense.reporter.model.DayReport;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class DefaultReportService implements ReportService {

    private final DayReportDao dayReportDao;

    public DefaultReportService(DayReportDao dayReportDao) {
        this.dayReportDao = dayReportDao;
    }

    @Override
    public DayReport getDayReport(int year, int month, int day) {
        LocalDate date = LocalDate.of(year, month, day);
        DayReport report = dayReportDao.get(date);
        return Optional.ofNullable(report).orElseGet(() -> {
            DayReport emptyReport = DayReport.createEmptyReport(date);
            return dayReportDao.save(emptyReport);
        });
    }

}
