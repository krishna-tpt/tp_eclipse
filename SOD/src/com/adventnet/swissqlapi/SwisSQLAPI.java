package com.adventnet.swissqlapi;

import com.adventnet.swissqlapi.config.datatypes.DatatypeMapping;
import com.adventnet.swissqlapi.config.metadata.MetaDataProperties;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.parser.ALLSQL;
import com.adventnet.swissqlapi.sql.parser.ParseException;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;
import com.adventnet.swissqlapi.sql.statement.create.CreateQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectInvolvedTables;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.WithStatement;
import com.adventnet.swissqlapi.util.FunctionValidateHandler;
import com.adventnet.swissqlapi.util.QueryConvDataHandler;
import com.adventnet.swissqlapi.util.QueryConvPropsHandler;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.util.database.MetaDataUtility;
import com.adventnet.swissqlapi.util.misc.BuiltInFunctionDetails;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Pattern;

public class SwisSQLAPI {
   private ALLSQL vembuParser = null;
   private boolean constructed = false;
   private SwisSQLStatement currentSwisSQLStatement = null;
   private boolean reInitToBeDone = true;
   private Hashtable dbCumDatatypeMapping = new Hashtable();
   private HashMap objectNames = new HashMap();
   public static boolean ANSIJOIN_ForOracle = false;
   public static ThreadLocal<HashMap> threadLocalMetaDataHm = new ThreadLocal();
   public static Hashtable columnDatatypes = new Hashtable();
   public static Hashtable targetDataTypesMetaDataHash = new Hashtable();
   public static Hashtable identityMapping = new Hashtable();
   public static Hashtable primaryKeyMetaData = new Hashtable();
   public static String excelTblName = "";
   public static final int GIVENSQL = 0;
   public static final int ORACLE = 1;
   public static final int MSSQLSERVER = 2;
   public static final int DB2 = 3;
   public static final int POSTGRESQL = 4;
   public static final int MYSQL = 5;
   public static final int INFORMIX = 6;
   public static final int SYBASE = 7;
   public static final int ANSISQL = 8;
   public static final int COMMON = 9;
   public static final int TIMESTEN = 10;
   public static final int NETEZZA = 11;
   public static final int TERADATA = 12;
   public static final int VECTORWISE = 13;
   public static final int BIGQUERY = 14;
   public static final int SNOWFLAKE = 15;
   public static final int ATHENA = 16;
   public static final int SAPHANA = 17;
   public static final int SQLITE = 18;
   public static final int HSQL = 19;
   public static final int EXCEL = 20;
   public static final int MSACCESS_JDBC = 21;
   public static boolean MSSQLSERVER_THETA = false;
   public static boolean convert_OracleThetaJOIN_To_ANSIJOIN = false;
   public static HashMap variableDatatypeMapping = null;
   public static UserObjectContext objectContext = null;
   private Hashtable dbCumDatatypeMappingFile = new Hashtable();
   private Hashtable dbCumDatatypeMappingStream = new Hashtable();
   public static boolean convertCaseToDecode = true;
   public static boolean quotedOracleIdentifier = false;
   public static boolean enableObjectMapping = false;
   public static boolean convertToTeradata = true;
   public static BuiltInFunctionDetails builtInFunctionDetails;
   public static boolean tozohodb = false;
   public static HashMap objectsOwnerName = new HashMap();
   public static HashMap targetDBMappedFunctionNames = new HashMap();
   public static String targetDBFunctionMappingFile = null;
   public boolean isAmazonRedShift = false;
   public boolean isPgsqlLive = false;
   public boolean isMSAzure = false;
   public boolean isBigQuery = false;
   public boolean isMSAzureWareHouse = false;
   public boolean isOracleLive = false;
   public boolean isMySqlLive = false;
   public boolean isSnowflake = false;
   public boolean isAthena = false;
   public boolean isTrino = false;
   public boolean isMongoDb = false;
   public boolean isVerticaDb = false;
   public boolean isSapHanaDb = false;
   public boolean isSqlite = false;
   public boolean isDenodo = false;
   public boolean isExcel = false;
   public boolean isMsAccessJdbc = false;
   public static boolean isPriorMysql8VersionDialect = false;
   public boolean canUseOracleFetch = true;
   private FunctionValidateHandler validationHandler = null;
   private QueryConvPropsHandler convPropHandler = null;
   private QueryConvDataHandler queryConvDataHandler;
   public static boolean truncateTableNameForDB2 = true;
   public static boolean truncateTableNameForOracle = true;
   public static int truncateTableCount = 0;
   public static int truncateIndexCount = 0;
   public static int truncateConstraintCount = 0;
   public static ThreadLocal involvedTablesTL = new ThreadLocal() {
      public SelectInvolvedTables initialValue() {
         return new SelectInvolvedTables();
      }
   };
   public static ThreadLocal properSelectExpression = new ThreadLocal() {
      public Boolean initialValue() {
         return false;
      }
   };
   private static ThreadLocal fullJoinExecute = new ThreadLocal() {
      public Boolean initialValue() {
         return false;
      }
   };
   public static ThreadLocal removeLimitAndFetchClause = new ThreadLocal() {
      public Boolean initialValue() {
         return false;
      }
   };
   public static ThreadLocal indexPositionsForCastingTL = new ThreadLocal() {
      public Set initialValue() {
         return new HashSet();
      }
   };
   public static ThreadLocal holidayTable = new ThreadLocal() {
      public LinkedHashSet initialValue() {
         return new LinkedHashSet();
      }
   };
   public static Map fnWhiteListMap;
   public static ThreadLocal indexPositionsForNULLTL = new ThreadLocal() {
      public Set initialValue() {
         return new HashSet();
      }
   };
   public static ArrayList<String> keyWordsAsTblColList = getSQLKeyWordsAsTblCol();
   public static HashMap<String, String> udfFunctionMap = getUDFFunctionMap();
   public static Map<String, String> MsSQLServerIntervalList = getIntervalValueMap();
   public static ArrayList<String> PostgresSqlTimeUnits = getPostgresSqlTimeUnits();

   public static void setMSSQLServerThetaConversion(boolean flag) {
      MSSQLSERVER_THETA = flag;
   }

   public static void setANSIJOIN_ForOracle(boolean flag) {
      ANSIJOIN_ForOracle = flag;
   }

   public static void setQuotedOracleIdentifier(boolean flag) {
      quotedOracleIdentifier = flag;
   }

   public SwisSQLAPI() {
      this.resetStaticVariables();
   }

   public SwisSQLAPI(String sql) {
      if (!this.constructed) {
         this.vembuParser = new ALLSQL(new StringReader(sql));
         this.reInitToBeDone = false;
         this.constructed = true;
      } else {
         this.vembuParser.ReInit((Reader)(new StringReader(sql)));
         this.reInitToBeDone = false;
      }

      this.resetStaticVariables();
   }

   public SwisSQLAPI(InputStream stream) {
      if (!this.constructed) {
         this.vembuParser = new ALLSQL(stream);
         this.reInitToBeDone = false;
         this.constructed = true;
      } else {
         this.vembuParser.ReInit(stream);
         this.reInitToBeDone = false;
      }

      this.resetStaticVariables();
   }

   public SwisSQLAPI(Reader stream) {
      if (!this.constructed) {
         this.vembuParser = new ALLSQL(stream);
         this.reInitToBeDone = false;
         this.constructed = true;
      } else {
         this.vembuParser.ReInit(stream);
         this.reInitToBeDone = false;
      }

      this.resetStaticVariables();
   }

   public void setHandlers(QueryConvPropsHandler propsHandler, QueryConvDataHandler dataHandler, FunctionValidateHandler funValHandler) {
      this.convPropHandler = propsHandler;
      this.queryConvDataHandler = dataHandler;
      this.validationHandler = funValHandler;
   }

   public HashMap getObjectNames() {
      return this.objectNames;
   }

   public void setAmazonRedShiftFlag(boolean isAmazonRedShift) {
      this.isAmazonRedShift = isAmazonRedShift;
   }

   public void setPgsqlFlag(boolean isPgsqlLive) {
      this.isPgsqlLive = isPgsqlLive;
   }

   public void setMSAzureFlag(boolean isMSAzure) {
      this.isMSAzure = isMSAzure;
   }

   public void setMSAzureWareHouseFlag(boolean isMSAzureWareHouse) {
      this.isMSAzureWareHouse = isMSAzureWareHouse;
   }

   public void setOracleLiveFlag(boolean isOracleLive) {
      this.isOracleLive = isOracleLive;
   }

   public void setBigQueryFlag(boolean isBigQuery) {
      this.isBigQuery = isBigQuery;
   }

   public void setMySqlLiveFlag(boolean isMySqlLive) {
      this.isMySqlLive = isMySqlLive;
   }

   public void setSnowflakeFlag(boolean isSnowflake) {
      this.isSnowflake = isSnowflake;
   }

   public void setAthenaFlag(boolean isAthena) {
      this.isAthena = isAthena;
   }

   public void setTrinoFlag(boolean isTrino) {
      this.isTrino = isTrino;
   }

   public void setMongoDbFlag(boolean isMongoDb) {
      this.isMongoDb = isMongoDb;
   }

   public void setVerticaFlag(boolean isVerticaDb) {
      this.isVerticaDb = isVerticaDb;
   }

   public void setSapHanaFlag(boolean isSapHanaDb) {
      this.isSapHanaDb = isSapHanaDb;
   }

   public void setSqliteFlag(boolean isSqlite) {
      this.isSqlite = isSqlite;
   }

   public void setDenodoFlag(boolean isDenodo) {
      this.isDenodo = isDenodo;
   }

   public void setExcelFlag(boolean isExcel) {
      this.isExcel = isExcel;
   }

   public void setMsaccessJdbcFlag(boolean isMsAccessJdbc) {
      this.isMsAccessJdbc = isMsAccessJdbc;
   }

   public static boolean getIsPriorMysql8VersionDialect() {
      return isPriorMysql8VersionDialect;
   }

   public static Hashtable getTableColumnListMetadata() {
      return threadLocalMetaDataHm.get() == null ? new Hashtable() : (Hashtable)((HashMap)threadLocalMetaDataHm.get()).get("tableColumnListMetadata");
   }

   public static void setTableColumnListMetadata(Object TableColumnListMetaData) {
      setMetaDataInThreadLocal("tableColumnListMetadata", TableColumnListMetaData);
   }

   public static Hashtable getDataTypesFromMetaDataHT() {
      return threadLocalMetaDataHm.get() == null ? new Hashtable() : (Hashtable)((HashMap)threadLocalMetaDataHm.get()).get("dataTypesFromMetaDataHT");
   }

   public static void setDataTypesFromMetaDataHT(Object DataTypesFromMetaData) {
      setMetaDataInThreadLocal("dataTypesFromMetaDataHT", DataTypesFromMetaData);
   }

   public void setCanUseOracleFetch(boolean canUseOracleFetch) {
      this.canUseOracleFetch = canUseOracleFetch;
   }

   public boolean getCanUseOracleFetch() {
      return this.canUseOracleFetch;
   }

   public void setValidationHandler(FunctionValidateHandler handler) {
      this.validationHandler = handler;
   }

   public FunctionValidateHandler getValidationHandler() {
      return this.validationHandler;
   }

   public void setConversionPropsHandler(QueryConvPropsHandler handler) {
      this.convPropHandler = handler;
   }

   public QueryConvPropsHandler getConversionPropsHandler() {
      return this.convPropHandler;
   }

   public synchronized void setSQLString(String sql) {
      if (sql != null && sql.trim().startsWith("(") && sql.trim().endsWith(")")) {
         sql = sql.trim().substring(1, sql.trim().length() - 1);
      }

      this.setFormulaOrSQLString(sql);
   }

   public synchronized void setFormulaString(String sql) {
      this.setFormulaOrSQLString(sql);
   }

   public synchronized void setFormulaOrSQLString(String sql) {
      if (!this.constructed) {
         this.vembuParser = new ALLSQL(new StringReader(sql));
         this.reInitToBeDone = false;
         this.constructed = true;
      } else {
         this.vembuParser.ReInit((Reader)(new StringReader(sql)));
         this.reInitToBeDone = false;
      }

      this.resetStaticVariables();
   }

   public synchronized void setSQLInputStream(InputStream stream) {
      if (!this.constructed) {
         this.vembuParser = new ALLSQL(stream);
         this.reInitToBeDone = false;
         this.constructed = true;
      } else {
         this.vembuParser.ReInit(stream);
         this.reInitToBeDone = false;
      }

      this.resetStaticVariables();
   }

   public synchronized void setSQLReader(Reader stream) {
      if (!this.constructed) {
         this.vembuParser = new ALLSQL(stream);
         this.reInitToBeDone = false;
         this.constructed = true;
      } else {
         this.vembuParser.ReInit(stream);
         this.reInitToBeDone = false;
      }

      this.resetStaticVariables();
   }

   public synchronized String convert(int dialect) throws ParseException, ConvertException {
      return this.convert(dialect, false);
   }

   public synchronized String convert(int dialect, boolean enable_indent) throws ParseException, ConvertException {
      return this.convert(dialect, enable_indent, false);
   }

   public synchronized String convert(int dialect, boolean enable_indent, boolean meta) throws ParseException, ConvertException {
      String s_sql = null;
      if (!this.reInitToBeDone) {
         this.currentSwisSQLStatement = this.vembuParser.CompilationUnit();
         this.reInitToBeDone = true;
      }

      if (this.currentSwisSQLStatement == null) {
         return "";
      } else {
         boolean setDatatypeMapping = false;
         if (this.currentSwisSQLStatement instanceof CreateQueryStatement || this.currentSwisSQLStatement instanceof SelectQueryStatement || this.currentSwisSQLStatement instanceof WithStatement) {
            if (!(this.currentSwisSQLStatement instanceof WithStatement)) {
               setDatatypeMapping = true;
            }

            if (this.currentSwisSQLStatement instanceof SelectQueryStatement) {
               ((SelectQueryStatement)this.currentSwisSQLStatement).setTopLevel(true);
               ((SelectQueryStatement)this.currentSwisSQLStatement).setReportsMeta(meta);
            }

            if (this.currentSwisSQLStatement instanceof WithStatement) {
               ((WithStatement)this.currentSwisSQLStatement).getWithSQS().setTopLevel(true);
               ((WithStatement)this.currentSwisSQLStatement).getWithSQS().setReportsMeta(meta);
            }
         }

         if (targetDBFunctionMappingFile != null) {
            this.loadFunctionNameMapping(targetDBFunctionMappingFile);
         }

         SelectQueryStatement sqs;
         switch(dialect) {
         case 0:
            s_sql = this.currentSwisSQLStatement.toString();
            break;
         case 1:
            if (setDatatypeMapping) {
               this.setDatatypeMappingForSQLDialect(this.currentSwisSQLStatement, 1);
            }

            if (this.isOracleLive && this.currentSwisSQLStatement instanceof SelectQueryStatement) {
               ((SelectQueryStatement)this.currentSwisSQLStatement).setOracleLiveFlag(this.isOracleLive);
               ((SelectQueryStatement)this.currentSwisSQLStatement).setCanUseOracleFetch(this.canUseOracleFetch);
            }

            if (this.isDenodo && this.currentSwisSQLStatement instanceof SelectQueryStatement) {
               ((SelectQueryStatement)this.currentSwisSQLStatement).setDenodoFlag(this.isDenodo);
            }

            s_sql = this.currentSwisSQLStatement.toOracleString();
            break;
         case 2:
            if (setDatatypeMapping) {
               this.setDatatypeMappingForSQLDialect(this.currentSwisSQLStatement, 2);
            }

            if (this.isMSAzure && this.currentSwisSQLStatement instanceof SelectQueryStatement) {
               ((SelectQueryStatement)this.currentSwisSQLStatement).setMSAzureFlag(this.isMSAzure);
            }

            if (this.isMSAzureWareHouse && this.currentSwisSQLStatement instanceof SelectQueryStatement) {
               ((SelectQueryStatement)this.currentSwisSQLStatement).setMSAzureWareHouseFlag(this.isMSAzureWareHouse);
            }

            s_sql = this.currentSwisSQLStatement.toMSSQLServerString();
            break;
         case 3:
            if (setDatatypeMapping) {
               this.setDatatypeMappingForSQLDialect(this.currentSwisSQLStatement, 3);
            }

            s_sql = this.currentSwisSQLStatement.toDB2String();
            break;
         case 4:
            if (setDatatypeMapping) {
               this.setDatatypeMappingForSQLDialect(this.currentSwisSQLStatement, 4);
            }

            if (this.currentSwisSQLStatement instanceof SelectQueryStatement) {
               sqs = (SelectQueryStatement)this.currentSwisSQLStatement;
               if (this.isAmazonRedShift) {
                  sqs.setAmazonRedShiftFlag(this.isAmazonRedShift);
               }

               if (this.isPgsqlLive) {
                  sqs.setPgsqlFlag(this.isPgsqlLive);
               }

               if (this.isVerticaDb) {
                  sqs.setVerticaFlag(this.isVerticaDb);
               }

               sqs.setQueryConversionPropHandler(this.convPropHandler);
            } else {
               WithStatement withStatement = (WithStatement)this.currentSwisSQLStatement;
               withStatement.setQueryConversionPropHandler(this.convPropHandler);
            }

            clearIndexPositions();
            clearNULLIndexPositions();
            s_sql = this.currentSwisSQLStatement.toPostgreSQLString();
            removeIndexPositionsTL();
            removeNULLIndexPositionsTL();
            break;
         case 5:
            if (setDatatypeMapping) {
               this.setDatatypeMappingForSQLDialect(this.currentSwisSQLStatement, 5);
            }

            if (this.currentSwisSQLStatement instanceof SelectQueryStatement) {
               sqs = (SelectQueryStatement)this.currentSwisSQLStatement;
               if (this.isMySqlLive) {
                  sqs.setMySqlLiveFlag(this.isMySqlLive);
                  sqs.setValidationHandler(this.validationHandler);
                  sqs.setQueryConversionPropHandler(this.convPropHandler);
               }

               if (this.isMongoDb) {
                  sqs.setMongoDbFlag(this.isMongoDb);
                  sqs.setValidationHandler(this.validationHandler);
                  sqs.setQueryConversionPropHandler(this.convPropHandler);
               }

               sqs.setQueryConversionPropHandler(this.convPropHandler);
               sqs.setSQLDialect(5);
            }

            s_sql = this.currentSwisSQLStatement.toMySQLString();
            break;
         case 6:
            if (setDatatypeMapping) {
               this.setDatatypeMappingForSQLDialect(this.currentSwisSQLStatement, 6);
            }

            s_sql = this.currentSwisSQLStatement.toInformixString();
            break;
         case 7:
            if (setDatatypeMapping) {
               this.setDatatypeMappingForSQLDialect(this.currentSwisSQLStatement, 7);
            }

            s_sql = this.currentSwisSQLStatement.toSybaseString();
            break;
         case 8:
            if (setDatatypeMapping) {
               this.setDatatypeMappingForSQLDialect(this.currentSwisSQLStatement, 8);
            }

            s_sql = this.currentSwisSQLStatement.toANSIString();
            break;
         case 9:
         default:
            s_sql = "Not Supported Dialect";
            break;
         case 10:
            if (setDatatypeMapping) {
               this.setDatatypeMappingForSQLDialect(this.currentSwisSQLStatement, 10);
            }

            s_sql = this.currentSwisSQLStatement.toTimesTenString();
            break;
         case 11:
            if (setDatatypeMapping) {
               this.setDatatypeMappingForSQLDialect(this.currentSwisSQLStatement, 11);
            }

            s_sql = this.currentSwisSQLStatement.toNetezzaString();
            break;
         case 12:
            if (setDatatypeMapping) {
               this.setDatatypeMappingForSQLDialect(this.currentSwisSQLStatement, 12);
            }

            s_sql = this.currentSwisSQLStatement.toTeradataString();
            break;
         case 13:
            if (setDatatypeMapping) {
               this.setDatatypeMappingForSQLDialect(this.currentSwisSQLStatement, 13);
            }

            if (this.currentSwisSQLStatement instanceof SelectQueryStatement) {
               sqs = (SelectQueryStatement)this.currentSwisSQLStatement;
               sqs.setQueryConversionPropHandler(this.convPropHandler);
            }

            clearIndexPositions();
            clearNULLIndexPositions();
            s_sql = this.currentSwisSQLStatement.toVectorWiseString();
            removeIndexPositionsTL();
            removeNULLIndexPositionsTL();
            break;
         case 14:
            if (setDatatypeMapping) {
               this.setDatatypeMappingForSQLDialect(this.currentSwisSQLStatement, 14);
            }

            if (this.isBigQuery && this.currentSwisSQLStatement instanceof SelectQueryStatement) {
               ((SelectQueryStatement)this.currentSwisSQLStatement).setBigQueryFlag(this.isBigQuery);
            }

            s_sql = this.currentSwisSQLStatement.toBigQueryString();
            break;
         case 15:
            if (setDatatypeMapping) {
               this.setDatatypeMappingForSQLDialect(this.currentSwisSQLStatement, 15);
            }

            if (this.currentSwisSQLStatement instanceof SelectQueryStatement) {
               sqs = (SelectQueryStatement)this.currentSwisSQLStatement;
               if (this.isSnowflake) {
                  sqs.setSnowflakeFlag(this.isSnowflake);
               }
            }

            s_sql = this.currentSwisSQLStatement.toSnowflakeString();
            break;
         case 16:
            if (setDatatypeMapping) {
               this.setDatatypeMappingForSQLDialect(this.currentSwisSQLStatement, 16);
            }

            if (this.currentSwisSQLStatement instanceof SelectQueryStatement) {
               sqs = (SelectQueryStatement)this.currentSwisSQLStatement;
               if (this.isAthena) {
                  sqs.setAthenaFlag(this.isAthena);
               }

               if (this.isTrino) {
                  sqs.setTrinoFlag(this.isTrino);
               }
            }

            s_sql = this.currentSwisSQLStatement.toAthenaString();
            break;
         case 17:
            if (setDatatypeMapping) {
               this.setDatatypeMappingForSQLDialect(this.currentSwisSQLStatement, 17);
            }

            if (this.currentSwisSQLStatement instanceof SelectQueryStatement) {
               sqs = (SelectQueryStatement)this.currentSwisSQLStatement;
               if (this.isSapHanaDb) {
                  sqs.setSapHanaFlag(this.isSapHanaDb);
               }
            }

            s_sql = this.currentSwisSQLStatement.toSapHanaString();
            break;
         case 18:
            if (setDatatypeMapping) {
               this.setDatatypeMappingForSQLDialect(this.currentSwisSQLStatement, 18);
            }

            if (this.currentSwisSQLStatement instanceof SelectQueryStatement) {
               sqs = (SelectQueryStatement)this.currentSwisSQLStatement;
               if (this.isSqlite) {
                  sqs.setSqliteFlag(this.isSqlite);
               }
            }

            s_sql = this.currentSwisSQLStatement.toSqliteString();
            break;
         case 19:
            if (setDatatypeMapping) {
               this.setDatatypeMappingForSQLDialect(this.currentSwisSQLStatement, 19);
            }

            if (this.currentSwisSQLStatement instanceof SelectQueryStatement) {
               sqs = (SelectQueryStatement)this.currentSwisSQLStatement;
               sqs.setMySqlLiveFlag(true);
               this.convPropHandler.getQueryConvProps().put("can.use.back.tip.in.column.name", false);
               sqs.setValidationHandler(this.validationHandler);
               sqs.setSQLDialect(19);
               sqs.setQueryConversionPropHandler(this.convPropHandler);
            }

            s_sql = this.currentSwisSQLStatement.toMySQLString();
            break;
         case 20:
            if (setDatatypeMapping) {
               this.setDatatypeMappingForSQLDialect(this.currentSwisSQLStatement, 20);
            }

            if (this.currentSwisSQLStatement instanceof SelectQueryStatement) {
               ((SelectQueryStatement)this.currentSwisSQLStatement).setExcelFlag(this.isExcel);
               sqs = (SelectQueryStatement)this.currentSwisSQLStatement;
               sqs.setSQLDialect(20);
            }

            s_sql = this.currentSwisSQLStatement.toExcelString();
            break;
         case 21:
            if (setDatatypeMapping) {
               this.setDatatypeMappingForSQLDialect(this.currentSwisSQLStatement, 21);
            }

            if (this.currentSwisSQLStatement instanceof SelectQueryStatement) {
               ((SelectQueryStatement)this.currentSwisSQLStatement).setMsAccessJdbcFlag(this.isExcel);
               sqs = (SelectQueryStatement)this.currentSwisSQLStatement;
               sqs.setSQLDialect(21);
            }

            s_sql = this.currentSwisSQLStatement.toMsAccessJdbcString();
         }

         return enable_indent ? s_sql : this.currentSwisSQLStatement.removeIndent(s_sql);
      }
   }

   private void setDatatypeMappingForSQLDialect(SwisSQLStatement sqs, int dialect) {
      DatatypeMapping mapping = (DatatypeMapping)this.dbCumDatatypeMapping.get(new Integer(dialect));
      if (mapping == null) {
         mapping = (DatatypeMapping)this.dbCumDatatypeMapping.get(new Integer(9));
      }

      if (sqs instanceof CreateQueryStatement) {
         ((CreateQueryStatement)sqs).setDatatypeMapping(mapping);
      } else if (sqs instanceof SelectQueryStatement) {
         ((SelectQueryStatement)sqs).setDatatypeMapping(mapping);
      }

   }

   public synchronized SwisSQLStatement parse() throws ParseException, ConvertException {
      SwisSQLStatement sss = this.vembuParser.CompilationUnit();
      this.objectNames = this.vembuParser.getObjectNames();
      return sss;
   }

   public synchronized SwisSQLStatement parseFormulas() throws ParseException, ConvertException {
      SwisSQLStatement sss = this.vembuParser.CompilationUnitForFormulas();
      this.objectNames = this.vembuParser.getObjectNames();
      return sss;
   }

   private void resetStaticVariables() {
      SelectQueryStatement.singleQueryConvertedToMultipleQueryList = null;
      SelectQueryStatement.setBeautyTabCount(0);
      this.resetTruncateVariables();
   }

   private void resetTruncateVariables() {
      if (truncateTableCount > 99) {
         truncateTableCount = 0;
      }

      if (truncateIndexCount > 99) {
         truncateIndexCount = 0;
      }

      if (truncateConstraintCount > 99) {
         truncateConstraintCount = 0;
      }

   }

   public void getMetaData() {
      try {
         MetaDataUtility mUtil = new MetaDataUtility();
         Vector outputStrings = new Vector();
         mUtil.getMetaData(outputStrings);
         this.loadMetaData(mUtil.getDestinationFile());
      } catch (SQLException var3) {
         System.out.println(" Error Code : " + var3.getErrorCode() + ". Please verify the Connection URL. Proceeding without loading metdata...");
      } catch (Exception var4) {
         System.out.println(" Problem in fetching/loading metadata. Proceeding without loading metdata...");
      }

   }

   public void getMetaData(MetaDataProperties property) {
      try {
         MetaDataUtility mUtil = new MetaDataUtility(property);
         Vector outputStrings = new Vector();
         mUtil.getMetaData(outputStrings);
         this.loadMetaData(mUtil.getDestinationFile());
      } catch (SQLException var4) {
         System.out.println(" Error Code : " + var4.getErrorCode() + ". Please verify the Connection URL. Proceeding without loading metdata...");
      } catch (Exception var5) {
         System.out.println(" Problem in fetching/loading metadata. Proceeding without loading metdata...");
      }

   }

   public void getMetaData(Connection con, MetaDataProperties property) {
      try {
         MetaDataUtility mUtil = new MetaDataUtility(con, property);
         Vector outputStrings = new Vector();
         mUtil.getMetaData(outputStrings);
         this.loadMetaData(mUtil.getDestinationFile());
      } catch (SQLException var5) {
         System.out.println(" Error Code : " + var5.getErrorCode() + ". Please verify the Connection URL. Proceeding without loading metdata...");
         var5.printStackTrace();
      } catch (Exception var6) {
         System.out.println(" Problem in fetching/loading metadata. Proceeding without loading metdata...");
      }

   }

   public void loadMetaData(String fileName) {
      try {
         FileInputStream fis = new FileInputStream(fileName);
         InputStreamReader isr = new InputStreamReader(fis);
         this.loadMetaData(isr);
         isr.close();
         fis.close();
      } catch (FileNotFoundException var4) {
         System.out.println(" LoadMetaData : File not found " + fileName + ". Proceeding with default handling...");
      } catch (IOException var5) {
         System.out.println(" LoadMetaData : GetMetaData yet to be done. Proceeding with default handling...");
      } catch (Exception var6) {
         System.out.println(" LoadMetaData : GetMetaData yet to be done. Proceeding with default handling...");
      }

   }

   public void loadMetaData(InputStreamReader isr) throws IOException {
      threadLocalMetaDataHm = new ThreadLocal();
      primaryKeyMetaData = new Hashtable();
      BufferedReader br = new BufferedReader(isr);
      String metadataString = br.readLine();

      while(metadataString != null) {
         String tablename = StringFunctions.getLastStrToken(metadataString, "=:").trim();
         metadataString = br.readLine();
         String columnname = StringFunctions.getLastStrToken(metadataString, "=:").trim();
         metadataString = br.readLine();
         String orgTypename = StringFunctions.getLastStrToken(metadataString, "=:").toLowerCase().trim();
         metadataString = br.readLine();
         String primaryKey = null;
         if (metadataString != null) {
            primaryKey = StringFunctions.getLastStrToken(metadataString, "=:").trim();
         }

         Hashtable orgTemp = new Hashtable();
         ArrayList columnList = new ArrayList();
         if (getDataTypesFromMetaDataHT().containsKey(tablename)) {
            orgTemp = (Hashtable)getDataTypesFromMetaDataHT().get(tablename);
            orgTemp.put(columnname, orgTypename);
            columnList = (ArrayList)getDataTypesFromMetaDataHT().get(tablename);
            columnList.add(columnname);
         } else {
            orgTemp.put(columnname, orgTypename);
            getDataTypesFromMetaDataHT().put(tablename, orgTemp);
            columnList.add(columnname);
            getTableColumnListMetadata().put(tablename, columnList);
         }

         if (primaryKey != null && primaryKey.equals("1")) {
            ArrayList tempCols;
            if (!primaryKeyMetaData.containsKey(tablename)) {
               tempCols = new ArrayList();
               tempCols.add(columnname);
               primaryKeyMetaData.put(tablename, tempCols);
            } else {
               tempCols = (ArrayList)primaryKeyMetaData.get(tablename);
               if (!tempCols.contains(columnname)) {
                  tempCols.add(columnname);
               }
            }
         }

         if (metadataString != null && metadataString.indexOf("PRIMARY_KEY=:") != -1) {
            metadataString = br.readLine();
         }
      }

   }

   public void setDatatypeMapping(int toDB, DatatypeMapping mapping) {
      this.dbCumDatatypeMapping.put(new Integer(toDB), mapping);
   }

   public void setDatatypeMapping(int toDB, String datatypeMappingFileName) {
      this.dbCumDatatypeMappingFile.put(new Integer(toDB), datatypeMappingFileName);

      try {
         this.setDatatypeMappingWithMappingObject(new Integer(toDB));
      } catch (IOException var4) {
         System.out.println(" Problem in loading datatype mapping from a file/Input stream. Proceeding with default handling ... ");
      } catch (Exception var5) {
         System.out.println(" Problem in loading datatype mapping from a file/Input stream. Proceeding with default handling ...");
         var5.printStackTrace();
      }

   }

   public void setDatatypeMapping(int toDB, InputStreamReader isr) {
      this.dbCumDatatypeMappingStream.put(new Integer(toDB), isr);

      try {
         this.setDatatypeMappingWithMappingObject(new Integer(toDB));
      } catch (IOException var4) {
         System.out.println(" Problem in loading datatype mapping from a file/Input stream. Proceeding with default handling ... ");
      } catch (Exception var5) {
         System.out.println(" Problem in loading datatype mapping from a file/Input stream. Proceeding with default handling ...");
         var5.printStackTrace();
      }

   }

   public static void setObjectsOwnerName(int targetDatabase, String ownerName) {
      objectsOwnerName.put(new Integer(targetDatabase), ownerName);
   }

   public static void setIdentityMapping(String fileName) {
      try {
         File f = new File(fileName);
         FileInputStream fstream = new FileInputStream(f);
         DataInputStream in = new DataInputStream(fstream);
         InputStreamReader isr = new InputStreamReader(in);
         setIdentityMapping(isr);
         isr.close();
         fstream.close();
         in.close();
      } catch (Exception var5) {
         var5.printStackTrace();
      }

   }

   public static void setIdentityMapping(InputStreamReader isr) {
      BufferedReader br = new BufferedReader(isr);

      try {
         String strLine;
         while((strLine = br.readLine()) != null) {
            if (!strLine.startsWith("#") && strLine.indexOf("=") != -1 && strLine.indexOf(".") != -1 && !strLine.startsWith("/*") && !strLine.startsWith("--")) {
               String[] datatype = strLine.trim().split("=");
               String temp = datatype[0].trim().toString();
               String identity = datatype[1].trim().toString();
               if (temp.indexOf(".") != -1) {
                  identityMapping.put(temp, identity);
               }
            }
         }

         br.close();
      } catch (IOException var6) {
         var6.printStackTrace();
      }

   }

   private void setDatatypeMappingWithMappingObject(Integer toDB) throws IOException, Exception {
      Enumeration enum1;
      String tab;
      if (this.dbCumDatatypeMappingStream.size() > 0) {
         enum1 = this.dbCumDatatypeMappingStream.keys();
         if (enum1 != null) {
            label93:
            while(true) {
               Object obj;
               do {
                  if (!enum1.hasMoreElements()) {
                     break label93;
                  }

                  obj = enum1.nextElement();
               } while(!obj.equals(toDB));

               BufferedReader br = new BufferedReader((InputStreamReader)this.dbCumDatatypeMappingStream.get(obj));
               DatatypeMapping mapping = null;
               if (br != null) {
                  String mappingString = br.readLine();

                  for(mapping = new DatatypeMapping(); mappingString != null; mappingString = br.readLine()) {
                     if (mappingString.indexOf("=") != -1 && !mappingString.trim().startsWith("#")) {
                        StringTokenizer stringTokenizer = new StringTokenizer(mappingString, "=");
                        String tabCol = stringTokenizer.nextToken().trim();
                        String mapped = stringTokenizer.nextToken().trim();
                        if (tabCol.indexOf(".") == -1) {
                           mapping.addGlobalDatatypeMapping(tabCol, mapped);
                        } else {
                           StringTokenizer tabCols = new StringTokenizer(tabCol, ".");
                           tab = tabCols.nextToken().trim();
                           String col = tabCols.nextToken().trim();
                           mapping.addTableSpecificDatatypeMapping(tab, col, mapped);
                        }
                     }
                  }

                  br.close();
               }

               if (this.dbCumDatatypeMapping.containsKey(obj)) {
                  this.dbCumDatatypeMapping.remove(obj);
               }

               this.dbCumDatatypeMapping.put(obj, mapping);
            }
         }
      }

      if (this.dbCumDatatypeMappingFile.size() > 0) {
         enum1 = this.dbCumDatatypeMappingFile.keys();
         if (enum1 != null) {
            while(true) {
               DatatypeMapping mapping;
               Object obj;
               Properties props;
               Enumeration enumProp;
               do {
                  while(true) {
                     do {
                        if (!enum1.hasMoreElements()) {
                           return;
                        }

                        mapping = new DatatypeMapping();
                        obj = enum1.nextElement();
                     } while(!obj.equals(toDB));

                     String mappingFileName = this.dbCumDatatypeMappingFile.get(obj).toString();
                     File file = new File(mappingFileName);
                     if (file.exists()) {
                        FileInputStream str = new FileInputStream(file);
                        props = new Properties();
                        props.load(str);
                        enumProp = props.keys();
                        break;
                     }

                     System.out.println(mappingFileName + " file is not found ...");
                  }
               } while(enumProp == null);

               while(enumProp.hasMoreElements()) {
                  String key = (String)enumProp.nextElement();
                  tab = (String)props.get(key);
                  if (key.indexOf(".") == -1) {
                     mapping.addGlobalDatatypeMapping(key, tab);
                  } else {
                     StringTokenizer tabCol = new StringTokenizer(key, ".");
                     String tab = tabCol.nextToken().trim();
                     String col = tabCol.nextToken().trim();
                     mapping.addTableSpecificDatatypeMapping(tab, col, tab);
                  }
               }

               if (this.dbCumDatatypeMapping.containsKey(obj)) {
                  this.dbCumDatatypeMapping.remove(obj);
               }

               this.dbCumDatatypeMapping.put(obj, mapping);
            }
         }
      }

   }

   public void loadObjectNameMapping(String objectNameMappingFileName) {
      try {
         FileInputStream fis = new FileInputStream(objectNameMappingFileName);
         InputStreamReader isr = new InputStreamReader(fis);
         this.loadObjectNameMapping(isr);
         isr.close();
         fis.close();
      } catch (FileNotFoundException var4) {
         System.out.println(" objectName mapping file not found " + objectNameMappingFileName + ". Proceeding with default handling...");
      } catch (Exception var5) {
         System.out.println(" Exception in loading the object name mapping . Proceeding with default handling...");
      }

   }

   public void loadObjectNameMapping(InputStreamReader objectNameMappingStreamReader) {
      try {
         BufferedReader br = new BufferedReader(objectNameMappingStreamReader);

         for(String mappingString = br.readLine(); mappingString != null; mappingString = br.readLine()) {
            StringTokenizer stringTokenizer = new StringTokenizer(mappingString, "=:");
            String sourceObjName = "";
            String targetObjName = "";
            if (stringTokenizer.hasMoreTokens()) {
               sourceObjName = stringTokenizer.nextToken().trim();
               if (stringTokenizer.hasMoreTokens()) {
                  targetObjName = stringTokenizer.nextToken().trim();
               }

               SwisSQLUtils.objectNameMapping.put(sourceObjName, targetObjName);
            }
         }
      } catch (IOException var7) {
         var7.printStackTrace();
      }

   }

   public void writeObjectNameMappingToFile(String objectNameMappingFileName) {
      if (SwisSQLUtils.objectNameMapping.size() != 0) {
         File conf = new File(objectNameMappingFileName);
         FileWriter fw = null;
         BufferedWriter bw = null;

         try {
            fw = new FileWriter(conf);
            bw = new BufferedWriter(fw);
            Enumeration keysEnum = SwisSQLUtils.objectNameMapping.keys();
            Enumeration valuesEnum = SwisSQLUtils.objectNameMapping.elements();

            while(keysEnum.hasMoreElements() && valuesEnum.hasMoreElements()) {
               String mapping = keysEnum.nextElement().toString() + ":=" + valuesEnum.nextElement().toString();
               bw.write(mapping);
               bw.newLine();
            }
         } catch (IOException var16) {
            var16.printStackTrace();
         } finally {
            try {
               if (bw != null) {
                  bw.close();
               }

               if (fw != null) {
                  fw.close();
               }
            } catch (IOException var15) {
               var15.printStackTrace();
            }

         }

      }
   }

   public SwisSQLStatement getCurrentSwisSQLStatement() {
      return this.currentSwisSQLStatement;
   }

   public void loadColumnDatatype(String fileName) throws IOException {
      try {
         FileInputStream fis = new FileInputStream(fileName);
         InputStreamReader isr = new InputStreamReader(fis);
         this.loadColumnDatatype(isr);
         isr.close();
         fis.close();
      } catch (FileNotFoundException var4) {
         System.out.println(" loadColumnDatatype : File not found " + fileName + ". Proceeding with default handling...");
      } catch (IOException var5) {
         System.out.println(" loadColumnDatatype : IOException. Proceeding with default handling...");
      } catch (Exception var6) {
         System.out.println(" loadColumnDatatype : Exception in loading column datatypes. Proceeding with default handling...");
      }

   }

   public void loadColumnDatatype(InputStreamReader isr) throws IOException {
      columnDatatypes = new Hashtable();
      BufferedReader br = new BufferedReader(isr);

      for(String metadataString = br.readLine(); metadataString != null; metadataString = br.readLine()) {
         String[] split = metadataString.split("=:");
         String columnname = split[0];
         String datatype = split[1];
         columnDatatypes.put(columnname, datatype);
      }

   }

   public static void setTargetDBFunctionMappingFile(String filename) {
      targetDBFunctionMappingFile = filename;
   }

   public static void addFunctionNameMapping(String originalFunctionName, String mappedFunctionName) {
      targetDBMappedFunctionNames.put(originalFunctionName.trim().toUpperCase(), mappedFunctionName.trim());
   }

   public void loadFunctionNameMapping(String filename) {
      try {
         FileInputStream functionMap = new FileInputStream(filename);
         InputStreamReader isr = new InputStreamReader(functionMap);
         this.loadFunctionNameMapping(isr);
         isr.close();
         functionMap.close();
      } catch (FileNotFoundException var4) {
         System.out.println("LoadFunctionMapping : File " + filename + " Not Found : Proceeding with Default configurations");
      } catch (IOException var5) {
         System.out.println("LoadFunctionMapping : IO Exception Occured : Proceeding with default configurations");
      } catch (Exception var6) {
         System.out.println("LoadFunctionMapping : Exception while loading function mappings" + var6.getMessage());
      }

   }

   public void loadFunctionNameMapping(InputStreamReader isr) throws IOException {
      targetDBMappedFunctionNames = new HashMap();
      BufferedReader reader = new BufferedReader(isr);

      for(String functionMap = reader.readLine(); functionMap != null; functionMap = reader.readLine()) {
         if (!functionMap.trim().equals("") && !functionMap.startsWith("#")) {
            String[] functionNames = functionMap.split(":=");
            if (functionNames.length == 2) {
               addFunctionNameMapping(functionNames[0], functionNames[1]);
            }
         }
      }

   }

   public String getBuildID() {
      return "5.0_OCT_09_2009";
   }

   public void setInvolvedTablesNeeded() {
      SelectInvolvedTables tl = (SelectInvolvedTables)involvedTablesTL.get();
      tl.involvedTables.clear();
      tl.isNeeded = true;
   }

   public Set getInvolvedTables() {
      SelectInvolvedTables tl = (SelectInvolvedTables)involvedTablesTL.get();
      return tl.involvedTables;
   }

   public void removeTL() {
      involvedTablesTL.remove();
   }

   public static void setProperSelExpTL() {
      properSelectExpression.set(true);
   }

   public static boolean getProperSelExp() {
      return (Boolean)properSelectExpression.get();
   }

   public static void clearProperSelExpTL() {
      properSelectExpression.remove();
   }

   public static boolean getCanFullJoinExecute() {
      return (Boolean)fullJoinExecute.get();
   }

   public static void setCanFullJoinExecute() {
      fullJoinExecute.set(true);
   }

   public static void clearFullJoinExecute() {
      fullJoinExecute.remove();
   }

   public static void updateRemoveLimitAndFetchClauseStatus(boolean status) {
      removeLimitAndFetchClause.set(status);
   }

   public static boolean canClearLimitAndFetchClauses() {
      return (Boolean)((Boolean)removeLimitAndFetchClause.get());
   }

   public static void clearIndexPositions() {
      Set tl = (Set)((Set)indexPositionsForCastingTL.get());
      tl.clear();
   }

   public static void setIndexPositionsForCasting(int index) {
      Set tl = (Set)((Set)indexPositionsForCastingTL.get());
      tl.add(index);
   }

   public static void setIndexPositionsForCasting(Set indexList) {
      Set tl = (Set)((Set)indexPositionsForCastingTL.get());
      tl.addAll(indexList);
   }

   public static Set getIndexPositionsForCasting() {
      return (Set)((Set)indexPositionsForCastingTL.get());
   }

   public static void removeIndexPositionsTL() {
      indexPositionsForCastingTL.remove();
   }

   public void setholidayTable(String Table_name, String Col_name) {
      LinkedHashSet tl = (LinkedHashSet)((LinkedHashSet)holidayTable.get());
      tl.add(Table_name);
      tl.add(Col_name);
   }

   public LinkedHashSet getholidayTable() {
      return (LinkedHashSet)((LinkedHashSet)holidayTable.get());
   }

   public static void setFnWhiteList(Object[][] fnList) {
      Map fnWhiteListMap_unm = new HashMap();

      for(int i = 0; i < fnList.length; ++i) {
         fnWhiteListMap_unm.put(fnList[i][0], fnList[i]);
      }

      Map fnWhiteListMap_unm = Collections.unmodifiableMap(fnWhiteListMap_unm);
      fnWhiteListMap = fnWhiteListMap_unm;
   }

   public static void clearNULLIndexPositions() {
      Set tl = (Set)((Set)indexPositionsForNULLTL.get());
      tl.clear();
   }

   public static void setNULLIndexPositionsForCasting(int index) {
      Set tl = (Set)((Set)indexPositionsForNULLTL.get());
      tl.add(index);
   }

   public static void removeNULLIndexPositionsForCasting(int index) {
      Set tl = (Set)((Set)indexPositionsForNULLTL.get());
      tl.remove(index);
   }

   public static Set getNULLIndexPositionsForCasting() {
      return (Set)((Set)indexPositionsForNULLTL.get());
   }

   public static void removeNULLIndexPositionsTL() {
      indexPositionsForNULLTL.remove();
   }

   public static boolean containsIndexForNULL(int indexPosition) {
      return ((Set)((Set)indexPositionsForNULLTL.get())).contains(indexPosition);
   }

   public void updateSwisOptionsToSQS(SelectQueryStatement sqs) {
      if (sqs != null) {
         sqs.setAmazonRedShiftFlag(this.isAmazonRedShift);
         sqs.setPgsqlFlag(this.isPgsqlLive);
         sqs.setMSAzureFlag(this.isMSAzure);
         sqs.setMSAzureWareHouseFlag(this.isMSAzureWareHouse);
         sqs.setOracleLiveFlag(this.isOracleLive);
         sqs.setBigQueryFlag(this.isBigQuery);
         sqs.setMySqlLiveFlag(this.isMySqlLive);
         sqs.setSnowflakeFlag(this.isSnowflake);
         sqs.setAthenaFlag(this.isAthena);
         sqs.setHandlers(this.convPropHandler, this.queryConvDataHandler, this.validationHandler);
      }

   }

   public void updateSwisHandlersToWithStmt(WithStatement wS) {
      if (wS != null) {
         wS.setHandlers(this.convPropHandler, this.queryConvDataHandler, this.validationHandler);
      }

   }

   public static ArrayList<String> getSQLKeyWordsAsTblCol() {
      ArrayList<String> keyWordsAsTblColList = new ArrayList();
      keyWordsAsTblColList.add("year");
      keyWordsAsTblColList.add("quarter");
      keyWordsAsTblColList.add("month");
      keyWordsAsTblColList.add("week");
      keyWordsAsTblColList.add("day");
      keyWordsAsTblColList.add("hour");
      keyWordsAsTblColList.add("minute");
      keyWordsAsTblColList.add("second");
      keyWordsAsTblColList.add("microsecond");
      return keyWordsAsTblColList;
   }

   public static HashMap<String, String> getUDFFunctionMap() {
      HashMap<String, String> udfFunctionMap = new HashMap();
      udfFunctionMap.put("ZR_FYEARDT", "year");
      udfFunctionMap.put("ZR_FQUARTERDT", "quarter");
      udfFunctionMap.put("ZR_BUSINESS_ENDDAY", "business_completion_day");
      udfFunctionMap.put("ZR_BUSINESS_DAYS", "business_days");
      udfFunctionMap.put("ZR_BUSINESS_HOURS", "business_hours");
      udfFunctionMap.put("ZR_BUSINESS_MINUTES", "business_minutes");
      udfFunctionMap.put("ZR_ISLASTMONTH", "islast_nmonth");
      udfFunctionMap.put("ZR_ISLASTQUARTER", "islast_nquarter");
      udfFunctionMap.put("ZR_ISNEXTMONTH", "isnext_nmonth");
      udfFunctionMap.put("ZR_ISNEXTQUARTER", "isnext_nquarter");
      udfFunctionMap.put("ZR_ISPREVIOUSMONTH", "isprevious_nmonth");
      udfFunctionMap.put("ZR_ISPREVIOUSQUARTER", "isprevious_nquarter");
      udfFunctionMap.put("ZR_ISPREVIOUSWEEK", "ispreviousweek");
      udfFunctionMap.put("ZR_ISCURRENTWEEK", "iscurrentweek");
      udfFunctionMap.put("ZR_ISNEXTWEEK", "isnextweek");
      udfFunctionMap.put("ZR_SECTOTIME", "sec_to_time");
      udfFunctionMap.put("ZR_TEXTBETWEEN", "substring_between");
      return udfFunctionMap;
   }

   public static Map getIntervalValueMap() {
      Map<String, String> mssqlIntervalValues = new HashMap();
      mssqlIntervalValues.put("year", "year");
      mssqlIntervalValues.put("yy", "year");
      mssqlIntervalValues.put("yyyy", "year");
      mssqlIntervalValues.put("quarter", "quarter");
      mssqlIntervalValues.put("qq", "quarter");
      mssqlIntervalValues.put("q", "quarter");
      mssqlIntervalValues.put("month", "month");
      mssqlIntervalValues.put("mm", "month");
      mssqlIntervalValues.put("m", "month");
      mssqlIntervalValues.put("dayofyear", "day");
      mssqlIntervalValues.put("dy", "day");
      mssqlIntervalValues.put("y", "day");
      mssqlIntervalValues.put("day", "day");
      mssqlIntervalValues.put("dd", "day");
      mssqlIntervalValues.put("d", "day");
      mssqlIntervalValues.put("week", "week");
      mssqlIntervalValues.put("wk", "week");
      mssqlIntervalValues.put("ww", "week");
      mssqlIntervalValues.put("weekday", "week");
      mssqlIntervalValues.put("dw", "week");
      mssqlIntervalValues.put("w", "week");
      mssqlIntervalValues.put("hour", "hour");
      mssqlIntervalValues.put("hh", "hour");
      mssqlIntervalValues.put("minute", "minute");
      mssqlIntervalValues.put("mi", "minute");
      mssqlIntervalValues.put("n", "minute");
      mssqlIntervalValues.put("second", "second");
      mssqlIntervalValues.put("ss", "second");
      mssqlIntervalValues.put("s", "second");
      mssqlIntervalValues.put("millisecond", "microsecond");
      mssqlIntervalValues.put("ms", "microsecond");
      mssqlIntervalValues.put("microsecond", "microsecond");
      mssqlIntervalValues.put("mcs", "microsecond");
      mssqlIntervalValues.put("nanosecond", "microsecond");
      mssqlIntervalValues.put("ns", "microsecond");
      return mssqlIntervalValues;
   }

   public static Pattern getPatternToUnEscapeBackSlash(String regexStr, boolean useDotAll) {
      return useDotAll ? Pattern.compile("(.)*" + regexStr + "(.)*", 32) : Pattern.compile("[\\s\\S]*" + regexStr + "[\\s\\S]*");
   }

   public static String unEscapeBackSlash(String sql, boolean useDotAll) {
      String sqlNew = sql;

      try {
         if (getPatternToUnEscapeBackSlash("\\\\\\\\_", useDotAll).matcher(sqlNew).matches()) {
            sqlNew = sqlNew.replaceAll("\\\\\\\\_", "\\\\_");
         } else if (getPatternToUnEscapeBackSlash("\\\\_", useDotAll).matcher(sqlNew).matches()) {
            sqlNew = sqlNew.replaceAll("\\\\_", "_");
         }

         if (getPatternToUnEscapeBackSlash("\\\\\\\\\"", useDotAll).matcher(sqlNew).matches()) {
            sqlNew = sqlNew.replaceAll("\\\\\\\\\"", "\\\\\"");
         } else if (getPatternToUnEscapeBackSlash("\\\\\"", useDotAll).matcher(sqlNew).matches()) {
            sqlNew = sqlNew.replaceAll("\\\\\"", "\"");
         }

         if (getPatternToUnEscapeBackSlash("\\\\\\\\", useDotAll).matcher(sqlNew).matches()) {
            sqlNew = sqlNew.replaceAll("\\\\\\\\", "\\\\");
         }

         return sqlNew;
      } catch (Exception var4) {
         return sql;
      }
   }

   public static ArrayList<String> getPostgresSqlTimeUnits() {
      ArrayList<String> timeUnits = new ArrayList();
      timeUnits.add("millennium");
      timeUnits.add("century");
      timeUnits.add("decade");
      timeUnits.add("year");
      timeUnits.add("quarter");
      timeUnits.add("month");
      timeUnits.add("week");
      timeUnits.add("day");
      timeUnits.add("hour");
      timeUnits.add("minute");
      timeUnits.add("second");
      timeUnits.add("millisecond");
      timeUnits.add("microsecond");
      return timeUnits;
   }

   public static void setMetaDataInThreadLocal(String key, Object value) {
      HashMap metadata = new HashMap();
      if (threadLocalMetaDataHm.get() != null) {
         metadata = (HashMap)threadLocalMetaDataHm.get();
      }

      metadata.put(key, value);
      threadLocalMetaDataHm.set(metadata);
   }
}
