package br.com.persist.arquivo;

import java.awt.BorderLayout;

import br.com.persist.formulario.AbstratoFormulario;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.Mensagens;

public class ArquivoTreeFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final ArquivoTreeContainer container;

	private ArquivoTreeFormulario(Formulario formulario) {
		super(Mensagens.getString(Constantes.LABEL_ARQUIVOS));
		container = new ArquivoTreeContainer(this, formulario);
		container.setArquivoTreeFormulario(this);
		montarLayout();
	}

	private ArquivoTreeFormulario(ArquivoTreeContainer container) {
		super(Mensagens.getString(Constantes.LABEL_ARQUIVOS));
		container.setArquivoTreeFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, ArquivoTreeContainer container) {
		ArquivoTreeFormulario form = new ArquivoTreeFormulario(container);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	public static void criar(Formulario formulario) {
		ArquivoTreeFormulario form = new ArquivoTreeFormulario(formulario);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	public void retornoAoFichario() {
		remove(container);
		container.setJanela(null);
		container.setArquivoTreeFormulario(null);
		fechar();
	}
}