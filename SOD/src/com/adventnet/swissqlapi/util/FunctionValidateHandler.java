package com.adventnet.swissqlapi.util;

import java.util.List;
import java.util.Map;

public interface FunctionValidateHandler {
   Map getWhiteListedFunctions();

   List<String> getBlackListedFunctions();

   List<String> getPivotAggFunctions();

   List<String> getNumericTypeFunctions();

   List<String> getStringTypeFunctions();

   List<String> getBooleanTypeFunctions();

   List<String> getAllDateTypeFunctions();

   List<String> getDateTypeFunctions();

   List<String> getDateTimeTypeFunctions();

   List<String> getDurationTypeFunctions();

   List<String> getTimeTypeFunctions();
}
