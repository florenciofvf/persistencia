package br.com.persist.plugins.objeto.internal;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Window;

import br.com.persist.abstrato.AbstratoFormulario;
import br.com.persist.formulario.Formulario;
import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.plugins.objeto.Objeto;

public class ExternalFormulario extends AbstratoFormulario {
	private static final long serialVersionUID = 1L;
	private final InternalContainer container;
	private boolean processado;

	private ExternalFormulario(Formulario formulario, Conexao padrao, Objeto objeto) {
		super(formulario, objeto.getId());
		container = new InternalContainer(this, padrao, objeto, false);
		container.setDimensaoListener(ExternalFormulario.this::getSize);
		container.setTituloListener(ExternalFormulario.this::setTitle);
		container.setComponenteListener(componenteListener);
		montarLayout();
	}

	private transient InternalListener.Componente componenteListener = new InternalListener.Componente() {
		@Override
		public Formulario getFormulario() {
			return formulario;
		}

		@Override
		public Component getComponente() {
			return ExternalFormulario.this;
		}
	};

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static ExternalFormulario criar(Formulario formulario, Conexao padrao, Objeto objeto) {
		ExternalFormulario form = new ExternalFormulario(formulario, padrao, objeto);
		Formulario.posicionarJanela(formulario, form);
		return form;
	}

	@Override
	public void windowActivatedHandler(Window window) {
		if (!processado) {
			processado = true;
			container.windowActivatedHandler(this);
		}
	}
}