package br.com.persist.formulario;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import br.com.persist.banco.Conexao;
import br.com.persist.listener.PainelObjetoListener;
import br.com.persist.painel.PainelUpdate;
import br.com.persist.util.BuscaAuto.Grupo;

public class UpdateFormulario extends AbstratoFormulario implements PainelObjetoListener {
	private static final long serialVersionUID = 1L;
	private final PainelUpdate painelUpdate;
	private PainelObjetoListener listener;

	public UpdateFormulario(String titulo, PainelObjetoListener listener, Conexao padrao, String instrucao,
			Map<String, String> mapaChaveValor) {
		super(titulo);
		this.listener = listener;
		painelUpdate = new PainelUpdate(this, padrao, instrucao, mapaChaveValor);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, painelUpdate);
	}

	@Override
	public void buscaAutomatica(Grupo grupo, String argumentos, AtomicBoolean processado) {
	}

	@Override
	public Vector<Conexao> getConexoes() {
		return listener.getConexoes();
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