package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.config.datatypes.DatatypeMapping;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;
import com.adventnet.swissqlapi.sql.statement.create.CharacterClass;
import com.adventnet.swissqlapi.sql.statement.create.CreateColumn;
import com.adventnet.swissqlapi.sql.statement.create.CreateQueryStatement;
import com.adventnet.swissqlapi.sql.statement.create.CreateSequenceStatement;
import com.adventnet.swissqlapi.sql.statement.create.Datatype;
import com.adventnet.swissqlapi.sql.statement.create.DateClass;
import com.adventnet.swissqlapi.sql.statement.create.NumericClass;
import com.adventnet.swissqlapi.sql.statement.drop.DropStatement;
import com.adventnet.swissqlapi.sql.statement.insert.CommonTableExpression;
import com.adventnet.swissqlapi.sql.statement.insert.InsertClause;
import com.adventnet.swissqlapi.sql.statement.insert.InsertQueryStatement;
import com.adventnet.swissqlapi.sql.statement.update.HintClause;
import com.adventnet.swissqlapi.sql.statement.update.OptionalHintClause;
import com.adventnet.swissqlapi.sql.statement.update.OptionalSpecifier;
import com.adventnet.swissqlapi.sql.statement.update.TableClause;
import com.adventnet.swissqlapi.sql.statement.update.TableExpression;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import com.adventnet.swissqlapi.util.FunctionValidateHandler;
import com.adventnet.swissqlapi.util.InstanceDataTypeHandler;
import com.adventnet.swissqlapi.util.QueryConvDataHandler;
import com.adventnet.swissqlapi.util.QueryConvPropsHandler;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.util.database.MetadataInfoUtil;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class SelectQueryStatement implements SwisSQLStatement {
   private UserObjectContext objectContext = null;
   private String create_statement;
   private SelectStatement select_statement;
   private FromClause from_clause;
   private WhereExpression whereExpression;
   private OrderByStatement order_by_statement;
   private GroupByStatement group_by_statement;
   private HavingStatement having_statement;
   private IntoStatement into_statement;
   private SetOperatorClause set_operator_clause;
   private ForUpdateStatement for_update_statement;
   private HierarchicalQueryClause hierarchical_query_clause;
   private LimitClause limit_clause;
   private FetchClause fetch_clause;
   private OptionalHintClause optionalHintClause;
   private ProcessSelectQueryForHierarchicalClause processSelectQueryForStartWithConnectBy = null;
   private HintClause hintClause;
   private boolean reportsMeta = false;
   private int SQLDialect = 5;
   private String openBrace = null;
   private String closeBrace = null;
   private XMLStatement xmls;
   private InsertQueryStatement iqs = null;
   private String definitionOnly;
   private Vector computeByVector;
   private boolean commentForCompute = false;
   private boolean commentForOrderByStatement = false;
   private String generalComments;
   private String multipleQuery;
   private String atIsolation;
   private String isolationReadLevel;
   public static ThreadLocal<Integer> beautyTabCountTl = new ThreadLocal();
   public static String singleQueryConvertedToMultipleQueryList = null;
   private SelectQueryStatement subQuery = null;
   private DatatypeMapping mapping;
   private ArrayList lockTableStatements = new ArrayList();
   private ArrayList lockTableList = new ArrayList();
   private ArrayList insertValList = null;
   private String withString = null;
   private String isolationLevel = null;
   private CreateSequenceStatement sequenceForIdentotyFn = null;
   private WithStatement withStatement = null;
   private ArrayList createForSubQuery = new ArrayList();
   private ArrayList insertForSubQuery = new ArrayList();
   private ArrayList dropSttForSubQuery = new ArrayList();
   private int sybaseTopRowCount = -1;
   public boolean hasSubQuery = false;
   public boolean isStartWith = false;
   public String subQuery1 = null;
   public String funcName = null;
   private Hashtable startWithConnectByHashtable = null;
   private String aliasForSubQuery = null;
   private boolean isOlapFunctionPresent = false;
   private LinkedHashMap olapDerivedTables = new LinkedHashMap();
   private RownumClause rownumClause = null;
   private boolean isSetOperatorQuery = false;
   private HavingStatement qualifyStatement = null;
   private String teradataComment;
   private boolean isRownumColumnPresent;
   private Vector tableColumnList = new Vector();
   private Vector listOfWithStatements = new Vector();
   private boolean topLevel = false;
   private boolean converted = false;
   private boolean sumFunctionWithPartition = false;
   private LinkedHashMap sumDerivedTables = new LinkedHashMap();
   private LinkedHashMap sumfunc_SelectColumn_Alias_pair = new LinkedHashMap();
   private boolean isAmazonRedShift = false;
   private boolean isMSAzure = true;
   private boolean isBigQuery = false;
   private boolean isMSAzureWareHouse = false;
   private boolean isOracleLive = false;
   private boolean isMySqlLive = false;
   private boolean isPgsqlLive = false;
   private boolean isSnowflake = false;
   private boolean isAthena = false;
   private boolean isTrino = false;
   private boolean isMongoDb = false;
   private boolean isVerticaDb = false;
   private boolean isSapHanaDb = false;
   private boolean isSqlite = false;
   private boolean isDenodo = false;
   private boolean isExcel = false;
   private boolean isMsAccessJdbc = false;
   private boolean canUseIfForPGCaseWhenExp = false;
   private Set indexPositionsOfStringLiteralsSet = new HashSet();
   private Set indexPositionsOfNULLStringsSet = new HashSet();
   private int currentIndexPosition = -1;
   private boolean sqsWithSetOperatorClList = false;
   private boolean isUnPivotUnionSQS = false;
   private boolean canCastAllToTextColumns = false;
   private Set<Integer> coalesceFunctionIndexPositionSet = new HashSet();
   private Set<Integer> ifFunctionIndexPositionSet = new HashSet();
   private Map<String, String> aliasVsSelectColExpMap = new HashMap();
   private QueryConvPropsHandler queryConvPropHandler = null;
   private QueryConvDataHandler queryConvDataHandler = null;
   private FunctionValidateHandler validationHandler = null;
   private boolean canAllowExceptionStacking = false;
   private boolean canSkipExceptions = false;
   private boolean isOnlyFullGroupByModeEnabled = true;
   private ArrayList<String> pivotOperatorColumnList = null;
   private HashMap<String, CommonTableExpression> cteViewDetsMap = new HashMap();
   private ArrayList cteColumnList;
   private HashMap<String, String> tableDetsMap = new HashMap();
   private HashMap<String, HashMap<String, String>> tableColumnDetsMap = new HashMap();
   private boolean isSubQuerySource = false;
   private HashMap<String, String> selColNameMap = new HashMap();
   private boolean canConsiderPrecisionForTodecimal = false;
   private boolean canAllowPlusOperatorAsArthematicOperator = false;
   private boolean canAllowNewFlowForPlusOperatorCase = false;
   private boolean isQtNewFlow = false;
   private boolean canUseOracleFetch = true;
   private boolean canUseDatePartInsteadOfExtract = true;
   private boolean isAliasReferenceClausesIteration = false;
   private ArrayList<String> aliasColumns = new ArrayList();
   private InstanceDataTypeHandler instanceDataTypeHandler = null;
   private String instanceDatatype = "UNDEFINED";
   private boolean isCTESupported = false;
   private QueryConvPropsHandler queryConvPropsHandler = new QueryConvPropsHandler();
   private CommentClass commentObject;
   private LinkedList list = null;

   public void setCommentClass(CommentClass commentObject) {
      this.commentObject = commentObject;
   }

   public CommentClass getCommentClass() {
      return this.commentObject;
   }

   public void setReportsMeta(boolean reportsMeta) {
      this.reportsMeta = reportsMeta;
   }

   public boolean getReportsMeta() {
      return this.reportsMeta;
   }

   public void setTeradataComment(String comment) {
      this.teradataComment = comment;
   }

   public void setSQLDialect(int i_sqldialect) {
      this.SQLDialect = i_sqldialect;
   }

   public void setCreateStatement(String s_cs) {
      this.create_statement = s_cs;
   }

   public void setSelectStatement(SelectStatement s) {
      this.select_statement = s;
   }

   public void setFromClause(FromClause fc) {
      this.from_clause = fc;
   }

   public void setWhereExpression(WhereExpression we) {
      this.whereExpression = we;
   }

   public void setOrderByStatement(OrderByStatement ordst_os) {
      this.order_by_statement = ordst_os;
   }

   public void setGroupByStatement(GroupByStatement gpst_gbs) {
      this.group_by_statement = gpst_gbs;
   }

   public void setHavingStatement(HavingStatement havstat_hs) {
      this.having_statement = havstat_hs;
   }

   public void setIntoStatement(IntoStatement is_sis) {
      this.into_statement = is_sis;
   }

   public void setXMLStatements(XMLStatement xmls) {
      this.xmls = xmls;
   }

   public XMLStatement getXMLStatements() {
      return this.xmls;
   }

   public InstanceDataTypeHandler getinstanceDataTypeHandler() {
      return this.instanceDataTypeHandler;
   }

   public void setSetOperatorClause(SetOperatorClause sop_so) {
      this.set_operator_clause = sop_so;
   }

   public void setForUpdateStatement(ForUpdateStatement fus_fu) {
      this.for_update_statement = fus_fu;
   }

   public void setHierarchicalQueryClause(HierarchicalQueryClause hqc_hq) {
      this.hierarchical_query_clause = hqc_hq;
   }

   public void setLimitClause(LimitClause lc_l) {
      this.limit_clause = lc_l;
   }

   public void setFetchClause(FetchClause fc_f) {
      this.fetch_clause = fc_f;
   }

   public void setOpenBrace(String str) {
      this.openBrace = str;
   }

   public void setCloseBrace(String str) {
      this.closeBrace = str;
   }

   public void setOptionalHintClause(OptionalHintClause optionalHintClauseObj) {
      this.optionalHintClause = optionalHintClauseObj;
   }

   public void setDefinitionOnly(String definitionOnly) {
      this.definitionOnly = definitionOnly;
   }

   public void setInsertQueryStatement(InsertQueryStatement iqs) {
      this.iqs = iqs;
   }

   public void setComputeByStatements(Vector computeByVector) {
      this.computeByVector = computeByVector;
   }

   public void setCommentForCompute(boolean commentForCompute) {
      this.commentForCompute = commentForCompute;
   }

   public void setCommentForOrderByStatement(boolean commentForOrderByStatement) {
      this.commentForOrderByStatement = commentForOrderByStatement;
   }

   public void setSubQuery(SelectQueryStatement sqs) {
      this.subQuery = sqs;
   }

   public void setAliasForSubQuery(String s) {
      this.aliasForSubQuery = s;
   }

   public void setDatatypeMapping(DatatypeMapping mapping) {
      this.mapping = mapping;
   }

   public void setLockTableStatements(ArrayList lockTableStatements) {
      this.lockTableStatements = lockTableStatements;
   }

   public void addLockTableList(String lockTableStt) {
      this.lockTableList.add(lockTableStt);
   }

   public void setGeneralComments(String generalComments) {
      this.generalComments = generalComments;
   }

   public void setMultipleQuery(String multipleQuery) {
      this.multipleQuery = multipleQuery;
   }

   public void addCreateForSubQuery(CreateQueryStatement cqs) {
      this.createForSubQuery.add(cqs);
   }

   public void addInsertForSubQuery(InsertQueryStatement iqs) {
      this.insertForSubQuery.add(iqs);
   }

   public void addDropSttForSubQuery(DropStatement dropStt) {
      this.dropSttForSubQuery.add(dropStt);
   }

   public void setAtIsolation(String atIsolation) {
      this.atIsolation = atIsolation;
   }

   public void setIsolationReadLevel(String isolationReadLevel) {
      this.isolationReadLevel = isolationReadLevel;
   }

   public void setOlapFunctionPresent(boolean present) {
      this.isOlapFunctionPresent = present;
   }

   public void addOlapDerivedTables(String partitionKey, FromTable ft) {
      this.olapDerivedTables.put(partitionKey, ft);
   }

   public void setRownumClause(RownumClause rownumClause) {
      this.rownumClause = rownumClause;
   }

   public void setRownumColumnPresent(boolean yes) {
      this.isRownumColumnPresent = yes;
   }

   public void setQualifyStatement(HavingStatement qualifyStatement) {
      this.qualifyStatement = qualifyStatement;
   }

   public void setSetOperatorQuery(boolean yes) {
      this.isSetOperatorQuery = yes;
   }

   public boolean isSetOperatorSelectQueryStatement() {
      return this.isSetOperatorQuery;
   }

   public void setSybaseTopRowCount(int i) {
      this.sybaseTopRowCount = i;
   }

   public void setHintClause(HintClause hintClause) {
      this.hintClause = hintClause;
   }

   public void setConverted(boolean queryConverted) {
      this.converted = queryConverted;
   }

   public void setAmazonRedShiftFlag(boolean isAmazonRedShift) {
      this.isAmazonRedShift = isAmazonRedShift;
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

   public void setPgsqlFlag(boolean isPgsqlLive) {
      this.isPgsqlLive = isPgsqlLive;
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

   public void setMsAccessJdbcFlag(boolean isMsAccessJdbc) {
      this.isMsAccessJdbc = isMsAccessJdbc;
   }

   public void setCanUseIFFunctionForPGCaseWhenExp(boolean canUseIFFunction) {
      this.canUseIfForPGCaseWhenExp = canUseIFFunction;
   }

   public void setSQSWithSetOperatorClList(boolean sqsWithSetClList) {
      this.sqsWithSetOperatorClList = sqsWithSetClList;
   }

   public boolean isSQSWithSetOperatorClList() {
      return this.sqsWithSetOperatorClList;
   }

   public void addCurrentIndexToCoalesceFunctionList() {
      if (this.currentIndexPosition != -1) {
         this.coalesceFunctionIndexPositionSet.add(this.currentIndexPosition);
      }

   }

   public void setCurrentIndexPosition(int indexPosition) {
      this.currentIndexPosition = indexPosition;
   }

   public int getCurrentIndexPosition() {
      return this.currentIndexPosition;
   }

   public void setCanAllowExceptionStacking(boolean canAllowExceptionStacking) {
      this.canAllowExceptionStacking = canAllowExceptionStacking;
   }

   public boolean canUseIFFunctionForPGCaseWhenExp() {
      return this.canUseIfForPGCaseWhenExp;
   }

   public void setCanSkipExceptions(boolean canSkipExceptions) {
      this.setCanAllowExceptionStacking(canSkipExceptions);
      this.canSkipExceptions = canSkipExceptions;
   }

   public void setCanUseDatePartInsteadOfExtract(boolean canAllow) {
      this.canUseDatePartInsteadOfExtract = canAllow;
   }

   public void setIsAliasReferenceClausesIteration(boolean isAliasReferenceClausesIteration) {
      this.isAliasReferenceClausesIteration = isAliasReferenceClausesIteration;
   }

   public void setIsUnPivotUnionSQS(boolean isUnPivotSQS) {
      this.isUnPivotUnionSQS = isUnPivotSQS;
   }

   public void setPivotOperatorColumnList(ArrayList<String> pivotClauseResColList) {
      this.pivotOperatorColumnList = pivotClauseResColList;
   }

   public void addCurrentIndexToIfFunctionList() {
      if (this.currentIndexPosition != -1) {
         this.ifFunctionIndexPositionSet.add(this.currentIndexPosition);
      }

   }

   public Set<Integer> getCoalesceFunctionIndexList() {
      return this.coalesceFunctionIndexPositionSet;
   }

   public Set<Integer> getIfFunctionListIndexList() {
      return this.ifFunctionIndexPositionSet;
   }

   public void setHandlers(QueryConvPropsHandler propsHandler, QueryConvDataHandler dataHandler, FunctionValidateHandler funValHandler) {
      this.queryConvPropHandler = propsHandler;
      this.queryConvDataHandler = dataHandler;
      this.validationHandler = funValHandler;
   }

   public void setQueryConversionPropHandler(QueryConvPropsHandler handler) {
      this.queryConvPropHandler = handler;
   }

   public void setQueryConvDataHandler(QueryConvDataHandler handler) {
      this.queryConvDataHandler = handler;
   }

   public void setValidationHandler(FunctionValidateHandler handler) {
      this.validationHandler = handler;
   }

   public void setInstanceDatatype(String instanceDatatype) {
      this.instanceDatatype = instanceDatatype;
   }

   public void setInstanceDataTypeHandler(InstanceDataTypeHandler handler) {
      this.instanceDataTypeHandler = handler;
   }

   public void setCTEViewDetsMap(HashMap cteViewDetsMap) {
      this.cteViewDetsMap = cteViewDetsMap;
   }

   public HashMap getCTEViewDetsMap() {
      return this.cteViewDetsMap;
   }

   public void setCTEColumnList(ArrayList cteColumnList) {
      this.cteColumnList = cteColumnList;
   }

   public ArrayList getCTEColumnList() {
      return this.cteColumnList;
   }

   public Map<String, String> getAliasVsSelectColExpMap() {
      return this.aliasVsSelectColExpMap;
   }

   public boolean isAmazonRedShift() {
      return this.isAmazonRedShift;
   }

   public boolean isMSAzure() {
      return this.isMSAzure;
   }

   public boolean isMSAzureWareHouse() {
      return this.isMSAzureWareHouse;
   }

   public boolean isOracleLive() {
      return this.isOracleLive;
   }

   public boolean isBigQueryLive() {
      return this.isBigQuery;
   }

   public boolean isMySqlLive() {
      return this.isMySqlLive;
   }

   public boolean isPgsqlLive() {
      return this.isPgsqlLive;
   }

   public boolean isSnowflake() {
      return this.isSnowflake;
   }

   public boolean isAthena() {
      return this.isAthena;
   }

   public boolean isTrino() {
      return this.isTrino;
   }

   public boolean isMongoDb() {
      return this.isMongoDb;
   }

   public boolean isVerticaDb() {
      return this.isVerticaDb;
   }

   public boolean isSapHanaDb() {
      return this.isSapHanaDb;
   }

   public boolean isSqlite() {
      return this.isSqlite;
   }

   public boolean isDenodo() {
      return this.isDenodo;
   }

   public boolean isHyperSql() {
      return this.getSQLDialect() == 19;
   }

   public boolean isMsAccess() {
      return this.getSQLDialect() == 21;
   }

   public boolean isExcel() {
      return this.isExcel;
   }

   public HintClause getHintClause() {
      return this.hintClause;
   }

   public int getSybaseTopRowCount() {
      return this.sybaseTopRowCount;
   }

   public ArrayList getLockTableList() {
      return this.lockTableList;
   }

   public String getCreateStatement() {
      return this.create_statement;
   }

   public SelectStatement getSelectStatement() {
      return this.select_statement;
   }

   public FromClause getFromClause() {
      return this.from_clause;
   }

   public WhereExpression getWhereExpression() {
      return this.whereExpression;
   }

   public OrderByStatement getOrderByStatement() {
      return this.order_by_statement;
   }

   public GroupByStatement getGroupByStatement() {
      return this.group_by_statement;
   }

   public HavingStatement getHavingStatement() {
      return this.having_statement;
   }

   public IntoStatement getIntoStatement() {
      return this.into_statement;
   }

   public SetOperatorClause getSetOperatorClause() {
      return this.set_operator_clause;
   }

   public ForUpdateStatement getForUpdateStatement() {
      return this.for_update_statement;
   }

   public HierarchicalQueryClause getHierarchicalQueryClause() {
      return this.hierarchical_query_clause;
   }

   public LimitClause getLimitClause() {
      return this.limit_clause;
   }

   public FetchClause getFetchClause() {
      return this.fetch_clause;
   }

   public int getSQLDialect() {
      return this.SQLDialect;
   }

   public SelectQueryStatement getSubQuery() {
      return this.subQuery;
   }

   public String getAliasForSubQuery() {
      return this.aliasForSubQuery;
   }

   public DatatypeMapping getDatatypeMapping() {
      return this.mapping;
   }

   public String getAtIsolation() {
      return this.atIsolation;
   }

   public String getIsolationReadLevel() {
      return this.isolationReadLevel;
   }

   public boolean isOlapFunctionPresent() {
      return this.isOlapFunctionPresent;
   }

   public RownumClause getRownumClause() {
      return this.rownumClause;
   }

   public HashMap getOlapDerivedTables() {
      return this.olapDerivedTables;
   }

   public HavingStatement getQualifyStatement() {
      return this.qualifyStatement;
   }

   public QueryConvPropsHandler getQueryConvPropHandler() {
      return this.queryConvPropHandler;
   }

   public QueryConvDataHandler getQueryConvDataHandler() {
      return this.queryConvDataHandler;
   }

   public FunctionValidateHandler getValidationHandler() {
      return this.validationHandler;
   }

   public boolean getCanAllowExceptionStacking() {
      return this.canAllowExceptionStacking;
   }

   public boolean getCanSkipExceptions() {
      return this.canSkipExceptions;
   }

   public boolean getIsAliasReferenceClausesIteration() {
      return this.isAliasReferenceClausesIteration;
   }

   public boolean isUnPivotUnionSQS() {
      return this.isUnPivotUnionSQS;
   }

   public ArrayList<String> getPivotOperatorColumnList() {
      return this.pivotOperatorColumnList;
   }

   public boolean isRownumColumnPresent() {
      return this.isRownumColumnPresent;
   }

   public void setInsertValList(ArrayList insertValList) {
      this.insertValList = insertValList;
   }

   public void setWithString(String w) {
      this.withString = w;
   }

   public void setIsolationLevel(String il) {
      this.isolationLevel = il;
   }

   public String getIsolationLevel() {
      return this.isolationLevel;
   }

   public void setSequenceForIdentityFn(CreateSequenceStatement sequenceForIdentotyFn) {
      this.sequenceForIdentotyFn = sequenceForIdentotyFn;
   }

   public CreateSequenceStatement getSequenceForIdentityFn() {
      return this.sequenceForIdentotyFn;
   }

   public void setWithStatement(WithStatement withStatement) {
      this.withStatement = withStatement;
   }

   public void setTopLevel(boolean top) {
      this.topLevel = top;
   }

   public boolean getTopLevel() {
      return this.topLevel;
   }

   public WithStatement getWithStatement() {
      return this.withStatement;
   }

   public Vector getListOfWithStatements() {
      return this.listOfWithStatements;
   }

   public void setStartWithConnectByHashtable(ProcessSelectQueryForHierarchicalClause hierarchicalClause) {
      this.processSelectQueryForStartWithConnectBy = hierarchicalClause;
   }

   public void addTableColumnToTableColumnList(TableColumn tcn) {
      this.tableColumnList.add(tcn);
   }

   public Vector getTableColumnList() {
      return this.tableColumnList;
   }

   public boolean isConverted() {
      return this.converted;
   }

   public void addSumDerivedTables(String key, FromTable ft) {
      this.sumDerivedTables.put(key, ft);
   }

   public void setSumFunctionWithPartitionAvailable(boolean setMe) {
      this.sumFunctionWithPartition = setMe;
   }

   public void addSumSelectColumnAlias(String key, String alias) {
      this.sumfunc_SelectColumn_Alias_pair.put(key, alias);
   }

   public HashMap getSumDerivedTables() {
      return this.sumDerivedTables;
   }

   public boolean isSumFunctionWithPartitionAvailable() {
      return this.sumFunctionWithPartition;
   }

   public HashMap getSumSelectColumn() {
      return this.sumfunc_SelectColumn_Alias_pair;
   }

   public void addTableDets(String inTblName, String dbTblName) {
      this.tableDetsMap.put(inTblName, dbTblName);
   }

   public void setTableDetsMap(HashMap tblDets) {
      this.tableDetsMap = tblDets;
   }

   public HashMap getTableDetsMap() {
      return this.tableDetsMap;
   }

   public void addTableColumnDets(String tableName, HashMap tblColDets) {
      this.tableColumnDetsMap.put(tableName, tblColDets);
   }

   public void setTableColumnDetsMap(HashMap tblColDets) {
      this.tableColumnDetsMap = tblColDets;
   }

   public HashMap getTableColumnDetsMap() {
      return this.tableColumnDetsMap;
   }

   public void setIsSubQuerySource(boolean isSubQrySrc) {
      this.isSubQuerySource = isSubQrySrc;
   }

   public void setIsCTESupported(boolean isCTESupported) {
      this.isCTESupported = isCTESupported;
   }

   public boolean isSubQuerySource() {
      return this.isSubQuerySource;
   }

   public boolean isCTESupported() {
      return this.isCTESupported;
   }

   public void addSelColNameMap(String inAliasName, String outAliasName) {
      this.selColNameMap.put(inAliasName, outAliasName);
   }

   public void setSelColNameMap(HashMap selColMap) {
      this.selColNameMap = selColMap;
   }

   public HashMap getSelColNameMap() {
      return this.selColNameMap;
   }

   public void setIsQtNewFlow(boolean bool) {
      this.isQtNewFlow = bool;
   }

   public boolean getIsQtNewFlow() {
      return this.isQtNewFlow;
   }

   public void setCanUseOracleFetch(boolean canUseOracleFetch) {
      this.canUseOracleFetch = canUseOracleFetch;
   }

   public boolean getCanUseOracleFetch() {
      return this.canUseOracleFetch;
   }

   public static int getBeautyTabCount() {
      return beautyTabCountTl.get() == null ? 0 : (Integer)beautyTabCountTl.get();
   }

   public static void setBeautyTabCount(int BeautyTabCount) {
      beautyTabCountTl.set(BeautyTabCount);
   }

   public ArrayList<String> getAliasColumns() {
      return this.aliasColumns;
   }

   public void setAliasColumns(ArrayList<String> aliasColumns) {
      this.aliasColumns = aliasColumns;
   }

   public String getInstanceDatatype() {
      return this.instanceDatatype;
   }

   private void replaceTableAliasInTableColumn(WhereColumn wc, String alias) throws ConvertException {
      Vector colExp = wc.getColumnExpression();

      for(int k = 0; k < colExp.size(); ++k) {
         Object obj = colExp.get(k);
         if (!(obj instanceof TableColumn)) {
            if (obj instanceof FunctionCalls) {
               Vector funcArgs = ((FunctionCalls)obj).getFunctionArguments();

               for(int j = 0; j < funcArgs.size(); ++j) {
                  if (funcArgs.get(j) instanceof SelectColumn) {
                     this.replaceTableAliasInTableColumn((SelectColumn)funcArgs.get(j), alias);
                  }
               }
            } else if (obj instanceof SelectColumn) {
               this.replaceTableAliasInTableColumn((SelectColumn)obj, alias);
            } else if (obj instanceof WhereColumn) {
               this.replaceTableAliasInTableColumn((WhereColumn)obj, alias);
            } else if (!(obj instanceof CaseStatement) && !(obj instanceof SelectQueryStatement) && obj instanceof String) {
            }
         } else {
            TableColumn tcnMod = (TableColumn)obj;
            TableColumn newTCN = tcnMod.toTeradataSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
            if (!tcnMod.getColumnName().equalsIgnoreCase("level") && !tcnMod.getColumnName().equalsIgnoreCase("\"level\"")) {
               newTCN.setTableName(alias);
            } else {
               newTCN.setTableName("PARENT1");
            }

            colExp.setElementAt(newTCN, k);
         }
      }

   }

   private void replaceTableAliasInTableColumn(SelectColumn sc, String alias) throws ConvertException {
      Vector colExp = sc.getColumnExpression();

      for(int k = 0; k < colExp.size(); ++k) {
         Object obj = colExp.get(k);
         if (!(obj instanceof TableColumn)) {
            if (obj instanceof FunctionCalls) {
               Vector funcArgs = ((FunctionCalls)obj).getFunctionArguments();

               for(int j = 0; j < funcArgs.size(); ++j) {
                  if (funcArgs.get(j) instanceof SelectColumn) {
                     this.replaceTableAliasInTableColumn((SelectColumn)funcArgs.get(j), alias);
                  }
               }
            } else if (obj instanceof SelectColumn) {
               this.replaceTableAliasInTableColumn((SelectColumn)obj, alias);
            } else if (!(obj instanceof CaseStatement) && !(obj instanceof SelectQueryStatement) && obj instanceof String) {
            }
         } else {
            TableColumn tcnMod = (TableColumn)obj;
            TableColumn newTCN = tcnMod.toTeradataSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
            if (!tcnMod.getColumnName().equalsIgnoreCase("level") && !tcnMod.getColumnName().equalsIgnoreCase("\"level\"")) {
               newTCN.setTableName(alias);
            } else {
               newTCN.setTableName("PARENT1");
            }

            colExp.setElementAt(newTCN, k);
         }
      }

   }

   private void replaceKeywordInTableColumn(SelectColumn sc) throws ConvertException {
      Vector colExp = sc.getColumnExpression();

      for(int k = 0; k < colExp.size(); ++k) {
         Object obj = colExp.get(k);
         if (!(obj instanceof TableColumn)) {
            if (obj instanceof FunctionCalls) {
               Vector funcArgs = ((FunctionCalls)obj).getFunctionArguments();

               for(int j = 0; j < funcArgs.size(); ++j) {
                  if (funcArgs.get(j) instanceof SelectColumn) {
                     this.replaceKeywordInTableColumn((SelectColumn)funcArgs.get(j));
                  }
               }
            } else if (obj instanceof SelectColumn) {
               this.replaceKeywordInTableColumn((SelectColumn)obj);
            } else if (!(obj instanceof CaseStatement) && !(obj instanceof SelectQueryStatement) && obj instanceof String) {
            }
         } else {
            TableColumn tcnMod = (TableColumn)obj;
            TableColumn newTCN = tcnMod.toTeradataSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
            if (tcnMod.getColumnName().equalsIgnoreCase("level") || tcnMod.getColumnName().equalsIgnoreCase("\"level\"")) {
               newTCN.setColumnName("1");
               newTCN.setTableName((String)null);
            }

            colExp.setElementAt(newTCN, k);
         }
      }

   }

   private void replaceTableAliasInWhereExpression(WhereExpression whereExp) throws ConvertException {
      Vector recursiveWhereItems = whereExp.getWhereItems();
      int wiSize = recursiveWhereItems.size();

      for(int wi = 0; wi < wiSize; ++wi) {
         Object obj = recursiveWhereItems.get(wi);
         if (obj instanceof WhereItem) {
            WhereItem whereItem = (WhereItem)obj;
            WhereColumn lft = whereItem.getLeftWhereExp();
            WhereColumn rft = whereItem.getRightWhereExp();
            if (whereItem.getOperator3() != null && whereItem.getOperator3().equalsIgnoreCase("PRIOR")) {
               whereItem.setOperator3((String)null);
               this.replaceTableAliasInTableColumn(rft, "PARENT1");
               this.replaceTableAliasInTableColumn(lft, "CHILD");
            } else if (whereItem.getOperator1() != null && whereItem.getOperator1().equalsIgnoreCase("PRIOR")) {
               whereItem.setOperator1((String)null);
               this.replaceTableAliasInTableColumn(lft, "PARENT1");
               this.replaceTableAliasInTableColumn(rft, "CHILD");
            }
         } else if (obj instanceof WhereExpression) {
            this.replaceTableAliasInWhereExpression((WhereExpression)obj);
         }
      }

   }

   public WithStatement convertHierarchical_queryToWithStatement(SelectQueryStatement to_sqs) throws ConvertException {
      WhereExpression startWithCond = this.hierarchical_query_clause.getStartWithCondition().toTeradataSelect(to_sqs, this);
      WhereExpression connectByCond = this.hierarchical_query_clause.getConnectByCondition().toTeradataSelect(to_sqs, this);
      Vector origSelectItems = to_sqs.getSelectStatement().getSelectItemList();
      SelectQueryStatement seedStmt = new SelectQueryStatement();
      SelectStatement seedSelect = new SelectStatement();
      seedSelect.setSelectClause("SELECT");
      Vector seedSelectItemsBase = new Vector();
      Vector selectItemsForWithSQS = new Vector();
      Vector seedSelectItems = new Vector();
      Vector tableColumnsAddedToSeedStmt = new Vector();

      int origSelectItemsSize;
      SelectColumn newSelectColumn;
      Vector newColumnExpression;
      for(origSelectItemsSize = 0; origSelectItemsSize < to_sqs.getTableColumnList().size(); ++origSelectItemsSize) {
         TableColumn tcn = (TableColumn)to_sqs.getTableColumnList().get(origSelectItemsSize);
         String tcnStr = tcn.toString();
         if (!tableColumnsAddedToSeedStmt.contains(tcnStr)) {
            newSelectColumn = new SelectColumn();
            newSelectColumn.setAliasName(tcn.getColumnName());
            newSelectColumn.setEndsWith(",");
            newColumnExpression = new Vector();
            if (!tcn.getColumnName().equalsIgnoreCase("LEVEL") && !tcn.getColumnName().equalsIgnoreCase("\"LEVEL\"")) {
               newColumnExpression.add(tcn);
            } else {
               newColumnExpression.add("1");
            }

            newSelectColumn.setColumnExpression(newColumnExpression);
            seedSelectItems.add(newSelectColumn);
            seedSelectItemsBase.add(newSelectColumn);
            tableColumnsAddedToSeedStmt.add(tcnStr);
         }
      }

      origSelectItemsSize = origSelectItems.size();

      int colExpi;
      for(int oi = 0; oi < origSelectItemsSize; ++oi) {
         SelectColumn sc = (SelectColumn)origSelectItems.get(oi);
         if (sc.getEndsWith() == null) {
            sc.setEndsWith(",");
         }

         newSelectColumn = new SelectColumn();
         if (sc.getAliasName() != null) {
            newSelectColumn.setAliasName(sc.getAliasName());
         } else if (sc.getColumnExpression().size() == 1 && sc.getColumnExpression().firstElement() instanceof TableColumn) {
            newSelectColumn.setAliasName(((TableColumn)sc.getColumnExpression().firstElement()).getColumnName());
         } else {
            newSelectColumn.setAliasName("ADV_ALIAS_" + oi);
         }

         newSelectColumn.setEndsWith(",");
         sc.setEndsWith((String)null);
         newColumnExpression = new Vector();
         String scStr = "";

         for(colExpi = 0; colExpi < sc.getColumnExpression().size(); ++colExpi) {
            scStr = scStr + sc.getColumnExpression().get(colExpi).toString() + " ";
         }

         if (scStr.indexOf("\"level\"") != -1) {
            scStr = scStr.replaceAll("\"level\"", "1");
         } else if (scStr.indexOf("\"LEVEL\"") != -1) {
            scStr = scStr.replaceAll("\"LEVEL\"", "1");
         } else if (scStr.indexOf("level") != -1) {
            scStr = scStr.replaceAll("level", "1");
         } else if (scStr.indexOf("LEVEL") != -1) {
            scStr = scStr.replaceAll("LEVEL", "1");
         }

         newColumnExpression.add(scStr);
         newSelectColumn.setColumnExpression(newColumnExpression);
         if (sc.getAliasName() == null) {
            sc.setAliasName(newSelectColumn.getAliasName());
         }

         if (sc.getColumnExpression().size() > 1 || sc.getColumnExpression().size() == 1 && !(sc.getColumnExpression().firstElement() instanceof TableColumn)) {
            seedSelectItems.add(newSelectColumn);
            seedSelectItemsBase.add(sc);
         }

         selectItemsForWithSQS.add(sc);
      }

      ((SelectColumn)seedSelectItems.lastElement()).setEndsWith((String)null);
      seedSelect.setSelectItemList(seedSelectItems);
      seedStmt.setSelectStatement(seedSelect);
      FromClause seedFC = new FromClause();
      seedFC.setFromClause("FROM");
      Vector seedFromItems = new Vector();
      seedFromItems.add(to_sqs.getFromClause());
      seedStmt.setFromClause(to_sqs.getFromClause());
      seedStmt.setWhereExpression(startWithCond);
      SelectQueryStatement recursiveStatement = new SelectQueryStatement();
      SelectStatement recursiveSelect = new SelectStatement();
      recursiveSelect.setSelectClause("SELECT");
      Vector recursiveSelectItems = new Vector();
      colExpi = seedSelectItemsBase.size();

      for(int oi = 0; oi < colExpi; ++oi) {
         SelectColumn sc = (SelectColumn)seedSelectItemsBase.get(oi);
         SelectColumn newSelectColumn = new SelectColumn();
         newSelectColumn.setAliasName(sc.getAliasName());
         newSelectColumn.setEndsWith(",");
         Vector newColumnExpression = new Vector();
         if (sc.getAliasName() == null || !sc.getAliasName().equalsIgnoreCase("LEVEL") && !sc.getAliasName().equalsIgnoreCase("\"LEVEL\"")) {
            newColumnExpression.addAll(sc.getColumnExpression());
         } else {
            newColumnExpression.add("1 + " + sc.getAliasName());
         }

         newSelectColumn.setColumnExpression(newColumnExpression);
         this.replaceTableAliasInTableColumn(newSelectColumn, "CHILD");
         recursiveSelectItems.add(newSelectColumn);
      }

      ((SelectColumn)recursiveSelectItems.lastElement()).setEndsWith((String)null);
      recursiveSelect.setSelectItemList(recursiveSelectItems);
      recursiveStatement.setSelectStatement(recursiveSelect);
      FromClause recursiveFromClause = new FromClause();
      recursiveFromClause.setFromClause("FROM");
      Vector recursiveFromItems = new Vector();
      FromTable parentTable = new FromTable();
      parentTable.setTableName("ADV_RECURSIVE");
      parentTable.setAliasName("PARENT1");
      recursiveFromItems.add(parentTable);
      FromTable childTable = new FromTable();
      FromClause to_sqsFC = to_sqs.getFromClause();
      if (to_sqsFC.getFromItemList().size() > 1) {
         throw new ConvertException("Hierarchical clause referring to multiple tables is not supported. \nPlease use Oracle's Subquery factoring clause for such queries. ");
      } else {
         FromTable origFT = (FromTable)to_sqsFC.getFromItemList().firstElement();
         childTable.setTableName(origFT.getTableName());
         childTable.setAliasName("CHILD");
         recursiveFromItems.add(childTable);
         recursiveFromClause.setFromItemList(recursiveFromItems);
         recursiveStatement.setFromClause(recursiveFromClause);
         this.replaceTableAliasInWhereExpression(connectByCond);
         recursiveStatement.setWhereExpression(connectByCond);
         SetOperatorClause withSOC = new SetOperatorClause();
         withSOC.setSelectQueryStatement(recursiveStatement);
         withSOC.setSetClause("UNION ALL");
         seedStmt.setSetOperatorClause(withSOC);
         WithStatement ws = new WithStatement();
         ws.setWith("WITH");
         CommonTableExpression cte = new CommonTableExpression();
         TableObject tableObject = new TableObject();
         tableObject.setTableName("ADV_RECURSIVE");
         cte.setViewName(tableObject);
         ArrayList columnNamesList = new ArrayList();
         columnNamesList.add("(");

         for(int si = 0; si < seedSelectItems.size(); ++si) {
            SelectColumn seedSC = (SelectColumn)seedSelectItems.get(si);
            columnNamesList.add(seedSC.getAliasName());
            if (si != seedSelectItems.size() - 1) {
               columnNamesList.add(",");
            }
         }

         columnNamesList.add(")");
         cte.setColumnList(columnNamesList);
         cte.setAs("AS");
         cte.setSelectQueryStatement(seedStmt);
         Vector cteList = new Vector();
         cteList.add(cte);
         ws.setCommonTableExpressionList(cteList);
         SelectQueryStatement withSQS = new SelectQueryStatement();
         SelectStatement withSelect = new SelectStatement();
         withSelect.setSelectClause("SELECT");
         Vector withSelectItems = new Vector();

         for(int e = 0; e < selectItemsForWithSQS.size(); ++e) {
            String obj = ((SelectColumn)selectItemsForWithSQS.get(e)).getAliasName();
            if (!obj.equalsIgnoreCase(",") && !obj.equalsIgnoreCase("(") && !obj.equalsIgnoreCase(")")) {
               SelectColumn sc = new SelectColumn();
               Vector v = new Vector();
               v.add(obj);
               sc.setColumnExpression(v);
               sc.setEndsWith(",");
               withSelectItems.add(sc);
            }
         }

         ((SelectColumn)withSelectItems.lastElement()).setEndsWith((String)null);
         withSelect.setSelectItemList(withSelectItems);
         withSQS.setSelectStatement(withSelect);
         FromClause withFC = new FromClause();
         withFC.setFromClause("FROM");
         Vector withFCItems = new Vector();
         FromTable withFT = new FromTable();
         withFT.setTableName("ADV_RECURSIVE");
         withFCItems.add(withFT);
         withFC.setFromItemList(withFCItems);
         withSQS.setFromClause(withFC);
         ws.setWithSQS(withSQS);
         return ws;
      }
   }

   public WithStatement convertHierarchical_queryToWithStatement() {
      String tableName = null;
      String columnName = null;
      FromClause fromclause = this.getFromClause();
      Vector tableItems = fromclause.getFromItemList();
      if (tableItems != null && tableItems.size() > 0) {
         FromTable fromTableName = (FromTable)tableItems.get(0);
         tableName = fromTableName.getTableName().toString();
      }

      SelectStatement selectStatement = this.getSelectStatement();
      Vector selectItemList = selectStatement.getSelectItemList();
      ArrayList newSelectItemsList = new ArrayList();

      for(int i = 0; i < selectItemList.size(); ++i) {
         columnName = selectItemList.get(i).toString();
         newSelectItemsList.add(columnName);
      }

      HierarchicalQueryClause hierarchyClause = this.getHierarchicalQueryClause();
      String startWithCondition = hierarchyClause.getStartWithCondition().toString();
      WhereExpression connectByCondition = hierarchyClause.getConnectByCondition();
      Vector connectByConditionVector = connectByCondition.getWhereItem();
      WhereItem connectByConditionChoose = (WhereItem)connectByConditionVector.get(0);
      String selectClause1 = " ";

      for(int i = 0; i < newSelectItemsList.size(); ++i) {
         Object obj_1 = newSelectItemsList.get(i);
         if (obj_1.toString().trim().equalsIgnoreCase("LEVEL")) {
            selectClause1 = selectClause1 + "1";
         } else if (obj_1.toString().trim().equalsIgnoreCase("LEVEL,")) {
            selectClause1 = selectClause1 + "1,";
         } else {
            selectClause1 = selectClause1 + " PARENT." + newSelectItemsList.get(i);
         }
      }

      String whereClause1 = " PARENT." + startWithCondition;
      String selectClause2 = "";

      for(int i = 0; i < newSelectItemsList.size(); ++i) {
         Object obj_1 = newSelectItemsList.get(i);
         if (obj_1.toString().trim().equalsIgnoreCase("LEVEL")) {
            selectClause2 = selectClause2 + "LEVEL+1";
         } else if (obj_1.toString().trim().equalsIgnoreCase("LEVEL,")) {
            selectClause2 = selectClause2 + "LEVEL+1,";
         } else {
            selectClause2 = selectClause2 + "CHILD." + newSelectItemsList.get(i);
         }
      }

      String whereClause2 = " CHILD.";
      String leftWhere = connectByConditionChoose.getLeftWhereExp().toString();
      String rightWhere = connectByConditionChoose.getRightWhereExp().toString();
      if (connectByConditionChoose.getOperator3() != null && connectByConditionChoose.getOperator3().toString().equalsIgnoreCase("PRIOR")) {
         whereClause2 = whereClause2 + leftWhere + " =" + " PARENT." + rightWhere;
      }

      if (connectByConditionChoose.getOperator1() == null) {
         return null;
      } else {
         if (connectByConditionChoose.getOperator1().toString().equalsIgnoreCase("PRIOR")) {
            whereClause2 = whereClause2 + rightWhere + " =" + " PARENT." + leftWhere;
         }

         WithStatement ws = new WithStatement();
         CommonTableExpression cte = new CommonTableExpression();
         ArrayList columnNamesList = new ArrayList();
         SetOperatorClause soc = new SetOperatorClause();
         WhereExpression we_1 = new WhereExpression();
         new WhereItem();
         new WhereColumn();
         WhereExpression we_2 = new WhereExpression();
         new WhereItem();
         Vector whereItems_1 = new Vector();
         Vector whereItems_2 = new Vector();
         ws.setWith("WITH");
         TableObject tableObject = new TableObject();
         tableObject.setTableName("ADV_RECURSIVE");
         cte.setViewName(tableObject);
         columnNamesList.add("(");
         Vector temp = this.getSelectStatement().getSelectItemList();

         for(int i = 0; i < temp.size(); ++i) {
            columnNamesList.add(temp.get(i));
         }

         columnNamesList.add(")");
         cte.setColumnList(columnNamesList);
         cte.setAs("As");
         SelectQueryStatement sqs = new SelectQueryStatement();
         Vector v1 = new Vector();
         Vector fromItems_1 = new Vector();
         Vector fromItems_2 = new Vector();
         SelectStatement ss = new SelectStatement();
         FromClause fc = new FromClause();
         v1.add(selectClause1);
         ss.setSelectItemList(v1);
         ss.setSelectClause("SELECT");
         sqs.setSelectStatement(ss);
         fc.setFromClause("FROM");
         FromTable ft1 = new FromTable();
         ft1.setTableName(tableName);
         ft1.setAliasName("PARENT");
         fromItems_1.add(ft1);
         fc.setFromItemList(fromItems_1);
         whereItems_1.add(whereClause1);
         we_1.setWhereItem(whereItems_1);
         sqs.setFromClause(fc);
         sqs.setWhereExpression(we_1);
         soc.setSetClause("UNION ALL");
         SelectQueryStatement sqs2 = new SelectQueryStatement();
         SelectStatement ss1 = new SelectStatement();
         FromClause fc2 = new FromClause();
         FromTable ft2 = new FromTable();
         FromTable ft3 = new FromTable();
         ss1.setSelectClause("SELECT");
         Vector v2 = new Vector();
         v2.add(selectClause2);
         ss1.setSelectItemList(v2);
         sqs2.setSelectStatement(ss1);
         ft2.setTableName("ADV_RECURSIVE");
         ft2.setAliasName("PARENT");
         fromItems_2.add(ft2);
         ft3.setTableName(tableName);
         ft3.setAliasName("CHILD");
         fromItems_2.add(ft3);
         fc2.setFromClause("FROM");
         fc2.setFromItemList(fromItems_2);
         sqs2.setFromClause(fc2);
         whereItems_2.add(whereClause2);
         we_2.setWhereItem(whereItems_2);
         sqs2.setWhereExpression(we_2);
         soc.setSelectQueryStatement(sqs2);
         sqs.setSetOperatorClause(soc);
         SelectQueryStatement with_sqs = new SelectQueryStatement();
         FromClause fc3 = new FromClause();
         Vector fromItems_3 = new Vector();
         fc3.setFromClause("FROM");
         FromTable ft4 = new FromTable();
         ft4.setTableName("ADV_RECURSIVE");
         with_sqs.setSelectStatement(this.getSelectStatement());
         cte.setSelectQueryStatement(sqs);
         Vector cteVector = new Vector();
         cteVector.add(cte);
         ws.setCommonTableExpressionList(cteVector);
         fromItems_3.add(ft4);
         fc3.setFromItemList(fromItems_3);
         with_sqs.setFromClause(fc3);
         WhereExpression we = this.getWhereExpression();
         with_sqs.setWhereExpression(this.getWhereExpression());
         with_sqs.setOrderByStatement(this.getOrderByStatement());
         with_sqs.setIntoStatement(this.getIntoStatement());
         ws.setWithSQS(with_sqs);
         return ws;
      }
   }

   public ProcessSelectQueryForHierarchicalClause handleStartWithConnectByClause() {
      Hashtable hashTable = new Hashtable();
      hashTable.put("$name$", "Anon");
      SelectStatement tsqlSelectStatement = this.getSelectStatement();
      Vector selectItems = tsqlSelectStatement.getSelectItemList();
      if (selectItems.size() == 1) {
         String columnName = null;
         String tableReference = null;
         String startWithAliasName = null;
         SelectColumn selectColumn = (SelectColumn)selectItems.get(0);
         Vector expression = selectColumn.getColumnExpression();
         if (expression.get(0) instanceof TableColumn) {
            TableColumn tableColumn = (TableColumn)expression.get(0);
            columnName = tableColumn.getColumnName();
            tableReference = tableColumn.getTableName();
            if (tableReference != null && !tableReference.trim().equals("")) {
               tableReference = tableReference.toLowerCase();
            }

            if (selectColumn.getAliasName() != null && !selectColumn.getAliasName().trim().equals("")) {
               startWithAliasName = selectColumn.getAliasName();
            } else {
               startWithAliasName = columnName;
            }
         }

         if (columnName != null) {
            hashTable.put("STARTWITH_DISTINCT_COLUMNAME", columnName);
         }

         if (tableReference != null) {
            hashTable.put("STARTWITH_DISTINCT_TABREF", tableReference);
         }

         if (startWithAliasName != null) {
            hashTable.put("STARTWITH_DISTINCT_ALIASNAME", startWithAliasName);
         }

         FromClause startwithtsqlFromClause = this.getFromClause();
         Vector startWithFromItems = startwithtsqlFromClause.getFromItemList();
         ArrayList startWithTableList = new ArrayList();
         ArrayList startWithAliasList = new ArrayList();
         if (startWithFromItems != null) {
            for(int i = 0; i < startWithFromItems.size(); ++i) {
               FromTable startWithtsqlFromTable = (FromTable)startWithFromItems.get(i);
               Object startWithTableName = startWithtsqlFromTable.getTableName();
               if (startWithTableName instanceof String) {
                  if (((String)startWithTableName).indexOf(46) != -1) {
                     startWithTableName = ((String)startWithTableName).substring(((String)startWithTableName).lastIndexOf(46) + 1);
                  }

                  startWithTableList.add((String)startWithTableName);
                  if (startWithtsqlFromTable.getAliasName() != null && !startWithtsqlFromTable.getAliasName().trim().equals("")) {
                     startWithAliasList.add(startWithtsqlFromTable.getAliasName().toLowerCase());
                  } else {
                     startWithAliasList.add(((String)startWithTableName).toLowerCase());
                  }
               }
            }

            hashTable.put("STARTWITH_TABLE_LIST", startWithTableList);
            hashTable.put("STARTWITH_TABLE_ALIAS_LIST", startWithAliasList);
         }
      }

      ProcessSelectQueryForHierarchicalClause processSelectQueryForHierarchicalClause = new ProcessSelectQueryForHierarchicalClause();
      hashTable.put("ORIGINALSWISSQLSTMT", this);
      processSelectQueryForHierarchicalClause.processSelectQueryWhenTreeIsEncountered(this, hashTable);
      this.startWithConnectByHashtable = processSelectQueryForHierarchicalClause.getStartWithConnectByHashtable();
      this.hasSubQuery = processSelectQueryForHierarchicalClause.hasSubQuery;
      this.subQuery1 = processSelectQueryForHierarchicalClause.subQuery;
      this.funcName = processSelectQueryForHierarchicalClause.funcName;
      return processSelectQueryForHierarchicalClause;
   }

   public boolean checkForPriorinHierarchyClause() {
      HierarchicalQueryClause hierarchyClause = this.getHierarchicalQueryClause();
      String startWithCondition = hierarchyClause.getStartWithCondition().toString();
      WhereExpression connectByCondition = hierarchyClause.getConnectByCondition();
      Vector connectByConditionVector = connectByCondition.getWhereItem();
      WhereItem connectByConditionChoose = (WhereItem)connectByConditionVector.get(0);
      return connectByConditionChoose.getOperator1() != null || connectByConditionChoose.getOperator2() != null || connectByConditionChoose.getOperator3() != null;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      int n;
      if (!SwisSQLUtils.swissqlMessageList.isEmpty()) {
         sb.append("/* SwisSQL Messages :\n");

         for(n = 0; n < SwisSQLUtils.swissqlMessageList.size(); ++n) {
            sb.append(SwisSQLUtils.swissqlMessageList.get(n).toString() + "\n");
         }

         sb.append("*/\n");
         SwisSQLUtils.swissqlMessageList.clear();
      }

      String returnString;
      if (this.commentObject != null) {
         returnString = this.commentObject.toString().trim();
         sb.append(returnString + "\n");
      }

      if (this.teradataComment != null) {
         sb.append(this.teradataComment + "\n");
      }

      if (this.hintClause != null) {
         sb.append(" " + this.hintClause);
      }

      int i;
      if (this.topLevel && this.getListOfWithStatements().size() > 0) {
         n = this.getListOfWithStatements().size();

         for(i = 0; i < n; ++i) {
            sb.append(((WithStatement)this.getListOfWithStatements().get(i)).toString() + "\n");
            if (i != n - 1) {
               sb.append(",");
            }
         }
      } else if (this.withStatement != null) {
         return this.withStatement.toString();
      }

      if (this.processSelectQueryForStartWithConnectBy != null && this.processSelectQueryForStartWithConnectBy.startWithConnectByHash != null) {
         this.startWithConnectByHashtable = this.processSelectQueryForStartWithConnectBy.startWithConnectByHash;
         returnString = this.processSelectQueryForStartWithConnectBy.funcName;
         String tempAnonFuncName = this.processSelectQueryForStartWithConnectBy.anonFunName;
         if (this.startWithConnectByHashtable.get(returnString) != null) {
            sb.append(this.startWithConnectByHashtable.get(returnString).toString());
            if (this.startWithConnectByHashtable.get(tempAnonFuncName) != null) {
               sb.append(this.startWithConnectByHashtable.get(tempAnonFuncName).toString());
            }
         }
      }

      if (this.getSybaseTopRowCount() != -1) {
         sb.append("SET ROWCOUNT " + this.getSybaseTopRowCount() + " \n");
      }

      for(n = 0; n < this.lockTableList.size(); ++n) {
         sb.append(this.lockTableList.get(n).toString() + ";\n");
      }

      for(n = 0; n < getBeautyTabCount(); ++n) {
         sb.append("\t");
      }

      if (this.singleQueryIntoMultipleQueriesForPLSQL() != null) {
         sb.append(this.singleQueryIntoMultipleQueriesForPLSQL());
         singleQueryConvertedToMultipleQueryList = null;
      }

      if (this.openBrace != null) {
         sb.append(this.openBrace);
      }

      if (this.sequenceForIdentotyFn != null) {
         try {
            sb.append(this.sequenceForIdentotyFn.toOracle().toString() + " \n/\n");
         } catch (Exception var4) {
         }
      }

      if (this.create_statement != null && this.select_statement == null) {
         sb.append(this.create_statement.toString().trim() + ";");
      } else if (this.create_statement != null) {
         sb.append(this.create_statement + " ");
      }

      if (this.createForSubQuery.size() > 0) {
         for(n = 0; n < this.createForSubQuery.size(); ++n) {
            sb.append(this.createForSubQuery.get(n).toString().trim() + ";\n\n");
         }
      }

      if (this.insertForSubQuery.size() > 0) {
         for(n = 0; n < this.insertForSubQuery.size(); ++n) {
            sb.append(this.insertForSubQuery.get(n).toString().trim() + ";\n\n");
         }
      }

      if (this.select_statement != null) {
         this.select_statement.setObjectContext(this.objectContext);
         sb.append(this.select_statement.toString());
      }

      if (this.into_statement != null) {
         this.into_statement.setObjectContext(this.objectContext);
         sb.append(" " + this.into_statement.toString());
      }

      sb.append("\n");
      if (this.from_clause != null && this.subQuery != null) {
         sb.append(" FROM\n");
      } else if (this.from_clause != null) {
         this.from_clause.setObjectContext(this.objectContext);
         sb.append(this.from_clause.toString() + " \n");
      }

      if (this.subQuery != null) {
         sb.append("(" + this.subQuery.toString() + ")");
      }

      if (this.aliasForSubQuery != null) {
         sb.append(" " + this.aliasForSubQuery + " ");
      }

      StringBuffer sb1;
      if (this.whereExpression != null) {
         sb1 = new StringBuffer();

         for(i = 0; i < getBeautyTabCount(); ++i) {
            sb.append("\t");
         }

         if (this.whereExpression.getConcatenation() != null) {
            sb1.append("+ ");
         }

         if (this.whereExpression.getCommentClass() != null) {
            sb1.append(this.whereExpression.getCommentClass().toString().trim() + " ");
         }

         sb1.append("WHERE");
         sb1.append("\t");
         this.whereExpression.setObjectContext(this.objectContext);
         if (this.whereExpression.toString() != null && this.whereExpression.toString().indexOf("AND") == 0) {
            sb1.append(StringFunctions.replaceFirst(" ", "AND", this.whereExpression.toString()));
         } else {
            sb1.append(" " + this.whereExpression.toString());
         }

         if (!sb1.toString().trim().equalsIgnoreCase("WHERE")) {
            sb.append(sb1.toString());
            sb.append("\n");
         }
      }

      if (this.hierarchical_query_clause != null) {
         sb.append(this.hierarchical_query_clause.toString() + " \n");
      }

      if (this.group_by_statement != null) {
         if (this.group_by_statement.getDescOption() != null && this.group_by_statement.getDescOption().equalsIgnoreCase("desc")) {
            this.group_by_statement.setDescOption((String)null);
         }

         this.group_by_statement.setObjectContext(this.objectContext);
         sb.append(this.group_by_statement.toString() + " \n");
      }

      if (this.having_statement != null) {
         this.having_statement.setObjectContext(this.objectContext);
         sb.append(this.having_statement.toString() + " \n");
      }

      if (this.qualifyStatement != null) {
         sb.append(this.qualifyStatement.toString() + " \n");
      }

      if (this.closeBrace != null) {
         sb.append(this.closeBrace);
      }

      if (this.set_operator_clause != null) {
         this.set_operator_clause.setObjectContext(this.objectContext);
         sb.append(this.set_operator_clause.toString() + " \n");
      }

      if (this.order_by_statement != null && this.subQuery == null && !this.canRemoveOrderByClause()) {
         if (this.commentForOrderByStatement) {
            for(n = 0; n < getBeautyTabCount(); ++n) {
               sb.append("\t");
            }

            sb.append("/*SwisSQLAPI Message : Manual intervention required\n");

            for(n = 0; n < getBeautyTabCount(); ++n) {
               sb.append("\t");
            }
         }

         this.order_by_statement.setObjectContext(this.objectContext);
         sb.append(this.order_by_statement.toString() + " \n");
         if (this.commentForOrderByStatement) {
            sb.append("*/\n");
         }
      }

      if (this.xmls != null) {
         sb.append(this.xmls.toString() + " \n");
      }

      if (this.computeByVector != null) {
         if (this.commentForCompute) {
            for(n = 0; n < getBeautyTabCount(); ++n) {
               sb.append("\t");
            }

            sb.append("/*SwisSQLAPI Message : Manual intervention required\n");

            for(n = 0; n < getBeautyTabCount(); ++n) {
               sb.append("\t");
            }
         }

         for(n = 0; n < this.computeByVector.size(); ++n) {
            for(i = 0; i < getBeautyTabCount(); ++i) {
               sb.append("\t");
            }

            sb.append(((ComputeByStatement)this.computeByVector.get(n)).toString() + "\n");
         }

         if (this.commentForCompute) {
            sb.append("*/\n");
         }
      }

      if (this.for_update_statement != null) {
         sb.append(this.for_update_statement.toString() + " \n");
      }

      if (this.limit_clause != null && !this.canRemoveLimitAndFetchClause()) {
         sb.append(this.limit_clause.toString() + " \n");
      }

      if (this.optionalHintClause != null) {
         sb.append(this.optionalHintClause.toString() + "\n");
      }

      if (this.fetch_clause != null && !this.canRemoveLimitAndFetchClause()) {
         sb.append(this.fetch_clause.toString() + " \n");
      }

      if (this.atIsolation != null) {
         sb.append(this.atIsolation + " READ " + this.isolationReadLevel.toUpperCase() + "\n");
      }

      if (this.definitionOnly != null) {
         sb.append(this.definitionOnly);
      }

      if (this.generalComments != null) {
         sb.append("\n" + this.generalComments + "\n");
      }

      if (this.iqs != null) {
         for(n = 0; n < getBeautyTabCount(); ++n) {
            sb.append("\t");
         }

         sb.append("\n" + this.iqs.toString().trim() + ")");
      }

      for(n = 0; n < this.lockTableStatements.size(); ++n) {
         sb.append(this.lockTableStatements.get(n).toString() + "\n");
      }

      if (this.dropSttForSubQuery.size() <= 0) {
         if (this.multipleQuery != null) {
            sb1 = new StringBuffer();
            sb1.append(sb.toString().trim() + ";\n");
            sb1.append(this.multipleQuery);
            return sb1.toString();
         } else {
            if (this.getSybaseTopRowCount() != -1) {
               sb.append("SET ROWCOUNT 0");
            }

            if (this.withString != null) {
               sb.append(this.withString + " ");
            }

            if (this.isolationLevel != null) {
               sb.append(this.isolationLevel);
            }

            if (this.commentObject != null && this.SQLDialect == 10) {
               sb.append(" " + this.commentObject);
            }

            returnString = sb.toString();
            return returnString;
         }
      } else {
         sb1 = new StringBuffer();
         sb1.append(sb.toString().trim() + ";\n\n");

         for(i = 0; i < this.dropSttForSubQuery.size(); ++i) {
            sb1.append(this.dropSttForSubQuery.get(i).toString().trim() + ";\n\n");
         }

         return sb1.toString();
      }
   }

   public String removeIndent(String s_ri) {
      s_ri = s_ri.replace('\n', ' ');
      s_ri = s_ri.replace('\t', ' ');
      return s_ri;
   }

   public String toInformixString() throws ConvertException {
      return this.toInformix().toString();
   }

   public String toMSSQLServerString() throws ConvertException {
      return this.toMSSQLServer().toString();
   }

   public String toSybaseString() throws ConvertException {
      return this.toSybase().toString();
   }

   public String toOracleString() throws ConvertException {
      return this.toOracle().toString();
   }

   public String toDB2String() throws ConvertException {
      return this.toDB2().toString();
   }

   public String toPostgreSQLString() throws ConvertException {
      return this.toPostgreSQL().toString();
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

   public String toTimesTenString() throws ConvertException {
      return this.toTimesTen().toString();
   }

   public String toNetezzaString() throws ConvertException {
      return this.toNetezza().toString();
   }

   public String toBigQueryString() throws ConvertException {
      return this.toBigQuery().toString();
   }

   public String toSnowflakeString() throws ConvertException {
      return this.toSnowflake().toString();
   }

   public String toAthenaString() throws ConvertException {
      return this.toAthena().toString();
   }

   public String toSapHanaString() throws ConvertException {
      return this.toSapHana().toString();
   }

   public String toSqliteString() throws ConvertException {
      return this.toSqlite().toString();
   }

   public String toExcelString() throws ConvertException {
      return this.toExcel().toString();
   }

   public String toMsAccessJdbcString() throws ConvertException {
      return this.toMsAccessJdbc().toString();
   }

   public SelectQueryStatement toANSI() throws ConvertException {
      SelectQueryStatement sqs_ansi_sql = new SelectQueryStatement();
      if (this.commentObject != null) {
         this.commentObject.setSQLDialect(8);
      }

      sqs_ansi_sql.setCommentClass(this.commentObject);
      sqs_ansi_sql.setSelectStatement(this.select_statement.toANSISelect(sqs_ansi_sql, this));
      if (this.from_clause != null) {
         sqs_ansi_sql.setFromClause(this.from_clause.toANSISelect(sqs_ansi_sql, this));
      }

      if (this.whereExpression != null) {
         sqs_ansi_sql.setWhereExpression(this.whereExpression.toANSISelect(sqs_ansi_sql, this));
      }

      if (this.order_by_statement != null) {
         sqs_ansi_sql.setOrderByStatement(this.order_by_statement.toANSISelect(sqs_ansi_sql, this));
      }

      if (this.computeByVector != null) {
         Vector computeByStatementVector = new Vector();

         for(int i = 0; i < this.computeByVector.size(); ++i) {
            computeByStatementVector.add(((ComputeByStatement)this.computeByVector.get(i)).toANSISelect(sqs_ansi_sql, this));
         }

         sqs_ansi_sql.setComputeByStatements(computeByStatementVector);
         sqs_ansi_sql.setCommentForCompute(true);
      }

      if (sqs_ansi_sql.getGroupByStatement() == null && this.group_by_statement != null) {
         sqs_ansi_sql.setGroupByStatement(this.group_by_statement.toANSISelect(sqs_ansi_sql, this));
      }

      if (this.set_operator_clause != null) {
         sqs_ansi_sql.setSetOperatorClause(this.set_operator_clause.toANSISelect(sqs_ansi_sql, this));
      }

      if (this.having_statement != null) {
         sqs_ansi_sql.setHavingStatement(this.having_statement.toANSISelect(sqs_ansi_sql, this));
      }

      if (this.into_statement != null) {
         throw new ConvertException();
      } else if (this.hierarchical_query_clause != null) {
         throw new ConvertException();
      } else if (this.limit_clause != null) {
         throw new ConvertException();
      } else if (this.fetch_clause != null) {
         throw new ConvertException();
      } else if (this.for_update_statement != null) {
         throw new ConvertException();
      } else {
         sqs_ansi_sql.setOptionalHintClause((OptionalHintClause)null);
         return sqs_ansi_sql;
      }
   }

   public SelectQueryStatement toTeradata() throws ConvertException {
      SelectQueryStatement sqs_Teradata_sql = new SelectQueryStatement();
      sqs_Teradata_sql.setTopLevel(this.topLevel);
      sqs_Teradata_sql.setHintClause(this.hintClause);
      if (this.commentObject != null) {
         this.commentObject.setSQLDialect(12);
      }

      sqs_Teradata_sql.setCommentClass(this.commentObject);
      sqs_Teradata_sql.setSelectStatement(this.select_statement.toTeradataSelect(sqs_Teradata_sql, this));
      if (this.from_clause != null) {
         sqs_Teradata_sql.setFromClause(this.from_clause.toTeradataSelect(sqs_Teradata_sql, this));
      }

      if (this.whereExpression != null) {
         sqs_Teradata_sql.setWhereExpression(this.whereExpression.toTeradataSelect(sqs_Teradata_sql, this));
      }

      if (this.order_by_statement != null) {
         sqs_Teradata_sql.setOrderByStatement(this.order_by_statement.toTeradataSelect(sqs_Teradata_sql, this));
      }

      Vector derivedTables;
      if (this.computeByVector != null) {
         derivedTables = new Vector();

         for(int i = 0; i < this.computeByVector.size(); ++i) {
            derivedTables.add(((ComputeByStatement)this.computeByVector.get(i)).toTeradataSelect(sqs_Teradata_sql, this));
         }

         sqs_Teradata_sql.setComputeByStatements(derivedTables);
         sqs_Teradata_sql.setCommentForCompute(true);
      }

      if (sqs_Teradata_sql.getGroupByStatement() == null && this.group_by_statement != null) {
         sqs_Teradata_sql.setGroupByStatement(this.group_by_statement.toTeradataSelect(sqs_Teradata_sql, this));
      }

      if (this.set_operator_clause != null) {
         sqs_Teradata_sql.setSetOperatorClause(this.set_operator_clause.toTeradataSelect(sqs_Teradata_sql, this));
      }

      if (this.having_statement != null) {
         sqs_Teradata_sql.setHavingStatement(this.having_statement.toTeradataSelect(sqs_Teradata_sql, this));
      }

      Vector derivedTables;
      Vector newFromItems;
      String orgnlTblAliasName;
      if (this.isOlapFunctionPresent && this.olapDerivedTables != null && this.olapDerivedTables.size() > 0) {
         derivedTables = new Vector();
         FromClause newFromClause = new FromClause();
         newFromClause.setFromClause("FROM");
         derivedTables = new Vector();
         derivedTables.addAll(sqs_Teradata_sql.getFromClause().getFromItemList());
         newFromClause.setFromItemList(derivedTables);
         WhereExpression newWhereExp = null;
         if (sqs_Teradata_sql.getWhereExpression() != null && sqs_Teradata_sql.getWhereExpression().hasNonNullWhereItem()) {
            newWhereExp = new WhereExpression();
            newFromItems = new Vector(sqs_Teradata_sql.getWhereExpression().getWhereItem());
            Vector newWhereExpOps = new Vector(sqs_Teradata_sql.getWhereExpression().getOperator());
            newWhereExp.setWhereItem(newFromItems);
            newWhereExp.setOperator(newWhereExpOps);
         }

         Object[] olapTables = this.olapDerivedTables.values().toArray();

         for(int j = 0; j < olapTables.length; ++j) {
            FromTable dt = (FromTable)olapTables[j];
            ((SelectQueryStatement)dt.getTableName()).setFromClause(newFromClause);
            if (newWhereExp != null) {
               if (((SelectQueryStatement)dt.getTableName()).getWhereExpression() == null) {
                  ((SelectQueryStatement)dt.getTableName()).setWhereExpression(newWhereExp);
               } else {
                  if (!((SelectQueryStatement)dt.getTableName()).getWhereExpression().getWhereItems().isEmpty()) {
                     ((SelectQueryStatement)dt.getTableName()).getWhereExpression().addOperator("AND");
                  }

                  ((SelectQueryStatement)dt.getTableName()).getWhereExpression().addWhereExpression(newWhereExp);
               }
            }

            orgnlTblAliasName = this.getFromClause().getLastElement().getAliasName();
            if (orgnlTblAliasName != null && orgnlTblAliasName.startsWith("\"")) {
               dt.setAliasName(orgnlTblAliasName.substring(1, orgnlTblAliasName.length() - 1) + j);
            } else {
               dt.setAliasName(orgnlTblAliasName + j);
            }

            if (sqs_Teradata_sql.getWhereExpression() != null && !sqs_Teradata_sql.getWhereExpression().isThetaJoinPresent()) {
               dt.setJoinClause((String)null);
               dt.setOnOrUsingJoin((String)null);
               if (!sqs_Teradata_sql.getWhereExpression().getWhereItems().isEmpty()) {
                  sqs_Teradata_sql.getWhereExpression().addOperator("AND");
               }

               sqs_Teradata_sql.getWhereExpression().addWhereExpression((WhereExpression)dt.getJoinExpression().firstElement());
               dt.setJoinExpression((Vector)null);
            }

            derivedTables.add(dt);
         }

         sqs_Teradata_sql.getFromClause().getFromItemList().addAll(derivedTables);
      }

      if (sqs_Teradata_sql.getRownumClause() != null || sqs_Teradata_sql.isRownumColumnPresent()) {
         this.handleRownumConversion(sqs_Teradata_sql);
      }

      if (this.into_statement != null) {
         throw new ConvertException();
      } else {
         if (this.hierarchical_query_clause != null) {
            this.withStatement = this.convertHierarchical_queryToWithStatement(sqs_Teradata_sql);
            this.withStatement.setWith("WITH RECURSIVE ");
            sqs_Teradata_sql.setWithStatement(this.withStatement);
            if (this.withStatement == null && this.hierarchical_query_clause.getStartWithCondition() != null) {
               sqs_Teradata_sql.setWhereExpression(this.hierarchical_query_clause.getStartWithCondition());
            }
         }

         if (this.limit_clause != null) {
            throw new ConvertException();
         } else if (this.fetch_clause != null) {
            throw new ConvertException();
         } else if (this.for_update_statement != null) {
            throw new ConvertException();
         } else {
            sqs_Teradata_sql.setOptionalHintClause((OptionalHintClause)null);
            boolean crossJoinFound = this.isCrossJoinAvailable(sqs_Teradata_sql);
            FromClause newFromClause;
            if (this.isOlapFunctionPresent && crossJoinFound) {
               String tableName = "ADVENTNET_SWISSQL1";
               new SelectStatement();
               new FromClause();
               newFromItems = new Vector();
               newFromItems.addAll(sqs_Teradata_sql.getSelectStatement().getSelectItemList());
               newFromClause = this.createFromClauseForDerivedTable(newFromItems, sqs_Teradata_sql, tableName);
               SelectStatement newSelectStatement = this.createSelectStatementForDerivedTable(this.getSelectStatement(), tableName);
               sqs_Teradata_sql = new SelectQueryStatement();
               sqs_Teradata_sql.setSelectStatement(newSelectStatement);
               sqs_Teradata_sql.setFromClause(newFromClause);
            }

            if (this.sumFunctionWithPartition) {
               Vector selectItems = new Vector(sqs_Teradata_sql.getSelectStatement().getSelectItemList());
               derivedTables = new Vector();
               newFromClause = new FromClause();
               newFromClause.setFromClause("FROM");
               newFromItems = new Vector();
               newFromItems.addAll(sqs_Teradata_sql.getFromClause().getFromItemList());
               newFromClause.setFromItemList(newFromItems);
               WhereExpression newWhereExp = null;
               if (sqs_Teradata_sql.getWhereExpression() != null && sqs_Teradata_sql.getWhereExpression().hasNonNullWhereItem()) {
                  newWhereExp = new WhereExpression();
                  Vector newWhereExpItems = new Vector(sqs_Teradata_sql.getWhereExpression().getWhereItem());
                  Vector newWhereExpOps = new Vector(sqs_Teradata_sql.getWhereExpression().getOperator());
                  newWhereExp.setWhereItem(newWhereExpItems);
                  newWhereExp.setOperator(newWhereExpOps);
               }

               Object[] sumTables = this.sumDerivedTables.values().toArray();

               for(int j = 0; j < sumTables.length; ++j) {
                  FromTable dt = (FromTable)sumTables[j];
                  ((SelectQueryStatement)dt.getTableName()).setFromClause(newFromClause);
                  if (newWhereExp != null) {
                     if (((SelectQueryStatement)dt.getTableName()).getWhereExpression() == null) {
                        ((SelectQueryStatement)dt.getTableName()).setWhereExpression(newWhereExp);
                     } else {
                        if (!((SelectQueryStatement)dt.getTableName()).getWhereExpression().getWhereItems().isEmpty()) {
                           ((SelectQueryStatement)dt.getTableName()).getWhereExpression().addOperator("AND");
                        }

                        ((SelectQueryStatement)dt.getTableName()).getWhereExpression().addWhereExpression(newWhereExp);
                     }
                  }

                  if (sqs_Teradata_sql.getWhereExpression() != null && !sqs_Teradata_sql.getWhereExpression().isThetaJoinPresent()) {
                     if (!sqs_Teradata_sql.getWhereExpression().getWhereItems().isEmpty()) {
                        sqs_Teradata_sql.getWhereExpression().addOperator("AND");
                     }

                     sqs_Teradata_sql.getWhereExpression().addWhereExpression((WhereExpression)dt.getJoinExpression().firstElement());
                  }

                  derivedTables.add(dt);
               }

               orgnlTblAliasName = "orgnl";
               FromClause fc = new FromClause();
               fc.setFromClause("FROM");
               Vector vc = new Vector();
               vc.addAll(derivedTables);
               vc.add(0, this.convertedSQStoDerivedTable(selectItems, newFromClause, newWhereExp, orgnlTblAliasName));
               fc.setFromItemList(vc);
               SelectStatement ss = new SelectStatement();
               ss.setSelectClause("SELECT");
               Vector vSelectItem = new Vector(this.getSelectStatement().getSelectItemList());
               Vector vSelectItems_modified = new Vector(this.modifySumMethodFunctionsInSelectItems(vSelectItem, orgnlTblAliasName, this.getSumSelectColumn(), this.getSumDerivedTables()));
               ss.setSelectItemList(vSelectItems_modified);
               sqs_Teradata_sql = new SelectQueryStatement();
               sqs_Teradata_sql.setSelectStatement(ss);
               sqs_Teradata_sql.setFromClause(fc);
            }

            if (this.getSelectStatement().getSelectItemList().size() != sqs_Teradata_sql.getSelectStatement().getSelectItemList().size()) {
               System.out.println("Issue in convesion size of select items does not match");
               throw new ConvertException("Issue in convesion size of select items does not match");
            } else {
               sqs_Teradata_sql.setConverted(true);
               return sqs_Teradata_sql;
            }
         }
      }
   }

   public FromTable convertedSQStoDerivedTable(Vector selectItems, FromClause fc, WhereExpression we, String aliasName) {
      SelectQueryStatement sqsAsFromTable = new SelectQueryStatement();
      SelectStatement ss = new SelectStatement();
      ss.setSelectClause("SELECT");
      selectItems = new Vector(this.removeSumFunctionWithPartitionByClause(selectItems));
      SelectColumn sc = (SelectColumn)selectItems.lastElement();
      sc.setEndsWith((String)null);
      selectItems.setElementAt(sc, selectItems.size() - 1);
      ss.setSelectItemList(selectItems);
      sqsAsFromTable.setSelectStatement(ss);
      sqsAsFromTable.setFromClause(fc);
      sqsAsFromTable.setWhereExpression(we);
      FromTable ft = new FromTable();
      ft.setTableName(sqsAsFromTable);
      ft.setAliasName(aliasName);
      return ft;
   }

   public Vector removeSumFunctionWithPartitionByClause(Vector sItems) {
      Vector sItem1 = new Vector(sItems);
      int i = 0;

      for(int size = sItem1.size(); i < size; ++i) {
         if (sItem1.get(i) instanceof SelectColumn) {
            Vector vsc = new Vector();
            SelectColumn sc = (SelectColumn)sItem1.get(i);
            if (this.checkForSumFunctionWithPartitionClause(sc.getColumnExpression())) {
               sItem1.removeElementAt(i);
               --i;
               --size;
            } else {
               vsc.addAll(sc.getColumnExpression());
               vsc = this.removeSumFunctionWithPartitionByClause(vsc);
               sc.setColumnExpression(vsc);
               sItem1.setElementAt(sc, i);
            }
         } else if (sItem1.get(i) instanceof TableColumn) {
            TableColumn tc = (TableColumn)sItem1.get(i);
            sItem1.setElementAt(tc, i);
         } else if (sItem1.get(i) instanceof FunctionCalls) {
            FunctionCalls fc = (FunctionCalls)sItem1.get(i);
            if (!fc.getFunctionName().getColumnName().equalsIgnoreCase("SUM") || fc.getPartitionByClause() == null) {
               Vector funArgs = new Vector(fc.getFunctionArguments());
               funArgs = this.removeSumFunctionWithPartitionByClause(funArgs);
               fc.setFunctionArguments(funArgs);
               sItem1.setElementAt(fc, i);
            }
         }
      }

      return sItem1;
   }

   public boolean checkForSumFunctionWithPartitionClause(Vector vc1) {
      boolean chk = false;
      Vector vc = new Vector(vc1);

      for(int i = 0; i < vc.size(); ++i) {
         if (vc.get(i) instanceof FunctionCalls) {
            FunctionCalls fc = (FunctionCalls)vc.get(i);
            if (fc.getFunctionName().getColumnName().equalsIgnoreCase("SUM") && fc.getPartitionByClause() != null && fc.getArgumentQualifier() != null && fc.getArgumentQualifier().equalsIgnoreCase("DISTINCT")) {
               chk = true;
            } else {
               chk |= this.checkForSumFunctionWithPartitionClause(fc.getFunctionArguments());
            }
         }

         if (vc.get(i) instanceof SelectColumn) {
            chk |= this.checkForSumFunctionWithPartitionClause(((SelectColumn)vc.get(i)).getColumnExpression());
         }
      }

      return chk;
   }

   public Vector modifySumMethodFunctionsInSelectItems(Vector vSelectItem1, String aliasName, HashMap aliasNameSelectColumnPair, HashMap derivedTables) throws ConvertException {
      Vector vSelectItem = new Vector(vSelectItem1);
      int i = 0;

      for(int size = vSelectItem.size(); i < size; ++i) {
         if (vSelectItem.get(i) instanceof SelectColumn) {
            Vector vsc = new Vector();
            SelectColumn sc = (SelectColumn)vSelectItem.get(i);
            vsc.addAll(sc.getColumnExpression());
            vsc = this.modifySumMethodFunctionsInSelectItems(vsc, aliasName, aliasNameSelectColumnPair, derivedTables);
            sc.setColumnExpression(vsc);
            vSelectItem.setElementAt(sc.toTeradataSelect((SelectQueryStatement)null, (SelectQueryStatement)null), i);
         } else if (vSelectItem.get(i) instanceof TableColumn) {
            TableColumn tc = (TableColumn)vSelectItem.get(i);
            tc.setTableName(aliasName);
            vSelectItem.setElementAt(tc.toTeradataSelect((SelectQueryStatement)null, (SelectQueryStatement)null), i);
         } else if (vSelectItem.get(i) instanceof FunctionCalls) {
            FunctionCalls fc = (FunctionCalls)vSelectItem.get(i);
            if (fc.getFunctionName().getColumnName().equalsIgnoreCase("SUM") && fc.getArgumentQualifier() != null && fc.getPartitionByClause() != null && fc.getFunctionArguments() != null) {
               String gpc = fc.getPartitionByClause().toString();
               String fcStr = fc.getFunctionArguments().get(0).toString();
               String newColName = aliasNameSelectColumnPair.get(gpc + fcStr).toString();
               TableColumn tc = new TableColumn();
               FromTable ft = (FromTable)derivedTables.get(gpc);
               tc.setTableName(ft.getAliasName());
               Vector vc = ((SelectQueryStatement)ft.getTableName()).getSelectStatement().getSelectItemList();
               tc.setColumnName(newColName);
               vSelectItem.setElementAt(tc.toTeradataSelect((SelectQueryStatement)null, (SelectQueryStatement)null), i);
            } else {
               Vector funArgs = new Vector(fc.getFunctionArguments());
               funArgs = this.modifySumMethodFunctionsInSelectItems(funArgs, aliasName, aliasNameSelectColumnPair, derivedTables);
               fc.setFunctionArguments(funArgs);
               vSelectItem.setElementAt(fc.toTeradataSelect((SelectQueryStatement)null, (SelectQueryStatement)null), i);
            }
         }
      }

      return vSelectItem;
   }

   public SelectQueryStatement toMSSQLServer() throws ConvertException {
      boolean wrapSelectQuery = false;
      Vector wrapperSelectCol = null;
      SelectQueryStatement sqs_ms_sql = new SelectQueryStatement();
      sqs_ms_sql.setOpenBrace(this.openBrace);
      sqs_ms_sql.setCloseBrace(this.closeBrace);
      if (this.commentObject != null) {
         this.commentObject.setSQLDialect(2);
      }

      sqs_ms_sql.setCommentClass(this.commentObject);
      sqs_ms_sql.setSelectStatement(this.select_statement.toMSSQLServerSelect(sqs_ms_sql, this));
      if (this.from_clause != null) {
         sqs_ms_sql.setFromClause(this.from_clause.toMSSQLServerSelect(sqs_ms_sql, this));
      }

      if (this.whereExpression != null) {
         sqs_ms_sql.setWhereExpression(this.whereExpression.toMSSQLServerSelect(sqs_ms_sql, this));
      }

      int i;
      if (this.order_by_statement != null) {
         int selectColCount = sqs_ms_sql.getSelectStatement().getSelectItemList().size();
         wrapperSelectCol = sqs_ms_sql.getSelectStatement().getSelectItemList();
         sqs_ms_sql.setOrderByStatement(this.order_by_statement.toMSSQLServerSelect(sqs_ms_sql, this));
         i = sqs_ms_sql.getSelectStatement().getSelectItemList().size();
         if (i > selectColCount) {
            wrapSelectQuery = true;
         }
      }

      if (this.xmls != null) {
         sqs_ms_sql.setXMLStatements(this.xmls);
      }

      if (this.computeByVector != null) {
         Vector computeByStatementVector = new Vector();

         for(i = 0; i < this.computeByVector.size(); ++i) {
            computeByStatementVector.add(((ComputeByStatement)this.computeByVector.get(i)).toMSSQLServerSelect(sqs_ms_sql, this));
         }

         sqs_ms_sql.setComputeByStatements(computeByStatementVector);
      }

      if (sqs_ms_sql.getGroupByStatement() == null && this.group_by_statement != null) {
         sqs_ms_sql.setGroupByStatement(this.group_by_statement.toMSSQLServerSelect(sqs_ms_sql, this));
         if (!this.isOnlyFullGroupByModeEnabled && sqs_ms_sql.getGroupByStatement() != null) {
            this.handleGroupByStatement(sqs_ms_sql, sqs_ms_sql.getGroupByStatement());
         }
      }

      if (this.set_operator_clause != null) {
         sqs_ms_sql.setSetOperatorClause(this.set_operator_clause.toMSSQLServerSelect(sqs_ms_sql, this));
      }

      if (this.having_statement != null) {
         sqs_ms_sql.setHavingStatement(this.having_statement.toMSSQLServerSelect(sqs_ms_sql, this));
      }

      if (this.into_statement != null) {
         sqs_ms_sql.setIntoStatement(this.into_statement.toMSSQLServerSelect(sqs_ms_sql, this));
      }

      if (this.hierarchical_query_clause != null) {
         boolean isPrior = this.checkForPriorinHierarchyClause();
         if (!SwisSQLOptions.convertHierarchicalClausetoSQL2005 && isPrior) {
            this.processSelectQueryForStartWithConnectBy = this.handleStartWithConnectByClause();
            sqs_ms_sql.setStartWithConnectByHashtable(this.processSelectQueryForStartWithConnectBy);
            sqs_ms_sql.setFromClause(this.from_clause.toMSSQLServerSelect(sqs_ms_sql, this));
         } else {
            this.withStatement = this.convertHierarchical_queryToWithStatement();
            sqs_ms_sql.setWithStatement(this.withStatement);
            if (this.withStatement == null && this.hierarchical_query_clause.getStartWithCondition() != null) {
               sqs_ms_sql.setWhereExpression(this.hierarchical_query_clause.getStartWithCondition());
            }
         }
      }

      if (this.limit_clause != null) {
         sqs_ms_sql.setLimitClause(this.limit_clause.toMSSQLServerSelect(sqs_ms_sql, this));
      }

      if (this.optionalHintClause != null) {
         sqs_ms_sql.setOptionalHintClause(this.optionalHintClause);
      }

      if (this.fetch_clause != null) {
         sqs_ms_sql.setFetchClause(this.fetch_clause.toMSSQLServerSelect(sqs_ms_sql, this));
      }

      if (this.for_update_statement != null && FunctionCalls.charToIntName) {
         sqs_ms_sql.setForUpdateStatement(this.for_update_statement.toMSSQLServerSelect(sqs_ms_sql, this));
      }

      if (wrapSelectQuery) {
         SelectQueryStatement wrapperQuery = new SelectQueryStatement();
         SelectStatement wrapperStmt = new SelectStatement();
         wrapperStmt.setSelectClause("SELECT");
         if (wrapperSelectCol != null) {
            wrapperStmt.setSelectItemList(wrapperSelectCol);
         }

         FromTable wrapperSubQuery = new FromTable();
         wrapperSubQuery.setTableName(sqs_ms_sql);
         wrapperQuery.setSelectStatement(wrapperStmt);
         wrapperSubQuery.setAliasName("alias");
         FromClause wrapperFromClause = new FromClause();
         wrapperFromClause.addFromItem(wrapperSubQuery);
         wrapperFromClause.setFromClause("FROM");
         wrapperQuery.setFromClause(wrapperFromClause);
         wrapperQuery.setOrderByStatement(sqs_ms_sql.getOrderByStatement());
         sqs_ms_sql.setOrderByStatement((OrderByStatement)null);
         return wrapperQuery;
      } else {
         return sqs_ms_sql;
      }
   }

   public SelectQueryStatement toSybase() throws ConvertException {
      SelectQueryStatement sqs_ms_sql = new SelectQueryStatement();
      sqs_ms_sql.setObjectContext(this.objectContext);
      if (this.commentObject != null) {
         this.commentObject.setSQLDialect(7);
      }

      sqs_ms_sql.setCommentClass(this.commentObject);
      this.select_statement.setObjectContext(this.objectContext);
      sqs_ms_sql.setSelectStatement(this.select_statement.toSybaseSelect(sqs_ms_sql, this));
      if (this.from_clause != null) {
         sqs_ms_sql.setFromClause(this.from_clause.toSybaseSelect(sqs_ms_sql, this));
      }

      if (this.whereExpression != null) {
         this.whereExpression.setObjectContext(this.objectContext);
         sqs_ms_sql.setWhereExpression(this.whereExpression.toSybaseSelect(sqs_ms_sql, this));
      }

      if (this.order_by_statement != null) {
         sqs_ms_sql.setOrderByStatement(this.order_by_statement.toSybaseSelect(sqs_ms_sql, this));
      }

      if (this.computeByVector != null) {
         Vector computeByStatementVector = new Vector();

         for(int i = 0; i < this.computeByVector.size(); ++i) {
            computeByStatementVector.add(((ComputeByStatement)this.computeByVector.get(i)).toSybaseSelect(sqs_ms_sql, this));
         }

         sqs_ms_sql.setComputeByStatements(computeByStatementVector);
         sqs_ms_sql.setCommentForCompute(true);
      }

      if (sqs_ms_sql.getGroupByStatement() == null && this.group_by_statement != null) {
         sqs_ms_sql.setGroupByStatement(this.group_by_statement.toSybaseSelect(sqs_ms_sql, this));
      }

      if (this.set_operator_clause != null) {
         sqs_ms_sql.setSetOperatorClause(this.set_operator_clause.toSybaseSelect(sqs_ms_sql, this));
      }

      if (this.having_statement != null) {
         sqs_ms_sql.setHavingStatement(this.having_statement.toSybaseSelect(sqs_ms_sql, this));
      }

      if (this.into_statement != null) {
         sqs_ms_sql.setIntoStatement(this.into_statement.toSybaseSelect(sqs_ms_sql, this));
      }

      if (this.hierarchical_query_clause != null) {
         sqs_ms_sql.setHierarchicalQueryClause(this.hierarchical_query_clause.toSybaseSelect(sqs_ms_sql, this));
      }

      if (this.limit_clause != null) {
         sqs_ms_sql.setLimitClause(this.limit_clause.toSybaseSelect(sqs_ms_sql, this));
      }

      if (this.fetch_clause != null) {
         sqs_ms_sql.setFetchClause(this.fetch_clause.toSybaseSelect(sqs_ms_sql, this));
      }

      if (this.for_update_statement != null) {
         if (SwisSQLOptions.convertOracleForUpdateToSybaseLockTable) {
            Vector fromItems;
            int l;
            Object currObj;
            String lockTableStatement;
            if (this.for_update_statement.getForUpdateQualifier() != null && this.for_update_statement.getForUpdateQualifier().toLowerCase().indexOf("of") != -1) {
               ArrayList addedTables = new ArrayList();
               fromItems = this.for_update_statement.getForUpdateTableName();

               for(l = 0; l < fromItems.size(); ++l) {
                  currObj = fromItems.get(l);
                  if (currObj instanceof TableColumn) {
                     String currTableName = ((TableColumn)currObj).getTableName();
                     if (currTableName != null && !addedTables.contains(currTableName)) {
                        if (this.objectContext != null) {
                           lockTableStatement = "LOCK TABLE " + this.objectContext.getEquivalent(currTableName) + " IN SHARE MODE";
                        } else {
                           lockTableStatement = "LOCK TABLE " + currTableName + " IN SHARE MODE";
                        }

                        if (this.for_update_statement.getNoWaitQualifier() != null && this.for_update_statement.getNoWaitQualifier().toLowerCase().indexOf("nowait") != -1) {
                           lockTableStatement = lockTableStatement + " NOWAIT";
                        }

                        this.lockTableStatements.add(lockTableStatement);
                        addedTables.add(currTableName);
                     } else if (currTableName == null) {
                        FromClause fromClause = sqs_ms_sql.getFromClause();
                        if (fromClause != null) {
                           Vector fromItems = fromClause.getFromItemList();
                           if (fromItems.size() == 1) {
                              if (fromItems.get(0) instanceof FromTable) {
                                 Object newObj = ((FromTable)fromItems.get(0)).getTableName();
                                 if (newObj instanceof String && !addedTables.contains(newObj.toString())) {
                                    if (this.objectContext != null) {
                                       lockTableStatement = "LOCK TABLE " + this.objectContext.getEquivalent(newObj.toString()) + " IN SHARE MODE";
                                    } else {
                                       lockTableStatement = "LOCK TABLE " + newObj.toString() + " IN SHARE MODE";
                                    }

                                    if (this.for_update_statement.getNoWaitQualifier() != null && this.for_update_statement.getNoWaitQualifier().toLowerCase().indexOf("nowait") != -1) {
                                       lockTableStatement = lockTableStatement + " NOWAIT";
                                    }

                                    this.lockTableStatements.add(lockTableStatement);
                                    addedTables.add(newObj.toString());
                                 }
                              }
                           } else {
                              FromTable currFromTable = MetadataInfoUtil.getTableOfColumn(sqs_ms_sql, (TableColumn)((TableColumn)currObj));
                              if (currFromTable != null) {
                                 Object fromTableObj = currFromTable.getTableName();
                                 if (fromTableObj instanceof String && !addedTables.contains(fromTableObj.toString())) {
                                    if (this.objectContext != null) {
                                       lockTableStatement = "LOCK TABLE " + this.objectContext.getEquivalent(fromTableObj.toString()) + " IN SHARE MODE";
                                    } else {
                                       lockTableStatement = "LOCK TABLE " + fromTableObj.toString() + " IN SHARE MODE";
                                    }

                                    if (this.for_update_statement.getNoWaitQualifier() != null && this.for_update_statement.getNoWaitQualifier().toLowerCase().indexOf("nowait") != -1) {
                                       lockTableStatement = lockTableStatement + " NOWAIT";
                                    }

                                    this.lockTableStatements.add(lockTableStatement);
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            } else {
               FromClause fromClause = sqs_ms_sql.getFromClause();
               if (fromClause != null) {
                  fromItems = fromClause.getFromItemList();

                  for(l = 0; l < fromItems.size(); ++l) {
                     currObj = fromItems.get(l);
                     if (currObj instanceof FromTable) {
                        currObj = ((FromTable)currObj).getTableName();
                        if (currObj instanceof String) {
                           if (this.objectContext != null) {
                              lockTableStatement = "LOCK TABLE " + this.objectContext.getEquivalent(currObj.toString()) + " IN SHARE MODE";
                           } else {
                              lockTableStatement = "LOCK TABLE " + currObj.toString() + " IN SHARE MODE";
                           }

                           if (this.for_update_statement.getNoWaitQualifier() != null && this.for_update_statement.getNoWaitQualifier().toLowerCase().indexOf("nowait") != -1) {
                              lockTableStatement = lockTableStatement + " NOWAIT";
                           }

                           this.lockTableStatements.add(lockTableStatement);
                        }
                     }
                  }
               }
            }

            sqs_ms_sql.setLockTableStatements(this.lockTableStatements);
            sqs_ms_sql.setForUpdateStatement((ForUpdateStatement)null);
         } else {
            sqs_ms_sql.setForUpdateStatement((ForUpdateStatement)null);
         }
      }

      sqs_ms_sql.setOptionalHintClause((OptionalHintClause)null);
      sqs_ms_sql.setAtIsolation(this.atIsolation);
      sqs_ms_sql.setIsolationReadLevel(this.isolationReadLevel);
      return sqs_ms_sql;
   }

   public SelectQueryStatement toDB2() throws ConvertException {
      SelectQueryStatement sqs_db2_sql = new SelectQueryStatement();
      this.select_statement.setInsertValList(this.insertValList);
      if (this.commentObject != null) {
         this.commentObject.setSQLDialect(3);
      }

      sqs_db2_sql.setCommentClass(this.commentObject);
      if (this.openBrace != null) {
         sqs_db2_sql.setOpenBrace(this.openBrace);
      }

      sqs_db2_sql.setSelectStatement(this.select_statement.toDB2Select(sqs_db2_sql, this));
      new SelectQueryStatement();
      Vector selectItems;
      if (this.from_clause != null) {
         sqs_db2_sql.setFromClause(this.from_clause.toDB2Select(sqs_db2_sql, this));
      } else {
         FromClause fc = new FromClause();
         FromTable ft = new FromTable();
         selectItems = new Vector();
         FetchClause local_fetch_clause = new FetchClause();
         fc.setFromClause("FROM");
         ft.setTableName("SYSIBM.SYSDUMMY1");
         selectItems.addElement(ft);
         fc.setFromItemList(selectItems);
         local_fetch_clause.setFetchFirstClause("FETCH FIRST");
         local_fetch_clause.setFetchCount("1");
         local_fetch_clause.setRowOnlyClause("ROW ONLY");
         fc.setFetchClauseFromSQS(local_fetch_clause);
         sqs_db2_sql.setFromClause(fc);
      }

      if (this.whereExpression != null) {
         sqs_db2_sql.setWhereExpression(this.whereExpression.toDB2Select(sqs_db2_sql, this));
      }

      if (this.order_by_statement != null) {
         SelectStatement ss = sqs_db2_sql.getSelectStatement();
         boolean orderBySetNull = false;
         if (ss != null) {
            selectItems = ss.getSelectItemList();
            if (selectItems != null) {
               for(int i = 0; i < selectItems.size(); ++i) {
                  Object obj = selectItems.get(i);
                  if (obj instanceof SelectColumn) {
                     SelectColumn sc = (SelectColumn)obj;
                     Vector colExpr = sc.getColumnExpression();
                     if (colExpr != null) {
                        for(int j = 0; j < colExpr.size(); ++j) {
                           obj = colExpr.get(j);
                           if (obj instanceof FunctionCalls) {
                              FunctionCalls fn = (FunctionCalls)obj;
                              TableColumn tc = fn.getFunctionName();
                              if (tc != null) {
                                 String colName = tc.getColumnName();
                                 if ((colName.equalsIgnoreCase("max") || colName.equalsIgnoreCase("min") || colName.equalsIgnoreCase("count") || colName.equalsIgnoreCase("sum") || colName.equalsIgnoreCase("avg")) && this.group_by_statement == null) {
                                    orderBySetNull = true;
                                    sqs_db2_sql.setOrderByStatement((OrderByStatement)null);
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         if (!orderBySetNull) {
            sqs_db2_sql.setOrderByStatement(this.order_by_statement.toDB2Select(sqs_db2_sql, this));
         }
      }

      if (this.computeByVector != null) {
         Vector computeByStatementVector = new Vector();

         for(int i = 0; i < this.computeByVector.size(); ++i) {
            computeByStatementVector.add(((ComputeByStatement)this.computeByVector.get(i)).toDB2Select(sqs_db2_sql, this));
         }

         sqs_db2_sql.setComputeByStatements(computeByStatementVector);
         sqs_db2_sql.setCommentForCompute(true);
      }

      if (sqs_db2_sql.getGroupByStatement() == null && this.group_by_statement != null) {
         sqs_db2_sql.setGroupByStatement(this.group_by_statement.toDB2Select(sqs_db2_sql, this));
         if (!this.isOnlyFullGroupByModeEnabled && sqs_db2_sql.getGroupByStatement() != null && sqs_db2_sql.getGroupByStatement().getGroupingSetClause() == null) {
            this.handleGroupByStatement(sqs_db2_sql, sqs_db2_sql.getGroupByStatement());
         }
      }

      if (this.set_operator_clause != null) {
         sqs_db2_sql.setSetOperatorClause(this.set_operator_clause.toDB2Select(sqs_db2_sql, this));
      }

      if (this.having_statement != null) {
         sqs_db2_sql.setHavingStatement(this.having_statement.toDB2Select(sqs_db2_sql, this));
      }

      if (this.into_statement != null) {
         sqs_db2_sql.setIntoStatement(this.into_statement.toDB2Select(sqs_db2_sql, this));
      }

      if (this.hierarchical_query_clause != null) {
         this.withStatement = this.convertHierarchical_queryToWithStatement();
         sqs_db2_sql.setWithStatement(this.withStatement);
         if (this.withStatement == null && this.hierarchical_query_clause.getStartWithCondition() != null) {
            sqs_db2_sql.setWhereExpression(this.hierarchical_query_clause.getStartWithCondition());
         }
      }

      if (this.limit_clause != null) {
         sqs_db2_sql.setLimitClause(this.limit_clause.toDB2Select(sqs_db2_sql, this));
      }

      if (this.fetch_clause != null) {
         sqs_db2_sql.setFetchClause(this.fetch_clause.toDB2Select(sqs_db2_sql, this));
      }

      if (this.for_update_statement != null) {
         sqs_db2_sql.setForUpdateStatement(this.for_update_statement.toDB2Select(sqs_db2_sql, this));
      }

      sqs_db2_sql.setOptionalHintClause((OptionalHintClause)null);
      if (this.definitionOnly != null) {
         sqs_db2_sql.setDefinitionOnly(this.definitionOnly);
      }

      if (this.iqs != null) {
         sqs_db2_sql.setInsertQueryStatement(this.iqs);
      }

      if (this.withString != null) {
         sqs_db2_sql.setWithString(this.withString);
      }

      if (this.isolationLevel != null) {
         sqs_db2_sql.setIsolationLevel(this.isolationLevel);
      }

      if (this.closeBrace != null) {
         sqs_db2_sql.setCloseBrace(this.closeBrace);
      }

      return sqs_db2_sql;
   }

   public SelectQueryStatement toBigQuery() throws ConvertException {
      SelectQueryStatement sqs_bigquery_sql = new SelectQueryStatement();
      sqs_bigquery_sql.setQueryConversionPropHandler(this.getQueryConvPropHandler());
      sqs_bigquery_sql.setSetOperatorQuery(this.isSetOperatorQuery);
      if (this.openBrace != null) {
         sqs_bigquery_sql.setOpenBrace(this.openBrace);
      }

      sqs_bigquery_sql.setSelectStatement(this.select_statement.toBigQuerySelect(sqs_bigquery_sql, this));
      if (this.from_clause != null) {
         sqs_bigquery_sql.setFromClause(this.from_clause.toBigQuerySelect(sqs_bigquery_sql, this));
      }

      if (this.whereExpression != null) {
         sqs_bigquery_sql.setWhereExpression(this.whereExpression.toBigQuerySelect(sqs_bigquery_sql, this));
      }

      if (this.order_by_statement != null) {
         sqs_bigquery_sql.setOrderByStatement(this.order_by_statement.toBigQuerySelect(sqs_bigquery_sql, this));
      }

      if (this.computeByVector != null) {
         Vector computeByStatementVector = new Vector();

         for(int i = 0; i < this.computeByVector.size(); ++i) {
            computeByStatementVector.add(((ComputeByStatement)this.computeByVector.get(i)).toBigQuerySelect(sqs_bigquery_sql, this));
         }

         sqs_bigquery_sql.setComputeByStatements(computeByStatementVector);
         sqs_bigquery_sql.setCommentForCompute(true);
      }

      if (sqs_bigquery_sql.getGroupByStatement() == null && this.group_by_statement != null) {
         sqs_bigquery_sql.setGroupByStatement(this.group_by_statement.toBigQuerySelect(sqs_bigquery_sql, this));
      }

      if (this.set_operator_clause != null) {
         sqs_bigquery_sql.setSetOperatorClause(this.set_operator_clause.toBigQuerySelect(sqs_bigquery_sql, this));
         this.replaceSelectItemsForStringLiteralsAndNULLString(sqs_bigquery_sql, "TEXT");
      }

      if (this.having_statement != null) {
         sqs_bigquery_sql.setHavingStatement(this.having_statement.toBigQuerySelect(sqs_bigquery_sql, this));
         this.handleHavingClauseWithoutGroupByClauseQueries(sqs_bigquery_sql);
      }

      if (this.into_statement != null) {
         sqs_bigquery_sql.setIntoStatement(this.into_statement.toBigQuerySelect(sqs_bigquery_sql, this));
      }

      if (this.hierarchical_query_clause != null) {
         throw new ConvertException();
      } else {
         if (this.limit_clause != null) {
            sqs_bigquery_sql.setLimitClause(this.limit_clause.toBigQuerySelect(sqs_bigquery_sql, this));
         }

         if (this.fetch_clause != null) {
            sqs_bigquery_sql.setFetchClause(this.fetch_clause.toBigQuerySelect(sqs_bigquery_sql, this));
         }

         sqs_bigquery_sql.setOptionalHintClause((OptionalHintClause)null);
         if (this.closeBrace != null) {
            sqs_bigquery_sql.setCloseBrace(this.closeBrace);
         }

         return sqs_bigquery_sql;
      }
   }

   public SelectQueryStatement toPostgreSQL() throws ConvertException {
      SelectQueryStatement sqs_postgre_sql = new SelectQueryStatement();
      sqs_postgre_sql.setSetOperatorQuery(this.isSetOperatorQuery);
      sqs_postgre_sql.setQueryConversionPropHandler(this.getQueryConvPropHandler());
      if (this.openBrace != null) {
         sqs_postgre_sql.setOpenBrace(this.openBrace);
      }

      sqs_postgre_sql.setSelectStatement(this.select_statement.toPostgreSQLSelect(sqs_postgre_sql, this));
      if (this.from_clause != null) {
         sqs_postgre_sql.setFromClause(this.from_clause.toPostgreSQLSelect(sqs_postgre_sql, this));
      }

      if (this.whereExpression != null) {
         sqs_postgre_sql.setWhereExpression(this.whereExpression.toPostgreSQLSelect(sqs_postgre_sql, this));
      }

      if (this.order_by_statement != null) {
         sqs_postgre_sql.setOrderByStatement(this.order_by_statement.toPostgreSQLSelect(sqs_postgre_sql, this));
      }

      if (this.computeByVector != null) {
         Vector computeByStatementVector = new Vector();

         for(int i = 0; i < this.computeByVector.size(); ++i) {
            computeByStatementVector.add(((ComputeByStatement)this.computeByVector.get(i)).toPostgreSQLSelect(sqs_postgre_sql, this));
         }

         sqs_postgre_sql.setComputeByStatements(computeByStatementVector);
         sqs_postgre_sql.setCommentForCompute(true);
      }

      if (sqs_postgre_sql.getGroupByStatement() == null && this.group_by_statement != null) {
         sqs_postgre_sql.setGroupByStatement(this.group_by_statement.toPostgreSQLSelect(sqs_postgre_sql, this));
      }

      if (this.set_operator_clause != null) {
         sqs_postgre_sql.setSetOperatorClause(this.set_operator_clause.toPostgreSQLSelect(sqs_postgre_sql, this));
         this.replaceSelectItemsForStringLiteralsAndNULLString(sqs_postgre_sql, StringFunctions.getStringDataType(this));
      }

      if (this.having_statement != null) {
         sqs_postgre_sql.setHavingStatement(this.having_statement.toPostgreSQLSelect(sqs_postgre_sql, this));
         this.handleHavingClauseWithoutGroupByClauseQueries(sqs_postgre_sql);
      }

      if (this.into_statement != null) {
         sqs_postgre_sql.setIntoStatement(this.into_statement.toPostgreSQLSelect(sqs_postgre_sql, this));
      }

      if (this.hierarchical_query_clause != null) {
         throw new ConvertException();
      } else {
         if (this.limit_clause != null) {
            sqs_postgre_sql.setLimitClause(this.limit_clause.toPostgreSQLSelect(sqs_postgre_sql, this));
         }

         if (this.fetch_clause != null) {
            sqs_postgre_sql.setFetchClause(this.fetch_clause.toPostgreSQLSelect(sqs_postgre_sql, this));
         }

         sqs_postgre_sql.setOptionalHintClause((OptionalHintClause)null);
         if (this.closeBrace != null) {
            sqs_postgre_sql.setCloseBrace(this.closeBrace);
         }

         return sqs_postgre_sql;
      }
   }

   public SelectQueryStatement toMySQL() throws ConvertException {
      SelectQueryStatement sqs_mysql_sql = new SelectQueryStatement();
      sqs_mysql_sql.setMySqlLiveFlag(this.isMySqlLive());
      sqs_mysql_sql.setMongoDbFlag(this.isMongoDb());
      sqs_mysql_sql.setSQLDialect(this.getSQLDialect());
      sqs_mysql_sql.setIsUnPivotUnionSQS(this.isUnPivotUnionSQS());
      sqs_mysql_sql.setQueryConvDataHandler(this.getQueryConvDataHandler());
      sqs_mysql_sql.setPivotOperatorColumnList(this.getPivotOperatorColumnList());
      sqs_mysql_sql.setIsQtNewFlow(this.getIsQtNewFlow());
      sqs_mysql_sql.setQueryConversionPropHandler(this.queryConvPropHandler);
      if (this.commentObject != null) {
         this.commentObject.setSQLDialect(5);
      }

      sqs_mysql_sql.setCommentClass(this.commentObject);
      sqs_mysql_sql.setSelectStatement(this.select_statement.toMySQLSelect(sqs_mysql_sql, this, true));
      if (this.from_clause != null) {
         sqs_mysql_sql.setFromClause(this.from_clause.toMySQLSelect(sqs_mysql_sql, this));
      } else if (this.isHyperSql()) {
         FromClause fc = new FromClause();
         FromTable ft = new FromTable();
         Vector fil = new Vector();
         fc.setFromClause("FROM");
         ft.setTableName("(VALUES(0))");
         fil.addElement(ft);
         fc.setFromItemList(fil);
         sqs_mysql_sql.setFromClause(fc);
      }

      if (this.whereExpression != null) {
         sqs_mysql_sql.setWhereExpression(this.whereExpression.toMySQLSelect(sqs_mysql_sql, this));
      }

      if (this.order_by_statement != null) {
         sqs_mysql_sql.setOrderByStatement(this.order_by_statement.toMySQLSelect(sqs_mysql_sql, this));
      }

      if (this.computeByVector != null) {
         Vector computeByStatementVector = new Vector();

         for(int i = 0; i < this.computeByVector.size(); ++i) {
            computeByStatementVector.add(((ComputeByStatement)this.computeByVector.get(i)).toPostgreSQLSelect(sqs_mysql_sql, this));
         }

         sqs_mysql_sql.setComputeByStatements(computeByStatementVector);
         sqs_mysql_sql.setCommentForCompute(true);
      }

      if (sqs_mysql_sql.getGroupByStatement() == null && this.group_by_statement != null) {
         sqs_mysql_sql.setGroupByStatement(this.group_by_statement.toMySQLSelect(sqs_mysql_sql, this));
      }

      if (this.set_operator_clause != null) {
         sqs_mysql_sql.setSetOperatorClause(this.set_operator_clause.toMySQLSelect(sqs_mysql_sql, this));
      }

      if (this.having_statement != null) {
         sqs_mysql_sql.setHavingStatement(this.having_statement.toMySQLSelect(sqs_mysql_sql, this));
      }

      if (this.into_statement != null) {
         sqs_mysql_sql.setIntoStatement(this.into_statement.toMySQLSelect(sqs_mysql_sql, this));
      }

      if (this.hierarchical_query_clause != null) {
         throw new ConvertException();
      } else {
         if (this.limit_clause != null) {
            sqs_mysql_sql.setLimitClause(this.limit_clause.toMySQLSelect(sqs_mysql_sql, this));
         }

         if (this.fetch_clause != null) {
            sqs_mysql_sql.setFetchClause(this.fetch_clause.toMySQLSelect(sqs_mysql_sql, this));
         }

         sqs_mysql_sql.setOptionalHintClause((OptionalHintClause)null);
         return sqs_mysql_sql;
      }
   }

   public SelectQueryStatement toSnowflake() throws ConvertException {
      SelectQueryStatement sqs_snowflake_sql = new SelectQueryStatement();
      sqs_snowflake_sql.setSetOperatorQuery(this.isSetOperatorQuery);
      sqs_snowflake_sql.setQueryConversionPropHandler(this.getQueryConvPropHandler());
      if (this.openBrace != null) {
         sqs_snowflake_sql.setOpenBrace(this.openBrace);
      }

      sqs_snowflake_sql.setSelectStatement(this.select_statement.toSnowflakeSelect(sqs_snowflake_sql, this));
      if (this.from_clause != null) {
         sqs_snowflake_sql.setFromClause(this.from_clause.toSnowflakeSelect(sqs_snowflake_sql, this));
      }

      if (this.whereExpression != null) {
         sqs_snowflake_sql.setWhereExpression(this.whereExpression.toSnowflakeSelect(sqs_snowflake_sql, this));
      }

      if (this.order_by_statement != null) {
         sqs_snowflake_sql.setOrderByStatement(this.order_by_statement.toSnowflakeSelect(sqs_snowflake_sql, this));
      }

      if (this.computeByVector != null) {
         Vector computeByStatementVector = new Vector();

         for(int i = 0; i < this.computeByVector.size(); ++i) {
            computeByStatementVector.add(((ComputeByStatement)this.computeByVector.get(i)).toSnowflakeSelect(sqs_snowflake_sql, this));
         }

         sqs_snowflake_sql.setComputeByStatements(computeByStatementVector);
         sqs_snowflake_sql.setCommentForCompute(true);
      }

      if (sqs_snowflake_sql.getGroupByStatement() == null && this.group_by_statement != null) {
         sqs_snowflake_sql.setGroupByStatement(this.group_by_statement.toSnowflakeSelect(sqs_snowflake_sql, this));
      }

      if (this.set_operator_clause != null) {
         sqs_snowflake_sql.setSetOperatorClause(this.set_operator_clause.toSnowflakeSelect(sqs_snowflake_sql, this));
         this.replaceSelectItemsForStringLiteralsAndNULLString(sqs_snowflake_sql, "TEXT");
      }

      if (this.having_statement != null) {
         sqs_snowflake_sql.setHavingStatement(this.having_statement.toSnowflakeSelect(sqs_snowflake_sql, this));
         this.handleHavingClauseWithoutGroupByClauseQueries(sqs_snowflake_sql);
      }

      if (this.into_statement != null) {
         sqs_snowflake_sql.setIntoStatement(this.into_statement.toSnowflakeSelect(sqs_snowflake_sql, this));
      }

      if (this.hierarchical_query_clause != null) {
         throw new ConvertException();
      } else {
         if (this.limit_clause != null) {
            sqs_snowflake_sql.setLimitClause(this.limit_clause.toSnowflakeSelect(sqs_snowflake_sql, this));
         }

         if (this.fetch_clause != null) {
            sqs_snowflake_sql.setFetchClause(this.fetch_clause.toSnowflakeSelect(sqs_snowflake_sql, this));
         }

         sqs_snowflake_sql.setOptionalHintClause((OptionalHintClause)null);
         if (this.closeBrace != null) {
            sqs_snowflake_sql.setCloseBrace(this.closeBrace);
         }

         return sqs_snowflake_sql;
      }
   }

   public SelectQueryStatement toAthena() throws ConvertException {
      SelectQueryStatement sqs_athena_sql = new SelectQueryStatement();
      sqs_athena_sql.setSetOperatorQuery(this.isSetOperatorQuery);
      sqs_athena_sql.setQueryConversionPropHandler(this.getQueryConvPropHandler());
      if (this.openBrace != null) {
         sqs_athena_sql.setOpenBrace(this.openBrace);
      }

      sqs_athena_sql.setSelectStatement(this.select_statement.toAthenaSelect(sqs_athena_sql, this));
      if (this.from_clause != null) {
         sqs_athena_sql.setFromClause(this.from_clause.toAthenaSelect(sqs_athena_sql, this));
      }

      if (this.whereExpression != null) {
         sqs_athena_sql.setWhereExpression(this.whereExpression.toAthenaSelect(sqs_athena_sql, this));
      }

      if (this.order_by_statement != null) {
         sqs_athena_sql.setOrderByStatement(this.order_by_statement.toAthenaSelect(sqs_athena_sql, this));
      }

      if (this.computeByVector != null) {
         Vector computeByStatementVector = new Vector();

         for(int i = 0; i < this.computeByVector.size(); ++i) {
            computeByStatementVector.add(((ComputeByStatement)this.computeByVector.get(i)).toAthenaSelect(sqs_athena_sql, this));
         }

         sqs_athena_sql.setComputeByStatements(computeByStatementVector);
         sqs_athena_sql.setCommentForCompute(true);
      }

      if (sqs_athena_sql.getGroupByStatement() == null && this.group_by_statement != null) {
         sqs_athena_sql.setGroupByStatement(this.group_by_statement.toAthenaSelect(sqs_athena_sql, this));
      }

      if (this.set_operator_clause != null) {
         sqs_athena_sql.setSetOperatorClause(this.set_operator_clause.toAthenaSelect(sqs_athena_sql, this));
         this.replaceSelectItemsForStringLiteralsAndNULLString(sqs_athena_sql, "VARCHAR");
      }

      if (this.having_statement != null) {
         sqs_athena_sql.setHavingStatement(this.having_statement.toAthenaSelect(sqs_athena_sql, this));
         this.handleHavingClauseWithoutGroupByClauseQueries(sqs_athena_sql);
      }

      if (this.into_statement != null) {
         sqs_athena_sql.setIntoStatement(this.into_statement.toAthenaSelect(sqs_athena_sql, this));
      }

      if (this.hierarchical_query_clause != null) {
         throw new ConvertException();
      } else {
         if (this.limit_clause != null) {
            sqs_athena_sql.setLimitClause(this.limit_clause.toAthenaSelect(sqs_athena_sql, this));
         }

         if (this.fetch_clause != null) {
            sqs_athena_sql.setFetchClause(this.fetch_clause.toAthenaSelect(sqs_athena_sql, this));
         }

         sqs_athena_sql.setOptionalHintClause((OptionalHintClause)null);
         if (this.closeBrace != null) {
            sqs_athena_sql.setCloseBrace(this.closeBrace);
         }

         return sqs_athena_sql;
      }
   }

   public SelectQueryStatement toSapHana() throws ConvertException {
      SelectQueryStatement sqs_saphana_sql = new SelectQueryStatement();
      sqs_saphana_sql.setSetOperatorQuery(this.isSetOperatorQuery);
      sqs_saphana_sql.setQueryConversionPropHandler(this.getQueryConvPropHandler());
      if (this.openBrace != null) {
         sqs_saphana_sql.setOpenBrace(this.openBrace);
      }

      sqs_saphana_sql.setSelectStatement(this.select_statement.toSapHanaSelect(sqs_saphana_sql, this));
      if (this.from_clause != null) {
         sqs_saphana_sql.setFromClause(this.from_clause.toSapHanaSelect(sqs_saphana_sql, this));
      }

      if (this.whereExpression != null) {
         sqs_saphana_sql.setWhereExpression(this.whereExpression.toSapHanaSelect(sqs_saphana_sql, this));
      }

      if (this.order_by_statement != null) {
         sqs_saphana_sql.setOrderByStatement(this.order_by_statement.toSapHanaSelect(sqs_saphana_sql, this));
      }

      if (this.computeByVector != null) {
         Vector computeByStatementVector = new Vector();

         for(int i = 0; i < this.computeByVector.size(); ++i) {
            computeByStatementVector.add(((ComputeByStatement)this.computeByVector.get(i)).toSapHanaSelect(sqs_saphana_sql, this));
         }

         sqs_saphana_sql.setComputeByStatements(computeByStatementVector);
         sqs_saphana_sql.setCommentForCompute(true);
      }

      if (sqs_saphana_sql.getGroupByStatement() == null && this.group_by_statement != null) {
         sqs_saphana_sql.setGroupByStatement(this.group_by_statement.toSapHanaSelect(sqs_saphana_sql, this));
      }

      if (this.set_operator_clause != null) {
         sqs_saphana_sql.setSetOperatorClause(this.set_operator_clause.toSapHanaSelect(sqs_saphana_sql, this));
         this.replaceSelectItemsForStringLiteralsAndNULLString(sqs_saphana_sql, "VARCHAR");
      }

      if (this.having_statement != null) {
         sqs_saphana_sql.setHavingStatement(this.having_statement.toSapHanaSelect(sqs_saphana_sql, this));
         this.handleHavingClauseWithoutGroupByClauseQueries(sqs_saphana_sql);
      }

      if (this.into_statement != null) {
         sqs_saphana_sql.setIntoStatement(this.into_statement.toSapHanaSelect(sqs_saphana_sql, this));
      }

      if (this.hierarchical_query_clause != null) {
         throw new ConvertException();
      } else {
         if (this.limit_clause != null) {
            sqs_saphana_sql.setLimitClause(this.limit_clause.toSapHanaSelect(sqs_saphana_sql, this));
         }

         if (this.fetch_clause != null) {
            sqs_saphana_sql.setFetchClause(this.fetch_clause.toSapHanaSelect(sqs_saphana_sql, this));
         }

         sqs_saphana_sql.setOptionalHintClause((OptionalHintClause)null);
         if (this.closeBrace != null) {
            sqs_saphana_sql.setCloseBrace(this.closeBrace);
         }

         return sqs_saphana_sql;
      }
   }

   public SelectQueryStatement toSqlite() throws ConvertException {
      SelectQueryStatement sqs_sqlite_sql = new SelectQueryStatement();
      sqs_sqlite_sql.setSetOperatorQuery(this.isSetOperatorQuery);
      sqs_sqlite_sql.setQueryConversionPropHandler(this.getQueryConvPropHandler());
      if (this.openBrace != null) {
         sqs_sqlite_sql.setOpenBrace(this.openBrace);
      }

      sqs_sqlite_sql.setSelectStatement(this.select_statement.toSqliteSelect(sqs_sqlite_sql, this));
      if (this.from_clause != null) {
         sqs_sqlite_sql.setFromClause(this.from_clause.toSqliteSelect(sqs_sqlite_sql, this));
      }

      if (this.whereExpression != null) {
         sqs_sqlite_sql.setWhereExpression(this.whereExpression.toSqliteSelect(sqs_sqlite_sql, this));
      }

      if (this.order_by_statement != null) {
         sqs_sqlite_sql.setOrderByStatement(this.order_by_statement.toSqliteSelect(sqs_sqlite_sql, this));
      }

      if (this.computeByVector != null) {
         Vector computeByStatementVector = new Vector();

         for(int i = 0; i < this.computeByVector.size(); ++i) {
            computeByStatementVector.add(((ComputeByStatement)this.computeByVector.get(i)).toSqliteSelect(sqs_sqlite_sql, this));
         }

         sqs_sqlite_sql.setComputeByStatements(computeByStatementVector);
         sqs_sqlite_sql.setCommentForCompute(true);
      }

      if (sqs_sqlite_sql.getGroupByStatement() == null && this.group_by_statement != null) {
         sqs_sqlite_sql.setGroupByStatement(this.group_by_statement.toSqliteSelect(sqs_sqlite_sql, this));
      }

      if (this.set_operator_clause != null) {
         sqs_sqlite_sql.setSetOperatorClause(this.set_operator_clause.toSqliteSelect(sqs_sqlite_sql, this));
         this.replaceSelectItemsForStringLiteralsAndNULLString(sqs_sqlite_sql, "TEXT");
      }

      if (this.having_statement != null) {
         sqs_sqlite_sql.setHavingStatement(this.having_statement.toSqliteSelect(sqs_sqlite_sql, this));
         this.handleHavingClauseWithoutGroupByClauseQueries(sqs_sqlite_sql);
      }

      if (this.into_statement != null) {
         sqs_sqlite_sql.setIntoStatement(this.into_statement.toSqliteSelect(sqs_sqlite_sql, this));
      }

      if (this.hierarchical_query_clause != null) {
         throw new ConvertException();
      } else {
         if (this.limit_clause != null) {
            sqs_sqlite_sql.setLimitClause(this.limit_clause.toSqliteSelect(sqs_sqlite_sql, this));
         }

         if (this.fetch_clause != null) {
            sqs_sqlite_sql.setFetchClause(this.fetch_clause.toSqliteSelect(sqs_sqlite_sql, this));
         }

         sqs_sqlite_sql.setOptionalHintClause((OptionalHintClause)null);
         if (this.closeBrace != null) {
            sqs_sqlite_sql.setCloseBrace(this.closeBrace);
         }

         return sqs_sqlite_sql;
      }
   }

   public SelectQueryStatement toExcel() throws ConvertException {
      SelectQueryStatement sqs_excel_sql = new SelectQueryStatement();
      sqs_excel_sql.setSetOperatorQuery(this.isSetOperatorQuery);
      sqs_excel_sql.setQueryConversionPropHandler(this.queryConvPropHandler);
      sqs_excel_sql.setExcelFlag(this.isExcel);
      if (this.openBrace != null) {
         sqs_excel_sql.setOpenBrace(this.openBrace);
      }

      sqs_excel_sql.setSelectStatement(this.select_statement.toExcelSelect(sqs_excel_sql, this));
      Vector computeByStatementVector;
      int i;
      if (this.from_clause != null) {
         this.from_clause = this.from_clause.toExcelSelect(sqs_excel_sql, this);
         this.from_clause.setExcel(true);
         computeByStatementVector = new Vector(this.from_clause.getFromItemList());
         i = 0;
         int closeBraceCount = 0;

         FromTable ft;
         for(int i = 0; i < computeByStatementVector.size() - 1; ++i) {
            if (computeByStatementVector.elementAt(i + 1) instanceof FromTable && ((FromTable)computeByStatementVector.elementAt(i + 1)).getJoinClause() != null) {
               ++i;
               closeBraceCount += 2;
               ft = new FromTable();
               ft.setTableName(")");
               ft.setJoinClause("");
               this.from_clause.fromItemList.add(closeBraceCount, ft);
            }
         }

         while(i > 0) {
            ft = new FromTable();
            ft.setTableName("(");
            ft.setJoinClause("");
            this.from_clause.fromItemList.add(0, ft);
            --i;
         }

         sqs_excel_sql.setFromClause(this.from_clause);
      } else {
         FromClause fc = new FromClause();
         FromTable ft = new FromTable();
         Vector fil = new Vector();
         if (!SwisSQLAPI.excelTblName.isEmpty()) {
            fc.setFromClause("FROM");
            ft.setTableName(SwisSQLAPI.excelTblName);
            fil.addElement(ft);
            fc.setFromItemList(fil);
         }

         sqs_excel_sql.setFromClause(fc);
      }

      if (this.whereExpression != null) {
         sqs_excel_sql.setWhereExpression(this.whereExpression.toExcelSelect(sqs_excel_sql, this));
      }

      if (this.order_by_statement != null) {
         sqs_excel_sql.setOrderByStatement(this.order_by_statement.toExcelSelect(sqs_excel_sql, this));
      }

      if (this.computeByVector != null) {
         computeByStatementVector = new Vector();

         for(i = 0; i < this.computeByVector.size(); ++i) {
            computeByStatementVector.add(((ComputeByStatement)this.computeByVector.get(i)).toExcelSelect(sqs_excel_sql, this));
         }

         sqs_excel_sql.setComputeByStatements(computeByStatementVector);
         sqs_excel_sql.setCommentForCompute(true);
      }

      if (sqs_excel_sql.getGroupByStatement() == null && this.group_by_statement != null) {
         sqs_excel_sql.setGroupByStatement(this.group_by_statement.toExcelSelect(sqs_excel_sql, this));
      }

      if (this.set_operator_clause != null) {
         sqs_excel_sql.setSetOperatorClause(this.set_operator_clause.toExcelSelect(sqs_excel_sql, this));
         this.replaceSelectItemsForStringLiteralsAndNULLString(sqs_excel_sql, "TEXT");
      }

      if (this.having_statement != null) {
         sqs_excel_sql.setHavingStatement(this.having_statement.toExcelSelect(sqs_excel_sql, this));
         this.handleHavingClauseWithoutGroupByClauseQueries(sqs_excel_sql);
      }

      if (this.into_statement != null) {
         sqs_excel_sql.setIntoStatement(this.into_statement.toExcelSelect(sqs_excel_sql, this));
      }

      if (this.hierarchical_query_clause != null) {
         throw new ConvertException();
      } else {
         if (this.limit_clause != null) {
            sqs_excel_sql.setLimitClause(this.limit_clause.toExcelSelect(sqs_excel_sql, this));
         }

         if (this.fetch_clause != null) {
            sqs_excel_sql.setFetchClause(this.fetch_clause.toExcelSelect(sqs_excel_sql, this));
         }

         sqs_excel_sql.setOptionalHintClause((OptionalHintClause)null);
         if (this.closeBrace != null) {
            sqs_excel_sql.setCloseBrace(this.closeBrace);
         }

         return sqs_excel_sql;
      }
   }

   public SelectQueryStatement toMsAccessJdbc() throws ConvertException {
      SelectQueryStatement sqs_msaccess_jdbc_sql = new SelectQueryStatement();
      sqs_msaccess_jdbc_sql.setSetOperatorQuery(this.isSetOperatorQuery);
      sqs_msaccess_jdbc_sql.setQueryConversionPropHandler(this.queryConvPropHandler);
      sqs_msaccess_jdbc_sql.setMsAccessJdbcFlag(this.isMsAccessJdbc);
      if (this.openBrace != null) {
         sqs_msaccess_jdbc_sql.setOpenBrace(this.openBrace);
      }

      sqs_msaccess_jdbc_sql.setSelectStatement(this.select_statement.toMsAccessJdbcSelect(sqs_msaccess_jdbc_sql, this));
      if (this.from_clause != null) {
         sqs_msaccess_jdbc_sql.setFromClause(this.from_clause.toMsAccessJdbcSelect(sqs_msaccess_jdbc_sql, this));
      } else {
         FromClause fc = new FromClause();
         FromTable ft = new FromTable();
         Vector fil = new Vector();
         fc.setFromClause("FROM");
         ft.setTableName("(VALUES(0))");
         fil.addElement(ft);
         fc.setFromItemList(fil);
         sqs_msaccess_jdbc_sql.setFromClause(fc);
      }

      if (this.whereExpression != null) {
         sqs_msaccess_jdbc_sql.setWhereExpression(this.whereExpression.toMsAccessJdbcSelect(sqs_msaccess_jdbc_sql, this));
      }

      if (this.order_by_statement != null) {
         sqs_msaccess_jdbc_sql.setOrderByStatement(this.order_by_statement.toMsAccessJdbcSelect(sqs_msaccess_jdbc_sql, this));
      }

      if (this.computeByVector != null) {
         Vector computeByStatementVector = new Vector();

         for(int i = 0; i < this.computeByVector.size(); ++i) {
            computeByStatementVector.add(((ComputeByStatement)this.computeByVector.get(i)).toMsAccessJdbcSelect(sqs_msaccess_jdbc_sql, this));
         }

         sqs_msaccess_jdbc_sql.setComputeByStatements(computeByStatementVector);
         sqs_msaccess_jdbc_sql.setCommentForCompute(true);
      }

      if (sqs_msaccess_jdbc_sql.getGroupByStatement() == null && this.group_by_statement != null) {
         sqs_msaccess_jdbc_sql.setGroupByStatement(this.group_by_statement.toMsAccessJdbcSelect(sqs_msaccess_jdbc_sql, this));
      }

      if (this.set_operator_clause != null) {
         sqs_msaccess_jdbc_sql.setSetOperatorClause(this.set_operator_clause.toMsAccessJdbcSelect(sqs_msaccess_jdbc_sql, this));
         this.replaceSelectItemsForStringLiteralsAndNULLString(sqs_msaccess_jdbc_sql, "TEXT");
      }

      if (this.having_statement != null) {
         sqs_msaccess_jdbc_sql.setHavingStatement(this.having_statement.toMsAccessJdbcSelect(sqs_msaccess_jdbc_sql, this));
         this.handleHavingClauseWithoutGroupByClauseQueries(sqs_msaccess_jdbc_sql);
      }

      if (this.into_statement != null) {
         sqs_msaccess_jdbc_sql.setIntoStatement(this.into_statement.toMsAccessJdbcSelect(sqs_msaccess_jdbc_sql, this));
      }

      if (this.hierarchical_query_clause != null) {
         throw new ConvertException();
      } else {
         if (this.limit_clause != null) {
            sqs_msaccess_jdbc_sql.setLimitClause(this.limit_clause.toMsAccessJdbcSelect(sqs_msaccess_jdbc_sql, this));
         }

         if (this.fetch_clause != null) {
            sqs_msaccess_jdbc_sql.setFetchClause(this.fetch_clause.toMsAccessJdbcSelect(sqs_msaccess_jdbc_sql, this));
         }

         sqs_msaccess_jdbc_sql.setOptionalHintClause((OptionalHintClause)null);
         if (this.closeBrace != null) {
            sqs_msaccess_jdbc_sql.setCloseBrace(this.closeBrace);
         }

         return sqs_msaccess_jdbc_sql;
      }
   }

   public SelectQueryStatement toOracle() throws ConvertException {
      SelectQueryStatement sqs_oracle_sql = new SelectQueryStatement();
      sqs_oracle_sql.setObjectContext(this.objectContext);
      boolean topWithOrderBy = false;
      String rowSpecifier = null;
      if (this.commentObject != null) {
         this.commentObject.setSQLDialect(1);
      }

      sqs_oracle_sql.setCommentClass(this.commentObject);
      SelectQueryStatement newSQS = this;
      XMLStatement newXmls = this.xmls;
      boolean xmlForest = false;
      String xmlString = null;
      String tableName = null;
      FromClause frcl = this.getFromClause();
      Vector fromIList = null;
      if (frcl != null) {
         fromIList = frcl.getFromItemList();
         if (fromIList != null) {
            for(int i = 0; i < fromIList.size(); ++i) {
               try {
                  Object tObj = fromIList.get(i);
                  if (tObj instanceof FromTable) {
                     tableName = ((FromTable)tObj).getTableName().toString();
                  } else if (tObj instanceof String) {
                     tableName = tObj.toString();
                  }
               } catch (Exception var37) {
               }
            }
         }
      }

      SelectStatement ssForId = this.getSelectStatement();
      Vector siList = ssForId.getSelectItemList();
      CreateSequenceStatement createSeq = new CreateSequenceStatement();
      Vector selectList;
      int i;
      Vector outerItems1;
      String s;
      if (siList != null) {
         for(int i = 0; i < siList.size(); ++i) {
            Object o = siList.get(i);
            if (o instanceof SelectColumn) {
               SelectColumn sclForId = (SelectColumn)o;
               selectList = sclForId.getColumnExpression();
               if (selectList != null) {
                  for(i = 0; i < selectList.size(); ++i) {
                     if (selectList.get(i) instanceof FunctionCalls && ((FunctionCalls)selectList.get(i)).getFunctionName() != null) {
                        TableColumn tcForId = ((FunctionCalls)selectList.get(i)).getFunctionName();
                        outerItems1 = ((FunctionCalls)selectList.get(i)).getFunctionArguments();
                        if (tcForId.getColumnName() != null && tcForId.getColumnName().equalsIgnoreCase("IDENTITY")) {
                           if (outerItems1 != null) {
                              if (outerItems1.size() > 0) {
                                 createSeq.setDataType((Datatype)outerItems1.get(0));
                                 createSeq.setAs("AS");
                              }

                              if (outerItems1.size() > 1) {
                                 createSeq.setStartValue(outerItems1.get(1) + "");
                                 createSeq.setStart("START");
                                 createSeq.setWith("WITH");
                              }

                              if (outerItems1.size() > 2) {
                                 createSeq.setIncrementValue(outerItems1.get(2) + "");
                                 createSeq.setIncrementString("INCREMENT BY");
                              }

                              this.setSequenceForIdentityFn(createSeq);
                           }

                           s = sclForId.getAliasName();
                           tcForId.setColumnName(s);
                           sclForId.setIsAS((String)null);
                           sclForId.setAliasName((String)null);
                           selectList.setElementAt(tcForId, i);
                        }
                     }
                  }
               }

               sclForId.setColumnExpression(selectList);
            }
         }
      }

      SetOperatorClause sopc = this.getSetOperatorClause();

      SelectQueryStatement subSQS;
      int k;
      while(tableName == null && sopc != null) {
         subSQS = sopc.getSelectQueryStatement();
         if (subSQS != null) {
            sopc = subSQS.getSetOperatorClause();
            frcl = subSQS.getFromClause();
            if (frcl != null) {
               fromIList = frcl.getFromItemList();
               if (fromIList != null) {
                  for(k = 0; k < fromIList.size(); ++k) {
                     try {
                        Object tObj = fromIList.get(k);
                        if (tObj instanceof FromTable) {
                           tableName = ((FromTable)tObj).getTableName().toString();
                        } else if (tObj instanceof String) {
                           tableName = tObj.toString();
                        }
                     } catch (Exception var36) {
                     }
                  }
               }
            }
         }
      }

      Vector wi;
      Object wItem;
      SelectStatement newSS;
      Vector newSelectItemList;
      int depth;
      FromTable ft;
      String alName;
      int i;
      Object o;
      SelectColumn sc;
      if (newXmls != null && newXmls.getXMLType().equalsIgnoreCase("EXPLICIT")) {
         int maxDepth = 1;
         newSS = this.getSelectStatement();
         selectList = newSS.getSelectItemList();
         if (selectList == null) {
            selectList = new Vector();
         }

         int i;
         Object o;
         for(i = 0; i < selectList.size(); ++i) {
            i = maxDepth;
            o = selectList.get(i);
            if (o instanceof SelectColumn) {
               s = ((SelectColumn)o).getAliasName();
               if (s.indexOf("!") > 0) {
                  try {
                     i = Integer.parseInt(s.substring(s.indexOf("!") + 1, s.lastIndexOf("!")));
                  } catch (Exception var35) {
                     var35.printStackTrace();
                  }

                  maxDepth = i > maxDepth ? i : maxDepth;
               }
            }
         }

         newSelectItemList = this.getSelectStatement().getSelectItemList();
         if (newSelectItemList == null) {
            newSelectItemList = new Vector();
         }

         String clName;
         Object clObj;
         for(i = 1; i < newSelectItemList.size(); ++i) {
            o = newSelectItemList.get(i);
            if (o instanceof SelectColumn) {
               s = null;
               Object subO = ((SelectColumn)o).getColumnExpression().elementAt(0);
               if (subO instanceof String) {
                  s = subO.toString();
               } else if (subO instanceof TableColumn) {
                  s = ((TableColumn)subO).getColumnName();
               }

               if (s != null && s.equalsIgnoreCase("NULL")) {
                  SelectQueryStatement newTempSQS = this;

                  while(newSQS.getSetOperatorClause() != null) {
                     newTempSQS = newTempSQS.getSetOperatorClause().getSelectQueryStatement();
                     if (newTempSQS != null) {
                        wi = newTempSQS.getSelectStatement().getSelectItemList();
                        Object oo = wi.get(i);
                        if (oo instanceof SelectColumn) {
                           clName = null;
                           clObj = ((SelectColumn)oo).getColumnExpression().elementAt(0);
                           if (subO instanceof String) {
                              clName = clObj.toString();
                           } else if (subO instanceof TableColumn) {
                              clName = ((TableColumn)clObj).getColumnName();
                           }

                           if (clName != null && !clName.equalsIgnoreCase("NULL")) {
                              wItem = newSelectItemList.remove(i);
                              ((SelectColumn)oo).setAliasName(((SelectColumn)wItem).getAliasName());
                              newSelectItemList.add(i, (SelectColumn)oo);
                              break;
                           }
                        }
                     }
                  }
               }
            }
         }

         if (newSelectItemList.size() > 1) {
            for(i = 0; i < 2; ++i) {
               o = newSelectItemList.get(0);
               if (o instanceof SelectColumn) {
                  s = ((SelectColumn)o).getAliasName();
                  if (s.equalsIgnoreCase("TAG")) {
                     newSelectItemList.remove(0);
                  }

                  if (s.equalsIgnoreCase("PARENT")) {
                     SelectColumn sc = (SelectColumn)newSelectItemList.remove(0);
                     Object tempO = sc.getColumnExpression().elementAt(0);
                     if (tempO instanceof String && tempO.toString().equalsIgnoreCase("NULL")) {
                        xmlForest = true;
                     } else if (tempO instanceof TableColumn && ((TableColumn)tempO).getColumnName().equalsIgnoreCase("NULL")) {
                        xmlForest = true;
                     } else {
                        xmlForest = false;
                     }
                  }
               }
            }
         }

         StringBuffer xmlStringBuf = new StringBuffer();
         if (xmlForest) {
            xmlStringBuf.append("XMLFOREST(");
            depth = 0;

            while(true) {
               if (depth >= newSelectItemList.size()) {
                  xmlString = xmlStringBuf.toString();
                  xmlString = xmlString.substring(0, xmlString.length() - 1);
                  xmlString = xmlString + ")";
                  break;
               }

               SelectColumn tempSc = (SelectColumn)newSelectItemList.get(depth);
               alName = tempSc.getAliasName();
               String clName = null;
               o = tempSc.getColumnExpression().elementAt(0);
               if (o != null) {
                  if (o instanceof String) {
                     clName = o.toString();
                  } else if (o instanceof TableColumn) {
                     clName = ((TableColumn)o).getColumnName();
                  }
               }

               if (alName != null) {
                  if (alName.startsWith("[")) {
                     alName = alName.substring(1);
                  }

                  if (alName.endsWith("]")) {
                     alName = alName.substring(0, alName.length() - 1);
                  }
               }

               if (alName != null) {
                  alName = alName.substring(alName.lastIndexOf("!") + 1, alName.length());
               }

               if (clName != null) {
                  if (clName.startsWith("[")) {
                     clName = clName.substring(1);
                  }

                  if (clName.endsWith("]")) {
                     clName = clName.substring(0, clName.length() - 1);
                  }
               }

               if (clName != null && alName != null) {
                  xmlStringBuf.append(clName + " AS " + alName + ",");
               } else if (clName != null) {
                  xmlStringBuf.append(clName + ",");
               }

               ++depth;
            }
         } else {
            xmlStringBuf.append("XMLELEMENT(");
            depth = 1;

            while(true) {
               if (depth > maxDepth) {
                  xmlString = xmlStringBuf.toString();
                  xmlString = xmlString + ")";
                  break;
               }

               if (depth > 1) {
                  xmlStringBuf.append(", XMLELEMENT(");
               }

               boolean tagAdded = false;
               int currentDepth = 1;

               for(i = 0; i < newSelectItemList.size(); ++i) {
                  o = selectList.get(i);
                  if (o instanceof SelectColumn) {
                     String alName = ((SelectColumn)o).getAliasName();
                     if (alName != null && alName.indexOf("!") > 0) {
                        try {
                           currentDepth = Integer.parseInt(alName.substring(alName.indexOf("!") + 1, alName.lastIndexOf("!")));
                        } catch (Exception var34) {
                           var34.printStackTrace();
                        }
                     }

                     if (currentDepth == depth) {
                        clName = null;
                        clObj = ((SelectColumn)o).getColumnExpression().elementAt(0);
                        if (clObj != null) {
                           if (clObj instanceof String) {
                              clName = clObj.toString();
                           } else if (clObj instanceof TableColumn) {
                              clName = ((TableColumn)clObj).getColumnName();
                           } else {
                              clName = clObj.toString();
                           }
                        }

                        if (alName != null) {
                           if (alName.startsWith("[")) {
                              alName = alName.substring(1);
                           }

                           if (alName.endsWith("]")) {
                              alName = alName.substring(0, alName.length() - 1);
                           }
                        }

                        if (alName != null && !tagAdded) {
                           xmlStringBuf.append("\"" + alName.substring(0, alName.indexOf("!")) + "\"");
                           xmlStringBuf.append(", XMLATTRIBUTES(");
                           tagAdded = true;
                        }

                        if (alName != null) {
                           alName = alName.substring(alName.lastIndexOf("!") + 1, alName.length());
                        }

                        if (clName != null && alName != null) {
                           if (clName.startsWith("[")) {
                              clName = clName.substring(1);
                           }

                           if (clName.endsWith("]")) {
                              clName = clName.substring(0, clName.length() - 1);
                           }

                           xmlStringBuf.append(clName + " AS " + alName + ",");
                        } else if (clName != null) {
                           xmlStringBuf.append(clName + ",");
                        }
                     }
                  }
               }

               xmlString = xmlStringBuf.toString();
               xmlString = xmlString.substring(0, xmlString.length() - 1);
               xmlString = xmlString + ")";
               if (depth > 1) {
                  xmlString = xmlString + ")";
               }

               xmlStringBuf = new StringBuffer(xmlString);
               ++depth;
            }
         }

         frcl = new FromClause();
         frcl.setFromClause("FROM");
         ft = new FromTable();
         ft.setTableName(tableName);
         fromIList = new Vector();
         fromIList.add(ft);
         frcl.setFromItemList(fromIList);
         this.setFromClause(frcl);
         this.setSetOperatorClause((SetOperatorClause)null);
         if (this.getOrderByStatement() != null) {
            OrderByStatement obs = this.getOrderByStatement();
            Vector v = obs.getOrderItemList();
            if (v != null) {
               for(i = 0; i < v.size(); ++i) {
                  OrderItem oi = (OrderItem)v.get(i);
                  sc = oi.getOrderSpecifier();
                  if (sc != null) {
                     Object clObj = sc.getColumnExpression().elementAt(0);
                     if (clObj != null) {
                        if (clObj instanceof String && clObj.toString().indexOf("!") > 0) {
                           String tempS = clObj.toString();
                           tempS = tempS.substring(tempS.lastIndexOf("!") + 1, tempS.length());
                           if (tempS.endsWith("]")) {
                              tempS = tempS.substring(0, tempS.length() - 1);
                           }

                           sc.getColumnExpression().remove(0);
                           sc.getColumnExpression().add(tempS);
                        } else if (clObj instanceof TableColumn) {
                           TableColumn tc = (TableColumn)clObj;
                           String tempS = tc.getColumnName();
                           tempS = tempS.substring(tempS.lastIndexOf("!") + 1, tempS.length());
                           if (tempS.endsWith("]")) {
                              tempS = tempS.substring(0, tempS.length() - 1);
                           }

                           tc.setColumnName(tempS);
                        }
                     }
                  }
               }
            }
         }
      }

      int i;
      Vector items;
      if (this.getSelectStatement() != null) {
         rowSpecifier = this.getSelectStatement().getSelectRowSpecifier();
         if (rowSpecifier != null && rowSpecifier.trim().equalsIgnoreCase("TOP") && this.getOrderByStatement() != null) {
            topWithOrderBy = true;
            Vector selectitems = this.select_statement.getSelectItemList();
            k = 1;

            for(i = 0; i < selectitems.size(); ++i) {
               if (selectitems.get(i) instanceof SelectColumn) {
                  SelectColumn tempSelectColumn = (SelectColumn)selectitems.get(i);
                  items = tempSelectColumn.getColumnExpression();

                  for(depth = 0; depth < items.size(); ++depth) {
                     if (items.get(depth) instanceof FunctionCalls && tempSelectColumn.getAliasName() == null) {
                        FunctionCalls columnsWithFunctions = (FunctionCalls)items.get(depth);
                        alName = "ADV_ALIAS_" + k;
                        tempSelectColumn.setAliasName(alName);
                        tempSelectColumn.setIsAS("AS");
                        ++k;
                     }
                  }
               }
            }
         }
      }

      sqs_oracle_sql.setSelectStatement(this.select_statement.toOracleSelect(sqs_oracle_sql, this));
      String lockStatement;
      String forUpdateClause;
      Vector computeByStatementVector;
      if (this.xmls != null && this.xmls.getXMLType() != null) {
         lockStatement = this.xmls.getXMLType();
         String xmlString1;
         if (lockStatement.equalsIgnoreCase("AUTO") && this.xmls.getElements() != null) {
            xmlString1 = "XMLFOREST(";
            sqs_oracle_sql.getSelectStatement().setXMLString(xmlString1);
            sqs_oracle_sql.getSelectStatement().setXMLEndTag(")");
         } else if (lockStatement.equalsIgnoreCase("AUTO")) {
            xmlString1 = "XMLELEMENT(";
            forUpdateClause = this.getTableNameForXMLElement(this.from_clause);
            xmlString1 = xmlString1 + "\"" + forUpdateClause + "\"" + ", XMLATTRIBUTES(";
            sqs_oracle_sql.getSelectStatement().setXMLString(xmlString1);
            sqs_oracle_sql.getSelectStatement().setXMLEndTag("))");
         } else if (lockStatement.equalsIgnoreCase("RAW")) {
            xmlString1 = "XMLELEMENT(";
            forUpdateClause = this.getTableNameForXMLElement(this.from_clause);
            xmlString1 = xmlString1 + "\"ROW\"" + ", XMLATTRIBUTES(";
            sqs_oracle_sql.getSelectStatement().setXMLString(xmlString1);
            sqs_oracle_sql.getSelectStatement().setXMLEndTag("))");
         } else if (xmlString != null) {
            sqs_oracle_sql.getSelectStatement().setXMLString(xmlString);
            computeByStatementVector = new Vector();
            sqs_oracle_sql.getSelectStatement().setSelectItemList(computeByStatementVector);
         } else if (lockStatement.equalsIgnoreCase("EXPLICIT")) {
            xmlString1 = "XMLELEMENT(";
            forUpdateClause = this.getTableNameForXMLElement(this.from_clause);
            xmlString1 = xmlString1 + "\"" + forUpdateClause + "\"" + ", XMLFOREST(";
            sqs_oracle_sql.getSelectStatement().setXMLString(xmlString1);
            sqs_oracle_sql.getSelectStatement().setXMLEndTag("))");
         }
      }

      FromClause fc;
      FromClause fc;
      Object o;
      if (this.from_clause != null) {
         fc = new FromClause();
         sqs_oracle_sql.setFromClause(fc);
         this.from_clause.setBaseFromClauseFound(true);
         fc = this.from_clause.toOracleSelect(sqs_oracle_sql, this);
         sqs_oracle_sql.setFromClause(fc);
         if (fc != null) {
            selectList = fc.getFromItemList();
            if (selectList != null) {
               for(i = 0; i < selectList.size(); ++i) {
                  o = selectList.get(i);
                  if (o instanceof FromTable) {
                     String lockStt = ((FromTable)o).getLockTableStatement();
                     if (lockStt != null) {
                        sqs_oracle_sql.addLockTableList(lockStt);
                     }
                  }
               }
            }
         }
      } else if (!this.isDenodo) {
         fc = new FromClause();
         FromTable ft = new FromTable();
         selectList = new Vector();
         fc.setFromClause("FROM");
         ft.setTableName("SYS.DUAL");
         selectList.addElement(ft);
         fc.setFromItemList(selectList);
         sqs_oracle_sql.setFromClause(fc);
      }

      if (this.withString != null && this.isolationLevel != null) {
         lockStatement = "LOCK TABLE ";
         fc = this.from_clause;
         selectList = fc.getFromItemList();

         for(i = 0; i < selectList.size(); ++i) {
            o = selectList.get(i);
            if (o instanceof FromTable) {
               ft = (FromTable)o;
               if (ft.getTableName() instanceof String) {
                  s = (String)ft.getTableName();
                  lockStatement = lockStatement + s + " IN ";
                  if (this.isolationLevel.trim().equalsIgnoreCase("RR") || this.isolationLevel.trim().equalsIgnoreCase("RS")) {
                     lockStatement = lockStatement + "EXCLUSIVE MODE";
                     sqs_oracle_sql.addLockTableList(lockStatement);
                  }
               }
            }
         }
      }

      if (this.whereExpression != null && !this.whereExpression.getCheckWhere()) {
         if (sqs_oracle_sql.getWhereExpression() == null) {
            sqs_oracle_sql.setWhereExpression(this.whereExpression.toOracleSelect(sqs_oracle_sql, this));
         } else if (!topWithOrderBy && (rowSpecifier == null || !rowSpecifier.trim().equalsIgnoreCase("TOP"))) {
            sqs_oracle_sql.getWhereExpression().addOperator("AND");
            this.whereExpression.setCloseBrace(")");
            this.whereExpression.setOpenBrace("(");
            sqs_oracle_sql.getWhereExpression().addWhereExpression(this.whereExpression.toOracleSelect(sqs_oracle_sql, this));
         }
      }

      if (this.order_by_statement != null) {
         sqs_oracle_sql.setOrderByStatement(this.order_by_statement.toOracleSelect(sqs_oracle_sql, this));
      }

      subSQS = null;
      if (topWithOrderBy) {
         newSS = new SelectStatement();
         subSQS = this.toFormSubQuery(sqs_oracle_sql);
         sqs_oracle_sql.setSubQuery(subSQS);
         FromClause fc1 = sqs_oracle_sql.getFromClause();
         newSelectItemList = fc1.getFromItemList();
         if (newSelectItemList.size() == 1 && subSQS != null && newSelectItemList.get(0) instanceof FromTable) {
            FromTable ft1 = (FromTable)newSelectItemList.get(0);
            sqs_oracle_sql.setAliasForSubQuery(ft1.getTableName().toString());
         }

         newSS.setSelectClause("SELECT");
         items = sqs_oracle_sql.getSelectStatement().getSelectItemList();
         outerItems1 = new Vector();
         Vector innerItems2 = new Vector();
         if (items != null) {
            boolean selectStarPresent = false;
            i = 0;

            while(true) {
               if (i >= items.size()) {
                  if (selectStarPresent) {
                     SelectColumn outerSC = new SelectColumn();
                     wi = new Vector();
                     wi.add("*");
                     outerSC.setColumnExpression(wi);
                     outerItems1.clear();
                     outerItems1.add(outerSC);
                  }
                  break;
               }

               o = items.get(i);
               if (o instanceof SelectColumn) {
                  sc = (SelectColumn)o;
                  SelectColumn newSc = new SelectColumn();
                  newSc.setObjectContext(sc.getObjectContext());
                  newSc.setAliasName(sc.getAliasName());
                  newSc.setIsAS(sc.getIsAS());
                  newSc.setEndsWith(sc.getEndsWith());
                  newSc.setOpenBrace(sc.getOpenBrace());
                  newSc.setCloseBrace(sc.getCloseBrace());
                  newSc.setInsideDecodeFunction(sc.getInsideDecodeFunction());
                  newSc.setInArithmeticExpression(sc.getInArithmeticExpression());
                  newSc.setCorrespondingTableColumn(sc.getCorrespondingTableColumn());
                  newSc.setTargetDataType(sc.getTargetDataType());
                  newSc.setSelectColFromUQSSetExpression(sc.getSelectColFromUQSSetExpression());
                  newSc.setColumnExpression(sc.getColumnExpression());
                  Vector vOuter = sc.getColumnExpression();
                  Vector vInner = new Vector();
                  Vector vOuter1 = new Vector();
                  if (vOuter != null) {
                     for(int j = 0; j < vOuter.size(); ++j) {
                        Object oo = vOuter.get(j);
                        if (oo instanceof String) {
                           String sce = oo.toString();
                           if (sce.equalsIgnoreCase("=")) {
                              if (vOuter.elementAt(j - 1) instanceof TableColumn) {
                                 vOuter1.add(vOuter.elementAt(j - 1));
                                 vOuter1.add(vOuter.elementAt(j));
                                 vInner.setElementAt(" ", j - 1);
                              }
                           } else if (sce.trim().endsWith(".*")) {
                              selectStarPresent = true;
                              vInner.add(j, vOuter.get(j));
                           } else {
                              vInner.add(j, vOuter.get(j));
                           }
                        } else {
                           TableColumn tc = null;
                           if (oo instanceof TableColumn) {
                              tc = (TableColumn)oo;
                           }

                           TableColumn newTc;
                           if (sc.getAliasName() != null && !sc.getAliasName().trim().equalsIgnoreCase("")) {
                              newTc = new TableColumn();
                              newTc.setColumnName(sc.getAliasName());
                              sc.setAliasName((String)null);
                              sc.setIsAS((String)null);
                              vOuter1.add(newTc);
                              sc.setColumnExpression(vOuter1);
                           } else if (tc != null && tc.getTableName() != null) {
                              newTc = new TableColumn();
                              newTc.setColumnName(tc.getColumnName());
                              vOuter1.add(newTc);
                              sc.setColumnExpression(vOuter1);
                           }

                           vInner.add(j, vOuter.get(j));
                        }
                     }

                     newSc.setColumnExpression(vInner);
                  }

                  innerItems2.add(newSc);
                  outerItems1.add(sc);
               }

               ++i;
            }
         }

         newSS.setSelectItemList(outerItems1);
         SelectStatement ss = subSQS.getSelectStatement();
         ss.setSelectItemList(innerItems2);
         subSQS.setSelectStatement(ss);
         sqs_oracle_sql.setSelectStatement(newSS);
         WhereExpression subWE = subSQS.getWhereExpression();
         if (subWE != null) {
            wi = subWE.getWhereItems();
            WhereExpression we = new WhereExpression();
            Vector newWI = new Vector();
            if (wi != null) {
               for(int n = 0; n < wi.size(); ++n) {
                  wItem = wi.get(n);
                  if (wItem instanceof WhereItem) {
                     WhereColumn wCol = ((WhereItem)wItem).getLeftWhereExp();
                     if (wCol != null) {
                        Vector colExprr = wCol.getColumnExpression();
                        if (colExprr != null && colExprr.size() > 0 && colExprr.get(0).toString().trim().equalsIgnoreCase("rownum")) {
                           newWI.add(wItem);
                           wi.remove(wItem);
                           Vector op = subWE.getOperator();
                           if (op != null && op.size() > 0) {
                              op.remove(op.size() - 1);
                           }
                        }
                     }
                  }
               }

               we.setWhereItem(newWI);
               sqs_oracle_sql.setWhereExpression(we);
            }
         }
      }

      if (this.computeByVector != null) {
         computeByStatementVector = new Vector();

         for(i = 0; i < this.computeByVector.size(); ++i) {
            computeByStatementVector.add(((ComputeByStatement)this.computeByVector.get(i)).toOracleSelect(sqs_oracle_sql, this));
         }

         if (!topWithOrderBy) {
            sqs_oracle_sql.setComputeByStatements(computeByStatementVector);
            sqs_oracle_sql.setCommentForCompute(true);
         } else {
            subSQS.setComputeByStatements(computeByStatementVector);
            subSQS.setCommentForCompute(true);
         }
      }

      if (this.group_by_statement != null) {
         if (!topWithOrderBy) {
            sqs_oracle_sql.setGroupByStatement(this.group_by_statement.toOracleSelect(sqs_oracle_sql, this));
            if (!this.isOnlyFullGroupByModeEnabled && sqs_oracle_sql.getGroupByStatement() != null && sqs_oracle_sql.getGroupByStatement().getGroupingSetClause() == null) {
               this.handleGroupByStatement(sqs_oracle_sql, sqs_oracle_sql.getGroupByStatement());
            }
         } else {
            subSQS.setGroupByStatement(this.group_by_statement.toOracleSelect(sqs_oracle_sql, this));
            if (subSQS.getGroupByStatement() != null && subSQS.getGroupByStatement().getGroupingSetClause() == null) {
               this.handleGroupByStatement(subSQS, subSQS.getGroupByStatement());
            }
         }
      }

      if (this.set_operator_clause != null && !this.set_operator_clause.getCheckSetOperator()) {
         if (!topWithOrderBy) {
            sqs_oracle_sql.setSetOperatorClause(this.set_operator_clause.toOracleSelect(sqs_oracle_sql, this));
         } else {
            subSQS.setSetOperatorClause(this.set_operator_clause.toOracleSelect(sqs_oracle_sql, this));
         }
      }

      if (!topWithOrderBy) {
         if (sqs_oracle_sql.getSetOperatorClause() != null && sqs_oracle_sql.getOrderByStatement() != null) {
            this.changeOrderByColForSOC(sqs_oracle_sql);
         }
      } else if (subSQS.getSetOperatorClause() != null && subSQS.getOrderByStatement() != null) {
         this.changeOrderByColForSOC(subSQS);
      }

      if (this.having_statement != null) {
         if (!topWithOrderBy) {
            sqs_oracle_sql.setHavingStatement(this.having_statement.toOracleSelect(sqs_oracle_sql, this));
         } else {
            subSQS.setHavingStatement(this.having_statement.toOracleSelect(sqs_oracle_sql, this));
         }
      }

      if (this.into_statement != null) {
         if (!topWithOrderBy) {
            if (!SwisSQLOptions.PLSQL) {
               sqs_oracle_sql.setIntoStatement(this.into_statement.toOracleSelect(sqs_oracle_sql, this));
            } else {
               sqs_oracle_sql.setIntoStatement(this.into_statement);
            }
         } else if (!SwisSQLOptions.PLSQL) {
            subSQS.setIntoStatement(this.into_statement.toOracleSelect(sqs_oracle_sql, this));
         } else {
            subSQS.setIntoStatement(this.into_statement);
         }
      }

      if (this.for_update_statement != null) {
         ForUpdateStatement fus = this.for_update_statement;
         if (fus.getForUpdateClause() != null) {
            forUpdateClause = fus.getForUpdateClause();
            if (forUpdateClause.equalsIgnoreCase("FOR READ")) {
               sqs_oracle_sql.setForUpdateStatement((ForUpdateStatement)null);
            } else {
               sqs_oracle_sql.setForUpdateStatement(this.for_update_statement.toOracleSelect(sqs_oracle_sql, this));
            }
         } else {
            sqs_oracle_sql.setForUpdateStatement(this.for_update_statement.toOracleSelect(sqs_oracle_sql, this));
         }
      }

      if (this.hierarchical_query_clause != null) {
         sqs_oracle_sql.setHierarchicalQueryClause(this.hierarchical_query_clause.toOracleSelect(sqs_oracle_sql, this));
      }

      if (this.limit_clause != null) {
         sqs_oracle_sql.setLimitClause(this.limit_clause.toOracleSelect(sqs_oracle_sql, this));
      }

      if (this.fetch_clause != null) {
         sqs_oracle_sql.setFetchClause(this.fetch_clause.toOracleSelect(sqs_oracle_sql, this));
      }

      sqs_oracle_sql.setOptionalHintClause((OptionalHintClause)null);
      sqs_oracle_sql.setObjectContext(this.objectContext);
      return sqs_oracle_sql;
   }

   public SelectQueryStatement toTimesTen() throws ConvertException {
      SelectQueryStatement sqs_timesten_sql = new SelectQueryStatement();
      sqs_timesten_sql.setObjectContext(this.objectContext);
      sqs_timesten_sql.setCommentClass(this.commentObject);
      if (this.commentObject != null) {
         this.commentObject.setSQLDialect(10);
      }

      sqs_timesten_sql.setSQLDialect(10);
      sqs_timesten_sql.setSelectStatement(this.select_statement.toTimesTenSelect(sqs_timesten_sql, this));
      String rowSpecifier = null;
      boolean aggreFnExistsInSelCol = false;
      Vector groupItems;
      int tableCount;
      Vector colNames;
      int j;
      if (this.getSelectStatement() != null) {
         SelectStatement ss = this.getSelectStatement();
         rowSpecifier = ss.getSelectRowSpecifier();
         groupItems = ss.getSelectItemList();
         if (groupItems != null) {
            for(tableCount = 0; tableCount < groupItems.size(); ++tableCount) {
               Object obj = groupItems.get(tableCount);
               if (obj instanceof SelectColumn) {
                  SelectColumn sc = (SelectColumn)obj;
                  colNames = sc.getColumnExpression();
                  if (colNames != null) {
                     for(j = 0; j < colNames.size(); ++j) {
                        Object subObj = colNames.get(j);
                        if (subObj instanceof SelectQueryStatement) {
                           throw new ConvertException("\nSubqueries are not allowed in select columns in TimesTen 5.1.21\n");
                        }

                        if (subObj instanceof FunctionCalls) {
                           aggreFnExistsInSelCol = this.isAggreFnExists((FunctionCalls)subObj);
                        }
                     }
                  }
               }
            }
         }
      }

      FromClause fc;
      if (this.from_clause == null) {
         fc = new FromClause();
         FromTable ft = new FromTable();
         Vector fil = new Vector();
         fc.setFromClause("FROM");
         ft.setTableName("MONITOR");
         fil.addElement(ft);
         fc.setFromItemList(fil);
         sqs_timesten_sql.setFromClause(fc);
      } else {
         fc = this.getFromClause();
         groupItems = fc.getFromItemList();
         tableCount = 0;

         for(int i = 0; i < groupItems.size(); ++i) {
            if (groupItems.get(i) instanceof FromTable) {
               Object sourceTable = ((FromTable)groupItems.get(i)).getTableName();
               if (sourceTable instanceof SelectQueryStatement) {
                  StringBuilder var10000 = (new StringBuilder()).append("ADV_SQSTABLE_");
                  ++tableCount;
                  CreateQueryStatement cqs = SwisSQLUtils.constructCQS(var10000.append(tableCount).toString(), (SelectQueryStatement)sourceTable, sqs_timesten_sql).toTimesTenCreate();
                  sqs_timesten_sql.addCreateForSubQuery(cqs);
                  FromTable ft = new FromTable();
                  ft.setTableName("ADV_SQSTABLE_" + tableCount);
                  groupItems.setElementAt(ft, i);
                  InsertQueryStatement iqs = new InsertQueryStatement();
                  InsertClause ic = new InsertClause();
                  OptionalSpecifier optionalSp = new OptionalSpecifier();
                  TableClause tc = new TableClause();
                  TableExpression tableExp = new TableExpression();
                  ic.setInsert("INSERT");
                  optionalSp.setInto("INTO");
                  TableObject tableObj = new TableObject();
                  tableObj.setTableName("ADV_SQSTABLE_" + tableCount);
                  ArrayList tableExpList = new ArrayList();
                  tc.setTableObject(tableObj);
                  tableExpList.add(tc);
                  tableExp.setTableClauseList(tableExpList);
                  ic.setOptionalSpecifier(optionalSp);
                  ic.setTableExpression(tableExp);
                  SelectQueryStatement subSQS = (SelectQueryStatement)sourceTable;
                  SelectStatement ss = subSQS.getSelectStatement();
                  Vector sourceSItems = ss.getSelectItemList();
                  boolean isAliasExists = false;

                  for(int k = 0; k < sourceSItems.size(); ++k) {
                     Object sourceObj = sourceSItems.get(k);
                     if (sourceObj instanceof SelectColumn && ((SelectColumn)sourceObj).getAliasName() != null) {
                        isAliasExists = true;
                        break;
                     }
                  }

                  Vector colNames;
                  if (!isAliasExists) {
                     Vector newSelItems = new Vector();
                     colNames = cqs.getColumnNames();

                     for(int k = 0; k < colNames.size(); ++k) {
                        TableColumn tCol = new TableColumn();
                        tCol.setColumnName(((CreateColumn)colNames.get(k)).getColumnName());
                        SelectColumn sCol = new SelectColumn();
                        Vector colExpr = new Vector();
                        colExpr.add(tCol);
                        sCol.setColumnExpression(colExpr);
                        if (k != colNames.size() - 1) {
                           sCol.setEndsWith(",");
                        }

                        newSelItems.add(sCol);
                     }

                     ss.setSelectItemList(newSelItems);
                  }

                  iqs.setSelectQueryStatement(subSQS.toTimesTen());
                  iqs.setInsertClause(ic);
                  sqs_timesten_sql.addInsertForSubQuery(iqs);
                  DropStatement dropStt = new DropStatement();
                  dropStt.setDrop("DROP");
                  dropStt.setTableOrSequence("TABLE");
                  colNames = new Vector();
                  TableObject tabObj = new TableObject();
                  tabObj.setTableName("ADV_SQSTABLE_" + tableCount);
                  colNames.add(tabObj);
                  dropStt.setTableNameVector(colNames);
                  sqs_timesten_sql.addDropSttForSubQuery(dropStt);
               }
            }
         }

         sqs_timesten_sql.setFromClause(fc.toTimesTenSelect(sqs_timesten_sql, this));
      }

      if (this.whereExpression != null && !this.whereExpression.getCheckWhere()) {
         if (sqs_timesten_sql.getWhereExpression() == null) {
            sqs_timesten_sql.setWhereExpression(this.whereExpression.toTimesTenSelect(sqs_timesten_sql, this));
         } else if (rowSpecifier == null || !rowSpecifier.trim().equalsIgnoreCase("FIRST")) {
            sqs_timesten_sql.getWhereExpression().addOperator("AND");
            this.whereExpression.setCloseBrace(")");
            this.whereExpression.setOpenBrace("(");
            sqs_timesten_sql.getWhereExpression().addWhereExpression(this.whereExpression.toTimesTenSelect(sqs_timesten_sql, this));
         }
      }

      if (this.order_by_statement != null) {
         sqs_timesten_sql.setOrderByStatement(this.order_by_statement.toTimesTenSelect(sqs_timesten_sql, this));
      }

      if (this.computeByVector != null) {
         for(int i = 0; i < this.computeByVector.size(); ++i) {
            Object obj = this.computeByVector.get(i);
            if (obj instanceof ComputeByStatement) {
               ComputeByStatement cbs = (ComputeByStatement)obj;
               Vector fnNames = cbs.getFunctionNameVector();
               if (fnNames != null) {
                  GroupByStatement newGrpBy = null;
                  colNames = cbs.getTableNameVector();
                  if (colNames != null) {
                     newGrpBy = new GroupByStatement();
                     newGrpBy.setGroupClause("GROUP BY");
                     newGrpBy.setGroupByItemList(colNames);
                     if (this.getGroupByStatement() == null) {
                        sqs_timesten_sql.setGroupByStatement(newGrpBy);
                        if (!this.selectColumnsExistsInGroupBy(sqs_timesten_sql, colNames)) {
                           if (aggreFnExistsInSelCol) {
                              throw new ConvertException("\n In TimesTen, all the items in select-list should also exists in group-by items except for the aggregate functions.\n Unlike other database, Sybase accepts the syntax where in it is not necessary that all the items in\n select-list should exists in group-by (or) compute-by items. Hence on conversion of such Sybase queries to\n TimesTen by adding the left out items from the select-list to group-by will affect the actual result set.\n Another alternative solution is to use the 'SELECT queries(sub queries)' instead of aggregate functions \n in the select-list. But using sub-queries in select-list is not supported in TimesTen 5.1.21\n\n Eg : SELECT col1, col2, max(col3) from test group by col1 // Sybase Query, the column 'col2' is not in group-by items\n\n Alternate Solution : SELECT col1, col2, (SELECT max(col3) from test where col1 = a.col1) from test a\n\n");
                           }

                           sqs_timesten_sql.setGroupByStatement((GroupByStatement)null);
                        }
                     }
                  }

                  this.multipleQuery = "";

                  for(j = 0; j < fnNames.size(); ++j) {
                     SelectQueryStatement newSQS = new SelectQueryStatement();
                     SelectStatement newSS = new SelectStatement();
                     newSS.setSelectClause("SELECT");
                     SelectColumn newSc = new SelectColumn();
                     Vector newColExpr = new Vector();
                     newColExpr.add(fnNames.get(j));
                     newSc.setColumnExpression(newColExpr);
                     Vector sItems = new Vector();
                     sItems.add(newSc);
                     newSS.setSelectItemList(sItems);
                     newSQS.setSelectStatement(newSS);
                     newSQS.setFromClause(sqs_timesten_sql.getFromClause());
                     newSQS.setWhereExpression(sqs_timesten_sql.getWhereExpression());
                     if (newGrpBy != null) {
                        newSQS.setGroupByStatement(newGrpBy);
                     }

                     this.multipleQuery = this.multipleQuery + "\n" + newSQS.toString().trim() + ";\n";
                  }

                  sqs_timesten_sql.setMultipleQuery(this.multipleQuery);
               }
            }
         }

         sqs_timesten_sql.setComputeByStatements((Vector)null);
      }

      if (this.group_by_statement != null) {
         sqs_timesten_sql.setGroupByStatement(this.group_by_statement.toTimesTenSelect(sqs_timesten_sql, this));
         GroupByStatement gbs = sqs_timesten_sql.getGroupByStatement();
         if (gbs != null) {
            groupItems = gbs.getGroupByItemList();
            if (!this.selectColumnsExistsInGroupBy(sqs_timesten_sql, groupItems)) {
               if (aggreFnExistsInSelCol) {
                  throw new ConvertException("\n In TimesTen, all the items in select-list should also exists in group-by items except for the aggregate functions.\n Unlike other database, Sybase accepts the syntax where in it is not necessary that all the items in\n select-list should exists in group-by (or) compute-by items. Hence on conversion of such Sybase queries to\n TimesTen by adding the left out items from the select-list to group-by will affect the actual result set.\n Another alternative solution is to use the 'SELECT queries(sub queries)' instead of aggregate functions \n in the select-list. But using sub-queries in select-list is not supported in TimesTen 5.1.21\n\n Eg : SELECT col1, col2, max(col3) from test group by col1 // Sybase Query, the column 'col2' is not in group-by items\n\n Alternate Solution : SELECT col1, col2, (SELECT max(col3) from test where col1 = a.col1) from test a\n\n");
               }

               sqs_timesten_sql.setGroupByStatement((GroupByStatement)null);
            }
         }
      }

      if (this.set_operator_clause != null && !this.set_operator_clause.getCheckSetOperator()) {
         sqs_timesten_sql.setSetOperatorClause(this.set_operator_clause.toTimesTenSelect(sqs_timesten_sql, this));
      }

      if (sqs_timesten_sql.getSetOperatorClause() != null && sqs_timesten_sql.getOrderByStatement() != null) {
         this.changeOrderByColForSOC(sqs_timesten_sql);
      }

      if (this.having_statement != null) {
         sqs_timesten_sql.setHavingStatement(this.having_statement.toTimesTenSelect(sqs_timesten_sql, this));
      }

      if (this.into_statement != null) {
         if (!SwisSQLOptions.PLSQL) {
            sqs_timesten_sql.setIntoStatement(this.into_statement.toTimesTenSelect(sqs_timesten_sql, this));
         } else {
            sqs_timesten_sql.setIntoStatement(this.into_statement);
         }
      }

      if (this.for_update_statement != null) {
         sqs_timesten_sql.setForUpdateStatement(this.for_update_statement.toTimesTenSelect(sqs_timesten_sql, this));
      }

      if (this.hierarchical_query_clause != null) {
         sqs_timesten_sql.setHierarchicalQueryClause(this.hierarchical_query_clause.toTimesTenSelect(sqs_timesten_sql, this));
      }

      if (this.limit_clause != null) {
         sqs_timesten_sql.setLimitClause(this.limit_clause.toTimesTenSelect(sqs_timesten_sql, this));
      }

      if (this.fetch_clause != null) {
         sqs_timesten_sql.setFetchClause(this.fetch_clause.toTimesTenSelect(sqs_timesten_sql, this));
      }

      if (this.atIsolation != null && this.isolationReadLevel.equalsIgnoreCase("UNCOMMITTED")) {
         ForUpdateStatement forUpdateStt = new ForUpdateStatement();
         forUpdateStt.setForUpdateClause("FOR UPDATE");
         sqs_timesten_sql.setForUpdateStatement(forUpdateStt);
      }

      sqs_timesten_sql.setOptionalHintClause((OptionalHintClause)null);
      sqs_timesten_sql.setObjectContext(this.objectContext);
      return sqs_timesten_sql;
   }

   public SelectQueryStatement toNetezza() throws ConvertException {
      SelectQueryStatement sqs_netezza_sql = new SelectQueryStatement();
      if (this.commentObject != null) {
         this.commentObject.setSQLDialect(11);
      }

      sqs_netezza_sql.setCommentClass(this.commentObject);
      sqs_netezza_sql.setSelectStatement(this.select_statement.toNetezzaSelect(sqs_netezza_sql, this));
      if (this.from_clause != null) {
         sqs_netezza_sql.setFromClause(this.from_clause.toNetezzaSelect(sqs_netezza_sql, this));
      }

      if (this.whereExpression != null) {
         sqs_netezza_sql.setWhereExpression(this.whereExpression.toNetezzaSelect(sqs_netezza_sql, this));
      }

      if (this.order_by_statement != null) {
         sqs_netezza_sql.setOrderByStatement(this.order_by_statement.toNetezzaSelect(sqs_netezza_sql, this));
      }

      if (this.computeByVector != null) {
         Vector computeByStatementVector = new Vector();

         for(int i = 0; i < this.computeByVector.size(); ++i) {
            computeByStatementVector.add(((ComputeByStatement)this.computeByVector.get(i)).toNetezzaSelect(sqs_netezza_sql, this));
         }

         sqs_netezza_sql.setComputeByStatements(computeByStatementVector);
         sqs_netezza_sql.setCommentForCompute(true);
      }

      if (sqs_netezza_sql.getGroupByStatement() == null && this.group_by_statement != null) {
         sqs_netezza_sql.setGroupByStatement(this.group_by_statement.toNetezzaSelect(sqs_netezza_sql, this));
      }

      if (this.set_operator_clause != null) {
         sqs_netezza_sql.setSetOperatorClause(this.set_operator_clause.toNetezzaSelect(sqs_netezza_sql, this));
      }

      if (this.having_statement != null) {
         sqs_netezza_sql.setHavingStatement(this.having_statement.toNetezzaSelect(sqs_netezza_sql, this));
      }

      if (this.into_statement != null) {
         sqs_netezza_sql.setIntoStatement(this.into_statement.toNetezzaSelect(sqs_netezza_sql, this));
      }

      if (this.hierarchical_query_clause != null) {
         throw new ConvertException();
      } else {
         if (this.limit_clause != null) {
            sqs_netezza_sql.setLimitClause(this.limit_clause.toNetezzaSelect(sqs_netezza_sql, this));
         }

         if (this.fetch_clause != null) {
            throw new ConvertException();
         } else {
            if (this.for_update_statement != null) {
               this.for_update_statement = null;
            }

            sqs_netezza_sql.setOptionalHintClause((OptionalHintClause)null);
            return sqs_netezza_sql;
         }
      }
   }

   private void handleGroupByStatement(SelectQueryStatement to_sqs, GroupByStatement to_group_by_statement) {
      Vector groupItems = to_group_by_statement.getGroupByItemList();
      if (!this.selectColumnsExistsInGroupBy(to_sqs, groupItems)) {
         SelectStatement ss = to_sqs.getSelectStatement();
         Vector selectItems = ss.getSelectItemList();
         int count = 0;

         for(int l = 0; l < selectItems.size(); ++l) {
            Object sobj = selectItems.get(l);
            if (sobj instanceof SelectColumn) {
               SelectColumn scol = (SelectColumn)sobj;
               Vector colExprn = scol.getColumnExpression();

               for(int s = 0; s < colExprn.size(); ++s) {
                  Object sObj = colExprn.get(s);
                  if (sObj instanceof FunctionCalls) {
                     FunctionCalls fc = (FunctionCalls)sObj;
                     TableColumn tfc = fc.getFunctionName();
                     if (tfc != null) {
                        String fnName = tfc.getColumnName();
                        if (fnName.equalsIgnoreCase("min") || fnName.equalsIgnoreCase("max") || fnName.equalsIgnoreCase("count") || fnName.equalsIgnoreCase("avg") || fnName.equalsIgnoreCase("sum")) {
                           SelectQueryStatement newSQS = new SelectQueryStatement();
                           SelectStatement newSS = new SelectStatement();
                           newSS.setSelectClause("SELECT");
                           Vector newSelItems = new Vector();
                           SelectColumn sCol = new SelectColumn();
                           Vector newColExprn = new Vector();
                           newColExprn.add(sObj);
                           sCol.setColumnExpression(newColExprn);
                           newSelItems.add(sCol);
                           newSS.setSelectItemList(newSelItems);
                           newSQS.setSelectStatement(newSS);
                           FromClause fromClause = to_sqs.getFromClause();
                           Vector fromItems = fromClause.getFromItemList();
                           FromClause newFC = new FromClause();
                           newFC.setFromClause("FROM");
                           Vector newFromItems = new Vector();
                           newFC.setFromItemList(newFromItems);
                           newSQS.setFromClause(newFC);
                           FromTable ft = null;
                           TableColumn tcfn = null;
                           Vector fnargs = fc.getFunctionArguments();
                           boolean count_star = false;
                           if (fnName.equalsIgnoreCase("count") && fnargs != null && fnargs.size() > 0 && fnargs.get(0).toString().trim().equals("*")) {
                              count_star = true;
                           } else {
                              Object argobj = fc.getFunctionArguments().get(0);
                              if (argobj instanceof SelectColumn) {
                                 tcfn = this.getTableColumn((SelectColumn)argobj);
                              } else if (argobj instanceof String) {
                                 tcfn = new TableColumn();
                                 tcfn.setColumnName(argobj.toString());
                              }
                           }

                           if (fromItems.size() == 1) {
                              if (fromItems.get(0) instanceof FromTable) {
                                 ft = (FromTable)fromItems.get(0);
                              }
                           } else if (tcfn != null) {
                              String tableName = tcfn.getTableName();
                              if (tableName != null) {
                                 ft = this.getFromTable(fromItems, tableName);
                              } else {
                                 String columnName = tcfn.getColumnName();
                                 ft = MetadataInfoUtil.getTableOfColumn(to_sqs, (String)columnName);
                              }
                           }

                           if (tcfn != null) {
                              tcfn.setTableName((String)null);
                           }

                           FromTable newFromTable = new FromTable();
                           StringBuilder var10001;
                           if (ft != null) {
                              newFromTable.setTableName(ft.getTableName());
                              newFromItems.add(newFromTable);
                              var10001 = (new StringBuilder()).append("adv_alias_");
                              ++count;
                              newFromTable.setAliasName(var10001.append(count).toString());
                           } else if (count_star) {
                              for(int f = 0; f < fromItems.size(); ++f) {
                                 Object frmObj = fromItems.get(f);
                                 if (frmObj instanceof FromTable) {
                                    FromTable newFrmTable = new FromTable();
                                    newFrmTable.setTableName(((FromTable)frmObj).getTableName());
                                    var10001 = (new StringBuilder()).append("adv_alias_");
                                    ++count;
                                    newFrmTable.setAliasName(var10001.append(count).toString());
                                    newFromItems.add(newFrmTable);
                                 }
                              }
                           } else {
                              to_sqs.setGeneralComments("/* SwisSQL Message : Metadata required for accurate conversions */");
                           }

                           WhereExpression we = new WhereExpression();

                           for(int n = 0; n < groupItems.size(); ++n) {
                              Object gObj = groupItems.get(n);
                              if (gObj instanceof SelectColumn) {
                                 WhereItem wi = new WhereItem();
                                 WhereColumn lwc = new WhereColumn();
                                 Vector newlColExpr = new Vector();
                                 WhereColumn rwc = new WhereColumn();
                                 Vector newrColExpr = new Vector();
                                 Vector gColExpr = ((SelectColumn)gObj).getColumnExpression();

                                 for(int r = 0; r < gColExpr.size(); ++r) {
                                    Object object = gColExpr.get(r);
                                    if (object instanceof TableColumn) {
                                       String gColName = ((TableColumn)object).getColumnName();
                                       String gTabName = ((TableColumn)object).getTableName();
                                       if (ft != null || count_star) {
                                          if (fromItems.size() > 1) {
                                             if (gTabName != null) {
                                                if (count_star) {
                                                   ft = this.getFromTable(fromItems, gTabName);
                                                } else if (ft != null && ft.getAliasName() != null) {
                                                   if (!ft.getAliasName().equalsIgnoreCase(gTabName)) {
                                                      continue;
                                                   }
                                                } else if (ft != null && !ft.getTableName().toString().equalsIgnoreCase(gTabName)) {
                                                   continue;
                                                }
                                             } else {
                                                FromTable ftab = MetadataInfoUtil.getTableOfColumn(to_sqs, (TableColumn)((TableColumn)object));
                                                if (ftab != null) {
                                                   if (count_star) {
                                                      ft = this.getFromTable(fromItems, ftab.getTableName().toString());
                                                   } else if (ft != null && !ftab.getTableName().toString().equalsIgnoreCase(ft.getTableName().toString())) {
                                                      continue;
                                                   }
                                                } else {
                                                   to_sqs.setGeneralComments("/* SwisSQL Message : Metadata might be required for accurate conversions. */");
                                                }
                                             }

                                             if (ft != null && count_star) {
                                                newFromTable = this.getFromTable(newFromItems, ft.getTableName().toString());
                                             }
                                          }

                                          if (ft != null) {
                                             TableColumn newrTC = new TableColumn();
                                             newrTC.setColumnName(gColName);
                                             if (ft.getAliasName() != null) {
                                                newrTC.setTableName(ft.getAliasName());
                                             } else {
                                                newrTC.setTableName(ft.getTableName().toString());
                                             }

                                             newrColExpr.add(newrTC);
                                             TableColumn newlTC = new TableColumn();
                                             newlTC.setColumnName(gColName);
                                             newlTC.setTableName(newFromTable.getAliasName());
                                             newlColExpr.add(newlTC);
                                          }
                                       }
                                    } else {
                                       newrColExpr.add(object);
                                       newlColExpr.add(object);
                                    }
                                 }

                                 if (newlColExpr.size() > 0) {
                                    if (we.getWhereItems().size() > 0) {
                                       we.addOperator("AND");
                                    }

                                    rwc.setColumnExpression(newrColExpr);
                                    lwc.setColumnExpression(newlColExpr);
                                    wi.setLeftWhereExp(lwc);
                                    wi.setRightWhereExp(rwc);
                                    wi.setOperator("=");
                                    we.addWhereItem(wi);
                                 }
                              }
                           }

                           newSQS.setWhereExpression(we);
                           newSQS.setOpenBrace("(");
                           newSQS.setCloseBrace(")");
                           colExprn.setElementAt(newSQS.removeIndent(newSQS.toString()), s);
                           if (scol.getAliasName() == null) {
                              boolean aliasExists = false;
                              if (colExprn.size() > 2 && colExprn.get(1).toString().trim().equals("=")) {
                                 aliasExists = true;
                              }

                              if (!aliasExists) {
                                 if (count_star) {
                                    scol.setAliasName("adv_count_value");
                                 } else if (tcfn != null) {
                                    scol.setAliasName(fnName.toLowerCase() + "_" + tcfn.getColumnName().toLowerCase());
                                 }
                              }
                           }

                           to_sqs.setGroupByStatement((GroupByStatement)null);
                        }
                     }
                  }
               }
            }
         }
      }

   }

   private FromTable getFromTable(Vector fromItems, String tableName) {
      int n;
      Object obj;
      FromTable frmTab;
      for(n = 0; n < fromItems.size(); ++n) {
         obj = fromItems.get(n);
         if (obj instanceof FromTable) {
            frmTab = (FromTable)obj;
            String aliasName = frmTab.getAliasName();
            if (aliasName != null && aliasName.equalsIgnoreCase(tableName)) {
               return frmTab;
            }
         }
      }

      for(n = 0; n < fromItems.size(); ++n) {
         obj = fromItems.get(n);
         if (obj instanceof FromTable) {
            frmTab = (FromTable)obj;
            Object tblObj = frmTab.getTableName();
            if (tblObj instanceof String && tableName.equalsIgnoreCase(tblObj.toString())) {
               return frmTab;
            }
         }
      }

      return null;
   }

   private TableColumn getTableColumn(SelectColumn sc) {
      Vector colExpr = sc.getColumnExpression();

      for(int i = 0; i < colExpr.size(); ++i) {
         Object obj = colExpr.get(i);
         if (obj instanceof TableColumn) {
            return (TableColumn)obj;
         }

         if (obj instanceof SelectColumn) {
            return this.getTableColumn((SelectColumn)obj);
         }

         if (obj instanceof FunctionCalls) {
            FunctionCalls fc = (FunctionCalls)obj;
            Vector fnArgs = fc.getFunctionArguments();
            if (fnArgs != null) {
               for(int j = 0; j < fnArgs.size(); ++j) {
                  Object argObj = fnArgs.get(j);
                  if (argObj instanceof SelectColumn) {
                     TableColumn tc = this.getTableColumn((SelectColumn)argObj);
                     if (tc != null) {
                        return tc;
                     }
                  }
               }
            }
         } else if (obj instanceof SelectQueryStatement) {
            SelectQueryStatement sqs = (SelectQueryStatement)obj;
            SelectStatement ss = sqs.getSelectStatement();
            Vector sItems = ss.getSelectItemList();

            for(int j = 0; j < sItems.size(); ++j) {
               Object sobj = sItems.get(j);
               if (sobj instanceof SelectColumn) {
                  TableColumn tc = this.getTableColumn((SelectColumn)sobj);
                  if (tc != null) {
                     return tc;
                  }
               }
            }
         }
      }

      return null;
   }

   private boolean selectColumnsExistsInGroupBy(SelectQueryStatement to_sqs, Vector groupItems) {
      if (to_sqs.getSelectStatement() != null) {
         SelectStatement ss = to_sqs.getSelectStatement();
         Vector sItems = ss.getSelectItemList();
         if (sItems != null) {
            for(int j = 0; j < sItems.size(); ++j) {
               Object sobj = sItems.get(j);
               if (sobj instanceof SelectColumn) {
                  SelectColumn sc = (SelectColumn)sobj;
                  Vector colExpr = sc.getColumnExpression();
                  if (colExpr != null && !this.selectColumnExprExistsInGroupBy(colExpr, groupItems)) {
                     return false;
                  }
               }
            }
         }
      }

      return true;
   }

   private boolean selectColumnExprExistsInGroupBy(Vector selColExpr, Vector groupItems) {
      Object selColObj;
      TableColumn tcs;
      int j;
      for(int i = 0; i < groupItems.size(); ++i) {
         Object gObj = groupItems.get(i);
         if (gObj instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)gObj;
            Vector gColExpr = sc.getColumnExpression();
            if (gColExpr.size() > 1) {
               if (selColExpr.size() == gColExpr.size()) {
                  int j = false;

                  for(j = 0; j < selColExpr.size(); ++j) {
                     if (j == 0 && selColExpr.size() > 2 && selColExpr.get(1).toString().trim().equals("=")) {
                        j = 1;
                     } else {
                        selColObj = selColExpr.get(j);
                        Object gColObj = gColExpr.get(j);
                        if (selColObj instanceof TableColumn && gColObj instanceof TableColumn) {
                           TableColumn tcg = (TableColumn)selColObj;
                           TableColumn tcs = (TableColumn)gColObj;
                           if (tcg.getTableName() != null && tcs.getTableName() != null) {
                              if (tcg.getTableName().equalsIgnoreCase(tcs.getTableName()) && !tcg.getColumnName().equalsIgnoreCase(tcs.getColumnName())) {
                                 break;
                              }
                           } else if (!tcg.getColumnName().equalsIgnoreCase(tcs.getColumnName()) && !tcs.getColumnName().trim().startsWith("@") && !tcs.getColumnName().trim().startsWith(":")) {
                              break;
                           }
                        } else if (!selColObj.toString().trim().equalsIgnoreCase(gColObj.toString().trim())) {
                           break;
                        }
                     }
                  }

                  if (j == selColExpr.size()) {
                     return true;
                  }
               }
            } else if (gColExpr.size() == 1 && gColExpr.get(0) instanceof FunctionCalls) {
               FunctionCalls fc = (FunctionCalls)gColExpr.get(0);
               FunctionCalls fc1;
               String fnName1;
               if (selColExpr.size() == 1 && selColExpr.get(0) instanceof FunctionCalls) {
                  fc1 = (FunctionCalls)selColExpr.get(0);
                  if (fc1.getFunctionName() != null) {
                     fnName1 = fc1.getFunctionName().getColumnName();
                     if (!fnName1.equalsIgnoreCase("min") && !fnName1.equalsIgnoreCase("max") && !fnName1.equalsIgnoreCase("count") && !fnName1.equalsIgnoreCase("avg") && !fnName1.equalsIgnoreCase("sum") && fc.toString().equalsIgnoreCase(fc1.toString())) {
                        return true;
                     }
                  }
               } else if (selColExpr.size() == 2 && selColExpr.get(1) instanceof FunctionCalls && selColExpr.get(0).toString().trim().equals("")) {
                  fc1 = (FunctionCalls)selColExpr.get(1);
                  if (fc1.getFunctionName() != null) {
                     fnName1 = fc1.getFunctionName().getColumnName();
                     if (!fnName1.equalsIgnoreCase("min") && !fnName1.equalsIgnoreCase("max") && !fnName1.equalsIgnoreCase("count") && !fnName1.equalsIgnoreCase("avg") && !fnName1.equalsIgnoreCase("sum") && fc.toString().equalsIgnoreCase(fc1.toString())) {
                        return true;
                     }
                  }
               }
            }
         } else if (gObj instanceof TableColumn && selColExpr.size() == 1 && selColExpr.get(0) instanceof TableColumn) {
            TableColumn tcg = (TableColumn)gObj;
            tcs = (TableColumn)selColExpr.get(0);
            if (tcg.getTableName() != null && tcs.getTableName() != null) {
               if (tcg.getTableName().equalsIgnoreCase(tcs.getTableName()) && tcg.getColumnName().equalsIgnoreCase(tcs.getColumnName())) {
                  return true;
               }
            } else {
               if (tcg.getColumnName().equalsIgnoreCase(tcs.getColumnName())) {
                  return true;
               }

               if (tcs.getColumnName().trim().startsWith("@") || tcs.getColumnName().trim().startsWith(":")) {
                  return true;
               }
            }
         } else if (gObj instanceof FunctionCalls) {
            FunctionCalls gObjFc = (FunctionCalls)gObj;
            if (!gObjFc.getFunctionName().getColumnName().equalsIgnoreCase("cube") && !gObjFc.getFunctionName().getColumnName().equalsIgnoreCase("rollup")) {
               return false;
            }

            return true;
         }
      }

      ArrayList tcList = new ArrayList();
      this.getTableColumns(selColExpr, tcList);
      if (tcList.size() == 0) {
         return true;
      } else {
         boolean exists = false;

         for(int j = 0; j < tcList.size(); ++j) {
            tcs = (TableColumn)tcList.get(j);
            exists = false;

            for(j = 0; j < groupItems.size(); ++j) {
               selColObj = groupItems.get(j);
               if (selColObj instanceof SelectColumn) {
                  SelectColumn gObj = (SelectColumn)selColObj;
                  Vector gColExpr = gObj.getColumnExpression();
                  if (gColExpr.size() == 1) {
                     Object gColObj = gColExpr.get(0);
                     if (gColObj instanceof TableColumn) {
                        TableColumn tcg = (TableColumn)gColObj;
                        if (tcg.getTableName() != null && tcs.getTableName() != null) {
                           if (tcg.getTableName().equalsIgnoreCase(tcs.getTableName()) && tcg.getColumnName().equalsIgnoreCase(tcs.getColumnName())) {
                              exists = true;
                              break;
                           }
                        } else if (tcg.getColumnName().equalsIgnoreCase(tcs.getColumnName())) {
                           exists = true;
                           break;
                        }
                     }
                  }
               }
            }

            if (!exists) {
               break;
            }
         }

         if (exists) {
            return true;
         } else {
            return false;
         }
      }
   }

   private void getTableColumns(Vector expr, ArrayList tcList) {
      for(int i = 0; i < expr.size(); ++i) {
         if (i == 0 && expr.size() > 2 && expr.get(1).toString().trim().equals("=")) {
            i = 1;
         } else {
            Object obj = expr.get(i);
            String fnName;
            if (obj instanceof TableColumn) {
               if (!tcList.contains((TableColumn)obj)) {
                  TableColumn tc = (TableColumn)obj;
                  fnName = tc.getColumnName();
                  if (!fnName.startsWith("@") && !fnName.startsWith(":")) {
                     tcList.add(tc);
                  }
               }
            } else if (obj instanceof SelectColumn) {
               this.getTableColumns(((SelectColumn)obj).getColumnExpression(), tcList);
            } else if (obj instanceof FunctionCalls) {
               FunctionCalls fc = (FunctionCalls)obj;
               if (fc.getFunctionName() != null) {
                  fnName = fc.getFunctionName().getColumnName();
                  if (!fnName.equalsIgnoreCase("min") && !fnName.equalsIgnoreCase("max") && !fnName.equalsIgnoreCase("count") && !fnName.equalsIgnoreCase("avg") && !fnName.equalsIgnoreCase("sum")) {
                     this.getTableColumns(((FunctionCalls)obj).getFunctionArguments(), tcList);
                  }
               }
            } else if (obj instanceof SelectQueryStatement) {
               this.getTableColumns(((SelectQueryStatement)obj).getSelectStatement().getSelectItemList(), tcList);
            }
         }
      }

   }

   private boolean isAggreFnExists(FunctionCalls fc) {
      String fnName = fc.getFunctionName().getColumnName();
      if (!fnName.equalsIgnoreCase("min") && !fnName.equalsIgnoreCase("max") && !fnName.equalsIgnoreCase("count") && !fnName.equalsIgnoreCase("avg") && !fnName.equalsIgnoreCase("sum")) {
         Vector fnArgs = fc.getFunctionArguments();
         if (fnArgs != null) {
            for(int i = 0; i < fnArgs.size(); ++i) {
               Object obj = fnArgs.get(i);
               if (obj instanceof SelectColumn) {
                  SelectColumn sc = (SelectColumn)obj;
                  Vector colExpr = sc.getColumnExpression();

                  for(int j = 0; j < colExpr.size(); ++j) {
                     obj = colExpr.get(j);
                     if (obj instanceof FunctionCalls && this.isAggreFnExists((FunctionCalls)obj)) {
                        return true;
                     }
                  }
               }
            }
         }

         return false;
      } else {
         return true;
      }
   }

   private void changeOrderByColForSOC(SelectQueryStatement sqs) {
      OrderByStatement orderBy = sqs.getOrderByStatement();
      Vector orderItem = orderBy.getOrderItemList();

      for(int i = 0; i < orderItem.size(); ++i) {
         Object obj = orderItem.get(i);
         if (obj instanceof OrderItem) {
            OrderItem oi = (OrderItem)obj;
            SelectColumn sc = oi.getOrderSpecifier();
            Vector colExpr = sc.getColumnExpression();

            for(int j = 0; j < colExpr.size(); ++j) {
               obj = colExpr.get(j);
               if (obj instanceof TableColumn) {
                  TableColumn tc = (TableColumn)obj;
                  this.getOrderByColumnName(sqs, tc);
               }
            }
         }
      }

   }

   private TableColumn getOrderByColumnName(SelectQueryStatement sqs, TableColumn tc) {
      tc.setTableName((String)null);
      String colName = tc.getColumnName();
      SelectStatement ss = sqs.getSelectStatement();
      Vector selItem = ss.getSelectItemList();
      if (selItem != null) {
         for(int i = 0; i < selItem.size(); ++i) {
            Object obj = selItem.get(i);
            if (obj instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)obj;
               Vector colExpr = sc.getColumnExpression();

               for(int j = 0; j < colExpr.size(); ++j) {
                  obj = colExpr.get(j);
                  if (obj instanceof TableColumn) {
                     TableColumn tabCol = (TableColumn)obj;
                     if (tabCol.getColumnName().equalsIgnoreCase(colName)) {
                        String aliasName = sc.getAliasName();
                        if (aliasName != null) {
                           tc.setColumnName(aliasName);
                        }

                        return tc;
                     }
                  }
               }
            }
         }
      }

      return tc;
   }

   private SelectQueryStatement toFormSubQuery(SelectQueryStatement sqs) {
      this.subQuery = new SelectQueryStatement();
      this.subQuery.setSelectStatement(sqs.getSelectStatement());
      this.subQuery.setFromClause(sqs.getFromClause());
      this.subQuery.setOrderByStatement(sqs.getOrderByStatement());
      this.subQuery.setWhereExpression(sqs.getWhereExpression());
      return this.subQuery;
   }

   public SelectQueryStatement toInformix() throws ConvertException {
      SelectQueryStatement informixSelectQueryStatement = new SelectQueryStatement();
      if (this.commentObject != null) {
         this.commentObject.setSQLDialect(6);
      }

      informixSelectQueryStatement.setCommentClass(this.commentObject);
      informixSelectQueryStatement.setSQLDialect(6);
      this.setSQLDialect(6);
      informixSelectQueryStatement.setSelectStatement(this.select_statement.toInformixSelect(informixSelectQueryStatement, this));
      FromClause fc;
      if (this.from_clause != null) {
         fc = new FromClause();
         informixSelectQueryStatement.setFromClause(fc);
         informixSelectQueryStatement.setFromClause(this.from_clause.toInformixSelect(informixSelectQueryStatement, this));
      } else {
         fc = new FromClause();
         FromTable ft = new FromTable();
         Vector fil = new Vector();
         SelectStatement ifxSelectStatement = informixSelectQueryStatement.getSelectStatement();
         ifxSelectStatement.setSelectRowSpecifier((String)null);
         ifxSelectStatement.setInformixRowSpecifier("FIRST");
         ifxSelectStatement.setSelectRowCount(1);
         fc.setFromClause("FROM");
         ft.setTableName("SYSTABLES");
         fil.addElement(ft);
         fc.setFromItemList(fil);
         informixSelectQueryStatement.setFromClause(fc);
      }

      if (this.whereExpression != null && !this.whereExpression.getCheckWhere()) {
         if (informixSelectQueryStatement.getWhereExpression() == null) {
            informixSelectQueryStatement.setWhereExpression(this.whereExpression.toInformixSelect(informixSelectQueryStatement, this));
         } else {
            informixSelectQueryStatement.getWhereExpression().addOperator("AND");
            informixSelectQueryStatement.getWhereExpression().addWhereExpression(this.whereExpression.toInformixSelect(informixSelectQueryStatement, this));
         }
      }

      if (this.order_by_statement != null) {
         informixSelectQueryStatement.setOrderByStatement(this.order_by_statement.toInformixSelect(informixSelectQueryStatement, this));
      }

      if (this.computeByVector != null) {
         Vector computeByStatementVector = new Vector();

         for(int i = 0; i < this.computeByVector.size(); ++i) {
            computeByStatementVector.add(((ComputeByStatement)this.computeByVector.get(i)).toInformixSelect(informixSelectQueryStatement, this));
         }

         informixSelectQueryStatement.setComputeByStatements(computeByStatementVector);
         informixSelectQueryStatement.setCommentForCompute(true);
      }

      if (this.group_by_statement != null) {
         informixSelectQueryStatement.setGroupByStatement(this.group_by_statement.toInformixSelect(informixSelectQueryStatement, this));
      }

      if (this.set_operator_clause != null && !this.set_operator_clause.getCheckSetOperator()) {
         informixSelectQueryStatement.setSetOperatorClause(this.set_operator_clause.toInformixSelect(informixSelectQueryStatement, this));
      }

      if (this.having_statement != null) {
         informixSelectQueryStatement.setHavingStatement(this.having_statement.toInformixSelect(informixSelectQueryStatement, this));
      }

      if (this.into_statement != null) {
         informixSelectQueryStatement.setIntoStatement(this.into_statement.toInformixSelect(informixSelectQueryStatement, this));
      }

      if (this.for_update_statement != null) {
         informixSelectQueryStatement.setForUpdateStatement(this.for_update_statement.toInformixSelect(informixSelectQueryStatement, this));
      }

      if (this.hierarchical_query_clause != null) {
         informixSelectQueryStatement.setHierarchicalQueryClause(this.hierarchical_query_clause.toInformixSelect(informixSelectQueryStatement, this));
      }

      if (this.limit_clause != null) {
         informixSelectQueryStatement.setLimitClause(this.limit_clause.toInformixSelect(informixSelectQueryStatement, this));
      }

      if (this.fetch_clause != null) {
         informixSelectQueryStatement.setFetchClause(this.fetch_clause.toInformixSelect(informixSelectQueryStatement, this));
      }

      informixSelectQueryStatement.setOptionalHintClause((OptionalHintClause)null);
      return informixSelectQueryStatement;
   }

   private String singleQueryIntoMultipleQueriesForPLSQL() {
      return singleQueryConvertedToMultipleQueryList;
   }

   public void addDummyWhereItem1Equalto1(WhereExpression we) {
      WhereItem wi = new WhereItem();
      Vector colExp = new Vector();
      colExp.add("1");
      WhereColumn wc = new WhereColumn();
      wc.setColumnExpression(colExp);
      wi.setLeftWhereExp(wc);
      wi.setRightWhereExp(wc);
      wi.setOperator("=");
      we.addWhereItem(wi);
      we.addOperator("AND");
      Vector whereItems = we.getWhereItems();

      for(int i = 0; i < whereItems.size(); ++i) {
         Object obj = whereItems.get(i);
         if (obj instanceof WhereExpression) {
            WhereExpression localWE = (WhereExpression)obj;
            this.addDummyWhereItem1Equalto1(localWE);
         }
      }

   }

   public String getTableNameForXMLElement(FromClause fc) {
      String tableNameStr = new String();
      if (fc != null) {
         Vector fromItems = this.from_clause.getFromItemList();
         if (fromItems != null) {
            for(int i = 0; i < fromItems.size(); ++i) {
               if (fromItems.get(i) instanceof FromTable) {
                  FromTable ft = (FromTable)fromItems.get(i);
                  if (ft.getTableName() != null && ft.getTableName() instanceof String) {
                     return ft.getTableName().toString();
                  }

                  if (ft.getAliasName() != null && !ft.getAliasName().equals("")) {
                     return ft.getAliasName();
                  }
               } else if (fromItems.get(i) instanceof FromClause) {
                  FromClause subFc = (FromClause)fromItems.get(i);
                  String str = this.getTableNameForXMLElement(subFc);
                  if (!str.equalsIgnoreCase("")) {
                     return str;
                  }
               }
            }
         }
      }

      return tableNameStr;
   }

   public UserObjectContext getObjectContext() {
      return this.objectContext;
   }

   public void setObjectContext(UserObjectContext obj) {
      this.objectContext = obj;
   }

   private void handleRownumConversion(SelectQueryStatement to_sqs) throws ConvertException {
      FromClause fc = this.getFromClause();
      Vector fromItems = fc.getFromItemList();
      Vector combinedOrderItems = new Vector();

      Vector orderItemList;
      for(int s = 0; s < fromItems.size(); ++s) {
         Object obj = fromItems.get(s);
         if (obj instanceof FromTable) {
            FromTable ft = (FromTable)obj;
            Object tableName = ft.getTableName();
            if (tableName instanceof SelectQueryStatement) {
               SelectQueryStatement subquery = (SelectQueryStatement)tableName;
               if (subquery.getOrderByStatement() != null) {
                  OrderByStatement obs = subquery.getOrderByStatement().toTeradataSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
                  orderItemList = obs.getOrderItemList();

                  for(int i_count = 0; i_count < orderItemList.size(); ++i_count) {
                     OrderItem oi = (OrderItem)orderItemList.elementAt(i_count);
                     if (oi != null) {
                        combinedOrderItems.add(oi);
                     }
                  }
               }
            }
         }
      }

      Vector wiVec;
      String qualifyAlias;
      Vector rownumSelColExp;
      Vector rownumOrderColExp;
      if (to_sqs.getOrderByStatement() == null && combinedOrderItems.size() == 0) {
         qualifyAlias = "rownum";
         FunctionCalls countOlap = new FunctionCalls();
         TableColumn countOlapName = new TableColumn();
         countOlapName.setColumnName("COUNT");
         countOlap.setFunctionName(countOlapName);
         FunctionCalls countOlapCast = new FunctionCalls();
         TableColumn countOlapCastName = new TableColumn();
         countOlapCastName.setColumnName("CAST");
         countOlapCast.setFunctionName(countOlapCastName);
         Vector countOlapCastArgs = new Vector();
         countOlapCastArgs.add("1");
         NumericClass integerType = new NumericClass();
         integerType.setDatatypeName("INTEGER");
         integerType.setOpenBrace((String)null);
         integerType.setClosedBrace((String)null);
         countOlapCastArgs.add(integerType);
         countOlapCast.setAsDatatype("AS");
         countOlapCast.setFunctionArguments(countOlapCastArgs);
         Vector countOlapArgs = new Vector();
         countOlapArgs.add(countOlapCast);
         countOlap.setFunctionArguments(countOlapArgs);
         countOlap.setOver("OVER");
         WindowingClause countOlapWindow = new WindowingClause();
         countOlapWindow.setRowsOrRange("ROWS");
         countOlapWindow.setUnbounded("UNBOUNDED");
         countOlapWindow.setPreceding("PRECEDING");
         countOlap.setWindowingClause(countOlapWindow);
         Vector selectItems = to_sqs.getSelectStatement().getSelectItemList();
         boolean rownumFound = false;

         for(int j = 0; j < selectItems.size(); ++j) {
            SelectColumn selCol = (SelectColumn)selectItems.get(j);
            selCol.replaceRownumTableColumn(countOlap);
         }

         if (to_sqs.getRownumClause() != null) {
            HavingStatement qualify = new HavingStatement();
            qualify.setHavingClause("QUALIFY");
            rownumSelColExp = new Vector();
            WhereExpression we = new WhereExpression();
            wiVec = new Vector();
            WhereItem wi = new WhereItem();
            WhereColumn lwc = new WhereColumn();
            rownumOrderColExp = new Vector();
            if (rownumFound) {
               if (qualifyAlias != null) {
                  rownumOrderColExp.add(qualifyAlias);
               } else {
                  rownumOrderColExp.add("rownum");
               }
            } else {
               rownumOrderColExp.add(countOlap);
            }

            lwc.setColumnExpression(rownumOrderColExp);
            wi.setLeftWhereExp(lwc);
            WhereColumn rwc = new WhereColumn();
            Vector rwcColExp = new Vector();
            rwcColExp.add(to_sqs.getRownumClause().getRownumValue());
            rwc.setColumnExpression(rwcColExp);
            wi.setRightWhereExp(rwc);
            wi.setOperator(to_sqs.getRownumClause().getOperator());
            wiVec.add(wi);
            we.setWhereItem(wiVec);
            rownumSelColExp.add(we);
            qualify.setHavingItems(rownumSelColExp);
            to_sqs.setQualifyStatement(qualify);
         }
      } else {
         qualifyAlias = "ROW_NO";
         SelectColumn rownumSelCol = new SelectColumn();
         FunctionCalls rownumFunc = new FunctionCalls();
         TableColumn rownumFuncName = new TableColumn();
         rownumFuncName.setColumnName("COUNT");
         rownumFunc.setFunctionName(rownumFuncName);
         FunctionCalls countOlapCast = new FunctionCalls();
         TableColumn countOlapCastName = new TableColumn();
         countOlapCastName.setColumnName("CAST");
         countOlapCast.setFunctionName(countOlapCastName);
         orderItemList = new Vector();
         orderItemList.add("1");
         NumericClass integerType = new NumericClass();
         integerType.setDatatypeName("INTEGER");
         integerType.setOpenBrace((String)null);
         integerType.setClosedBrace((String)null);
         orderItemList.add(integerType);
         countOlapCast.setAsDatatype("AS");
         countOlapCast.setFunctionArguments(orderItemList);
         Vector rownumFuncArgs = new Vector();
         SelectColumn countOlapCastSC = new SelectColumn();
         Vector countOlapCastSCExpr = new Vector();
         countOlapCastSCExpr.add(countOlapCast);
         countOlapCastSC.setColumnExpression(countOlapCastSCExpr);
         rownumFuncArgs.add(countOlapCastSC);
         rownumFunc.setFunctionArguments(rownumFuncArgs);
         OrderByStatement newObs;
         Vector selectItems;
         int j;
         if (to_sqs.getOrderByStatement() != null) {
            newObs = to_sqs.getOrderByStatement();
            OrderByStatement newObs = new OrderByStatement();
            selectItems = new Vector();
            wiVec = newObs.getOrderItemList();

            for(j = 0; j < wiVec.size(); ++j) {
               OrderItem oi = (OrderItem)wiVec.elementAt(j);
               if (oi != null) {
                  selectItems.add(oi);
               }
            }

            newObs.setOrderItemList(selectItems);
            newObs.setOrderClause("ORDER BY");
            rownumFunc.setOrderBy(newObs);
         }

         if (combinedOrderItems.size() > 0) {
            newObs = new OrderByStatement();
            newObs.setOrderItemList(combinedOrderItems);
            newObs.setOrderClause("ORDER BY");
            rownumFunc.setOrderBy(newObs);
         }

         rownumFunc.setOver("OVER");
         WindowingClause countOlapWindow = new WindowingClause();
         countOlapWindow.setRowsOrRange("ROWS");
         countOlapWindow.setUnbounded("UNBOUNDED");
         countOlapWindow.setPreceding("PRECEDING");
         rownumFunc.setWindowingClause(countOlapWindow);
         rownumSelColExp = new Vector();
         rownumSelColExp.add(rownumFunc);
         rownumSelCol.setColumnExpression(rownumSelColExp);
         selectItems = to_sqs.getSelectStatement().getSelectItemList();
         boolean rownumFound = false;

         SelectColumn rownumOrderCol;
         for(j = 0; j < selectItems.size(); ++j) {
            rownumOrderCol = (SelectColumn)selectItems.get(j);
            rownumOrderCol.replaceRownumTableColumn(rownumSelCol);
         }

         if (!rownumFound) {
            qualifyAlias = null;
         }

         if (to_sqs.getRownumClause() != null) {
            HavingStatement qualify = new HavingStatement();
            qualify.setHavingClause("QUALIFY");
            Vector qualifyItems = new Vector();
            WhereExpression we = new WhereExpression();
            Vector wiVec = new Vector();
            WhereItem wi = new WhereItem();
            WhereColumn lwc = new WhereColumn();
            Vector lwcColExp = new Vector();
            if (qualifyAlias == null) {
               lwcColExp.add(rownumSelCol);
            } else {
               lwcColExp.add(qualifyAlias);
            }

            lwc.setColumnExpression(lwcColExp);
            wi.setLeftWhereExp(lwc);
            WhereColumn rwc = new WhereColumn();
            Vector rwcColExp = new Vector();
            rwcColExp.add(to_sqs.getRownumClause().getRownumValue());
            rwc.setColumnExpression(rwcColExp);
            wi.setRightWhereExp(rwc);
            wi.setOperator(to_sqs.getRownumClause().getOperator());
            wiVec.add(wi);
            we.setWhereItem(wiVec);
            qualifyItems.add(we);
            qualify.setHavingItems(qualifyItems);
            to_sqs.setQualifyStatement(qualify);
         }

         OrderItem rownumOrderItem = new OrderItem();
         rownumOrderCol = new SelectColumn();
         rownumOrderColExp = new Vector();
         rownumOrderColExp.add(qualifyAlias);
         rownumOrderCol.setColumnExpression(rownumSelColExp);
         rownumOrderItem.setOrderSpecifier(rownumOrderCol);
         OrderByStatement var61 = to_sqs.getOrderByStatement();
      }

   }

   private void replaceRownumTableColumn(SelectColumn sc, Object newColumn) throws ConvertException {
   }

   public boolean isUnionClausePresent() {
      boolean unionClausePresent = false;
      if (this.set_operator_clause != null) {
         unionClausePresent = true;
      } else if (this.set_operator_clause == null) {
         unionClausePresent = this.isUnionClausePresentInSubQuery(this);
      }

      return unionClausePresent;
   }

   public boolean isUnionClausePresentInSubQuery(SelectQueryStatement subQueryStmt) {
      boolean unionClausePresent = false;
      Vector fromItems = subQueryStmt.getFromClause().getFromItemList();

      for(int j = 0; j < fromItems.size(); ++j) {
         if (fromItems.get(j) instanceof FromTable) {
            FromTable ft = (FromTable)fromItems.get(j);
            if (ft.getTableName() instanceof SelectQueryStatement && ((SelectQueryStatement)ft.getTableName()).getSetOperatorClause() != null) {
               unionClausePresent = true;
               break;
            }

            if (ft.getTableName() instanceof SelectQueryStatement) {
               unionClausePresent = this.isUnionClausePresentInSubQuery((SelectQueryStatement)ft.getTableName());
               if (unionClausePresent) {
                  break;
               }
            } else if (ft.getTableName() instanceof FromClause) {
               unionClausePresent = this.isUnionClausePresentInFromClause((FromClause)ft.getTableName());
               if (unionClausePresent) {
                  break;
               }
            }
         }
      }

      return unionClausePresent;
   }

   private boolean isUnionClausePresentInFromClause(FromClause fc) {
      boolean unionClausePresent = false;
      Vector fromItems = fc.getFromItemList();

      for(int j = 0; j < fromItems.size(); ++j) {
         if (fromItems.get(j) instanceof FromTable) {
            FromTable ft = (FromTable)fromItems.get(j);
            if (ft.getTableName() instanceof SelectQueryStatement && ((SelectQueryStatement)ft.getTableName()).getSetOperatorClause() != null) {
               unionClausePresent = true;
               break;
            }

            if (ft.getTableName() instanceof SelectQueryStatement) {
               unionClausePresent = this.isUnionClausePresentInSubQuery((SelectQueryStatement)ft.getTableName());
               if (unionClausePresent) {
                  break;
               }
            } else if (ft.getTableName() instanceof FromClause) {
               unionClausePresent = this.isUnionClausePresentInFromClause((FromClause)ft.getTableName());
               if (unionClausePresent) {
                  break;
               }
            }
         } else if (fromItems.get(j) instanceof FromClause) {
            unionClausePresent = this.isUnionClausePresentInFromClause((FromClause)fromItems.get(j));
            if (unionClausePresent) {
               break;
            }
         }
      }

      return unionClausePresent;
   }

   public LinkedList getListOfUnionedSelectQueries() {
      if (this.set_operator_clause != null) {
         this.list = new LinkedList();
         this.set_operator_clause.getSelectQueryStatement().setTopLevel(this.topLevel);
         this.list.add(this.recurseAndGetUnionSQLS(this, this.list, new LinkedList(), true));
         return this.list;
      } else {
         this.list = new LinkedList();
         return this.getListOfUnionedSelectQueriesInFromClause(this.getFromClause(), this.list);
      }
   }

   private LinkedList getListOfUnionedSelectQueriesInFromClause(FromClause fc, LinkedList list) {
      Vector fromItems = fc.getFromItemList();

      for(int j = 0; j < fromItems.size(); ++j) {
         if (fromItems.get(j) instanceof FromTable) {
            FromTable ft = (FromTable)fromItems.get(j);
            if (ft.getTableName() instanceof SelectQueryStatement) {
               LinkedList segList = this.recurseAndGetUnionSQLS((SelectQueryStatement)ft.getTableName(), list, new LinkedList(), true);
               if (!segList.isEmpty()) {
                  list.add(segList);
               }
            } else if (ft.getTableName() instanceof FromClause) {
               this.getListOfUnionedSelectQueriesInFromClause((FromClause)ft.getTableName(), list);
            }
         } else if (fromItems.get(j) instanceof FromClause) {
            this.getListOfUnionedSelectQueriesInFromClause((FromClause)fromItems.get(j), list);
         }
      }

      return list;
   }

   private LinkedList recurseAndGetUnionSQLS(SelectQueryStatement sqs, LinkedList list, LinkedList segmentList, boolean first) {
      if (sqs.getSetOperatorClause() != null) {
         SelectQueryStatement ss = new SelectQueryStatement();
         ss.setTopLevel(sqs.getTopLevel());
         ss.setSelectStatement(sqs.getSelectStatement());
         ss.setFromClause(sqs.getFromClause());
         if (sqs.getWhereExpression() != null) {
            ss.setWhereExpression(sqs.getWhereExpression());
         }

         if (sqs.getOrderByStatement() != null) {
            ss.setOrderByStatement(sqs.getOrderByStatement());
         }

         if (sqs.getGroupByStatement() != null) {
            ss.setGroupByStatement(sqs.getGroupByStatement());
         }

         if (sqs.getHavingStatement() != null) {
            ss.setHavingStatement(sqs.getHavingStatement());
         }

         segmentList.add(ss);
         this.recurseAndGetUnionSQLS(sqs.getSetOperatorClause().getSelectQueryStatement(), list, segmentList, false);
         Vector fromItems = sqs.getFromClause().getFromItemList();

         for(int j = 0; j < fromItems.size(); ++j) {
            if (fromItems.get(j) instanceof FromTable) {
               FromTable ft = (FromTable)fromItems.get(j);
               if (ft.getTableName() instanceof SelectQueryStatement) {
                  LinkedList segList = this.recurseAndGetUnionSQLS((SelectQueryStatement)ft.getTableName(), list, new LinkedList(), true);
                  if (!segList.isEmpty()) {
                     list.add(segList);
                  }
               } else if (ft.getTableName() instanceof FromClause) {
                  this.getListOfUnionedSelectQueriesInFromClause((FromClause)ft.getTableName(), list);
               }
            }
         }
      } else {
         if (!first) {
            segmentList.add(sqs);
         }

         Vector fromItems = sqs.getFromClause().getFromItemList();

         for(int j = 0; j < fromItems.size(); ++j) {
            if (fromItems.get(j) instanceof FromTable) {
               FromTable ft = (FromTable)fromItems.get(j);
               if (ft.getTableName() instanceof SelectQueryStatement) {
                  LinkedList segList = this.recurseAndGetUnionSQLS((SelectQueryStatement)ft.getTableName(), list, new LinkedList(), true);
                  if (!segList.isEmpty()) {
                     list.add(segList);
                  }
               } else if (ft.getTableName() instanceof FromClause) {
                  this.getListOfUnionedSelectQueriesInFromClause((FromClause)ft.getTableName(), list);
               }
            }
         }
      }

      return segmentList;
   }

   private SelectStatement createSelectStatementForDerivedTable(SelectStatement ssold, String dummyAliasName) throws ConvertException {
      new SelectStatement();
      SelectStatement ssnew = ssold.toTeradataSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
      Vector selectItemList = new Vector();
      selectItemList.addAll(ssnew.getSelectItemList());
      selectItemList = this.replaceTableNameInSelectItems(selectItemList, dummyAliasName);
      ssnew.setSelectItemList(selectItemList);
      return ssnew;
   }

   private FromClause createFromClauseForDerivedTable(Vector v, SelectQueryStatement sqs, String dummyAlias) throws ConvertException {
      SelectStatement ss = new SelectStatement();
      ss.setSelectClause("SELECT");
      Vector v1 = new Vector();
      v1.addAll(v);
      v1 = this.removeFunctionsInSelectItemLists(v1);
      ss.setSelectItemList(v1);
      sqs.setSelectStatement(ss);
      FromClause fc = new FromClause();
      fc.setFromClause("FROM");
      FromTable ft = new FromTable();
      Vector fromItem = new Vector();
      ft.setTableName(sqs);
      ft.setAliasName(dummyAlias);
      fromItem.add(ft);
      fc.setFromItemList(fromItem);
      return fc;
   }

   private Vector removeFunctionsInSelectItemLists(Vector vce) throws ConvertException {
      int i = 0;

      for(int size = vce.size(); i < size; ++i) {
         if (vce.get(i) instanceof SelectColumn) {
            SelectColumn dummySelectColumn = (SelectColumn)vce.get(i);
            Vector vse = dummySelectColumn.getColumnExpression();
            if (dummySelectColumn.getAliasName() != null) {
               dummySelectColumn.setAliasName((String)null);
            }

            if (!(vse.get(0) instanceof FunctionCalls)) {
               if (!(vse.get(0) instanceof String) && !this.isDuplicate((TableColumn)vse.get(0), vce, i)) {
                  dummySelectColumn.setColumnExpression(vse);
                  vce.setElementAt(dummySelectColumn, i);
               } else {
                  vce.removeElementAt(i);
                  --size;
                  --i;
               }
            } else {
               int tmp = 0;
               FunctionCalls funcCall = (FunctionCalls)vse.get(0);
               Vector functionCallColumns = funcCall.getFunctionArguments();
               vce.removeElementAt(i);
               --size;

               for(int k = 0; k < functionCallColumns.size(); ++k) {
                  SelectColumn functionArgsAsSelectColumn = (SelectColumn)functionCallColumns.get(k);
                  functionArgsAsSelectColumn.setEndsWith(",");
                  vce.add(i + tmp, functionArgsAsSelectColumn);
                  ++tmp;
                  ++size;
               }

               int psize;
               int pos;
               if (funcCall.getPartitionByClause() != null && funcCall.getPartitionByClause().getSelectColumnList() != null) {
                  ArrayList pSelectItems = funcCall.getPartitionByClause().getSelectColumnList();
                  pos = 0;

                  for(psize = pSelectItems.size(); pos < psize; ++pos) {
                     if (pSelectItems.get(pos) instanceof SelectColumn) {
                        SelectColumn sc = (SelectColumn)pSelectItems.get(pos);
                        sc.setEndsWith(",");
                        vce.add(i + tmp, sc);
                        ++tmp;
                        ++size;
                     }
                  }
               }

               if (funcCall.getOrderBy() != null && funcCall.getOrderBy().getOrderItemList() != null) {
                  Vector orderItemList = funcCall.getOrderBy().getOrderItemList();
                  pos = 0;

                  for(psize = orderItemList.size(); pos < psize; ++pos) {
                     if (orderItemList.get(pos) instanceof OrderItem) {
                        OrderItem oi = (OrderItem)orderItemList.get(pos);
                        if (oi.getOrderSpecifier() instanceof SelectColumn) {
                           SelectColumn orderSpecifierAsSelectColumn = oi.getOrderSpecifier();
                           orderSpecifierAsSelectColumn.setEndsWith(",");
                           vce.add(i + tmp, orderSpecifierAsSelectColumn);
                           ++tmp;
                           ++size;
                        }
                     }
                  }
               }

               --i;
            }
         }
      }

      if (vce.lastElement() instanceof SelectColumn) {
         SelectColumn dummySelectColumn = (SelectColumn)vce.lastElement();
         dummySelectColumn.setEndsWith((String)null);
         vce.setElementAt(dummySelectColumn, vce.size() - 1);
      }

      return vce;
   }

   private boolean isDuplicate(TableColumn obj, Vector vce, int limit) {
      String colStr = "";
      String objStr = obj.getColumnName().trim();

      for(int i = 0; i < limit; ++i) {
         if (vce.get(i) instanceof SelectColumn) {
            SelectColumn dummy = (SelectColumn)vce.get(i);
            Vector colExp = dummy.getColumnExpression();
            if (colExp.get(0) instanceof TableColumn) {
               colStr = ((TableColumn)colExp.get(0)).getColumnName();
               if (objStr.indexOf("\"") != -1) {
                  objStr = objStr.replaceAll("\"", "").trim();
               } else if (objStr.indexOf("'") != -1) {
                  objStr = objStr.replaceAll("'", "").trim();
               }

               if (colStr.indexOf("\"") != -1) {
                  colStr = colStr.replaceAll("\"", "").trim();
               } else if (colStr.indexOf("'") != -1) {
                  colStr = colStr.replaceAll("'", "").trim();
               }

               if (objStr.equalsIgnoreCase(colStr)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   private Vector replaceTableNameInSelectItems(Vector newSelectItems1, String newTableName) throws ConvertException {
      Vector newSI = new Vector();
      newSI.addAll(newSelectItems1);
      int size = newSI.size();

      for(int i = 0; i < size; ++i) {
         Vector funArgs;
         if (newSI.get(i) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)newSI.get(i);
            funArgs = new Vector();
            funArgs.addAll(sc.getColumnExpression());
            funArgs = this.replaceTableNameInSelectItems(funArgs, newTableName);
            sc.setColumnExpression(funArgs);
            newSI.setElementAt(sc.toTeradataSelect((SelectQueryStatement)null, (SelectQueryStatement)null), i);
         } else if (!(newSI.elementAt(i) instanceof FunctionCalls)) {
            if (newSI.elementAt(i) instanceof TableColumn) {
               TableColumn tc = (TableColumn)newSI.elementAt(i);
               tc.setTableName(newTableName);
               newSI.setElementAt(tc, i);
            }
         } else {
            FunctionCalls fc = (FunctionCalls)newSI.get(i);
            funArgs = new Vector();
            funArgs.addAll(fc.getFunctionArguments());
            this.replaceTableNameInSelectItems(funArgs, newTableName);
            int pos;
            int psize;
            Vector orderSpecifierList;
            if (fc.getPartitionByClause() != null && fc.getPartitionByClause().getSelectColumnList() != null) {
               ArrayList pSelectItems = new ArrayList();
               pSelectItems.addAll(fc.getPartitionByClause().getSelectColumnList());
               pos = 0;

               for(psize = pSelectItems.size(); pos < psize; ++pos) {
                  if (pSelectItems.get(pos) instanceof SelectColumn) {
                     SelectColumn sc = (SelectColumn)pSelectItems.get(pos);
                     orderSpecifierList = new Vector();
                     orderSpecifierList.addAll(sc.getColumnExpression());
                     orderSpecifierList = this.replaceTableNameInSelectItems(orderSpecifierList, newTableName);
                     sc.setColumnExpression(orderSpecifierList);
                     pSelectItems.set(pos, sc.toTeradataSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
                  }
               }
            }

            if (fc.getOrderBy() != null && fc.getOrderBy().getOrderItemList() != null) {
               Vector orderItemList = fc.getOrderBy().getOrderItemList();
               pos = 0;

               for(psize = orderItemList.size(); pos < psize; ++pos) {
                  if (orderItemList.get(pos) instanceof OrderItem) {
                     OrderItem oi = (OrderItem)orderItemList.get(pos);
                     if (oi.getOrderSpecifier() instanceof SelectColumn) {
                        orderSpecifierList = new Vector();
                        orderSpecifierList.add(oi.getOrderSpecifier());
                        orderSpecifierList = this.replaceTableNameInSelectItems(orderSpecifierList, newTableName);
                        oi.setOrderSpecifier((SelectColumn)orderSpecifierList.get(0));
                        orderItemList.set(pos, oi);
                     }
                  }
               }
            }
         }
      }

      return newSI;
   }

   private boolean isCrossJoinAvailable(SelectQueryStatement sqs_Teradata_sql) {
      new FromTable();
      new FromClause();
      if (sqs_Teradata_sql != null && sqs_Teradata_sql.getFromClause() != null) {
         FromClause fc = sqs_Teradata_sql.getFromClause();
         Vector fromItemList = fc.getFromItemList();
         int size = fromItemList.size();

         for(int i = 0; i < size; ++i) {
            if (fromItemList.get(i) instanceof FromTable) {
               FromTable ft = (FromTable)fromItemList.get(i);
               if (ft.getCrossJoinForPartitionClause() != null) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public void handleTeradataUnionAllConversion(LinkedList ArrayOfColumns) throws ConvertException {
      try {
         LinkedList listOfUnionQueries = ArrayOfColumns;
         if (this.list != null && this.list.size() == ArrayOfColumns.size()) {
            for(int k = 0; k < this.list.size(); ++k) {
               LinkedList listOfCols = (LinkedList)listOfUnionQueries.get(k);
               LinkedList listOfSegments = (LinkedList)this.list.get(k);
               if (listOfCols.size() == listOfSegments.size()) {
                  for(int n = 0; n < listOfCols.size(); ++n) {
                     SelectQueryStatement sqs = (SelectQueryStatement)listOfSegments.get(n);
                     Vector v = (Vector)listOfCols.get(n);
                     Vector v1 = (Vector)v.get(0);
                     Vector selectItem = sqs.getSelectStatement().getSelectItemList();
                     if (v1.size() == selectItem.size()) {
                        for(int j = 0; j < selectItem.size(); ++j) {
                           HashMap h = (HashMap)v1.get(j);
                           SelectColumn origSelCol = (SelectColumn)selectItem.get(j);
                           if (h.get("Update Flag").toString().equalsIgnoreCase("true") && !origSelCol.isTeradataUnionCastingDone()) {
                              SelectColumn castCol = new SelectColumn();
                              if (origSelCol.getAliasName() != null) {
                                 castCol.setAliasName(origSelCol.getAliasName());
                              } else if (origSelCol.getAliasForExpression() != null && origSelCol.getAliasForExpression().length() > 0) {
                                 if (origSelCol.getAliasForExpression().startsWith("\"")) {
                                    castCol.setAliasName(origSelCol.getAliasForExpression());
                                 } else {
                                    castCol.setAliasName("\"" + origSelCol.getAliasForExpression() + "\"");
                                 }
                              } else if (origSelCol.getColumnExpression().size() == 1 && !sqs.getTopLevel()) {
                                 String origSCAlias = origSelCol.getColumnExpression().get(0).toString().replaceAll("\n", " ").replaceAll("\t", " ").trim();
                                 if (origSCAlias.indexOf("*/") != -1) {
                                    origSCAlias = origSCAlias.substring(origSCAlias.lastIndexOf("*/") + 1);
                                 }

                                 if (origSCAlias.indexOf(".") != -1 && origSCAlias.indexOf(".") == origSCAlias.lastIndexOf(".")) {
                                    origSCAlias = origSCAlias.substring(origSCAlias.lastIndexOf(".") + 1);
                                 }

                                 if (origSCAlias.length() > 30) {
                                    origSCAlias = origSCAlias.substring(0, 29);
                                 }

                                 if (origSCAlias.length() > 0) {
                                    if (origSCAlias.startsWith("\"") && origSCAlias.length() > 2) {
                                       castCol.setAliasName(origSCAlias);
                                    } else {
                                       castCol.setAliasName("\"" + origSCAlias + "\"");
                                    }
                                 }
                              }

                              Vector castColExp = new Vector();
                              FunctionCalls castFunc = new FunctionCalls();
                              TableColumn castTCN = new TableColumn();
                              castTCN.setColumnName("CAST");
                              castFunc.setFunctionName(castTCN);
                              castFunc.setFunctionArguments(origSelCol.getColumnExpression());
                              String de = h.get("SQL Type").toString();
                              String deName = h.get("SQL Type").toString();
                              castFunc.getFunctionArguments().add(this.getDataTypeForCasting(deName, h.get("Precision").toString(), h.get("Scale").toString()));
                              castFunc.setAsDatatype("AS");
                              castColExp.add(castFunc);
                              castCol.setColumnExpression(castColExp);
                              if (origSelCol.getEndsWith() != null) {
                                 castCol.setEndsWith(origSelCol.getEndsWith());
                              }

                              castCol.setTeradataUnionCastingDone(true);
                              selectItem.setElementAt(castCol, j);
                           } else if (h.get("Update Flag").toString().equalsIgnoreCase("false") && origSelCol.getAliasName() == null && origSelCol.getAliasForExpression() != null) {
                              if (origSelCol.getAliasForExpression().startsWith("\"")) {
                                 origSelCol.setAliasName(origSelCol.getAliasForExpression());
                              } else {
                                 origSelCol.setAliasName("\"" + origSelCol.getAliasForExpression() + "\"");
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

      } catch (Exception var20) {
         String message = "Exception occurred while converting Union All queries using Prepared statement metadata. \n More details : \n" + var20.getMessage();
         ConvertException ce = new ConvertException(message);
         ce.setStackTrace(var20.getStackTrace());
         throw ce;
      }
   }

   private Datatype getDataTypeForCasting(String dataTypeName, String precision, String scale) {
      Datatype dt = null;
      if (!dataTypeName.equalsIgnoreCase("decimal") && !dataTypeName.equalsIgnoreCase("numeric")) {
         if (!dataTypeName.equalsIgnoreCase("varchar") && !dataTypeName.equalsIgnoreCase("char") && !dataTypeName.equalsIgnoreCase("varbyte") && !dataTypeName.equalsIgnoreCase("byte")) {
            if (dataTypeName.equalsIgnoreCase("timestamp")) {
               dt = new DateClass();
               ((Datatype)dt).setDatatypeName(dataTypeName);
               ((Datatype)dt).setSize("0");
               ((Datatype)dt).setOpenBrace("(");
               ((Datatype)dt).setClosedBrace(")");
            } else if (dataTypeName.equalsIgnoreCase("date")) {
               dt = new DateClass();
               ((Datatype)dt).setDatatypeName(dataTypeName);
            } else {
               dt = new NumericClass();
               ((Datatype)dt).setDatatypeName(dataTypeName);
            }
         } else {
            dt = new CharacterClass();
            ((Datatype)dt).setDatatypeName(dataTypeName);
            ((Datatype)dt).setSize(precision.toString());
            if (SwisSQLOptions.castCharDatatypeAsCaseSpecific) {
               ((CharacterClass)dt).setCaseSpecificPhrase("CASESPECIFIC");
            }

            ((Datatype)dt).setOpenBrace("(");
            ((Datatype)dt).setClosedBrace(")");
         }
      } else {
         dt = new NumericClass();
         ((Datatype)dt).setDatatypeName(dataTypeName);
         ((NumericClass)dt).setPrecision(precision.toString());
         ((Datatype)dt).setOpenBrace("(");
         ((Datatype)dt).setClosedBrace(")");
         if (!scale.toString().equalsIgnoreCase("0")) {
            ((NumericClass)dt).setScale(scale.toString());
         }
      }

      return (Datatype)dt;
   }

   public SelectQueryStatement toVectorWise() throws ConvertException {
      SelectQueryStatement sqs_vw_sql = new SelectQueryStatement();
      sqs_vw_sql.setSetOperatorQuery(this.isSetOperatorQuery);
      sqs_vw_sql.setQueryConversionPropHandler(this.getQueryConvPropHandler());
      sqs_vw_sql.setSelectStatement(this.select_statement.toVectorWiseSelect(sqs_vw_sql, this));
      if (this.openBrace != null) {
         sqs_vw_sql.setOpenBrace(this.openBrace);
      }

      if (this.from_clause != null) {
         sqs_vw_sql.setFromClause(this.from_clause.toVectorWiseSelect(sqs_vw_sql, this));
      }

      if (this.whereExpression != null) {
         sqs_vw_sql.setWhereExpression(this.whereExpression.toVectorWiseSelect(sqs_vw_sql, this));
      }

      if (this.order_by_statement != null) {
         sqs_vw_sql.setOrderByStatement(this.order_by_statement.toVectorWiseSelect(sqs_vw_sql, this));
      }

      if (this.computeByVector != null) {
         Vector computeByStatementVector = new Vector();

         for(int i = 0; i < this.computeByVector.size(); ++i) {
            computeByStatementVector.add(((ComputeByStatement)this.computeByVector.get(i)).toVectorWiseSelect(sqs_vw_sql, this));
         }

         sqs_vw_sql.setComputeByStatements(computeByStatementVector);
         sqs_vw_sql.setCommentForCompute(true);
      }

      if (sqs_vw_sql.getGroupByStatement() == null && this.group_by_statement != null) {
         sqs_vw_sql.setGroupByStatement(this.group_by_statement.toVectorWiseSelect(sqs_vw_sql, this));
      }

      if (this.set_operator_clause != null) {
         sqs_vw_sql.setSetOperatorClause(this.set_operator_clause.toVectorWiseSelect(sqs_vw_sql, this));
         this.replaceSelectItemsForStringLiteralsAndNULLString(sqs_vw_sql, "VARCHAR");
      }

      if (this.having_statement != null) {
         sqs_vw_sql.setHavingStatement(this.having_statement.toVectorWiseSelect(sqs_vw_sql, this));
         this.handleHavingClauseWithoutGroupByClauseQueries(sqs_vw_sql);
      }

      if (this.into_statement != null) {
         sqs_vw_sql.setIntoStatement(this.into_statement.toVectorWiseSelect(sqs_vw_sql, this));
      }

      if (this.hierarchical_query_clause != null) {
         throw new ConvertException();
      } else {
         if (this.limit_clause != null) {
            sqs_vw_sql.setLimitClause(this.limit_clause.toVectorWiseSelect(sqs_vw_sql, this));
         }

         if (this.closeBrace != null) {
            sqs_vw_sql.setCloseBrace(this.closeBrace);
         }

         sqs_vw_sql.setOptionalHintClause((OptionalHintClause)null);
         return sqs_vw_sql;
      }
   }

   public String toVectorWiseString() throws ConvertException {
      return this.toVectorWise().toString();
   }

   public SelectQueryStatement toReplaceTblCol() throws ConvertException {
      SelectQueryStatement sqs_replacetblcol_sql = new SelectQueryStatement();
      if (this.commentObject != null) {
         sqs_replacetblcol_sql.setCommentClass(this.commentObject);
      }

      if (this.openBrace != null) {
         sqs_replacetblcol_sql.setOpenBrace(this.openBrace);
      }

      if (this.from_clause != null) {
         sqs_replacetblcol_sql.setFromClause(this.from_clause.toReplaceTblCol(sqs_replacetblcol_sql, this));
      }

      sqs_replacetblcol_sql.setSelectStatement(this.select_statement.toReplaceTblCol(sqs_replacetblcol_sql, this));
      if (this.whereExpression != null) {
         sqs_replacetblcol_sql.setWhereExpression(this.whereExpression.toReplaceTblCol(sqs_replacetblcol_sql, this));
      }

      if (this.order_by_statement != null) {
         sqs_replacetblcol_sql.setOrderByStatement(this.order_by_statement.toReplaceTblCol(sqs_replacetblcol_sql, this));
      }

      if (this.computeByVector != null) {
         Vector computeByStatementVector = new Vector();

         for(int i = 0; i < this.computeByVector.size(); ++i) {
            computeByStatementVector.add(((ComputeByStatement)this.computeByVector.get(i)).toReplaceTblCol(sqs_replacetblcol_sql, this));
         }

         sqs_replacetblcol_sql.setComputeByStatements(computeByStatementVector);
      }

      if (this.group_by_statement != null) {
         sqs_replacetblcol_sql.setGroupByStatement(this.group_by_statement.toReplaceTblCol(sqs_replacetblcol_sql, this));
      }

      if (this.set_operator_clause != null) {
         sqs_replacetblcol_sql.setSetOperatorClause(this.set_operator_clause.toReplaceTblCol(sqs_replacetblcol_sql, this));
      }

      if (this.having_statement != null) {
         sqs_replacetblcol_sql.setHavingStatement(this.having_statement.toReplaceTblCol(sqs_replacetblcol_sql, this));
      }

      if (this.into_statement != null) {
         sqs_replacetblcol_sql.setIntoStatement(this.into_statement.toReplaceTblCol(sqs_replacetblcol_sql, this));
      }

      if (this.limit_clause != null) {
         sqs_replacetblcol_sql.setLimitClause(this.limit_clause.toReplaceTblCol(sqs_replacetblcol_sql, this));
      }

      if (this.fetch_clause != null) {
         sqs_replacetblcol_sql.setFetchClause(this.fetch_clause.toReplaceTblCol(sqs_replacetblcol_sql, this));
      }

      if (this.closeBrace != null) {
         sqs_replacetblcol_sql.setCloseBrace(this.closeBrace);
      }

      return sqs_replacetblcol_sql;
   }

   public void addAllIndexPositionsForStringLiterals(Set indexSet) {
      this.indexPositionsOfStringLiteralsSet.addAll(indexSet);
   }

   public void addAllIndexPositionsForStringLiterals(List indexSet) {
      this.indexPositionsOfStringLiteralsSet.addAll(indexSet);
   }

   public void addIndexPostionForStringLiteral(int index) {
      this.indexPositionsOfStringLiteralsSet.add(index);
   }

   public Set getIndexPositionsForStringLiterals() {
      return this.indexPositionsOfStringLiteralsSet;
   }

   public boolean hasToConvertToTextDataTypeForStringLiterals(int selectIndex) {
      return this.indexPositionsOfStringLiteralsSet.contains(selectIndex);
   }

   public void addAllIndexPositionsForNULLString(Set indexSet) {
      this.indexPositionsOfNULLStringsSet.addAll(indexSet);
   }

   public void addAllIndexPositionsForNULLString(List indexSet) {
      this.indexPositionsOfNULLStringsSet.addAll(indexSet);
   }

   public void addIndexPostionForNULLString(int index) {
      this.indexPositionsOfNULLStringsSet.add(index);
   }

   public void removeIndexPositionForNULLString(int index) {
      this.indexPositionsOfNULLStringsSet.remove(index);
   }

   public Set getIndexPositionsForNULLString() {
      return this.indexPositionsOfNULLStringsSet;
   }

   public boolean hasToConvertToTextDataTypeForNULLString(int selectIndex) {
      return this.indexPositionsOfNULLStringsSet.contains(selectIndex);
   }

   public boolean isSelectWithoutSetClause() {
      boolean selectWithoutSetClauses = false;
      if (this.set_operator_clause == null && !this.isSetOperatorQuery) {
         selectWithoutSetClauses = true;
      }

      return selectWithoutSetClauses;
   }

   public boolean isFirstSelectStatementInSetQuery() {
      boolean firstSelectStatement = false;
      if (this.set_operator_clause != null && !this.isSetOperatorQuery) {
         firstSelectStatement = true;
      }

      return firstSelectStatement;
   }

   public void replaceSelectItemsForNULLString(SelectQueryStatement sqs, String dataType) {
      Set indexSet = SwisSQLAPI.getNULLIndexPositionsForCasting();
      if (!indexSet.isEmpty() && sqs.isFirstSelectStatementInSetQuery() && sqs.select_statement != null && sqs.select_statement.getSelectItemList() != null) {
         Vector selectItemList = sqs.select_statement.getSelectItemList();

         for(int i_count = 0; i_count < selectItemList.size(); ++i_count) {
            if (indexSet.contains(i_count) && selectItemList.elementAt(i_count) instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)selectItemList.elementAt(i_count);
               sc.convertSelectColumnToTextDataType(dataType);
            }
         }
      }

   }

   public void replaceSetClauseSelectItemsForStringLiterals(SelectQueryStatement sqs, String dataType) {
      Set indexSet = SwisSQLAPI.getIndexPositionsForCasting();
      if (!indexSet.isEmpty() && sqs.select_statement != null && sqs.select_statement.getSelectItemList() != null) {
         Vector selectItemList = sqs.select_statement.getSelectItemList();

         for(int i_count = 0; i_count < selectItemList.size(); ++i_count) {
            if (indexSet.contains(i_count) && selectItemList.elementAt(i_count) instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)selectItemList.elementAt(i_count);
               sc.convertSelectColumnToTextDataType(dataType);
            }
         }
      }

   }

   public void replaceSelectItemsForSetQueriesDataTypeMismatchedColumns(String dataType) {
      this.replaceSelectItemsForStringLiteralsAndNULLString(this, dataType);
   }

   public void replaceSelectItemsForStringLiteralsAndNULLString(SelectQueryStatement sqs, String dataType) {
      Set indexSet = this.getIndexPositionsForStringLiterals();
      Set indexNULLSet = this.getIndexPositionsForNULLString();
      if ((!indexSet.isEmpty() || !indexNULLSet.isEmpty() && sqs.isFirstSelectStatementInSetQuery()) && sqs.select_statement != null && sqs.select_statement.getSelectItemList() != null) {
         Vector selectItemList = sqs.select_statement.getSelectItemList();

         for(int i_count = 0; i_count < selectItemList.size(); ++i_count) {
            boolean isNullValue = indexNULLSet.contains(i_count);
            String dataTypeNew = isNullValue && dataType.equalsIgnoreCase("VARCHAR") ? "VARCHAR(10)" : dataType;
            if ((indexSet.contains(i_count) || isNullValue) && selectItemList.elementAt(i_count) instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)selectItemList.elementAt(i_count);
               sc.convertSelectColumnToTextDataType(dataTypeNew);
            }
         }
      }

   }

   public void replaceSelectItemsForNULLStringAlone(String dataType) {
      Set indexNULLSet = this.getIndexPositionsForNULLString();
      if (!indexNULLSet.isEmpty() && this.select_statement != null && this.select_statement.getSelectItemList() != null) {
         Vector selectItemList = this.select_statement.getSelectItemList();

         for(int i_count = 0; i_count < selectItemList.size(); ++i_count) {
            boolean isNullValue = indexNULLSet.contains(i_count);
            String dataTypeNew = isNullValue && dataType.equalsIgnoreCase("VARCHAR") ? "VARCHAR(10)" : dataType;
            if (isNullValue && selectItemList.elementAt(i_count) instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)selectItemList.elementAt(i_count);
               Vector colExp = sc.getColumnExpression();
               if (colExp != null && colExp.size() == 1 && colExp.get(0) != null && colExp.get(0) instanceof String && colExp.get(0).toString().trim().equalsIgnoreCase("NULL")) {
                  sc.convertSelectColumnToTextDataType(dataTypeNew);
               }
            }
         }
      }

   }

   public boolean canRemoveOrderByClause() {
      boolean removeOrderBy = false;
      if (SwisSQLAPI.canClearLimitAndFetchClauses() || this.getIntegerValues("removal.option.for.order.and.fetch.clauses") == 1 || this.getIntegerValues("removal.option.for.order.and.fetch.clauses") == 3 || this.getIntegerValues("removal.option.for.order.and.fetch.clauses") == 0 && this.select_statement != null && this.select_statement.getSelectQualifier() != null && this.select_statement.getSelectQualifier().equalsIgnoreCase("DISTINCT")) {
         removeOrderBy = true;
      }

      return removeOrderBy;
   }

   public boolean canRemoveLimitAndFetchClause() {
      boolean removeLimitAndFetch = false;
      if (SwisSQLAPI.canClearLimitAndFetchClauses() || this.getIntegerValues("removal.option.for.order.and.fetch.clauses") == 2 || this.getIntegerValues("removal.option.for.order.and.fetch.clauses") == 3) {
         removeLimitAndFetch = true;
      }

      return removeLimitAndFetch;
   }

   public void removeOrderByStatementAndFetchClausesForPGAndVW() {
      if (this.order_by_statement != null && this.getIntegerValues("removal.option.for.order.and.fetch.clauses") >= 0) {
         switch(this.getIntegerValues("removal.option.for.order.and.fetch.clauses")) {
         case 0:
            if (this.select_statement != null && this.select_statement.getSelectQualifier() != null && this.select_statement.getSelectQualifier().equalsIgnoreCase("DISTINCT")) {
               this.order_by_statement = null;
            }
            break;
         case 1:
            this.order_by_statement = null;
            break;
         case 2:
            this.limit_clause = null;
            this.fetch_clause = null;
            break;
         case 3:
            this.order_by_statement = null;
            this.limit_clause = null;
            this.fetch_clause = null;
         }
      }

   }

   public void handleHavingClauseWithoutGroupByClauseQueries(SelectQueryStatement sqs) {
      if (sqs != null && sqs.getBooleanValues("can.handle.having.without.group.by")) {
         HavingStatement havingSt = sqs.getHavingStatement();
         Vector havingItems = havingSt.getHavingItems();
         boolean hasAggFunctions = false;
         WhereExpression wExp;
         if (havingItems != null && !havingItems.isEmpty()) {
            for(int i = 0; i < havingItems.size(); ++i) {
               if (havingItems.elementAt(i) instanceof WhereExpression) {
                  wExp = (WhereExpression)havingItems.elementAt(i);
                  hasAggFunctions = this.handleHavingWhereExpression(wExp, sqs);
               }
            }
         }

         if (this.group_by_statement == null && !hasAggFunctions) {
            if (this.whereExpression == null) {
               havingSt.setHavingClause("WHERE");
            } else {
               WhereExpression newExp = new WhereExpression();
               wExp = sqs.getWhereExpression();
               wExp.setOpenBrace("(");
               wExp.setCloseBrace(")");
               Vector vecItems = new Vector();
               Vector opItems = new Vector();
               vecItems.add(wExp);
               if (havingItems != null && !havingItems.isEmpty()) {
                  for(int i = 0; i < havingItems.size(); ++i) {
                     if (havingItems.elementAt(i) instanceof WhereExpression) {
                        WhereExpression we = (WhereExpression)havingItems.elementAt(i);
                        we.setOpenBrace("(");
                        we.setCloseBrace(")");
                        vecItems.add(we);
                        opItems.add("AND");
                     }
                  }

                  newExp.setWhereItem(vecItems);
                  newExp.setOperator(opItems);
                  newExp.setOpenBrace("(");
                  newExp.setCloseBrace(")");
                  sqs.setWhereExpression(newExp);
                  sqs.setHavingStatement((HavingStatement)null);
               }
            }
         }
      }

   }

   private boolean handleHavingWhereExpression(WhereExpression we, SelectQueryStatement sqs) {
      boolean hasAggFunctions = false;

      try {
         Vector wItems = we.getWhereItems();
         if (wItems != null && !wItems.isEmpty()) {
            for(int j = 0; j < wItems.size(); ++j) {
               if (wItems.elementAt(j) instanceof WhereItem) {
                  WhereItem wi = (WhereItem)wItems.get(j);
                  WhereColumn leftExp = wi.getLeftWhereExp();
                  if (leftExp != null && leftExp.getColumnExpression() != null && leftExp.getColumnExpression().size() == 1) {
                     String columnName;
                     TableColumn columnNameNew;
                     if (leftExp.getColumnExpression().get(0) instanceof String) {
                        columnName = ((String)leftExp.getColumnExpression().get(0)).toString().toUpperCase().replaceAll("`", "").replaceAll("\"", "").replaceAll("'", "");
                        if (sqs.aliasVsSelectColExpMap.containsKey(columnName)) {
                           columnNameNew = new TableColumn();
                           columnNameNew.setColumnName((String)sqs.aliasVsSelectColExpMap.get(columnName));
                           leftExp.getColumnExpression().setElementAt(columnNameNew, 0);
                        }
                     }

                     if (leftExp.getColumnExpression().get(0) instanceof TableColumn) {
                        columnName = ((TableColumn)leftExp.getColumnExpression().get(0)).toString().toUpperCase().replaceAll("`", "").replaceAll("\"", "").replaceAll("'", "");
                        if (sqs.aliasVsSelectColExpMap.containsKey(columnName)) {
                           columnNameNew = new TableColumn();
                           columnNameNew.setColumnName((String)sqs.aliasVsSelectColExpMap.get(columnName));
                           leftExp.getColumnExpression().setElementAt(columnNameNew, 0);
                        }
                     } else if (leftExp.getColumnExpression().get(0) instanceof FunctionCalls) {
                        FunctionCalls fc = (FunctionCalls)leftExp.getColumnExpression().get(0);
                        hasAggFunctions = this.checkInsideCastFunctionForHavingClauseAliasConversion(fc, sqs.aliasVsSelectColExpMap);
                     } else if (leftExp.getColumnExpression().get(0) instanceof SelectColumn) {
                        SelectColumn sc = (SelectColumn)leftExp.getColumnExpression().get(0);
                        if (sc != null && sc.getColumnExpression() != null && sc.getColumnExpression().size() == 3 && sc.getColumnExpression().get(0) != null && sc.getColumnExpression().get(1) != null && sc.getColumnExpression().get(2) != null && sc.getColumnExpression().get(0) instanceof FunctionCalls && sc.getColumnExpression().get(1) instanceof String && sc.getColumnExpression().get(2) instanceof String && sc.getColumnExpression().get(1).toString().equals("*") && sc.getColumnExpression().get(2).toString().equals("1")) {
                           FunctionCalls fc = (FunctionCalls)sc.getColumnExpression().get(0);
                           hasAggFunctions = this.checkInsideCastFunctionForHavingClauseAliasConversion(fc, sqs.aliasVsSelectColExpMap);
                        }
                     }
                  }
               } else if (wItems.elementAt(j) instanceof WhereExpression) {
                  WhereExpression we1 = (WhereExpression)wItems.elementAt(j);
                  hasAggFunctions = this.handleHavingWhereExpression(we1, sqs);
               }
            }
         }
      } catch (StackOverflowError var10) {
         hasAggFunctions = false;
      } catch (Exception var11) {
         hasAggFunctions = false;
      }

      return hasAggFunctions;
   }

   public boolean checkInsideCastFunctionForHavingClauseAliasConversion(FunctionCalls fc, Map<String, String> aliasVsSelectColExpMap) {
      boolean hasAggFunctions = false;

      try {
         String functionName = fc.getFunctionNameAsAString();
         if (functionName != null && !functionName.isEmpty()) {
            if (functionName.equalsIgnoreCase("CAST")) {
               return this.replaceAliasNameInsideFunction(fc, aliasVsSelectColExpMap, 2, true);
            }

            if (functionName.equalsIgnoreCase("SUM") || functionName.equalsIgnoreCase("AVG") || functionName.equalsIgnoreCase("COUNT") || functionName.equalsIgnoreCase("MIN") || functionName.equalsIgnoreCase("MAX") || functionName.equalsIgnoreCase("STD") || functionName.equalsIgnoreCase("VARIANCE") || functionName.startsWith("PERCENTILE_CONT") || functionName.startsWith("MODE()")) {
               hasAggFunctions = true;
               this.replaceAliasNameInsideFunction(fc, aliasVsSelectColExpMap, 1, false);
            }
         }
      } catch (StackOverflowError var5) {
         hasAggFunctions = false;
      } catch (Exception var6) {
         hasAggFunctions = false;
      }

      return hasAggFunctions;
   }

   public boolean replaceAliasNameInsideFunction(FunctionCalls fc, Map<String, String> aliasVsSelectColExpMap, int argCount, boolean allowFnCall) {
      try {
         if (fc.getFunctionArguments() != null && fc.getFunctionArguments().size() == argCount && fc.getFunctionArguments().get(0) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)fc.getFunctionArguments().get(0);
            if (sc.getColumnExpression() != null && sc.getColumnExpression().size() == 1) {
               if (sc.getColumnExpression().get(0) instanceof TableColumn) {
                  String columnName = ((TableColumn)sc.getColumnExpression().get(0)).toString().toUpperCase().replaceAll("`", "").replaceAll("\"", "").replaceAll("'", "");
                  if (aliasVsSelectColExpMap.containsKey(columnName)) {
                     TableColumn columnNameNew = new TableColumn();
                     columnNameNew.setColumnName((String)aliasVsSelectColExpMap.get(columnName));
                     sc.getColumnExpression().setElementAt(columnNameNew, 0);
                  }
               } else if (allowFnCall && sc.getColumnExpression().get(0) instanceof FunctionCalls) {
                  return this.checkInsideCastFunctionForHavingClauseAliasConversion((FunctionCalls)((FunctionCalls)sc.getColumnExpression().get(0)), aliasVsSelectColExpMap);
               }
            }
         }
      } catch (StackOverflowError var8) {
      } catch (Exception var9) {
      }

      return false;
   }

   public void setPropAndHandlerFromSQS(SelectQueryStatement from_sqs) {
      this.setQueryConvDataHandler(from_sqs.getQueryConvDataHandler());
      this.setQueryConversionPropHandler(from_sqs.getQueryConvPropHandler());
      this.getSelectStatement().setAliasNameVersion(from_sqs.getSelectStatement().getAliasNameVersion());
      this.setCanAllowExceptionStacking(from_sqs.getCanAllowExceptionStacking());
      this.setCanSkipExceptions(from_sqs.getCanSkipExceptions());
   }

   public void setPropAndHandlerFromWS(WithStatement from_with) {
      this.setQueryConvDataHandler(from_with.getQueryConvDataHandler());
      this.setQueryConversionPropHandler(from_with.getQueryConvPropHandler());
      this.getSelectStatement().setAliasNameVersion(from_with.getWithSQS().getSelectStatement().getAliasNameVersion());
      this.setCanAllowExceptionStacking(from_with.getWithSQS().getCanAllowExceptionStacking());
      this.setCanSkipExceptions(from_with.getWithSQS().getCanSkipExceptions());
   }

   public boolean getBooleanValues(String literal) {
      if (this.queryConvPropHandler != null) {
         HashMap hashMap = this.getQueryConvPropHandler().getQueryConvProps();
         return (Boolean)hashMap.get(literal);
      } else {
         return (Boolean)this.queryConvPropsHandler.getQueryConvProps().get(literal);
      }
   }

   public int getIntegerValues(String literal) {
      if (this.queryConvPropHandler != null) {
         HashMap hashMap = this.getQueryConvPropHandler().getQueryConvProps();
         return (Integer)hashMap.get(literal);
      } else {
         return (Integer)this.queryConvPropsHandler.getQueryConvProps().get(literal);
      }
   }
}
