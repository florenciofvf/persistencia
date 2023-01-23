package br.com.persist.plugins.objeto.alter;

import javax.swing.JTable;

import br.com.persist.abstrato.AbstratoEditor;
import br.com.persist.componente.SetValor;
import br.com.persist.componente.SetValor.Valor;

public class AlternativoEditor extends AbstratoEditor {
	private static final long serialVersionUID = 1L;

	@Override
	public void abrirModalEdicaoValor(JTable table, int row) {
		Valor valor = AlternativoProvedor.getValor(row);
		SetValor.view(table, valor);
	}
}