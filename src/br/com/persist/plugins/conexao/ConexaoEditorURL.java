package br.com.persist.plugins.conexao;

import javax.swing.JTable;

import br.com.persist.abstrato.AbstratoEditor;
import br.com.persist.componente.SetValor;
import br.com.persist.componente.SetValor.Valor;

public class ConexaoEditorURL extends AbstratoEditor {
	private static final long serialVersionUID = 1L;

	@Override
	public void abrirModalEdicaoValor(JTable table, int row) {
		Valor valor = ConexaoProvedor.getValorUrl(row);
		SetValor.view(table, valor);
	}
}