package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.parser.ParseException;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.OpenXML;
import com.adventnet.swissqlapi.sql.statement.delete.DeleteQueryStatement;
import com.adventnet.swissqlapi.sql.statement.update.SetClause;
import com.adventnet.swissqlapi.sql.statement.update.TableClause;
import com.adventnet.swissqlapi.sql.statement.update.TableExpression;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import com.adventnet.swissqlapi.sql.statement.update.UpdateQueryStatement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

public class FromClause {
   public static boolean doNotAddDotInSubquery = false;
   public String fromClause = new String();
   public Vector fromItemList = new Vector();
   private String openBraces;
   private String closedBraces;
   private FetchClause fetchClauseFromSQS;
   private UserObjectContext context = null;
   private CommentClass commentObj;
   private CommentClass commentObjAfterToken;
   private String aliasName;
   private String sqlServerapplyType;
   private boolean isExcel;
   private OpenXML oxml = null;
   private boolean baseFromClauseFound = false;
   private String updateColumnName = null;

   public void setExcel(boolean excel) {
      this.isExcel = excel;
   }

   public void setObjectContext(UserObjectContext context) {
      this.context = context;
   }

   public void setFromClause(String s_fc) {
      this.fromClause = s_fc;
   }

   public void setOpenXML(OpenXML oxml) {
      this.oxml = oxml;
   }

   public OpenXML getOpenXML() {
      return this.oxml;
   }

   public void setFromItemList(Vector v_fil) {
      this.fromItemList = v_fil;
      SelectInvolvedTables tl = (SelectInvolvedTables)SwisSQLAPI.involvedTablesTL.get();
      if (tl.isNeeded) {
         for(int i = 0; i < this.fromItemList.size(); ++i) {
            Object obj = ((FromTable)this.fromItemList.elementAt(i)).tableName;
            if (obj instanceof String) {
               String tableName = obj.toString();
               tl.involvedTables.add(tableName);
            }
         }
      }

   }

   public void setOpenBraces(String openBraces) {
      this.openBraces = openBraces;
   }

   public void setClosedBraces(String closedBraces) {
      this.closedBraces = closedBraces;
   }

   public void setFetchClauseFromSQS(FetchClause fetchClauseFromSQS) {
      this.fetchClauseFromSQS = fetchClauseFromSQS;
   }

   public void setCommentClass(CommentClass commentObj) {
      this.commentObj = commentObj;
   }

   public void setCommentClassAfterToken(CommentClass commentObj) {
      this.commentObjAfterToken = commentObj;
   }

   public void setAliasName(String alias) {
      this.aliasName = alias;
   }

   public String getFromClause() {
      return this.fromClause;
   }

   public Vector getFromItemList() {
      return this.fromItemList;
   }

   public FetchClause getFetchClauseFromSQS() {
      return this.fetchClauseFromSQS;
   }

   public CommentClass getCommentClass() {
      return this.commentObj;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();

      int i;
      for(i = 0; i < SelectQueryStatement.getBeautyTabCount(); ++i) {
         sb.append("\t");
      }

      if (this.commentObj != null) {
         sb.append(this.commentObj.toString().trim());
         sb.append("\n");

         for(i = 0; i < SelectQueryStatement.getBeautyTabCount(); ++i) {
            sb.append("\t");
         }
      }

      if (this.fromClause != null) {
         sb.append(this.fromClause.toUpperCase() + " ");
      }

      SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() + 1);
      if (this.openBraces != null) {
         sb.append("(");
      }

      if (this.oxml != null) {
         sb.append("(");
         sb.append(this.oxml.toString());
         sb.append(")");
      }

      for(i = 0; i < this.fromItemList.size(); ++i) {
         try {
            FromClause newFC;
            int j;
            if (i != this.fromItemList.size() - 1 && (!(this.fromItemList.elementAt(i + 1) instanceof FromTable) || ((FromTable)this.fromItemList.elementAt(i + 1)).getJoinClause() == null && ((FromTable)this.fromItemList.elementAt(i + 1)).getJoinClause() == null)) {
               if (this.fromItemList.elementAt(i) instanceof FromClause) {
                  if (this.openBraces != null) {
                     sb.append("(");
                  }

                  newFC = (FromClause)this.fromItemList.elementAt(i);
                  newFC.setObjectContext(this.context);
                  sb.append(newFC.toString() + ",");
                  if (this.closedBraces != null) {
                     sb.append(")");
                  }
               } else if (this.fromItemList.elementAt(i) instanceof FromTable) {
                  ((FromTable)this.fromItemList.elementAt(i)).setObjectContext(this.context);
                  if (this.isExcel) {
                     String tblName = ((FromTable)this.fromItemList.elementAt(i)).toString().trim();
                     if (!tblName.equalsIgnoreCase("(") && !tblName.equalsIgnoreCase(")")) {
                        sb.append(this.fromItemList.elementAt(i).toString() + ",");
                     } else {
                        sb.append(this.fromItemList.elementAt(i).toString());
                     }
                  } else {
                     sb.append(this.fromItemList.elementAt(i).toString() + ",");
                  }
               } else if (this.fromItemList.elementAt(i) != null) {
                  sb.append(this.fromItemList.elementAt(i).toString() + ",");
               }

               sb.append("\n");

               for(j = 0; j < SelectQueryStatement.getBeautyTabCount(); ++j) {
                  sb.append("\t");
               }
            } else if (((FromTable)this.fromItemList.elementAt(i + 1)).getJoinClause().equalsIgnoreCase("OUTER")) {
               if (this.fromItemList.elementAt(i) instanceof FromClause) {
                  if (this.openBraces != null) {
                     sb.append("(");
                  }

                  newFC = (FromClause)this.fromItemList.elementAt(i);
                  newFC.setObjectContext(this.context);
                  sb.append(newFC.toString() + ",");
                  if (this.closedBraces != null) {
                     sb.append(")");
                  }
               } else if (this.fromItemList.elementAt(i) instanceof FromTable) {
                  ((FromTable)this.fromItemList.elementAt(i)).setObjectContext(this.context);
                  sb.append(this.fromItemList.elementAt(i).toString() + ",");
               } else {
                  sb.append(this.fromItemList.elementAt(i).toString() + ",");
               }

               sb.append("\n");

               for(j = 0; j < SelectQueryStatement.getBeautyTabCount(); ++j) {
                  sb.append("\t");
               }
            } else if (this.fromItemList.elementAt(i) instanceof FromClause) {
               if (this.openBraces != null) {
                  sb.append("(");
               }

               newFC = (FromClause)this.fromItemList.elementAt(i);
               newFC.setObjectContext(this.context);
               sb.append(newFC.toString());
               if (this.closedBraces != null) {
                  sb.append(")");
               }
            } else if (this.fromItemList.elementAt(i) instanceof FromTable) {
               ((FromTable)this.fromItemList.elementAt(i)).setObjectContext(this.context);
               sb.append(this.fromItemList.elementAt(i).toString());
            } else if (this.fromItemList.elementAt(i) != null) {
               sb.append(this.fromItemList.elementAt(i).toString());
            }
         } catch (ArrayIndexOutOfBoundsException var5) {
            if (this.fromItemList.elementAt(i) instanceof FromClause) {
               if (this.openBraces != null) {
                  sb.append("(");
               }

               FromClause newFC = (FromClause)this.fromItemList.elementAt(i);
               newFC.setObjectContext(this.context);
               sb.append(newFC.toString());
               if (this.closedBraces != null) {
                  sb.append(")");
               }
            } else if (this.fromItemList.elementAt(i) instanceof FromTable) {
               ((FromTable)this.fromItemList.elementAt(i)).setObjectContext(this.context);
               sb.append(this.fromItemList.elementAt(i).toString());
            } else {
               sb.append(this.fromItemList.elementAt(i).toString());
            }
         }
      }

      if (this.commentObjAfterToken != null) {
         sb.append(" " + this.commentObjAfterToken.toString().trim());
      }

      if (this.closedBraces != null) {
         sb.append(")");
      }

      if (this.fetchClauseFromSQS != null) {
         sb.append(" " + this.fetchClauseFromSQS.toString());
      }

      SelectQueryStatement.setBeautyTabCount(SelectQueryStatement.getBeautyTabCount() - 1);
      return sb.toString();
   }

   public FromTable getLastElement() {
      if (this.fromItemList != null) {
         for(int i = 0; i < this.fromItemList.size(); ++i) {
            if (this.fromItemList.lastElement() instanceof FromTable) {
               return (FromTable)this.fromItemList.lastElement();
            }

            if (this.fromItemList.lastElement() instanceof FromClause) {
               FromClause fc = (FromClause)this.fromItemList.lastElement();
               fc.getLastElement();
            }
         }
      }

      return null;
   }

   public FromTable getFirstElement() {
      if (this.fromItemList != null) {
         if (this.fromItemList.firstElement() instanceof FromTable) {
            return (FromTable)this.fromItemList.firstElement();
         }

         if (this.fromItemList.firstElement() instanceof FromClause) {
            FromClause fc = (FromClause)this.fromItemList.firstElement();
            fc.getFirstElement();
         }
      }

      return null;
   }

   public FromTable getFromTablefromTheVector() {
      if (this.fromItemList != null) {
         for(int i = 0; i < this.fromItemList.size(); ++i) {
            if (this.fromItemList.elementAt(i) instanceof FromTable) {
               return (FromTable)this.fromItemList.elementAt(i);
            }

            if (this.fromItemList.elementAt(i) instanceof FromClause) {
               FromClause fc = (FromClause)this.fromItemList.elementAt(i);
               fc.getFromTablefromTheVector();
            }
         }
      }

      return null;
   }

   public String getAliasName() {
      return this.aliasName;
   }

   public Object clone() {
      Vector newFromItemList = new Vector();
      FromClause fc = new FromClause();
      fc.setBaseFromClauseFound(this.baseFromClauseFound);
      fc.setClosedBraces(this.closedBraces);
      fc.setCommentClass(this.commentObj);
      fc.setCommentClassAfterToken(this.commentObjAfterToken);
      fc.setFetchClauseFromSQS(this.fetchClauseFromSQS);
      fc.setFromClause(this.fromClause);
      fc.setFromItemList(this.fromItemList);
      fc.setObjectContext(this.context);
      fc.setOpenBraces(this.openBraces);
      fc.setOpenXML(this.oxml);
      Vector fromItemList = fc.getFromItemList();
      if (fromItemList != null) {
         for(int i = 0; i < fromItemList.size(); ++i) {
            Object obj = fromItemList.get(i);
            if (obj instanceof FromTable) {
               FromTable ft = (FromTable)((FromTable)obj).clone();
               newFromItemList.add(ft);
            }
         }

         fc.setFromItemList(newFromItemList);
      }

      return fc;
   }

   public FromClause toANSISelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      FromClause fc = new FromClause();
      fc.setCommentClass(this.commentObj);
      fc.setOpenBraces(this.openBraces);
      fc.setClosedBraces(this.closedBraces);
      fc.setFromClause(this.fromClause);
      Vector v_fil = new Vector();

      for(int i = 0; i < this.fromItemList.size(); ++i) {
         if (this.fromItemList.elementAt(i) instanceof FromTable) {
            v_fil.addElement(((FromTable)this.fromItemList.elementAt(i)).toANSISelect(to_sqs, from_sqs));
         } else {
            v_fil.addElement(((FromClause)this.fromItemList.elementAt(i)).toANSISelect(to_sqs, from_sqs));
         }
      }

      fc.setFromItemList(v_fil);
      if (v_fil.size() == 1) {
         FromTable ft = null;
         if (v_fil.firstElement() instanceof FromTable) {
            ft = (FromTable)v_fil.elementAt(0);
         } else {
            ft = this.getFirstElement();
         }

         if (ft != null && ft.getTableName() instanceof String && (((String)ft.getTableName()).equalsIgnoreCase("DUAL") || ((String)ft.getTableName()).equalsIgnoreCase("SYS.DUAL") || ((String)ft.getTableName()).equalsIgnoreCase("SYSIBM.SYSDUMMY1"))) {
            fc = null;
         }
      }

      return fc;
   }

   public FromClause toTeradataSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      FromClause fc = new FromClause();
      fc.setCommentClass(this.commentObj);
      fc.setOpenBraces(this.openBraces);
      fc.setClosedBraces(this.closedBraces);
      fc.setFromClause(this.fromClause);
      Vector v_fil = new Vector();

      for(int i = 0; i < this.fromItemList.size(); ++i) {
         if (this.fromItemList.elementAt(i) != null) {
            if (!(this.fromItemList.elementAt(i) instanceof FromTable)) {
               v_fil.addElement(((FromClause)this.fromItemList.elementAt(i)).toTeradataSelect(to_sqs, from_sqs));
            } else {
               FromTable teradataFromTable = ((FromTable)this.fromItemList.elementAt(i)).toTeradataSelect(to_sqs, from_sqs);
               if ((this.fromClause == null || this.fromClause.length() == 0) && teradataFromTable.getFromClauseOpenBraces() == null && teradataFromTable.getAliasName() != null && teradataFromTable.getAliasName().startsWith("SwisSQL")) {
                  fc.setAliasName(teradataFromTable.getAliasName());
                  teradataFromTable.setAliasName((String)null);
               }

               v_fil.addElement(teradataFromTable);
               if (teradataFromTable.getJoinExpression() == null && teradataFromTable.getQueryPartitionClause() != null && i != this.fromItemList.size() - 1) {
                  if (this.fromItemList.elementAt(i + 1) instanceof FromTable) {
                     ((FromTable)this.fromItemList.elementAt(i + 1)).setCrossJoinForPartitionClause(teradataFromTable.getCrossJoinForPartitionClause());
                     ((FromTable)this.fromItemList.elementAt(i + 1)).setCrossJoinExpression(teradataFromTable.getCrossJoinExpression());
                  } else if (this.fromItemList.elementAt(i + 1) instanceof FromClause) {
                     ((FromTable)((FromClause)this.fromItemList.elementAt(i + 1)).getFromItemList().firstElement()).setCrossJoinForPartitionClause(teradataFromTable.getCrossJoinForPartitionClause());
                  }
               } else if (teradataFromTable.getQueryPartitionClause() != null) {
                  v_fil.insertElementAt(teradataFromTable.getCrossJoinForPartitionClause(), v_fil.size() - 1);
               }
            }
         }
      }

      fc.setFromItemList(v_fil);
      if (v_fil.size() == 1) {
         FromTable ft = null;
         if (v_fil.firstElement() instanceof FromTable) {
            ft = (FromTable)v_fil.elementAt(0);
         } else {
            ft = this.getFirstElement();
         }

         if (!SwisSQLOptions.isDualTableNameRequired && ft != null && ft.getTableName() instanceof String) {
            String tabName = (String)ft.getTableName();
            String ignoreQuotes = tabName.replaceAll("\"", "");
            if (ignoreQuotes.equalsIgnoreCase("DUAL") || ignoreQuotes.equalsIgnoreCase("SYS.DUAL")) {
               fc = null;
            }
         }

         if (ft != null && ft.getTableName() instanceof String && ((String)ft.getTableName()).equalsIgnoreCase("SYSIBM.SYSDUMMY1")) {
            fc = null;
         }
      }

      return fc;
   }

   public FromClause toDB2Select(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      FromClause fc = new FromClause();
      fc.setCommentClass(this.commentObj);
      fc.setOpenBraces(this.openBraces);
      fc.setClosedBraces(this.closedBraces);
      fc.setFromClause(this.fromClause);
      Vector v_fil = new Vector();

      for(int i = 0; i < this.fromItemList.size(); ++i) {
         if (this.fromItemList.elementAt(i) instanceof FromTable) {
            v_fil.addElement(((FromTable)this.fromItemList.elementAt(i)).toDB2Select(to_sqs, from_sqs));
         } else if (this.fromItemList.elementAt(i) instanceof FromClause) {
            v_fil.addElement(((FromClause)this.fromItemList.elementAt(i)).toDB2Select(to_sqs, from_sqs));
         }
      }

      fc.setFromItemList(v_fil);
      if (v_fil.size() == 1) {
         FromTable ft = null;
         if (v_fil.firstElement() instanceof FromTable) {
            ft = (FromTable)v_fil.elementAt(0);
         } else {
            ft = this.getFirstElement();
         }

         if (ft != null && ft.getTableName() instanceof String && (((String)ft.getTableName()).equalsIgnoreCase("DUAL") || ((String)ft.getTableName()).equalsIgnoreCase("SYS.DUAL"))) {
            ft.setTableName("SYSIBM.SYSDUMMY1");
            FetchClause fec = new FetchClause();
            fec.setFetchFirstClause("FETCH FIRST");
            fec.setFetchCount("1");
            fec.setRowOnlyClause("ROWS ONLY");
            if (from_sqs.getFetchClause() != null) {
               throw new ConvertException("Conversion failure..");
            }

            to_sqs.setFetchClause(fec);
         } else if (ft != null && ft.getTableName() instanceof String && ((String)ft.getTableName()).equalsIgnoreCase("USER_SEQUENCES")) {
            ft.setTableName("SYSIBM.SYSSEQUENCES");
         }
      }

      if (this.fetchClauseFromSQS != null) {
         fc.setFetchClauseFromSQS(this.fetchClauseFromSQS.toDB2Select(to_sqs, from_sqs));
      }

      return fc;
   }

   public FromClause toMySQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      FromClause fc = new FromClause();
      fc.setCommentClass(this.commentObj);
      fc.setCommentClassAfterToken(this.commentObjAfterToken);
      fc.setFromClause(this.fromClause);
      Vector v_fil = new Vector();

      for(int i = 0; i < this.fromItemList.size(); ++i) {
         if (this.fromItemList.elementAt(i) instanceof FromTable) {
            v_fil.addElement(((FromTable)this.fromItemList.elementAt(i)).toMySQLSelect(to_sqs, from_sqs));
         } else if (this.fromItemList.elementAt(i) instanceof FromClause) {
            v_fil.addElement(((FromClause)this.fromItemList.elementAt(i)).toMySQLSelect(to_sqs, from_sqs));
         }
      }

      fc.setFromItemList(v_fil);
      if (v_fil.size() == 1) {
         FromTable ft = null;
         if (v_fil.firstElement() instanceof FromTable) {
            ft = (FromTable)v_fil.elementAt(0);
         } else {
            ft = this.getFirstElement();
         }

         if (ft != null && ft.getTableName() instanceof String && (((String)ft.getTableName()).equalsIgnoreCase("DUAL") || ((String)ft.getTableName()).equalsIgnoreCase("SYS.DUAL") || ((String)ft.getTableName()).equalsIgnoreCase("SYSIBM.SYSDUMMY1"))) {
            fc = null;
         }
      }

      return fc;
   }

   public FromClause toMSSQLServerSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      FromClause fc = new FromClause();
      fc.setCommentClass(this.commentObj);
      fc.setOpenBraces(this.openBraces);
      fc.setClosedBraces(this.closedBraces);
      fc.setFromClause(this.fromClause);
      Vector v_fil = new Vector();

      for(int i = 0; i < this.fromItemList.size(); ++i) {
         if (this.fromItemList.elementAt(i) instanceof FromTable) {
            v_fil.addElement(((FromTable)this.fromItemList.elementAt(i)).toMSSQLServerSelect(to_sqs, from_sqs));
         } else if (this.fromItemList.elementAt(i) instanceof FromClause) {
            v_fil.addElement(((FromClause)this.fromItemList.elementAt(i)).toMSSQLServerSelect(to_sqs, from_sqs));
         }
      }

      fc.setFromItemList(v_fil);
      if (v_fil.size() == 1) {
         FromTable ft = null;
         if (v_fil.firstElement() instanceof FromTable) {
            ft = (FromTable)v_fil.elementAt(0);
         } else if (v_fil.firstElement() instanceof FromClause) {
            ft = this.getFirstElement();
         }

         if (ft != null && ft.getTableName() instanceof String && (((String)ft.getTableName()).equalsIgnoreCase("DUAL") || ((String)ft.getTableName()).equalsIgnoreCase("SYS.DUAL") || ((String)ft.getTableName()).equalsIgnoreCase("SYSIBM.SYSDUMMY1"))) {
            fc = null;
         }
      }

      return fc;
   }

   public FromClause toSybaseSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      FromClause fc = new FromClause();
      fc.setCommentClass(this.commentObj);
      fc.setOpenBraces(this.openBraces);
      fc.setClosedBraces(this.closedBraces);
      fc.setFromClause(this.fromClause);
      Vector v_fil = new Vector();

      for(int i = 0; i < this.fromItemList.size(); ++i) {
         if (this.fromItemList.elementAt(i) instanceof FromTable) {
            v_fil.addElement(((FromTable)this.fromItemList.elementAt(i)).toSybaseSelect(to_sqs, from_sqs));
         } else if (this.fromItemList.elementAt(i) instanceof FromClause) {
            v_fil.addElement(((FromClause)this.fromItemList.elementAt(i)).toSybaseSelect(to_sqs, from_sqs));
         }
      }

      fc.setFromItemList(v_fil);
      if (v_fil.size() == 1) {
         FromTable ft = null;
         if (v_fil.elementAt(0) instanceof FromTable) {
            ft = (FromTable)v_fil.elementAt(0);
         } else if (v_fil.elementAt(0) instanceof FromClause) {
            ft = this.getFirstElement();
         }

         if (ft != null && ft.getTableName() instanceof String && (((String)ft.getTableName()).equalsIgnoreCase("DUAL") || ((String)ft.getTableName()).equalsIgnoreCase("SYS.DUAL") || ((String)ft.getTableName()).equalsIgnoreCase("SYSIBM.SYSDUMMY1"))) {
            fc = null;
         }
      }

      return fc;
   }

   public void setBaseFromClauseFound(boolean baseFromClauseFound) {
      this.baseFromClauseFound = baseFromClauseFound;
   }

   public FromClause toOracleSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      FromClause fc = null;
      if (this.baseFromClauseFound) {
         fc = to_sqs.getFromClause();
      } else {
         fc = this.copyObjectValues();
      }

      fc.setCommentClass(this.commentObj);
      fc.setFromClause(this.fromClause);
      fc.setOpenBraces((String)null);
      fc.setClosedBraces((String)null);
      fc.setOpenXML(this.oxml);
      Vector v_fil = new Vector();
      fc.setFromItemList(v_fil);

      for(int i = 0; i < this.fromItemList.size(); ++i) {
         if (this.fromItemList.elementAt(i) instanceof FromClause) {
            FromClause fromCl = (FromClause)this.fromItemList.elementAt(i);
            fromCl.setObjectContext(this.context);
            fromCl.baseFromClauseFound = false;
            v_fil.addElement(((FromClause)this.fromItemList.elementAt(i)).toOracleSelect(to_sqs, from_sqs));
         } else if (this.fromItemList.elementAt(i) instanceof FromTable) {
            FromTable ft = (FromTable)this.fromItemList.elementAt(i);
            ft.setObjectContext(this.context);
            Object obj = ft.getTableName();
            String join = ft.getJoinClause();
            if (join != null && join.trim().equalsIgnoreCase("apply") && i > 0 && this.fromItemList.elementAt(i - 1) instanceof FromTable) {
               FromTable ftPrevious = (FromTable)v_fil.elementAt(i - 1);
               String alias = ftPrevious.getAliasName();
               if (alias != null && alias.trim().equalsIgnoreCase("cross") || alias.trim().equalsIgnoreCase("outer")) {
                  ftPrevious.setAliasName((String)null);
                  this.sqlServerapplyType = alias.trim() + " " + join.trim();
                  ft.setJoinClause(this.sqlServerapplyType);
               }
            }

            if (obj instanceof String) {
               String temp = (String)obj;
               if (temp.indexOf(".") != -1) {
                  temp = temp.substring(temp.lastIndexOf(".") + 1);
               }

               ft.setOrigTableName(temp);
            }

            v_fil.addElement(ft.toOracleSelect(to_sqs, from_sqs));
         }
      }

      if (v_fil.size() == 1) {
         FromTable ft = null;
         if (v_fil.firstElement() instanceof FromTable) {
            ft = (FromTable)v_fil.elementAt(0);
         } else {
            ft = this.getFirstElement();
         }

         if (ft != null && ft.getTableName() instanceof String && (((String)ft.getTableName()).equalsIgnoreCase("SYSIBM.SYSDUMMY1") || ((String)ft.getTableName()).equalsIgnoreCase("SYSDUMMY1"))) {
            ft.setTableName("DUAL");
         }
      }

      fc.setFromItemList(v_fil);
      return fc;
   }

   public FromClause toInformixSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      FromClause fc = new FromClause();
      fc.setCommentClass(this.commentObj);
      fc.setFromClause(this.fromClause);
      fc.setOpenBraces((String)null);
      fc.setClosedBraces((String)null);
      Vector v_fil = new Vector();

      for(int i = 0; i < this.fromItemList.size(); ++i) {
         if (this.fromItemList.elementAt(i) instanceof FromClause) {
            v_fil.addElement(((FromClause)this.fromItemList.elementAt(i)).toInformixSelect(to_sqs, from_sqs));
         } else if (this.fromItemList.elementAt(i) instanceof FromTable) {
            v_fil.addElement(((FromTable)this.fromItemList.elementAt(i)).toInformixSelect(to_sqs, from_sqs));
         }
      }

      fc.setFromItemList(v_fil);
      if (v_fil.size() == 1) {
         FromTable ft = null;
         if (v_fil.elementAt(0) instanceof FromTable) {
            ft = (FromTable)v_fil.elementAt(0);
         } else if (v_fil.elementAt(0) instanceof FromClause) {
            ft = this.getFirstElement();
         }

         if (ft != null && ft.getTableName() instanceof String && (((String)ft.getTableName()).equalsIgnoreCase("DUAL") || ((String)ft.getTableName()).equalsIgnoreCase("SYS.DUAL"))) {
            SelectStatement ifxSelectStatement = to_sqs.getSelectStatement();
            ifxSelectStatement.setSelectRowSpecifier((String)null);
            ifxSelectStatement.setInformixRowSpecifier("FIRST");
            ifxSelectStatement.setSelectRowCount(1);
            ft.setTableName("SYSTABLES");
         }
      }

      return fc;
   }

   public FromClause toBigQuerySelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      FromClause fc = new FromClause();
      fc.setCommentClass((CommentClass)null);
      fc.setOpenBraces(this.openBraces);
      fc.setClosedBraces(this.closedBraces);
      fc.setFromClause(this.fromClause);
      Vector v_fil = new Vector();

      for(int i = 0; i < this.fromItemList.size(); ++i) {
         if (this.fromItemList.elementAt(i) instanceof FromTable) {
            v_fil.addElement(((FromTable)this.fromItemList.elementAt(i)).toBigQuerySelect(to_sqs, from_sqs));
         } else if (this.fromItemList.elementAt(i) instanceof FromClause) {
            v_fil.addElement(((FromClause)this.fromItemList.elementAt(i)).toBigQuerySelect(to_sqs, from_sqs));
         }
      }

      fc.setFromItemList(v_fil);
      if (v_fil.size() == 1) {
         FromTable ft = null;
         if (v_fil.elementAt(0) instanceof FromTable) {
            ft = (FromTable)v_fil.elementAt(0);
         } else if (v_fil.elementAt(0) instanceof FromClause) {
            ft = this.getFirstElement();
         }

         if (ft != null && ft.getTableName() instanceof String && (((String)ft.getTableName()).equalsIgnoreCase("DUAL") || ((String)ft.getTableName()).equalsIgnoreCase("SYS.DUAL"))) {
            fc = null;
         }
      }

      return fc;
   }

   public FromClause toPostgreSQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      FromClause fc = new FromClause();
      fc.setCommentClass((CommentClass)null);
      fc.setOpenBraces(this.openBraces);
      fc.setClosedBraces(this.closedBraces);
      fc.setFromClause(this.fromClause);
      Vector v_fil = new Vector();

      for(int i = 0; i < this.fromItemList.size(); ++i) {
         if (this.fromItemList.elementAt(i) instanceof FromTable) {
            v_fil.addElement(((FromTable)this.fromItemList.elementAt(i)).toPostgreSQLSelect(to_sqs, from_sqs));
         } else if (this.fromItemList.elementAt(i) instanceof FromClause) {
            v_fil.addElement(((FromClause)this.fromItemList.elementAt(i)).toPostgreSQLSelect(to_sqs, from_sqs));
         }
      }

      fc.setFromItemList(v_fil);
      if (v_fil.size() == 1) {
         FromTable ft = null;
         if (v_fil.elementAt(0) instanceof FromTable) {
            ft = (FromTable)v_fil.elementAt(0);
         } else if (v_fil.elementAt(0) instanceof FromClause) {
            ft = this.getFirstElement();
         }

         if (ft != null && ft.getTableName() instanceof String && (((String)ft.getTableName()).equalsIgnoreCase("DUAL") || ((String)ft.getTableName()).equalsIgnoreCase("SYS.DUAL"))) {
            fc = null;
         }
      }

      return fc;
   }

   public FromClause toTimesTenSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      FromClause fc = null;
      if (this.baseFromClauseFound) {
         fc = to_sqs.getFromClause();
      } else {
         fc = this.copyObjectValues();
      }

      fc.setFromClause(this.fromClause);
      fc.setOpenBraces((String)null);
      fc.setClosedBraces((String)null);
      fc.setOpenXML((OpenXML)null);
      Vector v_fil = new Vector();
      fc.setFromItemList(v_fil);

      for(int i = 0; i < this.fromItemList.size(); ++i) {
         if (this.fromItemList.elementAt(i) instanceof FromClause) {
            FromClause fromCl = (FromClause)this.fromItemList.elementAt(i);
            fromCl.setObjectContext(this.context);
            fromCl.baseFromClauseFound = false;
            v_fil.addElement(((FromClause)this.fromItemList.elementAt(i)).toTimesTenSelect(to_sqs, from_sqs));
         } else if (this.fromItemList.elementAt(i) instanceof FromTable) {
            ((FromTable)this.fromItemList.elementAt(i)).setObjectContext(this.context);
            v_fil.addElement(((FromTable)this.fromItemList.elementAt(i)).toTimesTenSelect(to_sqs, from_sqs));
         }
      }

      if (v_fil.size() == 1) {
         FromTable ft = null;
         if (v_fil.firstElement() instanceof FromTable) {
            ft = (FromTable)v_fil.elementAt(0);
         } else {
            ft = this.getFirstElement();
         }

         if (ft != null && ft.getTableName() instanceof String && (((String)ft.getTableName()).equalsIgnoreCase("SYSIBM.SYSDUMMY1") || ((String)ft.getTableName()).equalsIgnoreCase("SYSDUMMY1"))) {
            ft.setTableName("MONITOR");
         }
      }

      fc.setFromItemList(v_fil);
      return fc;
   }

   public FromClause toNetezzaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      FromClause fc = new FromClause();
      fc.setCommentClass(this.commentObj);
      fc.setOpenBraces(this.openBraces);
      fc.setClosedBraces(this.closedBraces);
      fc.setFromClause(this.fromClause);
      Vector v_fil = new Vector();

      for(int i = 0; i < this.fromItemList.size(); ++i) {
         if (this.fromItemList.elementAt(i) instanceof FromTable) {
            v_fil.addElement(((FromTable)this.fromItemList.elementAt(i)).toNetezzaSelect(to_sqs, from_sqs));
         } else {
            v_fil.addElement(((FromClause)this.fromItemList.elementAt(i)).toNetezzaSelect(to_sqs, from_sqs));
         }
      }

      fc.setFromItemList(v_fil);
      if (v_fil.size() == 1) {
         FromTable ft = null;
         if (v_fil.firstElement() instanceof FromTable) {
            ft = (FromTable)v_fil.elementAt(0);
         } else {
            ft = this.getFirstElement();
         }

         if (ft != null && ft.getTableName() instanceof String && (((String)ft.getTableName()).equalsIgnoreCase("DUAL") || ((String)ft.getTableName()).equalsIgnoreCase("SYS.DUAL") || ((String)ft.getTableName()).equalsIgnoreCase("SYSIBM.SYSDUMMY1"))) {
            fc = null;
         }
      }

      return fc;
   }

   public FromClause toSnowflakeSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      FromClause fc = new FromClause();
      fc.setCommentClass((CommentClass)null);
      fc.setOpenBraces(this.openBraces);
      fc.setClosedBraces(this.closedBraces);
      fc.setFromClause(this.fromClause);
      Vector v_fil = new Vector();

      for(int i = 0; i < this.fromItemList.size(); ++i) {
         if (this.fromItemList.elementAt(i) instanceof FromTable) {
            v_fil.addElement(((FromTable)this.fromItemList.elementAt(i)).toSnowflakeSelect(to_sqs, from_sqs));
         } else if (this.fromItemList.elementAt(i) instanceof FromClause) {
            v_fil.addElement(((FromClause)this.fromItemList.elementAt(i)).toSnowflakeSelect(to_sqs, from_sqs));
         }
      }

      fc.setFromItemList(v_fil);
      if (v_fil.size() == 1) {
         FromTable ft = null;
         if (v_fil.elementAt(0) instanceof FromTable) {
            ft = (FromTable)v_fil.elementAt(0);
         } else if (v_fil.elementAt(0) instanceof FromClause) {
            ft = this.getFirstElement();
         }

         if (ft != null && ft.getTableName() instanceof String && (((String)ft.getTableName()).equalsIgnoreCase("DUAL") || ((String)ft.getTableName()).equalsIgnoreCase("SYS.DUAL"))) {
            fc = null;
         }
      }

      return fc;
   }

   public FromClause toAthenaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      FromClause fc = new FromClause();
      fc.setCommentClass((CommentClass)null);
      fc.setOpenBraces(this.openBraces);
      fc.setClosedBraces(this.closedBraces);
      fc.setFromClause(this.fromClause);
      Vector v_fil = new Vector();

      for(int i = 0; i < this.fromItemList.size(); ++i) {
         if (this.fromItemList.elementAt(i) instanceof FromTable) {
            v_fil.addElement(((FromTable)this.fromItemList.elementAt(i)).toAthenaSelect(to_sqs, from_sqs));
         } else if (this.fromItemList.elementAt(i) instanceof FromClause) {
            v_fil.addElement(((FromClause)this.fromItemList.elementAt(i)).toAthenaSelect(to_sqs, from_sqs));
         }
      }

      fc.setFromItemList(v_fil);
      if (v_fil.size() == 1) {
         FromTable ft = null;
         if (v_fil.elementAt(0) instanceof FromTable) {
            ft = (FromTable)v_fil.elementAt(0);
         } else if (v_fil.elementAt(0) instanceof FromClause) {
            ft = this.getFirstElement();
         }

         if (ft != null && ft.getTableName() instanceof String && (((String)ft.getTableName()).equalsIgnoreCase("DUAL") || ((String)ft.getTableName()).equalsIgnoreCase("SYS.DUAL"))) {
            fc = null;
         }
      }

      return fc;
   }

   public FromClause toSapHanaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      FromClause fc = new FromClause();
      fc.setCommentClass((CommentClass)null);
      fc.setOpenBraces(this.openBraces);
      fc.setClosedBraces(this.closedBraces);
      fc.setFromClause(this.fromClause);
      Vector v_fil = new Vector();

      for(int i = 0; i < this.fromItemList.size(); ++i) {
         if (this.fromItemList.elementAt(i) instanceof FromTable) {
            v_fil.addElement(((FromTable)this.fromItemList.elementAt(i)).toSapHanaSelect(to_sqs, from_sqs));
         } else if (this.fromItemList.elementAt(i) instanceof FromClause) {
            v_fil.addElement(((FromClause)this.fromItemList.elementAt(i)).toSapHanaSelect(to_sqs, from_sqs));
         }
      }

      fc.setFromItemList(v_fil);
      if (v_fil.size() == 1) {
         FromTable ft = null;
         if (v_fil.elementAt(0) instanceof FromTable) {
            ft = (FromTable)v_fil.elementAt(0);
         } else if (v_fil.elementAt(0) instanceof FromClause) {
            ft = this.getFirstElement();
         }

         if (ft != null && ft.getTableName() instanceof String && (((String)ft.getTableName()).equalsIgnoreCase("DUAL") || ((String)ft.getTableName()).equalsIgnoreCase("SYS.DUAL") || ((String)ft.getTableName()).equalsIgnoreCase("SYS.DUMMY"))) {
            fc = null;
         }
      }

      return fc;
   }

   public FromClause toSqliteSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      FromClause fc = new FromClause();
      fc.setCommentClass((CommentClass)null);
      fc.setOpenBraces(this.openBraces);
      fc.setClosedBraces(this.closedBraces);
      fc.setFromClause(this.fromClause);
      Vector v_fil = new Vector();

      for(int i = 0; i < this.fromItemList.size(); ++i) {
         if (this.fromItemList.elementAt(i) instanceof FromTable) {
            v_fil.addElement(((FromTable)this.fromItemList.elementAt(i)).toSqliteSelect(to_sqs, from_sqs));
         } else if (this.fromItemList.elementAt(i) instanceof FromClause) {
            v_fil.addElement(((FromClause)this.fromItemList.elementAt(i)).toSqliteSelect(to_sqs, from_sqs));
         }
      }

      fc.setFromItemList(v_fil);
      if (v_fil.size() == 1) {
         FromTable ft = null;
         if (v_fil.elementAt(0) instanceof FromTable) {
            ft = (FromTable)v_fil.elementAt(0);
         } else if (v_fil.elementAt(0) instanceof FromClause) {
            ft = this.getFirstElement();
         }

         if (ft != null && ft.getTableName() instanceof String && (((String)ft.getTableName()).equalsIgnoreCase("sqlite_master") || ((String)ft.getTableName()).equalsIgnoreCase("sqlite_sequence") || ((String)ft.getTableName()).equalsIgnoreCase("sqlite_stat1"))) {
            fc = null;
         }
      }

      return fc;
   }

   public FromClause toExcelSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      FromClause fc = new FromClause();
      fc.setCommentClass((CommentClass)null);
      fc.setOpenBraces(this.openBraces);
      fc.setClosedBraces(this.closedBraces);
      fc.setFromClause(this.fromClause);
      Vector v_fil = new Vector();

      for(int i = 0; i < this.fromItemList.size(); ++i) {
         if (this.fromItemList.elementAt(i) instanceof FromTable) {
            FromTable ft = ((FromTable)this.fromItemList.elementAt(i)).toExcelSelect(to_sqs, from_sqs);
            if (ft != null && ft.getTableName() instanceof String) {
               SwisSQLAPI.excelTblName = (String)ft.getTableName();
            }

            v_fil.addElement(ft);
         } else if (this.fromItemList.elementAt(i) instanceof FromClause) {
            v_fil.addElement(((FromClause)this.fromItemList.elementAt(i)).toExcelSelect(to_sqs, from_sqs));
         }
      }

      fc.setFromItemList(v_fil);
      if (v_fil.size() == 1) {
         FromTable ft = null;
         if (v_fil.elementAt(0) instanceof FromTable) {
            ft = (FromTable)v_fil.elementAt(0);
         } else if (v_fil.elementAt(0) instanceof FromClause) {
            ft = this.getFirstElement();
         }

         if (ft != null && ft.getTableName() instanceof String && (((String)ft.getTableName()).equalsIgnoreCase("DUAL") || ((String)ft.getTableName()).equalsIgnoreCase("SYS.DUAL") || ((String)ft.getTableName()).equalsIgnoreCase("DUAL"))) {
            fc = null;
         }
      }

      return fc;
   }

   public FromClause toMsAccessJdbcSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      FromClause fc = new FromClause();
      fc.setCommentClass(this.commentObj);
      fc.setCommentClassAfterToken(this.commentObjAfterToken);
      fc.setFromClause(this.fromClause);
      Vector v_fil = new Vector();

      for(int i = 0; i < this.fromItemList.size(); ++i) {
         if (this.fromItemList.elementAt(i) instanceof FromTable) {
            v_fil.addElement(((FromTable)this.fromItemList.elementAt(i)).toMsAccessJdbcSelect(to_sqs, from_sqs));
         } else if (this.fromItemList.elementAt(i) instanceof FromClause) {
            v_fil.addElement(((FromClause)this.fromItemList.elementAt(i)).toMsAccessJdbcSelect(to_sqs, from_sqs));
         }
      }

      fc.setFromItemList(v_fil);
      if (v_fil.size() == 1) {
         FromTable ft = null;
         if (v_fil.firstElement() instanceof FromTable) {
            ft = (FromTable)v_fil.elementAt(0);
         } else {
            ft = this.getFirstElement();
         }

         if (ft != null && ft.getTableName() instanceof String && (((String)ft.getTableName()).equalsIgnoreCase("DUAL") || ((String)ft.getTableName()).equalsIgnoreCase("SYS.DUAL") || ((String)ft.getTableName()).equalsIgnoreCase("SYSIBM.SYSDUMMY1"))) {
            fc = null;
         }
      }

      return fc;
   }

   public void changeWhereItem(WhereExpression whereExpression, String tableName, String columnName, String orgTableName) {
      Hashtable columnNameTable = new Hashtable();
      if (SwisSQLAPI.getDataTypesFromMetaDataHT() != null) {
         if (SwisSQLAPI.getDataTypesFromMetaDataHT().containsKey(tableName.trim().toUpperCase())) {
            columnNameTable = (Hashtable)SwisSQLAPI.getDataTypesFromMetaDataHT().get(tableName.trim().toUpperCase());
         } else if (SwisSQLAPI.getDataTypesFromMetaDataHT().containsKey(tableName.trim().toLowerCase())) {
            columnNameTable = (Hashtable)SwisSQLAPI.getDataTypesFromMetaDataHT().get(tableName.trim().toLowerCase());
         }
      }

      if (whereExpression != null) {
         Vector whereItemList = whereExpression.getWhereItems();
         String alias = tableName;
         boolean columnNameAdded = false;
         int i = 0;

         for(int size = whereItemList.size(); i < size; ++i) {
            WhereItem whereItem = null;
            if (whereItemList.elementAt(i) instanceof WhereItem) {
               whereItem = (WhereItem)whereItemList.elementAt(i);
               WhereColumn wc = whereItem.getLeftWhereExp();
               if (wc != null) {
                  Vector colExp = wc.getColumnExpression();
                  TableColumn tblCol;
                  Object obj;
                  if (colExp != null) {
                     obj = colExp.elementAt(0);
                     if (obj instanceof TableColumn) {
                        tblCol = (TableColumn)obj;
                        if (tableName.equalsIgnoreCase(tblCol.getTableName())) {
                           tblCol.setTableName(alias);
                           columnName = tblCol.getColumnName();
                           if (!columnNameAdded) {
                              this.updateColumnName = columnName;
                              columnNameAdded = true;
                           }
                        } else if ((columnNameTable.containsKey(tblCol.getColumnName().trim().toLowerCase()) || columnNameTable.containsKey(tblCol.getColumnName().trim().toUpperCase())) && !columnNameAdded) {
                           this.updateColumnName = tblCol.getColumnName();
                           columnNameAdded = true;
                        }
                     }
                  }

                  Object obj = whereItem.getRightWhereExp();
                  if (obj instanceof WhereColumn) {
                     wc = (WhereColumn)obj;
                     colExp = wc.getColumnExpression();
                     if (colExp != null && colExp.size() != 0) {
                        obj = colExp.elementAt(0);
                        if (obj instanceof TableColumn) {
                           tblCol = (TableColumn)obj;
                           if (tableName.equalsIgnoreCase(tblCol.getTableName())) {
                              tblCol.setTableName(alias);
                              columnName = tblCol.getColumnName();
                              if (!columnNameAdded) {
                                 this.updateColumnName = columnName;
                                 columnNameAdded = true;
                              }
                           } else if ((columnNameTable.containsKey(tblCol.getColumnName().trim().toLowerCase()) || columnNameTable.containsKey(tblCol.getColumnName().trim().toUpperCase())) && !columnNameAdded) {
                              this.updateColumnName = tblCol.getColumnName();
                              columnNameAdded = true;
                           }
                        }
                     }
                  }
               }
            } else if (whereItemList.elementAt(i) instanceof WhereExpression) {
               this.changeWhereItem((WhereExpression)whereItemList.elementAt(i), tableName, columnName, orgTableName);
            }
         }

         if (this.updateColumnName == null && columnNameAdded) {
            this.updateColumnName = columnName;
         }
      }

   }

   public void convertToSubQuery(UpdateQueryStatement uqs, int database, FromClause fc) throws ConvertException {
      TableExpression tblExp = uqs.getTableExpression();
      TableClause tc = (TableClause)tblExp.getTableClauseList().get(0);
      TableObject tableObject = tc.getTableObject();
      String tableName = tableObject.getTableName();
      String orgTableName = null;
      String alias = tableName.toUpperCase();
      WhereExpression whereExpression = uqs.getWhereExpression();
      if (fc != null && fc.getFromItemList() != null) {
         Vector fromItems = fc.getFromItemList();

         for(int i = 0; i < fromItems.size(); ++i) {
            if (!(fromItems.get(i) instanceof FromTable)) {
               if (fromItems.get(i) instanceof FromClause) {
                  FromClause newFC = (FromClause)fromItems.get(i);
                  Vector newFromItems = newFC.getFromItemList();
                  this.processTheFromTableInsideTheFromItemList(fromItems, newFromItems, tableObject, tableName, orgTableName, alias, uqs, whereExpression);
               }
            } else {
               FromTable ft = (FromTable)fromItems.get(i);
               Object tableObjectInFrom;
               String tableNameInFrom;
               if (ft.getTableName() != null && ft.getAliasName() != null) {
                  tableObjectInFrom = ft.getTableName();
                  if (tableObjectInFrom instanceof String) {
                     tableNameInFrom = tableObject.toString();
                     if (tableNameInFrom.indexOf(46) != -1) {
                        tableNameInFrom = tableNameInFrom.substring(tableNameInFrom.indexOf(46));
                        if (tableNameInFrom.indexOf(46) != -1) {
                           tableNameInFrom = tableNameInFrom.substring(tableNameInFrom.indexOf(46));
                        }
                     }

                     if (tableNameInFrom.trim().equalsIgnoreCase(tableName)) {
                        orgTableName = tableName;
                        tableName = ft.getAliasName();
                        alias = ft.getAliasName();
                     }
                  }
               } else if (ft.getTableName() != null) {
                  tableObjectInFrom = ft.getTableName();
                  if (tableObjectInFrom instanceof String) {
                     tableNameInFrom = tableObjectInFrom.toString();
                     if (tableNameInFrom.indexOf(46) != -1) {
                        tableNameInFrom = tableNameInFrom.substring(tableNameInFrom.indexOf(46));
                        if (tableNameInFrom.indexOf(46) != -1) {
                           tableNameInFrom = tableNameInFrom.substring(tableNameInFrom.indexOf(46));
                        }
                     }

                     if (tableNameInFrom.trim().equalsIgnoreCase(tableName) && fromItems.size() == 1) {
                        uqs.setFromClause((FromClause)null);
                        return;
                     }

                     if (tableNameInFrom.trim().equalsIgnoreCase(tableName)) {
                        if (ft != null && ft.getJoinExpression() != null) {
                           for(int j = 0; j < ft.getJoinExpression().size(); ++j) {
                              WhereExpression whereExp = (WhereExpression)ft.getJoinExpression().get(j);
                              if (whereExpression != null) {
                                 if (whereExp.getOperator() != null && (whereExp.getOperator() == null || whereExp.getOperator().size() >= 1)) {
                                    for(int k = 0; k < whereExp.getOperator().size(); ++k) {
                                       whereExpression.addOperator((String)whereExp.getOperator().get(k));
                                    }
                                 } else {
                                    whereExpression.addOperator("AND");
                                 }

                                 whereExpression.addWhereExpression((WhereExpression)ft.getJoinExpression().get(j));
                              }
                           }
                        }

                        fromItems.remove(i);
                        alias = tableName.toUpperCase();
                     }
                  }
               }
            }
         }
      }

      String columnName = new String();
      this.changeWhereItem(whereExpression, tableName, columnName, orgTableName);
      if (this.updateColumnName == null) {
         SetClause setClause = uqs.getSetClause();
         ArrayList expList = setClause.getExpression();
         if (expList == null) {
            expList = setClause.getSetExpressionList();
         }

         if (expList != null && expList.size() > 0) {
            this.updateColumnName = expList.get(0).toString();
         }
      }

      WhereExpression newWhereExpression = new WhereExpression();
      WhereItem newWhereItem = new WhereItem();
      WhereColumn newWhereColumn = new WhereColumn();
      Vector colExp = new Vector();
      TableColumn newTableColumn = new TableColumn();
      newTableColumn.setColumnName(this.updateColumnName);
      if (this.updateColumnName.indexOf(".") == -1) {
         if (orgTableName == null) {
            newTableColumn.setTableName(tableName);
         } else {
            newTableColumn.setTableName(orgTableName);
         }
      }

      colExp.addElement(newTableColumn);
      newWhereColumn.setColumnExpression(colExp);
      newWhereItem.setLeftWhereExp(newWhereColumn);
      newWhereItem.setOperator("IN");
      SelectQueryStatement newSQS = new SelectQueryStatement();
      SelectStatement selectStmt = new SelectStatement();
      selectStmt.setSelectClause("SELECT");
      Vector columnList = new Vector();
      columnList.addElement(newWhereColumn);
      selectStmt.setSelectItemList(columnList);
      newSQS.setSelectStatement(selectStmt);
      newSQS.setFromClause(this);
      newSQS.setWhereExpression(whereExpression);
      if (database == 1) {
         newWhereItem.setRightWhereSubQuery(newSQS.toOracle());
      } else if (database == 2) {
         newWhereItem.setRightWhereSubQuery(newSQS.toMSSQLServer());
      } else if (database == 7) {
         newWhereItem.setRightWhereSubQuery(newSQS.toSybase());
      } else if (database == 3) {
         newWhereItem.setRightWhereSubQuery(newSQS.toDB2());
      } else if (database == 4) {
         newWhereItem.setRightWhereSubQuery(newSQS.toPostgreSQL());
      } else if (database == 5) {
         newWhereItem.setRightWhereSubQuery(newSQS.toMySQL());
      } else if (database == 8) {
         newWhereItem.setRightWhereSubQuery(newSQS.toANSI());
      } else if (database == 6) {
         newWhereItem.setRightWhereSubQuery(newSQS.toInformix());
      } else if (database == 11) {
         newWhereItem.setRightWhereSubQuery(newSQS.toNetezza());
      } else if (database == 13) {
         newWhereItem.setRightWhereSubQuery(newSQS.toVectorWise());
      }

      Vector dum = new Vector();
      dum.addElement(newWhereItem);
      newWhereExpression.setWhereItem(dum);
      uqs.setWhereClause(newWhereExpression);
      uqs.setFromClause((FromClause)null);
   }

   public void addFromItem(FromTable ft) {
      this.fromItemList.addElement(ft);
   }

   public void processTheFromTableInsideTheFromItemList(Vector originalFromItemList, Vector fromItems, TableObject tableObject, String tableName, String orgTableName, String alias, UpdateQueryStatement uqs, WhereExpression whereExpression) {
      for(int i = 0; i < fromItems.size(); ++i) {
         if (!(fromItems.get(i) instanceof FromTable)) {
            if (fromItems.get(i) instanceof FromClause) {
               Vector newFromItems = ((FromClause)fromItems.get(i)).getFromItemList();
               this.processTheFromTableInsideTheFromItemList(originalFromItemList, newFromItems, tableObject, tableName, orgTableName, alias, uqs, whereExpression);
            }
         } else {
            FromTable ft = (FromTable)fromItems.get(i);
            Object tableObjectInFrom;
            String tableNameInFrom;
            if (ft.getTableName() != null && ft.getAliasName() != null) {
               tableObjectInFrom = ft.getTableName();
               if (tableObjectInFrom instanceof String) {
                  tableNameInFrom = tableObject.toString();
                  if (tableNameInFrom.indexOf(46) != -1) {
                     tableNameInFrom = tableNameInFrom.substring(tableNameInFrom.indexOf(46));
                     if (tableNameInFrom.indexOf(46) != -1) {
                        tableNameInFrom = tableNameInFrom.substring(tableNameInFrom.indexOf(46));
                     }
                  }

                  if (tableNameInFrom.trim().equalsIgnoreCase(tableName)) {
                     orgTableName = tableName;
                     tableName = ft.getAliasName();
                     alias = ft.getAliasName();
                  }
               }
            } else if (ft.getTableName() != null) {
               tableObjectInFrom = ft.getTableName();
               if (tableObjectInFrom instanceof String) {
                  tableNameInFrom = tableObjectInFrom.toString();
                  if (tableNameInFrom.indexOf(46) != -1) {
                     tableNameInFrom = tableNameInFrom.substring(tableNameInFrom.indexOf(46));
                     if (tableNameInFrom.indexOf(46) != -1) {
                        tableNameInFrom = tableNameInFrom.substring(tableNameInFrom.indexOf(46));
                     }
                  }

                  if (tableNameInFrom.trim().equalsIgnoreCase(tableName) && fromItems.size() == 1) {
                     uqs.setFromClause((FromClause)null);
                     return;
                  }

                  if (tableNameInFrom.trim().equalsIgnoreCase(tableName)) {
                     if (ft != null && ft.getJoinExpression() != null) {
                        for(int j = 0; j < ft.getJoinExpression().size(); ++j) {
                           WhereExpression whereExp = (WhereExpression)ft.getJoinExpression().get(j);
                           if (whereExp.getOperator() == null || whereExp.getOperator() != null && whereExp.getOperator().size() < 1) {
                              whereExpression.addOperator("AND");
                           } else {
                              for(int k = 0; k < whereExp.getOperator().size(); ++k) {
                                 whereExpression.addOperator((String)whereExp.getOperator().get(k));
                              }
                           }

                           whereExpression.addWhereExpression((WhereExpression)ft.getJoinExpression().get(j));
                        }
                     }

                     fromItems.remove(i);
                     alias = tableName.toUpperCase();
                  }
               }
            }
         }
      }

   }

   public void convertToSubQuery(DeleteQueryStatement dqs, int database, FromClause fc) throws ConvertException, ParseException {
      TableExpression tblExp = dqs.getTableExpression();
      TableClause tc = (TableClause)tblExp.getTableClauseList().get(0);
      TableObject tableObject = tc.getTableObject();
      String tableName = tableObject.getTableName();
      String orgTableName = null;
      String alias = tableName.toUpperCase();
      String columnName = new String();
      if (fc != null && fc.getFromItemList() != null) {
         Vector fromItems = fc.getFromItemList();

         for(int i = 0; i < fromItems.size(); ++i) {
            if (fromItems.get(i) instanceof FromTable) {
               FromTable ft = (FromTable)fromItems.get(i);
               Object tableObjectInFrom;
               String tableNameInFrom;
               if (ft.getTableName() != null && ft.getAliasName() != null) {
                  tableObjectInFrom = ft.getTableName();
                  if (tableObjectInFrom instanceof String) {
                     tableNameInFrom = tableObject.toString();
                     if (tableNameInFrom.indexOf(46) != -1) {
                        tableNameInFrom = tableNameInFrom.substring(tableNameInFrom.indexOf(46));
                        if (tableNameInFrom.indexOf(46) != -1) {
                           tableNameInFrom = tableNameInFrom.substring(tableNameInFrom.indexOf(46) + 1);
                        }
                     }

                     if (tableNameInFrom.trim().equalsIgnoreCase(tableName)) {
                        orgTableName = tableName;
                        tableName = ft.getAliasName();
                        alias = ft.getAliasName();
                     }
                  }
               } else if (ft.getTableName() != null) {
                  tableObjectInFrom = ft.getTableName();
                  if (tableObjectInFrom instanceof String) {
                     tableNameInFrom = tableObjectInFrom.toString();
                     if (tableNameInFrom.indexOf(46) != -1) {
                        tableNameInFrom = tableNameInFrom.substring(tableNameInFrom.indexOf(46));
                        if (tableNameInFrom.indexOf(46) != -1) {
                           tableNameInFrom = tableNameInFrom.substring(tableNameInFrom.indexOf(46));
                        }
                     }

                     if (tableNameInFrom.trim().equalsIgnoreCase(tableName) && fromItems.size() == 1) {
                        dqs.setFromClause((FromClause)null);
                        return;
                     }

                     if (tableNameInFrom.trim().equalsIgnoreCase("DUAL") && fromItems.size() > 1) {
                        fromItems.remove(i);
                     } else if (tableNameInFrom.trim().equalsIgnoreCase(tableName)) {
                     }
                  }
               }
            } else if (fromItems.get(i) instanceof FromClause) {
               FromClause newFC = (FromClause)fromItems.get(i);
               newFC.convertToSubQuery(dqs, database, newFC);
            }
         }
      }

      WhereExpression whereExpression = dqs.getWhereExpression();
      this.changeWhereItem(whereExpression, tableName, columnName, orgTableName);
      Vector fromItemVector = this.getFromItemList();
      String columnNameString = new String();
      if (this.updateColumnName == null && fromItemVector != null) {
         for(int i = 0; i < fromItemVector.size(); ++i) {
            if (fromItemVector.elementAt(i) instanceof FromTable) {
               FromTable fromTableClause = (FromTable)fromItemVector.get(i);
               if (fromTableClause.getJoinExpression() != null) {
                  Vector joinExpVector = fromTableClause.getJoinExpression();

                  for(int count = 0; count < joinExpVector.size(); ++count) {
                     if (joinExpVector.elementAt(count) instanceof WhereExpression) {
                        WhereExpression whereExp = (WhereExpression)joinExpVector.elementAt(count);
                        Vector whereItemsVector = whereExp.getWhereItems();
                        boolean columnAdded = false;

                        for(int index = 0; index < whereItemsVector.size(); ++index) {
                           WhereItem whereItem = null;
                           if (whereItemsVector.elementAt(index) instanceof WhereItem) {
                              whereItem = (WhereItem)whereItemsVector.elementAt(index);
                              WhereColumn wc = whereItem.getLeftWhereExp();
                              Vector colExpression = wc.getColumnExpression();
                              TableColumn tblCol;
                              Object obj;
                              if (colExpression != null) {
                                 obj = colExpression.elementAt(0);
                                 if (obj instanceof TableColumn) {
                                    tblCol = (TableColumn)obj;
                                    if (tableName.equalsIgnoreCase(tblCol.getTableName())) {
                                       tblCol.setTableName(alias);
                                       columnNameString = tblCol.getColumnName();
                                       if (!columnAdded) {
                                          this.updateColumnName = columnNameString;
                                          columnAdded = true;
                                       }
                                    }
                                 }
                              }

                              Object obj = whereItem.getRightWhereExp();
                              if (obj instanceof WhereColumn) {
                                 wc = (WhereColumn)obj;
                                 Vector colExp = wc.getColumnExpression();
                                 if (colExp != null && colExp.size() != 0) {
                                    obj = colExp.elementAt(0);
                                    if (obj instanceof TableColumn) {
                                       tblCol = (TableColumn)obj;
                                       if (tableName.equalsIgnoreCase(tblCol.getTableName())) {
                                          tblCol.setTableName(alias);
                                          columnNameString = tblCol.getColumnName();
                                          if (!columnAdded) {
                                             this.updateColumnName = columnNameString;
                                             columnAdded = true;
                                          }
                                       } else {
                                          columnNameString = tblCol.getColumnName();
                                          if (!columnAdded) {
                                             this.updateColumnName = columnNameString;
                                             columnAdded = true;
                                          }
                                       }
                                    }
                                 }
                              }
                           } else if (whereItemsVector.elementAt(index) instanceof WhereExpression) {
                              this.changeWhereItem((WhereExpression)whereItemsVector.elementAt(index), tableName, columnNameString, orgTableName);
                           }
                        }

                        if (this.updateColumnName == null && columnAdded) {
                           this.updateColumnName = columnNameString;
                        }
                     }
                  }
               }
            }
         }
      }

      WhereExpression newWhereExpression = new WhereExpression();
      WhereItem newWhereItem = new WhereItem();
      WhereColumn newWhereColumn = new WhereColumn();
      Vector colExp = new Vector();
      TableColumn newTableColumn = new TableColumn();
      newTableColumn.setColumnName(this.updateColumnName);
      if (orgTableName != null) {
         newTableColumn.setTableName(orgTableName);
      } else {
         newTableColumn.setTableName(tableName);
      }

      if (this.updateColumnName == null) {
         doNotAddDotInSubquery = true;
      }

      colExp.addElement(newTableColumn);
      newWhereColumn.setColumnExpression(colExp);
      newWhereItem.setLeftWhereExp(newWhereColumn);
      newWhereItem.setOperator("IN");
      SelectQueryStatement newSQS = new SelectQueryStatement();
      SelectStatement selectStmt = new SelectStatement();
      selectStmt.setSelectClause("SELECT");
      Vector columnList = new Vector();
      columnList.addElement(newWhereColumn);
      selectStmt.setSelectItemList(columnList);
      newSQS.setSelectStatement(selectStmt);
      newSQS.setFromClause(this);
      newSQS.setWhereExpression(whereExpression);
      SwisSQLAPI tempAPI = new SwisSQLAPI();
      tempAPI.setSQLString(newSQS.toString());
      if (database == 1) {
         tempAPI.setSQLString(newSQS.toString());
         tempAPI.setSQLString(tempAPI.convert(1));
         newWhereItem.setRightWhereSubQuery((SelectQueryStatement)tempAPI.parse());
      } else if (database == 2) {
         tempAPI.setSQLString(newSQS.toString());
         tempAPI.setSQLString(tempAPI.convert(2));
         newWhereItem.setRightWhereSubQuery((SelectQueryStatement)tempAPI.parse());
      } else if (database == 7) {
         tempAPI.setSQLString(newSQS.toString());
         tempAPI.setSQLString(tempAPI.convert(7));
         newWhereItem.setRightWhereSubQuery((SelectQueryStatement)tempAPI.parse());
      } else if (database == 3) {
         tempAPI.setSQLString(newSQS.toString());
         tempAPI.setSQLString(tempAPI.convert(3));
         newWhereItem.setRightWhereSubQuery((SelectQueryStatement)tempAPI.parse());
      } else if (database == 4) {
         tempAPI.setSQLString(newSQS.toString());
         tempAPI.setSQLString(tempAPI.convert(4));
         newWhereItem.setRightWhereSubQuery((SelectQueryStatement)tempAPI.parse());
      } else if (database == 5) {
         tempAPI.setSQLString(newSQS.toString());
         tempAPI.setSQLString(tempAPI.convert(5));
         newWhereItem.setRightWhereSubQuery((SelectQueryStatement)tempAPI.parse());
      } else if (database == 8) {
         tempAPI.setSQLString(newSQS.toString());
         tempAPI.setSQLString(tempAPI.convert(8));
         newWhereItem.setRightWhereSubQuery((SelectQueryStatement)tempAPI.parse());
      } else if (database == 6) {
         tempAPI.setSQLString(newSQS.toString());
         tempAPI.setSQLString(tempAPI.convert(6));
         newWhereItem.setRightWhereSubQuery((SelectQueryStatement)tempAPI.parse());
      } else if (database == 11) {
         tempAPI.setSQLString(newSQS.toString());
         tempAPI.setSQLString(tempAPI.convert(11));
         newWhereItem.setRightWhereSubQuery((SelectQueryStatement)tempAPI.parse());
      } else if (database == 13) {
         tempAPI.setSQLString(newSQS.toString());
         tempAPI.setSQLString(tempAPI.convert(13));
         newWhereItem.setRightWhereSubQuery((SelectQueryStatement)tempAPI.parse());
      }

      doNotAddDotInSubquery = false;
      Vector dum = new Vector();
      dum.addElement(newWhereItem);
      newWhereExpression.setWhereItem(dum);
      dqs.setWhereClause(newWhereExpression);
      dqs.setFromClause((FromClause)null);
   }

   public Vector changeOrderForOuter(Vector containsFromItems) {
      Vector ChangedVectorOrder = new Vector();
      boolean checkForOuter = false;
      int countOuter = 0;

      for(int i = 0; i < containsFromItems.size(); ++i) {
         if (containsFromItems.elementAt(i) instanceof FromTable) {
            if (((FromTable)containsFromItems.get(i)).getOuter() == null && !checkForOuter) {
               if (i < ChangedVectorOrder.size()) {
                  ChangedVectorOrder.insertElementAt(containsFromItems.get(i), i - countOuter);
               } else {
                  ChangedVectorOrder.add(containsFromItems.get(i));
               }
            } else {
               if (((FromTable)containsFromItems.get(i)).getOuterOpenBrace() != null) {
                  checkForOuter = true;
               }

               ChangedVectorOrder.insertElementAt(containsFromItems.get(i), i);
               ++countOuter;
               if (((FromTable)containsFromItems.get(i)).getOuterClosedBrace() != null) {
                  checkForOuter = false;
               }
            }
         } else if (containsFromItems.elementAt(i) instanceof FromClause) {
            FromClause newFC = (FromClause)containsFromItems.get(i);
            Vector fromClauseVector = newFC.getFromItemList();
            newFC.changeOrderForOuter(fromClauseVector);
         }
      }

      return ChangedVectorOrder;
   }

   public Vector getOuterFromTableNames(Vector containsFromItems) {
      Vector outerFromItemNamesList = null;
      boolean ifInOuterJoin = false;
      if (this.fromItemList != null) {
         outerFromItemNamesList = new Vector();

         for(int i = 0; i < this.fromItemList.size(); ++i) {
            if (this.fromItemList.elementAt(i) instanceof FromTable) {
               FromTable newFromTable = (FromTable)this.fromItemList.get(i);
               if (newFromTable.getOuter() != null || ifInOuterJoin) {
                  if (newFromTable.getOuterOpenBrace() != null) {
                     ifInOuterJoin = true;
                  }

                  if (newFromTable.getAliasName() != null) {
                     outerFromItemNamesList.add(newFromTable.getAliasName());
                  } else {
                     outerFromItemNamesList.add(newFromTable.getTableName().toString());
                  }

                  if (newFromTable.getOuterClosedBrace() != null) {
                     ifInOuterJoin = false;
                  }
               }
            } else if (this.fromItemList.elementAt(i) instanceof FromClause) {
               FromClause newFC = (FromClause)this.fromItemList.get(i);
               Vector newFromItemList = newFC.getFromItemList();
               newFC.getOuterFromTableNames(newFromItemList);
            }
         }
      }

      return outerFromItemNamesList;
   }

   public FromClause copyObjectValues() {
      FromClause fc = new FromClause();
      fc.setFromClause(this.getFromClause());
      Vector vNew = new Vector();

      for(int i = 0; i < this.fromItemList.size(); ++i) {
         vNew.addElement(this.fromItemList.elementAt(i));
      }

      fc.setFromItemList(vNew);
      fc.setOpenBraces(this.openBraces);
      fc.setClosedBraces(this.closedBraces);
      fc.baseFromClauseFound = this.baseFromClauseFound;
      fc.updateColumnName = this.updateColumnName;
      fc.setFetchClauseFromSQS(this.fetchClauseFromSQS);
      fc.setObjectContext(this.context);
      return fc;
   }

   public FromClause toVectorWiseSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      FromClause fc = new FromClause();
      fc.setFromClause(this.fromClause);
      Vector v_fil = new Vector();

      for(int i = 0; i < this.fromItemList.size(); ++i) {
         if (this.fromItemList.elementAt(i) instanceof FromTable) {
            v_fil.addElement(((FromTable)this.fromItemList.elementAt(i)).toVectorWiseSelect(to_sqs, from_sqs));
         } else if (this.fromItemList.elementAt(i) instanceof FromClause) {
            v_fil.addElement(((FromClause)this.fromItemList.elementAt(i)).toVectorWiseSelect(to_sqs, from_sqs));
         }
      }

      fc.setFromItemList(v_fil);
      if (v_fil.size() == 1) {
         FromTable ft = null;
         if (v_fil.firstElement() instanceof FromTable) {
            ft = (FromTable)v_fil.elementAt(0);
         } else {
            ft = this.getFirstElement();
         }

         if (ft != null && ft.getTableName() instanceof String && (((String)ft.getTableName()).equalsIgnoreCase("DUAL") || ((String)ft.getTableName()).equalsIgnoreCase("SYS.DUAL") || ((String)ft.getTableName()).equalsIgnoreCase("SYSIBM.SYSDUMMY1"))) {
            fc = null;
         }
      }

      return fc;
   }

   public FromClause toReplaceTblCol(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      FromClause fromClConv = new FromClause();
      if (this.fromClause != null) {
         fromClConv.setFromClause(this.fromClause);
      }

      Vector vcConv = new Vector();

      for(int i = 0; i < this.fromItemList.size(); ++i) {
         if (this.fromItemList.elementAt(i) instanceof FromTable) {
            vcConv.addElement(((FromTable)this.fromItemList.elementAt(i)).toReplaceTblCol(to_sqs, from_sqs));
         } else if (this.fromItemList.elementAt(i) instanceof FromClause) {
            vcConv.addElement(((FromClause)this.fromItemList.elementAt(i)).toReplaceTblCol(to_sqs, from_sqs));
         }
      }

      fromClConv.setFromItemList(vcConv);
      if (this.openBraces != null) {
         fromClConv.setOpenBraces(this.openBraces);
      }

      if (this.closedBraces != null) {
         fromClConv.setClosedBraces(this.closedBraces);
      }

      if (this.commentObj != null) {
         fromClConv.setCommentClass(this.commentObj);
      }

      if (this.commentObjAfterToken != null) {
         fromClConv.setCommentClassAfterToken(this.commentObjAfterToken);
      }

      if (this.aliasName != null) {
         fromClConv.setAliasName(this.aliasName);
      }

      return fromClConv;
   }
}
