package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import br.com.persist.container.MapeamentoContainer;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class MapeamentoDialogo extends AbstratoDialogo implements IJanela {
	private static final long serialVersionUID = 1L;
	private final MapeamentoContainer container;

	public MapeamentoDialogo(Dialog dialog, Formulario formulario) {
		super(dialog, Mensagens.getString(Constantes.LABEL_MAPEAMENTOS));
		container = new MapeamentoContainer(this, formulario);
		montarLayout();
		configurar();
	}

	public MapeamentoDialogo(Frame frame, Formulario formulario) {
		super(frame, Mensagens.getString(Constantes.LABEL_MAPEAMENTOS));
		container = new MapeamentoContainer(this, formulario);
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

	public static void criar(Formulario formulario) {
		MapeamentoDialogo form = new MapeamentoDialogo(formulario, formulario);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
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