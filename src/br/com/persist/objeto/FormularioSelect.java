package br.com.persist.objeto;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import br.com.persist.banco.Conexao;
import br.com.persist.formulario.Formulario;
import br.com.persist.util.BuscaAuto.Grupo;

public class FormularioSelect extends FormularioAbstrato implements PainelObjetoListener {
	private static final long serialVersionUID = 1L;
	private final PainelObjetoListener listener;
	private final PainelSelect painelSelect;
	private final Formulario formulario;

	public FormularioSelect(String titulo, PainelObjetoListener listener, Conexao padrao, String instrucao,
			Map<String, String> mapaChaveValor) {
		super(titulo);
		this.listener = listener;
		painelSelect = new PainelSelect(this, padrao, instrucao, mapaChaveValor);
		this.formulario = null;
		montarLayout();
	}

	public FormularioSelect(String titulo, Formulario formulario, Conexao padrao, String instrucao,
			Map<String, String> mapaChaveValor) {
		super(titulo);
		this.formulario = formulario;
		painelSelect = new PainelSelect(this, padrao, instrucao, mapaChaveValor);
		this.listener = null;
		montarLayout();
	}

	private void montarLayout() {
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, painelSelect);
	}

	@Override
	public void buscaAutomatica(Grupo grupo, String argumentos, AtomicBoolean processado) {
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