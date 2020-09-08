package br.com.persist.plugins.persistencia;

public class Exportado {
	private final String tabelaDestino;
	private final String campoDestino;
	private final String campo;

	public Exportado(String tabelaDestino, String campoDestino, String campo) {
		this.tabelaDestino = tabelaDestino;
		this.campoDestino = campoDestino;
		this.campo = campo;
	}

	public String getTabelaDestino() {
		return tabelaDestino;
	}

	public String getCampoDestino() {
		return campoDestino;
	}

	public String getCampo() {
		return campo;
	}
}