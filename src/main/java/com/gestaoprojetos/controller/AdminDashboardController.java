package com.gestaoprojetos.controller;

import java.util.List;

import com.gestaoprojetos.model.Projeto;
import com.gestaoprojetos.model.Tarefa;
import com.gestaoprojetos.model.Usuario;
import com.gestaoprojetos.service.AutenticacaoService;
import com.gestaoprojetos.util.NavigationUtil;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AdminDashboardController {
    
    private final AutenticacaoService autenticacaoService;
    private final ProjetoController projetoController;
    private final TarefaController tarefaController;
    private final UsuarioController usuarioController;
    private final EquipeController equipeController;
    
    @FXML private Label labelTotalProjetos;
    @FXML private Label labelProjetosAndamento;
    @FXML private Label labelProjetosAtrasados;
    @FXML private Label labelTotalTarefas;
    @FXML private Label labelTarefasPendentes;
    @FXML private Label labelTarefasAtrasadas;
    @FXML private Label labelTotalUsuarios;
    @FXML private Label labelTotalEquipes;
    @FXML private Label labelUsuarioLogado;
    
    public AdminDashboardController() {
        this.autenticacaoService = new AutenticacaoService();
        this.projetoController = new ProjetoController();
        this.tarefaController = new TarefaController();
        this.usuarioController = new UsuarioController();
        this.equipeController = new EquipeController();
    }
    
    @FXML
    public void initialize() {
        carregarDadosDashboard();
    }
    
    private void carregarDadosDashboard() {
        Usuario usuarioLogado = autenticacaoService.getUsuarioLogado();
        if (usuarioLogado != null) {
            labelUsuarioLogado.setText("Bem-vindo, " + usuarioLogado.getNomeCompleto() + " (Administrador)");
            carregarEstatisticasCompletas();
        }
    }
    
    private void carregarEstatisticasCompletas() {
        // Estatísticas de Projetos
        List<Projeto> todosProjetos = projetoController.listarTodosProjetos();
        List<Projeto> projetosAndamento = projetoController.listarProjetosPorStatus("em_andamento");
        List<Projeto> projetosAtrasados = projetoController.listarProjetosAtrasados();
        
        // Estatísticas de Tarefas
        List<Tarefa> todasTarefas = tarefaController.listarTodasTarefas();
        List<Tarefa> tarefasPendentes = tarefaController.listarTarefasPorStatus("pendente");
        List<Tarefa> tarefasAtrasadas = tarefaController.listarTarefasAtrasadas();
        
        // Estatísticas de Usuários e Equipes
        List<Usuario> todosUsuarios = usuarioController.listarTodosUsuarios();
        int totalEquipes = equipeController.contarTotalEquipes();
        
        // Atualizar labels
        labelTotalProjetos.setText(String.valueOf(todosProjetos.size()));
        labelProjetosAndamento.setText(String.valueOf(projetosAndamento.size()));
        labelProjetosAtrasados.setText(String.valueOf(projetosAtrasados.size()));
        labelTotalTarefas.setText(String.valueOf(todasTarefas.size()));
        labelTarefasPendentes.setText(String.valueOf(tarefasPendentes.size()));
        labelTarefasAtrasadas.setText(String.valueOf(tarefasAtrasadas.size()));
        labelTotalUsuarios.setText(String.valueOf(todosUsuarios.size()));
        labelTotalEquipes.setText(String.valueOf(totalEquipes));
    }
    
    @FXML
    private void handleGerenciarUsuarios() {
        NavigationUtil.carregarTela("/view/UsuarioView.fxml");
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
    private void handleGerarRelatorios() {
        NavigationUtil.mostrarInfo("Relatórios", "Funcionalidade de relatórios em desenvolvimento");
    }
    
    @FXML
    private void handleLogout() {
        autenticacaoService.logout();
        NavigationUtil.carregarTela("/view/LoginView.fxml");
    }
    
    @FXML
    private void handleAtualizarDashboard() {
        carregarEstatisticasCompletas();
        NavigationUtil.mostrarSucesso("Dashboard atualizado!");
    }
}
