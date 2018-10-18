package com.myprojects.expense.reporter.service;

import com.myprojects.expense.messages.EventProtos;
import com.myprojects.expense.reporter.dao.DayReportDao;
import com.myprojects.expense.reporter.model.DayReport;
import com.myprojects.expense.reporter.model.ReportData;
import com.myprojects.expense.reporter.model.ReportTransaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class DefaultTransactionEventHandler implements TransactionEventHandler {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final DayReportDao dayReportDao;
    private final ReportService reportService;

    public DefaultTransactionEventHandler(DayReportDao dayReportDao, ReportService reportService) {
        this.dayReportDao = dayReportDao;
        this.reportService = reportService;
    }

    @Override
    public void handleTransactionEvent(EventProtos.Event event) {
        if (event.getType() == EventProtos.EventType.CREATE) {
            handleCreateEvent(event.getTransactionId(), event.getTransactionType(), event.getTransactionData());
        } else if (event.getType() == EventProtos.EventType.DELETE) {
            handleDeleteEvent(event.getTransactionId(), event.getTransactionType(), event.getTransactionData());
        } else {
            handleDeleteEvent(event.getTransactionId(), event.getTransactionType(), event.getOldTransactionData());
            handleCreateEvent(event.getTransactionId(), event.getTransactionType(), event.getTransactionData());
        }
    }

    private void handleCreateEvent(String transactionId, boolean transactionType, EventProtos.EventData transactionData) {
        DayReport dayReport = getDayReport(transactionData.getDate());

        ReportTransaction transaction = new ReportTransaction();
        transaction.setId(transactionId);
        transaction.setAmount(new BigDecimal(transactionData.getAmount()));
        transaction.setCategory(transactionData.getCategory());

        ReportData data = dayReport.getData();
        if (transactionType) {
            data.getIncomes().add(transaction);
            data.setTotalIncomes(data.getTotalIncomes().add(transaction.getAmount()));
        } else {
            data.getExpenses().add(transaction);
            data.setTotalExpenses(data.getTotalExpenses().add(transaction.getAmount()));
        }
        updateTotal(data);

        dayReportDao.save(dayReport);
    }

    private void handleDeleteEvent(String transactionId, boolean transactionType, EventProtos.EventData transactionData) {
        DayReport dayReport = getDayReport(transactionData.getDate());

        ReportData data = dayReport.getData();
        if (transactionType) {
            BigDecimal transactionAmount = removeTransactionFromList(transactionId, data.getIncomes());
            data.setTotalIncomes(data.getTotalIncomes().subtract(transactionAmount));
        } else {
            BigDecimal transactionAmount = removeTransactionFromList(transactionId, data.getExpenses());
            data.setTotalExpenses(data.getTotalExpenses().subtract(transactionAmount));
        }
        updateTotal(data);

        dayReportDao.save(dayReport);
    }

    private static void updateTotal(ReportData data) {
        data.setTotal(data.getTotalIncomes().subtract(data.getTotalExpenses()));
    }

    private static BigDecimal removeTransactionFromList(String transactionId, List<ReportTransaction> transactions) {
        Optional<ReportTransaction> transactionToBeRemoved = transactions.stream()
                .filter(transaction -> transaction.getId().equals(transactionId))
                .findFirst();

        if (transactionToBeRemoved.isPresent()) {
            transactions.remove(transactionToBeRemoved.get());
            return transactionToBeRemoved.get().getAmount();
        }
        return BigDecimal.ZERO;
    }

    private DayReport getDayReport(String date) {
        LocalDate localDate = LocalDate.parse(date, DATE_FORMATTER);
        return reportService.getDayReport(localDate.getYear(), localDate.getMonthValue(),
                localDate.getDayOfMonth());
    }

}
