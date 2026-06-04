package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.update.HintClause;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.util.database.MetadataInfoUtil;
import com.adventnet.swissqlapi.util.misc.CastingUtil;
import com.adventnet.swissqlapi.util.misc.GeneralUtil;
import java.util.Vector;

public class GroupByStatement {
   private String GroupClause;
   private boolean ALLOption;
   private Vector GroupByItemList;
   private String GroupingSetClause;
   private String WithOption;
   private boolean CheckGroupByStatement;
   private String openBraces;
   private String closedBraces;
   private UserObjectContext context = null;
   private CommentClass commentObj;
   private CommentClass commentObjAfterToken;
   private HintClause hintClause;
   private String descOption;

   public void setObjectContext(UserObjectContext context) {
      this.context = context;
   }

   public void setGroupClause(String s_gc) {
      this.GroupClause = s_gc;
   }

   public void setGroupingSetClause(String s_gsc) {
      this.GroupingSetClause = s_gsc;
   }

   public void setGroupByItemList(Vector v_gbil) {
      this.GroupByItemList = v_gbil;
   }

   public void setCheckGroupByStatement(boolean b_cgbs) {
      this.CheckGroupByStatement = b_cgbs;
   }

   public void setALLOption(boolean b_ao) {
      this.ALLOption = b_ao;
   }

   public void setWithOption(String s_wo) {
      this.WithOption = s_wo;
   }

   public void setOpenBraces(String openBraces) {
      this.openBraces = openBraces;
   }

   public void setClosedBraces(String closedBraces) {
      this.closedBraces = closedBraces;
   }

   public void setCommentClass(CommentClass commentObj) {
      this.commentObj = commentObj;
   }

   public void setCommentClassAfterToken(CommentClass commentObj) {
      this.commentObjAfterToken = commentObj;
   }

   public void setHintClause(HintClause hintClause) {
      this.hintClause = hintClause;
   }

   public void setDescOption(String desc) {
      this.descOption = desc;
   }

   public CommentClass getCommentClass() {
      return this.commentObj;
   }

   public boolean getCheckGroupByStatement() {
      return this.CheckGroupByStatement;
   }

   public String getGroupClause() {
      return this.GroupClause;
   }

   public Vector getGroupByItemList() {
      return this.GroupByItemList;
   }

   public String getGroupingSetClause() {
      return this.GroupingSetClause;
   }

   public boolean getALLOption() {
      return this.ALLOption;
   }

   public String getWithOption() {
      return this.WithOption;
   }

   public String getOpenBraces() {
      return this.openBraces;
   }

   public String getClosedBraces() {
      return this.closedBraces;
   }

   public HintClause getHintClause() {
      return this.hintClause;
   }

   public String getDescOption() {
      return this.descOption;
   }

   public GroupByStatement toANSISelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      GroupByStatement gbs = new GroupByStatement();
      gbs.setGroupClause(this.GroupClause);
      Vector v_gil = new Vector();
      new Vector();
      SetOperatorClause t_soc;
      SetOperatorClause soc;
      if (this.GroupingSetClause == null) {
         int i_count;
         SelectColumn sc;
         if (this.ALLOption) {
            for(i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
               if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)this.GroupByItemList.elementAt(i_count);
                  v_gil.addElement(sc.toANSISelect(to_sqs, from_sqs));
               }
            }

            gbs.setGroupByItemList(v_gil);
            soc = null;
            if (from_sqs.getSetOperatorClause() != null) {
               soc = from_sqs.getSetOperatorClause();
            }

            if (from_sqs.getWhereExpression() != null) {
               SetOperatorClause n_soc = this.createSetOperatorClause((Vector)null, from_sqs, to_sqs, 8);
               SelectQueryStatement t_sqs = to_sqs;
               to_sqs.setSetOperatorClause(n_soc);

               for(t_soc = to_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
                  t_sqs = t_soc.getSelectQueryStatement();
               }

               if (soc != null) {
                  t_sqs.setSetOperatorClause(soc.toANSISelect(to_sqs, from_sqs));
               }
            } else if (soc != null) {
               to_sqs.setSetOperatorClause(soc.toANSISelect(to_sqs, from_sqs));
            }
         } else {
            for(i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
               if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)this.GroupByItemList.elementAt(i_count);
                  v_gil.addElement(sc.toANSISelect(to_sqs, from_sqs));
               }
            }

            gbs.setGroupByItemList(v_gil);
         }
      } else {
         soc = null;
         if (((Vector)this.GroupByItemList.elementAt(0)).size() == 0) {
            gbs = null;
         } else {
            Vector singleGroupingSet = (Vector)this.GroupByItemList.elementAt(0);

            for(int i_count = 0; i_count < singleGroupingSet.size(); ++i_count) {
               if (singleGroupingSet.elementAt(i_count) instanceof SelectColumn) {
                  SelectColumn sc = (SelectColumn)singleGroupingSet.elementAt(i_count);
                  v_gil.addElement(sc.toANSISelect(to_sqs, from_sqs));
               }
            }

            gbs.setGroupByItemList(v_gil);
         }

         this.makeNonGroupedSelectItemsNull((Vector)this.GroupByItemList.elementAt(0), from_sqs, to_sqs, 8);
         if (from_sqs.getSetOperatorClause() != null) {
            soc = from_sqs.getSetOperatorClause();
         }

         for(int i_count = 1; i_count < this.GroupByItemList.size(); ++i_count) {
            Vector v_item_list = (Vector)this.GroupByItemList.elementAt(i_count);
            t_soc = this.createSetOperatorClause(v_item_list, from_sqs, to_sqs, 8);
            SelectQueryStatement t_sqs = to_sqs;

            for(SetOperatorClause t_soc = to_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
               t_sqs = t_soc.getSelectQueryStatement();
            }

            t_sqs.setSetOperatorClause(t_soc);
         }

         SelectQueryStatement t_sqs = to_sqs;

         for(SetOperatorClause t_soc = to_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
            t_sqs = t_soc.getSelectQueryStatement();
         }

         if (soc != null) {
            t_sqs.setSetOperatorClause(soc.toANSISelect(to_sqs, from_sqs));
         }
      }

      return gbs;
   }

   public GroupByStatement toTeradataSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      GroupByStatement gbs = new GroupByStatement();
      gbs.setGroupClause(this.GroupClause);
      Vector v_gil = new Vector();
      Vector v_tgil = new Vector();
      SelectColumn sc;
      Vector newFuncArgs;
      SelectColumn newSelectColumn;
      Vector newVector;
      int sci;
      SelectColumn fnArgCol;
      int i_count;
      FunctionCalls cubeRollUpFunc;
      Vector groupByVector;
      int j_count;
      int fni;
      Object fnArg;
      Vector fnArgVec;
      if (this.GroupingSetClause == null) {
         if (this.WithOption != null) {
            Vector v_gl = new Vector();
            sc = new SelectColumn();
            newFuncArgs = new Vector();
            FunctionCalls fc = new FunctionCalls();
            TableColumn tc = new TableColumn();

            for(int i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
               if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                  newSelectColumn = (SelectColumn)this.GroupByItemList.elementAt(i_count);
                  this.covertAilasToTableName(newSelectColumn, from_sqs);
                  v_gil.addElement(newSelectColumn.toTeradataSelect(to_sqs, from_sqs));
               }
            }

            tc.setColumnName(this.WithOption);
            fc.setFunctionName(tc);
            fc.setFunctionArguments(v_gil);
            newFuncArgs.addElement(fc);
            sc.setColumnExpression(newFuncArgs);
            v_gl.addElement(sc);
            gbs.setGroupByItemList(v_gl);
         } else if (this.ALLOption) {
            for(i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
               if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)this.GroupByItemList.elementAt(i_count);
                  v_gil.addElement(sc.toTeradataSelect(to_sqs, from_sqs));
               }
            }

            gbs.setGroupByItemList(v_gil);
            SetOperatorClause soc = null;
            if (from_sqs.getSetOperatorClause() != null) {
               soc = from_sqs.getSetOperatorClause();
            }

            if (from_sqs.getWhereExpression() != null) {
               SetOperatorClause n_soc = this.createSetOperatorClause((Vector)null, from_sqs, to_sqs, 12);
               SelectQueryStatement t_sqs = to_sqs;
               to_sqs.setSetOperatorClause(n_soc);

               for(SetOperatorClause t_soc = to_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
                  t_sqs = t_soc.getSelectQueryStatement();
               }

               if (soc != null) {
                  t_sqs.setSetOperatorClause(soc.toTeradataSelect(to_sqs, from_sqs));
               }
            } else if (soc != null) {
               to_sqs.setSetOperatorClause(soc.toTeradataSelect(to_sqs, from_sqs));
            }
         } else {
            for(i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
               if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                  boolean isNum = false;
                  boolean removeNumCol = false;
                  SelectColumn sc = ((SelectColumn)this.GroupByItemList.elementAt(i_count)).toTeradataSelect(to_sqs, from_sqs);
                  if (sc.getColumnExpression().size() == 1) {
                     String scStr = sc.getColumnExpression().get(0).toString().trim();

                     try {
                        Double.parseDouble(scStr);
                        isNum = true;
                     } catch (NumberFormatException var18) {
                     }

                     boolean aliasPresent = false;
                     if (to_sqs != null && from_sqs != null && to_sqs.getSelectStatement() != null && from_sqs.getSelectStatement().getSelectItemList().size() == to_sqs.getSelectStatement().getSelectItemList().size()) {
                        for(int sci = 0; sci < from_sqs.getSelectStatement().getSelectItemList().size(); ++sci) {
                           String al = ((SelectColumn)from_sqs.getSelectStatement().getSelectItemList().get(sci)).getTheCoreSelectItem().trim();
                           String aliasName;
                           if (al != null && al.equalsIgnoreCase(scStr)) {
                              aliasPresent = true;
                              aliasName = ((SelectColumn)to_sqs.getSelectStatement().getSelectItemList().get(sci)).getAliasName();
                              if (isNum) {
                                 removeNumCol = true;
                              } else {
                                 sc.getColumnExpression().setElementAt(((SelectColumn)to_sqs.getSelectStatement().getSelectItemList().get(sci)).getTheCoreSelectItem(), 0);
                              }
                              break;
                           }

                           aliasName = ((SelectColumn)to_sqs.getSelectStatement().getSelectItemList().get(sci)).getAliasName();
                           if (aliasName != null && aliasName.equalsIgnoreCase(scStr)) {
                              sc.getColumnExpression().setElementAt(((SelectColumn)to_sqs.getSelectStatement().getSelectItemList().get(sci)).getTheCoreSelectItem(), 0);
                           }
                        }
                     }
                  }

                  if (!removeNumCol) {
                     v_gil.addElement(sc);
                  }
               } else if (this.GroupByItemList.elementAt(i_count) instanceof Vector) {
                  groupByVector = (Vector)this.GroupByItemList.elementAt(i_count);

                  for(j_count = 0; j_count < groupByVector.size(); ++j_count) {
                     if (groupByVector.elementAt(j_count) instanceof SelectColumn) {
                        boolean isNum = false;
                        boolean removeNumCol = false;
                        SelectColumn sc = ((SelectColumn)groupByVector.elementAt(j_count)).toTeradataSelect(to_sqs, from_sqs);
                        if (sc.getColumnExpression().size() == 1) {
                           String scStr = sc.getColumnExpression().get(0).toString().trim();

                           try {
                              Double.parseDouble(scStr);
                              isNum = true;
                           } catch (NumberFormatException var17) {
                           }

                           boolean aliasPresent = false;
                           if (to_sqs != null && from_sqs != null && to_sqs.getSelectStatement() != null && from_sqs.getSelectStatement().getSelectItemList().size() == to_sqs.getSelectStatement().getSelectItemList().size()) {
                              for(sci = 0; sci < from_sqs.getSelectStatement().getSelectItemList().size(); ++sci) {
                                 String al = ((SelectColumn)from_sqs.getSelectStatement().getSelectItemList().get(sci)).getTheCoreSelectItem().trim();
                                 String aliasName;
                                 if (al != null && al.equalsIgnoreCase(scStr)) {
                                    aliasPresent = true;
                                    aliasName = ((SelectColumn)to_sqs.getSelectStatement().getSelectItemList().get(sci)).getAliasName();
                                    if (isNum) {
                                       removeNumCol = true;
                                    } else {
                                       sc.getColumnExpression().setElementAt(((SelectColumn)to_sqs.getSelectStatement().getSelectItemList().get(sci)).getTheCoreSelectItem(), 0);
                                    }
                                    break;
                                 }

                                 aliasName = ((SelectColumn)to_sqs.getSelectStatement().getSelectItemList().get(sci)).getAliasName();
                                 if (aliasName != null && aliasName.equalsIgnoreCase(scStr)) {
                                    sc.getColumnExpression().setElementAt(((SelectColumn)to_sqs.getSelectStatement().getSelectItemList().get(sci)).getTheCoreSelectItem(), 0);
                                 }
                              }
                           }
                        }

                        if (!removeNumCol) {
                           v_gil.addElement(sc);
                        }
                     }
                  }
               } else if (this.GroupByItemList.elementAt(i_count) instanceof FunctionCalls) {
                  cubeRollUpFunc = (FunctionCalls)this.GroupByItemList.elementAt(i_count);
                  newFuncArgs = new Vector();

                  for(fni = 0; fni < cubeRollUpFunc.getFunctionArguments().size(); ++fni) {
                     fnArg = cubeRollUpFunc.getFunctionArguments().get(fni);
                     if (fnArg instanceof SelectColumn) {
                        newFuncArgs.addElement(((SelectColumn)fnArg).toTeradataSelect(to_sqs, from_sqs));
                     } else if (fnArg instanceof Vector) {
                        fnArgVec = (Vector)fnArg;
                        newSelectColumn = new SelectColumn();
                        newVector = new Vector();

                        for(sci = 0; sci < fnArgVec.size(); ++sci) {
                           fnArgCol = ((SelectColumn)fnArgVec.elementAt(sci)).toTeradataSelect(to_sqs, from_sqs);
                           if (sci != fnArgVec.size() - 1) {
                              fnArgCol.setEndsWith(",");
                           }

                           newVector.add(fnArgCol);
                        }

                        newSelectColumn.setColumnExpression(newVector);
                        newSelectColumn.setOpenBrace("(");
                        newSelectColumn.setCloseBrace(")");
                        newFuncArgs.addElement(newSelectColumn);
                     }
                  }

                  cubeRollUpFunc.setFunctionArguments(newFuncArgs);
                  v_gil.addElement(cubeRollUpFunc);
               }
            }

            gbs.setGroupByItemList(v_gil);
         }
      } else {
         gbs.setGroupingSetClause(this.GroupingSetClause);
         gbs.setOpenBraces(this.openBraces);
         gbs.setClosedBraces(this.closedBraces);

         for(i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
            if (this.GroupByItemList.elementAt(i_count) instanceof Vector) {
               groupByVector = (Vector)this.GroupByItemList.elementAt(i_count);
               v_gil = new Vector();

               for(j_count = 0; j_count < groupByVector.size(); ++j_count) {
                  if (groupByVector.elementAt(j_count) instanceof SelectColumn) {
                     v_gil.addElement(((SelectColumn)groupByVector.elementAt(j_count)).toTeradataSelect(to_sqs, from_sqs));
                  }
               }

               v_tgil.addElement(v_gil);
            } else if (!(this.GroupByItemList.elementAt(i_count) instanceof FunctionCalls)) {
               sc = (SelectColumn)this.GroupByItemList.elementAt(i_count);
               v_tgil.addElement(sc.toTeradataSelect(to_sqs, from_sqs));
            } else {
               cubeRollUpFunc = (FunctionCalls)this.GroupByItemList.elementAt(i_count);
               newFuncArgs = new Vector();

               for(fni = 0; fni < cubeRollUpFunc.getFunctionArguments().size(); ++fni) {
                  fnArg = cubeRollUpFunc.getFunctionArguments().get(fni);
                  if (fnArg instanceof SelectColumn) {
                     newFuncArgs.addElement(((SelectColumn)fnArg).toTeradataSelect(to_sqs, from_sqs));
                  } else if (fnArg instanceof Vector) {
                     fnArgVec = (Vector)fnArg;
                     newSelectColumn = new SelectColumn();
                     newVector = new Vector();

                     for(sci = 0; sci < fnArgVec.size(); ++sci) {
                        fnArgCol = ((SelectColumn)fnArgVec.elementAt(sci)).toTeradataSelect(to_sqs, from_sqs);
                        if (sci != fnArgVec.size() - 1) {
                           fnArgCol.setEndsWith(",");
                        }

                        newVector.add(fnArgCol);
                     }

                     newSelectColumn.setColumnExpression(newVector);
                     newSelectColumn.setOpenBrace("(");
                     newSelectColumn.setCloseBrace(")");
                     newFuncArgs.addElement(newSelectColumn);
                  }
               }

               cubeRollUpFunc.setFunctionArguments(newFuncArgs);
               v_gil.addElement(cubeRollUpFunc);
            }
         }

         gbs.setGroupByItemList(v_tgil);
      }

      return gbs.getGroupByItemList() != null && !gbs.getGroupByItemList().isEmpty() ? gbs : null;
   }

   public GroupByStatement toDB2Select(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      GroupByStatement gbs = new GroupByStatement();
      gbs.setGroupClause(this.GroupClause);
      Vector v_gil = new Vector();
      Vector v_tgil = new Vector();
      int i_count;
      if (this.GroupingSetClause == null) {
         SelectColumn sc;
         Vector newFuncArgs;
         SelectColumn newSelectColumn;
         if (this.WithOption != null) {
            Vector v_gl = new Vector();
            sc = new SelectColumn();
            newFuncArgs = new Vector();
            FunctionCalls fc = new FunctionCalls();
            TableColumn tc = new TableColumn();

            for(int i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
               if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                  newSelectColumn = (SelectColumn)this.GroupByItemList.elementAt(i_count);
                  this.covertAilasToTableName(newSelectColumn, from_sqs);
                  v_gil.addElement(newSelectColumn.toDB2Select(to_sqs, from_sqs));
               }
            }

            tc.setColumnName(this.WithOption);
            fc.setFunctionName(tc);
            fc.setFunctionArguments(v_gil);
            newFuncArgs.addElement(fc);
            sc.setColumnExpression(newFuncArgs);
            v_gl.addElement(sc);
            gbs.setGroupByItemList(v_gl);
         } else if (this.ALLOption) {
            for(i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
               if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)this.GroupByItemList.elementAt(i_count);
                  this.covertAilasToTableName(sc, from_sqs);
                  v_gil.addElement(sc.toDB2Select(to_sqs, from_sqs));
               }
            }

            gbs.setGroupByItemList(v_gil);
            SetOperatorClause soc = null;
            if (from_sqs.getSetOperatorClause() != null) {
               soc = from_sqs.getSetOperatorClause();
            }

            if (from_sqs.getWhereExpression() != null) {
               SetOperatorClause n_soc = this.createSetOperatorClause((Vector)null, from_sqs, to_sqs, 3);
               SelectQueryStatement t_sqs = to_sqs;
               to_sqs.setSetOperatorClause(n_soc);

               for(SetOperatorClause t_soc = to_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
                  t_sqs = t_soc.getSelectQueryStatement();
               }

               if (soc != null) {
                  t_sqs.setSetOperatorClause(soc.toDB2Select(to_sqs, from_sqs));
               }
            } else if (soc != null) {
               to_sqs.setSetOperatorClause(soc.toDB2Select(to_sqs, from_sqs));
            }
         } else {
            for(i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
               if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)this.GroupByItemList.elementAt(i_count);
                  this.covertAilasToTableName(sc, from_sqs);
                  v_gil.addElement(sc.toDB2Select(to_sqs, from_sqs));
               } else if (this.GroupByItemList.elementAt(i_count) instanceof FunctionCalls) {
                  FunctionCalls cubeRollUpFunc = (FunctionCalls)this.GroupByItemList.elementAt(i_count);
                  newFuncArgs = new Vector();

                  for(int fni = 0; fni < cubeRollUpFunc.getFunctionArguments().size(); ++fni) {
                     Object fnArg = cubeRollUpFunc.getFunctionArguments().get(fni);
                     if (fnArg instanceof SelectColumn) {
                        newFuncArgs.addElement(((SelectColumn)fnArg).toDB2Select(to_sqs, from_sqs));
                     } else if (fnArg instanceof Vector) {
                        Vector fnArgVec = (Vector)fnArg;
                        newSelectColumn = new SelectColumn();
                        Vector newVector = new Vector();

                        for(int vi = 0; vi < fnArgVec.size(); ++vi) {
                           SelectColumn fnArgCol = ((SelectColumn)fnArgVec.elementAt(vi)).toDB2Select(to_sqs, from_sqs);
                           if (vi != fnArgVec.size() - 1) {
                              fnArgCol.setEndsWith(",");
                           }

                           newVector.add(fnArgCol);
                        }

                        newSelectColumn.setColumnExpression(newVector);
                        newSelectColumn.setOpenBrace("(");
                        newSelectColumn.setCloseBrace(")");
                        newFuncArgs.addElement(newSelectColumn);
                     }
                  }

                  cubeRollUpFunc.setFunctionArguments(newFuncArgs);
                  v_gil.addElement(cubeRollUpFunc);
               }
            }

            gbs.setGroupByItemList(v_gil);
         }
      } else {
         gbs.setGroupingSetClause(this.GroupingSetClause);
         gbs.setOpenBraces(this.openBraces);
         gbs.setClosedBraces(this.closedBraces);

         for(i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
            Vector v_item_list = (Vector)this.GroupByItemList.elementAt(i_count);
            v_gil = new Vector();

            for(int i_icount = 0; i_icount < v_item_list.size(); ++i_icount) {
               if (v_item_list.elementAt(i_icount) instanceof SelectColumn) {
                  v_gil.addElement(((SelectColumn)v_item_list.elementAt(i_icount)).toDB2Select(to_sqs, from_sqs));
               }
            }

            v_tgil.addElement(v_gil);
         }

         gbs.setGroupByItemList(v_tgil);
      }

      return gbs;
   }

   public GroupByStatement toMySQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      GroupByStatement gbs = new GroupByStatement();
      gbs.setCommentClassAfterToken(this.commentObjAfterToken);
      gbs.setGroupClause(this.GroupClause);
      Vector v_gbl = new Vector();
      if (this.GroupingSetClause == null) {
         for(int i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
            if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)this.GroupByItemList.elementAt(i_count);
               if (from_sqs != null && from_sqs.isHyperSql()) {
                  this.covertAilasToTableName(sc, from_sqs);
               }

               v_gbl.addElement(sc.toMySQLSelect(to_sqs, from_sqs));
            } else if (this.GroupByItemList.elementAt(i_count) instanceof FunctionCalls) {
               FunctionCalls fc = (FunctionCalls)this.GroupByItemList.elementAt(i_count);
               String s_fn = new String();
               if (fc.getFunctionName() != null) {
                  TableColumn c = fc.getFunctionName();
                  s_fn = fc.getFunctionName().getColumnName();
               }

               if ((s_fn == null || !s_fn.equalsIgnoreCase("cube")) && !s_fn.equalsIgnoreCase("rollup")) {
                  v_gbl.addElement(fc.toMSSQLServerSelect(to_sqs, from_sqs));
               } else {
                  gbs.setWithOption(s_fn);
                  Vector v_fa = fc.getFunctionArguments();

                  for(int cnt = 0; cnt < v_fa.size(); ++cnt) {
                     v_gbl.addElement(((SelectColumn)v_fa.elementAt(cnt)).toMySQLSelect(to_sqs, from_sqs));
                  }
               }
            }
         }
      }

      if (this.descOption != null) {
         gbs.setDescOption(this.descOption);
      }

      gbs.setGroupByItemList(v_gbl);
      if (this.GroupingSetClause != null) {
         throw new ConvertException();
      } else if (this.ALLOption) {
         throw new ConvertException();
      } else {
         if (this.WithOption != null) {
            gbs.setWithOption(this.WithOption);
         }

         return gbs;
      }
   }

   public GroupByStatement toMSSQLServerSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      GroupByStatement gbs = new GroupByStatement();
      gbs.setGroupClause(this.GroupClause);
      if (this.ALLOption) {
         gbs.setALLOption(this.ALLOption);
      }

      if (this.WithOption != null) {
         gbs.setWithOption(this.WithOption);
      }

      Vector v_gil = new Vector();
      new Vector();
      Vector v_ce;
      if (this.GroupingSetClause == null) {
         for(int i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
            if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)this.GroupByItemList.elementAt(i_count);
               v_ce = sc.getColumnExpression();
               this.covertAilasToTableName(sc, from_sqs);
               if (v_ce.elementAt(0) instanceof FunctionCalls) {
                  FunctionCalls fc = (FunctionCalls)v_ce.elementAt(0);
                  String s_fn = new String();
                  if (fc.getFunctionName() != null) {
                     TableColumn c = fc.getFunctionName();
                     s_fn = fc.getFunctionName().getColumnName();
                  }

                  if ((s_fn == null || !s_fn.equalsIgnoreCase("cube")) && !s_fn.equalsIgnoreCase("rollup")) {
                     v_gil.addElement(sc.toMSSQLServerSelect(to_sqs, from_sqs));
                  } else {
                     gbs.setWithOption(s_fn);
                     Vector v_fa = fc.getFunctionArguments();

                     for(int cnt = 0; cnt < v_fa.size(); ++cnt) {
                        v_gil.addElement(((SelectColumn)v_fa.elementAt(cnt)).toMSSQLServerSelect(to_sqs, from_sqs));
                     }
                  }
               } else if (v_ce.elementAt(0) instanceof TableColumn) {
                  SwisSQLUtils.checkAndReplaceGroupByItem(sc, from_sqs);
                  v_gil.addElement(sc.toMSSQLServerSelect(to_sqs, from_sqs));
               } else {
                  v_gil.addElement(sc.toMSSQLServerSelect(to_sqs, from_sqs));
               }
            } else if (this.GroupByItemList.elementAt(i_count) instanceof FunctionCalls) {
               FunctionCalls fc = (FunctionCalls)this.GroupByItemList.elementAt(i_count);
               String s_fn = new String();
               if (fc.getFunctionName() != null) {
                  TableColumn c = fc.getFunctionName();
                  s_fn = fc.getFunctionName().getColumnName();
               }

               if ((s_fn == null || !s_fn.equalsIgnoreCase("cube")) && !s_fn.equalsIgnoreCase("rollup")) {
                  v_gil.addElement(fc.toMSSQLServerSelect(to_sqs, from_sqs));
               } else {
                  gbs.setWithOption(s_fn);
                  Vector v_fa = fc.getFunctionArguments();

                  for(int cnt = 0; cnt < v_fa.size(); ++cnt) {
                     v_gil.addElement(((SelectColumn)v_fa.elementAt(cnt)).toMSSQLServerSelect(to_sqs, from_sqs));
                  }
               }
            }
         }

         this.processGroupByArguments(v_gil, v_gil, 0, false);
         gbs.setGroupByItemList(v_gil);
      } else {
         SetOperatorClause soc = null;
         if (((Vector)this.GroupByItemList.elementAt(0)).size() == 0) {
            gbs = null;
         } else {
            Vector singleGroupingSet = (Vector)this.GroupByItemList.elementAt(0);

            for(int i_count = 0; i_count < singleGroupingSet.size(); ++i_count) {
               if (singleGroupingSet.elementAt(i_count) instanceof SelectColumn) {
                  SelectColumn sc = (SelectColumn)singleGroupingSet.elementAt(i_count);
                  v_gil.addElement(sc.toMSSQLServerSelect(to_sqs, from_sqs));
               }
            }

            this.processGroupByArguments(v_gil, v_gil, 0, false);
            gbs.setGroupByItemList(v_gil);
         }

         this.makeNonGroupedSelectItemsNull((Vector)this.GroupByItemList.elementAt(0), from_sqs, to_sqs, 2);
         if (from_sqs.getSetOperatorClause() != null) {
            soc = from_sqs.getSetOperatorClause();
         }

         for(int i_count = 1; i_count < this.GroupByItemList.size(); ++i_count) {
            v_ce = (Vector)this.GroupByItemList.elementAt(i_count);
            SetOperatorClause n_soc = this.createSetOperatorClause(v_ce, from_sqs, to_sqs, 2);
            SelectQueryStatement t_sqs = to_sqs;

            for(SetOperatorClause t_soc = to_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
               t_sqs = t_soc.getSelectQueryStatement();
            }

            t_sqs.setSetOperatorClause(n_soc);
         }

         SelectQueryStatement t_sqs = to_sqs;

         for(SetOperatorClause t_soc = to_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
            t_sqs = t_soc.getSelectQueryStatement();
         }

         if (soc != null) {
            t_sqs.setSetOperatorClause(soc.toMSSQLServerSelect(to_sqs, from_sqs));
         }
      }

      if (gbs != null) {
         gbs.setCheckGroupByStatement(true);
      }

      return gbs;
   }

   public GroupByStatement toSybaseSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      GroupByStatement gbs = new GroupByStatement();
      gbs.setGroupClause(this.GroupClause);
      gbs.setObjectContext(this.context);
      if (this.ALLOption) {
         gbs.setALLOption(this.ALLOption);
      }

      if (this.WithOption != null) {
         gbs.setWithOption(this.WithOption);
      }

      Vector v_gil = new Vector();
      new Vector();
      int i_count;
      if (this.GroupingSetClause == null) {
         boolean rollupAdded = false;

         for(i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
            Vector v_fa;
            Vector v_fa;
            int cnt;
            if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)this.GroupByItemList.elementAt(i_count);
               Vector v_ce = sc.getColumnExpression();
               if (!(v_ce.elementAt(0) instanceof FunctionCalls)) {
                  if (v_ce.elementAt(0) instanceof TableColumn) {
                     this.covertAilasToTableName(sc, from_sqs);
                     v_gil.addElement(sc.toSybaseSelect(to_sqs, from_sqs));
                  } else {
                     v_gil.addElement(sc.toSybaseSelect(to_sqs, from_sqs));
                  }
               } else {
                  FunctionCalls fc = (FunctionCalls)v_ce.elementAt(0);
                  String s_fn = fc.getFunctionName().getColumnName();
                  if (!s_fn.equalsIgnoreCase("cube") && !s_fn.equalsIgnoreCase("rollup")) {
                     v_gil.addElement(fc.toSybaseSelect(to_sqs, from_sqs));
                  } else {
                     v_fa = to_sqs.getSelectStatement().getSelectItemList();
                     Vector newSelectItemList = new Vector();
                     if (v_fa != null) {
                        for(int ii = 0; ii < v_fa.size(); ++ii) {
                           if (v_fa.get(ii) instanceof SelectColumn) {
                              SelectColumn orgSC = (SelectColumn)v_fa.get(ii);
                              Vector columnExpression = orgSC.getColumnExpression();
                              Vector newColumnExp = new Vector();
                              SelectColumn newSC = new SelectColumn();
                              this.copyFromOneSCToAnother(columnExpression, newColumnExp);
                              newSC.setColumnExpression(newColumnExp);
                              newSC.setIsAS(orgSC.getIsAS());
                              newSC.setAliasName(orgSC.getAliasName());
                              newSC.setObjectContext(this.context);
                              if (v_fa.size() - 1 > ii) {
                                 newSC.setEndsWith(",");
                              }

                              newSelectItemList.add(newSC);
                           }
                        }
                     }

                     this.processCubeAndRollupConversion(fc, from_sqs, to_sqs, 7);
                     to_sqs.getSelectStatement().setSelectItemList(newSelectItemList);
                     rollupAdded = true;
                     int cnt;
                     if (fc.getFunctionArguments() != null && fc.getFunctionArguments().size() > 0) {
                        v_fa = new Vector();

                        for(cnt = 0; cnt < fc.getFunctionArguments().size(); ++cnt) {
                           if (cnt > 0) {
                              v_fa.add(", ");
                           }

                           v_fa.add(fc.getFunctionArguments().get(cnt));
                        }

                        sc.setColumnExpression(v_fa);
                        v_gil.addElement(sc.toSybaseSelect(to_sqs, from_sqs));
                     }

                     if (!rollupAdded) {
                        gbs.setWithOption(s_fn);
                        v_fa = fc.getFunctionArguments();

                        for(cnt = 0; cnt < v_fa.size(); ++cnt) {
                           v_gil.addElement(((SelectColumn)v_fa.elementAt(cnt)).toSybaseSelect(to_sqs, from_sqs));
                        }
                     }
                  }

                  if (!rollupAdded && !s_fn.equalsIgnoreCase("TRUNC") && !s_fn.equalsIgnoreCase("DECODE") && !s_fn.equalsIgnoreCase("FLOOR")) {
                     gbs.setWithOption(s_fn);
                     v_fa = fc.getFunctionArguments();

                     for(cnt = 0; cnt < v_fa.size(); ++cnt) {
                        v_gil.addElement(((SelectColumn)v_fa.elementAt(cnt)).toSybaseSelect(to_sqs, from_sqs));
                     }
                  }
               }
            } else if (this.GroupByItemList.elementAt(i_count) instanceof FunctionCalls) {
               FunctionCalls fc = (FunctionCalls)this.GroupByItemList.elementAt(i_count);
               String s_fn = fc.getFunctionName().getColumnName();
               Vector selectItemList;
               if (!s_fn.equalsIgnoreCase("cube") && !s_fn.equalsIgnoreCase("rollup")) {
                  v_gil.addElement(fc.toSybaseSelect(to_sqs, from_sqs));
               } else {
                  selectItemList = to_sqs.getSelectStatement().getSelectItemList();
                  Vector newSelectItemList = new Vector();
                  SelectColumn cubeRollupSelectColumn;
                  if (selectItemList != null) {
                     for(int ii = 0; ii < selectItemList.size(); ++ii) {
                        if (selectItemList.get(ii) instanceof SelectColumn) {
                           cubeRollupSelectColumn = (SelectColumn)selectItemList.get(ii);
                           v_fa = cubeRollupSelectColumn.getColumnExpression();
                           Vector newColumnExp = new Vector();
                           SelectColumn newSC = new SelectColumn();
                           this.copyFromOneSCToAnother(v_fa, newColumnExp);
                           newSC.setColumnExpression(newColumnExp);
                           newSC.setIsAS(cubeRollupSelectColumn.getIsAS());
                           newSC.setAliasName(cubeRollupSelectColumn.getAliasName());
                           newSC.setObjectContext(this.context);
                           if (selectItemList.size() - 1 > ii) {
                              newSC.setEndsWith(",");
                           }

                           newSelectItemList.add(newSC);
                        }
                     }
                  }

                  this.processCubeAndRollupConversion(fc, from_sqs, to_sqs, 7);
                  to_sqs.getSelectStatement().setSelectItemList(newSelectItemList);
                  rollupAdded = true;
                  if (fc.getFunctionArguments() != null && fc.getFunctionArguments().size() > 0) {
                     v_fa = new Vector();

                     for(cnt = 0; cnt < fc.getFunctionArguments().size(); ++cnt) {
                        if (cnt > 0) {
                           v_fa.add(", ");
                        }

                        v_fa.add(fc.getFunctionArguments().get(cnt));
                     }

                     cubeRollupSelectColumn = new SelectColumn();
                     cubeRollupSelectColumn.setColumnExpression(v_fa);
                     v_gil.addElement(cubeRollupSelectColumn.toSybaseSelect(to_sqs, from_sqs));
                  }

                  if (!rollupAdded) {
                     gbs.setWithOption(s_fn);
                     v_fa = fc.getFunctionArguments();

                     for(cnt = 0; cnt < v_fa.size(); ++cnt) {
                        v_gil.addElement(((SelectColumn)v_fa.elementAt(cnt)).toSybaseSelect(to_sqs, from_sqs));
                     }
                  }
               }

               if (!rollupAdded && !s_fn.equalsIgnoreCase("TRUNC") && !s_fn.equalsIgnoreCase("DECODE") && !s_fn.equalsIgnoreCase("FLOOR")) {
                  gbs.setWithOption(s_fn);
                  selectItemList = fc.getFunctionArguments();

                  for(int cnt = 0; cnt < selectItemList.size(); ++cnt) {
                     v_gil.addElement(((SelectColumn)selectItemList.elementAt(cnt)).toSybaseSelect(to_sqs, from_sqs));
                  }
               }
            }
         }

         this.processGroupByArguments(v_gil, v_gil, 0, false);
         gbs.setGroupByItemList(v_gil);
      } else {
         SetOperatorClause soc = null;
         if (((Vector)this.GroupByItemList.elementAt(0)).size() == 0) {
            gbs = null;
         } else {
            Vector singleGroupingSet = (Vector)this.GroupByItemList.elementAt(0);

            for(int i_count = 0; i_count < singleGroupingSet.size(); ++i_count) {
               if (singleGroupingSet.elementAt(i_count) instanceof SelectColumn) {
                  SelectColumn sc = (SelectColumn)singleGroupingSet.elementAt(i_count);
                  v_gil.addElement(sc.toSybaseSelect(to_sqs, from_sqs));
               }
            }

            this.processGroupByArguments(v_gil, v_gil, 0, false);
            gbs.setGroupByItemList(v_gil);
         }

         this.makeNonGroupedSelectItemsNull((Vector)this.GroupByItemList.elementAt(0), from_sqs, to_sqs, 7);
         if (from_sqs.getSetOperatorClause() != null) {
            soc = from_sqs.getSetOperatorClause();
         }

         for(i_count = 1; i_count < this.GroupByItemList.size(); ++i_count) {
            Vector v_item_list = (Vector)this.GroupByItemList.elementAt(i_count);
            SetOperatorClause n_soc = this.createSetOperatorClause(v_item_list, from_sqs, to_sqs, 7);
            SelectQueryStatement t_sqs = to_sqs;

            for(SetOperatorClause t_soc = to_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
               t_sqs = t_soc.getSelectQueryStatement();
            }

            t_sqs.setSetOperatorClause(n_soc);
         }

         SelectQueryStatement t_sqs = to_sqs;

         for(SetOperatorClause t_soc = to_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
            t_sqs = t_soc.getSelectQueryStatement();
         }

         if (soc != null) {
            t_sqs.setSetOperatorClause(soc.toSybaseSelect(to_sqs, from_sqs));
         }
      }

      if (gbs != null) {
         gbs.setCheckGroupByStatement(true);
      }

      return gbs;
   }

   public GroupByStatement toBigQuerySelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      GroupByStatement gbs = new GroupByStatement();
      gbs.setGroupClause(this.GroupClause);
      Vector v_gil = new Vector();
      new Vector();
      SetOperatorClause t_soc;
      SetOperatorClause soc;
      int i_count;
      SelectColumn sc;
      Vector v_gblw;
      if (this.GroupingSetClause == null) {
         SelectColumn sc;
         if (this.ALLOption) {
            for(int i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
               if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)this.GroupByItemList.elementAt(i_count);
                  v_gil.addElement(sc.toBigQuerySelect(to_sqs, from_sqs));
               }
            }

            gbs.setGroupByItemList(v_gil);
            soc = null;
            if (from_sqs.getSetOperatorClause() != null) {
               soc = from_sqs.getSetOperatorClause();
            }

            if (from_sqs.getWhereExpression() != null) {
               SetOperatorClause n_soc = this.createSetOperatorClause((Vector)null, from_sqs, to_sqs, 14);
               SelectQueryStatement t_sqs = to_sqs;
               to_sqs.setSetOperatorClause(n_soc);

               for(t_soc = to_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
                  t_sqs = t_soc.getSelectQueryStatement();
               }

               if (soc != null) {
                  t_sqs.setSetOperatorClause(soc.toBigQuerySelect(to_sqs, from_sqs));
               }
            } else if (soc != null) {
               to_sqs.setSetOperatorClause(soc.toBigQuerySelect(to_sqs, from_sqs));
            }
         } else {
            boolean rollupAdded = false;
            sc = null;

            for(i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
               rollupAdded = false;
               Vector newSelectItemList;
               Vector newArguments;
               SelectColumn orgSC;
               if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)this.GroupByItemList.elementAt(i_count);
                  Vector columnExpressionVector = sc.getColumnExpression();

                  for(int i = 0; i < columnExpressionVector.size(); ++i) {
                     if (columnExpressionVector.size() == 1 && columnExpressionVector.elementAt(i) instanceof FunctionCalls) {
                        FunctionCalls fc = (FunctionCalls)columnExpressionVector.elementAt(i);
                        String s_fn = fc.getFunctionName().getColumnName();
                        if (s_fn.equalsIgnoreCase("cube") || s_fn.equalsIgnoreCase("rollup")) {
                           Vector selectItemList = to_sqs.getSelectStatement().getSelectItemList();
                           newSelectItemList = new Vector();
                           if (selectItemList != null) {
                              for(int ii = 0; ii < selectItemList.size(); ++ii) {
                                 if (selectItemList.get(i) instanceof SelectColumn) {
                                    orgSC = (SelectColumn)selectItemList.get(ii);
                                    Vector columnExpression = orgSC.getColumnExpression();
                                    Vector newColumnExp = new Vector();
                                    SelectColumn newSC = new SelectColumn();
                                    this.copyFromOneSCToAnother(columnExpression, newColumnExp);
                                    newSC.setColumnExpression(newColumnExp);
                                    newSC.setIsAS(orgSC.getIsAS());
                                    newSC.setAliasName(orgSC.getAliasName());
                                    if (selectItemList.size() - 1 > ii) {
                                       newSC.setEndsWith(",");
                                    }

                                    newSelectItemList.add(newSC);
                                 }
                              }
                           }

                           this.processCubeAndRollupConversion(fc, from_sqs, to_sqs, 14);
                           to_sqs.getSelectStatement().setSelectItemList(newSelectItemList);
                           rollupAdded = true;
                           if (fc.getFunctionArguments() != null && fc.getFunctionArguments().size() > 0) {
                              newArguments = new Vector();

                              for(int j = 0; j < fc.getFunctionArguments().size(); ++j) {
                                 if (j > 0) {
                                    newArguments.add(", ");
                                 }

                                 newArguments.add(fc.getFunctionArguments().get(j));
                              }

                              sc.setColumnExpression(newArguments);
                              v_gil.addElement(sc.toBigQuerySelect(to_sqs, from_sqs));
                           }
                        }
                     }
                  }

                  if (!rollupAdded) {
                     v_gil.addElement(sc.toBigQuerySelect(to_sqs, from_sqs));
                  }
               } else if (this.GroupByItemList.elementAt(i_count) instanceof FunctionCalls) {
                  FunctionCalls fc = (FunctionCalls)this.GroupByItemList.elementAt(i_count);
                  String s_fn = fc.getFunctionName().getColumnName();
                  if (!s_fn.equalsIgnoreCase("cube") && !s_fn.equalsIgnoreCase("rollup")) {
                     v_gil.addElement(fc.toBigQuerySelect(to_sqs, from_sqs));
                  } else {
                     Vector selectItemList = to_sqs.getSelectStatement().getSelectItemList();
                     Vector newSelectItemList = new Vector();
                     if (selectItemList != null) {
                        for(int i = 0; i < selectItemList.size(); ++i) {
                           if (selectItemList.get(i) instanceof SelectColumn) {
                              SelectColumn orgSC = (SelectColumn)selectItemList.get(i);
                              newSelectItemList = orgSC.getColumnExpression();
                              newArguments = new Vector();
                              orgSC = new SelectColumn();
                              this.copyFromOneSCToAnother(newSelectItemList, newArguments);
                              orgSC.setColumnExpression(newArguments);
                              orgSC.setIsAS(orgSC.getIsAS());
                              orgSC.setAliasName(orgSC.getAliasName());
                              newSelectItemList.add(orgSC);
                              if (selectItemList.size() - 1 > i) {
                                 orgSC.setEndsWith(",");
                              }
                           }
                        }
                     }

                     this.processCubeAndRollupConversion(fc, from_sqs, to_sqs, 14);
                     to_sqs.getSelectStatement().setSelectItemList(newSelectItemList);
                  }
               }
            }

            gbs.setGroupByItemList(v_gil);
         }
      } else {
         soc = null;
         if (((Vector)this.GroupByItemList.elementAt(0)).size() == 0) {
            gbs = null;
         } else {
            Vector singleGroupingSet = (Vector)this.GroupByItemList.elementAt(0);

            for(i_count = 0; i_count < singleGroupingSet.size(); ++i_count) {
               if (singleGroupingSet.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)singleGroupingSet.elementAt(i_count);
                  v_gil.addElement(sc.toBigQuerySelect(to_sqs, from_sqs));
               }
            }

            gbs.setGroupByItemList(v_gil);
         }

         this.makeNonGroupedSelectItemsNull((Vector)this.GroupByItemList.elementAt(0), from_sqs, to_sqs, 14);
         if (from_sqs.getSetOperatorClause() != null) {
            soc = from_sqs.getSetOperatorClause();
         }

         for(int i_count = 1; i_count < this.GroupByItemList.size(); ++i_count) {
            v_gblw = (Vector)this.GroupByItemList.elementAt(i_count);
            t_soc = this.createSetOperatorClause(v_gblw, from_sqs, to_sqs, 14);
            SelectQueryStatement t_sqs = to_sqs;

            for(SetOperatorClause t_soc = to_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
               t_sqs = t_soc.getSelectQueryStatement();
            }

            t_sqs.setSetOperatorClause(t_soc);
         }

         SelectQueryStatement t_sqs = to_sqs;

         for(SetOperatorClause t_soc = to_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
            t_sqs = t_soc.getSelectQueryStatement();
         }

         if (soc != null) {
            t_sqs.setSetOperatorClause(soc.toBigQuerySelect(to_sqs, from_sqs));
         }
      }

      if (this.WithOption != null && (this.WithOption.equalsIgnoreCase("ROLLUP") || this.WithOption.equalsIgnoreCase("CUBE"))) {
         FunctionCalls fc = new FunctionCalls();
         TableColumn tc = new TableColumn();
         tc.setColumnName(this.WithOption.toUpperCase());
         fc.setFunctionName(tc);
         fc.setFunctionArguments(v_gil);
         v_gblw = new Vector();
         v_gblw.add(fc);
         gbs.setGroupByItemList(v_gblw);
      }

      return gbs;
   }

   public GroupByStatement toPostgreSQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      GroupByStatement gbs = new GroupByStatement();
      gbs.setGroupClause(this.GroupClause);
      Vector v_gil = new Vector();
      new Vector();
      SetOperatorClause t_soc;
      SetOperatorClause soc;
      int i_count;
      SelectColumn sc;
      Vector v_gblw;
      if (this.GroupingSetClause == null) {
         SelectColumn sc;
         if (this.ALLOption) {
            for(int i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
               if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)this.GroupByItemList.elementAt(i_count);
                  v_gil.addElement(sc.toPostgreSQLSelect(to_sqs, from_sqs));
               }
            }

            gbs.setGroupByItemList(v_gil);
            soc = null;
            if (from_sqs.getSetOperatorClause() != null) {
               soc = from_sqs.getSetOperatorClause();
            }

            if (from_sqs.getWhereExpression() != null) {
               SetOperatorClause n_soc = this.createSetOperatorClause((Vector)null, from_sqs, to_sqs, 4);
               SelectQueryStatement t_sqs = to_sqs;
               to_sqs.setSetOperatorClause(n_soc);

               for(t_soc = to_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
                  t_sqs = t_soc.getSelectQueryStatement();
               }

               if (soc != null) {
                  t_sqs.setSetOperatorClause(soc.toPostgreSQLSelect(to_sqs, from_sqs));
               }
            } else if (soc != null) {
               to_sqs.setSetOperatorClause(soc.toPostgreSQLSelect(to_sqs, from_sqs));
            }
         } else {
            boolean rollupAdded = false;
            sc = null;

            for(i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
               rollupAdded = false;
               Vector newSelectItemList;
               Vector newArguments;
               SelectColumn orgSC;
               if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)this.GroupByItemList.elementAt(i_count);
                  Vector columnExpressionVector = sc.getColumnExpression();

                  for(int i = 0; i < columnExpressionVector.size(); ++i) {
                     if (columnExpressionVector.size() == 1 && columnExpressionVector.elementAt(i) instanceof FunctionCalls) {
                        FunctionCalls fc = (FunctionCalls)columnExpressionVector.elementAt(i);
                        String s_fn = fc.getFunctionName().getColumnName();
                        if (s_fn.equalsIgnoreCase("cube") || s_fn.equalsIgnoreCase("rollup")) {
                           Vector selectItemList = to_sqs.getSelectStatement().getSelectItemList();
                           newSelectItemList = new Vector();
                           if (selectItemList != null) {
                              for(int ii = 0; ii < selectItemList.size(); ++ii) {
                                 if (selectItemList.get(i) instanceof SelectColumn) {
                                    orgSC = (SelectColumn)selectItemList.get(ii);
                                    Vector columnExpression = orgSC.getColumnExpression();
                                    Vector newColumnExp = new Vector();
                                    SelectColumn newSC = new SelectColumn();
                                    this.copyFromOneSCToAnother(columnExpression, newColumnExp);
                                    newSC.setColumnExpression(newColumnExp);
                                    newSC.setIsAS(orgSC.getIsAS());
                                    newSC.setAliasName(orgSC.getAliasName());
                                    if (selectItemList.size() - 1 > ii) {
                                       newSC.setEndsWith(",");
                                    }

                                    newSelectItemList.add(newSC);
                                 }
                              }
                           }

                           this.processCubeAndRollupConversion(fc, from_sqs, to_sqs, 4);
                           to_sqs.getSelectStatement().setSelectItemList(newSelectItemList);
                           rollupAdded = true;
                           if (fc.getFunctionArguments() != null && fc.getFunctionArguments().size() > 0) {
                              newArguments = new Vector();

                              for(int j = 0; j < fc.getFunctionArguments().size(); ++j) {
                                 if (j > 0) {
                                    newArguments.add(", ");
                                 }

                                 newArguments.add(fc.getFunctionArguments().get(j));
                              }

                              sc.setColumnExpression(newArguments);
                              v_gil.addElement(sc.toPostgreSQLSelect(to_sqs, from_sqs));
                           }
                        }
                     }
                  }

                  if (!rollupAdded && !isLiteralStringValuePresentInGroupby(sc)) {
                     v_gil.addElement(sc.toPostgreSQLSelect(to_sqs, from_sqs));
                  }
               } else if (this.GroupByItemList.elementAt(i_count) instanceof FunctionCalls) {
                  FunctionCalls fc = (FunctionCalls)this.GroupByItemList.elementAt(i_count);
                  String s_fn = fc.getFunctionName().getColumnName();
                  if (!s_fn.equalsIgnoreCase("cube") && !s_fn.equalsIgnoreCase("rollup")) {
                     v_gil.addElement(fc.toPostgreSQLSelect(to_sqs, from_sqs));
                  } else {
                     Vector selectItemList = to_sqs.getSelectStatement().getSelectItemList();
                     Vector newSelectItemList = new Vector();
                     if (selectItemList != null) {
                        for(int i = 0; i < selectItemList.size(); ++i) {
                           if (selectItemList.get(i) instanceof SelectColumn) {
                              SelectColumn orgSC = (SelectColumn)selectItemList.get(i);
                              newSelectItemList = orgSC.getColumnExpression();
                              newArguments = new Vector();
                              orgSC = new SelectColumn();
                              this.copyFromOneSCToAnother(newSelectItemList, newArguments);
                              orgSC.setColumnExpression(newArguments);
                              orgSC.setIsAS(orgSC.getIsAS());
                              orgSC.setAliasName(orgSC.getAliasName());
                              newSelectItemList.add(orgSC);
                              if (selectItemList.size() - 1 > i) {
                                 orgSC.setEndsWith(",");
                              }
                           }
                        }
                     }

                     this.processCubeAndRollupConversion(fc, from_sqs, to_sqs, 4);
                     to_sqs.getSelectStatement().setSelectItemList(newSelectItemList);
                  }
               }
            }

            if (v_gil.size() == 0) {
               gbs = null;
            } else {
               gbs.setGroupByItemList(v_gil);
            }
         }
      } else {
         soc = null;
         if (((Vector)this.GroupByItemList.elementAt(0)).size() == 0) {
            gbs = null;
         } else {
            Vector singleGroupingSet = (Vector)this.GroupByItemList.elementAt(0);

            for(i_count = 0; i_count < singleGroupingSet.size(); ++i_count) {
               if (singleGroupingSet.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)singleGroupingSet.elementAt(i_count);
                  v_gil.addElement(sc.toPostgreSQLSelect(to_sqs, from_sqs));
               }
            }

            gbs.setGroupByItemList(v_gil);
         }

         this.makeNonGroupedSelectItemsNull((Vector)this.GroupByItemList.elementAt(0), from_sqs, to_sqs, 4);
         if (from_sqs.getSetOperatorClause() != null) {
            soc = from_sqs.getSetOperatorClause();
         }

         for(int i_count = 1; i_count < this.GroupByItemList.size(); ++i_count) {
            v_gblw = (Vector)this.GroupByItemList.elementAt(i_count);
            t_soc = this.createSetOperatorClause(v_gblw, from_sqs, to_sqs, 4);
            SelectQueryStatement t_sqs = to_sqs;

            for(SetOperatorClause t_soc = to_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
               t_sqs = t_soc.getSelectQueryStatement();
            }

            t_sqs.setSetOperatorClause(t_soc);
         }

         SelectQueryStatement t_sqs = to_sqs;

         for(SetOperatorClause t_soc = to_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
            t_sqs = t_soc.getSelectQueryStatement();
         }

         if (soc != null) {
            t_sqs.setSetOperatorClause(soc.toPostgreSQLSelect(to_sqs, from_sqs));
         }
      }

      if (this.WithOption != null && (this.WithOption.equalsIgnoreCase("ROLLUP") || this.WithOption.equalsIgnoreCase("CUBE"))) {
         FunctionCalls fc = new FunctionCalls();
         TableColumn tc = new TableColumn();
         tc.setColumnName(this.WithOption.toUpperCase());
         fc.setFunctionName(tc);
         fc.setFunctionArguments(v_gil);
         v_gblw = new Vector();
         v_gblw.add(fc);
         gbs.setGroupByItemList(v_gblw);
      }

      return gbs;
   }

   public static boolean isLiteralStringValuePresentInGroupby(SelectColumn sc) {
      Vector vc = sc.getColumnExpression();
      if (vc.size() == 1 && vc.elementAt(0) instanceof String) {
         String groupByVal = (String)vc.elementAt(0);
         if (groupByVal.startsWith("'") && groupByVal.endsWith("'")) {
            return true;
         }
      }

      return false;
   }

   public GroupByStatement toInformixSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      GroupByStatement gbs = new GroupByStatement();
      gbs.setGroupClause(this.GroupClause);
      Vector v_gil = new Vector();
      new Vector();
      SetOperatorClause t_soc;
      SetOperatorClause soc;
      if (this.GroupingSetClause == null) {
         int i_count;
         SelectColumn sc;
         if (this.ALLOption) {
            for(i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
               if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)this.GroupByItemList.elementAt(i_count);
                  v_gil.addElement(sc.toInformixSelect(to_sqs, from_sqs));
               }
            }

            gbs.setGroupByItemList(v_gil);
            soc = null;
            if (from_sqs.getSetOperatorClause() != null) {
               soc = from_sqs.getSetOperatorClause();
            }

            if (from_sqs.getWhereExpression() != null) {
               SetOperatorClause n_soc = this.createSetOperatorClause((Vector)null, from_sqs, to_sqs, 6);
               SelectQueryStatement t_sqs = to_sqs;
               to_sqs.setSetOperatorClause(n_soc);

               for(t_soc = to_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
                  t_sqs = t_soc.getSelectQueryStatement();
               }

               if (soc != null) {
                  t_sqs.setSetOperatorClause(soc.toInformixSelect(to_sqs, from_sqs));
               }
            } else if (soc != null) {
               to_sqs.setSetOperatorClause(soc.toInformixSelect(to_sqs, from_sqs));
            }
         } else {
            for(i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
               if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)this.GroupByItemList.elementAt(i_count);
                  v_gil.addElement(sc.toInformixSelect(to_sqs, from_sqs));
               }
            }

            gbs.setGroupByItemList(v_gil);
         }
      } else {
         soc = null;
         if (((Vector)this.GroupByItemList.elementAt(0)).size() == 0) {
            gbs = null;
         } else {
            Vector singleGroupingSet = (Vector)this.GroupByItemList.elementAt(0);

            for(int i_count = 0; i_count < singleGroupingSet.size(); ++i_count) {
               if (singleGroupingSet.elementAt(i_count) instanceof SelectColumn) {
                  SelectColumn sc = (SelectColumn)singleGroupingSet.elementAt(i_count);
                  v_gil.addElement(sc.toInformixSelect(to_sqs, from_sqs));
               }
            }

            gbs.setGroupByItemList(v_gil);
         }

         this.makeNonGroupedSelectItemsNull((Vector)this.GroupByItemList.elementAt(0), from_sqs, to_sqs, 6);
         if (from_sqs.getSetOperatorClause() != null) {
            soc = from_sqs.getSetOperatorClause();
         }

         for(int i_count = 1; i_count < this.GroupByItemList.size(); ++i_count) {
            Vector v_item_list = (Vector)this.GroupByItemList.elementAt(i_count);
            t_soc = this.createSetOperatorClause(v_item_list, from_sqs, to_sqs, 6);
            SelectQueryStatement t_sqs = to_sqs;

            for(SetOperatorClause t_soc = to_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
               t_sqs = t_soc.getSelectQueryStatement();
            }

            t_sqs.setSetOperatorClause(t_soc);
         }

         SelectQueryStatement t_sqs = to_sqs;

         for(SetOperatorClause t_soc = to_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
            t_sqs = t_soc.getSelectQueryStatement();
         }

         if (soc != null) {
            t_sqs.setSetOperatorClause(soc.toInformixSelect(to_sqs, from_sqs));
         }
      }

      return gbs;
   }

   public GroupByStatement toSnowflakeSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      GroupByStatement gbs = new GroupByStatement();
      gbs.setGroupClause(this.GroupClause);
      Vector v_gil = new Vector();
      new Vector();
      SetOperatorClause t_soc;
      SetOperatorClause soc;
      int i_count;
      SelectColumn sc;
      Vector v_gblw;
      if (this.GroupingSetClause == null) {
         SelectColumn sc;
         if (this.ALLOption) {
            for(int i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
               if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)this.GroupByItemList.elementAt(i_count);
                  v_gil.addElement(sc.toSnowflakeSelect(to_sqs, from_sqs));
               }
            }

            gbs.setGroupByItemList(v_gil);
            soc = null;
            if (from_sqs.getSetOperatorClause() != null) {
               soc = from_sqs.getSetOperatorClause();
            }

            if (from_sqs.getWhereExpression() != null) {
               SetOperatorClause n_soc = this.createSetOperatorClause((Vector)null, from_sqs, to_sqs, 15);
               SelectQueryStatement t_sqs = to_sqs;
               to_sqs.setSetOperatorClause(n_soc);

               for(t_soc = to_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
                  t_sqs = t_soc.getSelectQueryStatement();
               }

               if (soc != null) {
                  t_sqs.setSetOperatorClause(soc.toSnowflakeSelect(to_sqs, from_sqs));
               }
            } else if (soc != null) {
               to_sqs.setSetOperatorClause(soc.toSnowflakeSelect(to_sqs, from_sqs));
            }
         } else {
            boolean rollupAdded = false;
            sc = null;

            for(i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
               rollupAdded = false;
               Vector newSelectItemList;
               Vector newArguments;
               SelectColumn orgSC;
               if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)this.GroupByItemList.elementAt(i_count);
                  Vector columnExpressionVector = sc.getColumnExpression();

                  for(int i = 0; i < columnExpressionVector.size(); ++i) {
                     if (columnExpressionVector.size() == 1 && columnExpressionVector.elementAt(i) instanceof FunctionCalls) {
                        FunctionCalls fc = (FunctionCalls)columnExpressionVector.elementAt(i);
                        String s_fn = fc.getFunctionName().getColumnName();
                        if (s_fn.equalsIgnoreCase("cube") || s_fn.equalsIgnoreCase("rollup")) {
                           Vector selectItemList = to_sqs.getSelectStatement().getSelectItemList();
                           newSelectItemList = new Vector();
                           if (selectItemList != null) {
                              for(int ii = 0; ii < selectItemList.size(); ++ii) {
                                 if (selectItemList.get(i) instanceof SelectColumn) {
                                    orgSC = (SelectColumn)selectItemList.get(ii);
                                    Vector columnExpression = orgSC.getColumnExpression();
                                    Vector newColumnExp = new Vector();
                                    SelectColumn newSC = new SelectColumn();
                                    this.copyFromOneSCToAnother(columnExpression, newColumnExp);
                                    newSC.setColumnExpression(newColumnExp);
                                    newSC.setIsAS(orgSC.getIsAS());
                                    newSC.setAliasName(orgSC.getAliasName());
                                    if (selectItemList.size() - 1 > ii) {
                                       newSC.setEndsWith(",");
                                    }

                                    newSelectItemList.add(newSC);
                                 }
                              }
                           }

                           this.processCubeAndRollupConversion(fc, from_sqs, to_sqs, 15);
                           to_sqs.getSelectStatement().setSelectItemList(newSelectItemList);
                           rollupAdded = true;
                           if (fc.getFunctionArguments() != null && fc.getFunctionArguments().size() > 0) {
                              newArguments = new Vector();

                              for(int j = 0; j < fc.getFunctionArguments().size(); ++j) {
                                 if (j > 0) {
                                    newArguments.add(", ");
                                 }

                                 newArguments.add(fc.getFunctionArguments().get(j));
                              }

                              sc.setColumnExpression(newArguments);
                              v_gil.addElement(sc.toSnowflakeSelect(to_sqs, from_sqs));
                           }
                        }
                     }
                  }

                  if (!rollupAdded) {
                     v_gil.addElement(sc.toSnowflakeSelect(to_sqs, from_sqs));
                  }
               } else if (this.GroupByItemList.elementAt(i_count) instanceof FunctionCalls) {
                  FunctionCalls fc = (FunctionCalls)this.GroupByItemList.elementAt(i_count);
                  String s_fn = fc.getFunctionName().getColumnName();
                  if (!s_fn.equalsIgnoreCase("cube") && !s_fn.equalsIgnoreCase("rollup")) {
                     v_gil.addElement(fc.toSnowflakeSelect(to_sqs, from_sqs));
                  } else {
                     Vector selectItemList = to_sqs.getSelectStatement().getSelectItemList();
                     Vector newSelectItemList = new Vector();
                     if (selectItemList != null) {
                        for(int i = 0; i < selectItemList.size(); ++i) {
                           if (selectItemList.get(i) instanceof SelectColumn) {
                              SelectColumn orgSC = (SelectColumn)selectItemList.get(i);
                              newSelectItemList = orgSC.getColumnExpression();
                              newArguments = new Vector();
                              orgSC = new SelectColumn();
                              this.copyFromOneSCToAnother(newSelectItemList, newArguments);
                              orgSC.setColumnExpression(newArguments);
                              orgSC.setIsAS(orgSC.getIsAS());
                              orgSC.setAliasName(orgSC.getAliasName());
                              newSelectItemList.add(orgSC);
                              if (selectItemList.size() - 1 > i) {
                                 orgSC.setEndsWith(",");
                              }
                           }
                        }
                     }

                     this.processCubeAndRollupConversion(fc, from_sqs, to_sqs, 15);
                     to_sqs.getSelectStatement().setSelectItemList(newSelectItemList);
                  }
               }
            }

            gbs.setGroupByItemList(v_gil);
         }
      } else {
         soc = null;
         if (((Vector)this.GroupByItemList.elementAt(0)).size() == 0) {
            gbs = null;
         } else {
            Vector singleGroupingSet = (Vector)this.GroupByItemList.elementAt(0);

            for(i_count = 0; i_count < singleGroupingSet.size(); ++i_count) {
               if (singleGroupingSet.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)singleGroupingSet.elementAt(i_count);
                  v_gil.addElement(sc.toSnowflakeSelect(to_sqs, from_sqs));
               }
            }

            gbs.setGroupByItemList(v_gil);
         }

         this.makeNonGroupedSelectItemsNull((Vector)this.GroupByItemList.elementAt(0), from_sqs, to_sqs, 15);
         if (from_sqs.getSetOperatorClause() != null) {
            soc = from_sqs.getSetOperatorClause();
         }

         for(int i_count = 1; i_count < this.GroupByItemList.size(); ++i_count) {
            v_gblw = (Vector)this.GroupByItemList.elementAt(i_count);
            t_soc = this.createSetOperatorClause(v_gblw, from_sqs, to_sqs, 15);
            SelectQueryStatement t_sqs = to_sqs;

            for(SetOperatorClause t_soc = to_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
               t_sqs = t_soc.getSelectQueryStatement();
            }

            t_sqs.setSetOperatorClause(t_soc);
         }

         SelectQueryStatement t_sqs = to_sqs;

         for(SetOperatorClause t_soc = to_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
            t_sqs = t_soc.getSelectQueryStatement();
         }

         if (soc != null) {
            t_sqs.setSetOperatorClause(soc.toSnowflakeSelect(to_sqs, from_sqs));
         }
      }

      if (this.WithOption != null && (this.WithOption.equalsIgnoreCase("ROLLUP") || this.WithOption.equalsIgnoreCase("CUBE"))) {
         FunctionCalls fc = new FunctionCalls();
         TableColumn tc = new TableColumn();
         tc.setColumnName(this.WithOption.toUpperCase());
         fc.setFunctionName(tc);
         fc.setFunctionArguments(v_gil);
         v_gblw = new Vector();
         v_gblw.add(fc);
         gbs.setGroupByItemList(v_gblw);
      }

      return gbs;
   }

   public GroupByStatement toAthenaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      GroupByStatement gbs = new GroupByStatement();
      gbs.setGroupClause(this.GroupClause);
      Vector v_gil = new Vector();
      new Vector();
      SetOperatorClause t_soc;
      SetOperatorClause soc;
      int i_count;
      SelectColumn sc;
      Vector v_gblw;
      if (this.GroupingSetClause == null) {
         SelectColumn sc;
         if (this.ALLOption) {
            for(int i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
               if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)this.GroupByItemList.elementAt(i_count);
                  this.covertAilasToTableName(sc, from_sqs);
                  v_gil.addElement(sc.toAthenaSelect(to_sqs, from_sqs));
               }
            }

            gbs.setGroupByItemList(v_gil);
            soc = null;
            if (from_sqs.getSetOperatorClause() != null) {
               soc = from_sqs.getSetOperatorClause();
            }

            if (from_sqs.getWhereExpression() != null) {
               SetOperatorClause n_soc = this.createSetOperatorClause((Vector)null, from_sqs, to_sqs, 16);
               SelectQueryStatement t_sqs = to_sqs;
               to_sqs.setSetOperatorClause(n_soc);

               for(t_soc = to_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
                  t_sqs = t_soc.getSelectQueryStatement();
               }

               if (soc != null) {
                  t_sqs.setSetOperatorClause(soc.toAthenaSelect(to_sqs, from_sqs));
               }
            } else if (soc != null) {
               to_sqs.setSetOperatorClause(soc.toAthenaSelect(to_sqs, from_sqs));
            }
         } else {
            boolean rollupAdded = false;
            sc = null;

            for(i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
               rollupAdded = false;
               Vector newSelectItemList;
               Vector newArguments;
               SelectColumn orgSC;
               if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)this.GroupByItemList.elementAt(i_count);
                  this.covertAilasToTableName(sc, from_sqs);
                  Vector columnExpressionVector = sc.getColumnExpression();

                  for(int i = 0; i < columnExpressionVector.size(); ++i) {
                     if (columnExpressionVector.size() == 1 && columnExpressionVector.elementAt(i) instanceof FunctionCalls) {
                        FunctionCalls fc = (FunctionCalls)columnExpressionVector.elementAt(i);
                        String s_fn = fc.getFunctionName().getColumnName();
                        if (s_fn.equalsIgnoreCase("cube") || s_fn.equalsIgnoreCase("rollup")) {
                           Vector selectItemList = to_sqs.getSelectStatement().getSelectItemList();
                           newSelectItemList = new Vector();
                           if (selectItemList != null) {
                              for(int ii = 0; ii < selectItemList.size(); ++ii) {
                                 if (selectItemList.get(i) instanceof SelectColumn) {
                                    orgSC = (SelectColumn)selectItemList.get(ii);
                                    Vector columnExpression = orgSC.getColumnExpression();
                                    Vector newColumnExp = new Vector();
                                    SelectColumn newSC = new SelectColumn();
                                    this.copyFromOneSCToAnother(columnExpression, newColumnExp);
                                    newSC.setColumnExpression(newColumnExp);
                                    newSC.setIsAS(orgSC.getIsAS());
                                    newSC.setAliasName(orgSC.getAliasName());
                                    if (selectItemList.size() - 1 > ii) {
                                       newSC.setEndsWith(",");
                                    }

                                    newSelectItemList.add(newSC);
                                 }
                              }
                           }

                           this.processCubeAndRollupConversion(fc, from_sqs, to_sqs, 16);
                           to_sqs.getSelectStatement().setSelectItemList(newSelectItemList);
                           rollupAdded = true;
                           if (fc.getFunctionArguments() != null && fc.getFunctionArguments().size() > 0) {
                              newArguments = new Vector();

                              for(int j = 0; j < fc.getFunctionArguments().size(); ++j) {
                                 if (j > 0) {
                                    newArguments.add(", ");
                                 }

                                 newArguments.add(fc.getFunctionArguments().get(j));
                              }

                              sc.setColumnExpression(newArguments);
                              v_gil.addElement(sc.toAthenaSelect(to_sqs, from_sqs));
                           }
                        }
                     }
                  }

                  if (!rollupAdded) {
                     v_gil.addElement(sc.toAthenaSelect(to_sqs, from_sqs));
                  }
               } else if (this.GroupByItemList.elementAt(i_count) instanceof FunctionCalls) {
                  FunctionCalls fc = (FunctionCalls)this.GroupByItemList.elementAt(i_count);
                  String s_fn = fc.getFunctionName().getColumnName();
                  if (!s_fn.equalsIgnoreCase("cube") && !s_fn.equalsIgnoreCase("rollup")) {
                     v_gil.addElement(fc.toAthenaSelect(to_sqs, from_sqs));
                  } else {
                     Vector selectItemList = to_sqs.getSelectStatement().getSelectItemList();
                     Vector newSelectItemList = new Vector();
                     if (selectItemList != null) {
                        for(int i = 0; i < selectItemList.size(); ++i) {
                           if (selectItemList.get(i) instanceof SelectColumn) {
                              SelectColumn orgSC = (SelectColumn)selectItemList.get(i);
                              newSelectItemList = orgSC.getColumnExpression();
                              newArguments = new Vector();
                              orgSC = new SelectColumn();
                              this.copyFromOneSCToAnother(newSelectItemList, newArguments);
                              orgSC.setColumnExpression(newArguments);
                              orgSC.setIsAS(orgSC.getIsAS());
                              orgSC.setAliasName(orgSC.getAliasName());
                              newSelectItemList.add(orgSC);
                              if (selectItemList.size() - 1 > i) {
                                 orgSC.setEndsWith(",");
                              }
                           }
                        }
                     }

                     this.processCubeAndRollupConversion(fc, from_sqs, to_sqs, 16);
                     to_sqs.getSelectStatement().setSelectItemList(newSelectItemList);
                  }
               }
            }

            gbs.setGroupByItemList(v_gil);
         }
      } else {
         soc = null;
         if (((Vector)this.GroupByItemList.elementAt(0)).size() == 0) {
            gbs = null;
         } else {
            Vector singleGroupingSet = (Vector)this.GroupByItemList.elementAt(0);

            for(i_count = 0; i_count < singleGroupingSet.size(); ++i_count) {
               if (singleGroupingSet.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)singleGroupingSet.elementAt(i_count);
                  this.covertAilasToTableName(sc, from_sqs);
                  v_gil.addElement(sc.toAthenaSelect(to_sqs, from_sqs));
               }
            }

            gbs.setGroupByItemList(v_gil);
         }

         this.makeNonGroupedSelectItemsNull((Vector)this.GroupByItemList.elementAt(0), from_sqs, to_sqs, 16);
         if (from_sqs.getSetOperatorClause() != null) {
            soc = from_sqs.getSetOperatorClause();
         }

         for(int i_count = 1; i_count < this.GroupByItemList.size(); ++i_count) {
            v_gblw = (Vector)this.GroupByItemList.elementAt(i_count);
            t_soc = this.createSetOperatorClause(v_gblw, from_sqs, to_sqs, 16);
            SelectQueryStatement t_sqs = to_sqs;

            for(SetOperatorClause t_soc = to_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
               t_sqs = t_soc.getSelectQueryStatement();
            }

            t_sqs.setSetOperatorClause(t_soc);
         }

         SelectQueryStatement t_sqs = to_sqs;

         for(SetOperatorClause t_soc = to_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
            t_sqs = t_soc.getSelectQueryStatement();
         }

         if (soc != null) {
            t_sqs.setSetOperatorClause(soc.toAthenaSelect(to_sqs, from_sqs));
         }
      }

      if (this.WithOption != null && (this.WithOption.equalsIgnoreCase("ROLLUP") || this.WithOption.equalsIgnoreCase("CUBE"))) {
         FunctionCalls fc = new FunctionCalls();
         TableColumn tc = new TableColumn();
         tc.setColumnName(this.WithOption.toUpperCase());
         fc.setFunctionName(tc);
         fc.setFunctionArguments(v_gil);
         v_gblw = new Vector();
         v_gblw.add(fc);
         gbs.setGroupByItemList(v_gblw);
      }

      return gbs;
   }

   public GroupByStatement toSapHanaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      GroupByStatement gbs = new GroupByStatement();
      gbs.setGroupClause(this.GroupClause);
      Vector v_gil = new Vector();
      new Vector();
      SetOperatorClause t_soc;
      SetOperatorClause soc;
      int i_count;
      SelectColumn sc;
      Vector v_gblw;
      if (this.GroupingSetClause == null) {
         SelectColumn sc;
         if (this.ALLOption) {
            for(int i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
               if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)this.GroupByItemList.elementAt(i_count);
                  this.covertAilasToTableName(sc, from_sqs);
                  v_gil.addElement(sc.toSapHanaSelect(to_sqs, from_sqs));
               }
            }

            gbs.setGroupByItemList(v_gil);
            soc = null;
            if (from_sqs.getSetOperatorClause() != null) {
               soc = from_sqs.getSetOperatorClause();
            }

            if (from_sqs.getWhereExpression() != null) {
               SetOperatorClause n_soc = this.createSetOperatorClause((Vector)null, from_sqs, to_sqs, 17);
               SelectQueryStatement t_sqs = to_sqs;
               to_sqs.setSetOperatorClause(n_soc);

               for(t_soc = to_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
                  t_sqs = t_soc.getSelectQueryStatement();
               }

               if (soc != null) {
                  t_sqs.setSetOperatorClause(soc.toSapHanaSelect(to_sqs, from_sqs));
               }
            } else if (soc != null) {
               to_sqs.setSetOperatorClause(soc.toSapHanaSelect(to_sqs, from_sqs));
            }
         } else {
            boolean rollupAdded = false;
            sc = null;

            for(i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
               rollupAdded = false;
               Vector newSelectItemList;
               Vector newArguments;
               SelectColumn orgSC;
               if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)this.GroupByItemList.elementAt(i_count);
                  this.covertAilasToTableName(sc, from_sqs);
                  Vector columnExpressionVector = sc.getColumnExpression();

                  for(int i = 0; i < columnExpressionVector.size(); ++i) {
                     if (columnExpressionVector.size() == 1 && columnExpressionVector.elementAt(i) instanceof FunctionCalls) {
                        FunctionCalls fc = (FunctionCalls)columnExpressionVector.elementAt(i);
                        String s_fn = fc.getFunctionName().getColumnName();
                        if (s_fn.equalsIgnoreCase("cube") || s_fn.equalsIgnoreCase("rollup")) {
                           Vector selectItemList = to_sqs.getSelectStatement().getSelectItemList();
                           newSelectItemList = new Vector();
                           if (selectItemList != null) {
                              for(int ii = 0; ii < selectItemList.size(); ++ii) {
                                 if (selectItemList.get(i) instanceof SelectColumn) {
                                    orgSC = (SelectColumn)selectItemList.get(ii);
                                    Vector columnExpression = orgSC.getColumnExpression();
                                    Vector newColumnExp = new Vector();
                                    SelectColumn newSC = new SelectColumn();
                                    this.copyFromOneSCToAnother(columnExpression, newColumnExp);
                                    newSC.setColumnExpression(newColumnExp);
                                    newSC.setIsAS(orgSC.getIsAS());
                                    newSC.setAliasName(orgSC.getAliasName());
                                    if (selectItemList.size() - 1 > ii) {
                                       newSC.setEndsWith(",");
                                    }

                                    newSelectItemList.add(newSC);
                                 }
                              }
                           }

                           this.processCubeAndRollupConversion(fc, from_sqs, to_sqs, 17);
                           to_sqs.getSelectStatement().setSelectItemList(newSelectItemList);
                           rollupAdded = true;
                           if (fc.getFunctionArguments() != null && fc.getFunctionArguments().size() > 0) {
                              newArguments = new Vector();

                              for(int j = 0; j < fc.getFunctionArguments().size(); ++j) {
                                 if (j > 0) {
                                    newArguments.add(", ");
                                 }

                                 newArguments.add(fc.getFunctionArguments().get(j));
                              }

                              sc.setColumnExpression(newArguments);
                              v_gil.addElement(sc.toSapHanaSelect(to_sqs, from_sqs));
                           }
                        }
                     }
                  }

                  if (!rollupAdded) {
                     v_gil.addElement(sc.toSapHanaSelect(to_sqs, from_sqs));
                  }
               } else if (this.GroupByItemList.elementAt(i_count) instanceof FunctionCalls) {
                  FunctionCalls fc = (FunctionCalls)this.GroupByItemList.elementAt(i_count);
                  String s_fn = fc.getFunctionName().getColumnName();
                  if (!s_fn.equalsIgnoreCase("cube") && !s_fn.equalsIgnoreCase("rollup")) {
                     v_gil.addElement(fc.toSapHanaSelect(to_sqs, from_sqs));
                  } else {
                     Vector selectItemList = to_sqs.getSelectStatement().getSelectItemList();
                     Vector newSelectItemList = new Vector();
                     if (selectItemList != null) {
                        for(int i = 0; i < selectItemList.size(); ++i) {
                           if (selectItemList.get(i) instanceof SelectColumn) {
                              SelectColumn orgSC = (SelectColumn)selectItemList.get(i);
                              newSelectItemList = orgSC.getColumnExpression();
                              newArguments = new Vector();
                              orgSC = new SelectColumn();
                              this.copyFromOneSCToAnother(newSelectItemList, newArguments);
                              orgSC.setColumnExpression(newArguments);
                              orgSC.setIsAS(orgSC.getIsAS());
                              orgSC.setAliasName(orgSC.getAliasName());
                              newSelectItemList.add(orgSC);
                              if (selectItemList.size() - 1 > i) {
                                 orgSC.setEndsWith(",");
                              }
                           }
                        }
                     }

                     this.processCubeAndRollupConversion(fc, from_sqs, to_sqs, 17);
                     to_sqs.getSelectStatement().setSelectItemList(newSelectItemList);
                  }
               }
            }

            gbs.setGroupByItemList(v_gil);
         }
      } else {
         soc = null;
         if (((Vector)this.GroupByItemList.elementAt(0)).size() == 0) {
            gbs = null;
         } else {
            Vector singleGroupingSet = (Vector)this.GroupByItemList.elementAt(0);

            for(i_count = 0; i_count < singleGroupingSet.size(); ++i_count) {
               if (singleGroupingSet.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)singleGroupingSet.elementAt(i_count);
                  this.covertAilasToTableName(sc, from_sqs);
                  v_gil.addElement(sc.toSapHanaSelect(to_sqs, from_sqs));
               }
            }

            gbs.setGroupByItemList(v_gil);
         }

         this.makeNonGroupedSelectItemsNull((Vector)this.GroupByItemList.elementAt(0), from_sqs, to_sqs, 17);
         if (from_sqs.getSetOperatorClause() != null) {
            soc = from_sqs.getSetOperatorClause();
         }

         for(int i_count = 1; i_count < this.GroupByItemList.size(); ++i_count) {
            v_gblw = (Vector)this.GroupByItemList.elementAt(i_count);
            t_soc = this.createSetOperatorClause(v_gblw, from_sqs, to_sqs, 17);
            SelectQueryStatement t_sqs = to_sqs;

            for(SetOperatorClause t_soc = to_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
               t_sqs = t_soc.getSelectQueryStatement();
            }

            t_sqs.setSetOperatorClause(t_soc);
         }

         SelectQueryStatement t_sqs = to_sqs;

         for(SetOperatorClause t_soc = to_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
            t_sqs = t_soc.getSelectQueryStatement();
         }

         if (soc != null) {
            t_sqs.setSetOperatorClause(soc.toSapHanaSelect(to_sqs, from_sqs));
         }
      }

      if (this.WithOption != null && (this.WithOption.equalsIgnoreCase("ROLLUP") || this.WithOption.equalsIgnoreCase("CUBE"))) {
         FunctionCalls fc = new FunctionCalls();
         TableColumn tc = new TableColumn();
         tc.setColumnName(this.WithOption.toUpperCase());
         fc.setFunctionName(tc);
         fc.setFunctionArguments(v_gil);
         v_gblw = new Vector();
         v_gblw.add(fc);
         gbs.setGroupByItemList(v_gblw);
      }

      return gbs;
   }

   public GroupByStatement toSqliteSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      GroupByStatement gbs = new GroupByStatement();
      gbs.setGroupClause(this.GroupClause);
      Vector v_gil = new Vector();
      new Vector();
      SetOperatorClause t_soc;
      SetOperatorClause soc;
      int i_count;
      SelectColumn sc;
      Vector v_gblw;
      if (this.GroupingSetClause == null) {
         SelectColumn sc;
         if (this.ALLOption) {
            for(int i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
               if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)this.GroupByItemList.elementAt(i_count);
                  this.covertAilasToTableName(sc, from_sqs);
                  v_gil.addElement(sc.toSqliteSelect(to_sqs, from_sqs));
               }
            }

            gbs.setGroupByItemList(v_gil);
            soc = null;
            if (from_sqs.getSetOperatorClause() != null) {
               soc = from_sqs.getSetOperatorClause();
            }

            if (from_sqs.getWhereExpression() != null) {
               SetOperatorClause n_soc = this.createSetOperatorClause((Vector)null, from_sqs, to_sqs, 18);
               SelectQueryStatement t_sqs = to_sqs;
               to_sqs.setSetOperatorClause(n_soc);

               for(t_soc = to_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
                  t_sqs = t_soc.getSelectQueryStatement();
               }

               if (soc != null) {
                  t_sqs.setSetOperatorClause(soc.toSqliteSelect(to_sqs, from_sqs));
               }
            } else if (soc != null) {
               to_sqs.setSetOperatorClause(soc.toSqliteSelect(to_sqs, from_sqs));
            }
         } else {
            boolean rollupAdded = false;
            sc = null;

            for(i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
               rollupAdded = false;
               Vector newSelectItemList;
               Vector newArguments;
               SelectColumn orgSC;
               if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)this.GroupByItemList.elementAt(i_count);
                  this.covertAilasToTableName(sc, from_sqs);
                  Vector columnExpressionVector = sc.getColumnExpression();

                  for(int i = 0; i < columnExpressionVector.size(); ++i) {
                     if (columnExpressionVector.size() == 1 && columnExpressionVector.elementAt(i) instanceof FunctionCalls) {
                        FunctionCalls fc = (FunctionCalls)columnExpressionVector.elementAt(i);
                        String s_fn = fc.getFunctionName().getColumnName();
                        if (s_fn.equalsIgnoreCase("cube") || s_fn.equalsIgnoreCase("rollup")) {
                           Vector selectItemList = to_sqs.getSelectStatement().getSelectItemList();
                           newSelectItemList = new Vector();
                           if (selectItemList != null) {
                              for(int ii = 0; ii < selectItemList.size(); ++ii) {
                                 if (selectItemList.get(i) instanceof SelectColumn) {
                                    orgSC = (SelectColumn)selectItemList.get(ii);
                                    Vector columnExpression = orgSC.getColumnExpression();
                                    Vector newColumnExp = new Vector();
                                    SelectColumn newSC = new SelectColumn();
                                    this.copyFromOneSCToAnother(columnExpression, newColumnExp);
                                    newSC.setColumnExpression(newColumnExp);
                                    newSC.setIsAS(orgSC.getIsAS());
                                    newSC.setAliasName(orgSC.getAliasName());
                                    if (selectItemList.size() - 1 > ii) {
                                       newSC.setEndsWith(",");
                                    }

                                    newSelectItemList.add(newSC);
                                 }
                              }
                           }

                           this.processCubeAndRollupConversion(fc, from_sqs, to_sqs, 18);
                           to_sqs.getSelectStatement().setSelectItemList(newSelectItemList);
                           rollupAdded = true;
                           if (fc.getFunctionArguments() != null && fc.getFunctionArguments().size() > 0) {
                              newArguments = new Vector();

                              for(int j = 0; j < fc.getFunctionArguments().size(); ++j) {
                                 if (j > 0) {
                                    newArguments.add(", ");
                                 }

                                 newArguments.add(fc.getFunctionArguments().get(j));
                              }

                              sc.setColumnExpression(newArguments);
                              v_gil.addElement(sc.toSqliteSelect(to_sqs, from_sqs));
                           }
                        }
                     }
                  }

                  if (!rollupAdded) {
                     v_gil.addElement(sc.toSqliteSelect(to_sqs, from_sqs));
                  }
               } else if (this.GroupByItemList.elementAt(i_count) instanceof FunctionCalls) {
                  FunctionCalls fc = (FunctionCalls)this.GroupByItemList.elementAt(i_count);
                  String s_fn = fc.getFunctionName().getColumnName();
                  if (!s_fn.equalsIgnoreCase("cube") && !s_fn.equalsIgnoreCase("rollup")) {
                     v_gil.addElement(fc.toSqliteSelect(to_sqs, from_sqs));
                  } else {
                     Vector selectItemList = to_sqs.getSelectStatement().getSelectItemList();
                     Vector newSelectItemList = new Vector();
                     if (selectItemList != null) {
                        for(int i = 0; i < selectItemList.size(); ++i) {
                           if (selectItemList.get(i) instanceof SelectColumn) {
                              SelectColumn orgSC = (SelectColumn)selectItemList.get(i);
                              newSelectItemList = orgSC.getColumnExpression();
                              newArguments = new Vector();
                              orgSC = new SelectColumn();
                              this.copyFromOneSCToAnother(newSelectItemList, newArguments);
                              orgSC.setColumnExpression(newArguments);
                              orgSC.setIsAS(orgSC.getIsAS());
                              orgSC.setAliasName(orgSC.getAliasName());
                              newSelectItemList.add(orgSC);
                              if (selectItemList.size() - 1 > i) {
                                 orgSC.setEndsWith(",");
                              }
                           }
                        }
                     }

                     this.processCubeAndRollupConversion(fc, from_sqs, to_sqs, 18);
                     to_sqs.getSelectStatement().setSelectItemList(newSelectItemList);
                  }
               }
            }

            gbs.setGroupByItemList(v_gil);
         }
      } else {
         soc = null;
         if (((Vector)this.GroupByItemList.elementAt(0)).size() == 0) {
            gbs = null;
         } else {
            Vector singleGroupingSet = (Vector)this.GroupByItemList.elementAt(0);

            for(i_count = 0; i_count < singleGroupingSet.size(); ++i_count) {
               if (singleGroupingSet.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)singleGroupingSet.elementAt(i_count);
                  this.covertAilasToTableName(sc, from_sqs);
                  v_gil.addElement(sc.toSqliteSelect(to_sqs, from_sqs));
               }
            }

            gbs.setGroupByItemList(v_gil);
         }

         this.makeNonGroupedSelectItemsNull((Vector)this.GroupByItemList.elementAt(0), from_sqs, to_sqs, 18);
         if (from_sqs.getSetOperatorClause() != null) {
            soc = from_sqs.getSetOperatorClause();
         }

         for(int i_count = 1; i_count < this.GroupByItemList.size(); ++i_count) {
            v_gblw = (Vector)this.GroupByItemList.elementAt(i_count);
            t_soc = this.createSetOperatorClause(v_gblw, from_sqs, to_sqs, 18);
            SelectQueryStatement t_sqs = to_sqs;

            for(SetOperatorClause t_soc = to_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
               t_sqs = t_soc.getSelectQueryStatement();
            }

            t_sqs.setSetOperatorClause(t_soc);
         }

         SelectQueryStatement t_sqs = to_sqs;

         for(SetOperatorClause t_soc = to_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
            t_sqs = t_soc.getSelectQueryStatement();
         }

         if (soc != null) {
            t_sqs.setSetOperatorClause(soc.toSqliteSelect(to_sqs, from_sqs));
         }
      }

      if (this.WithOption != null && (this.WithOption.equalsIgnoreCase("ROLLUP") || this.WithOption.equalsIgnoreCase("CUBE"))) {
         FunctionCalls fc = new FunctionCalls();
         TableColumn tc = new TableColumn();
         tc.setColumnName(this.WithOption.toUpperCase());
         fc.setFunctionName(tc);
         fc.setFunctionArguments(v_gil);
         v_gblw = new Vector();
         v_gblw.add(fc);
         gbs.setGroupByItemList(v_gblw);
      }

      return gbs;
   }

   public GroupByStatement toExcelSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      GroupByStatement gbs = new GroupByStatement();
      gbs.setGroupClause(this.GroupClause);
      Vector v_gil = new Vector();
      new Vector();
      SetOperatorClause t_soc;
      SetOperatorClause soc;
      int i_count;
      SelectColumn sc;
      Vector v_gblw;
      if (this.GroupingSetClause == null) {
         SelectColumn sc;
         if (this.ALLOption) {
            for(int i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
               if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)this.GroupByItemList.elementAt(i_count);
                  this.covertAilasToTableName(sc, from_sqs);
                  v_gil.addElement(sc.toExcelSelect(to_sqs, from_sqs));
               }
            }

            gbs.setGroupByItemList(v_gil);
            soc = null;
            if (from_sqs.getSetOperatorClause() != null) {
               soc = from_sqs.getSetOperatorClause();
            }

            if (from_sqs.getWhereExpression() != null) {
               SetOperatorClause n_soc = this.createSetOperatorClause((Vector)null, from_sqs, to_sqs, 20);
               SelectQueryStatement t_sqs = to_sqs;
               to_sqs.setSetOperatorClause(n_soc);

               for(t_soc = to_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
                  t_sqs = t_soc.getSelectQueryStatement();
               }

               if (soc != null) {
                  t_sqs.setSetOperatorClause(soc.toExcelSelect(to_sqs, from_sqs));
               }
            } else if (soc != null) {
               to_sqs.setSetOperatorClause(soc.toExcelSelect(to_sqs, from_sqs));
            }
         } else {
            boolean rollupAdded = false;
            sc = null;

            for(i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
               rollupAdded = false;
               Vector newSelectItemList;
               Vector newArguments;
               SelectColumn orgSC;
               if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)this.GroupByItemList.elementAt(i_count);
                  this.covertAilasToTableName(sc, from_sqs);
                  Vector columnExpressionVector = sc.getColumnExpression();

                  for(int i = 0; i < columnExpressionVector.size(); ++i) {
                     if (columnExpressionVector.size() == 1 && columnExpressionVector.elementAt(i) instanceof FunctionCalls) {
                        FunctionCalls fc = (FunctionCalls)columnExpressionVector.elementAt(i);
                        String s_fn = fc.getFunctionName().getColumnName();
                        if (s_fn.equalsIgnoreCase("cube") || s_fn.equalsIgnoreCase("rollup")) {
                           Vector selectItemList = to_sqs.getSelectStatement().getSelectItemList();
                           newSelectItemList = new Vector();
                           if (selectItemList != null) {
                              for(int ii = 0; ii < selectItemList.size(); ++ii) {
                                 if (selectItemList.get(i) instanceof SelectColumn) {
                                    orgSC = (SelectColumn)selectItemList.get(ii);
                                    Vector columnExpression = orgSC.getColumnExpression();
                                    Vector newColumnExp = new Vector();
                                    SelectColumn newSC = new SelectColumn();
                                    this.copyFromOneSCToAnother(columnExpression, newColumnExp);
                                    newSC.setColumnExpression(newColumnExp);
                                    newSC.setIsAS(orgSC.getIsAS());
                                    newSC.setAliasName(orgSC.getAliasName());
                                    if (selectItemList.size() - 1 > ii) {
                                       newSC.setEndsWith(",");
                                    }

                                    newSelectItemList.add(newSC);
                                 }
                              }
                           }

                           this.processCubeAndRollupConversion(fc, from_sqs, to_sqs, 20);
                           to_sqs.getSelectStatement().setSelectItemList(newSelectItemList);
                           rollupAdded = true;
                           if (fc.getFunctionArguments() != null && fc.getFunctionArguments().size() > 0) {
                              newArguments = new Vector();

                              for(int j = 0; j < fc.getFunctionArguments().size(); ++j) {
                                 if (j > 0) {
                                    newArguments.add(", ");
                                 }

                                 newArguments.add(fc.getFunctionArguments().get(j));
                              }

                              sc.setColumnExpression(newArguments);
                              v_gil.addElement(sc.toExcelSelect(to_sqs, from_sqs));
                           }
                        }
                     }
                  }

                  if (!rollupAdded) {
                     v_gil.addElement(sc.toExcelSelect(to_sqs, from_sqs));
                  }
               } else if (this.GroupByItemList.elementAt(i_count) instanceof FunctionCalls) {
                  FunctionCalls fc = (FunctionCalls)this.GroupByItemList.elementAt(i_count);
                  String s_fn = fc.getFunctionName().getColumnName();
                  if (!s_fn.equalsIgnoreCase("cube") && !s_fn.equalsIgnoreCase("rollup")) {
                     v_gil.addElement(fc.toExcelSelect(to_sqs, from_sqs));
                  } else {
                     Vector selectItemList = to_sqs.getSelectStatement().getSelectItemList();
                     Vector newSelectItemList = new Vector();
                     if (selectItemList != null) {
                        for(int i = 0; i < selectItemList.size(); ++i) {
                           if (selectItemList.get(i) instanceof SelectColumn) {
                              SelectColumn orgSC = (SelectColumn)selectItemList.get(i);
                              newSelectItemList = orgSC.getColumnExpression();
                              newArguments = new Vector();
                              orgSC = new SelectColumn();
                              this.copyFromOneSCToAnother(newSelectItemList, newArguments);
                              orgSC.setColumnExpression(newArguments);
                              orgSC.setIsAS(orgSC.getIsAS());
                              orgSC.setAliasName(orgSC.getAliasName());
                              newSelectItemList.add(orgSC);
                              if (selectItemList.size() - 1 > i) {
                                 orgSC.setEndsWith(",");
                              }
                           }
                        }
                     }

                     this.processCubeAndRollupConversion(fc, from_sqs, to_sqs, 20);
                     to_sqs.getSelectStatement().setSelectItemList(newSelectItemList);
                  }
               }
            }

            gbs.setGroupByItemList(v_gil);
         }
      } else {
         soc = null;
         if (((Vector)this.GroupByItemList.elementAt(0)).size() == 0) {
            gbs = null;
         } else {
            Vector singleGroupingSet = (Vector)this.GroupByItemList.elementAt(0);

            for(i_count = 0; i_count < singleGroupingSet.size(); ++i_count) {
               if (singleGroupingSet.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)singleGroupingSet.elementAt(i_count);
                  this.covertAilasToTableName(sc, from_sqs);
                  v_gil.addElement(sc.toExcelSelect(to_sqs, from_sqs));
               }
            }

            gbs.setGroupByItemList(v_gil);
         }

         this.makeNonGroupedSelectItemsNull((Vector)this.GroupByItemList.elementAt(0), from_sqs, to_sqs, 20);
         if (from_sqs.getSetOperatorClause() != null) {
            soc = from_sqs.getSetOperatorClause();
         }

         for(int i_count = 1; i_count < this.GroupByItemList.size(); ++i_count) {
            v_gblw = (Vector)this.GroupByItemList.elementAt(i_count);
            t_soc = this.createSetOperatorClause(v_gblw, from_sqs, to_sqs, 20);
            SelectQueryStatement t_sqs = to_sqs;

            for(SetOperatorClause t_soc = to_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
               t_sqs = t_soc.getSelectQueryStatement();
            }

            t_sqs.setSetOperatorClause(t_soc);
         }

         SelectQueryStatement t_sqs = to_sqs;

         for(SetOperatorClause t_soc = to_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
            t_sqs = t_soc.getSelectQueryStatement();
         }

         if (soc != null) {
            t_sqs.setSetOperatorClause(soc.toExcelSelect(to_sqs, from_sqs));
         }
      }

      if (this.WithOption != null && (this.WithOption.equalsIgnoreCase("ROLLUP") || this.WithOption.equalsIgnoreCase("CUBE"))) {
         FunctionCalls fc = new FunctionCalls();
         TableColumn tc = new TableColumn();
         tc.setColumnName(this.WithOption.toUpperCase());
         fc.setFunctionName(tc);
         fc.setFunctionArguments(v_gil);
         v_gblw = new Vector();
         v_gblw.add(fc);
         gbs.setGroupByItemList(v_gblw);
      }

      return gbs;
   }

   public GroupByStatement toMsAccessJdbcSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      GroupByStatement gbs = new GroupByStatement();
      gbs.setGroupClause(this.GroupClause);
      Vector v_gil = new Vector();
      new Vector();
      SetOperatorClause t_soc;
      SetOperatorClause soc;
      int i_count;
      SelectColumn sc;
      Vector v_gblw;
      if (this.GroupingSetClause == null) {
         SelectColumn sc;
         if (this.ALLOption) {
            for(int i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
               if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)this.GroupByItemList.elementAt(i_count);
                  this.covertAilasToTableName(sc, from_sqs);
                  v_gil.addElement(sc.toMsAccessJdbcSelect(to_sqs, from_sqs));
               }
            }

            gbs.setGroupByItemList(v_gil);
            soc = null;
            if (from_sqs.getSetOperatorClause() != null) {
               soc = from_sqs.getSetOperatorClause();
            }

            if (from_sqs.getWhereExpression() != null) {
               SetOperatorClause n_soc = this.createSetOperatorClause((Vector)null, from_sqs, to_sqs, 21);
               SelectQueryStatement t_sqs = to_sqs;
               to_sqs.setSetOperatorClause(n_soc);

               for(t_soc = to_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
                  t_sqs = t_soc.getSelectQueryStatement();
               }

               if (soc != null) {
                  t_sqs.setSetOperatorClause(soc.toMsAccessJdbcSelect(to_sqs, from_sqs));
               }
            } else if (soc != null) {
               to_sqs.setSetOperatorClause(soc.toMsAccessJdbcSelect(to_sqs, from_sqs));
            }
         } else {
            boolean rollupAdded = false;
            sc = null;

            for(i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
               rollupAdded = false;
               Vector newSelectItemList;
               Vector newArguments;
               SelectColumn orgSC;
               if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)this.GroupByItemList.elementAt(i_count);
                  this.covertAilasToTableName(sc, from_sqs);
                  Vector columnExpressionVector = sc.getColumnExpression();

                  for(int i = 0; i < columnExpressionVector.size(); ++i) {
                     if (columnExpressionVector.size() == 1 && columnExpressionVector.elementAt(i) instanceof FunctionCalls) {
                        FunctionCalls fc = (FunctionCalls)columnExpressionVector.elementAt(i);
                        String s_fn = fc.getFunctionName().getColumnName();
                        if (s_fn.equalsIgnoreCase("cube") || s_fn.equalsIgnoreCase("rollup")) {
                           Vector selectItemList = to_sqs.getSelectStatement().getSelectItemList();
                           newSelectItemList = new Vector();
                           if (selectItemList != null) {
                              for(int ii = 0; ii < selectItemList.size(); ++ii) {
                                 if (selectItemList.get(i) instanceof SelectColumn) {
                                    orgSC = (SelectColumn)selectItemList.get(ii);
                                    Vector columnExpression = orgSC.getColumnExpression();
                                    Vector newColumnExp = new Vector();
                                    SelectColumn newSC = new SelectColumn();
                                    this.copyFromOneSCToAnother(columnExpression, newColumnExp);
                                    newSC.setColumnExpression(newColumnExp);
                                    newSC.setIsAS(orgSC.getIsAS());
                                    newSC.setAliasName(orgSC.getAliasName());
                                    if (selectItemList.size() - 1 > ii) {
                                       newSC.setEndsWith(",");
                                    }

                                    newSelectItemList.add(newSC);
                                 }
                              }
                           }

                           this.processCubeAndRollupConversion(fc, from_sqs, to_sqs, 21);
                           to_sqs.getSelectStatement().setSelectItemList(newSelectItemList);
                           rollupAdded = true;
                           if (fc.getFunctionArguments() != null && fc.getFunctionArguments().size() > 0) {
                              newArguments = new Vector();

                              for(int j = 0; j < fc.getFunctionArguments().size(); ++j) {
                                 if (j > 0) {
                                    newArguments.add(", ");
                                 }

                                 newArguments.add(fc.getFunctionArguments().get(j));
                              }

                              sc.setColumnExpression(newArguments);
                              v_gil.addElement(sc.toMsAccessJdbcSelect(to_sqs, from_sqs));
                           }
                        }
                     }
                  }

                  if (!rollupAdded) {
                     v_gil.addElement(sc.toMsAccessJdbcSelect(to_sqs, from_sqs));
                  }
               } else if (this.GroupByItemList.elementAt(i_count) instanceof FunctionCalls) {
                  FunctionCalls fc = (FunctionCalls)this.GroupByItemList.elementAt(i_count);
                  String s_fn = fc.getFunctionName().getColumnName();
                  if (!s_fn.equalsIgnoreCase("cube") && !s_fn.equalsIgnoreCase("rollup")) {
                     v_gil.addElement(fc.toMsAccessJdbcSelect(to_sqs, from_sqs));
                  } else {
                     Vector selectItemList = to_sqs.getSelectStatement().getSelectItemList();
                     Vector newSelectItemList = new Vector();
                     if (selectItemList != null) {
                        for(int i = 0; i < selectItemList.size(); ++i) {
                           if (selectItemList.get(i) instanceof SelectColumn) {
                              SelectColumn orgSC = (SelectColumn)selectItemList.get(i);
                              newSelectItemList = orgSC.getColumnExpression();
                              newArguments = new Vector();
                              orgSC = new SelectColumn();
                              this.copyFromOneSCToAnother(newSelectItemList, newArguments);
                              orgSC.setColumnExpression(newArguments);
                              orgSC.setIsAS(orgSC.getIsAS());
                              orgSC.setAliasName(orgSC.getAliasName());
                              newSelectItemList.add(orgSC);
                              if (selectItemList.size() - 1 > i) {
                                 orgSC.setEndsWith(",");
                              }
                           }
                        }
                     }

                     this.processCubeAndRollupConversion(fc, from_sqs, to_sqs, 21);
                     to_sqs.getSelectStatement().setSelectItemList(newSelectItemList);
                  }
               }
            }

            gbs.setGroupByItemList(v_gil);
         }
      } else {
         soc = null;
         if (((Vector)this.GroupByItemList.elementAt(0)).size() == 0) {
            gbs = null;
         } else {
            Vector singleGroupingSet = (Vector)this.GroupByItemList.elementAt(0);

            for(i_count = 0; i_count < singleGroupingSet.size(); ++i_count) {
               if (singleGroupingSet.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)singleGroupingSet.elementAt(i_count);
                  this.covertAilasToTableName(sc, from_sqs);
                  v_gil.addElement(sc.toMsAccessJdbcSelect(to_sqs, from_sqs));
               }
            }

            gbs.setGroupByItemList(v_gil);
         }

         this.makeNonGroupedSelectItemsNull((Vector)this.GroupByItemList.elementAt(0), from_sqs, to_sqs, 21);
         if (from_sqs.getSetOperatorClause() != null) {
            soc = from_sqs.getSetOperatorClause();
         }

         for(int i_count = 1; i_count < this.GroupByItemList.size(); ++i_count) {
            v_gblw = (Vector)this.GroupByItemList.elementAt(i_count);
            t_soc = this.createSetOperatorClause(v_gblw, from_sqs, to_sqs, 21);
            SelectQueryStatement t_sqs = to_sqs;

            for(SetOperatorClause t_soc = to_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
               t_sqs = t_soc.getSelectQueryStatement();
            }

            t_sqs.setSetOperatorClause(t_soc);
         }

         SelectQueryStatement t_sqs = to_sqs;

         for(SetOperatorClause t_soc = to_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
            t_sqs = t_soc.getSelectQueryStatement();
         }

         if (soc != null) {
            t_sqs.setSetOperatorClause(soc.toMsAccessJdbcSelect(to_sqs, from_sqs));
         }
      }

      if (this.WithOption != null && (this.WithOption.equalsIgnoreCase("ROLLUP") || this.WithOption.equalsIgnoreCase("CUBE"))) {
         FunctionCalls fc = new FunctionCalls();
         TableColumn tc = new TableColumn();
         tc.setColumnName(this.WithOption.toUpperCase());
         fc.setFunctionName(tc);
         fc.setFunctionArguments(v_gil);
         v_gblw = new Vector();
         v_gblw.add(fc);
         gbs.setGroupByItemList(v_gblw);
      }

      return gbs;
   }

   public GroupByStatement toOracleSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      GroupByStatement gbs = new GroupByStatement();
      gbs.setCommentClass(this.commentObj);
      gbs.setGroupClause(this.GroupClause);
      gbs.setHintClause(this.hintClause);
      Vector v_gil = new Vector();
      Vector v_tgil = new Vector();
      SelectColumn sc;
      Vector newFuncArgs;
      SelectColumn newSelectColumn;
      Vector newVector;
      int vi;
      SelectColumn fnArgCol;
      int i_count;
      FunctionCalls cubeRollUpFunc;
      int fni;
      Object fnArg;
      Vector fnArgVec;
      if (this.GroupingSetClause == null) {
         if (this.WithOption != null) {
            Vector v_gl = new Vector();
            sc = new SelectColumn();
            newFuncArgs = new Vector();
            FunctionCalls fc = new FunctionCalls();
            TableColumn tc = new TableColumn();

            for(int i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
               if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                  newSelectColumn = (SelectColumn)this.GroupByItemList.elementAt(i_count);
                  this.covertAilasToTableName(newSelectColumn, from_sqs);
                  v_gil.addElement(newSelectColumn.toOracleSelect(to_sqs, from_sqs));
               }
            }

            tc.setColumnName(this.WithOption);
            fc.setFunctionName(tc);
            fc.setFunctionArguments(v_gil);
            newFuncArgs.addElement(fc);
            sc.setColumnExpression(newFuncArgs);
            v_gl.addElement(sc);
            gbs.setGroupByItemList(v_gl);
         } else if (this.ALLOption) {
            for(i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
               if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)this.GroupByItemList.elementAt(i_count);
                  this.covertAilasToTableName(sc, from_sqs);
                  v_gil.addElement(sc.toOracleSelect(to_sqs, from_sqs));
               }
            }

            gbs.setGroupByItemList(v_gil);
            SetOperatorClause soc = null;
            if (from_sqs.getSetOperatorClause() != null) {
               soc = from_sqs.getSetOperatorClause();
            }

            if (from_sqs.getWhereExpression() != null) {
               SetOperatorClause n_soc = this.createSetOperatorClause((Vector)null, from_sqs, to_sqs, 1);
               SelectQueryStatement t_sqs = to_sqs;
               to_sqs.setSetOperatorClause(n_soc);

               for(SetOperatorClause t_soc = to_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
                  t_sqs = t_soc.getSelectQueryStatement();
               }

               if (soc != null) {
                  t_sqs.setSetOperatorClause(soc.toOracleSelect(to_sqs, from_sqs));
               }
            } else if (soc != null) {
               to_sqs.setSetOperatorClause(soc.toOracleSelect(to_sqs, from_sqs));
            }
         } else {
            for(i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
               if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)this.GroupByItemList.elementAt(i_count);
                  this.covertAilasToTableName(sc, from_sqs);
                  v_gil.addElement(sc.toOracleSelect(to_sqs, from_sqs));
               } else if (this.GroupByItemList.elementAt(i_count) instanceof FunctionCalls) {
                  cubeRollUpFunc = (FunctionCalls)this.GroupByItemList.elementAt(i_count);
                  newFuncArgs = new Vector();

                  for(fni = 0; fni < cubeRollUpFunc.getFunctionArguments().size(); ++fni) {
                     fnArg = cubeRollUpFunc.getFunctionArguments().get(fni);
                     if (fnArg instanceof SelectColumn) {
                        newFuncArgs.addElement(((SelectColumn)fnArg).toOracleSelect(to_sqs, from_sqs));
                     } else if (fnArg instanceof Vector) {
                        fnArgVec = (Vector)fnArg;
                        newSelectColumn = new SelectColumn();
                        newVector = new Vector();

                        for(vi = 0; vi < fnArgVec.size(); ++vi) {
                           fnArgCol = ((SelectColumn)fnArgVec.elementAt(vi)).toOracleSelect(to_sqs, from_sqs);
                           if (vi != fnArgVec.size() - 1) {
                              fnArgCol.setEndsWith(",");
                           }

                           newVector.add(fnArgCol);
                        }

                        newSelectColumn.setColumnExpression(newVector);
                        newSelectColumn.setOpenBrace("(");
                        newSelectColumn.setCloseBrace(")");
                        newFuncArgs.addElement(newSelectColumn);
                     }
                  }

                  cubeRollUpFunc.setFunctionArguments(newFuncArgs);
                  v_gil.addElement(cubeRollUpFunc);
               }
            }

            gbs.setGroupByItemList(v_gil);
         }
      } else {
         gbs.setGroupingSetClause(this.GroupingSetClause);
         gbs.setOpenBraces(this.openBraces);
         gbs.setClosedBraces(this.closedBraces);

         for(i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
            if (this.GroupByItemList.elementAt(i_count) instanceof Vector) {
               Vector v_item_list = (Vector)this.GroupByItemList.elementAt(i_count);
               v_gil = new Vector();

               for(int i_icount = 0; i_icount < v_item_list.size(); ++i_icount) {
                  if (v_item_list.elementAt(i_icount) instanceof SelectColumn) {
                     v_gil.addElement(((SelectColumn)v_item_list.elementAt(i_icount)).toOracleSelect(to_sqs, from_sqs));
                  }
               }

               v_tgil.addElement(v_gil);
            } else if (!(this.GroupByItemList.elementAt(i_count) instanceof FunctionCalls)) {
               sc = (SelectColumn)this.GroupByItemList.elementAt(i_count);
               v_tgil.addElement(sc.toOracleSelect(to_sqs, from_sqs));
            } else {
               cubeRollUpFunc = (FunctionCalls)this.GroupByItemList.elementAt(i_count);
               newFuncArgs = new Vector();

               for(fni = 0; fni < cubeRollUpFunc.getFunctionArguments().size(); ++fni) {
                  fnArg = cubeRollUpFunc.getFunctionArguments().get(fni);
                  if (fnArg instanceof SelectColumn) {
                     newFuncArgs.addElement(((SelectColumn)fnArg).toOracleSelect(to_sqs, from_sqs));
                  } else if (fnArg instanceof Vector) {
                     fnArgVec = (Vector)fnArg;
                     newSelectColumn = new SelectColumn();
                     newVector = new Vector();

                     for(vi = 0; vi < fnArgVec.size(); ++vi) {
                        fnArgCol = ((SelectColumn)fnArgVec.elementAt(vi)).toOracleSelect(to_sqs, from_sqs);
                        if (vi != fnArgVec.size() - 1) {
                           fnArgCol.setEndsWith(",");
                        }

                        newVector.add(fnArgCol);
                     }

                     newSelectColumn.setColumnExpression(newVector);
                     newSelectColumn.setOpenBrace("(");
                     newSelectColumn.setCloseBrace(")");
                     newFuncArgs.addElement(newSelectColumn);
                  }
               }

               cubeRollUpFunc.setFunctionArguments(newFuncArgs);
               v_gil.addElement(cubeRollUpFunc);
            }
         }

         gbs.setGroupByItemList(v_tgil);
      }

      if (gbs != null) {
         gbs.setObjectContext(this.context);
      }

      return gbs;
   }

   public GroupByStatement toTimesTenSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      GroupByStatement gbs = new GroupByStatement();
      gbs.setGroupClause(this.GroupClause);
      Vector v_gil = new Vector();
      new Vector();
      Vector singleGroupingSet;
      if (this.GroupingSetClause == null) {
         SelectColumn sc;
         if (this.WithOption != null) {
            singleGroupingSet = new Vector();
            sc = new SelectColumn();
            Vector v_glsc = new Vector();
            FunctionCalls fc = new FunctionCalls();
            TableColumn tc = new TableColumn();

            for(int i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
               if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                  SelectColumn sc = (SelectColumn)this.GroupByItemList.elementAt(i_count);
                  this.covertAilasToTableName(sc, from_sqs);
                  v_gil.addElement(sc.toTimesTenSelect(to_sqs, from_sqs));
               }
            }

            tc.setColumnName(this.WithOption);
            fc.setFunctionName(tc);
            fc.setFunctionArguments(v_gil);
            v_glsc.addElement(fc);
            sc.setColumnExpression(v_glsc);
            singleGroupingSet.addElement(sc);
            gbs.setGroupByItemList(singleGroupingSet);
         } else {
            int i_count;
            if (this.ALLOption) {
               for(i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
                  if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                     sc = (SelectColumn)this.GroupByItemList.elementAt(i_count);
                     this.covertAilasToTableName(sc, from_sqs);
                     v_gil.addElement(sc.toTimesTenSelect(to_sqs, from_sqs));
                  }
               }

               gbs.setGroupByItemList(v_gil);
            } else {
               for(i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
                  if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                     sc = (SelectColumn)this.GroupByItemList.elementAt(i_count);
                     this.covertAilasToTableName(sc, from_sqs);
                     v_gil.addElement(sc.toTimesTenSelect(to_sqs, from_sqs));
                  }
               }

               gbs.setGroupByItemList(v_gil);
            }
         }
      } else {
         if (((Vector)this.GroupByItemList.elementAt(0)).size() == 0) {
            gbs = null;
         } else {
            singleGroupingSet = (Vector)this.GroupByItemList.elementAt(0);

            for(int i_count = 0; i_count < singleGroupingSet.size(); ++i_count) {
               if (singleGroupingSet.elementAt(i_count) instanceof SelectColumn) {
                  SelectColumn sc = (SelectColumn)singleGroupingSet.elementAt(i_count);
                  this.covertAilasToTableName(sc, from_sqs);
                  v_gil.addElement(sc.toTimesTenSelect(to_sqs, from_sqs));
               }
            }

            gbs.setGroupByItemList(v_gil);
         }

         this.makeNonGroupedSelectItemsNull((Vector)this.GroupByItemList.elementAt(0), from_sqs, to_sqs, 10);
      }

      if (gbs != null) {
         gbs.setObjectContext(this.context);
      }

      return gbs;
   }

   public GroupByStatement toNetezzaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      GroupByStatement gbs = new GroupByStatement();
      gbs.setGroupClause(this.GroupClause);
      Vector v_gil = new Vector();
      new Vector();
      SetOperatorClause t_soc;
      SetOperatorClause soc;
      int i_count;
      SelectColumn sc;
      if (this.GroupingSetClause == null) {
         SelectColumn sc;
         if (this.ALLOption) {
            for(int i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
               if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)this.GroupByItemList.elementAt(i_count);
                  v_gil.addElement(sc.toNetezzaSelect(to_sqs, from_sqs));
               }
            }

            gbs.setGroupByItemList(v_gil);
            soc = null;
            if (from_sqs.getSetOperatorClause() != null) {
               soc = from_sqs.getSetOperatorClause();
            }

            if (from_sqs.getWhereExpression() != null) {
               SetOperatorClause n_soc = this.createSetOperatorClause((Vector)null, from_sqs, to_sqs, 11);
               SelectQueryStatement t_sqs = to_sqs;
               to_sqs.setSetOperatorClause(n_soc);

               for(t_soc = to_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
                  t_sqs = t_soc.getSelectQueryStatement();
               }

               if (soc != null) {
                  t_sqs.setSetOperatorClause(soc.toNetezzaSelect(to_sqs, from_sqs));
               }
            } else if (soc != null) {
               to_sqs.setSetOperatorClause(soc.toNetezzaSelect(to_sqs, from_sqs));
            }
         } else {
            boolean rollupAdded = false;
            sc = null;

            for(i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
               if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)this.GroupByItemList.elementAt(i_count);
                  Vector columnExpressionVector = sc.getColumnExpression();

                  for(int i = 0; i < columnExpressionVector.size(); ++i) {
                     if (columnExpressionVector.size() != 1 || !(columnExpressionVector.elementAt(i) instanceof FunctionCalls)) {
                        if (columnExpressionVector.elementAt(0) instanceof TableColumn) {
                           this.covertAilasToTableName(sc, from_sqs);
                           v_gil.addElement(sc.toNetezzaSelect(to_sqs, from_sqs));
                           rollupAdded = true;
                        } else {
                           v_gil.addElement(sc.toNetezzaSelect(to_sqs, from_sqs));
                           rollupAdded = true;
                        }
                        break;
                     }

                     FunctionCalls fc = (FunctionCalls)columnExpressionVector.elementAt(i);
                     String s_fn = fc.getFunctionName().getColumnName();
                     if (!s_fn.equalsIgnoreCase("cube") && !s_fn.equalsIgnoreCase("rollup")) {
                        v_gil.addElement(fc.toNetezzaSelect(to_sqs, from_sqs));
                        rollupAdded = true;
                     } else {
                        Vector selectItemList = to_sqs.getSelectStatement().getSelectItemList();
                        Vector newSelectItemList = new Vector();
                        if (selectItemList != null) {
                           for(int ii = 0; ii < selectItemList.size(); ++ii) {
                              if (selectItemList.get(ii) instanceof SelectColumn) {
                                 SelectColumn orgSC = (SelectColumn)selectItemList.get(ii);
                                 Vector columnExpression = orgSC.getColumnExpression();
                                 Vector newColumnExp = new Vector();
                                 SelectColumn newSC = new SelectColumn();
                                 this.copyFromOneSCToAnother(columnExpression, newColumnExp);
                                 newSC.setColumnExpression(newColumnExp);
                                 newSC.setIsAS(orgSC.getIsAS());
                                 newSC.setAliasName(orgSC.getAliasName());
                                 if (selectItemList.size() - 1 > ii) {
                                    newSC.setEndsWith(",");
                                 }

                                 newSelectItemList.add(newSC);
                              }
                           }
                        }

                        this.processCubeAndRollupConversion(fc, from_sqs, to_sqs, 11);
                        to_sqs.getSelectStatement().setSelectItemList(newSelectItemList);
                        rollupAdded = true;
                        if (fc.getFunctionArguments() != null && fc.getFunctionArguments().size() > 0) {
                           Vector newArguments = new Vector();

                           for(int j = 0; j < fc.getFunctionArguments().size(); ++j) {
                              if (j > 0) {
                                 newArguments.add(", ");
                              }

                              newArguments.add(fc.getFunctionArguments().get(j));
                           }

                           sc.setColumnExpression(newArguments);
                           v_gil.addElement(sc.toNetezzaSelect(to_sqs, from_sqs));
                        }
                     }
                  }

                  if (!rollupAdded) {
                     v_gil.addElement(sc.toNetezzaSelect(to_sqs, from_sqs));
                  }
               }
            }

            gbs.setGroupByItemList(v_gil);
         }
      } else {
         soc = null;
         if (((Vector)this.GroupByItemList.elementAt(0)).size() == 0) {
            gbs = null;
         } else {
            Vector singleGroupingSet = (Vector)this.GroupByItemList.elementAt(0);

            for(i_count = 0; i_count < singleGroupingSet.size(); ++i_count) {
               if (singleGroupingSet.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)singleGroupingSet.elementAt(i_count);
                  v_gil.addElement(sc.toNetezzaSelect(to_sqs, from_sqs));
               }
            }

            gbs.setGroupByItemList(v_gil);
         }

         this.makeNonGroupedSelectItemsNull((Vector)this.GroupByItemList.elementAt(0), from_sqs, to_sqs, 11);
         if (from_sqs.getSetOperatorClause() != null) {
            soc = from_sqs.getSetOperatorClause();
         }

         for(int i_count = 1; i_count < this.GroupByItemList.size(); ++i_count) {
            Vector v_item_list = (Vector)this.GroupByItemList.elementAt(i_count);
            t_soc = this.createSetOperatorClause(v_item_list, from_sqs, to_sqs, 11);
            SelectQueryStatement t_sqs = to_sqs;

            for(SetOperatorClause t_soc = to_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
               t_sqs = t_soc.getSelectQueryStatement();
            }

            t_sqs.setSetOperatorClause(t_soc);
         }

         SelectQueryStatement t_sqs = to_sqs;

         for(SetOperatorClause t_soc = to_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
            t_sqs = t_soc.getSelectQueryStatement();
         }

         if (soc != null) {
            t_sqs.setSetOperatorClause(soc.toNetezzaSelect(to_sqs, from_sqs));
         }
      }

      return gbs;
   }

   private void makeNonGroupedSelectItemsNull(Vector singleGroupingSet, SelectQueryStatement from_sqs, SelectQueryStatement to_sqs, int database) throws ConvertException {
      Vector selectItemList = this.createNewSelectItemListIfStar(from_sqs, to_sqs);
      Vector v_sil = null;
      if (selectItemList == null) {
         v_sil = from_sqs.getSelectStatement().getSelectItemList();
      } else {
         v_sil = selectItemList;
      }

      for(int i_count = 0; i_count < v_sil.size(); ++i_count) {
         if (v_sil.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn selectItemSC = (SelectColumn)v_sil.elementAt(i_count);
            SelectColumn to_sql_selectItemSC = (SelectColumn)to_sqs.getSelectStatement().getSelectItemList().elementAt(i_count);
            if (selectItemSC.isAggregateFunction()) {
               if (singleGroupingSet == null) {
                  Vector v;
                  if (database != 3 && database != 6) {
                     v = new Vector();
                     v.addElement("0");
                     v.addElement("*");
                     v.addElement("NULL");
                     to_sql_selectItemSC.setColumnExpression(v);
                  } else {
                     v = new Vector();
                     v.addElement(" CAST(NULL AS INT)");
                     to_sql_selectItemSC.setColumnExpression(v);
                  }
               }
            } else {
               boolean areThere = true;
               if (singleGroupingSet != null) {
                  areThere = this.checkIfSelectItemIsPresentInSingleGroupingSet(singleGroupingSet, selectItemSC);
               }

               if (!areThere) {
                  Vector v = new Vector();
                  boolean castNullAsInt = false;
                  castNullAsInt = this.checkWhetherToCastNullAsInt(selectItemSC);
                  if (castNullAsInt && database == 4) {
                     v.addElement("CAST(NULL AS INT)");
                  } else {
                     v.addElement("NULL");
                  }

                  to_sql_selectItemSC.setColumnExpression(v);
                  if (selectItemSC.getAliasName() == null && database != 4) {
                     to_sql_selectItemSC.setAliasName(selectItemSC.getTheCoreSelectItem());
                  }
               }
            }
         }
      }

   }

   private boolean checkIfSelectItemIsPresentInSingleGroupingSet(Vector singleGroupingSet, SelectColumn selectItemSC) {
      for(int i = 0; i < singleGroupingSet.size(); ++i) {
         SelectColumn groupingSetSC = (SelectColumn)singleGroupingSet.elementAt(i);
         if (this.checkSelectColumnEuality(groupingSetSC, selectItemSC)) {
            return true;
         }
      }

      return false;
   }

   private boolean checkSelectColumnEuality(SelectColumn groupingSetSC, SelectColumn selectItemSC) {
      String groupingSetCoreSelectItem = groupingSetSC.getTheCoreSelectItem();
      String selectItemCoreSelectItem = selectItemSC.getTheCoreSelectItem();
      return selectItemCoreSelectItem.equalsIgnoreCase(groupingSetCoreSelectItem);
   }

   private Vector createNewSelectItemListIfStar(SelectQueryStatement from_sqs, SelectQueryStatement to_sqs) throws ConvertException {
      Vector selectItemList = from_sqs.getSelectStatement().getSelectItemList();
      boolean starPresent = false;

      for(int i = 0; i < selectItemList.size(); ++i) {
         SelectColumn selectItemSC = (SelectColumn)selectItemList.get(i);
         if (selectItemSC.toString().equals("* ") || selectItemSC.toString().indexOf(".*") != -1) {
            starPresent = true;
         }
      }

      if (!starPresent) {
         return null;
      } else {
         Vector copyGroupByListInSelectClause = new Vector();
         Vector comparisonGroupByList = new Vector();

         SelectColumn groupBySCToBeAdded;
         for(int i = 0; i < this.GroupByItemList.size(); ++i) {
            if (this.GroupByItemList.elementAt(i) instanceof Vector) {
               Vector groupByListVector = (Vector)this.GroupByItemList.get(i);

               for(int j = 0; j < groupByListVector.size(); ++j) {
                  groupBySCToBeAdded = (SelectColumn)groupByListVector.get(j);
                  if (!comparisonGroupByList.contains(groupBySCToBeAdded.toString().toUpperCase().trim())) {
                     comparisonGroupByList.add(groupBySCToBeAdded.toString().toUpperCase().trim());
                     copyGroupByListInSelectClause.add(groupBySCToBeAdded);
                  }
               }
            } else if (this.GroupByItemList.elementAt(i) instanceof SelectColumn) {
               SelectColumn groupBySCToBeAdded = (SelectColumn)this.GroupByItemList.get(i);
               if (!comparisonGroupByList.contains(groupBySCToBeAdded.toString().toUpperCase().trim())) {
                  comparisonGroupByList.add(groupBySCToBeAdded.toString().toUpperCase().trim());
                  copyGroupByListInSelectClause.add(groupBySCToBeAdded);
               }
            }
         }

         Vector newSelectItemListFromGroupingSet = new Vector();

         for(int i = 0; i < copyGroupByListInSelectClause.size(); ++i) {
            SelectColumn sc = (SelectColumn)copyGroupByListInSelectClause.get(i);
            groupBySCToBeAdded = sc.toANSISelect(to_sqs, from_sqs);
            if (i != copyGroupByListInSelectClause.size() - 1) {
               groupBySCToBeAdded.setEndsWith(",");
            }

            newSelectItemListFromGroupingSet.add(groupBySCToBeAdded);
         }

         SelectStatement toSQSSelectStatement = to_sqs.getSelectStatement();
         Vector v = toSQSSelectStatement.getSelectItemList();
         toSQSSelectStatement.setSelectItemList(newSelectItemListFromGroupingSet);
         return copyGroupByListInSelectClause;
      }
   }

   public SetOperatorClause createSetOperatorClause(Vector v_glist, SelectQueryStatement from_sqs, SelectQueryStatement to_sqs, int database) throws ConvertException {
      SetOperatorClause soc = new SetOperatorClause();
      GroupByStatement gbs = new GroupByStatement();
      SelectQueryStatement set_sqs = new SelectQueryStatement();
      int i_count;
      SelectColumn sc;
      Vector convertedSingleGroupingList;
      if (database == 1) {
         if (from_sqs.getSelectStatement() != null) {
            set_sqs.setSelectStatement(from_sqs.getSelectStatement().toOracleSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getFromClause() != null) {
            FromClause fc = new FromClause();
            set_sqs.setFromClause(fc);
            set_sqs.setFromClause(from_sqs.getFromClause().toOracleSelect(set_sqs, from_sqs));
         }

         if (set_sqs.getWhereExpression() == null) {
            if (from_sqs.getWhereExpression() != null) {
               set_sqs.setWhereExpression(from_sqs.getWhereExpression().toOracleSelect(set_sqs, from_sqs));
            }
         } else {
            set_sqs.getWhereExpression().addOperator("AND");
            set_sqs.getWhereExpression().addWhereExpression(from_sqs.getWhereExpression().toOracleSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getHavingStatement() != null) {
            set_sqs.setHavingStatement(from_sqs.getHavingStatement().toOracleSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getIntoStatement() != null) {
            set_sqs.setIntoStatement(from_sqs.getIntoStatement().toOracleSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getForUpdateStatement() != null) {
            set_sqs.setForUpdateStatement(from_sqs.getForUpdateStatement().toOracleSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getHierarchicalQueryClause() != null) {
            set_sqs.setHierarchicalQueryClause(from_sqs.getHierarchicalQueryClause().toOracleSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getLimitClause() != null) {
            set_sqs.setLimitClause(from_sqs.getLimitClause().toOracleSelect(set_sqs, from_sqs));
         }

         convertedSingleGroupingList = new Vector();
         if (v_glist != null && v_glist.size() != 0) {
            for(i_count = 0; i_count < v_glist.size(); ++i_count) {
               if (v_glist.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)v_glist.elementAt(i_count);
                  this.covertAilasToTableName(sc, from_sqs);
                  convertedSingleGroupingList.addElement(sc.toOracleSelect(set_sqs, from_sqs));
               }
            }

            gbs.setGroupByItemList(convertedSingleGroupingList);
            gbs.setGroupClause("GROUP BY");
            gbs.setGroupingSetClause((String)null);
         } else {
            gbs = null;
         }
      } else if (database == 2 && v_glist != null) {
         if (from_sqs.getSelectStatement() != null) {
            set_sqs.setSelectStatement(from_sqs.getSelectStatement().toMSSQLServerSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getFromClause() != null) {
            set_sqs.setFromClause(from_sqs.getFromClause().toMSSQLServerSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getWhereExpression() != null) {
            set_sqs.setWhereExpression(from_sqs.getWhereExpression().toMSSQLServerSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getHavingStatement() != null) {
            set_sqs.setHavingStatement(from_sqs.getHavingStatement().toMSSQLServerSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getIntoStatement() != null) {
            set_sqs.setIntoStatement(from_sqs.getIntoStatement().toMSSQLServerSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getForUpdateStatement() != null) {
            set_sqs.setForUpdateStatement(from_sqs.getForUpdateStatement().toMSSQLServerSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getHierarchicalQueryClause() != null) {
            set_sqs.setHierarchicalQueryClause(from_sqs.getHierarchicalQueryClause().toMSSQLServerSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getLimitClause() != null) {
            set_sqs.setLimitClause(from_sqs.getLimitClause().toMSSQLServerSelect(set_sqs, from_sqs));
         }

         convertedSingleGroupingList = new Vector();
         if (v_glist != null && v_glist.size() != 0) {
            for(i_count = 0; i_count < v_glist.size(); ++i_count) {
               if (v_glist.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)v_glist.elementAt(i_count);
                  this.covertAilasToTableName(sc, from_sqs);
                  convertedSingleGroupingList.addElement(sc.toMSSQLServerSelect(set_sqs, from_sqs));
               }
            }

            gbs.setGroupByItemList(convertedSingleGroupingList);
            gbs.setGroupClause("GROUP BY");
            gbs.setGroupingSetClause((String)null);
         } else {
            gbs = null;
         }
      } else if (database == 7 && v_glist != null) {
         if (from_sqs.getSelectStatement() != null) {
            set_sqs.setSelectStatement(from_sqs.getSelectStatement().toSybaseSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getFromClause() != null) {
            set_sqs.setFromClause(from_sqs.getFromClause().toSybaseSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getWhereExpression() != null) {
            set_sqs.setWhereExpression(from_sqs.getWhereExpression().toSybaseSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getHavingStatement() != null) {
            set_sqs.setHavingStatement(from_sqs.getHavingStatement().toSybaseSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getIntoStatement() != null) {
            set_sqs.setIntoStatement(from_sqs.getIntoStatement().toSybaseSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getForUpdateStatement() != null) {
            set_sqs.setForUpdateStatement(from_sqs.getForUpdateStatement().toSybaseSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getHierarchicalQueryClause() != null) {
            set_sqs.setHierarchicalQueryClause(from_sqs.getHierarchicalQueryClause().toSybaseSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getLimitClause() != null) {
            set_sqs.setLimitClause(from_sqs.getLimitClause().toSybaseSelect(set_sqs, from_sqs));
         }

         convertedSingleGroupingList = new Vector();
         if (v_glist != null && v_glist.size() != 0) {
            for(i_count = 0; i_count < v_glist.size(); ++i_count) {
               if (v_glist.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)v_glist.elementAt(i_count);
                  this.covertAilasToTableName(sc, from_sqs);
                  convertedSingleGroupingList.addElement(sc.toSybaseSelect(set_sqs, from_sqs));
               }
            }

            gbs.setGroupByItemList(convertedSingleGroupingList);
            gbs.setGroupClause("GROUP BY");
            gbs.setGroupingSetClause((String)null);
         } else {
            gbs = null;
         }
      } else if (database == 3 && v_glist == null) {
         if (from_sqs.getSelectStatement() != null) {
            set_sqs.setSelectStatement(from_sqs.getSelectStatement().toDB2Select(set_sqs, from_sqs));
         }

         if (from_sqs.getFromClause() != null) {
            set_sqs.setFromClause(from_sqs.getFromClause().toDB2Select(set_sqs, from_sqs));
         }

         if (from_sqs.getWhereExpression() != null) {
            set_sqs.setWhereExpression(from_sqs.getWhereExpression().toDB2Select(set_sqs, from_sqs));
         }

         if (from_sqs.getHavingStatement() != null) {
            set_sqs.setHavingStatement(from_sqs.getHavingStatement().toDB2Select(set_sqs, from_sqs));
         }

         if (from_sqs.getLimitClause() != null) {
            set_sqs.setLimitClause(from_sqs.getLimitClause().toDB2Select(set_sqs, from_sqs));
         }
      } else if (database == 4) {
         if (from_sqs.getSelectStatement() != null) {
            set_sqs.setSelectStatement(from_sqs.getSelectStatement().toPostgreSQLSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getFromClause() != null) {
            set_sqs.setFromClause(from_sqs.getFromClause().toPostgreSQLSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getWhereExpression() != null) {
            set_sqs.setWhereExpression(from_sqs.getWhereExpression().toPostgreSQLSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getHavingStatement() != null) {
            set_sqs.setHavingStatement(from_sqs.getHavingStatement().toPostgreSQLSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getIntoStatement() != null) {
            set_sqs.setIntoStatement(from_sqs.getIntoStatement().toPostgreSQLSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getLimitClause() != null) {
            set_sqs.setLimitClause(from_sqs.getLimitClause().toPostgreSQLSelect(set_sqs, from_sqs));
         }

         convertedSingleGroupingList = new Vector();
         if (v_glist != null && v_glist.size() != 0) {
            for(i_count = 0; i_count < v_glist.size(); ++i_count) {
               if (v_glist.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)v_glist.elementAt(i_count);
                  this.covertAilasToTableName(sc, from_sqs);
                  convertedSingleGroupingList.addElement(sc.toPostgreSQLSelect(set_sqs, from_sqs));
               }
            }

            gbs.setGroupByItemList(convertedSingleGroupingList);
            gbs.setGroupClause("GROUP BY");
            gbs.setGroupingSetClause((String)null);
         } else {
            gbs = null;
         }
      } else if (database == 14) {
         if (from_sqs.getSelectStatement() != null) {
            set_sqs.setSelectStatement(from_sqs.getSelectStatement().toBigQuerySelect(set_sqs, from_sqs));
         }

         if (from_sqs.getFromClause() != null) {
            set_sqs.setFromClause(from_sqs.getFromClause().toBigQuerySelect(set_sqs, from_sqs));
         }

         if (from_sqs.getWhereExpression() != null) {
            set_sqs.setWhereExpression(from_sqs.getWhereExpression().toBigQuerySelect(set_sqs, from_sqs));
         }

         if (from_sqs.getHavingStatement() != null) {
            set_sqs.setHavingStatement(from_sqs.getHavingStatement().toBigQuerySelect(set_sqs, from_sqs));
         }

         if (from_sqs.getIntoStatement() != null) {
            set_sqs.setIntoStatement(from_sqs.getIntoStatement().toBigQuerySelect(set_sqs, from_sqs));
         }

         if (from_sqs.getLimitClause() != null) {
            set_sqs.setLimitClause(from_sqs.getLimitClause().toBigQuerySelect(set_sqs, from_sqs));
         }

         convertedSingleGroupingList = new Vector();
         if (v_glist != null && v_glist.size() != 0) {
            for(i_count = 0; i_count < v_glist.size(); ++i_count) {
               if (v_glist.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)v_glist.elementAt(i_count);
                  this.covertAilasToTableName(sc, from_sqs);
                  convertedSingleGroupingList.addElement(sc.toBigQuerySelect(set_sqs, from_sqs));
               }
            }

            gbs.setGroupByItemList(convertedSingleGroupingList);
            gbs.setGroupClause("GROUP BY");
            gbs.setGroupingSetClause((String)null);
         } else {
            gbs = null;
         }
      } else if (database == 8) {
         if (from_sqs.getSelectStatement() != null) {
            set_sqs.setSelectStatement(from_sqs.getSelectStatement().toANSISelect(set_sqs, from_sqs));
         }

         if (from_sqs.getFromClause() != null) {
            set_sqs.setFromClause(from_sqs.getFromClause().toANSISelect(set_sqs, from_sqs));
         }

         if (from_sqs.getWhereExpression() != null) {
            set_sqs.setWhereExpression(from_sqs.getWhereExpression().toANSISelect(set_sqs, from_sqs));
         }

         if (from_sqs.getHavingStatement() != null) {
            set_sqs.setHavingStatement(from_sqs.getHavingStatement().toANSISelect(set_sqs, from_sqs));
         }

         convertedSingleGroupingList = new Vector();
         if (v_glist != null && v_glist.size() != 0) {
            for(i_count = 0; i_count < v_glist.size(); ++i_count) {
               if (v_glist.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)v_glist.elementAt(i_count);
                  this.covertAilasToTableName(sc, from_sqs);
                  convertedSingleGroupingList.addElement(sc.toANSISelect(set_sqs, from_sqs));
               }
            }

            gbs.setGroupByItemList(convertedSingleGroupingList);
            gbs.setGroupClause("GROUP BY");
            gbs.setGroupingSetClause((String)null);
         } else {
            gbs = null;
         }
      } else if (database == 6) {
         if (from_sqs.getSelectStatement() != null) {
            set_sqs.setSelectStatement(from_sqs.getSelectStatement().toInformixSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getFromClause() != null) {
            set_sqs.setFromClause(from_sqs.getFromClause().toInformixSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getWhereExpression() != null) {
            set_sqs.setWhereExpression(from_sqs.getWhereExpression().toInformixSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getHavingStatement() != null) {
            set_sqs.setHavingStatement(from_sqs.getHavingStatement().toInformixSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getIntoStatement() != null) {
            set_sqs.setIntoStatement(from_sqs.getIntoStatement().toInformixSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getLimitClause() != null) {
            set_sqs.setLimitClause(from_sqs.getLimitClause().toInformixSelect(set_sqs, from_sqs));
         }

         convertedSingleGroupingList = new Vector();
         if (v_glist != null && v_glist.size() != 0) {
            for(i_count = 0; i_count < v_glist.size(); ++i_count) {
               if (v_glist.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)v_glist.elementAt(i_count);
                  this.covertAilasToTableName(sc, from_sqs);
                  convertedSingleGroupingList.addElement(sc.toInformixSelect(set_sqs, from_sqs));
               }
            }

            gbs.setGroupByItemList(convertedSingleGroupingList);
            gbs.setGroupClause("GROUP BY");
            gbs.setGroupingSetClause((String)null);
         } else {
            gbs = null;
         }
      } else if (database == 11) {
         if (from_sqs.getSelectStatement() != null) {
            set_sqs.setSelectStatement(from_sqs.getSelectStatement().toNetezzaSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getFromClause() != null) {
            set_sqs.setFromClause(from_sqs.getFromClause().toNetezzaSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getWhereExpression() != null) {
            set_sqs.setWhereExpression(from_sqs.getWhereExpression().toNetezzaSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getHavingStatement() != null) {
            set_sqs.setHavingStatement(from_sqs.getHavingStatement().toNetezzaSelect(set_sqs, from_sqs));
         }

         convertedSingleGroupingList = new Vector();
         if (v_glist != null && v_glist.size() != 0) {
            for(i_count = 0; i_count < v_glist.size(); ++i_count) {
               if (v_glist.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)v_glist.elementAt(i_count);
                  this.covertAilasToTableName(sc, from_sqs);
                  convertedSingleGroupingList.addElement(sc.toNetezzaSelect(set_sqs, from_sqs));
               }
            }

            gbs.setGroupByItemList(convertedSingleGroupingList);
            gbs.setGroupClause("GROUP BY");
            gbs.setGroupingSetClause((String)null);
         } else {
            gbs = null;
         }
      } else if (database == 12) {
         if (from_sqs.getSelectStatement() != null) {
            set_sqs.setSelectStatement(from_sqs.getSelectStatement().toTeradataSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getFromClause() != null) {
            set_sqs.setFromClause(from_sqs.getFromClause().toTeradataSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getWhereExpression() != null) {
            set_sqs.setWhereExpression(from_sqs.getWhereExpression().toTeradataSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getHavingStatement() != null) {
            set_sqs.setHavingStatement(from_sqs.getHavingStatement().toTeradataSelect(set_sqs, from_sqs));
         }

         convertedSingleGroupingList = new Vector();
         if (v_glist != null && v_glist.size() != 0) {
            for(i_count = 0; i_count < v_glist.size(); ++i_count) {
               if (v_glist.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)v_glist.elementAt(i_count);
                  this.covertAilasToTableName(sc, from_sqs);
                  convertedSingleGroupingList.addElement(sc.toTeradataSelect(set_sqs, from_sqs));
               }
            }

            gbs.setGroupByItemList(convertedSingleGroupingList);
            gbs.setGroupClause("GROUP BY");
            gbs.setGroupingSetClause((String)null);
         } else {
            gbs = null;
         }
      } else if (database == 17) {
         if (from_sqs.getSelectStatement() != null) {
            set_sqs.setSelectStatement(from_sqs.getSelectStatement().toSapHanaSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getFromClause() != null) {
            set_sqs.setFromClause(from_sqs.getFromClause().toSapHanaSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getWhereExpression() != null) {
            set_sqs.setWhereExpression(from_sqs.getWhereExpression().toSapHanaSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getHavingStatement() != null) {
            set_sqs.setHavingStatement(from_sqs.getHavingStatement().toSapHanaSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getIntoStatement() != null) {
            set_sqs.setIntoStatement(from_sqs.getIntoStatement().toSapHanaSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getLimitClause() != null) {
            set_sqs.setLimitClause(from_sqs.getLimitClause().toSapHanaSelect(set_sqs, from_sqs));
         }

         convertedSingleGroupingList = new Vector();
         if (v_glist != null && v_glist.size() != 0) {
            for(i_count = 0; i_count < v_glist.size(); ++i_count) {
               if (v_glist.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)v_glist.elementAt(i_count);
                  this.covertAilasToTableName(sc, from_sqs);
                  convertedSingleGroupingList.addElement(sc.toSapHanaSelect(set_sqs, from_sqs));
               }
            }

            gbs.setGroupByItemList(convertedSingleGroupingList);
            gbs.setGroupClause("GROUP BY");
            gbs.setGroupingSetClause((String)null);
         } else {
            gbs = null;
         }
      } else if (database == 18) {
         if (from_sqs.getSelectStatement() != null) {
            set_sqs.setSelectStatement(from_sqs.getSelectStatement().toSqliteSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getFromClause() != null) {
            set_sqs.setFromClause(from_sqs.getFromClause().toSqliteSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getWhereExpression() != null) {
            set_sqs.setWhereExpression(from_sqs.getWhereExpression().toSqliteSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getHavingStatement() != null) {
            set_sqs.setHavingStatement(from_sqs.getHavingStatement().toSqliteSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getIntoStatement() != null) {
            set_sqs.setIntoStatement(from_sqs.getIntoStatement().toSqliteSelect(set_sqs, from_sqs));
         }

         if (from_sqs.getLimitClause() != null) {
            set_sqs.setLimitClause(from_sqs.getLimitClause().toSqliteSelect(set_sqs, from_sqs));
         }

         convertedSingleGroupingList = new Vector();
         if (v_glist != null && v_glist.size() != 0) {
            for(i_count = 0; i_count < v_glist.size(); ++i_count) {
               if (v_glist.elementAt(i_count) instanceof SelectColumn) {
                  sc = (SelectColumn)v_glist.elementAt(i_count);
                  this.covertAilasToTableName(sc, from_sqs);
                  convertedSingleGroupingList.addElement(sc.toSqliteSelect(set_sqs, from_sqs));
               }
            }

            gbs.setGroupByItemList(convertedSingleGroupingList);
            gbs.setGroupClause("GROUP BY");
            gbs.setGroupingSetClause((String)null);
         } else {
            gbs = null;
         }
      }

      this.makeNonGroupedSelectItemsNull(v_glist, from_sqs, set_sqs, database);
      convertedSingleGroupingList = new Vector();
      if (v_glist == null) {
         set_sqs.setWhereExpression(this.changeOperator(set_sqs.getWhereExpression()));

         for(i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
            if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
               sc = (SelectColumn)this.GroupByItemList.elementAt(i_count);
               this.covertAilasToTableName(sc, from_sqs);
               convertedSingleGroupingList.addElement(sc.toOracleSelect(set_sqs, from_sqs));
            }
         }

         gbs = new GroupByStatement();
         gbs.setGroupByItemList(convertedSingleGroupingList);
         gbs.setGroupClause("GROUP BY");
         gbs.setGroupingSetClause((String)null);
      }

      set_sqs.setGroupByStatement(gbs);
      soc.setSetClause("UNION ALL");
      soc.setSelectQueryStatement(set_sqs);
      return soc;
   }

   public WhereExpression changeOperator(WhereExpression we) {
      if (we == null) {
         return null;
      } else {
         Vector v_wi = we.getWhereItem();

         for(int i_count = 0; i_count < v_wi.size(); ++i_count) {
            if (v_wi.elementAt(i_count) instanceof WhereItem) {
               WhereItem wi = (WhereItem)v_wi.elementAt(i_count);
               wi.setBeginOperator("NOT ");
            } else if (v_wi.elementAt(i_count) instanceof WhereExpression) {
               this.changeOperator((WhereExpression)v_wi.elementAt(i_count));
            }
         }

         return we;
      }
   }

   public void covertAilasToTableName(SelectColumn sc, SelectQueryStatement from_sqs) {
      Vector v_ce = sc.getColumnExpression();
      SelectStatement ss = from_sqs.getSelectStatement();
      Vector v_sil = ss.getSelectItemList();
      new Vector();
      if (v_ce.size() == 1) {
         if (v_ce.elementAt(0) instanceof TableColumn) {
            TableColumn tc = (TableColumn)v_ce.elementAt(0);
            String colName = tc.getColumnName();
            if (tc.getTableName() == null) {
               FromTable ft = MetadataInfoUtil.getTableOfColumn(from_sqs, (String)colName);
               if (ft != null) {
                  return;
               }
            }

            for(int i_count = 0; i_count < v_sil.size(); ++i_count) {
               if (v_sil.elementAt(i_count) instanceof SelectColumn) {
                  SelectColumn t_sc = (SelectColumn)v_sil.elementAt(i_count);
                  if (t_sc.getAliasName() != null && t_sc.getAliasName().replace('"', ' ').trim().equalsIgnoreCase(((TableColumn)v_ce.elementAt(0)).getColumnName())) {
                     Vector v_nce = t_sc.getColumnExpression();
                     sc.setColumnExpression(v_nce);
                     break;
                  }
               }
            }
         } else if (!from_sqs.isDenodo() && (from_sqs.isOracleLive() || from_sqs.isMSAzure()) && v_ce.elementAt(0) instanceof String) {
            sc.convertOrdinalNumberToColumn(from_sqs, v_ce);
         }
      }

   }

   public String toString() {
      StringBuffer sb = new StringBuffer();

      int i_count;
      for(i_count = 0; i_count < SelectQueryStatement.getBeautyTabCount(); ++i_count) {
         sb.append("\t");
      }

      if (this.commentObj != null) {
         sb.append(this.commentObj.toString().trim() + " ");
      }

      if (this.GroupClause != null) {
         sb.append(this.GroupClause.toUpperCase());
      }

      if (this.ALLOption) {
         sb.append(" ALL");
      }

      if (this.hintClause != null) {
         sb.append(this.hintClause);
      }

      int i_count;
      if (this.GroupingSetClause == null) {
         SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() + 1);

         for(i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
            if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
               ((SelectColumn)this.GroupByItemList.elementAt(i_count)).setObjectContext(this.context);
            }

            if (i_count == this.GroupByItemList.size() - 1) {
               sb.append("  " + this.GroupByItemList.elementAt(i_count).toString());
            } else {
               sb.append(" " + this.GroupByItemList.elementAt(i_count).toString() + ",");
               sb.append("\n");

               for(i_count = 0; i_count < SelectQueryStatement.getBeautyTabCount(); ++i_count) {
                  sb.append("\t");
               }
            }
         }

         SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() - 1);
      } else {
         Vector groupingSetItems = new Vector();

         for(i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
            if (this.GroupByItemList.elementAt(i_count) instanceof Vector) {
               groupingSetItems.add(this.GroupByItemList.elementAt(i_count));
            }
         }

         sb.append(" " + this.GroupingSetClause.toUpperCase() + "(");

         for(i_count = 0; i_count < groupingSetItems.size(); ++i_count) {
            if (groupingSetItems.elementAt(i_count) instanceof Vector) {
               Vector v_item_list = (Vector)groupingSetItems.elementAt(i_count);
               if (this.openBraces != null) {
                  sb.append(this.openBraces);
               }

               for(int i_icount = 0; i_icount < v_item_list.size(); ++i_icount) {
                  if (v_item_list.elementAt(i_icount) instanceof SelectColumn) {
                     ((SelectColumn)v_item_list.elementAt(i_icount)).setObjectContext(this.context);
                  }

                  if (i_icount == v_item_list.size() - 1) {
                     sb.append(" " + v_item_list.elementAt(i_icount).toString());
                  } else {
                     sb.append(" " + v_item_list.elementAt(i_icount).toString() + ",");
                  }
               }

               if (this.closedBraces != null) {
                  sb.append(this.closedBraces);
               }

               if (i_count != groupingSetItems.size() - 1) {
                  sb.append(",");
               }
            }
         }

         sb.append(")");
         SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() + 1);

         for(i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
            if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
               ((SelectColumn)this.GroupByItemList.elementAt(i_count)).setObjectContext(this.context);
               if (i_count == this.GroupByItemList.size() - 1) {
                  sb.append(" , " + this.GroupByItemList.elementAt(i_count).toString());
               } else {
                  sb.append(", " + this.GroupByItemList.elementAt(i_count).toString() + "");
                  sb.append("\n");

                  for(int j = 0; j < SelectQueryStatement.getBeautyTabCount(); ++j) {
                     sb.append("\t");
                  }
               }
            }
         }

         SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() - 1);
      }

      if (this.commentObjAfterToken != null) {
         sb.append(" " + this.commentObjAfterToken.toString().trim());
      }

      if (this.descOption != null) {
         sb.append(" " + this.descOption.toUpperCase() + " ");
      }

      if (this.WithOption != null) {
         sb.append(" WITH " + this.WithOption.toUpperCase());
      }

      return sb.toString();
   }

   private void processGroupByArguments(Vector colExp, Vector orgExp, int j, boolean bool) {
      if (colExp != null) {
         for(int i = 0; i < colExp.size(); ++i) {
            if (!bool) {
               j = i;
            }

            if (colExp.get(i) instanceof TableColumn) {
               if (((TableColumn)colExp.get(i)).getColumnName().startsWith("@")) {
                  orgExp.removeElementAt(j);
               }
            } else if (colExp.get(i) instanceof String) {
               if (((String)colExp.get(i)).trim().startsWith("@")) {
                  orgExp.removeElementAt(j);
               } else if (((String)colExp.get(i)).trim().startsWith("'") && ((String)colExp.get(i)).trim().endsWith("'")) {
                  orgExp.removeElementAt(j);
               }
            } else {
               Vector FunctionArgs;
               if (colExp.get(i) instanceof SelectColumn) {
                  FunctionArgs = ((SelectColumn)colExp.get(i)).getColumnExpression();
                  this.processGroupByArguments(FunctionArgs, orgExp, j, true);
               } else if (colExp.get(i) instanceof FunctionCalls) {
                  FunctionArgs = ((FunctionCalls)colExp.get(i)).getFunctionArguments();
                  if (FunctionArgs != null && FunctionArgs.size() == 1) {
                     this.processGroupByArguments(FunctionArgs, orgExp, j, true);
                  }
               }
            }
         }
      }

   }

   public void processCubeAndRollupConversion(FunctionCalls cubeOrRollup, SelectQueryStatement from_sqs, SelectQueryStatement to_sqs, int convertInt) throws ConvertException {
      if (cubeOrRollup.getFunctionName().getTableName() == null && cubeOrRollup.getFunctionName().getOwnerName() == null) {
         Vector functionArguments;
         if (cubeOrRollup.getFunctionName().getColumnName().equalsIgnoreCase("CUBE")) {
            functionArguments = cubeOrRollup.getFunctionArguments();
         } else if (cubeOrRollup.getFunctionName().getColumnName().equalsIgnoreCase("ROLLUP")) {
            functionArguments = cubeOrRollup.getFunctionArguments();
            this.processingElementsForRollup(functionArguments, from_sqs, to_sqs, convertInt);
            if (cubeOrRollup.getFunctionName() != null) {
               cubeOrRollup.setFunctionName((TableColumn)null);
            }
         }
      }

   }

   public void processingElementsForRollup(Vector functionArguments, SelectQueryStatement from_sqs, SelectQueryStatement to_sqs, int convertInt) throws ConvertException {
      SetOperatorClause soc = null;

      for(int i = functionArguments.size() - 1; i >= 0; --i) {
         Vector newArgsForCubeProcessing = new Vector();

         for(int j = 0; j < i; ++j) {
            newArgsForCubeProcessing.add(functionArguments.get(j));
         }

         this.makeNonGroupedSelectItemsNull(newArgsForCubeProcessing, from_sqs, to_sqs, convertInt);
         if (from_sqs.getSetOperatorClause() != null) {
            soc = from_sqs.getSetOperatorClause();
         }

         SetOperatorClause n_soc = null;
         if (convertInt == 4) {
            n_soc = this.createSetOperatorClause(newArgsForCubeProcessing, from_sqs, to_sqs, 4);
         } else if (convertInt == 7) {
            n_soc = this.createSetOperatorClause(newArgsForCubeProcessing, from_sqs, to_sqs, 7);
         } else if (convertInt == 11) {
            n_soc = this.createSetOperatorClause(newArgsForCubeProcessing, from_sqs, to_sqs, 11);
         }

         SelectQueryStatement t_sqs = to_sqs;

         SetOperatorClause t_soc;
         for(t_soc = to_sqs.getSetOperatorClause(); t_soc != null; t_soc = t_sqs.getSetOperatorClause()) {
            t_sqs = t_soc.getSelectQueryStatement();
         }

         t_sqs.setSetOperatorClause(n_soc);

         while(t_soc != null) {
            t_sqs = t_soc.getSelectQueryStatement();
            t_soc = t_sqs.getSetOperatorClause();
         }

         if (soc != null) {
            if (convertInt == 4) {
               t_sqs.setSetOperatorClause(soc.toPostgreSQLSelect(to_sqs, from_sqs));
            } else if (convertInt == 7) {
               t_sqs.setSetOperatorClause(soc.toSybaseSelect(to_sqs, from_sqs));
            } else if (convertInt == 11) {
               t_sqs.setSetOperatorClause(soc.toNetezzaSelect(to_sqs, from_sqs));
            }
         }
      }

   }

   private void copyFromOneSCToAnother(Vector orgColumnExpression, Vector cloneColumnExpression) {
      if (orgColumnExpression != null) {
         for(int i = 0; i < orgColumnExpression.size(); ++i) {
            if (orgColumnExpression.get(i) instanceof String) {
               cloneColumnExpression.add(orgColumnExpression.get(i));
            } else if (orgColumnExpression.get(i) instanceof TableColumn) {
               TableColumn orgTC = (TableColumn)orgColumnExpression.get(i);
               TableColumn cloneTC = new TableColumn();
               cloneTC.setOwnerName(orgTC.getOwnerName());
               cloneTC.setTableName(orgTC.getTableName());
               cloneTC.setColumnName(orgTC.getColumnName());
               cloneTC.setDot(orgTC.getDot());
               cloneColumnExpression.add(cloneTC);
            } else if (!(orgColumnExpression.get(i) instanceof FunctionCalls)) {
               if (orgColumnExpression.get(i) instanceof SelectColumn) {
                  SelectColumn cloneSC = new SelectColumn();
                  SelectColumn orgSC = (SelectColumn)orgColumnExpression.get(i);
                  cloneSC.setAliasName(orgSC.getAliasName());
                  cloneSC.setIsAS(orgSC.getIsAS());
                  Vector newExpression = new Vector();
                  Vector orgExpression = orgSC.getColumnExpression();
                  this.copyFromOneSCToAnother(orgExpression, newExpression);
                  cloneSC.setColumnExpression(newExpression);
                  cloneColumnExpression.add(cloneSC);
               }
            } else {
               FunctionCalls orgFC = (FunctionCalls)orgColumnExpression.get(i);
               FunctionCalls newFC = new FunctionCalls();
               TableColumn orgTC = orgFC.getFunctionName();
               TableColumn cloneTC = new TableColumn();
               if (orgFC != null && orgTC != null) {
                  cloneTC.setOwnerName(orgTC.getOwnerName());
                  cloneTC.setTableName(orgTC.getTableName());
                  cloneTC.setColumnName(orgTC.getColumnName());
                  cloneTC.setDot(orgTC.getDot());
                  newFC.setFunctionName(cloneTC);
                  newFC.setArgumentQualifier(orgFC.getArgumentQualifier());
                  newFC.setAsDatatype(orgFC.getAsDatatype());
                  newFC.setForLength(orgFC.getForLength());
                  newFC.setFromInTrim(orgFC.getFromInTrim());
                  newFC.setLengthString(orgFC.getLengthString());
               } else if (orgFC.getArgumentQualifier() == null && orgFC.getFunctionArguments() == null) {
                  cloneTC.setColumnName(orgFC.toString());
                  newFC.setFunctionName(cloneTC);
                  newFC.setOpenBracesForFunctionNameRequired(false);
                  newFC.setAsDatatype(orgFC.getAsDatatype());
                  newFC.setForLength(orgFC.getForLength());
                  newFC.setFromInTrim(orgFC.getFromInTrim());
                  newFC.setLengthString(orgFC.getLengthString());
               }

               Vector newFunctionArgs = new Vector();
               Vector orgFunctionArgs = orgFC.getFunctionArguments();
               this.copyFromOneSCToAnother(orgFunctionArgs, newFunctionArgs);
               newFC.setFunctionArguments(newFunctionArgs);
               cloneColumnExpression.add(newFC);
            }
         }
      }

   }

   private boolean checkWhetherToCastNullAsInt(SelectColumn selectItemSC) {
      Vector selectItemSCColExpr = selectItemSC.getColumnExpression();

      for(int iCol = 0; iCol < selectItemSCColExpr.size(); ++iCol) {
         if (selectItemSCColExpr.get(iCol) instanceof FunctionCalls) {
            FunctionCalls selectItemSCColFC = (FunctionCalls)selectItemSCColExpr.get(iCol);
            if (selectItemSCColFC.getFunctionNameAsAString().equalsIgnoreCase("decode")) {
               String selectItemSCColFCLastArg = selectItemSCColFC.getFunctionArguments().lastElement().toString();

               try {
                  Integer.parseInt(selectItemSCColFCLastArg);
                  return true;
               } catch (NumberFormatException var7) {
                  return false;
               }
            }
         } else if (selectItemSCColExpr.get(iCol) instanceof SelectColumn) {
            return this.checkWhetherToCastNullAsInt((SelectColumn)selectItemSCColExpr.get(iCol));
         }
      }

      return false;
   }

   public GroupByStatement toVectorWiseSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      GroupByStatement gbs = new GroupByStatement();
      gbs.setGroupClause(this.GroupClause);
      Vector v_gbl = new Vector();
      if (this.GroupingSetClause == null) {
         for(int i_count = 0; i_count < this.GroupByItemList.size(); ++i_count) {
            if (this.GroupByItemList.elementAt(i_count) instanceof SelectColumn) {
               SelectColumn temp = ((SelectColumn)this.GroupByItemList.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs);
               if (!this.isVectorContains(v_gbl, temp.toString())) {
                  v_gbl.addElement(temp);
               }
            } else if (this.GroupByItemList.elementAt(i_count) instanceof FunctionCalls) {
               FunctionCalls fc = (FunctionCalls)this.GroupByItemList.elementAt(i_count);
               String s_fn = new String();
               if (fc.getFunctionName() != null) {
                  TableColumn c = fc.getFunctionName();
                  s_fn = fc.getFunctionName().getColumnName();
               }

               if ((s_fn == null || !s_fn.equalsIgnoreCase("cube")) && !s_fn.equalsIgnoreCase("rollup")) {
                  v_gbl.addElement(fc.toVectorWiseSelect(to_sqs, from_sqs));
               } else {
                  gbs.setWithOption(s_fn);
                  Vector v_fa = fc.getFunctionArguments();

                  for(int cnt = 0; cnt < v_fa.size(); ++cnt) {
                     v_gbl.addElement(((SelectColumn)v_fa.elementAt(cnt)).toVectorWiseSelect(to_sqs, from_sqs));
                  }
               }
            }
         }
      }

      if (this.descOption != null) {
         gbs.setDescOption(this.descOption);
      }

      gbs.setGroupByItemList(v_gbl);
      if (this.GroupingSetClause != null) {
         throw new ConvertException();
      } else if (this.ALLOption) {
         throw new ConvertException();
      } else {
         if (this.WithOption != null && (this.WithOption.equalsIgnoreCase("ROLLUP") || this.WithOption.equalsIgnoreCase("CUBE"))) {
            FunctionCalls fc = new FunctionCalls();
            TableColumn tc = new TableColumn();
            tc.setColumnName(this.WithOption.toUpperCase());
            fc.setFunctionName(tc);
            fc.setFunctionArguments(v_gbl);
            Vector v_gblw = new Vector();
            v_gblw.add(fc);
            gbs.setGroupByItemList(v_gblw);
         }

         return gbs;
      }
   }

   public GroupByStatement toReplaceTblCol(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      GroupByStatement groupByStmtConv = new GroupByStatement();
      groupByStmtConv.setGroupClause(this.GroupClause);
      groupByStmtConv.setALLOption(this.ALLOption);
      from_sqs.setIsAliasReferenceClausesIteration(true);
      Vector groupByStmtItemListConv = new Vector();
      int i = 0;

      for(int groupByItemSize = this.GroupByItemList.size(); i < groupByItemSize; ++i) {
         if (this.GroupByItemList.get(i) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.GroupByItemList.get(i);
            if (sc.getColumnExpression().size() == 1 && sc.getColumnExpression().get(0) instanceof TableColumn && ((TableColumn)sc.getColumnExpression().get(0)).getTableName() == null && CastingUtil.ContainsIgnoreCase(from_sqs.getAliasColumns(), GeneralUtil.trimIfAliasNameIsEnclosed(((TableColumn)sc.getColumnExpression().get(0)).getColumnName()))) {
               groupByStmtItemListConv.addElement(sc);
            } else {
               groupByStmtItemListConv.addElement(((SelectColumn)this.GroupByItemList.get(i)).toReplaceTblCol(to_sqs, from_sqs));
            }
         } else if (this.GroupByItemList.get(i) instanceof FunctionCalls) {
            groupByStmtItemListConv.addElement(((FunctionCalls)this.GroupByItemList.get(i)).toReplaceTblCol(to_sqs, from_sqs));
         }

         groupByStmtConv.setGroupByItemList(groupByStmtItemListConv);
      }

      from_sqs.setIsAliasReferenceClausesIteration(false);
      if (this.GroupingSetClause != null) {
         groupByStmtConv.setGroupingSetClause(this.GroupingSetClause);
      }

      if (this.WithOption != null) {
         groupByStmtConv.setWithOption(this.WithOption);
      }

      if (this.openBraces != null) {
         groupByStmtConv.setOpenBraces(this.openBraces);
      }

      if (this.closedBraces != null) {
         groupByStmtConv.setClosedBraces(this.closedBraces);
      }

      if (this.commentObj != null) {
         groupByStmtConv.setCommentClass(this.commentObj);
      }

      if (this.commentObjAfterToken != null) {
         groupByStmtConv.setCommentClassAfterToken(this.commentObjAfterToken);
      }

      if (this.descOption != null) {
         groupByStmtConv.setDescOption(this.descOption);
      }

      if (this.GroupingSetClause != null) {
         throw new ConvertException();
      } else if (this.ALLOption) {
         throw new ConvertException();
      } else {
         return groupByStmtConv;
      }
   }

   public boolean isVectorContains(Vector obj, String groupby) {
      if (obj == null) {
         return false;
      } else {
         for(int i = 0; i < obj.size(); ++i) {
            if (obj.get(i) instanceof SelectColumn) {
               String temp = ((SelectColumn)obj.get(i)).toString().trim();
               if (temp.equalsIgnoreCase(groupby)) {
                  return true;
               }
            }
         }

         return false;
      }
   }
}
