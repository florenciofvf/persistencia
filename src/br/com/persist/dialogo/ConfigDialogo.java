package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import br.com.persist.comp.CheckBox;
import br.com.persist.comp.Panel;
import br.com.persist.formulario.Formulario;
import br.com.persist.util.Mensagens;

public class ConfigDialogo extends Dialogo {
	private static final long serialVersionUID = 1L;
	private final CheckBox chkFicharioScroll = new CheckBox("label.fichario_scroll");
	private final Posicao[] posicoes = { new Posicao("label.acima", SwingConstants.TOP),
			new Posicao("label.esquerdo", SwingConstants.LEFT), new Posicao("label.abaixo", SwingConstants.BOTTOM),
			new Posicao("label.direito", SwingConstants.RIGHT) };
	private final RadioPosicao[] rdoPosicoes = new RadioPosicao[posicoes.length];
	private final Formulario formulario;

	public ConfigDialogo(Formulario formulario) {
		super(formulario, Mensagens.getString("label.configuracoes"), 700, 200, false);
		this.formulario = formulario;
		montarLayout();
		configurar();
		setVisible(true);
	}

	private void montarLayout() {
		chkFicharioScroll.setSelected(formulario.getFichario().getTabLayoutPolicy() == JTabbedPane.SCROLL_TAB_LAYOUT);

		Panel panelPosicoes = new Panel(new GridLayout(0, 4));
		ButtonGroup grupo = new ButtonGroup();

		for (int i = 0; i < posicoes.length; i++) {
			RadioPosicao radio = new RadioPosicao(posicoes[i]);
			panelPosicoes.add(radio);
			rdoPosicoes[i] = radio;
			grupo.add(radio);

			radio.setSelected(radio.posicao.indice == formulario.getFichario().getTabPlacement());
		}

		Panel container = new Panel(new GridLayout(0, 1));
		container.add(chkFicharioScroll);
		container.add(panelPosicoes);

		add(BorderLayout.CENTER, container);
	}

	private void configurar() {
		chkFicharioScroll.addActionListener(e -> formulario.getFichario().setTabLayoutPolicy(
				chkFicharioScroll.isSelected() ? JTabbedPane.SCROLL_TAB_LAYOUT : JTabbedPane.WRAP_TAB_LAYOUT));
	}

	protected void processar() {
	}

	class Posicao {
		final String nome;
		final int indice;

		public Posicao(String chave, int indice) {
			this.nome = Mensagens.getString(chave);
			this.indice = indice;
		}
	}

	class RadioPosicao extends JRadioButton {
		private static final long serialVersionUID = 1L;
		final Posicao posicao;

		public RadioPosicao(Posicao posicao) {
			super(posicao.nome);
			this.posicao = posicao;
			addActionListener(e -> formulario.getFichario().setTabPlacement(posicao.indice));
		}
	}
}