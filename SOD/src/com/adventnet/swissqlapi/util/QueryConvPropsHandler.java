package com.adventnet.swissqlapi.util;

import java.util.HashMap;

public class QueryConvPropsHandler {
   protected HashMap<String, Object> queryConvProps = new HashMap<String, Object>() {
      {
         this.put("allow.cte.query.conversion", true);
         this.put("full.join.count", 4);
         this.put("allow.multiple.full.join", false);
         this.put("replace.double.dots.in.table.name", true);
         this.put("allow.logical.exp.in.agg.function", false);
         this.put("consider.precision.for.to.decimal", false);
         this.put("allow.new.flow.for.plus", false);
         this.put("allow.for.plus.operator.as.arthematic.operator", false);
         this.put("convert.keywords.to.relative.form", true);
         this.put("function.arguments.count.mismatch", false);
         this.put("long.nested.if.count", false);
         this.put("max.nested.if.count", (Object)null);
         this.put("use.udf.functions.for.numeric", false);
         this.put("use.udf.functions.for.text", false);
         this.put("use.udf.functions.for.date.time", false);
         this.put("removal.option.for.order.and.fetch.clauses", -1);
         this.put("can.handle.string.literals.for.numeric", false);
         this.put("can.handle.string.literals.for.date.time", false);
         this.put("can.cast.string.literal.to.text", false);
         this.put("can.cast.string.literal.to.text.in.where.item", false);
         this.put("can.cast.string.literal.to.text.in.select.column.expression", false);
         this.put("can.use.udf.for.time.function", false);
         this.put("can.use.udf.for.decimal.casting", false);
         this.put("can.use.offset.computation.for.convert.tz", false);
         this.put("can.cast.string.functions.to.text.inside.if.function", false);
         this.put("can.use.mod.function", false);
         this.put("can.use.if.for.case.when.exp", false);
         this.put("can.handle.having.without.group.by", false);
         this.put("can.use.citext.over.text", false);
         this.put("can.use.collate.for.string.functions", false);
         this.put("can.use.date.part.instead.of.extract", true);
         this.put("cbc.encrypt.new.function", false);
         this.put("can.cast.set.query.list.sel.cols", false);
         this.put("use.udf.functions.for.str.to.date", false);
         this.put("can.use.back.tip.in.column.name", true);
         this.put("can.use.distinct.from.null.safe.equals.operator", false);
         this.put("set.can.cast.all.to.text.columns", false);
         this.put("null.select.column.flag", false);
         this.put("is.pg.build", false);
      }
   };
   public static final String USE_CTE_QUERY = "allow.cte.query.conversion";
   public static final String FULL_JOIN_COUNT = "full.join.count";
   public static final String ALLOW_MULTIPLE_FULL_JOIN = "allow.multiple.full.join";
   public static final String REPLACE_DOUBLE_DOTS_IN_TABLE_NAME = "replace.double.dots.in.table.name";
   public static final String ALLOW_LOGICAL_EXP_IN_AGG_FUN = "allow.logical.exp.in.agg.function";
   public static final String CONSIDER_PRECISION_FOR_TO_DECIMAL = "consider.precision.for.to.decimal";
   public static final String ALLOW_NEW_FLOW_FOR_PLUS_OPERATOR_CASE = "allow.new.flow.for.plus";
   public static final String ALLOW_FOR_PLUS_OPERATOR_AS_ARTHEMATIC_OPERATOR = "allow.for.plus.operator.as.arthematic.operator";
   public static final String CONVERT_KEYWORDS_TO_RELATIVE_FORM = "convert.keywords.to.relative.form";
   public static final String HANDLE_FUNCTION_ARGUMENTS_COUNT_MISMATCH = "function.arguments.count.mismatch";
   public static final String CHECK_LONG_NESTED_IF_COUNT = "long.nested.if.count";
   public static final String MAX_NESTED_IF_COUNT = "max.nested.if.count";
   public static final String USE_UDF_FUNCTIONS_FOR_NUMERIC = "use.udf.functions.for.numeric";
   public static final String USE_UDF_FUNCTIONS_FOR_TEXT = "use.udf.functions.for.text";
   public static final String USE_UDF_FUNCTIONS_FOR_DATE_TIME = "use.udf.functions.for.date.time";
   public static final String REMOVAL_OPTION_FOR_ORDER_AND_FETCH_CLAUSES = "removal.option.for.order.and.fetch.clauses";
   public static final String CAN_HANDLE_STRING_LITERALS_FOR_NUMERIC = "can.handle.string.literals.for.numeric";
   public static final String CAN_HANDLE_STRING_LITERALS_FOR_DATE_TIME = "can.handle.string.literals.for.date.time";
   public static final String CAN_HANDLE_NULLS_INSIDE_IN_CLAUSE = "can.handle.nulls.inside.in.clause";
   public static final String CAN_CAST_STRING_LITERAL_TO_TEXT = "can.cast.string.literal.to.text";
   public static final String CAN_CAST_STRING_LITERAL_TO_TEXT_IN_WHERE_ITEM = "can.cast.string.literal.to.text.in.where.item";
   public static final String CAN_CAST_STRING_LITERAL_TO_TEXT_IN_SELECT_COLUMN_EXPRESSION = "can.cast.string.literal.to.text.in.select.column.expression";
   public static final String CAN_USE_UDF_FOR_TIME_FUNCTION = "can.use.udf.for.time.function";
   public static final String CAN_USE_UDF_FOR_DECIMAL_CASTING = "can.use.udf.for.decimal.casting";
   public static final String CAN_USE_OFFSET_COMPUTATION_FOR_CONVERT_TZ = "can.use.offset.computation.for.convert.tz";
   public static final String CAN_CAST_STRING_FUNCTIONS_TO_TEXT_INSIDE_IF_FUNCTION = "can.cast.string.functions.to.text.inside.if.function";
   public static final String CAN_USE_MOD_FUNCTION = "can.use.mod.function";
   public static final String CAN_USE_IF_FUNC_FOR_CASE_WHEN_EXP = "can.use.if.for.case.when.exp";
   public static final String CAN_HANDLE_HAVING_WITHOUT_GROUP_BY = "can.handle.having.without.group.by";
   public static final String CAN_USE_CITEXT_OVER_TEXT = "can.use.citext.over.text";
   public static final String CAN_USE_COLLATE_FOR_STRING_FUNCTIONS = "can.use.collate.for.string.functions";
   public static final String CAN_USE_DATE_PART_INSTEAD_OF_EXTRACT = "can.use.date.part.instead.of.extract";
   public static final String CBC_ENCRYPT_NEW_FUNCTION = "cbc.encrypt.new.function";
   public static final String CAN_CAST_SET_QUERY_LIST_SEL_COLS = "can.cast.set.query.list.sel.cols";
   public static final String USE_UDF_FUNCTIONS_FOR_STR_TO_DATE = "use.udf.functions.for.str.to.date";
   public static final String CAN_USE_BACK_TIP_IN_COLUMN_NAME = "can.use.back.tip.in.column.name";
   public static final String CAN_USE_DISTINCT_FROM_NULL_SAFE_EQUALS_OPERATOR = "can.use.distinct.from.null.safe.equals.operator";
   public static final String SET_CAN_CAST_ALL_TO_TEXT_COLUMNS = "set.can.cast.all.to.text.columns";
   public static final String NULL_SELECT_COLUMN_FLAG = "null.select.column.flag";
   public static final String IS_PG_BUILD = "is.pg.build";

   public void setQueryConvProps(HashMap convProps) {
      this.queryConvProps = convProps;
   }

   public HashMap getQueryConvProps() {
      return this.queryConvProps;
   }

   public boolean isCTEQuerySupport() {
      return (Boolean)this.queryConvProps.get("allow.cte.query.conversion");
   }
}
