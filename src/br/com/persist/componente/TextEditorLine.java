package br.com.persist.componente;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Utilities;

public class TextEditorLine extends JPanel
		implements CaretListener, DocumentListener, PropertyChangeListener, FontListener {
	private static final long serialVersionUID = 8960786613762153802L;
	private TextEditor textEditor;
	private int ultimaLargura;
	private int tamanhoFonte;
	private int ultimaAltura;
	private int ultimaLinha;

	public TextEditorLine(TextEditor textEditor) {
		setFont(textEditor.getFont());
		this.textEditor = textEditor;
		setBackground(Color.WHITE);
		configLargura();
		configBorda();
		textEditor.getDocument().addDocumentListener(this);
		textEditor.addPropertyChangeListener("font", this);
		textEditor.addCaretListener(this);
		if (textEditor.getFontListener() == null) {
			textEditor.setFontListener(this);
		}
	}

	@Override
	public void alteradoPara(int tamanho) {
		tamanhoFonte = tamanho;
	}

	private void configLargura() {
		Element root = textEditor.getDocument().getDefaultRootElement();
		int total = root.getElementCount();
		int largura = Math.max(String.valueOf(total).length(), 3);

		if (ultimaLargura != largura) {
			ultimaLargura = largura;
			FontMetrics fontMetrics = getFontMetrics(getFont());
			int width = fontMetrics.charWidth('O') * largura;
			Insets insets = getInsets();
			int preferredWidth = insets.left + insets.right + width;

			Dimension d = getPreferredSize();
			d.setSize(preferredWidth, 1000000);
			setPreferredSize(d);
			setSize(d);
		}
	}

	public void configBorda() {
		Border outside = new MatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY);
		Border inside = new EmptyBorder(0, 5, 0, 5);
		setBorder(new CompoundBorder(outside, inside));
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		FontMetrics fontMetrics = textEditor.getFontMetrics(textEditor.getFont());
		Insets insets = getInsets();
		int larguraTotal = getSize().width - insets.left - insets.right;

		Rectangle clip = g.getClipBounds();
		int menor = textEditor.viewToModel(new Point(0, clip.y));
		int maior = textEditor.viewToModel(new Point(0, clip.y + clip.height));

		while (menor <= maior) {
			try {
				String numero = stringNumero(menor);
				int largura = fontMetrics.stringWidth(numero);
				int x = calgularX(larguraTotal, largura) + insets.left;
				int y = calcularY(menor, fontMetrics);
				if (ehLinhaAtual(menor)) {
					g.setColor(TextEditor.COLOR_SEL);
					Rectangle r = textEditor.modelToView(menor);
					g.fillRect(0, r.y, getWidth(), r.height);
				}
				g.setColor(Color.LIGHT_GRAY);
				g.drawString(numero, x, y);
				menor = Utilities.getRowEnd(textEditor, menor) + 1;
			} catch (Exception e) {
				break;
			}
		}

		if (tamanhoFonte != 0) {
			g.setColor(Color.BLUE);
			g.drawString("" + tamanhoFonte, 1, 10);
		}
	}

	private boolean ehLinhaAtual(int row) {
		int caretPos = textEditor.getCaretPosition();
		Element root = textEditor.getDocument().getDefaultRootElement();
		return root.getElementIndex(row) == root.getElementIndex(caretPos);
	}

	private String stringNumero(int row) {
		Element root = textEditor.getDocument().getDefaultRootElement();
		int index = root.getElementIndex(row);
		Element line = root.getElement(index);
		return line.getStartOffset() == row ? String.valueOf(index + 1) : "";
	}

	private int calgularX(int larguraTotal, int largura) {
		return (int) ((larguraTotal - largura) * 1.0F);
	}

	private int calcularY(int row, FontMetrics fontMetrics) throws BadLocationException {
		Rectangle r = textEditor.modelToView(row);
		int descent = fontMetrics.getDescent();
		int y = r.y + r.height;
		return y - descent;
	}

	@Override
	public void caretUpdate(CaretEvent e) {
		int caretPos = textEditor.getCaretPosition();
		Element root = textEditor.getDocument().getDefaultRootElement();
		int linha = root.getElementIndex(caretPos);
		if (ultimaLinha != linha) {
			ultimaLinha = linha;
			getParent().repaint();
		}
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		documentChanged();
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		documentChanged();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		documentChanged();
	}

	private void documentChanged() {
		SwingUtilities.invokeLater(() -> {
			try {
				int length = textEditor.getDocument().getLength();
				Rectangle r = textEditor.modelToView(length);
				if (r != null && ultimaAltura != r.y) {
					configLargura();
					getParent().repaint();
					ultimaAltura = r.y;
				}
			} catch (BadLocationException ex) {
				//
			}
		});
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getNewValue() instanceof Font) {
			Font newFont = (Font) e.getNewValue();
			ultimaLargura = 0;
			setFont(newFont);
			configLargura();
		}
	}
}