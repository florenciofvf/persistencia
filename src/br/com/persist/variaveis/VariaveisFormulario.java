package br.com.persist.variaveis;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import br.com.persist.formulario.AbstratoFormulario;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class VariaveisFormulario extends AbstratoFormulario implements IJanela {
	private static final long serialVersionUID = 1L;
	private final VariaveisContainer container;

	public VariaveisFormulario(Formulario formulario) {
		super(Mensagens.getString(Constantes.LABEL_VARIAVEIS));
		container = new VariaveisContainer(this, formulario);
		container.setVariaveisFormulario(this);
		montarLayout();
		configurar();
	}

	public VariaveisFormulario(VariaveisContainer container) {
		super(Mensagens.getString(Constantes.LABEL_VARIAVEIS));
		container.setVariaveisFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
		configurar();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	@Override
	public void fechar() {
		dispose();
	}

	public static void criar(Formulario formulario, VariaveisContainer container) {
		VariaveisFormulario form = new VariaveisFormulario(container);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	public static void criar(Formulario formulario) {
		VariaveisFormulario form = new VariaveisFormulario(formulario);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	public void retornoAoFichario() {
		Formulario formulario = container.getFormulario();

		if (formulario != null) {
			remove(container);
			container.setJanela(null);
			container.setVariaveisFormulario(null);
			formulario.getFichario().getVariaveis().retornoAoFichario(formulario, container);
			dispose();
		}
	}

	private void configurar() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				container.ini(getGraphics());
			}
		});
	}
}