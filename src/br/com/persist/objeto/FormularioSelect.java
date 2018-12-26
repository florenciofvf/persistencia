package br.com.persist.objeto;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.Vector;

import javax.swing.JFrame;

import br.com.persist.banco.Conexao;
import br.com.persist.util.BuscaAuto.Grupo;

public class FormularioSelect extends JFrame implements PainelObjetoListener {
	private static final long serialVersionUID = 1L;
	private final PainelSelect painelSelect;
	private PainelObjetoListener listener;

	public FormularioSelect(String titulo, PainelObjetoListener listener, Conexao padrao) {
		super(titulo);
		this.listener = listener;
		painelSelect = new PainelSelect(this, padrao);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(1000, 400);
		montarLayout();
	}

	private void montarLayout() {
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, painelSelect);
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