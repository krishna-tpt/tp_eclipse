package com.adventnet.swissqlapi.util;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import java.util.HashMap;
import java.util.Vector;

public class CreateConvertedFunctionsUtil {
   private static HashMap<String, String> castDtVsGeneralizedSubType = new HashMap<String, String>() {
      {
         this.put("BINARY", "STRING");
         this.put("CHAR", "STRING");
         this.put("DATE", "DATETIME");
         this.put("DATETIME", "DATETIME");
         this.put("DECIMAL", "NUMBER");
         this.put("DOUBLE", "NUMBER");
         this.put("JSON", "STRING");
      }
   };

   public static FunctionCalls createCastFunction(Object arg1, Object arg2) throws ConvertException {
      FunctionCalls fc = new FunctionCalls();
      SelectColumn sc_firstarg = new SelectColumn();
      SelectColumn sc_secondarg = new SelectColumn();
      Vector v_farg = new Vector();
      Vector v_sarg = new Vector();
      TableColumn tc = new TableColumn();
      Vector vec_functionarg = new Vector();
      tc.setColumnName("CAST");
      fc.setFunctionName(tc);
      fc.setAsDatatype("AS");
      v_farg.addElement(arg2);
      sc_firstarg.setColumnExpression(v_farg);
      v_sarg.addElement(arg1.toString());
      sc_secondarg.setColumnExpression(v_sarg);
      vec_functionarg.addElement(sc_firstarg);
      vec_functionarg.addElement(sc_secondarg);
      fc.setFunctionArguments(vec_functionarg);
      return fc;
   }

   public static String getCastFunctionDataType(Object arg1) {
      String dt = (String)castDtVsGeneralizedSubType.get(arg1.toString());
      return dt != null ? dt : "UNDEFINED";
   }

   public static SelectColumn createDefaultOperation(Object arg1, Object arg2, Object operator, String returnType) {
      SelectColumn sc = new SelectColumn();
      Vector vce = new Vector();
      vce.add(arg1);
      vce.add(operator);
      vce.add(arg2);
      sc.setColumnExpression(vce);
      sc.setInstanceDatatype(returnType);
      return sc;
   }

   public static FunctionCalls createSimpleFunctionCallWithTwoArgs(Object arg1, Object arg2, String wrapper, String returnType) {
      SelectColumn sc_firstarg = new SelectColumn();
      SelectColumn sc_secondarg = new SelectColumn();
      Vector v_farg = new Vector();
      TableColumn tc = new TableColumn();
      FunctionCalls fc = new FunctionCalls();
      Vector vec_firstarg = new Vector();
      Vector vec_secondarg = new Vector();
      tc.setColumnName(wrapper.toUpperCase());
      fc.setFunctionName(tc);
      vec_firstarg.addElement(arg1);
      sc_firstarg.setColumnExpression(vec_firstarg);
      v_farg.addElement(sc_firstarg);
      vec_secondarg.addElement(arg2);
      sc_secondarg.setColumnExpression(vec_secondarg);
      v_farg.addElement(sc_secondarg);
      fc.setFunctionArguments(v_farg);
      fc.setInstanceDatatype(returnType);
      return fc;
   }

   public static FunctionCalls createSimpleFunctionCallWithOneArgs(Object op, String wrapper, String returnType) {
      SelectColumn sc_arg = new SelectColumn();
      Vector v_arg = new Vector();
      TableColumn tc = new TableColumn();
      FunctionCalls fc = new FunctionCalls();
      Vector vec_arg = new Vector();
      tc.setColumnName(wrapper.toUpperCase());
      fc.setFunctionName(tc);
      vec_arg.addElement(op);
      sc_arg.setColumnExpression(vec_arg);
      v_arg.addElement(sc_arg);
      fc.setFunctionArguments(v_arg);
      fc.setInstanceDatatype(returnType);
      return fc;
   }

   public static FunctionCalls createSimpleFunctionCallWithThreeArgs(Object arg1, Object arg2, Object arg3, String wrapper, String returnType) {
      SelectColumn sc_firstarg = new SelectColumn();
      SelectColumn sc_secondarg = new SelectColumn();
      SelectColumn sc_thirdarg = new SelectColumn();
      Vector v_farg = new Vector();
      TableColumn tc = new TableColumn();
      FunctionCalls fc = new FunctionCalls();
      Vector vec_firstarg = new Vector();
      Vector vec_secondarg = new Vector();
      Vector vec_thirdarg = new Vector();
      tc.setColumnName(wrapper.toUpperCase());
      fc.setFunctionName(tc);
      vec_firstarg.addElement(arg1);
      sc_firstarg.setColumnExpression(vec_firstarg);
      v_farg.addElement(sc_firstarg);
      vec_secondarg.addElement(arg2);
      sc_secondarg.setColumnExpression(vec_secondarg);
      v_farg.addElement(sc_secondarg);
      vec_thirdarg.addElement(arg3);
      sc_thirdarg.setColumnExpression(vec_thirdarg);
      v_farg.addElement(sc_thirdarg);
      fc.setFunctionArguments(v_farg);
      fc.setInstanceDatatype(returnType);
      return fc;
   }

   public static SelectColumn createIntervalSelectColumn(Object operand) {
      SelectColumn sc = new SelectColumn();
      Vector ce = new Vector();
      TableColumn interval_tc = new TableColumn();
      interval_tc.setColumnName("INTERVAL");
      ce.add(interval_tc);
      if (!(operand instanceof SelectColumn)) {
         SelectColumn operand_sc = new SelectColumn();
         Vector operand_ce = new Vector();
         operand_ce.add(operand);
         operand_sc.setColumnExpression(operand_ce);
         operand = operand_sc;
      }

      ce.add(operand);
      ce.add("DAY");
      sc.setColumnExpression(ce);
      sc.setInstanceDatatype("INTERVAL");
      return sc;
   }
}
