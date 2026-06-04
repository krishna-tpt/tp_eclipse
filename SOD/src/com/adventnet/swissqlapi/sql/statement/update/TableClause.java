package com.adventnet.swissqlapi.sql.statement.update;

import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;

public class TableClause {
   private TableObject tableObject = null;
   private String dblink = null;
   private String partition = null;
   private String subPartition = null;
   private String alias = null;
   private boolean toMSSQLServer;
   private UserObjectContext context = null;

   public void setObjectContext(UserObjectContext context) {
      this.context = context;
   }

   public void setTableObject(TableObject to) {
      this.tableObject = to;
   }

   public TableObject getTableObject() {
      return this.tableObject;
   }

   public void setdblink(String s) {
      this.dblink = s;
   }

   public void setSubPartition(String s) {
      this.subPartition = s;
   }

   public void setPartition(String s) {
      this.partition = s;
   }

   public void setAlias(String s) {
      this.alias = s;
   }

   public String getAlias() {
      return this.alias;
   }

   public void setToMSSQLServer(boolean toMSSQLServer) {
      this.toMSSQLServer = toMSSQLServer;
   }

   public String getdblink() {
      return this.dblink;
   }

   public String getSubPartition() {
      return this.subPartition;
   }

   public String getPartition() {
      return this.partition;
   }

   public void toMySQL() throws ConvertException {
      if (this.tableObject != null) {
         String userName;
         if (this.tableObject.getTableName() != null) {
            userName = this.tableObject.getTableName();
            if (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("\"") && userName.endsWith("\"")) {
               userName = userName.substring(1, userName.length() - 1);
            }

            if (!userName.startsWith("`") && userName.indexOf(32) != -1) {
               userName = "`" + userName + "`";
            }

            this.tableObject.setTableName(userName);
         }

         if (this.tableObject.getUser() != null) {
            userName = this.tableObject.getUser();
            if (userName.startsWith("[") && userName.endsWith("]")) {
               userName = userName.substring(1, userName.length() - 1);
            }

            if (userName.indexOf(32) != -1) {
               userName = "`" + userName + "`";
            }

            this.tableObject.setUser(userName);
         }

         this.tableObject.toMySQL();
      }

   }

   public void toOracle() throws ConvertException {
      if (this.tableObject != null) {
         String userName;
         if (this.tableObject.getTableName() != null) {
            userName = this.tableObject.getTableName();
            if (this.tableObject.getDatabaseName() != null) {
               String databaseName = this.tableObject.getDatabaseName();
               if (userName.startsWith("#")) {
                  userName = userName.substring(1);
               }

               if (databaseName.startsWith("@")) {
                  databaseName = "\"" + databaseName + "\"";
               }

               this.tableObject.setTableName(userName);
               this.tableObject.setDatabaseName(databaseName);
            }

            if (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`")) {
               userName = userName.substring(1, userName.length() - 1);
            }

            if (!userName.startsWith("\"") && (SwisSQLOptions.retainQuotedIdentifierForOracle || userName.indexOf(32) != -1)) {
               userName = "\"" + userName + "\"";
            }

            this.tableObject.setTableName(userName);
         }

         if (this.tableObject.getUser() != null) {
            userName = this.tableObject.getUser();
            if (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`")) {
               userName = userName.substring(1, userName.length() - 1);
            }

            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }

            this.tableObject.setUser(userName);
         }

         this.tableObject.toOracle();
      }

   }

   public void toMSSQLServer() throws ConvertException {
      if (this.tableObject != null) {
         String userName;
         if (this.tableObject.getTableName() != null) {
            userName = this.tableObject.getTableName();
            if (userName.startsWith("`") && userName.endsWith("`")) {
               userName = userName.substring(1, userName.length() - 1);
            }

            if (!userName.startsWith("\"") && !userName.startsWith("[") && userName.indexOf(32) != -1) {
               userName = "[" + userName + "]";
            }

            this.tableObject.setTableName(userName);
         }

         if (this.tableObject.getUser() != null) {
            userName = this.tableObject.getUser();
            if (userName.startsWith("`") && userName.endsWith("`")) {
               userName = userName.substring(1, userName.length() - 1);
            }

            if (userName.indexOf(32) != -1) {
               userName = "[" + userName + "]";
            }

            this.tableObject.setUser(userName);
         }

         this.tableObject.toMSSQLServer();
      }

   }

   public void toSybase() throws ConvertException {
      if (this.tableObject != null) {
         String userName;
         if (this.tableObject.getTableName() != null) {
            userName = this.tableObject.getTableName();
            if (userName.startsWith("`") && userName.endsWith("`")) {
               userName = userName.substring(1, userName.length() - 1);
            }

            if (!userName.startsWith("\"") && !userName.startsWith("[") && userName.indexOf(32) != -1) {
               userName = "[" + userName + "]";
            }

            this.tableObject.setTableName(userName);
         }

         if (this.tableObject.getUser() != null) {
            userName = this.tableObject.getUser();
            if (userName.startsWith("`") && userName.endsWith("`")) {
               userName = userName.substring(1, userName.length() - 1);
            }

            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }

            this.tableObject.setUser(userName);
         }

         this.tableObject.toSybase();
      }

   }

   public void toPostgreSQL() throws ConvertException {
      if (this.tableObject != null) {
         this.tableObject.toPostgreSQL();
      }

   }

   public void toBigQuery() throws ConvertException {
      if (this.tableObject != null) {
         this.tableObject.toBigQuery();
      }

   }

   public void toDB2() throws ConvertException {
      if (this.tableObject != null) {
         String userName;
         if (this.tableObject.getTableName() != null) {
            userName = this.tableObject.getTableName();
            if (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`")) {
               userName = userName.substring(1, userName.length() - 1);
            }

            if (!userName.startsWith("\"") && userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }

            this.tableObject.setTableName(userName);
         }

         if (this.tableObject.getUser() != null) {
            userName = this.tableObject.getUser();
            if (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`")) {
               userName = userName.substring(1, userName.length() - 1);
            }

            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }

            this.tableObject.setUser(userName);
         }

         this.tableObject.toDB2();
      }

   }

   public void toSnowflake() throws ConvertException {
      if (this.tableObject != null) {
         this.tableObject.toSnowflake();
      }

   }

   public void toInformix() throws ConvertException {
      if (this.tableObject != null) {
         String userName;
         if (this.tableObject.getTableName() != null) {
            userName = this.tableObject.getTableName();
            if (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`")) {
               userName = userName.substring(1, userName.length() - 1);
            }

            if (!userName.startsWith("\"") && userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }

            this.tableObject.setTableName(userName);
         }

         if (this.tableObject.getUser() != null) {
            userName = this.tableObject.getUser();
            if (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`")) {
               userName = userName.substring(1, userName.length() - 1);
            }

            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }

            this.tableObject.setUser(userName);
         }

         this.tableObject.toInformix();
      }

   }

   public void toANSISQL() throws ConvertException {
      if (this.tableObject != null) {
         String userName;
         if (this.tableObject.getTableName() != null) {
            userName = this.tableObject.getTableName();
            if (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`")) {
               userName = userName.substring(1, userName.length() - 1);
            }

            if (!userName.startsWith("\"") && userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }

            this.tableObject.setTableName(userName);
         }

         if (this.tableObject.getUser() != null) {
            userName = this.tableObject.getUser();
            if (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`")) {
               userName = userName.substring(1, userName.length() - 1);
            }

            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }

            this.tableObject.setUser(userName);
         }

         this.tableObject.toANSISQL();
      }

   }

   public void toTeradata() throws ConvertException {
      if (this.tableObject != null) {
         String userName;
         if (this.tableObject.getTableName() != null) {
            userName = this.tableObject.getTableName();
            if (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`")) {
               userName = userName.substring(1, userName.length() - 1);
            }

            if (!userName.startsWith("\"") && userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }

            this.tableObject.setTableName(userName);
         }

         if (this.tableObject.getUser() != null) {
            userName = this.tableObject.getUser();
            if (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`")) {
               userName = userName.substring(1, userName.length() - 1);
            }

            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }

            this.tableObject.setUser(userName);
         }

         this.tableObject.toTeradata();
      }

   }

   public void toTimesTen() throws ConvertException {
      if (this.tableObject != null) {
         String userName;
         if (this.tableObject.getTableName() != null) {
            userName = this.tableObject.getTableName();
            if (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`")) {
               userName = userName.substring(1, userName.length() - 1);
               if (userName.indexOf(32) != -1) {
                  userName = "\"" + userName + "\"";
               }
            }

            this.tableObject.setTableName(userName);
         }

         if (this.tableObject.getUser() != null) {
            userName = this.tableObject.getUser();
            if (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`")) {
               userName = userName.substring(1, userName.length() - 1);
            }

            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }

            this.tableObject.setUser(userName);
         }

         this.tableObject.toTimesTen();
      }

   }

   public void toNetezza() throws ConvertException {
      if (this.tableObject != null) {
         String userName;
         if (this.tableObject.getTableName() != null) {
            userName = this.tableObject.getTableName();
            if (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`")) {
               userName = userName.substring(1, userName.length() - 1);
            }

            if (!userName.startsWith("\"") && userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }

            this.tableObject.setTableName(userName);
         }

         if (this.tableObject.getUser() != null) {
            userName = this.tableObject.getUser();
            if (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`")) {
               userName = userName.substring(1, userName.length() - 1);
            }

            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }

            this.tableObject.setUser(userName);
         }

         if (this.dblink != null) {
            this.dblink = null;
         }

         if (this.partition != null) {
            this.partition = null;
         }

         if (this.subPartition != null) {
            this.subPartition = null;
         }

         this.tableObject.toNetezza();
      }

   }

   public String toString() {
      StringBuffer stringbuffer = new StringBuffer();
      if (this.tableObject != null) {
         if (this.context != null) {
            this.tableObject.setObjectContext(this.context);
         }

         if (!this.toMSSQLServer) {
            stringbuffer.append(this.tableObject.toString());
            stringbuffer.append(" ");
         } else if (this.alias != null) {
            stringbuffer.append("");
         } else {
            stringbuffer.append(this.tableObject.toString());
            stringbuffer.append(" ");
         }
      }

      if (this.dblink != null) {
         stringbuffer.append(this.dblink);
         stringbuffer.append(" ");
      }

      if (this.partition != null) {
         stringbuffer.append(this.partition);
         stringbuffer.append(" ");
      }

      if (this.subPartition != null) {
         stringbuffer.append(this.subPartition);
         stringbuffer.append(" ");
      }

      if (this.alias != null) {
         stringbuffer.append(this.alias);
         stringbuffer.append(" ");
      }

      return stringbuffer.toString();
   }
}
