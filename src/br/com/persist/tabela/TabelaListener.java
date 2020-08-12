package br.com.persist.tabela;

public interface TabelaListener {
	public void tabelaMouseClick(Tabela tabela, int colunaClick);

	public void copiarNomeColuna(Tabela tabela, String nome, String anterior);

	public void concatenarNomeColuna(Tabela tabela, String nome);
}