package br.com.persist.dialogo;

import java.awt.BorderLayout;

import javax.swing.JTabbedPane;

import br.com.persist.comp.CheckBox;
import br.com.persist.formulario.Formulario;
import br.com.persist.util.Mensagens;

public class ConfigDialogo extends Dialogo {
	private static final long serialVersionUID = 1L;
	private final CheckBox chkFicharioScroll = new CheckBox("label.fichario_scroll");
	private final Formulario formulario;

	public ConfigDialogo(Formulario formulario) {
		super(formulario, Mensagens.getString("label.configuracoes"), 700, 200, false);
		this.formulario = formulario;
		montarLayout();
		configurar();
		setVisible(true);
	}

	private void montarLayout() {
		chkFicharioScroll.setSelected(formulario.getFichario().getTabLayoutPolicy() == JTabbedPane.SCROLL_TAB_LAYOUT);
		add(BorderLayout.CENTER, chkFicharioScroll);
	}

	private void configurar() {
		chkFicharioScroll.addActionListener(e -> formulario.getFichario().setTabLayoutPolicy(
				chkFicharioScroll.isSelected() ? JTabbedPane.SCROLL_TAB_LAYOUT : JTabbedPane.WRAP_TAB_LAYOUT));
	}

	protected void processar() {
	}
}