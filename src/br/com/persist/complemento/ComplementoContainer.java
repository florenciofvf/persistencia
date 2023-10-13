package br.com.persist.complemento;

import static br.com.persist.componente.BarraButtonEnum.APLICAR;
import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.LIMPAR;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JList;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;

import br.com.persist.assistencia.ColecaoStringModelo;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Nil;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;

public class ComplementoContainer extends Panel {
	private final ToolbarLista toolbarLista = new ToolbarLista();
	private final ToolbarArea toolbarArea = new ToolbarArea();
	private final transient ComplementoListener listener;
	private final JTextArea textArea = new JTextArea();
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private final JList<String> listaComplementos;

	public ComplementoContainer(Janela janela, ComplementoListener listener) {
		listaComplementos = new JList<>(new ColecaoStringModelo(listener.getColecaoComplemento()));
		listaComplementos.addMouseListener(mouseListenerInner);
		textArea.setText(listener.getComplementoPadrao());
		this.listener = listener;
		toolbar.ini(janela);
		montarLayout();
	}

	private void montarLayout() {
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, criarPanelTextArea(), criarPanelLista());
		listaComplementos.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		split.setDividerLocation(Constantes.SIZE.height / 2);
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, split);
	}

	private Panel criarPanelTextArea() {
		Panel panel = new Panel();
		panel.add(BorderLayout.NORTH, toolbarArea);
		panel.add(BorderLayout.CENTER, new ScrollPane(textArea));
		textArea.setLineWrap(true);
		return panel;
	}

	private Panel criarPanelLista() {
		Panel panel = new Panel();
		panel.add(BorderLayout.NORTH, toolbarLista);
		panel.add(BorderLayout.CENTER, new ScrollPane(listaComplementos));
		return panel;
	}

	private transient MouseListener mouseListenerInner = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			String sel = listaComplementos.getSelectedValue();
			if (!Util.estaVazio(sel)) {
				String string = textArea.getText();
				textArea.setText(string + " " + sel);
			}
		}
	};

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;

		public void ini(Janela janela) {
			super.ini(janela, APLICAR);
		}

		@Override
		protected void aplicar() {
			listener.processarComplemento(textArea.getText());
			fechar();
		}
	}

	private class ToolbarArea extends BarraButton {
		private static final long serialVersionUID = 1L;

		private ToolbarArea() {
			super.ini(new Nil(), LIMPAR, COPIAR, COLAR);
		}

		@Override
		protected void limpar() {
			textArea.setText(Constantes.VAZIO);
		}

		@Override
		protected void copiar() {
			String string = Util.getString(textArea);
			Util.setContentTransfered(string);
			copiarMensagem(string);
			textArea.requestFocus();
		}

		@Override
		protected void colar(boolean numeros, boolean letras) {
			Util.getContentTransfered(textArea, numeros, letras);
		}
	}

	private class ToolbarLista extends BarraButton {
		private Action limparComplementosAcao = Action
				.acaoIcon(ComplementoMensagens.getString("label.limpar_complementos"), Icones.EXCLUIR);
		private static final long serialVersionUID = 1L;

		private ToolbarLista() {
			super.ini(new Nil());
			addButton(limparComplementosAcao);
			limparComplementosAcao.setActionListener(e -> limparComplementos());
		}

		private void limparComplementos() {
			if (Util.confirmar(ComplementoContainer.this,
					ComplementoMensagens.getString("msg.confirma_exclusao_complementos"), false)) {
				listener.getColecaoComplemento().clear();
				listaComplementos.setModel(new ColecaoStringModelo(listener.getColecaoComplemento()));
			}
		}
	}
}