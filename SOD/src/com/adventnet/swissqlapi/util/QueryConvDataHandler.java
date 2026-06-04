package com.adventnet.swissqlapi.util;

import java.util.HashMap;
import java.util.Set;

public interface QueryConvDataHandler {
   void setPivotOperatorSelItemsLimit(int var1);

   void setPivotClauseInValuesLimit(int var1);

   void setTableDetsMap(HashMap<String, String> var1);

   void setTableColumnDetsMap(HashMap<String, HashMap<String, String>> var1);

   void setProcessingTableName(String var1);

   void setIsReverseParseConv(boolean var1);

   void setIsUserQuery(boolean var1);

   void setIsRenameRequest(boolean var1);

   void addExceptionMap(String var1, Object[] var2);

   Integer getPivotOperatorSelItemsLimit();

   Integer getPivotClauseInValuesLimit();

   HashMap<String, String> getTableDetsMap();

   HashMap<String, HashMap<String, String>> getTableColumnDetsMap();

   String getProcessingTableName();

   boolean isReverseParseConv();

   boolean isUserQuery();

   Set<String> getAllowedKeyWords();

   Set<String> getFunctionListWithAllowedKeyWords();

   boolean isRenameRequest();

   HashMap<String, HashMap<String, Object>> getExceptionMap();
}
