package com.adventnet.swissqlapi.sql.statement.create;

import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.ModifiedObjectAttr;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.util.misc.CustomizeUtil;
import java.util.StringTokenizer;

public class ConstraintClause {
   ConstraintType constraintType;
   private String constraint;
   private String constraintName;
   private String autoIncrement;
   private String columnName;
   private NotNull notNull;
   private String notNullStr;
   private String tableNameFromCQS;
   private String columnNameForSequence;
   private boolean commentForConstraintName = false;
   private String characterLengthForComment;
   private UserObjectContext context = null;
   private String triggerForIdentity;

   public void setObjectContext(UserObjectContext context) {
      this.context = context;
   }

   public void setConstraintName(String constraintName) {
      this.constraintName = constraintName;
   }

   public void setConstraint(String constraint) {
      this.constraint = constraint;
   }

   public void setNotNull(NotNull notNull) {
      this.notNull = notNull;
   }

   public void setConstraintType(ConstraintType constraintType) {
      this.constraintType = constraintType;
   }

   public void setAutoIncrement(String autoIncrement) {
      this.autoIncrement = autoIncrement;
   }

   public void setColumnName(String columnName) {
      this.columnName = columnName;
   }

   public void setTableNameFromCQS(String tableNameFromCQS) {
      this.tableNameFromCQS = tableNameFromCQS;
   }

   public void setColumnNameForSequence(String columnNameForSequence) {
      this.columnNameForSequence = columnNameForSequence;
   }

   public void setCommentForConstraintName(boolean commentForConstraintName) {
      this.commentForConstraintName = commentForConstraintName;
   }

   public void setCharacterLengthForComment(String characterLengthForComment) {
      this.characterLengthForComment = characterLengthForComment;
   }

   public String getColumnName() {
      return this.columnName;
   }

   public NotNull getNotNull() {
      return this.notNull;
   }

   public String getConstraint() {
      return this.constraint;
   }

   public String getConstraintName() {
      return this.constraintName;
   }

   public ConstraintType getConstraintType() {
      return this.constraintType;
   }

   public String getAutoIncrement() {
      return this.autoIncrement;
   }

   public String getTriggerForIdentity() {
      return this.triggerForIdentity;
   }

   public void toDB2String() throws ConvertException {
      this.setAutoIncrement((String)null);
      if (this.constraintName != null && (this.constraintName.startsWith("[") && this.constraintName.endsWith("]") || this.constraintName.startsWith("`") && this.constraintName.endsWith("`"))) {
         this.constraintName = this.constraintName.substring(1, this.constraintName.length() - 1);
         if (this.constraintName.indexOf(32) != -1) {
            this.constraintName = "\"" + this.constraintName + "\"";
         }
      }

      if (this.constraintName != null) {
         if (this.constraintName.startsWith("1") || this.constraintName.startsWith("2") || this.constraintName.startsWith("3") || this.constraintName.startsWith("4") || this.constraintName.startsWith("5") || this.constraintName.startsWith("6") || this.constraintName.startsWith("7") || this.constraintName.startsWith("8") || this.constraintName.startsWith("9") || this.constraintName.startsWith("0") || this.constraintName.startsWith("-") || this.constraintName.startsWith(".")) {
            if (this.constraintName.length() < 9) {
               this.constraintName = "CONS_NAME" + this.constraintName;
            } else if (this.constraintName.length() > 9 && this.constraintName.length() < 14) {
               this.constraintName = "CONS" + this.constraintName;
            } else {
               this.constraintName = "CON" + this.constraintName;
            }
         }

         this.setConstraintName(this.constraintName);
      }

      if (SwisSQLAPI.truncateTableNameForDB2) {
         if (this.constraintName != null && this.constraintName.length() > 18) {
            if (SwisSQLAPI.truncateConstraintCount > 99) {
               SwisSQLAPI.truncateConstraintCount = 0;
            }

            this.constraintName = this.constraintName.substring(0, 12) + "_ADV" + SwisSQLAPI.truncateConstraintCount;
            ++SwisSQLAPI.truncateConstraintCount;
            CreateQueryStatement.commentWhenConstraintNameTruncated = " -- SwisSQL Message : Manual Intervention required. The constraint name changed as the length was greater than 18 characters ; ";
         }

         this.setConstraintName(this.constraintName);
      }

      if (this.getConstraintType() != null) {
         ConstraintType toDB2ConstraintType = this.getConstraintType();
         if (toDB2ConstraintType instanceof PrimaryOrUniqueConstraintClause) {
            PrimaryOrUniqueConstraintClause primaryOrUniqueConstraintClause = (PrimaryOrUniqueConstraintClause)toDB2ConstraintType;
            primaryOrUniqueConstraintClause.setColumnName(this.getColumnName());
         } else if (toDB2ConstraintType instanceof ForeignConstraintClause) {
            ForeignConstraintClause foreignConstraintClause = (ForeignConstraintClause)toDB2ConstraintType;
            foreignConstraintClause.setColumnName(this.getColumnName());
         } else if (toDB2ConstraintType instanceof DefaultConstraintClause && this.getColumnName() != null) {
            this.setConstraint((String)null);
            this.setConstraintName((String)null);
            this.setCommentForConstraintName(false);
         }

         toDB2ConstraintType.toDB2String();
      } else if (this.notNull != null) {
         this.notNullStr = "";
         if (this.notNull.getIdentity() != null) {
            String tempIdentity = this.notNull.getIdentity();
            StringBuffer temp_SB = new StringBuffer();
            StringTokenizer st = new StringTokenizer(tempIdentity, ",");
            String token1 = st.nextToken();
            temp_SB.append("IDENTITY(START");
            temp_SB.append(" WITH");
            StringTokenizer stBrace = new StringTokenizer(token1, "(");
            if (stBrace.countTokens() > 1) {
               stBrace.nextToken();
               temp_SB.append(" " + stBrace.nextToken());
            } else {
               temp_SB.append(" 1");
            }

            if (st.countTokens() > 0) {
               String token2 = st.nextToken();
               temp_SB.append(" INCREMENT BY");
               temp_SB.append(" " + token2);
            } else {
               temp_SB.append(" INCREMENT BY");
               temp_SB.append(" 1");
            }

            this.notNullStr = temp_SB.toString() + " ";
         }

         if (this.notNull.getNullStatus() != null && !this.notNull.getNullStatus().trim().equalsIgnoreCase("NULL")) {
            this.notNullStr = this.notNullStr + this.notNull.getNullStatus();
         }
      }

   }

   public void toMSSQLServerString() throws ConvertException {
      if (this.constraintName != null) {
         if (this.constraintName.startsWith("`") && this.constraintName.endsWith("`")) {
            this.constraintName = this.constraintName.substring(1, this.constraintName.length() - 1);
            if (this.constraintName.indexOf(32) != -1) {
               this.constraintName = "\"" + this.constraintName + "\"";
            }
         }

         if (this.constraintName.startsWith("1") || this.constraintName.startsWith("2") || this.constraintName.startsWith("3") || this.constraintName.startsWith("4") || this.constraintName.startsWith("5") || this.constraintName.startsWith("6") || this.constraintName.startsWith("7") || this.constraintName.startsWith("8") || this.constraintName.startsWith("9") || this.constraintName.startsWith("0") || this.constraintName.startsWith("-") || this.constraintName.startsWith(".")) {
            if (this.constraintName.length() < 22) {
               this.constraintName = "CONS_NAME" + this.constraintName;
            } else if (this.constraintName.length() > 22 && this.constraintName.length() < 26) {
               this.constraintName = "CONS" + this.constraintName;
            } else {
               this.constraintName = "CON" + this.constraintName;
            }
         }

         this.setConstraintName(this.constraintName);
      }

      if (this.getConstraintType() != null) {
         ConstraintType toSQLServerConstraintType = this.getConstraintType();
         if (toSQLServerConstraintType instanceof DefaultConstraintClause) {
            DefaultConstraintClause var2 = (DefaultConstraintClause)toSQLServerConstraintType;
         }

         toSQLServerConstraintType.toMSSQLServerString();
      } else if (this.notNull != null) {
         this.notNullStr = "";
         if (this.notNull.getIncrement() == null) {
            String strTemp = this.notNull.getIdentity();
            if (strTemp != null && strTemp.indexOf(",") == -1 && strTemp.indexOf(")") != -1) {
               strTemp = strTemp.substring(0, strTemp.indexOf(")"));
               strTemp = strTemp + ", 1)";
               this.notNull.setIdentity(strTemp);
            }
         }

         if (this.notNull.getIdentity() != null) {
            this.notNullStr = this.notNullStr + this.notNull.getIdentity() + " ";
         }

         if (this.notNull.getNullStatus() != null) {
            this.notNullStr = this.notNullStr + this.notNull.getNullStatus();
         }
      }

   }

   public void toSybaseString() throws ConvertException {
      if (this.constraintName != null) {
         if (this.constraintName.startsWith("`") && this.constraintName.endsWith("`")) {
            this.constraintName = this.constraintName.substring(1, this.constraintName.length() - 1);
            if (this.constraintName.indexOf(32) != -1) {
               this.constraintName = "\"" + this.constraintName + "\"";
            }
         }

         this.setConstraintName(this.constraintName);
      }

      if (this.constraintName != null) {
         if (this.constraintName.startsWith("1") || this.constraintName.startsWith("2") || this.constraintName.startsWith("3") || this.constraintName.startsWith("4") || this.constraintName.startsWith("5") || this.constraintName.startsWith("6") || this.constraintName.startsWith("7") || this.constraintName.startsWith("8") || this.constraintName.startsWith("9") || this.constraintName.startsWith("0") || this.constraintName.startsWith("-") || this.constraintName.startsWith(".")) {
            if (this.constraintName.length() < 9) {
               this.constraintName = "CONS_NAME" + this.constraintName;
            } else if (this.constraintName.length() > 9 && this.constraintName.length() < 14) {
               this.constraintName = "CONS" + this.constraintName;
            } else {
               this.constraintName = "CON" + this.constraintName;
            }
         }

         this.setConstraintName(this.constraintName);
      }

      if (this.getConstraintType() != null) {
         ConstraintType toSybaseConstraintType = this.getConstraintType();
         if (toSybaseConstraintType instanceof DefaultConstraintClause) {
            DefaultConstraintClause defaultConstraintClause = (DefaultConstraintClause)toSybaseConstraintType;
            if (this.getColumnName() != null) {
               this.setConstraint((String)null);
               this.setConstraintName((String)null);
            }
         }

         toSybaseConstraintType.toSybaseString();
      } else if (this.notNull != null) {
         this.notNullStr = "";
         if (this.notNull.getIdentity() != null) {
            this.notNullStr = "IDENTITY ";
         }

         if (this.notNull.getNullStatus() != null) {
            this.notNullStr = this.notNullStr + this.notNull.getNullStatus();
         }
      }

   }

   public void toOracleString() throws ConvertException {
      this.setAutoIncrement((String)null);
      if (this.constraintName != null) {
         if (this.constraintName.startsWith("[") && this.constraintName.endsWith("]") || this.constraintName.startsWith("`") && this.constraintName.endsWith("`")) {
            this.constraintName = this.constraintName.substring(1, this.constraintName.length() - 1);
            if (SwisSQLOptions.retainQuotedIdentifierForOracle || this.constraintName.indexOf(32) != -1) {
               this.constraintName = "\"" + this.constraintName + "\"";
            }
         }

         this.constraintName = CustomizeUtil.objectNamesToQuotedIdentifier(this.constraintName, SwisSQLUtils.getKeywords(1), (ModifiedObjectAttr)null, 1);
         this.setConstraintName(this.constraintName);
      }

      if (this.constraintName != null && this.constraintName.length() > 30) {
         if (SwisSQLAPI.truncateConstraintCount > 99) {
            SwisSQLAPI.truncateConstraintCount = 0;
         }

         this.constraintName = this.constraintName.substring(0, 24) + "_ADV" + SwisSQLAPI.truncateConstraintCount;
         ++SwisSQLAPI.truncateConstraintCount;
         CreateQueryStatement.commentWhenConstraintNameTruncated = "/* SwisSQL Message : Manual Intervention required. The constraint name changed as the length was greater than 30 characters. */ ";
         this.setConstraintName(this.constraintName);
      }

      if (this.constraintName != null) {
         if (this.constraintName.startsWith("1") || this.constraintName.startsWith("2") || this.constraintName.startsWith("3") || this.constraintName.startsWith("4") || this.constraintName.startsWith("5") || this.constraintName.startsWith("6") || this.constraintName.startsWith("7") || this.constraintName.startsWith("8") || this.constraintName.startsWith("9") || this.constraintName.startsWith("0") || this.constraintName.startsWith("-") || this.constraintName.startsWith(".") || this.constraintName.startsWith("$")) {
            if (this.constraintName.length() < 22) {
               this.constraintName = "CONS_NAME" + this.constraintName;
            } else if (this.constraintName.length() > 22 && this.constraintName.length() < 26) {
               this.constraintName = "CONS" + this.constraintName;
            } else {
               this.constraintName = "CON" + this.constraintName;
            }
         }

         this.setConstraintName(this.constraintName);
      }

      if (this.getConstraintType() != null) {
         ConstraintType toOracleConstraintType = this.getConstraintType();
         if (toOracleConstraintType instanceof ForeignConstraintClause) {
            ForeignConstraintClause foreignConstraintClause = (ForeignConstraintClause)toOracleConstraintType;
            foreignConstraintClause.setColumnName(this.getColumnName());
            foreignConstraintClause.setTableNameFromCQS(this.tableNameFromCQS);
         } else if (toOracleConstraintType instanceof PrimaryOrUniqueConstraintClause) {
            ((PrimaryOrUniqueConstraintClause)toOracleConstraintType).setTableNameFromCQS(this.tableNameFromCQS);
         } else if (toOracleConstraintType instanceof DefaultConstraintClause && this.getColumnName() != null) {
            this.setConstraint((String)null);
            this.setConstraintName((String)null);
            this.setCommentForConstraintName(false);
         }

         toOracleConstraintType.toOracleString();
      } else if (this.notNull != null) {
         this.notNullStr = "";
         if (this.notNull.getIdentity() != null) {
            if (this.columnNameForSequence == null) {
               this.notNullStr = this.notNullStr + this.notNull.getIdentity() + " ";
            } else {
               String identity = this.notNull.getIdentity();
               CreateSequenceStatement createSequenceObj = new CreateSequenceStatement();
               TableObject tableObj = new TableObject();
               createSequenceObj.setSequence("SEQUENCE");
               String oracleColumnName = this.columnNameForSequence;
               String str;
               String str1;
               String str2;
               if ((!oracleColumnName.startsWith("[") || !oracleColumnName.endsWith("]")) && (!oracleColumnName.startsWith("`") || !oracleColumnName.endsWith("`"))) {
                  if (this.tableNameFromCQS != null) {
                     str = this.tableNameFromCQS + oracleColumnName + "_SEQ";
                     str1 = this.tableNameFromCQS + oracleColumnName;
                     str2 = this.tableNameFromCQS;
                     if (str.length() > 29) {
                        if (str1.length() > 25) {
                           str = str1.substring(0, 26) + "_SEQ";
                        } else if (str2.length() > 25) {
                           str = str2.substring(0, 26) + "_SEQ";
                        }

                        tableObj.setTableName(str);
                     } else {
                        tableObj.setTableName(this.tableNameFromCQS + "_" + oracleColumnName + "_SEQ");
                     }
                  } else {
                     tableObj.setTableName(this.columnName + "_SEQ");
                  }
               } else {
                  oracleColumnName = oracleColumnName.substring(1, oracleColumnName.length() - 1);
                  if (SwisSQLOptions.retainQuotedIdentifierForOracle || oracleColumnName.indexOf(32) != -1) {
                     oracleColumnName = "\"" + oracleColumnName + "\"";
                  }

                  if (this.tableNameFromCQS != null) {
                     str = this.tableNameFromCQS + oracleColumnName.substring(1, oracleColumnName.length() - 1) + "_SEQ";
                     str1 = this.tableNameFromCQS + oracleColumnName.substring(1, oracleColumnName.length() - 1);
                     str2 = this.tableNameFromCQS;
                     if (str.length() > 29) {
                        if (str1.length() > 25) {
                           str = str1.substring(0, 26) + "_SEQ";
                        } else if (str2.length() > 25) {
                           str = str2.substring(0, 26) + "_SEQ";
                        }

                        if (str.length() > 27) {
                           tableObj.setTableName("\"" + str.substring(0, 28) + "\"");
                        } else {
                           tableObj.setTableName("\"" + str + "\"");
                        }
                     } else if (str.length() > 27) {
                        tableObj.setTableName("\"" + this.tableNameFromCQS + "_" + oracleColumnName + "_S" + "\"");
                     } else {
                        tableObj.setTableName("\"" + this.tableNameFromCQS + "_" + oracleColumnName + "_SEQ" + "\"");
                     }
                  } else {
                     tableObj.setTableName(oracleColumnName + "_SEQ");
                  }
               }

               createSequenceObj.setSchemaName(tableObj);
               if (identity.trim().equalsIgnoreCase("IDENTITY")) {
                  createSequenceObj.setStart("START");
                  createSequenceObj.setWith("WITH");
                  createSequenceObj.setStartValue("1");
                  createSequenceObj.setIncrementString("INCREMENT BY");
                  createSequenceObj.setIncrementValue("1");
               } else {
                  str = identity.trim().substring(8).trim();
                  str = str.substring(1, str.length() - 1);
                  StringTokenizer st = new StringTokenizer(str, ",");
                  str2 = st.nextToken();
                  createSequenceObj.setStart("START");
                  createSequenceObj.setWith("WITH");
                  createSequenceObj.setStartValue(str2);
                  if (st.countTokens() > 0) {
                     String token2 = st.nextToken();
                     createSequenceObj.setIncrementString("INCREMENT BY");
                     createSequenceObj.setIncrementValue(token2);
                  } else {
                     createSequenceObj.setIncrementString("INCREMENT BY");
                     createSequenceObj.setIncrementValue("1");
                  }

                  if (this.notNull.getMaxValueOrNoMaxValue() != null) {
                     if (this.notNull.getMaxValueOrNoMaxValue().equalsIgnoreCase("NO MAXVALUE")) {
                        createSequenceObj.setMaxValueOrNoMaxValue("NOMAXVALUE");
                     } else {
                        createSequenceObj.setMaxValueOrNoMaxValue(this.notNull.getMaxValueOrNoMaxValue());
                     }
                  }

                  if (this.notNull.getMinValueOrNoMinValue() != null) {
                     if (this.notNull.getMinValueOrNoMinValue().equalsIgnoreCase("NO MINVALUE")) {
                        createSequenceObj.setMinValueOrNoMinValue("NOMINVALUE");
                     } else {
                        createSequenceObj.setMinValueOrNoMinValue(this.notNull.getMinValueOrNoMinValue());
                     }
                  }

                  if (this.notNull.getCycleOrNoCycle() != null) {
                     if (this.notNull.getCycleOrNoCycle().equalsIgnoreCase("NO CYCLE")) {
                        createSequenceObj.setCycleOrNoCycle("NOCYCLE");
                     } else {
                        createSequenceObj.setCycleOrNoCycle(this.notNull.getCycleOrNoCycle());
                     }
                  }

                  if (this.notNull.getOrderOrNoOrder() != null) {
                     if (this.notNull.getOrderOrNoOrder().equalsIgnoreCase("NO ORDER")) {
                        createSequenceObj.setOrderOrNoOrder("NOORDER");
                     } else {
                        createSequenceObj.setOrderOrNoOrder(this.notNull.getOrderOrNoOrder());
                     }
                  }

                  if (this.notNull.getCacheOrNoCache() != null) {
                     if (this.notNull.getCacheOrNoCache().equalsIgnoreCase("NO CACHE")) {
                        createSequenceObj.setCacheOrNoCache("NOCACHE");
                     } else {
                        createSequenceObj.setCacheOrNoCache(this.notNull.getCacheOrNoCache());
                     }
                  }

                  if (SwisSQLOptions.generateTriggerForIdentity) {
                     StringBuffer sb = new StringBuffer();
                     sb.append("CREATE OR REPLACE TRIGGER ");
                     String triggerName = createSequenceObj.getSchemaName().getTableName();
                     if (triggerName.endsWith("_SEQ")) {
                        sb.append("TR_" + triggerName.substring(0, triggerName.lastIndexOf("_SEQ")));
                     }

                     if (triggerName.endsWith("_S")) {
                        sb.append("TR_" + triggerName.substring(0, triggerName.lastIndexOf("_S")));
                     }

                     sb.append("\nBEFORE INSERT ON ");
                     sb.append(this.tableNameFromCQS);
                     sb.append(" FOR EACH ROW");
                     sb.append("\nBEGIN\n\tSELECT ");
                     sb.append(triggerName + ".nextval");
                     sb.append(" INTO ");
                     sb.append(":new." + oracleColumnName);
                     sb.append(" FROM dual; ");
                     sb.append("\nEND;\n");
                     this.triggerForIdentity = sb.toString();
                  }
               }

               if (SelectQueryStatement.singleQueryConvertedToMultipleQueryList != null) {
                  SelectQueryStatement.singleQueryConvertedToMultipleQueryList = SelectQueryStatement.singleQueryConvertedToMultipleQueryList + "CREATE " + createSequenceObj.toString() + "\n/" + "\n\n/* SwisSQL Message : Query split into multiple Queries. Manual Intervention required */\n";
               } else {
                  SelectQueryStatement.singleQueryConvertedToMultipleQueryList = "CREATE " + createSequenceObj.toString() + "\n/" + "\n\n/* SwisSQL Message : Query split into multiple Queries. Manual Intervention required */\n";
               }

               this.notNullStr = "";
            }
         }

         if (this.notNull.getNullStatus() != null) {
            this.notNullStr = this.notNullStr + this.notNull.getNullStatus();
         }
      }

   }

   public void toBigQueryString() throws ConvertException {
      if (this.constraintName != null) {
         if (this.constraintName.startsWith("[") && this.constraintName.endsWith("]")) {
            this.constraintName = this.constraintName.substring(1, this.constraintName.length() - 1);
            if (this.constraintName.indexOf(32) != -1) {
               this.constraintName = "\"" + this.constraintName + "\"";
            }
         }

         if (this.constraintName.startsWith("1") || this.constraintName.startsWith("2") || this.constraintName.startsWith("3") || this.constraintName.startsWith("4") || this.constraintName.startsWith("5") || this.constraintName.startsWith("6") || this.constraintName.startsWith("7") || this.constraintName.startsWith("8") || this.constraintName.startsWith("9") || this.constraintName.startsWith("0") || this.constraintName.startsWith("-") || this.constraintName.startsWith(".")) {
            if (this.constraintName.length() < 22) {
               this.constraintName = "CONS_NAME" + this.constraintName;
            } else if (this.constraintName.length() > 22 && this.constraintName.length() < 26) {
               this.constraintName = "CONS" + this.constraintName;
            } else {
               this.constraintName = "CON" + this.constraintName;
            }
         }

         this.setConstraintName(this.constraintName);
      }

      this.setAutoIncrement((String)null);
      if (this.getConstraintType() != null) {
         ConstraintType toBigQueryConstraintType = this.getConstraintType();
         if (toBigQueryConstraintType instanceof PrimaryOrUniqueConstraintClause) {
            PrimaryOrUniqueConstraintClause primaryOrUniqueConstraintClause = (PrimaryOrUniqueConstraintClause)toBigQueryConstraintType;
            primaryOrUniqueConstraintClause.setColumnName(this.getColumnName());
         } else if (toBigQueryConstraintType instanceof ForeignConstraintClause) {
            ForeignConstraintClause foreignConstraintClause = (ForeignConstraintClause)toBigQueryConstraintType;
            foreignConstraintClause.setColumnName(this.getColumnName());
         } else if (toBigQueryConstraintType instanceof DefaultConstraintClause && this.getColumnName() != null) {
            this.setConstraint((String)null);
            this.setConstraintName((String)null);
         }

         toBigQueryConstraintType.toBigQueryString();
      } else if (this.notNull != null) {
         this.notNullStr = "";
         if (this.notNull.getIdentity() != null) {
            if (this.columnNameForSequence == null) {
               this.notNullStr = this.notNullStr + this.notNull.getIdentity() + " ";
            } else {
               String identity = this.notNull.getIdentity();
               CreateSequenceStatement createSequenceObj = new CreateSequenceStatement();
               TableObject tableObj = new TableObject();
               createSequenceObj.setSequence("SEQUENCE");
               String bigqueryColumnName = this.columnNameForSequence;
               String str;
               String str1;
               String str2;
               if ((!bigqueryColumnName.startsWith("[") || !bigqueryColumnName.endsWith("]")) && (!bigqueryColumnName.startsWith("`") || !bigqueryColumnName.endsWith("`"))) {
                  if (this.tableNameFromCQS != null) {
                     str = this.tableNameFromCQS + bigqueryColumnName + "_SEQ";
                     str1 = this.tableNameFromCQS + bigqueryColumnName;
                     str2 = this.tableNameFromCQS;
                     if (str.length() > 29) {
                        if (str1.length() > 25) {
                           str = str1.substring(0, 26) + "_SEQ";
                        } else if (str2.length() > 25) {
                           str = str2.substring(0, 26) + "_SEQ";
                        }

                        tableObj.setTableName(str);
                     } else {
                        tableObj.setTableName(this.tableNameFromCQS + "_" + bigqueryColumnName + "_SEQ");
                     }
                  } else {
                     tableObj.setTableName(this.columnName + "_SEQ");
                  }
               } else {
                  bigqueryColumnName = bigqueryColumnName.substring(1, bigqueryColumnName.length() - 1);
                  if (bigqueryColumnName.indexOf(32) != -1) {
                     bigqueryColumnName = "\"" + bigqueryColumnName + "\"";
                  }

                  if (this.tableNameFromCQS != null) {
                     str = this.tableNameFromCQS + bigqueryColumnName.substring(1, bigqueryColumnName.length() - 1) + "_SEQ";
                     str1 = this.tableNameFromCQS + bigqueryColumnName.substring(1, bigqueryColumnName.length() - 1);
                     str2 = this.tableNameFromCQS;
                     if (str.length() > 29) {
                        if (str1.length() > 25) {
                           str = str1.substring(0, 26) + "_SEQ";
                        } else if (str2.length() > 25) {
                           str = str2.substring(0, 26) + "_SEQ";
                        }

                        if (str.length() > 27) {
                           tableObj.setTableName("\"" + str.substring(0, 28) + "\"");
                        } else {
                           tableObj.setTableName("\"" + str + "\"");
                        }
                     } else if (str.length() > 27) {
                        tableObj.setTableName("\"" + this.tableNameFromCQS + "_" + bigqueryColumnName + "_S" + "\"");
                     } else {
                        tableObj.setTableName("\"" + this.tableNameFromCQS + "_" + bigqueryColumnName + "_SEQ" + "\"");
                     }
                  } else {
                     tableObj.setTableName(bigqueryColumnName + "_SEQ");
                  }
               }

               createSequenceObj.setSchemaName(tableObj);
               if (identity.trim().equalsIgnoreCase("IDENTITY")) {
                  createSequenceObj.setStart("START");
                  createSequenceObj.setStartValue("1");
                  createSequenceObj.setIncrementString("INCREMENT ");
                  createSequenceObj.setIncrementValue("1");
               } else {
                  str = identity.trim().substring(8).trim();
                  str = str.substring(1, str.length() - 1);
                  StringTokenizer st = new StringTokenizer(str, ",");
                  str2 = st.nextToken();
                  createSequenceObj.setStart("START");
                  createSequenceObj.setStartValue(str2);
                  if (st.countTokens() > 0) {
                     String token2 = st.nextToken();
                     createSequenceObj.setIncrementString("INCREMENT ");
                     createSequenceObj.setIncrementValue(token2);
                  } else {
                     createSequenceObj.setIncrementString("INCREMENT ");
                     createSequenceObj.setIncrementValue("1");
                  }

                  if (this.notNull.getMaxValueOrNoMaxValue() != null) {
                     if (this.notNull.getMaxValueOrNoMaxValue().equalsIgnoreCase("NO MAXVALUE")) {
                        createSequenceObj.setMaxValueOrNoMaxValue((String)null);
                     } else {
                        createSequenceObj.setMaxValueOrNoMaxValue(this.notNull.getMaxValueOrNoMaxValue());
                     }
                  }

                  if (this.notNull.getMinValueOrNoMinValue() != null) {
                     if (this.notNull.getMinValueOrNoMinValue().equalsIgnoreCase("NO MINVALUE")) {
                        createSequenceObj.setMinValueOrNoMinValue((String)null);
                     } else {
                        createSequenceObj.setMinValueOrNoMinValue(this.notNull.getMinValueOrNoMinValue());
                     }
                  }

                  if (this.notNull.getCycleOrNoCycle() != null) {
                     if (this.notNull.getCycleOrNoCycle().equalsIgnoreCase("NO CYCLE")) {
                        createSequenceObj.setCycleOrNoCycle((String)null);
                     } else {
                        createSequenceObj.setCycleOrNoCycle(this.notNull.getCycleOrNoCycle());
                     }
                  }

                  if (this.notNull.getOrderOrNoOrder() != null) {
                     createSequenceObj.setOrderOrNoOrder((String)null);
                  }

                  if (this.notNull.getCacheOrNoCache() != null) {
                     if (this.notNull.getCacheOrNoCache().equalsIgnoreCase("NO CACHE")) {
                        createSequenceObj.setCacheOrNoCache((String)null);
                     } else {
                        createSequenceObj.setCacheOrNoCache(this.notNull.getCacheOrNoCache());
                     }
                  }
               }

               if (SelectQueryStatement.singleQueryConvertedToMultipleQueryList != null) {
                  SelectQueryStatement.singleQueryConvertedToMultipleQueryList = SelectQueryStatement.singleQueryConvertedToMultipleQueryList + "CREATE " + createSequenceObj.toString() + "\n;" + "\n\n/* SwisSQL Message : Query split into multiple Queries. Manual Intervention required */\n";
               } else {
                  SelectQueryStatement.singleQueryConvertedToMultipleQueryList = "CREATE " + createSequenceObj.toString() + "\n;" + "\n\n/* SwisSQL Message : Query split into multiple Queries. Manual Intervention required */\n";
               }

               this.notNullStr = "";
            }
         }

         if (this.notNull.getNullStatus() != null) {
            this.notNullStr = this.notNullStr + this.notNull.getNullStatus();
         }
      }

   }

   public void toPostgreSQLString() throws ConvertException {
      if (this.constraintName != null) {
         if (this.constraintName.startsWith("[") && this.constraintName.endsWith("]") || this.constraintName.startsWith("`") && this.constraintName.endsWith("`")) {
            this.constraintName = this.constraintName.substring(1, this.constraintName.length() - 1);
            if (this.constraintName.indexOf(32) != -1) {
               this.constraintName = "\"" + this.constraintName + "\"";
            }
         }

         if (this.constraintName.startsWith("1") || this.constraintName.startsWith("2") || this.constraintName.startsWith("3") || this.constraintName.startsWith("4") || this.constraintName.startsWith("5") || this.constraintName.startsWith("6") || this.constraintName.startsWith("7") || this.constraintName.startsWith("8") || this.constraintName.startsWith("9") || this.constraintName.startsWith("0") || this.constraintName.startsWith("-") || this.constraintName.startsWith(".")) {
            if (this.constraintName.length() < 22) {
               this.constraintName = "CONS_NAME" + this.constraintName;
            } else if (this.constraintName.length() > 22 && this.constraintName.length() < 26) {
               this.constraintName = "CONS" + this.constraintName;
            } else {
               this.constraintName = "CON" + this.constraintName;
            }
         }

         this.setConstraintName(this.constraintName);
      }

      this.setAutoIncrement((String)null);
      if (this.getConstraintType() != null) {
         ConstraintType toPostgreSQLConstraintType = this.getConstraintType();
         if (toPostgreSQLConstraintType instanceof PrimaryOrUniqueConstraintClause) {
            PrimaryOrUniqueConstraintClause primaryOrUniqueConstraintClause = (PrimaryOrUniqueConstraintClause)toPostgreSQLConstraintType;
            primaryOrUniqueConstraintClause.setColumnName(this.getColumnName());
         } else if (toPostgreSQLConstraintType instanceof ForeignConstraintClause) {
            ForeignConstraintClause foreignConstraintClause = (ForeignConstraintClause)toPostgreSQLConstraintType;
            foreignConstraintClause.setColumnName(this.getColumnName());
         } else if (toPostgreSQLConstraintType instanceof DefaultConstraintClause && this.getColumnName() != null) {
            this.setConstraint((String)null);
            this.setConstraintName((String)null);
         }

         toPostgreSQLConstraintType.toPostgreSQLString();
      } else if (this.notNull != null) {
         this.notNullStr = "";
         if (this.notNull.getIdentity() != null) {
            if (this.columnNameForSequence == null) {
               this.notNullStr = this.notNullStr + this.notNull.getIdentity() + " ";
            } else {
               String identity = this.notNull.getIdentity();
               CreateSequenceStatement createSequenceObj = new CreateSequenceStatement();
               TableObject tableObj = new TableObject();
               createSequenceObj.setSequence("SEQUENCE");
               String postgreSQLColumnName = this.columnNameForSequence;
               String str;
               String str1;
               String str2;
               if ((!postgreSQLColumnName.startsWith("[") || !postgreSQLColumnName.endsWith("]")) && (!postgreSQLColumnName.startsWith("`") || !postgreSQLColumnName.endsWith("`"))) {
                  if (this.tableNameFromCQS != null) {
                     str = this.tableNameFromCQS + postgreSQLColumnName + "_SEQ";
                     str1 = this.tableNameFromCQS + postgreSQLColumnName;
                     str2 = this.tableNameFromCQS;
                     if (str.length() > 29) {
                        if (str1.length() > 25) {
                           str = str1.substring(0, 26) + "_SEQ";
                        } else if (str2.length() > 25) {
                           str = str2.substring(0, 26) + "_SEQ";
                        }

                        tableObj.setTableName(str);
                     } else {
                        tableObj.setTableName(this.tableNameFromCQS + "_" + postgreSQLColumnName + "_SEQ");
                     }
                  } else {
                     tableObj.setTableName(this.columnName + "_SEQ");
                  }
               } else {
                  postgreSQLColumnName = postgreSQLColumnName.substring(1, postgreSQLColumnName.length() - 1);
                  if (postgreSQLColumnName.indexOf(32) != -1) {
                     postgreSQLColumnName = "\"" + postgreSQLColumnName + "\"";
                  }

                  if (this.tableNameFromCQS != null) {
                     str = this.tableNameFromCQS + postgreSQLColumnName.substring(1, postgreSQLColumnName.length() - 1) + "_SEQ";
                     str1 = this.tableNameFromCQS + postgreSQLColumnName.substring(1, postgreSQLColumnName.length() - 1);
                     str2 = this.tableNameFromCQS;
                     if (str.length() > 29) {
                        if (str1.length() > 25) {
                           str = str1.substring(0, 26) + "_SEQ";
                        } else if (str2.length() > 25) {
                           str = str2.substring(0, 26) + "_SEQ";
                        }

                        if (str.length() > 27) {
                           tableObj.setTableName("\"" + str.substring(0, 28) + "\"");
                        } else {
                           tableObj.setTableName("\"" + str + "\"");
                        }
                     } else if (str.length() > 27) {
                        tableObj.setTableName("\"" + this.tableNameFromCQS + "_" + postgreSQLColumnName + "_S" + "\"");
                     } else {
                        tableObj.setTableName("\"" + this.tableNameFromCQS + "_" + postgreSQLColumnName + "_SEQ" + "\"");
                     }
                  } else {
                     tableObj.setTableName(postgreSQLColumnName + "_SEQ");
                  }
               }

               createSequenceObj.setSchemaName(tableObj);
               if (identity.trim().equalsIgnoreCase("IDENTITY")) {
                  createSequenceObj.setStart("START");
                  createSequenceObj.setStartValue("1");
                  createSequenceObj.setIncrementString("INCREMENT ");
                  createSequenceObj.setIncrementValue("1");
               } else {
                  str = identity.trim().substring(8).trim();
                  str = str.substring(1, str.length() - 1);
                  StringTokenizer st = new StringTokenizer(str, ",");
                  str2 = st.nextToken();
                  createSequenceObj.setStart("START");
                  createSequenceObj.setStartValue(str2);
                  if (st.countTokens() > 0) {
                     String token2 = st.nextToken();
                     createSequenceObj.setIncrementString("INCREMENT ");
                     createSequenceObj.setIncrementValue(token2);
                  } else {
                     createSequenceObj.setIncrementString("INCREMENT ");
                     createSequenceObj.setIncrementValue("1");
                  }

                  if (this.notNull.getMaxValueOrNoMaxValue() != null) {
                     if (this.notNull.getMaxValueOrNoMaxValue().equalsIgnoreCase("NO MAXVALUE")) {
                        createSequenceObj.setMaxValueOrNoMaxValue((String)null);
                     } else {
                        createSequenceObj.setMaxValueOrNoMaxValue(this.notNull.getMaxValueOrNoMaxValue());
                     }
                  }

                  if (this.notNull.getMinValueOrNoMinValue() != null) {
                     if (this.notNull.getMinValueOrNoMinValue().equalsIgnoreCase("NO MINVALUE")) {
                        createSequenceObj.setMinValueOrNoMinValue((String)null);
                     } else {
                        createSequenceObj.setMinValueOrNoMinValue(this.notNull.getMinValueOrNoMinValue());
                     }
                  }

                  if (this.notNull.getCycleOrNoCycle() != null) {
                     if (this.notNull.getCycleOrNoCycle().equalsIgnoreCase("NO CYCLE")) {
                        createSequenceObj.setCycleOrNoCycle((String)null);
                     } else {
                        createSequenceObj.setCycleOrNoCycle(this.notNull.getCycleOrNoCycle());
                     }
                  }

                  if (this.notNull.getOrderOrNoOrder() != null) {
                     createSequenceObj.setOrderOrNoOrder((String)null);
                  }

                  if (this.notNull.getCacheOrNoCache() != null) {
                     if (this.notNull.getCacheOrNoCache().equalsIgnoreCase("NO CACHE")) {
                        createSequenceObj.setCacheOrNoCache((String)null);
                     } else {
                        createSequenceObj.setCacheOrNoCache(this.notNull.getCacheOrNoCache());
                     }
                  }
               }

               if (SelectQueryStatement.singleQueryConvertedToMultipleQueryList != null) {
                  SelectQueryStatement.singleQueryConvertedToMultipleQueryList = SelectQueryStatement.singleQueryConvertedToMultipleQueryList + "CREATE " + createSequenceObj.toString() + "\n;" + "\n\n/* SwisSQL Message : Query split into multiple Queries. Manual Intervention required */\n";
               } else {
                  SelectQueryStatement.singleQueryConvertedToMultipleQueryList = "CREATE " + createSequenceObj.toString() + "\n;" + "\n\n/* SwisSQL Message : Query split into multiple Queries. Manual Intervention required */\n";
               }

               this.notNullStr = "";
            }
         }

         if (this.notNull.getNullStatus() != null) {
            this.notNullStr = this.notNullStr + this.notNull.getNullStatus();
         }
      }

   }

   public void toANSIString() throws ConvertException {
      this.setAutoIncrement((String)null);
      if (this.constraintName != null) {
         if (this.constraintName.startsWith("[") && this.constraintName.endsWith("]") || this.constraintName.startsWith("`") && this.constraintName.endsWith("`")) {
            this.constraintName = this.constraintName.substring(1, this.constraintName.length() - 1);
            if (this.constraintName.indexOf(32) != -1) {
               this.constraintName = "\"" + this.constraintName + "\"";
            }
         }

         if (this.constraintName.startsWith("1") || this.constraintName.startsWith("2") || this.constraintName.startsWith("3") || this.constraintName.startsWith("4") || this.constraintName.startsWith("5") || this.constraintName.startsWith("6") || this.constraintName.startsWith("7") || this.constraintName.startsWith("8") || this.constraintName.startsWith("9") || this.constraintName.startsWith("0") || this.constraintName.startsWith("-") || this.constraintName.startsWith(".")) {
            if (this.constraintName.length() < 22) {
               this.constraintName = "CONS_NAME" + this.constraintName;
            } else if (this.constraintName.length() > 22 && this.constraintName.length() < 26) {
               this.constraintName = "CONS" + this.constraintName;
            } else {
               this.constraintName = "CON" + this.constraintName;
            }
         }

         this.setConstraintName(this.constraintName);
      }

      if (this.getConstraintType() != null) {
         ConstraintType toANSISQLConstraintType = this.getConstraintType();
         if (toANSISQLConstraintType instanceof PrimaryOrUniqueConstraintClause) {
            PrimaryOrUniqueConstraintClause primaryOrUniqueConstraintClause = (PrimaryOrUniqueConstraintClause)toANSISQLConstraintType;
            primaryOrUniqueConstraintClause.setColumnName(this.getColumnName());
         } else if (toANSISQLConstraintType instanceof ForeignConstraintClause) {
            ForeignConstraintClause foreignConstraintClause = (ForeignConstraintClause)toANSISQLConstraintType;
            foreignConstraintClause.setColumnName(this.getColumnName());
         } else if (toANSISQLConstraintType instanceof DefaultConstraintClause && this.getColumnName() != null) {
            this.setConstraint((String)null);
            this.setConstraintName((String)null);
         }

         toANSISQLConstraintType.toANSIString();
      } else if (this.notNull != null) {
         this.notNullStr = "";
         if (this.notNull.getIdentity() != null) {
            this.notNullStr = this.notNullStr + this.notNull.getIdentity() + " ";
         }

         if (this.notNull.getNullStatus() != null) {
            this.notNullStr = this.notNullStr + this.notNull.getNullStatus();
         }
      }

   }

   public void toMySQLString() throws ConvertException {
      if (this.constraintName != null) {
         if (this.constraintName.startsWith("[") && this.constraintName.endsWith("]")) {
            this.constraintName = this.constraintName.substring(1, this.constraintName.length() - 1);
            if (this.constraintName.indexOf(32) != -1) {
               this.constraintName = "\"" + this.constraintName + "\"";
            }
         } else if (!this.constraintName.startsWith("`") && !this.constraintName.endsWith("`")) {
            this.constraintName = "`" + this.constraintName + "`";
         }

         this.setConstraintName(this.constraintName);
      }

      if (this.getConstraintType() != null) {
         ConstraintType toMySQLConstraintType = this.getConstraintType();
         if (toMySQLConstraintType instanceof PrimaryOrUniqueConstraintClause) {
            PrimaryOrUniqueConstraintClause primaryOrUniqueConstraintClause = (PrimaryOrUniqueConstraintClause)toMySQLConstraintType;
            primaryOrUniqueConstraintClause.setColumnName(this.getColumnName());
         } else if (toMySQLConstraintType instanceof ForeignConstraintClause) {
            ForeignConstraintClause foreignConstraintClause = (ForeignConstraintClause)toMySQLConstraintType;
            foreignConstraintClause.setColumnName(this.getColumnName());
            this.setConstraint((String)null);
            this.setConstraintName((String)null);
         } else if (toMySQLConstraintType instanceof DefaultConstraintClause && this.getColumnName() != null) {
            this.setConstraint((String)null);
            this.setConstraintName((String)null);
         }

         toMySQLConstraintType.toMySQLString();
      } else if (this.notNull != null) {
         this.notNullStr = "";
         if (this.notNull.getIdentity() != null) {
            this.notNullStr = this.notNullStr + this.notNull.getIdentity() + " ";
         }

         if (this.notNull.getNullStatus() != null) {
            this.notNullStr = this.notNullStr + this.notNull.getNullStatus();
         }
      }

   }

   public void toSnowflakeString() throws ConvertException {
      if (this.constraintName != null) {
         if (this.constraintName.startsWith("[") && this.constraintName.endsWith("]") || this.constraintName.startsWith("`") && this.constraintName.endsWith("`")) {
            this.constraintName = this.constraintName.substring(1, this.constraintName.length() - 1);
            if (this.constraintName.indexOf(32) != -1) {
               this.constraintName = "\"" + this.constraintName + "\"";
            }
         }

         if (this.constraintName.startsWith("1") || this.constraintName.startsWith("2") || this.constraintName.startsWith("3") || this.constraintName.startsWith("4") || this.constraintName.startsWith("5") || this.constraintName.startsWith("6") || this.constraintName.startsWith("7") || this.constraintName.startsWith("8") || this.constraintName.startsWith("9") || this.constraintName.startsWith("0") || this.constraintName.startsWith("-") || this.constraintName.startsWith(".")) {
            if (this.constraintName.length() < 22) {
               this.constraintName = "CONS_NAME" + this.constraintName;
            } else if (this.constraintName.length() > 22 && this.constraintName.length() < 26) {
               this.constraintName = "CONS" + this.constraintName;
            } else {
               this.constraintName = "CON" + this.constraintName;
            }
         }

         this.setConstraintName(this.constraintName);
      }

      this.setAutoIncrement((String)null);
      if (this.getConstraintType() != null) {
         ConstraintType toSnowflakeConstraintType = this.getConstraintType();
         if (toSnowflakeConstraintType instanceof PrimaryOrUniqueConstraintClause) {
            PrimaryOrUniqueConstraintClause primaryOrUniqueConstraintClause = (PrimaryOrUniqueConstraintClause)toSnowflakeConstraintType;
            primaryOrUniqueConstraintClause.setColumnName(this.getColumnName());
         } else if (toSnowflakeConstraintType instanceof ForeignConstraintClause) {
            ForeignConstraintClause foreignConstraintClause = (ForeignConstraintClause)toSnowflakeConstraintType;
            foreignConstraintClause.setColumnName(this.getColumnName());
         } else if (toSnowflakeConstraintType instanceof DefaultConstraintClause && this.getColumnName() != null) {
            this.setConstraint((String)null);
            this.setConstraintName((String)null);
         }

         toSnowflakeConstraintType.toSnowflakeString();
      } else if (this.notNull != null) {
         this.notNullStr = "";
         if (this.notNull.getIdentity() != null) {
            if (this.columnNameForSequence == null) {
               this.notNullStr = this.notNullStr + this.notNull.getIdentity() + " ";
            } else {
               String identity = this.notNull.getIdentity();
               CreateSequenceStatement createSequenceObj = new CreateSequenceStatement();
               TableObject tableObj = new TableObject();
               createSequenceObj.setSequence("SEQUENCE");
               String snowflakeColumnName = this.columnNameForSequence;
               String str;
               String str1;
               String str2;
               if ((!snowflakeColumnName.startsWith("[") || !snowflakeColumnName.endsWith("]")) && (!snowflakeColumnName.startsWith("`") || !snowflakeColumnName.endsWith("`"))) {
                  if (this.tableNameFromCQS != null) {
                     str = this.tableNameFromCQS + snowflakeColumnName + "_SEQ";
                     str1 = this.tableNameFromCQS + snowflakeColumnName;
                     str2 = this.tableNameFromCQS;
                     if (str.length() > 29) {
                        if (str1.length() > 25) {
                           str = str1.substring(0, 26) + "_SEQ";
                        } else if (str2.length() > 25) {
                           str = str2.substring(0, 26) + "_SEQ";
                        }

                        tableObj.setTableName(str);
                     } else {
                        tableObj.setTableName(this.tableNameFromCQS + "_" + snowflakeColumnName + "_SEQ");
                     }
                  } else {
                     tableObj.setTableName(this.columnName + "_SEQ");
                  }
               } else {
                  snowflakeColumnName = snowflakeColumnName.substring(1, snowflakeColumnName.length() - 1);
                  if (snowflakeColumnName.indexOf(32) != -1) {
                     snowflakeColumnName = "\"" + snowflakeColumnName + "\"";
                  }

                  if (this.tableNameFromCQS != null) {
                     str = this.tableNameFromCQS + snowflakeColumnName.substring(1, snowflakeColumnName.length() - 1) + "_SEQ";
                     str1 = this.tableNameFromCQS + snowflakeColumnName.substring(1, snowflakeColumnName.length() - 1);
                     str2 = this.tableNameFromCQS;
                     if (str.length() > 29) {
                        if (str1.length() > 25) {
                           str = str1.substring(0, 26) + "_SEQ";
                        } else if (str2.length() > 25) {
                           str = str2.substring(0, 26) + "_SEQ";
                        }

                        if (str.length() > 27) {
                           tableObj.setTableName("\"" + str.substring(0, 28) + "\"");
                        } else {
                           tableObj.setTableName("\"" + str + "\"");
                        }
                     } else if (str.length() > 27) {
                        tableObj.setTableName("\"" + this.tableNameFromCQS + "_" + snowflakeColumnName + "_S" + "\"");
                     } else {
                        tableObj.setTableName("\"" + this.tableNameFromCQS + "_" + snowflakeColumnName + "_SEQ" + "\"");
                     }
                  } else {
                     tableObj.setTableName(snowflakeColumnName + "_SEQ");
                  }
               }

               createSequenceObj.setSchemaName(tableObj);
               if (identity.trim().equalsIgnoreCase("IDENTITY")) {
                  createSequenceObj.setStart("START");
                  createSequenceObj.setStartValue("1");
                  createSequenceObj.setIncrementString("INCREMENT ");
                  createSequenceObj.setIncrementValue("1");
               } else {
                  str = identity.trim().substring(8).trim();
                  str = str.substring(1, str.length() - 1);
                  StringTokenizer st = new StringTokenizer(str, ",");
                  str2 = st.nextToken();
                  createSequenceObj.setStart("START");
                  createSequenceObj.setStartValue(str2);
                  if (st.countTokens() > 0) {
                     String token2 = st.nextToken();
                     createSequenceObj.setIncrementString("INCREMENT ");
                     createSequenceObj.setIncrementValue(token2);
                  } else {
                     createSequenceObj.setIncrementString("INCREMENT ");
                     createSequenceObj.setIncrementValue("1");
                  }

                  if (this.notNull.getMaxValueOrNoMaxValue() != null) {
                     if (this.notNull.getMaxValueOrNoMaxValue().equalsIgnoreCase("NO MAXVALUE")) {
                        createSequenceObj.setMaxValueOrNoMaxValue((String)null);
                     } else {
                        createSequenceObj.setMaxValueOrNoMaxValue(this.notNull.getMaxValueOrNoMaxValue());
                     }
                  }

                  if (this.notNull.getMinValueOrNoMinValue() != null) {
                     if (this.notNull.getMinValueOrNoMinValue().equalsIgnoreCase("NO MINVALUE")) {
                        createSequenceObj.setMinValueOrNoMinValue((String)null);
                     } else {
                        createSequenceObj.setMinValueOrNoMinValue(this.notNull.getMinValueOrNoMinValue());
                     }
                  }

                  if (this.notNull.getCycleOrNoCycle() != null) {
                     if (this.notNull.getCycleOrNoCycle().equalsIgnoreCase("NO CYCLE")) {
                        createSequenceObj.setCycleOrNoCycle((String)null);
                     } else {
                        createSequenceObj.setCycleOrNoCycle(this.notNull.getCycleOrNoCycle());
                     }
                  }

                  if (this.notNull.getOrderOrNoOrder() != null) {
                     createSequenceObj.setOrderOrNoOrder((String)null);
                  }

                  if (this.notNull.getCacheOrNoCache() != null) {
                     if (this.notNull.getCacheOrNoCache().equalsIgnoreCase("NO CACHE")) {
                        createSequenceObj.setCacheOrNoCache((String)null);
                     } else {
                        createSequenceObj.setCacheOrNoCache(this.notNull.getCacheOrNoCache());
                     }
                  }
               }

               if (SelectQueryStatement.singleQueryConvertedToMultipleQueryList != null) {
                  SelectQueryStatement.singleQueryConvertedToMultipleQueryList = SelectQueryStatement.singleQueryConvertedToMultipleQueryList + "CREATE " + createSequenceObj.toString() + "\n;" + "\n\n/* SwisSQL Message : Query split into multiple Queries. Manual Intervention required */\n";
               } else {
                  SelectQueryStatement.singleQueryConvertedToMultipleQueryList = "CREATE " + createSequenceObj.toString() + "\n;" + "\n\n/* SwisSQL Message : Query split into multiple Queries. Manual Intervention required */\n";
               }

               this.notNullStr = "";
            }
         }

         if (this.notNull.getNullStatus() != null) {
            this.notNullStr = this.notNullStr + this.notNull.getNullStatus();
         }
      }

   }

   public void toInformixString() throws ConvertException {
      this.setAutoIncrement((String)null);
      if (this.constraintName != null) {
         if (this.constraintName.startsWith("[") && this.constraintName.endsWith("]") || this.constraintName.startsWith("`") && this.constraintName.endsWith("`")) {
            this.constraintName = this.constraintName.substring(1, this.constraintName.length() - 1);
            if (this.constraintName.indexOf(32) != -1) {
               this.constraintName = "\"" + this.constraintName + "\"";
            }
         }

         this.setConstraintName(this.constraintName);
      }

      if (this.getConstraintType() != null) {
         ConstraintType toInformixConstraintType = this.getConstraintType();
         if (toInformixConstraintType instanceof PrimaryOrUniqueConstraintClause) {
            PrimaryOrUniqueConstraintClause primaryOrUniqueConstraintClause = (PrimaryOrUniqueConstraintClause)toInformixConstraintType;
            primaryOrUniqueConstraintClause.setColumnName(this.getColumnName());
            this.setConstraint((String)null);
            this.setConstraintName((String)null);
         } else if (toInformixConstraintType instanceof ForeignConstraintClause) {
            ForeignConstraintClause foreignConstraintClause = (ForeignConstraintClause)toInformixConstraintType;
            foreignConstraintClause.setColumnName(this.getColumnName());
            this.setConstraint((String)null);
            this.setConstraintName((String)null);
         } else if (toInformixConstraintType instanceof DefaultConstraintClause && this.getColumnName() != null) {
            this.setConstraint((String)null);
            this.setConstraintName((String)null);
         }

         toInformixConstraintType.toInformixString();
      } else if (this.notNull != null) {
         this.notNullStr = "";
         if (this.notNull.getIdentity() != null) {
            this.notNullStr = this.notNullStr + this.notNull.getIdentity() + " ";
         }

         if (this.notNull.getNullStatus() != null) {
            this.notNullStr = this.notNullStr + this.notNull.getNullStatus();
         }
      }

   }

   public void toTimesTenString() throws ConvertException {
      if (this.constraintName != null) {
         if (this.constraintName.startsWith("`") && this.constraintName.endsWith("`")) {
            this.constraintName = this.constraintName.substring(1, this.constraintName.length() - 1);
            if (this.constraintName.indexOf(32) != -1) {
               this.constraintName = "\"" + this.constraintName + "\"";
            }
         } else if (this.constraintName.startsWith("1") || this.constraintName.startsWith("2") || this.constraintName.startsWith("3") || this.constraintName.startsWith("4") || this.constraintName.startsWith("5") || this.constraintName.startsWith("6") || this.constraintName.startsWith("7") || this.constraintName.startsWith("8") || this.constraintName.startsWith("9") || this.constraintName.startsWith("0") || this.constraintName.startsWith("-") || this.constraintName.startsWith(".")) {
            this.constraintName = "\"" + this.constraintName + "\"";
         }

         this.constraintName = CustomizeUtil.objectNamesToQuotedIdentifier(this.constraintName, SwisSQLUtils.getKeywords(10), (ModifiedObjectAttr)null, 10);
         if (this.constraintName.length() > 30) {
            if (SwisSQLAPI.truncateConstraintCount > 99) {
               SwisSQLAPI.truncateConstraintCount = 0;
            }

            this.constraintName = this.constraintName.substring(0, 24) + "_ADV" + SwisSQLAPI.truncateConstraintCount;
            ++SwisSQLAPI.truncateConstraintCount;
            CreateQueryStatement.commentWhenConstraintNameTruncated = "/* SwisSQL Message : Manual Intervention may be required. The constraint name changed as the length was greater than 30 characters. */ ";
         }

         this.setConstraintName(this.constraintName);
      }

      if (this.getConstraintType() != null) {
         ConstraintType toTimesTenConstraintType = this.getConstraintType();
         if (toTimesTenConstraintType instanceof DefaultConstraintClause) {
            DefaultConstraintClause defaultConstraintClause = (DefaultConstraintClause)toTimesTenConstraintType;
            if (this.getColumnName() != null) {
               this.setConstraint((String)null);
               this.setConstraintName((String)null);
            }
         }

         toTimesTenConstraintType.toTimesTenString();
      } else if (this.notNull != null) {
         if (this.constraintType == null) {
            this.setConstraint((String)null);
            this.setConstraintName((String)null);
         }

         this.notNullStr = "";
         if (this.notNull.getIdentity() != null) {
            if (this.columnNameForSequence == null) {
               this.notNullStr = this.notNullStr + this.notNull.getIdentity() + " ";
            } else {
               String identity = this.notNull.getIdentity();
               TableObject tableObj = new TableObject();
               CreateSequenceStatement createSequenceObj = new CreateSequenceStatement();
               createSequenceObj.setSequence("SEQUENCE");
               String columnName = this.columnNameForSequence;
               String str;
               String str1;
               String str2;
               if ((!columnName.startsWith("[") || !columnName.endsWith("]")) && (!columnName.startsWith("`") || !columnName.endsWith("`"))) {
                  if (this.tableNameFromCQS != null) {
                     str = this.tableNameFromCQS + columnName + "_SEQ";
                     str1 = this.tableNameFromCQS + columnName;
                     str2 = this.tableNameFromCQS;
                     if (str.length() > 29) {
                        if (str1.length() > 25) {
                           str = str1.substring(0, 26) + "_SEQ";
                        } else if (str2.length() > 25) {
                           str = str2.substring(0, 26) + "_SEQ";
                        }

                        tableObj.setTableName(str);
                     } else {
                        tableObj.setTableName(this.tableNameFromCQS + "_" + columnName + "_SEQ");
                     }
                  } else {
                     tableObj.setTableName(this.columnName + "_SEQ");
                  }
               } else {
                  columnName = columnName.substring(1, columnName.length() - 1);
                  if (columnName.indexOf(32) != -1) {
                     columnName = "\"" + columnName + "\"";
                  }

                  if (this.tableNameFromCQS != null) {
                     str = this.tableNameFromCQS + columnName.substring(1, columnName.length() - 1) + "_SEQ";
                     str1 = this.tableNameFromCQS + columnName.substring(1, columnName.length() - 1);
                     str2 = this.tableNameFromCQS;
                     if (str.length() > 29) {
                        if (str1.length() > 25) {
                           str = str1.substring(0, 26) + "_SEQ";
                        } else if (str2.length() > 25) {
                           str = str2.substring(0, 26) + "_SEQ";
                        }

                        if (str.length() > 27) {
                           tableObj.setTableName("\"" + str.substring(0, 28) + "\"");
                        } else {
                           tableObj.setTableName("\"" + str + "\"");
                        }
                     } else if (str.length() > 27) {
                        tableObj.setTableName("\"" + this.tableNameFromCQS + "_" + columnName + "_S" + "\"");
                     } else {
                        tableObj.setTableName("\"" + this.tableNameFromCQS + "_" + columnName + "_SEQ" + "\"");
                     }
                  } else {
                     tableObj.setTableName(columnName + "_SEQ");
                  }
               }

               createSequenceObj.setSchemaName(tableObj);
               if (!identity.trim().equalsIgnoreCase("IDENTITY")) {
                  str = identity.trim().substring(8).trim();
                  str = str.substring(1, str.length() - 1);
                  StringTokenizer st = new StringTokenizer(str, ",");
                  str2 = st.nextToken();
                  createSequenceObj.setMinValueOrNoMinValue("MINVALUE " + str2);
                  if (st.countTokens() > 0) {
                     String token2 = st.nextToken();
                     createSequenceObj.setIncrementString("INCREMENT BY");
                     createSequenceObj.setIncrementValue(token2);
                  } else {
                     createSequenceObj.setIncrementString("INCREMENT BY");
                     createSequenceObj.setIncrementValue("1");
                  }
               }

               if (SelectQueryStatement.singleQueryConvertedToMultipleQueryList != null) {
                  SelectQueryStatement.singleQueryConvertedToMultipleQueryList = SelectQueryStatement.singleQueryConvertedToMultipleQueryList + "CREATE " + createSequenceObj.toString() + ";\n\n/* SwisSQL Message : Query split into multiple Queries. Manual Intervention may be required */\n";
               } else {
                  SelectQueryStatement.singleQueryConvertedToMultipleQueryList = "CREATE " + createSequenceObj.toString() + ";\n\n/* SwisSQL Message : Query split into multiple Queries. Manual Intervention may be required */\n";
               }

               this.notNullStr = "";
            }
         }

         if (this.notNull.getNullStatus() != null) {
            this.notNullStr = this.notNullStr + this.notNull.getNullStatus();
         }
      }

   }

   public void toNetezzaString() throws ConvertException {
      this.setAutoIncrement((String)null);
      if (this.constraintName != null) {
         if (this.constraintName.startsWith("[") && this.constraintName.endsWith("]") || this.constraintName.startsWith("`") && this.constraintName.endsWith("`")) {
            this.constraintName = this.constraintName.substring(1, this.constraintName.length() - 1);
            if (this.constraintName.indexOf(32) != -1) {
               this.constraintName = "\"" + this.constraintName + "\"";
            }
         }

         if (this.constraintName.startsWith("1") || this.constraintName.startsWith("2") || this.constraintName.startsWith("3") || this.constraintName.startsWith("4") || this.constraintName.startsWith("5") || this.constraintName.startsWith("6") || this.constraintName.startsWith("7") || this.constraintName.startsWith("8") || this.constraintName.startsWith("9") || this.constraintName.startsWith("0") || this.constraintName.startsWith("-") || this.constraintName.startsWith(".")) {
            if (this.constraintName.length() < 22) {
               this.constraintName = "CONS_NAME" + this.constraintName;
            } else if (this.constraintName.length() > 22 && this.constraintName.length() < 26) {
               this.constraintName = "CONS" + this.constraintName;
            } else {
               this.constraintName = "CON" + this.constraintName;
            }
         }

         this.setConstraintName(this.constraintName);
      }

      if (this.getConstraintType() != null) {
         ConstraintType toNetezzaSQLConstraintType = this.getConstraintType();
         if (toNetezzaSQLConstraintType instanceof PrimaryOrUniqueConstraintClause) {
            PrimaryOrUniqueConstraintClause primaryOrUniqueConstraintClause = (PrimaryOrUniqueConstraintClause)toNetezzaSQLConstraintType;
            primaryOrUniqueConstraintClause.setColumnName(this.getColumnName());
         } else if (toNetezzaSQLConstraintType instanceof ForeignConstraintClause) {
            ForeignConstraintClause foreignConstraintClause = (ForeignConstraintClause)toNetezzaSQLConstraintType;
            foreignConstraintClause.setColumnName(this.getColumnName());
         } else if (toNetezzaSQLConstraintType instanceof DefaultConstraintClause && this.getColumnName() != null) {
            this.setConstraint((String)null);
            this.setConstraintName((String)null);
         }

         toNetezzaSQLConstraintType.toNetezzaString();
      } else if (this.notNull != null) {
         this.notNullStr = "";
         if (this.notNull.getIdentity() != null) {
            this.notNullStr = this.notNullStr + this.notNull.getIdentity() + " ";
         }

         if (this.notNull.getNullStatus() != null) {
            this.notNullStr = this.notNullStr + this.notNull.getNullStatus();
         }
      }

   }

   public void toTeradataString() throws ConvertException {
      this.setAutoIncrement((String)null);
      if (this.constraintName != null) {
         if (this.constraintName.startsWith("[") && this.constraintName.endsWith("]") || this.constraintName.startsWith("`") && this.constraintName.endsWith("`")) {
            this.constraintName = this.constraintName.substring(1, this.constraintName.length() - 1);
            if (this.constraintName.indexOf(32) != -1) {
               this.constraintName = "\"" + this.constraintName + "\"";
            }
         }

         if (this.constraintName.startsWith("1") || this.constraintName.startsWith("2") || this.constraintName.startsWith("3") || this.constraintName.startsWith("4") || this.constraintName.startsWith("5") || this.constraintName.startsWith("6") || this.constraintName.startsWith("7") || this.constraintName.startsWith("8") || this.constraintName.startsWith("9") || this.constraintName.startsWith("0") || this.constraintName.startsWith("-") || this.constraintName.startsWith(".")) {
            if (this.constraintName.length() < 22) {
               this.constraintName = "CONS_NAME" + this.constraintName;
            } else if (this.constraintName.length() > 22 && this.constraintName.length() < 26) {
               this.constraintName = "CONS" + this.constraintName;
            } else {
               this.constraintName = "CON" + this.constraintName;
            }
         }

         this.setConstraintName(this.constraintName);
      }

      if (this.getConstraintType() != null) {
         ConstraintType toTeradataSQLConstraintType = this.getConstraintType();
         if (toTeradataSQLConstraintType instanceof PrimaryOrUniqueConstraintClause) {
            PrimaryOrUniqueConstraintClause primaryOrUniqueConstraintClause = (PrimaryOrUniqueConstraintClause)toTeradataSQLConstraintType;
            primaryOrUniqueConstraintClause.setColumnName(this.getColumnName());
         } else if (toTeradataSQLConstraintType instanceof ForeignConstraintClause) {
            ForeignConstraintClause foreignConstraintClause = (ForeignConstraintClause)toTeradataSQLConstraintType;
            foreignConstraintClause.setColumnName(this.getColumnName());
         } else if (toTeradataSQLConstraintType instanceof DefaultConstraintClause && this.getColumnName() != null) {
            this.setConstraint((String)null);
            this.setConstraintName((String)null);
         }

         toTeradataSQLConstraintType.toTeradataString();
      } else if (this.notNull != null) {
         this.notNullStr = "";
         if (this.notNull.getIdentity() != null) {
            this.notNullStr = this.notNullStr + this.notNull.getIdentity() + " ";
         }

         if (this.notNull.getNullStatus() != null) {
            this.notNullStr = this.notNullStr + this.notNull.getNullStatus();
         }
      }

   }

   public ConstraintClause copyObjectValues() {
      ConstraintClause dupConstraintClause = new ConstraintClause();
      dupConstraintClause.setObjectContext(this.context);
      ConstraintType orgConstraintType = this.getConstraintType();
      if (orgConstraintType != null) {
         ConstraintType newConstraintType = null;
         if (orgConstraintType instanceof PrimaryOrUniqueConstraintClause) {
            PrimaryOrUniqueConstraintClause orgPrimOrUniqueClause = (PrimaryOrUniqueConstraintClause)orgConstraintType;
            newConstraintType = orgPrimOrUniqueClause.copyObjectValues();
         } else if (orgConstraintType instanceof ForeignConstraintClause) {
            ForeignConstraintClause orgForeignClause = (ForeignConstraintClause)orgConstraintType;
            newConstraintType = orgForeignClause.copyObjectValues();
         } else if (orgConstraintType instanceof CheckConstraintClause) {
            CheckConstraintClause orgCheckClause = (CheckConstraintClause)orgConstraintType;
            newConstraintType = orgCheckClause.copyObjectValues();
         } else if (orgConstraintType instanceof DefaultConstraintClause) {
            DefaultConstraintClause orgDefaultClause = (DefaultConstraintClause)orgConstraintType;
            newConstraintType = orgDefaultClause.copyObjectValues();
         }

         dupConstraintClause.setConstraintType(newConstraintType);
      }

      dupConstraintClause.setConstraintName(this.getConstraintName());
      dupConstraintClause.setAutoIncrement(this.getAutoIncrement());
      dupConstraintClause.setConstraint(this.getConstraint());
      dupConstraintClause.setNotNull(this.getNotNull());
      return dupConstraintClause;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.autoIncrement != null) {
         sb.append(this.autoIncrement + " ");
      }

      if (this.constraint != null) {
         sb.append(this.constraint.toUpperCase() + " ");
      }

      if (this.constraintName != null) {
         if (this.context != null) {
            String temp = this.context.getEquivalent(this.constraintName).toString();
            sb.append(temp + " ");
         } else {
            sb.append(this.constraintName + " ");
         }
      }

      if (this.constraintType != null) {
         if (this.constraintType instanceof PrimaryOrUniqueConstraintClause) {
            ((PrimaryOrUniqueConstraintClause)this.constraintType).setObjectContext(this.context);
            sb.append(((PrimaryOrUniqueConstraintClause)this.constraintType).toString());
         } else if (this.constraintType instanceof ForeignConstraintClause) {
            ((ForeignConstraintClause)this.constraintType).setObjectContext(this.context);
            sb.append(((ForeignConstraintClause)this.constraintType).toString());
         } else if (this.constraintType instanceof CheckConstraintClause) {
            ((CheckConstraintClause)this.constraintType).setObjectContext(this.context);
            sb.append(((CheckConstraintClause)this.constraintType).toString());
         } else if (this.constraintType instanceof DefaultConstraintClause) {
            ((DefaultConstraintClause)this.constraintType).setObjectContext(this.context);
            sb.append(((DefaultConstraintClause)this.constraintType).toString());
         }
      }

      if (this.notNullStr != null) {
         sb.append(this.notNullStr);
      }

      if (this.commentForConstraintName) {
         sb.append(" /*SwisSQL Message : Manual Intervention required. The constraint name changed as the length was greater than " + this.characterLengthForComment + " characters.*/ ");
      }

      return sb.toString();
   }
}
