package com.myprojects.expense.reporter.service;

import com.myprojects.expense.messages.EventProtos.Event;
import com.myprojects.expense.messages.EventProtos.EventData;
import com.myprojects.expense.messages.EventProtos.EventType;
import com.myprojects.expense.reporter.config.ReporterServiceConfig;
import com.myprojects.expense.reporter.dao.DayReportDao;
import com.myprojects.expense.reporter.model.DayReport;
import com.myprojects.expense.reporter.model.ReportDate;
import com.myprojects.expense.reporter.model.ReportStats;
import com.myprojects.expense.reporter.model.ReportTransaction;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static java.math.BigDecimal.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;

@ContextConfiguration(classes = ReporterServiceConfig.class)
@TestExecutionListeners(MockitoTestExecutionListener.class)
public class DefaultTransactionEventHandlerTests extends AbstractTestNGSpringContextTests {

    private static final BigDecimal FIVE = new BigDecimal("5");

    @MockBean
    private DayReportDao dayReportDao;

    @Autowired
    private DefaultTransactionEventHandler defaultTransactionEventHandler;

    @AfterMethod
    public void resetMocks() throws Exception {
        Mockito.reset(dayReportDao);
    }

    @Test
    public void testCreateEventForExpenseTransaction() throws Exception {
        DayReport report = handleEvent(newCreateEvent("some_id", false));
        assertThat(report.getIncomes(), hasSize(0));
        assertThat(report.getExpenses(), hasSize(1));
        assertThat(report.getExpenses().get(0).getId(), is("some_id"));
        assertThat(report.getExpenses().get(0).getAmount(), is(ONE));
        assertThat(report.getExpenses().get(0).getCategory(), is("abc"));
        assertThat(report.getStats().getTotal(), is(ONE.negate()));
        assertThat(report.getStats().getTotalIncomes(), is(ZERO));
        assertThat(report.getStats().getTotalExpenses(), is(ONE));
    }

    @Test
    public void testCreateEventForIncomeTransaction() throws Exception {
        DayReport report = handleEvent(newCreateEvent("some_id", true));
        assertThat(report.getIncomes(), hasSize(1));
        assertThat(report.getIncomes().get(0).getId(), is("some_id"));
        assertThat(report.getIncomes().get(0).getAmount(), is(ONE));
        assertThat(report.getIncomes().get(0).getCategory(), is("abc"));
        assertThat(report.getExpenses(), hasSize(0));
        assertThat(report.getStats().getTotal(), is(ONE));
        assertThat(report.getStats().getTotalIncomes(), is(ONE));
        assertThat(report.getStats().getTotalExpenses(), is(ZERO));
    }

    @Test
    public void testCreateEventWithInitialReport() throws Exception {
        mockInitialReport("id1", "id2");
        DayReport report = handleEvent(newCreateEvent("id3", false));
        assertThat(report.getIncomes(), hasSize(1));
        assertThat(report.getExpenses(), hasSize(2));
        assertThat(report.getStats().getTotal(), is(new BigDecimal("4")));
        assertThat(report.getStats().getTotalIncomes(), is(TEN));
        assertThat(report.getStats().getTotalExpenses(), is(new BigDecimal("6")));
    }

    @Test
    public void testDeleteEventForIncomeTransaction() throws Exception {
        mockInitialReport("id1", "id2");
        DayReport report = handleEvent(newDeleteEvent("id1", true));
        assertThat(report.getIncomes(), hasSize(0));
        assertThat(report.getExpenses(), hasSize(1));
        assertThat(report.getStats().getTotal(), is(FIVE.negate()));
        assertThat(report.getStats().getTotalIncomes(), is(ZERO));
        assertThat(report.getStats().getTotalExpenses(), is(FIVE));
    }

    @Test
    public void testDeleteEventForExpenseTransaction() throws Exception {
        mockInitialReport("id1", "id2");
        DayReport report = handleEvent(newDeleteEvent("id2", false));
        assertThat(report.getIncomes(), hasSize(1));
        assertThat(report.getExpenses(), hasSize(0));
        assertThat(report.getStats().getTotal(), is(TEN));
        assertThat(report.getStats().getTotalIncomes(), is(TEN));
        assertThat(report.getStats().getTotalExpenses(), is(ZERO));
    }

    @Test
    public void testDeleteEventNoInitialReport() throws Exception {
        DayReport report = handleEvent(newDeleteEvent("some_id", false));
        assertThat(report.getIncomes(), hasSize(0));
        assertThat(report.getExpenses(), hasSize(0));
        assertThat(report.getStats().getTotal(), is(ZERO));
        assertThat(report.getStats().getTotalIncomes(), is(ZERO));
        assertThat(report.getStats().getTotalExpenses(), is(ZERO));
    }

    @Test
    public void testModifyEventForIncomeTransaction() throws Exception {
        mockInitialReport("id1", "id2");
        DayReport report = handleEvent(newModifyEvent("id1", true));
        assertThat(report.getIncomes(), hasSize(1));
        assertThat(report.getExpenses(), hasSize(1));
        assertThat(report.getStats().getTotal(), is(new BigDecimal("15")));
        assertThat(report.getStats().getTotalIncomes(), is(new BigDecimal("20")));
        assertThat(report.getStats().getTotalExpenses(), is(FIVE));
    }

    @Test
    public void testModifyEventForExpenseTransaction() throws Exception {
        mockInitialReport("id1", "id2");
        DayReport report = handleEvent(newModifyEvent("id2", false));
        assertThat(report.getIncomes(), hasSize(1));
        assertThat(report.getExpenses(), hasSize(1));
        assertThat(report.getStats().getTotal(), is(TEN.negate()));
        assertThat(report.getStats().getTotalIncomes(), is(TEN));
        assertThat(report.getStats().getTotalExpenses(), is(new BigDecimal("20")));
    }

    private DayReport handleEvent(Event event) {
        Mockito.when(dayReportDao.save(any())).thenAnswer(returnsFirstArg());

        defaultTransactionEventHandler.handleTransactionEvent(event);

        ArgumentCaptor<DayReport> requestCaptor = ArgumentCaptor.forClass(DayReport.class);

        Mockito.verify(dayReportDao, atLeast(1)).save(requestCaptor.capture());
        DayReport report = requestCaptor.getValue();
        assertThat(report, notNullValue());
        assertThat(report.getDate().getDay(), is(1));
        assertThat(report.getDate().getMonth(), is(12));
        assertThat(report.getDate().getYear(), is(2000));

        return report;
    }

    private void mockInitialReport(String incomeTransactionId, String expenseTransactionId) {
        ArrayList<ReportTransaction> incomes = new ArrayList<>();
        incomes.add(new ReportTransaction(incomeTransactionId, TEN, "abc"));

        ArrayList<ReportTransaction> expenses = new ArrayList<>();
        expenses.add(new ReportTransaction(expenseTransactionId, FIVE, "abc"));

        DayReport initialReport = new DayReport();
        initialReport.setDate(new ReportDate(LocalDate.of(2000, 12, 1)));
        initialReport.setIncomes(incomes);
        initialReport.setExpenses(expenses);
        initialReport.setStats(new ReportStats(FIVE, TEN, FIVE));
        Mockito.when(dayReportDao.findOne(any())).thenReturn(Optional.of(initialReport));
    }

    private static Event newCreateEvent(String transactionId, boolean isIncome) {
        return Event.newBuilder()
                .setType(EventType.CREATE)
                .setTransactionId(transactionId)
                .setTransactionType(isIncome)
                .setTransactionData(EventData.newBuilder()
                        .setAmount("1")
                        .setCategory("abc")
                        .setDate("01/12/2000"))
                .build();
    }

    private static Event newDeleteEvent(String transactionId, boolean isIncome) {
        return Event.newBuilder()
                .setType(EventType.DELETE)
                .setTransactionId(transactionId)
                .setTransactionType(isIncome)
                .setTransactionData(EventData.newBuilder()
                        .setCategory("abc")
                        .setDate("01/12/2000"))
                .build();
    }

    private static Event newModifyEvent(String transactionId, boolean isIncome) {
        return Event.newBuilder()
                .setType(EventType.MODIFY)
                .setTransactionId(transactionId)
                .setTransactionType(isIncome)
                .setTransactionData(EventData.newBuilder()
                        .setAmount("20")
                        .setCategory("abc")
                        .setDate("01/12/2000"))
                .setOldTransactionData(EventData.newBuilder()
                        .setCategory("xyz")
                        .setDate("01/12/2000"))
                .build();
    }

}