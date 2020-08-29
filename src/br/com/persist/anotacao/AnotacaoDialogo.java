package br.com.persist.anotacao;

import java.awt.BorderLayout;
import java.awt.Frame;

import br.com.persist.dialogo.AbstratoDialogo;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.Mensagens;

public class AnotacaoDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final AnotacaoContainer container;

	private AnotacaoDialogo(Frame frame, Formulario formulario) {
		super(frame, Mensagens.getString(Constantes.LABEL_ANOTACOES));
		container = new AnotacaoContainer(this, formulario, null);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario) {
		AnotacaoDialogo form = new AnotacaoDialogo(formulario, formulario);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}
}