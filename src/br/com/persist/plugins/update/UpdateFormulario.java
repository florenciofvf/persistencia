package br.com.persist.plugins.update;

import java.awt.BorderLayout;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.Mensagens;

public class UpdateFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final UpdateContainer container;

	private UpdateFormulario(Formulario formulario, Conexao conexao, String conteudo) {
		super(Mensagens.getString(Constantes.LABEL_UPDATE));
		container = new UpdateContainer(this, formulario, conexao, conteudo);
		container.setUpdateFormulario(this);
		montarLayout();
	}

	private UpdateFormulario(UpdateContainer container) {
		super(Mensagens.getString(Constantes.LABEL_UPDATE));
		container.setUpdateFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, UpdateContainer container) {
		UpdateFormulario form = new UpdateFormulario(container);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	public static UpdateFormulario criar(Formulario formulario, Conexao conexao, String conteudo) {
		UpdateFormulario form = new UpdateFormulario(formulario, conexao, conteudo);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
		return form;
	}

	@Override
	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setUpdateFormulario(null);
		fechar();
	}

	@Override
	public void executarAoAbrirFormulario() {
		container.formularioVisivel();
	}
}