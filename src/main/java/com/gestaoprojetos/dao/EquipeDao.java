package com.gestaoprojetos.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.gestaoprojetos.model.Equipe;
import com.gestaoprojetos.model.Usuario;
import com.gestaoprojetos.util.DatabaseConnection;

public class EquipeDao {
	private final UsuarioDao usuarioDao;
	
	public EquipeDao() {
		this.usuarioDao = new UsuarioDao();
	}
	
	public void create(Equipe equipe) throws SQLException {
		String sql = "INSERT INTO equipe (nome, descricao) VALUES (?, ?)";
		
		try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
			
			stmt.setString(1,  equipe.getNome());
			stmt.setString(2, equipe.getDescricao());
			
			stmt.executeUpdate();
			
			try (ResultSet generatedKeys = stmt.getGeneratedKeys()){
				if(generatedKeys.next()) {
					equipe.setId(generatedKeys.getInt(1));
				}
			}
		}
	}
	
	public Equipe read(int id) throws SQLException {
		String sql = "SELECT * FROM equipe WHERE id_equipe = ?";
		Equipe equipe = null;
		
		try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){
			
			stmt.setInt(1, id);
			try(ResultSet rs = stmt.executeQuery()){
				if (rs.next()) {
					equipe = resultSetToEquipe(rs);
				}
			}
		}
		return equipe;
	}
	
	public List<Equipe> findAll() throws SQLException{
		List<Equipe> equipes = new ArrayList<>();
		String sql = "SELECT * FROM equipe";
		
		try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)){
			while (rs.next()) {
				equipes.add(resultSetToEquipe(rs));
			}
		}
		return equipes;
	}
	
	public List<Equipe> findByNome(String nome) throws SQLException {
		List<Equipe> equipes = new ArrayList<>();
		String sql = "SELECT * FROM equipe WHERE nome LIKE ?";
		
		try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){
			
			stmt.setString(1,  "%" + nome + "%");
			try (ResultSet rs = stmt.executeQuery()){
				while (rs.next()) {
					equipes.add(resultSetToEquipe(rs));
				}
			}
		}
		return equipes;
	}
	
	public void update(Equipe equipe) throws SQLException{
	    String sql = "UPDATE equipe SET nome = ?, descricao = ?, WHERE id_equipe = ?";

	    try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){

	        stmt.setString(1, equipe.getNome());
	        stmt.setString(2, equipe.getDescricao());
	        stmt.setInt(3, equipe.getId());

	        stmt.executeUpdate();
	    }
	}

	public void delete(int id) throws SQLException{
	    removerTodosMembros(id);

	    String sql = "DELETE FROM equipe WHERE id_equipe = ?";

	    try(Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){

	        stmt.setInt(1, id);
	        stmt.executeUpdate();
	    }
	}

	public boolean adicionarMembro(int idEquipe, int idUsuario) throws SQLException {
	    String sql = "INSERT INTO equipe_usuario (id_equipe, id_usuario, data_entrada) VALUES (?, ?, ?)";
	    
	    try (Connection conn = DatabaseConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        
	        stmt.setInt(1, idEquipe);
	        stmt.setInt(2, idUsuario);
	        stmt.setDate(3, new java.sql.Date(System.currentTimeMillis()));
	        
	        int rowsAffected = stmt.executeUpdate();
	        return rowsAffected > 0;
	    }
	}

	public boolean removerMembro(int idEquipe, int idUsuario) throws SQLException {
	    String sql = "DELETE FROM equipe_usuario WHERE id_equipe = ? AND id_usuario = ?";
	    
	    try (Connection conn = DatabaseConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        
	        stmt.setInt(1, idEquipe);
	        stmt.setInt(2, idUsuario);
	        
	        int rowsAffected = stmt.executeUpdate();
	        return rowsAffected > 0;
	    }
	}

	public void removerTodosMembros(int idEquipe) throws SQLException{
	    String sql = "DELETE FROM equipe_usuario WHERE id_equipe = ?";

	    try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){

	        stmt.setInt(1, idEquipe);
	        stmt.executeUpdate();
	    }
	}

	public List<Usuario> listarMembros(int idEquipe) throws SQLException{
	    List<Usuario> membros = new ArrayList<>();
	    String sql = "SELECT u.* FROM usuario u INNER JOIN equipe_usuario eu ON u.id_usuario = eu.id_usuario WHERE eu.id_equipe = ?";

	    try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){

	        stmt.setInt(1, idEquipe);
	        try(ResultSet rs = stmt.executeQuery()){
	            while (rs.next()){
	                membros.add(usuarioDao.resultSetToUsuario(rs));
	            }
	        }
	    }
	    return membros;
	}

	public boolean isMembro(int idEquipe, int idUsuario) throws SQLException{
	    String sql = "SELECT COUNT (*) FROM equipe_usuario WHERE id_equipe = ? AND id_usuario = ?";

	    try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){

	        stmt.setInt(1, idEquipe);
	        stmt.setInt(2, idUsuario);

	        try (ResultSet rs = stmt.executeQuery()){
	            if (rs.next()){
	                return rs.getInt(1) > 0;
	            }
	        }
	    }
	    return false;
	}

	public int countMembros(int idEquipe) throws SQLException {
	    String sql = "SELECT COUNT (*) FROM equipe_usuario WHERE id_equipe = ?";

	    try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){
	        
	        stmt.setInt(1, idEquipe);

	        try (ResultSet rs = stmt.executeQuery()){
	            if (rs.next()) {
	                return rs.getInt(1);
	            }
	        }
	    }
	    return 0;
	}

	public List<Equipe> findEquipesSemMembros() throws SQLException {
	    List<Equipe> equipes = new ArrayList<>();
	    String sql = "SELECT e.* FROM equipe e LEFT JOIN equipe_usuario eu ON e.id_equipe = eu.id_equipe WHERE eu.id_usuario IS NULL";

	    try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)){

	        while (rs.next()){
	            equipes.add(resultSetToEquipe(rs));
	        }
	    }
	    return equipes;
	}

	private Equipe resultSetToEquipe(ResultSet rs) throws SQLException{
	    return new Equipe(rs.getInt("id_equipe"), rs.getString("nome"), rs.getString("descricao"));
	}	
	
	public List<Equipe> findByGerente(int idGerente) throws SQLException {
	    List<Equipe> equipes = new ArrayList<>();
	    String sql = "SELECT e.* FROM equipe e " +
	                "INNER JOIN projeto p ON e.id_equipe = p.id_equipe " +
	                "WHERE p.id_gerente = ?";
	    
	    try (Connection conn = DatabaseConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        
	        stmt.setInt(1, idGerente);
	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                equipes.add(resultSetToEquipe(rs));
	            }
	        }
	    }
	    return equipes;
	}
	
	public int countAll() throws SQLException {
	    String sql = "SELECT COUNT(*) FROM equipe";
	    
	    try (Connection conn = DatabaseConnection.getConnection();
	         Statement stmt = conn.createStatement();
	         ResultSet rs = stmt.executeQuery(sql)) {
	        
	        if (rs.next()) {
	            return rs.getInt(1);
	        }
	    }
	    return 0;
	}
	
	public int countByGerente(int idGerente) throws SQLException {
	    String sql = "SELECT COUNT(DISTINCT e.id_equipe) FROM equipe e " +
	                "INNER JOIN projeto p ON e.id_equipe = p.id_equipe " +
	                "WHERE p.id_gerente = ?";
	    
	    try (Connection conn = DatabaseConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        
	        stmt.setInt(1, idGerente);
	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                return rs.getInt(1);
	            }
	        }
	    }
	    return 0;
	}
	
	
}
