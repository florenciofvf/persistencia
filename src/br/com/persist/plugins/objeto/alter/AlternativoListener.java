package br.com.persist.plugins.objeto.alter;

import java.util.List;

public interface AlternativoListener {
	public void aplicarAlternativo(Alternativo alternativo);

	public List<String> getGrupoFiltro();
}