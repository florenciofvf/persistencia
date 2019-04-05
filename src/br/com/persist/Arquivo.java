package br.com.persist;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.io.File;

public class Arquivo {
	private final List<Arquivo> arquivos;
	private boolean arquivoAberto;
	private boolean processado;
	private final File file;
	private Arquivo pai;

	public Arquivo(File file) {
		Objects.requireNonNull(file);
		arquivos = new ArrayList<>();
		this.file = file;
	}

	public void inflar() {
		getArquivos();

		for (Arquivo a : arquivos) {
			a.inflar();
		}
	}

	public void listar(List<Arquivo> lista) {
		if (isFile()) {
			lista.add(this);
		}

		for (Arquivo a : arquivos) {
			a.listar(lista);
		}
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

			for (File f : files) {
				Arquivo arq = new Arquivo(f);
				arquivos.add(arq);
				arq.pai = this;
			}
		}

		processado = true;
	}

	public boolean isFile() {
		return file.isFile();
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

	public boolean isArquivoAberto() {
		return arquivoAberto;
	}

	public void setArquivoAberto(boolean arquivoAberto) {
		this.arquivoAberto = arquivoAberto;
	}
}