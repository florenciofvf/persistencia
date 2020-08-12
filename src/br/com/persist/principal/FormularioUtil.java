package br.com.persist.principal;

import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import br.com.persist.componente.Menu;
import br.com.persist.conexao.Conexao;
import br.com.persist.util.Constantes;
import br.com.persist.util.NimbusLookAndFeel2;
import br.com.persist.util.Preferencias;
import br.com.persist.util.Util;

public class FormularioUtil {

	private FormularioUtil() {
	}

	public static void menuAparencia(Formulario formulario, Menu menu) {
		LookAndFeelInfo[] installedLookAndFeels = UIManager.getInstalledLookAndFeels();
		ButtonGroup grupo = new ButtonGroup();

		for (LookAndFeelInfo info : installedLookAndFeels) {
			ItemLAF item = new ItemLAF(formulario, info);
			grupo.add(item);
			menu.add(item);
		}

		LookAndFeelInfo info = new LookAndFeelInfo("Nimbus" + Constantes.DOIS, NimbusLookAndFeel2.class.getName());
		ItemLAF item = new ItemLAF(formulario, info);
		grupo.add(item);
		menu.add(item);
	}

	public static void aparenciaPadrao(Menu menu, String titulo) {
		int total = menu.getItemCount();

		for (int i = 0; i < total; i++) {
			JMenuItem item = menu.getItem(i);

			if (titulo.equals(item.getText())) {
				item.doClick();
			}
		}
	}

	public static void fechar(Formulario formulario) {
		try {
			formulario.getFichario().getSalvarAberto().salvar();

			if (Preferencias.isFecharConexao()) {
				Conexao.fecharConexoes();
			}
		} catch (Exception ex) {
			Util.stackTraceAndMessage(formulario.getClass().getName() + ".fechar()", ex, formulario);
		}
	}
}