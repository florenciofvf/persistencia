package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import br.com.persist.comp.CheckBox;
import br.com.persist.comp.Label;
import br.com.persist.comp.Panel;
import br.com.persist.formulario.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.Mensagens;

public class ConfigDialogo extends Dialogo {
	private static final long serialVersionUID = 1L;
	private final CheckBox chkAreaTransTabelaRegistros = new CheckBox("label.area_trans_tabela_registros");
	private final CheckBox chkFecharOrigemAposSoltar = new CheckBox("label.fechar_origem_apos_soltar");
	private final CheckBox chkAtivarAbrirAutoDestac = new CheckBox("label.abrir_auto_destacado");
	private final CheckBox chkAtivarAbrirAuto = new CheckBox("label.ativar_abrir_auto");
	private final CheckBox chkTabelaListener = new CheckBox("label.tabela_listener");
	private final CheckBox chkFicharioScroll = new CheckBox("label.fichario_scroll");
	private final Posicao[] posicoes = { new Posicao("label.acima", SwingConstants.TOP),
			new Posicao("label.esquerdo", SwingConstants.LEFT), new Posicao("label.abaixo", SwingConstants.BOTTOM),
			new Posicao("label.direito", SwingConstants.RIGHT) };
	private final RadioPosicao[] rdoPosicoes = new RadioPosicao[posicoes.length];
	private final Formulario formulario;

	public ConfigDialogo(Formulario formulario) {
		super(formulario, Mensagens.getString("label.configuracoes"), 700, 341, false);
		this.formulario = formulario;
		montarLayout();
		configurar();
		setVisible(true);
	}

	private void montarLayout() {
		chkFicharioScroll.setSelected(formulario.getFichario().getTabLayoutPolicy() == JTabbedPane.SCROLL_TAB_LAYOUT);
		chkAreaTransTabelaRegistros.setSelected(Constantes.area_trans_tabela_registros);
		chkAtivarAbrirAutoDestac.setSelected(Constantes.abrir_auto_destacado);
		chkFecharOrigemAposSoltar.setSelected(Constantes.fechar_apos_soltar);
		chkTabelaListener.setSelected(Constantes.tabela_listener);
		chkAtivarAbrirAuto.setSelected(Constantes.abrir_auto);

		Panel panelPosicoes = new Panel(new GridLayout(0, 4));
		ButtonGroup grupo = new ButtonGroup();

		for (int i = 0; i < posicoes.length; i++) {
			RadioPosicao radio = new RadioPosicao(posicoes[i]);
			radio.setMargin(new Insets(5, 10, 5, 5));
			panelPosicoes.add(radio);
			rdoPosicoes[i] = radio;
			grupo.add(radio);

			radio.setSelected(radio.posicao.indice == formulario.getFichario().getTabPlacement());
		}

		Label localAbas = new Label("label.local_abas");
		localAbas.setHorizontalAlignment(Label.CENTER);

		Panel container = new Panel(new GridLayout(0, 1));
		container.add(localAbas);
		container.add(panelPosicoes);
		container.add(new JSeparator());
		container.add(chkAreaTransTabelaRegistros);
		container.add(chkAtivarAbrirAuto);
		container.add(chkAtivarAbrirAutoDestac);
		container.add(chkFecharOrigemAposSoltar);
		container.add(chkFicharioScroll);
		container.add(chkTabelaListener);
		container.add(new JSeparator());

		add(BorderLayout.CENTER, container);

		Insets insets = new Insets(5, 10, 5, 5);

		chkAreaTransTabelaRegistros.setMargin(insets);
		chkFecharOrigemAposSoltar.setMargin(insets);
		chkAtivarAbrirAutoDestac.setMargin(insets);
		chkAtivarAbrirAuto.setMargin(insets);
		chkTabelaListener.setMargin(insets);
		chkFicharioScroll.setMargin(insets);
	}

	private void configurar() {
		chkAtivarAbrirAutoDestac
				.addActionListener(e -> Constantes.abrir_auto_destacado = chkAtivarAbrirAutoDestac.isSelected());

		chkFecharOrigemAposSoltar
				.addActionListener(e -> Constantes.fechar_apos_soltar = chkFecharOrigemAposSoltar.isSelected());

		chkFicharioScroll.addActionListener(e -> formulario.getFichario().setTabLayoutPolicy(
				chkFicharioScroll.isSelected() ? JTabbedPane.SCROLL_TAB_LAYOUT : JTabbedPane.WRAP_TAB_LAYOUT));

		chkTabelaListener.addActionListener(e -> Constantes.tabela_listener = chkTabelaListener.isSelected());

		chkAtivarAbrirAuto.addActionListener(e -> Constantes.abrir_auto = chkAtivarAbrirAuto.isSelected());

		chkAreaTransTabelaRegistros.addActionListener(
				e -> Constantes.area_trans_tabela_registros = chkAreaTransTabelaRegistros.isSelected());
	}

	protected void processar() {
	}

	private class Posicao {
		final String nome;
		final int indice;

		Posicao(String chave, int indice) {
			this.nome = Mensagens.getString(chave);
			this.indice = indice;
		}
	}

	private class RadioPosicao extends JRadioButton {
		private static final long serialVersionUID = 1L;
		final Posicao posicao;

		RadioPosicao(Posicao posicao) {
			super(posicao.nome);
			this.posicao = posicao;
			addActionListener(e -> formulario.getFichario().setTabPlacement(posicao.indice));
		}
	}
}