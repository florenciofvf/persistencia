package br.com.persist.plugins.fragmento;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.formulario.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.Mensagens;

public class FragmentoDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final FragmentoContainer container;

	private FragmentoDialogo(Frame frame, Formulario formulario, FragmentoListener listener) {
		super(frame, Mensagens.getString(Constantes.LABEL_FRAGMENTO));
		container = new FragmentoContainer(this, formulario, listener);
		container.setFragmentoDialogo(this);
		montarLayout();
	}

	private FragmentoDialogo(Dialog dialog, Formulario formulario, FragmentoListener listener) {
		super(dialog, Mensagens.getString(Constantes.LABEL_FRAGMENTO));
		container = new FragmentoContainer(this, formulario, listener);
		container.setFragmentoDialogo(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario) {
		FragmentoDialogo form = criar(formulario, formulario, null);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	@Override
	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setFragmentoDialogo(null);
		fechar();
	}

	@Override
	public void executarAoAbrirDialogo() {
		container.dialogoVisivel();
	}

	public static FragmentoDialogo criar(Dialog dialog, Formulario formulario, FragmentoListener listener) {
		return new FragmentoDialogo(dialog, formulario, listener);
	}

	public static FragmentoDialogo criar(Frame frame, Formulario formulario, FragmentoListener listener) {
		return new FragmentoDialogo(frame, formulario, listener);
	}
}