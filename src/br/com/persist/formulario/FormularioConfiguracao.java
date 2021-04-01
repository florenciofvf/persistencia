package br.com.persist.formulario;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JTabbedPane;

import br.com.persist.abstrato.AbstratoConfiguracao;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.Panel;

public class FormularioConfiguracao extends AbstratoConfiguracao {
	private static final long serialVersionUID = 1L;
	private final CheckBox chkFicharioScroll = new CheckBox("label.fichario_scroll");

	public FormularioConfiguracao(Formulario formulario) {
		super(formulario, Mensagens.getTituloAplicacao());
		montarLayout();
		configurar();
	}

	private void montarLayout() {
		chkFicharioScroll.setSelected(Preferencias.isFicharioComRolagem());

		Panel container = new Panel(new GridLayout(0, 1));
		container.add(chkFicharioScroll);

		Insets insets = new Insets(5, 10, 5, 5);
		chkFicharioScroll.setMargin(insets);

		add(BorderLayout.CENTER, container);
	}

	private void configurar() {
		chkFicharioScroll.addActionListener(e -> {
			Preferencias.setFicharioComRolagem(chkFicharioScroll.isSelected());
			formulario.setTabLayoutPolicy(
					Preferencias.isFicharioComRolagem() ? JTabbedPane.SCROLL_TAB_LAYOUT : JTabbedPane.WRAP_TAB_LAYOUT);
		});
	}
}