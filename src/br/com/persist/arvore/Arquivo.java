package br.com.persist.arvore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.io.File;

public class Arquivo {
	private final List<Arquivo> arquivos;
	private boolean processado;
	private final File file;
	private Arquivo pai;

	public Arquivo(File file) {
		Objects.requireNonNull(file);
		arquivos = new ArrayList<>();
		this.file = file;
	}

	public List<Arquivo> getArquivos() {
		if (!processado) {
			processar();
		}

		return arquivos;
	}

	public void excluir(Arquivo arquivo) {
		getArquivos().remove(arquivo);
	}

	public int getIndice(Arquivo arquivo) {
		return getArquivos().indexOf(arquivo);
	}

	public int getTotal() {
		return getArquivos().size();
	}

	public boolean estaVazio() {
		return getArquivos().isEmpty();
	}

	public Arquivo getArquivo(int index) {
		return arquivos.get(index);
	}

	private void processar() {
		if (file.isDirectory()) {
			File[] files = file.listFiles();

			for (File file : files) {
				Arquivo arq = new Arquivo(file);
				arquivos.add(arq);
				arq.pai = this;
			}
		}

		processado = true;
	}

	public File getFile() {
		return file;
	}

	public Arquivo getPai() {
		return pai;
	}

	@Override
	public String toString() {
		return file.getName();
	}
}