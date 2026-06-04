package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import java.util.Vector;

public class ConvertStringToDuration extends FunctionCalls {
   public static final String DUR_FORMAT_1_WITHOUT_FRAC_SEC = "%D days %H:%m:%s";
   public static final String DUR_FORMAT_2_WITHOUT_FRAC_SEC = "%D %H:%m:%s";
   public static final String DUR_FORMAT_3_WITHOUT_FRAC_SEC = "%D.%H:%m:%s";
   public static final String DUR_FORMAT_4_WITHOUT_FRAC_SEC = "%D days %H hrs %m mins %s secs";
   public static final String DUR_FORMAT_4_WITH_MILLISEC = "%D days %H hrs %m mins %s.%SSS secs";
   public static final String DUR_FORMAT_4_WITH_MICROSEC = "%D days %H hrs %m mins %s.%SSSSSS secs";
   public static final String DUR_FORMAT_HHH_WITHOUT_FRAC_SEC = "%H:%m:%s";
   public static final String DUR_FORMAT_HHH_1_WITHOUT_FRAC_SEC = "%H hrs %m mins %s secs";
   public static final String DUR_FORMAT_HHH_1_WITH_MILLISEC = "%H hrs %m mins %s.%SSS secs";
   public static final String DUR_FORMAT_HHH_1_WITH_MICROSEC = "%H hrs %m mins %s.%SSSSSS secs";
   public static final String DURATION_IN_SECONDS_WITHOUT_FRAC_SEC = "%s";
   public static final String DURATION_IN_SECONDS_WITH_MILLISEC = "%s.%SSS";
   public static final String DURATION_IN_SECONDS_WITH_MICROSEC = "%s.%SSSSSS";
   public static final String DURATION_IN_DAYS_ONLY = "%D days";
   public static final String DURATION_IN_HOURS_ONLY = "%H hrs";
   public static final String DURATION_IN_MINS_ONLY = "%m mins";
   public static final String DURATION_IN_SEC = "%s secs";
   public static final String DURATION_IN_SEC_WITH_MILLISEC = "%s.%SSS secs";
   public static final String DURATION_IN_SEC_WITH_MICROSEC = "%s.%SSSSSS secs";
   String dayStr = "'days'";
   String hrStr = "'hrs'";
   String minStr = "'mins'";
   String secStr = "'secs'";

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String format = ((SelectColumn)this.functionArguments.get(1)).getColumnExpression().get(0).toString();
      format = format.substring(1, format.length() - 1);
      if (from_sqs != null && from_sqs.isMySqlLive()) {
         String qry = "";
         if (!format.equals("%s") && !format.equals("%s.%SSSSSS") && !format.equals("%s.%SSS")) {
            if (format.equals("%D days")) {
               qry = "(convert(replace(replace(" + this.functionArguments.get(0) + "," + this.dayStr + ",''),' ',''), DECIMAL(23,6))*86400)";
            } else if (format.equals("%H hrs")) {
               qry = "(convert(replace(replace(" + this.functionArguments.get(0) + "," + this.hrStr + ",''),' ',''), DECIMAL(23,6))*3600)";
            } else if (format.equals("%m mins")) {
               qry = "(convert(replace(replace(" + this.functionArguments.get(0) + "," + this.minStr + ",''),' ',''), DECIMAL(23,6))*60)";
            } else if (!format.equals("%s secs") && !format.equals("%s.%SSSSSS secs") && !format.equals("%s.%SSS secs")) {
               if (!format.startsWith("%H:%m:%s") && !format.equals("%H hrs %m mins %s secs") && !format.equals("%H hrs %m mins %s.%SSSSSS secs") && !format.equals("%H hrs %m mins %s.%SSS secs")) {
                  if (!format.startsWith("%D days %H:%m:%s") && !format.startsWith("%D %H:%m:%s") && !format.startsWith("%D.%H:%m:%s")) {
                     if (!format.equals("%D days %H hrs %m mins %s secs") && !format.equals("%D days %H hrs %m mins %s.%SSSSSS secs") && !format.equals("%D days %H hrs %m mins %s.%SSS secs")) {
                        throw new ConvertException("Unsupported duration format is given :" + this.functionArguments.get(1));
                     }

                     qry = "((if(locate('-',trim(" + this.functionArguments.get(0) + "))=1,-1,1)) * ((convert(abs(replace(SUBSTRING_INDEX(" + this.functionArguments.get(0) + ", 'days', 1),' ', '')),DECIMAL(23,6))*86400)+(convert(SUBSTRING_INDEX(SUBSTRING_INDEX(" + this.functionArguments.get(0) + ", 'hrs', 1),'days',-1) ,DECIMAL(23,6))*3600)+(convert(SUBSTRING_INDEX(SUBSTRING_INDEX(" + this.functionArguments.get(0) + ", 'mins', 1),'hrs',-1) ,DECIMAL(23,6))*60)+(convert(SUBSTRING_INDEX(SUBSTRING_INDEX(" + this.functionArguments.get(0) + ", 'secs', 1),'mins',-1),DECIMAL(23,6)))))";
                  } else {
                     qry = "((if(locate('-',trim(" + this.functionArguments.get(0) + "))=1,-1,1)) * ((abs(convert(replace(SUBSTRING_INDEX(" + this.functionArguments.get(0) + ", 'days', 1),' ',''),DECIMAL(23,6)))*86400)+(convert(SUBSTRING_INDEX(SUBSTRING_INDEX(" + this.functionArguments.get(0) + ", ':', 1),' ',-1) ,DECIMAL(23,6))*3600)+(convert(SUBSTRING_INDEX(SUBSTRING_INDEX(" + this.functionArguments.get(0) + ", ':', -2),':',1) ,DECIMAL(23,6))*60)+(convert(SUBSTRING_INDEX(" + this.functionArguments.get(0) + ", ':', -1),DECIMAL(23,6)))))";
                  }
               } else {
                  qry = "((if(locate('-',trim(" + this.functionArguments.get(0) + "))=1,-1,1))*((convert(abs(SUBSTRING_INDEX(" + this.functionArguments.get(0) + ", ':', 1)) ,DECIMAL(23,6))*3600)+(convert(SUBSTRING_INDEX(SUBSTRING_INDEX(" + this.functionArguments.get(0) + ", ':', -2),':',1) ,DECIMAL(23,6))*60)+(convert(SUBSTRING_INDEX(" + this.functionArguments.get(0) + ", ':', -1),DECIMAL(23,6)))))";
               }
            } else {
               qry = "(convert(replace(replace(" + this.functionArguments.get(0) + "," + this.secStr + ",''),' ',''), DECIMAL(23,6)))";
            }
         } else {
            qry = "(convert(" + this.functionArguments.get(0) + ", DECIMAL(23,6)))";
         }

         this.functionName.setColumnName(qry);
         this.setFunctionArguments(new Vector());
         this.setOpenBracesForFunctionNameRequired(false);
      } else {
         Vector arguments = new Vector();
         Vector arguments1 = new Vector();

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
               arguments1.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
               arguments1.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         String fmt = ((SelectColumn)arguments.get(1)).getColumnExpression().get(0).toString();
         this.functionName.setColumnName("");
         Vector vc;
         SelectColumn sc;
         Vector vc_sc;
         FunctionCalls fc;
         TableColumn tc;
         Vector fcArgs;
         SelectColumn sc0;
         Vector vc_sc0;
         Vector fc2Args;
         SelectColumn sc_fc1_arg0;
         Vector fc3Args;
         if (!format.equals("%s") && !format.equals("%s.%SSSSSS") && !format.equals("%s.%SSS")) {
            Vector vc_whereColLft;
            SelectColumn sc_incoming_arg1;
            Vector vc_whereColRgt;
            SelectColumn sc_incoming_arg2;
            Vector vc_wc1;
            SelectColumn sc2;
            Vector vc_sc2;
            SelectColumn sc3;
            Vector vc_sc3;
            SelectColumn fc2_args_vc_2_sc;
            Vector fc2_args_vc_2_sc_vc;
            SelectColumn fc_arg_1_sc;
            Vector fc_arg_1_sc_vc;
            FunctionCalls fc3;
            TableColumn tc3;
            Vector vc_sc1_fc2Args;
            SelectColumn sc2_fc2Args;
            Vector vc_sc2_fc2Args;
            SelectColumn sc2;
            Vector vc_sc2;
            SelectColumn sc_col_exp;
            Vector vc_fc;
            FunctionCalls fc;
            TableColumn tc;
            SelectColumn sc1;
            Vector vc_sc1;
            FunctionCalls fc1;
            TableColumn tc1;
            Vector fc1Args;
            Vector vc_sc_fc1_arg0;
            FunctionCalls funcall3;
            TableColumn tabcol3;
            if (format.equals("%D days")) {
               vc = new Vector();
               sc = new SelectColumn();
               vc_sc = new Vector();
               sc_col_exp = new SelectColumn();
               sc_col_exp.setOpenBrace("(");
               vc_fc = new Vector();
               fc = new FunctionCalls();
               tc = new TableColumn();
               tc.setColumnName("convert");
               fc.setFunctionName(tc);
               vc_sc0 = new Vector();
               sc1 = new SelectColumn();
               vc_sc1 = new Vector();
               fc1 = new FunctionCalls();
               tc1 = new TableColumn();
               tc1.setColumnName("replace");
               fc1.setFunctionName(tc1);
               vc_sc1.addElement(fc1);
               fc1Args = new Vector();
               fc1.setFunctionArguments(fc1Args);
               sc_fc1_arg0 = new SelectColumn();
               vc_sc_fc1_arg0 = new Vector();
               funcall3 = new FunctionCalls();
               tabcol3 = new TableColumn();
               tabcol3.setColumnName("replace");
               funcall3.setFunctionName(tabcol3);
               vc_whereColLft = new Vector();
               sc_incoming_arg1 = new SelectColumn();
               vc_whereColRgt = new Vector();
               vc_whereColRgt.addElement(arguments.get(0));
               sc_incoming_arg1.setColumnExpression(vc_whereColRgt);
               vc_whereColLft.addElement(sc_incoming_arg1);
               sc_incoming_arg2 = new SelectColumn();
               vc_wc1 = new Vector();
               vc_wc1.addElement("'days'");
               sc_incoming_arg2.setColumnExpression(vc_wc1);
               vc_whereColLft.addElement(sc_incoming_arg2);
               sc2 = new SelectColumn();
               vc_sc2 = new Vector();
               vc_sc2.addElement("''");
               sc2.setColumnExpression(vc_sc2);
               vc_whereColLft.addElement(sc2);
               funcall3.setFunctionArguments(vc_whereColLft);
               vc_sc_fc1_arg0.addElement(funcall3);
               sc_fc1_arg0.setColumnExpression(vc_sc_fc1_arg0);
               fc1Args.addElement(sc_fc1_arg0);
               sc3 = new SelectColumn();
               vc_sc3 = new Vector();
               vc_sc3.addElement("' '");
               sc3.setColumnExpression(vc_sc3);
               fc1Args.addElement(sc3);
               fc2_args_vc_2_sc = new SelectColumn();
               fc2_args_vc_2_sc_vc = new Vector();
               fc2_args_vc_2_sc_vc.addElement("''");
               fc2_args_vc_2_sc.setColumnExpression(fc2_args_vc_2_sc_vc);
               fc1Args.addElement(fc2_args_vc_2_sc);
               sc1.setColumnExpression(vc_sc1);
               vc_sc0.addElement(sc1);
               fc_arg_1_sc = new SelectColumn();
               fc_arg_1_sc_vc = new Vector();
               fc3 = new FunctionCalls();
               tc3 = new TableColumn();
               tc3.setColumnName("DECIMAL");
               fc3.setFunctionName(tc3);
               vc_sc1_fc2Args = new Vector();
               sc2_fc2Args = new SelectColumn();
               vc_sc2_fc2Args = new Vector();
               vc_sc2_fc2Args.addElement("23");
               sc2_fc2Args.setColumnExpression(vc_sc2_fc2Args);
               vc_sc1_fc2Args.addElement(sc2_fc2Args);
               sc2 = new SelectColumn();
               vc_sc2 = new Vector();
               vc_sc2.addElement("6");
               sc2.setColumnExpression(vc_sc2);
               vc_sc1_fc2Args.addElement(sc2);
               fc3.setFunctionArguments(vc_sc1_fc2Args);
               fc_arg_1_sc_vc.addElement(fc3);
               fc_arg_1_sc.setColumnExpression(fc_arg_1_sc_vc);
               vc_sc0.addElement(fc_arg_1_sc);
               fc.setFunctionArguments(vc_sc0);
               vc_fc.addElement(fc);
               vc_fc.addElement("*");
               vc_fc.addElement("86400");
               sc_col_exp.setColumnExpression(vc_fc);
               sc_col_exp.setCloseBrace(")");
               vc_sc.addElement(sc_col_exp);
               sc.setColumnExpression(vc_sc);
               vc.addElement(sc);
               this.setFunctionArguments(vc);
            } else if (format.equals("%H hrs")) {
               vc = new Vector();
               sc = new SelectColumn();
               vc_sc = new Vector();
               sc_col_exp = new SelectColumn();
               sc_col_exp.setOpenBrace("(");
               vc_fc = new Vector();
               fc = new FunctionCalls();
               tc = new TableColumn();
               tc.setColumnName("convert");
               fc.setFunctionName(tc);
               vc_sc0 = new Vector();
               sc1 = new SelectColumn();
               vc_sc1 = new Vector();
               fc1 = new FunctionCalls();
               tc1 = new TableColumn();
               tc1.setColumnName("replace");
               fc1.setFunctionName(tc1);
               vc_sc1.addElement(fc1);
               fc1Args = new Vector();
               fc1.setFunctionArguments(fc1Args);
               sc_fc1_arg0 = new SelectColumn();
               vc_sc_fc1_arg0 = new Vector();
               funcall3 = new FunctionCalls();
               tabcol3 = new TableColumn();
               tabcol3.setColumnName("replace");
               funcall3.setFunctionName(tabcol3);
               vc_whereColLft = new Vector();
               sc_incoming_arg1 = new SelectColumn();
               vc_whereColRgt = new Vector();
               vc_whereColRgt.addElement(arguments.get(0));
               sc_incoming_arg1.setColumnExpression(vc_whereColRgt);
               vc_whereColLft.addElement(sc_incoming_arg1);
               sc_incoming_arg2 = new SelectColumn();
               vc_wc1 = new Vector();
               vc_wc1.addElement("'hrs'");
               sc_incoming_arg2.setColumnExpression(vc_wc1);
               vc_whereColLft.addElement(sc_incoming_arg2);
               sc2 = new SelectColumn();
               vc_sc2 = new Vector();
               vc_sc2.addElement("''");
               sc2.setColumnExpression(vc_sc2);
               vc_whereColLft.addElement(sc2);
               funcall3.setFunctionArguments(vc_whereColLft);
               vc_sc_fc1_arg0.addElement(funcall3);
               sc_fc1_arg0.setColumnExpression(vc_sc_fc1_arg0);
               fc1Args.addElement(sc_fc1_arg0);
               sc3 = new SelectColumn();
               vc_sc3 = new Vector();
               vc_sc3.addElement("' '");
               sc3.setColumnExpression(vc_sc3);
               fc1Args.addElement(sc3);
               fc2_args_vc_2_sc = new SelectColumn();
               fc2_args_vc_2_sc_vc = new Vector();
               fc2_args_vc_2_sc_vc.addElement("''");
               fc2_args_vc_2_sc.setColumnExpression(fc2_args_vc_2_sc_vc);
               fc1Args.addElement(fc2_args_vc_2_sc);
               sc1.setColumnExpression(vc_sc1);
               vc_sc0.addElement(sc1);
               fc_arg_1_sc = new SelectColumn();
               fc_arg_1_sc_vc = new Vector();
               fc3 = new FunctionCalls();
               tc3 = new TableColumn();
               tc3.setColumnName("DECIMAL");
               fc3.setFunctionName(tc3);
               vc_sc1_fc2Args = new Vector();
               sc2_fc2Args = new SelectColumn();
               vc_sc2_fc2Args = new Vector();
               vc_sc2_fc2Args.addElement("23");
               sc2_fc2Args.setColumnExpression(vc_sc2_fc2Args);
               vc_sc1_fc2Args.addElement(sc2_fc2Args);
               sc2 = new SelectColumn();
               vc_sc2 = new Vector();
               vc_sc2.addElement("6");
               sc2.setColumnExpression(vc_sc2);
               vc_sc1_fc2Args.addElement(sc2);
               fc3.setFunctionArguments(vc_sc1_fc2Args);
               fc_arg_1_sc_vc.addElement(fc3);
               fc_arg_1_sc.setColumnExpression(fc_arg_1_sc_vc);
               vc_sc0.addElement(fc_arg_1_sc);
               fc.setFunctionArguments(vc_sc0);
               vc_fc.addElement(fc);
               vc_fc.addElement("*");
               vc_fc.addElement("3600");
               sc_col_exp.setColumnExpression(vc_fc);
               sc_col_exp.setCloseBrace(")");
               vc_sc.addElement(sc_col_exp);
               sc.setColumnExpression(vc_sc);
               vc.addElement(sc);
               this.setFunctionArguments(vc);
            } else if (format.equals("%m mins")) {
               vc = new Vector();
               sc = new SelectColumn();
               vc_sc = new Vector();
               sc_col_exp = new SelectColumn();
               sc_col_exp.setOpenBrace("(");
               vc_fc = new Vector();
               fc = new FunctionCalls();
               tc = new TableColumn();
               tc.setColumnName("convert");
               fc.setFunctionName(tc);
               vc_sc0 = new Vector();
               sc1 = new SelectColumn();
               vc_sc1 = new Vector();
               fc1 = new FunctionCalls();
               tc1 = new TableColumn();
               tc1.setColumnName("replace");
               fc1.setFunctionName(tc1);
               vc_sc1.addElement(fc1);
               fc1Args = new Vector();
               fc1.setFunctionArguments(fc1Args);
               sc_fc1_arg0 = new SelectColumn();
               vc_sc_fc1_arg0 = new Vector();
               funcall3 = new FunctionCalls();
               tabcol3 = new TableColumn();
               tabcol3.setColumnName("replace");
               funcall3.setFunctionName(tabcol3);
               vc_whereColLft = new Vector();
               sc_incoming_arg1 = new SelectColumn();
               vc_whereColRgt = new Vector();
               vc_whereColRgt.addElement(arguments.get(0));
               sc_incoming_arg1.setColumnExpression(vc_whereColRgt);
               vc_whereColLft.addElement(sc_incoming_arg1);
               sc_incoming_arg2 = new SelectColumn();
               vc_wc1 = new Vector();
               vc_wc1.addElement("'mins'");
               sc_incoming_arg2.setColumnExpression(vc_wc1);
               vc_whereColLft.addElement(sc_incoming_arg2);
               sc2 = new SelectColumn();
               vc_sc2 = new Vector();
               vc_sc2.addElement("''");
               sc2.setColumnExpression(vc_sc2);
               vc_whereColLft.addElement(sc2);
               funcall3.setFunctionArguments(vc_whereColLft);
               vc_sc_fc1_arg0.addElement(funcall3);
               sc_fc1_arg0.setColumnExpression(vc_sc_fc1_arg0);
               fc1Args.addElement(sc_fc1_arg0);
               sc3 = new SelectColumn();
               vc_sc3 = new Vector();
               vc_sc3.addElement("' '");
               sc3.setColumnExpression(vc_sc3);
               fc1Args.addElement(sc3);
               fc2_args_vc_2_sc = new SelectColumn();
               fc2_args_vc_2_sc_vc = new Vector();
               fc2_args_vc_2_sc_vc.addElement("''");
               fc2_args_vc_2_sc.setColumnExpression(fc2_args_vc_2_sc_vc);
               fc1Args.addElement(fc2_args_vc_2_sc);
               sc1.setColumnExpression(vc_sc1);
               vc_sc0.addElement(sc1);
               fc_arg_1_sc = new SelectColumn();
               fc_arg_1_sc_vc = new Vector();
               fc3 = new FunctionCalls();
               tc3 = new TableColumn();
               tc3.setColumnName("DECIMAL");
               fc3.setFunctionName(tc3);
               vc_sc1_fc2Args = new Vector();
               sc2_fc2Args = new SelectColumn();
               vc_sc2_fc2Args = new Vector();
               vc_sc2_fc2Args.addElement("23");
               sc2_fc2Args.setColumnExpression(vc_sc2_fc2Args);
               vc_sc1_fc2Args.addElement(sc2_fc2Args);
               sc2 = new SelectColumn();
               vc_sc2 = new Vector();
               vc_sc2.addElement("6");
               sc2.setColumnExpression(vc_sc2);
               vc_sc1_fc2Args.addElement(sc2);
               fc3.setFunctionArguments(vc_sc1_fc2Args);
               fc_arg_1_sc_vc.addElement(fc3);
               fc_arg_1_sc.setColumnExpression(fc_arg_1_sc_vc);
               vc_sc0.addElement(fc_arg_1_sc);
               fc.setFunctionArguments(vc_sc0);
               vc_fc.addElement(fc);
               vc_fc.addElement("*");
               vc_fc.addElement("60");
               sc_col_exp.setColumnExpression(vc_fc);
               sc_col_exp.setCloseBrace(")");
               vc_sc.addElement(sc_col_exp);
               sc.setColumnExpression(vc_sc);
               vc.addElement(sc);
               this.setFunctionArguments(vc);
            } else if (!format.equals("%s secs") && !format.equals("%s.%SSSSSS secs") && !format.equals("%s.%SSS secs")) {
               if (format.startsWith("%H:%m:%s")) {
                  this.functionName.setColumnName("ZR_CONV_HMS_TO_DUR");
                  vc = new Vector();
                  sc = new SelectColumn();
                  vc_sc = new Vector();
                  vc_sc.addElement(arguments.get(0));
                  sc.setColumnExpression(vc_sc);
                  vc.addElement(sc);
                  this.setFunctionArguments(vc);
               } else if (!format.equals("%H hrs %m mins %s secs") && !format.equals("%H hrs %m mins %s.%SSSSSS secs") && !format.equals("%H hrs %m mins %s.%SSS secs")) {
                  if (format.startsWith("%D days %H:%m:%s")) {
                     this.functionName.setColumnName("ZR_CONV_DAYS_HMS_TO_DUR");
                     vc = new Vector();
                     sc = new SelectColumn();
                     vc_sc = new Vector();
                     vc_sc.addElement(arguments.get(0));
                     sc.setColumnExpression(vc_sc);
                     vc.addElement(sc);
                     this.setFunctionArguments(vc);
                  } else {
                     WhereItem whereIt;
                     WhereColumn whereColLft;
                     WhereColumn whereColRgt;
                     WhereColumn wc1;
                     if (format.startsWith("%D %H:%m:%s")) {
                        this.functionName.setColumnName("ZR_CONV_DAYS_HMS_TO_DUR");
                        vc = new Vector();
                        sc = new SelectColumn();
                        vc_sc = new Vector();
                        fc = new FunctionCalls();
                        tc = new TableColumn();
                        tc.setColumnName("insert");
                        fc.setFunctionName(tc);
                        fcArgs = new Vector();
                        sc0 = new SelectColumn();
                        vc_sc0 = new Vector();
                        vc_sc0.addElement(arguments.get(0));
                        sc0.setColumnExpression(vc_sc0);
                        fcArgs.addElement(sc0);
                        sc1 = new SelectColumn();
                        vc_sc1 = new Vector();
                        fc1 = new FunctionCalls();
                        tc1 = new TableColumn();
                        tc1.setColumnName("position");
                        fc1.setFunctionName(tc1);
                        fc1Args = new Vector();
                        sc_fc1_arg0 = new SelectColumn();
                        vc_sc_fc1_arg0 = new Vector();
                        whereIt = new WhereItem();
                        whereIt.setOpenBrace("");
                        whereIt.setCloseBrace("");
                        whereColLft = new WhereColumn();
                        vc_whereColLft = new Vector();
                        vc_whereColLft.addElement("' '");
                        whereColLft.setColumnExpression(vc_whereColLft);
                        whereIt.setLeftWhereExp(whereColLft);
                        whereColRgt = new WhereColumn();
                        vc_whereColRgt = new Vector();
                        wc1 = new WhereColumn();
                        vc_wc1 = new Vector();
                        vc_wc1.addElement(arguments1.get(0));
                        wc1.setColumnExpression(vc_wc1);
                        vc_whereColRgt.addElement(wc1);
                        whereColRgt.setColumnExpression(vc_whereColRgt);
                        whereIt.setRightWhereExp(whereColRgt);
                        whereIt.setOperator("IN");
                        vc_sc_fc1_arg0.addElement(whereIt);
                        sc_fc1_arg0.setColumnExpression(vc_sc_fc1_arg0);
                        fc1Args.addElement(sc_fc1_arg0);
                        fc1.setFunctionArguments(fc1Args);
                        vc_sc1.addElement(fc1);
                        sc1.setColumnExpression(vc_sc1);
                        fcArgs.addElement(sc1);
                        sc2 = new SelectColumn();
                        vc_sc2 = new Vector();
                        vc_sc2.addElement("1");
                        sc2.setColumnExpression(vc_sc2);
                        fcArgs.addElement(sc2);
                        sc3 = new SelectColumn();
                        vc_sc3 = new Vector();
                        vc_sc3.addElement("' days '");
                        sc3.setColumnExpression(vc_sc3);
                        fcArgs.addElement(sc3);
                        fc.setFunctionArguments(fcArgs);
                        vc_sc.addElement(fc);
                        sc.setColumnExpression(vc_sc);
                        vc.addElement(sc);
                        this.setFunctionArguments(vc);
                     } else if (format.startsWith("%D.%H:%m:%s")) {
                        this.functionName.setColumnName("ZR_CONV_DAYS_HMS_TO_DUR");
                        vc = new Vector();
                        sc = new SelectColumn();
                        vc_sc = new Vector();
                        fc = new FunctionCalls();
                        tc = new TableColumn();
                        tc.setColumnName("insert");
                        fc.setFunctionName(tc);
                        fcArgs = new Vector();
                        sc0 = new SelectColumn();
                        vc_sc0 = new Vector();
                        vc_sc0.addElement(arguments.get(0));
                        sc0.setColumnExpression(vc_sc0);
                        fcArgs.addElement(sc0);
                        sc1 = new SelectColumn();
                        vc_sc1 = new Vector();
                        fc1 = new FunctionCalls();
                        tc1 = new TableColumn();
                        tc1.setColumnName("position");
                        fc1.setFunctionName(tc1);
                        fc1Args = new Vector();
                        sc_fc1_arg0 = new SelectColumn();
                        vc_sc_fc1_arg0 = new Vector();
                        whereIt = new WhereItem();
                        whereIt.setOpenBrace("");
                        whereIt.setCloseBrace("");
                        whereColLft = new WhereColumn();
                        vc_whereColLft = new Vector();
                        vc_whereColLft.addElement("'.'");
                        whereColLft.setColumnExpression(vc_whereColLft);
                        whereIt.setLeftWhereExp(whereColLft);
                        whereColRgt = new WhereColumn();
                        vc_whereColRgt = new Vector();
                        wc1 = new WhereColumn();
                        vc_wc1 = new Vector();
                        vc_wc1.addElement(arguments1.get(0));
                        wc1.setColumnExpression(vc_wc1);
                        vc_whereColRgt.addElement(wc1);
                        whereColRgt.setColumnExpression(vc_whereColRgt);
                        whereIt.setRightWhereExp(whereColRgt);
                        whereIt.setOperator("IN");
                        vc_sc_fc1_arg0.addElement(whereIt);
                        sc_fc1_arg0.setColumnExpression(vc_sc_fc1_arg0);
                        fc1Args.addElement(sc_fc1_arg0);
                        fc1.setFunctionArguments(fc1Args);
                        vc_sc1.addElement(fc1);
                        sc1.setColumnExpression(vc_sc1);
                        fcArgs.addElement(sc1);
                        sc2 = new SelectColumn();
                        vc_sc2 = new Vector();
                        vc_sc2.addElement("1");
                        sc2.setColumnExpression(vc_sc2);
                        fcArgs.addElement(sc2);
                        sc3 = new SelectColumn();
                        vc_sc3 = new Vector();
                        vc_sc3.addElement("' days '");
                        sc3.setColumnExpression(vc_sc3);
                        fcArgs.addElement(sc3);
                        fc.setFunctionArguments(fcArgs);
                        vc_sc.addElement(fc);
                        sc.setColumnExpression(vc_sc);
                        vc.addElement(sc);
                        this.setFunctionArguments(vc);
                     } else if (format.equals("%D days %H hrs %m mins %s secs") || format.equals("%D days %H hrs %m mins %s.%SSSSSS secs") || format.equals("%D days %H hrs %m mins %s.%SSS secs")) {
                        this.functionName.setColumnName("ZR_CONV_FMTD_DAYS_HMS_TO_DUR");
                        vc = new Vector();
                        sc = new SelectColumn();
                        vc_sc = new Vector();
                        vc_sc.addElement(arguments.get(0));
                        sc.setColumnExpression(vc_sc);
                        vc.addElement(sc);
                        this.setFunctionArguments(vc);
                     }
                  }
               } else {
                  this.functionName.setColumnName("ZR_CONV_HMS_TO_DUR");
                  vc = new Vector();
                  sc = new SelectColumn();
                  vc_sc = new Vector();
                  fc = new FunctionCalls();
                  tc = new TableColumn();
                  tc.setColumnName("replace");
                  fc.setFunctionName(tc);
                  fcArgs = new Vector();
                  sc0 = new SelectColumn();
                  vc_sc0 = new Vector();
                  FunctionCalls fc2 = new FunctionCalls();
                  TableColumn tc2 = new TableColumn();
                  tc2.setColumnName("replace");
                  fc2.setFunctionName(tc2);
                  fc2Args = new Vector();
                  SelectColumn sc0_fc2Args = new SelectColumn();
                  fc1Args = new Vector();
                  FunctionCalls fc3 = new FunctionCalls();
                  TableColumn tc3 = new TableColumn();
                  tc3.setColumnName("replace");
                  fc3.setFunctionName(tc3);
                  fc3Args = new Vector();
                  SelectColumn sc0_fc3Args = new SelectColumn();
                  vc_whereColLft = new Vector();
                  FunctionCalls fc4 = new FunctionCalls();
                  TableColumn tc4 = new TableColumn();
                  tc4.setColumnName("replace");
                  fc4.setFunctionName(tc4);
                  Vector fc4Args = new Vector();
                  SelectColumn sc0_fc4Args = new SelectColumn();
                  Vector vc_sc0_fc4Args = new Vector();
                  vc_sc0_fc4Args.addElement(arguments.get(0));
                  sc0_fc4Args.setColumnExpression(vc_sc0_fc4Args);
                  fc4Args.addElement(sc0_fc4Args);
                  SelectColumn sc1_fc4Args = new SelectColumn();
                  Vector vc_sc1_fc4Args = new Vector();
                  vc_sc1_fc4Args.addElement("' '");
                  sc1_fc4Args.setColumnExpression(vc_sc1_fc4Args);
                  fc4Args.addElement(sc1_fc4Args);
                  SelectColumn sc2_fc4Args = new SelectColumn();
                  Vector vc_sc2_fc4Args = new Vector();
                  vc_sc2_fc4Args.addElement("''");
                  sc2_fc4Args.setColumnExpression(vc_sc2_fc4Args);
                  fc4Args.addElement(sc2_fc4Args);
                  fc4.setFunctionArguments(fc4Args);
                  vc_whereColLft.addElement(fc4);
                  sc0_fc3Args.setColumnExpression(vc_whereColLft);
                  fc3Args.addElement(sc0_fc3Args);
                  SelectColumn sc1_fc3Args = new SelectColumn();
                  Vector vc_sc1_fc3Args = new Vector();
                  vc_sc1_fc3Args.addElement("'hrs'");
                  sc1_fc3Args.setColumnExpression(vc_sc1_fc3Args);
                  fc3Args.addElement(sc1_fc3Args);
                  SelectColumn sc2_fc3Args = new SelectColumn();
                  Vector vc_sc2_fc3Args = new Vector();
                  vc_sc2_fc3Args.addElement("':'");
                  sc2_fc3Args.setColumnExpression(vc_sc2_fc3Args);
                  fc3Args.addElement(sc2_fc3Args);
                  fc3.setFunctionArguments(fc3Args);
                  fc1Args.addElement(fc3);
                  sc0_fc2Args.setColumnExpression(fc1Args);
                  fc2Args.addElement(sc0_fc2Args);
                  SelectColumn sc1_fc2Args = new SelectColumn();
                  vc_sc1_fc2Args = new Vector();
                  vc_sc1_fc2Args.addElement("'mins'");
                  sc1_fc2Args.setColumnExpression(vc_sc1_fc2Args);
                  fc2Args.addElement(sc1_fc2Args);
                  sc2_fc2Args = new SelectColumn();
                  vc_sc2_fc2Args = new Vector();
                  vc_sc2_fc2Args.addElement("':'");
                  sc2_fc2Args.setColumnExpression(vc_sc2_fc2Args);
                  fc2Args.addElement(sc2_fc2Args);
                  fc2.setFunctionArguments(fc2Args);
                  vc_sc0.addElement(fc2);
                  sc0.setColumnExpression(vc_sc0);
                  fcArgs.addElement(sc0);
                  sc2 = new SelectColumn();
                  vc_sc2 = new Vector();
                  vc_sc2.addElement("'secs'");
                  sc2.setColumnExpression(vc_sc2);
                  fcArgs.addElement(sc2);
                  SelectColumn sc3 = new SelectColumn();
                  Vector vc_sc3 = new Vector();
                  vc_sc3.addElement("''");
                  sc3.setColumnExpression(vc_sc3);
                  fcArgs.addElement(sc3);
                  fc.setFunctionArguments(fcArgs);
                  vc_sc.addElement(fc);
                  sc.setColumnExpression(vc_sc);
                  vc.addElement(sc);
                  this.setFunctionArguments(vc);
               }
            } else {
               vc = new Vector();
               sc = new SelectColumn();
               vc_sc = new Vector();
               sc_col_exp = new SelectColumn();
               sc_col_exp.setOpenBrace("(");
               vc_fc = new Vector();
               fc = new FunctionCalls();
               tc = new TableColumn();
               tc.setColumnName("convert");
               fc.setFunctionName(tc);
               vc_sc0 = new Vector();
               sc1 = new SelectColumn();
               vc_sc1 = new Vector();
               fc1 = new FunctionCalls();
               tc1 = new TableColumn();
               tc1.setColumnName("replace");
               fc1.setFunctionName(tc1);
               vc_sc1.addElement(fc1);
               fc1Args = new Vector();
               fc1.setFunctionArguments(fc1Args);
               sc_fc1_arg0 = new SelectColumn();
               vc_sc_fc1_arg0 = new Vector();
               funcall3 = new FunctionCalls();
               tabcol3 = new TableColumn();
               tabcol3.setColumnName("replace");
               funcall3.setFunctionName(tabcol3);
               vc_whereColLft = new Vector();
               sc_incoming_arg1 = new SelectColumn();
               vc_whereColRgt = new Vector();
               vc_whereColRgt.addElement(arguments.get(0));
               sc_incoming_arg1.setColumnExpression(vc_whereColRgt);
               vc_whereColLft.addElement(sc_incoming_arg1);
               sc_incoming_arg2 = new SelectColumn();
               vc_wc1 = new Vector();
               vc_wc1.addElement("'secs'");
               sc_incoming_arg2.setColumnExpression(vc_wc1);
               vc_whereColLft.addElement(sc_incoming_arg2);
               sc2 = new SelectColumn();
               vc_sc2 = new Vector();
               vc_sc2.addElement("''");
               sc2.setColumnExpression(vc_sc2);
               vc_whereColLft.addElement(sc2);
               funcall3.setFunctionArguments(vc_whereColLft);
               vc_sc_fc1_arg0.addElement(funcall3);
               sc_fc1_arg0.setColumnExpression(vc_sc_fc1_arg0);
               fc1Args.addElement(sc_fc1_arg0);
               sc3 = new SelectColumn();
               vc_sc3 = new Vector();
               vc_sc3.addElement("' '");
               sc3.setColumnExpression(vc_sc3);
               fc1Args.addElement(sc3);
               fc2_args_vc_2_sc = new SelectColumn();
               fc2_args_vc_2_sc_vc = new Vector();
               fc2_args_vc_2_sc_vc.addElement("''");
               fc2_args_vc_2_sc.setColumnExpression(fc2_args_vc_2_sc_vc);
               fc1Args.addElement(fc2_args_vc_2_sc);
               sc1.setColumnExpression(vc_sc1);
               vc_sc0.addElement(sc1);
               fc_arg_1_sc = new SelectColumn();
               fc_arg_1_sc_vc = new Vector();
               fc3 = new FunctionCalls();
               tc3 = new TableColumn();
               tc3.setColumnName("DECIMAL");
               fc3.setFunctionName(tc3);
               vc_sc1_fc2Args = new Vector();
               sc2_fc2Args = new SelectColumn();
               vc_sc2_fc2Args = new Vector();
               vc_sc2_fc2Args.addElement("23");
               sc2_fc2Args.setColumnExpression(vc_sc2_fc2Args);
               vc_sc1_fc2Args.addElement(sc2_fc2Args);
               sc2 = new SelectColumn();
               vc_sc2 = new Vector();
               vc_sc2.addElement("6");
               sc2.setColumnExpression(vc_sc2);
               vc_sc1_fc2Args.addElement(sc2);
               fc3.setFunctionArguments(vc_sc1_fc2Args);
               fc_arg_1_sc_vc.addElement(fc3);
               fc_arg_1_sc.setColumnExpression(fc_arg_1_sc_vc);
               vc_sc0.addElement(fc_arg_1_sc);
               fc.setFunctionArguments(vc_sc0);
               vc_fc.addElement(fc);
               vc_fc.addElement("*");
               vc_fc.addElement("1");
               sc_col_exp.setColumnExpression(vc_fc);
               sc_col_exp.setCloseBrace(")");
               vc_sc.addElement(sc_col_exp);
               sc.setColumnExpression(vc_sc);
               vc.addElement(sc);
               this.setFunctionArguments(vc);
            }
         } else {
            vc = new Vector();
            sc = new SelectColumn();
            vc.addElement(sc);
            vc_sc = new Vector();
            sc.setOpenBrace("(");
            sc.setColumnExpression(vc_sc);
            sc.setCloseBrace(")");
            fc = new FunctionCalls();
            tc = new TableColumn();
            tc.setColumnName("convert");
            fc.setFunctionName(tc);
            vc_sc.add(fc);
            fcArgs = new Vector();
            fc.setFunctionArguments(fcArgs);
            sc0 = new SelectColumn();
            vc_sc0 = new Vector();
            sc0.setColumnExpression(vc_sc0);
            TableColumn sc_replace_vc_tc = new TableColumn();
            sc_replace_vc_tc.setColumnName((String)arguments.get(0));
            fcArgs.add(sc0);
            SelectColumn sc_signed = new SelectColumn();
            fcArgs.add(sc_signed);
            fc2Args = new Vector();
            FunctionCalls sc_signed_vc_fc = new FunctionCalls();
            TableColumn tc_signed = new TableColumn();
            sc_signed_vc_fc.setFunctionName(tc_signed);
            sc_signed_vc_fc.setFunctionArguments(fc2Args);
            tc_signed.setColumnName("DECIMAL");
            sc_fc1_arg0 = new SelectColumn();
            SelectColumn sc_signed_vc_fc_vc_sc2 = new SelectColumn();
            fc2Args.addElement(sc_fc1_arg0);
            fc2Args.addElement(sc_signed_vc_fc_vc_sc2);
            fc3Args = new Vector();
            fc3Args.add("23");
            Vector sc_signed_vc_fc_vc_sc2_vc = new Vector();
            sc_signed_vc_fc_vc_sc2_vc.add("6");
            this.setFunctionArguments(vc);
         }

         this.setCustomDataType("DURATION");
      }
   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String format = ((SelectColumn)this.functionArguments.get(1)).getColumnExpression().get(0).toString();
      format = format.substring(1, format.length() - 1);
      if (from_sqs != null && from_sqs.isPgsqlLive()) {
         String qry = "";
         if (!format.equals("%s") && !format.equals("%s.%SSSSSS") && !format.equals("%s.%SSS")) {
            if (!format.equals("%D days") && !format.equals("%H hrs") && !format.equals("%m mins") && !format.equals("%s secs") && !format.equals("%s.%SSSSSS secs") && !format.equals("%s.%SSS secs") && !format.startsWith("%H:%m:%s") && !format.equals("%H hrs %m mins %s secs") && !format.equals("%H hrs %m mins %s.%SSSSSS secs") && !format.equals("%H hrs %m mins %s.%SSS secs") && !format.startsWith("%D days %H:%m:%s") && !format.equals("%D days %H hrs %m mins %s secs") && !format.equals("%D days %H hrs %m mins %s.%SSSSSS secs") && !format.equals("%D days %H hrs %m mins %s.%SSS secs")) {
               if (format.startsWith("%D %H:%m:%s")) {
                  qry = "SELECT (((CASE WHEN(position('-' in trim(" + this.functionArguments.get(0) + "))>0) THEN -1 ELSE 1 END) * CAST(EXTRACT(EPOCH FROM cast(REPLACE(" + this.functionArguments.get(0) + " ,'-','') as interval)) AS DECIMAL(23,6))));";
               } else {
                  if (!format.startsWith("%D.%H:%m:%s")) {
                     throw new ConvertException("Unsupported duration format is given :" + this.functionArguments.get(1));
                  }

                  qry = "SELECT (((CASE WHEN(position('-' in trim(overlay(" + this.functionArguments.get(0) + " placing ' days ' from position('.' in " + this.functionArguments.get(0) + ") for 1)))>0) THEN -1 ELSE 1 END) * CAST(EXTRACT(EPOCH FROM cast(REPLACE((overlay(" + this.functionArguments.get(0) + " placing ' days ' from position('.' in " + this.functionArguments.get(0) + ") for 1)) ,'-','') as interval)) AS DECIMAL(23,6))))";
               }
            } else {
               qry = "SELECT (CAST(EXTRACT(EPOCH FROM cast(" + this.functionArguments.get(0) + " as interval)) AS DECIMAL(23,6)));";
            }
         } else {
            qry = "SELECT (CAST(" + this.functionArguments.get(0) + " AS decimal(23, 6)))";
         }

         this.functionName.setColumnName(qry);
         this.setFunctionArguments(new Vector());
         this.setOpenBracesForFunctionNameRequired(false);
      }
   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      String fmt = ((SelectColumn)arguments.get(1)).getColumnExpression().get(0).toString();
      if (fmt.equals("'%H:%m:%s'")) {
         String query = "((convert(SUBSTRING(" + arguments.get(0).toString() + ", 0, CHARINDEX(':'," + arguments.get(0).toString() + ")) ,DECIMAL(23,6))*3600)+(convert(SUBSTRING(" + arguments.get(0).toString() + ", CHARINDEX(':'," + arguments.get(0).toString() + ")+1, CHARINDEX(':'," + arguments.get(0).toString() + ", CHARINDEX(':'," + arguments.get(0).toString() + "))-1 ) ,DECIMAL(23,6))*60)+(convert(SUBSTRING(REVERSE(" + arguments.get(0).toString() + "), 0, CHARINDEX(':',REVERSE(" + arguments.get(0).toString() + "),DECIMAL(23,6))))";
         this.functionName.setColumnName(query);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }

   }
}
