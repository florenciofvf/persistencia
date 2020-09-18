package br.com.persist.plugins.metadado;

import java.awt.BorderLayout;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.formulario.Formulario;
import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.util.Constantes;
import br.com.persist.util.Mensagens;

public class MetadadoFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final MetadadoContainer container;

	private MetadadoFormulario(Formulario formulario, Conexao conexao) {
		super(Mensagens.getString(Constantes.LABEL_METADADOS));
		container = new MetadadoContainer(this, formulario, conexao);
		container.setMetadadoFormulario(this);
		montarLayout();
	}

	private MetadadoFormulario(MetadadoContainer container) {
		super(Mensagens.getString(Constantes.LABEL_METADADOS));
		container.setMetadadoFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, MetadadoContainer container) {
		MetadadoFormulario form = new MetadadoFormulario(container);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	public static void criar(Formulario formulario, Conexao conexao) {
		MetadadoFormulario form = new MetadadoFormulario(formulario, conexao);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	@Override
	public void excluirContainer() {
		remove(container);
		container.setJanela(null);
		container.setMetadadoFormulario(null);
		fechar();
	}

	@Override
	public void executarAoAbrirFormulario() {
		container.formularioVisivel();
	}
}