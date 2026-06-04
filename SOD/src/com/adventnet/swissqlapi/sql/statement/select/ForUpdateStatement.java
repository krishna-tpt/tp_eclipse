package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.sql.statement.CommentClass;
import java.util.Vector;

public class ForUpdateStatement {
   public String ForUpdateClause = new String();
   public String ForUpdateQualifier = new String();
   public Vector ForUpdateTableName = new Vector();
   public String NoWaitQualifier = new String();
   private CommentClass commentObj;

   public void setForUpdateClause(String s_fuc) {
      this.ForUpdateClause = s_fuc;
   }

   public void setForUpdateQualifier(String s_fuq) {
      this.ForUpdateQualifier = s_fuq;
   }

   public void setForUpdateTableName(Vector v_futn) {
      this.ForUpdateTableName = v_futn;
   }

   public void setNoWaitQualifier(String s_nwq) {
      this.NoWaitQualifier = s_nwq;
   }

   public void setCommentClass(CommentClass commentObj) {
      this.commentObj = commentObj;
   }

   public CommentClass getCommentClass() {
      return this.commentObj;
   }

   public String getNoWaitQualifier() {
      return this.NoWaitQualifier;
   }

   public String getForUpdateQualifier() {
      return this.ForUpdateQualifier;
   }

   public Vector getForUpdateTableName() {
      return this.ForUpdateTableName;
   }

   public String getForUpdateClause() {
      return this.ForUpdateClause;
   }

   public ForUpdateStatement toMSSQLServerSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) {
      ForUpdateStatement fus = new ForUpdateStatement();
      if (this.ForUpdateClause != null) {
         fus.setForUpdateClause(this.ForUpdateClause);
      }

      if (this.ForUpdateQualifier != null) {
         fus.setForUpdateQualifier(this.ForUpdateQualifier);
      }

      if (this.NoWaitQualifier != null) {
         fus.setNoWaitQualifier(this.NoWaitQualifier);
      }

      Vector v_utn = new Vector();

      for(int i = 0; i < this.ForUpdateTableName.size(); ++i) {
         v_utn.addElement(((TableColumn)this.ForUpdateTableName.elementAt(i)).toMSSQLServerSelect(to_sqs, from_sqs));
      }

      fus.setForUpdateTableName(v_utn);
      return fus;
   }

   public ForUpdateStatement toSybaseSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) {
      ForUpdateStatement fus = new ForUpdateStatement();
      if (this.ForUpdateClause != null) {
         fus.setForUpdateClause(this.ForUpdateClause);
      }

      if (this.ForUpdateQualifier != null) {
         fus.setForUpdateQualifier(this.ForUpdateQualifier);
      }

      if (this.NoWaitQualifier != null) {
         fus.setNoWaitQualifier(this.NoWaitQualifier);
      }

      Vector v_utn = new Vector();

      for(int i = 0; i < this.ForUpdateTableName.size(); ++i) {
         v_utn.addElement(((TableColumn)this.ForUpdateTableName.elementAt(i)).toSybaseSelect(to_sqs, from_sqs));
      }

      fus.setForUpdateTableName(v_utn);
      return fus;
   }

   public ForUpdateStatement toOracleSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) {
      ForUpdateStatement fus = new ForUpdateStatement();
      fus.setCommentClass(this.commentObj);
      if (this.ForUpdateClause != null) {
         fus.setForUpdateClause(this.ForUpdateClause);
      }

      if (this.ForUpdateQualifier != null) {
         fus.setForUpdateQualifier(this.ForUpdateQualifier);
      }

      if (this.NoWaitQualifier != null) {
         fus.setNoWaitQualifier(this.NoWaitQualifier);
      }

      Vector v_utn = new Vector();

      for(int i = 0; i < this.ForUpdateTableName.size(); ++i) {
         v_utn.addElement(((TableColumn)this.ForUpdateTableName.elementAt(i)).toOracleSelect(to_sqs, from_sqs));
      }

      fus.setForUpdateTableName(v_utn);
      return fus;
   }

   public ForUpdateStatement toInformixSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) {
      ForUpdateStatement fus = new ForUpdateStatement();
      if (this.ForUpdateClause != null) {
         fus.setForUpdateClause(this.ForUpdateClause);
      }

      if (this.ForUpdateQualifier != null) {
         fus.setForUpdateQualifier(this.ForUpdateQualifier);
      }

      if (this.NoWaitQualifier != null) {
         fus.setNoWaitQualifier(this.NoWaitQualifier);
      }

      Vector v_utn = new Vector();

      for(int i = 0; i < this.ForUpdateTableName.size(); ++i) {
         v_utn.addElement(((TableColumn)this.ForUpdateTableName.elementAt(i)).toInformixSelect(to_sqs, from_sqs));
      }

      fus.setForUpdateTableName(v_utn);
      return fus;
   }

   public ForUpdateStatement toDB2Select(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) {
      ForUpdateStatement fus = new ForUpdateStatement();
      if (this.ForUpdateClause != null) {
         fus.setForUpdateClause(this.ForUpdateClause);
      }

      if (this.ForUpdateQualifier != null) {
         fus.setForUpdateQualifier(this.ForUpdateQualifier);
      }

      if (this.NoWaitQualifier != null) {
         fus.setNoWaitQualifier(this.NoWaitQualifier);
      }

      Vector v_utn = new Vector();

      for(int i = 0; i < this.ForUpdateTableName.size(); ++i) {
         v_utn.addElement(((TableColumn)this.ForUpdateTableName.elementAt(i)).toDB2Select(to_sqs, from_sqs));
      }

      fus.setForUpdateTableName(v_utn);
      return fus;
   }

   public ForUpdateStatement toTimesTenSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) {
      ForUpdateStatement fus = new ForUpdateStatement();
      if (this.ForUpdateClause != null) {
         fus.setForUpdateClause(this.ForUpdateClause);
      }

      if (this.ForUpdateQualifier != null) {
         fus.setForUpdateQualifier(this.ForUpdateQualifier);
      }

      if (this.NoWaitQualifier != null) {
         fus.setNoWaitQualifier(this.NoWaitQualifier);
      }

      Vector v_utn = new Vector();

      for(int i = 0; i < this.ForUpdateTableName.size(); ++i) {
         v_utn.addElement(((TableColumn)this.ForUpdateTableName.elementAt(i)).toTimesTenSelect(to_sqs, from_sqs));
      }

      fus.setForUpdateTableName(v_utn);
      return fus;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.commentObj != null) {
         sb.append(this.commentObj.toString().trim() + " ");
      }

      if (this.ForUpdateClause != null) {
         sb.append(this.ForUpdateClause.toUpperCase());
      }

      if (this.ForUpdateQualifier != null) {
         sb.append(" " + this.ForUpdateQualifier.toUpperCase() + " ");
      }

      SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() + 1);

      for(int i = 0; i < this.ForUpdateTableName.size(); ++i) {
         if (i == this.ForUpdateTableName.size() - 1) {
            sb.append(this.ForUpdateTableName.elementAt(i).toString());
         } else {
            sb.append(this.ForUpdateTableName.elementAt(i).toString() + ",");
            sb.append("\n");

            for(int j = 0; j < SelectQueryStatement.getBeautyTabCount(); ++j) {
               sb.append("\t");
            }
         }
      }

      SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() - 1);
      if (this.NoWaitQualifier != null) {
         sb.append(" " + this.NoWaitQualifier.toUpperCase());
      }

      return sb.toString();
   }
}
