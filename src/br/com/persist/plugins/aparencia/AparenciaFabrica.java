package br.com.persist.plugins.aparencia;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import br.com.persist.abstrato.AbstratoFabricaContainer;
import br.com.persist.abstrato.AbstratoServico;
import br.com.persist.abstrato.Servico;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.Util;

public class AparenciaFabrica extends AbstratoFabricaContainer {
	private ItemLAF padrao;

	@Override
	public List<Servico> getServicos(Formulario formulario) {
		return Arrays.asList(new AparenciaServico());
	}

	class AparenciaServico extends AbstratoServico {
		@Override
		public void visivelFormulario(Formulario formulario) {
			if (padrao != null) {
				padrao.doClick();
			}
		}
	}

	@Override
	public List<JMenuItem> criarMenuItens(Formulario formulario) {
		LookAndFeelInfo[] installedLookAndFeels = UIManager.getInstalledLookAndFeels();
		List<JMenuItem> lista = new ArrayList<>();
		ButtonGroup grupo = new ButtonGroup();

		for (LookAndFeelInfo info : installedLookAndFeels) {
			ItemLAF item = new ItemLAF(formulario, info);
			grupo.add(item);
			lista.add(item);
		}

		LookAndFeelInfo info = new LookAndFeelInfo("Nimbus" + Constantes.DOIS, NimbusLookAndFeel2.class.getName());
		padrao = new ItemLAF(formulario, info);
		grupo.add(padrao);
		lista.add(padrao);

		return lista;
	}
}

class ItemLAF extends JRadioButtonMenuItem {
	private static final long serialVersionUID = 1L;
	private final Formulario formulario;
	private final String classe;

	ItemLAF(Formulario formulario, LookAndFeelInfo info) {
		addActionListener(e -> processar());
		this.formulario = formulario;
		classe = info.getClassName();
		setText(info.getName());
	}

	void processar() {
		try {
			UIManager.setLookAndFeel(classe);
			SwingUtilities.updateComponentTreeUI(formulario);
		} catch (Exception ex) {
			Util.stackTraceAndMessage(getClass().getName(), ex, formulario);
		}
	}
}