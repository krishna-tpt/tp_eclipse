package com.adventnet.swissqlapi.sql.statement.create;

import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import java.util.Vector;

public class CheckConstraintClause implements ConstraintType {
   private Vector constraintColumnNames;
   private String constraintName;
   private String openBrace;
   private String closedBrace;
   private UserObjectContext context = null;
   private String objectName = null;
   private String stmtTableName;
   WhereExpression whereExpression;

   public void setObjectName(String name) {
      this.objectName = name;
   }

   public void setObjectContext(UserObjectContext context) {
      this.context = context;
   }

   public String getObjectName() {
      return this.objectName;
   }

   public void setConstraintColumnNames(Vector constraintColumnNames) {
      this.constraintColumnNames = constraintColumnNames;
   }

   public void setConstraintName(String constraintName) {
      this.constraintName = constraintName;
   }

   public void setWhereExpression(WhereExpression whereExpression) {
      this.whereExpression = whereExpression;
   }

   public void setOpenBrace(String openBrace) {
      this.openBrace = openBrace;
   }

   public void setClosedBrace(String closedBrace) {
      this.closedBrace = closedBrace;
   }

   public void setStmtTableName(String stmtTableName) {
      this.stmtTableName = stmtTableName;
   }

   public Vector getConstraintColumnNames() {
      return this.constraintColumnNames;
   }

   public String getConstraintName() {
      return this.constraintName;
   }

   public WhereExpression getWhereExpression() {
      return this.whereExpression;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.constraintName != null) {
         sb.append(this.constraintName.toUpperCase() + " ");
      }

      if (this.openBrace != null) {
         sb.append(this.openBrace);
      }

      if (this.constraintColumnNames != null) {
         for(int i = 0; i < this.constraintColumnNames.size(); ++i) {
            String col = this.constraintColumnNames.get(i).toString();
            String temp;
            if (this.objectName != null && this.context != null) {
               temp = this.objectName + "." + col;
               String sss = this.context.getEquivalent(temp).toString();
               if (!temp.equals(sss)) {
                  col = sss;
               }
            }

            if (i == 0) {
               if (this.context != null) {
                  temp = this.context.getEquivalent(col).toString();
                  sb.append(temp);
               } else {
                  sb.append(col);
               }
            } else if (this.context != null) {
               temp = this.context.getEquivalent(col).toString();
               sb.append(", " + temp);
            } else {
               sb.append(", " + col);
            }
         }
      }

      if (this.whereExpression != null) {
         this.whereExpression.setObjectContext(this.context);
         sb.append(this.whereExpression.toString());
      }

      if (this.closedBrace != null) {
         sb.append(this.closedBrace + " ");
      }

      return sb.toString();
   }

   public void toDB2String() throws ConvertException {
      this.getWhereExpression().setStmtTableName(this.stmtTableName);
      WhereExpression newWhereExpression = this.getWhereExpression().toDB2Select((SelectQueryStatement)null, (SelectQueryStatement)null);
      this.setWhereExpression(newWhereExpression);
   }

   public void toMSSQLServerString() throws ConvertException {
      WhereExpression newWhereExpression = this.getWhereExpression().toMSSQLServerSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
      this.setWhereExpression(newWhereExpression);
   }

   public void toSybaseString() throws ConvertException {
      WhereExpression newWhereExpression = this.getWhereExpression().toSybaseSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
      this.setWhereExpression(newWhereExpression);
   }

   public void toOracleString() throws ConvertException {
      WhereExpression newWhereExpression = this.getWhereExpression().toOracleSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
      Vector whereItems = newWhereExpression.getWhereItems();

      for(int i = 0; i < whereItems.size(); ++i) {
         if (whereItems.elementAt(i) instanceof WhereItem) {
            WhereItem wi1 = (WhereItem)whereItems.elementAt(i);
            if (wi1 != null && wi1.getOperator().equalsIgnoreCase("IN")) {
               WhereColumn wc = wi1.getRightWhereExp();
               if (wc != null) {
                  Vector v = wc.getColumnExpression();
                  Vector newVec = new Vector();
                  if (v.contains(",")) {
                     for(int k = 0; k < v.size(); ++k) {
                        String str = v.get(k).toString();
                        str = str.trim();
                        if (str.startsWith("\"") && str.endsWith("\"")) {
                           str = str.substring(1, str.length() - 1);
                           str = "'" + str + "'";
                        }

                        newVec.insertElementAt(str, k);
                     }
                  }

                  wc.setColumnExpression(newVec);
               }
            }
         }
      }

      this.setWhereExpression(newWhereExpression);
   }

   public void toPostgreSQLString() throws ConvertException {
      WhereExpression newWhereExpression = this.getWhereExpression().toPostgreSQLSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
      this.setWhereExpression(newWhereExpression);
   }

   public void toBigQueryString() throws ConvertException {
      WhereExpression newWhereExpression = this.getWhereExpression().toBigQuerySelect((SelectQueryStatement)null, (SelectQueryStatement)null);
      this.setWhereExpression(newWhereExpression);
   }

   public void toANSIString() throws ConvertException {
      WhereExpression newWhereExpression = this.getWhereExpression().toANSISelect((SelectQueryStatement)null, (SelectQueryStatement)null);
      this.setWhereExpression(newWhereExpression);
   }

   public void toMySQLString() throws ConvertException {
      WhereExpression newWhereExpression = this.getWhereExpression().toMySQLSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
      this.setWhereExpression(newWhereExpression);
   }

   public void toInformixString() throws ConvertException {
      WhereExpression newWhereExpression = this.getWhereExpression().toInformixSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
      this.setWhereExpression(newWhereExpression);
   }

   public void toTimesTenString() throws ConvertException {
   }

   public void toNetezzaString() throws ConvertException {
   }

   public void toTeradataString() throws ConvertException {
      WhereExpression newWhereExpression = this.getWhereExpression().toTeradataSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
      this.setWhereExpression(newWhereExpression);
   }

   public void toSnowflakeString() throws ConvertException {
      WhereExpression newWhereExpression = this.getWhereExpression().toSnowflakeSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
      this.setWhereExpression(newWhereExpression);
   }

   public ConstraintType copyObjectValues() {
      CheckConstraintClause dupCheckConstraintClause = new CheckConstraintClause();
      dupCheckConstraintClause.setClosedBrace(this.closedBrace);
      dupCheckConstraintClause.setConstraintColumnNames(this.getConstraintColumnNames());
      dupCheckConstraintClause.setConstraintName(this.getConstraintName());
      dupCheckConstraintClause.setOpenBrace(this.openBrace);
      dupCheckConstraintClause.setWhereExpression(this.getWhereExpression());
      dupCheckConstraintClause.setObjectContext(this.context);
      dupCheckConstraintClause.setObjectName(this.objectName);
      return dupCheckConstraintClause;
   }
}
