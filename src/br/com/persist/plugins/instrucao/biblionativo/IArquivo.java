package br.com.persist.plugins.instrucao.biblionativo;

import java.io.PrintWriter;

public class IArquivo {
	private final String absoluto;
	private final Lista lista;

	public IArquivo(String absoluto, Lista lista) {
		this.absoluto = absoluto;
		this.lista = lista;
	}

	public String getAbsoluto() {
		return absoluto;
	}

	public Lista getLista() {
		return lista;
	}

	public void salvar(PrintWriter pw) throws IllegalAccessException {
		long size = lista.size().longValue();
		for (long i = 0; i < size; i++) {
			ILinha linha = (ILinha) lista.get(i);
			linha.print(pw);
		}
	}

	@Override
	public String toString() {
		return absoluto + "\n" + lista;
	}
}