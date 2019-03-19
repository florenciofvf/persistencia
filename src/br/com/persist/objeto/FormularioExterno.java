package br.com.persist.objeto;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import br.com.persist.Objeto;
import br.com.persist.banco.Conexao;
import br.com.persist.formulario.Formulario;
import br.com.persist.util.BuscaAuto.Grupo;

public class FormularioExterno extends JFrame implements PainelObjetoListener {
	private static final long serialVersionUID = 1L;
	private final PainelObjeto painelObjeto;
	private final Formulario formulario;

	public FormularioExterno(Formulario formulario, Frame frame, Objeto objeto, Graphics g, Conexao padrao,
			boolean buscaAuto) {
		this.formulario = formulario;
		painelObjeto = new PainelObjeto(this, objeto, g, padrao, buscaAuto);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(1000, 600);
		setActionESC();
		montarLayout();
		setLocationRelativeTo(frame);
		setVisible(true);
	}

	private void setActionESC() {
		JComponent component = (JComponent) getContentPane();

		InputMap inputMap = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "esc");

		Action action = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		};

		ActionMap actionMap = component.getActionMap();
		actionMap.put("esc", action);
	}

	private void montarLayout() {
		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, painelObjeto);
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