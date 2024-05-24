package br.com.persist.plugins.persistencia.tabela;

import br.com.persist.plugins.persistencia.Coluna;

public interface TabelaPersistenciaListener {
	public void colocarNomeColunaAtalho(TabelaPersistencia tabelaPersistencia, String nome, boolean concat,
			Coluna coluna);

	public void colocarColunaComMemoriaAtalho(TabelaPersistencia tabelaPersistencia, String nome, String memoria);

	public void selectTotalValoresQueRepetem(TabelaPersistencia tabelaPersistencia, String nome, boolean form);

	public void selectTotalValorMaisRepetido(TabelaPersistencia tabelaPersistencia, String nome, boolean form);

	public void selectTotalMaiorLengthString(TabelaPersistencia tabelaPersistencia, String nome, boolean form);

	public void selectValorRepetidoComSuaQtd(TabelaPersistencia tabelaPersistencia, String nome, boolean form);

	public void colocarColunaComMemoria(TabelaPersistencia tabelaPersistencia, String nome, String memoria);

	public void colocarNomeColuna(TabelaPersistencia tabelaPersistencia, String nome, Coluna coluna);

	public void selectDistinct(TabelaPersistencia tabelaPersistencia, String nome, boolean form);

	public void selectGroupBy(TabelaPersistencia tabelaPersistencia, String nome, boolean form);

	public void selectMinimo(TabelaPersistencia tabelaPersistencia, String nome, boolean form);

	public void selectMaximo(TabelaPersistencia tabelaPersistencia, String nome, boolean form);

	public void tabelaMouseClick(TabelaPersistencia tabelaPersistencia, int colunaClicada);

	public void pesquisaApartirColuna(TabelaPersistencia tabelaPersistencia, String nome);

	public void mapearApartirBiblio(TabelaPersistencia tabelaPersistencia, String nome);

	public void campoExportadoPara(String coluna);

	public void campoImportadoDe(String coluna);
}