package br.com.persist.plugins.arquivo;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Arquivo {
	private static final Logger LOG = Logger.getGlobal();
	private final List<Arquivo> filhos;
	private boolean arquivoAberto;
	private boolean processado;
	private Arquivo pai;
	private File file;

	public Arquivo(File file) {
		Objects.requireNonNull(file);
		filhos = new ArrayList<>();
		this.file = file;
	}

	public void inflar() {
		getFilhos();
		for (Arquivo a : filhos) {
			a.inflar();
		}
	}

	public void excluir() {
		for (Arquivo a : filhos) {
			a.excluir();
		}
		if (isFile() || isDirectory()) {
			Path path = FileSystems.getDefault().getPath(file.getAbsolutePath());
			try {
				Files.delete(path);
			} catch (IOException e) {
				LOG.log(Level.FINEST, "EXCLUIR ARQUIVO");
			}
		}
	}

	public boolean renomear(String nome) {
		try {
			File destino = new File(file.getParent(), nome);
			boolean resp = file.renameTo(destino);
			if (resp) {
				file = destino;
			}
			return resp;
		} catch (Exception e) {
			return false;
		}
	}

	public void listar(List<Arquivo> lista) {
		if (isFile()) {
			lista.add(this);
		}
		for (Arquivo a : filhos) {
			a.listar(lista);
		}
	}

	public List<Arquivo> getFilhos() {
		if (!processado) {
			processar();
		}
		return filhos;
	}

	public void excluir(Arquivo arquivo) {
		getFilhos().remove(arquivo);
	}

	public int getIndice(Arquivo arquivo) {
		return getFilhos().indexOf(arquivo);
	}

	public int getTotal() {
		return getFilhos().size();
	}

	public boolean estaVazio() {
		return getFilhos().isEmpty();
	}

	public Arquivo getArquivo(int index) {
		return filhos.get(index);
	}

	private void processar() {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null) {
				Arrays.sort(files, (f1, f2) -> f1.getName().compareTo(f2.getName()));
			}
			for (File f : files) {
				Arquivo arq = new Arquivo(f);
				filhos.add(arq);
				arq.pai = this;
			}
		}
		processado = true;
	}

	public boolean isFile() {
		return file.isFile();
	}

	public boolean isDirectory() {
		return file.isDirectory();
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