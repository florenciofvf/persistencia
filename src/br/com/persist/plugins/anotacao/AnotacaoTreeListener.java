package br.com.persist.plugins.anotacao;

public interface AnotacaoTreeListener {
	public void diretorioAnotacao(AnotacaoTree anotacaoTree);

	public void conteudoAnotacao(AnotacaoTree anotacaoTree);

	public void renomearAnotacao(AnotacaoTree anotacaoTree);

	public void excluirAnotacao(AnotacaoTree anotacaoTree);

	public void abrirAnotacao(AnotacaoTree anotacaoTree);
}