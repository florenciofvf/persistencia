package br.com.persist.componente;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.logging.Logger;

import javax.swing.event.CaretEvent;
import javax.swing.plaf.TextUI;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.TabSet;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

public class TextEditor extends TextPane {
	public static final Color COLOR_SEL = new Color(155, 100, 255);
	public static final Color COLOR_TAB = new Color(185, 185, 185);
	public static final Color COLOR_RET = new Color(175, 175, 175);
	private static final Logger LOG = Logger.getGlobal();
	private static final long serialVersionUID = 1L;
	private final Rectangle rectangle;
	private static boolean paintERT;

	public TextEditor() {
		setEditorKit(new TextEditorKit());
		addCaretListener(this::processar);
		rectangle = new Rectangle();
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
				rectangle.width = getWidth();
				rectangle.height = r.height;
				rectangle.y = r.y;
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
		g.setColor(COLOR_SEL);
		g.drawRect(0, rectangle.y, rectangle.width, rectangle.height);
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
					g.fillArc(r.x, r.y, 7, 7, 90, 180);
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
					return new InstrucaoParagraphView(elem);
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

	static class InstrucaoParagraphView extends ParagraphView {
		public static final int TAB_SIZE = 20;

		public InstrucaoParagraphView(Element elem) {
			super(elem);
		}

		@Override
		public float nextTabStop(float x, int tabOffset) {
			TabSet tabs = getTabSet();
			if (tabs == null) {
				return getTabBase() + ((x / TAB_SIZE + 1) * TAB_SIZE);
			}
			return super.nextTabStop(x, tabOffset);
		}
	}
}