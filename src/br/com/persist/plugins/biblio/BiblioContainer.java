package br.com.persist.plugins.biblio;

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

public class BiblioContainer extends AbstratoContainer {
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private BiblioFormulario biblioFormulario;
	private BiblioDialogo biblioDialogo;

	public BiblioContainer(Janela janela, Formulario formulario) {
		super(formulario);
		toolbar.ini(janela);
		montarLayout();
	}

	public BiblioDialogo getBiblioDialogo() {
		return biblioDialogo;
	}

	public void setBiblioDialogo(BiblioDialogo biblioDialogo) {
		this.biblioDialogo = biblioDialogo;
		if (biblioDialogo != null) {
			biblioFormulario = null;
		}
	}

	public BiblioFormulario getBiblioFormulario() {
		return biblioFormulario;
	}

	public void setBiblioFormulario(BiblioFormulario biblioFormulario) {
		this.biblioFormulario = biblioFormulario;
		if (biblioFormulario != null) {
			biblioDialogo = null;
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
			if (formulario.excluirPagina(BiblioContainer.this)) {
				BiblioFormulario.criar(formulario, BiblioContainer.this);
			} else if (biblioDialogo != null) {
				biblioDialogo.excluirContainer();
				BiblioFormulario.criar(formulario, BiblioContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (biblioFormulario != null) {
				biblioFormulario.excluirContainer();
				formulario.adicionarPagina(BiblioContainer.this);
			} else if (biblioDialogo != null) {
				biblioDialogo.excluirContainer();
				formulario.adicionarPagina(BiblioContainer.this);
			}
		}

		@Override
		protected void abrirEmFormulario() {
			if (biblioDialogo != null) {
				biblioDialogo.excluirContainer();
			}
			BiblioFormulario.criar(formulario);
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
		protected void baixar() {
			// TODO - impl
		}

		@Override
		protected void salvar() {
			// TODO - impl
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
		return BiblioFabrica.class;
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
				return BiblioMensagens.getString(BiblioConstantes.LABEL_BIBLIO_MIN);
			}

			@Override
			public String getTitulo() {
				return BiblioMensagens.getString(BiblioConstantes.LABEL_BIBLIO);
			}

			@Override
			public String getHint() {
				return BiblioMensagens.getString(BiblioConstantes.LABEL_BIBLIO);
			}

			@Override
			public Icon getIcone() {
				return Icones.COR;
			}
		};
	}
}