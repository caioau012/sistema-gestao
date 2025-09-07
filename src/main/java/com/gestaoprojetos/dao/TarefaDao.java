package com.gestaoprojetos.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.gestaoprojetos.model.Projeto;
import com.gestaoprojetos.model.Tarefa;
import com.gestaoprojetos.model.Usuario;
import com.gestaoprojetos.util.DatabaseConnection;

public class TarefaDao {
	private final ProjetoDao projetoDao;
	private final UsuarioDao usuarioDao;
	
	public TarefaDao() {
		this.projetoDao = new ProjetoDao();
		this.usuarioDao = new UsuarioDao();
	}
	
	public void create(Tarefa tarefa) throws SQLException {
	    String sql = "INSERT INTO tarefa (titulo, descricao, status, data_inicio_prev, " +
	                "data_fim_prev, id_projeto, id_responsavel) VALUES (?, ?, ?, ?, ?, ?, ?)";
	    
	    try (Connection conn = DatabaseConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
	        
	        stmt.setString(1, tarefa.getTitulo());
	        stmt.setString(2, tarefa.getDescricao());
	        stmt.setString(3, tarefa.getStatus());
	        stmt.setDate(4, Date.valueOf(tarefa.getDataInicioPrev()));
	        stmt.setDate(5, Date.valueOf(tarefa.getDataFimPrev()));
	        stmt.setInt(6, tarefa.getProjeto().getId());
	        stmt.setInt(7, tarefa.getResponsavel().getId());
	        
	        stmt.executeUpdate();
	        
	        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	                tarefa.setId(generatedKeys.getInt(1));
	            }
	        }
	    }
	}
	
	public Tarefa read(int id) throws SQLException{
		String sql = "SELECT * FROM tarefa WHERE id_tarefa = ?";
		Tarefa tarefa = null;
		
		try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement((sql))){
			
			stmt.setInt(1,  id);
			try (ResultSet rs = stmt.executeQuery()){
				if (rs.next()) {
					tarefa = resultSetToTarefa(rs);
				}
			}
		}
		return tarefa;
	}
	
	public List<Tarefa> findAll() throws SQLException{
		List<Tarefa> tarefas = new ArrayList<>();
		String sql = "SELECT * FROM tarefa";
		
		try(Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)){
			while (rs.next()) {
				tarefas.add(resultSetToTarefa(rs));
			}
		}
		return tarefas;
	}
	
	public void update(Tarefa tarefa) throws SQLException {
		String sql = "UPDATE tarefa SET titulo = ?, descricao = ?, status = ?, data_inicio_prev = ?, data_fim_prev = ?, data_inicio_real = ?, data_fim_real = ?, id_projeto = ?, id_responsavel = ?, WHERE id_tarefa = ?";
		
		try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){
			
			stmt.setString(1, tarefa.getTitulo());
			stmt.setString(2, tarefa.getDescricao());
			stmt.setString(3,  tarefa.getStatus());
			stmt.setDate(4,  Date.valueOf(tarefa.getDataInicioPrev()));
			stmt.setDate(5,  Date.valueOf(tarefa.getDataFimPrev()));
			stmt.setDate(6,  tarefa.getDataInicioReal() != null ? Date.valueOf(tarefa.getDataInicioReal()): null);
			stmt.setDate(7,  tarefa.getDataFimReal() != null ? Date.valueOf(tarefa.getDataFimReal()) : null);
			stmt.setInt(8, tarefa.getProjeto().getId());
			stmt.setInt(9, tarefa.getResponsavel().getId());
			stmt.setInt(10, tarefa.getId());
			
			stmt.executeUpdate();
		}
	}
	
	public void delete(int id) throws SQLException{
		String sql = "DELETE FROM tarefa WHERE id_tarefa = ?";
		
		try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){
			
			stmt.setInt(1, id);
			stmt.executeUpdate();
		}
	}
	
	public List<Tarefa> findByProjeto(int idProjeto) throws SQLException{
		List<Tarefa> tarefas = new ArrayList<>();
		String sql = "SELECT * FROM tarefa WHERE id_projeto = ?";
		
		try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){
			
			stmt.setInt(1,  idProjeto);
			try(ResultSet rs = stmt.executeQuery()){
				while (rs.next()) {
					tarefas.add(resultSetToTarefa(rs));
				}
			}
		}
		return tarefas;
	}
	
	public List<Tarefa> findByResponsavel(int idResponsavel) throws SQLException {
		List<Tarefa> tarefas = new ArrayList<>();
		String sql = "SELECT * FROM tarefa WHERE id_responsavel = ?";
		
		try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){
			
			stmt.setInt(1, idResponsavel);
			try(ResultSet rs = stmt.executeQuery()){
				while(rs.next()) {
					tarefas.add(resultSetToTarefa(rs));
				}
			}
		}
		return tarefas;
	}
	
	public List<Tarefa> findByStatus(String status) throws SQLException {
		List<Tarefa> tarefas = new ArrayList<>();
		String sql = "SELECT * FROM tarefa WHERE status = ?";
		
		try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){
			
			stmt.setString(1,  status);
			try(ResultSet rs = stmt.executeQuery()){
				while(rs.next()) {
					tarefas.add(resultSetToTarefa(rs));
				}
			}
		}
		return tarefas;
	}
	
	
	public List<Tarefa> findTarefasAtrasadas() throws SQLException{
		List<Tarefa> tarefas = new ArrayList<>();
		String sql = "SELECT * FROM tarefa WHERE status != 'concluida' AND data_fim_prev < CURDATE()";
		
		try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)){
			while (rs.next()) {
				tarefas.add(resultSetToTarefa(rs));
			}
		}
		return tarefas;
	}
	
	private Tarefa resultSetToTarefa(ResultSet rs) throws SQLException{
		Projeto projeto = projetoDao.read(rs.getInt("id_projeto"));
		Usuario responsavel = usuarioDao.read(rs.getInt("id_responsavel"));
		
		return new Tarefa(rs.getInt("id_tarefa"), 
				rs.getString("titulo"), 
				rs.getString("descricao"),
				rs.getString("status"),
				rs.getDate("data_inicio_prev").toLocalDate(),
				rs.getDate("data_fim_prev").toLocalDate(),
				rs.getDate("data_inicio_real") != null ? rs.getDate("data_inicio_real").toLocalDate(): null,
				rs.getDate("data_fim_real") != null ? rs.getDate("data_fim_real").toLocalDate() : null, 
				projeto, 
				responsavel);
	}
}
