package br.com.persist.plugins.legado;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Util;
import br.com.persist.formulario.Formulario;

public class LegadoDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final LegadoContainer container;

	private LegadoDialogo(Frame frame, Formulario formulario) {
		super(frame, LegadoMensagens.getString(LegadoConstantes.LABEL_LEGADO));
		container = new LegadoContainer(this, formulario, null, null);
		container.setLegadoDialogo(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario) {
		LegadoDialogo form = new LegadoDialogo(formulario, formulario);
		Util.configSizeLocation(formulario, form, null);
		form.setVisible(true);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setLegadoDialogo(null);
		fechar();
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
		container.dialogOpenedHandler(dialog);
	}
}