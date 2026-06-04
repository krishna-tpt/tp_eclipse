package com.adventnet.swissqlapi.sql.functions.misc;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.ArrayList;
import java.util.Vector;

public class least extends FunctionCalls {
   private String CaseString;

   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      boolean isdenodo = from_sqs != null && from_sqs.isDenodo();
      if (isdenodo) {
         this.functionName.setColumnName("MIN");
      } else {
         this.functionName.setColumnName("LEAST");
      }

      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toSnowflake(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("LEAST");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSnowflakeSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("LEAST");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }

         if (arguments.size() > 0) {
            ArrayList caseExpression = new ArrayList();
            String caseStr = new String();
            if (arguments.size() == 1) {
               this.functionName.setColumnName("");
            } else {
               String tabString = "\n";

               for(int index = 1; index < arguments.size(); ++index) {
                  if (caseExpression.size() == 1) {
                     caseStr = tabString + "CASE WHEN " + caseExpression.get(0).toString() + " < " + arguments.get(index).toString() + " THEN " + caseExpression.get(0).toString() + " ELSE " + arguments.get(index).toString() + " END";
                     caseExpression.set(0, caseStr);
                  } else {
                     caseStr = tabString + "CASE WHEN " + arguments.get(index - 1).toString() + " < " + arguments.get(index).toString() + " THEN " + arguments.get(index - 1).toString() + " ELSE " + arguments.get(index).toString() + " END";
                     caseExpression.add(caseStr);
                  }
               }

               if (from_sqs != null && from_sqs.isMSAzure()) {
                  this.CaseString = "CAST(" + caseStr + " AS FLOAT)";
               } else {
                  this.CaseString = caseStr;
               }
            }
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toSybase(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("LEAST");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSybaseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }

         if (arguments.size() > 0) {
            ArrayList caseExpression = new ArrayList();
            String caseStr = new String();
            if (arguments.size() == 1) {
               this.functionName.setColumnName("");
            } else {
               String tabString = "\n";

               for(int index = 1; index < arguments.size(); ++index) {
                  if (caseExpression.size() == 1) {
                     caseStr = tabString + "CASE WHEN " + caseExpression.get(0).toString() + " < " + arguments.get(index).toString() + " THEN " + caseExpression.get(0).toString() + " ELSE " + arguments.get(index).toString() + " END";
                     caseExpression.set(0, caseStr);
                  } else {
                     caseStr = tabString + "CASE WHEN " + arguments.get(index - 1).toString() + " < " + arguments.get(index).toString() + " THEN " + arguments.get(index - 1).toString() + " ELSE " + arguments.get(index).toString() + " END";
                     caseExpression.add(caseStr);
                  }
               }

               this.CaseString = caseStr;
            }
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("LEAST");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toDB2Select(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }

         if (arguments.size() > 0) {
            ArrayList caseExpression = new ArrayList();
            String caseStr = new String();
            if (arguments.size() == 1) {
               this.functionName.setColumnName("");
            } else {
               String tabString = "\n";

               for(int index = 1; index < arguments.size(); ++index) {
                  if (caseExpression.size() == 1) {
                     caseStr = tabString + "CASE WHEN " + caseExpression.get(0).toString() + " < " + arguments.get(index).toString() + " THEN " + caseExpression.get(0).toString() + " ELSE " + arguments.get(index).toString() + " END";
                     caseExpression.set(0, caseStr);
                  } else {
                     caseStr = tabString + "CASE WHEN " + arguments.get(index - 1).toString() + " < " + arguments.get(index).toString() + " THEN " + arguments.get(index - 1).toString() + " ELSE " + arguments.get(index).toString() + " END";
                     caseExpression.add(caseStr);
                  }
               }

               this.CaseString = caseStr;
            }
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionArguments.size() == 1) {
         throw new ConvertException("\nGiven function " + this.functionName.getColumnName() + " with " + this.functionArguments.size() + " arguments is not supported. \n Please ensure that the correct number of arguments are passed\n");
      } else {
         boolean needsCasting = to_sqs != null && to_sqs.getBooleanValues("set.can.cast.all.to.text.columns");
         this.functionName.setColumnName("LEAST");
         Vector arguments = new Vector();

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)this.functionArguments.elementAt(i_count);
               sc.convertSelectColumnToTextDataType(needsCasting);
               arguments.addElement(sc.toPostgreSQLSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         this.setFunctionArguments(arguments);
         if (to_sqs != null) {
            to_sqs.addCurrentIndexToCoalesceFunctionList();
         }

      }
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("LEAST");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toANSISQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("LEAST");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toANSISelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("LEAST");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toTeradataSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (arguments.size() > 0 && arguments.size() < 11) {
         new ArrayList();
         new String();
         if (arguments.size() == 1) {
            this.functionName.setColumnName("");
         } else {
            String tabString = "\n";
            StringBuffer temp = new StringBuffer();
            temp.append("CASE ");

            for(int index = 0; index < arguments.size(); ++index) {
               if (index == arguments.size() - 1) {
                  temp.append("ELSE " + arguments.get(index));
               } else {
                  temp.append("WHEN ");

                  for(int k = 0; k != arguments.size(); ++k) {
                     if (k != index) {
                        temp.append(arguments.get(index) + " <= " + arguments.get(k));
                        if (k != arguments.size() - 1) {
                           temp.append(" AND ");
                        }
                     }
                  }

                  temp.append(" THEN " + arguments.get(index) + " \n");
               }
            }

            temp.append(" END");
            String caseStr = temp.toString();
            this.CaseString = caseStr;
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("LEAST");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toNetezzaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }

         if (arguments.size() > 0) {
            ArrayList caseExpression = new ArrayList();
            String caseStr = new String();
            if (arguments.size() == 1) {
               this.functionName.setColumnName("");
            } else {
               String tabString = "\n";

               for(int index = 1; index < arguments.size(); ++index) {
                  if (caseExpression.size() == 1) {
                     caseStr = tabString + "CASE WHEN " + caseExpression.get(0).toString() + " < " + arguments.get(index).toString() + " THEN " + caseExpression.get(0).toString() + " ELSE " + arguments.get(index).toString() + " END";
                     caseExpression.set(0, caseStr);
                  } else {
                     caseStr = tabString + "CASE WHEN " + arguments.get(index - 1).toString() + " < " + arguments.get(index).toString() + " THEN " + arguments.get(index - 1).toString() + " ELSE " + arguments.get(index).toString() + " END";
                     caseExpression.add(caseStr);
                  }
               }

               this.CaseString = caseStr;
            }
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("LEAST");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toBigQuerySelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toAthena(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("LEAST");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toAthenaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toSapHana(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("LEAST");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSapHanaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toSqlite(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("MIN");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSqliteSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public String toString() {
      if (this.CaseString == null) {
         return super.toString();
      } else {
         String tabString = "\n";

         for(int j = 1; j < SelectQueryStatement.getBeautyTabCount(); ++j) {
            tabString = tabString + "\t";
         }

         if (this.CaseString.indexOf(tabString) == -1) {
            this.CaseString = StringFunctions.replaceAll(tabString, "\n", this.CaseString);
         }

         return this.CaseString;
      }
   }
}
