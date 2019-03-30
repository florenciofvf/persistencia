package br.com.persist.arvore;

public interface ArvoreListener {
	public void abrirFormArquivo(Arvore arvore);

	public void abrirFichArquivo(Arvore arvore);

	public void atualizarArvore(Arvore arvore);

	public void excluirArquivo(Arvore arvore);
}