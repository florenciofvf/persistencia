package br.com.persist.arquivo;

public interface ArquivoTreeListener {
	public void focusInputPesquisar(ArquivoTree arquivoTree);

	public void diretorioArquivo(ArquivoTree arquivoTree);

	public void renomearArquivo(ArquivoTree arquivoTree);

	public void excluirArquivo(ArquivoTree arquivoTree);

	public void novoDiretorio(ArquivoTree arquivoTree);

	public void clonarArquivo(ArquivoTree arquivoTree);

	public void abrirArquivo(ArquivoTree arquivoTree);

	public void novoArquivo(ArquivoTree arquivoTree);
}