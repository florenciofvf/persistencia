package br.com.persist.plugins.objeto.internal;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.plugins.objeto.Objeto;

public class ExternalFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final InternalContainer container;

	private ExternalFormulario(Conexao padrao, Objeto objeto, Graphics g) {
		super(objeto.getId());
		container = new InternalContainer(this, padrao, objeto, g, false);
		container.setComponenteListener(ExternalFormulario.this::getThis);
		container.setDimensaoListener(ExternalFormulario.this::getSize);
		container.setTituloListener(ExternalFormulario.this::setTitle);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public Component getThis() {
		return this;
	}

	public static ExternalFormulario criar(Conexao padrao, Objeto objeto, Graphics g) {
		ExternalFormulario form = new ExternalFormulario(padrao, objeto, g);
		form.setVisible(true);
		return form;
	}

	public static ExternalFormulario criar2(Conexao padrao, Objeto objeto, Graphics g) {
		return new ExternalFormulario(padrao, objeto, g);
	}

	@Override
	public void executarAoAbrirFormulario() {
		container.formularioVisivel();
	}
}