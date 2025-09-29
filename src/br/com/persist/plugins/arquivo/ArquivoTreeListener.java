package br.com.persist.plugins.arquivo;

public interface ArquivoTreeListener {
	public void abrirArquivoFormulario(ArquivoTree arquivoTree);

	public void abrirArquivoFichario(ArquivoTree arquivoTree);

	public void focusInputPesquisar(ArquivoTree anexoTree);

	public void selecionarArquivo(ArquivoTree arquivoTree);

	public void diretorioArquivo(ArquivoTree arquivoTree);

	public void atualizarArquivo(ArquivoTree arquivoTree);

	public void conteudoArquivo(ArquivoTree arquivoTree);

	public void excluirArquivo(ArquivoTree arquivoTree);

	public void fecharArquivo(ArquivoTree arquivoTree);

	public void clonarArquivo(ArquivoTree arquivoTree);

	public void clickArquivo(ArquivoTree arquivoTree);
}