package com.adventnet.swissqlapi.sql.functions.math;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.util.database.MetadataInfoUtil;
import java.util.Vector;

public class floor extends FunctionCalls {
   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("FLOOR");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("FLOOR");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (!(this.functionArguments.elementAt(i_count) instanceof SelectColumn)) {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         } else {
            Vector colExpressions = ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression();
            boolean isFirstArgDateType = false;
            if (colExpressions != null) {
               for(int i = 0; i < colExpressions.size(); ++i) {
                  String fnName;
                  if (colExpressions.get(i) instanceof TableColumn) {
                     TableColumn tc = (TableColumn)colExpressions.get(i);
                     fnName = tc.getColumnName();
                     if (fnName != null && fnName.equalsIgnoreCase("SYSDATE")) {
                        isFirstArgDateType = true;
                        tc.setColumnName("(YEAR(GETDATE())*365 + MONTH(GETDATE())*31 + DAY(GETDATE()) + DATEPART(hh,GETDATE())/24.0 + DATEPART(mi,GETDATE())/1440.0 + DATEPART(ss,GETDATE())/86400.0)");
                     } else if (fnName != null) {
                        String dataType = MetadataInfoUtil.getDatatypeName(from_sqs, tc);
                        if (dataType != null && dataType.toLowerCase().indexOf("date") != -1 || isFirstArgDateType) {
                           String colName = tc.toString();
                           tc.setColumnName("(YEAR(" + colName + ")*365 + MONTH(" + colName + ")*31 + DAY(" + colName + ") + DATEPART(hh," + colName + ")/24.0 + DATEPART(mi," + colName + ")/1440.0 + DATEPART(ss," + colName + ")/86400.0)");
                           tc.setTableName((String)null);
                        }
                     }
                  } else if (colExpressions.get(i) instanceof FunctionCalls) {
                     FunctionCalls function = (FunctionCalls)colExpressions.get(i);
                     fnName = function.getFunctionName().getColumnName();
                     if (fnName.equalsIgnoreCase("TO_DATE")) {
                        isFirstArgDateType = true;
                        function = function.toMSSQLServerSelect(to_sqs, from_sqs);
                        TableColumn newTC = new TableColumn();
                        newTC.setColumnName("(YEAR(" + function.toString() + ")*365 + MONTH(" + function.toString() + ")*31 + DAY(" + function.toString() + ") + DATEPART(hh," + function.toString() + ")/24.0 + DATEPART(mi," + function.toString() + ")/1440.0 + DATEPART(ss," + function.toString() + ")/86400.0)");
                        colExpressions.setElementAt(newTC, i);
                     } else if (fnName.equalsIgnoreCase("TRUNC")) {
                        Vector fnArgs = function.getFunctionArguments();
                        if (fnArgs != null) {
                           Object obj = fnArgs.get(0);
                           if (obj instanceof SelectColumn) {
                              Vector scExpr = ((SelectColumn)obj).getColumnExpression();

                              for(int l = 0; l < scExpr.size(); ++l) {
                                 obj = scExpr.get(l);
                                 if (obj instanceof TableColumn) {
                                    TableColumn tc = (TableColumn)obj;
                                    String columnName = tc.getColumnName();
                                    if (columnName != null && columnName.equalsIgnoreCase("SYSDATE")) {
                                       isFirstArgDateType = true;
                                       function = function.toMSSQLServerSelect(to_sqs, from_sqs);
                                       TableColumn newTC = new TableColumn();
                                       newTC.setColumnName("(YEAR(" + function.toString() + ")*365 + MONTH(" + function.toString() + ")*31 + DAY(" + function.toString() + ") + DATEPART(hh," + function.toString() + ")/24.0 + DATEPART(mi," + function.toString() + ")/1440.0 + DATEPART(ss," + function.toString() + ")/86400.0)");
                                       colExpressions.setElementAt(newTC, i);
                                    } else if (columnName != null) {
                                       String dataType = MetadataInfoUtil.getDatatypeName(from_sqs, tc);
                                       if (dataType != null && dataType.toLowerCase().indexOf("date") != -1 || isFirstArgDateType) {
                                          function = function.toMSSQLServerSelect(to_sqs, from_sqs);
                                          TableColumn newTC = new TableColumn();
                                          newTC.setColumnName("(YEAR(" + function.toString() + ")*365 + MONTH(" + function.toString() + ")*31 + DAY(" + function.toString() + ") + DATEPART(hh," + function.toString() + ")/24.0 + DATEPART(mi," + function.toString() + ")/1440.0 + DATEPART(ss," + function.toString() + ")/86400.0)");
                                          colExpressions.setElementAt(newTC, i);
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
   }

   public void toSybase(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("FLOOR");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (!(this.functionArguments.elementAt(i_count) instanceof SelectColumn)) {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         } else {
            Vector colExpressions = ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression();
            boolean isFirstArgDateType = false;
            if (colExpressions != null) {
               for(int i = 0; i < colExpressions.size(); ++i) {
                  String fnName;
                  if (colExpressions.get(i) instanceof TableColumn) {
                     TableColumn tc = (TableColumn)colExpressions.get(i);
                     fnName = tc.getColumnName();
                     if (fnName != null && fnName.equalsIgnoreCase("SYSDATE")) {
                        isFirstArgDateType = true;
                        tc.setColumnName("(YEAR(GETDATE())*365 + MONTH(GETDATE())*31 + DAY(GETDATE()) + DATEPART(hh,GETDATE())/24.0 + DATEPART(mi,GETDATE())/1440.0 + DATEPART(ss,GETDATE())/86400.0)");
                     } else if (fnName != null) {
                        String dataType = MetadataInfoUtil.getDatatypeName(from_sqs, tc);
                        if (dataType != null && dataType.toLowerCase().indexOf("date") != -1 || isFirstArgDateType) {
                           String colName = tc.toString();
                           tc.setColumnName("(YEAR(" + colName + ")*365 + MONTH(" + colName + ")*31 + DAY(" + colName + ") + DATEPART(hh," + colName + ")/24.0 + DATEPART(mi," + colName + ")/1440.0 + DATEPART(ss," + colName + ")/86400.0)");
                           tc.setTableName((String)null);
                        }
                     }
                  } else if (colExpressions.get(i) instanceof FunctionCalls) {
                     FunctionCalls function = (FunctionCalls)colExpressions.get(i);
                     fnName = function.getFunctionName().getColumnName();
                     if (fnName.equalsIgnoreCase("TO_DATE")) {
                        isFirstArgDateType = true;
                        function = function.toSybaseSelect(to_sqs, from_sqs);
                        TableColumn newTC = new TableColumn();
                        newTC.setColumnName("(YEAR(" + function.toString() + ")*365 + MONTH(" + function.toString() + ")*31 + DAY(" + function.toString() + ") + DATEPART(hh," + function.toString() + ")/24.0 + DATEPART(mi," + function.toString() + ")/1440.0 + DATEPART(ss," + function.toString() + ")/86400.0)");
                        colExpressions.setElementAt(newTC, i);
                     } else if (fnName.equalsIgnoreCase("TRUNC")) {
                        Vector fnArgs = function.getFunctionArguments();
                        if (fnArgs != null) {
                           Object obj = fnArgs.get(0);
                           if (obj instanceof SelectColumn) {
                              Vector scExpr = ((SelectColumn)obj).getColumnExpression();

                              for(int l = 0; l < scExpr.size(); ++l) {
                                 obj = scExpr.get(l);
                                 if (obj instanceof TableColumn) {
                                    TableColumn tc = (TableColumn)obj;
                                    String columnName = tc.getColumnName();
                                    if (columnName != null && columnName.equalsIgnoreCase("SYSDATE")) {
                                       isFirstArgDateType = true;
                                       function = function.toSybaseSelect(to_sqs, from_sqs);
                                       TableColumn newTC = new TableColumn();
                                       newTC.setColumnName("(YEAR(" + function.toString() + ")*365 + MONTH(" + function.toString() + ")*31 + DAY(" + function.toString() + ") + DATEPART(hh," + function.toString() + ")/24.0 + DATEPART(mi," + function.toString() + ")/1440.0 + DATEPART(ss," + function.toString() + ")/86400.0)");
                                       colExpressions.setElementAt(newTC, i);
                                    } else if (columnName != null) {
                                       String dataType = MetadataInfoUtil.getDatatypeName(from_sqs, tc);
                                       if (dataType != null && dataType.toLowerCase().indexOf("date") != -1 || isFirstArgDateType) {
                                          function = function.toSybaseSelect(to_sqs, from_sqs);
                                          TableColumn newTC = new TableColumn();
                                          newTC.setColumnName("(YEAR(" + function.toString() + ")*365 + MONTH(" + function.toString() + ")*31 + DAY(" + function.toString() + ") + DATEPART(hh," + function.toString() + ")/24.0 + DATEPART(mi," + function.toString() + ")/1440.0 + DATEPART(ss," + function.toString() + ")/86400.0)");
                                          colExpressions.setElementAt(newTC, i);
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

            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSybaseSelect(to_sqs, from_sqs));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("FLOOR");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (!(this.functionArguments.elementAt(i_count) instanceof SelectColumn)) {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         } else {
            Vector colExpressions = ((SelectColumn)this.functionArguments.elementAt(i_count)).getColumnExpression();
            boolean isFirstArgDateType = false;
            if (colExpressions != null) {
               for(int i = 0; i < colExpressions.size(); ++i) {
                  String fnName;
                  if (colExpressions.get(i) instanceof TableColumn) {
                     TableColumn tc = (TableColumn)colExpressions.get(i);
                     fnName = tc.getColumnName();
                     if (fnName != null && fnName.equalsIgnoreCase("SYSDATE")) {
                        isFirstArgDateType = true;
                        tc.setColumnName("(YEAR(CURRENT TIMESTAMP)*365 + MONTH(CURRENT TIMESTAMP)*31 + DAY(CURRENT TIMESTAMP) + HOUR(CURRENT TIMESTAMP)/24.0 + MINUTE(CURRENT TIMESTAMP)/1440.0 + SECOND(CURRENT TIMESTAMP)/86400.0)");
                     } else if (fnName != null) {
                        String dataType = MetadataInfoUtil.getDatatypeName(from_sqs, tc);
                        if (dataType != null && dataType.toLowerCase().indexOf("date") != -1 || isFirstArgDateType) {
                           String colName = tc.toString();
                           tc.setColumnName("(YEAR(" + colName + ")*365 + MONTH(" + colName + ")*31 + DAY(" + colName + ") + HOUR(" + colName + ")/24.0 + MINUTE(" + colName + ")/1440.0 + SECOND(" + colName + ")/86400.0)");
                           tc.setTableName((String)null);
                        }
                     }
                  } else if (colExpressions.get(i) instanceof FunctionCalls) {
                     FunctionCalls function = (FunctionCalls)colExpressions.get(i);
                     fnName = function.getFunctionName().getColumnName();
                     if (fnName.equalsIgnoreCase("TO_DATE")) {
                        isFirstArgDateType = true;
                        function = function.toDB2Select(to_sqs, from_sqs);
                        TableColumn newTC = new TableColumn();
                        newTC.setColumnName("(YEAR(" + function.toString() + ")*365 + MONTH(" + function.toString() + ")*31 + DAY(" + function.toString() + ") + HOUR(" + function.toString() + ")/24.0 + MINUTE(" + function.toString() + ")/1440.0 + SECOND(" + function.toString() + ")/86400.0)");
                        colExpressions.setElementAt(newTC, i);
                     } else if (fnName.equalsIgnoreCase("TRUNC")) {
                        Vector fnArgs = function.getFunctionArguments();
                        if (fnArgs != null) {
                           Object obj = fnArgs.get(0);
                           if (obj instanceof SelectColumn) {
                              Vector scExpr = ((SelectColumn)obj).getColumnExpression();

                              for(int l = 0; l < scExpr.size(); ++l) {
                                 obj = scExpr.get(l);
                                 if (obj instanceof TableColumn) {
                                    TableColumn tc = (TableColumn)obj;
                                    String columnName = tc.getColumnName();
                                    if (columnName != null && columnName.equalsIgnoreCase("SYSDATE")) {
                                       isFirstArgDateType = true;
                                       function = function.toDB2Select(to_sqs, from_sqs);
                                       TableColumn newTC = new TableColumn();
                                       newTC.setColumnName("(YEAR(" + function.toString() + ")*365 + MONTH(" + function.toString() + ")*31 + DAY(" + function.toString() + ") + HOUR(" + function.toString() + ")/24.0 + MINUTE(" + function.toString() + ")/1440.0 + SECOND(" + function.toString() + ")/86400.0)");
                                       colExpressions.setElementAt(newTC, i);
                                    } else if (columnName != null) {
                                       String dataType = MetadataInfoUtil.getDatatypeName(from_sqs, tc);
                                       if (dataType != null && dataType.toLowerCase().indexOf("date") != -1 || isFirstArgDateType) {
                                          function = function.toDB2Select(to_sqs, from_sqs);
                                          TableColumn newTC = new TableColumn();
                                          newTC.setColumnName("(YEAR(" + function.toString() + ")*365 + MONTH(" + function.toString() + ")*31 + DAY(" + function.toString() + ") + HOUR(" + function.toString() + ")/24.0 + MINUTE(" + function.toString() + ")/1440.0 + SECOND(" + function.toString() + ")/86400.0)");
                                          colExpressions.setElementAt(newTC, i);
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

            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toDB2Select(to_sqs, from_sqs));
         }
      }

      this.functionName.setColumnName("CAST");
      this.setAsDatatype("AS");
      arguments.add("INTEGER");
      this.setFunctionArguments(arguments);
   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("FLOOR");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (from_sqs != null && from_sqs.isMySqlLive()) {
         this.functionName.setColumnName("FLOOR");
         this.setFunctionArguments(arguments);
      } else {
         this.functionName.setColumnName("");
         Vector fnArgs = new Vector(1);
         SelectColumn scFloorInDec = new SelectColumn();
         Vector vcFloorInDec = new Vector(2);
         SelectColumn scFloor = new SelectColumn();
         Vector colExpr = new Vector(1);
         FunctionCalls fnCl = new FunctionCalls();
         TableColumn tbCl = new TableColumn();
         tbCl.setColumnName("FLOOR");
         fnCl.setFunctionName(tbCl);
         fnCl.setFunctionArguments(arguments);
         colExpr.addElement(fnCl);
         scFloor.setColumnExpression(colExpr);
         vcFloorInDec.addElement(scFloor);
         vcFloorInDec.addElement("* 1.0");
         scFloorInDec.setColumnExpression(vcFloorInDec);
         fnArgs.addElement(scFloorInDec);
         this.setFunctionArguments(fnArgs);
      }

   }

   public void toANSISQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("FLOOR");
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

   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("FLOOR");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toTeradataSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toInformixSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.functionName.setColumnName("CAST");
      this.setAsDatatype("AS");
      arguments.add("INT");
      this.setFunctionArguments(arguments);
   }

   public void toTimesTen(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("FLOOR");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.functionArguments.elementAt(i_count);
            Vector colExpr = sc.getColumnExpression();
            if (colExpr.size() != 1 || !(colExpr.get(0) instanceof String)) {
               throw new ConvertException("\nThe function FLOOR is not supported in TimesTen 5.1.21\n");
            }

            this.functionName.setColumnName("");
            this.setOpenBracesForFunctionNameRequired(false);
            arguments.add(Math.floor(Double.parseDouble(colExpr.get(0).toString())) + "");
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("FLOOR");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toNetezzaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("FLOOR");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("FLOOR");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toBigQuerySelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toSapHana(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("FLOOR");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSapHanaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (arguments.size() == 1) {
         String qry = "CAST(FLOOR(" + arguments.get(0).toString() + ") AS INTEGER)";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else {
         this.setFunctionArguments(arguments);
      }

   }

   public void toSqlite(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("FLOOR");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSqliteSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (arguments.size() == 1) {
         String qry = "CAST(FLOOR(" + arguments.get(0).toString() + ") AS INTEGER)";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else {
         this.setFunctionArguments(arguments);
      }

   }

   public void toAthena(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("FLOOR");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toAthenaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (arguments.size() == 1) {
         String qry = "CAST(FLOOR(" + arguments.get(0).toString() + ") AS BIGINT)";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else {
         this.setFunctionArguments(arguments);
      }

   }

   public void toExcel(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toExcelSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (this.functionName.getColumnName().equalsIgnoreCase("FLOOR")) {
         if (arguments.size() == 1) {
            String qry = "Int(" + arguments.get(0).toString() + ")";
            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         } else {
            this.setFunctionArguments(arguments);
         }
      }

   }

   public void toMsAccess(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("FLOOR");
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
