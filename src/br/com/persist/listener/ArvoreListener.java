package br.com.persist.listener;

import br.com.persist.arvore.Arvore;

public interface ArvoreListener {
	public void abrirFormArquivo(Arvore arvore);

	public void abrirFichArquivo(Arvore arvore);

	public void atualizarArvore(Arvore arvore);

	public void excluirArquivo(Arvore arvore);
}