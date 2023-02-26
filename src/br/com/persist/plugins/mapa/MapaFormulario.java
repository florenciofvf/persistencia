package br.com.persist.plugins.mapa;

import java.awt.BorderLayout;
import java.awt.Window;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.formulario.Formulario;

public class MapaFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final MapaContainer container;

	private MapaFormulario(Formulario formulario, String conteudo, String idPagina) {
		super(MapaMensagens.getString(MapaConstantes.LABEL_MAPA));
		container = new MapaContainer(this, formulario, conteudo, idPagina);
		container.setMapaFormulario(this);
		montarLayout();
	}

	private MapaFormulario(MapaContainer container) {
		super(MapaMensagens.getString(MapaConstantes.LABEL_MAPA));
		container.setMapaFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, MapaContainer container) {
		MapaFormulario form = new MapaFormulario(container);
		Formulario.posicionarJanela(formulario, form);
	}

	public static void criar(Formulario formulario, String conteudo, String idPagina) {
		MapaFormulario form = new MapaFormulario(formulario, conteudo, idPagina);
		Formulario.posicionarJanela(formulario, form);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setMapaFormulario(null);
		fechar();
	}

	@Override
	public void windowOpenedHandler(Window window) {
		container.windowOpenedHandler(window);
	}
}