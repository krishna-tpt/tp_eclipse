package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.util.CreateConvertedFunctionsUtil;
import com.adventnet.swissqlapi.util.FunctionValidateHandler;
import com.adventnet.swissqlapi.util.InstanceDataTypeHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

public class PostfixFlowForMySQLSelect {
   public static final String NUMBER = "NUMBER";
   public static final String PLAIN = "PLAIN";
   public static final String BOOLEAN = "BOOLEAN";
   public static final String DATE = "DATE";
   public static final String DATETIME = "DATETIME";
   public static final String DURATION = "DURATION";
   public static final String UNDEFINED = "UNDEFINED";
   public static final String TIME = "TIME";
   public static final String INTERVAL = "INTERVAL";
   public static final HashMap<String, Integer> OPERATORVsPRECEDENCE_unm = new HashMap<String, Integer>() {
      {
         this.put("::", 12);
         this.put("**", 11);
         this.put("^", 11);
         this.put("^=", 10);
         this.put("~*", 9);
         this.put("/", 8);
         this.put("div", 8);
         this.put("%", 8);
         this.put("*", 7);
         this.put("+", 6);
         this.put("-", 5);
         this.put(">", 4);
         this.put("<", 4);
         this.put(">=", 4);
         this.put("<=", 4);
         this.put("&", 3);
         this.put("|", 2);
         this.put("<>", 1);
         this.put("=", 1);
         this.put("!=", 1);
      }
   };
   public static final Map<String, Integer> OPERATOR_VS_PRECEDENCE;
   public static final List<String> unaryOperators_unm;
   public static final List<String> UNARY_OPERATORS;
   public static final ArrayList<String> splStringFns_unm;
   public static final List<String> SPL_STRING_FNS;
   public static final ArrayList<String> COMPARATIVE_OPERATORS_unm;
   public static final List<String> COMPARATIVE_OPERATORS;
   public static final Set<String> ALLOWED_OPERATORS;
   private FunctionValidateHandler validation_handler = null;
   private InstanceDataTypeHandler dataTypeHandler = null;
   public static final ArrayList<String> BOOLEAN_TRUE_SYNONYMS_unm;
   public static final List<String> BOOLEAN_TRUE_SYNONYMS;
   public static final ArrayList<String> BOOLEAN_FALSE_SYNONYMS_unm;
   public static final List<String> BOOLEAN_FALSE_SYNONYMS;

   public Object[] postFixToMySQLFlowForSc(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs, Vector ce) throws ConvertException {
      this.dataTypeHandler = from_sqs.getinstanceDataTypeHandler();
      if (ce.size() == 1) {
         Object[] convObjNInstanceDatatype = this.getConvObjNInstanceDatatype(ce.get(0), from_sqs, to_sqs);
         Vector conv_ce = new Vector();
         conv_ce.add(convObjNInstanceDatatype[0]);
         return new Object[]{conv_ce, convObjNInstanceDatatype[1]};
      } else {
         Vector postfix_ce = this.toPostfixColumnExpression(ce, from_sqs, to_sqs);
         return this.detectDataTypeOfExpr(postfix_ce, from_sqs, to_sqs);
      }
   }

   public Vector toPostfixColumnExpression(Vector v_ce, SelectQueryStatement from_sqs, SelectQueryStatement to_sqs) throws ConvertException {
      Vector postfix_ce = new Vector();
      Stack<String> operatorStack = new Stack();
      InstanceDataTypeHandler dataTypeHandler = from_sqs.getinstanceDataTypeHandler();

      for(int i = 0; i < v_ce.size(); ++i) {
         Object ce = v_ce.elementAt(i);

         try {
            if (ce instanceof String && UNARY_OPERATORS.contains(ce.toString()) && i == 0) {
               if (v_ce.size() == 2) {
                  ++i;
                  return v_ce;
               }

               postfix_ce.addElement(this.scForUnaryOperator((String)v_ce.elementAt(i), v_ce.elementAt(i + 1)));
               ++i;
            } else if (ce instanceof String && UNARY_OPERATORS.contains(ce.toString()) && i > 0 && ALLOWED_OPERATORS.contains(v_ce.elementAt(i - 1).toString())) {
               postfix_ce.addElement(this.scForUnaryOperator((String)v_ce.elementAt(i), v_ce.elementAt(i + 1)));
               ++i;
            } else if (v_ce.size() > 3 && checkForIntervalSc(v_ce, i)) {
               SelectColumn interval_sc = new SelectColumn();
               Vector interval_ce = new Vector();
               interval_ce.add(v_ce.elementAt(i));
               interval_ce.add(v_ce.elementAt(i + 1));
               interval_ce.add(v_ce.elementAt(i + 2));
               interval_sc.setColumnExpression(interval_ce);
               interval_sc.setInstanceDatatype("INTERVAL");
               postfix_ce.add(interval_sc);
               i += 2;
            } else if (ce instanceof String && ALLOWED_OPERATORS.contains(ce)) {
               while(!operatorStack.isEmpty() && (Integer)OPERATOR_VS_PRECEDENCE.get(operatorStack.peek()) >= (Integer)OPERATOR_VS_PRECEDENCE.get(v_ce.elementAt(i))) {
                  postfix_ce.add(operatorStack.pop());
               }

               operatorStack.push((String)v_ce.elementAt(i));
            } else {
               postfix_ce.add(ce);
            }
         } catch (Exception var11) {
            throw var11;
         }
      }

      while(!operatorStack.isEmpty()) {
         postfix_ce.add(operatorStack.pop());
      }

      return postfix_ce;
   }

   public Object[] detectDataTypeOfExpr(Vector v_ce, SelectQueryStatement from_sqs, SelectQueryStatement to_sqs) throws ConvertException {
      Stack<Object> conv_ce = new Stack();
      Stack<String> dataType_ce = new Stack();
      this.validation_handler = from_sqs.getValidationHandler();
      if (v_ce.size() == 1) {
         return this.getConvObjNInstanceDatatype(v_ce.get(0), from_sqs, to_sqs);
      } else {
         if (v_ce.size() == 2 && UNARY_OPERATORS.contains(v_ce.get(0).toString())) {
            conv_ce.add(v_ce.get(0));
            conv_ce.add(1, this.convertToMySQL(v_ce.get(1), from_sqs, to_sqs));
            dataType_ce.add(this.getInstanceDataTypeForObj(v_ce.get(1), this.dataTypeHandler, from_sqs));
         } else if (checkForIntervalSc(v_ce, 0)) {
            v_ce.setElementAt(this.convertToMySQL(v_ce.get(1), from_sqs, to_sqs), 1);
            conv_ce.addAll(v_ce);
            dataType_ce.add("INTERVAL");
         } else {
            for(int i = 0; i < v_ce.size(); ++i) {
               Object ce = v_ce.elementAt(i);
               if (ce instanceof String && ALLOWED_OPERATORS.contains(ce.toString())) {
                  Object op1 = conv_ce.peek();
                  conv_ce.pop();
                  Object op2 = conv_ce.peek();
                  conv_ce.pop();
                  String dt1 = (String)dataType_ce.peek();
                  dataType_ce.pop();
                  String dt2 = (String)dataType_ce.peek();
                  dataType_ce.pop();
                  Object[] exprNrt = this.getExpressionNReturnType(op1, op2, ce, dt1, dt2);
                  conv_ce.add(exprNrt[0]);
                  dataType_ce.add((String)exprNrt[1]);
               } else {
                  Object[] convObjNInstanceDatatype = this.getConvObjNInstanceDatatype(ce, from_sqs, to_sqs);
                  conv_ce.add(convObjNInstanceDatatype[0]);
                  dataType_ce.add((String)convObjNInstanceDatatype[1]);
               }
            }
         }

         return new Object[]{conv_ce, dataType_ce.peek()};
      }
   }

   public Object convertToMySQL(Object ce, SelectQueryStatement from_sqs, SelectQueryStatement to_sqs) throws ConvertException {
      Object convObj = new Object();
      if (ce instanceof TableColumn) {
         convObj = ((TableColumn)ce).toMySQLSelect(to_sqs, from_sqs);
      } else if (ce instanceof FunctionCalls) {
         convObj = ((FunctionCalls)ce).toMySQLSelect(to_sqs, from_sqs);
      } else if (ce instanceof CaseStatement) {
         convObj = ((CaseStatement)ce).toMySQLSelect(to_sqs, from_sqs);
      } else if (ce instanceof SelectColumn) {
         convObj = ((SelectColumn)ce).toMySQLSelect(to_sqs, from_sqs);
      } else if (ce instanceof SelectQueryStatement) {
         convObj = ((SelectQueryStatement)ce).toMySQL();
      } else if (ce instanceof WhereItem) {
         convObj = ((WhereItem)ce).toMySQLSelect(to_sqs, from_sqs);
      } else if (ce instanceof WhereExpression) {
         convObj = ((WhereExpression)ce).toMySQLSelect(to_sqs, from_sqs);
      } else if (ce instanceof String) {
         String s_ce = (String)ce;
         if (s_ce.equalsIgnoreCase("CURRENT TIME")) {
            convObj = "CURRENT_TIME";
         } else if (s_ce.equalsIgnoreCase("CURRENT DATE")) {
            convObj = "CURRENT_DATE";
         } else if (s_ce.equalsIgnoreCase("CURRENT TIMESTAMP")) {
            convObj = "CURRENT_TIMESTAMP";
         } else if (s_ce.equalsIgnoreCase("CURRENT")) {
            convObj = "CURRENT_DATE";
         } else {
            convObj = s_ce;
         }
      } else if (ce instanceof VariableColumn) {
         convObj = ce;
      }

      return convObj;
   }

   public String getInstanceDataTypeForObj(Object ce, InstanceDataTypeHandler dataTypeHandler, SelectQueryStatement from_sqs) throws ConvertException {
      String instancetype = "UNDEFINED";
      if (ce instanceof TableColumn) {
         instancetype = dataTypeHandler.getInstanceDataTypeForTableColumn((TableColumn)ce, from_sqs);
      } else if (ce instanceof FunctionCalls) {
         String fnName = String.valueOf(((FunctionCalls)ce).getFunctionName()).toUpperCase();
         Object[] fnProps = (Object[])((Object[])this.validation_handler.getWhiteListedFunctions().get(fnName));
         instancetype = dataTypeHandler.getInstanceDataTypeForFunctionCall((FunctionCalls)ce, fnProps);
         if (instancetype == "UNDEFINED") {
            instancetype = ((FunctionCalls)ce).getInstanceDatatype();
         }
      } else if (ce instanceof CaseStatement) {
         instancetype = dataTypeHandler.getInstanceDataTypeForCaseStatement((CaseStatement)ce);
      } else if (ce instanceof SelectColumn) {
         instancetype = dataTypeHandler.getInstanceDataTypeForSelectColumn((SelectColumn)ce);
      } else if (ce instanceof SelectQueryStatement) {
         instancetype = dataTypeHandler.getInstanceDataTypeForSQS((SelectQueryStatement)ce);
      } else if (ce instanceof WhereItem) {
         instancetype = dataTypeHandler.getInstanceDataTypeForWhereItem((WhereItem)ce);
      } else if (ce instanceof WhereExpression) {
         instancetype = dataTypeHandler.getInstanceDataTypeForWhereExpression((WhereExpression)ce);
      } else if (ce instanceof VariableColumn) {
         instancetype = dataTypeHandler.getInstanceDataTypeForVariableColumn((VariableColumn)ce);
      } else if (ce instanceof String) {
         instancetype = dataTypeHandler.getInstanceDataTypeForString((String)ce);
      }

      return instancetype;
   }

   public Object[] getConvObjNInstanceDatatype(Object ce, SelectQueryStatement from_sqs, SelectQueryStatement to_sqs) throws ConvertException {
      FunctionValidateHandler validation_handler = from_sqs.getValidationHandler();
      String instancetype = "UNDEFINED";
      Object convObj = new Object();
      String s_ce;
      if (ce instanceof TableColumn) {
         s_ce = this.dataTypeHandler.getInstanceDataTypeForTableColumn((TableColumn)ce, from_sqs);
         convObj = ((TableColumn)ce).toMySQLSelect(to_sqs, from_sqs);
         if (s_ce != null) {
            ((TableColumn)convObj).setInstanceDatatype(s_ce);
            instancetype = s_ce;
         }
      } else if (ce instanceof FunctionCalls) {
         s_ce = String.valueOf(((FunctionCalls)ce).getFunctionName()).toUpperCase();
         Object[] fnProps = (Object[])((Object[])validation_handler.getWhiteListedFunctions().get(s_ce));
         String fn_instancetype = this.dataTypeHandler.getInstanceDataTypeForFunctionCall((FunctionCalls)ce, fnProps);
         convObj = ((FunctionCalls)ce).toMySQLSelect(to_sqs, from_sqs);
         if (fn_instancetype != null && fn_instancetype.equalsIgnoreCase("UNDEFINED")) {
            fn_instancetype = ((FunctionCalls)ce).getInstanceDatatype();
         }

         ((FunctionCalls)convObj).setInstanceDatatype(fn_instancetype);
         instancetype = fn_instancetype;
      } else if (ce instanceof CaseStatement) {
         convObj = ((CaseStatement)ce).toMySQLSelect(to_sqs, from_sqs);
         instancetype = this.dataTypeHandler.getInstanceDataTypeForCaseStatement((CaseStatement)ce);
      } else if (ce instanceof SelectColumn) {
         convObj = ((SelectColumn)ce).toMySQLSelect(to_sqs, from_sqs);
         instancetype = this.dataTypeHandler.getInstanceDataTypeForSelectColumn((SelectColumn)convObj);
      } else if (ce instanceof SelectQueryStatement) {
         ((SelectQueryStatement)ce).setInstanceDataTypeHandler(from_sqs.getinstanceDataTypeHandler());
         ((SelectQueryStatement)ce).setValidationHandler(from_sqs.getValidationHandler());
         convObj = ((SelectQueryStatement)ce).toMySQL();
         instancetype = this.dataTypeHandler.getInstanceDataTypeForSQS((SelectQueryStatement)convObj);
      } else if (ce instanceof WhereItem) {
         convObj = ((WhereItem)ce).toMySQLSelect(to_sqs, from_sqs);
         instancetype = this.dataTypeHandler.getInstanceDataTypeForWhereItem((WhereItem)convObj);
      } else if (ce instanceof WhereExpression) {
         convObj = ((WhereExpression)ce).toMySQLSelect(to_sqs, from_sqs);
         instancetype = this.dataTypeHandler.getInstanceDataTypeForWhereExpression((WhereExpression)convObj);
      } else if (ce instanceof VariableColumn) {
         instancetype = this.dataTypeHandler.getInstanceDataTypeForVariableColumn((VariableColumn)ce);
         convObj = ce;
         ((VariableColumn)ce).setInstanceDatatype(instancetype);
      } else if (ce instanceof String) {
         s_ce = (String)ce;
         if (s_ce.equalsIgnoreCase("CURRENT TIME")) {
            convObj = "CURRENT_TIME";
            instancetype = "TIME";
         } else if (s_ce.equalsIgnoreCase("CURRENT DATE")) {
            convObj = "CURRENT_DATE";
            instancetype = "DATE";
         } else if (s_ce.equalsIgnoreCase("CURRENT TIMESTAMP")) {
            convObj = "CURRENT_TIMESTAMP";
            instancetype = "DATE";
         } else if (s_ce.equalsIgnoreCase("CURRENT")) {
            convObj = "CURRENT_DATE";
            instancetype = "DATE";
         } else {
            try {
               String s_ce_temp = s_ce;
               if (s_ce.startsWith("'") && s_ce.endsWith("'") && s_ce.length() > 2) {
                  s_ce_temp = s_ce.substring(1, s_ce.length() - 1);
               }

               Double.parseDouble(s_ce_temp);
               convObj = ce;
               instancetype = "NUMBER";
            } catch (NumberFormatException var10) {
               if (!BOOLEAN_FALSE_SYNONYMS.contains(s_ce.toUpperCase()) && !BOOLEAN_TRUE_SYNONYMS.contains(s_ce.toUpperCase())) {
                  convObj = s_ce;
                  instancetype = "PLAIN";
               } else {
                  convObj = s_ce;
                  instancetype = "BOOLEAN";
               }
            }
         }
      }

      return new Object[]{convObj, instancetype};
   }

   public SelectColumn scForUnaryOperator(String operator, Object operand) {
      try {
         SelectColumn sc = new SelectColumn();
         Vector ce = new Vector();
         ce.add(operator);
         ce.add(operand);
         sc.setColumnExpression(ce);
         return sc;
      } catch (Exception var5) {
         return null;
      }
   }

   public static Boolean checkForIntervalSc(Object obj, int i) {
      Boolean isIntervalSc = false;
      Vector v_ce;
      if (obj instanceof SelectColumn) {
         v_ce = ((SelectColumn)obj).getColumnExpression();
         isIntervalSc = checkVectorForSc(v_ce, i);
      } else if (obj instanceof Vector) {
         v_ce = (Vector)obj;
         isIntervalSc = checkVectorForSc(v_ce, i);
      }

      return isIntervalSc;
   }

   public static Boolean checkVectorForSc(Vector v_ce, int i) {
      return v_ce.size() < i + 3 ? false : v_ce.get(i) instanceof TableColumn && ((TableColumn)v_ce.get(i)).getColumnName().equalsIgnoreCase("INTERVAL") && (v_ce.get(i + 1) instanceof SelectColumn || v_ce.get(i + 1) instanceof String);
   }

   public Object[] getExpressionNReturnType(Object op1, Object op2, Object operator, String dt1, String dt2) throws ConvertException {
      String returnType = "UNDEFINED";
      SelectColumn operation = new SelectColumn();
      Vector ce = new Vector();
      HashMap<Object, String> subTypeVsGeneralSubTypeGroup = new HashMap(this.dataTypeHandler.getSubTypeVsGeneralSubTypeGroup());
      if (!dt1.equals("UNDEFINED") && !dt2.equals("UNDEFINED")) {
         String dt1_subGroup = (String)subTypeVsGeneralSubTypeGroup.get(dt1);
         String dt2_subGroup = (String)subTypeVsGeneralSubTypeGroup.get(dt2);
         String operation_key = dt2_subGroup.toString() + operator + dt1_subGroup;
         if (dt1_subGroup.equalsIgnoreCase("BOOLEAN") && op1 instanceof String) {
            op1 = validateBooleanString(op1.toString());
         }

         if (dt2_subGroup.equalsIgnoreCase("BOOLEAN") && op2 instanceof String) {
            op2 = validateBooleanString(op2.toString());
         }

         Object[] wrappedOperationNreturnType = this.dataTypeHandler.getWrappedOperation(operation_key, op1, op2, operator, dt1, dt2);
         if (wrappedOperationNreturnType != null) {
            return wrappedOperationNreturnType;
         }

         if (COMPARATIVE_OPERATORS.contains(operator.toString())) {
            returnType = "BOOLEAN";
            ce.add(op2);
            ce.add(operator);
            ce.add(op1);
         } else if (operator.toString().equalsIgnoreCase("::")) {
            ce.add(CreateConvertedFunctionsUtil.createCastFunction(op1, op2));
            returnType = CreateConvertedFunctionsUtil.getCastFunctionDataType(op1);
         } else if ((dt1_subGroup.equalsIgnoreCase("NUMBER") || dt1_subGroup.equalsIgnoreCase("BOOLEAN")) && (dt2_subGroup.equalsIgnoreCase("NUMBER") || dt2_subGroup.equalsIgnoreCase("BOOLEAN")) && !COMPARATIVE_OPERATORS.contains(operator)) {
            ce.add(CreateConvertedFunctionsUtil.createDefaultOperation(op2, op1, operator, "NUMBER"));
            returnType = "NUMBER";
         } else if (operator.toString().equalsIgnoreCase("+")) {
            returnType = "PLAIN";
            ce.add(CreateConvertedFunctionsUtil.createSimpleFunctionCallWithTwoArgs(op2, op1, "CONCAT", returnType));
         } else if (!operator.toString().equalsIgnoreCase("^") && !operator.toString().equalsIgnoreCase("**")) {
            if (operator.toString().equalsIgnoreCase("-")) {
               ce.add(op2);
               ce.add(operator);
               ce.add(op1);
               returnType = "NUMBER";
            } else if (!operator.toString().equalsIgnoreCase("/") && !operator.toString().equalsIgnoreCase("div")) {
               if (operator.toString().equalsIgnoreCase("%")) {
                  ce.add(CreateConvertedFunctionsUtil.createSimpleFunctionCallWithTwoArgs(op2, op1, "MOD", "NUMBER"));
                  returnType = "NUMBER";
               } else {
                  ce.add(op2);
                  ce.add(operator);
                  ce.add(op1);
                  returnType = "UNDEFINED";
               }
            } else {
               ce.add(op2);
               ce.add(operator);
               ce.add(op1);
               returnType = "NUMBER";
            }
         } else {
            returnType = "NUMBER";
            ce.add(CreateConvertedFunctionsUtil.createSimpleFunctionCallWithTwoArgs(op2, op1, "POWER", returnType));
         }
      } else {
         ce.add(op2);
         ce.add(operator);
         ce.add(op1);
      }

      operation.setColumnExpression(ce);
      operation.setInstanceDatatype(returnType);
      return new Object[]{operation, returnType};
   }

   private static Object validateBooleanString(String op) {
      if (BOOLEAN_TRUE_SYNONYMS.contains(op.toUpperCase())) {
         op = "1";
      } else if (BOOLEAN_FALSE_SYNONYMS.contains(op.toUpperCase())) {
         op = "0";
      }

      return op;
   }

   static {
      OPERATOR_VS_PRECEDENCE = Collections.unmodifiableMap(OPERATORVsPRECEDENCE_unm);
      unaryOperators_unm = new ArrayList<String>() {
         {
            this.add("+");
            this.add("-");
            this.add("~");
         }
      };
      UNARY_OPERATORS = Collections.unmodifiableList(unaryOperators_unm);
      splStringFns_unm = new ArrayList<String>() {
         {
            this.add("CURRENT TIME");
            this.add("CURRENT DATE");
            this.add("CURRENT");
            this.add("CURRENT TIMESTAMP");
         }
      };
      SPL_STRING_FNS = Collections.unmodifiableList(splStringFns_unm);
      COMPARATIVE_OPERATORS_unm = new ArrayList<String>() {
         {
            this.add(">");
            this.add("<");
            this.add(">=");
            this.add("<=");
            this.add("&");
            this.add("|");
            this.add("<>");
            this.add("=");
            this.add("!=");
         }
      };
      COMPARATIVE_OPERATORS = Collections.unmodifiableList(COMPARATIVE_OPERATORS_unm);
      ALLOWED_OPERATORS = Collections.unmodifiableSet(OPERATOR_VS_PRECEDENCE.keySet());
      BOOLEAN_TRUE_SYNONYMS_unm = new ArrayList<String>() {
         {
            this.add("TRUE");
            this.add("ON");
            this.add("1");
            this.add("YES");
         }
      };
      BOOLEAN_TRUE_SYNONYMS = Collections.unmodifiableList(BOOLEAN_TRUE_SYNONYMS_unm);
      BOOLEAN_FALSE_SYNONYMS_unm = new ArrayList<String>() {
         {
            this.add("FALSE");
            this.add("OFF");
            this.add("0");
            this.add("NO");
         }
      };
      BOOLEAN_FALSE_SYNONYMS = Collections.unmodifiableList(BOOLEAN_FALSE_SYNONYMS_unm);
   }
}
