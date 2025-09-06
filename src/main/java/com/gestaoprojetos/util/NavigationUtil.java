package com.gestaoprojetos.util;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NavigationUtil {
    public static void carregarTela(String fxmlFile){
        try{
            
            FXMLLoader loader = new FXMLLoader(NavigationUtil.class.getResource(fxmlFile));
            Parent root = loader.load();

            Stage stage = getStageAtual();

            double width = stage.getWidth();
            double height = stage.getHeight();

            Scene scene = new Scene(root, width, height);

            stage.setScene(scene);
            stage.centerOnScreen();
        }
        catch (IOException e){
            System.err.println("ERRO: Não foi possível carregar a tela: " + fxmlFile);
            e.printStackTrace();
        }
    }

    private static Stage getStageAtual() {
        return (Stage) javafx.stage.Window.getWindows().get(0);
    }

    public static void mostrarErro(String mensagem){
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText("Ocorreu um erro");
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    public static void mostrarSucesso(String mensagem) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
