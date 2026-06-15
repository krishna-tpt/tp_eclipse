package com.expensemanager.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Budget {

    public enum Period { WEEKLY, MONTHLY, YEARLY }

    private int        id;
    private String     category;
    private BigDecimal amount;       // budget limit
    private BigDecimal spent;        // actual spent (computed)
    private Period     period;
    private int        alertAtPct;   // alert threshold %
    private int        year;
    private Integer    month;        // null for yearly
    private boolean    active;

    // ── Computed ─────────────────────────────────────────────────────────────

    public BigDecimal getRemaining() {
        if (spent == null) return amount;
        BigDecimal r = amount.subtract(spent);
        return r.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : r;
    }

    public double getSpentPct() {
        if (spent == null || amount.compareTo(BigDecimal.ZERO) == 0) return 0;
        return spent.multiply(BigDecimal.valueOf(100))
                    .divide(amount, 2, RoundingMode.HALF_UP)
                    .doubleValue();
    }

    public boolean isOverBudget()  { return getSpentPct() > 100; }
    public boolean isAtAlert()     { return getSpentPct() >= alertAtPct && !isOverBudget(); }
    public boolean isSafe()        { return getSpentPct() < alertAtPct; }

    /** CSS class for progress bar and badge */
    public String getStatusClass() {
        double pct = getSpentPct();
        if (pct > 100)           return "budget-over";
        if (pct >= alertAtPct)   return "budget-alert";
        if (pct >= alertAtPct * 0.7) return "budget-warn";
        return "budget-safe";
    }

    public String getStatusLabel() {
        double pct = getSpentPct();
        if (pct > 100)         return "Over Budget!";
        if (pct >= alertAtPct) return "Alert: " + (int)pct + "% spent";
        return (int)pct + "% used";
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────
    public int getId()                   { return id; }
    public void setId(int v)             { this.id = v; }
    public String getCategory()          { return category; }
    public void setCategory(String v)    { this.category = v; }
    public BigDecimal getAmount()        { return amount; }
    public void setAmount(BigDecimal v)  { this.amount = v; }
    public BigDecimal getSpent()         { return spent; }
    public void setSpent(BigDecimal v)   { this.spent = v; }
    public Period getPeriod()            { return period; }
    public void setPeriod(Period v)      { this.period = v; }
    public int getAlertAtPct()           { return alertAtPct; }
    public void setAlertAtPct(int v)     { this.alertAtPct = v; }
    public int getYear()                 { return year; }
    public void setYear(int v)           { this.year = v; }
    public Integer getMonth()            { return month; }
    public void setMonth(Integer v)      { this.month = v; }
    public boolean isActive()            { return active; }
    public void setActive(boolean v)     { this.active = v; }
}
