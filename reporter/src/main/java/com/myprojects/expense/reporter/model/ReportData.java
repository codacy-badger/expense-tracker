package com.myprojects.expense.reporter.model;

import java.math.BigDecimal;
import java.util.List;

public class ReportData {

    private BigDecimal total;
    private BigDecimal totalIncomes;
    private BigDecimal totalExpenses;
    private List<ReportTransaction> incomes;
    private List<ReportTransaction> expenses;

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getTotalIncomes() {
        return totalIncomes;
    }

    public void setTotalIncomes(BigDecimal totalIncomes) {
        this.totalIncomes = totalIncomes;
    }

    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(BigDecimal totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public List<ReportTransaction> getIncomes() {
        return incomes;
    }

    public void setIncomes(List<ReportTransaction> incomes) {
        this.incomes = incomes;
    }

    public List<ReportTransaction> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<ReportTransaction> expenses) {
        this.expenses = expenses;
    }
}
