package com.adventnet.swissqlapi.util;

import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;
import com.adventnet.swissqlapi.sql.statement.create.BinClass;
import com.adventnet.swissqlapi.sql.statement.create.CharacterClass;
import com.adventnet.swissqlapi.sql.statement.create.CreateColumn;
import com.adventnet.swissqlapi.sql.statement.create.CreateQueryStatement;
import com.adventnet.swissqlapi.sql.statement.create.Datatype;
import com.adventnet.swissqlapi.sql.statement.create.DateClass;
import com.adventnet.swissqlapi.sql.statement.create.NumericClass;
import com.adventnet.swissqlapi.sql.statement.insert.InsertQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.FromClause;
import com.adventnet.swissqlapi.sql.statement.select.FromTable;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import com.adventnet.swissqlapi.util.misc.CastingUtil;
import com.adventnet.swissqlapi.util.misc.CustomizeUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

public class SwisSQLUtils {
   public static Hashtable objectNameMapping = new Hashtable();
   private static String[] oracleSystemFunctionsArray = new String[]{"ABS", "ACOS", "APP_NAME", "ASCII", "ASIN", "ATAN", "ATN2", "AVG", "CAST", "CEILING", "CHAR", "CHARINDEX", "CHECKSUM", "COALESCE", "COL_LENGTH", "COL_NAME", "CONTAINS", "CONVERT", "COS", "COT", "COUNT", "DATALENGTH", "DATEADD", "DATEDIFF", "DATENAME", "DATEPART", "DIFFERENCE", "DEGREES", "DAY", "EXP", "FLOOR", "GETDATE", "GETUTCDATE", "ISDATE", "ISNULL", "ISNUMERIC", "LEFT", "LEN", "LOG", "LOG10", "LOWER", "LTRIM", "MAX", "MIN", "MONTH", "NEWID", "NULLIF", "OBJECT_ID", "PATINDEX", "QUOTENAME", "RAND", "REPLACE", "RADIANS", "REPLICATE", "REVERSE", "RIGHT", "ROUND", "RTRIM", "SOUNDEX", "SPACE", "STR", "SUBSTRING", "STUFF", "CASE", "USER_NAME", "UPPER", "UNICODE"};
   private static String[] mysqlSystemFunctionsArray = new String[]{"ABS", "ACOS", "APP_NAME", "ASCII", "ASIN", "ATAN", "ATN2", "AVG", "CAST", "CEILING", "CHAR", "CHARINDEX", "CHECKSUM", "COALESCE", "COL_LENGTH", "COL_NAME", "CONTAINS", "CONVERT", "COS", "COT", "COUNT", "DATALENGTH", "DATEADD", "DATEDIFF", "DATENAME", "DATEPART", "DIFFERENCE", "DEGREES", "DAY", "EXP", "FLOOR", "GETDATE", "GETUTCDATE", "ISDATE", "ISNULL", "ISNUMERIC", "LEFT", "LEN", "LOG", "LOG10", "LOWER", "LTRIM", "MAX", "MIN", "MONTH", "NEWID", "NULLIF", "OBJECT_ID", "PATINDEX", "QUOTENAME", "RAND", "REPLACE", "RADIANS", "REPLICATE", "REVERSE", "RIGHT", "ROUND", "RTRIM", "SOUNDEX", "SPACE", "STR", "SUBSTRING", "STUFF", "CASE", "USER_NAME", "UPPER", "UNICODE"};
   private static String[] sqlServerSystemFunctionsArray = new String[]{"abs", "acos", "app_name", "ascii", "asin", "atan", "atn2", "avg", "cast", "ceiling", "char", "charindex", "checksum", "coalesce", "col_length", "col_name", "contains", "convert", "cos", "cot", "count", "datalength", "dateadd", "datediff", "datename", "datepart", "difference", "degrees", "day", "exp", "floor", "getdate", "getutcdate", "isdate", "isnull", "isnumeric", "left", "len", "log", "log10", "lower", "ltrim", "max", "min", "month", "newid", "nullif", "object_id", "patindex", "quotename", "rand", "replace", "radians", "replicate", "reverse", "right", "round", "rtrim", "soundex", "space", "str", "substring", "stuff", "case", "user_name", "upper", "unicode"};
   private static String[] sybaseSystemFunctionsArray = new String[]{"abs", "acos", "app_name", "ascii", "asin", "atan", "atn2", "avg", "cast", "ceiling", "char", "charindex", "checksum", "coalesce", "col_length", "col_name", "contains", "convert", "cos", "cot", "count", "datalength", "dateadd", "datediff", "datename", "datepart", "day", "degrees", "exp", "floor", "getdate", "isdate", "isnull", "isnumeric", "left", "len", "log", "log10", "lower", "ltrim", "max", "min", "month", "newid", "nullif", "object_id", "patindex", "radians", "rand", "replace", "replicate", "reverse", "right", "round", "rtrim", "space", "str", "substring", "stuff", "case", "user_name", "upper"};
   private static String[] db2SystemFunctionsArray = new String[]{"ABS", "ASCII", "DAYNAME", "ACOS", "CHAR", "DAYOFWEEK", "ASIN", "CONCAT", "DAYOFYEAR", "ATAN", "DIFFERENCE", "HOUR", "ATAN2", "INSERT", "MINUTE", "CEILING", "LCASE", "MONTH", "COS", "LEFT", "MONTHNAME", "COT", "LENGTH", "MONTHNAME", "DEGREES", "LOCATE", "QUARTER", "EXP", "LTRIM", "SECOND", "FLOOR", "REPEAT", "TIMESTAMPDIFF", "LOG", "REPLACE", "WEEK", "LOG10", "RIGHT", "YEAR", "MOD", "RTRIM", "POWER", "SOUNDEX", "RADIANS", "SPACE", "RAND", "SUBSTRING", "ROUND", "UCASE", "SIGN", "SIN", "SQRT", "TAN", "TRUNCATE"};
   private static String[] postgresqlSystemFunctionsArray = new String[]{"ABS", "ASCII", "DATABASE", "CURDATE", "ACOS", "CHAR", "IFNULL", "CURTIME", "ASIN", "CONCAT", "USER", "DAYNAME", "ATAN", "LCASE", "DAYOFMONTH", "ATAN2", "LEFT", "DAYOFWEEK", "CEILING", "LENGTH", "DAYOFYEAR", "COS", "LTRIM", "HOUR", "COT", "REPEAT", "MINUTE", "DEGREES", "REPLACE", "MONTH", "EXP", "RTRIM", "MONTHNAME", "FLOOR", "SPACE", "NOW", "LOG", "SUBSTRING", "QUARTER", "LOG10", "UCASE", "SECOND", "MOD", "WEEK", "PI", "YEAR", "POWER", "RADIANS", "RAND", "ROUND", "SIGN", "SIN", "SQRT", "TAN", "TRUNCATE"};
   private static String[] teradataSystemFunctionsArray = new String[]{"CURRENT_DATE", "CURRENT_TIMESTAMP", "CURRENT_TIME", "DATABASE", "DATE", "PROFILE", "ROLE", "SESSION", "TIME", "USER"};
   private static String[] oracleKeywordsArray = new String[]{"ACCESS", "ADD", "ALL", "ALTER", "AND", "ANY", "AS", "ASC", "AUDIT", "BETWEEN", "BY", "CHAR", "CHECK", "CLUSTER", "COLUMN", "COMMENT", "COMPRESS", "CONNECT", "CREATE", "CURRENT", "DATE", "DECIMAL", "DEFAULT", "DELETE", "DESC", "DISTINCT", "DROP", "ELSE", "EXCLUSIVE", "EXISTS", "FILE", "FLOAT", "FOR", "FROM", "GRANT", "GROUP", "HAVING", "IDENTIFIED", "IMMEDIATE", "IN", "INCREMENT", "INDEX", "INITIAL", "INSERT", "INTEGER", "INTERSECT", "INTO", "IS", "LEVEL", "LIKE", "LOCK", "LONG", "MAXEXTENTS", "MINUS", "MLSLABEL", "MODE", "MODIFY", "NOAUDIT", "NOCOMPRESS", "NOT", "NOWAIT", "NULL", "NUMBER", "OF", "OFFLINE", "ON", "ONLINE", "OPTION", "OR", "ORDER", "PCTFREE", "PRIOR", "PRIVILEGES", "PUBLIC", "RAW", "RENAME", "RESOURCE", "REVOKE", "ROW", "ROWID", "ROWNUM", "ROWS", "SELECT", "SESSION", "SET", "SHARE", "SIZE", "SMALLINT", "START", "SUCCESSFUL", "SYNONYM", "SYSDATE", "TABLE", "THEN", "TO", "TRIGGER", "UID", "UNION", "UNIQUE", "UPDATE", "USER", "VALIDATE", "VALUES", "VARCHAR", "VARCHAR2", "VIEW", "WHENEVER", "WHERE", "WITH"};
   private static String[] sqlServerKeywordsArray = new String[]{"ADD", "EXCEPT", "PERCENT", "ALL", "EXEC", "PLAN", "ALTER", "EXECUTE", "PRECISION", "AND", "EXISTS", "PRIMARY", "ANY", "EXIT", "PRINT", "AS", "FETCH", "PROC", "ASC", "FILE", "PROCEDURE", "AUTHORIZATION", "FILLFACTOR", "PUBLIC", "BACKUP", "FOR", "RAISERROR", "BEGIN", "FOREIGN", "READ", "BETWEEN", "FREETEXT", "READTEXT", "BREAK", "FREETEXTTABLE", "RECONFIGURE", "BROWSE", "FROM", "REFERENCES", "BULK", "FULL", "REPLICATION", "BY", "FUNCTION", "RESTORE", "CASCADE", "GOTO", "RESTRICT", "CASE", "GRANT", "RETURN", "CHECK", "GROUP", "REVOKE", "CHECKPOINT", "HAVING", "RIGHT", "CLOSE", "HOLDLOCK", "ROLLBACK", "CLUSTERED", "IDENTITY", "ROWCOUNT", "COALESCE", "IDENTITY_INSERT", "ROWGUIDCOL", "COLLATE", "IDENTITYCOL", "RULE", "COLUMN", "IF", "SAVE", "COMMIT", "IN", "SCHEMA", "COMPUTE", "INDEX", "SELECT", "CONSTRAINT", "INNER", "SESSION_USER", "CONTAINS", "INSERT", "SET", "CONTAINSTABLE", "INTERSECT", "SETUSER", "CONTINUE", "INTO", "SHUTDOWN", "CONVERT", "IS", "SOME", "CREATE", "JOIN", "STATISTICS", "CROSS", "KEY", "SYSTEM_USER", "CURRENT", "KILL", "TABLE", "CURRENT_DATE", "LEFT", "TEXTSIZE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "LIKE", "THEN", "LINENO", "TO", "CURRENT_USER", "LOAD", "TOP", "CURSOR", "NATIONAL", "TRAN", "DATABASE", "NOCHECK", "TRANSACTION", "DBCC", "NONCLUSTERED", "TRIGGER", "DEALLOCATE", "NOT", "TRUNCATE", "DECLARE", "NULL", "TSEQUAL", "DEFAULT", "NULLIF", "UNION", "DELETE", "OF", "UNIQUE", "DENY", "OFF", "UPDATE", "DESC", "OFFSETS", "UPDATETEXT", "DISK", "ON", "USE", "DISTINCT", "OPEN", "USER", "DISTRIBUTED", "OPENDATASOURCE", "VALUES", "DOUBLE", "OPENQUERY", "VARYING", "DROP", "OPENROWSET", "VIEW", "DUMMY", "OPENXML", "WAITFOR", "DUMP", "OPTION", "WHEN", "ELSE", "OR", "WHERE", "END", "ORDER", "WHILE", "ERRLVL", "OUTER", "WITH", "ESCAPE", "OVER", "WRITETEXT"};
   private static String[] timestenReservedWordsArray = new String[]{"ABS", "ACTION", "ADD", "ADDMONTHS", "ALL", "ALLOWABLE", "ALTER", "AND", "ANY", "AS", "ASC", "ASYNCHRONOUS", "AUTHORIZATION", "AUTOREFRESH", "AVG", "BEGIN", "BETWEEN", "BIGINT", "BIGINTS", "BINARY", "BITAND", "BITNEG", "BITOR", "BULK", "BY", "CACHE", "CACHEONLY", "CALL", "CASCADE", "CHAR", "CHARACTER", "CHECK", "COLON", "COLUMN", "COMMA", "COMMIT", "COMPRESS", "CONCAT", "CONFLICTS", "CONNECT", "CONSTRAINT", "COUNT", "CREATE", "CS", "CURRENT", "CURRENT_SCHEMA", "CURRENT_USER", "CURRENTDATE", "CURRENTDATETIME", "CURRENTTIME", "CYCLE", "DATASTORE", "DATASTORE_OWNER", "DATE", "DATETIME", "DAY", "DDL", "DEBUG", "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DELETE", "DELETE_FT", "DESC", "DIGIT", "DISABLE", "DISTINCT", "DOT", "DOUBLE", "DROP", "DURABLE", "DURATION", "ELEMENT", "ENCRYPTED", "EQU", "ESCAPE", "EVERY", "EXCEPTION", "EXCLOR", "EXISTS", "EXIT", "EXTERNALLY", "FAILTHRESHOLD", "FIRST", "FLOAT", "FLUSH", "FOR", "FOREIGN", "FRACTION", "FROM", "FULL", "GARBAGE", "GEQ", "GETDATE", "GRANT", "GROUP", "GRT", "HASH", "HAVING", "HEXSTRING", "HOUR", "ID", "IDENT", "IDENTIFIED", "IN", "INCREMENT", "INCREMENTAL", "INDEX", "INDICATOR", "INLINE", "INSERT", "INSERTONLY", "INSTANCE", "INT", "INTEGER", "INTERVAL", "INTO", "IS", "KEY", "LATENCY", "LBRACE", "LEQ", "LES", "LIKE", "LIMIT", "LOAD", "LOCAL", "LONG", "LOWER", "LPAREN", "MASTER", "MATERIALIZED", "MAX", "MAXVALUE", "MILLISECONDS", "MIN", "MINUS", "MINUTE", "MINUTES", "MINVALUE", "MOD", "MODE", "MONTH", "MULTI", "NAME", "NATIONAL", "NCHAR", "NEQ", "NO", "NONDURABLE", "NOT", "NOTIMPLEMENTED", "NQUOTESTR", "NULL", "NUMERIC", "NVARCHAR", "NVL", "OF", "OFF", "ON", "OR", "ORACLE", "ORACLEQUERY", "ORDER", "OUT_OF_LINE", "OUTERJOIN", "PAGES", "PAUSED", "PLUS", "PORT", "PRECISION", "PRIMARY", "PRIVATE", "PRIVILEGES", "PROPAGATE", "PROPAGATOR", "PUBLIC", "PUBLICREAD", "PUBLICROW", "QUIT", "QUOTESTR", "RBRACE", "RC", "READONLY", "REAL", "REALS", "RECEIPT", "REFERENCES", "REFRESH", "RELEASE", "REPLICATION", "REPORT", "REQUEST", "REQUIRED", "RESTRICT", "RESUME", "RETURN", "REVOKE", "ROLLBACK", "ROW", "ROWS", "RPAREN", "RR", "RTRIM", "RU", "SCHEMA", "SECOND", "SECONDS", "SECTION", "SELECT", "SELF", "SEMI", "SEQCACHE", "SEQCACHEONLY", "SEQUENCE", "SERVICES", "SESSION", "SESSION_USER", "SET", "SLASH", "SMALLINT", "SOME", "STAR", "START", "STATE", "STOPPED", "STORE", "SUBSCRIBER", "SUM", "SYNCHRONOUS", "SYSDATE", "SYSTEM", "SYSTEM_USER", "TABLE", "TIME", "TIMEOUT", "TIMESTAMP", "TINYINT", "TO", "TO_CHAR", "TO_DATE", "TOCHAR", "TODATE", "TOINTEGER", "TRAFFIC", "TRANSMIT", "TWOSAFE", "UNION", "UNIQUE", "UNLOAD", "UPDATE", "UPPER", "USER", "USERMANAGED", "VALUES", "VARBINARY", "VARCHAR", "VARYING", "VIEW", "WAIT", "WHEN", "WHERE", "WITH", "WORK", "WRITE", "WRITETHROUGH", "YEAR"};
   private static String[] netezzaReservedWordsArray = new String[]{"ABORT", "ADMIN", "AGGREGATE", "ALIGN", "ALL", "ALLOCATE", "ANALYSE", "ANALYZE", "AND", "ANY", "AS", "ASC", "BETWEEN", "BINARY", "BIT", "BOTH", "CASE", "CHAR", "CHARACTER", "DEC", "DECIMAL", "DEFAULT", "DEFERRABLE", "DESC", "DISTINCT", "DISTRIBUTE", "DO", "ELSE", "END", "EXCEPT", "EXCLUDE", "EXISTS", "EXPLAIN", "EXPRESS", "EXTEND", "FALSE", "LEADING", "LEFT", "LIKE", "LIMIT", "LISTEN", "LOAD", "LOCAL", "LOCK", "MINUS", "MOVE", "NATURAL", "NCHAR", "NEW", "NOT", "NOTNULL", "NULL", "NULLS", "NUMERIC", "RESET", "REUSE", "RIGHT", "ROWS", "ROWSETLIMIT", "RULE", "SEARCH", "SELECT", "SESSION_USER", "SETOF", "SHOW", "SOME", "SYSTEM", "THEN", "TIES", "TIME", "TIMESTAMP", "CHECK", "CLUSTER", "COLLATE", "COLLATION", "COLUMN", "CONSTRAINT", "COPY", "CROSS", "CURRENT", "CURRENT_RUSER", "CURRENT_USERID", "CURRENT_USEROID", "DEALLOCATE", "FIRST", "FLOAT", "FOLLOWING", "FOR", "FOREIGN", "FROM", "FULL", "FUNCTION", "GENSTATS", "GLOBAL", "GROUP", "HAVING", "ILIKE", "IN", "INDEX", "INITIALLY", "INNER", "INOUT", "INTERSECT", "INTERVAL", "INTO", "OFF", "OFFSET", "OLD", "ON", "ONLINE", "ONLY", "OR", "ORDER", "OTHERS", "OUT", "OUTER", "OVER", "OVERLAPS", "PARTITION", "POSITION", "PRECEDING", "PRECISION", "PRESERVE", "PRIMARY", "RESET", "REUSE", "TO", "TRAILING", "TRANSACTION", "TRUE", "UNBOUNDED", "UNION", "UNIQUE", "USING", "VACUUM", "VARCHAR", "VERBOSE", "WHEN", "WHERE", "WITH", "WRITE"};
   private static String[] postgresqlReservedWordsArray = new String[]{"ALL", "ANALYSE", "ANALYZE", "AND", "ANY", "ARRAY", "AS", "ASC", "ASYMMETRIC", "AUTHORIZATION", "BETWEEN", "BINARY", "BOTH", "CASE", "CAST", "CHECK", "COLLATE", "COLUMN", "CONSTRAINT", "CREATE", "CROSS", "CURRENT_CATALOG", "CURRENT_DATE", "CURRENT_ROLE", "CURRENT_SCHEMA", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "DEFAULT", "DEFERRABLE", "DESC", "DISTINCT", "DO", "ELSE", "END", "EXCEPT", "FETCH", "FOR", "FOREIGN", "FREEZE", "FROM", "FULL", "GRANT", "GROUP", "HAVING", "ILIKE", "IN", "INITIALLY", "INNER", "INTERSECT", "INTO", "IS", "ISNULL", "JOIN", "LEADING", "LEFT", "LIKE", "LIMIT", "LOCALTIME", "LOCALTIMESTAMP", "NATURAL", "NEW", "NOT", "NOTNULL", "NULL", "OFF", "OFFSET", "OLD", "ON", "ONLY", "OR", "ORDER", "OUTER", "OVERLAPS", "PLACING", "PRIMARY", "REFERENCES", "RETURNING", "RIGHT", "SELECT", "SESSION_USER", "SIMILAR", "SOME", "SYMMETRIC", "TABLE", "THEN", "TO", "TRAILING", "UNION", "UNIQUE", "USER", "USING", "VARIADIC", "VERBOSE", "WHEN", "WHERE", "WITH"};
   private static String[] teradataReservedWordsArray = new String[]{"A", "ABORT", "ABORTSESSION", "ABS", "ABSOLUTE", "ACCESS", "ACCESS_LOCK", "ACCOUNT", "ACOS", "ACOSH", "ACTION", "ADA", "ADD", "ADD_MONTHS", "ADMIN", "AFTER", "AG", "AGGREGATE", "ALIAS", "ALL", "ALLOCATE", "ALLOCATION", "ALLPARAMS", "ALTER", "ALWAYS", "AMP", "ANALYSIS", "AND", "ANSIDATE", "ANY", "ARCHIVE", "ARE", "ARGLPAREN", "ARRAY", "AS", "ASC", "ASCII", "ASENSITIVE", "ASIN", "ASINH", "ASSERTION", "ASSIGNMENT", "ASYMMETRIC", "AT", "ATAN", "ATAN2", "ATANH", "ATOMIC", "ATTR", "ATTRIBUTE", "ATTRIBUTES", "ATTRS", "AUTHORIZATION", "AVE", "AVERAGE", "AVG", "BEFORE", "BEGIN", "BERNOULLI", "BETWEEN", "BIGINT", "BINARY", "BLOB", "BOOLEAN", "BOTH", "BREADTH", "BT", "BUT", "BY", "BYTE", "BYTEINT", "BYTES", "C", "CALL", "CALLED", "CALLER", "CARDINALITY", "CASCADE", "CASCADED", "CASE", "CASE_N", "CASESPECIFIC", "CAST", "CATALOG", "CATALOG_NAME", "CD", "CEIL", "CEILING", "CHAIN", "CHANGERATE", "CHAR", "CHAR_LENGTH", "CHAR2HEXINT", "CHARACTER", "CHARACTER_LENGTH", "CHARACTER_SET_CATALOG", "CHARACTER_SET_NAME", "CHARACTER_SET_SCHEMA", "CHARACTERISTICS", "CHARACTERS", "CHARS", "CHARSET_COLL", "CHECK", "CHECKED", "CHECKPOINT", "CHECKSUM", "CLASS", "CLASS_ORIGIN", "CLIENT", "CLOB", "CLOSE", "CLUSTER", "CM", "COALESCE", "COBOL", "COLLATE", "COLLATION", "COLLATION_CATALOG", "COLLATION_NAME", "COLLATION_SCHEMA", "COLLECT", "COLUMN", "COLUMN_NAME", "COLUMNS", "COLUMNSPERINDEX", "COLUMNSPERJOININDEX", "COMMAND_FUNCTION", "COMMAND_FUNCTION_CODE", "COMMENT", "COMMIT", "COMMITTED", "COMPARABLE", "COMPARISON", "COMPILE", "COMPRESS", "CONDITION", "CONDITION_NUMBER", "CONNECT", "CONNECTION", "CONNECTION_NAME", "CONSTRAINT", "CONSTRAINT_CATALOG", "CONSTRAINT_NAME", "CONSTRAINT_SCHEMA", "CONSTRAINTS", "CONSTRUCTOR", "CONSUME", "CONTAINS", "CONTINUE", "CONVERT", "CONVERT_TABLE_HEADER", "CORR", "CORRESPONDING", "COS", "COSH", "COSTS", "COUNT", "COVAR_POP", "COVAR_SAMP", "CPP", "CPUTIME", "CPUTIMENORM", "CREATE", "CROSS", "CS", "CSUM", "CT", "CUBE", "CUME_DIST", "CURRENT", "CURRENT_DATE", "CURRENT_DEFAULT_TRANSFORM_GROUP", "CURRENT_PATH", "CURRENT_ROLE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_TRANSFORM_GROUP_FOR_TYPE", "CURRENT_USER", "CURSOR", "CURSOR_NAME", "CV", "CYCLE", "DATA", "DATABASE", "DATABLOCKSIZE", "DATE", "DATEFORM", "DATETIME_INTERVAL_CODE", "DATETIME_INTERVAL_PRECISION", "DAY", "DBC", "DEALLOCATE", "DEBUG", "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DEFAULTS", "DEFERRABLE", "DEFERRED", "DEFINED", "DEFINER", "DEGREE", "DEGREES", "DEL", "DELETE", "DEMOGRAPHICS", "DENIALS", "DENSE_RANK", "DEPTH", "DEREF", "DERIVED", "DESC", "DESCRIBE", "DESCRIPTOR", "DETERMINISTIC", "DIAGNOSTIC", "DIAGNOSTICS", "DIGITS", "DISABLED", "DISCONNECT", "DISPATCH", "DISTINCT", "DO", "DOMAIN", "DOUBLE", "DR", "DROP", "DUAL", "DUMP", "DYNAMIC", "DYNAMIC_FUNCTION", "DYNAMIC_FUNCTION_CODE", "EACH", "EBCDIC", "ECHO", "ELAPSEDSEC", "ELAPSEDTIME", "ELEMENT", "ELSE", "ELSEIF", "ENABLED", "ENCRYPT", "END", "END-EXEC", "EQ", "EQUALS", "ERROR", "ERRORFILES", "ERRORS", "ERRORTABLES", "ESCAPE", "ET", "EVERY", "EXCEPT", "EXCEPTION", "EXCL", "EXCLUDE", "EXCLUDING", "EXCLUSIVE", "EXEC", "EXECUTE", "EXISTING", "EXISTS", "EXIT", "EXP", "EXPIRE", "EXPLAIN", "EXTERNAL", "EXTRACT", "FALLBACK", "FALSE", "FASTEXPORT", "FETCH", "FILTER", "FINAL", "FIRST", "FLOAT", "FLOOR", "FOLLOWING", "FOR", "FOREIGN", "FORMAT", "FORTRAN", "FOUND", "FREE", "FREESPACE", "FROM", "FULL", "FUNCTION", "FUSION", "G", "GE", "GENERAL", "GENERATED", "GET", "GIVE", "GLOBAL", "GO", "GOTO", "GRANT", "GRANTED", "GRAPHIC", "GROUP", "GROUPING", "GT", "HANDLER", "HASH", "HASHAMP", "HASHBAKAMP", "HASHBUCKET", "HASHROW", "HAVING", "HELP", "HIERARCHY", "HIGH", "HOLD", "HOST", "HOUR", "IDENTITY", "IF", "IFP", "IMMEDIATE", "IMPLEMENTATION", "IN", "INCLUDING", "INCONSISTENT", "INCREMENT", "INDEX", "INDEXESPERTABLE", "INDEXMAINTMODE", "INDICATOR", "INIT", "INITIALLY", "INITIATE", "INNER", "INOUT", "INPUT", "INS", "INSENSITIVE", "INSERT", "INSTANCE", "INSTANTIABLE", "INSTEAD", "INT", "INTEGER", "INTEGERDATE", "INTERFACE", "INTERNAL", "INTERSECT", "INTERSECTION", "INTERVAL", "INTO", "INVOKER", "IOCOUNT", "IS", "ISOLATION", "ITERATE", "JAR", "JAVA", "JIS_COLL", "JOIN", "JOURNAL", "K", "KANJI1", "KANJISJIS", "KBYTE", "KBYTES", "KEEP", "KEY", "KEY_MEMBER", "KEY_TYPE", "KILOBYTES", "KURTOSIS", "LANGUAGE", "LARGE", "LAST", "LATERAL", "LATIN", "LE", "LEADING", "LEAVE", "LEFT", "LENGTH", "LEVEL", "LIKE", "LIMIT", "LN", "LOADING", "LOCAL", "LOCALTIME", "LOCALTIMESTAMP", "LOCATOR", "LOCK", "LOCKEDUSEREXPIRE", "LOCKING", "LOG", "LOGGING", "LOGON", "LONG", "LOOP", "LOW", "LOWER", "LT", "M", "MACRO", "MAP", "MATCH", "MATCHED", "MAVG", "MAX", "MAXCHAR", "MAXIMUM", "MAXLOGONATTEMPTS", "MAXVALUE", "MCHARACTERS", "MDIFF", "MEDIUM", "MEMBER", "MERGE", "MESSAGE_LENGTH", "MESSAGE_OCTET_LENGTH", "MESSAGE_TEXT", "METHOD", "MIN", "MINCHAR", "MINDEX", "MINIMUM", "MINUS", "MINUTE", "MINVALUE", "MLINREG", "MLOAD", "MOD", "MODE", "MODIFIED", "MODIFIES", "MODIFY", "MODULE", "MONITOR", "MONRESOURCE", "MONSESSION", "MONTH", "MORE", "MSUBSTR", "MSUM", "MULTINATIONAL", "MULTISET", "MUMPS", "NAME", "NAMED", "NAMES", "NATIONAL", "NATURAL", "NCHAR", "NCLOB", "NE", "NESTING", "NEW", "NEW_TABLE", "NEXT", "NO", "NONE", "NONOPTCOST", "NONOPTINIT", "NORMALIZE", "NORMALIZED", "NOT", "NOWAIT", "NULL", "NULLABLE", "NULLIF", "NULLIFZERO", "NULLS", "NUMBER", "NUMERIC", "OA", "OBJECT", "OBJECTS", "OCTET_LENGTH", "OCTETS", "OF", "OFF", "OLD", "OLD_TABLE", "ON", "ONLINE", "ONLY", "OPEN", "OPTION", "OPTIONS", "OR", "ORDER", "ORDERED_ANALYTIC", "ORDERING", "ORDINALITY", "OTHERS", "OUT", "OUTER", "OUTPUT", "OVER", "OVERLAPS", "OVERLAY", "OVERLAYS", "OVERRIDE", "OVERRIDING", "PAD", "PARAMETER", "PARAMETER_MODE", "PARAMETER_NAME", "PARAMETER_ORDINAL_POSITION", "PARAMETER_SPECIFIC_CATALOG", "PARAMETER_SPECIFIC_NAME", "PARAMETER_SPECIFIC_SCHEMA", "PARAMID", "PARTIAL", "PARTITION", "PARTITIONED", "PARTITION#L1", "PARTITION#L2", "PARTITION#L3", "PARTITION#L4", "PARTITION#L5", "PARTITION#L6", "PARTITION#L7", "PARTITION#L8", "PARTITION#L9", "PARTITION#L10", "PARTITION#L11", "PARTITION#L12", "PARTITION#L13", "PARTITION#L14", "PARTITION#L15", "PASCAL", "PASSWORD", "PATH", "PERCENT", "PERCENT_RANK", "PERCENTILE_CONT", "PERCENTILE_DISC", "PERM", "PERMANENT", "PLACING", "PLI", "POSITION", "POWER", "PRECEDING", "PRECISION", "PREPARE", "PRESERVE", "PRIMARY", "PRINT", "PRIOR", "PRIVATE", "PRIVILEGES", "PROCEDURE", "PROFILE", "PROTECTED", "PROTECTION", "PUBLIC", "QUALIFIED", "QUALIFY", "QUANTILE", "QUEUE", "QUERY", "QUERY_BAND", "RADIANS", "RANDOM", "RANDOMIZED", "RANGE", "RANGE#L1", "RANGE#L2", "RANGE#L3", "RANGE#L4", "RANGE#L5", "RANGE#L6", "RANGE#L7", "RANGE#L8", "RANGE#L9", "RANGE#L10", "RANGE#L11", "RANGE#L12", "RANGE#L13", "RANGE#L14", "RANGE#L15", "RANGE_N", "RANK", "READ", "READS", "REAL", "RECALC", "RECURSIVE", "REF", "REFERENCES", "REFERENCING", "REGR_AVGX", "REGR_AVGY", "REGR_COUNT", "REGR_INTERCEPT", "REGR_R2", "REGR_SLOPE", "REGR_SXX", "REGR_SXY", "REGR_SYY", "RELATIVE", "RELEASE", "RENAME", "REPEAT", "REPEATABLE", "REPLACE", "REPLACEMENT", "REPLCONTROL", "REPLICATION", "REQUEST", "RESTART", "RESTORE", "RESTRICT", "RESTRICTWORDS", "RESULT", "RESUME", "RET", "RETAIN", "RETRIEVE", "RETURN", "RETURNED_CARDINALITY", "RETURNED_LENGTH", "RETURNED_OCTET_LENGTH", "RETURNED_SQLSTATE", "RETURNS", "REUSE", "REVALIDATE", "REVOKE", "RIGHT", "RIGHTS", "ROLE", "ROLLBACK", "ROLLFORWARD", "ROLLUP", "ROUTINE", "ROUTINE_CATALOG", "ROUTINE_NAME", "ROUTINE_SCHEMA", "ROW", "ROW_COUNT", "ROW_NUMBER", "ROWID", "ROWS", "RU", "SAMPLE", "SAMPLEID", "SAMPLES", "SAVEPOINT", "SCALE", "SCHEMA", "SCHEMA_NAME", "SCOPE", "SCOPE_CATALOG", "SCOPE_NAME", "SCOPE_SCHEMA", "SCROLL", "SEARCH", "SEARCHSPACE", "SECOND", "SECTION", "SECURITY", "SEED", "SEL", "SELECT", "SELF", "SENSITIVE", "SEQUENCE", "SERIALIZABLE", "SERVER_NAME", "SESSION", "SESSION_USER", "SET", "SETRESRATE", "SETS", "SETSESSRATE", "SHARE", "SHOW", "SIMILAR", "SIMPLE", "SIN", "SINH", "SIZE", "SKEW", "SMALLINT", "SOME", "SOUNDEX", "SOURCE", "SPACE", "SPECCHAR", "SPECIFIC", "SPECIFIC_NAME", "SPECIFICTYPE", "SPL", "SPOOL", "SQL", "SQLDATA", "SQLEXCEPTION", "SQLSTATE", "SQLTEXT", "SQLWARNING", "SQRT", "SR", "SS", "START", "STARTUP", "STAT", "STATE", "STATEMENT", "STATIC", "STATISTICS", "STATS", "STDDEV_POP", "STDDEV_SAMP", "STEPINFO", "STRING_CS", "STRUCTURE", "STYLE", "SUBCLASS_ORIGIN", "SUBLIST", "SUBMULTISET", "SUBSCRIBER", "SUBSTR", "SUBSTRING", "SUM", "SUMMARY", "SUMMARYONLY", "SUSPEND", "SYMMETRIC", "SYSTEM", "SYSTEM_USER", "SYSTEMTEST", "TABLE", "TABLE_NAME", "TABLESAMPLE", "TAN", "TANH", "TARGET", "TBL_CS", "TD_GENERAL", "TD_INTERNAL", "TEMPORARY", "TERMINATE", "TEXT", "THAN", "THEN", "THRESHOLD", "TIES", "TIME", "TIMESTAMP", "TIMEZONE_HOUR", "TIMEZONE_MINUTE", "TITLE", "TO", "TOP", "TPA", "TOP_LEVEL_COUNT", "TRACE", "TRAILING", "TRANSACTION", "TRANSACTION_ACTIVE", "TRANSACTIONS_COMMITTED", "TRANSACTIONS_ROLLED_BACK", "TRANSFORM", "TRANSFORMS", "TRANSLATE", "TRANSLATE_CHK", "TRANSLATION", "TREAT", "TRIGGER", "TRIGGER_CATALOG", "TRIGGER_NAME", "TRIGGER_SCHEMA", "TRIM", "TRUE", "TYPE", "UC", "UDTCASTAS", "UDTCASTLPAREN", "UDTMETHOD", "UDTTYPE", "UDTUSAGE", "UESCAPE", "UNBOUNDED", "UNCOMMITTED", "UNDEFINED", "UNDER", "UNDO", "UNICODE", "UNION", "UNIQUE", "UNKNOWN", "UNNAMED", "UNNEST", "UNTIL", "UPD", "UPDATE", "UPPER", "UPPERCASE", "USAGE", "USE", "USER", "USER_DEFINED_TYPE_CATALOG", "USER_DEFINED_TYPE_CODE", "USER_DEFINED_TYPE_NAME", "USER_DEFINED_TYPE_SCHEMA", "USING", "VALUE", "VALUES", "VAR_POP", "VAR_SAMP", "VARBYTE", "VARCHAR", "VARGRAPHIC", "VARYING", "VIEW", "VOLATILE", "WAIT", "WARNING", "WHEN", "WHENEVER", "WHERE", "WHILE", "WIDTH_BUCKET", "WINDOW", "WITH", "WITHIN", "WITHOUT", "WORK", "WRITE", "YEAR", "ZEROIFNULL", "ZONE", "CTCONTROL", "EXPAND", "EXPANDING", "GLOP", "RESIGNAL", "SIGNAL", "UNTIL_CHANGED", "VARIANT_TYPE", "XMLPLAN"};
   private static String[] oracleDateFormat = new String[]{"CC", "SCC", "SYYYY", "YYYY", "YEAR", "SYEAR", "YYY", "YY", "Y", "IYYY", "IY", "I", "Q", "MONTH", "MON", "MM", "RM", "WW", "IW", "W", "DDD", "DD", "J", "DAY", "DY", "D", "HH", "HH12", "HH24", "MI"};
   private static String[] oracleTimeZonesArray = new String[]{"GMT", "CET", "CST", "CST6CDT", "CUBA", "EET", "EST", "EST5EDT", "EGYPT", "EIRE", "GB", "GB-EIRE", "GREENWICH", "HST", "HONGKONG", "ICELAND", "IRAN", "ISRAEL", "JAMAICA", "JAPAN", "KWAJALEIN", "LIBYA", "MET", "MST", "MST7MDT", "NZ", "NZ_CHAT", "NAVAJO", "PRC", "PST", "PST8PDT", "POLAND", "PORTUGAL", "ROC", "ROK", "SINGAPORE", "TURKEY", "UTC", "W_SU", "WET"};
   private static final ArrayList oracleTimeZones;
   private static ArrayList functionsReturningDate;
   private static ArrayList functionsReturningTimestamp;
   public static ArrayList swissqlMessageList;

   public static String[] getSystemFunctions(int dialecttype) {
      if (dialecttype == 2) {
         return sqlServerSystemFunctionsArray;
      } else if (dialecttype == 7) {
         return sybaseSystemFunctionsArray;
      } else if (dialecttype == 1) {
         return oracleSystemFunctionsArray;
      } else if (dialecttype == 5) {
         return mysqlSystemFunctionsArray;
      } else if (dialecttype == 3) {
         return db2SystemFunctionsArray;
      } else if (dialecttype == 4) {
         return postgresqlSystemFunctionsArray;
      } else {
         return dialecttype == 12 ? teradataSystemFunctionsArray : null;
      }
   }

   public static String[] getKeywords(int dialecttype) {
      if (dialecttype == 1) {
         return oracleKeywordsArray;
      } else if (dialecttype == 2) {
         return sqlServerKeywordsArray;
      } else if (dialecttype == 10) {
         return timestenReservedWordsArray;
      } else if (dialecttype == 11) {
         return netezzaReservedWordsArray;
      } else if (dialecttype == 12) {
         return teradataReservedWordsArray;
      } else {
         return dialecttype == 4 ? postgresqlReservedWordsArray : null;
      }
   }

   public static String[] getKeywords(String database) {
      return database.equalsIgnoreCase("teradata") ? teradataReservedWordsArray : null;
   }

   public static ArrayList getOracleTimeZones() {
      return oracleTimeZones;
   }

   public static CreateQueryStatement constructCQS(String tableName, SelectQueryStatement from_sqs, SwisSQLStatement to_sqs) {
      CreateQueryStatement cqs = new CreateQueryStatement();
      cqs.setCreate("CREATE");
      cqs.setTableOrView("TABLE");
      cqs.setClosedBraces(")");
      cqs.setOpenBraces("(");
      if (tableName != null) {
         TableObject to = new TableObject();
         to.setTableName(tableName);
         cqs.setTableObject(to);
      }

      SelectStatement fromSS = from_sqs.getSelectStatement();
      Vector sItems = fromSS.getSelectItemList();
      Vector ccVector = new Vector();
      FromClause fc = from_sqs.getFromClause();
      if (fc != null) {
         Vector fromItems = fc.getFromItemList();

         for(int i = 0; i < fromItems.size(); ++i) {
            Object obj = fromItems.get(i);
            if (obj instanceof FromTable) {
               Object tableObj = ((FromTable)obj).getTableName();
               if (tableObj instanceof String) {
                  tableName = tableObj.toString();
                  if (tableName.indexOf(".") != -1) {
                     tableName = tableName.substring(tableName.lastIndexOf(".") + 1, tableName.length());
                  }

                  Hashtable colDatatypeTable = (Hashtable)((Hashtable)CastingUtil.getValueIgnoreCase(SwisSQLAPI.getDataTypesFromMetaDataHT(), tableName));
                  if (colDatatypeTable == null) {
                     if (to_sqs instanceof SelectQueryStatement) {
                        ((SelectQueryStatement)to_sqs).setGeneralComments("/* SwisSQL Message : Metadata of the source database required for accurate conversion */");
                     } else if (to_sqs instanceof InsertQueryStatement) {
                        ((InsertQueryStatement)to_sqs).setGeneralComments("/* SwisSQL Message : Metadata of the source database required for accurate conversion */");
                     }
                  } else if (sItems.size() == 1 && sItems.get(0).toString().equals("*") && tableName != null) {
                     Set keys = colDatatypeTable.keySet();
                     Iterator it = keys.iterator();

                     while(it.hasNext()) {
                        Object col = it.next();
                        CreateColumn cc = new CreateColumn();
                        cc.setColumnName(col.toString());
                        Datatype datatype = constructDatatype((String)CastingUtil.getValueIgnoreCase(colDatatypeTable, col.toString()));
                        if (datatype == null) {
                           if (to_sqs instanceof SelectQueryStatement) {
                              ((SelectQueryStatement)to_sqs).setGeneralComments("/* SwisSQL Message : Metadata of the source database required for accurate conversion */");
                           } else if (to_sqs instanceof InsertQueryStatement) {
                              ((InsertQueryStatement)to_sqs).setGeneralComments("/* SwisSQL Message : Metadata of the source database required for accurate conversion */");
                           }
                        }

                        cc.setDatatype(datatype);
                        ccVector.add(cc);
                     }
                  } else {
                     for(int j = 0; j < sItems.size(); ++j) {
                        Object scObj = sItems.get(j);
                        int colCount = false;
                        if (scObj instanceof SelectColumn) {
                           SelectColumn sc = (SelectColumn)scObj;
                           Vector colExpr = sc.getColumnExpression();
                           if (colExpr.size() == 1 && colExpr.get(0) instanceof TableColumn) {
                              CreateColumn cc = new CreateColumn();
                              String colName = ((TableColumn)colExpr.get(0)).getColumnName();
                              if (sc.getAliasName() == null) {
                                 cc.setColumnName(colName);
                              } else {
                                 cc.setColumnName(sc.getAliasName());
                              }

                              Datatype datatype = constructDatatype((String)CastingUtil.getValueIgnoreCase(colDatatypeTable, colName));
                              if (datatype == null) {
                                 if (to_sqs instanceof SelectQueryStatement) {
                                    ((SelectQueryStatement)to_sqs).setGeneralComments("/* SwisSQL Message : Metadata of the source database required for accurate conversion */");
                                 } else if (to_sqs instanceof InsertQueryStatement) {
                                    ((InsertQueryStatement)to_sqs).setGeneralComments("/* SwisSQL Message : Metadata of the source database required for accurate conversion */");
                                 }
                              }

                              cc.setDatatype(datatype);
                              ccVector.add(cc);
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      cqs.setColumnNames(ccVector);
      return cqs;
   }

   public static Datatype constructDatatype(String datatypeName) {
      String size = null;
      if (datatypeName != null) {
         int index1 = datatypeName.indexOf("(");
         String tempType = datatypeName.toLowerCase();
         if (index1 != -1) {
            tempType = datatypeName.toLowerCase().substring(0, index1);
         }

         if (CreateColumn.getUserDefinedDatatypes().containsKey(tempType)) {
            datatypeName = (String)CreateColumn.getUserDefinedDatatypes().get(tempType);
         }

         int index = datatypeName.indexOf("(");
         if (index != -1) {
            size = datatypeName.substring(index + 1, datatypeName.length() - 1);
            datatypeName = datatypeName.substring(0, index);
         }

         if (datatypeName.toLowerCase().indexOf("char") != -1 || datatypeName.toLowerCase().indexOf("clob") != -1 || datatypeName.equalsIgnoreCase("text")) {
            CharacterClass cc = new CharacterClass();
            cc.setDatatypeName(datatypeName.toUpperCase());
            if (size != null) {
               cc.setOpenBrace("(");
               cc.setSize(size);
               cc.setClosedBrace(")");
            }

            return cc;
         }

         if (datatypeName.toLowerCase().indexOf("time") != -1 || datatypeName.toLowerCase().indexOf("date") != -1) {
            DateClass dc = new DateClass();
            dc.setDatatypeName(datatypeName.toUpperCase());
            return dc;
         }

         if (datatypeName.toLowerCase().indexOf("int") != -1 || datatypeName.toLowerCase().indexOf("number") != -1 || datatypeName.toLowerCase().indexOf("double") != -1 || datatypeName.equalsIgnoreCase("real") || datatypeName.equalsIgnoreCase("float") || datatypeName.toLowerCase().indexOf("dec") != -1 || datatypeName.equalsIgnoreCase("numeric") || datatypeName.toLowerCase().indexOf("money") != -1) {
            NumericClass nc = new NumericClass();
            nc.setDatatypeName(datatypeName.toUpperCase());
            if (size != null) {
               nc.setOpenBrace("(");
               nc.setSize(size);
               nc.setClosedBrace(")");
            }

            if (datatypeName.toLowerCase().indexOf("int") != -1 || datatypeName.toLowerCase().indexOf("double") != -1 || datatypeName.equalsIgnoreCase("real") || datatypeName.equalsIgnoreCase("float")) {
               nc.setOpenBrace((String)null);
               nc.setSize((String)null);
               nc.setClosedBrace((String)null);
            }

            return nc;
         }

         if (datatypeName.toLowerCase().indexOf("blob") != -1 || datatypeName.toLowerCase().indexOf("binary") != -1 || datatypeName.equalsIgnoreCase("bit") || datatypeName.equalsIgnoreCase("image") || datatypeName.toLowerCase().indexOf("bool") != -1 || datatypeName.equalsIgnoreCase("raw") || datatypeName.equalsIgnoreCase("longtext") || datatypeName.equalsIgnoreCase("mediumtext") || datatypeName.equalsIgnoreCase("tinytext")) {
            BinClass bc = new BinClass();
            bc.setDatatypeName(datatypeName.toUpperCase());
            if (size != null) {
               bc.setOpenBrace("(");
               bc.setSize(size);
               bc.setClosedBrace(")");
            }

            return bc;
         }
      }

      return null;
   }

   public static String getDateFormat(String dateTimeLiteralValue, int targetdb) {
      if (dateTimeLiteralValue.equals("''")) {
         return "'1900-01-01 00:00:00'";
      } else if (dateTimeLiteralValue.startsWith("'")) {
         dateTimeLiteralValue = dateTimeLiteralValue.substring(1, dateTimeLiteralValue.length() - 1);
         if (dateTimeLiteralValue.trim().length() == 0) {
            return "'1900-01-01 00:00:00'";
         } else {
            String dateformat = "";
            boolean secondsWithColon = false;
            String dateLiteralValue = dateTimeLiteralValue;
            String timeLiteralValue = "";
            int space = false;
            String tempStr;
            int len;
            int space;
            if ((space = dateTimeLiteralValue.indexOf(" ")) != -1 && space >= 5) {
               dateLiteralValue = dateTimeLiteralValue.substring(0, space);
               timeLiteralValue = dateTimeLiteralValue.substring(space + 1);
            } else if (dateTimeLiteralValue.indexOf(":") != -1 || dateTimeLiteralValue.toLowerCase().indexOf("am") != -1 || dateTimeLiteralValue.toLowerCase().indexOf("pm") != -1) {
               timeLiteralValue = dateTimeLiteralValue;
               dateLiteralValue = "";
               tempStr = dateTimeLiteralValue.toLowerCase();
               if (tempStr.indexOf("jan") != -1 || tempStr.indexOf("feb") != -1 || tempStr.indexOf("mar") != -1 || tempStr.indexOf("apr") != -1 || tempStr.indexOf("may") != -1 || tempStr.indexOf("jun") != -1 || tempStr.indexOf("jul") != -1 || tempStr.indexOf("aug") != -1 || tempStr.indexOf("sep") != -1 || tempStr.indexOf("oct") != -1 || tempStr.indexOf("nov") != -1 || tempStr.indexOf("dec") != -1) {
                  len = tempStr.indexOf(" ", 10);
                  if (len != -1) {
                     dateLiteralValue = dateTimeLiteralValue.substring(0, len);
                     timeLiteralValue = dateTimeLiteralValue.substring(len + 1);
                  } else {
                     len = tempStr.indexOf(" ", 6);
                     dateLiteralValue = dateTimeLiteralValue.substring(0, len);
                     timeLiteralValue = dateTimeLiteralValue.substring(len + 1);
                  }
               }
            }

            tempStr = dateLiteralValue.toLowerCase();
            int index;
            if (tempStr.indexOf("jan") == -1 && tempStr.indexOf("feb") == -1 && tempStr.indexOf("mar") == -1 && tempStr.indexOf("apr") == -1 && tempStr.indexOf("may") == -1 && tempStr.indexOf("jun") == -1 && tempStr.indexOf("jul") == -1 && tempStr.indexOf("aug") == -1 && tempStr.indexOf("sep") == -1 && tempStr.indexOf("oct") == -1 && tempStr.indexOf("nov") == -1 && tempStr.indexOf("dec") == -1) {
               len = dateLiteralValue.length();
               String seperator = "";
               if (len == 10 || len == 8 || len == 9) {
                  index = dateLiteralValue.indexOf("-");
                  if (index != -1) {
                     seperator = "-";
                  } else if ((index = dateLiteralValue.indexOf("/")) != -1) {
                     seperator = "/";
                  } else if ((index = dateLiteralValue.indexOf(".")) != -1) {
                     seperator = ".";
                  }

                  if (index == 2 && seperator != "") {
                     if (len != 10 && len != 9) {
                        if (len == 8) {
                           dateformat = dateformat + "MM" + seperator + "DD" + seperator + "YY";
                        }
                     } else {
                        dateformat = dateformat + "MM" + seperator + "DD" + seperator + "YYYY";
                     }
                  } else if (index == 1 && seperator != "") {
                     dateformat = dateformat + "MM" + seperator + "DD" + seperator + "YYYY";
                  } else if (index == 4 && seperator != "") {
                     dateformat = dateformat + "YYYY" + seperator + "MM" + seperator + "DD";
                  }
               }

               if (seperator == "") {
                  if (len == 8) {
                     if (targetdb == 10) {
                        return "'" + dateTimeLiteralValue + "'";
                     }

                     dateformat = dateformat + "YYYYMMDD";
                  } else if (len == 6) {
                     if (targetdb == 10) {
                        return "'" + dateTimeLiteralValue + "'";
                     }

                     dateformat = dateformat + "YYMMDD";
                  }
               }
            } else {
               while(tempStr.indexOf("  ") != -1) {
                  tempStr = tempStr.replaceAll("  ", " ");
               }

               StringTokenizer st = new StringTokenizer(tempStr, " ");

               for(boolean start = true; st.hasMoreTokens(); start = false) {
                  if (!start) {
                     dateformat = dateformat + " ";
                  }

                  String token = st.nextToken();
                  if (token.length() == 3) {
                     if (token.indexOf(",") != -1) {
                        dateformat = dateformat + "DD,";
                     } else {
                        dateformat = dateformat + "MON";
                     }
                  } else if (token.length() == 4) {
                     if (token.indexOf(",") != -1) {
                        dateformat = dateformat + "MON,";
                     } else {
                        dateformat = dateformat + "YYYY";
                     }
                  } else if (token.length() == 2) {
                     if (dateformat.indexOf("DD") != -1) {
                        dateformat = dateformat + "YY";
                     } else {
                        dateformat = dateformat + "DD";
                     }
                  } else if (token.length() == 1 && !token.equalsIgnoreCase(",")) {
                     dateformat = dateformat + "DD";
                  }
               }

               if (dateformat == "" && tempStr.length() > 0 && tempStr.indexOf(" ") == -1 && tempStr.indexOf("-") == 2 && tempStr.lastIndexOf("-") == 6) {
                  if (tempStr.length() == 11) {
                     dateformat = "DD-MON-YYYY";
                  } else {
                     dateformat = "DD-MON-YY";
                  }
               }
            }

            len = timeLiteralValue.length();
            if (len > 0) {
               if (dateformat != "") {
                  dateformat = dateformat + " ";
               }

               String[] time = timeLiteralValue.split(":");
               int index = false;
               String ms;
               int index1;
               if (time.length > 1) {
                  if (time.length >= 3) {
                     dateformat = dateformat + "HH24:MI:SS";
                     if (time.length > 3) {
                        secondsWithColon = true;
                        if (targetdb == 1) {
                           ms = time[3];
                           dateformat = dateformat + ":FF" + ms.length();
                        }
                     } else if (targetdb == 1) {
                        ms = time[2];
                        int index1 = true;
                        if ((index1 = ms.indexOf(".")) != -1) {
                           ms = ms.substring(index1 + 1);
                           dateformat = dateformat + ".FF" + ms.length();
                        }
                     }
                  } else if ((index = timeLiteralValue.toLowerCase().indexOf("am")) == -1 && (index = timeLiteralValue.toLowerCase().indexOf("pm")) == -1) {
                     dateformat = dateformat + "HH24:MI";
                  } else if (timeLiteralValue.toLowerCase().charAt(index) == 'a') {
                     dateformat = dateformat + "HH:MIAM";
                  } else {
                     dateformat = dateformat + "HH:MIPM";
                  }
               } else if ((index = timeLiteralValue.toLowerCase().indexOf("am")) != -1 || (index = timeLiteralValue.toLowerCase().indexOf("pm")) != -1) {
                  dateformat = dateformat + "HH";
                  ms = timeLiteralValue.substring(1);
                  int i;
                  if (CustomizeUtil.isStartsWithNum(ms)) {
                     if (index != 2) {
                        index1 = index - 2;

                        for(i = 0; i < index1; ++i) {
                           dateformat = dateformat + " ";
                        }
                     }
                  } else if (len != 3) {
                     index1 = index - 1;

                     for(i = 0; i < index1; ++i) {
                        dateformat = dateformat + " ";
                     }
                  }

                  if (timeLiteralValue.toLowerCase().charAt(index) == 'a') {
                     dateformat = dateformat + "AM";
                  } else {
                     dateformat = dateformat + "PM";
                  }
               }
            }

            if (dateformat == "") {
               return null;
            } else {
               if (targetdb == 10) {
                  if ((dateformat.equals("YYYY-MM-DD") || dateformat.equals("YYYY-MM-DD HH24:MI:SS") || dateformat.equals("HH24:MI:SS")) && !secondsWithColon) {
                     if (!dateformat.equals("YYYY-MM-DD") && !dateformat.equals("HH24:MI:SS")) {
                        return null;
                     }

                     return dateformat;
                  }
               } else if (targetdb == 1 && (dateformat.equals("DD-MON-YYYY") || dateformat.equals("DD-MON-YY"))) {
                  return null;
               }

               return "'" + dateformat + "'";
            }
         }
      } else {
         return null;
      }
   }

   public static HashMap truncateNames(List data, int validLength) {
      HashMap returnTruncatedMap = new HashMap();
      int size = data.size();
      int genValue = 0;

      for(int i = 0; i < size; ++i) {
         if (data.get(i) != null) {
            String val = (String)data.get(i);
            boolean addQuotes = false;
            if (val.startsWith("\"") && val.endsWith("\"")) {
               val = val.substring(1, val.length() - 1);
               addQuotes = true;
            }

            if (val.length() > validLength) {
               String temp = val.substring(0, validLength);
               boolean present = false;
               Set set = returnTruncatedMap.keySet();
               Iterator iter = set.iterator();

               String validCol;
               while(iter.hasNext()) {
                  Object next = iter.next();
                  validCol = (String)returnTruncatedMap.get(next);
                  if (validCol.startsWith("\"") && validCol.endsWith("\"")) {
                     validCol = validCol.substring(1, validCol.length() - 1);
                  }

                  if (validCol.equals(temp) && !next.equals(val)) {
                     present = true;
                     break;
                  }
               }

               if (!present) {
                  for(int j = 0; j < size; ++j) {
                     validCol = (String)data.get(j);
                     if (validCol.startsWith("\"") && validCol.endsWith("\"")) {
                        validCol = validCol.substring(1, validCol.length() - 1);
                     }

                     if (validCol.length() == validLength && validCol.equalsIgnoreCase(temp)) {
                        present = true;
                        break;
                     }
                  }
               }

               if (present) {
                  String intStr = "" + genValue;
                  int intlen = intStr.length();
                  String intStr2 = "" + (genValue + 1);
                  String cc;
                  if (intStr2.length() > intlen) {
                     cc = temp.substring(0, validLength - (intlen + 2));
                     cc = cc + "_" + (genValue + 1);
                     if (addQuotes) {
                        cc = "\"" + cc + "\"";
                     }

                     returnTruncatedMap.put(data.get(i), cc);
                     ++genValue;
                  } else {
                     cc = temp.substring(0, validLength - (intlen + 1));
                     cc = cc + "_" + (genValue + 1);
                     if (addQuotes) {
                        cc = "\"" + cc + "\"";
                     }

                     returnTruncatedMap.put(data.get(i), cc);
                     ++genValue;
                  }
               } else {
                  if (addQuotes) {
                     temp = "\"" + temp + "\"";
                  }

                  returnTruncatedMap.put(data.get(i), temp);
               }
            }
         }
      }

      if (SwisSQLAPI.enableObjectMapping) {
         objectNameMapping.putAll(returnTruncatedMap);
      }

      return returnTruncatedMap;
   }

   public static boolean isAggregateFunction(SelectColumn scol) {
      if (scol != null) {
         Vector colExprn = scol.getColumnExpression();

         for(int s = 0; s < colExprn.size(); ++s) {
            Object sObj = colExprn.get(s);
            if (sObj instanceof FunctionCalls) {
               FunctionCalls fc = (FunctionCalls)sObj;
               TableColumn tfc = fc.getFunctionName();
               if (tfc != null) {
                  String fnName = tfc.getColumnName();
                  if (fnName.equalsIgnoreCase("min") || fnName.equalsIgnoreCase("max") || fnName.equalsIgnoreCase("count") || fnName.equalsIgnoreCase("avg") || fnName.equalsIgnoreCase("sum")) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   public static void checkAndReplaceGroupByItem(SelectColumn sc, SelectQueryStatement from_sqs) {
      Vector scColExpr = sc.getColumnExpression();
      if (scColExpr.elementAt(0) instanceof TableColumn) {
         TableColumn scTableColumn = (TableColumn)scColExpr.elementAt(0);
         if (scTableColumn.getColumnName().toLowerCase().equalsIgnoreCase("date_trunc")) {
            Vector from_sqsSelectItem = from_sqs.getSelectStatement().getSelectItemList();

            for(int j = 0; j < from_sqsSelectItem.size(); ++j) {
               if (from_sqsSelectItem.get(j) instanceof SelectColumn) {
                  Vector from_sqsSelectItemColExpr = ((SelectColumn)from_sqsSelectItem.get(j)).getColumnExpression();

                  for(int jv = 0; jv < from_sqsSelectItemColExpr.size(); ++jv) {
                     if (from_sqsSelectItemColExpr.get(jv) instanceof TableColumn) {
                        TableColumn from_sqsSelectItemColExprTC = (TableColumn)from_sqsSelectItemColExpr.get(jv);
                        if (from_sqsSelectItemColExprTC.getColumnName().toLowerCase().equalsIgnoreCase("date_trunc")) {
                           sc.setColumnExpression(scColExpr);
                        }
                     }

                     if (from_sqsSelectItemColExpr.get(jv) instanceof FunctionCalls) {
                        FunctionCalls from_sqsSelectItemColExprFC = (FunctionCalls)from_sqsSelectItemColExpr.get(jv);
                        if (from_sqsSelectItemColExprFC.getFunctionNameAsAString().toLowerCase().equalsIgnoreCase("date_trunc")) {
                           sc.setColumnExpression(from_sqsSelectItemColExpr);
                        }
                     }
                  }
               }
            }
         }
      }

   }

   public static String getObjectNameFromMapping(String origObjName) {
      String targetObjName = (String)objectNameMapping.get(origObjName);
      if (targetObjName != null) {
         targetObjName.trim();
      }

      return targetObjName;
   }

   public static void setObjectNameForMapping(String origName, String targetName) {
      if (SwisSQLAPI.enableObjectMapping) {
         objectNameMapping.put(origName, targetName);
      }

   }

   public static String[] getOracleDateFormats() {
      return oracleDateFormat;
   }

   public static String getFunctionReturnType(String functionName, Vector functionArgs) {
      if (functionName != null) {
         if (functionName.equalsIgnoreCase("substring") || functionName.equalsIgnoreCase("char") || functionName.equalsIgnoreCase("lower") || functionName.equalsIgnoreCase("ltrim") || functionName.equalsIgnoreCase("replicate") || functionName.equalsIgnoreCase("right") || functionName.equalsIgnoreCase("rtrim") || functionName.equalsIgnoreCase("space") || functionName.equalsIgnoreCase("stuff") || functionName.equalsIgnoreCase("upper")) {
            return "string";
         }

         if (functionName.equalsIgnoreCase("convert")) {
            if (functionArgs != null && functionArgs.size() > 0 && functionArgs.get(0) instanceof CharacterClass) {
               return "string";
            }
         } else {
            if (CastingUtil.ContainsIgnoreCase(functionsReturningTimestamp, functionName)) {
               return "timestamp";
            }

            if (CastingUtil.ContainsIgnoreCase(functionsReturningDate, functionName)) {
               return "date";
            }

            if (!functionName.equalsIgnoreCase("round") && !functionName.equalsIgnoreCase("trunc")) {
               if (functionName.equalsIgnoreCase("cast") && functionArgs != null) {
                  for(int v = 0; v < functionArgs.size(); ++v) {
                     if (functionArgs.get(v) instanceof DateClass) {
                        DateClass dcType = (DateClass)functionArgs.get(v);
                        if (dcType.getDatatypeName() != null && dcType.getDatatypeName().equalsIgnoreCase("date")) {
                           return "date";
                        }

                        if (dcType.getDatatypeName() != null && dcType.getDatatypeName().equalsIgnoreCase("timestamp")) {
                           return "timestamp";
                        }
                     } else if (functionArgs.get(v) instanceof SelectColumn) {
                        Vector scColExp = ((SelectColumn)functionArgs.get(v)).getColumnExpression();

                        for(int v1 = 0; v1 < scColExp.size(); ++v1) {
                           if (scColExp.get(v1) instanceof DateClass) {
                              DateClass dcType = (DateClass)scColExp.get(v1);
                              if (dcType.getDatatypeName() != null && dcType.getDatatypeName().equalsIgnoreCase("date")) {
                                 return "date";
                              }

                              if (dcType.getDatatypeName() != null && dcType.getDatatypeName().equalsIgnoreCase("timestamp")) {
                                 return "timestamp";
                              }
                           }
                        }
                     }
                  }
               }
            } else if (functionArgs != null && functionArgs.size() > 1) {
               String fnArg = functionArgs.get(1).toString();
               if (fnArg.startsWith("'") && fnArg.endsWith("'")) {
                  fnArg = fnArg.substring(1, fnArg.length() - 1);
               }

               for(int i = 0; i < oracleDateFormat.length; ++i) {
                  String dateFmt = oracleDateFormat[i];
                  if (fnArg.equalsIgnoreCase(dateFmt)) {
                     return "date";
                  }
               }
            }
         }
      }

      return "none";
   }

   public static String convertDayToInterval(String day) {
      String intervalStr = "";
      int var2 = 86400;

      try {
         int dayValue = (int)(Double.parseDouble(day) * 100.0D);
         if (dayValue <= 0) {
            return day;
         }

         if (dayValue >= 100) {
            return "INTERVAL '" + dayValue / 100 + "' DAY";
         }

         int dayVal = 86400 * dayValue / 100;
         int hours = dayVal / 3600;
         int minutes = dayVal % 3600 / 60;
         int seconds = dayVal % 3600 % 60 / 60;
         String minuteStr = "" + minutes;
         if (minuteStr.length() == 1) {
            minuteStr = "0" + minuteStr;
         }

         String secondStr = "" + seconds;
         if (secondStr.length() == 1) {
            secondStr = "0" + secondStr;
         }

         intervalStr = "INTERVAL '" + hours + ":" + minuteStr + ":" + secondStr + "' HOUR TO SECOND";
      } catch (NumberFormatException var10) {
      }

      return intervalStr;
   }

   public static TableColumn getMappedFunctionName(HashMap functionMapping, TableColumn functionName) {
      if (functionMapping != null && functionName != null) {
         String originalFunctionName = functionName.getColumnName();
         String mappedFunctionName = originalFunctionName;
         if (functionMapping.containsKey(originalFunctionName.toUpperCase())) {
            mappedFunctionName = functionMapping.get(originalFunctionName.toUpperCase()).toString();
         }

         functionName.setColumnName(mappedFunctionName);
      }

      return functionName;
   }

   static {
      oracleTimeZones = new ArrayList(Arrays.asList(oracleTimeZonesArray));
      functionsReturningDate = new ArrayList(Arrays.asList("TO_DATE", "ADD_MONTHS", "LAST_DAY", "NEXT_DAY", "SYSDATE", "CURRENT_DATE"));
      functionsReturningTimestamp = new ArrayList(Arrays.asList("TO_TIMESTAMP", "SYSTIMESTAMP", "CURRENT_TIMESTAMP"));
      swissqlMessageList = new ArrayList();
   }
}
