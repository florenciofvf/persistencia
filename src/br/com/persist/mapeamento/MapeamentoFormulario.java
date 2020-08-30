package br.com.persist.mapeamento;

import java.awt.BorderLayout;

import br.com.persist.formulario.AbstratoFormulario;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.Mensagens;

public class MapeamentoFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final MapeamentoContainer container;

	private MapeamentoFormulario(Formulario formulario) {
		super(Mensagens.getString(Constantes.LABEL_MAPEAMENTOS));
		container = new MapeamentoContainer(this, formulario);
		container.setMapeamentoFormulario(this);
		montarLayout();
	}

	private MapeamentoFormulario(MapeamentoContainer container) {
		super(Mensagens.getString(Constantes.LABEL_MAPEAMENTOS));
		container.setMapeamentoFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	@Override
	public void executarAoAbrirForm() {
		container.ini(getGraphics());
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
		fechar();
	}
}