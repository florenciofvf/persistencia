package br.com.persist.plugins.fragmento;

import java.awt.BorderLayout;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.formulario.Formulario;

public class FragmentoFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final FragmentoContainer container;

	private FragmentoFormulario(Formulario formulario) {
		super(Mensagens.getString(Constantes.LABEL_FRAGMENTO));
		container = new FragmentoContainer(this, formulario, null);
		container.setFragmentoFormulario(this);
		montarLayout();
	}

	private FragmentoFormulario(FragmentoContainer container) {
		super(Mensagens.getString(Constantes.LABEL_FRAGMENTO));
		container.setFragmentoFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, FragmentoContainer container) {
		FragmentoFormulario form = new FragmentoFormulario(container);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
		Formulario.posicionarJanela(formulario, form);
	}

	public static void criar(Formulario formulario) {
		FragmentoFormulario form = new FragmentoFormulario(formulario);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
		Formulario.posicionarJanela(formulario, form);
	}

	@Override
	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setFragmentoFormulario(null);
		fechar();
	}

	@Override
	public void executarAoAbrirFormulario() {
		container.formularioVisivel();
	}
}