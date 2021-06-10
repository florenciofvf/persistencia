package br.com.persist.plugins.mapeamento;

import java.awt.BorderLayout;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.formulario.Formulario;

public class MapeamentoFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final MapeamentoContainer container;

	private MapeamentoFormulario(Formulario formulario) {
		super(Mensagens.getString(Constantes.LABEL_MAPEAMENTOS));
		container = new MapeamentoContainer(this, formulario);
		container.setMapeamentoFormulario(this);
		montarLayout();
	}

	private MapeamentoFormulario(MapeamentoContainer container) {
		super(Mensagens.getString(Constantes.LABEL_MAPEAMENTOS));
		container.setMapeamentoFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, MapeamentoContainer container) {
		MapeamentoFormulario form = new MapeamentoFormulario(container);
		Formulario.posicionarJanela(formulario, form);
	}

	public static void criar(Formulario formulario) {
		MapeamentoFormulario form = new MapeamentoFormulario(formulario);
		Formulario.posicionarJanela(formulario, form);
	}

	@Override
	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setMapeamentoFormulario(null);
		fechar();
	}

	@Override
	public void executarAoAbrirFormulario() {
		container.formularioVisivel();
	}
}