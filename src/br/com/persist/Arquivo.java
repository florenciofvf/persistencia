package br.com.persist;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class Arquivo {
	private static final Logger LOG = Logger.getGlobal();
	private final List<Arquivo> arquivos;
	private boolean arquivoAberto;
	private boolean processado;
	private String nomeIcone;
	private Arquivo pai;
	private Icon icone;
	private File file;

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

	public void excluir() {
		for (Arquivo a : arquivos) {
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

	public Icon getIcone() {
		return icone;
	}

	public void setIcone(Icon icone, String nomeIcone) {
		this.nomeIcone = nomeIcone;
		this.icone = icone;
	}

	public String getNomeIcone() {
		return nomeIcone;
	}
}