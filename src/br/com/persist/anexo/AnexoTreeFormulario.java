package br.com.persist.anexo;

import java.awt.BorderLayout;

import br.com.persist.container.AnexoTreeContainer;
import br.com.persist.formulario.AbstratoFormulario;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class AnexoTreeFormulario extends AbstratoFormulario implements IJanela {
	private static final long serialVersionUID = 1L;
	private final AnexoTreeContainer container;

	public AnexoTreeFormulario(Formulario formulario) {
		super(Mensagens.getString(Constantes.LABEL_ANEXOS));
		container = new AnexoTreeContainer(this, formulario);
		container.setAnexoTreeFormulario(this);
		montarLayout();
	}

	public AnexoTreeFormulario(AnexoTreeContainer container) {
		super(Mensagens.getString(Constantes.LABEL_ANEXOS));
		container.setAnexoTreeFormulario(this);
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

	public static void criar(Formulario formulario, AnexoTreeContainer container) {
		AnexoTreeFormulario form = new AnexoTreeFormulario(container);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	public static void criar(Formulario formulario) {
		AnexoTreeFormulario form = new AnexoTreeFormulario(formulario);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	public void retornoAoFichario() {
		remove(container);
		container.setJanela(null);
		container.setAnexoTreeFormulario(null);
		Formulario formulario = container.getFormulario();
		formulario.getFichario().getAnexoTree().retornoAoFichario(formulario, container);
		dispose();
	}
}