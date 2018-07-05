package br.com.persist.formulario;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import br.com.persist.comp.RadioButtonMenuItem;
import br.com.persist.util.Util;

public class ItemLAF extends RadioButtonMenuItem {
	private static final long serialVersionUID = 1L;
	private final String classe;

	public ItemLAF(Formulario formulario, LookAndFeelInfo info) {
		this.setText(info.getName());
		classe = info.getClassName();

		addActionListener(e -> {
			try {
				UIManager.setLookAndFeel(classe);
				SwingUtilities.updateComponentTreeUI(formulario);
			} catch (Exception ex) {
				Util.stackTraceAndMessage(getClass().getName() + ".ItemMenu()", ex, formulario);
			}
		});
	}
}