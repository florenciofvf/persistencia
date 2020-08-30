package br.com.persist.objeto;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;

import br.com.persist.conexao.Conexao;
import br.com.persist.conexao.ConexaoProvedor;
import br.com.persist.formulario.AbstratoFormulario;

public class OTabelaFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final OTabelaContainer container;

	private OTabelaFormulario(ConexaoProvedor provedor, Conexao padrao, Objeto objeto, Graphics g) {
		super(objeto.getId());
		container = new OTabelaContainer(this, provedor, padrao, objeto, g, false);
		container.setComponenteListener(OTabelaFormulario.this::getThis);
		container.setDimensaoListener(OTabelaFormulario.this::getSize);
		container.setTituloListener(OTabelaFormulario.this::setTitle);
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

	public static OTabelaFormulario criar(ConexaoProvedor provedor, Conexao padrao, Objeto objeto, Graphics g) {
		return new OTabelaFormulario(provedor, padrao, objeto, g);
	}
}