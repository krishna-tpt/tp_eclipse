package com.adventnet.swissqlapi.sql.statement.misc;

import com.adventnet.swissqlapi.sql.UserObjectContext;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.statement.ModifiedObjectAttr;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;
import com.adventnet.swissqlapi.sql.statement.update.TableObject;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.util.misc.CustomizeUtil;
import java.util.Vector;

public class GrantQueryStatement implements SwisSQLStatement {
   private String grant;
   private String allPrivileges;
   private Vector grantStatementVector;
   private String toStr;
   private Vector securityAccountVector;
   private Vector permissionVector;
   private Vector columnNamesVector;
   private String onString;
   private TableObject tableOrView;
   private String withGrantOption;
   private String asString;
   private String groupOrRole;
   private String columnOpenBraces;
   private String columnClosedBraces;
   private CommentClass commentObject;
   private UserObjectContext context = null;
   private Vector systemPrivilegeVector;
   private Vector newGrantStmtsVector = new Vector();

   public void setGrant(String grant) {
      this.grant = grant;
   }

   public void setAllPrivileges(String allPrivileges) {
      this.allPrivileges = allPrivileges;
   }

   public void setGrantStatementVector(Vector grantStatementVector) {
      this.grantStatementVector = grantStatementVector;
   }

   public void setTo(String toStr) {
      this.toStr = toStr;
   }

   public void setSecurityAccountVector(Vector securityAccountVector) {
      this.securityAccountVector = securityAccountVector;
   }

   public void setPermissionVector(Vector permissionVector) {
      this.permissionVector = permissionVector;
   }

   public void setColumnNamesVector(Vector columnNamesVector) {
      this.columnNamesVector = columnNamesVector;
   }

   public void setObjectContext(UserObjectContext obj) {
      this.context = obj;
   }

   public void setOn(String onString) {
      this.onString = onString;
   }

   public void setTableOrView(TableObject tableOrView) {
      this.tableOrView = tableOrView;
   }

   public void setWithGrantOption(String withGrantOption) {
      this.withGrantOption = withGrantOption;
   }

   public void setAs(String asString) {
      this.asString = asString;
   }

   public void setGroupOrRole(String groupOrRole) {
      this.groupOrRole = groupOrRole;
   }

   public void setColumnOpenBraces(String columnOpenBraces) {
      this.columnOpenBraces = columnOpenBraces;
   }

   public void setColumnClosedBraces(String columnClosedBraces) {
      this.columnClosedBraces = columnClosedBraces;
   }

   public void setCommentClause(CommentClass commentObject) {
      this.commentObject = commentObject;
   }

   public void setSystemPrivilegeVector(Vector systemPrivVector) {
      this.systemPrivilegeVector = systemPrivVector;
   }

   public String getGrant() {
      return this.grant;
   }

   public String getAllPrivileges() {
      return this.allPrivileges;
   }

   public Vector getGrantStatementVector() {
      return this.grantStatementVector;
   }

   public String getTo() {
      return this.toStr;
   }

   public Vector getSecurityAccountVector() {
      return this.securityAccountVector;
   }

   public Vector getPermissionVector() {
      return this.permissionVector;
   }

   public Vector getColumnNamesVector() {
      return this.columnNamesVector;
   }

   public String getOn() {
      return this.onString;
   }

   public TableObject getTableOrView() {
      return this.tableOrView;
   }

   public String getWithGrantOption() {
      return this.withGrantOption;
   }

   public String getAs() {
      return this.asString;
   }

   public String getGroupOrRole() {
      return this.groupOrRole;
   }

   public CommentClass getCommentClass() {
      return null;
   }

   public UserObjectContext getObjectContext() {
      return null;
   }

   public Vector getSystemPrivilegeVector() {
      return this.systemPrivilegeVector;
   }

   public Vector getNewGrantStmtsVector() {
      return this.newGrantStmtsVector;
   }

   public String removeIndent(String str) {
      str = str.replace('\n', ' ');
      str = str.replace('\t', ' ');
      return str;
   }

   public void setCommentClass(CommentClass commentClass) {
   }

   public GrantQueryStatement toANSIGrant() throws ConvertException {
      GrantQueryStatement grantQueryStmt = new GrantQueryStatement();
      if (this.commentObject != null) {
         grantQueryStmt.setCommentClause(this.commentObject);
      }

      if (this.grant != null) {
         grantQueryStmt.setGrant(this.grant);
      }

      if (this.allPrivileges != null) {
         grantQueryStmt.setAllPrivileges(this.allPrivileges);
      }

      if (this.grantStatementVector != null) {
         grantQueryStmt.setGrantStatementVector(this.grantStatementVector);
      }

      if (this.toStr != null) {
         grantQueryStmt.setTo(this.toStr);
      }

      if (this.securityAccountVector != null) {
         Vector securityVector = new Vector();

         for(int i = 0; i < this.securityAccountVector.size(); ++i) {
            String securityString = (String)this.securityAccountVector.get(i);
            if (securityString.startsWith("[")) {
               securityString = securityString.substring(1, securityString.length() - 1);
            }

            securityVector.addElement(securityString);
         }

         grantQueryStmt.setSecurityAccountVector(securityVector);
      }

      if (this.permissionVector != null) {
         grantQueryStmt.setPermissionVector(this.permissionVector);
      }

      if (this.columnNamesVector != null) {
         grantQueryStmt.setColumnNamesVector(this.columnNamesVector);
      }

      if (this.onString != null) {
         grantQueryStmt.setOn(this.onString);
      }

      if (this.tableOrView != null) {
         this.tableOrView.toANSISQL();
         grantQueryStmt.setTableOrView(this.tableOrView);
      }

      if (this.withGrantOption != null) {
         grantQueryStmt.setWithGrantOption(this.withGrantOption);
      }

      grantQueryStmt.setAs((String)null);
      grantQueryStmt.setGroupOrRole((String)null);
      return grantQueryStmt;
   }

   public GrantQueryStatement toTeradataGrant() throws ConvertException {
      GrantQueryStatement grantQueryStmt = new GrantQueryStatement();
      if (this.commentObject != null) {
         grantQueryStmt.setCommentClause(this.commentObject);
      }

      if (this.grant != null) {
         grantQueryStmt.setGrant(this.grant);
      }

      if (this.allPrivileges != null) {
         grantQueryStmt.setAllPrivileges(this.allPrivileges);
      }

      if (this.grantStatementVector != null) {
         grantQueryStmt.setGrantStatementVector(this.grantStatementVector);
      }

      if (this.toStr != null) {
         grantQueryStmt.setTo(this.toStr);
      }

      if (this.securityAccountVector != null) {
         Vector securityVector = new Vector();

         for(int i = 0; i < this.securityAccountVector.size(); ++i) {
            String securityString = (String)this.securityAccountVector.get(i);
            if (securityString.startsWith("[")) {
               securityString = securityString.substring(1, securityString.length() - 1);
            }

            securityVector.addElement(securityString);
         }

         grantQueryStmt.setSecurityAccountVector(securityVector);
      }

      if (this.permissionVector != null) {
         grantQueryStmt.setPermissionVector(this.permissionVector);
      }

      if (this.columnNamesVector != null) {
         grantQueryStmt.setColumnNamesVector(this.columnNamesVector);
      }

      if (this.onString != null) {
         grantQueryStmt.setOn(this.onString);
      }

      if (this.tableOrView != null) {
         this.tableOrView.toTeradata();
         grantQueryStmt.setTableOrView(this.tableOrView);
      }

      if (this.withGrantOption != null) {
         grantQueryStmt.setWithGrantOption(this.withGrantOption);
      }

      grantQueryStmt.setAs((String)null);
      grantQueryStmt.setGroupOrRole((String)null);
      return grantQueryStmt;
   }

   public GrantQueryStatement toDB2Grant() throws ConvertException {
      GrantQueryStatement grantQueryStmt = new GrantQueryStatement();
      if (this.commentObject != null) {
         grantQueryStmt.setCommentClause(this.commentObject);
      }

      if (this.grant != null) {
         grantQueryStmt.setGrant(this.grant);
      }

      if (this.allPrivileges != null) {
         grantQueryStmt.setAllPrivileges(this.allPrivileges);
      }

      if (this.grantStatementVector != null) {
         grantQueryStmt.setGrantStatementVector(this.grantStatementVector);
      }

      if (this.toStr != null) {
         grantQueryStmt.setTo(this.toStr);
      }

      if (this.securityAccountVector != null) {
         Vector securityVector = new Vector();

         for(int i = 0; i < this.securityAccountVector.size(); ++i) {
            String securityString = (String)this.securityAccountVector.get(i);
            if (securityString.startsWith("[")) {
               securityString = securityString.substring(1, securityString.length() - 1);
            }

            securityVector.addElement(securityString);
         }

         grantQueryStmt.setSecurityAccountVector(securityVector);
      }

      if (this.permissionVector != null) {
         grantQueryStmt.setPermissionVector(this.permissionVector);
      }

      if (this.columnNamesVector != null) {
         grantQueryStmt.setColumnNamesVector(this.columnNamesVector);
      }

      if (this.onString != null) {
         grantQueryStmt.setOn(this.onString);
      }

      if (this.tableOrView != null) {
         this.tableOrView.toDB2();
         grantQueryStmt.setTableOrView(this.tableOrView);
      }

      if (this.withGrantOption != null) {
         grantQueryStmt.setWithGrantOption(this.withGrantOption);
      }

      grantQueryStmt.setAs((String)null);
      grantQueryStmt.setGroupOrRole((String)null);
      return grantQueryStmt;
   }

   public GrantQueryStatement toInformixGrant() throws ConvertException {
      GrantQueryStatement grantQueryStmt = new GrantQueryStatement();
      if (this.commentObject != null) {
         grantQueryStmt.setCommentClause(this.commentObject);
      }

      if (this.grant != null) {
         grantQueryStmt.setGrant(this.grant);
      }

      if (this.allPrivileges != null) {
         grantQueryStmt.setAllPrivileges(this.allPrivileges);
      }

      if (this.grantStatementVector != null) {
         grantQueryStmt.setGrantStatementVector(this.grantStatementVector);
      }

      if (this.toStr != null) {
         grantQueryStmt.setTo(this.toStr);
      }

      if (this.securityAccountVector != null) {
         Vector securityVector = new Vector();

         for(int i = 0; i < this.securityAccountVector.size(); ++i) {
            String securityString = (String)this.securityAccountVector.get(i);
            if (securityString.startsWith("[")) {
               securityString = securityString.substring(1, securityString.length() - 1);
            }

            securityVector.addElement(securityString);
         }

         grantQueryStmt.setSecurityAccountVector(securityVector);
      }

      if (this.permissionVector != null) {
         grantQueryStmt.setPermissionVector(this.permissionVector);
      }

      if (this.columnNamesVector != null) {
         grantQueryStmt.setColumnNamesVector(this.columnNamesVector);
      }

      if (this.onString != null) {
         grantQueryStmt.setOn(this.onString);
      }

      if (this.tableOrView != null) {
         this.tableOrView.toInformix();
         grantQueryStmt.setTableOrView(this.tableOrView);
      }

      if (this.withGrantOption != null) {
         grantQueryStmt.setWithGrantOption(this.withGrantOption);
      }

      grantQueryStmt.setAs((String)null);
      grantQueryStmt.setGroupOrRole((String)null);
      return grantQueryStmt;
   }

   public GrantQueryStatement toMSSQLServerGrant() throws ConvertException {
      GrantQueryStatement grantQueryStmt = new GrantQueryStatement();
      if (this.commentObject != null) {
         grantQueryStmt.setCommentClause(this.commentObject);
      }

      if (this.grant != null) {
         grantQueryStmt.setGrant(this.grant);
      }

      if (this.allPrivileges != null) {
         grantQueryStmt.setAllPrivileges(this.allPrivileges);
      }

      if (this.grantStatementVector != null) {
         grantQueryStmt.setGrantStatementVector(this.grantStatementVector);
      }

      if (this.toStr != null) {
         grantQueryStmt.setTo(this.toStr);
      }

      if (this.securityAccountVector != null) {
         Vector securityVector = new Vector();

         for(int i = 0; i < this.securityAccountVector.size(); ++i) {
            String securityString = (String)this.securityAccountVector.get(i);
            if (securityString.startsWith("[")) {
               securityString = securityString.substring(1, securityString.length() - 1);
            }

            securityVector.addElement(securityString);
         }

         grantQueryStmt.setSecurityAccountVector(securityVector);
      }

      if (this.permissionVector != null) {
         grantQueryStmt.setPermissionVector(this.permissionVector);
      }

      if (this.columnNamesVector != null) {
         grantQueryStmt.setColumnNamesVector(this.columnNamesVector);
      }

      if (this.onString != null) {
         grantQueryStmt.setOn(this.onString);
      }

      if (this.tableOrView != null) {
         this.tableOrView.toMSSQLServer();
         grantQueryStmt.setTableOrView(this.tableOrView);
      }

      if (this.withGrantOption != null) {
         grantQueryStmt.setWithGrantOption(this.withGrantOption);
      }

      grantQueryStmt.setAs((String)null);
      grantQueryStmt.setGroupOrRole((String)null);
      return grantQueryStmt;
   }

   public GrantQueryStatement toMySQLGrant() throws ConvertException {
      GrantQueryStatement grantQueryStmt = new GrantQueryStatement();
      if (this.commentObject != null) {
         grantQueryStmt.setCommentClause(this.commentObject);
      }

      if (this.grant != null) {
         grantQueryStmt.setGrant(this.grant);
      }

      if (this.allPrivileges != null) {
         grantQueryStmt.setAllPrivileges(this.allPrivileges);
      }

      if (this.grantStatementVector != null) {
         grantQueryStmt.setGrantStatementVector(this.grantStatementVector);
      }

      if (this.toStr != null) {
         grantQueryStmt.setTo(this.toStr);
      }

      if (this.securityAccountVector != null) {
         Vector securityVector = new Vector();

         for(int i = 0; i < this.securityAccountVector.size(); ++i) {
            String securityString = (String)this.securityAccountVector.get(i);
            if (securityString.startsWith("[")) {
               securityString = securityString.substring(1, securityString.length() - 1);
            }

            securityVector.addElement(securityString);
         }

         grantQueryStmt.setSecurityAccountVector(securityVector);
      }

      if (this.permissionVector != null) {
         grantQueryStmt.setPermissionVector(this.permissionVector);
      }

      if (this.columnNamesVector != null) {
         grantQueryStmt.setColumnNamesVector(this.columnNamesVector);
      }

      if (this.onString != null) {
         grantQueryStmt.setOn(this.onString);
      }

      if (this.tableOrView != null) {
         this.tableOrView.toMySQL();
         grantQueryStmt.setTableOrView(this.tableOrView);
      }

      if (this.withGrantOption != null) {
         grantQueryStmt.setWithGrantOption(this.withGrantOption);
      }

      grantQueryStmt.setAs((String)null);
      grantQueryStmt.setGroupOrRole((String)null);
      return grantQueryStmt;
   }

   public GrantQueryStatement toOracleGrant() throws ConvertException {
      GrantQueryStatement grantQueryStmt = new GrantQueryStatement();
      if (this.commentObject != null) {
         grantQueryStmt.setCommentClause(this.commentObject);
      }

      if (this.grant != null) {
         grantQueryStmt.setGrant(this.grant);
      }

      if (this.allPrivileges != null) {
         grantQueryStmt.setAllPrivileges(this.allPrivileges);
      }

      if (this.grantStatementVector != null) {
         grantQueryStmt.setGrantStatementVector(this.grantStatementVector);
      }

      if (this.toStr != null) {
         grantQueryStmt.setTo(this.toStr);
      }

      if (this.securityAccountVector != null) {
         Vector securityVector = new Vector();

         for(int i = 0; i < this.securityAccountVector.size(); ++i) {
            String securityString = (String)this.securityAccountVector.get(i);
            if (securityString.startsWith("[")) {
               securityString = securityString.substring(1, securityString.length() - 1);
            }

            securityVector.addElement(securityString);
         }

         grantQueryStmt.setSecurityAccountVector(securityVector);
      }

      if (this.permissionVector != null) {
         grantQueryStmt.setPermissionVector(this.permissionVector);
      }

      if (this.columnNamesVector != null) {
         grantQueryStmt.setColumnNamesVector(this.columnNamesVector);
      }

      if (this.onString != null) {
         grantQueryStmt.setOn(this.onString);
      }

      if (this.tableOrView != null) {
         this.tableOrView.toOracle();
         grantQueryStmt.setTableOrView(this.tableOrView);
      }

      if (this.withGrantOption != null) {
         grantQueryStmt.setWithGrantOption(this.withGrantOption);
      }

      grantQueryStmt.setAs((String)null);
      grantQueryStmt.setGroupOrRole((String)null);
      return grantQueryStmt;
   }

   public GrantQueryStatement toPostgreSQLGrant() throws ConvertException {
      GrantQueryStatement grantQueryStmt = new GrantQueryStatement();
      if (this.commentObject != null) {
         grantQueryStmt.setCommentClause(this.commentObject);
      }

      if (this.grant != null) {
         grantQueryStmt.setGrant(this.grant);
      }

      if (this.allPrivileges != null) {
         grantQueryStmt.setAllPrivileges(this.allPrivileges);
      }

      if (this.grantStatementVector != null) {
         grantQueryStmt.setGrantStatementVector(this.grantStatementVector);
      }

      if (this.toStr != null) {
         grantQueryStmt.setTo(this.toStr);
      }

      if (this.securityAccountVector != null) {
         Vector securityVector = new Vector();

         for(int i = 0; i < this.securityAccountVector.size(); ++i) {
            String securityString = (String)this.securityAccountVector.get(i);
            if (securityString.startsWith("[")) {
               securityString = securityString.substring(1, securityString.length() - 1);
            }

            securityVector.addElement(securityString);
         }

         grantQueryStmt.setSecurityAccountVector(securityVector);
      }

      if (this.permissionVector != null) {
         grantQueryStmt.setPermissionVector(this.permissionVector);
      }

      if (this.columnNamesVector != null) {
         grantQueryStmt.setColumnNamesVector(this.columnNamesVector);
      }

      if (this.onString != null) {
         grantQueryStmt.setOn(this.onString);
      }

      if (this.tableOrView != null) {
         this.tableOrView.toPostgreSQL();
         grantQueryStmt.setTableOrView(this.tableOrView);
      }

      if (this.withGrantOption != null) {
         grantQueryStmt.setWithGrantOption(this.withGrantOption);
      }

      grantQueryStmt.setAs((String)null);
      grantQueryStmt.setGroupOrRole((String)null);
      return grantQueryStmt;
   }

   public GrantQueryStatement toBigQueryGrant() throws ConvertException {
      GrantQueryStatement grantQueryStmt = new GrantQueryStatement();
      if (this.commentObject != null) {
         grantQueryStmt.setCommentClause(this.commentObject);
      }

      if (this.grant != null) {
         grantQueryStmt.setGrant(this.grant);
      }

      if (this.allPrivileges != null) {
         grantQueryStmt.setAllPrivileges(this.allPrivileges);
      }

      if (this.grantStatementVector != null) {
         grantQueryStmt.setGrantStatementVector(this.grantStatementVector);
      }

      if (this.toStr != null) {
         grantQueryStmt.setTo(this.toStr);
      }

      if (this.securityAccountVector != null) {
         Vector securityVector = new Vector();

         for(int i = 0; i < this.securityAccountVector.size(); ++i) {
            String securityString = (String)this.securityAccountVector.get(i);
            if (securityString.startsWith("[")) {
               securityString = securityString.substring(1, securityString.length() - 1);
            }

            securityVector.addElement(securityString);
         }

         grantQueryStmt.setSecurityAccountVector(securityVector);
      }

      if (this.permissionVector != null) {
         grantQueryStmt.setPermissionVector(this.permissionVector);
      }

      if (this.columnNamesVector != null) {
         grantQueryStmt.setColumnNamesVector(this.columnNamesVector);
      }

      if (this.onString != null) {
         grantQueryStmt.setOn(this.onString);
      }

      if (this.tableOrView != null) {
         this.tableOrView.toBigQuery();
         grantQueryStmt.setTableOrView(this.tableOrView);
      }

      if (this.withGrantOption != null) {
         grantQueryStmt.setWithGrantOption(this.withGrantOption);
      }

      grantQueryStmt.setAs((String)null);
      grantQueryStmt.setGroupOrRole((String)null);
      return grantQueryStmt;
   }

   public GrantQueryStatement toSybaseGrant() throws ConvertException {
      GrantQueryStatement grantQueryStmt = new GrantQueryStatement();
      grantQueryStmt.setObjectContext(this.context);
      if (this.commentObject != null) {
         grantQueryStmt.setCommentClause(this.commentObject);
      }

      if (this.grant != null) {
         grantQueryStmt.setGrant(this.grant);
      }

      if (this.allPrivileges != null) {
         grantQueryStmt.setAllPrivileges(this.allPrivileges);
      }

      if (this.grantStatementVector != null) {
         grantQueryStmt.setGrantStatementVector(this.grantStatementVector);
      }

      if (this.toStr != null) {
         grantQueryStmt.setTo(this.toStr);
      }

      if (this.securityAccountVector != null) {
         Vector securityVector = new Vector();

         for(int i = 0; i < this.securityAccountVector.size(); ++i) {
            String securityString = (String)this.securityAccountVector.get(i);
            if (securityString.startsWith("[")) {
               securityString = securityString.substring(1, securityString.length() - 1);
            }

            securityVector.addElement(securityString);
         }

         grantQueryStmt.setSecurityAccountVector(securityVector);
      }

      if (this.permissionVector != null) {
         grantQueryStmt.setPermissionVector(this.permissionVector);
      }

      if (this.columnNamesVector != null) {
         grantQueryStmt.setColumnNamesVector(this.columnNamesVector);
      }

      if (this.onString != null) {
         grantQueryStmt.setOn(this.onString);
      }

      if (this.tableOrView != null) {
         this.tableOrView.toSybase();
         grantQueryStmt.setTableOrView(this.tableOrView);
      }

      if (this.withGrantOption != null) {
         grantQueryStmt.setWithGrantOption(this.withGrantOption);
      }

      grantQueryStmt.setAs((String)null);
      grantQueryStmt.setGroupOrRole((String)null);
      return grantQueryStmt;
   }

   public GrantQueryStatement toTimesTenGrant() throws ConvertException {
      GrantQueryStatement grantQueryStmt = new GrantQueryStatement();
      grantQueryStmt.setObjectContext(this.context);
      if (this.commentObject != null) {
         grantQueryStmt.setCommentClause(this.commentObject);
      }

      if (this.grant != null) {
         grantQueryStmt.setGrant(this.grant);
      }

      if (this.allPrivileges != null) {
         grantQueryStmt.setAllPrivileges(this.allPrivileges);
      }

      if (this.grantStatementVector != null) {
         grantQueryStmt.setGrantStatementVector(this.grantStatementVector);
      }

      if (this.toStr != null) {
         grantQueryStmt.setTo(this.toStr);
      }

      if (this.securityAccountVector != null) {
         Vector securityVector = new Vector();

         for(int i = 0; i < this.securityAccountVector.size(); ++i) {
            String securityString = (String)this.securityAccountVector.get(i);
            if (securityString.startsWith("[")) {
               securityString = securityString.substring(1, securityString.length() - 1);
            }

            securityVector.addElement(securityString);
         }

         grantQueryStmt.setSecurityAccountVector(securityVector);
      }

      if (this.permissionVector != null) {
         grantQueryStmt.setPermissionVector(this.permissionVector);
      }

      if (this.columnNamesVector != null) {
         grantQueryStmt.setColumnNamesVector(this.columnNamesVector);
      }

      if (this.onString != null) {
         grantQueryStmt.setOn((String)null);
      }

      if (this.tableOrView != null) {
         grantQueryStmt.setTableOrView((TableObject)null);
      }

      if (this.withGrantOption != null) {
         grantQueryStmt.setWithGrantOption(this.withGrantOption);
      }

      grantQueryStmt.setAs((String)null);
      grantQueryStmt.setGroupOrRole((String)null);
      return grantQueryStmt;
   }

   public GrantQueryStatement toNetezzaGrant() throws ConvertException {
      GrantQueryStatement grantQueryStmt = new GrantQueryStatement();
      if (this.commentObject != null) {
         grantQueryStmt.setCommentClause(this.commentObject);
      }

      if (this.grant != null) {
         grantQueryStmt.setGrant(this.grant);
      }

      if (this.allPrivileges != null) {
         grantQueryStmt.setAllPrivileges(this.allPrivileges);
      }

      if (this.grantStatementVector != null) {
         grantQueryStmt.setGrantStatementVector(this.grantStatementVector);
      }

      if (this.toStr != null) {
         grantQueryStmt.setTo(this.toStr);
      }

      Vector newPermissionVector;
      int permissionVectorSize;
      String userName;
      if (this.securityAccountVector != null) {
         newPermissionVector = new Vector();

         for(permissionVectorSize = 0; permissionVectorSize < this.securityAccountVector.size(); ++permissionVectorSize) {
            userName = (String)this.securityAccountVector.get(permissionVectorSize);
            if (userName.startsWith("[")) {
               userName = userName.substring(1, userName.length() - 1);
            }

            newPermissionVector.addElement(userName);
         }

         grantQueryStmt.setSecurityAccountVector(newPermissionVector);
      }

      int i;
      if (this.systemPrivilegeVector != null) {
         newPermissionVector = new Vector();
         Vector objectPrivVector = new Vector();
         i = this.systemPrivilegeVector.size();

         int objectPrivVectorSize;
         for(objectPrivVectorSize = 0; objectPrivVectorSize < i; ++objectPrivVectorSize) {
            String obj = this.systemPrivilegeVector.get(objectPrivVectorSize).toString();
            if (obj.toUpperCase().indexOf("TABLE") != -1 || obj.toUpperCase().indexOf("VIEW") != -1 || obj.toUpperCase().indexOf("SEQUENCE") != -1 || obj.toUpperCase().indexOf("SYNONYM") != -1 || obj.toUpperCase().indexOf("USER") != -1) {
               if (obj.toUpperCase().startsWith("CREATE")) {
                  String[] objArr = obj.split(":swissql:");
                  if (objArr.length == 3) {
                     newPermissionVector.add(objArr[0] + " " + objArr[2]);
                  } else {
                     newPermissionVector.add(objArr[0] + " " + objArr[1]);
                  }
               } else {
                  objectPrivVector.add(this.systemPrivilegeVector.get(objectPrivVectorSize));
               }
            }
         }

         Vector objPrivPermissionVector;
         String systemPriv;
         int j;
         GrantQueryStatement newObjPrivStmt;
         if (newPermissionVector.size() >= 1) {
            objectPrivVectorSize = newPermissionVector.size();

            for(j = 0; j < objectPrivVectorSize; ++j) {
               newObjPrivStmt = new GrantQueryStatement();
               newObjPrivStmt.setGrant(this.grant);
               objPrivPermissionVector = new Vector();
               systemPriv = newPermissionVector.get(j).toString();
               objPrivPermissionVector.add(systemPriv);
               newObjPrivStmt.setPermissionVector(objPrivPermissionVector);
               newObjPrivStmt.setTo(this.toStr);
               newObjPrivStmt.setSecurityAccountVector(this.securityAccountVector);
               newObjPrivStmt.setWithGrantOption(this.withGrantOption);
               grantQueryStmt.getNewGrantStmtsVector().add(newObjPrivStmt.toNetezzaGrant());
            }
         }

         if (objectPrivVector.size() >= 1) {
            objectPrivVectorSize = objectPrivVector.size();

            for(j = 0; j < objectPrivVectorSize; ++j) {
               newObjPrivStmt = new GrantQueryStatement();
               newObjPrivStmt.setGrant(this.grant);
               objPrivPermissionVector = new Vector();
               systemPriv = objectPrivVector.get(j).toString();
               String[] systemPrivArr = systemPriv.split(":swissql:");
               objPrivPermissionVector.add(systemPrivArr[0]);
               newObjPrivStmt.setPermissionVector(objPrivPermissionVector);
               TableObject tableObj = new TableObject();
               if (systemPrivArr.length == 3) {
                  tableObj.setTableName(systemPrivArr[2]);
               } else {
                  tableObj.setTableName(systemPrivArr[1]);
               }

               newObjPrivStmt.setTableOrView(tableObj);
               newObjPrivStmt.setOn("ON");
               newObjPrivStmt.setTo(this.toStr);
               newObjPrivStmt.setSecurityAccountVector(this.securityAccountVector);
               newObjPrivStmt.setWithGrantOption(this.withGrantOption);
               grantQueryStmt.getNewGrantStmtsVector().add(newObjPrivStmt.toNetezzaGrant());
            }
         }
      }

      String tableName;
      if (this.permissionVector != null) {
         if (this.permissionVector.size() == 1) {
            grantQueryStmt.setPermissionVector(this.permissionVector);
         } else {
            newPermissionVector = new Vector();
            permissionVectorSize = this.permissionVector.size();

            for(i = 0; i < permissionVectorSize; ++i) {
               tableName = this.permissionVector.get(i).toString();
               if (tableName.equalsIgnoreCase("DELETE") || tableName.equalsIgnoreCase("INSERT") || tableName.equalsIgnoreCase("SELECT") || tableName.equalsIgnoreCase("TRUNCATE") || tableName.equalsIgnoreCase("UPDATE") || tableName.equalsIgnoreCase("DROP") || tableName.equalsIgnoreCase("ALTER")) {
                  newPermissionVector.add(this.permissionVector.get(i));
               }
            }

            grantQueryStmt.setPermissionVector(newPermissionVector);
         }
      }

      if (this.columnNamesVector != null) {
         grantQueryStmt.setColumnNamesVector((Vector)null);
      }

      if (this.onString != null) {
         grantQueryStmt.setOn(this.onString);
      }

      if (this.tableOrView != null) {
         TableObject grantTableObject = this.tableOrView;
         String ownerName = grantTableObject.getOwner();
         userName = grantTableObject.getUser();
         tableName = grantTableObject.getTableName();
         if (ownerName != null && (ownerName.startsWith("[") && ownerName.endsWith("]") || ownerName.startsWith("`") && ownerName.endsWith("`"))) {
            ownerName = ownerName.substring(1, ownerName.length() - 1);
            if (ownerName.indexOf(32) != -1) {
               ownerName = "\"" + ownerName + "\"";
            }
         }

         if (userName != null && (userName.startsWith("[") && userName.endsWith("]") || userName.startsWith("`") && userName.endsWith("`"))) {
            userName = userName.substring(1, userName.length() - 1);
            if (userName.indexOf(32) != -1) {
               userName = "\"" + userName + "\"";
            }
         }

         if (tableName != null && (tableName.startsWith("[") && tableName.endsWith("]") || tableName.startsWith("`") && tableName.endsWith("`"))) {
            tableName = tableName.substring(1, tableName.length() - 1);
            if (tableName.indexOf(32) != -1) {
               tableName = "\"" + tableName + "\"";
            }
         }

         tableName = CustomizeUtil.objectNamesToQuotedIdentifier(tableName, SwisSQLUtils.getKeywords(11), (ModifiedObjectAttr)null, 11);
         grantTableObject.setOwner(ownerName);
         grantTableObject.setUser(userName);
         grantTableObject.setTableName(tableName);
         grantTableObject.toNetezza();
         grantQueryStmt.setTableOrView(grantTableObject);
      }

      if (this.withGrantOption != null) {
         this.withGrantOption = this.withGrantOption.replaceFirst("ADMIN", "GRANT");
         grantQueryStmt.setWithGrantOption(this.withGrantOption);
      }

      grantQueryStmt.setAs((String)null);
      grantQueryStmt.setGroupOrRole((String)null);
      return grantQueryStmt;
   }

   public GrantQueryStatement toSnowflakeGrant() throws ConvertException {
      GrantQueryStatement grantQueryStmt = new GrantQueryStatement();
      if (this.commentObject != null) {
         grantQueryStmt.setCommentClause(this.commentObject);
      }

      if (this.grant != null) {
         grantQueryStmt.setGrant(this.grant);
      }

      if (this.allPrivileges != null) {
         grantQueryStmt.setAllPrivileges(this.allPrivileges);
      }

      if (this.grantStatementVector != null) {
         grantQueryStmt.setGrantStatementVector(this.grantStatementVector);
      }

      if (this.toStr != null) {
         grantQueryStmt.setTo(this.toStr);
      }

      if (this.securityAccountVector != null) {
         Vector securityVector = new Vector();

         for(int i = 0; i < this.securityAccountVector.size(); ++i) {
            String securityString = (String)this.securityAccountVector.get(i);
            if (securityString.startsWith("[")) {
               securityString = securityString.substring(1, securityString.length() - 1);
            }

            securityVector.addElement(securityString);
         }

         grantQueryStmt.setSecurityAccountVector(securityVector);
      }

      if (this.permissionVector != null) {
         grantQueryStmt.setPermissionVector(this.permissionVector);
      }

      if (this.columnNamesVector != null) {
         grantQueryStmt.setColumnNamesVector(this.columnNamesVector);
      }

      if (this.onString != null) {
         grantQueryStmt.setOn(this.onString);
      }

      if (this.tableOrView != null) {
         this.tableOrView.toSnowflake();
         grantQueryStmt.setTableOrView(this.tableOrView);
      }

      if (this.withGrantOption != null) {
         grantQueryStmt.setWithGrantOption(this.withGrantOption);
      }

      grantQueryStmt.setAs((String)null);
      grantQueryStmt.setGroupOrRole((String)null);
      return grantQueryStmt;
   }

   public String toANSIString() throws ConvertException {
      return this.toANSIGrant().toString();
   }

   public String toTeradataString() throws ConvertException {
      return this.toTeradataGrant().toString();
   }

   public String toDB2String() throws ConvertException {
      return this.toDB2Grant().toString();
   }

   public String toInformixString() throws ConvertException {
      return this.toInformixGrant().toString();
   }

   public String toMSSQLServerString() throws ConvertException {
      return this.toMSSQLServerGrant().toString();
   }

   public String toMySQLString() throws ConvertException {
      return this.toMySQLGrant().toString();
   }

   public String toOracleString() throws ConvertException {
      return this.toOracleGrant().toString();
   }

   public String toPostgreSQLString() throws ConvertException {
      return this.toPostgreSQLGrant().toString();
   }

   public String toBigQueryString() throws ConvertException {
      return this.toBigQueryGrant().toString();
   }

   public String toSybaseString() throws ConvertException {
      return this.toSybaseGrant().toString();
   }

   public String toTimesTenString() throws ConvertException {
      return this.toTimesTenGrant().toString();
   }

   public String toNetezzaString() throws ConvertException {
      return this.toNetezzaGrant().toString();
   }

   public String toSnowflakeString() throws ConvertException {
      return this.toSnowflakeGrant().toString();
   }

   public String toAthenaString() throws ConvertException {
      return null;
   }

   public String toSapHanaString() throws ConvertException {
      return null;
   }

   public String toSqliteString() throws ConvertException {
      return null;
   }

   public String toExcelString() throws ConvertException {
      return null;
   }

   public String toMsAccessJdbcString() throws ConvertException {
      return null;
   }

   public String toString() {
      StringBuffer sb = new StringBuffer();
      String indentString = "\n";
      if (this.commentObject != null) {
         sb.append(this.commentObject.toString());
      }

      if (this.grant != null) {
         sb.append(indentString + this.grant.toUpperCase() + " ");
      }

      if (this.allPrivileges != null) {
         sb.append(" " + this.allPrivileges.toUpperCase());
      }

      int i;
      if (this.grantStatementVector != null) {
         for(i = 0; i < this.grantStatementVector.size(); ++i) {
            if (i != 0) {
               sb.append(", ");
            }

            sb.append(this.grantStatementVector.get(i));
         }
      }

      if (this.permissionVector != null) {
         for(i = 0; i < this.permissionVector.size(); ++i) {
            if (i != 0) {
               sb.append(",");
            }

            sb.append(this.permissionVector.get(i));
            if ((this.permissionVector.get(i).toString().equalsIgnoreCase("insert") || this.permissionVector.get(i).toString().equalsIgnoreCase("update") || this.permissionVector.get(i).toString().equalsIgnoreCase("delete") || this.permissionVector.get(i).toString().equalsIgnoreCase("references")) && this.columnNamesVector != null && this.columnNamesVector.elementAt(i) != null) {
               sb.append("(");
               sb.append(this.columnNamesVector.get(i));
               sb.append(")");
            }
         }
      }

      if (this.onString != null) {
         sb.append(indentString + this.onString.toUpperCase());
      }

      if (this.tableOrView != null) {
         this.tableOrView.setObjectContext(this.context);
         sb.append(" " + this.tableOrView);
      }

      if (this.toStr != null) {
         sb.append(indentString + this.toStr.toUpperCase() + " ");
      }

      if (this.securityAccountVector != null) {
         for(i = 0; i < this.securityAccountVector.size(); ++i) {
            if (i != 0) {
               sb.append(", ");
            }

            sb.append(" " + this.securityAccountVector.get(i));
         }
      }

      if (this.withGrantOption != null) {
         sb.append(" " + this.withGrantOption.toUpperCase());
      }

      if (this.asString != null) {
         sb.append(" " + this.asString.toUpperCase());
      }

      if (this.groupOrRole != null) {
         sb.append(" " + this.groupOrRole);
      }

      if (this.newGrantStmtsVector != null && this.newGrantStmtsVector.size() >= 1) {
         StringBuffer newSb = new StringBuffer();
         int newGrantStmtsVectorSize = this.newGrantStmtsVector.size();

         for(int i = 0; i < newGrantStmtsVectorSize; ++i) {
            newSb.append(((GrantQueryStatement)this.newGrantStmtsVector.get(i)).toString());
            if (i != newGrantStmtsVectorSize - 1) {
               newSb.append(";");
            }
         }

         return newSb.toString();
      } else {
         return sb.toString();
      }
   }

   public String toVectorWiseString() throws ConvertException {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public SwisSQLStatement toVectorWise() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toInformix() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toNetezza() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toTimesTen() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toOracle() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toSqlite() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toSapHana() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toAthena() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toSnowflake() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toMySQL() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toPostgreSQL() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toBigQuery() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toDB2() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toSybase() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toMSSQLServer() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toTeradata() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toANSI() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toMsAccessJdbc() throws ConvertException {
      return null;
   }

   public SwisSQLStatement toExcel() throws ConvertException {
      return null;
   }
}
