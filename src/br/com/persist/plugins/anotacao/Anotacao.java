package br.com.persist.plugins.anotacao;

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

public class Anotacao {
	private static final Logger LOG = Logger.getGlobal();
	private final List<Anotacao> filhos;
	private boolean processado;
	private Anotacao pai;
	private File file;

	public Anotacao(File file) {
		this.file = Objects.requireNonNull(file);
		filhos = new ArrayList<>();
	}

	public void inflar(StringBuilder sb) {
		getFilhos();
		for (Anotacao a : filhos) {
			a.inflar(sb);
		}
	}

	public void excluir() {
		for (Anotacao a : filhos) {
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

	public List<Anotacao> getFilhos() {
		if (!processado) {
			processar();
		}
		return filhos;
	}

	public void excluir(Anotacao anotacao) {
		getFilhos().remove(anotacao);
	}

	public int getIndice(Anotacao anotacoa) {
		return getFilhos().indexOf(anotacoa);
	}

	public int getTotal() {
		return getFilhos().size();
	}

	public boolean estaVazio() {
		return getFilhos().isEmpty();
	}

	public Anotacao getAnotacao(int index) {
		return filhos.get(index);
	}

	public Anotacao getAnotacao(String descricao, boolean porParte) {
		for (Anotacao m : filhos) {
			String nome = m.file.getName();
			if (porParte && nome.toUpperCase().indexOf(descricao.toUpperCase()) != -1) {
				return m;
			}
			if (nome.equalsIgnoreCase(descricao)) {
				return m;
			}
		}
		for (Anotacao m : filhos) {
			Anotacao resp = m.getAnotacao(descricao, porParte);
			if (resp != null) {
				return resp;
			}
		}
		return null;
	}

	public void preencher(List<Anotacao> lista, String descricao, boolean porParte) {
		String nome = file.getName();
		if ((porParte && nome.toUpperCase().indexOf(descricao.toUpperCase()) != -1)
				|| nome.equalsIgnoreCase(descricao)) {
			lista.add(this);
		}
		for (Anotacao a : filhos) {
			a.preencher(lista, descricao, porParte);
		}
	}

	private void processar() {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null) {
				Arrays.sort(files, (f1, f2) -> f1.getName().compareTo(f2.getName()));
				for (File f : files) {
					if (!AnotacaoModelo.ignorar(f.getName())) {
						Anotacao arq = new Anotacao(f);
						filhos.add(arq);
						arq.pai = this;
					}
				}
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

	public Anotacao getPai() {
		return pai;
	}

	@Override
	public String toString() {
		return file.getName();
	}
}