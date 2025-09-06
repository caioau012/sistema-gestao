package com.gestaoprojetos.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static Connection connection;
    
    private static final String URL = "jdbc:mysql://localhost:3306/gestao_projetos";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456789";
    
    private DatabaseConnection() {}
    
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                Properties props = new Properties();
                props.setProperty("user", USERNAME);
                props.setProperty("password", PASSWORD);
                props.setProperty("useSSL", "false");
                props.setProperty("serverTimezone", "UTC");
                props.setProperty("allowPublicKeyRetrieval", "true");
                
                connection = DriverManager.getConnection(URL, props);
                System.out.println("✅ Conexão com MySQL estabelecida!");
                
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver MySQL não encontrado: " + e.getMessage());
            }
        }
        return connection;
    }
    
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✅ Conexão com MySQL fechada!");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao fechar conexão: " + e.getMessage());
        }
    }
    
    public static boolean testarConexao() {
        try {
            getConnection();
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Falha na conexão: " + e.getMessage());
            return false;
        }
    }
}
