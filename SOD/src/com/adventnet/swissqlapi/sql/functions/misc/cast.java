package com.adventnet.swissqlapi.sql.functions.misc;

import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.config.datatypes.DatatypeMapping;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.functions.date.dateadd;
import com.adventnet.swissqlapi.sql.statement.create.BinClass;
import com.adventnet.swissqlapi.sql.statement.create.CharacterClass;
import com.adventnet.swissqlapi.sql.statement.create.Datatype;
import com.adventnet.swissqlapi.sql.statement.create.DateClass;
import com.adventnet.swissqlapi.sql.statement.create.NumericClass;
import com.adventnet.swissqlapi.sql.statement.create.QuotedIdentifierDatatype;
import com.adventnet.swissqlapi.sql.statement.select.FromClause;
import com.adventnet.swissqlapi.sql.statement.select.FromTable;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.util.database.MetadataInfoUtil;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.Hashtable;
import java.util.Vector;

public class cast extends FunctionCalls {
   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      boolean isdenodo = from_sqs != null && from_sqs.isDenodo();
      Vector arguments;
      if (this.functionName.getColumnName().equalsIgnoreCase("cast")) {
         this.functionName.setColumnName("CAST");
         arguments = new Vector();
         SelectColumn sc = null;
         boolean isDate = false;
         boolean isChar = false;
         boolean isCharVarch = false;

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            String type;
            String size;
            TableColumn tc;
            Object obj;
            FunctionCalls fc;
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               SelectColumn selCol = (SelectColumn)this.functionArguments.elementAt(i_count);
               Vector colExpr = selCol.getColumnExpression();
               if (colExpr.size() == 1) {
                  obj = colExpr.get(0);
                  if (obj instanceof TableColumn) {
                     TableColumn tc = (TableColumn)obj;
                     String colName = tc.getColumnName();
                     type = null;
                     if (from_sqs != null && from_sqs.getFromClause() != null) {
                        type = MetadataInfoUtil.getDatatypeName(from_sqs, tc);
                     }

                     if (type != null && type.toLowerCase().indexOf("date") != -1) {
                        isDate = true;
                     } else if (type == null && SwisSQLAPI.variableDatatypeMapping != null && (from_sqs != null && from_sqs.getFromClause() == null || from_sqs == null) && SwisSQLAPI.variableDatatypeMapping.containsKey(colName)) {
                        size = (String)SwisSQLAPI.variableDatatypeMapping.get(colName);
                        if (size.toLowerCase().indexOf("date") != -1) {
                           isDate = true;
                        } else if (size.toLowerCase().startsWith("char") || size.toLowerCase().startsWith("nchar")) {
                           isChar = true;
                        }

                        if (size.toLowerCase().startsWith("char") || size.toLowerCase().startsWith("varchar")) {
                           isCharVarch = true;
                        }
                     }

                     if (type != null && (type.toLowerCase().startsWith("char") || type.toLowerCase().startsWith("nchar"))) {
                        isChar = true;
                     }

                     if (type != null && (type.toLowerCase().startsWith("char") || type.toLowerCase().startsWith("varchar"))) {
                        isCharVarch = true;
                     }
                  } else if (obj instanceof FunctionCalls) {
                     fc = (FunctionCalls)obj;
                     tc = fc.getFunctionName();
                     if (tc != null) {
                        type = tc.getColumnName();
                        if (type.equalsIgnoreCase("getdate")) {
                           isDate = true;
                        } else if (type.equalsIgnoreCase("right") || type.equalsIgnoreCase("SUBSTR")) {
                           isCharVarch = true;
                        }
                     }
                  }
               }

               sc = ((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs);
               arguments.addElement(sc);
            } else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
               Datatype datatype = (Datatype)this.functionArguments.elementAt(i_count);
               DatatypeMapping mapping = null;
               if (from_sqs != null) {
                  mapping = from_sqs.getDatatypeMapping();
               }

               String str;
               if (!this.mapDatatype(datatype, mapping, sc, from_sqs)) {
                  boolean money = false;
                  if (datatype instanceof NumericClass) {
                     str = datatype.getDatatypeName();
                     if (str.equalsIgnoreCase("money") || str.equalsIgnoreCase("smallmoney")) {
                        money = true;
                     }
                  } else if (datatype instanceof BinClass) {
                     str = datatype.getDatatypeName();
                     if (!isdenodo && str.equalsIgnoreCase("varbinary")) {
                        this.functionName.setColumnName("RAWTOHEX");
                        this.setFunctionArguments(arguments);
                        this.setAsDatatype((String)null);
                        return;
                     }

                     if (str.equalsIgnoreCase("binary")) {
                        this.functionName.setColumnName("");
                        this.setFunctionArguments(arguments);
                        this.setAsDatatype((String)null);
                        this.setOpenBracesForFunctionNameRequired(false);
                        return;
                     }
                  }

                  if (isdenodo) {
                     datatype.toDenodoString();
                  } else {
                     datatype.toOracleString();
                  }

                  if (SwisSQLOptions.fromSQLServer) {
                     boolean isSetExpr = false;
                     if (from_sqs != null && from_sqs.getSelectStatement().getSelectItemList() != null && from_sqs.getSelectStatement().getSelectItemList().size() == 1 && from_sqs.getSelectStatement().getSelectItemList().get(0) instanceof SelectColumn && from_sqs.getFromClause() == null && (((SelectColumn)from_sqs.getSelectStatement().getSelectItemList().get(0)).getColumnExpression().size() < 3 || !((SelectColumn)from_sqs.getSelectStatement().getSelectItemList().get(0)).getColumnExpression().get(1).toString().equals("="))) {
                        isSetExpr = true;
                     }

                     if (isSetExpr) {
                        if (datatype instanceof BinClass) {
                           BinClass bc = (BinClass)datatype;
                           type = bc.getDatatypeName();
                           size = bc.getSize();
                           if (type.equalsIgnoreCase("raw") || type.equalsIgnoreCase("number")) {
                              bc.setSize((String)null);
                              bc.setOpenBrace((String)null);
                              bc.setClosedBrace((String)null);
                           }
                        } else if (money) {
                           NumericClass nc = (NumericClass)datatype;
                           nc.setOpenBrace((String)null);
                           nc.setClosedBrace((String)null);
                           nc.setPrecision((String)null);
                           nc.setScale((String)null);
                        }
                     }
                  }
               }

               NumericClass plsqlNumericClass;
               DateClass plsqlDateClass;
               if (SwisSQLOptions.PLSQL) {
                  if (datatype instanceof NumericClass) {
                     plsqlNumericClass = (NumericClass)datatype;
                     plsqlNumericClass.setOpenBrace((String)null);
                     plsqlNumericClass.setPrecision((String)null);
                     plsqlNumericClass.setScale((String)null);
                     plsqlNumericClass.setClosedBrace((String)null);
                  } else if (datatype instanceof BinClass) {
                     BinClass plsqlBinClass = (BinClass)datatype;
                     plsqlBinClass.setClosedBrace((String)null);
                     plsqlBinClass.setSize((String)null);
                     plsqlBinClass.setOpenBrace((String)null);
                  } else if (datatype instanceof CharacterClass) {
                     CharacterClass plsqlCharacterClass = (CharacterClass)datatype;
                     plsqlCharacterClass.setClosedBrace((String)null);
                     plsqlCharacterClass.setSize((String)null);
                     plsqlCharacterClass.setOpenBrace((String)null);
                  } else if (datatype instanceof DateClass) {
                     plsqlDateClass = (DateClass)datatype;
                     plsqlDateClass.setClosedBrace((String)null);
                     plsqlDateClass.setSize((String)null);
                     plsqlDateClass.setOpenBrace((String)null);
                  }

                  arguments.addElement(datatype);
               } else if (datatype instanceof DateClass) {
                  if (!isdenodo) {
                     this.functionName.setColumnName("TO_DATE");
                     this.setAsDatatype((String)null);
                     this.convertTheDateFunctionWithDateFormatDefinition(this.functionArguments, arguments);
                     plsqlDateClass = (DateClass)datatype;
                     plsqlDateClass.setClosedBrace((String)null);
                     plsqlDateClass.setSize((String)null);
                     plsqlDateClass.setOpenBrace((String)null);
                  } else {
                     arguments.setElementAt(StringFunctions.handleLiteralStringDateForDenodo(arguments.get(0).toString()), 0);
                     arguments.addElement(datatype);
                  }
               } else {
                  arguments.addElement(datatype);
               }

               if (SwisSQLOptions.fromSQLServer && datatype instanceof NumericClass) {
                  plsqlNumericClass = (NumericClass)datatype;
                  if (isDate) {
                     this.functionName.setColumnName("");
                     this.setOpenBracesForFunctionNameRequired(false);
                     this.setAsDatatype((String)null);
                     str = arguments.get(0).toString();
                     arguments.clear();
                     arguments.add(str + " - TO_DATE('01-JAN-1900')");
                  }
               }

               if (isChar && (datatype instanceof NumericClass || datatype instanceof BinClass)) {
                  obj = arguments.get(0);
                  fc = new FunctionCalls();
                  tc = new TableColumn();
                  tc.setColumnName("TRIM");
                  fc.setFunctionName(tc);
                  Vector fnArgs = new Vector();
                  fnArgs.add(obj);
                  fc.setFunctionArguments(fnArgs);
                  arguments.setElementAt(fc, 0);
               }

               if (isCharVarch && datatype instanceof CharacterClass) {
                  String dataType = datatype.getDatatypeName();
                  if (dataType.equalsIgnoreCase("nchar") || dataType.equalsIgnoreCase("nvarchar2") || dataType.equalsIgnoreCase("varchar2") || dataType.equalsIgnoreCase("char") || dataType.equalsIgnoreCase("varchar")) {
                     this.functionName.setColumnName("");
                     this.setAsDatatype((String)null);
                     this.setOpenBracesForFunctionNameRequired(false);
                     arguments.remove(arguments.size() - 1);
                  }
               }
            } else {
               Object obj = this.functionArguments.elementAt(i_count);
               if (obj != null && obj.toString().equalsIgnoreCase("uniqueidentifier")) {
                  arguments.addElement("CHAR(36)");
               } else if (obj != null && obj.toString().equalsIgnoreCase("sql_variant")) {
                  arguments.addElement("SYS.ANYDATA");
               } else {
                  arguments.addElement(this.functionArguments.elementAt(i_count));
               }
            }
         }

         this.setFunctionArguments(arguments);
      }

      if (this.functionName.getColumnName().equalsIgnoreCase("decimal") || this.functionName.getColumnName().equalsIgnoreCase("dec")) {
         this.functionName.setColumnName("CAST");
         arguments = new Vector();
         arguments.addElement(this.functionArguments.get(0));
         this.setAsDatatype("AS");
         NumericClass plsqlNumericClass = new NumericClass();
         plsqlNumericClass.setDatatypeName("NUMBER");
         if (this.functionArguments.size() > 1) {
            SelectColumn sc1 = (SelectColumn)this.functionArguments.get(1);
            SelectColumn sc2 = (SelectColumn)this.functionArguments.get(2);
            Vector p = sc1.getColumnExpression();
            Vector s = sc2.getColumnExpression();
            String prec = (String)p.get(0);
            String scal = (String)s.get(0);
            plsqlNumericClass.setPrecision(prec);
            plsqlNumericClass.setScale(scal);
            plsqlNumericClass.setOpenBrace("(");
            plsqlNumericClass.setClosedBrace(")");
         }

         arguments.addElement(plsqlNumericClass);
         this.setFunctionArguments(arguments);
      }

   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("CAST");
      Vector arguments = new Vector();
      SelectColumn sc = null;

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
            sc = ((SelectColumn)this.functionArguments.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs);
         } else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
            Datatype datatype = (Datatype)this.functionArguments.elementAt(i_count);
            DatatypeMapping mapping = null;
            if (from_sqs != null) {
               mapping = from_sqs.getDatatypeMapping();
            }

            if (!this.mapDatatype(datatype, mapping, sc, from_sqs)) {
               if (datatype.getDatatypeName().equalsIgnoreCase("binary")) {
                  datatype.setDatatypeName("VARBINARY");
                  datatype.setOpenBrace("(");
                  datatype.setSize(datatype.getSize() != null ? datatype.getSize() : "MAX");
                  datatype.setClosedBrace(")");
               } else if (datatype.getDatatypeName().equalsIgnoreCase("char")) {
                  datatype.setDatatypeName("VARCHAR");
                  datatype.setOpenBrace("(");
                  datatype.setSize(datatype.getSize() != null ? datatype.getSize() : "MAX");
                  datatype.setClosedBrace(")");
               }

               datatype.toMSSQLServerString();
            }

            arguments.addElement(datatype);
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toSnowflake(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionArguments.elementAt(1) instanceof BinClass) {
         StringBuffer cast = new StringBuffer();
         if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
            cast.append(((SelectColumn)this.functionArguments.elementAt(0)).toSnowflakeSelect(to_sqs, from_sqs).toString());
         } else {
            cast.append(this.functionArguments.elementAt(0).toString());
         }

         this.functionName.setColumnName("" + cast);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else {
         this.functionName.setColumnName("CAST");
         Vector arguments = new Vector();
         SelectColumn sc = null;

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               sc = ((SelectColumn)this.functionArguments.elementAt(i_count)).toSnowflakeSelect(to_sqs, from_sqs);
               arguments.addElement(sc);
            } else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
               Datatype datatype = (Datatype)this.functionArguments.elementAt(i_count);
               DatatypeMapping mapping = null;
               if (from_sqs != null) {
                  mapping = from_sqs.getDatatypeMapping();
               }

               if (!this.mapDatatype(datatype, mapping, sc, from_sqs)) {
                  datatype.toSnowflakeString();
               }

               if (datatype instanceof CharacterClass) {
                  String type = datatype.getDatatypeName();
                  if (type.equalsIgnoreCase("char")) {
                     datatype.setDatatypeName("TEXT");
                  }
               }

               arguments.addElement(datatype);
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         this.setFunctionArguments(arguments);
      }
   }

   public void toSybase(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("CONVERT");
      this.setAsDatatype((String)null);
      Vector arguments = new Vector();
      SelectColumn sc = null;

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSybaseSelect(to_sqs, from_sqs));
            sc = ((SelectColumn)this.functionArguments.elementAt(i_count)).toSybaseSelect(to_sqs, from_sqs);
         } else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
            Datatype datatype = (Datatype)this.functionArguments.elementAt(i_count);
            DatatypeMapping mapping = null;
            if (from_sqs != null) {
               mapping = from_sqs.getDatatypeMapping();
            }

            if (!this.mapDatatype(datatype, mapping, sc, from_sqs)) {
               datatype.toSybaseString();
            }

            arguments.addElement(datatype);
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      Vector swapArguments = new Vector();
      if (arguments.size() > 1) {
         swapArguments.add(arguments.get(1));
         swapArguments.add(arguments.get(0));
      }

      this.setFunctionArguments(swapArguments);
   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("CAST");
      Vector arguments = new Vector();
      SelectColumn sc = null;

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toDB2Select(to_sqs, from_sqs));
            sc = ((SelectColumn)this.functionArguments.elementAt(i_count)).toDB2Select(to_sqs, from_sqs);
         } else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
            Datatype datatype = (Datatype)this.functionArguments.elementAt(i_count);
            DatatypeMapping mapping = null;
            if (from_sqs != null) {
               mapping = from_sqs.getDatatypeMapping();
            }

            if (!this.mapDatatype(datatype, mapping, sc, from_sqs)) {
               if (datatype instanceof CharacterClass && datatype.getDatatypeName().equalsIgnoreCase("VARCHAR")) {
                  if (datatype.getSize() == null) {
                     datatype.setSize("100");
                     datatype.setOpenBrace("(");
                     datatype.setClosedBrace(")");
                  }

                  Object o = arguments.get(0);
                  if (o instanceof SelectColumn) {
                     SelectColumn sc1 = (SelectColumn)o;
                     Vector v = sc1.getColumnExpression();
                     int size = v.size();

                     for(int k = 0; k < size; ++k) {
                        Object o2 = v.elementAt(k);
                        if (o2 instanceof TableColumn) {
                           TableColumn tc = (TableColumn)o2;
                           String str = "CHAR(" + o2 + ")";
                           v.setElementAt(str, k);
                        }
                     }
                  }
               }

               datatype.toDB2String();
            }

            arguments.addElement(datatype);
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SelectColumn sc = null;
      boolean isBinary = this.functionArguments.elementAt(1) instanceof BinClass;
      boolean isCHAR = this.functionArguments.elementAt(1) instanceof CharacterClass;
      boolean isSigned = this.functionArguments.elementAt(1).toString().trim().equalsIgnoreCase("signed");
      boolean isDate = this.functionArguments.elementAt(1) instanceof DateClass;
      boolean isDecimal = !isSigned && this.functionArguments.elementAt(1) instanceof NumericClass && this.functionArguments.elementAt(1).toString().toLowerCase().trim().startsWith("decimal");
      boolean useCitext = from_sqs != null && from_sqs.getBooleanValues("can.use.citext.over.text");
      if (!isBinary && !isCHAR && !isSigned && !isDate) {
         this.functionName.setColumnName("CAST");
         Vector arguments = new Vector();

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               if (i_count == 0 && isDecimal) {
                  ((SelectColumn)this.functionArguments.elementAt(i_count)).convertAsNumericUDFFunctionCall(from_sqs);
               }

               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
               sc = ((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs);
            } else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
               Datatype datatype = (Datatype)this.functionArguments.elementAt(i_count);
               DatatypeMapping mapping = null;
               if (from_sqs != null) {
                  mapping = from_sqs.getDatatypeMapping();
               }

               if (!this.mapDatatype(datatype, mapping, sc, from_sqs)) {
                  datatype.toPostgreSQLString();
               }

               arguments.addElement(datatype);
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         this.setFunctionArguments(arguments);
      } else {
         StringBuilder cast = new StringBuilder();
         String dataType = "";
         if (useCitext && isBinary || isCHAR || isSigned || isDate) {
            cast.append("CAST(");
         }

         if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
            sc = ((SelectColumn)this.functionArguments.elementAt(0)).toPostgreSQLSelect(to_sqs, from_sqs);
            if (useCitext) {
               cast.append(this.removeCasting(sc));
            } else {
               cast.append(sc);
            }
         } else {
            cast.append(this.functionArguments.elementAt(0).toString());
         }

         if (isBinary) {
            dataType = useCitext ? "TEXT" : "";
         } else if (isCHAR) {
            dataType = StringFunctions.getStringDataType(from_sqs);
         } else if (isSigned) {
            dataType = "BIGINT";
         } else {
            dataType = "DATE";
            String inputDataType = this.functionArguments.elementAt(1).toString().replaceAll(" ", "");
            if (inputDataType.equalsIgnoreCase("DATETIME(3)")) {
               dataType = "TIMESTAMP(3)";
            } else if (inputDataType.equalsIgnoreCase("DATETIME")) {
               dataType = "TIMESTAMP";
            }
         }

         if (useCitext && isBinary || isCHAR || isSigned || isDate) {
            cast.append(" AS ").append(dataType).append(")");
         }

         this.functionName.setColumnName(cast.toString());
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }

   }

   public SelectColumn removeCasting(SelectColumn sc) {
      SelectColumn modSelectColumn = new SelectColumn();
      Vector v = sc.getColumnExpression();
      if (v.size() == 1) {
         if (v.get(0) instanceof SelectColumn) {
            return this.removeCasting((SelectColumn)v.get(0));
         }

         if (v.get(0) instanceof FunctionCalls) {
            final FunctionCalls fc = (FunctionCalls)v.get(0);
            if (fc.getFunctionName().toString().equalsIgnoreCase("cast") && fc.getFunctionArguments().get(1).equals("CITEXT")) {
               modSelectColumn.setColumnExpression(new Vector() {
                  {
                     this.add(fc.getFunctionArguments().get(0));
                  }
               });
               return modSelectColumn;
            }
         }
      }

      return sc;
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (from_sqs != null && from_sqs.isHyperSql()) {
         if (this.functionArguments.elementAt(1) instanceof BinClass) {
            StringBuffer cast = new StringBuffer();
            if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
               cast.append(((SelectColumn)this.functionArguments.elementAt(0)).toMySQLSelect(to_sqs, from_sqs).toString());
            } else {
               cast.append(this.functionArguments.elementAt(0).toString());
            }

            this.functionName.setColumnName("" + cast);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
            return;
         }

         if (this.functionArguments.elementAt(1) instanceof CharacterClass) {
            Datatype datatype = (Datatype)this.functionArguments.get(1);
            if (datatype.getDatatypeName().equalsIgnoreCase("char")) {
               if (datatype.getSize() != null) {
                  datatype.setDatatypeName("VARCHAR");
               } else {
                  datatype.setDatatypeName("LONGVARCHAR");
               }
            }

            this.functionArguments.set(1, datatype);
         }
      }

      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else if (from_sqs.getBooleanValues("consider.precision.for.to.decimal") && this.functionArguments.elementAt(i_count) instanceof NumericClass) {
            NumericClass nc = (NumericClass)this.functionArguments.elementAt(i_count);
            if (nc.getPrecision() != null || nc.getScale() != null) {
               if (nc.getOpenBrace() == null) {
                  nc.setOpenBrace("(");
               }

               if (nc.getClosedBrace() == null) {
                  nc.setClosedBrace(")");
               }

               nc.setPrecision("38");
               if (nc.getScale() == null) {
                  nc.setScale("2");
               }
            }

            arguments.addElement(nc);
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toANSISQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("CAST");
      Vector arguments = new Vector();
      SelectColumn sc = null;

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            functionArgsInSingleQuotesToDouble = false;
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toANSISelect(to_sqs, from_sqs));
            sc = ((SelectColumn)this.functionArguments.elementAt(i_count)).toANSISelect(to_sqs, from_sqs);
            functionArgsInSingleQuotesToDouble = true;
         } else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
            Datatype datatype = (Datatype)this.functionArguments.elementAt(i_count);
            DatatypeMapping mapping = null;
            if (from_sqs != null) {
               mapping = from_sqs.getDatatypeMapping();
            }

            if (!this.mapDatatype(datatype, mapping, sc, from_sqs)) {
               datatype.toANSIString();
            }

            arguments.addElement(datatype);
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionArguments.elementAt(1) instanceof BinClass) {
         StringBuffer cast = new StringBuffer();
         if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
            cast.append(((SelectColumn)this.functionArguments.elementAt(0)).toInformixSelect(to_sqs, from_sqs).toString());
         } else {
            cast.append(this.functionArguments.elementAt(0).toString());
         }

         this.functionName.setColumnName("" + cast);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else {
         this.functionName.setColumnName("");
         Vector arguments = new Vector();
         this.setAsDatatype("");
         SelectColumn sc = null;

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toInformixSelect(to_sqs, from_sqs));
               sc = ((SelectColumn)this.functionArguments.elementAt(i_count)).toInformixSelect(to_sqs, from_sqs);
            } else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
               Datatype datatype = (Datatype)this.functionArguments.elementAt(i_count);
               DatatypeMapping mapping = null;
               if (from_sqs != null) {
                  mapping = from_sqs.getDatatypeMapping();
               }

               if (!this.mapDatatype(datatype, mapping, sc, from_sqs)) {
                  datatype.toInformixString();
               }

               arguments.addElement(datatype);
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         arguments.add(1, "::");
         this.setFunctionArguments(arguments);
      }

   }

   public void toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("CAST");
      Vector arguments = new Vector();
      SelectColumn sc = null;

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toNetezzaSelect(to_sqs, from_sqs));
            sc = ((SelectColumn)this.functionArguments.elementAt(i_count)).toNetezzaSelect(to_sqs, from_sqs);
         } else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
            Datatype datatype = (Datatype)this.functionArguments.elementAt(i_count);
            DatatypeMapping mapping = null;
            if (from_sqs != null) {
               mapping = from_sqs.getDatatypeMapping();
            }

            if (!this.mapDatatype(datatype, mapping, sc, from_sqs)) {
               datatype.toNetezzaString();
            }

            arguments.addElement(datatype);
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("CAST");
      Vector arguments = new Vector();
      SelectColumn sc = null;

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            sc = ((SelectColumn)this.functionArguments.elementAt(i_count)).toTeradataSelect(to_sqs, from_sqs);
            if (sc.getTheCoreSelectItem().trim().equals("''")) {
               arguments.addElement("NULL");
            } else {
               arguments.addElement(sc);
            }
         } else if (!(this.functionArguments.elementAt(i_count) instanceof Datatype)) {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         } else {
            Datatype datatype = (Datatype)this.functionArguments.elementAt(i_count);
            DatatypeMapping mapping = null;
            if (from_sqs != null) {
               mapping = from_sqs.getDatatypeMapping();
            }

            if (!this.mapDatatype(datatype, mapping, sc, from_sqs)) {
               datatype.toTeradataString();
            }

            if (datatype instanceof NumericClass) {
               NumericClass nc = (NumericClass)datatype;
               if (nc.getPrecision() == null && nc.getScale() == null) {
                  nc.setOpenBrace("(");
                  nc.setPrecision("38");
                  nc.setScale("16");
                  nc.setClosedBrace(")");
               }
            } else if (datatype instanceof CharacterClass && SwisSQLOptions.castCharDatatypeAsCaseSpecific) {
               ((CharacterClass)datatype).setCaseSpecificPhrase("CASESPECIFIC");
            } else if (datatype instanceof DateClass && datatype.getDatatypeName().equalsIgnoreCase("date")) {
               datatype.setDatatypeName("TIMESTAMP");
               datatype.setSize("0");
               datatype.setOpenBrace("(");
               datatype.setClosedBrace(")");
            }

            arguments.addElement(datatype);
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void convertTheDateFunctionWithDateFormatDefinition(Vector functionArguments, Vector arguments) {
      for(int i = 0; i < functionArguments.size(); ++i) {
         if (functionArguments.get(0) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)functionArguments.get(0);
            Vector columnExpression = sc.getColumnExpression();
            if (columnExpression != null) {
               for(int index = 0; index < columnExpression.size(); ++index) {
                  if (columnExpression.get(index) instanceof String) {
                     String str = (String)columnExpression.get(index);
                     dateadd dateAddObj = new dateadd();
                     str = dateAddObj.dateFormatConversion(str);
                     if (str.trim().endsWith(")")) {
                        str = str.substring(0, str.length() - 1);
                     }

                     columnExpression.set(index, str);
                     sc.setColumnExpression(columnExpression);
                     arguments.set(0, sc);
                     return;
                  }
               }
            }
         }
      }

   }

   private boolean mapDatatype(Datatype dataType, DatatypeMapping mapping, SelectColumn sc, SelectQueryStatement from_sqs) {
      if (dataType != null) {
         String datatypeName = dataType.getDatatypeName();
         if (datatypeName != null) {
            int i;
            String newDatatypeName;
            String origDatatypeName;
            Vector fromItems;
            if (SwisSQLAPI.objectContext != null) {
               String fromColName = null;
               if (sc != null) {
                  Vector colExp = sc.getColumnExpression();
                  if (colExp != null) {
                     for(int i = 0; i < colExp.size(); ++i) {
                        if (colExp.get(i) instanceof TableColumn) {
                           fromColName = ((TableColumn)colExp.get(i)).getColumnName();
                        }
                     }
                  }
               }

               if (from_sqs == null || from_sqs != null && from_sqs.getFromClause() == null) {
                  Object val = SwisSQLAPI.objectContext.getMappedDatatype((String)null, (String)null, datatypeName);
                  if (val != null) {
                     origDatatypeName = (String)val;
                     if (origDatatypeName != null) {
                        if (origDatatypeName.indexOf("(") != -1) {
                           dataType.setDatatypeName(origDatatypeName.substring(0, origDatatypeName.indexOf("(")));
                           dataType.setOpenBrace("(");
                           dataType.setClosedBrace(")");
                           dataType.setSize(origDatatypeName.substring(origDatatypeName.indexOf("(") + 1, origDatatypeName.indexOf(")")));
                           if (dataType instanceof QuotedIdentifierDatatype) {
                              ((QuotedIdentifierDatatype)dataType).setPrecision(origDatatypeName.substring(origDatatypeName.indexOf("(") + 1, origDatatypeName.indexOf(")")));
                           }
                        } else {
                           dataType.setDatatypeName(origDatatypeName);
                        }

                        return true;
                     }
                  }
               }

               if (fromColName != null && from_sqs != null) {
                  FromClause fromClause = from_sqs.getFromClause();
                  if (fromClause != null) {
                     fromItems = fromClause.getFromItemList();
                     if (fromItems != null) {
                        for(i = 0; i < fromItems.size(); ++i) {
                           if (fromItems.get(i) instanceof FromTable) {
                              Object fromItemObj = ((FromTable)fromItems.get(i)).getTableName();
                              if (fromItemObj != null) {
                                 Object val = SwisSQLAPI.objectContext.getMappedDatatype(fromItemObj.toString().toLowerCase(), fromColName, datatypeName);
                                 if (val != null) {
                                    newDatatypeName = (String)val;
                                    if (newDatatypeName != null) {
                                       if (newDatatypeName.indexOf("(") != -1) {
                                          dataType.setDatatypeName(newDatatypeName.substring(0, newDatatypeName.indexOf("(")));
                                          dataType.setOpenBrace("(");
                                          dataType.setClosedBrace(")");
                                          dataType.setSize(newDatatypeName.substring(newDatatypeName.indexOf("(") + 1, newDatatypeName.indexOf(")")));
                                          if (dataType instanceof QuotedIdentifierDatatype) {
                                             ((QuotedIdentifierDatatype)dataType).setPrecision(newDatatypeName.substring(newDatatypeName.indexOf("(") + 1, newDatatypeName.indexOf(")")));
                                          }
                                       } else {
                                          dataType.setDatatypeName(newDatatypeName);
                                       }

                                       return true;
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }

            if (mapping != null) {
               Hashtable tableSpecificMapping = mapping.getTableSpecificDatatypeMapping();
               if (tableSpecificMapping != null) {
                  String fromColName = null;
                  if (sc != null) {
                     fromItems = sc.getColumnExpression();
                     if (fromItems != null) {
                        for(i = 0; i < fromItems.size(); ++i) {
                           if (fromItems.get(i) instanceof TableColumn) {
                              fromColName = ((TableColumn)fromItems.get(i)).getColumnName();
                           }
                        }
                     }
                  }

                  if (fromColName != null && from_sqs != null) {
                     FromClause fromClause = from_sqs.getFromClause();
                     if (fromClause != null) {
                        Vector fromItems = fromClause.getFromItemList();
                        if (fromItems != null) {
                           for(int i = 0; i < fromItems.size(); ++i) {
                              if (fromItems.get(i) instanceof FromTable && tableSpecificMapping.containsKey(((FromTable)fromItems.get(i)).getTableName().toString().toLowerCase())) {
                                 Hashtable column = (Hashtable)tableSpecificMapping.get(((FromTable)fromItems.get(i)).getTableName().toString().toLowerCase());
                                 if (column != null) {
                                    newDatatypeName = (String)column.get(fromColName.toLowerCase());
                                    if (newDatatypeName != null) {
                                       if (newDatatypeName.indexOf("(") != -1) {
                                          dataType.setDatatypeName(newDatatypeName.substring(0, newDatatypeName.indexOf("(")));
                                          dataType.setOpenBrace("(");
                                          dataType.setClosedBrace(")");
                                          dataType.setSize(newDatatypeName.substring(newDatatypeName.indexOf("(") + 1, newDatatypeName.indexOf(")")));
                                          if (dataType instanceof QuotedIdentifierDatatype) {
                                             ((QuotedIdentifierDatatype)dataType).setPrecision(newDatatypeName.substring(newDatatypeName.indexOf("(") + 1, newDatatypeName.indexOf(")")));
                                          }
                                       } else {
                                          dataType.setDatatypeName(newDatatypeName);
                                       }

                                       return true;
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }

               Hashtable globalMapping = mapping.getGlobalDatatypeMapping();
               if (globalMapping != null) {
                  origDatatypeName = datatypeName;
                  if (dataType.getOpenBrace() != null) {
                     datatypeName = datatypeName + "(" + dataType.getSize() + ")";
                  }

                  String newDatatypeName;
                  if (globalMapping.containsKey(datatypeName.toLowerCase())) {
                     newDatatypeName = (String)globalMapping.get(datatypeName.toLowerCase());
                     if (newDatatypeName.indexOf("(") != -1) {
                        dataType.setDatatypeName(newDatatypeName.substring(0, newDatatypeName.indexOf("(")));
                        dataType.setOpenBrace("(");
                        dataType.setClosedBrace(")");
                        dataType.setSize(newDatatypeName.substring(newDatatypeName.indexOf("(") + 1, newDatatypeName.indexOf(")")));
                        if (dataType instanceof QuotedIdentifierDatatype) {
                           ((QuotedIdentifierDatatype)dataType).setPrecision(newDatatypeName.substring(newDatatypeName.indexOf("(") + 1, newDatatypeName.indexOf(")")));
                        }
                     } else if (datatypeName.indexOf("(") != -1) {
                        dataType.setDatatypeName(newDatatypeName);
                        dataType.setOpenBrace((String)null);
                        dataType.setClosedBrace((String)null);
                        dataType.setSize((String)null);
                     } else {
                        dataType.setDatatypeName(newDatatypeName);
                     }

                     return true;
                  }

                  if (globalMapping.containsKey(origDatatypeName.toLowerCase())) {
                     newDatatypeName = (String)globalMapping.get(origDatatypeName.toLowerCase());
                     dataType.setDatatypeName(newDatatypeName);
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      StringBuffer cast;
      if (this.functionArguments.elementAt(1) instanceof BinClass) {
         cast = new StringBuffer();
         if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
            cast.append(((SelectColumn)this.functionArguments.elementAt(0)).toVectorWiseSelect(to_sqs, from_sqs).toString());
         } else {
            cast.append(this.functionArguments.elementAt(0).toString());
         }

         this.functionName.setColumnName("" + cast);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionArguments.elementAt(1) instanceof DateClass) {
         cast = new StringBuffer();
         if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
            this.handleStringLiteralForDateTime(from_sqs, 0, false);
            cast.append(((SelectColumn)this.functionArguments.elementAt(0)).toVectorWiseSelect(to_sqs, from_sqs).toString());
         } else {
            cast.append(this.functionArguments.elementAt(0).toString());
         }

         String dataType = "DATE";
         if (this.functionArguments.elementAt(1).toString().replaceAll(" ", "").equalsIgnoreCase("DATETIME")) {
            dataType = "TIMESTAMP";
         }

         this.functionName.setColumnName("CAST(" + cast + " AS " + dataType + ")");
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionArguments.elementAt(1) instanceof CharacterClass) {
         cast = new StringBuffer();
         if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
            cast.append(((SelectColumn)this.functionArguments.elementAt(0)).toVectorWiseSelect(to_sqs, from_sqs).toString());
         } else {
            cast.append(this.functionArguments.elementAt(0).toString());
         }

         this.functionName.setColumnName("CAST(" + cast + " as VARCHAR)");
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionArguments.elementAt(1).toString().trim().equalsIgnoreCase("signed")) {
         cast = new StringBuffer();
         if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
            cast.append(((SelectColumn)this.functionArguments.elementAt(0)).toVectorWiseSelect(to_sqs, from_sqs).toString());
         } else {
            cast.append(this.functionArguments.elementAt(0).toString());
         }

         this.functionName.setColumnName("cast(" + cast + " as BIGINT)");
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else {
         int i;
         if (this.functionArguments.elementAt(1).toString().replaceAll(" ", "").equalsIgnoreCase("decimal")) {
            for(i = 0; i < this.functionArguments.size(); ++i) {
               if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                  arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toVectorWiseSelect(to_sqs, from_sqs));
               } else {
                  arguments.addElement(this.functionArguments.elementAt(i));
               }
            }

            this.functionName.setColumnName("cast(" + arguments.get(0) + " as DECIMAL(38,0))");
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         } else {
            this.functionName.setColumnName("CAST");

            for(i = 0; i < this.functionArguments.size(); ++i) {
               if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
                  arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toVectorWiseSelect(to_sqs, from_sqs));
               } else {
                  arguments.addElement(this.functionArguments.elementAt(i));
               }
            }

            this.setFunctionArguments(arguments);
         }
      }

   }

   public void toMysql(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("CAST");
      Vector arguments = new Vector();

      for(int i = 0; i < this.functionArguments.size(); ++i) {
         if (this.functionArguments.elementAt(i) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      StringBuffer cast;
      if (this.functionArguments.elementAt(1) instanceof BinClass) {
         cast = new StringBuffer();
         if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
            cast.append(((SelectColumn)this.functionArguments.elementAt(0)).toBigQuerySelect(to_sqs, from_sqs).toString());
         } else {
            cast.append(this.functionArguments.elementAt(0).toString());
         }

         this.functionName.setColumnName("" + cast);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionArguments.elementAt(1) instanceof CharacterClass) {
         cast = new StringBuffer();
         if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
            cast.append(((SelectColumn)this.functionArguments.elementAt(0)).toBigQuerySelect(to_sqs, from_sqs).toString());
         } else {
            cast.append(this.functionArguments.elementAt(0).toString());
         }

         this.functionName.setColumnName("CAST(" + cast + " as STRING)");
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionArguments.elementAt(1).toString().trim().equalsIgnoreCase("signed")) {
         cast = new StringBuffer();
         if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
            cast.append(((SelectColumn)this.functionArguments.elementAt(0)).toBigQuerySelect(to_sqs, from_sqs).toString());
         } else {
            cast.append(this.functionArguments.elementAt(0).toString());
         }

         this.functionName.setColumnName("cast(" + cast + " as INT64)");
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionArguments.elementAt(1) instanceof DateClass) {
         cast = new StringBuffer();
         if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
            this.handleStringLiteralForDateTime(from_sqs, 0, false);
            cast.append(((SelectColumn)this.functionArguments.elementAt(0)).toBigQuerySelect(to_sqs, from_sqs).toString());
         } else {
            cast.append(this.functionArguments.elementAt(0).toString());
         }

         String dataType = "TIMESTAMP";
         if (this.functionArguments.elementAt(1).toString().replaceAll(" ", "").equalsIgnoreCase("DATE")) {
            dataType = "DATE";
         }

         this.functionName.setColumnName("CAST(" + cast + " AS " + dataType + ")");
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else {
         this.functionName.setColumnName("CAST");
         Vector arguments = new Vector();
         SelectColumn sc = null;

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toBigQuerySelect(to_sqs, from_sqs));
               sc = ((SelectColumn)this.functionArguments.elementAt(i_count)).toBigQuerySelect(to_sqs, from_sqs);
            } else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
               Datatype datatype = (Datatype)this.functionArguments.elementAt(i_count);
               DatatypeMapping mapping = null;
               if (from_sqs != null) {
                  mapping = from_sqs.getDatatypeMapping();
               }

               if (!this.mapDatatype(datatype, mapping, sc, from_sqs)) {
                  datatype.toBigQueryString();
               }

               arguments.addElement(datatype);
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         this.setFunctionArguments(arguments);
      }

   }

   public void toAthena(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      StringBuffer cast;
      if (this.functionArguments.elementAt(1) instanceof BinClass) {
         cast = new StringBuffer();
         if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
            cast.append(((SelectColumn)this.functionArguments.elementAt(0)).toAthenaSelect(to_sqs, from_sqs).toString());
         } else {
            cast.append(this.functionArguments.elementAt(0).toString());
         }

         this.functionName.setColumnName("" + cast);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionArguments.elementAt(1) instanceof CharacterClass) {
         cast = new StringBuffer();
         if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
            cast.append(((SelectColumn)this.functionArguments.elementAt(0)).toAthenaSelect(to_sqs, from_sqs).toString());
         } else {
            cast.append(this.functionArguments.elementAt(0).toString());
         }

         this.functionName.setColumnName("CAST(" + cast + " as VARCHAR)");
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionArguments.elementAt(1).toString().trim().equalsIgnoreCase("signed")) {
         cast = new StringBuffer();
         if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
            cast.append(((SelectColumn)this.functionArguments.elementAt(0)).toAthenaSelect(to_sqs, from_sqs).toString());
         } else {
            cast.append(this.functionArguments.elementAt(0).toString());
         }

         this.functionName.setColumnName("cast(" + cast + " as BIGINT)");
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else {
         SelectColumn sc;
         if (this.functionArguments.elementAt(1) instanceof DateClass) {
            cast = new StringBuffer();
            if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
               sc = ((SelectColumn)this.functionArguments.elementAt(0)).toAthenaSelect(to_sqs, from_sqs);
               String dateString = StringFunctions.handleLiteralStringDateForAthena(sc.toString());
               cast.append(dateString);
            } else {
               cast.append(this.functionArguments.elementAt(0).toString());
            }

            String dataType = "TIMESTAMP";
            if (this.functionArguments.elementAt(1).toString().replaceAll(" ", "").equalsIgnoreCase("DATE")) {
               dataType = "DATE";
            }

            this.functionName.setColumnName("CAST(" + cast + " AS " + dataType + ")");
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         } else {
            this.functionName.setColumnName("CAST");
            Vector arguments = new Vector();
            sc = null;

            for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
               if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                  arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toAthenaSelect(to_sqs, from_sqs));
                  sc = ((SelectColumn)this.functionArguments.elementAt(i_count)).toAthenaSelect(to_sqs, from_sqs);
               } else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
                  Datatype datatype = (Datatype)this.functionArguments.elementAt(i_count);
                  DatatypeMapping mapping = null;
                  if (from_sqs != null) {
                     mapping = from_sqs.getDatatypeMapping();
                  }

                  if (!this.mapDatatype(datatype, mapping, sc, from_sqs)) {
                     datatype.toAthenaString();
                  }

                  arguments.addElement(datatype);
               } else {
                  arguments.addElement(this.functionArguments.elementAt(i_count));
               }
            }

            this.setFunctionArguments(arguments);
         }
      }

   }

   public void toSapHana(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      StringBuffer cast;
      if (this.functionArguments.elementAt(1) instanceof BinClass) {
         cast = new StringBuffer();
         if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
            cast.append(((SelectColumn)this.functionArguments.elementAt(0)).toSapHanaSelect(to_sqs, from_sqs).toString());
         } else {
            cast.append(this.functionArguments.elementAt(0).toString());
         }

         this.functionName.setColumnName("" + cast);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionArguments.elementAt(1) instanceof CharacterClass) {
         cast = new StringBuffer();
         if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
            cast.append(((SelectColumn)this.functionArguments.elementAt(0)).toSapHanaSelect(to_sqs, from_sqs).toString());
         } else {
            cast.append(this.functionArguments.elementAt(0).toString());
         }

         this.functionName.setColumnName("CAST(" + cast + " as VARCHAR)");
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionArguments.elementAt(1).toString().trim().equalsIgnoreCase("signed")) {
         cast = new StringBuffer();
         if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
            cast.append(((SelectColumn)this.functionArguments.elementAt(0)).toSapHanaSelect(to_sqs, from_sqs).toString());
         } else {
            cast.append(this.functionArguments.elementAt(0).toString());
         }

         this.functionName.setColumnName("cast(" + cast + " as BIGINT)");
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else {
         SelectColumn sc;
         if (this.functionArguments.elementAt(1) instanceof DateClass) {
            cast = new StringBuffer();
            if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
               sc = ((SelectColumn)this.functionArguments.elementAt(0)).toSapHanaSelect(to_sqs, from_sqs);
               String dateString = sc.toString();
               cast.append(dateString);
            } else {
               cast.append(this.functionArguments.elementAt(0).toString());
            }

            String dataType = "TIMESTAMP";
            if (this.functionArguments.elementAt(1).toString().replaceAll(" ", "").equalsIgnoreCase("DATE")) {
               dataType = "DATE";
            }

            this.functionName.setColumnName("CAST(" + cast + " AS " + dataType + ")");
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         } else {
            this.functionName.setColumnName("CAST");
            Vector arguments = new Vector();
            sc = null;

            for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
               if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                  arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSapHanaSelect(to_sqs, from_sqs));
                  sc = ((SelectColumn)this.functionArguments.elementAt(i_count)).toSapHanaSelect(to_sqs, from_sqs);
               } else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
                  Datatype datatype = (Datatype)this.functionArguments.elementAt(i_count);
                  DatatypeMapping mapping = null;
                  if (from_sqs != null) {
                     mapping = from_sqs.getDatatypeMapping();
                  }

                  if (!this.mapDatatype(datatype, mapping, sc, from_sqs)) {
                     datatype.toSapHanaString();
                  }

                  arguments.addElement(datatype);
               } else {
                  arguments.addElement(this.functionArguments.elementAt(i_count));
               }
            }

            this.setFunctionArguments(arguments);
         }
      }

   }

   public void toSqlite(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      StringBuffer cast;
      if (this.functionArguments.elementAt(1) instanceof BinClass) {
         cast = new StringBuffer();
         if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
            cast.append(((SelectColumn)this.functionArguments.elementAt(0)).toSqliteSelect(to_sqs, from_sqs).toString());
         } else {
            cast.append(this.functionArguments.elementAt(0).toString());
         }

         this.functionName.setColumnName("" + cast);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionArguments.elementAt(1) instanceof CharacterClass) {
         cast = new StringBuffer();
         if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
            cast.append(((SelectColumn)this.functionArguments.elementAt(0)).toSqliteSelect(to_sqs, from_sqs).toString());
         } else {
            cast.append(this.functionArguments.elementAt(0).toString());
         }

         this.functionName.setColumnName("CAST(" + cast + " as TEXT)");
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionArguments.elementAt(1).toString().trim().equalsIgnoreCase("signed")) {
         cast = new StringBuffer();
         if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
            cast.append(((SelectColumn)this.functionArguments.elementAt(0)).toSqliteSelect(to_sqs, from_sqs).toString());
         } else {
            cast.append(this.functionArguments.elementAt(0).toString());
         }

         this.functionName.setColumnName("cast(" + cast + " as INTEGER)");
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else {
         SelectColumn sc;
         if (this.functionArguments.elementAt(1) instanceof DateClass) {
            cast = new StringBuffer();
            if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
               sc = ((SelectColumn)this.functionArguments.elementAt(0)).toSqliteSelect(to_sqs, from_sqs);
               String dateString = sc.toString();
               cast.append(dateString);
            } else {
               cast.append(this.functionArguments.elementAt(0).toString());
            }

            this.functionName.setColumnName("CAST(" + cast + " AS TEXT)");
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         } else {
            this.functionName.setColumnName("CAST");
            Vector arguments = new Vector();
            sc = null;

            for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
               if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                  arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSqliteSelect(to_sqs, from_sqs));
                  sc = ((SelectColumn)this.functionArguments.elementAt(i_count)).toSqliteSelect(to_sqs, from_sqs);
               } else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
                  Datatype datatype = (Datatype)this.functionArguments.elementAt(i_count);
                  DatatypeMapping mapping = null;
                  if (from_sqs != null) {
                     mapping = from_sqs.getDatatypeMapping();
                  }

                  if (!this.mapDatatype(datatype, mapping, sc, from_sqs)) {
                     datatype.toSqliteString();
                  }

                  arguments.addElement(datatype);
               } else {
                  arguments.addElement(this.functionArguments.elementAt(i_count));
               }
            }

            this.setFunctionArguments(arguments);
         }
      }

   }

   public void toExcel(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String dataType = this.functionArguments.elementAt(1).toString().trim();
      StringBuffer cast;
      if (this.functionArguments.elementAt(1) instanceof BinClass) {
         cast = new StringBuffer();
         if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
            cast.append(((SelectColumn)this.functionArguments.elementAt(0)).toExcelSelect(to_sqs, from_sqs).toString());
         } else {
            cast.append(this.functionArguments.elementAt(0).toString());
         }

         this.functionName.setColumnName("" + cast);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionArguments.elementAt(1) instanceof CharacterClass) {
         cast = new StringBuffer();
         if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
            cast.append(((SelectColumn)this.functionArguments.elementAt(0)).toExcelSelect(to_sqs, from_sqs).toString());
         } else {
            cast.append(this.functionArguments.elementAt(0).toString());
         }

         this.functionName.setColumnName("CStr(" + cast + ")");
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (!dataType.equalsIgnoreCase("signed") && !dataType.equalsIgnoreCase("int")) {
         SelectColumn sc;
         if (this.functionArguments.elementAt(1) instanceof DateClass) {
            cast = new StringBuffer();
            if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
               sc = ((SelectColumn)this.functionArguments.elementAt(0)).toExcelSelect(to_sqs, from_sqs);
               String dateString = sc.toString();
               cast.append(dateString);
            } else {
               cast.append(this.functionArguments.elementAt(0).toString());
            }

            this.functionName.setColumnName("CStr(" + cast + ")");
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         } else {
            this.functionName.setColumnName("CAST");
            Vector arguments = new Vector();
            sc = null;

            for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
               if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                  arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toExcelSelect(to_sqs, from_sqs));
                  sc = ((SelectColumn)this.functionArguments.elementAt(i_count)).toExcelSelect(to_sqs, from_sqs);
               } else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
                  Datatype datatype = (Datatype)this.functionArguments.elementAt(i_count);
                  DatatypeMapping mapping = null;
                  if (from_sqs != null) {
                     mapping = from_sqs.getDatatypeMapping();
                  }

                  if (!this.mapDatatype(datatype, mapping, sc, from_sqs)) {
                     datatype.toExcelString();
                  }

                  arguments.addElement(datatype);
               } else {
                  arguments.addElement(this.functionArguments.elementAt(i_count));
               }
            }

            this.setFunctionArguments(arguments);
         }
      } else {
         cast = new StringBuffer();
         if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
            cast.append(((SelectColumn)this.functionArguments.elementAt(0)).toExcelSelect(to_sqs, from_sqs).toString());
         } else {
            cast.append(this.functionArguments.elementAt(0).toString());
         }

         this.functionName.setColumnName("CLng(" + cast + ")");
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }

   }

   public void toMsAccess(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      StringBuffer cast;
      if (this.functionArguments.elementAt(1) instanceof BinClass) {
         cast = new StringBuffer();
         if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
            cast.append(((SelectColumn)this.functionArguments.elementAt(0)).toMsAccessJdbcSelect(to_sqs, from_sqs).toString());
         } else {
            cast.append(this.functionArguments.elementAt(0).toString());
         }

         this.functionName.setColumnName("" + cast);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionArguments.elementAt(1) instanceof CharacterClass) {
         cast = new StringBuffer();
         if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
            cast.append(((SelectColumn)this.functionArguments.elementAt(0)).toMsAccessJdbcSelect(to_sqs, from_sqs).toString());
         } else {
            cast.append(this.functionArguments.elementAt(0).toString());
         }

         this.functionName.setColumnName("CAST(" + cast + " as VARCHAR)");
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else if (this.functionArguments.elementAt(1).toString().trim().equalsIgnoreCase("signed")) {
         cast = new StringBuffer();
         if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
            cast.append(((SelectColumn)this.functionArguments.elementAt(0)).toMsAccessJdbcSelect(to_sqs, from_sqs).toString());
         } else {
            cast.append(this.functionArguments.elementAt(0).toString());
         }

         this.functionName.setColumnName("cast(" + cast + " as BIGINT)");
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else {
         SelectColumn sc;
         if (this.functionArguments.elementAt(1) instanceof DateClass) {
            cast = new StringBuffer();
            if (this.functionArguments.elementAt(0) instanceof SelectColumn) {
               sc = ((SelectColumn)this.functionArguments.elementAt(0)).toMsAccessJdbcSelect(to_sqs, from_sqs);
               String dateString = StringFunctions.handleLiteralStringDateForHyperSql(sc.toString());
               cast.append(dateString);
            } else {
               cast.append(this.functionArguments.elementAt(0).toString());
            }

            String dataType = "TIMESTAMP";
            if (this.functionArguments.elementAt(1).toString().replaceAll(" ", "").equalsIgnoreCase("DATE")) {
               dataType = "DATE";
            }

            this.functionName.setColumnName("CAST(" + cast + " AS " + dataType + ")");
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         } else {
            this.functionName.setColumnName("CAST");
            Vector arguments = new Vector();
            sc = null;

            for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
               if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                  arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMsAccessJdbcSelect(to_sqs, from_sqs));
                  sc = ((SelectColumn)this.functionArguments.elementAt(i_count)).toMsAccessJdbcSelect(to_sqs, from_sqs);
               } else if (this.functionArguments.elementAt(i_count) instanceof Datatype) {
                  Datatype datatype = (Datatype)this.functionArguments.elementAt(i_count);
                  DatatypeMapping mapping = null;
                  if (from_sqs != null) {
                     mapping = from_sqs.getDatatypeMapping();
                  }

                  if (!this.mapDatatype(datatype, mapping, sc, from_sqs)) {
                     datatype.toMsAccessJdbcString();
                  }

                  arguments.addElement(datatype);
               } else {
                  arguments.addElement(this.functionArguments.elementAt(i_count));
               }
            }

            this.setFunctionArguments(arguments);
         }
      }

   }
}
