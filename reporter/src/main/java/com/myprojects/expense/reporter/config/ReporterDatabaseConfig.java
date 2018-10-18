package com.myprojects.expense.reporter.config;

import com.myprojects.expense.reporter.dao.DayReportDao;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = DayReportDao.class)
public class ReporterDatabaseConfig {

}
