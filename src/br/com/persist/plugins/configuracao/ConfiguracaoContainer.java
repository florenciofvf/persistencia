package br.com.persist.plugins.configuracao;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
import static br.com.persist.componente.BarraButtonEnum.DESTACAR_EM_FORMULARIO;
import static br.com.persist.componente.BarraButtonEnum.RETORNAR_AO_FICHARIO;
import static br.com.persist.componente.BarraButtonEnum.SALVAR;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import br.com.persist.abstrato.AbstratoConfiguracao;
import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.abstrato.FabricaContainer;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;
import br.com.persist.formulario.FormularioFabrica;

public class ConfiguracaoContainer extends AbstratoContainer {
	private static final long serialVersionUID = 1L;
	private final PainelConfiguracao painelConfiguracao = new PainelConfiguracao();
	private ConfiguracaoFormulario configuracaoFormulario;
	private ConfiguracaoDialogo configuracaoDialogo;
	private final Toolbar toolbar = new Toolbar();

	public ConfiguracaoContainer(Janela janela, Formulario formulario) {
		super(formulario);
		toolbar.ini(janela);
		montarLayout();
	}

	public ConfiguracaoDialogo getConfiguracaoDialogo() {
		return configuracaoDialogo;
	}

	public void setConfiguracaoDialogo(ConfiguracaoDialogo configuracaoDialogo) {
		this.configuracaoDialogo = configuracaoDialogo;
		if (configuracaoDialogo != null) {
			configuracaoFormulario = null;
		}
	}

	public ConfiguracaoFormulario getConfiguracaoFormulario() {
		return configuracaoFormulario;
	}

	public void setConfiguracaoFormulario(ConfiguracaoFormulario configuracaoFormulario) {
		this.configuracaoFormulario = configuracaoFormulario;
		if (configuracaoFormulario != null) {
			configuracaoDialogo = null;
		}
	}

	private void montarLayout() {
		FormularioFabrica formularioFabrica = (FormularioFabrica) formulario
				.getFabrica(FormularioFabrica.class.getName());
		if (formularioFabrica != null) {
			addConfiguracao(formularioFabrica);
		}
		List<FabricaContainer> lista = formulario.getFabricas();
		for (FabricaContainer fabricaContainer : lista) {
			if (fabricaContainer instanceof FormularioFabrica) {
				continue;
			}
			addConfiguracao(fabricaContainer);
		}
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, new ScrollPane(painelConfiguracao));
	}

	private void addConfiguracao(FabricaContainer fabricaContainer) {
		AbstratoConfiguracao configuracao = fabricaContainer.getConfiguracao(formulario);
		if (configuracao != null) {
			painelConfiguracao.addConfiguracao(configuracao);
		}
	}

	@Override
	public void setJanela(Janela janela) {
		toolbar.setJanela(janela);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;

		public void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, ABRIR_EM_FORMULARO, SALVAR);
		}

		@Override
		protected void destacarEmFormulario() {
			if (formulario.excluirPagina(ConfiguracaoContainer.this)) {
				ConfiguracaoFormulario.criar(formulario, ConfiguracaoContainer.this);
			} else if (configuracaoDialogo != null) {
				configuracaoDialogo.excluirContainer();
				ConfiguracaoFormulario.criar(formulario, ConfiguracaoContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (configuracaoFormulario != null) {
				configuracaoFormulario.excluirContainer();
				formulario.adicionarPagina(ConfiguracaoContainer.this);
			} else if (configuracaoDialogo != null) {
				configuracaoDialogo.excluirContainer();
				formulario.adicionarPagina(ConfiguracaoContainer.this);
			}
		}

		@Override
		protected void abrirEmFormulario() {
			if (configuracaoDialogo != null) {
				configuracaoDialogo.excluirContainer();
			}
			ConfiguracaoFormulario.criar(formulario);
		}

		void formularioVisivel() {
			painelConfiguracao.formularioVisivel();
			buttonDestacar.estadoFormulario();
		}

		void paginaVisivel() {
			painelConfiguracao.paginaVisivel();
			buttonDestacar.estadoFichario();
		}

		void dialogoVisivel() {
			painelConfiguracao.dialogoVisivel();
			buttonDestacar.estadoDialogo();
		}

		@Override
		protected void salvar() {
			Preferencias.salvar();
			salvoMensagem();
		}
	}

	@Override
	public void adicionadoAoFichario(Fichario fichario) {
		toolbar.paginaVisivel();
	}

	public void formularioVisivel() {
		toolbar.formularioVisivel();
	}

	public void dialogoVisivel() {
		toolbar.dialogoVisivel();
	}

	@Override
	public String getStringPersistencia() {
		return Constantes.VAZIO;
	}

	@Override
	public Class<?> getClasseFabrica() {
		return ConfiguracaoFabrica.class;
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
				return Mensagens.getString(Constantes.LABEL_CONFIGURACOES_MIN);
			}

			@Override
			public String getTitulo() {
				return Mensagens.getString(Constantes.LABEL_CONFIGURACOES);
			}

			@Override
			public String getHint() {
				return Mensagens.getString(Constantes.LABEL_CONFIGURACOES);
			}

			@Override
			public Icon getIcone() {
				return Icones.CONFIG;
			}
		};
	}

	private class PainelConfiguracao extends Panel {
		private static final long serialVersionUID = 1L;
		private final List<AbstratoConfiguracao> lista;

		private PainelConfiguracao() {
			super(new GridLayout(0, 1));
			lista = new ArrayList<>();
		}

		void addConfiguracao(AbstratoConfiguracao configuracao) {
			lista.add(configuracao);
			add(configuracao);
		}

		void formularioVisivel() {
			for (AbstratoConfiguracao ac : lista) {
				ac.formularioVisivel();
			}
		}

		void dialogoVisivel() {
			for (AbstratoConfiguracao ac : lista) {
				ac.dialogoVisivel();
			}
		}

		void paginaVisivel() {
			for (AbstratoConfiguracao ac : lista) {
				ac.paginaVisivel();
			}
		}
	}
}