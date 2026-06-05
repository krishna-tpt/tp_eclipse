package com.expensemanager.model;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents a single Income or Expense transaction.
 * Supports dynamic/custom columns via extraFields map.
 */
public class Transaction {

    public enum Type { INCOME, EXPENSE }

    private Type type;
    private LocalDateTime dateTime;
    private double amount;
    private String category;
    private String payment;   // optional
    private String note;
    // Custom columns — order preserved (LinkedHashMap)
    private Map<String, String> extraFields = new LinkedHashMap<>();

    public Transaction() {}

    public Transaction(Type type, LocalDateTime dateTime, double amount,
                       String category, String payment, String note) {
        this.type = type;
        this.dateTime = dateTime;
        this.amount = amount;
        this.category = category;
        this.payment = payment;
        this.note = note;
    }

    // --- Getters / Setters ---

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getPayment() { return payment; }
    public void setPayment(String payment) { this.payment = payment; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public Map<String, String> getExtraFields() { return extraFields; }
    public void setExtraFields(Map<String, String> extraFields) { this.extraFields = extraFields; }
    public void addExtraField(String key, String value) { this.extraFields.put(key, value); }

    @Override
    public String toString() {
        return "Transaction{type=" + type + ", amount=" + amount +
               ", category='" + category + "', dateTime=" + dateTime + "}";
    }
}
