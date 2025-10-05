package br.com.persist.plugins.configuracao;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
import static br.com.persist.componente.BarraButtonEnum.DESTACAR_EM_FORMULARIO;
import static br.com.persist.componente.BarraButtonEnum.RETORNAR_AO_FICHARIO;
import static br.com.persist.componente.BarraButtonEnum.SALVAR;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import br.com.persist.assistencia.Muro;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.TextField;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;
import br.com.persist.formulario.FormularioFabrica;

public class ConfiguracaoContainer extends AbstratoContainer {
	private final PainelConfiguracao painelConfiguracao = new PainelConfiguracao();
	private ConfiguracaoFormulario configuracaoFormulario;
	private static final long serialVersionUID = 1L;
	private ConfiguracaoDialogo configuracaoDialogo;
	private final Toolbar toolbar = new Toolbar();
	private ScrollPane scrollPane;

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
			AbstratoConfiguracao configuracao = formularioFabrica.getConfiguracao(formulario);
			painelConfiguracao.addConfiguracao(configuracao);
		}
		List<FabricaContainer> lista = formulario.getFabricas();
		for (FabricaContainer fabricaContainer : lista) {
			if (fabricaContainer instanceof FormularioFabrica) {
				continue;
			}
			AbstratoConfiguracao configuracao = fabricaContainer.getConfiguracao(formulario);
			painelConfiguracao.addConfiguracao(configuracao);
		}
		add(BorderLayout.NORTH, toolbar);
		scrollPane = new ScrollPane(painelConfiguracao);
		add(BorderLayout.CENTER, scrollPane);
	}

	@Override
	public void setJanela(Janela janela) {
		toolbar.setJanela(janela);
	}

	private class Toolbar extends BarraButton implements ActionListener {
		private static final long serialVersionUID = 1L;
		private final TextField txtPesquisa = new TextField(35);

		public void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, ABRIR_EM_FORMULARO, SALVAR);
			add(txtPesquisa);
			txtPesquisa.setToolTipText(Mensagens.getString("label.pesquisar"));
			txtPesquisa.addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!Util.isEmpty(txtPesquisa.getText())) {
				AbstratoConfiguracao config = painelConfiguracao.localizar(txtPesquisa.getText().toUpperCase());
				if (config != null) {
					exibir(config);
				}
			}
		}

		private void exibir(AbstratoConfiguracao config) {
			int altura = painelConfiguracao.getAlturaPara(config);
			scrollPane.getVerticalScrollBar().setValue(altura);
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

		@Override
		public void windowOpenedHandler(Window window) {
			painelConfiguracao.windowOpenedHandler(window);
			buttonDestacar.estadoFormulario();
		}

		@Override
		public void dialogOpenedHandler(Dialog dialog) {
			painelConfiguracao.dialogOpenedHandler(dialog);
			buttonDestacar.estadoDialogo();
		}

		void adicionadoAoFichario() {
			painelConfiguracao.adicionadoAoFichario();
			buttonDestacar.estadoFichario();
		}

		@Override
		protected void salvar() {
			Preferencias.salvar();
			salvoMensagem();
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
		private final Muro muro = new Muro();

		private PainelConfiguracao() {
			lista = new ArrayList<>();
			add(BorderLayout.CENTER, muro);
		}

		void addConfiguracao(AbstratoConfiguracao configuracao) {
			if (configuracao != null) {
				lista.add(configuracao);
				muro.camada(configuracao);
			}
		}

		void windowOpenedHandler(Window window) {
			for (AbstratoConfiguracao ac : lista) {
				ac.windowOpenedHandler(window);
			}
		}

		void dialogOpenedHandler(Dialog dialog) {
			for (AbstratoConfiguracao ac : lista) {
				ac.dialogOpenedHandler(dialog);
			}
		}

		void adicionadoAoFichario() {
			for (AbstratoConfiguracao ac : lista) {
				ac.adicionadoAoFichario();
			}
		}

		AbstratoConfiguracao localizar(String string) {
			for (AbstratoConfiguracao item : lista) {
				String titulo = item.getTitulo().toUpperCase();
				if (titulo.indexOf(string) != -1) {
					return item;
				}
			}
			return null;
		}

		int getAlturaPara(AbstratoConfiguracao config) {
			int total = 0;
			for (AbstratoConfiguracao item : lista) {
				if (item == config) {
					break;
				}
				total += item.getHeight();
			}
			return total;
		}
	}
}