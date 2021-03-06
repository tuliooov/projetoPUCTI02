package com.ti2cc;

public class Avaliacoes {
	

	private String cpf;
	private int likes;
	private int deslikes;
	private int candidaturaId;
	private int comentarios;
	
	public Avaliacoes(String cpf, int likes, int deslikes, int comentarios, int candidaturaId) {
		this.cpf = cpf;
		this.likes = likes;
		this.deslikes = deslikes;
		this.comentarios = comentarios;
		this.candidaturaId = candidaturaId;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	public int getDeslikes() {
		return deslikes;
	}

	public void setDeslikes(int deslikes) {
		this.deslikes = deslikes;
	}

	public int getCandidaturaId() {
		return candidaturaId;
	}

	public void setCandidaturaId(int candidaturaId) {
		this.candidaturaId = candidaturaId;
	}

	public int getComentarios() {
		return comentarios;
	}

	public void setComentarios(int comentarios) {
		this.comentarios = comentarios;
	}

	@Override
	public String toString() {
		return "Avaliacoes [cpf=" + cpf + ", likes=" + likes + ", deslikes=" + deslikes + ", candidaturaId="
				+ candidaturaId + ", comentarios=" + comentarios + "]";
	}

	
}
