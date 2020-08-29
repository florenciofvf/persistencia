package br.com.persist.propriedades;

import java.awt.BorderLayout;

import br.com.persist.formulario.AbstratoFormulario;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Mensagens;

public class PropriedadesFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final PropriedadesContainer container;

	private PropriedadesFormulario(Formulario formulario, String conteudo) {
		super(Mensagens.getString("label.propriedades"));
		container = new PropriedadesContainer(this, formulario, conteudo);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Formulario formulario, String conteudo) {
		PropriedadesFormulario form = new PropriedadesFormulario(formulario, conteudo);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}
}