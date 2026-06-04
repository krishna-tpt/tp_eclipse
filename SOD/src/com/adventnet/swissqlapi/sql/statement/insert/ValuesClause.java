package com.adventnet.swissqlapi.sql.statement.insert;

import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ValuesClause {
   private String values = new String();
   private String default_String = new String();
   private ArrayList valuesList = new ArrayList();
   private UserObjectContext context = null;
   private TableColumn tableColumn = new TableColumn();
   private InsertClause insertClause;
   private ArrayList insertValList;
   private CommentClass commentObj;
   private InsertQueryStatement insertQueryStmt;

   public void setInsertQueryStatement(InsertQueryStatement iq) {
      this.insertQueryStmt = iq;
   }

   public void setValues(String s) {
      this.values = s;
   }

   public void setDefault(String s) {
      this.default_String = s;
   }

   public void setValuesList(ArrayList v) {
      this.valuesList = v;
   }

   public void setObjectContext(UserObjectContext obj) {
      this.context = obj;
   }

   public void setInsertValList(ArrayList insertValList) {
      this.insertValList = insertValList;
   }

   public void setCommentClass(CommentClass commentObj) {
      this.commentObj = commentObj;
   }

   public CommentClass getCommentClass() {
      return this.commentObj;
   }

   public String getValues() {
      return this.values;
   }

   public String getDefault() {
      return this.default_String;
   }

   public ArrayList getValuesList() {
      return this.valuesList;
   }

   public ValuesClause toOracle() throws ConvertException {
      ValuesClause dupValuesClause = this.copyObjectValues();
      dupValuesClause.setCommentClass(this.commentObj);
      ArrayList newValuesList = new ArrayList();
      ArrayList getExistingValuesList = dupValuesClause.getValuesList();
      ArrayList insertStmts = new ArrayList();
      boolean bracesOpen = false;
      int exValListSize = getExistingValuesList.size();

      for(int i = 0; i < getExistingValuesList.size(); ++i) {
         if (getExistingValuesList.get(i) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)getExistingValuesList.get(i);
            String column_Name_Var;
            if (sc.getColumnExpression() != null) {
               Vector colExp = sc.getColumnExpression();
               if (colExp.get(0) instanceof TableColumn) {
                  TableColumn table_Column_Val = (TableColumn)colExp.get(0);
                  if (table_Column_Val.getColumnName() != null) {
                     column_Name_Var = table_Column_Val.getColumnName();
                     SelectColumn sc1;
                     Vector columnExpression;
                     int count_Var;
                     TableColumn tc;
                     String column_Name;
                     SelectColumn sc2;
                     Vector columnExpression_Vector;
                     int count_Var_Index;
                     TableColumn tc_Temp;
                     String column_Name_temp;
                     if (column_Name_Var.equalsIgnoreCase("NEXTVAL")) {
                        if (i + 2 < getExistingValuesList.size() && getExistingValuesList.get(i + 1) instanceof SelectColumn) {
                           sc1 = (SelectColumn)getExistingValuesList.get(i + 1);
                           columnExpression = sc1.getColumnExpression();

                           for(count_Var = 0; count_Var < columnExpression.size(); ++count_Var) {
                              if (columnExpression.elementAt(count_Var) instanceof TableColumn) {
                                 tc = (TableColumn)columnExpression.elementAt(count_Var);
                                 if (tc.getColumnName() != null) {
                                    column_Name = tc.getColumnName();
                                    if (column_Name.equalsIgnoreCase("FOR")) {
                                       column_Name = "FOR";
                                    }

                                    if (getExistingValuesList.get(i + 2) instanceof SelectColumn) {
                                       sc2 = (SelectColumn)getExistingValuesList.get(i + 2);
                                       columnExpression_Vector = sc2.getColumnExpression();

                                       for(count_Var_Index = 0; count_Var_Index < columnExpression_Vector.size(); ++count_Var_Index) {
                                          if (columnExpression_Vector.elementAt(count_Var_Index) instanceof TableColumn) {
                                             tc_Temp = (TableColumn)columnExpression_Vector.elementAt(count_Var_Index);
                                             if (tc.getColumnName() != null) {
                                                column_Name_temp = tc_Temp.getColumnName();
                                                table_Column_Val.setTableName(column_Name_temp);
                                                table_Column_Val.setColumnName("NEXTVAL");
                                                ++i;
                                                ++i;
                                             }
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     } else if (column_Name_Var.equalsIgnoreCase("NEXT") && i + 3 < getExistingValuesList.size() && getExistingValuesList.get(i + 1) instanceof SelectColumn) {
                        sc1 = (SelectColumn)getExistingValuesList.get(i + 1);
                        columnExpression = sc1.getColumnExpression();

                        for(count_Var = 0; count_Var < columnExpression.size(); ++count_Var) {
                           if (columnExpression.elementAt(count_Var) instanceof TableColumn) {
                              tc = (TableColumn)columnExpression.elementAt(count_Var);
                              if (tc.getColumnName() != null) {
                                 column_Name = tc.getColumnName();
                                 if (column_Name.equalsIgnoreCase("VALUE") && getExistingValuesList.get(i + 2) instanceof SelectColumn) {
                                    sc2 = (SelectColumn)getExistingValuesList.get(i + 2);
                                    columnExpression_Vector = sc2.getColumnExpression();

                                    for(count_Var_Index = 0; count_Var_Index < columnExpression_Vector.size(); ++count_Var_Index) {
                                       if (columnExpression_Vector.elementAt(count_Var_Index) instanceof TableColumn) {
                                          tc_Temp = (TableColumn)columnExpression_Vector.elementAt(count_Var_Index);
                                          if (tc.getColumnName() != null) {
                                             column_Name_temp = tc_Temp.getColumnName();
                                             if (column_Name_temp.equalsIgnoreCase("FOR") && getExistingValuesList.get(i + 3) instanceof SelectColumn) {
                                                SelectColumn sc3 = (SelectColumn)getExistingValuesList.get(i + 3);
                                                Vector columnExpression_Vector3 = sc3.getColumnExpression();

                                                for(int count_Var_Index3 = 0; count_Var_Index3 < columnExpression_Vector3.size(); ++count_Var_Index3) {
                                                   if (columnExpression_Vector3.elementAt(count_Var_Index3) instanceof TableColumn) {
                                                      TableColumn tc_Temp3 = (TableColumn)columnExpression_Vector3.elementAt(count_Var_Index3);
                                                      if (tc_Temp3.getColumnName() != null) {
                                                         String column_Name_temp3 = tc_Temp3.getColumnName();
                                                         table_Column_Val.setTableName(column_Name_temp3);
                                                         table_Column_Val.setColumnName("NEXTVAL");
                                                         ++i;
                                                         ++i;
                                                         ++i;
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
                        }
                     }
                  }
               }
            }

            SelectColumn tempSelectColumn = sc.toOracleSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
            String tempString = tempSelectColumn.toString();
            if (tempString.indexOf("\"") != -1) {
               tempString = "'" + tempString.substring(1, tempString.length() - 1) + "'";
               newValuesList.add(tempString);
            } else {
               column_Name_Var = tempString.trim().toLowerCase();
               if (!column_Name_Var.equals("default")) {
                  newValuesList.add(tempSelectColumn);
               } else if (InsertClause.isOracleDEFColTruncated) {
                  if (getExistingValuesList.size() != i + 2) {
                     ++i;
                  } else {
                     newValuesList.remove(newValuesList.size() - 1);
                  }
               } else {
                  newValuesList.add(tempSelectColumn);
               }
            }
         } else if (getExistingValuesList.get(i) instanceof SelectQueryStatement) {
            SelectQueryStatement subQueryInsideValues = (SelectQueryStatement)getExistingValuesList.get(i);
            newValuesList.add(subQueryInsideValues.toOracle());
         } else {
            Object obj = getExistingValuesList.get(i);
            if (bracesOpen && obj.toString().equalsIgnoreCase("(")) {
               this.handleMultipleValuesList(insertStmts, getExistingValuesList.subList(i, exValListSize));
               break;
            }

            if (obj.toString().equalsIgnoreCase("(")) {
               bracesOpen = true;
            }

            newValuesList.add(getExistingValuesList.get(i));
         }
      }

      dupValuesClause.setValuesList(newValuesList);
      this.insertQueryStmt.setMultipleValuesInsertStmts(insertStmts);
      InsertClause.isOracleDEFColTruncated = false;
      return dupValuesClause;
   }

   public ValuesClause toMSSQLServer() throws ConvertException {
      ArrayList insertStmts = new ArrayList();
      ValuesClause dupValuesClause = this.copyObjectValues();
      ArrayList newValuesList = new ArrayList();
      ArrayList getExistingValuesList = dupValuesClause.getValuesList();
      boolean bracesOpen = false;
      int exValListSize = getExistingValuesList.size();

      for(int i = 0; i < exValListSize; ++i) {
         if (getExistingValuesList.get(i) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)getExistingValuesList.get(i);
            SelectColumn tempSelectColumn = sc.toMSSQLServerSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
            String tempString = tempSelectColumn.toString();
            if (tempString.indexOf("\"") != -1) {
               tempString = "'" + tempString.substring(1, tempString.length() - 1) + "'";
               newValuesList.add(tempString);
            } else if (tempString.trim().startsWith("@") && SwisSQLOptions.EnableDeltekSpecificConversions) {
               tempString = tempString.replaceFirst("@", ":");
               newValuesList.add(tempString);
            } else {
               newValuesList.add(tempSelectColumn);
            }
         } else if (getExistingValuesList.get(i) instanceof SelectQueryStatement) {
            SelectQueryStatement subQueryInsideValues = (SelectQueryStatement)getExistingValuesList.get(i);
            newValuesList.add(subQueryInsideValues.toMSSQLServer());
         } else {
            Object obj = getExistingValuesList.get(i);
            if (bracesOpen && obj.toString().equalsIgnoreCase("(")) {
               this.handleMultipleValuesList(insertStmts, getExistingValuesList.subList(i, exValListSize));
               break;
            }

            if (obj.toString().equalsIgnoreCase("(")) {
               bracesOpen = true;
            }

            newValuesList.add(getExistingValuesList.get(i));
         }
      }

      dupValuesClause.setValuesList(newValuesList);
      this.insertQueryStmt.setMultipleValuesInsertStmts(insertStmts);
      return dupValuesClause;
   }

   private void handleMultipleValuesList(ArrayList insertStmts, List multiValList) throws ConvertException {
      int multiValListSize = multiValList.size();
      int firstOpenBraceIndex = multiValList.indexOf("(");
      int firstCloseBraceIndex = multiValList.indexOf(")");
      int lastCloseBraceIndex = multiValList.lastIndexOf(")");
      List firstValuesSet = multiValList.subList(firstOpenBraceIndex, firstCloseBraceIndex + 1);
      int firstValuesSetSize = firstValuesSet.size();
      ArrayList newValuesList = new ArrayList();

      for(int i = 0; i < firstValuesSetSize; ++i) {
         if (firstValuesSet.get(i) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)firstValuesSet.get(i);
            SelectColumn tempSelectColumn = sc.toMSSQLServerSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
            String tempString = tempSelectColumn.toString();
            if (tempString.indexOf("\"") != -1) {
               tempString = "'" + tempString.substring(1, tempString.length() - 1) + "'";
               newValuesList.add(tempString);
            } else {
               newValuesList.add(tempSelectColumn);
            }
         } else {
            newValuesList.add(firstValuesSet.get(i));
         }
      }

      InsertQueryStatement iq = new InsertQueryStatement();
      iq.setInsertClause(this.insertQueryStmt.getInsertClause());
      ValuesClause valuesCl = new ValuesClause();
      valuesCl.setValues("VALUES");
      valuesCl.setValuesList(newValuesList);
      iq.setValuesClause(valuesCl);
      insertStmts.add(iq);
      if (firstCloseBraceIndex != lastCloseBraceIndex) {
         this.handleMultipleValuesList(insertStmts, multiValList.subList(firstCloseBraceIndex + 1, multiValListSize));
      }
   }

   public ValuesClause toDB2() throws ConvertException {
      ValuesClause dupValuesClause = this.copyObjectValues();
      ArrayList newValuesList = new ArrayList();
      ArrayList getExistingValuesList = dupValuesClause.getValuesList();
      int count = 0;

      for(int i = 0; i < getExistingValuesList.size(); ++i) {
         if (getExistingValuesList.get(i) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)getExistingValuesList.get(i);
            if (this.insertValList != null && this.insertValList.size() > 0) {
               sc.setCorrespondingTableColumn((TableColumn)this.insertValList.get(count));
               ++count;
            }

            if (sc.getColumnExpression() != null) {
               Vector columnExpVector = sc.getColumnExpression();
               if (columnExpVector.get(0) instanceof TableColumn) {
                  TableColumn tableColumn = (TableColumn)columnExpVector.get(0);
                  if (tableColumn.getColumnName().toUpperCase().equalsIgnoreCase("NEXTVAL")) {
                     String tableName = tableColumn.getTableName();
                     String columnName = tableColumn.getColumnName();
                     if (tableName != null) {
                        tableColumn.setTableName("NEXT VALUE FOR ");
                        tableColumn.setColumnName(tableName);
                     }
                  }
               } else if (columnExpVector.get(0) instanceof String) {
                  String str = (String)columnExpVector.get(0);
                  if (str.trim().startsWith("'") && str.indexOf("\n") != -1) {
                     str = StringFunctions.replaceAll(" ", "\n", str);
                     columnExpVector.setElementAt(str, 0);
                  }

                  if (str.trim().startsWith("'") && str.indexOf("\r") != -1) {
                     str = StringFunctions.replaceAll(" ", "\r", str);
                     columnExpVector.setElementAt(str, 0);
                  }
               }
            }

            if (sc.toString().equalsIgnoreCase("NULL")) {
               newValuesList.add(sc);
            } else {
               newValuesList.add(sc.toDB2Select((SelectQueryStatement)null, (SelectQueryStatement)null));
            }
         } else if (getExistingValuesList.get(i) instanceof SelectQueryStatement) {
            SelectQueryStatement subQueryInsideValues = (SelectQueryStatement)getExistingValuesList.get(i);
            newValuesList.add(subQueryInsideValues.toDB2());
         } else {
            if (getExistingValuesList.get(i) instanceof String) {
               String checkNullCast = (String)getExistingValuesList.get(i);
               if (checkNullCast.equalsIgnoreCase("NULL") || checkNullCast.trim().indexOf("CAST( NULL AS") != -1) {
                  ++count;
               }
            }

            newValuesList.add(getExistingValuesList.get(i));
         }
      }

      dupValuesClause.setValuesList(newValuesList);
      return dupValuesClause;
   }

   public ValuesClause toANSI() throws ConvertException {
      ValuesClause dupValuesClause = this.copyObjectValues();
      ArrayList newValuesList = new ArrayList();
      ArrayList getExistingValuesList = dupValuesClause.getValuesList();

      for(int i = 0; i < getExistingValuesList.size(); ++i) {
         if (getExistingValuesList.get(i) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)getExistingValuesList.get(i);
            SelectColumn tempSelectColumn = sc.toANSISelect((SelectQueryStatement)null, (SelectQueryStatement)null);
            String tempString = tempSelectColumn.toString();
            if (tempString.indexOf("\"") != -1) {
               tempString = "'" + tempString.substring(1, tempString.length() - 1) + "'";
               newValuesList.add(tempString);
            } else {
               newValuesList.add(tempSelectColumn);
            }
         } else if (getExistingValuesList.get(i) instanceof SelectQueryStatement) {
            SelectQueryStatement subQueryInsideValues = (SelectQueryStatement)getExistingValuesList.get(i);
            newValuesList.add(subQueryInsideValues.toANSI());
         } else {
            newValuesList.add(getExistingValuesList.get(i));
         }
      }

      dupValuesClause.setValuesList(newValuesList);
      return dupValuesClause;
   }

   public ValuesClause toTeradata() throws ConvertException {
      ValuesClause dupValuesClause = this.copyObjectValues();
      ArrayList newValuesList = new ArrayList();
      ArrayList getExistingValuesList = dupValuesClause.getValuesList();

      for(int i = 0; i < getExistingValuesList.size(); ++i) {
         if (getExistingValuesList.get(i) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)getExistingValuesList.get(i);
            SelectColumn tempSelectColumn = sc.toTeradataSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
            String tempString = tempSelectColumn.toString();
            newValuesList.add(tempSelectColumn);
         } else if (getExistingValuesList.get(i) instanceof SelectQueryStatement) {
            SelectQueryStatement subQueryInsideValues = (SelectQueryStatement)getExistingValuesList.get(i);
            newValuesList.add(subQueryInsideValues.toTeradata());
         } else {
            newValuesList.add(getExistingValuesList.get(i));
         }
      }

      dupValuesClause.setValuesList(newValuesList);
      return dupValuesClause;
   }

   public ValuesClause toBigQuery() throws ConvertException {
      ValuesClause dupValuesClause = this.copyObjectValues();
      ArrayList newValuesList = new ArrayList();
      ArrayList getExistingValuesList = dupValuesClause.getValuesList();

      for(int i = 0; i < getExistingValuesList.size(); ++i) {
         if (getExistingValuesList.get(i) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)getExistingValuesList.get(i);
            newValuesList.add(sc.toBigQuerySelect((SelectQueryStatement)null, (SelectQueryStatement)null));
         } else if (getExistingValuesList.get(i) instanceof SelectQueryStatement) {
            SelectQueryStatement subQueryInsideValues = (SelectQueryStatement)getExistingValuesList.get(i);
            newValuesList.add(subQueryInsideValues.toBigQuery());
         } else {
            newValuesList.add(getExistingValuesList.get(i));
         }
      }

      dupValuesClause.setValuesList(newValuesList);
      return dupValuesClause;
   }

   public ValuesClause toPostgreSQL() throws ConvertException {
      ValuesClause dupValuesClause = this.copyObjectValues();
      ArrayList newValuesList = new ArrayList();
      ArrayList getExistingValuesList = dupValuesClause.getValuesList();

      for(int i = 0; i < getExistingValuesList.size(); ++i) {
         if (getExistingValuesList.get(i) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)getExistingValuesList.get(i);
            newValuesList.add(sc.toPostgreSQLSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
         } else if (getExistingValuesList.get(i) instanceof SelectQueryStatement) {
            SelectQueryStatement subQueryInsideValues = (SelectQueryStatement)getExistingValuesList.get(i);
            newValuesList.add(subQueryInsideValues.toPostgreSQL());
         } else {
            newValuesList.add(getExistingValuesList.get(i));
         }
      }

      dupValuesClause.setValuesList(newValuesList);
      return dupValuesClause;
   }

   public ValuesClause toInformix() throws ConvertException {
      ValuesClause dupValuesClause = this.copyObjectValues();
      ArrayList newValuesList = new ArrayList();
      ArrayList getExistingValuesList = dupValuesClause.getValuesList();

      for(int i = 0; i < getExistingValuesList.size(); ++i) {
         if (getExistingValuesList.get(i) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)getExistingValuesList.get(i);
            newValuesList.add(sc.toInformixSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
         } else if (getExistingValuesList.get(i) instanceof SelectQueryStatement) {
            SelectQueryStatement subQueryInsideValues = (SelectQueryStatement)getExistingValuesList.get(i);
            newValuesList.add(subQueryInsideValues.toInformix());
         } else {
            newValuesList.add(getExistingValuesList.get(i));
         }
      }

      dupValuesClause.setValuesList(newValuesList);
      return dupValuesClause;
   }

   public ValuesClause toMySQL() throws ConvertException {
      ValuesClause dupValuesClause = this.copyObjectValues();
      ArrayList newValuesList = new ArrayList();
      ArrayList getExistingValuesList = dupValuesClause.getValuesList();

      for(int i = 0; i < getExistingValuesList.size(); ++i) {
         if (getExistingValuesList.get(i) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)getExistingValuesList.get(i);
            newValuesList.add(sc.toMySQLSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
         } else {
            newValuesList.add(getExistingValuesList.get(i));
         }
      }

      dupValuesClause.setValuesList(newValuesList);
      return dupValuesClause;
   }

   public ValuesClause toSnowflake() throws ConvertException {
      ValuesClause dupValuesClause = this.copyObjectValues();
      ArrayList newValuesList = new ArrayList();
      ArrayList getExistingValuesList = dupValuesClause.getValuesList();

      for(int i = 0; i < getExistingValuesList.size(); ++i) {
         if (getExistingValuesList.get(i) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)getExistingValuesList.get(i);
            newValuesList.add(sc.toSnowflakeSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
         } else if (getExistingValuesList.get(i) instanceof SelectQueryStatement) {
            SelectQueryStatement subQueryInsideValues = (SelectQueryStatement)getExistingValuesList.get(i);
            newValuesList.add(subQueryInsideValues.toSnowflake());
         } else {
            newValuesList.add(getExistingValuesList.get(i));
         }
      }

      dupValuesClause.setValuesList(newValuesList);
      return dupValuesClause;
   }

   public ValuesClause toSybase() throws ConvertException {
      ValuesClause dupValuesClause = this.copyObjectValues();
      ArrayList newValuesList = new ArrayList();
      ArrayList getExistingValuesList = dupValuesClause.getValuesList();

      for(int i = 0; i < getExistingValuesList.size(); ++i) {
         if (getExistingValuesList.get(i) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)getExistingValuesList.get(i);
            sc.setObjectContext(this.context);
            SelectColumn tempSelectColumn = sc.toSybaseSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
            String tempString = tempSelectColumn.toString();
            if (tempString.indexOf("\"") != -1) {
               tempString = "'" + tempString.substring(1, tempString.length() - 1) + "'";
               newValuesList.add(tempString);
            } else {
               newValuesList.add(tempSelectColumn);
            }
         } else if (getExistingValuesList.get(i) instanceof SelectQueryStatement) {
            SelectQueryStatement subQueryInsideValues = (SelectQueryStatement)getExistingValuesList.get(i);
            subQueryInsideValues.setObjectContext(this.context);
            newValuesList.add(subQueryInsideValues.toSybase());
         } else {
            newValuesList.add(getExistingValuesList.get(i));
         }
      }

      dupValuesClause.setValuesList(newValuesList);
      return dupValuesClause;
   }

   public ValuesClause toTimesTen() throws ConvertException {
      ValuesClause dupValuesClause = this.copyObjectValues();
      ArrayList newValuesList = new ArrayList();
      ArrayList getExistingValuesList = dupValuesClause.getValuesList();
      boolean isHexaValue = false;

      for(int i = 0; i < getExistingValuesList.size(); ++i) {
         if (getExistingValuesList.get(i) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)getExistingValuesList.get(i);
            sc.setObjectContext(this.context);
            SelectColumn tempSelectColumn = sc.toTimesTenSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
            String tempString = tempSelectColumn.toString();
            if (tempString.startsWith("\"") && tempString.endsWith("\"")) {
               tempString = "'" + tempString.substring(1, tempString.length() - 1) + "'";
               newValuesList.add(tempString);
            } else if (tempString.trim().equals("0")) {
               isHexaValue = true;
               newValuesList.add(tempSelectColumn);
            } else if (tempString.trim().toLowerCase().startsWith("x") && isHexaValue && sc.getColumnExpression().size() == 1) {
               String hexaValue = newValuesList.get(newValuesList.size() - 1).toString().trim();
               newValuesList.set(newValuesList.size() - 1, hexaValue + tempString.trim());
            } else {
               newValuesList.add(tempSelectColumn);
            }
         } else if (getExistingValuesList.get(i) instanceof SelectQueryStatement) {
            SelectQueryStatement subQueryInsideValues = (SelectQueryStatement)getExistingValuesList.get(i);
            subQueryInsideValues.setObjectContext(this.context);
            newValuesList.add(subQueryInsideValues.toTimesTen());
         } else {
            newValuesList.add(getExistingValuesList.get(i));
         }
      }

      dupValuesClause.setValuesList(newValuesList);
      return dupValuesClause;
   }

   public ValuesClause toNetezza() throws ConvertException {
      ValuesClause dupValuesClause = this.copyObjectValues();
      ArrayList newValuesList = new ArrayList();
      ArrayList getExistingValuesList = dupValuesClause.getValuesList();

      for(int i = 0; i < getExistingValuesList.size(); ++i) {
         if (getExistingValuesList.get(i) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)getExistingValuesList.get(i);
            SelectColumn tempSelectColumn = sc.toNetezzaSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
            String tempString = tempSelectColumn.toString();
            if (tempString.indexOf("\"") != -1) {
               tempString = "'" + tempString.substring(1, tempString.length() - 1) + "'";
               newValuesList.add(tempString);
            } else {
               newValuesList.add(tempSelectColumn);
            }
         } else if (getExistingValuesList.get(i) instanceof SelectQueryStatement) {
            SelectQueryStatement subQueryInsideValues = (SelectQueryStatement)getExistingValuesList.get(i);
            newValuesList.add(subQueryInsideValues.toNetezza());
         } else {
            newValuesList.add(getExistingValuesList.get(i));
         }
      }

      dupValuesClause.setValuesList(newValuesList);
      return dupValuesClause;
   }

   public ValuesClause copyObjectValues() {
      ValuesClause valuesClause = new ValuesClause();
      valuesClause.setValuesList(this.getValuesList());
      valuesClause.setDefault(this.default_String);
      valuesClause.setValues(this.values);
      valuesClause.setObjectContext(this.context);
      return valuesClause;
   }

   public String removeIndent(String s_ri) {
      s_ri = s_ri.replace('\n', ' ');
      s_ri = s_ri.replace('\t', ' ');
      return s_ri;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.default_String != null) {
         sb.append(this.default_String.toUpperCase());
         sb.append(" ");
      }

      if (this.commentObj != null) {
         sb.append(this.commentObj.toString().trim() + " ");
      }

      if (this.values != null) {
         sb.append(this.values.toUpperCase());
         sb.append(" ");
      }

      if (this.valuesList != null) {
         int size = this.valuesList.size();
         SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() + 2);

         for(int i = 0; i < size; ++i) {
            String isCommaOrOpenBrace = "";
            if (this.valuesList.get(i) instanceof String) {
               isCommaOrOpenBrace = (String)this.valuesList.get(i);
            }

            int j;
            if (isCommaOrOpenBrace.equals("(")) {
               for(j = 0; j < SelectQueryStatement.getBeautyTabCount(); ++j) {
                  sb.append("\t");
               }
            }

            sb.append(this.valuesList.get(i) + " ");
            if (isCommaOrOpenBrace.equals(",")) {
               sb.append("\n");

               for(j = 0; j < SelectQueryStatement.getBeautyTabCount(); ++j) {
                  sb.append("\t");
               }
            }
         }

         SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() - 2);
      }

      return sb.toString();
   }
}
