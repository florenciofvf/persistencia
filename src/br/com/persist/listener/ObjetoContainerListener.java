package br.com.persist.listener;

import java.awt.Dimension;
import java.util.concurrent.atomic.AtomicBoolean;

import br.com.persist.util.BuscaAuto.Grupo;

public interface ObjetoContainerListener {
	public void buscaAutomatica(Grupo grupo, String argumentos, AtomicBoolean processado);

	public void setTitulo(String titulo);

	public Dimension getDimensoes();
}