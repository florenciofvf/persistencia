package br.com.persist.assistencia;

import br.com.persist.componente.Label;

public interface Busca {
	public void selecionar(Label label);

	public void limparSelecao();

	public int getTotal();
}