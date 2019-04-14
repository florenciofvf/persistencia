package br.com.persist.listener;

import java.awt.Dimension;

import br.com.persist.util.BuscaAuto.Grupo;

public interface ObjetoContainerListener {
	public void buscaAutomatica(Grupo grupo, String argumentos);

	public void setTitulo(String titulo);

	public Dimension getDimensoes();
}