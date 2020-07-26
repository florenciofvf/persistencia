package br.com.persist.container;

import java.awt.BorderLayout;

import javax.swing.JList;
import javax.swing.JSplitPane;

import br.com.persist.comp.BarraButton;
import br.com.persist.comp.Panel;
import br.com.persist.comp.ScrollPane;
import br.com.persist.comp.TextArea;
import br.com.persist.comp.TextField;
import br.com.persist.desktop.Objeto;
import br.com.persist.modelo.ListaStringModelo;
import br.com.persist.util.Action;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Icones;
import br.com.persist.util.Util;

public class ComplementoContainer extends Panel {
	private static final long serialVersionUID = 1L;
	private final TextArea textArea = new TextArea();
	private final Toolbar toolbar = new Toolbar();
	private final JList<String> complementos;
	private TextField txtComplemento;

	public ComplementoContainer(IJanela janela, Objeto objeto, TextField txtComplemento) {
		complementos = new JList<>(new ListaStringModelo(objeto.getComplementos()));
		textArea.setText(txtComplemento.getText());
		this.txtComplemento = txtComplemento;
		toolbar.ini(janela);
		montarLayout();
	}

	private void montarLayout() {
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, textArea, new ScrollPane(complementos));
		split.setDividerLocation(Constantes.SIZE.width / 2);

		add(BorderLayout.CENTER, split);
		add(BorderLayout.NORTH, toolbar);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private Action sucessoAcao = Action.actionIcon("label.aplicar", Icones.SUCESSO);

		public void ini(IJanela janela) {
			super.ini(janela, true, false);

			addButton(sucessoAcao);
			configCopiar1Acao(true);

			sucessoAcao.setActionListener(e -> {
				txtComplemento.setText(Util.normalizar(textArea.getText(), true));
				fechar();
			});
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
	}
}