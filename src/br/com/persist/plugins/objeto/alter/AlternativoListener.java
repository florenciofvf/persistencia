package br.com.persist.plugins.objeto.alter;

import java.util.List;

public interface AlternativoListener {
	public void aplicarAlternativo(List<Alternativo> alternativos, boolean concatenar);

	public List<String> getGrupoFiltro();
}