package br.com.persist.formulario;

import java.awt.BorderLayout;

import br.com.persist.arvore.ContainerArvore;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Mensagens;

public class ArvoreFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final ContainerArvore container;

	public ArvoreFormulario(Formulario formulario) {
		super(Mensagens.getString("label.arquivos"));
		container = new ContainerArvore(formulario);
		setLocationRelativeTo(formulario);
		montarLayout();
		setVisible(true);
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}
}