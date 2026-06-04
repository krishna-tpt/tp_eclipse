package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.parser.ParseException;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.update.HintClause;
import com.adventnet.swissqlapi.util.misc.CastingUtil;
import com.adventnet.swissqlapi.util.misc.GeneralUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

public class SelectStatement {
   private String selectClause;
   private String reports;
   private String selectQualifier;
   private Vector distinctList;
   private String selectRowSpecifier;
   private String ifxSelectRowSpecifier;
   private String percentSpecifier;
   private int selectRowCount;
   private Integer ifxSelectRowCount;
   private String selectSpecialQualifier;
   private Vector selectItemList;
   private String rowcountVariable;
   private String openBraceForSelectInInsert;
   private String xmlString;
   private String endBracesForXMLString;
   private UserObjectContext context = null;
   private ArrayList insertValList = null;
   private HintClause hintClause;
   private String openBraceForRowCount;
   private String closedBraceForRowCount;
   private Integer aliasNameVersion = 0;
   private CommentClass commentObj;

   public void setSpecialCase(String rep) {
      this.reports = rep;
   }

   public void setObjectContext(UserObjectContext context) {
      this.context = context;
   }

   public void setSelectClause(String s) {
      this.selectClause = s;
   }

   public void setSelectItemList(Vector v) {
      this.selectItemList = v;
   }

   public void setSelectQualifier(String s_sq) {
      this.selectQualifier = s_sq;
   }

   public void setSelectRowSpecifier(String s_srs) {
      this.selectRowSpecifier = s_srs;
   }

   public void setInformixRowSpecifier(String s_srs) {
      this.ifxSelectRowSpecifier = s_srs;
   }

   public void setSelectRowCount(int i_rc) {
      this.selectRowCount = i_rc;
   }

   public void setIfxSelectRowCount(int ifx_rc) {
      this.ifxSelectRowCount = ifx_rc;
   }

   public void setSelectRowCountVariable(String rowcountVariable) {
      this.rowcountVariable = rowcountVariable;
   }

   public void setSelectSpecialQualifier(String s_ssq) {
      this.selectSpecialQualifier = s_ssq;
   }

   public void setDistinctList(Vector v_dl) {
      this.distinctList = v_dl;
   }

   public void setOpenBraceForSelectInInsertQuery(String openBraceForSelectInInsert) {
      this.openBraceForSelectInInsert = openBraceForSelectInInsert;
   }

   public void setPercentSpecifier(String s_ps) {
      this.percentSpecifier = s_ps;
   }

   public void setXMLString(String xmlString) {
      this.xmlString = xmlString;
   }

   public void setXMLEndTag(String endBracesForXMLString) {
      this.endBracesForXMLString = endBracesForXMLString;
   }

   public void setInsertValList(ArrayList insertValList) {
      this.insertValList = insertValList;
   }

   public void setHintClause(HintClause hintClause) {
      this.hintClause = hintClause;
   }

   public void setCommentClass(CommentClass commentObj) {
      this.commentObj = commentObj;
   }

   public void setOpenBraceForRowCount(String openBraceForRowCount) {
      this.openBraceForRowCount = openBraceForRowCount;
   }

   public void setClosedBraceForRowCount(String closedBraceForRowCount) {
      this.closedBraceForRowCount = closedBraceForRowCount;
   }

   public void setAliasNameVersion(Integer aliasNameVersion1) {
      this.aliasNameVersion = aliasNameVersion1;
   }

   public CommentClass getCommentClass() {
      return this.commentObj;
   }

   public String getSelectClause() {
      return this.selectClause;
   }

   public Vector getSelectItemList() {
      return this.selectItemList;
   }

   public String getSelectQualifier() {
      return this.selectQualifier;
   }

   public String getSelectRowSpecifier() {
      return this.selectRowSpecifier;
   }

   public String getInformixRowSpecifier() {
      return this.ifxSelectRowSpecifier;
   }

   public int getSelectRowCount() {
      return this.selectRowCount;
   }

   public int getIfxSelectRowCount() {
      return this.ifxSelectRowCount;
   }

   public String getSelectRowCountVariable() {
      return this.rowcountVariable;
   }

   public String getSelectSpecialQualifier() {
      return this.selectSpecialQualifier;
   }

   public Vector getDistinctList() {
      return this.distinctList;
   }

   public String getPercentSpecifier() {
      return this.percentSpecifier;
   }

   public HintClause getHintClause() {
      return this.hintClause;
   }

   public String getOpenBraceForRowCount() {
      return this.openBraceForRowCount;
   }

   public String getClosedBraceForRowCount() {
      return this.closedBraceForRowCount;
   }

   public Integer getAliasNameVersion() {
      return this.aliasNameVersion;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.openBraceForSelectInInsert != null) {
         sb.append(this.openBraceForSelectInInsert);
      }

      sb.append(this.selectClause.toUpperCase());
      if (this.reports != null) {
         sb.append(" " + this.reports.toUpperCase());
      }

      if (this.hintClause != null) {
         sb.append(" " + this.hintClause);
      }

      if (this.commentObj != null) {
         sb.append(this.commentObj.toString().trim() + " ");
      }

      if (this.ifxSelectRowSpecifier != null) {
         if (this.ifxSelectRowCount != null && this.ifxSelectRowCount != 0) {
            sb.append(" " + this.ifxSelectRowSpecifier.toUpperCase() + " " + this.ifxSelectRowCount);
         } else {
            sb.append(" " + this.ifxSelectRowSpecifier.toUpperCase() + " " + this.selectRowCount);
         }
      }

      int size;
      if (this.selectQualifier != null) {
         sb.append(" " + this.selectQualifier.toUpperCase());
         if (this.selectQualifier.equalsIgnoreCase("DISTINCT ON")) {
            sb.append("(");

            for(size = 0; size < this.distinctList.size(); ++size) {
               if (size == this.distinctList.size() - 1) {
                  sb.append(this.distinctList.elementAt(size).toString());
               } else {
                  sb.append(this.distinctList.elementAt(size).toString() + ",");
               }
            }

            sb.append(")");
         }
      }

      if (this.selectRowSpecifier != null) {
         if (this.rowcountVariable != null) {
            sb.append(" " + this.selectRowSpecifier.toUpperCase() + " (" + this.rowcountVariable + ")");
         } else {
            sb.append(" " + this.selectRowSpecifier.toUpperCase());
            if (this.openBraceForRowCount != null) {
               sb.append(this.openBraceForRowCount + this.selectRowCount + this.closedBraceForRowCount);
            } else {
               sb.append(" " + this.selectRowCount);
            }
         }

         if (this.percentSpecifier != null) {
            sb.append(" " + this.percentSpecifier.toUpperCase());
         }
      }

      if (this.selectSpecialQualifier != null) {
         sb.append(" " + this.selectSpecialQualifier.toUpperCase());
      }

      if (this.xmlString != null) {
         sb.append(" " + this.xmlString);
      }

      size = this.selectItemList.size();
      SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() + 2);

      for(int i = 0; i < size; ++i) {
         if (size > 1) {
            sb.append("\n");

            for(int j = 0; j < SelectQueryStatement.getBeautyTabCount(); ++j) {
               sb.append("\t");
            }
         }

         if (this.selectItemList.elementAt(i) instanceof SelectColumn) {
            ((SelectColumn)this.selectItemList.elementAt(i)).setObjectContext(this.context);
         }

         sb.append(" " + this.selectItemList.elementAt(i).toString());
      }

      if (this.endBracesForXMLString != null) {
         sb.append(this.endBracesForXMLString);
      }

      SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() - 2);
      return sb.toString();
   }

   public SelectStatement toANSISelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SelectStatement t_ss = new SelectStatement();
      t_ss.setSelectClause(this.selectClause);
      if (this.selectQualifier != null) {
         GroupByStatement gbs_gb = from_sqs.getGroupByStatement();
         HavingStatement hs = from_sqs.getHavingStatement();
         if (this.selectQualifier.equalsIgnoreCase("DISTINCT ON")) {
            if (gbs_gb != null) {
               to_sqs.setGroupByStatement(gbs_gb.toANSISelect(to_sqs, from_sqs));
               this.addGroupByItems(to_sqs, from_sqs);
            } else {
               to_sqs.setGroupByStatement(this.createGroupByStatement(to_sqs, from_sqs));
            }

            if (hs != null) {
               to_sqs.setHavingStatement(hs.toANSISelect(to_sqs, from_sqs));
               this.addHavingItem(to_sqs);
            } else {
               to_sqs.setHavingStatement(this.createHavingStatement());
            }

            this.setSelectItemList(t_ss, from_sqs, to_sqs);
         } else if (this.selectQualifier.equalsIgnoreCase("UNIQUE")) {
            t_ss.setSelectQualifier("DISTINCT");
         } else {
            t_ss.setSelectQualifier(this.selectQualifier);
         }
      }

      if (this.selectRowSpecifier != null) {
         throw new ConvertException();
      } else {
         if (this.selectSpecialQualifier != null) {
            StringBuffer sb = new StringBuffer();
            sb.append("/* " + this.selectSpecialQualifier + "*/");
            t_ss.setSelectSpecialQualifier(sb.toString());
         }

         if (this.selectQualifier != null && !this.selectQualifier.equalsIgnoreCase("DISTINCT ON") || this.selectQualifier == null) {
            Vector v_sil = new Vector();

            for(int i_count = 0; i_count < this.selectItemList.size(); ++i_count) {
               if (this.selectItemList.elementAt(i_count) instanceof SelectColumn) {
                  v_sil.addElement(((SelectColumn)this.selectItemList.elementAt(i_count)).toANSISelect(to_sqs, from_sqs));
               } else if (this.selectItemList.elementAt(i_count) instanceof WhereColumn) {
                  v_sil.addElement((WhereColumn)this.selectItemList.elementAt(i_count));
               } else {
                  v_sil.addElement((String)this.selectItemList.elementAt(i_count));
               }
            }

            t_ss.setSelectItemList(v_sil);
         }

         return t_ss;
      }
   }

   public SelectStatement toTeradataSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SelectStatement t_ss = new SelectStatement();
      t_ss.setSelectClause(this.selectClause);
      t_ss.setHintClause(this.hintClause);
      if (this.commentObj != null) {
         String commentStr = this.commentObj.toString();
         String commId = "%SSTD%";
         if (commentStr.indexOf(commId) != -1) {
            commentStr = commentStr.replaceAll("/\\*", "").replaceAll(commId, "").replaceAll("\\*/", "");
         }

         to_sqs.setTeradataComment(commentStr);
      }

      if (this.selectQualifier != null) {
         GroupByStatement gbs_gb = from_sqs.getGroupByStatement();
         HavingStatement hs = from_sqs.getHavingStatement();
         if (this.selectQualifier.equalsIgnoreCase("DISTINCT ON")) {
            if (gbs_gb != null) {
               to_sqs.setGroupByStatement(gbs_gb.toTeradataSelect(to_sqs, from_sqs));
               this.addGroupByItems(to_sqs, from_sqs);
            } else {
               to_sqs.setGroupByStatement(this.createGroupByStatement(to_sqs, from_sqs));
            }

            if (hs != null) {
               to_sqs.setHavingStatement(hs.toTeradataSelect(to_sqs, from_sqs));
               this.addHavingItem(to_sqs);
            } else {
               to_sqs.setHavingStatement(this.createHavingStatement());
            }

            this.setSelectItemList(t_ss, from_sqs, to_sqs);
         } else if (this.selectQualifier.equalsIgnoreCase("UNIQUE")) {
            t_ss.setSelectQualifier("DISTINCT");
         } else {
            t_ss.setSelectQualifier(this.selectQualifier);
         }
      }

      if (this.selectRowSpecifier != null) {
         throw new ConvertException();
      } else {
         if (this.selectSpecialQualifier != null) {
            StringBuffer sb = new StringBuffer();
            sb.append("/* " + this.selectSpecialQualifier + "*/");
            t_ss.setSelectSpecialQualifier(sb.toString());
         }

         if (this.selectQualifier != null && !this.selectQualifier.equalsIgnoreCase("DISTINCT ON") || this.selectQualifier == null) {
            Vector v_sil = new Vector();

            for(int i_count = 0; i_count < this.selectItemList.size(); ++i_count) {
               if (!(this.selectItemList.elementAt(i_count) instanceof SelectColumn)) {
                  if (this.selectItemList.elementAt(i_count) instanceof WhereColumn) {
                     v_sil.addElement((WhereColumn)this.selectItemList.elementAt(i_count));
                  } else {
                     v_sil.addElement((String)this.selectItemList.elementAt(i_count));
                  }
               } else {
                  SelectColumn sc1 = (SelectColumn)this.selectItemList.elementAt(i_count);
                  if (from_sqs != null && !from_sqs.getTopLevel()) {
                     if (sc1.getColumnExpression().size() == 1 && !(sc1.getColumnExpression().get(0) instanceof TableColumn) && sc1.getAliasName() == null) {
                        String aliasForExpr = sc1.getTheCoreSelectItem().trim();
                        if (sc1.getColumnExpression().get(0) instanceof SelectColumn && ((SelectColumn)sc1.getColumnExpression().get(0)).getColumnExpression().size() == 1) {
                           if (!(((SelectColumn)sc1.getColumnExpression().get(0)).getColumnExpression().get(0) instanceof TableColumn)) {
                              aliasForExpr = ((SelectColumn)sc1.getColumnExpression().get(0)).toString();
                           } else {
                              aliasForExpr = "";
                           }
                        }

                        if (aliasForExpr.lastIndexOf(",") != -1) {
                           aliasForExpr = aliasForExpr.substring(0, aliasForExpr.lastIndexOf(",")).trim();
                        }

                        if (aliasForExpr.indexOf("*/") != -1) {
                           aliasForExpr = aliasForExpr.substring(aliasForExpr.indexOf("*/") + 2).trim();
                        }

                        boolean isNum = false;

                        try {
                           Double.parseDouble(aliasForExpr);
                           isNum = true;
                        } catch (NumberFormatException var10) {
                           isNum = false;
                        }

                        if (!isNum && !aliasForExpr.toLowerCase().startsWith("case") && aliasForExpr.indexOf(".") != -1 && aliasForExpr.indexOf(".") == aliasForExpr.lastIndexOf(".") && aliasForExpr.indexOf("(") == -1) {
                           aliasForExpr = aliasForExpr.substring(aliasForExpr.lastIndexOf(".") + 1);
                        }

                        if (aliasForExpr.indexOf("/*") != -1) {
                           if (aliasForExpr.indexOf("/*") == 0 && aliasForExpr.indexOf("*/") < aliasForExpr.length() - 1) {
                              aliasForExpr = aliasForExpr.substring(aliasForExpr.indexOf("*/") + 1).trim();
                           } else {
                              aliasForExpr = aliasForExpr.substring(0, aliasForExpr.indexOf("/*")).trim();
                           }
                        }

                        if (!aliasForExpr.equalsIgnoreCase("*") && !aliasForExpr.startsWith("*") && !aliasForExpr.endsWith("*")) {
                           if (aliasForExpr.length() > 30) {
                              aliasForExpr = aliasForExpr.substring(0, 29);
                           }

                           if (aliasForExpr.length() > 0) {
                              sc1.setAliasForExpression("\"" + aliasForExpr.replaceAll("\n", " ").replaceAll("\t", " ") + "\"");
                           }
                        }
                     }

                     if (sc1.getAliasName() == null && sc1.getAliasForExpression() != null) {
                        sc1.setAliasName(sc1.getAliasForExpression());
                     }
                  }

                  v_sil.addElement(sc1.toTeradataSelect(to_sqs, from_sqs));
               }
            }

            t_ss.setSelectItemList(v_sil);
         }

         return t_ss;
      }
   }

   public SelectStatement toDB2Select(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SelectStatement t_ss = new SelectStatement();
      t_ss.setOpenBraceForSelectInInsertQuery(this.openBraceForSelectInInsert);
      t_ss.setSelectClause(this.selectClause);
      if (this.selectQualifier != null) {
         GroupByStatement gbs_gb = from_sqs.getGroupByStatement();
         HavingStatement hs = from_sqs.getHavingStatement();
         if (this.selectQualifier.equalsIgnoreCase("DISTINCT ON")) {
            if (gbs_gb != null) {
               to_sqs.setGroupByStatement(gbs_gb.toDB2Select(to_sqs, from_sqs));
               this.addGroupByItems(to_sqs, from_sqs);
            } else {
               to_sqs.setGroupByStatement(this.createGroupByStatement(to_sqs, from_sqs));
            }

            if (hs != null) {
               to_sqs.setHavingStatement(hs.toDB2Select(to_sqs, from_sqs));
               this.addHavingItem(to_sqs);
            } else {
               to_sqs.setHavingStatement(this.createHavingStatement());
            }

            this.setSelectItemList(t_ss, from_sqs, to_sqs);
         } else if (this.selectQualifier.equalsIgnoreCase("UNIQUE")) {
            t_ss.setSelectQualifier("DISTINCT");
         } else {
            t_ss.setSelectQualifier(this.selectQualifier);
         }
      }

      if (this.selectRowSpecifier != null || this.ifxSelectRowSpecifier != null) {
         if (this.percentSpecifier != null) {
            throw new ConvertException();
         }

         FetchClause fc = new FetchClause();
         if (from_sqs.getFetchClause() != null) {
            throw new ConvertException();
         }

         fc.setFetchFirstClause("FETCH FIRST");
         fc.setFetchCount("" + this.selectRowCount);
         fc.setRowOnlyClause("ROWS ONLY");
         to_sqs.setFetchClause(fc);
      }

      if (this.selectQualifier != null && !this.selectQualifier.equalsIgnoreCase("DISTINCT ON") || this.selectQualifier == null) {
         Vector v_sil = new Vector();
         int count = 0;

         for(int i_count = 0; i_count < this.selectItemList.size(); ++i_count) {
            if (!(this.selectItemList.elementAt(i_count) instanceof SelectColumn)) {
               if (this.selectItemList.elementAt(i_count) instanceof WhereColumn) {
                  v_sil.addElement((WhereColumn)this.selectItemList.elementAt(i_count));
               } else {
                  v_sil.addElement((String)this.selectItemList.elementAt(i_count));
               }
            } else {
               SelectColumn sc = (SelectColumn)this.selectItemList.elementAt(i_count);
               if (this.insertValList != null) {
                  sc.setCorrespondingTableColumn((TableColumn)this.insertValList.get(count));
               }

               Vector v_ce = sc.getColumnExpression();
               int ii_count = 0;

               while(true) {
                  if (ii_count >= v_ce.size()) {
                     sc.setColumnExpression(v_ce);
                     v_sil.addElement(((SelectColumn)this.selectItemList.elementAt(i_count)).toDB2Select(to_sqs, from_sqs));
                     break;
                  }

                  if (v_ce.elementAt(ii_count) instanceof String) {
                     String s_ce = (String)v_ce.elementAt(ii_count);
                     if (s_ce.equalsIgnoreCase("*") && v_ce.size() == 1 && this.selectItemList.size() > 1) {
                        FromClause fc = from_sqs.getFromClause();
                        Vector v_fil = fc.getFromItemList();
                        if (v_fil.size() > 1) {
                           for(int countNum = 0; countNum < v_fil.size(); ++countNum) {
                              if (v_fil.elementAt(countNum) instanceof FromTable) {
                                 FromTable ft = (FromTable)v_fil.elementAt(countNum);
                                 if (ft.getAliasName() == null) {
                                    Object o_tn = ft.getTableName();
                                    if (!(o_tn instanceof String)) {
                                       throw new ConvertException();
                                    }

                                    s_ce = (String)o_tn + ".*";
                                 } else {
                                    s_ce = ft.getAliasName() + ".*";
                                 }
                              } else if (v_fil.elementAt(countNum) instanceof FromClause) {
                                 Vector newFromItemList = ((FromClause)v_fil.get(countNum)).getFromItemList();
                                 this.changeTheSelectColumnWithStarToTableNameStar(v_ce, newFromItemList, ii_count);
                              }

                              if (countNum == 0) {
                                 v_ce.setElementAt(s_ce, ii_count);
                              } else {
                                 v_ce.add(",");
                                 v_ce.add(s_ce);
                              }
                           }
                        } else if (v_fil.elementAt(0) instanceof FromTable) {
                           FromTable ft = (FromTable)v_fil.elementAt(0);
                           if (ft.getAliasName() == null) {
                              Object o_tn = ft.getTableName();
                              if (!(o_tn instanceof String)) {
                                 throw new ConvertException();
                              }

                              s_ce = (String)o_tn + ".*";
                           } else {
                              s_ce = ft.getAliasName() + ".*";
                           }

                           v_ce.setElementAt(s_ce, ii_count);
                        } else if (v_fil.elementAt(0) instanceof FromClause) {
                           Vector newFCVector = ((FromClause)v_fil.get(0)).getFromItemList();
                           this.changeTheSelectColumnWithStarToTableNameStar(v_ce, newFCVector, ii_count);
                        }
                     }
                  }

                  ++ii_count;
               }
            }

            ++count;
         }

         t_ss.setSelectItemList(v_sil);
      }

      return t_ss;
   }

   public SelectStatement toMySQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      return this.toMySQLSelect(to_sqs, from_sqs, false);
   }

   public SelectStatement toMySQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs, Boolean fromOrigQuery) throws ConvertException {
      SelectStatement t_ss = new SelectStatement();
      t_ss.setSelectClause(this.selectClause);
      if (this.reports != null && !from_sqs.isMongoDb() && !from_sqs.isHyperSql()) {
         t_ss.setSpecialCase(this.reports);
      }

      if (this.selectQualifier != null) {
         GroupByStatement gbs_gb = from_sqs.getGroupByStatement();
         HavingStatement hs = from_sqs.getHavingStatement();
         if (this.selectQualifier.equalsIgnoreCase("DISTINCT ON")) {
            if (gbs_gb != null) {
               to_sqs.setGroupByStatement(gbs_gb.toMySQLSelect(to_sqs, from_sqs));
               this.addGroupByItems(to_sqs, from_sqs);
            } else {
               to_sqs.setGroupByStatement(this.createGroupByStatement(to_sqs, from_sqs));
            }

            if (hs != null) {
               to_sqs.setHavingStatement(hs.toMySQLSelect(to_sqs, from_sqs));
               this.addHavingItem(to_sqs);
            } else {
               to_sqs.setHavingStatement(this.createHavingStatement());
            }

            this.setSelectItemList(t_ss, from_sqs, to_sqs);
         } else if (this.selectQualifier.equalsIgnoreCase("UNIQUE")) {
            t_ss.setSelectQualifier("DISTINCT");
         } else {
            t_ss.setSelectQualifier(this.selectQualifier);
         }
      }

      if (this.selectRowSpecifier != null) {
         if (this.percentSpecifier != null) {
            throw new ConvertException();
         }

         LimitClause lc = new LimitClause();
         if (from_sqs.getLimitClause() != null) {
            throw new ConvertException();
         }

         lc.setLimitClause("LIMIT");
         lc.setLimitValue("" + this.selectRowCount);
         to_sqs.setLimitClause(lc);
      }

      if (this.selectSpecialQualifier != null) {
         t_ss.setSelectSpecialQualifier(this.selectSpecialQualifier);
      }

      if (this.selectQualifier != null && !this.selectQualifier.equalsIgnoreCase("DISTINCT ON") || this.selectQualifier == null) {
         Vector v_sil = new Vector();

         for(int i_count = 0; i_count < this.selectItemList.size(); ++i_count) {
            if (!(this.selectItemList.elementAt(i_count) instanceof SelectColumn)) {
               v_sil.addElement((String)this.selectItemList.elementAt(i_count));
            } else {
               SelectColumn sc = (SelectColumn)this.selectItemList.elementAt(i_count);
               if (this.aliasNameVersion != 0 && !sc.hasStarInSelectColumn()) {
                  if (sc.getAliasName() == null && sc.getAliasForExpression() == null) {
                     String orginalAliasName;
                     if (this.aliasNameVersion == 1) {
                        orginalAliasName = getProperAliasName(sc.toString());
                        orginalAliasName = "`" + orginalAliasName + "`";
                        sc.setAliasName(orginalAliasName);
                        sc.setIsAS("as");
                     } else if (this.aliasNameVersion == 2) {
                        try {
                           SwisSQLAPI.setProperSelExpTL();
                           orginalAliasName = sc.toString();
                           orginalAliasName = "`" + orginalAliasName + "`";
                           sc.setAliasName(orginalAliasName);
                        } catch (Exception var17) {
                           throw new ConvertException("Provide Alias Name");
                        } finally {
                           SwisSQLAPI.clearProperSelExpTL();
                        }
                     }
                  } else if (sc.getAliasName() != null && sc.getIsAS() == null) {
                     sc.setIsAS("as");
                  }
               }

               Vector v_ce = sc.getColumnExpression();

               for(int ii_count = 0; ii_count < v_ce.size(); ++ii_count) {
                  if (v_ce.elementAt(ii_count) instanceof String) {
                     String s_ce = (String)v_ce.elementAt(ii_count);
                     if (s_ce.equalsIgnoreCase("*") && v_ce.size() == 1 && this.selectItemList.size() > 1) {
                        FromClause fc = from_sqs.getFromClause();
                        Vector v_fil = fc.getFromItemList();
                        if (v_fil.size() <= 1) {
                           FromTable ft = (FromTable)v_fil.elementAt(0);
                           if (ft.getAliasName() != null) {
                              v_ce.setElementAt(ft.getAliasName(), ii_count);
                              v_ce.addElement(".*");
                              break;
                           }

                           Object o_tn = ft.getTableName();
                           if (!(o_tn instanceof String)) {
                              throw new ConvertException();
                           }
                        }
                     }
                  }
               }

               sc.setMainSelectColumn(true);
               sc.setColumnExpression(v_ce);
               v_sil.addElement(((SelectColumn)this.selectItemList.elementAt(i_count)).toMySQLSelect(to_sqs, from_sqs, false, fromOrigQuery));
            }
         }

         t_ss.setSelectItemList(v_sil);
      }

      return t_ss;
   }

   public HavingStatement createHavingStatement() {
      HavingStatement hs = new HavingStatement();
      Vector v_hi = new Vector();
      Vector v_fa = new Vector();
      SelectColumn sc = new SelectColumn();
      SelectColumn sc_new = new SelectColumn();
      SelectColumn sc_temp = new SelectColumn();
      Vector v_new = new Vector();
      Vector v_fi = new Vector();
      TableColumn tc = new TableColumn();
      FunctionCalls fc = new FunctionCalls();
      Vector vec_fa = new Vector();
      v_hi.addElement("(");
      tc.setColumnName("COUNT");
      fc.setFunctionName(tc);
      vec_fa.addElement("*");
      sc_new.setColumnExpression(vec_fa);
      v_fa.addElement(sc_new);
      fc.setFunctionArguments(v_fa);
      v_fi.addElement(fc);
      sc.setColumnExpression(v_fi);
      v_hi.addElement(sc);
      v_hi.addElement("<");
      v_new.addElement("2");
      sc_temp.setColumnExpression(v_new);
      v_hi.addElement(sc_temp);
      v_hi.addElement(")");
      hs.setHavingClause("HAVING");
      hs.setHavingItems(v_hi);
      return hs;
   }

   public void addHavingItem(SelectQueryStatement to_sqs) {
      HavingStatement hs = to_sqs.getHavingStatement();
      Vector v_to_hi = hs.getHavingItems();
      Vector v_hi = new Vector();

      for(int i_count = 0; i_count < v_to_hi.size(); ++i_count) {
         v_hi.addElement(v_to_hi.elementAt(i_count));
      }

      v_hi.addElement("AND");
      SelectColumn sc = new SelectColumn();
      SelectColumn sc_new = new SelectColumn();
      SelectColumn sc_temp = new SelectColumn();
      Vector v_new = new Vector();
      Vector v_fi = new Vector();
      Vector v_fa = new Vector();
      TableColumn tc = new TableColumn();
      FunctionCalls fc = new FunctionCalls();
      Vector vec_fa = new Vector();
      v_hi.addElement("(");
      tc.setColumnName("COUNT");
      fc.setFunctionName(tc);
      vec_fa.addElement("*");
      sc_new.setColumnExpression(vec_fa);
      v_fa.addElement(sc);
      fc.setFunctionArguments(v_fa);
      v_fi.addElement(fc);
      sc.setColumnExpression(v_fi);
      v_hi.addElement(sc);
      v_hi.addElement("<");
      v_new.addElement("2");
      sc_temp.setColumnExpression(v_new);
      v_hi.addElement(sc_temp);
      v_hi.addElement(")");
      hs.setHavingItems(v_hi);
   }

   public SelectStatement toBigQuerySelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SelectStatement t_ss = new SelectStatement();
      t_ss.setSelectClause(this.selectClause);
      Vector v_sil;
      int i_count;
      if (this.selectQualifier != null) {
         if (this.selectQualifier.equalsIgnoreCase("UNIQUE")) {
            t_ss.setSelectQualifier("DISTINCT");
         } else {
            t_ss.setSelectQualifier(this.selectQualifier);
         }

         v_sil = new Vector();
         if (this.distinctList != null) {
            for(i_count = 0; i_count < this.distinctList.size(); ++i_count) {
               v_sil.addElement(((SelectColumn)this.distinctList.elementAt(i_count)).toBigQuerySelect(to_sqs, from_sqs));
            }
         }

         t_ss.setDistinctList(v_sil);
      }

      if (this.selectRowSpecifier != null) {
         if (this.percentSpecifier != null) {
            throw new ConvertException();
         }

         LimitClause lc = new LimitClause();
         if (from_sqs.getLimitClause() != null) {
            throw new ConvertException();
         }

         lc.setLimitClause("LIMIT");
         lc.setLimitValue("" + this.selectRowCount);
         to_sqs.setLimitClause(lc);
      }

      if (this.selectSpecialQualifier != null) {
         StringBuffer sb = new StringBuffer();
         sb.append("/* " + this.selectSpecialQualifier + "*/");
         t_ss.setSelectSpecialQualifier(sb.toString());
      }

      if (this.selectItemList != null) {
         v_sil = new Vector();

         for(i_count = 0; i_count < this.selectItemList.size(); ++i_count) {
            if (!(this.selectItemList.elementAt(i_count) instanceof SelectColumn)) {
               if (this.selectItemList.elementAt(i_count) instanceof WhereColumn) {
                  v_sil.addElement((WhereColumn)this.selectItemList.elementAt(i_count));
               } else {
                  v_sil.addElement((String)this.selectItemList.elementAt(i_count));
               }
            } else {
               SelectColumn sc = (SelectColumn)this.selectItemList.elementAt(i_count);
               sc.convertSelectColumnToTextDataTypeIfChildSelectHasStringLiterals(i_count, this.getIndexPositionsSetForStringLiterals(from_sqs));
               if (from_sqs != null) {
                  if (from_sqs.isSelectWithoutSetClause()) {
                     sc.convertSelectColumnToNumericDataType("SIGNED");
                  } else {
                     sc.trackIndexPositionsForCastingNULLString(i_count, from_sqs.isFirstSelectStatementInSetQuery(), this.getIndexPositionsSetForNULLString(from_sqs));
                  }
               }

               Vector v_ce = sc.getColumnExpression();

               for(int ii_count = 0; ii_count < v_ce.size(); ++ii_count) {
                  if (v_ce.elementAt(ii_count) instanceof String) {
                     String s_ce = (String)v_ce.elementAt(ii_count);
                     if (s_ce.equalsIgnoreCase("*") && v_ce.size() == 1 && this.selectItemList.size() > 1) {
                        FromClause fc = from_sqs.getFromClause();
                        Vector v_fil = fc.getFromItemList();
                        if (v_fil.size() <= 1) {
                           FromTable ft = (FromTable)v_fil.elementAt(0);
                           if (ft.getAliasName() == null) {
                              Object o_tn = ft.getTableName();
                              if (!(o_tn instanceof String)) {
                                 throw new ConvertException();
                              }
                           } else {
                              s_ce = ft.getAliasName() + ".*";
                           }

                           v_ce.setElementAt(s_ce, ii_count);
                        }
                     }
                  } else if (v_ce.elementAt(ii_count) instanceof TableColumn) {
                     TableColumn tc = (TableColumn)v_ce.elementAt(ii_count);
                     tc.setColumnName(tc.getColumnName());
                     tc.setTableName(tc.getTableName());
                  }
               }

               sc.setColumnExpression(v_ce);
               if (to_sqs != null) {
                  to_sqs.setCurrentIndexPosition(i_count);
               }

               v_sil.addElement(((SelectColumn)this.selectItemList.elementAt(i_count)).toBigQuerySelect(to_sqs, from_sqs));
            }
         }

         if (to_sqs != null) {
            to_sqs.setCurrentIndexPosition(-1);
         }

         t_ss.setSelectItemList(v_sil);
      }

      return t_ss;
   }

   public SelectStatement toPostgreSQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SelectStatement t_ss = new SelectStatement();
      t_ss.setSelectClause(this.selectClause);
      Vector v_sil;
      int i_count;
      if (this.selectQualifier != null) {
         if (this.selectQualifier.equalsIgnoreCase("UNIQUE")) {
            t_ss.setSelectQualifier("DISTINCT");
         } else {
            t_ss.setSelectQualifier(this.selectQualifier);
         }

         v_sil = new Vector();
         if (this.distinctList != null) {
            for(i_count = 0; i_count < this.distinctList.size(); ++i_count) {
               v_sil.addElement(((SelectColumn)this.distinctList.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
            }
         }

         t_ss.setDistinctList(v_sil);
      }

      if (this.selectRowSpecifier != null) {
         if (this.percentSpecifier != null) {
            throw new ConvertException();
         }

         LimitClause lc = new LimitClause();
         if (from_sqs.getLimitClause() != null) {
            throw new ConvertException();
         }

         lc.setLimitClause("LIMIT");
         lc.setLimitValue("" + this.selectRowCount);
         to_sqs.setLimitClause(lc);
      }

      if (this.selectSpecialQualifier != null) {
         StringBuffer sb = new StringBuffer();
         sb.append("/* " + this.selectSpecialQualifier + "*/");
         t_ss.setSelectSpecialQualifier(sb.toString());
      }

      if (this.selectItemList != null) {
         v_sil = new Vector();

         for(i_count = 0; i_count < this.selectItemList.size(); ++i_count) {
            if (!(this.selectItemList.elementAt(i_count) instanceof SelectColumn)) {
               if (this.selectItemList.elementAt(i_count) instanceof WhereColumn) {
                  v_sil.addElement((WhereColumn)this.selectItemList.elementAt(i_count));
               } else {
                  v_sil.addElement((String)this.selectItemList.elementAt(i_count));
               }
            } else {
               SelectColumn sc = (SelectColumn)this.selectItemList.elementAt(i_count);
               sc.convertSelectColumnToTextDataTypeIfChildSelectHasStringLiterals(i_count, this.getIndexPositionsSetForStringLiterals(from_sqs));
               if (from_sqs != null) {
                  if (!from_sqs.isSelectWithoutSetClause() || from_sqs.getBooleanValues("can.cast.set.query.list.sel.cols") && from_sqs.isSQSWithSetOperatorClList()) {
                     sc.trackIndexPositionsForCastingNULLString(i_count, from_sqs.isFirstSelectStatementInSetQuery(), this.getIndexPositionsSetForNULLString(from_sqs));
                  } else {
                     sc.convertSelectColumnToNumericDataType("SIGNED");
                  }
               }

               Vector v_ce = sc.getColumnExpression();

               for(int ii_count = 0; ii_count < v_ce.size(); ++ii_count) {
                  if (v_ce.elementAt(ii_count) instanceof String) {
                     String s_ce = (String)v_ce.elementAt(ii_count);
                     if (s_ce.equalsIgnoreCase("*") && v_ce.size() == 1 && this.selectItemList.size() > 1) {
                        FromClause fc = from_sqs.getFromClause();
                        Vector v_fil = fc.getFromItemList();
                        if (v_fil.size() <= 1) {
                           FromTable ft = (FromTable)v_fil.elementAt(0);
                           if (ft.getAliasName() == null) {
                              Object o_tn = ft.getTableName();
                              if (!(o_tn instanceof String)) {
                                 throw new ConvertException();
                              }
                           } else {
                              s_ce = ft.getAliasName() + ".*";
                           }

                           v_ce.setElementAt(changeBackTip(s_ce), ii_count);
                        }
                     }
                  } else if (v_ce.elementAt(ii_count) instanceof TableColumn) {
                     TableColumn tc = (TableColumn)v_ce.elementAt(ii_count);
                     tc.setColumnName(checkandRemoveDoubleQuoteForPostgresIdentifier(changeBackTip(tc.getColumnName()), from_sqs == null ? false : from_sqs.getReportsMeta()));
                     tc.setTableName(checkandRemoveDoubleQuoteForPostgresIdentifier(changeBackTip(tc.getTableName()), from_sqs == null ? false : from_sqs.getReportsMeta()));
                  }
               }

               sc.setColumnExpression(v_ce);
               if (to_sqs != null) {
                  to_sqs.setCurrentIndexPosition(i_count);
               }

               v_sil.addElement(((SelectColumn)this.selectItemList.elementAt(i_count)).toPostgreSQLSelect(to_sqs, from_sqs));
            }
         }

         if (to_sqs != null) {
            to_sqs.setCurrentIndexPosition(-1);
         }

         t_ss.setSelectItemList(v_sil);
      }

      return t_ss;
   }

   public SelectStatement toMSSQLServerSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SelectStatement t_ss = new SelectStatement();
      t_ss.setSelectClause(this.selectClause);
      t_ss.setCommentClass(this.commentObj);
      if (this.selectQualifier != null) {
         GroupByStatement gbs_gb = from_sqs.getGroupByStatement();
         HavingStatement hs = from_sqs.getHavingStatement();
         if (this.selectQualifier.equalsIgnoreCase("DISTINCT ON")) {
            if (gbs_gb != null) {
               to_sqs.setGroupByStatement(gbs_gb.toMSSQLServerSelect(to_sqs, from_sqs));
               this.addGroupByItems(to_sqs, from_sqs);
            } else {
               to_sqs.setGroupByStatement(this.createGroupByStatement(to_sqs, from_sqs));
            }

            if (hs != null) {
               to_sqs.setHavingStatement(hs.toMSSQLServerSelect(to_sqs, from_sqs));
               this.addHavingItem(to_sqs);
            } else {
               to_sqs.setHavingStatement(this.createHavingStatement());
            }

            this.setSelectItemList(t_ss, from_sqs, to_sqs);
         } else if (this.selectQualifier.equalsIgnoreCase("UNIQUE")) {
            t_ss.setSelectQualifier("DISTINCT");
         } else {
            t_ss.setSelectQualifier(this.selectQualifier);
         }
      }

      if (this.selectRowSpecifier != null || this.ifxSelectRowSpecifier != null) {
         if (this.selectRowSpecifier != null && this.selectRowSpecifier.equalsIgnoreCase("FIRST")) {
            t_ss.setSelectRowSpecifier("TOP");
         } else if (this.ifxSelectRowSpecifier != null && this.ifxSelectRowSpecifier.equalsIgnoreCase("FIRST")) {
            t_ss.setSelectRowSpecifier("TOP");
            this.ifxSelectRowSpecifier = null;
         } else {
            t_ss.setSelectRowSpecifier(this.selectRowSpecifier);
            t_ss.setOpenBraceForRowCount(this.openBraceForRowCount);
            t_ss.setClosedBraceForRowCount(this.closedBraceForRowCount);
            t_ss.setSelectRowCountVariable(this.rowcountVariable);
         }

         t_ss.setSelectRowCount(this.selectRowCount);
      }

      if (this.selectSpecialQualifier != null) {
         StringBuffer sb = new StringBuffer();
         sb.append("/* " + this.selectSpecialQualifier + "*/");
         t_ss.setSelectSpecialQualifier(sb.toString());
      }

      if (this.selectQualifier != null && !this.selectQualifier.equalsIgnoreCase("DISTINCT ON") || this.selectQualifier == null) {
         Vector v_sil = new Vector();

         for(int i_count = 0; i_count < this.selectItemList.size(); ++i_count) {
            if (this.selectItemList.elementAt(i_count) instanceof SelectColumn) {
               v_sil.addElement(((SelectColumn)this.selectItemList.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
            } else if (this.selectItemList.elementAt(i_count) instanceof WhereColumn) {
               v_sil.addElement((WhereColumn)this.selectItemList.elementAt(i_count));
            } else {
               v_sil.addElement((String)this.selectItemList.elementAt(i_count));
            }
         }

         t_ss.setSelectItemList(v_sil);
      }

      t_ss.setPercentSpecifier(this.percentSpecifier);
      return t_ss;
   }

   public SelectStatement toSnowflakeSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SelectStatement t_ss = new SelectStatement();
      t_ss.setSelectClause(this.selectClause);
      Vector v_sil;
      int i_count;
      if (this.selectQualifier != null) {
         if (this.selectQualifier.equalsIgnoreCase("UNIQUE")) {
            t_ss.setSelectQualifier("DISTINCT");
         } else {
            t_ss.setSelectQualifier(this.selectQualifier);
         }

         v_sil = new Vector();
         if (this.distinctList != null) {
            for(i_count = 0; i_count < this.distinctList.size(); ++i_count) {
               v_sil.addElement(((SelectColumn)this.distinctList.elementAt(i_count)).toSnowflakeSelect(to_sqs, from_sqs));
            }
         }

         t_ss.setDistinctList(v_sil);
      }

      if (this.selectRowSpecifier != null) {
         if (this.percentSpecifier != null) {
            throw new ConvertException();
         }

         LimitClause lc = new LimitClause();
         if (from_sqs.getLimitClause() != null) {
            throw new ConvertException();
         }

         lc.setLimitClause("LIMIT");
         lc.setLimitValue("" + this.selectRowCount);
         to_sqs.setLimitClause(lc);
      }

      if (this.selectSpecialQualifier != null) {
         StringBuffer sb = new StringBuffer();
         sb.append("/* " + this.selectSpecialQualifier + "*/");
         t_ss.setSelectSpecialQualifier(sb.toString());
      }

      if (this.selectItemList != null) {
         v_sil = new Vector();

         for(i_count = 0; i_count < this.selectItemList.size(); ++i_count) {
            if (!(this.selectItemList.elementAt(i_count) instanceof SelectColumn)) {
               if (this.selectItemList.elementAt(i_count) instanceof WhereColumn) {
                  v_sil.addElement((WhereColumn)this.selectItemList.elementAt(i_count));
               } else {
                  v_sil.addElement((String)this.selectItemList.elementAt(i_count));
               }
            } else {
               SelectColumn sc = (SelectColumn)this.selectItemList.elementAt(i_count);
               sc.convertSelectColumnToTextDataTypeIfChildSelectHasStringLiterals(i_count, this.getIndexPositionsSetForStringLiterals(from_sqs));
               if (from_sqs != null) {
                  if (from_sqs.isSelectWithoutSetClause()) {
                     sc.convertSelectColumnToNumericDataType("SIGNED");
                  } else {
                     sc.trackIndexPositionsForCastingNULLString(i_count, from_sqs.isFirstSelectStatementInSetQuery(), this.getIndexPositionsSetForNULLString(from_sqs));
                  }
               }

               Vector v_ce = sc.getColumnExpression();

               for(int ii_count = 0; ii_count < v_ce.size(); ++ii_count) {
                  if (v_ce.elementAt(ii_count) instanceof String) {
                     String s_ce = (String)v_ce.elementAt(ii_count);
                     if (s_ce.equalsIgnoreCase("*") && v_ce.size() == 1 && this.selectItemList.size() > 1) {
                        FromClause fc = from_sqs.getFromClause();
                        Vector v_fil = fc.getFromItemList();
                        if (v_fil.size() <= 1) {
                           FromTable ft = (FromTable)v_fil.elementAt(0);
                           if (ft.getAliasName() == null) {
                              Object o_tn = ft.getTableName();
                              if (!(o_tn instanceof String)) {
                                 throw new ConvertException();
                              }
                           } else {
                              s_ce = ft.getAliasName() + ".*";
                           }

                           v_ce.setElementAt(changeBackTip(s_ce), ii_count);
                        }
                     }
                  } else if (v_ce.elementAt(ii_count) instanceof TableColumn) {
                     TableColumn tc = (TableColumn)v_ce.elementAt(ii_count);
                     tc.setColumnName(changeBackTip(tc.getColumnName()));
                     tc.setTableName(changeBackTip(tc.getTableName()));
                  }
               }

               sc.setColumnExpression(v_ce);
               if (to_sqs != null) {
                  to_sqs.setCurrentIndexPosition(i_count);
               }

               v_sil.addElement(((SelectColumn)this.selectItemList.elementAt(i_count)).toSnowflakeSelect(to_sqs, from_sqs));
            }
         }

         if (to_sqs != null) {
            to_sqs.setCurrentIndexPosition(-1);
         }

         t_ss.setSelectItemList(v_sil);
      }

      return t_ss;
   }

   public SelectStatement toAthenaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SelectStatement t_ss = new SelectStatement();
      t_ss.setSelectClause(this.selectClause);
      Vector v_sil;
      int i_count;
      if (this.selectQualifier != null) {
         if (this.selectQualifier.equalsIgnoreCase("UNIQUE")) {
            t_ss.setSelectQualifier("DISTINCT");
         } else {
            t_ss.setSelectQualifier(this.selectQualifier);
         }

         v_sil = new Vector();
         if (this.distinctList != null) {
            for(i_count = 0; i_count < this.distinctList.size(); ++i_count) {
               v_sil.addElement(((SelectColumn)this.distinctList.elementAt(i_count)).toAthenaSelect(to_sqs, from_sqs));
            }
         }

         t_ss.setDistinctList(v_sil);
      }

      if (this.selectRowSpecifier != null) {
         if (this.percentSpecifier != null) {
            throw new ConvertException();
         }

         LimitClause lc = new LimitClause();
         if (from_sqs.getLimitClause() != null) {
            throw new ConvertException();
         }

         lc.setLimitClause("LIMIT");
         lc.setLimitValue("" + this.selectRowCount);
         to_sqs.setLimitClause(lc);
      }

      if (this.selectSpecialQualifier != null) {
         StringBuffer sb = new StringBuffer();
         sb.append("/* " + this.selectSpecialQualifier + "*/");
         t_ss.setSelectSpecialQualifier(sb.toString());
      }

      if (this.selectItemList != null) {
         v_sil = new Vector();

         for(i_count = 0; i_count < this.selectItemList.size(); ++i_count) {
            if (!(this.selectItemList.elementAt(i_count) instanceof SelectColumn)) {
               if (this.selectItemList.elementAt(i_count) instanceof WhereColumn) {
                  v_sil.addElement((WhereColumn)this.selectItemList.elementAt(i_count));
               } else {
                  v_sil.addElement((String)this.selectItemList.elementAt(i_count));
               }
            } else {
               SelectColumn sc = (SelectColumn)this.selectItemList.elementAt(i_count);
               sc.convertSelectColumnToTextDataTypeIfChildSelectHasStringLiterals(i_count, this.getIndexPositionsSetForStringLiterals(from_sqs));
               if (from_sqs != null) {
                  if (from_sqs.isSelectWithoutSetClause()) {
                     sc.convertSelectColumnToNumericDataType("SIGNED");
                  } else {
                     sc.trackIndexPositionsForCastingNULLString(i_count, from_sqs.isFirstSelectStatementInSetQuery(), this.getIndexPositionsSetForNULLString(from_sqs));
                  }
               }

               Vector v_ce = sc.getColumnExpression();

               for(int ii_count = 0; ii_count < v_ce.size(); ++ii_count) {
                  if (v_ce.elementAt(ii_count) instanceof String) {
                     String s_ce = (String)v_ce.elementAt(ii_count);
                     if (s_ce.equalsIgnoreCase("*") && v_ce.size() == 1 && this.selectItemList.size() > 1) {
                        FromClause fc = from_sqs.getFromClause();
                        Vector v_fil = fc.getFromItemList();
                        if (v_fil.size() <= 1) {
                           FromTable ft = (FromTable)v_fil.elementAt(0);
                           if (ft.getAliasName() == null) {
                              Object o_tn = ft.getTableName();
                              if (!(o_tn instanceof String)) {
                                 throw new ConvertException();
                              }
                           } else {
                              s_ce = ft.getAliasName() + ".*";
                           }

                           v_ce.setElementAt(s_ce, ii_count);
                        }
                     }
                  } else if (v_ce.elementAt(ii_count) instanceof TableColumn) {
                     TableColumn tc = (TableColumn)v_ce.elementAt(ii_count);
                     tc.setColumnName(tc.getColumnName());
                     tc.setTableName(tc.getTableName());
                  }
               }

               sc.setColumnExpression(v_ce);
               if (to_sqs != null) {
                  to_sqs.setCurrentIndexPosition(i_count);
               }

               v_sil.addElement(((SelectColumn)this.selectItemList.elementAt(i_count)).toAthenaSelect(to_sqs, from_sqs));
            }
         }

         if (to_sqs != null) {
            to_sqs.setCurrentIndexPosition(-1);
         }

         t_ss.setSelectItemList(v_sil);
      }

      return t_ss;
   }

   public SelectStatement toSapHanaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SelectStatement t_ss = new SelectStatement();
      t_ss.setSelectClause(this.selectClause);
      Vector v_sil;
      int i_count;
      if (this.selectQualifier != null) {
         if (this.selectQualifier.equalsIgnoreCase("UNIQUE")) {
            t_ss.setSelectQualifier("DISTINCT");
         } else {
            t_ss.setSelectQualifier(this.selectQualifier);
         }

         v_sil = new Vector();
         if (this.distinctList != null) {
            for(i_count = 0; i_count < this.distinctList.size(); ++i_count) {
               v_sil.addElement(((SelectColumn)this.distinctList.elementAt(i_count)).toSapHanaSelect(to_sqs, from_sqs));
            }
         }

         t_ss.setDistinctList(v_sil);
      }

      if (this.selectRowSpecifier != null) {
         if (this.percentSpecifier != null) {
            throw new ConvertException();
         }

         LimitClause lc = new LimitClause();
         if (from_sqs.getLimitClause() != null) {
            throw new ConvertException();
         }

         lc.setLimitClause("LIMIT");
         lc.setLimitValue("" + this.selectRowCount);
         to_sqs.setLimitClause(lc);
      }

      if (this.selectSpecialQualifier != null) {
         StringBuffer sb = new StringBuffer();
         sb.append("/* " + this.selectSpecialQualifier + "*/");
         t_ss.setSelectSpecialQualifier(sb.toString());
      }

      if (this.selectItemList != null) {
         v_sil = new Vector();

         for(i_count = 0; i_count < this.selectItemList.size(); ++i_count) {
            if (!(this.selectItemList.elementAt(i_count) instanceof SelectColumn)) {
               if (this.selectItemList.elementAt(i_count) instanceof WhereColumn) {
                  v_sil.addElement((WhereColumn)this.selectItemList.elementAt(i_count));
               } else {
                  v_sil.addElement((String)this.selectItemList.elementAt(i_count));
               }
            } else {
               SelectColumn sc = (SelectColumn)this.selectItemList.elementAt(i_count);
               sc.convertSelectColumnToTextDataTypeIfChildSelectHasStringLiterals(i_count, this.getIndexPositionsSetForStringLiterals(from_sqs));
               if (from_sqs != null) {
                  if (from_sqs.isSelectWithoutSetClause()) {
                     sc.convertSelectColumnToNumericDataType("SIGNED");
                  } else {
                     sc.trackIndexPositionsForCastingNULLString(i_count, from_sqs.isFirstSelectStatementInSetQuery(), this.getIndexPositionsSetForNULLString(from_sqs));
                  }
               }

               Vector v_ce = sc.getColumnExpression();

               for(int ii_count = 0; ii_count < v_ce.size(); ++ii_count) {
                  if (v_ce.elementAt(ii_count) instanceof String) {
                     String s_ce = (String)v_ce.elementAt(ii_count);
                     if (s_ce.equalsIgnoreCase("*") && v_ce.size() == 1 && this.selectItemList.size() > 1) {
                        FromClause fc = from_sqs.getFromClause();
                        Vector v_fil = fc.getFromItemList();
                        if (v_fil.size() <= 1) {
                           FromTable ft = (FromTable)v_fil.elementAt(0);
                           if (ft.getAliasName() == null) {
                              Object o_tn = ft.getTableName();
                              if (!(o_tn instanceof String)) {
                                 throw new ConvertException();
                              }
                           } else {
                              s_ce = ft.getAliasName() + ".*";
                           }

                           v_ce.setElementAt(s_ce, ii_count);
                        }
                     }
                  } else if (v_ce.elementAt(ii_count) instanceof TableColumn) {
                     TableColumn tc = (TableColumn)v_ce.elementAt(ii_count);
                     tc.setColumnName(tc.getColumnName());
                     tc.setTableName(tc.getTableName());
                  }
               }

               sc.setColumnExpression(v_ce);
               if (to_sqs != null) {
                  to_sqs.setCurrentIndexPosition(i_count);
               }

               v_sil.addElement(((SelectColumn)this.selectItemList.elementAt(i_count)).toSapHanaSelect(to_sqs, from_sqs));
            }
         }

         if (to_sqs != null) {
            to_sqs.setCurrentIndexPosition(-1);
         }

         t_ss.setSelectItemList(v_sil);
      }

      return t_ss;
   }

   public SelectStatement toSqliteSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SelectStatement t_ss = new SelectStatement();
      t_ss.setSelectClause(this.selectClause);
      Vector v_sil;
      int i_count;
      if (this.selectQualifier != null) {
         if (this.selectQualifier.equalsIgnoreCase("UNIQUE")) {
            t_ss.setSelectQualifier("DISTINCT");
         } else {
            t_ss.setSelectQualifier(this.selectQualifier);
         }

         v_sil = new Vector();
         if (this.distinctList != null) {
            for(i_count = 0; i_count < this.distinctList.size(); ++i_count) {
               v_sil.addElement(((SelectColumn)this.distinctList.elementAt(i_count)).toSqliteSelect(to_sqs, from_sqs));
            }
         }

         t_ss.setDistinctList(v_sil);
      }

      if (this.selectRowSpecifier != null) {
         if (this.percentSpecifier != null) {
            throw new ConvertException();
         }

         LimitClause lc = new LimitClause();
         if (from_sqs.getLimitClause() != null) {
            throw new ConvertException();
         }

         lc.setLimitClause("LIMIT");
         lc.setLimitValue("" + this.selectRowCount);
         to_sqs.setLimitClause(lc);
      }

      if (this.selectSpecialQualifier != null) {
         StringBuffer sb = new StringBuffer();
         sb.append("/* " + this.selectSpecialQualifier + "*/");
         t_ss.setSelectSpecialQualifier(sb.toString());
      }

      if (this.selectItemList != null) {
         v_sil = new Vector();

         for(i_count = 0; i_count < this.selectItemList.size(); ++i_count) {
            if (!(this.selectItemList.elementAt(i_count) instanceof SelectColumn)) {
               if (this.selectItemList.elementAt(i_count) instanceof WhereColumn) {
                  v_sil.addElement((WhereColumn)this.selectItemList.elementAt(i_count));
               } else {
                  v_sil.addElement((String)this.selectItemList.elementAt(i_count));
               }
            } else {
               SelectColumn sc = (SelectColumn)this.selectItemList.elementAt(i_count);
               sc.convertSelectColumnToTextDataTypeIfChildSelectHasStringLiterals(i_count, this.getIndexPositionsSetForStringLiterals(from_sqs));
               if (from_sqs != null) {
                  if (from_sqs.isSelectWithoutSetClause()) {
                     sc.convertSelectColumnToNumericDataType("INTEGER");
                  } else {
                     sc.trackIndexPositionsForCastingNULLString(i_count, from_sqs.isFirstSelectStatementInSetQuery(), this.getIndexPositionsSetForNULLString(from_sqs));
                  }
               }

               Vector v_ce = sc.getColumnExpression();

               for(int ii_count = 0; ii_count < v_ce.size(); ++ii_count) {
                  if (v_ce.elementAt(ii_count) instanceof String) {
                     String s_ce = (String)v_ce.elementAt(ii_count);
                     if (s_ce.equalsIgnoreCase("*") && v_ce.size() == 1 && this.selectItemList.size() > 1) {
                        FromClause fc = from_sqs.getFromClause();
                        Vector v_fil = fc.getFromItemList();
                        if (v_fil.size() <= 1) {
                           FromTable ft = (FromTable)v_fil.elementAt(0);
                           if (ft.getAliasName() == null) {
                              Object o_tn = ft.getTableName();
                              if (!(o_tn instanceof String)) {
                                 throw new ConvertException();
                              }
                           } else {
                              s_ce = ft.getAliasName() + ".*";
                           }

                           v_ce.setElementAt(s_ce, ii_count);
                        }
                     }
                  } else if (v_ce.elementAt(ii_count) instanceof TableColumn) {
                     TableColumn tc = (TableColumn)v_ce.elementAt(ii_count);
                     tc.setColumnName(tc.getColumnName());
                     tc.setTableName(tc.getTableName());
                  }
               }

               sc.setColumnExpression(v_ce);
               if (to_sqs != null) {
                  to_sqs.setCurrentIndexPosition(i_count);
               }

               v_sil.addElement(((SelectColumn)this.selectItemList.elementAt(i_count)).toSqliteSelect(to_sqs, from_sqs));
            }
         }

         if (to_sqs != null) {
            to_sqs.setCurrentIndexPosition(-1);
         }

         t_ss.setSelectItemList(v_sil);
      }

      return t_ss;
   }

   public SelectStatement toExcelSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SelectStatement t_ss = new SelectStatement();
      t_ss.setSelectClause(this.selectClause);
      Vector v_sil;
      int i_count;
      if (this.selectQualifier != null) {
         if (this.selectQualifier.equalsIgnoreCase("UNIQUE")) {
            t_ss.setSelectQualifier("DISTINCT");
         } else {
            t_ss.setSelectQualifier(this.selectQualifier);
         }

         v_sil = new Vector();
         if (this.distinctList != null) {
            for(i_count = 0; i_count < this.distinctList.size(); ++i_count) {
               v_sil.addElement(((SelectColumn)this.distinctList.elementAt(i_count)).toExcelSelect(to_sqs, from_sqs));
            }
         }

         t_ss.setDistinctList(v_sil);
      }

      if (this.selectRowSpecifier != null) {
         if (this.percentSpecifier != null) {
            throw new ConvertException();
         }

         LimitClause lc = new LimitClause();
         if (from_sqs.getLimitClause() != null) {
            throw new ConvertException();
         }

         lc.setLimitClause("LIMIT");
         lc.setLimitValue("" + this.selectRowCount);
         to_sqs.setLimitClause(lc);
      }

      if (this.selectSpecialQualifier != null) {
         StringBuffer sb = new StringBuffer();
         sb.append("/* " + this.selectSpecialQualifier + "*/");
         t_ss.setSelectSpecialQualifier(sb.toString());
      }

      if (this.selectItemList != null) {
         v_sil = new Vector();

         for(i_count = 0; i_count < this.selectItemList.size(); ++i_count) {
            if (!(this.selectItemList.elementAt(i_count) instanceof SelectColumn)) {
               if (this.selectItemList.elementAt(i_count) instanceof WhereColumn) {
                  v_sil.addElement((WhereColumn)this.selectItemList.elementAt(i_count));
               } else {
                  v_sil.addElement((String)this.selectItemList.elementAt(i_count));
               }
            } else {
               SelectColumn sc = (SelectColumn)this.selectItemList.elementAt(i_count);
               sc.convertSelectColumnToTextDataTypeIfChildSelectHasStringLiterals(i_count, this.getIndexPositionsSetForStringLiterals(from_sqs));
               if (from_sqs != null) {
                  if (from_sqs.isSelectWithoutSetClause()) {
                     sc.convertSelectColumnToNumericDataType("INTEGER");
                  } else {
                     sc.trackIndexPositionsForCastingNULLString(i_count, from_sqs.isFirstSelectStatementInSetQuery(), this.getIndexPositionsSetForNULLString(from_sqs));
                  }
               }

               Vector v_ce = sc.getColumnExpression();

               for(int ii_count = 0; ii_count < v_ce.size(); ++ii_count) {
                  if (v_ce.elementAt(ii_count) instanceof String) {
                     String s_ce = (String)v_ce.elementAt(ii_count);
                     if (s_ce.equalsIgnoreCase("*") && v_ce.size() == 1 && this.selectItemList.size() > 1) {
                        FromClause fc = from_sqs.getFromClause();
                        Vector v_fil = fc.getFromItemList();
                        if (v_fil.size() <= 1) {
                           FromTable ft = (FromTable)v_fil.elementAt(0);
                           if (ft.getAliasName() == null) {
                              Object o_tn = ft.getTableName();
                              if (!(o_tn instanceof String)) {
                                 throw new ConvertException();
                              }
                           } else {
                              s_ce = ft.getAliasName() + ".*";
                           }

                           v_ce.setElementAt(s_ce, ii_count);
                        }
                     }
                  } else if (v_ce.elementAt(ii_count) instanceof TableColumn) {
                     TableColumn tc = (TableColumn)v_ce.elementAt(ii_count);
                     tc.setColumnName(tc.getColumnName());
                     tc.setTableName(tc.getTableName());
                  }
               }

               sc.setColumnExpression(v_ce);
               if (to_sqs != null) {
                  to_sqs.setCurrentIndexPosition(i_count);
               }

               v_sil.addElement(((SelectColumn)this.selectItemList.elementAt(i_count)).toExcelSelect(to_sqs, from_sqs));
            }
         }

         if (to_sqs != null) {
            to_sqs.setCurrentIndexPosition(-1);
         }

         t_ss.setSelectItemList(v_sil);
      }

      return t_ss;
   }

   public SelectStatement toMsAccessJdbcSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SelectStatement t_ss = new SelectStatement();
      t_ss.setSelectClause(this.selectClause);
      Vector v_sil;
      int i_count;
      if (this.selectQualifier != null) {
         if (this.selectQualifier.equalsIgnoreCase("UNIQUE")) {
            t_ss.setSelectQualifier("DISTINCT");
         } else {
            t_ss.setSelectQualifier(this.selectQualifier);
         }

         v_sil = new Vector();
         if (this.distinctList != null) {
            for(i_count = 0; i_count < this.distinctList.size(); ++i_count) {
               v_sil.addElement(((SelectColumn)this.distinctList.elementAt(i_count)).toMsAccessJdbcSelect(to_sqs, from_sqs));
            }
         }

         t_ss.setDistinctList(v_sil);
      }

      if (this.selectRowSpecifier != null) {
         if (this.percentSpecifier != null) {
            throw new ConvertException();
         }

         LimitClause lc = new LimitClause();
         if (from_sqs.getLimitClause() != null) {
            throw new ConvertException();
         }

         lc.setLimitClause("LIMIT");
         lc.setLimitValue("" + this.selectRowCount);
         to_sqs.setLimitClause(lc);
      }

      if (this.selectSpecialQualifier != null) {
         StringBuffer sb = new StringBuffer();
         sb.append("/* " + this.selectSpecialQualifier + "*/");
         t_ss.setSelectSpecialQualifier(sb.toString());
      }

      if (this.selectItemList != null) {
         v_sil = new Vector();

         for(i_count = 0; i_count < this.selectItemList.size(); ++i_count) {
            if (!(this.selectItemList.elementAt(i_count) instanceof SelectColumn)) {
               if (this.selectItemList.elementAt(i_count) instanceof WhereColumn) {
                  v_sil.addElement((WhereColumn)this.selectItemList.elementAt(i_count));
               } else {
                  v_sil.addElement((String)this.selectItemList.elementAt(i_count));
               }
            } else {
               SelectColumn sc = (SelectColumn)this.selectItemList.elementAt(i_count);
               sc.convertSelectColumnToTextDataTypeIfChildSelectHasStringLiterals(i_count, this.getIndexPositionsSetForStringLiterals(from_sqs));
               if (from_sqs != null) {
                  if (from_sqs.isSelectWithoutSetClause()) {
                     sc.convertSelectColumnToNumericDataType("INTEGER");
                  } else {
                     sc.trackIndexPositionsForCastingNULLString(i_count, from_sqs.isFirstSelectStatementInSetQuery(), this.getIndexPositionsSetForNULLString(from_sqs));
                  }
               }

               Vector v_ce = sc.getColumnExpression();

               for(int ii_count = 0; ii_count < v_ce.size(); ++ii_count) {
                  if (v_ce.elementAt(ii_count) instanceof String) {
                     String s_ce = (String)v_ce.elementAt(ii_count);
                     if (s_ce.equalsIgnoreCase("*") && v_ce.size() == 1 && this.selectItemList.size() > 1) {
                        FromClause fc = from_sqs.getFromClause();
                        Vector v_fil = fc.getFromItemList();
                        if (v_fil.size() <= 1) {
                           FromTable ft = (FromTable)v_fil.elementAt(0);
                           if (ft.getAliasName() == null) {
                              Object o_tn = ft.getTableName();
                              if (!(o_tn instanceof String)) {
                                 throw new ConvertException();
                              }
                           } else {
                              s_ce = ft.getAliasName() + ".*";
                           }

                           v_ce.setElementAt(s_ce, ii_count);
                        }
                     }
                  } else if (v_ce.elementAt(ii_count) instanceof TableColumn) {
                     TableColumn tc = (TableColumn)v_ce.elementAt(ii_count);
                     tc.setColumnName(tc.getColumnName());
                     tc.setTableName(tc.getTableName());
                  }
               }

               sc.setColumnExpression(v_ce);
               if (to_sqs != null) {
                  to_sqs.setCurrentIndexPosition(i_count);
               }

               v_sil.addElement(((SelectColumn)this.selectItemList.elementAt(i_count)).toMsAccessJdbcSelect(to_sqs, from_sqs));
            }
         }

         if (to_sqs != null) {
            to_sqs.setCurrentIndexPosition(-1);
         }

         t_ss.setSelectItemList(v_sil);
      }

      return t_ss;
   }

   public SelectStatement toSybaseSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SelectStatement t_ss = new SelectStatement();
      t_ss.setSelectClause(this.selectClause);
      if (this.selectQualifier != null) {
         GroupByStatement gbs_gb = from_sqs.getGroupByStatement();
         HavingStatement hs = from_sqs.getHavingStatement();
         if (this.selectQualifier.equalsIgnoreCase("DISTINCT ON")) {
            if (gbs_gb != null) {
               to_sqs.setGroupByStatement(gbs_gb.toSybaseSelect(to_sqs, from_sqs));
               this.addGroupByItems(to_sqs, from_sqs);
            } else {
               to_sqs.setGroupByStatement(this.createGroupByStatement(to_sqs, from_sqs));
            }

            if (hs != null) {
               to_sqs.setHavingStatement(hs.toSybaseSelect(to_sqs, from_sqs));
               this.addHavingItem(to_sqs);
            } else {
               to_sqs.setHavingStatement(this.createHavingStatement());
            }

            this.setSelectItemList(t_ss, from_sqs, to_sqs);
         } else if (this.selectQualifier.equalsIgnoreCase("UNIQUE")) {
            t_ss.setSelectQualifier("DISTINCT");
         } else {
            t_ss.setSelectQualifier(this.selectQualifier);
         }
      }

      if (this.selectRowSpecifier != null || this.ifxSelectRowSpecifier != null) {
         if (this.selectRowSpecifier != null && this.selectRowSpecifier.equalsIgnoreCase("FIRST")) {
            t_ss.setSelectRowSpecifier("TOP");
         } else if (this.ifxSelectRowSpecifier != null && this.ifxSelectRowSpecifier.equalsIgnoreCase("FIRST")) {
            t_ss.setSelectRowSpecifier("TOP");
            this.ifxSelectRowSpecifier = null;
         } else if (this.selectRowSpecifier != null && this.selectRowSpecifier.equalsIgnoreCase("TOP")) {
            t_ss.setSelectRowSpecifier((String)null);
            to_sqs.setSybaseTopRowCount(this.selectRowCount);
         } else {
            t_ss.setSelectRowSpecifier(this.selectRowSpecifier);
         }

         t_ss.setSelectRowCount(this.selectRowCount);
      }

      if (this.selectSpecialQualifier != null) {
         StringBuffer sb = new StringBuffer();
         sb.append("/* " + this.selectSpecialQualifier + "*/");
         t_ss.setSelectSpecialQualifier(sb.toString());
      }

      for(int i_count = 0; i_count < this.selectItemList.size(); ++i_count) {
         if (this.selectItemList.elementAt(i_count) instanceof SelectColumn) {
            ((SelectColumn)this.selectItemList.elementAt(i_count)).setObjectContext(this.context);
         }
      }

      if (this.selectQualifier != null && !this.selectQualifier.equalsIgnoreCase("DISTINCT ON") || this.selectQualifier == null) {
         Vector v_sil = new Vector();
         Vector aliasNames = new Vector();
         int aliasCount = 1;

         for(int i_count = 0; i_count < this.selectItemList.size(); ++i_count) {
            if (!(this.selectItemList.elementAt(i_count) instanceof SelectColumn)) {
               if (this.selectItemList.elementAt(i_count) instanceof WhereColumn) {
                  ((WhereColumn)this.selectItemList.elementAt(i_count)).setObjectContext(this.context);
                  v_sil.addElement((WhereColumn)this.selectItemList.elementAt(i_count));
               } else {
                  v_sil.addElement((String)this.selectItemList.elementAt(i_count));
               }
            } else {
               SelectColumn sc = (SelectColumn)this.selectItemList.elementAt(i_count);
               sc.setObjectContext(this.context);
               if (sc.getAliasName() != null) {
                  if (aliasNames != null && aliasNames.contains(sc.getAliasName())) {
                     sc.setAliasName(sc.getAliasName() + aliasCount);
                     ++aliasCount;
                  } else {
                     aliasNames.add(sc.getAliasName());
                  }
               }

               v_sil.addElement(((SelectColumn)this.selectItemList.elementAt(i_count)).toSybaseSelect(to_sqs, from_sqs));
            }
         }

         t_ss.setSelectItemList(v_sil);
      }

      t_ss.setPercentSpecifier(this.percentSpecifier);
      t_ss.setObjectContext(this.context);
      return t_ss;
   }

   public void setSelectItemList(SelectStatement ss, SelectQueryStatement from_sqs, SelectQueryStatement to_sqs) throws ConvertException {
      SelectStatement from_ss = from_sqs.getSelectStatement();
      Vector v_sil = from_ss.getSelectItemList();
      Vector v_new_sil = new Vector();

      for(int i_count = 0; i_count < v_sil.size(); ++i_count) {
         SelectColumn sc = new SelectColumn();
         Vector v_fi = new Vector();
         TableColumn tc = new TableColumn();
         FunctionCalls fc = new FunctionCalls();
         Vector vec_af = new Vector();
         tc.setColumnName("MAX");
         fc.setFunctionName(tc);
         if (v_sil.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn sc_vi = ((SelectColumn)v_sil.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs);
            sc_vi.setEndsWith((String)null);
            sc_vi.setAliasName((String)null);
            sc_vi.setIsAS((String)null);
            v_fi.addElement(sc_vi);
            if (sc_vi.getColumnExpression().elementAt(0) instanceof TableColumn) {
               sc.setAliasName(((TableColumn)sc_vi.getColumnExpression().elementAt(0)).getColumnName());
            }
         } else {
            v_fi.addElement((String)v_sil.elementAt(i_count));
         }

         fc.setFunctionArguments(v_fi);
         vec_af.addElement(fc);
         sc.setColumnExpression(vec_af);
         if (i_count != v_sil.size() - 1) {
            sc.setEndsWith(",");
         }

         v_new_sil.addElement(sc);
      }

      ss.setSelectItemList(v_new_sil);
   }

   public GroupByStatement createGroupByStatement(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      GroupByStatement gbs = new GroupByStatement();
      Vector v_gbil = new Vector();
      gbs.setGroupClause("GROUP BY");

      for(int i_count = 0; i_count < this.distinctList.size(); ++i_count) {
         v_gbil.addElement(((SelectColumn)this.distinctList.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
      }

      gbs.setGroupByItemList(v_gbil);
      return gbs;
   }

   public void addGroupByItems(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      GroupByStatement gbs = to_sqs.getGroupByStatement();
      Vector v_gbil = gbs.getGroupByItemList();
      Vector v_new_gbil = new Vector();

      int i_count;
      for(i_count = 0; i_count < v_gbil.size(); ++i_count) {
         v_new_gbil.addElement(v_gbil.elementAt(i_count));
      }

      for(i_count = 0; i_count < this.distinctList.size(); ++i_count) {
         v_new_gbil.addElement(((SelectColumn)this.distinctList.elementAt(i_count)).toMSSQLServerSelect(to_sqs, from_sqs));
      }

      gbs.setGroupByItemList(v_new_gbil);
   }

   public SelectStatement toOracleSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SelectStatement t_ss = new SelectStatement();
      t_ss.setSelectClause(this.selectClause);
      t_ss.setHintClause(this.hintClause);
      t_ss.setCommentClass(this.commentObj);
      WhereExpression f_we;
      if (this.selectQualifier != null) {
         f_we = from_sqs.getWhereExpression();
         new WhereExpression();
         if (this.selectQualifier.equalsIgnoreCase("DISTINCT ON")) {
            if (f_we != null) {
               to_sqs.setWhereExpression(f_we.toOracleSelect(to_sqs, from_sqs));
               to_sqs.getWhereExpression().addOperator("AND");
               to_sqs.getWhereExpression().addWhereExpression(this.createQuery(from_sqs.getFromClause(), to_sqs, from_sqs));
            } else {
               to_sqs.setWhereExpression(this.createQuery(from_sqs.getFromClause(), to_sqs, from_sqs));
            }
         } else {
            t_ss.setSelectQualifier(this.selectQualifier);
         }
      }

      WhereColumn col1;
      if ((this.selectRowSpecifier != null || this.ifxSelectRowSpecifier != null) && (this.selectRowSpecifier != null && this.selectRowSpecifier.equalsIgnoreCase("TOP") || this.selectRowSpecifier != null && this.selectRowSpecifier.equalsIgnoreCase("FIRST") || this.ifxSelectRowSpecifier != null && this.ifxSelectRowSpecifier.equalsIgnoreCase("FIRST"))) {
         f_we = from_sqs.getWhereExpression();
         WhereItem wi = new WhereItem();
         Vector v_temp = new Vector();
         col1 = new WhereColumn();
         v_temp.addElement("ROWNUM");
         col1.setColumnExpression(v_temp);
         wi.setLeftWhereExp(col1);
         wi.setOperator("<");
         v_temp = new Vector();
         col1 = new WhereColumn();
         if (this.percentSpecifier == null) {
            if (this.rowcountVariable != null) {
               v_temp.addElement(this.rowcountVariable + " + 1");
            } else {
               v_temp.addElement(Integer.toString(this.selectRowCount + 1));
            }
         } else {
            String s_sqs;
            if (this.rowcountVariable != null && this.selectRowCount == 0) {
               s_sqs = "select count(*)*(" + this.rowcountVariable + "/100) + 1 " + from_sqs.getFromClause().toString();
            } else {
               s_sqs = "select count(*)*(" + this.selectRowCount + "/100) + 1 " + from_sqs.getFromClause().toString();
            }

            SwisSQLAPI swissqlapi = new SwisSQLAPI(s_sqs);

            try {
               s_sqs = swissqlapi.convert(1);
               s_sqs = "(" + s_sqs + ")";
            } catch (ParseException var18) {
               throw new ConvertException(" Could not parse the converted from clause " + s_sqs);
            } catch (ConvertException var19) {
               throw var19;
            }

            v_temp.addElement(s_sqs);
         }

         col1.setColumnExpression(v_temp);
         wi.setRightWhereExp(col1);
         if (f_we != null && f_we.getCheckWhere()) {
            to_sqs.getWhereExpression().addOperator("AND");
            to_sqs.getWhereExpression().addWhereItem(wi);
         } else if (f_we != null) {
            to_sqs.setWhereExpression(f_we.toOracleSelect(to_sqs, from_sqs));
            to_sqs.getWhereExpression().addOperator("AND");
            to_sqs.getWhereExpression().addWhereItem(wi);
         } else {
            WhereExpression we = new WhereExpression();
            we.addWhereItem(wi);
            if (to_sqs != null && to_sqs.getWhereExpression() != null) {
               to_sqs.getWhereExpression().addOperator("AND");
               to_sqs.getWhereExpression().addWhereExpression(we);
            } else {
               to_sqs.setWhereExpression(we);
            }
         }
      }

      if (this.selectSpecialQualifier != null) {
         StringBuffer sb = new StringBuffer();
         sb.append("/* +" + this.selectSpecialQualifier + "*/");
         t_ss.setSelectSpecialQualifier(sb.toString());
      }

      Vector v_sil = new Vector();

      for(int i_count = 0; i_count < this.selectItemList.size(); ++i_count) {
         if (!(this.selectItemList.elementAt(i_count) instanceof SelectColumn)) {
            if (this.selectItemList.elementAt(i_count) instanceof WhereColumn) {
               v_sil.addElement((WhereColumn)this.selectItemList.elementAt(i_count));
            } else if (this.selectItemList.elementAt(i_count) instanceof SelectQueryStatement) {
               SelectQueryStatement ss = (SelectQueryStatement)this.selectItemList.elementAt(i_count);
               if (ss.getWhereExpression() == null) {
                  WhereExpression we = new WhereExpression();
                  ss.setWhereExpression(we);
               } else {
                  ss.getWhereExpression().addOperator("AND");
               }

               col1 = new WhereColumn();
               Vector col1List = new Vector();
               col1List.add("ROWNUM");
               col1.setColumnExpression(col1List);
               WhereColumn col2 = new WhereColumn();
               Vector col2List = new Vector();
               col2List.add("1");
               col2.setColumnExpression(col2List);
               WhereItem it = new WhereItem();
               it.setLeftWhereExp(col1);
               it.setRightWhereExp(col2);
               it.setOperator("=");
               ss.getWhereExpression().addWhereItem(it);
               v_sil.addElement(ss);
            } else {
               v_sil.addElement((String)this.selectItemList.elementAt(i_count));
            }
         } else {
            SelectColumn originalsc = (SelectColumn)this.selectItemList.elementAt(i_count);
            SelectColumn oracleSelectColumn = ((SelectColumn)this.selectItemList.elementAt(i_count)).toOracleSelect(to_sqs, from_sqs);
            Vector v_ce = oracleSelectColumn.getColumnExpression();

            for(int ii_count = 0; ii_count < v_ce.size(); ++ii_count) {
               if (v_ce.elementAt(ii_count) instanceof String) {
                  String s_ce = (String)v_ce.elementAt(ii_count);
                  if (s_ce.equalsIgnoreCase("*") && v_ce.size() == 1 && this.selectItemList.size() > 1) {
                     FromClause fc = from_sqs.getFromClause();
                     Vector v_fil = fc.getFromItemList();
                     if (v_fil.size() > 1) {
                        for(int countNum = 0; countNum < v_fil.size(); ++countNum) {
                           if (v_fil.elementAt(countNum) instanceof FromTable) {
                              FromTable ft = (FromTable)v_fil.elementAt(countNum);
                              if (ft.getAliasName() == null) {
                                 Object o_tn = ft.getTableName();
                                 if (!(o_tn instanceof String)) {
                                    throw new ConvertException();
                                 }

                                 String tableName = (String)o_tn;
                                 if (tableName.toLowerCase().startsWith("dbo.")) {
                                    tableName = tableName.substring(4);
                                 } else if (tableName.toLowerCase().startsWith("[dbo].")) {
                                    tableName = tableName.substring(6);
                                 }

                                 s_ce = tableName + ".*";
                              } else {
                                 s_ce = ft.getAliasName() + ".*";
                              }
                           } else if (v_fil.elementAt(countNum) instanceof FromClause) {
                              Vector newFromItemList = ((FromClause)v_fil.get(countNum)).getFromItemList();
                              this.changeTheSelectColumnWithStarToTableNameStar(v_ce, newFromItemList, ii_count);
                           }

                           if (countNum == 0) {
                              v_ce.setElementAt(s_ce, ii_count);
                           } else {
                              v_ce.add(",");
                              v_ce.add(s_ce);
                           }
                        }
                     } else if (v_fil.elementAt(0) instanceof FromTable) {
                        FromTable ft = (FromTable)v_fil.elementAt(0);
                        if (ft.getAliasName() == null) {
                           Object o_tn = ft.getTableName();
                           if (!(o_tn instanceof String)) {
                              throw new ConvertException();
                           }

                           String tableName = (String)o_tn;
                           if (tableName.toLowerCase().startsWith("dbo.")) {
                              tableName = tableName.substring(4);
                           } else if (tableName.toLowerCase().startsWith("[dbo].")) {
                              tableName = tableName.substring(6);
                           }

                           s_ce = tableName + ".*";
                        } else {
                           s_ce = ft.getAliasName() + ".*";
                        }

                        v_ce.setElementAt(s_ce, ii_count);
                     } else if (v_fil.elementAt(0) instanceof FromClause) {
                        Vector newFCVector = ((FromClause)v_fil.get(0)).getFromItemList();
                        this.changeTheSelectColumnWithStarToTableNameStar(v_ce, newFCVector, ii_count);
                     }
                  }
               }
            }

            oracleSelectColumn.setColumnExpression(v_ce);
            v_sil.addElement(oracleSelectColumn);
         }
      }

      t_ss.setSelectItemList(v_sil);
      t_ss.setObjectContext(this.context);
      return t_ss;
   }

   private void changeTheSelectColumnWithStarToTableNameStar(Vector v_ce, Vector v_fil, int num) throws ConvertException {
      for(int ii_count = 0; ii_count < v_ce.size(); ++ii_count) {
         if (v_ce.elementAt(ii_count) instanceof String) {
            String s_ce = (String)v_ce.elementAt(ii_count);
            if (s_ce.equalsIgnoreCase("*") && v_ce.size() == 1 && this.selectItemList.size() > 1) {
               if (v_fil.size() > 1) {
                  for(int countNum = 0; countNum < v_fil.size(); ++countNum) {
                     if (v_fil.elementAt(countNum) instanceof FromTable) {
                        FromTable ft = (FromTable)v_fil.elementAt(countNum);
                        if (ft.getAliasName() == null) {
                           Object o_tn = ft.getTableName();
                           if (!(o_tn instanceof String)) {
                              throw new ConvertException();
                           }

                           s_ce = (String)o_tn + ".*";
                        } else {
                           s_ce = ft.getAliasName() + ".*";
                        }
                     } else if (v_fil.elementAt(countNum) instanceof FromClause) {
                        Vector newFCVector = ((FromClause)v_fil.get(countNum)).getFromItemList();
                        this.changeTheSelectColumnWithStarToTableNameStar(v_ce, newFCVector, num);
                     }

                     if (countNum == 0) {
                        v_ce.setElementAt(s_ce, num);
                     } else {
                        if (countNum > 0) {
                           v_ce.add(",");
                        }

                        v_ce.add(s_ce);
                     }
                  }
               } else if (v_fil.elementAt(0) instanceof FromTable) {
                  FromTable ft = (FromTable)v_fil.elementAt(0);
                  if (ft.getAliasName() == null) {
                     Object o_tn = ft.getTableName();
                     if (!(o_tn instanceof String)) {
                        throw new ConvertException();
                     }

                     s_ce = (String)o_tn + ".*";
                  } else {
                     s_ce = ft.getAliasName() + ".*";
                  }

                  v_ce.setElementAt(s_ce, ii_count);
               } else if (v_fil.elementAt(0) instanceof FromClause) {
                  Vector newFCVector = ((FromClause)v_fil.get(0)).getFromItemList();
                  this.changeTheSelectColumnWithStarToTableNameStar(v_ce, newFCVector, ii_count);
               }
            }
         }
      }

   }

   public SelectStatement toInformixSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SelectStatement t_ss = new SelectStatement();
      t_ss.setCommentClass(this.commentObj);
      t_ss.setSelectClause(this.selectClause);
      if (this.selectQualifier != null) {
         GroupByStatement gbs_gb = from_sqs.getGroupByStatement();
         HavingStatement hs = from_sqs.getHavingStatement();
         if (this.selectQualifier.equalsIgnoreCase("DISTINCT ON")) {
            if (gbs_gb != null) {
               to_sqs.setGroupByStatement(gbs_gb.toInformixSelect(to_sqs, from_sqs));
               this.addGroupByItems(to_sqs, from_sqs);
            } else {
               to_sqs.setGroupByStatement(this.createGroupByStatement(to_sqs, from_sqs));
            }

            if (hs != null) {
               to_sqs.setHavingStatement(hs.toMySQLSelect(to_sqs, from_sqs));
               this.addHavingItem(to_sqs);
            } else {
               to_sqs.setHavingStatement(this.createHavingStatement());
            }

            this.setSelectItemList(t_ss, from_sqs, to_sqs);
         } else {
            t_ss.setSelectQualifier(this.selectQualifier);
         }
      }

      if (this.selectRowSpecifier != null) {
         if (this.selectRowSpecifier.equalsIgnoreCase("TOP")) {
            t_ss.setInformixRowSpecifier("FIRST");
            t_ss.setSelectRowSpecifier((String)null);
         } else {
            t_ss.setSelectRowSpecifier(this.selectRowSpecifier);
         }

         t_ss.setSelectRowCount(this.selectRowCount);
      }

      if (this.selectSpecialQualifier != null) {
         StringBuffer sb = new StringBuffer();
         sb.append("/* " + this.selectSpecialQualifier + "*/");
         t_ss.setSelectSpecialQualifier(sb.toString());
      }

      if (this.selectQualifier != null && !this.selectQualifier.equalsIgnoreCase("DISTINCT ON") || this.selectQualifier == null) {
         Vector v_sil = new Vector();

         for(int i_count = 0; i_count < this.selectItemList.size(); ++i_count) {
            if (this.selectItemList.elementAt(i_count) instanceof SelectColumn) {
               v_sil.addElement(((SelectColumn)this.selectItemList.elementAt(i_count)).toInformixSelect(to_sqs, from_sqs));
            } else if (this.selectItemList.elementAt(i_count) instanceof WhereColumn) {
               v_sil.addElement((WhereColumn)this.selectItemList.elementAt(i_count));
            } else {
               v_sil.addElement((String)this.selectItemList.elementAt(i_count));
            }
         }

         t_ss.setSelectItemList(v_sil);
      }

      if (this.selectRowSpecifier != null && this.selectRowSpecifier.equalsIgnoreCase("FIRST")) {
         t_ss.setSelectRowSpecifier((String)null);
         t_ss.setInformixRowSpecifier("FIRST");
      }

      return t_ss;
   }

   public SelectStatement toTimesTenSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SelectStatement t_ss = new SelectStatement();
      t_ss.setSelectClause(this.selectClause);
      t_ss.setOpenBraceForSelectInInsertQuery(this.openBraceForSelectInInsert);
      if (this.selectQualifier != null) {
         WhereExpression f_we = from_sqs.getWhereExpression();
         new WhereExpression();
         if (this.selectQualifier.equalsIgnoreCase("DISTINCT ON")) {
            if (f_we != null) {
               to_sqs.setWhereExpression(f_we.toTimesTenSelect(to_sqs, from_sqs));
               to_sqs.getWhereExpression().addOperator("AND");
               to_sqs.getWhereExpression().addWhereExpression(this.createQuery(from_sqs.getFromClause(), to_sqs, from_sqs));
            } else {
               to_sqs.setWhereExpression(this.createQuery(from_sqs.getFromClause(), to_sqs, from_sqs));
            }
         } else {
            t_ss.setSelectQualifier(this.selectQualifier);
         }
      }

      if (this.selectRowSpecifier != null || this.ifxSelectRowSpecifier != null) {
         if (this.selectRowSpecifier != null && this.selectRowSpecifier.equalsIgnoreCase("TOP")) {
            t_ss.setSelectRowSpecifier("FIRST");
         } else {
            t_ss.setSelectRowSpecifier(this.selectRowSpecifier);
         }

         t_ss.setSelectRowCount(this.selectRowCount);
      }

      if (this.selectSpecialQualifier != null) {
      }

      Vector v_sil = new Vector();

      for(int i_count = 0; i_count < this.selectItemList.size(); ++i_count) {
         if (!(this.selectItemList.elementAt(i_count) instanceof SelectColumn)) {
            if (this.selectItemList.elementAt(i_count) instanceof WhereColumn) {
               v_sil.addElement((WhereColumn)this.selectItemList.elementAt(i_count));
            } else if (this.selectItemList.elementAt(i_count) instanceof SelectQueryStatement) {
               SelectQueryStatement var19 = (SelectQueryStatement)this.selectItemList.elementAt(i_count);
            } else {
               v_sil.addElement((String)this.selectItemList.elementAt(i_count));
            }
         } else {
            SelectColumn originalsc = (SelectColumn)this.selectItemList.elementAt(i_count);
            SelectColumn timesTenSelectColumn = ((SelectColumn)this.selectItemList.elementAt(i_count)).toTimesTenSelect(to_sqs, from_sqs);
            Vector v_ce = timesTenSelectColumn.getColumnExpression();

            for(int ii_count = 0; ii_count < v_ce.size(); ++ii_count) {
               if (v_ce.elementAt(ii_count) instanceof String) {
                  String s_ce = (String)v_ce.elementAt(ii_count);
                  if (s_ce.equalsIgnoreCase("*") && v_ce.size() == 1 && this.selectItemList.size() > 1) {
                     FromClause fc = from_sqs.getFromClause();
                     Vector v_fil = fc.getFromItemList();
                     if (v_fil.size() > 1) {
                        for(int countNum = 0; countNum < v_fil.size(); ++countNum) {
                           if (v_fil.elementAt(countNum) instanceof FromTable) {
                              FromTable ft = (FromTable)v_fil.elementAt(countNum);
                              if (ft.getAliasName() == null) {
                                 Object o_tn = ft.getTableName();
                                 if (!(o_tn instanceof String)) {
                                    throw new ConvertException();
                                 }

                                 String tableName = (String)o_tn;
                                 if (tableName.toLowerCase().startsWith("dbo.")) {
                                    tableName = tableName.substring(4);
                                 } else if (tableName.toLowerCase().startsWith("[dbo].")) {
                                    tableName = tableName.substring(6);
                                 }

                                 s_ce = tableName + ".*";
                              } else {
                                 s_ce = ft.getAliasName() + ".*";
                              }
                           } else if (v_fil.elementAt(countNum) instanceof FromClause) {
                              Vector newFromItemList = ((FromClause)v_fil.get(countNum)).getFromItemList();
                              this.changeTheSelectColumnWithStarToTableNameStar(v_ce, newFromItemList, ii_count);
                           }

                           if (countNum == 0) {
                              v_ce.setElementAt(s_ce, ii_count);
                           } else {
                              v_ce.add(",");
                              v_ce.add(s_ce);
                           }
                        }
                     } else if (v_fil.elementAt(0) instanceof FromTable) {
                        FromTable ft = (FromTable)v_fil.elementAt(0);
                        if (ft.getAliasName() == null) {
                           Object o_tn = ft.getTableName();
                           if (!(o_tn instanceof String)) {
                              throw new ConvertException();
                           }

                           String tableName = (String)o_tn;
                           if (tableName.toLowerCase().startsWith("dbo.")) {
                              tableName = tableName.substring(4);
                           } else if (tableName.toLowerCase().startsWith("[dbo].")) {
                              tableName = tableName.substring(6);
                           }

                           s_ce = tableName + ".*";
                        } else {
                           s_ce = ft.getAliasName() + ".*";
                        }

                        v_ce.setElementAt(s_ce, ii_count);
                     } else if (v_fil.elementAt(0) instanceof FromClause) {
                        Vector newFCVector = ((FromClause)v_fil.get(0)).getFromItemList();
                        this.changeTheSelectColumnWithStarToTableNameStar(v_ce, newFCVector, ii_count);
                     }
                  }
               }
            }

            timesTenSelectColumn.setColumnExpression(v_ce);
            v_sil.addElement(timesTenSelectColumn);
         }
      }

      t_ss.setSelectItemList(v_sil);
      t_ss.setObjectContext(this.context);
      return t_ss;
   }

   public SelectStatement toNetezzaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SelectStatement t_ss = new SelectStatement();
      t_ss.setSelectClause(this.selectClause);
      t_ss.setHintClause(this.hintClause);
      if (this.selectQualifier != null) {
         GroupByStatement gbs_gb = from_sqs.getGroupByStatement();
         HavingStatement hs = from_sqs.getHavingStatement();
         if (this.selectQualifier.equalsIgnoreCase("DISTINCT ON")) {
            if (gbs_gb != null) {
               to_sqs.setGroupByStatement(gbs_gb.toNetezzaSelect(to_sqs, from_sqs));
               this.addGroupByItems(to_sqs, from_sqs);
            } else {
               to_sqs.setGroupByStatement(this.createGroupByStatement(to_sqs, from_sqs));
            }

            if (hs != null) {
               to_sqs.setHavingStatement(hs.toNetezzaSelect(to_sqs, from_sqs));
               this.addHavingItem(to_sqs);
            } else {
               to_sqs.setHavingStatement(this.createHavingStatement());
            }

            this.setSelectItemList(t_ss, from_sqs, to_sqs);
         } else if (this.selectQualifier.equalsIgnoreCase("UNIQUE")) {
            t_ss.setSelectQualifier("DISTINCT");
         } else {
            t_ss.setSelectQualifier(this.selectQualifier);
         }
      }

      if (this.selectRowSpecifier != null) {
         if (this.percentSpecifier != null) {
            throw new ConvertException();
         }

         LimitClause lc = new LimitClause();
         if (from_sqs.getLimitClause() != null) {
            throw new ConvertException();
         }

         lc.setLimitClause("LIMIT");
         lc.setLimitValue("" + this.selectRowCount);
         to_sqs.setLimitClause(lc);
      }

      if (this.selectSpecialQualifier != null) {
         StringBuffer sb = new StringBuffer();
         sb.append("/* " + this.selectSpecialQualifier + "*/");
         t_ss.setSelectSpecialQualifier(sb.toString());
      }

      if (this.selectQualifier != null && !this.selectQualifier.equalsIgnoreCase("DISTINCT ON") || this.selectQualifier == null) {
         Vector v_sil = new Vector();

         for(int i_count = 0; i_count < this.selectItemList.size(); ++i_count) {
            if (this.selectItemList.elementAt(i_count) instanceof SelectColumn) {
               v_sil.addElement(((SelectColumn)this.selectItemList.elementAt(i_count)).toNetezzaSelect(to_sqs, from_sqs));
            } else if (this.selectItemList.elementAt(i_count) instanceof WhereColumn) {
               v_sil.addElement((WhereColumn)this.selectItemList.elementAt(i_count));
            } else {
               v_sil.addElement((String)this.selectItemList.elementAt(i_count));
            }
         }

         t_ss.setSelectItemList(v_sil);
      }

      return t_ss;
   }

   WhereExpression createQuery(FromClause t_fc, SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      WhereItem wi = new WhereItem();
      WhereColumn wc = new WhereColumn();
      Vector v_sc = new Vector();
      SelectQueryStatement sqs_i = new SelectQueryStatement();
      SelectStatement ss_i = new SelectStatement();
      FunctionCalls fc = new FunctionCalls();
      FromClause fcl = new FromClause();
      GroupByStatement gbs = new GroupByStatement();
      TableColumn tc = new TableColumn();
      WhereExpression we = new WhereExpression();
      v_sc.addElement("ROWID");
      wc.setColumnExpression(v_sc);
      wi.setLeftWhereExp(wc);
      wi.setOperator("IN");
      tc.setColumnName("MIN");
      fc.setFunctionName(tc);
      SelectColumn sc = new SelectColumn();
      v_sc = new Vector();
      v_sc.addElement("ROWID");
      sc.setColumnExpression(v_sc);
      v_sc = new Vector();
      v_sc.addElement(sc);
      fc.setFunctionArguments(v_sc);
      sc = new SelectColumn();
      v_sc = new Vector();
      v_sc.addElement(fc);
      sc.setColumnExpression(v_sc);
      v_sc = new Vector();
      v_sc.addElement(sc);
      ss_i.setSelectClause("select");
      ss_i.setSelectItemList(v_sc);
      fcl.setFromClause(t_fc.getFromClause());
      Vector v_fil = t_fc.getFromItemList();
      Vector v_nfil = new Vector();

      for(int i_count = 0; i_count < v_fil.size(); ++i_count) {
         FromTable fc_n = (FromTable)v_fil.elementAt(i_count);
         fc_n.setIsAS(false);
         v_nfil.addElement(fc_n);
      }

      fcl.setFromItemList(v_nfil);
      gbs.setGroupClause("group by");
      gbs.setGroupByItemList(this.distinctList);
      sqs_i.setSelectStatement(ss_i);
      sqs_i.setFromClause(fcl);
      sqs_i.setGroupByStatement(gbs);
      wi.setRightWhereSubQuery(sqs_i.toOracle());
      we.addWhereItem(wi);
      return we;
   }

   public static String changeBackTip(String obj) {
      if (obj == null) {
         return obj;
      } else {
         String obj1 = obj.replace('`', '"');
         return obj1;
      }
   }

   public static String checkandRemoveDoubleQuoteForPostgresIdentifier(String obj) {
      if (obj != null) {
         obj = obj.toLowerCase();
      }

      return obj;
   }

   public static String checkandRemoveDoubleQuoteForPostgresIdentifier(String obj, boolean noNeedToLower) {
      if (obj != null) {
         if (noNeedToLower) {
            if (obj.startsWith("\"T_") || obj.startsWith("\"C_") || obj.startsWith("\"TC_") || obj.startsWith("\"AQ_") || obj.startsWith("\"MDF_") || obj.startsWith("\"WQ_") || obj.startsWith("\"COL_") || obj.startsWith("\"MCOL_") || obj.startsWith("\"MDFCOL_") || obj.startsWith("\"JOINCOL_") || obj.startsWith("\"BQ") || obj.startsWith("\"VUDQ") || obj.startsWith("\"MDFQuery") || obj.startsWith("\"ZA_NULL_JOINCOL_BLEND") || obj.startsWith("\"T_DC_FILTER") || obj.startsWith("\"NULL_COL")) {
               obj = obj.toLowerCase();
            }
         } else {
            obj = obj.toLowerCase();
         }
      }

      return obj;
   }

   public SelectStatement toVectorWiseSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SelectStatement t_ss = new SelectStatement();
      t_ss.setSelectClause(this.selectClause);
      if (this.selectQualifier != null) {
         GroupByStatement gbs_gb = from_sqs.getGroupByStatement();
         HavingStatement hs = from_sqs.getHavingStatement();
         if (this.selectQualifier.equalsIgnoreCase("DISTINCT ON")) {
            if (gbs_gb != null) {
               to_sqs.setGroupByStatement(gbs_gb.toVectorWiseSelect(to_sqs, from_sqs));
               this.addGroupByItems(to_sqs, from_sqs);
            } else {
               to_sqs.setGroupByStatement(this.createGroupByStatement(to_sqs, from_sqs));
            }

            if (hs != null) {
               to_sqs.setHavingStatement(hs.toVectorWiseSelect(to_sqs, from_sqs));
               this.addHavingItem(to_sqs);
            } else {
               to_sqs.setHavingStatement(this.createHavingStatement());
            }

            this.setSelectItemList(t_ss, from_sqs, to_sqs);
         } else if (this.selectQualifier.equalsIgnoreCase("UNIQUE")) {
            t_ss.setSelectQualifier("DISTINCT");
         } else {
            t_ss.setSelectQualifier(this.selectQualifier);
         }
      }

      if (this.selectRowSpecifier != null) {
         if (this.percentSpecifier != null) {
            throw new ConvertException();
         }

         LimitClause lc = new LimitClause();
         if (from_sqs.getLimitClause() != null) {
            throw new ConvertException();
         }

         lc.setLimitClause("LIMIT");
         lc.setLimitValue("" + this.selectRowCount);
         to_sqs.setLimitClause(lc);
      }

      if (this.selectSpecialQualifier != null) {
         t_ss.setSelectSpecialQualifier(this.selectSpecialQualifier);
      }

      if (this.selectQualifier != null && !this.selectQualifier.equalsIgnoreCase("DISTINCT ON") || this.selectQualifier == null) {
         Vector v_sil = new Vector();

         for(int i_count = 0; i_count < this.selectItemList.size(); ++i_count) {
            if (!(this.selectItemList.elementAt(i_count) instanceof SelectColumn)) {
               v_sil.addElement((String)this.selectItemList.elementAt(i_count));
            } else {
               SelectColumn sc = (SelectColumn)this.selectItemList.elementAt(i_count);
               sc.convertSelectColumnToTextDataTypeIfChildSelectHasStringLiterals(i_count, this.getIndexPositionsSetForStringLiterals(from_sqs));
               if (from_sqs != null) {
                  if (from_sqs.isSelectWithoutSetClause()) {
                     sc.convertSelectColumnToNumericDataType("SIGNED");
                  } else {
                     sc.trackIndexPositionsForCastingNULLString(i_count, from_sqs.isFirstSelectStatementInSetQuery(), this.getIndexPositionsSetForNULLString(from_sqs));
                  }
               }

               Vector v_ce = sc.getColumnExpression();

               for(int ii_count = 0; ii_count < v_ce.size(); ++ii_count) {
                  if (v_ce.elementAt(ii_count) instanceof String) {
                     String s_ce = (String)v_ce.elementAt(ii_count);
                     if (s_ce.equalsIgnoreCase("*") && v_ce.size() == 1 && this.selectItemList.size() > 1) {
                        FromClause fc = from_sqs.getFromClause();
                        Vector v_fil = fc.getFromItemList();
                        if (v_fil.size() <= 1) {
                           FromTable ft = (FromTable)v_fil.elementAt(0);
                           if (ft.getAliasName() == null) {
                              Object o_tn = ft.getTableName();
                              if (!(o_tn instanceof String)) {
                                 throw new ConvertException();
                              }
                           } else {
                              s_ce = ft.getAliasName() + ".*";
                           }

                           v_ce.setElementAt(changeBackTip(s_ce), ii_count);
                        }
                     }
                  } else if (v_ce.elementAt(ii_count) instanceof TableColumn) {
                     TableColumn tc = (TableColumn)v_ce.elementAt(ii_count);
                     tc.setColumnName(changeBackTip(tc.getColumnName()));
                  }
               }

               sc.setColumnExpression(v_ce);
               v_sil.addElement(((SelectColumn)this.selectItemList.elementAt(i_count)).toVectorWiseSelect(to_sqs, from_sqs));
            }
         }

         t_ss.setSelectItemList(v_sil);
      }

      return t_ss;
   }

   public SelectStatement toReplaceTblCol(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      SelectStatement selStmtConv = new SelectStatement();
      selStmtConv.setSelectClause(this.selectClause);
      if (this.commentObj != null) {
         selStmtConv.setCommentClass(this.commentObj);
      }

      if (this.selectQualifier != null) {
         selStmtConv.setSelectQualifier(this.selectQualifier);
      }

      if (this.distinctList != null) {
         selStmtConv.setDistinctList(this.distinctList);
      }

      if (this.selectRowSpecifier != null) {
         selStmtConv.setSelectRowSpecifier(this.selectRowSpecifier);
      }

      if (this.percentSpecifier != null) {
         selStmtConv.setPercentSpecifier(this.percentSpecifier);
      }

      selStmtConv.setSelectRowCount(this.selectRowCount);
      if (this.rowcountVariable != null) {
         selStmtConv.setSelectRowCountVariable(this.rowcountVariable);
      }

      if (this.selectSpecialQualifier != null) {
         selStmtConv.setSelectSpecialQualifier(this.selectSpecialQualifier);
      }

      if (this.openBraceForRowCount != null) {
         selStmtConv.setOpenBraceForRowCount(this.openBraceForRowCount);
      }

      if (this.closedBraceForRowCount != null) {
         selStmtConv.setClosedBraceForRowCount(this.closedBraceForRowCount);
      }

      if (this.hintClause != null) {
         selStmtConv.setHintClause(this.hintClause);
      }

      Vector vcConv = new Vector();
      HashSet duplicateColList = new HashSet();
      HashSet duplicateColsInStar = new HashSet();
      ArrayList uniqueColListOfStar = new ArrayList();
      ArrayList uniqueColList = new ArrayList();
      HashSet expandedList = new HashSet();
      ArrayList cteColumnList = from_sqs.getCTEColumnList();

      for(int i_count = 0; i_count < this.selectItemList.size(); ++i_count) {
         if (this.selectItemList.elementAt(i_count) instanceof SelectColumn) {
            SelectColumn sc = (SelectColumn)this.selectItemList.elementAt(i_count);
            String aliasName = sc.getAliasName();
            if (sc.getColumnExpression().get(0) instanceof SelectQueryStatement) {
               ((SelectQueryStatement)sc.getColumnExpression().get(0)).setPropAndHandlerFromSQS(from_sqs);
            }

            boolean isStarClause = sc.hasStarInSelectColumn();
            if (from_sqs.getQueryConvDataHandler().isReverseParseConv() && !from_sqs.isSubQuerySource() && !from_sqs.isCTESupported() && aliasName != null && aliasName.equals("`__ZDBID`")) {
               this.selectItemList.removeElementAt(i_count);
               ((SelectColumn)vcConv.elementAt(vcConv.size() - 1)).setEndsWith((String)null);
            } else {
               String orginalAliasName;
               HashMap colDetsMap;
               if (isStarClause) {
                  if (sc.getColumnExpression().size() > 1) {
                     orginalAliasName = GeneralUtil.trimIfAliasNameIsEnclosed((String)sc.getColumnExpression().get(0));
                     colDetsMap = (HashMap)CastingUtil.getValueIgnoreCase(from_sqs.getTableColumnDetsMap(), orginalAliasName);
                     if (colDetsMap != null) {
                        this.checkAndAddDuplicateCols(to_sqs, colDetsMap, uniqueColList, uniqueColListOfStar, duplicateColsInStar, expandedList, orginalAliasName);
                     } else {
                        if (!from_sqs.getCanAllowExceptionStacking()) {
                           throw new ConvertException("Unknown column present in select query", "UNKNOWN_TABLE_OR_ALIAS_USED", new Object[]{orginalAliasName});
                        }

                        from_sqs.getQueryConvDataHandler().addExceptionMap("UNKNOWN_TABLE_OR_ALIAS_USED", new Object[]{orginalAliasName});
                     }
                  } else {
                     Iterator var32 = from_sqs.getTableColumnDetsMap().keySet().iterator();

                     while(var32.hasNext()) {
                        Object tbls = var32.next();
                        HashMap<String, String> tableCols = (HashMap)from_sqs.getTableColumnDetsMap().get(tbls);
                        this.checkAndAddDuplicateCols(to_sqs, tableCols, uniqueColList, uniqueColListOfStar, duplicateColsInStar, expandedList, tbls.toString());
                     }
                  }
               }

               char quote;
               if (!from_sqs.isSubQuerySource() && !from_sqs.isCTESupported()) {
                  if (this.aliasNameVersion != 0 && aliasName != null && from_sqs.getQueryConvDataHandler().isReverseParseConv() && !from_sqs.getQueryConvDataHandler().isUserQuery()) {
                     orginalAliasName = from_sqs.getQueryConvDataHandler().getProcessingTableName();
                     colDetsMap = (HashMap)CastingUtil.getValueIgnoreCase(from_sqs.getQueryConvDataHandler().getTableColumnDetsMap(), orginalAliasName);
                     String[] colDets = GeneralUtil.getReplacedTblColDets(colDetsMap, aliasName);
                     aliasName = colDets[2];
                     sc.setAliasName(GeneralUtil.trimIfAliasNameIsEnclosed(aliasName).trim());
                  } else if (sc.getAliasName() == null) {
                     if (!isStarClause) {
                        if (this.aliasNameVersion == 1) {
                           orginalAliasName = getProperAliasName(sc.toString()).trim();
                           orginalAliasName = "`" + orginalAliasName + "`";
                           sc.setAliasForExpression(orginalAliasName);
                        } else if (this.aliasNameVersion == 2) {
                           try {
                              SwisSQLAPI.setProperSelExpTL();
                              orginalAliasName = sc.toString().trim();
                              orginalAliasName = "`" + orginalAliasName + "`";
                              sc.setAliasForExpression(orginalAliasName);
                           } catch (Exception var28) {
                              throw new ConvertException("Provide Alias Name");
                           } finally {
                              SwisSQLAPI.clearProperSelExpTL();
                           }
                        }
                     }
                  } else if (sc.getAliasName() != null) {
                     orginalAliasName = aliasName;
                     quote = '1';
                     if (GeneralUtil.isItEnclosedAliasName(aliasName)) {
                        quote = aliasName.charAt(0);
                        orginalAliasName = GeneralUtil.trimStartAndEndChars(aliasName).trim();
                     }

                     if (orginalAliasName.isEmpty()) {
                        throw new ConvertException("Alias Name cannot be empty", "SQL_ALIASNAME_EMPTY_IN_QT");
                     }

                     if (sc.getIsAS() == null) {
                        sc.setIsAS("as");
                     }

                     from_sqs.getAliasColumns().add(orginalAliasName);
                     orginalAliasName = GeneralUtil.encloseColOrTabWithProperQuote(orginalAliasName, quote);
                     sc.setAliasName(orginalAliasName);
                     sc.setAliasForExpression(orginalAliasName);
                  }
               } else {
                  if (from_sqs.isCTESupported() && cteColumnList.size() > 0 && cteColumnList.size() != this.selectItemList.size()) {
                     throw new ConvertException("INVALID NUMBER OF ARGUMENTS FOR WITH CLAUSE", "CTE_ALIAS_COLUMNS_NOT_EQUAL");
                  }

                  if (aliasName == null) {
                     if (!isStarClause) {
                        if (this.aliasNameVersion == 1) {
                           orginalAliasName = getProperAliasName(sc.toString()).trim();
                           if (cteColumnList != null && cteColumnList.size() > 0) {
                              orginalAliasName = (String)cteColumnList.get(i_count);
                              to_sqs.addSelColNameMap(orginalAliasName, orginalAliasName);
                              sc.setAliasForExpression(orginalAliasName);
                           } else {
                              to_sqs.addSelColNameMap(orginalAliasName, orginalAliasName);
                              sc.setAliasForExpression(orginalAliasName);
                           }
                        } else if (this.aliasNameVersion == 2) {
                           try {
                              SwisSQLAPI.setProperSelExpTL();
                              orginalAliasName = sc.toString().trim();
                              to_sqs.addSelColNameMap(orginalAliasName, orginalAliasName);
                              if (cteColumnList != null && cteColumnList.size() > 0) {
                                 orginalAliasName = (String)cteColumnList.get(i_count);
                                 to_sqs.addSelColNameMap(orginalAliasName, orginalAliasName);
                                 sc.setAliasForExpression(orginalAliasName);
                              } else {
                                 to_sqs.addSelColNameMap(orginalAliasName, orginalAliasName);
                                 sc.setAliasForExpression(orginalAliasName);
                              }
                           } catch (Exception var30) {
                              throw new ConvertException("Provide Alias Name");
                           } finally {
                              SwisSQLAPI.clearProperSelExpTL();
                           }
                        }
                     }
                  } else {
                     orginalAliasName = aliasName;
                     quote = '1';
                     if (GeneralUtil.isItEnclosedAliasName(aliasName)) {
                        quote = aliasName.charAt(0);
                        orginalAliasName = GeneralUtil.trimStartAndEndChars(aliasName).trim();
                     }

                     if (orginalAliasName.isEmpty()) {
                        throw new ConvertException("Alias Name cannot be empty", "SQL_ALIASNAME_EMPTY_IN_QT");
                     }

                     to_sqs.addSelColNameMap(orginalAliasName, orginalAliasName);
                     from_sqs.getAliasColumns().add(orginalAliasName);
                     if (cteColumnList != null && cteColumnList.size() > 0 && this.selectItemList.size() == cteColumnList.size()) {
                        sc.setAliasForExpression((String)cteColumnList.get(i_count));
                     } else {
                        sc.setAliasForExpression(orginalAliasName);
                     }

                     sc.setAliasName(GeneralUtil.encloseColOrTabWithProperQuote(orginalAliasName, quote));
                  }
               }

               SelectColumn selCol = (SelectColumn)this.selectItemList.elementAt(i_count);
               selCol.setMainSelectColumn(true);
               vcConv.addElement(selCol.toReplaceTblCol(to_sqs, from_sqs));
               String finalAliasName = GeneralUtil.trimIfAliasNameIsEnclosed(sc.getAliasForExpression());
               if (finalAliasName != null && CastingUtil.ContainsIgnoreCase(uniqueColList, finalAliasName)) {
                  if (CastingUtil.ContainsIgnoreCase(uniqueColListOfStar, finalAliasName)) {
                     duplicateColsInStar.add(finalAliasName);
                  }

                  duplicateColList.add(finalAliasName);
               } else if (finalAliasName != null) {
                  uniqueColList.add(finalAliasName);
               }
            }
         } else {
            vcConv.addElement((String)this.selectItemList.elementAt(i_count));
         }
      }

      if (duplicateColsInStar != null && !duplicateColsInStar.isEmpty()) {
         duplicateColList.addAll(duplicateColsInStar);
         this.checkDuplicateColsAndThrowEx(from_sqs, duplicateColList, expandedList);
      } else {
         this.checkDuplicateColsAndThrowEx(from_sqs, duplicateColList, new HashSet());
      }

      selStmtConv.setSelectItemList(vcConv);
      return selStmtConv;
   }

   public Set getIndexPositionsSetForStringLiterals(SelectQueryStatement from_sqs) {
      return from_sqs != null ? from_sqs.getIndexPositionsForStringLiterals() : null;
   }

   public Set getIndexPositionsSetForNULLString(SelectQueryStatement from_sqs) {
      return from_sqs != null ? from_sqs.getIndexPositionsForNULLString() : null;
   }

   public static String getProperAliasName(String orginalAliasName) {
      orginalAliasName = orginalAliasName.replaceAll("\"", "");
      orginalAliasName = orginalAliasName.replaceAll("'", "");
      orginalAliasName = orginalAliasName.replaceAll("`", "");
      if (orginalAliasName.endsWith(",")) {
         orginalAliasName = orginalAliasName.substring(0, orginalAliasName.length() - 1);
      }

      return orginalAliasName;
   }

   public void checkAndAddDuplicateCols(SelectQueryStatement to_sqs, HashMap<String, String> tableCols, ArrayList uniqueColList, ArrayList uniqueStarColList, HashSet duplicateColList, HashSet expandedList, String tableName) {
      Iterator var8 = tableCols.keySet().iterator();

      while(var8.hasNext()) {
         Object colName = var8.next();
         to_sqs.addSelColNameMap(colName.toString(), (String)tableCols.get(colName));
         Object colName = GeneralUtil.trimIfAliasNameIsEnclosed(colName.toString());
         if (!colName.toString().equalsIgnoreCase("__ZDBID")) {
            if (CastingUtil.ContainsIgnoreCase(uniqueColList, colName.toString())) {
               duplicateColList.add(colName);
            } else {
               uniqueStarColList.add(colName);
               uniqueColList.add(colName);
            }

            expandedList.add("\"" + tableName + "\".\"" + colName + "\" `" + tableName + "." + colName + "`");
         }
      }

   }

   public void checkDuplicateColsAndThrowEx(SelectQueryStatement from_sqs, HashSet duplicateColList, HashSet expandedList) throws ConvertException {
      if (!duplicateColList.isEmpty() && !from_sqs.getQueryConvDataHandler().isReverseParseConv() && !from_sqs.isSetOperatorSelectQueryStatement()) {
         if (!expandedList.isEmpty()) {
            if (!from_sqs.getCanAllowExceptionStacking()) {
               throw new ConvertException("Duplicate column present ", "DUP_DISP_COLS_IN_QUERY", new Object[]{duplicateColList, expandedList});
            }

            from_sqs.getQueryConvDataHandler().addExceptionMap("DUP_DISP_COLS_IN_QUERY", new Object[]{duplicateColList, expandedList});
         } else {
            if (!from_sqs.getCanAllowExceptionStacking()) {
               throw new ConvertException("Duplicate columns used ", "ZDB_DUPLICATE_COLS", new Object[]{duplicateColList});
            }

            from_sqs.getQueryConvDataHandler().addExceptionMap("ZDB_DUPLICATE_COLS", new Object[]{duplicateColList});
         }
      }

   }
}
