package com.adventnet.swissqlapi.sql.statement.create;

import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.config.datatypes.DatatypeMapping;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.ModifiedObjectAttr;
import com.adventnet.swissqlapi.sql.statement.ModifiedObjectNames;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;
import com.adventnet.swissqlapi.sql.statement.insert.InsertClause;
import com.adventnet.swissqlapi.sql.statement.insert.InsertQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.FetchClause;
import com.adventnet.swissqlapi.sql.statement.select.FromClause;
import com.adventnet.swissqlapi.sql.statement.select.FromTable;
import com.adventnet.swissqlapi.sql.statement.select.IntoStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectStatement;
import com.adventnet.swissqlapi.sql.statement.select.SetOperatorClause;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.sql.statement.select.WithStatement;
import com.adventnet.swissqlapi.sql.statement.update.OptionalSpecifier;
import com.adventnet.swissqlapi.sql.statement.update.TableClause;
import com.adventnet.swissqlapi.sql.statement.update.TableExpression;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.util.misc.CastingUtil;
import com.adventnet.swissqlapi.util.misc.CustomizeUtil;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

public class CreateQueryStatement implements SwisSQLStatement {
   private String createOrReplace;
   private String createString;
   private String tableOrView;
   private String as;
   private String force;
   private String onConditionForANSI;
   private String temporary;
   private TableObject tableObject;
   private SelectQueryStatement selectQueryStatement;
   private String withCheckOption;
   private String openBrace;
   private String closedBrace;
   private Vector columnNames;
   private String onCondition;
   private String organisationHeap;
   private String diskAttribute;
   private String oraclePhysicalChar;
   private String materialized;
   private UserObjectContext context = null;
   private String constraint;
   private String constraintName;
   private CreateIndexClause createIndexClause;
   private Vector createIndexVector;
   private boolean createIndexClauseBooleanValue;
   private String typeString;
   private String typeIdentifier;
   private CommentClass commentObject;
   private String indexString;
   private CreateSequenceStatement createSequence;
   private CreateSynonymStatement createSynonym;
   private boolean isTenroxRequirement = false;
   private String onConditionString;
   private String quotedIdentifierCondition;
   private Vector physicalAttributesVector;
   private String ignoreOrReplace;
   private String selectStatementString;
   private DatabaseObject databaseObject;
   private DatatypeMapping mapping;
   private String withReadOnly;
   private boolean includeDrop = false;
   private String dropString;
   public static String commentWhenConstraintNameTruncated = "";
   private ModifiedObjectNames modifiedObjects = null;
   private String withSchemaBinding;
   private String indexedViewStmt;
   private String lock;
   private String lockData;
   private String createSequenceStr;
   private String createSynonymStr;
   private String triggerForIdentity;
   private String mysqlCommentTableOption;
   private String distributeOnRandomClause;
   private FunctionCalls startWithFunction;
   Vector computedColumnsVector = new Vector();
   private InsertQueryStatement insertQueryStatement = null;
   private String externalTable;
   private String externalDirectory;
   private String externalDelimiter;
   private String startWith;
   private String nextString;
   private String viewMetaAttribute;
   private SelectColumn selectColumnInNextClause = new SelectColumn();
   private CreateQueryStatement computedColView = null;
   private String onConditionForTeradata;
   private String noLogConditionForTeradata;
   private boolean comment_flag__for_create_as_select_in_timesten = false;
   private WithStatement withStatement = null;
   private String openBraceForSelectQuery;
   private String closeBraceForSelectQuery;
   private ArrayList ttUniqueIndexforUniqueCons = new ArrayList();

   public void setOpenBraceForSelectQuery(String openBrace) {
      this.openBraceForSelectQuery = openBrace;
   }

   public void setCloseBraceForSelectQuery(String closeBrace) {
      this.closeBraceForSelectQuery = closeBrace;
   }

   public void setInsertQueryStatement(InsertQueryStatement iqs) {
      this.insertQueryStatement = iqs;
   }

   public InsertQueryStatement getInsertQueryStatement() {
      return this.insertQueryStatement;
   }

   public void setDropOption(boolean b) {
      this.includeDrop = b;
   }

   public void setDropString(String str) {
      this.dropString = str;
   }

   public void setDatabaseObject(DatabaseObject object) {
      this.databaseObject = object;
   }

   public void setCreate(String createString) {
      this.createString = createString;
   }

   public void setCommentClass(CommentClass commentObject) {
      this.commentObject = commentObject;
   }

   public CommentClass getCommentClass() {
      return this.commentObject;
   }

   public void setCreateOrReplace(String createOrReplace) {
      this.createOrReplace = createOrReplace;
   }

   public void setMaterialized(String materialized) {
      this.materialized = materialized;
   }

   public void setTableOrView(String tableOrView) {
      this.tableOrView = tableOrView;
   }

   public void setAs(String as) {
      this.as = as;
   }

   public void setForce(String force) {
      this.force = force;
   }

   public void setTemp(String temporary) {
      this.temporary = temporary;
   }

   public void setOnCondition(String onCondition) {
      this.onCondition = onCondition;
   }

   public void setHeap(String organisationHeap) {
      this.organisationHeap = organisationHeap;
   }

   public void setDiskAttributes(String diskAttribute) {
      this.diskAttribute = diskAttribute;
   }

   public void setOpenBraces(String openBrace) {
      this.openBrace = openBrace;
   }

   public void setPhysicalCharacteristics(String oraclePhysicalChar) {
      this.oraclePhysicalChar = oraclePhysicalChar;
   }

   public void setClosedBraces(String closedBrace) {
      this.closedBrace = closedBrace;
   }

   public void setTableObject(TableObject tableObject) {
      if (tableObject != null) {
         tableObject.setObjectContext(this.context);
      }

      this.tableObject = tableObject;
   }

   public void setSelectQueryStatement(SelectQueryStatement selectQueryStatement) {
      this.selectQueryStatement = selectQueryStatement;
   }

   public void setWithCheckOption(String withCheckOption) {
      this.withCheckOption = withCheckOption;
   }

   public void setColumnNames(Vector columnNames) {
      this.columnNames = columnNames;
   }

   public void setCreateIndexClause(CreateIndexClause createIndexClause) {
      this.createIndexClause = createIndexClause;
   }

   public void setCreateIndexVector(Vector createIndexVector) {
      this.createIndexVector = createIndexVector;
   }

   public void setCreateIndexClauseBooleanValue(boolean createIndexClauseBooleanValue) {
      this.createIndexClauseBooleanValue = createIndexClauseBooleanValue;
   }

   public void setTypeString(String typeString) {
      this.typeString = typeString;
   }

   public void setTypeIdentifier(String typeIdentifier) {
      this.typeIdentifier = typeIdentifier;
   }

   public void setConstraint(String constraint) {
      this.constraint = constraint;
   }

   public void setConstraintName(String constraintName) {
      this.constraintName = constraintName;
   }

   public void setCreateSequenceStatement(CreateSequenceStatement createSequence) {
      this.createSequence = createSequence;
   }

   public void setCreateSequenceString(String createSequenceStr) {
      this.createSequenceStr = createSequenceStr;
   }

   public void setCreateSynonymStatement(CreateSynonymStatement createSynonym) {
      this.createSynonym = createSynonym;
   }

   public void setCreateSynonymString(String createSynonymStr) {
      this.createSynonymStr = createSynonymStr;
   }

   public void setOnForQuotedIdentifier(String onConditionString) {
      this.onConditionString = onConditionString;
   }

   public void setQuotedIdentifierCondition(String quotedIdentifierCondition) {
      this.quotedIdentifierCondition = quotedIdentifierCondition;
   }

   public void setIndexString(String indexString) {
      this.indexString = indexString;
   }

   public void setPhysicalAttributesVector(Vector physicalAttributesVector) {
      this.physicalAttributesVector = physicalAttributesVector;
   }

   public void setIgnoreOrReplace(String ignoreOrReplace) {
      this.ignoreOrReplace = ignoreOrReplace;
   }

   public void setSelectStatementString(String selectStatementString) {
      this.selectStatementString = selectStatementString;
   }

   public void setDatatypeMapping(DatatypeMapping mapping) {
      this.mapping = mapping;
   }

   public void setWithReadOnly(String withReadOnly) {
      this.withReadOnly = withReadOnly;
   }

   public void setModifiedObject(ModifiedObjectNames modifiedObjects) {
      this.modifiedObjects = modifiedObjects;
   }

   private void setTTUniqueIndicesForUniqCons(ArrayList ttUniqueIndexforUniqueCons) {
      this.ttUniqueIndexforUniqueCons = ttUniqueIndexforUniqueCons;
   }

   public void setLock(String lock) {
      this.lock = lock;
   }

   public void setLockData(String lockData) {
      this.lockData = lockData;
   }

   public void setTriggerForIdentity(String triggerForIdentity) {
      this.triggerForIdentity = triggerForIdentity;
   }

   public void setMysqlCommentTableOption(String mysqlCommentTableOption) {
      this.mysqlCommentTableOption = mysqlCommentTableOption;
   }

   public void setDistributeOnRandomClause(String distOnRandomClause) {
      this.distributeOnRandomClause = distOnRandomClause;
   }

   public void setExternalTable(String externalTable) {
      this.externalTable = externalTable;
   }

   public void setExternalDefaultDirectory(String directory) {
      this.externalDirectory = directory;
   }

   public void setExternalDelimiter(String delimiter) {
      this.externalDelimiter = delimiter;
   }

   public void setComputedColumnView(CreateQueryStatement computedColView) {
      this.computedColView = computedColView;
   }

   public void setStartWith(String startWith) {
      this.startWith = startWith;
   }

   public void setStartWithFunction(FunctionCalls fc) {
      this.startWithFunction = fc;
   }

   public void setNextString(String next) {
      this.nextString = next;
   }

   public void setSelectColumnInNextClause(SelectColumn sc) {
      this.selectColumnInNextClause = sc;
   }

   public void setViewMetaAttribute(String viewMetaAttribute) {
      this.viewMetaAttribute = viewMetaAttribute;
   }

   public void setWithStatement(WithStatement withStmt) {
      this.withStatement = withStmt;
   }

   public DatabaseObject getDatabaseObject() {
      return this.databaseObject;
   }

   public String getCreate() {
      return this.createString;
   }

   public String getCreateOrReplace() {
      return this.createOrReplace;
   }

   public String getTableOrView() {
      return this.tableOrView;
   }

   public String getAs() {
      return this.as;
   }

   public String getForce() {
      return this.force;
   }

   public String getTemp() {
      return this.temporary;
   }

   public String getOnCondition() {
      return this.onCondition;
   }

   public String getHeap() {
      return this.organisationHeap;
   }

   public String getDiskAttributes() {
      return this.diskAttribute;
   }

   public String getPhysicalCharacteristics() {
      return this.oraclePhysicalChar;
   }

   public TableObject getTableObject() {
      return this.tableObject;
   }

   public SelectQueryStatement getSelectQueryStatement() {
      return this.selectQueryStatement;
   }

   public String getWithCheckOption() {
      return this.withCheckOption;
   }

   public Vector getColumnNames() {
      return this.columnNames;
   }

   public String getConstraint() {
      return this.constraint;
   }

   public String getConstraintName() {
      return this.constraintName;
   }

   public CreateIndexClause getCreateIndexClause() {
      return this.createIndexClause;
   }

   public Vector getCreateIndexVector() {
      return this.createIndexVector;
   }

   public boolean getCreateIndexClauseBooleanValue() {
      return this.createIndexClauseBooleanValue;
   }

   public String getTypeString() {
      return this.typeString;
   }

   public String getTypeIdentifier() {
      return this.typeIdentifier;
   }

   public CreateSequenceStatement getCreateSequence() {
      return this.createSequence;
   }

   public String getCreateSequenceString() {
      return this.createSequenceStr;
   }

   public CreateSynonymStatement getCreateSynonym() {
      return this.createSynonym;
   }

   public String getCreateSynonymString() {
      return this.createSynonymStr;
   }

   public Vector getPhysicalAttributesVector() {
      return this.physicalAttributesVector;
   }

   public String getIgnoreOrReplace() {
      return this.ignoreOrReplace;
   }

   public String getSelectStatementString() {
      return this.selectStatementString;
   }

   public String getWithReadOnly() {
      return this.withReadOnly;
   }

   public ModifiedObjectNames getModifiedObject() {
      return this.modifiedObjects;
   }

   public String getLock() {
      return this.lock;
   }

   public String getLockData() {
      return this.lockData;
   }

   public String getDistributeOnRandomClause() {
      return this.distributeOnRandomClause;
   }

   public String getExternalTable() {
      return this.externalTable;
   }

   public String getExternalDefaultDirectory() {
      return this.externalDirectory;
   }

   public String getExternalDelimiter() {
      return this.externalDelimiter;
   }

   public String getMaterialized() {
      return this.materialized;
   }

   public String getStartWith() {
      return this.startWith;
   }

   public FunctionCalls getStartWithFunction() {
      return this.startWithFunction;
   }

   public String getNextString() {
      return this.nextString;
   }

   public SelectColumn getSelectColumnInNextClause() {
      return this.selectColumnInNextClause;
   }

   public String getViewMetaAttribute() {
      return this.viewMetaAttribute;
   }

   public WithStatement getWithStatement() {
      return this.withStatement;
   }

   public String removeIndent(String str) {
      str = str.replace('\n', ' ');
      str = str.replace('\t', ' ');
      return str;
   }

   public void computedColumns(CreateQueryStatement cqs) {
      CreateQueryStatement createStmt = new CreateQueryStatement();
      SelectQueryStatement selectQuery = new SelectQueryStatement();
      SelectStatement selectStmt = new SelectStatement();
      SelectColumn selectColumn = new SelectColumn();
      TableObject viewTableObject = new TableObject();
      new CreateColumn();
      selectColumn.setColumnExpression(cqs.getColumnNames());
      if (this.computedColumnsVector.size() > 0) {
         createStmt.setCreate("CREATE");
         createStmt.setTableOrView("VIEW");
         String viewName = cqs.getTableObject() + "_" + "VIEW";
         viewTableObject.setTableName(viewName);
         createStmt.setAs("AS");
         createStmt.setTableObject(viewTableObject);
         selectStmt.setSelectClause("select");
         Vector viewSelectColumnList = new Vector();

         int k;
         CreateColumn computedCreateColumn;
         SelectColumn computedSelectColumn;
         for(k = 0; k < cqs.getColumnNames().size(); ++k) {
            computedCreateColumn = (CreateColumn)cqs.getColumnNames().get(k);
            computedSelectColumn = new SelectColumn();
            TableColumn computedTableColumn = new TableColumn();
            Vector computedSelectColumnExpression = new Vector();
            String computedSelectColumnName = computedCreateColumn.getColumnName();
            computedTableColumn.setColumnName(computedSelectColumnName);
            computedSelectColumnExpression.add(computedTableColumn);
            if (computedSelectColumnExpression.size() != 0) {
               computedSelectColumn.setEndsWith(",");
            }

            computedSelectColumn.setColumnExpression(computedSelectColumnExpression);
            viewSelectColumnList.add(computedSelectColumn);
         }

         for(k = 0; k < this.computedColumnsVector.size(); ++k) {
            computedCreateColumn = (CreateColumn)this.computedColumnsVector.get(k);
            computedSelectColumn = computedCreateColumn.getComputedColumnExpression();
            computedSelectColumn.setIsAS("as");
            computedSelectColumn.setAliasName(computedCreateColumn.getColumnName());
            if (this.computedColumnsVector.size() != k + 1) {
               computedSelectColumn.setEndsWith(",");
            }

            viewSelectColumnList.add(computedSelectColumn);
         }

         selectStmt.setSelectItemList(viewSelectColumnList);
         selectQuery.setSelectStatement(selectStmt);
         FromClause fromClauseObj = new FromClause();
         FromTable fromTableObj = new FromTable();
         fromTableObj.setTableName(this.getTableObject().getTableName());
         Vector fromItemList = new Vector();
         fromItemList.add(fromTableObj);
         fromClauseObj.setFromItemList(fromItemList);
         fromClauseObj.setFromClause("FROM");
         selectQuery.setFromClause(fromClauseObj);
      }

      createStmt.setSelectQueryStatement(selectQuery);
      this.computedColView = createStmt;
      cqs.setComputedColumnView(this.computedColView);
   }

   public String toInformixString() throws ConvertException {
      return this.toInformixCreate().toString();
   }

   public CreateQueryStatement toInformixCreate() throws ConvertException {
      commentWhenConstraintNameTruncated = "";
      CreateQueryStatement cqs = this.copyObjectValues();
      cqs.setCreateOrReplace((String)null);
      cqs.setOnForQuotedIdentifier((String)null);
      cqs.setQuotedIdentifierCondition((String)null);
      cqs.setMaterialized((String)null);
      cqs.setWithReadOnly((String)null);
      cqs.setLock((String)null);
      cqs.setLockData((String)null);
      cqs.setExternalTable((String)null);
      cqs.setExternalDefaultDirectory((String)null);
      cqs.setExternalDelimiter((String)null);
      cqs.setViewMetaAttribute((String)null);
      Vector temp_Vector;
      int i;
      if (cqs.getColumnNames() != null) {
         temp_Vector = cqs.getColumnNames();

         for(i = 0; i < temp_Vector.size(); ++i) {
            if (temp_Vector.get(i) instanceof CreateColumn) {
               CreateColumn changeCreateColumn = (CreateColumn)temp_Vector.get(i);
               if (cqs.getTableObject() != null) {
                  changeCreateColumn.setTableNameFromCreateQueryStmt(cqs.getTableObject().getTableName());
                  changeCreateColumn.setDatatypeMapping(this.mapping);
               }

               changeCreateColumn.toInformixString();
            }
         }
      }

      String createSynonymObj;
      if (cqs.getTableObject() != null) {
         TableObject cqsTableObject = cqs.getTableObject();
         createSynonymObj = cqsTableObject.getOwner();
         String userName = cqsTableObject.getUser();
         String tableName = cqsTableObject.getTableName();
         if (createSynonymObj != null && (createSynonymObj.startsWith("[") && createSynonymObj.endsWith("]") || createSynonymObj.startsWith("`") && createSynonymObj.endsWith("`"))) {
            createSynonymObj = createSynonymObj.substring(1, createSynonymObj.length() - 1);
            if (createSynonymObj.indexOf(32) != -1) {
               createSynonymObj = "\"" + createSynonymObj + "\"";
            }
         }

         if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
            userName = userName.substring(1, userName.length() - 1);
            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }
         }

         if (tableName != null && (tableName.startsWith("[") && tableName.endsWith("]") || tableName.startsWith("`") && tableName.endsWith("`"))) {
            tableName = tableName.substring(1, tableName.length() - 1);
            if (tableName.indexOf(32) != -1) {
               tableName = "\"" + tableName + "\"";
            }
         }

         cqsTableObject.setOwner(createSynonymObj);
         cqsTableObject.setUser(userName);
         cqsTableObject.setTableName(tableName);
         cqsTableObject.toInformix();
      }

      cqs.setTemp((String)null);
      cqs.setForce((String)null);
      cqs.setHeap((String)null);
      cqs.setPhysicalCharacteristics((String)null);
      cqs.setDiskAttributes((String)null);
      cqs.setOnCondition((String)null);
      cqs.setConstraint((String)null);
      cqs.setConstraintName((String)null);
      if (this.getSelectQueryStatement() != null) {
         cqs.setSelectQueryStatement(this.selectQueryStatement.toInformix());
      }

      if (this.getWithStatement() != null) {
         cqs.setWithStatement((WithStatement)this.withStatement.toInformix());
      }

      if (cqs.getCreateIndexClause() != null) {
         CreateIndexClause createIndexClauseObject = cqs.getCreateIndexClause();
         cqs.setCreateIndexClause(createIndexClauseObject.toInformix());
      }

      if (cqs.getCreateIndexVector() != null) {
         temp_Vector = new Vector();

         for(i = 0; i < cqs.getCreateIndexVector().size(); ++i) {
            CreateIndexClause createIndexClauseObject = (CreateIndexClause)cqs.getCreateIndexVector().get(i);
            CreateIndexClause informixIndexClause = createIndexClauseObject.toInformix();
            temp_Vector.add(informixIndexClause);
         }

         cqs.setCreateIndexVector(temp_Vector);
      }

      cqs.setTypeIdentifier((String)null);
      cqs.setTypeString((String)null);
      if (cqs.getCreateSequence() != null) {
         CreateSequenceStatement tempCreateSequence = cqs.getCreateSequence();
         CreateSequenceStatement createSequenceObj = tempCreateSequence.toInformix();
         cqs.setCreateSequenceStatement(createSequenceObj);
      }

      if (cqs.getCreateSynonym() != null) {
         CreateSynonymStatement tempCreateSynonym = cqs.getCreateSynonym();
         tempCreateSynonym.setCreate("CREATE");
         if (this.createOrReplace != null) {
            tempCreateSynonym.setCreateOrReplace(this.createOrReplace);
         }

         createSynonymObj = tempCreateSynonym.toInformixString();
         cqs.setCreateSynonymString(createSynonymObj);
      }

      cqs.setPhysicalAttributesVector((Vector)null);
      cqs.setIgnoreOrReplace((String)null);
      cqs.setSelectStatementString((String)null);
      return cqs;
   }

   public String toANSIString() throws ConvertException {
      return this.toANSICreate().toString();
   }

   public CreateQueryStatement toANSICreate() throws ConvertException {
      commentWhenConstraintNameTruncated = "";
      CreateQueryStatement cqs = this.copyObjectValues();
      cqs.setCreateOrReplace((String)null);
      cqs.setOnForQuotedIdentifier((String)null);
      cqs.setQuotedIdentifierCondition((String)null);
      cqs.setMaterialized((String)null);
      cqs.setViewMetaAttribute((String)null);
      cqs.setWithReadOnly((String)null);
      cqs.setLock((String)null);
      cqs.setLockData((String)null);
      cqs.setExternalTable((String)null);
      cqs.setExternalDefaultDirectory((String)null);
      cqs.setExternalDelimiter((String)null);
      if (cqs.getColumnNames() != null) {
         Vector columnNamesVector = cqs.getColumnNames();

         for(int i = 0; i < columnNamesVector.size(); ++i) {
            if (columnNamesVector.get(i) instanceof CreateColumn) {
               CreateColumn changeCreateColumn = (CreateColumn)columnNamesVector.get(i);
               if (cqs.getTableObject() != null) {
                  changeCreateColumn.setTableNameFromCreateQueryStmt(cqs.getTableObject().getTableName());
                  changeCreateColumn.setDatatypeMapping(this.mapping);
               }

               changeCreateColumn.toANSIString();
               if (changeCreateColumn.getComputedColumnExpression() != null) {
                  this.computedColumnsVector.add(columnNamesVector.get(i));
                  columnNamesVector.removeElementAt(i);
                  --i;
               }
            }
         }
      }

      String indexString;
      if (cqs.getTemp() != null) {
         indexString = cqs.getTemp();
         if (indexString.equalsIgnoreCase("TEMP") || indexString.equalsIgnoreCase("TEMPORARY")) {
            cqs.setTemp("LOCAL TEMPORARY");
         }
      }

      String createSynonymObj;
      if (cqs.getTableObject() != null) {
         TableObject cqsTableObject = cqs.getTableObject();
         createSynonymObj = cqsTableObject.getOwner();
         String userName = cqsTableObject.getUser();
         String tableName = cqsTableObject.getTableName();
         if (createSynonymObj != null && (createSynonymObj.startsWith("[") && createSynonymObj.endsWith("]") || createSynonymObj.startsWith("`") && createSynonymObj.endsWith("`"))) {
            createSynonymObj = createSynonymObj.substring(1, createSynonymObj.length() - 1);
            if (createSynonymObj.indexOf(32) != -1) {
               createSynonymObj = "\"" + createSynonymObj + "\"";
            }
         }

         if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
            userName = userName.substring(1, userName.length() - 1);
            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }
         }

         if (tableName != null && (tableName.startsWith("[") && tableName.endsWith("]") || tableName.startsWith("`") && tableName.endsWith("`"))) {
            tableName = tableName.substring(1, tableName.length() - 1);
            if (tableName.indexOf(32) != -1) {
               tableName = "\"" + tableName + "\"";
            }
         }

         cqsTableObject.setOwner(createSynonymObj);
         cqsTableObject.setUser(userName);
         cqsTableObject.setTableName(tableName);
         cqsTableObject.toANSISQL();
      }

      if (cqs.getOnCondition() != null) {
         cqs.onConditionForANSI = cqs.getOnCondition();
         cqs.setOnCondition((String)null);
      }

      cqs.setForce((String)null);
      cqs.setHeap((String)null);
      cqs.setDiskAttributes((String)null);
      cqs.setPhysicalCharacteristics((String)null);
      cqs.setConstraint((String)null);
      cqs.setConstraintName((String)null);
      if (this.getSelectQueryStatement() != null) {
         cqs.setSelectQueryStatement(this.selectQueryStatement.toANSI());
      }

      if (this.getWithStatement() != null) {
         cqs.setWithStatement((WithStatement)this.withStatement.toANSISQL());
      }

      if (cqs.getCreateIndexClause() != null) {
         CreateIndexClause createIndexClauseObject = cqs.getCreateIndexClause();
         cqs.setCreateIndexClause(createIndexClauseObject.toANSI());
      }

      if (cqs.getCreateIndexVector() != null) {
         indexString = "";
         new Vector();

         for(int i = 0; i < cqs.getCreateIndexVector().size(); ++i) {
            CreateIndexClause createIndexClauseObject = (CreateIndexClause)cqs.getCreateIndexVector().get(i);
            if (!this.existsPKasIndex(createIndexClauseObject, cqs)) {
               createIndexClauseObject.setIndexOrKey("CREATE INDEX");
               createIndexClauseObject.setOn("ON");
               createIndexClauseObject.setTableOrView(this.tableObject);
               CreateIndexClause ansiIndexClause = createIndexClauseObject.toANSI();
               indexString = indexString + ";\n/* SwisSQL Message : Query split into multiple Queries.*/\n" + ansiIndexClause.toString().trim();
            }
         }

         cqs.setIndexString(indexString);
         cqs.setCreateIndexVector((Vector)null);
      }

      cqs.setTypeIdentifier((String)null);
      cqs.setTypeString((String)null);
      if (cqs.getCreateSequence() != null) {
         CreateSequenceStatement tempCreateSequence = cqs.getCreateSequence();
         CreateSequenceStatement createSequenceObj = tempCreateSequence.toANSI();
         cqs.setCreateSequenceStatement(createSequenceObj);
      }

      if (cqs.getCreateSynonym() != null) {
         CreateSynonymStatement tempCreateSynonym = cqs.getCreateSynonym();
         tempCreateSynonym.setCreate("CREATE");
         if (this.createOrReplace != null) {
            tempCreateSynonym.setCreateOrReplace(this.createOrReplace);
         }

         createSynonymObj = tempCreateSynonym.toANSIString();
         cqs.setCreateSynonymString(createSynonymObj);
      }

      cqs.setPhysicalAttributesVector((Vector)null);
      cqs.setIgnoreOrReplace((String)null);
      cqs.setSelectStatementString((String)null);
      if (this.computedColumnsVector.size() >= 1) {
         this.computedColumns(cqs);
      }

      return cqs;
   }

   public String toDB2String() throws ConvertException {
      return this.toDB2Create().toString();
   }

   public CreateQueryStatement toDB2Create() throws ConvertException {
      if (SwisSQLAPI.truncateTableCount > 99) {
         SwisSQLAPI.truncateTableCount = 0;
      }

      if (SwisSQLAPI.truncateIndexCount > 99) {
         SwisSQLAPI.truncateIndexCount = 0;
      }

      commentWhenConstraintNameTruncated = "";
      CreateQueryStatement cqs = this.copyObjectValues();
      cqs.setCreateOrReplace((String)null);
      cqs.setOnForQuotedIdentifier((String)null);
      cqs.setQuotedIdentifierCondition((String)null);
      cqs.setMaterialized((String)null);
      cqs.setViewMetaAttribute((String)null);
      cqs.setWithReadOnly((String)null);
      cqs.setLock((String)null);
      cqs.setLockData((String)null);
      cqs.setExternalTable((String)null);
      cqs.setExternalDefaultDirectory((String)null);
      cqs.setExternalDelimiter((String)null);
      String tableName;
      if (cqs.getColumnNames() != null) {
         Vector columnNamesVector = cqs.getColumnNames();
         Vector storeAllColumnNamesVector = new Vector();
         TableObject to = cqs.getTableObject();
         if (to != null) {
            tableName = to.getTableName().toLowerCase().trim();
            if (SwisSQLOptions.addRowidColumnForAllDB2Tables && tableName != null) {
               try {
                  FileOutputStream fos = new FileOutputStream("conf/TablesHavingRowIdColumns.conf", true);
                  OutputStreamWriter osw = new OutputStreamWriter(fos);
                  PrintWriter pw = new PrintWriter(osw);
                  pw.println(tableName);
                  CreateColumn cc = new CreateColumn();
                  cc.setColumnName("RowId");
                  CharacterClass charClass = new CharacterClass();
                  charClass.setDatatypeName("VARCHAR");
                  charClass.setOpenBrace("(");
                  charClass.setClosedBrace(")");
                  charClass.setSize("13");
                  charClass.setBinary("FOR BIT DATA");
                  cc.setDatatype(charClass);
                  columnNamesVector.add(0, cc);
                  if (!InsertQueryStatement.tablesWithRowIDColumnsList.contains(tableName)) {
                     InsertQueryStatement.tablesWithRowIDColumnsList.add(tableName);
                  }

                  pw.close();
                  osw.close();
                  fos.close();
               } catch (Exception var17) {
               }
            }
         }

         for(int i = 0; i < columnNamesVector.size(); ++i) {
            if (columnNamesVector.get(i) instanceof CreateColumn) {
               CreateColumn changeCreateColumn = (CreateColumn)columnNamesVector.get(i);
               if (cqs.getTableObject() != null) {
                  changeCreateColumn.setTableNameFromCreateQueryStmt(cqs.getTableObject().getTableName());
                  changeCreateColumn.setDatatypeMapping(this.mapping);
               }

               storeAllColumnNamesVector.add(changeCreateColumn.getColumnName());
               Vector constraintVector = changeCreateColumn.getConstraintClause();
               boolean isTableLevelConstraintRemoved = false;
               isTableLevelConstraintRemoved = this.removeCheckConstrWithDetFunctions(constraintVector, changeCreateColumn, columnNamesVector, i);
               if (changeCreateColumn.getColumnName() == null && constraintVector != null) {
                  ConstraintClause db2ConstraintClause = (ConstraintClause)constraintVector.get(0);
                  ConstraintType db2ConstraintType = db2ConstraintClause.getConstraintType();
                  if (db2ConstraintType != null) {
                     if (db2ConstraintType instanceof PrimaryOrUniqueConstraintClause) {
                        PrimaryOrUniqueConstraintClause db2PrimaryOrUniqueConstraintClause = (PrimaryOrUniqueConstraintClause)db2ConstraintType;
                        if (db2PrimaryOrUniqueConstraintClause.getConstraintColumnNames() != null) {
                           Vector constraintColumnNamesVector = db2PrimaryOrUniqueConstraintClause.getConstraintColumnNames();

                           for(int i_count = 0; i_count < constraintColumnNamesVector.size(); ++i_count) {
                              String constraintColumnName = (String)db2PrimaryOrUniqueConstraintClause.getConstraintColumnNames().get(i_count);
                              if (storeAllColumnNamesVector.contains(constraintColumnName) || storeAllColumnNamesVector.contains("[" + constraintColumnName + "]") || storeAllColumnNamesVector.contains("`" + constraintColumnName + "`")) {
                                 int indexOfColumnName = storeAllColumnNamesVector.indexOf(constraintColumnName);
                                 if (indexOfColumnName == -1) {
                                    indexOfColumnName = storeAllColumnNamesVector.indexOf("[" + constraintColumnName + "]");
                                 }

                                 if (indexOfColumnName == -1) {
                                    indexOfColumnName = storeAllColumnNamesVector.indexOf("`" + constraintColumnName + "`");
                                 }

                                 CreateColumn createColumnWithConstraint = (CreateColumn)columnNamesVector.get(indexOfColumnName);
                                 createColumnWithConstraint.setNullStatus("NOT NULL");
                                 changeCreateColumn.setNotNullSetFromCreateQueryStatement(true);
                                 changeCreateColumn.toDB2String();
                              }
                           }
                        }
                     } else {
                        changeCreateColumn.toDB2String();
                     }
                  }
               } else {
                  changeCreateColumn.toDB2String();
               }

               if (isTableLevelConstraintRemoved) {
                  --i;
               }
            }
         }
      }

      String createSynonymObj;
      if (cqs.getTableObject() != null) {
         TableObject cqsTableObject = cqs.getTableObject();
         createSynonymObj = cqsTableObject.getOwner();
         String userName = cqsTableObject.getUser();
         tableName = cqsTableObject.getTableName();
         if (createSynonymObj != null && (createSynonymObj.startsWith("[") && createSynonymObj.endsWith("]") || createSynonymObj.startsWith("`") && createSynonymObj.endsWith("`"))) {
            createSynonymObj = createSynonymObj.substring(1, createSynonymObj.length() - 1);
            if (createSynonymObj.indexOf(32) != -1) {
               createSynonymObj = "\"" + createSynonymObj + "\"";
            }
         }

         if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
            userName = userName.substring(1, userName.length() - 1);
            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }
         }

         if (tableName != null && (tableName.startsWith("[") && tableName.endsWith("]") || tableName.startsWith("`") && tableName.endsWith("`"))) {
            tableName = tableName.substring(1, tableName.length() - 1);
            if (tableName.indexOf(32) != -1) {
               tableName = "\"" + tableName + "\"";
            }
         }

         cqsTableObject.setOwner(createSynonymObj);
         cqsTableObject.setUser(userName);
         cqsTableObject.setTableName(tableName);
         cqsTableObject.toDB2();
      }

      cqs.setTemp((String)null);
      cqs.setForce((String)null);
      cqs.setHeap((String)null);
      cqs.setPhysicalCharacteristics((String)null);
      cqs.setDiskAttributes((String)null);
      cqs.setOnCondition((String)null);
      cqs.setConstraint((String)null);
      cqs.setConstraintName((String)null);
      if (this.getSelectQueryStatement() != null) {
         SelectQueryStatement db2_sqs = this.selectQueryStatement.toDB2();
         if (this.tableOrView.trim().equalsIgnoreCase("VIEW")) {
            FromClause fc = db2_sqs.getFromClause();
            fc.setFetchClauseFromSQS((FetchClause)null);
            if (db2_sqs.getSetOperatorClause() != null) {
               SetOperatorClause soc = db2_sqs.getSetOperatorClause();
               SelectQueryStatement subSQS = soc.getSelectQueryStatement();
               this.removeFetchClause(subSQS);
               this.checkForColumnNames(db2_sqs, subSQS);
            }
         }

         cqs.setSelectQueryStatement(db2_sqs);
      }

      if (this.getWithStatement() != null) {
         cqs.setWithStatement((WithStatement)this.withStatement.toDB2());
      }

      if (cqs.getCreateIndexClause() != null) {
         CreateIndexClause createIndexClauseObject = cqs.getCreateIndexClause();
         cqs.setCreateIndexClause(createIndexClauseObject.toDB2());
      }

      if (cqs.getCreateIndexVector() != null) {
         String indexString = "";
         new Vector();

         for(int i = 0; i < cqs.getCreateIndexVector().size(); ++i) {
            CreateIndexClause createIndexClauseObject = (CreateIndexClause)cqs.getCreateIndexVector().get(i);
            if (!this.existsPKasIndex(createIndexClauseObject, cqs)) {
               createIndexClauseObject.setIndexOrKey("CREATE INDEX");
               createIndexClauseObject.setOn("ON");
               createIndexClauseObject.setTableOrView(this.tableObject);
               CreateIndexClause ansiIndexClause = createIndexClauseObject.toDB2();
               indexString = indexString + "\n@\n/* SwisSQL Message : Query split into multiple Queries.*/\n" + ansiIndexClause.toString().trim();
            }
         }

         cqs.setIndexString(indexString);
         cqs.setCreateIndexVector((Vector)null);
      }

      cqs.setTypeIdentifier((String)null);
      cqs.setTypeString((String)null);
      if (cqs.getCreateSequence() != null) {
         CreateSequenceStatement tempCreateSequence = cqs.getCreateSequence();
         CreateSequenceStatement createSequenceObj = tempCreateSequence.toDB2();
         cqs.setCreateSequenceStatement(createSequenceObj);
      }

      if (cqs.getCreateSynonym() != null) {
         CreateSynonymStatement tempCreateSynonym = cqs.getCreateSynonym();
         tempCreateSynonym.setCreate("CREATE");
         if (this.createOrReplace != null) {
            tempCreateSynonym.setCreateOrReplace(this.createOrReplace);
         }

         createSynonymObj = tempCreateSynonym.toDB2String();
         cqs.setCreateSynonymString(createSynonymObj);
      }

      cqs.setPhysicalAttributesVector((Vector)null);
      cqs.setIgnoreOrReplace((String)null);
      cqs.setSelectStatementString((String)null);
      return cqs;
   }

   private void removeFetchClause(SelectQueryStatement sqs) {
      if (sqs != null) {
         SetOperatorClause soc = sqs.getSetOperatorClause();
         if (soc != null) {
            SelectQueryStatement subSQS = soc.getSelectQueryStatement();
            this.removeFetchClause(subSQS);
         }

         FromClause fc = sqs.getFromClause();
         fc.setFetchClauseFromSQS((FetchClause)null);
      }

   }

   private void checkForColumnNames(SelectQueryStatement fullSQS, SelectQueryStatement subSQS) {
      if (subSQS != null) {
         SelectStatement fullSS = fullSQS.getSelectStatement();
         SelectStatement subSS = subSQS.getSelectStatement();
         Vector fullSItem = fullSS.getSelectItemList();
         Vector subSItem = subSS.getSelectItemList();

         for(int i = 0; i < fullSItem.size(); ++i) {
            Object fullObj = fullSItem.get(i);
            Object subObj = subSItem.get(i);
            if (fullObj instanceof SelectColumn && subObj instanceof SelectColumn) {
               SelectColumn fullSC = (SelectColumn)fullObj;
               SelectColumn subSC = (SelectColumn)subObj;
               Vector fullColExpr = fullSC.getColumnExpression();
               Vector subColExpr = subSC.getColumnExpression();
               String fullAlias = fullSC.getAliasName();
               String subAlias = subSC.getAliasName();
               if (fullAlias != null && subAlias == null) {
                  subSC.setAliasName(fullAlias);
               } else if (fullAlias == null && subAlias != null) {
                  fullSC.setAliasName(subAlias);
               } else if (fullAlias != null && subAlias != null) {
                  if (!fullAlias.equalsIgnoreCase(subAlias)) {
                     subSC.setAliasName(fullAlias);
                  }
               } else if (fullColExpr.size() == 1 && subColExpr.size() == 1) {
                  Object fullObj1 = fullColExpr.get(0);
                  Object subObj1 = subColExpr.get(0);
                  TableColumn fullTC = null;
                  TableColumn subTC = null;
                  if (fullObj1 instanceof TableColumn && subObj1 instanceof TableColumn) {
                     fullTC = (TableColumn)fullObj1;
                     subTC = (TableColumn)subObj1;
                     if (!fullTC.getColumnName().equalsIgnoreCase(subTC.getColumnName())) {
                        fullSC.setAliasName(fullTC.getColumnName());
                        subSC.setAliasName(fullTC.getColumnName());
                     }
                  } else if (fullObj1 instanceof TableColumn) {
                     fullTC = (TableColumn)fullObj1;
                     subSC.setAliasName(fullTC.getColumnName());
                  } else if (subObj1 instanceof TableColumn) {
                     subTC = (TableColumn)subObj1;
                     fullSC.setAliasName(subTC.getColumnName());
                  }
               }
            }
         }
      }

   }

   private CheckConstraintClause isCheckConstraint(ConstraintClause cclause) {
      ConstraintType ctype = cclause.getConstraintType();
      return ctype != null && ctype instanceof CheckConstraintClause ? (CheckConstraintClause)ctype : null;
   }

   private String getCheckConstraintClauseName(Vector v) {
      for(int i = 0; i < v.size(); ++i) {
         ConstraintClause cclause = (ConstraintClause)v.elementAt(i);
         CheckConstraintClause ccc = this.isCheckConstraint(cclause);
         if (ccc != null) {
            WhereExpression we = ccc.getWhereExpression();
            Vector witemV = we.getWhereItems();
            if (witemV != null) {
               return this.leftWhereColumn(witemV.get(0));
            }
         }
      }

      return "";
   }

   private String leftWhereColumn(Object obj) {
      if (obj instanceof WhereExpression) {
         WhereExpression we = (WhereExpression)obj;
         Vector wis = we.getWhereItems();
         if (wis != null) {
            return this.leftWhereColumn(wis.get(0));
         }
      } else if (obj instanceof WhereItem) {
         WhereItem wi = (WhereItem)obj;
         return wi.getLeftWhereExp().toString();
      }

      return "";
   }

   private boolean containsInVector(String str, Vector v) {
      if (str != null && !str.equalsIgnoreCase("") && v != null && !v.isEmpty()) {
         str = str.trim();
         if (str.startsWith("\"") || str.startsWith("'")) {
            str = str.substring(1, str.length() - 1);
         }

         for(int i = 0; i < v.size(); ++i) {
            String s = (String)v.elementAt(i);
            s = s.trim();
            if (s.equalsIgnoreCase(str)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public void setDeltekIdentityString(String tab_col, CreateColumn changeCreateColumn) {
      String identity = SwisSQLAPI.identityMapping.get(tab_col).toString();
      if (identity != null && changeCreateColumn != null) {
         changeCreateColumn.setIdentity(identity);
      }

   }

   public String toMSSQLServerString() throws ConvertException {
      return this.toMSSQLServerCreate().toString();
   }

   public CreateQueryStatement toMSSQLServerCreate() throws ConvertException {
      CreateQueryStatement cqs = this.copyObjectValues();
      commentWhenConstraintNameTruncated = "";
      cqs.setForce((String)null);
      cqs.setDropOption(this.includeDrop);
      cqs.setDropString(this.dropString);
      cqs.setPhysicalCharacteristics((String)null);
      cqs.setCreateOrReplace((String)null);
      cqs.setConstraint((String)null);
      cqs.setConstraintName((String)null);
      cqs.setViewMetaAttribute(this.viewMetaAttribute);
      cqs.setWithReadOnly((String)null);
      cqs.setLock((String)null);
      cqs.setLockData((String)null);
      cqs.setExternalTable((String)null);
      cqs.setExternalDefaultDirectory((String)null);
      cqs.setExternalDelimiter((String)null);
      if (cqs.getOnCondition() != null && !cqs.getOnCondition().startsWith("ON DEFAULT")) {
         cqs.setOnCondition((String)null);
      }

      if (cqs.materialized != null) {
         cqs.withSchemaBinding = "WITH SCHEMABINDING";
      }

      int i;
      Vector selectItems;
      int i;
      Object obj;
      int i;
      Vector fromItems;
      if (cqs.getColumnNames() != null) {
         Vector columnNamesVector = cqs.getColumnNames();
         Vector vec = new Vector();

         CreateColumn changeCreateColumn;
         for(i = 0; i < columnNamesVector.size(); ++i) {
            if (columnNamesVector.get(i) instanceof CreateColumn) {
               changeCreateColumn = (CreateColumn)columnNamesVector.get(i);
               String current_col_name;
               if (changeCreateColumn != null && changeCreateColumn.getColumnName() != null) {
                  current_col_name = this.tableObject.getTableName().trim() + "." + changeCreateColumn.getColumnName().trim();
                  if (SwisSQLAPI.identityMapping.containsKey(current_col_name)) {
                     this.setDeltekIdentityString(current_col_name, changeCreateColumn);
                  }
               }

               if (cqs.getTableObject() != null) {
                  changeCreateColumn.setTableNameFromCreateQueryStmt(cqs.getTableObject().getTableName());
                  DateClass datatype;
                  if (SwisSQLOptions.changeDatatype_For_Deltek && changeCreateColumn.getColumnName().toUpperCase().trim().endsWith("_DT") && changeCreateColumn.getDatatype() instanceof DateClass) {
                     datatype = (DateClass)changeCreateColumn.getDatatype();
                     if (datatype.getDatatypeName().trim().equalsIgnoreCase("DATE")) {
                        datatype.setDatatypeName("SMALLDATETIME");
                     }
                  } else if (SwisSQLOptions.changeDatatype_For_Deltek && changeCreateColumn.getColumnName().toUpperCase().trim().endsWith("_DTT") && changeCreateColumn.getDatatype() instanceof DateClass) {
                     datatype = (DateClass)changeCreateColumn.getDatatype();
                     if (datatype.getDatatypeName().trim().equalsIgnoreCase("DATE")) {
                        datatype.setDatatypeName("DATETIME");
                     }
                  }

                  changeCreateColumn.setDatatypeMapping(this.mapping);
               }

               current_col_name = changeCreateColumn.getColumnName();
               if (changeCreateColumn.getDatatype() != null && (changeCreateColumn.getDatatype().toString().trim().equalsIgnoreCase("CLOB") || changeCreateColumn.getDatatype().toString().trim().equalsIgnoreCase("BLOB"))) {
                  vec.addElement(current_col_name);
                  selectItems = changeCreateColumn.getConstraintClause();
                  if (selectItems != null) {
                     for(i = 0; i < selectItems.size(); ++i) {
                        obj = selectItems.elementAt(i);
                        if (obj instanceof ConstraintClause && this.isCheckConstraint((ConstraintClause)obj) != null) {
                           selectItems.removeElementAt(i);
                        }
                     }
                  }
               }

               if (SwisSQLOptions.EnableDeltekSpecificConversions && changeCreateColumn.getColumnName() != null && changeCreateColumn.getColumnName().trim().equalsIgnoreCase("ID_COL")) {
                  changeCreateColumn.setIdentity("IDENTITY");
                  if (changeCreateColumn.getDatatype() != null && changeCreateColumn.getDatatype().getDatatypeName().trim().equalsIgnoreCase("DECIMAL")) {
                     changeCreateColumn.getDatatype().setDatatypeName("NUMERIC");
                  }
               }

               changeCreateColumn.toMSSQLServerString();
            }
         }

         for(i = 0; i < columnNamesVector.size(); ++i) {
            changeCreateColumn = (CreateColumn)columnNamesVector.get(i);
            if (changeCreateColumn != null && changeCreateColumn.getColumnName() == null) {
               Vector constraintVector = changeCreateColumn.getConstraintClause();
               String current_check_clause_name = this.getCheckConstraintClauseName(constraintVector);
               if (constraintVector != null && this.containsInVector(current_check_clause_name, vec)) {
                  columnNamesVector.remove(changeCreateColumn);
                  --i;
               }
            }
         }

         if (cqs.getDiskAttributes() != null) {
            fromItems = cqs.getColumnNames();

            for(i = 0; i < columnNamesVector.size(); ++i) {
               if (columnNamesVector.get(i) instanceof CreateColumn) {
                  CreateColumn changeCreateColumn = (CreateColumn)columnNamesVector.get(i);
                  selectItems = changeCreateColumn.getConstraintClause();
                  if (selectItems != null) {
                     for(i = 0; i < selectItems.size(); ++i) {
                        ConstraintClause toSQLServerConstraintClause = (ConstraintClause)selectItems.get(i);
                        ConstraintType toSQLServerConstraintType = toSQLServerConstraintClause.getConstraintType();
                        if (toSQLServerConstraintType instanceof PrimaryOrUniqueConstraintClause) {
                           PrimaryOrUniqueConstraintClause pcc = (PrimaryOrUniqueConstraintClause)toSQLServerConstraintType;
                           changeCreateColumn.setNotNullSetFromCreateQueryStatement(true);
                           String fillfactor = cqs.getDiskAttributes();
                           String tempFillfactor = "";
                           if (fillfactor != null) {
                              tempFillfactor = fillfactor.substring(0, 7);
                              tempFillfactor = fillfactor.toUpperCase();
                              int fillIntValue = 0;
                              if (tempFillfactor.startsWith("PCTFREE")) {
                                 fillfactor = fillfactor.substring(8);
                              } else if (tempFillfactor.startsWith("PCTUSED")) {
                                 fillfactor = fillfactor.substring(8);
                                 fillIntValue = Integer.parseInt(fillfactor);
                                 fillIntValue = 100 - fillIntValue;
                                 (new StringBuilder()).append("").append(fillIntValue).toString();
                              }

                              pcc.setWith("WITH");
                              HashMap diskAttr = new HashMap();
                              diskAttr.put("FILLFACTOR", new String(fillIntValue + ""));
                              pcc.setDiskAttr(diskAttr);
                              cqs.setHeap((String)null);
                              cqs.setDiskAttributes((String)null);
                              break;
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      String createSynonymObj;
      String columnName;
      if (cqs.getTableObject() != null) {
         TableObject orgTableObject = cqs.getTableObject();
         createSynonymObj = orgTableObject.getOwner();
         String userName = orgTableObject.getUser();
         columnName = orgTableObject.getTableName();
         if (this.tableOrView != null && this.includeDrop) {
            if (this.tableOrView.equalsIgnoreCase("TABLE")) {
               this.dropString = "--SwisSQL DROP SCRIPTS\nIF EXISTS ( SELECT name from sysobjects where name='" + columnName + "' AND type='U')\n DROP TABLE " + columnName + "\nGO\n";
            } else {
               this.dropString = "--SwisSQL DROP SCRIPTS\nIF EXISTS ( SELECT name from sysobjects where name='" + columnName + "' AND type='V')\n DROP VIEW " + columnName + "\nGO\n";
            }

            cqs.setDropString(this.dropString);
         }

         if (createSynonymObj != null && createSynonymObj.startsWith("`") && createSynonymObj.endsWith("`")) {
            createSynonymObj = createSynonymObj.substring(1, createSynonymObj.length() - 1);
            if (createSynonymObj.indexOf(32) != -1) {
               createSynonymObj = "\"" + createSynonymObj + "\"";
            }
         }

         if (userName != null && userName.startsWith("`") && userName.endsWith("`")) {
            userName = userName.substring(1, userName.length() - 1);
            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }
         }

         if (columnName != null && columnName.startsWith("`") && columnName.endsWith("`")) {
            columnName = columnName.substring(1, columnName.length() - 1);
            if (columnName.indexOf(32) != -1) {
               columnName = "\"" + columnName + "\"";
            }
         }

         if (cqs.getTemp() != null && cqs.getTemp().equalsIgnoreCase("GLOBAL TEMPORARY")) {
            columnName = "##" + columnName;
         }

         orgTableObject.setOwner(createSynonymObj);
         orgTableObject.setUser(userName);
         orgTableObject.setTableName(columnName);
         orgTableObject.toMSSQLServer();
      }

      cqs.setForce((String)null);
      cqs.setTemp((String)null);
      SelectQueryStatement sqs;
      if (this.getSelectQueryStatement() != null && cqs.getTableOrView() != null && cqs.getTableOrView().equalsIgnoreCase("TABLE")) {
         cqs.setCreate((String)null);
         cqs.setAs((String)null);
         sqs = cqs.getSelectQueryStatement();
         IntoStatement is = new IntoStatement();
         is.setIntoClause("INTO");
         is.setTableOrFileName(cqs.getTableObject().toString());
         sqs.setIntoStatement(is);
         cqs.setTableObject((TableObject)null);
         cqs.setTableObject((TableObject)null);
         cqs.setTableOrView((String)null);
         cqs.setSelectQueryStatement(sqs.toMSSQLServer());
      } else {
         sqs = cqs.getSelectQueryStatement();
         if (sqs != null) {
            FromClause fc = sqs.getFromClause();
            if (fc != null) {
               fromItems = fc.getFromItemList();

               for(i = 0; i < fromItems.size(); ++i) {
                  Object obj = fromItems.get(i);
                  if (obj instanceof FromTable) {
                     FromTable ft = (FromTable)obj;
                     obj = ft.getTableName();
                     if (obj instanceof String) {
                        String fromTableName = (String)obj;
                        int index = fromTableName.indexOf(".");
                        if (index != -1 && fromTableName.toLowerCase().indexOf("dbo.") == -1) {
                           fromTableName = fromTableName.substring(index + 1, fromTableName.length());
                        }

                        if (this.materialized != null) {
                           ft.setTableName("dbo." + fromTableName);
                        } else {
                           ft.setTableName(fromTableName);
                        }
                     }
                  }
               }
            }

            cqs.setSelectQueryStatement(sqs.toMSSQLServer());
         }
      }

      if (this.getWithStatement() != null) {
         cqs.setSelectQueryStatement((SelectQueryStatement)this.withStatement.toMSSQLServer());
         cqs.setWithStatement((WithStatement)null);
      }

      String viewName;
      if (cqs.getTypeIdentifier() != null) {
         viewName = cqs.getTypeIdentifier();
         if (cqs.getTypeString() != null && cqs.getTypeString().equalsIgnoreCase("comment")) {
            this.mysqlCommentTableOption = "/*" + cqs.getTypeString() + viewName + "*/";
            cqs.setMysqlCommentTableOption(this.mysqlCommentTableOption);
         }
      }

      if (cqs.getCreateIndexClause() != null) {
         CreateIndexClause createIndexClauseObject = cqs.getCreateIndexClause();
         cqs.setCreateIndexClause(createIndexClauseObject.toMSSQLServer());
      }

      if (cqs.getCreateIndexVector() != null) {
         viewName = "";
         new Vector();

         for(i = 0; i < cqs.getCreateIndexVector().size(); ++i) {
            CreateIndexClause createIndexClauseObject = (CreateIndexClause)cqs.getCreateIndexVector().get(i);
            if (!this.existsPKasIndex(createIndexClauseObject, cqs)) {
               createIndexClauseObject.setIndexOrKey("CREATE INDEX");
               createIndexClauseObject.setOn("ON");
               createIndexClauseObject.setTableOrView(this.tableObject);
               CreateIndexClause ansiIndexClause = createIndexClauseObject.toMSSQLServer();
               viewName = viewName + ";\n/* SwisSQL Message : Query split into multiple Queries.*/\n" + ansiIndexClause.toString().trim();
            }
         }

         cqs.setIndexString(viewName);
         cqs.setCreateIndexVector((Vector)null);
      }

      cqs.setTypeIdentifier((String)null);
      cqs.setTypeString((String)null);
      if (cqs.getCreateSequence() != null) {
         CreateSequenceStatement tempCreateSequence = cqs.getCreateSequence();
         CreateSequenceStatement createSequenceObj = tempCreateSequence.toMSSQLServer();
         cqs.setCreateSequenceStatement(createSequenceObj);
      }

      if (cqs.getCreateSynonym() != null) {
         CreateSynonymStatement tempCreateSynonym = cqs.getCreateSynonym();
         tempCreateSynonym.setCreate("CREATE");
         if (this.createOrReplace != null) {
            tempCreateSynonym.setCreateOrReplace(this.createOrReplace);
         }

         createSynonymObj = tempCreateSynonym.toMSSQLServerString();
         cqs.setCreateSynonymString(createSynonymObj);
      }

      cqs.setPhysicalAttributesVector((Vector)null);
      cqs.setIgnoreOrReplace((String)null);
      cqs.setSelectStatementString((String)null);
      if (this.materialized != null) {
         viewName = cqs.getTableObject().getTableName();
         SelectQueryStatement sqs = cqs.getSelectQueryStatement();
         ArrayList columnList = null;
         columnName = "";
         if (sqs != null) {
            FromClause matlFC = sqs.getFromClause();
            if (matlFC != null) {
               selectItems = matlFC.getFromItemList();

               for(i = 0; i < selectItems.size(); ++i) {
                  obj = selectItems.get(i);
                  if (obj instanceof FromTable) {
                     FromTable matlFromTable = (FromTable)obj;
                     obj = matlFromTable.getTableName();
                     if (obj instanceof String) {
                        String matlTableName = (String)obj;
                        int matlIndex = matlTableName.lastIndexOf(".");
                        if (matlIndex != -1) {
                           matlTableName = matlTableName.substring(matlIndex + 1, matlTableName.length());
                        }

                        if (SwisSQLAPI.primaryKeyMetaData.get(matlTableName.toUpperCase()) != null) {
                           columnList = (ArrayList)SwisSQLAPI.primaryKeyMetaData.get(matlTableName.toUpperCase());
                           break;
                        }
                     }
                  }
               }
            }
         }

         if (sqs != null && columnList == null) {
            SelectStatement ss = sqs.getSelectStatement();
            if (ss != null) {
               selectItems = ss.getSelectItemList();

               for(i = 0; i < selectItems.size(); ++i) {
                  obj = selectItems.get(i);
                  if (obj instanceof SelectColumn) {
                     Vector colExpr = ((SelectColumn)obj).getColumnExpression();
                     if (colExpr.size() == 1) {
                        obj = colExpr.get(0);
                        if (obj instanceof TableColumn) {
                           columnName = ((TableColumn)obj).getColumnName();
                           break;
                        }
                     }
                  }
               }
            }
         }

         if (columnList != null) {
            for(int l = 0; l < columnList.size(); ++l) {
               if (!columnName.equals("")) {
                  columnName = columnName + "," + columnList.get(l);
               } else {
                  columnName = (String)columnList.get(0);
               }
            }
         }

         cqs.indexedViewStmt = "CREATE UNIQUE CLUSTERED INDEX " + viewName + "index_ADV ON " + viewName + "(" + columnName + ")";
         cqs.setMaterialized((String)null);
      }

      return cqs;
   }

   public String toSybaseString() throws ConvertException {
      return this.toSybaseCreate().toString();
   }

   public CreateQueryStatement toSybaseCreate() throws ConvertException {
      commentWhenConstraintNameTruncated = "";
      CreateQueryStatement cqs = this.copyObjectValues();
      cqs.setTemp((String)null);
      cqs.setForce((String)null);
      cqs.setPhysicalCharacteristics((String)null);
      cqs.setCreateOrReplace((String)null);
      cqs.setConstraint((String)null);
      cqs.setConstraintName((String)null);
      cqs.setOnForQuotedIdentifier((String)null);
      cqs.setQuotedIdentifierCondition((String)null);
      cqs.setMaterialized((String)null);
      cqs.setViewMetaAttribute((String)null);
      cqs.setWithReadOnly((String)null);
      cqs.setExternalTable((String)null);
      cqs.setExternalDefaultDirectory((String)null);
      cqs.setExternalDelimiter((String)null);
      if (cqs.getOnCondition() != null && !cqs.getOnCondition().startsWith("ON DEFAULT")) {
         cqs.setOnCondition((String)null);
      }

      Vector tempVector;
      int i;
      int i;
      if (cqs.getColumnNames() != null) {
         tempVector = cqs.getColumnNames();

         for(i = 0; i < tempVector.size(); ++i) {
            if (tempVector.get(i) instanceof CreateColumn) {
               CreateColumn changeCreateColumn = (CreateColumn)tempVector.get(i);
               if (cqs.getTableObject() != null) {
                  changeCreateColumn.setTableNameFromCreateQueryStmt(cqs.getTableObject().getTableName());
                  changeCreateColumn.setDatatypeMapping(this.mapping);
               }

               changeCreateColumn.toSybaseString();
            }
         }

         this.removeUniqueKeyConstrOnPKExists(tempVector);
         if (cqs.getDiskAttributes() != null) {
            Vector ColumnNamesVector = cqs.getColumnNames();

            for(i = 0; i < tempVector.size(); ++i) {
               if (tempVector.get(i) instanceof CreateColumn) {
                  CreateColumn changeCreateColumn = (CreateColumn)tempVector.get(i);
                  Vector getConstraintVector = changeCreateColumn.getConstraintClause();
                  if (getConstraintVector != null) {
                     for(int j = 0; j < getConstraintVector.size(); ++j) {
                        ConstraintClause toSQLServerConstraintClause = (ConstraintClause)getConstraintVector.get(j);
                        ConstraintType toSQLServerConstraintType = toSQLServerConstraintClause.getConstraintType();
                        if (toSQLServerConstraintType instanceof PrimaryOrUniqueConstraintClause) {
                           PrimaryOrUniqueConstraintClause pcc = (PrimaryOrUniqueConstraintClause)toSQLServerConstraintType;
                           changeCreateColumn.setNotNullSetFromCreateQueryStatement(true);
                           String fillfactor = cqs.getDiskAttributes();
                           String tempFillfactor = "";
                           if (fillfactor != null) {
                              tempFillfactor = fillfactor.substring(0, 7);
                              tempFillfactor = fillfactor.toUpperCase();
                              int fillIntValue = 0;
                              if (tempFillfactor.startsWith("PCTFREE")) {
                                 fillfactor = fillfactor.substring(8);
                              } else if (tempFillfactor.startsWith("PCTUSED")) {
                                 fillfactor = fillfactor.substring(8);
                                 fillIntValue = Integer.parseInt(fillfactor);
                                 fillIntValue = 100 - fillIntValue;
                                 (new StringBuilder()).append("").append(fillIntValue).toString();
                              }

                              pcc.setWith("WITH");
                              HashMap diskAttr = new HashMap();
                              diskAttr.put("FILLFACTOR", new String(fillIntValue + ""));
                              pcc.setDiskAttr(diskAttr);
                              cqs.setHeap((String)null);
                              cqs.setDiskAttributes((String)null);
                              break;
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      String createSynonymObj;
      if (cqs.getTableObject() != null) {
         TableObject orgTableObject = cqs.getTableObject();
         createSynonymObj = orgTableObject.getOwner();
         String userName = orgTableObject.getUser();
         String tableName = orgTableObject.getTableName();
         if (createSynonymObj != null && (createSynonymObj.startsWith("[") && createSynonymObj.endsWith("]") || createSynonymObj.startsWith("`") && createSynonymObj.endsWith("`"))) {
            createSynonymObj = createSynonymObj.substring(1, createSynonymObj.length() - 1);
            if (createSynonymObj.indexOf(32) != -1) {
               createSynonymObj = "\"" + createSynonymObj + "\"";
            }
         }

         if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
            userName = userName.substring(1, userName.length() - 1);
            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }
         }

         if (tableName != null && (tableName.startsWith("[") && tableName.endsWith("]") || tableName.startsWith("`") && tableName.endsWith("`"))) {
            tableName = tableName.substring(1, tableName.length() - 1);
            if (tableName.indexOf(32) != -1) {
               tableName = "\"" + tableName + "\"";
            }
         }

         orgTableObject.setOwner(createSynonymObj);
         orgTableObject.setUser(userName);
         orgTableObject.setTableName(tableName);
         orgTableObject.toSybase();
      }

      if (cqs.getTableOrView() != null && cqs.getTableOrView().equalsIgnoreCase("VIEW")) {
         cqs.getTableObject().setDot(".");
      }

      cqs.setForce((String)null);
      if (this.getSelectQueryStatement() != null) {
         cqs.setSelectQueryStatement(this.selectQueryStatement.toSybase());
      }

      if (this.getWithStatement() != null) {
         cqs.setSelectQueryStatement((SelectQueryStatement)this.withStatement.toSybase());
         cqs.setWithStatement((WithStatement)null);
      }

      if (cqs.getCreateIndexClause() != null) {
         CreateIndexClause createIndexClauseObject = cqs.getCreateIndexClause();
         cqs.setCreateIndexClause(createIndexClauseObject.toSybase());
      }

      if (cqs.getCreateIndexVector() != null) {
         String indexString = "";
         new Vector();

         for(i = 0; i < cqs.getCreateIndexVector().size(); ++i) {
            CreateIndexClause createIndexClauseObject = (CreateIndexClause)cqs.getCreateIndexVector().get(i);
            if (!this.existsPKasIndex(createIndexClauseObject, cqs)) {
               createIndexClauseObject.setIndexOrKey("CREATE INDEX");
               createIndexClauseObject.setOn("ON");
               createIndexClauseObject.setTableOrView(this.tableObject);
               CreateIndexClause ansiIndexClause = createIndexClauseObject.toSybase();
               indexString = indexString + ";\n/* SwisSQL Message : Query split into multiple Queries.*/\n" + ansiIndexClause.toString().trim();
            }
         }

         cqs.setIndexString(indexString);
         cqs.setCreateIndexVector((Vector)null);
      }

      cqs.setTypeIdentifier((String)null);
      cqs.setTypeString((String)null);
      if (cqs.getCreateSequence() != null) {
         CreateSequenceStatement tempCreateSequence = cqs.getCreateSequence();
         CreateSequenceStatement createSequenceObj = tempCreateSequence.toSybase();
         cqs.setCreateSequenceStatement(createSequenceObj);
      }

      if (cqs.getCreateSynonym() != null) {
         CreateSynonymStatement tempCreateSynonym = cqs.getCreateSynonym();
         tempCreateSynonym.setCreate("CREATE");
         if (this.createOrReplace != null) {
            tempCreateSynonym.setCreateOrReplace(this.createOrReplace);
         }

         createSynonymObj = tempCreateSynonym.toSybaseString();
         cqs.setCreateSynonymString(createSynonymObj);
      }

      if (cqs.getPhysicalAttributesVector() != null) {
         tempVector = new Vector();

         for(i = 0; i < cqs.getPhysicalAttributesVector().size(); ++i) {
            PhysicalAttributesClause tempPhysicalAttributesClause = (PhysicalAttributesClause)cqs.getPhysicalAttributesVector().get(i);
            tempPhysicalAttributesClause.setFillFactor((String)null);
            tempPhysicalAttributesClause.setFillFactorValue((String)null);
            tempVector.add(tempPhysicalAttributesClause.toSybase());
         }

         cqs.setPhysicalAttributesVector(tempVector);
      }

      cqs.setIgnoreOrReplace((String)null);
      cqs.setSelectStatementString((String)null);
      return cqs;
   }

   public String toMySQLString() throws ConvertException {
      CreateQueryStatement cqs = this.toMySQLCreate();
      if (cqs.getDatabaseObject() != null) {
         return cqs.getDatabaseObject().getSize() != null ? "CREATE DATABASE " + cqs.getDatabaseObject().getName() + " SIZE '" + cqs.getDatabaseObject().getSize() : "CREATE DATABASE " + cqs.getDatabaseObject().getName();
      } else {
         return cqs.toString();
      }
   }

   public String toSnowflakeString() throws ConvertException {
      return this.toSnowflakeCreate().toString();
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

   public CreateQueryStatement toSnowflakeCreate() throws ConvertException {
      commentWhenConstraintNameTruncated = "";
      CreateQueryStatement cqs = this.copyObjectValues();
      cqs.setForce((String)null);
      cqs.setHeap((String)null);
      cqs.setPhysicalCharacteristics((String)null);
      cqs.setCreateOrReplace((String)null);
      cqs.setDiskAttributes((String)null);
      cqs.setConstraint((String)null);
      cqs.setConstraintName((String)null);
      cqs.setOnForQuotedIdentifier((String)null);
      cqs.setQuotedIdentifierCondition((String)null);
      cqs.setMaterialized((String)null);
      cqs.setViewMetaAttribute((String)null);
      cqs.setWithReadOnly((String)null);
      cqs.setLock((String)null);
      cqs.setLockData((String)null);
      cqs.setExternalTable((String)null);
      cqs.setExternalDefaultDirectory((String)null);
      cqs.setExternalDelimiter((String)null);
      String indexString;
      if (cqs.getTemp() != null) {
         indexString = cqs.getTemp();
         if (indexString.equalsIgnoreCase("GLOBAL TEMPORARY") || indexString.equalsIgnoreCase("LOCAL TEMPORARY")) {
            cqs.setTemp("TEMPORARY");
         }
      }

      String createSynonymObj;
      if (cqs.getTableObject() != null) {
         TableObject cqsTableObject = cqs.getTableObject();
         createSynonymObj = cqsTableObject.getOwner();
         String userName = cqsTableObject.getUser();
         String tableName = cqsTableObject.getTableName();
         if (createSynonymObj != null && (createSynonymObj.startsWith("[") && createSynonymObj.endsWith("]") || createSynonymObj.startsWith("`") && createSynonymObj.endsWith("`"))) {
            createSynonymObj = createSynonymObj.substring(1, createSynonymObj.length() - 1);
            if (createSynonymObj.indexOf(32) != -1) {
               createSynonymObj = "\"" + createSynonymObj + "\"";
            }
         }

         if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
            userName = userName.substring(1, userName.length() - 1);
            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }
         }

         if (tableName != null && (tableName.startsWith("[") && tableName.endsWith("]") || tableName.startsWith("`") && tableName.endsWith("`"))) {
            tableName = tableName.substring(1, tableName.length() - 1);
            if (tableName.indexOf(32) != -1) {
               tableName = "\"" + tableName + "\"";
            }
         }

         cqsTableObject.setOwner(createSynonymObj);
         cqsTableObject.setUser(userName);
         cqsTableObject.setTableName(tableName);
         cqsTableObject.toSnowflake();
      }

      if (cqs.getCreateIndexClause() != null) {
         CreateIndexClause createIndexClauseObject = cqs.getCreateIndexClause();
         cqs.setCreateIndexClause(createIndexClauseObject.toSnowflake());
      }

      if (cqs.getCreateIndexVector() != null) {
         indexString = "";
         new Vector();

         for(int i = 0; i < cqs.getCreateIndexVector().size(); ++i) {
            CreateIndexClause createIndexClauseObject = (CreateIndexClause)cqs.getCreateIndexVector().get(i);
            if (!this.existsPKasIndex(createIndexClauseObject, cqs)) {
               createIndexClauseObject.setIndexOrKey("CREATE INDEX");
               createIndexClauseObject.setOn("ON");
               createIndexClauseObject.setTableOrView(this.tableObject);
               CreateIndexClause ansiIndexClause = createIndexClauseObject.toSnowflake();
               indexString = indexString + "\n/* SwisSQL Message : Query split into multiple Queries.*/\n" + ansiIndexClause.toString();
            }
         }

         cqs.setIndexString("\n" + indexString);
         cqs.setCreateIndexVector((Vector)null);
      }

      if (cqs.getOnCondition() != null && cqs.getOnCondition().startsWith("ON DEFAULT")) {
         cqs.setOnCondition((String)null);
      }

      if (cqs.getColumnNames() != null) {
         Vector columnNamesVector = cqs.getColumnNames();

         for(int i = 0; i < columnNamesVector.size(); ++i) {
            if (columnNamesVector.get(i) instanceof CreateColumn) {
               CreateColumn changeCreateColumn = (CreateColumn)columnNamesVector.get(i);
               if (cqs.getTableObject() != null) {
                  changeCreateColumn.setTableNameFromCreateQueryStmt(cqs.getTableObject().getTableName());
                  changeCreateColumn.setDatatypeMapping(this.mapping);
               }

               changeCreateColumn.toSnowflakeString();
            }
         }
      }

      if (this.getSelectQueryStatement() != null) {
         cqs.setSelectQueryStatement(this.selectQueryStatement.toSnowflake());
      }

      if (this.getWithStatement() != null) {
         cqs.setSelectQueryStatement((SelectQueryStatement)this.withStatement.toSnowflake());
         cqs.setWithStatement((WithStatement)null);
      }

      cqs.setTypeIdentifier((String)null);
      cqs.setTypeString((String)null);
      if (cqs.getCreateSequence() != null) {
         CreateSequenceStatement tempCreateSequence = cqs.getCreateSequence();
         CreateSequenceStatement createSequenceObj = tempCreateSequence.toSnowflake();
         cqs.setCreateSequenceStatement(createSequenceObj);
      }

      if (cqs.getCreateSynonym() != null) {
         CreateSynonymStatement tempCreateSynonym = cqs.getCreateSynonym();
         tempCreateSynonym.setCreate("CREATE");
         if (this.createOrReplace != null) {
            tempCreateSynonym.setCreateOrReplace(this.createOrReplace);
         }

         createSynonymObj = tempCreateSynonym.toSnowflakeString();
         cqs.setCreateSynonymString(createSynonymObj);
      }

      cqs.setPhysicalAttributesVector((Vector)null);
      cqs.setIgnoreOrReplace((String)null);
      cqs.setSelectStatementString((String)null);
      return cqs;
   }

   public CreateQueryStatement toMySQLCreate() throws ConvertException {
      commentWhenConstraintNameTruncated = "";
      CreateQueryStatement cqs = this.copyObjectValues();
      cqs.setForce((String)null);
      cqs.setHeap((String)null);
      cqs.setDiskAttributes((String)null);
      cqs.setPhysicalCharacteristics((String)null);
      cqs.setCreateOrReplace((String)null);
      cqs.setConstraint((String)null);
      cqs.setConstraintName((String)null);
      cqs.setOnForQuotedIdentifier((String)null);
      cqs.setQuotedIdentifierCondition((String)null);
      cqs.setMaterialized((String)null);
      cqs.setViewMetaAttribute((String)null);
      cqs.setWithReadOnly((String)null);
      cqs.setLock((String)null);
      cqs.setLockData((String)null);
      cqs.setExternalTable((String)null);
      cqs.setExternalDefaultDirectory((String)null);
      cqs.setExternalDelimiter((String)null);
      if (cqs.getDatabaseObject() != null) {
         return cqs;
      } else {
         if (cqs.getTemp() != null) {
            String tempCQS = cqs.getTemp();
            if (tempCQS.equalsIgnoreCase("GLOBAL TEMPORARY") || tempCQS.equalsIgnoreCase("LOCAL TEMPORARY") || tempCQS.equalsIgnoreCase("TEMP")) {
               cqs.setTemp("TEMPORARY");
            }
         }

         String ownerName;
         String createSynonymObj;
         if (cqs.getTableObject() != null) {
            TableObject cqsTableObject = cqs.getTableObject();
            ownerName = cqsTableObject.getOwner();
            createSynonymObj = cqsTableObject.getUser();
            String tableName = cqsTableObject.getTableName();
            if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("\"") && ownerName.endsWith("\""))) {
               ownerName = ownerName.substring(1, ownerName.length() - 1);
               if (ownerName.indexOf(32) != -1) {
                  ownerName = "`" + ownerName + "`";
               }
            }

            if (createSynonymObj != null && (createSynonymObj.startsWith("[") && createSynonymObj.endsWith("]") || createSynonymObj.startsWith("\"") && createSynonymObj.endsWith("\""))) {
               createSynonymObj = createSynonymObj.substring(1, createSynonymObj.length() - 1);
               if (createSynonymObj.indexOf(32) != -1) {
                  createSynonymObj = "`" + createSynonymObj + "`";
               }
            }

            if (tableName != null && (tableName.startsWith("[") && tableName.endsWith("]") || tableName.startsWith("\"") && tableName.endsWith("\""))) {
               tableName = tableName.substring(1, tableName.length() - 1);
               if (tableName.indexOf(32) != -1) {
                  tableName = "`" + tableName + "`";
               }
            }

            cqsTableObject.setOwner(ownerName);
            cqsTableObject.setUser(createSynonymObj);
            cqsTableObject.setTableName(tableName);
            cqsTableObject.toMySQL();
         }

         if (cqs.getCreateIndexClause() != null) {
            CreateIndexClause createIndexClauseObject = cqs.getCreateIndexClause();
            CreateIndexClause mySQLIndexClause = createIndexClauseObject.toMySQL();
            cqs.setCreateIndexClause(mySQLIndexClause);
         }

         if (cqs.getCreateIndexVector() != null) {
            Vector tempVector = new Vector();

            for(int i = 0; i < cqs.getCreateIndexVector().size(); ++i) {
               CreateIndexClause createIndexClauseObject = (CreateIndexClause)cqs.getCreateIndexVector().get(i);
               tempVector.add(createIndexClauseObject.toMySQL());
            }

            cqs.setCreateIndexVector(tempVector);
         }

         if (cqs.getOnCondition() != null) {
            cqs.setOnCondition((String)null);
         }

         Map map = new HashMap();
         if (cqs.getColumnNames() != null) {
            Vector columnNamesVector = cqs.getColumnNames();

            for(int i = 0; i < columnNamesVector.size(); ++i) {
               Object column = columnNamesVector.get(i);
               if (column instanceof CreateColumn) {
                  CreateColumn changeCreateColumn = (CreateColumn)column;
                  Datatype dt = changeCreateColumn.getDatatype();
                  String sizeStr = null;
                  int size = 0;
                  String columnName;
                  if (dt instanceof CharacterClass) {
                     columnName = dt.getDatatypeName();
                     if (columnName.equalsIgnoreCase("VARCHAR") || columnName.equalsIgnoreCase("CHAR") || columnName.equalsIgnoreCase("NCHAR") || columnName.equalsIgnoreCase("NVARCHAR") || columnName.equalsIgnoreCase("TEXT")) {
                        sizeStr = dt.getSize();
                        if (sizeStr != null) {
                           try {
                              size = Integer.parseInt(sizeStr);
                           } catch (NumberFormatException var23) {
                           }
                        }
                     }
                  }

                  if (cqs.getTableObject() != null) {
                     changeCreateColumn.setTableNameFromCreateQueryStmt(cqs.getTableObject().getTableName());
                     changeCreateColumn.setDatatypeMapping(this.mapping);
                  }

                  changeCreateColumn.toMySQLString();
                  columnName = changeCreateColumn.getColumnName();
                  if (columnName != null) {
                     Datatype dt2 = changeCreateColumn.getDatatype();
                     if (dt2 instanceof CharacterClass) {
                        String datatypeName2 = dt2.getDatatypeName();
                        if (datatypeName2.equalsIgnoreCase("TEXT") && size > 255) {
                           columnName = changeCreateColumn.getColumnName();
                           map.put(columnName, sizeStr);
                        }
                     }
                  }

                  if (columnName == null) {
                     Vector constraintList = changeCreateColumn.getConstraintClause();
                     if (constraintList != null) {
                        int z = constraintList.size();

                        for(int j = 0; j < z; ++j) {
                           Object o = constraintList.get(j);
                           if (o instanceof ConstraintClause) {
                              ConstraintClause clause = (ConstraintClause)o;
                              ConstraintType type = clause.getConstraintType();
                              if (type instanceof PrimaryOrUniqueConstraintClause) {
                                 PrimaryOrUniqueConstraintClause pk = (PrimaryOrUniqueConstraintClause)type;
                                 Vector constraintColumnNames = pk.getConstraintColumnNames();
                                 int g = constraintColumnNames.size();

                                 for(int k = 0; k < g; ++k) {
                                    String ccn = (String)constraintColumnNames.get(k);
                                    String siz = (String)map.get(ccn);
                                    if (siz != null) {
                                       pk.addToColumnNameVsSize(ccn, siz);
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         if (this.getSelectQueryStatement() != null) {
            cqs.setSelectQueryStatement(this.selectQueryStatement.toMySQL());
         }

         if (this.getWithStatement() != null) {
            cqs.setSelectQueryStatement((SelectQueryStatement)this.withStatement.toMySQL());
            cqs.setWithStatement((WithStatement)null);
         }

         if (cqs.getTypeString() != null) {
            ownerName = cqs.getTypeString();
         }

         if (cqs.getTypeIdentifier() != null) {
            ownerName = cqs.getTypeIdentifier();
         }

         if (cqs.getCreateSequence() != null) {
            CreateSequenceStatement tempCreateSequence = cqs.getCreateSequence();
            CreateSequenceStatement createSequenceObj = tempCreateSequence.toMySQL();
            cqs.setCreateSequenceStatement(createSequenceObj);
         }

         if (cqs.getCreateSynonym() != null) {
            CreateSynonymStatement tempCreateSynonym = cqs.getCreateSynonym();
            tempCreateSynonym.setCreate("CREATE");
            if (this.createOrReplace != null) {
               tempCreateSynonym.setCreateOrReplace(this.createOrReplace);
            }

            createSynonymObj = tempCreateSynonym.toMySQLString();
            cqs.setCreateSynonymString(createSynonymObj);
         }

         cqs.setPhysicalAttributesVector((Vector)null);
         return cqs;
      }
   }

   public String toOracleString() throws ConvertException {
      CreateQueryStatement cqs = this.toOracleCreate();
      if (cqs.getDatabaseObject() != null) {
         return cqs.getDatabaseObject().getSize() != null ? "CREATE TABLESPACE " + cqs.getDatabaseObject().getName() + " LOGGING DATAFILE '" + cqs.getDatabaseObject().getName() + ".dbf' SIZE " + cqs.getDatabaseObject().getSize() + "M RESUSE AUTOEXTEND ON MAXSIZE UNLIMITED" : "CREATE TABLESPACE " + cqs.getDatabaseObject().getName() + " LOGGING DATAFILE '" + cqs.getDatabaseObject().getName() + ".dbf' SIZE " + "5M RESUSE AUTOEXTEND ON MAXSIZE UNLIMITED";
      } else {
         return cqs.toString();
      }
   }

   public CreateQueryStatement toOracleCreate() throws ConvertException {
      String[] keywords = null;
      this.modifiedObjects = new ModifiedObjectNames();
      ModifiedObjectAttr modifiedAttr = new ModifiedObjectAttr();
      String origTableName = null;
      if (SwisSQLUtils.getKeywords(1) != null) {
         keywords = (String[])SwisSQLUtils.getKeywords(1);
      }

      if (SwisSQLAPI.truncateTableCount > 99) {
         SwisSQLAPI.truncateTableCount = 0;
      }

      commentWhenConstraintNameTruncated = "";
      CreateQueryStatement cqs = this.copyObjectValues();
      cqs.setDropOption(this.includeDrop);
      cqs.setOnForQuotedIdentifier((String)null);
      cqs.setQuotedIdentifierCondition((String)null);
      cqs.setLock((String)null);
      cqs.setLockData((String)null);
      cqs.setExternalTable((String)null);
      cqs.setExternalDefaultDirectory((String)null);
      cqs.setExternalDelimiter((String)null);
      if (cqs.getTemp() != null) {
         cqs.setTemp("GLOBAL TEMPORARY");
      }

      TableObject orgTableObject;
      String createSynonymObj;
      String ownerName;
      String userName;
      String oracleTableName;
      if (cqs.getTableObject() != null) {
         StringBuffer tableStringBuffer = new StringBuffer();
         orgTableObject = cqs.getTableObject();
         createSynonymObj = orgTableObject.getTableName();
         ownerName = orgTableObject.getOwner();
         userName = orgTableObject.getUser();
         if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("`") && ownerName.endsWith("`"))) {
            ownerName = ownerName.substring(1, ownerName.length() - 1);
            if (SwisSQLOptions.retainQuotedIdentifierForOracle || ownerName.indexOf(32) != -1) {
               ownerName = "\"" + ownerName + "\"";
            }
         }

         if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
            userName = userName.substring(1, userName.length() - 1);
            if (SwisSQLOptions.retainQuotedIdentifierForOracle || userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }
         }

         if (createSynonymObj != null && (createSynonymObj.startsWith("[") && createSynonymObj.endsWith("]") || createSynonymObj.startsWith("`") && createSynonymObj.endsWith("`"))) {
            createSynonymObj = createSynonymObj.substring(1, createSynonymObj.length() - 1);
            if (SwisSQLOptions.retainQuotedIdentifierForOracle || createSynonymObj.indexOf(32) != -1) {
               createSynonymObj = "\"" + createSynonymObj + "\"";
            }
         }

         origTableName = createSynonymObj;
         createSynonymObj = CustomizeUtil.objectNamesToQuotedIdentifier(createSynonymObj, keywords, modifiedAttr, 1);
         modifiedAttr.setOriginalName(origTableName);
         if (createSynonymObj != null && !createSynonymObj.equals(origTableName)) {
            modifiedAttr.setModifiedName(createSynonymObj);
            this.modifiedObjects.addModifiedObjectName(modifiedAttr);
         }

         if (SwisSQLAPI.truncateTableNameForOracle && createSynonymObj != null && createSynonymObj.length() > 30) {
            modifiedAttr = new ModifiedObjectAttr();
            modifiedAttr.setOriginalName(origTableName);
            if (createSynonymObj.indexOf("\"") != -1) {
               createSynonymObj = createSynonymObj.substring(0, 23) + "_ADV" + SwisSQLAPI.truncateTableCount + "\"";
               modifiedAttr.setModifiedName(createSynonymObj);
               ++SwisSQLAPI.truncateTableCount;
            } else {
               createSynonymObj = createSynonymObj.substring(0, 24) + "_ADV" + SwisSQLAPI.truncateTableCount;
               modifiedAttr.setModifiedName(createSynonymObj);
               ++SwisSQLAPI.truncateTableCount;
            }

            SwisSQLUtils.setObjectNameForMapping(origTableName, createSynonymObj);
            modifiedAttr.setModifiedType(1);
            this.modifiedObjects.addModifiedObjectName(modifiedAttr);
         }

         orgTableObject.setTableName(createSynonymObj);
         if (ownerName != null && (ownerName.equalsIgnoreCase("dbo") || ownerName.equalsIgnoreCase("[dbo]"))) {
            orgTableObject.setOwner((String)null);
         }

         orgTableObject.setOwner(ownerName);
         if (userName == null || !userName.equalsIgnoreCase("dbo") && !userName.equalsIgnoreCase("[dbo]")) {
            orgTableObject.setUser(userName);
         } else {
            orgTableObject.setUser((String)null);
         }

         if (this.isTenroxRequirement) {
            orgTableObject.setUser("PUSER");
         }

         StringTokenizer st = new StringTokenizer(createSynonymObj, ".");
         int count = 0;

         Vector tokenVector;
         for(tokenVector = new Vector(); st.hasMoreTokens(); ++count) {
            tokenVector.add(st.nextToken());
         }

         for(int i_count = 0; i_count < tokenVector.size(); ++i_count) {
            oracleTableName = (String)tokenVector.get(i_count);
            if (oracleTableName != null && oracleTableName.startsWith("[") && oracleTableName.endsWith("]")) {
               oracleTableName = oracleTableName.substring(1, oracleTableName.length() - 1);
               if (SwisSQLOptions.retainQuotedIdentifierForOracle || oracleTableName.indexOf(32) != -1) {
                  oracleTableName = "\"" + oracleTableName + "\"";
               }
            }

            if (i_count > 0) {
               tableStringBuffer.append(".");
            }

            tableStringBuffer.append(oracleTableName);
         }

         orgTableObject.setTableName(tableStringBuffer.toString());
         orgTableObject.toOracle();
      }

      String origColName;
      if (cqs.getCreateIndexClause() != null) {
         CreateIndexClause createIndexClauseObject = cqs.getCreateIndexClause();
         orgTableObject = createIndexClauseObject.getIndexName();
         CreateIndexClause oracleCreateIndexClause = createIndexClauseObject.toOracle();
         if (orgTableObject != null) {
            ownerName = orgTableObject.getTableName();
            modifiedAttr = new ModifiedObjectAttr();
            userName = CustomizeUtil.objectNamesToQuotedIdentifier(ownerName, keywords, modifiedAttr, 1);
            modifiedAttr.setOriginalName(ownerName);
            if (!ownerName.equals(userName) && ownerName.length() < 31) {
               orgTableObject.setTableName(userName);
               modifiedAttr.setModifiedName(userName);
               this.modifiedObjects.addModifiedObjectName(modifiedAttr);
            }

            if (ownerName.length() > 30) {
               oracleCreateIndexClause.toString();
               origColName = oracleCreateIndexClause.getIndexName().getTableName();
               modifiedAttr.setModifiedName(origColName);
               modifiedAttr.setModifiedType(1);
               this.modifiedObjects.addModifiedObjectName(modifiedAttr);
            }
         }

         cqs.setCreateIndexClause(oracleCreateIndexClause);
      }

      String indexString;
      int countLong;
      if (cqs.getCreateIndexVector() != null) {
         indexString = "";
         new Vector();

         for(countLong = 0; countLong < cqs.getCreateIndexVector().size(); ++countLong) {
            CreateIndexClause createIndexClauseObject = (CreateIndexClause)cqs.getCreateIndexVector().get(countLong);
            if (!this.existsPKasIndex(createIndexClauseObject, cqs) && !this.isDatatypeBlobOrClob(createIndexClauseObject, cqs)) {
               createIndexClauseObject.setIndexOrKey("CREATE INDEX");
               createIndexClauseObject.setOn("ON");
               createIndexClauseObject.setTableOrView(this.tableObject);
               CreateIndexClause ansiIndexClause = createIndexClauseObject.toOracle();
               indexString = indexString + "\n/\n/* SwisSQL Message : Query split into multiple Queries.*/\n" + ansiIndexClause.toString().trim();
            }
         }

         cqs.setIndexString(indexString);
         cqs.setCreateIndexVector((Vector)null);
      }

      if (cqs.getOnCondition() != null && cqs.getOnCondition().startsWith("ON DEFAULT")) {
         cqs.setOnCondition((String)null);
      }

      if (cqs.getConstraint() != null) {
         indexString = cqs.getConstraint();
      }

      if (cqs.getConstraintName() != null) {
         indexString = cqs.getConstraintName();
      }

      ArrayList columnList = new ArrayList();
      Vector temp_Vector;
      if (cqs.getColumnNames() != null) {
         temp_Vector = cqs.getColumnNames();
         countLong = 0;

         ConstraintClause toOracleConstraintClause;
         String modifiedColName;
         String tableName;
         Vector changedConstraintVector;
         int j;
         String fkTableName;
         for(int i = 0; i < temp_Vector.size(); ++i) {
            if (temp_Vector.get(i) instanceof CreateColumn) {
               CreateColumn changeCreateColumn = (CreateColumn)temp_Vector.get(i);
               origColName = changeCreateColumn.getColumnName();
               if (origColName != null) {
                  modifiedAttr = new ModifiedObjectAttr();
                  changeCreateColumn.setColumnName(CustomizeUtil.objectNamesToQuotedIdentifier(changeCreateColumn.getColumnName(), keywords, modifiedAttr, 1));
                  modifiedColName = changeCreateColumn.getColumnName();
                  if (!origColName.equals(modifiedColName)) {
                     modifiedAttr.setOriginalName(origColName);
                     modifiedAttr.setModifiedName(modifiedColName);
                     modifiedAttr.setTableName(origTableName);
                     this.modifiedObjects.addModifiedColumns(modifiedAttr);
                  }
               }

               String datatypeName;
               if (cqs.getTableObject() != null) {
                  TableObject orgTableObject = cqs.getTableObject();
                  tableName = orgTableObject.getTableName();
                  datatypeName = orgTableObject.getOrigTableName();
                  oracleTableName = tableName;
                  if (this.context != null) {
                     if (datatypeName != null && datatypeName.startsWith("#")) {
                        oracleTableName = this.context.getEquivalent(datatypeName).toString();
                     } else {
                        oracleTableName = this.context.getEquivalent(tableName).toString();
                     }
                  }

                  changeCreateColumn.setTableNameFromCreateQueryStmt(oracleTableName);
                  changeCreateColumn.setDatatypeMapping(this.mapping);
               }

               if (changeCreateColumn.getDatatype() != null) {
                  Datatype changeCreateColumnDatatype = changeCreateColumn.getDatatype();
                  if (changeCreateColumnDatatype instanceof CharacterClass) {
                     CharacterClass charClass = (CharacterClass)changeCreateColumnDatatype;
                     datatypeName = charClass.getDatatypeName();
                     if (datatypeName.equalsIgnoreCase("LONG")) {
                        ++countLong;
                        if (countLong > 1) {
                           charClass.setDatatypeName("TEXT");
                           charClass.setOpenBrace((String)null);
                           charClass.setClosedBrace((String)null);
                           charClass.setSize((String)null);
                        }
                     }
                  }
               }

               Vector getConstraintVector = changeCreateColumn.getConstraintClause();
               boolean isTableLevelConstraintRemoved = false;
               isTableLevelConstraintRemoved = this.removeCheckConstrWithDetFunctions(getConstraintVector, changeCreateColumn, temp_Vector, i);
               changeCreateColumn.toOracleString();
               if (changeCreateColumn.getColumnName() != null) {
                  columnList.add(changeCreateColumn.getColumnName());
               }

               if (changeCreateColumn.getCreateSequenceString() != null) {
                  cqs.setCreateSequenceString(changeCreateColumn.getCreateSequenceString());
               }

               changedConstraintVector = changeCreateColumn.getConstraintClause();
               if (getConstraintVector != null) {
                  for(j = 0; j < getConstraintVector.size(); ++j) {
                     toOracleConstraintClause = (ConstraintClause)getConstraintVector.get(j);
                     String origConstrName = toOracleConstraintClause.getConstraintName();
                     modifiedAttr = new ModifiedObjectAttr();
                     modifiedAttr.setOriginalName(origConstrName);
                     if (origConstrName != null && origConstrName.startsWith("\"") && !origConstrName.endsWith("\"")) {
                        toOracleConstraintClause.setConstraintName(origConstrName + "\"");
                        modifiedAttr.setModifiedType(1);
                        modifiedAttr.setModifiedName(origConstrName + "\"");
                        this.modifiedObjects.addModifiedConstraints(modifiedAttr);
                     }

                     toOracleConstraintClause.setConstraintName(CustomizeUtil.objectNamesToQuotedIdentifier(toOracleConstraintClause.getConstraintName(), keywords, modifiedAttr, 1));
                     String modifiedConstrName = toOracleConstraintClause.getConstraintName();
                     if (origConstrName != null && !origConstrName.equals(modifiedConstrName)) {
                        modifiedAttr.setModifiedName(modifiedConstrName);
                        this.modifiedObjects.addModifiedConstraints(modifiedAttr);
                     }

                     if (toOracleConstraintClause != null) {
                        ConstraintType changeConstraintType = toOracleConstraintClause.getConstraintType();
                        String modifiedFKTableName;
                        if (changeConstraintType instanceof PrimaryOrUniqueConstraintClause) {
                           PrimaryOrUniqueConstraintClause primaryOrUniqueConstraintClause = (PrimaryOrUniqueConstraintClause)changeConstraintType;
                           primaryOrUniqueConstraintClause.setClustered((String)null);
                           changeCreateColumn.setNotNullSetFromCreateQueryStatement(true);
                           Vector colNames = primaryOrUniqueConstraintClause.getConstraintColumnNames();
                           this.columnNamesToQuotedIdentifier(colNames, keywords, origTableName);
                           if (primaryOrUniqueConstraintClause.getWith() != null) {
                              Object fillfactor = primaryOrUniqueConstraintClause.getDiskAttr().get("FILLFACTOR");
                              if (fillfactor != null) {
                                 modifiedFKTableName = fillfactor.toString();
                                 cqs.setHeap("ORGANIZATION HEAP ");

                                 try {
                                    int pcnt = Integer.parseInt(modifiedFKTableName);
                                    if (pcnt <= 60) {
                                       cqs.setDiskAttributes("PCTFREE " + pcnt);
                                    } else {
                                       cqs.setDiskAttributes("PCTFREE " + pcnt + ",\n\tPCTUSED " + (100 - pcnt));
                                    }
                                 } catch (Exception var24) {
                                    cqs.setDiskAttributes("PCTFREE " + modifiedFKTableName);
                                 }
                              }

                              primaryOrUniqueConstraintClause.setWith((String)null);
                              primaryOrUniqueConstraintClause.setDiskAttr((HashMap)null);
                           }
                        } else if (changeConstraintType instanceof ForeignConstraintClause) {
                           ForeignConstraintClause foreignConstraintClause = (ForeignConstraintClause)changeConstraintType;
                           TableObject fkTableObject = foreignConstraintClause.getTableName();
                           modifiedAttr = new ModifiedObjectAttr();
                           if (fkTableObject != null) {
                              fkTableName = fkTableObject.getTableName();
                              if (fkTableName != null) {
                                 modifiedFKTableName = CustomizeUtil.objectNamesToQuotedIdentifier(fkTableName, keywords, modifiedAttr, 1);
                                 if (!modifiedFKTableName.equals(fkTableName)) {
                                    fkTableObject.setTableName(modifiedFKTableName);
                                    modifiedAttr.setOriginalName(fkTableName);
                                    modifiedAttr.setModifiedName(modifiedFKTableName);
                                    this.modifiedObjects.addModifiedObjectName(modifiedAttr);
                                 }
                              }
                           }

                           Vector colNames = foreignConstraintClause.getConstraintColumnNames();
                           this.columnNamesToQuotedIdentifier(colNames, keywords, origTableName);
                           Vector fkColNames = foreignConstraintClause.getReferenceTableColumnNames();
                           this.columnNamesToQuotedIdentifier(fkColNames, keywords, origTableName);
                        }

                        cqs.setTriggerForIdentity(toOracleConstraintClause.getTriggerForIdentity());
                     }
                  }
               }

               if (isTableLevelConstraintRemoved) {
                  --i;
               }
            }
         }

         this.removeUniqueKeyConstrOnPKExists(temp_Vector);
         HashMap truncatedNames = SwisSQLUtils.truncateNames(columnList, 30);

         for(int i = 0; i < temp_Vector.size(); ++i) {
            if (temp_Vector.get(i) instanceof CreateColumn) {
               CreateColumn changeCreateColumn = (CreateColumn)temp_Vector.get(i);
               modifiedColName = changeCreateColumn.getColumnName();
               tableName = (String)truncatedNames.get(modifiedColName);
               if (tableName != null) {
                  changeCreateColumn.setColumnName(tableName);
               }

               changedConstraintVector = changeCreateColumn.getConstraintClause();
               if (changedConstraintVector != null) {
                  for(j = 0; j < changedConstraintVector.size(); ++j) {
                     toOracleConstraintClause = (ConstraintClause)changedConstraintVector.get(j);
                     if (toOracleConstraintClause != null) {
                        ConstraintType changeConstraintType = toOracleConstraintClause.getConstraintType();
                        Vector colNames;
                        int k;
                        String colName;
                        if (changeConstraintType instanceof PrimaryOrUniqueConstraintClause) {
                           PrimaryOrUniqueConstraintClause primaryOrUniqueConstraintClause = (PrimaryOrUniqueConstraintClause)changeConstraintType;
                           colNames = primaryOrUniqueConstraintClause.getConstraintColumnNames();
                           if (colNames != null) {
                              for(k = 0; k < colNames.size(); ++k) {
                                 colName = (String)colNames.get(k);
                                 fkTableName = (String)truncatedNames.get(colName);
                                 if (fkTableName != null) {
                                    colNames.setElementAt(fkTableName, k);
                                 }
                              }
                           }
                        } else if (changeConstraintType instanceof ForeignConstraintClause) {
                           ForeignConstraintClause foreignConstraintClause = (ForeignConstraintClause)changeConstraintType;
                           colNames = foreignConstraintClause.getConstraintColumnNames();
                           if (colNames != null) {
                              for(k = 0; k < colNames.size(); ++k) {
                                 colName = (String)colNames.get(k);
                                 fkTableName = (String)truncatedNames.get(colName);
                                 if (fkTableName != null) {
                                    colNames.setElementAt(fkTableName, k);
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      if (this.getSelectQueryStatement() != null) {
         cqs.setSelectQueryStatement(this.selectQueryStatement.toOracle());
      }

      if (this.getWithStatement() != null) {
         cqs.setSelectQueryStatement((SelectQueryStatement)this.withStatement.toOracle());
         cqs.setWithStatement((WithStatement)null);
      }

      cqs.setTypeIdentifier((String)null);
      cqs.setTypeString((String)null);
      String name;
      if (cqs.getPhysicalCharacteristics() != null) {
         name = cqs.getPhysicalCharacteristics();
      }

      if (cqs.getCreateSequence() != null) {
         CreateSequenceStatement tempCreateSequence = cqs.getCreateSequence();
         CreateSequenceStatement createSequenceObj = tempCreateSequence.toOracle();
         cqs.setCreateSequenceStatement(createSequenceObj);
      }

      if (cqs.getCreateSynonym() != null) {
         CreateSynonymStatement tempCreateSynonym = cqs.getCreateSynonym();
         tempCreateSynonym.setCreate("CREATE");
         if (this.createOrReplace != null) {
            tempCreateSynonym.setCreateOrReplace(this.createOrReplace);
         }

         createSynonymObj = tempCreateSynonym.toOracleString();
         cqs.setCreateSynonymString(createSynonymObj);
      }

      if (cqs.getPhysicalAttributesVector() != null) {
         temp_Vector = new Vector();

         for(countLong = 0; countLong < cqs.getPhysicalAttributesVector().size(); ++countLong) {
            PhysicalAttributesClause temp_PhysicalAttributesClause = (PhysicalAttributesClause)cqs.getPhysicalAttributesVector().get(countLong);
            temp_Vector.add(temp_PhysicalAttributesClause.toOracle());
         }

         cqs.setPhysicalAttributesVector(temp_Vector);
      }

      cqs.setIgnoreOrReplace((String)null);
      cqs.setSelectStatementString((String)null);
      name = "";
      if (this.tableObject != null) {
         this.tableObject.setObjectContext(this.context);
         name = this.tableObject.toString();
      }

      if (this.tableOrView != null) {
         if (this.tableOrView.equalsIgnoreCase("TABLE")) {
            this.dropString = "BEGIN \nEXECUTE IMMEDIATE 'DROP " + this.tableOrView + " " + name + " CASCADE CONSTRAINTS';\nEXCEPTION WHEN OTHERS THEN NULL;\nEND;\n/\n\n";
         } else {
            this.dropString = "BEGIN \nEXECUTE IMMEDIATE 'DROP " + this.tableOrView + " " + name + "';\nEXCEPTION WHEN OTHERS THEN NULL;\nEND;\n/\n\n";
         }
      } else if (this.createIndexClause != null) {
         if (this.createIndexClause.getIndexName() != null) {
            this.createIndexClause.getIndexName().setObjectContext(this.context);
            name = this.createIndexClause.getIndexName().toString();
         }

         this.dropString = "BEGIN \nEXECUTE IMMEDIATE 'DROP INDEX " + name + "';\nEXCEPTION WHEN OTHERS THEN NULL;\nEND;\n/\n\n";
      }

      cqs.setDropString(this.dropString);
      cqs.setModifiedObject(this.modifiedObjects);
      return cqs;
   }

   private void columnNamesToQuotedIdentifier(Vector colNames, String[] keywords, String origTableName) {
      if (colNames != null) {
         for(int k = 0; k < colNames.size(); ++k) {
            Object obj = colNames.get(k);
            if (obj instanceof String) {
               String origCol = (String)obj;
               if (origCol != null) {
                  ModifiedObjectAttr modifiedAttr = new ModifiedObjectAttr();
                  String modifiedCol = CustomizeUtil.objectNamesToQuotedIdentifier(origCol, keywords, modifiedAttr, 1);
                  colNames.setElementAt(modifiedCol, k);
                  if (!modifiedCol.equals(origCol)) {
                     modifiedAttr.setTableName(origTableName);
                     modifiedAttr.setOriginalName(origCol);
                     modifiedAttr.setModifiedName(modifiedCol);
                     this.modifiedObjects.addModifiedColumns(modifiedAttr);
                  }
               }
            }
         }
      }

   }

   private boolean removeCheckConstrWithDetFunctions(Vector getConstraintVector, CreateColumn changeCreateColumn, Vector columnNamesVector, int i) {
      if (getConstraintVector != null) {
         for(int j = 0; j < getConstraintVector.size(); ++j) {
            ConstraintClause toConstraintClause = (ConstraintClause)getConstraintVector.get(j);
            if (toConstraintClause != null && toConstraintClause.getConstraintType() instanceof CheckConstraintClause) {
               CheckConstraintClause ccc = (CheckConstraintClause)toConstraintClause.getConstraintType();
               boolean remove = false;
               WhereExpression we = ccc.getWhereExpression();
               Vector v = we.getWhereItems();
               int size = v.size();

               for(int k = 0; k < size; ++k) {
                  Object val = v.get(k);
                  if (val instanceof WhereItem) {
                     WhereItem wi = (WhereItem)val;
                     WhereColumn wcl = wi.getLeftWhereExp();
                     Vector col = wcl.getColumnExpression();
                     if (col != null) {
                        for(int h = 0; h < col.size(); ++h) {
                           Object obj = col.get(h);
                           if (obj instanceof FunctionCalls) {
                              FunctionCalls fc = (FunctionCalls)obj;
                              TableColumn tc = fc.getFunctionName();
                              if (tc != null) {
                                 String name = tc.getColumnName();
                                 if (name.equalsIgnoreCase("getdate")) {
                                    remove = true;
                                 }
                              }
                           }
                        }
                     }

                     WhereColumn wcr = wi.getRightWhereExp();
                     if (wcr != null) {
                        Vector colr = wcr.getColumnExpression();
                        if (colr != null) {
                           for(int h = 0; h < colr.size(); ++h) {
                              Object obj = colr.get(h);
                              if (obj instanceof FunctionCalls) {
                                 FunctionCalls fc = (FunctionCalls)obj;
                                 TableColumn tc = fc.getFunctionName();
                                 if (tc != null) {
                                    String name = tc.getColumnName();
                                    if (name.equalsIgnoreCase("getdate")) {
                                       remove = true;
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }

               if (remove) {
                  if (changeCreateColumn.getColumnName() == null) {
                     columnNamesVector.remove(i);
                     return true;
                  }

                  getConstraintVector.remove(j);
               }
            }
         }
      }

      return false;
   }

   public String toBigQueryString() throws ConvertException {
      return this.toBigQueryCreate().toString();
   }

   public CreateQueryStatement toBigQueryCreate() throws ConvertException {
      commentWhenConstraintNameTruncated = "";
      CreateQueryStatement cqs = this.copyObjectValues();
      cqs.setForce((String)null);
      cqs.setHeap((String)null);
      cqs.setPhysicalCharacteristics((String)null);
      cqs.setCreateOrReplace((String)null);
      cqs.setDiskAttributes((String)null);
      cqs.setConstraint((String)null);
      cqs.setConstraintName((String)null);
      cqs.setOnForQuotedIdentifier((String)null);
      cqs.setQuotedIdentifierCondition((String)null);
      cqs.setMaterialized((String)null);
      cqs.setViewMetaAttribute((String)null);
      cqs.setWithReadOnly((String)null);
      cqs.setLock((String)null);
      cqs.setLockData((String)null);
      cqs.setExternalTable((String)null);
      cqs.setExternalDefaultDirectory((String)null);
      cqs.setExternalDelimiter((String)null);
      String indexString;
      if (cqs.getTemp() != null) {
         indexString = cqs.getTemp();
         if (indexString.equalsIgnoreCase("GLOBAL TEMPORARY") || indexString.equalsIgnoreCase("LOCAL TEMPORARY")) {
            cqs.setTemp("TEMPORARY");
         }
      }

      String createSynonymObj;
      if (cqs.getTableObject() != null) {
         TableObject cqsTableObject = cqs.getTableObject();
         createSynonymObj = cqsTableObject.getOwner();
         String userName = cqsTableObject.getUser();
         String tableName = cqsTableObject.getTableName();
         if (createSynonymObj != null && (createSynonymObj.startsWith("[") && createSynonymObj.endsWith("]") || createSynonymObj.startsWith("\"") && createSynonymObj.endsWith("\""))) {
            createSynonymObj = createSynonymObj.substring(1, createSynonymObj.length() - 1);
            if (createSynonymObj.indexOf(32) != -1) {
               createSynonymObj = "`" + createSynonymObj + "`";
            }
         }

         if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("\"") && userName.endsWith("\""))) {
            userName = userName.substring(1, userName.length() - 1);
            if (userName.indexOf(32) != -1) {
               userName = "`" + userName + "`";
            }
         }

         if (tableName != null && (tableName.startsWith("[") && tableName.endsWith("]") || tableName.startsWith("\"") && tableName.endsWith("\""))) {
            tableName = tableName.substring(1, tableName.length() - 1);
            if (tableName.indexOf(32) != -1) {
               tableName = "`" + tableName + "`";
            }
         }

         cqsTableObject.setOwner(createSynonymObj);
         cqsTableObject.setUser(userName);
         cqsTableObject.setTableName(tableName);
         cqsTableObject.toBigQuery();
      }

      if (cqs.getCreateIndexClause() != null) {
         CreateIndexClause createIndexClauseObject = cqs.getCreateIndexClause();
         cqs.setCreateIndexClause(createIndexClauseObject.toBigQuery());
      }

      if (cqs.getCreateIndexVector() != null) {
         indexString = "";
         new Vector();

         for(int i = 0; i < cqs.getCreateIndexVector().size(); ++i) {
            CreateIndexClause createIndexClauseObject = (CreateIndexClause)cqs.getCreateIndexVector().get(i);
            if (!this.existsPKasIndex(createIndexClauseObject, cqs)) {
               createIndexClauseObject.setIndexOrKey("CREATE INDEX");
               createIndexClauseObject.setOn("ON");
               createIndexClauseObject.setTableOrView(this.tableObject);
               CreateIndexClause ansiIndexClause = createIndexClauseObject.toBigQuery();
               indexString = indexString + "\n/* SwisSQL Message : Query split into multiple Queries.*/\n" + ansiIndexClause.toString();
            }
         }

         cqs.setIndexString("\n" + indexString);
         cqs.setCreateIndexVector((Vector)null);
      }

      if (cqs.getOnCondition() != null && cqs.getOnCondition().startsWith("ON DEFAULT")) {
         cqs.setOnCondition((String)null);
      }

      if (cqs.getColumnNames() != null) {
         Vector columnNamesVector = cqs.getColumnNames();

         for(int i = 0; i < columnNamesVector.size(); ++i) {
            if (columnNamesVector.get(i) instanceof CreateColumn) {
               CreateColumn changeCreateColumn = (CreateColumn)columnNamesVector.get(i);
               if (cqs.getTableObject() != null) {
                  changeCreateColumn.setTableNameFromCreateQueryStmt(cqs.getTableObject().getTableName());
                  changeCreateColumn.setDatatypeMapping(this.mapping);
               }

               changeCreateColumn.toBigQueryString();
            }
         }
      }

      if (this.getSelectQueryStatement() != null) {
         cqs.setSelectQueryStatement(this.selectQueryStatement.toBigQuery());
      }

      if (this.getWithStatement() != null) {
         cqs.setSelectQueryStatement((SelectQueryStatement)this.withStatement.toBigQuery());
         cqs.setWithStatement((WithStatement)null);
      }

      cqs.setTypeIdentifier((String)null);
      cqs.setTypeString((String)null);
      if (cqs.getCreateSequence() != null) {
         CreateSequenceStatement tempCreateSequence = cqs.getCreateSequence();
         CreateSequenceStatement createSequenceObj = tempCreateSequence.toBigQuery();
         cqs.setCreateSequenceStatement(createSequenceObj);
      }

      if (cqs.getCreateSynonym() != null) {
         CreateSynonymStatement tempCreateSynonym = cqs.getCreateSynonym();
         tempCreateSynonym.setCreate("CREATE");
         if (this.createOrReplace != null) {
            tempCreateSynonym.setCreateOrReplace(this.createOrReplace);
         }

         createSynonymObj = tempCreateSynonym.toBigQueryString();
         cqs.setCreateSynonymString(createSynonymObj);
      }

      cqs.setPhysicalAttributesVector((Vector)null);
      cqs.setIgnoreOrReplace((String)null);
      cqs.setSelectStatementString((String)null);
      return cqs;
   }

   public String toPostgreSQLString() throws ConvertException {
      return this.toPostgreSQLCreate().toString();
   }

   public CreateQueryStatement toPostgreSQLCreate() throws ConvertException {
      commentWhenConstraintNameTruncated = "";
      CreateQueryStatement cqs = this.copyObjectValues();
      cqs.setForce((String)null);
      cqs.setHeap((String)null);
      cqs.setPhysicalCharacteristics((String)null);
      cqs.setCreateOrReplace((String)null);
      cqs.setDiskAttributes((String)null);
      cqs.setConstraint((String)null);
      cqs.setConstraintName((String)null);
      cqs.setOnForQuotedIdentifier((String)null);
      cqs.setQuotedIdentifierCondition((String)null);
      cqs.setMaterialized((String)null);
      cqs.setViewMetaAttribute((String)null);
      cqs.setWithReadOnly((String)null);
      cqs.setLock((String)null);
      cqs.setLockData((String)null);
      cqs.setExternalTable((String)null);
      cqs.setExternalDefaultDirectory((String)null);
      cqs.setExternalDelimiter((String)null);
      String indexString;
      if (cqs.getTemp() != null) {
         indexString = cqs.getTemp();
         if (indexString.equalsIgnoreCase("GLOBAL TEMPORARY") || indexString.equalsIgnoreCase("LOCAL TEMPORARY")) {
            cqs.setTemp("TEMPORARY");
         }
      }

      String createSynonymObj;
      if (cqs.getTableObject() != null) {
         TableObject cqsTableObject = cqs.getTableObject();
         createSynonymObj = cqsTableObject.getOwner();
         String userName = cqsTableObject.getUser();
         String tableName = cqsTableObject.getTableName();
         if (createSynonymObj != null && (createSynonymObj.startsWith("[") && createSynonymObj.endsWith("]") || createSynonymObj.startsWith("`") && createSynonymObj.endsWith("`"))) {
            createSynonymObj = createSynonymObj.substring(1, createSynonymObj.length() - 1);
            if (createSynonymObj.indexOf(32) != -1) {
               createSynonymObj = "\"" + createSynonymObj + "\"";
            }
         }

         if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
            userName = userName.substring(1, userName.length() - 1);
            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }
         }

         if (tableName != null && (tableName.startsWith("[") && tableName.endsWith("]") || tableName.startsWith("`") && tableName.endsWith("`"))) {
            tableName = tableName.substring(1, tableName.length() - 1);
            if (tableName.indexOf(32) != -1) {
               tableName = "\"" + tableName + "\"";
            }
         }

         cqsTableObject.setOwner(createSynonymObj);
         cqsTableObject.setUser(userName);
         cqsTableObject.setTableName(tableName);
         cqsTableObject.toPostgreSQL();
      }

      if (cqs.getCreateIndexClause() != null) {
         CreateIndexClause createIndexClauseObject = cqs.getCreateIndexClause();
         cqs.setCreateIndexClause(createIndexClauseObject.toPostgreSQL());
      }

      if (cqs.getCreateIndexVector() != null) {
         indexString = "";
         new Vector();

         for(int i = 0; i < cqs.getCreateIndexVector().size(); ++i) {
            CreateIndexClause createIndexClauseObject = (CreateIndexClause)cqs.getCreateIndexVector().get(i);
            if (!this.existsPKasIndex(createIndexClauseObject, cqs)) {
               createIndexClauseObject.setIndexOrKey("CREATE INDEX");
               createIndexClauseObject.setOn("ON");
               createIndexClauseObject.setTableOrView(this.tableObject);
               CreateIndexClause ansiIndexClause = createIndexClauseObject.toPostgreSQL();
               indexString = indexString + "\n/* SwisSQL Message : Query split into multiple Queries.*/\n" + ansiIndexClause.toString();
            }
         }

         cqs.setIndexString("\n" + indexString);
         cqs.setCreateIndexVector((Vector)null);
      }

      if (cqs.getOnCondition() != null && cqs.getOnCondition().startsWith("ON DEFAULT")) {
         cqs.setOnCondition((String)null);
      }

      if (cqs.getColumnNames() != null) {
         Vector columnNamesVector = cqs.getColumnNames();

         for(int i = 0; i < columnNamesVector.size(); ++i) {
            if (columnNamesVector.get(i) instanceof CreateColumn) {
               CreateColumn changeCreateColumn = (CreateColumn)columnNamesVector.get(i);
               if (cqs.getTableObject() != null) {
                  changeCreateColumn.setTableNameFromCreateQueryStmt(cqs.getTableObject().getTableName());
                  changeCreateColumn.setDatatypeMapping(this.mapping);
               }

               changeCreateColumn.toPostgreSQLString();
            }
         }
      }

      if (this.getSelectQueryStatement() != null) {
         cqs.setSelectQueryStatement(this.selectQueryStatement.toPostgreSQL());
      }

      if (this.getWithStatement() != null) {
         cqs.setSelectQueryStatement((SelectQueryStatement)this.withStatement.toPostgreSQL());
         cqs.setWithStatement((WithStatement)null);
      }

      cqs.setTypeIdentifier((String)null);
      cqs.setTypeString((String)null);
      if (cqs.getCreateSequence() != null) {
         CreateSequenceStatement tempCreateSequence = cqs.getCreateSequence();
         CreateSequenceStatement createSequenceObj = tempCreateSequence.toPostgreSQL();
         cqs.setCreateSequenceStatement(createSequenceObj);
      }

      if (cqs.getCreateSynonym() != null) {
         CreateSynonymStatement tempCreateSynonym = cqs.getCreateSynonym();
         tempCreateSynonym.setCreate("CREATE");
         if (this.createOrReplace != null) {
            tempCreateSynonym.setCreateOrReplace(this.createOrReplace);
         }

         createSynonymObj = tempCreateSynonym.toPostgreSQLString();
         cqs.setCreateSynonymString(createSynonymObj);
      }

      cqs.setPhysicalAttributesVector((Vector)null);
      cqs.setIgnoreOrReplace((String)null);
      cqs.setSelectStatementString((String)null);
      return cqs;
   }

   private void removeUniqueKeyConstrOnPKExists(Vector columnNamesVector) {
      ArrayList list = new ArrayList();

      for(int j = 0; j < columnNamesVector.size(); ++j) {
         if (columnNamesVector.get(j) instanceof CreateColumn) {
            CreateColumn changeCreateColumn = (CreateColumn)columnNamesVector.get(j);
            Vector constrClause = changeCreateColumn.getConstraintClause();
            if (constrClause != null) {
               for(int k = 0; k < constrClause.size(); ++k) {
                  ConstraintClause constraintClause = (ConstraintClause)constrClause.get(k);
                  if (constraintClause != null) {
                     ConstraintType constrType = constraintClause.getConstraintType();
                     if (constrType != null && constrType instanceof PrimaryOrUniqueConstraintClause) {
                        PrimaryOrUniqueConstraintClause tempPrimaryOrUniqueConstraintClause = (PrimaryOrUniqueConstraintClause)constrType;
                        if (tempPrimaryOrUniqueConstraintClause != null) {
                           Vector ukpkColNames = tempPrimaryOrUniqueConstraintClause.getConstraintColumnNames();
                           String ukpkConstraintName = tempPrimaryOrUniqueConstraintClause.getConstraintName();
                           int l;
                           if (ukpkColNames != null && ukpkConstraintName != null && (ukpkConstraintName.trim().equalsIgnoreCase("unique key") || ukpkConstraintName.trim().equalsIgnoreCase("unique")) && !list.isEmpty() && ukpkColNames.size() == list.size()) {
                              for(l = 0; l < ukpkColNames.size(); ++l) {
                                 if (list.get(l).toString().equalsIgnoreCase(ukpkColNames.get(l).toString()) && l == ukpkColNames.size() - 1) {
                                    columnNamesVector.remove(j);
                                    --j;
                                 }
                              }
                           }

                           if (ukpkColNames != null && ukpkConstraintName != null && ukpkConstraintName.trim().equalsIgnoreCase("primary key")) {
                              for(l = 0; l < ukpkColNames.size(); ++l) {
                                 list.add(ukpkColNames.get(l).toString().toLowerCase());
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

   }

   public String toTimesTenString() throws ConvertException {
      return this.toTimesTenCreate().toString();
   }

   public CreateQueryStatement toTimesTenCreate() throws ConvertException {
      commentWhenConstraintNameTruncated = "";
      CreateQueryStatement cqs = this.copyObjectValues();
      cqs.setSelectQueryStatement((SelectQueryStatement)null);
      cqs.setHeap((String)null);
      cqs.setTemp((String)null);
      cqs.setForce((String)null);
      cqs.setPhysicalCharacteristics((String)null);
      cqs.setCreateOrReplace((String)null);
      cqs.setConstraint((String)null);
      cqs.setConstraintName((String)null);
      cqs.setOnForQuotedIdentifier((String)null);
      cqs.setQuotedIdentifierCondition((String)null);
      cqs.setViewMetaAttribute((String)null);
      cqs.setWithReadOnly((String)null);
      cqs.setLock((String)null);
      cqs.setLockData((String)null);
      cqs.setExternalTable((String)null);
      cqs.setExternalDefaultDirectory((String)null);
      cqs.setExternalDelimiter((String)null);
      if (cqs.getDatabaseObject() != null) {
         throw new ConvertException("\n\nCREATE DATABASE syntax is not supported in TimesTen 5.1.21\n");
      } else if (cqs.getTemp() != null) {
         throw new ConvertException("\n\nSession specific Temporary Tables are not supported in TimesTen 5.1.21\n");
      } else if (cqs.getTableOrView() != null && cqs.getTableOrView().equalsIgnoreCase("view") && cqs.materialized == null) {
         throw new ConvertException("\n\nCREATE VIEW is not supported in TimesTen 5.1.21\n");
      } else {
         if (cqs.getOnCondition() != null) {
            cqs.setOnCondition((String)null);
         }

         if (cqs.getDiskAttributes() != null) {
            cqs.setDiskAttributes((String)null);
         }

         String indexString;
         String createSynonymObj;
         if (cqs.getTableObject() != null) {
            TableObject orgTableObject = cqs.getTableObject();
            indexString = orgTableObject.getOwner();
            createSynonymObj = orgTableObject.getTableName();
            if (indexString != null && (indexString.startsWith("[") && indexString.endsWith("]") || indexString.startsWith("`") && indexString.endsWith("`"))) {
               indexString = indexString.substring(1, indexString.length() - 1);
               if (indexString.indexOf(32) != -1) {
                  indexString = "\"" + indexString + "\"";
               }
            }

            if (createSynonymObj != null && (createSynonymObj.startsWith("[") && createSynonymObj.endsWith("]") || createSynonymObj.startsWith("`") && createSynonymObj.endsWith("`"))) {
               createSynonymObj = createSynonymObj.substring(1, createSynonymObj.length() - 1);
               if (createSynonymObj.indexOf(32) != -1) {
                  createSynonymObj = "\"" + createSynonymObj + "\"";
               }
            }

            if (createSynonymObj != null && createSynonymObj.startsWith("#")) {
               throw new ConvertException("\n\nSession specific Temporary Tables are not supported in TimesTen 5.1.21\n");
            }

            orgTableObject.setOwner(indexString);
            orgTableObject.setTableName(createSynonymObj);
            orgTableObject.toTimesTen();
         }

         int indexCount = 0;
         Vector constraintVector;
         TableObject refTable;
         if (cqs.getColumnNames() != null) {
            Vector columnNamesVector = cqs.getColumnNames();

            for(int i = 0; i < columnNamesVector.size(); ++i) {
               if (columnNamesVector.get(i) instanceof CreateColumn) {
                  CreateColumn changeCreateColumn = (CreateColumn)columnNamesVector.get(i);
                  changeCreateColumn.setColumnName(CustomizeUtil.objectNamesToQuotedIdentifier(changeCreateColumn.getColumnName(), SwisSQLUtils.getKeywords(10), (ModifiedObjectAttr)null, 10));
                  if (cqs.getTableObject() != null) {
                     changeCreateColumn.setTableNameFromCreateQueryStmt(cqs.getTableObject().getTableName());
                     changeCreateColumn.setDatatypeMapping(this.mapping);
                  }

                  constraintVector = changeCreateColumn.getConstraintClause();
                  boolean columnRemoved = false;
                  if (constraintVector != null) {
                     for(int j = 0; j < constraintVector.size(); ++j) {
                        ConstraintClause toTimesTenConstraintClause = (ConstraintClause)constraintVector.get(j);
                        ConstraintType toTimesTenConstraintType = toTimesTenConstraintClause.getConstraintType();
                        String consName;
                        Vector refCols;
                        if (toTimesTenConstraintType instanceof PrimaryOrUniqueConstraintClause) {
                           PrimaryOrUniqueConstraintClause pkClause = (PrimaryOrUniqueConstraintClause)toTimesTenConstraintType;
                           consName = pkClause.getConstraintName();
                           if (consName.toLowerCase().indexOf("unique") != -1) {
                              CreateIndexClause createIndexClause = new CreateIndexClause();
                              createIndexClause.setIndexOrKey("CREATE UNIQUE INDEX");
                              TableObject indexObject = new TableObject();
                              String indexName = cqs.getTableObject().getTableName();
                              if (indexName.length() > 22) {
                                 indexName = indexName.substring(0, 22);
                              }

                              StringBuilder var48 = (new StringBuilder()).append(indexName).append("_index_");
                              ++indexCount;
                              indexObject.setTableName(var48.append(indexCount).toString());
                              createIndexClause.setIndexName(indexObject);
                              createIndexClause.setOn("ON");
                              refCols = pkClause.getConstraintColumnNames();
                              ArrayList indexColumns = new ArrayList();
                              if (refCols != null && changeCreateColumn.getColumnName() == null) {
                                 for(int k = 0; k < refCols.size(); ++k) {
                                    String consColName = (String)refCols.get(k);
                                    indexColumns.add(this.createIndexColumn(consColName));
                                 }
                              } else if (changeCreateColumn.getColumnName() != null) {
                                 indexColumns.add(this.createIndexColumn(changeCreateColumn.getColumnName()));
                              }

                              createIndexClause.setIndexColumns(indexColumns);
                              createIndexClause.setTableOrView(cqs.getTableObject());
                              createIndexClause.setClosedBraces(")");
                              createIndexClause.setOpenBraces("(");
                              CreateIndexClause indexClause = createIndexClause.toTimesTen();
                              this.ttUniqueIndexforUniqueCons.add(indexClause);
                              if (changeCreateColumn.getColumnName() == null) {
                                 columnNamesVector.remove(i--);
                                 columnRemoved = true;
                              } else {
                                 constraintVector.remove(j--);
                              }
                           }
                        } else if (toTimesTenConstraintType instanceof CheckConstraintClause) {
                           if (commentWhenConstraintNameTruncated.trim().length() > 0 && commentWhenConstraintNameTruncated.indexOf("CHECK Constraint") == -1) {
                              commentWhenConstraintNameTruncated = commentWhenConstraintNameTruncated + "\n/* SwisSQL Message : CHECK Constraint is not supported in TimesTen 5.1.21 */";
                           } else if (commentWhenConstraintNameTruncated.indexOf("CHECK Constraint") == -1) {
                              commentWhenConstraintNameTruncated = "/* SwisSQL Message : CHECK Constraint is not supported in TimesTen 5.1.21 */";
                           }

                           if (changeCreateColumn.getColumnName() == null) {
                              columnNamesVector.remove(i--);
                              columnRemoved = true;
                           } else {
                              constraintVector.remove(j--);
                           }
                        } else if (toTimesTenConstraintType instanceof ForeignConstraintClause) {
                           ForeignConstraintClause tempForeignConstraintClause = (ForeignConstraintClause)toTimesTenConstraintType;
                           if (tempForeignConstraintClause.getConstraintName() == null && tempForeignConstraintClause.getReference() != null && changeCreateColumn.getColumnName() != null) {
                              if (commentWhenConstraintNameTruncated.trim().length() > 0 && commentWhenConstraintNameTruncated.indexOf("Referential Integrity Constraint") == -1) {
                                 commentWhenConstraintNameTruncated = commentWhenConstraintNameTruncated + "\n/* SwisSQL Message : Column-level Referential Integrity Constraint is not supported in TimesTen 5.1.21. Constraint definition moved at the level of the table. */";
                              } else if (commentWhenConstraintNameTruncated.indexOf("Referential Integrity Constraint") == -1) {
                                 commentWhenConstraintNameTruncated = "/* SwisSQL Message : Column-level Referential Integrity Constraint is not supported in TimesTen 5.1.21. Constraint definition moved at the level of the table. */";
                              }

                              if (toTimesTenConstraintClause.getConstraintName() == null) {
                                 toTimesTenConstraintClause.setConstraint("CONSTRAINT");
                                 consName = "_CT_" + ++SwisSQLAPI.truncateConstraintCount;
                                 if (changeCreateColumn.getColumnName().length() > 25) {
                                    consName = changeCreateColumn.getColumnName().substring(0, 25) + consName;
                                 } else {
                                    consName = changeCreateColumn.getColumnName() + consName;
                                 }

                                 toTimesTenConstraintClause.setConstraintName(consName);
                              }

                              tempForeignConstraintClause.setConstraintName("FOREIGN KEY");
                              Vector consColNames = new Vector();
                              consColNames.add(changeCreateColumn.getColumnName());
                              tempForeignConstraintClause.setConstraintColumnNames(consColNames);
                              tempForeignConstraintClause.setOpenBrace("(");
                              tempForeignConstraintClause.setClosedBrace(")");
                              if (tempForeignConstraintClause.getReferenceTableColumnNames() == null) {
                                 tempForeignConstraintClause.setReferenceOpenBrace("(");
                                 tempForeignConstraintClause.setReferenceClosedBrace(")");
                                 refTable = tempForeignConstraintClause.getTableName();
                                 Object obj = CastingUtil.getValueIgnoreCase(SwisSQLAPI.primaryKeyMetaData, refTable.getTableName());
                                 if (obj != null) {
                                    ArrayList colList = (ArrayList)obj;
                                    refCols = new Vector();

                                    for(int k = 0; k < colList.size(); ++k) {
                                       refCols.add(colList.get(k));
                                    }

                                    tempForeignConstraintClause.setReferenceTableColumnNames(refCols);
                                 } else if (commentWhenConstraintNameTruncated.trim().length() > 0 && commentWhenConstraintNameTruncated.indexOf("Metadata") == -1) {
                                    commentWhenConstraintNameTruncated = commentWhenConstraintNameTruncated + "\n/* SwisSQL Message : Metadata of the source database required for accurate conversion */";
                                 } else if (commentWhenConstraintNameTruncated.indexOf("Metadata") == -1) {
                                    commentWhenConstraintNameTruncated = "/* SwisSQL Message : Metadata of the source database required for accurate conversion */";
                                 }
                              }

                              CreateColumn ccForRefConstr = new CreateColumn();
                              Vector newConstrVector = new Vector();
                              newConstrVector.add(toTimesTenConstraintClause);
                              ccForRefConstr.setConstraintClause(newConstrVector);
                              columnNamesVector.add(ccForRefConstr);
                              constraintVector.remove(j--);
                           }
                        }
                     }
                  }

                  cqs.setTTUniqueIndicesForUniqCons(this.ttUniqueIndexforUniqueCons);
                  if (!columnRemoved) {
                     changeCreateColumn.toTimesTenString();
                  }
               }
            }
         }

         cqs.setForce((String)null);
         if (this.getSelectQueryStatement() != null && this.getTableOrView().equalsIgnoreCase("VIEW")) {
            cqs.setSelectQueryStatement(this.getSelectQueryStatement());
         }

         if (this.getWithStatement() != null && this.getTableOrView().equalsIgnoreCase("VIEW")) {
            cqs.setWithStatement((WithStatement)this.withStatement.toTimesTen());
         }

         if (this.getSelectQueryStatement() != null && this.getTableOrView().equalsIgnoreCase("TABLE")) {
            TableObject orgTableObject = cqs.getTableObject();
            if (orgTableObject != null) {
               createSynonymObj = orgTableObject.getTableName();
               cqs = SwisSQLUtils.constructCQS(createSynonymObj, this.getSelectQueryStatement(), (SwisSQLStatement)null);
               Vector selectColVector = cqs.getColumnNames();
               constraintVector = this.getColumnNames();

               for(int i = 0; i < selectColVector.size(); ++i) {
                  if (constraintVector != null && constraintVector.get(i) != null && selectColVector != null && selectColVector.get(i) != null) {
                     String colname = ((CreateColumn)constraintVector.get(i)).getColumnName();
                     ((CreateColumn)selectColVector.get(i)).setColumnName(colname);
                  }
               }

               cqs = cqs.toTimesTenCreate();
               InsertQueryStatement iqs = new InsertQueryStatement();
               InsertClause ic = new InsertClause();
               ic.setInsert("INSERT");
               OptionalSpecifier optionalSpecifier = new OptionalSpecifier();
               optionalSpecifier.setInto("INTO");
               ic.setOptionalSpecifier(optionalSpecifier);
               TableExpression texpr = new TableExpression();
               ArrayList newList = new ArrayList();
               TableClause tc = new TableClause();
               refTable = new TableObject();
               refTable.setTableName(createSynonymObj);
               tc.setTableObject(refTable);
               newList.add(tc);
               texpr.setTableClauseList(newList);
               ic.setTableExpression(texpr);
               iqs.setInsertClause(ic);
               cqs.setInsertQueryStatement(iqs);
               this.comment_flag__for_create_as_select_in_timesten = true;
               cqs.setSelectQueryStatement(this.getSelectQueryStatement());
            }
         }

         if (cqs.getCreateIndexClause() != null) {
            CreateIndexClause createIndexClauseObject = cqs.getCreateIndexClause();
            cqs.setCreateIndexClause(createIndexClauseObject.toTimesTen());
         }

         if (cqs.getCreateIndexVector() != null) {
            indexString = "";
            new Vector();

            for(int i = 0; i < cqs.getCreateIndexVector().size(); ++i) {
               CreateIndexClause createIndexClauseObject = (CreateIndexClause)cqs.getCreateIndexVector().get(i);
               if (!this.existsPKasIndex(createIndexClauseObject, cqs)) {
                  createIndexClauseObject.setIndexOrKey("CREATE INDEX");
                  createIndexClauseObject.setOn("ON");
                  createIndexClauseObject.setTableOrView(this.tableObject);
                  CreateIndexClause indexClause = createIndexClauseObject.toTimesTen();
                  indexString = indexString + ";\n/* SwisSQL Message : Query split into multiple Queries.*/\n" + indexClause.toString().trim();
               }
            }

            cqs.setIndexString(indexString);
            cqs.setCreateIndexVector((Vector)null);
         }

         cqs.setTypeIdentifier((String)null);
         cqs.setTypeString((String)null);
         if (cqs.getCreateSequence() != null) {
            CreateSequenceStatement tempCreateSequence = cqs.getCreateSequence();
            CreateSequenceStatement createSequenceObj = tempCreateSequence.toTimesTen();
            cqs.setCreateSequenceStatement(createSequenceObj);
         }

         if (cqs.getCreateSynonym() != null) {
            CreateSynonymStatement tempCreateSynonym = cqs.getCreateSynonym();
            tempCreateSynonym.setCreate("CREATE");
            if (this.createOrReplace != null) {
               tempCreateSynonym.setCreateOrReplace(this.createOrReplace);
            }

            createSynonymObj = tempCreateSynonym.toTimesTenString();
            cqs.setCreateSynonymString(createSynonymObj);
         }

         cqs.setPhysicalAttributesVector((Vector)null);
         cqs.setIgnoreOrReplace((String)null);
         cqs.setSelectStatementString((String)null);
         return cqs;
      }
   }

   public String toNetezzaString() throws ConvertException {
      return this.toNetezzaCreate().toString();
   }

   public CreateQueryStatement toNetezzaCreate() throws ConvertException {
      commentWhenConstraintNameTruncated = "";
      CreateQueryStatement cqs = this.copyObjectValues();
      cqs.setOnForQuotedIdentifier((String)null);
      cqs.setQuotedIdentifierCondition((String)null);
      cqs.setViewMetaAttribute((String)null);
      cqs.setWithReadOnly((String)null);
      cqs.setWithCheckOption((String)null);
      cqs.setLock((String)null);
      cqs.setLockData((String)null);
      cqs.setStartWith(this.startWith);
      cqs.setStartWithFunction(this.startWithFunction);
      cqs.setNextString(this.nextString);
      cqs.setSelectColumnInNextClause(this.selectColumnInNextClause);
      int i;
      if (cqs.getColumnNames() != null) {
         int lobDatatypeCount = 0;
         Vector columnNamesVector = cqs.getColumnNames();

         for(i = 0; i < columnNamesVector.size(); ++i) {
            if (columnNamesVector.get(i) instanceof CreateColumn) {
               CreateColumn changeCreateColumn = (CreateColumn)columnNamesVector.get(i);
               if (cqs.getTableObject() != null) {
                  changeCreateColumn.setTableNameFromCreateQueryStmt(cqs.getTableObject().getTableName());
                  changeCreateColumn.setDatatypeMapping(this.mapping);
               }

               Vector constraintVector = changeCreateColumn.getConstraintClause();
               boolean columnRemoved = false;
               if (constraintVector != null) {
                  for(int j = 0; j < constraintVector.size(); ++j) {
                     ConstraintClause toTimesTenConstraintClause = (ConstraintClause)constraintVector.get(j);
                     ConstraintType toTimesTenConstraintType = toTimesTenConstraintClause.getConstraintType();
                     if (toTimesTenConstraintType instanceof CheckConstraintClause) {
                        if (commentWhenConstraintNameTruncated.trim().length() > 0 && commentWhenConstraintNameTruncated.indexOf("CHECK Constraint") == -1) {
                           commentWhenConstraintNameTruncated = commentWhenConstraintNameTruncated + "\n/* SwisSQL Message : CHECK Constraint is not supported in Netezza */";
                        } else if (commentWhenConstraintNameTruncated.indexOf("CHECK Constraint") == -1) {
                           commentWhenConstraintNameTruncated = "/* SwisSQL Message : CHECK Constraint is not supported in Netezza */";
                        }

                        if (changeCreateColumn.getColumnName() == null) {
                           columnNamesVector.remove(i--);
                           columnRemoved = true;
                        } else {
                           constraintVector.remove(j--);
                        }
                     }
                  }
               }

               changeCreateColumn.toNetezzaString();
               if (changeCreateColumn.getDatatype() != null) {
                  String dataTypeSize = changeCreateColumn.getDatatype().getSize();
                  if (dataTypeSize != null) {
                     try {
                        int datatype_size = Integer.parseInt(dataTypeSize);
                        lobDatatypeCount += datatype_size;
                     } catch (NumberFormatException var11) {
                     }
                  }
               }
            }
         }

         if (lobDatatypeCount >= 64000) {
            SwisSQLUtils.swissqlMessageList.add("The tuple size of the tables is more than 64k. Manual intervention required.");
         }
      }

      String indexString;
      if (cqs.getTemp() != null) {
         indexString = cqs.getTemp();
         cqs.setTemp("TEMPORARY");
      }

      String createSynonymObj;
      if (cqs.getTableObject() != null) {
         TableObject cqsTableObject = cqs.getTableObject();
         createSynonymObj = cqsTableObject.getOwner();
         String userName = cqsTableObject.getUser();
         String tableName = cqsTableObject.getTableName();
         if (createSynonymObj != null && (createSynonymObj.startsWith("[") && createSynonymObj.endsWith("]") || createSynonymObj.startsWith("`") && createSynonymObj.endsWith("`"))) {
            createSynonymObj = createSynonymObj.substring(1, createSynonymObj.length() - 1);
            if (createSynonymObj.indexOf(32) != -1) {
               createSynonymObj = "\"" + createSynonymObj + "\"";
            }
         }

         if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
            userName = userName.substring(1, userName.length() - 1);
            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }
         }

         if (tableName != null && (tableName.startsWith("[") && tableName.endsWith("]") || tableName.startsWith("`") && tableName.endsWith("`"))) {
            tableName = tableName.substring(1, tableName.length() - 1);
            if (tableName.indexOf(32) != -1) {
               tableName = "\"" + tableName + "\"";
            }
         }

         tableName = CustomizeUtil.objectNamesToQuotedIdentifier(tableName, SwisSQLUtils.getKeywords(11), (ModifiedObjectAttr)null, 11);
         cqsTableObject.setOwner(createSynonymObj);
         cqsTableObject.setUser(userName);
         cqsTableObject.setTableName(tableName);
         cqsTableObject.toNetezza();
      }

      if (cqs.getOnCondition() != null) {
         cqs.setOnCondition((String)null);
      }

      cqs.setForce((String)null);
      cqs.setHeap((String)null);
      cqs.setDiskAttributes((String)null);
      cqs.setPhysicalCharacteristics((String)null);
      cqs.setConstraint((String)null);
      cqs.setConstraintName((String)null);
      if (cqs.getSelectQueryStatement() != null) {
         if (cqs.getTableOrView() != null && cqs.getTableOrView().equalsIgnoreCase("VIEW")) {
            if (cqs.getColumnNames() != null) {
               cqs.setColumnNames((Vector)null);
               cqs.setOpenBraces((String)null);
               cqs.setClosedBraces((String)null);
            }

            if (cqs.getMaterialized() != null) {
               cqs.getSelectQueryStatement().setWhereExpression((WhereExpression)null);
            }
         }

         cqs.setSelectQueryStatement(this.selectQueryStatement.toNetezza());
      }

      if (this.getWithStatement() != null) {
         cqs.setSelectQueryStatement((SelectQueryStatement)this.withStatement.toNetezza());
         cqs.setWithStatement((WithStatement)null);
      }

      if (cqs.getCreateIndexClause() != null) {
         CreateIndexClause createIndexClauseObject = cqs.getCreateIndexClause();
         cqs.setCreateIndexClause(createIndexClauseObject.toNetezza());
      }

      if (cqs.getCreateIndexVector() != null) {
         indexString = "";
         new Vector();

         for(i = 0; i < cqs.getCreateIndexVector().size(); ++i) {
            CreateIndexClause createIndexClauseObject = (CreateIndexClause)cqs.getCreateIndexVector().get(i);
            if (!this.existsPKasIndex(createIndexClauseObject, cqs)) {
               createIndexClauseObject.setIndexOrKey("CREATE INDEX");
               createIndexClauseObject.setOn("ON");
               createIndexClauseObject.setTableOrView(this.tableObject);
               CreateIndexClause netezzaIndexClause = createIndexClauseObject.toNetezza();
               indexString = indexString + ";\n/* SwisSQL Message : Query split into multiple Queries.*/\n" + netezzaIndexClause.toString().trim();
            }
         }

         cqs.setIndexString(indexString);
         cqs.setCreateIndexVector((Vector)null);
      }

      cqs.setTypeIdentifier((String)null);
      cqs.setTypeString((String)null);
      if (cqs.getCreateSequence() != null) {
         CreateSequenceStatement tempCreateSequence = cqs.getCreateSequence();
         CreateSequenceStatement createSequenceObj = tempCreateSequence.toNetezza();
         cqs.setCreateSequenceStatement(createSequenceObj);
      }

      if (cqs.getCreateSynonym() != null) {
         CreateSynonymStatement tempCreateSynonym = cqs.getCreateSynonym();
         tempCreateSynonym.setCreate("CREATE");
         if (this.createOrReplace != null) {
            tempCreateSynonym.setCreateOrReplace(this.createOrReplace);
         }

         createSynonymObj = tempCreateSynonym.toNetezzaString();
         cqs.setCreateSynonymString(createSynonymObj);
      }

      cqs.setPhysicalAttributesVector((Vector)null);
      cqs.setIgnoreOrReplace((String)null);
      cqs.setSelectStatementString((String)null);
      if (cqs.getTableOrView() != null && cqs.getTableOrView().equalsIgnoreCase("TABLE")) {
         cqs.setDistributeOnRandomClause("DISTRIBUTE ON RANDOM");
      }

      if (cqs.getExternalTable() != null) {
         cqs.setDistributeOnRandomClause((String)null);
      }

      return cqs;
   }

   public String toTeradataString() throws ConvertException {
      return this.toTeradataCreate().toString();
   }

   public CreateQueryStatement toTeradataCreate() throws ConvertException {
      commentWhenConstraintNameTruncated = "";
      CreateQueryStatement cqs = this.copyObjectValues();
      cqs.setCreateOrReplace((String)null);
      cqs.setOnForQuotedIdentifier((String)null);
      cqs.setQuotedIdentifierCondition((String)null);
      cqs.setMaterialized((String)null);
      cqs.setViewMetaAttribute((String)null);
      cqs.setWithReadOnly((String)null);
      cqs.setLock((String)null);
      cqs.setLockData((String)null);
      cqs.setExternalTable((String)null);
      cqs.setExternalDefaultDirectory((String)null);
      cqs.setExternalDelimiter((String)null);
      if (cqs.getCommentClass() != null) {
         cqs.getCommentClass().setSQLDialect(12);
      }

      if (cqs.getColumnNames() != null) {
         Vector columnNamesVector = cqs.getColumnNames();

         for(int i = 0; i < columnNamesVector.size(); ++i) {
            if (columnNamesVector.get(i) instanceof CreateColumn) {
               CreateColumn changeCreateColumn = (CreateColumn)columnNamesVector.get(i);
               if (cqs.getTableObject() != null) {
                  changeCreateColumn.setTableNameFromCreateQueryStmt(cqs.getTableObject().getTableName());
                  changeCreateColumn.setDatatypeMapping(this.mapping);
               }

               changeCreateColumn.toTeradataString();
               if (cqs.getTemp() != null && SwisSQLOptions.convertTemporaryTableToPermanentTable && changeCreateColumn.getDatatype() instanceof CharacterClass && changeCreateColumn.getDatatype().getDatatypeName() != null && (changeCreateColumn.getDatatype().getDatatypeName().equalsIgnoreCase("char") || changeCreateColumn.getDatatype().getDatatypeName().equalsIgnoreCase("varchar"))) {
                  ((CharacterClass)changeCreateColumn.getDatatype()).setCaseSpecificPhrase("CASESPECIFIC");
               }

               if (changeCreateColumn.getComputedColumnExpression() != null) {
                  this.computedColumnsVector.add(columnNamesVector.get(i));
                  columnNamesVector.removeElementAt(i);
                  --i;
               }
            }
         }
      }

      String createSynonymObj;
      if (cqs.getTableObject() != null) {
         TableObject cqsTableObject = cqs.getTableObject();
         createSynonymObj = cqsTableObject.getOwner();
         String userName = cqsTableObject.getUser();
         String tableName = cqsTableObject.getTableName();
         if (createSynonymObj != null && (createSynonymObj.startsWith("[") && createSynonymObj.endsWith("]") || createSynonymObj.startsWith("`") && createSynonymObj.endsWith("`"))) {
            createSynonymObj = createSynonymObj.substring(1, createSynonymObj.length() - 1);
            if (createSynonymObj.indexOf(32) != -1) {
               createSynonymObj = "\"" + createSynonymObj + "\"";
            }

            createSynonymObj = CustomizeUtil.objectNamesToQuotedIdentifier(createSynonymObj, SwisSQLUtils.getKeywords("teradata"), (ModifiedObjectAttr)null, -1);
         }

         if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
            userName = userName.substring(1, userName.length() - 1);
            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }

            userName = CustomizeUtil.objectNamesToQuotedIdentifier(userName, SwisSQLUtils.getKeywords("teradata"), (ModifiedObjectAttr)null, -1);
         }

         if (tableName != null && (tableName.startsWith("[") && tableName.endsWith("]") || tableName.startsWith("`") && tableName.endsWith("`"))) {
            tableName = tableName.substring(1, tableName.length() - 1);
            if (tableName.indexOf(32) != -1) {
               tableName = "\"" + tableName + "\"";
            }

            tableName = CustomizeUtil.objectNamesToQuotedIdentifier(tableName, SwisSQLUtils.getKeywords("teradata"), (ModifiedObjectAttr)null, -1);
         }

         cqsTableObject.setOwner(createSynonymObj);
         cqsTableObject.setUser(userName);
         cqsTableObject.setTableName(tableName);
         cqsTableObject.toTeradata();
      }

      String indexString;
      if (cqs.getTemp() != null) {
         if (!SwisSQLOptions.convertTemporaryTableToPermanentTable && SwisSQLOptions.oracleTempTableToTDConversionFormat != 2) {
            if (SwisSQLOptions.oracleTempTableToTDConversionFormat == 0) {
               indexString = cqs.getTemp();
               if (indexString.equalsIgnoreCase("TEMP") || indexString.equalsIgnoreCase("TEMPORARY") || indexString.equalsIgnoreCase("GLOBAL TEMPORARY")) {
                  cqs.setTemp("GLOBAL TEMPORARY");
                  cqs.noLogConditionForTeradata = ", NO LOG";
                  cqs.onConditionForTeradata = "WITH DATA";
                  InsertQueryStatement iqs = new InsertQueryStatement();
                  InsertClause ic = new InsertClause();
                  ic.setInsert("INSERT");
                  OptionalSpecifier optionalSpecifier = new OptionalSpecifier();
                  optionalSpecifier.setInto("INTO");
                  ic.setOptionalSpecifier(optionalSpecifier);
                  TableExpression texpr = new TableExpression();
                  ArrayList newList = new ArrayList();
                  TableClause tc = new TableClause();
                  tc.setTableObject(cqs.getTableObject());
                  newList.add(tc);
                  texpr.setTableClauseList(newList);
                  ic.setTableExpression(texpr);
                  iqs.setInsertClause(ic);
                  cqs.setInsertQueryStatement(iqs);
               }
            } else {
               indexString = cqs.getTemp();
               if (indexString.equalsIgnoreCase("TEMP") || indexString.equalsIgnoreCase("TEMPORARY") || indexString.equalsIgnoreCase("GLOBAL TEMPORARY")) {
                  cqs.setTemp("VOLATILE");
                  cqs.noLogConditionForTeradata = ", NO LOG";
               }
            }
         } else {
            cqs.setTemp((String)null);
            cqs.setOnCondition((String)null);
            if (this.getSelectQueryStatement() != null) {
               cqs.onConditionForTeradata = "WITH DATA";
            }
         }
      }

      if (cqs.getOnCondition() != null) {
         if (this.getSelectQueryStatement() != null) {
            if (SwisSQLOptions.oracleTempTableToTDConversionFormat == 0) {
               cqs.onConditionForTeradata = "WITH NO DATA\n" + cqs.getOnCondition();
            } else {
               cqs.onConditionForTeradata = "WITH DATA\n" + cqs.getOnCondition();
            }

            cqs.setOnCondition((String)null);
         } else {
            cqs.onConditionForTeradata = cqs.getOnCondition();
            cqs.setOnCondition((String)null);
         }
      }

      cqs.setForce((String)null);
      cqs.setHeap((String)null);
      cqs.setDiskAttributes((String)null);
      cqs.setPhysicalCharacteristics((String)null);
      cqs.setConstraint((String)null);
      cqs.setConstraintName((String)null);
      if (this.getSelectQueryStatement() != null) {
         SelectQueryStatement teradataSQS = this.selectQueryStatement.toTeradata();
         cqs.setOpenBraceForSelectQuery("(");
         cqs.setSelectQueryStatement(teradataSQS);
         cqs.setCloseBraceForSelectQuery(")");
         if (cqs.getInsertQueryStatement() != null && cqs.getInsertQueryStatement().getSubQuery() == null) {
            cqs.getInsertQueryStatement().setSubQuery(teradataSQS);
         }
      }

      if (cqs.getCreateIndexClause() != null) {
         CreateIndexClause createIndexClauseObject = cqs.getCreateIndexClause();
         cqs.setCreateIndexClause(createIndexClauseObject.toTeradata());
      }

      if (cqs.getCreateIndexVector() != null) {
         indexString = "";
         new Vector();

         for(int i = 0; i < cqs.getCreateIndexVector().size(); ++i) {
            CreateIndexClause createIndexClauseObject = (CreateIndexClause)cqs.getCreateIndexVector().get(i);
            if (!this.existsPKasIndex(createIndexClauseObject, cqs)) {
               createIndexClauseObject.setIndexOrKey("CREATE INDEX");
               createIndexClauseObject.setOn("ON");
               createIndexClauseObject.setTableOrView(this.tableObject);
               CreateIndexClause TeradataIndexClause = createIndexClauseObject.toTeradata();
               indexString = indexString + ";\n/* SwisSQL Message : Query split into multiple Queries.*/\n" + TeradataIndexClause.toString().trim();
            }
         }

         cqs.setIndexString(indexString);
         cqs.setCreateIndexVector((Vector)null);
      }

      cqs.setTypeIdentifier((String)null);
      cqs.setTypeString((String)null);
      if (cqs.getCreateSequence() != null) {
         CreateSequenceStatement tempCreateSequence = cqs.getCreateSequence();
         CreateSequenceStatement createSequenceObj = tempCreateSequence.toTeradata();
         cqs.setCreateSequenceStatement(createSequenceObj);
      }

      if (cqs.getCreateSynonym() != null) {
         CreateSynonymStatement tempCreateSynonym = cqs.getCreateSynonym();
         tempCreateSynonym.setCreate("CREATE");
         if (this.createOrReplace != null) {
            tempCreateSynonym.setCreateOrReplace(this.createOrReplace);
         }

         createSynonymObj = tempCreateSynonym.toTeradataString();
         cqs.setCreateSynonymString(createSynonymObj);
      }

      cqs.setPhysicalAttributesVector((Vector)null);
      cqs.setIgnoreOrReplace((String)null);
      cqs.setSelectStatementString((String)null);
      if (this.computedColumnsVector.size() >= 1) {
         this.computedColumns(cqs);
      }

      return cqs;
   }

   private IndexColumn createIndexColumn(String consColName) {
      IndexColumn indexCol = new IndexColumn();
      SelectColumn sc = new SelectColumn();
      Vector colExpr = new Vector();
      TableColumn tc = new TableColumn();
      tc.setColumnName(consColName);
      colExpr.add(tc);
      sc.setColumnExpression(colExpr);
      indexCol.setIndexColumnName(sc);
      return indexCol;
   }

   public CreateQueryStatement copyObjectValues() {
      Vector createColumnVector = new Vector();
      CreateQueryStatement dupCreateQueryStatement = new CreateQueryStatement();
      dupCreateQueryStatement.setCreate(this.getCreate());
      dupCreateQueryStatement.setObjectContext(this.context);
      dupCreateQueryStatement.setCommentClass(this.commentObject);
      dupCreateQueryStatement.setCreateOrReplace(this.getCreateOrReplace());
      dupCreateQueryStatement.setTableOrView(this.getTableOrView());
      dupCreateQueryStatement.setAs(this.getAs());
      dupCreateQueryStatement.setForce(this.getForce());
      dupCreateQueryStatement.setTemp(this.getTemp());
      dupCreateQueryStatement.setOnCondition(this.getOnCondition());
      dupCreateQueryStatement.setHeap(this.getHeap());
      dupCreateQueryStatement.setDiskAttributes(this.getDiskAttributes());
      dupCreateQueryStatement.setTableObject(this.getTableObject());
      dupCreateQueryStatement.setSelectQueryStatement(this.getSelectQueryStatement());
      dupCreateQueryStatement.setWithCheckOption(this.getWithCheckOption());
      dupCreateQueryStatement.setClosedBraces(this.closedBrace);
      dupCreateQueryStatement.setOpenBraces(this.openBrace);
      dupCreateQueryStatement.setPhysicalCharacteristics(this.oraclePhysicalChar);
      dupCreateQueryStatement.setConstraint(this.getConstraint());
      dupCreateQueryStatement.setConstraintName(this.getConstraintName());
      dupCreateQueryStatement.setDatabaseObject(this.getDatabaseObject());
      dupCreateQueryStatement.setExternalTable(this.getExternalTable());
      dupCreateQueryStatement.setExternalDefaultDirectory(this.getExternalDefaultDirectory());
      dupCreateQueryStatement.setExternalDelimiter(this.getExternalDelimiter());
      dupCreateQueryStatement.setCreateIndexClause(this.getCreateIndexClause());
      dupCreateQueryStatement.setCreateIndexClauseBooleanValue(this.getCreateIndexClauseBooleanValue());
      dupCreateQueryStatement.setCreateIndexVector(this.getCreateIndexVector());
      dupCreateQueryStatement.setTypeString(this.typeString);
      dupCreateQueryStatement.setTypeIdentifier(this.typeIdentifier);
      dupCreateQueryStatement.setCreateSequenceStatement(this.getCreateSequence());
      dupCreateQueryStatement.setCreateSynonymStatement(this.getCreateSynonym());
      dupCreateQueryStatement.setOnForQuotedIdentifier(this.onConditionString);
      dupCreateQueryStatement.setQuotedIdentifierCondition(this.quotedIdentifierCondition);
      dupCreateQueryStatement.setPhysicalAttributesVector(this.getPhysicalAttributesVector());
      dupCreateQueryStatement.setIgnoreOrReplace(this.getIgnoreOrReplace());
      dupCreateQueryStatement.setSelectStatementString(this.getSelectStatementString());
      dupCreateQueryStatement.setMaterialized(this.materialized);
      dupCreateQueryStatement.setViewMetaAttribute(this.viewMetaAttribute);
      dupCreateQueryStatement.setWithReadOnly(this.withReadOnly);
      dupCreateQueryStatement.setLock(this.lock);
      dupCreateQueryStatement.setLockData(this.lockData);
      if (this.columnNames != null) {
         for(int i = 0; i < this.columnNames.size(); ++i) {
            if (this.columnNames.get(i) instanceof CreateColumn) {
               CreateColumn orgCreateColumn = (CreateColumn)this.columnNames.get(i);
               if (orgCreateColumn != null) {
                  CreateColumn newCreateColumn = orgCreateColumn.copyObjectValues();
                  createColumnVector.add(newCreateColumn);
               }
            }
         }
      }

      dupCreateQueryStatement.setColumnNames(createColumnVector);
      return dupCreateQueryStatement;
   }

   private String singleQueryIntoMultipleQueriesForPLSQL() {
      return SelectQueryStatement.singleQueryConvertedToMultipleQueryList;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      int i;
      if (!SwisSQLUtils.swissqlMessageList.isEmpty()) {
         sb.append("/* SwisSQL Messages :\n");

         for(i = 0; i < SwisSQLUtils.swissqlMessageList.size(); ++i) {
            sb.append(SwisSQLUtils.swissqlMessageList.get(i).toString() + "\n");
         }

         sb.append("*/\n");
         SwisSQLUtils.swissqlMessageList.clear();
      }

      if (this.commentObject != null) {
         sb.append(this.commentObject.toString() + "\n");
      }

      if (this.createSynonymStr != null) {
         sb.append(this.createSynonymStr);
         return sb.toString();
      } else {
         if (this.includeDrop && this.dropString != null) {
            sb.append(this.dropString);
         }

         String source;
         String str;
         if (this.singleQueryIntoMultipleQueriesForPLSQL() != null) {
            str = this.singleQueryIntoMultipleQueriesForPLSQL();
            if (str.indexOf("CREATE  SEQUENCE") != -1 && this.includeDrop) {
               source = str.substring(str.indexOf("CREATE  SEQUENCE") + 18);
               source = source.substring(0, source.indexOf("\n"));
               String ds = "BEGIN \nEXECUTE IMMEDIATE 'DROP SEQUENCE " + source + "';\nEXCEPTION WHEN OTHERS THEN NULL;\nEND;\n/\n\n";
               sb.append(ds);
            }

            sb.append(str);
            SelectQueryStatement.singleQueryConvertedToMultipleQueryList = null;
         }

         if (commentWhenConstraintNameTruncated != null && !commentWhenConstraintNameTruncated.equals("")) {
            sb.append("\n" + commentWhenConstraintNameTruncated + "\n\n");
         }

         if (this.createString != null) {
            sb.append(this.createString.toUpperCase() + " ");
         }

         if (this.createOrReplace != null) {
            sb.append(this.createOrReplace + " ");
         }

         if (this.force != null) {
            sb.append(this.force + " ");
         }

         if (this.temporary != null) {
            sb.append(this.temporary + " ");
         }

         if (this.materialized != null) {
            sb.append(this.materialized.toUpperCase() + " ");
         }

         if (this.externalTable != null) {
            sb.append(this.externalTable.toUpperCase() + " ");
         }

         if (this.tableOrView != null) {
            sb.append(this.tableOrView.toUpperCase() + " ");
         }

         if (this.tableObject != null) {
            this.tableObject.setObjectContext(this.context);
            sb.append(this.tableObject.toString() + " ");
            if (this.noLogConditionForTeradata != null) {
               sb.append(this.noLogConditionForTeradata + "\n");
            } else {
               sb.append(" \n");
            }
         }

         if (this.startWith != null && this.startWithFunction != null) {
            sb.append(" " + this.startWith + " ");
            sb.append(this.startWithFunction.toString() + "\n");
         }

         if (this.nextString != null && this.selectColumnInNextClause != null) {
            sb.append("  " + this.nextString + " ");
            sb.append(this.selectColumnInNextClause.toString() + "\n");
         }

         if (this.withSchemaBinding != null) {
            sb.append(" " + this.withSchemaBinding + " ");
         }

         if (this.onConditionForANSI != null) {
            sb.append(this.onConditionForANSI + " \n\t");
         }

         if (this.openBrace != null) {
            sb.append(this.openBrace);
         }

         if (this.constraint != null) {
            sb.append("\n\t" + this.constraint.toUpperCase());
         }

         if (this.constraintName != null) {
            if (this.context != null) {
               str = this.context.getEquivalent(this.constraintName).toString();
               sb.append(" " + str);
            } else {
               sb.append(" " + this.constraintName);
            }
         }

         if (this.columnNames != null) {
            for(i = 0; i < this.columnNames.size(); ++i) {
               if (this.columnNames.get(i) instanceof CreateColumn) {
                  CreateColumn createColumn = (CreateColumn)this.columnNames.get(i);
                  createColumn.setObjectContext(this.context);
                  if (i == 0) {
                     sb.append("\n\t" + createColumn.toString());
                  } else {
                     sb.append(", \n\t" + createColumn.toString());
                  }
               }
            }
         }

         if (this.createIndexClause != null) {
            this.createIndexClause.setObjectContext(this.context);
            sb.append(this.createIndexClause.toString());
         }

         if (this.createIndexVector != null) {
            for(i = 0; i < this.createIndexVector.size(); ++i) {
               CreateIndexClause tempCreateIndexClause = (CreateIndexClause)this.createIndexVector.get(i);
               if (this.createIndexClauseBooleanValue) {
                  sb.append(",\n\t");
               }

               tempCreateIndexClause.setObjectContext(this.context);
               sb.append(tempCreateIndexClause.toString().trim());
            }
         }

         if (this.mysqlCommentTableOption != null) {
            sb.append("\n" + this.mysqlCommentTableOption + "\n");
         }

         if (this.closedBrace != null) {
            sb.append("\n" + this.closedBrace + "\n");
         }

         if (this.onCondition != null) {
            sb.append("\t" + this.onCondition + " ");
         }

         if (this.organisationHeap != null) {
            sb.append("\n\t" + this.organisationHeap + " ");
         }

         if (this.onConditionString != null) {
            sb.append("\n\t" + this.onConditionString + " ");
         }

         if (this.quotedIdentifierCondition != null) {
            sb.append(this.quotedIdentifierCondition + " ");
         }

         if (this.oraclePhysicalChar != null) {
            sb.append("\n\t" + this.oraclePhysicalChar + " ");
         }

         if (this.diskAttribute != null) {
            sb.append("\n\t" + this.diskAttribute + " ");
         }

         if (this.materialized == null && this.viewMetaAttribute != null) {
            sb.append(this.viewMetaAttribute + " \n");
         }

         if (this.as != null) {
            sb.append(this.as + " \n");
         }

         if (this.selectQueryStatement != null) {
            if (this.context != null) {
               this.selectQueryStatement.setObjectContext(this.context);
            }

            if (this.openBraceForSelectQuery != null) {
               sb.append(this.openBraceForSelectQuery + " ");
            }

            sb.append(this.selectQueryStatement.toString() + " ");
            if (this.closeBraceForSelectQuery != null) {
               sb.append(this.closeBraceForSelectQuery + " ");
            }
         }

         if (this.withStatement != null) {
            sb.append(this.withStatement.toString() + " ");
         }

         if (this.withReadOnly != null) {
            sb.append(" " + this.withReadOnly);
         }

         if (this.withCheckOption != null) {
            sb.append("\n\t" + this.withCheckOption);
         }

         if (this.typeString != null) {
            sb.append("\t" + this.typeString.toUpperCase());
         }

         if (this.typeIdentifier != null) {
            sb.append(this.typeIdentifier + "\n");
         }

         if (this.ignoreOrReplace != null) {
            sb.append("\t" + this.ignoreOrReplace + " ");
         }

         if (this.selectStatementString != null) {
            sb.append(" " + this.selectStatementString + "\n");
         }

         if (this.createSequence != null) {
            this.createSequence.setObjectContext(this.context);
            sb.append(this.createSequence.toString());
         }

         if (this.physicalAttributesVector != null) {
            for(i = 0; i < this.physicalAttributesVector.size(); ++i) {
               sb.append("\n\t" + ((PhysicalAttributesClause)this.physicalAttributesVector.get(i)).toString());
            }
         }

         if (this.indexString != null) {
            str = sb.toString();
            str = str.trim();
            sb.replace(0, sb.length(), str);
            sb.append(this.indexString);
         }

         if (this.ttUniqueIndexforUniqueCons.size() > 0) {
            sb.append(";\n\n/* SwisSQL Message : UNIQUE constraint is not supported in TimesTen 5.1.21. Query split into multiple queries. */\n");

            for(i = 0; i < this.ttUniqueIndexforUniqueCons.size(); ++i) {
               source = this.ttUniqueIndexforUniqueCons.get(i).toString().trim();
               StringBuffer sb1 = new StringBuffer(source);
               sb1.replace(source.length() - 1, source.length() - 1, "");
               sb.append("\n" + sb1.toString() + ";\n");
            }
         }

         if (this.indexedViewStmt != null) {
            sb.append("\nGO\n/* SwisSQL Message: Compiling the below \" CREATE UNIQUE CLUSTERED INDEX \" statement might generate compilation error. Hence execute the below \" ALTER DATABASE \" statement by specifying the database name */ \n --ALTER DATABASE <dbname> SET ARITHABORT ON \n --GO\n");
            sb.append("\n" + this.indexedViewStmt + "\n");
         }

         if (this.lock != null) {
            sb.append(" " + this.lock.toUpperCase() + " ");
         }

         if (this.lockData != null) {
            sb.append(this.lockData.toUpperCase());
         }

         if (this.comment_flag__for_create_as_select_in_timesten) {
            sb.append(";\n\n/* SwisSQL Message : 'CREATE TABLE AS SELECT' is not supported in TimesTen 5.1.21. Query split into multiple queries. */\n");
         }

         if (this.triggerForIdentity != null) {
            sb.append("\n\n/\n\n" + this.triggerForIdentity + "\n\t");
         }

         if (this.distributeOnRandomClause != null) {
            sb.append(" " + this.distributeOnRandomClause);
         }

         if (this.onConditionForTeradata != null) {
            sb.append(" " + this.onConditionForTeradata);
         }

         if (this.externalDirectory != null && this.externalDelimiter != null) {
            sb.append(" USING (dataobject('" + this.externalDirectory + "')" + " " + "DELIMITER " + "'" + this.externalDelimiter + "')  ");
         } else if (this.externalDirectory != null && this.externalDelimiter == null) {
            sb.append(" USING (dataobject('" + this.externalDirectory + "')" + ") ");
         } else if (this.externalDirectory == null && this.externalDelimiter != null) {
            sb.append(" USING (DELIMITER '" + this.externalDelimiter + "')  ");
         }

         if (this.computedColView != null) {
            sb.append(";\n" + this.computedColView.toString());
         }

         if (this.getInsertQueryStatement() != null) {
            sb = new StringBuffer(sb.toString().trim());
            sb.append(";\n\n" + this.getInsertQueryStatement().toString() + " ");
         }

         return sb.toString();
      }
   }

   public UserObjectContext getObjectContext() {
      return this.context;
   }

   public void setObjectContext(UserObjectContext obj) {
      this.context = obj;
   }

   private String removeApstorphe(String str) {
      if (str.indexOf("`") == -1 && str.indexOf("'") == -1) {
         return str;
      } else {
         str = str.substring(1, str.length() - 1);
         return str;
      }
   }

   private boolean existsPKasIndex(CreateIndexClause createIndexClauseObject, CreateQueryStatement cqs) {
      Vector pkColumns = new Vector();
      Vector colVector = cqs.getColumnNames();
      Vector vec;
      if (colVector != null) {
         for(int k = 0; k < colVector.size(); ++k) {
            Object obj = colVector.get(k);
            if (obj instanceof CreateColumn) {
               CreateColumn createCol = (CreateColumn)colVector.get(k);
               vec = createCol.getConstraintClause();
               if (vec != null && !vec.isEmpty()) {
                  ConstraintType ct = ((ConstraintClause)vec.get(0)).getConstraintType();
                  if (ct != null && ct instanceof PrimaryOrUniqueConstraintClause) {
                     Vector constrColNames = ((PrimaryOrUniqueConstraintClause)ct).getConstraintColumnNames();
                     if (constrColNames != null) {
                        obj = constrColNames.get(0);
                        if (obj != null) {
                           pkColumns.add(this.removeApstorphe((String)obj));
                        }
                     }
                  }
               }
            }
         }
      }

      ArrayList col = createIndexClauseObject.getIndexColumns();

      for(int j = 0; j < col.size(); ++j) {
         Object obj = col.get(j);
         if (obj instanceof IndexColumn) {
            vec = ((IndexColumn)obj).getIndexColumnName().getColumnExpression();

            for(int l = 0; l < vec.size(); ++l) {
               obj = vec.get(l);
               if (obj instanceof TableColumn) {
                  String str = this.removeApstorphe(((TableColumn)obj).getColumnName());
                  if (pkColumns.contains(str)) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   private boolean isDatatypeBlobOrClob(CreateIndexClause createIndexClauseObject, CreateQueryStatement cqs) {
      String indexColumnName = "";
      ArrayList col = createIndexClauseObject.getIndexColumns();

      for(int j = 0; j < col.size(); ++j) {
         Object obj = col.get(j);
         if (obj instanceof IndexColumn) {
            indexColumnName = ((IndexColumn)obj).getIndexColumnName().toString();
            indexColumnName = this.removeApstorphe(indexColumnName);
         }
      }

      Vector colVector = cqs.getColumnNames();
      if (colVector != null) {
         for(int k = 0; k < colVector.size(); ++k) {
            Object obj = colVector.get(k);
            if (obj instanceof CreateColumn) {
               CreateColumn createCol = (CreateColumn)colVector.get(k);
               String createColumnName = createCol.getColumnName();
               if (createColumnName != null) {
                  createColumnName = this.removeApstorphe(createColumnName);
                  if (indexColumnName.equalsIgnoreCase(createColumnName)) {
                     String dType = createCol.getDatatype().getDatatypeName();
                     if (dType != null && (dType.trim().equalsIgnoreCase("BLOB") || dType.trim().equalsIgnoreCase("CLOB") || dType.trim().equalsIgnoreCase("TEXT") || dType.trim().equalsIgnoreCase("MEDIUMTEXT") || dType.trim().equalsIgnoreCase("LONGTEXT"))) {
                        return true;
                     }
                  }
               }
            }
         }
      }

      return false;
   }

   public String toVectorWiseString() throws ConvertException {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public SwisSQLStatement toVectorWise() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toInformix() throws ConvertException {
      return this.toInformixCreate();
   }

   public SwisSQLStatement toNetezza() throws ConvertException {
      return this.toNetezzaCreate();
   }

   public SwisSQLStatement toTimesTen() throws ConvertException {
      return this.toTimesTenCreate();
   }

   public SwisSQLStatement toOracle() throws ConvertException {
      return this.toOracleCreate();
   }

   public SwisSQLStatement toSqlite() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toSapHana() throws ConvertException {
      return this.toSapHana();
   }

   public SwisSQLStatement toAthena() throws ConvertException {
      return this.toAthena();
   }

   public SwisSQLStatement toSnowflake() throws ConvertException {
      return this.toSnowflakeCreate();
   }

   public SwisSQLStatement toMySQL() throws ConvertException {
      return this.toMySQLCreate();
   }

   public SwisSQLStatement toPostgreSQL() throws ConvertException {
      return this.toPostgreSQLCreate();
   }

   public SwisSQLStatement toBigQuery() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toDB2() throws ConvertException {
      return this.toDB2Create();
   }

   public SwisSQLStatement toSybase() throws ConvertException {
      return this.toSybaseCreate();
   }

   public SwisSQLStatement toMSSQLServer() throws ConvertException {
      return this.toMSSQLServerCreate();
   }

   public SwisSQLStatement toTeradata() throws ConvertException {
      return this.toTeradataCreate();
   }

   public SwisSQLStatement toANSI() throws ConvertException {
      return this.toANSICreate();
   }

   public SwisSQLStatement toMsAccessJdbc() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toExcel() throws ConvertException {
      return null;
   }
}
