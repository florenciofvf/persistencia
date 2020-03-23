package br.com.persist.formulario;

import java.awt.BorderLayout;

import br.com.persist.container.ArvoreContainer;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class ArvoreFormulario extends AbstratoFormulario implements IJanela {
	private static final long serialVersionUID = 1L;
	private final ArvoreContainer container;

	public ArvoreFormulario(Formulario formulario) {
		super(Mensagens.getString(Constantes.LABEL_ARQUIVOS));
		container = new ArvoreContainer(this, formulario);
		container.setArvoreFormulario(this);
		montarLayout();
	}

	public ArvoreFormulario(ArvoreContainer container) {
		super(Mensagens.getString(Constantes.LABEL_ARQUIVOS));
		container.setArvoreFormulario(this);
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

	public static void criar(Formulario formulario, ArvoreContainer container) {
		ArvoreFormulario form = new ArvoreFormulario(container);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	public static void criar(Formulario formulario) {
		ArvoreFormulario form = new ArvoreFormulario(formulario);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	public void retornoAoFichario() {
		remove(container);
		container.setJanela(null);
		container.setArvoreFormulario(null);
		Formulario formulario = container.getFormulario();
		formulario.getFichario().getArvore().retornoAoFichario(formulario, container);
		dispose();
	}
}