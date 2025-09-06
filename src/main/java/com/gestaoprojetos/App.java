package com.gestaoprojetos;

import java.sql.SQLException;

import com.gestaoprojetos.util.DatabaseConnection;
import com.gestaoprojetos.util.NavigationUtil;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            testarConexaoBanco();
            
            primaryStage.setTitle("Sistema de Gestão de Projetos 🚀");
            primaryStage.setWidth(1000);
            primaryStage.setHeight(700);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            
            NavigationUtil.carregarTela("/view/LoginView.fxml");
            
            primaryStage.show();
            
            System.out.println("✅ Sistema iniciado com sucesso!");
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao iniciar sistema: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void testarConexaoBanco() {
        try {
            DatabaseConnection.getConnection();
            System.out.println("✅ Conexão com banco de dados estabelecida!");
            
            criarUsuarioAdminPadrao();
            
        } catch (SQLException e) {
            System.err.println("❌ Erro na conexão com banco: " + e.getMessage());
        }
    }
    
    private void criarUsuarioAdminPadrao() {
        try {

            System.out.println("👤 Verificando usuário admin padrão...");
            
        } catch (Exception e) {
            System.err.println("⚠️  Não foi possível verificar usuário admin: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        System.out.println("🚀 Iniciando Sistema de Gestão de Projetos...");
        launch(args);
    }
}
