package br.com.persist.plugins.expressao;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Util;
import br.com.persist.formulario.Formulario;

public class ExpressaoDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final ExpressaoContainer container;

	private ExpressaoDialogo(Frame frame, Formulario formulario) {
		super(frame, ExpressaoMensagens.getString(ExpressaoConstantes.LABEL_EXPRESSAO));
		container = new ExpressaoContainer(this, formulario);
		container.setExpressaoDialogo(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario) {
		ExpressaoDialogo form = new ExpressaoDialogo(formulario, formulario);
		Util.configSizeLocation(formulario, form, null);
		form.setVisible(true);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setExpressaoDialogo(null);
		fechar();
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
		container.dialogOpenedHandler(dialog);
	}
}