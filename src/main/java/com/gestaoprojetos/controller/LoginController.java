package com.gestaoprojetos.controller;

import com.gestaoprojetos.service.AutenticacaoService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    
    @FXML private TextField loginField;
    @FXML private PasswordField senhaField;
    @FXML private Label mensagemLabel;
    
    private final AutenticacaoService autenticacaoService = new AutenticacaoService();
    
    @FXML
    private void handleLogin() {
        String login = loginField.getText();
        String senha = senhaField.getText();
        
        if (login.isEmpty() || senha.isEmpty()) {
            mensagemLabel.setText("Preencha todos os campos!");
            return;
        }
        
        if (autenticacaoService.login(login, senha)) {
            carregarDashboard();
        } else {
            mensagemLabel.setText("Login ou senha inválidos!");
        }
    }
    
    private void carregarDashboard() {
        try {
            String perfil = autenticacaoService.getUsuarioLogado().getPerfil();
            String fxmlFile = "";
            
            switch (perfil) {
                case "administrador":
                    fxmlFile = "/view/AdminDashboardView.fxml";
                    break;
                case "gerente":
                    fxmlFile = "/view/GerenteDashboardView.fxml";
                    break;
                case "colaborador":
                    fxmlFile = "/view/ColabDashboardView.fxml";
                    break;
            }
            
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Stage stage = (Stage) loginField.getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 700));
            stage.setTitle("Dashboard - Sistema de Gestão");
            
        } catch (Exception e) {
            mensagemLabel.setText("Erro: " + e.getMessage());
        }
    }
    
}
