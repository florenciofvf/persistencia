package br.com.persist.formulario;

import java.awt.BorderLayout;

import br.com.persist.container.PropriedadesContainer;
import br.com.persist.principal.Formulario;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class PropriedadesFormulario extends AbstratoFormulario implements IJanela {
	private static final long serialVersionUID = 1L;
	private final PropriedadesContainer container;

	public PropriedadesFormulario(Formulario formulario, String conteudo) {
		super(Mensagens.getString("label.propriedades"));
		container = new PropriedadesContainer(this, formulario, conteudo);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	@Override
	public void fechar() {
		dispose();
	}

	public static void criar(Formulario formulario, String conteudo) {
		PropriedadesFormulario form = new PropriedadesFormulario(formulario, conteudo);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}
}