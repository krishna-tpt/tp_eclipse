package com.zoho.analytics.jdbc;

import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.parser.ParseException;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;
import com.adventnet.swissqlapi.sql.statement.alter.AlterStatement;
import com.adventnet.swissqlapi.sql.statement.alter.AlterTable;
import com.adventnet.swissqlapi.sql.statement.create.CreateColumn;
import com.adventnet.swissqlapi.sql.statement.create.Datatype;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.util.misc.GeneralUtil;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONException;
import org.json.JSONObject;

class ZohoAnalyticsQueryParserUtil {
   static SwisSQLStatement parseSQLQuery(String sql) throws ParseException, ConvertException, SQLException {
      sql = ZohoAnalyticsJDBCUtil.validateAndTrimSQL(sql);
      SwisSQLAPI swis = new SwisSQLAPI();
      swis.setSQLString(sql);
      SwisSQLStatement swisSt = swis.parse();
      return swisSt;
   }

   static String parseWhereExpressions(WhereExpression whereExp, String tableName) {
      if (whereExp == null) {
         return null;
      } else {
         String criteriaValue = "";
         Vector<String> whereOperatorList = whereExp.getOperator();
         int ind = 0;

         for(int i = 0; i < whereExp.getWhereItems().size(); ++i) {
            Object item = whereExp.getWhereItems().get(i);
            if (item instanceof WhereExpression) {
               ((WhereExpression)item).removeBrace();
               String nestedCriteria = parseWhereExpressions((WhereExpression)item, tableName);
               if (i < whereOperatorList.size()) {
                  String centralOperator = whereExp.getOperator().get(i).toString();
                  criteriaValue = criteriaValue + "(" + nestedCriteria + ")" + " " + centralOperator;
               } else {
                  criteriaValue = criteriaValue + "(" + nestedCriteria + ")";
               }
            } else if (item instanceof WhereItem) {
               criteriaValue = criteriaValue + parseWhereItem((WhereItem)item, tableName);
               if (!whereOperatorList.isEmpty() && ind != whereOperatorList.size() && i < whereOperatorList.size()) {
                  criteriaValue = criteriaValue + (String)whereOperatorList.get(ind);
                  ++ind;
               }
            }
         }

         return criteriaValue;
      }
   }

   static String parseWhereItem(WhereItem item, String tableName) {
      String leftExp = GeneralUtil.trimIfAliasNameIsEnclosed(item.getLeftWhereExp().toString().trim());
      String rightExp = item.getRightWhereExp().toString().trim();
      String operator = GeneralUtil.trimIfAliasNameIsEnclosed(item.getOperator());
      return "\"" + tableName.trim() + "\".\"" + leftExp + "\"" + operator + " " + rightExp;
   }

   static JSONObject parseAlterStatementVector(ZohoAnalyticsWorkspace workspace, String sql) throws ParseException, SQLException, ConvertException, JSONException {
      SwisSQLStatement parsedQuery = parseSQLQuery(sql);
      AlterStatement alt = (AlterStatement)parsedQuery;
      String tableName = GeneralUtil.trimIfAliasNameIsEnclosed(alt.getTableName().toString()).trim();
      long viewId = workspace.getViewIdFromName(tableName);
      AlterTable alterStVector = (AlterTable)alt.getAlterStatementVector().firstElement();
      JSONObject alter = new JSONObject();
      alter.put("viewId", viewId);
      alter.put("viewName", tableName);
      alter.put("alterStVector", alterStVector);
      return alter;
   }

   static JSONObject constructColumnItem(CreateColumn col, ArrayList<String> columnNames) throws SQLException {
      JSONObject columnItem = new JSONObject();
      String columnNameWithSpacesAndQuotes = col.getColumnName();
      String columnNameTrimmed = GeneralUtil.trimIfAliasNameIsEnclosed(columnNameWithSpacesAndQuotes).trim();
      columnItem.put("COLUMNNAME", columnNameTrimmed);
      columnNames.add(columnNameTrimmed);
      ArrayList defaultArr = col.getDefaultExpression();
      String nullCheck = col.getNullStatus();
      String userDefinedDataType;
      if (defaultArr != null) {
         userDefinedDataType = String.valueOf(defaultArr.get(0));
         userDefinedDataType = userDefinedDataType.replaceAll("^['\"]|['\"]$", "");
         userDefinedDataType = userDefinedDataType.replaceAll("^['\"]|['\"]$", "");
         columnItem.put("DEFAULT", userDefinedDataType);
      }

      if (nullCheck != null && nullCheck.equalsIgnoreCase("NOT NULL")) {
         columnItem.put("MANDATORY", true);
      }

      userDefinedDataType = col.getUserDefinedDatatype();
      if (userDefinedDataType == null || userDefinedDataType.trim().isEmpty()) {
         Datatype preDefinedDatatype = col.getDatatype();
         userDefinedDataType = preDefinedDatatype.toString().trim();
      }

      if (userDefinedDataType.contains("GEO")) {
         String regex = "(GEO)_(\\d+)";
         Pattern pattern = Pattern.compile(regex);
         Matcher matcher = pattern.matcher(userDefinedDataType);
         if (!matcher.find()) {
            throw new SQLException(MessageFormat.format("\"Data type 'GEO' must be formatted as ''GEO_{{GEOROLE}}''. Sample: ''GEO_2''", ""), "", 7292);
         }

         userDefinedDataType = matcher.group(1);
         columnItem.put("GEOROLE", matcher.group(2));
      }

      if (ZohoAnalyticsJDBCConstants.GEO_ROLE_DATA_TYPE.contains(userDefinedDataType)) {
         columnItem.put("GEOROLE", ZohoAnalyticsJDBCConstants.GEO_ROLE_DATA_TYPE.indexOf(userDefinedDataType));
         userDefinedDataType = "GEO";
      }

      columnItem.put("DATATYPE", userDefinedDataType);
      return columnItem;
   }
}
