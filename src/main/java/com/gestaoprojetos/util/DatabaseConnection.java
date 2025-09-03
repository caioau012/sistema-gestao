package com.gestaoprojetos.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
		private static Connection conn;
		
		private DatabaseConnection() {
		}
		
		public static Connection getConnection() throws SQLException{
			if (conn == null || conn.isClosed()) {
				Properties props = new Properties();
				props.setProperty("user", "root");
				props.setProperty("password", "sua_senha");
				props.setProperty("useSSL", "false");
				props.setProperty("serverTimezone", "UTC");
				
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gestao_projetos", props);
			}
			return conn;
		}
		
		public static void closeConnection() throws SQLException{
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}
		}
}
