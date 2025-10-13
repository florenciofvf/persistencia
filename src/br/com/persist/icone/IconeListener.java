package br.com.persist.icone;

import javax.swing.Icon;

import br.com.persist.assistencia.AssistenciaException;
import br.com.persist.componente.Label;

public interface IconeListener {
	public void setIcone(Object objeto, String nome, Icon icon) throws AssistenciaException;

	public void limparIcone(Object objeto);

	public Object getOptObjeto();

	public Label getOptLabel();
}