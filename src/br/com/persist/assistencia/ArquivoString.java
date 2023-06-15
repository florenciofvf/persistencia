package br.com.persist.assistencia;

public class ArquivoString {
	private final String absoluto;
	private final Lista lista;

	public ArquivoString(String absoluto, Lista lista) {
		this.absoluto = absoluto;
		this.lista = lista;
	}

	public String getAbsoluto() {
		return absoluto;
	}

	public Lista getLista() {
		return lista;
	}

	@Override
	public String toString() {
		return absoluto + "\n" + lista;
	}
}