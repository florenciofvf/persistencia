package br.com.persist.formulario;

import java.awt.BorderLayout;

import br.com.persist.banco.Conexao;
import br.com.persist.banco.ConexaoProvedor;
import br.com.persist.container.MetadadoTreeContainer;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class MetadadoTreeFormulario extends AbstratoFormulario implements IJanela {
	private static final long serialVersionUID = 1L;
	private final MetadadoTreeContainer container;

	public MetadadoTreeFormulario(Formulario formulario, ConexaoProvedor provedor, Conexao padrao) {
		super(Mensagens.getString(Constantes.LABEL_METADADOS));
		container = new MetadadoTreeContainer(this, formulario, provedor, padrao);
		container.setMetadadoTreeFormulario(this);
		montarLayout();
	}

	public MetadadoTreeFormulario(MetadadoTreeContainer container) {
		super(Mensagens.getString(Constantes.LABEL_METADADOS));
		container.setMetadadoTreeFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	@Override
	public void fechar() {
		dispose();
	}

	public static void criar(Formulario formulario, MetadadoTreeContainer container) {
		MetadadoTreeFormulario form = new MetadadoTreeFormulario(container);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	public static void criar(Formulario formulario, ConexaoProvedor provedor, Conexao padrao) {
		MetadadoTreeFormulario form = new MetadadoTreeFormulario(formulario, provedor, padrao);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	public void retornoAoFichario() {
		remove(container);
		container.setJanela(null);
		container.setMetadadoTreeFormulario(null);
		Formulario formulario = container.getFormulario();
		formulario.getFichario().getMetadadoTree().retornoAoFichario(formulario, container);
		dispose();
	}
}