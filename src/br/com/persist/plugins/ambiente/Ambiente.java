package br.com.persist.plugins.ambiente;

public class Ambiente {
	final String tituloMin;
	final String titulo;
	final String chave;

	public Ambiente(String chave, String titulo, String tituloMin) {
		this.tituloMin = tituloMin;
		this.titulo = titulo;
		this.chave = chave;
	}
}