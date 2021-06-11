package br.com.persist.plugins.variaveis;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Util;
import br.com.persist.formulario.Formulario;

public class VariavelDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final VariavelContainer container;

	private VariavelDialogo(Frame frame, Formulario formulario) {
		super(frame, Mensagens.getString(Constantes.LABEL_VARIAVEIS));
		container = new VariavelContainer(this, formulario);
		container.setVariavelDialogo(this);
		montarLayout();
	}

	private VariavelDialogo(Dialog dialog, Formulario formulario) {
		super(dialog, Mensagens.getString(Constantes.LABEL_VARIAVEIS));
		container = new VariavelContainer(this, formulario);
		container.setVariavelDialogo(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario) {
		VariavelDialogo form = new VariavelDialogo(formulario, formulario);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
		Util.configSizeLocation(formulario, form, null);
	}

	@Override
	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setVariavelDialogo(null);
		fechar();
	}

	@Override
	public void executarAoAbrirDialogo() {
		container.dialogoVisivel();
	}

	public static VariavelDialogo criar(Dialog dialog, Formulario formulario) {
		return new VariavelDialogo(dialog, formulario);
	}

	public static VariavelDialogo criar(Frame frame, Formulario formulario) {
		return new VariavelDialogo(frame, formulario);
	}
}