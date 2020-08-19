package br.com.persist.objeto;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import br.com.persist.conexao.Conexao;
import br.com.persist.conexao.ConexaoProvedor;
import br.com.persist.formulario.AbstratoFormulario;
import br.com.persist.util.IJanela;

public class ObjetoContainerFormulario extends AbstratoFormulario implements IJanela {
	private static final long serialVersionUID = 1L;
	private final ObjetoContainer container;

	private ObjetoContainerFormulario(ConexaoProvedor provedor, Conexao padrao, Objeto objeto, Graphics g) {
		super(objeto.getId());
		container = new ObjetoContainer(this, provedor, padrao, objeto, g, false);
		container.setComponenteListener(ObjetoContainerFormulario.this::getThis);
		container.setDimensaoListener(ObjetoContainerFormulario.this::getSize);
		container.setTituloListener(ObjetoContainerFormulario.this::setTitle);
		montarLayout();
		configurar();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public Component getThis() {
		return this;
	}

	@Override
	public void fechar() {
		dispose();
	}

	public static ObjetoContainerFormulario criar(ConexaoProvedor provedor, Conexao padrao, Objeto objeto, Graphics g) {
		return new ObjetoContainerFormulario(provedor, padrao, objeto, g);
	}

	private void configurar() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				container.ini(getGraphics());
			}
		});
	}
}