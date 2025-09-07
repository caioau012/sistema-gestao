package com.gestaoprojetos.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Equipe {
	private int id;
	private String nome;
	private String descricao;
	private List<Usuario> membros;
	
	public Equipe(int id, String nome, String descricao) {
		this.id = id;
		this.nome = nome;
		this.descricao = descricao;
		this.membros = new ArrayList<>();
	}
	
	public void adicionarMembro(Usuario usuario) {
		if (!membros.contains(usuario)) {
			membros.add(usuario);
		}
	}
	
	public void removerMembro(Usuario usuario) {
		membros.remove(usuario);
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

	public List<Usuario> getMembros() {
		return membros;
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
		Equipe other = (Equipe) obj;
		return id == other.id;
	}	
}
