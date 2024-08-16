package br.com.persist.icone;

import br.com.persist.assistencia.AssistenciaException;

public interface IconeListener {
	public void setIcone(String nome) throws AssistenciaException;

	public void limparIcone();
}