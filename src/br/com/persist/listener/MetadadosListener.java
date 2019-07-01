package br.com.persist.listener;

import br.com.persist.metadado.Metadados;

public interface MetadadosListener {
	public void abrirExportacaoFormArquivo(Metadados metadados);

	public void abrirExportacaoFichArquivo(Metadados metadados);

	public void abrirImportacaoFormArquivo(Metadados metadados);

	public void abrirImportacaoFichArquivo(Metadados metadados);
}