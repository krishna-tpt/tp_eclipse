package com.zoho.analytics.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;

public class ZohoAnalyticsDatabaseMetadata extends ZohoAnalyticsWrapper implements DatabaseMetaData {
   private ZohoAnalyticsMetaData metaData;
   private ZohoAnalyticsConnection connection;

   ZohoAnalyticsDatabaseMetadata(Connection conn) throws SQLException {
      this.connection = (ZohoAnalyticsConnection)conn;
      this.metaData = ((ZohoAnalyticsConnection)conn).getAnalyticsMetaData();
   }

   public boolean allProceduresAreCallable() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("ALLPROCEDURESARECALLABLE", false);
   }

   public boolean allTablesAreSelectable() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("ALLTABLESARESELECTABLE", false);
   }

   public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("DATADEFINITIONCAUSESTRANSACTIONCOMMIT", false);
   }

   public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("DATADEFINITIONIGNOREDINTRANSACTIONS", false);
   }

   public boolean deletesAreDetected(int type) throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("DELETESAREDETECTED_" + type, false);
   }

   public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("DOESMAXROWSIZEINCLUDEBLOBS", false);
   }

   public ResultSet getAttributes(String catalog, String schemaPattern, String typeNamePattern, String attributeNamePattern) throws SQLException {
      throw new NotImplementedException();
   }

   public ResultSet getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable) throws SQLException {
      return this.metaData.getBestRowIdentifier(catalog, table);
   }

   public ResultSet getCatalogs() throws SQLException {
      return this.metaData.getCatalogs();
   }

   public String getCatalogSeparator() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("CATALOGSEPARATOR", "");
   }

   public String getCatalogTerm() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("CATALOGTERM", "");
   }

   public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern) throws SQLException {
      throw new NotImplementedException();
   }

   public ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
      return this.metaData.getColumns(catalog, tableNamePattern, columnNamePattern);
   }

   public Connection getConnection() throws SQLException {
      return this.connection;
   }

   public ResultSet getCrossReference(String primaryCatalogName, String primarySchemaName, String primaryTableName, String foreignCatalogName, String foreignSchemaName, String foreignTableName) throws SQLException {
      return this.metaData.getCrossReference(primaryCatalogName, primaryTableName, foreignCatalogName, foreignTableName);
   }

   public int getDatabaseMajorVersion() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("DATABASEMAJORVERSION", 0);
   }

   public int getDatabaseMinorVersion() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("DATABASEMINORVERSION", 0);
   }

   public String getDatabaseProductName() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("DATABASEPRODUCTNAME", "");
   }

   public String getDatabaseProductVersion() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("DATABASEPRODUCTVERSION", "");
   }

   public int getDefaultTransactionIsolation() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("DEFAULTTRANSACTIONISOLATION", 0);
   }

   public int getDriverMajorVersion() {
      try {
         return ZohoAnalyticsJDBCUtil.getServerProps("DRIVERMAJORVERSION", 0);
      } catch (Exception var2) {
         return -1;
      }
   }

   public int getDriverMinorVersion() {
      try {
         return ZohoAnalyticsJDBCUtil.getServerProps("DRIVERMINORVERSION", 0);
      } catch (Exception var2) {
         return -1;
      }
   }

   public String getDriverName() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("DRIVERNAME", "");
   }

   public String getDriverVersion() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("DRIVERVERSION", "");
   }

   public ResultSet getExportedKeys(String catalog, String schema, String tableName) throws SQLException {
      return this.metaData.getExportedKeys(catalog, tableName);
   }

   public String getExtraNameCharacters() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("EXTRANAMECHARACTERS", "");
   }

   public String getIdentifierQuoteString() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("IDENTIFIERQUOTESTRING", "");
   }

   public ResultSet getImportedKeys(String catalog, String schema, String table) throws SQLException {
      return this.metaData.getImportedKeys(catalog, table);
   }

   public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate) throws SQLException {
      throw new NotImplementedException();
   }

   public int getJDBCMajorVersion() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("JDBCMAJORVERSION", 0);
   }

   public int getJDBCMinorVersion() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("JDBCMINORVERSION", 0);
   }

   public int getMaxBinaryLiteralLength() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("MAXBINARYLITERALLENGTH", 0);
   }

   public int getMaxCatalogNameLength() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("MAXCATALOGNAMELENGTH", 0);
   }

   public int getMaxCharLiteralLength() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("MAXCHARLITERALLENGTH", 0);
   }

   public int getMaxColumnNameLength() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("MAXCOLUMNNAMELENGTH", 0);
   }

   public int getMaxColumnsInGroupBy() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("MAXCOLUMNSINGROUPBY", 0);
   }

   public int getMaxColumnsInIndex() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("MAXCOLUMNSININDEX", 0);
   }

   public int getMaxColumnsInOrderBy() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("MAXCOLUMNSINORDERBY", 0);
   }

   public int getMaxColumnsInSelect() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("MAXCOLUMNSINSELECT", 0);
   }

   public int getMaxColumnsInTable() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("MAXCOLUMNSINTABLE", 0);
   }

   public int getMaxConnections() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("MAXCONNECTIONS", 0);
   }

   public int getMaxCursorNameLength() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("MAXCURSORNAMELENGTH", 0);
   }

   public int getMaxIndexLength() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("MAXINDEXLENGTH", 0);
   }

   public int getMaxProcedureNameLength() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("MAXPROCEDURENAMELENGTH", 0);
   }

   public int getMaxRowSize() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("MAXROWSIZE", 0);
   }

   public int getMaxSchemaNameLength() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("MAXSCHEMANAMELENGTH", 0);
   }

   public int getMaxStatementLength() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("MAXSTATEMENTLENGTH", 0);
   }

   public int getMaxStatements() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("MAXSTATEMENTS", 0);
   }

   public int getMaxTableNameLength() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("MAXTABLENAMELENGTH", 0);
   }

   public int getMaxTablesInSelect() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("MAXTABLESINSELECT", 0);
   }

   public int getMaxUserNameLength() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("MAXUSERNAMELENGTH", 0);
   }

   public String getNumericFunctions() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("NUMERICFUNCTIONS", "");
   }

   public ResultSet getPrimaryKeys(String catalog, String schema, String table) throws SQLException {
      return this.metaData.getPrimaryKeys(catalog, table);
   }

   public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern) throws SQLException {
      throw new NotImplementedException();
   }

   public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern) throws SQLException {
      throw new NotImplementedException();
   }

   public String getProcedureTerm() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("PROCEDURETERM", "");
   }

   public int getResultSetHoldability() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("RESULTSETHOLDABILITY", 0);
   }

   public ResultSet getSchemas() throws SQLException {
      throw new NotImplementedException();
   }

   public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException {
      throw new NotImplementedException();
   }

   public String getSchemaTerm() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("SCHEMATERM", "");
   }

   public String getSearchStringEscape() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("SEARCHSTRINGESCAPE", "");
   }

   public String getSQLKeywords() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("SQLKEYWORDS", "");
   }

   public int getSQLStateType() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("SQLSTATETYPE", 0);
   }

   public String getStringFunctions() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("STRINGFUNCTIONS", "");
   }

   public ResultSet getSuperTables(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
      throw new NotImplementedException();
   }

   public ResultSet getSuperTypes(String catalog, String schemaPattern, String typeNamePattern) throws SQLException {
      throw new NotImplementedException();
   }

   public String getSystemFunctions() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("SYSTEMFUNCTIONS", "");
   }

   public ResultSet getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
      throw new NotImplementedException();
   }

   public ResultSet getTables(String catalogName, String schemaPattern, String tableNamePattern, String[] types) throws SQLException {
      return this.metaData.getTables(catalogName, tableNamePattern, types);
   }

   public ResultSet getTableTypes() throws SQLException {
      return this.metaData.getTableTypes();
   }

   public String getTimeDateFunctions() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("TIMEDATEFUNCTIONS", "");
   }

   public ResultSet getTypeInfo() throws SQLException {
      return this.metaData.getTypeInfo();
   }

   public ResultSet getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types) throws SQLException {
      throw new NotImplementedException();
   }

   public String getURL() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("URL", "");
   }

   public String getUserName() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("USERNAME", "");
   }

   public ResultSet getVersionColumns(String catalog, String schema, String table) throws SQLException {
      throw new NotImplementedException();
   }

   public boolean insertsAreDetected(int type) throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("INSERTSAREDETECTED_" + type, false);
   }

   public boolean isCatalogAtStart() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("ISCATALOGATSTART", false);
   }

   public boolean isReadOnly() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("ISREADONLY", false);
   }

   public boolean locatorsUpdateCopy() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("LOCATORSUPDATECOPY", false);
   }

   public boolean nullPlusNonNullIsNull() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("NULLPLUSNONNULLISNULL", false);
   }

   public boolean nullsAreSortedAtEnd() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("NULLSARESORTEDATEND", false);
   }

   public boolean nullsAreSortedAtStart() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("NULLSARESORTEDATSTART", false);
   }

   public boolean nullsAreSortedHigh() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("NULLSARESORTEDHIGH", false);
   }

   public boolean nullsAreSortedLow() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("NULLSARESORTEDLOW", false);
   }

   public boolean othersDeletesAreVisible(int type) throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("OTHERSDELETESAREVISIBLE_" + type, false);
   }

   public boolean othersInsertsAreVisible(int type) throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("OTHERSINSERTSAREVISIBLE_" + type, false);
   }

   public boolean othersUpdatesAreVisible(int type) throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("OTHERSUPDATESAREVISIBLE_" + type, false);
   }

   public boolean ownDeletesAreVisible(int type) throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("OWNDELETESAREVISIBLE_" + type, false);
   }

   public boolean ownInsertsAreVisible(int type) throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("OWNINSERTSAREVISIBLE_" + type, false);
   }

   public boolean ownUpdatesAreVisible(int type) throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("OWNUPDATESAREVISIBLE_" + type, false);
   }

   public boolean storesLowerCaseIdentifiers() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("STORESLOWERCASEIDENTIFIERS", false);
   }

   public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("LOWERCASEQUOTEDIDENTIFIERS", false);
   }

   public boolean storesMixedCaseIdentifiers() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("MIXEDCASEIDENTIFIERS", false);
   }

   public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("MIXEDCASEQUOTEDIDENTIFIERS", false);
   }

   public boolean storesUpperCaseIdentifiers() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("UPPERCASEIDENTIFIERS", false);
   }

   public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("UPPERCASEQUOTEDIDENTIFIERS", false);
   }

   public boolean supportsAlterTableWithAddColumn() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("ALTERTABLEWITHADDCOLUMN", false);
   }

   public boolean supportsAlterTableWithDropColumn() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("ALTERTABLEWITHDROPCOLUMN", false);
   }

   public boolean supportsANSI92EntryLevelSQL() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("ANSI92ENTRYLEVELSQL", false);
   }

   public boolean supportsANSI92FullSQL() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("ANSI92FULLSQL", false);
   }

   public boolean supportsANSI92IntermediateSQL() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("ANSI92INTERMEDIATESQL", false);
   }

   public boolean supportsBatchUpdates() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("BATCHUPDATES", false);
   }

   public boolean supportsCatalogsInDataManipulation() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("CATALOGSINDATAMANIPULATION", false);
   }

   public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("CATALOGSINDATAMANIPULATION", false);
   }

   public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("CATALOGSINPRIVILEGEDEFINITIONS", false);
   }

   public boolean supportsCatalogsInProcedureCalls() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("CATALOGSINPROCEDURECALLS", false);
   }

   public boolean supportsCatalogsInTableDefinitions() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("CATALOGSINTABLEDEFINITIONS", false);
   }

   public boolean supportsColumnAliasing() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("COLUMNALIASING", false);
   }

   public boolean supportsConvert() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("CONVERT", false);
   }

   public boolean supportsConvert(int fromType, int toType) throws SQLException {
      throw new NotImplementedException();
   }

   public boolean supportsCoreSQLGrammar() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("CORESQLGRAMMAR", false);
   }

   public boolean supportsCorrelatedSubqueries() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("CORRELATEDSUBQUERIES", false);
   }

   public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("DATADEFINITIONANDDATAMANIPULATIONTRANSACTIONS", false);
   }

   public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("DATAMANIPULATIONTRANSACTIONSONLY", false);
   }

   public boolean supportsDifferentTableCorrelationNames() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("DIFFERENTTABLECORRELATIONNAMES", false);
   }

   public boolean supportsExpressionsInOrderBy() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("EXPRESSIONSINORDERBY", false);
   }

   public boolean supportsExtendedSQLGrammar() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("EXTENDEDSQLGRAMMAR", false);
   }

   public boolean supportsFullOuterJoins() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("FULLOUTERJOINS", false);
   }

   public boolean supportsGetGeneratedKeys() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("GETGENERATEDKEYS", false);
   }

   public boolean supportsGroupBy() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("GROUPBY", false);
   }

   public boolean supportsGroupByBeyondSelect() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("GROUPBYBEYONDSELECT", false);
   }

   public boolean supportsGroupByUnrelated() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("GROUPBYUNRELATED", false);
   }

   public boolean supportsIntegrityEnhancementFacility() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("INTEGRITYENHANCEMENTFACILITY", false);
   }

   public boolean supportsLikeEscapeClause() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("LIKEESCAPECLAUSE", false);
   }

   public boolean supportsLimitedOuterJoins() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("LIMITEDOUTERJOINS", false);
   }

   public boolean supportsMinimumSQLGrammar() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("MINIMUMSQLGRAMMAR", false);
   }

   public boolean supportsMixedCaseIdentifiers() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("MIXEDCASEIDENTIFIERS", false);
   }

   public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("MIXEDCASEQUOTEDIDENTIFIERS", false);
   }

   public boolean supportsMultipleOpenResults() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("MULTIPLEOPENRESULTS", false);
   }

   public boolean supportsMultipleResultSets() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("MULTIPLERESULTSETS", false);
   }

   public boolean supportsMultipleTransactions() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("MULTIPLETRANSACTIONS", false);
   }

   public boolean supportsNamedParameters() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("NAMEDPARAMETERS", false);
   }

   public boolean supportsNonNullableColumns() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("NONNULLABLECOLUMNS", false);
   }

   public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("OPENCURSORSACROSSCOMMIT", false);
   }

   public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("OPENCURSORSACROSSROLLBACK", false);
   }

   public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("OPENSTATEMENTSACROSSCOMMIT", false);
   }

   public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("OPENSTATEMENTSACROSSROLLBACK", false);
   }

   public boolean supportsOrderByUnrelated() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("ORDERBYUNRELATED", false);
   }

   public boolean supportsOuterJoins() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("OUTERJOINS", false);
   }

   public boolean supportsPositionedDelete() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("POSITIONEDDELETE", false);
   }

   public boolean supportsPositionedUpdate() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("POSITIONEDUPDATE", false);
   }

   public boolean supportsResultSetConcurrency(int type, int concurrency) throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("RESULTSETCONCURRENCY_" + type + "_" + concurrency, false);
   }

   public boolean supportsResultSetHoldability(int holdability) throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("RESULTSETHOLDABILITY_" + holdability, false);
   }

   public boolean supportsResultSetType(int type) throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("RESULTSETTYPE_" + type, false);
   }

   public boolean supportsSavepoints() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("SAVEPOINTS", false);
   }

   public boolean supportsSchemasInDataManipulation() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("SCHEMASINDATAMANIPULATION", false);
   }

   public boolean supportsSchemasInIndexDefinitions() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("SCHEMASININDEXDEFINITIONS", false);
   }

   public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("SCHEMASINPRIVILEGEDEFINITIONS", false);
   }

   public boolean supportsSchemasInProcedureCalls() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("SCHEMASINPROCEDURECALLS", false);
   }

   public boolean supportsSchemasInTableDefinitions() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("SCHEMASINTABLEDEFINITIONS", false);
   }

   public boolean supportsSelectForUpdate() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("SELECTFORUPDATE", false);
   }

   public boolean supportsStatementPooling() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("STATEMENTPOOLING", false);
   }

   public boolean supportsStoredProcedures() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("STOREDPROCEDURES", false);
   }

   public boolean supportsSubqueriesInComparisons() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("SUBQUERIESINCOMPARISONS", false);
   }

   public boolean supportsSubqueriesInExists() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("SUBQUERIESINEXISTS", false);
   }

   public boolean supportsSubqueriesInIns() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("SUBQUERIESININS", false);
   }

   public boolean supportsSubqueriesInQuantifieds() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("SUBQUERIESINQUANTIFIEDS", false);
   }

   public boolean supportsTableCorrelationNames() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("TABLECORRELATIONNAMES", false);
   }

   public boolean supportsTransactionIsolationLevel(int level) throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("SUPPORTSTRANSACTIONISOLATIONLEVEL_" + level, false);
   }

   public boolean supportsTransactions() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("TRANSACTIONS", false);
   }

   public boolean supportsUnion() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("UNION", false);
   }

   public boolean supportsUnionAll() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("UNIONALL", false);
   }

   public boolean updatesAreDetected(int type) throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("UPDATESAREDETECTED_" + type, false);
   }

   public boolean usesLocalFilePerTable() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("USESLOCALFILEPERTABLE", false);
   }

   public boolean usesLocalFiles() throws SQLException {
      return ZohoAnalyticsJDBCUtil.getServerProps("USESLOCALFILES", false);
   }

   public RowIdLifetime getRowIdLifetime() throws SQLException {
      throw new NotImplementedException();
   }

   public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
      throw new NotImplementedException();
   }

   public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
      throw new NotImplementedException();
   }

   public ResultSet getClientInfoProperties() throws SQLException {
      throw new NotImplementedException();
   }

   public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern) throws SQLException {
      throw new NotImplementedException();
   }

   public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern) throws SQLException {
      throw new NotImplementedException();
   }

   public ResultSet getPseudoColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
      throw new NotImplementedException();
   }

   public boolean generatedKeyAlwaysReturned() throws SQLException {
      throw new NotImplementedException();
   }
}
