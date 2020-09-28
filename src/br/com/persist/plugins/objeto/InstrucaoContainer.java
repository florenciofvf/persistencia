package br.com.persist.plugins.objeto;

import java.awt.BorderLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Panel;
import br.com.persist.componente.TextArea;
import br.com.persist.componente.TextField;

import static br.com.persist.componente.BarraButtonEnum.EXCLUIR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.COLAR;

public class InstrucaoContainer extends Panel {
	private static final long serialVersionUID = 1L;
	private final transient InstrucaoContainerListener listener;
	private final TextField textFieldNome = new TextField();
	private final TextArea textArea = new TextArea();
	private final Toolbar toolbar = new Toolbar();
	private final transient Instrucao instrucao;

	public InstrucaoContainer(Janela janela, Instrucao instrucao, InstrucaoContainerListener listener) {
		textFieldNome.addActionListener(e -> instrucao.setNome(textFieldNome.getText()));
		textFieldNome.addFocusListener(focusNomeListener);
		textFieldNome.setText(instrucao.getNome());
		textArea.addKeyListener(keyValorListener);
		textArea.setText(instrucao.getValor());
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
		private final TextField textFieldOrdem = new TextField();

		public void ini(Janela janela) {
			super.ini(janela, EXCLUIR, COPIAR, COLAR);
			textFieldOrdem.setToolTipText(Mensagens.getString("label.ordem"));
			textFieldOrdem.setText(Integer.toString(instrucao.getOrdem()));
			textFieldOrdem.addActionListener(e -> configurarOrdem());
			textFieldOrdem.addFocusListener(focusOrdemListener);
			add(textFieldOrdem);
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
		protected void excluir() {
			listener.excluirInstrucao(instrucao);
		}

		@Override
		protected void copiar() {
			String string = Util.getString(textArea.getTextAreaInner());
			Util.setContentTransfered(string);
			copiarMensagem(string);
			textArea.requestFocus();
		}

		@Override
		protected void colar() {
			Util.getContentTransfered(textArea.getTextAreaInner());
		}
	}
}