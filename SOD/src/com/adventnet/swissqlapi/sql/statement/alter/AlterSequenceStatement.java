package com.adventnet.swissqlapi.sql.statement.alter;

import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;
import com.adventnet.swissqlapi.sql.statement.create.Datatype;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;

public class AlterSequenceStatement implements SwisSQLStatement {
   private String alter;
   private String sequence;
   private TableObject createTableObject;
   private String as;
   private Datatype datatype;
   private String start;
   private String with;
   private String startValue;
   private String incrementString;
   private String incrementValue;
   private String maxValueOrNoMaxValue;
   private String minValueOrNoMinValue;
   private String cycleOrNoCycle;
   private String cacheOrNoCache;
   private String orderOrNoOrder;
   private UserObjectContext context = null;

   public void setObjectContext(UserObjectContext context) {
      this.context = context;
   }

   public void setAlter(String alter) {
      this.alter = alter;
   }

   public void setSequence(String sequence) {
      this.sequence = sequence;
   }

   public void setSchemaName(TableObject createTableObject) {
      this.createTableObject = createTableObject;
   }

   public void setAs(String as) {
      this.as = as;
   }

   public void setDataType(Datatype datatype) {
      this.datatype = datatype;
   }

   public void setStart(String start) {
      this.start = start;
   }

   public void setWith(String with) {
      this.with = with;
   }

   public void setStartValue(String startValue) {
      this.startValue = startValue;
   }

   public void setIncrementString(String incrementString) {
      this.incrementString = incrementString;
   }

   public void setIncrementValue(String incrementValue) {
      this.incrementValue = incrementValue;
   }

   public void setMaxValueOrNoMaxValue(String maxValueOrNoMaxValue) {
      this.maxValueOrNoMaxValue = maxValueOrNoMaxValue;
   }

   public void setMinValueOrNoMinValue(String minValueOrNoMinValue) {
      this.minValueOrNoMinValue = minValueOrNoMinValue;
   }

   public void setCycleOrNoCycle(String cycleOrNoCycle) {
      this.cycleOrNoCycle = cycleOrNoCycle;
   }

   public void setCacheOrNoCache(String cacheOrNoCache) {
      this.cacheOrNoCache = cacheOrNoCache;
   }

   public void setOrderOrNoOrder(String orderOrNoOrder) {
      this.orderOrNoOrder = orderOrNoOrder;
   }

   public String getStart() {
      return this.start;
   }

   public TableObject getSchemaName() {
      return this.createTableObject;
   }

   public Datatype getDatatype() {
      return this.datatype;
   }

   public String getMaxValueOrNoMaxValue() {
      return this.maxValueOrNoMaxValue;
   }

   public String getMinValueOrNoMinValue() {
      return this.minValueOrNoMinValue;
   }

   public String getCycleOrNoCycle() {
      return this.cycleOrNoCycle;
   }

   public String getCacheOrNoCache() {
      return this.cacheOrNoCache;
   }

   public String getOrderOrNoOrder() {
      return this.orderOrNoOrder;
   }

   public String getSequence() {
      return this.sequence;
   }

   public String getStartValue() {
      return this.startValue;
   }

   public String getIncrementValue() {
      return this.incrementValue;
   }

   public String getAlter() {
      return this.alter;
   }

   public AlterSequenceStatement copyObjectValues() {
      AlterSequenceStatement dupAlterSequenceStatement = new AlterSequenceStatement();
      dupAlterSequenceStatement.setAlter(this.alter);
      dupAlterSequenceStatement.setAs(this.as);
      dupAlterSequenceStatement.setCacheOrNoCache(this.cacheOrNoCache);
      dupAlterSequenceStatement.setCycleOrNoCycle(this.cycleOrNoCycle);
      dupAlterSequenceStatement.setDataType(this.datatype);
      dupAlterSequenceStatement.setIncrementString(this.incrementString);
      dupAlterSequenceStatement.setIncrementValue(this.incrementValue);
      dupAlterSequenceStatement.setMaxValueOrNoMaxValue(this.maxValueOrNoMaxValue);
      dupAlterSequenceStatement.setMinValueOrNoMinValue(this.minValueOrNoMinValue);
      dupAlterSequenceStatement.setOrderOrNoOrder(this.orderOrNoOrder);
      dupAlterSequenceStatement.setSequence(this.sequence);
      dupAlterSequenceStatement.setSchemaName(this.createTableObject);
      dupAlterSequenceStatement.setStart(this.start);
      dupAlterSequenceStatement.setStartValue(this.startValue);
      dupAlterSequenceStatement.setWith(this.with);
      dupAlterSequenceStatement.setObjectContext(this.context);
      return dupAlterSequenceStatement;
   }

   public String toOracleString() throws ConvertException {
      return this.toOracle().toString();
   }

   public String toMSSQLServerString() throws ConvertException {
      return this.toMSSQLServer().toString();
   }

   public String toSybaseString() throws ConvertException {
      return this.toSybase().toString();
   }

   public String toDB2String() throws ConvertException {
      return this.toDB2().toString();
   }

   public String toPostgreSQLString() throws ConvertException {
      return this.toPostgreSQL().toString();
   }

   public String toBigQueryString() throws ConvertException {
      return this.toBigQuery().toString();
   }

   public String toMySQLString() throws ConvertException {
      return this.toMySQL().toString();
   }

   public String toANSIString() throws ConvertException {
      return this.toANSI().toString();
   }

   public String toTeradataString() throws ConvertException {
      return this.toTeradata().toString();
   }

   public String toInformixString() throws ConvertException {
      return this.toInformix().toString();
   }

   public String toTimesTenString() throws ConvertException {
      return this.toTimesTen().toString();
   }

   public String toNetezzaString() throws ConvertException {
      return this.toNetezza().toString();
   }

   public String toSnowflakeString() throws ConvertException {
      return this.toSnowflake().toString();
   }

   public String toAthenaString() throws ConvertException {
      return null;
   }

   public String toSapHanaString() throws ConvertException {
      return null;
   }

   public String toSqliteString() throws ConvertException {
      return null;
   }

   public String toExcelString() throws ConvertException {
      return null;
   }

   public String toMsAccessJdbcString() throws ConvertException {
      return null;
   }

   public AlterSequenceStatement toANSI() throws ConvertException {
      AlterSequenceStatement alterSequence = this.copyObjectValues();
      alterSequence.setAs((String)null);
      alterSequence.setDataType((Datatype)null);
      if (alterSequence.getSchemaName() != null) {
         TableObject tempSchemaName = alterSequence.getSchemaName();
         tempSchemaName.toANSISQL();
      }

      return alterSequence;
   }

   public AlterSequenceStatement toDB2() throws ConvertException {
      AlterSequenceStatement alterSequence = this.copyObjectValues();
      if (alterSequence.getSchemaName() != null) {
         TableObject tempSchemaName = alterSequence.getSchemaName();
         tempSchemaName.toDB2();
      }

      if (alterSequence.getDatatype() != null) {
         Datatype db2Datatype = alterSequence.getDatatype();
         db2Datatype.toDB2String();
      }

      if (alterSequence.getMaxValueOrNoMaxValue() != null && alterSequence.getMaxValueOrNoMaxValue().toUpperCase().equalsIgnoreCase("NOMAXVALUE")) {
         alterSequence.setMaxValueOrNoMaxValue("NO MAXVALUE");
      }

      if (alterSequence.getMinValueOrNoMinValue() != null && alterSequence.getMinValueOrNoMinValue().toUpperCase().equalsIgnoreCase("NOMINVALUE")) {
         alterSequence.setMinValueOrNoMinValue("NO MINVALUE");
      }

      if (alterSequence.getCycleOrNoCycle() != null && alterSequence.getCycleOrNoCycle().toUpperCase().equalsIgnoreCase("NOCYCLE")) {
         alterSequence.setCycleOrNoCycle("NO CYCLE");
      }

      if (alterSequence.getCacheOrNoCache() != null && alterSequence.getCacheOrNoCache().toUpperCase().equalsIgnoreCase("NOCACHE")) {
         alterSequence.setCacheOrNoCache("NO CACHE");
      }

      if (alterSequence.getOrderOrNoOrder() != null && alterSequence.getOrderOrNoOrder().toUpperCase().equalsIgnoreCase("NOORDER")) {
         alterSequence.setOrderOrNoOrder("NO ORDER");
      }

      return alterSequence;
   }

   public AlterSequenceStatement toInformix() throws ConvertException {
      AlterSequenceStatement alterSequence = this.copyObjectValues();
      alterSequence.setAs((String)null);
      alterSequence.setDataType((Datatype)null);
      if (alterSequence.getSchemaName() != null) {
         TableObject tempSchemaName = alterSequence.getSchemaName();
         tempSchemaName.toInformix();
      }

      return alterSequence;
   }

   public AlterSequenceStatement toMSSQLServer() throws ConvertException {
      AlterSequenceStatement alterSequence = this.copyObjectValues();
      alterSequence.setAs((String)null);
      alterSequence.setDataType((Datatype)null);
      if (this.start == null) {
         alterSequence.setStart("START");
         alterSequence.setWith("WITH");
         alterSequence.setStartValue("1");
      }

      if (this.incrementString == null) {
         alterSequence.setIncrementString("INCREMENT BY");
         alterSequence.setIncrementValue("1");
      }

      if (alterSequence.getSchemaName() != null) {
         TableObject tempSchemaName = alterSequence.getSchemaName();
         tempSchemaName.toMSSQLServer();
      }

      return alterSequence;
   }

   public AlterSequenceStatement toMySQL() throws ConvertException {
      AlterSequenceStatement alterSequence = this.copyObjectValues();
      alterSequence.setAs((String)null);
      alterSequence.setDataType((Datatype)null);
      if (alterSequence.getSchemaName() != null) {
         TableObject tempSchemaName = alterSequence.getSchemaName();
         tempSchemaName.toMySQL();
      }

      return alterSequence;
   }

   public AlterSequenceStatement toOracle() throws ConvertException {
      AlterSequenceStatement alterSequence = this.copyObjectValues();
      alterSequence.setAs((String)null);
      alterSequence.setDataType((Datatype)null);
      if (alterSequence.getStart() != null) {
         String tempStart = alterSequence.getStart();
         alterSequence.setWith("WITH");
      }

      if (alterSequence.getSchemaName() != null) {
         TableObject tempSchemaName = alterSequence.getSchemaName();
         tempSchemaName.toOracle();
      }

      if (alterSequence.getMaxValueOrNoMaxValue() != null && alterSequence.getMaxValueOrNoMaxValue().toUpperCase().equalsIgnoreCase("NO MAXVALUE")) {
         alterSequence.setMaxValueOrNoMaxValue("NOMAXVALUE");
      }

      if (alterSequence.getMinValueOrNoMinValue() != null) {
         if (alterSequence.getMinValueOrNoMinValue().toUpperCase().equalsIgnoreCase("NO MINVALUE")) {
            alterSequence.setMinValueOrNoMinValue("NOMINVALUE");
         }
      } else if (this.startValue != null && Integer.parseInt(this.startValue.trim()) == 0) {
         alterSequence.setMinValueOrNoMinValue("MINVALUE 0");
      }

      if (alterSequence.getCycleOrNoCycle() != null && alterSequence.getCycleOrNoCycle().toUpperCase().equalsIgnoreCase("NO CYCLE")) {
         alterSequence.setCycleOrNoCycle("NOCYCLE");
      }

      if (alterSequence.getCacheOrNoCache() != null && alterSequence.getCacheOrNoCache().toUpperCase().equalsIgnoreCase("NO CACHE")) {
         alterSequence.setCacheOrNoCache("NOCACHE");
      }

      if (alterSequence.getOrderOrNoOrder() != null && alterSequence.getOrderOrNoOrder().toUpperCase().equalsIgnoreCase("NO ORDER")) {
         alterSequence.setOrderOrNoOrder("NOORDER");
      }

      return alterSequence;
   }

   public AlterSequenceStatement toBigQuery() throws ConvertException {
      AlterSequenceStatement alterSequence = this.copyObjectValues();
      alterSequence.setAs((String)null);
      alterSequence.setDataType((Datatype)null);
      if (alterSequence.getStart() != null) {
         String tempStart = alterSequence.getStart();
         alterSequence.setWith((String)null);
      }

      if (alterSequence.getSchemaName() != null) {
         TableObject tempSchemaName = alterSequence.getSchemaName();
         tempSchemaName.toBigQuery();
      }

      return alterSequence;
   }

   public AlterSequenceStatement toPostgreSQL() throws ConvertException {
      AlterSequenceStatement alterSequence = this.copyObjectValues();
      alterSequence.setAs((String)null);
      alterSequence.setDataType((Datatype)null);
      if (alterSequence.getStart() != null) {
         String tempStart = alterSequence.getStart();
         alterSequence.setWith((String)null);
      }

      if (alterSequence.getSchemaName() != null) {
         TableObject tempSchemaName = alterSequence.getSchemaName();
         tempSchemaName.toPostgreSQL();
      }

      return alterSequence;
   }

   public AlterSequenceStatement toSybase() throws ConvertException {
      AlterSequenceStatement alterSequence = this.copyObjectValues();
      alterSequence.setAs((String)null);
      alterSequence.setDataType((Datatype)null);
      if (this.start == null) {
         alterSequence.setStart("START");
         alterSequence.setWith("WITH");
         alterSequence.setStartValue("1");
      }

      if (this.incrementString == null) {
         alterSequence.setIncrementString("INCREMENT BY");
         alterSequence.setIncrementValue("1");
      }

      if (alterSequence.getSchemaName() != null) {
         TableObject tempSchemaName = alterSequence.getSchemaName();
         tempSchemaName.toSybase();
      }

      return alterSequence;
   }

   public AlterSequenceStatement toTimesTen() throws ConvertException {
      AlterSequenceStatement alterSequence = this.copyObjectValues();
      alterSequence.setAs((String)null);
      alterSequence.setDataType((Datatype)null);
      alterSequence.setStart((String)null);
      alterSequence.setWith((String)null);
      alterSequence.setStartValue((String)null);
      if (this.maxValueOrNoMaxValue != null && this.maxValueOrNoMaxValue.trim().equalsIgnoreCase("NOMAXVALUE")) {
         alterSequence.setMaxValueOrNoMaxValue((String)null);
      }

      if (this.minValueOrNoMinValue != null && this.minValueOrNoMinValue.trim().equalsIgnoreCase("NOMINVALUE")) {
         alterSequence.setMinValueOrNoMinValue((String)null);
      }

      alterSequence.setOrderOrNoOrder((String)null);
      if (this.cycleOrNoCycle != null && this.cycleOrNoCycle.equalsIgnoreCase("NOCYCLE")) {
         alterSequence.setCycleOrNoCycle((String)null);
      }

      if (alterSequence.getSchemaName() != null) {
         TableObject tempSchemaName = alterSequence.getSchemaName();
         tempSchemaName.toTimesTen();
      }

      return alterSequence;
   }

   public AlterSequenceStatement toNetezza() throws ConvertException {
      AlterSequenceStatement alterSequence = this.copyObjectValues();
      if (alterSequence.getCacheOrNoCache() != null) {
         alterSequence.setCacheOrNoCache((String)null);
      }

      if (alterSequence.getOrderOrNoOrder() != null) {
         alterSequence.setOrderOrNoOrder((String)null);
      }

      if (alterSequence.getCycleOrNoCycle() != null && alterSequence.getCycleOrNoCycle().toLowerCase().startsWith("no")) {
         alterSequence.setCycleOrNoCycle("NO CYCLE");
      }

      if (alterSequence.getMaxValueOrNoMaxValue() != null && alterSequence.getMaxValueOrNoMaxValue().toLowerCase().startsWith("no")) {
         alterSequence.setMaxValueOrNoMaxValue("NO MAXVALUE");
      }

      if (alterSequence.getMinValueOrNoMinValue() != null && alterSequence.getMinValueOrNoMinValue().toLowerCase().startsWith("no")) {
         alterSequence.setMinValueOrNoMinValue("NO MINVALUE");
      }

      if (alterSequence.getSchemaName() != null) {
         TableObject tempSchemaName = alterSequence.getSchemaName();
         tempSchemaName.toNetezza();
      }

      return alterSequence;
   }

   public AlterSequenceStatement toSnowflake() throws ConvertException {
      AlterSequenceStatement alterSequence = this.copyObjectValues();
      alterSequence.setAs((String)null);
      alterSequence.setDataType((Datatype)null);
      if (alterSequence.getStart() != null) {
         String tempStart = alterSequence.getStart();
         alterSequence.setWith((String)null);
      }

      if (alterSequence.getSchemaName() != null) {
         TableObject tempSchemaName = alterSequence.getSchemaName();
         tempSchemaName.toSnowflake();
      }

      return alterSequence;
   }

   public AlterSequenceStatement toTeradata() throws ConvertException {
      AlterSequenceStatement alterSequence = this.copyObjectValues();
      alterSequence.setAs((String)null);
      alterSequence.setDataType((Datatype)null);
      if (alterSequence.getSchemaName() != null) {
         TableObject tempSchemaName = alterSequence.getSchemaName();
         tempSchemaName.toTeradata();
      }

      return alterSequence;
   }

   public void setCommentClass(CommentClass commentObject) {
   }

   public CommentClass getCommentClass() {
      return null;
   }

   public UserObjectContext getObjectContext() {
      return null;
   }

   public String removeIndent(String formattedSqlString) {
      formattedSqlString = formattedSqlString.replace('\n', ' ');
      formattedSqlString = formattedSqlString.replace('\t', ' ');
      return formattedSqlString;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.alter != null) {
         sb.append(this.alter.toUpperCase());
      }

      if (this.sequence != null) {
         sb.append(" " + this.sequence.toUpperCase());
      }

      if (this.createTableObject != null) {
         this.createTableObject.setObjectContext(this.context);
         sb.append("  " + this.createTableObject);
      }

      if (this.as != null) {
         sb.append(" AS");
      }

      if (this.datatype != null) {
         sb.append(" " + this.datatype.toString());
      }

      if (this.start != null) {
         sb.append("\n\t" + this.start.toUpperCase());
      }

      if (this.with != null) {
         sb.append(" " + this.with.toUpperCase());
      }

      if (this.startValue != null) {
         sb.append(" " + this.startValue.toUpperCase());
      }

      if (this.incrementString != null) {
         sb.append("\n\t" + this.incrementString);
      }

      if (this.incrementValue != null) {
         sb.append(" " + this.incrementValue);
      }

      if (this.maxValueOrNoMaxValue != null) {
         sb.append("\n\t" + this.maxValueOrNoMaxValue.toUpperCase());
      }

      if (this.minValueOrNoMinValue != null) {
         sb.append("\n\t" + this.minValueOrNoMinValue.toUpperCase());
      }

      if (this.cycleOrNoCycle != null) {
         sb.append("\n\t" + this.cycleOrNoCycle.toUpperCase());
      }

      if (this.cacheOrNoCache != null) {
         sb.append("\n\t" + this.cacheOrNoCache.toUpperCase());
      }

      if (this.orderOrNoOrder != null) {
         sb.append("\n\t" + this.orderOrNoOrder.toUpperCase());
      }

      return sb.toString();
   }

   public String toVectorWiseString() throws ConvertException {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public SwisSQLStatement toVectorWise() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toSqlite() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toSapHana() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toAthena() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toMsAccessJdbc() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toExcel() throws ConvertException {
      return null;
   }
}
