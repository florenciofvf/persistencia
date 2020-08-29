package br.com.persist.comparacao;

import java.awt.BorderLayout;

import br.com.persist.formulario.AbstratoFormulario;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.Mensagens;

public class ComparacaoFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final ComparacaoContainer container;

	private ComparacaoFormulario(Formulario formulario) {
		super(Mensagens.getString(Constantes.LABEL_COMPARACAO));
		container = new ComparacaoContainer(this, formulario);
		container.setComparacaoFormulario(this);
		montarLayout();
	}

	private ComparacaoFormulario(ComparacaoContainer container) {
		super(Mensagens.getString(Constantes.LABEL_COMPARACAO));
		container.setComparacaoFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, ComparacaoContainer container) {
		ComparacaoFormulario form = new ComparacaoFormulario(container);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	public static void criar(Formulario formulario) {
		ComparacaoFormulario form = new ComparacaoFormulario(formulario);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	public void retornoAoFichario() {
		remove(container);
		container.setJanela(null);
		container.setComparacaoFormulario(null);
		Formulario formulario = container.getFormulario();
		formulario.getFichario().getComparacao().retornoAoFichario(formulario, container);
		fechar();
	}
}