package com.gestaoprojetos.service;


import java.sql.SQLException;

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

    public boolean temPermissao(String perfilRequerido){
        if (usuarioLogado == null){
            return false;
        }

        switch (perfilRequerido){
            case "administrador":
                return "administrador".equals(usuarioLogado.getPerfil());
            case "gerente":
                return "gerente".equals(usuarioLogado.getPerfil());
            case "colaborador": 
                return true;
            default:
                return false;
        }
    }
}
