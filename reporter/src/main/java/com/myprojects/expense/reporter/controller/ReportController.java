package com.myprojects.expense.reporter.controller;

import com.myprojects.expense.reporter.model.DayReport;
import com.myprojects.expense.reporter.service.ReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = ReportController.PATH)
public class ReportController {

    public static final String PATH = "/reports";

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping(path = "/{year}/{month}/{day}")
    public DayReport getDayReport(@PathVariable Integer year, @PathVariable Integer month, @PathVariable Integer day) {
        return reportService.getDayReport(year, month, day);
    }

}
