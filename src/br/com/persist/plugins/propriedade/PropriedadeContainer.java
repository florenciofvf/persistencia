package br.com.persist.plugins.propriedade;

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

public class PropriedadeContainer extends AbstratoContainer {
	private PropriedadeFormulario propriedadeFormulario;
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private PropriedadeDialogo propriedadeDialogo;

	public PropriedadeContainer(Janela janela, Formulario formulario) {
		super(formulario);
		toolbar.ini(janela);
		montarLayout();
	}

	public PropriedadeDialogo getPropriedadeDialogo() {
		return propriedadeDialogo;
	}

	public void setPropriedadeDialogo(PropriedadeDialogo propriedadeDialogo) {
		this.propriedadeDialogo = propriedadeDialogo;
		if (propriedadeDialogo != null) {
			propriedadeFormulario = null;
		}
	}

	public PropriedadeFormulario getPropriedadeFormulario() {
		return propriedadeFormulario;
	}

	public void setPropriedadeFormulario(PropriedadeFormulario propriedadeFormulario) {
		this.propriedadeFormulario = propriedadeFormulario;
		if (propriedadeFormulario != null) {
			propriedadeDialogo = null;
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
			if (formulario.excluirPagina(PropriedadeContainer.this)) {
				PropriedadeFormulario.criar(formulario, PropriedadeContainer.this);
			} else if (propriedadeDialogo != null) {
				propriedadeDialogo.excluirContainer();
				PropriedadeFormulario.criar(formulario, PropriedadeContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (propriedadeFormulario != null) {
				propriedadeFormulario.excluirContainer();
				formulario.adicionarPagina(PropriedadeContainer.this);
			} else if (propriedadeDialogo != null) {
				propriedadeDialogo.excluirContainer();
				formulario.adicionarPagina(PropriedadeContainer.this);
			}
		}

		@Override
		protected void abrirEmFormulario() {
			if (propriedadeDialogo != null) {
				propriedadeDialogo.excluirContainer();
			}
			PropriedadeFormulario.criar(formulario);
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
		return PropriedadeFabrica.class;
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
				return PropriedadeMensagens.getString(PropriedadeConstantes.LABEL_PROPRIEDADE_MIN);
			}

			@Override
			public String getTitulo() {
				return PropriedadeMensagens.getString(PropriedadeConstantes.LABEL_PROPRIEDADE);
			}

			@Override
			public String getHint() {
				return PropriedadeMensagens.getString(PropriedadeConstantes.LABEL_PROPRIEDADE);
			}

			@Override
			public Icon getIcone() {
				return Icones.EDIT;
			}
		};
	}
}