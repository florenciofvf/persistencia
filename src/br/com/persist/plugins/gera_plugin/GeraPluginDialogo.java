package br.com.persist.plugins.gera_plugin;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Util;
import br.com.persist.formulario.Formulario;

public class GeraPluginDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final GeraPluginContainer container;

	private GeraPluginDialogo(Frame frame, Formulario formulario) {
		super(frame, GeraPluginMensagens.getString(GeraPluginConstantes.LABEL_GERA_PLUGIN));
		container = new GeraPluginContainer(this, formulario);
		container.setGeraPluginDialogo(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario) {
		GeraPluginDialogo form = new GeraPluginDialogo(formulario, formulario);
		Util.configSizeLocation(formulario, form, null);
		form.setVisible(true);
	}

	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setGeraPluginDialogo(null);
		fechar();
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
		container.dialogOpenedHandler(dialog);
	}
}