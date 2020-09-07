package br.com.persist.plugins.anexo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;

import br.com.persist.util.Constantes;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class Anexo {
	private static final Logger LOG = Logger.getGlobal();
	private final List<Anexo> filhos;
	private boolean abrirVisivel;
	private boolean padraoAbrir;
	private boolean processado;
	private String nomeIcone;
	private boolean checado;
	private Color corFonte;
	private Icon icone;
	private File file;
	private Anexo pai;

	public Anexo(File file) {
		Objects.requireNonNull(file);
		filhos = new ArrayList<>();
		this.file = file;
	}

	public void inflar(StringBuilder sb) {
		config(sb);
		getFilhos();

		for (Anexo a : filhos) {
			a.inflar(sb);
		}
	}

	public void abrirVisivel(AnexoTree anexoTree) {
		if (abrirVisivel) {
			AnexoTreeUtil.selecionarObjeto(anexoTree, this);
		}

		for (Anexo a : filhos) {
			a.abrirVisivel(anexoTree);
		}
	}

	public void excluir() {
		for (Anexo a : filhos) {
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

	public List<Anexo> getFilhos() {
		if (!processado) {
			processar();
		}

		return filhos;
	}

	public void excluir(Anexo anexo) {
		getFilhos().remove(anexo);
	}

	public int getIndice(Anexo anexo) {
		return getFilhos().indexOf(anexo);
	}

	public int getTotal() {
		return getFilhos().size();
	}

	public boolean estaVazio() {
		return getFilhos().isEmpty();
	}

	public Anexo getAnexo(int index) {
		return filhos.get(index);
	}

	private void processar() {
		if (file.isDirectory()) {
			File[] files = file.listFiles();

			if (files != null) {
				Arrays.sort(files, (f1, f2) -> f1.getName().compareTo(f2.getName()));
			}

			for (File f : files) {
				Anexo arq = new Anexo(f);
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

	public Anexo getPai() {
		return pai;
	}

	@Override
	public String toString() {
		return file.getName();
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
		Anexo ane = AnexoModelo.getAnexos().get(criarChave(sb).toString());

		if (ane != null) {
			setIcone(ane.getIcone(), ane.getNomeIcone());
			setAbrirVisivel(ane.isAbrirVisivel());
			setPadraoAbrir(ane.isPadraoAbrir());
			setCorFonte(ane.getCorFonte());
			ane.setChecado(true);
		}
	}

	public StringBuilder criarChave(StringBuilder sb) {
		sb.delete(0, sb.length());
		sb.append(Constantes.SEP);
		Anexo ane = this;

		while (ane != null) {
			sb.append(ane.file.getName() + Constantes.SEP);
			ane = ane.getPai();
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