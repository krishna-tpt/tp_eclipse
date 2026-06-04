package com.adventnet.swissqlapi.sql.functions.misc;

import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import java.util.Vector;

public class hex extends FunctionCalls {
   public void toPostgreSQL(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnName = this.functionName.getColumnName();
      SelectColumn sc;
      String arg2;
      String qry;
      if (fnName.equalsIgnoreCase("aes_decrypt")) {
         sc = (SelectColumn)this.functionArguments.get(0);
         String arg1 = "";
         String arg2 = "";
         Vector vc = sc.getColumnExpression();
         if (vc.get(0) instanceof FunctionCalls) {
            FunctionCalls fnCl = (FunctionCalls)((FunctionCalls)vc.get(0));
            if (fnCl.getFunctionName().getColumnName().equalsIgnoreCase("unhex")) {
               arg1 = ((SelectColumn)fnCl.getFunctionArguments().get(0)).toPostgreSQLSelect(to_sqs, from_sqs).toString();
            }
         }

         arg2 = this.functionArguments.get(1).toString();
         new String();
         if (this.functionArguments.size() == 3) {
            if (from_sqs != null && from_sqs.getBooleanValues("cbc.encrypt.new.function")) {
               qry = this.functionArguments.get(2).toString();
               arg2 = "aes256_cbc_decrypt(" + arg1 + "::bytea, " + arg2 + "::bytea," + qry + "::bytea)";
            } else {
               arg2 = "PGP_SYM_DECRYPT((" + arg1 + "::bytea), (" + arg2 + "::text), 'compress-algo=0,s2k-mode=1,cipher-algo=aes256')";
            }
         } else {
            arg2 = "convert_from(decrypt((" + arg1 + ")::bytea,(" + arg2 + ")::bytea, 'aes'),'utf8')";
         }

         this.functionName.setColumnName(arg2);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else {
         if (fnName.equalsIgnoreCase("hex")) {
            sc = (SelectColumn)this.functionArguments.get(0);
            Vector vc = sc.getColumnExpression();
            if (vc.size() == 1 && vc.get(0) instanceof FunctionCalls) {
               FunctionCalls fnCl = (FunctionCalls)((FunctionCalls)vc.get(0));
               if (fnCl.getFunctionName().getColumnName().equalsIgnoreCase("aes_encrypt")) {
                  String arg1 = ((SelectColumn)fnCl.getFunctionArguments().get(0)).toPostgreSQLSelect(to_sqs, from_sqs).toString();
                  arg2 = ((SelectColumn)fnCl.getFunctionArguments().get(1)).toPostgreSQLSelect(to_sqs, from_sqs).toString();
                  new String();
                  if (fnCl.getFunctionArguments().size() == 3) {
                     if (from_sqs != null && from_sqs.getBooleanValues("cbc.encrypt.new.function")) {
                        String arg3 = ((SelectColumn)fnCl.getFunctionArguments().get(2)).toPostgreSQLSelect(to_sqs, from_sqs).toString();
                        qry = "aes256_cbc_encrypt(" + arg1 + ", " + arg2 + "::bytea," + arg3 + "::bytea)";
                     } else {
                        qry = "PGP_SYM_ENCRYPT((" + arg1 + "), (" + arg2 + "), 'compress-algo=0,s2k-mode=1,cipher-algo=aes256')";
                     }
                  } else {
                     qry = "(encrypt(convert_to((" + arg1 + ")::text,'utf8'), (" + arg2 + ")::bytea, 'aes')::bytea)";
                  }

                  this.functionName.setColumnName(qry);
                  this.setOpenBracesForFunctionNameRequired(false);
                  this.functionArguments = new Vector();
                  return;
               }
            }
         }

         Vector arguments = new Vector();

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         if (fnName.equalsIgnoreCase("hex")) {
            this.functionName.setColumnName("encode(CAST(" + arguments.get(0).toString() + " AS TEXT),'hex')");
         } else if (fnName.equalsIgnoreCase("unhex")) {
            this.functionName.setColumnName("encode(decode(CAST(" + arguments.get(0).toString() + " AS TEXT),'hex'),'escape')");
         } else if (fnName.equalsIgnoreCase("mid")) {
            if (arguments.size() == 2) {
               this.functionName.setColumnName("substring((" + arguments.get(0).toString() + ")::text," + arguments.get(1).toString() + ")");
            } else {
               this.functionName.setColumnName("substring((" + arguments.get(0).toString() + ")::text," + arguments.get(1).toString() + "," + arguments.get(2).toString() + ")");
            }
         }

         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      }
   }

   public void toVectorWise(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String fnName = this.functionName.getColumnName();
      String qry = "";
      SelectColumn sc;
      Vector vc;
      String arg1;
      String arg2;
      FunctionCalls fnCl;
      if (fnName.equalsIgnoreCase("aes_decrypt")) {
         sc = (SelectColumn)this.functionArguments.get(0);
         vc = sc.getColumnExpression();
         arg1 = "";
         arg2 = "";
         if (vc.get(0) instanceof FunctionCalls) {
            fnCl = (FunctionCalls)((FunctionCalls)vc.get(0));
            if (fnCl.getFunctionName().getColumnName().equalsIgnoreCase("unhex")) {
               arg1 = ((SelectColumn)fnCl.getFunctionArguments().get(0)).toVectorWiseSelect(to_sqs, from_sqs).toString();
            }
         }

         arg2 = this.functionArguments.get(1).toString();
         qry = "AES_DECRYPT_VARCHAR(" + arg1 + ", " + arg2 + ",128)";
         this.functionName.setColumnName(qry);
         this.setOpenBracesForFunctionNameRequired(false);
         this.functionArguments = new Vector();
      } else {
         if (fnName.equalsIgnoreCase("hex")) {
            sc = (SelectColumn)this.functionArguments.get(0);
            vc = sc.getColumnExpression();
            arg1 = "";
            arg2 = "";
            if (vc.size() == 1 && vc.get(0) instanceof FunctionCalls) {
               fnCl = (FunctionCalls)((FunctionCalls)vc.get(0));
               if (fnCl.getFunctionName().getColumnName().equalsIgnoreCase("aes_encrypt")) {
                  arg1 = ((SelectColumn)fnCl.getFunctionArguments().get(0)).toVectorWiseSelect(to_sqs, from_sqs).toString();
                  arg2 = ((SelectColumn)fnCl.getFunctionArguments().get(1)).toVectorWiseSelect(to_sqs, from_sqs).toString();
                  qry = "AES_ENCRYPT_VARCHAR(" + arg1 + ", " + arg2 + ",128)";
                  this.functionName.setColumnName(qry);
                  this.setOpenBracesForFunctionNameRequired(false);
                  this.functionArguments = new Vector();
                  return;
               }
            }
         }

         Vector arguments = new Vector();

         for(int i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
            if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
               arguments.addElement(((SelectColumn)this.functionArguments.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
            } else {
               arguments.addElement(this.functionArguments.elementAt(i_count));
            }
         }

         this.setFunctionArguments(arguments);
      }
   }
}
