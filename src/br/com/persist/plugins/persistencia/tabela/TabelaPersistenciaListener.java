package br.com.persist.plugins.persistencia.tabela;

public interface TabelaPersistenciaListener {
	public void copiarNomeColuna(TabelaPersistencia tabelaPersistencia, String nome, String anterior);

	public void tabelaMouseClick(TabelaPersistencia tabelaPersistencia, int colunaClicada);

	public void concatenarNomeColuna(TabelaPersistencia tabelaPersistencia, String nome);
}