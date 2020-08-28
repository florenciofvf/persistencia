package br.com.persist.mapeamento;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import br.com.persist.dialogo.AbstratoDialogo;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class MapeamentoDialogo extends AbstratoDialogo implements IJanela {
	private static final long serialVersionUID = 1L;
	private final MapeamentoContainer container;

	private MapeamentoDialogo(Dialog dialog, Formulario formulario) {
		super(dialog, Mensagens.getString(Constantes.LABEL_MAPEAMENTOS));
		container = new MapeamentoContainer(this, formulario);
		montarLayout();
	}

	private MapeamentoDialogo(Frame frame, Formulario formulario) {
		super(frame, Mensagens.getString(Constantes.LABEL_MAPEAMENTOS));
		container = new MapeamentoContainer(this, formulario);
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

	public static void criar(Formulario formulario) {
		MapeamentoDialogo form = criar(formulario, formulario);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	public static MapeamentoDialogo criar(Dialog dialog, Formulario formulario) {
		return new MapeamentoDialogo(dialog, formulario);
	}

	public static MapeamentoDialogo criar(Frame frame, Formulario formulario) {
		return new MapeamentoDialogo(frame, formulario);
	}
}