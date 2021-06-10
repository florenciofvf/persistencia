package br.com.persist.plugins.objeto.internal;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.util.concurrent.atomic.AtomicReference;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.formulario.Formulario;
import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.plugins.objeto.Objeto;

public class ExternalFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final InternalContainer container;
	private final Formulario formulario;

	private ExternalFormulario(Formulario formulario, Conexao padrao, Objeto objeto, Graphics g) {
		super(objeto.getId());
		container = new InternalContainer(this, padrao, objeto, g, false);
		container.setDimensaoListener(ExternalFormulario.this::getSize);
		container.setTituloListener(ExternalFormulario.this::setTitle);
		container.setComponenteListener(componenteListener);
		this.formulario = formulario;
		montarLayout();
	}

	private transient InternalListener.Componente componenteListener = new InternalListener.Componente() {
		@Override
		public void getFormulario(AtomicReference<Formulario> ref) {
			ref.set(formulario);
		}

		@Override
		public Component getComponente() {
			return ExternalFormulario.this;
		}
	};

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static ExternalFormulario criar(Formulario formulario, Conexao padrao, Objeto objeto, Graphics g) {
		ExternalFormulario form = new ExternalFormulario(formulario, padrao, objeto, g);
		Formulario.posicionarJanela(formulario, form);
		return form;
	}

	@Override
	public void executarAoAbrirFormulario() {
		container.formularioVisivel();
	}
}