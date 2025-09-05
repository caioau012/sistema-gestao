package com.gestaoprojetos.controller;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import com.gestaoprojetos.dao.ProjetoDao;
import com.gestaoprojetos.dao.UsuarioDao;
import com.gestaoprojetos.model.Projeto;
import com.gestaoprojetos.model.Usuario;

public class ProjetoController {
	private final ProjetoDao projetoDao;
	private final UsuarioDao usuarioDao;
	
	public ProjetoController() {
		this.projetoDao = new ProjetoDao();
		this.usuarioDao = new UsuarioDao();
	}
	
	public boolean criarProjeto(String nome, String descricao, LocalDate dataInicio, LocalDate dataTerminoPrev, String status, int idGerente) {
		try {
			if (!validarDadosProjeto(nome, dataInicio, dataTerminoPrev, idGerente)) {
				return false;
			}
			
			Usuario gerente = usuarioDao.read(idGerente);
			if(gerente == null) {
				System.err.println("Gerente n'ao encontrado!");
				return false;
			}
			
			Projeto projeto = new Projeto(0, nome, descricao, dataInicio, dataTerminoPrev, status, gerente);
			projetoDao.create(projeto);
			return true;
		}
		catch (SQLException e) {
			System.err.println("Erro ao criar projeto: " + e.getMessage());
			return false;
		}
	}
	
	public Projeto buscarProjetoPorId(int id) {
		try {
			return projetoDao.read(id);
		}
		catch (SQLException e) {
			System.err.println("Erro ao buscar projeto: " + e.getMessage());
			return null;
		}
	}
	
	public List<Projeto> listarTodosProjetos(){
		try {
			return projetoDao.findAll();
		}
		catch (SQLException e) {
			System.err.println("Erro ao listar projetos: " + e.getMessage());
			return List.of();
		}
	}
	
	public List<Projeto> listarProjetosPorStatus(String status){
		try {
			return projetoDao.findByStatus(status);
		}
		catch (SQLException e) {
			System.err.println("Erro ao listar projetos por status: " + e.getMessage());
			return List.of();
		}
	}
	
	public List<Projeto> listarProjetosPorGerente(int idGerente){
		try {
			return projetoDao.findByGerente(idGerente);
		}
		catch (SQLException e) {
			System.err.println("Erro ao listar projetos por gerente: " + e.getMessage());
			return List.of();
		}
	}
	
	public boolean atualizarProjeto(int id, String nome, String descricao, LocalDate dataInicio, LocalDate dataTerminoPrev, String status, int idGerente) {
		try {
			Usuario gerente = usuarioDao.read(idGerente);
			if (gerente == null) {
				System.err.println("Gerente não encontrado!");
				return false;
			}
			
			Projeto projeto = new Projeto(id, nome, descricao, dataInicio, dataTerminoPrev, status, gerente);
			projetoDao.update(projeto);
			return true;
		}
		catch (SQLException e) {
			System.err.println("Erro ao atualizar projeto: " + e.getMessage());
			return false;
		}
	}
	
	public boolean atualizarStatusProjeto(int id, String novoStatus) {
		try {
			Projeto projeto = projetoDao.read(id);
			if (projeto != null) {
				projeto.setStatus(novoStatus);
				projetoDao.update(projeto);
				return true;
			}
			return false;
		}
		catch (SQLException e) {
			System.err.println("Erro ao atualizar status: " + e.getMessage());
			return false;
		}
	}
	
	public boolean excluirProjeto(int id) {
		try {
			projetoDao.delete(id);
			return true;
		}
		catch (SQLException e) {
			System.err.println("Erro ao excluir projeto: " + e.getMessage());
			return false;
		}
	}
	
	private boolean validarDadosProjeto(String nome, LocalDate dataInicio, LocalDate dataTerminoPrev, int idGerente) {
		if (nome == null || nome.trim().isEmpty()) {
			System.err.println("Nome do projeto é obrigatório");
			return false;
		}
		
		if (dataInicio == null) {
			System.err.println("Data de início é obrigatória");
			return false;
		}
		
		if (dataTerminoPrev == null || dataTerminoPrev.isBefore(dataInicio)) {
			System.err.println("Data de término prevista deve ser após a data de início");
			return false;
		}
		
		if (idGerente <= 0) {
			System.err.println("ID do gerente inválido");
			return false;
		}
		
		return true;
	}
	
	public List<Projeto> listarProjetosAtrasados(){
		try {
			return projetoDao.findProjetosAtrasados();
		}
		catch (SQLException e) {
			System.err.println("Erro ao buscar projetos atrasados: " + e.getMessage());
			return List.of();
		}
	}
	
	public List<Projeto> listarProjetosPorPeriodo(LocalDate inicio, LocalDate fim){
		try {
			return projetoDao.findByPeriodo(inicio, fim);
		}
		catch (SQLException e) {
			System.err.println("Erro ao buscar projetos por período: " + e.getMessage());
			return List.of();
		}
	}
		
}
