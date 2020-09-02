package br.com.persist.fichario;

import java.awt.Component;

public interface Pagina {
	public void adicionadoAoFichario(Fichario fichario);

	public void excluindoDoFichario(Fichario fichario);

	public String getStringPersistencia();

	public Class<?> getClasseFabrica();

	public Component getComponent();

	public Titulo getTitulo();
}