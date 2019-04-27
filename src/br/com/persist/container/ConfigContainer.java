package br.com.persist.container;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import br.com.persist.comp.BarraButton;
import br.com.persist.comp.CheckBox;
import br.com.persist.comp.Label;
import br.com.persist.comp.Panel;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Action;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Preferencias;

public class ConfigContainer extends Panel {
	private static final long serialVersionUID = 1L;
	private final CheckBox chkAreaTransTabelaRegistros = new CheckBox("label.area_trans_tabela_registros");
	private final CheckBox chkFecharOrigemAposSoltar = new CheckBox("label.fechar_origem_apos_soltar");
	private final CheckBox chkNomeColunaListener = new CheckBox("label.copiar_nome_coluna_listener");
	private final CheckBox chkAtivarAbrirAutoDestac = new CheckBox("label.abrir_auto_destacado");
	private final CheckBox chkAbortarFecharComESC = new CheckBox("label.abortar_fechar_com_esc");
	private final CheckBox chkAtivarAbrirAuto = new CheckBox("label.ativar_abrir_auto");
	private final CheckBox chkFicharioScroll = new CheckBox("label.fichario_scroll");
	private final Toolbar toolbar = new Toolbar();

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

	private final Formulario formulario;

	public ConfigContainer(IJanela janela, Formulario formulario) {
		this.formulario = formulario;
		toolbar.ini(janela);
		montarLayout();
		configurar();
	}

	private void montarLayout() {
		chkAreaTransTabelaRegistros.setSelected(Preferencias.isAreaTransTabelaRegistros());
		chkNomeColunaListener.setSelected(Preferencias.isCopiarNomeColunaListener());
		chkAtivarAbrirAutoDestac.setSelected(Preferencias.isAbrirAutoDestacado());
		chkAbortarFecharComESC.setSelected(Preferencias.isAbortarFecharComESC());
		chkFecharOrigemAposSoltar.setSelected(Preferencias.isFecharAposSoltar());
		chkFicharioScroll.setSelected(Preferencias.isFicharioComRolagem());
		chkAtivarAbrirAuto.setSelected(Preferencias.isAbrirAuto());

		Panel panelDestacados = criarPainelGrupo(destacados, Preferencias.getTipoContainerPesquisaAuto());
		Panel panelIntervalos = criarPainelGrupo(intervalos, Preferencias.getIntervaloPesquisaAuto());
		Panel panelPosicoes = criarPainelGrupo(posicoes, Preferencias.getPosicaoAbaFichario());

		Label tituloDestacado = criarLabelTitulo("label.tipo_container_pesquisa_auto");
		Label tituloIntervalo = criarLabelTitulo("label.intervalo_pesquisa_auto");
		Label tituloLocalAbas = criarLabelTitulo("label.local_abas");

		Panel container = new Panel(new GridLayout(0, 1));
		container.add(tituloLocalAbas);
		container.add(panelPosicoes);
		container.add(new JSeparator());
		container.add(tituloIntervalo);
		container.add(panelIntervalos);
		container.add(new JSeparator());
		container.add(chkAreaTransTabelaRegistros);
		container.add(chkAbortarFecharComESC);
		container.add(chkNomeColunaListener);
		container.add(chkFecharOrigemAposSoltar);
		container.add(chkFicharioScroll);
		container.add(new JSeparator());
		container.add(chkAtivarAbrirAuto);
		container.add(chkAtivarAbrirAutoDestac);
		container.add(tituloDestacado);
		container.add(panelDestacados);

		add(BorderLayout.CENTER, container);
		add(BorderLayout.NORTH, toolbar);

		Insets insets = new Insets(5, 10, 5, 5);

		chkAreaTransTabelaRegistros.setMargin(insets);
		chkFecharOrigemAposSoltar.setMargin(insets);
		chkAtivarAbrirAutoDestac.setMargin(insets);
		chkAbortarFecharComESC.setMargin(insets);
		chkNomeColunaListener.setMargin(insets);
		chkAtivarAbrirAuto.setMargin(insets);
		chkFicharioScroll.setMargin(insets);
	}

	private Panel criarPainelGrupo(NomeValor[] nomeValores, int padrao) {
		Panel panel = new Panel(new FlowLayout(FlowLayout.CENTER));
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

		chkNomeColunaListener
				.addActionListener(e -> Preferencias.setCopiarNomeColunaListener(chkNomeColunaListener.isSelected()));

		chkAtivarAbrirAutoDestac
				.addActionListener(e -> Preferencias.setAbrirAutoDestacado(chkAtivarAbrirAutoDestac.isSelected()));

		chkAbortarFecharComESC
				.addActionListener(e -> Preferencias.setAbortarFecharComESC(chkAbortarFecharComESC.isSelected()));

		chkFecharOrigemAposSoltar
				.addActionListener(e -> Preferencias.setFecharAposSoltar(chkFecharOrigemAposSoltar.isSelected()));

		chkAtivarAbrirAuto.addActionListener(e -> Preferencias.setAbrirAuto(chkAtivarAbrirAuto.isSelected()));

		chkAreaTransTabelaRegistros.addActionListener(
				e -> Preferencias.setAreaTransTabelaRegistros(chkAreaTransTabelaRegistros.isSelected()));
	}

	private class NomeValor {
		static final byte INTERVALO_AUTO = 2;
		static final byte POSICAO_ABA = 1;
		static final byte DESTACADOS = 3;
		final String nome;
		final int valor;
		final int tipo;

		NomeValor(String chave, int valor, int tipo) {
			this.nome = Mensagens.getString(chave);
			this.valor = valor;
			this.tipo = tipo;
		}
	}

	private class RadioPosicao extends JRadioButton {
		private static final long serialVersionUID = 1L;
		final transient NomeValor nomeValor;

		RadioPosicao(NomeValor nomeValor) {
			super(nomeValor.nome);
			this.nomeValor = nomeValor;

			addActionListener(e -> {
				if (nomeValor.tipo == NomeValor.POSICAO_ABA) {
					Preferencias.setPosicaoAbaFichario(nomeValor.valor);
					formulario.getFichario().setTabPlacement(Preferencias.getPosicaoAbaFichario());
				} else if (nomeValor.tipo == NomeValor.INTERVALO_AUTO) {
					Preferencias.setIntervaloPesquisaAuto(nomeValor.valor);
				} else if (nomeValor.tipo == NomeValor.DESTACADOS) {
					Preferencias.setTipoContainerPesquisaAuto(nomeValor.valor);
				}
			});
		}
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private Action salvarAcao = Action.actionIconSalvar();

		@Override
		public void ini(IJanela janela) {
			super.ini(janela);

			addButton(salvarAcao);

			salvarAcao.setActionListener(e -> Preferencias.salvar());
		}
	}
}