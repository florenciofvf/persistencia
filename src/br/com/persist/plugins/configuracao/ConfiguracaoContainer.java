package br.com.persist.plugins.configuracao;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
import static br.com.persist.componente.BarraButtonEnum.DESTACAR_EM_FORMULARIO;
import static br.com.persist.componente.BarraButtonEnum.RETORNAR_AO_FICHARIO;
import static br.com.persist.componente.BarraButtonEnum.SALVAR;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Button;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Label;
import br.com.persist.componente.Panel;
import br.com.persist.componente.PanelCenter;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.TextField;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;

public class ConfiguracaoContainer extends AbstratoContainer {
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
	private final CheckBox chkFecharComESCFormulario = new CheckBox("label.fechar_com_esc_formulario");
	private final CheckBox chkFecharOrigemAposSoltar = new CheckBox("label.fechar_origem_apos_soltar");
	private final CheckBox chkNomeColunaListener = new CheckBox("label.copiar_nome_coluna_listener");
	private final CheckBox chkHabitInnerJoinsObj = new CheckBox("label.habilitadoInnerJoinsObjeto");
	private final CheckBox chkFecharComESCInternal = new CheckBox("label.fechar_com_esc_internal");
	private final CheckBox chkAtivarAbrirAutoDestac = new CheckBox("label.abrir_auto_destacado");
	private final CheckBox chkFecharComESCDialogo = new CheckBox("label.fechar_com_esc_dialogo");
	private final CheckBox chkMonitorPreferencial = new CheckBox("label.monitor_preferencial");
	private final CheckBox chkAtivarAbrirAuto = new CheckBox("label.ativar_abrir_auto");
	private final CheckBox chkFicharioScroll = new CheckBox("label.fichario_scroll");
	private final CheckBox chkNomearArrasto = new CheckBox("label.nomear_arrasto");
	private final CheckBox chkTituloAbaMin = new CheckBox("label.titulo_aba_min");
	private final ButtonGroup grupoTiposContainer = new ButtonGroup();
	private final TextField txtFormFichaDialogo = new TextField();
	private final TextField txtDefinirLargura = new TextField();
	private final TextField txtDefinirAltura = new TextField();
	private final TextField txtFormDialogo = new TextField();
	private final TextField txtFormFicha = new TextField();
	private ConfiguracaoFormulario configuracaoFormulario;
	private ConfiguracaoDialogo configuracaoDialogo;
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

	private final transient NomeValor[] destacados = {
			new NomeValor("label.formulario", Constantes.TIPO_CONTAINER_FORMULARIO, NomeValor.DESTACADOS),
			new NomeValor("label.fichario", Constantes.TIPO_CONTAINER_FICHARIO, NomeValor.DESTACADOS),
			new NomeValor("label.desktop", Constantes.TIPO_CONTAINER_DESKTOP, NomeValor.DESTACADOS) };

	private final transient NomeValor[] posicoes = {
			new NomeValor("label.acima", SwingConstants.TOP, NomeValor.POSICAO_ABA),
			new NomeValor("label.esquerdo", SwingConstants.LEFT, NomeValor.POSICAO_ABA),
			new NomeValor("label.abaixo", SwingConstants.BOTTOM, NomeValor.POSICAO_ABA),
			new NomeValor("label.direito", SwingConstants.RIGHT, NomeValor.POSICAO_ABA) };

	public ConfiguracaoContainer(Janela janela, Formulario formulario) {
		super(formulario);
		toolbar.ini(janela);
		montarLayout();
		configurar();
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
		PanelCenter panelIntervalosCompara = criarPainelGrupo(intervalosCompara, Preferencias.getIntervaloComparacao());
		PanelCenter panelDestacados = criarPainelGrupoDestac(destacados, Preferencias.getTipoContainerPesquisaAuto());
		PanelCenter panelIntervalos = criarPainelGrupo(intervalos, Preferencias.getIntervaloPesquisaAuto());
		PanelCenter panelPosicoes = criarPainelGrupo(posicoes, Preferencias.getPosicaoAbaFichario());

		chkAplicarLarguraAoAbrirArquivoObjeto.setSelected(Preferencias.isAplicarLarguraAoAbrirArquivoObjeto());
		chkAplicarAlturaAoAbrirArquivoObjeto.setSelected(Preferencias.isAplicarAlturaAoAbrirArquivoObjeto());
		chkExecAposCopiarColunaConcatenado.setSelected(Preferencias.isExecAposCopiarColunaConcatenado());
		chkExecAposBaixarParaComplemento.setSelected(Preferencias.isExecAposBaixarParaComplemento());
		chkAreaTransTabelaRegistros.setSelected(Preferencias.isAreaTransTabelaRegistros());
		chkFecharComESCFormulario.setSelected(Preferencias.isFecharComESCFormulario());
		chkHabitInnerJoinsObj.setSelected(Preferencias.isHabilitadoInnerJoinsObjeto());
		chkNomeColunaListener.setSelected(Preferencias.isCopiarNomeColunaListener());
		chkFecharComESCInternal.setSelected(Preferencias.isFecharComESCInternal());
		chkAtivarAbrirAutoDestac.setSelected(Preferencias.isAbrirAutoDestacado());
		txtDefinirLargura.setText("" + Preferencias.getPorcHorizontalLocalForm());
		chkFecharComESCDialogo.setSelected(Preferencias.isFecharComESCDialogo());
		chkFecharOrigemAposSoltar.setSelected(Preferencias.isFecharAposSoltar());
		chkMonitorPreferencial.setSelected(Preferencias.isMonitorPreferencial());
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
		Label tituloLocalAbas = criarLabelTitulo("label.local_abas");
		Label email = criarLabelTitulo("contato");
		email.setText(email.getText() + " - " + Mensagens.getString("versao"));

		Panel container = new Panel(new GridLayout(0, 1));
		container.add(email);
		container.add(tituloLocalAbas);
		container.add(panelPosicoes);
		container.add(new JSeparator());
		container.add(tituloIntervalo);
		container.add(panelIntervalos);
		container.add(new JSeparator());
		container.add(tituloIntervaloCompara);
		container.add(panelIntervalosCompara);
		container.add(criarLabelTitulo("label.titulo_cor_total_recente"));
		container.add(new PainelCorTotalRecente());
		container.add(new JSeparator());
		if (Preferencias.isMonitorPreferencial()) {
			container.add(criarLabelTitulo("label.monitor_preferencial"));
			container.add(new PainelMonitorPreferencial());
			container.add(new JSeparator());
		}
		container.add(chkAreaTransTabelaRegistros);
		container.add(chkExecAposBaixarParaComplemento);
		container.add(chkNomeColunaListener);
		container.add(chkExecAposCopiarColunaConcatenado);
		container.add(chkFecharOrigemAposSoltar);
		container.add(chkHabitInnerJoinsObj);
		container.add(chkNomearArrasto);
		container.add(chkFecharComESCFormulario);
		container.add(chkFecharComESCInternal);
		container.add(chkFecharComESCDialogo);
		container.add(chkTituloAbaMin);
		container.add(chkFicharioScroll);
		container.add(chkMonitorPreferencial);
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
		container.add(new PanelCenter(new Label("label.definir_altura"), txtDefinirAltura));
		container.add(new PanelCenter(chkAplicarAlturaAoAbrirArquivoObjeto));
		container.add(new PanelCenter(new Label("label.definir_largura"), txtDefinirLargura));
		container.add(new PanelCenter(chkAplicarLarguraAoAbrirArquivoObjeto));

		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, new ScrollPane(container));

		Insets insets2 = new Insets(5, 30, 5, 5);
		Insets insets = new Insets(5, 10, 5, 5);

		chkExecAposCopiarColunaConcatenado.setMargin(insets2);
		chkExecAposBaixarParaComplemento.setMargin(insets);
		chkAreaTransTabelaRegistros.setMargin(insets);
		chkFecharComESCFormulario.setMargin(insets);
		chkFecharOrigemAposSoltar.setMargin(insets);
		chkAtivarAbrirAutoDestac.setMargin(insets2);
		chkFecharComESCInternal.setMargin(insets);
		chkMonitorPreferencial.setMargin(insets);
		chkFecharComESCDialogo.setMargin(insets);
		chkHabitInnerJoinsObj.setMargin(insets);
		chkNomeColunaListener.setMargin(insets);
		chkAtivarAbrirAuto.setMargin(insets);
		chkFicharioScroll.setMargin(insets);
		chkNomearArrasto.setMargin(insets);
		chkTituloAbaMin.setMargin(insets);
	}

	private void checkPesquisa() {
		chkAtivarAbrirAutoDestac.setEnabled(chkAtivarAbrirAuto.isSelected());
		if (!chkAtivarAbrirAuto.isSelected()) {
			chkAtivarAbrirAutoDestac.setSelected(false);
		}
		boolean habilitado = chkAtivarAbrirAuto.isSelected() && !chkAtivarAbrirAutoDestac.isSelected();
		Enumeration<AbstractButton> elements = grupoTiposContainer.getElements();
		while (elements.hasMoreElements()) {
			elements.nextElement().setEnabled(habilitado);
		}
	}

	private void checkCopiarColuna() {
		chkExecAposCopiarColunaConcatenado.setEnabled(chkNomeColunaListener.isSelected());
		if (!chkNomeColunaListener.isSelected()) {
			chkExecAposCopiarColunaConcatenado.setSelected(false);
		}
	}

	private PanelCenter criarPainelGrupoDestac(NomeValor[] nomeValores, int padrao) {
		PanelCenter panel = new PanelCenter();
		for (int i = 0; i < nomeValores.length; i++) {
			RadioPosicao radio = new RadioPosicao(nomeValores[i]);
			radio.setSelected(radio.nomeValor.valor == padrao);
			radio.setMargin(new Insets(5, 10, 5, 5));
			panel.add(radio);
			grupoTiposContainer.add(radio);
		}
		return panel;
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
			formulario.setTabLayoutPolicy(
					Preferencias.isFicharioComRolagem() ? JTabbedPane.SCROLL_TAB_LAYOUT : JTabbedPane.WRAP_TAB_LAYOUT);
		});

		chkAplicarLarguraAoAbrirArquivoObjeto.addActionListener(e -> Preferencias
				.setAplicarLarguraAoAbrirArquivoObjeto(chkAplicarLarguraAoAbrirArquivoObjeto.isSelected()));

		chkAplicarAlturaAoAbrirArquivoObjeto.addActionListener(e -> Preferencias
				.setAplicarAlturaAoAbrirArquivoObjeto(chkAplicarAlturaAoAbrirArquivoObjeto.isSelected()));

		chkHabitInnerJoinsObj
				.addActionListener(e -> Preferencias.setHabilitadoInnerJoinsObjeto(chkHabitInnerJoinsObj.isSelected()));

		chkFecharComESCFormulario
				.addActionListener(e -> Preferencias.setFecharComESCFormulario(chkFecharComESCFormulario.isSelected()));

		chkExecAposCopiarColunaConcatenado.addActionListener(
				e -> Preferencias.setExecAposCopiarColunaConcatenado(chkExecAposCopiarColunaConcatenado.isSelected()));

		txtFormFichaDialogo.addActionListener(e -> Preferencias.setFormFichaDialogo(txtFormFichaDialogo.getText()));

		chkFecharComESCInternal
				.addActionListener(e -> Preferencias.setFecharComESCInternal(chkFecharComESCInternal.isSelected()));

		chkExecAposBaixarParaComplemento.addActionListener(
				e -> Preferencias.setExecAposBaixarParaComplemento(chkExecAposBaixarParaComplemento.isSelected()));

		chkFecharComESCDialogo
				.addActionListener(e -> Preferencias.setFecharComESCDialogo(chkFecharComESCDialogo.isSelected()));

		chkFecharOrigemAposSoltar
				.addActionListener(e -> Preferencias.setFecharAposSoltar(chkFecharOrigemAposSoltar.isSelected()));

		chkMonitorPreferencial
				.addActionListener(e -> Preferencias.setMonitorPreferencial(chkMonitorPreferencial.isSelected()));

		chkNomearArrasto.addActionListener(e -> Preferencias.setNomearArrasto(chkNomearArrasto.isSelected()));

		chkTituloAbaMin.addActionListener(e -> Preferencias.setTituloAbaMin(chkTituloAbaMin.isSelected()));

		chkAreaTransTabelaRegistros.addActionListener(
				e -> Preferencias.setAreaTransTabelaRegistros(chkAreaTransTabelaRegistros.isSelected()));

		txtFormDialogo.addActionListener(e -> Preferencias.setFormDialogo(txtFormDialogo.getText()));
		txtFormFicha.addActionListener(e -> Preferencias.setFormFicha(txtFormFicha.getText()));

		chkNomeColunaListener.addActionListener(e -> {
			Preferencias.setCopiarNomeColunaListener(chkNomeColunaListener.isSelected());
			checkCopiarColuna();
		});

		chkAtivarAbrirAutoDestac.addActionListener(e -> {
			Preferencias.setAbrirAutoDestacado(chkAtivarAbrirAutoDestac.isSelected());
			checkPesquisa();
		});

		chkAtivarAbrirAuto.addActionListener(e -> {
			Preferencias.setAbrirAuto(chkAtivarAbrirAuto.isSelected());
			checkPesquisa();
		});

		txtFormFichaDialogo.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				Preferencias.setFormFichaDialogo(txtFormFichaDialogo.getText());
			}
		});

		txtDefinirLargura.addActionListener(e -> definirLargura());
		txtDefinirAltura.addActionListener(e -> definirAltura());

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
					formulario.setTabPlacement(Preferencias.getPosicaoAbaFichario());
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

	private class PainelMonitorPreferencial extends Panel {
		private static final long serialVersionUID = 1L;
		private Button buttonNaoPreferencial = new Button("label.nao_preferencial");
		private Button buttonPreferencial = new Button("label.preferencial");

		private PainelMonitorPreferencial() {
			super(new FlowLayout());
			add(buttonPreferencial);
			add(buttonNaoPreferencial);
			buttonPreferencial.addActionListener(e -> formulario.salvarMonitorComoPreferencial());
			buttonNaoPreferencial.addActionListener(e -> formulario.excluirMonitorComoPreferencial());
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
			buttonDestacar.estadoFormulario();
			checkCopiarColuna();
			checkPesquisa();
		}

		void paginaVisivel() {
			buttonDestacar.estadoFichario();
			checkCopiarColuna();
			checkPesquisa();
		}

		void dialogoVisivel() {
			buttonDestacar.estadoDialogo();
			checkCopiarColuna();
			checkPesquisa();
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
}