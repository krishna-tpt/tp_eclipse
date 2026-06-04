package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.functions.misc.cast;
import com.adventnet.swissqlapi.sql.statement.create.NumericClass;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import java.util.Vector;

public class DecInt extends FunctionCalls {
   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      int argLength = this.functionArguments.size();
      String fnStr = this.functionName.getColumnName().toUpperCase();
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < argLength; ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (fnStr.equalsIgnoreCase("to_positive_integer")) {
         TableColumn absFunc = new TableColumn();
         absFunc.setColumnName("ABS");
         this.setFunctionName(absFunc);
         Vector fnArgs = new Vector();
         SelectColumn scFnAgrs = new SelectColumn();
         Vector scFnArgscolExp = new Vector();
         cast ct = new cast();
         TableColumn ctTblcol = new TableColumn();
         ctTblcol.setColumnName("CAST");
         ct.setFunctionName(ctTblcol);
         ct.setAsDatatype("AS");
         arguments.add("SIGNED");
         ct.setFunctionArguments(arguments);
         scFnArgscolExp.add(ct);
         scFnAgrs.setColumnExpression(scFnArgscolExp);
         fnArgs.add(scFnAgrs);
         this.setCustomDataType("POSITIVE_NUMBER");
         this.setFunctionArguments(fnArgs);
      } else {
         String type = this.functionName.getColumnName().equalsIgnoreCase("TO_INTEGER") ? "SIGNED" : "DECIMAL";
         Object col = arguments.elementAt(0);
         String precisionValue = "";
         String scaleValue = "";
         Vector vc_typeIn;
         if (fnStr.equalsIgnoreCase("TO_DECIMAL")) {
            int precision = 0;
            int scale = false;
            SelectColumn sc_scale;
            String scale_str;
            if (argLength > 1 && arguments.elementAt(1) != null && arguments.elementAt(1) instanceof SelectColumn) {
               sc_scale = (SelectColumn)arguments.elementAt(1);
               vc_typeIn = sc_scale.getColumnExpression();
               if (!(vc_typeIn.elementAt(0) instanceof String)) {
                  throw new ConvertException("Invalid Argument Value for Function" + fnStr, "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[]{fnStr, "PRECISION"});
               }

               scale_str = (String)vc_typeIn.elementAt(0);
               scale_str = scale_str.replaceAll("'", "");

               try {
                  precision = Integer.parseInt(scale_str);
               } catch (Exception var20) {
                  throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[]{fnStr, "PRECISION", "Provide values between 1 to 38"});
               }

               if (precision < 1 || precision > 38) {
                  throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[]{fnStr, "PRECISION", "Provide values between 1 to 38"});
               }
            }

            if (argLength > 2 && arguments.elementAt(2) != null && arguments.elementAt(2) instanceof SelectColumn) {
               sc_scale = (SelectColumn)arguments.elementAt(2);
               vc_typeIn = sc_scale.getColumnExpression();
               if (!(vc_typeIn.elementAt(0) instanceof String)) {
                  throw new ConvertException("Invalid Argument Value for Function" + fnStr, "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[]{fnStr, "SCALE"});
               }

               scale_str = (String)vc_typeIn.elementAt(0);
               scale_str = scale_str.replaceAll("'", "");

               int scale;
               try {
                  scale = Integer.parseInt(scale_str);
               } catch (Exception var19) {
                  throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[]{fnStr, "SCALE", "Provide values as the difference of precision value and length of numeric value"});
               }

               if (scale >= precision) {
                  throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[]{fnStr, "SCALE", "Provide values as the difference of precision value and length of numeric value"});
               }
            }
         }

         if (!this.functionName.getColumnName().equalsIgnoreCase("TO_CURRENCY") && !this.functionName.getColumnName().equalsIgnoreCase("TO_PERCENTAGE") && !this.functionName.getColumnName().equalsIgnoreCase("TO_DURATION")) {
            if (type.equalsIgnoreCase("DECIMAL")) {
               precisionValue = argLength >= 2 && arguments.elementAt(1) != null ? arguments.elementAt(1).toString() : "38";
               if (from_sqs.getBooleanValues("consider.precision.for.to.decimal")) {
                  precisionValue = "38";
               }

               scaleValue = argLength >= 3 && arguments.elementAt(2) != null ? arguments.elementAt(2).toString() : "2";
            }
         } else {
            precisionValue = "19";
            scaleValue = "4";
            if (this.functionName.getColumnName().equalsIgnoreCase("TO_CURRENCY")) {
               this.setCustomDataType("CURRENCY");
            } else if (this.functionName.getColumnName().equalsIgnoreCase("TO_PERCENTAGE")) {
               this.setCustomDataType("PERCENT");
            } else if (this.functionName.getColumnName().equalsIgnoreCase("TO_DURATION")) {
               this.setCustomDataType("DURATION");
               precisionValue = "23";
               scaleValue = "6";
               if (arguments.size() == 2) {
                  String durUnit = arguments.elementAt(1).toString().toLowerCase();
                  SelectColumn sc_dur = new SelectColumn();
                  Vector vc_dur = new Vector();
                  vc_dur.addElement(arguments.get(0));
                  if (durUnit.equals("day")) {
                     vc_dur.addElement("*86400");
                  } else if (durUnit.equals("hour")) {
                     vc_dur.addElement("*3600");
                  } else if (durUnit.equals("minute")) {
                     vc_dur.addElement("*60");
                  } else if (durUnit.equals("second")) {
                     vc_dur.addElement("");
                  } else if (durUnit.equals("millisecond")) {
                     vc_dur.addElement("*0.001");
                  } else {
                     if (!durUnit.equals("microsecond")) {
                        throw new ConvertException("Invalid Argument Value for Function TO_DURATION", "INVALID_ARGUMENT_VALUE", new Object[]{"TO_DURATION", "DURATION_UNITS", "Provide any one of the following value , DAY, HOUR, MINUTE, SECOND, MILLISECOND, MICROSECOND"});
                     }

                     vc_dur.addElement("*0.000001");
                  }

                  sc_dur.setColumnExpression(vc_dur);
                  arguments.clear();
                  arguments.add(sc_dur);
                  col = sc_dur;
               }
            }
         }

         this.functionName.setColumnName("CONVERT");
         arguments = new Vector();
         arguments.addElement(col);
         if (fnStr.equalsIgnoreCase("TO_INTEGER")) {
            arguments.addElement(type);
         } else {
            SelectColumn sc_type = new SelectColumn();
            FunctionCalls fn_type = new FunctionCalls();
            TableColumn tb_type = new TableColumn();
            tb_type.setColumnName(type);
            fn_type.setFunctionName(tb_type);
            vc_typeIn = new Vector();
            Vector vc_typeOut = new Vector();
            SelectColumn sc_precision = new SelectColumn();
            Vector vc_precision = new Vector();
            vc_precision.add(precisionValue);
            sc_precision.setColumnExpression(vc_precision);
            vc_typeIn.addElement(sc_precision);
            SelectColumn sc_scale = new SelectColumn();
            Vector vc_scale = new Vector();
            vc_scale.add(scaleValue);
            sc_scale.setColumnExpression(vc_scale);
            vc_typeIn.addElement(sc_scale);
            fn_type.setFunctionArguments(vc_typeIn);
            vc_typeOut.addElement(fn_type);
            sc_type.setColumnExpression(vc_typeOut);
            arguments.addElement(sc_type);
         }

         this.setFunctionArguments(arguments);
      }

   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      int argLength = this.functionArguments.size();
      Vector arguments = new Vector();
      this.functionArguments.setElementAt("(" + this.functionArguments.get(0) + "::text)", 0);

      for(int i_count = 0; i_count < argLength; ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      SelectColumn udfSC = new SelectColumn();
      Vector udfSCColExp = new Vector();
      FunctionCalls udf = new FunctionCalls();
      Vector udfArgs = new Vector();
      udfArgs.add(arguments.elementAt(0));
      udf.setFunctionArguments(udfArgs);
      udfSCColExp.addElement(udf);
      udfSC.setColumnExpression(udfSCColExp);
      if (this.functionName.getColumnName().equalsIgnoreCase("TO_DECIMAL")) {
         NumericClass decimalNC = new NumericClass();
         decimalNC.setDatatypeName("DECIMAL");
         decimalNC.setPrecision(argLength >= 2 && arguments.elementAt(1) != null ? arguments.elementAt(1).toString() : "38");
         decimalNC.setScale(argLength >= 3 && arguments.elementAt(2) != null ? arguments.elementAt(2).toString() : "2");
         decimalNC.setOpenBrace("(");
         decimalNC.setClosedBrace(")");
         udf.getFunctionName().setColumnName("todouble_udf");
         arguments = new Vector();
         arguments.add(udfSC);
         arguments.add(decimalNC);
      } else {
         udf.getFunctionName().setColumnName("tonumeric_udf");
         arguments = new Vector();
         arguments.add(udfSC);
         arguments.add("BIGINT");
      }

      this.functionName.setColumnName("cast");
      this.setAsDatatype("as");
      this.setFunctionArguments(arguments);
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      int argLength = this.functionArguments.size();
      StringBuffer[] argu = new StringBuffer[this.functionArguments.size()];

      for(int i_count = 0; i_count < argLength; ++i_count) {
         argu[i_count] = new StringBuffer();
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            argu[i_count].append(((SelectColumn)this.functionArguments.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
         } else {
            argu[i_count].append(this.functionArguments.elementAt(i_count));
         }
      }

      String qry = "";
      String col = argu[0].toString();
      if (this.functionName.getColumnName().equalsIgnoreCase("TO_DECIMAL")) {
         String precision = argLength >= 2 && argu[1].toString() != null ? argu[1].toString() : "38";
         String scale = argLength >= 3 && argu[2].toString() != null ? argu[2].toString() : "2";
         qry = "IF(" + col + " IS DECIMAL, CAST(" + col + " AS DECIMAL(" + precision + ", " + scale + ")),0)";
      } else {
         qry = "IF(" + col + " IS INTEGER, BIGINT(" + col + "),0) ";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }
}
