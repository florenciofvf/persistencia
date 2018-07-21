package br.com.persist.formulario;

import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import br.com.persist.util.Util;

public class ItemLAF extends JRadioButtonMenuItem {
	private static final long serialVersionUID = 1L;
	private final String classe;

	public ItemLAF(Formulario formulario, LookAndFeelInfo info) {
		classe = info.getClassName();
		setText(info.getName());

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