package br.com.persist.arquivo;

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
	private final List<String> ignorados;
	private final List<Arquivo> filhos;
	private boolean arquivoAberto;
	private boolean processado;
	private Arquivo pai;
	private File file;

	public Arquivo(File file, List<String> ignorados) {
		this.ignorados = Objects.requireNonNull(ignorados);
		this.file = Objects.requireNonNull(file);
		filhos = new ArrayList<>();
	}

	public List<String> getIgnorados() {
		return ignorados;
	}

	public void reiniciar() {
		processado = true;
		filhos.clear();
		inflar();
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

	private void processar() {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null) {
				Arrays.sort(files, (f1, f2) -> f1.getName().compareTo(f2.getName()));
				for (File f : files) {
					if (!ignorar(f.getName())) {
						Arquivo arq = new Arquivo(f, ignorados);
						filhos.add(arq);
						arq.pai = this;
					}
				}
			}
		}
		processado = true;
	}

	private boolean ignorar(String string) {
		if (string != null) {
			for (String s : ignorados) {
				if (string.endsWith(s)) {
					return true;
				}
			}
		}
		return false;
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

	public Arquivo getArquivo(String descricao, boolean porParte) {
		for (Arquivo m : filhos) {
			String nome = m.file.getName();
			if (porParte && nome.toUpperCase().indexOf(descricao.toUpperCase()) != -1) {
				return m;
			}
			if (nome.equalsIgnoreCase(descricao)) {
				return m;
			}
		}
		for (Arquivo m : filhos) {
			Arquivo resp = m.getArquivo(descricao, porParte);
			if (resp != null) {
				return resp;
			}
		}
		return null;
	}

	public void preencher(List<Arquivo> lista, String descricao, boolean porParte) {
		String nome = file.getName();
		if ((porParte && nome.toUpperCase().indexOf(descricao.toUpperCase()) != -1)
				|| nome.equalsIgnoreCase(descricao)) {
			lista.add(this);
		}
		for (Arquivo a : filhos) {
			a.preencher(lista, descricao, porParte);
		}
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

	public String getName() {
		return file.getName();
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