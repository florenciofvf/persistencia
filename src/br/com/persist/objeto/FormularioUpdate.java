package br.com.persist.objeto;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.Map;
import java.util.Vector;

import javax.swing.JFrame;

import br.com.persist.banco.Conexao;
import br.com.persist.util.BuscaAuto.Grupo;

public class FormularioUpdate extends JFrame implements PainelObjetoListener {
	private static final long serialVersionUID = 1L;
	private final PainelUpdate painelUpdate;
	private PainelObjetoListener listener;

	public FormularioUpdate(String titulo, PainelObjetoListener listener, String instrucao, Conexao padrao,
			Map<String, String> mapaChaveValor) {
		super(titulo);
		this.listener = listener;
		painelUpdate = new PainelUpdate(this, instrucao, padrao, mapaChaveValor);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(1000, 400);
		montarLayout();
	}

	private void montarLayout() {
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, painelUpdate);
	}

	@Override
	public void buscaAutomatica(Grupo grupo, String argumentos) {
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