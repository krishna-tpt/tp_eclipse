package com.adventnet.swissqlapi.sql.functions.misc;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import java.util.Vector;

public class isnull extends FunctionCalls {
   private String CaseString;

   public void toOracle(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("NVL");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() == 1) {
         this.functionName.setColumnName("DECODE");
         this.functionArguments.add("NULL , 1 , 0");
      }

   }

   public void toMSSQLServer(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("ISNULL");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() == 1) {
         StringBuffer sb = new StringBuffer();
         sb.append("CASE ");
         sb.append(((SelectColumn)this.functionArguments.elementAt(0)).toMSSQLServerSelect(to_sqs, from_sqs).toString() + " ");
         sb.append("WHEN  NULL THEN 1 ELSE 0 ");
         sb.append("END");
         this.CaseString = sb.toString();
         this.functionName = null;
         this.argumentQualifier = null;
         this.functionArguments = null;
      }

   }

   public void toSnowflake(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("NVL");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSnowflakeSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() == 1) {
         this.functionName.setColumnName("DECODE");
         this.functionArguments.add("NULL , 1 , 0");
      }

   }

   public void toSybase(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("ISNULL");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSybaseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() == 1) {
         StringBuffer sb = new StringBuffer();
         sb.append("CASE ");
         if (this.context != null) {
            ((SelectColumn)this.functionArguments.elementAt(0)).setObjectContext(this.context);
            sb.append(this.context.getEquivalent(((SelectColumn)this.functionArguments.elementAt(0)).toSybaseSelect(to_sqs, from_sqs).toString()) + " ");
         } else {
            sb.append(((SelectColumn)this.functionArguments.elementAt(0)).toSybaseSelect(to_sqs, from_sqs).toString() + " ");
         }

         sb.append("WHEN  NULL THEN 1 ELSE 0 ");
         sb.append("END");
         this.CaseString = sb.toString();
         this.functionName = null;
         this.argumentQualifier = null;
         this.functionArguments = null;
      }

   }

   public void toDB2(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("COALESCE");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toDB2Select(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() == 1) {
         StringBuffer sb = new StringBuffer();
         sb.append("CASE ");
         sb.append(((SelectColumn)this.functionArguments.elementAt(0)).toDB2Select(to_sqs, from_sqs).toString() + " ");
         sb.append("WHEN  NULL THEN 1 ELSE 0 ");
         sb.append("END");
         this.CaseString = sb.toString();
         this.functionName = null;
         this.argumentQualifier = null;
         this.functionArguments = null;
      }

   }

   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("COALESCE");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() == 1) {
         StringBuffer sb = new StringBuffer();
         sb.append("CASE WHEN  ");
         sb.append(arguments.get(0).toString() + " ");
         sb.append(" IS NULL THEN 1 ELSE 0 ");
         sb.append("END");
         this.CaseString = sb.toString();
         this.functionName = null;
         this.argumentQualifier = null;
         this.functionArguments = null;
      }

   }

   public void toMySQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.functionArguments.size() != 1) {
         this.functionName.setColumnName("COALESCE");
      }

      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() == 1) {
         this.functionName.setColumnName("ISNULL");
      }

   }

   public void toANSISQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("COALESCE");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toANSISelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() == 1) {
         StringBuffer sb = new StringBuffer();
         sb.append("CASE ");
         sb.append(((SelectColumn)this.functionArguments.elementAt(0)).toANSISelect(to_sqs, from_sqs).toString() + " ");
         sb.append("WHEN  NULL THEN 1 ELSE 0 END ");
         this.CaseString = sb.toString();
         this.functionName = null;
         this.argumentQualifier = null;
         this.functionArguments = null;
      }

   }

   public void toTeradata(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("COALESCE");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toTeradataSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() == 1) {
         StringBuffer sb = new StringBuffer();
         sb.append("CASE ");
         sb.append(((SelectColumn)this.functionArguments.elementAt(0)).toTeradataSelect(to_sqs, from_sqs).toString() + " ");
         sb.append("WHEN  NULL THEN 1 ELSE 0 END ");
         this.CaseString = sb.toString();
         this.functionName = null;
         this.argumentQualifier = null;
         this.functionArguments = null;
      }

   }

   public void toInformix(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("NVL");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toInformixSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() == 1) {
         this.functionName.setColumnName("DECODE");
         this.functionArguments.add("NULL , 1 , 0");
      }

   }

   public void toTimesTen(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("NVL");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toTimesTenSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
   }

   public void toNetezza(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("COALESCE");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toNetezzaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() == 1) {
         StringBuffer sb = new StringBuffer();
         sb.append("CASE ");
         sb.append(((SelectColumn)this.functionArguments.elementAt(0)).toNetezzaSelect(to_sqs, from_sqs).toString() + " ");
         sb.append("WHEN  NULL THEN 1 ELSE 0 END ");
         this.CaseString = sb.toString();
         this.functionName = null;
         this.argumentQualifier = null;
         this.functionArguments = null;
      }

   }

   public String toString() {
      return this.CaseString != null ? this.CaseString : super.toString();
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("COALESCE");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() == 1) {
         StringBuffer sb = new StringBuffer();
         sb.append("CASE WHEN ");
         sb.append(arguments.get(0).toString() + " ");
         sb.append(" IS NULL THEN 1 ELSE 0 END ");
         this.CaseString = sb.toString();
         this.functionName = null;
         this.argumentQualifier = null;
         this.functionArguments = null;
      }

   }

   public void toBigQuery(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("COALESCE");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toBigQuerySelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() == 1) {
         StringBuffer sb = new StringBuffer();
         sb.append("CASE WHEN  ");
         sb.append(arguments.get(0).toString() + " ");
         sb.append(" IS NULL THEN 1 ELSE 0 ");
         sb.append("END");
         this.CaseString = sb.toString();
         this.functionName = null;
         this.argumentQualifier = null;
         this.functionArguments = null;
      }

   }

   public void toSapHana(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.functionName.setColumnName("COALESCE");
      Vector arguments = new Vector();

      for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
         if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
            arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toSapHanaSelect(to_sqs, from_sqs));
         } else {
            arguments.addElement(this.functionArguments.elementAt(i_count));
         }
      }

      this.setFunctionArguments(arguments);
      if (this.functionArguments.size() == 1) {
         StringBuffer sb = new StringBuffer();
         sb.append("CASE WHEN  ");
         sb.append(arguments.get(0).toString() + " ");
         sb.append(" IS NULL THEN 1 ELSE 0 ");
         sb.append("END");
         this.CaseString = sb.toString();
         this.functionName = null;
         this.argumentQualifier = null;
         this.functionArguments = null;
      }

   }
}
