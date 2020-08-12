package br.com.persist.objeto;

import java.awt.Dimension;

import br.com.persist.busca_apos.GrupoBuscaAutoApos;
import br.com.persist.busca_auto.GrupoBuscaAuto;
import br.com.persist.link_auto.GrupoLinkAuto;

public interface ObjetoContainerListener {
	public void buscaAutomatica(GrupoBuscaAuto grupo, String argumentos);

	public void linkAutomatico(GrupoLinkAuto link, String argumento);

	public void buscaAutomaticaApos(GrupoBuscaAutoApos grupoApos);

	public void configAlturaAutomatica(int total);

	public void setTitulo(String titulo);

	public void selecionar(boolean b);

	public Dimension getDimensoes();
}