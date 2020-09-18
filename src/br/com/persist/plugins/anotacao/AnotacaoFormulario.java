package br.com.persist.plugins.anotacao;

import java.awt.BorderLayout;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.formulario.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.Mensagens;

public class AnotacaoFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final AnotacaoContainer container;

	private AnotacaoFormulario(Formulario formulario, String conteudo) {
		super(Mensagens.getString(Constantes.LABEL_ANOTACOES));
		container = new AnotacaoContainer(this, formulario, conteudo);
		container.setAnotacaoFormulario(this);
		montarLayout();
	}

	private AnotacaoFormulario(AnotacaoContainer container) {
		super(Mensagens.getString(Constantes.LABEL_ANOTACOES));
		container.setAnotacaoFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, AnotacaoContainer container) {
		AnotacaoFormulario form = new AnotacaoFormulario(container);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	public static void criar(Formulario formulario, String conteudo) {
		AnotacaoFormulario form = new AnotacaoFormulario(formulario, conteudo);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	@Override
	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setAnotacaoFormulario(null);
		fechar();
	}

	@Override
	public void executarAoAbrirFormulario() {
		container.formularioVisivel();
	}
}