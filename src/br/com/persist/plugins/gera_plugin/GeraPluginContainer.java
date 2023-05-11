package br.com.persist.plugins.gera_plugin;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
import static br.com.persist.componente.BarraButtonEnum.DESTACAR_EM_FORMULARIO;
import static br.com.persist.componente.BarraButtonEnum.RETORNAR_AO_FICHARIO;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Window;

import javax.swing.Icon;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;

public class GeraPluginContainer extends AbstratoContainer {
	private GeraPluginFormulario geraPluginFormulario;
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private GeraPluginDialogo geraPluginDialogo;

	public GeraPluginContainer(Janela janela, Formulario formulario) {
		super(formulario);
		toolbar.ini(janela);
		montarLayout();
	}

	public GeraPluginDialogo getGeraPluginDialogo() {
		return geraPluginDialogo;
	}

	public void setGeraPluginDialogo(GeraPluginDialogo geraPluginDialogo) {
		this.geraPluginDialogo = geraPluginDialogo;
		if (geraPluginDialogo != null) {
			geraPluginFormulario = null;
		}
	}

	public GeraPluginFormulario getGeraPluginFormulario() {
		return geraPluginFormulario;
	}

	public void setGeraPluginFormulario(GeraPluginFormulario geraPluginFormulario) {
		this.geraPluginFormulario = geraPluginFormulario;
		if (geraPluginFormulario != null) {
			geraPluginDialogo = null;
		}
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
	}

	@Override
	public void setJanela(Janela janela) {
		toolbar.setJanela(janela);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;

		protected void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, ABRIR_EM_FORMULARO);
		}

		@Override
		protected void destacarEmFormulario() {
			if (formulario.excluirPagina(GeraPluginContainer.this)) {
				GeraPluginFormulario.criar(formulario, GeraPluginContainer.this);
			} else if (geraPluginDialogo != null) {
				geraPluginDialogo.excluirContainer();
				GeraPluginFormulario.criar(formulario, GeraPluginContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (geraPluginFormulario != null) {
				geraPluginFormulario.excluirContainer();
				formulario.adicionarPagina(GeraPluginContainer.this);
			} else if (geraPluginDialogo != null) {
				geraPluginDialogo.excluirContainer();
				formulario.adicionarPagina(GeraPluginContainer.this);
			}
		}

		@Override
		protected void abrirEmFormulario() {
			if (geraPluginDialogo != null) {
				geraPluginDialogo.excluirContainer();
			}
			GeraPluginFormulario.criar(formulario);
		}

		@Override
		public void windowOpenedHandler(Window window) {
			buttonDestacar.estadoFormulario();
		}

		void adicionadoAoFichario() {
			buttonDestacar.estadoFichario();
		}

		@Override
		public void dialogOpenedHandler(Dialog dialog) {
			buttonDestacar.estadoDialogo();
		}
	}

	@Override
	public void adicionadoAoFichario(Fichario fichario) {
		toolbar.adicionadoAoFichario();
	}

	@Override
	public void windowOpenedHandler(Window window) {
		toolbar.windowOpenedHandler(window);
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
		toolbar.dialogOpenedHandler(dialog);
	}

	@Override
	public String getStringPersistencia() {
		return Constantes.VAZIO;
	}

	@Override
	public Class<?> getClasseFabrica() {
		return GeraPluginFabrica.class;
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public Titulo getTitulo() {
		return new AbstratoTitulo() {
			@Override
			public String getTituloMin() {
				return GeraPluginMensagens.getString(GeraPluginConstantes.LABEL_GERA_PLUGIN_MIN);
			}

			@Override
			public String getTitulo() {
				return GeraPluginMensagens.getString(GeraPluginConstantes.LABEL_GERA_PLUGIN);
			}

			@Override
			public String getHint() {
				return GeraPluginMensagens.getString(GeraPluginConstantes.LABEL_GERA_PLUGIN);
			}

			@Override
			public Icon getIcone() {
				return Icones.SOMA;
			}
		};
	}
}