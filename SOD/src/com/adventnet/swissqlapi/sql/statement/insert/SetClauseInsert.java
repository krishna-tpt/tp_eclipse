package com.adventnet.swissqlapi.sql.statement.insert;

import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import java.util.ArrayList;
import java.util.Vector;

public class SetClauseInsert {
   private String set = null;
   private ArrayList setList = null;
   private UserObjectContext context = null;

   public void setSet(String s) {
      this.set = s;
   }

   public void setSetList(ArrayList v) {
      this.setList = v;
   }

   public void setObjectContext(UserObjectContext obj) {
      this.context = obj;
   }

   public String getSet() {
      return this.set;
   }

   public ArrayList getSetList() {
      return this.setList;
   }

   public void toGeneric(InsertQueryStatement q) {
      ValuesClause valuesClause = new ValuesClause();
      valuesClause.setValues("VALUES");
      ArrayList columnList = new ArrayList();
      ArrayList valuesList = new ArrayList();
      int size = this.setList.size();
      columnList.add("(");

      Vector v_nce;
      for(int i = 0; i < size; ++i) {
         SelectColumn sc = new SelectColumn();
         Vector v_nce = new Vector();
         if (!(this.setList.get(i) instanceof String) || !((String)this.setList.get(i)).equals(",")) {
            if (!(this.setList.get(i) instanceof SelectColumn)) {
               columnList.add(this.setList.get(i));
               columnList.add(",");
            } else {
               ((SelectColumn)this.setList.get(i)).setObjectContext(this.context);
               v_nce = ((SelectColumn)this.setList.get(i)).getColumnExpression();

               for(int j = 0; j < v_nce.size() && (!(v_nce.elementAt(j) instanceof String) || !((String)v_nce.elementAt(j)).equals("=")); ++j) {
                  v_nce.addElement(v_nce.elementAt(j));
               }

               sc.setColumnExpression(v_nce);
               columnList.add(sc);
               columnList.add(",");
            }
         }
      }

      columnList.set(columnList.lastIndexOf(","), ")");
      InsertClause insertClause = q.getInsertClause();
      insertClause.setColumnList(columnList);
      valuesList.add("(");

      for(int i = 0; i < size; ++i) {
         SelectColumn sc = new SelectColumn();
         v_nce = new Vector();
         if (!(this.setList.get(i) instanceof String) || !((String)this.setList.get(i)).equals(",")) {
            if (!(this.setList.get(i) instanceof SelectColumn)) {
               valuesList.add(this.setList.get(i));
               valuesList.add(",");
            } else {
               Vector v_ce = ((SelectColumn)this.setList.get(i)).getColumnExpression();
               boolean b_after_equals = false;

               for(int j = 0; j < v_ce.size(); ++j) {
                  if (b_after_equals) {
                     v_nce.addElement(v_ce.elementAt(j));
                  }

                  if (v_ce.elementAt(j) instanceof String && ((String)v_ce.elementAt(j)).equals("=")) {
                     b_after_equals = true;
                  }
               }

               sc.setColumnExpression(v_nce);
               valuesList.add(sc);
               valuesList.add(",");
            }
         }
      }

      valuesList.set(valuesList.lastIndexOf(","), ")");
      valuesClause.setValuesList(valuesList);
      q.setValuesClause(valuesClause);
      q.setInsertClause(insertClause);
      q.setInsertClause(insertClause);
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.set != null) {
         sb.append(this.set.toUpperCase() + " ");
      }

      if (this.setList != null) {
         int size = this.setList.size();

         for(int i = 0; i < size; ++i) {
            if (this.setList.get(i) instanceof String) {
               if (this.context != null) {
                  String temp = this.context.getEquivalent(this.setList.get(i)).toString();
                  sb.append(temp + " ");
               } else {
                  sb.append(this.setList.get(i) + " ");
               }
            } else {
               sb.append(this.setList.get(i) + " ");
            }
         }
      }

      return sb.toString();
   }
}
