package br.com.persist.plugins.aparencia;

import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.UIDefaults;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import br.com.persist.assistencia.Constantes;

public class NimbusLookAndFeel2 extends NimbusLookAndFeel {
	private static final long serialVersionUID = 1L;

	@Override
	public String getID() {
		return super.getID() + Constantes.DOIS;
	}

	@Override
	public String getName() {
		return super.getName() + Constantes.DOIS;
	}

	@Override
	public String getDescription() {
		return super.getDescription() + Constantes.DOIS;
	}

	@Override
	protected void initClassDefaults(UIDefaults table) {
		super.initClassDefaults(table);
		table.put("TabbedPaneUI", SynthTabbedPaneUI2.class.getName());
	}

	@Override
	public UIDefaults getDefaults() {
		UIDefaults resp = super.getDefaults();
		resp.put("TabbedPane:TabbedPaneTab[Disabled+Selected].backgroundPainter", null);
		resp.put("TabbedPane:TabbedPaneTabArea[Disabled].backgroundPainter", null);
		resp.put("TabbedPane:TabbedPaneTab[Disabled].backgroundPainter", null);
		resp.put("InternalFrame[Enabled].backgroundPainter",
				new MyLazyPainter("br.com.persist.plugins.aparencia.MyInternalFramePainter",
						MyInternalFramePainter.BACKGROUND_ENABLED, new Insets(25, 6, 6, 6), new Dimension(25, 36),
						false, MyAbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE,
						Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
		resp.put("InternalFrame[Enabled+WindowFocused].backgroundPainter",
				new MyLazyPainter("br.com.persist.plugins.aparencia.MyInternalFramePainter",
						MyInternalFramePainter.BACKGROUND_ENABLED_WINDOWFOCUSED, new Insets(25, 6, 6, 6),
						new Dimension(25, 36), false, MyAbstractRegionPainter.PaintContext.CacheMode.NINE_SQUARE_SCALE,
						Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
		return resp;
	}
}