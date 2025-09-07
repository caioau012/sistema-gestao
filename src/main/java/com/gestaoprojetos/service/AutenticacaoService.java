package com.gestaoprojetos.service;


import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.gestaoprojetos.dao.UsuarioDao;
import com.gestaoprojetos.model.Usuario;

public class AutenticacaoService{
    private Usuario usuarioLogado;
    private final UsuarioDao usuarioDao;

    public AutenticacaoService(){
        this.usuarioDao = new UsuarioDao();
    }

    public boolean login(String login, String senha){
        try{
            Usuario usuario = usuarioDao.autenticar(login, senha);
            if (usuario != null){
                this.usuarioLogado = usuario;
                return true;
            }
            return false;
        }
        catch (SQLException e){
            System.err.println("Erro ao autenticar: " + e.getMessage());
            return false;
        }
    }

    public void logout(){
        this.usuarioLogado = null;
    }

    public Usuario getUsuarioLogado(){
        return usuarioLogado;
    }

    public boolean temPermissao(String recurso) {
        if (usuarioLogado == null) return false;
        
        String perfil = usuarioLogado.getPerfil();
        
        Map<String, List<String>> permissoes = Map.of(
            "administrador", List.of("usuarios", "projetos", "tarefas", "equipes", "relatorios"),
            "gerente", List.of("projetos", "tarefas", "equipes"),
            "colaborador", List.of("tarefas")
        );
        
        return permissoes.getOrDefault(perfil, List.of()).contains(recurso);
    }
}
