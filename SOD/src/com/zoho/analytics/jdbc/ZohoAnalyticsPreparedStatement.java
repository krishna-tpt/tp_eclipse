package com.zoho.analytics.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

class ZohoAnalyticsPreparedStatement extends ZohoAnalyticsStatement implements PreparedStatement {
   private String sql = null;

   ZohoAnalyticsPreparedStatement(ZohoAnalyticsConnection con, String sql) {
      super(con);
      this.sql = sql;
   }

   public void addBatch() throws SQLException {
      throw new NotImplementedException();
   }

   public void clearParameters() throws SQLException {
      throw new NotImplementedException();
   }

   public boolean execute() throws SQLException {
      ZohoAnalyticsStatement zrstmt = (ZohoAnalyticsStatement)this.con.createStatement();
      this.currResultSet = zrstmt.executeQuery(this.sql);
      boolean exists = this.currResultSet.next();
      this.currResultSet.beforeFirst();
      return exists;
   }

   public ResultSet executeQuery() throws SQLException {
      throw new NotImplementedException();
   }

   public int executeUpdate() throws SQLException {
      throw new NotImplementedException();
   }

   public ResultSetMetaData getMetaData() throws SQLException {
      throw new NotImplementedException();
   }

   public ParameterMetaData getParameterMetaData() throws SQLException {
      throw new NotImplementedException();
   }

   public void setArray(int param, Array array) throws SQLException {
      throw new NotImplementedException();
   }

   public void setAsciiStream(int param, InputStream inputStream, int param2) throws SQLException {
      throw new NotImplementedException();
   }

   public void setBigDecimal(int param, BigDecimal bigDecimal) throws SQLException {
      throw new NotImplementedException();
   }

   public void setBinaryStream(int param, InputStream inputStream, int param2) throws SQLException {
      throw new NotImplementedException();
   }

   public void setBlob(int param, Blob blob) throws SQLException {
      throw new NotImplementedException();
   }

   public void setBoolean(int param, boolean param1) throws SQLException {
      throw new NotImplementedException();
   }

   public void setByte(int param, byte param1) throws SQLException {
      throw new NotImplementedException();
   }

   public void setBytes(int param, byte[] values) throws SQLException {
      throw new NotImplementedException();
   }

   public void setCharacterStream(int param, Reader reader, int param2) throws SQLException {
      throw new NotImplementedException();
   }

   public void setClob(int param, Clob clob) throws SQLException {
      throw new NotImplementedException();
   }

   public void setDate(int param, Date date) throws SQLException {
      throw new NotImplementedException();
   }

   public void setDate(int param, Date date, Calendar calendar) throws SQLException {
      throw new NotImplementedException();
   }

   public void setDouble(int param, double param1) throws SQLException {
      throw new NotImplementedException();
   }

   public void setFloat(int param, float param1) throws SQLException {
      throw new NotImplementedException();
   }

   public void setInt(int param, int param1) throws SQLException {
      throw new NotImplementedException();
   }

   public void setLong(int param, long param1) throws SQLException {
      throw new NotImplementedException();
   }

   public void setNull(int param, int param1) throws SQLException {
      throw new NotImplementedException();
   }

   public void setNull(int param, int param1, String str) throws SQLException {
      throw new NotImplementedException();
   }

   public void setObject(int param, Object obj) throws SQLException {
      throw new NotImplementedException();
   }

   public void setObject(int param, Object obj, int param2) throws SQLException {
      throw new NotImplementedException();
   }

   public void setObject(int param, Object obj, int param2, int param3) throws SQLException {
      throw new NotImplementedException();
   }

   public void setRef(int param, Ref ref) throws SQLException {
      throw new NotImplementedException();
   }

   public void setShort(int param, short param1) throws SQLException {
      throw new NotImplementedException();
   }

   public void setString(int param, String str) throws SQLException {
      throw new NotImplementedException();
   }

   public void setTime(int param, Time time) throws SQLException {
      throw new NotImplementedException();
   }

   public void setTime(int param, Time time, Calendar calendar) throws SQLException {
      throw new NotImplementedException();
   }

   public void setTimestamp(int param, Timestamp timestamp) throws SQLException {
      throw new NotImplementedException();
   }

   public void setTimestamp(int param, Timestamp timestamp, Calendar calendar) throws SQLException {
      throw new NotImplementedException();
   }

   public void setURL(int param, URL uRL) throws SQLException {
      throw new NotImplementedException();
   }

   /** @deprecated */
   @Deprecated
   public void setUnicodeStream(int param, InputStream inputStream, int param2) throws SQLException {
      throw new NotImplementedException();
   }

   public void setRowId(int parameterIndex, RowId x) throws SQLException {
      throw new NotImplementedException();
   }

   public void setNString(int parameterIndex, String value) throws SQLException {
      throw new NotImplementedException();
   }

   public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
      throw new NotImplementedException();
   }

   public void setNClob(int parameterIndex, NClob value) throws SQLException {
      throw new NotImplementedException();
   }

   public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
      throw new NotImplementedException();
   }

   public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
      throw new NotImplementedException();
   }

   public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
      throw new NotImplementedException();
   }

   public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
      throw new NotImplementedException();
   }

   public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
      throw new NotImplementedException();
   }

   public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
      throw new NotImplementedException();
   }

   public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
      throw new NotImplementedException();
   }

   public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
      throw new NotImplementedException();
   }

   public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
      throw new NotImplementedException();
   }

   public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
      throw new NotImplementedException();
   }

   public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
      throw new NotImplementedException();
   }

   public void setClob(int parameterIndex, Reader reader) throws SQLException {
      throw new NotImplementedException();
   }

   public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
      throw new NotImplementedException();
   }

   public void setNClob(int parameterIndex, Reader reader) throws SQLException {
      throw new NotImplementedException();
   }

   public void closeOnCompletion() throws SQLException {
      throw new NotImplementedException();
   }

   public boolean isCloseOnCompletion() throws SQLException {
      throw new NotImplementedException();
   }
}
