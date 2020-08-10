package br.com.persist.listener;

import br.com.persist.metadado.MetadadoTree;

public interface MetadadosListener {
	public void abrirExportacaoFormArquivo(MetadadoTree metadados, boolean circular);

	public void abrirExportacaoFichArquivo(MetadadoTree metadados, boolean circular);

	public void abrirImportacaoFormArquivo(MetadadoTree metadados, boolean circular);

	public void abrirImportacaoFichArquivo(MetadadoTree metadados, boolean circular);

	public void exportarFormArquivo(MetadadoTree metadados);

	public void exportarFichArquivo(MetadadoTree metadados);
}