package br.com.persist.formulario;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import br.com.persist.Objeto;
import br.com.persist.banco.Conexao;
import br.com.persist.painel.PainelObjeto;
import br.com.persist.painel.PainelObjetoListener;
import br.com.persist.principal.Formulario;
import br.com.persist.util.BuscaAuto.Grupo;

public class ExternoFormulario extends AbstratoFormulario implements PainelObjetoListener {
	private static final long serialVersionUID = 1L;
	private final PainelObjeto painelObjeto;
	private final Formulario formulario;

	public ExternoFormulario(Formulario formulario, Frame frame, Objeto objeto, Graphics g, Conexao padrao,
			boolean buscaAuto) {
		super(objeto.getId());
		this.formulario = formulario;
		painelObjeto = new PainelObjeto(this, objeto, g, padrao, buscaAuto);
		setLocationRelativeTo(frame);
		montarLayout();
		setVisible(true);
	}

	private void montarLayout() {
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