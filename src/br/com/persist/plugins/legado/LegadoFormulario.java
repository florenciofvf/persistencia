package br.com.persist.plugins.legado;

import java.awt.BorderLayout;
import java.awt.Window;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.formulario.Formulario;

public class LegadoFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final LegadoContainer container;

	private LegadoFormulario(Formulario formulario, String conteudo, String idPagina) {
		super(formulario, LegadoMensagens.getString(LegadoConstantes.LABEL_LEGADO));
		container = new LegadoContainer(this, formulario, conteudo, idPagina);
		container.setLegadoFormulario(this);
		montarLayout();
	}

	private LegadoFormulario(LegadoContainer container) {
		super(container.getFormulario(), LegadoMensagens.getString(LegadoConstantes.LABEL_LEGADO));
		container.setLegadoFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, LegadoContainer container) {
		LegadoFormulario form = new LegadoFormulario(container);
		Formulario.posicionarJanela(formulario, form);
	}

	public static void criar(Formulario formulario, String conteudo, String idPagina) {
		LegadoFormulario form = new LegadoFormulario(formulario, conteudo, idPagina);
		Formulario.posicionarJanela(formulario, form);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setLegadoFormulario(null);
		fechar();
	}

	@Override
	public void windowOpenedHandler(Window window) {
		container.windowOpenedHandler(window);
	}
}