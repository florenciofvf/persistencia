package br.com.persist.componente;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.event.CaretEvent;
import javax.swing.plaf.TextUI;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.JTextComponent;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.TabSet;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

public class TextEditor extends TextPane {
	public static final Color COLOR_SEL = new Color(230, 240, 250);
	public static final Color COLOR_TAB = Color.LIGHT_GRAY;
	public static final Color COLOR_RET = Color.LIGHT_GRAY;
	private static final Logger LOG = Logger.getGlobal();
	private static final long serialVersionUID = 1L;
	private transient TextEditorListener listener;
	private static boolean paintERT;
	final Rectangle caretRect;

	public TextEditor() {
		setHighlighter(new TextEditorHighlighter());
		setEditorKit(new TextEditorKit());
		addCaretListener(this::processar);
		caretRect = new Rectangle();
		configurar();
	}

	private void configurar() {
		inputMap().put(getKeyStrokeCtrl(KeyEvent.VK_F), "focus_input_pesquisar");
		getActionMap().put("focus_input_pesquisar", actionFocusPesquisar);
	}

	private transient javax.swing.Action actionFocusPesquisar = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (listener != null) {
				listener.focusInputPesquisar(TextEditor.this);
			}
		}
	};

	public static KeyStroke getKeyStrokeCtrl(int keyCode) {
		return KeyStroke.getKeyStroke(keyCode, InputEvent.CTRL_MASK);
	}

	private InputMap inputMap() {
		return getInputMap(WHEN_IN_FOCUSED_WINDOW);
	}

	public TextEditorListener getListener() {
		return listener;
	}

	public void setListener(TextEditorListener listener) {
		this.listener = listener;
	}

	public static boolean isPaintERT() {
		return paintERT;
	}

	public static void setPaintERT(boolean paintERT) {
		TextEditor.paintERT = paintERT;
	}

	private void processar(CaretEvent e) {
		TextUI textUI = getUI();
		try {
			Rectangle r = textUI.modelToView(this, e.getDot());
			if (r != null) {
				caretRect.width = getWidth();
				caretRect.height = r.height;
				caretRect.y = r.y;
				repaint();
			}
		} catch (BadLocationException ex) {
			LOG.warning(ex.getMessage());
		}
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (paintERT) {
			TextUI textUI = getUI();
			String text = getText();
			paintE(g, textUI, text);
			paintR(g, textUI, text);
			paintT(g, textUI, text);
		}
	}

	private void paintE(Graphics g, TextUI textUI, String text) {
		int pos = text.indexOf(' ');
		while (pos != -1) {
			try {
				Rectangle r = textUI.modelToView(this, pos);
				if (r != null) {
					g.setColor(COLOR_RET);
					g.fillOval(r.x + 1, r.y + r.height / 3, 3, 3);
				}
				pos = text.indexOf(' ', pos + 1);
			} catch (BadLocationException e) {
				break;
			}
		}
	}

	private void paintR(Graphics g, TextUI textUI, String text) {
		int pos = text.indexOf('\n');
		while (pos != -1) {
			try {
				Rectangle r = textUI.modelToView(this, pos);
				if (r != null) {
					r.height -= 3;
					g.setColor(COLOR_RET);
					g.fillArc(r.x, r.y - 1, 7, 7, 90, 180);
					g.fillRect(r.x + 4, r.y, 2, 1);
					desenharR(g, r, 0);
					desenharR(g, r, 2);
				}
				pos = text.indexOf('\n', pos + 1);
			} catch (BadLocationException e) {
				break;
			}
		}
	}

	private void desenharR(Graphics g, Rectangle r, int offset) {
		r.x += offset;
		g.drawLine(r.x + 4, r.y, r.x + 4, r.y + r.height);
	}

	private void paintT(Graphics g, TextUI textUI, String text) {
		int pos = text.indexOf('\t');
		while (pos != -1) {
			try {
				Rectangle r = textUI.modelToView(this, pos);
				if (r != null) {
					g.setColor(COLOR_TAB);
					desenharT(g, r, 0);
					desenharT(g, r, 2);
				}
				pos = text.indexOf('\t', pos + 1);
			} catch (BadLocationException e) {
				break;
			}
		}
	}

	private void desenharT(Graphics g, Rectangle r, int offset) {
		int umQuarto = r.height / 4;
		int umTerco = r.height / 3;
		int metade = r.height / 2;
		r.x += offset;
		g.drawLine(r.x + 1, r.y + umQuarto + 1, r.x + 3, r.y + metade - 1);
		g.drawLine(r.x + 3, r.y + metade, r.x + 1, r.y + r.height - umTerco - 1);
	}
}

class TextEditorKit extends StyledEditorKit {
	private static final ViewFactory factory = new InstrucaoStyledViewFactory();
	private static final long serialVersionUID = 1L;

	@Override
	public ViewFactory getViewFactory() {
		return factory;
	}

	static class InstrucaoStyledViewFactory implements ViewFactory {
		public View create(Element elem) {
			String kind = elem.getName();
			if (kind != null) {
				if (kind.equals(AbstractDocument.ContentElementName)) {
					return new LabelView(elem);
				} else if (kind.equals(AbstractDocument.ParagraphElementName)) {
					return new TextEditorParagraphView(elem);
				} else if (kind.equals(AbstractDocument.SectionElementName)) {
					return new BoxView(elem, View.Y_AXIS);
				} else if (kind.equals(StyleConstants.ComponentElementName)) {
					return new ComponentView(elem);
				} else if (kind.equals(StyleConstants.IconElementName)) {
					return new IconView(elem);
				}
			}
			return new LabelView(elem);
		}
	}

	static class TextEditorParagraphView extends ParagraphView {
		public static final int TAB_SIZE = 20;

		public TextEditorParagraphView(Element elem) {
			super(elem);
		}

		@Override
		public float nextTabStop(float x, int tabOffset) {
			TabSet tabs = getTabSet();
			if (tabs == null) {
				return (float) (getTabBase() + (((int) x / TAB_SIZE + 1) * TAB_SIZE));
			}
			return super.nextTabStop(x, tabOffset);
		}
	}
}

class TextEditorHighlighter extends DefaultHighlighter {
	private JTextComponent component;

	@Override
	public final void install(final JTextComponent c) {
		this.component = c;
		super.install(c);
	}

	@Override
	public final void deinstall(final JTextComponent c) {
		this.component = null;
		super.deinstall(c);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (component instanceof TextEditor) {
			TextEditor editor = (TextEditor) component;
			g.setColor(TextEditor.COLOR_SEL);
			g.fillRect(0, editor.caretRect.y, editor.caretRect.width, editor.caretRect.height);
		}
	}
}