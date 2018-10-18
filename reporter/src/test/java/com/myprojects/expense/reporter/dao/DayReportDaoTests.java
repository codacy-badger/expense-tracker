package com.myprojects.expense.reporter.dao;


import com.myprojects.expense.reporter.config.ReporterDatabaseConfig;
import com.myprojects.expense.reporter.model.DayReport;
import com.myprojects.expense.reporter.model.ReportData;
import com.myprojects.expense.reporter.model.ReportTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = ReporterDatabaseConfig.class)
public class DayReportDaoTests extends AbstractTestNGSpringContextTests {

    @Autowired
    private DayReportDao dayReportDao;

    private UUID createdReportId;

    @Test
    public void testCreate() throws Exception {
        ArrayList<ReportTransaction> incomes = new ArrayList<>();
        incomes.add(new ReportTransaction("id1", TEN, "abc"));

        ArrayList<ReportTransaction> expenses = new ArrayList<>();
        expenses.add(new ReportTransaction("id2", TEN, "xyz"));

        ReportData reportData = new ReportData();
        reportData.setTotal(ZERO);
        reportData.setTotalIncomes(TEN);
        reportData.setTotalExpenses(TEN);
        reportData.setIncomes(incomes);
        reportData.setExpenses(expenses);

        DayReport report = new DayReport();
        report.setDate(LocalDate.of(2000, 12, 1));
        report.setData(reportData);

        createdReportId = dayReportDao.save(report).getId();

        assertThat(createdReportId, notNullValue());
    }

    @Test(dependsOnMethods = "testCreate")
    public void testGetById() throws Exception {
        DayReport report = dayReportDao.get(createdReportId);

        assertThat(report.getId(), is(createdReportId));
        assertThat(report.getDate().getDayOfMonth(), is(1));
        assertThat(report.getDate().getMonthValue(), is(12));
        assertThat(report.getDate().getYear(), is(2000));
        assertThat(report.getData().getIncomes(), hasSize(1));
        assertThat(report.getData().getIncomes().get(0).getId(), is("id1"));
        assertThat(report.getData().getIncomes().get(0).getAmount(), is(TEN));
        assertThat(report.getData().getIncomes().get(0).getCategory(), is("abc"));
        assertThat(report.getData().getExpenses(), hasSize(1));
        assertThat(report.getData().getExpenses().get(0).getId(), is("id2"));
        assertThat(report.getData().getExpenses().get(0).getAmount(), is(TEN));
        assertThat(report.getData().getExpenses().get(0).getCategory(), is("xyz"));
        assertThat(report.getData().getTotal(), is(ZERO));
        assertThat(report.getData().getTotalExpenses(), is(TEN));
        assertThat(report.getData().getTotalIncomes(), is(TEN));
    }
}