package com.adventnet.swissqlapi.util.database;

import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;
import com.adventnet.swissqlapi.sql.statement.delete.DeleteQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.FromClause;
import com.adventnet.swissqlapi.sql.statement.select.FromTable;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.update.TableClause;
import com.adventnet.swissqlapi.sql.statement.update.TableExpression;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import com.adventnet.swissqlapi.sql.statement.update.UpdateQueryStatement;
import com.adventnet.swissqlapi.util.misc.CastingUtil;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class MetadataInfoUtil {
   public static Hashtable tempTableMetadata = new Hashtable();

   public static String getDatatypeName(SwisSQLStatement fromSWS, TableColumn tableColumnObj) {
      Vector oldFromTables = new Vector();
      Vector fromTables = new Vector();
      int index;
      String table;
      if (fromSWS != null) {
         if (fromSWS instanceof SelectQueryStatement) {
            SelectQueryStatement from_sqs = (SelectQueryStatement)fromSWS;
            if (from_sqs.getFromClause() != null) {
               oldFromTables = from_sqs.getFromClause().getFromItemList();
            }
         } else if (fromSWS instanceof UpdateQueryStatement) {
            UpdateQueryStatement fromUQS = (UpdateQueryStatement)fromSWS;
            TableExpression tExpr = fromUQS.getTableExpression();
            if (tExpr != null) {
               ArrayList tabClauseList = tExpr.getTableClauseList();
               if (tabClauseList != null) {
                  for(index = 0; index < tabClauseList.size(); ++index) {
                     Object obj = tabClauseList.get(index);
                     if (obj instanceof TableClause) {
                        TableObject tabObj = ((TableClause)obj).getTableObject();
                        table = tabObj.getTableName();
                        fromTables.add(table);
                     }
                  }
               }
            }
         } else if (tableColumnObj.getTableName() != null) {
            fromTables.add(tableColumnObj.getTableName().toString());
         }
      } else if (tableColumnObj.getTableName() != null) {
         fromTables.add(tableColumnObj.getTableName().toString());
      }

      Enumeration enum1 = null;
      if (oldFromTables != null) {
         enum1 = oldFromTables.elements();
      }

      String tableName;
      if (enum1 != null) {
         while(enum1.hasMoreElements()) {
            Object obj = enum1.nextElement();
            tableName = null;
            if (obj instanceof FromTable && ((FromTable)obj).getTableName() != null) {
               tableName = ((FromTable)obj).getTableName().toString();
            }

            if (tableName != null && tableName.trim().length() > 0) {
               index = tableName.lastIndexOf(".");
               if (index != -1) {
                  tableName = tableName.substring(index + 1, tableName.length());
               }

               fromTables.add(tableName);
            }
         }
      }

      try {
         String columnName = null;
         tableName = tableColumnObj.getColumnName();
         if (tableName.trim().length() > 0) {
            index = tableName.lastIndexOf(".");
            if (index != -1) {
               columnName = tableName.substring(index + 1, tableName.length());
            } else {
               columnName = tableName;
            }

            if ((columnName.startsWith("\"") || columnName.startsWith("[") || columnName.startsWith("`")) && columnName.length() > 2) {
               columnName = columnName.substring(1, columnName.length() - 1);
            }
         }

         String tableName = tableColumnObj.getTableName();
         if (tableName != null) {
            if ((tableName.startsWith("\"") || tableName.startsWith("[") || tableName.startsWith("`")) && tableName.length() > 2) {
               tableName = tableName.substring(1, tableName.length() - 1);
            }

            Hashtable colDatatypeTable = (Hashtable)CastingUtil.getValueIgnoreCase(SwisSQLAPI.getDataTypesFromMetaDataHT(), tableName);
            if (colDatatypeTable != null) {
               String dataType = (String)CastingUtil.getValueIgnoreCase(colDatatypeTable, columnName);
               if (dataType != null) {
                  return dataType;
               }
            }
         }

         Enumeration enum2 = fromTables.elements();
         if (enum2 != null) {
            while(enum2.hasMoreElements()) {
               Object obj = enum2.nextElement();
               table = obj.toString().trim().toUpperCase();
               if ((table.startsWith("\"") || table.startsWith("[") || table.startsWith("`")) && table.length() > 2) {
                  table = table.substring(1, table.length() - 1);
               }

               Hashtable colDatatypeTable = (Hashtable)CastingUtil.getValueIgnoreCase(SwisSQLAPI.getDataTypesFromMetaDataHT(), table);
               if (colDatatypeTable != null) {
                  String dataType = (String)CastingUtil.getValueIgnoreCase(colDatatypeTable, columnName);
                  if (dataType != null) {
                     return dataType;
                  }
               }
            }
         }

         if (fromSWS == null) {
            Enumeration enum3 = SwisSQLAPI.getDataTypesFromMetaDataHT().keys();
            if (enum3 != null) {
               while(enum3.hasMoreElements()) {
                  Hashtable columnDatatypeTable = (Hashtable)CastingUtil.getValueIgnoreCase(SwisSQLAPI.getDataTypesFromMetaDataHT(), enum3.nextElement().toString());
                  if (columnDatatypeTable != null) {
                     String dataType = (String)CastingUtil.getValueIgnoreCase(columnDatatypeTable, columnName);
                     if (dataType != null) {
                        return dataType;
                     }
                  }
               }
            }
         }
      } catch (Exception var13) {
         var13.printStackTrace();
      }

      return null;
   }

   public static FromTable getTableOfColumn(SwisSQLStatement fromSWS, TableColumn tc) {
      if (fromSWS != null) {
         if (fromSWS instanceof SelectQueryStatement) {
            SelectQueryStatement fromSQS = (SelectQueryStatement)fromSWS;
            Vector oldFromTables = new Vector();
            if (fromSQS.getFromClause() != null) {
               oldFromTables = fromSQS.getFromClause().getFromItemList();
            }

            return getFromTable(oldFromTables, tc);
         }

         TableExpression tExpr;
         ArrayList tabClauseList;
         int i;
         Object obj;
         TableObject tabObj;
         FromClause fromClause;
         Vector fromItems;
         if (fromSWS instanceof UpdateQueryStatement) {
            UpdateQueryStatement fromUQS = (UpdateQueryStatement)fromSWS;
            String columnName = tc.getColumnName();
            if ((columnName.startsWith("\"") || columnName.startsWith("[") || columnName.startsWith("`")) && columnName.length() > 2) {
               columnName = columnName.substring(1, columnName.length() - 1);
            }

            tExpr = fromUQS.getTableExpression();
            if (tExpr != null) {
               tabClauseList = tExpr.getTableClauseList();
               if (tabClauseList != null) {
                  for(i = 0; i < tabClauseList.size(); ++i) {
                     obj = tabClauseList.get(i);
                     if (obj instanceof TableClause) {
                        tabObj = ((TableClause)obj).getTableObject();
                        String tableName = tabObj.getTableName();
                        if ((tableName.startsWith("\"") || tableName.startsWith("[") || tableName.startsWith("`")) && tableName.length() > 2) {
                           tableName = tableName.substring(1, tableName.length() - 1);
                        }

                        try {
                           Hashtable colDatatypeTable = (Hashtable)CastingUtil.getValueIgnoreCase(SwisSQLAPI.getDataTypesFromMetaDataHT(), tableName);
                           if (colDatatypeTable != null) {
                              String dataType = (String)CastingUtil.getValueIgnoreCase(colDatatypeTable, columnName);
                              if (dataType != null) {
                                 FromTable ft = new FromTable();
                                 ft.setTableName(tableName);
                                 String aliasName = ((TableClause)obj).getAlias();
                                 if (aliasName != null) {
                                    ft.setAliasName(aliasName);
                                 }

                                 return ft;
                              }
                           }
                        } catch (Exception var15) {
                           var15.printStackTrace();
                        }
                     }
                  }
               }
            }

            fromClause = fromUQS.getFromClause();
            if (fromClause != null) {
               new Vector();
               fromItems = fromClause.getFromItemList();
               return getFromTable(fromItems, tc);
            }
         } else if (fromSWS instanceof DeleteQueryStatement) {
            ArrayList tableNames = new ArrayList();
            DeleteQueryStatement fromDQS = (DeleteQueryStatement)fromSWS;
            tExpr = fromDQS.getTableExpression();
            if (tExpr != null) {
               tabClauseList = tExpr.getTableClauseList();
               if (tabClauseList != null) {
                  for(i = 0; i < tabClauseList.size(); ++i) {
                     obj = tabClauseList.get(i);
                     if (obj instanceof TableClause) {
                        tabObj = ((TableClause)obj).getTableObject();
                        tableNames.add(tabObj.getTableName());
                     }
                  }
               }
            }

            fromClause = fromDQS.getFromClause();
            int i;
            if (fromClause != null) {
               fromItems = fromClause.getFromItemList();
               if (fromItems != null) {
                  for(i = 0; i < fromItems.size(); ++i) {
                     Object obj = fromItems.get(i);
                     if (obj instanceof FromTable) {
                        FromTable ft = (FromTable)obj;
                        Object ftobj = ft.getTableName();
                        if (ftobj instanceof String) {
                           tableNames.add((String)ftobj);
                        }
                     }
                  }
               }
            }

            String columnName = tc.getColumnName();
            if ((columnName.startsWith("\"") || columnName.startsWith("[") || columnName.startsWith("`")) && columnName.length() > 2) {
               columnName = columnName.substring(1, columnName.length() - 1);
            }

            for(i = 0; i < tableNames.size(); ++i) {
               String tableName = (String)tableNames.get(i);
               if ((tableName.startsWith("\"") || tableName.startsWith("[") || tableName.startsWith("`")) && tableName.length() > 2) {
                  tableName = tableName.substring(1, tableName.length() - 1);
               }

               try {
                  Hashtable colDatatypeTable = (Hashtable)CastingUtil.getValueIgnoreCase(SwisSQLAPI.getDataTypesFromMetaDataHT(), tableName);
                  if (colDatatypeTable != null) {
                     String dType = (String)CastingUtil.getValueIgnoreCase(colDatatypeTable, columnName);
                     if (dType != null) {
                        FromTable ft = new FromTable();
                        ft.setTableName(tableName);
                        return ft;
                     }
                  }
               } catch (Exception var14) {
                  var14.printStackTrace();
               }
            }
         }
      }

      return null;
   }

   private static FromTable getFromTable(Vector oldFromTables, TableColumn tc) {
      Enumeration enum1 = null;
      if (oldFromTables != null) {
         enum1 = oldFromTables.elements();
      }

      if (enum1 != null) {
         String columnName = null;
         String tableColumn = tc.getColumnName();
         if (tableColumn.trim().length() > 0) {
            int index = tableColumn.lastIndexOf(".");
            if (index != -1) {
               columnName = tableColumn.substring(index + 1, tableColumn.length());
            } else {
               columnName = tableColumn;
            }
         }

         while(enum1.hasMoreElements()) {
            Object obj = enum1.nextElement();
            String tableName = null;
            if (obj instanceof FromTable && ((FromTable)obj).getTableName() != null) {
               tableName = ((FromTable)obj).getTableName().toString();
            }

            if (tableName != null && tableName.trim().length() > 0) {
               int index = tableName.lastIndexOf(".");
               if (index != -1) {
                  tableName = tableName.substring(index + 1, tableName.length());
               }
            }

            try {
               if (tableName != null) {
                  if ((tableName.startsWith("\"") || tableName.startsWith("[") || tableName.startsWith("`")) && tableName.length() > 2) {
                     tableName = tableName.substring(1, tableName.length() - 1);
                  }

                  if ((columnName.startsWith("\"") || columnName.startsWith("[") || columnName.startsWith("`")) && columnName.length() > 2) {
                     columnName = columnName.substring(1, columnName.length() - 1);
                  }

                  Hashtable colDatatypeTable = (Hashtable)CastingUtil.getValueIgnoreCase(SwisSQLAPI.getDataTypesFromMetaDataHT(), tableName);
                  if (colDatatypeTable != null) {
                     String dataType = (String)CastingUtil.getValueIgnoreCase(colDatatypeTable, columnName);
                     if (dataType != null) {
                        return (FromTable)obj;
                     }
                  }

                  Hashtable tempColDatatypeTable = (Hashtable)CastingUtil.getValueIgnoreCase(tempTableMetadata, tableName);
                  if (tempColDatatypeTable != null) {
                     Object tempObj = CastingUtil.getValueIgnoreCase(tempColDatatypeTable, columnName);
                     if (tempObj != null) {
                        return (FromTable)obj;
                     }
                  }
               }
            } catch (Exception var10) {
               var10.printStackTrace();
            }
         }
      }

      return null;
   }

   public static String getFromTable(ArrayList fromTableList, TableColumn tc) {
      if (fromTableList != null) {
         String columnName = null;
         String tableColumn = tc.getColumnName();
         int i;
         if (tableColumn.trim().length() > 0) {
            i = tableColumn.lastIndexOf(".");
            if (i != -1) {
               columnName = tableColumn.substring(i + 1, tableColumn.length());
            } else {
               columnName = tableColumn;
            }
         }

         for(i = 0; i < fromTableList.size(); ++i) {
            String tableName = (String)fromTableList.get(i);
            int index = tableName.lastIndexOf(".");
            if (index != -1) {
               tableName = tableName.substring(index + 1, tableName.length());
            }

            try {
               if (tableName != null) {
                  if ((tableName.startsWith("\"") || tableName.startsWith("[") || tableName.startsWith("`")) && tableName.length() > 2) {
                     tableName = tableName.substring(1, tableName.length() - 1);
                  }

                  if ((columnName.startsWith("\"") || columnName.startsWith("[") || columnName.startsWith("`")) && columnName.length() > 2) {
                     columnName = columnName.substring(1, columnName.length() - 1);
                  }

                  Hashtable colDatatypeTable = (Hashtable)CastingUtil.getValueIgnoreCase(SwisSQLAPI.getDataTypesFromMetaDataHT(), tableName);
                  if (colDatatypeTable != null) {
                     String dataType = (String)CastingUtil.getValueIgnoreCase(colDatatypeTable, columnName);
                     if (dataType != null) {
                        return tableName;
                     }
                  }
               }
            } catch (Exception var9) {
               var9.printStackTrace();
            }
         }
      }

      return null;
   }

   public static FromTable getTableOfColumn(SwisSQLStatement fromSQS, String columnName) {
      TableColumn newTC = new TableColumn();
      newTC.setColumnName(columnName);
      return getTableOfColumn(fromSQS, newTC);
   }

   public static String getTargetDataTypeForColumn(TableColumn tableColumn) {
      if (SwisSQLAPI.targetDataTypesMetaDataHash == null) {
         return null;
      } else if (tableColumn == null) {
         return null;
      } else if (tableColumn.getTableName() == null) {
         if (tableColumn.getColumnName() != null) {
            return SwisSQLAPI.variableDatatypeMapping != null ? CastingUtil.getDataType((String)((String)SwisSQLAPI.variableDatatypeMapping.get(tableColumn.getColumnName()))) : null;
         } else {
            return null;
         }
      } else {
         String tableName = tableColumn.getTableName().toLowerCase();
         String columnName = tableColumn.getColumnName().toLowerCase();
         if ((tableName.startsWith("\"") || tableName.startsWith("[") || tableName.startsWith("`")) && tableName.length() > 2) {
            tableName = tableName.substring(1, tableName.length() - 1);
         }

         if ((columnName.startsWith("\"") || columnName.startsWith("[") || columnName.startsWith("`")) && columnName.length() > 2) {
            columnName = columnName.substring(1, columnName.length() - 1);
         }

         Hashtable columnHash = (Hashtable)SwisSQLAPI.targetDataTypesMetaDataHash.get(tableName);
         if (columnHash == null) {
            columnHash = (Hashtable)SwisSQLAPI.targetDataTypesMetaDataHash.get(tableName.toUpperCase());
            if (columnHash == null) {
               return null;
            }
         }

         String dataType = (String)columnHash.get(columnName);
         if (dataType == null) {
            dataType = (String)columnHash.get(columnName.toUpperCase());
         }

         return dataType;
      }
   }
}
