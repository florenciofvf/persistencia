package br.com.persist.componente;

import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.LIMPAR;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.SetValor.Valor;

public class SetValor {
	private SetValor() {
	}

	public static void view(Component c, Valor valor) {
		Component comp = Util.getViewParent(c);
		SetValorDialogo form = null;
		if (comp instanceof Frame) {
			form = new SetValorDialogo((Frame) comp, valor);
		} else if (comp instanceof Dialog) {
			form = new SetValorDialogo((Dialog) comp, valor);
		} else {
			form = new SetValorDialogo((Frame) null, valor);
		}
		if (!Util.isEmpty(valor.get()) && valor.get().length() < 200) {
			form.configAjuste = true;
			form.pack();
		}
		form.setLocationRelativeTo(comp != null ? comp : c);
		form.setVisible(true);
	}

	public static interface Valor {
		void set(String s);

		String getTitle();

		String get();
	}
}

class SetValorDialogo extends AbstratoDialogo {
	private TextEditor textEditor = new TextEditor();
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private final transient Valor valor;
	private ScrollPane scrollPane;
	boolean configAjuste;

	SetValorDialogo(Frame frame, Valor valor) {
		super(frame, valor.getTitle());
		textEditor.setText(valor.get());
		this.valor = valor;
		init();
	}

	SetValorDialogo(Dialog dialog, Valor valor) {
		super(dialog, valor.getTitle());
		textEditor.setText(valor.get());
		this.valor = valor;
		init();
	}

	private void init() {
		toolbar.ini(null);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		ScrollPane scrollPaneEditor = new ScrollPane(textEditor);
		scrollPaneEditor.setRowHeaderView(new TextEditorLine(textEditor));
		Panel panelScroll = new Panel();
		panelScroll.add(BorderLayout.CENTER, scrollPaneEditor);
		scrollPane = new ScrollPane(panelScroll);
		add(BorderLayout.CENTER, scrollPane);
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
		if (configAjuste) {
			checarVisivelScrollVertical();
		}
	}

	private void checarVisivelScrollVertical() {
		JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
		if (scrollBar.isVisible()) {
			Dimension size = getSize();
			setSize(size.width, size.height + 10);
			SwingUtilities.invokeLater(SetValorDialogo.this::checarVisivelScrollVertical);
		} else {
			checarVisivelScrollHorizontal();
		}
	}

	private void checarVisivelScrollHorizontal() {
		JScrollBar scrollBar = scrollPane.getHorizontalScrollBar();
		if (scrollBar.isVisible()) {
			Dimension size = getSize();
			setSize(size.width + 10, size.height);
			SwingUtilities.invokeLater(SetValorDialogo.this::checarVisivelScrollHorizontal);
		}
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;

		public void ini(Janela janela) {
			super.ini(janela, LIMPAR, COPIAR, COLAR);
		}

		@Override
		protected void limpar() {
			textEditor.limpar();
		}

		@Override
		protected void copiar() {
			String string = Util.getString(textEditor);
			Util.setContentTransfered(string);
			copiarMensagem(string);
			textEditor.requestFocus();
		}

		@Override
		protected void colar(boolean numeros, boolean letras) {
			Util.getContentTransfered(textEditor, numeros, letras);
		}
	}

	@Override
	public void dialogClosingHandler(Dialog dialog) {
		valor.set(textEditor.getText());
	}

	@Override
	public void dispose() {
		valor.set(textEditor.getText());
		super.dispose();
	}
}