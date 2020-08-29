package br.com.persist.desktop;

import java.awt.BorderLayout;

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
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, new ScrollPane(desktop));
	}

	@Override
	public void executarAoAbrirForm() {
		desktop.ini(getGraphics());
		desktop.getDistribuicao().distribuir(-Constantes.VINTE);
		desktop.atualizarFormularios();
		desktop.getLarguras().configurar(Largura.TOTAL_A_DIREITA);
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