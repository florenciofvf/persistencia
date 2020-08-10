package br.com.persist.listener;

import br.com.persist.arquivo.ArquivoTree;

public interface ArquivoTreeListener {
	public void selecionarArquivo(ArquivoTree arquivoTree);

	public void abrirFormArquivo(ArquivoTree arquivoTree);

	public void abrirFichArquivo(ArquivoTree arquivoTree);

	public void atualizarArvore(ArquivoTree arquivoTree);

	public void excluirArquivo(ArquivoTree arquivoTree);

	public void fecharArquivo(ArquivoTree arquivoTree);

	public void clickArquivo(ArquivoTree arquivoTree);

	public void pastaArquivo(ArquivoTree arquivoTree);
}