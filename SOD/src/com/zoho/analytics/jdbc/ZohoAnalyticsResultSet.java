package com.zoho.analytics.jdbc;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class ZohoAnalyticsResultSet extends ZohoAnalyticsWrapper implements ResultSet {
   private Statement stmt;
   private ArrayList<String[]> rowData = new ArrayList();
   private int currentRow = -1;
   private boolean isClosed = false;
   private String[] columnNameArr = null;
   private String[] tableNameArr = null;
   private int[] dataTypeArr = null;
   private ZohoAnalyticsResultSetMetaData rsmd = null;
   private boolean isLastValueNull = false;

   ZohoAnalyticsResultSet(Statement stmt, ArrayList<String[]> rowData, String[] columnNameArr, int[] dataTypeArr, String[] tableNameArr, ZohoAnalyticsResultSetMetaData rsmd) {
      this.rowData = rowData;
      this.columnNameArr = columnNameArr;
      this.dataTypeArr = dataTypeArr;
      this.tableNameArr = tableNameArr;
      this.rsmd = rsmd;
      this.stmt = stmt;
   }

   private ZohoAnalyticsWorkspace getCurrentAnalyticsWorkspace() throws SQLException {
      return ((ZohoAnalyticsStatement)this.stmt).getAnalyticsWorkspace();
   }

   public boolean absolute(int row) throws SQLException {
      this.checkClosed();
      boolean valid = false;
      if (this.rowData.size() == 0) {
         valid = false;
      } else {
         if (row == 0) {
            throw new SQLException("Absolute position cannot be zero");
         }

         if (row == 1) {
            valid = this.first();
         } else if (row == -1) {
            valid = this.last();
         } else if (row > this.rowData.size()) {
            this.afterLast();
            valid = false;
         } else if (row < 0) {
            row = this.rowData.size() + row + 1;
            if (row <= 0) {
               this.beforeFirst();
               valid = false;
            } else {
               valid = this.absolute(row);
            }
         } else {
            --row;
            this.currentRow = row;
            valid = true;
         }
      }

      return valid;
   }

   public void afterLast() throws SQLException {
      this.checkClosed();
      this.currentRow = this.rowData.size();
   }

   public void beforeFirst() throws SQLException {
      this.checkClosed();
      this.currentRow = -1;
   }

   public void cancelRowUpdates() throws SQLException {
   }

   public void clearWarnings() throws SQLException {
   }

   public void close() throws SQLException {
      this.rowData = new ArrayList();
      this.isClosed = true;
   }

   public void deleteRow() throws SQLException {
      throw new NotImplementedException();
   }

   public int findColumn(String columnName) throws SQLException {
      for(int i = 0; i < this.columnNameArr.length; ++i) {
         if (this.columnNameArr[i].equalsIgnoreCase(columnName)) {
            return i + 1;
         }
      }

      throw new SQLException("Column '" + columnName + "' not found in the result set");
   }

   public boolean first() throws SQLException {
      this.checkClosed();
      if (this.rowData.size() == 0) {
         return false;
      } else {
         this.currentRow = 0;
         return true;
      }
   }

   public Array getArray(int columnIndex) throws SQLException {
      throw new NotImplementedException();
   }

   public Array getArray(String columnName) throws SQLException {
      throw new NotImplementedException();
   }

   public InputStream getAsciiStream(String columnName) throws SQLException {
      return this.getBinaryStream(this.findColumn(columnName));
   }

   public InputStream getAsciiStream(int columnIndex) throws SQLException {
      return this.getBinaryStream(columnIndex);
   }

   public BigDecimal getBigDecimal(String columnName) throws SQLException {
      return this.getBigDecimal(this.findColumn(columnName));
   }

   public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
      this.checkClosePosLimit(columnIndex);
      BigDecimal val = null;
      String value = this.getValue(columnIndex);
      if (value == null) {
         return null;
      } else {
         try {
            value = this.removeChars(value);
            val = new BigDecimal(value);
            return val;
         } catch (NumberFormatException var5) {
            throw new SQLException("Cannot be converted to a BigDecimal value");
         }
      }
   }

   /** @deprecated */
   @Deprecated
   public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
      BigDecimal val = this.getBigDecimal(columnIndex);
      return val.setScale(scale);
   }

   /** @deprecated */
   @Deprecated
   public BigDecimal getBigDecimal(String columnName, int scale) throws SQLException {
      BigDecimal val = this.getBigDecimal(columnName);
      return val.setScale(scale);
   }

   public InputStream getBinaryStream(int columnIndex) throws SQLException {
      byte[] byteArr = this.getBytes(columnIndex);
      return byteArr != null ? new ByteArrayInputStream(byteArr) : null;
   }

   public InputStream getBinaryStream(String columnName) throws SQLException {
      return this.getBinaryStream(this.findColumn(columnName));
   }

   public Blob getBlob(int columnIndex) throws SQLException {
      throw new NotImplementedException();
   }

   public Blob getBlob(String columnName) throws SQLException {
      throw new NotImplementedException();
   }

   public boolean getBoolean(int columnIndex) throws SQLException {
      this.checkClosePosLimit(columnIndex);
      String value = this.getValue(columnIndex);
      return value != null && value.equalsIgnoreCase("yes");
   }

   public boolean getBoolean(String columnName) throws SQLException {
      return this.getBoolean(this.findColumn(columnName));
   }

   public byte getByte(int columnIndex) throws SQLException {
      this.checkClosePosLimit(columnIndex);
      String value = this.getValue(columnIndex);
      if (value == null) {
         return 0;
      } else {
         try {
            int dindex = value.indexOf(".");
            if (dindex != -1) {
               double val = Double.parseDouble(value);
               return (byte)((int)val);
            } else {
               long val = Long.parseLong(value);
               return (byte)((int)val);
            }
         } catch (NumberFormatException var6) {
            throw new SQLException("Column value cannot be converted to byte");
         }
      }
   }

   public byte getByte(String columnName) throws SQLException {
      return this.getByte(this.findColumn(columnName));
   }

   public byte[] getBytes(int columnIndex) throws SQLException {
      this.checkClosePosLimit(columnIndex);
      String value = this.getValue(columnIndex);
      return value == null ? null : value.getBytes();
   }

   public byte[] getBytes(String columnName) throws SQLException {
      return this.getBytes(this.findColumn(columnName));
   }

   public Reader getCharacterStream(int columnIndex) throws SQLException {
      return new StringReader(this.getString(columnIndex));
   }

   public Reader getCharacterStream(String columnName) throws SQLException {
      return this.getCharacterStream(this.findColumn(columnName));
   }

   public Clob getClob(int param) throws SQLException {
      throw new NotImplementedException();
   }

   public Clob getClob(String str) throws SQLException {
      throw new NotImplementedException();
   }

   public int getConcurrency() throws SQLException {
      return 1007;
   }

   public String getCursorName() throws SQLException {
      throw new SQLException("Positioned update is not supported");
   }

   public Date getDate(int columnIndex) throws SQLException {
      this.checkClosePosLimit(columnIndex);
      String value = this.getValue(columnIndex);
      if (value == null) {
         return null;
      } else {
         try {
            Date dt = new Date(this.getDateLongVal(value, columnIndex));
            return dt;
         } catch (Exception var4) {
            throw new SQLException("Column value cannot be converted to date");
         }
      }
   }

   public Date getDate(String columnName) throws SQLException {
      return this.getDate(this.findColumn(columnName));
   }

   public Date getDate(int param, Calendar calendar) throws SQLException {
      throw new NotImplementedException();
   }

   public Date getDate(String str, Calendar calendar) throws SQLException {
      throw new NotImplementedException();
   }

   public double getDouble(int columnIndex) throws SQLException {
      this.checkClosePosLimit(columnIndex);
      String value = this.getValue(columnIndex);
      if (value == null) {
         return 0.0D;
      } else {
         try {
            value = this.removeChars(value);
            double val = Double.parseDouble(value);
            return val;
         } catch (NumberFormatException var5) {
            throw new SQLException("Column value cannot be converted to double");
         }
      }
   }

   public double getDouble(String columnName) throws SQLException {
      return this.getDouble(this.findColumn(columnName));
   }

   public int getFetchDirection() throws SQLException {
      return 1002;
   }

   public int getFetchSize() throws SQLException {
      return 0;
   }

   public float getFloat(int columnIndex) throws SQLException {
      this.checkClosePosLimit(columnIndex);
      String value = this.getValue(columnIndex);
      if (value == null) {
         return 0.0F;
      } else {
         try {
            value = this.removeChars(value);
            float val = Float.parseFloat(value);
            return val;
         } catch (NumberFormatException var4) {
            throw new SQLException("Column value cannot be converted to float");
         }
      }
   }

   public float getFloat(String columnName) throws SQLException {
      return this.getFloat(this.findColumn(columnName));
   }

   public int getInt(String columnName) throws SQLException {
      return this.getInt(this.findColumn(columnName));
   }

   private String removeChars(String value) {
      return value.replace(",", "").replace("(", "").replace(")", "");
   }

   public int getInt(int columnIndex) throws SQLException {
      this.checkClosePosLimit(columnIndex);
      String value = this.getValue(columnIndex);
      if (value == null) {
         return 0;
      } else {
         try {
            value = this.removeChars(value);
            Integer val = Integer.parseInt(value);
            return val;
         } catch (NumberFormatException var4) {
            throw new SQLException("Column value cannot be converted to integer value");
         }
      }
   }

   public long getLong(int columnIndex) throws SQLException {
      this.checkClosePosLimit(columnIndex);
      String value = this.getValue(columnIndex);
      if (value == null) {
         return 0L;
      } else {
         try {
            value = this.removeChars(value);
            long val = Long.parseLong(value);
            return val;
         } catch (NumberFormatException var5) {
            throw new SQLException("Column value cannot be converted to long");
         }
      }
   }

   public long getLong(String columnName) throws SQLException {
      return this.getLong(this.findColumn(columnName));
   }

   public ResultSetMetaData getMetaData() throws SQLException {
      return this.rsmd;
   }

   public Object getObject(int columnIndex) throws SQLException {
      this.checkColumnLimit(columnIndex);
      int type = this.dataTypeArr[columnIndex - 1];
      switch(type) {
      case -7:
      case -6:
      case 16:
         return this.getBoolean(columnIndex);
      case -5:
         return this.getLong(columnIndex);
      case -4:
      case -3:
      case -2:
         return this.getBytes(columnIndex);
      case -1:
      case 1:
      case 12:
         return this.getString(columnIndex);
      case 2:
      case 3:
         return this.getBigDecimal(columnIndex);
      case 4:
      case 5:
         return this.getInt(columnIndex);
      case 6:
      case 8:
         return this.getDouble(columnIndex);
      case 7:
         return this.getFloat(columnIndex);
      case 91:
         return this.getDate(columnIndex);
      case 92:
         return this.getTime(columnIndex);
      case 93:
         return this.getTimestamp(columnIndex);
      default:
         return this.getString(columnIndex);
      }
   }

   public Object getObject(String columnName) throws SQLException {
      return this.getObject(this.findColumn(columnName));
   }

   public Object getObject(int columnIndex, Map map) throws SQLException {
      return this.getObject(columnIndex);
   }

   public Object getObject(String columnName, Map map) throws SQLException {
      return this.getObject(this.findColumn(columnName));
   }

   public Ref getRef(int columnIndex) throws SQLException {
      throw new NotImplementedException();
   }

   public Ref getRef(String columnName) throws SQLException {
      throw new NotImplementedException();
   }

   public int getRow() throws SQLException {
      return this.currentRow + 1;
   }

   public short getShort(String columnName) throws SQLException {
      return this.getShort(this.findColumn(columnName));
   }

   public short getShort(int columnIndex) throws SQLException {
      this.checkClosePosLimit(columnIndex);
      String value = this.getValue(columnIndex);
      if (value == null) {
         return 0;
      } else {
         try {
            short val = Short.parseShort(value);
            return val;
         } catch (NumberFormatException var4) {
            throw new SQLException("Column value cannot be converted to short");
         }
      }
   }

   public Statement getStatement() throws SQLException {
      return this.stmt;
   }

   public String getString(int columnIndex) throws SQLException {
      this.checkClosePosLimit(columnIndex);
      String value = this.getValue(columnIndex);
      return value == null ? null : value;
   }

   public String getString(String columnName) throws SQLException {
      return this.getString(this.findColumn(columnName));
   }

   public Time getTime(String columnName) throws SQLException {
      return this.getTime(this.findColumn(columnName));
   }

   public Time getTime(int columnIndex) throws SQLException {
      this.checkClosePosLimit(columnIndex);
      String value = this.getValue(columnIndex);
      if (value == null) {
         return null;
      } else {
         try {
            Time dt = new Time(this.getDateLongVal(value, columnIndex));
            return dt;
         } catch (Exception var4) {
            throw new SQLException("Column value cannot be converted to time");
         }
      }
   }

   public Time getTime(String columnName, Calendar calendar) throws SQLException {
      throw new NotImplementedException();
   }

   public Time getTime(int columnIndex, Calendar calendar) throws SQLException {
      throw new NotImplementedException();
   }

   public Timestamp getTimestamp(String columnName) throws SQLException {
      return this.getTimestamp(this.findColumn(columnName));
   }

   public Timestamp getTimestamp(int columnIndex) throws SQLException {
      this.checkClosePosLimit(columnIndex);
      String value = this.getValue(columnIndex);
      if (value == null) {
         return null;
      } else {
         try {
            Timestamp dt = new Timestamp(this.getDateLongVal(value, columnIndex));
            return dt;
         } catch (Exception var4) {
            throw new SQLException("Column value cannot be converted to timestamp");
         }
      }
   }

   private long getDateLongVal(String value, int columnIndex) throws Exception {
      String tableName = this.tableNameArr[columnIndex - 1];
      String columnName = this.columnNameArr[columnIndex - 1];
      ZohoAnalyticsWorkspace workspace = this.getCurrentAnalyticsWorkspace();
      ZohoAnalyticsColumn column = workspace.getAnalyticsView(tableName).getAnalyticsColumn(columnName);
      SimpleDateFormat dtFormat = new SimpleDateFormat(column.get("DATE_FORMAT"));
      java.util.Date dtVal = dtFormat.parse(value);
      return dtVal.getTime();
   }

   public Timestamp getTimestamp(String columnName, Calendar calendar) throws SQLException {
      throw new NotImplementedException();
   }

   public Timestamp getTimestamp(int columnIndex, Calendar calendar) throws SQLException {
      throw new NotImplementedException();
   }

   public int getType() throws SQLException {
      return 1004;
   }

   public URL getURL(int columnIndex) throws SQLException {
      throw new NotImplementedException();
   }

   public URL getURL(String columnName) throws SQLException {
      throw new NotImplementedException();
   }

   /** @deprecated */
   @Deprecated
   public InputStream getUnicodeStream(String columnName) throws SQLException {
      throw new NotImplementedException();
   }

   /** @deprecated */
   @Deprecated
   public InputStream getUnicodeStream(int columnIndex) throws SQLException {
      throw new NotImplementedException();
   }

   public SQLWarning getWarnings() throws SQLException {
      return null;
   }

   public void insertRow() throws SQLException {
      throw new NotImplementedException();
   }

   public boolean isAfterLast() throws SQLException {
      return this.currentRow >= this.rowData.size();
   }

   public boolean isBeforeFirst() throws SQLException {
      if (this.rowData.size() == 0) {
         return false;
      } else {
         return this.currentRow == -1;
      }
   }

   public boolean isFirst() throws SQLException {
      return this.currentRow == 0;
   }

   public boolean isLast() throws SQLException {
      if (this.rowData.size() == 0) {
         return false;
      } else {
         return this.currentRow == this.rowData.size() - 1;
      }
   }

   public boolean last() throws SQLException {
      this.checkClosed();
      if (this.rowData.size() == 0) {
         return false;
      } else {
         this.currentRow = this.rowData.size() - 1;
         return true;
      }
   }

   public void moveToCurrentRow() throws SQLException {
   }

   public void moveToInsertRow() throws SQLException {
      throw new NotImplementedException();
   }

   public boolean next() throws SQLException {
      this.checkClosed();
      ++this.currentRow;
      return this.currentRow < this.rowData.size();
   }

   public boolean previous() throws SQLException {
      --this.currentRow;
      return this.currentRow != -1;
   }

   public void refreshRow() throws SQLException {
   }

   public boolean relative(int rows) throws SQLException {
      this.checkClosed();
      if (this.rowData.size() == 0) {
         return false;
      } else {
         this.currentRow += rows;
         return !this.isAfterLast() && !this.isBeforeFirst();
      }
   }

   public boolean rowDeleted() throws SQLException {
      throw new NotImplementedException();
   }

   public boolean rowInserted() throws SQLException {
      throw new NotImplementedException();
   }

   public boolean rowUpdated() throws SQLException {
      throw new NotImplementedException();
   }

   public void setFetchDirection(int direction) throws SQLException {
      switch(direction) {
      case 1000:
      case 1001:
      case 1002:
         return;
      default:
         throw new SQLException("Illegal argument");
      }
   }

   public void setFetchSize(int size) throws SQLException {
   }

   public void updateArray(String columnName, Array array) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateArray(int columnIndex, Array array) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateAsciiStream(String columnName, InputStream inputStream, int length) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateAsciiStream(int columnIndex, InputStream inputStream, int length) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateBigDecimal(String columnName, BigDecimal bigDecimal) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateBigDecimal(int columnIndex, BigDecimal bigDecimal) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateBinaryStream(int columnIndex, InputStream inputStream, int length) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateBinaryStream(String columnName, InputStream inputStream, int length) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateBlob(int columnIndex, Blob blob) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateBlob(String columnName, Blob blob) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateBoolean(int columnIndex, boolean x) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateBoolean(String columnName, boolean x) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateByte(int columnIndex, byte x) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateByte(String columnName, byte x) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateBytes(int columnIndex, byte[] values) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateBytes(String columnName, byte[] values) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateCharacterStream(int columnIndex, Reader reader, int length) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateCharacterStream(String columnName, Reader reader, int length) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateClob(String columnName, Clob clob) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateClob(int columnIndex, Clob clob) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateDate(int columnIndex, Date date) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateDate(String columnName, Date date) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateDouble(int columnIndex, double x) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateDouble(String columnName, double x) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateFloat(String columnName, float x) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateFloat(int columnIndex, float x) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateInt(String columnName, int x) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateInt(int columnIndex, int x) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateLong(int columnIndex, long x) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateLong(String columnName, long x) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateNull(String columnName) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateNull(int columnIndex) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateObject(String columnName, Object obj) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateObject(int columnIndex, Object obj) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateObject(int columnIndex, Object obj, int scale) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateObject(String columnName, Object obj, int scale) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateRef(int columnIndex, Ref ref) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateRef(String columnName, Ref ref) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateRow() throws SQLException {
      throw new NotImplementedException();
   }

   public void updateShort(int columnIndex, short x) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateShort(String columnName, short x) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateString(int columnIndex, String str) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateString(String columnName, String str) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateTime(String columnName, Time time) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateTime(int columnIndex, Time time) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateTimestamp(String columnName, Timestamp timestamp) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateTimestamp(int columnIndex, Timestamp timestamp) throws SQLException {
      throw new NotImplementedException();
   }

   public boolean wasNull() throws SQLException {
      return this.isLastValueNull;
   }

   public RowId getRowId(int columnIndex) throws SQLException {
      throw new NotImplementedException();
   }

   public RowId getRowId(String columnLabel) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateRowId(int columnIndex, RowId x) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateRowId(String columnLabel, RowId x) throws SQLException {
      throw new NotImplementedException();
   }

   public int getHoldability() throws SQLException {
      throw new NotImplementedException();
   }

   public boolean isClosed() throws SQLException {
      throw new NotImplementedException();
   }

   public void updateNString(int columnIndex, String nString) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateNString(String columnLabel, String nString) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
      throw new NotImplementedException();
   }

   public NClob getNClob(int columnIndex) throws SQLException {
      throw new NotImplementedException();
   }

   public NClob getNClob(String columnLabel) throws SQLException {
      throw new NotImplementedException();
   }

   public SQLXML getSQLXML(int columnIndex) throws SQLException {
      throw new NotImplementedException();
   }

   public SQLXML getSQLXML(String columnLabel) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
      throw new NotImplementedException();
   }

   public String getNString(int columnIndex) throws SQLException {
      throw new NotImplementedException();
   }

   public String getNString(String columnLabel) throws SQLException {
      throw new NotImplementedException();
   }

   public Reader getNCharacterStream(int columnIndex) throws SQLException {
      throw new NotImplementedException();
   }

   public Reader getNCharacterStream(String columnLabel) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateCharacterStream(String columnLabel, Reader x, long length) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateClob(int columnIndex, Reader reader) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateClob(String columnLabel, Reader reader) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateNClob(int columnIndex, Reader reader) throws SQLException {
      throw new NotImplementedException();
   }

   public void updateNClob(String columnLabel, Reader reader) throws SQLException {
      throw new NotImplementedException();
   }

   private void checkClosed() throws SQLException {
      this.isLastValueNull = false;
      if (this.isClosed) {
         throw new SQLException("Operation not allowed after result set is closed");
      }
   }

   private void checkRowPosition() throws SQLException {
      if (this.rowData.size() == 0) {
         throw new SQLException("Illegal operation on empty result set");
      } else if (this.currentRow >= this.rowData.size()) {
         throw new SQLException("Cursor after end of result set");
      } else if (this.currentRow == -1) {
         throw new SQLException("Cursor before start of result set");
      }
   }

   private void checkColumnLimit(int columnIndex) throws SQLException {
      if (columnIndex < 1 || columnIndex > this.columnNameArr.length) {
         throw new SQLException("Column index out of range");
      }
   }

   private void checkClosePosLimit(int columnIndex) throws SQLException {
      this.checkClosed();
      this.checkRowPosition();
      this.checkColumnLimit(columnIndex);
   }

   private String getValue(int columnIndex) {
      String value = ((String[])((String[])this.rowData.get(this.currentRow)))[columnIndex - 1];
      if (value != null && !value.trim().equals("")) {
         return value;
      } else {
         this.isLastValueNull = true;
         return null;
      }
   }

   public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
      throw new NotImplementedException();
   }

   public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
      throw new NotImplementedException();
   }
}
