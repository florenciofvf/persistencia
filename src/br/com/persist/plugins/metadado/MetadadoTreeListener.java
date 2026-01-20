package br.com.persist.plugins.metadado;

import br.com.persist.assistencia.AssistenciaException;

public interface MetadadoTreeListener {
	public void abrirExportacaoDialogArquivo(MetadadoTree metadadoTree, boolean circular);

	public void abrirExportacaoDialogArquivoCircularAndMemoria(MetadadoTree metadadoTree);

	public void abrirImportacaoDialogArquivo(MetadadoTree metadadoTree, boolean circular);

	public void abrirExportacaoFormArquivo(MetadadoTree metadadoTree, boolean circular);

	public void abrirExportacaoFormArquivoCircularAndMemoria(MetadadoTree metadadoTree);

	public void abrirImportacaoFormArquivo(MetadadoTree metadadoTree, boolean circular);

	public void abrirExportacaoFichArquivo(MetadadoTree metadadoTree, boolean circular);

	public void abrirExportacaoFichArquivoCircularAndMemoria(MetadadoTree metadadoTree);

	public void abrirImportacaoFichArquivo(MetadadoTree metadadoTree, boolean circular);

	public void registros(MetadadoTree metadadoTree) throws AssistenciaException;

	public void exportarDialogArquivo(MetadadoTree metadadoTree);

	public void exportarFormArquivo(MetadadoTree metadadoTree);

	public void exportarFichArquivo(MetadadoTree metadadoTree);

	public void focusInputPesquisar(MetadadoTree metadadoTree);

	public void atualizarArvore(MetadadoTree metadadoTree);

	public void constraintInfo(MetadadoTree metadadoTree);
}