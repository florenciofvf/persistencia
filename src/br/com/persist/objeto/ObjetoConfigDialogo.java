package br.com.persist.objeto;

import java.awt.BorderLayout;
import java.awt.Frame;

import br.com.persist.dialogo.AbstratoDialogo;
import br.com.persist.superficie.Superficie;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;

public class ObjetoConfigDialogo extends AbstratoDialogo implements IJanela {
	private static final long serialVersionUID = 1L;
	private final ObjetoConfigContainer container;

	private ObjetoConfigDialogo(Frame frame, Superficie superficie, Objeto objeto) {
		super(frame, objeto.getId());
		setSize(Constantes.SIZE2);
		container = new ObjetoConfigContainer(this, superficie, objeto);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	@Override
	public void executarAoAbrirDialog() {
		container.ini(getGraphics());
	}

	@Override
	public void fechar() {
		dispose();
	}

	public static ObjetoConfigDialogo criar(Frame frame, Superficie superficie, Objeto objeto) {
		ObjetoConfigDialogo form = new ObjetoConfigDialogo(frame, superficie, objeto);
		form.setLocationRelativeTo(frame);
		form.setVisible(true);

		return form;
	}
}