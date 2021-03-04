package br.com.persist.plugins.persistencia.tabela;

public interface TabelaPersistenciaListener {
	public void copiarNomeColunaLike(TabelaPersistencia tabelaPersistencia, String nome, String anterior);

	public void copiarNomeColuna(TabelaPersistencia tabelaPersistencia, String nome, String anterior);

	public void concatenarNomeColunaLike(TabelaPersistencia tabelaPersistencia, String nome);

	public void tabelaMouseClick(TabelaPersistencia tabelaPersistencia, int colunaClicada);

	public void concatenarNomeColuna(TabelaPersistencia tabelaPersistencia, String nome);
}