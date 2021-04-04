package br.com.persist.plugins.objeto;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import javax.swing.JColorChooser;
import javax.swing.JRadioButton;

import br.com.persist.abstrato.AbstratoConfiguracao;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.Label;
import br.com.persist.componente.Panel;
import br.com.persist.componente.PanelCenter;
import br.com.persist.componente.TextField;
import br.com.persist.formulario.Formulario;

public class ObjetoConfiguracao extends AbstratoConfiguracao {
	private static final long serialVersionUID = 1L;
	private final CheckBox chkAplicarLarguraAoAbrirArquivoObjeto = new CheckBox(
			"label.aplicar_largura_ao_abrir_arq_objeto");
	private final CheckBox chkAplicarAlturaAoAbrirArquivoObjeto = new CheckBox(
			"label.aplicar_altura_ao_abrir_arq_objeto");
	private final CheckBox chkHabitInnerJoinsObj = new CheckBox("label.habilitadoInnerJoinsObjeto");
	private final CheckBox chkAtivarAbrirAutoDestac = new CheckBox("label.abrir_auto_destacado");
	private final CheckBox chkAtivarAbrirAuto = new CheckBox("label.ativar_abrir_auto");
	private final ButtonGroup grupoTiposContainer = new ButtonGroup();
	private final TextField txtDefinirLargura = new TextField();
	private final TextField txtDefinirAltura = new TextField();
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

	public ObjetoConfiguracao(Formulario formulario) {
		super(formulario, Mensagens.getString("label.plugin_objeto"));
		montarLayout();
		configurar();
	}

	private void montarLayout() {
		PanelCenter panelIntervalosCompara = criarPainelGrupo(intervalosCompara, Preferencias.getIntervaloComparacao());
		PanelCenter panelDestacados = criarPainelGrupoDestac(destacados, Preferencias.getTipoContainerPesquisaAuto());
		PanelCenter panelIntervalos = criarPainelGrupo(intervalos, Preferencias.getIntervaloPesquisaAuto());

		chkAplicarLarguraAoAbrirArquivoObjeto.setSelected(Preferencias.isAplicarLarguraAoAbrirArquivoObjeto());
		chkAplicarAlturaAoAbrirArquivoObjeto.setSelected(Preferencias.isAplicarAlturaAoAbrirArquivoObjeto());
		chkHabitInnerJoinsObj.setSelected(Preferencias.isHabilitadoInnerJoinsObjeto());
		chkAtivarAbrirAutoDestac.setSelected(Preferencias.isAbrirAutoDestacado());
		txtDefinirLargura.setText("" + Preferencias.getPorcHorizontalLocalForm());
		txtDefinirAltura.setText("" + Preferencias.getPorcVerticalLocalForm());
		chkAtivarAbrirAuto.setSelected(Preferencias.isAbrirAuto());

		Panel container = new Panel(new GridLayout(0, 1));
		Label tituloIntervaloCompara = criarLabelTitulo("label.intervalo_comparacao_titulo");
		Label tituloDestacado = criarLabelTitulo("label.tipo_container_pesquisa_auto");
		Label tituloIntervalo = criarLabelTitulo("label.intervalo_pesquisa_auto");
		container.add(panelS(tituloIntervalo, panelIntervalos));
		container.add(panelS(tituloIntervaloCompara, panelIntervalosCompara,
				criarLabelTitulo("label.titulo_cor_total_recente"), new PainelCorTotalRecente(),
				chkHabitInnerJoinsObj));
		container.add(panelS(chkAtivarAbrirAuto, chkAtivarAbrirAutoDestac, tituloDestacado, panelDestacados));
		container.add(panel(0, 0, new PanelCenter(new Label("label.definir_altura"), txtDefinirAltura),
				new PanelCenter(chkAplicarAlturaAoAbrirArquivoObjeto),
				new PanelCenter(new Label("label.definir_largura"), txtDefinirLargura),
				new PanelCenter(chkAplicarLarguraAoAbrirArquivoObjeto)));
		Insets insets = new Insets(5, 10, 5, 5);
		chkAtivarAbrirAutoDestac.setMargin(insets);
		add(BorderLayout.CENTER, container);
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

	public static Panel panel(int top, int bottom, Component... comps) {
		Panel container = new Panel(new GridLayout(0, 1));
		container.setBorder(BorderFactory.createMatteBorder(top, 0, bottom, 0, Color.BLACK));
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
		chkHabitInnerJoinsObj
				.addActionListener(e -> Preferencias.setHabilitadoInnerJoinsObjeto(chkHabitInnerJoinsObj.isSelected()));
		chkAplicarLarguraAoAbrirArquivoObjeto.addActionListener(e -> Preferencias
				.setAplicarLarguraAoAbrirArquivoObjeto(chkAplicarLarguraAoAbrirArquivoObjeto.isSelected()));
		chkAplicarAlturaAoAbrirArquivoObjeto.addActionListener(e -> Preferencias
				.setAplicarAlturaAoAbrirArquivoObjeto(chkAplicarAlturaAoAbrirArquivoObjeto.isSelected()));
		chkAtivarAbrirAutoDestac.addActionListener(e -> {
			Preferencias.setAbrirAutoDestacado(chkAtivarAbrirAutoDestac.isSelected());
			checkPesquisa();
		});
		chkAtivarAbrirAuto.addActionListener(e -> {
			Preferencias.setAbrirAuto(chkAtivarAbrirAuto.isSelected());
			checkPesquisa();
		});
		txtDefinirLargura.addActionListener(e -> definirLargura());
		txtDefinirAltura.addActionListener(e -> definirAltura());
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

	private Label criarLabelTitulo(String chaveRotulo) {
		Label label = new Label(chaveRotulo);
		label.setHorizontalAlignment(Label.CENTER);
		return label;
	}

	private class NomeValor {
		private static final byte INTERVALO_COMPARA = 5;
		private static final byte INTERVALO_AUTO = 2;
		private static final byte DESTACADOS = 3;
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
				if (nomeValor.tipo == NomeValor.INTERVALO_AUTO) {
					Preferencias.setIntervaloPesquisaAuto(nomeValor.valor);
				} else if (nomeValor.tipo == NomeValor.INTERVALO_COMPARA) {
					Preferencias.setIntervaloComparacao(nomeValor.valor);
				} else if (nomeValor.tipo == NomeValor.DESTACADOS) {
					Preferencias.setTipoContainerPesquisaAuto(nomeValor.valor);
				}
			});
		}
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
				Color color = JColorChooser.showDialog(ObjetoConfiguracao.this, label.getText(), label.getForeground());
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
	public void formularioVisivel() {
		checkPesquisa();
	}

	@Override
	public void paginaVisivel() {
		checkPesquisa();
	}

	@Override
	public void dialogoVisivel() {
		checkPesquisa();
	}
}