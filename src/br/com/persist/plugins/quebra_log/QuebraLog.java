package br.com.persist.plugins.quebra_log;

import java.awt.Component;
import java.awt.Desktop;
import java.io.File;
import java.util.Objects;

import br.com.persist.assistencia.Util;

public class QuebraLog {
	private final String absolutePath;
	private final String nome;
	private final File file;
	private String tamanho;

	public QuebraLog(File file) {
		this.file = Objects.requireNonNull(file);
		absolutePath = file.getAbsolutePath();
		nome = file.getName();
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

	public String getNome() {
		return nome;
	}

	public void abrir(Component c) {
		try {
			Desktop.getDesktop().open(file);
		} catch (Exception ex) {
			Util.stackTraceAndMessage(QuebraLogConstantes.PAINEL_QUEBRA_LOG, ex, c);
		}
	}

	public void atualizarTamanho() {
		long length = file.length();
		if (length < 1024) {
			tamanho = length + " Bytes";
		} else if (length < 1024 * 1024) {
			tamanho = length / 1024 + " Kilobytes";
		} else if (length < 1024 * 1024 * 1024) {
			tamanho = (length / 1024 / 1024) + " Megabytes";
		} else {
			tamanho = (length / 1024 / 1024 / 1024) + " Gigabytes";
		}
	}
}