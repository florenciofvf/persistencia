package br.com.persist.plugins.projeto;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import br.com.persist.arquivo.Arquivo;
import br.com.persist.assistencia.Icones;

public class ProjetoRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = 1L;

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		if (value instanceof Arquivo) {
			configIcon(value);
		}
		return this;
	}

	private void configIcon(Object value) {
		Arquivo obj = (Arquivo) value;
		if (obj.isDescricao()) {
			setIcon(Icones.RULE);
		} else if (obj.isException()) {
			setIcon(Icones.EXCEPTION);
		} else if (obj.isPai()) {
			setIcon(Icones.FAVORITO);
		} else if (obj.isService()) {
			setIcon(Icones.CONFIG);
		} else if (obj.isExec()) {
			setIcon(Icones.EXECUTAR);
		} else if (obj.isAuto()) {
			setIcon(Icones.CONFIG2);
		} else if (obj.isPessoa()) {
			setIcon(Icones.PESSOA);
		} else if (obj.isTimer()) {
			setIcon(Icones.TIMER);
		} else if (obj.isAnexo()) {
			setIcon(Icones.ANEXO);
		} else if (obj.isInfo()) {
			setIcon(Icones.INFO);
		} else if (obj.isSeta()) {
			setIcon(Icones.SETA);
		} else if (obj.isCheck()) {
			setIcon(Icones.SUCESSO);
		} else if (obj.isURL()) {
			setIcon(Icones.URL);
		} else if (obj.isFile()) {
			setIcon(Icones.TEXTO);
		}
	}
}