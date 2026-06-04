package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import java.util.Vector;

public class strsearch extends FunctionCalls {
   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnStr = this.functionName.getColumnName();
      this.functionName.setColumnName("IF");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (!fnStr.equalsIgnoreCase("ISSTARTSWITH") && !fnStr.equalsIgnoreCase("IS_STARTSWITH")) {
         if (!fnStr.equalsIgnoreCase("ISENDSWITH") && !fnStr.equalsIgnoreCase("IS_ENDSWITH")) {
            if (!fnStr.equalsIgnoreCase("ISCONTAINS") && !fnStr.equalsIgnoreCase("IS_CONTAINS")) {
               SelectColumn sc_len;
               Vector vc_len;
               Vector vc_datetime;
               if (!fnStr.equalsIgnoreCase("ISEMPTY") && !fnStr.equalsIgnoreCase("IS_EMPTY")) {
                  if (fnStr.equalsIgnoreCase("TO_STRING")) {
                     this.functionName.setColumnName("CONVERT");
                     vc_datetime = new Vector();
                     sc_len = new SelectColumn();
                     vc_len = new Vector();
                     String str_len = "";
                     int len = false;
                     vc_datetime.addElement(arguments.get(0));
                     if (this.functionArguments.size() > 1) {
                        if (arguments.elementAt(1) instanceof SelectColumn) {
                           SelectColumn sc = (SelectColumn)arguments.elementAt(1);
                           Vector vc = sc.getColumnExpression();
                           if (!(vc.elementAt(0) instanceof String)) {
                              throw new ConvertException("Invalid Argument Value for Function" + fnStr.toUpperCase(), "ONLY_SIMPLE_TYPE_ARGUMENT", new Object[]{fnStr.toUpperCase(), "STRING_LEN"});
                           }

                           str_len = (String)vc.elementAt(0);
                           str_len = str_len.replaceAll("'", "");
                           this.validateStringLength(str_len, fnStr.toUpperCase());
                        }
                     } else {
                        str_len = "100";
                     }

                     vc_len.addElement("char(" + str_len + ")");
                     sc_len.setColumnExpression(vc_len);
                     vc_datetime.addElement(sc_len);
                     this.setFunctionArguments(vc_datetime);
                  } else if (fnStr.equalsIgnoreCase("CONVERT_TO_DATETIME")) {
                     this.functionName.setColumnName("STR_TO_DATE");
                     vc_datetime = new Vector();
                     vc_datetime.addElement(arguments.get(0));
                     String str_format = "";
                     new SelectColumn();
                     if (arguments.elementAt(1) instanceof SelectColumn) {
                        SelectColumn sc = (SelectColumn)arguments.elementAt(1);
                        Vector vc = sc.getColumnExpression();
                        if (vc.elementAt(0) instanceof String) {
                           str_format = (String)vc.elementAt(0);
                        }

                        str_format = str_format.replaceAll("yyyy", "%Y");
                        str_format = str_format.replaceAll("yy", "%y");
                        str_format = str_format.replaceAll("MMMM", "%M");
                        str_format = str_format.replaceAll("MMM", "%b");
                        str_format = str_format.replaceAll("MM", "%m");
                        str_format = str_format.replaceAll("EEEE", "%W");
                        str_format = str_format.replaceAll("EEE", "%a");
                        str_format = str_format.replaceAll("dd", "%d");
                        str_format = str_format.replaceAll("HH", "%H");
                        str_format = str_format.replaceAll("hh", "%h");
                        str_format = str_format.replaceAll("mm", "%i");
                        str_format = str_format.replaceAll("ss", "%s");
                        str_format = str_format.replaceAll("SSS", "%f");
                        str_format = str_format.replaceAll("a", "%p");
                     }

                     vc_datetime.addElement(str_format);
                     this.setFunctionArguments(vc_datetime);
                  }
               } else {
                  this.functionName.setColumnName("IF");
                  vc_datetime = new Vector();
                  sc_len = new SelectColumn();
                  vc_len = new Vector();
                  WhereItem whIt_isEmpty = new WhereItem();
                  WhereColumn whCol_LeftExp = new WhereColumn();
                  Vector vc_leftExp = new Vector();
                  WhereColumn whCol_RightExp = new WhereColumn();
                  Vector vc_rightExp = new Vector();
                  SelectColumn sc_ifNull = new SelectColumn();
                  Vector vc_ifNullIn = new Vector();
                  Vector vc_ifNullOut = new Vector();
                  FunctionCalls fnCl_ifNull = new FunctionCalls();
                  TableColumn tbCl_ifNull = new TableColumn();
                  tbCl_ifNull.setColumnName("IFNULL");
                  fnCl_ifNull.setFunctionName(tbCl_ifNull);
                  SelectColumn sc_trim = new SelectColumn();
                  Vector vc_trimIn = new Vector();
                  Vector vc_trimOut = new Vector();
                  FunctionCalls fnCl_trim = new FunctionCalls();
                  TableColumn tbCl_trim = new TableColumn();
                  tbCl_trim.setColumnName("TRIM");
                  fnCl_trim.setFunctionName(tbCl_trim);
                  vc_trimIn.addElement(arguments.get(0));
                  fnCl_trim.setFunctionArguments(vc_trimIn);
                  vc_trimOut.addElement(fnCl_trim);
                  sc_trim.setColumnExpression(vc_trimOut);
                  vc_ifNullIn.addElement(sc_trim);
                  vc_ifNullIn.addElement("''");
                  fnCl_ifNull.setFunctionArguments(vc_ifNullIn);
                  vc_ifNullOut.addElement(fnCl_ifNull);
                  sc_ifNull.setColumnExpression(vc_ifNullOut);
                  vc_leftExp.addElement(sc_ifNull);
                  whCol_LeftExp.setColumnExpression(vc_leftExp);
                  vc_rightExp.addElement("''");
                  whCol_RightExp.setColumnExpression(vc_rightExp);
                  whIt_isEmpty.setLeftWhereExp(whCol_LeftExp);
                  whIt_isEmpty.setOperator("=");
                  whIt_isEmpty.setRightWhereExp(whCol_RightExp);
                  vc_len.addElement(whIt_isEmpty);
                  sc_len.setColumnExpression(vc_len);
                  SelectColumn sc_trueStmt = new SelectColumn();
                  Vector vc_trueStmt = new Vector();
                  vc_trueStmt.addElement("1");
                  sc_trueStmt.setColumnExpression(vc_trueStmt);
                  SelectColumn sc_falseStmt = new SelectColumn();
                  Vector vc_falseStmt = new Vector();
                  vc_falseStmt.addElement("0");
                  sc_falseStmt.setColumnExpression(vc_falseStmt);
                  vc_datetime.addElement(sc_len);
                  vc_datetime.addElement(sc_trueStmt);
                  vc_datetime.addElement(sc_falseStmt);
                  this.setFunctionArguments(vc_datetime);
               }
            } else {
               this.setFinalArguments(arguments, fnStr);
            }
         } else {
            this.setFinalArguments(arguments, fnStr);
         }
      } else {
         this.setFinalArguments(arguments, fnStr);
      }

   }

   public void setFinalArguments(Vector arguments, String funName) {
      Vector finalArguments = new Vector();
      String true_num = "1";
      String false_num = "0";
      SelectColumn sc_if = new SelectColumn();
      WhereItem wi_if = new WhereItem();
      Vector vc_if = new Vector();
      WhereColumn if_left = new WhereColumn();
      Vector vc_if_left = new Vector();
      WhereColumn if_right = new WhereColumn();
      Vector vc_if_right = new Vector();
      new SelectColumn();
      new Vector();
      new Vector();
      vc_if_left.addElement(arguments.get(0));
      if_left.setColumnExpression(vc_if_left);
      wi_if.setLeftWhereExp(if_left);
      wi_if.setOperator("like");
      StringBuilder strsb = new StringBuilder();
      boolean isString = false;
      SelectColumn sc_concat;
      Vector vc_concatIn;
      if (arguments.elementAt(1) instanceof SelectColumn) {
         sc_concat = (SelectColumn)arguments.elementAt(1);
         vc_concatIn = sc_concat.getColumnExpression();
         if (vc_concatIn.elementAt(0) instanceof String) {
            isString = true;
            String str = (String)vc_concatIn.elementAt(0);
            if (str.startsWith("'") && str.endsWith("'")) {
               str = str.substring(1, str.length() - 1);
            }

            if (!funName.equalsIgnoreCase("ISENDSWITH") && !funName.equalsIgnoreCase("IS_ENDSWITH")) {
               if (!funName.equalsIgnoreCase("ISSTARTSWITH") && !funName.equalsIgnoreCase("IS_STARTSWITH")) {
                  if (funName.equalsIgnoreCase("ISCONTAINS") || funName.equalsIgnoreCase("IS_CONTAINS")) {
                     strsb.append("'%");
                     strsb.append(str);
                     strsb.append("%'");
                  }
               } else {
                  strsb.append("'");
                  strsb.append(str);
                  strsb.append("%'");
               }
            } else {
               strsb.append("'%");
               strsb.append(str);
               strsb.append("'");
            }
         }
      }

      if (!(arguments.elementAt(1) instanceof String) && !isString) {
         sc_concat = new SelectColumn();
         vc_concatIn = new Vector();
         Vector vc_concatOut = new Vector();
         FunctionCalls fn_concat = new FunctionCalls();
         TableColumn tb_concat = new TableColumn();
         tb_concat.setColumnName("CONCAT");
         fn_concat.setFunctionName(tb_concat);
         if (!funName.equalsIgnoreCase("ISCONTAINS") && !funName.equalsIgnoreCase("IS_CONTAINS")) {
            if (!funName.equalsIgnoreCase("ISSTARTSWITH") && !funName.equalsIgnoreCase("IS_STARTSWITH")) {
               if (funName.equalsIgnoreCase("ISENDSWITH") || funName.equalsIgnoreCase("IS_ENDSWITH")) {
                  vc_concatIn.add("'%'");
                  vc_concatIn.addElement(arguments.get(1));
               }
            } else {
               vc_concatIn.addElement(arguments.get(1));
               vc_concatIn.add("'%'");
            }
         } else {
            vc_concatIn.add("'%'");
            vc_concatIn.addElement(arguments.get(1));
            vc_concatIn.add("'%'");
         }

         fn_concat.setFunctionArguments(vc_concatIn);
         vc_concatOut.addElement(fn_concat);
         sc_concat.setColumnExpression(vc_concatOut);
         vc_if_right.addElement(sc_concat);
      } else {
         sc_concat = new SelectColumn();
         vc_concatIn = new Vector();
         vc_concatIn.addElement(strsb);
         sc_concat.setColumnExpression(vc_concatIn);
         vc_if_right.addElement(sc_concat);
      }

      if_right.setColumnExpression(vc_if_right);
      wi_if.setRightWhereExp(if_right);
      vc_if.addElement(wi_if);
      sc_if.setColumnExpression(vc_if);
      finalArguments.addElement(sc_if);
      finalArguments.addElement(true_num);
      finalArguments.addElement(false_num);
      this.setFunctionArguments(finalArguments);
   }
}
