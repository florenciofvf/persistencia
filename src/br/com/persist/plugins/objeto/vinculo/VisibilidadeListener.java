package br.com.persist.plugins.objeto.vinculo;

import java.util.List;

public interface VisibilidadeListener {
	public List<Referencia> getReferencias();

	public void salvar();
}