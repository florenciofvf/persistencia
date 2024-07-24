package br.com.persist.plugins.instrucao.biblionativo;

import java.io.PrintWriter;

public class Arquivo {
	private final java.lang.String absoluto;
	private final Lista lista;

	public Arquivo(java.lang.String absoluto, Lista lista) {
		this.absoluto = absoluto;
		this.lista = lista;
	}

	public java.lang.String getAbsoluto() {
		return absoluto;
	}

	public Lista getLista() {
		return lista;
	}

	public void salvar(PrintWriter pw) {
		long size = lista.size().longValue();
		for (long i = 0; i < size; i++) {
			ArquivoLinha linha = (ArquivoLinha) lista.get(i);
			linha.print(pw);
		}
	}

	@Override
	public java.lang.String toString() {
		return absoluto + "\n" + lista;
	}
}