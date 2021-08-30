package br.com.persist.plugins.conexao;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Util;
import br.com.persist.formulario.Formulario;

public class ConexaoDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final ConexaoContainer container;

	private ConexaoDialogo(Frame frame, Formulario formulario) {
		super(frame, Mensagens.getString(Constantes.LABEL_CONEXAO));
		container = new ConexaoContainer(this, formulario);
		container.setConexaoDialogo(this);
		montarLayout();
	}

	private ConexaoDialogo(Dialog dialog, Formulario formulario) {
		super(dialog, Mensagens.getString(Constantes.LABEL_CONEXAO));
		container = new ConexaoContainer(this, formulario);
		container.setConexaoDialogo(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario) {
		ConexaoDialogo form = new ConexaoDialogo(formulario, formulario);
		Util.configSizeLocation(formulario, form, null);
		form.setVisible(true);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setConexaoDialogo(null);
		fechar();
	}

	public static ConexaoDialogo criar(Dialog dialog, Formulario formulario) {
		return new ConexaoDialogo(dialog, formulario);
	}

	public static ConexaoDialogo criar(Frame frame, Formulario formulario) {
		return new ConexaoDialogo(frame, formulario);
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
		container.dialogOpenedHandler(this);
	}
}