package br.com.persist.plugins.update;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Util;
import br.com.persist.formulario.Formulario;
import br.com.persist.plugins.conexao.Conexao;

public class UpdateDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final UpdateContainer container;

	private UpdateDialogo(Frame frame, Formulario formulario, Conexao conexao, String conteudo) {
		super(frame, Mensagens.getString(Constantes.LABEL_ATUALIZAR));
		container = new UpdateContainer(this, formulario, conexao, conteudo);
		container.setUpdateDialogo(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static UpdateDialogo criar(Formulario formulario, Conexao conexao, String conteudo) {
		UpdateDialogo form = new UpdateDialogo(formulario, formulario, conexao, conteudo);
		Util.configSizeLocation(formulario, form, null);
		form.setVisible(true);
		return form;
	}

	public static UpdateDialogo criar2(Formulario formulario, Conexao conexao, String conteudo) {
		return new UpdateDialogo(formulario, formulario, conexao, conteudo);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setUpdateDialogo(null);
		fechar();
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
		container.dialogOpenedHandler(dialog);
	}
}