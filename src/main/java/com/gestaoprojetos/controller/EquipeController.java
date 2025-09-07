package com.gestaoprojetos.controller;

import java.sql.SQLException;
import java.util.List;

import com.gestaoprojetos.dao.EquipeDao;
import com.gestaoprojetos.dao.UsuarioDao;
import com.gestaoprojetos.model.Equipe;
import com.gestaoprojetos.model.Usuario;

public class EquipeController {
    private final EquipeDao equipeDao;
    private final UsuarioDao usuarioDao;
    
    public EquipeController() {
        this.equipeDao = new EquipeDao();
        this.usuarioDao = new UsuarioDao();
    }
    
    // CREATE
    public boolean criarEquipe(String nome, String descricao) {
        try {
            if (!validarDadosEquipe(nome)) return false;
            
            Equipe equipe = new Equipe(0, nome, descricao);
            equipeDao.create(equipe);
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao criar equipe: " + e.getMessage());
            return false;
        }
    }
    
    // READ
    public Equipe buscarEquipePorId(int id) {
        try {
            return equipeDao.read(id);
        } catch (SQLException e) {
            System.err.println("Erro ao buscar equipe: " + e.getMessage());
            return null;
        }
    }
    
    public List<Equipe> listarTodasEquipes() {
        try {
            return equipeDao.findAll();
        } catch (SQLException e) {
            System.err.println("Erro ao listar equipes: " + e.getMessage());
            return List.of();
        }
    }
    
    public List<Equipe> listarEquipesPorGerente(int idGerente) {
        try {
            // Implementar este método no EquipeDAO
            return equipeDao.findByGerente(idGerente);
        } catch (SQLException e) {
            System.err.println("Erro ao listar equipes do gerente: " + e.getMessage());
            return List.of();
        }
    }
    
    // UPDATE
    public boolean atualizarEquipe(int id, String nome, String descricao) {
        try {
            Equipe equipe = new Equipe(id, nome, descricao);
            equipeDao.update(equipe);
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar equipe: " + e.getMessage());
            return false;
        }
    }
    
    // DELETE
    public boolean excluirEquipe(int id) {
        try {
            equipeDao.delete(id);
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir equipe: " + e.getMessage());
            return false;
        }
    }
    
    // GERENCIAMENTO DE MEMBROS
    public boolean adicionarMembro(int idEquipe, int idUsuario) {
        try {
            return equipeDao.adicionarMembro(idEquipe, idUsuario);
        } catch (SQLException e) {
            System.err.println("Erro ao adicionar membro: " + e.getMessage());
            return false;
        }
    }
    
    public boolean removerMembro(int idEquipe, int idUsuario) {
        try {
            return equipeDao.removerMembro(idEquipe, idUsuario);
        } catch (SQLException e) {
            System.err.println("Erro ao remover membro: " + e.getMessage());
            return false;
        }
    }
    
    public List<Usuario> listarMembros(int idEquipe) {
        try {
            return equipeDao.listarMembros(idEquipe);
        } catch (SQLException e) {
            System.err.println("Erro ao listar membros: " + e.getMessage());
            return List.of();
        }
    }
    
    // ESTATÍSTICAS
    public int contarTotalEquipes() {
        try {
            return equipeDao.countAll();
        } catch (SQLException e) {
            System.err.println("Erro ao contar equipes: " + e.getMessage());
            return 0;
        }
    }
    
    public int contarEquipesPorGerente(int idGerente) {
        try {
            return equipeDao.countByGerente(idGerente);
        } catch (SQLException e) {
            System.err.println("Erro ao contar equipes do gerente: " + e.getMessage());
            return 0;
        }
    }
    
    public int contarMembros(int idEquipe) {
        try {
            return equipeDao.countMembros(idEquipe);
        } catch (SQLException e) {
            System.err.println("Erro ao contar membros: " + e.getMessage());
            return 0;
        }
    }
    
    // VALIDAÇÃO
    private boolean validarDadosEquipe(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            System.err.println("Nome da equipe é obrigatório");
            return false;
        }
        if (nome.length() < 3) {
            System.err.println("Nome da equipe deve ter pelo menos 3 caracteres");
            return false;
        }
        return true;
    }
}
