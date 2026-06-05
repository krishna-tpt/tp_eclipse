package com.expensemanager.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds configuration data from the Config sheet.
 * Categories can belong to Income, Expense, or Both.
 */
public class AppConfig {

    private List<String> incomeCategories = new ArrayList<>();
    private List<String> expenseCategories = new ArrayList<>();
    private List<String> incomePaymentModes = new ArrayList<>();
    private List<String> expensePaymentModes = new ArrayList<>();
    // Extra custom columns added dynamically
    private List<String> incomeCustomColumns = new ArrayList<>();
    private List<String> expenseCustomColumns = new ArrayList<>();

    public List<String> getIncomeCategories() { return incomeCategories; }
    public void setIncomeCategories(List<String> incomeCategories) { this.incomeCategories = incomeCategories; }

    public List<String> getExpenseCategories() { return expenseCategories; }
    public void setExpenseCategories(List<String> expenseCategories) { this.expenseCategories = expenseCategories; }

    public List<String> getIncomePaymentModes() { return incomePaymentModes; }
    public void setIncomePaymentModes(List<String> incomePaymentModes) { this.incomePaymentModes = incomePaymentModes; }

    public List<String> getExpensePaymentModes() { return expensePaymentModes; }
    public void setExpensePaymentModes(List<String> expensePaymentModes) { this.expensePaymentModes = expensePaymentModes; }

    public List<String> getIncomeCustomColumns() { return incomeCustomColumns; }
    public void setIncomeCustomColumns(List<String> c) { this.incomeCustomColumns = c; }

    public List<String> getExpenseCustomColumns() { return expenseCustomColumns; }
    public void setExpenseCustomColumns(List<String> c) { this.expenseCustomColumns = c; }
}
