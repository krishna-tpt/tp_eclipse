package com.expense.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;

public class Transaction {
    private int id;
    private Date transactionDate;
    private Time transactionTime;
    private BigDecimal amount;
    private String category;
    private String note;
    private String type; // "income" or "expense"
    private Map<String, String> customValues = new HashMap<>();

    public Transaction() {}

    public Transaction(int id, Date date, Time time, BigDecimal amount,
                       String category, String note, String type) {
        this.id = id;
        this.transactionDate = date;
        this.transactionTime = time;
        this.amount = amount;
        this.category = category;
        this.note = note;
        this.type = type;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Date getTransactionDate() { return transactionDate; }
    public void setTransactionDate(Date d) { this.transactionDate = d; }

    public Time getTransactionTime() { return transactionTime; }
    public void setTransactionTime(Time t) { this.transactionTime = t; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal a) { this.amount = a; }

    public String getCategory() { return category; }
    public void setCategory(String c) { this.category = c; }

    public String getNote() { return note; }
    public void setNote(String n) { this.note = n; }

    public String getType() { return type; }
    public void setType(String t) { this.type = t; }

    public Map<String, String> getCustomValues() { return customValues; }
    public void setCustomValues(Map<String, String> m) { this.customValues = m; }
    public void addCustomValue(String key, String value) { this.customValues.put(key, value); }
}
