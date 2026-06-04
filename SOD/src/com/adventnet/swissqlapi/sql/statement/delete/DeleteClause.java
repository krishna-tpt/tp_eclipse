package com.adventnet.swissqlapi.sql.statement.delete;

import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.update.HintClause;
import com.adventnet.swissqlapi.sql.statement.update.OptionalSpecifier;

public class DeleteClause {
   private String delete_ = new String();
   private OptionalSpecifier optionalSpecifier = null;
   private HintClause hint = new HintClause();
   private CommentClass commentObj;

   public void setDelete(String s) {
      this.delete_ = s;
   }

   public String getDelete() {
      return this.delete_;
   }

   public void setCommentClass(CommentClass commentObj) {
      this.commentObj = commentObj;
   }

   public CommentClass getCommentClass() {
      return this.commentObj;
   }

   public void setOptionalSpecifier(OptionalSpecifier s) {
      this.optionalSpecifier = s;
   }

   public OptionalSpecifier getOptionalSpecifier() {
      return this.optionalSpecifier;
   }

   public void setHintClause(HintClause hintclause) {
      this.hint = hintclause;
   }

   public HintClause getHintClause() {
      return this.hint;
   }

   public String toString() {
      StringBuffer stringbuffer = new StringBuffer();
      stringbuffer.append(this.delete_.toUpperCase() + " ");
      if (this.commentObj != null) {
         stringbuffer.append(this.commentObj.toString().trim() + " ");
      }

      if (this.optionalSpecifier != null) {
         stringbuffer.append(this.optionalSpecifier.toString() + " ");
      }

      if (this.hint != null && this.hint.toString() != null) {
         stringbuffer.append(this.hint.toString() + " ");
      }

      return stringbuffer.toString();
   }

   private void toGeneric() {
      this.optionalSpecifier = null;
   }

   public void toDB2() {
      this.setCommentClass((CommentClass)null);
      this.optionalSpecifier = new OptionalSpecifier();
      this.optionalSpecifier.setFrom("FROM");
   }

   public void toOracle() {
      if (this.optionalSpecifier != null) {
         if (this.optionalSpecifier.getFrom() != null) {
            this.optionalSpecifier = new OptionalSpecifier();
            this.optionalSpecifier.setFrom("FROM");
         } else {
            this.optionalSpecifier = null;
         }
      }

   }

   public void toANSISQL() {
      this.setCommentClass((CommentClass)null);
      if (this.optionalSpecifier != null) {
         if (this.optionalSpecifier.getFrom() != null) {
            this.optionalSpecifier = new OptionalSpecifier();
            this.optionalSpecifier.setFrom("FROM");
         } else {
            this.optionalSpecifier = null;
         }
      }

      this.hint = null;
   }

   public void toSQLServer() {
      this.setCommentClass((CommentClass)null);
      if (this.optionalSpecifier != null) {
         if (this.optionalSpecifier.getFrom() != null) {
            this.optionalSpecifier = new OptionalSpecifier();
            this.optionalSpecifier.setFrom("FROM");
         } else {
            this.optionalSpecifier = null;
         }
      }

   }

   public void toSybase() {
      this.setCommentClass((CommentClass)null);
      if (this.optionalSpecifier != null) {
         if (this.optionalSpecifier.getFrom() != null) {
            this.optionalSpecifier = new OptionalSpecifier();
            this.optionalSpecifier.setFrom("FROM");
         } else {
            this.optionalSpecifier = null;
         }
      }

   }

   public void toMySQL() {
      this.setCommentClass((CommentClass)null);
      if (this.optionalSpecifier != null) {
         this.optionalSpecifier.toMySQL();
      }

      this.optionalSpecifier.setFrom("FROM");
      this.hint = null;
   }

   public void toPostgreSQL() {
      this.setCommentClass((CommentClass)null);
      if (this.optionalSpecifier != null) {
         this.optionalSpecifier.toPostgreSQL();
         if (this.optionalSpecifier.getFrom() == null) {
            this.optionalSpecifier.setFrom("FROM");
         }
      } else {
         this.optionalSpecifier = new OptionalSpecifier();
         this.optionalSpecifier.setFrom("FROM");
      }

      this.hint = null;
   }

   public void toBigQuery() {
      this.setCommentClass((CommentClass)null);
      if (this.optionalSpecifier != null) {
         this.optionalSpecifier.toBigQuery();
         if (this.optionalSpecifier.getFrom() == null) {
            this.optionalSpecifier.setFrom("FROM");
         }
      } else {
         this.optionalSpecifier = new OptionalSpecifier();
         this.optionalSpecifier.setFrom("FROM");
      }

      this.hint = null;
   }

   public void toInformix() {
      this.setCommentClass((CommentClass)null);
      this.optionalSpecifier = new OptionalSpecifier();
      this.optionalSpecifier.setFrom("FROM");
   }

   public void toTimesTen() {
      this.setCommentClass((CommentClass)null);
      this.optionalSpecifier = new OptionalSpecifier();
      this.optionalSpecifier.setFrom("FROM");
   }

   public void toNetezza() {
      this.setCommentClass((CommentClass)null);
      if (this.optionalSpecifier != null) {
         if (this.optionalSpecifier.getFrom() != null) {
            this.optionalSpecifier = new OptionalSpecifier();
            this.optionalSpecifier.setFrom("FROM");
         } else {
            this.optionalSpecifier = null;
         }
      }

      this.hint = null;
   }

   public void toSnowflake() {
      this.setCommentClass((CommentClass)null);
      if (this.optionalSpecifier != null) {
         this.optionalSpecifier.toSnowflake();
         if (this.optionalSpecifier.getFrom() == null) {
            this.optionalSpecifier.setFrom("FROM");
         }
      } else {
         this.optionalSpecifier = new OptionalSpecifier();
         this.optionalSpecifier.setFrom("FROM");
      }

      this.hint = null;
   }

   public void toTeradata() {
      this.setCommentClass((CommentClass)null);
      if (this.optionalSpecifier != null) {
         if (this.optionalSpecifier.getFrom() != null) {
            this.optionalSpecifier = new OptionalSpecifier();
            this.optionalSpecifier.setFrom("FROM");
         } else {
            this.optionalSpecifier = null;
         }
      }

      this.hint = null;
   }
}
