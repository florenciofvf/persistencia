package br.com.persist.mapeamento;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import br.com.persist.formulario.AbstratoFormulario;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class MapeamentoFormulario extends AbstratoFormulario implements IJanela {
	private static final long serialVersionUID = 1L;
	private final MapeamentoContainer container;

	private MapeamentoFormulario(Formulario formulario) {
		super(Mensagens.getString(Constantes.LABEL_MAPEAMENTOS));
		container = new MapeamentoContainer(this, formulario);
		container.setMapeamentoFormulario(this);
		montarLayout();
		configurar();
	}

	private MapeamentoFormulario(MapeamentoContainer container) {
		super(Mensagens.getString(Constantes.LABEL_MAPEAMENTOS));
		container.setMapeamentoFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
		configurar();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	private void configurar() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				container.ini(getGraphics());
			}
		});
	}

	@Override
	public void fechar() {
		dispose();
	}

	public static void criar(Formulario formulario, MapeamentoContainer container) {
		MapeamentoFormulario form = new MapeamentoFormulario(container);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	public static void criar(Formulario formulario) {
		MapeamentoFormulario form = new MapeamentoFormulario(formulario);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	public void retornoAoFichario() {
		remove(container);
		container.setJanela(null);
		container.setMapeamentoFormulario(null);
		Formulario formulario = container.getFormulario();
		formulario.getFichario().getMapeamento().retornoAoFichario(formulario, container);
		dispose();
	}
}