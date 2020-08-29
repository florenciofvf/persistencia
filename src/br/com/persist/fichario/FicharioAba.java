package br.com.persist.fichario;

import java.awt.Component;

import javax.swing.Icon;

public interface FicharioAba {
	public String getChaveTituloMin();

	public Component getComponent();

	public String getChaveTitulo();

	public String getHintTitulo();

	public Icon getIcone();
}