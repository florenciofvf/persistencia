package br.com.persist.plugins.instrucao;

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

public class InstrucaoContainer extends AbstratoContainer {
	private InstrucaoFormulario instrucaoFormulario;
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private InstrucaoDialogo instrucaoDialogo;

	public InstrucaoContainer(Janela janela, Formulario formulario) {
		super(formulario);
		toolbar.ini(janela);
		montarLayout();
	}

	public InstrucaoDialogo getInstrucaoDialogo() {
		return instrucaoDialogo;
	}

	public void setInstrucaoDialogo(InstrucaoDialogo instrucaoDialogo) {
		this.instrucaoDialogo = instrucaoDialogo;
		if (instrucaoDialogo != null) {
			instrucaoFormulario = null;
		}
	}

	public InstrucaoFormulario getInstrucaoFormulario() {
		return instrucaoFormulario;
	}

	public void setInstrucaoFormulario(InstrucaoFormulario instrucaoFormulario) {
		this.instrucaoFormulario = instrucaoFormulario;
		if (instrucaoFormulario != null) {
			instrucaoDialogo = null;
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
			if (formulario.excluirPagina(InstrucaoContainer.this)) {
				InstrucaoFormulario.criar(formulario, InstrucaoContainer.this);
			} else if (instrucaoDialogo != null) {
				instrucaoDialogo.excluirContainer();
				InstrucaoFormulario.criar(formulario, InstrucaoContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (instrucaoFormulario != null) {
				instrucaoFormulario.excluirContainer();
				formulario.adicionarPagina(InstrucaoContainer.this);
			} else if (instrucaoDialogo != null) {
				instrucaoDialogo.excluirContainer();
				formulario.adicionarPagina(InstrucaoContainer.this);
			}
		}

		@Override
		protected void abrirEmFormulario() {
			if (instrucaoDialogo != null) {
				instrucaoDialogo.excluirContainer();
			}
			InstrucaoFormulario.criar(formulario);
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
		return InstrucaoFabrica.class;
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
				return InstrucaoMensagens.getString(InstrucaoConstantes.LABEL_INSTRUCAO_MIN);
			}

			@Override
			public String getTitulo() {
				return InstrucaoMensagens.getString(InstrucaoConstantes.LABEL_INSTRUCAO);
			}

			@Override
			public String getHint() {
				return InstrucaoMensagens.getString(InstrucaoConstantes.LABEL_INSTRUCAO);
			}

			@Override
			public Icon getIcone() {
				return Icones.FRAGMENTO;
			}
		};
	}
}
