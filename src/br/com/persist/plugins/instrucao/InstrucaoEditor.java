package br.com.persist.plugins.instrucao;

import javax.swing.text.AbstractDocument;
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

public class InstrucaoEditor extends StyledEditorKit {
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