package br.com.persist.metadado;

public interface MetadadoTreeListener {
	public void abrirExportacaoFormArquivo(MetadadoTree metadadoTree, boolean circular);

	public void abrirExportacaoFichArquivo(MetadadoTree metadadoTree, boolean circular);

	public void abrirImportacaoFormArquivo(MetadadoTree metadadoTree, boolean circular);

	public void abrirImportacaoFichArquivo(MetadadoTree metadadoTree, boolean circular);

	public void exportarFormArquivo(MetadadoTree metadadoTree);

	public void exportarFichArquivo(MetadadoTree metadadoTree);
}