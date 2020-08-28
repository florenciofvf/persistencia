package br.com.persist.conexao;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import br.com.persist.formulario.AbstratoFormulario;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class ConexaoFormulario extends AbstratoFormulario implements IJanela {
	private static final long serialVersionUID = 1L;
	private final ConexaoContainer container;

	private ConexaoFormulario(ConexaoContainer container) {
		super(Mensagens.getString(Constantes.LABEL_CONEXAO));
		container.setConexaoFormulario(this);
		this.container = container;
		container.setJanela(this);
		montarLayout();
		configurar();
	}

	private ConexaoFormulario(Formulario formulario) {
		super(Mensagens.getString(Constantes.LABEL_CONEXAO));
		container = new ConexaoContainer(this, formulario);
		container.setConexaoFormulario(this);
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
		dispose();
	}
}