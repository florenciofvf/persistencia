package br.com.persist.listener;

import br.com.persist.arvore.Arvore;

public interface ArvoreListener {
	public void selecionarArquivo(Arvore arvore);

	public void abrirFormArquivo(Arvore arvore);

	public void abrirFichArquivo(Arvore arvore);

	public void atualizarArvore(Arvore arvore);

	public void excluirArquivo(Arvore anexo);

	public void fecharArquivo(Arvore arvore);

	public void clickArquivo(Arvore arvore);

	public void pastaArquivo(Arvore arvore);
}