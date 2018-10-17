package com.myprojects.expense.reporter.config;

import com.myprojects.expense.reporter.dao.DayReportDao;
import com.myprojects.expense.reporter.model.DayReport;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackageClasses = DayReportDao.class)
@EntityScan(basePackageClasses = DayReport.class)
public class ReporterDatabaseConfig {

}
