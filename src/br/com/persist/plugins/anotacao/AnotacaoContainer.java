package br.com.persist.plugins.anotacao;

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

public class AnotacaoContainer extends AbstratoContainer {
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private AnotacaoFormulario anotacaoFormulario;
	private AnotacaoDialogo anotacaoDialogo;
	private final AnotacaoSplit split;

	public AnotacaoContainer(Janela janela, Formulario formulario, String conteudo) {
		super(formulario);
		split = new AnotacaoSplit();
		split.inicializar();
		toolbar.ini(janela);
		montarLayout();
	}

	public AnotacaoDialogo getAnotacaoDialogo() {
		return anotacaoDialogo;
	}

	public void setAnotacaoDialogo(AnotacaoDialogo anotacaoDialogo) {
		this.anotacaoDialogo = anotacaoDialogo;
		if (anotacaoDialogo != null) {
			anotacaoFormulario = null;
		}
	}

	public AnotacaoFormulario getAnotacaoFormulario() {
		return anotacaoFormulario;
	}

	public void setAnotacaoFormulario(AnotacaoFormulario anotacaoFormulario) {
		this.anotacaoFormulario = anotacaoFormulario;
		if (anotacaoFormulario != null) {
			anotacaoDialogo = null;
		}
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, split);
	}

	@Override
	public void setJanela(Janela janela) {
		toolbar.setJanela(janela);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;

		public void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, ABRIR_EM_FORMULARO);
		}

		@Override
		protected void destacarEmFormulario() {
			if (formulario.excluirPagina(AnotacaoContainer.this)) {
				AnotacaoFormulario.criar(formulario, AnotacaoContainer.this);
			} else if (anotacaoDialogo != null) {
				anotacaoDialogo.excluirContainer();
				AnotacaoFormulario.criar(formulario, AnotacaoContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (anotacaoFormulario != null) {
				anotacaoFormulario.excluirContainer();
				formulario.adicionarPagina(AnotacaoContainer.this);
			} else if (anotacaoDialogo != null) {
				anotacaoDialogo.excluirContainer();
				formulario.adicionarPagina(AnotacaoContainer.this);
			}
		}

		@Override
		protected void abrirEmFormulario() {
			if (anotacaoDialogo != null) {
				anotacaoDialogo.excluirContainer();
			}
			AnotacaoFormulario.criar(formulario, Constantes.VAZIO);
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
		return AnotacaoFabrica.class;
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
				return AnotacaoMensagens.getString(AnotacaoConstantes.LABEL_ANOTACOES_MIN);
			}

			@Override
			public String getTitulo() {
				return AnotacaoMensagens.getString(AnotacaoConstantes.LABEL_ANOTACOES);
			}

			@Override
			public String getHint() {
				return AnotacaoMensagens.getString(AnotacaoConstantes.LABEL_ANOTACOES);
			}

			@Override
			public Icon getIcone() {
				return Icones.PANEL4;
			}
		};
	}
}