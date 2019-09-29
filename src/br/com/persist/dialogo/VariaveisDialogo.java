package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import br.com.persist.container.VariaveisContainer;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class VariaveisDialogo extends AbstratoDialogo implements IJanela {
	private static final long serialVersionUID = 1L;
	private final VariaveisContainer container;

	public VariaveisDialogo(Dialog dialog) {
		super(dialog, Mensagens.getString(Constantes.LABEL_VARIAVEIS));
		container = new VariaveisContainer(this);
		montarLayout();
		configurar();
	}

	public VariaveisDialogo(Frame frame) {
		super(frame, Mensagens.getString(Constantes.LABEL_VARIAVEIS));
		container = new VariaveisContainer(this);
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