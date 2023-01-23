package br.com.persist.plugins.objeto.alter;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Util;
import br.com.persist.formulario.Formulario;

public class AlternativoDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final AlternativoContainer container;

	private AlternativoDialogo(Frame frame, Formulario formulario, AlternativoListener listener) {
		super(frame, Mensagens.getString(Constantes.LABEL_ALTERNATIVO));
		container = new AlternativoContainer(this, formulario, listener);
		container.setAlternativoDialogo(this);
		montarLayout();
	}

	private AlternativoDialogo(Dialog dialog, Formulario formulario, AlternativoListener listener) {
		super(dialog, Mensagens.getString(Constantes.LABEL_ALTERNATIVO));
		container = new AlternativoContainer(this, formulario, listener);
		container.setAlternativoDialogo(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario) {
		AlternativoDialogo form = criar(formulario, formulario, null);
		Util.configSizeLocation(formulario, form, null);
		form.setVisible(true);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setAlternativoDialogo(null);
		fechar();
	}

	public static AlternativoDialogo criar(Dialog dialog, Formulario formulario, AlternativoListener listener) {
		return new AlternativoDialogo(dialog, formulario, listener);
	}

	public static AlternativoDialogo criar(Frame frame, Formulario formulario, AlternativoListener listener) {
		return new AlternativoDialogo(frame, formulario, listener);
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
		container.dialogOpenedHandler(dialog);
	}
}