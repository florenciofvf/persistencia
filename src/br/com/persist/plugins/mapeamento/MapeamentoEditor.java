package br.com.persist.plugins.mapeamento;

import br.com.persist.abstrato.AbstratoEditor;
import br.com.persist.componente.SetValor;
import br.com.persist.componente.SetValor.Valor;
import br.com.persist.componente.Table;

public class MapeamentoEditor extends AbstratoEditor {
	private static final long serialVersionUID = 1L;

	@Override
	public void abrirModalEdicaoValor(Table table, int row) {
		Valor valor = MapeamentoProvedor.getValor(row);
		SetValor.view(table, valor);
	}
}