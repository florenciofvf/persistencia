package br.com.persist.fragmento;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import br.com.persist.formulario.AbstratoFormulario;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class FragmentoFormulario extends AbstratoFormulario implements IJanela {
	private static final long serialVersionUID = 1L;
	private final FragmentoContainer container;

	private FragmentoFormulario(Formulario formulario) {
		super(Mensagens.getString(Constantes.LABEL_FRAGMENTO));
		container = new FragmentoContainer(this, formulario, null);
		container.setFragmentoFormulario(this);
		montarLayout();
		configurar();
	}

	private FragmentoFormulario(FragmentoContainer container) {
		super(Mensagens.getString(Constantes.LABEL_FRAGMENTO));
		container.setFragmentoFormulario(this);
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

	public static void criar(Formulario formulario, FragmentoContainer container) {
		FragmentoFormulario form = new FragmentoFormulario(container);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	public static void criar(Formulario formulario) {
		FragmentoFormulario form = new FragmentoFormulario(formulario);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	public void retornoAoFichario() {
		Formulario formulario = container.getFormulario();

		if (formulario != null) {
			remove(container);
			container.setJanela(null);
			container.setFragmentoFormulario(null);
			formulario.getFichario().getFragmento().retornoAoFichario(formulario, container);
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