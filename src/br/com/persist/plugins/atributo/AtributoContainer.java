package br.com.persist.plugins.atributo;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.DESTACAR_EM_FORMULARIO;
import static br.com.persist.componente.BarraButtonEnum.RETORNAR_AO_FICHARIO;
import static br.com.persist.componente.BarraButtonEnum.SALVAR;

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

public class AtributoContainer extends AbstratoContainer {
	private AtributoFormulario atributoFormulario;
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private AtributoDialogo atributoDialogo;

	public AtributoContainer(Janela janela, Formulario formulario) {
		super(formulario);
		toolbar.ini(janela);
		montarLayout();
	}

	public AtributoDialogo getAtributoDialogo() {
		return atributoDialogo;
	}

	public void setAtributoDialogo(AtributoDialogo atributoDialogo) {
		this.atributoDialogo = atributoDialogo;
		if (atributoDialogo != null) {
			atributoFormulario = null;
		}
	}

	public AtributoFormulario getAtributoFormulario() {
		return atributoFormulario;
	}

	public void setAtributoFormulario(AtributoFormulario atributoFormulario) {
		this.atributoFormulario = atributoFormulario;
		if (atributoFormulario != null) {
			atributoDialogo = null;
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

		public void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, ABRIR_EM_FORMULARO, BAIXAR, SALVAR);
		}

		@Override
		protected void destacarEmFormulario() {
			if (formulario.excluirPagina(AtributoContainer.this)) {
				AtributoFormulario.criar(formulario, AtributoContainer.this);
			} else if (atributoDialogo != null) {
				atributoDialogo.excluirContainer();
				AtributoFormulario.criar(formulario, AtributoContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (atributoFormulario != null) {
				atributoFormulario.excluirContainer();
				formulario.adicionarPagina(AtributoContainer.this);
			} else if (atributoDialogo != null) {
				atributoDialogo.excluirContainer();
				formulario.adicionarPagina(AtributoContainer.this);
			}
		}

		@Override
		protected void abrirEmFormulario() {
			if (atributoDialogo != null) {
				atributoDialogo.excluirContainer();
			}
			AtributoFormulario.criar(formulario);
		}

		@Override
		public void windowOpenedHandler(Window window) {
			buttonDestacar.estadoFormulario();
		}

		@Override
		public void dialogOpenedHandler(Dialog dialog) {
			buttonDestacar.estadoDialogo();
		}

		void adicionadoAoFichario() {
			buttonDestacar.estadoFichario();
		}

		@Override
		public void baixar() {
		}

		@Override
		public void salvar() {
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
		return AtributoFabrica.class;
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
				return AtributoMensagens.getString(AtributoConstantes.LABEL_ATRIBUTO_MIN);
			}

			@Override
			public String getTitulo() {
				return AtributoMensagens.getString(AtributoConstantes.LABEL_ATRIBUTO);
			}

			@Override
			public String getHint() {
				return AtributoMensagens.getString(AtributoConstantes.LABEL_ATRIBUTO);
			}

			@Override
			public Icon getIcone() {
				return Icones.REGION;
			}
		};
	}
}