package com.sodreport.excel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OracleDDLExecutor {

	Connection conn;
	String TableName = "Z_SOD_TRANSACTIONS";
	List<String> indexesCreate = new ArrayList<>();
	List<String> indexesdrop = new ArrayList<>();
	String tableCreate;

	public OracleDDLExecutor(Connection conn) {
		this.conn = conn;
	}

	public void DDLExecutor() {
		if (conn != null) {
			backupIndexes();
			dropIndexes();
			backupTable();
			dropTable();
			createTable();
			createIndexes();
		}

	}

	private void backupIndexes() {

		String indexCreateSQL = "SELECT index_name, DBMS_METADATA.GET_DDL('INDEX', index_name)"
				+ " FROM user_indexes WHERE table_name = '" + TableName + "'";

		System.out.println("SQL : " + indexCreateSQL);
		ResultSet rs = null;
		try {
			Statement st = conn.createStatement();
			System.out.println("Query Executed");
			rs = st.executeQuery(indexCreateSQL);
			System.out.println("Result set Query Executed");
			while (rs.next()) {

				String indexName = rs.getString(1);
				String ddl = rs.getString(2);

				indexesdrop.add("DROP INDEX " + indexName);
				indexesCreate.add(ddl);
			}
			System.out.println("Index Create Query: \n" + indexesCreate);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void dropIndexes() {

		for (int i = 0; i < indexesdrop.size(); i++) {
			System.out.println(indexesdrop.get(i));

			try {
				Statement st = conn.createStatement();
				st.execute(indexesdrop.get(i));
				System.out.println(indexesCreate);
				System.out.println("Index Drop Query Successfully");

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void backupTable() {

		String tableCreateSQL = "SELECT DBMS_METADATA.GET_DDL('TABLE', '" + TableName + "') FROM DUAL";

		ResultSet rs = null;
		try {
			Statement st = conn.createStatement();
			rs = st.executeQuery(tableCreateSQL);
			if (rs.next()) {
				tableCreate = rs.getString(1);
			}
			System.out.println("Table Create Query: \n" + tableCreate);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void dropTable() {

		String tabledropSQL = "Drop table " + TableName;
		try {
			Statement st = conn.createStatement();
			st.execute(tabledropSQL);
			System.out.println("Table Drop Query: " + tabledropSQL);
			System.out.println("Table Dropped Successfully");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void createTable() {

		try {
			Statement st = conn.createStatement();
			st.execute(tableCreate);
			System.out.println("Table Create Query: " + tableCreate);
			System.out.println("Table Created Successfully");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void createIndexes() {

		for (int i = 0; i < indexesCreate.size(); i++) {
			System.out.println(indexesCreate.get(i));

			try {
				Statement st = conn.createStatement();
				st.execute(indexesCreate.get(i));
				System.out.println(indexesCreate);
				System.out.println("Indexes Created Successfully");

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
