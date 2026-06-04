package com.adventnet.swissqlapi.sql.functions.misc;

import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.create.BinClass;
import com.adventnet.swissqlapi.sql.statement.create.CharacterClass;
import com.adventnet.swissqlapi.sql.statement.create.Datatype;
import com.adventnet.swissqlapi.sql.statement.create.NumericClass;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.util.database.MetadataInfoUtil;
import java.util.Vector;

public class convert extends FunctionCalls {
   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("CAST");
      this.setAsDatatype("AS");
      Vector arguments = new Vector();
      TableColumn tableColumn = null;
      String columnDataType = null;
      Boolean isdenodo = from_sqs != null && from_sqs.isDenodo();
      boolean isDate = false;
      boolean isChar = false;
      boolean isCharVarch = false;
      boolean isBinary = false;
      Vector dummyArgs;
      String size;
      TableColumn tc;
      Datatype dt;
      SelectColumn scTemp;
      TableColumn tc;
      Vector newFunctionArgument;
      String dataType;
      Object columOjb;
      String innerFunctionName;
      if (this.functionArguments.size() == 2) {
         Object argObj = this.functionArguments.get(1);
         if (argObj instanceof SelectColumn) {
            SelectColumn selCol = (SelectColumn)argObj;
            dummyArgs = selCol.getColumnExpression();
            if (dummyArgs.size() == 1) {
               Object obj = dummyArgs.get(0);
               if (obj instanceof TableColumn) {
                  tc = (TableColumn)obj;
                  dataType = tc.getColumnName();
                  size = null;
                  if (from_sqs != null && from_sqs.getFromClause() != null) {
                     size = MetadataInfoUtil.getDatatypeName(from_sqs, tc);
                  }

                  if (size != null && size.toLowerCase().indexOf("date") != -1) {
                     isDate = true;
                  } else if (size == null && SwisSQLAPI.variableDatatypeMapping != null && (from_sqs != null && from_sqs.getFromClause() == null || from_sqs == null) && SwisSQLAPI.variableDatatypeMapping.containsKey(dataType)) {
                     innerFunctionName = (String)SwisSQLAPI.variableDatatypeMapping.get(dataType);
                     if (innerFunctionName.toLowerCase().indexOf("date") != -1) {
                        isDate = true;
                     } else if (!innerFunctionName.toLowerCase().startsWith("char") && !innerFunctionName.toLowerCase().startsWith("nchar")) {
                        if (innerFunctionName.toLowerCase().startsWith("binary") || innerFunctionName.toLowerCase().startsWith("varbinary")) {
                           isBinary = true;
                        }
                     } else {
                        isChar = true;
                     }

                     if (innerFunctionName.toLowerCase().startsWith("char") || innerFunctionName.toLowerCase().startsWith("varchar")) {
                        isCharVarch = true;
                     }
                  }

                  if (size != null && (size.toLowerCase().startsWith("char") || size.toLowerCase().startsWith("nchar"))) {
                     isChar = true;
                  }

                  if (size != null && (size.toLowerCase().startsWith("char") || size.toLowerCase().startsWith("varchar"))) {
                     isCharVarch = true;
                  }

                  if (size != null && (size.toLowerCase().startsWith("binary") || size.toLowerCase().startsWith("varbinary"))) {
                     isBinary = true;
                  }
               } else if (obj instanceof FunctionCalls) {
                  FunctionCalls fc = (FunctionCalls)obj;
                  TableColumn fnTc = fc.getFunctionName();
                  if (fnTc != null) {
                     size = fnTc.getColumnName();
                     if (size.equalsIgnoreCase("getdate")) {
                        isDate = true;
                     }
                  }
               } else if (obj instanceof SelectColumn) {
                  scTemp = (SelectColumn)obj;
                  newFunctionArgument = scTemp.getColumnExpression();
                  if (newFunctionArgument.size() == 1) {
                     columOjb = newFunctionArgument.get(0);
                     if (columOjb instanceof TableColumn) {
                        tc = (TableColumn)columOjb;
                        if (tc.getColumnName().equalsIgnoreCase("sysdate")) {
                           isDate = true;
                        }
                     }
                  }
               }
            }
         }

         if (isDate) {
            Object obj = this.functionArguments.get(0);
            if (obj instanceof Datatype) {
               dt = (Datatype)obj;
               if (dt instanceof NumericClass) {
                  if (argObj instanceof SelectColumn) {
                     argObj = ((SelectColumn)argObj).toOracleSelect(to_sqs, from_sqs);
                  }

                  this.functionName.setColumnName("");
                  this.setOpenBracesForFunctionNameRequired(false);
                  this.setAsDatatype((String)null);
                  arguments.add(argObj + " - TO_DATE('01-JAN-1900')");
                  this.setFunctionArguments(arguments);
                  return;
               }
            }
         }
      }

      boolean targetTypeBinOrNum = false;
      boolean targetTypeNCharacter = false;

      Vector tempArgs;
      Vector dummyArgs;
      boolean isSetExpr;
      FunctionCalls tempFC;
      String size;
      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn scl = (SelectColumn)this.functionArguments.elementAt(i_count);
            dummyArgs = scl.getColumnExpression();
            if (dummyArgs != null) {
               for(int jj = 0; jj < dummyArgs.size(); ++jj) {
                  columOjb = dummyArgs.get(jj);
                  if (columOjb instanceof TableColumn) {
                     tableColumn = (TableColumn)columOjb;
                  }
               }
            }

            if (this.functionArguments.size() == 2 && i_count == 1 && this.functionArguments.get(1).toString().toLowerCase().startsWith("char")) {
               arguments.add(this.functionArguments.get(1));
            } else if (this.functionArguments.size() == 2 && i_count == 1 && this.functionArguments.get(1).toString().toLowerCase().startsWith("decimal")) {
               arguments.addElement(this.functionArguments.get(1));
            } else if (isdenodo && this.functionArguments.size() == 2 && i_count == 1 && this.functionArguments.get(1).toString().toLowerCase().startsWith("signed")) {
               arguments.addElement(this.functionArguments.get(1));
            } else {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
            }

            if (isChar && targetTypeBinOrNum) {
               Object obj = arguments.lastElement();
               tempFC = new FunctionCalls();
               tc = new TableColumn();
               tc.setColumnName("TRIM");
               tempFC.setFunctionName(tc);
               tempArgs = new Vector();
               tempArgs.add(obj);
               tempFC.setFunctionArguments(tempArgs);
               arguments.setElementAt(tempFC, arguments.size() - 1);
            } else {
               if (isCharVarch && targetTypeNCharacter) {
                  this.functionName.setColumnName("");
                  this.setAsDatatype((String)null);
                  this.setOpenBracesForFunctionNameRequired(false);
                  arguments.remove(0);
                  this.setFunctionArguments(arguments);
                  return;
               }

               if (this.functionArguments.size() == 2 && i_count == 1 && (arguments.get(1).toString().toLowerCase().startsWith("signed") || arguments.get(1).toString().toLowerCase().startsWith("decimal"))) {
                  if (arguments.get(1).toString().toLowerCase().startsWith("signed")) {
                     dataType = isdenodo ? "INT8" : "int";
                     arguments.set(1, dataType);
                  } else if (arguments.get(1).toString().equalsIgnoreCase("decimal_")) {
                     arguments.set(1, "decimal");
                  }

                  this.setFunctionArguments(arguments);
                  return;
               }

               if (this.functionArguments.size() == 2 && i_count == 1 && arguments.get(1).toString().toLowerCase().startsWith("char")) {
                  if (arguments.get(1).toString().equalsIgnoreCase("char")) {
                     this.functionName.setColumnName("TO_CHAR");
                     this.setAsDatatype((String)null);
                     arguments.removeElementAt(1);
                  }

                  this.setFunctionArguments(arguments);
                  return;
               }
            }
         } else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
            Datatype datatype = (Datatype)this.functionArguments.elementAt(i_count);
            isSetExpr = false;
            if (datatype instanceof BinClass) {
               BinClass bc = (BinClass)datatype;
               size = bc.getDatatypeName();
               if ((size.equalsIgnoreCase("binary") || size.equalsIgnoreCase("varbinary")) && bc.getSize() == null) {
                  isSetExpr = true;
               }
            }

            if (isdenodo) {
               datatype.toDenodoString();
            } else {
               datatype.toOracleString();
            }

            if (SwisSQLOptions.fromSQLServer) {
               boolean isSetExpr = false;
               if (from_sqs != null && from_sqs.getSelectStatement().getSelectItemList() != null && from_sqs.getSelectStatement().getSelectItemList().size() == 1 && from_sqs.getSelectStatement().getSelectItemList().get(0) instanceof SelectColumn && from_sqs.getFromClause() == null && (((SelectColumn)from_sqs.getSelectStatement().getSelectItemList().get(0)).getColumnExpression().size() < 3 || !((SelectColumn)from_sqs.getSelectStatement().getSelectItemList().get(0)).getColumnExpression().get(1).toString().equals("="))) {
                  isSetExpr = true;
               }

               if (datatype instanceof BinClass) {
                  BinClass bc = (BinClass)datatype;
                  innerFunctionName = bc.getDatatypeName();
                  size = bc.getSize();
                  if (innerFunctionName.equalsIgnoreCase("raw")) {
                     if (isSetExpr && !isSetExpr) {
                        bc.setSize("30");
                        bc.setOpenBrace("(");
                        bc.setClosedBrace(")");
                     } else if (isSetExpr) {
                        bc.setSize((String)null);
                        bc.setOpenBrace((String)null);
                        bc.setClosedBrace((String)null);
                     }
                  }
               } else if (datatype instanceof CharacterClass) {
                  CharacterClass cc = (CharacterClass)datatype;
                  innerFunctionName = cc.getDatatypeName();
                  size = cc.getSize();
                  if (innerFunctionName.equalsIgnoreCase("nchar") || innerFunctionName.equalsIgnoreCase("nvarchar2")) {
                     if (size == null && !isSetExpr) {
                        cc.setSize("30");
                        cc.setOpenBrace("(");
                        cc.setClosedBrace(")");
                     } else if (isSetExpr) {
                        cc.setSize((String)null);
                        cc.setOpenBrace((String)null);
                        cc.setClosedBrace((String)null);
                     }
                  }
               }
            }

            if (!(datatype instanceof BinClass) && !(datatype instanceof NumericClass)) {
               if (datatype instanceof CharacterClass) {
                  dataType = datatype.getDatatypeName();
                  if (dataType.equalsIgnoreCase("nchar") || dataType.equalsIgnoreCase("nvarchar2")) {
                     targetTypeNCharacter = true;
                  }
               }
            } else {
               targetTypeBinOrNum = true;
            }

            arguments.addElement(datatype);
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      Vector newFunctionArgument;
      if (this.functionArguments.get(0) instanceof Datatype && (this.functionArguments.size() != 2 || this.functionArguments.get(1).toString().indexOf("'") == -1 || !((Datatype)this.functionArguments.get(0)).toString().trim().toUpperCase().startsWith("CHAR") && !((Datatype)this.functionArguments.get(0)).toString().trim().toUpperCase().startsWith("VARCHAR"))) {
         dt = (Datatype)this.functionArguments.get(0);
         int i;
         SelectColumn newSC;
         Vector newColExp;
         if (!dt.toString().trim().toUpperCase().startsWith("CHAR") && !dt.toString().trim().toUpperCase().startsWith("VARCHAR")) {
            if (!dt.toString().trim().equalsIgnoreCase("DATE") && !dt.toString().trim().equalsIgnoreCase("DATETIME")) {
               if (!dt.toString().toUpperCase().trim().startsWith("DECIMAL") && !dt.toString().toUpperCase().trim().startsWith("NUMERIC") && !dt.toString().toUpperCase().trim().startsWith("NUMBER")) {
                  newFunctionArgument = new Vector();
                  newFunctionArgument.add(0, arguments.get(1));
                  newFunctionArgument.add(1, arguments.get(0));
                  this.setFunctionArguments(newFunctionArgument);
               } else {
                  this.functionName.setColumnName("TO_NUMBER");
                  newFunctionArgument = new Vector();
                  this.asDatatype = null;
                  newFunctionArgument.add(arguments.get(1));
                  this.setFunctionArguments(newFunctionArgument);
               }
            } else {
               newFunctionArgument = new Vector();
               this.functionName.setColumnName("TO_DATE");
               this.asDatatype = null;
               newFunctionArgument.add(arguments.get(1));
               if (this.functionArguments.size() > 2) {
                  if (this.functionArguments.get(2) instanceof SelectColumn) {
                     scTemp = (SelectColumn)this.functionArguments.get(2);
                     newFunctionArgument = scTemp.getColumnExpression();
                     if (newFunctionArgument != null && newFunctionArgument.size() == 1) {
                        if (newFunctionArgument.get(0) instanceof String) {
                           size = (String)newFunctionArgument.get(0);

                           try {
                              i = Integer.parseInt(size);
                              if (i == 103) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'DD/MM/YYYY'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 102) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'YYYY.MM.DD'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i != 121 && i != 120) {
                                 if (i == 111) {
                                    newSC = new SelectColumn();
                                    newColExp = new Vector();
                                    newColExp.add("'YYYY/MM/DD'");
                                    newSC.setColumnExpression(newColExp);
                                    newFunctionArgument.add(newSC);
                                 } else if (i == 101) {
                                    newSC = new SelectColumn();
                                    newColExp = new Vector();
                                    newColExp.add("'MM DD YYYY'");
                                    newSC.setColumnExpression(newColExp);
                                    newFunctionArgument.add(newSC);
                                 } else if (i == 104) {
                                    newSC = new SelectColumn();
                                    newColExp = new Vector();
                                    newColExp.add("'DD.MM.YYYY'");
                                    newSC.setColumnExpression(newColExp);
                                    newFunctionArgument.add(newSC);
                                 } else if (i == 105) {
                                    newSC = new SelectColumn();
                                    newColExp = new Vector();
                                    newColExp.add("'DD-MM-YYYY'");
                                    newSC.setColumnExpression(newColExp);
                                    newFunctionArgument.add(newSC);
                                 } else if (i == 106) {
                                    newSC = new SelectColumn();
                                    newColExp = new Vector();
                                    newColExp.add("'DD MON YYYY'");
                                    newSC.setColumnExpression(newColExp);
                                    newFunctionArgument.add(newSC);
                                 } else if (i == 107) {
                                    newSC = new SelectColumn();
                                    newColExp = new Vector();
                                    newColExp.add("'MON DD, YYYY'");
                                    newSC.setColumnExpression(newColExp);
                                    newFunctionArgument.add(newSC);
                                 } else if (i != 108 && i != 8) {
                                    if (i == 110) {
                                       newSC = new SelectColumn();
                                       newColExp = new Vector();
                                       newColExp.add("'MM-DD-YYYY'");
                                       newSC.setColumnExpression(newColExp);
                                       newFunctionArgument.add(newSC);
                                    } else if (i == 112) {
                                       newSC = new SelectColumn();
                                       newColExp = new Vector();
                                       newColExp.add("'YYYYMMDD'");
                                       newSC.setColumnExpression(newColExp);
                                       newFunctionArgument.add(newSC);
                                    } else if (i == 113) {
                                       newSC = new SelectColumn();
                                       newColExp = new Vector();
                                       newColExp.add("'DD MON YYYY'");
                                       newSC.setColumnExpression(newColExp);
                                       newFunctionArgument.add(newSC);
                                    } else if (i != 14 && i != 114) {
                                       if (i != 120 && i != 121) {
                                          if (i == 1) {
                                             newSC = new SelectColumn();
                                             newColExp = new Vector();
                                             newColExp.add("'MM/DD/YY'");
                                             newSC.setColumnExpression(newColExp);
                                             newFunctionArgument.add(newSC);
                                          } else if (i == 2) {
                                             newSC = new SelectColumn();
                                             newColExp = new Vector();
                                             newColExp.add("'YYY/MM/DD'");
                                             newSC.setColumnExpression(newColExp);
                                             newFunctionArgument.add(newSC);
                                          } else if (i == 3) {
                                             newSC = new SelectColumn();
                                             newColExp = new Vector();
                                             newColExp.add("'DD/MM/YY'");
                                             newSC.setColumnExpression(newColExp);
                                             newFunctionArgument.add(newSC);
                                          } else if (i == 4) {
                                             newSC = new SelectColumn();
                                             newColExp = new Vector();
                                             newColExp.add("'DD.MM.YY'");
                                             newSC.setColumnExpression(newColExp);
                                             newFunctionArgument.add(newSC);
                                          } else if (i == 5) {
                                             newSC = new SelectColumn();
                                             newColExp = new Vector();
                                             newColExp.add("'DD-MM-YY'");
                                             newSC.setColumnExpression(newColExp);
                                             newFunctionArgument.add(newSC);
                                          } else if (i == 6) {
                                             newSC = new SelectColumn();
                                             newColExp = new Vector();
                                             newColExp.add("'DD-MON YY'");
                                             newSC.setColumnExpression(newColExp);
                                             newFunctionArgument.add(newSC);
                                          } else if (i == 7) {
                                             newSC = new SelectColumn();
                                             newColExp = new Vector();
                                             newColExp.add("'MON DD, YY'");
                                             newSC.setColumnExpression(newColExp);
                                             newFunctionArgument.add(newSC);
                                          } else if (i != 9 && i != 109) {
                                             if (i == 10) {
                                                newSC = new SelectColumn();
                                                newColExp = new Vector();
                                                newColExp.add("'MM-DD-YY'");
                                                newSC.setColumnExpression(newColExp);
                                                newFunctionArgument.add(newSC);
                                             } else if (i == 11) {
                                                newSC = new SelectColumn();
                                                newColExp = new Vector();
                                                newColExp.add("'YY/MM/DD'");
                                                newSC.setColumnExpression(newColExp);
                                                newFunctionArgument.add(newSC);
                                             } else if (i == 12) {
                                                newSC = new SelectColumn();
                                                newColExp = new Vector();
                                                newColExp.add("'YYMMDD'");
                                                newSC.setColumnExpression(newColExp);
                                                newFunctionArgument.add(newSC);
                                             } else if (i == 13) {
                                                newSC = new SelectColumn();
                                                newColExp = new Vector();
                                                newColExp.add("'DD MON YYYY'");
                                                newSC.setColumnExpression(newColExp);
                                                newFunctionArgument.add(newSC);
                                             } else if (i != 0 && i != 100) {
                                                if (i == 20) {
                                                   newSC = new SelectColumn();
                                                   newColExp = new Vector();
                                                   newColExp.add("'YYYY MM DD HH24:MI:SS'");
                                                   newSC.setColumnExpression(newColExp);
                                                   newFunctionArgument.add(newSC);
                                                } else if (i == 21) {
                                                   newSC = new SelectColumn();
                                                   newColExp = new Vector();
                                                   newColExp.add("'YYYY MM DD HH24:MI:SS.FF3'");
                                                   newSC.setColumnExpression(newColExp);
                                                   newFunctionArgument.add(newSC);
                                                } else {
                                                   newFunctionArgument.add(this.functionArguments.get(2));
                                                }
                                             } else {
                                                newSC = new SelectColumn();
                                                newColExp = new Vector();
                                                newColExp.add("'Mon DD YYYY HH:MIAM'");
                                                newSC.setColumnExpression(newColExp);
                                                newFunctionArgument.add(newSC);
                                             }
                                          } else {
                                             newSC = new SelectColumn();
                                             newColExp = new Vector();
                                             newColExp.add("'Mon DD YYYY HH:MI:SSAM'");
                                             newSC.setColumnExpression(newColExp);
                                             newFunctionArgument.add(newSC);
                                          }
                                       } else {
                                          newSC = new SelectColumn();
                                          newColExp = new Vector();
                                          newColExp.add("'YYYY-MM-DD HH24:MI:SS'");
                                          newSC.setColumnExpression(newColExp);
                                          newFunctionArgument.add(newSC);
                                       }
                                    } else {
                                       newSC = new SelectColumn();
                                       newColExp = new Vector();
                                       newColExp.add("'HH24:MI:SS'");
                                       newSC.setColumnExpression(newColExp);
                                       newFunctionArgument.add(newSC);
                                    }
                                 } else {
                                    newSC = new SelectColumn();
                                    newColExp = new Vector();
                                    newColExp.add("'HH24:MI:SS'");
                                    newSC.setColumnExpression(newColExp);
                                    newFunctionArgument.add(newSC);
                                 }
                              } else {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'YYYY-MM-DD HH24:MI:SS'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              }
                           } catch (NumberFormatException var25) {
                              newFunctionArgument.add(this.functionArguments.get(2));
                           }
                        } else {
                           newFunctionArgument.add(this.functionArguments.get(2));
                        }
                     } else {
                        newFunctionArgument.add(this.functionArguments.get(2));
                     }
                  }
               } else if (this.functionArguments.size() == 2) {
                  if (this.functionArguments.get(1).toString().toUpperCase().indexOf("'MM'") != -1 && this.functionArguments.get(1).toString().toUpperCase().indexOf("'YYYY'") != -1) {
                     newFunctionArgument.add("'YYYY-MM-DD'");
                  }

                  if (this.functionArguments.get(1).toString().startsWith("'")) {
                     String format = SwisSQLUtils.getDateFormat(this.functionArguments.get(1).toString().trim(), 1);
                     if (format != null) {
                        if (format.startsWith("'1900")) {
                           newFunctionArgument.setElementAt(format, 0);
                           newFunctionArgument.add("'YYYY-MM-DD HH24:MI:SS'");
                        } else {
                           if (format.indexOf(".FF") != -1 || format.indexOf(":FF") != -1) {
                              this.functionName.setColumnName("TO_TIMESTAMP");
                           }

                           newFunctionArgument.add(format);
                        }
                     }
                  }

                  if (this.functionArguments.get(1) instanceof SelectColumn) {
                     scTemp = (SelectColumn)this.functionArguments.get(1);
                     newFunctionArgument = scTemp.getColumnExpression();
                     if (newFunctionArgument != null && newFunctionArgument.size() == 1 && newFunctionArgument.get(0) instanceof FunctionCalls) {
                        tempFC = (FunctionCalls)newFunctionArgument.get(0);
                        innerFunctionName = tempFC.getFunctionName().getColumnName();
                        if (innerFunctionName != null && innerFunctionName.trim().equalsIgnoreCase("TO_CHAR")) {
                           tempArgs = tempFC.getFunctionArguments();
                           if (tempArgs != null && tempArgs.size() == 2 && tempArgs.get(1) instanceof SelectColumn) {
                              SelectColumn toBeAdded = (SelectColumn)tempArgs.get(1);
                              newFunctionArgument.add(toBeAdded);
                           }
                        }
                     }
                  }
               }

               this.setFunctionArguments(newFunctionArgument);
            }
         } else {
            if (tableColumn != null) {
               columnDataType = MetadataInfoUtil.getDatatypeName(from_sqs, tableColumn);
            }

            if (columnDataType != null && columnDataType.toLowerCase().startsWith("uniqueidentifier")) {
               newFunctionArgument = new Vector();
               newFunctionArgument.add(arguments.get(1));
               newFunctionArgument.add(arguments.get(0));
               this.setFunctionArguments(newFunctionArgument);
            } else {
               newFunctionArgument = new Vector();
               this.functionName.setColumnName("TO_CHAR");
               this.asDatatype = null;
               newFunctionArgument.add(arguments.get(1));
               if (this.functionArguments.size() > 2) {
                  if (this.functionArguments.get(2) instanceof SelectColumn) {
                     scTemp = (SelectColumn)this.functionArguments.get(2);
                     newFunctionArgument = scTemp.getColumnExpression();
                     if (newFunctionArgument != null && newFunctionArgument.size() == 1) {
                        if (newFunctionArgument.get(0) instanceof String) {
                           size = (String)newFunctionArgument.get(0);

                           try {
                              i = Integer.parseInt(size);
                              if (i == 100) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'Mon DD YYYY HH:MIAM'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 101) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'MM/DD/YYYY'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 102) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'YYYY.MM.DD'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 103) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'DD/MM/YYYY'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 104) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'DD.MM.YYYY'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 105) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'DD-MM-YYYY'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 106) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'DD Mon YYYY'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 107) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'MON DD, YYYY'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 108) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'HH24:MI:SS'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 109) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'Mon DD YYYY HH:MI:SSAM'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 110) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'MM-DD-YYYY'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 111) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'YYYY/MM/DD'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 112) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'YYYYMMDD'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 113) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'DD Mon YYYY HH24:MI:SS'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 114) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'HH24:MI:SS'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 120) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'YYYY-MM-DD HH24:MI:SS'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 121) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'YYYY-MM-DD HH24:MI:SS'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 0) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'Mon DD YYYY HH:MIAM'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 1) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'MM/DD/YY'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 2) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'YY.MM.DD'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 3) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'DD/MM/YY'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 4) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'DD.MM.YY'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 5) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'DD-MM-YY'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 6) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'DD Mon YY'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 7) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'Mon DD, YY'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 8) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'HH24:MI:SS'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 9) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'Mon DD YYYY HH:MI:SSAM'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 10) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'MM-DD-YY'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 11) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'YY/MM/DD'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 12) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'YYMMDD'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 13) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'DD MON YYYY HH24:MI:SS'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 14) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'HH24:MI:SS'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 20) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'YYYY MM DD HH24:MI:SS'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 21) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'YYYY MM DD HH24:MI:SS.FF3'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else {
                                 newFunctionArgument.add(this.functionArguments.get(2));
                              }
                           } catch (NumberFormatException var23) {
                              newFunctionArgument.add(this.functionArguments.get(2));
                           }
                        } else {
                           newFunctionArgument.add(this.functionArguments.get(2));
                        }
                     } else {
                        newFunctionArgument.add(this.functionArguments.get(2));
                     }
                  }
               } else if (isDate) {
                  scTemp = new SelectColumn();
                  newFunctionArgument = new Vector();
                  newFunctionArgument.add("'Mon DD YYYY HH:MIAM'");
                  scTemp.setColumnExpression(newFunctionArgument);
                  newFunctionArgument.add(scTemp);
               } else if (isBinary) {
                  this.functionName.setColumnName("CAST");
                  this.asDatatype = "AS";
                  newFunctionArgument.add(arguments.get(0));
                  if (SwisSQLOptions.fromSQLServer) {
                     isSetExpr = false;
                     if (from_sqs != null && from_sqs.getSelectStatement().getSelectItemList() != null && from_sqs.getSelectStatement().getSelectItemList().size() == 1 && from_sqs.getSelectStatement().getSelectItemList().get(0) instanceof SelectColumn && from_sqs.getFromClause() == null && (((SelectColumn)from_sqs.getSelectStatement().getSelectItemList().get(0)).getColumnExpression().size() < 3 || !((SelectColumn)from_sqs.getSelectStatement().getSelectItemList().get(0)).getColumnExpression().get(1).toString().equals("="))) {
                        isSetExpr = true;
                     }

                     CharacterClass cc = (CharacterClass)arguments.get(0);
                     size = cc.getSize();
                     if (size == null && !isSetExpr) {
                        cc.setSize("30");
                        cc.setOpenBrace("(");
                        cc.setClosedBrace(")");
                     } else if (isSetExpr) {
                        cc.setSize((String)null);
                        cc.setOpenBrace((String)null);
                        cc.setClosedBrace((String)null);
                     }
                  }
               }

               this.setFunctionArguments(newFunctionArgument);
            }
         }
      } else if (this.functionArguments.get(0) instanceof SelectColumn) {
         SelectColumn sc = (SelectColumn)this.functionArguments.get(0);
         if (sc.getColumnExpression() != null && sc.getColumnExpression().size() > 0 && sc.getColumnExpression().get(0) instanceof FunctionCalls) {
            FunctionCalls fc = (FunctionCalls)sc.getColumnExpression().get(0);
            tc = fc.getFunctionName();
            if ((tc == null || !tc.getColumnName().equalsIgnoreCase("CHAR")) && !tc.getColumnName().equalsIgnoreCase("CHR") && !tc.getColumnName().equalsIgnoreCase("VARCHAR")) {
               if (tc.getColumnName().trim().equalsIgnoreCase("DECIMAL") || tc.getColumnName().trim().equalsIgnoreCase("NUMERIC")) {
                  this.functionName.setColumnName("TO_NUMBER");
                  newFunctionArgument = new Vector();
                  this.asDatatype = null;
                  newFunctionArgument.add(arguments.get(1));
                  this.setFunctionArguments(newFunctionArgument);
               }
            } else {
               newFunctionArgument = new Vector();
               this.functionName.setColumnName("TO_CHAR");
               this.asDatatype = null;
               newFunctionArgument.add(arguments.get(1));
               if (this.functionArguments.size() > 2 && this.functionArguments.get(2) instanceof SelectColumn) {
                  SelectColumn fcSelectColumn = (SelectColumn)this.functionArguments.get(2);
                  Vector fcColumnExpression = fcSelectColumn.getColumnExpression();
                  if (fcColumnExpression != null && fcColumnExpression.size() == 1) {
                     if (fcColumnExpression.get(0) instanceof String) {
                        size = (String)fcColumnExpression.get(0);

                        try {
                           int i = Integer.parseInt(size);
                           SelectColumn newSC;
                           Vector newColExp;
                           if (i == 103) {
                              newSC = new SelectColumn();
                              newColExp = new Vector();
                              newColExp.add("'DD/MM/YYYY'");
                              newSC.setColumnExpression(newColExp);
                              newFunctionArgument.add(newSC);
                           } else if (i == 102) {
                              newSC = new SelectColumn();
                              newColExp = new Vector();
                              newColExp.add("'YYYY.MM.DD'");
                              newSC.setColumnExpression(newColExp);
                              newFunctionArgument.add(newSC);
                           } else if (i != 121 && i != 120) {
                              if (i == 111) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'YYYY/MM/DD'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 101) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'DD MON YYYY'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 104) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'DD.MM.YYYY'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 105) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'DD-MM-YYY'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 106) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'YYYY-MM-DD'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 107) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'MON DD, YYYY'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 108) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'HH:MI:SS'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 109) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'MON DD YYYY'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 110) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'MM-DD-YYYY'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 112) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'YYYY MM DD'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 113) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'DD MON YYYY'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 2) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'YYY/MM/DD'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 3) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'DD/MM/YY'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 4) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'DD.MM.YY'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 5) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'DD-MM-YY'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 6) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'DD-MON YY'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 7) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'MON DD, YY'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 9) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'MON DD YYYY'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 10) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'MM-DD-YY'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 11) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'YY/MM/DD'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 12) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'YYMMDD'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else if (i == 13) {
                                 newSC = new SelectColumn();
                                 newColExp = new Vector();
                                 newColExp.add("'DD MON YYYY'");
                                 newSC.setColumnExpression(newColExp);
                                 newFunctionArgument.add(newSC);
                              } else {
                                 newFunctionArgument.add(this.functionArguments.get(2));
                              }
                           } else {
                              newSC = new SelectColumn();
                              newColExp = new Vector();
                              newColExp.add("'YYYY-MM-DD'");
                              newSC.setColumnExpression(newColExp);
                              newFunctionArgument.add(newSC);
                           }
                        } catch (NumberFormatException var24) {
                           newFunctionArgument.add(this.functionArguments.get(2));
                        }
                     } else {
                        newFunctionArgument.add(this.functionArguments.get(2));
                     }
                  } else {
                     newFunctionArgument.add(this.functionArguments.get(2));
                  }
               }

               this.setFunctionArguments(newFunctionArgument);
            }
         } else {
            newFunctionArgument = new Vector();
            newFunctionArgument.add(0, arguments.get(1));
            if (arguments.get(0).toString().equalsIgnoreCase("sql_variant")) {
               newFunctionArgument.add(1, "SYS.ANYDATA");
            } else if (arguments.get(0).toString().equalsIgnoreCase("uniqueidentifier")) {
               newFunctionArgument.add(1, "CHAR(36)");
            } else {
               newFunctionArgument.add(1, arguments.get(0));
            }

            this.setFunctionArguments(newFunctionArgument);
         }
      } else if (this.functionArguments.size() != 2 || this.functionArguments.get(1).toString().indexOf("'") == -1 || !((Datatype)this.functionArguments.get(0)).toString().trim().toUpperCase().startsWith("CHAR") && !((Datatype)this.functionArguments.get(0)).toString().trim().toUpperCase().startsWith("VARCHAR")) {
         dummyArgs = new Vector();
         dummyArgs.add(0, arguments.get(1));
         dummyArgs.add(1, arguments.get(0));
         this.setFunctionArguments(dummyArgs);
      } else if (arguments.get(0).toString().indexOf("(") != -1) {
         String value = arguments.get(1).toString().substring(1, arguments.get(1).toString().length() - 1);
         int size = Integer.parseInt(((Datatype)arguments.get(0)).getSize());
         if (value.length() > size) {
            this.functionName.setColumnName("SUBSTR");
            this.setAsDatatype((String)null);
            this.functionArguments.clear();
            this.functionArguments.add(arguments.get(1));
            this.functionArguments.add("1");
            this.functionArguments.add(new Integer(size));
         } else if (value.length() <= size) {
            this.functionName.setColumnName(arguments.get(1).toString());
            dummyArgs = new Vector();
            this.setFunctionArguments(dummyArgs);
            this.setAsDatatype((String)null);
            this.setOpenBracesForFunctionNameRequired(false);
         }
      } else {
         this.functionName.setColumnName(arguments.get(1).toString());
         dummyArgs = new Vector();
         this.setFunctionArguments(dummyArgs);
         this.setAsDatatype((String)null);
         this.setOpenBracesForFunctionNameRequired(false);
      }

      if (SwisSQLOptions.removeFormatForOracleToCharFunction && this.functionName.getColumnName().equalsIgnoreCase("to_char") && this.functionArguments.size() > 1) {
         this.functionArguments.removeElementAt(1);
      }

   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("CONVERT");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
            Datatype datatype = (Datatype)this.functionArguments.elementAt(i_count);
            datatype.toMSSQLServerString();
            arguments.addElement(datatype);
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (arguments.get(1).toString().equalsIgnoreCase("CHAR")) {
         arguments.set(1, "VARCHAR(MAX)");
      }

      if (from_sqs.isMSAzure()) {
         String arg1 = arguments.get(1).toString();
         if (arg1.equalsIgnoreCase("SIGNED") || arg1.equalsIgnoreCase("UNSIGNED")) {
            arg1 = "BIGINT";
         }

         arguments.set(1, arguments.get(0));
         arguments.set(0, arg1);
      }

      this.setFunctionArguments(arguments);
   }

   public void toSnowflake(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSnowflakeSelect(to_sqs, from_sqs));
         } else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
            Datatype datatype = (Datatype)this.functionArguments.elementAt(i_count);
            datatype.toSnowflakeString();
            arguments.addElement(datatype);
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      String newdatatype = arguments.get(1).toString();
      String oldDatatype = arguments.get(1).toString().toUpperCase();
      if (!oldDatatype.equals("SIGNED") && !oldDatatype.equals("UNSIGNED")) {
         if (oldDatatype.startsWith("CHAR")) {
            newdatatype = "TEXT";
         }
      } else {
         newdatatype = "BIGINT";
      }

      qry = "CAST( " + arguments.get(0).toString() + " AS " + newdatatype + ")";
      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionArguments(new Vector());
      this.functionName.setColumnName(qry);
   }

   public void toAthena(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toAthenaSelect(to_sqs, from_sqs));
         } else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
            Datatype datatype = (Datatype)this.functionArguments.elementAt(i_count);
            datatype.toAthenaString();
            arguments.addElement(datatype);
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      String newdatatype = arguments.get(1).toString();
      String oldDatatype = arguments.get(1).toString().toUpperCase();
      if (!oldDatatype.equals("SIGNED") && !oldDatatype.equals("UNSIGNED")) {
         if (oldDatatype.startsWith("CHAR") || oldDatatype.startsWith("CHR")) {
            newdatatype = "VARCHAR";
         }
      } else {
         newdatatype = "BIGINT";
      }

      qry = "CAST( " + arguments.get(0).toString() + " AS " + newdatatype + ")";
      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionArguments(new Vector());
      this.functionName.setColumnName(qry);
   }

   public void toSqlite(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSqliteSelect(to_sqs, from_sqs));
         } else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
            Datatype datatype = (Datatype)this.functionArguments.elementAt(i_count);
            datatype.toSqliteString();
            arguments.addElement(datatype);
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      String newdatatype = arguments.get(1).toString();
      String oldDatatype = arguments.get(1).toString().toUpperCase();
      if (!oldDatatype.equals("SIGNED") && !oldDatatype.equals("UNSIGNED")) {
         if (oldDatatype.startsWith("CHAR") || oldDatatype.startsWith("CHR")) {
            newdatatype = "TEXT";
         }
      } else {
         newdatatype = "INTEGER";
      }

      qry = "CAST( " + arguments.get(0).toString() + " AS " + newdatatype + ")";
      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionArguments(new Vector());
      this.functionName.setColumnName(qry);
   }

   public void toSapHana(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSapHanaSelect(to_sqs, from_sqs));
         } else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
            Datatype datatype = (Datatype)this.functionArguments.elementAt(i_count);
            datatype.toSapHanaString();
            arguments.addElement(datatype);
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      String newdatatype = arguments.get(1).toString();
      String oldDatatype = arguments.get(1).toString().toUpperCase();
      if (!oldDatatype.equals("SIGNED") && !oldDatatype.equals("UNSIGNED")) {
         if (oldDatatype.startsWith("CHAR") || oldDatatype.startsWith("CHR")) {
            newdatatype = "VARCHAR";
         }
      } else {
         newdatatype = "BIGINT";
      }

      qry = "CAST( " + arguments.get(0).toString() + " AS " + newdatatype + ")";
      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionArguments(new Vector());
      this.functionName.setColumnName(qry);
   }

   public void toSybase(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("CONVERT");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSybaseSelect(to_sqs, from_sqs));
         } else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
            Datatype datatype = (Datatype)this.functionArguments.elementAt(i_count);
            datatype.toSybaseString();
            arguments.addElement(datatype);
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("CAST");
      this.setAsDatatype("AS");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            if (i_count == 1 && this.functionArguments.elementAt(i_count).toString().toLowerCase().contains("char(")) {
               arguments.addElement((SelectColumn)this.functionArguments.elementAt(i_count));
            } else {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toDB2Select(to_sqs, from_sqs));
            }
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (arguments.size() == 2) {
         if (arguments.get(1).toString().equalsIgnoreCase("signed")) {
            arguments.set(1, "INTEGER");
         } else if (arguments.get(1).toString().equalsIgnoreCase("CHAR")) {
            arguments.set(1, "VARCHAR");
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() == 2) {
         Object obj1 = this.functionArguments.get(1);
         if (obj1 instanceof CharacterClass) {
            CharacterClass cc = (CharacterClass)obj1;
            if (cc.getDatatypeName().equalsIgnoreCase("varchar")) {
               Object obj2 = this.functionArguments.get(0);
               if (obj2 instanceof SelectColumn) {
                  Vector colExpr = ((SelectColumn)obj2).getColumnExpression();
                  if (colExpr.size() == 1 && colExpr.get(0) instanceof FunctionCalls) {
                     FunctionCalls fc = (FunctionCalls)colExpr.get(0);
                     TableColumn tc = fc.getFunctionName();
                     if (tc != null && this.isIntegerRetFunction(tc.getColumnName())) {
                        String size = cc.getSize();
                        if (size == null) {
                           size = "30";
                        }

                        colExpr.setElementAt("RTRIM(CAST(" + colExpr.get(0) + " AS CHAR(" + size + ")))", 0);
                     }
                  }
               }
            }
         }
      }

   }

   private boolean isIntegerRetFunction(String fnName) {
      return fnName.equalsIgnoreCase("day") || fnName.equalsIgnoreCase("month") || fnName.equalsIgnoreCase("year") || fnName.equalsIgnoreCase("quarter") || fnName.equalsIgnoreCase("week") || fnName.equalsIgnoreCase("dayofyear") || fnName.equalsIgnoreCase("dayofweek") || fnName.equalsIgnoreCase("hour") || fnName.equalsIgnoreCase("minute") || fnName.equalsIgnoreCase("second");
   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      boolean useCitext = from_sqs != null && from_sqs.getBooleanValues("can.use.citext.over.text");

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String dataType = from_sqs != null && from_sqs.isVerticaDb() ? "VARCHAR" : "TEXT";
      if (this.functionName.getColumnName().equalsIgnoreCase("VARCHAR")) {
         this.functionName.setColumnName("CAST(" + arguments.get(0).toString() + " AS " + (useCitext ? "CITEXT" : dataType) + ")");
         this.setFunctionArguments(new Vector());
         this.setOpenBracesForFunctionNameRequired(false);
      } else {
         String qry = "";
         boolean isPostgreLiveDbs = from_sqs != null && (from_sqs.isAmazonRedShift() || from_sqs.isPgsqlLive() || from_sqs.isVerticaDb());
         boolean canUseUDFFunction = from_sqs != null && !isPostgreLiveDbs && from_sqs.getBooleanValues("use.udf.functions.for.numeric");
         if (arguments.size() == 2) {
            String numericStr = "(" + arguments.get(0).toString() + ")";
            if (canUseUDFFunction) {
               numericStr = "TONUMERIC_UDF(" + numericStr + ")";
            } else {
               numericStr = "TONUMERIC_UDF((" + numericStr + ")::" + dataType + ")";
            }

            if (!arguments.get(1).toString().equalsIgnoreCase("CURRENT_DATE") && !arguments.get(1).toString().equalsIgnoreCase("DATE")) {
               if (arguments.get(1).toString().equalsIgnoreCase("DATETIME")) {
                  qry = " cast(" + arguments.get(0) + " as TIMESTAMP) ";
               } else if (!arguments.get(1).toString().equalsIgnoreCase("CURRENT_TIME") && !arguments.get(1).toString().equalsIgnoreCase("TIME")) {
                  if (!arguments.get(1).toString().equalsIgnoreCase("CHAR") && !arguments.get(1).toString().startsWith("CHR")) {
                     if (arguments.get(1).toString().equalsIgnoreCase("SIGNED")) {
                        if (isPostgreLiveDbs) {
                           qry = " cast(" + arguments.get(0) + " as BIGINT) ";
                        } else {
                           qry = " TOBIGINT_UDF(" + arguments.get(0) + ")";
                        }
                     } else if (arguments.get(1).toString().replaceAll("\\s+", "").equalsIgnoreCase("SIGNEDASINTEGER")) {
                        if (isPostgreLiveDbs) {
                           qry = " cast(" + arguments.get(0) + " as BIGINT) ";
                        } else {
                           qry = " TOBIGINT_UDF(" + arguments.get(0) + ")";
                        }
                     } else if (!arguments.get(1).toString().equalsIgnoreCase("DECIMAL") && !arguments.get(1).toString().toUpperCase().startsWith("DECIMAL")) {
                        if (!arguments.get(1).toString().equalsIgnoreCase("\"BINARY\"")) {
                           throw new ConvertException("\nGiven function " + this.functionName.getColumnName() + " with " + this.functionArguments.size() + " arguments is not supported. \n Please ensure that the correct number of arguments are passed\n");
                        }

                        qry = arguments.get(0) + "";
                     } else if (isPostgreLiveDbs) {
                        qry = "CAST(" + arguments.get(0).toString() + "AS " + arguments.get(1).toString() + ")";
                     } else {
                        qry = "CAST(" + numericStr + " AS " + arguments.get(1).toString() + ")";
                     }
                  } else if (isPostgreLiveDbs && arguments.get(1).toString().toLowerCase().startsWith("chr(")) {
                     String len = arguments.get(1).toString().toLowerCase().replace("chr(", "").replace(")", "");
                     qry = "substring(" + arguments.get(0) + "::" + dataType + " from 1 for " + len + " )";
                  } else {
                     qry = " cast(" + arguments.get(0) + " as " + (useCitext ? "CITEXT" : "TEXT") + ") ";
                  }
               } else {
                  qry = " cast(" + arguments.get(0) + " as TIME) ";
               }
            } else {
               qry = " cast(" + arguments.get(0) + " as DATE) ";
            }

            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         }

      }
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      int size = this.functionArguments.size();
      boolean isDateConversion = false;
      boolean isChar = false;
      String funName = null;
      SelectColumn selCol = null;
      if (size > 1 && this.functionArguments.get(1) instanceof SelectColumn) {
         selCol = (SelectColumn)this.functionArguments.get(1);
      }

      funName = this.getFunctionNameFromSelectColumn(selCol);
      if (funName != null) {
         if (funName.trim().equalsIgnoreCase("GetDate")) {
            isDateConversion = true;
         } else if (funName.trim().equalsIgnoreCase("char")) {
            isChar = true;
         }
      }

      boolean isCharNeeded;
      Vector arguments;
      if (isDateConversion) {
         isCharNeeded = false;
         this.functionName.setColumnName("CONVERT");
         arguments = new Vector();

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
            } else if (this.functionArguments.elementAt(i_count) instanceof CharacterClass) {
               isCharNeeded = true;
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         if (size == 3) {
            arguments.clear();
            arguments.addElement("CURDATE() ");
         }

         if (isCharNeeded) {
            arguments.addElement("CHAR");
         }

         this.setFunctionArguments(arguments);
      } else {
         isCharNeeded = true;
         arguments = new Vector();
         Vector datatypeVec = new Vector();
         Vector expressionVec = new Vector();

         Datatype targDatatype;
         for(int i_count = 0; i_count < size; ++i_count) {
            if (!(this.functionArguments.elementAt(i_count) instanceof SelectColumn)) {
               if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
                  targDatatype = (Datatype)this.functionArguments.elementAt(i_count);
                  targDatatype.toMySQLString();
                  arguments.addElement(targDatatype);
                  datatypeVec.addElement(targDatatype);
               } else {
                  arguments.addElement(this.functionArguments.elementAt(i_count));
                  expressionVec.addElement(this.functionArguments.elementAt(i_count));
               }
            } else {
               new SelectColumn();
               SelectColumn sc;
               if (isChar && i_count == 1 && from_sqs != null && from_sqs.isMySqlLive()) {
                  sc = (SelectColumn)this.functionArguments.elementAt(i_count);
               } else {
                  sc = ((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs);
               }

               if (i_count == 1 && from_sqs != null && from_sqs.isHyperSql()) {
                  Vector colExp = sc.getColumnExpression();
                  if (colExp != null && !colExp.isEmpty() && colExp.get(0).toString().equalsIgnoreCase("signed")) {
                     colExp.set(0, "BIGINT");
                  }
               }

               arguments.addElement(sc);
               expressionVec.addElement(sc);
            }
         }

         if (size == 2 && this.getUsing() == null) {
            Vector newArguments = new Vector();
            if (datatypeVec.size() == 1 && expressionVec.size() == 1) {
               newArguments.add(expressionVec.get(0));
               targDatatype = (Datatype)datatypeVec.get(0);
               if (targDatatype.getDatatypeName().equalsIgnoreCase("VARCHAR") || targDatatype.getDatatypeName().equalsIgnoreCase("CHAR") || targDatatype.getDatatypeName().equalsIgnoreCase("TEXT") || targDatatype.getDatatypeName().equalsIgnoreCase("LONGTEXT")) {
                  if (targDatatype instanceof CharacterClass) {
                     CharacterClass charDatatype = (CharacterClass)targDatatype;
                     charDatatype.setNational((String)null);
                     charDatatype.setBinary((String)null);
                  }

                  targDatatype.setDatatypeName("CHAR");
                  newArguments.add(targDatatype);
                  this.setFunctionArguments(newArguments);
                  isCharNeeded = false;
               }
            }
         }

         if (isCharNeeded) {
            this.setFunctionArguments(arguments);
         }
      }

   }

   public void toANSISQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("CAST");
      Vector arguments = new Vector();
      this.setAsDatatype("AS");

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toANSISelect(to_sqs, from_sqs));
         } else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
            Datatype datatype = (Datatype)this.functionArguments.elementAt(i_count);
            datatype.toANSIString();
            arguments.addElement(datatype);
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      Vector swapArguments = new Vector();
      swapArguments.add(0, arguments.get(1));
      swapArguments.add(1, arguments.get(0));
      this.setFunctionArguments(swapArguments);
   }

   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("CAST");
      Vector arguments = new Vector();
      this.setAsDatatype("AS");

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toTeradataSelect(to_sqs, from_sqs));
         } else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
            Datatype datatype = (Datatype)this.functionArguments.elementAt(i_count);
            datatype.toTeradataString();
            arguments.addElement(datatype);
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (arguments.get(1) instanceof SelectColumn && arguments.get(1).toString().toUpperCase().indexOf("US7ASCII") != -1) {
         this.functionName.setColumnName("TRANSLATE");
         this.setAsDatatype("USING");
         arguments.setElementAt("UNICODE_TO_LATIN", 1);
         this.setFunctionArguments(arguments);
      } else {
         Vector swapArguments = new Vector();
         swapArguments.add(0, arguments.get(1));
         swapArguments.add(1, arguments.get(0));
         this.setFunctionArguments(swapArguments);
      }

   }

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("");
      Vector arguments = new Vector();
      this.setAsDatatype("");

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toInformixSelect(to_sqs, from_sqs));
         } else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
            Datatype datatype = (Datatype)this.functionArguments.elementAt(i_count);
            datatype.toInformixString();
            arguments.addElement(datatype);
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      String newdatatype = arguments.get(1).toString();
      String oldDatatype = arguments.get(1).toString().toUpperCase();
      if (!oldDatatype.equals("SIGNED") && !oldDatatype.equals("UNSIGNED")) {
         if (oldDatatype.startsWith("CHAR") || oldDatatype.startsWith("CHR")) {
            newdatatype = "LVARCHAR";
         }
      } else {
         newdatatype = "BIGINT";
      }

      qry = "CAST( " + arguments.get(0).toString() + " AS " + newdatatype + ")";
      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionArguments(new Vector());
      this.functionName.setColumnName(qry);
   }

   public void toExcel(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toExcelSelect(to_sqs, from_sqs));
         } else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
            Datatype datatype = (Datatype)this.functionArguments.elementAt(i_count);
            datatype.toExcelString();
            arguments.addElement(datatype);
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      String newdatatype = arguments.get(1).toString();
      String oldDatatype = arguments.get(1).toString().toUpperCase();
      if (!oldDatatype.equalsIgnoreCase("SIGNED") && !oldDatatype.equalsIgnoreCase("UNSIGNED")) {
         if (!oldDatatype.startsWith("BOOL") && !oldDatatype.startsWith("BOOLEAN")) {
            if (!oldDatatype.startsWith("TIMESTAMP") && !oldDatatype.startsWith("DATE") && !oldDatatype.startsWith("DATETIME")) {
               qry = "CStr(" + arguments.get(0).toString() + ")";
            } else {
               qry = "CDate(" + arguments.get(0).toString() + ")";
            }
         } else {
            qry = "CBool(" + arguments.get(0).toString() + ")";
         }
      } else {
         qry = "CLng(" + arguments.get(0).toString() + ")";
      }

      this.setOpenBracesForFunctionNameRequired(false);
      this.setFunctionArguments(new Vector());
      this.functionName.setColumnName(qry);
   }

   public void toMsAccess(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMsAccessJdbcSelect(to_sqs, from_sqs));
         } else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
            Datatype datatype = (Datatype)this.functionArguments.elementAt(i_count);
            datatype.toMsAccessJdbcString();
            arguments.addElement(datatype);
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      String oldDatatype = arguments.get(1).toString().toUpperCase();
      if (oldDatatype.startsWith("DECIMAL")) {
         this.functionName.setColumnName("CAST");
         this.setAsDatatype("AS");
         this.setFunctionArguments(arguments);
      } else {
         if (!oldDatatype.equalsIgnoreCase("SIGNED") && !oldDatatype.equalsIgnoreCase("UNSIGNED")) {
            if (!oldDatatype.startsWith("BOOL") && !oldDatatype.startsWith("BOOLEAN")) {
               if (!oldDatatype.startsWith("TIMESTAMP") && !oldDatatype.startsWith("DATETIME")) {
                  if (oldDatatype.startsWith("DATE")) {
                     qry = "CAST(" + arguments.get(0).toString() + " AS DATE)";
                  } else {
                     qry = "CAST(" + arguments.get(0).toString() + " AS LONGVARCHAR)";
                  }
               } else {
                  qry = "CAST(" + arguments.get(0).toString() + " AS TIMESTAMP)";
               }
            } else {
               qry = "CAST(" + arguments.get(0).toString() + " AS BOOLEAN)";
            }
         } else {
            qry = "CAST(" + arguments.get(0).toString() + " AS BIGINT)";
         }

         this.setOpenBracesForFunctionNameRequired(false);
         this.setFunctionArguments(new Vector());
         this.functionName.setColumnName(qry);
      }
   }

   public void toTimesTen(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      if (this.functionArguments.size() == 2) {
         if (this.functionArguments.elementAt(0) instanceof Datatype) {
            Datatype datatype = (Datatype)this.functionArguments.elementAt(0);
            String type = datatype.getDatatypeName();
            if (type.toLowerCase().indexOf("char") != -1) {
               this.functionName.setColumnName("TO_CHAR");
               arguments.add(((SelectColumn)this.functionArguments.elementAt(1)).toTimesTenSelect(to_sqs, from_sqs));
               this.setFunctionArguments(arguments);
            } else {
               if (type.toLowerCase().indexOf("date") == -1 && type.toLowerCase().indexOf("time") == -1) {
                  throw new ConvertException("\nThe function CONVERT is not supported in TimesTen 5.1.21\n");
               }

               this.functionName.setColumnName("TO_DATE");
               arguments.add(((SelectColumn)this.functionArguments.elementAt(1)).toTimesTenSelect(to_sqs, from_sqs));
               if (this.functionArguments.elementAt(1).toString().trim().startsWith("'")) {
                  String literalValue = this.functionArguments.elementAt(1).toString().trim();
                  String format = SwisSQLUtils.getDateFormat(literalValue, 10);
                  if (format != null && (format.equals("YYYY-MM-DD") || format.equals("HH24:MI:SS"))) {
                     if (type.toLowerCase().indexOf("datetime") != -1) {
                        if (format.equals("YYYY-MM-DD")) {
                           literalValue = literalValue.substring(0, literalValue.length() - 1) + " 00:00:00'";
                        } else {
                           literalValue = "'1900-01-01 " + literalValue.substring(1);
                        }

                        ((SelectColumn)arguments.get(0)).getColumnExpression().setElementAt(literalValue, 0);
                     }

                     format = null;
                  }

                  if (format != null) {
                     if (format.startsWith("'1900")) {
                        ((SelectColumn)arguments.get(0)).getColumnExpression().setElementAt(format, 0);
                     } else if (format.equals(literalValue)) {
                        literalValue = literalValue.substring(1, literalValue.length() - 1);
                        int len = literalValue.length();
                        if (len == 8) {
                           literalValue = literalValue.substring(0, 4) + "-" + literalValue.substring(4, 6) + "-" + literalValue.substring(6);
                        } else if (len == 6) {
                           String yearStr = literalValue.substring(0, 2);
                           int year = Integer.parseInt(yearStr);
                           if (year < 50) {
                              yearStr = "20" + yearStr;
                           } else {
                              yearStr = "19" + yearStr;
                           }

                           literalValue = yearStr + "-" + literalValue.substring(2, 4) + "-" + literalValue.substring(4);
                        }

                        ((SelectColumn)arguments.get(0)).getColumnExpression().setElementAt("'" + literalValue + "'", 0);
                     } else {
                        arguments.add(format);
                     }
                  }
               }

               this.setFunctionArguments(arguments);
            }
         }
      } else if (this.functionArguments.size() == 3) {
         this.functionName.setColumnName("TO_DATE");
         arguments.add(((SelectColumn)this.functionArguments.elementAt(1)).toTimesTenSelect(to_sqs, from_sqs));
         this.setFunctionArguments(arguments);
      }

   }

   public void toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("CAST");
      Vector arguments = new Vector();
      this.setAsDatatype("AS");

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toNetezzaSelect(to_sqs, from_sqs));
         } else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
            Datatype datatype = (Datatype)this.functionArguments.elementAt(i_count);
            datatype.toNetezzaString();
            arguments.addElement(datatype);
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      Vector swapArguments = new Vector();
      swapArguments.add(0, arguments.get(1));
      swapArguments.add(1, arguments.get(0));
      this.setFunctionArguments(swapArguments);
   }

   private String getFunctionNameFromSelectColumn(SelectColumn selCol) {
      String funName = null;
      if (selCol != null) {
         Vector colExp = selCol.getColumnExpression();
         if (colExp != null && colExp.size() == 1 && colExp.get(0) instanceof FunctionCalls) {
            FunctionCalls funCall = (FunctionCalls)colExp.get(0);
            TableColumn tabCol = funCall.getFunctionName();
            if (tabCol != null) {
               funName = tabCol.getColumnName();
            }
         }
      }

      return funName;
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (this.functionName.getColumnName().equalsIgnoreCase("VARCHAR")) {
         this.setFunctionArguments(arguments);
      } else if (this.functionArguments.size() != 2) {
         throw new ConvertException("\nGiven function " + this.functionName.getColumnName() + "is not supported in VectorWise\n");
      } else {
         if (!arguments.get(1).toString().equalsIgnoreCase("CHAR") && !arguments.get(1).toString().toUpperCase().startsWith("CHAR") && !arguments.get(1).toString().toUpperCase().startsWith("CHR")) {
            if (arguments.get(1).toString().equalsIgnoreCase("BINARY")) {
               this.functionName.setColumnName(arguments.get(0).toString());
               this.setOpenBracesForFunctionNameRequired(false);
               this.functionArguments = new Vector();
            } else if (arguments.get(1).toString().equalsIgnoreCase("DATE")) {
               this.functionName.setColumnName("CAST(" + arguments.get(0).toString() + " AS DATE)");
               this.setOpenBracesForFunctionNameRequired(false);
               this.functionArguments = new Vector();
            } else if (arguments.get(1).toString().equalsIgnoreCase("DATETIME")) {
               this.functionName.setColumnName("CAST(" + arguments.get(0).toString() + " AS TIMESTAMP)");
               this.setOpenBracesForFunctionNameRequired(false);
               this.functionArguments = new Vector();
            } else if (arguments.get(1).toString().equalsIgnoreCase("TIME")) {
               this.functionName.setColumnName("CAST(" + arguments.get(0).toString() + " AS TIME)");
               this.setOpenBracesForFunctionNameRequired(false);
               this.functionArguments = new Vector();
            } else {
               String secArg;
               String firstArg;
               if (!arguments.get(1).toString().equalsIgnoreCase("SIGNED") && !arguments.get(1).toString().equalsIgnoreCase("SIGNED INTEGER") && !arguments.get(1).toString().equalsIgnoreCase("INTEGER")) {
                  if (!arguments.get(1).toString().equalsIgnoreCase("DECIMAL") && !arguments.get(1).toString().toUpperCase().startsWith("DECIMAL")) {
                     if (!arguments.get(1).toString().toUpperCase().startsWith("TRUNCATE")) {
                        throw new ConvertException("\nGiven function " + this.functionName.getColumnName() + "is not supported in VectorWise\n");
                     }

                     firstArg = arguments.get(1).toString().toUpperCase().replaceAll("TRUNCATE", "DECIMAL");
                     this.functionName.setColumnName("IF(" + arguments.get(0).toString() + " IS DECIMAL, CAST(" + arguments.get(0).toString() + " AS " + firstArg + "),0)");
                     this.setOpenBracesForFunctionNameRequired(false);
                     this.functionArguments = new Vector();
                  } else {
                     firstArg = arguments.get(0).toString();
                     secArg = arguments.get(1).toString();
                     if (((SelectColumn)this.functionArguments.get(1)).getColumnExpression().get(0) != null && !(((SelectColumn)this.functionArguments.get(1)).getColumnExpression().get(0) instanceof FunctionCalls)) {
                        secArg = "DECIMAL(38,0)";
                     }

                     String qry = "";
                     Object obj = ((SelectColumn)this.functionArguments.get(0)).getColumnExpression().get(0);
                     if (obj instanceof FunctionCalls && ((FunctionCalls)obj).getFunctionName().getColumnName().equalsIgnoreCase("AES_DECRYPT")) {
                        qry = "CAST(" + firstArg + " AS " + secArg + ")";
                     } else {
                        qry = "IF(" + firstArg + " IS DECIMAL, CAST(" + firstArg + " AS " + secArg + "),0)";
                     }

                     this.functionName.setColumnName(qry);
                     this.setOpenBracesForFunctionNameRequired(false);
                     this.functionArguments = new Vector();
                  }
               } else {
                  firstArg = "";
                  secArg = arguments.get(0).toString();
                  Object obj = ((SelectColumn)this.functionArguments.get(0)).getColumnExpression().get(0);
                  if (obj instanceof FunctionCalls && ((FunctionCalls)obj).getFunctionName().getColumnName().equalsIgnoreCase("AES_DECRYPT")) {
                     firstArg = "CAST(" + secArg + " AS BIGINT)";
                  } else {
                     firstArg = "IF(" + secArg + " IS INTEGER, CAST(" + secArg + " AS BIGINT),0)";
                  }

                  this.functionName.setColumnName(firstArg);
                  this.setOpenBracesForFunctionNameRequired(false);
                  this.functionArguments = new Vector();
               }
            }
         } else {
            this.functionName.setColumnName("CAST(" + arguments.get(0).toString() + " AS VARCHAR)");
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         }

      }
   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            if (this.functionArguments.elementAt(i_count).toString().toLowerCase().startsWith("char(")) {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            } else {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toBigQuerySelect(to_sqs, from_sqs));
            }
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (this.functionName.getColumnName().equalsIgnoreCase("VARCHAR")) {
         this.functionName.setColumnName("CAST(" + arguments.get(0).toString() + " AS STRING)");
         this.setFunctionArguments(new Vector());
         this.setOpenBracesForFunctionNameRequired(false);
      } else {
         String qry = "";
         if (arguments.size() == 2) {
            if (!arguments.get(1).toString().equalsIgnoreCase("CURRENT_DATE") && !arguments.get(1).toString().equalsIgnoreCase("DATE")) {
               if (arguments.get(1).toString().equalsIgnoreCase("DATETIME")) {
                  qry = " cast(" + arguments.get(0) + " AS TIMESTAMP) ";
               } else if (!arguments.get(1).toString().equalsIgnoreCase("CURRENT_TIME") && !arguments.get(1).toString().equalsIgnoreCase("TIME")) {
                  String num;
                  if (!arguments.get(1).toString().equalsIgnoreCase("CHAR") && !arguments.get(1).toString().startsWith("CHR") && !arguments.get(1).toString().toLowerCase().startsWith("char")) {
                     if (!arguments.get(1).toString().replaceAll("\\s+", "").equalsIgnoreCase("SIGNEDASINTEGER") && !arguments.get(1).toString().equalsIgnoreCase("SIGNED") && !arguments.get(1).toString().equalsIgnoreCase("INT") && !arguments.get(1).toString().equalsIgnoreCase("INTEGER") && !arguments.get(1).toString().equalsIgnoreCase("NUMERIC")) {
                        if (!arguments.get(1).toString().equalsIgnoreCase("DECIMAL") && !arguments.get(1).toString().toUpperCase().startsWith("DECIMAL")) {
                           if (!arguments.get(1).toString().equalsIgnoreCase("\"BINARY\"")) {
                              throw new ConvertException("\nGiven function " + this.functionName.getColumnName() + " with " + this.functionArguments.size() + " arguments is not supported. \n Please ensure that the correct number of arguments are passed\n");
                           }

                           qry = "CAST(" + arguments.get(0) + " AS BINARY)";
                        } else if (arguments.get(1).toString().toUpperCase().startsWith("DECIMAL(")) {
                           num = ((FunctionCalls)((SelectColumn)this.functionArguments.get(1)).getColumnExpression().get(0)).getFunctionArguments().get(1).toString();
                           qry = "TRUNC(CAST(" + arguments.get(0).toString() + " AS FLOAT64), " + num + ")";
                        } else {
                           qry = "CAST(" + arguments.get(0).toString() + " AS FLOAT64 )";
                        }
                     } else {
                        qry = " cast(" + arguments.get(0) + " as INT64) ";
                     }
                  } else if (!arguments.get(1).toString().toLowerCase().startsWith("chr(") && !arguments.get(1).toString().toLowerCase().startsWith("char(")) {
                     qry = " cast(" + arguments.get(0) + " AS STRING) ";
                  } else {
                     num = arguments.get(1).toString().toLowerCase().replace("char(", "").replace(")", "").replace("chr(", "");
                     qry = "substr( CAST(" + arguments.get(0) + " AS STRING), 1, " + num + " )";
                  }
               } else {
                  qry = " cast(" + arguments.get(0) + " AS TIME) ";
               }
            } else {
               qry = " cast(" + arguments.get(0) + " AS DATE) ";
            }

            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         }

      }
   }
}
