package com.adventnet.swissqlapi.sql.statement.create;

import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.ModifiedObjectAttr;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.util.misc.CustomizeUtil;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class PrimaryOrUniqueConstraintClause implements ConstraintType {
   private Vector constraintColumnNames;
   private String constraintName;
   private String clusteredStatus;
   private String with;
   private HashMap diskAttr;
   private String openBrace;
   private String closedBrace;
   private String columnName;
   private String usingIndex;
   private String onString;
   private String onIndexOrIdentifier;
   private UserObjectContext context = null;
   private HashMap constrColSortClause;
   private String sortClause;
   private String tableNameFromCQS;
   private Map columnNameVsSize = new HashMap();

   public void addToColumnNameVsSize(String columnName, String size) {
      if (columnName.startsWith("`")) {
         columnName = columnName.substring(1, columnName.length() - 1);
      }

      this.columnNameVsSize.put(columnName, size);
   }

   public void setObjectContext(UserObjectContext context) {
      this.context = context;
   }

   public void setConstraintColumnNames(Vector constraintColumnNames) {
      this.constraintColumnNames = constraintColumnNames;
   }

   public void setConstraintName(String constraintName) {
      this.constraintName = constraintName;
   }

   public void setClustered(String clusteredStatus) {
      this.clusteredStatus = clusteredStatus;
   }

   public void setWith(String with) {
      this.with = with;
   }

   public void setDiskAttr(HashMap diskAttr) {
      this.diskAttr = diskAttr;
   }

   public void setUsingIndex(String usingIndex) {
      this.usingIndex = usingIndex;
   }

   public void setOpenBrace(String openBrace) {
      this.openBrace = openBrace;
   }

   public void setClosedBrace(String closedBrace) {
      this.closedBrace = closedBrace;
   }

   public void setColumnName(String columnName) {
      this.columnName = columnName;
   }

   public void setOnString(String onString) {
      this.onString = onString;
   }

   public void setOnIndexOrIdentifier(String onIndexOrIdentifier) {
      this.onIndexOrIdentifier = onIndexOrIdentifier;
   }

   public void setConstrColumnSortClauseMap(HashMap constrColSortClause) {
      this.constrColSortClause = constrColSortClause;
   }

   public void setSortClause(String sortClause) {
      this.sortClause = sortClause;
   }

   public String getColumnName() {
      return this.columnName;
   }

   public String getClustered() {
      return this.clusteredStatus;
   }

   public String getWith() {
      return this.with;
   }

   public HashMap getDiskAttr() {
      return this.diskAttr;
   }

   public String getUsingIndex() {
      return this.usingIndex;
   }

   public Vector getConstraintColumnNames() {
      return this.constraintColumnNames;
   }

   public String getConstraintName() {
      return this.constraintName;
   }

   public HashMap getConstrColumnSortClauseMap() {
      return this.constrColSortClause;
   }

   public String getSortClause() {
      return this.sortClause;
   }

   public void setTableNameFromCQS(String tableNameFromCQS) {
      this.tableNameFromCQS = tableNameFromCQS;
   }

   public void toDB2String() throws ConvertException {
      this.setClustered((String)null);
      this.setWith((String)null);
      this.setDiskAttr((HashMap)null);
      this.setUsingIndex((String)null);
      this.setOnString((String)null);
      this.setOnIndexOrIdentifier((String)null);
      this.setConstrColumnSortClauseMap((HashMap)null);
      this.setSortClause((String)null);
      if (this.getColumnName() != null) {
         this.setOpenBrace((String)null);
         this.setConstraintColumnNames((Vector)null);
         this.setClosedBrace((String)null);
      }

      if (this.constraintColumnNames != null) {
         Vector oracleColumnVector = new Vector();

         for(int i = 0; i < this.constraintColumnNames.size(); ++i) {
            if (this.constraintColumnNames.elementAt(i) instanceof String) {
               String constraintColumn = (String)this.constraintColumnNames.get(i);
               String strConst = this.getConstraintName();
               if (strConst.toUpperCase().indexOf("UNIQUE KEY") != -1) {
                  strConst = "UNIQUE ";
                  this.setConstraintName(strConst);
               }

               if (constraintColumn.startsWith("[") && constraintColumn.endsWith("]") || constraintColumn.startsWith("`") && constraintColumn.endsWith("`")) {
                  constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                  if (constraintColumn.indexOf(32) != -1) {
                     constraintColumn = "\"" + constraintColumn + "\"";
                  }

                  oracleColumnVector.add(constraintColumn);
               } else {
                  oracleColumnVector.add(this.constraintColumnNames.get(i));
               }
            } else {
               oracleColumnVector.add(this.constraintColumnNames.get(i));
            }
         }

         this.setConstraintColumnNames(oracleColumnVector);
      }

   }

   public void toMSSQLServerString() throws ConvertException {
      if (this.onString != null) {
         this.setUsingIndex(this.onString + " " + this.onIndexOrIdentifier);
      }

      this.setConstrColumnSortClauseMap((HashMap)null);
      this.setSortClause((String)null);
      if (this.constraintColumnNames != null) {
         String columnsString = "";
         Vector oracleColumnVector = new Vector();

         for(int i = 0; i < this.constraintColumnNames.size(); ++i) {
            String strConst = this.getConstraintName();
            if (strConst.toUpperCase().indexOf("UNIQUE KEY") != -1) {
               strConst = "UNIQUE";
               this.setConstraintName(strConst);
            }

            if (this.constraintColumnNames.elementAt(i) instanceof String) {
               String constraintColumn = (String)this.constraintColumnNames.get(i);
               String[] keywords = null;
               if (SwisSQLUtils.getKeywords(2) != null) {
                  keywords = (String[])SwisSQLUtils.getKeywords(2);
                  if (constraintColumn.trim().length() > 0) {
                     constraintColumn = CustomizeUtil.objectNamesToBracedIdentifier(constraintColumn, keywords, (ModifiedObjectAttr)null);
                  }
               }

               if (constraintColumn.startsWith("`") && constraintColumn.endsWith("`")) {
                  constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                  if (constraintColumn.indexOf(32) != -1) {
                     constraintColumn = "\"" + constraintColumn + "\"";
                  }

                  oracleColumnVector.add(constraintColumn);
               } else if (constraintColumn.trim().startsWith("[") && constraintColumn.trim().endsWith("]")) {
                  constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                  if (constraintColumn.indexOf(32) != -1) {
                     constraintColumn = "\"" + constraintColumn + "\"";
                  }

                  oracleColumnVector.add(constraintColumn);
               } else if (this.usingIndex != null && strConst != null && strConst.toUpperCase().indexOf("UNIQUE") == -1) {
                  if (i == this.constraintColumnNames.size() - 1) {
                     columnsString = columnsString + constraintColumn;
                     this.setUsingIndex((String)null);
                     oracleColumnVector.add("CLUSTERED(" + columnsString + ")");
                  } else {
                     columnsString = columnsString + constraintColumn + ",";
                  }

                  this.setClosedBrace((String)null);
                  this.setOpenBrace((String)null);
               } else {
                  oracleColumnVector.add(constraintColumn);
               }
            } else {
               oracleColumnVector.add(this.constraintColumnNames.get(i));
            }
         }

         if (this.usingIndex != null) {
            this.setUsingIndex((String)null);
         }

         this.setConstraintColumnNames(oracleColumnVector);
      }

   }

   public void toSybaseString() throws ConvertException {
      this.setUsingIndex((String)null);
      if (this.constraintColumnNames != null) {
         Vector oracleColumnVector = new Vector();
         if (this.columnNameVsSize != null && !this.columnNameVsSize.isEmpty()) {
            this.columnNameVsSize = new HashMap();
         }

         for(int i = 0; i < this.constraintColumnNames.size(); ++i) {
            String strConst = this.getConstraintName();
            if (strConst.toUpperCase().indexOf("UNIQUE KEY") != -1) {
               strConst = "UNIQUE";
               this.setConstraintName(strConst);
            }

            if (this.constraintColumnNames.elementAt(i) instanceof String) {
               String constraintColumn = (String)this.constraintColumnNames.get(i);
               if (constraintColumn.startsWith("`") && constraintColumn.endsWith("`")) {
                  String tempValue = (String)this.constrColSortClause.get(constraintColumn);
                  this.constrColSortClause.remove(constraintColumn);
                  constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                  if (constraintColumn.indexOf(32) != -1) {
                     constraintColumn = "\"" + constraintColumn + "\"";
                  }

                  oracleColumnVector.add(constraintColumn);
                  this.constrColSortClause.put(constraintColumn, tempValue);
               } else {
                  oracleColumnVector.add(this.constraintColumnNames.get(i));
               }
            } else {
               oracleColumnVector.add(this.constraintColumnNames.get(i));
            }
         }

         this.setConstraintColumnNames(oracleColumnVector);
      }

   }

   public void toOracleString() throws ConvertException {
      if (this.getColumnName() != null) {
         this.setOpenBrace((String)null);
         this.setConstraintColumnNames((Vector)null);
         this.setClosedBrace((String)null);
      }

      this.setConstrColumnSortClauseMap((HashMap)null);
      this.setSortClause((String)null);
      if (this.constraintColumnNames != null) {
         Vector oracleColumnVector = new Vector();

         for(int i = 0; i < this.constraintColumnNames.size(); ++i) {
            String strConst = this.getConstraintName();
            if (strConst.toUpperCase().indexOf("UNIQUE KEY") != -1) {
               strConst = "UNIQUE";
               this.setConstraintName(strConst);
            }

            if (!(this.constraintColumnNames.elementAt(i) instanceof String)) {
               oracleColumnVector.add(this.constraintColumnNames.get(i));
            } else {
               String constraintColumn = (String)this.constraintColumnNames.get(i);
               constraintColumn = CustomizeUtil.objectNamesToQuotedIdentifier(constraintColumn, SwisSQLUtils.getKeywords(1), (ModifiedObjectAttr)null, 1);
               if ((!constraintColumn.startsWith("[") || !constraintColumn.endsWith("]")) && (!constraintColumn.startsWith("`") || !constraintColumn.endsWith("`"))) {
                  oracleColumnVector.add(constraintColumn);
               } else {
                  constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                  if (SwisSQLOptions.retainQuotedIdentifierForOracle || constraintColumn.indexOf(32) != -1) {
                     constraintColumn = "\"" + constraintColumn + "\"";
                  }

                  oracleColumnVector.add(constraintColumn);
               }

               if (this.tableNameFromCQS == null) {
                  boolean addQuotes = false;
                  if (constraintColumn.startsWith("\"") && constraintColumn.endsWith("\"")) {
                     constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                     addQuotes = true;
                  }

                  if (constraintColumn.length() > 30) {
                     constraintColumn = constraintColumn.substring(0, 30);
                     if (addQuotes) {
                        constraintColumn = "\"" + constraintColumn + "\"";
                     }

                     oracleColumnVector.setElementAt(constraintColumn, oracleColumnVector.size() - 1);
                  }
               }
            }
         }

         this.setConstraintColumnNames(oracleColumnVector);
      }

      this.setUsingIndex(this.usingIndex);
      if (this.onString != null) {
         String indexString = this.onIndexOrIdentifier;
         if (indexString.startsWith("[") && indexString.endsWith("]") || indexString.startsWith("`") && indexString.endsWith("`")) {
            indexString = indexString.substring(1, indexString.length() - 1);
            if (SwisSQLOptions.retainQuotedIdentifierForOracle || indexString.indexOf(32) != -1) {
               indexString = "\"" + indexString + "\"";
            }
         }

         if (!indexString.equalsIgnoreCase("primary")) {
            this.setUsingIndex("USING INDEX TABLESPACE " + indexString);
         }

         this.setOnString((String)null);
         this.setOnIndexOrIdentifier((String)null);
      }

   }

   public void toBigQueryString() throws ConvertException {
      this.setClustered((String)null);
      this.setWith((String)null);
      this.setDiskAttr((HashMap)null);
      this.setUsingIndex((String)null);
      this.setConstrColumnSortClauseMap((HashMap)null);
      this.setSortClause((String)null);
      if (this.getColumnName() != null) {
         this.setOpenBrace((String)null);
         this.setConstraintColumnNames((Vector)null);
         this.setClosedBrace((String)null);
      }

      if (this.constraintColumnNames != null) {
         Vector bigqueryColumnVector = new Vector();

         for(int i = 0; i < this.constraintColumnNames.size(); ++i) {
            if (!(this.constraintColumnNames.elementAt(i) instanceof String)) {
               bigqueryColumnVector.add(this.constraintColumnNames.get(i));
            } else {
               String constraintColumn = (String)this.constraintColumnNames.get(i);
               if ((!constraintColumn.startsWith("[") || !constraintColumn.endsWith("]")) && (!constraintColumn.startsWith("`") || !constraintColumn.endsWith("`"))) {
                  bigqueryColumnVector.add(this.constraintColumnNames.get(i));
               } else {
                  constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                  if (constraintColumn.indexOf(32) != -1) {
                     constraintColumn = "\"" + constraintColumn + "\"";
                  }

                  bigqueryColumnVector.add(constraintColumn);
               }
            }
         }

         this.setConstraintColumnNames(bigqueryColumnVector);
      }

   }

   public void toPostgreSQLString() throws ConvertException {
      this.setClustered((String)null);
      this.setWith((String)null);
      this.setDiskAttr((HashMap)null);
      this.setUsingIndex((String)null);
      this.setConstrColumnSortClauseMap((HashMap)null);
      this.setSortClause((String)null);
      if (this.getColumnName() != null) {
         this.setOpenBrace((String)null);
         this.setConstraintColumnNames((Vector)null);
         this.setClosedBrace((String)null);
      }

      if (this.constraintColumnNames != null) {
         Vector oracleColumnVector = new Vector();

         for(int i = 0; i < this.constraintColumnNames.size(); ++i) {
            if (!(this.constraintColumnNames.elementAt(i) instanceof String)) {
               oracleColumnVector.add(this.constraintColumnNames.get(i));
            } else {
               String constraintColumn = (String)this.constraintColumnNames.get(i);
               if ((!constraintColumn.startsWith("[") || !constraintColumn.endsWith("]")) && (!constraintColumn.startsWith("`") || !constraintColumn.endsWith("`"))) {
                  oracleColumnVector.add(this.constraintColumnNames.get(i));
               } else {
                  constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                  if (constraintColumn.indexOf(32) != -1) {
                     constraintColumn = "\"" + constraintColumn + "\"";
                  }

                  oracleColumnVector.add(constraintColumn);
               }
            }
         }

         this.setConstraintColumnNames(oracleColumnVector);
      }

   }

   public void toANSIString() throws ConvertException {
      this.setClustered((String)null);
      this.setWith((String)null);
      this.setDiskAttr((HashMap)null);
      this.setUsingIndex((String)null);
      this.setOnString((String)null);
      this.setOnIndexOrIdentifier((String)null);
      this.setConstrColumnSortClauseMap((HashMap)null);
      this.setSortClause((String)null);
      if (this.getColumnName() != null) {
         this.setOpenBrace((String)null);
         this.setConstraintColumnNames((Vector)null);
         this.setClosedBrace((String)null);
      }

      if (this.constraintColumnNames != null) {
         Vector oracleColumnVector = new Vector();

         for(int i = 0; i < this.constraintColumnNames.size(); ++i) {
            if (!(this.constraintColumnNames.elementAt(i) instanceof String)) {
               oracleColumnVector.add(this.constraintColumnNames.get(i));
            } else {
               String constraintColumn = (String)this.constraintColumnNames.get(i);
               if ((!constraintColumn.startsWith("[") || !constraintColumn.endsWith("]")) && (!constraintColumn.startsWith("`") || !constraintColumn.endsWith("`"))) {
                  oracleColumnVector.add(this.constraintColumnNames.get(i));
               } else {
                  constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                  if (constraintColumn.indexOf(32) != -1) {
                     constraintColumn = "\"" + constraintColumn + "\"";
                  }

                  oracleColumnVector.add(constraintColumn);
               }
            }
         }

         this.setConstraintColumnNames(oracleColumnVector);
      }

   }

   public void toTeradataString() throws ConvertException {
      this.setClustered((String)null);
      this.setWith((String)null);
      this.setDiskAttr((HashMap)null);
      this.setUsingIndex((String)null);
      this.setOnString((String)null);
      this.setOnIndexOrIdentifier((String)null);
      this.setConstrColumnSortClauseMap((HashMap)null);
      this.setSortClause((String)null);
      if (this.getColumnName() != null) {
         this.setOpenBrace((String)null);
         this.setConstraintColumnNames((Vector)null);
         this.setClosedBrace((String)null);
      }

      if (this.constraintColumnNames != null) {
         Vector oracleColumnVector = new Vector();

         for(int i = 0; i < this.constraintColumnNames.size(); ++i) {
            if (!(this.constraintColumnNames.elementAt(i) instanceof String)) {
               oracleColumnVector.add(this.constraintColumnNames.get(i));
            } else {
               String constraintColumn = (String)this.constraintColumnNames.get(i);
               if ((!constraintColumn.startsWith("[") || !constraintColumn.endsWith("]")) && (!constraintColumn.startsWith("`") || !constraintColumn.endsWith("`"))) {
                  oracleColumnVector.add(this.constraintColumnNames.get(i));
               } else {
                  constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                  if (constraintColumn.indexOf(32) != -1) {
                     constraintColumn = "\"" + constraintColumn + "\"";
                  }

                  oracleColumnVector.add(constraintColumn);
               }
            }
         }

         this.setConstraintColumnNames(oracleColumnVector);
      }

   }

   public void toMySQLString() throws ConvertException {
      this.setClustered((String)null);
      this.setWith((String)null);
      this.setDiskAttr((HashMap)null);
      this.setUsingIndex((String)null);
      this.setOnString((String)null);
      this.setOnIndexOrIdentifier((String)null);
      this.setConstrColumnSortClauseMap((HashMap)null);
      this.setSortClause((String)null);
      if (this.getColumnName() != null && this.constraintColumnNames != null && this.constraintColumnNames.contains(this.getColumnName())) {
         this.setOpenBrace((String)null);
         this.setConstraintColumnNames((Vector)null);
         this.setClosedBrace((String)null);
      }

      if (this.constraintColumnNames != null) {
         Vector oracleColumnVector = new Vector();

         for(int i = 0; i < this.constraintColumnNames.size(); ++i) {
            if (!(this.constraintColumnNames.elementAt(i) instanceof String)) {
               oracleColumnVector.add(this.constraintColumnNames.get(i));
            } else {
               String constraintColumn = (String)this.constraintColumnNames.get(i);
               if (constraintColumn.startsWith("[") && constraintColumn.endsWith("]") || constraintColumn.startsWith("\"") && constraintColumn.endsWith("\"")) {
                  constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                  constraintColumn = "`" + constraintColumn + "`";
                  oracleColumnVector.add(constraintColumn);
               } else if (!constraintColumn.startsWith("`") && !constraintColumn.endsWith("`")) {
                  constraintColumn = "`" + constraintColumn + "`";
                  oracleColumnVector.add(constraintColumn);
               } else {
                  oracleColumnVector.add(constraintColumn);
               }
            }
         }

         this.setConstraintColumnNames(oracleColumnVector);
      }

   }

   public void toSnowflakeString() throws ConvertException {
      this.setClustered((String)null);
      this.setWith((String)null);
      this.setDiskAttr((HashMap)null);
      this.setUsingIndex((String)null);
      this.setConstrColumnSortClauseMap((HashMap)null);
      this.setSortClause((String)null);
      if (this.getColumnName() != null) {
         this.setOpenBrace((String)null);
         this.setConstraintColumnNames((Vector)null);
         this.setClosedBrace((String)null);
      }

      if (this.constraintColumnNames != null) {
         Vector snowflakeColumnVector = new Vector();

         for(int i = 0; i < this.constraintColumnNames.size(); ++i) {
            if (!(this.constraintColumnNames.elementAt(i) instanceof String)) {
               snowflakeColumnVector.add(this.constraintColumnNames.get(i));
            } else {
               String constraintColumn = (String)this.constraintColumnNames.get(i);
               if ((!constraintColumn.startsWith("[") || !constraintColumn.endsWith("]")) && (!constraintColumn.startsWith("`") || !constraintColumn.endsWith("`"))) {
                  snowflakeColumnVector.add(this.constraintColumnNames.get(i));
               } else {
                  constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                  if (constraintColumn.indexOf(32) != -1) {
                     constraintColumn = "\"" + constraintColumn + "\"";
                  }

                  snowflakeColumnVector.add(constraintColumn);
               }
            }
         }

         this.setConstraintColumnNames(snowflakeColumnVector);
      }

   }

   public void toInformixString() throws ConvertException {
      this.setClustered((String)null);
      this.setWith((String)null);
      this.setDiskAttr((HashMap)null);
      this.setUsingIndex((String)null);
      this.setOnString((String)null);
      this.setOnIndexOrIdentifier((String)null);
      this.setConstrColumnSortClauseMap((HashMap)null);
      this.setSortClause((String)null);
      if (this.getColumnName() != null) {
         this.setOpenBrace((String)null);
         this.setConstraintColumnNames((Vector)null);
         this.setClosedBrace((String)null);
      }

      if (this.constraintColumnNames != null) {
         Vector oracleColumnVector = new Vector();

         for(int i = 0; i < this.constraintColumnNames.size(); ++i) {
            if (!(this.constraintColumnNames.elementAt(i) instanceof String)) {
               oracleColumnVector.add(this.constraintColumnNames.get(i));
            } else {
               String constraintColumn = (String)this.constraintColumnNames.get(i);
               if ((!constraintColumn.startsWith("[") || !constraintColumn.endsWith("]")) && (!constraintColumn.startsWith("`") || !constraintColumn.endsWith("`"))) {
                  oracleColumnVector.add(this.constraintColumnNames.get(i));
               } else {
                  constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                  if (constraintColumn.indexOf(32) != -1) {
                     constraintColumn = "\"" + constraintColumn + "\"";
                  }

                  oracleColumnVector.add(constraintColumn);
               }
            }
         }

         this.setConstraintColumnNames(oracleColumnVector);
      }

   }

   public void toTimesTenString() throws ConvertException {
      this.setUsingIndex((String)null);
      this.setConstrColumnSortClauseMap((HashMap)null);
      this.setSortClause((String)null);
      this.setWith((String)null);
      this.setDiskAttr((HashMap)null);
      this.setClustered((String)null);
      if (this.constraintColumnNames != null) {
         Vector columnVector = new Vector();

         for(int i = 0; i < this.constraintColumnNames.size(); ++i) {
            String strConst = this.getConstraintName();
            if (strConst.toUpperCase().indexOf("UNIQUE KEY") != -1) {
               strConst = "UNIQUE";
               this.setConstraintName(strConst);
            }

            if (this.constraintColumnNames.elementAt(i) instanceof String) {
               String constraintColumn = (String)this.constraintColumnNames.get(i);
               if (constraintColumn.startsWith("`") && constraintColumn.endsWith("`")) {
                  constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                  if (constraintColumn.indexOf(32) != -1) {
                     constraintColumn = "\"" + constraintColumn + "\"";
                  }

                  columnVector.add(constraintColumn);
               } else {
                  columnVector.add(this.constraintColumnNames.get(i));
               }
            } else {
               columnVector.add(this.constraintColumnNames.get(i));
            }
         }

         this.setConstraintColumnNames(columnVector);
      }

   }

   public void toNetezzaString() throws ConvertException {
      this.setClustered((String)null);
      this.setWith((String)null);
      this.setDiskAttr((HashMap)null);
      this.setUsingIndex((String)null);
      this.setOnString((String)null);
      this.setOnIndexOrIdentifier((String)null);
      this.setConstrColumnSortClauseMap((HashMap)null);
      this.setSortClause((String)null);
      if (this.getColumnName() != null) {
         this.setOpenBrace((String)null);
         this.setConstraintColumnNames((Vector)null);
         this.setClosedBrace((String)null);
      }

      if (this.constraintColumnNames != null) {
         Vector oracleColumnVector = new Vector();

         for(int i = 0; i < this.constraintColumnNames.size(); ++i) {
            if (!(this.constraintColumnNames.elementAt(i) instanceof String)) {
               oracleColumnVector.add(this.constraintColumnNames.get(i));
            } else {
               String constraintColumn = (String)this.constraintColumnNames.get(i);
               if ((!constraintColumn.startsWith("[") || !constraintColumn.endsWith("]")) && (!constraintColumn.startsWith("`") || !constraintColumn.endsWith("`"))) {
                  oracleColumnVector.add(this.constraintColumnNames.get(i));
               } else {
                  constraintColumn = constraintColumn.substring(1, constraintColumn.length() - 1);
                  if (constraintColumn.indexOf(32) != -1) {
                     constraintColumn = "\"" + constraintColumn + "\"";
                  }

                  oracleColumnVector.add(constraintColumn);
               }
            }
         }

         this.setConstraintColumnNames(oracleColumnVector);
      }

   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.constraintName != null) {
         sb.append(this.constraintName.toUpperCase() + " ");
      }

      if (this.clusteredStatus != null) {
         sb.append(this.clusteredStatus + " ");
      }

      if (this.sortClause != null) {
         sb.append(this.sortClause.toUpperCase() + " ");
      }

      if (this.openBrace != null) {
         sb.append(this.openBrace);
      }

      if (this.constraintColumnNames != null) {
         for(int i = 0; i < this.constraintColumnNames.size(); ++i) {
            String col = this.constraintColumnNames.get(i).toString();
            String sizeStr;
            if (this.context != null) {
               sizeStr = this.context.getEquivalent(col).toString();
               if (!col.equals(sizeStr)) {
                  col = sizeStr;
               }
            }

            String modifiedCol = col;
            if (i == 0) {
               if (this.context != null) {
                  sizeStr = this.context.getEquivalent(col).toString();
                  sb.append(sizeStr);
               } else {
                  sb.append(col);
                  if (col.startsWith("`")) {
                     modifiedCol = col.substring(1, col.length() - 1);
                  }

                  sizeStr = (String)this.columnNameVsSize.get(modifiedCol);
                  if (sizeStr != null) {
                     sb.append("(");
                     sb.append(sizeStr);
                     sb.append(")");
                  }
               }
            } else if (this.context != null) {
               sizeStr = this.context.getEquivalent(col).toString();
               sb.append(", " + sizeStr);
            } else {
               sb.append(", " + col);
               if (col.startsWith("`")) {
                  modifiedCol = col.substring(1, col.length() - 1);
               }

               sizeStr = (String)this.columnNameVsSize.get(modifiedCol);
               if (sizeStr != null) {
                  sb.append("(");
                  sb.append(sizeStr);
                  sb.append(")");
               }
            }

            if (this.constrColSortClause != null && this.constrColSortClause.get(col) != null) {
               sb.append(" " + ((String)this.constrColSortClause.get(col)).toUpperCase());
            }
         }
      }

      if (this.closedBrace != null) {
         sb.append(this.closedBrace + " ");
      }

      if (this.with != null) {
         sb.append(this.with.toUpperCase() + " ");
      }

      if (this.diskAttr != null && this.diskAttr.size() > 0) {
         Set keys = this.diskAttr.keySet();
         Iterator it = keys.iterator();

         for(boolean start = true; it.hasNext(); start = false) {
            if (!start) {
               sb.append(", ");
            }

            Object obj = it.next();
            sb.append(obj.toString().toUpperCase() + " = " + (String)this.diskAttr.get(obj));
         }

         sb.append(" ");
      }

      if (this.usingIndex != null) {
         sb.append(this.usingIndex + " ");
      }

      return sb.toString();
   }

   public ConstraintType copyObjectValues() {
      PrimaryOrUniqueConstraintClause dupPrimaryOrUniqueConstraintClause = new PrimaryOrUniqueConstraintClause();
      dupPrimaryOrUniqueConstraintClause.setClosedBrace(this.closedBrace);
      dupPrimaryOrUniqueConstraintClause.setConstraintColumnNames(this.getConstraintColumnNames());
      dupPrimaryOrUniqueConstraintClause.setConstraintName(this.getConstraintName());
      dupPrimaryOrUniqueConstraintClause.setOpenBrace(this.openBrace);
      dupPrimaryOrUniqueConstraintClause.setConstrColumnSortClauseMap(this.constrColSortClause);
      dupPrimaryOrUniqueConstraintClause.setSortClause(this.sortClause);
      dupPrimaryOrUniqueConstraintClause.setClustered(this.getClustered());
      dupPrimaryOrUniqueConstraintClause.setWith(this.getWith());
      dupPrimaryOrUniqueConstraintClause.setDiskAttr(this.getDiskAttr());
      dupPrimaryOrUniqueConstraintClause.setUsingIndex(this.getUsingIndex());
      dupPrimaryOrUniqueConstraintClause.setOnString(this.onString);
      dupPrimaryOrUniqueConstraintClause.setOnIndexOrIdentifier(this.onIndexOrIdentifier);
      dupPrimaryOrUniqueConstraintClause.setObjectContext(this.context);
      if (this.columnNameVsSize != null) {
         Iterator it = this.columnNameVsSize.keySet().iterator();

         while(it.hasNext()) {
            String colName = it.next().toString();
            String colSize = this.columnNameVsSize.get(colName).toString();
            dupPrimaryOrUniqueConstraintClause.addToColumnNameVsSize(colName, colSize);
         }
      }

      return dupPrimaryOrUniqueConstraintClause;
   }
}
