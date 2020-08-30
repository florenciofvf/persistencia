package br.com.persist.configuracao;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import br.com.persist.componente.BarraButton;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.Label;
import br.com.persist.componente.Panel;
import br.com.persist.componente.PanelCenter;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.TextField;
import br.com.persist.container.AbstratoContainer;
import br.com.persist.fichario.IFicharioSalvar;
import br.com.persist.icone.Icones;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Preferencias;
import br.com.persist.util.Util;

public class ConfiguracaoContainer extends AbstratoContainer implements IFicharioSalvar {
	private static final long serialVersionUID = 1L;
	private final CheckBox chkExecAposCopiarColunaConcatenado = new CheckBox(
			"label.executar_apos_copiar_coluna_concatenado");
	private final CheckBox chkExecAposBaixarParaComplemento = new CheckBox(
			"label.executar_apos_baixar_para_complemento");
	private final CheckBox chkAplicarLarguraAoAbrirArquivoObjeto = new CheckBox(
			"label.aplicar_largura_ao_abrir_arq_objeto");
	private final CheckBox chkAplicarAlturaAoAbrirArquivoObjeto = new CheckBox(
			"label.aplicar_altura_ao_abrir_arq_objeto");
	private final CheckBox chkAreaTransTabelaRegistros = new CheckBox("label.area_trans_tabela_registros");
	private final CheckBox chkFecharOrigemAposSoltar = new CheckBox("label.fechar_origem_apos_soltar");
	private final CheckBox chkNomeColunaListener = new CheckBox("label.copiar_nome_coluna_listener");
	private final CheckBox chkAtivarAbrirAutoDestac = new CheckBox("label.abrir_auto_destacado");
	private final CheckBox chkAbortarFecharComESC = new CheckBox("label.abortar_fechar_com_esc");
	private final CheckBox chkAtivarAbrirAuto = new CheckBox("label.ativar_abrir_auto");
	private final CheckBox chkFicharioScroll = new CheckBox("label.fichario_scroll");
	private final CheckBox chkNomearArrasto = new CheckBox("label.nomear_arrasto");
	private final CheckBox chkTituloAbaMin = new CheckBox("label.titulo_aba_min");
	private final TextField txtFormFichaDialogo = new TextField();
	private final TextField txtDefinirLargura = new TextField();
	private final TextField txtDefinirAltura = new TextField();
	private final TextField txtFormDialogo = new TextField();
	private final TextField txtFormFicha = new TextField();
	private ConfiguracaoFormulario configuracaoFormulario;
	private final Toolbar toolbar = new Toolbar();

	private final transient NomeValor[] intervalosCompara = { new NomeValor("label.1", 1, NomeValor.INTERVALO_COMPARA),
			new NomeValor("label.3", 3, NomeValor.INTERVALO_COMPARA),
			new NomeValor("label.5", 5, NomeValor.INTERVALO_COMPARA),
			new NomeValor("label.10", 10, NomeValor.INTERVALO_COMPARA),
			new NomeValor("label.30", 30, NomeValor.INTERVALO_COMPARA),
			new NomeValor("label.50", 50, NomeValor.INTERVALO_COMPARA),
			new NomeValor("label.100", 100, NomeValor.INTERVALO_COMPARA),
			new NomeValor("label.200", 200, NomeValor.INTERVALO_COMPARA),
			new NomeValor("label.300", 300, NomeValor.INTERVALO_COMPARA),
			new NomeValor("label.500", 500, NomeValor.INTERVALO_COMPARA),
			new NomeValor("label.1000", 1000, NomeValor.INTERVALO_COMPARA),
			new NomeValor("label.2000", 2000, NomeValor.INTERVALO_COMPARA) };

	private final transient NomeValor[] intervalos = { new NomeValor("label.1000", 1000, NomeValor.INTERVALO_AUTO),
			new NomeValor("label.5000", 5000, NomeValor.INTERVALO_AUTO),
			new NomeValor("label.7500", 7500, NomeValor.INTERVALO_AUTO),
			new NomeValor("label.10000", 10000, NomeValor.INTERVALO_AUTO),
			new NomeValor("label.15000", 15000, NomeValor.INTERVALO_AUTO),
			new NomeValor("label.20000", 20000, NomeValor.INTERVALO_AUTO),
			new NomeValor("label.30000", 30000, NomeValor.INTERVALO_AUTO),
			new NomeValor("label.40000", 40000, NomeValor.INTERVALO_AUTO),
			new NomeValor("label.60000", 60000, NomeValor.INTERVALO_AUTO) };

	private final transient NomeValor[] layouts = { new NomeValor("label.somente_fichario", 1, NomeValor.LAYOUTS),
			new NomeValor("label.arquivo_anexo_esquerdo", 2, NomeValor.LAYOUTS),
			new NomeValor("label.anexo_arquivo_esquerdo", 3, NomeValor.LAYOUTS),
			new NomeValor("label.arquivo_anexo_abaixo", 4, NomeValor.LAYOUTS),
			new NomeValor("label.anexo_arquivo_abaixo", 5, NomeValor.LAYOUTS),
			new NomeValor("label.arquivo_abaixo", 6, NomeValor.LAYOUTS),
			new NomeValor("label.anexo_abaixo", 5, NomeValor.LAYOUTS) };

	private final transient NomeValor[] destacados = {
			new NomeValor("label.formulario", Constantes.TIPO_CONTAINER_FORMULARIO, NomeValor.DESTACADOS),
			new NomeValor("label.fichario", Constantes.TIPO_CONTAINER_FICHARIO, NomeValor.DESTACADOS),
			new NomeValor("label.desktop", Constantes.TIPO_CONTAINER_DESKTOP, NomeValor.DESTACADOS) };

	private final transient NomeValor[] posicoes = {
			new NomeValor("label.acima", SwingConstants.TOP, NomeValor.POSICAO_ABA),
			new NomeValor("label.esquerdo", SwingConstants.LEFT, NomeValor.POSICAO_ABA),
			new NomeValor("label.abaixo", SwingConstants.BOTTOM, NomeValor.POSICAO_ABA),
			new NomeValor("label.direito", SwingConstants.RIGHT, NomeValor.POSICAO_ABA) };

	public ConfiguracaoContainer(IJanela janela, Formulario formulario) {
		super(formulario);
		toolbar.ini(janela);
		montarLayout();
		configurar();
	}

	public ConfiguracaoFormulario getConfiguracaoFormulario() {
		return configuracaoFormulario;
	}

	public void setConfiguracaoFormulario(ConfiguracaoFormulario configuracaoFormulario) {
		this.configuracaoFormulario = configuracaoFormulario;
	}

	private void montarLayout() {
		PanelCenter panelIntervalosCompara = criarPainelGrupo(intervalosCompara, Preferencias.getIntervaloComparacao());
		PanelCenter panelDestacados = criarPainelGrupo(destacados, Preferencias.getTipoContainerPesquisaAuto());
		PanelCenter panelIntervalos = criarPainelGrupo(intervalos, Preferencias.getIntervaloPesquisaAuto());
		PanelCenter panelPosicoes = criarPainelGrupo(posicoes, Preferencias.getPosicaoAbaFichario());
		PanelCenter panelLayouts = criarPainelGrupo(layouts, Preferencias.getLayoutAbertura());

		chkAplicarLarguraAoAbrirArquivoObjeto.setSelected(Preferencias.isAplicarLarguraAoAbrirArquivoObjeto());
		chkAplicarAlturaAoAbrirArquivoObjeto.setSelected(Preferencias.isAplicarAlturaAoAbrirArquivoObjeto());
		chkExecAposCopiarColunaConcatenado.setSelected(Preferencias.isExecAposCopiarColunaConcatenado());
		chkExecAposBaixarParaComplemento.setSelected(Preferencias.isExecAposBaixarParaComplemento());
		chkAreaTransTabelaRegistros.setSelected(Preferencias.isAreaTransTabelaRegistros());
		chkNomeColunaListener.setSelected(Preferencias.isCopiarNomeColunaListener());
		chkAtivarAbrirAutoDestac.setSelected(Preferencias.isAbrirAutoDestacado());
		txtDefinirLargura.setText("" + Preferencias.getPorcHorizontalLocalForm());
		chkAbortarFecharComESC.setSelected(Preferencias.isAbortarFecharComESC());
		chkFecharOrigemAposSoltar.setSelected(Preferencias.isFecharAposSoltar());
		txtDefinirAltura.setText("" + Preferencias.getPorcVerticalLocalForm());
		chkFicharioScroll.setSelected(Preferencias.isFicharioComRolagem());
		txtFormFichaDialogo.setText(Preferencias.getFormFichaDialogo());
		chkNomearArrasto.setSelected(Preferencias.isNomearArrasto());
		chkAtivarAbrirAuto.setSelected(Preferencias.isAbrirAuto());
		chkTituloAbaMin.setSelected(Preferencias.isTituloAbaMin());
		txtFormDialogo.setText(Preferencias.getFormDialogo());
		txtFormFicha.setText(Preferencias.getFormFicha());

		Label tituloIntervaloCompara = criarLabelTitulo("label.intervalo_comparacao_titulo");
		Label tituloDestacado = criarLabelTitulo("label.tipo_container_pesquisa_auto");
		Label tituloIntervalo = criarLabelTitulo("label.intervalo_pesquisa_auto");
		Label tituloLayoutAbr = criarLabelTitulo("label.layout_abertura");
		Label tituloLocalAbas = criarLabelTitulo("label.local_abas");
		Label email = criarLabelTitulo("contato");

		Panel container = new Panel(new GridLayout(0, 1));
		container.add(email);
		container.add(tituloLocalAbas);
		container.add(panelPosicoes);
		container.add(new JSeparator());
		container.add(tituloIntervalo);
		container.add(panelIntervalos);
		container.add(new JSeparator());
		container.add(tituloLayoutAbr);
		container.add(panelLayouts);
		container.add(new JSeparator());
		container.add(tituloIntervaloCompara);
		container.add(panelIntervalosCompara);
		container.add(criarLabelTitulo("label.titulo_cor_total_recente"));
		container.add(new PainelCorTotalRecente());
		container.add(new JSeparator());
		container.add(chkAreaTransTabelaRegistros);
		container.add(chkAbortarFecharComESC);
		container.add(chkExecAposBaixarParaComplemento);
		container.add(chkExecAposCopiarColunaConcatenado);
		container.add(chkNomeColunaListener);
		container.add(chkFecharOrigemAposSoltar);
		container.add(chkNomearArrasto);
		container.add(chkTituloAbaMin);
		container.add(chkFicharioScroll);
		container.add(new JSeparator());
		container.add(chkAtivarAbrirAuto);
		container.add(chkAtivarAbrirAutoDestac);
		container.add(tituloDestacado);
		container.add(panelDestacados);
		container.add(new JSeparator());
		container.add(new PanelCenter(new Label("label.form_ficha_dialogo"), txtFormFichaDialogo));
		container.add(new PanelCenter(new Label("label.form_dialogo"), txtFormDialogo));
		container.add(new PanelCenter(new Label("label.form_ficha"), txtFormFicha));
		container.add(new JSeparator());
		container.add(new PanelCenter(new Label("label.definir_largura"), txtDefinirLargura));
		container.add(new PanelCenter(chkAplicarLarguraAoAbrirArquivoObjeto));
		container.add(new PanelCenter(new Label("label.definir_altura"), txtDefinirAltura));
		container.add(new PanelCenter(chkAplicarAlturaAoAbrirArquivoObjeto));

		add(BorderLayout.CENTER, new ScrollPane(container));
		add(BorderLayout.NORTH, toolbar);

		Insets insets = new Insets(5, 10, 5, 5);

		chkExecAposCopiarColunaConcatenado.setMargin(insets);
		chkExecAposBaixarParaComplemento.setMargin(insets);
		chkAreaTransTabelaRegistros.setMargin(insets);
		chkFecharOrigemAposSoltar.setMargin(insets);
		chkAtivarAbrirAutoDestac.setMargin(insets);
		chkAbortarFecharComESC.setMargin(insets);
		chkNomeColunaListener.setMargin(insets);
		chkAtivarAbrirAuto.setMargin(insets);
		chkFicharioScroll.setMargin(insets);
		chkNomearArrasto.setMargin(insets);
		chkTituloAbaMin.setMargin(insets);
	}

	private PanelCenter criarPainelGrupo(NomeValor[] nomeValores, int padrao) {
		PanelCenter panel = new PanelCenter();
		ButtonGroup grupo = new ButtonGroup();

		for (int i = 0; i < nomeValores.length; i++) {
			RadioPosicao radio = new RadioPosicao(nomeValores[i]);
			radio.setSelected(radio.nomeValor.valor == padrao);
			radio.setMargin(new Insets(5, 10, 5, 5));
			panel.add(radio);
			grupo.add(radio);
		}

		return panel;
	}

	private Label criarLabelTitulo(String chaveRotulo) {
		Label label = new Label(chaveRotulo);
		label.setHorizontalAlignment(Label.CENTER);

		return label;
	}

	private void configurar() {
		chkFicharioScroll.addActionListener(e -> {
			Preferencias.setFicharioComRolagem(chkFicharioScroll.isSelected());
			formulario.getFichario().setTabLayoutPolicy(
					Preferencias.isFicharioComRolagem() ? JTabbedPane.SCROLL_TAB_LAYOUT : JTabbedPane.WRAP_TAB_LAYOUT);
		});

		chkAplicarLarguraAoAbrirArquivoObjeto.addActionListener(e -> Preferencias
				.setAplicarLarguraAoAbrirArquivoObjeto(chkAplicarLarguraAoAbrirArquivoObjeto.isSelected()));

		chkAplicarAlturaAoAbrirArquivoObjeto.addActionListener(e -> Preferencias
				.setAplicarAlturaAoAbrirArquivoObjeto(chkAplicarAlturaAoAbrirArquivoObjeto.isSelected()));

		chkExecAposCopiarColunaConcatenado.addActionListener(
				e -> Preferencias.setExecAposCopiarColunaConcatenado(chkExecAposCopiarColunaConcatenado.isSelected()));

		chkNomeColunaListener
				.addActionListener(e -> Preferencias.setCopiarNomeColunaListener(chkNomeColunaListener.isSelected()));

		txtFormFichaDialogo.addActionListener(e -> Preferencias.setFormFichaDialogo(txtFormFichaDialogo.getText()));

		chkExecAposBaixarParaComplemento.addActionListener(
				e -> Preferencias.setExecAposBaixarParaComplemento(chkExecAposBaixarParaComplemento.isSelected()));

		chkAtivarAbrirAutoDestac
				.addActionListener(e -> Preferencias.setAbrirAutoDestacado(chkAtivarAbrirAutoDestac.isSelected()));

		chkAbortarFecharComESC
				.addActionListener(e -> Preferencias.setAbortarFecharComESC(chkAbortarFecharComESC.isSelected()));

		chkFecharOrigemAposSoltar
				.addActionListener(e -> Preferencias.setFecharAposSoltar(chkFecharOrigemAposSoltar.isSelected()));

		chkNomearArrasto.addActionListener(e -> Preferencias.setNomearArrasto(chkNomearArrasto.isSelected()));

		chkAtivarAbrirAuto.addActionListener(e -> Preferencias.setAbrirAuto(chkAtivarAbrirAuto.isSelected()));

		chkTituloAbaMin.addActionListener(e -> Preferencias.setTituloAbaMin(chkTituloAbaMin.isSelected()));

		chkAreaTransTabelaRegistros.addActionListener(
				e -> Preferencias.setAreaTransTabelaRegistros(chkAreaTransTabelaRegistros.isSelected()));

		txtFormDialogo.addActionListener(e -> Preferencias.setFormDialogo(txtFormDialogo.getText()));
		txtFormFicha.addActionListener(e -> Preferencias.setFormFicha(txtFormFicha.getText()));

		txtDefinirLargura.addActionListener(e -> definirLargura());
		txtDefinirAltura.addActionListener(e -> definirAltura());

		txtFormFichaDialogo.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				Preferencias.setFormFichaDialogo(txtFormFichaDialogo.getText());
			}
		});

		txtFormDialogo.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				Preferencias.setFormDialogo(txtFormDialogo.getText());
			}
		});

		txtFormFicha.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				Preferencias.setFormFicha(txtFormFicha.getText());
			}
		});

		txtDefinirLargura.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				definirLargura();
			}
		});

		txtDefinirAltura.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				definirAltura();
			}
		});
	}

	private void definirLargura() {
		int porcentagem = Util.getInt(txtDefinirLargura.getText(), Preferencias.getPorcHorizontalLocalForm());
		formulario.definirLarguraEmPorcentagem(porcentagem);
		Preferencias.setPorcHorizontalLocalForm(porcentagem);
	}

	private void definirAltura() {
		int porcentagem = Util.getInt(txtDefinirAltura.getText(), Preferencias.getPorcVerticalLocalForm());
		formulario.definirAlturaEmPorcentagem(porcentagem);
		Preferencias.setPorcVerticalLocalForm(porcentagem);
	}

	private class NomeValor {
		private static final byte INTERVALO_COMPARA = 5;
		private static final byte INTERVALO_AUTO = 2;
		private static final byte POSICAO_ABA = 1;
		private static final byte DESTACADOS = 3;
		private static final byte LAYOUTS = 4;
		private final String nome;
		private final int valor;
		private final int tipo;

		private NomeValor(String chave, int valor, int tipo) {
			this.nome = Mensagens.getString(chave);
			this.valor = valor;
			this.tipo = tipo;
		}
	}

	private class RadioPosicao extends JRadioButton {
		private static final long serialVersionUID = 1L;
		private final transient NomeValor nomeValor;

		private RadioPosicao(NomeValor nomeValor) {
			super(nomeValor.nome);
			this.nomeValor = nomeValor;

			addActionListener(e -> {
				if (nomeValor.tipo == NomeValor.POSICAO_ABA) {
					Preferencias.setPosicaoAbaFichario(nomeValor.valor);
					formulario.getFichario().setTabPlacement(Preferencias.getPosicaoAbaFichario());

				} else if (nomeValor.tipo == NomeValor.INTERVALO_AUTO) {
					Preferencias.setIntervaloPesquisaAuto(nomeValor.valor);

				} else if (nomeValor.tipo == NomeValor.INTERVALO_COMPARA) {
					Preferencias.setIntervaloComparacao(nomeValor.valor);

				} else if (nomeValor.tipo == NomeValor.DESTACADOS) {
					Preferencias.setTipoContainerPesquisaAuto(nomeValor.valor);

				} else if (nomeValor.tipo == NomeValor.LAYOUTS) {
					Preferencias.setLayoutAbertura(nomeValor.valor);
				}
			});
		}
	}

	@Override
	public void setJanela(IJanela janela) {
		toolbar.setJanela(janela);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;

		public void ini(IJanela janela) {
			super.ini(janela, false, true);
			configButtonDestacar(e -> destacarEmFormulario(), e -> abrirEmFormulario(), e -> retornoAoFichario(),
					e -> clonarEmFormulario());
			configAbrirAutoFichario(Constantes.ABRIR_AUTO_FICHARIO_CONFIGURACAO);
		}

		@Override
		protected void salvar() {
			Preferencias.salvar();
		}
	}

	private class PainelCorTotalRecente extends Panel {
		private static final long serialVersionUID = 1L;
		private Label labelAntesProcessar = new Label("label.cor_antes_processar");
		private Label labelBuscarTotal = new Label("label.cor_total_atual");
		private Label labelComparacao = new Label("label.cor_comparacao");
		private transient MouseInner mouseInner = new MouseInner();

		private PainelCorTotalRecente() {
			super(new GridLayout(0, 3));

			add(labelAntesProcessar);
			add(labelBuscarTotal);
			add(labelComparacao);

			labelAntesProcessar.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			labelAntesProcessar.setForeground(Preferencias.getCorAntesTotalRecente());
			labelBuscarTotal.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			labelComparacao.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			labelBuscarTotal.setForeground(Preferencias.getCorTotalAtual());
			labelComparacao.setForeground(Preferencias.getCorComparaRec());

			labelAntesProcessar.setHorizontalAlignment(Label.CENTER);
			labelBuscarTotal.setHorizontalAlignment(Label.CENTER);
			labelComparacao.setHorizontalAlignment(Label.CENTER);

			labelAntesProcessar.addMouseListener(mouseInner);
			labelBuscarTotal.addMouseListener(mouseInner);
			labelComparacao.addMouseListener(mouseInner);
		}

		private class MouseInner extends MouseAdapter {
			@Override
			public void mouseClicked(MouseEvent e) {
				Label label = (Label) e.getSource();

				Color color = JColorChooser.showDialog(ConfiguracaoContainer.this, label.getText(),
						label.getForeground());

				if (color == null) {
					return;
				}

				label.setForeground(color);

				if (label == labelAntesProcessar) {
					Preferencias.setCorAntesTotalRecente(color);
				} else if (label == labelBuscarTotal) {
					Preferencias.setCorTotalAtual(color);
				} else if (label == labelComparacao) {
					Preferencias.setCorComparaRec(color);
				}
			}
		}
	}

	@Override
	protected void destacarEmFormulario() {
		if (formulario.excluirFicharioAba(this)) {
			ConfiguracaoFormulario.criar(formulario, this);
		}
	}

	@Override
	protected void clonarEmFormulario() {
		if (formulario.excluirFicharioAba(this)) {
			ConfiguracaoFormulario.criar(formulario);
		}
	}

	@Override
	protected void abrirEmFormulario() {
		ConfiguracaoFormulario.criar(formulario);
	}

	@Override
	protected void retornoAoFichario() {
		if (configuracaoFormulario != null) {
			configuracaoFormulario.retornoAoFichario();
			formulario.adicionarFicharioAba(this);
		}
	}

	@Override
	public File getFileSalvarAberto() {
		return new File(getClasseFabricaEContainerDetalhe());
	}

	@Override
	public String getClasseFabricaEContainerDetalhe() {
		return classeFabricaEContainer(ConfiguracaoFabrica.class, ConfiguracaoContainer.class);
	}

	@Override
	public String getChaveTituloMin() {
		return Constantes.LABEL_CONFIGURACOES_MIN;
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public String getChaveTitulo() {
		return Constantes.LABEL_CONFIGURACOES;
	}

	@Override
	public String getHintTitulo() {
		return Mensagens.getString(Constantes.LABEL_CONFIGURACOES);
	}

	@Override
	public Icon getIcone() {
		return Icones.CONFIG;
	}
}