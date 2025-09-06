package com.gestaoprojetos.controller;

import java.time.LocalDate;
import java.util.List;

import com.gestaoprojetos.model.Projeto;
import com.gestaoprojetos.model.Tarefa;
import com.gestaoprojetos.model.Usuario;
import com.gestaoprojetos.service.AutenticacaoService;
import com.gestaoprojetos.util.NavigationUtil;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController{
    
    private final AutenticacaoService autenticacaoService;
    private final ProjetoController projetoController;
    private final TarefaController tarefaController;
    private final UsuarioController usuarioController;

    @FXML private Label labelTotalProjetos;
    @FXML private Label labelProjetosAndamento;
    @FXML private Label labelProjetosAtrasados;
    @FXML private Label labelTotalTarefas;
    @FXML private Label labelTarefasPendentes;
    @FXML private Label labelTarefasAtrasadas;
    @FXML private Label labelUsuarioLogado;

    public DashboardController(){
        this.autenticacaoService = new AutenticacaoService();
        this.projetoController = new ProjetoController();
        this.tarefaController = new TarefaController();
        this.usuarioController = new UsuarioController();
    }

    @FXML
    public void initialize(){
        carregarDadosDashboard();
    }

    private void carregarDadosDashboard(){
        Usuario usuarioLogado = autenticacaoService.getUsuarioLogado();
        if (usuarioLogado != null){
            labelUsuarioLogado.setText("Bem vindo, " + usuarioLogado.getNomeCompleto());

            if("administrador".equals(usuarioLogado.getPerfil())){
                carregarEstatisticasAdmin();
            }
            else if("gerente".equals(usuarioLogado.getPerfil())){
                carregarEstatisticasGerente(usuarioLogado.getId());
            }
            else{
                carregarEstatisticasColaborador(usuarioLogado.getId());
            }
        }
    }

    private void carregarEstatisticasAdmin(){
        List<Projeto> todosProjetos = projetoController.listarTodosProjetos();
        List<Projeto> projetosAndamento = projetoController.listarProjetosPorStatus("em_andamento");
        List<Projeto> projetosAtrasados = projetoController.listarProjetosAtrasados();

        List<Tarefa> todasTarefas = tarefaController.listarTodasTarefas();
        List<Tarefa> tarefasPendentes = tarefaController.listarTarefasPorStatus("pendente");
        List<Tarefa> tarefasAtrasadas = tarefaController.listarTarefasAtrasadas();

        labelTotalProjetos.setText(String.valueOf(todosProjetos.size()));
        labelProjetosAndamento.setText(String.valueOf(projetosAndamento.size()));
        labelProjetosAtrasados.setText(String.valueOf(projetosAtrasados.size()));
        
        labelTotalTarefas.setText(String.valueOf(todasTarefas.size()));
        labelTarefasPendentes.setText(String.valueOf(tarefasPendentes.size()));
        labelTarefasAtrasadas.setText(String.valueOf(tarefasAtrasadas.size()));
    }

    private void carregarEstatisticasGerente(int idGerente){
        List<Projeto> meusProjetos = projetoController.listarProjetosPorGerente(idGerente);
        List<Projeto> projetosAndamento = meusProjetos.stream().filter(p -> "em_andamento".equals(p.getStatus())).toList();
        List<Projeto> projetosAtrasados = projetoController.listarProjetosAtrasados().stream().filter(p -> p.getGerente().getId() == idGerente).toList();

        List<Tarefa> minhasTarefas = meusProjetos.stream().flatMap(p -> tarefaController.listarTarefasPorProjeto(p.getId()).stream()).toList();
        List<Tarefa> tarefasPendentes = minhasTarefas.stream().filter(t -> "pendente".equals(t.getStatus())).toList();
        List<Tarefa> tarefasAtrasadas = tarefaController.listarTarefasAtrasadas().stream().filter(t -> t.getProjeto().getGerente().getId() == idGerente).toList();

        labelTotalProjetos.setText(String.valueOf(meusProjetos.size()));
        labelProjetosAndamento.setText(String.valueOf(projetosAndamento.size()));
        labelProjetosAtrasados.setText(String.valueOf(projetosAtrasados.size()));
        labelTotalTarefas.setText(String.valueOf(minhasTarefas.size()));
        labelTarefasPendentes.setText(String.valueOf(tarefasPendentes.size()));
        labelTarefasAtrasadas.setText(String.valueOf(tarefasAtrasadas.size()));
    }

    public void carregarEstatisticasColaborador(int idColaborador){
        
        List<Tarefa> minhasTarefas = tarefaController.listarTarefasPorResponsavel(idColaborador);
        List<Tarefa> tarefasPendentes = minhasTarefas.stream().filter(t -> "pendente".equals(t.getStatus())).toList();
        List<Tarefa> tarefasAtrasadas = minhasTarefas.stream().filter(t -> "pendente".equals(t.getStatus()) && t.getDataFimPrev() != null && t.getDataFimPrev().isBefore(LocalDate.now())).toList();

        List<Projeto> meusProjetos = minhasTarefas.stream().map(Tarefa::getProjeto).distinct().toList();
        List<Projeto> projetosAndamento = meusProjetos.stream().filter(p -> "em_andamento".equals(p.getStatus())).toList();

        labelTotalProjetos.setText(String.valueOf(meusProjetos.size()));
        labelProjetosAndamento.setText(String.valueOf(projetosAndamento.size()));
        labelProjetosAtrasados.setText("0");
        labelTotalTarefas.setText(String.valueOf(minhasTarefas.size()));
        labelTarefasPendentes.setText(String.valueOf(tarefasPendentes.size()));
        labelTarefasAtrasadas.setText(String.valueOf(tarefasAtrasadas.size()));
    }

    @FXML
    private void handleGerenciarUsuarios(){
        NavigationUtil.carregarTela("/view/UsuarioView.fxml");
    }

    @FXML
    private void handleGerenciarProjetos(){
        NavigationUtil.carregarTela("/view/ProjetoView.fxml");
    }

    @FXML
    private void handleGerenciarTarefas(){
        NavigationUtil.carregarTela("/view/TarefaView.fxml");
    }

    @FXML
    private void handleGerenciarEquipes() {
        NavigationUtil.carregarTela("/view/EquipeViwe.fxml");
    }

    @FXML
    private void handleLogout(){
        autenticacaoService.logout();
        NavigationUtil.carregarTela("/view/LoginView.fxml");
    }

    @FXML
    private void handleAtualizarDashboard(){
        carregarDadosDashboard();
    }
}


