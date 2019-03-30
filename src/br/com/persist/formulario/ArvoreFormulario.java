package br.com.persist.formulario;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import br.com.persist.arvore.ArvoreContainer;
import br.com.persist.formulario.AbstratoFormulario;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Mensagens;

public class ArvoreFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final ArvoreContainer container;

	public ArvoreFormulario(Formulario formulario) {
		super(Mensagens.getString("label.arquivos"));
		container = new ArvoreContainer(formulario);
		setLocationRelativeTo(formulario);
		montarLayout();
		configurar();
		setVisible(true);
	}

	private void configurar() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
			}
		});
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}
}