package br.com.persist.formulario;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import br.com.persist.abstrato.AbstratoConfiguracao;
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
	private static final long serialVersionUID = 1L;
	private final CheckBox chkAplicarLarguraAoAbrirArquivo = criarCheckBox("label.aplicar_largura_ao_abrir_arq_objeto");
	private final CheckBox chkAplicarAlturaAoAbrirArquivo = criarCheckBox("label.aplicar_altura_ao_abrir_arq_objeto");
	private final CheckBox chkFecharComESCFormulario = criarCheckBox("label.fechar_com_esc_formulario");
	private final CheckBox chkFecharComESCInternal = criarCheckBox("label.fechar_com_esc_internal");
	private final CheckBox chkFecharComESCDialogo = criarCheckBox("label.fechar_com_esc_dialogo");
	private final CheckBox chkMonitorPreferencial = criarCheckBox("label.monitor_preferencial");
	private final CheckBox chkFicharioScroll = new CheckBox("label.fichario_scroll");
	private final CheckBox chkTituloAbaMin = new CheckBox("label.titulo_aba_min");
	private final TextField txtFormFichaDialogo = new TextField();
	private final TextField txtDefinirLargura = new TextField();
	private final TextField txtDefinirAltura = new TextField();
	private final TextField txtFormDialogo = new TextField();
	private final TextField txtFormFicha = new TextField();
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

	private void montarLayout() {
		PanelCenter panelPosicoes = criarPainelGrupo(posicoes, Preferencias.getPosicaoAbaFichario());

		chkAplicarLarguraAoAbrirArquivo.setSelected(Preferencias.isAplicarLarguraAoAbrirArquivoObjeto());
		chkAplicarAlturaAoAbrirArquivo.setSelected(Preferencias.isAplicarAlturaAoAbrirArquivoObjeto());
		chkFecharComESCFormulario.setSelected(Preferencias.isFecharComESCFormulario());
		chkFecharComESCInternal.setSelected(Preferencias.isFecharComESCInternal());
		txtDefinirLargura.setText("" + Preferencias.getPorcHorizontalLocalForm());
		chkFecharComESCDialogo.setSelected(Preferencias.isFecharComESCDialogo());
		chkMonitorPreferencial.setSelected(Preferencias.isMonitorPreferencial());
		txtDefinirAltura.setText("" + Preferencias.getPorcVerticalLocalForm());
		chkFicharioScroll.setSelected(Preferencias.isFicharioComRolagem());
		txtFormFichaDialogo.setText(Preferencias.getFormFichaDialogo());
		chkTituloAbaMin.setSelected(Preferencias.isTituloAbaMin());
		txtFormDialogo.setText(Preferencias.getFormDialogo());
		txtFormFicha.setText(Preferencias.getFormFicha());

		Muro muro = new Muro();
		Label tituloLocalAbas = criarLabelTitulo("label.local_abas");
		Label email = criarLabelTitulo("contato");
		email.setText(email.getText() + " - " + Mensagens.getString("versao"));
		muro.camada(panelS(email, tituloLocalAbas, panelPosicoes));
		muro.camada(panelS(new PanelCenter(new Label("label.form_ficha_dialogo"), txtFormFichaDialogo),
				new PanelCenter(new Label("label.form_dialogo"), txtFormDialogo),
				new PanelCenter(new Label("label.form_ficha"), txtFormFicha)));
		muro.camada(panelS(new PanelCenter(new Label("label.definir_largura"), txtDefinirLargura),
				new PanelCenter(new Label("label.definir_altura"), txtDefinirAltura)));
		muro.camada(panel(0, 0, chkAplicarLarguraAoAbrirArquivo, chkAplicarAlturaAoAbrirArquivo,
				chkFecharComESCFormulario, chkFecharComESCInternal, chkFecharComESCDialogo, chkTituloAbaMin,
				chkFicharioScroll, chkMonitorPreferencial));
		if (Preferencias.isMonitorPreferencial()) {
			muro.camada(panelN(criarLabelTituloRotulo("label.monitor_preferencial"), new PainelMonitorPreferencial()));
		}
		add(BorderLayout.CENTER, muro);
	}

	public static Panel panel(int top, int bottom, Component... comps) {
		Panel container = new Panel(new GridLayout(0, 1));
		container.setBorder(BorderFactory.createMatteBorder(top, 0, bottom, 0, Color.GRAY));
		for (Component c : comps) {
			container.add(c);
		}
		return container;
	}

	public static Panel panelN(Component... comps) {
		return panel(1, 0, comps);
	}

	public static Panel panelS(Component... comps) {
		return panel(0, 1, comps);
	}

	private void configurar() {
		chkFicharioScroll.addActionListener(e -> {
			Preferencias.setFicharioComRolagem(chkFicharioScroll.isSelected());
			formulario.setTabLayoutPolicy(
					Preferencias.isFicharioComRolagem() ? JTabbedPane.SCROLL_TAB_LAYOUT : JTabbedPane.WRAP_TAB_LAYOUT);
		});
		chkFecharComESCFormulario
				.addActionListener(e -> Preferencias.setFecharComESCFormulario(chkFecharComESCFormulario.isSelected()));
		chkFecharComESCInternal
				.addActionListener(e -> Preferencias.setFecharComESCInternal(chkFecharComESCInternal.isSelected()));
		txtFormFichaDialogo.addActionListener(e -> Preferencias.setFormFichaDialogo(txtFormFichaDialogo.getText()));
		chkFecharComESCDialogo
				.addActionListener(e -> Preferencias.setFecharComESCDialogo(chkFecharComESCDialogo.isSelected()));
		chkMonitorPreferencial
				.addActionListener(e -> Preferencias.setMonitorPreferencial(chkMonitorPreferencial.isSelected()));
		chkAplicarLarguraAoAbrirArquivo.addActionListener(
				e -> Preferencias.setAplicarLarguraAoAbrirArquivoObjeto(chkAplicarLarguraAoAbrirArquivo.isSelected()));
		chkTituloAbaMin.addActionListener(e -> Preferencias.setTituloAbaMin(chkTituloAbaMin.isSelected()));
		chkAplicarAlturaAoAbrirArquivo.addActionListener(
				e -> Preferencias.setAplicarAlturaAoAbrirArquivoObjeto(chkAplicarAlturaAoAbrirArquivo.isSelected()));
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

	private Label criarLabelTituloRotulo(String rotulo) {
		return criarLabelTitulo(FormularioMensagens.getString(rotulo), false);
	}

	private Label criarLabelTitulo(String rotulo, boolean chaveRotulo) {
		Label label = new Label(rotulo, chaveRotulo);
		label.setHorizontalAlignment(Label.CENTER);
		return label;
	}

	private Label criarLabelTitulo(String rotulo) {
		return criarLabelTitulo(rotulo, true);
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