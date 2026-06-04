package com.adventnet.swissqlapi.sql.functions.date;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.create.DateClass;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import java.util.Vector;

public class ToTimestampTZ extends FunctionCalls {
   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.functionArguments.elementAt(i_count);
            boolean handleToCharForToTimestampTZ = false;
            if (sc.getColumnExpression().firstElement() instanceof FunctionCalls && ((FunctionCalls)sc.getColumnExpression().firstElement()).getFunctionName().getColumnName().equalsIgnoreCase("to_char")) {
               handleToCharForToTimestampTZ = true;
            }

            sc = sc.toTeradataSelect(to_sqs, from_sqs);
            if (handleToCharForToTimestampTZ) {
               Object toCharFn = ((FunctionCalls)sc.getColumnExpression().firstElement()).getFunctionArguments().get(0);
               sc.getColumnExpression().setElementAt(toCharFn, 0);
            }

            arguments.addElement(sc);
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      SelectColumn sc1 = (SelectColumn)arguments.get(0);
      Vector newArguments = new Vector();
      String tzr = sc1.getColumnExpression().lastElement().toString();
      String tzrTrimmed = tzr.substring(1, tzr.length() - 1).trim().toUpperCase();
      if (tzrTrimmed.startsWith(":")) {
         tzrTrimmed = tzrTrimmed.substring(1);
      }

      if (tzr.startsWith("'") && (tzr.indexOf("/") != -1 || tzrTrimmed.startsWith("GMT") || SwisSQLUtils.getOracleTimeZones().contains(tzrTrimmed)) && tzrTrimmed.indexOf("MM") == -1 && tzrTrimmed.indexOf("YY") == -1 && tzrTrimmed.indexOf("DD") == -1) {
         tzr = tzr.substring(1, tzr.length() - 1).trim();
         if (tzr.startsWith(":")) {
            tzr = tzr.substring(1);
         }

         newArguments.add("'" + tzr + "'");
         if (this.atTimeZoneRegion == null) {
            newArguments.add("'" + tzr + "'");
         } else {
            SelectColumn convAtTimeZoneRegion = this.getAtTimeZoneRegion().toTeradataSelect(to_sqs, from_sqs);

            for(int j = 0; j < convAtTimeZoneRegion.getColumnExpression().size(); ++j) {
               Object obj = convAtTimeZoneRegion.getColumnExpression().get(j);
               if (obj instanceof String) {
                  String objStr = obj.toString().trim();
                  if (objStr.startsWith("'")) {
                     objStr = objStr.substring(1, objStr.length() - 1).trim();
                  }

                  if (objStr.startsWith(":")) {
                     objStr = objStr.substring(1);
                  }

                  convAtTimeZoneRegion.getColumnExpression().setElementAt("'" + objStr + "'", j);
               }
            }

            newArguments.add(convAtTimeZoneRegion);
         }

         sc1.getColumnExpression().removeElementAt(sc1.getColumnExpression().size() - 1);
         if (sc1.getColumnExpression().lastElement().toString().equalsIgnoreCase("||")) {
            sc1.getColumnExpression().removeElementAt(sc1.getColumnExpression().size() - 1);
         }

         arguments.removeElementAt(arguments.size() - 1);
         arguments.addAll(newArguments);
      }

      boolean isDateArg = false;
      FunctionCalls dateFunc;
      if (arguments.elementAt(0) instanceof SelectColumn) {
         if (((SelectColumn)arguments.elementAt(0)).getColumnExpression().get(0) instanceof FunctionCalls) {
            dateFunc = (FunctionCalls)((SelectColumn)arguments.elementAt(0)).getColumnExpression().get(0);
            if (dateFunc.getFunctionName() != null && SwisSQLUtils.getFunctionReturnType(dateFunc.getFunctionName().getColumnName(), dateFunc.getFunctionArguments()).equalsIgnoreCase("date")) {
               isDateArg = true;
            }
         } else if (((SelectColumn)arguments.elementAt(0)).getColumnExpression().get(0) instanceof SelectColumn) {
            SelectColumn funcCol = (SelectColumn)((SelectColumn)arguments.elementAt(0)).getColumnExpression().get(0);
            if (funcCol.getColumnExpression().get(0) instanceof FunctionCalls) {
               FunctionCalls dateFunc = (FunctionCalls)funcCol.getColumnExpression().get(0);
               if (dateFunc.getFunctionName() != null && SwisSQLUtils.getFunctionReturnType(dateFunc.getFunctionName().getColumnName(), dateFunc.getFunctionArguments()).equalsIgnoreCase("date")) {
                  isDateArg = true;
               }
            }
         }
      }

      if (isDateArg) {
         dateFunc = new FunctionCalls();
         TableColumn castTcn = new TableColumn();
         castTcn.setColumnName("CAST");
         dateFunc.setFunctionName(castTcn);
         dateFunc.setAsDatatype("AS");
         DateClass castDatatype = new DateClass();
         castDatatype.setDatatypeName("TIMESTAMP");
         castDatatype.setOpenBrace("(");
         castDatatype.setSize("0");
         castDatatype.setClosedBrace(")");
         Vector castTimestampArgs = new Vector();
         castTimestampArgs.add(arguments.elementAt(0));
         castTimestampArgs.add(castDatatype);
         dateFunc.setFunctionArguments(castTimestampArgs);
         arguments.setElementAt(dateFunc, 0);
      }

      this.setFunctionArguments(arguments);
      this.setAtTimeZoneRegion((SelectColumn)null);
   }
}
