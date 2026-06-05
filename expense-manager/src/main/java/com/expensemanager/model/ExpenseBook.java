package com.expensemanager.model;

import java.time.LocalDate;

/**
 * Represents a row in Expense_Book sheet — monthly ledger entries.
 * S.NO | Name (e.g. JUN-2026) | Created (Excel date serial)
 */
public class ExpenseBook {
    private int sno;
    private String name;       // e.g. "JUN-2026"
    private LocalDate created;

    public ExpenseBook() {}
    public ExpenseBook(int sno, String name, LocalDate created) {
        this.sno = sno;
        this.name = name;
        this.created = created;
    }

    public int getSno() { return sno; }
    public void setSno(int sno) { this.sno = sno; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getCreated() { return created; }
    public void setCreated(LocalDate created) { this.created = created; }
}
