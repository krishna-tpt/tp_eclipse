package com.adventnet.swissqlapi.util;

import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.CaseStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.VariableColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import java.util.Map;

public interface InstanceDataTypeHandler {
   String getInstanceDataTypeForFunctionCall(FunctionCalls var1, Object[] var2);

   String getInstanceDataTypeForCaseStatement(CaseStatement var1);

   String getInstanceDataTypeForSelectColumn(SelectColumn var1);

   String getInstanceDataTypeForSQS(SelectQueryStatement var1);

   String getInstanceDataTypeForTableColumn(TableColumn var1, SelectQueryStatement var2);

   String getInstanceDataTypeForVariableColumn(VariableColumn var1);

   String getInstanceDataTypeForWhereExpression(WhereExpression var1);

   String getInstanceDataTypeForWhereItem(WhereItem var1);

   String getInstanceDataTypeForString(String var1);

   Object[] getWrappedOperation(String var1, Object var2, Object var3, Object var4, String var5, String var6);

   Object createFunctionWithWrapper(Object var1, Object var2, Object var3, String var4, String var5, String var6, String var7);

   Map<Object, String> getSubTypeVsGeneralSubTypeGroup();
}
