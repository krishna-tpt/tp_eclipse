package com.adventnet.swissqlapi.util.misc;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import java.util.HashMap;
import java.util.Vector;

public class GeneralUtil {
   public static String[] getReplacedTblColDets(HashMap<String, String> tblColDets, String inTblColName) throws ConvertException {
      char quote = '1';
      String trimTblColName = inTblColName;
      if (isItEnclosedTblCol(inTblColName)) {
         quote = inTblColName.charAt(0);
         trimTblColName = trimStartAndEndChars(inTblColName);
      }

      String dbTblColName = getConvName(tblColDets, trimTblColName);
      String outTblColName = encloseColOrTabWithProperQuote(dbTblColName, quote);
      return new String[]{trimTblColName, dbTblColName, outTblColName};
   }

   public static String getConvName(HashMap<String, String> tblColDets, String rawTblColName) {
      Object dbTblColName = CastingUtil.getValueIgnoreCase(tblColDets, rawTblColName);
      if (dbTblColName == null) {
      }

      return dbTblColName.toString();
   }

   public static boolean isItEnclosedTblCol(String str) {
      return str.startsWith("\"") && str.endsWith("\"") || str.startsWith("[") && str.endsWith("]") || str.startsWith("`") && str.endsWith("`");
   }

   public static String trimIfTblColIsEnclosed(String str) {
      return str != null && isItEnclosedTblCol(str) ? str.substring(1, str.length() - 1) : str;
   }

   public static boolean isItEnclosedAliasName(String str) {
      return str.startsWith("\"") && str.endsWith("\"") || str.startsWith("'") && str.endsWith("'") || str.startsWith("`") && str.endsWith("`") || str.startsWith("[") && str.endsWith("]");
   }

   public static String trimIfAliasNameIsEnclosed(String str) {
      return str != null && isItEnclosedAliasName(str) ? str.substring(1, str.length() - 1) : str;
   }

   public static String trimStartAndEndChars(String str) {
      return str.substring(1, str.length() - 1);
   }

   public static String encloseColOrTabWithProperQuote(String colOrTabName, char inputQuote) throws ConvertException {
      char quoteToBe = quoteToBe(colOrTabName);
      if (quoteToBe == '1') {
         if (inputQuote == '1') {
            return colOrTabName;
         }

         quoteToBe = inputQuote;
      }

      if (quoteToBe == '[') {
         colOrTabName = '[' + colOrTabName + ']';
      } else {
         colOrTabName = quoteToBe + colOrTabName + quoteToBe;
      }

      return colOrTabName;
   }

   public static char quoteToBe(String colOrTabName) throws ConvertException {
      char quoteToBe = '1';
      boolean isBackTickPresent = colOrTabName.contains("`");
      boolean isDoubleQuotePresent = colOrTabName.contains("\"");
      boolean isSquareBracesPresent = colOrTabName.contains("[") || colOrTabName.contains("]");
      if (isBackTickPresent && isDoubleQuotePresent && isSquareBracesPresent) {
         throw new ConvertException("Invalid Alias Name " + colOrTabName);
      } else {
         if (isDoubleQuotePresent && isSquareBracesPresent) {
            quoteToBe = '`';
         } else if (isSquareBracesPresent && isBackTickPresent) {
            quoteToBe = '"';
         } else if (isBackTickPresent && isDoubleQuotePresent) {
            quoteToBe = '[';
         } else if (isBackTickPresent) {
            quoteToBe = '"';
         } else if (isDoubleQuotePresent) {
            quoteToBe = '`';
         } else if (isSquareBracesPresent) {
            quoteToBe = '`';
         }

         return quoteToBe;
      }
   }

   public static int checkVectorandGetMaxNestedIfCount(Vector vc, int checkingStatus) {
      int maxNestedIfCount = 0;

      for(int i = 0; i < vc.size(); ++i) {
         int ifcount = 0;
         if (vc.get(i) instanceof WhereItem && checkingStatus == 1) {
            WhereItem WI1 = (WhereItem)vc.get(i);
            ifcount = WI1.getWhereItemNestedIfCount();
         } else if (vc.get(i) instanceof WhereExpression && (checkingStatus == 1 || checkingStatus == 3)) {
            WhereExpression WE = (WhereExpression)vc.get(i);
            ifcount = WE.getWhereExpressionNestedIfCount();
         } else if (vc.get(i) instanceof FunctionCalls && (checkingStatus == 2 || checkingStatus == 3)) {
            FunctionCalls fc = (FunctionCalls)vc.get(i);
            ifcount = fc.getFcNestedIfCount();
         } else if (vc.elementAt(i) instanceof SelectColumn && checkingStatus == 4) {
            SelectColumn sc = (SelectColumn)vc.elementAt(i);
            ifcount = sc.getScNestedIfCount();
         }

         if (ifcount > maxNestedIfCount) {
            maxNestedIfCount = ifcount;
         }
      }

      return maxNestedIfCount;
   }

   public static String checkAndEncloseColumnName(String colName) {
      if (!isItEnclosedTblCol(colName) && !colName.matches("^[a-zA-Z0-9_]+$")) {
         if (colName.contains("\"")) {
            colName = "`" + colName + "`";
         } else {
            colName = "\"" + colName + "\"";
         }
      }

      return colName;
   }
}
