package br.com.persist.container;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JList;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;

import br.com.persist.comp.BarraButton;
import br.com.persist.comp.Panel;
import br.com.persist.comp.ScrollPane;
import br.com.persist.comp.TextArea;
import br.com.persist.comp.TextField;
import br.com.persist.desktop.Objeto;
import br.com.persist.listener.ComplementoListener;
import br.com.persist.modelo.ListaStringModelo;
import br.com.persist.util.Action;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Icones;
import br.com.persist.util.Util;

public class ComplementoContainer extends Panel {
	private static final long serialVersionUID = 1L;
	private final transient ComplementoListener listener;
	private final TextArea textArea = new TextArea();
	private final Toolbar toolbar = new Toolbar();
	private final JList<String> complementos;
	private final transient Objeto objeto;

	public ComplementoContainer(IJanela janela, Objeto objeto, TextField txtComplemento, ComplementoListener listener) {
		complementos = new JList<>(new ListaStringModelo(objeto.getComplementos()));
		complementos.addMouseListener(mouseListenerInner);
		textArea.setText(txtComplemento.getText());
		this.listener = listener;
		this.objeto = objeto;
		toolbar.ini(janela);
		montarLayout();
	}

	private void montarLayout() {
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, textArea, new ScrollPane(complementos));
		complementos.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		split.setDividerLocation(Constantes.SIZE.width / 2);

		add(BorderLayout.CENTER, split);
		add(BorderLayout.NORTH, toolbar);
	}

	private transient MouseListener mouseListenerInner = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			String sel = complementos.getSelectedValue();

			if (!Util.estaVazio(sel)) {
				String string = textArea.getText();
				textArea.setText(string + " " + sel);
			}
		}
	};

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private Action sucessoAcao = Action.actionIcon("label.aplicar", Icones.SUCESSO);
		private Action limparCompleAcao = Action.actionIcon("label.limpar_complementos", Icones.NOVO);

		public void ini(IJanela janela) {
			super.ini(janela, true, false);

			addButton(limparCompleAcao);
			addButton(sucessoAcao);
			configCopiar1Acao(true);

			sucessoAcao.setActionListener(e -> {
				String string = Util.normalizar(textArea.getText(), true);
				listener.processarComplemento(string);
				fechar();
			});

			limparCompleAcao.setActionListener(e -> limparComplementos());
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

		@Override
		protected void limpar() {
			textArea.limpar();
		}

		private void limparComplementos() {
			if (Util.confirmaExclusao(ComplementoContainer.this, false)) {
				objeto.getComplementos().clear();
				complementos.setModel(new ListaStringModelo(objeto.getComplementos()));
			}
		}
	}
}