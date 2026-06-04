package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.ModifiedObjectAttr;
import com.adventnet.swissqlapi.sql.statement.OverrideToString;
import com.adventnet.swissqlapi.sql.statement.delete.DeleteQueryStatement;
import com.adventnet.swissqlapi.sql.statement.update.UpdateQueryStatement;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.util.database.MetadataInfoUtil;
import com.adventnet.swissqlapi.util.misc.CastingUtil;
import com.adventnet.swissqlapi.util.misc.CustomizeUtil;
import com.adventnet.swissqlapi.util.misc.DB2DataTypeConverter;
import com.adventnet.swissqlapi.util.misc.GeneralUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

public class TableColumn {
   private String OwnerName;
   private String dot = ".";
   private String TableName;
   private String ColumnName;
   private String tempTableName;
   private String startPosition;
   private String endPosition;
   private int startValue;
   private int endValue;
   private int length;
   private UserObjectContext context = null;
   private String targetDataType;
   private boolean toDB2 = false;
   private OverrideToString override_to_string;
   private boolean isTenroxRequirement = false;
   private boolean isFunctionName = false;
   private String sourceDataType;
   private UpdateQueryStatement fromUQS;
   private DeleteQueryStatement fromDQS;
   private String origTableName;
   private CommentClass commentObj;
   private CommentClass commentObjAfterToken;
   private boolean isOuterJoined = false;
   private String databaseName;
   private String collateClause;
   private String collationName;
   private boolean isPivotColumn = false;
   private String instanceDatatype = "UNDEFINED";
   public static ArrayList<String> pgSqlKeywordsList = getPgSQLKeyWordList();
   public static ArrayList<String> pgSqlSpecCharList = getPgSQLSpecCharList();

   public void setObjectContext(UserObjectContext context) {
      this.context = context;
   }

   public void setOwnerName(String s_on) {
      this.OwnerName = s_on;
   }

   public void setDot(String d) {
      this.dot = d;
   }

   public void setTableName(String s_tn) {
      this.TableName = s_tn;
   }

   public void setColumnName(String s_cn) {
      this.ColumnName = s_cn;
   }

   public void setTempTableColumnName(String s_ttcn) {
      this.tempTableName = s_ttcn;
   }

   public void setStartPosition(String startPosition) {
      this.startPosition = startPosition;
   }

   public void setEndPosition(String endPosition) {
      this.endPosition = endPosition;
   }

   public void setFromUQS(UpdateQueryStatement fromUQS) {
      this.fromUQS = fromUQS;
   }

   public void setFromDQS(DeleteQueryStatement fromDQS) {
      this.fromDQS = fromDQS;
   }

   public void registerOverrideToString(OverrideToString ots) {
      this.override_to_string = ots;
   }

   public void setIsFunctionName(boolean isFunctionName) {
      this.isFunctionName = isFunctionName;
   }

   public void setOrigTableName(String origTableName) {
      this.origTableName = origTableName;
   }

   public void setCommentClass(CommentClass commentObj) {
      this.commentObj = commentObj;
   }

   public void setCommentClassAfterToken(CommentClass commentObj) {
      this.commentObjAfterToken = commentObj;
   }

   public void setOuterJoin(boolean val) {
      this.isOuterJoined = val;
   }

   public void setIsPivotColumn(boolean val) {
      this.isPivotColumn = val;
   }

   public CommentClass getCommentClass() {
      return this.commentObj;
   }

   public CommentClass getCommentClassAfterToken() {
      return this.commentObjAfterToken;
   }

   public String getOrigTableName() {
      return this.origTableName;
   }

   public String getDot() {
      return this.dot;
   }

   public String getOwnerName() {
      return this.OwnerName;
   }

   public String getTableName() {
      return this.TableName;
   }

   public String getColumnName() {
      return this.ColumnName;
   }

   public String getTempTableColumnName() {
      return this.tempTableName;
   }

   public String getStartPosition() {
      return this.startPosition;
   }

   public String getEndPosition() {
      return this.endPosition;
   }

   public boolean getOuterJoin() {
      return this.isOuterJoined;
   }

   public boolean isPivotColumn() {
      return this.isPivotColumn;
   }

   public void setToDB2(boolean toDB2) {
      this.toDB2 = toDB2;
   }

   public void setTargetDataType(String targetDataType) {
      this.targetDataType = targetDataType;
   }

   public void setSourceDataType(String sourceDataType) {
      this.sourceDataType = sourceDataType;
   }

   public String getDatabaseName() {
      return this.databaseName;
   }

   public void setDatabaseName(String databaseName) {
      this.databaseName = databaseName;
   }

   public String getCollationName() {
      return this.collationName;
   }

   public void setCollationName(String collationName) {
      this.collationName = collationName;
   }

   public String getCollateClause() {
      return this.collateClause;
   }

   public void setCollateClause(String collateClause) {
      this.collateClause = collateClause;
   }

   public void setInstanceDatatype(String instanceDatatype) {
      this.instanceDatatype = instanceDatatype;
   }

   public String getInstanceDatatype() {
      return this.instanceDatatype;
   }

   public TableColumn toMSSQLServerSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) {
      TableColumn tn = new TableColumn();
      tn.setCommentClass(this.commentObj);
      if (this.TableName != null && this.TableName.startsWith("`") && this.TableName.endsWith("`")) {
         this.TableName = this.TableName.substring(1, this.TableName.length() - 1);
         if (this.TableName.indexOf(32) != -1) {
            this.TableName = "\"" + this.TableName + "\"";
         }
      }

      if (this.ColumnName != null && this.ColumnName.startsWith("`") && this.ColumnName.endsWith("`")) {
         this.ColumnName = this.ColumnName.substring(1, this.ColumnName.length() - 1);
         if (this.ColumnName.indexOf(32) != -1) {
            this.ColumnName = "\"" + this.ColumnName + "\"";
         }
      }

      if (this.OwnerName != null && this.OwnerName.startsWith("`") && this.OwnerName.endsWith("`")) {
         this.OwnerName = this.OwnerName.substring(1, this.OwnerName.length() - 1);
         if (this.OwnerName.indexOf(32) != -1) {
            this.OwnerName = "\"" + this.OwnerName + "\"";
         }
      }

      String tempColumnName;
      if (this.ColumnName.startsWith("\"") && this.ColumnName.endsWith("\"")) {
         tempColumnName = this.ColumnName.substring(1, this.ColumnName.length() - 1);
         if (tempColumnName.trim().equals("")) {
            this.ColumnName = "'" + tempColumnName + "'";
         }
      }

      tn.setOwnerName(this.OwnerName);
      tn.registerOverrideToString(this.override_to_string);
      tn.setTableName(this.TableName);
      if ((this.ColumnName.equalsIgnoreCase("DATE") || this.ColumnName.equalsIgnoreCase("SYSDATE") || this.ColumnName.equalsIgnoreCase("CURRENT DATE") || this.ColumnName.equalsIgnoreCase("CURRENT_DATE")) && !SwisSQLOptions.fromSybase && !this.isFunctionName) {
         tn.setColumnName("GETDATE()");
      } else if (this.ColumnName.equalsIgnoreCase("SYS_GUID")) {
         tn.setColumnName("NEWID()");
      } else if (this.ColumnName.equalsIgnoreCase("TIME") && from_sqs != null && from_sqs.getFromClause() == null) {
         tn.setColumnName("CURRENT_TIME");
      } else if (!this.ColumnName.equalsIgnoreCase("SYSTIMESTAMP") && !this.ColumnName.equalsIgnoreCase("CURRENT DATE") && !this.ColumnName.equalsIgnoreCase("CURRENT")) {
         if (!this.ColumnName.equalsIgnoreCase("USER") && !this.ColumnName.equalsIgnoreCase("CURRENT_USER")) {
            if (this.startPosition != null) {
               this.startValue = Integer.parseInt(this.startPosition);
               this.endValue = Integer.parseInt(this.endPosition);
               this.length = this.endValue - this.startValue + 1;
               tempColumnName = this.getColumnName();
               tn.setColumnName("SUBSTRING(" + tempColumnName + "," + this.startValue + "," + this.length + ")");
               tn.setStartPosition((String)null);
               tn.setEndPosition((String)null);
            } else {
               tn.setColumnName(this.ColumnName);
            }
         } else if (SwisSQLOptions.fromSybase) {
            tn.setColumnName("USER");
         } else {
            tn.setColumnName("SYSTEM_USER");
         }
      } else {
         tn.setColumnName("CURRENT_TIMESTAMP");
      }

      if (from_sqs != null && from_sqs.isMSAzure()) {
         if (this.OwnerName != null) {
            tn.setDot(".");
         }
      } else if (SwisSQLOptions.removeDBSchemaQualifier && this.OwnerName != null) {
         tn.setOwnerName((String)null);
         tn.setDot(".");
      } else if (this.OwnerName != null && this.OwnerName.equalsIgnoreCase("dbo")) {
         tn.setDot(".");
      } else {
         tn.setDot(new String(".."));
      }

      if (this.ColumnName.startsWith(":")) {
         tn.setColumnName("@" + this.ColumnName.substring(1));
      }

      if (this.collateClause != null && this.collationName != null && !this.collateClause.isEmpty() && !this.collationName.isEmpty()) {
         tn.setCollateClause(this.collateClause);
         tn.setCollationName(this.collationName);
      }

      return tn;
   }

   public TableColumn toSybaseSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) {
      TableColumn tn = new TableColumn();
      tn.setCommentClass(this.commentObj);
      if (this.TableName != null && this.TableName.startsWith("`") && this.TableName.endsWith("`")) {
         this.TableName = this.TableName.substring(1, this.TableName.length() - 1);
         if (this.TableName.indexOf(32) != -1) {
            this.TableName = "\"" + this.TableName + "\"";
         }
      }

      if (this.ColumnName != null && this.ColumnName.startsWith("`") && this.ColumnName.endsWith("`")) {
         this.ColumnName = this.ColumnName.substring(1, this.ColumnName.length() - 1);
         if (this.ColumnName.indexOf(32) != -1) {
            this.ColumnName = "\"" + this.ColumnName + "\"";
         }
      }

      if (this.OwnerName != null && this.OwnerName.startsWith("`") && this.OwnerName.endsWith("`")) {
         this.OwnerName = this.OwnerName.substring(1, this.OwnerName.length() - 1);
         if (this.OwnerName.indexOf(32) != -1) {
            this.OwnerName = "\"" + this.OwnerName + "\"";
         }
      }

      tn.setOwnerName(this.OwnerName);
      tn.registerOverrideToString(this.override_to_string);
      tn.setTableName(this.TableName);
      if ((this.ColumnName.equalsIgnoreCase("DATE") || this.ColumnName.equalsIgnoreCase("SYSDATE") || this.ColumnName.equalsIgnoreCase("CURRENT DATE") || this.ColumnName.equalsIgnoreCase("CURRENT_DATE")) && !this.isFunctionName) {
         tn.setColumnName("GETDATE()");
      } else if (this.ColumnName.equalsIgnoreCase("SYS_GUID")) {
         tn.setColumnName("NEWID()");
      } else if (this.ColumnName.equalsIgnoreCase("TIME") && from_sqs != null && from_sqs.getFromClause() == null) {
         tn.setColumnName("CURRENT_TIME");
      } else if (!this.ColumnName.equalsIgnoreCase("SYSTIMESTAMP") && !this.ColumnName.equalsIgnoreCase("CURRENT DATE") && !this.ColumnName.equalsIgnoreCase("CURRENT")) {
         if (!this.ColumnName.equalsIgnoreCase("USER") && !this.ColumnName.equalsIgnoreCase("CURRENT_USER") && !this.ColumnName.equalsIgnoreCase("SYSTEM_USER")) {
            if (this.startPosition != null) {
               this.startValue = Integer.parseInt(this.startPosition);
               this.endValue = Integer.parseInt(this.endPosition);
               this.length = this.endValue - this.startValue + 1;
               String tempColumnName = this.getColumnName();
               tn.setColumnName("SUBSTRING(" + tempColumnName + "," + this.startValue + "," + this.length + ")");
               tn.setStartPosition((String)null);
               tn.setEndPosition((String)null);
            } else {
               tn.setColumnName(this.ColumnName);
            }
         } else {
            tn.setColumnName("USER");
         }
      } else {
         tn.setColumnName("CURRENT_TIMESTAMP");
      }

      if (this.OwnerName != null && !this.OwnerName.equalsIgnoreCase("dbo") && SwisSQLOptions.fullyQualifiedWithDatabaseName) {
         tn.setDatabaseName(this.OwnerName);
         tn.setOwnerName("dbo");
      } else {
         tn.setDot(new String(".."));
      }

      if (this.ColumnName.startsWith(":")) {
         tn.setColumnName("@" + this.ColumnName.substring(1));
      } else {
         tn.setObjectContext(this.context);
      }

      return tn;
   }

   public TableColumn toANSISelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) {
      TableColumn tn = new TableColumn();
      tn.setCommentClass(this.commentObj);
      if (this.TableName != null && (this.TableName.startsWith("[") && this.TableName.endsWith("]") || this.TableName.startsWith("`") && this.TableName.endsWith("`"))) {
         this.TableName = this.TableName.substring(1, this.TableName.length() - 1);
         if (this.TableName.indexOf(32) != -1) {
            this.TableName = "\"" + this.TableName + "\"";
         }
      }

      if (this.ColumnName != null && (this.ColumnName.startsWith("[") && this.ColumnName.endsWith("]") || this.ColumnName.startsWith("`") && this.ColumnName.endsWith("`"))) {
         this.ColumnName = this.ColumnName.substring(1, this.ColumnName.length() - 1);
         if (this.ColumnName.indexOf(32) != -1) {
            this.ColumnName = "\"" + this.ColumnName + "\"";
         }
      }

      if (this.OwnerName != null && (this.OwnerName.startsWith("[") && this.OwnerName.endsWith("]") || this.OwnerName.startsWith("`") && this.OwnerName.endsWith("`")) && SwisSQLOptions.setDoubleQuotesToAnsiSqlTableObjects) {
         this.OwnerName = this.OwnerName.substring(1, this.OwnerName.length() - 1);
         if (this.OwnerName.indexOf(32) != -1) {
            this.OwnerName = "\"" + this.OwnerName + "\"";
         }
      }

      if (this.TableName != null && !this.TableName.startsWith("\"") && !this.TableName.endsWith("\"") && SwisSQLOptions.setDoubleQuotesToAnsiSqlTableObjects) {
         this.TableName = "\"" + this.TableName + "\"";
      }

      if (this.ColumnName != null && !this.ColumnName.startsWith("\"") && !this.ColumnName.endsWith("\"") && SwisSQLOptions.setDoubleQuotesToAnsiSqlTableObjects) {
         this.ColumnName = "\"" + this.ColumnName + "\"";
      }

      tn.registerOverrideToString(this.override_to_string);
      tn.setOwnerName(this.OwnerName);
      tn.setTableName(this.TableName);
      if (this.ColumnName.charAt(0) == '\'' && SwisSQLOptions.setDoubleQuotesToAnsiSqlTableObjects) {
         tn.setColumnName(this.ColumnName.replace('\'', '"'));
      } else if ((this.ColumnName.equalsIgnoreCase("DATE") || this.ColumnName.equalsIgnoreCase("SYSDATE") || this.ColumnName.equalsIgnoreCase("CURRENT DATE")) && !this.isFunctionName) {
         tn.setColumnName("CURRENT_DATE");
      } else if (this.ColumnName.equalsIgnoreCase("SYS_GUID")) {
         tn.setColumnName("NEWID()");
      } else if ((!this.ColumnName.equalsIgnoreCase("TIME") || from_sqs == null || from_sqs.getFromClause() != null) && !this.ColumnName.equalsIgnoreCase("CURRENT TIME")) {
         if (!this.ColumnName.equalsIgnoreCase("TIMESTAMP") && !this.ColumnName.equalsIgnoreCase("SYSTIMESTAMP") && !this.ColumnName.equalsIgnoreCase("CURRENT TIMESTAMP") && !this.ColumnName.equalsIgnoreCase("CURRENT")) {
            if (!this.ColumnName.equalsIgnoreCase("USER") && !this.ColumnName.equalsIgnoreCase("SYSTEM_USER")) {
               if (this.startPosition != null) {
                  this.startValue = Integer.parseInt(this.startPosition);
                  this.endValue = Integer.parseInt(this.endPosition);
                  this.length = this.endValue - this.startValue + 1;
                  String tempColumnName = this.getColumnName();
                  tn.setColumnName("SUBSTR(" + tempColumnName + "," + this.startValue + "," + this.length + ")");
                  tn.setStartPosition((String)null);
                  tn.setEndPosition((String)null);
               } else {
                  tn.setColumnName(this.ColumnName);
               }
            } else {
               tn.setColumnName("CURRENT_USER");
            }
         } else {
            tn.setColumnName("CURRENT_TIMESTAMP");
         }
      } else {
         tn.setColumnName("CURRENT_TIME");
      }

      tn.setDot(new String("."));
      return tn;
   }

   public TableColumn toTeradataSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) {
      TableColumn tn = new TableColumn();
      tn.setCommentClass(this.commentObj);
      if (this.TableName != null && (this.TableName.startsWith("[") && this.TableName.endsWith("]") || this.TableName.startsWith("`") && this.TableName.endsWith("`"))) {
         this.TableName = this.TableName.substring(1, this.TableName.length() - 1);
         if (this.TableName.indexOf(32) != -1) {
            this.TableName = "\"" + this.TableName + "\"";
         }
      }

      if (this.ColumnName != null && (this.ColumnName.startsWith("[") && this.ColumnName.endsWith("]") || this.ColumnName.startsWith("`") && this.ColumnName.endsWith("`"))) {
         this.ColumnName = this.ColumnName.substring(1, this.ColumnName.length() - 1);
         if (this.ColumnName.indexOf(32) != -1) {
            this.ColumnName = "\"" + this.ColumnName + "\"";
         }
      }

      if (this.OwnerName != null && (this.OwnerName.startsWith("[") && this.OwnerName.endsWith("]") || this.OwnerName.startsWith("`") && this.OwnerName.endsWith("`"))) {
         this.OwnerName = this.OwnerName.substring(1, this.OwnerName.length() - 1);
         if (this.OwnerName.indexOf(32) != -1) {
            this.OwnerName = "\"" + this.OwnerName + "\"";
         }
      }

      if (this.OwnerName != null && !this.OwnerName.startsWith("\"") && !this.isFunctionName) {
         this.OwnerName = CustomizeUtil.objectNamesToQuotedIdentifier(this.OwnerName, SwisSQLUtils.getKeywords("teradata"), (ModifiedObjectAttr)null, -1);
      }

      if (this.ColumnName != null && !this.ColumnName.startsWith("\"") && !this.isFunctionName && !this.isSystemFunction(this.ColumnName)) {
         this.ColumnName = CustomizeUtil.objectNamesToQuotedIdentifier(this.ColumnName, SwisSQLUtils.getKeywords("teradata"), (ModifiedObjectAttr)null, -1);
      }

      if (this.TableName != null && !this.TableName.startsWith("\"") && !this.isFunctionName) {
         this.TableName = CustomizeUtil.objectNamesToQuotedIdentifier(this.TableName, SwisSQLUtils.getKeywords("teradata"), (ModifiedObjectAttr)null, -1);
         if (this.TableName.equalsIgnoreCase("DUAL") || this.TableName.equalsIgnoreCase("SYS.DUAL")) {
            this.TableName = "\"DUAL\"";
         }
      }

      if ((this.ColumnName.equalsIgnoreCase("\"rownum\"") || this.ColumnName.equalsIgnoreCase("rownum")) && to_sqs != null) {
         to_sqs.setRownumColumnPresent(true);
      }

      tn.registerOverrideToString(this.override_to_string);
      tn.setOwnerName(this.OwnerName);
      tn.setTableName(this.TableName);
      if (!this.ColumnName.equalsIgnoreCase("DATE") && !this.ColumnName.equalsIgnoreCase("CURRENT DATE")) {
         if (this.ColumnName.equalsIgnoreCase("SYSDATE") && !this.isFunctionName) {
            tn.setColumnName("CURRENT_TIMESTAMP(0)");
         } else if (this.ColumnName.equalsIgnoreCase("SYS_GUID")) {
            tn.setColumnName("NEWID()");
         } else if ((!this.ColumnName.equalsIgnoreCase("TIME") || from_sqs == null || from_sqs.getFromClause() != null) && !this.ColumnName.equalsIgnoreCase("CURRENT TIME")) {
            if (!this.ColumnName.equalsIgnoreCase("TIMESTAMP") && !this.ColumnName.equalsIgnoreCase("SYSTIMESTAMP") && !this.ColumnName.equalsIgnoreCase("CURRENT TIMESTAMP") && !this.ColumnName.equalsIgnoreCase("CURRENT")) {
               if (!this.ColumnName.equalsIgnoreCase("USER") && !this.ColumnName.equalsIgnoreCase("SYSTEM_USER")) {
                  if (this.startPosition != null) {
                     this.startValue = Integer.parseInt(this.startPosition);
                     this.endValue = Integer.parseInt(this.endPosition);
                     this.length = this.endValue - this.startValue + 1;
                     String tempColumnName = this.getColumnName();
                     tn.setColumnName("SUBSTR(" + tempColumnName + "," + this.startValue + "," + this.length + ")");
                     tn.setStartPosition((String)null);
                     tn.setEndPosition((String)null);
                  } else {
                     tn.setColumnName(this.ColumnName);
                  }
               } else {
                  tn.setColumnName("CURRENT_USER");
               }
            } else {
               tn.setColumnName("CURRENT_TIMESTAMP(0)");
            }
         } else {
            tn.setColumnName("CURRENT_TIME");
         }
      } else {
         tn.setColumnName("CURRENT_DATE");
      }

      tn.setDot(new String("."));
      return tn;
   }

   public TableColumn toDB2Select(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) {
      this.sourceDataType = MetadataInfoUtil.getDatatypeName(from_sqs, this);
      TableColumn tn = new TableColumn();
      tn.setCommentClass(this.commentObj);
      tn.setTargetDataType(this.targetDataType);
      tn.setSourceDataType(this.sourceDataType);
      tn.setToDB2(true);
      if (this.TableName != null && (this.TableName.startsWith("[") && this.TableName.endsWith("]") || this.TableName.startsWith("`") && this.TableName.endsWith("`"))) {
         this.TableName = this.TableName.substring(1, this.TableName.length() - 1);
         if (this.TableName.indexOf(32) != -1) {
            this.TableName = "\"" + this.TableName + "\"";
         }
      }

      if (this.ColumnName != null && (this.ColumnName.startsWith("[") && this.ColumnName.endsWith("]") || this.ColumnName.startsWith("`") && this.ColumnName.endsWith("`"))) {
         this.ColumnName = this.ColumnName.substring(1, this.ColumnName.length() - 1);
         if (this.ColumnName.indexOf(32) != -1) {
            this.ColumnName = "\"" + this.ColumnName + "\"";
         }
      }

      if (this.OwnerName != null && (this.OwnerName.startsWith("[") && this.OwnerName.endsWith("]") || this.OwnerName.startsWith("`") && this.OwnerName.endsWith("`"))) {
         this.OwnerName = this.OwnerName.substring(1, this.OwnerName.length() - 1);
         if (this.OwnerName.indexOf(32) != -1) {
            this.OwnerName = "\"" + this.OwnerName + "\"";
         }
      }

      tn.setOwnerName(this.OwnerName);
      tn.registerOverrideToString(this.override_to_string);
      tn.setTableName(this.TableName);
      String tempColumnName;
      if (this.ColumnName != null && this.ColumnName.charAt(0) == '"') {
         if (this.startPosition != null) {
            this.startValue = Integer.parseInt(this.startPosition);
            this.endValue = Integer.parseInt(this.endPosition);
            this.length = this.endValue - this.startValue + 1;
            tempColumnName = this.getColumnName();
            tn.setColumnName("SUBSTR(" + tempColumnName.toUpperCase() + "," + this.startValue + "," + this.length + ")");
            tn.setStartPosition((String)null);
            tn.setEndPosition((String)null);
         } else {
            tn.setColumnName(this.ColumnName);
         }
      } else if (this.ColumnName != null && (this.ColumnName.equalsIgnoreCase("DATE") || this.ColumnName.equalsIgnoreCase("CURRENT_DATE"))) {
         tn.setColumnName("CURRENT DATE");
      } else if (this.ColumnName == null || (!this.ColumnName.equalsIgnoreCase("TIME") || from_sqs == null || from_sqs.getFromClause() != null) && !this.ColumnName.equalsIgnoreCase("CURRENT_TIME")) {
         if (this.ColumnName != null && (this.ColumnName.equalsIgnoreCase("SYSDATE") || this.ColumnName.equalsIgnoreCase("SYSTIMESTAMP") || this.ColumnName.equalsIgnoreCase("CURRENT_TIMESTAMP") || this.ColumnName.equalsIgnoreCase("CURRENT")) && !this.isFunctionName) {
            tn.setColumnName("CURRENT TIMESTAMP");
         } else if (this.ColumnName != null && (this.ColumnName.equalsIgnoreCase("CURRENT_USER") || this.ColumnName.equalsIgnoreCase("SYSTEM_USER"))) {
            tn.setColumnName("USER");
         } else if (this.startPosition != null) {
            this.startValue = Integer.parseInt(this.startPosition);
            this.endValue = Integer.parseInt(this.endPosition);
            this.length = this.endValue - this.startValue + 1;
            tempColumnName = this.getColumnName();
            tn.setColumnName("SUBSTR(" + tempColumnName + "," + this.startValue + "," + this.length + ")");
            tn.setStartPosition((String)null);
            tn.setEndPosition((String)null);
         } else {
            tn.setColumnName(this.ColumnName);
         }
      } else {
         tn.setColumnName("CURRENT TIME");
      }

      tn.setDot(new String("."));
      return tn;
   }

   public TableColumn toOracleSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) {
      boolean isdenodo = from_sqs != null && from_sqs.isDenodo();
      TableColumn tn = new TableColumn();
      tn.setCommentClass(this.commentObj);
      if (this.origTableName == null && this.TableName != null) {
         tn.setOrigTableName(this.TableName);
      }

      boolean quotes = false;
      if (this.TableName != null) {
         if (this.TableName.startsWith("#")) {
            this.TableName = this.TableName.substring(1);
         }

         if (this.TableName.startsWith("@")) {
            this.TableName = "\"" + this.TableName + "\"";
         }

         if (isdenodo && !GeneralUtil.isItEnclosedTblCol(this.TableName)) {
            this.TableName = "\"" + this.TableName + "\"";
         }

         tn.setOrigTableName(this.TableName);
      }

      if (this.TableName != null && !this.isFunctionName) {
         this.TableName = CustomizeUtil.objectNamesToQuotedIdentifier(this.TableName, SwisSQLUtils.getKeywords(1), (ModifiedObjectAttr)null, 1);
      }

      if (this.ColumnName != null && this.TableName == null && this.ColumnName.startsWith("\"") && this.ColumnName.endsWith("\"")) {
         quotes = true;
      }

      boolean addQuotes;
      FromClause fc;
      FromTable ft;
      FromTable ft;
      if (this.ColumnName != null && !this.isFunctionName) {
         addQuotes = true;
         if (this.TableName == null && !this.ColumnName.startsWith("[") && !this.ColumnName.startsWith("\"") && !this.ColumnName.startsWith("`") && (this.ColumnName.equalsIgnoreCase("sysdate") || this.ColumnName.equalsIgnoreCase("rownum") || this.ColumnName.equalsIgnoreCase("user"))) {
            addQuotes = false;
            if (from_sqs != null) {
               fc = from_sqs.getFromClause();
               if (fc != null) {
                  ft = MetadataInfoUtil.getTableOfColumn(from_sqs, (TableColumn)this);
                  if (ft != null) {
                     addQuotes = true;
                  }
               }
            } else if (this.fromUQS != null) {
               ft = MetadataInfoUtil.getTableOfColumn(this.fromUQS, (TableColumn)this);
               if (ft != null) {
                  addQuotes = true;
               }
            } else if (this.fromDQS != null) {
               ft = MetadataInfoUtil.getTableOfColumn(this.fromDQS, (TableColumn)this);
               if (ft != null) {
                  addQuotes = true;
               }
            }
         }

         if (addQuotes) {
            this.ColumnName = CustomizeUtil.objectNamesToQuotedIdentifier(this.ColumnName, SwisSQLUtils.getKeywords(1), (ModifiedObjectAttr)null, 1);
         }
      }

      if (this.tempTableName != null) {
         if (this.tempTableName.startsWith("#")) {
            this.tempTableName = this.tempTableName.substring(1);
            String[] splitTempTableName = null;
            String splitwhere = "\\.";
            splitTempTableName = this.tempTableName.split(splitwhere);
            if (splitTempTableName.length == 1 && splitTempTableName[0].startsWith("@")) {
               this.tempTableName = "\"" + splitTempTableName[0] + "\"";
            } else if (splitTempTableName.length == 2) {
               this.origTableName = splitTempTableName[splitTempTableName.length - 1];
               this.OwnerName = splitTempTableName[splitTempTableName.length - 2];
               if (this.origTableName.startsWith("@")) {
                  this.tempTableName = "\"" + this.origTableName + "\"";
               }
            }
         }

         tn.setColumnName(this.ColumnName);
         tn.setTempTableColumnName(this.tempTableName);
      }

      if (this.TableName != null && (this.TableName.startsWith("[") && this.TableName.endsWith("]") || this.TableName.startsWith("`") && this.TableName.endsWith("`"))) {
         this.TableName = this.TableName.substring(1, this.TableName.length() - 1);
         if (SwisSQLOptions.retainQuotedIdentifierForOracle || this.TableName.indexOf(32) != -1) {
            this.TableName = "\"" + this.TableName + "\"";
         }
      }

      if (this.ColumnName != null && (this.ColumnName.startsWith("[") && this.ColumnName.endsWith("]") || this.ColumnName.startsWith("`") && this.ColumnName.endsWith("`"))) {
         this.ColumnName = this.ColumnName.substring(1, this.ColumnName.length() - 1);
         if (SwisSQLOptions.retainQuotedIdentifierForOracle || this.ColumnName.indexOf(32) != -1) {
            this.ColumnName = "\"" + this.ColumnName + "\"";
         }
      }

      if (this.OwnerName != null && (this.OwnerName.startsWith("[") && this.OwnerName.endsWith("]") || this.OwnerName.startsWith("`") && this.OwnerName.endsWith("`"))) {
         this.OwnerName = this.OwnerName.substring(1, this.OwnerName.length() - 1);
         if (SwisSQLOptions.retainQuotedIdentifierForOracle || this.OwnerName.indexOf(32) != -1) {
            this.OwnerName = "\"" + this.OwnerName + "\"";
         }
      }

      if (!quotes) {
         if (!this.ColumnName.startsWith("'")) {
            addQuotes = false;
            if (this.ColumnName.startsWith("\"") && this.ColumnName.endsWith("\"")) {
               this.ColumnName = this.ColumnName.substring(1, this.ColumnName.length() - 1);
               addQuotes = true;
            }

            if (this.ColumnName.length() > 30) {
               if (this.ColumnName.startsWith("@")) {
                  this.ColumnName = this.ColumnName.substring(0, 31);
               } else {
                  this.ColumnName = this.ColumnName.substring(0, 30);
               }
            }

            if (addQuotes) {
               this.ColumnName = "\"" + this.ColumnName + "\"";
            }
         }
      } else {
         addQuotes = false;
         if (from_sqs != null) {
            fc = from_sqs.getFromClause();
            if (fc != null) {
               ft = MetadataInfoUtil.getTableOfColumn(from_sqs, (TableColumn)this);
               if (ft != null) {
                  addQuotes = true;
               }
            }
         } else if (this.fromUQS != null) {
            ft = MetadataInfoUtil.getTableOfColumn(this.fromUQS, (TableColumn)this);
            if (ft != null) {
               addQuotes = true;
            }
         } else if (this.fromDQS != null) {
            ft = MetadataInfoUtil.getTableOfColumn(this.fromDQS, (TableColumn)this);
            if (ft != null) {
               addQuotes = true;
            }
         }

         if (addQuotes) {
            boolean addQuotes = false;
            if (this.ColumnName.startsWith("\"") && this.ColumnName.endsWith("\"")) {
               this.ColumnName = this.ColumnName.substring(1, this.ColumnName.length() - 1);
               addQuotes = true;
            }

            if (this.ColumnName.length() > 30) {
               this.ColumnName = this.ColumnName.substring(0, 30);
            }

            if (addQuotes) {
               this.ColumnName = "\"" + this.ColumnName + "\"";
            }
         }
      }

      if (this.OwnerName != null && this.OwnerName.equalsIgnoreCase("DBO") && this.isTenroxRequirement) {
         tn.setOwnerName("PUSER");
      } else if (this.OwnerName != null && this.OwnerName.equalsIgnoreCase("DBO")) {
         tn.setOwnerName((String)null);
      } else if (SwisSQLOptions.removeOracleSchemaQualifier) {
         tn.setOwnerName((String)null);
      } else {
         tn.setOwnerName(this.OwnerName);
      }

      tn.registerOverrideToString(this.override_to_string);
      tn.setTableName(this.TableName);
      String tempColumnName;
      if (this.ColumnName.charAt(0) == '"') {
         if (this.startPosition != null) {
            this.startValue = Integer.parseInt(this.startPosition);
            this.endValue = Integer.parseInt(this.endPosition);
            this.length = this.endValue - this.startValue + 1;
            tempColumnName = this.getColumnName();
            tn.setColumnName("SUBSTR(" + tempColumnName.toUpperCase() + "," + this.startValue + "," + this.length + ")");
            tn.setStartPosition((String)null);
            tn.setEndPosition((String)null);
         } else {
            tn.setColumnName(this.ColumnName);
         }
      } else if (from_sqs.isOracleLive() && !from_sqs.isDenodo() && this.ColumnName.equalsIgnoreCase("DATE_FORMAT")) {
         tn.setColumnName("TO_CHAR");
      } else if (from_sqs.isOracleLive() && this.ColumnName.equalsIgnoreCase("datediff")) {
         tn.setColumnName("");
      } else if (!this.ColumnName.equalsIgnoreCase("DATE") && !this.ColumnName.equalsIgnoreCase("CURRENT_DATE") && !this.ColumnName.equalsIgnoreCase("CURRENT")) {
         if (this.ColumnName.equalsIgnoreCase("TIME") && from_sqs != null && from_sqs.getFromClause() == null || this.ColumnName.equalsIgnoreCase("CURRENT_TIME") && !SwisSQLOptions.fromSybase) {
            tn.setColumnName("TO_CHAR(SYSDATE,'HH:MI:SS')");
         } else if (this.ColumnName.equalsIgnoreCase("CURRENT_TIMESTAMP")) {
            tn.setColumnName("systimestamp");
         } else if (!this.ColumnName.equalsIgnoreCase("SYSTEM_USER") && !this.ColumnName.equalsIgnoreCase("CURRENT_USER") && !this.ColumnName.equalsIgnoreCase("USER") && !this.ColumnName.equalsIgnoreCase("SESSION_USER")) {
            if (this.ColumnName.trim().startsWith("$")) {
               try {
                  tempColumnName = this.ColumnName.substring(1);
                  float numericValue = Float.parseFloat(tempColumnName);
                  tn.setColumnName(tempColumnName);
               } catch (NumberFormatException var9) {
                  tn.setColumnName(this.ColumnName);
               }
            } else if (this.startPosition != null) {
               this.startValue = Integer.parseInt(this.startPosition);
               this.endValue = Integer.parseInt(this.endPosition);
               this.length = this.endValue - this.startValue + 1;
               tempColumnName = this.getColumnName();
               tn.setColumnName("SUBSTR(" + tempColumnName + "," + this.startValue + "," + this.length + ")");
               tn.setStartPosition((String)null);
               tn.setEndPosition((String)null);
            } else if (this.ColumnName.startsWith("@")) {
               tn.setColumnName(":" + this.ColumnName.substring(1));
            } else {
               if (isdenodo && !this.isFunctionName && !GeneralUtil.isItEnclosedTblCol(this.ColumnName)) {
                  this.ColumnName = "\"" + this.ColumnName + "\"";
               }

               tn.setColumnName(this.ColumnName);
            }
         } else {
            tn.setColumnName("USER");
         }
      } else {
         tn.setColumnName("TO_DATE(SYSDATE)");
      }

      tn.setDot(new String("."));
      if (this.origTableName != null) {
         tn.setOrigTableName(this.origTableName);
      }

      return tn;
   }

   public TableColumn toInformixSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) {
      TableColumn tn = new TableColumn();
      tn.setCommentClass(this.commentObj);
      if (this.TableName != null && (this.TableName.startsWith("[") && this.TableName.endsWith("]") || this.TableName.startsWith("`") && this.TableName.endsWith("`"))) {
         this.TableName = this.TableName.substring(1, this.TableName.length() - 1);
         if (this.TableName.indexOf(32) != -1) {
            this.TableName = "\"" + this.TableName + "\"";
         }
      }

      if (this.ColumnName != null && (this.ColumnName.startsWith("[") && this.ColumnName.endsWith("]") || this.ColumnName.startsWith("`") && this.ColumnName.endsWith("`"))) {
         this.ColumnName = this.ColumnName.substring(1, this.ColumnName.length() - 1);
         if (this.ColumnName.indexOf(32) != -1) {
            this.ColumnName = "\"" + this.ColumnName + "\"";
         }
      }

      if (this.OwnerName != null && (this.OwnerName.startsWith("[") && this.OwnerName.endsWith("]") || this.OwnerName.startsWith("`") && this.OwnerName.endsWith("`"))) {
         this.OwnerName = this.OwnerName.substring(1, this.OwnerName.length() - 1);
         if (this.OwnerName.indexOf(32) != -1) {
            this.OwnerName = "\"" + this.OwnerName + "\"";
         }
      }

      tn.setOwnerName(this.OwnerName);
      tn.registerOverrideToString(this.override_to_string);
      tn.setTableName(this.TableName);
      if ((this.ColumnName.equalsIgnoreCase("DATE") || this.ColumnName.equalsIgnoreCase("CURRENT_DATE") || this.ColumnName.equalsIgnoreCase("SYSDATE")) && !this.isFunctionName) {
         tn.setColumnName("CURRENT");
      } else if (!this.ColumnName.equalsIgnoreCase("SYSTEM_USER") && !this.ColumnName.equalsIgnoreCase("CURRENT_USER")) {
         if (this.startPosition != null) {
            tn.setStartPosition(this.startPosition);
            tn.setEndPosition(this.endPosition);
            tn.setColumnName(this.ColumnName);
         } else {
            tn.setColumnName(this.ColumnName);
         }
      } else {
         tn.setColumnName("USER");
      }

      tn.setDot(new String("."));
      return tn;
   }

   public static ArrayList<String> getPgSQLKeyWordList() {
      ArrayList<String> keyWordList = new ArrayList();
      keyWordList.add("do");
      keyWordList.add("as");
      keyWordList.add("union");
      keyWordList.add("table");
      return keyWordList;
   }

   public static ArrayList<String> getPgSQLSpecCharList() {
      ArrayList<String> specCharList = new ArrayList();
      specCharList.add(" ");
      specCharList.add(".");
      specCharList.add("$");
      specCharList.add("#");
      specCharList.add("(");
      specCharList.add(")");
      specCharList.add("!");
      specCharList.add("@");
      specCharList.add("%");
      specCharList.add("^");
      specCharList.add("&");
      specCharList.add("*");
      specCharList.add(";");
      specCharList.add(":");
      specCharList.add("=");
      specCharList.add("<");
      specCharList.add(">");
      specCharList.add("?");
      specCharList.add("~");
      specCharList.add("|");
      specCharList.add("0");
      specCharList.add("1");
      specCharList.add("2");
      specCharList.add("3");
      specCharList.add("4");
      specCharList.add("5");
      specCharList.add("6");
      specCharList.add("7");
      specCharList.add("8");
      specCharList.add("9");
      return specCharList;
   }

   public static boolean isPgTblColNameNeedToEnclosed(String strObj) {
      if (pgSqlKeywordsList.contains(strObj.toLowerCase())) {
         return true;
      } else {
         Iterator var1 = pgSqlSpecCharList.iterator();

         String charVal;
         do {
            if (!var1.hasNext()) {
               return false;
            }

            charVal = (String)var1.next();
         } while(!strObj.contains(charVal));

         return true;
      }
   }

   public static String enclosePgTabColNamesIfNeeded(String str) {
      return isPgTblColNameNeedToEnclosed(str) ? "\"" + str + "\"" : str;
   }

   public TableColumn toBigQuerySelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) {
      TableColumn tn = new TableColumn();
      tn.setCommentClass(this.commentObj);
      if (this.TableName != null && (this.TableName.startsWith("[") && this.TableName.endsWith("]") || this.TableName.startsWith("\"") && this.TableName.endsWith("\"")) && from_sqs != null && from_sqs.getBooleanValues("can.use.back.tip.in.column.name")) {
         this.TableName = this.TableName.substring(1, this.TableName.length() - 1);
         this.TableName = this.TableName.replace("`", "``");
         this.TableName = "`" + this.TableName + "`";
      }

      if (this.ColumnName != null && (this.ColumnName.startsWith("[") && this.ColumnName.endsWith("]") || this.ColumnName.startsWith("\"") && this.ColumnName.endsWith("\"")) && from_sqs != null && from_sqs.getBooleanValues("can.use.back.tip.in.column.name")) {
         this.ColumnName = this.ColumnName.substring(1, this.ColumnName.length() - 1);
         this.ColumnName = "`" + this.ColumnName + "`";
      }

      if (this.OwnerName != null && (this.OwnerName.startsWith("[") && this.OwnerName.endsWith("]") || this.OwnerName.startsWith("\"") && this.OwnerName.endsWith("\"")) && from_sqs != null && from_sqs.getBooleanValues("can.use.back.tip.in.column.name")) {
         this.OwnerName = this.OwnerName.substring(1, this.OwnerName.length() - 1);
         this.OwnerName = "`" + this.OwnerName + "`";
      }

      tn.setOwnerName(this.OwnerName);
      tn.setTableName(this.TableName);
      tn.registerOverrideToString(this.override_to_string);
      String tempColumnName;
      if (this.ColumnName.charAt(0) == '"') {
         if (this.startPosition != null) {
            this.startValue = Integer.parseInt(this.startPosition);
            this.endValue = Integer.parseInt(this.endPosition);
            this.length = this.endValue - this.startValue + 1;
            tempColumnName = this.getColumnName();
            tn.setColumnName("SUBSTR(" + tempColumnName.toUpperCase() + "," + this.startValue + "," + this.length + ")");
            tn.setStartPosition((String)null);
            tn.setEndPosition((String)null);
         }
      } else if ((!this.ColumnName.equalsIgnoreCase("DATE") || from_sqs == null || from_sqs.getFromClause() != null) && !this.ColumnName.equalsIgnoreCase("CURDATE") && !this.ColumnName.equalsIgnoreCase("CURRENT_DATE") && !this.ColumnName.equalsIgnoreCase("SYSDATE")) {
         if (this.ColumnName.equalsIgnoreCase("TIME") && from_sqs != null && from_sqs.getFromClause() == null) {
            tn.setColumnName("CURRENT_TIME");
         } else if (!this.ColumnName.equalsIgnoreCase("CURTIME") && !this.ColumnName.equalsIgnoreCase("CURRENT_TIME") && !this.ColumnName.equalsIgnoreCase("SYSTIME")) {
            if ((this.ColumnName.equalsIgnoreCase("SYSTIMESTAMP") || this.ColumnName.equalsIgnoreCase("CURRENT")) && !this.isFunctionName) {
               tn.setColumnName("CURRENT_TIMESTAMP");
            } else if (!this.ColumnName.equalsIgnoreCase("SYSTEM_USER") && !this.ColumnName.equalsIgnoreCase("USER")) {
               if (this.startPosition != null) {
                  this.startValue = Integer.parseInt(this.startPosition);
                  this.endValue = Integer.parseInt(this.endPosition);
                  this.length = this.endValue - this.startValue + 1;
                  tempColumnName = this.getColumnName();
                  tn.setColumnName("SUBSTR(" + tempColumnName + "," + this.startValue + "," + this.length + ")");
                  tn.setStartPosition((String)null);
                  tn.setEndPosition((String)null);
               } else {
                  tn.setColumnName(this.ColumnName);
               }
            } else {
               tn.setColumnName("CURRENT_USER");
            }
         } else {
            tn.setColumnName("CURRENT_TIME");
         }
      } else {
         tn.setColumnName("CURRENT_DATE");
      }

      tn.setDot(new String("."));
      return tn;
   }

   public TableColumn toAthenaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) {
      TableColumn tn = new TableColumn();
      tn.setCommentClass((CommentClass)null);
      if (this.TableName != null && (this.TableName.startsWith("[") && this.TableName.endsWith("]") || this.TableName.startsWith("`") && this.TableName.endsWith("`"))) {
         this.TableName = this.TableName.substring(1, this.TableName.length() - 1);
         if (this.TableName.indexOf(32) != -1 || this.TableName.contains(".")) {
            this.TableName = "\"" + this.TableName + "\"";
         }
      }

      if (this.ColumnName != null && (this.ColumnName.startsWith("[") && this.ColumnName.endsWith("]") || this.ColumnName.startsWith("`") && this.ColumnName.endsWith("`"))) {
         this.ColumnName = this.ColumnName.substring(1, this.ColumnName.length() - 1);
         if (this.ColumnName.indexOf(32) != -1 || this.ColumnName.contains(".")) {
            this.ColumnName = "\"" + this.ColumnName + "\"";
         }
      }

      if (this.OwnerName != null && (this.OwnerName.startsWith("[") && this.OwnerName.endsWith("]") || this.OwnerName.startsWith("`") && this.OwnerName.endsWith("`"))) {
         this.OwnerName = this.OwnerName.substring(1, this.OwnerName.length() - 1);
         if (this.OwnerName.indexOf(32) != -1 || this.OwnerName.contains(".")) {
            this.OwnerName = "\"" + this.OwnerName + "\"";
         }
      }

      if (this.OwnerName != null) {
         this.OwnerName = CustomizeUtil.objectNamesToQuotedIdentifier(this.OwnerName, SwisSQLUtils.getKeywords(16), (ModifiedObjectAttr)null, 16);
      }

      if (this.ColumnName != null) {
         this.ColumnName = CustomizeUtil.objectNamesToQuotedIdentifier(this.ColumnName, SwisSQLUtils.getKeywords(16), (ModifiedObjectAttr)null, 16);
      }

      if (this.TableName != null) {
         this.TableName = CustomizeUtil.objectNamesToQuotedIdentifier(this.TableName, SwisSQLUtils.getKeywords(16), (ModifiedObjectAttr)null, 16);
      }

      tn.setOwnerName(this.OwnerName);
      tn.setTableName(this.TableName);
      if (this.TableName != null) {
         tn.setTableName(SelectStatement.changeBackTip(this.TableName));
      }

      tn.registerOverrideToString(this.override_to_string);
      String tempColumnName;
      if (this.ColumnName.charAt(0) == '"') {
         if (this.startPosition != null) {
            this.startValue = Integer.parseInt(this.startPosition);
            this.endValue = Integer.parseInt(this.endPosition);
            this.length = this.endValue - this.startValue + 1;
            tempColumnName = this.getColumnName();
            tn.setColumnName("SUBSTR(" + tempColumnName.toUpperCase() + "," + this.startValue + "," + this.length + ")");
            tn.setStartPosition((String)null);
            tn.setEndPosition((String)null);
         } else if (this.ColumnName != null) {
            tn.setColumnName(SelectStatement.changeBackTip(this.ColumnName));
         }
      } else if ((!this.ColumnName.equalsIgnoreCase("DATE") || from_sqs == null || from_sqs.getFromClause() != null) && !this.ColumnName.equalsIgnoreCase("CURDATE") && !this.ColumnName.equalsIgnoreCase("CURRENT_DATE") && !this.ColumnName.equalsIgnoreCase("SYSDATE")) {
         if (this.ColumnName.equalsIgnoreCase("TIME") && from_sqs != null && from_sqs.getFromClause() == null) {
            tn.setColumnName("CURRENT_TIME");
         } else if (!this.ColumnName.equalsIgnoreCase("CURTIME") && !this.ColumnName.equalsIgnoreCase("CURRENT_TIME") && !this.ColumnName.equalsIgnoreCase("SYSTIME")) {
            if ((this.ColumnName.equalsIgnoreCase("SYSTIMESTAMP") || this.ColumnName.equalsIgnoreCase("CURRENT")) && !this.isFunctionName) {
               tn.setColumnName("CURRENT_TIMESTAMP");
            } else if (!this.ColumnName.equalsIgnoreCase("SYSTEM_USER") && !this.ColumnName.equalsIgnoreCase("USER")) {
               if (this.startPosition != null) {
                  this.startValue = Integer.parseInt(this.startPosition);
                  this.endValue = Integer.parseInt(this.endPosition);
                  this.length = this.endValue - this.startValue + 1;
                  tempColumnName = this.getColumnName();
                  tn.setColumnName("SUBSTR(" + tempColumnName + "," + this.startValue + "," + this.length + ")");
                  tn.setStartPosition((String)null);
                  tn.setEndPosition((String)null);
               } else {
                  tn.setColumnName(this.ColumnName);
                  if (this.ColumnName != null) {
                     tn.setColumnName(SelectStatement.changeBackTip(this.ColumnName));
                  }
               }
            } else {
               tn.setColumnName("CURRENT_USER");
            }
         } else {
            tn.setColumnName("CURRENT_TIME");
         }
      } else {
         tn.setColumnName("CURRENT_DATE");
      }

      tn.setDot(new String("."));
      return tn;
   }

   public TableColumn toSapHanaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) {
      TableColumn tn = new TableColumn();
      tn.setCommentClass((CommentClass)null);
      if (this.TableName != null && (this.TableName.startsWith("[") && this.TableName.endsWith("]") || this.TableName.startsWith("`") && this.TableName.endsWith("`"))) {
         this.TableName = this.TableName.substring(1, this.TableName.length() - 1);
         if (this.TableName.indexOf(32) != -1 || this.TableName.contains(".")) {
            this.TableName = "\"" + this.TableName + "\"";
         }
      }

      if (this.ColumnName != null && (this.ColumnName.startsWith("[") && this.ColumnName.endsWith("]") || this.ColumnName.startsWith("`") && this.ColumnName.endsWith("`"))) {
         this.ColumnName = this.ColumnName.substring(1, this.ColumnName.length() - 1);
         if (this.ColumnName.indexOf(32) != -1 || this.ColumnName.contains(".")) {
            this.ColumnName = "\"" + this.ColumnName + "\"";
         }
      }

      if (this.OwnerName != null && (this.OwnerName.startsWith("[") && this.OwnerName.endsWith("]") || this.OwnerName.startsWith("`") && this.OwnerName.endsWith("`"))) {
         this.OwnerName = this.OwnerName.substring(1, this.OwnerName.length() - 1);
         if (this.OwnerName.indexOf(32) != -1 || this.OwnerName.contains(".")) {
            this.OwnerName = "\"" + this.OwnerName + "\"";
         }
      }

      if (this.OwnerName != null) {
         this.OwnerName = CustomizeUtil.objectNamesToQuotedIdentifier(this.OwnerName, SwisSQLUtils.getKeywords(17), (ModifiedObjectAttr)null, 17);
      }

      if (this.ColumnName != null) {
         this.ColumnName = CustomizeUtil.objectNamesToQuotedIdentifier(this.ColumnName, SwisSQLUtils.getKeywords(17), (ModifiedObjectAttr)null, 17);
      }

      if (this.TableName != null) {
         this.TableName = CustomizeUtil.objectNamesToQuotedIdentifier(this.TableName, SwisSQLUtils.getKeywords(17), (ModifiedObjectAttr)null, 17);
      }

      tn.setOwnerName(this.OwnerName);
      tn.setTableName(this.TableName);
      if (this.TableName != null) {
         tn.setTableName(SelectStatement.changeBackTip(this.TableName));
      }

      tn.registerOverrideToString(this.override_to_string);
      String tempColumnName;
      if (this.ColumnName.charAt(0) == '"') {
         if (this.startPosition != null) {
            this.startValue = Integer.parseInt(this.startPosition);
            this.endValue = Integer.parseInt(this.endPosition);
            this.length = this.endValue - this.startValue + 1;
            tempColumnName = this.getColumnName();
            tn.setColumnName("SUBSTR(" + tempColumnName.toUpperCase() + "," + this.startValue + "," + this.length + ")");
            tn.setStartPosition((String)null);
            tn.setEndPosition((String)null);
         } else if (this.ColumnName != null) {
            tn.setColumnName(SelectStatement.changeBackTip(this.ColumnName));
         }
      } else if ((!this.ColumnName.equalsIgnoreCase("DATE") || from_sqs == null || from_sqs.getFromClause() != null) && !this.ColumnName.equalsIgnoreCase("CURDATE") && !this.ColumnName.equalsIgnoreCase("CURRENT_DATE") && !this.ColumnName.equalsIgnoreCase("SYSDATE")) {
         if (this.ColumnName.equalsIgnoreCase("TIME") && from_sqs != null && from_sqs.getFromClause() == null) {
            tn.setColumnName("CURRENT_TIME");
         } else if (!this.ColumnName.equalsIgnoreCase("CURTIME") && !this.ColumnName.equalsIgnoreCase("CURRENT_TIME") && !this.ColumnName.equalsIgnoreCase("SYSTIME")) {
            if ((this.ColumnName.equalsIgnoreCase("SYSTIMESTAMP") || this.ColumnName.equalsIgnoreCase("CURRENT")) && !this.isFunctionName) {
               tn.setColumnName("CURRENT_TIMESTAMP");
            } else if (!this.ColumnName.equalsIgnoreCase("SYSTEM_USER") && !this.ColumnName.equalsIgnoreCase("USER")) {
               if (this.startPosition != null) {
                  this.startValue = Integer.parseInt(this.startPosition);
                  this.endValue = Integer.parseInt(this.endPosition);
                  this.length = this.endValue - this.startValue + 1;
                  tempColumnName = this.getColumnName();
                  tn.setColumnName("SUBSTR(" + tempColumnName + "," + this.startValue + "," + this.length + ")");
                  tn.setStartPosition((String)null);
                  tn.setEndPosition((String)null);
               } else {
                  tn.setColumnName(this.ColumnName);
                  if (this.ColumnName != null) {
                     tn.setColumnName(SelectStatement.changeBackTip(this.ColumnName));
                  }
               }
            } else {
               tn.setColumnName("CURRENT_USER");
            }
         } else {
            tn.setColumnName("CURRENT_TIME");
         }
      } else {
         tn.setColumnName("CURRENT_DATE");
      }

      tn.setDot(new String("."));
      return tn;
   }

   public TableColumn toSqliteSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) {
      TableColumn tn = new TableColumn();
      tn.setCommentClass((CommentClass)null);
      if (this.TableName != null && (this.TableName.startsWith("[") && this.TableName.endsWith("]") || this.TableName.startsWith("`") && this.TableName.endsWith("`"))) {
         this.TableName = this.TableName.substring(1, this.TableName.length() - 1);
         if (this.TableName.indexOf(32) != -1 || this.TableName.contains(".")) {
            this.TableName = "\"" + this.TableName + "\"";
         }
      }

      if (this.ColumnName != null && (this.ColumnName.startsWith("[") && this.ColumnName.endsWith("]") || this.ColumnName.startsWith("`") && this.ColumnName.endsWith("`"))) {
         this.ColumnName = this.ColumnName.substring(1, this.ColumnName.length() - 1);
         if (this.ColumnName.indexOf(32) != -1 || this.ColumnName.contains(".")) {
            this.ColumnName = "\"" + this.ColumnName + "\"";
         }
      }

      if (this.OwnerName != null && (this.OwnerName.startsWith("[") && this.OwnerName.endsWith("]") || this.OwnerName.startsWith("`") && this.OwnerName.endsWith("`"))) {
         this.OwnerName = this.OwnerName.substring(1, this.OwnerName.length() - 1);
         if (this.OwnerName.indexOf(32) != -1 || this.OwnerName.contains(".")) {
            this.OwnerName = "\"" + this.OwnerName + "\"";
         }
      }

      if (this.OwnerName != null) {
         this.OwnerName = CustomizeUtil.objectNamesToQuotedIdentifier(this.OwnerName, SwisSQLUtils.getKeywords(18), (ModifiedObjectAttr)null, 18);
      }

      if (this.ColumnName != null) {
         this.ColumnName = CustomizeUtil.objectNamesToQuotedIdentifier(this.ColumnName, SwisSQLUtils.getKeywords(18), (ModifiedObjectAttr)null, 18);
      }

      if (this.TableName != null) {
         this.TableName = CustomizeUtil.objectNamesToQuotedIdentifier(this.TableName, SwisSQLUtils.getKeywords(18), (ModifiedObjectAttr)null, 18);
      }

      tn.setOwnerName(this.OwnerName);
      tn.setTableName(this.TableName);
      if (this.TableName != null) {
         tn.setTableName(SelectStatement.changeBackTip(this.TableName));
      }

      tn.registerOverrideToString(this.override_to_string);
      String tempColumnName;
      if (this.ColumnName.charAt(0) == '"') {
         if (this.startPosition != null) {
            this.startValue = Integer.parseInt(this.startPosition);
            this.endValue = Integer.parseInt(this.endPosition);
            this.length = this.endValue - this.startValue + 1;
            tempColumnName = this.getColumnName();
            tn.setColumnName("SUBSTR(" + tempColumnName.toUpperCase() + "," + this.startValue + "," + this.length + ")");
            tn.setStartPosition((String)null);
            tn.setEndPosition((String)null);
         } else if (this.ColumnName != null) {
            tn.setColumnName(SelectStatement.changeBackTip(this.ColumnName));
         }
      } else if ((!this.ColumnName.equalsIgnoreCase("DATE") || from_sqs == null || from_sqs.getFromClause() != null) && !this.ColumnName.equalsIgnoreCase("CURDATE") && !this.ColumnName.equalsIgnoreCase("CURRENT_DATE") && !this.ColumnName.equalsIgnoreCase("SYSDATE")) {
         if (this.ColumnName.equalsIgnoreCase("TIME") && from_sqs != null && from_sqs.getFromClause() == null) {
            tn.setColumnName("TIME()");
         } else if (!this.ColumnName.equalsIgnoreCase("CURTIME") && !this.ColumnName.equalsIgnoreCase("CURRENT_TIME") && !this.ColumnName.equalsIgnoreCase("SYSTIME")) {
            if ((this.ColumnName.equalsIgnoreCase("SYSTIMESTAMP") || this.ColumnName.equalsIgnoreCase("CURRENT")) && !this.isFunctionName) {
               tn.setColumnName("DATETIME()");
            } else if (this.startPosition != null) {
               this.startValue = Integer.parseInt(this.startPosition);
               this.endValue = Integer.parseInt(this.endPosition);
               this.length = this.endValue - this.startValue + 1;
               tempColumnName = this.getColumnName();
               tn.setColumnName("SUBSTR(" + tempColumnName + "," + this.startValue + "," + this.length + ")");
               tn.setStartPosition((String)null);
               tn.setEndPosition((String)null);
            } else {
               tn.setColumnName(this.ColumnName);
               if (this.ColumnName != null) {
                  tn.setColumnName(SelectStatement.changeBackTip(this.ColumnName));
               }
            }
         } else {
            tn.setColumnName("TIME()");
         }
      } else {
         tn.setColumnName("DATE()");
      }

      tn.setDot(new String("."));
      return tn;
   }

   public TableColumn toExcelSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) {
      TableColumn tn = new TableColumn();
      tn.setCommentClass((CommentClass)null);
      if (this.TableName != null && (this.TableName.startsWith("\"") && this.TableName.endsWith("\"") || this.TableName.startsWith("`") && this.TableName.endsWith("`"))) {
         this.TableName = this.TableName.substring(1, this.TableName.length() - 1);
         if (this.TableName.indexOf(32) != -1 || this.TableName.contains(".")) {
            this.TableName = "[" + this.TableName + "]";
         }
      }

      if (this.ColumnName != null && (this.ColumnName.startsWith("\"") && this.ColumnName.endsWith("\"") || this.ColumnName.startsWith("`") && this.ColumnName.endsWith("`"))) {
         this.ColumnName = this.ColumnName.substring(1, this.ColumnName.length() - 1);
         this.ColumnName = "[" + this.ColumnName + "]";
      }

      if (this.OwnerName != null && (this.OwnerName.startsWith("\"") && this.OwnerName.endsWith("\"") || this.OwnerName.startsWith("`") && this.OwnerName.endsWith("`"))) {
         this.OwnerName = this.OwnerName.substring(1, this.OwnerName.length() - 1);
         if (this.OwnerName.indexOf(32) != -1 || this.OwnerName.contains(".")) {
            this.OwnerName = "[" + this.OwnerName + "]";
         }
      }

      if (this.ColumnName != null) {
         this.ColumnName = CustomizeUtil.objectNamesToQuotedIdentifier(this.ColumnName, SwisSQLUtils.getKeywords(20), (ModifiedObjectAttr)null, 20);
      }

      if (this.OwnerName != null) {
         this.OwnerName = CustomizeUtil.objectNamesToQuotedIdentifier(this.OwnerName, SwisSQLUtils.getKeywords(20), (ModifiedObjectAttr)null, 20);
         tn.setOwnerName(this.OwnerName.startsWith("[") && this.OwnerName.endsWith("]") ? this.OwnerName : "[" + this.OwnerName + "]");
      } else {
         tn.setColumnName(this.OwnerName);
      }

      if (this.TableName != null) {
         this.TableName = CustomizeUtil.objectNamesToQuotedIdentifier(this.TableName, SwisSQLUtils.getKeywords(20), (ModifiedObjectAttr)null, 20);
         if (this.TableName.startsWith("\"") && this.TableName.endsWith("\"") || this.TableName.startsWith("`") && this.TableName.endsWith("`")) {
            this.TableName = this.TableName.substring(1, this.TableName.length() - 1);
            this.TableName = "[" + this.TableName + "]";
         }

         tn.setTableName(this.TableName.startsWith("[") && this.TableName.endsWith("]") ? this.TableName : "[" + this.TableName + "]");
      } else {
         tn.setColumnName(this.TableName);
      }

      tn.registerOverrideToString(this.override_to_string);
      String tempColumnName;
      if (this.ColumnName.charAt(0) == '"') {
         if (this.startPosition != null) {
            this.startValue = Integer.parseInt(this.startPosition);
            this.endValue = Integer.parseInt(this.endPosition);
            this.length = this.endValue - this.startValue + 1;
            tempColumnName = this.getColumnName();
            tn.setColumnName("MID(" + tempColumnName.toUpperCase() + "," + this.startValue + "," + this.length + ")");
            tn.setStartPosition((String)null);
            tn.setEndPosition((String)null);
         } else {
            tn.setColumnName(this.ColumnName);
         }
      } else if ((this.ColumnName.equalsIgnoreCase("DATE") && from_sqs != null && from_sqs.getFromClause() == null || this.ColumnName.equalsIgnoreCase("CURDATE") || this.ColumnName.equalsIgnoreCase("CURRENT_DATE") || this.ColumnName.equalsIgnoreCase("SYSDATE")) && !this.isFunctionName) {
         tn.setColumnName("DATE()");
      } else if ((this.ColumnName.equalsIgnoreCase("CURTIME") || this.ColumnName.equalsIgnoreCase("CURRENT_TIME") || this.ColumnName.equalsIgnoreCase("SYSTIME")) && !this.isFunctionName) {
         tn.setColumnName("TIME()");
      } else if ((this.ColumnName.equalsIgnoreCase("SYSTIMESTAMP") || this.ColumnName.equalsIgnoreCase("CURRENT")) && !this.isFunctionName) {
         tn.setColumnName("NOW()");
      } else if (this.startPosition != null) {
         this.startValue = Integer.parseInt(this.startPosition);
         this.endValue = Integer.parseInt(this.endPosition);
         this.length = this.endValue - this.startValue + 1;
         tempColumnName = this.getColumnName();
         tn.setColumnName("MID(" + tempColumnName + "," + this.startValue + "," + this.length + ")");
         tn.setStartPosition((String)null);
         tn.setEndPosition((String)null);
      } else {
         tn.setColumnName(this.ColumnName);
      }

      tn.setDot(new String("."));
      return tn;
   }

   public TableColumn toMsAccessJdbcSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) {
      TableColumn tn = new TableColumn();
      tn.setCommentClass((CommentClass)null);
      if (this.TableName != null && (this.TableName.startsWith("\"") && this.TableName.endsWith("\"") || this.TableName.startsWith("`") && this.TableName.endsWith("`"))) {
         this.TableName = this.TableName.substring(1, this.TableName.length() - 1);
         if (this.TableName.indexOf(32) != -1 || this.TableName.contains(".")) {
            this.TableName = "[" + this.TableName + "]";
         }
      }

      if (this.ColumnName != null && (this.ColumnName.startsWith("\"") && this.ColumnName.endsWith("\"") || this.ColumnName.startsWith("`") && this.ColumnName.endsWith("`"))) {
         this.ColumnName = this.ColumnName.substring(1, this.ColumnName.length() - 1);
         this.ColumnName = "[" + this.ColumnName + "]";
      }

      if (this.OwnerName != null && (this.OwnerName.startsWith("\"") && this.OwnerName.endsWith("\"") || this.OwnerName.startsWith("`") && this.OwnerName.endsWith("`"))) {
         this.OwnerName = this.OwnerName.substring(1, this.OwnerName.length() - 1);
         if (this.OwnerName.indexOf(32) != -1 || this.OwnerName.contains(".")) {
            this.OwnerName = "[" + this.OwnerName + "]";
         }
      }

      if (this.ColumnName != null) {
         this.ColumnName = CustomizeUtil.objectNamesToQuotedIdentifier(this.ColumnName, SwisSQLUtils.getKeywords(21), (ModifiedObjectAttr)null, 21);
      }

      if (this.OwnerName != null) {
         this.OwnerName = CustomizeUtil.objectNamesToQuotedIdentifier(this.OwnerName, SwisSQLUtils.getKeywords(21), (ModifiedObjectAttr)null, 21);
         tn.setOwnerName(this.OwnerName.startsWith("[") && this.OwnerName.endsWith("]") ? this.OwnerName : "[" + this.OwnerName + "]");
      } else {
         tn.setColumnName(this.OwnerName);
      }

      if (this.TableName != null) {
         this.TableName = CustomizeUtil.objectNamesToQuotedIdentifier(this.TableName, SwisSQLUtils.getKeywords(21), (ModifiedObjectAttr)null, 21);
         if (this.TableName.startsWith("\"") && this.TableName.endsWith("\"") || this.TableName.startsWith("`") && this.TableName.endsWith("`")) {
            this.TableName = this.TableName.substring(1, this.TableName.length() - 1);
            this.TableName = "[" + this.TableName + "]";
         }

         tn.setTableName(this.TableName.startsWith("[") && this.TableName.endsWith("]") ? this.TableName : "[" + this.TableName + "]");
      } else {
         tn.setColumnName(this.TableName);
      }

      tn.registerOverrideToString(this.override_to_string);
      String tempColumnName;
      if (this.ColumnName.charAt(0) == '"') {
         if (this.startPosition != null) {
            this.startValue = Integer.parseInt(this.startPosition);
            this.endValue = Integer.parseInt(this.endPosition);
            this.length = this.endValue - this.startValue + 1;
            tempColumnName = this.getColumnName();
            tn.setColumnName("SUBSTR(" + tempColumnName.toUpperCase() + "," + this.startValue + "," + this.length + ")");
            tn.setStartPosition((String)null);
            tn.setEndPosition((String)null);
         } else {
            tn.setColumnName(this.ColumnName);
         }
      } else if ((this.ColumnName.equalsIgnoreCase("DATE") && from_sqs != null && from_sqs.getFromClause() == null || this.ColumnName.equalsIgnoreCase("CURDATE") || this.ColumnName.equalsIgnoreCase("CURRENT_DATE") || this.ColumnName.equalsIgnoreCase("SYSDATE")) && !this.isFunctionName) {
         tn.setColumnName("DATE()");
      } else if ((this.ColumnName.equalsIgnoreCase("CURTIME") || this.ColumnName.equalsIgnoreCase("CURRENT_TIME") || this.ColumnName.equalsIgnoreCase("SYSTIME")) && !this.isFunctionName) {
         tn.setColumnName("TIME()");
      } else if ((this.ColumnName.equalsIgnoreCase("SYSTIMESTAMP") || this.ColumnName.equalsIgnoreCase("CURRENT")) && !this.isFunctionName) {
         tn.setColumnName("NOW()");
      } else if (this.startPosition != null) {
         this.startValue = Integer.parseInt(this.startPosition);
         this.endValue = Integer.parseInt(this.endPosition);
         this.length = this.endValue - this.startValue + 1;
         tempColumnName = this.getColumnName();
         tn.setColumnName("SUBSTR(" + tempColumnName + "," + this.startValue + "," + this.length + ")");
         tn.setStartPosition((String)null);
         tn.setEndPosition((String)null);
      } else {
         tn.setColumnName(this.ColumnName);
      }

      tn.setDot(new String("."));
      return tn;
   }

   public TableColumn toPostgreSQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) {
      TableColumn tn = new TableColumn();
      tn.setCommentClass((CommentClass)null);
      boolean isPostgreLiveDbs = from_sqs != null && (from_sqs.isAmazonRedShift() || from_sqs.isPgsqlLive() || from_sqs.isVerticaDb());
      if (this.TableName != null && (this.TableName.startsWith("[") && this.TableName.endsWith("]") || this.TableName.startsWith("`") && this.TableName.endsWith("`"))) {
         this.TableName = this.TableName.substring(1, this.TableName.length() - 1);
         if (from_sqs != null && from_sqs.getReportsMeta()) {
            this.TableName = "\"" + this.TableName + "\"";
         } else {
            this.TableName = enclosePgTabColNamesIfNeeded(this.TableName);
         }
      }

      if (this.ColumnName != null && (this.ColumnName.startsWith("[") && this.ColumnName.endsWith("]") || this.ColumnName.startsWith("`") && this.ColumnName.endsWith("`"))) {
         this.ColumnName = this.ColumnName.substring(1, this.ColumnName.length() - 1);
         if (from_sqs != null && from_sqs.getReportsMeta()) {
            this.ColumnName = "\"" + this.ColumnName + "\"";
         } else {
            this.ColumnName = enclosePgTabColNamesIfNeeded(this.ColumnName);
         }
      }

      if (this.OwnerName != null && (this.OwnerName.startsWith("[") && this.OwnerName.endsWith("]") || this.OwnerName.startsWith("`") && this.OwnerName.endsWith("`"))) {
         this.OwnerName = this.OwnerName.substring(1, this.OwnerName.length() - 1);
         if (from_sqs != null && from_sqs.getReportsMeta()) {
            this.OwnerName = "\"" + this.OwnerName + "\"";
         } else {
            this.OwnerName = enclosePgTabColNamesIfNeeded(this.OwnerName);
         }
      }

      if (this.OwnerName != null) {
         this.OwnerName = CustomizeUtil.objectNamesToQuotedIdentifier(this.OwnerName, SwisSQLUtils.getKeywords(4), (ModifiedObjectAttr)null, 4);
      }

      if (this.ColumnName != null) {
         this.ColumnName = CustomizeUtil.objectNamesToQuotedIdentifier(this.ColumnName, SwisSQLUtils.getKeywords(4), (ModifiedObjectAttr)null, 4);
      }

      if (this.TableName != null) {
         this.TableName = CustomizeUtil.objectNamesToQuotedIdentifier(this.TableName, SwisSQLUtils.getKeywords(4), (ModifiedObjectAttr)null, 4);
      }

      tn.setOwnerName(isPostgreLiveDbs ? this.OwnerName : null);
      tn.setTableName(this.TableName);
      if (this.TableName != null) {
         tn.setTableName(SelectStatement.checkandRemoveDoubleQuoteForPostgresIdentifier(SelectStatement.changeBackTip(this.TableName), from_sqs == null ? false : from_sqs.getReportsMeta()));
      }

      tn.registerOverrideToString(this.override_to_string);
      String tempColumnName;
      if (this.ColumnName.charAt(0) == '"') {
         if (this.startPosition != null) {
            this.startValue = Integer.parseInt(this.startPosition);
            this.endValue = Integer.parseInt(this.endPosition);
            this.length = this.endValue - this.startValue + 1;
            tempColumnName = this.getColumnName();
            tn.setColumnName("SUBSTR(" + tempColumnName.toUpperCase() + "," + this.startValue + "," + this.length + ")");
            tn.setStartPosition((String)null);
            tn.setEndPosition((String)null);
         } else if (this.ColumnName != null) {
            tn.setColumnName(SelectStatement.checkandRemoveDoubleQuoteForPostgresIdentifier(SelectStatement.changeBackTip(this.ColumnName), from_sqs == null ? false : from_sqs.getReportsMeta()));
         }
      } else if ((!this.ColumnName.equalsIgnoreCase("DATE") || from_sqs == null || from_sqs.getFromClause() != null) && !this.ColumnName.equalsIgnoreCase("CURDATE") && !this.ColumnName.equalsIgnoreCase("CURRENT_DATE") && !this.ColumnName.equalsIgnoreCase("SYSDATE")) {
         if (this.ColumnName.equalsIgnoreCase("TIME") && from_sqs != null && from_sqs.getFromClause() == null) {
            tn.setColumnName("CURRENT_TIME");
         } else if (!this.ColumnName.equalsIgnoreCase("CURTIME") && !this.ColumnName.equalsIgnoreCase("CURRENT_TIME") && !this.ColumnName.equalsIgnoreCase("SYSTIME")) {
            if ((this.ColumnName.equalsIgnoreCase("SYSTIMESTAMP") || this.ColumnName.equalsIgnoreCase("CURRENT")) && !this.isFunctionName) {
               tn.setColumnName("CURRENT_TIMESTAMP");
            } else if (!this.ColumnName.equalsIgnoreCase("SYSTEM_USER") && !this.ColumnName.equalsIgnoreCase("USER")) {
               if (this.startPosition != null) {
                  this.startValue = Integer.parseInt(this.startPosition);
                  this.endValue = Integer.parseInt(this.endPosition);
                  this.length = this.endValue - this.startValue + 1;
                  tempColumnName = this.getColumnName();
                  tn.setColumnName("SUBSTR(" + tempColumnName + "," + this.startValue + "," + this.length + ")");
                  tn.setStartPosition((String)null);
                  tn.setEndPosition((String)null);
               } else {
                  tn.setColumnName(this.ColumnName);
                  if (this.ColumnName != null) {
                     tn.setColumnName(SelectStatement.checkandRemoveDoubleQuoteForPostgresIdentifier(SelectStatement.changeBackTip(this.ColumnName), from_sqs == null ? false : from_sqs.getReportsMeta()));
                  }
               }
            } else {
               tn.setColumnName("CURRENT_USER");
            }
         } else {
            tn.setColumnName("CURRENT_TIME");
         }
      } else {
         tn.setColumnName("CURRENT_DATE");
      }

      tn.setDot(new String("."));
      return tn;
   }

   public TableColumn toMySQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) {
      TableColumn tn = new TableColumn();
      tn.setCommentClass(this.commentObj);
      if (this.TableName != null && (this.TableName.startsWith("[") && this.TableName.endsWith("]") || this.TableName.startsWith("\"") && this.TableName.endsWith("\"")) && from_sqs != null && from_sqs.getBooleanValues("can.use.back.tip.in.column.name")) {
         this.TableName = this.TableName.substring(1, this.TableName.length() - 1);
         this.TableName = "`" + this.TableName + "`";
      }

      if (this.ColumnName != null && (this.ColumnName.startsWith("[") && this.ColumnName.endsWith("]") || this.ColumnName.startsWith("\"") && this.ColumnName.endsWith("\"")) && from_sqs != null && from_sqs.getBooleanValues("can.use.back.tip.in.column.name")) {
         this.ColumnName = this.ColumnName.substring(1, this.ColumnName.length() - 1);
         this.ColumnName = "`" + this.ColumnName + "`";
      }

      if (this.OwnerName != null && (this.OwnerName.startsWith("[") && this.OwnerName.endsWith("]") || this.OwnerName.startsWith("\"") && this.OwnerName.endsWith("\"")) && from_sqs != null && from_sqs.getBooleanValues("can.use.back.tip.in.column.name")) {
         this.OwnerName = this.OwnerName.substring(1, this.OwnerName.length() - 1);
         this.OwnerName = "`" + this.OwnerName + "`";
      }

      tn.setOwnerName(this.OwnerName);
      tn.setTableName(this.TableName);
      tn.registerOverrideToString(this.override_to_string);
      String tempColumnName;
      if (from_sqs != null && !from_sqs.isHyperSql() && this.ColumnName.charAt(0) == '"') {
         if (this.startPosition != null) {
            this.startValue = Integer.parseInt(this.startPosition);
            this.endValue = Integer.parseInt(this.endPosition);
            this.length = this.endValue - this.startValue + 1;
            tempColumnName = this.getColumnName();
            tn.setColumnName("SUBSTRING(" + tempColumnName.toUpperCase() + "," + this.startValue + "," + this.length + ")");
            tn.setStartPosition((String)null);
            tn.setEndPosition((String)null);
         } else {
            tn.setColumnName(this.ColumnName.replace('"', ' ').trim());
         }
      } else if (from_sqs.getBooleanValues("convert.keywords.to.relative.form") && this.ColumnName.equalsIgnoreCase("DATE")) {
         tn.setColumnName("CURRENT_DATE");
      } else if (from_sqs.getBooleanValues("convert.keywords.to.relative.form") && this.ColumnName.equalsIgnoreCase("TIME") && from_sqs != null && from_sqs.getFromClause() == null) {
         tn.setColumnName("CURRENT_TIME");
      } else if (from_sqs.getBooleanValues("convert.keywords.to.relative.form") && (this.ColumnName.equalsIgnoreCase("TIMESTAMP") || this.ColumnName.equalsIgnoreCase("SYSTIMESTAMP") || this.ColumnName.equalsIgnoreCase("SYSDATE") || this.ColumnName.equalsIgnoreCase("CURRENT")) && !this.isFunctionName) {
         tn.setColumnName("CURRENT_TIMESTAMP");
      } else if (!from_sqs.getBooleanValues("convert.keywords.to.relative.form") || !this.ColumnName.equalsIgnoreCase("SYSTEM_USER") && !this.ColumnName.equalsIgnoreCase("USER") && !this.ColumnName.equalsIgnoreCase("CURRENT_USER")) {
         if (this.startPosition != null) {
            this.startValue = Integer.parseInt(this.startPosition);
            this.endValue = Integer.parseInt(this.endPosition);
            this.length = this.endValue - this.startValue + 1;
            tempColumnName = this.getColumnName();
            tn.setColumnName("SUBSTRING(" + tempColumnName + "," + this.startValue + "," + this.length + ")");
            tn.setStartPosition((String)null);
            tn.setEndPosition((String)null);
         } else {
            tn.setColumnName(this.ColumnName);
         }
      } else {
         tn.setColumnName("USER()");
      }

      tn.setDot(new String("."));
      tn.setIsPivotColumn(this.isPivotColumn);
      return tn;
   }

   public TableColumn toSnowflakeSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) {
      TableColumn tn = new TableColumn();
      tn.setCommentClass((CommentClass)null);
      if (this.TableName != null && (this.TableName.startsWith("[") && this.TableName.endsWith("]") || this.TableName.startsWith("`") && this.TableName.endsWith("`"))) {
         this.TableName = this.TableName.substring(1, this.TableName.length() - 1);
         if (this.TableName.indexOf(32) != -1 || this.TableName.contains(".")) {
            this.TableName = "\"" + this.TableName + "\"";
         }
      }

      if (this.ColumnName != null && (this.ColumnName.startsWith("[") && this.ColumnName.endsWith("]") || this.ColumnName.startsWith("`") && this.ColumnName.endsWith("`"))) {
         this.ColumnName = this.ColumnName.substring(1, this.ColumnName.length() - 1);
         if (this.ColumnName.indexOf(32) != -1 || this.ColumnName.contains(".")) {
            this.ColumnName = "\"" + this.ColumnName + "\"";
         }
      }

      if (this.OwnerName != null && (this.OwnerName.startsWith("[") && this.OwnerName.endsWith("]") || this.OwnerName.startsWith("`") && this.OwnerName.endsWith("`"))) {
         this.OwnerName = this.OwnerName.substring(1, this.OwnerName.length() - 1);
         if (this.OwnerName.indexOf(32) != -1 || this.OwnerName.contains(".")) {
            this.OwnerName = "\"" + this.OwnerName + "\"";
         }
      }

      if (this.OwnerName != null) {
         this.OwnerName = CustomizeUtil.objectNamesToQuotedIdentifier(this.OwnerName, SwisSQLUtils.getKeywords(15), (ModifiedObjectAttr)null, 15);
      }

      if (this.ColumnName != null) {
         this.ColumnName = CustomizeUtil.objectNamesToQuotedIdentifier(this.ColumnName, SwisSQLUtils.getKeywords(15), (ModifiedObjectAttr)null, 15);
      }

      if (this.TableName != null) {
         this.TableName = CustomizeUtil.objectNamesToQuotedIdentifier(this.TableName, SwisSQLUtils.getKeywords(15), (ModifiedObjectAttr)null, 15);
      }

      tn.setOwnerName(this.OwnerName);
      tn.setTableName(this.TableName);
      if (this.TableName != null) {
         tn.setTableName(SelectStatement.changeBackTip(this.TableName));
      }

      tn.registerOverrideToString(this.override_to_string);
      String tempColumnName;
      if (this.ColumnName.charAt(0) == '"') {
         if (this.startPosition != null) {
            this.startValue = Integer.parseInt(this.startPosition);
            this.endValue = Integer.parseInt(this.endPosition);
            this.length = this.endValue - this.startValue + 1;
            tempColumnName = this.getColumnName();
            tn.setColumnName("SUBSTR(" + tempColumnName.toUpperCase() + "," + this.startValue + "," + this.length + ")");
            tn.setStartPosition((String)null);
            tn.setEndPosition((String)null);
         } else if (this.ColumnName != null) {
            tn.setColumnName(SelectStatement.changeBackTip(this.ColumnName));
         }
      } else if ((!this.ColumnName.equalsIgnoreCase("DATE") || from_sqs == null || from_sqs.getFromClause() != null) && !this.ColumnName.equalsIgnoreCase("CURDATE") && !this.ColumnName.equalsIgnoreCase("CURRENT_DATE") && !this.ColumnName.equalsIgnoreCase("SYSDATE")) {
         if (this.ColumnName.equalsIgnoreCase("TIME") && from_sqs != null && from_sqs.getFromClause() == null) {
            tn.setColumnName("CURRENT_TIME");
         } else if (!this.ColumnName.equalsIgnoreCase("CURTIME") && !this.ColumnName.equalsIgnoreCase("CURRENT_TIME") && !this.ColumnName.equalsIgnoreCase("SYSTIME")) {
            if ((this.ColumnName.equalsIgnoreCase("SYSTIMESTAMP") || this.ColumnName.equalsIgnoreCase("CURRENT")) && !this.isFunctionName) {
               tn.setColumnName("CURRENT_TIMESTAMP");
            } else if (!this.ColumnName.equalsIgnoreCase("SYSTEM_USER") && !this.ColumnName.equalsIgnoreCase("USER")) {
               if (this.startPosition != null) {
                  this.startValue = Integer.parseInt(this.startPosition);
                  this.endValue = Integer.parseInt(this.endPosition);
                  this.length = this.endValue - this.startValue + 1;
                  tempColumnName = this.getColumnName();
                  tn.setColumnName("SUBSTR(" + tempColumnName + "," + this.startValue + "," + this.length + ")");
                  tn.setStartPosition((String)null);
                  tn.setEndPosition((String)null);
               } else {
                  tn.setColumnName(this.ColumnName);
                  if (this.ColumnName != null) {
                     tn.setColumnName(SelectStatement.changeBackTip(this.ColumnName));
                  }
               }
            } else {
               tn.setColumnName("CURRENT_USER");
            }
         } else {
            tn.setColumnName("CURRENT_TIME");
         }
      } else {
         tn.setColumnName("CURRENT_DATE");
      }

      tn.setDot(new String("."));
      return tn;
   }

   public TableColumn toTimesTenSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) {
      TableColumn tn = new TableColumn();
      if (this.TableName != null && (this.TableName.startsWith("[") && this.TableName.endsWith("]") || this.TableName.startsWith("`") && this.TableName.endsWith("`"))) {
         this.TableName = this.TableName.substring(1, this.TableName.length() - 1);
         if (this.TableName.indexOf(32) != -1) {
            this.TableName = "\"" + this.TableName + "\"";
         }
      }

      if (this.TableName != null && !this.isFunctionName) {
         this.TableName = CustomizeUtil.objectNamesToQuotedIdentifier(this.TableName, SwisSQLUtils.getKeywords(10), (ModifiedObjectAttr)null, 10);
      }

      if (this.ColumnName != null && !this.isFunctionName && !this.ColumnName.equalsIgnoreCase("user") && (!this.ColumnName.equalsIgnoreCase("sysdate") || !SwisSQLOptions.SOURCE_DB_IS_ORACLE)) {
         this.ColumnName = CustomizeUtil.objectNamesToQuotedIdentifier(this.ColumnName, SwisSQLUtils.getKeywords(10), (ModifiedObjectAttr)null, 10);
      }

      if (this.ColumnName != null && (this.ColumnName.startsWith("[") && this.ColumnName.endsWith("]") || this.ColumnName.startsWith("`") && this.ColumnName.endsWith("`"))) {
         this.ColumnName = this.ColumnName.substring(1, this.ColumnName.length() - 1);
         if (this.ColumnName.indexOf(32) != -1) {
            this.ColumnName = "\"" + this.ColumnName + "\"";
         }
      }

      if (this.OwnerName != null && (this.OwnerName.startsWith("[") && this.OwnerName.endsWith("]") || this.OwnerName.startsWith("`") && this.OwnerName.endsWith("`"))) {
         this.OwnerName = this.OwnerName.substring(1, this.OwnerName.length() - 1);
         if (this.OwnerName.indexOf(32) != -1) {
            this.OwnerName = "\"" + this.OwnerName + "\"";
         }
      }

      if (this.OwnerName != null && this.OwnerName.equalsIgnoreCase("DBO")) {
         tn.setOwnerName((String)null);
      } else {
         tn.setOwnerName(this.OwnerName);
      }

      tn.registerOverrideToString(this.override_to_string);
      boolean isAliasSet = false;
      if (from_sqs != null) {
         FromClause fc = from_sqs.getFromClause();
         Vector v_fil = new Vector();
         if (fc != null) {
            v_fil = fc.getFromItemList();
         }

         for(int i_count = 0; i_count < v_fil.size(); ++i_count) {
            if (v_fil.elementAt(i_count) instanceof FromTable) {
               FromTable ft = (FromTable)v_fil.elementAt(i_count);
               if (ft.getTableName() instanceof String) {
                  String s_tn = (String)ft.getTableName();
                  if (s_tn.equalsIgnoreCase(this.TableName) && ft.getAliasName() == null) {
                     isAliasSet = false;
                     break;
                  }

                  if (s_tn.equalsIgnoreCase(this.TableName) && ft.getAliasName() != null) {
                     tn.setTableName(ft.getAliasName());
                     isAliasSet = true;
                  }
               }
            }
         }
      }

      if (!isAliasSet) {
         tn.setTableName(this.TableName);
      }

      if (!this.ColumnName.equalsIgnoreCase("DATE") && !this.ColumnName.equalsIgnoreCase("CURRENT_DATE") && !this.ColumnName.equalsIgnoreCase("CURRENT")) {
         if ((!this.ColumnName.equalsIgnoreCase("TIME") || from_sqs == null || from_sqs.getFromClause() != null) && (!this.ColumnName.equalsIgnoreCase("CURRENT_TIME") || SwisSQLOptions.fromSybase)) {
            if (!this.ColumnName.equalsIgnoreCase("SYSTEM_USER") && !this.ColumnName.equalsIgnoreCase("CURRENT_USER") && !this.ColumnName.equalsIgnoreCase("USER")) {
               if (this.ColumnName.trim().startsWith("$")) {
                  try {
                     String dollarString = this.ColumnName.substring(1);
                     float numericValue = Float.parseFloat(dollarString);
                     tn.setColumnName(dollarString);
                  } catch (NumberFormatException var10) {
                     tn.setColumnName(this.ColumnName);
                  }
               } else if (this.ColumnName.startsWith("@")) {
                  tn.setColumnName(":" + this.ColumnName.substring(1));
               } else {
                  tn.setColumnName(this.ColumnName);
               }
            } else {
               tn.setColumnName("USER");
            }
         } else {
            tn.setColumnName("TO_CHAR(SYSDATE,'HH:MI:SS')");
         }
      } else {
         tn.setColumnName("SYSDATE");
      }

      tn.setDot(new String("."));
      return tn;
   }

   public TableColumn toNetezzaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) {
      TableColumn tn = new TableColumn();
      tn.setCommentClass(this.commentObj);
      if (this.TableName != null && (this.TableName.startsWith("[") && this.TableName.endsWith("]") || this.TableName.startsWith("`") && this.TableName.endsWith("`"))) {
         this.TableName = this.TableName.substring(1, this.TableName.length() - 1);
         if (this.TableName.indexOf(32) != -1) {
            this.TableName = "\"" + this.TableName + "\"";
         }
      }

      if (this.ColumnName != null && (this.ColumnName.startsWith("[") && this.ColumnName.endsWith("]") || this.ColumnName.startsWith("`") && this.ColumnName.endsWith("`"))) {
         this.ColumnName = this.ColumnName.substring(1, this.ColumnName.length() - 1);
         if (this.ColumnName.indexOf(32) != -1) {
            this.ColumnName = "\"" + this.ColumnName + "\"";
         }
      }

      if (this.OwnerName != null && (this.OwnerName.startsWith("[") && this.OwnerName.endsWith("]") || this.OwnerName.startsWith("`") && this.OwnerName.endsWith("`"))) {
         this.OwnerName = this.OwnerName.substring(1, this.OwnerName.length() - 1);
         if (this.OwnerName.indexOf(32) != -1) {
            this.OwnerName = "\"" + this.OwnerName + "\"";
         }
      }

      if (this.OwnerName != null) {
         this.OwnerName = CustomizeUtil.objectNamesToQuotedIdentifier(this.OwnerName, SwisSQLUtils.getKeywords(11), (ModifiedObjectAttr)null, 11);
      }

      if (this.ColumnName != null) {
         this.ColumnName = CustomizeUtil.objectNamesToQuotedIdentifier(this.ColumnName, SwisSQLUtils.getKeywords(11), (ModifiedObjectAttr)null, 11);
      }

      if (this.TableName != null) {
         this.TableName = CustomizeUtil.objectNamesToQuotedIdentifier(this.TableName, SwisSQLUtils.getKeywords(11), (ModifiedObjectAttr)null, 11);
      }

      tn.registerOverrideToString(this.override_to_string);
      tn.setOwnerName(this.OwnerName);
      if (SwisSQLOptions.renameTableNameAsSchemName_TableName) {
         if (this.OwnerName != null && !this.OwnerName.startsWith("\"") && !this.TableName.startsWith("\"")) {
            tn.setTableName(this.OwnerName + "_" + this.TableName);
         } else {
            tn.setTableName(this.TableName);
         }
      } else {
         tn.setTableName(this.TableName);
      }

      if (this.ColumnName.charAt(0) == '\'') {
         tn.setColumnName(this.ColumnName.replace('\'', '\''));
      } else if ((this.ColumnName.equalsIgnoreCase("SYSDATE") || this.ColumnName.equalsIgnoreCase("CURRENT DATE")) && !this.isFunctionName) {
         tn.setColumnName("CURRENT_DATE");
      } else if (this.ColumnName.equalsIgnoreCase("SYS_GUID")) {
         tn.setColumnName("NEWID()");
      } else if ((!this.ColumnName.equalsIgnoreCase("TIME") || from_sqs == null || from_sqs.getFromClause() != null) && !this.ColumnName.equalsIgnoreCase("CURRENT TIME")) {
         if (!this.ColumnName.equalsIgnoreCase("TIMESTAMP") && !this.ColumnName.equalsIgnoreCase("SYSTIMESTAMP") && !this.ColumnName.equalsIgnoreCase("CURRENT TIMESTAMP") && !this.ColumnName.equalsIgnoreCase("CURRENT")) {
            if (!this.ColumnName.equalsIgnoreCase("USER") && !this.ColumnName.equalsIgnoreCase("SYSTEM_USER")) {
               if (this.startPosition != null) {
                  this.startValue = Integer.parseInt(this.startPosition);
                  this.endValue = Integer.parseInt(this.endPosition);
                  this.length = this.endValue - this.startValue + 1;
                  String tempColumnName = this.getColumnName();
                  tn.setColumnName("SUBSTR(" + tempColumnName + "," + this.startValue + "," + this.length + ")");
                  tn.setStartPosition((String)null);
                  tn.setEndPosition((String)null);
               } else {
                  tn.setColumnName(this.ColumnName);
               }
            } else {
               tn.setColumnName("CURRENT_USER");
            }
         } else {
            tn.setColumnName("CURRENT_TIMESTAMP");
         }
      } else {
         tn.setColumnName("CURRENT_TIME");
      }

      tn.setDot(new String("."));
      return tn;
   }

   public String getResultString() {
      StringBuffer sb = new StringBuffer();
      if (this.commentObj != null) {
         sb.append(this.commentObj.toString().trim() + " ");
      }

      if (this.override_to_string != null) {
         sb.append(this.override_to_string.toString(this));
      } else {
         if (this.tempTableName != null) {
            sb.append(this.tempTableName);
            sb.append(".");
         }

         if (this.databaseName != null) {
            sb.append(this.databaseName);
            sb.append(".");
         }

         if (this.OwnerName != null) {
            sb.append(this.OwnerName);
            sb.append(this.dot);
         }

         String temp;
         if (this.TableName != null) {
            if (!this.TableName.equalsIgnoreCase("NEXT VALUE FOR ") && !this.TableName.trim().equalsIgnoreCase("NEXTVAL FOR") && !this.TableName.trim().equalsIgnoreCase("PREVVAL FOR")) {
               if (this.context != null) {
                  temp = null;
                  if (this.origTableName != null) {
                     Object obj = this.context.getEquivalent(this.origTableName);
                     if (obj != null) {
                        temp = obj.toString();
                     }
                  }

                  if ((this.origTableName == null || !this.origTableName.equals(temp)) && temp != null) {
                     sb.append(temp + ".");
                  } else {
                     sb.append(this.TableName + ".");
                  }
               } else if (FromClause.doNotAddDotInSubquery) {
                  sb.append(this.TableName);
               } else if (SwisSQLAPI.getProperSelExp()) {
                  sb.append(GeneralUtil.trimIfTblColIsEnclosed(this.TableName) + ".");
               } else {
                  sb.append(this.TableName + ".");
               }
            } else {
               sb.append(this.TableName);
            }
         }

         if (this.context != null && !FromClause.doNotAddDotInSubquery) {
            temp = this.context.getEquivalent(this.ColumnName).toString();
            sb.append(temp);
         } else if (this.ColumnName != null) {
            if (SwisSQLAPI.getProperSelExp()) {
               sb.append(GeneralUtil.trimIfTblColIsEnclosed(this.ColumnName));
            } else {
               sb.append(this.ColumnName);
            }

            if (this.collateClause != null && this.collationName != null && !this.collateClause.isEmpty() && !this.collationName.isEmpty()) {
               sb.append(" ");
               sb.append(this.collateClause);
               sb.append(" ");
               sb.append(this.collationName);
               sb.append(" ");
            }
         }

         if (this.startPosition != null) {
            sb.append("[" + this.startPosition);
         }

         if (this.endPosition != null) {
            sb.append("," + this.endPosition + "]");
         }
      }

      if (this.commentObjAfterToken != null) {
         sb.append(" " + this.commentObjAfterToken.toString().trim());
      }

      return sb.toString();
   }

   public String getSourceDataType() {
      String str = this.getResultString();
      String sourceDataType = this.sourceDataType;
      if (sourceDataType != null) {
         sourceDataType = DB2DataTypeConverter.convertPLSQLTypeToDB2Type(sourceDataType);
         sourceDataType = CastingUtil.getDataType(sourceDataType);
      } else if (sourceDataType == null && SwisSQLAPI.variableDatatypeMapping != null) {
         sourceDataType = CastingUtil.getDataType((String)((String)SwisSQLAPI.variableDatatypeMapping.get(str)));
      }

      return sourceDataType;
   }

   public String toString() {
      String str = this.getResultString();
      if (this.toDB2) {
         String res = CastingUtil.getDB2DataTypeCastedParameter(this.getSourceDataType(), this.targetDataType, str);
         return res;
      } else {
         return str;
      }
   }

   private boolean isSystemFunction(String columnName) {
      boolean bl = false;
      String[] sysFuncs = SwisSQLUtils.getSystemFunctions(12);

      for(int k = 0; k < sysFuncs.length; ++k) {
         if (columnName.equalsIgnoreCase(sysFuncs[k])) {
            bl = true;
            break;
         }
      }

      return bl;
   }

   public TableColumn toVectorWiseSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) {
      TableColumn tn = new TableColumn();
      if (this.TableName != null && (this.TableName.startsWith("[") && this.TableName.endsWith("]") || this.TableName.startsWith("`") && this.TableName.endsWith("`"))) {
         this.TableName = this.TableName.substring(1, this.TableName.length() - 1);
         this.TableName = "\"" + this.TableName + "\"";
      }

      if (this.ColumnName != null && (this.ColumnName.startsWith("[") && this.ColumnName.endsWith("]") || this.ColumnName.startsWith("`") && this.ColumnName.endsWith("`"))) {
         this.ColumnName = this.ColumnName.substring(1, this.ColumnName.length() - 1);
         this.ColumnName = "\"" + this.ColumnName + "\"";
      }

      if (this.OwnerName != null && (this.OwnerName.startsWith("[") && this.OwnerName.endsWith("]") || this.OwnerName.startsWith("`") && this.OwnerName.endsWith("`"))) {
         this.OwnerName = this.OwnerName.substring(1, this.OwnerName.length() - 1);
         this.OwnerName = "\"" + this.OwnerName + "\"";
      }

      tn.setOwnerName(this.OwnerName);
      tn.setTableName(this.TableName);
      tn.registerOverrideToString(this.override_to_string);
      String tempColumnName;
      if (this.ColumnName.charAt(0) == '`') {
         if (this.startPosition != null) {
            this.startValue = Integer.parseInt(this.startPosition);
            this.endValue = Integer.parseInt(this.endPosition);
            this.length = this.endValue - this.startValue + 1;
            tempColumnName = this.getColumnName();
            tn.setColumnName("SUBSTRING(" + tempColumnName.toUpperCase() + "," + this.startValue + "," + this.length + ")");
            tn.setStartPosition((String)null);
            tn.setEndPosition((String)null);
         } else {
            tn.setColumnName(this.ColumnName.replace('`', ' ').trim());
         }
      } else if (this.ColumnName.equalsIgnoreCase("DATE")) {
         tn.setColumnName("CURRENT_DATE");
      } else if (this.ColumnName.equalsIgnoreCase("TIME") && from_sqs != null && from_sqs.getFromClause() == null) {
         tn.setColumnName("CURRENT_TIME");
      } else if ((this.ColumnName.equalsIgnoreCase("TIMESTAMP") || this.ColumnName.equalsIgnoreCase("SYSTIMESTAMP") || this.ColumnName.equalsIgnoreCase("SYSDATE") || this.ColumnName.equalsIgnoreCase("CURRENT")) && !this.isFunctionName) {
         tn.setColumnName("CURRENT_TIMESTAMP");
      } else if (!this.ColumnName.equalsIgnoreCase("SYSTEM_USER") && !this.ColumnName.equalsIgnoreCase("USER") && !this.ColumnName.equalsIgnoreCase("CURRENT_USER")) {
         if (this.startPosition != null) {
            this.startValue = Integer.parseInt(this.startPosition);
            this.endValue = Integer.parseInt(this.endPosition);
            this.length = this.endValue - this.startValue + 1;
            tempColumnName = this.getColumnName();
            tn.setColumnName("SUBSTRING(" + tempColumnName + "," + this.startValue + "," + this.length + ")");
            tn.setStartPosition((String)null);
            tn.setEndPosition((String)null);
         } else {
            tn.setColumnName(this.ColumnName);
         }
      } else {
         tn.setColumnName("USER()");
      }

      tn.setDot(new String("."));
      return tn;
   }

   public TableColumn toReplaceTblCol(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      TableColumn tblColConv = new TableColumn();
      if (this.commentObj != null) {
         tblColConv.setCommentClass(this.commentObj);
      }

      if (this.tempTableName != null) {
         tblColConv.setTempTableColumnName(this.tempTableName);
      }

      if (this.databaseName != null) {
         tblColConv.setDatabaseName(this.databaseName);
      }

      if (this.OwnerName != null) {
         tblColConv.setOwnerName(this.OwnerName);
      }

      String colName;
      String tableName;
      String trimTblName;
      HashMap colDetsMap;
      String[] colDets;
      Iterator it;
      if (this.TableName != null) {
         if (from_sqs.getQueryConvDataHandler() != null) {
            colName = GeneralUtil.trimIfTblColIsEnclosed(this.ColumnName);
            tableName = GeneralUtil.trimIfTblColIsEnclosed(this.TableName);
            if (from_sqs.getQueryConvDataHandler().isRenameRequest()) {
               if (CastingUtil.getValueIgnoreCase(from_sqs.getQueryConvDataHandler().getTableDetsMap(), tableName) != null) {
                  tableName = GeneralUtil.getReplacedTblColDets(from_sqs.getQueryConvDataHandler().getTableDetsMap(), tableName)[2];
               }

               it = from_sqs.getQueryConvDataHandler().getTableColumnDetsMap().keySet().iterator();

               while(it.hasNext()) {
                  HashMap colDetsMap = (HashMap)from_sqs.getQueryConvDataHandler().getTableColumnDetsMap().get((String)it.next());
                  if (CastingUtil.getValueIgnoreCase(colDetsMap, colName) != null) {
                     String[] colDets = GeneralUtil.getReplacedTblColDets(colDetsMap, this.ColumnName);
                     colName = colDets[2];
                     break;
                  }
               }
            } else if (from_sqs.getCanSkipExceptions() || !from_sqs.getQueryConvDataHandler().getExceptionMap().containsKey("INVALID_TABLE") || !((HashMap)from_sqs.getQueryConvDataHandler().getExceptionMap().get("INVALID_TABLE")).containsKey(tableName)) {
               if (CastingUtil.getValueIgnoreCase(from_sqs.getTableDetsMap(), tableName) != null) {
                  String[] tblDets = GeneralUtil.getReplacedTblColDets(from_sqs.getTableDetsMap(), this.TableName);
                  trimTblName = tblDets[0];
                  tableName = GeneralUtil.checkAndEncloseColumnName(tblDets[2]);
                  colDetsMap = (HashMap)CastingUtil.getValueIgnoreCase(from_sqs.getTableColumnDetsMap(), trimTblName);
                  if (CastingUtil.getValueIgnoreCase(colDetsMap, colName) != null) {
                     colDets = GeneralUtil.getReplacedTblColDets(colDetsMap, this.ColumnName);
                     colName = GeneralUtil.checkAndEncloseColumnName(colDets[2]);
                  } else if (!from_sqs.getIsAliasReferenceClausesIteration() || !from_sqs.getAliasColumns().contains(colName)) {
                     if (!from_sqs.getCanAllowExceptionStacking()) {
                        throw new ConvertException("Invalid Column", "INVALID_COLUMN_IN_SELECT", new Object[]{colName});
                     }

                     from_sqs.getQueryConvDataHandler().addExceptionMap("INVALID_COLUMN_IN_SELECT", new Object[]{colName, null});
                  }
               } else {
                  if (!from_sqs.getCanAllowExceptionStacking()) {
                     throw new ConvertException("Unknown column present in select query", "UNKNOWN_TABLE_OR_ALIAS_USED", new Object[]{tableName});
                  }

                  from_sqs.getQueryConvDataHandler().addExceptionMap("UNKNOWN_TABLE_OR_ALIAS_USED", new Object[]{tableName + "." + colName, null});
               }
            }

            tblColConv.setTableName(tableName);
            tblColConv.setColumnName(colName);
         } else {
            tblColConv.setTableName(this.TableName);
            tblColConv.setColumnName(this.ColumnName);
         }
      } else if (from_sqs.getQueryConvDataHandler() != null) {
         colName = GeneralUtil.trimIfTblColIsEnclosed(this.ColumnName);
         tableName = colName;
         if (from_sqs.getQueryConvDataHandler().isRenameRequest()) {
            it = from_sqs.getQueryConvDataHandler().getTableColumnDetsMap().keySet().iterator();

            while(it.hasNext()) {
               trimTblName = (String)it.next();
               colDetsMap = (HashMap)from_sqs.getQueryConvDataHandler().getTableColumnDetsMap().get(trimTblName);
               if (CastingUtil.getValueIgnoreCase(colDetsMap, colName) != null) {
                  colDets = GeneralUtil.getReplacedTblColDets(colDetsMap, this.ColumnName);
                  tableName = colDets[2];
               }
            }
         } else {
            int tblColDetsSize = from_sqs.getTableColumnDetsMap().size();
            if (tblColDetsSize == 0) {
               if (!from_sqs.getCanAllowExceptionStacking()) {
                  throw new ConvertException("Invalid Column", "INVALID_COLUMN_IN_SELECT", new Object[]{colName});
               }

               from_sqs.getQueryConvDataHandler().addExceptionMap("INVALID_COLUMN_IN_SELECT", new Object[]{colName, null});
            }

            HashSet<String> duplicateColsInMultipleTbls = new HashSet();
            Iterator it = from_sqs.getTableColumnDetsMap().keySet().iterator();

            String error;
            while(it.hasNext()) {
               error = (String)it.next();
               HashMap colDetsMap = (HashMap)from_sqs.getTableColumnDetsMap().get(error);
               if (CastingUtil.getValueIgnoreCase(colDetsMap, colName) != null) {
                  String[] colDets = GeneralUtil.getReplacedTblColDets(colDetsMap, this.ColumnName);
                  tableName = colDets[2];
                  duplicateColsInMultipleTbls.add(error + "." + colDets[0]);
               }
            }

            if (duplicateColsInMultipleTbls.size() <= 0 && (!from_sqs.getIsAliasReferenceClausesIteration() || !from_sqs.getAliasColumns().contains(colName))) {
               if (!from_sqs.getCanAllowExceptionStacking()) {
                  throw new ConvertException("Invalid Column", "INVALID_COLUMN_IN_SELECT", new Object[]{colName});
               }

               from_sqs.getQueryConvDataHandler().addExceptionMap("INVALID_COLUMN_IN_SELECT", new Object[]{colName, null});
            }

            if (duplicateColsInMultipleTbls.size() > 1) {
               if (!from_sqs.getCanAllowExceptionStacking()) {
                  throw new ConvertException("Display Columns in Multiples tables", "DISCOLS_IN_MULTIPLE_TABLES", new Object[]{duplicateColsInMultipleTbls});
               }

               error = "Same display columns presents in multiple tables " + duplicateColsInMultipleTbls.toString() + " of your SQL Query. To execute or save query, kindly use the table name or table alias name before the display columns";
               from_sqs.getQueryConvDataHandler().addExceptionMap("DISCOLS_IN_MULTIPLE_TABLES", new Object[]{error, null});
            }
         }

         tableName = GeneralUtil.checkAndEncloseColumnName(tableName);
         tblColConv.setColumnName(tableName);
      } else {
         tblColConv.setColumnName(this.ColumnName);
      }

      if (this.startPosition != null) {
         tblColConv.setStartPosition(this.startPosition);
      }

      if (this.endPosition != null) {
         tblColConv.setEndPosition(this.endPosition);
      }

      if (this.origTableName != null) {
         tblColConv.setOrigTableName(this.origTableName);
      }

      if (this.collateClause != null) {
         tblColConv.setCollateClause(this.collateClause);
      }

      if (this.collationName != null) {
         tblColConv.setCollationName(this.collationName);
      }

      if (this.commentObjAfterToken != null) {
         tblColConv.setCommentClassAfterToken(this.commentObjAfterToken);
      }

      return tblColConv;
   }
}
