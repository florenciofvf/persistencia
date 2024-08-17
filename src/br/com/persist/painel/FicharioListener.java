package br.com.persist.painel;

@FunctionalInterface
public interface FicharioListener {
	void ficharioVazio(Fichario fichario) throws SeparadorException;
}