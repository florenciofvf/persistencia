package br.com.persist.fichario;

import java.awt.Component;
import java.io.File;
import java.util.Map;

import br.com.persist.principal.Formulario;

public interface Pagina {
	public void processar(Formulario formulario, Map<String, Object> args);

	public void adicionadoAoFichario(Fichario fichario);

	public void excluindoDoFichario(Fichario fichario);

	public String getStringPersistencia();

	public Class<?> getClasseFabrica();

	public Component getComponent();

	public Titulo getTitulo();

	public File getFile();
}