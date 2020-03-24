package br.com.persist.formulario;

import java.awt.BorderLayout;

import br.com.persist.container.ComparacaoContainer;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class ComparacaoFormulario extends AbstratoFormulario implements IJanela {
	private static final long serialVersionUID = 1L;
	private final ComparacaoContainer container;

	public ComparacaoFormulario(Formulario formulario) {
		super(Mensagens.getString(Constantes.LABEL_COMPARACAO));
		container = new ComparacaoContainer(this, formulario);
		container.setComparacaoFormulario(this);
		montarLayout();
	}

	public ComparacaoFormulario(ComparacaoContainer container) {
		super(Mensagens.getString(Constantes.LABEL_COMPARACAO));
		container.setComparacaoFormulario(this);
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
		dispose();
	}
}