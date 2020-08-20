package br.com.persist.desktop;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import br.com.persist.componente.ScrollPane;
import br.com.persist.formulario.AbstratoFormulario;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Util;

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

	public static void posicionar(Formulario principal, DesktopFormulario formulario) {
		final int espaco = 3;
		Point principalLocation = principal.getLocation();
		Dimension principalSize = principal.getSize();
		Rectangle configuraSize = principal.getGraphicsConfiguration().getBounds();

		if (!Util.porcentagemMaiorQue(principalSize.height, configuraSize.height, 70)) {
			int x = principalLocation.x;
			int y = principalLocation.y + principalSize.height + espaco;
			int l = principalSize.width;
			int a = configuraSize.height - (principalLocation.y + principalSize.height) - espaco;
			formulario.setBounds(x, y, l, a);

		} else if (!Util.porcentagemMaiorQue(principalSize.width, configuraSize.width, 70)) {
			int x = principalLocation.x + principalSize.width + espaco;
			int y = principalLocation.y;
			int l = configuraSize.width - (principalLocation.x + principalSize.width) - espaco;
			int a = principalSize.height;
			formulario.setBounds(x, y, l, a);

		} else {
			formulario.setLocationRelativeTo(principal);
		}
	}
}