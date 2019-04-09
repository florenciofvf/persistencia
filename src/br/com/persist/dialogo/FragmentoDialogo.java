package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import br.com.persist.container.FragmentoContainer;
import br.com.persist.listener.FragmentoListener;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class FragmentoDialogo extends AbstratoDialogo implements IJanela {
	private static final long serialVersionUID = 1L;
	private final FragmentoContainer container;

	public FragmentoDialogo(Dialog dialog, FragmentoListener listener) {
		super(dialog, Mensagens.getString(Constantes.LABEL_FRAGMENTO));
		container = new FragmentoContainer(this, listener);
		montarLayout();
		configurar();
	}

	public FragmentoDialogo(Frame frame, FragmentoListener listener) {
		super(frame, Mensagens.getString(Constantes.LABEL_FRAGMENTO));
		container = new FragmentoContainer(this, listener);
		montarLayout();
		configurar();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	@Override
	public void fechar() {
		dispose();
	}

	private void configurar() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				container.ini(getGraphics());
			}
		});
	}
}