package br.com.persist.objeto;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JFrame;

import br.com.persist.banco.Conexao;
import br.com.persist.formulario.Formulario;
import br.com.persist.util.BuscaAuto.Grupo;

public class FormularioSelect extends JFrame implements PainelObjetoListener {
	private static final long serialVersionUID = 1L;
	private final PainelSelect painelSelect;
	private final Formulario formulario;

	public FormularioSelect(Formulario formulario, Frame frame, Conexao padrao) {
		this.formulario = formulario;
		painelSelect = new PainelSelect(this, padrao);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(1000, 600);
		montarLayout();
		setLocationRelativeTo(frame);
		setVisible(true);
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
		return formulario.getConexoes();
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