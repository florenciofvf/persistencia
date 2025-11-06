package br.com.persist.plugins.metadado;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.ArgumentoException;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Util;
import br.com.persist.formulario.Formulario;
import br.com.persist.plugins.conexao.Conexao;

public class MetadadoDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final MetadadoContainer container;

	private MetadadoDialogo(Frame frame, Formulario formulario, Conexao conexao) throws ArgumentoException {
		super(frame, Mensagens.getString(Constantes.LABEL_METADADOS));
		container = new MetadadoContainer(this, formulario, conexao);
		container.setMetadadoDialogo(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, Conexao conexao) throws ArgumentoException {
		MetadadoDialogo form = new MetadadoDialogo(formulario, formulario, conexao);
		Util.configSizeLocation(formulario, form, null);
		form.setVisible(true);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setMetadadoDialogo(null);
		fechar();
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
		container.dialogOpenedHandler(dialog);
	}
}