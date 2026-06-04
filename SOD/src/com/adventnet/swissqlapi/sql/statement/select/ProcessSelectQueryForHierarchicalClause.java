package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

public class ProcessSelectQueryForHierarchicalClause {
   public boolean startWithSubQuery = false;
   public SelectQueryStatement selectQryStment = null;
   public String subQuery = null;
   public String curname = null;
   public String anonFunName = "";
   public String funcName = null;
   public String FuncStr = "";
   public boolean isitRowNum = false;
   public boolean hasSubQuery = false;
   public boolean priorChildcolumnboolean = false;
   Hashtable startWithConnectByHash = new Hashtable();

   public Hashtable getStartWithConnectByHashtable() {
      return this.startWithConnectByHash;
   }

   public void processSelectQueryWhenTreeIsEncountered(SelectQueryStatement tsqlSelectQueryStatement, Hashtable hashTable) {
      String tableName = null;
      String aliasName = null;
      String secondAliasName = null;
      String secondtableName = null;
      FromClause tsqlFromClause = tsqlSelectQueryStatement.getFromClause();
      String fromTableAsQuery = null;
      Vector tableItems = tsqlFromClause.getFromItemList();

      FromTable tsqlFromTable1;
      for(int t = 0; t < tableItems.size(); ++t) {
         if (tableItems.get(t) instanceof FromTable) {
            tsqlFromTable1 = (FromTable)tableItems.get(t);
            Object tN = tsqlFromTable1.getTableName();
            if (tN instanceof SelectQueryStatement) {
               SelectQueryStatement sqs = (SelectQueryStatement)tN;
               FromClause fc = sqs.getFromClause();
               Vector fcList = fc.getFromItemList();
               if (fcList != null && fcList.size() > 0) {
                  FromTable fT = (FromTable)fcList.get(0);
                  if (fT != null) {
                     fromTableAsQuery = fT.getTableName().toString();
                  }
               }
            }
         }
      }

      if (tableItems != null && tableItems.size() > 0) {
         FromTable tsqlFromTable = (FromTable)tableItems.get(0);
         if (fromTableAsQuery != null) {
            tableName = fromTableAsQuery;
         } else {
            tableName = tsqlFromTable.getTableName().toString();
         }

         aliasName = tsqlFromTable.getAliasName();
         if (tableItems.size() > 1) {
            tsqlFromTable1 = (FromTable)tableItems.get(1);
            secondtableName = tsqlFromTable1.getTableName().toString();
            secondAliasName = tsqlFromTable1.getAliasName();
         }
      }

      String name = (String)hashTable.get("$name$");
      int i = 0;
      int sizeOfstartWithLeftExp = 0;
      String fName = "";
      Hashtable tSQLTables = SwisSQLAPI.getDataTypesFromMetaDataHT();
      Hashtable tableColumns = SwisSQLAPI.getTableColumnListMetadata();
      ArrayList orderedColumnsList = (ArrayList)tableColumns.get(tableName.toUpperCase());
      Hashtable columnHashtable = (Hashtable)tSQLTables.get(tableName.toUpperCase());
      if (columnHashtable == null) {
         columnHashtable = (Hashtable)tSQLTables.get(tableName);
      }

      Hashtable secColumnHashtable = null;
      if (tableItems.size() > 1 && secondtableName != null) {
         secColumnHashtable = (Hashtable)tSQLTables.get(secondtableName.toUpperCase());
         if (secColumnHashtable == null) {
            secColumnHashtable = (Hashtable)tSQLTables.get(secondtableName);
         }
      }

      int i = i + 1;
      fName = name + i;
      fName = fName + "ConnectBy";
      int j = 3;
      this.anonFunName = "sp_" + tableName + "_hierarchy";
      int j = j + 1;
      this.anonFunName = "sp_" + tableName + "_hierarchy" + j;
      String selectColumnsString = "";
      Vector selectColumnsVector = null;
      String selectColumnStringDecl = "";
      String whereColumnNames = "";
      String tabColName = "";
      String whereColumnNamesWithTypes = null;
      String whereColumnNamesWithAlias = "";
      SelectStatement ss = tsqlSelectQueryStatement.getSelectStatement();
      Vector ssVec = ss.getSelectItemList();
      Vector selColVec = new Vector();

      Vector weVec;
      int a;
      for(int k = 0; k < ssVec.size(); ++k) {
         if (ssVec.elementAt(k) instanceof SelectColumn) {
            weVec = ((SelectColumn)((SelectColumn)ssVec.elementAt(k))).getColumnExpression();

            for(a = 0; a < weVec.size(); ++a) {
               if (weVec.elementAt(a) instanceof TableColumn) {
                  String hasrownum = ((TableColumn)((TableColumn)weVec.elementAt(a))).getColumnName();
                  selColVec.add(hasrownum);
                  if (hasrownum.equalsIgnoreCase("rownum")) {
                     this.isitRowNum = true;
                  }
               }
            }
         }
      }

      WhereExpression we = tsqlSelectQueryStatement.getWhereExpression();
      String column;
      if (we != null) {
         weVec = we.getWhereItems();
         if (weVec != null) {
            for(a = 0; a < weVec.size(); ++a) {
               whereColumnNamesWithAlias = whereColumnNamesWithAlias + " AND " + weVec.elementAt(a).toString();
            }

            if (whereColumnNamesWithAlias.startsWith(" AND")) {
               whereColumnNamesWithAlias = whereColumnNamesWithAlias.substring(4, whereColumnNamesWithAlias.length());
            }
         }

         for(a = 0; a < weVec.size(); ++a) {
            if (weVec.elementAt(a) instanceof WhereItem) {
               WhereColumn wc = ((WhereItem)weVec.elementAt(a)).getLeftWhereExp();
               Vector wcVec = wc.getColumnExpression();

               for(int b = 0; b < wcVec.size(); ++b) {
                  if (wcVec.elementAt(b) instanceof TableColumn) {
                     TableColumn tc = (TableColumn)wcVec.get(b);
                     tabColName = tc.getColumnName();
                     if (!selColVec.contains(tabColName) && columnHashtable != null) {
                        whereColumnNames = whereColumnNames + "," + tabColName;
                        column = (String)columnHashtable.get(tabColName);
                        if (column != null && column.toLowerCase().trim().startsWith("number")) {
                           column = column.replaceFirst("number", "numeric");
                        } else if (column != null && column.toLowerCase().trim().startsWith("varchar2(")) {
                           column = column.replaceFirst("varchar2", "varchar");
                        }

                        whereColumnNamesWithTypes = whereColumnNamesWithTypes + ", " + tabColName + " " + column;
                     }

                     if (tc.getTableName() != null) {
                        tc.setTableName((String)null);
                     }
                  }
               }
            }
         }
      }

      String join_Var = null;
      if (tableItems.size() > 1) {
         try {
            FromClause fromclass = tsqlSelectQueryStatement.getFromClause();
            Vector fromitemlist = fromclass.getFromItemList();
            join_Var = ((FromTable)fromitemlist.get(1)).toString();
         } catch (Exception var81) {
            System.out.println("Exception thrown while obtaining the JOIN variable if the query has multiple tables");
         }
      }

      whereColumnNames = this.removeCommaFromString(whereColumnNames);
      whereColumnNamesWithTypes = this.removeCommaFromString(whereColumnNamesWithTypes);
      selectColumnsVector = this.getSelectColumnsForStartWithConnectBy(tsqlSelectQueryStatement.getSelectStatement());
      selectColumnsString = this.getSelectColumnsForStartWithConnectByString(selectColumnsVector);
      this.getSelectColumnsDeclaration(tableName, selectColumnsVector);
      HierarchicalQueryClause treeStatement = tsqlSelectQueryStatement.getHierarchicalQueryClause();
      OrderByStatement obstmt = tsqlSelectQueryStatement.getOrderByStatement();
      String orderByColumns = "";
      String Order = "";
      SelectColumn sc;
      TableColumn tc;
      String actualPriorColumn;
      String childColumn;
      if (obstmt != null) {
         childColumn = obstmt.getSiblings();
         if (childColumn != null) {
            Vector vec = obstmt.getOrderItemList();

            for(int u = 0; u < vec.size(); ++u) {
               if (vec.get(u) instanceof OrderItem) {
                  OrderItem oI = (OrderItem)vec.get(u);
                  sc = oI.getOrderSpecifier();
                  Order = oI.getOrder();
                  if (sc != null) {
                     Vector colExp = sc.getColumnExpression();
                     if (colExp != null && !colExp.isEmpty()) {
                        for(int a = 0; a < colExp.size(); ++a) {
                           if (colExp.get(a) instanceof TableColumn) {
                              tc = (TableColumn)colExp.get(a);
                              actualPriorColumn = tc.getColumnName();
                              if (actualPriorColumn != null) {
                                 if (Order != null) {
                                    orderByColumns = orderByColumns + ", " + actualPriorColumn + " " + Order;
                                 } else {
                                    orderByColumns = orderByColumns + ", " + actualPriorColumn;
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }

            tsqlSelectQueryStatement.setOrderByStatement((OrderByStatement)null);
         }
      }

      orderByColumns = this.removeCommaFromString(orderByColumns);
      childColumn = null;
      column = null;
      String priorColumn = null;
      Object currentVariable = null;
      sc = null;
      String dupliPriorLeftColumn = null;
      String dupliPriorRightColumn = null;
      tc = null;
      actualPriorColumn = null;
      String actualColumn = null;
      String startWithColumns = "";
      String leftColumnName = null;
      String rightColumnName = null;
      String additionalColName = null;
      String additionalColNameWithoutType = null;
      String secondTabColName = null;
      String startWithLCol = null;
      String startWithRCol = null;
      boolean isLeftColumn = false;
      String startWithColumnsWithTypes = "";
      String Operator = null;
      String additionalConditions = null;
      String priorLHS = null;
      String priorRHS = null;
      String startwithLHS = null;
      String startwithRHS = null;
      String totalParams;
      Vector startWithRightExp;
      String selectedColumns;
      String selectedColumnType;
      String columnNamesWithAliases;
      int w;
      if (treeStatement.getStartWithCondition() != null && treeStatement.getConnectByCondition() != null) {
         WhereExpression startWithExpression = treeStatement.getStartWithCondition();
         WhereExpression connectByExpression = treeStatement.getConnectByCondition();
         Vector whereItemList1 = startWithExpression.getWhereItem();
         Vector whereItemList2 = connectByExpression.getWhereItem();
         Vector operatorVec = connectByExpression.getOperator();
         if (operatorVec != null && !operatorVec.isEmpty()) {
            Operator = (String)operatorVec.get(0);
         }

         if (whereItemList1 != null && whereItemList1.size() == 1 && whereItemList2 != null && whereItemList2.size() >= 1) {
            Vector startWithLeftExp;
            TableColumn tcLHS;
            if (whereItemList2.size() > 1 && whereItemList2.get(1) instanceof WhereItem) {
               WhereItem wI = (WhereItem)whereItemList2.get(1);
               totalParams = wI.getOperator();
               WhereColumn wLC = wI.getLeftWhereExp();
               WhereColumn wRC = wI.getRightWhereExp();
               startWithLeftExp = wLC.getColumnExpression();
               startWithRightExp = wRC.getColumnExpression();
               if (startWithLeftExp != null && !startWithLeftExp.isEmpty() && startWithLeftExp.get(0) instanceof TableColumn) {
                  tcLHS = (TableColumn)((TableColumn)startWithLeftExp.get(0));
                  additionalConditions = tcLHS.getColumnName();
               }

               if (totalParams != null && additionalConditions != null) {
                  additionalConditions = additionalConditions + " " + totalParams;
               }

               if (startWithRightExp != null && !startWithRightExp.isEmpty()) {
                  if (startWithRightExp.get(0) instanceof TableColumn) {
                     tcLHS = (TableColumn)((TableColumn)startWithRightExp.get(0));
                     additionalConditions = additionalConditions + " " + tcLHS.getColumnName();
                  } else if (startWithRightExp.get(0) instanceof String) {
                     additionalConditions = additionalConditions + " " + (String)startWithRightExp.get(0);
                  }
               }

               if (Operator != null && additionalConditions != null) {
                  additionalConditions = " " + Operator + " " + additionalConditions;
               }
            }

            WhereColumn connectByRightWhereColumn;
            WhereColumn connectByLeftWhereColumn;
            Vector connectByRightExp;
            if (whereItemList2.get(0) instanceof WhereItem) {
               String dupliPriorColumn;
               TableColumn tcLHS;
               if (((WhereItem)whereItemList2.get(0)).getOperator1() == null && ((WhereItem)whereItemList2.get(0)).getOperator3() != null) {
                  connectByRightWhereColumn = ((WhereItem)whereItemList2.get(0)).getLeftWhereExp();
                  connectByLeftWhereColumn = ((WhereItem)whereItemList2.get(0)).getRightWhereExp();
                  if (connectByLeftWhereColumn != null) {
                     connectByRightExp = connectByLeftWhereColumn.getColumnExpression();
                     dupliPriorColumn = ((TableColumn)connectByRightExp.get(0)).toString();
                     tcLHS = (TableColumn)connectByRightExp.get(0);
                     priorRHS = tcLHS.getColumnName();
                     dupliPriorRightColumn = ((TableColumn)connectByRightExp.get(0)).toString();
                  }

                  if (connectByRightWhereColumn != null) {
                     connectByRightExp = connectByRightWhereColumn.getColumnExpression();
                     dupliPriorLeftColumn = ((TableColumn)connectByRightExp.get(0)).toString();
                     tcLHS = (TableColumn)connectByRightExp.get(0);
                     priorLHS = tcLHS.getColumnName();
                  }
               }

               if (((WhereItem)whereItemList2.get(0)).getOperator1() != null && ((WhereItem)whereItemList2.get(0)).getOperator3() == null) {
                  connectByRightWhereColumn = ((WhereItem)whereItemList2.get(0)).getLeftWhereExp();
                  connectByLeftWhereColumn = ((WhereItem)whereItemList2.get(0)).getRightWhereExp();
                  if (connectByRightWhereColumn != null) {
                     connectByRightExp = connectByRightWhereColumn.getColumnExpression();
                     dupliPriorColumn = ((TableColumn)connectByRightExp.get(0)).toString();
                     tcLHS = (TableColumn)connectByRightExp.get(0);
                     priorRHS = tcLHS.getColumnName();
                     dupliPriorLeftColumn = ((TableColumn)connectByRightExp.get(0)).toString();
                     if (connectByRightExp.get(0) instanceof TableColumn && tableItems.size() > 1) {
                        leftColumnName = ((TableColumn)connectByRightExp.get(0)).getColumnName();
                        columnNamesWithAliases = ((TableColumn)connectByRightExp.get(0)).getTableName();
                        if (secondAliasName != null && columnNamesWithAliases != null && columnNamesWithAliases.equals(secondAliasName) && leftColumnName != null) {
                           secondTabColName = leftColumnName;
                           additionalColName = columnNamesWithAliases + "_" + leftColumnName;
                           additionalColNameWithoutType = additionalColName;
                           if (secColumnHashtable != null) {
                              additionalColName = "," + additionalColName + " " + secColumnHashtable.get(leftColumnName);
                           }
                        }
                     }
                  }

                  if (connectByLeftWhereColumn != null) {
                     connectByRightExp = connectByLeftWhereColumn.getColumnExpression();
                     dupliPriorColumn = ((TableColumn)connectByRightExp.get(0)).toString();
                     dupliPriorRightColumn = ((TableColumn)connectByRightExp.get(0)).toString();
                     tcLHS = (TableColumn)connectByRightExp.get(0);
                     priorLHS = tcLHS.getColumnName();
                     if (connectByRightExp.get(0) instanceof TableColumn && tableItems.size() > 1) {
                        rightColumnName = ((TableColumn)connectByRightExp.get(0)).getColumnName();
                        columnNamesWithAliases = ((TableColumn)connectByRightExp.get(0)).getTableName();
                        if (secondAliasName != null && columnNamesWithAliases != null && columnNamesWithAliases.equals(secondAliasName) && rightColumnName != null) {
                           secondTabColName = rightColumnName;
                           additionalColName = columnNamesWithAliases + "_" + rightColumnName;
                           additionalColNameWithoutType = additionalColName;
                           additionalColName = additionalColName + " " + secColumnHashtable.get(rightColumnName);
                        }
                     }
                  }
               }
            }

            if (whereItemList1.get(0) instanceof WhereItem) {
               connectByRightWhereColumn = ((WhereItem)whereItemList1.get(0)).getLeftWhereExp();
               connectByLeftWhereColumn = ((WhereItem)whereItemList1.get(0)).getRightWhereExp();
               new Vector();
               new Vector();
               if (tableItems.size() > 1) {
                  if (connectByRightWhereColumn != null) {
                     connectByRightExp = connectByRightWhereColumn.getColumnExpression();
                     if (connectByRightExp != null && connectByRightExp.size() == 1 && connectByRightExp.get(0) instanceof TableColumn) {
                        startWithLCol = ((TableColumn)((TableColumn)connectByRightExp.get(0))).getColumnName();
                     }
                  }

                  if (connectByLeftWhereColumn != null) {
                     Vector startWithRightVector = connectByLeftWhereColumn.getColumnExpression();
                     if (startWithRightVector != null && startWithRightVector.size() == 1 && startWithRightVector.get(0) instanceof TableColumn) {
                        startWithRCol = ((TableColumn)((TableColumn)startWithRightVector.get(0))).getColumnName();
                     }
                  }
               }

               String dupliChildColumn;
               if (((WhereItem)whereItemList1.get(0)).getRightWhereSubQuery() != null && ((WhereItem)whereItemList1.get(0)).getRightWhereSubQuery() instanceof SelectQueryStatement) {
                  this.startWithSubQuery = true;
                  if (connectByRightWhereColumn != null) {
                     startWithLeftExp = connectByRightWhereColumn.getColumnExpression();
                     if (startWithLeftExp != null && startWithLeftExp.size() == 1 && startWithLeftExp.get(0) instanceof TableColumn) {
                        sizeOfstartWithLeftExp = startWithLeftExp.size();
                        childColumn = ((TableColumn)startWithLeftExp.get(0)).toString();
                        currentVariable = "";
                        this.hasSubQuery = true;
                        this.subQuery = ((WhereItem)whereItemList1.get(0)).getRightWhereSubQuery().toString();
                     }

                     if (startWithLeftExp != null && startWithLeftExp.size() > 1) {
                        sizeOfstartWithLeftExp = startWithLeftExp.size();
                        int z = 0;

                        while(true) {
                           if (z >= startWithLeftExp.size()) {
                              currentVariable = "";
                              this.hasSubQuery = true;
                              this.subQuery = ((WhereItem)whereItemList1.get(0)).getRightWhereSubQuery().toString();
                              break;
                           }

                           if (startWithLeftExp.get(z) instanceof WhereColumn) {
                              Vector wcVec = ((WhereColumn)((WhereColumn)startWithLeftExp.get(z))).getColumnExpression();

                              for(int q = 0; q < wcVec.size(); ++q) {
                                 if (wcVec.get(q) instanceof TableColumn) {
                                    dupliChildColumn = ((TableColumn)wcVec.get(q)).toString();
                                    if (dupliChildColumn.equalsIgnoreCase(dupliPriorLeftColumn)) {
                                       childColumn = dupliChildColumn;
                                       actualPriorColumn = dupliPriorLeftColumn;
                                       actualColumn = dupliPriorRightColumn;
                                       isLeftColumn = true;
                                    } else if (dupliChildColumn.equalsIgnoreCase(dupliPriorRightColumn)) {
                                       childColumn = dupliChildColumn;
                                       actualPriorColumn = dupliPriorRightColumn;
                                       actualColumn = dupliPriorLeftColumn;
                                       isLeftColumn = false;
                                    }

                                    if (!dupliChildColumn.equals(",")) {
                                       startWithColumns = startWithColumns + ", " + dupliChildColumn;
                                       if (!startWithColumns.equals("")) {
                                          startWithColumns = this.removeCommaFromString(startWithColumns);
                                       }

                                       if (columnHashtable != null) {
                                          selectedColumnType = (String)columnHashtable.get(dupliChildColumn.toUpperCase());
                                          if (selectedColumnType != null && selectedColumnType.toLowerCase().trim().startsWith("number")) {
                                             selectedColumnType = selectedColumnType.replaceFirst("number", "numeric");
                                          } else if (selectedColumnType != null && selectedColumnType.toLowerCase().trim().startsWith("varchar2(")) {
                                             selectedColumnType = selectedColumnType.replaceFirst("varchar2", "varchar");
                                          }

                                          startWithColumnsWithTypes = startWithColumnsWithTypes + ", " + dupliChildColumn + "_adv" + " " + selectedColumnType;
                                       }
                                    }
                                 }
                              }
                           }

                           ++z;
                        }
                     }
                  }
               } else if (connectByRightWhereColumn != null) {
                  startWithLeftExp = connectByRightWhereColumn.getColumnExpression();
                  startWithRightExp = new Vector();
                  if (connectByLeftWhereColumn == null) {
                     startWithRightExp.add("null");
                  } else {
                     startWithRightExp = connectByLeftWhereColumn.getColumnExpression();
                  }

                  if (startWithLeftExp != null && startWithRightExp != null && startWithLeftExp.size() == 1 && startWithRightExp.size() == 1) {
                     sizeOfstartWithLeftExp = startWithLeftExp.size();
                     if (startWithLeftExp.get(0) instanceof TableColumn && (startWithRightExp.get(0) instanceof TableColumn || startWithRightExp.get(0) instanceof String)) {
                        childColumn = ((TableColumn)startWithLeftExp.get(0)).toString();
                        tcLHS = (TableColumn)startWithLeftExp.get(0);
                        startwithLHS = tcLHS.getColumnName();
                        if (startWithRightExp.get(0) instanceof TableColumn) {
                           currentVariable = ((TableColumn)startWithRightExp.get(0)).getColumnName();
                           startwithRHS = ((TableColumn)startWithRightExp.get(0)).getColumnName();
                        } else if (startWithRightExp.get(0) instanceof String) {
                           currentVariable = startWithRightExp.get(0);
                        } else if (startWithLeftExp.get(0) instanceof TableColumn) {
                           currentVariable = ((TableColumn)startWithLeftExp.get(0)).getColumnName();
                        } else {
                           currentVariable = (String)startWithLeftExp.get(0);
                        }
                     }
                  }

                  if (startWithLeftExp != null && startWithRightExp != null && startWithLeftExp.size() > 1 && startWithRightExp.size() == 1) {
                     sizeOfstartWithLeftExp = startWithLeftExp.size();

                     for(w = 0; w < startWithLeftExp.size(); ++w) {
                        if (startWithLeftExp.get(w) instanceof TableColumn && (startWithRightExp.get(0) instanceof TableColumn || startWithRightExp.get(0) instanceof String)) {
                           dupliChildColumn = ((TableColumn)startWithLeftExp.get(w)).toString();
                           if (dupliChildColumn.equalsIgnoreCase(dupliPriorLeftColumn) || dupliChildColumn.equalsIgnoreCase(dupliPriorRightColumn)) {
                              childColumn = dupliChildColumn;
                              if (dupliChildColumn.equalsIgnoreCase(dupliPriorLeftColumn)) {
                                 isLeftColumn = true;
                              } else {
                                 isLeftColumn = false;
                              }

                              if (startWithRightExp.get(0) instanceof TableColumn) {
                                 currentVariable = ((TableColumn)startWithRightExp.get(0)).getColumnName();
                              } else if (startWithRightExp.get(0) instanceof String) {
                                 currentVariable = startWithRightExp.get(0).toString();
                              } else if (startWithLeftExp.get(w) instanceof TableColumn) {
                                 currentVariable = ((TableColumn)startWithLeftExp.get(w)).getColumnName();
                              } else {
                                 currentVariable = (String)startWithLeftExp.get(w);
                              }
                           }

                           if (!dupliChildColumn.equals(",")) {
                              startWithColumns = startWithColumns + ", " + dupliChildColumn;
                              if (columnHashtable != null) {
                                 selectedColumns = (String)columnHashtable.get(dupliChildColumn.toUpperCase());
                                 if (selectedColumns != null && selectedColumns.toLowerCase().trim().startsWith("number")) {
                                    selectedColumns = selectedColumns.replaceFirst("number", "numeric");
                                 } else if (selectedColumns != null && selectedColumns.toLowerCase().trim().startsWith("varchar2(")) {
                                    selectedColumns = selectedColumns.replaceFirst("varchar2", "varchar");
                                 }

                                 startWithColumnsWithTypes = startWithColumnsWithTypes + ", " + dupliChildColumn + "_adv" + " " + selectedColumns;
                              }
                           }
                        }
                     }
                  }
               }
            }

            if (whereItemList2.get(0) instanceof WhereItem) {
               if (((WhereItem)whereItemList2.get(0)).getOperator1() == null && ((WhereItem)whereItemList2.get(0)).getOperator3() != null) {
                  connectByRightWhereColumn = ((WhereItem)whereItemList2.get(0)).getLeftWhereExp();
                  connectByLeftWhereColumn = ((WhereItem)whereItemList2.get(0)).getRightWhereExp();
                  if (sizeOfstartWithLeftExp == 1) {
                     if (connectByRightWhereColumn != null) {
                        connectByRightExp = connectByRightWhereColumn.getColumnExpression();
                        column = ((TableColumn)connectByRightExp.get(0)).toString();
                     }

                     if (connectByLeftWhereColumn != null) {
                        connectByRightExp = connectByLeftWhereColumn.getColumnExpression();
                        priorColumn = ((TableColumn)connectByRightExp.get(0)).toString();
                     }
                  } else if (sizeOfstartWithLeftExp > 1) {
                     priorColumn = actualPriorColumn;
                     column = actualColumn;
                  }

                  if (tableName != null && childColumn != null && column != null && priorColumn != null && currentVariable != null) {
                     if (tableItems.size() == 1) {
                        if (sizeOfstartWithLeftExp == 1) {
                           if (priorColumn.equalsIgnoreCase(childColumn)) {
                              this.funcName = "sp_" + tableName + "_hierarchy1";
                           } else {
                              this.funcName = "sp_" + tableName + "_hierarchy2";
                           }
                        } else if (sizeOfstartWithLeftExp > 1) {
                           if (isLeftColumn) {
                              this.funcName = "sp_" + tableName + "_hierarchy1";
                           } else {
                              this.funcName = "sp_" + tableName + "_hierarchy2";
                           }
                        }
                     } else if (tableItems.size() > 1) {
                        this.funcName = "sp_" + tableName + "_hierarchy3";
                     }

                     if (hashTable.containsKey(currentVariable) && ((String)hashTable.get(currentVariable)).equals("@" + currentVariable)) {
                        currentVariable = hashTable.get(currentVariable);
                     }

                     this.getSelectQueryStmtForStartWithConnectBy(currentVariable, this.funcName, tsqlSelectQueryStatement);
                  }
               }

               if (((WhereItem)whereItemList2.get(0)).getOperator1() != null && ((WhereItem)whereItemList2.get(0)).getOperator3() == null) {
                  connectByRightWhereColumn = ((WhereItem)whereItemList2.get(0)).getRightWhereExp();
                  connectByLeftWhereColumn = ((WhereItem)whereItemList2.get(0)).getLeftWhereExp();
                  if (sizeOfstartWithLeftExp == 1) {
                     if (connectByLeftWhereColumn != null) {
                        connectByRightExp = connectByLeftWhereColumn.getColumnExpression();
                        priorColumn = ((TableColumn)connectByRightExp.get(0)).toString();
                     }

                     if (connectByRightWhereColumn != null) {
                        connectByRightExp = connectByRightWhereColumn.getColumnExpression();
                        column = ((TableColumn)connectByRightExp.get(0)).toString();
                     }
                  } else if (sizeOfstartWithLeftExp > 1) {
                     priorColumn = actualPriorColumn;
                     column = actualColumn;
                  }

                  if (tableName != null && childColumn != null && column != null && priorColumn != null && currentVariable != null) {
                     if (sizeOfstartWithLeftExp == 1) {
                        if (tableItems.size() == 1) {
                           if (priorColumn.equalsIgnoreCase(childColumn)) {
                              this.funcName = "sp_" + tableName + "_hierarchy1";
                           } else {
                              this.funcName = "sp_" + tableName + "_hierarchy2";
                           }
                        } else if (tableItems.size() > 1) {
                           this.funcName = "sp_" + tableName + "_hierarchy3";
                        }
                     } else if (sizeOfstartWithLeftExp > 1) {
                        if (isLeftColumn) {
                           this.funcName = "sp_" + tableName + "_hierarchy1";
                        } else {
                           this.funcName = "sp_" + tableName + "_hierarchy2";
                        }
                     }

                     if (hashTable.containsKey(currentVariable) && ((String)hashTable.get(currentVariable)).equals("@" + currentVariable)) {
                        currentVariable = hashTable.get(currentVariable);
                     }

                     this.getSelectQueryStmtForStartWithConnectBy(currentVariable, this.funcName, tsqlSelectQueryStatement);
                     tsqlSelectQueryStatement.setHierarchicalQueryClause((HierarchicalQueryClause)null);
                  }
               }
            }
         }
      }

      String connectByString = null;
      if (sizeOfstartWithLeftExp == 1) {
         if (tableItems.size() == 1) {
            if (priorColumn.equalsIgnoreCase(childColumn)) {
               connectByString = this.StartWithConnectByGenerator(currentVariable, false, tableItems.size(), Operator);
            } else {
               connectByString = this.StartWithConnectByGenerator(currentVariable, true, tableItems.size(), Operator);
            }
         } else if (tableItems.size() > 1) {
            connectByString = this.StartWithConnectByGenerator(currentVariable, true, tableItems.size(), Operator);
         }
      } else if (sizeOfstartWithLeftExp > 1) {
         if (isLeftColumn) {
            connectByString = this.StartWithConnectByGenerator(currentVariable, false, tableItems.size(), Operator);
         } else {
            connectByString = this.StartWithConnectByGenerator(currentVariable, true, tableItems.size(), Operator);
         }
      }

      String columnNames = "";
      String allColumns = "";
      String columnTypes = "";
      String toReturns = "";
      String allParams = "";
      totalParams = "";
      String columnNamesWithAtSymbol = "";
      String totalColumnsWithAtSymbol = "";
      columnNamesWithAliases = "";
      startWithRightExp = new Vector();
      if (columnHashtable != null) {
         for(w = 0; w < orderedColumnsList.size(); ++w) {
            columnNames = (String)orderedColumnsList.get(w);
            startWithRightExp.add(columnNames);
            if (aliasName != null) {
               columnNamesWithAliases = columnNamesWithAliases + ", " + aliasName + "." + columnNames;
            }

            allColumns = allColumns + "," + columnNames;
            columnTypes = (String)columnHashtable.get(columnNames);
            if (columnTypes != null && columnTypes.toLowerCase().trim().startsWith("number")) {
               columnTypes = columnTypes.replaceFirst("number", "numeric");
            } else if (columnTypes != null && columnTypes.toLowerCase().trim().startsWith("varchar2(")) {
               columnTypes = columnTypes.replaceFirst("varchar2", "varchar");
            }

            toReturns = toReturns + ", " + columnNames + " " + columnTypes;
            allParams = "DECLARE @" + columnNames + " " + columnTypes + "\n";
            totalParams = totalParams + " " + allParams;
            columnNamesWithAtSymbol = "@" + columnNames;
            totalColumnsWithAtSymbol = totalColumnsWithAtSymbol + "," + columnNamesWithAtSymbol;
         }
      }

      String funcName1;
      if (priorColumn.indexOf(".") != -1) {
         funcName1 = priorColumn.substring(0, priorColumn.indexOf("."));
         selectedColumns = priorColumn.substring(priorColumn.indexOf(".") + 1, priorColumn.length());
         if (aliasName != null && funcName1.equalsIgnoreCase(aliasName)) {
            if (priorLHS != null && selectedColumns.equalsIgnoreCase(priorLHS)) {
               priorColumn = priorLHS;
            } else if (priorRHS != null && selectedColumns.equalsIgnoreCase(priorRHS)) {
               priorColumn = priorRHS;
            }
         }
      }

      if (childColumn.indexOf(".") != -1) {
         funcName1 = childColumn.substring(0, childColumn.indexOf("."));
         selectedColumns = childColumn.substring(childColumn.indexOf(".") + 1, childColumn.length());
         if (aliasName != null && funcName1.equalsIgnoreCase(aliasName) && startwithLHS != null && selectedColumns.equalsIgnoreCase(startwithLHS)) {
            childColumn = startwithLHS;
         }
      }

      if (secondAliasName != null && secondTabColName != null) {
         columnNamesWithAliases = columnNamesWithAliases + "," + secondAliasName + "." + secondTabColName;
      }

      columnNamesWithAliases = this.removeCommaFromString(columnNamesWithAliases);
      totalColumnsWithAtSymbol = this.removeCommaFromString(totalColumnsWithAtSymbol);
      allColumns = this.removeCommaFromString(allColumns);
      toReturns = this.removeCommaFromString(toReturns);
      if (aliasName != null && tableItems.size() > 1) {
         columnNamesWithAliases = "INSERT INTO @CTEST1 select NULL,NULL," + columnNamesWithAliases + " FROM " + tableName + " " + aliasName + " ";
      }

      if (join_Var != null && tableItems.size() > 1) {
         columnNamesWithAliases = columnNamesWithAliases + join_Var;
      }

      if (aliasName != null) {
         connectByString = connectByString.replaceAll("TableName_SUBSTITUTE_WITH_ACTUAL_TABLENAME", tableName + " " + aliasName);
      } else {
         connectByString = connectByString.replaceAll("TableName_SUBSTITUTE_WITH_ACTUAL_TABLENAME", tableName);
      }

      if (orderByColumns != null && !orderByColumns.equals("")) {
         connectByString = connectByString.replaceAll("ORDER_SIBLINGS_BY_COLUMNS", "ORDER BY " + orderByColumns);
      } else {
         connectByString = connectByString.replaceAll("ORDER_SIBLINGS_BY_COLUMNS", "");
      }

      connectByString = connectByString.replaceAll("ChildName_SUBSTITUTE_WITH_ACTUAL_COLUMNNAME", childColumn);
      connectByString = connectByString.replaceAll("Column_SUBSTITUTE_WITH_ACTUAL_COLUMNNAME", column);
      connectByString = connectByString.replaceAll("PriorC_SUBSTITUTE_WITH_ACTUAL_COLUMNNAME", priorColumn);
      connectByString = connectByString.replaceAll("SUBSTITUTE_SELECTCOLUMNS", selectColumnsString);
      if (tableItems.size() == 1) {
         connectByString = connectByString.replaceAll("ADV_SUBSTITUTE_COLUMNSDECL", toReturns);
         connectByString = connectByString.replaceAll("ADV_SUBSTITUTE_ALLCOLUMNS", allColumns);
      } else if (tableItems.size() > 1) {
         if (additionalColName != null) {
            connectByString = connectByString.replaceAll("ADV_SUBSTITUTE_COLUMNSDECL", toReturns + additionalColName);
         }

         if (additionalColNameWithoutType != null) {
            connectByString = connectByString.replaceAll("ADV_SUBSTITUTE_ALLCOLUMNS", allColumns + "," + additionalColNameWithoutType);
            connectByString = connectByString.replaceAll("ADV_SUBSTITUTE_ADDITIONAL_COLUMNNAME", additionalColNameWithoutType);
         }

         if (startWithLCol != null) {
            connectByString = connectByString.replaceAll("ADV_SUBSTITUTE_SW_L_COLUMNNAME", startWithLCol);
         }

         if (rightColumnName != null) {
            connectByString = connectByString.replaceAll("ADV_SUBSTITUTE_CB_R_COLUMNNAME", rightColumnName);
         }

         if (columnNamesWithAliases != null) {
            connectByString = connectByString.replaceAll("INSERTING_INTO_CTEST1_WITH_JOINS", columnNamesWithAliases);
         }
      }

      if (Operator != null && additionalConditions != null) {
         connectByString = connectByString.replaceAll("ADDITIONAL_CONDITIONS", additionalConditions);
      }

      connectByString = connectByString.replaceAll("ADV_SUBSTITUTE_TABLENAME", tableName);
      connectByString = connectByString.replaceAll("ADV_SUBSTITUTE_STARTWITH_COLUMNNAME", childColumn);
      connectByString = connectByString.replaceAll("ADV_SUBSTITUTE_PRIOR_COLUMNNAME", priorColumn);
      connectByString = connectByString.replaceAll("ADV_SUBSTITUTE_VARIABLE_DECL", totalParams);
      connectByString = connectByString.replaceAll("ADV_SUBSTITUTE_ALLVARIABLES", totalColumnsWithAtSymbol);
      connectByString = connectByString.replaceAll("ADV_SUBSTITUTE_PRIOR1_COLUMNNAME", column);
      funcName1 = "sp_" + tableName;
      if (sizeOfstartWithLeftExp == 1) {
         if (tableItems.size() == 1) {
            if (priorColumn.equalsIgnoreCase(childColumn)) {
               connectByString = connectByString.replaceAll("ADV_SUBSTITUTE_PROCEDURE_NAME", funcName1 + "_hierarchy1");
            } else {
               connectByString = connectByString.replaceAll("ADV_SUBSTITUTE_PROCEDURE_NAME", funcName1 + "_hierarchy2");
            }
         } else if (tableItems.size() > 1) {
            connectByString = connectByString.replaceAll("ADV_SUBSTITUTE_PROCEDURE_NAME", funcName1 + "_hierarchy3");
         }
      } else if (sizeOfstartWithLeftExp > 1) {
         if (isLeftColumn) {
            connectByString = connectByString.replaceAll("ADV_SUBSTITUTE_PROCEDURE_NAME", funcName1 + "_hierarchy1");
         } else {
            connectByString = connectByString.replaceAll("ADV_SUBSTITUTE_PROCEDURE_NAME", funcName1 + "_hierarchy2");
         }
      }

      selectedColumns = "";
      selectedColumnType = "";
      String selectedColumnsWithTypes = "";
      String selectedColumnsNumbersRemoved = "";
      if (!selectColumnsVector.contains("*")) {
         if (selectColumnsString.indexOf(",") == -1 && columnHashtable != null) {
            selectedColumnType = (String)columnHashtable.get(selectColumnsString.toUpperCase().trim());
            if (selectedColumnType != null && selectedColumnType.toLowerCase().trim().startsWith("number")) {
               selectedColumnType = selectedColumnType.replaceFirst("number", "numeric");
            } else if (selectedColumnType != null && selectedColumnType.toLowerCase().trim().startsWith("varchar2(")) {
               selectedColumnType = selectedColumnType.replaceFirst("varchar2", "varchar");
            }

            if (!selectColumnsString.trim().startsWith("'")) {
               selectedColumnsWithTypes = selectColumnsString + " " + selectedColumnType;
               selectedColumnsNumbersRemoved = selectedColumnsNumbersRemoved + ", " + selectColumnsString;
            }
         } else {
            StringTokenizer stToken = new StringTokenizer(selectColumnsString, ",");
            if (columnHashtable != null) {
               while(stToken.hasMoreTokens()) {
                  selectedColumns = stToken.nextToken();

                  try {
                     int var79 = Integer.parseInt(selectedColumns.trim());
                  } catch (Exception var82) {
                     selectedColumnType = (String)columnHashtable.get(selectedColumns.toUpperCase().trim());
                     if (selectedColumnType != null && selectedColumnType.toLowerCase().trim().startsWith("number")) {
                        selectedColumnType = selectedColumnType.replaceFirst("number", "numeric");
                     } else if (selectedColumnType != null && selectedColumnType.toLowerCase().trim().startsWith("varchar2(")) {
                        selectedColumnType = selectedColumnType.replaceFirst("varchar2", "varchar");
                     }

                     if ((selectedColumns.equalsIgnoreCase("level") || selectedColumns.toUpperCase().startsWith("level ") || selectedColumns.toLowerCase().startsWith(" level ")) && selectedColumnType == null) {
                        selectedColumns = "level";
                        selectedColumnType = "int";
                     }

                     if (!selectedColumns.equalsIgnoreCase(" null") && !selectedColumns.trim().startsWith("'")) {
                        selectedColumnsWithTypes = selectedColumnsWithTypes + ", " + selectedColumns + " " + selectedColumnType;
                        selectedColumnsNumbersRemoved = selectedColumnsNumbersRemoved + ", " + selectedColumns;
                     }
                  }
               }
            }
         }
      } else if (selectColumnsVector.contains("*")) {
         if (columnHashtable != null) {
            Enumeration enum1 = columnHashtable.keys();
            String nextEle = "";

            while(true) {
               if (!enum1.hasMoreElements()) {
                  if (!selectedColumns.equals("")) {
                     selectedColumns = selectedColumns.substring(1);
                  }

                  if (!selectedColumnsWithTypes.equals("*")) {
                     selectedColumnsWithTypes = selectedColumnsWithTypes.substring(1);
                  }
                  break;
               }

               nextEle = (String)enum1.nextElement();
               selectedColumns = selectedColumns + ", " + nextEle;
               selectedColumnType = ((String)((String)columnHashtable.get(nextEle))).toUpperCase().trim();
               if (selectedColumnType != null && selectedColumnType.toLowerCase().trim().startsWith("number")) {
                  selectedColumnType = selectedColumnType.replaceFirst("number", "numeric");
               } else if (selectedColumnType != null && selectedColumnType.toLowerCase().trim().startsWith("varchar2(")) {
                  selectedColumnType = selectedColumnType.replaceFirst("varchar2", "varchar");
               }

               if (!nextEle.trim().startsWith("'")) {
                  selectedColumnsWithTypes = selectedColumnsWithTypes + ", " + nextEle + " " + selectedColumnType;
               }
            }
         }

         selectedColumnsNumbersRemoved = selectedColumnsNumbersRemoved + ", " + selectedColumns;
      }

      selectedColumnsWithTypes = this.removeCommaFromString(selectedColumnsWithTypes);
      startWithColumnsWithTypes = this.removeCommaFromString(startWithColumnsWithTypes);
      selectedColumnsNumbersRemoved = this.removeCommaFromString(selectedColumnsNumbersRemoved);
      if ((name == null || name.equals("Anon")) && this.hasSubQuery) {
         this.FuncStr = this.FuncStr + " CREATE FUNCTION " + this.anonFunName + "() \n";
         if (whereColumnNamesWithTypes != null) {
            this.FuncStr = this.FuncStr + "returns @result TABLE(" + selectedColumnsWithTypes + ", " + whereColumnNamesWithTypes + ")" + " \n" + "AS \n";
         } else {
            this.FuncStr = this.FuncStr + "returns @result TABLE(" + selectedColumnsWithTypes + ")" + " \n" + "AS \n";
         }

         if (sizeOfstartWithLeftExp > 1) {
            this.FuncStr = this.FuncStr + "BEGIN \n" + "DECLARE  @adv_test TABLE(" + startWithColumnsWithTypes + ") \n";
         } else {
            this.FuncStr = this.FuncStr + "BEGIN \n" + "DECLARE @adv_test TABLE(column1 varchar(255)) \n";
         }

         this.FuncStr = this.FuncStr + "INSERT INTO @adv_test " + this.subQuery + " \n" + "DECLARE @adv_storevar varchar(255) \n";
         if (whereColumnNamesWithTypes != null) {
            this.FuncStr = this.FuncStr + "DECLARE  @adv_test1 TABLE(" + selectedColumnsWithTypes + ", " + whereColumnNamesWithTypes + ") \n";
         } else {
            this.FuncStr = this.FuncStr + "DECLARE  @adv_test1 TABLE(" + selectedColumnsWithTypes + ") \n";
         }

         if (sizeOfstartWithLeftExp > 1) {
            this.FuncStr = this.FuncStr + "DECLARE adv_cur CURSOR FOR select distinct(" + childColumn + "_adv) " + "from @adv_test \n";
         } else {
            this.FuncStr = this.FuncStr + "DECLARE adv_cur CURSOR FOR select distinct(column1) from @adv_test \n";
         }

         this.FuncStr = this.FuncStr + " OPEN adv_cur \n" + "FETCH  NEXT FROM adv_cur INTO  @adv_storevar \n" + "WHILE ((@@FETCH_STATUS = 0) ) \n" + "BEGIN \n" + "INSERT INTO @adv_test1 SELECT " + selectedColumnsNumbersRemoved + whereColumnNames + " FROM DBO." + this.funcName + "(@adv_storevar) \n" + "FETCH  NEXT FROM adv_cur INTO  @adv_storevar \n" + "END \n" + "CLOSE adv_cur \n" + "deallocate adv_cur \n" + "INSERT into @result(" + selectedColumnsNumbersRemoved + ") select " + selectedColumnsNumbersRemoved + " from @adv_test1 \n" + "RETURN \n" + "END \n" + "GO \n";
         this.startWithConnectByHash.put(this.anonFunName, this.FuncStr);
      }

      this.startWithConnectByHash.put(this.funcName, connectByString);
   }

   public String removeCommaFromString(String str) {
      if (str != null) {
         if (str.startsWith(",")) {
            str = str.substring(1, str.length());
         }

         if (str.endsWith(",")) {
            str = str.substring(0, str.length() - 1);
         }
      }

      return str;
   }

   public String getTableNameFromSQSTMT(SelectQueryStatement sqstmt) {
      String nameOfTable = null;
      FromClause fromclass = sqstmt.getFromClause();
      Vector fromitemlist = fromclass.getFromItemList();
      FromTable fromtable = (FromTable)fromitemlist.get(0);
      Object ob = fromtable.getTableName();
      nameOfTable = ob.toString();
      return nameOfTable;
   }

   public boolean isTableNameSQS(SelectQueryStatement sqstmt) {
      FromClause fromclass = sqstmt.getFromClause();
      Vector fromitemlist = fromclass.getFromItemList();
      FromTable fromtable = (FromTable)fromitemlist.get(0);
      Object ob = fromtable.getTableName();
      return ob instanceof SelectQueryStatement;
   }

   public SelectQueryStatement getSelectQueryStmtForStartWithConnectBy(Object currentVariable, String functionName, SelectQueryStatement tsqlSelectQueryStatement) {
      SelectStatement selectStatement = new SelectStatement();
      selectStatement.setSelectClause("SELECT");
      Vector newItemList = new Vector();
      functionName = this.funcName;
      if (this.hasSubQuery) {
         functionName = this.anonFunName;
      }

      int fromIndex = false;
      int selectIndex = false;
      new Vector();
      String queryColumnsString = "";
      String tName = null;
      Hashtable tSQLTables = SwisSQLAPI.getDataTypesFromMetaDataHT();
      tName = this.getTableNameFromSQSTMT(tsqlSelectQueryStatement);
      FromTable distinctStr;
      if (this.isTableNameSQS(tsqlSelectQueryStatement)) {
         FromClause fromclass = tsqlSelectQueryStatement.getFromClause();
         Vector fromitemlist = fromclass.getFromItemList();
         distinctStr = (FromTable)fromitemlist.get(0);
         Object ob = distinctStr.getTableName();
         if (ob instanceof SelectQueryStatement) {
            SelectQueryStatement tableAsSQS = (SelectQueryStatement)ob;
            WhereExpression we = tableAsSQS.getWhereExpression();
            if (we != null) {
               WhereExpression mainWhereExp = tsqlSelectQueryStatement.getWhereExpression();
               if (mainWhereExp != null) {
                  mainWhereExp.addOperator("AND");
                  mainWhereExp.addWhereExpression(we);
               } else {
                  tsqlSelectQueryStatement.setWhereExpression(we);
               }
            }
         }
      }

      Hashtable columnHashtable = (Hashtable)tSQLTables.get(tName.toUpperCase());
      if (columnHashtable == null) {
         columnHashtable = (Hashtable)tSQLTables.get(tName);
      }

      Vector queryColumnsVector = tsqlSelectQueryStatement.getSelectStatement().getSelectItemList();
      if (queryColumnsVector != null && !queryColumnsVector.isEmpty()) {
         for(int g = 0; g < queryColumnsVector.size(); ++g) {
            if (queryColumnsVector.get(g) instanceof SelectColumn) {
               Vector whereexp = ((SelectColumn)queryColumnsVector.get(g)).getColumnExpression();
               if (whereexp != null) {
                  for(int h = 0; h < whereexp.size(); ++h) {
                     if (whereexp.get(h) instanceof TableColumn) {
                        TableColumn tc = (TableColumn)whereexp.get(h);
                        if (tc.getTableName() != null) {
                           tc.setTableName((String)null);
                        }
                     }
                  }
               }
            }
         }
      }

      SelectStatement selstmt = tsqlSelectQueryStatement.getSelectStatement();
      distinctStr = null;
      if (selstmt.getSelectQualifier() != null) {
         String distinctStr = selstmt.getSelectQualifier();
         if (distinctStr.equalsIgnoreCase("DISTINCT")) {
            selectStatement.setSelectQualifier("DISTINCT");
         }
      }

      Vector ColEx;
      Vector whereEXP;
      if (queryColumnsVector.size() == 1) {
         if (queryColumnsVector.get(0) instanceof SelectColumn) {
            whereEXP = ((SelectColumn)queryColumnsVector.get(0)).getColumnExpression();
            if (whereEXP != null && whereEXP.size() == 1) {
               if (whereEXP.contains("*")) {
                  if (columnHashtable != null) {
                     int size = 0;
                     Enumeration enum1 = columnHashtable.keys();

                     SelectColumn sc;
                     for(String nextEle = ""; enum1.hasMoreElements(); newItemList.add(sc)) {
                        ++size;
                        sc = new SelectColumn();
                        TableColumn tc = new TableColumn();
                        ColEx = new Vector();
                        nextEle = (String)enum1.nextElement();
                        tc.setColumnName(nextEle);
                        ColEx.add(tc);
                        sc.setColumnExpression(ColEx);
                        if (size < columnHashtable.size()) {
                           sc.setEndsWith(",");
                        }
                     }
                  }

                  selectStatement.setSelectItemList(newItemList);
               } else if (!whereEXP.contains("*")) {
                  Vector selVector1 = selstmt.getSelectItemList();
                  selectStatement.setSelectItemList(selVector1);
               }
            }
         }
      } else {
         whereEXP = selstmt.getSelectItemList();
         selectStatement.setSelectItemList(whereEXP);
      }

      tsqlSelectQueryStatement.setSelectStatement(selectStatement);
      FromClause newFromClause = new FromClause();
      newFromClause.setFromClause("FROM");
      FunctionCalls functionCall = new FunctionCalls();
      TableColumn tableColumn = new TableColumn();
      tableColumn.setColumnName(functionName);
      functionCall.setFunctionName(tableColumn);
      SelectColumn fromSelectColumn = new SelectColumn();
      Vector fromArgumentsVector = new Vector();
      fromArgumentsVector.add(currentVariable);
      fromSelectColumn.setColumnExpression(fromArgumentsVector);
      Vector fromClauseSelectItemVector = new Vector();
      fromClauseSelectItemVector.add(fromSelectColumn);
      functionCall.setFunctionArguments(fromClauseSelectItemVector);
      ColEx = new Vector();
      FromTable newFromTable = new FromTable();
      newFromTable.setOuterOpenBrace((String)null);
      newFromTable.setOuterClosedBrace((String)null);
      newFromTable.setTableName(functionCall);
      ColEx.add(newFromTable);
      newFromClause.setFromItemList(ColEx);
      tsqlSelectQueryStatement.setFromClause(newFromClause);
      tsqlSelectQueryStatement.setHierarchicalQueryClause((HierarchicalQueryClause)null);
      return tsqlSelectQueryStatement;
   }

   private Vector getSelectColumnsForStartWithConnectBy(SelectStatement selectStatement) {
      Vector toReturnVector = new Vector();
      Vector selectItemsVector = selectStatement.getSelectItemList();

      for(int i = 0; i < selectItemsVector.size(); ++i) {
         SelectColumn selectColumn = (SelectColumn)selectItemsVector.elementAt(i);
         String selectColumnString = selectColumn.toString().trim();
         if (selectColumnString.endsWith(",")) {
            selectColumnString = selectColumnString.substring(0, selectColumnString.length() - 1);
         }

         toReturnVector.addElement(selectColumnString);
      }

      return toReturnVector;
   }

   private String getSelectColumnsForStartWithConnectByString(Vector selectColumnsStringVector) {
      String toReturnSelectString = "";

      for(int i = 0; i < selectColumnsStringVector.size(); ++i) {
         String selectColumnAsString = (String)selectColumnsStringVector.elementAt(i);
         toReturnSelectString = toReturnSelectString + ", " + selectColumnAsString.toString();
      }

      toReturnSelectString = toReturnSelectString.substring(1);
      return toReturnSelectString;
   }

   private String getSelectColumnsDeclaration(String tableName, Vector selectColumnsStringVector) {
      String toReturn = "";
      Hashtable tSQLTables = SwisSQLAPI.getDataTypesFromMetaDataHT();
      Hashtable columnHashtable = (Hashtable)tSQLTables.get(tableName.toUpperCase());
      Hashtable tableColumns = SwisSQLAPI.getTableColumnListMetadata();
      ArrayList orderedColumnsList = (ArrayList)tableColumns.get(tableName.toUpperCase());
      String columnName;
      if (columnHashtable != null) {
         Enumeration enum2 = columnHashtable.keys();
         columnName = "";
         String columnType = "";

         for(int i = 0; i < selectColumnsStringVector.size(); ++i) {
            if (((String)selectColumnsStringVector.elementAt(0)).equals("*")) {
               for(int n = 0; n < orderedColumnsList.size(); ++n) {
                  columnName = (String)orderedColumnsList.get(n);
                  columnType = (String)columnHashtable.get(columnName.toUpperCase());
                  if (columnType != null && columnType.toLowerCase().trim().startsWith("number")) {
                     columnType = columnType.replaceFirst("number", "numeric");
                  } else if (columnType != null && columnType.toLowerCase().trim().startsWith("varchar2(")) {
                     columnType = columnType.replaceFirst("varchar2", "varchar");
                  }

                  toReturn = toReturn + ", " + columnName + " " + columnType;
               }
            } else {
               columnName = (String)selectColumnsStringVector.elementAt(i);
               columnName = (String)selectColumnsStringVector.elementAt(i);
               columnType = (String)columnHashtable.get(columnName.toUpperCase());
               if (columnType != null && columnType.toLowerCase().trim().startsWith("number")) {
                  columnType = columnType.replaceFirst("number", "numeric");
               } else if (columnType != null && columnType.toLowerCase().trim().startsWith("varchar2(")) {
                  columnType = columnType.replaceFirst("varchar2", "varchar");
               }

               toReturn = toReturn + ", " + columnName + " " + columnType;
            }
         }
      } else {
         String columnType = "VARCHAR(100)";
         columnName = "";

         for(int i = 0; i < selectColumnsStringVector.size(); ++i) {
            columnName = (String)selectColumnsStringVector.elementAt(i);
            toReturn = toReturn + ", " + columnName + " " + columnType;
         }
      }

      toReturn = toReturn.substring(1);
      return toReturn;
   }

   public String StartWithConnectByGenerator(Object columnVariable, boolean isPriorChild, int tableItemSize, String additionalConditions) {
      StringBuffer generatedOutput = new StringBuffer();

      try {
         FileInputStream fis = null;
         if (tableItemSize == 1) {
            if (isPriorChild) {
               if (additionalConditions != null) {
                  fis = new FileInputStream("conf/TreeTraversal_PRIOR_ADDI.conf");
               } else {
                  fis = new FileInputStream("conf/TreeTraversal.conf");
               }
            } else {
               fis = new FileInputStream("conf/TreeTraversal1.conf");
            }
         } else if (tableItemSize > 1) {
            fis = new FileInputStream("conf/TreeTraversal2.conf");
         }

         InputStreamReader isr = new InputStreamReader(fis);
         BufferedReader br = new BufferedReader(isr);
         if (columnVariable != null || this.startWithSubQuery) {
            generatedOutput.append("\n");

            for(String comment = br.readLine(); comment != null; comment = br.readLine()) {
               generatedOutput.append("\t");
               generatedOutput.append(comment + "\n");
            }
         }
      } catch (FileNotFoundException var10) {
         var10.printStackTrace();
      } catch (IOException var11) {
         var11.printStackTrace();
      }

      return generatedOutput.toString();
   }

   public String getStartWithValue(SelectQueryStatement tsqlSelectQueryStatement) {
      String childColumn = null;
      String column = null;
      String priorColumn = null;
      String currentVariable = null;

      try {
         HierarchicalQueryClause treeStatement = tsqlSelectQueryStatement.getHierarchicalQueryClause();
         if (treeStatement.getStartWithCondition() != null && treeStatement.getConnectByCondition() != null) {
            WhereExpression startWithExpression = treeStatement.getStartWithCondition();
            WhereExpression connectByExpression = treeStatement.getConnectByCondition();
            Vector whereItemList1 = startWithExpression.getWhereItem();
            Vector whereItemList2 = connectByExpression.getWhereItem();
            if (whereItemList1 != null && whereItemList1.size() == 1 && whereItemList2 != null && whereItemList2.size() == 1 && whereItemList1.get(0) instanceof WhereItem) {
               WhereColumn startWithLeftWhereColumn = ((WhereItem)whereItemList1.get(0)).getLeftWhereExp();
               WhereColumn startWithRightWhereColumn = ((WhereItem)whereItemList1.get(0)).getRightWhereExp();
               Vector startWithRightExp;
               if (((WhereItem)whereItemList1.get(0)).getRightWhereSubQuery() != null && ((WhereItem)whereItemList1.get(0)).getRightWhereSubQuery() instanceof SelectQueryStatement) {
                  this.startWithSubQuery = true;
                  this.selectQryStment = ((WhereItem)whereItemList1.get(0)).getRightWhereSubQuery();
                  SelectStatement selectStmnt = this.selectQryStment.getSelectStatement();
                  selectStmnt.setSelectQualifier("DISTINCT");
                  if (startWithLeftWhereColumn != null) {
                     startWithRightExp = startWithLeftWhereColumn.getColumnExpression();
                     if (startWithRightExp != null && startWithRightExp.size() == 1 && startWithRightExp.get(0) instanceof TableColumn) {
                        childColumn = ((TableColumn)startWithRightExp.get(0)).toString();
                     }
                  }
               } else if (startWithLeftWhereColumn != null) {
                  Vector startWithLeftExp = startWithLeftWhereColumn.getColumnExpression();
                  startWithRightExp = startWithRightWhereColumn.getColumnExpression();
                  if (startWithLeftExp != null && startWithRightExp != null && startWithLeftExp.size() == 1 && startWithRightExp.size() == 1 && startWithLeftExp.get(0) instanceof TableColumn && (startWithRightExp.get(0) instanceof TableColumn || startWithRightExp.get(0) instanceof String)) {
                     childColumn = ((TableColumn)startWithLeftExp.get(0)).toString();
                     if (startWithRightExp.get(0) instanceof TableColumn) {
                        currentVariable = ((TableColumn)startWithRightExp.get(0)).toString();
                     } else if (startWithRightExp.get(0) instanceof String) {
                        currentVariable = startWithRightExp.get(0).toString();
                     } else if (startWithLeftExp.get(0) instanceof TableColumn) {
                        currentVariable = ((TableColumn)startWithLeftExp.get(0)).toString();
                     } else {
                        currentVariable = (String)startWithLeftExp.get(0);
                     }
                  }
               }
            }
         }
      } catch (Exception var15) {
         var15.printStackTrace();
      }

      return currentVariable;
   }
}
