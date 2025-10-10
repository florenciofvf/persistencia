package br.com.persist.plugins.ponto;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.DESTACAR_EM_FORMULARIO;
import static br.com.persist.componente.BarraButtonEnum.LIMPAR;
import static br.com.persist.componente.BarraButtonEnum.RETORNAR_AO_FICHARIO;
import static br.com.persist.componente.BarraButtonEnum.SALVAR;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Window;
import java.io.File;

import javax.swing.Icon;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.abstrato.PluginBasico;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;

public class PontoContainer extends AbstratoContainer implements PluginBasico {
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private PontoArea area = new PontoArea();
	private PontoFormulario pontoFormulario;
	private PontoDialogo pontoDialogo;
	private final File file;

	public PontoContainer(Janela janela, Formulario formulario) {
		super(formulario);
		file = new File(PontoConstantes.PONTO + Constantes.SEPARADOR + PontoConstantes.PONTO);
		toolbar.ini(janela);
		montarLayout();
		area.init();
		area.abrir(file);
	}

	public PontoDialogo getPontoDialogo() {
		return pontoDialogo;
	}

	public void setPontoDialogo(PontoDialogo pontoDialogo) {
		this.pontoDialogo = pontoDialogo;
		if (pontoDialogo != null) {
			pontoFormulario = null;
		}
	}

	public PontoFormulario getPontoFormulario() {
		return pontoFormulario;
	}

	public void setPontoFormulario(PontoFormulario pontoFormulario) {
		this.pontoFormulario = pontoFormulario;
		if (pontoFormulario != null) {
			pontoDialogo = null;
		}
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, area);
	}

	@Override
	public void setJanela(Janela janela) {
		toolbar.setJanela(janela);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;

		public void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, ABRIR_EM_FORMULARO, LIMPAR, BAIXAR, SALVAR);
		}

		@Override
		protected void destacarEmFormulario() {
			if (formulario.excluirPagina(PontoContainer.this)) {
				PontoFormulario.criar(formulario, PontoContainer.this);
			} else if (pontoDialogo != null) {
				pontoDialogo.excluirContainer();
				PontoFormulario.criar(formulario, PontoContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (pontoFormulario != null) {
				pontoFormulario.excluirContainer();
				formulario.adicionarPagina(PontoContainer.this);
			} else if (pontoDialogo != null) {
				pontoDialogo.excluirContainer();
				formulario.adicionarPagina(PontoContainer.this);
			}
		}

		@Override
		protected void abrirEmFormulario() {
			if (pontoDialogo != null) {
				pontoDialogo.excluirContainer();
			}
			PontoFormulario.criar(formulario);
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
			area.abrir(file);
		}

		@Override
		protected void limpar() {
			area.limpar();
		}

		@Override
		protected void salvar() {
			if (Util.confirmaSalvar(PontoContainer.this, Constantes.TRES)) {
				area.salvar(file);
				salvoMensagem();
			}
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
		return PontoFabrica.class;
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
				return PontoMensagens.getString(PontoConstantes.LABEL_PONTO_MIN);
			}

			@Override
			public String getTitulo() {
				return PontoMensagens.getString(PontoConstantes.LABEL_PONTO);
			}

			@Override
			public String getHint() {
				return PontoMensagens.getString(PontoConstantes.LABEL_PONTO);
			}

			@Override
			public Icon getIcone() {
				return Icones.TIMER;
			}
		};
	}
}