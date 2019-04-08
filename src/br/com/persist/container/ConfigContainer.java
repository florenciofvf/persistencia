package br.com.persist.container;

import java.awt.BorderLayout;
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
	private final CheckBox chkAtivarAbrirAuto = new CheckBox("label.ativar_abrir_auto");
	private final CheckBox chkFicharioScroll = new CheckBox("label.fichario_scroll");
	private final Toolbar toolbar = new Toolbar();

	private final transient NomeValor[] nomeValorPosicoes = {
			new NomeValor("label.acima", SwingConstants.TOP, NomeValor.POSICAO_ABA),
			new NomeValor("label.esquerdo", SwingConstants.LEFT, NomeValor.POSICAO_ABA),
			new NomeValor("label.abaixo", SwingConstants.BOTTOM, NomeValor.POSICAO_ABA),
			new NomeValor("label.direito", SwingConstants.RIGHT, NomeValor.POSICAO_ABA) };

	private final transient NomeValor[] nomeValorIntervalos = {
			new NomeValor("label.1000", 1000, NomeValor.INTERVALO_AUTO),
			new NomeValor("label.5000", 5000, NomeValor.INTERVALO_AUTO),
			new NomeValor("label.10000", 10000, NomeValor.INTERVALO_AUTO),
			new NomeValor("label.20000", 20000, NomeValor.INTERVALO_AUTO),
			new NomeValor("label.30000", 30000, NomeValor.INTERVALO_AUTO),
			new NomeValor("label.59000", 59000, NomeValor.INTERVALO_AUTO), };

	private final transient NomeValor[] nomeValorDestacados = {
			new NomeValor("label.formulario", Constantes.TIPO_CONTAINER_FORMULARIO, NomeValor.DESTACADOS),
			new NomeValor("label.fichario", Constantes.TIPO_CONTAINER_DESKTOP, NomeValor.DESTACADOS) };

	private final RadioPosicao[] rdoDestacados = new RadioPosicao[nomeValorDestacados.length];
	private final RadioPosicao[] rdoIntervalos = new RadioPosicao[nomeValorIntervalos.length];
	private final RadioPosicao[] rdoPosicoes = new RadioPosicao[nomeValorPosicoes.length];
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
		chkFecharOrigemAposSoltar.setSelected(Preferencias.isFecharAposSoltar());
		chkFicharioScroll.setSelected(Preferencias.isFicharioComRolagem());
		chkAtivarAbrirAuto.setSelected(Preferencias.isAbrirAuto());

		Panel panelDestacados = new Panel(new GridLayout(0, 2));
		Panel panelIntervalos = new Panel(new GridLayout(0, 6));
		Panel panelPosicoes = new Panel(new GridLayout(0, 4));
		ButtonGroup grupoDestacados = new ButtonGroup();
		ButtonGroup grupoIntervalos = new ButtonGroup();
		ButtonGroup grupoPosicoes = new ButtonGroup();

		for (int i = 0; i < nomeValorPosicoes.length; i++) {
			RadioPosicao radio = new RadioPosicao(nomeValorPosicoes[i]);
			radio.setMargin(new Insets(5, 10, 5, 5));
			grupoPosicoes.add(radio);
			panelPosicoes.add(radio);
			rdoPosicoes[i] = radio;

			radio.setSelected(radio.nomeValor.valor == Preferencias.getPosicaoAbaFichario());
		}

		for (int i = 0; i < nomeValorIntervalos.length; i++) {
			RadioPosicao radio = new RadioPosicao(nomeValorIntervalos[i]);
			radio.setMargin(new Insets(5, 10, 5, 5));
			grupoIntervalos.add(radio);
			panelIntervalos.add(radio);
			rdoIntervalos[i] = radio;

			radio.setSelected(radio.nomeValor.valor == Preferencias.getIntervaloPesquisaAuto());
		}

		for (int i = 0; i < nomeValorDestacados.length; i++) {
			RadioPosicao radio = new RadioPosicao(nomeValorDestacados[i]);
			radio.setMargin(new Insets(5, 10, 5, 5));
			grupoDestacados.add(radio);
			panelDestacados.add(radio);
			rdoDestacados[i] = radio;

			radio.setSelected(radio.nomeValor.valor == Preferencias.getTipoContainerPesquisaAuto());
		}

		Label tituloDestacado = new Label("label.tipo_container_pesquisa_auto");
		Label tituloIntervalo = new Label("label.intervalo_pesquisa_auto");
		tituloDestacado.setHorizontalAlignment(Label.CENTER);
		tituloIntervalo.setHorizontalAlignment(Label.CENTER);
		Label localAbas = new Label("label.local_abas");
		localAbas.setHorizontalAlignment(Label.CENTER);

		Panel container = new Panel(new GridLayout(0, 1));
		container.add(localAbas);
		container.add(panelPosicoes);
		container.add(new JSeparator());
		container.add(tituloIntervalo);
		container.add(panelIntervalos);
		container.add(new JSeparator());
		container.add(chkAreaTransTabelaRegistros);
		container.add(chkNomeColunaListener);
		container.add(new JSeparator());
		container.add(chkAtivarAbrirAuto);
		container.add(chkAtivarAbrirAutoDestac);
		container.add(tituloDestacado);
		container.add(panelDestacados);
		container.add(new JSeparator());
		container.add(chkFecharOrigemAposSoltar);
		container.add(chkFicharioScroll);

		add(BorderLayout.CENTER, container);
		add(BorderLayout.NORTH, toolbar);

		Insets insets = new Insets(5, 10, 5, 5);

		chkAreaTransTabelaRegistros.setMargin(insets);
		chkFecharOrigemAposSoltar.setMargin(insets);
		chkAtivarAbrirAutoDestac.setMargin(insets);
		chkNomeColunaListener.setMargin(insets);
		chkAtivarAbrirAuto.setMargin(insets);
		chkFicharioScroll.setMargin(insets);
	}

	private void configurar() {
		chkNomeColunaListener
				.addActionListener(e -> Preferencias.setCopiarNomeColunaListener(chkNomeColunaListener.isSelected()));

		chkFicharioScroll.addActionListener(e -> {
			Preferencias.setFicharioComRolagem(chkFicharioScroll.isSelected());
			formulario.getFichario().setTabLayoutPolicy(
					Preferencias.isFicharioComRolagem() ? JTabbedPane.SCROLL_TAB_LAYOUT : JTabbedPane.WRAP_TAB_LAYOUT);
		});

		chkAtivarAbrirAutoDestac
				.addActionListener(e -> Preferencias.setAbrirAutoDestacado(chkAtivarAbrirAutoDestac.isSelected()));

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