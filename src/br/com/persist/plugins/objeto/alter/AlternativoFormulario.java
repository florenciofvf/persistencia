package br.com.persist.plugins.objeto.alter;

import java.awt.BorderLayout;
import java.awt.Window;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.formulario.Formulario;

public class AlternativoFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final AlternativoContainer container;

	private AlternativoFormulario(Formulario formulario) {
		super(Mensagens.getString(Constantes.LABEL_ALTERNATIVO));
		container = new AlternativoContainer(this, formulario, null);
		container.setAlternativoFormulario(this);
		montarLayout();
	}

	private AlternativoFormulario(AlternativoContainer container) {
		super(Mensagens.getString(Constantes.LABEL_ALTERNATIVO));
		container.setAlternativoFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, AlternativoContainer container) {
		AlternativoFormulario form = new AlternativoFormulario(container);
		Formulario.posicionarJanela(formulario, form);
	}

	public static void criar(Formulario formulario) {
		AlternativoFormulario form = new AlternativoFormulario(formulario);
		Formulario.posicionarJanela(formulario, form);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setAlternativoFormulario(null);
		fechar();
	}

	@Override
	public void windowOpenedHandler(Window window) {
		container.windowOpenedHandler(window);
	}
}