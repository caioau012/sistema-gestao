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
import com.gestaoprojetos.model.Usuario;
import com.gestaoprojetos.util.DatabaseConnection;

public class ProjetoDao{
	private final UsuarioDao usuarioDao;
	
	public ProjetoDao() {
		this.usuarioDao = new UsuarioDao();
	}
	
    public void create(Projeto projeto) throws SQLException {
        String sql = "INSERT INTO projeto (nome, descricao, data_inicio, data_termino_prev, status, id_gerente) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            stmt.setString(1, projeto.getNome());
            stmt.setString(2, projeto.getDescricao());
            stmt.setDate(3, Date.valueOf(projeto.getDataInicio()));
            stmt.setDate(4, Date.valueOf(projeto.getDataTerminoPrev()));
            stmt.setString(5, projeto.getStatus());
            stmt.setInt(6, projeto.getGerente().getId());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()){
                if (generatedKeys.next()){
                    projeto.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public Projeto read(int id) throws SQLException {
        String sql = "SELECT * FROM projeto WHERE id_projeto = ?";
        Projeto projeto = null;

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()){
                    projeto = resultSetToProjeto(rs);
                }
            }
        }
        return projeto;
    }

    public List<Projeto> findAll() throws SQLException{
        List<Projeto> projetos = new ArrayList<>();
        String sql = "SELECT * FROM projeto";

        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)){
            while(rs.next()){
                projetos.add(resultSetToProjeto(rs));
            }
        }
        return projetos;
    }

    public void update(Projeto projeto) throws SQLException{
        String sql = "UPDATE projeto SET nome = ?, descricao = ?, dataInicio = ?, dataTerminoPrev = ?, status = ?, id_gerente = ? WHERE id_projeto = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, projeto.getNome());
            stmt.setString(2, projeto.getDescricao());
            stmt.setDate(3, Date.valueOf(projeto.getDataInicio()));
            stmt.setDate(4, Date.valueOf(projeto.getDataTerminoPrev()));
            stmt.setString(5, projeto.getStatus());
            stmt.setInt(6, projeto.getGerente().getId());
            stmt.setInt(7, projeto.getId());
        }
    }

    public void delete(int id) throws SQLException{
        String sql = "DELETE FROM projeto WHERE id_projeto = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public Projeto resultSetToProjeto(ResultSet rs) throws SQLException {
        int idGerente = rs.getInt("id_gerente");
        Usuario gerente = usuarioDao.read(idGerente);
        
        return new Projeto(
            rs.getInt("id_projeto"),
            rs.getString("nome"),
            rs.getString("descricao"),
            rs.getDate("data_inicio").toLocalDate(), 
            rs.getDate("data_termino_prev").toLocalDate(), 
            rs.getString("status"),
            gerente
        );
    }

}