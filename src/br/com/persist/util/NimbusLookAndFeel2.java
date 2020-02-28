package br.com.persist.util;

import javax.swing.UIDefaults;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

public class NimbusLookAndFeel2 extends NimbusLookAndFeel {
	private static final long serialVersionUID = 1L;

	@Override
	public String getName() {
		return super.getName() + "2";
	}

	@Override
	public String getID() {
		return super.getID() + "2";
	}

	@Override
	public String getDescription() {
		return super.getDescription() + "2";
	}

	@Override
	protected void initClassDefaults(UIDefaults table) {
		super.initClassDefaults(table);
		table.put("TabbedPaneUI", "br.com.persist.util.SynthTabbedPaneUI2");
	}

	@Override
	public UIDefaults getDefaults() {
		UIDefaults resp = super.getDefaults();
		resp.put("TabbedPane:TabbedPaneTab[Disabled+Selected].backgroundPainter", null);
		resp.put("TabbedPane:TabbedPaneTabArea[Disabled].backgroundPainter", null);
		resp.put("TabbedPane:TabbedPaneTab[Disabled].backgroundPainter", null);

		return resp;
	}
}