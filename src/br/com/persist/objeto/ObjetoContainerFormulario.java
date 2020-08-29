package br.com.persist.objeto;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;

import br.com.persist.conexao.Conexao;
import br.com.persist.conexao.ConexaoProvedor;
import br.com.persist.formulario.AbstratoFormulario;

public class ObjetoContainerFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final ObjetoContainer container;

	private ObjetoContainerFormulario(ConexaoProvedor provedor, Conexao padrao, Objeto objeto, Graphics g) {
		super(objeto.getId());
		container = new ObjetoContainer(this, provedor, padrao, objeto, g, false);
		container.setComponenteListener(ObjetoContainerFormulario.this::getThis);
		container.setDimensaoListener(ObjetoContainerFormulario.this::getSize);
		container.setTituloListener(ObjetoContainerFormulario.this::setTitle);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	@Override
	public void executarAoAbrirForm() {
		container.ini(getGraphics());
	}

	public Component getThis() {
		return this;
	}

	public static ObjetoContainerFormulario criar(ConexaoProvedor provedor, Conexao padrao, Objeto objeto, Graphics g) {
		return new ObjetoContainerFormulario(provedor, padrao, objeto, g);
	}
}