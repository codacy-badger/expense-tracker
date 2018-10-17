package com.myprojects.expense.reporter.dao;

import com.myprojects.expense.reporter.model.DayReport;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface DayReportDao extends CrudRepository<DayReport, UUID> {

}
