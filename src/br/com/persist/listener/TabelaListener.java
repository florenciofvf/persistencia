package br.com.persist.listener;

import br.com.persist.tabela.Tabela;

public interface TabelaListener {
	public void tabelaMouseClick(Tabela tabela, int colunaClick);

	public void copiarNomeColuna(Tabela tabela, String nome);
}