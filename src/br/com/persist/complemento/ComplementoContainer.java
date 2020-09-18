package br.com.persist.complemento;

import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.LIMPAR;
import static br.com.persist.componente.BarraButtonEnum.APLICAR;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JList;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;

import br.com.persist.assistencia.ColecaoStringModelo;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.TextArea;

public class ComplementoContainer extends Panel {
	private static final long serialVersionUID = 1L;
	private final transient ComplementoListener listener;
	private final TextArea textArea = new TextArea();
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
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, textArea, new ScrollPane(listaComplementos));
		listaComplementos.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		split.setDividerLocation(Constantes.SIZE.width / 2);

		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, split);
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
		private Action limparComplementosAcao = Action.actionIcon("label.limpar_complementos", Icones.NOVO);

		public void ini(Janela janela) {
			super.ini(janela, LIMPAR, COPIAR, COLAR, APLICAR);
			addButton(limparComplementosAcao);
			limparComplementosAcao.setActionListener(e -> limparComplementos());
		}

		@Override
		protected void limpar() {
			textArea.limpar();
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

		@Override
		protected void aplicar() {
			String string = Util.normalizar(textArea.getText(), true);
			listener.processarComplemento(string);
			fechar();
		}

		private void limparComplementos() {
			if (Util.confirmaExclusao(ComplementoContainer.this, false)) {
				listener.getColecaoComplemento().clear();
				listaComplementos.setModel(new ColecaoStringModelo(listener.getColecaoComplemento()));
			}
		}
	}
}