package br.com.persist.formulario;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.concurrent.atomic.AtomicBoolean;

import br.com.persist.Objeto;
import br.com.persist.banco.Conexao;
import br.com.persist.banco.ConexaoProvedor;
import br.com.persist.container.ObjetoContainer;
import br.com.persist.listener.ObjetoContainerListener;
import br.com.persist.util.BuscaAuto.Grupo;
import br.com.persist.util.IJanela;

public class ObjetoContainerFormulario extends AbstratoFormulario implements IJanela, ObjetoContainerListener {
	private static final long serialVersionUID = 1L;
	private final ObjetoContainer container;

	public ObjetoContainerFormulario(ConexaoProvedor provedor, Conexao padrao, Objeto objeto, Graphics g) {
		super(objeto.getId());
		container = new ObjetoContainer(this, provedor, padrao, objeto, this, g, false);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	@Override
	public void fechar() {
		dispose();
	}

	@Override
	public void buscaAutomatica(Grupo grupo, String argumentos, AtomicBoolean processado) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Dimension getDimensoes() {
		return getSize();
	}

	@Override
	public void setTitulo(String titulo) {
		setTitle(titulo);
	}
}