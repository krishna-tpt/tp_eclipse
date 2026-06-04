package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.create.CharacterClass;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.util.database.MetadataInfoUtil;
import java.util.Vector;

public class concat extends FunctionCalls {
   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      boolean isdenodo = from_sqs != null && from_sqs.isDenodo();
      Vector arguments;
      int i;
      Vector columnExp;
      SelectColumn arg;
      if (this.functionName.getColumnName().equalsIgnoreCase("concat_ws")) {
         this.functionName.setColumnName("");
         arguments = new Vector();

         for(i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toOracleSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i));
            }
         }

         arg = new SelectColumn();
         columnExp = new Vector();
         Vector newArgs = new Vector();
         Object separator = arguments.get(0);

         for(int i = 1; i < arguments.size(); ++i) {
            SelectColumn argSC;
            String argStr;
            if (i == arguments.size() - 1) {
               if (arguments.get(i) instanceof SelectColumn) {
                  argSC = (SelectColumn)arguments.get(i);
                  if (argSC.getColumnExpression().size() == 1 && argSC.getColumnExpression().get(0) instanceof String) {
                     argStr = argSC.getColumnExpression().get(0).toString();
                     if (argStr.equalsIgnoreCase("null")) {
                        int columnExpSize = columnExp.size();
                        columnExp.remove(columnExpSize - 1);
                        columnExp.remove(columnExpSize - 2);
                        columnExp.remove(columnExpSize - 3);
                        break;
                     }
                  }
               }

               columnExp.add(arguments.get(i));
               break;
            }

            if (arguments.get(i) instanceof SelectColumn) {
               argSC = (SelectColumn)arguments.get(i);
               if (argSC.getColumnExpression().size() == 1 && argSC.getColumnExpression().get(0) instanceof String) {
                  argStr = argSC.getColumnExpression().get(0).toString();
                  if (argStr.equalsIgnoreCase("null")) {
                     continue;
                  }
               }
            }

            columnExp.add(arguments.get(i));
            columnExp.add(new String("||"));
            columnExp.add(separator);
            columnExp.add(new String("||"));
         }

         arg.setColumnExpression(columnExp);
         newArgs.add(arg);
         this.setFunctionArguments(newArgs);
      } else {
         this.functionName.setColumnName("CONCAT");
         arguments = new Vector();

         for(i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toOracleSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i));
            }
         }

         this.setFunctionArguments(arguments);
         if (!isdenodo && this.functionArguments.size() > 2) {
            this.functionName.setColumnName("");
            arg = new SelectColumn();
            columnExp = new Vector();
            columnExp.add(this.functionArguments.get(0));

            for(int i = 0; i < this.functionArguments.size() - 1; ++i) {
               columnExp.add("||");
               columnExp.add(this.functionArguments.get(i + 1));
               arg.setColumnExpression(columnExp);
               this.functionArguments.setElementAt(arg, i);
            }

            this.functionArguments.setSize(1);
         }
      }

   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments;
      int i;
      Vector columnExp;
      SelectColumn arg;
      if (this.functionName.getColumnName().equalsIgnoreCase("concat_ws")) {
         this.functionName.setColumnName("");
         arguments = new Vector();

         for(i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toMSSQLServerSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i));
            }
         }

         arg = new SelectColumn();
         columnExp = new Vector();
         Object separator = arguments.get(0);

         for(int i = 1; i < arguments.size(); ++i) {
            SelectColumn argSC;
            String argStr;
            if (i == arguments.size() - 1) {
               if (arguments.get(i) instanceof SelectColumn) {
                  argSC = (SelectColumn)arguments.get(i);
                  if (argSC.getColumnExpression().size() == 1 && argSC.getColumnExpression().get(0) instanceof String) {
                     argStr = argSC.getColumnExpression().get(0).toString();
                     if (argStr.equalsIgnoreCase("null")) {
                        int columnExpSize = columnExp.size();
                        columnExp.remove(columnExpSize - 1);
                        columnExp.remove(columnExpSize - 2);
                        columnExp.remove(columnExpSize - 3);
                        break;
                     }
                  }
               }

               columnExp.add("CAST( " + arguments.get(i) + " AS VARCHAR(MAX))");
               break;
            }

            if (arguments.get(i) instanceof SelectColumn) {
               argSC = (SelectColumn)arguments.get(i);
               if (argSC.getColumnExpression().size() == 1 && argSC.getColumnExpression().get(0) instanceof String) {
                  argStr = argSC.getColumnExpression().get(0).toString();
                  if (argStr.equalsIgnoreCase("null")) {
                     continue;
                  }
               }
            }

            columnExp.add("CAST( " + arguments.get(i) + " AS VARCHAR(MAX))");
            columnExp.add(new String("+"));
            columnExp.add(separator);
            columnExp.add(new String("+"));
         }

         arg.setColumnExpression(columnExp);
         this.functionArguments.clear();
         this.functionArguments.add(arg);
      } else {
         arguments = new Vector();

         for(i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toMSSQLServerSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i));
            }
         }

         this.setFunctionArguments(arguments);
         this.functionName.setColumnName("");
         if (this.functionArguments.size() == 2) {
            arg = new SelectColumn();
            columnExp = new Vector();
            columnExp.add("CAST( " + this.functionArguments.get(0) + " AS VARCHAR(MAX))");
            columnExp.add(new String("+"));
            columnExp.add("CAST( " + this.functionArguments.get(1) + " AS VARCHAR(MAX))");
            arg.setColumnExpression(columnExp);
            this.functionArguments.setElementAt(arg, 0);
            this.functionArguments.setSize(1);
         } else if (this.functionArguments.size() > 2) {
            arg = new SelectColumn();
            columnExp = new Vector();
            columnExp.add("CAST( " + this.functionArguments.get(0) + " AS VARCHAR(MAX))");

            for(int i = 0; i < this.functionArguments.size() - 1; ++i) {
               columnExp.add(new String("+"));
               columnExp.add("CAST( " + this.functionArguments.get(i + 1) + " AS VARCHAR(MAX))");
               arg.setColumnExpression(columnExp);
               this.functionArguments.setElementAt(arg, i);
            }

            this.functionArguments.setSize(1);
         }
      }

   }

   public void toSybase(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("");
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toSybaseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      this.setFunctionArguments(arguments);
      Vector columnExp;
      SelectColumn arg;
      if (this.functionArguments.size() == 2) {
         arg = new SelectColumn();
         columnExp = new Vector();
         columnExp.add(this.functionArguments.get(0));
         columnExp.add(new String("+"));
         columnExp.add(this.functionArguments.get(1));
         arg.setColumnExpression(columnExp);
         this.functionArguments.setElementAt(arg, 0);
         this.functionArguments.setSize(1);
      } else if (this.functionArguments.size() > 2) {
         arg = new SelectColumn();
         columnExp = new Vector();
         columnExp.add(this.functionArguments.get(0));

         for(int i = 0; i < this.functionArguments.size() - 1; ++i) {
            columnExp.add(new String("+"));
            columnExp.add(this.functionArguments.get(i + 1));
            arg.setColumnExpression(columnExp);
            this.functionArguments.setElementAt(arg, i);
         }

         this.functionArguments.setSize(1);
      }

   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments;
      int i;
      Vector columnExp;
      SelectColumn arg;
      if (this.functionName.getColumnName().equalsIgnoreCase("concat_ws")) {
         this.functionName.setColumnName("");
         arguments = new Vector();

         for(i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toDB2Select(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i));
            }
         }

         arg = new SelectColumn();
         columnExp = new Vector();
         Object separator = arguments.get(0);

         for(int i = 1; i < arguments.size(); ++i) {
            SelectColumn argSC;
            String argStr;
            if (i == arguments.size() - 1) {
               if (arguments.get(i) instanceof SelectColumn) {
                  argSC = (SelectColumn)arguments.get(i);
                  if (argSC.getColumnExpression().size() == 1 && argSC.getColumnExpression().get(0) instanceof String) {
                     argStr = argSC.getColumnExpression().get(0).toString();
                     if (argStr.equalsIgnoreCase("null")) {
                        int columnExpSize = columnExp.size();
                        columnExp.remove(columnExpSize - 1);
                        columnExp.remove(columnExpSize - 2);
                        columnExp.remove(columnExpSize - 3);
                        break;
                     }
                  }
               }

               columnExp.add("CAST( " + arguments.get(i) + " AS VARCHAR)");
               break;
            }

            if (arguments.get(i) instanceof SelectColumn) {
               argSC = (SelectColumn)arguments.get(i);
               if (argSC.getColumnExpression().size() == 1 && argSC.getColumnExpression().get(0) instanceof String) {
                  argStr = argSC.getColumnExpression().get(0).toString();
                  if (argStr.equalsIgnoreCase("null")) {
                     continue;
                  }
               }
            }

            columnExp.add("CAST( " + arguments.get(i) + " AS VARCHAR)");
            columnExp.add(new String("||"));
            columnExp.add(separator);
            columnExp.add(new String("||"));
         }

         arg.setColumnExpression(columnExp);
         this.functionArguments.clear();
         this.functionArguments.add(arg);
      } else {
         this.functionName.setColumnName("CONCAT");
         arguments = new Vector();

         for(i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toDB2Select(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i));
            }
         }

         this.setFunctionArguments(arguments);
         if (this.functionArguments.size() > 2) {
            this.functionName.setColumnName("");
            arg = new SelectColumn();
            columnExp = new Vector();
            columnExp.add("CAST((" + this.functionArguments.get(0) + ") AS VARCHAR)");

            for(int i = 0; i < this.functionArguments.size() - 1; ++i) {
               columnExp.add(new String("||"));
               columnExp.add("CAST((" + this.functionArguments.get(i + 1) + ") AS VARCHAR)");
               arg.setColumnExpression(columnExp);
               this.functionArguments.setElementAt(arg, i);
            }

            this.functionArguments.setSize(1);
         }
      }

   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      boolean isPostgreLiveDbs = from_sqs != null && (from_sqs.isAmazonRedShift() || from_sqs.isPgsqlLive() || from_sqs.isVerticaDb());
      SelectColumn sc;
      if (this.functionName.getColumnName().equalsIgnoreCase("concat_ws")) {
         this.functionName.setColumnName("");
         boolean isVertica = from_sqs != null && from_sqs.isVerticaDb();
         boolean isRedShift = from_sqs != null && from_sqs.isAmazonRedShift();
         int i;
         if (!isVertica && !isRedShift) {
            for(i = 0; i < this.functionArguments.size(); ++i) {
               if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                  SelectColumn selCol = (SelectColumn)this.functionArguments.elementAt(i);
                  if (!isPostgreLiveDbs && selCol.getColumnExpression() != null && selCol.getColumnExpression().size() == 1 && selCol.getColumnExpression().get(0) instanceof String) {
                     String value = selCol.getColumnExpression().get(0).toString().trim();
                     if (value.startsWith("'") && value.endsWith("'") && value.contains("\\")) {
                        value = "E" + value;
                        selCol.getColumnExpression().set(0, value);
                     }
                  }

                  selCol.convertSelectColumnToTextDataType();
                  arguments.addElement(selCol.toPostgreSQLSelect(to_sqs, from_sqs));
               } else {
                  arguments.addElement(this.functionArguments.elementAt(i));
               }
            }

            this.setFunctionArguments(arguments);
            this.functionName.setColumnName("CONCAT_WS");
         } else {
            for(i = 0; i < this.functionArguments.size(); ++i) {
               if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                  arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toPostgreSQLSelect(to_sqs, from_sqs));
               } else {
                  arguments.addElement(this.functionArguments.elementAt(i));
               }
            }

            this.setFunctionArguments(arguments);
            if (this.functionArguments.size() >= 2) {
               sc = new SelectColumn();
               Vector columnExp = new Vector();

               for(int i = 0; i < this.functionArguments.size() - 1; ++i) {
                  columnExp.add(this.functionArguments.get(i + 1));
                  if (i != this.functionArguments.size() - 2) {
                     columnExp.add(new String("||"));
                     columnExp.add(this.functionArguments.get(0));
                     columnExp.add(new String("||"));
                  }
               }

               sc.setColumnExpression(columnExp);
               this.functionArguments.setElementAt(sc, 0);
               this.functionArguments.setSize(1);
            }
         }
      } else {
         this.functionName.setColumnName("");
         String datatype = from_sqs != null && from_sqs.isVerticaDb() ? "VARCHAR" : "TEXT";

         for(int j = 0; j < this.functionArguments.size(); ++j) {
            if (this.functionArguments.elementAt(j) instanceof SelectColumn) {
               sc = (SelectColumn)this.functionArguments.elementAt(j);
               if (!isPostgreLiveDbs && sc.getColumnExpression() != null && sc.getColumnExpression().size() == 1 && sc.getColumnExpression().get(0) instanceof String) {
                  String value = sc.getColumnExpression().get(0).toString().trim();
                  if (value.startsWith("'") && value.endsWith("'") && value.contains("\\")) {
                     value = "E" + value;
                     sc.getColumnExpression().set(0, value);
                  }
               }

               sc.convertSelectColumnToTextDataType();
               arguments.addElement(sc.toPostgreSQLSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement("CAST(" + this.functionArguments.elementAt(j) + " AS " + datatype + ")");
            }
         }

         this.setFunctionArguments(arguments);
         if (from_sqs != null && !isPostgreLiveDbs && from_sqs.getBooleanValues("use.udf.functions.for.text")) {
            this.functionName.setColumnName("STRING_CONCAT");
         } else if (this.functionArguments.size() >= 2) {
            SelectColumn arg = new SelectColumn();
            Vector columnExp = new Vector();
            columnExp.add(this.functionArguments.get(0));

            for(int i = 0; i < this.functionArguments.size() - 1; ++i) {
               columnExp.add(new String("||"));
               columnExp.add(this.functionArguments.get(i + 1));
               arg.setColumnExpression(columnExp);
               this.functionArguments.setElementAt(arg, i);
            }

            this.functionArguments.setSize(1);
         }
      }

   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionName.getColumnName().equalsIgnoreCase("concat_ws")) {
         this.functionName.setColumnName("CONCAT_WS");
      } else {
         this.functionName.setColumnName("CONCAT");
         this.modifyFunctionArguments(from_sqs);
      }

      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      if (from_sqs != null && from_sqs.isHyperSql()) {
         arguments.replaceAll((arg) -> {
            return "CAST(" + arg + " AS LONGVARCHAR)";
         });
      }

      this.setFunctionArguments(arguments);
   }

   public void toSnowflake(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionName.getColumnName().equalsIgnoreCase("concat_ws")) {
         this.functionName.setColumnName("CONCAT_WS");
      } else {
         this.functionName.setColumnName("CONCAT");
      }

      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toSnowflakeSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toSapHana(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      int i;
      Vector columnExp;
      int i;
      SelectColumn sc;
      if (this.functionName.getColumnName().equalsIgnoreCase("concat_ws")) {
         this.functionName.setColumnName("");

         for(i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toSapHanaSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i));
            }
         }

         this.setFunctionArguments(arguments);
         if (this.functionArguments.size() >= 2) {
            sc = new SelectColumn();
            columnExp = new Vector();

            for(i = 0; i < this.functionArguments.size() - 1; ++i) {
               columnExp.add(this.functionArguments.get(i + 1));
               if (i != this.functionArguments.size() - 2) {
                  columnExp.add(new String("||"));
                  columnExp.add(this.functionArguments.get(0));
                  columnExp.add(new String("||"));
               }
            }

            sc.setColumnExpression(columnExp);
            this.functionArguments.setElementAt(sc, 0);
            this.functionArguments.setSize(1);
         }
      } else {
         this.functionName.setColumnName("");

         for(i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toSapHanaSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i));
            }
         }

         this.setFunctionArguments(arguments);
         if (this.functionArguments.size() >= 2) {
            sc = new SelectColumn();
            columnExp = new Vector();
            columnExp.add(this.functionArguments.get(0));

            for(i = 0; i < this.functionArguments.size() - 1; ++i) {
               columnExp.add(new String("||"));
               columnExp.add(this.functionArguments.get(i + 1));
               sc.setColumnExpression(columnExp);
               this.functionArguments.setElementAt(sc, i);
            }

            this.functionArguments.setSize(1);
         }
      }

   }

   public void toSqlite(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      int i;
      Vector columnExp;
      int i;
      SelectColumn sc;
      if (this.functionName.getColumnName().equalsIgnoreCase("concat_ws")) {
         this.functionName.setColumnName("");

         for(i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toSqliteSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i));
            }
         }

         this.setFunctionArguments(arguments);
         if (this.functionArguments.size() >= 2) {
            sc = new SelectColumn();
            columnExp = new Vector();

            for(i = 0; i < this.functionArguments.size() - 1; ++i) {
               columnExp.add(this.functionArguments.get(i + 1));
               if (i != this.functionArguments.size() - 2) {
                  columnExp.add("||");
                  columnExp.add(this.functionArguments.get(0));
                  columnExp.add("||");
               }
            }

            sc.setColumnExpression(columnExp);
            this.functionArguments.setElementAt(sc, 0);
            this.functionArguments.setSize(1);
         }
      } else {
         this.functionName.setColumnName("");

         for(i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toSqliteSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i));
            }
         }

         this.setFunctionArguments(arguments);
         if (this.functionArguments.size() >= 2) {
            sc = new SelectColumn();
            columnExp = new Vector();
            columnExp.add(this.functionArguments.get(0));

            for(i = 0; i < this.functionArguments.size() - 1; ++i) {
               columnExp.add("||");
               columnExp.add(this.functionArguments.get(i + 1));
               sc.setColumnExpression(columnExp);
               this.functionArguments.setElementAt(sc, i);
            }

            this.functionArguments.setSize(1);
         }
      }

   }

   public void toANSISQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("");
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toANSISelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() >= 2) {
         this.functionName.setColumnName("");
         SelectColumn arg = new SelectColumn();
         Vector columnExp = new Vector();
         columnExp.add(this.functionArguments.get(0));

         for(int i = 0; i < this.functionArguments.size() - 1; ++i) {
            columnExp.add("||");
            columnExp.add(this.functionArguments.get(i + 1));
            arg.setColumnExpression(columnExp);
            this.functionArguments.setElementAt(arg, i);
         }

         this.functionArguments.setSize(1);
      }

   }

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toInformixSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() >= 2) {
         SelectColumn sc = new SelectColumn();
         Vector columnExp = new Vector();
         int i;
         if (!this.functionName.getColumnName().equalsIgnoreCase("concat_ws")) {
            columnExp.add(this.functionArguments.get(0));

            for(i = 0; i < this.functionArguments.size() - 1; ++i) {
               columnExp.add(new String("||"));
               columnExp.add(this.functionArguments.get(i + 1));
               sc.setColumnExpression(columnExp);
               this.functionArguments.setElementAt(sc, i);
            }
         } else {
            for(i = 0; i < this.functionArguments.size() - 1; ++i) {
               columnExp.add(this.functionArguments.get(i + 1));
               if (i != this.functionArguments.size() - 2) {
                  columnExp.add("||");
                  columnExp.add(this.functionArguments.get(0));
                  columnExp.add("||");
               }
            }

            sc.setColumnExpression(columnExp);
            this.functionArguments.setElementAt(sc, 0);
         }

         this.functionName.setColumnName("");
         this.functionArguments.setSize(1);
      }

   }

   public void toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("");
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toNetezzaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() >= 2) {
         this.functionName.setColumnName("");
         SelectColumn arg = new SelectColumn();
         Vector columnExp = new Vector();
         columnExp.add(this.functionArguments.get(0));

         for(int i = 0; i < this.functionArguments.size() - 1; ++i) {
            columnExp.add(new String("||"));
            columnExp.add(this.functionArguments.get(i + 1));
            arg.setColumnExpression(columnExp);
            this.functionArguments.setElementAt(arg, i);
         }

         this.functionArguments.setSize(1);
      }

   }

   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("");
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toTeradataSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() >= 2) {
         this.functionName.setColumnName("");
         SelectColumn arg = new SelectColumn();
         Vector columnExp = new Vector();
         columnExp.add(this.functionArguments.get(0));

         for(int i = 0; i < this.functionArguments.size() - 1; ++i) {
            columnExp.add("||");
            columnExp.add(this.functionArguments.get(i + 1));
            arg.setColumnExpression(columnExp);
            this.functionArguments.setElementAt(arg, i);
         }

         this.functionArguments.setSize(1);
      }

   }

   private void modifyFunctionArguments(SelectQueryStatement from_sqs) {
      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.get(i) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.functionArguments.get(i);
            Vector newColumnExpr = new Vector();
            Vector v = sc.getColumnExpression();
            if (v != null && v.size() == 1 && v.get(0) instanceof TableColumn) {
               TableColumn tc = (TableColumn)v.get(0);
               String dataType = MetadataInfoUtil.getDatatypeName(from_sqs, tc);
               if (dataType != null && !dataType.toLowerCase().startsWith("char") && !dataType.toLowerCase().startsWith("varchar")) {
                  SelectColumn newSC = new SelectColumn();
                  FunctionCalls castFunction = new FunctionCalls();
                  TableColumn tc1 = new TableColumn();
                  CharacterClass charClass = new CharacterClass();
                  tc1.setColumnName("CAST");
                  castFunction.setFunctionName(tc1);
                  castFunction.setAsDatatype("AS");
                  charClass.setDatatypeName("CHAR");
                  Vector newFunctionArgs = new Vector();
                  newFunctionArgs.add(0, sc);
                  newFunctionArgs.add(1, charClass);
                  castFunction.setFunctionArguments(newFunctionArgs);
                  newColumnExpr.add(0, castFunction);
                  newSC.setColumnExpression(newColumnExpr);
                  this.functionArguments.set(i, newSC);
               }
            }
         }
      }

   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      SelectColumn sc;
      if (this.functionName.getColumnName().equalsIgnoreCase("concat_ws")) {
         Object separator;
         if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
            sc = (SelectColumn)this.functionArguments.elementAt(0);
            sc.convertSelectColumnToTextDataType();
            separator = sc.toVectorWiseSelect(to_sqs, from_sqs);
         } else {
            separator = "CAST(" + this.functionArguments.elementAt(0).toString() + " AS VARCHAR)";
         }

         for(int i = 1; i < this.functionArguments.size(); ++i) {
            if (i > 1) {
               arguments.addElement(separator);
            }

            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)this.functionArguments.elementAt(i);
               sc.convertSelectColumnToTextDataType();
               arguments.addElement(sc.toVectorWiseSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement("CAST(" + this.functionArguments.elementAt(i) + " AS VARCHAR)");
            }
         }
      } else {
         for(int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
               sc = (SelectColumn)this.functionArguments.elementAt(i);
               sc.convertSelectColumnToTextDataType();
               arguments.addElement(sc.toVectorWiseSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i));
            }
         }
      }

      this.functionName.setColumnName("CONCAT");
      this.setFunctionArguments(arguments);
   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toBigQuerySelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      if (this.functionName.getColumnName().equalsIgnoreCase("concat_ws")) {
         StringBuilder argnew = new StringBuilder();

         for(int j = 1; j < arguments.size(); ++j) {
            argnew.append("CAST( " + arguments.get(j) + " AS STRING)");
            if (j != arguments.size() - 1) {
               argnew.append(", ");
            }
         }

         this.functionName.setColumnName("ARRAY_TO_STRING([" + argnew + "], " + arguments.get(0) + " )");
         this.setFunctionArguments(new Vector());
         this.setOpenBracesForFunctionNameRequired(false);
      } else {
         this.functionName.setColumnName("CONCAT");
         this.setFunctionArguments(arguments);
      }

   }

   public void toAthena(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments;
      int i;
      int i;
      if (this.functionName.getColumnName().equalsIgnoreCase("concat_ws")) {
         arguments = new Vector();

         for(i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toAthenaSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i));
            }
         }

         StringBuilder argnew = new StringBuilder();

         for(i = 1; i < arguments.size(); ++i) {
            argnew.append("CAST( " + arguments.get(i) + " AS VARCHAR)");
            if (i != arguments.size() - 1) {
               argnew.append(", ");
            }
         }

         this.functionName.setColumnName("ARRAY_JOIN(ARRAY[" + argnew + "], " + arguments.get(0) + ")");
         this.setFunctionArguments(new Vector());
         this.setOpenBracesForFunctionNameRequired(false);
      } else {
         arguments = new Vector();

         for(i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toAthenaSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i));
            }
         }

         this.setFunctionArguments(arguments);
         StringBuffer selColExp = new StringBuffer();
         if (this.functionArguments.size() >= 1) {
            selColExp.append("CAST( " + this.functionArguments.get(0) + " AS VARCHAR)");

            for(i = 0; i < this.functionArguments.size() - 1; ++i) {
               selColExp.append(" || ");
               selColExp.append("CAST( " + this.functionArguments.get(i + 1) + " AS VARCHAR)");
            }
         }

         this.functionName.setColumnName(selColExp.toString());
         this.setFunctionArguments(new Vector());
         this.setOpenBracesForFunctionNameRequired(false);
      }

   }

   public void toExcel(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      int i;
      Vector columnExp;
      int i;
      SelectColumn sc;
      if (this.functionName.getColumnName().equalsIgnoreCase("concat_ws")) {
         this.functionName.setColumnName("");

         for(i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toExcelSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i));
            }
         }

         this.setFunctionArguments(arguments);
         if (this.functionArguments.size() >= 2) {
            sc = new SelectColumn();
            columnExp = new Vector();

            for(i = 0; i < this.functionArguments.size() - 1; ++i) {
               columnExp.add(this.functionArguments.get(i + 1));
               if (i != this.functionArguments.size() - 2) {
                  columnExp.add(new String("&"));
                  columnExp.add(this.functionArguments.get(0));
                  columnExp.add(new String("&"));
               }
            }

            sc.setColumnExpression(columnExp);
            this.functionArguments.setElementAt(sc, 0);
            this.functionArguments.setSize(1);
         }
      } else {
         this.functionName.setColumnName("");

         for(i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toExcelSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i));
            }
         }

         this.setFunctionArguments(arguments);
         if (this.functionArguments.size() >= 2) {
            sc = new SelectColumn();
            columnExp = new Vector();
            columnExp.add(this.functionArguments.get(0));

            for(i = 0; i < this.functionArguments.size() - 1; ++i) {
               columnExp.add(new String("&"));
               columnExp.add(this.functionArguments.get(i + 1));
               sc.setColumnExpression(columnExp);
               this.functionArguments.setElementAt(sc, i);
            }

            this.functionArguments.setSize(1);
         }
      }

   }

   public void toMsAccess(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionName.getColumnName().equalsIgnoreCase("concat_ws")) {
         this.functionName.setColumnName("CONCAT_WS");
      } else {
         this.functionName.setColumnName("CONCAT");
         this.modifyFunctionArguments(from_sqs);
      }

      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toMsAccessJdbcSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      arguments.replaceAll((arg) -> {
         return "CAST(" + arg + " AS LONGVARCHAR)";
      });
      this.setFunctionArguments(arguments);
   }
}
