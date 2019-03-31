package br.com.persist.formulario;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import br.com.persist.banco.Conexao;
import br.com.persist.listener.PainelObjetoListener;
import br.com.persist.painel.SelectPainel;
import br.com.persist.principal.Formulario;
import br.com.persist.util.BuscaAuto.Grupo;

public class SelectFormulario extends AbstratoFormulario implements PainelObjetoListener {
	private static final long serialVersionUID = 1L;
	private final transient PainelObjetoListener listener;
	private final SelectPainel selectPainel;
	private final Formulario formulario;

	public SelectFormulario(String titulo, PainelObjetoListener listener, Conexao padrao, String instrucao,
			Map<String, String> mapaChaveValor) {
		super(titulo);
		this.listener = listener;
		selectPainel = new SelectPainel(this, padrao, instrucao, mapaChaveValor);
		this.formulario = null;
		montarLayout();
	}

	public SelectFormulario(String titulo, Formulario formulario, Conexao padrao, String instrucao,
			Map<String, String> mapaChaveValor) {
		super(titulo);
		this.formulario = formulario;
		selectPainel = new SelectPainel(this, padrao, instrucao, mapaChaveValor);
		this.listener = null;
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, selectPainel);
	}

	@Override
	public void buscaAutomatica(Grupo grupo, String argumentos, AtomicBoolean processado) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Vector<Conexao> getConexoes() {
		return listener != null ? listener.getConexoes() : formulario.getConexoes();
	}

	@Override
	public Dimension getDimensoes() {
		return getSize();
	}

	@Override
	public Frame getFrame() {
		return this;
	}
}