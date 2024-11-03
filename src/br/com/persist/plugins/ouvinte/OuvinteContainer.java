package br.com.persist.plugins.ouvinte;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.DESTACAR_EM_FORMULARIO;
import static br.com.persist.componente.BarraButtonEnum.RETORNAR_AO_FICHARIO;
import static br.com.persist.componente.BarraButtonEnum.SALVAR;

import java.awt.BorderLayout;
import java.awt.Component;
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

public class OuvinteContainer extends AbstratoContainer {
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private OuvinteFormulario ouvinteFormulario;

	public OuvinteContainer(Janela janela, Formulario formulario) {
		super(formulario);
		toolbar.ini(janela);
		montarLayout();
	}

	public OuvinteFormulario getOuvinteFormulario() {
		return ouvinteFormulario;
	}

	public void setOuvinteFormulario(OuvinteFormulario ouvinteFormulario) {
		this.ouvinteFormulario = ouvinteFormulario;
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

		public void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, ABRIR_EM_FORMULARO, BAIXAR, SALVAR);
		}

		@Override
		protected void destacarEmFormulario() {
			if (formulario.excluirPagina(OuvinteContainer.this)) {
				OuvinteFormulario.criar(formulario, OuvinteContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (ouvinteFormulario != null) {
				ouvinteFormulario.excluirContainer();
				formulario.adicionarPagina(OuvinteContainer.this);
			}
		}

		@Override
		protected void abrirEmFormulario() {
			OuvinteFormulario.criar(formulario);
		}

		@Override
		public void windowOpenedHandler(Window window) {
			buttonDestacar.estadoFormulario();
		}

		void adicionadoAoFichario() {
			buttonDestacar.estadoFichario();
		}

		@Override
		protected void baixar() {
		}

		@Override
		protected void salvar() {
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
	public String getStringPersistencia() {
		return Constantes.VAZIO;
	}

	@Override
	public Class<?> getClasseFabrica() {
		return OuvinteFabrica.class;
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
				return OuvinteMensagens.getString(OuvinteConstantes.LABEL_OUVINTE_MIN);
			}

			@Override
			public String getTitulo() {
				return OuvinteMensagens.getString(OuvinteConstantes.LABEL_OUVINTE);
			}

			@Override
			public String getHint() {
				return OuvinteMensagens.getString(OuvinteConstantes.LABEL_OUVINTE);
			}

			@Override
			public Icon getIcone() {
				return Icones.PANEL;
			}
		};
	}
}