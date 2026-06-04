package com.adventnet.swissqlapi.sql.statement.create;

import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;

public class DefaultConstraintClause implements ConstraintType {
   private String constraintName;
   private String defaultValue;
   private String openBrace;
   private String closedBrace;
   private String columnName;
   private String forClause;
   private FunctionCalls defaultFunction;
   private UserObjectContext context = null;

   public void setObjectContext(UserObjectContext context) {
      this.context = context;
   }

   public void setConstraintName(String constraintName) {
      this.constraintName = constraintName;
   }

   public void setDefaultValue(String defaultValue) {
      this.defaultValue = defaultValue;
   }

   public void setOpenBrace(String openBrace) {
      this.openBrace = openBrace;
   }

   public void setClosedBrace(String closedBrace) {
      this.closedBrace = closedBrace;
   }

   public void setColumnName(String columnName) {
      this.columnName = columnName;
   }

   public void setDefaultFunction(FunctionCalls functionCalls) {
      this.defaultFunction = functionCalls;
   }

   public void setForClause(String forClause) {
      this.forClause = forClause;
   }

   public String getColumnName() {
      return this.columnName;
   }

   public String getDefaultValue() {
      return this.defaultValue;
   }

   public String getConstraintName() {
      return this.constraintName;
   }

   public FunctionCalls getDefaultFunction() {
      return this.defaultFunction;
   }

   public String getForClause() {
      return this.forClause;
   }

   public void toDB2String() throws ConvertException {
      if (this.getDefaultValue() != null) {
         if (this.defaultValue.equalsIgnoreCase("SYSDATE")) {
            this.setDefaultValue("CURRENT TIMESTAMP");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT_DATE")) {
            this.setDefaultValue("CURRENT DATE");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT_TIME")) {
            this.setDefaultValue("CURRENT TIME");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT_TIMESTAMP")) {
            this.setDefaultValue("CURRENT TIMESTAMP");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT")) {
            this.setDefaultValue("CURRENT DATE");
         } else if (!this.defaultValue.equalsIgnoreCase("SYSTEM_USER") && !this.defaultValue.equalsIgnoreCase("CURRENT_USER")) {
            if (this.defaultFunction != null) {
               this.setDefaultFunction(this.defaultFunction.toDB2Select((SelectQueryStatement)null, (SelectQueryStatement)null));
            }
         } else {
            this.setDefaultValue("USER");
         }
      }

   }

   public void toMSSQLServerString() throws ConvertException {
      if (this.getDefaultValue() != null) {
         if (this.defaultValue.equalsIgnoreCase("SYSDATE")) {
            this.setDefaultValue("GETDATE()");
         } else if (this.defaultValue.equalsIgnoreCase("SYS_GUID")) {
            this.setDefaultValue("NEWID()");
         } else if (!this.defaultValue.equalsIgnoreCase("CURRENT DATE") && !this.defaultValue.equalsIgnoreCase("CURRENT_DATE")) {
            if (!this.defaultValue.equalsIgnoreCase("CURRENT TIME") && !this.defaultValue.equalsIgnoreCase("CURRENT_TIME")) {
               if (!this.defaultValue.equalsIgnoreCase("CURRENT TIMESTAMP") && !this.defaultValue.equalsIgnoreCase("CURRENT_TIMESTAMP")) {
                  if (this.defaultValue.equalsIgnoreCase("CURRENT")) {
                     this.setDefaultValue("GETDATE()");
                  } else if (!this.defaultValue.equalsIgnoreCase("USER") && !this.defaultValue.equalsIgnoreCase("CURRENT_USER")) {
                     if (!this.defaultValue.toUpperCase().startsWith("EMPTY_BLOB") && !this.defaultValue.toUpperCase().startsWith("EMPTY_CLOB")) {
                        if (this.defaultFunction != null) {
                           this.setDefaultFunction(this.defaultFunction.toMSSQLServerSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
                        }
                     } else {
                        this.setDefaultValue("''");
                     }
                  } else {
                     this.setDefaultValue("SYSTEM_USER");
                  }
               } else {
                  this.setDefaultValue("GETDATE()");
               }
            } else {
               this.setDefaultValue("GETDATE()");
            }
         } else {
            this.setDefaultValue("GETDATE()");
         }
      }

   }

   public void toSybaseString() throws ConvertException {
      if (this.getDefaultValue() != null) {
         if (this.defaultValue.equalsIgnoreCase("SYSDATE")) {
            this.setDefaultValue("GETDATE()");
         } else if (this.defaultValue.equalsIgnoreCase("SYS_GUID")) {
            this.setDefaultValue("NEWID()");
         } else if (!this.defaultValue.equalsIgnoreCase("CURRENT DATE") && !this.defaultValue.equalsIgnoreCase("CURRENT_DATE")) {
            if (!this.defaultValue.equalsIgnoreCase("CURRENT TIME") && !this.defaultValue.equalsIgnoreCase("CURRENT_TIME")) {
               if (!this.defaultValue.equalsIgnoreCase("CURRENT TIMESTAMP") && !this.defaultValue.equalsIgnoreCase("CURRENT_TIMESTAMP")) {
                  if (this.defaultValue.equalsIgnoreCase("CURRENT")) {
                     this.setDefaultValue("GETDATE()");
                  } else if (!this.defaultValue.equalsIgnoreCase("SYSTEM_USER") && !this.defaultValue.equalsIgnoreCase("CURRENT_USER")) {
                     if (this.defaultValue.indexOf("\"") != -1) {
                        this.defaultValue = "'" + this.defaultValue.substring(1, this.defaultValue.length() - 1) + "'";
                        this.setDefaultValue(this.defaultValue);
                     } else if (this.defaultFunction != null) {
                        this.setDefaultFunction(this.defaultFunction.toSybaseSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
                     }
                  } else {
                     this.setDefaultValue("USER");
                  }
               } else {
                  this.setDefaultValue("GETDATE()");
               }
            } else {
               this.setDefaultValue("GETDATE()");
            }
         } else {
            this.setDefaultValue("GETDATE()");
         }
      }

   }

   public void toOracleString() throws ConvertException {
      if (this.getDefaultValue() != null) {
         if (!this.defaultValue.equalsIgnoreCase("CURRENT_DATE") && !this.defaultValue.equalsIgnoreCase("CURRENT_TIME") && !this.defaultValue.equalsIgnoreCase("CURRENT_TIMESTAMP") && !this.defaultValue.equalsIgnoreCase("CURRENT DATE") && !this.defaultValue.equalsIgnoreCase("CURRENT TIME") && !this.defaultValue.equalsIgnoreCase("CURRENT TIMESTAMP") && !this.defaultValue.equalsIgnoreCase("CURRENT")) {
            if (!this.defaultValue.equalsIgnoreCase("SYSTEM_USER") && !this.defaultValue.equalsIgnoreCase("CURRENT_USER")) {
               if (this.defaultFunction != null) {
                  this.setDefaultFunction(this.defaultFunction.toOracleSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
               }
            } else {
               this.setDefaultValue("USER");
            }
         } else {
            this.setDefaultValue("SYSDATE");
         }
      }

   }

   public void toBigQueryString() throws ConvertException {
      if (this.getDefaultValue() != null) {
         if (this.defaultValue.equalsIgnoreCase("SYSDATE")) {
            this.setDefaultValue("CURRENT_DATE");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT DATE")) {
            this.setDefaultValue("CURRENT_DATE");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT TIME")) {
            this.setDefaultValue("CURRENT_TIME");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT TIMESTAMP")) {
            this.setDefaultValue("CURRENT_TIMESTAMP");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT")) {
            this.setDefaultValue("CURRENT_DATE");
         } else if (!this.defaultValue.equalsIgnoreCase("SYSTEM_USER") && !this.defaultValue.equalsIgnoreCase("USER")) {
            if (this.defaultFunction != null) {
               this.setDefaultFunction(this.defaultFunction.toBigQuerySelect((SelectQueryStatement)null, (SelectQueryStatement)null));
            }
         } else {
            this.setDefaultValue("CURRENT_USER");
         }
      }

   }

   public void toPostgreSQLString() throws ConvertException {
      if (this.getDefaultValue() != null) {
         if (this.defaultValue.equalsIgnoreCase("SYSDATE")) {
            this.setDefaultValue("CURRENT_DATE");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT DATE")) {
            this.setDefaultValue("CURRENT_DATE");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT TIME")) {
            this.setDefaultValue("CURRENT_TIME");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT TIMESTAMP")) {
            this.setDefaultValue("CURRENT_TIMESTAMP");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT")) {
            this.setDefaultValue("CURRENT_DATE");
         } else if (!this.defaultValue.equalsIgnoreCase("SYSTEM_USER") && !this.defaultValue.equalsIgnoreCase("USER")) {
            if (this.defaultFunction != null) {
               this.setDefaultFunction(this.defaultFunction.toPostgreSQLSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
            }
         } else {
            this.setDefaultValue("CURRENT_USER");
         }
      }

   }

   public void toANSIString() throws ConvertException {
      if (this.getDefaultValue() != null) {
         if (this.defaultValue.equalsIgnoreCase("SYSDATE")) {
            this.setDefaultValue("CURRENT_DATE");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT DATE")) {
            this.setDefaultValue("CURRENT_DATE");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT TIME")) {
            this.setDefaultValue("CURRENT_TIME");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT TIMESTAMP")) {
            this.setDefaultValue("CURRENT_TIMESTAMP");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT")) {
            this.setDefaultValue("CURRENT_DATE");
         } else if (this.defaultValue.equalsIgnoreCase("USER")) {
            this.setDefaultValue("CURRENT_USER");
         } else if (this.defaultFunction != null) {
            this.setDefaultFunction(this.defaultFunction.toANSISelect((SelectQueryStatement)null, (SelectQueryStatement)null));
         }
      }

   }

   public void toMySQLString() throws ConvertException {
      if (this.getDefaultValue() != null) {
         if (this.defaultValue.equalsIgnoreCase("SYSDATE")) {
            this.setDefaultValue("CURRENT_DATE");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT DATE")) {
            this.setDefaultValue("CURRENT_DATE");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT TIME")) {
            this.setDefaultValue("CURRENT_TIME");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT TIMESTAMP")) {
            this.setDefaultValue("CURRENT_TIMESTAMP");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT")) {
            this.setDefaultValue("CURRENT_DATE");
         } else if (!this.defaultValue.equalsIgnoreCase("SYSTEM_USER") && !this.defaultValue.equalsIgnoreCase("CURRENT_USER")) {
            if (this.defaultFunction != null) {
               this.setDefaultFunction(this.defaultFunction.toMySQLSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
            }
         } else {
            this.setDefaultValue("USER()");
         }
      }

   }

   public void toSnowflakeString() throws ConvertException {
      if (this.getDefaultValue() != null) {
         if (this.defaultValue.equalsIgnoreCase("SYSDATE")) {
            this.setDefaultValue("CURRENT_DATE");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT DATE")) {
            this.setDefaultValue("CURRENT_DATE");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT TIME")) {
            this.setDefaultValue("CURRENT_TIME");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT TIMESTAMP")) {
            this.setDefaultValue("CURRENT_TIMESTAMP");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT")) {
            this.setDefaultValue("CURRENT_DATE");
         } else if (!this.defaultValue.equalsIgnoreCase("SYSTEM_USER") && !this.defaultValue.equalsIgnoreCase("USER")) {
            if (this.defaultFunction != null) {
               this.setDefaultFunction(this.defaultFunction.toSnowflakeSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
            }
         } else {
            this.setDefaultValue("CURRENT_USER");
         }
      }

   }

   public void toInformixString() throws ConvertException {
      if (this.getDefaultValue() != null) {
         if (this.defaultValue.equalsIgnoreCase("SYSDATE")) {
            this.setDefaultValue("CURRENT");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT DATE")) {
            this.setDefaultValue("CURRENT");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT TIME")) {
            this.setDefaultValue("CURRENT_TIME");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT TIMESTAMP")) {
            this.setDefaultValue("CURRENT_TIMESTAMP");
         } else if (!this.defaultValue.equalsIgnoreCase("SYSTEM_USER") && !this.defaultValue.equalsIgnoreCase("CURRENT_USER")) {
            if (this.defaultFunction != null) {
               this.setDefaultFunction(this.defaultFunction.toInformixSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
            }
         } else {
            this.setDefaultValue("USER");
         }
      }

   }

   public void toTimesTenString() throws ConvertException {
      if (this.getDefaultValue() != null) {
         if (this.defaultValue.equalsIgnoreCase("SYS_GUID")) {
            this.setDefaultValue("NEWID()");
         } else if (!this.defaultValue.equalsIgnoreCase("CURRENT DATE") && !this.defaultValue.equalsIgnoreCase("CURRENT_DATE")) {
            if (!this.defaultValue.equalsIgnoreCase("CURRENT TIME") && !this.defaultValue.equalsIgnoreCase("CURRENT_TIME")) {
               if (!this.defaultValue.equalsIgnoreCase("CURRENT TIMESTAMP") && !this.defaultValue.equalsIgnoreCase("CURRENT_TIMESTAMP")) {
                  if (this.defaultValue.equalsIgnoreCase("CURRENT")) {
                     this.setDefaultValue("SYSDATE");
                  } else if (!this.defaultValue.equalsIgnoreCase("SYSTEM_USER") && !this.defaultValue.equalsIgnoreCase("CURRENT_USER")) {
                     if (this.defaultValue.indexOf("\"") != -1) {
                        this.defaultValue = "'" + this.defaultValue.substring(1, this.defaultValue.length() - 1) + "'";
                        this.setDefaultValue(this.defaultValue);
                     } else if (this.defaultFunction != null) {
                     }
                  } else {
                     this.setDefaultValue("USER");
                  }
               } else {
                  this.setDefaultValue("SYSDATE");
               }
            } else {
               this.setDefaultValue("SYSDATE");
            }
         } else {
            this.setDefaultValue("SYSDATE");
         }
      }

   }

   public void toNetezzaString() throws ConvertException {
      if (this.getDefaultValue() != null) {
         if (this.defaultValue.equalsIgnoreCase("SYSDATE")) {
            this.setDefaultValue("SYSDATE");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT DATE")) {
            this.setDefaultValue("CURRENT_DATE");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT TIME")) {
            this.setDefaultValue("CURRENT_TIME");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT TIMESTAMP")) {
            this.setDefaultValue("CURRENT_TIMESTAMP");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT")) {
            this.setDefaultValue("CURRENT_DATE");
         } else if (this.defaultValue.equalsIgnoreCase("USER")) {
            this.setDefaultValue("CURRENT_USER");
         } else if (this.defaultFunction != null) {
            this.setDefaultFunction(this.defaultFunction.toNetezzaSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
         }
      }

   }

   public void toTeradataString() throws ConvertException {
      if (this.getDefaultValue() != null) {
         if (this.defaultValue.equalsIgnoreCase("SYSDATE")) {
            this.setDefaultValue("CURRENT_DATE");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT DATE")) {
            this.setDefaultValue("CURRENT_DATE");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT TIME")) {
            this.setDefaultValue("CURRENT_TIME");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT TIMESTAMP")) {
            this.setDefaultValue("CURRENT_TIMESTAMP");
         } else if (this.defaultValue.equalsIgnoreCase("CURRENT")) {
            this.setDefaultValue("CURRENT_DATE");
         } else if (this.defaultValue.equalsIgnoreCase("USER")) {
            this.setDefaultValue("CURRENT_USER");
         } else if (this.defaultFunction != null) {
            this.setDefaultFunction(this.defaultFunction.toTeradataSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
         }
      }

   }

   public ConstraintType copyObjectValues() {
      DefaultConstraintClause dupDefaultConstraintClause = new DefaultConstraintClause();
      dupDefaultConstraintClause.setConstraintName(this.getConstraintName());
      dupDefaultConstraintClause.setDefaultValue(this.getDefaultValue());
      dupDefaultConstraintClause.setDefaultFunction(this.getDefaultFunction());
      dupDefaultConstraintClause.setForClause(this.getForClause());
      dupDefaultConstraintClause.setObjectContext(this.context);
      return dupDefaultConstraintClause;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      String temp;
      if (this.constraintName != null) {
         if (this.context != null) {
            temp = this.context.getEquivalent(this.constraintName).toString();
            sb.append(temp + " ");
         } else {
            sb.append(this.constraintName + " ");
         }
      }

      if (this.openBrace != null) {
         sb.append(this.openBrace);
      }

      if (this.defaultValue != null) {
         sb.append(this.defaultValue + " ");
      }

      if (this.defaultFunction != null) {
         this.defaultFunction.setObjectContext(this.context);
         sb.append(this.defaultFunction.toString() + " ");
      }

      if (this.closedBrace != null) {
         sb.append(this.closedBrace + " ");
      }

      if (this.forClause != null) {
         sb.append(this.forClause + " ");
         if (this.columnName != null) {
            if (this.context != null) {
               temp = this.context.getEquivalent(this.columnName).toString();
               sb.append(temp + " ");
            } else {
               sb.append(this.columnName + " ");
            }
         }
      }

      return sb.toString();
   }
}
