package com.adventnet.swissqlapi.sql.functions;

import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.functions.aggregate.aggregateIf;
import com.adventnet.swissqlapi.sql.functions.aggregate.avg;
import com.adventnet.swissqlapi.sql.functions.aggregate.count;
import com.adventnet.swissqlapi.sql.functions.aggregate.max;
import com.adventnet.swissqlapi.sql.functions.aggregate.mean;
import com.adventnet.swissqlapi.sql.functions.aggregate.median;
import com.adventnet.swissqlapi.sql.functions.aggregate.min;
import com.adventnet.swissqlapi.sql.functions.aggregate.mode;
import com.adventnet.swissqlapi.sql.functions.aggregate.mtd;
import com.adventnet.swissqlapi.sql.functions.aggregate.percentile;
import com.adventnet.swissqlapi.sql.functions.aggregate.qtd;
import com.adventnet.swissqlapi.sql.functions.aggregate.regression;
import com.adventnet.swissqlapi.sql.functions.aggregate.stddeviation;
import com.adventnet.swissqlapi.sql.functions.aggregate.sum;
import com.adventnet.swissqlapi.sql.functions.aggregate.variance;
import com.adventnet.swissqlapi.sql.functions.aggregate.ytd;
import com.adventnet.swissqlapi.sql.functions.analytic.Cume_Dist;
import com.adventnet.swissqlapi.sql.functions.analytic.DenseRank;
import com.adventnet.swissqlapi.sql.functions.analytic.FirstValue;
import com.adventnet.swissqlapi.sql.functions.analytic.Lag;
import com.adventnet.swissqlapi.sql.functions.analytic.LastValue;
import com.adventnet.swissqlapi.sql.functions.analytic.Lead;
import com.adventnet.swissqlapi.sql.functions.analytic.Nth_Value;
import com.adventnet.swissqlapi.sql.functions.analytic.Ntile;
import com.adventnet.swissqlapi.sql.functions.analytic.Rank;
import com.adventnet.swissqlapi.sql.functions.analytic.Ratio_To_Report;
import com.adventnet.swissqlapi.sql.functions.analytic.RowNumber;
import com.adventnet.swissqlapi.sql.functions.analytic.Tabular;
import com.adventnet.swissqlapi.sql.functions.date.AbsoluteMonth;
import com.adventnet.swissqlapi.sql.functions.date.AbsoluteQuarter;
import com.adventnet.swissqlapi.sql.functions.date.AbsoluteWeek;
import com.adventnet.swissqlapi.sql.functions.date.ConvertStringToDuration;
import com.adventnet.swissqlapi.sql.functions.date.FirstDateCurrentWeek;
import com.adventnet.swissqlapi.sql.functions.date.FromTZ;
import com.adventnet.swissqlapi.sql.functions.date.MakeDate;
import com.adventnet.swissqlapi.sql.functions.date.NumToDSInterval;
import com.adventnet.swissqlapi.sql.functions.date.NumToYMInterval;
import com.adventnet.swissqlapi.sql.functions.date.RoundDuration;
import com.adventnet.swissqlapi.sql.functions.date.SecToTime;
import com.adventnet.swissqlapi.sql.functions.date.StrToDate;
import com.adventnet.swissqlapi.sql.functions.date.SysDateTime;
import com.adventnet.swissqlapi.sql.functions.date.SysDateTimeOffset;
import com.adventnet.swissqlapi.sql.functions.date.TimeToSec;
import com.adventnet.swissqlapi.sql.functions.date.ToDSInterval;
import com.adventnet.swissqlapi.sql.functions.date.ToTimestampTZ;
import com.adventnet.swissqlapi.sql.functions.date.ToYMInterval;
import com.adventnet.swissqlapi.sql.functions.date.WeekOfMonth;
import com.adventnet.swissqlapi.sql.functions.date.addToDate;
import com.adventnet.swissqlapi.sql.functions.date.adddaystoduration;
import com.adventnet.swissqlapi.sql.functions.date.addduration;
import com.adventnet.swissqlapi.sql.functions.date.addhourstoduration;
import com.adventnet.swissqlapi.sql.functions.date.addminutesstoduration;
import com.adventnet.swissqlapi.sql.functions.date.addmonths;
import com.adventnet.swissqlapi.sql.functions.date.addmonthstoduration;
import com.adventnet.swissqlapi.sql.functions.date.addsecondstoduration;
import com.adventnet.swissqlapi.sql.functions.date.addtimetotime;
import com.adventnet.swissqlapi.sql.functions.date.addweekstoduration;
import com.adventnet.swissqlapi.sql.functions.date.addyearstoduration;
import com.adventnet.swissqlapi.sql.functions.date.businessDate;
import com.adventnet.swissqlapi.sql.functions.date.convertTz;
import com.adventnet.swissqlapi.sql.functions.date.convert_dayshms_to_dur;
import com.adventnet.swissqlapi.sql.functions.date.convert_fmtdayshms_to_dur;
import com.adventnet.swissqlapi.sql.functions.date.convert_hms_to_dur;
import com.adventnet.swissqlapi.sql.functions.date.currentDate;
import com.adventnet.swissqlapi.sql.functions.date.currentTimestamp;
import com.adventnet.swissqlapi.sql.functions.date.date;
import com.adventnet.swissqlapi.sql.functions.date.dateadd;
import com.adventnet.swissqlapi.sql.functions.date.datediff;
import com.adventnet.swissqlapi.sql.functions.date.dateformat;
import com.adventnet.swissqlapi.sql.functions.date.datepart;
import com.adventnet.swissqlapi.sql.functions.date.day;
import com.adventnet.swissqlapi.sql.functions.date.dayofquarter;
import com.adventnet.swissqlapi.sql.functions.date.daysofduration;
import com.adventnet.swissqlapi.sql.functions.date.end_day;
import com.adventnet.swissqlapi.sql.functions.date.endofhour;
import com.adventnet.swissqlapi.sql.functions.date.extract;
import com.adventnet.swissqlapi.sql.functions.date.getdate;
import com.adventnet.swissqlapi.sql.functions.date.gettime;
import com.adventnet.swissqlapi.sql.functions.date.hoursofduration;
import com.adventnet.swissqlapi.sql.functions.date.iscurrenthour;
import com.adventnet.swissqlapi.sql.functions.date.islastnhour;
import com.adventnet.swissqlapi.sql.functions.date.isnextnhour;
import com.adventnet.swissqlapi.sql.functions.date.ispreviousnhour;
import com.adventnet.swissqlapi.sql.functions.date.lastDate;
import com.adventnet.swissqlapi.sql.functions.date.last_day;
import com.adventnet.swissqlapi.sql.functions.date.localtimestamp;
import com.adventnet.swissqlapi.sql.functions.date.makeduration;
import com.adventnet.swissqlapi.sql.functions.date.maketime;
import com.adventnet.swissqlapi.sql.functions.date.minutesofduration;
import com.adventnet.swissqlapi.sql.functions.date.month;
import com.adventnet.swissqlapi.sql.functions.date.monthdiff;
import com.adventnet.swissqlapi.sql.functions.date.months_between;
import com.adventnet.swissqlapi.sql.functions.date.monthsofduration;
import com.adventnet.swissqlapi.sql.functions.date.nextDate;
import com.adventnet.swissqlapi.sql.functions.date.next_day;
import com.adventnet.swissqlapi.sql.functions.date.previousDate;
import com.adventnet.swissqlapi.sql.functions.date.quarter;
import com.adventnet.swissqlapi.sql.functions.date.secondsofduration;
import com.adventnet.swissqlapi.sql.functions.date.start_datetime;
import com.adventnet.swissqlapi.sql.functions.date.start_day;
import com.adventnet.swissqlapi.sql.functions.date.startofhour;
import com.adventnet.swissqlapi.sql.functions.date.subdaysfromduration;
import com.adventnet.swissqlapi.sql.functions.date.subduration;
import com.adventnet.swissqlapi.sql.functions.date.subhoursfromduration;
import com.adventnet.swissqlapi.sql.functions.date.subminutesfromduration;
import com.adventnet.swissqlapi.sql.functions.date.submonthsfromduration;
import com.adventnet.swissqlapi.sql.functions.date.subsecondsfromduration;
import com.adventnet.swissqlapi.sql.functions.date.subweeksfromduration;
import com.adventnet.swissqlapi.sql.functions.date.subyearsfromduration;
import com.adventnet.swissqlapi.sql.functions.date.time;
import com.adventnet.swissqlapi.sql.functions.date.timeAddSub;
import com.adventnet.swissqlapi.sql.functions.date.timediffinduration;
import com.adventnet.swissqlapi.sql.functions.date.timestampAdd;
import com.adventnet.swissqlapi.sql.functions.date.timestampDiff;
import com.adventnet.swissqlapi.sql.functions.date.timestampdiffinduration;
import com.adventnet.swissqlapi.sql.functions.date.timetomin;
import com.adventnet.swissqlapi.sql.functions.date.tochar;
import com.adventnet.swissqlapi.sql.functions.date.todate;
import com.adventnet.swissqlapi.sql.functions.date.truncmicrosec;
import com.adventnet.swissqlapi.sql.functions.date.unixTimestamp;
import com.adventnet.swissqlapi.sql.functions.date.utcDate;
import com.adventnet.swissqlapi.sql.functions.date.utcTimestamp;
import com.adventnet.swissqlapi.sql.functions.date.week;
import com.adventnet.swissqlapi.sql.functions.date.weeksofduration;
import com.adventnet.swissqlapi.sql.functions.date.year;
import com.adventnet.swissqlapi.sql.functions.date.yearsofduration;
import com.adventnet.swissqlapi.sql.functions.json.extract_json;
import com.adventnet.swissqlapi.sql.functions.math.abs;
import com.adventnet.swissqlapi.sql.functions.math.atan2;
import com.adventnet.swissqlapi.sql.functions.math.bigint;
import com.adventnet.swissqlapi.sql.functions.math.ceil;
import com.adventnet.swissqlapi.sql.functions.math.cot;
import com.adventnet.swissqlapi.sql.functions.math.degrees;
import com.adventnet.swissqlapi.sql.functions.math.exp;
import com.adventnet.swissqlapi.sql.functions.math.floor;
import com.adventnet.swissqlapi.sql.functions.math.ln;
import com.adventnet.swissqlapi.sql.functions.math.log;
import com.adventnet.swissqlapi.sql.functions.math.mod;
import com.adventnet.swissqlapi.sql.functions.math.pi;
import com.adventnet.swissqlapi.sql.functions.math.power;
import com.adventnet.swissqlapi.sql.functions.math.radians;
import com.adventnet.swissqlapi.sql.functions.math.rand;
import com.adventnet.swissqlapi.sql.functions.math.round;
import com.adventnet.swissqlapi.sql.functions.math.smallint;
import com.adventnet.swissqlapi.sql.functions.math.sqrt;
import com.adventnet.swissqlapi.sql.functions.math.square;
import com.adventnet.swissqlapi.sql.functions.math.tonumber;
import com.adventnet.swissqlapi.sql.functions.math.trig;
import com.adventnet.swissqlapi.sql.functions.math.trigh;
import com.adventnet.swissqlapi.sql.functions.math.trunc;
import com.adventnet.swissqlapi.sql.functions.misc.Binary;
import com.adventnet.swissqlapi.sql.functions.misc.Hashbytes;
import com.adventnet.swissqlapi.sql.functions.misc.ascii;
import com.adventnet.swissqlapi.sql.functions.misc.bitand;
import com.adventnet.swissqlapi.sql.functions.misc.bitor;
import com.adventnet.swissqlapi.sql.functions.misc.cast;
import com.adventnet.swissqlapi.sql.functions.misc.contains;
import com.adventnet.swissqlapi.sql.functions.misc.conv;
import com.adventnet.swissqlapi.sql.functions.misc.convert;
import com.adventnet.swissqlapi.sql.functions.misc.decode;
import com.adventnet.swissqlapi.sql.functions.misc.encrypt;
import com.adventnet.swissqlapi.sql.functions.misc.format;
import com.adventnet.swissqlapi.sql.functions.misc.greatest;
import com.adventnet.swissqlapi.sql.functions.misc.grouping_id;
import com.adventnet.swissqlapi.sql.functions.misc.hex;
import com.adventnet.swissqlapi.sql.functions.misc.hextoraw;
import com.adventnet.swissqlapi.sql.functions.misc.iffunction;
import com.adventnet.swissqlapi.sql.functions.misc.ifmatches;
import com.adventnet.swissqlapi.sql.functions.misc.ifnull;
import com.adventnet.swissqlapi.sql.functions.misc.is_srvrolemember;
import com.adventnet.swissqlapi.sql.functions.misc.isnull;
import com.adventnet.swissqlapi.sql.functions.misc.least;
import com.adventnet.swissqlapi.sql.functions.misc.nullif;
import com.adventnet.swissqlapi.sql.functions.misc.nvl;
import com.adventnet.swissqlapi.sql.functions.misc.nvl2;
import com.adventnet.swissqlapi.sql.functions.misc.object_id;
import com.adventnet.swissqlapi.sql.functions.misc.serverproperty;
import com.adventnet.swissqlapi.sql.functions.misc.suser_sid;
import com.adventnet.swissqlapi.sql.functions.misc.suser_sname;
import com.adventnet.swissqlapi.sql.functions.misc.sys_context;
import com.adventnet.swissqlapi.sql.functions.misc.sysguid;
import com.adventnet.swissqlapi.sql.functions.misc.todecisionbox;
import com.adventnet.swissqlapi.sql.functions.misc.translate;
import com.adventnet.swissqlapi.sql.functions.misc.user;
import com.adventnet.swissqlapi.sql.functions.misc.userenv;
import com.adventnet.swissqlapi.sql.functions.reports.dateTrunc;
import com.adventnet.swissqlapi.sql.functions.reports.fiscalQuarter;
import com.adventnet.swissqlapi.sql.functions.reports.fiscalQuarterYear;
import com.adventnet.swissqlapi.sql.functions.reports.fiscalWeek;
import com.adventnet.swissqlapi.sql.functions.reports.fiscalYear;
import com.adventnet.swissqlapi.sql.functions.reports.startWeek;
import com.adventnet.swissqlapi.sql.functions.string.DecInt;
import com.adventnet.swissqlapi.sql.functions.string.chr;
import com.adventnet.swissqlapi.sql.functions.string.concat;
import com.adventnet.swissqlapi.sql.functions.string.concat_ignore_null;
import com.adventnet.swissqlapi.sql.functions.string.datename;
import com.adventnet.swissqlapi.sql.functions.string.groupConcat;
import com.adventnet.swissqlapi.sql.functions.string.groupFirstLast;
import com.adventnet.swissqlapi.sql.functions.string.indexof;
import com.adventnet.swissqlapi.sql.functions.string.initcap;
import com.adventnet.swissqlapi.sql.functions.string.insert;
import com.adventnet.swissqlapi.sql.functions.string.instr;
import com.adventnet.swissqlapi.sql.functions.string.left;
import com.adventnet.swissqlapi.sql.functions.string.length;
import com.adventnet.swissqlapi.sql.functions.string.lower;
import com.adventnet.swissqlapi.sql.functions.string.lpad;
import com.adventnet.swissqlapi.sql.functions.string.ltrim;
import com.adventnet.swissqlapi.sql.functions.string.rawtohex;
import com.adventnet.swissqlapi.sql.functions.string.repeat;
import com.adventnet.swissqlapi.sql.functions.string.replace;
import com.adventnet.swissqlapi.sql.functions.string.reverse;
import com.adventnet.swissqlapi.sql.functions.string.right;
import com.adventnet.swissqlapi.sql.functions.string.rpad;
import com.adventnet.swissqlapi.sql.functions.string.rtrim;
import com.adventnet.swissqlapi.sql.functions.string.set;
import com.adventnet.swissqlapi.sql.functions.string.soundex;
import com.adventnet.swissqlapi.sql.functions.string.space;
import com.adventnet.swissqlapi.sql.functions.string.str;
import com.adventnet.swissqlapi.sql.functions.string.strcmp;
import com.adventnet.swissqlapi.sql.functions.string.strsearch;
import com.adventnet.swissqlapi.sql.functions.string.stuff;
import com.adventnet.swissqlapi.sql.functions.string.substring;
import com.adventnet.swissqlapi.sql.functions.string.substringIndex;
import com.adventnet.swissqlapi.sql.functions.string.substring_between;
import com.adventnet.swissqlapi.sql.functions.string.substring_count;
import com.adventnet.swissqlapi.sql.functions.string.toemail;
import com.adventnet.swissqlapi.sql.functions.string.trim;
import com.adventnet.swissqlapi.sql.functions.string.upper;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.ModifiedObjectAttr;
import com.adventnet.swissqlapi.sql.statement.OverrideToString;
import com.adventnet.swissqlapi.sql.statement.create.CharacterClass;
import com.adventnet.swissqlapi.sql.statement.create.Datatype;
import com.adventnet.swissqlapi.sql.statement.create.NumericClass;
import com.adventnet.swissqlapi.sql.statement.select.CaseStatement;
import com.adventnet.swissqlapi.sql.statement.select.FromTable;
import com.adventnet.swissqlapi.sql.statement.select.GroupByStatement;
import com.adventnet.swissqlapi.sql.statement.select.HavingStatement;
import com.adventnet.swissqlapi.sql.statement.select.OrderByStatement;
import com.adventnet.swissqlapi.sql.statement.select.OrderItem;
import com.adventnet.swissqlapi.sql.statement.select.QueryPartitionClause;
import com.adventnet.swissqlapi.sql.statement.select.SelectColumn;
import com.adventnet.swissqlapi.sql.statement.select.SelectQueryStatement;
import com.adventnet.swissqlapi.sql.statement.select.SelectStatement;
import com.adventnet.swissqlapi.sql.statement.select.TableColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereColumn;
import com.adventnet.swissqlapi.sql.statement.select.WhereExpression;
import com.adventnet.swissqlapi.sql.statement.select.WhereItem;
import com.adventnet.swissqlapi.sql.statement.select.WindowingClause;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.util.misc.CastingUtil;
import com.adventnet.swissqlapi.util.misc.CustomizeUtil;
import com.adventnet.swissqlapi.util.misc.GeneralUtil;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FunctionCalls {
   private static String trimmedFnName;
   protected TableColumn functionName = new TableColumn();
   protected String argumentQualifier;
   protected String trailingString;
   protected String fromStringInFunction;
   protected String forStringInFunction;
   protected String lengthString;
   protected String asDatatype;
   protected String using;
   protected String inString;
   protected String separatorString;
   protected String over;
   protected OrderByStatement obs;
   protected Vector functionArguments = new Vector();
   public static boolean charToIntName = false;
   protected String CaseString;
   protected boolean decodeConvertedToCaseStatement = false;
   protected UserObjectContext context = null;
   protected String collateClause = null;
   protected String collationName;
   protected String toDateString;
   protected String toDateSymbolValue;
   protected String divisionBy31;
   private boolean openBracesForFunctionNameRequired = true;
   private String wrapper = null;
   private OverrideToString override_to_string;
   private String partitionBy;
   private QueryPartitionClause partitionByClause;
   private boolean inArithmeticExpr;
   private String targetDataType;
   private CommentClass commentObj;
   private String adventNetMessage = null;
   private WindowingClause windowClause;
   private String dateArithmetic;
   protected String keep = null;
   protected String denseRank = null;
   protected String last = null;
   protected String first = null;
   public static boolean functionArgsInSingleQuotesToDouble = true;
   protected SelectColumn atTimeZoneRegion = null;
   private String withinGroup;
   protected CaseStatement caseStatement;
   private String usingClause = null;
   private boolean stripComma = false;
   protected boolean castToTextInsideIf = true;
   protected boolean isCastAsTextAllowedForTimeDiff = true;
   private int fcNestedIfCount = 0;
   private String customDataType = null;
   private String instanceDatatype = "UNDEFINED";
   private boolean outerJoin = false;
   private static final int[] WEEKDAY_MAP = new int[8];
   private static final int[] WEEKSTARTDAY_WEEKDAY_MAP = new int[8];
   private static final Set<String> simpleDateFns = new HashSet();
   public static final Pattern DURATION_FORMAT_PATTERN3_COMPILED;

   public boolean castToTextInsideIf() {
      return this.castToTextInsideIf;
   }

   public void setCastToTextInsideIf(boolean yes) {
      this.castToTextInsideIf = yes;
   }

   public boolean castTimeDiffToText() {
      return this.isCastAsTextAllowedForTimeDiff;
   }

   public void setCastTimeDiffToText(boolean yes) {
      this.isCastAsTextAllowedForTimeDiff = yes;
   }

   public void setDateArithmetic(String dateArithmetic) {
      this.dateArithmetic = dateArithmetic;
   }

   public void setObjectContext(UserObjectContext context) {
      this.context = context;
   }

   public void setOver(String over) {
      this.over = over;
   }

   public void setWithinGroup(String withinGroup) {
      this.withinGroup = withinGroup;
   }

   public void setOrderBy(OrderByStatement obs) {
      this.obs = obs;
   }

   public void setPartitionBy(String partitionBy) {
      this.partitionBy = partitionBy;
   }

   public void setFunctionName(TableColumn tc_fn) {
      this.functionName = tc_fn;
   }

   public void setArgumentQualifier(String s_aq) {
      this.argumentQualifier = s_aq;
   }

   public void setFunctionArguments(Vector v_fa) {
      this.functionArguments = v_fa;
   }

   public void setTrailingString(String trailingString) {
      this.trailingString = trailingString;
   }

   public void setFromInTrim(String fromStringInFunction) {
      this.fromStringInFunction = fromStringInFunction;
   }

   public void setForLength(String forStringInFunction) {
      this.forStringInFunction = forStringInFunction;
   }

   public void setLengthString(String lengthString) {
      this.lengthString = lengthString;
   }

   public void setAsDatatype(String asDatatype) {
      this.asDatatype = asDatatype;
   }

   public void setUsing(String using) {
      this.using = using;
   }

   public void setInString(String in) {
      this.inString = in;
   }

   public void setSeparatorString(String separatorString) {
      this.separatorString = separatorString;
   }

   public void setDivisionBy31(String divisionBy31) {
      this.divisionBy31 = divisionBy31;
   }

   public void setTargetDataType(String targetDataType) {
      this.targetDataType = targetDataType;
   }

   public void setOpenBracesForFunctionNameRequired(boolean openBracesForFunctionNameRequired) {
      this.openBracesForFunctionNameRequired = openBracesForFunctionNameRequired;
   }

   public void setToDateExpression(String toDateString) {
      this.toDateString = toDateString;
   }

   public void setToDateSymbolValue(String toDateSymbolValue) {
      this.toDateSymbolValue = toDateSymbolValue;
   }

   public void setPartitionByClause(QueryPartitionClause pbc) {
      this.partitionByClause = pbc;
   }

   public void registerOverrideToString(OverrideToString ots) {
      this.override_to_string = ots;
   }

   public void setWrapper(String wrapper) {
      this.wrapper = wrapper;
   }

   public void setCommentClass(CommentClass commentObj) {
      this.commentObj = commentObj;
   }

   public void setAdventNetMessageString(String message) {
      this.adventNetMessage = message;
   }

   public void setWindowingClause(WindowingClause windowClause) {
      this.windowClause = windowClause;
   }

   public void setLast(String last) {
      this.last = last;
   }

   public void setFirst(String first) {
      this.first = first;
   }

   public void setKeep(String keep) {
      this.keep = keep;
   }

   public void setDenseRank(String denseRank) {
      this.denseRank = denseRank;
   }

   public void setAtTimeZoneRegion(SelectColumn atTZR) {
      this.atTimeZoneRegion = atTZR;
   }

   public void setUsingClause(String option) {
      this.usingClause = option;
   }

   public void setStripComma(boolean stripComma) {
      this.stripComma = stripComma;
   }

   public void setFcNestedIfCount(int count) {
      this.fcNestedIfCount = count;
   }

   public void setCustomDataType(String dataType) {
      this.customDataType = dataType;
   }

   public void setInstanceDatatype(String instanceDatatype) {
      this.instanceDatatype = instanceDatatype;
   }

   public String getDateArithmetic() {
      return this.dateArithmetic;
   }

   public TableColumn getFunctionName() {
      return this.functionName;
   }

   public String getWrapper() {
      return this.wrapper;
   }

   public String getArgumentQualifier() {
      return this.argumentQualifier;
   }

   public Vector getFunctionArguments() {
      return this.functionArguments;
   }

   public String getFunctionNameAsAString() {
      return this.functionName != null ? this.functionName.getColumnName() : null;
   }

   public String getTrailingString() {
      return this.trailingString;
   }

   public String getFromInTrim() {
      return this.fromStringInFunction;
   }

   public String getForLength() {
      return this.forStringInFunction;
   }

   public String getLengthString() {
      return this.lengthString;
   }

   public String getAsDatatype() {
      return this.asDatatype;
   }

   public String getUsing() {
      return this.using;
   }

   public String getInString() {
      return this.inString;
   }

   public String getSeparatorString() {
      return this.separatorString;
   }

   public String getDivisionBy31() {
      return this.divisionBy31;
   }

   public String getToDateExpression() {
      return this.toDateString;
   }

   public String getToDateSymbolValue() {
      return this.toDateSymbolValue;
   }

   public String getOver() {
      return this.over;
   }

   public String getWithinGroup() {
      return this.withinGroup;
   }

   public OrderByStatement getOrderBy() {
      return this.obs;
   }

   public String getPartitionBy() {
      return this.partitionBy;
   }

   public QueryPartitionClause getPartitionByClause() {
      return this.partitionByClause;
   }

   public CommentClass getCommentClass() {
      return this.commentObj;
   }

   public String getAdventNetMessageString() {
      return this.adventNetMessage;
   }

   public WindowingClause getWindowingClause() {
      return this.windowClause;
   }

   public String getKeep() {
      return this.keep;
   }

   public String getDenseRank() {
      return this.denseRank;
   }

   public String getLast() {
      return this.last;
   }

   public String getFirst() {
      return this.first;
   }

   public SelectColumn getAtTimeZoneRegion() {
      return this.atTimeZoneRegion;
   }

   public String getUsingClause() {
      return this.usingClause;
   }

   public boolean getOpenBracesForFunctionNameRequired() {
      return this.openBracesForFunctionNameRequired;
   }

   public int getFcNestedIfCount() {
      return this.fcNestedIfCount;
   }

   public String getCustomDataType() {
      return this.customDataType;
   }

   public void setInArithmeticExpression(boolean inArithmeticExpr) {
      this.inArithmeticExpr = inArithmeticExpr;
   }

   public boolean isStripComma() {
      return this.stripComma;
   }

   public String getInstanceDatatype() {
      return this.instanceDatatype;
   }

   public FunctionCalls toTeradataSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      FunctionCalls functioncall = null;
      if (this.functionName != null) {
         functioncall = getNewInstance(this.functionName.getColumnName());
      }

      if (functioncall == null) {
         FunctionCalls newFunctioncall = new FunctionCalls();
         if (this.argumentQualifier != null) {
            if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
               newFunctioncall.setArgumentQualifier("DISTINCT");
            } else {
               newFunctioncall.setArgumentQualifier(this.argumentQualifier);
            }
         }

         newFunctioncall.setTrailingString(this.trailingString);
         newFunctioncall.setAsDatatype(this.getAsDatatype());
         newFunctioncall.setFromInTrim(this.fromStringInFunction);
         if (this.functionArguments != null) {
            Vector newFunctionArguments = new Vector();

            for(int i = 0; i < this.functionArguments.size(); ++i) {
               if (this.functionArguments.get(i) instanceof SelectColumn) {
                  newFunctionArguments.add(((SelectColumn)this.functionArguments.get(i)).toTeradataSelect(to_sqs, from_sqs));
               }

               if (this.functionArguments.get(i) instanceof Datatype) {
                  Datatype newDatatype = (Datatype)this.functionArguments.get(i);
                  newDatatype.toTeradataString();
                  newFunctionArguments.add(newDatatype);
               }

               if (this.functionArguments.get(i) instanceof String) {
                  newFunctionArguments.add((String)this.functionArguments.get(i));
               }

               if (this.functionArguments.get(i) instanceof Integer) {
                  newFunctionArguments.add((Integer)this.functionArguments.get(i));
               }

               if (this.functionArguments.get(i) instanceof Double) {
                  newFunctionArguments.add((Double)this.functionArguments.get(i));
               }
            }

            newFunctioncall.setFunctionArguments(newFunctionArguments);
         }

         if (this.obs != null) {
            newFunctioncall.setOrderBy(this.obs.toTeradataSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
         }

         newFunctioncall.setOpenBracesForFunctionNameRequired(this.openBracesForFunctionNameRequired);
         newFunctioncall.setKeep(this.keep);
         newFunctioncall.setDenseRank(this.denseRank);
         newFunctioncall.setFirst(this.first);
         newFunctioncall.setLast(this.last);
         newFunctioncall.setOver(this.over);
         newFunctioncall.setWithinGroup(this.withinGroup);
         if (from_sqs != null && (this.obs != null || this.partitionByClause != null)) {
            from_sqs.setOlapFunctionPresent(true);
         }

         if (this.partitionByClause != null) {
            newFunctioncall.setPartitionByClause(this.partitionByClause.toTeradataSelect(to_sqs, from_sqs));
         }

         if (this.windowClause != null) {
            newFunctioncall.setWindowingClause(this.windowClause.toTeradata(to_sqs, from_sqs));
         }

         if (this.atTimeZoneRegion != null) {
            functioncall.setAtTimeZoneRegion(this.atTimeZoneRegion);
         }

         newFunctioncall.setFunctionName(this.functionName);
         newFunctioncall.setForLength(this.forStringInFunction);
         newFunctioncall.setLengthString(this.lengthString);
         return newFunctioncall;
      } else {
         if (this.argumentQualifier != null) {
            if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
               functioncall.setArgumentQualifier("DISTINCT");
            } else {
               functioncall.setArgumentQualifier(this.argumentQualifier);
            }
         }

         functioncall.setTrailingString(this.trailingString);
         functioncall.setAsDatatype(this.getAsDatatype());
         functioncall.setFromInTrim(this.fromStringInFunction);
         functioncall.setFunctionArguments(this.functionArguments);
         this.functionName.setIsFunctionName(true);
         functioncall.setFunctionName(this.functionName.toTeradataSelect(to_sqs, from_sqs));
         functioncall.setForLength(this.forStringInFunction);
         functioncall.setLengthString(this.lengthString);
         functioncall.setDateArithmetic(this.dateArithmetic);
         if (this.obs != null) {
            functioncall.setOrderBy(this.obs.toTeradataSelect(to_sqs, from_sqs));
         }

         functioncall.setOpenBracesForFunctionNameRequired(this.openBracesForFunctionNameRequired);
         functioncall.setKeep(this.keep);
         functioncall.setDenseRank(this.denseRank);
         functioncall.setFirst(this.first);
         functioncall.setLast(this.last);
         functioncall.setOver(this.over);
         functioncall.setWithinGroup(this.withinGroup);
         if (from_sqs != null && (this.obs != null || this.partitionByClause != null)) {
            from_sqs.setOlapFunctionPresent(true);
         }

         if (this.partitionByClause != null) {
            functioncall.setPartitionByClause(this.partitionByClause.toTeradataSelect(to_sqs, from_sqs));
         }

         if (this.windowClause != null) {
            functioncall.setWindowingClause(this.windowClause.toTeradata(to_sqs, from_sqs));
         }

         if (this.atTimeZoneRegion != null) {
            functioncall.setAtTimeZoneRegion(this.atTimeZoneRegion);
         }

         functioncall.toTeradata(to_sqs, from_sqs);
         if (functioncall.getKeep() != null && functioncall.getDenseRank() != null) {
            this.handleKeepDenseRank(to_sqs, from_sqs, functioncall);
         }

         functioncall.setCommentClass(this.commentObj);
         return functioncall;
      }
   }

   public FunctionCalls toANSISelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.CaseString != null && this.functionName == null) {
         return this;
      } else {
         FunctionCalls functioncall = null;
         if (this.functionName != null) {
            functioncall = getNewInstance(this.functionName.getColumnName());
         }

         if (functioncall == null) {
            FunctionCalls newFunctioncall = new FunctionCalls();
            if (this.argumentQualifier != null) {
               if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
                  newFunctioncall.setArgumentQualifier("DISTINCT");
               } else {
                  newFunctioncall.setArgumentQualifier(this.argumentQualifier);
               }
            }

            newFunctioncall.setTrailingString(this.trailingString);
            newFunctioncall.setAsDatatype(this.getAsDatatype());
            newFunctioncall.setFromInTrim(this.fromStringInFunction);
            if (this.functionArguments != null) {
               Vector newFunctionArguments = new Vector();

               for(int i = 0; i < this.functionArguments.size(); ++i) {
                  if (this.functionArguments.get(i) instanceof SelectColumn) {
                     newFunctionArguments.add(((SelectColumn)this.functionArguments.get(i)).toANSISelect(to_sqs, from_sqs));
                  }

                  if (this.functionArguments.get(i) instanceof Datatype) {
                     Datatype newDatatype = (Datatype)this.functionArguments.get(i);
                     newDatatype.toANSIString();
                     newFunctionArguments.add(newDatatype);
                  }

                  if (this.functionArguments.get(i) instanceof String) {
                     newFunctionArguments.add((String)this.functionArguments.get(i));
                  }

                  if (this.functionArguments.get(i) instanceof Integer) {
                     newFunctionArguments.add((Integer)this.functionArguments.get(i));
                  }

                  if (this.functionArguments.get(i) instanceof Double) {
                     newFunctionArguments.add((Double)this.functionArguments.get(i));
                  }
               }

               newFunctioncall.setFunctionArguments(newFunctionArguments);
            }

            if (from_sqs != null) {
               if (from_sqs.getOrderByStatement() != null) {
                  if (this.obs != null) {
                     from_sqs.getOrderByStatement().addOrderItems(this.obs.getOrderItemList());
                     this.obs.setOrderItemList((Vector)null);
                     this.obs.setOrderClause((String)null);
                  }
               } else {
                  from_sqs.setOrderByStatement(this.obs);
               }
            }

            newFunctioncall.setFunctionName(this.functionName);
            newFunctioncall.setForLength(this.forStringInFunction);
            newFunctioncall.setLengthString(this.lengthString);
            return newFunctioncall;
         } else {
            if (this.argumentQualifier != null) {
               if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
                  functioncall.setArgumentQualifier("DISTINCT");
               } else {
                  functioncall.setArgumentQualifier(this.argumentQualifier);
               }
            }

            functioncall.setTrailingString(this.trailingString);
            functioncall.setAsDatatype(this.getAsDatatype());
            functioncall.setFromInTrim(this.fromStringInFunction);
            functioncall.setFunctionArguments(this.functionArguments);
            this.functionName.setIsFunctionName(true);
            functioncall.setFunctionName(this.functionName.toANSISelect(to_sqs, from_sqs));
            functioncall.setForLength(this.forStringInFunction);
            functioncall.setLengthString(this.lengthString);
            functioncall.setDateArithmetic(this.dateArithmetic);
            functioncall.setOrderBy(this.obs);
            functioncall.setOver(this.over);
            if (this.partitionByClause != null) {
               functioncall.setPartitionByClause(this.partitionByClause.toANSISelect(to_sqs, from_sqs));
            }

            if (from_sqs != null) {
               if (from_sqs.getOrderByStatement() != null) {
                  if (this.obs != null) {
                     from_sqs.getOrderByStatement().addOrderItems(this.obs.getOrderItemList());
                     this.obs.setOrderItemList((Vector)null);
                     this.obs.setOrderClause((String)null);
                  }
               } else {
                  from_sqs.setOrderByStatement(this.obs);
               }
            }

            functioncall.toANSISQL(to_sqs, from_sqs);
            functioncall.setCommentClass(this.commentObj);
            return functioncall;
         }
      }
   }

   public FunctionCalls toMSSQLServerSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      FunctionCalls functioncall = null;
      if (this.functionName != null) {
         functioncall = getClassName(this.functionName.getColumnName(), 2);
         String funcName = this.functionName.toString();
         if (SwisSQLOptions.fromSybase && (funcName.equalsIgnoreCase("LEFT") || funcName.equalsIgnoreCase("LTRIM") || funcName.equalsIgnoreCase("REPLICATE") || funcName.equalsIgnoreCase("RIGHT") || funcName.equalsIgnoreCase("RTRIM") || funcName.equalsIgnoreCase("SPACE") || funcName.equalsIgnoreCase("SUBSTRING"))) {
            functioncall.setWrapper("NULLIF");
         }
      }

      if (functioncall == null) {
         FunctionCalls newFunctioncall = new FunctionCalls();
         if (this.argumentQualifier != null) {
            if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
               newFunctioncall.setArgumentQualifier("DISTINCT");
            } else {
               newFunctioncall.setArgumentQualifier(this.argumentQualifier);
            }
         }

         newFunctioncall.setTrailingString(this.trailingString);
         newFunctioncall.setAsDatatype(this.getAsDatatype());
         newFunctioncall.setFromInTrim(this.fromStringInFunction);
         newFunctioncall.setOpenBracesForFunctionNameRequired(this.openBracesForFunctionNameRequired);
         if (this.functionArguments != null) {
            Vector newFunctionArguments = new Vector();

            for(int i = 0; i < this.functionArguments.size(); ++i) {
               if (this.functionArguments.get(i) instanceof SelectColumn) {
                  newFunctionArguments.add(((SelectColumn)this.functionArguments.get(i)).toMSSQLServerSelect(to_sqs, from_sqs));
               }

               if (this.functionArguments.get(i) instanceof Datatype) {
                  Datatype newDatatype = (Datatype)this.functionArguments.get(i);
                  newDatatype.toMSSQLServerString();
                  newFunctionArguments.add(newDatatype);
               }

               if (this.functionArguments.get(i) instanceof String) {
                  newFunctionArguments.add((String)this.functionArguments.get(i));
               }

               if (this.functionArguments.get(i) instanceof Integer) {
                  newFunctionArguments.add((Integer)this.functionArguments.get(i));
               }

               if (this.functionArguments.get(i) instanceof Double) {
                  newFunctionArguments.add((Double)this.functionArguments.get(i));
               }
            }

            newFunctioncall.setFunctionArguments(newFunctionArguments);
         }

         newFunctioncall.setFunctionName(this.functionName);
         newFunctioncall.setForLength(this.forStringInFunction);
         newFunctioncall.setLengthString(this.lengthString);
         newFunctioncall.setDateArithmetic(this.dateArithmetic);
         newFunctioncall.setOver(this.over);
         if (this.partitionByClause != null) {
            newFunctioncall.setPartitionByClause(this.partitionByClause.toMSSQLServerSelect(to_sqs, from_sqs));
         }

         if (this.windowClause != null) {
            newFunctioncall.setWindowingClause(this.windowClause.toMSSQLServer(to_sqs, from_sqs));
         }

         if ((this.partitionByClause != null || this.windowClause != null) && this.obs != null) {
            newFunctioncall.setOrderBy(this.obs.toMSSQLServerSelect(to_sqs, from_sqs));
         } else {
            newFunctioncall.setOrderBy(this.obs);
         }

         return newFunctioncall;
      } else {
         if (this.argumentQualifier != null) {
            if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
               functioncall.setArgumentQualifier("DISTINCT");
            } else {
               functioncall.setArgumentQualifier(this.argumentQualifier);
            }
         }

         functioncall.setTrailingString(this.trailingString);
         functioncall.setAsDatatype(this.getAsDatatype());
         functioncall.setFromInTrim(this.fromStringInFunction);
         functioncall.setFunctionArguments(this.functionArguments);
         this.functionName.setIsFunctionName(true);
         functioncall.setFunctionName(this.functionName.toMSSQLServerSelect(to_sqs, from_sqs));
         functioncall.setForLength(this.forStringInFunction);
         functioncall.setLengthString(this.lengthString);
         functioncall.setDateArithmetic(this.dateArithmetic);
         functioncall.setOver(this.over);
         if (this.partitionByClause != null) {
            functioncall.setPartitionByClause(this.partitionByClause.toMSSQLServerSelect(to_sqs, from_sqs));
         }

         if (this.windowClause != null) {
            functioncall.setWindowingClause(this.windowClause.toMSSQLServer(to_sqs, from_sqs));
         }

         if ((this.partitionByClause != null || this.windowClause != null) && this.obs != null) {
            functioncall.setOrderBy(this.obs.toMSSQLServerSelect(to_sqs, from_sqs));
         } else {
            functioncall.setOrderBy(this.obs);
         }

         functioncall.toMSSQLServer(to_sqs, from_sqs);
         functioncall.setCommentClass(this.commentObj);
         return functioncall;
      }
   }

   public FunctionCalls toSybaseSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      FunctionCalls functioncall = null;
      if (this.functionName != null) {
         functioncall = getNewInstance(this.functionName.getColumnName());
      }

      if (functioncall == null) {
         FunctionCalls newFunctioncall = new FunctionCalls();
         if (this.argumentQualifier != null) {
            if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
               newFunctioncall.setArgumentQualifier("DISTINCT");
            } else {
               newFunctioncall.setArgumentQualifier(this.argumentQualifier);
            }
         }

         newFunctioncall.setTrailingString(this.trailingString);
         newFunctioncall.setAsDatatype(this.getAsDatatype());
         newFunctioncall.setFromInTrim(this.fromStringInFunction);
         newFunctioncall.setObjectContext(this.context);
         if (this.functionArguments != null) {
            Vector newFunctionArguments = new Vector();

            for(int i = 0; i < this.functionArguments.size(); ++i) {
               if (this.functionArguments.get(i) instanceof SelectColumn) {
                  ((SelectColumn)this.functionArguments.get(i)).setObjectContext(this.context);
                  newFunctionArguments.add(((SelectColumn)this.functionArguments.get(i)).toSybaseSelect(to_sqs, from_sqs));
               }

               if (this.functionArguments.get(i) instanceof Datatype) {
                  Datatype newDatatype = (Datatype)this.functionArguments.get(i);
                  newDatatype.toSybaseString();
                  newFunctionArguments.add(newDatatype);
               }

               if (this.functionArguments.get(i) instanceof String) {
                  newFunctionArguments.add((String)this.functionArguments.get(i));
               }

               if (this.functionArguments.get(i) instanceof Integer) {
                  newFunctionArguments.add((Integer)this.functionArguments.get(i));
               }

               if (this.functionArguments.get(i) instanceof Double) {
                  newFunctionArguments.add((Double)this.functionArguments.get(i));
               }
            }

            newFunctioncall.setFunctionArguments(newFunctionArguments);
         }

         newFunctioncall.setFunctionName(this.functionName);
         newFunctioncall.setForLength(this.forStringInFunction);
         newFunctioncall.setLengthString(this.lengthString);
         newFunctioncall.setDateArithmetic(this.dateArithmetic);
         if (from_sqs != null) {
            if (from_sqs.getOrderByStatement() != null) {
               if (this.obs != null) {
                  from_sqs.getOrderByStatement().addOrderItems(this.obs.getOrderItemList());
                  this.obs.setOrderItemList((Vector)null);
                  this.obs.setOrderClause((String)null);
               }
            } else {
               from_sqs.setOrderByStatement(this.obs);
            }
         }

         try {
            if (newFunctioncall.getFunctionName() != null) {
               TableColumn functionName = newFunctioncall.getFunctionName();
               if (functionName.getTableName() == null && functionName.getColumnName() != null && !functionName.getColumnName().equals("") && !this.isSybaseSystemFunction(functionName.getColumnName().toLowerCase())) {
                  functionName.setTableName("DBO");
                  functionName.setDot(".");
                  if (this.context != null) {
                     functionName.setColumnName(this.context.getEquivalent(functionName.getColumnName()).toString());
                  }
               }
            }
         } catch (Exception var8) {
         }

         return newFunctioncall;
      } else {
         if (this.argumentQualifier != null) {
            if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
               functioncall.setArgumentQualifier("DISTINCT");
            } else {
               functioncall.setArgumentQualifier(this.argumentQualifier);
            }
         }

         functioncall.setTrailingString(this.trailingString);
         functioncall.setAsDatatype(this.getAsDatatype());
         functioncall.setFromInTrim(this.fromStringInFunction);

         for(int l = 0; l < this.functionArguments.size(); ++l) {
            if (this.functionArguments.get(l) instanceof SelectColumn) {
               ((SelectColumn)this.functionArguments.get(l)).setObjectContext(this.context);
            }
         }

         functioncall.setFunctionArguments(this.functionArguments);
         this.functionName.setIsFunctionName(true);
         functioncall.setFunctionName(this.functionName.toSybaseSelect(to_sqs, from_sqs));
         functioncall.setForLength(this.forStringInFunction);
         functioncall.setLengthString(this.lengthString);
         functioncall.setObjectContext(this.context);
         functioncall.setDateArithmetic(this.dateArithmetic);
         if (from_sqs != null) {
            if (from_sqs.getOrderByStatement() != null) {
               if (this.obs != null) {
                  from_sqs.getOrderByStatement().addOrderItems(this.obs.getOrderItemList());
                  this.obs.setOrderItemList((Vector)null);
                  this.obs.setOrderClause((String)null);
               }
            } else {
               from_sqs.setOrderByStatement(this.obs);
            }
         }

         functioncall.toSybase(to_sqs, from_sqs);
         functioncall.setCommentClass(this.commentObj);
         return functioncall;
      }
   }

   public FunctionCalls toDB2Select(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      FunctionCalls functioncall = getClassName(this.functionName.getColumnName(), 3);
      if (functioncall == null) {
         FunctionCalls newFunctioncall = new FunctionCalls();
         if (this.argumentQualifier != null) {
            if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
               newFunctioncall.setArgumentQualifier("DISTINCT");
            } else {
               newFunctioncall.setArgumentQualifier(this.argumentQualifier);
            }
         }

         newFunctioncall.setTrailingString(this.trailingString);
         newFunctioncall.setAsDatatype(this.getAsDatatype());
         newFunctioncall.setFromInTrim(this.fromStringInFunction);
         if (this.functionArguments != null) {
            Vector newFunctionArguments = new Vector();

            for(int i = 0; i < this.functionArguments.size(); ++i) {
               if (this.functionArguments.get(i) instanceof SelectColumn) {
                  String tDataType = CastingUtil.getParameterDataType(this.functionName.getColumnName(), i);
                  ((SelectColumn)this.functionArguments.get(i)).setTargetDataType(tDataType);
                  newFunctionArguments.add(((SelectColumn)this.functionArguments.get(i)).toDB2Select(to_sqs, from_sqs));
               }

               if (this.functionArguments.get(i) instanceof Datatype) {
                  Datatype newDatatype = (Datatype)this.functionArguments.get(i);
                  newDatatype.toDB2String();
                  newFunctionArguments.add(newDatatype);
               }

               if (this.functionArguments.get(i) instanceof String) {
                  newFunctionArguments.add((String)this.functionArguments.get(i));
               }

               if (this.functionArguments.get(i) instanceof Integer) {
                  newFunctionArguments.add((Integer)this.functionArguments.get(i));
               }

               if (this.functionArguments.get(i) instanceof Double) {
                  newFunctionArguments.add((Double)this.functionArguments.get(i));
               }
            }

            newFunctioncall.setFunctionArguments(newFunctionArguments);
         }

         newFunctioncall.setFunctionName(this.functionName);
         if (this.functionName.getColumnName().equalsIgnoreCase("identity") && this.functionName.getTableName() == null) {
            this.functionName.setColumnName("ROW_NUMBER() OVER");
            newFunctioncall.setFunctionArguments(new Vector());
         }

         newFunctioncall.setForLength(this.forStringInFunction);
         newFunctioncall.setLengthString(this.lengthString);
         newFunctioncall.setOver(this.over);
         if (this.partitionByClause != null) {
            newFunctioncall.setPartitionByClause(this.partitionByClause.toDB2Select(to_sqs, from_sqs));
         }

         if (this.windowClause != null) {
            newFunctioncall.setWindowingClause(this.windowClause.toDB2(to_sqs, from_sqs));
         }

         if ((this.partitionByClause != null || this.windowClause != null) && this.obs != null) {
            newFunctioncall.setOrderBy(this.obs.toDB2Select(to_sqs, from_sqs));
         } else {
            newFunctioncall.setOrderBy(this.obs);
         }

         return newFunctioncall;
      } else {
         if (this.argumentQualifier != null) {
            if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
               functioncall.setArgumentQualifier("DISTINCT");
            } else {
               functioncall.setArgumentQualifier(this.argumentQualifier);
            }
         }

         functioncall.setTrailingString(this.trailingString);
         functioncall.setAsDatatype(this.getAsDatatype());
         functioncall.setFromInTrim(this.fromStringInFunction);
         functioncall.setFunctionArguments(this.functionArguments);
         this.functionName.setIsFunctionName(true);
         functioncall.setFunctionName(this.functionName.toDB2Select(to_sqs, from_sqs));
         functioncall.setForLength(this.forStringInFunction);
         functioncall.setLengthString(this.lengthString);
         functioncall.setDateArithmetic(this.dateArithmetic);
         functioncall.setOver(this.over);
         if (this.partitionByClause != null) {
            functioncall.setPartitionByClause(this.partitionByClause.toDB2Select(to_sqs, from_sqs));
         }

         if (this.windowClause != null) {
            functioncall.setWindowingClause(this.windowClause.toDB2(to_sqs, from_sqs));
         }

         if ((this.partitionByClause != null || this.windowClause != null) && this.obs != null) {
            functioncall.setOrderBy(this.obs.toDB2Select(to_sqs, from_sqs));
         } else if (from_sqs != null) {
            if (from_sqs.getOrderByStatement() != null) {
               if (this.obs != null) {
                  from_sqs.getOrderByStatement().addOrderItems(this.obs.getOrderItemList());
                  this.obs.setOrderItemList((Vector)null);
                  this.obs.setOrderClause((String)null);
               }
            } else {
               from_sqs.setOrderByStatement(this.obs);
            }
         }

         functioncall.setOpenBracesForFunctionNameRequired(this.openBracesForFunctionNameRequired);
         if (functioncall instanceof decode || functioncall instanceof nvl) {
            functioncall.setTargetDataType(this.targetDataType);
            functioncall.setInArithmeticExpression(this.inArithmeticExpr);
         }

         functioncall.toDB2(to_sqs, from_sqs);
         functioncall.setCommentClass(this.commentObj);
         return functioncall;
      }
   }

   public FunctionCalls toOracleSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      FunctionCalls functioncall = getClassName(this.functionName.getColumnName(), 1);
      boolean isdenodo = from_sqs != null && from_sqs.isDenodo();
      Vector newFunctionArguments;
      if (functioncall == null) {
         FunctionCalls newFunctioncall = new FunctionCalls();
         if (this.argumentQualifier != null) {
            newFunctioncall.setArgumentQualifier(this.argumentQualifier);
         }

         newFunctioncall.setTrailingString(this.trailingString);
         newFunctioncall.setAsDatatype(this.getAsDatatype());
         newFunctioncall.setFromInTrim(this.fromStringInFunction);
         newFunctioncall.setOpenBracesForFunctionNameRequired(this.openBracesForFunctionNameRequired);
         if (this.functionArguments != null) {
            newFunctionArguments = new Vector();

            for(int i = 0; i < this.functionArguments.size(); ++i) {
               if (this.functionArguments.get(i) instanceof SelectColumn) {
                  newFunctionArguments.add(((SelectColumn)this.functionArguments.get(i)).toOracleSelect(to_sqs, from_sqs));
               }

               if (this.functionArguments.get(i) instanceof Datatype) {
                  Datatype newDatatype = (Datatype)this.functionArguments.get(i);
                  if (isdenodo) {
                     newDatatype.toDenodoString();
                  } else {
                     newDatatype.toOracleString();
                  }

                  newFunctionArguments.add(newDatatype);
               }

               if (this.functionArguments.get(i) instanceof String) {
                  newFunctionArguments.add((String)this.functionArguments.get(i));
               }

               if (this.functionArguments.get(i) instanceof Integer) {
                  newFunctionArguments.add((Integer)this.functionArguments.get(i));
               }

               if (this.functionArguments.get(i) instanceof Double) {
                  newFunctionArguments.add((Double)this.functionArguments.get(i));
               }
            }

            newFunctioncall.setFunctionArguments(newFunctionArguments);
         }

         if (this.functionName != null) {
            String tempTableName = this.functionName.getTableName();
            String colName = this.functionName.getColumnName();
            if (this.functionName.getColumnName().startsWith("[") && this.functionName.getColumnName().endsWith("]")) {
               this.functionName.setColumnName(colName.substring(1, colName.length() - 1));
            }

            if (tempTableName == null && colName != null && colName.equalsIgnoreCase("quotename")) {
               this.functionName.setColumnName("");
               newFunctioncall.setOpenBracesForFunctionNameRequired(false);
               Object obj = newFunctioncall.getFunctionArguments().get(0);
               Vector fnArgs = new Vector();
               fnArgs.add(obj);
               newFunctioncall.setFunctionArguments(fnArgs);
            }

            if (tempTableName != null && (tempTableName.equalsIgnoreCase("dbo") || tempTableName.equalsIgnoreCase("[dbo]"))) {
               if (colName.length() > 30) {
                  colName = colName.substring(0, 30);
               }

               colName = CustomizeUtil.objectNamesToQuotedIdentifier(colName, SwisSQLUtils.getKeywords(1), (ModifiedObjectAttr)null, 1);
               this.functionName.setColumnName(colName);
               this.functionName.setTableName((String)null);
            }
         }

         newFunctioncall.setFunctionName(this.functionName);
         newFunctioncall.setForLength(this.forStringInFunction);
         newFunctioncall.setLengthString(this.lengthString);
         newFunctioncall.setOver(this.over);
         if (this.partitionByClause != null) {
            newFunctioncall.setPartitionByClause(this.partitionByClause.toOracleSelect(to_sqs, from_sqs));
         }

         if (this.windowClause != null) {
            newFunctioncall.setWindowingClause(this.windowClause.toOracle(to_sqs, from_sqs));
         }

         if ((this.partitionByClause != null || this.windowClause != null) && this.obs != null) {
            newFunctioncall.setOrderBy(this.obs.toOracleSelect(to_sqs, from_sqs));
         } else {
            newFunctioncall.setOrderBy(this.obs);
         }

         newFunctioncall.setObjectContext(this.context);
         newFunctioncall.setCommentClass(this.commentObj);
         newFunctioncall.setDateArithmetic(this.dateArithmetic);
         return newFunctioncall;
      } else {
         functioncall.setArgumentQualifier(this.argumentQualifier);
         functioncall.setAsDatatype(this.getAsDatatype());
         functioncall.setTrailingString(this.trailingString);
         functioncall.setFromInTrim(this.fromStringInFunction);
         functioncall.setFunctionArguments(this.functionArguments);
         this.functionName.setIsFunctionName(true);
         functioncall.setFunctionName(this.functionName.toOracleSelect(to_sqs, from_sqs));
         functioncall.setForLength(this.forStringInFunction);
         functioncall.setLengthString(this.lengthString);
         functioncall.setDateArithmetic(this.dateArithmetic);
         functioncall.setOver(this.over);
         if (this.partitionByClause != null) {
            functioncall.setPartitionByClause(this.partitionByClause.toOracleSelect(to_sqs, from_sqs));
         }

         if (this.windowClause != null) {
            functioncall.setWindowingClause(this.windowClause.toOracle(to_sqs, from_sqs));
         }

         if ((this.partitionByClause != null || this.windowClause != null) && this.obs != null) {
            functioncall.setOrderBy(this.obs.toOracleSelect(to_sqs, from_sqs));
         } else {
            functioncall.setOrderBy(this.obs);
         }

         functioncall.setObjectContext(this.context);
         functioncall.toOracle(to_sqs, from_sqs);
         functioncall.setCommentClass(this.commentObj);
         if (from_sqs.isOracleLive() && functioncall.getFunctionName().toString().equalsIgnoreCase("TO_CHAR") && functioncall.getFunctionArguments().size() == 2) {
            SelectColumn selectColumn = new SelectColumn();
            newFunctionArguments = new Vector();
            Vector timeStamp = new Vector();
            SelectColumn selectColumn1 = new SelectColumn();
            Vector v1 = new Vector();
            FunctionCalls inner_call = new FunctionCalls();
            TableColumn inner_tc = new TableColumn();
            String date_format = functioncall.getFunctionArguments().get(1).toString();
            List invalid_formats = Arrays.asList("%D", "%e", "%U", "%u", "%v", "%V", "%X", "%x");
            Iterator var15 = invalid_formats.iterator();

            while(var15.hasNext()) {
               Object invalid_format = var15.next();
               if (date_format.contains((String)invalid_format)) {
                  throw new ConvertException("The specific format is not supported");
               }
            }

            String changed_format = date_format.replace("%b", "Mon");
            changed_format = changed_format.replace("%a", "Dy");
            changed_format = changed_format.replace("%c", "MM");
            changed_format = changed_format.replace("%d", "DD");
            changed_format = changed_format.replace("%f", "FF");
            changed_format = changed_format.replace("%H", "HH24");
            changed_format = changed_format.replace("%h", "HH");
            changed_format = changed_format.replace("%I", "HH");
            changed_format = changed_format.replace("%i", "MI");
            changed_format = changed_format.replace("%j", "DDD");
            changed_format = changed_format.replace("%k", "HH24");
            changed_format = changed_format.replace("%l", "HH");
            changed_format = changed_format.replace("%M", "FMMonthFM");
            changed_format = changed_format.replace("%m", "MM");
            changed_format = changed_format.replace("%p", "AM");
            changed_format = changed_format.replace("%r", "HH12:MI:SS AM");
            changed_format = changed_format.replace("%s", "SS");
            changed_format = changed_format.replace("%S", "SS");
            changed_format = changed_format.replace("%T", "HH24:MI:SS");
            changed_format = changed_format.replace("%w", "D");
            changed_format = changed_format.replace("%W", "FMDayFM");
            changed_format = changed_format.replace("%Y", "YYYY");
            changed_format = changed_format.replace("%y", "YY");
            changed_format = changed_format.replace(".0", "");
            newFunctionArguments.add(0, changed_format);
            selectColumn.setColumnExpression(newFunctionArguments);
            String date = functioncall.getFunctionArguments().get(0).toString();
            String convertedDate = StringFunctions.handleLiteralStringDateForOracle(date);
            if (!date.equalsIgnoreCase(convertedDate)) {
               inner_tc.setColumnName(convertedDate);
               inner_call.setOpenBracesForFunctionNameRequired(false);
            } else {
               inner_tc.setColumnName("TO_TIMESTAMP");
               timeStamp.add(0, functioncall.getFunctionArguments().get(0));
               inner_call.setFunctionArguments(timeStamp);
            }

            inner_call.setFunctionName(inner_tc);
            v1.add(inner_call);
            selectColumn1.setColumnExpression(v1);
            functioncall.getFunctionArguments().set(0, selectColumn1);
            functioncall.getFunctionArguments().set(1, selectColumn);
         }

         return functioncall;
      }
   }

   public FunctionCalls toBigQuerySelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      FunctionCalls functioncall = getClassName(this.functionName.getColumnName(), 14);
      if (functioncall == null) {
         FunctionCalls newFunctioncall = new FunctionCalls();
         newFunctioncall.setCastToTextInsideIf(this.castToTextInsideIf);
         if (this.argumentQualifier != null) {
            if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
               newFunctioncall.setArgumentQualifier("DISTINCT");
            } else {
               newFunctioncall.setArgumentQualifier(this.argumentQualifier);
            }
         }

         newFunctioncall.setTrailingString(this.trailingString);
         newFunctioncall.setAsDatatype(this.getAsDatatype());
         newFunctioncall.setFromInTrim(this.fromStringInFunction);
         newFunctioncall.setDateArithmetic(this.dateArithmetic);
         if (this.functionArguments != null) {
            Vector newFunctionArguments = new Vector();
            this.handlePositionFunctionArguments(this.functionName.getColumnName(), this.functionArguments, "STRING");

            for(int i = 0; i < this.functionArguments.size(); ++i) {
               if (this.functionArguments.get(i) instanceof SelectColumn) {
                  newFunctionArguments.add(((SelectColumn)this.functionArguments.get(i)).toBigQuerySelect(to_sqs, from_sqs));
               }

               if (this.functionArguments.get(i) instanceof Datatype) {
                  Datatype newDatatype = (Datatype)this.functionArguments.get(i);
                  newDatatype.toBigQueryString();
                  newFunctionArguments.add(newDatatype);
               }

               if (this.functionArguments.get(i) instanceof String) {
                  newFunctionArguments.add((String)this.functionArguments.get(i));
               }

               if (this.functionArguments.get(i) instanceof Integer) {
                  newFunctionArguments.add((Integer)this.functionArguments.get(i));
               }

               if (this.functionArguments.get(i) instanceof Double) {
                  newFunctionArguments.add((Double)this.functionArguments.get(i));
               }
            }

            newFunctioncall.setFunctionArguments(newFunctionArguments);
         }

         newFunctioncall.setFunctionName(this.functionName);
         newFunctioncall.setForLength(this.forStringInFunction);
         newFunctioncall.setLengthString(this.lengthString);
         newFunctioncall.setFunctionName(this.functionName);
         newFunctioncall.setForLength(this.forStringInFunction);
         newFunctioncall.setLengthString(this.lengthString);
         newFunctioncall.setDateArithmetic(this.dateArithmetic);
         newFunctioncall.setOver(this.over);
         newFunctioncall.setWithinGroup(this.withinGroup);
         if (from_sqs != null && (this.obs != null || this.partitionByClause != null)) {
            from_sqs.setOlapFunctionPresent(true);
         }

         if (this.partitionByClause != null) {
            newFunctioncall.setPartitionByClause(this.partitionByClause.toBigQuerySelect(to_sqs, from_sqs));
         }

         if (this.windowClause != null) {
            newFunctioncall.setWindowingClause(this.windowClause.toBigQuery(to_sqs, from_sqs));
         }

         if ((this.partitionByClause != null || this.windowClause != null) && this.obs != null) {
            newFunctioncall.setOrderBy(this.obs.toBigQuerySelect(to_sqs, from_sqs));
         } else if (from_sqs != null) {
            if (this.functionName != null && this.functionName.getColumnName() != null && this.functionName.getColumnName().equalsIgnoreCase("GROUP_CONCAT") && this.obs != null) {
               functioncall.obs = this.obs;
            } else if (from_sqs.getOrderByStatement() != null) {
               if (this.obs != null) {
                  from_sqs.getOrderByStatement().addOrderItems(this.obs.getOrderItemList());
                  this.obs.setOrderItemList((Vector)null);
                  this.obs.setOrderClause((String)null);
               }
            } else {
               from_sqs.setOrderByStatement(this.obs);
            }
         }

         return newFunctioncall;
      } else {
         if (this.argumentQualifier != null) {
            if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
               functioncall.setArgumentQualifier("DISTINCT");
            } else {
               functioncall.setArgumentQualifier(this.argumentQualifier);
            }
         }

         functioncall.setTrailingString(this.trailingString);
         functioncall.setAsDatatype(this.getAsDatatype());
         functioncall.setFromInTrim(this.fromStringInFunction);
         functioncall.setFunctionArguments(this.functionArguments);
         this.functionName.setIsFunctionName(true);
         functioncall.setFunctionName(this.functionName.toBigQuerySelect(to_sqs, from_sqs));
         functioncall.setForLength(this.forStringInFunction);
         functioncall.setLengthString(this.lengthString);
         functioncall.setDateArithmetic(this.dateArithmetic);
         functioncall.setSeparatorString(this.separatorString);
         functioncall.setOver(this.over);
         functioncall.setWithinGroup(this.withinGroup);
         if (from_sqs != null && (this.obs != null || this.partitionByClause != null)) {
            from_sqs.setOlapFunctionPresent(true);
         }

         if (this.partitionByClause != null) {
            functioncall.setPartitionByClause(this.partitionByClause.toBigQuerySelect(to_sqs, from_sqs));
         }

         if (this.windowClause != null) {
            functioncall.setWindowingClause(this.windowClause.toBigQuery(to_sqs, from_sqs));
         }

         if ((this.partitionByClause != null || this.windowClause != null) && this.obs != null) {
            functioncall.setOrderBy(this.obs.toBigQuerySelect(to_sqs, from_sqs));
         } else if (from_sqs != null) {
            if (this.functionName != null && this.functionName.getColumnName() != null && this.functionName.getColumnName().equalsIgnoreCase("GROUP_CONCAT") && this.obs != null) {
               functioncall.obs = this.obs;
            } else if (from_sqs.getOrderByStatement() != null) {
               if (this.obs != null) {
                  from_sqs.getOrderByStatement().addOrderItems(this.obs.getOrderItemList());
                  this.obs.setOrderItemList((Vector)null);
                  this.obs.setOrderClause((String)null);
               }
            } else {
               from_sqs.setOrderByStatement(this.obs);
            }
         }

         functioncall.toBigQuery(to_sqs, from_sqs);
         functioncall.setCommentClass(this.commentObj);
         return functioncall;
      }
   }

   public FunctionCalls toAthenaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      FunctionCalls functioncall = getClassName(this.functionName.getColumnName(), 16);
      if (functioncall == null) {
         FunctionCalls newFunctioncall = new FunctionCalls();
         newFunctioncall.setCastToTextInsideIf(this.castToTextInsideIf);
         if (this.argumentQualifier != null) {
            if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
               newFunctioncall.setArgumentQualifier("DISTINCT");
            } else {
               newFunctioncall.setArgumentQualifier(this.argumentQualifier);
            }
         }

         newFunctioncall.setTrailingString(this.trailingString);
         newFunctioncall.setAsDatatype(this.getAsDatatype());
         newFunctioncall.setFromInTrim(this.fromStringInFunction);
         newFunctioncall.setDateArithmetic(this.dateArithmetic);
         if (this.functionArguments != null) {
            Vector newFunctionArguments = new Vector();
            this.handlePositionFunctionArguments(this.functionName.getColumnName(), this.functionArguments, "STRING");

            for(int i = 0; i < this.functionArguments.size(); ++i) {
               if (this.functionArguments.get(i) instanceof SelectColumn) {
                  newFunctionArguments.add(((SelectColumn)this.functionArguments.get(i)).toAthenaSelect(to_sqs, from_sqs));
               }

               if (this.functionArguments.get(i) instanceof Datatype) {
                  Datatype newDatatype = (Datatype)this.functionArguments.get(i);
                  newDatatype.toAthenaString();
                  newFunctionArguments.add(newDatatype);
               }

               if (this.functionArguments.get(i) instanceof String) {
                  newFunctionArguments.add((String)this.functionArguments.get(i));
               }

               if (this.functionArguments.get(i) instanceof Integer) {
                  newFunctionArguments.add((Integer)this.functionArguments.get(i));
               }

               if (this.functionArguments.get(i) instanceof Double) {
                  newFunctionArguments.add((Double)this.functionArguments.get(i));
               }
            }

            newFunctioncall.setFunctionArguments(newFunctionArguments);
         }

         newFunctioncall.setFunctionName(this.functionName);
         newFunctioncall.setForLength(this.forStringInFunction);
         newFunctioncall.setLengthString(this.lengthString);
         newFunctioncall.setFunctionName(this.functionName);
         newFunctioncall.setForLength(this.forStringInFunction);
         newFunctioncall.setLengthString(this.lengthString);
         newFunctioncall.setDateArithmetic(this.dateArithmetic);
         newFunctioncall.setOver(this.over);
         newFunctioncall.setWithinGroup(this.withinGroup);
         if (from_sqs != null && (this.obs != null || this.partitionByClause != null)) {
            from_sqs.setOlapFunctionPresent(true);
         }

         if (this.partitionByClause != null) {
            newFunctioncall.setPartitionByClause(this.partitionByClause.toAthenaSelect(to_sqs, from_sqs));
         }

         if (this.windowClause != null) {
            newFunctioncall.setWindowingClause(this.windowClause.toAthena(to_sqs, from_sqs));
         }

         if ((this.partitionByClause != null || this.windowClause != null) && this.obs != null) {
            newFunctioncall.setOrderBy(this.obs.toAthenaSelect(to_sqs, from_sqs));
         } else if (from_sqs != null) {
            if (this.functionName != null && this.functionName.getColumnName() != null && this.functionName.getColumnName().equalsIgnoreCase("GROUP_CONCAT") && this.obs != null) {
               functioncall.obs = this.obs;
            } else if (from_sqs.getOrderByStatement() != null) {
               if (this.obs != null) {
                  from_sqs.getOrderByStatement().addOrderItems(this.obs.getOrderItemList());
                  this.obs.setOrderItemList((Vector)null);
                  this.obs.setOrderClause((String)null);
               }
            } else {
               from_sqs.setOrderByStatement(this.obs);
            }
         }

         return newFunctioncall;
      } else {
         if (this.argumentQualifier != null) {
            if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
               functioncall.setArgumentQualifier("DISTINCT");
            } else {
               functioncall.setArgumentQualifier(this.argumentQualifier);
            }
         }

         functioncall.setTrailingString(this.trailingString);
         functioncall.setAsDatatype(this.getAsDatatype());
         functioncall.setFromInTrim(this.fromStringInFunction);
         functioncall.setFunctionArguments(this.functionArguments);
         this.functionName.setIsFunctionName(true);
         functioncall.setFunctionName(this.functionName.toAthenaSelect(to_sqs, from_sqs));
         functioncall.setForLength(this.forStringInFunction);
         functioncall.setLengthString(this.lengthString);
         functioncall.setDateArithmetic(this.dateArithmetic);
         functioncall.setSeparatorString(this.separatorString);
         functioncall.setOver(this.over);
         functioncall.setWithinGroup(this.withinGroup);
         if (from_sqs != null && (this.obs != null || this.partitionByClause != null)) {
            from_sqs.setOlapFunctionPresent(true);
         }

         if (this.partitionByClause != null) {
            functioncall.setPartitionByClause(this.partitionByClause.toAthenaSelect(to_sqs, from_sqs));
         }

         if (this.windowClause != null) {
            functioncall.setWindowingClause(this.windowClause.toAthena(to_sqs, from_sqs));
         }

         if ((this.partitionByClause != null || this.windowClause != null) && this.obs != null) {
            functioncall.setOrderBy(this.obs.toAthenaSelect(to_sqs, from_sqs));
         } else if (from_sqs != null) {
            if (this.functionName != null && this.functionName.getColumnName() != null && this.functionName.getColumnName().equalsIgnoreCase("GROUP_CONCAT") && this.obs != null) {
               functioncall.obs = this.obs;
            } else if (from_sqs.getOrderByStatement() != null) {
               if (this.obs != null) {
                  from_sqs.getOrderByStatement().addOrderItems(this.obs.getOrderItemList());
                  this.obs.setOrderItemList((Vector)null);
                  this.obs.setOrderClause((String)null);
               }
            } else {
               from_sqs.setOrderByStatement(this.obs);
            }
         }

         functioncall.toAthena(to_sqs, from_sqs);
         functioncall.setCommentClass(this.commentObj);
         return functioncall;
      }
   }

   public FunctionCalls toSapHanaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      FunctionCalls functioncall = getClassName(this.functionName.getColumnName(), 17);
      if (functioncall == null) {
         FunctionCalls newFunctioncall = new FunctionCalls();
         newFunctioncall.setCastToTextInsideIf(this.castToTextInsideIf);
         if (this.argumentQualifier != null) {
            if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
               newFunctioncall.setArgumentQualifier("DISTINCT");
            } else {
               newFunctioncall.setArgumentQualifier(this.argumentQualifier);
            }
         }

         newFunctioncall.setTrailingString(this.trailingString);
         newFunctioncall.setAsDatatype(this.getAsDatatype());
         newFunctioncall.setFromInTrim(this.fromStringInFunction);
         newFunctioncall.setDateArithmetic(this.dateArithmetic);
         if (this.functionArguments != null) {
            Vector newFunctionArguments = new Vector();
            this.handlePositionFunctionArguments(this.functionName.getColumnName(), this.functionArguments, "TEXT");

            for(int i = 0; i < this.functionArguments.size(); ++i) {
               if (this.functionArguments.get(i) instanceof SelectColumn) {
                  newFunctionArguments.add(((SelectColumn)this.functionArguments.get(i)).toSapHanaSelect(to_sqs, from_sqs));
               }

               if (this.functionArguments.get(i) instanceof Datatype) {
                  Datatype newDatatype = (Datatype)this.functionArguments.get(i);
                  newDatatype.toSapHanaString();
                  newFunctionArguments.add(newDatatype);
               }

               if (this.functionArguments.get(i) instanceof String) {
                  newFunctionArguments.add((String)this.functionArguments.get(i));
               }

               if (this.functionArguments.get(i) instanceof Integer) {
                  newFunctionArguments.add((Integer)this.functionArguments.get(i));
               }

               if (this.functionArguments.get(i) instanceof Double) {
                  newFunctionArguments.add((Double)this.functionArguments.get(i));
               }
            }

            newFunctioncall.setFunctionArguments(newFunctionArguments);
         }

         newFunctioncall.setFunctionName(this.functionName);
         newFunctioncall.setForLength(this.forStringInFunction);
         newFunctioncall.setLengthString(this.lengthString);
         newFunctioncall.setFunctionName(this.functionName);
         newFunctioncall.setForLength(this.forStringInFunction);
         newFunctioncall.setLengthString(this.lengthString);
         newFunctioncall.setDateArithmetic(this.dateArithmetic);
         newFunctioncall.setOver(this.over);
         newFunctioncall.setWithinGroup(this.withinGroup);
         if (from_sqs != null && (this.obs != null || this.partitionByClause != null)) {
            from_sqs.setOlapFunctionPresent(true);
         }

         if (this.partitionByClause != null) {
            newFunctioncall.setPartitionByClause(this.partitionByClause.toSapHanaSelect(to_sqs, from_sqs));
         }

         if (this.windowClause != null) {
            newFunctioncall.setWindowingClause(this.windowClause.toSapHana(to_sqs, from_sqs));
         }

         if ((this.partitionByClause != null || this.windowClause != null) && this.obs != null) {
            newFunctioncall.setOrderBy(this.obs.toSapHanaSelect(to_sqs, from_sqs));
         } else if (from_sqs != null) {
            if (this.functionName != null && this.functionName.getColumnName() != null && this.functionName.getColumnName().equalsIgnoreCase("GROUP_CONCAT") && this.obs != null) {
               functioncall.obs = this.obs;
            } else if (from_sqs.getOrderByStatement() != null) {
               if (this.obs != null) {
                  from_sqs.getOrderByStatement().addOrderItems(this.obs.getOrderItemList());
                  this.obs.setOrderItemList((Vector)null);
                  this.obs.setOrderClause((String)null);
               }
            } else {
               from_sqs.setOrderByStatement(this.obs);
            }
         }

         return newFunctioncall;
      } else {
         if (this.argumentQualifier != null) {
            if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
               functioncall.setArgumentQualifier("DISTINCT");
            } else {
               functioncall.setArgumentQualifier(this.argumentQualifier);
            }
         }

         functioncall.setTrailingString(this.trailingString);
         functioncall.setAsDatatype(this.getAsDatatype());
         functioncall.setFromInTrim(this.fromStringInFunction);
         functioncall.setFunctionArguments(this.functionArguments);
         this.functionName.setIsFunctionName(true);
         functioncall.setFunctionName(this.functionName.toSapHanaSelect(to_sqs, from_sqs));
         functioncall.setForLength(this.forStringInFunction);
         functioncall.setLengthString(this.lengthString);
         functioncall.setDateArithmetic(this.dateArithmetic);
         functioncall.setSeparatorString(this.separatorString);
         functioncall.setOver(this.over);
         functioncall.setWithinGroup(this.withinGroup);
         if (from_sqs != null && (this.obs != null || this.partitionByClause != null)) {
            from_sqs.setOlapFunctionPresent(true);
         }

         if (this.partitionByClause != null) {
            functioncall.setPartitionByClause(this.partitionByClause.toSapHanaSelect(to_sqs, from_sqs));
         }

         if (this.windowClause != null) {
            functioncall.setWindowingClause(this.windowClause.toSapHana(to_sqs, from_sqs));
         }

         if ((this.partitionByClause != null || this.windowClause != null) && this.obs != null) {
            functioncall.setOrderBy(this.obs.toSapHanaSelect(to_sqs, from_sqs));
         } else if (from_sqs != null) {
            if (this.functionName != null && this.functionName.getColumnName() != null && this.functionName.getColumnName().equalsIgnoreCase("GROUP_CONCAT") && this.obs != null) {
               functioncall.obs = this.obs;
            } else if (from_sqs.getOrderByStatement() != null) {
               if (this.obs != null) {
                  from_sqs.getOrderByStatement().addOrderItems(this.obs.getOrderItemList());
                  this.obs.setOrderItemList((Vector)null);
                  this.obs.setOrderClause((String)null);
               }
            } else {
               from_sqs.setOrderByStatement(this.obs);
            }
         }

         functioncall.toSapHana(to_sqs, from_sqs);
         functioncall.setCommentClass(this.commentObj);
         return functioncall;
      }
   }

   public FunctionCalls toSqliteSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      FunctionCalls functioncall = getClassName(this.functionName.getColumnName(), 18);
      if (functioncall == null) {
         FunctionCalls newFunctioncall = new FunctionCalls();
         newFunctioncall.setCastToTextInsideIf(this.castToTextInsideIf);
         if (this.argumentQualifier != null) {
            if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
               newFunctioncall.setArgumentQualifier("DISTINCT");
            } else {
               newFunctioncall.setArgumentQualifier(this.argumentQualifier);
            }
         }

         newFunctioncall.setTrailingString(this.trailingString);
         newFunctioncall.setAsDatatype(this.getAsDatatype());
         newFunctioncall.setFromInTrim(this.fromStringInFunction);
         newFunctioncall.setDateArithmetic(this.dateArithmetic);
         if (this.functionArguments != null) {
            Vector newFunctionArguments = new Vector();
            this.handlePositionFunctionArguments(this.functionName.getColumnName(), this.functionArguments, "TEXT");

            for(int i = 0; i < this.functionArguments.size(); ++i) {
               if (this.functionArguments.get(i) instanceof SelectColumn) {
                  newFunctionArguments.add(((SelectColumn)this.functionArguments.get(i)).toSqliteSelect(to_sqs, from_sqs));
               }

               if (this.functionArguments.get(i) instanceof Datatype) {
                  Datatype newDatatype = (Datatype)this.functionArguments.get(i);
                  newDatatype.toSqliteString();
                  newFunctionArguments.add(newDatatype);
               }

               if (this.functionArguments.get(i) instanceof String) {
                  newFunctionArguments.add((String)this.functionArguments.get(i));
               }

               if (this.functionArguments.get(i) instanceof Integer) {
                  newFunctionArguments.add((Integer)this.functionArguments.get(i));
               }

               if (this.functionArguments.get(i) instanceof Double) {
                  newFunctionArguments.add((Double)this.functionArguments.get(i));
               }
            }

            newFunctioncall.setFunctionArguments(newFunctionArguments);
         }

         newFunctioncall.setFunctionName(this.functionName);
         newFunctioncall.setForLength(this.forStringInFunction);
         newFunctioncall.setLengthString(this.lengthString);
         newFunctioncall.setFunctionName(this.functionName);
         newFunctioncall.setForLength(this.forStringInFunction);
         newFunctioncall.setLengthString(this.lengthString);
         newFunctioncall.setDateArithmetic(this.dateArithmetic);
         newFunctioncall.setOver(this.over);
         newFunctioncall.setWithinGroup(this.withinGroup);
         if (from_sqs != null && (this.obs != null || this.partitionByClause != null)) {
            from_sqs.setOlapFunctionPresent(true);
         }

         if (this.partitionByClause != null) {
            newFunctioncall.setPartitionByClause(this.partitionByClause.toSqliteSelect(to_sqs, from_sqs));
         }

         if (this.windowClause != null) {
            newFunctioncall.setWindowingClause(this.windowClause.toSqlite(to_sqs, from_sqs));
         }

         if ((this.partitionByClause != null || this.windowClause != null) && this.obs != null) {
            newFunctioncall.setOrderBy(this.obs.toSqliteSelect(to_sqs, from_sqs));
         } else if (from_sqs != null) {
            if (this.functionName != null && this.functionName.getColumnName() != null && this.functionName.getColumnName().equalsIgnoreCase("GROUP_CONCAT") && this.obs != null) {
               functioncall.obs = this.obs.toSqliteSelect(to_sqs, from_sqs);
            } else if (from_sqs.getOrderByStatement() != null) {
               if (this.obs != null) {
                  from_sqs.getOrderByStatement().addOrderItems(this.obs.getOrderItemList());
                  this.obs.setOrderItemList((Vector)null);
                  this.obs.setOrderClause((String)null);
               }
            } else {
               from_sqs.setOrderByStatement(this.obs);
            }
         }

         return newFunctioncall;
      } else {
         if (this.argumentQualifier != null) {
            if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
               functioncall.setArgumentQualifier("DISTINCT");
            } else {
               functioncall.setArgumentQualifier(this.argumentQualifier);
            }
         }

         functioncall.setTrailingString(this.trailingString);
         functioncall.setAsDatatype(this.getAsDatatype());
         functioncall.setFromInTrim(this.fromStringInFunction);
         functioncall.setFunctionArguments(this.functionArguments);
         this.functionName.setIsFunctionName(true);
         functioncall.setFunctionName(this.functionName.toSqliteSelect(to_sqs, from_sqs));
         functioncall.setForLength(this.forStringInFunction);
         functioncall.setLengthString(this.lengthString);
         functioncall.setDateArithmetic(this.dateArithmetic);
         functioncall.setSeparatorString(this.separatorString);
         functioncall.setOver(this.over);
         functioncall.setWithinGroup(this.withinGroup);
         if (from_sqs != null && (this.obs != null || this.partitionByClause != null)) {
            from_sqs.setOlapFunctionPresent(true);
         }

         if (this.partitionByClause != null) {
            functioncall.setPartitionByClause(this.partitionByClause.toSqliteSelect(to_sqs, from_sqs));
         }

         if (this.windowClause != null) {
            functioncall.setWindowingClause(this.windowClause.toSqlite(to_sqs, from_sqs));
         }

         if ((this.partitionByClause != null || this.windowClause != null) && this.obs != null) {
            functioncall.setOrderBy(this.obs.toSqliteSelect(to_sqs, from_sqs));
         } else if (from_sqs != null) {
            if (this.functionName != null && this.functionName.getColumnName() != null && this.functionName.getColumnName().equalsIgnoreCase("GROUP_CONCAT") && this.obs != null) {
               functioncall.obs = this.obs.toSqliteSelect(to_sqs, from_sqs);
            } else if (from_sqs.getOrderByStatement() != null) {
               if (this.obs != null) {
                  from_sqs.getOrderByStatement().addOrderItems(this.obs.getOrderItemList());
                  this.obs.setOrderItemList((Vector)null);
                  this.obs.setOrderClause((String)null);
               }
            } else {
               from_sqs.setOrderByStatement(this.obs);
            }
         }

         functioncall.toSqlite(to_sqs, from_sqs);
         functioncall.setCommentClass(this.commentObj);
         return functioncall;
      }
   }

   public FunctionCalls toExcelSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      FunctionCalls functioncall = getClassName(this.functionName.getColumnName(), 20);
      if (functioncall == null) {
         FunctionCalls newFunctioncall = new FunctionCalls();
         newFunctioncall.setCastToTextInsideIf(this.castToTextInsideIf);
         if (this.argumentQualifier != null) {
            if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
               newFunctioncall.setArgumentQualifier("DISTINCT");
            } else {
               newFunctioncall.setArgumentQualifier(this.argumentQualifier);
            }
         }

         newFunctioncall.setTrailingString(this.trailingString);
         newFunctioncall.setAsDatatype(this.getAsDatatype());
         newFunctioncall.setFromInTrim(this.fromStringInFunction);
         newFunctioncall.setDateArithmetic(this.dateArithmetic);
         if (this.functionArguments != null) {
            Vector newFunctionArguments = new Vector();

            for(int i = 0; i < this.functionArguments.size(); ++i) {
               if (this.functionArguments.get(i) instanceof SelectColumn) {
                  newFunctionArguments.add(((SelectColumn)this.functionArguments.get(i)).toExcelSelect(to_sqs, from_sqs));
               }

               if (this.functionArguments.get(i) instanceof Datatype) {
                  Datatype newDatatype = (Datatype)this.functionArguments.get(i);
                  newDatatype.toExcelString();
                  newFunctionArguments.add(newDatatype);
               }

               if (this.functionArguments.get(i) instanceof String) {
                  newFunctionArguments.add((String)this.functionArguments.get(i));
               }

               if (this.functionArguments.get(i) instanceof Integer) {
                  newFunctionArguments.add((Integer)this.functionArguments.get(i));
               }

               if (this.functionArguments.get(i) instanceof Double) {
                  newFunctionArguments.add((Double)this.functionArguments.get(i));
               }
            }

            if (this.functionArguments.size() < 1) {
               newFunctioncall.setOpenBracesForFunctionNameRequired(false);
            }

            newFunctioncall.setFunctionArguments(newFunctionArguments);
         }

         newFunctioncall.setFunctionName(this.functionName);
         newFunctioncall.setForLength(this.forStringInFunction);
         newFunctioncall.setLengthString(this.lengthString);
         newFunctioncall.setFunctionName(this.functionName);
         newFunctioncall.setForLength(this.forStringInFunction);
         newFunctioncall.setLengthString(this.lengthString);
         newFunctioncall.setDateArithmetic(this.dateArithmetic);
         newFunctioncall.setOver(this.over);
         newFunctioncall.setWithinGroup(this.withinGroup);
         if (from_sqs != null && (this.obs != null || this.partitionByClause != null)) {
            from_sqs.setOlapFunctionPresent(true);
         }

         if (this.partitionByClause != null) {
            newFunctioncall.setPartitionByClause(this.partitionByClause.toExcelSelect(to_sqs, from_sqs));
         }

         if (this.windowClause != null) {
            newFunctioncall.setWindowingClause(this.windowClause.toExcel(to_sqs, from_sqs));
         }

         if ((this.partitionByClause != null || this.windowClause != null) && this.obs != null) {
            newFunctioncall.setOrderBy(this.obs.toExcelSelect(to_sqs, from_sqs));
         } else if (from_sqs != null) {
            if (this.functionName != null && this.functionName.getColumnName() != null && this.functionName.getColumnName().equalsIgnoreCase("GROUP_CONCAT") && this.obs != null) {
               functioncall.obs = this.obs.toExcelSelect(to_sqs, from_sqs);
            } else if (from_sqs.getOrderByStatement() != null) {
               if (this.obs != null) {
                  from_sqs.getOrderByStatement().addOrderItems(this.obs.getOrderItemList());
                  this.obs.setOrderItemList((Vector)null);
                  this.obs.setOrderClause((String)null);
               }
            } else {
               from_sqs.setOrderByStatement(this.obs);
            }
         }

         return newFunctioncall;
      } else {
         if (this.argumentQualifier != null) {
            if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
               functioncall.setArgumentQualifier("DISTINCT");
            } else {
               functioncall.setArgumentQualifier(this.argumentQualifier);
            }
         }

         functioncall.setTrailingString(this.trailingString);
         functioncall.setAsDatatype(this.getAsDatatype());
         functioncall.setFromInTrim(this.fromStringInFunction);
         functioncall.setFunctionArguments(this.functionArguments);
         this.functionName.setIsFunctionName(true);
         functioncall.setFunctionName(this.functionName.toExcelSelect(to_sqs, from_sqs));
         functioncall.setForLength(this.forStringInFunction);
         functioncall.setLengthString(this.lengthString);
         functioncall.setDateArithmetic(this.dateArithmetic);
         functioncall.setSeparatorString(this.separatorString);
         functioncall.setOver(this.over);
         functioncall.setWithinGroup(this.withinGroup);
         if (from_sqs != null && (this.obs != null || this.partitionByClause != null)) {
            from_sqs.setOlapFunctionPresent(true);
         }

         if (this.partitionByClause != null) {
            functioncall.setPartitionByClause(this.partitionByClause.toExcelSelect(to_sqs, from_sqs));
         }

         if (this.windowClause != null) {
            functioncall.setWindowingClause(this.windowClause.toExcel(to_sqs, from_sqs));
         }

         if ((this.partitionByClause != null || this.windowClause != null) && this.obs != null) {
            functioncall.setOrderBy(this.obs.toExcelSelect(to_sqs, from_sqs));
         } else if (from_sqs != null) {
            if (this.functionName != null && this.functionName.getColumnName() != null && this.functionName.getColumnName().equalsIgnoreCase("GROUP_CONCAT") && this.obs != null) {
               functioncall.obs = this.obs.toExcelSelect(to_sqs, from_sqs);
            } else if (from_sqs.getOrderByStatement() != null) {
               if (this.obs != null) {
                  from_sqs.getOrderByStatement().addOrderItems(this.obs.getOrderItemList());
                  this.obs.setOrderItemList((Vector)null);
                  this.obs.setOrderClause((String)null);
               }
            } else {
               from_sqs.setOrderByStatement(this.obs);
            }
         }

         functioncall.toExcel(to_sqs, from_sqs);
         functioncall.setCommentClass(this.commentObj);
         return functioncall;
      }
   }

   public FunctionCalls toMsAccessJdbcSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      FunctionCalls functioncall = getClassName(this.functionName.getColumnName(), 21);
      if (functioncall == null) {
         FunctionCalls newFunctioncall = new FunctionCalls();
         newFunctioncall.setCastToTextInsideIf(this.castToTextInsideIf);
         if (this.argumentQualifier != null) {
            if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
               newFunctioncall.setArgumentQualifier("DISTINCT");
            } else {
               newFunctioncall.setArgumentQualifier(this.argumentQualifier);
            }
         }

         newFunctioncall.setTrailingString(this.trailingString);
         newFunctioncall.setAsDatatype(this.getAsDatatype());
         newFunctioncall.setFromInTrim(this.fromStringInFunction);
         newFunctioncall.setDateArithmetic(this.dateArithmetic);
         if (this.functionArguments != null) {
            Vector newFunctionArguments = new Vector();

            for(int i = 0; i < this.functionArguments.size(); ++i) {
               if (this.functionArguments.get(i) instanceof SelectColumn) {
                  newFunctionArguments.add(((SelectColumn)this.functionArguments.get(i)).toMsAccessJdbcSelect(to_sqs, from_sqs));
               }

               if (this.functionArguments.get(i) instanceof Datatype) {
                  Datatype newDatatype = (Datatype)this.functionArguments.get(i);
                  newDatatype.toMsAccessJdbcString();
                  newFunctionArguments.add(newDatatype);
               }

               if (this.functionArguments.get(i) instanceof String) {
                  newFunctionArguments.add((String)this.functionArguments.get(i));
               }

               if (this.functionArguments.get(i) instanceof Integer) {
                  newFunctionArguments.add((Integer)this.functionArguments.get(i));
               }

               if (this.functionArguments.get(i) instanceof Double) {
                  newFunctionArguments.add((Double)this.functionArguments.get(i));
               }
            }

            newFunctioncall.setFunctionArguments(newFunctionArguments);
         }

         newFunctioncall.setFunctionName(this.functionName);
         newFunctioncall.setForLength(this.forStringInFunction);
         newFunctioncall.setLengthString(this.lengthString);
         newFunctioncall.setFunctionName(this.functionName);
         newFunctioncall.setForLength(this.forStringInFunction);
         newFunctioncall.setLengthString(this.lengthString);
         newFunctioncall.setDateArithmetic(this.dateArithmetic);
         newFunctioncall.setOver(this.over);
         newFunctioncall.setWithinGroup(this.withinGroup);
         if (from_sqs != null && (this.obs != null || this.partitionByClause != null)) {
            from_sqs.setOlapFunctionPresent(true);
         }

         if (this.partitionByClause != null) {
            newFunctioncall.setPartitionByClause(this.partitionByClause.toMsAccessJdbcSelect(to_sqs, from_sqs));
         }

         if (this.windowClause != null) {
            newFunctioncall.setWindowingClause(this.windowClause.toMsAccessJdbc(to_sqs, from_sqs));
         }

         if ((this.partitionByClause != null || this.windowClause != null) && this.obs != null) {
            newFunctioncall.setOrderBy(this.obs.toMsAccessJdbcSelect(to_sqs, from_sqs));
         } else if (from_sqs != null) {
            if (this.functionName != null && this.functionName.getColumnName() != null && this.functionName.getColumnName().equalsIgnoreCase("GROUP_CONCAT") && this.obs != null) {
               functioncall.obs = this.obs.toMsAccessJdbcSelect(to_sqs, from_sqs);
            } else if (from_sqs.getOrderByStatement() != null) {
               if (this.obs != null) {
                  from_sqs.getOrderByStatement().addOrderItems(this.obs.getOrderItemList());
                  this.obs.setOrderItemList((Vector)null);
                  this.obs.setOrderClause((String)null);
               }
            } else {
               from_sqs.setOrderByStatement(this.obs);
            }
         }

         return newFunctioncall;
      } else {
         if (this.argumentQualifier != null) {
            if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
               functioncall.setArgumentQualifier("DISTINCT");
            } else {
               functioncall.setArgumentQualifier(this.argumentQualifier);
            }
         }

         functioncall.setTrailingString(this.trailingString);
         functioncall.setAsDatatype(this.getAsDatatype());
         functioncall.setFromInTrim(this.fromStringInFunction);
         functioncall.setFunctionArguments(this.functionArguments);
         this.functionName.setIsFunctionName(true);
         functioncall.setFunctionName(this.functionName.toMsAccessJdbcSelect(to_sqs, from_sqs));
         functioncall.setForLength(this.forStringInFunction);
         functioncall.setLengthString(this.lengthString);
         functioncall.setDateArithmetic(this.dateArithmetic);
         functioncall.setSeparatorString(this.separatorString);
         functioncall.setOver(this.over);
         functioncall.setWithinGroup(this.withinGroup);
         if (from_sqs != null && (this.obs != null || this.partitionByClause != null)) {
            from_sqs.setOlapFunctionPresent(true);
         }

         if (this.partitionByClause != null) {
            functioncall.setPartitionByClause(this.partitionByClause.toMsAccessJdbcSelect(to_sqs, from_sqs));
         }

         if (this.windowClause != null) {
            functioncall.setWindowingClause(this.windowClause.toMsAccessJdbc(to_sqs, from_sqs));
         }

         if ((this.partitionByClause != null || this.windowClause != null) && this.obs != null) {
            functioncall.setOrderBy(this.obs.toMsAccessJdbcSelect(to_sqs, from_sqs));
         } else if (from_sqs != null) {
            if (this.functionName != null && this.functionName.getColumnName() != null && this.functionName.getColumnName().equalsIgnoreCase("GROUP_CONCAT") && this.obs != null) {
               functioncall.obs = this.obs.toMsAccessJdbcSelect(to_sqs, from_sqs);
            } else if (from_sqs.getOrderByStatement() != null) {
               if (this.obs != null) {
                  from_sqs.getOrderByStatement().addOrderItems(this.obs.getOrderItemList());
                  this.obs.setOrderItemList((Vector)null);
                  this.obs.setOrderClause((String)null);
               }
            } else {
               from_sqs.setOrderByStatement(this.obs);
            }
         }

         functioncall.toMsAccess(to_sqs, from_sqs);
         functioncall.setCommentClass(this.commentObj);
         return functioncall;
      }
   }

   public FunctionCalls toPostgreSQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      FunctionCalls functioncall = getClassName(this.functionName.getColumnName(), 4);
      boolean castToCitext = from_sqs != null && from_sqs.getBooleanValues("can.use.citext.over.text") && StringFunctions.isCastingRequiredForFunction(this.functionName.toString().toUpperCase());
      if (functioncall == null) {
         FunctionCalls newFunctioncall = new FunctionCalls();
         newFunctioncall.setCastToTextInsideIf(this.castToTextInsideIf);
         newFunctioncall.setCastTimeDiffToText(this.castTimeDiffToText());
         if (this.argumentQualifier != null) {
            if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
               newFunctioncall.setArgumentQualifier("DISTINCT");
            } else {
               newFunctioncall.setArgumentQualifier(this.argumentQualifier);
            }
         }

         newFunctioncall.setTrailingString(this.trailingString);
         newFunctioncall.setAsDatatype(this.getAsDatatype());
         newFunctioncall.setFromInTrim(this.fromStringInFunction);
         newFunctioncall.setDateArithmetic(this.dateArithmetic);
         if (this.functionArguments != null) {
            Vector newFunctionArguments = new Vector();
            this.handlePositionFunctionArguments(this.functionName.getColumnName(), this.functionArguments, StringFunctions.getStringDataType(from_sqs));

            for(int i = 0; i < this.functionArguments.size(); ++i) {
               if (this.functionArguments.get(i) instanceof SelectColumn) {
                  newFunctionArguments.add(((SelectColumn)this.functionArguments.get(i)).toPostgreSQLSelect(to_sqs, from_sqs));
               }

               if (this.functionArguments.get(i) instanceof Datatype) {
                  Datatype newDatatype = (Datatype)this.functionArguments.get(i);
                  newDatatype.toPostgreSQLString();
                  newFunctionArguments.add(newDatatype);
               }

               if (this.functionArguments.get(i) instanceof String) {
                  newFunctionArguments.add((String)this.functionArguments.get(i));
               }

               if (this.functionArguments.get(i) instanceof Integer) {
                  newFunctionArguments.add((Integer)this.functionArguments.get(i));
               }

               if (this.functionArguments.get(i) instanceof Double) {
                  newFunctionArguments.add((Double)this.functionArguments.get(i));
               }
            }

            newFunctioncall.setFunctionArguments(newFunctionArguments);
         }

         newFunctioncall.setFunctionName(this.functionName);
         newFunctioncall.setForLength(this.forStringInFunction);
         newFunctioncall.setLengthString(this.lengthString);
         newFunctioncall.setFunctionName(this.functionName);
         newFunctioncall.setForLength(this.forStringInFunction);
         newFunctioncall.setLengthString(this.lengthString);
         newFunctioncall.setDateArithmetic(this.dateArithmetic);
         newFunctioncall.setOver(this.over);
         newFunctioncall.setWithinGroup(this.withinGroup);
         if (from_sqs != null && (this.obs != null || this.partitionByClause != null)) {
            from_sqs.setOlapFunctionPresent(true);
         }

         if (this.partitionByClause != null) {
            newFunctioncall.setPartitionByClause(this.partitionByClause.toPostgreSQLSelect(to_sqs, from_sqs));
         }

         if (this.windowClause != null) {
            newFunctioncall.setWindowingClause(this.windowClause.toPostgreSQL(to_sqs, from_sqs));
         }

         if ((this.partitionByClause != null || this.windowClause != null) && this.obs != null) {
            newFunctioncall.setOrderBy(this.obs.toPostgreSQLSelect(to_sqs, from_sqs));
         } else if (from_sqs != null) {
            if (this.functionName != null && this.functionName.getColumnName() != null && this.functionName.getColumnName().equalsIgnoreCase("GROUP_CONCAT") && this.obs != null) {
               functioncall.obs = this.obs;
            } else if (from_sqs.getOrderByStatement() != null) {
               if (this.obs != null) {
                  from_sqs.getOrderByStatement().addOrderItems(this.obs.getOrderItemList());
                  this.obs.setOrderItemList((Vector)null);
                  this.obs.setOrderClause((String)null);
               }
            } else {
               from_sqs.setOrderByStatement(this.obs);
            }
         }

         return newFunctioncall;
      } else {
         functioncall.setCastToTextInsideIf(this.castToTextInsideIf);
         functioncall.setCastTimeDiffToText(this.castTimeDiffToText());
         if (this.argumentQualifier != null) {
            if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
               functioncall.setArgumentQualifier("DISTINCT");
            } else {
               functioncall.setArgumentQualifier(this.argumentQualifier);
            }
         }

         functioncall.setTrailingString(this.trailingString);
         functioncall.setAsDatatype(this.getAsDatatype());
         functioncall.setFromInTrim(this.fromStringInFunction);
         functioncall.setFunctionArguments(this.functionArguments);
         this.functionName.setIsFunctionName(true);
         functioncall.setFunctionName(this.functionName.toPostgreSQLSelect(to_sqs, from_sqs));
         functioncall.setForLength(this.forStringInFunction);
         functioncall.setLengthString(this.lengthString);
         functioncall.setDateArithmetic(this.dateArithmetic);
         functioncall.setSeparatorString(this.separatorString);
         functioncall.setOver(this.over);
         functioncall.setWithinGroup(this.withinGroup);
         if (from_sqs != null && (this.obs != null || this.partitionByClause != null)) {
            from_sqs.setOlapFunctionPresent(true);
         }

         if (this.partitionByClause != null) {
            functioncall.setPartitionByClause(this.partitionByClause.toPostgreSQLSelect(to_sqs, from_sqs));
         }

         if (this.windowClause != null) {
            functioncall.setWindowingClause(this.windowClause.toPostgreSQL(to_sqs, from_sqs));
         }

         if ((this.partitionByClause != null || this.windowClause != null) && this.obs != null) {
            functioncall.setOrderBy(this.obs.toPostgreSQLSelect(to_sqs, from_sqs));
         } else if (from_sqs != null) {
            if (this.functionName != null && this.functionName.getColumnName() != null && this.functionName.getColumnName().equalsIgnoreCase("GROUP_CONCAT") && this.obs != null) {
               functioncall.obs = this.obs;
            } else if (from_sqs.getOrderByStatement() != null) {
               if (this.obs != null) {
                  from_sqs.getOrderByStatement().addOrderItems(this.obs.getOrderItemList());
                  this.obs.setOrderItemList((Vector)null);
                  this.obs.setOrderClause((String)null);
               }
            } else {
               from_sqs.setOrderByStatement(this.obs);
            }
         }

         functioncall.toPostgreSQL(to_sqs, from_sqs);
         functioncall.setCommentClass(this.commentObj);
         return from_sqs != null && from_sqs.getBooleanValues("can.use.citext.over.text") ? this.castToCitext(castToCitext, functioncall) : functioncall;
      }
   }

   FunctionCalls castToCitext(boolean useCitext, FunctionCalls functionCalls) {
      if (useCitext) {
         FunctionCalls wrapperFunctionCall = new FunctionCalls();
         SelectColumn selectColumn = new SelectColumn();
         Vector selectColumnVector = new Vector();
         TableColumn functionName = new TableColumn();
         functionName.setColumnName("CAST");
         wrapperFunctionCall.setAsDatatype(" AS ");
         selectColumnVector.addElement(functionCalls);
         selectColumn.setColumnExpression(selectColumnVector);
         Vector arguments = new Vector();
         arguments.addElement(selectColumn);
         arguments.addElement("CITEXT");
         wrapperFunctionCall.setFunctionArguments(arguments);
         wrapperFunctionCall.setFunctionName(functionName);
         return wrapperFunctionCall;
      } else {
         return functionCalls;
      }
   }

   public FunctionCalls toMySQLSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      this.validateFunctionArgumentCount(from_sqs);
      if ((this.CaseString == null || this.functionName != null) && !this.decodeConvertedToCaseStatement) {
         FunctionCalls functioncall = getClassName(this.functionName.getColumnName(), from_sqs.getSQLDialect());
         if (functioncall == null) {
            FunctionCalls newFunctioncall = new FunctionCalls();
            if (this.argumentQualifier != null) {
               if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
                  newFunctioncall.setArgumentQualifier("DISTINCT");
               } else {
                  newFunctioncall.setArgumentQualifier(this.argumentQualifier);
               }
            }

            newFunctioncall.setInString(this.getInString());
            newFunctioncall.setSeparatorString(this.getSeparatorString());
            newFunctioncall.setTrailingString(this.trailingString);
            newFunctioncall.setAsDatatype(this.getAsDatatype());
            newFunctioncall.setFromInTrim(this.fromStringInFunction);
            if (this.getSeparatorString() != null) {
               newFunctioncall.setOrderBy(this.obs);
            }

            if (this.functionArguments != null) {
               Vector newFunctionArguments = new Vector();

               for(int i = 0; i < this.functionArguments.size(); ++i) {
                  if (this.functionArguments.get(i) instanceof SelectColumn) {
                     String str = null;
                     SelectColumn sc1 = (SelectColumn)this.functionArguments.get(i);
                     Vector colExp = sc1.getColumnExpression();
                     if (colExp.size() == 1 && colExp.get(0) instanceof TableColumn) {
                        str = ((TableColumn)colExp.get(0)).getColumnName().trim().toLowerCase();
                     }

                     if (!this.functionName.getColumnName().trim().equalsIgnoreCase("GET_FORMAT") || str == null || !str.equalsIgnoreCase("date") && !str.equalsIgnoreCase("time")) {
                        if (from_sqs.isMongoDb() && (this.functionName.getColumnName().trim().equalsIgnoreCase("ADDDATE") || this.functionName.getColumnName().trim().equalsIgnoreCase("SUBDATE")) && colExp.get(0).toString().equalsIgnoreCase("interval")) {
                           String temp = "";
                           int j = 0;

                           while(true) {
                              if (j >= colExp.size()) {
                                 newFunctionArguments.add(temp);
                                 break;
                              }

                              if (j != 1) {
                                 temp = temp + colExp.get(j).toString() + " ";
                              } else {
                                 String removeBracket = colExp.get(j).toString();
                                 removeBracket = removeBracket.charAt(0) == '(' && removeBracket.charAt(removeBracket.length() - 1) == ')' ? removeBracket.substring(1, removeBracket.length() - 1) : removeBracket;
                                 temp = temp + removeBracket + " ";
                              }

                              ++j;
                           }
                        } else {
                           newFunctionArguments.add(((SelectColumn)this.functionArguments.get(i)).toMySQLSelect(to_sqs, from_sqs));
                        }
                     } else {
                        newFunctionArguments.add((SelectColumn)this.functionArguments.get(i));
                     }
                  }

                  if (this.functionArguments.get(i) instanceof Datatype) {
                     Datatype newDatatype = (Datatype)this.functionArguments.get(i);
                     newDatatype.toMySQLString();
                     newFunctionArguments.add(newDatatype);
                  }

                  if (this.functionArguments.get(i) instanceof String) {
                     newFunctionArguments.add((String)this.functionArguments.get(i));
                  }

                  if (this.functionArguments.get(i) instanceof Integer) {
                     newFunctionArguments.add((Integer)this.functionArguments.get(i));
                  }

                  if (this.functionArguments.get(i) instanceof Double) {
                     newFunctionArguments.add((Double)this.functionArguments.get(i));
                  }
               }

               newFunctioncall.setFunctionArguments(newFunctionArguments);
            }

            newFunctioncall.setFunctionName(this.functionName);
            newFunctioncall.setForLength(this.forStringInFunction);
            newFunctioncall.setLengthString(this.lengthString);
            newFunctioncall.setDateArithmetic(this.dateArithmetic);
            newFunctioncall.setOver(this.over);
            newFunctioncall.setWithinGroup(this.withinGroup);
            if (from_sqs != null && (this.obs != null || this.partitionByClause != null)) {
               from_sqs.setOlapFunctionPresent(true);
            }

            if (this.partitionByClause != null) {
               newFunctioncall.setPartitionByClause(this.partitionByClause.toMySQLSelect(to_sqs, from_sqs));
            }

            if (this.windowClause != null) {
               newFunctioncall.setWindowingClause(this.windowClause.toMySQL(to_sqs, from_sqs));
            }

            if ((this.partitionByClause != null || this.windowClause != null) && this.obs != null) {
               newFunctioncall.setOrderBy(this.obs.toMySQLSelect(to_sqs, from_sqs));
            } else {
               if (this.functionName != null && this.functionName.getColumnName() != null && this.functionName.getColumnName().equalsIgnoreCase("GROUP_CONCAT") && this.obs != null) {
                  newFunctioncall.obs = this.obs.toMySQLSelect(to_sqs, from_sqs);
               }

               if (from_sqs != null && this.separatorString == null) {
                  if (from_sqs.getOrderByStatement() != null) {
                     if (this.obs != null) {
                        from_sqs.getOrderByStatement().addOrderItems(this.obs.getOrderItemList());
                        this.obs.setOrderItemList((Vector)null);
                        this.obs.setOrderClause((String)null);
                     }
                  } else {
                     from_sqs.setOrderByStatement(this.obs);
                  }
               }
            }

            return newFunctioncall;
         } else {
            if (this.argumentQualifier != null) {
               if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
                  functioncall.setArgumentQualifier("DISTINCT");
               } else {
                  functioncall.setArgumentQualifier(this.argumentQualifier);
               }
            }

            functioncall.setTrailingString(this.trailingString);
            functioncall.setAsDatatype(this.getAsDatatype());
            functioncall.setFromInTrim(this.fromStringInFunction);
            functioncall.setFunctionArguments(this.functionArguments);
            this.functionName.setIsFunctionName(true);
            functioncall.setFunctionName(this.functionName.toMySQLSelect(to_sqs, from_sqs));
            functioncall.setForLength(this.forStringInFunction);
            functioncall.setLengthString(this.lengthString);
            functioncall.setDateArithmetic(this.dateArithmetic);
            functioncall.setUsing(this.getUsing());
            functioncall.setSeparatorString(this.getSeparatorString());
            functioncall.setInString(this.getInString());
            functioncall.setOver(this.over);
            functioncall.setWithinGroup(this.withinGroup);
            if (from_sqs != null && (this.obs != null || this.partitionByClause != null)) {
               from_sqs.setOlapFunctionPresent(true);
            }

            if (this.partitionByClause != null) {
               functioncall.setPartitionByClause(this.partitionByClause.toMySQLSelect(to_sqs, from_sqs));
            }

            if (this.windowClause != null) {
               functioncall.setWindowingClause(this.windowClause.toMySQL(to_sqs, from_sqs));
            }

            if ((this.partitionByClause != null || this.windowClause != null) && this.obs != null) {
               functioncall.setOrderBy(this.obs.toMySQLSelect(to_sqs, from_sqs));
            } else if (this.functionName != null && this.functionName.getColumnName() != null && this.functionName.getColumnName().equalsIgnoreCase("GROUP_CONCAT") && this.obs != null) {
               functioncall.obs = this.obs.toMySQLSelect(to_sqs, from_sqs);
            } else if (from_sqs != null && this.obs != null) {
               throw new ConvertException("Order by statement in non-window function " + this.functionName.getColumnName(), "ORDERBY_IN_NON_WINDOW", new Object[]{this.functionName.getColumnName().toUpperCase()});
            }

            if (!this.instanceDatatype.equalsIgnoreCase("UNDEFINED")) {
               functioncall.setInstanceDatatype(this.instanceDatatype);
            }

            functioncall.toMySQL(to_sqs, from_sqs);
            functioncall.setCommentClass(this.commentObj);
            TableColumn functionName = functioncall.getFunctionName();
            if (functionName != null && functionName.getColumnName().equalsIgnoreCase("IF") && from_sqs != null && from_sqs.getBooleanValues("long.nested.if.count")) {
               int maxNestedIfCount = from_sqs.getIntegerValues("max.nested.if.count");
               Vector fnargs = functioncall.getFunctionArguments();
               int maxIfcount = GeneralUtil.checkVectorandGetMaxNestedIfCount(fnargs, 4);
               if (maxIfcount + 1 > maxNestedIfCount) {
                  throw new ConvertException("You cannot use nested if with count more than " + maxNestedIfCount + "", "NESTED IF COUNT EXCEEDED", new Object[]{maxNestedIfCount});
               }

               functioncall.setFcNestedIfCount(maxIfcount + 1);
            }

            return functioncall;
         }
      } else {
         return this;
      }
   }

   public FunctionCalls toSnowflakeSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      ArrayList<String> snowflakeFuncList = new ArrayList(Arrays.asList("SUM", "AVG", "NULLIF", "COUNT", "DATE", "DAY", "ABS", "MIN", "MAX", "POWER", "POW", "EXP", "LN", "PI", "FLOOR", "DEGREES", "RADIANS", "SQUARE", "LEFT", "RIGHT", "LPAD", "RPAD", "LTRIM", "RTRIM", "SPACE", "ROUND", "IFNULL", "MEDIAN", "MODE"));
      String trimFnName = this.functionName.getColumnName().trim();
      FunctionCalls functioncall = null;
      if (!snowflakeFuncList.contains(trimFnName.toUpperCase())) {
         functioncall = getClassName(this.functionName.getColumnName(), 15);
      }

      if (functioncall == null) {
         FunctionCalls newFunctioncall = new FunctionCalls();
         newFunctioncall.setCastToTextInsideIf(this.castToTextInsideIf);
         if (this.argumentQualifier != null) {
            if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
               newFunctioncall.setArgumentQualifier("DISTINCT");
            } else {
               newFunctioncall.setArgumentQualifier(this.argumentQualifier);
            }
         }

         newFunctioncall.setTrailingString(this.trailingString);
         newFunctioncall.setAsDatatype(this.getAsDatatype());
         newFunctioncall.setFromInTrim(this.fromStringInFunction);
         newFunctioncall.setDateArithmetic(this.dateArithmetic);
         if (this.functionArguments != null) {
            Vector newFunctionArguments = new Vector();
            this.handlePositionFunctionArguments(this.functionName.getColumnName(), this.functionArguments, "TEXT");

            for(int i = 0; i < this.functionArguments.size(); ++i) {
               if (this.functionArguments.get(i) instanceof SelectColumn) {
                  newFunctionArguments.add(((SelectColumn)this.functionArguments.get(i)).toSnowflakeSelect(to_sqs, from_sqs));
               }

               if (this.functionArguments.get(i) instanceof Datatype) {
                  Datatype newDatatype = (Datatype)this.functionArguments.get(i);
                  newDatatype.toSnowflakeString();
                  newFunctionArguments.add(newDatatype);
               }

               if (this.functionArguments.get(i) instanceof String) {
                  newFunctionArguments.add((String)this.functionArguments.get(i));
               }

               if (this.functionArguments.get(i) instanceof Integer) {
                  newFunctionArguments.add((Integer)this.functionArguments.get(i));
               }

               if (this.functionArguments.get(i) instanceof Double) {
                  newFunctionArguments.add((Double)this.functionArguments.get(i));
               }
            }

            newFunctioncall.setFunctionArguments(newFunctionArguments);
         }

         newFunctioncall.setFunctionName(this.functionName);
         newFunctioncall.setForLength(this.forStringInFunction);
         newFunctioncall.setLengthString(this.lengthString);
         newFunctioncall.setFunctionName(this.functionName);
         newFunctioncall.setForLength(this.forStringInFunction);
         newFunctioncall.setLengthString(this.lengthString);
         newFunctioncall.setDateArithmetic(this.dateArithmetic);
         newFunctioncall.setOver(this.over);
         newFunctioncall.setWithinGroup(this.withinGroup);
         if (from_sqs != null && (this.obs != null || this.partitionByClause != null)) {
            from_sqs.setOlapFunctionPresent(true);
         }

         if (this.partitionByClause != null) {
            newFunctioncall.setPartitionByClause(this.partitionByClause.toSnowflakeSelect(to_sqs, from_sqs));
         }

         if (this.windowClause != null) {
            newFunctioncall.setWindowingClause(this.windowClause.toSnowflake(to_sqs, from_sqs));
         }

         if ((this.partitionByClause != null || this.windowClause != null) && this.obs != null) {
            newFunctioncall.setOrderBy(this.obs.toSnowflakeSelect(to_sqs, from_sqs));
         } else if (from_sqs != null) {
            if (this.functionName != null && this.functionName.getColumnName() != null && this.functionName.getColumnName().equalsIgnoreCase("GROUP_CONCAT") && this.obs != null) {
               functioncall.obs = this.obs;
            } else if (from_sqs.getOrderByStatement() != null) {
               if (this.obs != null) {
                  from_sqs.getOrderByStatement().addOrderItems(this.obs.getOrderItemList());
                  this.obs.setOrderItemList((Vector)null);
                  this.obs.setOrderClause((String)null);
               }
            } else {
               from_sqs.setOrderByStatement(this.obs);
            }
         }

         return newFunctioncall;
      } else {
         functioncall.setCastToTextInsideIf(this.castToTextInsideIf);
         if (this.argumentQualifier != null) {
            if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
               functioncall.setArgumentQualifier("DISTINCT");
            } else {
               functioncall.setArgumentQualifier(this.argumentQualifier);
            }
         }

         functioncall.setTrailingString(this.trailingString);
         functioncall.setAsDatatype(this.getAsDatatype());
         functioncall.setFromInTrim(this.fromStringInFunction);
         functioncall.setFunctionArguments(this.functionArguments);
         this.functionName.setIsFunctionName(true);
         functioncall.setFunctionName(this.functionName.toSnowflakeSelect(to_sqs, from_sqs));
         functioncall.setForLength(this.forStringInFunction);
         functioncall.setLengthString(this.lengthString);
         functioncall.setDateArithmetic(this.dateArithmetic);
         functioncall.setSeparatorString(this.separatorString);
         functioncall.setOver(this.over);
         functioncall.setWithinGroup(this.withinGroup);
         if (from_sqs != null && (this.obs != null || this.partitionByClause != null)) {
            from_sqs.setOlapFunctionPresent(true);
         }

         if (this.partitionByClause != null) {
            functioncall.setPartitionByClause(this.partitionByClause.toSnowflakeSelect(to_sqs, from_sqs));
         }

         if (this.windowClause != null) {
            functioncall.setWindowingClause(this.windowClause.toSnowflake(to_sqs, from_sqs));
         }

         if ((this.partitionByClause != null || this.windowClause != null) && this.obs != null) {
            functioncall.setOrderBy(this.obs.toSnowflakeSelect(to_sqs, from_sqs));
         } else if (from_sqs != null) {
            if (this.functionName != null && this.functionName.getColumnName() != null && this.functionName.getColumnName().equalsIgnoreCase("GROUP_CONCAT") && this.obs != null) {
               functioncall.obs = this.obs;
            } else if (from_sqs.getOrderByStatement() != null) {
               if (this.obs != null) {
                  from_sqs.getOrderByStatement().addOrderItems(this.obs.getOrderItemList());
                  this.obs.setOrderItemList((Vector)null);
                  this.obs.setOrderClause((String)null);
               }
            } else {
               from_sqs.setOrderByStatement(this.obs);
            }
         }

         functioncall.toSnowflake(to_sqs, from_sqs);
         functioncall.setCommentClass(this.commentObj);
         return functioncall;
      }
   }

   public void validateFunctionArgumentCount(SelectQueryStatement from_sqs) throws ConvertException {
      if (from_sqs != null && from_sqs.getBooleanValues("function.arguments.count.mismatch")) {
         if (from_sqs.getValidationHandler() != null && from_sqs.getValidationHandler().getWhiteListedFunctions() != null) {
            Object[] fnDetails = (Object[])((Object[])from_sqs.getValidationHandler().getWhiteListedFunctions().get(this.functionName.getColumnName().toUpperCase()));
            if (fnDetails == null) {
               if (from_sqs.getValidationHandler().getBlackListedFunctions() == null || from_sqs.getValidationHandler().getBlackListedFunctions().contains(this.functionName.getColumnName().toUpperCase())) {
                  throw new ConvertException("UNSUPPORTED_MYSQL_FN " + this.functionName.getColumnName(), "UNSUPPORTED_MYSQL_FN", new Object[]{this.functionName.getColumnName().toUpperCase()});
               }
            } else {
               int min = (Integer)fnDetails[1];
               int max = (Integer)fnDetails[2];
               if (min != -1 && min > this.functionArguments.size() || max != -1 && max < this.functionArguments.size()) {
                  throw new ConvertException("Function Arguments Count Mismatch for " + this.functionName.getColumnName(), "ARGUMENT_COUNT_MISMATCH", new Object[]{this.functionName.getColumnName().toUpperCase()});
               }
            }
         }
      }
   }

   public FunctionCalls toVectorWiseSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if ((this.CaseString == null || this.functionName != null) && !this.decodeConvertedToCaseStatement) {
         FunctionCalls functioncall = getClassName(this.functionName.getColumnName(), 13);
         if (functioncall == null) {
            FunctionCalls newFunctioncall = new FunctionCalls();
            if (this.argumentQualifier != null) {
               if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
                  newFunctioncall.setArgumentQualifier("DISTINCT");
               } else {
                  newFunctioncall.setArgumentQualifier(this.argumentQualifier);
               }
            }

            newFunctioncall.setInString(this.getInString());
            newFunctioncall.setSeparatorString(this.getSeparatorString());
            newFunctioncall.setTrailingString(this.trailingString);
            newFunctioncall.setAsDatatype(this.getAsDatatype());
            newFunctioncall.setFromInTrim(this.fromStringInFunction);
            if (this.getSeparatorString() != null) {
               newFunctioncall.setOrderBy(this.obs);
            }

            if (this.functionArguments != null) {
               Vector newFunctionArguments = new Vector();
               this.handlePositionFunctionArguments(this.functionName.getColumnName(), this.functionArguments, "CHAR");

               for(int i = 0; i < this.functionArguments.size(); ++i) {
                  if (this.functionArguments.get(i) instanceof SelectColumn) {
                     String str = null;
                     SelectColumn sc1 = (SelectColumn)this.functionArguments.get(i);
                     Vector colExp = sc1.getColumnExpression();
                     if (colExp.size() == 1 && colExp.get(0) instanceof TableColumn) {
                        str = ((TableColumn)colExp.get(0)).getColumnName().trim().toLowerCase();
                     }

                     if (!this.functionName.getColumnName().trim().equalsIgnoreCase("GET_FORMAT") || str == null || !str.equalsIgnoreCase("date") && !str.equalsIgnoreCase("time")) {
                        newFunctionArguments.add(((SelectColumn)this.functionArguments.get(i)).toVectorWiseSelect(to_sqs, from_sqs));
                     } else {
                        newFunctionArguments.add((SelectColumn)this.functionArguments.get(i));
                     }
                  }

                  if (this.functionArguments.get(i) instanceof Datatype) {
                     Datatype newDatatype = (Datatype)this.functionArguments.get(i);
                     newDatatype.toMySQLString();
                     newFunctionArguments.add(newDatatype);
                  }

                  if (this.functionArguments.get(i) instanceof String) {
                     newFunctionArguments.add((String)this.functionArguments.get(i));
                  }

                  if (this.functionArguments.get(i) instanceof Integer) {
                     newFunctionArguments.add((Integer)this.functionArguments.get(i));
                  }

                  if (this.functionArguments.get(i) instanceof Double) {
                     newFunctionArguments.add((Double)this.functionArguments.get(i));
                  }
               }

               newFunctioncall.setFunctionArguments(newFunctionArguments);
            }

            newFunctioncall.setFunctionName(this.functionName);
            newFunctioncall.setForLength(this.forStringInFunction);
            newFunctioncall.setLengthString(this.lengthString);
            newFunctioncall.setDateArithmetic(this.dateArithmetic);
            newFunctioncall.setFunctionName(this.functionName);
            newFunctioncall.setForLength(this.forStringInFunction);
            newFunctioncall.setLengthString(this.lengthString);
            newFunctioncall.setDateArithmetic(this.dateArithmetic);
            newFunctioncall.setOver(this.over);
            newFunctioncall.setWithinGroup(this.withinGroup);
            if (from_sqs != null && (this.obs != null || this.partitionByClause != null)) {
               from_sqs.setOlapFunctionPresent(true);
            }

            if (this.partitionByClause != null) {
               newFunctioncall.setPartitionByClause(this.partitionByClause.toVectorWiseSelect(to_sqs, from_sqs));
            }

            if (this.windowClause != null) {
               newFunctioncall.setWindowingClause(this.windowClause.toVectorWise(to_sqs, from_sqs));
            }

            if ((this.partitionByClause != null || this.windowClause != null) && this.obs != null) {
               newFunctioncall.setOrderBy(this.obs.toVectorWiseSelect(to_sqs, from_sqs));
            } else if (from_sqs != null && this.separatorString == null) {
               if (from_sqs.getOrderByStatement() != null) {
                  if (this.obs != null) {
                     from_sqs.getOrderByStatement().addOrderItems(this.obs.getOrderItemList());
                     this.obs.setOrderItemList((Vector)null);
                     this.obs.setOrderClause((String)null);
                  }
               } else {
                  from_sqs.setOrderByStatement(this.obs);
               }
            }

            return newFunctioncall;
         } else {
            if (this.argumentQualifier != null) {
               if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
                  functioncall.setArgumentQualifier("DISTINCT");
               } else {
                  functioncall.setArgumentQualifier(this.argumentQualifier);
               }
            }

            functioncall.setTrailingString(this.trailingString);
            functioncall.setAsDatatype(this.getAsDatatype());
            functioncall.setFromInTrim(this.fromStringInFunction);
            functioncall.setFunctionArguments(this.functionArguments);
            this.functionName.setIsFunctionName(true);
            functioncall.setFunctionName(this.functionName.toVectorWiseSelect(to_sqs, from_sqs));
            functioncall.setForLength(this.forStringInFunction);
            functioncall.setLengthString(this.lengthString);
            functioncall.setDateArithmetic(this.dateArithmetic);
            functioncall.setUsing(this.getUsing());
            functioncall.setSeparatorString(this.getSeparatorString());
            functioncall.setInString(this.getInString());
            functioncall.setOver(this.over);
            functioncall.setWithinGroup(this.withinGroup);
            if (from_sqs != null && (this.obs != null || this.partitionByClause != null)) {
               from_sqs.setOlapFunctionPresent(true);
            }

            if (this.partitionByClause != null) {
               functioncall.setPartitionByClause(this.partitionByClause.toVectorWiseSelect(to_sqs, from_sqs));
            }

            if (this.windowClause != null) {
               functioncall.setWindowingClause(this.windowClause.toVectorWise(to_sqs, from_sqs));
            }

            if ((this.partitionByClause != null || this.windowClause != null) && this.obs != null) {
               functioncall.setOrderBy(this.obs.toVectorWiseSelect(to_sqs, from_sqs));
            } else if (from_sqs != null) {
               if (this.functionName != null && this.functionName.getColumnName() != null && this.functionName.getColumnName().equalsIgnoreCase("GROUP_CONCAT") && this.obs != null) {
                  functioncall.obs = this.obs;
               } else if (from_sqs.getOrderByStatement() != null) {
                  if (this.obs != null) {
                     from_sqs.getOrderByStatement().addOrderItems(this.obs.getOrderItemList());
                     this.obs.setOrderItemList((Vector)null);
                     this.obs.setOrderClause((String)null);
                  }
               } else {
                  from_sqs.setOrderByStatement(this.obs);
               }
            }

            functioncall.toVectorWise(to_sqs, from_sqs);
            return functioncall;
         }
      } else {
         return this;
      }
   }

   public FunctionCalls toInformixSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      String trimFuncName = this.functionName.getColumnName();
      Set<String> informixFuncList = new HashSet(Arrays.asList("ASCII", "GREATEST", "LEAST", "SPACE"));
      FunctionCalls functioncall = null;
      if (!informixFuncList.contains(trimFuncName.toUpperCase())) {
         functioncall = getClassName(trimFuncName, 6);
      }

      if (functioncall == null) {
         FunctionCalls newFunctioncall = new FunctionCalls();
         if (this.argumentQualifier != null) {
            newFunctioncall.setArgumentQualifier(this.argumentQualifier);
         }

         newFunctioncall.setTrailingString(this.trailingString);
         newFunctioncall.setAsDatatype(this.getAsDatatype());
         newFunctioncall.setFromInTrim(this.fromStringInFunction);
         if (this.functionArguments != null) {
            Vector newFunctionArguments = new Vector();

            for(int i = 0; i < this.functionArguments.size(); ++i) {
               if (this.functionArguments.get(i) instanceof SelectColumn) {
                  newFunctionArguments.add(((SelectColumn)this.functionArguments.get(i)).toInformixSelect(to_sqs, from_sqs));
               }

               if (this.functionArguments.get(i) instanceof Datatype) {
                  Datatype newDatatype = (Datatype)this.functionArguments.get(i);
                  newDatatype.toInformixString();
                  newFunctionArguments.add(newDatatype);
               }

               if (this.functionArguments.get(i) instanceof String) {
                  newFunctionArguments.add((String)this.functionArguments.get(i));
               }

               if (this.functionArguments.get(i) instanceof Integer) {
                  newFunctionArguments.add((Integer)this.functionArguments.get(i));
               }

               if (this.functionArguments.get(i) instanceof Double) {
                  newFunctionArguments.add((Double)this.functionArguments.get(i));
               }
            }

            newFunctioncall.setFunctionArguments(newFunctionArguments);
         }

         newFunctioncall.setFunctionName(this.functionName);
         newFunctioncall.setForLength(this.forStringInFunction);
         newFunctioncall.setLengthString(this.lengthString);
         newFunctioncall.setDateArithmetic(this.dateArithmetic);
         newFunctioncall.setOver(this.over);
         newFunctioncall.setWithinGroup(this.withinGroup);
         if (from_sqs != null && (this.obs != null || this.partitionByClause != null)) {
            from_sqs.setOlapFunctionPresent(true);
         }

         if (this.partitionByClause != null) {
            newFunctioncall.setPartitionByClause(this.partitionByClause.toInformixSelect(to_sqs, from_sqs));
         }

         if (this.windowClause != null) {
            newFunctioncall.setWindowingClause(this.windowClause.toInformix(to_sqs, from_sqs));
         }

         if ((this.partitionByClause != null || this.windowClause != null) && this.obs != null) {
            newFunctioncall.setOrderBy(this.obs.toInformixSelect(to_sqs, from_sqs));
         } else if (from_sqs != null) {
            if (this.functionName != null && this.functionName.getColumnName() != null && this.functionName.getColumnName().equalsIgnoreCase("GROUP_CONCAT") && this.obs != null) {
               functioncall.obs = this.obs;
            } else if (from_sqs.getOrderByStatement() != null) {
               if (this.obs != null) {
                  from_sqs.getOrderByStatement().addOrderItems(this.obs.getOrderItemList());
                  this.obs.setOrderItemList((Vector)null);
                  this.obs.setOrderClause((String)null);
               }
            } else {
               from_sqs.setOrderByStatement(this.obs);
            }
         }

         return newFunctioncall;
      } else {
         functioncall.setArgumentQualifier(this.argumentQualifier);
         functioncall.setAsDatatype(this.getAsDatatype());
         functioncall.setTrailingString(this.trailingString);
         functioncall.setFromInTrim(this.fromStringInFunction);
         functioncall.setFunctionArguments(this.functionArguments);
         this.functionName.setIsFunctionName(true);
         functioncall.setFunctionName(this.functionName.toInformixSelect(to_sqs, from_sqs));
         functioncall.setForLength(this.forStringInFunction);
         functioncall.setLengthString(this.lengthString);
         functioncall.setDateArithmetic(this.dateArithmetic);
         functioncall.setSeparatorString(this.separatorString);
         functioncall.setOver(this.over);
         functioncall.setWithinGroup(this.withinGroup);
         if (from_sqs != null && (this.obs != null || this.partitionByClause != null)) {
            from_sqs.setOlapFunctionPresent(true);
         }

         if (this.partitionByClause != null) {
            functioncall.setPartitionByClause(this.partitionByClause.toInformixSelect(to_sqs, from_sqs));
         }

         if (this.windowClause != null) {
            functioncall.setWindowingClause(this.windowClause.toInformix(to_sqs, from_sqs));
         }

         if ((this.partitionByClause != null || this.windowClause != null) && this.obs != null) {
            functioncall.setOrderBy(this.obs.toInformixSelect(to_sqs, from_sqs));
         } else if (from_sqs != null) {
            if (this.functionName != null && this.functionName.getColumnName() != null && this.functionName.getColumnName().equalsIgnoreCase("GROUP_CONCAT") && this.obs != null) {
               functioncall.obs = this.obs;
            } else if (from_sqs.getOrderByStatement() != null) {
               if (this.obs != null) {
                  from_sqs.getOrderByStatement().addOrderItems(this.obs.getOrderItemList());
                  this.obs.setOrderItemList((Vector)null);
                  this.obs.setOrderClause((String)null);
               }
            } else {
               from_sqs.setOrderByStatement(this.obs);
            }
         }

         functioncall.toInformix(to_sqs, from_sqs);
         functioncall.setCommentClass(this.commentObj);
         return functioncall;
      }
   }

   public FunctionCalls toTimesTenSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      if (this.getFunctionName() != null) {
         String func_name = this.getFunctionName().toString();
         if (func_name != null && func_name.equalsIgnoreCase("SOUNDEX")) {
            throw new ConvertException("\nThe Function \"" + func_name + "\" is not supported in TimesTen 5.1.21\n");
         }
      }

      FunctionCalls functioncall = getNewInstance(this.functionName.getColumnName());
      if (functioncall == null) {
         FunctionCalls newFunctioncall = new FunctionCalls();
         if (this.argumentQualifier != null) {
            newFunctioncall.setArgumentQualifier(this.argumentQualifier);
         }

         newFunctioncall.setTrailingString(this.trailingString);
         newFunctioncall.setAsDatatype(this.getAsDatatype());
         newFunctioncall.setFromInTrim(this.fromStringInFunction);
         if (this.functionArguments != null) {
            Vector newFunctionArguments = new Vector();

            for(int i = 0; i < this.functionArguments.size(); ++i) {
               if (this.functionArguments.get(i) instanceof SelectColumn) {
                  newFunctionArguments.add(((SelectColumn)this.functionArguments.get(i)).toTimesTenSelect(to_sqs, from_sqs));
               }

               if (this.functionArguments.get(i) instanceof Datatype) {
                  Datatype newDatatype = (Datatype)this.functionArguments.get(i);
                  newDatatype.toTimesTenString();
                  newFunctionArguments.add(newDatatype);
               }

               if (this.functionArguments.get(i) instanceof String) {
                  newFunctionArguments.add((String)this.functionArguments.get(i));
               }

               if (this.functionArguments.get(i) instanceof Integer) {
                  newFunctionArguments.add((Integer)this.functionArguments.get(i));
               }

               if (this.functionArguments.get(i) instanceof Double) {
                  newFunctionArguments.add((Double)this.functionArguments.get(i));
               }
            }

            newFunctioncall.setFunctionArguments(newFunctionArguments);
         }

         if (this.functionName != null) {
            String tempTableName = this.functionName.getTableName();
            String colName = this.functionName.getColumnName();
            if (this.functionName.getColumnName().startsWith("[") && this.functionName.getColumnName().endsWith("]")) {
               this.functionName.setColumnName(colName.substring(1, colName.length() - 1));
            }

            if (tempTableName == null) {
               String fnName = this.functionName.getColumnName();
               if (SwisSQLOptions.fromSybase) {
                  throw new ConvertException("\nThe Built-in function " + fnName.toUpperCase() + " is not supported in TimesTen 5.1.21\n");
               }
            }

            if (tempTableName != null && (tempTableName.equalsIgnoreCase("dbo") || tempTableName.equalsIgnoreCase("[dbo]"))) {
               this.functionName.setTableName((String)null);
            }
         }

         newFunctioncall.setFunctionName(this.functionName);
         newFunctioncall.setForLength(this.forStringInFunction);
         newFunctioncall.setLengthString(this.lengthString);
         newFunctioncall.setOrderBy(this.obs);
         newFunctioncall.setOver(this.over);
         newFunctioncall.setPartitionByClause(this.partitionByClause);
         newFunctioncall.setObjectContext(this.context);
         newFunctioncall.setDateArithmetic(this.dateArithmetic);
         return newFunctioncall;
      } else {
         functioncall.setArgumentQualifier(this.argumentQualifier);
         functioncall.setAsDatatype(this.getAsDatatype());
         functioncall.setTrailingString(this.trailingString);
         functioncall.setFromInTrim(this.fromStringInFunction);
         functioncall.setFunctionArguments(this.functionArguments);
         this.functionName.setIsFunctionName(true);
         functioncall.setFunctionName(this.functionName.toTimesTenSelect(to_sqs, from_sqs));
         functioncall.setForLength(this.forStringInFunction);
         functioncall.setLengthString(this.lengthString);
         functioncall.setDateArithmetic(this.dateArithmetic);
         functioncall.setOrderBy(this.obs);
         functioncall.setOver(this.over);
         functioncall.setObjectContext(this.context);
         functioncall.toTimesTen(to_sqs, from_sqs);
         return functioncall;
      }
   }

   public FunctionCalls toNetezzaSelect(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      FunctionCalls functioncall = getNewInstance(this.functionName.getColumnName());
      if (functioncall == null) {
         FunctionCalls newFunctioncall = new FunctionCalls();
         if (this.argumentQualifier != null) {
            if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
               newFunctioncall.setArgumentQualifier("DISTINCT");
            } else {
               newFunctioncall.setArgumentQualifier(this.argumentQualifier);
            }
         }

         newFunctioncall.setTrailingString(this.trailingString);
         newFunctioncall.setAsDatatype(this.getAsDatatype());
         newFunctioncall.setFromInTrim(this.fromStringInFunction);
         if (this.functionArguments != null) {
            Vector newFunctionArguments = new Vector();

            for(int i = 0; i < this.functionArguments.size(); ++i) {
               if (this.functionArguments.get(i) instanceof SelectColumn) {
                  newFunctionArguments.add(((SelectColumn)this.functionArguments.get(i)).toNetezzaSelect(to_sqs, from_sqs));
               }

               if (this.functionArguments.get(i) instanceof Datatype) {
                  Datatype newDatatype = (Datatype)this.functionArguments.get(i);
                  newDatatype.toNetezzaString();
                  newFunctionArguments.add(newDatatype);
               }

               if (this.functionArguments.get(i) instanceof String) {
                  newFunctionArguments.add((String)this.functionArguments.get(i));
               }

               if (this.functionArguments.get(i) instanceof Integer) {
                  newFunctionArguments.add((Integer)this.functionArguments.get(i));
               }

               if (this.functionArguments.get(i) instanceof Double) {
                  newFunctionArguments.add((Double)this.functionArguments.get(i));
               }
            }

            newFunctioncall.setFunctionArguments(newFunctionArguments);
         }

         newFunctioncall.setFunctionName(SwisSQLUtils.getMappedFunctionName(SwisSQLAPI.targetDBMappedFunctionNames, this.functionName));
         newFunctioncall.setForLength(this.forStringInFunction);
         newFunctioncall.setLengthString(this.lengthString);
         if (this.obs != null) {
            newFunctioncall.setOrderBy(this.obs.toNetezzaSelect((SelectQueryStatement)null, (SelectQueryStatement)null));
         }

         newFunctioncall.setKeep(this.keep);
         newFunctioncall.setDenseRank(this.denseRank);
         newFunctioncall.setFirst(this.first);
         newFunctioncall.setLast(this.last);
         newFunctioncall.setOver(this.over);
         newFunctioncall.setPartitionByClause(this.partitionByClause);
         newFunctioncall.setWindowingClause(this.windowClause);
         newFunctioncall.setObjectContext(this.context);
         newFunctioncall.setCommentClass(this.commentObj);
         newFunctioncall.setDateArithmetic(this.dateArithmetic);
         return newFunctioncall;
      } else {
         if (this.argumentQualifier != null) {
            if (this.argumentQualifier.equalsIgnoreCase("UNIQUE")) {
               functioncall.setArgumentQualifier("DISTINCT");
            } else {
               functioncall.setArgumentQualifier(this.argumentQualifier);
            }
         }

         functioncall.setTrailingString(this.trailingString);
         functioncall.setAsDatatype(this.getAsDatatype());
         functioncall.setFromInTrim(this.fromStringInFunction);
         functioncall.setFunctionArguments(this.functionArguments);
         this.functionName.setIsFunctionName(true);
         functioncall.setFunctionName(this.functionName.toNetezzaSelect(to_sqs, from_sqs));
         functioncall.setForLength(this.forStringInFunction);
         functioncall.setLengthString(this.lengthString);
         functioncall.setDateArithmetic(this.dateArithmetic);
         functioncall.setKeep(this.keep);
         functioncall.setDenseRank(this.denseRank);
         functioncall.setFirst(this.first);
         functioncall.setLast(this.last);
         if (this.obs != null) {
            functioncall.setOrderBy(this.obs.toNetezzaSelect(to_sqs, from_sqs));
         }

         functioncall.setOver(this.over);
         if (this.partitionByClause != null) {
            functioncall.setPartitionByClause(this.partitionByClause.toNetezzaSelect(to_sqs, from_sqs));
         }

         if (this.windowClause != null) {
            functioncall.setWindowingClause(this.windowClause.toNetezza(to_sqs, from_sqs));
         }

         functioncall.setObjectContext(this.context);
         functioncall.setCommentClass(this.commentObj);
         functioncall.toNetezza(to_sqs, from_sqs);
         functioncall.setFunctionName(SwisSQLUtils.getMappedFunctionName(SwisSQLAPI.targetDBMappedFunctionNames, this.functionName));
         return functioncall;
      }
   }

   public FunctionCalls toReplaceTblCol(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs) throws ConvertException {
      FunctionCalls fnClConv = new FunctionCalls();
      TableColumn fnNameConv = new TableColumn();
      if (from_sqs != null && from_sqs.getQueryConvDataHandler().isReverseParseConv()) {
         this.handleUserDefineFunctions();
      }

      fnNameConv.setColumnName(this.functionName.getColumnName());
      fnNameConv.setCommentClass(this.functionName.getCommentClass());
      fnNameConv.setCommentClassAfterToken(this.functionName.getCommentClassAfterToken());
      fnClConv.setFunctionName(fnNameConv);
      if (this.argumentQualifier != null) {
         fnClConv.setArgumentQualifier(this.argumentQualifier);
      }

      if (this.trailingString != null) {
         fnClConv.setTrailingString(this.trailingString);
      }

      if (this.fromStringInFunction != null) {
         fnClConv.setFromInTrim(this.fromStringInFunction);
      }

      if (this.forStringInFunction != null) {
         fnClConv.setForLength(this.forStringInFunction);
      }

      if (this.lengthString != null) {
         fnClConv.setLengthString(this.lengthString);
      }

      if (this.functionArguments != null) {
         Vector fnArgsConv = new Vector();

         for(int i = 0; i < this.functionArguments.size(); ++i) {
            if (this.functionArguments.get(i) instanceof SelectColumn) {
               SelectColumn selCol = (SelectColumn)this.functionArguments.get(i);
               String colName = selCol.toString().trim();
               if (from_sqs.getQueryConvDataHandler() != null && from_sqs.getQueryConvDataHandler().getAllowedKeyWords().contains(colName.toLowerCase())) {
                  if (!from_sqs.getQueryConvDataHandler().getFunctionListWithAllowedKeyWords().contains(this.functionName.getColumnName().toLowerCase())) {
                     throw new ConvertException("Invalid Column", "INVALID_COLUMN_IN_SELECT", new Object[]{colName});
                  }

                  fnArgsConv.addElement(this.functionArguments.get(i));
               } else {
                  fnArgsConv.addElement(selCol.toReplaceTblCol(to_sqs, from_sqs));
               }
            } else {
               fnArgsConv.addElement(this.functionArguments.get(i));
            }
         }

         fnClConv.setFunctionArguments(fnArgsConv);
      }

      if (this.asDatatype != null) {
         fnClConv.setAsDatatype(this.asDatatype);
      }

      if (this.using != null) {
         fnClConv.setUsing(this.using);
      }

      if (this.inString != null) {
         fnClConv.setInString(this.inString);
      }

      if (this.separatorString != null) {
         fnClConv.setSeparatorString(this.separatorString);
      }

      if (this.over != null) {
         fnClConv.setOver(this.over);
      }

      if (this.obs != null) {
         for(int i = 0; i < this.obs.getOrderItemList().size(); ++i) {
            OrderItem oi = (OrderItem)this.obs.getOrderItemList().get(i);
            oi.setIsFromSelectStatement(true);
         }

         fnClConv.setOrderBy(this.obs.toReplaceTblCol(to_sqs, from_sqs));
      }

      if (this.partitionBy != null) {
         fnClConv.setPartitionBy(this.partitionBy);
      }

      if (this.partitionByClause != null) {
         fnClConv.setPartitionByClause(this.partitionByClause.toReplaceTblCol(to_sqs, from_sqs));
      }

      if (this.commentObj != null) {
         fnClConv.setCommentClass(this.commentObj);
      }

      if (this.windowClause != null) {
         fnClConv.setWindowingClause(this.windowClause.toReplaceTblCol(to_sqs, from_sqs));
      }

      if (this.dateArithmetic != null) {
         fnClConv.setDateArithmetic(this.dateArithmetic);
      }

      if (this.keep != null) {
         fnClConv.setKeep(this.keep);
      }

      if (this.denseRank != null) {
         fnClConv.setDenseRank(this.denseRank);
      }

      if (this.last != null) {
         fnClConv.setLast(this.last);
      }

      if (this.first != null) {
         fnClConv.setFirst(this.first);
      }

      if (this.atTimeZoneRegion != null) {
         fnClConv.setAtTimeZoneRegion(this.atTimeZoneRegion.toReplaceTblCol(to_sqs, from_sqs));
      }

      if (this.withinGroup != null) {
         fnClConv.setWithinGroup(this.withinGroup);
      }

      if (this.usingClause != null) {
         fnClConv.setUsingClause(this.usingClause);
      }

      return fnClConv;
   }

   public void handleUserDefineFunctions() throws ConvertException {
      String fnName = this.functionName.getColumnName().toUpperCase();
      if (SwisSQLAPI.udfFunctionMap.keySet().contains(fnName)) {
         this.functionName.setColumnName((String)SwisSQLAPI.udfFunctionMap.get(fnName));
         if (!fnName.equalsIgnoreCase("ZR_ISLASTMONTH") && !fnName.equalsIgnoreCase("ZR_ISLASTQUARTER") && !fnName.equalsIgnoreCase("ZR_ISNEXTMONTH") && !fnName.equalsIgnoreCase("ZR_ISNEXTQUARTER") && !fnName.equalsIgnoreCase("ZR_ISPREVIOUSMONTH") && !fnName.equalsIgnoreCase("ZR_ISPREVIOUSQUARTER")) {
            if (!fnName.equalsIgnoreCase("ZR_ISPREVIOUSWEEK") && !fnName.equalsIgnoreCase("ZR_ISCURRENTWEEK") && !fnName.equalsIgnoreCase("ZR_ISNEXTWEEK")) {
               if (!fnName.equalsIgnoreCase("ZR_FYEARDT") && !fnName.equalsIgnoreCase("ZR_FQUARTERDT")) {
                  if (!fnName.equalsIgnoreCase("ZR_TEXTBETWEEN")) {
                     if (!fnName.equalsIgnoreCase("ZR_SECTOTIME")) {
                        throw new ConvertException("BusinessDay UDF Function is present");
                     }

                     this.functionArguments.set(0, ((FunctionCalls)((FunctionCalls)((SelectColumn)((SelectColumn)((SelectColumn)this.functionArguments.get(0)).getColumnExpression().get(0))).getColumnExpression().get(0))).getFunctionArguments().get(0));
                     this.functionArguments.removeElementAt(2);
                     this.functionArguments.removeElementAt(1);
                  }
               } else {
                  this.functionArguments.set(1, ((SelectColumn)((SelectColumn)((SelectColumn)this.functionArguments.get(1)).getColumnExpression().get(0))).getColumnExpression().get(2));
               }
            } else {
               this.functionArguments.removeElementAt(2);
               this.functionArguments.set(1, Integer.toString(Integer.parseInt(this.functionArguments.get(1).toString()) - 1));
            }
         } else {
            this.functionArguments.removeElementAt(2);
         }
      }

   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      if (this.commentObj != null) {
         sb.append(this.commentObj.toString().trim() + " ");
      }

      if (this.toDateString != null) {
         sb.append("(");
      }

      if (this.override_to_string != null) {
         sb.append(this.override_to_string.toString(this));
      } else {
         if (this.functionName != null) {
            sb.append(this.functionName.toString());
            if (this.openBracesForFunctionNameRequired) {
               sb.append("(");
            }
         }

         if (this.argumentQualifier != null) {
            if (this.argumentQualifier.indexOf("'") == -1) {
               sb.append(this.argumentQualifier.toUpperCase() + " ");
            } else {
               sb.append(this.argumentQualifier + " ");
            }
         }

         if (this.trailingString != null) {
            sb.append(this.trailingString + " ");
         }

         if (this.fromStringInFunction != null) {
            sb.append(this.fromStringInFunction.toUpperCase() + " ");
         }

         boolean asDatatypeAdded = false;
         int i_count;
         String temp;
         if (this.lengthString != null) {
            for(i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
               if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                  ((SelectColumn)this.functionArguments.elementAt(i_count)).setObjectContext(this.context);
               } else if (this.functionArguments.elementAt(i_count) instanceof TableColumn) {
                  ((TableColumn)this.functionArguments.elementAt(i_count)).setObjectContext(this.context);
               } else if (this.functionArguments.elementAt(i_count) instanceof FunctionCalls) {
                  ((FunctionCalls)this.functionArguments.elementAt(i_count)).setObjectContext(this.context);
               }

               if (this.context != null && this.functionArguments.elementAt(i_count) instanceof String) {
                  temp = this.context.getEquivalent(this.functionArguments.elementAt(i_count)).toString();
                  sb.append(temp + "");
               } else {
                  sb.append(this.functionArguments.elementAt(i_count).toString() + "");
               }
            }

            sb.append(" " + this.forStringInFunction + " ");
            sb.append(this.lengthString);
         } else {
            for(i_count = 0; i_count < this.functionArguments.size(); ++i_count) {
               if (this.functionArguments.elementAt(i_count) instanceof SelectColumn) {
                  ((SelectColumn)this.functionArguments.elementAt(i_count)).setObjectContext(this.context);
               } else if (this.functionArguments.elementAt(i_count) instanceof TableColumn) {
                  ((TableColumn)this.functionArguments.elementAt(i_count)).setObjectContext(this.context);
               } else if (this.functionArguments.elementAt(i_count) instanceof FunctionCalls) {
                  ((FunctionCalls)this.functionArguments.elementAt(i_count)).setObjectContext(this.context);
               }

               if (i_count == this.functionArguments.size() - 1) {
                  if (!asDatatypeAdded && this.asDatatype != null) {
                     sb.append(this.asDatatype + " ");
                     asDatatypeAdded = true;
                  }

                  if (this.context != null && this.functionArguments.elementAt(i_count) instanceof String) {
                     temp = this.context.getEquivalent(this.functionArguments.elementAt(i_count)).toString().trim();
                     sb.append(temp);
                  } else {
                     sb.append(this.functionArguments.elementAt(i_count).toString().trim());
                  }
               } else if (this.asDatatype != null) {
                  if (!asDatatypeAdded && this.functionArguments.elementAt(i_count) instanceof Datatype) {
                     sb.append(" " + this.asDatatype + " " + this.functionArguments.elementAt(i_count).toString() + " ");
                     asDatatypeAdded = true;
                  } else {
                     sb.append(this.functionArguments.elementAt(i_count).toString() + " ");
                  }
               } else if (this.using != null) {
                  sb.append(this.functionArguments.elementAt(i_count).toString() + " " + this.using + " ");
               } else if (this.inString != null) {
                  sb.append(this.functionArguments.elementAt(i_count).toString() + " " + this.inString + " ");
               } else if (this.separatorString != null) {
                  sb.append(this.functionArguments.elementAt(i_count).toString() + " ");
                  if (this.obs != null) {
                     sb.append(this.obs.toString() + " ");
                  }

                  sb.append(this.separatorString + " ");
               } else if (this.context != null && this.functionArguments.elementAt(i_count) instanceof String) {
                  temp = this.context.getEquivalent(this.functionArguments.elementAt(i_count)).toString().trim();
                  if (!this.stripComma) {
                     sb.append(temp + ",");
                  }

                  sb.append(" ");
               } else {
                  sb.append(this.functionArguments.elementAt(i_count).toString().trim());
                  if (!this.stripComma) {
                     sb.append(",");
                  }

                  sb.append(" ");
               }
            }
         }

         if (this.collateClause != null) {
            sb.append(" ").append(this.collateClause).append(" ").append(this.collationName);
         }

         if (this.openBracesForFunctionNameRequired) {
            sb.append(")");
         }

         if (this.getAdventNetMessageString() != null) {
            sb.append("/*" + this.getAdventNetMessageString() + "*/");
         }

         if (this.toDateString != null) {
            sb.append(" + ( " + this.toDateString + " * ");
         }

         if (this.toDateSymbolValue != null) {
            sb.append(this.toDateSymbolValue + " ) ");
         }

         if (this.dateArithmetic != null) {
            sb.append(this.dateArithmetic);
         }

         if (this.keep != null) {
            sb.append(" " + this.keep + " ");
            if (this.denseRank != null) {
               sb.append("( " + this.denseRank);
            }

            if (this.last != null) {
               sb.append(" " + this.last + " ");
            }

            if (this.first != null) {
               sb.append(" " + this.first + " ");
            }

            if (this.obs != null) {
               sb.append(this.obs.toString() + " )");
            }
         }

         if (this.over != null && this.keep == null && this.separatorString == null) {
            sb.append(" " + this.over);
            sb.append("(");
            if (this.partitionByClause != null) {
               sb.append(this.partitionByClause.toString() + " ");
            }

            if (this.obs != null) {
               sb.append(this.obs.toString() + " ");
            }

            if (this.windowClause != null) {
               sb.append(this.windowClause.toString());
            }

            sb.append(")");
         }

         if (this.divisionBy31 != null) {
            sb.append(" " + this.divisionBy31);
         }
      }

      if (this.toDateString != null) {
         sb.append(")");
      }

      if (this.atTimeZoneRegion != null) {
         sb.append(" AT TIME ZONE " + this.atTimeZoneRegion);
      }

      return this.wrapper != null ? this.wrapper + "(" + sb.toString() + " , '')" : sb.toString();
   }

   public static FunctionCalls getNewInstance(String functionName) {
      return getClassName(functionName, -1);
   }

   private static FunctionCalls getClassName(String fname, int dbType) {
      String trimFnName = fname.trim();
      boolean isVectorWise = false;
      boolean isPostgreSql = false;
      boolean isMsAzure = false;
      boolean isMySql = false;
      boolean isOracle = false;
      boolean isBigQuery = false;
      boolean isSnowflake = false;
      boolean isDB2 = false;
      boolean isAthena = false;
      boolean isSapHana = false;
      boolean isSqlite = false;
      boolean isInformix = false;
      boolean isHyperSql = false;
      boolean isExcel = false;
      boolean isMsAccessJdbc = false;
      switch(dbType) {
      case 1:
         isOracle = true;
         break;
      case 2:
         isMsAzure = true;
         break;
      case 3:
         isDB2 = true;
         break;
      case 4:
         isPostgreSql = true;
         break;
      case 5:
         isMySql = true;
         break;
      case 6:
         isInformix = true;
      case 7:
      case 8:
      case 9:
      case 10:
      case 11:
      case 12:
      default:
         break;
      case 13:
         isVectorWise = true;
         break;
      case 14:
         isBigQuery = true;
         break;
      case 15:
         isSnowflake = true;
         break;
      case 16:
         isAthena = true;
         break;
      case 17:
         isSapHana = true;
         break;
      case 18:
         isSqlite = true;
         break;
      case 19:
         isHyperSql = true;
         isMySql = true;
         break;
      case 20:
         isExcel = true;
         break;
      case 21:
         isMsAccessJdbc = true;
      }

      if (isHyperSql) {
         label3887: {
            if (trimFnName.equalsIgnoreCase("adddate") || trimFnName.equalsIgnoreCase("subdate")) {
               return new dateadd();
            }

            if (!trimFnName.equalsIgnoreCase("current_date") && !trimFnName.equalsIgnoreCase("current_timestamp") && !trimFnName.equalsIgnoreCase("current_time")) {
               if (!trimFnName.equalsIgnoreCase("yearweek") && !trimFnName.equalsIgnoreCase("week")) {
                  if (trimFnName.equalsIgnoreCase("utc_timestamp")) {
                     return new utcTimestamp();
                  }

                  if (trimFnName.equalsIgnoreCase("microsecond") || trimFnName.equalsIgnoreCase("second") || trimFnName.equalsIgnoreCase("dayofmonth") || trimFnName.equalsIgnoreCase("dayofyear") || trimFnName.equalsIgnoreCase("weekday")) {
                     return new extract();
                  }

                  if (trimFnName.equalsIgnoreCase("from_unixtime")) {
                     return new dateformat();
                  }

                  if (trimFnName.equalsIgnoreCase("makedate")) {
                     return new MakeDate();
                  }

                  if (trimFnName.equalsIgnoreCase("substring_index")) {
                     return new substringIndex();
                  }

                  if (trimFnName.equalsIgnoreCase("time_to_sec")) {
                     return new TimeToSec();
                  }

                  if (trimFnName.equalsIgnoreCase("log2")) {
                     return new log();
                  }
                  break label3887;
               }

               return new week();
            }

            return new getdate();
         }
      }

      if (isMySql) {
         label3888: {
            if (trimFnName.equalsIgnoreCase("if_null")) {
               return new ifnull();
            }

            if (trimFnName.equalsIgnoreCase("convert_base") || trimFnName.equalsIgnoreCase("convertbase")) {
               return new conv();
            }

            if (trimFnName.equalsIgnoreCase("insert_string")) {
               return new insert();
            }

            if (!trimFnName.equalsIgnoreCase("string_compare") && !trimFnName.equalsIgnoreCase("strcmp")) {
               if (!trimFnName.equalsIgnoreCase("add_time") && !trimFnName.equalsIgnoreCase("addtime")) {
                  if (trimFnName.equalsIgnoreCase("date_diff")) {
                     return new datediff();
                  }

                  if (trimFnName.equalsIgnoreCase("day_name")) {
                     return new extract();
                  }

                  if (trimFnName.equalsIgnoreCase("day_of_month")) {
                     return new extract();
                  }

                  if (trimFnName.equalsIgnoreCase("day_of_year")) {
                     return new extract();
                  }

                  if (trimFnName.equalsIgnoreCase("make_date")) {
                     return new MakeDate();
                  }

                  if (trimFnName.equalsIgnoreCase("micro_second")) {
                     return new extract();
                  }

                  if (trimFnName.equalsIgnoreCase("sub_date")) {
                     return new extract();
                  }

                  if (!trimFnName.equalsIgnoreCase("sub_time") && !trimFnName.equalsIgnoreCase("subtime")) {
                     if (trimFnName.equalsIgnoreCase("week_day")) {
                        return new extract();
                     }

                     if (trimFnName.equalsIgnoreCase("year_week")) {
                        return new extract();
                     }

                     if (trimFnName.equalsIgnoreCase("convert_tz")) {
                        return new convertTz();
                     }

                     if (trimFnName.equalsIgnoreCase("time_format")) {
                        return new dateformat();
                     }

                     if (trimFnName.equalsIgnoreCase("str_to_date")) {
                        return new StrToDate();
                     }
                     break label3888;
                  }

                  return new extract();
               }

               return new extract();
            }

            return new strcmp();
         }
      }

      if (isOracle) {
         if (trimFnName.equalsIgnoreCase("quarter")) {
            return new quarter();
         }

         if (trimFnName.equalsIgnoreCase("adddate") || trimFnName.equalsIgnoreCase("addtime") || trimFnName.equalsIgnoreCase("subdate") || trimFnName.equalsIgnoreCase("subtime") || trimFnName.equalsIgnoreCase("date_add") || trimFnName.equalsIgnoreCase("date_sub")) {
            return new addToDate();
         }

         if (trimFnName.equalsIgnoreCase("DAYOFYEAR") || trimFnName.equalsIgnoreCase("minute") || trimFnName.equalsIgnoreCase("dayname") || trimFnName.equalsIgnoreCase("dayofmonth")) {
            return new datepart();
         }

         if (trimFnName.equalsIgnoreCase("makedate")) {
            return new MakeDate();
         }

         if (trimFnName.equalsIgnoreCase("insert")) {
            return new insert();
         }

         if (trimFnName.equalsIgnoreCase("binary")) {
            return new Binary();
         }

         if (trimFnName.equalsIgnoreCase("week") || trimFnName.equalsIgnoreCase("weekofyear") || trimFnName.equalsIgnoreCase("yearweek") || trimFnName.equalsIgnoreCase("convert_tz")) {
            return new week();
         }

         if (trimFnName.equalsIgnoreCase("STR_TO_DATE")) {
            return new StrToDate();
         }

         if (trimFnName.equalsIgnoreCase("current_date")) {
            return new getdate();
         }

         if (trimFnName.equalsIgnoreCase("current_time")) {
            return new gettime();
         }

         if (trimFnName.equalsIgnoreCase("utc_timestamp")) {
            return new getdate();
         }

         if (trimFnName.equalsIgnoreCase("current_timestamp")) {
            return new getdate();
         }

         if (trimFnName.equalsIgnoreCase("timestamp")) {
            return new TimeToSec();
         }

         if (trimFnName.equalsIgnoreCase("microsecond")) {
            return new datepart();
         }

         if (trimFnName.equalsIgnoreCase("mid")) {
            return new substring();
         }

         if (trimFnName.equalsIgnoreCase("time_format")) {
            return new StrToDate();
         }

         if (trimFnName.equalsIgnoreCase("time_to_sec")) {
            return new TimeToSec();
         }

         if (trimFnName.equalsIgnoreCase("from_unixtime")) {
            return new dateformat();
         }

         if (trimFnName.equalsIgnoreCase("substring_index")) {
            return new substringIndex();
         }

         if (trimFnName.equalsIgnoreCase("sec_to_time")) {
            return new SecToTime();
         }

         if (trimFnName.equalsIgnoreCase("unix_timestamp")) {
            return new unixTimestamp();
         }

         if (trimFnName.equalsIgnoreCase("timediff")) {
            return new TimeToSec();
         }

         if (trimFnName.equalsIgnoreCase("extract") || trimFnName.equalsIgnoreCase("weekday")) {
            return new extract();
         }
      }

      if (isSnowflake) {
         if (trimFnName.equalsIgnoreCase("FROM_UNIXTIME")) {
            return new FromTZ();
         }

         if (trimFnName.equalsIgnoreCase("date_add") || trimFnName.equalsIgnoreCase("date_sub") || trimFnName.equalsIgnoreCase("adddate") || trimFnName.equalsIgnoreCase("subdate")) {
            return new dateadd();
         }

         if (trimFnName.equalsIgnoreCase("dayname") || trimFnName.equalsIgnoreCase("weekday") || trimFnName.equalsIgnoreCase("minute") || trimFnName.equalsIgnoreCase("quarter") || trimFnName.equalsIgnoreCase("dayofyear") || trimFnName.equalsIgnoreCase("dayofmonth") || trimFnName.equalsIgnoreCase("Microsecond")) {
            return new extract();
         }

         if (trimFnName.equalsIgnoreCase("STR_TO_DATE")) {
            return new StrToDate();
         }

         if (trimFnName.equalsIgnoreCase("time_format")) {
            return new dateformat();
         }

         if (trimFnName.equalsIgnoreCase("datediff")) {
            return new timestampDiff();
         }

         if (trimFnName.equalsIgnoreCase("second")) {
            return new datepart();
         }

         if (trimFnName.equalsIgnoreCase("ADDTIME") || trimFnName.equalsIgnoreCase("SUBTIME")) {
            return new timeAddSub();
         }

         if (trimFnName.equalsIgnoreCase("time_to_sec") || trimFnName.equalsIgnoreCase("timediff") || trimFnName.equalsIgnoreCase("timestamp")) {
            return new TimeToSec();
         }

         if (trimFnName.equalsIgnoreCase("unix_timestamp")) {
            return new unixTimestamp();
         }

         if (trimFnName.equalsIgnoreCase("utc_timestamp")) {
            return new utcTimestamp();
         }

         if (trimFnName.equalsIgnoreCase("week") || trimFnName.equalsIgnoreCase("weekofyear") || trimFnName.equalsIgnoreCase("yearweek") || trimFnName.equalsIgnoreCase("convert_tz")) {
            return new week();
         }

         if (trimFnName.equalsIgnoreCase("log2")) {
            return new log();
         }

         if (trimFnName.equalsIgnoreCase("mid")) {
            return new substring();
         }

         if (trimFnName.equalsIgnoreCase("strcmp")) {
            return new strcmp();
         }

         if (trimFnName.equalsIgnoreCase("percentile") || trimFnName.equalsIgnoreCase("percentile_cont")) {
            return new percentile();
         }

         if (trimFnName.equalsIgnoreCase("substring_index")) {
            return new substringIndex();
         }
      }

      if (isDB2) {
         label3889: {
            if (!trimFnName.equalsIgnoreCase("date_add") && !trimFnName.equalsIgnoreCase("date_sub") && !trimFnName.equalsIgnoreCase("adddate") && !trimFnName.equalsIgnoreCase("subdate")) {
               if (!trimFnName.equalsIgnoreCase("STR_TO_DATE") && !trimFnName.equalsIgnoreCase("TIME_FORMAT") && !trimFnName.equalsIgnoreCase("FROM_UNIXTIME")) {
                  if (!trimFnName.equalsIgnoreCase("curdate") && !trimFnName.equalsIgnoreCase("currentdate") && !trimFnName.equalsIgnoreCase("current_date")) {
                     if (!trimFnName.equalsIgnoreCase("current_time") && !trimFnName.equalsIgnoreCase("curtime")) {
                        if (trimFnName.equalsIgnoreCase("current_timestamp")) {
                           return new currentTimestamp();
                        }

                        if (trimFnName.equalsIgnoreCase("minute") || trimFnName.equalsIgnoreCase("quarter") || trimFnName.equalsIgnoreCase("dayname") || trimFnName.equalsIgnoreCase("weekday") || trimFnName.equalsIgnoreCase("dayofyear") || trimFnName.equalsIgnoreCase("dayofmonth") || trimFnName.equalsIgnoreCase("Microsecond") || trimFnName.equalsIgnoreCase("second")) {
                           return new extract();
                        }

                        if (trimFnName.equalsIgnoreCase("datediff")) {
                           return new timestampDiff();
                        }

                        if (trimFnName.equalsIgnoreCase("makedate")) {
                           return new MakeDate();
                        }

                        if (trimFnName.equalsIgnoreCase("utc_timestamp")) {
                           return new utcTimestamp();
                        }

                        if (!trimFnName.equalsIgnoreCase("log2") && !trimFnName.equalsIgnoreCase("log10")) {
                           if (trimFnName.equalsIgnoreCase("ifnull")) {
                              return new ifnull();
                           }

                           if (!trimFnName.equalsIgnoreCase("week") && !trimFnName.equalsIgnoreCase("weekofyear") && !trimFnName.equalsIgnoreCase("yearweek")) {
                              if (trimFnName.equalsIgnoreCase("strcmp")) {
                                 return new strcmp();
                              }

                              if (!trimFnName.equalsIgnoreCase("ADDTIME") && !trimFnName.equalsIgnoreCase("SUBTIME")) {
                                 if (trimFnName.equalsIgnoreCase("convert_tz")) {
                                    return new convertTz();
                                 }

                                 if (!trimFnName.equalsIgnoreCase("time_to_sec") && !trimFnName.equalsIgnoreCase("timediff")) {
                                    if (trimFnName.equalsIgnoreCase("mid")) {
                                       return new substring();
                                    }

                                    if (trimFnName.equalsIgnoreCase("substring_index")) {
                                       return new substringIndex();
                                    }
                                    break label3889;
                                 }

                                 return new TimeToSec();
                              }

                              return new timeAddSub();
                           }

                           return new week();
                        }

                        return new log();
                     }

                     return new gettime();
                  }

                  return new getdate();
               }

               return new dateformat();
            }

            return new dateadd();
         }
      }

      if (isAthena) {
         label3890: {
            if (trimFnName.equalsIgnoreCase("STR_TO_DATE") || trimFnName.equalsIgnoreCase("FROM_UNIXTIME") || trimFnName.equalsIgnoreCase("time_format")) {
               return new dateformat();
            }

            if (!trimFnName.equalsIgnoreCase("curdate") && !trimFnName.equalsIgnoreCase("currentdate") && !trimFnName.equalsIgnoreCase("current_date")) {
               if (trimFnName.equalsIgnoreCase("current_timestamp")) {
                  return new currentTimestamp();
               }

               if (trimFnName.equalsIgnoreCase("utc_timestamp")) {
                  return new utcTimestamp();
               }

               if (trimFnName.equalsIgnoreCase("current_time")) {
                  return new gettime();
               }

               if (trimFnName.equalsIgnoreCase("date_add") || trimFnName.equalsIgnoreCase("date_sub") || trimFnName.equalsIgnoreCase("adddate") || trimFnName.equalsIgnoreCase("subdate")) {
                  return new dateadd();
               }

               if (trimFnName.equalsIgnoreCase("ADDTIME") || trimFnName.equalsIgnoreCase("SUBTIME")) {
                  return new timeAddSub();
               }

               if (!trimFnName.equalsIgnoreCase("dayname") && !trimFnName.equalsIgnoreCase("weekday") && !trimFnName.equalsIgnoreCase("minute") && !trimFnName.equalsIgnoreCase("quarter") && !trimFnName.equalsIgnoreCase("dayofyear") && !trimFnName.equalsIgnoreCase("dayofmonth") && !trimFnName.equalsIgnoreCase("Microsecond") && !trimFnName.equalsIgnoreCase("second")) {
                  if (trimFnName.equalsIgnoreCase("log2")) {
                     return new log();
                  }

                  if (trimFnName.equalsIgnoreCase("convert_tz")) {
                     return new convertTz();
                  }

                  if (trimFnName.equalsIgnoreCase("makedate")) {
                     return new MakeDate();
                  }

                  if (!trimFnName.equalsIgnoreCase("week") && !trimFnName.equalsIgnoreCase("weekofyear") && !trimFnName.equalsIgnoreCase("yearweek")) {
                     if (!trimFnName.equalsIgnoreCase("time_to_sec") && !trimFnName.equalsIgnoreCase("timediff") && !trimFnName.equalsIgnoreCase("timestamp")) {
                        if (trimFnName.equalsIgnoreCase("SEC_TO_TIME")) {
                           return new SecToTime();
                        }

                        if (trimFnName.equalsIgnoreCase("insert")) {
                           return new insert();
                        }

                        if (trimFnName.equalsIgnoreCase("unix_timestamp")) {
                           return new unixTimestamp();
                        }

                        if (trimFnName.equalsIgnoreCase("strcmp")) {
                           return new strcmp();
                        }

                        if (trimFnName.equalsIgnoreCase("ifnull")) {
                           return new ifnull();
                        }

                        if (trimFnName.equalsIgnoreCase("substring_index")) {
                           return new substringIndex();
                        }

                        if (trimFnName.equalsIgnoreCase("mid")) {
                           return new substring();
                        }
                        break label3890;
                     }

                     return new TimeToSec();
                  }

                  return new week();
               }

               return new extract();
            }

            return new getdate();
         }
      }

      if (isSapHana) {
         label3891: {
            if (!trimFnName.equalsIgnoreCase("date_add") && !trimFnName.equalsIgnoreCase("date_sub") && !trimFnName.equalsIgnoreCase("adddate") && !trimFnName.equalsIgnoreCase("subdate")) {
               if (trimFnName.equalsIgnoreCase("week") || trimFnName.equalsIgnoreCase("weekofyear") || trimFnName.equalsIgnoreCase("yearweek")) {
                  return new week();
               }

               if (trimFnName.equalsIgnoreCase("concat")) {
                  return new concat();
               }

               if (!trimFnName.equalsIgnoreCase("ADDTIME") && !trimFnName.equalsIgnoreCase("SUBTIME")) {
                  if (trimFnName.equalsIgnoreCase("STR_TO_DATE")) {
                     return new StrToDate();
                  }

                  if (!trimFnName.equalsIgnoreCase("curdate") && !trimFnName.equalsIgnoreCase("currentdate") && !trimFnName.equalsIgnoreCase("current_date")) {
                     if (trimFnName.equalsIgnoreCase("current_time")) {
                        return new gettime();
                     }

                     if (trimFnName.equalsIgnoreCase("current_timestamp")) {
                        return new currentTimestamp();
                     }

                     if (trimFnName.equalsIgnoreCase("date_diff")) {
                        return new datediff();
                     }

                     if (!trimFnName.equalsIgnoreCase("dayname") && !trimFnName.equalsIgnoreCase("weekday") && !trimFnName.equalsIgnoreCase("minute") && !trimFnName.equalsIgnoreCase("quarter") && !trimFnName.equalsIgnoreCase("dayofyear") && !trimFnName.equalsIgnoreCase("dayofmonth") && !trimFnName.equalsIgnoreCase("Microsecond") && !trimFnName.equalsIgnoreCase("second")) {
                        if (trimFnName.equalsIgnoreCase("FROM_UNIXTIME")) {
                           return new FromTZ();
                        }

                        if (trimFnName.equalsIgnoreCase("makedate")) {
                           return new MakeDate();
                        }

                        if (!trimFnName.equalsIgnoreCase("time_to_sec") && !trimFnName.equalsIgnoreCase("timediff") && !trimFnName.equalsIgnoreCase("timestamp")) {
                           if (trimFnName.equalsIgnoreCase("unix_timestamp")) {
                              return new unixTimestamp();
                           }

                           if (trimFnName.equalsIgnoreCase("utc_timestamp")) {
                              return new utcTimestamp();
                           }

                           if (trimFnName.equalsIgnoreCase("insert")) {
                              return new insert();
                           }

                           if (trimFnName.equalsIgnoreCase("strcmp")) {
                              return new strcmp();
                           }

                           if (trimFnName.equalsIgnoreCase("substring_index")) {
                              return new substringIndex();
                           }

                           if (trimFnName.equalsIgnoreCase("convert_tz")) {
                              return new convertTz();
                           }

                           if (trimFnName.equalsIgnoreCase("time_format")) {
                              return new dateformat();
                           }

                           if (trimFnName.equalsIgnoreCase("log2")) {
                              return new log();
                           }
                           break label3891;
                        }

                        return new TimeToSec();
                     }

                     return new extract();
                  }

                  return new getdate();
               }

               return new timeAddSub();
            }

            return new dateadd();
         }
      }

      if (isSqlite) {
         label3892: {
            if (trimFnName.equalsIgnoreCase("date_add") || trimFnName.equalsIgnoreCase("date_sub") || trimFnName.equalsIgnoreCase("adddate") || trimFnName.equalsIgnoreCase("subdate")) {
               return new dateadd();
            }

            if (!trimFnName.equalsIgnoreCase("week") && !trimFnName.equalsIgnoreCase("weekofyear") && !trimFnName.equalsIgnoreCase("yearweek")) {
               if (trimFnName.equalsIgnoreCase("ADDTIME") || trimFnName.equalsIgnoreCase("SUBTIME")) {
                  return new timeAddSub();
               }

               if (trimFnName.equalsIgnoreCase("STR_TO_DATE")) {
                  return new StrToDate();
               }

               if (trimFnName.equalsIgnoreCase("time_format") || trimFnName.equalsIgnoreCase("FROM_UNIXTIME")) {
                  return new dateformat();
               }

               if (trimFnName.equalsIgnoreCase("dayname") || trimFnName.equalsIgnoreCase("weekday") || trimFnName.equalsIgnoreCase("minute") || trimFnName.equalsIgnoreCase("quarter") || trimFnName.equalsIgnoreCase("dayofyear") || trimFnName.equalsIgnoreCase("dayofmonth") || trimFnName.equalsIgnoreCase("Microsecond") || trimFnName.equalsIgnoreCase("second")) {
                  return new extract();
               }

               if (trimFnName.equalsIgnoreCase("convert_tz")) {
                  return new convertTz();
               }

               if (trimFnName.equalsIgnoreCase("curdate") || trimFnName.equalsIgnoreCase("currentdate") || trimFnName.equalsIgnoreCase("current_date") || trimFnName.equalsIgnoreCase("current_timestamp")) {
                  return new getdate();
               }

               if (trimFnName.equalsIgnoreCase("current_time")) {
                  return new gettime();
               }

               if (trimFnName.equalsIgnoreCase("makedate")) {
                  return new MakeDate();
               }

               if (!trimFnName.equalsIgnoreCase("time_to_sec") && !trimFnName.equalsIgnoreCase("timediff") && !trimFnName.equalsIgnoreCase("timestamp")) {
                  if (trimFnName.equalsIgnoreCase("unix_timestamp")) {
                     return new unixTimestamp();
                  }

                  if (trimFnName.equalsIgnoreCase("utc_timestamp")) {
                     return new utcTimestamp();
                  }

                  if (trimFnName.equalsIgnoreCase("insert")) {
                     return new insert();
                  }

                  if (trimFnName.equalsIgnoreCase("substring_index")) {
                     return new substringIndex();
                  }
                  break label3892;
               }

               return new TimeToSec();
            }

            return new week();
         }
      }

      if (isInformix) {
         label3893: {
            if (trimFnName.equalsIgnoreCase("log2")) {
               return new log();
            }

            if (trimFnName.equalsIgnoreCase("substring_index")) {
               return new substringIndex();
            }

            if (trimFnName.equalsIgnoreCase("insert")) {
               return new insert();
            }

            if (trimFnName.equalsIgnoreCase("strcmp")) {
               return new strcmp();
            }

            if (trimFnName.equalsIgnoreCase("ifnull")) {
               return new ifnull();
            }

            if (!trimFnName.equalsIgnoreCase("dayname") && !trimFnName.equalsIgnoreCase("weekday") && !trimFnName.equalsIgnoreCase("minute") && !trimFnName.equalsIgnoreCase("quarter") && !trimFnName.equalsIgnoreCase("dayofyear") && !trimFnName.equalsIgnoreCase("dayofmonth") && !trimFnName.equalsIgnoreCase("Microsecond") && !trimFnName.equalsIgnoreCase("second")) {
               if (!trimFnName.equalsIgnoreCase("curdate") && !trimFnName.equalsIgnoreCase("currentdate") && !trimFnName.equalsIgnoreCase("current_date")) {
                  if (trimFnName.equalsIgnoreCase("current_time")) {
                     return new gettime();
                  }

                  if (trimFnName.equalsIgnoreCase("current_timestamp")) {
                     return new currentTimestamp();
                  }

                  if (trimFnName.equalsIgnoreCase("utc_timestamp")) {
                     return new utcTimestamp();
                  }

                  if (trimFnName.equalsIgnoreCase("unix_timestamp")) {
                     return new unixTimestamp();
                  }

                  if (trimFnName.equalsIgnoreCase("FROM_UNIXTIME")) {
                     return new FromTZ();
                  }

                  if (!trimFnName.equalsIgnoreCase("date_add") && !trimFnName.equalsIgnoreCase("date_sub") && !trimFnName.equalsIgnoreCase("adddate") && !trimFnName.equalsIgnoreCase("subdate")) {
                     if (trimFnName.equalsIgnoreCase("STR_TO_DATE") || trimFnName.equalsIgnoreCase("TIME_FORMAT")) {
                        return new dateformat();
                     }

                     if (trimFnName.equalsIgnoreCase("makedate")) {
                        return new MakeDate();
                     }

                     if (trimFnName.equalsIgnoreCase("time_to_sec") || trimFnName.equalsIgnoreCase("timediff") || trimFnName.equalsIgnoreCase("timestamp")) {
                        return new TimeToSec();
                     }

                     if (!trimFnName.equalsIgnoreCase("ADDTIME") && !trimFnName.equalsIgnoreCase("SUBTIME")) {
                        if (trimFnName.equalsIgnoreCase("convert_tz")) {
                           return new convertTz();
                        }

                        if (trimFnName.equalsIgnoreCase("week") || trimFnName.equalsIgnoreCase("weekofyear") || trimFnName.equalsIgnoreCase("yearweek")) {
                           return new week();
                        }
                        break label3893;
                     }

                     return new timeAddSub();
                  }

                  return new dateadd();
               }

               return new getdate();
            }

            return new extract();
         }
      }

      if (trimFnName.equalsIgnoreCase("substring") && SwisSQLOptions.SOURCE_DB_IS_ORACLE) {
         return null;
      } else if (!trimFnName.equalsIgnoreCase("substr") && !trimFnName.equalsIgnoreCase("substring") && !trimFnName.equalsIgnoreCase("substrb")) {
         if (trimFnName.equalsIgnoreCase("mid") && isPostgreSql) {
            return new substring();
         } else if (isVectorWise && trimFnName.equalsIgnoreCase("mid")) {
            return new substring();
         } else if (trimFnName.equalsIgnoreCase("space")) {
            return new space();
         } else if (trimFnName.equalsIgnoreCase("datename")) {
            return new datename();
         } else if (!trimFnName.equalsIgnoreCase("len") && !trimFnName.equalsIgnoreCase("length") && !trimFnName.equalsIgnoreCase("char_length") && !trimFnName.equalsIgnoreCase("character_length") && !trimFnName.equalsIgnoreCase("datalength") && !trimFnName.equalsIgnoreCase("lengthb") && !trimFnName.equalsIgnoreCase("octet_length")) {
            if (!trimFnName.equalsIgnoreCase("instr") && !trimFnName.equalsIgnoreCase("strpos") && !trimFnName.equalsIgnoreCase("posstr") && !trimFnName.equalsIgnoreCase("patindex") && !trimFnName.equalsIgnoreCase("locate") && !trimFnName.equalsIgnoreCase("instrb")) {
               if (trimFnName.equalsIgnoreCase("charindex") && !SwisSQLOptions.SOURCE_DB_IS_ORACLE) {
                  return new instr();
               } else if (!trimFnName.equalsIgnoreCase("lower") && !trimFnName.equalsIgnoreCase("lcase") && !trimFnName.equalsIgnoreCase("lowercase") && !trimFnName.equalsIgnoreCase("lower_case")) {
                  if (!trimFnName.equalsIgnoreCase("upper") && !trimFnName.equalsIgnoreCase("ucase") && !trimFnName.equalsIgnoreCase("uppercase") && !trimFnName.equalsIgnoreCase("upper_case")) {
                     if (trimFnName.equalsIgnoreCase("stuff")) {
                        return new stuff();
                     } else if (!trimFnName.equalsIgnoreCase("replace") && !trimFnName.equalsIgnoreCase("str_replace")) {
                        if (trimFnName.equalsIgnoreCase("reverse")) {
                           return new reverse();
                        } else if (!trimFnName.equalsIgnoreCase("lpad") && !trimFnName.equalsIgnoreCase("left_pad")) {
                           if (trimFnName.equalsIgnoreCase("trim")) {
                              return new trim();
                           } else if (!trimFnName.equalsIgnoreCase("ltrim") && !trimFnName.equalsIgnoreCase("left_trim")) {
                              if (!trimFnName.equalsIgnoreCase("rtrim") && !trimFnName.equalsIgnoreCase("right_trim")) {
                                 if (!trimFnName.equalsIgnoreCase("left") && !trimFnName.equalsIgnoreCase("left_string")) {
                                    if (!trimFnName.equalsIgnoreCase("right") && !trimFnName.equalsIgnoreCase("right_string")) {
                                       if (!trimFnName.equalsIgnoreCase("repeat") && !trimFnName.equalsIgnoreCase("replicate")) {
                                          if (!trimFnName.equalsIgnoreCase("rpad") && !trimFnName.equalsIgnoreCase("right_pad")) {
                                             if (trimFnName.equalsIgnoreCase("str")) {
                                                return new str();
                                             } else if (!trimFnName.equalsIgnoreCase("concat") && !trimFnName.equalsIgnoreCase("concatenate") && !trimFnName.equalsIgnoreCase("concat_ws")) {
                                                if (trimFnName.equalsIgnoreCase("concat_ignore_null")) {
                                                   return new concat_ignore_null();
                                                } else if (!trimFnName.equalsIgnoreCase("chr") && !trimFnName.equalsIgnoreCase("char") && !trimFnName.equalsIgnoreCase("nchar")) {
                                                   if (trimFnName.equalsIgnoreCase("initcap")) {
                                                      return new initcap();
                                                   } else if (trimFnName.equalsIgnoreCase("rawtohex")) {
                                                      return new rawtohex();
                                                   } else if (!trimFnName.equalsIgnoreCase("indexof") && !trimFnName.equalsIgnoreCase("substring_position") && !trimFnName.equalsIgnoreCase("index_of_string")) {
                                                      if (trimFnName.equalsIgnoreCase("soundex") && (isVectorWise || isPostgreSql)) {
                                                         return new soundex();
                                                      } else if (!trimFnName.equalsIgnoreCase("substring_between") && !trimFnName.equalsIgnoreCase("substring_after") && !trimFnName.equalsIgnoreCase("substring_before")) {
                                                         if (trimFnName.equalsIgnoreCase("substring_count")) {
                                                            return new substring_count();
                                                         } else if (!trimFnName.equalsIgnoreCase("isstartswith") && !trimFnName.equalsIgnoreCase("is_startswith") && !trimFnName.equalsIgnoreCase("isendswith") && !trimFnName.equalsIgnoreCase("is_endswith") && !trimFnName.equalsIgnoreCase("iscontains") && !trimFnName.equalsIgnoreCase("is_contains") && !trimFnName.equalsIgnoreCase("isempty") && !trimFnName.equalsIgnoreCase("is_empty") && !trimFnName.equalsIgnoreCase("to_string") && !trimFnName.equalsIgnoreCase("convert_to_datetime")) {
                                                            if (trimFnName.equalsIgnoreCase("pi")) {
                                                               return new pi();
                                                            } else if (!trimFnName.equalsIgnoreCase("pow") && !trimFnName.equalsIgnoreCase("power")) {
                                                               if (trimFnName.equalsIgnoreCase("square")) {
                                                                  return new square();
                                                               } else if (trimFnName.equalsIgnoreCase("cot")) {
                                                                  return new cot();
                                                               } else if (trimFnName.equalsIgnoreCase("round")) {
                                                                  return new round();
                                                               } else if (trimFnName.equalsIgnoreCase("abs")) {
                                                                  return new abs();
                                                               } else if (trimFnName.equalsIgnoreCase("exp")) {
                                                                  return new exp();
                                                               } else if (!trimFnName.equalsIgnoreCase("log") && !trimFnName.equalsIgnoreCase("log10")) {
                                                                  if (trimFnName.equalsIgnoreCase("ln")) {
                                                                     return new ln();
                                                                  } else if (trimFnName.equalsIgnoreCase("sqrt")) {
                                                                     return new sqrt();
                                                                  } else if (!trimFnName.equalsIgnoreCase("rand") && !trimFnName.equalsIgnoreCase("random")) {
                                                                     if (!trimFnName.equalsIgnoreCase("ceil") && !trimFnName.equalsIgnoreCase("ceiling")) {
                                                                        if (trimFnName.equalsIgnoreCase("floor")) {
                                                                           return new floor();
                                                                        } else if (!trimFnName.equalsIgnoreCase("sin") && !trimFnName.equalsIgnoreCase("cos") && !trimFnName.equalsIgnoreCase("tan") && !trimFnName.equalsIgnoreCase("asin") && !trimFnName.equalsIgnoreCase("acos") && !trimFnName.equalsIgnoreCase("atan") && !trimFnName.equalsIgnoreCase("sign")) {
                                                                           if (!trimFnName.equalsIgnoreCase("sinh") && !trimFnName.equalsIgnoreCase("cosh") && !trimFnName.equalsIgnoreCase("tanh")) {
                                                                              if (!trimFnName.equalsIgnoreCase("atan2") && !trimFnName.equalsIgnoreCase("atn2")) {
                                                                                 if (trimFnName.equalsIgnoreCase("mod")) {
                                                                                    return new mod();
                                                                                 } else if (trimFnName.equalsIgnoreCase("to_number")) {
                                                                                    return new tonumber();
                                                                                 } else if (!trimFnName.equalsIgnoreCase("trunc") && !trimFnName.equalsIgnoreCase("truncate") && !trimFnName.equalsIgnoreCase("integer") && !trimFnName.equalsIgnoreCase("int") && !trimFnName.equalsIgnoreCase("decimal") && !trimFnName.equalsIgnoreCase("dec")) {
                                                                                    if (trimFnName.equalsIgnoreCase("smallint")) {
                                                                                       return new smallint();
                                                                                    } else if (trimFnName.equalsIgnoreCase("bigint")) {
                                                                                       return new bigint();
                                                                                    } else if (trimFnName.equalsIgnoreCase("degrees")) {
                                                                                       return new degrees();
                                                                                    } else if (trimFnName.equalsIgnoreCase("radians")) {
                                                                                       return new radians();
                                                                                    } else if (!trimFnName.equalsIgnoreCase("getdate") && !trimFnName.equalsIgnoreCase("getutcdate") && !trimFnName.equalsIgnoreCase("now") && !trimFnName.equalsIgnoreCase("sysdate")) {
                                                                                       if (trimFnName.equalsIgnoreCase("to_char")) {
                                                                                          return new tochar();
                                                                                       } else if (!trimFnName.equalsIgnoreCase("to_date") && !trimFnName.equalsIgnoreCase("to_timestamp")) {
                                                                                          if (trimFnName.equalsIgnoreCase("add_months")) {
                                                                                             return new addmonths();
                                                                                          } else if (!trimFnName.equalsIgnoreCase("date_add") && !trimFnName.equalsIgnoreCase("date_sub")) {
                                                                                             if ((trimFnName.equalsIgnoreCase("dateadd") || trimFnName.equalsIgnoreCase("datesub")) && !SwisSQLOptions.SOURCE_DB_IS_ORACLE) {
                                                                                                return new dateadd();
                                                                                             } else if (isPostgreSql && trimFnName.equalsIgnoreCase("unix_timestamp")) {
                                                                                                return new unixTimestamp();
                                                                                             } else if (trimFnName.equalsIgnoreCase("datediff") && !SwisSQLOptions.SOURCE_DB_IS_ORACLE && !isExcel && !isMsAccessJdbc) {
                                                                                                return new datediff();
                                                                                             } else if (trimFnName.equalsIgnoreCase("months_between")) {
                                                                                                return new months_between();
                                                                                             } else if (!trimFnName.equalsIgnoreCase("month_diff") && !trimFnName.equalsIgnoreCase("monthdiff")) {
                                                                                                if (trimFnName.equalsIgnoreCase("datepart")) {
                                                                                                   return new datepart();
                                                                                                } else if (!trimFnName.equalsIgnoreCase("monthname") && !trimFnName.equalsIgnoreCase("month_name") && !trimFnName.equalsIgnoreCase("dayofweek") && !trimFnName.equalsIgnoreCase("day_of_week") && !trimFnName.equalsIgnoreCase("julian_day") && !trimFnName.equalsIgnoreCase("week_iso") && !trimFnName.equalsIgnoreCase("days")) {
                                                                                                   if (trimFnName.equalsIgnoreCase("hour")) {
                                                                                                      return new datepart();
                                                                                                   } else if (!trimFnName.equalsIgnoreCase("month") && !trimFnName.equalsIgnoreCase("monthnum") && !trimFnName.equalsIgnoreCase("month_num")) {
                                                                                                      if (trimFnName.equalsIgnoreCase("day")) {
                                                                                                         return new day();
                                                                                                      } else if (!trimFnName.equalsIgnoreCase("dayofquarter") && !trimFnName.equalsIgnoreCase("day_of_quarter")) {
                                                                                                         if (!trimFnName.equalsIgnoreCase("add_date") && !trimFnName.equalsIgnoreCase("addyear") && !trimFnName.equalsIgnoreCase("add_year") && !trimFnName.equalsIgnoreCase("addmonth") && !trimFnName.equalsIgnoreCase("add_month") && !trimFnName.equalsIgnoreCase("addweek") && !trimFnName.equalsIgnoreCase("add_week") && !trimFnName.equalsIgnoreCase("addquarter") && !trimFnName.equalsIgnoreCase("add_quarter") && !trimFnName.equalsIgnoreCase("addhour") && !trimFnName.equalsIgnoreCase("add_hour") && !trimFnName.equalsIgnoreCase("addminute") && !trimFnName.equalsIgnoreCase("add_minute") && !trimFnName.equalsIgnoreCase("addsecond") && !trimFnName.equalsIgnoreCase("add_second")) {
                                                                                                            if (trimFnName.equalsIgnoreCase("year")) {
                                                                                                               return new year();
                                                                                                            } else if (trimFnName.equalsIgnoreCase("start_day")) {
                                                                                                               return new start_day();
                                                                                                            } else if (trimFnName.equalsIgnoreCase("end_day")) {
                                                                                                               return new end_day();
                                                                                                            } else if (trimFnName.equalsIgnoreCase("last_day")) {
                                                                                                               return new last_day();
                                                                                                            } else if (trimFnName.equalsIgnoreCase("date")) {
                                                                                                               return new date();
                                                                                                            } else if (trimFnName.equalsIgnoreCase("next_day")) {
                                                                                                               return new next_day();
                                                                                                            } else if (!trimFnName.equalsIgnoreCase("isprevious_nyear") && !trimFnName.equalsIgnoreCase("is_previous_nyear") && !trimFnName.equalsIgnoreCase("isprevious_nmonth") && !trimFnName.equalsIgnoreCase("is_previous_nmonth") && !trimFnName.equalsIgnoreCase("isprevious_nquarter") && !trimFnName.equalsIgnoreCase("is_previous_nquarter") && !trimFnName.equalsIgnoreCase("ispreviousweek") && !trimFnName.equalsIgnoreCase("is_previous_week") && !trimFnName.equalsIgnoreCase("isprevious_nday") && !trimFnName.equalsIgnoreCase("is_previous_nday") && !trimFnName.equalsIgnoreCase("yesterday") && !trimFnName.equalsIgnoreCase("previous_nday") && !trimFnName.equalsIgnoreCase("previous_nmonth")) {
                                                                                                               if (!trimFnName.equalsIgnoreCase("islast_nyear") && !trimFnName.equalsIgnoreCase("is_last_nyear") && !trimFnName.equalsIgnoreCase("islast_nmonth") && !trimFnName.equalsIgnoreCase("is_last_nmonth") && !trimFnName.equalsIgnoreCase("islast_nquarter") && !trimFnName.equalsIgnoreCase("is_last_nquarter") && !trimFnName.equalsIgnoreCase("islast_nday") && !trimFnName.equalsIgnoreCase("is_last_nday") && !trimFnName.equalsIgnoreCase("last_nday") && !trimFnName.equalsIgnoreCase("last_nmonth")) {
                                                                                                                  if (!trimFnName.equalsIgnoreCase("isnext_nyear") && !trimFnName.equalsIgnoreCase("is_next_nyear") && !trimFnName.equalsIgnoreCase("isnext_nmonth") && !trimFnName.equalsIgnoreCase("is_next_nmonth") && !trimFnName.equalsIgnoreCase("isnext_nquarter") && !trimFnName.equalsIgnoreCase("is_next_nquarter") && !trimFnName.equalsIgnoreCase("isnextweek") && !trimFnName.equalsIgnoreCase("is_next_week") && !trimFnName.equalsIgnoreCase("isnext_nday") && !trimFnName.equalsIgnoreCase("is_next_nday") && !trimFnName.equalsIgnoreCase("tomorrow") && !trimFnName.equalsIgnoreCase("next_nday") && !trimFnName.equalsIgnoreCase("next_nmonth") && !trimFnName.equalsIgnoreCase("next_weekday") && !trimFnName.equalsIgnoreCase("next_week_day")) {
                                                                                                                     if (!trimFnName.equalsIgnoreCase("iscurrentyear") && !trimFnName.equalsIgnoreCase("is_current_year") && !trimFnName.equalsIgnoreCase("iscurrentmonth") && !trimFnName.equalsIgnoreCase("is_current_month") && !trimFnName.equalsIgnoreCase("iscurrentquarter") && !trimFnName.equalsIgnoreCase("is_current_quarter") && !trimFnName.equalsIgnoreCase("iscurrentweek") && !trimFnName.equalsIgnoreCase("is_current_week") && !trimFnName.equalsIgnoreCase("today")) {
                                                                                                                        if (!trimFnName.equalsIgnoreCase("business_completion_day") && !trimFnName.equalsIgnoreCase("business_days") && !trimFnName.equalsIgnoreCase("business_hours") && !trimFnName.equalsIgnoreCase("business_minutes")) {
                                                                                                                           if (!trimFnName.equalsIgnoreCase("start_datetime") && !trimFnName.equalsIgnoreCase("date_trunc")) {
                                                                                                                              if (!isMySql || !trimFnName.equalsIgnoreCase("quartername") && !trimFnName.equalsIgnoreCase("quarter_name") && !trimFnName.equalsIgnoreCase("quarternum") && !trimFnName.equalsIgnoreCase("quarter_num") && !trimFnName.equalsIgnoreCase("quarter")) {
                                                                                                                                 if (trimFnName.equalsIgnoreCase("extract")) {
                                                                                                                                    return new extract();
                                                                                                                                 } else if (!isMsAzure && !isExcel && !isMsAccessJdbc && trimFnName.equalsIgnoreCase("second")) {
                                                                                                                                    return new trunc();
                                                                                                                                 } else if (trimFnName.equalsIgnoreCase("date_format") || isVectorWise && trimFnName.equalsIgnoreCase("TIME_FORMAT")) {
                                                                                                                                    return new dateformat();
                                                                                                                                 } else if (trimFnName.equalsIgnoreCase("from_tz")) {
                                                                                                                                    return new FromTZ();
                                                                                                                                 } else if (trimFnName.equalsIgnoreCase("to_timestamp_tz")) {
                                                                                                                                    return new ToTimestampTZ();
                                                                                                                                 } else if (trimFnName.equalsIgnoreCase("NUMTODSINTERVAL")) {
                                                                                                                                    return new NumToDSInterval();
                                                                                                                                 } else if (trimFnName.equalsIgnoreCase("NumToYMInterval")) {
                                                                                                                                    return new NumToYMInterval();
                                                                                                                                 } else if (!trimFnName.equalsIgnoreCase("sysdatetime") && !trimFnName.equalsIgnoreCase("SYSUTCDATETIME")) {
                                                                                                                                    if (trimFnName.equalsIgnoreCase("SYSDATETIMEOFFSET")) {
                                                                                                                                       return new SysDateTimeOffset();
                                                                                                                                    } else if (!trimFnName.equalsIgnoreCase("first_date_current_week") && !trimFnName.equalsIgnoreCase("start_date_current_week")) {
                                                                                                                                       if (!trimFnName.equalsIgnoreCase("weekofmonth") && !trimFnName.equalsIgnoreCase("week_of_month")) {
                                                                                                                                          if (!trimFnName.equalsIgnoreCase("absquarter") && !trimFnName.equalsIgnoreCase("abs_quarter")) {
                                                                                                                                             if (!trimFnName.equalsIgnoreCase("absmonth") && !trimFnName.equalsIgnoreCase("abs_month")) {
                                                                                                                                                if (!trimFnName.equalsIgnoreCase("absweek") && !trimFnName.equalsIgnoreCase("abs_week")) {
                                                                                                                                                   if ((!isMySql || !trimFnName.equalsIgnoreCase("weekofyear")) && !trimFnName.equalsIgnoreCase("week_of_year")) {
                                                                                                                                                      if (!trimFnName.equalsIgnoreCase("group_first") && !trimFnName.equalsIgnoreCase("group_last")) {
                                                                                                                                                         if (!trimFnName.equalsIgnoreCase("to_integer") && !trimFnName.equalsIgnoreCase("to_decimal") && !trimFnName.equalsIgnoreCase("to_currency") && !trimFnName.equalsIgnoreCase("to_percentage") && !trimFnName.equalsIgnoreCase("to_duration") && !trimFnName.equalsIgnoreCase("to_positive_integer")) {
                                                                                                                                                            if (!isVectorWise || !trimFnName.equalsIgnoreCase("minute") && !trimFnName.equalsIgnoreCase("quarter") && !trimFnName.equalsIgnoreCase("dayofmonth") && !trimFnName.equalsIgnoreCase("dayofyear")) {
                                                                                                                                                               if (isVectorWise && (trimFnName.equalsIgnoreCase("from_unixtime") || trimFnName.equalsIgnoreCase("from_days") || trimFnName.equalsIgnoreCase("to_days") || trimFnName.equalsIgnoreCase("dayname"))) {
                                                                                                                                                                  return new FromTZ();
                                                                                                                                                               } else if (isVectorWise && trimFnName.equalsIgnoreCase("microsecond")) {
                                                                                                                                                                  return new datepart();
                                                                                                                                                               } else if (!isVectorWise || !trimFnName.equalsIgnoreCase("time_to_sec") && !trimFnName.equalsIgnoreCase("adddate") && !trimFnName.equalsIgnoreCase("subdate") && !trimFnName.equalsIgnoreCase("timediff") && !trimFnName.equalsIgnoreCase("weekday")) {
                                                                                                                                                                  if (isVectorWise && trimFnName.equalsIgnoreCase("binary")) {
                                                                                                                                                                     return new Binary();
                                                                                                                                                                  } else if (isPostgreSql && (trimFnName.equalsIgnoreCase("hex") || trimFnName.equalsIgnoreCase("unhex") || trimFnName.equalsIgnoreCase("aes_decrypt") || trimFnName.equalsIgnoreCase("aes_encrypt"))) {
                                                                                                                                                                     return new hex();
                                                                                                                                                                  } else if (!isVectorWise || !trimFnName.equalsIgnoreCase("hex") && !trimFnName.equalsIgnoreCase("aes_decrypt") && !trimFnName.equalsIgnoreCase("aes_encrypt")) {
                                                                                                                                                                     if (isVectorWise && (trimFnName.equalsIgnoreCase("makedate") || trimFnName.equalsIgnoreCase("period_add") || trimFnName.equalsIgnoreCase("maketime") || trimFnName.equalsIgnoreCase("period_diff"))) {
                                                                                                                                                                        return new MakeDate();
                                                                                                                                                                     } else if (isVectorWise && (trimFnName.equalsIgnoreCase("week") || trimFnName.equalsIgnoreCase("yearweek") || trimFnName.equalsIgnoreCase("addtime") || trimFnName.equalsIgnoreCase("subtime") || trimFnName.equalsIgnoreCase("weekofyear") || trimFnName.equalsIgnoreCase("convert_tz") || trimFnName.equalsIgnoreCase("convert_timezone"))) {
                                                                                                                                                                        return new week();
                                                                                                                                                                     } else if (!isVectorWise || !trimFnName.equalsIgnoreCase("curdate") && !trimFnName.equalsIgnoreCase("currentdate") && !trimFnName.equalsIgnoreCase("current_date") && !trimFnName.equalsIgnoreCase("cur_date")) {
                                                                                                                                                                        if (isVectorWise && trimFnName.equalsIgnoreCase("format")) {
                                                                                                                                                                           return new format();
                                                                                                                                                                        } else if (isVectorWise && trimFnName.equalsIgnoreCase("STR_TO_DATE")) {
                                                                                                                                                                           return new StrToDate();
                                                                                                                                                                        } else if (!isPostgreSql || !trimFnName.equalsIgnoreCase("date_add") && !trimFnName.equalsIgnoreCase("date_sub") && !trimFnName.equalsIgnoreCase("adddate") && !trimFnName.equalsIgnoreCase("subdate")) {
                                                                                                                                                                           if (isPostgreSql && trimFnName.equalsIgnoreCase("mid")) {
                                                                                                                                                                              return new hex();
                                                                                                                                                                           } else if ((isOracle || isPostgreSql || isMsAzure || isVectorWise) && trimFnName.equalsIgnoreCase("log2")) {
                                                                                                                                                                              return new log();
                                                                                                                                                                           } else if (isPostgreSql && trimFnName.equalsIgnoreCase("convert_tz")) {
                                                                                                                                                                              return new convertTz();
                                                                                                                                                                           } else if (!trimFnName.equalsIgnoreCase("timestampdiff") && !trimFnName.equalsIgnoreCase("timestamp_diff") && !trimFnName.equalsIgnoreCase("dateandtimediff") && !trimFnName.equalsIgnoreCase("datetime_diff") && !trimFnName.equalsIgnoreCase("age_years") && !trimFnName.equalsIgnoreCase("age_months") && !trimFnName.equalsIgnoreCase("days_between")) {
                                                                                                                                                                              if (!isPostgreSql && !isVectorWise && !isMySql || !trimFnName.equalsIgnoreCase("TIMESTAMPADD") && !trimFnName.equalsIgnoreCase("TIMESTAMP_ADD")) {
                                                                                                                                                                                 if (isPostgreSql && (trimFnName.equalsIgnoreCase("ADDTIME") || trimFnName.equalsIgnoreCase("SUBTIME"))) {
                                                                                                                                                                                    return new timeAddSub();
                                                                                                                                                                                 } else if (!isPostgreSql || !trimFnName.equalsIgnoreCase("curdate") && !trimFnName.equalsIgnoreCase("currentdate") && !trimFnName.equalsIgnoreCase("current_date")) {
                                                                                                                                                                                    if (isPostgreSql && (trimFnName.equalsIgnoreCase("current_time") || trimFnName.equalsIgnoreCase("curtime"))) {
                                                                                                                                                                                       return new gettime();
                                                                                                                                                                                    } else if (!isVectorWise || !trimFnName.equalsIgnoreCase("current_time") && !trimFnName.equalsIgnoreCase("curtime")) {
                                                                                                                                                                                       if (!isPostgreSql || !trimFnName.equalsIgnoreCase("weekday") && !trimFnName.equalsIgnoreCase("minute") && !trimFnName.equalsIgnoreCase("microsecond") && !trimFnName.equalsIgnoreCase("quarter") && !trimFnName.equalsIgnoreCase("dayname") && !trimFnName.equalsIgnoreCase("dayofmonth") && !trimFnName.equalsIgnoreCase("dayofyear")) {
                                                                                                                                                                                          if (!isPostgreSql || !trimFnName.equalsIgnoreCase("makedate") && !trimFnName.equalsIgnoreCase("period_add") && !trimFnName.equalsIgnoreCase("period_diff") && !trimFnName.equalsIgnoreCase("maketime")) {
                                                                                                                                                                                             if (isPostgreSql && (trimFnName.equalsIgnoreCase("timestamp") || trimFnName.equalsIgnoreCase("timediff"))) {
                                                                                                                                                                                                return new TimeToSec();
                                                                                                                                                                                             } else if ((isPostgreSql || isMySql || isVectorWise) && trimFnName.equalsIgnoreCase("SEC_TO_TIME")) {
                                                                                                                                                                                                return new SecToTime();
                                                                                                                                                                                             } else if ((isVectorWise || isPostgreSql) && trimFnName.equalsIgnoreCase("localtimestamp")) {
                                                                                                                                                                                                return new localtimestamp();
                                                                                                                                                                                             } else if (isPostgreSql && (trimFnName.equalsIgnoreCase("STR_TO_DATE") || trimFnName.equalsIgnoreCase("TIME_FORMAT"))) {
                                                                                                                                                                                                return new dateformat();
                                                                                                                                                                                             } else if (trimFnName.equalsIgnoreCase("time")) {
                                                                                                                                                                                                return new time();
                                                                                                                                                                                             } else if ((isVectorWise || isPostgreSql) && trimFnName.equalsIgnoreCase("utc_date")) {
                                                                                                                                                                                                return new utcDate();
                                                                                                                                                                                             } else if ((isVectorWise || isPostgreSql) && (trimFnName.equalsIgnoreCase("utc_timestamp") || trimFnName.equalsIgnoreCase("utc_time"))) {
                                                                                                                                                                                                return new utcTimestamp();
                                                                                                                                                                                             } else if (isPostgreSql && (trimFnName.equalsIgnoreCase("week") || trimFnName.equalsIgnoreCase("weekofyear") || trimFnName.equalsIgnoreCase("yearweek"))) {
                                                                                                                                                                                                return new week();
                                                                                                                                                                                             } else if ((isVectorWise || isPostgreSql) && trimFnName.equalsIgnoreCase("current_timestamp")) {
                                                                                                                                                                                                return new currentTimestamp();
                                                                                                                                                                                             } else if (!isPostgreSql || !trimFnName.equalsIgnoreCase("FROM_UNIXTIME") && !trimFnName.equalsIgnoreCase("from_days") && !trimFnName.equalsIgnoreCase("to_days")) {
                                                                                                                                                                                                if (isPostgreSql && trimFnName.equalsIgnoreCase("time_to_sec")) {
                                                                                                                                                                                                   return new TimeToSec();
                                                                                                                                                                                                } else if (isPostgreSql && trimFnName.equalsIgnoreCase("binary")) {
                                                                                                                                                                                                   return new Binary();
                                                                                                                                                                                                } else if (isPostgreSql && trimFnName.equalsIgnoreCase("format")) {
                                                                                                                                                                                                   return new format();
                                                                                                                                                                                                } else if (!isPostgreSql || !trimFnName.equalsIgnoreCase("conv") && !trimFnName.equalsIgnoreCase("bin") && !trimFnName.equalsIgnoreCase("oct")) {
                                                                                                                                                                                                   if (isPostgreSql && (trimFnName.equalsIgnoreCase("md5") || trimFnName.equalsIgnoreCase("ZA_AES128_DECRYPT") || trimFnName.equalsIgnoreCase("ZA_AES128_ENCRYPT"))) {
                                                                                                                                                                                                      return new encrypt();
                                                                                                                                                                                                   } else if ((isVectorWise || isPostgreSql || isMsAzure || isOracle || isSqlite || isExcel || isMsAccessJdbc) && trimFnName.equalsIgnoreCase("strcmp")) {
                                                                                                                                                                                                      return new strcmp();
                                                                                                                                                                                                   } else if ((isVectorWise || isPostgreSql || isExcel || isMsAccessJdbc) && trimFnName.equalsIgnoreCase("insert")) {
                                                                                                                                                                                                      return new insert();
                                                                                                                                                                                                   } else if (!isMySql || !trimFnName.equalsIgnoreCase("timediff") && !trimFnName.equalsIgnoreCase("time_diff")) {
                                                                                                                                                                                                      if (trimFnName.equalsIgnoreCase("make_time")) {
                                                                                                                                                                                                         return new maketime();
                                                                                                                                                                                                      } else if (trimFnName.equalsIgnoreCase("time_to_min")) {
                                                                                                                                                                                                         return new timetomin();
                                                                                                                                                                                                      } else if (trimFnName.equalsIgnoreCase("start_of_hour")) {
                                                                                                                                                                                                         return new startofhour();
                                                                                                                                                                                                      } else if (trimFnName.equalsIgnoreCase("end_of_hour")) {
                                                                                                                                                                                                         return new endofhour();
                                                                                                                                                                                                      } else if (trimFnName.equalsIgnoreCase("truncate_micro_sec")) {
                                                                                                                                                                                                         return new truncmicrosec();
                                                                                                                                                                                                      } else if (trimFnName.equalsIgnoreCase("time_diff_in_duration")) {
                                                                                                                                                                                                         return new timediffinduration();
                                                                                                                                                                                                      } else if (trimFnName.equalsIgnoreCase("is_current_hour")) {
                                                                                                                                                                                                         return new iscurrenthour();
                                                                                                                                                                                                      } else if (trimFnName.equalsIgnoreCase("is_last_nhour")) {
                                                                                                                                                                                                         return new islastnhour();
                                                                                                                                                                                                      } else if (trimFnName.equalsIgnoreCase("is_previous_nhour")) {
                                                                                                                                                                                                         return new ispreviousnhour();
                                                                                                                                                                                                      } else if (trimFnName.equalsIgnoreCase("is_next_nhour")) {
                                                                                                                                                                                                         return new isnextnhour();
                                                                                                                                                                                                      } else if (trimFnName.equalsIgnoreCase("time_sum_in_duration")) {
                                                                                                                                                                                                         return new addtimetotime();
                                                                                                                                                                                                      } else if (!trimFnName.equalsIgnoreCase("datetime_diff_in_duration") && !trimFnName.equalsIgnoreCase("timestamp_diff_in_duration")) {
                                                                                                                                                                                                         if (trimFnName.equalsIgnoreCase("ZR_CONV_HMS_TO_DUR")) {
                                                                                                                                                                                                            return new convert_hms_to_dur();
                                                                                                                                                                                                         } else if (trimFnName.equalsIgnoreCase("ZR_CONV_DAYS_HMS_TO_DUR")) {
                                                                                                                                                                                                            return new convert_dayshms_to_dur();
                                                                                                                                                                                                         } else if (trimFnName.equalsIgnoreCase("ZR_CONV_FMTD_DAYS_HMS_TO_DUR")) {
                                                                                                                                                                                                            return new convert_fmtdayshms_to_dur();
                                                                                                                                                                                                         } else if (trimFnName.equalsIgnoreCase("convert_to_duration")) {
                                                                                                                                                                                                            return new ConvertStringToDuration();
                                                                                                                                                                                                         } else if (trimFnName.equalsIgnoreCase("duration_to_years")) {
                                                                                                                                                                                                            return new yearsofduration();
                                                                                                                                                                                                         } else if (trimFnName.equalsIgnoreCase("duration_to_months")) {
                                                                                                                                                                                                            return new monthsofduration();
                                                                                                                                                                                                         } else if (trimFnName.equalsIgnoreCase("duration_to_weeks")) {
                                                                                                                                                                                                            return new weeksofduration();
                                                                                                                                                                                                         } else if (trimFnName.equalsIgnoreCase("duration_to_days")) {
                                                                                                                                                                                                            return new daysofduration();
                                                                                                                                                                                                         } else if (trimFnName.equalsIgnoreCase("duration_to_hours")) {
                                                                                                                                                                                                            return new hoursofduration();
                                                                                                                                                                                                         } else if (trimFnName.equalsIgnoreCase("duration_to_minutes")) {
                                                                                                                                                                                                            return new minutesofduration();
                                                                                                                                                                                                         } else if (trimFnName.equalsIgnoreCase("duration_to_seconds")) {
                                                                                                                                                                                                            return new secondsofduration();
                                                                                                                                                                                                         } else if (trimFnName.equalsIgnoreCase("add_years_to_duration")) {
                                                                                                                                                                                                            return new addyearstoduration();
                                                                                                                                                                                                         } else if (trimFnName.equalsIgnoreCase("add_months_to_duration")) {
                                                                                                                                                                                                            return new addmonthstoduration();
                                                                                                                                                                                                         } else if (trimFnName.equalsIgnoreCase("add_weeks_to_duration")) {
                                                                                                                                                                                                            return new addweekstoduration();
                                                                                                                                                                                                         } else if (trimFnName.equalsIgnoreCase("add_days_to_duration")) {
                                                                                                                                                                                                            return new adddaystoduration();
                                                                                                                                                                                                         } else if (trimFnName.equalsIgnoreCase("add_hours_to_duration")) {
                                                                                                                                                                                                            return new addhourstoduration();
                                                                                                                                                                                                         } else if (trimFnName.equalsIgnoreCase("add_minutes_to_duration")) {
                                                                                                                                                                                                            return new addminutesstoduration();
                                                                                                                                                                                                         } else if (trimFnName.equalsIgnoreCase("add_seconds_to_duration")) {
                                                                                                                                                                                                            return new addsecondstoduration();
                                                                                                                                                                                                         } else if (trimFnName.equalsIgnoreCase("sub_years_from_duration")) {
                                                                                                                                                                                                            return new subyearsfromduration();
                                                                                                                                                                                                         } else if (trimFnName.equalsIgnoreCase("sub_months_from_duration")) {
                                                                                                                                                                                                            return new submonthsfromduration();
                                                                                                                                                                                                         } else if (trimFnName.equalsIgnoreCase("sub_weeks_from_duration")) {
                                                                                                                                                                                                            return new subweeksfromduration();
                                                                                                                                                                                                         } else if (trimFnName.equalsIgnoreCase("sub_days_from_duration")) {
                                                                                                                                                                                                            return new subdaysfromduration();
                                                                                                                                                                                                         } else if (trimFnName.equalsIgnoreCase("sub_hours_from_duration")) {
                                                                                                                                                                                                            return new subhoursfromduration();
                                                                                                                                                                                                         } else if (trimFnName.equalsIgnoreCase("sub_minutes_from_duration")) {
                                                                                                                                                                                                            return new subminutesfromduration();
                                                                                                                                                                                                         } else if (trimFnName.equalsIgnoreCase("sub_seconds_from_duration")) {
                                                                                                                                                                                                            return new subsecondsfromduration();
                                                                                                                                                                                                         } else if (trimFnName.equalsIgnoreCase("make_duration")) {
                                                                                                                                                                                                            return new makeduration();
                                                                                                                                                                                                         } else if (trimFnName.equalsIgnoreCase("add_duration")) {
                                                                                                                                                                                                            return new addduration();
                                                                                                                                                                                                         } else if (trimFnName.equalsIgnoreCase("sub_duration")) {
                                                                                                                                                                                                            return new subduration();
                                                                                                                                                                                                         } else if (trimFnName.equalsIgnoreCase("round_duration")) {
                                                                                                                                                                                                            return new RoundDuration();
                                                                                                                                                                                                         } else if ((!isMySql || !trimFnName.equalsIgnoreCase("extract_json")) && (!isPostgreSql || !trimFnName.equalsIgnoreCase("json_value"))) {
                                                                                                                                                                                                            if (isMsAzure) {
                                                                                                                                                                                                               if (trimFnName.equalsIgnoreCase("mid")) {
                                                                                                                                                                                                                  return new substring();
                                                                                                                                                                                                               }

                                                                                                                                                                                                               if (trimFnName.equalsIgnoreCase("date_add") || trimFnName.equalsIgnoreCase("date_sub") || trimFnName.equalsIgnoreCase("adddate") || trimFnName.equalsIgnoreCase("subdate") || trimFnName.equalsIgnoreCase("datediff")) {
                                                                                                                                                                                                                  return new dateadd();
                                                                                                                                                                                                               }

                                                                                                                                                                                                               if (trimFnName.equalsIgnoreCase("minute") || trimFnName.equalsIgnoreCase("quarter") || trimFnName.equalsIgnoreCase("dayname") || trimFnName.equalsIgnoreCase("weekday") || trimFnName.equalsIgnoreCase("dayofyear") || trimFnName.equalsIgnoreCase("dayofmonth") || trimFnName.equalsIgnoreCase("Microsecond")) {
                                                                                                                                                                                                                  return new extract();
                                                                                                                                                                                                               }

                                                                                                                                                                                                               if (trimFnName.equalsIgnoreCase("week") || trimFnName.equalsIgnoreCase("weekofyear") || trimFnName.equalsIgnoreCase("yearweek") || trimFnName.equalsIgnoreCase("convert_tz")) {
                                                                                                                                                                                                                  return new week();
                                                                                                                                                                                                               }

                                                                                                                                                                                                               if (trimFnName.equalsIgnoreCase("date_diff")) {
                                                                                                                                                                                                                  return new datediff();
                                                                                                                                                                                                               }

                                                                                                                                                                                                               if (trimFnName.equalsIgnoreCase("STR_TO_DATE") || trimFnName.equalsIgnoreCase("time_format") || trimFnName.equalsIgnoreCase("FROM_UNIXTIME")) {
                                                                                                                                                                                                                  return new dateformat();
                                                                                                                                                                                                               }

                                                                                                                                                                                                               if (trimFnName.equalsIgnoreCase("ADDTIME") || trimFnName.equalsIgnoreCase("SUBTIME")) {
                                                                                                                                                                                                                  return new timeAddSub();
                                                                                                                                                                                                               }

                                                                                                                                                                                                               if (trimFnName.equalsIgnoreCase("makedate")) {
                                                                                                                                                                                                                  return new MakeDate();
                                                                                                                                                                                                               }

                                                                                                                                                                                                               if (trimFnName.equalsIgnoreCase("ifnull")) {
                                                                                                                                                                                                                  return new ifnull();
                                                                                                                                                                                                               }

                                                                                                                                                                                                               if (trimFnName.equalsIgnoreCase("insert")) {
                                                                                                                                                                                                                  return new insert();
                                                                                                                                                                                                               }

                                                                                                                                                                                                               if (trimFnName.equalsIgnoreCase("second")) {
                                                                                                                                                                                                                  return new datepart();
                                                                                                                                                                                                               }

                                                                                                                                                                                                               if (trimFnName.equalsIgnoreCase("binary")) {
                                                                                                                                                                                                                  return new Binary();
                                                                                                                                                                                                               }

                                                                                                                                                                                                               if (trimFnName.equalsIgnoreCase("time_to_sec") || trimFnName.equalsIgnoreCase("timediff") || trimFnName.equalsIgnoreCase("timestamp")) {
                                                                                                                                                                                                                  return new TimeToSec();
                                                                                                                                                                                                               }

                                                                                                                                                                                                               if (trimFnName.equalsIgnoreCase("current_time") || trimFnName.equalsIgnoreCase("curtime")) {
                                                                                                                                                                                                                  return new gettime();
                                                                                                                                                                                                               }

                                                                                                                                                                                                               if (trimFnName.equalsIgnoreCase("curdate") || trimFnName.equalsIgnoreCase("currentdate") || trimFnName.equalsIgnoreCase("current_date") || trimFnName.equalsIgnoreCase("cur_date")) {
                                                                                                                                                                                                                  return new getdate();
                                                                                                                                                                                                               }

                                                                                                                                                                                                               if (trimFnName.equalsIgnoreCase("unix_timestamp")) {
                                                                                                                                                                                                                  return new unixTimestamp();
                                                                                                                                                                                                               }

                                                                                                                                                                                                               if (trimFnName.equalsIgnoreCase("current_timestamp")) {
                                                                                                                                                                                                                  return new currentTimestamp();
                                                                                                                                                                                                               }

                                                                                                                                                                                                               if (trimFnName.equalsIgnoreCase("substring_index")) {
                                                                                                                                                                                                                  return new substringIndex();
                                                                                                                                                                                                               }

                                                                                                                                                                                                               if (trimFnName.equalsIgnoreCase("utc_timestamp")) {
                                                                                                                                                                                                                  return new utcTimestamp();
                                                                                                                                                                                                               }
                                                                                                                                                                                                            }

                                                                                                                                                                                                            if (isBigQuery) {
                                                                                                                                                                                                               label3905: {
                                                                                                                                                                                                                  if (trimFnName.equalsIgnoreCase("unix_timestamp")) {
                                                                                                                                                                                                                     return new unixTimestamp();
                                                                                                                                                                                                                  }

                                                                                                                                                                                                                  if (!trimFnName.equalsIgnoreCase("date_add") && !trimFnName.equalsIgnoreCase("date_sub") && !trimFnName.equalsIgnoreCase("adddate") && !trimFnName.equalsIgnoreCase("subdate")) {
                                                                                                                                                                                                                     if (trimFnName.equalsIgnoreCase("mid")) {
                                                                                                                                                                                                                        return new substring();
                                                                                                                                                                                                                     }

                                                                                                                                                                                                                     if (trimFnName.equalsIgnoreCase("log2")) {
                                                                                                                                                                                                                        return new log();
                                                                                                                                                                                                                     }

                                                                                                                                                                                                                     if (!trimFnName.equalsIgnoreCase("ADDTIME") && !trimFnName.equalsIgnoreCase("SUBTIME")) {
                                                                                                                                                                                                                        if (trimFnName.equalsIgnoreCase("curdate") || trimFnName.equalsIgnoreCase("currentdate") || trimFnName.equalsIgnoreCase("current_date")) {
                                                                                                                                                                                                                           return new getdate();
                                                                                                                                                                                                                        }

                                                                                                                                                                                                                        if (trimFnName.equalsIgnoreCase("current_time") || trimFnName.equalsIgnoreCase("curtime")) {
                                                                                                                                                                                                                           return new gettime();
                                                                                                                                                                                                                        }

                                                                                                                                                                                                                        if (!trimFnName.equalsIgnoreCase("weekday") && !trimFnName.equalsIgnoreCase("minute") && !trimFnName.equalsIgnoreCase("microsecond") && !trimFnName.equalsIgnoreCase("quarter") && !trimFnName.equalsIgnoreCase("dayname") && !trimFnName.equalsIgnoreCase("dayofmonth") && !trimFnName.equalsIgnoreCase("dayofyear")) {
                                                                                                                                                                                                                           if (trimFnName.equalsIgnoreCase("makedate") || trimFnName.equalsIgnoreCase("maketime")) {
                                                                                                                                                                                                                              return new MakeDate();
                                                                                                                                                                                                                           }

                                                                                                                                                                                                                           if (trimFnName.equalsIgnoreCase("timestamp") || trimFnName.equalsIgnoreCase("timediff") || trimFnName.equalsIgnoreCase("time_to_sec")) {
                                                                                                                                                                                                                              return new TimeToSec();
                                                                                                                                                                                                                           }

                                                                                                                                                                                                                           if (trimFnName.equalsIgnoreCase("SEC_TO_TIME")) {
                                                                                                                                                                                                                              return new SecToTime();
                                                                                                                                                                                                                           }

                                                                                                                                                                                                                           if (!trimFnName.equalsIgnoreCase("STR_TO_DATE") && !trimFnName.equalsIgnoreCase("TIME_FORMAT")) {
                                                                                                                                                                                                                              if (!trimFnName.equalsIgnoreCase("week") && !trimFnName.equalsIgnoreCase("weekofyear") && !trimFnName.equalsIgnoreCase("yearweek")) {
                                                                                                                                                                                                                                 if (trimFnName.equalsIgnoreCase("current_timestamp")) {
                                                                                                                                                                                                                                    return new currentTimestamp();
                                                                                                                                                                                                                                 }

                                                                                                                                                                                                                                 if (trimFnName.equalsIgnoreCase("FROM_UNIXTIME") || trimFnName.equalsIgnoreCase("from_days") || trimFnName.equalsIgnoreCase("to_days")) {
                                                                                                                                                                                                                                    return new FromTZ();
                                                                                                                                                                                                                                 }

                                                                                                                                                                                                                                 if (trimFnName.equalsIgnoreCase("binary")) {
                                                                                                                                                                                                                                    return new Binary();
                                                                                                                                                                                                                                 }

                                                                                                                                                                                                                                 if (trimFnName.equalsIgnoreCase("strcmp")) {
                                                                                                                                                                                                                                    return new strcmp();
                                                                                                                                                                                                                                 }

                                                                                                                                                                                                                                 if (trimFnName.equalsIgnoreCase("insert")) {
                                                                                                                                                                                                                                    return new insert();
                                                                                                                                                                                                                                 }

                                                                                                                                                                                                                                 if (trimFnName.equalsIgnoreCase("varchar")) {
                                                                                                                                                                                                                                    return new convert();
                                                                                                                                                                                                                                 }

                                                                                                                                                                                                                                 if (trimFnName.equalsIgnoreCase("ifnull")) {
                                                                                                                                                                                                                                    return new ifnull();
                                                                                                                                                                                                                                 }

                                                                                                                                                                                                                                 if (trimFnName.equalsIgnoreCase("substring_index")) {
                                                                                                                                                                                                                                    return new substringIndex();
                                                                                                                                                                                                                                 }

                                                                                                                                                                                                                                 if (trimFnName.equalsIgnoreCase("utc_timestamp")) {
                                                                                                                                                                                                                                    return new utcTimestamp();
                                                                                                                                                                                                                                 }

                                                                                                                                                                                                                                 if (trimFnName.equalsIgnoreCase("convert_tz")) {
                                                                                                                                                                                                                                    return new convertTz();
                                                                                                                                                                                                                                 }
                                                                                                                                                                                                                                 break label3905;
                                                                                                                                                                                                                              }

                                                                                                                                                                                                                              return new week();
                                                                                                                                                                                                                           }

                                                                                                                                                                                                                           return new dateformat();
                                                                                                                                                                                                                        }

                                                                                                                                                                                                                        return new extract();
                                                                                                                                                                                                                     }

                                                                                                                                                                                                                     return new timeAddSub();
                                                                                                                                                                                                                  }

                                                                                                                                                                                                                  return new dateadd();
                                                                                                                                                                                                               }
                                                                                                                                                                                                            }

                                                                                                                                                                                                            if (isExcel || isMsAccessJdbc) {
                                                                                                                                                                                                               label3926: {
                                                                                                                                                                                                                  if (!trimFnName.equalsIgnoreCase("weekday") && !trimFnName.equalsIgnoreCase("minute") && !trimFnName.equalsIgnoreCase("microsecond") && !trimFnName.equalsIgnoreCase("quarter") && !trimFnName.equalsIgnoreCase("dayname") && !trimFnName.equalsIgnoreCase("dayofmonth") && !trimFnName.equalsIgnoreCase("dayofyear") && !trimFnName.equalsIgnoreCase("second")) {
                                                                                                                                                                                                                     if (!trimFnName.equalsIgnoreCase("week") && !trimFnName.equalsIgnoreCase("weekofyear") && !trimFnName.equalsIgnoreCase("yearweek") && !trimFnName.equalsIgnoreCase("convert_tz")) {
                                                                                                                                                                                                                        if (!trimFnName.equalsIgnoreCase("curdate") && !trimFnName.equalsIgnoreCase("currentdate") && !trimFnName.equalsIgnoreCase("current_date") && !trimFnName.equalsIgnoreCase("cur_date") && !trimFnName.equalsIgnoreCase("current_timestamp") && !trimFnName.equalsIgnoreCase("current_time")) {
                                                                                                                                                                                                                           if (trimFnName.equalsIgnoreCase("substring_index")) {
                                                                                                                                                                                                                              return new substringIndex();
                                                                                                                                                                                                                           }

                                                                                                                                                                                                                           if (trimFnName.equalsIgnoreCase("unix_timestamp")) {
                                                                                                                                                                                                                              return new unixTimestamp();
                                                                                                                                                                                                                           }

                                                                                                                                                                                                                           if (trimFnName.equalsIgnoreCase("timestamp") || trimFnName.equalsIgnoreCase("timediff") || trimFnName.equalsIgnoreCase("time_to_sec")) {
                                                                                                                                                                                                                              return new TimeToSec();
                                                                                                                                                                                                                           }

                                                                                                                                                                                                                           if (trimFnName.equalsIgnoreCase("str_to_date")) {
                                                                                                                                                                                                                              return new StrToDate();
                                                                                                                                                                                                                           }

                                                                                                                                                                                                                           if (!trimFnName.equalsIgnoreCase("TIME_FORMAT") && !trimFnName.equalsIgnoreCase("FROM_UNIXTIME")) {
                                                                                                                                                                                                                              if (!trimFnName.equalsIgnoreCase("ADDTIME") && !trimFnName.equalsIgnoreCase("SUBTIME")) {
                                                                                                                                                                                                                                 if (trimFnName.equalsIgnoreCase("date_add") || trimFnName.equalsIgnoreCase("date_sub") || trimFnName.equalsIgnoreCase("adddate") || trimFnName.equalsIgnoreCase("subdate")) {
                                                                                                                                                                                                                                    return new dateadd();
                                                                                                                                                                                                                                 }

                                                                                                                                                                                                                                 if (trimFnName.equalsIgnoreCase("makedate") || trimFnName.equalsIgnoreCase("maketime")) {
                                                                                                                                                                                                                                    return new MakeDate();
                                                                                                                                                                                                                                 }

                                                                                                                                                                                                                                 if (trimFnName.equalsIgnoreCase("datediff")) {
                                                                                                                                                                                                                                    return new timestampDiff();
                                                                                                                                                                                                                                 }

                                                                                                                                                                                                                                 if (trimFnName.equalsIgnoreCase("utc_timestamp")) {
                                                                                                                                                                                                                                    return new utcTimestamp();
                                                                                                                                                                                                                                 }

                                                                                                                                                                                                                                 if (trimFnName.equalsIgnoreCase("ifnull")) {
                                                                                                                                                                                                                                    return new ifnull();
                                                                                                                                                                                                                                 }

                                                                                                                                                                                                                                 if (!trimFnName.equalsIgnoreCase("log") && !trimFnName.equalsIgnoreCase("log10") && !trimFnName.equalsIgnoreCase("log2")) {
                                                                                                                                                                                                                                    break label3926;
                                                                                                                                                                                                                                 }

                                                                                                                                                                                                                                 return new log();
                                                                                                                                                                                                                              }

                                                                                                                                                                                                                              return new timeAddSub();
                                                                                                                                                                                                                           }

                                                                                                                                                                                                                           return new dateformat();
                                                                                                                                                                                                                        }

                                                                                                                                                                                                                        return new getdate();
                                                                                                                                                                                                                     }

                                                                                                                                                                                                                     return new week();
                                                                                                                                                                                                                  }

                                                                                                                                                                                                                  return new extract();
                                                                                                                                                                                                               }
                                                                                                                                                                                                            }

                                                                                                                                                                                                            if (fname.trim().equalsIgnoreCase("TO_DSINTERVAL")) {
                                                                                                                                                                                                               return new ToDSInterval();
                                                                                                                                                                                                            } else if (fname.trim().equalsIgnoreCase("TO_YMINTERVAL")) {
                                                                                                                                                                                                               return new ToYMInterval();
                                                                                                                                                                                                            } else if ((isPostgreSql || isVectorWise) && trimFnName.equalsIgnoreCase("varchar")) {
                                                                                                                                                                                                               return new convert();
                                                                                                                                                                                                            } else if (trimFnName.equalsIgnoreCase("ascii") || trimFnName.equalsIgnoreCase("unicode") || isPostgreSql && trimFnName.equalsIgnoreCase("ord")) {
                                                                                                                                                                                                               return new ascii();
                                                                                                                                                                                                            } else if (trimFnName.equalsIgnoreCase("contains")) {
                                                                                                                                                                                                               return new contains();
                                                                                                                                                                                                            } else if (trimFnName.equalsIgnoreCase("bitand")) {
                                                                                                                                                                                                               return new bitand();
                                                                                                                                                                                                            } else if (trimFnName.equalsIgnoreCase("bitor")) {
                                                                                                                                                                                                               return new bitor();
                                                                                                                                                                                                            } else if (trimFnName.equalsIgnoreCase("user")) {
                                                                                                                                                                                                               return new user();
                                                                                                                                                                                                            } else if (trimFnName.equalsIgnoreCase("userenv")) {
                                                                                                                                                                                                               return new userenv();
                                                                                                                                                                                                            } else if (!trimFnName.equalsIgnoreCase("suser_sname") && !trimFnName.equalsIgnoreCase("suser_name")) {
                                                                                                                                                                                                               if (!trimFnName.equalsIgnoreCase("suser_sid") && !trimFnName.equalsIgnoreCase("suser_id") && !trimFnName.equalsIgnoreCase("user_id")) {
                                                                                                                                                                                                                  if (trimFnName.equalsIgnoreCase("decode")) {
                                                                                                                                                                                                                     return new decode();
                                                                                                                                                                                                                  } else if (!trimFnName.equalsIgnoreCase("isnull") && !trimFnName.equalsIgnoreCase("is_null")) {
                                                                                                                                                                                                                     if (!trimFnName.equalsIgnoreCase("nullif") && !trimFnName.equalsIgnoreCase("null_if")) {
                                                                                                                                                                                                                        if (trimFnName.equalsIgnoreCase("nvl2")) {
                                                                                                                                                                                                                           return new nvl2();
                                                                                                                                                                                                                        } else if (!trimFnName.equalsIgnoreCase("nvl") && !trimFnName.equalsIgnoreCase("coalesce")) {
                                                                                                                                                                                                                           if (trimFnName.equalsIgnoreCase("translate")) {
                                                                                                                                                                                                                              return new translate();
                                                                                                                                                                                                                           } else if (trimFnName.equalsIgnoreCase("convert")) {
                                                                                                                                                                                                                              return new convert();
                                                                                                                                                                                                                           } else if (trimFnName.equalsIgnoreCase("cast")) {
                                                                                                                                                                                                                              return new cast();
                                                                                                                                                                                                                           } else if (!trimFnName.equalsIgnoreCase("sys_guid") && !trimFnName.equalsIgnoreCase("newid")) {
                                                                                                                                                                                                                              if (!trimFnName.equalsIgnoreCase("greatest") && !trimFnName.equalsIgnoreCase("findmaxvalue") && !trimFnName.equalsIgnoreCase("find_max_value")) {
                                                                                                                                                                                                                                 if (!trimFnName.equalsIgnoreCase("least") && !trimFnName.equalsIgnoreCase("findminvalue") && !trimFnName.equalsIgnoreCase("find_min_value")) {
                                                                                                                                                                                                                                    if (trimFnName.equalsIgnoreCase("object_id")) {
                                                                                                                                                                                                                                       return new object_id();
                                                                                                                                                                                                                                    } else if (trimFnName.equalsIgnoreCase("serverproperty")) {
                                                                                                                                                                                                                                       return new serverproperty();
                                                                                                                                                                                                                                    } else if (!trimFnName.equalsIgnoreCase("db_id") && !trimFnName.equalsIgnoreCase("db_name") && !trimFnName.equalsIgnoreCase("host_id") && !trimFnName.equalsIgnoreCase("host_name")) {
                                                                                                                                                                                                                                       if (trimFnName.equalsIgnoreCase("is_srvrolemember")) {
                                                                                                                                                                                                                                          return new is_srvrolemember();
                                                                                                                                                                                                                                       } else if (trimFnName.equalsIgnoreCase("if")) {
                                                                                                                                                                                                                                          return new iffunction();
                                                                                                                                                                                                                                       } else if (trimFnName.equalsIgnoreCase("to_decision_box")) {
                                                                                                                                                                                                                                          return new todecisionbox();
                                                                                                                                                                                                                                       } else if (trimFnName.equalsIgnoreCase("if_case")) {
                                                                                                                                                                                                                                          return new ifmatches();
                                                                                                                                                                                                                                       } else if ((isPostgreSql || isVectorWise || isOracle) && trimFnName.equalsIgnoreCase("ifnull")) {
                                                                                                                                                                                                                                          return new ifnull();
                                                                                                                                                                                                                                       } else if (trimFnName.equalsIgnoreCase("hextoraw")) {
                                                                                                                                                                                                                                          return new hextoraw();
                                                                                                                                                                                                                                       } else if (!trimFnName.equalsIgnoreCase("grouping_id") && !fname.trim().equalsIgnoreCase("group_id")) {
                                                                                                                                                                                                                                          if (trimFnName.equalsIgnoreCase("hashbytes")) {
                                                                                                                                                                                                                                             return new Hashbytes();
                                                                                                                                                                                                                                          } else if (trimFnName.equalsIgnoreCase("sum")) {
                                                                                                                                                                                                                                             return new sum();
                                                                                                                                                                                                                                          } else if (!trimFnName.equalsIgnoreCase("sumif") && !trimFnName.equalsIgnoreCase("sum_if")) {
                                                                                                                                                                                                                                             if (trimFnName.equalsIgnoreCase("avg")) {
                                                                                                                                                                                                                                                return new avg();
                                                                                                                                                                                                                                             } else if (!trimFnName.equalsIgnoreCase("avgif") && !trimFnName.equalsIgnoreCase("avg_if")) {
                                                                                                                                                                                                                                                if (trimFnName.equalsIgnoreCase("count")) {
                                                                                                                                                                                                                                                   return new count();
                                                                                                                                                                                                                                                } else if (!trimFnName.equalsIgnoreCase("countif") && !trimFnName.equalsIgnoreCase("count_if")) {
                                                                                                                                                                                                                                                   if (trimFnName.equalsIgnoreCase("count_wb")) {
                                                                                                                                                                                                                                                      return new aggregateIf();
                                                                                                                                                                                                                                                   } else if (!trimFnName.equalsIgnoreCase("distinctcount") && !trimFnName.equalsIgnoreCase("count_distinct")) {
                                                                                                                                                                                                                                                      if (trimFnName.equalsIgnoreCase("ytd")) {
                                                                                                                                                                                                                                                         return new ytd();
                                                                                                                                                                                                                                                      } else if (trimFnName.equalsIgnoreCase("qtd")) {
                                                                                                                                                                                                                                                         return new qtd();
                                                                                                                                                                                                                                                      } else if (trimFnName.equalsIgnoreCase("mtd")) {
                                                                                                                                                                                                                                                         return new mtd();
                                                                                                                                                                                                                                                      } else if (trimFnName.equalsIgnoreCase("max")) {
                                                                                                                                                                                                                                                         return new max();
                                                                                                                                                                                                                                                      } else if (trimFnName.equalsIgnoreCase("min")) {
                                                                                                                                                                                                                                                         return new min();
                                                                                                                                                                                                                                                      } else if (!trimFnName.equalsIgnoreCase("stddev") && !trimFnName.equalsIgnoreCase("stdev") && !trimFnName.equalsIgnoreCase("std") && !trimFnName.equalsIgnoreCase("stddev_pop") && !trimFnName.equalsIgnoreCase("stddev_samp") && !trimFnName.equalsIgnoreCase("stddev_sample")) {
                                                                                                                                                                                                                                                         if (!trimFnName.equalsIgnoreCase("variance") && !trimFnName.equalsIgnoreCase("var") && !trimFnName.equalsIgnoreCase("varp") && !trimFnName.equalsIgnoreCase("var_pop") && !trimFnName.equalsIgnoreCase("var_samp") && !trimFnName.equalsIgnoreCase("variance_samp") && !trimFnName.equalsIgnoreCase("variance_sample") && !trimFnName.equalsIgnoreCase("corr") && !trimFnName.equalsIgnoreCase("covar_pop") && !trimFnName.equalsIgnoreCase("covariance")) {
                                                                                                                                                                                                                                                            if (!trimFnName.equalsIgnoreCase("regr_intercept") && !trimFnName.equalsIgnoreCase("regr_r2") && !trimFnName.equalsIgnoreCase("regr_slope") && !trimFnName.equalsIgnoreCase("regr_avgx") && !trimFnName.equalsIgnoreCase("regr_avgy")) {
                                                                                                                                                                                                                                                               if ((trimFnName.equalsIgnoreCase("group_concat") || trimFnName.equalsIgnoreCase("substring_index")) && isVectorWise) {
                                                                                                                                                                                                                                                                  return new repeat();
                                                                                                                                                                                                                                                               } else if (!isMySql && !isPostgreSql && !isVectorWise && !isOracle && !isSnowflake || !trimFnName.equalsIgnoreCase("mode") && !trimFnName.equalsIgnoreCase("stats_mode")) {
                                                                                                                                                                                                                                                                  if ((isMySql || isPostgreSql || isVectorWise || isMsAzure || isOracle || isBigQuery || isSnowflake || isDB2 || isAthena || isSapHana || isSqlite) && trimFnName.equalsIgnoreCase("median")) {
                                                                                                                                                                                                                                                                     return new median();
                                                                                                                                                                                                                                                                  } else if ((isMySql || isPostgreSql || isVectorWise) && trimFnName.equalsIgnoreCase("mean")) {
                                                                                                                                                                                                                                                                     return new mean();
                                                                                                                                                                                                                                                                  } else if ((isMySql || isPostgreSql || isVectorWise || isMsAzure || isOracle || isBigQuery || isSnowflake || isDB2 || isAthena || isSapHana) && trimFnName.equalsIgnoreCase("percentile")) {
                                                                                                                                                                                                                                                                     return new percentile();
                                                                                                                                                                                                                                                                  } else if ((isPostgreSql || isVectorWise || isMsAzure || isOracle || isBigQuery || isSnowflake || isDB2 || isAthena || isSapHana) && trimFnName.equalsIgnoreCase("percentile_cont")) {
                                                                                                                                                                                                                                                                     return new percentile();
                                                                                                                                                                                                                                                                  } else if (isPostgreSql && trimFnName.equalsIgnoreCase("group_concat")) {
                                                                                                                                                                                                                                                                     return new groupConcat();
                                                                                                                                                                                                                                                                  } else if (isPostgreSql && trimFnName.equalsIgnoreCase("substring_index")) {
                                                                                                                                                                                                                                                                     return new substringIndex();
                                                                                                                                                                                                                                                                  } else if ((!isPostgreSql || !trimFnName.equalsIgnoreCase("elt") && !trimFnName.equalsIgnoreCase("field") && !trimFnName.equalsIgnoreCase("find_in_set") && !trimFnName.equalsIgnoreCase("make_set")) && (!isVectorWise || !trimFnName.equalsIgnoreCase("elt") && !trimFnName.equalsIgnoreCase("field"))) {
                                                                                                                                                                                                                                                                     if (!trimFnName.equalsIgnoreCase("first_value") && !trimFnName.equalsIgnoreCase("first")) {
                                                                                                                                                                                                                                                                        if (!trimFnName.equalsIgnoreCase("last_value") && !trimFnName.equalsIgnoreCase("last")) {
                                                                                                                                                                                                                                                                           if (trimFnName.equalsIgnoreCase("dense_rank")) {
                                                                                                                                                                                                                                                                              return new DenseRank();
                                                                                                                                                                                                                                                                           } else if (trimFnName.equalsIgnoreCase("lead")) {
                                                                                                                                                                                                                                                                              return new Lead();
                                                                                                                                                                                                                                                                           } else if (trimFnName.equalsIgnoreCase("lag")) {
                                                                                                                                                                                                                                                                              return new Lag();
                                                                                                                                                                                                                                                                           } else if (!trimFnName.equalsIgnoreCase("ntile") && !trimFnName.equalsIgnoreCase("n_tile")) {
                                                                                                                                                                                                                                                                              if (trimFnName.equalsIgnoreCase("Ratio_To_Report")) {
                                                                                                                                                                                                                                                                                 return new Ratio_To_Report();
                                                                                                                                                                                                                                                                              } else if (trimFnName.equalsIgnoreCase("cume_dist")) {
                                                                                                                                                                                                                                                                                 return new Cume_Dist();
                                                                                                                                                                                                                                                                              } else if (!trimFnName.equalsIgnoreCase("rank") && !trimFnName.equalsIgnoreCase("percent_rank")) {
                                                                                                                                                                                                                                                                                 if (trimFnName.equalsIgnoreCase("row_number")) {
                                                                                                                                                                                                                                                                                    return new RowNumber();
                                                                                                                                                                                                                                                                                 } else if (trimFnName.equalsIgnoreCase("nth_value")) {
                                                                                                                                                                                                                                                                                    return new Nth_Value();
                                                                                                                                                                                                                                                                                 } else if (!trimFnName.equalsIgnoreCase("include_groupby") && !trimFnName.equalsIgnoreCase("exclude_groupby") && !trimFnName.equalsIgnoreCase("fixed_groupby") && !trimFnName.equalsIgnoreCase("map_groupby") && !trimFnName.equalsIgnoreCase("ignore_filters")) {
                                                                                                                                                                                                                                                                                    if (trimFnName.equalsIgnoreCase("ZR_fyear")) {
                                                                                                                                                                                                                                                                                       return new fiscalYear();
                                                                                                                                                                                                                                                                                    } else if (trimFnName.equalsIgnoreCase("ZR_fyeardt")) {
                                                                                                                                                                                                                                                                                       return new fiscalYear();
                                                                                                                                                                                                                                                                                    } else if (trimFnName.equalsIgnoreCase("ZR_fyearintrval")) {
                                                                                                                                                                                                                                                                                       return new fiscalYear();
                                                                                                                                                                                                                                                                                    } else if (trimFnName.equalsIgnoreCase("ZR_fquarter")) {
                                                                                                                                                                                                                                                                                       return new fiscalQuarter();
                                                                                                                                                                                                                                                                                    } else if (trimFnName.equalsIgnoreCase("ZR_fquarterdt")) {
                                                                                                                                                                                                                                                                                       return new fiscalQuarter();
                                                                                                                                                                                                                                                                                    } else if (trimFnName.equalsIgnoreCase("ZR_fquarterintrval")) {
                                                                                                                                                                                                                                                                                       return new fiscalQuarter();
                                                                                                                                                                                                                                                                                    } else if (trimFnName.equalsIgnoreCase("ZR_fquarteryear")) {
                                                                                                                                                                                                                                                                                       return new fiscalQuarterYear();
                                                                                                                                                                                                                                                                                    } else if (trimFnName.equalsIgnoreCase("ZR_fquarteryeardt")) {
                                                                                                                                                                                                                                                                                       return new fiscalQuarterYear();
                                                                                                                                                                                                                                                                                    } else if (trimFnName.equalsIgnoreCase("ZR_fquarteryearintrval")) {
                                                                                                                                                                                                                                                                                       return new fiscalQuarterYear();
                                                                                                                                                                                                                                                                                    } else if (trimFnName.equalsIgnoreCase("ZR_fweek")) {
                                                                                                                                                                                                                                                                                       return new fiscalWeek();
                                                                                                                                                                                                                                                                                    } else if (trimFnName.equalsIgnoreCase("ZR_fweekdtNwkstrtday")) {
                                                                                                                                                                                                                                                                                       return new fiscalWeek();
                                                                                                                                                                                                                                                                                    } else if (trimFnName.equalsIgnoreCase("ZR_fweekintrvalNwkstrtday")) {
                                                                                                                                                                                                                                                                                       return new fiscalWeek();
                                                                                                                                                                                                                                                                                    } else if (trimFnName.equalsIgnoreCase("ZR_fweekdt")) {
                                                                                                                                                                                                                                                                                       return new fiscalWeek();
                                                                                                                                                                                                                                                                                    } else if (trimFnName.equalsIgnoreCase("ZR_fweekintrval")) {
                                                                                                                                                                                                                                                                                       return new fiscalWeek();
                                                                                                                                                                                                                                                                                    } else if (trimFnName.equalsIgnoreCase("ZR_fweekyear")) {
                                                                                                                                                                                                                                                                                       return new fiscalWeek();
                                                                                                                                                                                                                                                                                    } else if (trimFnName.equalsIgnoreCase("ZR_fweekyeardtNwkstrtday")) {
                                                                                                                                                                                                                                                                                       return new fiscalWeek();
                                                                                                                                                                                                                                                                                    } else if (trimFnName.equalsIgnoreCase("ZR_fweekyearintrvalNwkstrtday")) {
                                                                                                                                                                                                                                                                                       return new fiscalWeek();
                                                                                                                                                                                                                                                                                    } else if (trimFnName.equalsIgnoreCase("ZR_fweekyeardt")) {
                                                                                                                                                                                                                                                                                       return new fiscalWeek();
                                                                                                                                                                                                                                                                                    } else if (trimFnName.equalsIgnoreCase("ZR_fweekyearintrval")) {
                                                                                                                                                                                                                                                                                       return new fiscalWeek();
                                                                                                                                                                                                                                                                                    } else if (trimFnName.equalsIgnoreCase("ZR_WeekYearDtNwkStrtDay")) {
                                                                                                                                                                                                                                                                                       return new startWeek();
                                                                                                                                                                                                                                                                                    } else if (trimFnName.equalsIgnoreCase("ZR_WeekDtNwkStrtDay")) {
                                                                                                                                                                                                                                                                                       return new startWeek();
                                                                                                                                                                                                                                                                                    } else if (trimFnName.equalsIgnoreCase("ZR_WeekYearIntrvalNwkStrtDay")) {
                                                                                                                                                                                                                                                                                       return new startWeek();
                                                                                                                                                                                                                                                                                    } else if (trimFnName.equalsIgnoreCase("ZR_WeekIntrvalNwkStrtDay")) {
                                                                                                                                                                                                                                                                                       return new startWeek();
                                                                                                                                                                                                                                                                                    } else if (trimFnName.equalsIgnoreCase("ZR_date_trunc")) {
                                                                                                                                                                                                                                                                                       return new dateTrunc();
                                                                                                                                                                                                                                                                                    } else if (trimFnName.equalsIgnoreCase("ZR_ISPREVIOUSMONTH")) {
                                                                                                                                                                                                                                                                                       return new previousDate();
                                                                                                                                                                                                                                                                                    } else if (trimFnName.equalsIgnoreCase("ZR_ISPREVIOUSQUARTER")) {
                                                                                                                                                                                                                                                                                       return new previousDate();
                                                                                                                                                                                                                                                                                    } else if (trimFnName.equalsIgnoreCase("ZR_ISPREVIOUSWEEK")) {
                                                                                                                                                                                                                                                                                       return new previousDate();
                                                                                                                                                                                                                                                                                    } else if (trimFnName.equalsIgnoreCase("ZR_ISLASTMONTH")) {
                                                                                                                                                                                                                                                                                       return new lastDate();
                                                                                                                                                                                                                                                                                    } else if (trimFnName.equalsIgnoreCase("ZR_ISLASTQUARTER")) {
                                                                                                                                                                                                                                                                                       return new lastDate();
                                                                                                                                                                                                                                                                                    } else if (trimFnName.equalsIgnoreCase("ZR_ISNEXTMONTH")) {
                                                                                                                                                                                                                                                                                       return new nextDate();
                                                                                                                                                                                                                                                                                    } else if (trimFnName.equalsIgnoreCase("ZR_ISNEXTQUARTER")) {
                                                                                                                                                                                                                                                                                       return new nextDate();
                                                                                                                                                                                                                                                                                    } else if (trimFnName.equalsIgnoreCase("ZR_ISNEXTWEEK")) {
                                                                                                                                                                                                                                                                                       return new nextDate();
                                                                                                                                                                                                                                                                                    } else if (trimFnName.equalsIgnoreCase("ZR_ISCURRENTWEEK")) {
                                                                                                                                                                                                                                                                                       return new currentDate();
                                                                                                                                                                                                                                                                                    } else if (trimFnName.equalsIgnoreCase("ZR_BUSINESS_DAYS")) {
                                                                                                                                                                                                                                                                                       return new businessDate();
                                                                                                                                                                                                                                                                                    } else if (trimFnName.equalsIgnoreCase("ZR_BUSINESS_HOURS")) {
                                                                                                                                                                                                                                                                                       return new businessDate();
                                                                                                                                                                                                                                                                                    } else if (trimFnName.equalsIgnoreCase("ZR_BUSINESS_MINUTES")) {
                                                                                                                                                                                                                                                                                       return new businessDate();
                                                                                                                                                                                                                                                                                    } else if (trimFnName.equalsIgnoreCase("ZR_BUSINESS_ENDDAY")) {
                                                                                                                                                                                                                                                                                       return new businessDate();
                                                                                                                                                                                                                                                                                    } else if (trimFnName.equalsIgnoreCase("ZR_TEXTBETWEEN")) {
                                                                                                                                                                                                                                                                                       return new substring_between();
                                                                                                                                                                                                                                                                                    } else if (trimFnName.equalsIgnoreCase("ZR_SECTOTIME")) {
                                                                                                                                                                                                                                                                                       return new SecToTime();
                                                                                                                                                                                                                                                                                    } else if (trimFnName.equalsIgnoreCase("to_email")) {
                                                                                                                                                                                                                                                                                       return new toemail();
                                                                                                                                                                                                                                                                                    } else {
                                                                                                                                                                                                                                                                                       return null;
                                                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                                                 } else {
                                                                                                                                                                                                                                                                                    return new Tabular();
                                                                                                                                                                                                                                                                                 }
                                                                                                                                                                                                                                                                              } else {
                                                                                                                                                                                                                                                                                 return new Rank();
                                                                                                                                                                                                                                                                              }
                                                                                                                                                                                                                                                                           } else {
                                                                                                                                                                                                                                                                              return new Ntile();
                                                                                                                                                                                                                                                                           }
                                                                                                                                                                                                                                                                        } else {
                                                                                                                                                                                                                                                                           return new LastValue();
                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                     } else {
                                                                                                                                                                                                                                                                        return new FirstValue();
                                                                                                                                                                                                                                                                     }
                                                                                                                                                                                                                                                                  } else {
                                                                                                                                                                                                                                                                     return new set();
                                                                                                                                                                                                                                                                  }
                                                                                                                                                                                                                                                               } else {
                                                                                                                                                                                                                                                                  return new mode();
                                                                                                                                                                                                                                                               }
                                                                                                                                                                                                                                                            } else {
                                                                                                                                                                                                                                                               return new regression();
                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                         } else {
                                                                                                                                                                                                                                                            return new variance();
                                                                                                                                                                                                                                                         }
                                                                                                                                                                                                                                                      } else {
                                                                                                                                                                                                                                                         return new stddeviation();
                                                                                                                                                                                                                                                      }
                                                                                                                                                                                                                                                   } else {
                                                                                                                                                                                                                                                      return new aggregateIf();
                                                                                                                                                                                                                                                   }
                                                                                                                                                                                                                                                } else {
                                                                                                                                                                                                                                                   return new aggregateIf();
                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                             } else {
                                                                                                                                                                                                                                                return new aggregateIf();
                                                                                                                                                                                                                                             }
                                                                                                                                                                                                                                          } else {
                                                                                                                                                                                                                                             return new aggregateIf();
                                                                                                                                                                                                                                          }
                                                                                                                                                                                                                                       } else {
                                                                                                                                                                                                                                          return new grouping_id();
                                                                                                                                                                                                                                       }
                                                                                                                                                                                                                                    } else {
                                                                                                                                                                                                                                       return new sys_context();
                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                 } else {
                                                                                                                                                                                                                                    return new least();
                                                                                                                                                                                                                                 }
                                                                                                                                                                                                                              } else {
                                                                                                                                                                                                                                 return new greatest();
                                                                                                                                                                                                                              }
                                                                                                                                                                                                                           } else {
                                                                                                                                                                                                                              return new sysguid();
                                                                                                                                                                                                                           }
                                                                                                                                                                                                                        } else {
                                                                                                                                                                                                                           return new nvl();
                                                                                                                                                                                                                        }
                                                                                                                                                                                                                     } else {
                                                                                                                                                                                                                        return new nullif();
                                                                                                                                                                                                                     }
                                                                                                                                                                                                                  } else {
                                                                                                                                                                                                                     return new isnull();
                                                                                                                                                                                                                  }
                                                                                                                                                                                                               } else {
                                                                                                                                                                                                                  return new suser_sid();
                                                                                                                                                                                                               }
                                                                                                                                                                                                            } else {
                                                                                                                                                                                                               return new suser_sname();
                                                                                                                                                                                                            }
                                                                                                                                                                                                         } else {
                                                                                                                                                                                                            return new extract_json();
                                                                                                                                                                                                         }
                                                                                                                                                                                                      } else {
                                                                                                                                                                                                         return new timestampdiffinduration();
                                                                                                                                                                                                      }
                                                                                                                                                                                                   } else {
                                                                                                                                                                                                      return new TimeToSec();
                                                                                                                                                                                                   }
                                                                                                                                                                                                } else {
                                                                                                                                                                                                   return new conv();
                                                                                                                                                                                                }
                                                                                                                                                                                             } else {
                                                                                                                                                                                                return new FromTZ();
                                                                                                                                                                                             }
                                                                                                                                                                                          } else {
                                                                                                                                                                                             return new MakeDate();
                                                                                                                                                                                          }
                                                                                                                                                                                       } else {
                                                                                                                                                                                          return new extract();
                                                                                                                                                                                       }
                                                                                                                                                                                    } else {
                                                                                                                                                                                       return new gettime();
                                                                                                                                                                                    }
                                                                                                                                                                                 } else {
                                                                                                                                                                                    return new getdate();
                                                                                                                                                                                 }
                                                                                                                                                                              } else {
                                                                                                                                                                                 return new timestampAdd();
                                                                                                                                                                              }
                                                                                                                                                                           } else {
                                                                                                                                                                              return new timestampDiff();
                                                                                                                                                                           }
                                                                                                                                                                        } else {
                                                                                                                                                                           return new dateadd();
                                                                                                                                                                        }
                                                                                                                                                                     } else {
                                                                                                                                                                        return new getdate();
                                                                                                                                                                     }
                                                                                                                                                                  } else {
                                                                                                                                                                     return new hex();
                                                                                                                                                                  }
                                                                                                                                                               } else {
                                                                                                                                                                  return new TimeToSec();
                                                                                                                                                               }
                                                                                                                                                            } else {
                                                                                                                                                               return new extract();
                                                                                                                                                            }
                                                                                                                                                         } else {
                                                                                                                                                            return new DecInt();
                                                                                                                                                         }
                                                                                                                                                      } else {
                                                                                                                                                         return new groupFirstLast();
                                                                                                                                                      }
                                                                                                                                                   } else {
                                                                                                                                                      return new week();
                                                                                                                                                   }
                                                                                                                                                } else {
                                                                                                                                                   return new AbsoluteWeek();
                                                                                                                                                }
                                                                                                                                             } else {
                                                                                                                                                return new AbsoluteMonth();
                                                                                                                                             }
                                                                                                                                          } else {
                                                                                                                                             return new AbsoluteQuarter();
                                                                                                                                          }
                                                                                                                                       } else {
                                                                                                                                          return new WeekOfMonth();
                                                                                                                                       }
                                                                                                                                    } else {
                                                                                                                                       return new FirstDateCurrentWeek();
                                                                                                                                    }
                                                                                                                                 } else {
                                                                                                                                    return new SysDateTime();
                                                                                                                                 }
                                                                                                                              } else {
                                                                                                                                 return new quarter();
                                                                                                                              }
                                                                                                                           } else {
                                                                                                                              return new start_datetime();
                                                                                                                           }
                                                                                                                        } else {
                                                                                                                           return new businessDate();
                                                                                                                        }
                                                                                                                     } else {
                                                                                                                        return new currentDate();
                                                                                                                     }
                                                                                                                  } else {
                                                                                                                     return new nextDate();
                                                                                                                  }
                                                                                                               } else {
                                                                                                                  return new lastDate();
                                                                                                               }
                                                                                                            } else {
                                                                                                               return new previousDate();
                                                                                                            }
                                                                                                         } else {
                                                                                                            return new addToDate();
                                                                                                         }
                                                                                                      } else {
                                                                                                         return new dayofquarter();
                                                                                                      }
                                                                                                   } else {
                                                                                                      return new month();
                                                                                                   }
                                                                                                } else {
                                                                                                   return new datepart();
                                                                                                }
                                                                                             } else {
                                                                                                return new monthdiff();
                                                                                             }
                                                                                          } else {
                                                                                             return new dateadd();
                                                                                          }
                                                                                       } else {
                                                                                          return new todate();
                                                                                       }
                                                                                    } else {
                                                                                       return new getdate();
                                                                                    }
                                                                                 } else {
                                                                                    return new trunc();
                                                                                 }
                                                                              } else {
                                                                                 return new atan2();
                                                                              }
                                                                           } else {
                                                                              return new trigh();
                                                                           }
                                                                        } else {
                                                                           return new trig();
                                                                        }
                                                                     } else {
                                                                        return new ceil();
                                                                     }
                                                                  } else {
                                                                     return new rand();
                                                                  }
                                                               } else {
                                                                  return new log();
                                                               }
                                                            } else {
                                                               return new power();
                                                            }
                                                         } else {
                                                            return new strsearch();
                                                         }
                                                      } else {
                                                         return new substring_between();
                                                      }
                                                   } else {
                                                      return new indexof();
                                                   }
                                                } else {
                                                   return new chr();
                                                }
                                             } else {
                                                return new concat();
                                             }
                                          } else {
                                             return new rpad();
                                          }
                                       } else {
                                          return new repeat();
                                       }
                                    } else {
                                       return new right();
                                    }
                                 } else {
                                    return new left();
                                 }
                              } else {
                                 return new rtrim();
                              }
                           } else {
                              return new ltrim();
                           }
                        } else {
                           return new lpad();
                        }
                     } else {
                        return new replace();
                     }
                  } else {
                     return new upper();
                  }
               } else {
                  return new lower();
               }
            } else {
               return new instr();
            }
         } else {
            return new length();
         }
      } else {
         return new substring();
      }
   }

   public void toOracle() throws ConvertException {
   }

   public void toMSSQLServer() throws ConvertException {
   }

   public void toSybase() throws ConvertException {
   }

   public void toDB2() throws ConvertException {
   }

   public void toPostgreSQL() throws ConvertException {
   }

   public void toMySQL() throws ConvertException {
   }

   public void toANSISQL() throws ConvertException {
   }

   public void toInformix() throws ConvertException {
   }

   public void toTimesTen() throws ConvertException {
   }

   public void toNetezza() throws ConvertException {
   }

   public void toTeradata() throws ConvertException {
   }

   public void toVectorWise() throws ConvertException {
   }

   public void toOracle(SelectQueryStatement to, SelectQueryStatement from) throws ConvertException {
   }

   public void toMSSQLServer(SelectQueryStatement to, SelectQueryStatement from) throws ConvertException {
   }

   public void toSybase(SelectQueryStatement to, SelectQueryStatement from) throws ConvertException {
   }

   public void toDB2(SelectQueryStatement to, SelectQueryStatement from) throws ConvertException {
   }

   public void toPostgreSQL(SelectQueryStatement to, SelectQueryStatement from) throws ConvertException {
   }

   public void toMySQL(SelectQueryStatement to, SelectQueryStatement from) throws ConvertException {
   }

   public void toANSISQL(SelectQueryStatement to, SelectQueryStatement from) throws ConvertException {
   }

   public void toInformix(SelectQueryStatement to, SelectQueryStatement from) throws ConvertException {
   }

   public void toTimesTen(SelectQueryStatement to, SelectQueryStatement from) throws ConvertException {
   }

   public void toNetezza(SelectQueryStatement to, SelectQueryStatement from) throws ConvertException {
   }

   public void toTeradata(SelectQueryStatement to, SelectQueryStatement from) throws ConvertException {
   }

   public void toVectorWise(SelectQueryStatement to, SelectQueryStatement from) throws ConvertException {
   }

   public void toBigQuery(SelectQueryStatement to, SelectQueryStatement from) throws ConvertException {
   }

   public void toSnowflake(SelectQueryStatement to, SelectQueryStatement from) throws ConvertException {
   }

   public void toAthena(SelectQueryStatement to, SelectQueryStatement from) throws ConvertException {
   }

   public void toSapHana(SelectQueryStatement to, SelectQueryStatement from) throws ConvertException {
   }

   public void toSqlite(SelectQueryStatement to, SelectQueryStatement from) throws ConvertException {
   }

   public void toExcel(SelectQueryStatement to, SelectQueryStatement from) throws ConvertException {
   }

   public void toMsAccess(SelectQueryStatement to, SelectQueryStatement from) throws ConvertException {
   }

   public boolean getOuterJoin() {
      return this.outerJoin;
   }

   public void setOuterJoin(boolean oj) {
      this.outerJoin = oj;
   }

   private boolean isSQLServerSystemFunction(String functionname) {
      String[] arr = SwisSQLUtils.getSystemFunctions(2);

      for(int i = 0; i < arr.length; ++i) {
         if (arr[i].equalsIgnoreCase(functionname)) {
            return true;
         }
      }

      return false;
   }

   private boolean isSybaseSystemFunction(String functionname) {
      String[] arr = SwisSQLUtils.getSystemFunctions(7);

      for(int i = 0; i < arr.length; ++i) {
         if (arr[i].equalsIgnoreCase(functionname)) {
            return true;
         }
      }

      return false;
   }

   public FromTable createTeradataDerivedTable(SelectQueryStatement to_sqs, FunctionCalls fnCall, SelectColumn functionArgument, String alias) throws ConvertException {
      return null;
   }

   private void handleKeepDenseRank(SelectQueryStatement to_sqs, SelectQueryStatement from_sqs, FunctionCalls aggrFn) throws ConvertException {
      boolean createNewDerivedTable = false;
      String alias = from_sqs.getFromClause().getLastElement().getAliasName();
      String idx = "" + from_sqs.getOlapDerivedTables().size();
      String keepDenseRank = "keepdenserank" + aggrFn.getFunctionName() + this.getOrderBy();
      if (this.getPartitionByClause() != null) {
         String partitionString = "keepdenserank" + this.getPartitionByClause().toString() + aggrFn.getFunctionName() + this.getOrderBy();
         if (!from_sqs.getOlapDerivedTables().containsKey(partitionString)) {
            createNewDerivedTable = true;
         }
      } else if (!from_sqs.getOlapDerivedTables().containsKey(keepDenseRank)) {
         createNewDerivedTable = true;
      }

      Vector gbsItems;
      if (createNewDerivedTable) {
         SelectQueryStatement derivedTable = new SelectQueryStatement();
         SelectStatement selectStatement = new SelectStatement();
         selectStatement.setSelectClause("SELECT");
         Vector selectItemsList = new Vector();
         Vector newWhereItemList = new Vector();
         HavingStatement qualifyStmt = new HavingStatement();
         qualifyStmt.setHavingClause("QUALIFY");
         WhereExpression qualifyExpression = new WhereExpression();
         Vector qualifyItems = new Vector();
         GroupByStatement gbs = new GroupByStatement();
         gbs.setGroupClause("GROUP BY");
         gbsItems = new Vector();
         QueryPartitionClause qpc = new QueryPartitionClause();
         qpc.setPartitionBy("PARTITION BY");
         ArrayList qpcSelCols = new ArrayList();
         Vector tableColumns = from_sqs.getSelectStatement().getSelectItemList();

         SelectColumn newAggrFnArg;
         for(int tcni = 0; tcni < tableColumns.size(); ++tcni) {
            SelectColumn tcn = (SelectColumn)tableColumns.get(tcni);
            Vector tcnColExp = tcn.getColumnExpression();

            for(int j = 0; j < tcnColExp.size(); ++j) {
               Object jObj = tcnColExp.get(j);
               if (jObj instanceof TableColumn) {
                  newAggrFnArg = new SelectColumn();
                  newAggrFnArg.setColumnExpression(tcnColExp);
                  newAggrFnArg = newAggrFnArg.toTeradataSelect((SelectQueryStatement)null, (SelectQueryStatement)null);
                  newAggrFnArg.setAliasName("ADV_ALIAS_" + tcni);
                  newAggrFnArg.setEndsWith(",");
                  selectItemsList.add(newAggrFnArg);
                  SelectColumn gbsTCN = new SelectColumn();
                  gbsTCN.setColumnExpression(newAggrFnArg.getColumnExpression());
                  gbsItems.add(gbsTCN);
                  SelectColumn qpcTCN = new SelectColumn();
                  qpcTCN.setColumnExpression(newAggrFnArg.getColumnExpression());
                  qpcTCN.setEndsWith(",");
                  qpcSelCols.add(qpcTCN);
                  String whereidx = "";
                  if (from_sqs.getOlapDerivedTables().size() > 0) {
                     whereidx = "" + (from_sqs.getOlapDerivedTables().size() - 1);
                  } else {
                     whereidx = "" + from_sqs.getOlapDerivedTables().size();
                  }

                  newWhereItemList.add(this.generateWhereItems(tcn.toTeradataSelect((SelectQueryStatement)null, (SelectQueryStatement)null), alias + whereidx, newAggrFnArg.getAliasName()));
                  break;
               }
            }
         }

         ((SelectColumn)qpcSelCols.get(qpcSelCols.size() - 1)).setEndsWith((String)null);
         qpc.setSelectColumnList(qpcSelCols);
         gbs.setGroupByItemList(gbsItems);
         if (aggrFn.getPartitionByClause() != null) {
            newWhereItemList.clear();
            ArrayList selColsList = aggrFn.getPartitionByClause().getSelectColumnList();

            for(int k = 0; k < selColsList.size(); ++k) {
               if (selColsList.get(k) instanceof SelectColumn) {
                  SelectColumn partSelCol = (SelectColumn)selColsList.get(k);
                  SelectColumn newPartSelCol = new SelectColumn();
                  newPartSelCol.setColumnExpression(partSelCol.getColumnExpression());
                  newPartSelCol.setAliasName("partition_" + k);
                  newPartSelCol.setEndsWith(",");
                  newWhereItemList.add(this.generateWhereItems(partSelCol, alias + idx, "partition_" + k));
                  selectItemsList.add(newPartSelCol);
               }
            }
         } else {
            aggrFn.setPartitionByClause(qpc);
         }

         if (from_sqs.getGroupByStatement() == null && to_sqs.getGroupByStatement() == null) {
            to_sqs.setGroupByStatement(gbs);
         }

         SelectColumn rownumSelCol = new SelectColumn();
         FunctionCalls rownumFunc = new FunctionCalls();
         TableColumn rownumFuncName = new TableColumn();
         rownumFuncName.setColumnName("ROW_NUMBER");
         rownumFunc.setFunctionName(rownumFuncName);
         rownumFunc.setFunctionArguments(new Vector());
         if (aggrFn.getPartitionByClause() != null) {
            rownumFunc.setPartitionByClause(aggrFn.getPartitionByClause());
         }

         if (aggrFn.getOrderBy() != null) {
            OrderByStatement obs = aggrFn.getOrderBy();
            if (aggrFn.getLast() != null) {
               Vector orderItemList = obs.getOrderItemList();

               for(int i_count = 0; i_count < orderItemList.size(); ++i_count) {
                  OrderItem oi = (OrderItem)orderItemList.elementAt(i_count);
                  if (oi != null) {
                     String orderType = oi.getOrder();
                     if (orderType != null && orderType.equalsIgnoreCase("ASC")) {
                        oi.setOrder("DESC");
                     }

                     if (orderType != null && orderType.equalsIgnoreCase("DESC")) {
                        oi.setOrder("ASC");
                     } else if (orderType == null) {
                        oi.setOrder("DESC");
                     }
                  }
               }
            }

            rownumFunc.setOrderBy(obs);
         }

         rownumFunc.setOver("OVER");
         Vector rownumSelColExp = new Vector();
         rownumSelColExp.add(rownumFunc);
         rownumSelCol.setColumnExpression(rownumSelColExp);
         rownumSelCol.setAliasName("rownum_0");
         rownumSelCol.setEndsWith(",");
         selectItemsList.add(rownumSelCol);
         SelectColumn aggrFnArg = (SelectColumn)aggrFn.getFunctionArguments().firstElement();
         aggrFnArg.setAliasName("denserank_" + alias + idx);
         aggrFnArg.setEndsWith(",");
         selectItemsList.add(aggrFnArg);
         newAggrFnArg = new SelectColumn();
         Vector newAggrFnArgExp = new Vector();
         TableColumn newAggrFnArgCol = new TableColumn();
         newAggrFnArgCol.setColumnName("denserank_" + alias + idx);
         newAggrFnArgExp.add(newAggrFnArgCol);
         newAggrFnArg.setColumnExpression(newAggrFnArgExp);
         Vector newFunctionArgs = new Vector();
         newFunctionArgs.add(newAggrFnArg);
         aggrFn.setFunctionArguments(newFunctionArgs);
         ((SelectColumn)selectItemsList.lastElement()).setEndsWith((String)null);
         selectStatement.setSelectItemList(selectItemsList);
         derivedTable.setSelectStatement(selectStatement);
         WhereItem wi = new WhereItem();
         WhereColumn lwc = new WhereColumn();
         Vector lwcColExp = new Vector();
         TableColumn tc1 = new TableColumn();
         tc1.setColumnName(rownumSelCol.getAliasName());
         lwcColExp.add(tc1);
         WhereColumn rwc = new WhereColumn();
         Vector rwcColExp = new Vector();
         rwcColExp.add("1");
         lwc.setColumnExpression(lwcColExp);
         rwc.setColumnExpression(rwcColExp);
         wi.setLeftWhereExp(lwc);
         wi.setRightWhereExp(rwc);
         wi.setOperator("=");
         qualifyExpression.addWhereItem(wi);
         qualifyItems.add(qualifyExpression);
         qualifyStmt.setHavingItems(qualifyItems);
         derivedTable.setHavingStatement(qualifyStmt);
         FromTable derivedTableFromItem = new FromTable();
         derivedTableFromItem.setTableName(derivedTable);
         derivedTableFromItem.setAliasName(alias + idx);
         derivedTableFromItem.setJoinClause("INNER JOIN ");
         derivedTableFromItem.setOnOrUsingJoin("ON");
         Vector joinCondition = new Vector();
         WhereExpression we = new WhereExpression();
         we.setWhereItem(newWhereItemList);
         Vector operators = new Vector();

         for(int s = 0; s < newWhereItemList.size() - 1; ++s) {
            operators.add("AND");
         }

         we.setOperator(operators);
         joinCondition.add(we);
         derivedTableFromItem.setJoinExpression(joinCondition);
         aggrFn.setKeep((String)null);
         aggrFn.setFirst((String)null);
         aggrFn.setLast((String)null);
         aggrFn.setDenseRank((String)null);
         aggrFn.setOver((String)null);
         aggrFn.setPartitionBy((String)null);
         aggrFn.setPartitionByClause((QueryPartitionClause)null);
         aggrFn.setOrderBy((OrderByStatement)null);
         from_sqs.setOlapFunctionPresent(true);
         if (this.getPartitionByClause() != null) {
            String partitionString = "keepdenserank" + this.getPartitionByClause().toString();
            from_sqs.addOlapDerivedTables(partitionString + aggrFn.getFunctionName() + this.getOrderBy(), derivedTableFromItem);
         } else {
            from_sqs.addOlapDerivedTables("keepdenserank" + aggrFn.getFunctionName() + this.getOrderBy(), derivedTableFromItem);
         }
      } else {
         SelectColumn newArgSelCol = new SelectColumn();
         SelectColumn arg = (SelectColumn)this.getFunctionArguments().get(0);
         newArgSelCol.setColumnExpression(arg.getColumnExpression());
         newArgSelCol.setIgnoreNulls(arg.getIgnoreNulls());
         String partitionString = "keepdenserank";
         if (this.getPartitionByClause() != null) {
            partitionString = partitionString + this.getPartitionByClause().toString() + aggrFn.getFunctionName() + this.getOrderBy();
         } else {
            partitionString = partitionString + aggrFn.getFunctionName() + this.getOrderBy();
         }

         if (from_sqs.getOlapDerivedTables().containsKey(partitionString)) {
            FromTable derivedTable = (FromTable)from_sqs.getOlapDerivedTables().get(partitionString);
            Vector existingSelectItems = ((SelectQueryStatement)derivedTable.getTableName()).getSelectStatement().getSelectItemList();
            int siz = existingSelectItems.size();
            newArgSelCol.setAliasName("denserank_" + alias + siz);
            SelectColumn selCol = (SelectColumn)existingSelectItems.get(siz - 1);
            if (selCol.getEndsWith() == null) {
               selCol.setEndsWith(",");
            }

            existingSelectItems.add(newArgSelCol);
            SelectColumn newAggrFnArg = new SelectColumn();
            gbsItems = new Vector();
            TableColumn newAggrFnArgCol = new TableColumn();
            newAggrFnArgCol.setColumnName("denserank_" + alias + siz);
            gbsItems.add(newAggrFnArgCol);
            newAggrFnArg.setColumnExpression(gbsItems);
            Vector newFunctionArgs = new Vector();
            newFunctionArgs.add(newAggrFnArg);
            aggrFn.setFunctionArguments(newFunctionArgs);
            alias = derivedTable.getAliasName();
            idx = "" + (from_sqs.getOlapDerivedTables().size() - 1);
         }

         aggrFn.setKeep((String)null);
         aggrFn.setFirst((String)null);
         aggrFn.setLast((String)null);
         aggrFn.setDenseRank((String)null);
         aggrFn.setOver((String)null);
         aggrFn.setPartitionBy((String)null);
         aggrFn.setPartitionByClause((QueryPartitionClause)null);
         aggrFn.setOrderBy((OrderByStatement)null);
         from_sqs.setOlapFunctionPresent(true);
      }

   }

   private WhereItem generateWhereItems(SelectColumn selCol, String derivedTableAlias, String derivedTableColumn) throws ConvertException {
      WhereItem wi = new WhereItem();
      WhereColumn lwc = new WhereColumn();
      Vector lwcColExp = new Vector();
      if (selCol.getEndsWith() != null) {
         selCol.setEndsWith((String)null);
      }

      if (selCol.getAliasName() != null) {
         lwcColExp.add(selCol.getAliasName());
      } else {
         lwcColExp.add(selCol);
      }

      WhereColumn rwc = new WhereColumn();
      Vector rwcColExp = new Vector();
      if (selCol != null) {
         TableColumn rsc = new TableColumn();
         rsc.setTableName(derivedTableAlias);
         rsc.setColumnName(derivedTableColumn);
         rsc.setDot(".");
         rwcColExp.add(rsc);
      }

      lwc.setColumnExpression(lwcColExp);
      rwc.setColumnExpression(rwcColExp);
      wi.setLeftWhereExp(lwc);
      wi.setRightWhereExp(rwc);
      wi.setOperator("=");
      return wi;
   }

   public static boolean checkForCasting(Vector colExp) {
      boolean needsCasting = true;

      try {
         if (colExp != null && colExp.size() == 1 && colExp.get(0) instanceof FunctionCalls) {
            FunctionCalls fc = (FunctionCalls)colExp.get(0);
            String functionNameStr = fc.getFunctionNameAsAString() != null ? fc.getFunctionNameAsAString().trim().toUpperCase() : "";
            if (!functionNameStr.isEmpty() && functionNameStr.equalsIgnoreCase("CAST") && fc.getFunctionArguments().size() == 2 && fc.getFunctionArguments().get(1) instanceof CharacterClass) {
               needsCasting = false;
            } else if (fc.getFunctionArguments() == null || !fc.getFunctionArguments().isEmpty() || !functionNameStr.startsWith("CAST(") || !functionNameStr.endsWith("CHAR)") && !functionNameStr.endsWith("VARCHAR)") && !functionNameStr.endsWith("TEXT)")) {
               if (!functionNameStr.startsWith("CHAR_LENGTH")) {
                  Vector list = StringFunctions.getStringFunctionsListForCasting();

                  for(int i = 0; i < list.size(); ++i) {
                     String functionNameString = list.get(i).toString();
                     if (functionNameStr.startsWith(functionNameString)) {
                        needsCasting = false;
                        break;
                     }
                  }
               }
            } else {
               needsCasting = false;
            }
         }
      } catch (Exception var7) {
      }

      return needsCasting;
   }

   public static Vector castToCharClass(Vector colExp, String dataType) {
      Vector newColumnExpr = null;
      if (colExp != null) {
         try {
            if (checkForCasting(colExp)) {
               FunctionCalls castFunction = castToCharFunctionCall(colExp, dataType);
               if (castFunction != null) {
                  newColumnExpr = new Vector();
                  newColumnExpr.add(0, castFunction);
               }
            }
         } catch (Exception var4) {
            newColumnExpr = null;
         }
      }

      return newColumnExpr;
   }

   public static FunctionCalls castToCharFunctionCall(String argument) {
      Vector colExp = new Vector();
      colExp.add(argument);
      return castToCharFunctionCall(colExp, "CHAR");
   }

   public static FunctionCalls castToCharFunctionCall(Vector colExp, String dataType) {
      try {
         cast castFunction = new cast();
         TableColumn tc1 = new TableColumn();
         CharacterClass charClass = new CharacterClass();
         tc1.setColumnName("CAST");
         castFunction.setFunctionName(tc1);
         castFunction.setAsDatatype("AS");
         charClass.setDatatypeName(dataType);
         Vector newFunctionArgs = new Vector();
         SelectColumn sc = new SelectColumn();
         sc.setColumnExpression(colExp);
         newFunctionArgs.add(0, sc);
         newFunctionArgs.add(1, charClass);
         castFunction.setFunctionArguments(newFunctionArgs);
         return castFunction;
      } catch (Exception var7) {
         return null;
      }
   }

   public static Vector castToNumericClass(Vector colExp, String dataType) {
      Vector newColumnExpr = null;
      if (colExp != null) {
         try {
            cast castFunction = new cast();
            TableColumn tc1 = new TableColumn();
            NumericClass numClass = new NumericClass();
            tc1.setColumnName("CAST");
            castFunction.setFunctionName(tc1);
            castFunction.setAsDatatype("AS");
            numClass.setDatatypeName(dataType);
            Vector newFunctionArgs = new Vector();
            SelectColumn sc = new SelectColumn();
            sc.setColumnExpression(colExp);
            newFunctionArgs.add(0, sc);
            newFunctionArgs.add(1, numClass);
            castFunction.setFunctionArguments(newFunctionArgs);
            newColumnExpr = new Vector();
            newColumnExpr.add(0, castFunction);
         } catch (Exception var8) {
            newColumnExpr = null;
         }
      }

      return newColumnExpr;
   }

   public void handlePositionFunctionArguments(String functionName, Vector newFunctionArguments, String dataType) {
      if (functionName != null && functionName.equalsIgnoreCase("POSITION") && newFunctionArguments != null && newFunctionArguments.size() == 1 && newFunctionArguments.get(0) instanceof SelectColumn && ((SelectColumn)newFunctionArguments.get(0)).getColumnExpression().size() == 1 && ((SelectColumn)newFunctionArguments.get(0)).getColumnExpression().get(0) instanceof WhereItem) {
         WhereItem wi = (WhereItem)((WhereItem)((SelectColumn)newFunctionArguments.get(0)).getColumnExpression().get(0));
         WhereColumn leftWhereCol = wi.getLeftWhereExp();
         WhereColumn rightWhereCol = wi.getRightWhereExp();
         if (rightWhereCol != null && leftWhereCol != null && rightWhereCol.getColumnExpression() != null && leftWhereCol.getColumnExpression() != null) {
            Vector leftWhrColExp = castToCharClass(leftWhereCol.getColumnExpression(), dataType);
            WhereColumn rightWhrCol;
            if (rightWhereCol.getColumnExpression().size() == 3 && rightWhereCol.getColumnExpression().get(0) instanceof String && rightWhereCol.getColumnExpression().get(1) instanceof WhereColumn && rightWhereCol.getColumnExpression().get(2) instanceof String && rightWhereCol.getColumnExpression().get(0).toString().equals("(") && rightWhereCol.getColumnExpression().get(2).toString().equals(")")) {
               rightWhrCol = (WhereColumn)((WhereColumn)rightWhereCol.getColumnExpression().get(1));
               rightWhereCol = rightWhrCol;
            } else if (rightWhereCol.getColumnExpression().size() == 1 && rightWhereCol.getColumnExpression().get(0) instanceof WhereColumn) {
               rightWhrCol = (WhereColumn)((WhereColumn)rightWhereCol.getColumnExpression().get(0));
               rightWhereCol = rightWhrCol;
            }

            Vector rightWhrColExp = castToCharClass(rightWhereCol.getColumnExpression(), dataType);
            if (leftWhrColExp != null) {
               leftWhereCol.setColumnExpression(leftWhrColExp);
            }

            if (rightWhrColExp != null) {
               rightWhereCol.setColumnExpression(rightWhrColExp);
            }
         }
      }

   }

   public boolean needsCastingForStringLiterals() {
      boolean needsCasting = false;

      try {
         for(int j = 0; j < this.functionArguments.size(); ++j) {
            if (this.functionArguments.elementAt(j) instanceof SelectColumn) {
               SelectColumn sc = (SelectColumn)this.functionArguments.elementAt(j);
               if (sc.needsCastingForStringLiterals()) {
                  needsCasting = true;
                  break;
               }
            }
         }
      } catch (Exception var4) {
      }

      return needsCasting;
   }

   public String handleStringLiteralForDateTime(String dateTimeString, SelectQueryStatement from_sqs) {
      boolean canHandleStringLiteralsForDateTime = from_sqs != null && from_sqs.getBooleanValues("can.handle.string.literals.for.date.time");
      dateTimeString = StringFunctions.convertToAnsiDateLiteral(dateTimeString, canHandleStringLiteralsForDateTime);
      return dateTimeString;
   }

   public void handleStringLiteralForDateTime(SelectQueryStatement from_sqs, int elementPosition, boolean castingNeeded) {
      try {
         boolean canHandleStringLiteralsForDateTime = from_sqs != null && from_sqs.getBooleanValues("can.handle.string.literals.for.date.time");
         if ((canHandleStringLiteralsForDateTime || castingNeeded) && this.functionArguments != null && this.functionArguments.size() > elementPosition && ((SelectColumn)this.functionArguments.elementAt(elementPosition)).getColumnExpression() != null && ((SelectColumn)this.functionArguments.elementAt(elementPosition)).getColumnExpression().size() == 1 && ((SelectColumn)this.functionArguments.elementAt(elementPosition)).getColumnExpression().get(0) instanceof String) {
            String dateTimeString = ((SelectColumn)this.functionArguments.elementAt(elementPosition)).getColumnExpression().get(0).toString();
            dateTimeString = StringFunctions.convertToAnsiDateLiteral(dateTimeString, canHandleStringLiteralsForDateTime);
            if (castingNeeded) {
               dateTimeString = "CAST(" + dateTimeString + " AS TIMESTAMP)";
            }

            ((SelectColumn)this.functionArguments.elementAt(elementPosition)).getColumnExpression().set(0, dateTimeString);
         }
      } catch (Exception var6) {
      }

   }

   public void handleStringLiteralForDate(SelectQueryStatement from_sqs, int elementPosition, boolean castingNeeded) {
      try {
         boolean canHandleStringLiteralsForDateTime = from_sqs != null && from_sqs.getBooleanValues("can.handle.string.literals.for.date.time");
         if ((canHandleStringLiteralsForDateTime || castingNeeded) && this.functionArguments != null && this.functionArguments.size() > elementPosition && ((SelectColumn)this.functionArguments.elementAt(elementPosition)).getColumnExpression() != null && ((SelectColumn)this.functionArguments.elementAt(elementPosition)).getColumnExpression().size() == 1 && ((SelectColumn)this.functionArguments.elementAt(elementPosition)).getColumnExpression().get(0) instanceof String) {
            String dateTimeString = ((SelectColumn)this.functionArguments.elementAt(elementPosition)).getColumnExpression().get(0).toString();
            dateTimeString = StringFunctions.convertToAnsiDateLiteral(dateTimeString, canHandleStringLiteralsForDateTime);
            if (castingNeeded) {
               dateTimeString = "CAST(" + dateTimeString + " AS DATE)";
            }

            ((SelectColumn)this.functionArguments.elementAt(elementPosition)).getColumnExpression().set(0, dateTimeString);
         }
      } catch (Exception var6) {
      }

   }

   public String handleStringLiteralForTime(String timeString, SelectQueryStatement from_sqs) {
      return this.handleStringLiteralForTime(timeString, from_sqs, false);
   }

   public String handleStringLiteralForTime(String timeString, SelectQueryStatement from_sqs, boolean forToChar) {
      boolean canHandleStringLiteralsForTime = from_sqs != null && from_sqs.getBooleanValues("can.handle.string.literals.for.date.time");
      timeString = StringFunctions.convertToAnsiTimeLiteral(timeString, canHandleStringLiteralsForTime, forToChar);
      return timeString;
   }

   public void handleStringLiteralForTime(SelectQueryStatement from_sqs, int elementPosition, boolean castingNeeded) {
      this.handleStringLiteralForTime(from_sqs, elementPosition, castingNeeded, false);
   }

   public void handleStringLiteralForTime(SelectQueryStatement from_sqs, int elementPosition, boolean castingNeeded, boolean forToChar) {
      try {
         boolean canHandleStringLiteralsForDateTime = from_sqs != null && from_sqs.getBooleanValues("can.handle.string.literals.for.date.time");
         if ((canHandleStringLiteralsForDateTime || castingNeeded) && this.functionArguments != null && this.functionArguments.size() > elementPosition && ((SelectColumn)this.functionArguments.elementAt(elementPosition)).getColumnExpression() != null && ((SelectColumn)this.functionArguments.elementAt(elementPosition)).getColumnExpression().size() == 1 && ((SelectColumn)this.functionArguments.elementAt(elementPosition)).getColumnExpression().get(0) instanceof String) {
            String timeString = ((SelectColumn)this.functionArguments.elementAt(elementPosition)).getColumnExpression().get(0).toString();
            timeString = StringFunctions.convertToAnsiTimeLiteral(timeString, canHandleStringLiteralsForDateTime, forToChar);
            if (castingNeeded) {
               timeString = "CAST(" + timeString + " AS TIME)";
            }

            ((SelectColumn)this.functionArguments.elementAt(elementPosition)).getColumnExpression().set(0, timeString);
         }
      } catch (Exception var7) {
      }

   }

   public static Set getSimpleDateFnList() {
      return simpleDateFns;
   }

   public static int getWeekStartDayValue(int startMonth, int startWeekDay) {
      int weekStartDay = true;
      int weekStartDay;
      if (startMonth > 1) {
         weekStartDay = WEEKDAY_MAP[startWeekDay];
      } else {
         weekStartDay = WEEKSTARTDAY_WEEKDAY_MAP[startWeekDay];
      }

      return weekStartDay;
   }

   public String getPgSQLDateFormatForMySQLWildCard(String dateFormat) {
      dateFormat = dateFormat.replaceAll("%a", "Dy");
      dateFormat = dateFormat.replaceAll("%b", "Mon");
      dateFormat = dateFormat.replaceAll("%c", "MM");
      dateFormat = dateFormat.replaceAll("%D", "FMDDth");
      dateFormat = dateFormat.replaceAll("%d", "DD");
      dateFormat = dateFormat.replaceAll("%e", "FMdd");
      dateFormat = dateFormat.replaceAll("%f", "US");
      dateFormat = dateFormat.replaceAll("%H", "HH24");
      dateFormat = dateFormat.replaceAll("%h", "HH12");
      dateFormat = dateFormat.replaceAll("%I", "HH12");
      dateFormat = dateFormat.replaceAll("%i", "MI");
      dateFormat = dateFormat.replaceAll("%j", "DDD");
      dateFormat = dateFormat.replaceAll("%k", "FMHH24");
      dateFormat = dateFormat.replaceAll("%l", "FMHH12");
      dateFormat = dateFormat.replaceAll("%M", "FMMonth");
      dateFormat = dateFormat.replaceAll("%m", "MM");
      dateFormat = dateFormat.replaceAll("%p", "AM");
      dateFormat = dateFormat.replaceAll("%r", "HH12:MI:ss AM");
      dateFormat = dateFormat.replaceAll("%S", "SS");
      dateFormat = dateFormat.replaceAll("%s", "SS");
      dateFormat = dateFormat.replaceAll("%T", "HH24:MI:ss");
      dateFormat = dateFormat.replaceAll("%U", "WW");
      dateFormat = dateFormat.replaceAll("%u", "WW");
      dateFormat = dateFormat.replaceAll("%V", "WW");
      dateFormat = dateFormat.replaceAll("%v", "IW");
      dateFormat = dateFormat.replaceAll("%W", "FMDay");
      dateFormat = dateFormat.replaceAll("%w", "D");
      dateFormat = dateFormat.replaceAll("%X", "YYYY");
      dateFormat = dateFormat.replaceAll("%x", "IYYY");
      dateFormat = dateFormat.replaceAll("%Y", "YYYY");
      dateFormat = dateFormat.replaceAll("%y", "YY");
      dateFormat = dateFormat.replaceAll("%01", "01");
      return dateFormat;
   }

   public void validateExcludeWeekendAsCharArray(String weekendPattern, String fnStr) throws ConvertException {
      if (weekendPattern.length() != 1 && !weekendPattern.contains(",")) {
         throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[]{fnStr, "EXCLUDE_WEEKENDS", "Provide values between 1 to 7 or combination between 1 to 7 separated by comma"});
      } else {
         weekendPattern = weekendPattern.replaceAll(",", "");
         char[] sortedWeekendPattern = weekendPattern.toCharArray();

         for(int index = 0; index < sortedWeekendPattern.length; ++index) {
            int weekValue = sortedWeekendPattern[index] - 48;
            if (weekValue <= 0 || weekValue >= 8) {
               throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[]{fnStr, "EXCLUDE_WEEKENDS", "Provide values between 1 to 7 or combination between 1 to 7 separated by comma"});
            }
         }

      }
   }

   public void validateExcludeWeekendAsString(String weekendPattern, String fnStr) throws ConvertException {
      for(int index = 0; index < 7; ++index) {
         if (weekendPattern.charAt(index) != '1' && weekendPattern.charAt(index) != '0') {
            throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[]{fnStr, "EXCLUDE_WEEKENDS", "Provide values as a seven digit string with 0's and 1's"});
         }
      }

   }

   public void validateWorkStartAndEndTime(String timeArg, String paramName, String fnStr, int wst) throws ConvertException {
      if (!timeArg.contains(":")) {
         throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[]{fnStr, paramName.toUpperCase(), "Provide value in HH:mm:ss format only. WORK_START_TIME of the day should be less than WORK_END_TIME of the day"});
      } else {
         String[] arrayStart = timeArg.split(":");
         if (arrayStart.length != 3) {
            throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[]{fnStr, paramName.toUpperCase(), "Provide value in HH:mm:ss format only. WORK_START_TIME of the day should be less than WORK_END_TIME of the day"});
         } else {
            int[] a = new int[3];

            int workTime;
            for(workTime = 0; workTime < 3; ++workTime) {
               try {
                  a[workTime] = Integer.parseInt(arrayStart[workTime]);
               } catch (Exception var9) {
                  throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[]{fnStr, paramName.toUpperCase(), "Provide only numeric values in HH:mm:ss format"});
               }
            }

            workTime = a[0] * 3600 + a[1] * 60 + a[2];
            if (workTime < 0 || workTime > 86400 || workTime < wst) {
               throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[]{fnStr, paramName.toUpperCase(), "Provide value in HH:mm:ss format only. WORK_START_TIME of the day should be less than WORK_END_TIME of the day"});
            }
         }
      }
   }

   public void validateStringLength(String str_len, String fnStr) throws ConvertException {
      int len;
      try {
         len = Integer.valueOf(str_len);
      } catch (Exception var5) {
         throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[]{fnStr, "STRING_LEN", "Provide only numeric values between 1 to 255"});
      }

      if (len < 1 || len > 255) {
         throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[]{fnStr, "STRING_LEN", "Provide values between 1 to 255"});
      }
   }

   public void validateIsWholeValue(String absoluteValue, String fnStr) throws ConvertException {
      if (!absoluteValue.equalsIgnoreCase("1") && !absoluteValue.equalsIgnoreCase("0")) {
         throw new ConvertException("Invalid Argument Value for Function MONTHS_BETWEEN", "INVALID_ARGUMENT_VALUE", new Object[]{"MONTHS_BETWEEN", "ISWHOLE_VALUE", "Provide values 0 or 1"});
      }
   }

   public void validateFiscalStartMonth(String fiscalStartMonth_str, String fnStr) throws ConvertException {
      int fiscalStartMonth;
      try {
         fiscalStartMonth = Integer.parseInt(fiscalStartMonth_str);
      } catch (Exception var5) {
         throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[]{fnStr, "FISCAL_START_MONTH", "Provide only numeric values between 1 to 12"});
      }

      if (fiscalStartMonth < 1 || fiscalStartMonth > 12) {
         throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[]{fnStr, "FISCAL_START_MONTH", "Provide values between 1 to 12"});
      }
   }

   public void validateWeekMode(String weekMode, String fnStr) throws ConvertException {
      if (!weekMode.equalsIgnoreCase("1") && !weekMode.equalsIgnoreCase("2")) {
         throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[]{fnStr, "WEEK_MODE", "Provide values 0 or 1"});
      }
   }

   public void validateWeek_Start_Day(String weekStartDay_str, String fnStr) throws ConvertException {
      int weekStartDay;
      try {
         weekStartDay = Integer.parseInt(weekStartDay_str);
      } catch (Exception var5) {
         throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[]{fnStr, "WEEK_START_DAY", "Provide only numeric values between 1 to 7"});
      }

      if (weekStartDay < 1 || weekStartDay > 7) {
         throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[]{fnStr, "WEEK_START_DAY", "Provide values between 1 to 7"});
      }
   }

   public void validateWeekDay(String weekDay_str, String fnStr) throws ConvertException {
      int weekDay;
      try {
         weekDay = Integer.parseInt(weekDay_str);
      } catch (Exception var5) {
         throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[]{fnStr, "WEEKDAY", "Provide only numeric values between 1 to 7"});
      }

      if (weekDay < 1 || weekDay > 7) {
         throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[]{fnStr, "WEEKDAY", "Provide values between 1 to 7"});
      }
   }

   public void validateWeek_StartDay(String weekStartDay_str, String fnStr) throws ConvertException {
      int weekStartDay;
      try {
         weekStartDay = Integer.parseInt(weekStartDay_str);
      } catch (Exception var5) {
         throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[]{fnStr, "WEEK_STARTDAY", "Provide only numeric values between 0 to 6"});
      }

      if (weekStartDay < 0 || weekStartDay > 6) {
         throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[]{fnStr, "WEEK_STARTDAY", "Provide values between 0 to 6"});
      }
   }

   public void validatePercentileRange(String range_str, String fnStr) throws ConvertException {
      int range;
      try {
         range = Integer.parseInt(range_str);
      } catch (Exception var5) {
         throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[]{fnStr, "RANGE", "Provide only numeric values between 0 to 100"});
      }

      if (range < 0 || range > 100) {
         throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[]{fnStr, "RANGE", "Provide values between 0 to 100"});
      }
   }

   public void validateAggFunArgsType(Vector arguments, String fnStr) throws ConvertException {
      if (arguments.elementAt(0) instanceof SelectColumn) {
         SelectColumn sc = (SelectColumn)arguments.elementAt(0);
         Vector vc = sc.getColumnExpression();
         if (vc.elementAt(0) instanceof WhereExpression || vc.elementAt(0) instanceof WhereItem) {
            throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_EXPRESSION", new Object[]{fnStr, "Provide a valid numeric column or expression or value as argument for the function"});
         }
      }

   }

   public void validateDateAddFunUnits(Vector arguments, String fnStr) throws ConvertException {
      if (arguments.elementAt(0) instanceof SelectColumn) {
         SelectColumn sc = (SelectColumn)arguments.elementAt(0);
         Vector vc = sc.getColumnExpression();
         if (!(vc.elementAt(0) instanceof TableColumn) || !SwisSQLAPI.MsSQLServerIntervalList.containsKey(((TableColumn)vc.elementAt(0)).toString().toLowerCase())) {
            throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[]{fnStr, "DATEPART", "Provide a valid datepart (year or month or yy or dd)"});
         }
      }

   }

   public void validateSubStringIndex(String indexVal, String fnStr) throws ConvertException {
      int index;
      try {
         index = Integer.parseInt(indexVal);
      } catch (Exception var5) {
         throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[]{fnStr, "INDEX", "Provide only numeric values"});
      }

      if (index < 1) {
         throw new ConvertException("Invalid Argument Value for Function " + fnStr, "INVALID_ARGUMENT_VALUE", new Object[]{fnStr, "INDEX", "Provide only numeric values"});
      }
   }

   public void validateDateTruncUnits(Vector arguments, String fnStr) throws ConvertException {
      if (arguments.elementAt(0) instanceof SelectColumn) {
         SelectColumn sc = (SelectColumn)arguments.elementAt(0);
         Vector vc = sc.getColumnExpression();
         if (!SwisSQLAPI.PostgresSqlTimeUnits.contains(vc.elementAt(0).toString().toLowerCase().replaceAll("'", ""))) {
            throw new ConvertException("Invalid Argument Value for Function" + fnStr, "INVALID_ARGUMENT_VALUE", new Object[]{fnStr, "DATE_UNIT", "\nProvide any one of the following value microsecond, millisecond, second, minute, hour, week, month, quarter, year, decade, century or millennium"});
         }
      }

   }

   public static String removeSingleQuotes(String str) {
      return str.startsWith("'") && str.endsWith("'") ? str.substring(1, str.length() - 1) : str;
   }

   public static Object getNumericalDurationValue(Object input) throws ConvertException {
      String durationConstVal = (String)input;
      durationConstVal = durationConstVal.startsWith("'") ? removeSingleQuotes(durationConstVal) : durationConstVal;
      durationConstVal = durationConstVal.trim();
      Matcher defFmtMatcher = DURATION_FORMAT_PATTERN3_COMPILED.matcher(durationConstVal);
      if (!"".equals(durationConstVal) && !durationConstVal.matches("[-+]?\\d*\\.?\\d+")) {
         return defFmtMatcher.matches() ? convertDurationToSeconds(durationConstVal) : input;
      } else {
         throw new ConvertException("Please give direct value in default format: %D:%H:%m:%s", "DIRECT_VALUE_NOT_IN_DEFAULT_FMT");
      }
   }

   public static Object convertDurationToSeconds(String input) throws ConvertException {
      if (input != null && !"".equals(input)) {
         boolean isNegative = input.trim().startsWith("-");
         if (isNegative) {
            input = input.replace("-", "");
         }

         input = input.trim();
         double days = 0.0D;
         double hours = 0.0D;
         double minutes = 0.0D;
         double seconds = 0.0D;
         String microseconds = ".000000";
         Matcher matcher = DURATION_FORMAT_PATTERN3_COMPILED.matcher(input);
         if (matcher.matches()) {
            days = matcher.group(4) != null ? Double.parseDouble(matcher.group(4)) : 0.0D;
            hours = matcher.group(6) != null ? Double.parseDouble(matcher.group(6)) : 0.0D;
            minutes = matcher.group(7) != null ? Double.parseDouble(matcher.group(7)) : 0.0D;
            seconds = matcher.group(8) != null ? Double.parseDouble(matcher.group(8)) : 0.0D;
            microseconds = matcher.group(9) != null ? matcher.group(9) : microseconds;
         }

         if (!(days > 751967.0D) && !(hours > 4.33133333E8D) && !(minutes > 1.55928E12D) && !(seconds > 5.613408E15D)) {
            int len = microseconds.length();
            if (len != 7 && len != 4) {
               return (new BigDecimal((double)(isNegative ? -1 : 1) * (days * 24.0D * 3600.0D + hours * 3600.0D + minutes * 60.0D + seconds))).setScale(6, 1).add(new BigDecimal(microseconds));
            } else {
               BigDecimal bd = new BigDecimal((double)(isNegative ? -1 : 1) * (days * 24.0D * 3600.0D + hours * 3600.0D + minutes * 60.0D + seconds));
               return bd + microseconds;
            }
         } else {
            throw new ConvertException("Duration range exceeds for the input:" + input, "DURATION_RANGE_EXCEEDS");
         }
      } else {
         throw new ConvertException("unable to convert " + input + " to number of seconds for the default format", "INVALID_DURATION_VALUE");
      }
   }

   static {
      WEEKDAY_MAP[1] = 1;
      WEEKDAY_MAP[7] = 2;
      WEEKDAY_MAP[6] = 3;
      WEEKDAY_MAP[5] = 4;
      WEEKDAY_MAP[4] = 5;
      WEEKDAY_MAP[3] = 6;
      WEEKDAY_MAP[2] = 0;
      WEEKSTARTDAY_WEEKDAY_MAP[1] = 4;
      WEEKSTARTDAY_WEEKDAY_MAP[7] = 5;
      WEEKSTARTDAY_WEEKDAY_MAP[6] = 6;
      WEEKSTARTDAY_WEEKDAY_MAP[5] = 0;
      WEEKSTARTDAY_WEEKDAY_MAP[4] = 1;
      WEEKSTARTDAY_WEEKDAY_MAP[3] = 2;
      WEEKSTARTDAY_WEEKDAY_MAP[2] = 3;
      simpleDateFns.add("abs_quarter");
      simpleDateFns.add("absquarter");
      simpleDateFns.add("abs_month");
      simpleDateFns.add("absmonth");
      simpleDateFns.add("quarter");
      simpleDateFns.add("month");
      simpleDateFns.add("week");
      simpleDateFns.add("week_day");
      simpleDateFns.add("weekday");
      simpleDateFns.add("hour");
      simpleDateFns.add("abs_week");
      simpleDateFns.add("absweek");
      simpleDateFns.add("date");
      simpleDateFns.add("timestamp");
      simpleDateFns.add("year");
      simpleDateFns.add("day");
      DURATION_FORMAT_PATTERN3_COMPILED = Pattern.compile("(^-?\\s*?(((\\d+)\\s*\\.\\s*))?((\\d+):(\\d+):(\\d+)(\\.\\d*)?)$)");
   }
}
