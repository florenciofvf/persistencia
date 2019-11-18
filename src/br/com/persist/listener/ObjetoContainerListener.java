package br.com.persist.listener;

import java.awt.Dimension;

import br.com.persist.util.BuscaAuto.Grupo;
import br.com.persist.util.LinkAuto.Link;

public interface ObjetoContainerListener {
	public void buscaAutomatica(Grupo grupo, String argumentos);

	public void linkAutomatico(Link link, String argumento);

	public void configAlturaAutomatica(int total);

	public void setTitulo(String titulo);

	public Dimension getDimensoes();
}