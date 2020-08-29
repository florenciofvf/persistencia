package br.com.persist.conexao;

import java.awt.BorderLayout;

import br.com.persist.formulario.AbstratoFormulario;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.Mensagens;

public class ConexaoFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final ConexaoContainer container;

	private ConexaoFormulario(ConexaoContainer container) {
		super(Mensagens.getString(Constantes.LABEL_CONEXAO));
		container.setConexaoFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
	}

	private ConexaoFormulario(Formulario formulario) {
		super(Mensagens.getString(Constantes.LABEL_CONEXAO));
		container = new ConexaoContainer(this, formulario);
		container.setConexaoFormulario(this);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	@Override
	public void executarAoAbrirForm() {
		container.ini(getGraphics());
	}

	public static void criar(Formulario formulario, ConexaoContainer container) {
		ConexaoFormulario form = new ConexaoFormulario(container);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	public static void criar(Formulario formulario) {
		ConexaoFormulario form = new ConexaoFormulario(formulario);
		form.setLocationRelativeTo(formulario);
		form.setVisible(true);
	}

	public void retornoAoFichario() {
		remove(container);
		container.setJanela(null);
		container.setConexaoFormulario(null);
		Formulario formulario = container.getFormulario();
		formulario.getFichario().getConexoes().retornoAoFichario(formulario, container);
		fechar();
	}
}