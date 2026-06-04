package com.adventnet.swissqlapi.sql.statement.select;

import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.util.misc.CastingUtil;
import com.adventnet.swissqlapi.util.misc.GeneralUtil;

public class OrderItem {
   private SelectColumn orderSpecifier;
   private String Order;
   private String UsingOperator;
   private String nullsOrder;
   private UserObjectContext context = null;
   private boolean isFromSelectStatement = false;

   public void setObjectContext(UserObjectContext context) {
      this.context = context;
   }

   public void setOrderSpecifier(SelectColumn sc) {
      this.orderSpecifier = sc;
   }

   public void setOrder(String s_o) {
      this.Order = s_o;
   }

   public void setNullsOrder(String nullsOrder) {
      this.nullsOrder = nullsOrder;
   }

   public String getNullsOrder() {
      return this.nullsOrder;
   }

   public void setUsingOperator(String s_uo) {
      this.UsingOperator = s_uo;
   }

   public void setIsFromSelectStatement(boolean isSelStmt) {
      this.isFromSelectStatement = isSelStmt;
   }

   public SelectColumn getOrderSpecifier() {
      return this.orderSpecifier;
   }

   public String getOrder() {
      return this.Order;
   }

   public String getUsingOperator() {
      return this.UsingOperator;
   }

   public OrderItem toANSISelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      OrderItem oi = new OrderItem();
      if (this.orderSpecifier != null) {
         oi.setOrderSpecifier(this.orderSpecifier.toANSISelect(to_sqs, from_sqs));
      }

      if (this.Order != null && this.Order.equalsIgnoreCase("NULLS FIRST")) {
         this.Order = " ";
      } else if (this.Order != null && this.Order.equalsIgnoreCase("NULLS LAST")) {
         this.Order = " ";
      } else if (this.Order != null && !this.Order.equalsIgnoreCase("USING")) {
         oi.setOrder(this.Order);
      } else if (this.UsingOperator != null) {
         if (!this.UsingOperator.equalsIgnoreCase("<") && !this.UsingOperator.equalsIgnoreCase("<=") && !this.UsingOperator.equalsIgnoreCase("~") && !this.UsingOperator.equalsIgnoreCase("~*") && !this.UsingOperator.equalsIgnoreCase("!~") && !this.UsingOperator.equalsIgnoreCase("!~*") && !this.UsingOperator.equalsIgnoreCase("*")) {
            if (!this.UsingOperator.equalsIgnoreCase(">") && !this.UsingOperator.equalsIgnoreCase(">=") && !this.UsingOperator.equalsIgnoreCase("/")) {
               if (this.UsingOperator.equalsIgnoreCase("=") || this.UsingOperator.equalsIgnoreCase("<>") || this.UsingOperator.equalsIgnoreCase("!=")) {
                  oi = null;
               }
            } else {
               oi.setOrder("DESC");
            }
         } else {
            oi.setOrder("ASC");
         }
      }

      return oi;
   }

   public OrderItem toTeradataSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      OrderItem oi = new OrderItem();
      if (this.orderSpecifier != null) {
         SelectColumn teradataOrderSpecifier = this.orderSpecifier.toTeradataSelect(to_sqs, from_sqs);
         String scStr = this.orderSpecifier.getTheCoreSelectItem().trim();
         oi.setOrderSpecifier(teradataOrderSpecifier);
         boolean aliasPresent = false;
         if (to_sqs != null && from_sqs != null && to_sqs.getSelectStatement() != null && from_sqs.getSelectStatement().getSelectItemList().size() == to_sqs.getSelectStatement().getSelectItemList().size()) {
            for(int sci = 0; sci < from_sqs.getSelectStatement().getSelectItemList().size(); ++sci) {
               String al = ((SelectColumn)from_sqs.getSelectStatement().getSelectItemList().get(sci)).getTheCoreSelectItem().trim();
               if (al != null && al.equalsIgnoreCase(scStr)) {
                  aliasPresent = true;
                  teradataOrderSpecifier.getColumnExpression().clear();
                  teradataOrderSpecifier.getColumnExpression().add(((SelectColumn)to_sqs.getSelectStatement().getSelectItemList().get(sci)).getTheCoreSelectItem());
                  break;
               }

               String aliasName = ((SelectColumn)from_sqs.getSelectStatement().getSelectItemList().get(sci)).getAliasName();
               if (aliasName != null && aliasName.equalsIgnoreCase(scStr)) {
                  teradataOrderSpecifier.getColumnExpression().clear();
                  teradataOrderSpecifier.getColumnExpression().add(((SelectColumn)to_sqs.getSelectStatement().getSelectItemList().get(sci)).getTheCoreSelectItem());
                  break;
               }
            }
         }
      }

      if (this.Order != null && this.Order.equalsIgnoreCase("NULLS FIRST")) {
         this.Order = " ";
      } else if (this.Order != null && this.Order.equalsIgnoreCase("NULLS LAST")) {
         this.Order = " ";
      } else if (this.Order != null && !this.Order.equalsIgnoreCase("USING")) {
         oi.setOrder(this.Order);
      } else if (this.UsingOperator != null) {
         if (!this.UsingOperator.equalsIgnoreCase("<") && !this.UsingOperator.equalsIgnoreCase("<=") && !this.UsingOperator.equalsIgnoreCase("~") && !this.UsingOperator.equalsIgnoreCase("~*") && !this.UsingOperator.equalsIgnoreCase("!~") && !this.UsingOperator.equalsIgnoreCase("!~*") && !this.UsingOperator.equalsIgnoreCase("*")) {
            if (!this.UsingOperator.equalsIgnoreCase(">") && !this.UsingOperator.equalsIgnoreCase(">=") && !this.UsingOperator.equalsIgnoreCase("/")) {
               if (this.UsingOperator.equalsIgnoreCase("=") || this.UsingOperator.equalsIgnoreCase("<>") || this.UsingOperator.equalsIgnoreCase("!=")) {
                  oi = null;
               }
            } else {
               oi.setOrder("DESC");
            }
         } else {
            oi.setOrder("ASC");
         }
      }

      return oi;
   }

   public OrderItem toBigQuerySelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      OrderItem oi = new OrderItem();
      if (this.orderSpecifier != null) {
         oi.setOrderSpecifier(this.orderSpecifier.toBigQuerySelect(to_sqs, from_sqs));
      }

      if (this.Order != null && this.Order.equalsIgnoreCase("DESC")) {
         oi.setOrder(this.Order + " NULLS LAST ");
      } else if (this.Order != null) {
         oi.setOrder(this.Order + " NULLS FIRST ");
      } else {
         oi.setOrder(" NULLS FIRST ");
      }

      if (this.UsingOperator != null) {
         oi.setUsingOperator(this.UsingOperator);
      }

      return oi;
   }

   public OrderItem toAthenaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      OrderItem oi = new OrderItem();
      if (this.orderSpecifier != null) {
         oi.setOrderSpecifier(this.orderSpecifier.toAthenaSelect(to_sqs, from_sqs));
      }

      if (this.Order != null && this.Order.equalsIgnoreCase("DESC")) {
         oi.setOrder(this.Order);
         oi.setNullsOrder("NULLS LAST");
      } else if (this.Order != null) {
         oi.setOrder(this.Order);
         oi.setNullsOrder("NULLS FIRST");
      } else {
         oi.setNullsOrder("NULLS FIRST");
      }

      if (this.UsingOperator != null) {
         oi.setUsingOperator(this.UsingOperator);
      }

      return oi;
   }

   public OrderItem toSapHanaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      OrderItem oi = new OrderItem();
      if (this.orderSpecifier != null) {
         oi.setOrderSpecifier(this.orderSpecifier.toSapHanaSelect(to_sqs, from_sqs));
      }

      if (this.Order != null && this.Order.equalsIgnoreCase("DESC")) {
         oi.setOrder(this.Order);
         oi.setNullsOrder("NULLS LAST");
      } else if (this.Order != null) {
         oi.setOrder(this.Order);
         oi.setNullsOrder("NULLS FIRST");
      } else {
         oi.setNullsOrder("NULLS FIRST");
      }

      if (this.UsingOperator != null) {
         if (!this.UsingOperator.equalsIgnoreCase("<") && !this.UsingOperator.equalsIgnoreCase("<=") && !this.UsingOperator.equalsIgnoreCase("~") && !this.UsingOperator.equalsIgnoreCase("~*") && !this.UsingOperator.equalsIgnoreCase("!~") && !this.UsingOperator.equalsIgnoreCase("!~*") && !this.UsingOperator.equalsIgnoreCase("*")) {
            if (!this.UsingOperator.equalsIgnoreCase(">") && !this.UsingOperator.equalsIgnoreCase(">=") && !this.UsingOperator.equalsIgnoreCase("/")) {
               if (this.UsingOperator.equalsIgnoreCase("=") || this.UsingOperator.equalsIgnoreCase("<>") || this.UsingOperator.equalsIgnoreCase("!=")) {
                  oi = null;
               }
            } else {
               oi.setOrder("DESC");
            }
         } else {
            oi.setOrder("ASC");
         }
      }

      return oi;
   }

   public OrderItem toSqliteSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      OrderItem oi = new OrderItem();
      if (this.orderSpecifier != null) {
         oi.setOrderSpecifier(this.orderSpecifier.toSqliteSelect(to_sqs, from_sqs));
      }

      if (this.Order != null && this.Order.equalsIgnoreCase("DESC")) {
         oi.setOrder(this.Order);
         oi.setNullsOrder("NULLS LAST");
      } else if (this.Order != null) {
         oi.setOrder(this.Order);
         oi.setNullsOrder("NULLS FIRST");
      } else {
         oi.setNullsOrder("NULLS FIRST");
      }

      if (this.UsingOperator != null) {
         if (!this.UsingOperator.equalsIgnoreCase("<") && !this.UsingOperator.equalsIgnoreCase("<=") && !this.UsingOperator.equalsIgnoreCase("~") && !this.UsingOperator.equalsIgnoreCase("~*") && !this.UsingOperator.equalsIgnoreCase("!~") && !this.UsingOperator.equalsIgnoreCase("!~*") && !this.UsingOperator.equalsIgnoreCase("*")) {
            if (!this.UsingOperator.equalsIgnoreCase(">") && !this.UsingOperator.equalsIgnoreCase(">=") && !this.UsingOperator.equalsIgnoreCase("/")) {
               if (this.UsingOperator.equalsIgnoreCase("=") || this.UsingOperator.equalsIgnoreCase("<>") || this.UsingOperator.equalsIgnoreCase("!=")) {
                  oi = null;
               }
            } else {
               oi.setOrder("DESC");
            }
         } else {
            oi.setOrder("ASC");
         }
      }

      return oi;
   }

   public OrderItem toExcelSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      OrderItem oi = new OrderItem();
      if (this.orderSpecifier != null) {
         oi.setOrderSpecifier(this.orderSpecifier.toExcelSelect(to_sqs, from_sqs));
      }

      if (this.Order != null && this.Order.equalsIgnoreCase("DESC")) {
         oi.setOrder(this.Order);
         oi.setNullsOrder((String)null);
      } else if (this.Order != null) {
         oi.setOrder(this.Order);
         oi.setNullsOrder((String)null);
      } else {
         oi.setNullsOrder((String)null);
      }

      if (this.UsingOperator != null) {
         if (!this.UsingOperator.equalsIgnoreCase("<") && !this.UsingOperator.equalsIgnoreCase("<=") && !this.UsingOperator.equalsIgnoreCase("~") && !this.UsingOperator.equalsIgnoreCase("~*") && !this.UsingOperator.equalsIgnoreCase("!~") && !this.UsingOperator.equalsIgnoreCase("!~*") && !this.UsingOperator.equalsIgnoreCase("*")) {
            if (!this.UsingOperator.equalsIgnoreCase(">") && !this.UsingOperator.equalsIgnoreCase(">=") && !this.UsingOperator.equalsIgnoreCase("/")) {
               if (this.UsingOperator.equalsIgnoreCase("=") || this.UsingOperator.equalsIgnoreCase("<>") || this.UsingOperator.equalsIgnoreCase("!=")) {
                  oi = null;
               }
            } else {
               oi.setOrder("DESC");
            }
         } else {
            oi.setOrder("ASC");
         }
      }

      return oi;
   }

   public OrderItem toMsAccessJdbcSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      OrderItem oi = new OrderItem();
      if (this.orderSpecifier != null) {
         oi.setOrderSpecifier(this.orderSpecifier.toMsAccessJdbcSelect(to_sqs, from_sqs));
      }

      if (this.Order != null && this.Order.equalsIgnoreCase("DESC")) {
         oi.setOrder(this.Order);
         oi.setNullsOrder((String)null);
      } else if (this.Order != null) {
         oi.setOrder(this.Order);
         oi.setNullsOrder((String)null);
      } else {
         oi.setNullsOrder((String)null);
      }

      if (this.UsingOperator != null) {
         if (!this.UsingOperator.equalsIgnoreCase("<") && !this.UsingOperator.equalsIgnoreCase("<=") && !this.UsingOperator.equalsIgnoreCase("~") && !this.UsingOperator.equalsIgnoreCase("~*") && !this.UsingOperator.equalsIgnoreCase("!~") && !this.UsingOperator.equalsIgnoreCase("!~*") && !this.UsingOperator.equalsIgnoreCase("*")) {
            if (!this.UsingOperator.equalsIgnoreCase(">") && !this.UsingOperator.equalsIgnoreCase(">=") && !this.UsingOperator.equalsIgnoreCase("/")) {
               if (this.UsingOperator.equalsIgnoreCase("=") || this.UsingOperator.equalsIgnoreCase("<>") || this.UsingOperator.equalsIgnoreCase("!=")) {
                  oi = null;
               }
            } else {
               oi.setOrder("DESC");
            }
         } else {
            oi.setOrder("ASC");
         }
      }

      return oi;
   }

   public OrderItem toPostgreSQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      OrderItem oi = new OrderItem();
      boolean isVertica = from_sqs != null && from_sqs.isVerticaDb();
      if (this.orderSpecifier != null) {
         oi.setOrderSpecifier(this.orderSpecifier.toPostgreSQLSelect(to_sqs, from_sqs));
      }

      if (this.Order != null && this.Order.equalsIgnoreCase("DESC")) {
         oi.setOrder(isVertica ? this.Order : this.Order + " NULLS LAST ");
      } else if (this.Order != null) {
         oi.setOrder(isVertica ? this.Order : this.Order + " NULLS FIRST ");
      } else {
         oi.setOrder(isVertica ? null : " NULLS FIRST ");
      }

      if (this.UsingOperator != null) {
         oi.setUsingOperator(this.UsingOperator);
      }

      return oi;
   }

   public OrderItem toMySQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      OrderItem oi = new OrderItem();
      if (this.orderSpecifier != null) {
         oi.setOrderSpecifier(this.orderSpecifier.toMySQLSelect(to_sqs, from_sqs));
      }

      if (this.Order != null && this.Order.equalsIgnoreCase("NULLS FIRST")) {
         oi.setOrder((String)null);
      } else if (this.Order != null && this.Order.equalsIgnoreCase("NULLS LAST")) {
         this.Order = " ";
      } else if (this.Order != null && !this.Order.equalsIgnoreCase("USING")) {
         oi.setOrder(this.Order);
      } else if (this.UsingOperator != null) {
         if (!this.UsingOperator.equalsIgnoreCase("<") && !this.UsingOperator.equalsIgnoreCase("<=") && !this.UsingOperator.equalsIgnoreCase("~") && !this.UsingOperator.equalsIgnoreCase("~*") && !this.UsingOperator.equalsIgnoreCase("!~") && !this.UsingOperator.equalsIgnoreCase("!~*") && !this.UsingOperator.equalsIgnoreCase("*")) {
            if (!this.UsingOperator.equalsIgnoreCase(">") && !this.UsingOperator.equalsIgnoreCase(">=") && !this.UsingOperator.equalsIgnoreCase("/")) {
               if (this.UsingOperator.equalsIgnoreCase("=") || this.UsingOperator.equalsIgnoreCase("<>") || this.UsingOperator.equalsIgnoreCase("!=")) {
                  oi = null;
               }
            } else {
               oi.setOrder("DESC");
            }
         } else {
            oi.setOrder("ASC");
         }
      }

      return oi;
   }

   public OrderItem toSnowflakeSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      OrderItem oi = new OrderItem();
      if (this.orderSpecifier != null) {
         oi.setOrderSpecifier(this.orderSpecifier.toSnowflakeSelect(to_sqs, from_sqs));
      }

      if (this.Order != null && this.Order.equalsIgnoreCase("DESC")) {
         oi.setOrder(this.Order + " NULLS LAST ");
      } else if (this.Order != null) {
         oi.setOrder(this.Order + " NULLS FIRST ");
      } else {
         oi.setOrder(" NULLS FIRST ");
      }

      if (this.UsingOperator != null) {
         oi.setUsingOperator(this.UsingOperator);
      }

      return oi;
   }

   public OrderItem toMSSQLServerSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      OrderItem oi = new OrderItem();
      if (this.orderSpecifier != null) {
         oi.setOrderSpecifier(this.orderSpecifier.toMSSQLServerSelect(to_sqs, from_sqs));
      }

      if (this.nullsOrder != null) {
         if (this.nullsOrder.trim().equalsIgnoreCase("NULLS FIRST")) {
            oi.setNullsOrder((String)null);
            oi.setOrder("ASC");
         } else if (this.nullsOrder.trim().equalsIgnoreCase("NULLS LAST")) {
            oi.setNullsOrder((String)null);
            oi.setOrder("DESC");
         }
      }

      if (this.Order != null && this.Order.equalsIgnoreCase("NULLS FIRST")) {
         oi.setOrder((String)null);
      } else if (this.Order != null && this.Order.equalsIgnoreCase("NULLS LAST")) {
         this.Order = " ";
      } else if (this.Order != null && !this.Order.equalsIgnoreCase("USING")) {
         oi.setOrder(this.Order);
      } else if (this.UsingOperator != null) {
         if (!this.UsingOperator.equalsIgnoreCase("<") && !this.UsingOperator.equalsIgnoreCase("<=") && !this.UsingOperator.equalsIgnoreCase("~") && !this.UsingOperator.equalsIgnoreCase("~*") && !this.UsingOperator.equalsIgnoreCase("!~") && !this.UsingOperator.equalsIgnoreCase("!~*") && !this.UsingOperator.equalsIgnoreCase("*")) {
            if (!this.UsingOperator.equalsIgnoreCase(">") && !this.UsingOperator.equalsIgnoreCase(">=") && !this.UsingOperator.equalsIgnoreCase("/")) {
               if (this.UsingOperator.equalsIgnoreCase("=") || this.UsingOperator.equalsIgnoreCase("<>") || this.UsingOperator.equalsIgnoreCase("!=")) {
                  oi = null;
               }
            } else {
               oi.setOrder("DESC");
            }
         } else {
            oi.setOrder("ASC");
         }
      }

      return oi;
   }

   public OrderItem toSybaseSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      OrderItem oi = new OrderItem();
      oi.setObjectContext(this.context);
      if (this.orderSpecifier != null) {
         oi.setOrderSpecifier(this.orderSpecifier.toSybaseSelect(to_sqs, from_sqs));
      }

      if (this.Order != null && this.Order.equalsIgnoreCase("NULLS FIRST")) {
         oi.setOrder((String)null);
      } else if (this.Order != null && this.Order.equalsIgnoreCase("NULLS LAST")) {
         this.Order = " ";
      } else if (this.Order != null && !this.Order.equalsIgnoreCase("USING")) {
         oi.setOrder(this.Order);
      } else if (this.UsingOperator != null) {
         if (!this.UsingOperator.equalsIgnoreCase("<") && !this.UsingOperator.equalsIgnoreCase("<=") && !this.UsingOperator.equalsIgnoreCase("~") && !this.UsingOperator.equalsIgnoreCase("~*") && !this.UsingOperator.equalsIgnoreCase("!~") && !this.UsingOperator.equalsIgnoreCase("!~*") && !this.UsingOperator.equalsIgnoreCase("*")) {
            if (!this.UsingOperator.equalsIgnoreCase(">") && !this.UsingOperator.equalsIgnoreCase(">=") && !this.UsingOperator.equalsIgnoreCase("/")) {
               if (this.UsingOperator.equalsIgnoreCase("=") || this.UsingOperator.equalsIgnoreCase("<>") || this.UsingOperator.equalsIgnoreCase("!=")) {
                  oi = null;
               }
            } else {
               oi.setOrder("DESC");
            }
         } else {
            oi.setOrder("ASC");
         }
      }

      return oi;
   }

   public OrderItem toDB2Select(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      OrderItem oi = new OrderItem();
      if (this.orderSpecifier != null) {
         oi.setOrderSpecifier(this.orderSpecifier.toDB2Select(to_sqs, from_sqs));
      }

      if (this.Order != null && this.Order.equalsIgnoreCase("DESC")) {
         oi.setOrder(this.Order);
         oi.setNullsOrder("NULLS LAST");
      } else if (this.Order != null) {
         oi.setOrder(this.Order);
         oi.setNullsOrder("NULLS FIRST");
      } else {
         oi.setNullsOrder("NULLS FIRST");
      }

      if (this.UsingOperator != null) {
         if (!this.UsingOperator.equalsIgnoreCase("<") && !this.UsingOperator.equalsIgnoreCase("<=") && !this.UsingOperator.equalsIgnoreCase("~") && !this.UsingOperator.equalsIgnoreCase("~*") && !this.UsingOperator.equalsIgnoreCase("!~") && !this.UsingOperator.equalsIgnoreCase("!~*") && !this.UsingOperator.equalsIgnoreCase("*")) {
            if (!this.UsingOperator.equalsIgnoreCase(">") && !this.UsingOperator.equalsIgnoreCase(">=") && !this.UsingOperator.equalsIgnoreCase("/")) {
               if (this.UsingOperator.equalsIgnoreCase("=") || this.UsingOperator.equalsIgnoreCase("<>") || this.UsingOperator.equalsIgnoreCase("!=")) {
                  oi = null;
               }
            } else {
               oi.setOrder("DESC");
            }
         } else {
            oi.setOrder("ASC");
         }
      }

      return oi;
   }

   public OrderItem toOracleSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      OrderItem oi = new OrderItem();
      boolean isdenodo = from_sqs != null && from_sqs.isDenodo();
      if (this.orderSpecifier != null) {
         this.orderSpecifier.setIsOrderItem(true);
         oi.setOrderSpecifier(this.orderSpecifier.toOracleSelect(to_sqs, from_sqs));
      }

      oi.setObjectContext(this.context);
      if (this.Order != null && this.Order.equalsIgnoreCase("DESC")) {
         oi.setOrder(this.Order);
         oi.setNullsOrder(isdenodo ? null : "NULLS LAST");
      } else if (this.Order != null) {
         oi.setOrder(this.Order);
         oi.setNullsOrder(isdenodo ? null : "NULLS FIRST");
      } else {
         oi.setNullsOrder(isdenodo ? null : "NULLS FIRST");
      }

      if (this.UsingOperator != null) {
         if (!this.UsingOperator.equalsIgnoreCase("<") && !this.UsingOperator.equalsIgnoreCase("<=") && !this.UsingOperator.equalsIgnoreCase("~") && !this.UsingOperator.equalsIgnoreCase("~*") && !this.UsingOperator.equalsIgnoreCase("!~") && !this.UsingOperator.equalsIgnoreCase("!~*") && !this.UsingOperator.equalsIgnoreCase("*")) {
            if (!this.UsingOperator.equalsIgnoreCase(">") && !this.UsingOperator.equalsIgnoreCase(">=") && !this.UsingOperator.equalsIgnoreCase("/")) {
               if (this.UsingOperator.equalsIgnoreCase("=") || this.UsingOperator.equalsIgnoreCase("<>") || this.UsingOperator.equalsIgnoreCase("!=")) {
                  oi = null;
               }
            } else {
               oi.setOrder("DESC");
            }
         } else {
            oi.setOrder("ASC");
         }
      }

      return oi;
   }

   public OrderItem toInformixSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      OrderItem oi = new OrderItem();
      if (this.orderSpecifier != null) {
         oi.setOrderSpecifier(this.orderSpecifier.toInformixSelect(to_sqs, from_sqs));
      }

      if (this.Order != null) {
         oi.setOrder(this.Order);
         oi.setNullsOrder(this.Order.equalsIgnoreCase("DESC") ? "NULLS LAST" : "NULLS FIRST");
      } else {
         oi.setNullsOrder("NULLS FIRST");
      }

      if (this.UsingOperator != null) {
         if (!this.UsingOperator.equalsIgnoreCase("<") && !this.UsingOperator.equalsIgnoreCase("<=") && !this.UsingOperator.equalsIgnoreCase("~") && !this.UsingOperator.equalsIgnoreCase("~*") && !this.UsingOperator.equalsIgnoreCase("!~") && !this.UsingOperator.equalsIgnoreCase("!~*") && !this.UsingOperator.equalsIgnoreCase("*")) {
            if (!this.UsingOperator.equalsIgnoreCase(">") && !this.UsingOperator.equalsIgnoreCase(">=") && !this.UsingOperator.equalsIgnoreCase("/")) {
               if (this.UsingOperator.equalsIgnoreCase("=") || this.UsingOperator.equalsIgnoreCase("<>") || this.UsingOperator.equalsIgnoreCase("!=")) {
                  oi = null;
               }
            } else {
               oi.setOrder("DESC");
            }
         } else {
            oi.setOrder("ASC");
         }
      }

      return oi;
   }

   public OrderItem toTimesTenSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      OrderItem oi = new OrderItem();
      if (this.orderSpecifier != null) {
         oi.setOrderSpecifier(this.orderSpecifier.toTimesTenSelect(to_sqs, from_sqs));
      }

      oi.setObjectContext(this.context);
      if (this.Order != null && !this.Order.equalsIgnoreCase("USING")) {
         if (this.nullsOrder != null) {
            oi.setOrder(this.Order + " " + this.nullsOrder);
         } else {
            oi.setOrder(this.Order);
         }
      } else if (this.nullsOrder != null) {
         oi.setOrder(this.nullsOrder);
      } else if (this.UsingOperator != null) {
         if (!this.UsingOperator.equalsIgnoreCase("<") && !this.UsingOperator.equalsIgnoreCase("<=") && !this.UsingOperator.equalsIgnoreCase("~") && !this.UsingOperator.equalsIgnoreCase("~*") && !this.UsingOperator.equalsIgnoreCase("!~") && !this.UsingOperator.equalsIgnoreCase("!~*") && !this.UsingOperator.equalsIgnoreCase("*")) {
            if (!this.UsingOperator.equalsIgnoreCase(">") && !this.UsingOperator.equalsIgnoreCase(">=") && !this.UsingOperator.equalsIgnoreCase("/")) {
               if (this.UsingOperator.equalsIgnoreCase("=") || this.UsingOperator.equalsIgnoreCase("<>") || this.UsingOperator.equalsIgnoreCase("!=")) {
                  oi = null;
               }
            } else {
               oi.setOrder("DESC");
            }
         } else {
            oi.setOrder("ASC");
         }
      }

      return oi;
   }

   public OrderItem toNetezzaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      OrderItem oi = new OrderItem();
      if (this.orderSpecifier != null) {
         oi.setOrderSpecifier(this.orderSpecifier.toNetezzaSelect(to_sqs, from_sqs));
      }

      if (this.Order != null && this.Order.equalsIgnoreCase("NULLS FIRST")) {
         this.Order = " ";
      } else if (this.Order != null && this.Order.equalsIgnoreCase("NULLS LAST")) {
         this.Order = " ";
      } else if (this.Order != null && !this.Order.equalsIgnoreCase("USING")) {
         oi.setOrder(this.Order);
      } else if (this.UsingOperator != null) {
         if (!this.UsingOperator.equalsIgnoreCase("<") && !this.UsingOperator.equalsIgnoreCase("<=") && !this.UsingOperator.equalsIgnoreCase("~") && !this.UsingOperator.equalsIgnoreCase("~*") && !this.UsingOperator.equalsIgnoreCase("!~") && !this.UsingOperator.equalsIgnoreCase("!~*") && !this.UsingOperator.equalsIgnoreCase("*")) {
            if (!this.UsingOperator.equalsIgnoreCase(">") && !this.UsingOperator.equalsIgnoreCase(">=") && !this.UsingOperator.equalsIgnoreCase("/")) {
               if (this.UsingOperator.equalsIgnoreCase("=") || this.UsingOperator.equalsIgnoreCase("<>") || this.UsingOperator.equalsIgnoreCase("!=")) {
                  oi = null;
               }
            } else {
               oi.setOrder("DESC");
            }
         } else {
            oi.setOrder("ASC");
         }
      }

      return oi;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.orderSpecifier != null) {
         this.orderSpecifier.setObjectContext(this.context);
         sb.append(this.orderSpecifier);
      }

      if (this.Order != null) {
         sb.append(" " + this.Order.toUpperCase());
      }

      if (this.nullsOrder != null) {
         sb.append(" " + this.nullsOrder.toUpperCase());
      }

      if (this.Order != null && this.Order.equalsIgnoreCase("using")) {
         sb.append(" " + this.UsingOperator.toUpperCase());
      }

      return sb.toString();
   }

   public OrderItem toVectorWiseSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      OrderItem oi = new OrderItem();
      if (this.orderSpecifier != null) {
         oi.setOrderSpecifier(this.orderSpecifier.toVectorWiseSelect(to_sqs, from_sqs));
      }

      if (this.Order != null && this.Order.equalsIgnoreCase("NULLS FIRST")) {
         oi.setOrder((String)null);
      } else if (this.Order != null && this.Order.equalsIgnoreCase("NULLS LAST")) {
         this.Order = " ";
      } else if (this.Order != null && !this.Order.equalsIgnoreCase("USING")) {
         oi.setOrder(this.Order);
      } else if (this.UsingOperator != null) {
         if (!this.UsingOperator.equalsIgnoreCase("<") && !this.UsingOperator.equalsIgnoreCase("<=") && !this.UsingOperator.equalsIgnoreCase("~") && !this.UsingOperator.equalsIgnoreCase("~*") && !this.UsingOperator.equalsIgnoreCase("!~") && !this.UsingOperator.equalsIgnoreCase("!~*") && !this.UsingOperator.equalsIgnoreCase("*")) {
            if (!this.UsingOperator.equalsIgnoreCase(">") && !this.UsingOperator.equalsIgnoreCase(">=") && !this.UsingOperator.equalsIgnoreCase("/")) {
               if (this.UsingOperator.equalsIgnoreCase("=") || this.UsingOperator.equalsIgnoreCase("<>") || this.UsingOperator.equalsIgnoreCase("!=")) {
                  oi = null;
               }
            } else {
               oi.setOrder("DESC");
            }
         } else {
            oi.setOrder("ASC");
         }
      }

      if (oi.getOrder() != null && oi.getOrder().equalsIgnoreCase("DESC")) {
         oi.setNullsOrder(" NULLS LAST");
      } else {
         oi.setNullsOrder(" NULLS FIRST");
      }

      return oi;
   }

   public OrderItem toReplaceTblCol(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      OrderItem oiConv = new OrderItem();
      if (this.orderSpecifier != null) {
         if (!this.isFromSelectStatement && this.orderSpecifier.getColumnExpression().size() == 1 && this.orderSpecifier.getColumnExpression().get(0) instanceof TableColumn && ((TableColumn)this.orderSpecifier.getColumnExpression().get(0)).getTableName() == null && CastingUtil.ContainsIgnoreCase(from_sqs.getAliasColumns(), GeneralUtil.trimIfAliasNameIsEnclosed(((TableColumn)this.orderSpecifier.getColumnExpression().get(0)).getColumnName()))) {
            oiConv.setOrderSpecifier(this.orderSpecifier);
         } else {
            oiConv.setOrderSpecifier(this.orderSpecifier.toReplaceTblCol(to_sqs, from_sqs));
         }
      }

      if (this.Order != null) {
         oiConv.setOrder(this.Order);
      }

      if (this.UsingOperator != null) {
         oiConv.setUsingOperator(this.UsingOperator);
      }

      return oiConv;
   }
}
