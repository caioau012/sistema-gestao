package com.gestaoprojetos.controller;

import java.sql.SQLException;
import java.util.List;

import com.gestaoprojetos.dao.UsuarioDao;
import com.gestaoprojetos.model.Usuario;

public class UsuarioController {
    private final UsuarioDao usuarioDao;
    
    public UsuarioController() {
        this.usuarioDao = new UsuarioDao();
    }
    
    // CREATE
    public boolean criarUsuario(String nome, String cpf, String email, 
                              String cargo, String login, String senha, 
                              String perfil) {
        try {
            if (!validarDadosUsuario(nome, cpf, email, login, senha)) {
                return false;
            }
            
            if (usuarioDao.loginExists(login)) {
                System.err.println("Login já existe!");
                return false;
            }
            
            Usuario usuario = new Usuario(0, nome, cpf, email, cargo, login, senha, perfil);
            usuarioDao.create(usuario);
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao criar usuário: " + e.getMessage());
            return false;
        }
    }
    
    // READ
    public Usuario buscarUsuarioPorId(int id) {
        try {
            return usuarioDao.read(id);
        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário: " + e.getMessage());
            return null;
        }
    }
    
    public Usuario buscarUsuarioPorLogin(String login) {
        try {
            return usuarioDao.findByLogin(login);
        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário por login: " + e.getMessage());
            return null;
        }
    }
    
    public List<Usuario> listarTodosUsuarios() {
        try {
            return usuarioDao.findAll();
        } catch (SQLException e) {
            System.err.println("Erro ao listar usuários: " + e.getMessage());
            return List.of();
        }
    }
    
    public List<Usuario> listarUsuariosPorPerfil(String perfil) {
        try {
            return usuarioDao.findByPerfil(perfil);
        } catch (SQLException e) {
            System.err.println("Erro ao listar usuários por perfil: " + e.getMessage());
            return List.of();
        }
    }
    
    // UPDATE
    public boolean atualizarUsuario(int id, String nome, String cpf, String email, 
                                  String cargo, String login, String senha, 
                                  String perfil) {
        try {
            Usuario usuario = new Usuario(id, nome, cpf, email, cargo, login, senha, perfil);
            usuarioDao.update(usuario);
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar usuário: " + e.getMessage());
            return false;
        }
    }
    
    // DELETE
    public boolean excluirUsuario(int id) {
        try {
            usuarioDao.delete(id);
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir usuário: " + e.getMessage());
            return false;
        }
    }
    
    // VALIDAÇÃO
    private boolean validarDadosUsuario(String nome, String cpf, String email, 
                                      String login, String senha) {
        if (nome == null || nome.trim().isEmpty()) {
            System.err.println("Nome é obrigatório");
            return false;
        }
        
        if (cpf == null || cpf.length() != 11) {
            System.err.println("CPF deve ter 11 dígitos");
            return false;
        }
        
        if (email == null || !email.contains("@")) {
            System.err.println("Email inválido");
            return false;
        }
        
        if (login == null || login.trim().isEmpty()) {
            System.err.println("Login é obrigatório");
            return false;
        }
        
        if (senha == null || senha.length() < 6) {
            System.err.println("Senha deve ter pelo menos 6 caracteres");
            return false;
        }
        
        return true;
    }
    
    // VERIFICAÇÕES
    public boolean loginExiste(String login) {
        try {
            return usuarioDao.loginExists(login);
        } catch (SQLException e) {
            System.err.println("Erro ao verificar login: " + e.getMessage());
            return false;
        }
    }
    
    public boolean emailExiste(String email) {
        try {
            return usuarioDao.emailExists(email);
        } catch (SQLException e) {
            System.err.println("Erro ao verificar email: " + e.getMessage());
            return false;
        }
    }
    
    public boolean cpfExiste(String cpf) {
        try {
            return usuarioDao.cpfExists(cpf);
        } catch (SQLException e) {
            System.err.println("Erro ao verificar CPF: " + e.getMessage());
            return false;
        }
    }
}