package br.com.persist.icone;

import br.com.persist.assistencia.AssistenciaException;
import br.com.persist.assistencia.Icone;
import br.com.persist.componente.Label;

public interface IconeListener {
	public void setIcone(Object objeto, String nome, Icone icon) throws AssistenciaException;

	public void limparIcone(Object objeto);

	public Object getOptObjeto();

	public Label getOptLabel();
}