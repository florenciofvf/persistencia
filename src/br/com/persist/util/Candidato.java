package br.com.persist.util;

public class Candidato {
	private final String numero;
	private int votos;

	public Candidato(String numero) {
		this.numero = numero;
	}

	public int getVotos() {
		return votos;
	}

	public void somarVoto() {
		votos++;
	}

	public String getNumero() {
		return numero;
	}
}