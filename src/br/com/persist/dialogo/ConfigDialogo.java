package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import br.com.persist.comp.CheckBox;
import br.com.persist.comp.Label;
import br.com.persist.comp.Panel;
import br.com.persist.formulario.Formulario;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Preferencias;

public class ConfigDialogo extends Dialogo {
	private static final long serialVersionUID = 1L;
	private final CheckBox chkAreaTransTabelaRegistros = new CheckBox("label.area_trans_tabela_registros");
	private final CheckBox chkFecharOrigemAposSoltar = new CheckBox("label.fechar_origem_apos_soltar");
	private final CheckBox chkNomeColunaListener = new CheckBox("label.copiar_nome_coluna_listener");
	private final CheckBox chkAtivarAbrirAutoDestac = new CheckBox("label.abrir_auto_destacado");
	private final CheckBox chkAtivarAbrirAuto = new CheckBox("label.ativar_abrir_auto");
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
		chkAreaTransTabelaRegistros.setSelected(Preferencias.area_trans_tabela_registros);
		chkNomeColunaListener.setSelected(Preferencias.copiar_nome_coluna_listener);
		chkAtivarAbrirAutoDestac.setSelected(Preferencias.abrir_auto_destacado);
		chkFecharOrigemAposSoltar.setSelected(Preferencias.fechar_apos_soltar);
		chkFicharioScroll.setSelected(Preferencias.fichario_com_rolagem);
		chkAtivarAbrirAuto.setSelected(Preferencias.abrir_auto);

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
		container.add(chkNomeColunaListener);
		container.add(chkAtivarAbrirAuto);
		container.add(chkAtivarAbrirAutoDestac);
		container.add(chkFecharOrigemAposSoltar);
		container.add(chkFicharioScroll);
		container.add(new JSeparator());

		add(BorderLayout.CENTER, container);

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
				.addActionListener(e -> Preferencias.copiar_nome_coluna_listener = chkNomeColunaListener.isSelected());

		chkFicharioScroll.addActionListener(e -> {
			Preferencias.fichario_com_rolagem = chkFicharioScroll.isSelected();
			formulario.getFichario().setTabLayoutPolicy(
					Preferencias.fichario_com_rolagem ? JTabbedPane.SCROLL_TAB_LAYOUT : JTabbedPane.WRAP_TAB_LAYOUT);
		});

		chkAtivarAbrirAutoDestac
				.addActionListener(e -> Preferencias.abrir_auto_destacado = chkAtivarAbrirAutoDestac.isSelected());

		chkFecharOrigemAposSoltar
				.addActionListener(e -> Preferencias.fechar_apos_soltar = chkFecharOrigemAposSoltar.isSelected());

		chkAtivarAbrirAuto.addActionListener(e -> Preferencias.abrir_auto = chkAtivarAbrirAuto.isSelected());

		chkAreaTransTabelaRegistros.addActionListener(
				e -> Preferencias.area_trans_tabela_registros = chkAreaTransTabelaRegistros.isSelected());

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Preferencias.salvar();
			}
		});
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