package com.adventnet.swissqlapi.sql.functions.misc;

import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.create.DateClass;
import com.adventnet.swissqlapi.sql.statement.select.CaseStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhenStatement;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.util.misc.CastingUtil;
import java.util.ArrayList;
import java.util.Vector;

public class decode extends FunctionCalls {
   private TableColumn corrTableColumn;
   private String dataType;
   private boolean inArithmeticExpr = false;

   public decode() {
      this.CaseString = null;
   }

   public void setInArithmeticExpression(boolean inArithmeticExpr) {
      this.inArithmeticExpr = inArithmeticExpr;
   }

   public void setTargetDataType(String targetDataType) {
      this.dataType = targetDataType;
   }

   public CaseStatement getCaseStatement() {
      return this.caseStatement;
   }

   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) {
      this.CaseString = null;
   }

   public void toANSISQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      StringBuffer sb = new StringBuffer();
      boolean requiresSearchedCaseStatement = false;
      ArrayList positionOfNull = new ArrayList();
      SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() + 2);
      sb.append("\n");

      int i;
      for(i = 0; i < SelectQueryStatement.getBeautyTabCount(); ++i) {
         sb.append("\t");
      }

      for(i = 1; i < this.functionArguments.size() - 1; ++i) {
         if (this.functionArguments.get(i) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.functionArguments.get(i);
            Vector columnList = sc.getColumnExpression();
            if (columnList != null) {
               if (columnList.size() == 1 && columnList.get(0) instanceof String && ((String)columnList.get(0)).trim().equalsIgnoreCase("NULL")) {
                  requiresSearchedCaseStatement = true;
                  positionOfNull.add(i + "");
               } else if (columnList.size() == 1 && columnList.get(0) instanceof String && ((String)columnList.get(0)).trim().equalsIgnoreCase("''")) {
                  requiresSearchedCaseStatement = true;
                  columnList.set(0, "NULL");
                  positionOfNull.add(i + "");
               }
            }
         }
      }

      sb.append("CASE ");
      if (!requiresSearchedCaseStatement) {
         sb.append(((SelectColumn)this.functionArguments.elementAt(0)).toANSISelect(to_sqs, from_sqs).toString() + " ");
      }

      SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() + 1);
      StringBuilder var10001;
      int j;
      if ((this.functionArguments.size() & 1) != 0) {
         for(i = 1; i < this.functionArguments.size(); ++i) {
            sb.append("\n");

            for(j = 0; j < SelectQueryStatement.getBeautyTabCount(); ++j) {
               sb.append("\t");
            }

            sb.append("WHEN ");
            if (requiresSearchedCaseStatement) {
               sb.append(((SelectColumn)this.functionArguments.elementAt(0)).toANSISelect(to_sqs, from_sqs).toString());
               if (positionOfNull.contains(i + "")) {
                  sb.append(" IS ");
               } else {
                  sb.append(" = ");
               }
            }

            sb.append(((SelectColumn)this.functionArguments.elementAt(i)).toANSISelect(to_sqs, from_sqs).toString() + " ");
            sb.append("THEN ");
            var10001 = new StringBuilder();
            ++i;
            sb.append(var10001.append(((SelectColumn)this.functionArguments.elementAt(i)).toANSISelect(to_sqs, from_sqs).toString()).append(" ").toString());
         }
      } else {
         for(i = 1; i < this.functionArguments.size() - 1; ++i) {
            sb.append("\n");

            for(j = 0; j < SelectQueryStatement.getBeautyTabCount(); ++j) {
               sb.append("\t");
            }

            sb.append("WHEN ");
            if (requiresSearchedCaseStatement) {
               sb.append(((SelectColumn)this.functionArguments.elementAt(0)).toANSISelect(to_sqs, from_sqs).toString());
               if (positionOfNull.contains(i + "")) {
                  sb.append(" IS ");
               } else {
                  sb.append(" = ");
               }
            }

            sb.append(((SelectColumn)this.functionArguments.elementAt(i)).toANSISelect(to_sqs, from_sqs).toString() + " ");
            sb.append("THEN ");
            var10001 = new StringBuilder();
            ++i;
            sb.append(var10001.append(((SelectColumn)this.functionArguments.elementAt(i)).toANSISelect(to_sqs, from_sqs).toString()).append(" ").toString());
         }

         sb.append("\n");

         for(j = 0; j < SelectQueryStatement.getBeautyTabCount(); ++j) {
            sb.append("\t");
         }

         sb.append("ELSE ");
         sb.append(((SelectColumn)this.functionArguments.elementAt(i)).toANSISelect(to_sqs, from_sqs).toString() + " ");
      }

      SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() - 1);
      sb.append("\n");

      for(i = 0; i < SelectQueryStatement.getBeautyTabCount(); ++i) {
         sb.append("\t");
      }

      sb.append("END");
      SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() - 2);
      this.CaseString = sb.toString();
      this.functionName = null;
      this.argumentQualifier = null;
      this.functionArguments = null;
   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      StringBuffer sb = new StringBuffer();
      boolean requiresSearchedCaseStatement = false;
      ArrayList positionOfNull = new ArrayList();
      SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() + 2);
      sb.append("\n");

      int i;
      for(i = 0; i < SelectQueryStatement.getBeautyTabCount(); ++i) {
         sb.append("\t");
      }

      for(i = 1; i < this.functionArguments.size() - 1; ++i) {
         if (this.functionArguments.get(i) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.functionArguments.get(i);
            Vector columnList = sc.getColumnExpression();
            if (columnList != null) {
               if (columnList.size() == 1 && columnList.get(0) instanceof String && ((String)columnList.get(0)).trim().equalsIgnoreCase("NULL")) {
                  requiresSearchedCaseStatement = true;
                  positionOfNull.add(i + "");
               } else if (columnList.size() == 1 && columnList.get(0) instanceof String && ((String)columnList.get(0)).trim().equalsIgnoreCase("''")) {
                  requiresSearchedCaseStatement = true;
                  columnList.set(0, "NULL");
                  positionOfNull.add(i + "");
               }
            }
         }
      }

      sb.append("CASE ");
      if (!requiresSearchedCaseStatement) {
         sb.append(((SelectColumn)this.functionArguments.elementAt(0)).toMSSQLServerSelect(to_sqs, from_sqs).toString() + " ");
      }

      SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() + 1);
      StringBuilder var10001;
      int j;
      if ((this.functionArguments.size() & 1) != 0) {
         for(i = 1; i < this.functionArguments.size(); ++i) {
            sb.append("\n");

            for(j = 0; j < SelectQueryStatement.getBeautyTabCount(); ++j) {
               sb.append("\t");
            }

            sb.append("WHEN ");
            if (requiresSearchedCaseStatement) {
               sb.append(((SelectColumn)this.functionArguments.elementAt(0)).toMSSQLServerSelect(to_sqs, from_sqs).toString());
               if (positionOfNull.contains(i + "")) {
                  sb.append(" IS ");
               } else {
                  sb.append(" = ");
               }
            }

            sb.append(((SelectColumn)this.functionArguments.elementAt(i)).toMSSQLServerSelect(to_sqs, from_sqs).toString() + " ");
            sb.append("THEN ");
            var10001 = new StringBuilder();
            ++i;
            sb.append(var10001.append(((SelectColumn)this.functionArguments.elementAt(i)).toMSSQLServerSelect(to_sqs, from_sqs).toString()).append(" ").toString());
         }
      } else {
         for(i = 1; i < this.functionArguments.size() - 1; ++i) {
            sb.append("\n");

            for(j = 0; j < SelectQueryStatement.getBeautyTabCount(); ++j) {
               sb.append("\t");
            }

            sb.append("WHEN ");
            if (requiresSearchedCaseStatement) {
               sb.append(((SelectColumn)this.functionArguments.elementAt(0)).toMSSQLServerSelect(to_sqs, from_sqs).toString());
               if (positionOfNull.contains(i + "")) {
                  sb.append(" IS ");
               } else {
                  sb.append(" = ");
               }
            }

            sb.append(((SelectColumn)this.functionArguments.elementAt(i)).toMSSQLServerSelect(to_sqs, from_sqs).toString() + " ");
            sb.append("THEN ");
            var10001 = new StringBuilder();
            ++i;
            sb.append(var10001.append(((SelectColumn)this.functionArguments.elementAt(i)).toMSSQLServerSelect(to_sqs, from_sqs).toString()).append(" ").toString());
         }

         sb.append("\n");

         for(j = 0; j < SelectQueryStatement.getBeautyTabCount(); ++j) {
            sb.append("\t");
         }

         sb.append("ELSE ");
         sb.append(((SelectColumn)this.functionArguments.elementAt(i)).toMSSQLServerSelect(to_sqs, from_sqs).toString() + " ");
      }

      SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() - 1);
      sb.append("\n");

      for(i = 0; i < SelectQueryStatement.getBeautyTabCount(); ++i) {
         sb.append("\t");
      }

      sb.append("END");
      SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() - 2);
      this.CaseString = sb.toString();
      this.functionName = null;
      this.argumentQualifier = null;
      this.functionArguments = null;
   }

   public void toSybase(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      StringBuffer sb = new StringBuffer();
      SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() + 2);
      sb.append("\n");

      int i;
      for(i = 0; i < SelectQueryStatement.getBeautyTabCount(); ++i) {
         sb.append("\t");
      }

      sb.append("CASE ");
      if (!SwisSQLOptions.caseWithEqualForDecode) {
         if (this.context != null) {
            ((SelectColumn)this.functionArguments.elementAt(0)).setObjectContext(this.context);
            sb.append(this.context.getEquivalent(((SelectColumn)this.functionArguments.elementAt(0)).toSybaseSelect(to_sqs, from_sqs).toString()) + " ");
         } else {
            sb.append(((SelectColumn)this.functionArguments.elementAt(0)).toSybaseSelect(to_sqs, from_sqs).toString() + " ");
         }
      }

      SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() + 1);
      StringBuilder var10001;
      int j;
      if ((this.functionArguments.size() & 1) != 0) {
         for(i = 1; i < this.functionArguments.size(); ++i) {
            sb.append("\n");

            for(j = 0; j < SelectQueryStatement.getBeautyTabCount(); ++j) {
               sb.append("\t");
            }

            sb.append("WHEN ");
            if (SwisSQLOptions.caseWithEqualForDecode) {
               if (this.context != null) {
                  ((SelectColumn)this.functionArguments.elementAt(0)).setObjectContext(this.context);
                  sb.append(this.context.getEquivalent(((SelectColumn)this.functionArguments.elementAt(0)).toSybaseSelect(to_sqs, from_sqs).toString()) + " = ");
               } else {
                  sb.append(((SelectColumn)this.functionArguments.elementAt(0)).toSybaseSelect(to_sqs, from_sqs).toString() + " = ");
               }
            }

            if (this.context != null) {
               ((SelectColumn)this.functionArguments.elementAt(i)).setObjectContext(this.context);
               sb.append(this.context.getEquivalent(((SelectColumn)this.functionArguments.elementAt(i)).toSybaseSelect(to_sqs, from_sqs).toString()) + " ");
            } else {
               sb.append(((SelectColumn)this.functionArguments.elementAt(i)).toSybaseSelect(to_sqs, from_sqs).toString() + " ");
            }

            sb.append("THEN ");
            if (this.context != null) {
               ++i;
               ((SelectColumn)this.functionArguments.elementAt(i)).setObjectContext(this.context);
               sb.append(this.context.getEquivalent(((SelectColumn)this.functionArguments.elementAt(i)).toSybaseSelect(to_sqs, from_sqs).toString()) + " ");
            } else {
               var10001 = new StringBuilder();
               ++i;
               sb.append(var10001.append(((SelectColumn)this.functionArguments.elementAt(i)).toSybaseSelect(to_sqs, from_sqs).toString()).append(" ").toString());
            }
         }
      } else {
         for(i = 1; i < this.functionArguments.size() - 1; ++i) {
            sb.append("\n");

            for(j = 0; j < SelectQueryStatement.getBeautyTabCount(); ++j) {
               sb.append("\t");
            }

            sb.append("WHEN ");
            if (SwisSQLOptions.caseWithEqualForDecode) {
               if (this.context != null) {
                  ((SelectColumn)this.functionArguments.elementAt(0)).setObjectContext(this.context);
                  sb.append(this.context.getEquivalent(((SelectColumn)this.functionArguments.elementAt(0)).toSybaseSelect(to_sqs, from_sqs).toString()) + " = ");
               } else {
                  sb.append(((SelectColumn)this.functionArguments.elementAt(0)).toSybaseSelect(to_sqs, from_sqs).toString() + " = ");
               }
            }

            if (this.context != null) {
               ((SelectColumn)this.functionArguments.elementAt(i)).setObjectContext(this.context);
               sb.append(this.context.getEquivalent(((SelectColumn)this.functionArguments.elementAt(i)).toSybaseSelect(to_sqs, from_sqs).toString()) + " ");
            } else {
               sb.append(((SelectColumn)this.functionArguments.elementAt(i)).toSybaseSelect(to_sqs, from_sqs).toString() + " ");
            }

            sb.append("THEN ");
            if (this.context != null) {
               ++i;
               ((SelectColumn)this.functionArguments.elementAt(i)).setObjectContext(this.context);
               sb.append(this.context.getEquivalent(((SelectColumn)this.functionArguments.elementAt(i)).toSybaseSelect(to_sqs, from_sqs).toString()) + " ");
            } else {
               var10001 = new StringBuilder();
               ++i;
               sb.append(var10001.append(((SelectColumn)this.functionArguments.elementAt(i)).toSybaseSelect(to_sqs, from_sqs).toString()).append(" ").toString());
            }
         }

         sb.append("\n");

         for(j = 0; j < SelectQueryStatement.getBeautyTabCount(); ++j) {
            sb.append("\t");
         }

         sb.append("ELSE ");
         if (this.context != null) {
            ((SelectColumn)this.functionArguments.elementAt(i)).setObjectContext(this.context);
            sb.append(this.context.getEquivalent(((SelectColumn)this.functionArguments.elementAt(i)).toSybaseSelect(to_sqs, from_sqs).toString()) + " ");
         } else {
            sb.append(((SelectColumn)this.functionArguments.elementAt(i)).toSybaseSelect(to_sqs, from_sqs).toString() + " ");
         }
      }

      SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() - 1);
      sb.append("\n");

      for(i = 0; i < SelectQueryStatement.getBeautyTabCount(); ++i) {
         sb.append("\t");
      }

      sb.append("END");
      SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() - 2);
      this.CaseString = sb.toString();
      this.functionName = null;
      this.argumentQualifier = null;
      this.functionArguments = null;
   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      StringBuffer sb = new StringBuffer();
      String caseDataType = null;
      boolean requiresSearchedCaseStatement = false;
      ArrayList positionOfNull = new ArrayList();
      SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() + 2);
      sb.append("\n");

      int i;
      for(i = 0; i < SelectQueryStatement.getBeautyTabCount(); ++i) {
         sb.append("\t");
      }

      for(i = 0; i < this.functionArguments.size(); ++i) {
         if (((SelectColumn)this.functionArguments.elementAt(i)).toString().trim().equalsIgnoreCase("NULL")) {
            ((SelectColumn)this.functionArguments.elementAt(i)).setInsideDecodeFunction(true);
         }
      }

      for(i = 1; i < this.functionArguments.size() - 1; ++i) {
         if (this.functionArguments.get(i) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.functionArguments.get(i);
            Vector columnList = sc.getColumnExpression();
            if (columnList != null) {
               if (columnList.size() == 1 && columnList.get(0) instanceof String && ((String)columnList.get(0)).trim().equalsIgnoreCase("NULL")) {
                  requiresSearchedCaseStatement = true;
                  positionOfNull.add(i + "");
               } else if (columnList.size() == 1 && columnList.get(0) instanceof String && ((String)columnList.get(0)).trim().equalsIgnoreCase("''")) {
                  requiresSearchedCaseStatement = true;
                  columnList.set(0, " NULL");
                  positionOfNull.add(i + "");
               }
            }
         }
      }

      sb.append("CASE ");
      if (!requiresSearchedCaseStatement) {
         String str = ((SelectColumn)this.functionArguments.elementAt(0)).toDB2Select(to_sqs, from_sqs).toString();
         if (SwisSQLAPI.variableDatatypeMapping != null) {
            caseDataType = CastingUtil.getDataType((String)((String)SwisSQLAPI.variableDatatypeMapping.get(str)));
         }

         sb.append(str + " ");
      }

      SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() + 1);
      int j;
      String str;
      String sourceDataType;
      if ((this.functionArguments.size() & 1) != 0) {
         for(i = 1; i < this.functionArguments.size(); ++i) {
            sb.append("\n");

            for(j = 0; j < SelectQueryStatement.getBeautyTabCount(); ++j) {
               sb.append("\t");
            }

            sb.append("WHEN ");
            if (requiresSearchedCaseStatement) {
               sb.append(((SelectColumn)this.functionArguments.elementAt(0)).toDB2Select(to_sqs, from_sqs).toString());
               if (positionOfNull.contains(i + "")) {
                  sb.append(" IS ");
               } else {
                  sb.append(" = ");
               }
            }

            str = ((SelectColumn)this.functionArguments.elementAt(i)).toDB2Select(to_sqs, from_sqs).toString();
            sourceDataType = null;
            if (SwisSQLAPI.variableDatatypeMapping != null) {
               sourceDataType = CastingUtil.getDataType((String)((String)CastingUtil.getValueIgnoreCase(SwisSQLAPI.variableDatatypeMapping, str)));
            }

            sb.append(CastingUtil.getDB2DataTypeCastedString(sourceDataType, caseDataType, str) + " ");
            sb.append("THEN ");
            ++i;
            ((SelectColumn)this.functionArguments.elementAt(i)).setInArithmeticExpression(this.inArithmeticExpr);
            ((SelectColumn)this.functionArguments.elementAt(i)).setTargetDataType(this.dataType);
            str = ((SelectColumn)this.functionArguments.elementAt(i)).toDB2Select(to_sqs, from_sqs).toString();
            sb.append(str + " ");
         }
      } else {
         for(i = 1; i < this.functionArguments.size() - 1; ++i) {
            sb.append("\n");

            for(j = 0; j < SelectQueryStatement.getBeautyTabCount(); ++j) {
               sb.append("\t");
            }

            sb.append("WHEN ");
            if (requiresSearchedCaseStatement) {
               str = null;
               sourceDataType = ((SelectColumn)this.functionArguments.elementAt(0)).toDB2Select(to_sqs, from_sqs).toString();
               if (SwisSQLAPI.variableDatatypeMapping != null) {
                  str = CastingUtil.getDataType((String)((String)CastingUtil.getValueIgnoreCase(SwisSQLAPI.variableDatatypeMapping, sourceDataType)));
               }

               sb.append(CastingUtil.getDB2DataTypeCastedString(str, caseDataType, sourceDataType) + " ");
               if (positionOfNull.contains(i + "")) {
                  sb.append(" IS ");
               } else {
                  sb.append(" = ");
               }
            }

            str = null;
            sourceDataType = ((SelectColumn)this.functionArguments.elementAt(i)).toDB2Select(to_sqs, from_sqs).toString();
            if (SwisSQLAPI.variableDatatypeMapping != null) {
               str = CastingUtil.getDataType((String)((String)CastingUtil.getValueIgnoreCase(SwisSQLAPI.variableDatatypeMapping, sourceDataType)));
            }

            sb.append(CastingUtil.getDB2DataTypeCastedString(str, caseDataType, sourceDataType) + " ");
            sb.append("THEN ");
            ++i;
            ((SelectColumn)this.functionArguments.elementAt(i)).setInArithmeticExpression(this.inArithmeticExpr);
            ((SelectColumn)this.functionArguments.elementAt(i)).setTargetDataType(this.dataType);
            sourceDataType = ((SelectColumn)this.functionArguments.elementAt(i)).toDB2Select(to_sqs, from_sqs).toString();
            sb.append(sourceDataType + " ");
         }

         sb.append("\n");

         for(j = 0; j < SelectQueryStatement.getBeautyTabCount(); ++j) {
            sb.append("\t");
         }

         sb.append("ELSE ");
         ((SelectColumn)this.functionArguments.elementAt(i)).setInArithmeticExpression(this.inArithmeticExpr);
         ((SelectColumn)this.functionArguments.elementAt(i)).setTargetDataType(this.dataType);
         str = ((SelectColumn)this.functionArguments.elementAt(i)).toDB2Select(to_sqs, from_sqs).toString();
         sb.append(str + " ");
      }

      SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() - 1);
      sb.append("\n");

      for(i = 0; i < SelectQueryStatement.getBeautyTabCount(); ++i) {
         sb.append("\t");
      }

      sb.append("END");
      SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() - 2);
      this.CaseString = sb.toString();
      this.functionName = null;
      this.argumentQualifier = null;
      this.functionArguments = null;
   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      StringBuffer sb = new StringBuffer();
      sb.append("CASE ");
      sb.append(((SelectColumn)this.functionArguments.elementAt(0)).toPostgreSQLSelect(to_sqs, from_sqs).toString() + " ");
      StringBuilder var10001;
      int i;
      if ((this.functionArguments.size() & 1) != 0) {
         for(i = 1; i < this.functionArguments.size(); ++i) {
            sb.append("WHEN ");
            sb.append(((SelectColumn)this.functionArguments.elementAt(i)).toPostgreSQLSelect(to_sqs, from_sqs).toString() + " ");
            sb.append("THEN ");
            var10001 = new StringBuilder();
            ++i;
            sb.append(var10001.append(((SelectColumn)this.functionArguments.elementAt(i)).toPostgreSQLSelect(to_sqs, from_sqs).toString()).append(" ").toString());
         }
      } else {
         for(i = 1; i < this.functionArguments.size() - 1; ++i) {
            sb.append("WHEN ");
            sb.append(((SelectColumn)this.functionArguments.elementAt(i)).toPostgreSQLSelect(to_sqs, from_sqs).toString() + " ");
            sb.append("THEN ");
            var10001 = new StringBuilder();
            ++i;
            sb.append(var10001.append(((SelectColumn)this.functionArguments.elementAt(i)).toPostgreSQLSelect(to_sqs, from_sqs).toString()).append(" ").toString());
         }

         sb.append("ELSE ");
         sb.append(((SelectColumn)this.functionArguments.elementAt(i)).toPostgreSQLSelect(to_sqs, from_sqs).toString() + " ");
      }

      sb.append("END");
      this.CaseString = sb.toString();
      this.functionName = null;
      this.argumentQualifier = null;
      this.functionArguments = null;
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.decodeConvertedToCaseStatement = true;
      this.functionName.setColumnName((String)null);
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() > 2) {
         arguments = new Vector();
         CaseStatement caseStmt = new CaseStatement();
         WhereExpression caseCondition = new WhereExpression();
         Vector whereItemsVector = new Vector();
         WhereItem caseConditionWhereItems = new WhereItem();
         WhereColumn caseLeftWC = new WhereColumn();
         Vector caseColExp = new Vector();
         caseStmt.setCaseClause("CASE");
         caseStmt.setElseClause("ELSE");
         caseStmt.setEndClause("END");
         caseColExp.add(((SelectColumn)this.functionArguments.elementAt(0)).toMySQLSelect(to_sqs, from_sqs).toString());
         caseLeftWC.setColumnExpression(caseColExp);
         caseConditionWhereItems.setLeftWhereExp(caseLeftWC);
         whereItemsVector.add(caseConditionWhereItems);
         caseCondition.setWhereItem(whereItemsVector);
         caseStmt.setCaseCondition(caseCondition);
         Vector whenStmtList = new Vector();

         for(int i = 1; i < this.functionArguments.size() - 1; ++i) {
            WhenStatement whenStmt = new WhenStatement();
            WhereExpression whenConditionWE = new WhereExpression();
            Vector whenConditionVector = new Vector();
            WhereItem whenWhereItem = new WhereItem();
            WhereColumn whenLeftWC = new WhereColumn();
            Vector whenColExp = new Vector();
            SelectColumn thenStmt = new SelectColumn();
            Vector thenColExp = new Vector();
            whenStmt.setWhenClause("WHEN");
            whenColExp.add(((SelectColumn)this.functionArguments.elementAt(i)).toMySQLSelect(to_sqs, from_sqs).toString());
            whenStmt.setThenClause("THEN");
            ++i;
            thenColExp.add(((SelectColumn)this.functionArguments.elementAt(i)).toMySQLSelect(to_sqs, from_sqs).toString());
            whenLeftWC.setColumnExpression(whenColExp);
            whenWhereItem.setLeftWhereExp(whenLeftWC);
            whenConditionVector.add(whenWhereItem);
            whenConditionWE.setWhereItem(whenConditionVector);
            whenStmt.setWhenCondition(whenConditionWE);
            thenStmt.setColumnExpression(thenColExp);
            whenStmt.setThenStatement(thenStmt);
            whenStmtList.add(whenStmt);
         }

         SelectColumn elseStmt = new SelectColumn();
         Vector elseColExp = new Vector();
         elseColExp.add(((SelectColumn)this.functionArguments.elementAt(this.functionArguments.size() - 1)).toMySQLSelect(to_sqs, from_sqs).toString() + " ");
         elseStmt.setColumnExpression(elseColExp);
         caseStmt.setElseStatement(elseStmt);
         caseStmt.setWhenStatementList(whenStmtList);
         SelectColumn argument = new SelectColumn();
         Vector colExp = new Vector();
         colExp.add(caseStmt);
         argument.setColumnExpression(colExp);
         arguments.add(argument);
         this.setFunctionArguments(arguments);
         this.functionName = null;
         this.argumentQualifier = null;
         this.setOpenBracesForFunctionNameRequired(false);
      }

   }

   public void toTimesTen(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      throw new ConvertException("\nDECODE() function is not supported in TimesTen 5.1.21\n");
   }

   public void toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.CaseString = null;
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toNetezzaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      boolean requiresSearchedCaseStatement = false;
      ArrayList positionOfNull = new ArrayList();

      Vector columnList;
      for(int i = 1; i < this.functionArguments.size() - 1; ++i) {
         if (this.functionArguments.get(i) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.functionArguments.get(i);
            columnList = sc.getColumnExpression();
            if (columnList != null) {
               if (columnList.size() == 1 && columnList.get(0) instanceof String && ((String)columnList.get(0)).trim().equalsIgnoreCase("NULL")) {
                  requiresSearchedCaseStatement = true;
                  positionOfNull.add(i + "");
               } else if (columnList.size() == 1 && columnList.get(0) instanceof String && ((String)columnList.get(0)).trim().equalsIgnoreCase("''")) {
                  requiresSearchedCaseStatement = true;
                  columnList.set(0, "NULL");
                  positionOfNull.add(i + "");
               }
            }
         }
      }

      this.caseStatement = new CaseStatement();
      this.caseStatement.setCaseClause("CASE");
      if (!requiresSearchedCaseStatement) {
         WhereItem wi = new WhereItem();
         WhereColumn wc = new WhereColumn();
         columnList = new Vector();
         columnList.add(((SelectColumn)this.functionArguments.elementAt(0)).toTeradataSelect(to_sqs, from_sqs));
         wc.setColumnExpression(columnList);
         wi.setLeftWhereExp(wc);
         WhereExpression we = new WhereExpression();
         we.addWhereItem(wi);
         this.caseStatement.setCaseCondition(we);
      }

      WhereColumn wc;
      Vector fnArgs;
      WhereColumn rwc;
      Vector rwcColExp;
      WhereExpression we;
      Vector whenStmtList;
      int i;
      WhenStatement when_statement;
      WhereItem wi;
      SelectColumn sc1;
      WhereExpression we;
      if ((this.functionArguments.size() & 1) != 0) {
         whenStmtList = new Vector();

         for(i = 1; i < this.functionArguments.size(); ++i) {
            when_statement = new WhenStatement();
            when_statement.setWhenClause("WHEN");
            when_statement.setThenClause("THEN");
            if (requiresSearchedCaseStatement) {
               wi = new WhereItem();
               wc = new WhereColumn();
               fnArgs = new Vector();
               fnArgs.add(((SelectColumn)this.functionArguments.elementAt(0)).toTeradataSelect(to_sqs, from_sqs));
               wc.setColumnExpression(fnArgs);
               wi.setLeftWhereExp(wc);
               if (positionOfNull.contains(i + "")) {
                  wi.setOperator("IS");
               } else {
                  wi.setOperator("=");
               }

               rwc = new WhereColumn();
               rwcColExp = new Vector();
               rwcColExp.add(((SelectColumn)this.functionArguments.elementAt(i)).toTeradataSelect(to_sqs, from_sqs));
               rwc.setColumnExpression(rwcColExp);
               wi.setRightWhereExp(rwc);
               we = new WhereExpression();
               we.addWhereItem(wi);
               when_statement.setWhenCondition(we);
            } else {
               wi = new WhereItem();
               wc = new WhereColumn();
               fnArgs = new Vector();
               fnArgs.add(((SelectColumn)this.functionArguments.elementAt(i)).toTeradataSelect(to_sqs, from_sqs));
               wc.setColumnExpression(fnArgs);
               wi.setLeftWhereExp(wc);
               we = new WhereExpression();
               we.addWhereItem(wi);
               when_statement.setWhenCondition(we);
            }

            ++i;
            sc1 = (SelectColumn)this.functionArguments.elementAt(i);
            when_statement.setThenStatement(sc1.toTeradataSelect(to_sqs, from_sqs));
            whenStmtList.add(when_statement);
         }

         this.caseStatement.setWhenStatementList(whenStmtList);
      } else {
         whenStmtList = new Vector();

         for(i = 1; i < this.functionArguments.size() - 1; ++i) {
            when_statement = new WhenStatement();
            when_statement.setWhenClause("WHEN");
            when_statement.setThenClause("THEN");
            if (requiresSearchedCaseStatement) {
               wi = new WhereItem();
               wc = new WhereColumn();
               fnArgs = new Vector();
               fnArgs.add(((SelectColumn)this.functionArguments.elementAt(0)).toTeradataSelect(to_sqs, from_sqs));
               wc.setColumnExpression(fnArgs);
               wi.setLeftWhereExp(wc);
               if (positionOfNull.contains(i + "")) {
                  wi.setOperator("IS");
               } else {
                  wi.setOperator("=");
               }

               rwc = new WhereColumn();
               rwcColExp = new Vector();
               rwcColExp.add(((SelectColumn)this.functionArguments.elementAt(i)).toTeradataSelect(to_sqs, from_sqs));
               rwc.setColumnExpression(rwcColExp);
               wi.setRightWhereExp(rwc);
               we = new WhereExpression();
               we.addWhereItem(wi);
               when_statement.setWhenCondition(we);
            } else {
               wi = new WhereItem();
               wc = new WhereColumn();
               fnArgs = new Vector();
               fnArgs.add(((SelectColumn)this.functionArguments.elementAt(i)).toTeradataSelect(to_sqs, from_sqs));
               wc.setColumnExpression(fnArgs);
               wi.setLeftWhereExp(wc);
               we = new WhereExpression();
               we.addWhereItem(wi);
               when_statement.setWhenCondition(we);
            }

            ++i;
            when_statement.setThenStatement(((SelectColumn)this.functionArguments.elementAt(i)).toTeradataSelect(to_sqs, from_sqs));
            whenStmtList.add(when_statement);
         }

         this.caseStatement.setWhenStatementList(whenStmtList);
         this.caseStatement.setElseClause("ELSE");
         this.caseStatement.setElseStatement(((SelectColumn)this.functionArguments.elementAt(i)).toTeradataSelect(to_sqs, from_sqs));
      }

      boolean isDatePresent = false;
      int dateWhenStmtIdx = true;

      int i;
      for(i = 0; i < this.caseStatement.getWhenClauseList().size(); ++i) {
         sc1 = ((WhenStatement)this.caseStatement.getWhenClauseList().elementAt(i)).getThenStatement();

         for(int n = 0; n < sc1.getColumnExpression().size(); ++n) {
            Object obj = sc1.getColumnExpression().get(n);
            if (obj instanceof FunctionCalls) {
               FunctionCalls fcObj = (FunctionCalls)obj;
               if (fcObj.getFunctionName() != null) {
                  String fnName = fcObj.getFunctionName().getColumnName();
                  if (SwisSQLUtils.getFunctionReturnType(fnName, fcObj.getFunctionArguments()).equalsIgnoreCase("date")) {
                     isDatePresent = true;
                  }
               }
            }
         }
      }

      for(i = 0; i < this.caseStatement.getWhenClauseList().size(); ++i) {
         WhenStatement convertedWhenStmt = (WhenStatement)this.caseStatement.getWhenClauseList().elementAt(i);
         if (isDatePresent) {
            FunctionCalls caseFunc = new FunctionCalls();
            TableColumn fnName = new TableColumn();
            fnName.setColumnName("CAST");
            caseFunc.setFunctionName(fnName);
            Vector fnArgs = new Vector();
            fnArgs.add(convertedWhenStmt.getThenStatement());
            caseFunc.setAsDatatype("AS");
            DateClass timestamp = new DateClass();
            timestamp.setDatatypeName("TIMESTAMP");
            timestamp.setSize("0");
            timestamp.setOpenBrace("(");
            timestamp.setClosedBrace(")");
            fnArgs.add(timestamp);
            caseFunc.setFunctionArguments(fnArgs);
            SelectColumn newSelCol = new SelectColumn();
            Vector colExp = new Vector();
            colExp.add(caseFunc);
            newSelCol.setColumnExpression(colExp);
            convertedWhenStmt.setThenStatement(newSelCol);
         }
      }

      if (this.caseStatement.getElseStatement() != null) {
         SelectColumn convertedElseStatement = this.caseStatement.getElseStatement();
         if (isDatePresent) {
            FunctionCalls caseFunc = new FunctionCalls();
            TableColumn fnName = new TableColumn();
            fnName.setColumnName("CAST");
            caseFunc.setFunctionName(fnName);
            fnArgs = new Vector();
            fnArgs.add(convertedElseStatement);
            caseFunc.setAsDatatype("AS");
            DateClass timestamp = new DateClass();
            timestamp.setDatatypeName("TIMESTAMP");
            timestamp.setSize("0");
            timestamp.setOpenBrace("(");
            timestamp.setClosedBrace(")");
            fnArgs.add(timestamp);
            caseFunc.setFunctionArguments(fnArgs);
            SelectColumn newSelCol = new SelectColumn();
            Vector colExp = new Vector();
            colExp.add(caseFunc);
            newSelCol.setColumnExpression(colExp);
            this.caseStatement.setElseStatement(newSelCol);
         }
      }

      this.caseStatement.setEndClause("END");
      this.CaseString = null;
      this.functionName = null;
      this.argumentQualifier = null;
      this.functionArguments = null;
   }

   public String toString() {
      if (this.CaseString != null) {
         return this.CaseString;
      } else if (this.caseStatement != null) {
         return this.caseStatement.toString();
      } else {
         if (this.functionName != null) {
            this.functionName.setColumnName("DECODE");
         }

         return super.toString();
      }
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      throw new ConvertException("\nGiven function " + this.functionName.getColumnName() + "is not supported in VectorWise\n");
   }
}
