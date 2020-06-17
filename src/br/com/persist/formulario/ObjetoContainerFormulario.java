package br.com.persist.formulario;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.persist.banco.Conexao;
import br.com.persist.banco.ConexaoProvedor;
import br.com.persist.busca_apos.GrupoBuscaAutoApos;
import br.com.persist.busca_auto.GrupoBuscaAuto;
import br.com.persist.container.ObjetoContainer;
import br.com.persist.desktop.Objeto;
import br.com.persist.link_auto.GrupoLinkAuto;
import br.com.persist.listener.ObjetoContainerListener;
import br.com.persist.util.IJanela;

public class ObjetoContainerFormulario extends AbstratoFormulario implements IJanela, ObjetoContainerListener {
	private static final Logger LOG = Logger.getGlobal();
	private static final long serialVersionUID = 1L;
	private final ObjetoContainer container;

	public ObjetoContainerFormulario(ConexaoProvedor provedor, Conexao padrao, Objeto objeto, Graphics g) {
		super(objeto.getId());
		container = new ObjetoContainer(this, provedor, padrao, objeto, this, g, false);
		montarLayout();
		configurar();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	@Override
	public void fechar() {
		dispose();
	}

	private void configurar() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				container.ini(getGraphics());
			}
		});
	}

	@Override
	public void buscaAutomatica(GrupoBuscaAuto grupo, String argumentos) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void linkAutomatico(GrupoLinkAuto link, String argumento) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void buscaAutomaticaApos(GrupoBuscaAutoApos grupoApos) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void configAlturaAutomatica(int total) {
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

	@Override
	public void selecionar(boolean b) {
		LOG.log(Level.FINEST, "{0}", b);
	}
}