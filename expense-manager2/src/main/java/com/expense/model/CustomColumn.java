package com.expense.model;

public class CustomColumn {
    private int id;
    private String tableName;
    private String columnName;
    private String columnLabel;
    private String columnType;

    public CustomColumn() {}

    public CustomColumn(int id, String tableName, String columnName,
                        String columnLabel, String columnType) {
        this.id = id;
        this.tableName = tableName;
        this.columnName = columnName;
        this.columnLabel = columnLabel;
        this.columnType = columnType;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTableName() { return tableName; }
    public void setTableName(String t) { this.tableName = t; }

    public String getColumnName() { return columnName; }
    public void setColumnName(String c) { this.columnName = c; }

    public String getColumnLabel() { return columnLabel; }
    public void setColumnLabel(String l) { this.columnLabel = l; }

    public String getColumnType() { return columnType; }
    public void setColumnType(String t) { this.columnType = t; }
}
