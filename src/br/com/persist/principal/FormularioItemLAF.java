package br.com.persist.principal;

import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import br.com.persist.util.Util;

class FormularioItemLAF extends JRadioButtonMenuItem {
	private static final long serialVersionUID = 1L;
	private final Formulario formulario;
	private final String classe;

	public FormularioItemLAF(Formulario formulario, LookAndFeelInfo info) {
		addActionListener(e -> processar());
		this.formulario = formulario;
		classe = info.getClassName();
		setText(info.getName());
	}

	public void processar() {
		try {
			UIManager.setLookAndFeel(classe);
			SwingUtilities.updateComponentTreeUI(formulario);
		} catch (Exception ex) {
			Util.stackTraceAndMessage(getClass().getName(), ex, formulario);
		}
	}
}