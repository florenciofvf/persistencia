package br.com.persist.desktop;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import br.com.persist.componente.ScrollPane;
import br.com.persist.formulario.AbstratoFormulario;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.Mensagens;

public class DesktopFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final Desktop desktop;

	private DesktopFormulario(Formulario formulario) {
		super(Mensagens.getString(Constantes.LABEL_FORMULARIO));
		desktop = new Desktop(formulario, false);
		montarLayout();
		configurar();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, new ScrollPane(desktop));
	}

	private void configurar() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				desktop.ini(getGraphics());
				desktop.getDistribuicao().distribuir(-Constantes.VINTE);
				desktop.atualizarFormularios();
				desktop.getLarguras().configurar(Largura.TOTAL_A_DIREITA);
			}
		});
	}

	public Desktop getDesktop() {
		return desktop;
	}

	public static DesktopFormulario criar(Formulario formulario) {
		DesktopFormulario form = new DesktopFormulario(formulario);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);

		return form;
	}
}