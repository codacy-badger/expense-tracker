package com.myprojects.expense.reporter.service;


import com.myprojects.expense.reporter.config.ReporterServiceConfig;
import com.myprojects.expense.reporter.dao.DayReportDao;
import com.myprojects.expense.reporter.model.DayReport;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.time.LocalDate;

import static java.math.BigDecimal.ZERO;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;

@ContextConfiguration(classes = ReporterServiceConfig.class)
@TestExecutionListeners(MockitoTestExecutionListener.class)
public class DefaultReportServiceTests extends AbstractTestNGSpringContextTests {

    @MockBean
    private DayReportDao mockReportDao;

    @Autowired
    private DefaultReportService reportService;

    @Test
    public void testGetReportByDate() throws Exception {
        DayReport expectedReport = new DayReport();

        Mockito.when(mockReportDao.get(any(LocalDate.class)))
                .thenReturn(expectedReport);

        DayReport report = reportService.getDayReport(2018, 12, 1);
        assertThat(report, is(expectedReport));

        ArgumentCaptor<LocalDate> argumentCaptor = ArgumentCaptor.forClass(LocalDate.class);
        Mockito.verify(mockReportDao).get(argumentCaptor.capture());
        LocalDate exampleDate = argumentCaptor.getValue();
        assertThat(exampleDate.getYear(), is(2018));
        assertThat(exampleDate.getMonthValue(), is(12));
        assertThat(exampleDate.getDayOfMonth(), is(1));
    }

    @Test
    public void testGetReportCreatesEmptyReportIfItDoesNotExist() throws Exception {
        Mockito.when(mockReportDao.get(any(LocalDate.class)))
                .thenReturn(null);

        Mockito.when(mockReportDao.save(any()))
                .thenAnswer(returnsFirstArg());

        DayReport report = reportService.getDayReport(2018, 12, 1);
        assertThat(report, notNullValue());
        assertThat(report.getDate().getYear(), is(2018));
        assertThat(report.getDate().getMonthValue(), is(12));
        assertThat(report.getDate().getDayOfMonth(), is(1));
        assertThat(report.getData().getTotal(), is(ZERO));
        assertThat(report.getData().getTotalIncomes(), is(ZERO));
        assertThat(report.getData().getTotalExpenses(), is(ZERO));
        assertThat(report.getData().getIncomes(), empty());
        assertThat(report.getData().getExpenses(), empty());

        Mockito.verify(mockReportDao).save(any());
    }

}