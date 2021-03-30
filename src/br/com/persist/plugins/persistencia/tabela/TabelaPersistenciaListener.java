package br.com.persist.plugins.persistencia.tabela;

public interface TabelaPersistenciaListener {
	public void colocarColunaComMemoria(TabelaPersistencia tabelaPersistencia, String nome, String memoria);

	public void tabelaMouseClick(TabelaPersistencia tabelaPersistencia, int colunaClicada);

	public void concatenarNomeColuna(TabelaPersistencia tabelaPersistencia, String nome);

	public void colocarNomeColuna(TabelaPersistencia tabelaPersistencia, String nome);
}