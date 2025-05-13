package br.com.persist.formulario;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import br.com.persist.abstrato.AbstratoConfiguracao;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Muro;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Button;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.Label;
import br.com.persist.componente.Panel;
import br.com.persist.componente.PanelCenter;
import br.com.persist.componente.TextField;

public class FormularioConfiguracao extends AbstratoConfiguracao {
	protected static final String[] TAMANHOS = { "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "18",
			"20", "22" };
	protected static final String[] FONTES = GraphicsEnvironment.getLocalGraphicsEnvironment()
			.getAvailableFontFamilyNames();
	private final CheckBox chkAplicarLarguraAoAbrirArquivo = criarCheckBox("label.aplicar_largura_ao_abrir_arq_objeto");
	private final CheckBox chkAplicarAlturaAoAbrirArquivo = criarCheckBox("label.aplicar_altura_ao_abrir_arq_objeto");
	private final CheckBox chkFecharComESCFormulario = criarCheckBox("label.fechar_com_esc_formulario");
	private final CheckBox chkAbrirFormularioDireita = criarCheckBox("label.abrir_formulario_direita");
	private final CheckBox chkAbrirFormularioAbaixo = criarCheckBox("label.abrir_formulario_abaixo");
	private final CheckBox chkFecharComESCInternal = criarCheckBox("label.fechar_com_esc_internal");
	private final CheckBox chkFecharComESCDialogo = criarCheckBox("label.fechar_com_esc_dialogo");
	private final CheckBox chkMonitorPreferencial = criarCheckBox("label.monitor_preferencial");
	private final Button buttonAplicarLA = criarButton("label.aplicar_largura_altura");
	private final CheckBox chkFicharioScroll = criarCheckBox("label.fichario_scroll");
	private final CheckBox chkDesenharERTEditor = criarCheckBox("label.desenhar_ERT");
	private final CheckBox chkTituloAbaMin = criarCheckBox("label.titulo_aba_min");
	private JComboBox<String> comboSize = new JComboBox<>(TAMANHOS);
	private JComboBox<String> comboFontes = new JComboBox<>(FONTES);
	private final TextField txtFormFichaDialogo = new TextField();
	private final TextField txtDimensaoMensagem = new TextField();
	private final TextField txtDefinirLargura = new TextField();
	private final Button buttonConectaDesconecta = new Button();
	private final TextField txtDefinirAltura = new TextField();
	private final TextField txtFormDialogo = new TextField();
	private final TextField txtFormFicha = new TextField();
	private static final long serialVersionUID = 1L;
	private final transient NomeValor[] posicoes = {
			new NomeValor("label.acima", SwingConstants.TOP, NomeValor.POSICAO_ABA),
			new NomeValor("label.esquerdo", SwingConstants.LEFT, NomeValor.POSICAO_ABA),
			new NomeValor("label.abaixo", SwingConstants.BOTTOM, NomeValor.POSICAO_ABA),
			new NomeValor("label.direito", SwingConstants.RIGHT, NomeValor.POSICAO_ABA) };

	public FormularioConfiguracao(Formulario formulario) {
		super(formulario, Mensagens.getTituloAplicacao());
		montarLayout();
		configurar();
	}

	static CheckBox criarCheckBox(String chaveRotulo) {
		return new CheckBox(FormularioMensagens.getString(chaveRotulo), false);
	}

	static Button criarButton(String chaveRotulo) {
		return new Button(FormularioMensagens.getString(chaveRotulo), false);
	}

	private void montarLayout() {
		chkAplicarLarguraAoAbrirArquivo.setSelected(Preferencias.isAplicarLarguraAoAbrirArquivoObjeto());
		chkAplicarAlturaAoAbrirArquivo.setSelected(Preferencias.isAplicarAlturaAoAbrirArquivoObjeto());
		PanelCenter panelPosicoes = criarPainelGrupo(posicoes, Preferencias.getPosicaoAbaFichario());
		chkAbrirFormularioDireita.setSelected(Preferencias.isAbrirFormularioDireita());
		chkFecharComESCFormulario.setSelected(Preferencias.isFecharComESCFormulario());
		chkAbrirFormularioAbaixo.setSelected(Preferencias.isAbrirFormularioAbaixo());
		chkDesenharERTEditor.setSelected(Preferencias.isDesenharEspacoRetornoTab());
		chkFecharComESCInternal.setSelected(Preferencias.isFecharComESCInternal());
		txtDefinirLargura.setText("" + Preferencias.getPorcHorizontalLocalForm());
		chkFecharComESCDialogo.setSelected(Preferencias.isFecharComESCDialogo());
		chkMonitorPreferencial.setSelected(Preferencias.isMonitorPreferencial());
		txtDefinirAltura.setText("" + Preferencias.getPorcVerticalLocalForm());
		chkFicharioScroll.setSelected(Preferencias.isFicharioComRolagem());
		txtFormFichaDialogo.setText(Preferencias.getFormFichaDialogo());
		txtDimensaoMensagem.setText(Preferencias.getDimensaoMensagem());
		chkTituloAbaMin.setSelected(Preferencias.isTituloAbaMin());
		txtFormDialogo.setText(Preferencias.getFormDialogo());
		txtFormFicha.setText(Preferencias.getFormFicha());

		Font font = Preferencias.getFontPreferencia();
		if (font != null) {
			comboFontes.setSelectedItem(font.getName());
			comboSize.setSelectedItem("" + font.getSize());
		}

		Muro muro = new Muro();
		Label tituloLocalAbas = criarLabelTituloRotulo("label.local_abas");
		Label email = criarLabelTitulo("contato");
		email.setText(email.getText() + " - " + Mensagens.getString("versao"));
		muro.camada(Muro.panelGridBorderBottom(email, tituloLocalAbas, panelPosicoes));
		muro.camada(
				Muro.panelGridBorderBottom(new PanelCenter(criarLabel("label.form_ficha_dialogo"), txtFormFichaDialogo),
						new PanelCenter(criarLabel("label.form_dialogo"), txtFormDialogo),
						new PanelCenter(criarLabel("label.form_ficha"), txtFormFicha),
						new PanelCenter(criarLabel("label.dimensao_mensagem"), txtDimensaoMensagem)));
		muro.camada(Muro.panelGridBorderBottom(new PanelCenter(criarLabel("label.definir_largura"), txtDefinirLargura),
				new PanelCenter(criarLabel("label.definir_altura"), txtDefinirAltura),
				new PanelCenter(buttonAplicarLA)));
		muro.camada(Muro.panelGridBorderBottom(new PanelCenter(buttonConectaDesconecta)));
		muro.camada(Muro.panelGrid(chkAplicarLarguraAoAbrirArquivo, chkAplicarAlturaAoAbrirArquivo,
				chkDesenharERTEditor, chkMonitorPreferencial, chkAbrirFormularioDireita, chkAbrirFormularioAbaixo,
				chkFecharComESCFormulario, chkFecharComESCInternal, chkFecharComESCDialogo, chkTituloAbaMin,
				chkFicharioScroll));
		if (Preferencias.isMonitorPreferencial()) {
			muro.camada(Muro.panelGridBorderTop(criarLabelTituloRotulo("label.monitor_preferencial"),
					new PainelMonitorPreferencial()));
		}
		muro.camada(Muro.panelGridBorderTop(
				new PanelCenter(criarLabel("label.fonte"), comboFontes, criarLabel("label.tamanho"), comboSize)));
		add(BorderLayout.CENTER, muro);
	}

	private void configurar() {
		comboFontes.addItemListener(FormularioConfiguracao.this::alterarFonteNome);
		comboSize.addItemListener(FormularioConfiguracao.this::alterarFonteSize);
		chkFicharioScroll.addActionListener(e -> {
			Preferencias.setFicharioComRolagem(chkFicharioScroll.isSelected());
			formulario.setTabLayoutPolicy(
					Preferencias.isFicharioComRolagem() ? JTabbedPane.SCROLL_TAB_LAYOUT : JTabbedPane.WRAP_TAB_LAYOUT);
		});
		chkDesenharERTEditor
				.addActionListener(e -> Preferencias.setDesenharEspacoRetornoTab(chkDesenharERTEditor.isSelected()));
		chkFecharComESCFormulario
				.addActionListener(e -> Preferencias.setFecharComESCFormulario(chkFecharComESCFormulario.isSelected()));
		chkFecharComESCInternal
				.addActionListener(e -> Preferencias.setFecharComESCInternal(chkFecharComESCInternal.isSelected()));
		txtFormFichaDialogo.addActionListener(e -> Preferencias.setFormFichaDialogo(txtFormFichaDialogo.getText()));
		txtDimensaoMensagem.addActionListener(e -> Preferencias.setDimensaoMensagem(txtDimensaoMensagem.getText()));
		chkFecharComESCDialogo
				.addActionListener(e -> Preferencias.setFecharComESCDialogo(chkFecharComESCDialogo.isSelected()));
		chkMonitorPreferencial
				.addActionListener(e -> Preferencias.setMonitorPreferencial(chkMonitorPreferencial.isSelected()));
		chkAplicarLarguraAoAbrirArquivo.addActionListener(
				e -> Preferencias.setAplicarLarguraAoAbrirArquivoObjeto(chkAplicarLarguraAoAbrirArquivo.isSelected()));
		chkAbrirFormularioDireita
				.addActionListener(e -> Preferencias.setAbrirFormularioDireita(chkAbrirFormularioDireita.isSelected()));
		chkAbrirFormularioAbaixo
				.addActionListener(e -> Preferencias.setAbrirFormularioAbaixo(chkAbrirFormularioAbaixo.isSelected()));
		chkTituloAbaMin.addActionListener(e -> Preferencias.setTituloAbaMin(chkTituloAbaMin.isSelected()));
		chkAplicarAlturaAoAbrirArquivo.addActionListener(
				e -> Preferencias.setAplicarAlturaAoAbrirArquivoObjeto(chkAplicarAlturaAoAbrirArquivo.isSelected()));
		txtFormDialogo.addActionListener(e -> Preferencias.setFormDialogo(txtFormDialogo.getText()));
		txtFormFicha.addActionListener(e -> Preferencias.setFormFicha(txtFormFicha.getText()));
		buttonConectaDesconecta.addActionListener(e -> buttonConectaDesconectaHandler());
		txtDefinirLargura.addActionListener(e -> definirLargura());
		txtDefinirAltura.addActionListener(e -> definirAltura());
		txtFormFichaDialogo.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				Preferencias.setFormFichaDialogo(txtFormFichaDialogo.getText());
			}
		});
		txtDimensaoMensagem.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				Preferencias.setDimensaoMensagem(txtDimensaoMensagem.getText());
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
		buttonAplicarLA.addActionListener(e -> {
			definirLargura();
			definirAltura();
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
		buttonConectaDesconectaText();
	}

	private void alterarFonteNome(ItemEvent e) {
		if (ItemEvent.SELECTED == e.getStateChange()) {
			Object object = comboFontes.getSelectedItem();
			if (object instanceof String) {
				alterarNomeFonte((String) object);
			}
		}
	}

	private void alterarFonteSize(ItemEvent e) {
		if (ItemEvent.SELECTED == e.getStateChange()) {
			Object object = comboSize.getSelectedItem();
			if (object instanceof String) {
				alterarSizeFonte(Integer.parseInt((String) object));
			}
		}
	}

	private void alterarNomeFonte(String nome) {
		Font font = Preferencias.getFontPreferencia();
		int style = font != null ? font.getStyle() : 0;
		int size = font != null ? font.getSize() : 12;
		Preferencias.setFontPreferencia(new Font(nome, style, size));
	}

	private void alterarSizeFonte(int size) {
		Font font = Preferencias.getFontPreferencia();
		int style = font != null ? font.getStyle() : 0;
		String nome = font != null ? font.getName() : "Arial";
		Preferencias.setFontPreferencia(new Font(nome, style, size));
	}

	private void buttonConectaDesconectaHandler() {
		Preferencias.setDesconectado(!Preferencias.isDesconectado());
		buttonConectaDesconectaText();
	}

	private void buttonConectaDesconectaText() {
		buttonConectaDesconecta
				.setText("MANTER " + (Preferencias.isDesconectado() ? Constantes.CONECTADO : Constantes.DESCONECTADO));
		setBorder(BorderFactory.createTitledBorder(Mensagens.getTituloAplicacao()));
		formulario.atualizarTitulo();
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

	private Label criarLabelTituloRotulo(String rotulo) {
		return criarLabelTitulo(FormularioMensagens.getString(rotulo), false);
	}

	private Label criarLabelTitulo(String rotulo, boolean chaveRotulo) {
		Label label = new Label(rotulo, chaveRotulo);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		return label;
	}

	private Label criarLabelTitulo(String rotulo) {
		return criarLabelTitulo(rotulo, true);
	}

	private Label criarLabel(String chaveRotulo) {
		return new Label(FormularioMensagens.getString(chaveRotulo), false);
	}

	private class PainelMonitorPreferencial extends Panel {
		private Button buttonNaoPreferencial = new Button("label.nao_preferencial");
		private Button buttonNaoPrefFormular = new Button("label.nao_pref_formula");
		private Button buttonPreferencial = new Button("label.preferencial");
		private static final long serialVersionUID = 1L;

		private PainelMonitorPreferencial() {
			super(new FlowLayout());
			add(buttonPreferencial);
			add(buttonNaoPreferencial);
			add(buttonNaoPrefFormular);
			buttonPreferencial.addActionListener(e -> formulario.salvarMonitorComoPreferencial());
			buttonNaoPreferencial.addActionListener(e -> formulario.excluirMonitorComoPreferencial());
			buttonNaoPrefFormular.addActionListener(e -> formulario.excluirMonitorFormComoPreferencial());
		}
	}

	private class NomeValor {
		private static final byte POSICAO_ABA = 1;
		private final String nome;
		private final int valor;
		private final int tipo;

		private NomeValor(String chave, int valor, int tipo) {
			this.nome = Mensagens.getString(chave);
			this.valor = valor;
			this.tipo = tipo;
		}
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
				}
			});
		}
	}
}