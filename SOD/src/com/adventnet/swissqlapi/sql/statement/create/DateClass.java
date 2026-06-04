package com.adventnet.swissqlapi.sql.statement.create;

import com.adventnet.swissqlapi.config.SwisSQLOptions;

public class DateClass implements Datatype {
   private String datatypeName;
   private String openBrace;
   private String closedBrace;
   private String size;
   private String withLocalTimeZone;
   private String arrayStr;

   public void setDatatypeName(String datatypeName) {
      this.datatypeName = datatypeName;
   }

   public void setOpenBrace(String openBrace) {
      this.openBrace = openBrace;
   }

   public void setClosedBrace(String closedBrace) {
      this.closedBrace = closedBrace;
   }

   public void setWithLocalTimeZone(String withLocalTimeZone) {
      this.withLocalTimeZone = withLocalTimeZone;
   }

   public void setSize(String size) {
      this.size = size;
   }

   public String getDatatypeName() {
      return this.datatypeName;
   }

   public String getSize() {
      return this.size;
   }

   public String getWithLocalTimeZone() {
      return this.withLocalTimeZone;
   }

   public String getClosedBrace() {
      return this.closedBrace;
   }

   public String getOpenBrace() {
      return this.openBrace;
   }

   public void toInformixString() {
      if (this.getDatatypeName() != null) {
         String changeIfxDatatype = this.getDatatypeName();
         if (changeIfxDatatype.equalsIgnoreCase("DATETIME")) {
            this.setDatatypeName("DATETIME YEAR TO FRACTION");
         } else if (changeIfxDatatype.equalsIgnoreCase("SMALLDATETIME")) {
            this.setDatatypeName("DATETIME YEAR TO DAY");
         }

         if (changeIfxDatatype.equalsIgnoreCase("TIMESTAMP")) {
            this.setDatatypeName("DATETIME YEAR TO FRACTION");
            if (this.getSize() != null) {
               this.setOpenBrace((String)null);
               this.setSize((String)null);
               this.setClosedBrace((String)null);
            }
         }

         if (this.getArray() != null) {
            this.setArray((String)null);
         }
      }

   }

   public void toDB2String() {
      if (this.getDatatypeName() != null) {
         String changeDB2Datatype = this.getDatatypeName();
         if (changeDB2Datatype.equalsIgnoreCase("TIMESTAMP") && this.getSize() != null) {
            this.setOpenBrace((String)null);
            this.setSize((String)null);
            this.setClosedBrace((String)null);
         }

         if (SwisSQLOptions.fromAccess && changeDB2Datatype.equalsIgnoreCase("DATETIME")) {
            this.setDatatypeName("TIMESTAMP");
            this.setOpenBrace((String)null);
            this.setSize((String)null);
            this.setClosedBrace((String)null);
         } else if (changeDB2Datatype.equalsIgnoreCase("DATETIME")) {
            this.setDatatypeName("TIMESTAMP");
         } else if (changeDB2Datatype.equalsIgnoreCase("SMALLDATETIME")) {
            this.setDatatypeName("TIMESTAMP");
         }

         if (this.getArray() != null) {
            this.setArray((String)null);
         }
      }

   }

   public void toOracleString() {
      String changeOracleDatatype = this.getDatatypeName();
      if (SwisSQLOptions.fromAccess && changeOracleDatatype.equalsIgnoreCase("DATETIME")) {
         this.setDatatypeName("DATE");
         this.setOpenBrace((String)null);
         this.setSize((String)null);
         this.setClosedBrace((String)null);
      }

      if (this.getDatatypeName() != null) {
         if (!this.getDatatypeName().equalsIgnoreCase("time") && !this.getDatatypeName().equalsIgnoreCase("timestamp")) {
            if (this.getDatatypeName().equalsIgnoreCase("datetime2")) {
               this.setDatatypeName("TIMESTAMP");
            } else if (this.getDatatypeName().equalsIgnoreCase("datetimeoffset")) {
               this.setDatatypeName("TIMESTAMP");
               this.setWithLocalTimeZone("WITH TIME ZONE");
            } else {
               this.setDatatypeName("DATE");
               if (this.getSize() != null) {
                  this.setOpenBrace((String)null);
                  this.setSize((String)null);
                  this.setClosedBrace((String)null);
               }

               if (this.getArray() != null) {
                  this.setArray((String)null);
               }
            }
         } else {
            this.setDatatypeName("TIMESTAMP");
         }
      }

   }

   public void toDenodoString() {
      String changeOracleDatatype = this.getDatatypeName();
      if (SwisSQLOptions.fromAccess && changeOracleDatatype.equalsIgnoreCase("DATETIME")) {
         this.setDatatypeName("DATE");
         this.setOpenBrace((String)null);
         this.setSize((String)null);
         this.setClosedBrace((String)null);
      }

      if (this.getDatatypeName() != null) {
         if (!this.getDatatypeName().equalsIgnoreCase("time") && !this.getDatatypeName().equalsIgnoreCase("timestamp")) {
            if (this.getDatatypeName().equalsIgnoreCase("datetime2")) {
               this.setDatatypeName("TIMESTAMP");
            } else if (this.getDatatypeName().equalsIgnoreCase("datetimeoffset")) {
               this.setDatatypeName("TIMESTAMP");
               this.setWithLocalTimeZone("WITH TIME ZONE");
            } else {
               this.setDatatypeName("DATE");
               if (this.getSize() != null) {
                  this.setOpenBrace((String)null);
                  this.setSize((String)null);
                  this.setClosedBrace((String)null);
               }

               if (this.getArray() != null) {
                  this.setArray((String)null);
               }
            }
         } else {
            this.setDatatypeName("TIMESTAMP");
         }
      }

   }

   public void toExcelString() {
   }

   public void toMsAccessJdbcString() {
      String changeMsAccessDatatype = this.getDatatypeName();
      if (changeMsAccessDatatype.equalsIgnoreCase("DATETIME")) {
         this.setDatatypeName("DATE");
         this.setOpenBrace((String)null);
         this.setSize((String)null);
         this.setClosedBrace((String)null);
      }

      if (this.getDatatypeName() != null) {
         if (this.getDatatypeName().equalsIgnoreCase("time")) {
            this.setDatatypeName("TIME");
         } else if (!this.getDatatypeName().equalsIgnoreCase("timestamp") && !this.getDatatypeName().equalsIgnoreCase("datetime2")) {
            if (this.getDatatypeName().equalsIgnoreCase("datetimeoffset")) {
               this.setDatatypeName("TIMESTAMP");
               this.setWithLocalTimeZone("WITH TIME ZONE");
            } else {
               this.setDatatypeName("DATE");
               if (this.getSize() != null) {
                  this.setOpenBrace((String)null);
                  this.setSize((String)null);
                  this.setClosedBrace((String)null);
               }

               if (this.getArray() != null) {
                  this.setArray((String)null);
               }
            }
         } else {
            this.setDatatypeName("TIMESTAMP");
         }
      }

   }

   public void toMSSQLServerString() {
      if (this.getDatatypeName() != null) {
         String changeSQLServerDatatype = this.getDatatypeName();
         if (SwisSQLOptions.fromAccess && changeSQLServerDatatype.equalsIgnoreCase("DATETIME")) {
            this.setDatatypeName(changeSQLServerDatatype);
            this.setOpenBrace((String)null);
            this.setSize((String)null);
            this.setClosedBrace((String)null);
         } else if (!changeSQLServerDatatype.equalsIgnoreCase("TIMESTAMP")) {
            if (changeSQLServerDatatype.equalsIgnoreCase("DATE")) {
               this.setDatatypeName("DATETIME");
            } else if (changeSQLServerDatatype.equalsIgnoreCase("TIME")) {
               this.setDatatypeName("DATETIME");
            }
         } else {
            if (!SwisSQLOptions.fromSybase && !SwisSQLOptions.fromSQLServer) {
               this.setDatatypeName("DATETIME");
            } else {
               this.setDatatypeName("TIMESTAMP");
            }

            if (this.getSize() != null) {
               this.setOpenBrace((String)null);
               this.setSize((String)null);
               this.setClosedBrace((String)null);
            }
         }

         if (this.getArray() != null) {
            this.setArray((String)null);
         }
      }

   }

   public void toSybaseString() {
      if (this.getDatatypeName() != null) {
         String changeSybaseDatatype = this.getDatatypeName();
         if (SwisSQLOptions.fromAccess && changeSybaseDatatype.equalsIgnoreCase("DATETIME")) {
            this.setDatatypeName(changeSybaseDatatype);
            this.setOpenBrace((String)null);
            this.setSize((String)null);
            this.setClosedBrace((String)null);
         } else if (changeSybaseDatatype.equalsIgnoreCase("TIMESTAMP")) {
            this.setDatatypeName("DATETIME");
            if (this.getSize() != null) {
               this.setOpenBrace((String)null);
               this.setSize((String)null);
               this.setClosedBrace((String)null);
            }
         } else if (changeSybaseDatatype.equalsIgnoreCase("DATE")) {
            this.setDatatypeName("DATETIME");
         } else if (changeSybaseDatatype.equalsIgnoreCase("TIME")) {
            this.setDatatypeName("DATETIME");
         }

         if (this.getArray() != null) {
            this.setArray((String)null);
         }
      }

   }

   public void toPostgreSQLString() {
      if (this.getDatatypeName() != null) {
         String changePostgreSQLDatatype = this.getDatatypeName();
         if (changePostgreSQLDatatype.equalsIgnoreCase("TIMESTAMP")) {
            this.setDatatypeName("TIMESTAMP");
         } else if (changePostgreSQLDatatype.equalsIgnoreCase("DATETIME")) {
            this.setDatatypeName("TIMESTAMP");
         } else if (changePostgreSQLDatatype.equalsIgnoreCase("SMALLDATETIME")) {
            this.setDatatypeName("TIMESTAMP");
         }
      }

   }

   public void toBigQueryString() {
      if (this.getDatatypeName() != null) {
         String changeBigQueryDatatype = this.getDatatypeName();
         if (changeBigQueryDatatype.equalsIgnoreCase("TIMESTAMP")) {
            this.setDatatypeName("TIMESTAMP");
         } else if (changeBigQueryDatatype.equalsIgnoreCase("DATETIME")) {
            this.setDatatypeName("TIMESTAMP");
         } else if (changeBigQueryDatatype.equalsIgnoreCase("SMALLDATETIME")) {
            this.setDatatypeName("TIMESTAMP");
         }
      }

   }

   public void toMySQLString() {
      if (this.getDatatypeName() != null) {
         String changeSQLServerDatatype = this.getDatatypeName();
         if (SwisSQLOptions.fromAccess && changeSQLServerDatatype.equalsIgnoreCase("DATETIME")) {
            this.setDatatypeName(changeSQLServerDatatype);
            this.setOpenBrace((String)null);
            this.setSize((String)null);
            this.setClosedBrace((String)null);
         } else if (changeSQLServerDatatype.equalsIgnoreCase("DATE")) {
            this.setDatatypeName("DATETIME");
         } else if (changeSQLServerDatatype.equalsIgnoreCase("TIME")) {
            this.setDatatypeName("DATETIME");
         } else if (changeSQLServerDatatype.equalsIgnoreCase("SMALLDATETIME")) {
            this.setDatatypeName("DATETIME");
         }

         if (this.getArray() != null) {
            this.setArray((String)null);
         }
      }

   }

   public void toSnowflakeString() {
      if (this.getDatatypeName() != null) {
         String changeSnowflakeDatatype = this.getDatatypeName();
         if (changeSnowflakeDatatype.equalsIgnoreCase("TIMESTAMP")) {
            this.setDatatypeName("TIMESTAMP");
         } else if (changeSnowflakeDatatype.equalsIgnoreCase("DATETIME")) {
            this.setDatatypeName("TIMESTAMP");
         } else if (changeSnowflakeDatatype.equalsIgnoreCase("SMALLDATETIME")) {
            this.setDatatypeName("TIMESTAMP");
         }
      }

   }

   public void toSapHanaString() {
      if (this.getDatatypeName() != null) {
         String changeSapHanaDatatype = this.getDatatypeName();
         if (changeSapHanaDatatype.equalsIgnoreCase("TIMESTAMP")) {
            this.setDatatypeName("TIMESTAMP");
         } else if (changeSapHanaDatatype.equalsIgnoreCase("SECONDDATE")) {
            this.setDatatypeName("TIMESTAMP");
         }
      }

   }

   public void toSqliteString() {
      if (this.getDatatypeName() != null) {
         String changeSqliteDatatype = this.getDatatypeName();
         if (changeSqliteDatatype.equalsIgnoreCase("TIMESTAMP") || changeSqliteDatatype.equalsIgnoreCase("SECONDDATE")) {
            this.setDatatypeName("TEXT");
         }
      }

   }

   public void toAthenaString() {
      if (this.getDatatypeName() != null) {
         String changeAthenaDatatype = this.getDatatypeName();
         if (changeAthenaDatatype.equalsIgnoreCase("TIMESTAMP")) {
            this.setDatatypeName("TIMESTAMP");
         } else if (changeAthenaDatatype.equalsIgnoreCase("DATETIME")) {
            this.setDatatypeName("TIMESTAMP");
         } else if (changeAthenaDatatype.equalsIgnoreCase("SMALLDATETIME")) {
            this.setDatatypeName("TIMESTAMP");
         }
      }

   }

   public void toANSIString() {
      if (this.getDatatypeName() != null) {
         String changeANSIDatatype = this.getDatatypeName();
         if (changeANSIDatatype.equalsIgnoreCase("TIMESTAMP")) {
            if (this.getSize() != null) {
               this.setOpenBrace((String)null);
               this.setSize((String)null);
               this.setClosedBrace((String)null);
            }
         } else if (changeANSIDatatype.equalsIgnoreCase("DATETIME")) {
            this.setDatatypeName("TIMESTAMP");
         } else if (changeANSIDatatype.equalsIgnoreCase("SMALLDATETIME")) {
            this.setDatatypeName("TIMESTAMP");
         }

         if (this.getArray() != null) {
            this.setArray((String)null);
         }
      }

   }

   public void toTimesTenString() {
      if (this.getDatatypeName() != null) {
         String sourceType = this.getDatatypeName();
         if (sourceType.equalsIgnoreCase("DATETIME")) {
            this.setDatatypeName("TIMESTAMP");
         } else if (sourceType.equalsIgnoreCase("SMALLDATETIME")) {
            this.setDatatypeName("TIMESTAMP");
         } else if (sourceType.equalsIgnoreCase("TIMESTAMP")) {
            this.setOpenBrace((String)null);
            this.setSize((String)null);
            this.setClosedBrace((String)null);
            this.setWithLocalTimeZone((String)null);
         }

         if (this.getArray() != null) {
            this.setArray((String)null);
         }
      }

   }

   public void toNetezzaString() {
      if (this.getDatatypeName() != null) {
         String changeNetezzaDatatype = this.getDatatypeName();
         if (changeNetezzaDatatype.equalsIgnoreCase("TIMESTAMP")) {
            if (this.getSize() != null) {
               this.setOpenBrace((String)null);
               this.setSize((String)null);
               this.setClosedBrace((String)null);
               this.setWithLocalTimeZone((String)null);
            }
         } else if (changeNetezzaDatatype.equalsIgnoreCase("DATETIME")) {
            this.setDatatypeName("TIMESTAMP");
         } else if (changeNetezzaDatatype.equalsIgnoreCase("SMALLDATETIME")) {
            this.setDatatypeName("TIMESTAMP");
         }

         if (this.getArray() != null) {
            this.setArray((String)null);
         }
      }

   }

   public void toTeradataString() {
      if (this.getDatatypeName() != null) {
         String changeTeradataDatatype = this.getDatatypeName();
         if (changeTeradataDatatype.equalsIgnoreCase("DATETIME")) {
            this.setDatatypeName("TIMESTAMP");
         } else if (changeTeradataDatatype.equalsIgnoreCase("SMALLDATETIME")) {
            this.setDatatypeName("TIMESTAMP");
         } else if (changeTeradataDatatype.equalsIgnoreCase("DATE")) {
            this.setDatatypeName("TIMESTAMP");
            this.setOpenBrace("(");
            this.setSize("0");
            this.setClosedBrace(")");
         }

         if (this.getArray() != null) {
            this.setArray((String)null);
         }
      }

   }

   public Datatype copyObjectValues() {
      DateClass newDateClass = new DateClass();
      newDateClass.setClosedBrace(this.closedBrace);
      newDateClass.setDatatypeName(this.getDatatypeName());
      newDateClass.setOpenBrace(this.openBrace);
      newDateClass.setSize(this.getSize());
      newDateClass.setArray(this.getArray());
      return newDateClass;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.datatypeName != null) {
         sb.append(this.datatypeName + " ");
      }

      if (this.openBrace != null) {
         sb.append(this.openBrace);
      }

      if (this.size != null) {
         sb.append(this.size);
      }

      if (this.closedBrace != null) {
         sb.append(this.closedBrace);
      }

      if (this.arrayStr != null) {
         sb.append(this.arrayStr);
      }

      if (this.withLocalTimeZone != null) {
         sb.append(" " + this.withLocalTimeZone + " ");
      }

      return sb.toString();
   }

   public void setArray(String arrayStr) {
   }

   public String getArray() {
      return this.arrayStr;
   }
}
