package br.com.persist.plugins.expressao.biblionativo;

import java.io.File;
import java.io.PrintWriter;

public class Arquivo {
	private final File file;
	private final Lista lista;

	public Arquivo(File file, Lista lista) {
		this.lista = lista;
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	public Lista getLista() {
		return lista;
	}

	public void salvar(PrintWriter pw) throws IllegalAccessException {
		long size = lista.size().longValue();
		for (long i = 0; i < size; i++) {
			Linha linha = (Linha) lista.get(i);
			linha.print(pw);
		}
	}

	@Override
	public String toString() {
		return file + "\n" + lista;
	}
}