package br.com.persist.objeto;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.util.Vector;

import javax.swing.JInternalFrame;

import br.com.persist.Objeto;
import br.com.persist.banco.Conexao;
import br.com.persist.formulario.Desktop;
import br.com.persist.formulario.Formulario;
import br.com.persist.util.BuscaAuto.Grupo;

public class FormularioInterno extends JInternalFrame implements PainelObjetoListener {
	private static final long serialVersionUID = 1L;
	private final PainelObjeto painelObjeto;
	private final Formulario formulario;

	public FormularioInterno(Formulario formulario, Objeto objeto, Graphics g, Conexao padrao) {
		super(objeto.getId(), true, true, true, true);
		this.formulario = formulario;
		painelObjeto = new PainelObjeto(this, objeto, g, padrao);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(1000, 600);
		montarLayout();
		setVisible(true);
	}

	public boolean ehTabela(String nome) {
		return painelObjeto.getObjeto().getTabela2().equalsIgnoreCase(nome);
	}

	public PainelObjeto getPainelObjeto() {
		return painelObjeto;
	}

	public void buscaAutomatica(String campo, String argumentos) {
		painelObjeto.buscaAutomatica(campo, argumentos);
	}

	private void montarLayout() {
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, painelObjeto);
	}

	@Override
	public void buscaAutomatica(Grupo grupo, String argumentos) {
		Container parent = getParent();
		Desktop desktop = null;

		while (parent != null) {
			if (parent instanceof Desktop) {
				desktop = (Desktop) parent;
				break;
			}

			parent = getParent();
		}

		if (desktop != null) {
			desktop.buscaAutomatica(grupo, argumentos, painelObjeto);
		}
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
		return null;
	}
}