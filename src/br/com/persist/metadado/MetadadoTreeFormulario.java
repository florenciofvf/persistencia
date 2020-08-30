package br.com.persist.metadado;

import java.awt.BorderLayout;

import br.com.persist.conexao.Conexao;
import br.com.persist.conexao.ConexaoProvedor;
import br.com.persist.formulario.AbstratoFormulario;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.Mensagens;

public class MetadadoTreeFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final MetadadoTreeContainer container;

	private MetadadoTreeFormulario(Formulario formulario, ConexaoProvedor provedor, Conexao padrao) {
		super(Mensagens.getString(Constantes.LABEL_METADADOS));
		container = new MetadadoTreeContainer(this, formulario, provedor, padrao);
		container.setMetadadoTreeFormulario(this);
		montarLayout();
	}

	private MetadadoTreeFormulario(MetadadoTreeContainer container) {
		super(Mensagens.getString(Constantes.LABEL_METADADOS));
		container.setMetadadoTreeFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
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
		fechar();
	}
}