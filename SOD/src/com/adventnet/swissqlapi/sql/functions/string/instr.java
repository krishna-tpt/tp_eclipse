package com.adventnet.swissqlapi.sql.functions.string;

import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.util.Vector;

public class instr extends FunctionCalls {
   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Object temp;
      String tempString;
      int i_count;
      if (!this.functionName.getColumnName().equalsIgnoreCase("CHARINDEX") && !this.functionName.getColumnName().equalsIgnoreCase("LOCATE")) {
         if (this.functionName.getColumnName().equalsIgnoreCase("PATINDEX")) {
            temp = this.functionArguments.get(0);
            if (temp instanceof SelectColumn) {
               tempString = ((SelectColumn)temp).toString().trim();
               if (tempString.startsWith("'%")) {
                  tempString = StringFunctions.replaceFirst("'", "'%", tempString);
                  temp = tempString;
               }

               if (tempString.endsWith("%'")) {
                  tempString = tempString.substring(0, tempString.length() - 2) + "'";
                  temp = tempString;
               }
            }

            this.functionArguments.set(0, this.functionArguments.get(1));
            this.functionArguments.set(1, temp);
         }
      } else {
         temp = this.functionArguments.get(0);
         this.functionArguments.set(0, this.functionArguments.get(1));
         this.functionArguments.set(1, temp);
         if (this.functionArguments.size() > 2) {
            tempString = this.functionArguments.get(2).toString();
            if (tempString.indexOf("-") != -1) {
               this.functionArguments.setElementAt("1", 2);
            } else {
               try {
                  i_count = Integer.parseInt(tempString);
                  if (i_count == 0) {
                     this.functionArguments.setElementAt("1", 2);
                  }
               } catch (Exception var6) {
               }
            }
         }
      }

      this.functionName.setColumnName("INSTR");
      Vector arguments = new Vector();
      boolean isdenodo = from_sqs != null && from_sqs.isDenodo();

      for(i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (isdenodo) {
         String qry;
         if (arguments.size() == 2) {
            qry = "INSTR(" + arguments.get(0) + "," + arguments.get(1) + ")+1";
            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.setFunctionArguments(new Vector());
         } else {
            qry = " case when " + arguments.get(2) + " = 0 then 0 when (INSTR(SUBSTR(" + arguments.get(0) + ", " + arguments.get(2) + ")," + arguments.get(1) + ")+1)=0 then 0 else ((INSTR(SUBSTR(" + arguments.get(0) + ", " + arguments.get(2) + "), " + arguments.get(1) + "))+" + arguments.get(2) + ") end";
            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.setFunctionArguments(new Vector());
         }
      }

   }

   public void toSnowflake(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSnowflakeSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      Vector swaparg = new Vector();
      if (arguments.size() == 2 && !this.functionName.getColumnName().equalsIgnoreCase("LOCATE")) {
         swaparg.addElement(arguments.get(1));
         swaparg.addElement(arguments.get(0));
         this.setFunctionArguments(swaparg);
      } else if (arguments.size() == 3 && !this.functionName.getColumnName().equalsIgnoreCase("LOCATE")) {
         swaparg.addElement(arguments.get(1));
         swaparg.addElement(arguments.get(0));
         swaparg.addElement(arguments.get(2));
         this.setFunctionArguments(swaparg);
      } else {
         this.setFunctionArguments(arguments);
      }

      this.functionName.setColumnName("CHARINDEX");
   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String orgFunctionName = new String();
      if (this.functionName.getColumnName().equalsIgnoreCase("INSTR") || this.functionName.getColumnName().equalsIgnoreCase("STRPOS")) {
         orgFunctionName = this.functionName.getColumnName();
         Object temp = this.functionArguments.get(0);
         this.functionArguments.set(0, this.functionArguments.get(1));
         this.functionArguments.set(1, temp);
      }

      if (this.functionName.getColumnName().equalsIgnoreCase("PATINDEX")) {
         this.functionName.setColumnName("PATINDEX");
      } else if (this.functionArguments.size() <= 3) {
         this.functionName.setColumnName("CHARINDEX");
      } else {
         this.functionName.setColumnName("dbo.ADV_CHARINDEX4");
      }

      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (orgFunctionName.equalsIgnoreCase("INSTR") && this.functionArguments.size() == 4 && this.functionArguments.get(3) instanceof SelectColumn) {
         try {
            Vector colExpression = ((SelectColumn)this.functionArguments.get(3)).getColumnExpression();
            int fourthArg = Integer.parseInt((String)colExpression.get(0));
            if (fourthArg == 1) {
               this.functionArguments.removeElementAt(3);
            }
         } catch (Exception var7) {
            var7.printStackTrace();
         }
      }

   }

   public void toSybase(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String orgFunctionName = new String();
      if (this.functionName.getColumnName().equalsIgnoreCase("INSTR") || this.functionName.getColumnName().equalsIgnoreCase("STRPOS")) {
         orgFunctionName = this.functionName.getColumnName();
         Object temp = this.functionArguments.get(0);
         this.functionArguments.set(0, this.functionArguments.get(1));
         this.functionArguments.set(1, temp);
      }

      if (this.functionName.getColumnName().equalsIgnoreCase("PATINDEX")) {
         this.functionName.setColumnName("PATINDEX");
      } else {
         this.functionName.setColumnName("CHARINDEX");
      }

      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSybaseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (orgFunctionName.equalsIgnoreCase("INSTR") && this.functionArguments.size() == 4 && this.functionArguments.get(3) instanceof SelectColumn) {
         try {
            Vector colExpression = ((SelectColumn)this.functionArguments.get(3)).getColumnExpression();
            int fourthArg = Integer.parseInt((String)colExpression.get(0));
            if (fourthArg == 1) {
               this.functionArguments.removeElementAt(3);
            }
         } catch (Exception var7) {
            var7.printStackTrace();
         }
      }

   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Object temp;
      if (!this.functionName.getColumnName().equalsIgnoreCase("INSTR") && !this.functionName.getColumnName().equalsIgnoreCase("STRPOS")) {
         if (this.functionName.getColumnName().equalsIgnoreCase("PATINDEX")) {
            temp = this.functionArguments.get(0);
            if (temp instanceof SelectColumn) {
               String tempString = ((SelectColumn)temp).toString().trim();
               if (tempString.startsWith("'%")) {
                  tempString = StringFunctions.replaceFirst("'", "'%", tempString);
                  temp = tempString;
               }

               if (tempString.endsWith("%'")) {
                  tempString = tempString.substring(0, tempString.length() - 2) + "'";
                  temp = tempString;
               }
            }

            this.functionArguments.set(0, this.functionArguments.get(1));
            this.functionArguments.set(1, temp);
         }
      } else {
         temp = this.functionArguments.get(0);
         this.functionArguments.set(0, this.functionArguments.get(1));
         this.functionArguments.set(1, temp);
      }

      this.functionName.setColumnName("LOCATE");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toDB2Select(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Object temp;
      if (!this.functionName.getColumnName().equalsIgnoreCase("CHARINDEX") && !this.functionName.getColumnName().equalsIgnoreCase("LOCATE")) {
         if (this.functionName.getColumnName().equalsIgnoreCase("PATINDEX")) {
            temp = this.functionArguments.get(0);
            if (temp instanceof SelectColumn) {
               String tempString = ((SelectColumn)temp).toString().trim();
               if (tempString.startsWith("'%")) {
                  tempString = StringFunctions.replaceFirst("'", "'%", tempString);
                  temp = tempString;
               }

               if (tempString.endsWith("%'")) {
                  tempString = tempString.substring(0, tempString.length() - 2) + "'";
                  temp = tempString;
               }
            }

            this.functionArguments.set(0, this.functionArguments.get(1));
            this.functionArguments.set(1, temp);
         }
      } else {
         temp = this.functionArguments.get(0);
         this.functionArguments.set(0, this.functionArguments.get(1));
         this.functionArguments.set(1, temp);
      }

      this.functionName.setColumnName("STRPOS");
      Vector arguments = new Vector();

      int argLength;
      for(argLength = 0; argLength < this.functionArguments.size(); ++argLength) {
         if (this.functionArguments.elementAt(argLength) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.functionArguments.elementAt(argLength);
            if (argLength <= 1) {
               sc.convertSelectColumnToTextDataType();
            }

            arguments.addElement(sc.toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(argLength));
         }
      }

      this.setFunctionArguments(arguments);
      if (!SwisSQLOptions.isDataupia || this.functionName.getColumnName() == null || !this.functionName.getColumnName().trim().equalsIgnoreCase("INSTR")) {
         argLength = this.functionArguments.size();
         if (argLength == 3) {
            String qry = " case when " + arguments.get(2) + " = 0 then 0 when (STRPOS(SUBSTRING(" + arguments.get(0) + ", " + arguments.get(2) + ")," + arguments.get(1) + "))=0 then 0 else ((STRPOS(SUBSTRING(" + arguments.get(0) + ", " + arguments.get(2) + "), " + arguments.get(1) + ")-1)+" + arguments.get(2) + ") end";
            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         }

      }
   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionName.getColumnName().equalsIgnoreCase("PATINDEX")) {
         Object temp = this.functionArguments.get(0);
         if (temp instanceof SelectColumn) {
            String tempString = ((SelectColumn)temp).toString().trim();
            if (tempString.startsWith("'%")) {
               tempString = StringFunctions.replaceFirst("'", "'%", tempString);
               temp = tempString;
            }

            if (tempString.endsWith("%'")) {
               tempString = tempString.substring(0, tempString.length() - 2) + "'";
               temp = tempString;
            }
         }

         this.functionArguments.set(0, this.functionArguments.get(1));
         this.functionArguments.set(1, temp);
      }

      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (this.functionName.getColumnName().equalsIgnoreCase("INSTR") || this.functionName.getColumnName().equalsIgnoreCase("STRPOS")) {
         Object temp = arguments.get(0);
         arguments.set(0, arguments.get(1));
         arguments.set(1, temp);
      }

      this.functionName.setColumnName("LOCATE");
      this.setFunctionArguments(arguments);
   }

   public void toANSISQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Object temp;
      if (!this.functionName.getColumnName().equalsIgnoreCase("CHARINDEX") && !this.functionName.getColumnName().equalsIgnoreCase("LOCATE")) {
         if (this.functionName.getColumnName().equalsIgnoreCase("PATINDEX")) {
            temp = this.functionArguments.get(0);
            if (temp instanceof SelectColumn) {
               String tempString = ((SelectColumn)temp).toString().trim();
               if (tempString.startsWith("'%")) {
                  tempString = StringFunctions.replaceFirst("'", "'%", tempString);
                  temp = tempString;
               }

               if (tempString.endsWith("%'")) {
                  tempString = tempString.substring(0, tempString.length() - 2) + "'";
                  temp = tempString;
               }
            }

            this.functionArguments.set(0, this.functionArguments.get(1));
            this.functionArguments.set(1, temp);
         }
      } else {
         temp = this.functionArguments.get(0);
         this.functionArguments.set(0, this.functionArguments.get(1));
         this.functionArguments.set(1, temp);
      }

      this.functionName.setColumnName("ANSI_INSTR");
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

   public void toTimesTen(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      throw new ConvertException("\nThe built-in function " + this.functionName.getColumnName().toUpperCase() + " is not supported in TimesTen 5.1.21\n");
   }

   public void toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Object temp;
      if (!this.functionName.getColumnName().equalsIgnoreCase("CHARINDEX") && !this.functionName.getColumnName().equalsIgnoreCase("LOCATE")) {
         if (this.functionName.getColumnName().equalsIgnoreCase("PATINDEX")) {
            temp = this.functionArguments.get(0);
            if (temp instanceof SelectColumn) {
               String tempString = ((SelectColumn)temp).toString().trim();
               if (tempString.startsWith("'%")) {
                  tempString = StringFunctions.replaceFirst("'", "'%", tempString);
                  temp = tempString;
               }

               if (tempString.endsWith("%'")) {
                  tempString = tempString.substring(0, tempString.length() - 2) + "'";
                  temp = tempString;
               }
            }

            this.functionArguments.set(0, this.functionArguments.get(1));
            this.functionArguments.set(1, temp);
         }
      } else {
         temp = this.functionArguments.get(0);
         this.functionArguments.set(0, this.functionArguments.get(1));
         this.functionArguments.set(1, temp);
      }

      this.functionName.setColumnName("INSTR");
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
      Object temp;
      if (!this.functionName.getColumnName().equalsIgnoreCase("CHARINDEX") && !this.functionName.getColumnName().equalsIgnoreCase("LOCATE")) {
         if (this.functionName.getColumnName().equalsIgnoreCase("PATINDEX")) {
            temp = this.functionArguments.get(0);
            if (temp instanceof SelectColumn) {
               String tempString = ((SelectColumn)temp).toString().trim();
               if (tempString.startsWith("'%")) {
                  tempString = StringFunctions.replaceFirst("'", "'%", tempString);
                  temp = tempString;
               }

               if (tempString.endsWith("%'")) {
                  tempString = tempString.substring(0, tempString.length() - 2) + "'";
                  temp = tempString;
               }
            }

            this.functionArguments.set(0, this.functionArguments.get(1));
            this.functionArguments.set(1, temp);
         }
      } else {
         temp = this.functionArguments.get(0);
         this.functionArguments.set(0, this.functionArguments.get(1));
         this.functionArguments.set(1, temp);
      }

      this.functionName.setColumnName("INSTR");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toTeradataSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (arguments.size() == 2) {
         this.functionName.setColumnName("POSITION");
         Vector newArguments = new Vector();
         newArguments.add(arguments.lastElement());
         newArguments.add(arguments.firstElement());
         this.setFunctionArguments(newArguments);
         this.setAsDatatype("IN");
      } else {
         this.setFunctionArguments(arguments);
      }

   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      StringBuffer[] argu = new StringBuffer[this.functionArguments.size()];
      String qry = "";
      String firstArg = "";
      String secArg = "";

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         argu[i_count] = new StringBuffer();
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.functionArguments.elementAt(i_count);
            if (i_count <= 1) {
               sc.convertSelectColumnToTextDataType();
            }

            argu[i_count].append(sc.toVectorWiseSelect(to_sqs, from_sqs));
         } else {
            argu[i_count].append(this.functionArguments.elementAt(i_count));
         }
      }

      firstArg = argu[0].toString();
      secArg = argu[1].toString();
      if (this.functionName.getColumnName().equalsIgnoreCase("INSTR")) {
         firstArg = argu[1].toString();
         secArg = argu[0].toString();
      }

      if (this.functionArguments.size() == 2) {
         qry = " position(" + firstArg + "," + secArg + ")";
      } else if (this.functionArguments.size() == 3) {
         qry = "if(position(" + firstArg + ",substring(" + secArg + "," + argu[2] + "))>0,(" + argu[2] + "+position(" + firstArg + ",substring(" + secArg + "," + argu[2] + "))-1),0)";
      }

      this.functionName.setColumnName(qry);
      this.setOpenBracesForFunctionNameRequired(false);
      this.functionArguments = new Vector();
   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Object temp;
      if (!this.functionName.getColumnName().equalsIgnoreCase("CHARINDEX") && !this.functionName.getColumnName().equalsIgnoreCase("LOCATE")) {
         if (this.functionName.getColumnName().equalsIgnoreCase("PATINDEX")) {
            temp = this.functionArguments.get(0);
            if (temp instanceof SelectColumn) {
               String tempString = ((SelectColumn)temp).toString().trim();
               if (tempString.startsWith("'%")) {
                  tempString = StringFunctions.replaceFirst("'", "'%", tempString);
                  temp = tempString;
               }

               if (tempString.endsWith("%'")) {
                  tempString = tempString.substring(0, tempString.length() - 2) + "'";
                  temp = tempString;
               }
            }

            this.functionArguments.set(0, this.functionArguments.get(1));
            this.functionArguments.set(1, temp);
         }
      } else {
         temp = this.functionArguments.get(0);
         this.functionArguments.set(0, this.functionArguments.get(1));
         this.functionArguments.set(1, temp);
      }

      this.functionName.setColumnName("STRPOS");
      Vector arguments = new Vector();

      int argLength;
      for(argLength = 0; argLength < this.functionArguments.size(); ++argLength) {
         if (this.functionArguments.elementAt(argLength) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.functionArguments.elementAt(argLength);
            arguments.addElement(sc.toBigQuerySelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(argLength));
         }
      }

      this.setFunctionArguments(arguments);
      if (!SwisSQLOptions.isDataupia || this.functionName.getColumnName() == null || !this.functionName.getColumnName().trim().equalsIgnoreCase("INSTR")) {
         argLength = this.functionArguments.size();
         if (argLength == 3) {
            String qry = " case when " + arguments.get(2) + " = 0 then 0 when (STRPOS(SUBSTR(" + arguments.get(0) + ", " + arguments.get(2) + ")," + arguments.get(1) + "))=0 then 0 else ((STRPOS(SUBSTR(" + arguments.get(0) + ", " + arguments.get(2) + "), " + arguments.get(1) + ")-1)+" + arguments.get(2) + ") end";
            this.functionName.setColumnName(qry);
            this.setOpenBracesForFunctionNameRequired(false);
            this.functionArguments = new Vector();
         }

      }
   }

   public void toAthena(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Object temp;
      if (!this.functionName.getColumnName().equalsIgnoreCase("CHARINDEX") && !this.functionName.getColumnName().equalsIgnoreCase("LOCATE")) {
         if (this.functionName.getColumnName().equalsIgnoreCase("PATINDEX")) {
            temp = this.functionArguments.get(0);
            if (temp instanceof SelectColumn) {
               String tempString = ((SelectColumn)temp).toString().trim();
               if (tempString.startsWith("'%")) {
                  tempString = StringFunctions.replaceFirst("'", "'%", tempString);
                  temp = tempString;
               }

               if (tempString.endsWith("%'")) {
                  tempString = tempString.substring(0, tempString.length() - 2) + "'";
                  temp = tempString;
               }
            }

            this.functionArguments.set(0, this.functionArguments.get(1));
            this.functionArguments.set(1, temp);
         }
      } else {
         temp = this.functionArguments.get(0);
         this.functionArguments.set(0, this.functionArguments.get(1));
         this.functionArguments.set(1, temp);
      }

      this.functionName.setColumnName("STRPOS");
      Vector arguments = new Vector();

      int i_count;
      for(i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.functionArguments.elementAt(i_count);
            arguments.addElement(sc.toAthenaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      i_count = this.functionArguments.size();
      if (i_count == 3) {
         String qry = " case when " + arguments.get(2) + " = 0 then 0 when (STRPOS(SUBSTR(" + arguments.get(0) + ", " + arguments.get(2) + ")," + arguments.get(1) + "))=0 then 0 else ((STRPOS(SUBSTR(" + arguments.get(0) + ", " + arguments.get(2) + "), " + arguments.get(1) + ")-1)+" + arguments.get(2) + ") end";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }

   }

   public void toSapHana(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      Object temp;
      if (!this.functionName.getColumnName().equalsIgnoreCase("CHARINDEX") && !this.functionName.getColumnName().equalsIgnoreCase("LOCATE")) {
         if (this.functionName.getColumnName().equalsIgnoreCase("PATINDEX")) {
            temp = this.functionArguments.get(0);
            if (temp instanceof SelectColumn) {
               String tempString = ((SelectColumn)temp).toString().trim();
               if (tempString.startsWith("'%")) {
                  tempString = StringFunctions.replaceFirst("'", "'%", tempString);
                  temp = tempString;
               }

               if (tempString.endsWith("%'")) {
                  tempString = tempString.substring(0, tempString.length() - 2) + "'";
                  temp = tempString;
               }
            }

            this.functionArguments.set(0, this.functionArguments.get(1));
            this.functionArguments.set(1, temp);
         }
      } else {
         temp = this.functionArguments.get(0);
         this.functionArguments.set(0, this.functionArguments.get(1));
         this.functionArguments.set(1, temp);
      }

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSapHanaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.functionName.setColumnName("LOCATE");
      this.setFunctionArguments(arguments);
   }

   public void toSqlite(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();
      Object temp;
      String tempString;
      if (!this.functionName.getColumnName().equalsIgnoreCase("CHARINDEX") && !this.functionName.getColumnName().equalsIgnoreCase("LOCATE")) {
         if (this.functionName.getColumnName().equalsIgnoreCase("PATINDEX")) {
            temp = this.functionArguments.get(0);
            if (temp instanceof SelectColumn) {
               tempString = ((SelectColumn)temp).toString().trim();
               if (tempString.startsWith("'%")) {
                  tempString = StringFunctions.replaceFirst("'", "'%", tempString);
                  temp = tempString;
               }

               if (tempString.endsWith("%'")) {
                  tempString = tempString.substring(0, tempString.length() - 2) + "'";
                  temp = tempString;
               }
            }

            this.functionArguments.set(0, this.functionArguments.get(1));
            this.functionArguments.set(1, temp);
         }
      } else {
         temp = this.functionArguments.get(0);
         this.functionArguments.set(0, this.functionArguments.get(1));
         this.functionArguments.set(1, temp);
      }

      int i_count;
      for(i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSqliteSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.functionName.setColumnName("INSTR");
      this.setFunctionArguments(arguments);
      i_count = this.functionArguments.size();
      if (i_count == 3) {
         tempString = " case when " + arguments.get(2) + " = 0 then 0 when (INSTR(SUBSTR(" + arguments.get(0) + ", " + arguments.get(2) + ")," + arguments.get(1) + "))=0 then 0 else ((INSTR(SUBSTR(" + arguments.get(0) + ", " + arguments.get(2) + "), " + arguments.get(1) + ")-1)+" + arguments.get(2) + ") end";
         this.functionName.setColumnName(tempString);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }

   }

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.functionArguments.elementAt(i_count);
            arguments.addElement(sc.toInformixSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (this.functionName.getColumnName().equalsIgnoreCase("CHARINDEX") || this.functionName.getColumnName().equalsIgnoreCase("LOCATE")) {
         Object temp = arguments.get(0);
         arguments.set(0, arguments.get(1));
         arguments.set(1, temp);
      }

      this.functionName.setColumnName("INSTR");
      this.setFunctionArguments(arguments);
   }

   public void toExcel(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.functionArguments.elementAt(i_count);
            arguments.addElement(sc.toExcelSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (this.functionName.getColumnName().equalsIgnoreCase("CHARINDEX") || this.functionName.getColumnName().equalsIgnoreCase("LOCATE")) {
         Object temp = arguments.get(0);
         arguments.set(0, arguments.get(1));
         arguments.set(1, temp);
      }

      this.functionName.setColumnName("INSTR");
      this.setFunctionArguments(arguments);
   }

   public void toMsAccess(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionName.getColumnName().equalsIgnoreCase("PATINDEX")) {
         Object temp = this.functionArguments.get(0);
         if (temp instanceof SelectColumn) {
            String tempString = ((SelectColumn)temp).toString().trim();
            if (tempString.startsWith("'%")) {
               tempString = StringFunctions.replaceFirst("'", "'%", tempString);
               temp = tempString;
            }

            if (tempString.endsWith("%'")) {
               tempString = tempString.substring(0, tempString.length() - 2) + "'";
               temp = tempString;
            }
         }

         this.functionArguments.set(0, this.functionArguments.get(1));
         this.functionArguments.set(1, temp);
      }

      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMsAccessJdbcSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      if (this.functionName.getColumnName().equalsIgnoreCase("INSTR") || this.functionName.getColumnName().equalsIgnoreCase("STRPOS")) {
         Object temp = arguments.get(0);
         arguments.set(0, arguments.get(1));
         arguments.set(1, temp);
      }

      this.functionName.setColumnName("LOCATE");
      this.setFunctionArguments(arguments);
   }
}
