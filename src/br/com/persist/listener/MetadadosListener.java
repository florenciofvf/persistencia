package br.com.persist.listener;

import br.com.persist.metadado.Metadados;

public interface MetadadosListener {
	public void abrirExportacaoFormArquivo(Metadados metadados, boolean circular);

	public void abrirExportacaoFichArquivo(Metadados metadados, boolean circular);

	public void abrirImportacaoFormArquivo(Metadados metadados, boolean circular);

	public void abrirImportacaoFichArquivo(Metadados metadados, boolean circular);
}