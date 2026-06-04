package com.adventnet.swissqlapi.sql.statement.create;

import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.config.SwisSQLOptions;

public class NumericClass implements Datatype {
   private String datatypeName;
   private String openBrace;
   private String closedBrace;
   private String precision;
   private String scale;
   private String arrayStr;
   private boolean isTenroxRequirement = false;

   public void setDatatypeName(String datatypeName) {
      this.datatypeName = datatypeName;
   }

   public void setOpenBrace(String openBrace) {
      this.openBrace = openBrace;
   }

   public void setClosedBrace(String closedBrace) {
      this.closedBrace = closedBrace;
   }

   public void setSize(String size) {
      if (size != null && size.indexOf(",") != -1) {
         this.setPrecision(size.substring(0, size.indexOf(",")));
         this.setScale(size.substring(size.indexOf(",") + 1));
      } else {
         this.setPrecision(size);
      }

   }

   public void setPrecision(String precision) {
      this.precision = precision;
   }

   public void setScale(String scale) {
      this.scale = scale;
   }

   public String getDatatypeName() {
      return this.datatypeName;
   }

   public String getPrecision() {
      return this.precision;
   }

   public String getScale() {
      return this.scale;
   }

   public String getOpenBrace() {
      return this.openBrace;
   }

   public String getClosedBrace() {
      return this.closedBrace;
   }

   public String getSize() {
      return null;
   }

   public void toInformixString() {
      if (this.getDatatypeName() != null) {
         String changeIfxDatatype = this.getDatatypeName();
         if (changeIfxDatatype.equalsIgnoreCase("TINYINT")) {
            this.setDatatypeName("SMALLINT");
         } else if (!changeIfxDatatype.equalsIgnoreCase("FLOAT") && !changeIfxDatatype.equalsIgnoreCase("FLOAT8") && !changeIfxDatatype.equalsIgnoreCase("FLOAT4")) {
            if (changeIfxDatatype.equalsIgnoreCase("BIGINT")) {
               this.setDatatypeName("INTEGER");
            } else if (changeIfxDatatype.equalsIgnoreCase("MEDIUMINT")) {
               this.setDatatypeName("INTEGER");
            } else if (changeIfxDatatype.equalsIgnoreCase("MONEY")) {
               this.setDatatypeName("MONEY");
               this.setOpenBrace("(");
               this.setPrecision("19");
               this.setScale("4");
               this.setClosedBrace(")");
            } else if (changeIfxDatatype.equalsIgnoreCase("SMALLMONEY")) {
               this.setDatatypeName("MONEY");
               this.setOpenBrace("(");
               this.setPrecision("10");
               this.setScale("4");
               this.setClosedBrace(")");
            } else if (!changeIfxDatatype.equalsIgnoreCase("NUMBER") && !changeIfxDatatype.equalsIgnoreCase("NUM")) {
               if (changeIfxDatatype.equalsIgnoreCase("DOUBLE")) {
                  this.setDatatypeName("DOUBLE PRECISION");
               } else if (changeIfxDatatype.equalsIgnoreCase("FIXED")) {
                  this.setDatatypeName("NUMERIC");
               }
            } else {
               this.setDatatypeName("NUMERIC");
            }
         } else if (this.getPrecision() == null) {
            this.setDatatypeName("FLOAT");
         } else {
            if (Integer.parseInt(this.getPrecision()) >= 8 && !changeIfxDatatype.equalsIgnoreCase("FLOAT4")) {
               this.setDatatypeName("FLOAT");
            } else {
               this.setDatatypeName("SMALLFLOAT");
            }

            this.setPrecision((String)null);
            this.setOpenBrace((String)null);
            this.setClosedBrace((String)null);
         }

         if (this.getArray() != null) {
            this.setArray((String)null);
         }
      }

   }

   public void toDB2String() {
      if (this.getDatatypeName() != null) {
         String changeDB2Datatype = this.getDatatypeName();
         if (!SwisSQLOptions.fromAccess || !changeDB2Datatype.equalsIgnoreCase("INTEGER") && !changeDB2Datatype.equalsIgnoreCase("REAL") && !changeDB2Datatype.equalsIgnoreCase("SMALLINT")) {
            if (this.getPrecision() == null || !changeDB2Datatype.equalsIgnoreCase("INT") && !changeDB2Datatype.equalsIgnoreCase("INTEGER") && !changeDB2Datatype.equalsIgnoreCase("INT2") && !changeDB2Datatype.equalsIgnoreCase("INT4") && !changeDB2Datatype.equalsIgnoreCase("MEDIUMINT") && !changeDB2Datatype.equalsIgnoreCase("TINYINT") && !changeDB2Datatype.equalsIgnoreCase("SMALLINT") && !changeDB2Datatype.equalsIgnoreCase("SIGNED")) {
               if (SwisSQLOptions.fromAccess && changeDB2Datatype.equalsIgnoreCase("BYTE")) {
                  this.setDatatypeName("SMALLINT");
                  this.setOpenBrace((String)null);
                  this.setSize((String)null);
                  this.setClosedBrace((String)null);
               } else if (SwisSQLOptions.fromAccess && changeDB2Datatype.equalsIgnoreCase("COUNTER")) {
                  this.setDatatypeName("INT GENERATED BY DEFAULT AS IDENTITY(START WITH 1 INCREMENT BY 1)");
                  this.setOpenBrace((String)null);
                  this.setSize((String)null);
                  this.setClosedBrace((String)null);
               } else if (SwisSQLOptions.fromAccess && changeDB2Datatype.equalsIgnoreCase("CURRENCY")) {
                  this.setDatatypeName("DECIMAL");
               } else if (this.getPrecision() != null && this.getScale() != null && changeDB2Datatype.equalsIgnoreCase("FLOAT")) {
                  this.setDatatypeName("DECIMAL");
               } else if (changeDB2Datatype.equalsIgnoreCase("BIGINT")) {
                  this.setDatatypeName("BIGINT");
                  this.setPrecision((String)null);
                  this.setOpenBrace((String)null);
                  this.setClosedBrace((String)null);
               } else if (changeDB2Datatype.equalsIgnoreCase("MEDIUMINT")) {
                  this.setDatatypeName("INTEGER");
               } else if (changeDB2Datatype.equalsIgnoreCase("TINYINT")) {
                  this.setDatatypeName("SMALLINT");
               } else if (changeDB2Datatype.equalsIgnoreCase("NUMBER")) {
                  this.setDatatypeName("NUM");
               } else if (changeDB2Datatype.equalsIgnoreCase("INT2")) {
                  this.setDatatypeName("SMALLINT");
               } else if (changeDB2Datatype.equalsIgnoreCase("INT4")) {
                  this.setDatatypeName("INTEGER");
               } else if (changeDB2Datatype.equalsIgnoreCase("INT8")) {
                  this.setDatatypeName("INTEGER");
               } else if (changeDB2Datatype.equalsIgnoreCase("FLOAT4")) {
                  this.setDatatypeName("FLOAT");
                  this.setOpenBrace("(");
                  this.setPrecision("24");
                  this.setClosedBrace(")");
               } else if (changeDB2Datatype.equalsIgnoreCase("FLOAT8")) {
                  this.setDatatypeName("FLOAT");
                  this.setOpenBrace("(");
                  this.setPrecision("48");
                  this.setClosedBrace(")");
               } else if (changeDB2Datatype.equalsIgnoreCase("MONEY")) {
                  this.setDatatypeName("DECIMAL");
                  this.setOpenBrace("(");
                  this.setPrecision("19");
                  this.setScale("4");
                  this.setClosedBrace(")");
               } else if (changeDB2Datatype.equalsIgnoreCase("SMALLMONEY")) {
                  this.setDatatypeName("DECIMAL");
                  this.setOpenBrace("(");
                  this.setPrecision("10");
                  this.setScale("4");
                  this.setClosedBrace(")");
               } else if (changeDB2Datatype.equalsIgnoreCase("FIXED")) {
                  this.setDatatypeName("DECIMAL");
               } else if (changeDB2Datatype.equalsIgnoreCase("DOUBLE")) {
                  this.setOpenBrace((String)null);
                  this.setPrecision((String)null);
                  this.setScale((String)null);
                  this.setClosedBrace((String)null);
               }
            } else {
               this.setDatatypeName("INTEGER");
               this.setPrecision((String)null);
               this.setOpenBrace((String)null);
               this.setClosedBrace((String)null);
            }
         } else {
            this.setDatatypeName(changeDB2Datatype);
            this.setOpenBrace((String)null);
            this.setSize((String)null);
            this.setClosedBrace((String)null);
         }

         if (this.getArray() != null) {
            this.setArray((String)null);
         }
      }

   }

   public void toOracleString() {
      if (this.getDatatypeName() != null) {
         String changeOracleDatatype = this.getDatatypeName();
         if (!SwisSQLOptions.fromAccess || !changeOracleDatatype.equalsIgnoreCase("INTEGER") && !changeOracleDatatype.equalsIgnoreCase("REAL") && !changeOracleDatatype.equalsIgnoreCase("SMALLINT")) {
            if (SwisSQLOptions.fromAccess && changeOracleDatatype.equalsIgnoreCase("BYTE")) {
               this.setDatatypeName("SMALLINT");
               this.setOpenBrace((String)null);
               this.setSize((String)null);
               this.setClosedBrace((String)null);
            } else if (SwisSQLOptions.fromAccess && changeOracleDatatype.equalsIgnoreCase("COUNTER")) {
               this.setDatatypeName("INT");
               this.setOpenBrace((String)null);
               this.setSize((String)null);
               this.setClosedBrace((String)null);
            } else if (SwisSQLOptions.fromAccess && changeOracleDatatype.equalsIgnoreCase("GUID")) {
               this.setDatatypeName("CHAR(36)");
               this.setOpenBrace((String)null);
               this.setSize((String)null);
               this.setClosedBrace((String)null);
            } else if (SwisSQLOptions.fromAccess && changeOracleDatatype.equalsIgnoreCase("CURRENCY")) {
               this.setDatatypeName("DECIMAL");
            } else if (this.getPrecision() == null || !changeOracleDatatype.equalsIgnoreCase("INT") && !changeOracleDatatype.equalsIgnoreCase("INTEGER") && !changeOracleDatatype.equalsIgnoreCase("INT2") && !changeOracleDatatype.equalsIgnoreCase("INT4") && !changeOracleDatatype.equalsIgnoreCase("MEDIUMINT") && !changeOracleDatatype.equalsIgnoreCase("TINYINT") && !changeOracleDatatype.equalsIgnoreCase("BIGINT") && !changeOracleDatatype.equalsIgnoreCase("SMALLINT")) {
               if (this.getPrecision() != null && this.getScale() != null && changeOracleDatatype.equalsIgnoreCase("FLOAT")) {
                  this.setDatatypeName("DECIMAL");
               } else if (changeOracleDatatype.equalsIgnoreCase("BIGINT")) {
                  this.setDatatypeName("INTEGER");
               } else if (changeOracleDatatype.equalsIgnoreCase("MEDIUMINT")) {
                  this.setDatatypeName("INTEGER");
               } else if (changeOracleDatatype.equalsIgnoreCase("TINYINT")) {
                  if (this.isTenroxRequirement) {
                     this.setDatatypeName("NUMBER");
                     this.setOpenBrace("(");
                     this.setClosedBrace(")");
                     this.setPrecision("11");
                  } else {
                     this.setDatatypeName("SMALLINT");
                  }
               } else if (changeOracleDatatype.equalsIgnoreCase("NUM")) {
                  this.setDatatypeName("NUMBER");
               } else if (changeOracleDatatype.equalsIgnoreCase("INT2")) {
                  this.setDatatypeName("SMALLINT");
               } else if (changeOracleDatatype.equalsIgnoreCase("INT4")) {
                  if (this.isTenroxRequirement) {
                     this.setDatatypeName("NUMBER");
                     this.setOpenBrace("(");
                     this.setClosedBrace(")");
                     this.setPrecision("11");
                  } else {
                     this.setDatatypeName("INTEGER");
                  }
               } else if (changeOracleDatatype.equalsIgnoreCase("INT8")) {
                  if (this.isTenroxRequirement) {
                     this.setDatatypeName("NUMBER");
                     this.setOpenBrace("(");
                     this.setClosedBrace(")");
                     this.setPrecision("11");
                  } else {
                     this.setDatatypeName("INTEGER");
                  }
               } else if (changeOracleDatatype.equalsIgnoreCase("FLOAT4")) {
                  if (this.isTenroxRequirement) {
                     this.setDatatypeName("NUMBER");
                     this.setOpenBrace("(");
                     this.setPrecision("11");
                     this.setScale("2");
                     this.setClosedBrace(")");
                  } else {
                     this.setDatatypeName("FLOAT");
                     this.setOpenBrace("(");
                     this.setPrecision("24");
                     this.setClosedBrace(")");
                  }
               } else if (changeOracleDatatype.equalsIgnoreCase("FLOAT8")) {
                  if (this.isTenroxRequirement) {
                     this.setDatatypeName("NUMBER");
                     this.setOpenBrace("(");
                     this.setPrecision("11");
                     this.setScale("2");
                     this.setClosedBrace(")");
                  } else {
                     this.setDatatypeName("FLOAT");
                     this.setOpenBrace("(");
                     this.setPrecision("48");
                     this.setClosedBrace(")");
                  }
               } else if (changeOracleDatatype.equalsIgnoreCase("DEC")) {
                  if (this.isTenroxRequirement) {
                     this.setDatatypeName("NUMBER");
                     this.setOpenBrace("(");
                     this.setPrecision("11");
                     this.setScale("2");
                     this.setClosedBrace(")");
                  } else {
                     this.setDatatypeName("DECIMAL");
                  }
               } else if (changeOracleDatatype.equalsIgnoreCase("DOUBLE")) {
                  this.setDatatypeName("DOUBLE PRECISION");
                  this.setOpenBrace((String)null);
                  this.setPrecision((String)null);
                  this.setScale((String)null);
                  this.setClosedBrace((String)null);
               } else if (changeOracleDatatype.equalsIgnoreCase("MONEY")) {
                  if (this.isTenroxRequirement) {
                     this.setDatatypeName("NUMBER");
                     this.setOpenBrace("(");
                     this.setPrecision("11");
                     this.setScale("2");
                     this.setClosedBrace(")");
                  } else {
                     this.setDatatypeName("DECIMAL");
                     this.setOpenBrace("(");
                     this.setPrecision("19");
                     this.setScale("4");
                     this.setClosedBrace(")");
                  }
               } else if (changeOracleDatatype.equalsIgnoreCase("SMALLMONEY")) {
                  if (this.isTenroxRequirement) {
                     this.setDatatypeName("NUMBER");
                     this.setOpenBrace("(");
                     this.setPrecision("11");
                     this.setScale("2");
                     this.setClosedBrace(")");
                  } else {
                     this.setDatatypeName("DECIMAL");
                     this.setOpenBrace("(");
                     this.setPrecision("10");
                     this.setScale("4");
                     this.setClosedBrace(")");
                  }
               } else if (!changeOracleDatatype.equalsIgnoreCase("INT") && !changeOracleDatatype.equalsIgnoreCase("SMALLINT") && !changeOracleDatatype.equalsIgnoreCase("INTEGER")) {
                  if (!changeOracleDatatype.equalsIgnoreCase("FLOAT") && !changeOracleDatatype.equalsIgnoreCase("DECIMAL")) {
                     if (changeOracleDatatype.equalsIgnoreCase("FIXED")) {
                        this.setDatatypeName("DECIMAL");
                     }
                  } else if (this.isTenroxRequirement) {
                     this.setDatatypeName("NUMBER");
                     this.setOpenBrace("(");
                     this.setPrecision("11");
                     this.setScale("2");
                     this.setClosedBrace(")");
                  } else {
                     this.setDatatypeName(changeOracleDatatype);
                  }
               } else if (this.isTenroxRequirement) {
                  this.setDatatypeName("NUMBER");
                  this.setOpenBrace("(");
                  this.setClosedBrace(")");
                  this.setPrecision("11");
               } else {
                  this.setDatatypeName(changeOracleDatatype);
               }
            } else {
               this.setPrecisionScale();
               this.setScale((String)null);
               this.setDatatypeName("NUMBER");
            }
         } else {
            this.setDatatypeName(changeOracleDatatype);
            this.setOpenBrace((String)null);
            this.setSize((String)null);
            this.setClosedBrace((String)null);
         }

         if (this.getArray() != null) {
            this.setArray((String)null);
         }
      }

   }

   public void toDenodoString() {
      if (this.getDatatypeName() != null) {
         String changeOracleDatatype = this.getDatatypeName();
         if (!SwisSQLOptions.fromAccess || !changeOracleDatatype.equalsIgnoreCase("INTEGER") && !changeOracleDatatype.equalsIgnoreCase("REAL") && !changeOracleDatatype.equalsIgnoreCase("SMALLINT")) {
            if (SwisSQLOptions.fromAccess && changeOracleDatatype.equalsIgnoreCase("BYTE")) {
               this.setDatatypeName("SMALLINT");
               this.setOpenBrace((String)null);
               this.setSize((String)null);
               this.setClosedBrace((String)null);
            } else if (SwisSQLOptions.fromAccess && changeOracleDatatype.equalsIgnoreCase("COUNTER")) {
               this.setDatatypeName("INT4");
               this.setOpenBrace((String)null);
               this.setSize((String)null);
               this.setClosedBrace((String)null);
            } else if (SwisSQLOptions.fromAccess && changeOracleDatatype.equalsIgnoreCase("GUID")) {
               this.setDatatypeName("CHAR(36)");
               this.setOpenBrace((String)null);
               this.setSize((String)null);
               this.setClosedBrace((String)null);
            } else if (SwisSQLOptions.fromAccess && changeOracleDatatype.equalsIgnoreCase("CURRENCY")) {
               this.setDatatypeName("DECIMAL");
            } else if (this.getPrecision() == null || !changeOracleDatatype.equalsIgnoreCase("INT") && !changeOracleDatatype.equalsIgnoreCase("INTEGER") && !changeOracleDatatype.equalsIgnoreCase("INT2") && !changeOracleDatatype.equalsIgnoreCase("INT4") && !changeOracleDatatype.equalsIgnoreCase("MEDIUMINT") && !changeOracleDatatype.equalsIgnoreCase("TINYINT") && !changeOracleDatatype.equalsIgnoreCase("BIGINT") && !changeOracleDatatype.equalsIgnoreCase("SMALLINT")) {
               if (this.getPrecision() != null && this.getScale() != null && changeOracleDatatype.equalsIgnoreCase("FLOAT")) {
                  this.setDatatypeName("DECIMAL");
               } else if (changeOracleDatatype.equalsIgnoreCase("BIGINT")) {
                  this.setDatatypeName("INTEGER");
               } else if (changeOracleDatatype.equalsIgnoreCase("MEDIUMINT")) {
                  this.setDatatypeName("INTEGER");
               } else if (changeOracleDatatype.equalsIgnoreCase("TINYINT")) {
                  if (this.isTenroxRequirement) {
                     this.setDatatypeName("NUMERIC");
                     this.setOpenBrace("(");
                     this.setClosedBrace(")");
                     this.setPrecision("11");
                  } else {
                     this.setDatatypeName("SMALLINT");
                  }
               } else if (changeOracleDatatype.equalsIgnoreCase("NUM")) {
                  this.setDatatypeName("NUMERIC");
               } else if (changeOracleDatatype.equalsIgnoreCase("INT2")) {
                  this.setDatatypeName("SMALLINT");
               } else if (changeOracleDatatype.equalsIgnoreCase("INT4")) {
                  if (this.isTenroxRequirement) {
                     this.setDatatypeName("NUMERIC");
                     this.setOpenBrace("(");
                     this.setClosedBrace(")");
                     this.setPrecision("11");
                  } else {
                     this.setDatatypeName("INTEGER");
                  }
               } else if (changeOracleDatatype.equalsIgnoreCase("INT8")) {
                  if (this.isTenroxRequirement) {
                     this.setDatatypeName("NUMERIC");
                     this.setOpenBrace("(");
                     this.setClosedBrace(")");
                     this.setPrecision("11");
                  } else {
                     this.setDatatypeName("INTEGER");
                  }
               } else if (changeOracleDatatype.equalsIgnoreCase("FLOAT4")) {
                  if (this.isTenroxRequirement) {
                     this.setDatatypeName("NUMERIC");
                     this.setOpenBrace("(");
                     this.setPrecision("11");
                     this.setScale("2");
                     this.setClosedBrace(")");
                  } else {
                     this.setDatatypeName("FLOAT4");
                  }
               } else if (changeOracleDatatype.equalsIgnoreCase("FLOAT8")) {
                  if (this.isTenroxRequirement) {
                     this.setDatatypeName("NUMERIC");
                     this.setOpenBrace("(");
                     this.setPrecision("11");
                     this.setScale("2");
                     this.setClosedBrace(")");
                  } else {
                     this.setDatatypeName("FLOAT8");
                  }
               } else if (changeOracleDatatype.equalsIgnoreCase("DEC")) {
                  if (this.isTenroxRequirement) {
                     this.setDatatypeName("NUMERIC");
                     this.setOpenBrace("(");
                     this.setPrecision("11");
                     this.setScale("2");
                     this.setClosedBrace(")");
                  } else {
                     this.setDatatypeName("DECIMAL");
                  }
               } else if (changeOracleDatatype.equalsIgnoreCase("DOUBLE")) {
                  this.setDatatypeName("DOUBLE PRECISION");
                  this.setOpenBrace((String)null);
                  this.setPrecision((String)null);
                  this.setScale((String)null);
                  this.setClosedBrace((String)null);
               } else if (changeOracleDatatype.equalsIgnoreCase("MONEY")) {
                  if (this.isTenroxRequirement) {
                     this.setDatatypeName("NUMERIC");
                     this.setOpenBrace("(");
                     this.setPrecision("11");
                     this.setScale("2");
                     this.setClosedBrace(")");
                  } else {
                     this.setDatatypeName("DECIMAL");
                     this.setOpenBrace("(");
                     this.setPrecision("19");
                     this.setScale("4");
                     this.setClosedBrace(")");
                  }
               } else if (changeOracleDatatype.equalsIgnoreCase("SMALLMONEY")) {
                  if (this.isTenroxRequirement) {
                     this.setDatatypeName("NUMERIC");
                     this.setOpenBrace("(");
                     this.setPrecision("11");
                     this.setScale("2");
                     this.setClosedBrace(")");
                  } else {
                     this.setDatatypeName("DECIMAL");
                     this.setOpenBrace("(");
                     this.setPrecision("10");
                     this.setScale("4");
                     this.setClosedBrace(")");
                  }
               } else if (!changeOracleDatatype.equalsIgnoreCase("INT") && !changeOracleDatatype.equalsIgnoreCase("SMALLINT") && !changeOracleDatatype.equalsIgnoreCase("INTEGER")) {
                  if (!changeOracleDatatype.equalsIgnoreCase("FLOAT") && !changeOracleDatatype.equalsIgnoreCase("DECIMAL")) {
                     if (changeOracleDatatype.equalsIgnoreCase("FIXED")) {
                        this.setDatatypeName("DECIMAL");
                     }
                  } else if (this.isTenroxRequirement) {
                     this.setDatatypeName("NUMERIC");
                     this.setOpenBrace("(");
                     this.setPrecision("11");
                     this.setScale("2");
                     this.setClosedBrace(")");
                  } else {
                     this.setDatatypeName(changeOracleDatatype);
                  }
               } else if (this.isTenroxRequirement) {
                  this.setDatatypeName("NUMERIC");
                  this.setOpenBrace("(");
                  this.setClosedBrace(")");
                  this.setPrecision("11");
               } else {
                  this.setDatatypeName(changeOracleDatatype);
               }
            } else {
               this.setPrecisionScale();
               this.setScale((String)null);
               this.setDatatypeName("INTEGER");
            }
         } else {
            this.setDatatypeName(changeOracleDatatype);
            this.setOpenBrace((String)null);
            this.setSize((String)null);
            this.setClosedBrace((String)null);
         }

         if (this.getArray() != null) {
            this.setArray((String)null);
         }
      }

   }

   public void toExcelString() {
   }

   public void toMsAccessJdbcString() {
      if (this.getDatatypeName() != null) {
         String changemsAccessDatatype = this.getDatatypeName();
         if (changemsAccessDatatype.equalsIgnoreCase("BYTE")) {
            this.setDatatypeName("TINYINT");
            this.setOpenBrace((String)null);
            this.setSize((String)null);
            this.setClosedBrace((String)null);
         } else if (changemsAccessDatatype.equalsIgnoreCase("REAL")) {
            this.setDatatypeName("FLOAT");
         } else if (changemsAccessDatatype.equalsIgnoreCase("CURRENCY")) {
            this.setDatatypeName("DECIMAL");
            this.setOpenBrace("(");
            this.setSize("20");
            this.setScale("4");
            this.setClosedBrace(")");
         } else if (changemsAccessDatatype.equalsIgnoreCase("COUNTER")) {
            this.setDatatypeName("INT");
            this.setOpenBrace((String)null);
            this.setSize((String)null);
            this.setClosedBrace((String)null);
         } else if (changemsAccessDatatype.equalsIgnoreCase("DOUBLE")) {
            this.setOpenBrace((String)null);
            this.setSize((String)null);
            this.setClosedBrace((String)null);
         } else if (changemsAccessDatatype.equalsIgnoreCase("INT2")) {
            this.setDatatypeName("SMALLINT");
         } else if (changemsAccessDatatype.equalsIgnoreCase("INT4")) {
            this.setDatatypeName("INTEGER");
         } else if (changemsAccessDatatype.equalsIgnoreCase("INT8")) {
            this.setDatatypeName("INTEGER");
         } else if (changemsAccessDatatype.equalsIgnoreCase("FLOAT4")) {
            this.setDatatypeName("FLOAT");
            this.setOpenBrace("(");
            this.setPrecision("24");
            this.setClosedBrace(")");
         }

         if (!changemsAccessDatatype.equalsIgnoreCase("NUMBER") && !changemsAccessDatatype.equalsIgnoreCase("NUM")) {
            if (changemsAccessDatatype.equalsIgnoreCase("FLOAT8")) {
               this.setDatatypeName("FLOAT");
               this.setOpenBrace("(");
               this.setPrecision("48");
               this.setClosedBrace(")");
            } else if (changemsAccessDatatype.equalsIgnoreCase("MONEY")) {
               this.setDatatypeName("DECIMAL");
               this.setOpenBrace("(");
               this.setPrecision("19");
               this.setScale("4");
               this.setClosedBrace(")");
            } else if (changemsAccessDatatype.equalsIgnoreCase("SMALLMONEY")) {
               this.setDatatypeName("DECIMAL");
               this.setOpenBrace("(");
               this.setPrecision("10");
               this.setScale("4");
               this.setClosedBrace(")");
            } else if (changemsAccessDatatype.equalsIgnoreCase("FIXED")) {
               this.setDatatypeName("DECIMAL");
            } else if (changemsAccessDatatype.equalsIgnoreCase("SIGNED")) {
               this.setDatatypeName("BIGINT");
            }
         } else {
            this.setDatatypeName("NUMERIC");
         }

         if (this.getArray() != null) {
            this.setArray((String)null);
         }
      }

   }

   public void toMSSQLServerString() {
      if (this.getDatatypeName() != null) {
         String changeSQLServerDatatype = this.getDatatypeName();
         if (SwisSQLOptions.fromAccess && (changeSQLServerDatatype.equalsIgnoreCase("INTEGER") || changeSQLServerDatatype.equalsIgnoreCase("REAL") || changeSQLServerDatatype.equalsIgnoreCase("SMALLINT"))) {
            this.setDatatypeName(changeSQLServerDatatype);
            this.setOpenBrace((String)null);
            this.setSize((String)null);
            this.setClosedBrace((String)null);
         } else if (this.getPrecision() == null || !changeSQLServerDatatype.equalsIgnoreCase("INT") && !changeSQLServerDatatype.equalsIgnoreCase("INTEGER") && !changeSQLServerDatatype.equalsIgnoreCase("INT2") && !changeSQLServerDatatype.equalsIgnoreCase("INT4") && !changeSQLServerDatatype.equalsIgnoreCase("MEDIUMINT") && !changeSQLServerDatatype.equalsIgnoreCase("TINYINT") && !changeSQLServerDatatype.equalsIgnoreCase("BIGINT") && !changeSQLServerDatatype.equalsIgnoreCase("SMALLINT")) {
            if (SwisSQLOptions.fromAccess && changeSQLServerDatatype.equalsIgnoreCase("BYTE")) {
               this.setDatatypeName("TINYINT");
               this.setOpenBrace((String)null);
               this.setSize((String)null);
               this.setClosedBrace((String)null);
            } else if (SwisSQLOptions.fromAccess && changeSQLServerDatatype.equalsIgnoreCase("COUNTER")) {
               this.setDatatypeName("INT IDENTITY");
               this.setOpenBrace((String)null);
               this.setSize((String)null);
               this.setClosedBrace((String)null);
            } else if (SwisSQLOptions.fromAccess && changeSQLServerDatatype.equalsIgnoreCase("GUID")) {
               this.setDatatypeName("UNIQUEIDENTIFIER");
               this.setOpenBrace((String)null);
               this.setSize((String)null);
               this.setClosedBrace((String)null);
            } else if (SwisSQLOptions.fromAccess && changeSQLServerDatatype.equalsIgnoreCase("CURRENCY")) {
               this.setDatatypeName("MONEY");
               this.setOpenBrace((String)null);
               this.setSize((String)null);
               this.setPrecision((String)null);
               this.setClosedBrace((String)null);
               this.setScale((String)null);
            } else if (this.getPrecision() != null && this.getScale() != null && changeSQLServerDatatype.equalsIgnoreCase("FLOAT")) {
               this.setDatatypeName("DECIMAL");
            } else if (changeSQLServerDatatype.equalsIgnoreCase("INTEGER")) {
               this.setDatatypeName("BIGINT");
            } else if (changeSQLServerDatatype.equalsIgnoreCase("MEDIUMINT")) {
               this.setDatatypeName("INT");
            } else if (!changeSQLServerDatatype.equalsIgnoreCase("NUMBER") && !changeSQLServerDatatype.equalsIgnoreCase("NUM")) {
               if (changeSQLServerDatatype.equalsIgnoreCase("INT2")) {
                  this.setDatatypeName("SMALLINT");
               } else if (changeSQLServerDatatype.equalsIgnoreCase("INT4")) {
                  this.setDatatypeName("INT");
               } else if (changeSQLServerDatatype.equalsIgnoreCase("INT8")) {
                  this.setDatatypeName("BIGINT");
               } else if (changeSQLServerDatatype.equalsIgnoreCase("FLOAT4")) {
                  this.setDatatypeName("FLOAT");
                  this.setOpenBrace("(");
                  this.setPrecision("24");
                  this.setClosedBrace(")");
               } else if (changeSQLServerDatatype.equalsIgnoreCase("FLOAT8")) {
                  this.setDatatypeName("FLOAT");
                  this.setOpenBrace("(");
                  this.setPrecision("48");
                  this.setClosedBrace(")");
               } else if (changeSQLServerDatatype.equalsIgnoreCase("DEC")) {
                  this.setDatatypeName("DECIMAL");
               } else if (changeSQLServerDatatype.equalsIgnoreCase("DOUBLE")) {
                  this.setDatatypeName("FLOAT");
                  this.setOpenBrace("(");
                  this.setPrecision("53");
                  this.setScale((String)null);
                  this.setClosedBrace(")");
               } else if (changeSQLServerDatatype.equalsIgnoreCase("FIXED")) {
                  this.setDatatypeName("DECIMAL");
               }
            } else {
               this.setPrecisionScale();
               this.setDatatypeName("NUMERIC");
            }
         } else {
            this.setPrecisionScale();
            this.setDatatypeName("NUMERIC");
         }
      }

   }

   public void toSybaseString() {
      if (this.getDatatypeName() != null) {
         String changeSQLServerDatatype = this.getDatatypeName();
         if (!SwisSQLOptions.fromAccess || !changeSQLServerDatatype.equalsIgnoreCase("INTEGER") && !changeSQLServerDatatype.equalsIgnoreCase("REAL") && !changeSQLServerDatatype.equalsIgnoreCase("SMALLINT")) {
            if (this.getPrecision() == null || !changeSQLServerDatatype.equalsIgnoreCase("INT") && !changeSQLServerDatatype.equalsIgnoreCase("INTEGER") && !changeSQLServerDatatype.equalsIgnoreCase("INT2") && !changeSQLServerDatatype.equalsIgnoreCase("INT4") && !changeSQLServerDatatype.equalsIgnoreCase("MEDIUMINT") && !changeSQLServerDatatype.equalsIgnoreCase("TINYINT") && !changeSQLServerDatatype.equalsIgnoreCase("BIGINT") && !changeSQLServerDatatype.equalsIgnoreCase("SMALLINT")) {
               if (SwisSQLOptions.fromAccess && changeSQLServerDatatype.equalsIgnoreCase("BYTE")) {
                  this.setDatatypeName("TINYINT");
                  this.setOpenBrace((String)null);
                  this.setSize((String)null);
                  this.setClosedBrace((String)null);
               } else if (SwisSQLOptions.fromAccess && changeSQLServerDatatype.equalsIgnoreCase("COUNTER")) {
                  this.setDatatypeName("NUMERIC IDENTITY");
                  this.setOpenBrace((String)null);
                  this.setSize((String)null);
                  this.setClosedBrace((String)null);
               } else if (SwisSQLOptions.fromAccess && changeSQLServerDatatype.equalsIgnoreCase("CURRENCY")) {
                  this.setDatatypeName("MONEY");
                  this.setOpenBrace((String)null);
                  this.setSize((String)null);
                  this.setPrecision((String)null);
                  this.setClosedBrace((String)null);
                  this.setScale((String)null);
               } else if (!changeSQLServerDatatype.equalsIgnoreCase("INTEGER") && !changeSQLServerDatatype.equalsIgnoreCase("MEDIUMINT") && !changeSQLServerDatatype.equalsIgnoreCase("INT4")) {
                  if (changeSQLServerDatatype.equalsIgnoreCase("BIGINT")) {
                     this.setDatatypeName("DECIMAL");
                     this.setOpenBrace("(");
                     this.setClosedBrace(")");
                     this.setPrecision("19");
                     this.setScale("0");
                  } else if (!changeSQLServerDatatype.equalsIgnoreCase("NUMBER") && !changeSQLServerDatatype.equalsIgnoreCase("NUM")) {
                     if (changeSQLServerDatatype.equalsIgnoreCase("INT2")) {
                        this.setDatatypeName("SMALLINT");
                     } else if (changeSQLServerDatatype.equalsIgnoreCase("INT8")) {
                        this.setDatatypeName("BIGINT");
                     } else if (changeSQLServerDatatype.equalsIgnoreCase("FLOAT4")) {
                        this.setDatatypeName("FLOAT");
                        this.setOpenBrace("(");
                        this.setPrecision("24");
                        this.setClosedBrace(")");
                     } else if (changeSQLServerDatatype.equalsIgnoreCase("FLOAT8")) {
                        this.setDatatypeName("FLOAT");
                        this.setOpenBrace("(");
                        this.setPrecision("48");
                        this.setClosedBrace(")");
                     } else if (changeSQLServerDatatype.equalsIgnoreCase("FLOAT")) {
                        this.setScale((String)null);
                     } else if (changeSQLServerDatatype.equalsIgnoreCase("DEC")) {
                        this.setDatatypeName("DECIMAL");
                     } else if (changeSQLServerDatatype.equalsIgnoreCase("DOUBLE")) {
                        this.setDatatypeName("DOUBLE PRECISION");
                        this.setOpenBrace((String)null);
                        this.setPrecision((String)null);
                        this.setScale((String)null);
                        this.setClosedBrace((String)null);
                     } else if (changeSQLServerDatatype.equalsIgnoreCase("FIXED")) {
                        this.setDatatypeName("DECIMAL");
                     }
                  } else {
                     if (this.getPrecision() != null && Integer.parseInt(this.getPrecision()) > 38) {
                        this.setPrecision("38");
                     }

                     this.setDatatypeName("NUMERIC");
                  }
               } else {
                  this.setDatatypeName("INT");
               }
            } else {
               this.setPrecisionScale();
               this.setDatatypeName("NUMERIC");
            }
         } else {
            this.setDatatypeName(changeSQLServerDatatype);
            this.setOpenBrace((String)null);
            this.setSize((String)null);
            this.setClosedBrace((String)null);
         }
      }

   }

   public void toBigQueryString() {
      if (this.getDatatypeName() != null) {
         String changeBigQuerySQLDatatype = this.getDatatypeName();
         if (!changeBigQuerySQLDatatype.equalsIgnoreCase("INTEGER") && !changeBigQuerySQLDatatype.equalsIgnoreCase("BIGINT") && !changeBigQuerySQLDatatype.equalsIgnoreCase("INT")) {
            if (changeBigQuerySQLDatatype.equalsIgnoreCase("MEDIUMINT")) {
               this.setDatatypeName("INT64");
               if (this.getPrecision() != null || this.getSize() != null) {
                  this.removeSizeForPostgreSQLDatatypes();
               }
            } else if (!changeBigQuerySQLDatatype.equalsIgnoreCase("NUMBER") && !changeBigQuerySQLDatatype.equalsIgnoreCase("NUM")) {
               if (!changeBigQuerySQLDatatype.equalsIgnoreCase("TINYINT") && !changeBigQuerySQLDatatype.equalsIgnoreCase("SMALLINT")) {
                  if (changeBigQuerySQLDatatype.equalsIgnoreCase("FLOAT")) {
                     this.setDatatypeName("FLOAT64");
                     if (this.getScale() != null) {
                        this.setScale((String)null);
                     }
                  } else if (!changeBigQuerySQLDatatype.equalsIgnoreCase("SMALLMONEY") && !changeBigQuerySQLDatatype.equalsIgnoreCase("MONEY")) {
                     if (changeBigQuerySQLDatatype.equalsIgnoreCase("DOUBLE")) {
                        this.setDatatypeName("FLOAT64");
                        if (this.getPrecision() != null || this.getSize() != null) {
                           this.removeSizeForPostgreSQLDatatypes();
                        }
                     } else if (changeBigQuerySQLDatatype.equalsIgnoreCase("FIXED")) {
                        this.setDatatypeName("NUMERIC");
                     }
                  } else {
                     this.setDatatypeName("NUMERIC");
                  }
               } else {
                  this.setDatatypeName("INT64");
                  if (this.getPrecision() != null || this.getSize() != null) {
                     this.removeSizeForPostgreSQLDatatypes();
                  }
               }
            } else {
               this.setDatatypeName("NUMERIC");
            }
         } else {
            this.setDatatypeName("INT64");
            if (this.getPrecision() != null || this.getSize() != null) {
               this.removeSizeForPostgreSQLDatatypes();
            }
         }
      }

   }

   public void toPostgreSQLString() {
      if (this.getDatatypeName() != null) {
         String changePostgreSQLDatatype = this.getDatatypeName();
         if (!changePostgreSQLDatatype.equalsIgnoreCase("INTEGER") && !changePostgreSQLDatatype.equalsIgnoreCase("BIGINT") && !changePostgreSQLDatatype.equalsIgnoreCase("INT")) {
            if (changePostgreSQLDatatype.equalsIgnoreCase("MEDIUMINT")) {
               this.setDatatypeName("INT4");
               if (this.getPrecision() != null || this.getSize() != null) {
                  this.removeSizeForPostgreSQLDatatypes();
               }
            } else if (!changePostgreSQLDatatype.equalsIgnoreCase("NUMBER") && !changePostgreSQLDatatype.equalsIgnoreCase("NUM")) {
               if (!changePostgreSQLDatatype.equalsIgnoreCase("TINYINT") && !changePostgreSQLDatatype.equalsIgnoreCase("SMALLINT")) {
                  if (changePostgreSQLDatatype.equalsIgnoreCase("FLOAT")) {
                     this.setDatatypeName("FLOAT");
                     if (this.getScale() != null) {
                        this.setScale((String)null);
                     }
                  } else if (changePostgreSQLDatatype.equalsIgnoreCase("SMALLMONEY")) {
                     this.setDatatypeName("NUMERIC");
                     this.setOpenBrace("(");
                     this.setPrecision("19");
                     this.setScale("4");
                     this.setClosedBrace(")");
                  } else if (changePostgreSQLDatatype.equalsIgnoreCase("MONEY")) {
                     this.setDatatypeName("NUMERIC");
                     this.setOpenBrace("(");
                     this.setPrecision("19");
                     this.setScale("4");
                     this.setClosedBrace(")");
                  } else if (changePostgreSQLDatatype.equalsIgnoreCase("DOUBLE")) {
                     this.setDatatypeName("DOUBLE PRECISION");
                     if (this.getPrecision() != null || this.getSize() != null) {
                        this.removeSizeForPostgreSQLDatatypes();
                     }
                  } else if (changePostgreSQLDatatype.equalsIgnoreCase("FIXED")) {
                     this.setDatatypeName("NUMERIC");
                  }
               } else {
                  this.setDatatypeName("INT2");
                  if (this.getPrecision() != null || this.getSize() != null) {
                     this.removeSizeForPostgreSQLDatatypes();
                  }
               }
            } else {
               this.setDatatypeName("NUMERIC");
            }
         } else {
            this.setDatatypeName("INT8");
            if (this.getPrecision() != null || this.getSize() != null) {
               this.removeSizeForPostgreSQLDatatypes();
            }
         }
      }

   }

   public void removeSizeForPostgreSQLDatatypes() {
      this.setPrecision((String)null);
      this.setScale((String)null);
      this.setSize((String)null);
      this.setOpenBrace((String)null);
      this.setClosedBrace((String)null);
   }

   public void toMySQLString() {
      if (this.getDatatypeName() != null) {
         String changeMySQLDatatype = this.getDatatypeName();
         if (SwisSQLOptions.fromAccess && changeMySQLDatatype.equalsIgnoreCase("BYTE")) {
            this.setDatatypeName("TINYINT UNSIGNED");
            this.setOpenBrace((String)null);
            this.setSize((String)null);
            this.setClosedBrace((String)null);
         } else if (SwisSQLOptions.fromAccess && changeMySQLDatatype.equalsIgnoreCase("REAL")) {
            this.setDatatypeName("FLOAT");
         } else if (SwisSQLOptions.fromAccess && changeMySQLDatatype.equalsIgnoreCase("CURRENCY")) {
            this.setDatatypeName("DECIMAL");
            this.setOpenBrace("(");
            this.setSize("20");
            this.setScale("4");
            this.setClosedBrace(")");
         } else if (SwisSQLOptions.fromAccess && changeMySQLDatatype.equalsIgnoreCase("COUNTER")) {
            this.setDatatypeName("INT AUTO_INCREMENT");
            this.setOpenBrace((String)null);
            this.setSize((String)null);
            this.setClosedBrace((String)null);
         } else if (SwisSQLOptions.fromAccess && changeMySQLDatatype.equalsIgnoreCase("DOUBLE")) {
            this.setOpenBrace((String)null);
            this.setSize((String)null);
            this.setClosedBrace((String)null);
         } else if (changeMySQLDatatype.equalsIgnoreCase("INT2")) {
            this.setDatatypeName("SMALLINT");
         } else if (changeMySQLDatatype.equalsIgnoreCase("INT4")) {
            this.setDatatypeName("INTEGER");
         } else if (changeMySQLDatatype.equalsIgnoreCase("INT8")) {
            this.setDatatypeName("INTEGER");
         } else if (changeMySQLDatatype.equalsIgnoreCase("FLOAT4")) {
            this.setDatatypeName("FLOAT");
            this.setOpenBrace("(");
            this.setPrecision("24");
            this.setClosedBrace(")");
         }

         if (!changeMySQLDatatype.equalsIgnoreCase("NUMBER") && !changeMySQLDatatype.equalsIgnoreCase("NUM")) {
            if (changeMySQLDatatype.equalsIgnoreCase("FLOAT8")) {
               this.setDatatypeName("FLOAT");
               this.setOpenBrace("(");
               this.setPrecision("48");
               this.setClosedBrace(")");
            } else if (changeMySQLDatatype.equalsIgnoreCase("MONEY")) {
               this.setDatatypeName("DECIMAL");
               this.setOpenBrace("(");
               this.setPrecision("19");
               this.setScale("4");
               this.setClosedBrace(")");
            } else if (changeMySQLDatatype.equalsIgnoreCase("SMALLMONEY")) {
               this.setDatatypeName("DECIMAL");
               this.setOpenBrace("(");
               this.setPrecision("10");
               this.setScale("4");
               this.setClosedBrace(")");
            } else if (changeMySQLDatatype.equalsIgnoreCase("FIXED")) {
               this.setDatatypeName("DECIMAL");
            }
         } else {
            this.setDatatypeName("NUMERIC");
         }

         if (this.getArray() != null) {
            this.setArray((String)null);
         }
      }

   }

   public void toSnowflakeString() {
      if (this.getDatatypeName() != null) {
         String changeSnowflakeDatatype = this.getDatatypeName();
         if (SwisSQLOptions.fromAccess && changeSnowflakeDatatype.equalsIgnoreCase("BYTE")) {
            this.setDatatypeName("BYTEINT");
            this.setOpenBrace((String)null);
            this.setSize((String)null);
            this.setClosedBrace((String)null);
         } else if (SwisSQLOptions.fromAccess && changeSnowflakeDatatype.equalsIgnoreCase("CURRENCY")) {
            this.setDatatypeName("DECIMAL");
            this.setOpenBrace("(");
            this.setSize("20");
            this.setScale("4");
            this.setClosedBrace(")");
         } else if (SwisSQLOptions.fromAccess && changeSnowflakeDatatype.equalsIgnoreCase("COUNTER")) {
            this.setDatatypeName("INT");
            this.setOpenBrace((String)null);
            this.setSize((String)null);
            this.setClosedBrace((String)null);
         } else if (SwisSQLOptions.fromAccess && changeSnowflakeDatatype.equalsIgnoreCase("DOUBLE")) {
            this.setOpenBrace((String)null);
            this.setSize((String)null);
            this.setClosedBrace((String)null);
         } else if (changeSnowflakeDatatype.equalsIgnoreCase("INT2")) {
            this.setDatatypeName("SMALLINT");
         } else if (changeSnowflakeDatatype.equalsIgnoreCase("INT4")) {
            this.setDatatypeName("INTEGER");
         } else if (changeSnowflakeDatatype.equalsIgnoreCase("INT8")) {
            this.setDatatypeName("INTEGER");
         }

         if (changeSnowflakeDatatype.equalsIgnoreCase("NUM")) {
            this.setDatatypeName("NUMERIC");
         } else if (changeSnowflakeDatatype.equalsIgnoreCase("MONEY")) {
            this.setDatatypeName("DECIMAL");
            this.setOpenBrace("(");
            this.setPrecision("19");
            this.setScale("4");
            this.setClosedBrace(")");
         } else if (changeSnowflakeDatatype.equalsIgnoreCase("SMALLMONEY")) {
            this.setDatatypeName("DECIMAL");
            this.setOpenBrace("(");
            this.setPrecision("10");
            this.setScale("4");
            this.setClosedBrace(")");
         } else if (changeSnowflakeDatatype.equalsIgnoreCase("FIXED")) {
            this.setDatatypeName("DECIMAL");
         }

         if (this.getArray() != null) {
            this.setArray((String)null);
         }
      }

   }

   public void toSapHanaString() {
      if (this.getDatatypeName() != null) {
         String changeSapHanaDatatype = this.getDatatypeName();
         if (!changeSapHanaDatatype.equalsIgnoreCase("TINYINT") && !changeSapHanaDatatype.equalsIgnoreCase("SMALLINT")) {
            if (!changeSapHanaDatatype.equalsIgnoreCase("INTEGER") && !changeSapHanaDatatype.equalsIgnoreCase("BIGINT")) {
               if (changeSapHanaDatatype.equalsIgnoreCase("DECIMAL") || changeSapHanaDatatype.equalsIgnoreCase("SMALLDECIMAL") || changeSapHanaDatatype.equalsIgnoreCase("DEC")) {
                  this.setDatatypeName("DECIMAL");
                  if (this.getPrecision() == null && this.getScale() == null) {
                     this.setOpenBrace("(");
                     this.setPrecision("38");
                     this.setScale("14");
                     this.setClosedBrace(")");
                  }
               }
            } else {
               this.setDatatypeName("INTEGER");
            }
         } else {
            this.setDatatypeName("SMALLINT");
         }
      }

   }

   public void toSqliteString() {
      if (this.getDatatypeName() != null) {
         String changeSqliteDatatype = this.getDatatypeName();
         if (!changeSqliteDatatype.equalsIgnoreCase("BIGINT") && !changeSqliteDatatype.equalsIgnoreCase("MEDIUMINT") && !changeSqliteDatatype.equalsIgnoreCase("TINYINT") && !changeSqliteDatatype.equalsIgnoreCase("INT2") && !changeSqliteDatatype.equalsIgnoreCase("INT4") && !changeSqliteDatatype.equalsIgnoreCase("INT8") && !changeSqliteDatatype.equalsIgnoreCase("NUMBER") && !changeSqliteDatatype.equalsIgnoreCase("NUM")) {
            if (!changeSqliteDatatype.equalsIgnoreCase("DOUBLE") && !changeSqliteDatatype.equalsIgnoreCase("FLOAT4") && !changeSqliteDatatype.equalsIgnoreCase("FLOAT8") && !changeSqliteDatatype.equalsIgnoreCase("DEC") && !changeSqliteDatatype.equalsIgnoreCase("DOUBLE") && !changeSqliteDatatype.equalsIgnoreCase("MONEY") && !changeSqliteDatatype.equalsIgnoreCase("SMALLMONEY") && !changeSqliteDatatype.equalsIgnoreCase("FIXED")) {
               if (changeSqliteDatatype.equalsIgnoreCase("NUMERIC")) {
                  this.setDatatypeName("NUMERIC");
               }
            } else {
               this.setDatatypeName("REAL");
            }
         } else {
            this.setDatatypeName("INTEGER");
         }

         if (this.getArray() != null) {
            this.setArray((String)null);
         }
      }

   }

   public void toAthenaString() {
      if (this.getDatatypeName() != null) {
         String changeAthenaSQLDatatype = this.getDatatypeName();
         if (!changeAthenaSQLDatatype.equalsIgnoreCase("INTEGER") && !changeAthenaSQLDatatype.equalsIgnoreCase("MEDIUMINT") && !changeAthenaSQLDatatype.equalsIgnoreCase("INT4") && !changeAthenaSQLDatatype.equalsIgnoreCase("INT8")) {
            if (changeAthenaSQLDatatype.equalsIgnoreCase("INT2")) {
               this.setDatatypeName("SMALLINT");
            } else if (!changeAthenaSQLDatatype.equalsIgnoreCase("NUMBER") && !changeAthenaSQLDatatype.equalsIgnoreCase("NUM") && !changeAthenaSQLDatatype.equalsIgnoreCase("NUMERIC")) {
               if (changeAthenaSQLDatatype.equalsIgnoreCase("MONEY")) {
                  this.setDatatypeName("DECIMAL");
                  this.setOpenBrace("(");
                  this.setPrecision("19");
                  this.setScale("4");
                  this.setClosedBrace(")");
               } else if (changeAthenaSQLDatatype.equalsIgnoreCase("SMALLMONEY")) {
                  this.setDatatypeName("DECIMAL");
                  this.setOpenBrace("(");
                  this.setPrecision("10");
                  this.setScale("4");
                  this.setClosedBrace(")");
               } else if (changeAthenaSQLDatatype.equalsIgnoreCase("FIXED")) {
                  this.setDatatypeName("DECIMAL");
               }
            } else {
               this.setDatatypeName("DECIMAL");
               if (this.getPrecision() == null && this.getScale() == null) {
                  this.setOpenBrace("(");
                  this.setPrecision("38");
                  this.setScale("14");
                  this.setClosedBrace(")");
               }
            }
         } else {
            this.setDatatypeName("INT");
         }
      }

   }

   public void toANSIString() {
      if (this.getDatatypeName() != null) {
         String changeANSIDatatype = this.getDatatypeName();
         if (changeANSIDatatype.equalsIgnoreCase("BIGINT")) {
            this.setDatatypeName("INTEGER");
         } else if (changeANSIDatatype.equalsIgnoreCase("MEDIUMINT")) {
            this.setDatatypeName("INTEGER");
         } else if (changeANSIDatatype.equalsIgnoreCase("TINYINT")) {
            this.setDatatypeName("SMALLINT");
         } else if (changeANSIDatatype.equalsIgnoreCase("INT2")) {
            this.setDatatypeName("SMALLINT");
         } else if (changeANSIDatatype.equalsIgnoreCase("DOUBLE")) {
            this.setDatatypeName("DOUBLE PRECISION");
         } else if (changeANSIDatatype.equalsIgnoreCase("INT4")) {
            this.setDatatypeName("INTEGER");
         } else if (changeANSIDatatype.equalsIgnoreCase("INT8")) {
            this.setDatatypeName("INTEGER");
         } else if (changeANSIDatatype.equalsIgnoreCase("FLOAT4")) {
            this.setDatatypeName("FLOAT");
            this.setOpenBrace("(");
            this.setPrecision("24");
            this.setClosedBrace(")");
         } else if (changeANSIDatatype.equalsIgnoreCase("FLOAT8")) {
            this.setDatatypeName("FLOAT");
            this.setOpenBrace("(");
            this.setPrecision("48");
            this.setClosedBrace(")");
         } else if (!changeANSIDatatype.equalsIgnoreCase("NUMBER") && !changeANSIDatatype.equalsIgnoreCase("NUM")) {
            if (changeANSIDatatype.equalsIgnoreCase("DEC")) {
               this.setDatatypeName("DECIMAL");
            } else if (changeANSIDatatype.equalsIgnoreCase("DOUBLE")) {
               this.setDatatypeName("DOUBLE PRECISION");
            } else if (changeANSIDatatype.equalsIgnoreCase("MONEY")) {
               this.setDatatypeName("DECIMAL");
               this.setOpenBrace("(");
               this.setPrecision("19");
               this.setScale("4");
               this.setClosedBrace(")");
            } else if (changeANSIDatatype.equalsIgnoreCase("SMALLMONEY")) {
               this.setDatatypeName("DECIMAL");
               this.setOpenBrace("(");
               this.setPrecision("10");
               this.setScale("4");
               this.setClosedBrace(")");
            } else if (changeANSIDatatype.equalsIgnoreCase("FIXED")) {
               this.setDatatypeName("DECIMAL");
            }
         } else {
            this.setDatatypeName("NUMERIC");
         }

         if (this.getArray() != null) {
            this.setArray((String)null);
         }
      }

   }

   public void toTimesTenString() {
      if (this.getDatatypeName() != null) {
         String sourceType = this.getDatatypeName();
         if (this.getPrecision() != null && (sourceType.equalsIgnoreCase("INT") || sourceType.equalsIgnoreCase("INTEGER") || sourceType.equalsIgnoreCase("INT2") || sourceType.equalsIgnoreCase("INT4") || sourceType.equalsIgnoreCase("MEDIUMINT") || sourceType.equalsIgnoreCase("TINYINT") || sourceType.equalsIgnoreCase("BIGINT") || sourceType.equalsIgnoreCase("SMALLINT"))) {
            this.setDatatypeName("NUMERIC");
         } else if (!sourceType.equalsIgnoreCase("MEDIUMINT") && !sourceType.equalsIgnoreCase("INT4")) {
            if (!sourceType.equalsIgnoreCase("NUMBER") && !sourceType.equalsIgnoreCase("NUM")) {
               if (sourceType.equalsIgnoreCase("INT2")) {
                  this.setDatatypeName("SMALLINT");
               } else if (sourceType.equalsIgnoreCase("INT8")) {
                  this.setDatatypeName("BIGINT");
               } else if (sourceType.equalsIgnoreCase("FLOAT")) {
                  String precision_str = this.getPrecision();
                  boolean var3 = true;

                  try {
                     int precision_int = Integer.parseInt(precision_str);
                     if (precision_int < 24) {
                        this.setPrecision("24");
                     } else {
                        this.setPrecision("53");
                     }
                  } catch (Exception var5) {
                  }
               } else if (sourceType.equalsIgnoreCase("FLOAT4")) {
                  this.setDatatypeName("FLOAT");
                  this.setOpenBrace("(");
                  this.setPrecision("24");
                  this.setClosedBrace(")");
               } else if (sourceType.equalsIgnoreCase("FLOAT8")) {
                  this.setDatatypeName("FLOAT");
                  this.setOpenBrace("(");
                  this.setPrecision("53");
                  this.setClosedBrace(")");
               } else if (sourceType.equalsIgnoreCase("FIXED")) {
                  this.setDatatypeName("DECIMAL");
               } else if (sourceType.equalsIgnoreCase("MONEY")) {
                  this.setDatatypeName("DECIMAL");
                  this.setOpenBrace("(");
                  this.setPrecision("19");
                  this.setScale("4");
                  this.setClosedBrace(")");
               } else if (sourceType.equalsIgnoreCase("SMALLMONEY")) {
                  this.setDatatypeName("DECIMAL");
                  this.setOpenBrace("(");
                  this.setPrecision("10");
                  this.setScale("4");
                  this.setClosedBrace(")");
               }
            } else {
               if (this.getPrecision() != null && Integer.parseInt(this.getPrecision()) > 40) {
                  this.setPrecision("40");
               }

               this.setDatatypeName("NUMERIC");
            }
         } else {
            this.setDatatypeName("INT");
         }

         if (this.getArray() != null) {
            this.setArray((String)null);
         }
      }

   }

   public void toNetezzaString() {
      if (this.getDatatypeName() != null) {
         String changeNetezzaDatatype = this.getDatatypeName();
         if (changeNetezzaDatatype.equalsIgnoreCase("BIGINT")) {
            this.setDatatypeName("BIGINT");
         } else if (changeNetezzaDatatype.equalsIgnoreCase("MEDIUMINT")) {
            this.setDatatypeName("INTEGER");
         } else if (changeNetezzaDatatype.equalsIgnoreCase("TINYINT")) {
            this.setDatatypeName("SMALLINT");
         } else if (changeNetezzaDatatype.equalsIgnoreCase("INT2")) {
            this.setDatatypeName("SMALLINT");
         } else if (changeNetezzaDatatype.equalsIgnoreCase("DOUBLE")) {
            this.setDatatypeName("DOUBLE PRECISION");
         } else if (!changeNetezzaDatatype.equalsIgnoreCase("INT4") && !changeNetezzaDatatype.equalsIgnoreCase("INT") && !changeNetezzaDatatype.equalsIgnoreCase("INTEGER")) {
            if (changeNetezzaDatatype.equalsIgnoreCase("INT8")) {
               this.setDatatypeName("INTEGER");
            } else if (changeNetezzaDatatype.equalsIgnoreCase("FLOAT4")) {
               this.setDatatypeName("FLOAT");
               this.setOpenBrace("(");
               this.setPrecision("24");
               this.setClosedBrace(")");
            } else if (changeNetezzaDatatype.equalsIgnoreCase("FLOAT8")) {
               this.setDatatypeName("FLOAT");
               this.setOpenBrace("(");
               this.setPrecision("48");
               this.setClosedBrace(")");
            } else if (!changeNetezzaDatatype.equalsIgnoreCase("NUMBER") && !changeNetezzaDatatype.equalsIgnoreCase("NUM") && !changeNetezzaDatatype.equalsIgnoreCase("NUMERIC")) {
               if (!changeNetezzaDatatype.equalsIgnoreCase("DEC") && !changeNetezzaDatatype.equalsIgnoreCase("DECIMAL")) {
                  if (changeNetezzaDatatype.equalsIgnoreCase("DOUBLE")) {
                     this.setDatatypeName("DOUBLE PRECISION");
                  } else if (changeNetezzaDatatype.equalsIgnoreCase("MONEY")) {
                     this.setDatatypeName("NUMERIC");
                     this.setOpenBrace("(");
                     this.setPrecision("19");
                     this.setScale("4");
                     this.setClosedBrace(")");
                  } else if (changeNetezzaDatatype.equalsIgnoreCase("SMALLMONEY")) {
                     this.setDatatypeName("NUMERIC");
                     this.setOpenBrace("(");
                     this.setPrecision("10");
                     this.setScale("4");
                     this.setClosedBrace(")");
                  } else if (changeNetezzaDatatype.equalsIgnoreCase("FIXED")) {
                     this.setDatatypeName("NUMERIC");
                  }
               } else {
                  this.setDatatypeName("NUMERIC");
                  this.handleNumericTypeForNetezza();
               }
            } else {
               this.setDatatypeName("NUMERIC");
               this.handleNumericTypeForNetezza();
            }
         } else {
            this.setDatatypeName("INTEGER");
         }

         if (this.getArray() != null) {
            this.setArray((String)null);
         }
      }

   }

   public void toTeradataString() {
      if (this.getDatatypeName() != null) {
         String changeTeradataDatatype = this.getDatatypeName();
         if (changeTeradataDatatype.equalsIgnoreCase("BIGINT")) {
            this.setDatatypeName("INTEGER");
         } else if (changeTeradataDatatype.equalsIgnoreCase("MEDIUMINT")) {
            this.setDatatypeName("INTEGER");
         } else if (changeTeradataDatatype.equalsIgnoreCase("TINYINT")) {
            this.setDatatypeName("SMALLINT");
         } else if (changeTeradataDatatype.equalsIgnoreCase("INT2")) {
            this.setDatatypeName("SMALLINT");
         } else if (changeTeradataDatatype.equalsIgnoreCase("DOUBLE")) {
            this.setDatatypeName("DOUBLE PRECISION");
         } else if (changeTeradataDatatype.equalsIgnoreCase("INT4")) {
            this.setDatatypeName("INTEGER");
         } else if (changeTeradataDatatype.equalsIgnoreCase("INT8")) {
            this.setDatatypeName("INTEGER");
         } else if (changeTeradataDatatype.equalsIgnoreCase("FLOAT4")) {
            this.setDatatypeName("FLOAT");
            this.setOpenBrace("(");
            this.setPrecision("24");
            this.setClosedBrace(")");
         } else if (changeTeradataDatatype.equalsIgnoreCase("FLOAT8")) {
            this.setDatatypeName("FLOAT");
            this.setOpenBrace("(");
            this.setPrecision("48");
            this.setClosedBrace(")");
         } else if (!changeTeradataDatatype.equalsIgnoreCase("NUMBER") && !changeTeradataDatatype.equalsIgnoreCase("NUM") && !changeTeradataDatatype.equalsIgnoreCase("NUMERIC")) {
            if (changeTeradataDatatype.equalsIgnoreCase("DEC")) {
               this.setDatatypeName("DECIMAL");
               if (SwisSQLAPI.convertToTeradata && this.getPrecision() == null && this.getScale() == null) {
                  this.setOpenBrace("(");
                  this.setPrecision("38");
                  this.setScale("16");
                  this.setClosedBrace(")");
               }
            } else if (changeTeradataDatatype.equalsIgnoreCase("DOUBLE")) {
               this.setDatatypeName("DOUBLE PRECISION");
            } else if (changeTeradataDatatype.equalsIgnoreCase("MONEY")) {
               this.setDatatypeName("DECIMAL");
               this.setOpenBrace("(");
               this.setPrecision("19");
               this.setScale("4");
               this.setClosedBrace(")");
            } else if (changeTeradataDatatype.equalsIgnoreCase("SMALLMONEY")) {
               this.setDatatypeName("DECIMAL");
               this.setOpenBrace("(");
               this.setPrecision("10");
               this.setScale("4");
               this.setClosedBrace(")");
            } else if (changeTeradataDatatype.equalsIgnoreCase("FIXED")) {
               this.setDatatypeName("DECIMAL");
            }
         } else {
            this.setDatatypeName("DECIMAL");
            if (this.getPrecision() == null && this.getScale() == null) {
               this.setOpenBrace("(");
               this.setPrecision("38");
               this.setScale("14");
               this.setClosedBrace(")");
            }
         }

         if (this.getArray() != null) {
            this.setArray((String)null);
         }
      }

   }

   public Datatype copyObjectValues() {
      NumericClass newNumericClass = new NumericClass();
      newNumericClass.setClosedBrace(this.closedBrace);
      newNumericClass.setDatatypeName(this.getDatatypeName());
      newNumericClass.setOpenBrace(this.openBrace);
      newNumericClass.setPrecision(this.getPrecision());
      newNumericClass.setScale(this.getScale());
      return newNumericClass;
   }

   public void setPrecisionScale() {
      if (this.getPrecision() != null) {
         try {
            int prec = Integer.parseInt(this.precision);
            int scl;
            if (prec >= 0 && prec <= 9 && this.scale != null) {
               try {
                  scl = Integer.parseInt(this.scale);
                  if (scl > 4) {
                     this.scale = "4";
                  }
               } catch (NumberFormatException var7) {
               }
            }

            if (prec >= 10 && prec <= 19 && this.scale != null) {
               try {
                  scl = Integer.parseInt(this.scale);
                  if (scl > 8) {
                     this.scale = "8";
                  }
               } catch (NumberFormatException var6) {
               }
            }

            if (prec >= 20 && prec <= 28 && this.scale != null) {
               try {
                  scl = Integer.parseInt(this.scale);
                  if (scl > 12) {
                     this.scale = "12";
                  }
               } catch (NumberFormatException var5) {
               }
            }

            if (prec >= 29 && prec <= 38 && this.scale != null) {
               try {
                  scl = Integer.parseInt(this.scale);
                  if (scl > 16) {
                     this.scale = "16";
                  }
               } catch (NumberFormatException var4) {
               }
            }

            if (prec >= 38) {
               this.setPrecision("38");
               if (this.scale != null) {
                  try {
                     scl = Integer.parseInt(this.scale);
                     if (scl > 16) {
                        this.scale = "16";
                     }
                  } catch (NumberFormatException var3) {
                  }
               }
            }
         } catch (NumberFormatException var8) {
         }
      }

   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      if (this.datatypeName != null) {
         sb.append(this.datatypeName + " ");
      }

      if (this.openBrace != null) {
         sb.append(this.openBrace);
      }

      if (this.precision != null) {
         sb.append(this.precision);
      }

      if (this.scale != null) {
         sb.append(", " + this.scale);
      }

      if (this.closedBrace != null) {
         sb.append(this.closedBrace);
      }

      if (this.arrayStr != null) {
         sb.append(this.arrayStr);
      }

      return sb.toString();
   }

   private void handleNumericTypeForNetezza() {
      try {
         int precVal;
         if (this.getPrecision() != null && this.getScale() != null) {
            precVal = Integer.parseInt(this.getPrecision());
            int scaleVal = Integer.parseInt(this.getScale());
            if (scaleVal == 0) {
               if (precVal <= 9) {
                  this.setDatatypeName("INTEGER");
                  this.setPrecision((String)null);
                  this.setOpenBrace((String)null);
                  this.setScale((String)null);
                  this.setClosedBrace((String)null);
               } else if (precVal > 9 && precVal <= 18) {
                  this.setDatatypeName("BIGINT");
                  this.setPrecision((String)null);
                  this.setOpenBrace((String)null);
                  this.setScale((String)null);
                  this.setClosedBrace((String)null);
               } else if (precVal > 38) {
                  this.setPrecision("38");
               }
            } else if (precVal > 38) {
               this.setPrecision("38");
            }
         } else if (this.getPrecision() != null && this.getScale() == null) {
            precVal = Integer.parseInt(this.getPrecision());
            if (precVal <= 9) {
               this.setDatatypeName("INTEGER");
               this.setPrecision((String)null);
               this.setOpenBrace((String)null);
               this.setScale((String)null);
               this.setClosedBrace((String)null);
            } else if (precVal > 9 && precVal <= 18) {
               this.setDatatypeName("BIGINT");
               this.setPrecision((String)null);
               this.setOpenBrace((String)null);
               this.setScale((String)null);
               this.setClosedBrace((String)null);
            } else if (precVal > 38) {
               this.setPrecision("38");
            }
         }
      } catch (NumberFormatException var3) {
         var3.printStackTrace();
      }

   }

   public void setArray(String arrayStr) {
   }

   public String getArray() {
      return this.arrayStr;
   }
}
