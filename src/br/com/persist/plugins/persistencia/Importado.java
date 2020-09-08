package br.com.persist.plugins.persistencia;

public class Importado {
	private final String tabelaOrigem;
	private final String campoOrigem;
	private final String campo;

	public Importado(String tabelaOrigem, String campoOrigem, String campo) {
		this.tabelaOrigem = tabelaOrigem;
		this.campoOrigem = campoOrigem;
		this.campo = campo;
	}

	public String getTabelaOrigem() {
		return tabelaOrigem;
	}

	public String getCampoOrigem() {
		return campoOrigem;
	}

	public String getCampo() {
		return campo;
	}
}