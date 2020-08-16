package br.com.persist.instrucao;

import java.awt.BorderLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Panel;
import br.com.persist.componente.TextArea;
import br.com.persist.componente.TextField;
import br.com.persist.icone.Icones;
import br.com.persist.util.Action;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Util;

public class InstrucaoContainer extends Panel {
	private static final long serialVersionUID = 1L;
	private final transient InstrucaoContainerListener listener;
	private final TextField textFieldNome = new TextField();
	private final TextArea textArea = new TextArea();
	private final Toolbar toolbar = new Toolbar();
	private final transient Instrucao instrucao;

	public InstrucaoContainer(IJanela janela, Instrucao instrucao, InstrucaoContainerListener listener) {
		textFieldNome.setText(instrucao.getNome());
		textFieldNome.addActionListener(e -> instrucao.setNome(textFieldNome.getText()));
		textFieldNome.addFocusListener(focusNomeListener);
		textArea.setText(instrucao.getValor());
		textArea.addKeyListener(keyValorListener);
		this.instrucao = instrucao;
		this.listener = listener;
		toolbar.ini(janela);
		montarLayout();
	}

	private void montarLayout() {
		textArea.setToolTipText(Mensagens.getString("hint.instrucoes"));
		add(BorderLayout.NORTH, toolbar);

		Panel panel = new Panel();
		panel.add(BorderLayout.NORTH, textFieldNome);
		panel.add(BorderLayout.CENTER, textArea);
		add(BorderLayout.CENTER, panel);
	}

	public Instrucao getInstrucao() {
		return instrucao;
	}

	private transient FocusListener focusNomeListener = new FocusListener() {
		@Override
		public void focusGained(FocusEvent e) {
			instrucao.setNome(textFieldNome.getText());
		}

		@Override
		public void focusLost(FocusEvent e) {
			instrucao.setNome(textFieldNome.getText());
		}
	};

	private transient KeyListener keyValorListener = new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
			instrucao.setValor(textArea.getText());
		}

		@Override
		public void keyReleased(KeyEvent e) {
			instrucao.setValor(textArea.getText());
		}
	};

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private Action excluirAcao = Action.actionIcon("label.excluir2", Icones.EXCLUIR);
		private final TextField textFieldOrdem = new TextField();

		public void ini(IJanela janela) {
			super.ini(janela, false, false);
			textFieldOrdem.setText(Integer.toString(instrucao.getOrdem()));
			textFieldOrdem.addActionListener(e -> configurarOrdem());
			textFieldOrdem.addFocusListener(focusOrdemListener);

			addButton(excluirAcao);
			configCopiar1Acao(true);
			add(textFieldOrdem);
			excluirAcao.setActionListener(e -> listener.excluirInstrucao(instrucao));
		}

		private transient FocusListener focusOrdemListener = new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				configurarOrdem();
			}

			@Override
			public void focusLost(FocusEvent e) {
				configurarOrdem();
			}
		};

		private void configurarOrdem() {
			int atual = instrucao.getOrdem();
			instrucao.setOrdem(Util.getInt(textFieldOrdem.getText(), atual));
		}

		@Override
		protected void copiar1() {
			String string = Util.getString(textArea.getTextAreaInner());
			Util.setContentTransfered(string);
			textArea.requestFocus();
		}

		@Override
		protected void colar1() {
			Util.getContentTransfered(textArea.getTextAreaInner());
		}
	}
}