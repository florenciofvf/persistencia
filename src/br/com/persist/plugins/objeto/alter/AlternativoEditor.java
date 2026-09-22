package br.com.persist.plugins.objeto.alter;

import br.com.persist.abstrato.AbstratoEditor;
import br.com.persist.componente.SetValor;
import br.com.persist.componente.SetValor.Valor;
import br.com.persist.componente.Table;

public class AlternativoEditor extends AbstratoEditor {
	private static final long serialVersionUID = 1L;

	@Override
	public void abrirModalEdicaoValor(Table table, int row) {
		Valor valor = AlternativoProvedor.getValor(row);
		SetValor.view(table, valor);
	}
}