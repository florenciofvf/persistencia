package br.com.persist.arquivo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;

import br.com.persist.anexo.AnexoTreeModelo;
import br.com.persist.anexo.AnexoTree;
import br.com.persist.anexo.AnexoTreeUtil;
import br.com.persist.util.Constantes;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class Arquivo {
	private static final Logger LOG = Logger.getGlobal();
	private final List<Arquivo> filhos;
	private boolean arquivoAberto;
	private boolean abrirVisivel;
	private boolean padraoAbrir;
	private boolean processado;
	private String nomeIcone;
	private boolean checado;
	private Color corFonte;
	private Arquivo pai;
	private Icon icone;
	private File file;

	public Arquivo(File file) {
		Objects.requireNonNull(file);
		filhos = new ArrayList<>();
		this.file = file;
	}

	public void inflar(boolean anexos, StringBuilder sb) {
		if (anexos) {
			config(sb);
		}

		getFilhos();

		for (Arquivo a : filhos) {
			a.inflar(anexos, sb);
		}
	}

	public void abrirVisivel(AnexoTree anexoTree) {
		if (abrirVisivel) {
			AnexoTreeUtil.selecionarObjeto(anexoTree, this);
		}

		for (Arquivo a : filhos) {
			a.abrirVisivel(anexoTree);
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

	public Icon getIcone() {
		return icone;
	}

	public void setIcone(Icon icone, String nomeIcone) {
		if (icone != null && nomeIcone != null) {
			this.nomeIcone = nomeIcone;
			this.icone = icone;
		}
	}

	public void limparIcone() {
		this.nomeIcone = null;
		this.icone = null;
	}

	public String getNomeIcone() {
		return nomeIcone;
	}

	public boolean isPadraoAbrir() {
		return padraoAbrir;
	}

	public void setPadraoAbrir(boolean padraoAbrir) {
		this.padraoAbrir = padraoAbrir;
	}

	public boolean isAbrirVisivel() {
		return abrirVisivel;
	}

	public void setAbrirVisivel(boolean abrirVisivel) {
		this.abrirVisivel = abrirVisivel;
	}

	private void config(StringBuilder sb) {
		Arquivo arq = AnexoTreeModelo.getArquivos().get(criarChave(sb).toString());

		if (arq != null) {
			setIcone(arq.getIcone(), arq.getNomeIcone());
			setAbrirVisivel(arq.isAbrirVisivel());
			setPadraoAbrir(arq.isPadraoAbrir());
			setCorFonte(arq.getCorFonte());
			arq.setChecado(true);
		}
	}

	public StringBuilder criarChave(StringBuilder sb) {
		sb.delete(0, sb.length());
		sb.append(Constantes.SEP);
		Arquivo arq = this;

		while (arq != null) {
			sb.append(arq.file.getName() + Constantes.SEP);
			arq = arq.getPai();
		}

		return sb;
	}

	public boolean isChecado() {
		return checado;
	}

	public void setChecado(boolean checado) {
		this.checado = checado;
	}

	public Color getCorFonte() {
		return corFonte;
	}

	public void setCorFonte(Color corFonte) {
		this.corFonte = corFonte;
	}
}