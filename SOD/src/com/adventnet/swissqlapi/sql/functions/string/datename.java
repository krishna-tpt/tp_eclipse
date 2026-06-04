package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import java.util.Vector;

public class datename extends FunctionCalls {
   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("TO_CHAR");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (arguments.size() == 2) {
         Vector newArguments = new Vector();
         newArguments.add(arguments.get(1));
         if (!(arguments.get(0) instanceof SelectColumn)) {
            newArguments.add("' || " + arguments.get(0) + " || '");
         } else if (((SelectColumn)arguments.get(0)).getColumnExpression() != null && ((SelectColumn)arguments.get(0)).getColumnExpression().size() > 0) {
            if (((SelectColumn)arguments.get(0)).getColumnExpression().get(0) instanceof TableColumn) {
               TableColumn tc = (TableColumn)((SelectColumn)arguments.get(0)).getColumnExpression().get(0);
               if (tc.getTableName() == null && tc.getColumnName() != null) {
                  boolean ms = false;
                  if (!tc.getColumnName().equalsIgnoreCase("MONTH") && !tc.getColumnName().equalsIgnoreCase("MM") && !tc.getColumnName().equalsIgnoreCase("M")) {
                     if (!tc.getColumnName().equalsIgnoreCase("YY") && !tc.getColumnName().equalsIgnoreCase("YYYY") && !tc.getColumnName().equalsIgnoreCase("YEAR")) {
                        if (!tc.getColumnName().equalsIgnoreCase("DAYOFYEAR") && !tc.getColumnName().equalsIgnoreCase("DY") && !tc.getColumnName().equalsIgnoreCase("Y")) {
                           if (!tc.getColumnName().equalsIgnoreCase("DAY") && !tc.getColumnName().equalsIgnoreCase("DD") && !tc.getColumnName().equalsIgnoreCase("D")) {
                              if (!tc.getColumnName().equalsIgnoreCase("WEEK") && !tc.getColumnName().equalsIgnoreCase("WK") && !tc.getColumnName().equalsIgnoreCase("W") && !tc.getColumnName().equalsIgnoreCase("WW")) {
                                 if (!tc.getColumnName().equalsIgnoreCase("QUARTER") && !tc.getColumnName().equalsIgnoreCase("QQ") && !tc.getColumnName().equalsIgnoreCase("Q")) {
                                    if (!tc.getColumnName().equalsIgnoreCase("WEEKDAY") && !tc.getColumnName().equalsIgnoreCase("DW")) {
                                       if (!tc.getColumnName().equalsIgnoreCase("HOUR") && !tc.getColumnName().equalsIgnoreCase("HH")) {
                                          if (!tc.getColumnName().equalsIgnoreCase("MINUTE") && !tc.getColumnName().equalsIgnoreCase("MI") && !tc.getColumnName().equalsIgnoreCase("N")) {
                                             if (!tc.getColumnName().equalsIgnoreCase("SECOND") && !tc.getColumnName().equalsIgnoreCase("SS") && !tc.getColumnName().equalsIgnoreCase("S")) {
                                                if (!tc.getColumnName().equalsIgnoreCase("MILLISECOND") && !tc.getColumnName().equalsIgnoreCase("MS")) {
                                                   newArguments.add("' || " + arguments.get(0) + " || '");
                                                } else {
                                                   newArguments.add("'FF'");
                                                   ms = true;
                                                }
                                             } else {
                                                newArguments.add("'SS'");
                                             }
                                          } else {
                                             newArguments.add("'MI'");
                                          }
                                       } else {
                                          newArguments.add("'HH24'");
                                       }
                                    } else {
                                       newArguments.add("'DAY'");
                                    }
                                 } else {
                                    newArguments.add("'Q'");
                                 }
                              } else {
                                 newArguments.add("'WW'");
                              }
                           } else {
                              newArguments.add("'DD'");
                           }
                        } else {
                           newArguments.add("'DDD'");
                        }
                     } else {
                        newArguments.add("'YYYY'");
                     }
                  } else {
                     newArguments.add("'MONTH'");
                  }

                  if (arguments.get(1).toString().trim().startsWith("'")) {
                     Object obj = arguments.get(1);
                     String format = SwisSQLUtils.getDateFormat(obj.toString().trim(), 1);
                     SelectColumn newSC = new SelectColumn();
                     Vector colExpr = new Vector();
                     FunctionCalls fc = new FunctionCalls();
                     TableColumn fctc = new TableColumn();
                     if (ms) {
                        fctc.setColumnName("TO_TIMESTAMP");
                     } else {
                        fctc.setColumnName("TO_DATE");
                     }

                     Vector fnArgs = new Vector();
                     fnArgs.add(obj);
                     fc.setFunctionArguments(fnArgs);
                     fc.setFunctionName(fctc);
                     colExpr.add(fc);
                     newSC.setColumnExpression(colExpr);
                     newArguments.setElementAt(newSC, 0);
                     if (format != null) {
                        if (format.startsWith("'1900")) {
                           fnArgs.setElementAt(format, 0);
                           fnArgs.add("'YYYY-MM-DD HH24:MI:SS'");
                        } else {
                           fnArgs.add(format);
                        }
                     }
                  }
               }
            }
         } else {
            newArguments.add("' || " + arguments.get(0) + " || '");
         }

         this.setFunctionArguments(newArguments);
      }

   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("DATENAME");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toSybase(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("DATENAME");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSybaseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("DATENAME");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toDB2Select(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (arguments.size() == 2) {
         String arg1 = arguments.get(0).toString();
         arguments.remove(0);
         FunctionCalls innerFunction = new FunctionCalls();
         TableColumn innerFn = new TableColumn();
         if (!arg1.equalsIgnoreCase("yy") && !arg1.equalsIgnoreCase("year")) {
            if (!arg1.equalsIgnoreCase("quarter") && !arg1.equalsIgnoreCase("qq")) {
               if (!arg1.equalsIgnoreCase("dd") && !arg1.equalsIgnoreCase("day")) {
                  if (!arg1.equalsIgnoreCase("month") && !arg1.equalsIgnoreCase("mm")) {
                     if (!arg1.equalsIgnoreCase("week") && !arg1.equalsIgnoreCase("wk")) {
                        if (!arg1.equalsIgnoreCase("dayofyear") && !arg1.equalsIgnoreCase("dy")) {
                           if (!arg1.equalsIgnoreCase("weekday") && !arg1.equalsIgnoreCase("dw")) {
                              if (!arg1.equalsIgnoreCase("hour") && !arg1.equalsIgnoreCase("hh")) {
                                 if (!arg1.equalsIgnoreCase("minute") && !arg1.equalsIgnoreCase("mi")) {
                                    if (!arg1.equalsIgnoreCase("second") && !arg1.equalsIgnoreCase("ss")) {
                                       innerFn.setColumnName("");
                                    } else {
                                       innerFn.setColumnName("SECOND");
                                    }
                                 } else {
                                    innerFn.setColumnName("MINUTE");
                                 }
                              } else {
                                 innerFn.setColumnName("HOUR");
                              }
                           } else {
                              this.functionName.setColumnName("DAYNAME");
                           }
                        } else {
                           innerFn.setColumnName("DAYOFYEAR");
                        }
                     } else {
                        innerFn.setColumnName("WEEK");
                     }
                  } else {
                     this.functionName.setColumnName("MONTHNAME");
                  }
               } else {
                  innerFn.setColumnName("DAY");
               }
            } else {
               innerFn.setColumnName("QUARTER");
            }
         } else {
            innerFn.setColumnName("YEAR");
         }

         if (!arg1.equalsIgnoreCase("month") && !arg1.equalsIgnoreCase("mm") && !arg1.equalsIgnoreCase("weekday") && !arg1.equalsIgnoreCase("dw")) {
            innerFunction.setFunctionArguments(arguments);
            innerFunction.setFunctionName(innerFn);
            SelectColumn sc = new SelectColumn();
            Vector colExpr = new Vector();
            colExpr.add(innerFunction);
            sc.setColumnExpression(colExpr);
            Vector outerArg = new Vector();
            outerArg.add(sc);
            this.setFunctionArguments(outerArg);
            this.functionName.setColumnName("CHAR");
         }
      }

   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("DATENAME");
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
      this.functionName.setColumnName("DATENAME");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toANSISQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("DATENAME");
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
      this.functionName.setColumnName("DATENAME");
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
      this.functionName.setColumnName("DATENAME");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toInformixSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toTimesTen(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("TO_CHAR");
      Vector arguments = new Vector();
      if (this.functionArguments.size() == 2) {
         if (this.functionArguments.get(1) instanceof SelectColumn) {
            arguments.add(((SelectColumn)this.functionArguments.get(1)).toTimesTenSelect(to_sqs, from_sqs));
         } else {
            arguments.add(this.functionArguments.get(1));
         }

         SelectColumn sc = (SelectColumn)this.functionArguments.get(0);
         String datePart = sc.getColumnExpression().get(0).toString();
         if (!datePart.equalsIgnoreCase("year") && !datePart.equalsIgnoreCase("yy")) {
            if (!datePart.equalsIgnoreCase("quarter") && !datePart.equalsIgnoreCase("qq")) {
               if (!datePart.equalsIgnoreCase("month") && !datePart.equalsIgnoreCase("mm")) {
                  if (!datePart.equalsIgnoreCase("day") && !datePart.equalsIgnoreCase("dd")) {
                     if (!datePart.equalsIgnoreCase("hour") && !datePart.equalsIgnoreCase("hh")) {
                        if (!datePart.equalsIgnoreCase("minute") && !datePart.equalsIgnoreCase("mi")) {
                           if (datePart.equalsIgnoreCase("second") || datePart.equalsIgnoreCase("ss")) {
                              arguments.add("'SS'");
                           }
                        } else {
                           arguments.add("'MI'");
                        }
                     } else {
                        arguments.add("'HH24'");
                     }
                  } else {
                     arguments.add("'DD'");
                  }
               } else {
                  arguments.add("'MONTH'");
               }
            } else {
               arguments.add("'Q'");
            }
         } else {
            arguments.add("'YYYY'");
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("DATENAME");
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
      this.functionName.setColumnName("DATE_FORMAT");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      arguments.addElement("'%W'");
      this.setFunctionArguments(arguments);
   }
}
