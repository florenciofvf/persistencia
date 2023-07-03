package br.com.persist.plugins.quebra_log;

import java.awt.Component;
import java.awt.Desktop;
import java.io.File;
import java.util.Objects;

import br.com.persist.assistencia.Util;

public class QuebraLog {
	private final String absolutePath;
	private final long tamanhoBloco;
	private final String nome;
	private final File origem;
	private final int indice;
	private final File file;
	private String tamanho;
	private int row;

	public QuebraLog(File origem, File file, int indice, long tamanhoBloco) {
		this.origem = Objects.requireNonNull(origem);
		this.file = Objects.requireNonNull(file);
		absolutePath = file.getAbsolutePath();
		this.tamanhoBloco = tamanhoBloco;
		nome = file.getName();
		this.indice = indice;
	}

	public String getAbsolutePath() {
		return absolutePath;
	}

	public String getTamanho() {
		return tamanho;
	}

	public File getFile() {
		return file;
	}

	public long getTamanhoBloco() {
		return tamanhoBloco;
	}

	public int getIndice() {
		return indice;
	}

	public String getNome() {
		return nome;
	}

	public File getOrigem() {
		return origem;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public void abrir(Component c) {
		try {
			Desktop.getDesktop().open(file);
		} catch (Exception ex) {
			Util.stackTraceAndMessage(QuebraLogConstantes.PAINEL_QUEBRA_LOG, ex, c);
		}
	}

	public void atualizarTamanho() {
		tamanho = atualizarTamanho(file);
	}

	public static String atualizarTamanho(File file) {
		long length = file.length();
		if (length < 1024) {
			return length + " Bytes";
		} else if (length < 1024 * 1024) {
			return (length / 1024) + " Kilobytes";
		} else if (length < 1024 * 1024 * 1024) {
			return (length / 1024 / 1024) + " Megabytes";
		} else {
			return (length / 1024 / 1024 / 1024) + " Gigabytes";
		}
	}
}