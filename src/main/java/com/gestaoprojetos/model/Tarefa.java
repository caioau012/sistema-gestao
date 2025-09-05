package com.gestaoprojetos.model;

import java.time.LocalDate;
import java.util.Objects;

public class Tarefa {
	private int id;
	private String titulo;
	private String descricao;
	private String status;
	private LocalDate dataInicioPrev;
	private LocalDate dataFimPrev;
	private LocalDate dataInicioReal;
	private LocalDate dataFimReal;
	private Projeto projeto;
	private Usuario responsavel;
	
	public Tarefa() {
	}

	public Tarefa(int id, String titulo, String descricao, String status, LocalDate dataInicioPrev, LocalDate dataFimPrev,	LocalDate dataInicioReal, LocalDate dataFimReal, Projeto projeto, Usuario responsavel) {
		this.id = id;
		this.titulo = titulo;
		this.descricao = descricao;
		this.status = status;
		this.dataInicioPrev = dataInicioPrev;
		this.dataFimPrev = dataFimPrev;
		this.dataInicioReal = dataInicioReal;
		this.dataFimReal = dataFimReal;
		this.projeto = projeto;
		this.responsavel = responsavel;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDate getDataInicioPrev() {
		return dataInicioPrev;
	}

	public void setDataInicioPrev(LocalDate dataInicioPrev) {
		this.dataInicioPrev = dataInicioPrev;
	}

	public LocalDate getDataFimPrev() {
		return dataFimPrev;
	}

	public void setDataFimPrev(LocalDate dataFimPrev) {
		this.dataFimPrev = dataFimPrev;
	}

	public LocalDate getDataInicioReal() {
		return dataInicioReal;
	}

	public void setDataInicioReal(LocalDate dataInicioReal) {
		this.dataInicioReal = dataInicioReal;
	}

	public LocalDate getDataFimReal() {
		return dataFimReal;
	}

	public void setDataFimReal(LocalDate dataFimReal) {
		this.dataFimReal = dataFimReal;
	}

	public Projeto getProjeto() {
		return projeto;
	}

	public void setProjeto(Projeto projeto) {
		this.projeto = projeto;
	}

	public Usuario getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(Usuario responsavel) {
		this.responsavel = responsavel;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tarefa other = (Tarefa) obj;
		return id == other.id;
	}
	
	@Override
	public String toString() {
		return titulo + " (" + responsavel + ")";
	}
	
}
