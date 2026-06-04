package com.zoho.analytics.jdbc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

interface ZohoAnalyticsJDBCConstants {
   int SELECT_QUERY = 0;
   int INSERT_QUERY = 1;
   int UPDATE_QUERY = 2;
   int DELETE_QUERY = 3;
   int CREATE_TABLE_QUERY = 4;
   int CREATE_VIEW_QUERY = 5;
   int ALTER_TABLE_QUERY = 6;
   int DROP_QUERY = 7;
   int INT_RESULT = 0;
   String STRING_RESULT = "";
   boolean BOOLEAN_RESULT = false;
   String[] VIEW_PROPS = new String[]{"TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "TABLE_TYPE", "REMARKS", "TYPE_CAT", "TYPE_SCHEM", "TYPE_NAME", "SELF_REFERENCING_COL_NAME", "REF_GENERATION"};
   String[] COLUMN_PROPS = new String[]{"TABLE_CAT", "TABLE_SCHEM", "TABLE_NAME", "COLUMN_NAME", "DATA_TYPE", "TYPE_NAME", "COLUMN_SIZE", "BUFFER_LENGTH", "DECIMAL_DIGITS", "NUM_PREC_RADIX", "NULLABLE", "REMARKS", "COLUMN_DEF", "SQL_DATA_TYPE", "SQL_DATETIME_SUB", "CHAR_OCTET_LENGTH", "ORDINAL_POSITION", "IS_NULLABLE", "DATE_FORMAT"};
   String[] BEST_ROW_PROPS = new String[]{"SCOPE", "COLUMN_NAME", "DATA_TYPE", "TYPE_NAME", "COLUMN_SIZE", "BUFFER_LENGTH", "DECIMAL_DIGITS", "PSEUDO_COLUMN"};
   String[] DATA_TYPE_PROPS = new String[]{"TYPE_NAME", "DATA_TYPE", "PRECISION", "LITERAL_PREFIX", "LITERAL_SUFFIX", "CREATE_PARAMS", "NULLABLE", "CASE_SENSITIVE", "SEARCHABLE", "UNSIGNED_ATTRIBUTE", "FIXED_PREC_SCALE", "AUTO_INCREMENT", "LOCAL_TYPE_NAME", "MINIMUM_SCALE", "MAXIMUM_SCALE", "SQL_DATA_TYPE", "SQL_DATETIME_SUB", "NUM_PREC_RADIX"};
   String[] REFERENCE_KEYS_PROPS = new String[]{"PKTABLE_CAT", "PKTABLE_SCHEM", "PKTABLE_NAME", "PKCOLUMN_NAME", "FKTABLE_CAT", "FKTABLE_SCHEM", "FKTABLE_NAME", "FKCOLUMN_NAME", "KEY_SEQ", "UPDATE_RULE", "DELETE_RULE", "FK_NAME", "PK_NAME", "DEFERRABILITY"};
   ZohoAnalyticsConstants[] TABLE_TYPES = new ZohoAnalyticsConstants[]{new ZohoAnalyticsConstants("TABLE_TYPE", "TABLE"), new ZohoAnalyticsConstants("TABLE_TYPE", "VIEW")};
   ArrayList<String> GEO_ROLE_DATA_TYPE = new ArrayList(Arrays.asList("CONTINENT", "COUNTRY", "STATE", "COUNTY", "CITY", "ZIP_CODE", "LATITUDE", "LONGITUDE", "AIRPORT"));
   Map<Integer, Integer> ANALYTICS_SQL_DATATYPE_MAP = Collections.unmodifiableMap(new HashMap<Integer, Integer>() {
      {
         this.put(1, 12);
         this.put(2, 12);
         this.put(3, 12);
         this.put(4, -5);
         this.put(5, -5);
         this.put(6, 8);
         this.put(7, 8);
         this.put(8, 8);
         this.put(9, 93);
         this.put(10, -7);
         this.put(11, 12);
         this.put(12, 12);
         this.put(13, -5);
         this.put(14, -5);
         this.put(15, 12);
         this.put(16, 12);
         this.put(17, 12);
         this.put(18, -5);
         this.put(19, 12);
         this.put(20, 0);
         this.put(21, 12);
         this.put(22, 93);
         this.put(23, 8);
         this.put(24, 12);
         this.put(25, 12);
         this.put(26, 12);
      }
   });
   Map<String, String> HOST_NAME_VS_ACCOUNTS_URL = Collections.unmodifiableMap(new HashMap<String, String>() {
      {
         this.put("analyticsapi.zoho.com", "https://accounts.zoho.com");
         this.put("analyticsapi.zoho.eu", "https://accounts.zoho.eu");
         this.put("analyticsapi.zoho.in", "https://accounts.zoho.in");
         this.put("analyticsapi.zoho.com.au", "https://accounts.zoho.com.au");
         this.put("analyticsapi.zoho.com.cn", "https://accounts.zoho.com.cn");
         this.put("analyticsapi.zoho.jp", "https://accounts.zoho.jp");
         this.put("analyticsapi.zoho.sa", "https://accounts.zoho.sa");
         this.put("analyticsapi.zohocloud.ca", "https://accounts.zohocloud.ca");
      }
   });
   List<HashMap<String, String>> ZDB_SUBTYPES_LIST = new ArrayList<HashMap<String, String>>() {
      {
         this.add(new HashMap<String, String>() {
            {
               this.put("ZNAME", "PLAIN");
               this.put("TYPE_NAME", "Plain Text");
               this.put("DATA_TYPE", "CHAR");
               this.put("PRECISION", "-1");
               this.put("LITERAL_PREFIX", "'");
               this.put("LITERAL_SUFFIX", "'");
               this.put("CREATE_PARAMS", "500");
               this.put("NULLABLE", "1");
               this.put("CASE_SENSITIVE", "true");
               this.put("SEARCHABLE", "0");
               this.put("UNSIGNED_ATTRIBUTE", "false");
               this.put("FIXED_PREC_SCALE", "false");
               this.put("AUTO_INCREMENT", "false");
               this.put("LOCAL_TYPE_NAME", "");
               this.put("MINIMUM_SCALE", "");
               this.put("MAXIMUM_SCALE", "");
               this.put("SQL_DATA_TYPE", "12");
               this.put("SQL_DATETIME_SUB", "0");
               this.put("NUM_PREC_RADIX", "10");
               this.put("MAXSIZE", "100");
            }
         });
         this.add(new HashMap<String, String>() {
            {
               this.put("ZNAME", "CURRENCY");
               this.put("TYPE_NAME", "Currency");
               this.put("DATA_TYPE", "DOUBLE");
               this.put("PRECISION", "-1");
               this.put("LITERAL_PREFIX", "'");
               this.put("LITERAL_SUFFIX", "'");
               this.put("CREATE_PARAMS", "500");
               this.put("NULLABLE", "1");
               this.put("CASE_SENSITIVE", "true");
               this.put("SEARCHABLE", "0");
               this.put("UNSIGNED_ATTRIBUTE", "false");
               this.put("FIXED_PREC_SCALE", "false");
               this.put("AUTO_INCREMENT", "false");
               this.put("LOCAL_TYPE_NAME", "");
               this.put("MINIMUM_SCALE", "");
               this.put("MAXIMUM_SCALE", "");
               this.put("SQL_DATA_TYPE", "8");
               this.put("SQL_DATETIME_SUB", "0");
               this.put("NUM_PREC_RADIX", "10");
               this.put("MAXSIZE", "19");
            }
         });
         this.add(new HashMap<String, String>() {
            {
               this.put("ZNAME", "REFCOL");
               this.put("TYPE_NAME", "Lookup Column");
               this.put("DATA_TYPE", "BIGINT");
               this.put("PRECISION", "-1");
               this.put("LITERAL_PREFIX", "'");
               this.put("LITERAL_SUFFIX", "'");
               this.put("CREATE_PARAMS", "500");
               this.put("NULLABLE", "1");
               this.put("CASE_SENSITIVE", "true");
               this.put("SEARCHABLE", "0");
               this.put("UNSIGNED_ATTRIBUTE", "false");
               this.put("FIXED_PREC_SCALE", "false");
               this.put("AUTO_INCREMENT", "false");
               this.put("LOCAL_TYPE_NAME", "");
               this.put("MINIMUM_SCALE", "");
               this.put("MAXIMUM_SCALE", "");
               this.put("SQL_DATA_TYPE", "-5");
               this.put("SQL_DATETIME_SUB", "0");
               this.put("NUM_PREC_RADIX", "10");
               this.put("MAXSIZE", "19");
            }
         });
         this.add(new HashMap<String, String>() {
            {
               this.put("ZNAME", "NUMBER");
               this.put("TYPE_NAME", "Number");
               this.put("DATA_TYPE", "BIGINT");
               this.put("PRECISION", "-1");
               this.put("LITERAL_PREFIX", "'");
               this.put("LITERAL_SUFFIX", "'");
               this.put("CREATE_PARAMS", "500");
               this.put("NULLABLE", "1");
               this.put("CASE_SENSITIVE", "true");
               this.put("SEARCHABLE", "0");
               this.put("UNSIGNED_ATTRIBUTE", "false");
               this.put("FIXED_PREC_SCALE", "false");
               this.put("AUTO_INCREMENT", "false");
               this.put("LOCAL_TYPE_NAME", "");
               this.put("MINIMUM_SCALE", "");
               this.put("MAXIMUM_SCALE", "");
               this.put("SQL_DATA_TYPE", "-5");
               this.put("SQL_DATETIME_SUB", "0");
               this.put("NUM_PREC_RADIX", "10");
               this.put("MAXSIZE", "19");
            }
         });
         this.add(new HashMap<String, String>() {
            {
               this.put("ZNAME", "RELCOL");
               this.put("TYPE_NAME", "Lookup Column");
               this.put("DATA_TYPE", "BIGINT");
               this.put("PRECISION", "-1");
               this.put("LITERAL_PREFIX", "'");
               this.put("LITERAL_SUFFIX", "'");
               this.put("CREATE_PARAMS", "500");
               this.put("NULLABLE", "1");
               this.put("CASE_SENSITIVE", "true");
               this.put("SEARCHABLE", "0");
               this.put("UNSIGNED_ATTRIBUTE", "");
               this.put("FIXED_PREC_SCALE", "false");
               this.put("AUTO_INCREMENT", "false");
               this.put("LOCAL_TYPE_NAME", "");
               this.put("MINIMUM_SCALE", "");
               this.put("MAXIMUM_SCALE", "");
               this.put("SQL_DATA_TYPE", "0");
               this.put("SQL_DATETIME_SUB", "0");
               this.put("NUM_PREC_RADIX", "10");
               this.put("MAXSIZE", "19");
            }
         });
         this.add(new HashMap<String, String>() {
            {
               this.put("ZNAME", "FORMULACOL");
               this.put("TYPE_NAME", "Formula Column");
               this.put("DATA_TYPE", "TEXT");
               this.put("PRECISION", "-1");
               this.put("LITERAL_PREFIX", "'");
               this.put("LITERAL_SUFFIX", "'");
               this.put("CREATE_PARAMS", "500");
               this.put("NULLABLE", "1");
               this.put("CASE_SENSITIVE", "true");
               this.put("SEARCHABLE", "0");
               this.put("UNSIGNED_ATTRIBUTE", "false");
               this.put("FIXED_PREC_SCALE", "false");
               this.put("AUTO_INCREMENT", "false");
               this.put("LOCAL_TYPE_NAME", "");
               this.put("MINIMUM_SCALE", "");
               this.put("MAXIMUM_SCALE", "");
               this.put("SQL_DATA_TYPE", "12");
               this.put("SQL_DATETIME_SUB", "0");
               this.put("NUM_PREC_RADIX", "10");
               this.put("MAXSIZE", "0");
            }
         });
         this.add(new HashMap<String, String>() {
            {
               this.put("ZNAME", "TIME");
               this.put("TYPE_NAME", "TIME Column");
               this.put("DATA_TYPE", "TIME");
               this.put("PRECISION", "6");
               this.put("LITERAL_PREFIX", "'");
               this.put("LITERAL_SUFFIX", "'");
               this.put("CREATE_PARAMS", "");
               this.put("NULLABLE", "1");
               this.put("CASE_SENSITIVE", "true");
               this.put("SEARCHABLE", "0");
               this.put("UNSIGNED_ATTRIBUTE", "false");
               this.put("FIXED_PREC_SCALE", "true");
               this.put("AUTO_INCREMENT", "false");
               this.put("LOCAL_TYPE_NAME", "");
               this.put("MINIMUM_SCALE", "");
               this.put("MAXIMUM_SCALE", "");
               this.put("SQL_DATA_TYPE", "12");
               this.put("SQL_DATETIME_SUB", "0");
               this.put("NUM_PREC_RADIX", "10");
               this.put("MAXSIZE", "500");
            }
         });
         this.add(new HashMap<String, String>() {
            {
               this.put("ZNAME", "DECIMAL_NUMBER");
               this.put("TYPE_NAME", "Decimal Number");
               this.put("DATA_TYPE", "DOUBLE");
               this.put("PRECISION", "-1");
               this.put("LITERAL_PREFIX", "'");
               this.put("LITERAL_SUFFIX", "'");
               this.put("CREATE_PARAMS", "500");
               this.put("NULLABLE", "1");
               this.put("CASE_SENSITIVE", "true");
               this.put("SEARCHABLE", "0");
               this.put("UNSIGNED_ATTRIBUTE", "false");
               this.put("FIXED_PREC_SCALE", "false");
               this.put("AUTO_INCREMENT", "false");
               this.put("LOCAL_TYPE_NAME", "");
               this.put("MINIMUM_SCALE", "");
               this.put("MAXIMUM_SCALE", "");
               this.put("SQL_DATA_TYPE", "8");
               this.put("SQL_DATETIME_SUB", "0");
               this.put("NUM_PREC_RADIX", "10");
               this.put("MAXSIZE", "19");
            }
         });
         this.add(new HashMap<String, String>() {
            {
               this.put("ZNAME", "AUTO");
               this.put("TYPE_NAME", "Auto");
               this.put("DATA_TYPE", "BIGINT");
               this.put("PRECISION", "-1");
               this.put("LITERAL_PREFIX", "'");
               this.put("LITERAL_SUFFIX", "'");
               this.put("CREATE_PARAMS", "500");
               this.put("NULLABLE", "1");
               this.put("CASE_SENSITIVE", "true");
               this.put("SEARCHABLE", "0");
               this.put("UNSIGNED_ATTRIBUTE", "false");
               this.put("FIXED_PREC_SCALE", "false");
               this.put("AUTO_INCREMENT", "false");
               this.put("LOCAL_TYPE_NAME", "");
               this.put("MINIMUM_SCALE", "");
               this.put("MAXIMUM_SCALE", "");
               this.put("SQL_DATA_TYPE", "-5");
               this.put("SQL_DATETIME_SUB", "0");
               this.put("NUM_PREC_RADIX", "10");
               this.put("MAXSIZE", "19");
            }
         });
         this.add(new HashMap<String, String>() {
            {
               this.put("ZNAME", "URL");
               this.put("TYPE_NAME", "URL");
               this.put("DATA_TYPE", "CHAR");
               this.put("PRECISION", "-1");
               this.put("LITERAL_PREFIX", "'");
               this.put("LITERAL_SUFFIX", "'");
               this.put("CREATE_PARAMS", "500");
               this.put("NULLABLE", "1");
               this.put("CASE_SENSITIVE", "true");
               this.put("SEARCHABLE", "0");
               this.put("UNSIGNED_ATTRIBUTE", "false");
               this.put("FIXED_PREC_SCALE", "false");
               this.put("AUTO_INCREMENT", "false");
               this.put("LOCAL_TYPE_NAME", "");
               this.put("MINIMUM_SCALE", "");
               this.put("MAXIMUM_SCALE", "");
               this.put("SQL_DATA_TYPE", "12");
               this.put("SQL_DATETIME_SUB", "0");
               this.put("NUM_PREC_RADIX", "10");
               this.put("MAXSIZE", "1000");
            }
         });
         this.add(new HashMap<String, String>() {
            {
               this.put("ZNAME", "DEFAULTVAL");
               this.put("TYPE_NAME", "Default Value Column");
               this.put("DATA_TYPE", "TEXT");
               this.put("PRECISION", "-1");
               this.put("LITERAL_PREFIX", "'");
               this.put("LITERAL_SUFFIX", "'");
               this.put("CREATE_PARAMS", "500");
               this.put("NULLABLE", "1");
               this.put("CASE_SENSITIVE", "true");
               this.put("SEARCHABLE", "0");
               this.put("UNSIGNED_ATTRIBUTE", "false");
               this.put("FIXED_PREC_SCALE", "false");
               this.put("AUTO_INCREMENT", "false");
               this.put("LOCAL_TYPE_NAME", "");
               this.put("MINIMUM_SCALE", "");
               this.put("MAXIMUM_SCALE", "");
               this.put("SQL_DATA_TYPE", "12");
               this.put("SQL_DATETIME_SUB", "0");
               this.put("NUM_PREC_RADIX", "10");
               this.put("MAXSIZE", "0");
            }
         });
         this.add(new HashMap<String, String>() {
            {
               this.put("ZNAME", "BOOLEAN");
               this.put("TYPE_NAME", "Yes/No Decision");
               this.put("DATA_TYPE", "BOOLEAN");
               this.put("PRECISION", "-1");
               this.put("LITERAL_PREFIX", "'");
               this.put("LITERAL_SUFFIX", "'");
               this.put("CREATE_PARAMS", "500");
               this.put("NULLABLE", "1");
               this.put("CASE_SENSITIVE", "true");
               this.put("SEARCHABLE", "0");
               this.put("UNSIGNED_ATTRIBUTE", "false");
               this.put("FIXED_PREC_SCALE", "false");
               this.put("AUTO_INCREMENT", "false");
               this.put("LOCAL_TYPE_NAME", "");
               this.put("MINIMUM_SCALE", "");
               this.put("MAXIMUM_SCALE", "");
               this.put("SQL_DATA_TYPE", "-7");
               this.put("SQL_DATETIME_SUB", "0");
               this.put("NUM_PREC_RADIX", "10");
               this.put("MAXSIZE", "1");
            }
         });
         this.add(new HashMap<String, String>() {
            {
               this.put("ZNAME", "GEOMETRY");
               this.put("TYPE_NAME", "Geometry Column");
               this.put("DATA_TYPE", "CHAR");
               this.put("PRECISION", "-1");
               this.put("LITERAL_PREFIX", "'");
               this.put("LITERAL_SUFFIX", "'");
               this.put("CREATE_PARAMS", "");
               this.put("NULLABLE", "1");
               this.put("CASE_SENSITIVE", "true");
               this.put("SEARCHABLE", "0");
               this.put("UNSIGNED_ATTRIBUTE", "false");
               this.put("FIXED_PREC_SCALE", "false");
               this.put("AUTO_INCREMENT", "false");
               this.put("LOCAL_TYPE_NAME", "");
               this.put("MINIMUM_SCALE", "");
               this.put("MAXIMUM_SCALE", "");
               this.put("SQL_DATA_TYPE", "12");
               this.put("SQL_DATETIME_SUB", "0");
               this.put("NUM_PREC_RADIX", "10");
               this.put("MAXSIZE", "500");
            }
         });
         this.add(new HashMap<String, String>() {
            {
               this.put("ZNAME", "MULTI_LINE");
               this.put("TYPE_NAME", "Multi Line Text");
               this.put("DATA_TYPE", "CHAR");
               this.put("PRECISION", "-1");
               this.put("LITERAL_PREFIX", "'");
               this.put("LITERAL_SUFFIX", "'");
               this.put("CREATE_PARAMS", "500");
               this.put("NULLABLE", "1");
               this.put("CASE_SENSITIVE", "true");
               this.put("SEARCHABLE", "0");
               this.put("UNSIGNED_ATTRIBUTE", "false");
               this.put("FIXED_PREC_SCALE", "false");
               this.put("AUTO_INCREMENT", "false");
               this.put("LOCAL_TYPE_NAME", "");
               this.put("MINIMUM_SCALE", "");
               this.put("MAXIMUM_SCALE", "");
               this.put("SQL_DATA_TYPE", "12");
               this.put("SQL_DATETIME_SUB", "0");
               this.put("NUM_PREC_RADIX", "10");
               this.put("MAXSIZE", "500");
            }
         });
         this.add(new HashMap<String, String>() {
            {
               this.put("ZNAME", "DATE");
               this.put("TYPE_NAME", "Date");
               this.put("DATA_TYPE", "BIGINT");
               this.put("PRECISION", "-1");
               this.put("LITERAL_PREFIX", "'");
               this.put("LITERAL_SUFFIX", "'");
               this.put("CREATE_PARAMS", "500");
               this.put("NULLABLE", "1");
               this.put("CASE_SENSITIVE", "true");
               this.put("SEARCHABLE", "0");
               this.put("UNSIGNED_ATTRIBUTE", "false");
               this.put("FIXED_PREC_SCALE", "false");
               this.put("AUTO_INCREMENT", "false");
               this.put("LOCAL_TYPE_NAME", "");
               this.put("MINIMUM_SCALE", "");
               this.put("MAXIMUM_SCALE", "");
               this.put("SQL_DATA_TYPE", "93");
               this.put("SQL_DATETIME_SUB", "0");
               this.put("NUM_PREC_RADIX", "10");
               this.put("MAXSIZE", "19");
            }
         });
         this.add(new HashMap<String, String>() {
            {
               this.put("ZNAME", "NAVIG");
               this.put("TYPE_NAME", "Navig");
               this.put("DATA_TYPE", "TEXT");
               this.put("PRECISION", "-1");
               this.put("LITERAL_PREFIX", "'");
               this.put("LITERAL_SUFFIX", "'");
               this.put("CREATE_PARAMS", "500");
               this.put("NULLABLE", "1");
               this.put("CASE_SENSITIVE", "true");
               this.put("SEARCHABLE", "0");
               this.put("UNSIGNED_ATTRIBUTE", "false");
               this.put("FIXED_PREC_SCALE", "false");
               this.put("AUTO_INCREMENT", "false");
               this.put("LOCAL_TYPE_NAME", "");
               this.put("MINIMUM_SCALE", "");
               this.put("MAXIMUM_SCALE", "");
               this.put("SQL_DATA_TYPE", "12");
               this.put("SQL_DATETIME_SUB", "0");
               this.put("NUM_PREC_RADIX", "10");
               this.put("MAXSIZE", "1000");
            }
         });
         this.add(new HashMap<String, String>() {
            {
               this.put("ZNAME", "PERCENT");
               this.put("TYPE_NAME", "Percentage");
               this.put("DATA_TYPE", "DOUBLE");
               this.put("PRECISION", "-1");
               this.put("LITERAL_PREFIX", "'");
               this.put("LITERAL_SUFFIX", "'");
               this.put("CREATE_PARAMS", "500");
               this.put("NULLABLE", "1");
               this.put("CASE_SENSITIVE", "true");
               this.put("SEARCHABLE", "0");
               this.put("UNSIGNED_ATTRIBUTE", "false");
               this.put("FIXED_PREC_SCALE", "false");
               this.put("AUTO_INCREMENT", "false");
               this.put("LOCAL_TYPE_NAME", "");
               this.put("MINIMUM_SCALE", "");
               this.put("MAXIMUM_SCALE", "");
               this.put("SQL_DATA_TYPE", "8");
               this.put("SQL_DATETIME_SUB", "0");
               this.put("NUM_PREC_RADIX", "10");
               this.put("MAXSIZE", "19");
            }
         });
         this.add(new HashMap<String, String>() {
            {
               this.put("ZNAME", "GEO");
               this.put("TYPE_NAME", "Geo Column");
               this.put("DATA_TYPE", "CHAR");
               this.put("PRECISION", "-1");
               this.put("LITERAL_PREFIX", "'");
               this.put("LITERAL_SUFFIX", "'");
               this.put("CREATE_PARAMS", "");
               this.put("NULLABLE", "1");
               this.put("CASE_SENSITIVE", "true");
               this.put("SEARCHABLE", "0");
               this.put("UNSIGNED_ATTRIBUTE", "false");
               this.put("FIXED_PREC_SCALE", "false");
               this.put("AUTO_INCREMENT", "false");
               this.put("LOCAL_TYPE_NAME", "");
               this.put("MINIMUM_SCALE", "");
               this.put("MAXIMUM_SCALE", "");
               this.put("SQL_DATA_TYPE", "12");
               this.put("SQL_DATETIME_SUB", "0");
               this.put("NUM_PREC_RADIX", "10");
               this.put("MAXSIZE", "100");
            }
         });
         this.add(new HashMap<String, String>() {
            {
               this.put("ZNAME", "DATE_AS_DATE");
               this.put("TYPE_NAME", "Date");
               this.put("DATA_TYPE", "TIMESTAMP");
               this.put("PRECISION", "-1");
               this.put("LITERAL_PREFIX", "'");
               this.put("LITERAL_SUFFIX", "'");
               this.put("CREATE_PARAMS", "");
               this.put("NULLABLE", "1");
               this.put("CASE_SENSITIVE", "true");
               this.put("SEARCHABLE", "0");
               this.put("UNSIGNED_ATTRIBUTE", "false");
               this.put("FIXED_PREC_SCALE", "false");
               this.put("AUTO_INCREMENT", "false");
               this.put("LOCAL_TYPE_NAME", "");
               this.put("MINIMUM_SCALE", "");
               this.put("MAXIMUM_SCALE", "");
               this.put("SQL_DATA_TYPE", "93");
               this.put("SQL_DATETIME_SUB", "0");
               this.put("NUM_PREC_RADIX", "10");
               this.put("MAXSIZE", "19");
            }
         });
         this.add(new HashMap<String, String>() {
            {
               this.put("ZNAME", "DURATION");
               this.put("TYPE_NAME", "Duration Column");
               this.put("DATA_TYPE", "DECIMAL");
               this.put("PRECISION", "6");
               this.put("LITERAL_PREFIX", "'");
               this.put("LITERAL_SUFFIX", "'");
               this.put("CREATE_PARAMS", "");
               this.put("NULLABLE", "1");
               this.put("CASE_SENSITIVE", "true");
               this.put("SEARCHABLE", "0");
               this.put("UNSIGNED_ATTRIBUTE", "false");
               this.put("FIXED_PREC_SCALE", "true");
               this.put("AUTO_INCREMENT", "false");
               this.put("LOCAL_TYPE_NAME", "");
               this.put("MINIMUM_SCALE", "");
               this.put("MAXIMUM_SCALE", "");
               this.put("SQL_DATA_TYPE", "12");
               this.put("SQL_DATETIME_SUB", "0");
               this.put("NUM_PREC_RADIX", "10");
               this.put("MAXSIZE", "23");
            }
         });
         this.add(new HashMap<String, String>() {
            {
               this.put("ZNAME", "EMAIL");
               this.put("TYPE_NAME", "E-Mail");
               this.put("DATA_TYPE", "CHAR");
               this.put("PRECISION", "-1");
               this.put("LITERAL_PREFIX", "'");
               this.put("LITERAL_SUFFIX", "'");
               this.put("CREATE_PARAMS", "500");
               this.put("NULLABLE", "1");
               this.put("CASE_SENSITIVE", "true");
               this.put("SEARCHABLE", "0");
               this.put("UNSIGNED_ATTRIBUTE", "false");
               this.put("FIXED_PREC_SCALE", "false");
               this.put("AUTO_INCREMENT", "false");
               this.put("LOCAL_TYPE_NAME", "");
               this.put("MINIMUM_SCALE", "");
               this.put("MAXIMUM_SCALE", "");
               this.put("SQL_DATA_TYPE", "12");
               this.put("SQL_DATETIME_SUB", "0");
               this.put("NUM_PREC_RADIX", "10");
               this.put("MAXSIZE", "100");
            }
         });
         this.add(new HashMap<String, String>() {
            {
               this.put("ZNAME", "FKCOL");
               this.put("TYPE_NAME", "FKCOL");
               this.put("DATA_TYPE", "TEXT");
               this.put("PRECISION", "-1");
               this.put("LITERAL_PREFIX", "'");
               this.put("LITERAL_SUFFIX", "'");
               this.put("CREATE_PARAMS", "500");
               this.put("NULLABLE", "1");
               this.put("CASE_SENSITIVE", "true");
               this.put("SEARCHABLE", "0");
               this.put("UNSIGNED_ATTRIBUTE", "false");
               this.put("FIXED_PREC_SCALE", "false");
               this.put("AUTO_INCREMENT", "false");
               this.put("LOCAL_TYPE_NAME", "");
               this.put("MINIMUM_SCALE", "");
               this.put("MAXIMUM_SCALE", "");
               this.put("SQL_DATA_TYPE", "12");
               this.put("SQL_DATETIME_SUB", "0");
               this.put("NUM_PREC_RADIX", "10");
               this.put("MAXSIZE", "0");
            }
         });
         this.add(new HashMap<String, String>() {
            {
               this.put("ZNAME", "AUTO_NUMBER");
               this.put("TYPE_NAME", "Auto Number");
               this.put("DATA_TYPE", "BIGINT");
               this.put("PRECISION", "-1");
               this.put("LITERAL_PREFIX", "'");
               this.put("LITERAL_SUFFIX", "'");
               this.put("CREATE_PARAMS", "500");
               this.put("NULLABLE", "1");
               this.put("CASE_SENSITIVE", "true");
               this.put("SEARCHABLE", "0");
               this.put("UNSIGNED_ATTRIBUTE", "false");
               this.put("FIXED_PREC_SCALE", "false");
               this.put("AUTO_INCREMENT", "true");
               this.put("LOCAL_TYPE_NAME", "");
               this.put("MINIMUM_SCALE", "");
               this.put("MAXIMUM_SCALE", "");
               this.put("SQL_DATA_TYPE", "-5");
               this.put("SQL_DATETIME_SUB", "0");
               this.put("NUM_PREC_RADIX", "10");
               this.put("MAXSIZE", "19");
            }
         });
         this.add(new HashMap<String, String>() {
            {
               this.put("ZNAME", "DATATYPE");
               this.put("TYPE_NAME", "DataType");
               this.put("DATA_TYPE", "TEXT");
               this.put("PRECISION", "-1");
               this.put("LITERAL_PREFIX", "'");
               this.put("LITERAL_SUFFIX", "'");
               this.put("CREATE_PARAMS", "500");
               this.put("NULLABLE", "1");
               this.put("CASE_SENSITIVE", "true");
               this.put("SEARCHABLE", "0");
               this.put("UNSIGNED_ATTRIBUTE", "false");
               this.put("FIXED_PREC_SCALE", "false");
               this.put("AUTO_INCREMENT", "false");
               this.put("LOCAL_TYPE_NAME", "");
               this.put("MINIMUM_SCALE", "");
               this.put("MAXIMUM_SCALE", "");
               this.put("SQL_DATA_TYPE", "12");
               this.put("SQL_DATETIME_SUB", "0");
               this.put("NUM_PREC_RADIX", "10");
               this.put("MAXSIZE", "0");
            }
         });
         this.add(new HashMap<String, String>() {
            {
               this.put("ZNAME", "POSITIVE_NUMBER");
               this.put("TYPE_NAME", "Positive Number");
               this.put("DATA_TYPE", "BIGINT");
               this.put("PRECISION", "-1");
               this.put("LITERAL_PREFIX", "'");
               this.put("LITERAL_SUFFIX", "'");
               this.put("CREATE_PARAMS", "500");
               this.put("NULLABLE", "1");
               this.put("CASE_SENSITIVE", "true");
               this.put("SEARCHABLE", "0");
               this.put("UNSIGNED_ATTRIBUTE", "true");
               this.put("FIXED_PREC_SCALE", "false");
               this.put("AUTO_INCREMENT", "false");
               this.put("LOCAL_TYPE_NAME", "");
               this.put("MINIMUM_SCALE", "");
               this.put("MAXIMUM_SCALE", "");
               this.put("SQL_DATA_TYPE", "-5");
               this.put("SQL_DATETIME_SUB", "0");
               this.put("NUM_PREC_RADIX", "10");
               this.put("MAXSIZE", "19");
            }
         });
         this.add(new HashMap<String, String>() {
            {
               this.put("ZNAME", "GEO_NUM");
               this.put("TYPE_NAME", "Geo Column");
               this.put("DATA_TYPE", "DOUBLE");
               this.put("PRECISION", "-1");
               this.put("LITERAL_PREFIX", "'");
               this.put("LITERAL_SUFFIX", "'");
               this.put("CREATE_PARAMS", "");
               this.put("NULLABLE", "1");
               this.put("CASE_SENSITIVE", "true");
               this.put("SEARCHABLE", "0");
               this.put("UNSIGNED_ATTRIBUTE", "true");
               this.put("FIXED_PREC_SCALE", "false");
               this.put("AUTO_INCREMENT", "false");
               this.put("LOCAL_TYPE_NAME", "");
               this.put("MINIMUM_SCALE", "");
               this.put("MAXIMUM_SCALE", "");
               this.put("SQL_DATA_TYPE", "8");
               this.put("SQL_DATETIME_SUB", "0");
               this.put("NUM_PREC_RADIX", "10");
               this.put("MAXSIZE", "19");
            }
         });
      }
   };
   ZohoAnalyticsConstants[] ZDB_SUBTYPES = (ZohoAnalyticsConstants[])ZDB_SUBTYPES_LIST.stream().map(ZohoAnalyticsConstants::new).toArray((x$0) -> {
      return new ZohoAnalyticsConstants[x$0];
   });
   Map<String, Object> ANALYTICS_SERVER_PROPS = Collections.unmodifiableMap(new HashMap<String, Object>() {
      {
         this.put("CATALOGSINPRIVILEGEDEFINITIONS", (Object)null);
         this.put("OPENSTATEMENTSACROSSCOMMIT", (Object)null);
         this.put("COLUMNALIASING", (Object)null);
         this.put("TABLETYPES", (Object)null);
         this.put("MAXCOLUMNSININDEX", (Object)null);
         this.put("NULLPLUSNONNULLISNULL", true);
         this.put("CATALOGS", (Object)null);
         this.put("DELETESAREDETECTED_1005", false);
         this.put("DELETESAREDETECTED_1004", false);
         this.put("CATALOGSINTABLEDEFINITIONS", (Object)null);
         this.put("DELETESAREDETECTED_1003", false);
         this.put("JDBCMINORVERSION", (Object)null);
         this.put("CONVERT", (Object)null);
         this.put("CATALOGSEPARATOR", (Object)null);
         this.put("MAXSTATEMENTS", (Object)null);
         this.put("SCHEMASINPRIVILEGEDEFINITIONS", (Object)null);
         this.put("MAXCHARLITERALLENGTH", (Object)null);
         this.put("MAXINDEXLENGTH", (Object)null);
         this.put("LOWERCASEQUOTEDIDENTIFIERS", false);
         this.put("MAXCATALOGNAMELENGTH", (Object)null);
         this.put("MAXSCHEMANAMELENGTH", (Object)null);
         this.put("USERNAME", (Object)null);
         this.put("SELECTFORUPDATE", (Object)null);
         this.put("INTEGRITYENHANCEMENTFACILITY", (Object)null);
         this.put("RESULTSETHOLDABILITY_2", (Object)null);
         this.put("RESULTSETHOLDABILITY_1", (Object)null);
         this.put("SUBQUERIESINCOMPARISONS", (Object)null);
         this.put("CORESQLGRAMMAR", (Object)null);
         this.put("CORRELATEDSUBQUERIES", (Object)null);
         this.put("NULLSARESORTEDLOW", true);
         this.put("MAXCURSORNAMELENGTH", (Object)null);
         this.put("NONNULLABLECOLUMNS", (Object)null);
         this.put("LOCATORSUPDATECOPY", (Object)null);
         this.put("LOWERCASEIDENTIFIERS", false);
         this.put("MAXTABLESINSELECT", (Object)null);
         this.put("SUBQUERIESINEXISTS", (Object)null);
         this.put("GETGENERATEDKEYS", (Object)null);
         this.put("MINIMUMSQLGRAMMAR", (Object)null);
         this.put("OTHERSDELETESAREVISIBLE_1004", false);
         this.put("SAVEPOINTS", (Object)null);
         this.put("OTHERSDELETESAREVISIBLE_1003", false);
         this.put("URL", (Object)null);
         this.put("GROUPBYBEYONDSELECT", (Object)null);
         this.put("MULTIPLEOPENRESULTS", (Object)null);
         this.put("POSITIONEDUPDATE", (Object)null);
         this.put("SCHEMASINTABLEDEFINITIONS", (Object)null);
         this.put("MAXTABLENAMELENGTH", (Object)null);
         this.put("MULTIPLETRANSACTIONS", (Object)null);
         this.put("POSITIONEDDELETE", (Object)null);
         this.put("BATCHUPDATES", (Object)null);
         this.put("SUBQUERIESININS", (Object)null);
         this.put("SYSTEMFUNCTIONS", "");
         this.put("MAXCONNECTIONS", (Object)null);
         this.put("UPPERCASEQUOTEDIDENTIFIERS", false);
         this.put("NULLSARESORTEDATSTART", false);
         this.put("SCHEMASINDATAMANIPULATION", (Object)null);
         this.put("OWNINSERTSAREVISIBLE_1004", true);
         this.put("OWNINSERTSAREVISIBLE_1005", true);
         this.put("OTHERSUPDATESAREVISIBLE_1004", false);
         this.put("DOESMAXROWSIZEINCLUDEBLOBS", (Object)null);
         this.put("OTHERSUPDATESAREVISIBLE_1003", false);
         this.put("MAXCOLUMNSINGROUPBY", (Object)null);
         this.put("OTHERSUPDATESAREVISIBLE_1005", false);
         this.put("EXTRANAMECHARACTERS", " ");
         this.put("TABLECORRELATIONNAMES", (Object)null);
         this.put("CONNECTION", (Object)null);
         this.put("RESULTSETTYPE_1005", (Object)null);
         this.put("NUMERICFUNCTIONS", "ABS,ACOS,ASIN,ATAN,ATAN2,CEIL,CEILING,CONV,CONVERTBASE,CONVERT_BASE,COS,COT,CRC32,DISTINCT,DISTINCTCOUNT,COUNT_DISTINCT,DEGREES,EXP,FLOOR,FORMAT,LN,LOG,LOG2,LOG10,MEAN,MD5,MEDIAN,MODE,MOD,OCT,PI,POW,RADIANS,RAND,ROUND,SIGN,SIN,SQUARE,SQRT,TAN,TRUNCATE,AVG,COUNT,COUNT_WB,GROUP_CONCAT,MAX,MIN,PERCENTILE,PERCENTILE_CONT,STD,STDDEV,STDDEV_POP,STDDEV_SAMP,STDDEV_SAMPLE,SUM,COALESCE,COMPUTE,USEJOIN,VARIANCE,VAR_POP,VAR_SAMP,VARIANCE_SAMP,VARIANCE_SAMPLE,CORR,COVAR_POP,COVARIANCE,POWER,INCLUDE_GROUPBY,EXCLUDE_GROUPBY,FIXED_GROUPBY,MAP_GROUPBY,IGNORE_FILTERS,DURATION_TO_YEARS,DURATION_TO_MONTHS,DURATION_TO_WEEKS,DURATION_TO_DAYS,DURATION_TO_HOURS,DURATION_TO_MINUTES,DURATION_TO_SECONDS,MAKE_DURATION,ADD_YEARS_TO_DURATION,ADD_MONTHS_TO_DURATION,ADD_WEEKS_TO_DURATION,ADD_DAYS_TO_DURATION,ADD_HOURS_TO_DURATION,ADD_MINUTES_TO_DURATION,ADD_SECONDS_TO_DURATION,SUB_YEARS_FROM_DURATION,SUB_MONTHS_FROM_DURATION,SUB_WEEKS_FROM_DURATION,SUB_DAYS_FROM_DURATION,SUB_HOURS_FROM_DURATION,SUB_MINUTES_FROM_DURATION,SUB_SECONDS_FROM_DURATION,ADD_DURATION,SUB_DURATION,ROUND_DURATION,DATETIME_DIFF_IN_DURATION,TIMESTAMP_DIFF_IN_DURATION");
         this.put("TIMEDATEFUNCTIONS", "ABSQUARTER,ABS_QUARTER,ABSMONTH,ABS_MONTH,ABSWEEK,ABS_WEEK,ADDDATE,ADD_DATE,ADDHOUR,ADD_HOUR,ADDMINUTE,ADD_MINUTE,ADDMONTH,ADD_MONTH,ADDQUARTER,ADD_QUARTER,ADDSECOND,ADD_SECOND,ADDTIME,ADD_TIME,ADDWEEK,ADD_WEEK,ADDYEAR,ADD_YEAR,AGE_MONTHS,AGE_YEARS,BUSINESS_DAYS,BUSINESS_COMPLETION_DAY,BUSINESS_HOURS,BUSINESS_MINUTES,CONVERT_STRING_TO_DATE,CONVERT_TZ,CONVERTTIMEZONE,CREATEDTIME,CREATED_TIME,CURDATE,CURRENT_DATE,CURRENTDATE,CURRENT_TIME,CURTIME,DATE,DATEDIFF,DATEDIFF,DATE_DIFF,DATE_DIFF,DATEANDTIMEDIFF,DATETIME_DIFF,START_DATETIME,DAYNAME,DAY_NAME,DAYOFMONTH,DAY_OF_MONTH,DAYOFQUARTER,DAY_OF_QUARTER,DAYOFWEEK,DAY_OF_WEEK,DAYOFYEAR,DAY_OF_YEAR,DAYS_BETWEEN,EXTRACT,FIRST_DATE_CURRENT_WEEK,START_DATE_CURRENT_WEEK,FROM_DAYS,FROM_UNIXTIME,FROMUNIXTIME,HOUR,INTERVAL,ISEMPTY,IS_EMPTY,ISCURRENTYEAR,IS_CURRENT_YEAR,ISCURRENTQUARTER,IS_CURRENT_QUARTER,ISCURRENTMONTH,IS_CURRENT_MONTH,ISCURRENTWEEK,IS_CURRENT_WEEK,ISLAST_NYEAR,IS_LAST_NYEAR,ISLAST_NQUARTER,IS_LAST_NQUARTER,ISLAST_NMONTH,IS_LAST_NMONTH,ISLAST_NDAY,IS_LAST_NDAY,ISNEXT_NYEAR,IS_NEXT_NYEAR,ISNEXT_NQUARTER,IS_NEXT_NQUARTER,ISNEXT_NMONTH,IS_NEXT_NMONTH,ISNEXTWEEK,IS_NEXT_WEEK,ISNEXT_NDAY,IS_NEXT_NDAY,ISPREVIOUS_NYEAR,IS_PREVIOUS_NYEAR,ISPREVIOUS_NQUARTER,IS_PREVIOUS_NQUARTER,ISPREVIOUS_NMONTH,IS_PREVIOUS_NMONTH,ISPREVIOUSWEEK,IS_PREVIOUS_WEEK,ISPREVIOUS_NDAY,IS_PREVIOUS_NDAY,LAST_DAY,LASTDAY,LAST_NDAY,LAST_NMONTH,LOCALTIME,LOCALTIMESTAMP,MAKEDATE,MAKE_DATE,MAKETIME,MICROSECOND,MICRO_SECOND,MINUTE,MODIFIEDTIME,MODIFIED_TIME,MONTH,MONTHNAME,MONTH_NAME,MONTHNUM,MONTH_NUM,MONTHS_BETWEEN,MONTH_DIFF,NEXT_WEEKDAY,NEXT_WEEK_DAY,NEXT_NDAY,NEXT_NMONTH,NOW,PERIOD_ADD,PERIOD_DIFF,PREVIOUS_NDAY,PREVIOUS_NMONTH,QUARTER,QUARTERNAME,QUARTER_NAME,QUARTERNUM,QUARTER_NUM,SEC_TO_TIME,SECTOTIME,START_DAY,END_DAY,STR_TO_DATE,SUBTIME,SUB_TIME,SYSDATE,TABLEDATAMODIFIEDTIME,TABLE_DATA_MODIFIED_TIME,TIME,TIMEDIFF,TIME_DIFF,TIMESTAMP,TIME_FORMAT,TIME_TO_SEC,TIMETOSEC,TODAY,TOMORROW,CONVERT_TO_DATETIME,TO_DAYS,UNIX_TIMESTAMP,UTC_DATE,UTC_TIMESTAMP,WEEK,WEEKDAY,WEEK_DAY,WEEKOFMONTH,WEEK_OF_MONTH,WEEKOFYEAR,WEEK_OF_YEAR,YEAR,YEARWEEK,YEAR_WEEK,YESTERDAY,ZR_BUSINESS_ENDDAY,ZR_BUSINESS_DAYS,ZR_BUSINESS_HOURS,ZR_BUSINESS_MINUTES,ZR_ISCURRENTWEEK,ZR_ISLASTMONTH,ZR_ISLASTQUARTER,ZR_ISNEXTMONTH,ZR_ISNEXTQUARTER,ZR_ISNEXTWEEK,ZR_ISPREVIOUSMONTH,ZR_ISPREVIOUSQUARTER,ZR_ISPREVIOUSWEEK,ZR_SECTOTIME,ZR_FQUARTERDT,ZR_FQUARTERYEARDT,ZR_FWEEKYEARDT,ZR_FWEEKYEARDTNWKSTRTDAY,ZR_FYEARDT,ZR_WEEKYEARDTNWKSTRTDAY,ZR_FWEEKDT,ZR_FWEEKDTNWKSTRTDAY,ZR_WEEKDTNWKSTRTDAY,GREATEST,ISNULL,IS_NULL,LCASE,LEAST,LOWER,POSITION,SECOND,STDDEV,STRCMP,STRING_COMPARE,STARTDATEOFYEAR,STARTDATEOFQUARTER,STARTDATEOFMONTH,STARTDATEOFWEEK,SUBDATE,SUB_DATE,SUBSTR,TIMESTAMPADD,TIMESTAMP_ADD,TIMESTAMPDIFF,TIMESTAMP_DIFF,UCASE,UTC_TIME,DAY,CURRENT_TIMESTAMP,DATE_FORMAT,DATE_ADD,DATE_SUB,INSERT,INSERT_STRING,OCTET_LENGTH,YTD,QTD,MTD,MAKE_TIME,START_OF_HOUR,END_OF_HOUR,TIME_TO_MIN,IS_CURRENT_HOUR,IS_LAST_NHOUR,IS_PREVIOUS_NHOUR,IS_NEXT_NHOUR,CONVERT_TO_DURATION,ZR_CONV_HMS_TO_DUR,ZR_CONV_DAYS_HMS_TO_DUR,ZR_CONV_FMTD_DAYS_HMS_TO_DUR,TIME_SUM_IN_DURATION,TIME_DIFF_IN_DURATION,ZR_TIME_DIFF_IN_DURATION");
         this.put("ISREADONLY", (Object)null);
         this.put("MAXCOLUMNSINSELECT", (Object)null);
         this.put("INSERTSAREDETECTED_1003", false);
         this.put("INSERTSAREDETECTED_1005", false);
         this.put("RESULTSETTYPE_1003", (Object)null);
         this.put("STOREDPROCEDURES", (Object)null);
         this.put("DATADEFINITIONIGNOREDINTRANSACTIONS", (Object)null);
         this.put("INSERTSAREDETECTED_1004", false);
         this.put("RESULTSETTYPE_1004", (Object)null);
         this.put("DATABASEMAJORVERSION", (Object)null);
         this.put("MAXUSERNAMELENGTH", (Object)null);
         this.put("DRIVERVERSION", (Object)null);
         this.put("MAXCOLUMNNAMELENGTH", (Object)null);
         this.put("MIXEDCASEQUOTEDIDENTIFIERS", true);
         this.put("DIFFERENTTABLECORRELATIONNAMES", (Object)null);
         this.put("USESLOCALFILEPERTABLE", (Object)null);
         this.put("UNION", (Object)null);
         this.put("DEFAULTTRANSACTIONISOLATION", (Object)null);
         this.put("OWNINSERTSAREVISIBLE_1003", true);
         this.put("ALTERTABLEWITHDROPCOLUMN", (Object)null);
         this.put("OPENCURSORSACROSSCOMMIT", (Object)null);
         this.put("UNIONALL", (Object)null);
         this.put("RESULTSETCONCURRENCY_1004_1008", (Object)null);
         this.put("RESULTSETCONCURRENCY_1004_1007", (Object)null);
         this.put("CATALOGSINPROCEDURECALLS", (Object)null);
         this.put("SCHEMASININDEXDEFINITIONS", (Object)null);
         this.put("OUTERJOINS", (Object)null);
         this.put("DRIVERMINORVERSION", (Object)null);
         this.put("RESULTSETCONCURRENCY_1003_1008", (Object)null);
         this.put("EXTENDEDSQLGRAMMAR", (Object)null);
         this.put("RESULTSETCONCURRENCY_1003_1007", (Object)null);
         this.put("USESLOCALFILES", (Object)null);
         this.put("SCHEMASINPROCEDURECALLS", (Object)null);
         this.put("LIKEESCAPECLAUSE", (Object)null);
         this.put("WINDOWFUNCTIONS", "WINDOW_SUM,WINDOW_AVG,WINDOW_COUNT,WINDOW_MIN,WINDOW_MAX,WINDOW_STD,WINDOW_VARIANCE,WINDOW_CORR,WINDOW_COVAR_POP,RUNNING_SUM,RUNNING_AVG,RUNNING_COUNT,RUNNING_MIN,RUNNING_MAX,RANK,DENSE_RANK,PERCENT_RANK,CUME_DIST,ROW_NUMBER,LEAD,LAG,FIRST,FIRST_VALUE,LAST,LAST_VALUE,N_TILE,NTH_VALUE,RANK,DENSE_RANK,PERCENT_RANK,CUME_DIST,ROW_NUMBER,LEAD,LAG,N_TILE,NTILE,NTH_VALUE");
         this.put("SUPPORTSTRANSACTIONISOLATIONLEVEL_4", (Object)null);
         this.put("SEARCHSTRINGESCAPE", (Object)null);
         this.put("SUPPORTSTRANSACTIONISOLATIONLEVEL_0", (Object)null);
         this.put("MAXSTATEMENTLENGTH", (Object)null);
         this.put("ANSI92FULLSQL", (Object)null);
         this.put("PROCEDURETERM", (Object)null);
         this.put("ALTERTABLEWITHADDCOLUMN", (Object)null);
         this.put("CATALOGSINDATAMANIPULATION", (Object)null);
         this.put("SUPPORTSTRANSACTIONISOLATIONLEVEL_2", (Object)null);
         this.put("SUPPORTSTRANSACTIONISOLATIONLEVEL_1", (Object)null);
         this.put("ALLTABLESARESELECTABLE", true);
         this.put("SQLSTATETYPE", (Object)null);
         this.put("MAXCOLUMNSINORDERBY", (Object)null);
         this.put("MAXROWSIZE", (Object)null);
         this.put("SUPPORTSTRANSACTIONISOLATIONLEVEL_8", (Object)null);
         this.put("OPENSTATEMENTSACROSSROLLBACK", (Object)null);
         this.put("DATABASEMINORVERSION", (Object)null);
         this.put("LIMITEDOUTERJOINS", (Object)null);
         this.put("OWNDELETESAREVISIBLE_1004", true);
         this.put("MIXEDCASEIDENTIFIERS", true);
         this.put("OWNDELETESAREVISIBLE_1005", true);
         this.put("NAMEDPARAMETERS", (Object)null);
         this.put("MAXCOLUMNSINTABLE", (Object)null);
         this.put("SCHEMATERM", (Object)null);
         this.put("TYPEINFO", (Object)null);
         this.put("OWNDELETESAREVISIBLE_1003", true);
         this.put("DATABASEPRODUCTNAME", (Object)null);
         this.put("CATALOGTERM", "Reporting Database");
         this.put("OTHERSDELETESAREVISIBLE_1005", false);
         this.put("GROUPBY", (Object)null);
         this.put("DATADEFINITIONANDDATAMANIPULATIONTRANSACTIONS", (Object)null);
         this.put("SCHEMAS", (Object)null);
         this.put("ANSI92INTERMEDIATESQL", (Object)null);
         this.put("DATAMANIPULATIONTRANSACTIONSONLY", (Object)null);
         this.put("STRINGFUNCTIONS", "ASCII,BIN,CONCAT,BIT_LENGTH,CHAR,CHAR_LENGTH,CHARACTER_LENGTH,CONCAT_WS,CONCAT_IGNORE_NULL,ELT,EXTRACT_JSON,FIELD,SUBSTRING_POSITION,FINDMAXVALUE,FIND_MAX_VALUE,FINDMINVALUE,FIND_MIN_VALUE,SUBSTRING_COUNT,FIND_IN_SET,FORMAT,GROUP_FIRST,GROUP_LAST,HEX,INDEXOF,INDEX_OF_STRING,INITCAP,INSTR,ISSTARTSWITH,IS_STARTSWITH,ISENDSWITH,IS_ENDSWITH,ISCONTAINS,IS_CONTAINS,JSON_VALUE,LEFT,LEFT_STRING,LENGTH,LOCATE,LOWERCASE,LOWER_CASE,LPAD,LEFT_PAD,LTRIM,LEFT_TRIM,MAKE_SET,ORD,REPEAT,REPLACE,REVERSE,RIGHT,RIGHT_STRING,RPAD,RIGHT_PAD,RTRIM,RIGHT_TRIM,SOUNDEX,SPACE,SPLIT,SUBSTRING,SUBSTRING_AFTER,SUBSTRING_BEFORE,SUBSTRING_INDEX,SUBSTRING_BETWEEN,TRIM,UNHEX,UPPER,UPPERCASE,UPPER_CASE,ZR_TEXTBETWEEN,MID");
         this.put("ISCATALOGATSTART", (Object)null);
         this.put("MAXBINARYLITERALLENGTH", (Object)null);
         this.put("GROUPBYUNRELATED", (Object)null);
         this.put("TRANSACTIONS", (Object)null);
         this.put("SQLKEYWORDS", (Object)null);
         this.put("NULLSARESORTEDATEND", false);
         this.put("OWNUPDATESAREVISIBLE_1005", true);
         this.put("OWNUPDATESAREVISIBLE_1004", true);
         this.put("DRIVERNAME", (Object)null);
         this.put("EXPRESSIONSINORDERBY", (Object)null);
         this.put("UPPERCASEIDENTIFIERS", false);
         this.put("DRIVERMAJORVERSION", (Object)null);
         this.put("NULLSARESORTEDHIGH", false);
         this.put("CATALOGSININDEXDEFINITIONS", (Object)null);
         this.put("IDENTIFIERQUOTESTRING", "\"");
         this.put("JDBCMAJORVERSION", (Object)null);
         this.put("RESULTSETCONCURRENCY_1005_1008", (Object)null);
         this.put("RESULTSETCONCURRENCY_1005_1007", (Object)null);
         this.put("SUBQUERIESINQUANTIFIEDS", (Object)null);
         this.put("MAXPROCEDURENAMELENGTH", (Object)null);
         this.put("ORDERBYUNRELATED", (Object)null);
         this.put("DATABASEPRODUCTVERSION", (Object)null);
         this.put("UPDATESAREDETECTED_1005", false);
         this.put("UPDATESAREDETECTED_1004", false);
         this.put("OWNUPDATESAREVISIBLE_1003", true);
         this.put("UPDATESAREDETECTED_1003", false);
         this.put("ANSI92ENTRYLEVELSQL", (Object)null);
         this.put("ALLPROCEDURESARECALLABLE", (Object)null);
         this.put("MULTIPLERESULTSETS", (Object)null);
         this.put("DATADEFINITIONCAUSESTRANSACTIONCOMMIT", (Object)null);
         this.put("OTHERSINSERTSAREVISIBLE_1005", false);
         this.put("OTHERSINSERTSAREVISIBLE_1004", false);
         this.put("OTHERSINSERTSAREVISIBLE_1003", false);
         this.put("OPENCURSORSACROSSROLLBACK", (Object)null);
         this.put("FULLOUTERJOINS", (Object)null);
         this.put("STATEMENTPOOLING", (Object)null);
      }
   });
}
