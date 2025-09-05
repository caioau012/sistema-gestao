package com.gestaoprojetos.controller;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import com.gestaoprojetos.dao.ProjetoDao;
import com.gestaoprojetos.dao.TarefaDao;
import com.gestaoprojetos.dao.UsuarioDao;
import com.gestaoprojetos.model.Projeto;
import com.gestaoprojetos.model.Tarefa;
import com.gestaoprojetos.model.Usuario;

public class TarefaController{
    private final TarefaDao tarefaDao;
    private final ProjetoDao projetoDao;
    private final UsuarioDao usuarioDao;
    
    public TarefaController(){
        this.tarefaDao = new TarefaDao();
        this.projetoDao = new ProjetoDao();
        this.usuarioDao = new UsuarioDao();
    }

    public boolean criarTarefa(String titulo, String descricao, String status, LocalDate dataInicioPrev, LocalDate dataFimPrev, int idProjeto, int idResponsavel){
        try{
            if (!validarDadosTarefa(titulo, idProjeto, idResponsavel)){
                return false;
            }

            Projeto projeto = projetoDao.read(idProjeto);
            Usuario responsavel = usuarioDao.read(idResponsavel);

            if (projeto == null || responsavel == null){
                System.err.println("Projeto ou responsável não encontrado!");
            }

            Tarefa tarefa = new Tarefa(0, titulo, descricao, status, dataInicioPrev, dataFimPrev, null, null, projeto, responsavel);
            tarefaDao.create(tarefa);
            return true;
        }
        catch (SQLException e){
            System.err.println("Erro ao criar tarefa: " + e.getMessage());
            return false;
        }
    }

    public Tarefa buscarTarefaPorId(int id){
        try{
            return tarefaDao.read(id);
        }
        catch (SQLException e){
            System.err.println("Erro ao buscar tarefa: " + e.getMessage());
            return null;
        }
    }

    public List<Tarefa> listarTodasTarefas(){
        try{
            return tarefaDao.findAll();
        }
        catch (SQLException e){
            System.err.println("Erro ao listar tarefas: " + e.getMessage());
            return List.of();
        }
    }

    public List<Tarefa> listarTarefasPorProjeto(int idProjeto){
        try{
            return tarefaDao.findByProjeto(idProjeto);
        }
        catch (SQLException e){
            System.err.println("Erro ao listar tarefas por projeto: " + e.getMessage());
            return List.of();
        }
    }
    public List<Tarefa> listarTarefasPorResponsavel(int idResponsavel){
        try{
            return tarefaDao.findByResponsavel(idResponsavel);
        }
        catch (SQLException e){
            System.err.println("Erro ao listar tarefas por responsável: " + e.getMessage());
            return List.of();
        }
    }

    public List<Tarefa> listarTarefasPorStatus(String status){
        try{
            return tarefaDao.findByStatus(status);
        }
        catch (SQLException e){
            System.err.println("Erro ao listar tarefas por status: " + e.getMessage());
            return List.of();
        }
    }

    public boolean atualizarTarefa(int id, String titulo, String descricao, String status, LocalDate dataInicioPrev, LocalDate dataFimPrev, LocalDate dataInicioReal, LocalDate dataFimReal, int idProjeto, int idResponsavel){
        try{
            Projeto projeto = projetoDao.read(idProjeto);
            Usuario responsavel = usuarioDao.read(idResponsavel);

            if (projeto == null || responsavel == null){
                System.err.println("Projeto ou responsável não encontrado!");
                return false;
            }

            Tarefa tarefa = new Tarefa(id, titulo, descricao, status, dataInicioPrev, dataFimPrev, dataInicioReal, dataFimReal, projeto, responsavel);
            tarefaDao.update(tarefa);
            return true;
        }
        catch (SQLException e){
            System.err.println("Erro ao atualizar tarefa: " + e.getMessage());
            return false;
        }
    }

    public boolean atualizarStatusTarefa(int id, String novoStatus) {
        try{
            Tarefa tarefa = tarefaDao.read(id);
            if (tarefa != null){
                tarefa.setStatus (novoStatus);

                if ("concluida".equals(novoStatus) && tarefa.getDataFimReal() == null){
                    tarefa.setDataFimReal(LocalDate.now());
                }

                tarefaDao.update(tarefa);
                return true;
            }
            return false;
        }
        catch (SQLException e) {
            System.err.println("Erro ao atualizar status da tarefa: " + e.getMessage());
            return false;
        }
    }

    public boolean registrarInicioReal(int id) {
        try{
            Tarefa tarefa = tarefaDao.read(id);
            if (tarefa != null){
                tarefa.setDataInicioReal(LocalDate.now());
                tarefa.setStatus("em_execucao");
                tarefaDao.update(tarefa);
                return true;
            }
            return false;
        }
        catch (SQLException e){
            System.err.println("Erro ao registrar início real: " + e.getMessage());
            return false;
        }
    }

    public boolean excluirTarefa(int id){
        try{
            tarefaDao.delete(id);
            return true;
        }
        catch (SQLException e){
            System.err.println("Erro ao excluir tarefa: " + e.getMessage());
            return false;
        }
    }

    private boolean validarDadosTarefa(String titulo, int idProjeto, int idResponsavel){
        if (titulo == null || titulo.trim().isEmpty()){
            System.err.println("Título da tarefa é obrigatório");
            return false;
        }

        if (idProjeto <= 0){
            System.err.println("ID do projeto inválido");
            return false;
        }

        if (idResponsavel <= 0){
            System.err.println("ID do responsável inválido");
            return false;
        }
        return true;
    }

    public List<Tarefa> listarTarefasAtrasadas(){
        try{
            return tarefaDao.findTarefasAtrasadas();
        }
        catch (SQLException e){
            System.err.println("Erro ao buscar tarefas atrasadas: " + e.getMessage());
            return List.of();
        }
    }

    public String calcularProgressoProjeto(int idProjeto){
        try{
            List<Tarefa> tarefas = tarefaDao.findByProjeto(idProjeto);
            if (tarefas.isEmpty()) {
                return "0%";
            }

            long total = tarefas.size();
            long concluidas = tarefas.stream().filter(t -> "concluida".equals(t.getStatus())).count();

            double progresso = (concluidas * 100.0) / total;
            return String.format("%.1f%%", progresso);
        }
        catch (SQLException e){
            System.err.println("Erro ao calcular progresso: " + e.getMessage());
            return "0%";
        }
    }
}