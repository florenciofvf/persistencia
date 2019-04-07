package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

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
	}

	public FragmentoDialogo(Frame frame, FragmentoListener listener) {
		super(frame, Mensagens.getString(Constantes.LABEL_FRAGMENTO));
		container = new FragmentoContainer(this, listener);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	@Override
	public void fechar() {
		dispose();
	}
}