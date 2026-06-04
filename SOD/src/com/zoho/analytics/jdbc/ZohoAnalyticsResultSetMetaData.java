package com.zoho.analytics.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ZohoAnalyticsResultSetMetaData extends ZohoAnalyticsWrapper implements ResultSetMetaData {
   private String[] colNameArr;
   private int[] sqlTypeArr;
   private String[] tblNameArr;
   private String[] subTypeArr;
   private int[] subTypeIdArr;
   private String workspaceName;

   ZohoAnalyticsResultSetMetaData(String[] colNameArr, int[] sqlTypeArr, String[] tblNameArr, String[] subTypeArr, int[] subTypeIdArr, String workspaceName) {
      this.colNameArr = colNameArr;
      this.sqlTypeArr = sqlTypeArr;
      this.tblNameArr = tblNameArr;
      this.subTypeArr = subTypeArr;
      this.subTypeIdArr = subTypeIdArr;
      this.workspaceName = workspaceName;
   }

   public String getCatalogName(int columnIndex) throws SQLException {
      return this.workspaceName;
   }

   public String getColumnClassName(int columnIndex) throws SQLException {
      this.checkColumnLimit(columnIndex);
      int type = this.sqlTypeArr[columnIndex - 1];
      switch(type) {
      case -7:
      case 16:
         return "java.lang.Boolean";
      case -6:
      case 4:
      case 5:
         return "java.lang.Integer";
      case -5:
         return "java.lang.Long";
      case -1:
      case 1:
      case 12:
         return "java.lang.String";
      case 2:
      case 3:
         return "java.math.BigDecimal";
      case 6:
      case 8:
         return "java.lang.Double";
      case 7:
         return "java.lang.Float";
      case 91:
         return "java.sql.Date";
      case 92:
         return "java.sql.Time";
      case 93:
         return "java.sql.Timestamp";
      default:
         return "java.lang.Object";
      }
   }

   public int getColumnCount() throws SQLException {
      return this.colNameArr.length;
   }

   public int getColumnDisplaySize(int columnIndex) throws SQLException {
      return 100;
   }

   public String getColumnLabel(int columnIndex) throws SQLException {
      return this.getColumnName(columnIndex);
   }

   public String getColumnName(int columnIndex) throws SQLException {
      this.checkColumnLimit(columnIndex);
      return this.colNameArr[columnIndex - 1];
   }

   public int getColumnType(int columnIndex) throws SQLException {
      this.checkColumnLimit(columnIndex);
      return this.sqlTypeArr[columnIndex - 1];
   }

   public String getColumnTypeName(int columnIndex) throws SQLException {
      this.checkColumnLimit(columnIndex);
      return this.subTypeArr[columnIndex - 1];
   }

   public int getPrecision(int columnIndex) throws SQLException {
      this.checkColumnLimit(columnIndex);
      int var10000 = this.subTypeIdArr[columnIndex - 1];
      return 10;
   }

   public int getScale(int columnIndex) throws SQLException {
      this.checkColumnLimit(columnIndex);
      int var10000 = this.subTypeIdArr[columnIndex - 1];
      return 10;
   }

   public String getSchemaName(int columnIndex) throws SQLException {
      return "";
   }

   public String getTableName(int columnIndex) throws SQLException {
      this.checkColumnLimit(columnIndex);
      return this.tblNameArr[columnIndex - 1];
   }

   public boolean isAutoIncrement(int columnIndex) throws SQLException {
      this.checkColumnLimit(columnIndex);
      int subType = this.subTypeIdArr[columnIndex - 1];
      return subType == 18;
   }

   public boolean isCaseSensitive(int columnIndex) throws SQLException {
      return false;
   }

   public boolean isCurrency(int columnIndex) throws SQLException {
      this.checkColumnLimit(columnIndex);
      int subType = this.subTypeIdArr[columnIndex - 1];
      return subType == 7;
   }

   public boolean isDefinitelyWritable(int columnIndex) throws SQLException {
      return false;
   }

   public int isNullable(int columnIndex) throws SQLException {
      return 0;
   }

   public boolean isReadOnly(int columnIndex) throws SQLException {
      return true;
   }

   public boolean isSearchable(int columnIndex) throws SQLException {
      return true;
   }

   public boolean isSigned(int columnIndex) throws SQLException {
      this.checkColumnLimit(columnIndex);
      int subType = this.subTypeIdArr[columnIndex - 1];
      return subType != 5;
   }

   public boolean isWritable(int param) throws SQLException {
      return false;
   }

   private void checkColumnLimit(int columnIndex) throws SQLException {
      if (columnIndex < 1 || columnIndex > this.colNameArr.length) {
         throw new SQLException("Column index out of range");
      }
   }
}
