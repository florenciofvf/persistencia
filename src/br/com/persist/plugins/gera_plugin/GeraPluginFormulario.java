package br.com.persist.plugins.gera_plugin;

import java.awt.BorderLayout;
import java.awt.Window;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.formulario.Formulario;

public class GeraPluginFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final GeraPluginContainer container;

	private GeraPluginFormulario(Formulario formulario) {
		super(GeraPluginMensagens.getString(GeraPluginConstantes.LABEL_GERA_PLUGIN));
		container = new GeraPluginContainer(this, formulario);
		container.setGeraPluginFormulario(this);
		montarLayout();
	}

	private GeraPluginFormulario(GeraPluginContainer container) {
		super(GeraPluginMensagens.getString(GeraPluginConstantes.LABEL_GERA_PLUGIN));
		container.setGeraPluginFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, GeraPluginContainer container) {
		GeraPluginFormulario form = new GeraPluginFormulario(container);
		Formulario.posicionarJanela(formulario, form);
	}

	public static void criar(Formulario formulario) {
		GeraPluginFormulario form = new GeraPluginFormulario(formulario);
		Formulario.posicionarJanela(formulario, form);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setGeraPluginFormulario(null);
		fechar();
	}

	@Override
	public void windowOpenedHandler(Window window) {
		container.windowOpenedHandler(window);
	}
}