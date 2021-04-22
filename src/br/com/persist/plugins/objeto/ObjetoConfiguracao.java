package br.com.persist.plugins.objeto;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JColorChooser;
import javax.swing.JRadioButton;

import br.com.persist.abstrato.AbstratoConfiguracao;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Muro;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.Label;
import br.com.persist.componente.Panel;
import br.com.persist.componente.PanelCenter;
import br.com.persist.formulario.Formulario;

public class ObjetoConfiguracao extends AbstratoConfiguracao {
	private static final long serialVersionUID = 1L;
	private final CheckBox chkHabitInnerJoinsObj = new CheckBox(
			ObjetoMensagens.getString("label.habilitadoInnerJoinsObjeto"), false);
	private final CheckBox chkAtivarAbrirAutoDestac = new CheckBox(
			ObjetoMensagens.getString("label.abrir_auto_destacado"), false);
	private final CheckBox chkAtivarAbrirAuto = new CheckBox(ObjetoMensagens.getString("label.ativar_abrir_auto"),
			false);
	private final ButtonGroup grupoTiposContainer = new ButtonGroup();
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
			new NomeValor("label.formulario", ObjetoConstantes.TIPO_CONTAINER_FORMULARIO, NomeValor.DESTACADOS),
			new NomeValor("label.fichario", ObjetoConstantes.TIPO_CONTAINER_FICHARIO, NomeValor.DESTACADOS),
			new NomeValor("label.desktop", ObjetoConstantes.TIPO_CONTAINER_DESKTOP, NomeValor.DESTACADOS) };

	public ObjetoConfiguracao(Formulario formulario) {
		super(formulario, Mensagens.getString("label.plugin_objeto"));
		montarLayout();
		configurar();
	}

	private void montarLayout() {
		PanelCenter panelIntervalosCompara = criarPainelGrupo(intervalosCompara,
				ObjetoPreferencia.getIntervaloComparacao());
		PanelCenter panelDestacados = criarPainelGrupoDestac(destacados,
				ObjetoPreferencia.getTipoContainerPesquisaAuto());
		PanelCenter panelIntervalos = criarPainelGrupo(intervalos, ObjetoPreferencia.getIntervaloPesquisaAuto());

		chkHabitInnerJoinsObj.setSelected(ObjetoPreferencia.isHabilitadoInnerJoinsObjeto());
		chkAtivarAbrirAutoDestac.setSelected(ObjetoPreferencia.isAbrirAutoDestacado());
		chkAtivarAbrirAuto.setSelected(ObjetoPreferencia.isAbrirAuto());

		Muro muro = new Muro();
		Label tituloIntervaloCompara = criarLabelTitulo("label.intervalo_comparacao_titulo");
		Label tituloDestacado = criarLabelTitulo(ObjetoMensagens.getString("label.tipo_container_pesquisa_auto"),
				false);
		Label tituloIntervalo = criarLabelTitulo("label.intervalo_pesquisa_auto");
		muro.camada(panelS(tituloIntervalo, panelIntervalos));
		muro.camada(panelS(tituloIntervaloCompara, panelIntervalosCompara,
				criarLabelTitulo("label.titulo_cor_total_recente"), new PainelCorTotalRecente(),
				chkHabitInnerJoinsObj));
		muro.camada(panel(0, 0, chkAtivarAbrirAuto, chkAtivarAbrirAutoDestac, tituloDestacado, panelDestacados));
		Insets insets = new Insets(5, 10, 5, 5);
		chkAtivarAbrirAutoDestac.setMargin(insets);
		add(BorderLayout.CENTER, muro);
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
		chkHabitInnerJoinsObj.addActionListener(
				e -> ObjetoPreferencia.setHabilitadoInnerJoinsObjeto(chkHabitInnerJoinsObj.isSelected()));
		chkAtivarAbrirAutoDestac.addActionListener(e -> {
			ObjetoPreferencia.setAbrirAutoDestacado(chkAtivarAbrirAutoDestac.isSelected());
			checkPesquisa();
		});
		chkAtivarAbrirAuto.addActionListener(e -> {
			ObjetoPreferencia.setAbrirAuto(chkAtivarAbrirAuto.isSelected());
			checkPesquisa();
		});
	}

	private Label criarLabelTitulo(String chaveRotulo, boolean chave) {
		Label label = new Label(chaveRotulo, chave);
		label.setHorizontalAlignment(Label.CENTER);
		return label;
	}

	private Label criarLabelTitulo(String chaveRotulo) {
		return criarLabelTitulo(chaveRotulo, true);
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
					ObjetoPreferencia.setIntervaloPesquisaAuto(nomeValor.valor);
				} else if (nomeValor.tipo == NomeValor.INTERVALO_COMPARA) {
					ObjetoPreferencia.setIntervaloComparacao(nomeValor.valor);
				} else if (nomeValor.tipo == NomeValor.DESTACADOS) {
					ObjetoPreferencia.setTipoContainerPesquisaAuto(nomeValor.valor);
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
			labelAntesProcessar.setForeground(ObjetoPreferencia.getCorAntesTotalRecente());
			labelAntesProcessar.setBorder(BorderFactory.createLineBorder(Color.GRAY));
			labelBuscarTotal.setBorder(BorderFactory.createLineBorder(Color.GRAY));
			labelComparacao.setBorder(BorderFactory.createLineBorder(Color.GRAY));
			labelBuscarTotal.setForeground(ObjetoPreferencia.getCorTotalAtual());
			labelComparacao.setForeground(ObjetoPreferencia.getCorComparaRec());
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
					ObjetoPreferencia.setCorAntesTotalRecente(color);
				} else if (label == labelBuscarTotal) {
					ObjetoPreferencia.setCorTotalAtual(color);
				} else if (label == labelComparacao) {
					ObjetoPreferencia.setCorComparaRec(color);
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