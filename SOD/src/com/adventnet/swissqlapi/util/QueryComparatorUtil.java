package com.adventnet.swissqlapi.util;

import com.adventnet.swissqlapi.sql.statement.select.FromClause;
import com.adventnet.swissqlapi.sql.statement.select.FromTable;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.SetOperatorClause;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

public class QueryComparatorUtil {
   public static boolean isUnionPresent(SelectQueryStatement sqs) {
      return sqs.getSetOperatorClause() != null;
   }

   public static boolean isJoinClausePresent(SelectQueryStatement sqs) {
      FromClause fc = sqs.getFromClause();
      if (fc != null) {
         Vector fromItemList = fc.getFromItemList();
         return fromItemList.size() > 1;
      } else {
         return false;
      }
   }

   public static boolean isSubQueryUsedInSelQueryFromClause(SelectQueryStatement sqs) {
      return isSubQueryUsedInFromClause(sqs.getFromClause());
   }

   public static boolean isSubQueryUsedInFromClause(FromClause fc) {
      if (fc != null) {
         Vector fromItemList = fc.getFromItemList();

         for(int i = 0; i < fromItemList.size(); ++i) {
            if (fromItemList.get(i) instanceof FromTable) {
               FromTable ft = (FromTable)fromItemList.get(i);
               if (ft.getTableName() instanceof SelectQueryStatement) {
                  return true;
               }

               if (ft.getTableName() instanceof FromClause) {
                  boolean result = isSubQueryUsedInFromClause((FromClause)ft.getTableName());
                  if (result) {
                     return true;
                  }
               }
            } else if (fromItemList.get(i) instanceof FromClause) {
               boolean result = isSubQueryUsedInFromClause((FromClause)fromItemList.get(i));
               if (result) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public static boolean isSubQueryUsedInWhereClause(SelectQueryStatement sqs) {
      WhereExpression we = sqs.getWhereExpression();
      return we != null ? checkSubQueryInWhereExpression(we) : false;
   }

   public static boolean isGroupByPresent(SelectQueryStatement sqs) {
      return sqs.getGroupByStatement() != null;
   }

   public static boolean checkSubQueryInWhereExpression(WhereExpression we) {
      Vector whereItemList = we.getWhereItems();
      if (whereItemList.size() > 0) {
         for(int i = 0; i < whereItemList.size(); ++i) {
            if (whereItemList.get(i) instanceof WhereItem) {
               boolean isSubqueryPresent = checkForSubQuery((WhereItem)whereItemList.get(i));
               if (isSubqueryPresent) {
                  return true;
               }
            } else if (whereItemList.get(i) instanceof WhereExpression) {
               return checkSubQueryInWhereExpression((WhereExpression)whereItemList.get(i));
            }
         }
      }

      return false;
   }

   private static boolean checkForSubQuery(WhereItem whereItem) {
      return whereItem.getRightWhereSubQuery() != null;
   }

   public static LightWeightFromTable generateLightWeightFromTableFromQuery(SelectQueryStatement sqs) {
      LightWeightFromTable lwft = new LightWeightFromTable();
      FromClause fc = sqs.getFromClause();
      Vector fromItemList = fc.getFromItemList();
      Iterator var4 = fromItemList.iterator();

      while(var4.hasNext()) {
         Object fromItem = var4.next();
         FromTable ft = (FromTable)fromItem;
         generateLightWeightFromTable(ft, lwft);
      }

      return lwft;
   }

   private static void generateLightWeightFromTable(FromTable ft, LightWeightFromTable lwft) {
      Vector involvedTables;
      if (lwft.getInvlovedTables() == null) {
         involvedTables = new Vector();
         lwft.setInvlovedTables(involvedTables);
      }

      involvedTables = lwft.getInvlovedTables();
      lwft.setInvlovedTables(involvedTables);
      if (!isJoinClausePresent(ft)) {
         involvedTables.add(ft.getTableName());
      } else {
         String joinType = ft.getJoinClause().trim();
         if (!joinType.equalsIgnoreCase("inner join") && !joinType.equalsIgnoreCase("join")) {
            LightWeightFromTable associatedTableLwft;
            if (!joinType.equalsIgnoreCase("left join") && !joinType.equalsIgnoreCase("left outer join")) {
               if (joinType.equalsIgnoreCase("right join") || joinType.equalsIgnoreCase("right outer join")) {
                  ft.setJoinClause("JOIN");
                  associatedTableLwft = new LightWeightFromTable();
                  generateLightWeightFromTable(ft, associatedTableLwft);
                  LightWeightFromTable lwftClone = lwft.clone();
                  associatedTableLwft.setAssociatedTable(lwftClone);
                  associatedTableLwft.setAssociatedTableRelation(associatedTableLwft.getRelation());
                  associatedTableLwft.setRelation((Vector)null);
                  switchLightWeightFromTable(lwft, associatedTableLwft);
               }
            } else {
               ft.setJoinClause("JOIN");
               associatedTableLwft = new LightWeightFromTable();
               lwft.setAssociatedTableRelation(generateRelation(ft.getJoinExpression()));
               generateLightWeightFromTable(ft, associatedTableLwft);
               lwft.setAssociatedTable(associatedTableLwft);
            }
         } else {
            involvedTables.add(ft.getTableName());
            lwft.setRelation(generateRelation(ft.getJoinExpression()));
         }
      }

   }

   private static void switchLightWeightFromTable(LightWeightFromTable lwft, LightWeightFromTable associatedTableLwft) {
      lwft.clear();
      lwft.setAssociatedTable(associatedTableLwft.getAssociatedTable());
      lwft.setInvlovedTables(associatedTableLwft.getInvlovedTables());
      lwft.setRelation(associatedTableLwft.getRelation());
      lwft.setAssociatedTableRelation(associatedTableLwft.getAssociatedTableRelation());
   }

   private static Vector generateRelation(Vector joinExpression) {
      Vector result = null;
      if (joinExpression.size() == 0) {
         return null;
      } else {
         Iterator var2 = joinExpression.iterator();

         while(var2.hasNext()) {
            Object where = var2.next();
            if (where instanceof WhereExpression) {
               result = generateConditionForWhereExpression((WhereExpression)where);
            }
         }

         joinExpression.clear();
         return result;
      }
   }

   private static Vector generateConditionForWhereExpression(WhereExpression we) {
      Vector result = new Vector();
      Vector whereItems = we.getWhereItems();
      Vector operators = we.getOperator();
      boolean isRelationalOperatorPresent = whereItems.size() == we.getOperator().size() + 1;
      int i = 0;

      for(Iterator var6 = whereItems.iterator(); var6.hasNext(); ++i) {
         Object whereItem = var6.next();
         if (whereItem instanceof WhereItem) {
            Condition cond = new Condition();
            cond.setOperator(((WhereItem)whereItem).getOperator());
            Vector expressions = new Vector();
            if (((WhereItem)whereItem).getLeftWhereExp() != null && ((WhereItem)whereItem).getLeftWhereExp() instanceof WhereColumn) {
               expressions.addAll(getColumnsUsed(((WhereItem)whereItem).getLeftWhereExp()));
            }

            if (((WhereItem)whereItem).getRightWhereExp() != null && ((WhereItem)whereItem).getRightWhereExp() instanceof WhereColumn) {
               expressions.addAll(getColumnsUsed(((WhereItem)whereItem).getRightWhereExp()));
            }

            cond.setExpressions(expressions);
            result.add(cond);
         } else if (whereItem instanceof WhereExpression && ((WhereExpression)whereItem).getOperator() != null) {
            result.add(generateConditionForWhereExpression((WhereExpression)whereItem));
         }

         if (i < operators.size()) {
            result.add(operators.get(i));
         }
      }

      return result;
   }

   public static boolean isJoinClausePresent(FromTable ft) {
      return ft.getJoinClause() != null;
   }

   private static Vector getColumnsUsed(WhereColumn wc) {
      Vector columnUsed = new Vector();
      Vector colExp = wc.getColumnExpression();
      if (colExp.size() == 1) {
         if (colExp.get(0) instanceof TableColumn) {
            TableColumn tc = (TableColumn)colExp.get(0);
            columnUsed.add(tc.getColumnName());
         } else if (colExp.get(0) instanceof String) {
            columnUsed.add(colExp.get(0));
         }
      }

      return columnUsed;
   }

   public static boolean isBothLightWeightFromTableSimilar(LightWeightFromTable lwft1, LightWeightFromTable lwft2) {
      if (isEqual(lwft1.getInvlovedTables(), lwft2.getInvlovedTables(), false) && isEqual(lwft1.getRelation(), lwft2.getRelation(), true) && isEqual(lwft1.getAssociatedTableRelation(), lwft2.getAssociatedTableRelation(), true)) {
         LightWeightFromTable lwft1AssociatedTable = lwft1.getAssociatedTable();
         LightWeightFromTable lwft2AssociatedTable = lwft2.getAssociatedTable();
         return lwft1AssociatedTable == null && lwft2AssociatedTable == null ? true : isBothLightWeightFromTableSimilar(lwft1AssociatedTable, lwft2AssociatedTable);
      } else {
         return false;
      }
   }

   private static boolean isEqual(Object obj1, Object obj2, boolean isRelationObj) {
      if (obj1 == null && obj2 == null) {
         return true;
      } else if (obj1 != null && obj2 == null || obj1 == null && obj2 != null) {
         return false;
      } else if (isRelationObj) {
         return compareConditionObjects((Vector)obj1, (Vector)obj2);
      } else if (!isRelationObj && obj1 != null && obj2 != null && obj1 instanceof Vector && obj2 instanceof Vector && ((Vector)obj1).size() == ((Vector)obj2).size()) {
         boolean result = true;

         for(int i = 0; i < ((Vector)obj1).size(); ++i) {
            if (!result) {
               return false;
            }

            result = result && ((Vector)obj2).contains(((Vector)obj1).get(i));
         }

         return true;
      } else {
         return false;
      }
   }

   private static boolean compareConditionObjects(Vector obj1, Vector obj2) {
      if (obj1.size() == 0 && obj2.size() == 0) {
         return true;
      } else {
         Vector vec1 = convertToLiner(obj1, new Vector());
         Vector vec2 = convertToLiner(obj2, new Vector());
         if (vec1 == null && vec2 != null || vec2 == null && vec1 != null) {
            return false;
         } else {
            return vec1 != null && vec2 != null ? compareLinerVectors(vec1, vec2) : compareNonLinerVectors(obj1, obj2);
         }
      }
   }

   private static boolean compareNonLinerVectors(Vector obj1, Vector obj2) {
      Vector resultVector1 = new Vector();
      Vector resultVector2 = new Vector();
      convertNonLinerVectorToLiner(obj1, resultVector1);
      convertNonLinerVectorToLiner(obj2, resultVector2);
      return compareLinerVectors(resultVector1, resultVector2);
   }

   private static void convertNonLinerVectorToLiner(Vector vec, Vector result) {
      for(int i = 0; i < vec.size(); ++i) {
         Object obj = vec.get(i);
         if (obj instanceof Condition) {
            result.add(obj);
         } else if (obj instanceof String) {
            result.add(obj);
         } else if (obj instanceof Vector) {
            Vector vec1 = convertToLiner((Vector)obj, new Vector());
            if (vec1 == null) {
               Vector insideVec = new Vector();
               convertNonLinerVectorToLiner((Vector)obj, insideVec);
               result.add(insideVec);
            } else {
               result.addAll(vec1);
            }
         }
      }

   }

   private static boolean compareLinerVectors(Vector vec1, Vector vec2) {
      if (vec1.size() != vec2.size()) {
         return false;
      } else {
         boolean result = true;

         for(int i = 0; i < vec1.size(); ++i) {
            if (vec1.get(i) instanceof String || vec1.get(i) instanceof Condition) {
               result = result && vec2.remove(vec1.get(i));
            }

            if (!result) {
               return false;
            }
         }

         return true;
      }
   }

   private static Vector convertToLiner(Vector vec, Vector linerVector) {
      for(int i = 0; i < vec.size(); ++i) {
         if (vec.get(i) instanceof Vector) {
            Vector result = convertToLiner((Vector)vec.get(i), linerVector);
            if (result == null) {
               return null;
            }
         } else if (vec.get(i) instanceof String) {
            linerVector.add(vec.get(i).toString().toLowerCase().trim());
            if (linerVector.contains("and") && linerVector.contains("or")) {
               return null;
            }
         } else if (vec.get(i) instanceof Condition) {
            linerVector.add(vec.get(i));
         }
      }

      return linerVector;
   }

   public static HashSet<String> getInvolvedTablesFromQuery(SelectQueryStatement sqs) {
      return !isSubQueryUsedInSelQueryFromClause(sqs) ? getInvolvedTablesinFromClause(sqs.getFromClause()) : null;
   }

   private static HashSet<String> getInvolvedTablesinFromClause(FromClause fc) {
      if (fc != null) {
         HashSet<String> tableNames = new HashSet();
         Vector fromItemList = fc.getFromItemList();

         for(int i = 0; i < fromItemList.size(); ++i) {
            if (fromItemList.get(i) instanceof FromTable) {
               FromTable ft = (FromTable)fromItemList.get(i);
               if (ft.getTableName() instanceof String) {
                  tableNames.add((String)ft.getTableName());
               } else if (ft.getTableName() instanceof FromClause) {
                  HashSet<String> involvedTables = getInvolvedTablesinFromClause((FromClause)ft.getTableName());
                  if (involvedTables != null) {
                     tableNames.addAll(involvedTables);
                  }
               }
            } else if (fromItemList.get(i) instanceof FromClause) {
               HashSet<String> involvedTables = getInvolvedTablesinFromClause((FromClause)fromItemList.get(i));
               if (involvedTables != null) {
                  tableNames.addAll(involvedTables);
               }
            }
         }

         return tableNames;
      } else {
         return null;
      }
   }

   public static void splitUnionQuery(SelectQueryStatement sqs, ArrayList<SelectQueryStatement> queries) {
      if (sqs.getSetOperatorClause() == null) {
         queries.add(sqs);
      } else if (sqs.getSetOperatorClause() != null) {
         SelectQueryStatement sqs1 = sqs.getSetOperatorClause().getSelectQueryStatement();
         sqs.setSetOperatorClause((SetOperatorClause)null);
         queries.add(sqs);
         splitUnionQuery(sqs1, queries);
      }

   }
}
