package com.adventnet.swissqlapi.sql.statement.create;

import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import java.util.ArrayList;

public class CharacterClass implements Datatype {
   private String datatypeName;
   private String openBrace;
   private String closedBrace;
   private String size;
   private String nationalType;
   private String varyingType;
   private String doBinarySearch;
   private String notlogged;
   private String ascii;
   private String unicode;
   private String SAPbyte;
   private ArrayList enumValues;
   private ArrayList setValues;
   private String arrayStr;
   private String caseSpecificPhrase;

   public void setNational(String nationalType) {
      this.nationalType = nationalType;
   }

   public void setVarying(String varyingType) {
      this.varyingType = varyingType;
   }

   public void setBinary(String doBinarySearch) {
      this.doBinarySearch = doBinarySearch;
   }

   public void setDatatypeName(String datatypeName) {
      this.datatypeName = datatypeName;
   }

   public void setAscii(String ascii) {
      this.ascii = ascii;
   }

   public void setUnicode(String unicode) {
      this.unicode = unicode;
   }

   public void setByte(String SAPbyte) {
      this.SAPbyte = SAPbyte;
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

   public void setEnumValues(ArrayList enumValues) {
      this.enumValues = enumValues;
   }

   public void setSetValues(ArrayList setValues) {
      this.setValues = setValues;
   }

   public void setCaseSpecificPhrase(String caseSpecPhrase) {
      this.caseSpecificPhrase = caseSpecPhrase;
   }

   public String getDatatypeName() {
      return this.datatypeName;
   }

   public String getSize() {
      return this.size;
   }

   public String getNational() {
      return this.nationalType;
   }

   public String getVarying() {
      return this.varyingType;
   }

   public String getBinary() {
      return this.doBinarySearch;
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

   public String getAscii() {
      return this.ascii;
   }

   public String getUnicode() {
      return this.unicode;
   }

   public String getByte() {
      return this.SAPbyte;
   }

   public ArrayList getEnumValues() {
      return this.enumValues;
   }

   public ArrayList getSetValues() {
      return this.setValues;
   }

   public String getCaseSpecificPhrase() {
      return this.caseSpecificPhrase;
   }

   public void toInformixString() {
      this.setNotLogged((String)null);
      if (this.getDatatypeName() != null) {
         if (this.getBinary() != null) {
            this.setBinary((String)null);
         }

         String changeIfxDatatype = this.getDatatypeName();
         if (this.getNational() == null && this.getUnicode() == null) {
            if (changeIfxDatatype.equalsIgnoreCase("NCHAR")) {
               if (this.getVarying() != null) {
                  this.setDatatypeName("NVARCHAR");
                  this.setVarying((String)null);
               }
            } else if (changeIfxDatatype.equalsIgnoreCase("VARCHAR2")) {
               this.setDatatypeName("VARCHAR");
            } else if (changeIfxDatatype.equalsIgnoreCase("NVARCHAR2")) {
               this.setDatatypeName("NVARCHAR");
            } else if (changeIfxDatatype.equalsIgnoreCase("NTEXT")) {
               this.setDatatypeName("TEXT");
            } else if (changeIfxDatatype.equalsIgnoreCase("BPCHAR")) {
               this.setDatatypeName("CHAR");
            } else if (!changeIfxDatatype.equalsIgnoreCase("LONG") && !changeIfxDatatype.equalsIgnoreCase("NCLOB") && !changeIfxDatatype.equalsIgnoreCase("DBCLOB")) {
               if (changeIfxDatatype.equalsIgnoreCase("ENUM")) {
                  this.setDatatypeName("VARCHAR");
                  this.setOpenBrace("(");
                  this.setSize(this.maxLengthFromEnumValues(this.getEnumValues()));
                  this.setClosedBrace(")");
               }
            } else {
               this.setDatatypeName("TEXT");
            }
         } else if (!changeIfxDatatype.equalsIgnoreCase("CHAR") && !changeIfxDatatype.equalsIgnoreCase("CHARACTER")) {
            if (changeIfxDatatype.equalsIgnoreCase("VARCHAR") && this.getUnicode() != null) {
               this.setDatatypeName("NVARCHAR");
            }
         } else {
            if (this.getVarying() != null) {
               this.setDatatypeName("NVARCHAR");
               this.setVarying((String)null);
            } else {
               this.setDatatypeName("NCHAR");
            }

            this.setNational((String)null);
         }

         if (this.getAscii() != null) {
            this.setAscii((String)null);
         }

         if (this.getUnicode() != null) {
            this.setUnicode((String)null);
         }

         if (this.getByte() != null) {
            this.setByte((String)null);
         }

         if (this.getArray() != null) {
            this.setArray((String)null);
         }
      }

   }

   public void toDB2String() {
      if (this.getDatatypeName() != null) {
         String changeDB2Datatype = this.getDatatypeName();
         if (this.getNational() != null) {
            this.setNational((String)null);
         }

         if (this.getBinary() != null) {
            this.setBinary("FOR BIT DATA");
         }

         if (this.notlogged != null) {
            this.setNotLogged(" NOT LOGGED");
         }

         if (!changeDB2Datatype.equalsIgnoreCase("CLOB") && !changeDB2Datatype.equalsIgnoreCase("NCLOB") && !changeDB2Datatype.equalsIgnoreCase("TEXT") && !changeDB2Datatype.equalsIgnoreCase("LONG")) {
            if (SwisSQLOptions.fromAccess && changeDB2Datatype.equalsIgnoreCase("LONGCHAR")) {
               this.setDatatypeName("CLOB (2G) NOT LOGGED");
               this.setOpenBrace((String)null);
               this.setSize((String)null);
               this.setClosedBrace((String)null);
            } else if (changeDB2Datatype.equalsIgnoreCase("NTEXT")) {
               this.setDatatypeName("CLOB");
               this.setOpenBrace("(");
               this.setSize("1073741823");
               this.setClosedBrace(")");
               this.setNotLogged(" NOT LOGGED");
            } else {
               int size;
               String sizeStr;
               if (!changeDB2Datatype.equalsIgnoreCase("CHAR") && !changeDB2Datatype.equalsIgnoreCase("CHARACTER")) {
                  if (!changeDB2Datatype.equalsIgnoreCase("NVARCHAR") && !changeDB2Datatype.equalsIgnoreCase("NVARCHAR2") && !changeDB2Datatype.equalsIgnoreCase("VARCHAR2")) {
                     if (changeDB2Datatype.equalsIgnoreCase("NCHAR")) {
                        sizeStr = this.getSize();
                        if (sizeStr != null) {
                           size = Integer.parseInt(sizeStr);
                           if (size > 254) {
                              this.setDatatypeName("VARCHAR");
                           } else {
                              this.setDatatypeName("CHAR");
                           }
                        } else {
                           this.setDatatypeName("CHAR");
                        }
                     } else if (changeDB2Datatype.equalsIgnoreCase("BPCHAR")) {
                        this.setDatatypeName("CHAR");
                     } else if (changeDB2Datatype.equalsIgnoreCase("\"CHAR\"")) {
                        this.setDatatypeName("CHAR");
                        this.setOpenBrace("(");
                        this.setSize("1");
                        this.setClosedBrace(")");
                     } else if (changeDB2Datatype.equalsIgnoreCase("VARCHAR") && this.getUnicode() != null) {
                        this.setDatatypeName("VARGRAPHIC");
                     } else if (changeDB2Datatype.equalsIgnoreCase("ENUM")) {
                        this.setDatatypeName("VARCHAR");
                        this.setOpenBrace("(");
                        this.setSize(this.maxLengthFromEnumValues(this.getEnumValues()));
                        this.setClosedBrace(")");
                     }
                  } else {
                     this.setDatatypeName("VARCHAR");
                  }
               } else {
                  sizeStr = this.getSize();
                  if (sizeStr != null) {
                     size = Integer.parseInt(sizeStr);
                     if (size > 254) {
                        this.setDatatypeName("VARCHAR");
                     }
                  }

                  if (this.getVarying() != null) {
                     this.setDatatypeName("VARCHAR");
                     this.setVarying((String)null);
                  }

                  if (this.getUnicode() != null) {
                     this.setDatatypeName("GRAPHIC");
                  }
               }
            }
         } else {
            this.setDatatypeName("CLOB");
            if (this.getSize() == null) {
               this.setOpenBrace("(");
               this.setSize("2G");
               this.setClosedBrace(")");
               this.setNotLogged(" NOT LOGGED");
            } else {
               try {
                  int precisionValue = Integer.parseInt(this.getSize());
                  if (precisionValue >= 1073741823) {
                     this.setSize("2G");
                     this.setNotLogged(" NOT LOGGED");
                  }
               } catch (NumberFormatException var4) {
               }
            }
         }

         if (this.getAscii() != null) {
            this.setAscii((String)null);
         }

         if (this.getUnicode() != null) {
            this.setUnicode((String)null);
         }

         if (this.getByte() != null) {
            this.setByte((String)null);
         }

         if (this.getArray() != null) {
            this.setArray((String)null);
         }
      }

   }

   public void toOracleString() {
      this.setNotLogged((String)null);
      if (this.getDatatypeName() != null) {
         String changeOracleDatatype = this.getDatatypeName();
         if (!changeOracleDatatype.equalsIgnoreCase("CLOB") && !changeOracleDatatype.equalsIgnoreCase("DBCLOB")) {
            if (changeOracleDatatype.equalsIgnoreCase("XML")) {
               this.setDatatypeName("CLOB");
            } else if (changeOracleDatatype.equalsIgnoreCase("TEXT")) {
               this.setDatatypeName("CLOB");
            } else if (SwisSQLOptions.fromAccess && changeOracleDatatype.equalsIgnoreCase("LONGCHAR")) {
               this.setDatatypeName("CLOB");
               this.setOpenBrace((String)null);
               this.setSize((String)null);
               this.setClosedBrace((String)null);
            } else if (changeOracleDatatype.equalsIgnoreCase("NTEXT")) {
               this.setDatatypeName("NCLOB");
            } else if (changeOracleDatatype.equalsIgnoreCase("VARCHAR")) {
               if (this.getUnicode() != null) {
                  this.setDatatypeName("NVARCHAR2");
                  if (this.size != null && Integer.parseInt(this.size) > 2000) {
                     this.setSize("2000");
                  }
               } else if (this.getBinary() != null) {
                  this.setDatatypeName("RAW");
                  if (this.size != null && Integer.parseInt(this.size) > 2000) {
                     this.setDatatypeName("LONG RAW");
                     this.setSize((String)null);
                     this.setClosedBrace((String)null);
                     this.setOpenBrace((String)null);
                  }
               } else {
                  this.setDatatypeName("VARCHAR2");
               }

               try {
                  if (this.size != null && Integer.parseInt(this.size) > 4000) {
                     this.setDatatypeName("CLOB");
                     this.setOpenBrace((String)null);
                     this.setClosedBrace((String)null);
                     this.setSize((String)null);
                  }
               } catch (NumberFormatException var3) {
                  if (this.size != null && this.size.equalsIgnoreCase("max")) {
                     this.setDatatypeName("CLOB");
                     this.setOpenBrace((String)null);
                     this.setClosedBrace((String)null);
                     this.setSize((String)null);
                  }
               }
            } else if (changeOracleDatatype.equalsIgnoreCase("NVARCHAR")) {
               this.setDatatypeName("NVARCHAR2");

               try {
                  if (this.size != null && Integer.parseInt(this.size) > 2000) {
                     this.setDatatypeName("NCLOB");
                     this.setOpenBrace((String)null);
                     this.setClosedBrace((String)null);
                     this.setSize((String)null);
                  }
               } catch (NumberFormatException var4) {
                  if (this.size != null && this.size.equalsIgnoreCase("max")) {
                     this.setDatatypeName("NCLOB");
                     this.setOpenBrace((String)null);
                     this.setClosedBrace((String)null);
                     this.setSize((String)null);
                  }
               }
            } else if (changeOracleDatatype.equalsIgnoreCase("LONG VARCHAR")) {
               this.setDatatypeName("LONG");
               if (this.getBinary() != null) {
                  this.setDatatypeName("LONG RAW");
               }
            } else if (changeOracleDatatype.equalsIgnoreCase("\"CHAR\"")) {
               this.setDatatypeName("CHAR");
               this.setOpenBrace("(");
               this.setSize("1");
               this.setClosedBrace(")");
            } else if (changeOracleDatatype.equalsIgnoreCase("BPCHAR")) {
               this.setDatatypeName("CHAR");
            } else if (changeOracleDatatype.equalsIgnoreCase("CHAR") && this.getOpenBrace() != null && this.getUnicode() != null) {
               this.setDatatypeName("NCHAR");
            } else if (!changeOracleDatatype.equalsIgnoreCase("CHAR") && !changeOracleDatatype.equalsIgnoreCase("CHARACTER")) {
               if (changeOracleDatatype.equalsIgnoreCase("NCHAR")) {
                  if (this.size != null && Integer.parseInt(this.size) > 2000) {
                     this.setDatatypeName("NCLOB");
                     this.setOpenBrace((String)null);
                     this.setClosedBrace((String)null);
                     this.setSize((String)null);
                     this.setVarying((String)null);
                  }
               } else if (changeOracleDatatype.equalsIgnoreCase("ENUM")) {
                  this.setDatatypeName("VARCHAR2");
                  this.setOpenBrace("(");
                  this.setSize(this.maxLengthFromEnumValues(this.getEnumValues()));
                  this.setClosedBrace(")");
               } else if ((changeOracleDatatype.equalsIgnoreCase("CHAR") || changeOracleDatatype.equalsIgnoreCase("CHARACTER")) && this.getBinary() != null) {
                  this.setDatatypeName("RAW");
                  if (this.size == null) {
                     this.setOpenBrace("(");
                     this.setSize("1");
                     this.setClosedBrace(")");
                  }
               } else if (changeOracleDatatype.equalsIgnoreCase("LONG") && this.getUnicode() != null) {
                  this.setDatatypeName("CLOB");
               }
            } else if (this.size != null && Integer.parseInt(this.size) > 2000) {
               this.setDatatypeName("CLOB");
               this.setOpenBrace((String)null);
               this.setClosedBrace((String)null);
               this.setSize((String)null);
               this.setVarying((String)null);
            }
         } else {
            this.setDatatypeName("CLOB");
            if (this.getSize() != null) {
               this.setOpenBrace((String)null);
               this.setSize((String)null);
               this.setClosedBrace((String)null);
               this.setDatatypeName("LONG");
            }
         }

         if (this.getAscii() != null) {
            this.setAscii((String)null);
         }

         if (this.getUnicode() != null) {
            this.setUnicode((String)null);
         }

         if (this.getByte() != null) {
            this.setByte((String)null);
         }

         if (this.getBinary() != null) {
            this.setBinary((String)null);
         }

         if (this.getArray() != null) {
            this.setArray((String)null);
         }
      }

   }

   public void toDenodoString() {
      this.setNotLogged((String)null);
      if (this.getDatatypeName() != null) {
         String changeBigQueryDatatype = this.getDatatypeName();
         if (this.getBinary() != null) {
            this.setBinary((String)null);
            this.setDatatypeName("BYTEA");
            this.setSize((String)null);
            this.setClosedBrace((String)null);
            this.setOpenBrace((String)null);
         } else if (changeBigQueryDatatype.equalsIgnoreCase("CHARACTER")) {
            if (this.getVarying() != null) {
               this.setDatatypeName("VARCHAR");
               this.setVarying((String)null);
            } else {
               this.setDatatypeName("VARCHAR");
            }
         } else if (changeBigQueryDatatype.equalsIgnoreCase("VARCHAR2")) {
            this.setDatatypeName("VARCHAR");
         } else if (!changeBigQueryDatatype.equalsIgnoreCase("NVARCHAR2") && !changeBigQueryDatatype.equalsIgnoreCase("NVARCHAR")) {
            if (changeBigQueryDatatype.equalsIgnoreCase("NCHAR")) {
               this.setDatatypeName("VARCHAR");
            } else if (!changeBigQueryDatatype.equalsIgnoreCase("CLOB") && !changeBigQueryDatatype.equalsIgnoreCase("NCLOB") && !changeBigQueryDatatype.equalsIgnoreCase("DBCLOB") && !changeBigQueryDatatype.equalsIgnoreCase("LONG") && !changeBigQueryDatatype.equalsIgnoreCase("NTEXT") && !changeBigQueryDatatype.equalsIgnoreCase("LONG VARCHAR")) {
               if (changeBigQueryDatatype.equalsIgnoreCase("CHAR") && this.getUnicode() != null) {
                  this.setDatatypeName("VARCHAR");
               } else if (changeBigQueryDatatype.equalsIgnoreCase("BPCHAR")) {
                  this.setDatatypeName("VARCHAR");
               } else if (changeBigQueryDatatype.equalsIgnoreCase("VARCHAR") && this.getUnicode() != null) {
                  this.setDatatypeName("VARCHAR");
               } else if (changeBigQueryDatatype.equalsIgnoreCase("ENUM")) {
                  this.setDatatypeName("VARCHAR");
                  this.setOpenBrace("(");
                  this.setSize(this.maxLengthFromEnumValues(this.getEnumValues()));
                  this.setClosedBrace(")");
               }
            } else {
               this.setDatatypeName("VARCHAR");
               this.setOpenBrace((String)null);
               this.setSize((String)null);
               this.setClosedBrace((String)null);
            }
         } else if (this.getSize() != null) {
            this.setDatatypeName("NVARCHAR");
            this.setOpenBrace("(");
            this.setSize(this.getSize());
            this.setClosedBrace(")");
         } else {
            this.setDatatypeName("VARCHAR");
         }

         if (this.getAscii() != null) {
            this.setAscii((String)null);
         }

         if (this.getUnicode() != null) {
            this.setUnicode((String)null);
         }

         if (this.getByte() != null) {
            this.setByte((String)null);
         }
      }

   }

   public void toExcelString() {
      this.setNotLogged((String)null);
      if (this.getDatatypeName() != null) {
         String changeExcelDatatype = this.getDatatypeName();
         if (this.getBinary() != null) {
            this.setDatatypeName("OLE Object");
         } else if (!changeExcelDatatype.equalsIgnoreCase("VARCHAR2") && !changeExcelDatatype.equalsIgnoreCase("NVARCHAR2") && !changeExcelDatatype.equalsIgnoreCase("NVARCHAR") && !changeExcelDatatype.equalsIgnoreCase("NCHAR") && !changeExcelDatatype.equalsIgnoreCase("BPCHAR")) {
            if (!changeExcelDatatype.equalsIgnoreCase("CLOB") && !changeExcelDatatype.equalsIgnoreCase("NCLOB") && !changeExcelDatatype.equalsIgnoreCase("DBCLOB") && !changeExcelDatatype.equalsIgnoreCase("LONG") && !changeExcelDatatype.equalsIgnoreCase("NTEXT") && !changeExcelDatatype.equalsIgnoreCase("LONG VARCHAR")) {
               if (changeExcelDatatype.equalsIgnoreCase("ENUM")) {
                  this.setDatatypeName("Long Text");
               }
            } else {
               this.setDatatypeName("Long Text");
               this.setOpenBrace((String)null);
               this.setSize((String)null);
               this.setClosedBrace((String)null);
            }
         } else {
            this.setDatatypeName("Long Text");
         }

         if (this.getAscii() != null) {
            this.setAscii((String)null);
         }

         if (this.getUnicode() != null) {
            this.setUnicode((String)null);
         }

         if (this.getByte() != null) {
            this.setByte((String)null);
         }
      }

   }

   public void toMsAccessJdbcString() {
      this.setNotLogged((String)null);
      if (this.getDatatypeName() != null) {
         String changeMsAccessDatatype = this.getDatatypeName();
         if (this.getBinary() != null) {
            this.setDatatypeName("BLOB");
         } else if (!changeMsAccessDatatype.equalsIgnoreCase("VARCHAR2") && !changeMsAccessDatatype.equalsIgnoreCase("NVARCHAR2") && !changeMsAccessDatatype.equalsIgnoreCase("NVARCHAR") && !changeMsAccessDatatype.equalsIgnoreCase("NCHAR") && !changeMsAccessDatatype.equalsIgnoreCase("BPCHAR")) {
            if (!changeMsAccessDatatype.equalsIgnoreCase("CLOB") && !changeMsAccessDatatype.equalsIgnoreCase("NCLOB") && !changeMsAccessDatatype.equalsIgnoreCase("DBCLOB") && !changeMsAccessDatatype.equalsIgnoreCase("LONG") && !changeMsAccessDatatype.equalsIgnoreCase("NTEXT") && !changeMsAccessDatatype.equalsIgnoreCase("LONG VARCHAR")) {
               if (changeMsAccessDatatype.equalsIgnoreCase("ENUM")) {
                  this.setDatatypeName("LONGVARCHAR");
                  this.setOpenBrace("(");
                  this.setSize(this.maxLengthFromEnumValues(this.getEnumValues()));
                  this.setClosedBrace(")");
               }
            } else {
               this.setDatatypeName("LONGVARCHAR");
               this.setOpenBrace((String)null);
               this.setSize((String)null);
               this.setClosedBrace((String)null);
            }
         } else {
            this.setDatatypeName("LONGVARCHAR");
         }

         if (this.getAscii() != null) {
            this.setAscii((String)null);
         }

         if (this.getUnicode() != null) {
            this.setUnicode((String)null);
         }

         if (this.getByte() != null) {
            this.setByte((String)null);
         }
      }

   }

   public void toMSSQLServerString() {
      this.setNotLogged((String)null);
      if (this.getDatatypeName() != null) {
         String changeSQLServerDatatype = this.getDatatypeName();
         if (this.getBinary() != null) {
            this.setBinary((String)null);
         }

         if (this.getNational() == null && this.getUnicode() == null) {
            if (changeSQLServerDatatype.equalsIgnoreCase("VARCHAR2")) {
               this.setDatatypeName("VARCHAR");
            } else if (changeSQLServerDatatype.equalsIgnoreCase("NVARCHAR2")) {
               this.setDatatypeName("NVARCHAR");
            } else if (changeSQLServerDatatype.equalsIgnoreCase("CHARACTER")) {
               this.setDatatypeName("CHAR");
            } else if (!changeSQLServerDatatype.equalsIgnoreCase("CLOB") && !changeSQLServerDatatype.equalsIgnoreCase("DBCLOB") && !changeSQLServerDatatype.equalsIgnoreCase("LONG") && !changeSQLServerDatatype.equalsIgnoreCase("LONG VARCHAR")) {
               if (SwisSQLOptions.fromAccess && changeSQLServerDatatype.equalsIgnoreCase("LONGCHAR")) {
                  this.setDatatypeName("TEXT");
                  this.setOpenBrace((String)null);
                  this.setSize((String)null);
                  this.setClosedBrace((String)null);
               } else if (changeSQLServerDatatype.equalsIgnoreCase("NCLOB")) {
                  this.setDatatypeName("NTEXT");
                  this.setOpenBrace((String)null);
                  this.setSize((String)null);
                  this.setClosedBrace((String)null);
               } else if (changeSQLServerDatatype.equalsIgnoreCase("\"CHAR\"")) {
                  this.setDatatypeName("CHAR");
                  this.setOpenBrace("(");
                  this.setSize("1");
                  this.setClosedBrace(")");
               } else if (changeSQLServerDatatype.equalsIgnoreCase("BPCHAR")) {
                  this.setDatatypeName("CHAR");
               } else if (changeSQLServerDatatype.equalsIgnoreCase("GRAPHIC")) {
                  this.setDatatypeName("NCHAR");
               } else if (changeSQLServerDatatype.equalsIgnoreCase("ENUM")) {
                  this.setDatatypeName("VARCHAR");
                  this.setOpenBrace("(");
                  this.setSize(this.maxLengthFromEnumValues(this.getEnumValues()));
                  this.setClosedBrace(")");
               } else if (changeSQLServerDatatype.equalsIgnoreCase("SET")) {
                  this.setDatatypeName("SET");
                  this.setOpenBrace("(");
                  String s = this.getSetValues().toString();
                  s = s.substring(1, s.length() - 1);
                  this.setSize(s);
                  this.setClosedBrace(")");
               } else if (changeSQLServerDatatype.equalsIgnoreCase("LONG") && this.getUnicode() != null) {
                  this.setDatatypeName("TEXT");
               }
            } else {
               this.setDatatypeName("TEXT");
               this.setOpenBrace((String)null);
               this.setSize((String)null);
               this.setClosedBrace((String)null);
            }
         } else if (!changeSQLServerDatatype.equalsIgnoreCase("CHAR") && !changeSQLServerDatatype.equalsIgnoreCase("CHARACTER")) {
            if (changeSQLServerDatatype.equalsIgnoreCase("VARCHAR") && this.getUnicode() != null) {
               this.setDatatypeName("NVARCHAR");
            }
         } else {
            this.setDatatypeName("NCHAR");
            this.setNational((String)null);
         }

         if (this.getAscii() != null) {
            this.setAscii((String)null);
         }

         if (this.getUnicode() != null) {
            this.setUnicode((String)null);
         }

         if (this.getByte() != null) {
            this.setByte((String)null);
         }

         if (this.getArray() != null) {
            this.setArray((String)null);
         }
      }

   }

   public void toSybaseString() {
      this.setNotLogged((String)null);
      if (this.getDatatypeName() != null) {
         String changeSQLServerDatatype = this.getDatatypeName();
         if (this.getBinary() != null) {
            this.setBinary((String)null);
         } else if ((this.getNational() == null || this.getVarying() == null) && this.getUnicode() == null) {
            if (this.getNational() != null) {
               if (changeSQLServerDatatype.equalsIgnoreCase("CHAR") || changeSQLServerDatatype.equalsIgnoreCase("CHARACTER")) {
                  this.setDatatypeName("NCHAR");
                  this.setNational((String)null);
               }
            } else if (changeSQLServerDatatype.equalsIgnoreCase("VARCHAR2")) {
               this.setDatatypeName("VARCHAR");
            } else if (changeSQLServerDatatype.equalsIgnoreCase("NVARCHAR2")) {
               this.setDatatypeName("NVARCHAR");
            } else if (changeSQLServerDatatype.equalsIgnoreCase("CHARACTER")) {
               this.setDatatypeName("CHAR");
            } else if (!changeSQLServerDatatype.equalsIgnoreCase("CLOB") && !changeSQLServerDatatype.equalsIgnoreCase("NCLOB") && !changeSQLServerDatatype.equalsIgnoreCase("DBCLOB") && !changeSQLServerDatatype.equalsIgnoreCase("LONG") && !changeSQLServerDatatype.equalsIgnoreCase("LONG VARCHAR") && !changeSQLServerDatatype.equalsIgnoreCase("NTEXT")) {
               if (SwisSQLOptions.fromAccess && changeSQLServerDatatype.equalsIgnoreCase("LONGCHAR")) {
                  this.setDatatypeName("TEXT");
                  this.setOpenBrace((String)null);
                  this.setSize((String)null);
                  this.setClosedBrace((String)null);
               } else if (changeSQLServerDatatype.equalsIgnoreCase("\"CHAR\"")) {
                  this.setDatatypeName("CHAR");
                  this.setOpenBrace("(");
                  this.setSize("1");
                  this.setClosedBrace(")");
               } else if (changeSQLServerDatatype.equalsIgnoreCase("BPCHAR")) {
                  this.setDatatypeName("CHAR");
               } else if (changeSQLServerDatatype.equalsIgnoreCase("ENUM")) {
                  this.setDatatypeName("VARCHAR");
                  this.setOpenBrace("(");
                  this.setSize(this.maxLengthFromEnumValues(this.getEnumValues()));
                  this.setClosedBrace(")");
               } else if (changeSQLServerDatatype.equalsIgnoreCase("LONG") && this.getUnicode() != null) {
                  this.setDatatypeName("TEXT");
               }
            } else {
               this.setDatatypeName("TEXT");
               this.setOpenBrace((String)null);
               this.setSize((String)null);
               this.setClosedBrace((String)null);
            }
         } else if (!changeSQLServerDatatype.equalsIgnoreCase("CHAR") && !changeSQLServerDatatype.equalsIgnoreCase("CHARACTER") && !changeSQLServerDatatype.equalsIgnoreCase("NCHAR")) {
            if (changeSQLServerDatatype.equalsIgnoreCase("VARCHAR") && this.getUnicode() != null) {
               this.setDatatypeName("NVARCHAR");
            }
         } else {
            this.setDatatypeName("NVARCHAR");
            this.setNational((String)null);
            this.setVarying((String)null);
         }

         if (this.getAscii() != null) {
            this.setAscii((String)null);
         }

         if (this.getUnicode() != null) {
            this.setUnicode((String)null);
         }

         if (this.getByte() != null) {
            this.setByte((String)null);
         }

         if (this.getArray() != null) {
            this.setArray((String)null);
         }
      }

   }

   public void toBigQueryString() {
      this.setNotLogged((String)null);
      if (this.getDatatypeName() != null) {
         String changeBigQueryDatatype = this.getDatatypeName();
         if (this.getBinary() != null) {
            this.setBinary((String)null);
            this.setDatatypeName("BYTES");
            this.setSize((String)null);
            this.setClosedBrace((String)null);
            this.setOpenBrace((String)null);
         } else if (changeBigQueryDatatype.equalsIgnoreCase("CHARACTER")) {
            if (this.getVarying() != null) {
               this.setDatatypeName("STRING");
               this.setVarying((String)null);
            } else {
               this.setDatatypeName("STRING");
            }
         } else if (changeBigQueryDatatype.equalsIgnoreCase("VARCHAR2")) {
            this.setDatatypeName("STRING");
         } else if (!changeBigQueryDatatype.equalsIgnoreCase("NVARCHAR2") && !changeBigQueryDatatype.equalsIgnoreCase("NVARCHAR")) {
            if (changeBigQueryDatatype.equalsIgnoreCase("NCHAR")) {
               this.setDatatypeName("STRING");
               this.setNational("NATIONAL");
            } else if (!changeBigQueryDatatype.equalsIgnoreCase("CLOB") && !changeBigQueryDatatype.equalsIgnoreCase("NCLOB") && !changeBigQueryDatatype.equalsIgnoreCase("DBCLOB") && !changeBigQueryDatatype.equalsIgnoreCase("LONG") && !changeBigQueryDatatype.equalsIgnoreCase("NTEXT") && !changeBigQueryDatatype.equalsIgnoreCase("LONG VARCHAR")) {
               if (changeBigQueryDatatype.equalsIgnoreCase("CHAR") && this.getUnicode() != null) {
                  this.setDatatypeName("STRING");
                  this.setNational("NATIONAL");
                  this.setVarying("VARYING");
               } else if (changeBigQueryDatatype.equalsIgnoreCase("BPCHAR")) {
                  this.setDatatypeName("STRING");
               } else if (changeBigQueryDatatype.equalsIgnoreCase("VARCHAR") && this.getUnicode() != null) {
                  this.setDatatypeName("STRING");
                  this.setNational("NATIONAL");
                  this.setVarying("VARYING");
               } else if (changeBigQueryDatatype.equalsIgnoreCase("ENUM")) {
                  this.setDatatypeName("STRING");
                  this.setOpenBrace("(");
                  this.setSize(this.maxLengthFromEnumValues(this.getEnumValues()));
                  this.setClosedBrace(")");
               }
            } else {
               this.setDatatypeName("STRING");
               this.setOpenBrace((String)null);
               this.setSize((String)null);
               this.setClosedBrace((String)null);
            }
         } else {
            this.setDatatypeName("STRING");
            this.setNational("NATIONAL");
            this.setVarying("VARYING");
         }

         if (this.getAscii() != null) {
            this.setAscii((String)null);
         }

         if (this.getUnicode() != null) {
            this.setUnicode((String)null);
         }

         if (this.getByte() != null) {
            this.setByte((String)null);
         }
      }

   }

   public void toPostgreSQLString() {
      this.setNotLogged((String)null);
      if (this.getDatatypeName() != null) {
         String changePostgreSQLDatatype = this.getDatatypeName();
         if (this.getBinary() != null) {
            this.setBinary((String)null);
            this.setDatatypeName("BYTEA");
            this.setSize((String)null);
            this.setClosedBrace((String)null);
            this.setOpenBrace((String)null);
         } else if (changePostgreSQLDatatype.equalsIgnoreCase("CHARACTER")) {
            if (this.getVarying() != null) {
               this.setDatatypeName("VARCHAR");
               this.setVarying((String)null);
            } else {
               this.setDatatypeName("CHAR");
            }
         } else if (changePostgreSQLDatatype.equalsIgnoreCase("VARCHAR2")) {
            this.setDatatypeName("VARCHAR");
         } else if (!changePostgreSQLDatatype.equalsIgnoreCase("NVARCHAR2") && !changePostgreSQLDatatype.equalsIgnoreCase("NVARCHAR")) {
            if (changePostgreSQLDatatype.equalsIgnoreCase("NCHAR")) {
               this.setDatatypeName("CHAR");
               this.setNational("NATIONAL");
            } else if (!changePostgreSQLDatatype.equalsIgnoreCase("CLOB") && !changePostgreSQLDatatype.equalsIgnoreCase("NCLOB") && !changePostgreSQLDatatype.equalsIgnoreCase("DBCLOB") && !changePostgreSQLDatatype.equalsIgnoreCase("LONG") && !changePostgreSQLDatatype.equalsIgnoreCase("NTEXT") && !changePostgreSQLDatatype.equalsIgnoreCase("LONG VARCHAR")) {
               if (changePostgreSQLDatatype.equalsIgnoreCase("CHAR") && this.getUnicode() != null) {
                  this.setDatatypeName("CHAR");
                  this.setNational("NATIONAL");
                  this.setVarying("VARYING");
               } else if (changePostgreSQLDatatype.equalsIgnoreCase("BPCHAR")) {
                  this.setDatatypeName("BPCHAR");
               } else if (changePostgreSQLDatatype.equalsIgnoreCase("VARCHAR") && this.getUnicode() != null) {
                  this.setDatatypeName("VARCHAR");
                  this.setNational("NATIONAL");
                  this.setVarying("VARYING");
               } else if (changePostgreSQLDatatype.equalsIgnoreCase("ENUM")) {
                  this.setDatatypeName("VARCHAR");
                  this.setOpenBrace("(");
                  this.setSize(this.maxLengthFromEnumValues(this.getEnumValues()));
                  this.setClosedBrace(")");
               }
            } else {
               this.setDatatypeName("TEXT");
               this.setOpenBrace((String)null);
               this.setSize((String)null);
               this.setClosedBrace((String)null);
            }
         } else {
            this.setDatatypeName("CHAR");
            this.setNational("NATIONAL");
            this.setVarying("VARYING");
         }

         if (this.getAscii() != null) {
            this.setAscii((String)null);
         }

         if (this.getUnicode() != null) {
            this.setUnicode((String)null);
         }

         if (this.getByte() != null) {
            this.setByte((String)null);
         }
      }

   }

   public void toMySQLString() {
      this.setNotLogged((String)null);
      if (this.getDatatypeName() != null) {
         String changeMySQLDatatype = this.getDatatypeName();
         if (SwisSQLOptions.fromAccess && changeMySQLDatatype.equalsIgnoreCase("LONGCHAR")) {
            this.setDatatypeName("TEXT");
            this.setOpenBrace((String)null);
            this.setSize((String)null);
            this.setClosedBrace((String)null);
         } else if (changeMySQLDatatype.equalsIgnoreCase("VARCHAR2")) {
            this.setDatatypeName("VARCHAR");
         }

         if (this.getBinary() != null) {
            this.setBinary("BINARY");
         } else if (!changeMySQLDatatype.equalsIgnoreCase("NVARCHAR2") && !changeMySQLDatatype.equalsIgnoreCase("NVARCHAR")) {
            if (changeMySQLDatatype.equalsIgnoreCase("NCHAR")) {
               this.setNational("NATIONAL");
               this.setDatatypeName("CHAR");
            } else if (!changeMySQLDatatype.equalsIgnoreCase("CLOB") && !changeMySQLDatatype.equalsIgnoreCase("NCLOB") && !changeMySQLDatatype.equalsIgnoreCase("DBCLOB") && !changeMySQLDatatype.equalsIgnoreCase("LONG") && !changeMySQLDatatype.equalsIgnoreCase("LONG VARCHAR") && !changeMySQLDatatype.equalsIgnoreCase("NTEXT")) {
               if (changeMySQLDatatype.equalsIgnoreCase("\"CHAR\"")) {
                  this.setDatatypeName("CHAR");
                  this.setOpenBrace("(");
                  this.setSize("1");
                  this.setClosedBrace(")");
               } else if (changeMySQLDatatype.equalsIgnoreCase("BPCHAR")) {
                  this.setDatatypeName("CHAR");
               } else if (changeMySQLDatatype.equalsIgnoreCase("VARCHAR") && this.getUnicode() != null) {
                  this.setNational("NATIONAL");
                  this.setDatatypeName("VARCHAR");
               } else if (changeMySQLDatatype.equalsIgnoreCase("CHAR") && this.getUnicode() != null) {
                  this.setNational("NATIONAL");
                  this.setDatatypeName("CHAR");
               }
            } else {
               this.setDatatypeName("LONGTEXT");
               this.setOpenBrace((String)null);
               this.setSize((String)null);
               this.setClosedBrace((String)null);
            }
         } else {
            this.setNational("NATIONAL");
            this.setDatatypeName("VARCHAR");
         }

         if (this.getAscii() != null) {
            this.setAscii((String)null);
         }

         if (this.getUnicode() != null) {
            this.setUnicode((String)null);
         }

         if (this.getByte() != null) {
            this.setByte((String)null);
         }

         if ((this.getDatatypeName().equalsIgnoreCase("VARCHAR") || this.getDatatypeName().equalsIgnoreCase("CHAR") || this.getDatatypeName().equalsIgnoreCase("NCHAR") || this.getDatatypeName().equalsIgnoreCase("NVARCHAR") || this.getDatatypeName().equalsIgnoreCase("CHARACTER")) && this.size != null) {
            int temp = Integer.parseInt(this.size);
            if (this.getVarying() != null && temp < 255) {
               this.setDatatypeName("VARCHAR");
            }

            if (temp > 255) {
               this.setDatatypeName("text");
               this.setSize((String)null);
               this.setOpenBrace((String)null);
               this.setClosedBrace((String)null);
               this.setNational((String)null);
            }
         }

         if (this.getArray() != null) {
            this.setArray((String)null);
         }
      }

   }

   public void toSnowflakeString() {
      this.setNotLogged((String)null);
      if (this.getDatatypeName() != null) {
         String changeSnowflakeDatatype = this.getDatatypeName();
         if (this.getBinary() != null) {
            this.setDatatypeName("BINARY");
         } else if (changeSnowflakeDatatype.equalsIgnoreCase("VARCHAR2")) {
            this.setDatatypeName("VARCHAR");
         } else if (!changeSnowflakeDatatype.equalsIgnoreCase("NVARCHAR2") && !changeSnowflakeDatatype.equalsIgnoreCase("NVARCHAR")) {
            if (changeSnowflakeDatatype.equalsIgnoreCase("NCHAR")) {
               this.setDatatypeName("VARCHAR");
               this.setNational("NATIONAL");
            } else if (!changeSnowflakeDatatype.equalsIgnoreCase("CLOB") && !changeSnowflakeDatatype.equalsIgnoreCase("NCLOB") && !changeSnowflakeDatatype.equalsIgnoreCase("DBCLOB") && !changeSnowflakeDatatype.equalsIgnoreCase("LONG") && !changeSnowflakeDatatype.equalsIgnoreCase("NTEXT") && !changeSnowflakeDatatype.equalsIgnoreCase("LONG VARCHAR")) {
               if (changeSnowflakeDatatype.equalsIgnoreCase("BPCHAR")) {
                  this.setDatatypeName("STRING");
               } else if (changeSnowflakeDatatype.equalsIgnoreCase("ENUM")) {
                  this.setDatatypeName("VARCHAR");
                  this.setOpenBrace("(");
                  this.setSize(this.maxLengthFromEnumValues(this.getEnumValues()));
                  this.setClosedBrace(")");
               }
            } else {
               this.setDatatypeName("TEXT");
               this.setOpenBrace((String)null);
               this.setSize((String)null);
               this.setClosedBrace((String)null);
            }
         } else {
            this.setDatatypeName("VARCHAR");
            this.setNational("NATIONAL");
            this.setVarying("VARYING");
         }

         if (this.getAscii() != null) {
            this.setAscii((String)null);
         }

         if (this.getUnicode() != null) {
            this.setUnicode((String)null);
         }

         if (this.getByte() != null) {
            this.setByte((String)null);
         }
      }

   }

   public void toSapHanaString() {
      this.setNotLogged((String)null);
      if (this.getDatatypeName() != null) {
         String changeSapHanaDatatype = this.getDatatypeName();
         if (this.getBinary() != null) {
            this.setDatatypeName("VARBINARY");
         } else if (changeSapHanaDatatype.equalsIgnoreCase("VARCHAR2")) {
            this.setDatatypeName("VARCHAR");
         } else if (!changeSapHanaDatatype.equalsIgnoreCase("NVARCHAR2") && !changeSapHanaDatatype.equalsIgnoreCase("NVARCHAR")) {
            if (changeSapHanaDatatype.equalsIgnoreCase("NCHAR")) {
               this.setDatatypeName("VARCHAR");
               this.setNational("NATIONAL");
            } else if (!changeSapHanaDatatype.equalsIgnoreCase("CLOB") && !changeSapHanaDatatype.equalsIgnoreCase("NCLOB") && !changeSapHanaDatatype.equalsIgnoreCase("DBCLOB") && !changeSapHanaDatatype.equalsIgnoreCase("LONG") && !changeSapHanaDatatype.equalsIgnoreCase("NTEXT") && !changeSapHanaDatatype.equalsIgnoreCase("LONG VARCHAR")) {
               if (changeSapHanaDatatype.equalsIgnoreCase("BPCHAR")) {
                  this.setDatatypeName("TEXT");
               } else if (changeSapHanaDatatype.equalsIgnoreCase("ENUM")) {
                  this.setDatatypeName("VARCHAR");
                  this.setOpenBrace("(");
                  this.setSize(this.maxLengthFromEnumValues(this.getEnumValues()));
                  this.setClosedBrace(")");
               }
            } else {
               this.setDatatypeName("TEXT");
               this.setOpenBrace((String)null);
               this.setSize((String)null);
               this.setClosedBrace((String)null);
            }
         } else {
            this.setDatatypeName("VARCHAR");
            this.setNational("NATIONAL");
            this.setVarying("VARYING");
         }

         if (this.getAscii() != null) {
            this.setAscii((String)null);
         }

         if (this.getUnicode() != null) {
            this.setUnicode((String)null);
         }

         if (this.getByte() != null) {
            this.setByte((String)null);
         }
      }

   }

   public void toSqliteString() {
      this.setNotLogged((String)null);
      if (this.getDatatypeName() != null) {
         String changeSqliteDatatype = this.getDatatypeName();
         if (this.getBinary() != null) {
            this.setDatatypeName("BLOB");
         } else if (!changeSqliteDatatype.equalsIgnoreCase("VARCHAR2") && !changeSqliteDatatype.equalsIgnoreCase("NVARCHAR2") && !changeSqliteDatatype.equalsIgnoreCase("NVARCHAR") && !changeSqliteDatatype.equalsIgnoreCase("NCHAR") && !changeSqliteDatatype.equalsIgnoreCase("BPCHAR")) {
            if (!changeSqliteDatatype.equalsIgnoreCase("CLOB") && !changeSqliteDatatype.equalsIgnoreCase("NCLOB") && !changeSqliteDatatype.equalsIgnoreCase("DBCLOB") && !changeSqliteDatatype.equalsIgnoreCase("LONG") && !changeSqliteDatatype.equalsIgnoreCase("NTEXT") && !changeSqliteDatatype.equalsIgnoreCase("LONG VARCHAR")) {
               if (changeSqliteDatatype.equalsIgnoreCase("ENUM")) {
                  this.setDatatypeName("TEXT");
                  this.setOpenBrace("(");
                  this.setSize(this.maxLengthFromEnumValues(this.getEnumValues()));
                  this.setClosedBrace(")");
               }
            } else {
               this.setDatatypeName("TEXT");
               this.setOpenBrace((String)null);
               this.setSize((String)null);
               this.setClosedBrace((String)null);
            }
         } else {
            this.setDatatypeName("TEXT");
         }

         if (this.getAscii() != null) {
            this.setAscii((String)null);
         }

         if (this.getUnicode() != null) {
            this.setUnicode((String)null);
         }

         if (this.getByte() != null) {
            this.setByte((String)null);
         }
      }

   }

   public void toAthenaString() throws ConvertException {
      this.setNotLogged((String)null);
      if (this.getDatatypeName() != null) {
         String changeAthenaDatatype = this.getDatatypeName();
         if (this.getBinary() != null) {
            this.setBinary((String)null);
            this.setDatatypeName("VARBINARY");
            this.setSize((String)null);
            this.setClosedBrace((String)null);
            this.setOpenBrace((String)null);
         } else if (changeAthenaDatatype.equalsIgnoreCase("CHARACTER")) {
            if (this.getVarying() != null) {
               this.setDatatypeName("STRING");
               this.setVarying((String)null);
            } else {
               this.setDatatypeName("VARCHAR");
            }
         } else if (changeAthenaDatatype.equalsIgnoreCase("VARCHAR2")) {
            this.setDatatypeName("STRING");
         } else if (!changeAthenaDatatype.equalsIgnoreCase("NVARCHAR2") && !changeAthenaDatatype.equalsIgnoreCase("NVARCHAR")) {
            if (changeAthenaDatatype.equalsIgnoreCase("NCHAR")) {
               this.setDatatypeName("STRING");
            } else if (!changeAthenaDatatype.equalsIgnoreCase("CLOB") && !changeAthenaDatatype.equalsIgnoreCase("NCLOB") && !changeAthenaDatatype.equalsIgnoreCase("DBCLOB") && !changeAthenaDatatype.equalsIgnoreCase("LONG") && !changeAthenaDatatype.equalsIgnoreCase("NTEXT") && !changeAthenaDatatype.equalsIgnoreCase("LONG VARCHAR")) {
               if (changeAthenaDatatype.equalsIgnoreCase("CHAR") && this.getUnicode() != null) {
                  this.setDatatypeName("STRING");
               } else if (changeAthenaDatatype.equalsIgnoreCase("BPCHAR")) {
                  this.setDatatypeName("STRING");
               } else if (changeAthenaDatatype.equalsIgnoreCase("VARCHAR") && this.getUnicode() != null) {
                  this.setDatatypeName("STRING");
               } else if (changeAthenaDatatype.equalsIgnoreCase("ENUM")) {
                  this.setDatatypeName("STRING");
                  this.setOpenBrace("(");
                  this.setSize(this.maxLengthFromEnumValues(this.getEnumValues()));
                  this.setClosedBrace(")");
               }
            } else {
               this.setDatatypeName("STRING");
               this.setOpenBrace((String)null);
               this.setSize((String)null);
               this.setClosedBrace((String)null);
            }
         } else {
            this.setDatatypeName("STRING");
         }

         if (this.getAscii() != null) {
            this.setAscii((String)null);
         }

         if (this.getUnicode() != null) {
            this.setUnicode((String)null);
         }

         if (this.getByte() != null) {
            this.setByte((String)null);
         }
      }

   }

   public void toANSIString() {
      this.setNotLogged((String)null);
      if (this.getDatatypeName() != null) {
         String changeANSIDatatype = this.getDatatypeName();
         if (this.getBinary() != null) {
            this.setBinary((String)null);
         }

         if (!changeANSIDatatype.equalsIgnoreCase("CLOB") && !changeANSIDatatype.equalsIgnoreCase("DBCLOB")) {
            if (!changeANSIDatatype.equalsIgnoreCase("TEXT") && !changeANSIDatatype.equalsIgnoreCase("NTEXT")) {
               if (changeANSIDatatype.equalsIgnoreCase("VARCHAR2")) {
                  this.setDatatypeName("VARCHAR");
               } else if (changeANSIDatatype.equalsIgnoreCase("NVARCHAR2")) {
                  this.setDatatypeName("NVARCHAR");
               } else if (changeANSIDatatype.equalsIgnoreCase("NCHAR")) {
                  this.setDatatypeName("CHAR");
               } else if (changeANSIDatatype.equalsIgnoreCase("LONG VARCHAR")) {
                  this.setDatatypeName("LONG");
               } else if (changeANSIDatatype.equalsIgnoreCase("\"CHAR\"")) {
                  this.setDatatypeName("CHAR");
                  this.setOpenBrace("(");
                  this.setSize("1");
                  this.setClosedBrace(")");
               } else if (changeANSIDatatype.equalsIgnoreCase("BPCHAR")) {
                  this.setDatatypeName("CHAR");
               } else if (changeANSIDatatype.equalsIgnoreCase("VARCHAR") && this.getUnicode() != null) {
                  this.setDatatypeName("NVARCHAR");
               } else if (changeANSIDatatype.equalsIgnoreCase("CHAR") && this.getUnicode() != null) {
                  this.setDatatypeName("NCHAR");
               } else if (changeANSIDatatype.equalsIgnoreCase("ENUM")) {
                  this.setDatatypeName("VARCHAR");
                  this.setOpenBrace("(");
                  this.setSize(this.maxLengthFromEnumValues(this.getEnumValues()));
                  this.setClosedBrace(")");
               }
            } else {
               this.setDatatypeName("LONG");
            }
         } else {
            this.setDatatypeName("CLOB");
            if (this.getSize() != null) {
               this.setOpenBrace((String)null);
               this.setSize((String)null);
               this.setClosedBrace((String)null);
            }
         }

         if (this.getAscii() != null) {
            this.setAscii((String)null);
         }

         if (this.getUnicode() != null) {
            this.setUnicode((String)null);
         }

         if (this.getByte() != null) {
            this.setByte((String)null);
         }

         if (this.getArray() != null) {
            this.setArray((String)null);
         }
      }

   }

   public void toTimesTenString() {
      this.setNotLogged((String)null);
      if (this.getDatatypeName() != null) {
         String sourceType = this.getDatatypeName();
         if (this.getBinary() != null) {
            this.setBinary((String)null);
         }

         if (sourceType.equalsIgnoreCase("NCHAR") && this.getVarying() != null) {
            this.setDatatypeName("NVARCHAR");
         } else if (sourceType.equalsIgnoreCase("CHAR") || sourceType.equalsIgnoreCase("CHARACTER")) {
            if (this.getNational() != null && this.getVarying() != null) {
               this.setDatatypeName("NVARCHAR");
               if (this.size == null) {
                  this.setOpenBrace("(");
                  this.setClosedBrace(")");
                  this.setSize("1");
               }
            }

            if (this.getNational() != null && this.getVarying() == null) {
               this.setDatatypeName("NCHAR");
            }

            if (this.getNational() == null && this.getVarying() != null) {
               this.setDatatypeName("VARCHAR");
            }
         }

         this.setNational((String)null);
         this.setVarying((String)null);
         if (sourceType.equalsIgnoreCase("VARCHAR2")) {
            this.setDatatypeName("VARCHAR");
         } else if (sourceType.equalsIgnoreCase("NVARCHAR2")) {
            this.setDatatypeName("NVARCHAR");
         } else if (!sourceType.equalsIgnoreCase("CLOB") && !sourceType.equalsIgnoreCase("DBCLOB") && !sourceType.equalsIgnoreCase("LONG") && !sourceType.equalsIgnoreCase("LONG VARCHAR") && !sourceType.equalsIgnoreCase("NTEXT") && !sourceType.equalsIgnoreCase("TEXT")) {
            if (sourceType.equalsIgnoreCase("NCLOB")) {
               this.setDatatypeName("NVARCHAR");
               this.setOpenBrace("(");
               this.setSize("2097152");
               this.setClosedBrace(")");
            } else if (sourceType.equalsIgnoreCase("\"CHAR\"")) {
               this.setDatatypeName("CHAR");
            } else if (sourceType.equalsIgnoreCase("BPCHAR")) {
               this.setDatatypeName("CHAR");
            } else if (sourceType.equalsIgnoreCase("UNICHAR")) {
               this.setDatatypeName("NCHAR");
               this.setOpenBrace("(");
               this.setSize("4150");
               this.setClosedBrace(")");
            } else if (sourceType.equalsIgnoreCase("UNIVARCHAR")) {
               this.setDatatypeName("NVARCHAR");
               this.setOpenBrace("(");
               this.setSize("2097152");
               this.setClosedBrace(")");
            } else if (sourceType.equalsIgnoreCase("ENUM")) {
               this.setDatatypeName("VARCHAR");
               this.setOpenBrace("(");
               this.setSize(this.maxLengthFromEnumValues(this.getEnumValues()));
               this.setClosedBrace(")");
            } else if ((sourceType.equalsIgnoreCase("VARCHAR") || sourceType.equalsIgnoreCase("NVARCHAR")) && this.size == null) {
               this.setOpenBrace("(");
               this.setClosedBrace(")");
               this.setSize("1");
            } else if (sourceType.equalsIgnoreCase("CHAR") && this.size != null) {
               if (Integer.parseInt(this.size) > 8300) {
                  this.setSize("8300");
               }
            } else if (sourceType.equalsIgnoreCase("VARCHAR") && this.size != null) {
               if (Integer.parseInt(this.size) > 4194304) {
                  this.setSize("4194304");
               }
            } else if (sourceType.equalsIgnoreCase("NVARCHAR") && this.size != null) {
               if (Integer.parseInt(this.size) > 2097152) {
                  this.setSize("2097152");
               }
            } else if (sourceType.equalsIgnoreCase("NCHAR") && this.size != null && Integer.parseInt(this.size) > 4150) {
               this.setSize("4150");
            }
         } else {
            this.setDatatypeName("VARCHAR");
            this.setOpenBrace("(");
            this.setSize("4194304");
            this.setClosedBrace(")");
         }

         if (this.getAscii() != null) {
            this.setAscii((String)null);
         }

         if (this.getUnicode() != null) {
            this.setUnicode((String)null);
         }

         if (this.getByte() != null) {
            this.setByte((String)null);
         }

         if (this.getArray() != null) {
            this.setArray((String)null);
         }
      }

   }

   public void toNetezzaString() {
      String varcharMaxSize = "32760";
      String nvarcharMaxSize = "8192";
      this.setNotLogged((String)null);
      if (this.getDatatypeName() != null) {
         String changeNetezzaDatatype = this.getDatatypeName();
         if (this.getBinary() != null) {
            this.setBinary((String)null);
         }

         if (!changeNetezzaDatatype.equalsIgnoreCase("CLOB") && !changeNetezzaDatatype.equalsIgnoreCase("DBCLOB")) {
            if (!changeNetezzaDatatype.equalsIgnoreCase("TEXT") && !changeNetezzaDatatype.equalsIgnoreCase("NTEXT")) {
               if (changeNetezzaDatatype.equalsIgnoreCase("VARCHAR2")) {
                  this.setDatatypeName("VARCHAR");

                  try {
                     if (this.getSize() != null && Integer.parseInt(this.getSize()) < 17) {
                        this.setDatatypeName("CHAR");
                     }
                  } catch (NumberFormatException var7) {
                     var7.printStackTrace();
                  }
               } else {
                  int nvarcharSize;
                  if (changeNetezzaDatatype.equalsIgnoreCase("NVARCHAR2")) {
                     this.setDatatypeName("NVARCHAR");

                     try {
                        nvarcharSize = Integer.parseInt(this.getSize());
                        if (nvarcharSize > 8192) {
                           this.setSize(nvarcharMaxSize);
                        }
                     } catch (NumberFormatException var6) {
                     }
                  } else if (changeNetezzaDatatype.equalsIgnoreCase("NCHAR")) {
                     this.setDatatypeName("NVARCHAR");

                     try {
                        nvarcharSize = Integer.parseInt(this.getSize());
                        if (nvarcharSize > 8192) {
                           this.setSize(nvarcharMaxSize);
                        }
                     } catch (NumberFormatException var5) {
                     }
                  } else if (!changeNetezzaDatatype.equalsIgnoreCase("LONG") && !changeNetezzaDatatype.equalsIgnoreCase("LONG VARCHAR")) {
                     if (changeNetezzaDatatype.equalsIgnoreCase("\"CHAR\"")) {
                        this.setDatatypeName("CHAR");
                     } else if (changeNetezzaDatatype.equalsIgnoreCase("BPCHAR")) {
                        this.setDatatypeName("CHAR");
                     } else if (changeNetezzaDatatype.equalsIgnoreCase("VARCHAR") && this.getUnicode() != null) {
                        this.setDatatypeName("VARCHAR");
                     } else if (changeNetezzaDatatype.equalsIgnoreCase("CHAR") && this.getUnicode() != null) {
                        this.setDatatypeName("VARCHAR");
                     } else if (changeNetezzaDatatype.equalsIgnoreCase("ENUM")) {
                        this.setDatatypeName("VARCHAR");
                        this.setOpenBrace("(");
                        this.setSize(this.maxLengthFromEnumValues(this.getEnumValues()));
                        this.setClosedBrace(")");
                     } else if (changeNetezzaDatatype.equalsIgnoreCase("NCLOB")) {
                        this.setDatatypeName("NVARCHAR");
                        this.setOpenBrace("(");
                        this.setSize(nvarcharMaxSize);
                        this.setClosedBrace(")");
                     }
                  } else {
                     this.setDatatypeName("VARCHAR");
                     this.setOpenBrace("(");
                     this.setSize(varcharMaxSize);
                     this.setClosedBrace(")");
                  }
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

         if (this.getAscii() != null) {
            this.setAscii((String)null);
         }

         if (this.getUnicode() != null) {
            this.setUnicode((String)null);
         }

         if (this.getByte() != null) {
            this.setByte((String)null);
         }
      }

   }

   public void toTeradataString() {
      this.setNotLogged((String)null);
      if (this.getDatatypeName() != null) {
         String changeTeradataDatatype = this.getDatatypeName();
         if (this.getBinary() != null) {
            this.setBinary((String)null);
         }

         if (!changeTeradataDatatype.equalsIgnoreCase("CLOB") && !changeTeradataDatatype.equalsIgnoreCase("DBCLOB")) {
            if (!changeTeradataDatatype.equalsIgnoreCase("TEXT") && !changeTeradataDatatype.equalsIgnoreCase("NTEXT")) {
               if (changeTeradataDatatype.equalsIgnoreCase("VARCHAR2")) {
                  this.setDatatypeName("VARCHAR");
               } else if (changeTeradataDatatype.equalsIgnoreCase("NVARCHAR2")) {
                  this.setDatatypeName("NVARCHAR");
               } else if (changeTeradataDatatype.equalsIgnoreCase("NCHAR")) {
                  this.setDatatypeName("CHAR");
               } else if (changeTeradataDatatype.equalsIgnoreCase("LONG VARCHAR")) {
                  this.setDatatypeName("LONG");
               } else if (changeTeradataDatatype.equalsIgnoreCase("\"CHAR\"")) {
                  this.setDatatypeName("CHAR");
                  this.setOpenBrace("(");
                  this.setSize("1");
                  this.setClosedBrace(")");
               } else if (changeTeradataDatatype.equalsIgnoreCase("BPCHAR")) {
                  this.setDatatypeName("CHAR");
               } else if (changeTeradataDatatype.equalsIgnoreCase("VARCHAR") && this.getUnicode() != null) {
                  this.setDatatypeName("NVARCHAR");
               } else if (changeTeradataDatatype.equalsIgnoreCase("CHAR") && this.getUnicode() != null) {
                  this.setDatatypeName("NCHAR");
               } else if (changeTeradataDatatype.equalsIgnoreCase("ENUM")) {
                  this.setDatatypeName("VARCHAR");
                  this.setOpenBrace("(");
                  this.setSize(this.maxLengthFromEnumValues(this.getEnumValues()));
                  this.setClosedBrace(")");
               }
            } else {
               this.setDatatypeName("LONG");
            }
         } else {
            this.setDatatypeName("CLOB");
            if (this.getSize() != null) {
               this.setOpenBrace((String)null);
               this.setSize((String)null);
               this.setClosedBrace((String)null);
            }
         }

         if (this.getAscii() != null) {
            this.setAscii((String)null);
         }

         if (this.getUnicode() != null) {
            this.setUnicode((String)null);
         }

         if (this.getByte() != null) {
            this.setByte((String)null);
         }

         if (this.getArray() != null) {
            this.setArray((String)null);
         }
      }

   }

   public Datatype copyObjectValues() {
      CharacterClass newCharacterClass = new CharacterClass();
      newCharacterClass.setClosedBrace(this.closedBrace);
      newCharacterClass.setDatatypeName(this.getDatatypeName());
      newCharacterClass.setOpenBrace(this.openBrace);
      newCharacterClass.setSize(this.getSize());
      newCharacterClass.setNational(this.getNational());
      newCharacterClass.setVarying(this.getVarying());
      newCharacterClass.setBinary(this.getBinary());
      newCharacterClass.setAscii(this.getAscii());
      newCharacterClass.setUnicode(this.getUnicode());
      newCharacterClass.setByte(this.getByte());
      newCharacterClass.setEnumValues(this.getEnumValues());
      newCharacterClass.setSetValues(this.getSetValues());
      newCharacterClass.setNotLogged(this.getNotLogged());
      newCharacterClass.setArray(this.getArray());
      return newCharacterClass;
   }

   private String maxLengthFromEnumValues(ArrayList enumValues) {
      int max = enumValues.get(0).toString().length();

      for(int i = 1; i < enumValues.size(); ++i) {
         int current = enumValues.get(i).toString().length();
         if (max < current) {
            max = current;
         }
      }

      return (new Integer(max)).toString();
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.nationalType != null) {
         sb.append(this.nationalType + " ");
      }

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

      if (this.enumValues != null) {
         for(int i = 0; i < this.enumValues.size(); ++i) {
            sb.append(this.enumValues.get(i).toString());
            if (i != this.enumValues.size() - 1) {
               sb.append(",");
            }
         }
      }

      if (this.closedBrace != null) {
         sb.append(this.closedBrace);
      }

      if (this.notlogged != null) {
         sb.append(this.notlogged);
      }

      if (this.doBinarySearch != null) {
         sb.append(" " + this.doBinarySearch + " ");
      }

      if (this.ascii != null) {
         sb.append(" " + this.ascii + " ");
      }

      if (this.unicode != null) {
         sb.append(" " + this.unicode + " ");
      }

      if (this.SAPbyte != null) {
         sb.append(" " + this.SAPbyte + " ");
      }

      if (this.arrayStr != null) {
         sb.append(this.arrayStr);
      }

      if (this.caseSpecificPhrase != null) {
         sb.append(" " + this.caseSpecificPhrase + " ");
      }

      return sb.toString();
   }

   public void setArray(String arrayStr) {
      this.arrayStr = arrayStr;
   }

   public String getArray() {
      return this.arrayStr;
   }
}
