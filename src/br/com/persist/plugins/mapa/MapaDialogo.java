package br.com.persist.plugins.mapa;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Util;
import br.com.persist.formulario.Formulario;

public class MapaDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final MapaContainer container;

	private MapaDialogo(Frame frame, Formulario formulario) {
		super(frame, MapaMensagens.getString(MapaConstantes.LABEL_MAPA));
		container = new MapaContainer(this, formulario, null, null);
		container.setEntregaDialogo(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario) {
		MapaDialogo form = new MapaDialogo(formulario, formulario);
		Util.configSizeLocation(formulario, form, null);
		form.setVisible(true);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setEntregaDialogo(null);
		fechar();
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
		container.dialogOpenedHandler(dialog);
	}
}