package com.gestaoprojetos.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.gestaoprojetos.model.Usuario;
import com.gestaoprojetos.util.DatabaseConnection;

public class UsuarioDao {
	public void create(Usuario usuario) throws SQLException{
		String sql = "INSERT INTO usuario (nome_completo, cpf, email, cargo, login, senha, perfil) VALUES (?, ?, ?, ?, ?, ?, ?)";
		try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
			stmt.setString(1,  usuario.getNomeCompleto());
			stmt.setString(2,  usuario.getCpf());
			stmt.setString(3, usuario.getEmail());
			stmt.setString(4,  usuario.getCargo());
			stmt.setString(5,  usuario.getLogin());
			stmt.setString(6, usuario.getSenha());
			stmt.setString(7, usuario.getPerfil());
			
			stmt.executeUpdate();
			
			try (ResultSet generatedKeys = stmt.getGeneratedKeys()){
				if (generatedKeys.next()) {
					usuario.setId(generatedKeys.getInt(1));
				}
			}
		}
	}
	
	public Usuario read(int id) throws SQLException {
		String sql = "SELECT * FROM usuario WHERE id_usuario = ?";
		Usuario usuario = null;
		
		try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setInt(1,  id);
			try (ResultSet rs = stmt.executeQuery()){
				if (rs.next()) {
					usuario = resultSetToUsuario(rs);
				}
			}
		}
		return usuario;
	}
	
	public List<Usuario> findAll() throws SQLException{
		List<Usuario> usuarios = new ArrayList<>();
		String sql = "SELECT * FROM usuario";
		
		try(Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement();ResultSet rs = stmt.executeQuery(sql)){
			while(rs.next()) {
				usuarios.add(resultSetToUsuario(rs));
			}
		}
		return usuarios;
	}
	
	public void update(Usuario usuario) throws SQLException {
		String sql = "UPDATE usuario SET nome_completo=?, cpf=?, email=?, cargo=?, login=?, senha=?, perfil=? WHERE id_usuario=?";
		
		try(Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){
			
			stmt.setString(1,  usuario.getNomeCompleto());
			stmt.setString(2,  usuario.getCpf());
			stmt.setString(3,  usuario.getEmail());
			stmt.setString(4,  usuario.getCargo());
			stmt.setString(5,  usuario.getLogin());
			stmt.setString(6,  usuario.getSenha());
			stmt.setString(7, usuario.getPerfil());
			stmt.setInt(8,  usuario.getId());
			
			stmt.executeUpdate();
		}
	}
	
	public void delete(int id) throws SQLException{
		String sql = "DELETE FROM usuario WHERE id_usuario=?";
		
		try(Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){
			
			stmt.setInt(1,  id);
			stmt.executeUpdate();
		}
	}
	
	public Usuario autenticar(String login, String senha) throws SQLException{
		String sql = "SELECT * FROM usuario WHERE login = ? AND senha = ?";
		Usuario usuario = null;
		
		try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){
			
			stmt.setString(1,  login);
			stmt.setString(2,  senha);
			
			try(ResultSet rs = stmt.executeQuery()){
				if (rs.next()) {
					usuario = resultSetToUsuario(rs);
				}
			}
		}
		return usuario;
	}
	
	public Usuario findByLogin(String login) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE login = ?";
        Usuario usuario = null;

        try (Connection conn = DatabaseConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, login);

            try(ResultSet rs = stmt.executeQuery()){
                if (rs.next()) {
                    usuario = resultSetToUsuario(rs);
                }
            }
        }
        return usuario;
    }
	
    public List<Usuario> findByPerfil(String perfil) throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuario WHERE perfil = ?";

        try (Connection conn = DatabaseConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, perfil);

            try(ResultSet rs = stmt.executeQuery()){
                while (rs.next()) {
                    usuarios.add(resultSetToUsuario(rs));
                }
            }
        }
        return usuarios;
    }
    
    public boolean loginExists(String login) throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuario WHERE login = ?";

        try (Connection conn = DatabaseConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, login);

            try(ResultSet rs = stmt.executeQuery()){
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
	
	public Usuario resultSetToUsuario(ResultSet rs) throws SQLException {
		return new Usuario(rs.getInt("id_usuario"), rs.getString("nome_completo"), rs.getString("cpf"), rs.getString("email"), rs.getString("cargo"), rs.getString("login"), rs.getString("senha"), rs.getString("perfil"));
	}	
}
