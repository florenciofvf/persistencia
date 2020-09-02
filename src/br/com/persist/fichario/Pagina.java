package br.com.persist.fichario;

import java.awt.Component;
import java.io.File;

public interface Pagina {
	public void adicionadoAoFichario(Fichario fichario);

	public void excluindoDoFichario(Fichario fichario);

	public String getStringPersistencia();

	public Class<?> getClasseFabrica();

	public Component getComponent();

	public Titulo getTitulo();

	public File getFile();
}