package br.com.persist.plugins.persistencia.tabela;

import br.com.persist.plugins.persistencia.Coluna;

public interface TabelaPersistenciaListener {
	public void colocarNomeColunaAtalho(TabelaPersistencia tabelaPersistencia, String nome, boolean concat,
			Coluna coluna);

	public void colocarColunaComMemoriaAtalho(TabelaPersistencia tabelaPersistencia, String nome, String memoria);

	public void colocarColunaComMemoria(TabelaPersistencia tabelaPersistencia, String nome, String memoria);

	public void tabelaMouseClick(TabelaPersistencia tabelaPersistencia, int colunaClicada);

	public void pesquisaApartirColuna(TabelaPersistencia tabelaPersistencia, String nome);

	public void colocarNomeColuna(TabelaPersistencia tabelaPersistencia, String nome);

	public void campoExportadoPara(String coluna);

	public void campoImportadoDe(String coluna);
}