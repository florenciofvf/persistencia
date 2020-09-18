package br.com.persist.plugins.aparencia;

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

		return resp;
	}
}