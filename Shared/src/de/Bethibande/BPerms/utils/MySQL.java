package de.Bethibande.BPerms.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQL {
	
	private final String HOST;
	private final String DATABASE;
	private final String USER;
	private final String PASSWORD;
	private final int PORT;
	
	private Connection con;

	public static MySQL INSTANCE;

	public MySQL(String host, String database, String user, String password, int port) {
		this.HOST = host;
		this.DATABASE = database;
		this.USER = user;
		this.PASSWORD = password;
		this.PORT = port;

		connect();
	}

	public void connect() {
		try {
			con = DriverManager.getConnection("jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE + "?autoReconnect=true",
					USER, PASSWORD);
			System.out.println("[MySQL] connected!");
		} catch (SQLException e) {
			System.out.println("[MySQL] connection failed! Error: " + e.getMessage());
		}
	}

	public void close() {
		try {
			if (con != null) {
				con.close();
				System.out.println("[MySQL] Die Verbindung zur MySQL wurde Erfolgreich beendet!");
			}
		} catch (SQLException e) {
			System.out.println("[MySQL] Fehler beim beenden der Verbindung zur MySQL! Fehler: " + e.getMessage());
		}
	}

	public void update(String qry) {
		Statement st = null;
		try {
			st = con.createStatement();
			st.executeUpdate(qry);
		} catch (Exception e) {
			connect();
			update2(qry);
			System.err.println(e);
		}
		closeStatement(st);
	}
	private void update2(String qry) {
		Statement st = null;
		try {
			st = con.createStatement();
			st.executeUpdate(qry);
		} catch (Exception e) {
			connect();
			System.err.println(e);
		}
		closeStatement(st);
	}

	public ResultSet query(String qry) {
		ResultSet rs = null;
		try {
			Statement st = con.createStatement();
			rs = st.executeQuery(qry);
		} catch (SQLException e) {
			connect();
			System.err.println(e);
			return query2(qry);
		}
		return rs;
	}

	private ResultSet query2(String qry) {
		ResultSet rs = null;
		try {
			Statement st = con.createStatement();
			rs = st.executeQuery(qry);
		} catch (SQLException e) {
			connect();
			System.err.println(e);
		}
		return rs;
	}
	
	public static void closeStatement(Statement st){
		if(st != null)
			try {
				st.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	public static void closeResultset(ResultSet rs){
		if(rs != null)
			try {
				rs.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
	}

}