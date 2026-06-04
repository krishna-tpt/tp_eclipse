package com.adventnet.swissqlapi.sql.statement.create;

import com.adventnet.swissqlapi.config.SwisSQLOptions;

public class BinClass implements Datatype {
   private String datatypeName;
   private String openBrace;
   private String closedBrace;
   private String size;
   private String varyingType;
   private String notlogged;
   private String arrayStr;

   public void setDatatypeName(String datatypeName) {
      this.datatypeName = datatypeName;
   }

   public void setVarying(String varyingType) {
      this.varyingType = varyingType;
   }

   public void setOpenBrace(String openBrace) {
      this.openBrace = openBrace;
   }

   public void setClosedBrace(String closedBrace) {
      this.closedBrace = closedBrace;
   }

   public void setSize(String size) {
      this.size = size;
   }

   public void setNotLogged(String notlogged) {
      this.notlogged = notlogged;
   }

   public String getDatatypeName() {
      return this.datatypeName;
   }

   public String getSize() {
      return this.size;
   }

   public String getVarying() {
      return this.varyingType;
   }

   public String getOpenBrace() {
      return this.openBrace;
   }

   public String getClosedBrace() {
      return this.closedBrace;
   }

   public String getNotLogged() {
      return this.notlogged;
   }

   public void toDB2String() {
      if (this.getVarying() != null) {
         this.setVarying((String)null);
      }

      if (this.getDatatypeName() != null) {
         String changeDB2Datatype = this.getDatatypeName();
         if (changeDB2Datatype.equalsIgnoreCase("BLOB")) {
            if (this.size != null) {
               if (!this.size.toUpperCase().endsWith("G")) {
                  if (this.size.toUpperCase().endsWith("M")) {
                     this.size = this.size.substring(0, this.size.length() - 1);
                     int precisionValue = Integer.parseInt(this.size);
                     if (precisionValue >= 1024 && precisionValue < 2000) {
                        this.setSize("1G");
                        this.setNotLogged(" NOT LOGGED");
                     } else if (precisionValue >= 2000) {
                        this.setSize("2G");
                        this.setNotLogged(" NOT LOGGED");
                     } else {
                        this.setSize(this.size + "M");
                     }
                  }
               } else {
                  String newSize = this.size.substring(0, this.size.length() - 1);
                  int value = Integer.parseInt(newSize);
                  if (value == 1) {
                     this.setSize("1G");
                  } else if (value == 2) {
                     this.setSize("2G");
                  }

                  this.setNotLogged(" NOT LOGGED");
               }
            } else {
               this.setDatatypeName("BLOB");
               this.setOpenBrace("(");
               this.setSize("2G");
               this.setClosedBrace(")");
               this.setNotLogged(" NOT LOGGED");
            }

            if (this.notlogged != null) {
               this.setNotLogged(" NOT LOGGED");
            }
         } else if (changeDB2Datatype.equalsIgnoreCase("LONGTEXT")) {
            this.setDatatypeName("CLOB");
            this.setOpenBrace("(");
            this.setSize("2G");
            this.setClosedBrace(")");
            this.setNotLogged(" NOT LOGGED");
         } else if (SwisSQLOptions.fromAccess && changeDB2Datatype.equalsIgnoreCase("BIT")) {
            this.setDatatypeName("NUM");
            this.setOpenBrace("(");
            this.setSize("1");
            this.setClosedBrace(")");
         } else if (!changeDB2Datatype.equalsIgnoreCase("LONGBLOB") && !changeDB2Datatype.equalsIgnoreCase("BFILE") && !changeDB2Datatype.equalsIgnoreCase("LONG RAW") && !changeDB2Datatype.equalsIgnoreCase("IMAGE") && !changeDB2Datatype.equalsIgnoreCase("BYTEA")) {
            if (changeDB2Datatype.equalsIgnoreCase("RAW")) {
               this.setDatatypeName("BLOB");
               this.setOpenBrace("(");
               this.setSize("2000");
               this.setClosedBrace(")");
            } else if (changeDB2Datatype.equalsIgnoreCase("MEDIUMBLOB")) {
               this.setDatatypeName("BLOB");
               this.setOpenBrace("(");
               this.setSize("16777215");
               this.setClosedBrace(")");
            } else if (changeDB2Datatype.equalsIgnoreCase("MEDIUMTEXT")) {
               this.setDatatypeName("CLOB");
               this.setOpenBrace("(");
               this.setSize("16777215");
               this.setClosedBrace(")");
            } else if (!changeDB2Datatype.equalsIgnoreCase("TINYBLOB") && !changeDB2Datatype.equalsIgnoreCase("TINYTEXT")) {
               if (changeDB2Datatype.equalsIgnoreCase("BINARY")) {
                  this.setDatatypeName("VARCHAR");
                  this.setOpenBrace((String)null);
                  this.setSize((String)null);
                  this.setClosedBrace((String)null);
               } else if (!changeDB2Datatype.equalsIgnoreCase("VARBINARY")) {
                  if (changeDB2Datatype.equalsIgnoreCase("BIT")) {
                     this.setDatatypeName("NUM(1)");
                  }
               } else {
                  this.setDatatypeName("BLOB");
                  this.setOpenBrace("(");
                  if (this.getSize() != null && this.getSize().equalsIgnoreCase("MAX")) {
                     this.setSize("2147483647");
                  } else {
                     this.setSize("8000");
                  }

                  this.setClosedBrace(")");
               }
            } else {
               this.setDatatypeName("CHAR");
            }
         } else {
            this.setDatatypeName("BLOB");
            this.setOpenBrace("(");
            this.setSize("2G");
            this.setClosedBrace(")");
            this.setNotLogged(" NOT LOGGED");
         }

         if (this.getArray() != null) {
            this.setArray((String)null);
         }
      }

   }

   public void toOracleString() {
      if (this.notlogged != null) {
         this.setNotLogged((String)null);
      }

      if (this.getVarying() != null) {
         this.setVarying((String)null);
      }

      if (this.getDatatypeName() != null) {
         String changeOracleDatatype = this.getDatatypeName();
         if (changeOracleDatatype.equalsIgnoreCase("BLOB")) {
            if (this.getSize() != null) {
               this.setOpenBrace((String)null);
               this.setSize((String)null);
               this.setClosedBrace((String)null);
            }
         } else if (SwisSQLOptions.fromAccess && changeOracleDatatype.equalsIgnoreCase("BIT")) {
            this.setDatatypeName("NUMBER");
            this.setOpenBrace("(");
            this.setSize("1");
            this.setClosedBrace(")");
         } else if (!changeOracleDatatype.equalsIgnoreCase("LONGBLOB") && !changeOracleDatatype.equalsIgnoreCase("LONGTEXT")) {
            if (!changeOracleDatatype.equalsIgnoreCase("MEDIUMBLOB") && !changeOracleDatatype.equalsIgnoreCase("MEDIUMTEXT")) {
               if (!changeOracleDatatype.equalsIgnoreCase("TINYBLOB") && !changeOracleDatatype.equalsIgnoreCase("TINYTEXT")) {
                  if (!changeOracleDatatype.equalsIgnoreCase("BINARY") && !changeOracleDatatype.equalsIgnoreCase("VARBINARY")) {
                     if (!changeOracleDatatype.equalsIgnoreCase("IMAGE") && !changeOracleDatatype.equalsIgnoreCase("BYTEA")) {
                        if (changeOracleDatatype.equalsIgnoreCase("BIT")) {
                           this.setDatatypeName("NUMBER");
                           this.setSize("1");
                           this.setOpenBrace("(");
                           this.setClosedBrace(")");
                        }
                     } else {
                        this.setDatatypeName("BLOB");
                        this.setOpenBrace((String)null);
                        this.setSize((String)null);
                        this.setClosedBrace((String)null);
                     }
                  } else {
                     if (this.getOpenBrace() == null) {
                        this.setDatatypeName("RAW");
                        this.setOpenBrace("(");
                        this.setSize("1");
                        this.setClosedBrace(")");
                     }

                     if (this.getSize() != null) {
                        if (this.getSize().equalsIgnoreCase("MAX")) {
                           this.setDatatypeName("BLOB");
                           this.setOpenBrace((String)null);
                           this.setSize((String)null);
                           this.setClosedBrace((String)null);
                        } else {
                           int siz = Integer.parseInt(this.getSize());
                           if (siz <= 2000) {
                              this.setDatatypeName("RAW");
                           } else {
                              this.setDatatypeName("BLOB");
                              this.setOpenBrace((String)null);
                              this.setSize((String)null);
                              this.setClosedBrace((String)null);
                           }
                        }
                     }
                  }
               } else {
                  this.setDatatypeName("CHAR");
               }
            } else {
               this.setDatatypeName("LONG");
            }
         } else {
            this.setDatatypeName("BLOB");
         }

         if (this.getArray() != null) {
            this.setArray((String)null);
         }
      }

   }

   public void toDenodoString() {
      if (this.notlogged != null) {
         this.setNotLogged((String)null);
      }

      if (this.getVarying() != null) {
         this.setVarying((String)null);
      }

      if (this.getDatatypeName() != null) {
         String changeDenodoDatatype = this.getDatatypeName();
         if (!changeDenodoDatatype.equalsIgnoreCase("BLOB") && !changeDenodoDatatype.equalsIgnoreCase("MEDIUMBLOB") && !changeDenodoDatatype.equalsIgnoreCase("LONGBLOB") && !changeDenodoDatatype.equalsIgnoreCase("TINYBLOB") && !changeDenodoDatatype.equalsIgnoreCase("LONG RAW") && !changeDenodoDatatype.equalsIgnoreCase("RAW") && !changeDenodoDatatype.equalsIgnoreCase("BINARY") && !changeDenodoDatatype.equalsIgnoreCase("VARBINARY") && !changeDenodoDatatype.equalsIgnoreCase("IMAGE") && !changeDenodoDatatype.equalsIgnoreCase("BFILE")) {
            if (!changeDenodoDatatype.equalsIgnoreCase("LONGTEXT") && !changeDenodoDatatype.equalsIgnoreCase("MEDIUMTEXT") && !changeDenodoDatatype.equalsIgnoreCase("CLOB") && !changeDenodoDatatype.equalsIgnoreCase("TINYTEXT")) {
               if (changeDenodoDatatype.equalsIgnoreCase("BIT")) {
                  this.setDatatypeName("INT8");
               } else if (changeDenodoDatatype.equalsIgnoreCase("BOOLEAN")) {
                  this.setDatatypeName("BOOL");
               } else if (changeDenodoDatatype.equalsIgnoreCase("BYTEA")) {
                  this.setDatatypeName("BYTEA");
               } else {
                  this.setDatatypeName("VARCHAR");
               }
            } else {
               if (this.getSize() != null) {
                  this.setOpenBrace((String)null);
                  this.setSize((String)null);
                  this.setClosedBrace((String)null);
               }

               this.setDatatypeName("VARCHAR");
            }
         } else {
            if (this.getSize() != null) {
               this.setOpenBrace((String)null);
               this.setSize((String)null);
               this.setClosedBrace((String)null);
            }

            this.setDatatypeName("BYTEA");
         }
      }

   }

   public void toMSSQLServerString() {
      if (this.notlogged != null) {
         this.setNotLogged((String)null);
      }

      if (this.getVarying() != null) {
         this.setVarying((String)null);
      }

      if (this.getDatatypeName() != null) {
         String changeSQLServerDatatype = this.getDatatypeName();
         if (!changeSQLServerDatatype.equalsIgnoreCase("BLOB") && !changeSQLServerDatatype.equalsIgnoreCase("MEDIUMBLOB") && !changeSQLServerDatatype.equalsIgnoreCase("LONGBLOB") && !changeSQLServerDatatype.equalsIgnoreCase("TINYBLOB") && !changeSQLServerDatatype.equalsIgnoreCase("LONG RAW") && !changeSQLServerDatatype.equalsIgnoreCase("BYTEA")) {
            if (SwisSQLOptions.fromAccess && changeSQLServerDatatype.equalsIgnoreCase("BIT")) {
               this.setDatatypeName(changeSQLServerDatatype);
               this.setOpenBrace((String)null);
               this.setSize((String)null);
               this.setClosedBrace((String)null);
            } else if (!changeSQLServerDatatype.equalsIgnoreCase("LONGTEXT") && !changeSQLServerDatatype.equalsIgnoreCase("MEDIUMTEXT") && !changeSQLServerDatatype.equalsIgnoreCase("BFILE")) {
               if (changeSQLServerDatatype.equalsIgnoreCase("RAW")) {
                  this.setDatatypeName("VARBINARY");
               } else if (changeSQLServerDatatype.equalsIgnoreCase("TINYTEXT")) {
                  this.setDatatypeName("CHAR");
               } else if (changeSQLServerDatatype.equalsIgnoreCase("BOOLEAN")) {
                  this.setDatatypeName("BOOLEAN");
               }
            } else {
               if (this.getSize() != null) {
                  this.setOpenBrace((String)null);
                  this.setSize((String)null);
                  this.setClosedBrace((String)null);
               }

               this.setDatatypeName("TEXT");
            }
         } else {
            if (this.getSize() != null) {
               this.setOpenBrace((String)null);
               this.setSize((String)null);
               this.setClosedBrace((String)null);
            }

            this.setDatatypeName("IMAGE");
         }

         if (this.getArray() != null) {
            this.setArray((String)null);
         }
      }

   }

   public void toExcelString() {
      if (this.notlogged != null) {
         this.setNotLogged((String)null);
      }

      if (this.getVarying() != null) {
         this.setVarying((String)null);
      }

      if (this.getSize() != null) {
         this.setOpenBrace((String)null);
         this.setSize((String)null);
         this.setClosedBrace((String)null);
      }

      if (this.getDatatypeName() != null) {
         String changeExcelDatatype = this.getDatatypeName();
         if (!changeExcelDatatype.equalsIgnoreCase("BLOB") && !changeExcelDatatype.equalsIgnoreCase("MEDIUMBLOB") && !changeExcelDatatype.equalsIgnoreCase("LONGBLOB") && !changeExcelDatatype.equalsIgnoreCase("TINYBLOB") && !changeExcelDatatype.equalsIgnoreCase("LONG RAW") && !changeExcelDatatype.equalsIgnoreCase("RAW") && !changeExcelDatatype.equalsIgnoreCase("BINARY") && !changeExcelDatatype.equalsIgnoreCase("VARBINARY") && !changeExcelDatatype.equalsIgnoreCase("IMAGE") && !changeExcelDatatype.equalsIgnoreCase("BFILE") && !changeExcelDatatype.equalsIgnoreCase("BYTEA")) {
            if (!changeExcelDatatype.equalsIgnoreCase("LONGTEXT") && !changeExcelDatatype.equalsIgnoreCase("MEDIUMTEXT") && !changeExcelDatatype.equalsIgnoreCase("CLOB") && !changeExcelDatatype.equalsIgnoreCase("TINYTEXT")) {
               if (!changeExcelDatatype.equalsIgnoreCase("BIT") && !changeExcelDatatype.equalsIgnoreCase("BOOLEAN")) {
                  this.setDatatypeName("Long Text");
               } else {
                  this.setDatatypeName("Large Number");
               }
            } else {
               if (this.getSize() != null) {
                  this.setOpenBrace((String)null);
                  this.setSize((String)null);
                  this.setClosedBrace((String)null);
               }

               this.setDatatypeName("Long Text");
            }
         } else {
            this.setDatatypeName("OLE Object");
         }
      }

   }

   public void toMsAccessJdbcString() {
      if (this.notlogged != null) {
         this.setNotLogged((String)null);
      }

      if (this.getVarying() != null) {
         this.setVarying((String)null);
      }

      if (this.getSize() != null) {
         this.setOpenBrace((String)null);
         this.setSize((String)null);
         this.setClosedBrace((String)null);
      }

      if (this.getDatatypeName() != null) {
         String changeMsAccessDatatype = this.getDatatypeName();
         if (!changeMsAccessDatatype.equalsIgnoreCase("BLOB") && !changeMsAccessDatatype.equalsIgnoreCase("MEDIUMBLOB") && !changeMsAccessDatatype.equalsIgnoreCase("LONGBLOB") && !changeMsAccessDatatype.equalsIgnoreCase("TINYBLOB") && !changeMsAccessDatatype.equalsIgnoreCase("LONG RAW") && !changeMsAccessDatatype.equalsIgnoreCase("RAW") && !changeMsAccessDatatype.equalsIgnoreCase("BINARY") && !changeMsAccessDatatype.equalsIgnoreCase("VARBINARY") && !changeMsAccessDatatype.equalsIgnoreCase("IMAGE") && !changeMsAccessDatatype.equalsIgnoreCase("BFILE") && !changeMsAccessDatatype.equalsIgnoreCase("BYTEA")) {
            if (!changeMsAccessDatatype.equalsIgnoreCase("LONGTEXT") && !changeMsAccessDatatype.equalsIgnoreCase("MEDIUMTEXT") && !changeMsAccessDatatype.equalsIgnoreCase("CLOB") && !changeMsAccessDatatype.equalsIgnoreCase("TINYTEXT")) {
               if (!changeMsAccessDatatype.equalsIgnoreCase("BIT") && !changeMsAccessDatatype.equalsIgnoreCase("BOOLEAN")) {
                  this.setDatatypeName("LONGVARCHAR");
               } else {
                  this.setDatatypeName("INTEGER");
               }
            } else {
               if (this.getSize() != null) {
                  this.setOpenBrace((String)null);
                  this.setSize((String)null);
                  this.setClosedBrace((String)null);
               }

               this.setDatatypeName("LONGVARCHAR");
            }
         } else {
            this.setDatatypeName("BLOB");
         }
      }

   }

   public void toSybaseString() {
      if (this.notlogged != null) {
         this.setNotLogged((String)null);
      }

      if (this.getVarying() != null) {
         this.setVarying((String)null);
      }

      if (this.getDatatypeName() != null) {
         String changeSQLServerDatatype = this.getDatatypeName();
         if (!changeSQLServerDatatype.equalsIgnoreCase("BLOB") && !changeSQLServerDatatype.equalsIgnoreCase("LONGBLOB") && !changeSQLServerDatatype.equalsIgnoreCase("MEDIUMBLOB") && !changeSQLServerDatatype.equalsIgnoreCase("ORACLEBLOB") && !changeSQLServerDatatype.equalsIgnoreCase("LONG RAW") && !changeSQLServerDatatype.equalsIgnoreCase("TINYBLOB") && !changeSQLServerDatatype.equalsIgnoreCase("BYTEA")) {
            if (SwisSQLOptions.fromAccess && changeSQLServerDatatype.equalsIgnoreCase("BIT")) {
               this.setDatatypeName(changeSQLServerDatatype);
               this.setOpenBrace((String)null);
               this.setSize((String)null);
               this.setClosedBrace((String)null);
            } else if (!changeSQLServerDatatype.equalsIgnoreCase("LONGTEXT") && !changeSQLServerDatatype.equalsIgnoreCase("MEDIUMTEXT") && !changeSQLServerDatatype.equalsIgnoreCase("BFILE")) {
               if (changeSQLServerDatatype.equalsIgnoreCase("RAW")) {
                  this.setDatatypeName("VARBINARY");
               } else if (changeSQLServerDatatype.equalsIgnoreCase("TINYTEXT")) {
                  this.setDatatypeName("CHAR");
                  this.setOpenBrace("(");
                  this.setSize("255");
                  this.setClosedBrace(")");
               } else if (changeSQLServerDatatype.equalsIgnoreCase("BOOLEAN")) {
                  this.setDatatypeName("BIT");
               }
            } else {
               if (this.getSize() != null) {
                  this.setOpenBrace((String)null);
                  this.setSize((String)null);
                  this.setClosedBrace((String)null);
               }

               this.setDatatypeName("TEXT");
            }
         } else {
            if (this.getSize() != null) {
               this.setOpenBrace((String)null);
               this.setSize((String)null);
               this.setClosedBrace((String)null);
            }

            this.setDatatypeName("IMAGE");
         }

         if (this.getArray() != null) {
            this.setArray((String)null);
         }
      }

   }

   public void toBigQueryString() {
      if (this.notlogged != null) {
         this.setNotLogged((String)null);
      }

      if (this.getVarying() != null) {
         this.setVarying((String)null);
      }

      if (this.getSize() != null) {
         this.setOpenBrace((String)null);
         this.setSize((String)null);
         this.setClosedBrace((String)null);
      }

      if (this.getDatatypeName() != null) {
         String changeBigQueryDatatype = this.getDatatypeName();
         if (!changeBigQueryDatatype.equalsIgnoreCase("BLOB") && !changeBigQueryDatatype.equalsIgnoreCase("MEDIUMBLOB") && !changeBigQueryDatatype.equalsIgnoreCase("LONGBLOB") && !changeBigQueryDatatype.equalsIgnoreCase("TINYBLOB") && !changeBigQueryDatatype.equalsIgnoreCase("LONG RAW") && !changeBigQueryDatatype.equalsIgnoreCase("RAW") && !changeBigQueryDatatype.equalsIgnoreCase("BINARY") && !changeBigQueryDatatype.equalsIgnoreCase("VARBINARY") && !changeBigQueryDatatype.equalsIgnoreCase("IMAGE") && !changeBigQueryDatatype.equalsIgnoreCase("BFILE")) {
            if (!changeBigQueryDatatype.equalsIgnoreCase("LONGTEXT") && !changeBigQueryDatatype.equalsIgnoreCase("MEDIUMTEXT") && !changeBigQueryDatatype.equalsIgnoreCase("CLOB") && !changeBigQueryDatatype.equalsIgnoreCase("TINYTEXT")) {
               if (changeBigQueryDatatype.equalsIgnoreCase("BIT")) {
                  this.setDatatypeName("int64");
               } else if (changeBigQueryDatatype.equalsIgnoreCase("BOOLEAN")) {
                  this.setDatatypeName("BOOL");
               } else if (changeBigQueryDatatype.equalsIgnoreCase("BYTEA")) {
                  this.setDatatypeName("BYTES");
               } else {
                  this.setDatatypeName("STRING");
               }
            } else {
               if (this.getSize() != null) {
                  this.setOpenBrace((String)null);
                  this.setSize((String)null);
                  this.setClosedBrace((String)null);
               }

               this.setDatatypeName("STRING");
            }
         } else {
            if (this.getSize() != null) {
               this.setOpenBrace((String)null);
               this.setSize((String)null);
               this.setClosedBrace((String)null);
            }

            this.setDatatypeName("BYTES");
         }
      }

   }

   public void toPostgreSQLString() {
      if (this.notlogged != null) {
         this.setNotLogged((String)null);
      }

      if (this.getVarying() != null) {
         this.setVarying((String)null);
      }

      if (this.getSize() != null) {
         this.setOpenBrace((String)null);
         this.setSize((String)null);
         this.setClosedBrace((String)null);
      }

      if (this.getDatatypeName() != null) {
         String changePostgreSQLDatatype = this.getDatatypeName();
         if (!changePostgreSQLDatatype.equalsIgnoreCase("BLOB") && !changePostgreSQLDatatype.equalsIgnoreCase("MEDIUMBLOB") && !changePostgreSQLDatatype.equalsIgnoreCase("LONGBLOB") && !changePostgreSQLDatatype.equalsIgnoreCase("TINYBLOB") && !changePostgreSQLDatatype.equalsIgnoreCase("LONG RAW") && !changePostgreSQLDatatype.equalsIgnoreCase("RAW") && !changePostgreSQLDatatype.equalsIgnoreCase("BINARY") && !changePostgreSQLDatatype.equalsIgnoreCase("VARBINARY") && !changePostgreSQLDatatype.equalsIgnoreCase("IMAGE") && !changePostgreSQLDatatype.equalsIgnoreCase("BFILE")) {
            if (!changePostgreSQLDatatype.equalsIgnoreCase("LONGTEXT") && !changePostgreSQLDatatype.equalsIgnoreCase("MEDIUMTEXT") && !changePostgreSQLDatatype.equalsIgnoreCase("CLOB") && !changePostgreSQLDatatype.equalsIgnoreCase("TINYTEXT")) {
               if (changePostgreSQLDatatype.equalsIgnoreCase("BIT")) {
                  this.setDatatypeName("smallint");
               } else if (changePostgreSQLDatatype.equalsIgnoreCase("BOOLEAN")) {
                  this.setDatatypeName("BOOL");
               } else if (changePostgreSQLDatatype.equalsIgnoreCase("BYTEA")) {
                  this.setDatatypeName("BYTEA");
               } else {
                  this.setDatatypeName("TEXT");
               }
            } else {
               if (this.getSize() != null) {
                  this.setOpenBrace((String)null);
                  this.setSize((String)null);
                  this.setClosedBrace((String)null);
               }

               this.setDatatypeName("TEXT");
            }
         } else {
            if (this.getSize() != null) {
               this.setOpenBrace((String)null);
               this.setSize((String)null);
               this.setClosedBrace((String)null);
            }

            this.setDatatypeName("BYTEA");
         }
      }

   }

   public void toMySQLString() {
      if (this.notlogged != null) {
         this.setNotLogged((String)null);
      }

      if (this.getVarying() != null) {
         this.setVarying((String)null);
      }

      if (this.getSize() != null) {
         this.setOpenBrace((String)null);
         this.setSize((String)null);
         this.setClosedBrace((String)null);
      }

      if (this.getDatatypeName() != null) {
         String changeMySQLDatatype = this.getDatatypeName();
         if (SwisSQLOptions.fromAccess && changeMySQLDatatype.equalsIgnoreCase("BIT")) {
            this.setDatatypeName("INTEGER");
            this.setOpenBrace("(");
            this.setSize("1");
            this.setClosedBrace(")");
         } else if (!changeMySQLDatatype.equalsIgnoreCase("LONG RAW") && !changeMySQLDatatype.equalsIgnoreCase("RAW") && !changeMySQLDatatype.equalsIgnoreCase("BINARY") && !changeMySQLDatatype.equalsIgnoreCase("VARBINARY") && !changeMySQLDatatype.equalsIgnoreCase("IMAGE") && !changeMySQLDatatype.equalsIgnoreCase("BYTEA")) {
            if (changeMySQLDatatype.equalsIgnoreCase("BIT")) {
               this.setDatatypeName("TINYINT");
            } else if (changeMySQLDatatype.equalsIgnoreCase("BFILE")) {
               this.setDatatypeName("LONGBLOB");
            } else if (changeMySQLDatatype.equalsIgnoreCase("BOOLEAN")) {
               this.setDatatypeName("TINYINT");
               this.setOpenBrace("(");
               this.setSize("1");
               this.setClosedBrace(")");
            }
         } else {
            this.setDatatypeName("LONGBLOB");
         }

         if (this.getArray() != null) {
            this.setArray((String)null);
         }
      }

   }

   public void toSnowflakeString() {
      if (this.notlogged != null) {
         this.setNotLogged((String)null);
      }

      if (this.getVarying() != null) {
         this.setVarying((String)null);
      }

      if (this.getSize() != null) {
         this.setOpenBrace((String)null);
         this.setSize((String)null);
         this.setClosedBrace((String)null);
      }

      if (this.getDatatypeName() != null) {
         String changeSnowflakeDatatype = this.getDatatypeName();
         if (!changeSnowflakeDatatype.equalsIgnoreCase("BLOB") && !changeSnowflakeDatatype.equalsIgnoreCase("MEDIUMBLOB") && !changeSnowflakeDatatype.equalsIgnoreCase("LONGBLOB") && !changeSnowflakeDatatype.equalsIgnoreCase("TINYBLOB") && !changeSnowflakeDatatype.equalsIgnoreCase("LONG RAW") && !changeSnowflakeDatatype.equalsIgnoreCase("RAW") && !changeSnowflakeDatatype.equalsIgnoreCase("BINARY") && !changeSnowflakeDatatype.equalsIgnoreCase("VARBINARY") && !changeSnowflakeDatatype.equalsIgnoreCase("IMAGE") && !changeSnowflakeDatatype.equalsIgnoreCase("BFILE")) {
            if (!changeSnowflakeDatatype.equalsIgnoreCase("LONGTEXT") && !changeSnowflakeDatatype.equalsIgnoreCase("MEDIUMTEXT") && !changeSnowflakeDatatype.equalsIgnoreCase("CLOB") && !changeSnowflakeDatatype.equalsIgnoreCase("TINYTEXT")) {
               if (changeSnowflakeDatatype.equalsIgnoreCase("BIT")) {
                  this.setDatatypeName("smallint");
               } else if (changeSnowflakeDatatype.equalsIgnoreCase("BOOLEAN")) {
                  this.setDatatypeName("BOOLEAN");
               } else if (changeSnowflakeDatatype.equalsIgnoreCase("BYTEA")) {
                  this.setDatatypeName("BINARY");
               } else {
                  this.setDatatypeName("TEXT");
               }
            } else {
               if (this.getSize() != null) {
                  this.setOpenBrace((String)null);
                  this.setSize((String)null);
                  this.setClosedBrace((String)null);
               }

               this.setDatatypeName("TEXT");
            }
         } else {
            this.setDatatypeName("BINARY");
         }
      }

   }

   public void toAthenaString() {
      if (this.notlogged != null) {
         this.setNotLogged((String)null);
      }

      if (this.getVarying() != null) {
         this.setVarying((String)null);
      }

      if (this.getSize() != null) {
         this.setOpenBrace((String)null);
         this.setSize((String)null);
         this.setClosedBrace((String)null);
      }

      if (this.getDatatypeName() != null) {
         String changeAthenaDatatype = this.getDatatypeName();
         if (!changeAthenaDatatype.equalsIgnoreCase("BLOB") && !changeAthenaDatatype.equalsIgnoreCase("MEDIUMBLOB") && !changeAthenaDatatype.equalsIgnoreCase("LONGBLOB") && !changeAthenaDatatype.equalsIgnoreCase("TINYBLOB") && !changeAthenaDatatype.equalsIgnoreCase("LONG RAW") && !changeAthenaDatatype.equalsIgnoreCase("RAW") && !changeAthenaDatatype.equalsIgnoreCase("BINARY") && !changeAthenaDatatype.equalsIgnoreCase("VARBINARY") && !changeAthenaDatatype.equalsIgnoreCase("IMAGE") && !changeAthenaDatatype.equalsIgnoreCase("BFILE")) {
            if (!changeAthenaDatatype.equalsIgnoreCase("LONGTEXT") && !changeAthenaDatatype.equalsIgnoreCase("MEDIUMTEXT") && !changeAthenaDatatype.equalsIgnoreCase("CLOB") && !changeAthenaDatatype.equalsIgnoreCase("TINYTEXT")) {
               if (changeAthenaDatatype.equalsIgnoreCase("BIT")) {
                  this.setDatatypeName("TINYINT");
               } else if (changeAthenaDatatype.equalsIgnoreCase("BOOLEAN")) {
                  this.setDatatypeName("BOOLEAN");
               } else if (changeAthenaDatatype.equalsIgnoreCase("BYTEA")) {
                  this.setDatatypeName("VARBINARY");
               } else {
                  this.setDatatypeName("STRING");
               }
            } else {
               if (this.getSize() != null) {
                  this.setOpenBrace((String)null);
                  this.setSize((String)null);
                  this.setClosedBrace((String)null);
               }

               this.setDatatypeName("STRING");
            }
         } else {
            if (this.getSize() != null) {
               this.setOpenBrace((String)null);
               this.setSize((String)null);
               this.setClosedBrace((String)null);
            }

            this.setDatatypeName("VARBINARY");
         }
      }

   }

   public void toSapHanaString() {
      if (this.notlogged != null) {
         this.setNotLogged((String)null);
      }

      if (this.getVarying() != null) {
         this.setVarying((String)null);
      }

      if (this.getSize() != null) {
         this.setOpenBrace((String)null);
         this.setSize((String)null);
         this.setClosedBrace((String)null);
      }

      if (this.getDatatypeName() != null) {
         String changeSapHanaDatatype = this.getDatatypeName();
         if (!changeSapHanaDatatype.equalsIgnoreCase("BLOB") && !changeSapHanaDatatype.equalsIgnoreCase("MEDIUMBLOB") && !changeSapHanaDatatype.equalsIgnoreCase("LONGBLOB") && !changeSapHanaDatatype.equalsIgnoreCase("TINYBLOB") && !changeSapHanaDatatype.equalsIgnoreCase("LONG RAW") && !changeSapHanaDatatype.equalsIgnoreCase("RAW") && !changeSapHanaDatatype.equalsIgnoreCase("BINARY") && !changeSapHanaDatatype.equalsIgnoreCase("VARBINARY") && !changeSapHanaDatatype.equalsIgnoreCase("IMAGE") && !changeSapHanaDatatype.equalsIgnoreCase("BFILE")) {
            if (!changeSapHanaDatatype.equalsIgnoreCase("LONGTEXT") && !changeSapHanaDatatype.equalsIgnoreCase("MEDIUMTEXT") && !changeSapHanaDatatype.equalsIgnoreCase("CLOB") && !changeSapHanaDatatype.equalsIgnoreCase("TINYTEXT")) {
               if (changeSapHanaDatatype.equalsIgnoreCase("BIT")) {
                  this.setDatatypeName("smallint");
               } else if (changeSapHanaDatatype.equalsIgnoreCase("BOOLEAN")) {
                  this.setDatatypeName("BOOLEAN");
               } else if (changeSapHanaDatatype.equalsIgnoreCase("BYTEA")) {
                  this.setDatatypeName("VARBINARY");
               } else {
                  this.setDatatypeName("TEXT");
               }
            } else {
               if (this.getSize() != null) {
                  this.setOpenBrace((String)null);
                  this.setSize((String)null);
                  this.setClosedBrace((String)null);
               }

               this.setDatatypeName("TEXT");
            }
         } else {
            this.setDatatypeName("VARBINARY");
         }
      }

   }

   public void toSqliteString() {
      if (this.notlogged != null) {
         this.setNotLogged((String)null);
      }

      if (this.getVarying() != null) {
         this.setVarying((String)null);
      }

      if (this.getSize() != null) {
         this.setOpenBrace((String)null);
         this.setSize((String)null);
         this.setClosedBrace((String)null);
      }

      if (this.getDatatypeName() != null) {
         String changeSqliteDatatype = this.getDatatypeName();
         if (!changeSqliteDatatype.equalsIgnoreCase("BLOB") && !changeSqliteDatatype.equalsIgnoreCase("MEDIUMBLOB") && !changeSqliteDatatype.equalsIgnoreCase("LONGBLOB") && !changeSqliteDatatype.equalsIgnoreCase("TINYBLOB") && !changeSqliteDatatype.equalsIgnoreCase("LONG RAW") && !changeSqliteDatatype.equalsIgnoreCase("RAW") && !changeSqliteDatatype.equalsIgnoreCase("BINARY") && !changeSqliteDatatype.equalsIgnoreCase("VARBINARY") && !changeSqliteDatatype.equalsIgnoreCase("IMAGE") && !changeSqliteDatatype.equalsIgnoreCase("BFILE") && !changeSqliteDatatype.equalsIgnoreCase("BYTEA")) {
            if (!changeSqliteDatatype.equalsIgnoreCase("LONGTEXT") && !changeSqliteDatatype.equalsIgnoreCase("MEDIUMTEXT") && !changeSqliteDatatype.equalsIgnoreCase("CLOB") && !changeSqliteDatatype.equalsIgnoreCase("TINYTEXT")) {
               if (!changeSqliteDatatype.equalsIgnoreCase("BIT") && !changeSqliteDatatype.equalsIgnoreCase("BOOLEAN")) {
                  this.setDatatypeName("TEXT");
               } else {
                  this.setDatatypeName("INTEGER");
               }
            } else {
               if (this.getSize() != null) {
                  this.setOpenBrace((String)null);
                  this.setSize((String)null);
                  this.setClosedBrace((String)null);
               }

               this.setDatatypeName("TEXT");
            }
         } else {
            this.setDatatypeName("BLOB");
         }
      }

   }

   public void toANSIString() {
      if (this.notlogged != null) {
         this.setNotLogged((String)null);
      }

      if (this.getVarying() != null) {
         this.setVarying((String)null);
      }

      if (this.getDatatypeName() != null) {
         String changeANSIDatatype = this.getDatatypeName();
         if (changeANSIDatatype.equalsIgnoreCase("BLOB")) {
            if (this.getSize() != null) {
               this.setOpenBrace((String)null);
               this.setSize((String)null);
               this.setClosedBrace((String)null);
            }
         } else if (!changeANSIDatatype.equalsIgnoreCase("LONGBLOB") && !changeANSIDatatype.equalsIgnoreCase("LONGTEXT") && !changeANSIDatatype.equalsIgnoreCase("IMAGE") && !changeANSIDatatype.equalsIgnoreCase("BYTEA")) {
            if (!changeANSIDatatype.equalsIgnoreCase("MEDIUMBLOB") && !changeANSIDatatype.equalsIgnoreCase("MEDIUMTEXT")) {
               if (!changeANSIDatatype.equalsIgnoreCase("TINYBLOB") && !changeANSIDatatype.equalsIgnoreCase("TINYTEXT")) {
                  if (!changeANSIDatatype.equalsIgnoreCase("LONG RAW") && !changeANSIDatatype.equalsIgnoreCase("RAW") && !changeANSIDatatype.equalsIgnoreCase("BINARY") && !changeANSIDatatype.equalsIgnoreCase("VARBINARY")) {
                     if (changeANSIDatatype.equalsIgnoreCase("BFILE")) {
                        this.setDatatypeName("BLOB");
                     }
                  } else {
                     this.setDatatypeName("BLOB");
                  }
               } else {
                  this.setDatatypeName("CHAR");
               }
            } else {
               this.setDatatypeName("LONG");
            }
         } else {
            this.setDatatypeName("BLOB");
         }

         if (this.getArray() != null) {
            this.setArray((String)null);
         }
      }

   }

   public void toInformixString() {
      if (this.notlogged != null) {
         this.setNotLogged((String)null);
      }

      if (this.getVarying() != null) {
         this.setVarying((String)null);
      }

      if (this.getDatatypeName() != null) {
         String changeIfxDatatype = this.getDatatypeName();
         if (changeIfxDatatype.equalsIgnoreCase("BIT")) {
            this.setDatatypeName("SMALLINT");
         } else if (!changeIfxDatatype.equalsIgnoreCase("BINARY") && !changeIfxDatatype.equalsIgnoreCase("VARBINARY")) {
            if (!changeIfxDatatype.equalsIgnoreCase("LONGBLOB") && !changeIfxDatatype.equalsIgnoreCase("LONGTEXT") && !changeIfxDatatype.equalsIgnoreCase("BFILE") && !changeIfxDatatype.equalsIgnoreCase("BLOB") && !changeIfxDatatype.equalsIgnoreCase("LONG RAW") && !changeIfxDatatype.equalsIgnoreCase("IMAGE") && !changeIfxDatatype.equalsIgnoreCase("BYTEA")) {
               if (!changeIfxDatatype.equalsIgnoreCase("TINYBLOB") && !changeIfxDatatype.equalsIgnoreCase("TINYTEXT")) {
                  if (changeIfxDatatype.equalsIgnoreCase("MEDIUMBLOB") || changeIfxDatatype.equalsIgnoreCase("MEDIUMTEXT") || changeIfxDatatype.equalsIgnoreCase("RAW")) {
                     this.setDatatypeName("TEXT");
                  }
               } else {
                  this.setDatatypeName("CHAR");
               }
            } else {
               this.setDatatypeName("BLOB");
               this.setClosedBrace((String)null);
               this.setOpenBrace((String)null);
               this.setSize((String)null);
            }
         } else {
            this.setDatatypeName("BYTE");
            this.setSize((String)null);
            this.setOpenBrace((String)null);
            this.setClosedBrace((String)null);
         }

         if (this.getArray() != null) {
            this.setArray((String)null);
         }
      }

   }

   public void toTimesTenString() {
      if (this.notlogged != null) {
         this.setNotLogged((String)null);
      }

      if (this.getVarying() != null) {
         this.setVarying((String)null);
      }

      if (this.getDatatypeName() != null) {
         String sourceType = this.getDatatypeName();
         if (!sourceType.equalsIgnoreCase("BLOB") && !sourceType.equalsIgnoreCase("LONGBLOB") && !sourceType.equalsIgnoreCase("MEDIUMBLOB") && !sourceType.equalsIgnoreCase("ORACLEBLOB") && !sourceType.equalsIgnoreCase("LONG RAW") && !sourceType.equalsIgnoreCase("BFILE") && !sourceType.equalsIgnoreCase("BYTEA")) {
            if (!sourceType.equalsIgnoreCase("LONGTEXT") && !sourceType.equalsIgnoreCase("MEDIUMTEXT")) {
               if (!sourceType.equalsIgnoreCase("RAW") && !sourceType.equalsIgnoreCase("TINYBLOB")) {
                  if (sourceType.equalsIgnoreCase("TINYTEXT")) {
                     this.setDatatypeName("CHAR");
                     this.setOpenBrace("(");
                     this.setSize("255");
                     this.setClosedBrace(")");
                  } else if (!sourceType.equalsIgnoreCase("BOOLEAN") && !sourceType.equalsIgnoreCase("BIT")) {
                     if (sourceType.equalsIgnoreCase("BINARY")) {
                        if (this.size == null) {
                           this.setOpenBrace("(");
                           this.setSize("1");
                           this.setClosedBrace(")");
                        } else if (Integer.parseInt(this.size) > 8300) {
                           this.setOpenBrace("(");
                           this.setSize("8300");
                           this.setClosedBrace(")");
                        }
                     } else if (sourceType.equalsIgnoreCase("VARBINARY")) {
                        if (this.size == null) {
                           this.setOpenBrace("(");
                           this.setSize("1");
                           this.setClosedBrace(")");
                        } else if (Integer.parseInt(this.size) > 4194304) {
                           this.setOpenBrace("(");
                           this.setSize("4194304");
                           this.setClosedBrace(")");
                        }
                     } else if (sourceType.equalsIgnoreCase("IMAGE")) {
                        this.setDatatypeName("VARBINARY");
                        this.setOpenBrace("(");
                        this.setSize("4194304");
                        this.setClosedBrace(")");
                     }
                  } else {
                     this.setDatatypeName("TINYINT");
                  }
               } else {
                  this.setDatatypeName("BINARY");
                  if (this.size == null) {
                     this.setOpenBrace("(");
                     this.setSize("1");
                     this.setClosedBrace(")");
                  }
               }
            } else {
               this.setDatatypeName("VARCHAR");
               this.setOpenBrace("(");
               this.setSize("4194304");
               this.setClosedBrace(")");
            }
         } else {
            this.setDatatypeName("VARBINARY");
            this.setOpenBrace("(");
            this.setSize("4194304");
            this.setClosedBrace(")");
         }

         if (this.getArray() != null) {
            this.setArray((String)null);
         }
      }

   }

   public void toNetezzaString() {
      String varcharMaxSize = "32760";
      if (this.notlogged != null) {
         this.setNotLogged((String)null);
      }

      if (this.getVarying() != null) {
         this.setVarying((String)null);
      }

      if (this.getDatatypeName() != null) {
         String changeNetezzaDatatype = this.getDatatypeName();
         if (changeNetezzaDatatype.equalsIgnoreCase("BLOB")) {
            this.setDatatypeName("VARCHAR");
            this.setOpenBrace("(");
            this.setSize(varcharMaxSize);
            this.setClosedBrace(")");
         } else if (!changeNetezzaDatatype.equalsIgnoreCase("LONGBLOB") && !changeNetezzaDatatype.equalsIgnoreCase("LONGTEXT") && !changeNetezzaDatatype.equalsIgnoreCase("IMAGE") && !changeNetezzaDatatype.equalsIgnoreCase("BYTEA")) {
            if (!changeNetezzaDatatype.equalsIgnoreCase("MEDIUMBLOB") && !changeNetezzaDatatype.equalsIgnoreCase("MEDIUMTEXT")) {
               if (!changeNetezzaDatatype.equalsIgnoreCase("TINYBLOB") && !changeNetezzaDatatype.equalsIgnoreCase("TINYTEXT")) {
                  if (!changeNetezzaDatatype.equalsIgnoreCase("LONG RAW") && !changeNetezzaDatatype.equalsIgnoreCase("RAW") && !changeNetezzaDatatype.equalsIgnoreCase("BINARY") && !changeNetezzaDatatype.equalsIgnoreCase("VARBINARY")) {
                     if (changeNetezzaDatatype.equalsIgnoreCase("BFILE")) {
                        this.setDatatypeName("VARCHAR");
                        this.setOpenBrace("(");
                        this.setSize(varcharMaxSize);
                        this.setClosedBrace(")");
                     }
                  } else {
                     this.setDatatypeName("VARCHAR");
                     this.setOpenBrace("(");
                     this.setSize(varcharMaxSize);
                     this.setClosedBrace(")");
                  }
               } else {
                  this.setDatatypeName("CHAR");
               }
            } else {
               this.setDatatypeName("VARCHAR");
               this.setOpenBrace("(");
               this.setSize(varcharMaxSize);
               this.setClosedBrace(")");
            }
         } else {
            this.setDatatypeName("VARCHAR");
            this.setOpenBrace("(");
            this.setSize(varcharMaxSize);
            this.setClosedBrace(")");
         }

         if (this.getArray() != null) {
            this.setArray((String)null);
         }
      }

   }

   public void toTeradataString() {
      if (this.notlogged != null) {
         this.setNotLogged((String)null);
      }

      if (this.getVarying() != null) {
         this.setVarying((String)null);
      }

      if (this.getDatatypeName() != null) {
         String changeTeradataDatatype = this.getDatatypeName();
         if (changeTeradataDatatype.equalsIgnoreCase("BLOB")) {
            if (this.getSize() != null) {
               this.setOpenBrace((String)null);
               this.setSize((String)null);
               this.setClosedBrace((String)null);
            }
         } else if (!changeTeradataDatatype.equalsIgnoreCase("LONGBLOB") && !changeTeradataDatatype.equalsIgnoreCase("LONGTEXT") && !changeTeradataDatatype.equalsIgnoreCase("IMAGE") && !changeTeradataDatatype.equalsIgnoreCase("BYTEA")) {
            if (!changeTeradataDatatype.equalsIgnoreCase("MEDIUMBLOB") && !changeTeradataDatatype.equalsIgnoreCase("MEDIUMTEXT")) {
               if (!changeTeradataDatatype.equalsIgnoreCase("TINYBLOB") && !changeTeradataDatatype.equalsIgnoreCase("TINYTEXT")) {
                  if (!changeTeradataDatatype.equalsIgnoreCase("LONG RAW") && !changeTeradataDatatype.equalsIgnoreCase("RAW") && !changeTeradataDatatype.equalsIgnoreCase("BINARY") && !changeTeradataDatatype.equalsIgnoreCase("VARBINARY")) {
                     if (changeTeradataDatatype.equalsIgnoreCase("BFILE")) {
                        this.setDatatypeName("BLOB");
                     }
                  } else {
                     this.setDatatypeName("BLOB");
                  }
               } else {
                  this.setDatatypeName("CHAR");
               }
            } else {
               this.setDatatypeName("LONG");
            }
         } else {
            this.setDatatypeName("BLOB");
         }

         if (this.getArray() != null) {
            this.setArray((String)null);
         }
      }

   }

   public Datatype copyObjectValues() {
      BinClass newBinClass = new BinClass();
      newBinClass.setClosedBrace(this.closedBrace);
      newBinClass.setDatatypeName(this.getDatatypeName());
      newBinClass.setOpenBrace(this.openBrace);
      newBinClass.setSize(this.getSize());
      newBinClass.setVarying(this.getVarying());
      newBinClass.setNotLogged(this.getNotLogged());
      newBinClass.setArray(this.getArray());
      return newBinClass;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.datatypeName != null) {
         sb.append(this.datatypeName + " ");
      }

      if (this.varyingType != null) {
         sb.append(this.varyingType + " ");
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

      if (this.notlogged != null) {
         sb.append(this.notlogged);
      }

      if (this.arrayStr != null) {
         sb.append(this.arrayStr);
      }

      return sb.toString();
   }

   public void setArray(String arrayStr) {
   }

   public String getArray() {
      return this.arrayStr;
   }
}
