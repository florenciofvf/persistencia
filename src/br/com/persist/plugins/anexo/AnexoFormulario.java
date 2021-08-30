package br.com.persist.plugins.anexo;

import java.awt.BorderLayout;
import java.awt.Window;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.formulario.Formulario;

public class AnexoFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final AnexoContainer container;

	private AnexoFormulario(Formulario formulario) {
		super(Mensagens.getString(Constantes.LABEL_ANEXOS));
		container = new AnexoContainer(this, formulario);
		container.setAnexoFormulario(this);
		montarLayout();
	}

	private AnexoFormulario(AnexoContainer container) {
		super(Mensagens.getString(Constantes.LABEL_ANEXOS));
		container.setAnexoFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, AnexoContainer container) {
		AnexoFormulario form = new AnexoFormulario(container);
		Formulario.posicionarJanela(formulario, form);
	}

	public static void criar(Formulario formulario) {
		AnexoFormulario form = new AnexoFormulario(formulario);
		Formulario.posicionarJanela(formulario, form);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setAnexoFormulario(null);
		fechar();
	}

	@Override
	public void windowOpenedHandler(Window window) {
		container.windowOpenedHandler(this);
	}
}