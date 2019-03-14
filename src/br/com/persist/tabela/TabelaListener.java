package br.com.persist.tabela;

public interface TabelaListener {
	public void copiarNomeColuna(Tabela tabela, String nome);

	public void tabelaMouseClick(Tabela tabela);
}