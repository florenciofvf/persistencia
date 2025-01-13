package br.com.persist.painel;

public interface FicharioListener {
	void abaSelecionada(Fichario fichario, Transferivel transferivel);

	void ficharioVazio(Fichario fichario) throws SeparadorException;
}