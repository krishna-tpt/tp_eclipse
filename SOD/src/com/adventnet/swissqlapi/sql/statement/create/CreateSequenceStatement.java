package com.adventnet.swissqlapi.sql.statement.create;

import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;

public class CreateSequenceStatement {
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

   public String getIncrementString() {
      return this.incrementString;
   }

   public CreateSequenceStatement copyObjectValues() {
      CreateSequenceStatement dupCreateSequenceStatement = new CreateSequenceStatement();
      dupCreateSequenceStatement.setAs(this.as);
      dupCreateSequenceStatement.setCacheOrNoCache(this.cacheOrNoCache);
      dupCreateSequenceStatement.setCycleOrNoCycle(this.cycleOrNoCycle);
      dupCreateSequenceStatement.setDataType(this.datatype);
      dupCreateSequenceStatement.setIncrementString(this.incrementString);
      dupCreateSequenceStatement.setIncrementValue(this.incrementValue);
      dupCreateSequenceStatement.setMaxValueOrNoMaxValue(this.maxValueOrNoMaxValue);
      dupCreateSequenceStatement.setMinValueOrNoMinValue(this.minValueOrNoMinValue);
      dupCreateSequenceStatement.setOrderOrNoOrder(this.orderOrNoOrder);
      dupCreateSequenceStatement.setSequence(this.sequence);
      dupCreateSequenceStatement.setSchemaName(this.createTableObject);
      dupCreateSequenceStatement.setStart(this.start);
      dupCreateSequenceStatement.setStartValue(this.startValue);
      dupCreateSequenceStatement.setWith(this.with);
      dupCreateSequenceStatement.setObjectContext(this.context);
      return dupCreateSequenceStatement;
   }

   public CreateSequenceStatement toANSI() throws ConvertException {
      CreateSequenceStatement createSequence = this.copyObjectValues();
      createSequence.setAs((String)null);
      createSequence.setDataType((Datatype)null);
      if (createSequence.getSchemaName() != null) {
         TableObject tempSchemaName = createSequence.getSchemaName();
         tempSchemaName.toANSISQL();
      }

      return createSequence;
   }

   public CreateSequenceStatement toDB2() throws ConvertException {
      CreateSequenceStatement createSequence = this.copyObjectValues();
      if (createSequence.getSchemaName() != null) {
         TableObject tempSchemaName = createSequence.getSchemaName();
         tempSchemaName.toDB2();
      }

      if (createSequence.getDatatype() != null) {
         Datatype db2Datatype = createSequence.getDatatype();
         db2Datatype.toDB2String();
      }

      if (createSequence.getMaxValueOrNoMaxValue() != null && createSequence.getMaxValueOrNoMaxValue().toUpperCase().equalsIgnoreCase("NOMAXVALUE")) {
         createSequence.setMaxValueOrNoMaxValue("NO MAXVALUE");
      }

      if (createSequence.getMinValueOrNoMinValue() != null && createSequence.getMinValueOrNoMinValue().toUpperCase().equalsIgnoreCase("NOMINVALUE")) {
         createSequence.setMinValueOrNoMinValue("NO MINVALUE");
      }

      if (createSequence.getCycleOrNoCycle() != null && createSequence.getCycleOrNoCycle().toUpperCase().equalsIgnoreCase("NOCYCLE")) {
         createSequence.setCycleOrNoCycle("NO CYCLE");
      }

      if (createSequence.getCacheOrNoCache() != null && createSequence.getCacheOrNoCache().toUpperCase().equalsIgnoreCase("NOCACHE")) {
         createSequence.setCacheOrNoCache("NO CACHE");
      }

      if (createSequence.getOrderOrNoOrder() != null && createSequence.getOrderOrNoOrder().toUpperCase().equalsIgnoreCase("NOORDER")) {
         createSequence.setOrderOrNoOrder("NO ORDER");
      }

      return createSequence;
   }

   public CreateSequenceStatement toInformix() throws ConvertException {
      CreateSequenceStatement createSequence = this.copyObjectValues();
      createSequence.setAs((String)null);
      createSequence.setDataType((Datatype)null);
      if (createSequence.getSchemaName() != null) {
         TableObject tempSchemaName = createSequence.getSchemaName();
         tempSchemaName.toInformix();
      }

      return createSequence;
   }

   public CreateSequenceStatement toMSSQLServer() throws ConvertException {
      CreateSequenceStatement createSequence = this.copyObjectValues();
      createSequence.setAs((String)null);
      createSequence.setDataType((Datatype)null);
      if (this.start == null) {
         createSequence.setStart("START");
         createSequence.setWith("WITH");
         createSequence.setStartValue("1");
      }

      if (this.incrementString == null) {
         createSequence.setIncrementString("INCREMENT BY");
         createSequence.setIncrementValue("1");
      }

      if (createSequence.getSchemaName() != null) {
         TableObject tempSchemaName = createSequence.getSchemaName();
         tempSchemaName.toMSSQLServer();
      }

      return createSequence;
   }

   public CreateSequenceStatement toMySQL() throws ConvertException {
      CreateSequenceStatement createSequence = this.copyObjectValues();
      createSequence.setAs((String)null);
      createSequence.setDataType((Datatype)null);
      if (createSequence.getSchemaName() != null) {
         TableObject tempSchemaName = createSequence.getSchemaName();
         tempSchemaName.toMySQL();
      }

      return createSequence;
   }

   public CreateSequenceStatement toOracle() throws ConvertException {
      CreateSequenceStatement createSequence = this.copyObjectValues();
      createSequence.setAs((String)null);
      createSequence.setDataType((Datatype)null);
      if (createSequence.getStart() != null) {
         String tempStart = createSequence.getStart();
         createSequence.setWith("WITH");
      }

      if (createSequence.getSchemaName() != null) {
         TableObject tempSchemaName = createSequence.getSchemaName();
         tempSchemaName.toOracle();
      }

      if (createSequence.getMaxValueOrNoMaxValue() != null && createSequence.getMaxValueOrNoMaxValue().toUpperCase().equalsIgnoreCase("NO MAXVALUE")) {
         createSequence.setMaxValueOrNoMaxValue("NOMAXVALUE");
      }

      if (createSequence.getMinValueOrNoMinValue() != null) {
         if (createSequence.getMinValueOrNoMinValue().toUpperCase().equalsIgnoreCase("NO MINVALUE")) {
            createSequence.setMinValueOrNoMinValue("NOMINVALUE");
         }
      } else if (this.startValue != null && Integer.parseInt(this.startValue.trim()) == 0) {
         createSequence.setMinValueOrNoMinValue("MINVALUE 0");
      }

      if (createSequence.getCycleOrNoCycle() != null && createSequence.getCycleOrNoCycle().toUpperCase().equalsIgnoreCase("NO CYCLE")) {
         createSequence.setCycleOrNoCycle("NOCYCLE");
      }

      if (createSequence.getCacheOrNoCache() != null && createSequence.getCacheOrNoCache().toUpperCase().equalsIgnoreCase("NO CACHE")) {
         createSequence.setCacheOrNoCache("NOCACHE");
      }

      if (createSequence.getOrderOrNoOrder() != null && createSequence.getOrderOrNoOrder().toUpperCase().equalsIgnoreCase("NO ORDER")) {
         createSequence.setOrderOrNoOrder("NOORDER");
      }

      return createSequence;
   }

   public CreateSequenceStatement toBigQuery() throws ConvertException {
      CreateSequenceStatement createSequence = this.copyObjectValues();
      createSequence.setAs((String)null);
      createSequence.setDataType((Datatype)null);
      if (createSequence.getStart() != null) {
         String tempStart = createSequence.getStart();
         createSequence.setWith((String)null);
      }

      if (createSequence.getIncrementString() != null) {
         createSequence.setIncrementString("INCREMENT ");
      }

      if (createSequence.getSchemaName() != null) {
         TableObject tempSchemaName = createSequence.getSchemaName();
         tempSchemaName.toBigQuery();
      }

      return createSequence;
   }

   public CreateSequenceStatement toPostgreSQL() throws ConvertException {
      CreateSequenceStatement createSequence = this.copyObjectValues();
      createSequence.setAs((String)null);
      createSequence.setDataType((Datatype)null);
      if (createSequence.getStart() != null) {
         String tempStart = createSequence.getStart();
         createSequence.setWith((String)null);
      }

      if (createSequence.getIncrementString() != null) {
         createSequence.setIncrementString("INCREMENT ");
      }

      if (createSequence.getSchemaName() != null) {
         TableObject tempSchemaName = createSequence.getSchemaName();
         tempSchemaName.toPostgreSQL();
      }

      return createSequence;
   }

   public CreateSequenceStatement toSybase() throws ConvertException {
      CreateSequenceStatement createSequence = this.copyObjectValues();
      createSequence.setAs((String)null);
      createSequence.setDataType((Datatype)null);
      if (this.start == null) {
         createSequence.setStart("START");
         createSequence.setWith("WITH");
         createSequence.setStartValue("1");
      }

      if (this.incrementString == null) {
         createSequence.setIncrementString("INCREMENT BY");
         createSequence.setIncrementValue("1");
      }

      if (createSequence.getSchemaName() != null) {
         TableObject tempSchemaName = createSequence.getSchemaName();
         tempSchemaName.toSybase();
      }

      return createSequence;
   }

   public CreateSequenceStatement toTimesTen() throws ConvertException {
      CreateSequenceStatement createSequence = this.copyObjectValues();
      createSequence.setAs((String)null);
      createSequence.setDataType((Datatype)null);
      createSequence.setStart((String)null);
      createSequence.setWith((String)null);
      createSequence.setStartValue((String)null);
      if (this.maxValueOrNoMaxValue != null && this.maxValueOrNoMaxValue.trim().equalsIgnoreCase("NOMAXVALUE")) {
         createSequence.setMaxValueOrNoMaxValue((String)null);
      }

      if (this.minValueOrNoMinValue != null && this.minValueOrNoMinValue.trim().equalsIgnoreCase("NOMINVALUE")) {
         createSequence.setMinValueOrNoMinValue((String)null);
      }

      createSequence.setOrderOrNoOrder((String)null);
      if (this.cycleOrNoCycle != null && this.cycleOrNoCycle.equalsIgnoreCase("NOCYCLE")) {
         createSequence.setCycleOrNoCycle((String)null);
      }

      if (createSequence.getSchemaName() != null) {
         TableObject tempSchemaName = createSequence.getSchemaName();
         tempSchemaName.toTimesTen();
      }

      return createSequence;
   }

   public CreateSequenceStatement toNetezza() throws ConvertException {
      CreateSequenceStatement createSequence = this.copyObjectValues();
      if (createSequence.getCacheOrNoCache() != null) {
         createSequence.setCacheOrNoCache((String)null);
      }

      if (createSequence.getOrderOrNoOrder() != null) {
         createSequence.setOrderOrNoOrder((String)null);
      }

      if (createSequence.getCycleOrNoCycle() != null && createSequence.getCycleOrNoCycle().toLowerCase().startsWith("no")) {
         createSequence.setCycleOrNoCycle("NO CYCLE");
      }

      if (createSequence.getMaxValueOrNoMaxValue() != null && createSequence.getMaxValueOrNoMaxValue().toLowerCase().startsWith("no")) {
         createSequence.setMaxValueOrNoMaxValue("NO MAXVALUE");
      }

      if (createSequence.getMinValueOrNoMinValue() != null && createSequence.getMinValueOrNoMinValue().toLowerCase().startsWith("no")) {
         createSequence.setMinValueOrNoMinValue("NO MINVALUE");
      }

      if (createSequence.getSchemaName() != null) {
         TableObject tempSchemaName = createSequence.getSchemaName();
         tempSchemaName.toNetezza();
      }

      return createSequence;
   }

   public CreateSequenceStatement toSnowflake() throws ConvertException {
      CreateSequenceStatement createSequence = this.copyObjectValues();
      createSequence.setAs((String)null);
      createSequence.setDataType((Datatype)null);
      if (createSequence.getStart() != null) {
         String tempStart = createSequence.getStart();
         createSequence.setWith((String)null);
      }

      if (createSequence.getIncrementString() != null) {
         createSequence.setIncrementString("INCREMENT ");
      }

      if (createSequence.getSchemaName() != null) {
         TableObject tempSchemaName = createSequence.getSchemaName();
         tempSchemaName.toSnowflake();
      }

      return createSequence;
   }

   public CreateSequenceStatement toTeradata() throws ConvertException {
      CreateSequenceStatement createSequence = this.copyObjectValues();
      createSequence.setAs((String)null);
      createSequence.setDataType((Datatype)null);
      if (createSequence.getSchemaName() != null) {
         TableObject tempSchemaName = createSequence.getSchemaName();
         tempSchemaName.toTeradata();
      }

      return createSequence;
   }

   public String removeIndent(String str) {
      str = str.replace('\n', ' ');
      str = str.replace('\t', ' ');
      return str;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
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
}
