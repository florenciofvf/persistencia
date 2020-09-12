package br.com.persist.plugins.objeto;

import java.awt.BorderLayout;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.abstrato.DesktopLargura;
import br.com.persist.componente.ScrollPane;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.Mensagens;

public class DesktopFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final Desktop desktop;

	private DesktopFormulario() {
		super(Mensagens.getString(Constantes.LABEL_FORMULARIO));
		desktop = new Desktop(false);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, new ScrollPane(desktop));
	}

	public static DesktopFormulario criar(Formulario formulario) {
		DesktopFormulario form = new DesktopFormulario();
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
		return form;
	}

	@Override
	public void executarAoAbrirFormulario() {
		desktop.getDistribuicao().distribuir(-Constantes.VINTE);
		desktop.atualizarFormularios();
		desktop.getLarguras().configurar(DesktopLargura.TOTAL_A_DIREITA);
	}
}