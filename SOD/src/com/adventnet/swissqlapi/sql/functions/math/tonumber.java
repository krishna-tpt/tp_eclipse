package com.adventnet.swissqlapi.sql.functions.math;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.create.NumericClass;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.util.database.MetadataInfoUtil;
import java.util.Vector;

public class tonumber extends FunctionCalls {
   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("TO_NUMBER");
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
      this.functionName.setColumnName("CONVERT");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (arguments.size() == 1) {
         this.functionArguments.add(this.functionArguments.get(0));
         int scale;
         if (this.functionArguments.get(0) instanceof SelectColumn) {
            SelectColumn fSc = (SelectColumn)this.functionArguments.get(0);
            Vector fScVec = fSc.getColumnExpression();
            scale = fScVec.size();

            for(int v = 0; v < scale; ++v) {
               Object fScVecArg = fScVec.elementAt(v);
               String arg;
               if (fScVecArg instanceof TableColumn) {
                  arg = MetadataInfoUtil.getDatatypeName(from_sqs, (TableColumn)fScVecArg);
                  if (arg != null && arg.indexOf("(") != -1) {
                     String dtypeSize = arg.substring(arg.indexOf("(") + 1, arg.indexOf(")"));
                     if (dtypeSize.indexOf(",") == -1 && Integer.parseInt(dtypeSize) > 38) {
                        dtypeSize = "38";
                     }

                     arguments.setElementAt("NUMERIC(" + dtypeSize + ")", 0);
                  } else {
                     arguments.setElementAt("NUMERIC(8, 2)", 0);
                  }
               } else if (!(fScVecArg instanceof String)) {
                  arguments.setElementAt("NUMERIC(8, 2)", 0);
               } else {
                  arg = fScVecArg.toString();
                  int argLen;
                  if (arg.indexOf(".") != -1) {
                     argLen = arg.length() - 1;
                     if (arg.startsWith("'") || arg.startsWith("\"")) {
                        argLen -= 2;
                     }

                     int scale = arg.substring(arg.indexOf("."), argLen + 1).length();
                     arguments.setElementAt("NUMERIC(" + argLen + ", " + scale + ")", 0);
                  } else {
                     argLen = arg.length();
                     if (arg.startsWith("'") || arg.startsWith("\"")) {
                        argLen -= 2;
                     }

                     arguments.setElementAt("NUMERIC(" + argLen + ")", 0);
                  }
               }
            }
         } else if (this.functionArguments.get(0) instanceof String) {
            String arg = this.functionArguments.get(0).toString();
            if (arg.startsWith("'")) {
               int argLength = arg.length() - 2;
               if (arg.indexOf(".") != -1) {
                  argLength = arg.length() - 1;
                  scale = arg.substring(arg.indexOf("."), argLength + 1).length();
                  arguments.setElementAt("NUMERIC(" + argLength + ", " + scale + ")", 0);
               } else {
                  arguments.setElementAt("NUMERIC(" + argLength + ")", 0);
               }
            } else {
               arguments.setElementAt("NUMERIC(8, 2)", 0);
            }
         } else {
            arguments.setElementAt("NUMERIC(8, 2)", 0);
         }
      } else if (arguments.size() > 1) {
         this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
         this.functionArguments.setElementAt("NUMERIC(8, 2)", 0);
      }

   }

   public void toSybase(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("CONVERT");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSybaseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (arguments.size() == 1) {
         this.functionArguments.add(this.functionArguments.get(0));
         int scale;
         if (this.functionArguments.get(0) instanceof SelectColumn) {
            SelectColumn fSc = (SelectColumn)this.functionArguments.get(0);
            Vector fScVec = fSc.getColumnExpression();
            scale = fScVec.size();

            for(int v = 0; v < scale; ++v) {
               Object fScVecArg = fScVec.elementAt(v);
               String arg;
               if (fScVecArg instanceof TableColumn) {
                  arg = MetadataInfoUtil.getDatatypeName(from_sqs, (TableColumn)fScVecArg);
                  if (arg != null && arg.indexOf("(") != -1) {
                     String dtypeSize = arg.substring(arg.indexOf("(") + 1, arg.indexOf(")"));
                     if (dtypeSize.indexOf(",") == -1 && Integer.parseInt(dtypeSize) > 38) {
                        dtypeSize = "38";
                     }

                     arguments.setElementAt("NUMERIC(" + dtypeSize + ")", 0);
                  } else {
                     arguments.setElementAt("NUMERIC(8, 2)", 0);
                  }
               } else if (!(fScVecArg instanceof String)) {
                  arguments.setElementAt("NUMERIC(8, 2)", 0);
               } else {
                  arg = fScVecArg.toString();
                  int argLen;
                  if (arg.indexOf(".") != -1) {
                     argLen = arg.length() - 1;
                     if (arg.startsWith("'") || arg.startsWith("\"")) {
                        argLen -= 2;
                     }

                     int scale = arg.substring(arg.indexOf("."), argLen + 1).length();
                     arguments.setElementAt("NUMERIC(" + argLen + ", " + scale + ")", 0);
                  } else {
                     argLen = arg.length();
                     if (arg.startsWith("'") || arg.startsWith("\"")) {
                        argLen -= 2;
                     }

                     arguments.setElementAt("NUMERIC(" + argLen + ")", 0);
                  }
               }
            }
         } else if (this.functionArguments.get(0) instanceof String) {
            String arg = this.functionArguments.get(0).toString();
            if (arg.startsWith("'")) {
               if (arg.indexOf(".") != -1) {
                  int argLength = arg.length() - 1;
                  if (arg.startsWith("'") || arg.startsWith("\"")) {
                     argLength -= 2;
                  }

                  scale = arg.substring(arg.indexOf("."), argLength + 1).length();
                  arguments.setElementAt("NUMERIC(" + argLength + ", " + scale + ")", 0);
               } else {
                  arguments.setElementAt("NUMERIC(" + arg.length() + ")", 0);
               }
            } else {
               arguments.setElementAt("NUMERIC(8, 2)", 0);
            }
         } else {
            arguments.setElementAt("NUMERIC(8, 2)", 0);
         }
      } else if (arguments.size() > 1) {
         this.functionArguments.setElementAt(this.functionArguments.get(0), 1);
         this.functionArguments.setElementAt("NUMERIC(8, 2)", 0);
      }

   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("TO_NUMBER");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toDB2Select(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      NumericClass nc;
      if (arguments.size() == 1) {
         this.functionName.setColumnName("CAST");
         nc = new NumericClass();
         nc.setDatatypeName("NUMERIC");
         nc.setClosedBrace(")");
         nc.setOpenBrace("(");
         nc.setScale("2");
         nc.setPrecision("22");
         this.setAsDatatype("AS");
         arguments.add(nc);
      } else if (arguments.size() == 2) {
         this.functionName.setColumnName("CAST");
         nc = new NumericClass();
         nc.setDatatypeName("NUMERIC");
         nc.setClosedBrace(")");
         nc.setOpenBrace("(");
         nc.setScale("16");
         nc.setPrecision("22");
         this.setAsDatatype("AS");
         arguments.set(1, nc);
      }

      this.setFunctionArguments(arguments);
   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("TO_NUMBER");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (arguments.size() == 1) {
         this.functionName.setColumnName("CAST");
         NumericClass nc = new NumericClass();
         nc.setDatatypeName("NUMERIC");
         nc.setClosedBrace(")");
         nc.setOpenBrace("(");
         nc.setScale("16");
         nc.setPrecision("22");
         this.setAsDatatype("AS");
         arguments.add(nc);
      }

      this.setFunctionArguments(arguments);
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (arguments.size() == 1 && arguments.get(0) instanceof SelectColumn) {
         this.functionName.setColumnName("");
         Vector v = ((SelectColumn)arguments.get(0)).getColumnExpression();
         v.add("*1");
      }

      this.setFunctionArguments(arguments);
   }

   public void toANSISQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("TO_NUMBER");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toANSISelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (arguments.size() == 1) {
         this.functionName.setColumnName("CAST");
         NumericClass nc = new NumericClass();
         nc.setDatatypeName("NUMERIC");
         this.setAsDatatype("AS");
         arguments.add(nc);
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

      if (arguments.size() > 0) {
         if (arguments.get(0) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)arguments.get(0);
            Vector columnExp = sc.getColumnExpression();
            columnExp.add("::");
            columnExp.add("NUMERIC (22, 16)");
         }

         if (arguments.size() == 2) {
            arguments.removeElementAt(1);
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("TO_NUMBER");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toNetezzaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (arguments.size() == 1) {
         this.functionName.setColumnName("CAST");
         NumericClass nc = new NumericClass();
         nc.setDatatypeName("NUMERIC");
         this.setAsDatatype("AS");
         arguments.add(nc);
      }

      this.setFunctionArguments(arguments);
   }

   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            try {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toTeradataSelect(to_sqs, from_sqs));
            } catch (ConvertException var6) {
               throw var6;
            }
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (arguments.size() == 3) {
         arguments.removeElementAt(2);
      }

      if (arguments.size() == 2) {
         arguments.removeElementAt(1);
      }

      if (arguments.size() == 1) {
         this.functionName.setColumnName("CAST");
         NumericClass nc = new NumericClass();
         nc.setDatatypeName("NUMERIC");
         if (this.functionArguments.get(0) instanceof SelectColumn) {
            String[] precScale = this.getPrecisionAndScale(from_sqs, (SelectColumn)this.functionArguments.elementAt(0));
            nc.setOpenBrace("(");
            nc.setPrecision(precScale[0]);
            nc.setScale(precScale[1]);
            nc.setClosedBrace(")");
         } else {
            nc.setOpenBrace("(");
            nc.setPrecision("38");
            nc.setScale("16");
            nc.setClosedBrace(")");
         }

         this.setAsDatatype("AS");
         arguments.add(nc);
      }

      this.setFunctionArguments(arguments);
   }

   private String[] getPrecisionAndScale(SelectQueryStatement from_sqs, SelectColumn numberArg) {
      String[] precScale = new String[2];
      Vector columnExpression = numberArg.getColumnExpression();
      int colExpSize = columnExpression.size();

      for(int i = 0; i < colExpSize; ++i) {
         Object colExprElement = columnExpression.elementAt(i);
         if (colExprElement instanceof TableColumn) {
            TableColumn tcn = (TableColumn)colExprElement;
            String datatype = MetadataInfoUtil.getDatatypeName(from_sqs, tcn);
            if (datatype != null) {
               int commaIndex = datatype.indexOf(",");
               if (commaIndex != -1) {
                  int openBraceIndex = datatype.indexOf("(");
                  int closeBraceIndex = datatype.indexOf(")");
                  precScale[0] = datatype.substring(openBraceIndex + 1, commaIndex);
                  precScale[1] = datatype.substring(commaIndex + 1, closeBraceIndex);
               } else {
                  precScale[0] = "38";
                  precScale[1] = "0";
               }
            }
         } else if (colExprElement instanceof String) {
            String s_ce = (String)colExprElement;
            int dotIndex = s_ce.indexOf(".");
            if (dotIndex != -1) {
               if (s_ce.startsWith("'")) {
                  precScale[0] = "" + s_ce.substring(1, s_ce.length() - 1).length();
                  precScale[1] = "" + s_ce.substring(dotIndex + 1, s_ce.length() - 1).length();
               } else {
                  precScale[0] = "" + s_ce.length();
                  precScale[1] = "" + s_ce.substring(dotIndex + 1, s_ce.length()).length();
               }
            } else {
               precScale[0] = "38";
               precScale[1] = "0";
            }
         }
      }

      if (precScale[0] == null) {
         precScale[0] = "38";
      }

      if (precScale[1] == null) {
         precScale[1] = "16";
      }

      return precScale;
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      throw new ConvertException("\nGiven function " + this.functionName.getColumnName() + "is not supported in VectorWise\n");
   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("TO_NUMBER");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toBigQuerySelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (arguments.size() == 1) {
         this.functionName.setColumnName("CAST");
         NumericClass nc = new NumericClass();
         nc.setDatatypeName("NUMERIC");
         this.setAsDatatype("AS");
         arguments.add(nc);
      }

      this.setFunctionArguments(arguments);
   }
}
