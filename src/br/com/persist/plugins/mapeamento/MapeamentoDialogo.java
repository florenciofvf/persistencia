package br.com.persist.plugins.mapeamento;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Util;
import br.com.persist.formulario.Formulario;

public class MapeamentoDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final MapeamentoContainer container;

	private MapeamentoDialogo(Dialog dialog, Formulario formulario) {
		super(dialog, Mensagens.getString(Constantes.LABEL_MAPEAMENTOS));
		container = new MapeamentoContainer(this, formulario);
		container.setMapeamentoDialogo(this);
		montarLayout();
	}

	private MapeamentoDialogo(Frame frame, Formulario formulario) {
		super(frame, Mensagens.getString(Constantes.LABEL_MAPEAMENTOS));
		container = new MapeamentoContainer(this, formulario);
		container.setMapeamentoDialogo(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario) {
		MapeamentoDialogo form = criar(formulario, formulario);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
		Util.configSizeLocation(formulario, form, null);
	}

	@Override
	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setMapeamentoDialogo(null);
		fechar();
	}

	@Override
	public void executarAoAbrirDialogo() {
		container.dialogoVisivel();
	}

	public static MapeamentoDialogo criar(Dialog dialog, Formulario formulario) {
		return new MapeamentoDialogo(dialog, formulario);
	}

	public static MapeamentoDialogo criar(Frame frame, Formulario formulario) {
		return new MapeamentoDialogo(frame, formulario);
	}
}