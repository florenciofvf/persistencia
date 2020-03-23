package br.com.persist.formulario;

import java.awt.BorderLayout;

import br.com.persist.container.AnotacaoContainer;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class AnotacaoFormulario extends AbstratoFormulario implements IJanela {
	private static final long serialVersionUID = 1L;
	private final AnotacaoContainer container;

	public AnotacaoFormulario(AnotacaoContainer container) {
		super(Mensagens.getString(Constantes.LABEL_ANOTACOES));
		container.setAnotacaoFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	public AnotacaoFormulario(Formulario formulario) {
		super(Mensagens.getString(Constantes.LABEL_ANOTACOES));
		container = new AnotacaoContainer(this, formulario);
		container.setAnotacaoFormulario(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	@Override
	public void fechar() {
		dispose();
	}

	public static void criar(Formulario formulario, AnotacaoContainer container) {
		AnotacaoFormulario form = new AnotacaoFormulario(container);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	public static void criar(Formulario formulario) {
		AnotacaoFormulario form = new AnotacaoFormulario(formulario);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	public void retornoAoFichario() {
		remove(container);
		container.setJanela(null);
		container.setAnotacaoFormulario(null);
		Formulario formulario = container.getFormulario();
		formulario.getFichario().retornoDestacarEmFormularioAnotacao(formulario, container);
		dispose();
	}
}