package com.gestaoprojetos.controller;

import java.util.List;

import com.gestaoprojetos.model.Projeto;
import com.gestaoprojetos.model.Tarefa;
import com.gestaoprojetos.model.Usuario;
import com.gestaoprojetos.service.AutenticacaoService;
import com.gestaoprojetos.util.NavigationUtil;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class GerenteDashboardController {
    
    private final AutenticacaoService autenticacaoService;
    private final ProjetoController projetoController;
    private final TarefaController tarefaController;
    private final EquipeController equipeController;
    
    @FXML private Label labelTotalProjetos;
    @FXML private Label labelProjetosAndamento;
    @FXML private Label labelProjetosAtrasados;
    @FXML private Label labelTotalTarefas;
    @FXML private Label labelTarefasPendentes;
    @FXML private Label labelTarefasAtrasadas;
    @FXML private Label labelTotalEquipes;
    @FXML private Label labelUsuarioLogado;
    
    public GerenteDashboardController() {
        this.autenticacaoService = new AutenticacaoService();
        this.projetoController = new ProjetoController();
        this.tarefaController = new TarefaController();
        this.equipeController = new EquipeController();
    }
    
    @FXML
    public void initialize() {
        carregarDadosDashboard();
    }
    
    private void carregarDadosDashboard() {
        Usuario usuarioLogado = autenticacaoService.getUsuarioLogado();
        if (usuarioLogado != null) {
            labelUsuarioLogado.setText("Bem-vindo, " + usuarioLogado.getNomeCompleto() + " (Gerente)");
            carregarEstatisticasGerente(usuarioLogado.getId());
        }
    }
    
    private void carregarEstatisticasGerente(int idGerente) {
        // Apenas projetos do gerente
        List<Projeto> meusProjetos = projetoController.listarProjetosPorGerente(idGerente);
        List<Projeto> projetosAndamento = meusProjetos.stream()
            .filter(p -> "em_andamento".equals(p.getStatus()))
            .toList();
        List<Projeto> projetosAtrasados = projetoController.listarProjetosAtrasados().stream()
            .filter(p -> p.getGerente().getId() == idGerente)
            .toList();
        
        // Tarefas dos projetos do gerente
        List<Tarefa> tarefasProjetos = meusProjetos.stream()
            .flatMap(p -> tarefaController.listarTarefasPorProjeto(p.getId()).stream())
            .toList();
        List<Tarefa> tarefasPendentes = tarefasProjetos.stream()
            .filter(t -> "pendente".equals(t.getStatus()))
            .toList();
        List<Tarefa> tarefasAtrasadas = tarefasProjetos.stream()
            .filter(t -> "pendente".equals(t.getStatus()) && t.getDataFimPrev() != null)
            .toList();
        
        // Equipes do gerente (implementar este método no EquipeController)
        int minhasEquipes = equipeController.contarEquipesPorGerente(idGerente);
        
        labelTotalProjetos.setText(String.valueOf(meusProjetos.size()));
        labelProjetosAndamento.setText(String.valueOf(projetosAndamento.size()));
        labelProjetosAtrasados.setText(String.valueOf(projetosAtrasados.size()));
        labelTotalTarefas.setText(String.valueOf(tarefasProjetos.size()));
        labelTarefasPendentes.setText(String.valueOf(tarefasPendentes.size()));
        labelTarefasAtrasadas.setText(String.valueOf(tarefasAtrasadas.size()));
        labelTotalEquipes.setText(String.valueOf(minhasEquipes));
    }
    
    @FXML
    private void handleGerenciarProjetos() {
        NavigationUtil.carregarTela("/view/ProjetoView.fxml");
    }
    
    @FXML
    private void handleGerenciarTarefas() {
        NavigationUtil.carregarTela("/view/TarefaView.fxml");
    }
    
    @FXML
    private void handleGerenciarEquipes() {
        NavigationUtil.carregarTela("/view/EquipeView.fxml");
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
            carregarEstatisticasGerente(usuarioLogado.getId());
        }
    }
}
