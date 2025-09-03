package com.gestaoprojetos.model;

import java.util.ArrayList;
import java.util.List;

public class Equipe {
	private int id;
	private String nome;
	private String descricao;
	private List<Usuario> membros;
	
	public Equipe() {
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
}
