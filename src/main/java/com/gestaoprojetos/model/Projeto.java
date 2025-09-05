package com.gestaoprojetos.model;

import java.time.LocalDate;
import java.util.Objects;

public class Projeto {
	private int id;
	private String nome;
	private String descricao;
	private LocalDate dataInicio;
	private LocalDate dataTerminoPrev;
	private String status;
	private Usuario gerente;
	
	public Projeto() {
	}

	public Projeto(int id, String nome, String descricao, LocalDate dataInicio, LocalDate dataTerminoPrev, String status, Usuario gerente) {
		this.id = id;
		this.nome = nome;
		this.descricao = descricao;
		this.dataInicio = dataInicio;
		this.dataTerminoPrev = dataTerminoPrev;
		this.status = status;
		this.gerente = gerente;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public LocalDate getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(LocalDate dataInicio) {
		this.dataInicio = dataInicio;
	}

	public LocalDate getDataTerminoPrev() {
		return dataTerminoPrev;
	}

	public void setDtaTerminoPrev(LocalDate dataTerminoPrev) {
		this.dataTerminoPrev = dataTerminoPrev;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Usuario getGerente() {
		return gerente;
	}

	public void setGerente(Usuario gerente) {
		this.gerente = gerente;
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
		Projeto other = (Projeto) obj;
		return id == other.id;
	}
	
	@Override
	public String toString() {
		return nome + " (" + gerente + ")";
	}
	
	
}
