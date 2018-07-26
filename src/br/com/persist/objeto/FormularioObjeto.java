package br.com.persist.objeto;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.util.Vector;

import javax.swing.JFrame;

import br.com.persist.Objeto;
import br.com.persist.banco.Conexao;
import br.com.persist.formulario.Formulario;

public class FormularioObjeto extends JFrame implements PainelObjetoListener {
	private static final long serialVersionUID = 1L;
	private final PainelObjeto painelObjeto;
	private final Formulario formulario;

	public FormularioObjeto(Formulario formulario, Objeto objeto, Graphics g, Conexao padrao) {
		this.formulario = formulario;
		painelObjeto = new PainelObjeto(this, objeto, g, padrao);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle(objeto.getId());
		setSize(800, 600);
		montarLayout();
		setLocationRelativeTo(formulario);
		setVisible(true);
	}

	private void montarLayout() {
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, painelObjeto);
	}

	@Override
	public Vector<Conexao> getConexoes() {
		return formulario.getConexoes();
	}

	@Override
	public Frame getFrame() {
		return this;
	}
}