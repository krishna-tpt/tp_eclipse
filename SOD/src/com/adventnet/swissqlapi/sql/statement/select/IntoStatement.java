package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.ModifiedObjectAttr;
import com.adventnet.swissqlapi.sql.statement.create.CreateColumn;
import com.adventnet.swissqlapi.sql.statement.create.CreateQueryStatement;
import com.adventnet.swissqlapi.sql.statement.create.CreateSequenceStatement;
import com.adventnet.swissqlapi.sql.statement.insert.InsertClause;
import com.adventnet.swissqlapi.sql.statement.insert.InsertQueryStatement;
import com.adventnet.swissqlapi.sql.statement.update.OptionalSpecifier;
import com.adventnet.swissqlapi.sql.statement.update.TableClause;
import com.adventnet.swissqlapi.sql.statement.update.TableExpression;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.util.misc.CustomizeUtil;
import java.util.ArrayList;
import java.util.Vector;

public class IntoStatement {
   private String IntoClause;
   private String TableQualifier;
   private String TableKeyword;
   private String FileQualifier;
   private String TableOrFileName;
   private ArrayList vArrayList = new ArrayList();
   private UserObjectContext context = null;
   private CommentClass commentObj;
   private String fieldsTerminatedByString = null;
   private String optionallyEnclosed = null;
   private String linesTerminated = null;

   public void setCommentClass(CommentClass commentObj) {
      this.commentObj = commentObj;
   }

   public void setIntoClause(String s_ic) {
      this.IntoClause = s_ic;
   }

   public void setTableQualifier(String s_tq) {
      this.TableQualifier = s_tq;
   }

   public void setObjectContext(UserObjectContext obj) {
      this.context = obj;
   }

   public void setTableKeyword(String s_tk) {
      this.TableKeyword = s_tk;
   }

   public void setFileQualifier(String s_fq) {
      this.FileQualifier = s_fq;
   }

   public void setTableOrFileName(String s_tofn) {
      this.TableOrFileName = s_tofn;
   }

   public void addVarray(String vArray) {
      this.vArrayList.add(vArray);
   }

   public void setVarray(ArrayList vArray) {
      this.vArrayList = vArray;
   }

   public void setFieldsTerminated(String fields) {
      this.fieldsTerminatedByString = fields;
   }

   public void setOptionallyEnclosed(String optionallyEnclosedBy) {
      this.optionallyEnclosed = optionallyEnclosedBy;
   }

   public void setLinesTerminated(String linesTerminatedBy) {
      this.linesTerminated = linesTerminatedBy;
   }

   public String getFieldsTerminated() {
      return this.fieldsTerminatedByString;
   }

   public String getOptionallyEnclosed() {
      return this.optionallyEnclosed;
   }

   public String getLinesTerminated() {
      return this.linesTerminated;
   }

   public String getTableOrFileName() {
      return this.TableOrFileName;
   }

   public ArrayList getVarray() {
      return this.vArrayList;
   }

   public String getTableQualifier() {
      return this.TableQualifier;
   }

   public String getFileQualifier() {
      return this.FileQualifier;
   }

   public String getTableKeyword() {
      return this.TableKeyword;
   }

   public CommentClass getCommentClass() {
      return this.commentObj;
   }

   public IntoStatement toPostgreSQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      IntoStatement is = new IntoStatement();
      is.setIntoClause(this.IntoClause);
      is.setTableQualifier(this.TableQualifier);
      is.setTableKeyword(this.TableKeyword);
      is.setTableOrFileName(this.TableOrFileName);
      if (this.FileQualifier != null) {
         throw new ConvertException("Conversion failure..Incorrect syntax near the keyword " + this.TableOrFileName);
      } else {
         return is;
      }
   }

   public IntoStatement toBigQuerySelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      IntoStatement is = new IntoStatement();
      is.setIntoClause(this.IntoClause);
      is.setTableQualifier(this.TableQualifier);
      is.setTableKeyword(this.TableKeyword);
      is.setTableOrFileName(this.TableOrFileName);
      if (this.FileQualifier != null) {
         throw new ConvertException("Conversion failure..Incorrect syntax near the keyword " + this.TableOrFileName);
      } else {
         return is;
      }
   }

   public IntoStatement toAthenaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      IntoStatement is = new IntoStatement();
      is.setIntoClause(this.IntoClause);
      is.setTableQualifier(this.TableQualifier);
      is.setTableKeyword(this.TableKeyword);
      is.setTableOrFileName(this.TableOrFileName);
      if (this.FileQualifier != null) {
         throw new ConvertException("Conversion failure..Incorrect syntax near the keyword " + this.TableOrFileName);
      } else {
         return is;
      }
   }

   public IntoStatement toSapHanaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      IntoStatement is = new IntoStatement();
      is.setIntoClause(this.IntoClause);
      is.setTableQualifier(this.TableQualifier);
      is.setTableKeyword(this.TableKeyword);
      is.setTableOrFileName(this.TableOrFileName);
      if (this.FileQualifier != null) {
         throw new ConvertException("Conversion failure..Incorrect syntax near the keyword " + this.TableOrFileName);
      } else {
         return is;
      }
   }

   public IntoStatement toSqliteSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      IntoStatement is = new IntoStatement();
      is.setIntoClause(this.IntoClause);
      is.setTableQualifier(this.TableQualifier);
      is.setTableKeyword(this.TableKeyword);
      is.setTableOrFileName(this.TableOrFileName);
      if (this.FileQualifier != null) {
         throw new ConvertException("Conversion failure..Incorrect syntax near the keyword " + this.TableOrFileName);
      } else {
         return is;
      }
   }

   public IntoStatement toExcelSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      IntoStatement is = new IntoStatement();
      is.setIntoClause(this.IntoClause);
      is.setTableQualifier(this.TableQualifier);
      is.setTableKeyword(this.TableKeyword);
      is.setTableOrFileName(this.TableOrFileName);
      if (this.FileQualifier != null) {
         throw new ConvertException("Conversion failure..Incorrect syntax near the keyword " + this.TableOrFileName);
      } else {
         return is;
      }
   }

   public IntoStatement toInformixSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      IntoStatement is = new IntoStatement();
      is.setIntoClause(this.IntoClause);
      is.setTableQualifier(this.TableQualifier);
      is.setTableKeyword(this.TableKeyword);
      is.setTableOrFileName(this.TableOrFileName);
      if (this.FileQualifier != null) {
         throw new ConvertException("Conversion failure..Incorrect syntax near the keyword " + this.TableOrFileName);
      } else {
         return is;
      }
   }

   public IntoStatement toMsAccessJdbcSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      IntoStatement is = new IntoStatement();
      is.setIntoClause(this.IntoClause);
      is.setTableQualifier(this.TableQualifier);
      is.setTableKeyword(this.TableKeyword);
      is.setTableOrFileName(this.TableOrFileName);
      if (this.FileQualifier != null) {
         throw new ConvertException("Conversion failure..Incorrect syntax near the keyword " + this.TableOrFileName);
      } else {
         return is;
      }
   }

   public IntoStatement toSnowflakeSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      IntoStatement is = new IntoStatement();
      is.setIntoClause(this.IntoClause);
      is.setTableQualifier(this.TableQualifier);
      is.setTableKeyword(this.TableKeyword);
      is.setTableOrFileName(this.TableOrFileName);
      if (this.FileQualifier != null) {
         throw new ConvertException("Conversion failure..Incorrect syntax near the keyword " + this.TableOrFileName);
      } else {
         return is;
      }
   }

   public IntoStatement toMySQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      IntoStatement is = new IntoStatement();
      if (this.TableKeyword == null && this.FileQualifier != null) {
         if (this.FileQualifier == null || !this.FileQualifier.trim().equalsIgnoreCase("OUTFILE")) {
            throw new ConvertException("Conversion failure..Incorrect syntax near the keyword " + this.TableOrFileName);
         }

         is.setIntoClause("INTO");
         is.setFileQualifier(this.getFileQualifier());
         is.setTableOrFileName(this.getTableOrFileName());
         if (this.fieldsTerminatedByString != null) {
            is.setFieldsTerminated(this.fieldsTerminatedByString);
         }

         if (this.linesTerminated != null) {
            is.setLinesTerminated(this.linesTerminated);
         }

         if (this.optionallyEnclosed != null) {
            is.setOptionallyEnclosed(this.optionallyEnclosed);
         }
      } else {
         to_sqs.setCreateStatement("CREATE TABLE " + this.TableOrFileName + " AS");
         is = null;
      }

      return is;
   }

   public IntoStatement toOracleSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      IntoStatement is = new IntoStatement();
      String origTableName = null;
      if (this.TableOrFileName != null) {
         origTableName = this.TableOrFileName;
         this.TableOrFileName = CustomizeUtil.objectNamesToQuotedIdentifier(this.TableOrFileName, SwisSQLUtils.getKeywords(1), (ModifiedObjectAttr)null, 1);
      }

      if (SwisSQLOptions.PLSQL) {
         is.setIntoClause(this.IntoClause);
         is.setTableOrFileName(this.TableOrFileName);
      } else {
         if (this.TableKeyword != null | this.FileQualifier == null) {
            if (from_sqs.getSequenceForIdentityFn() != null) {
               CreateSequenceStatement createSeq = from_sqs.getSequenceForIdentityFn();
               createSeq.setSequence("CREATE SEQUENCE ");
               TableObject tobj = new TableObject();
               tobj.setTableName(this.TableOrFileName + "_SEQ");
               createSeq.setSchemaName(tobj);
               to_sqs.setSequenceForIdentityFn(createSeq);
            } else {
               String temp = this.TableOrFileName;
               if (this.context != null && origTableName != null) {
                  temp = (String)this.context.getEquivalent(origTableName);
                  if (temp == null || temp != null && temp.equals(origTableName)) {
                     temp = this.TableOrFileName;
                  }
               }

               to_sqs.setCreateStatement("CREATE GLOBAL TEMPORARY TABLE " + temp + " AS");
            }
         } else if (this.FileQualifier != null) {
            throw new ConvertException("Conversion failure..Incorrect syntax near the keyword " + this.TableOrFileName);
         }

         is = null;
      }

      return is;
   }

   public IntoStatement toDB2Select(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      new IntoStatement();
      if (this.TableKeyword == null && this.FileQualifier != null) {
         if (this.FileQualifier != null) {
            throw new ConvertException("Conversion failure..Incorrect syntax near the keyword " + this.TableOrFileName);
         }
      } else {
         to_sqs.setCreateStatement("CREATE TABLE " + this.TableOrFileName + " AS(");
         to_sqs.setDefinitionOnly(") DEFINITION ONLY;");
         InsertQueryStatement iqs = new InsertQueryStatement();
         InsertClause ic = new InsertClause();
         OptionalSpecifier optionalSp = new OptionalSpecifier();
         TableClause tc = new TableClause();
         TableExpression tableExp = new TableExpression();
         ic.setInsert("INSERT");
         optionalSp.setInto("INTO");
         TableObject tableObj = new TableObject();
         tableObj.setTableName(this.TableOrFileName);
         ArrayList tableExpList = new ArrayList();
         tc.setTableObject(tableObj);
         tableExpList.add(tc);
         tableExp.setTableClauseList(tableExpList);
         ic.setOptionalSpecifier(optionalSp);
         ic.setTableExpression(tableExp);
         iqs.setInsertClause(ic);
         SelectQueryStatement sqs = new SelectQueryStatement();
         sqs.setIntoStatement((IntoStatement)null);
         new SelectStatement();
         SelectStatement ss = from_sqs.getSelectStatement();
         ss.setOpenBraceForSelectInInsertQuery("(");
         sqs.setSelectStatement(ss);
         sqs.setFromClause(from_sqs.getFromClause());
         sqs.setGroupByStatement(from_sqs.getGroupByStatement());
         sqs.setHavingStatement(from_sqs.getHavingStatement());
         sqs.setOrderByStatement(from_sqs.getOrderByStatement());
         sqs.setLimitClause(from_sqs.getLimitClause());
         sqs.setSetOperatorClause(from_sqs.getSetOperatorClause());
         sqs.setWhereExpression(from_sqs.getWhereExpression());
         iqs.setSubQuery(sqs.toDB2());
         to_sqs.setInsertQueryStatement(iqs);
      }

      IntoStatement is = null;
      return (IntoStatement)is;
   }

   public IntoStatement toMSSQLServerSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      IntoStatement is = new IntoStatement();
      is.setCommentClass(this.commentObj);
      if (this.FileQualifier != null) {
         throw new ConvertException("Conversion failure..Incorrect syntax near the keyword " + this.TableOrFileName);
      } else {
         is.setIntoClause(this.IntoClause);
         if (this.TableOrFileName != null && this.TableOrFileName.startsWith(":")) {
            is.setTableOrFileName("@" + this.TableOrFileName.substring(1));
         } else {
            is.setTableOrFileName(this.TableOrFileName);
         }

         if (this.vArrayList != null && this.vArrayList.size() > 0) {
            ArrayList newVarray = new ArrayList();

            for(int i = 0; i < this.vArrayList.size(); ++i) {
               newVarray.add("@" + this.vArrayList.get(i).toString().substring(1));
            }

            is.setVarray(newVarray);
         }

         return is;
      }
   }

   public IntoStatement toSybaseSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      IntoStatement is = new IntoStatement();
      if (this.FileQualifier != null) {
         throw new ConvertException("Conversion failure..Incorrect syntax near the keyword " + this.TableOrFileName);
      } else {
         is.setIntoClause(this.IntoClause);
         if (this.TableOrFileName != null && this.TableOrFileName.startsWith(":")) {
            is.setTableOrFileName("@" + this.TableOrFileName.substring(1));
         } else {
            is.setTableOrFileName(this.TableOrFileName);
         }

         if (this.vArrayList != null && this.vArrayList.size() > 0) {
            ArrayList newVarray = new ArrayList();

            for(int i = 0; i < this.vArrayList.size(); ++i) {
               newVarray.add("@" + this.vArrayList.get(i).toString().substring(1));
            }

            is.setVarray(newVarray);
         }

         return is;
      }
   }

   public IntoStatement toTimesTenSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      new IntoStatement();
      if (this.TableKeyword == null && this.FileQualifier != null) {
         if (this.FileQualifier != null) {
            throw new ConvertException("Conversion failure..Incorrect syntax near the keyword " + this.TableOrFileName);
         }
      } else {
         CreateQueryStatement cqs = SwisSQLUtils.constructCQS(this.TableOrFileName, from_sqs, to_sqs);
         to_sqs.setCreateStatement(cqs.toTimesTenString());
         to_sqs.setSelectStatement((SelectStatement)null);
         to_sqs.setFromClause((FromClause)null);
         to_sqs.setGroupByStatement((GroupByStatement)null);
         to_sqs.setHavingStatement((HavingStatement)null);
         to_sqs.setOrderByStatement((OrderByStatement)null);
         to_sqs.setLimitClause((LimitClause)null);
         to_sqs.setSetOperatorClause((SetOperatorClause)null);
         to_sqs.setWhereExpression((WhereExpression)null);
         InsertQueryStatement iqs = new InsertQueryStatement();
         InsertClause ic = new InsertClause();
         OptionalSpecifier optionalSp = new OptionalSpecifier();
         TableClause tc = new TableClause();
         TableExpression tableExp = new TableExpression();
         ic.setInsert("INSERT");
         optionalSp.setInto("INTO");
         TableObject tableObj = new TableObject();
         tableObj.setTableName(this.TableOrFileName);
         ArrayList tableExpList = new ArrayList();
         tc.setTableObject(tableObj);
         tableExpList.add(tc);
         tableExp.setTableClauseList(tableExpList);
         ic.setOptionalSpecifier(optionalSp);
         ic.setTableExpression(tableExp);
         iqs.setInsertClause(ic);
         SelectQueryStatement sqs = new SelectQueryStatement();
         sqs.setIntoStatement((IntoStatement)null);
         new SelectStatement();
         SelectStatement ss = from_sqs.getSelectStatement();
         Vector sourceSItems = ss.getSelectItemList();
         boolean isAliasExists = false;

         for(int k = 0; k < sourceSItems.size(); ++k) {
            Object sourceObj = sourceSItems.get(k);
            if (sourceObj instanceof SelectColumn && ((SelectColumn)sourceObj).getAliasName() != null) {
               isAliasExists = true;
               break;
            }
         }

         if (!isAliasExists) {
            Vector newSelItems = new Vector();
            Vector colNames = cqs.getColumnNames();

            for(int i = 0; i < colNames.size(); ++i) {
               TableColumn tCol = new TableColumn();
               tCol.setColumnName(((CreateColumn)colNames.get(i)).getColumnName());
               SelectColumn sCol = new SelectColumn();
               Vector colExpr = new Vector();
               colExpr.add(tCol);
               sCol.setColumnExpression(colExpr);
               if (i != colNames.size() - 1) {
                  sCol.setEndsWith(",");
               }

               newSelItems.add(sCol);
            }

            ss.setSelectItemList(newSelItems);
         }

         ss.setOpenBraceForSelectInInsertQuery("(");
         sqs.setSelectStatement(ss);
         sqs.setFromClause(from_sqs.getFromClause());
         sqs.setGroupByStatement(from_sqs.getGroupByStatement());
         sqs.setHavingStatement(from_sqs.getHavingStatement());
         sqs.setOrderByStatement(from_sqs.getOrderByStatement());
         sqs.setLimitClause(from_sqs.getLimitClause());
         sqs.setSetOperatorClause(from_sqs.getSetOperatorClause());
         sqs.setWhereExpression(from_sqs.getWhereExpression());
         iqs.setSubQuery(sqs.toTimesTen());
         to_sqs.setInsertQueryStatement(iqs);
      }

      IntoStatement is = null;
      return (IntoStatement)is;
   }

   public IntoStatement toNetezzaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      IntoStatement is = new IntoStatement();
      is.setIntoClause(this.IntoClause);
      is.setTableQualifier(this.TableQualifier);
      is.setTableKeyword(this.TableKeyword);
      is.setTableOrFileName(this.TableOrFileName);
      if (this.FileQualifier != null) {
         throw new ConvertException("Conversion failure..Incorrect syntax near the keyword " + this.TableOrFileName);
      } else {
         return is;
      }
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.commentObj != null) {
         sb.append(this.commentObj.toString());
         sb.append("\n");
      }

      sb.append(this.IntoClause.toUpperCase());
      if (this.TableQualifier != null) {
         sb.append(" " + this.TableQualifier.toUpperCase());
      } else if (this.FileQualifier != null) {
         sb.append(" " + this.FileQualifier.toUpperCase());
      }

      if (this.TableKeyword != null) {
         sb.append(" " + this.TableKeyword.toUpperCase());
      }

      if (this.vArrayList.size() == 0) {
         if (this.context != null) {
            sb.append(" " + this.context.getEquivalent(this.TableOrFileName));
         } else {
            sb.append(" " + this.TableOrFileName);
         }
      }

      if (this.fieldsTerminatedByString != null) {
         sb.append(" " + this.fieldsTerminatedByString);
      }

      if (this.optionallyEnclosed != null) {
         sb.append(" " + this.optionallyEnclosed);
      }

      if (this.linesTerminated != null) {
         sb.append(" " + this.linesTerminated + " ");
      } else {
         for(int i = 0; i < this.vArrayList.size(); ++i) {
            if (i != this.vArrayList.size() - 1) {
               sb.append(" " + this.vArrayList.get(i).toString() + ",");
            } else {
               sb.append(" " + this.vArrayList.get(i).toString());
            }
         }
      }

      return sb.toString();
   }

   public IntoStatement toVectorWiseSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      IntoStatement is = new IntoStatement();
      if (this.TableKeyword == null && this.FileQualifier != null) {
         if (this.FileQualifier == null || !this.FileQualifier.trim().equalsIgnoreCase("OUTFILE")) {
            throw new ConvertException("Conversion failure..Incorrect syntax near the keyword " + this.TableOrFileName);
         }

         is.setIntoClause("INTO");
         is.setFileQualifier(this.getFileQualifier());
         is.setTableOrFileName(this.getTableOrFileName());
         if (this.fieldsTerminatedByString != null) {
            is.setFieldsTerminated(this.fieldsTerminatedByString);
         }

         if (this.linesTerminated != null) {
            is.setLinesTerminated(this.linesTerminated);
         }

         if (this.optionallyEnclosed != null) {
            is.setOptionallyEnclosed(this.optionallyEnclosed);
         }
      } else {
         to_sqs.setCreateStatement("CREATE TABLE " + this.TableOrFileName + " AS");
         is = null;
      }

      return is;
   }

   public IntoStatement toReplaceTblCol(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      return this;
   }
}
