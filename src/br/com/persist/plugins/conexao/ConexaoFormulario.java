package br.com.persist.plugins.conexao;

import java.awt.BorderLayout;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.formulario.Formulario;

public class ConexaoFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final ConexaoContainer container;

	private ConexaoFormulario(Formulario formulario) {
		super(Mensagens.getString(Constantes.LABEL_CONEXAO));
		container = new ConexaoContainer(this, formulario);
		container.setConexaoFormulario(this);
		montarLayout();
	}

	private ConexaoFormulario(ConexaoContainer container) {
		super(Mensagens.getString(Constantes.LABEL_CONEXAO));
		container.setConexaoFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, ConexaoContainer container) {
		ConexaoFormulario form = new ConexaoFormulario(container);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	public static void criar(Formulario formulario) {
		ConexaoFormulario form = new ConexaoFormulario(formulario);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	@Override
	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setConexaoFormulario(null);
		fechar();
	}

	@Override
	public void executarAoAbrirFormulario() {
		container.formularioVisivel();
	}
}