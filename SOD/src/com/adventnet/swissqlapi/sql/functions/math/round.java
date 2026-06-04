package com.adventnet.swissqlapi.sql.functions.math;

import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.create.NumericClass;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.util.misc.CastingUtil;
import java.util.Vector;

public class round extends FunctionCalls {
   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("ROUND");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }

         if (i_count == 2) {
            String value = arguments.get(2).toString().trim();
            if (!value.equals("0")) {
               this.functionName.setColumnName("TRUNC");
            }

            arguments.removeElementAt(2);
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("ROUND");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() == 1) {
         this.functionName.setColumnName("CAST");
         Vector newArg = new Vector();
         StringBuffer argBuffer = new StringBuffer();
         argBuffer.append("ROUND(");
         argBuffer.append(this.functionArguments.get(0).toString());
         argBuffer.append(",0) AS INTEGER");
         newArg.add(argBuffer.toString());
         this.setFunctionArguments(newArg);
      }

   }

   public void toSybase(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("ROUND");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSybaseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() == 1) {
         this.functionName.setColumnName("CONVERT");
         Vector newArg = new Vector();
         StringBuffer argBuffer = new StringBuffer();
         argBuffer.append("INTEGER,ROUND(");
         argBuffer.append(this.functionArguments.get(0).toString());
         argBuffer.append(",0)");
         newArg.add(argBuffer.toString());
         this.setFunctionArguments(newArg);
      }

   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("ROUND");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toDB2Select(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() == 1) {
         this.functionName.setColumnName("CAST");
         Vector newArg = new Vector();
         StringBuffer argBuffer = new StringBuffer();
         argBuffer.append("ROUND(");
         argBuffer.append(this.functionArguments.get(0).toString());
         argBuffer.append(",0) AS INTEGER");
         newArg.add(argBuffer.toString());
         this.setFunctionArguments(newArg);
      }

   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("ROUND");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      Vector vc;
      String roundNum;
      SelectColumn sc;
      if (arguments.elementAt(0) instanceof SelectColumn) {
         sc = (SelectColumn)arguments.elementAt(0);
         vc = sc.getColumnExpression();
         if (vc.elementAt(0) instanceof String) {
            roundNum = (String)vc.elementAt(0);
            roundNum = roundNum.replaceAll("'", "");

            try {
               Double.parseDouble(roundNum);
               vc.setElementAt(roundNum, 0);
            } catch (Exception var9) {
            }
         }
      }

      if (arguments.size() > 1 && arguments.elementAt(1) instanceof SelectColumn) {
         sc = (SelectColumn)arguments.elementAt(1);
         vc = sc.getColumnExpression();
         if (vc.elementAt(0) instanceof String) {
            roundNum = (String)vc.elementAt(0);
            roundNum = roundNum.replaceAll("'", "");

            try {
               Integer.parseInt(roundNum);
               vc.setElementAt(roundNum, 0);
            } catch (Exception var8) {
            }
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      boolean addToDecimalWrap = false;
      this.functionName.setColumnName("ROUND");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      SelectColumn sc;
      try {
         String scale = "";
         if (arguments.size() > 1) {
            if (arguments.get(1) instanceof SelectColumn) {
               sc = (SelectColumn)arguments.get(1);
               Vector ce = sc.getColumnExpression();
               if (ce.size() == 1 && ce.get(0) instanceof String) {
                  scale = ce.get(0).toString();
               } else if (ce.size() == 2 && ce.get(0) instanceof String && ce.get(1) instanceof String) {
                  scale = ce.get(0).toString() + ce.get(1).toString();
               }
            }

            if (!scale.equalsIgnoreCase("") && Double.parseDouble(scale) >= 0.0D) {
               addToDecimalWrap = true;
            }
         }
      } catch (NumberFormatException var15) {
         addToDecimalWrap = false;
      }

      if (!addToDecimalWrap) {
         this.setFunctionArguments(arguments);
      } else {
         this.functionName.setColumnName("CONVERT");
         Vector vc_convertIn = new Vector();
         sc = new SelectColumn();
         FunctionCalls fn_type = new FunctionCalls();
         TableColumn tb_type = new TableColumn();
         tb_type.setColumnName("DECIMAL");
         fn_type.setFunctionName(tb_type);
         Vector vc_typeIn = new Vector();
         Vector vc_typeOut = new Vector();
         SelectColumn sc_precision = new SelectColumn();
         Vector vc_precision = new Vector();
         vc_precision.add("38");
         sc_precision.setColumnExpression(vc_precision);
         vc_typeIn.addElement(sc_precision);
         SelectColumn sc_scale = new SelectColumn();
         Vector vc_scale = new Vector();
         vc_scale.add(arguments.get(1));
         sc_scale.setColumnExpression(vc_scale);
         vc_typeIn.addElement(sc_scale);
         fn_type.setFunctionArguments(vc_typeIn);
         vc_typeOut.addElement(fn_type);
         sc.setColumnExpression(vc_typeOut);
         vc_convertIn.addElement(arguments.get(0));
         vc_convertIn.addElement(sc);
         this.setFunctionArguments(vc_convertIn);
      }

   }

   public void toANSISQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("ROUND");
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
      this.functionName.setColumnName("ROUND");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toTeradataSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      boolean isDateArg;
      if (arguments.size() == 1) {
         isDateArg = false;
         Object obj = arguments.firstElement();
         if (obj instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)obj;

            for(int k = 0; k < sc.getColumnExpression().size(); ++k) {
               Object scObj = sc.getColumnExpression().get(k);
               if (scObj instanceof TableColumn) {
                  TableColumn tc = (TableColumn)scObj;
                  if (CastingUtil.getValueIgnoreCase(SwisSQLAPI.columnDatatypes, tc.getColumnName()) != null && CastingUtil.getValueIgnoreCase(SwisSQLAPI.columnDatatypes, tc.getColumnName()).toString().equalsIgnoreCase("timestamp")) {
                     isDateArg = true;
                  }

                  if (tc.getColumnName().toLowerCase().startsWith("current_date") || tc.getColumnName().toLowerCase().startsWith("current_time")) {
                     isDateArg = true;
                  }
               } else if (scObj instanceof FunctionCalls) {
                  FunctionCalls dateFunc = (FunctionCalls)scObj;
                  if (dateFunc.getFunctionName() != null) {
                     String returnType = SwisSQLUtils.getFunctionReturnType(dateFunc.getFunctionName().getColumnName(), dateFunc.getFunctionArguments());
                     if (returnType.equalsIgnoreCase("date") || returnType.equalsIgnoreCase("timestamp")) {
                        isDateArg = true;
                     }
                  }
               }
            }

            if (isDateArg && sc.getColumnExpression().size() > 1) {
               sc.getColumnExpression().add("DAY(4)");
               this.functionName.setColumnName("");
            } else if (!isDateArg) {
               this.functionName.setColumnName("CAST");
               NumericClass nc = new NumericClass();
               this.setAsDatatype("AS");
               nc.setDatatypeName("DECIMAL");
               nc.setOpenBrace("(");
               nc.setPrecision("38");
               nc.setScale("0");
               nc.setClosedBrace(")");
               arguments.add(nc);
               this.setFunctionArguments(arguments);
            }
         }
      } else if (arguments.size() == 2) {
         isDateArg = false;

         try {
            Integer.parseInt(arguments.get(1).toString());
            isDateArg = true;
         } catch (NumberFormatException var11) {
         }

         if (isDateArg) {
            this.functionName.setColumnName("CAST");
            NumericClass nc = new NumericClass();
            this.setAsDatatype("AS");
            nc.setDatatypeName("DECIMAL");
            nc.setOpenBrace("(");
            nc.setPrecision("38");
            String prec = arguments.get(1).toString();
            nc.setScale(this.getModifiedPrecision(prec));
            nc.setClosedBrace(")");
            arguments.setElementAt(nc, 1);
            this.setFunctionArguments(arguments);
         }
      }

   }

   public String getModifiedPrecision(String num) {
      try {
         int number = Integer.parseInt(num);
         if (number > 22) {
            number = 22;
         }

         return "" + number;
      } catch (NumberFormatException var3) {
         return num;
      }
   }

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("ROUND");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toInformixSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() == 1) {
         this.functionName.setColumnName("CAST");
         Vector newArg = new Vector();
         StringBuffer argBuffer = new StringBuffer();
         argBuffer.append("ROUND(");
         argBuffer.append(this.functionArguments.get(0).toString());
         argBuffer.append(",0) AS INTEGER");
         newArg.add(argBuffer.toString());
         this.setFunctionArguments(newArg);
      }

   }

   public void toTimesTen(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      throw new ConvertException("\nThe function ROUND is not supported in TimesTen 5.1.21\n");
   }

   public void toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("ROUND");
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
      StringBuffer arguments = new StringBuffer();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (i_count > 0) {
            arguments.append(",");
         }

         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.append(((SelectColumn)this.functionArguments.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
         } else {
            arguments.append(this.functionArguments.elementAt(i_count));
         }
      }

      String argument = "";
      if (this.functionArguments.size() < 2) {
         argument = "cast(round(" + arguments + ",0) as bigint)";
      } else {
         argument = "round(" + arguments + ")";
      }

      this.functionName.setColumnName(argument);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("ROUND");
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

   public void toAthena(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("ROUND");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toAthenaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (this.functionArguments.size() < 2) {
         arguments.set(0, "CAST(ROUND(" + arguments.get(0).toString() + ") AS BIGINT)");
      }

      this.setFunctionArguments(arguments);
   }

   public void toSapHana(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("ROUND");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSapHanaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toSqlite(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("ROUND");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSqliteSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toExcel(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("Round");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toExcelSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toMsAccess(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("ROUND");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMsAccessJdbcSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (arguments.size() < 2) {
         arguments.addElement("0");
      }

      this.setFunctionArguments(arguments);
   }
}
