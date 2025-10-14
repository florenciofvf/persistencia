package br.com.persist.plugins.arquivo;

public interface ArquivoTreeListener {
	public void abrirArquivoFormulario(ArquivoTreeExt arquivoTree);

	public void abrirArquivoFichario(ArquivoTreeExt arquivoTree);

	public void focusInputPesquisar(ArquivoTreeExt anexoTree);

	public void selecionarArquivo(ArquivoTreeExt arquivoTree);

	public void diretorioArquivo(ArquivoTreeExt arquivoTree);

	public void atualizarArquivo(ArquivoTreeExt arquivoTree);

	public void conteudoArquivo(ArquivoTreeExt arquivoTree);

	public void excluirArquivo(ArquivoTreeExt arquivoTree);

	public void fecharArquivo(ArquivoTreeExt arquivoTree);

	public void clonarArquivo(ArquivoTreeExt arquivoTree);

	public void clickArquivo(ArquivoTreeExt arquivoTree);
}