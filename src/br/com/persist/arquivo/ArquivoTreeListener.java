package br.com.persist.arquivo;

public interface ArquivoTreeListener {
	public void diretorioArquivo(ArquivoTree arquivoTree);

	public void renomearArquivo(ArquivoTree arquivoTree);

	public void excluirArquivo(ArquivoTree arquivoTree);

	public void abrirArquivo(ArquivoTree arquivoTree);
}