package com.adventnet.swissqlapi.sql.functions.math;

import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.functions.date.extract;
import com.adventnet.swissqlapi.sql.statement.create.DateClass;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.util.database.MetadataInfoUtil;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.Vector;

public class trunc extends FunctionCalls {
   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      boolean isdenodo = from_sqs != null && from_sqs.isDenodo();
      if (this.functionName.getColumnName().equalsIgnoreCase("trunc") || this.functionName.getColumnName().equalsIgnoreCase("truncate")) {
         this.functionName.setColumnName("TRUNC");

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         this.setFunctionArguments(arguments);
      }

      if (isdenodo) {
         String qry = "";
         if (this.functionName.getColumnName().equalsIgnoreCase("second")) {
            for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
               if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                  arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
               } else {
                  arguments.addElement(this.functionArguments.elementAt(i_count));
               }
            }

            qry = "CAST(GETSECOND(" + StringFunctions.handleLiteralStringDateForDenodo(this.functionArguments.get(0).toString()) + ") AS INT8)";
         } else {
            qry = arguments.size() == 1 ? "TRUNC(" + arguments.get(0) + ")" : "CAST(" + arguments.get(0) + " AS DECIMAL(38," + arguments.get(1) + "))";
         }

         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else {
         TableColumn subTC;
         Vector subFunctionArgs;
         Vector scColumnExpression;
         SelectColumn sc;
         Vector scColumnExpression;
         FunctionCalls subFunction;
         if (this.functionName.getColumnName().equalsIgnoreCase("integer") || this.functionName.getColumnName().equalsIgnoreCase("int")) {
            this.functionName.setColumnName("TRUNC");
            subFunction = new FunctionCalls();
            subTC = new TableColumn();
            subTC.setColumnName("TO_NUMBER");
            subFunction.setFunctionName(subTC);
            subFunctionArgs = new Vector();
            scColumnExpression = new Vector();
            sc = new SelectColumn();
            subFunctionArgs.add(this.functionArguments.get(0));
            subFunction.setFunctionArguments(subFunctionArgs);
            scColumnExpression.add(subFunction);
            sc.setColumnExpression(scColumnExpression);
            scColumnExpression = new Vector();
            scColumnExpression.add(sc);
            this.setFunctionArguments(scColumnExpression);
         }

         Vector newArgument;
         if (this.functionName.getColumnName().equalsIgnoreCase("decimal") || this.functionName.getColumnName().equalsIgnoreCase("dec")) {
            this.functionName.setColumnName("TRUNC");
            subFunction = new FunctionCalls();
            subTC = new TableColumn();
            subTC.setColumnName("TO_NUMBER");
            subFunction.setFunctionName(subTC);
            subFunctionArgs = new Vector();
            scColumnExpression = new Vector();
            sc = new SelectColumn();
            subFunctionArgs.add(this.functionArguments.get(0));
            subFunction.setFunctionArguments(subFunctionArgs);
            scColumnExpression.add(subFunction);
            sc.setColumnExpression(scColumnExpression);
            Object scaleObj = null;
            if (this.functionArguments.size() > 2) {
               scaleObj = this.functionArguments.get(2);
            }

            newArgument = new Vector();
            newArgument.add(sc);
            if (scaleObj != null) {
               newArgument.add(scaleObj);
            }

            this.setFunctionArguments(newArgument);
         }

         if (this.functionName.getColumnName().equalsIgnoreCase("second")) {
            this.functionName.setColumnName("TRUNC");
            subFunction = new FunctionCalls();
            subTC = new TableColumn();
            subTC.setColumnName("EXTRACT");
            subFunction.setFunctionName(subTC);
            subFunctionArgs = new Vector();
            Object arg = this.functionArguments.get(0);
            if (arg instanceof SelectColumn) {
               ((SelectColumn)arg).getColumnExpression().add(0, "SECOND FROM");
               subFunctionArgs.add(((SelectColumn)arg).toOracleSelect(to_sqs, from_sqs));
            } else {
               subFunctionArgs.add(arg);
            }

            subFunction.setFunctionArguments(subFunctionArgs);
            sc = new SelectColumn();
            scColumnExpression = new Vector();
            scColumnExpression.add(subFunction);
            sc.setColumnExpression(scColumnExpression);
            newArgument = new Vector();
            newArgument.add(sc);
            this.setFunctionArguments(newArgument);
         }

      }
   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      boolean isArgDate = false;
      if (from_sqs == null || !from_sqs.isMSAzure() || !this.functionName.getColumnName().equalsIgnoreCase("decimal")) {
         Vector selectColV;
         String funcArgument1;
         FunctionCalls fc;
         TableColumn tc;
         Vector v;
         Vector tcV;
         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (!(this.functionArguments.elementAt(i_count) instanceof SelectColumn)) {
               if (this.functionArguments.get(i_count) instanceof String) {
                  funcArgument1 = (String)this.functionArguments.get(i_count);
                  if (funcArgument1.trim().equalsIgnoreCase("SYSDATE")) {
                     isArgDate = true;
                     funcArgument1 = "GETDATE()";
                  }

                  if (funcArgument1.trim().equalsIgnoreCase("SYS_GUID")) {
                     funcArgument1 = "NEWID()";
                  }

                  arguments.addElement(funcArgument1);
               } else {
                  arguments.addElement(this.functionArguments.elementAt(i_count));
               }
            } else {
               Vector colExpressions = ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression();
               if (colExpressions != null) {
                  for(int i = 0; i < colExpressions.size(); ++i) {
                     String columnName;
                     if (colExpressions.get(i) instanceof TableColumn) {
                        TableColumn tc = (TableColumn)colExpressions.get(i);
                        String columnName = tc.getColumnName();
                        if (columnName != null && columnName.equalsIgnoreCase("SYSDATE")) {
                           isArgDate = true;
                        } else if (columnName != null) {
                           columnName = MetadataInfoUtil.getDatatypeName(from_sqs, tc);
                           if (columnName != null && columnName.toLowerCase().indexOf("date") != -1) {
                              isArgDate = true;
                           } else {
                              String dataType1;
                              if (columnName.startsWith(":")) {
                                 if (SwisSQLAPI.variableDatatypeMapping != null && SwisSQLAPI.variableDatatypeMapping.containsKey(columnName.substring(1))) {
                                    dataType1 = (String)SwisSQLAPI.variableDatatypeMapping.get(columnName.substring(1));
                                    if (dataType1.toLowerCase().indexOf("date") != -1) {
                                       isArgDate = true;
                                    }
                                 }
                              } else if (columnName == null && SwisSQLAPI.variableDatatypeMapping != null && SwisSQLAPI.variableDatatypeMapping.containsKey(columnName)) {
                                 dataType1 = (String)SwisSQLAPI.variableDatatypeMapping.get(columnName);
                                 if (dataType1.toLowerCase().indexOf("date") != -1) {
                                    isArgDate = true;
                                 }
                              }
                           }
                        }
                     } else if (colExpressions.get(i) instanceof FunctionCalls) {
                        fc = (FunctionCalls)colExpressions.get(i);
                        tc = fc.getFunctionName();
                        columnName = tc.getColumnName();
                        if (tc.getColumnName().toString().equalsIgnoreCase("TO_DATE")) {
                           isArgDate = true;
                        } else {
                           v = fc.getFunctionArguments();
                           if (v != null) {
                              for(int k = 0; k < v.size(); ++k) {
                                 Object obj = v.elementAt(k);
                                 if (obj instanceof SelectColumn) {
                                    tcV = ((SelectColumn)obj).getColumnExpression();
                                    if (tcV != null) {
                                       for(int m = 0; m < tcV.size(); ++m) {
                                          Object tcObj = tcV.elementAt(m);
                                          if (tcObj instanceof TableColumn) {
                                             String dataType = MetadataInfoUtil.getDatatypeName(from_sqs, (TableColumn)tcObj);
                                             if (dataType != null && dataType.toLowerCase().indexOf("date") != -1) {
                                                isArgDate = true;
                                             } else {
                                                String dataType1;
                                                if (columnName.startsWith(":")) {
                                                   if (SwisSQLAPI.variableDatatypeMapping != null && SwisSQLAPI.variableDatatypeMapping.containsKey(columnName.substring(1))) {
                                                      dataType1 = (String)SwisSQLAPI.variableDatatypeMapping.get(columnName.substring(1));
                                                      if (dataType1.toLowerCase().indexOf("date") != -1) {
                                                         isArgDate = true;
                                                      }
                                                   }
                                                } else if (dataType == null && SwisSQLAPI.variableDatatypeMapping != null && SwisSQLAPI.variableDatatypeMapping.containsKey(columnName)) {
                                                   dataType1 = (String)SwisSQLAPI.variableDatatypeMapping.get(columnName);
                                                   if (dataType1.toLowerCase().indexOf("date") != -1) {
                                                      isArgDate = true;
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
                     } else if (!(colExpressions.get(i) instanceof SelectQueryStatement)) {
                        if (colExpressions.get(i) instanceof String && (colExpressions.get(i).toString().equalsIgnoreCase("'MONTH'") || colExpressions.get(i).toString().equalsIgnoreCase("'DAY'") || colExpressions.get(i).toString().equalsIgnoreCase("'YEAR'") || colExpressions.get(i).toString().equalsIgnoreCase("'WEEK'") || colExpressions.get(i).toString().equalsIgnoreCase("'HOUR'") || colExpressions.get(i).toString().equalsIgnoreCase("'MINUTE'") || colExpressions.get(i).toString().equalsIgnoreCase("'SECOND'"))) {
                           isArgDate = true;
                        }
                     } else {
                        SelectQueryStatement sqs = (SelectQueryStatement)colExpressions.get(i);
                        SelectStatement ss = sqs.getSelectStatement();
                        selectColV = ss.getSelectItemList();
                        SelectColumn sc = (SelectColumn)selectColV.get(0);
                        Vector colExprV = sc.getColumnExpression();

                        for(int k = 0; k < colExprV.size(); ++k) {
                           Object obj = colExprV.get(k);
                           if (obj instanceof FunctionCalls) {
                              FunctionCalls fc = (FunctionCalls)obj;
                              Vector funcArgV = fc.getFunctionArguments();
                              if (funcArgV != null) {
                                 for(int j = 0; j < funcArgV.size(); ++j) {
                                    Object funcarg = funcArgV.get(j);
                                    if (funcarg instanceof SelectColumn) {
                                       Vector colExpV = ((SelectColumn)funcarg).getColumnExpression();
                                       if (colExpV.get(0) instanceof TableColumn) {
                                          String dataType = MetadataInfoUtil.getDatatypeName(from_sqs, (TableColumn)colExpV.get(0));
                                          if (dataType != null && dataType.toLowerCase().indexOf("date") != -1) {
                                             isArgDate = true;
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

               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
            }
         }

         this.setFunctionArguments(arguments);
         if (FunctionCalls.charToIntName) {
            this.functionName.setColumnName("");
            this.functionName.setColumnName("TRUNC");
            this.functionName.setTableName("dbo");
         } else {
            Vector newArguments;
            if (!isArgDate) {
               if (arguments.size() == 1) {
                  this.functionName.setColumnName("FLOOR");
                  newArguments = new Vector();
                  newArguments.add(arguments.get(0));
                  this.setFunctionArguments(arguments);
               } else if (arguments.size() == 2) {
                  this.functionName.setColumnName("");
                  SelectColumn truncSelectColumn = new SelectColumn();
                  SelectColumn castSelectColumn = new SelectColumn();
                  SelectColumn floorSelectColumn = new SelectColumn();
                  fc = new FunctionCalls();
                  tc = new TableColumn();
                  tc.setColumnName("FLOOR");
                  selectColV = new Vector();
                  selectColV.add(arguments.elementAt(0));
                  fc.setFunctionName(tc);
                  fc.setFunctionArguments(selectColV);
                  v = new Vector();
                  FunctionCalls castFunctionCall = new FunctionCalls();
                  TableColumn castFunction = new TableColumn();
                  castFunction.setColumnName("CAST");
                  castFunctionCall.setFunctionName(castFunction);
                  tcV = new Vector();
                  Vector floorInsideFloorArgument = new Vector();
                  FunctionCalls floorFunctionCall = new FunctionCalls();
                  TableColumn floorTableColumn = new TableColumn();
                  floorTableColumn.setColumnName("FLOOR");
                  tcV.addElement("(");
                  tcV.addElement(arguments.get(0));
                  tcV.addElement(" - ");
                  FunctionCalls floorInsideFloorFunctionCall = new FunctionCalls();
                  TableColumn floorInsideFloorTableColumn = new TableColumn();
                  floorInsideFloorTableColumn.setColumnName("FLOOR");
                  floorInsideFloorArgument.addElement(arguments.get(0));
                  floorInsideFloorFunctionCall.setFunctionName(floorInsideFloorTableColumn);
                  floorInsideFloorFunctionCall.setFunctionArguments(floorInsideFloorArgument);
                  tcV.addElement(floorInsideFloorFunctionCall);
                  tcV.addElement(")");
                  tcV.addElement(" * ");
                  Vector powerArgument = new Vector();
                  FunctionCalls powerFunctionCall = new FunctionCalls();
                  TableColumn powerTableColumn = new TableColumn();
                  powerTableColumn.setColumnName("POWER");
                  powerArgument.addElement("10");
                  powerArgument.addElement(arguments.get(1));
                  powerFunctionCall.setFunctionName(powerTableColumn);
                  powerFunctionCall.setFunctionArguments(powerArgument);
                  tcV.addElement(powerFunctionCall);
                  floorFunctionCall.setFunctionName(floorTableColumn);
                  floorSelectColumn.setColumnExpression(tcV);
                  Vector floorSCArgument = new Vector();
                  floorSCArgument.add(floorSelectColumn);
                  floorFunctionCall.setFunctionArguments(floorSCArgument);
                  Vector castArgument = new Vector();
                  castArgument.addElement(floorFunctionCall);
                  castArgument.addElement("/");
                  castArgument.addElement(powerFunctionCall);
                  castArgument.addElement(" AS");
                  castArgument.addElement(" FLOAT");
                  castSelectColumn.setColumnExpression(castArgument);
                  Vector castSCArgument = new Vector();
                  castSCArgument.add(castSelectColumn);
                  castFunctionCall.setFunctionArguments(castSCArgument);
                  v.addElement(fc);
                  v.addElement("+");
                  v.addElement(castFunctionCall);
                  truncSelectColumn.setColumnExpression(v);
                  Vector truncSCArgument = new Vector();
                  truncSCArgument.add(truncSelectColumn);
                  this.setFunctionArguments(truncSCArgument);
               }
            } else if (arguments.size() == 1) {
               this.functionName.setColumnName("CONVERT");
               newArguments = new Vector();
               newArguments.add("DATETIME");
               FunctionCalls newFunction = new FunctionCalls();
               TableColumn tc = new TableColumn();
               tc.setColumnName("CONVERT");
               newFunction.setFunctionName(tc);
               Vector functionArgs = new Vector();
               functionArgs.add("VARCHAR");
               functionArgs.add(arguments.get(0));
               functionArgs.add("112");
               newFunction.setFunctionArguments(functionArgs);
               newArguments.add(newFunction);
               this.setFunctionArguments(newArguments);
            } else if (arguments.size() == 2) {
               String funcArgument0 = arguments.get(0).toString();
               funcArgument1 = arguments.get(1).toString();
               if (funcArgument1.toLowerCase().startsWith("timestamp")) {
                  funcArgument1 = StringFunctions.replaceFirst("", "timestamp", funcArgument1).trim();
               }

               if (arguments.get(1).toString().equalsIgnoreCase("'MONTH'")) {
                  this.functionName.setColumnName("CONVERT(DATETIME, CONVERT(VARCHAR, " + arguments.get(0).toString() + ", 121)) - datepart(day," + arguments.get(0).toString() + ") + 1");
               } else if (arguments.get(1).toString().equalsIgnoreCase("'DAY'")) {
                  this.functionName.setColumnName("CONVERT(DATETIME, CONVERT(VARCHAR, " + arguments.get(0).toString() + ", 121)) - datepart(dw," + arguments.get(0).toString() + ") + 1");
               }

               if (funcArgument0.equalsIgnoreCase("'MONTH'")) {
                  this.functionName.setColumnName("DATEADD(mm, DATEDIFF(mm,0," + funcArgument1 + "), 0)");
               } else if (funcArgument0.equalsIgnoreCase("'DAY'")) {
                  this.functionName.setColumnName("DATEADD(dd, DATEDIFF(dd,0," + funcArgument1 + "), 0)");
               } else if (funcArgument0.equalsIgnoreCase("'YEAR'")) {
                  this.functionName.setColumnName("DATEADD(yy, DATEDIFF(yy,0," + funcArgument1 + "), 0)");
               }

               if (funcArgument0.equalsIgnoreCase("'WEEK'")) {
                  this.functionName.setColumnName("DATEADD(wk, DATEDIFF(wk,0," + funcArgument1 + "), 0)");
               }

               if (funcArgument0.equalsIgnoreCase("'HOUR'")) {
                  this.functionName.setColumnName("DATEADD(hh, DATEDIFF(hh,0," + funcArgument1 + "), 0)");
               }

               if (funcArgument0.equalsIgnoreCase("'MINUTE'")) {
                  this.functionName.setColumnName("DATEADD(mi, DATEDIFF(mi,0," + funcArgument1 + "), 0)");
               }

               if (funcArgument0.equalsIgnoreCase("'SECOND'")) {
                  this.functionName.setColumnName("CONVERT(DATETIME, CONVERT(VARCHAR," + funcArgument1 + ", 120)) ");
               }

               Vector newFnArgs = new Vector();
               this.setFunctionArguments(newFnArgs);
               this.setOpenBracesForFunctionNameRequired(false);
            }
         }

      }
   }

   public void toSnowflake(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSnowflakeSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toSybase(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      boolean isArgDate = false;

      FunctionCalls fc;
      TableColumn tc;
      String columnName;
      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (!(this.functionArguments.elementAt(i_count) instanceof SelectColumn)) {
            if (this.functionArguments.get(i_count) instanceof String) {
               String s = (String)this.functionArguments.get(i_count);
               if (s.trim().equalsIgnoreCase("SYSDATE")) {
                  isArgDate = true;
                  s = "GETDATE()";
               }

               if (s.trim().equalsIgnoreCase("SYS_GUID")) {
                  s = "NEWID()";
               }

               arguments.addElement(s);
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         } else {
            Vector colExpressions = ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression();
            if (colExpressions != null) {
               for(int i = 0; i < colExpressions.size(); ++i) {
                  String dataType;
                  if (colExpressions.get(i) instanceof TableColumn) {
                     TableColumn tc = (TableColumn)colExpressions.get(i);
                     columnName = tc.getColumnName();
                     if (columnName != null && columnName.equalsIgnoreCase("SYSDATE")) {
                        isArgDate = true;
                     } else if (columnName != null) {
                        dataType = MetadataInfoUtil.getDatatypeName(from_sqs, tc);
                        if (dataType != null && dataType.toLowerCase().indexOf("date") != -1) {
                           isArgDate = true;
                        } else {
                           String dataType1;
                           if (columnName.startsWith(":")) {
                              if (SwisSQLAPI.variableDatatypeMapping != null) {
                                 if (SwisSQLAPI.variableDatatypeMapping.containsKey(columnName.substring(1))) {
                                    dataType1 = (String)SwisSQLAPI.variableDatatypeMapping.get(columnName.substring(1));
                                    if (dataType1.toLowerCase().indexOf("date") != -1) {
                                       isArgDate = true;
                                    }
                                 } else if (!SwisSQLOptions.convertTruncWithVariableToFloor) {
                                    isArgDate = true;
                                 }
                              } else if (!SwisSQLOptions.convertTruncWithVariableToFloor) {
                                 isArgDate = true;
                              }
                           } else if (dataType == null && !SwisSQLOptions.convertTruncWithVariableToFloor) {
                              isArgDate = true;
                           } else if (dataType == null && SwisSQLAPI.variableDatatypeMapping != null && SwisSQLAPI.variableDatatypeMapping.containsKey(columnName)) {
                              dataType1 = (String)SwisSQLAPI.variableDatatypeMapping.get(columnName);
                              if (dataType1.toLowerCase().indexOf("date") != -1) {
                                 isArgDate = true;
                              }
                           }
                        }
                     }
                  } else if (colExpressions.get(i) instanceof FunctionCalls) {
                     fc = (FunctionCalls)colExpressions.get(i);
                     tc = fc.getFunctionName();
                     if (tc.getColumnName().toString().equalsIgnoreCase("TO_DATE")) {
                        isArgDate = true;
                     } else {
                        dataType = MetadataInfoUtil.getDatatypeName(from_sqs, tc);
                        if (dataType != null && dataType.toLowerCase().indexOf("date") != -1) {
                           isArgDate = true;
                        }
                     }
                  } else if (colExpressions.get(i) instanceof String && colExpressions.get(i).toString().equalsIgnoreCase("'MONTH'")) {
                     isArgDate = true;
                  }
               }
            }

            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSybaseSelect(to_sqs, from_sqs));
         }
      }

      this.setFunctionArguments(arguments);
      Vector newArguments;
      if (!isArgDate) {
         if (arguments.size() == 1) {
            this.functionName.setColumnName("FLOOR");
            newArguments = new Vector();
            newArguments.add(arguments.get(0));
            this.setFunctionArguments(arguments);
         } else if (arguments.size() == 2) {
            this.functionName.setColumnName("");
            SelectColumn truncSelectColumn = new SelectColumn();
            SelectColumn castSelectColumn = new SelectColumn();
            SelectColumn floorSelectColumn = new SelectColumn();
            fc = new FunctionCalls();
            tc = new TableColumn();
            tc.setColumnName("FLOOR");
            Vector firstFloorArg = new Vector();
            firstFloorArg.add(arguments.elementAt(0));
            fc.setFunctionName(tc);
            fc.setFunctionArguments(firstFloorArg);
            Vector truncArgument = new Vector();
            FunctionCalls castFunctionCall = new FunctionCalls();
            TableColumn castFunction = new TableColumn();
            castFunction.setColumnName("CONVERT");
            castFunctionCall.setFunctionName(castFunction);
            Vector floorArgument = new Vector();
            Vector floorInsideFloorArgument = new Vector();
            FunctionCalls floorFunctionCall = new FunctionCalls();
            TableColumn floorTableColumn = new TableColumn();
            floorTableColumn.setColumnName("FLOAT,FLOOR");
            floorArgument.addElement("(");
            floorArgument.addElement(arguments.get(0));
            floorArgument.addElement(" - ");
            FunctionCalls floorInsideFloorFunctionCall = new FunctionCalls();
            TableColumn floorInsideFloorTableColumn = new TableColumn();
            floorInsideFloorTableColumn.setColumnName("FLOOR");
            floorInsideFloorArgument.addElement(arguments.get(0));
            floorInsideFloorFunctionCall.setFunctionName(floorInsideFloorTableColumn);
            floorInsideFloorFunctionCall.setFunctionArguments(floorInsideFloorArgument);
            floorArgument.addElement(floorInsideFloorFunctionCall);
            floorArgument.addElement(")");
            floorArgument.addElement(" * ");
            Vector powerArgument = new Vector();
            FunctionCalls powerFunctionCall = new FunctionCalls();
            TableColumn powerTableColumn = new TableColumn();
            powerTableColumn.setColumnName("POWER");
            powerArgument.addElement("10");
            powerArgument.addElement(arguments.get(1));
            powerFunctionCall.setFunctionName(powerTableColumn);
            powerFunctionCall.setFunctionArguments(powerArgument);
            floorArgument.addElement(powerFunctionCall);
            floorFunctionCall.setFunctionName(floorTableColumn);
            floorSelectColumn.setColumnExpression(floorArgument);
            Vector floorSCArgument = new Vector();
            floorSCArgument.add(floorSelectColumn);
            floorFunctionCall.setFunctionArguments(floorSCArgument);
            Vector castArgument = new Vector();
            castArgument.addElement(floorFunctionCall);
            castArgument.addElement("/");
            castArgument.addElement(powerFunctionCall);
            castSelectColumn.setColumnExpression(castArgument);
            Vector castSCArgument = new Vector();
            castSCArgument.add(castSelectColumn);
            castFunctionCall.setFunctionArguments(castSCArgument);
            truncArgument.addElement(fc);
            truncArgument.addElement("+");
            truncArgument.addElement(castFunctionCall);
            truncSelectColumn.setColumnExpression(truncArgument);
            Vector truncSCArgument = new Vector();
            truncSCArgument.add(truncSelectColumn);
            this.setFunctionArguments(truncSCArgument);
         }
      } else if (arguments.size() == 1) {
         this.functionName.setColumnName("CONVERT");
         newArguments = new Vector();
         newArguments.add("DATETIME");
         FunctionCalls newFunction = new FunctionCalls();
         TableColumn tc = new TableColumn();
         tc.setColumnName("CONVERT");
         newFunction.setFunctionName(tc);
         Vector functionArgs = new Vector();
         functionArgs.add("VARCHAR");
         functionArgs.add(arguments.get(0));
         columnName = SwisSQLOptions.dateFormatForConvertFunction;
         if (columnName != null && columnName.length() > 0) {
            functionArgs.add(columnName);
         } else {
            functionArgs.add("112");
         }

         newFunction.setFunctionArguments(functionArgs);
         newArguments.add(newFunction);
         this.setFunctionArguments(newArguments);
      } else if (arguments.size() == 2 && arguments.get(1).toString().equalsIgnoreCase("'MONTH'")) {
         this.functionName.setColumnName("CONVERT(DATETIME, CONVERT(VARCHAR, " + arguments.get(0).toString() + ", 121)) - datepart(day," + arguments.get(0).toString() + ") + 1");
         newArguments = new Vector();
         this.setFunctionArguments(newArguments);
         this.setOpenBracesForFunctionNameRequired(false);
      }

   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (!this.functionName.getColumnName().equalsIgnoreCase("DECIMAL")) {
         boolean datetype = false;
         Vector arguments;
         Vector newArguments;
         if (this.functionArguments.size() > 0 && this.functionArguments.elementAt(0) instanceof SelectColumn) {
            arguments = ((SelectColumn)this.functionArguments.elementAt(0)).getColumnExpression();
            if (arguments.size() == 1) {
               if (arguments.elementAt(0).toString().equalsIgnoreCase("sysdate")) {
                  datetype = true;
                  this.functionName.setColumnName("TIMESTAMP");
                  newArguments = new Vector();
                  newArguments.addElement("SUBSTR(CHAR(CURRENT TIMESTAMP), 1, 10) || '-00.00.00.000000'");
                  this.setFunctionArguments(newArguments);
                  return;
               }

               if (arguments.elementAt(0) instanceof TableColumn) {
                  String datatype = MetadataInfoUtil.getDatatypeName(from_sqs, (TableColumn)arguments.elementAt(0));
                  if (datatype != null && (datatype.indexOf("date") != -1 || datatype.indexOf("timestamp") != -1)) {
                     datetype = true;
                     this.functionName.setColumnName("TIMESTAMP");
                     Vector arguments = new Vector();
                     arguments.addElement("SUBSTR(CHAR(" + arguments.elementAt(0) + "), 1, 10) || '-00.00.00.000000'");
                     this.setFunctionArguments(arguments);
                     return;
                  }
               }
            }
         }

         if (!datetype && !this.functionName.getColumnName().trim().equalsIgnoreCase("INTEGER")) {
            this.functionName.setColumnName("TRUNC");
            arguments = new Vector();

            for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
               if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                  arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toDB2Select(to_sqs, from_sqs));
               } else {
                  arguments.addElement(this.functionArguments.elementAt(i_count));
               }
            }

            this.setFunctionArguments(arguments);
            if (SwisSQLOptions.UDBSQL) {
               this.functionName.setColumnName("TRUNC");
               return;
            }

            if (arguments.size() == 1) {
               this.functionName.setColumnName("CAST");
               newArguments = new Vector();
               newArguments.add(arguments.get(0));
               this.setAsDatatype("AS");
               newArguments.add("INTEGER");
               this.setFunctionArguments(newArguments);
            }
         }

      }
   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      boolean isPostgreLiveDbs = from_sqs != null && (from_sqs.isAmazonRedShift() || from_sqs.isPgsqlLive() || from_sqs.isVerticaDb());
      boolean canUseUDFFunction = from_sqs != null && !isPostgreLiveDbs && from_sqs.getBooleanValues("use.udf.functions.for.date.time");
      if (!this.functionName.getColumnName().equalsIgnoreCase("date_trunc")) {
         Vector arguments = new Vector();

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               if (this.functionName.getColumnName().equalsIgnoreCase("SECOND")) {
                  this.handleStringLiteralForTime(from_sqs, i_count, true, true);
                  ((SelectColumn)this.functionArguments.elementAt(i_count)).modifyCurrentTimeAsCurrentTimestamp(from_sqs);
               }

               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         if (this.functionName.getColumnName().equalsIgnoreCase("TRUNCATE")) {
            if (from_sqs != null && from_sqs.isAmazonRedShift()) {
               this.functionName.setColumnName("TRUNC(" + arguments.get(0).toString() + "," + arguments.get(1).toString() + ")");
            } else {
               this.functionName.setColumnName("TRUNC(cast(" + arguments.get(0).toString() + " as numeric)," + arguments.get(1).toString() + ")");
            }

            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         } else if (this.functionName.getColumnName().equalsIgnoreCase("SECOND")) {
            String qry = extract.applyDatePartOrExtractWrapper(from_sqs, "SECOND", arguments.get(0) + "::time");
            if (canUseUDFFunction) {
               qry = "SECOND(" + arguments.get(0).toString() + ")";
            }

            if (isPostgreLiveDbs) {
               if (arguments.get(0) instanceof SelectColumn) {
                  qry = "EXTRACT(SECOND FROM " + arguments.get(0).toString() + ")";
               } else if (arguments.get(0).toString().contains("-") || arguments.get(0).toString().contains("/")) {
                  qry = "EXTRACT(SECOND FROM " + arguments.get(0).toString() + " :: timestamp)";
               }
            }

            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         }

      }
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionName.getColumnName().trim().equalsIgnoreCase("SECOND")) {
         this.functionName.setColumnName("SECOND");
      } else if (this.functionName.getColumnName().trim().equalsIgnoreCase("DECIMAL")) {
         this.functionName.setColumnName("DECIMAL");
      } else {
         this.functionName.setColumnName("TRUNCATE");
      }

      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (from_sqs.getBooleanValues("consider.precision.for.to.decimal") && this.functionName.getColumnName().trim().equalsIgnoreCase("DECIMAL")) {
         SelectColumn sc_precision = new SelectColumn();
         Vector vc_precision = new Vector();
         vc_precision.add("38");
         sc_precision.setColumnExpression(vc_precision);
         arguments.setElementAt(sc_precision, 0);
      }

      this.setFunctionArguments(arguments);
      if (arguments.size() == 1) {
         if (!this.functionName.getColumnName().trim().equalsIgnoreCase("SECOND")) {
            this.functionName.setColumnName("FLOOR");
         }

         Vector newArguments = new Vector();
         newArguments.add(arguments.get(0));
         this.setFunctionArguments(arguments);
      }

   }

   public void toANSISQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("TRUNCATE");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toANSISelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionName.getColumnName().trim().equalsIgnoreCase("DECIMAL")) {
         this.functionName.setColumnName("DECIMAL");
      } else {
         this.functionName.setColumnName("TRUNC");
      }

      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toInformixSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (this.functionName.getColumnName().trim().equalsIgnoreCase("DECIMAL") && arguments.get(0) instanceof SelectColumn) {
         Vector colExp = ((SelectColumn)arguments.get(0)).getColumnExpression();
         if (colExp != null && colExp.size() > 0 && colExp.get(0) instanceof String) {
            int precision = Integer.parseInt(colExp.get(0).toString());
            if (precision > 32) {
               colExp.set(0, "32");
            }
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toTimesTen(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      throw new ConvertException("\nThe function TRUNC is not supported in TimesTen 5.1.21\n");
   }

   public void toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnName = this.functionName.getColumnName();
      this.functionName.setColumnName("TRUNC");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toNetezzaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (fnName.equalsIgnoreCase("trunc") && arguments.size() == 1) {
         Object argument1 = arguments.elementAt(0);
         if (argument1 instanceof SelectColumn) {
            Vector colExp = ((SelectColumn)argument1).getColumnExpression();
            if (colExp.get(0) instanceof TableColumn) {
               String dataType = MetadataInfoUtil.getDatatypeName(from_sqs, (TableColumn)colExp.elementAt(0));
               if (dataType != null && (dataType.toLowerCase().indexOf("date") != -1 || dataType.toLowerCase().indexOf("timestamp") != -1)) {
                  this.functionName.setColumnName("DATE_TRUNC");
               } else if (((TableColumn)colExp.get(0)).getColumnName() != null && ((TableColumn)colExp.get(0)).getColumnName().equalsIgnoreCase("current_date")) {
                  this.functionName.setColumnName("DATE_TRUNC");
               }
            }
         }
      }

      if (fnName.equalsIgnoreCase("trunc") && arguments.size() > 1) {
         boolean quoted = false;
         String argument2 = arguments.elementAt(1).toString();
         if (argument2.startsWith("\"") || argument2.startsWith("'")) {
            quoted = true;
            argument2 = argument2.substring(1, argument2.length() - 1);
            if (!argument2.equalsIgnoreCase("mm") && !argument2.equalsIgnoreCase("rm")) {
               if (!argument2.equalsIgnoreCase("syyyy") && !argument2.equalsIgnoreCase("yyyy") && !argument2.equalsIgnoreCase("syear") && !argument2.equalsIgnoreCase("yyy") && !argument2.equalsIgnoreCase("yy") && !argument2.equalsIgnoreCase("y")) {
                  if (!argument2.equalsIgnoreCase("ddd") && !argument2.equalsIgnoreCase("dd") && !argument2.equalsIgnoreCase("j")) {
                     if (argument2.equalsIgnoreCase("hh") || argument2.equalsIgnoreCase("hh12") || argument2.equalsIgnoreCase("hh24")) {
                        argument2 = "HOUR";
                     }
                  } else {
                     argument2 = "DAY";
                  }
               } else {
                  argument2 = "YEAR";
               }
            } else {
               argument2 = "MON";
            }
         }

         for(int i = 0; i < SwisSQLUtils.getOracleDateFormats().length; ++i) {
            String dateFmt = SwisSQLUtils.getOracleDateFormats()[i];
            if (argument2.equalsIgnoreCase(dateFmt)) {
               Object obj = arguments.get(0);
               if (argument2.equalsIgnoreCase("mi")) {
                  argument2 = "MINUTE";
               }

               if (quoted) {
                  argument2 = "'" + argument2 + "'";
               }

               arguments.setElementAt(argument2, 0);
               arguments.setElementAt(obj, 1);
               this.functionName.setColumnName("DATE_TRUNC");
               break;
            }
         }
      }

   }

   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            try {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toTeradataSelect(to_sqs, from_sqs));
            } catch (ConvertException var14) {
               throw var14;
            }
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (!SwisSQLOptions.convertOracleTruncToCastAsDate) {
         this.functionName.setColumnName("TRUNC");
         this.setFunctionArguments(arguments);
      } else {
         int k;
         DateClass dc;
         if (arguments.size() == 1) {
            boolean castAsDate = true;
            if (arguments.elementAt(0) instanceof SelectColumn && ((SelectColumn)arguments.elementAt(0)).getColumnExpression().size() > 1) {
               boolean arithemeticOperatorFound = false;
               k = 0;
               Vector argColExp = ((SelectColumn)arguments.elementAt(0)).getColumnExpression();
               int k = 0;

               while(true) {
                  if (k >= argColExp.size()) {
                     if (k > 1 && arithemeticOperatorFound) {
                        castAsDate = false;
                     }
                     break;
                  }

                  if (argColExp.elementAt(k) instanceof String) {
                     if (!argColExp.elementAt(k).toString().equalsIgnoreCase("+") && !argColExp.elementAt(k).toString().equalsIgnoreCase("-")) {
                        if (argColExp.elementAt(k).toString().equalsIgnoreCase("/")) {
                           arithemeticOperatorFound = true;
                        }
                     } else if (k > 0 && k < argColExp.size() - 1) {
                        Object nextObj = argColExp.get(k + 1);
                        if (nextObj instanceof String) {
                           boolean isNum = false;

                           try {
                              Integer.parseInt(nextObj.toString());
                              isNum = true;
                           } catch (NumberFormatException var13) {
                           }

                           if (isNum) {
                              argColExp.setElementAt("INTERVAL '" + nextObj + "' day", k + 1);
                           }
                        }
                     }
                  } else if (argColExp.elementAt(k) instanceof SelectColumn && ((SelectColumn)argColExp.elementAt(k)).getColumnExpression() != null && ((SelectColumn)argColExp.elementAt(k)).getColumnExpression().size() > 1) {
                     castAsDate = false;
                  } else {
                     ++k;
                  }

                  ++k;
               }
            }

            if (castAsDate) {
               this.functionName.setColumnName("CAST");
               dc = new DateClass();
               dc.setDatatypeName("DATE");
               this.setAsDatatype("AS");
               arguments.add(dc);
               this.setFunctionArguments(arguments);
            } else {
               this.functionName.setColumnName("TRUNC");
               this.setFunctionArguments(arguments);
            }
         } else if (arguments.size() == 2) {
            String arg2 = arguments.get(1).toString();
            if (arg2.startsWith("'")) {
               arg2 = arg2.substring(1, arg2.length() - 1);
            }

            if (!arg2.equalsIgnoreCase("ddd") && !arg2.equalsIgnoreCase("dd") && !arg2.equalsIgnoreCase("j")) {
               this.functionName.setColumnName("TRUNC");
               this.setFunctionArguments(arguments);
            } else {
               if (arguments.elementAt(0) instanceof SelectColumn && ((SelectColumn)arguments.elementAt(0)).getColumnExpression().size() > 1) {
                  Vector argColExp = ((SelectColumn)arguments.elementAt(0)).getColumnExpression();

                  for(k = 0; k < argColExp.size(); ++k) {
                     if (argColExp.elementAt(k) instanceof String && (argColExp.elementAt(k).toString().equalsIgnoreCase("+") || argColExp.elementAt(k).toString().equalsIgnoreCase("-")) && k > 0 && k < argColExp.size() - 1) {
                        Object nextObj = argColExp.get(k + 1);
                        if (nextObj instanceof String) {
                           boolean isNum = false;

                           try {
                              Integer.parseInt(nextObj.toString());
                              isNum = true;
                           } catch (NumberFormatException var12) {
                           }

                           if (isNum) {
                              argColExp.setElementAt("INTERVAL '" + nextObj + "' day", k + 1);
                           }
                        }
                     }
                  }
               }

               this.functionName.setColumnName("CAST");
               dc = new DateClass();
               dc.setDatatypeName("DATE");
               this.setAsDatatype("AS");
               arguments.setElementAt(dc, 1);
               this.setFunctionArguments(arguments);
            }
         } else {
            this.functionName.setColumnName("TRUNC");
            this.setFunctionArguments(arguments);
         }

      }
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionName.getColumnName().equalsIgnoreCase("SECOND")) {
         this.functionName.setColumnName("SECOND");
      } else if (this.functionName.getColumnName().trim().equalsIgnoreCase("DECIMAL")) {
         this.functionName.setColumnName("DECIMAL");
      } else {
         this.functionName.setColumnName("TRUNCATE");
      }

      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            if (this.functionName.getColumnName().equalsIgnoreCase("SECOND")) {
               this.handleStringLiteralForTime(from_sqs, i_count, true, true);
            }

            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionName.getColumnName().trim().equalsIgnoreCase("TRUNCATE")) {
         String qry = "";
         String precision = "5";
         if (arguments.elementAt(1) instanceof SelectColumn) {
            SelectColumn sc_precision = (SelectColumn)arguments.elementAt(1);
            Vector vc_precision = sc_precision.getColumnExpression();
            if (vc_precision.size() == 1 && vc_precision.elementAt(0) instanceof String) {
               precision = (String)vc_precision.elementAt(0);
               precision = precision.replaceAll("'", "");

               try {
                  Double.parseDouble(precision);
               } catch (Exception var9) {
                  precision = "5";
               }
            }
         }

         qry = "truncate(cast(" + arguments.get(0).toString() + " as decimal(32," + precision + "))," + arguments.get(1).toString() + ")";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }

   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (!this.functionName.getColumnName().equalsIgnoreCase("DECIMAL")) {
         Vector arguments = new Vector();

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               if (this.functionName.getColumnName().equalsIgnoreCase("SECOND")) {
                  this.handleStringLiteralForTime(from_sqs, i_count, true, true);
               }

               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toBigQuerySelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         if (this.functionName.getColumnName().equalsIgnoreCase("SECOND")) {
            String qry = "extract(SECOND from " + arguments.get(0).toString() + " )";
            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         } else if (this.functionName.getColumnName().equalsIgnoreCase("date_trunc")) {
            this.setFunctionArguments(arguments);
         } else {
            this.functionName.setColumnName("TRUNC");
            this.setFunctionArguments(arguments);
         }

      }
   }

   public void toAthena(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (!this.functionName.getColumnName().trim().equalsIgnoreCase("DECIMAL")) {
         Vector arguments = new Vector();

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toAthenaSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         this.setFunctionArguments(arguments);
         String qry = "";
         if (this.functionName.getColumnName().equalsIgnoreCase("TRUNCATE") || this.functionName.getColumnName().equalsIgnoreCase("TRUNC")) {
            if (from_sqs != null && from_sqs.isTrino()) {
               qry = "TRUNCATE(" + arguments.get(0).toString() + "*POWER(10," + arguments.get(1) + "))/POWER(10," + arguments.get(1) + ")";
            } else {
               qry = "CAST(SUBSTR(CAST(" + arguments.get(0).toString() + " AS VARCHAR), 1, INDEX(CAST(" + arguments.get(0).toString() + " AS VARCHAR), '.')+" + arguments.get(1).toString() + ") as DECIMAL(38," + arguments.get(1).toString() + "))";
            }

            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         }

      }
   }

   public void toSapHana(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (!this.functionName.getColumnName().trim().equalsIgnoreCase("DECIMAL")) {
         Vector arguments = new Vector();

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSapHanaSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         this.setFunctionArguments(arguments);
         String qry = "";
         if (this.functionName.getColumnName().equalsIgnoreCase("TRUNCATE") || this.functionName.getColumnName().equalsIgnoreCase("TRUNC")) {
            qry = arguments.size() == 1 ? "CAST((" + arguments.get(0).toString() + ") AS DECIMAL(38,0))" : "CAST((" + arguments.get(0).toString() + ") AS DECIMAL(38," + arguments.get(1).toString() + "))";
            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         }

      }
   }

   public void toSqlite(SelectQueryStatement from_sqs, SelectQueryStatement to_sqs) throws ConvertException {
      if (!this.functionName.getColumnName().trim().equalsIgnoreCase("DECIMAL")) {
         Vector arguments = new Vector();

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSqliteSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         this.setFunctionArguments(arguments);
         String qry = "";
         if (this.functionName.getColumnName().equalsIgnoreCase("TRUNCATE") || this.functionName.getColumnName().equalsIgnoreCase("TRUNC")) {
            qry = arguments.size() == 1 ? "TRUNC(" + arguments.get(0) + ")" : "CAST((CASE WHEN " + arguments.get(1) + " > 0 THEN CASE WHEN " + arguments.get(0) + " < 0 THEN -1.0*SUBSTR(" + arguments.get(0) + "*-1.0,1,INSTR(" + arguments.get(0) + "*-1.0,'.')+(" + arguments.get(1) + ")) ELSE SUBSTR(" + arguments.get(0) + "*1.0,1,INSTR(" + arguments.get(0) + "*1.0,'.')+(" + arguments.get(1) + ")) END WHEN " + arguments.get(1) + " = 0 THEN CASE WHEN " + arguments.get(0) + " < 0 THEN -1*SUBSTR(" + arguments.get(0) + "*1.0,1,INSTR(" + arguments.get(0) + "*1.0,'.')-(" + arguments.get(1) + ")) ELSE SUBSTR(" + arguments.get(0) + "*1.0,1,INSTR(" + arguments.get(0) + "*1.0,'.')-(" + arguments.get(1) + ")) END ELSE 0 END) AS REAL)";
            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         }

      }
   }

   public void toExcel(SelectQueryStatement from_sqs, SelectQueryStatement to_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toExcelSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (this.functionName.getColumnName().equalsIgnoreCase("TRUNCATE") || this.functionName.getColumnName().equalsIgnoreCase("TRUNC")) {
         if (arguments.size() > 1) {
            this.functionName.setColumnName("FIX(" + arguments.get(0) + "*(10^" + arguments.get(1) + "))/(10^" + arguments.get(1) + ")");
         } else {
            this.functionName.setColumnName("FIX(" + arguments.get(0) + ")");
         }

         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }

   }

   public void toMsAccess(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMsAccessJdbcSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }
}
