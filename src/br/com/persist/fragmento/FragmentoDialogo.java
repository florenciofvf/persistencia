package br.com.persist.fragmento;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import br.com.persist.dialogo.AbstratoDialogo;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class FragmentoDialogo extends AbstratoDialogo implements IJanela {
	private static final long serialVersionUID = 1L;
	private final FragmentoContainer container;

	private FragmentoDialogo(Dialog dialog, Formulario formulario, FragmentoListener listener) {
		super(dialog, Mensagens.getString(Constantes.LABEL_FRAGMENTO));
		container = new FragmentoContainer(this, formulario, listener);
		montarLayout();
		configurar();
	}

	private FragmentoDialogo(Frame frame, Formulario formulario, FragmentoListener listener) {
		super(frame, Mensagens.getString(Constantes.LABEL_FRAGMENTO));
		container = new FragmentoContainer(this, formulario, listener);
		montarLayout();
		configurar();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	private void configurar() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				container.ini(getGraphics());
			}
		});
	}

	@Override
	public void fechar() {
		dispose();
	}

	public static void criar(Formulario formulario) {
		FragmentoDialogo form = criar(formulario, formulario, null);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	public static FragmentoDialogo criar(Dialog dialog, Formulario formulario, FragmentoListener listener) {
		return new FragmentoDialogo(dialog, formulario, listener);
	}

	public static FragmentoDialogo criar(Frame frame, Formulario formulario, FragmentoListener listener) {
		return new FragmentoDialogo(frame, formulario, listener);
	}
}