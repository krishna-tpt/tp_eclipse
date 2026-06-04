package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.sql.exception.ConvertException;

public class WindowingClause {
   String between;
   String and;
   String unbounded;
   String preceding;
   String following;
   String current;
   String row;
   SelectColumn windowExpr;
   WindowingClause firstWindow;
   WindowingClause secondWindow;
   String rowsOrRange;

   public void setUnbounded(String unbounded) {
      this.unbounded = unbounded;
   }

   public void setPreceding(String preceding) {
      this.preceding = preceding;
   }

   public void setFollowing(String following) {
      this.following = following;
   }

   public void setBetween(String between) {
      this.between = between;
   }

   public void setAnd(String and) {
      this.and = and;
   }

   public void setCurrent(String current) {
      this.current = current;
   }

   public void setRow(String row) {
      this.row = row;
   }

   public void setRowsOrRange(String rowsOrRange) {
      this.rowsOrRange = rowsOrRange;
   }

   public void setFirstWindow(WindowingClause firstWindow) {
      this.firstWindow = firstWindow;
   }

   public void setSecondWindow(WindowingClause secondWindow) {
      this.secondWindow = secondWindow;
   }

   public void setWindowExpr(SelectColumn windowExpr) {
      this.windowExpr = windowExpr;
   }

   public String getUnbounded() {
      return this.unbounded;
   }

   public String getPreceding() {
      return this.preceding;
   }

   public String getFollowing() {
      return this.following;
   }

   public String getBetween() {
      return this.between;
   }

   public String getAnd() {
      return this.and;
   }

   public String getCurrent() {
      return this.current;
   }

   public String getRow() {
      return this.row;
   }

   public String getRowsOrRange() {
      return this.rowsOrRange;
   }

   public WindowingClause getFirstWindow() {
      return this.firstWindow;
   }

   public WindowingClause getSecondWindow() {
      return this.secondWindow;
   }

   public SelectColumn getWindowExpr() {
      return this.windowExpr;
   }

   public WindowingClause toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WindowingClause windowingClause = new WindowingClause();
      if (this.getRowsOrRange() != null) {
         windowingClause.setRowsOrRange(this.getRowsOrRange().toUpperCase());
      }

      if (this.getBetween() != null) {
         windowingClause.setBetween("BETWEEN");
      }

      if (this.getFirstWindow() != null) {
         windowingClause.setFirstWindow(this.getFirstWindow().toNetezza(to_sqs, from_sqs));
      }

      if (this.getAnd() != null) {
         windowingClause.setAnd("AND");
      }

      if (this.getSecondWindow() != null) {
         windowingClause.setSecondWindow(this.getSecondWindow().toNetezza(to_sqs, from_sqs));
      }

      if (this.getUnbounded() != null) {
         windowingClause.setUnbounded("UNBOUNDED");
      }

      if (this.getWindowExpr() != null) {
         windowingClause.setWindowExpr(this.getWindowExpr().toNetezzaSelect(to_sqs, from_sqs));
      }

      if (this.getCurrent() != null) {
         windowingClause.setCurrent("CURRENT");
      }

      if (this.getRow() != null) {
         windowingClause.setRow("ROW");
      }

      if (this.getPreceding() != null) {
         windowingClause.setPreceding("PRECEDING");
      }

      if (this.getFollowing() != null) {
         windowingClause.setFollowing("FOLLOWING");
      }

      return windowingClause;
   }

   public WindowingClause toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WindowingClause windowingClause = new WindowingClause();
      if (this.getRowsOrRange() != null) {
         windowingClause.setRowsOrRange(this.getRowsOrRange().toUpperCase());
      }

      if (this.getBetween() != null) {
         windowingClause.setBetween("BETWEEN");
      }

      if (this.getFirstWindow() != null) {
         windowingClause.setFirstWindow(this.getFirstWindow().toTeradata(to_sqs, from_sqs));
      }

      if (this.getAnd() != null) {
         windowingClause.setAnd("AND");
      }

      if (this.getSecondWindow() != null) {
         windowingClause.setSecondWindow(this.getSecondWindow().toTeradata(to_sqs, from_sqs));
      }

      if (this.getUnbounded() != null) {
         windowingClause.setUnbounded("UNBOUNDED");
      }

      if (this.getWindowExpr() != null) {
         windowingClause.setWindowExpr(this.getWindowExpr().toTeradataSelect(to_sqs, from_sqs));
      }

      if (this.getCurrent() != null) {
         windowingClause.setCurrent("CURRENT");
      }

      if (this.getRow() != null) {
         windowingClause.setRow("ROW");
      }

      if (this.getPreceding() != null) {
         windowingClause.setPreceding("PRECEDING");
      }

      if (this.getFollowing() != null) {
         windowingClause.setFollowing("FOLLOWING");
      }

      return windowingClause;
   }

   public WindowingClause toSnowflake(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WindowingClause windowingClause = new WindowingClause();
      if (this.getRowsOrRange() != null) {
         windowingClause.setRowsOrRange(this.getRowsOrRange().toUpperCase());
      }

      if (this.getBetween() != null) {
         windowingClause.setBetween("BETWEEN");
      }

      if (this.getFirstWindow() != null) {
         windowingClause.setFirstWindow(this.getFirstWindow().toSnowflake(to_sqs, from_sqs));
      }

      if (this.getAnd() != null) {
         windowingClause.setAnd("AND");
      }

      if (this.getSecondWindow() != null) {
         windowingClause.setSecondWindow(this.getSecondWindow().toSnowflake(to_sqs, from_sqs));
      }

      if (this.getUnbounded() != null) {
         windowingClause.setUnbounded("UNBOUNDED");
      }

      if (this.getWindowExpr() != null) {
         windowingClause.setWindowExpr(this.getWindowExpr().toSnowflakeSelect(to_sqs, from_sqs));
      }

      if (this.getCurrent() != null) {
         windowingClause.setCurrent("CURRENT");
      }

      if (this.getRow() != null) {
         windowingClause.setRow("ROW");
      }

      if (this.getPreceding() != null) {
         windowingClause.setPreceding("PRECEDING");
      }

      if (this.getFollowing() != null) {
         windowingClause.setFollowing("FOLLOWING");
      }

      return windowingClause;
   }

   public WindowingClause toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WindowingClause windowingClause = new WindowingClause();
      if (this.getRowsOrRange() != null) {
         windowingClause.setRowsOrRange(this.getRowsOrRange().toUpperCase());
      }

      if (this.getBetween() != null) {
         windowingClause.setBetween("BETWEEN");
      }

      if (this.getFirstWindow() != null) {
         windowingClause.setFirstWindow(this.getFirstWindow().toMySQL(to_sqs, from_sqs));
      }

      if (this.getAnd() != null) {
         windowingClause.setAnd("AND");
      }

      if (this.getSecondWindow() != null) {
         windowingClause.setSecondWindow(this.getSecondWindow().toMySQL(to_sqs, from_sqs));
      }

      if (this.getUnbounded() != null) {
         windowingClause.setUnbounded("UNBOUNDED");
      }

      if (this.getWindowExpr() != null) {
         windowingClause.setWindowExpr(this.getWindowExpr().toMySQLSelect(to_sqs, from_sqs));
      }

      if (this.getCurrent() != null) {
         windowingClause.setCurrent("CURRENT");
      }

      if (this.getRow() != null) {
         windowingClause.setRow("ROW");
      }

      if (this.getPreceding() != null) {
         windowingClause.setPreceding("PRECEDING");
      }

      if (this.getFollowing() != null) {
         windowingClause.setFollowing("FOLLOWING");
      }

      return windowingClause;
   }

   public WindowingClause toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WindowingClause windowingClause = new WindowingClause();
      if (this.getRowsOrRange() != null) {
         windowingClause.setRowsOrRange(this.getRowsOrRange().toUpperCase());
      }

      if (this.getBetween() != null) {
         windowingClause.setBetween("BETWEEN");
      }

      if (this.getFirstWindow() != null) {
         windowingClause.setFirstWindow(this.getFirstWindow().toVectorWise(to_sqs, from_sqs));
      }

      if (this.getAnd() != null) {
         windowingClause.setAnd("AND");
      }

      if (this.getSecondWindow() != null) {
         windowingClause.setSecondWindow(this.getSecondWindow().toVectorWise(to_sqs, from_sqs));
      }

      if (this.getUnbounded() != null) {
         windowingClause.setUnbounded("UNBOUNDED");
      }

      if (this.getWindowExpr() != null) {
         windowingClause.setWindowExpr(this.getWindowExpr().toVectorWiseSelect(to_sqs, from_sqs));
      }

      if (this.getCurrent() != null) {
         windowingClause.setCurrent("CURRENT");
      }

      if (this.getRow() != null) {
         windowingClause.setRow("ROW");
      }

      if (this.getPreceding() != null) {
         windowingClause.setPreceding("PRECEDING");
      }

      if (this.getFollowing() != null) {
         windowingClause.setFollowing("FOLLOWING");
      }

      return windowingClause;
   }

   public WindowingClause toReplaceTblCol(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WindowingClause windowClConv = new WindowingClause();
      if (this.between != null) {
         windowClConv.setBetween(this.between);
      }

      if (this.and != null) {
         windowClConv.setAnd(this.and);
      }

      if (this.unbounded != null) {
         windowClConv.setUnbounded(this.unbounded);
      }

      if (this.preceding != null) {
         windowClConv.setPreceding(this.preceding);
      }

      if (this.following != null) {
         windowClConv.setFollowing(this.following);
      }

      if (this.current != null) {
         windowClConv.setCurrent(this.current);
      }

      if (this.row != null) {
         windowClConv.setRow(this.row);
      }

      if (this.windowExpr != null) {
         windowClConv.setWindowExpr(this.windowExpr.toReplaceTblCol(to_sqs, from_sqs));
      }

      if (this.firstWindow != null) {
         windowClConv.setFirstWindow(this.firstWindow.toReplaceTblCol(to_sqs, from_sqs));
      }

      if (this.secondWindow != null) {
         windowClConv.setSecondWindow(this.secondWindow.toReplaceTblCol(to_sqs, from_sqs));
      }

      if (this.rowsOrRange != null) {
         windowClConv.setRowsOrRange(this.rowsOrRange);
      }

      return windowClConv;
   }

   public WindowingClause toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WindowingClause windowingClause = new WindowingClause();
      if (this.getRowsOrRange() != null) {
         windowingClause.setRowsOrRange(this.getRowsOrRange().toUpperCase());
      }

      if (this.getBetween() != null) {
         windowingClause.setBetween("BETWEEN");
      }

      if (this.getFirstWindow() != null) {
         windowingClause.setFirstWindow(this.getFirstWindow().toBigQuery(to_sqs, from_sqs));
      }

      if (this.getAnd() != null) {
         windowingClause.setAnd("AND");
      }

      if (this.getSecondWindow() != null) {
         windowingClause.setSecondWindow(this.getSecondWindow().toBigQuery(to_sqs, from_sqs));
      }

      if (this.getUnbounded() != null) {
         windowingClause.setUnbounded("UNBOUNDED");
      }

      if (this.getWindowExpr() != null) {
         windowingClause.setWindowExpr(this.getWindowExpr().toBigQuerySelect(to_sqs, from_sqs));
      }

      if (this.getCurrent() != null) {
         windowingClause.setCurrent("CURRENT");
      }

      if (this.getRow() != null) {
         windowingClause.setRow("ROW");
      }

      if (this.getPreceding() != null) {
         windowingClause.setPreceding("PRECEDING");
      }

      if (this.getFollowing() != null) {
         windowingClause.setFollowing("FOLLOWING");
      }

      return windowingClause;
   }

   public WindowingClause toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WindowingClause windowingClause = new WindowingClause();
      if (this.getRowsOrRange() != null) {
         windowingClause.setRowsOrRange(this.getRowsOrRange().toUpperCase());
      }

      if (this.getBetween() != null) {
         windowingClause.setBetween("BETWEEN");
      }

      if (this.getFirstWindow() != null) {
         windowingClause.setFirstWindow(this.getFirstWindow().toPostgreSQL(to_sqs, from_sqs));
      }

      if (this.getAnd() != null) {
         windowingClause.setAnd("AND");
      }

      if (this.getSecondWindow() != null) {
         windowingClause.setSecondWindow(this.getSecondWindow().toPostgreSQL(to_sqs, from_sqs));
      }

      if (this.getUnbounded() != null) {
         windowingClause.setUnbounded("UNBOUNDED");
      }

      if (this.getWindowExpr() != null) {
         windowingClause.setWindowExpr(this.getWindowExpr().toPostgreSQLSelect(to_sqs, from_sqs));
      }

      if (this.getCurrent() != null) {
         windowingClause.setCurrent("CURRENT");
      }

      if (this.getRow() != null) {
         windowingClause.setRow("ROW");
      }

      if (this.getPreceding() != null) {
         windowingClause.setPreceding("PRECEDING");
      }

      if (this.getFollowing() != null) {
         windowingClause.setFollowing("FOLLOWING");
      }

      return windowingClause;
   }

   public WindowingClause toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WindowingClause windowingClause = new WindowingClause();
      if (this.getRowsOrRange() != null) {
         windowingClause.setRowsOrRange(this.getRowsOrRange().toUpperCase());
      }

      if (this.getBetween() != null) {
         windowingClause.setBetween("BETWEEN");
      }

      if (this.getFirstWindow() != null) {
         windowingClause.setFirstWindow(this.getFirstWindow().toOracle(to_sqs, from_sqs));
      }

      if (this.getAnd() != null) {
         windowingClause.setAnd("AND");
      }

      if (this.getSecondWindow() != null) {
         windowingClause.setSecondWindow(this.getSecondWindow().toOracle(to_sqs, from_sqs));
      }

      if (this.getUnbounded() != null) {
         windowingClause.setUnbounded("UNBOUNDED");
      }

      if (this.getWindowExpr() != null) {
         windowingClause.setWindowExpr(this.getWindowExpr().toOracleSelect(to_sqs, from_sqs));
      }

      if (this.getCurrent() != null) {
         windowingClause.setCurrent("CURRENT");
      }

      if (this.getRow() != null) {
         windowingClause.setRow("ROW");
      }

      if (this.getPreceding() != null) {
         windowingClause.setPreceding("PRECEDING");
      }

      if (this.getFollowing() != null) {
         windowingClause.setFollowing("FOLLOWING");
      }

      return windowingClause;
   }

   public WindowingClause toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WindowingClause windowingClause = new WindowingClause();
      if (this.getRowsOrRange() != null) {
         windowingClause.setRowsOrRange(this.getRowsOrRange().toUpperCase());
      }

      if (this.getBetween() != null) {
         windowingClause.setBetween("BETWEEN");
      }

      if (this.getFirstWindow() != null) {
         windowingClause.setFirstWindow(this.getFirstWindow().toMSSQLServer(to_sqs, from_sqs));
      }

      if (this.getAnd() != null) {
         windowingClause.setAnd("AND");
      }

      if (this.getSecondWindow() != null) {
         windowingClause.setSecondWindow(this.getSecondWindow().toMSSQLServer(to_sqs, from_sqs));
      }

      if (this.getUnbounded() != null) {
         windowingClause.setUnbounded("UNBOUNDED");
      }

      if (this.getWindowExpr() != null) {
         windowingClause.setWindowExpr(this.getWindowExpr().toMSSQLServerSelect(to_sqs, from_sqs));
      }

      if (this.getCurrent() != null) {
         windowingClause.setCurrent("CURRENT");
      }

      if (this.getRow() != null) {
         windowingClause.setRow("ROW");
      }

      if (this.getPreceding() != null) {
         windowingClause.setPreceding("PRECEDING");
      }

      if (this.getFollowing() != null) {
         windowingClause.setFollowing("FOLLOWING");
      }

      return windowingClause;
   }

   public WindowingClause toAthena(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WindowingClause windowingClause = new WindowingClause();
      if (this.getRowsOrRange() != null) {
         windowingClause.setRowsOrRange(this.getRowsOrRange().toUpperCase());
      }

      if (this.getBetween() != null) {
         windowingClause.setBetween("BETWEEN");
      }

      if (this.getFirstWindow() != null) {
         windowingClause.setFirstWindow(this.getFirstWindow().toAthena(to_sqs, from_sqs));
      }

      if (this.getAnd() != null) {
         windowingClause.setAnd("AND");
      }

      if (this.getSecondWindow() != null) {
         windowingClause.setSecondWindow(this.getSecondWindow().toAthena(to_sqs, from_sqs));
      }

      if (this.getUnbounded() != null) {
         windowingClause.setUnbounded("UNBOUNDED");
      }

      if (this.getWindowExpr() != null) {
         windowingClause.setWindowExpr(this.getWindowExpr().toAthenaSelect(to_sqs, from_sqs));
      }

      if (this.getCurrent() != null) {
         windowingClause.setCurrent("CURRENT");
      }

      if (this.getRow() != null) {
         windowingClause.setRow("ROW");
      }

      if (this.getPreceding() != null) {
         windowingClause.setPreceding("PRECEDING");
      }

      if (this.getFollowing() != null) {
         windowingClause.setFollowing("FOLLOWING");
      }

      return windowingClause;
   }

   public WindowingClause toSapHana(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WindowingClause windowingClause = new WindowingClause();
      if (this.getRowsOrRange() != null) {
         windowingClause.setRowsOrRange(this.getRowsOrRange().toUpperCase());
      }

      if (this.getBetween() != null) {
         windowingClause.setBetween("BETWEEN");
      }

      if (this.getFirstWindow() != null) {
         windowingClause.setFirstWindow(this.getFirstWindow().toSapHana(to_sqs, from_sqs));
      }

      if (this.getAnd() != null) {
         windowingClause.setAnd("AND");
      }

      if (this.getSecondWindow() != null) {
         windowingClause.setSecondWindow(this.getSecondWindow().toSapHana(to_sqs, from_sqs));
      }

      if (this.getUnbounded() != null) {
         windowingClause.setUnbounded("UNBOUNDED");
      }

      if (this.getWindowExpr() != null) {
         windowingClause.setWindowExpr(this.getWindowExpr().toSapHanaSelect(to_sqs, from_sqs));
      }

      if (this.getCurrent() != null) {
         windowingClause.setCurrent("CURRENT");
      }

      if (this.getRow() != null) {
         windowingClause.setRow("ROW");
      }

      if (this.getPreceding() != null) {
         windowingClause.setPreceding("PRECEDING");
      }

      if (this.getFollowing() != null) {
         windowingClause.setFollowing("FOLLOWING");
      }

      return windowingClause;
   }

   public WindowingClause toSqlite(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WindowingClause windowingClause = new WindowingClause();
      if (this.getRowsOrRange() != null) {
         windowingClause.setRowsOrRange(this.getRowsOrRange().toUpperCase());
      }

      if (this.getBetween() != null) {
         windowingClause.setBetween("BETWEEN");
      }

      if (this.getFirstWindow() != null) {
         windowingClause.setFirstWindow(this.getFirstWindow().toSqlite(to_sqs, from_sqs));
      }

      if (this.getAnd() != null) {
         windowingClause.setAnd("AND");
      }

      if (this.getSecondWindow() != null) {
         windowingClause.setSecondWindow(this.getSecondWindow().toSqlite(to_sqs, from_sqs));
      }

      if (this.getUnbounded() != null) {
         windowingClause.setUnbounded("UNBOUNDED");
      }

      if (this.getWindowExpr() != null) {
         windowingClause.setWindowExpr(this.getWindowExpr().toSqliteSelect(to_sqs, from_sqs));
      }

      if (this.getCurrent() != null) {
         windowingClause.setCurrent("CURRENT");
      }

      if (this.getRow() != null) {
         windowingClause.setRow("ROW");
      }

      if (this.getPreceding() != null) {
         windowingClause.setPreceding("PRECEDING");
      }

      if (this.getFollowing() != null) {
         windowingClause.setFollowing("FOLLOWING");
      }

      return windowingClause;
   }

   public WindowingClause toExcel(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WindowingClause windowingClause = new WindowingClause();
      if (this.getRowsOrRange() != null) {
         windowingClause.setRowsOrRange(this.getRowsOrRange().toUpperCase());
      }

      if (this.getBetween() != null) {
         windowingClause.setBetween("BETWEEN");
      }

      if (this.getFirstWindow() != null) {
         windowingClause.setFirstWindow(this.getFirstWindow().toExcel(to_sqs, from_sqs));
      }

      if (this.getAnd() != null) {
         windowingClause.setAnd("AND");
      }

      if (this.getSecondWindow() != null) {
         windowingClause.setSecondWindow(this.getSecondWindow().toExcel(to_sqs, from_sqs));
      }

      if (this.getUnbounded() != null) {
         windowingClause.setUnbounded("UNBOUNDED");
      }

      if (this.getWindowExpr() != null) {
         windowingClause.setWindowExpr(this.getWindowExpr().toExcelSelect(to_sqs, from_sqs));
      }

      if (this.getCurrent() != null) {
         windowingClause.setCurrent("CURRENT");
      }

      if (this.getRow() != null) {
         windowingClause.setRow("ROW");
      }

      if (this.getPreceding() != null) {
         windowingClause.setPreceding("PRECEDING");
      }

      if (this.getFollowing() != null) {
         windowingClause.setFollowing("FOLLOWING");
      }

      return windowingClause;
   }

   public WindowingClause toMsAccessJdbc(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WindowingClause windowingClause = new WindowingClause();
      if (this.getRowsOrRange() != null) {
         windowingClause.setRowsOrRange(this.getRowsOrRange().toUpperCase());
      }

      if (this.getBetween() != null) {
         windowingClause.setBetween("BETWEEN");
      }

      if (this.getFirstWindow() != null) {
         windowingClause.setFirstWindow(this.getFirstWindow().toMsAccessJdbc(to_sqs, from_sqs));
      }

      if (this.getAnd() != null) {
         windowingClause.setAnd("AND");
      }

      if (this.getSecondWindow() != null) {
         windowingClause.setSecondWindow(this.getSecondWindow().toMsAccessJdbc(to_sqs, from_sqs));
      }

      if (this.getUnbounded() != null) {
         windowingClause.setUnbounded("UNBOUNDED");
      }

      if (this.getWindowExpr() != null) {
         windowingClause.setWindowExpr(this.getWindowExpr().toMsAccessJdbcSelect(to_sqs, from_sqs));
      }

      if (this.getCurrent() != null) {
         windowingClause.setCurrent("CURRENT");
      }

      if (this.getRow() != null) {
         windowingClause.setRow("ROW");
      }

      if (this.getPreceding() != null) {
         windowingClause.setPreceding("PRECEDING");
      }

      if (this.getFollowing() != null) {
         windowingClause.setFollowing("FOLLOWING");
      }

      return windowingClause;
   }

   public WindowingClause toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WindowingClause windowingClause = new WindowingClause();
      if (this.getRowsOrRange() != null) {
         windowingClause.setRowsOrRange(this.getRowsOrRange().toUpperCase());
      }

      if (this.getBetween() != null) {
         windowingClause.setBetween("BETWEEN");
      }

      if (this.getFirstWindow() != null) {
         windowingClause.setFirstWindow(this.getFirstWindow().toDB2(to_sqs, from_sqs));
      }

      if (this.getAnd() != null) {
         windowingClause.setAnd("AND");
      }

      if (this.getSecondWindow() != null) {
         windowingClause.setSecondWindow(this.getSecondWindow().toDB2(to_sqs, from_sqs));
      }

      if (this.getUnbounded() != null) {
         windowingClause.setUnbounded("UNBOUNDED");
      }

      if (this.getWindowExpr() != null) {
         windowingClause.setWindowExpr(this.getWindowExpr().toDB2Select(to_sqs, from_sqs));
      }

      if (this.getCurrent() != null) {
         windowingClause.setCurrent("CURRENT");
      }

      if (this.getRow() != null) {
         windowingClause.setRow("ROW");
      }

      if (this.getPreceding() != null) {
         windowingClause.setPreceding("PRECEDING");
      }

      if (this.getFollowing() != null) {
         windowingClause.setFollowing("FOLLOWING");
      }

      return windowingClause;
   }

   public WindowingClause toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WindowingClause windowingClause = new WindowingClause();
      if (this.getRowsOrRange() != null) {
         windowingClause.setRowsOrRange(this.getRowsOrRange().toUpperCase());
      }

      if (this.getBetween() != null) {
         windowingClause.setBetween("BETWEEN");
      }

      if (this.getFirstWindow() != null) {
         windowingClause.setFirstWindow(this.getFirstWindow().toInformix(to_sqs, from_sqs));
      }

      if (this.getAnd() != null) {
         windowingClause.setAnd("AND");
      }

      if (this.getSecondWindow() != null) {
         windowingClause.setSecondWindow(this.getSecondWindow().toInformix(to_sqs, from_sqs));
      }

      if (this.getUnbounded() != null) {
         windowingClause.setUnbounded("UNBOUNDED");
      }

      if (this.getWindowExpr() != null) {
         windowingClause.setWindowExpr(this.getWindowExpr().toInformixSelect(to_sqs, from_sqs));
      }

      if (this.getCurrent() != null) {
         windowingClause.setCurrent("CURRENT");
      }

      if (this.getRow() != null) {
         windowingClause.setRow("ROW");
      }

      if (this.getPreceding() != null) {
         windowingClause.setPreceding("PRECEDING");
      }

      if (this.getFollowing() != null) {
         windowingClause.setFollowing("FOLLOWING");
      }

      return windowingClause;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.getRowsOrRange() != null) {
         sb.append(" " + this.getRowsOrRange().toUpperCase() + " ");
      }

      if (this.getBetween() != null) {
         sb.append(" BETWEEN ");
      }

      if (this.getFirstWindow() != null) {
         sb.append(this.getFirstWindow().toString());
      }

      if (this.getAnd() != null) {
         sb.append(" AND ");
      }

      if (this.getSecondWindow() != null) {
         sb.append(this.getSecondWindow().toString());
      }

      if (this.getUnbounded() != null) {
         sb.append(" UNBOUNDED ");
      }

      if (this.getWindowExpr() != null) {
         sb.append(this.getWindowExpr().toString());
      }

      if (this.getCurrent() != null) {
         sb.append(" CURRENT ");
      }

      if (this.getRow() != null) {
         sb.append(" ROW ");
      }

      if (this.getPreceding() != null) {
         sb.append(" PRECEDING ");
      }

      if (this.getFollowing() != null) {
         sb.append(" FOLLOWING ");
      }

      return sb.toString();
   }
}
