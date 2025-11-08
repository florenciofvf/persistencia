package br.com.persist.plugins.objeto;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.ScrollPane;
import br.com.persist.formulario.Formulario;

public class DesktopDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final Desktop desktop;

	private DesktopDialogo(Formulario formulario) {
		super((Frame) null, Mensagens.getString(Constantes.LABEL_FORMULARIO));
		desktop = new Desktop(formulario, false);
		desktop.setAjusteAutoLarguraForm(true);
		desktop.addTotalDireitoAuto();
		montarLayout();
		config();
	}

	private void config() {
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				desktop.configurarLargura(getSize());
			}
		});
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, new ScrollPane(desktop));
	}

	public Desktop getDesktop() {
		return desktop;
	}

	public static DesktopDialogo criar(Formulario formulario) {
		DesktopDialogo form = new DesktopDialogo(formulario);
		Util.configSizeLocation(formulario, form, null);
		return form;
	}

	@Override
	public void dialogActivatedHandler(Dialog dialog) {
		desktop.windowActivatedHandler(dialog);
	}
}