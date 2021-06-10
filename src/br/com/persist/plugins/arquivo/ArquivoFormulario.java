package br.com.persist.plugins.arquivo;

import java.awt.BorderLayout;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.formulario.Formulario;

public class ArquivoFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final ArquivoContainer container;

	private ArquivoFormulario(Formulario formulario) {
		super(Mensagens.getString(Constantes.LABEL_ARQUIVOS));
		container = new ArquivoContainer(this, formulario);
		container.setArquivoFormulario(this);
		montarLayout();
	}

	private ArquivoFormulario(ArquivoContainer container) {
		super(Mensagens.getString(Constantes.LABEL_ARQUIVOS));
		container.setArquivoFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, ArquivoContainer container) {
		ArquivoFormulario form = new ArquivoFormulario(container);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
		Formulario.posicionarJanela(formulario, form);
	}

	public static void criar(Formulario formulario) {
		ArquivoFormulario form = new ArquivoFormulario(formulario);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
		Formulario.posicionarJanela(formulario, form);
	}

	@Override
	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setArquivoFormulario(null);
		fechar();
	}

	@Override
	public void executarAoAbrirFormulario() {
		container.formularioVisivel();
	}
}