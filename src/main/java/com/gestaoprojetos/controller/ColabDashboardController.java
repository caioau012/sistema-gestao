package com.gestaoprojetos.controller;

import java.time.LocalDate;
import java.util.List;

import com.gestaoprojetos.model.Tarefa;
import com.gestaoprojetos.model.Usuario;
import com.gestaoprojetos.service.AutenticacaoService;
import com.gestaoprojetos.util.NavigationUtil;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class ColabDashboardController {
    
    private final AutenticacaoService autenticacaoService;
    private final TarefaController tarefaController;
    
    @FXML private Label labelTotalTarefas;
    @FXML private Label labelTarefasPendentes;
    @FXML private Label labelTarefasAtrasadas;
    @FXML private Label labelUsuarioLogado;
    @FXML private ListView<String> listTarefasRecentes;
    
    public ColabDashboardController() {
        this.autenticacaoService = new AutenticacaoService();
        this.tarefaController = new TarefaController();
    }
    
    @FXML
    public void initialize() {
        carregarDadosDashboard();
    }
    
    private void carregarDadosDashboard() {
        Usuario usuarioLogado = autenticacaoService.getUsuarioLogado();
        if (usuarioLogado != null) {
            labelUsuarioLogado.setText("Bem-vindo, " + usuarioLogado.getNomeCompleto() + " (Colaborador)");
            carregarEstatisticasColaborador(usuarioLogado.getId());
            carregarTarefasRecentes(usuarioLogado.getId());
        }
    }
    
    private void carregarEstatisticasColaborador(int idColaborador) {
        List<Tarefa> minhasTarefas = tarefaController.listarTarefasPorResponsavel(idColaborador);
        List<Tarefa> tarefasPendentes = minhasTarefas.stream()
            .filter(t -> "pendente".equals(t.getStatus()))
            .toList();
        
        List<Tarefa> tarefasAtrasadas = minhasTarefas.stream()
            .filter(t -> "pendente".equals(t.getStatus()) && 
                        t.getDataFimPrev() != null && 
                        t.getDataFimPrev().isBefore(LocalDate.now()))
            .toList();
        
        labelTotalTarefas.setText(String.valueOf(minhasTarefas.size()));
        labelTarefasPendentes.setText(String.valueOf(tarefasPendentes.size()));
        labelTarefasAtrasadas.setText(String.valueOf(tarefasAtrasadas.size()));
    }
    
    private void carregarTarefasRecentes(int idColaborador) {
        List<Tarefa> tarefasRecentes = tarefaController.listarTarefasPorResponsavel(idColaborador);
        
        listTarefasRecentes.getItems().clear();
        for (Tarefa tarefa : tarefasRecentes) {
            String statusEmoji = "✅".equals(tarefa.getStatus()) ? "✅" : 
                               "em_execucao".equals(tarefa.getStatus()) ? "🟡" : "⏰";
            String item = String.format("%s %s - %s", statusEmoji, tarefa.getTitulo(), tarefa.getStatus());
            listTarefasRecentes.getItems().add(item);
        }
    }
    
    @FXML
    private void handleVisualizarTarefas() {
        NavigationUtil.carregarTela("/view/TarefaView.fxml");
    }
    
    @FXML
    private void handleAtualizarStatus() {
        NavigationUtil.mostrarInfo("Atualizar Status", "Selecione uma tarefa para atualizar o status");
    }
    
    @FXML
    private void handleLogout() {
        autenticacaoService.logout();
        NavigationUtil.carregarTela("/view/LoginView.fxml");
    }
    
    @FXML
    private void handleAtualizarDashboard() {
        Usuario usuarioLogado = autenticacaoService.getUsuarioLogado();
        if (usuarioLogado != null) {
            carregarEstatisticasColaborador(usuarioLogado.getId());
            carregarTarefasRecentes(usuarioLogado.getId());
            NavigationUtil.mostrarSucesso("Dashboard atualizado!");
        }
    }
}
