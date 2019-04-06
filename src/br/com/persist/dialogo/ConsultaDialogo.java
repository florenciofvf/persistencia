package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.Frame;

import br.com.persist.banco.ConexaoProvedor;
import br.com.persist.container.ConsultaContainer;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class ConsultaDialogo extends AbstratoDialogoTMP implements IJanela {
	private static final long serialVersionUID = 1L;
	private final ConsultaContainer container;

	public ConsultaDialogo(Frame frame, ConexaoProvedor provedor) {
		super(frame, Mensagens.getString(Constantes.LABEL_CONSULTA));
		container = new ConsultaContainer(this, provedor, null, null, null);
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