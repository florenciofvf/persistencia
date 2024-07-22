package br.com.persist.assistencia;

import java.io.PrintWriter;

import br.com.persist.plugins.instrucao.biblio_nativo.Lista;

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

	public void salvar(PrintWriter pw) {
		long size = lista.size().longValue();
		for (long i = 0; i < size; i++) {
			LinhaString linhaString = (LinhaString) lista.get(i);
			linhaString.print(pw);
		}
	}

	@Override
	public String toString() {
		return absoluto + "\n" + lista;
	}
}